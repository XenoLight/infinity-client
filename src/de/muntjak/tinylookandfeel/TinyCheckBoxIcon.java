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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.plaf.metal.MetalCheckBoxIcon;
import javax.swing.table.TableCellRenderer;

import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * TinyCheckBoxIcon
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyCheckBoxIcon extends MetalCheckBoxIcon {

	/*
	 * CheckKey is used as key in the cache HashMap. Overrides equals() and
	 * hashCode().
	 */
	private static class CheckKey {

		private final Color c;
		private final boolean pressed;
		private final boolean enabled;
		private final boolean rollover;
		private final boolean focused;

		CheckKey(final Color c, final boolean pressed, final boolean enabled, final boolean rollover,
				final boolean focused) {
			this.c = c;
			this.pressed = pressed;
			this.enabled = enabled;
			this.rollover = rollover;
			this.focused = focused;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null)
				return false;
			if (!(o instanceof CheckKey))
				return false;

			final CheckKey other = (CheckKey) o;

			return pressed == other.pressed && enabled == other.enabled
			&& rollover == other.rollover && focused == other.focused
			&& c.equals(other.c);
		}

		@Override
		public int hashCode() {
			return c.hashCode() * (pressed ? 1 : 2) * (enabled ? 4 : 8)
			* (rollover ? 16 : 32);
		}
	}

	/*
	 * DisabledCheckKey is used as key in the cache HashMap. Overrides equals()
	 * and hashCode(). Used only if we are run from ControlPanel.
	 */
	private static class DisabledCheckKey {
		int spread1;
		int spread2;
		Color c, back;

		DisabledCheckKey(final Color c, final Color back) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
			this.c = c;
			this.back = back;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null)
				return false;
			if (!(o instanceof DisabledCheckKey))
				return false;

			final DisabledCheckKey other = (DisabledCheckKey) o;

			return (c.equals(other.c) && back.equals(other.back)
					&& spread1 == other.spread1 && spread2 == other.spread2);
		}

		@Override
		public int hashCode() {
			return c.hashCode() * back.hashCode() * spread1 * spread2;
		}
	}

	/*
	 * EnabledCheckKey is used as key in the cache HashMap. Overrides equals()
	 * and hashCode(). Used only if we are run from ControlPanel.
	 */
	private static class EnabledCheckKey {
		int spread1;
		int spread2;
		Color c, back;

		EnabledCheckKey(final Color c, final Color back) {
			spread1 = Theme.buttonSpreadLight.getValue();
			spread2 = Theme.buttonSpreadDark.getValue();
			this.c = c;
			this.back = back;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null)
				return false;
			if (!(o instanceof EnabledCheckKey))
				return false;

			final EnabledCheckKey other = (EnabledCheckKey) o;

			return (c.equals(other.c) && back.equals(other.back)
					&& spread1 == other.spread1 && spread2 == other.spread2);
		}

		@Override
		public int hashCode() {
			return c.hashCode() * back.hashCode() * spread1 * spread2;
		}
	}

	// cache for already drawn icons - speeds up drawing by a factor
	// of up to 100 if there are several check boxes or one check box
	// is painted several times
	private static final HashMap cache = new HashMap();

	// both width and height of checkbox
	private static int checkSize = 13;

	private static final int[][] a = {
		{ 255, 255, 255, 242, 228, 209, 187, 165, 142, 120, 104 },
		{ 255, 255, 242, 228, 209, 187, 165, 142, 120, 104, 86 },
		{ 255, 242, 228, 209, 187, 165, 142, 120, 104, 86, 72 },
		{ 242, 228, 209, 187, 165, 142, 120, 104, 86, 72, 56 },
		{ 228, 209, 187, 165, 142, 120, 104, 86, 72, 56, 42 },
		{ 209, 187, 165, 142, 120, 104, 86, 72, 56, 42, 28 },
		{ 187, 165, 142, 120, 104, 86, 72, 56, 42, 28, 17 },
		{ 165, 142, 120, 104, 86, 72, 56, 42, 28, 17, 9 },
		{ 142, 120, 104, 86, 72, 56, 42, 28, 17, 9, 0 },
		{ 120, 104, 86, 72, 56, 42, 28, 17, 9, 0, 0 },
		{ 104, 86, 72, 56, 42, 28, 17, 9, 0, 0, 0 } };

	public static void clearCache() {
		cache.clear();
	}

	private void drawXpCheck(final Graphics g, final AbstractButton b, Color c, final int x,
			final int y, final int w, final int h) {
		final boolean pressed = b.getModel().isPressed();
		final boolean armed = b.getModel().isArmed();
		final boolean enabled = b.isEnabled();
		final boolean rollover = b.getModel().isRollover();
		final boolean focused = (Theme.buttonFocusBorder.getValue() && !rollover && b
				.isFocusOwner());

		// In 1.3.5 key was build with argument rollover instead of (rollover ||
		// armed)
		// Fixed in 1.3.6
		final CheckKey key = new CheckKey(c, pressed, enabled, (rollover || armed),
				focused);
		final Object value = cache.get(key);

		if (value != null) {
			// image already cached - paint image and return
			g.drawImage((Image) value, x, y, b);
			return;
		}

		final Image img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		final Graphics imgGraphics = img.getGraphics();

		// spread light is between 0 and 20
		int spread1 = Theme.buttonSpreadLight.getValue();
		int spread2 = Theme.buttonSpreadDark.getValue();

		if (!b.isEnabled()) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
		}

		final int spreadStep1 = spread1 * 5; // 20 -> 100
		// this means, we can never fully darken background,
		// but we also want it bright enough
		int spreadStep2 = spread2 * 4; // 20 -> 80

		if (pressed && (rollover || armed)) {
			spreadStep2 *= 2;
		}

		c = ColorRoutines.lighten(c, spreadStep1);

		imgGraphics.setColor(ColorRoutines.darken(c, spreadStep2));
		imgGraphics.fillRect(1, 1, w - 2, h - 2);
		Color color;

		for (int row = 0; row < 11; row++) {
			for (int col = 0; col < 11; col++) {
				color = new Color(c.getRed(), c.getGreen(), c.getBlue(),
						255 - a[col][row]);
				imgGraphics.setColor(color);
				imgGraphics.drawLine(col + 1, row + 1, col + 1, row + 1);
			}
		}

		// border
		if (!b.isEnabled()) {
			imgGraphics.setColor(Theme.buttonBorderDisabledColor.getColor());
			imgGraphics.drawRect(0, 0, w - 1, h - 1);
		} else {
			imgGraphics.setColor(Theme.buttonBorderColor.getColor());
			imgGraphics.drawRect(0, 0, w - 1, h - 1);

			if (rollover && Theme.buttonRolloverBorder.getValue() && !pressed) {
				DrawRoutines.drawRolloverCheckBorder(imgGraphics,
						Theme.buttonRolloverColor.getColor(), 0, 0, w, h);
			} else if (focused && !pressed) {
				DrawRoutines.drawRolloverCheckBorder(imgGraphics,
						Theme.buttonDefaultColor.getColor(), 0, 0, w, h);
			}
		}

		// dispose of image graphics
		imgGraphics.dispose();

		// draw the image
		g.drawImage(img, x, y, b);

		// add the image to the cache
		cache.put(key, img);

		if (TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyCheckBoxIcon.cache.size=" + cache.size());
		}
	}

	private void drawXpCheckMark(final Graphics g, final int x, final int y) {
		g.drawLine(x + 3, y + 5, x + 3, y + 7);
		g.drawLine(x + 4, y + 6, x + 4, y + 8);
		g.drawLine(x + 5, y + 7, x + 5, y + 9);
		g.drawLine(x + 6, y + 6, x + 6, y + 8);
		g.drawLine(x + 7, y + 5, x + 7, y + 7);
		g.drawLine(x + 8, y + 4, x + 8, y + 6);
		g.drawLine(x + 9, y + 3, x + 9, y + 5);
	}

	private void drawXpCheckNoCache(final Graphics g, final AbstractButton b, Color c,
			final int x, final int y, final int w, final int h) {
		final boolean pressed = b.getModel().isPressed();
		final boolean armed = b.getModel().isArmed();
		final boolean enabled = b.isEnabled();
		final boolean rollover = b.getModel().isRollover();
		final boolean focused = (Theme.buttonFocusBorder.getValue() && !rollover && b
				.isFocusOwner());
		final boolean useCachedImage = !pressed && !armed && !rollover && !focused;
		Image img = null;
		Object key = null;

		if (useCachedImage) {
			// New in 1.3.7: Keys must also evaluate border color
			if (enabled) {
				key = new EnabledCheckKey(c, Theme.buttonBorderColor.getColor());
			} else {
				key = new DisabledCheckKey(c,
						Theme.buttonBorderDisabledColor.getColor());
			}

			final Object value = cache.get(key);

			if (value != null) {
				// image already cached - paint image and return
				g.drawImage((Image) value, x, y, b);
				return;
			}

			img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		}

		// spread light is between 0 and 20
		int spread1 = Theme.buttonSpreadLight.getValue();
		int spread2 = Theme.buttonSpreadDark.getValue();

		if (!enabled) {
			spread1 = Theme.buttonSpreadLightDisabled.getValue();
			spread2 = Theme.buttonSpreadDarkDisabled.getValue();
		}

		final int spreadStep1 = spread1 * 5; // 20 -> 100
		// this means, we can never fully darken background,
		// but we also want it bright enough
		int spreadStep2 = spread2 * 4; // 20 -> 80

		if (pressed && (rollover || armed)) {
			spreadStep2 *= 2;
		}

		c = ColorRoutines.lighten(c, spreadStep1);

		Graphics graphics = null;
		int bx = x, by = y;

		if (img != null) {
			graphics = img.getGraphics();

			Color bg = b.getBackground();

			if (!b.isOpaque()) {
				Container parent = b.getParent();
				bg = parent.getBackground();

				while (parent != null && !parent.isOpaque()) {
					parent = parent.getParent();
					bg = parent.getBackground();
				}
			}

			graphics.setColor(bg);
			graphics.fillRect(0, 0, w - 1, h - 1);
			bx = 0;
			by = 0;
		} else {
			graphics = g;
			graphics.translate(x, y);
		}

		graphics.setColor(ColorRoutines.darken(c, spreadStep2));
		graphics.fillRect(1, 1, w - 2, h - 2);
		Color color;

		for (int row = 0; row < 11; row++) {
			for (int col = 0; col < 11; col++) {
				color = new Color(c.getRed(), c.getGreen(), c.getBlue(),
						255 - a[col][row]);
				graphics.setColor(color);
				graphics.drawLine(col + 1, row + 1, col + 1, row + 1);
			}
		}

		if (img == null) {
			graphics.translate(-x, -y);
		}

		// border
		if (!enabled) {
			graphics.setColor(Theme.buttonBorderDisabledColor.getColor());
			graphics.drawRect(bx, by, w - 1, h - 1);
		} else {
			graphics.setColor(Theme.buttonBorderColor.getColor());
			graphics.drawRect(bx, by, w - 1, h - 1);

			if (rollover && Theme.buttonRolloverBorder.getValue() && !pressed) {
				DrawRoutines.drawRolloverCheckBorder(graphics,
						Theme.buttonRolloverColor.getColor(), bx, by, w, h);
			} else if (focused && !pressed) {
				DrawRoutines.drawRolloverCheckBorder(graphics,
						Theme.buttonDefaultColor.getColor(), bx, by, w, h);
			}
		}

		if (img != null) {
			// dispose of image graphics
			graphics.dispose();

			// draw the image
			g.drawImage(img, x, y, b);

			// cache image
			cache.put(key, img);
		}
	}

	@Override
	protected int getControlSize() {
		return getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return checkSize;
	}

	@Override
	public int getIconWidth() {
		return checkSize;
	}

	private void paintFlatCheck(final JCheckBox cb, final Graphics g, final int x, final int y) {
		if (cb.isSelected()) {
			if (!cb.isEnabled()) {
				g.setColor(Theme.buttonCheckDisabledColor.getColor());
			} else {
				g.setColor(Theme.buttonCheckColor.getColor());
			}

			drawXpCheckMark(g, x, y);
		}

		// Draw flat border
		g.setColor(Theme.buttonBorderColor.getColor());
		g.drawRect(x, y, checkSize - 1, checkSize - 1);
	}

	/**
	 * Draws the check box icon at the specified location.
	 * 
	 * @param c
	 *            The component to draw on.
	 * @param g
	 *            The graphics context.
	 * @param x
	 *            The x coordinate of the top left corner.
	 * @param y
	 *            The y coordinate of the top left corner.
	 */
	@Override
	public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
		final JCheckBox cb = (JCheckBox) c;
		final Container parent = cb.getParent();

		// New in 1.4.0: If we are a TableCellRenderer or a
		// TableCellEditor.component
		// or if borderPaintedFlat is true, we paint the icon only.
		if (cb.isBorderPaintedFlat() || (cb instanceof TableCellRenderer)
				|| (parent instanceof JTable)) {
			paintFlatCheck(cb, g, x, y);
			return;
		}

		Color col = null;

		if (!cb.isEnabled()) {
			col = Theme.buttonDisabledColor.getColor();
		} else if (cb.getModel().isPressed()) {
			if (cb.getModel().isRollover()) {
				col = Theme.buttonPressedColor.getColor();
			} else {
				col = Theme.buttonNormalColor.getColor();
			}
		} else if (cb.getModel().isRollover()) {
			col = Theme.buttonRolloverBgColor.getColor();
		} else {
			col = Theme.buttonNormalColor.getColor();
		}

		g.setColor(col);

		if (TinyLookAndFeel.controlPanelInstantiated) {
			drawXpCheckNoCache(g, cb, col, x, y, getIconWidth(),
					getIconHeight());
		} else {
			drawXpCheck(g, cb, col, x, y, getIconWidth(), getIconHeight());
		}

		// checkmark
		if (!cb.isSelected())
			return;

		if (!cb.isEnabled()) {
			g.setColor(Theme.buttonCheckDisabledColor.getColor());
		} else {
			g.setColor(Theme.buttonCheckColor.getColor());
		}

		drawXpCheckMark(g, x, y);
	}
}