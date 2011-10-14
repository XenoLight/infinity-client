/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Color;

/**
 * @author Michael Hagen
 */
public class ColorHelper {

	public static Color brighter(final Color c, final double p) {
		if (c == null) {
			return null;
		}

		double r = c.getRed();
		double g = c.getGreen();
		double b = c.getBlue();

		final double rd = 255.0 - r;
		final double gd = 255.0 - g;
		final double bd = 255.0 - b;

		r += (rd * p) / 100.0;
		g += (gd * p) / 100.0;
		b += (bd * p) / 100.0;
		return createColor((int) r, (int) g, (int) b);
	}

	public static final Color createColor(final int r, final int g, final int b) {
		return new Color(((r & 0xFF) << 16) | ((g & 0xFF) << 8)
				| ((b & 0xFF) << 0));
	}

	public static Color[] createColorArr(final Color c1, final Color c2, final int steps) {
		if (c1 == null || c2 == null) {
			return null;
		}

		final Color colors[] = new Color[steps];
		double r = c1.getRed();
		double g = c1.getGreen();
		double b = c1.getBlue();
		final double dr = (c2.getRed() - r) / steps;
		final double dg = (c2.getGreen() - g) / steps;
		final double db = (c2.getBlue() - b) / steps;
		colors[0] = c1;
		for (int i = 1; i < steps - 1; i++) {
			r += dr;
			g += dg;
			b += db;
			colors[i] = createColor((int) r, (int) g, (int) b);
		}
		colors[steps - 1] = c2;
		return colors;
	}

	public static Color darker(final Color c, final double p) {
		if (c == null) {
			return null;
		}

		double r = c.getRed();
		double g = c.getGreen();
		double b = c.getBlue();

		r -= (r * p) / 100.0;
		g -= (g * p) / 100.0;
		b -= (b * p) / 100.0;

		return createColor((int) r, (int) g, (int) b);
	}

	public static int getGrayValue(final Color c) {
		if (c == null) {
			return 0;
		}

		final double r = c.getRed();
		final double g = c.getGreen();
		final double b = c.getBlue();
		return Math.min(255, (int) (r * 0.28 + g * 0.59 + b * 0.13));
	}

	public static Color median(final Color c1, final Color c2) {
		if ((c1 == null || c2 == null)) {
			return null;
		}

		final int r = (c1.getRed() + c2.getRed()) / 2;
		final int g = (c1.getGreen() + c2.getGreen()) / 2;
		final int b = (c1.getBlue() + c2.getBlue()) / 2;
		return createColor(r, g, b);
	}

	public static Color toGray(final Color c) {
		if (c == null) {
			return null;
		}

		final int gray = getGrayValue(c);
		return new Color(gray, gray, gray, c.getAlpha());
	}

	private ColorHelper() {
	}
}
