/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiUtils {

	public static void fillComponent(final Graphics g, final Component c) {
		if (AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
			final int w = c.getWidth();
			final int h = c.getHeight();
			final Point p = JTattooUtilities.getRelLocation(c);
			int y = 2 - (p.y % 3);
			g.setColor(AbstractLookAndFeel.getTheme().getBackgroundColorLight());
			g.fillRect(0, 0, w, h);
			g.setColor(AbstractLookAndFeel.getTheme().getBackgroundColorDark());
			while (y < h) {
				g.drawLine(0, y, w, y);
				y += 3;
			}
		} else {
			g.setColor(AbstractLookAndFeel.getBackgroundColor());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}
	}

	private HiFiUtils() {
	}
}
