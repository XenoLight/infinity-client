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
import java.awt.image.BufferedImage;

/**
 * hue, saturation, and value = HSV
 * 
 * @author Sorcermus - version 1.0
 * @author Runedev development team - version 1.1
 */
public class ColorCompute {

	public static String bitmapToString(final BufferedImage map) {
		String imageString = "";
		for (int x = 0; x < map.getWidth(); ++x) {
			for (int y = 0; y < map.getHeight(); ++y) {
				imageString = imageString + x + "," + y + ","
				+ map.getRGB(x, y) + ":";
			}
		}
		return imageString;
	}

	public static boolean checkHSV(final int[] hsv, final int[] newHSV) {
		final int[] diffs = { hsv[0] - newHSV[0], hsv[1] - newHSV[1],
				hsv[2] - newHSV[2] };

		return ((diffs[2] >= -400) && (diffs[2] <= 400) && (diffs[0] >= -10)
				&& (diffs[0] <= 10) && (diffs[1] >= -10) && (diffs[1] <= 10));
	}

	public static int[] colorToHSV(final Color color) {
		final int[] hsv = new int[3];

		final int r = color.getRed();
		final int b = color.getBlue();
		final int g = color.getGreen();
		int min = 0;
		int max1;
		if (r > g) {
			max1 = r;
		} else {
			min = r;
			max1 = g;
		}
		if (b > max1) {
			max1 = b;
		}
		if (b < min) {
			min = b;
		}

		final int delMax = max1 - min;

		float H = 0.0F;
		final float V = max1;
		float S1;
		if (delMax == 0) {
			H = 0.0F;
			S1 = 0.0F;
		} else {
			S1 = delMax / 255.0F;
			if (r == max1) {
				H = (g - b) / delMax * 60.0F;
			} else if (g == max1) {
				H = (2.0F + (b - r) / delMax) * 60.0F;
			} else if (b == max1) {
				H = (4.0F + (r - g) / delMax) * 60.0F;
			}
		}
		hsv[0] = (int) H;
		hsv[1] = (int) (S1 * 100.0F);
		hsv[2] = (int) (V * 100.0F);
		return hsv;
	}

	public static boolean computeTolerance(final Color color1, final Color color2, final int tol) {
		return ((color1.getRed() - color2.getRed() <= tol)
				&& (color1.getRed() - color2.getRed() >= -tol)
				&& (color1.getGreen() - color2.getGreen() <= tol)
				&& (color1.getGreen() - color2.getGreen() >= -tol)
				&& (color1.getBlue() - color2.getBlue() <= tol) && (color1
						.getBlue() - color2.getBlue() >= -tol));
	}

	public static int distance(final Point p1, final Point p2) {
		return ((p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y));
	}

	public static int random(final int low, final int high) {
		return (int) ((high - low) * Math.random() + low);
	}
}
