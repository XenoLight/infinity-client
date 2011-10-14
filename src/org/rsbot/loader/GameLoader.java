package org.rsbot.loader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.rsbot.loader.asm.ClassReader;
import org.rsbot.loader.script.ModScript;
import org.rsbot.loader.script.ParseException;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;
import org.rsbot.util.io.IniParser;

public class GameLoader {

	private final static GameLoader instance = new GameLoader();
	private final Logger log = Logger.getLogger(GameLoader.class.getName());
	private final File manifest, cache;
	private final static String TARGET = "runescape";
	public final static int PORT_CLIENT = 43594;
	private int[] version = {-1, -1, -1};
	private Map<String, byte[]> classes;

	private GameLoader() {
		manifest = new File(GlobalConfiguration.Paths.getCacheDirectory(), "client.ini");
		cache = new File(GlobalConfiguration.Paths.getCacheDirectory(), "client.jar");
	}

	public static GameLoader getInstance() {
		return instance;
	}

	private boolean isCacheClean() {
		if (!(manifest.exists() && cache.exists())) {
			return false;
		}
		final Map<String, String> info;
		try {
			info = IniParser.deserialise(manifest).get(IniParser.EMPTYSECTION);
		} catch (final IOException ignored) {
			ignored.printStackTrace();
			return false;
		}
		if (info != null && info.containsKey("v1") && info.containsKey("t")) {
			try {
				version[1] = Integer.parseInt(info.get("v1"));
			} catch (final NumberFormatException ignored) {
				return false;
			}
			try {
				version[2] = getRemoteVersion(version[1]);
			} catch (final IOException ignored) {
				return false;
			}
			boolean notModified = false;
			try {
				notModified = HttpClient.isModifiedSince(new URL(GlobalConfiguration.Paths.URLs.UPDATE), Long.parseLong(info.get("t")));
			} catch (final IOException ignored) {
			} catch (final NumberFormatException ignored) {
			}
			return version[1] == version[2] && notModified;
		} else {
			return false;
		}
	}

	public void load() throws IOException, ParseException {
		classes = new HashMap<String, byte[]>();

		if (isCacheClean()) {
			log.info("Reading game client from cache");
			final JarFile jar = new JarFile(cache);
			final Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				final JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6).replace('/', '.');
					classes.put(name, IOHelper.read(jar.getInputStream(entry)));
				}
			}
			jar.close();
		} else {
			log.info("Downloading new game client");
			final ModScript script = new ModScript(HttpClient.downloadBinary(new URL(GlobalConfiguration.Paths.URLs.UPDATE)));
			version[0] = script.getVersion();
			if (version[2] > version[0]) {
				throw new ParseException("Patch outdated (" + version[2] + " > " + version[0] + ")");
			}
			final JarFile loader = getJar(TARGET, true), client = getJar(TARGET, false);
			final List<String> replace = Arrays.asList(script.getAttribute("replace").split(" "));

			for (final JarFile jar : new JarFile[]{loader, client}) {
				final Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();
					String name = entry.getName();
					if (name.endsWith(".class")) {
						name = name.substring(0, name.length() - 6).replace('/', '.');
						if (jar == client || replace.contains(name)) {
							classes.put(name, IOHelper.read(jar.getInputStream(entry)));
						}
					}
				}
				jar.close();
			}

			for (final Map.Entry<String, byte[]> entry : classes.entrySet()) {
				entry.setValue(script.process(entry.getKey(), entry.getValue()));
			}

			final ClassReader reader = new ClassReader(new ByteArrayInputStream(classes.get("client")));
			final VersionVisitor vv = new VersionVisitor();
			reader.accept(vv, ClassReader.SKIP_FRAMES);
			version[1] = vv.getVersion();

			final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(cache));
			zip.setMethod(ZipOutputStream.STORED);
			zip.setLevel(0);
			for (final Entry<String, byte[]> item : classes.entrySet()) {
				final ZipEntry entry = new ZipEntry(item.getKey() + ".class");
				entry.setMethod(ZipEntry.STORED);
				final byte[] data = item.getValue();
				entry.setSize(data.length);
				entry.setCompressedSize(data.length);
				entry.setCrc(IOHelper.crc32(data));
				zip.putNextEntry(entry);
				zip.write(item.getValue());
				zip.closeEntry();
			}
			zip.close();

			final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>(1);
			final Map<String, String> info = new HashMap<String, String>();
			info.put("v1", Integer.toString(version[1]));
			info.put("t", Long.toString(System.currentTimeMillis() / 1000L));
			data.put(IniParser.EMPTYSECTION, info);
			IniParser.serialise(data, manifest);
		}
	}

	public Map<String, byte[]> getClasses() {
		final Map<String, byte[]> copy = new HashMap<String, byte[]>(classes.size());
		for (final Entry<String, byte[]> item : classes.entrySet()) {
			copy.put(item.getKey(), item.getValue().clone());
		}
		return copy;
	}

	public String getTargetName() {
		return TARGET;
	}

	public boolean isOutdated() {
		return version[2] > version[1];
	}

	public int getRemoteVersion(final int start) throws IOException {
		for (int i = start; i < start + 50; i++) {
			final Socket sock = new Socket("world53." + getTargetName() + ".com", PORT_CLIENT);
			final byte[] payload = new byte[]{15, 0, 0, (byte) (i >> 8), (byte) i};
			sock.getOutputStream().write(payload, 0, payload.length);
			if (sock.getInputStream().read() == 0) {
				sock.close();
				return i;
			} else {
				sock.close();
			}
		}
		return -1;
	}

	private JarFile getJar(final String target, final boolean loader) {
		while (true) {
			final int world = 1 + new Random().nextInt(169);
			try {
				final StringBuilder sb = new StringBuilder(50);
				sb.append("jar:http://world").append(world).append(".").append(target).append(".com/");
				sb.append(loader ? "loader" : target).append(".jar!/");
				final JarURLConnection juc = (JarURLConnection) new URL(sb.toString()).openConnection();
				juc.setConnectTimeout(5000);
				return juc.getJarFile();
			} catch (final Exception ignored) {
			}
		}
	}
}
