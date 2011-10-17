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
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.rsbot.bot.Bot;
import org.rsbot.util.color.ColorCompute;

/**
 * @author Sorcermus - version 1.0
 * @author Runedev development team - version 1.1
 */
public class ColorRecognition {

	public static Color[] COMMON_COLORS = { new Color(255, 255, 255),
		new Color(218, 218, 218), new Color(3, 232, 232) };
	private final BufferedImage[] CHARSET = new BufferedImage[123];

	public ColorRecognition() {
	}

	public BufferedImage applyFilter(final Color[] filter, final Dimension dim) {
		final BufferedImage image = new BufferedImage(dim.width, dim.height, 1);
		image.getGraphics().drawImage(
				Bot.getImage().getSubimage(0, 0, dim.width, dim.height), 0, 0,
				null);
		for (int x = 0; x < dim.width; ++x) {
			for (int y = 0; y < dim.height; ++y) {
				final int rgb = image.getRGB(x, y);
				for (final Color color : filter) {
					if (!(ColorCompute.computeTolerance(new Color(rgb), color,
							40))) {
						image.setRGB(x, y, -65536);
					} else {
						image.setRGB(x, y, -1);
					}
				}
			}
		}
		try {
			ImageIO.write(image, "png", new File("imageCR.png"));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public Rectangle find(final BufferedImage image, final BufferedImage bitmap,
			final Rectangle searchArea) {
		Color color = new Color(bitmap.getRGB(0, 0));
		for (int x = searchArea.x; x < searchArea.x + searchArea.width; ++x) {
			for (int y = searchArea.y; y < searchArea.y + searchArea.height; ++y) {
				Color color2 = new Color(image.getRGB(x, y));
				int count = 0;
				if (ColorCompute.computeTolerance(color, color2, 5)) {
					++count;
					for (int x1 = 0; x1 < bitmap.getWidth(); ++x1) {
						for (int y1 = 0; y1 < bitmap.getHeight(); ++y1) {
							if ((x1 + x >= image.getWidth())
									|| (y1 + y >= image.getHeight())) {
								return null;
							}
							color2 = new Color(image.getRGB(x + x1, y + y1));
							color = new Color(bitmap.getRGB(x1, y1));
							if (ColorCompute.computeTolerance(color, color2, 5)) {
								++count;
							}
							if (count >= bitmap.getWidth() * bitmap.getHeight()) {
								return new Rectangle(x, y, x + x1, y + y1);
							}
						}
					}
				}
			}
		}
		return null;
	}

	public String getToptext() {
		String toptext = "";
		for (int i = 0; i < CHARSET.length; ++i) {
			if ((CHARSET[i] == null)
					|| (find(
							applyFilter(COMMON_COLORS, new Dimension(100, 30)),
							CHARSET[i], new Rectangle(0, 0, 100, 30)) == null)) {
				continue;
			}
			toptext = toptext + (char) i;
		}

		return toptext;
	}

	public void loadCharacters() {
		for (int i = 0; i < CHARSET.length; ++i) {
			final File file = new File("./bitmaps/" + i + ".bmp");
			if (!(file.exists())) {
				continue;
			}
			try {
				CHARSET[i] = ImageIO.read(file);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
