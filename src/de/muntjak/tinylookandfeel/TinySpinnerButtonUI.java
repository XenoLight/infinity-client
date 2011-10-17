/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;

import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinySpinnerButtonUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinySpinnerButtonUI extends TinyButtonUI {

	private static class ButtonKey {

		private final Color background, parentBackground;
		private final Dimension size;
		private final boolean rollover;
		private final int orientation;

		ButtonKey(final Color background, final Color parentBackground, final Dimension size,
				final boolean rollover, final int orientation) {
			this.background = background;
			this.parentBackground = parentBackground;
			this.size = size;
			this.rollover = rollover;
			this.orientation = orientation;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null)
				return false;
			if (!(o instanceof ButtonKey))
				return false;

			final ButtonKey other = (ButtonKey) o;

			return orientation == other.orientation
			&& rollover == other.rollover
			&& background.equals(other.background)
			&& parentBackground.equals(other.parentBackground)
			&& size.equals(other.size);
		}

		@Override
		public int hashCode() {
			return background.hashCode() * parentBackground.hashCode()
			* size.hashCode() * orientation * (rollover ? 2 : 1);
		}
	}

	// cache for already drawn spinner buttons - speeds up drawing by a factor
	// of 3
	// (new in 1.4.0)
	private static final HashMap cache = new HashMap();

	private final int orientation;

	protected static final Dimension xpSize = new Dimension(15, 8);

	public static void clearCache() {
		cache.clear();
	}

	public static ComponentUI createUI(final JComponent c) {
		throw new IllegalStateException("Must not be used this way.");
	}

	/**
	 * 
	 * @param button
	 *            a JSpinner's increment/decrement button
	 * @return the spinner
	 */
	public static JSpinner getSpinner(final Component button) {
		// With TinyLaF, the parent of a spinner button is the JSpinner
		return (JSpinner) button.getParent();
	}

	/**
	 * 
	 * @param button
	 *            a JSpinner's increment/decrement button
	 * @return the spinner's parent container
	 */
	public static Container getSpinnerParent(final Component button) {
		// With TinyLaF, the parent of a spinner button is the JSpinner,
		// so we return the spinner's parent
		Container parent = button.getParent();
		boolean spinnerFound = false;

		while (parent != null) {
			if (parent instanceof JSpinner) {
				spinnerFound = true;
			}

			parent = parent.getParent();

			if (spinnerFound)
				return parent;
		}

		return null;
	}

	/**
	 * Creates a new Spinner Button. Use either SwingConstants.SOUTH or
	 * SwingConstants.NORTH for a SpinnerButton of Type up or a down.
	 * 
	 * @param type
	 */
	TinySpinnerButtonUI(final int type) {
		orientation = type;
	}

	private void drawXpArrow(final Graphics g, final AbstractButton b) {
		int y = (b.getSize().height - 6) / 2;

		switch (orientation) {
		case SwingConstants.NORTH:
			y--;
			g.drawLine(7, y + 2, 7, y + 2);
			g.drawLine(6, y + 3, 8, y + 3);
			g.drawLine(5, y + 4, 9, y + 4);
			g.drawLine(4, y + 5, 6, y + 5);
			g.drawLine(8, y + 5, 10, y + 5);
			break;
		case SwingConstants.SOUTH:
			g.drawLine(4, y + 2, 6, y + 2);
			g.drawLine(8, y + 2, 10, y + 2);
			g.drawLine(5, y + 3, 9, y + 3);
			g.drawLine(6, y + 4, 8, y + 4);
			g.drawLine(7, y + 5, 7, y + 5);
			break;
		}
	}

	private void drawXpButton(final Graphics g, final AbstractButton b) {
		final boolean paintRollover = !b.getModel().isPressed()
		&& b.getModel().isRollover()
		&& Theme.spinnerRollover.getValue();
		final Color bg = getSpinnerParent(b).getBackground();

		// because g.getColor() signals if the button is enabled,
		// we don't need an extra enabled flag
		final ButtonKey key = new ButtonKey(g.getColor(), bg, b.getSize(),
				paintRollover, orientation);
		final Object value = cache.get(key);

		if (value != null) {
			// image already cached - paint image and return
			g.drawImage((Image) value, 0, 0, b);
			return;
		}

		final int x2 = b.getWidth() - 1;
		final int y2 = b.getHeight() - 1;
		final int h = b.getHeight();

		final Image img = new BufferedImage(x2 + 1, h, BufferedImage.TYPE_INT_ARGB);
		final Graphics imgGraphics = img.getGraphics();

		int spread1 = Theme.spinnerSpreadLight.getValue();
		int spread2 = Theme.spinnerSpreadDark.getValue();

		if (!b.isEnabled()) {
			spread1 = Theme.spinnerSpreadLightDisabled.getValue();
			spread2 = Theme.spinnerSpreadDarkDisabled.getValue();
		}

		final float spreadStep1 = 10.0f * spread1 / (h - 2);
		final float spreadStep2 = 10.0f * spread2 / (h - 2);
		final int halfY = h / 2;
		int yd;
		final Color c = g.getColor();

		for (int y = 1; y < h - 1; y++) {
			if (y < halfY) {
				yd = halfY - y;
				imgGraphics.setColor(ColorRoutines.lighten(c,
						(int) (yd * spreadStep1)));
			} else if (y == halfY) {
				imgGraphics.setColor(c);
			} else {
				yd = y - halfY;
				imgGraphics.setColor(ColorRoutines.darken(c,
						(int) (yd * spreadStep2)));
			}

			imgGraphics.drawLine(1, y, x2, y);
		}

		// paint background for edges
		imgGraphics.setColor(bg);
		imgGraphics.drawRect(0, 0, x2, y2);

		// left/top resp. left/bottom pixel should be painted
		// with spinner background
		imgGraphics.setColor(TinySpinnerButtonUI.getSpinner(b).getBackground());

		if (Boolean.TRUE.equals(b.getClientProperty("isNextButton"))) {
			// left/bottom
			imgGraphics.drawLine(0, y2, 0, y2);
		} else {
			// left/top
			imgGraphics.drawLine(0, 0, 0, 0);
		}

		// because spinner buttons are small, we paint
		// a simple and fast border.
		// New in 1.4.0: Instead of using button border colors
		// we use spinner border colors
		if (!b.isEnabled()) {
			imgGraphics.setColor(Theme.spinnerBorderDisabledColor.getColor());
		} else {
			imgGraphics.setColor(Theme.spinnerBorderColor.getColor());
		}

		// paint border
		imgGraphics.drawLine(1, 0, x2 - 1, 0);
		imgGraphics.drawLine(1, y2, x2 - 1, y2);
		imgGraphics.drawLine(0, 1, 0, y2 - 1);
		imgGraphics.drawLine(x2, 1, x2, y2 - 1);

		if (paintRollover) {
			DrawRoutines.drawRolloverBorder(imgGraphics,
					Theme.buttonRolloverColor.getColor(), 0, 0, x2 + 1, h);
		}

		// paint arrow
		if (!b.isEnabled()) {
			imgGraphics.setColor(Theme.spinnerArrowDisabledColor.getColor());
		} else {
			imgGraphics.setColor(Theme.spinnerArrowColor.getColor());
		}

		drawXpArrow(imgGraphics, b);

		// dispose of image graphics
		imgGraphics.dispose();

		// draw the image
		g.drawImage(img, 0, 0, b);

		// add the image to the cache
		cache.put(key, img);

		if (TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinySpinnerButton.cache.size=" + cache.size());
		}
	}

	private void drawXpButtonNoCache(final Graphics g, final AbstractButton b) {
		final int x2 = b.getWidth() - 1;
		final int y2 = b.getHeight() - 1;
		final int h = y2 + 1;

		int spread1 = Theme.spinnerSpreadLight.getValue();
		int spread2 = Theme.spinnerSpreadDark.getValue();
		if (!b.isEnabled()) {
			spread1 = Theme.spinnerSpreadLightDisabled.getValue();
			spread2 = Theme.spinnerSpreadDarkDisabled.getValue();
		}

		final float spreadStep1 = 10.0f * spread1 / (h - 2);
		final float spreadStep2 = 10.0f * spread2 / (h - 2);
		final int halfY = h / 2;
		int yd;
		final Color c = g.getColor();

		for (int y = 1; y < h - 1; y++) {
			if (y < halfY) {
				yd = halfY - y;
				g.setColor(ColorRoutines.lighten(c, (int) (yd * spreadStep1)));
			} else if (y == halfY) {
				g.setColor(c);
			} else {
				yd = y - halfY;
				g.setColor(ColorRoutines.darken(c, (int) (yd * spreadStep2)));
			}

			g.drawLine(1, y, x2, y);
		}

		// paint arrow
		if (!b.isEnabled()) {
			g.setColor(Theme.spinnerArrowDisabledColor.getColor());
		} else {
			g.setColor(Theme.spinnerArrowColor.getColor());
		}

		drawXpArrow(g, b);
	}

	/**
	 * @see javax.swing.plaf.basic.BasicButtonUI#getPreferredSize(javax.swing.JComponent)
	 */
	@Override
	public Dimension getPreferredSize(final JComponent c) {
		return xpSize;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		final AbstractButton button = (AbstractButton) c;

		if (!button.isEnabled()) {
			g.setColor(Theme.spinnerButtDisabledColor.getColor());
		} else if (button.getModel().isPressed()) {
			if (button.getModel().isRollover() || button.getModel().isArmed()) {
				g.setColor(Theme.spinnerButtPressedColor.getColor());
			} else {
				g.setColor(Theme.spinnerButtColor.getColor());
			}
		} else if (button.getModel().isRollover()
				|| button.getModel().isArmed()) {
			g.setColor(Theme.spinnerButtRolloverColor.getColor());
		} else {
			g.setColor(Theme.spinnerButtColor.getColor());
		}

		// paint button background and arrow
		if (TinyLookAndFeel.controlPanelInstantiated) {
			drawXpButtonNoCache(g, button);
		} else {
			drawXpButton(g, button);
		}
	}
}
