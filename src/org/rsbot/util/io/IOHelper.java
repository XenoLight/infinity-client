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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.rsbot.util.StringUtil;

/**
 * @author Paris @ RSBot development for the powerbot.org client - version 1.0
 * @author Runedev development team - version 1.1
 */
public class IOHelper {

	public static long crc32(final byte[] data) throws IOException {
		return crc32(new ByteArrayInputStream(data));
	}

	public static long crc32(final File path) throws IOException {
		return crc32(new FileInputStream(path));
	}

	public static long crc32(final InputStream in) throws IOException {
		final CheckedInputStream cis = new CheckedInputStream(in, new CRC32());
		final byte[] buf = new byte[128];
		while (cis.read(buf) > -1) {
		}
		return cis.getChecksum().getValue();
	}

	public static byte[] read(final File in) {
		try {
			return read(new FileInputStream(in));
		} catch (final FileNotFoundException ignored) {
			return null;
		}
	}

	public static byte[] read(final InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			final byte[] temp = new byte[4096];
			int read;
			while ((read = is.read(temp)) != -1) {
				buffer.write(temp, 0, read);
			}
		} catch (final IOException ignored) {
			try {
				buffer.close();
			} catch (final IOException ignored2) {
			}
			buffer = null;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (final IOException ignored) {
			}
		}
		return buffer == null ? null : buffer.toByteArray();
	}

	public static byte[] read(final URL in) {
		try {
			return read(in.openStream());
		} catch (final IOException ignored) {
			return null;
		}
	}

	public static String readString(final File in) {
		return StringUtil.newStringUtf8(read(in));
	}

	public static String readString(final URL in) {
		return StringUtil.newStringUtf8(read(in));
	}

	public static void recursiveDelete(final File path,
			final boolean deleteParent) {
		if (!path.exists()) {
			return;
		}
		for (final File file : path.listFiles()) {
			if (file.isDirectory()) {
				recursiveDelete(file, true);
			} else {
				file.delete();
			}
		}
		if (deleteParent) {
			path.delete();
		}
	}

	public static void saveto(final InputStream in, final String outpath) {
		try {
			final OutputStream out = new FileOutputStream(new File(outpath));

			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (final Exception e) {
		}
	}

	public static void write(final InputStream in, final File out) {
		try {
			write(in, new FileOutputStream(out));
		} catch (final FileNotFoundException ignored) {
		}
	}

	public static void write(final InputStream in, final OutputStream out) {
		try {
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (final IOException ignored) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException ignored) {
			}
		}
	}

	public static void write(final String s, final File out) {
		final ByteArrayInputStream in = new ByteArrayInputStream(
				StringUtil.getBytesUtf8(s));
		write(in, out);
	}
}
