/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * @author Michael Hagen
 */
public class BaseSeparatorUI extends BasicSeparatorUI {

	private static final Dimension size = new Dimension(2, 3);

	public static ComponentUI createUI(final JComponent c) {
		return new BaseSeparatorUI();
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		return size;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		boolean horizontal = true;
		if (c instanceof JSeparator) {
			horizontal = (((JSeparator) c).getOrientation() == SwingConstants.HORIZONTAL);
		}
		if (horizontal) {
			final int w = c.getWidth();
			g.setColor(AbstractLookAndFeel.getBackgroundColor());
			g.drawLine(0, 0, w, 0);
			g.setColor(ColorHelper.darker(
					AbstractLookAndFeel.getBackgroundColor(), 30));
			g.drawLine(0, 1, w, 1);
			g.setColor(ColorHelper.brighter(
					AbstractLookAndFeel.getBackgroundColor(), 50));
			g.drawLine(0, 2, w, 2);
		} else {
			final int h = c.getHeight();
			g.setColor(ColorHelper.darker(
					AbstractLookAndFeel.getBackgroundColor(), 30));
			g.drawLine(0, 0, 0, h);
			g.setColor(ColorHelper.brighter(
					AbstractLookAndFeel.getBackgroundColor(), 50));
			g.drawLine(1, 0, 1, h);
		}
	}
}
