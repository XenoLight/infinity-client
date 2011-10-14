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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;

import org.rsbot.util.GlobalConfiguration;

/**
 * @author RSBot development for the powerbot.org client - version 1.0
 * @author Runedev development team - version 1.1
 */
public class UIDData {

	private static final String newline = System.getProperty("line.separator");
	private static final String separator = "#";

	private final HashMap<String, byte[]> uids = new HashMap<String, byte[]>();
	private String lastUsed = "";

	public UIDData() {
		final File fUIDs = new File(GlobalConfiguration.Paths.getUIDCache());
		if (!fUIDs.exists()) {
			return;
		}

		try {
			final BufferedReader in = new BufferedReader(new FileReader(fUIDs));
			String line;// Used to store the lines from the file
			while ((line = in.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}

				final String[] data = line.split(separator, 2);
				uids.put(data[0], data[1].getBytes());
			}
		} catch (final Exception ignored) {
		}
	}

	public String getLastUsed() {
		return lastUsed;
	}

	public byte[] getUID(String name) {
		if (name.equals("")) {
			name = "DEFAULT";
		}

		lastUsed = name;

		final byte[] data = uids.get(name);
		if (data == null) {
			return new byte[0];
		}

		return data;
	}

	public void save() {
		try {
			final File fUIDs = new File(GlobalConfiguration.Paths.getUIDCache());
			if (fUIDs.exists() || fUIDs.createNewFile()) {
				final FileOutputStream out = new FileOutputStream(fUIDs);
				for (final String key : uids.keySet()) {
					out.write(key.getBytes());
					out.write(separator.getBytes());
					out.write(uids.get(key));

					out.write(newline.getBytes());
				}
				out.close();
			}
		} catch (final Exception ignored) {
		}
	}

	public void setUID(String name, final byte[] uid) {
		if (name.equals("")) {
			name = "DEFAULT";
		}

		uids.put(name, uid);
	}
}