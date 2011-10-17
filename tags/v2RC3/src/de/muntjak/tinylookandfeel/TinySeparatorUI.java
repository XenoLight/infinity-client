/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * TinySeparatorUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinySeparatorUI extends BasicSeparatorUI {

	protected static final Dimension vertDimension = new Dimension(0, 2);
	protected static final Dimension horzDimension = new Dimension(2, 0);

	public static ComponentUI createUI(final JComponent c) {
		return new TinySeparatorUI();
	}

	protected void drawXpSeparator(final Graphics g, final JComponent c) {
		final Dimension s = c.getSize();
		g.setColor(c.getBackground());

		if (((JSeparator) c).getOrientation() == SwingConstants.VERTICAL) {
			g.drawLine(0, 0, 0, s.height);
		} else { // HORIZONTAL
			g.drawLine(0, 0, s.width, 0);
		}
	}

	@Override
	public Dimension getMinimumSize(final JComponent c) {
		return getPreferredSize(c);
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		if (((JSeparator) c).getOrientation() == SwingConstants.VERTICAL) {
			return horzDimension;
		} else {
			return vertDimension;
		}
	}

	@Override
	protected void installDefaults(final JSeparator s) {
		LookAndFeel.installColors(s, "Separator.background",
		"Separator.foreground");
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		drawXpSeparator(g, c);
	}
}
