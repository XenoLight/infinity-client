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
package org.lazygamerz.scripting.api.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.util.color.ColorCompute;
import org.rsbot.util.color.RGBBitmap;

/**
 * @author Sorcermus - version 1.0
 * @author Runedev development team - version 1.1
 */
public class ColorMethods {

	private final ColorRecognition cr;
	public ColorMethods(final Methods methods) {
		this.cr = new ColorRecognition();
	}

	public Rectangle find(final RGBBitmap bitmap, final Rectangle searchArea) {
		Color color = new Color(bitmap.getBitmap().getRGB(0, 0));
		for (int x = searchArea.x; x < searchArea.x + searchArea.width; ++x) {
			for (int y = searchArea.y; y < searchArea.y + searchArea.height; ++y) {
				Color color2 = new Color(Bot.getImage().getRGB(x, y));
				int count = 0;
				if (ColorCompute.computeTolerance(color, color2, 5)) {
					++count;
					for (int x1 = 0; x1 < bitmap.getBitmap().getWidth(); ++x1) {
						for (int y1 = 0; y1 < bitmap.getBitmap().getHeight(); ++y1) {
							color2 = new Color(Bot.getImage().getRGB(x + x1,
									y + y1));
							color = new Color(bitmap.getBitmap().getRGB(x1, y1));
							if (ColorCompute.computeTolerance(color, color2, 5)) {
								++count;
							}
							if (count >= bitmap.getBitmap().getWidth()
									* bitmap.getBitmap().getHeight()) {
								return new Rectangle(x, y, x + x1, y + y1);
							}
						}
					}
				}
			}
		}
		return null;
	}

	public boolean findToptextMatch(final int whiteCount) {
		int whitecount = 0;
		final BufferedImage cap = getCR().applyFilter(ColorRecognition.COMMON_COLORS,
				new Dimension(150, 20));
		for (int x = 0; x < cap.getWidth(); ++x) {
			for (int y = 0; y < cap.getHeight(); ++y) {
				if (cap.getRGB(x, y) != -1) {
					continue;
				}
				++whitecount;
			}
		}
		System.out.println(whiteCount + "," + whitecount);
		return (whitecount == whiteCount);
	}

	public Color getColorAt(final int x, final int y) {
		return new Color(Bot.getImage().getRGB(x, y));
	}

	public ColorRecognition getCR() {
		return cr;
	}

	public int getWhites() {
		int whitecount = 0;
		final BufferedImage cap = getCR().applyFilter(ColorRecognition.COMMON_COLORS,
				new Dimension(150, 20));
		for (int x = 0; x < cap.getWidth(); ++x) {
			for (int y = 0; y < cap.getHeight(); ++y) {
				if (cap.getRGB(x, y) != -1) {
					continue;
				}
				++whitecount;
			}
		}
		System.out.println(whitecount);
		return whitecount;
	}
}
