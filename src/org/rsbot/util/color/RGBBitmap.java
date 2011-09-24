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
package org.rsbot.util.color;

import java.awt.image.BufferedImage;

/**
 * @author Sorcermus - version 1.0
 * @author Runedev development team - version 1.1
 */
public class RGBBitmap {

	public static RGBBitmap fromString(final String bitMapData) {
		final String[] dataEntries = bitMapData.split(":");
		final BufferedImage bitmap = new BufferedImage(dataEntries.length / 2,
				dataEntries.length / 2, 1);
		for (final String dataEntry : dataEntries) {
			final String[] data = dataEntry.split(",");
			bitmap.setRGB(Integer.parseInt(data[0]), Integer.parseInt(data[1]),
					Integer.parseInt(data[2]));
		}
		return new RGBBitmap(bitmap);
	}

	private final BufferedImage image;

	public RGBBitmap(final BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getBitmap() {
		return this.image;
	}
}
