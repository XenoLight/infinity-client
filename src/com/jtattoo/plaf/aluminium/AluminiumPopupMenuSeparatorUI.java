/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Color;
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
public class AluminiumPopupMenuSeparatorUI extends BasicSeparatorUI {

	private static final Dimension size = new Dimension(1, 1);

	public static ComponentUI createUI(final JComponent c) {
		return new AluminiumPopupMenuSeparatorUI();
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		return size;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		boolean horizontal = true;
		if (c instanceof JSeparator) {
			final JSeparator sep = ((JSeparator) c);
			horizontal = (sep.getOrientation() == SwingConstants.HORIZONTAL);
		}
		if (horizontal) {
			g.setColor(Color.lightGray);
			g.drawLine(0, 0, c.getWidth(), 0);
		} else {
			g.setColor(Color.lightGray);
			g.drawLine(0, 0, 0, c.getHeight());
		}
	}
}
