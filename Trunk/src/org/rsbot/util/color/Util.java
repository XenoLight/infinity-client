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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.rsbot.bot.Bot;

/**
 * This is the main color utility file that handles color such as red, green,
 * and blue.
 * 
 * @author Runedev develpment team - version 1.0
 */
public class Util {

	public static Point[] findColorInArea(final Rectangle q, final Color desired,
			final Color tolerance) {
		final ArrayList<Point> al = new ArrayList<Point>();
		final int rt = tolerance.getRed(), gt = tolerance.getGreen(), bt = tolerance
		.getBlue();
		for (int i = q.x; i < q.x + q.width; i++) {
			for (int j = q.y; j < q.y + q.height; j++) {
				final Color c = grabColorAt(i, j);
				if (c == null) {
					continue;
				}
				final int r = Math.abs(c.getRed() - desired.getRed());
				final int g = Math.abs(c.getGreen() - desired.getGreen());
				final int b = Math.abs(c.getBlue() - desired.getBlue());
				if (r <= rt && b <= bt && g <= gt) {
					al.add(new Point(i, j));
				}
			}
		}
		al.trimToSize();
		if (al.isEmpty()) {
			return null;
		}
		return al.toArray(new Point[al.size()]);
	}

	public static Color grabColorAt(final int x, final int y) {
		try {
			return new Color(Bot.getBotBuffer().getRGB(x, y));
		} catch (final Exception ignored) {
			return null;
		}
	}
}
