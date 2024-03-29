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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyProgressBarUI
 * 
 * @version 1.1
 * @author Hans Bickel
 */
public class TinyProgressBarUI extends BasicProgressBarUI {

	/*
	 * ProgressKey is used as key in the cache HashMap. Overrides equals() and
	 * hashCode().
	 */
	private static class ProgressKey {

		private final Color c;
		private final boolean horizontal;
		private final int size;

		ProgressKey(final Color c, final boolean horizontal, final int size) {
			this.c = c;
			this.horizontal = horizontal;
			this.size = size;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null)
				return false;
			if (!(o instanceof ProgressKey))
				return false;

			final ProgressKey other = (ProgressKey) o;

			return size == other.size && horizontal == other.horizontal
			&& c.equals(other.c);
		}

		@Override
		public int hashCode() {
			return c.hashCode() * (horizontal ? 1 : 2) * size;
		}
	}

	private static final HashMap cache = new HashMap();
	/* "Override" the dimensions from BasicProgressBarUI */
	private static final Dimension PREFERRED_YQ_HORIZONTAL = new Dimension(146,
			7);

	private static final Dimension PREFERRED_YQ_VERTICAL = new Dimension(7, 146);

	public static void clearCache() {
		cache.clear();
	}

	/**
	 * Creates the UI delegate for the given component.
	 * 
	 * @param mainColor
	 *            The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(final JComponent c) {
		return new TinyProgressBarUI();
	}

	// draw determinate
	private void drawXpHorzProgress(final Graphics g, final int x, final int y, final int w, final int h,
			final int amountFull) {
		g.translate(x, y);

		// paint the track
		if (!progressBar.isOpaque()) {
			g.setColor(progressBar.getBackground());
			g.fillRect(0, 0, w, h);
		}

		final ProgressKey key = new ProgressKey(progressBar.getForeground(), true, h);
		Object value = cache.get(key);

		if (value == null) {
			// create new image
			final Image img = new BufferedImage(6, h, BufferedImage.TYPE_INT_ARGB);
			final Graphics imgGraphics = img.getGraphics();

			// draw into image graphics
			final Color c = progressBar.getForeground();
			final Color c2 = ColorRoutines.lighten(c, 15);
			final Color c3 = ColorRoutines.lighten(c, 35);
			final Color c4 = ColorRoutines.lighten(c, 60);

			imgGraphics.setColor(c4);
			imgGraphics.drawLine(0, 0, 5, 0);
			imgGraphics.drawLine(0, h - 1, 5, h - 1);

			imgGraphics.setColor(c3);
			imgGraphics.drawLine(0, 1, 5, 1);
			imgGraphics.drawLine(0, h - 2, 5, h - 2);

			imgGraphics.setColor(c2);
			imgGraphics.drawLine(0, 2, 5, 2);
			imgGraphics.drawLine(0, h - 3, 5, h - 3);

			imgGraphics.setColor(c);
			imgGraphics.fillRect(0, 3, 6, h - 6);

			// dispose of image graphics
			imgGraphics.dispose();

			cache.put(key, img);
			value = img;

			if (TinyLookAndFeel.PRINT_CACHE_SIZES) {
				System.out.println("TinyProgressBarUI.cache.size="
						+ cache.size());
			}
		}

		int mx = 0;

		while (mx < amountFull) {
			if (mx + 6 > w) {
				// paint partially
				g.drawImage((Image) value, mx, 0, w - mx, h, progressBar);
			} else {
				g.drawImage((Image) value, mx, 0, progressBar);
			}

			mx += 8;
		}

		g.translate(-x, -y);
	}

	// draw indeterminate
	private void drawXpHorzProgress(final Graphics g, final int x, final int y, final int w, final int h,
			final Rectangle boxRect) {
		// paint the track
		if (!progressBar.isOpaque()) {
			g.setColor(progressBar.getBackground());
			g.fillRect(x, y, w, h);
		}

		g.translate(boxRect.x, boxRect.y);

		final ProgressKey key = new ProgressKey(progressBar.getForeground(), true, h);
		Object value = cache.get(key);

		if (value == null) {
			// create new image
			final Image img = new BufferedImage(6, h, BufferedImage.TYPE_INT_ARGB);
			final Graphics imgGraphics = img.getGraphics();

			// draw into image graphics
			final Color c = progressBar.getForeground();
			final Color c2 = ColorRoutines.lighten(c, 15);
			final Color c3 = ColorRoutines.lighten(c, 35);
			final Color c4 = ColorRoutines.lighten(c, 60);

			imgGraphics.setColor(c4);
			imgGraphics.drawLine(0, 0, 5, 0);
			imgGraphics.drawLine(0, h - 1, 5, h - 1);

			imgGraphics.setColor(c3);
			imgGraphics.drawLine(0, 1, 5, 1);
			imgGraphics.drawLine(0, h - 2, 5, h - 2);

			imgGraphics.setColor(c2);
			imgGraphics.drawLine(0, 2, 5, 2);
			imgGraphics.drawLine(0, h - 3, 5, h - 3);

			imgGraphics.setColor(c);
			imgGraphics.fillRect(0, 3, 6, h - 6);

			// dispose of image graphics
			imgGraphics.dispose();

			cache.put(key, img);
			value = img;

			if (TinyLookAndFeel.PRINT_CACHE_SIZES) {
				System.out.println("TinyProgressBarUI.cache.size="
						+ cache.size());
			}
		}

		int mx = 0;

		while (mx + 6 < boxRect.width) {
			g.drawImage((Image) value, mx, 0, progressBar);

			mx += 8;
		}

		g.translate(-boxRect.x, -boxRect.y);
	}

	// draw determinate
	private void drawXpVertProgress(final Graphics g, final int x, final int y, final int w, final int h,
			final int amountFull) {
		g.translate(x, y);

		// paint the track
		if (!progressBar.isOpaque()) {
			g.setColor(progressBar.getBackground());
			g.fillRect(0, 0, w, h);
		}

		final ProgressKey key = new ProgressKey(progressBar.getForeground(), false, w);
		Object value = cache.get(key);

		if (value == null) {
			// create new image
			final Image img = new BufferedImage(w, 6, BufferedImage.TYPE_INT_ARGB);
			final Graphics imgGraphics = img.getGraphics();

			// draw into image graphics
			final Color c = progressBar.getForeground();
			final Color c2 = ColorRoutines.lighten(c, 15);
			final Color c3 = ColorRoutines.lighten(c, 35);
			final Color c4 = ColorRoutines.lighten(c, 60);

			imgGraphics.setColor(c4);
			imgGraphics.drawLine(0, 0, 0, 5);
			imgGraphics.drawLine(w - 1, 0, w - 1, 5);

			imgGraphics.setColor(c3);
			imgGraphics.drawLine(1, 0, 1, 5);
			imgGraphics.drawLine(w - 2, 0, w - 2, 5);

			imgGraphics.setColor(c2);
			imgGraphics.drawLine(2, 0, 2, 5);
			imgGraphics.drawLine(w - 3, 0, w - 3, 5);

			imgGraphics.setColor(c);
			imgGraphics.fillRect(3, 0, w - 6, 6);

			// dispose of image graphics
			imgGraphics.dispose();

			cache.put(key, img);
			value = img;

			if (TinyLookAndFeel.PRINT_CACHE_SIZES) {
				System.out.println("TinyProgressBarUI.cache.size="
						+ cache.size());
			}
		}

		// paints bottom to top...
		int my = 0;

		while (my < amountFull) {
			if (my + 6 > h) {
				// paint partially
				g.drawImage((Image) value, 0, 0, w, h - my, progressBar);
			} else {
				g.drawImage((Image) value, 0, h - my - 6, progressBar);
			}

			my += 8;
		}

		g.translate(-x, -y);
	}

	// draw indeterminate
	private void drawXpVertProgress(final Graphics g, final int x, final int y, final int w, final int h,
			final Rectangle boxRect) {
		// paint the track
		if (!progressBar.isOpaque()) {
			g.setColor(progressBar.getBackground());
			g.fillRect(x, y, w, h);
		}

		g.translate(boxRect.x, boxRect.y);

		final ProgressKey key = new ProgressKey(progressBar.getForeground(), false, w);
		Object value = cache.get(key);

		if (value == null) {
			// create new image
			final Image img = new BufferedImage(w, 6, BufferedImage.TYPE_INT_ARGB);
			final Graphics imgGraphics = img.getGraphics();

			// draw into image graphics
			final Color c = progressBar.getForeground();
			final Color c2 = ColorRoutines.lighten(c, 15);
			final Color c3 = ColorRoutines.lighten(c, 35);
			final Color c4 = ColorRoutines.lighten(c, 60);

			imgGraphics.setColor(c4);
			imgGraphics.drawLine(0, 0, 0, 5);
			imgGraphics.drawLine(w - 1, 0, w - 1, 5);

			imgGraphics.setColor(c3);
			imgGraphics.drawLine(1, 0, 1, 5);
			imgGraphics.drawLine(w - 2, 0, w - 2, 5);

			imgGraphics.setColor(c2);
			imgGraphics.drawLine(2, 0, 2, 5);
			imgGraphics.drawLine(w - 3, 0, w - 3, 5);

			imgGraphics.setColor(c);
			imgGraphics.fillRect(3, 0, w - 6, 6);

			// dispose of image graphics
			imgGraphics.dispose();

			cache.put(key, img);
			value = img;

			if (TinyLookAndFeel.PRINT_CACHE_SIZES) {
				System.out.println("TinyProgressBarUI.cache.size="
						+ cache.size());
			}
		}

		int my = 0;

		while (my + 6 < boxRect.height) {
			g.drawImage((Image) value, 0, my, progressBar);

			my += 8;
		}

		g.translate(-boxRect.x, -boxRect.y);
	}

	@Override
	protected Dimension getPreferredInnerHorizontal() {
		return PREFERRED_YQ_HORIZONTAL;
	}

	// private void paintString(Graphics g, int x, int y, int width, int height,
	// int fillStart, int amountFull, Insets b) {
	// if(!(g instanceof Graphics2D)) return;
	//
	// Graphics2D g2d = (Graphics2D)g;
	// String progressString = progressBar.getString();
	// g2d.setFont(progressBar.getFont());
	// Point renderLocation = getStringPlacement(g2d, progressString, x, y,
	// width, height);
	// Rectangle oldClip = g2d.getClipBounds();
	//
	// if(progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	// g2d.setColor(getSelectionBackground());
	// // New in 1.4.0: Antialiased text with 1.6 JREs
	// TinyUtils.drawString(g2d, progressString, renderLocation.x,
	// renderLocation.y);
	// g2d.setColor(getSelectionForeground());
	// g2d.clipRect(fillStart, y, amountFull, height);
	// // New in 1.4.0: Antialiased text with 1.6 JREs
	// TinyUtils.drawString(g2d, progressString, renderLocation.x,
	// renderLocation.y);
	// }
	// else { // VERTICAL
	// g2d.setColor(getSelectionBackground());
	// AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2);
	// g2d.setFont(progressBar.getFont().deriveFont(rotate));
	// renderLocation = getStringPlacement(g2d, progressString, x, y, width,
	// height);
	// // New in 1.4.0: Antialiased text with 1.6 JREs
	// TinyUtils.drawString(g2d, progressString, renderLocation.x,
	// renderLocation.y);
	// g2d.setColor(getSelectionForeground());
	// g2d.clipRect(x, fillStart, width, amountFull);
	// // New in 1.4.0: Antialiased text with 1.6 JREs
	// TinyUtils.drawString(g2d, progressString, renderLocation.x,
	// renderLocation.y);
	// }
	//
	// g2d.setClip(oldClip);
	// }

	/*
	 * Inserted this to fix a bug that came with 1.4.2_02 and caused NPE at
	 * javax
	 * .swing.plaf.basic.BasicProgressBarUI.updateSizes(BasicProgressBarUI.java
	 * :439).
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicProgressBarUI#paintString(java.awt.Graphics,
	 * int, int, int, int, int, java.awt.Insets)
	 */
	// protected void paintString(Graphics g, int x, int y, int width,
	// int height, int amountFull, Insets b)
	// {
	// Rectangle boxRect = new Rectangle(); // *
	// try { // * The Fix
	// boxRect = getBox(boxRect); // *
	// } catch (NullPointerException ignore) {} // *
	//
	// if(progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	// if(progressBar.getComponentOrientation().isLeftToRight()) {
	// if(progressBar.isIndeterminate()) {
	// paintString(g, x, y, width, height, boxRect.x, boxRect.width, b);
	// } else {
	// paintString(g, x, y, width, height, x, amountFull, b);
	// }
	// } else {
	// paintString(g, x, y, width, height, x + width - amountFull, amountFull,
	// b);
	// }
	// } else {
	// if(progressBar.isIndeterminate()) {
	// paintString(g, x, y, width, height, boxRect.y, boxRect.height, b);
	// } else {
	// paintString(g, x, y, width, height, y + height - amountFull, amountFull,
	// b);
	// }
	// }
	// }

	@Override
	protected Dimension getPreferredInnerVertical() {
		return PREFERRED_YQ_VERTICAL;
	}

	@Override
	protected Color getSelectionBackground() {
		return Theme.progressSelectBackColor.getColor();
	}

	@Override
	protected Color getSelectionForeground() {
		return Theme.progressSelectForeColor.getColor();
	}

	@Override
	protected void installDefaults() {
		// Note: Omitting the following line was a bug from v1.3.01 until
		// v1.3.04
		// Note: The following method turned out to be new in 1.5. Therefore
		// replaced with progressBar.setOpaque(true)
		// LookAndFeel.installProperty(progressBar, "opaque", Boolean.TRUE);

		// removed again in 1.3.7 (because opaque progress bar
		// fills bounds with track [background] color)
		// progressBar.setOpaque(true);

		LookAndFeel.installBorder(progressBar, "ProgressBar.border");
		LookAndFeel.installColorsAndFont(progressBar, "ProgressBar.background",
				"ProgressBar.foreground", "ProgressBar.font");
	}

	@Override
	protected void paintDeterminate(final Graphics g, final JComponent c) {
		final Insets b = progressBar.getInsets(); // area for border
		final int barRectWidth = progressBar.getWidth() - (b.right + b.left);
		final int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

		if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
			final int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

			drawXpHorzProgress(g, b.left, b.top, barRectWidth, barRectHeight,
					amountFull);

			// Deal with possible text painting
			if (progressBar.isStringPainted()) {
				g.setFont(c.getFont());
				paintString(g, b.left, b.top, barRectWidth, barRectHeight,
						amountFull, b);
			}

		} else { // VERTICAL
			final int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

			drawXpVertProgress(g, b.left, b.top, barRectWidth, barRectHeight,
					amountFull);

			// Deal with possible text painting
			if (progressBar.isStringPainted()) {
				g.setFont(c.getFont());
				paintString(g, b.left, b.top, barRectWidth, barRectHeight,
						amountFull, b);
			}
		}
	}

	@Override
	protected void paintIndeterminate(final Graphics g, final JComponent c) {
		final Insets b = progressBar.getInsets(); // area for border
		final int barRectWidth = progressBar.getWidth() - (b.right + b.left);
		final int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

		Rectangle boxRect = new Rectangle();

		try {
			boxRect = getBox(boxRect);
		} catch (final NullPointerException ignore) {
		}

		if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
			drawXpHorzProgress(g, b.left, b.top, barRectWidth, barRectHeight,
					boxRect);
		} else {
			drawXpVertProgress(g, b.left, b.top, barRectWidth, barRectHeight,
					boxRect);
		}

		// Deal with possible text painting
		if (progressBar.isStringPainted()) {
			if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
				// paintString(g, b.left, b.top, barRectWidth, barRectHeight,
				// boxRect.x, boxRect.width, b);
				paintString(g, b.left, b.top, barRectWidth, barRectHeight,
						boxRect.width, b);
			} else {
				// paintString(g, b.left, b.top, barRectWidth, barRectHeight,
				// boxRect.y, boxRect.height, b);
				paintString(g, b.left, b.top, barRectWidth, barRectHeight,
						boxRect.height, b);
			}
		}
	}
}