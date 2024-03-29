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
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyProgressBarBorder
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyProgressBarBorder extends AbstractBorder implements UIResource {

	protected static final Insets INSETS_YQ = new Insets(3, 3, 3, 3);

	private void drawXpBorder(final Component c, final Graphics g, int x, int y, int w,
			int h) {
		DrawRoutines.drawProgressBarBorder(g,
				Theme.progressBorderColor.getColor(), x, y, w, h);

		DrawRoutines.drawProgressBarBorder(g,
				Theme.progressDarkColor.getColor(), x + 1, y + 1, w - 2, h - 2);

		w -= 4;
		h -= 4;
		x += 2;
		y += 2;
		g.setColor(Theme.progressLightColor.getColor());
		// rect
		g.drawLine(x + 1, y, x + w - 2, y);
		g.drawLine(x, y + 1, x, y + h - 2);

		// track
		g.setColor(Theme.progressTrackColor.getColor());
		g.drawLine(x + 1, y + h - 1, x + w - 2, y + h - 1);
		g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 2);
	}

	/**
	 * Gets the border insets for a given component.
	 * 
	 * @param c
	 *            The component to get its border insets.
	 * @return Always returns the same insets as defined in <code>insets</code>.
	 */
	@Override
	public Insets getBorderInsets(final Component c) {
		return INSETS_YQ;
	}

	/**
	 * Draws the button border for the given component.
	 * 
	 * @param mainColor
	 *            The component to draw its border.
	 * @param g
	 *            The graphics context.
	 * @param x
	 *            The x coordinate of the top left corner.
	 * @param y
	 *            The y coordinate of the top left corner.
	 * @param w
	 *            The width.
	 * @param h
	 *            The height.
	 */
	@Override
	public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w, final int h) {
		drawXpBorder(c, g, x, y, w, h);
	}

}
