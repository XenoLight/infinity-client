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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Paris @ RSBot development for the powerbot.org client - version 1.0
 * @author Runedev development team - version 1.1
 */
public class IniParser {
	private static final char SECTIONOPEN = '[';
	private static final char SECTIONCLOSE = ']';
	private static final char KEYBOUND = '=';
	private static final char[] COMMENTS = {'#', ';'};
	public static final String EMPTYSECTION = "";

	private IniParser() {
	}

	public static void serialise(final Map<String, Map<String, String>> data, final File out) throws IOException {
		final BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		serialise(data, bw);
		bw.close();
	}

	public static void serialise(final Map<String, Map<String, String>> data, final BufferedWriter out) throws IOException {
		if (data.containsKey(EMPTYSECTION)) {
			writeSection(EMPTYSECTION, data.get(EMPTYSECTION), out);
			out.newLine();
		}
		for (final Entry<String, Map<String, String>> entry : data.entrySet()) {
			final String section = entry.getKey();
			if (section.equals(EMPTYSECTION)) {
				continue;
			}
			writeSection(section, entry.getValue(), out);
			out.newLine();
		}
	}

	private static void writeSection(final String section, final Map<String, String> map, final BufferedWriter out) throws IOException {
		if (!(section == null || section.isEmpty())) {
			out.write(SECTIONOPEN);
			out.write(section);
			out.write(SECTIONCLOSE);
			out.newLine();
		}
		for (final Entry<String, String> entry : map.entrySet()) {
			out.write(entry.getKey());
			out.write(KEYBOUND);
			out.write(entry.getValue());
			out.newLine();
		}
	}

	public static Map<String, Map<String, String>> deserialise(final File input) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(input));
		final Map<String, Map<String, String>> data = deserialise(reader);
		reader.close();
		return data;
	}

	public static Map<String, Map<String, String>> deserialise(final InputStream in) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		final Map<String, Map<String, String>> data = deserialise(reader);
		reader.close();
		return data;
	}

	public static Map<String, Map<String, String>> deserialise(final BufferedReader input) throws IOException {
		final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
		String line, section = EMPTYSECTION;

		while ((line = input.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			int z;
			final int l = line.length();
			final char t = line.charAt(0);
			if (t == SECTIONOPEN) {
				z = line.indexOf(SECTIONCLOSE, 1);
				z = z == -1 ? l : z;
				section = z == 1 ? "" : line.substring(1, z).trim();
			} else {
				boolean skip = false;
				for (final char c : COMMENTS) {
					if (t == c) {
						skip = true;
						break;
					}
				}
				if (skip) {
					continue;
				}
				z = line.indexOf(KEYBOUND);
				z = z == -1 ? l : z;
				String key, value = "";
				key = line.substring(0, z).trim();
				if (++z < l) {
					value = line.substring(z).trim();
				}
				if (!data.containsKey(section)) {
					data.put(section, new HashMap<String, String>());
				}
				data.get(section).put(key, value);
			}
		}

		return data;
	}

	public static boolean parseBool(final String mode) {
		return mode.equals("1") || mode.equalsIgnoreCase("true") || mode.equalsIgnoreCase("yes");
	}
}
