/*
 * 2011 Runedev development team
 * http://lazygamerz.org
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 3 of the Licence, or (at your opinion) any
 * later version.
 *
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 */
package org.rsbot.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.tools.ToolProvider;

import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.GlobalConfiguration.OperatingSystem;

/**
 * @author RSBot development for the powerbot.org client - version 1.0
 * @author Runedev development team - version 1.1
 */
public class JavaCompiler {
	private final static String JAVACARGS = "-g:none";

	private static int compileNative(final javax.tools.JavaCompiler javac,
			final InputStream source, final String classPath)
	throws FileNotFoundException {
		final FileOutputStream[] out = new FileOutputStream[2];
		for (int i = 0; i < 2; i++) {
			out[i] = new FileOutputStream(new File(
					GlobalConfiguration.Paths.getCollectDirectory(), "compile."
					+ Integer.toString(i) + ".txt"));
		}
		return javac.run(source, out[0], out[1], JAVACARGS, "-cp", classPath);
	}

	private static void compileSystem(final File source, final String classPath)
	throws IOException {
		final String javac = findJavac();
		if (javac == null) {
			throw new IOException();
		}
		Runtime.getRuntime().exec(
				new String[] { javac, JAVACARGS, "-cp", classPath,
						source.getAbsolutePath() });
	}

	public static boolean compileWeb(final String source, final File out) {
		try {
			HttpClient.download(
					new URL(
							source
							+ "?v="
							+ Integer.toString(GlobalConfiguration
									.getVersion()) + "&s="
									+ URLEncoder.encode(source, "UTF-8")), out);
		} catch (final Exception ignored) {
			return false;
		}
		if (out.length() == 0) {
			out.delete();
		}
		return out.exists();
	}

	private static String findJavac() {
		try {
			if (GlobalConfiguration.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
				String currentVersion = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\" /v CurrentVersion");
				currentVersion = currentVersion.substring(
						currentVersion.indexOf("REG_SZ") + 6).trim();
				String binPath = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\\"
						+ currentVersion + "\" /v JavaHome");
				binPath = binPath.substring(binPath.indexOf("REG_SZ") + 6)
				.trim() + "\\bin\\javac.exe";
				return new File(binPath).exists() ? binPath : null;
			} else {
				final String whichQuery = readProcess("which javac");
				return whichQuery == null || whichQuery.length() == 0 ? null
						: whichQuery.trim();
			}
		} catch (final Exception ignored) {
			return null;
		}
	}

	public static boolean isAvailable() {
		return !(ToolProvider.getSystemJavaCompiler() == null && findJavac() == null);
	}

	private static String readProcess(final String exec) throws IOException {
		final Process compiler = Runtime.getRuntime().exec(exec);
		final InputStream is = compiler.getInputStream();
		try {
			compiler.waitFor();
		} catch (final InterruptedException ignored) {
			return null;
		}
		final StringBuilder result = new StringBuilder(256);
		int r;
		while ((r = is.read()) != -1) {
			result.append((char) r);
		}
		return result.toString();
	}

	public static boolean run(final File source, final String classPath) {
		final javax.tools.JavaCompiler javac = ToolProvider
		.getSystemJavaCompiler();
		try {
			if (javac != null) {
				return compileNative(javac, new FileInputStream(source),
						classPath) == 0;
			} else {
				compileSystem(source, classPath);
				return true;
			}
		} catch (final IOException ignored) {
		}
		return false;
	}
}
