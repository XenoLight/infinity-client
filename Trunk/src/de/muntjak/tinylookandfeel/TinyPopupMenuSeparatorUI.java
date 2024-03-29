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
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSeparatorUI;

/**
 * TinyPopupMenuSeparatorUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyPopupMenuSeparatorUI extends MetalSeparatorUI {

	private static final Dimension preferredSize = new Dimension(0, 3);

	public static ComponentUI createUI(final JComponent c) {
		return new TinyPopupMenuSeparatorUI();
	}

	private void drawXpSeparator(final Graphics g, final Dimension s) {
		g.setColor(Theme.menuPopupColor.getColor());
		g.fillRect(0, 0, s.width, s.height);

		g.setColor(Theme.menuSeparatorColor.getColor());
		g.drawLine(2, 1, s.width - 3, 1);
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		return preferredSize;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		drawXpSeparator(g, c.getSize());
	}
}