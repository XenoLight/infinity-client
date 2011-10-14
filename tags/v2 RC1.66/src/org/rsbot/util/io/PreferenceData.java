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
import java.io.IOException;
import java.io.RandomAccessFile;

import org.rsbot.util.GlobalConfiguration;

/**
 * @author RSBot development for the powerbot.org client - version 1.1
 * @author Runedev development team - version 1.1
 */
public class PreferenceData {
	private final int type;
	private final File file;

	public PreferenceData(final int type) {
		this.type = type;
		file = new File(GlobalConfiguration.Paths.getSettingsDirectory()
				+ File.separator + "pref" + type + ".dat");
		try {
			if (!file.exists()) {
				file.createNewFile();
				switch (type) {
				case 1:
					set(new byte[] { 24, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							1, 2, 0, 0, 0, 3, 1, 0, 1, 0, 0, 4, 8, 0, 0, 0,
							127, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 4, 0, 1, 2, 0 });
					break;

				case 2:
					set(new byte[] { 1, 0, 19, 3, -38, 0, 0, 0, 1, 3, -37, 0,
							0, 0, 1, 3, -30, 0, 0, 0, 2, 3, -28, -1, -1, -1,
							-1, 3, -27, -1, -1, -1, -1, 3, -26, -1, -1, -1, -1,
							3, -25, -1, -1, -1, -1, 4, 11, 0, 0, 0, 79, 4, 12,
							0, 0, 0, 74, 4, 13, -1, -1, -1, -1, 4, 28, -1, -1,
							-1, -1, 4, -40, 0, 0, 0, 3, 4, -6, 0, 0, 0, 67, 4,
							-3, 0, 0, 0, 0, 5, 35, -1, -1, -1, -1, 5, 36, -1,
							-1, -1, -1, 5, -122, 0, 0, 0, 1, 5, -115, -1, -1,
							-1, -1, 5, -107, -1, -1, -1, -1 });
					break;
				}
			}
		} catch (final IOException ignored) {
		}
	}

	private byte[] checkPrefs(final byte[] data) {
		switch (type) {

		case 1: {
			if (data.length <= 40) {
				break;
			}

			data[19] = 3; // Graphics Mode
		}

		}

		return data;
	}

	public byte[] get() {
		try {
			final RandomAccessFile raf = new RandomAccessFile(file, "rw");
			final byte[] b = new byte[(int) raf.length()];
			raf.readFully(b);

			return checkPrefs(b);
		} catch (final IOException ioe) {
			return new byte[0];
		}
	}

	public void set(byte[] data) {
		data = checkPrefs(data);

		try {
			final RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.write(data);
		} catch (final IOException ignored) {
		}
	}
}
