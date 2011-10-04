/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import de.muntjak.tinylookandfeel.Theme;

/**
 * TinyToolTipBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolTipBorder implements Border {

	private static final Insets insets = new Insets(3, 3, 3, 3);
	private final boolean active;

	public TinyToolTipBorder(final boolean b) {
		active = b;
	}

	@Override
	public Insets getBorderInsets(final Component c) {
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w, final int h) {
		if (active) {
			g.setColor(Theme.tipBorderColor.getColor());
		} else {
			g.setColor(Theme.tipBorderDis.getColor());
		}

		g.drawRect(x, y, w - 1, h - 1);
	}
}
