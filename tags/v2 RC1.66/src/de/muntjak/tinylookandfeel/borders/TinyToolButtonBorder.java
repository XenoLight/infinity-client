/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.borders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.TinyFileChooserUI;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyToolButtonBorder is the border for JButton, JToggleButton and JSpinner
 * buttons.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolButtonBorder extends AbstractBorder {

	protected static final Insets insets = new Insets(1, 1, 1, 1);

	private void drawXpBorder(final Component c, final Graphics g, final int x, final int y, final int w,
			final int h) {
		final AbstractButton b = (AbstractButton) c;
		Color col = null;
		final boolean isFileChooserButton = Boolean.TRUE
		.equals(b
				.getClientProperty(TinyFileChooserUI.IS_FILE_CHOOSER_BUTTON_KEY));

		// New in 1.3.7 (previously only b.getModel().isRollover() evaluated)
		final boolean isRollover = b.getModel().isRollover()
		|| b.getModel().isArmed();

		if (b.getModel().isPressed()) {
			if (isRollover) {
				col = Theme.toolBorderPressedColor.getColor();
			} else {
				if (b.isSelected()) {
					col = Theme.toolBorderSelectedColor.getColor();
				} else {
					if (isFileChooserButton)
						return; // no border painted

					col = Theme.toolBorderColor.getColor();
				}
			}
		} else if (isRollover) {
			if (b.isSelected()) {
				col = Theme.toolBorderSelectedColor.getColor();
			} else {
				col = Theme.toolBorderRolloverColor.getColor();
			}
		} else if (b.isSelected()) {
			col = Theme.toolBorderSelectedColor.getColor();
		} else {
			if (isFileChooserButton)
				return; // no border painted

			col = Theme.toolBorderColor.getColor();
		}

		DrawRoutines.drawRoundedBorder(g, col, x, y, w, h);
	}

	/**
	 * Gets the border insets for a given component.
	 * 
	 * @return some insets...
	 */
	@Override
	public Insets getBorderInsets(final Component c) {
		if (!(c instanceof AbstractButton))
			return insets;

		final AbstractButton b = (AbstractButton) c;

		if (b.getMargin() == null || (b.getMargin() instanceof UIResource)) {
			return Theme.toolMargin;
		} else {
			final Insets margin = b.getMargin();

			return new Insets(margin.top + 1, margin.left + 1,
					margin.bottom + 1, margin.right + 1);
		}
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