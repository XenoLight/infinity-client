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

import javax.swing.JMenuBar;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.TinyToolBarUI;

/**
 * The border for menu bars.
 * 
 * @author Hans Bickel
 * @version 1.4.0
 */
public class TinyMenuBarBorder extends AbstractBorder implements UIResource {

	static final Insets borderInsets = new Insets(1, 1, 2, 1);

	@Override
	public Insets getBorderInsets(final Component c) {
		return borderInsets;
	}

	@Override
	public Insets getBorderInsets(final Component c, final Insets newInsets) {
		newInsets.top = borderInsets.top;
		newInsets.left = borderInsets.left;
		newInsets.bottom = borderInsets.bottom;
		newInsets.right = borderInsets.right;

		return newInsets;
	}

	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w, final int h) {
		// Only paint a border if we're not next to a horizontal toolbar
		if (!TinyToolBarUI.doesMenuBarBorderToolBar((JMenuBar) c)) {
			g.translate(x, y);
			g.setColor(Theme.toolBarDarkColor.getColor());
			g.drawLine(0, h - 1, w, h - 1);
			g.translate(-x, -y);
		}
	}
}
