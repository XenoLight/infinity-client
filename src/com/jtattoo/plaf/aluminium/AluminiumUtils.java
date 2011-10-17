/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AluminiumUtils {

	public static void fillComponent(final Graphics g, final Component c) {
		if (!JTattooUtilities.isMac()
				&& AbstractLookAndFeel.getTheme().isBackgroundPatternOn()) {
			final Point offset = JTattooUtilities.getRelLocation(c);
			final Dimension size = JTattooUtilities.getFrameSize(c);
			final Graphics2D g2D = (Graphics2D) g;
			g2D.setPaint(new AluminiumGradientPaint(offset, size));
			g2D.fillRect(0, 0, c.getWidth(), c.getHeight());
			g2D.setPaint(null);
		} else {
			g.setColor(AbstractLookAndFeel.getBackgroundColor());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}
	}

	public static void fillComponent(final Graphics g, final Component c, final int x, final int y,
			final int w, final int h) {
		final Shape savedClip = g.getClip();
		g.setClip(x, y, w, h);
		fillComponent(g, c);
		g.setClip(savedClip);
	}

	private AluminiumUtils() {
	}
}
