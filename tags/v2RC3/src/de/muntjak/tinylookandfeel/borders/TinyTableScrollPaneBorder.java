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

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;

/**
 * TinyTableScrollPaneBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyTableScrollPaneBorder extends AbstractBorder implements
UIResource {

	private static final Insets insets = new Insets(1, 1, 1, 1);

	@Override
	public Insets getBorderInsets(final Component c) {
		return insets;
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w, final int h) {
		g.setColor(Theme.tableBorderLightColor.getColor());
		g.drawLine(x + w - 1, y, x + w - 1, y + h - 1); // right
		g.drawLine(x, y + h - 1, x + w - 1, y + h - 1); // bottom

		g.setColor(Theme.tableBorderDarkColor.getColor());
		g.drawLine(x, y, x, y + h - 1); // left
		g.drawLine(x, y, x + w - 1, y); // top
	}
}