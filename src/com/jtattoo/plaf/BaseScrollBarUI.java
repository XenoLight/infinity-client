/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Adjustable;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * @author Michael Hagen
 */
public class BaseScrollBarUI extends BasicScrollBarUI {

	protected class MyTrackListener extends TrackListener {

		@Override
		public void mouseEntered(final MouseEvent e) {
			super.mouseEntered(e);
			isRollover = true;
			final Rectangle r = getTrackBounds();
			scrollbar.repaint(r.x, r.y, r.width, r.height);
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			super.mouseExited(e);
			isRollover = false;
			final Rectangle r = getTrackBounds();
			scrollbar.repaint(r.x, r.y, r.width, r.height);
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			final Rectangle r = getTrackBounds();
			scrollbar.repaint(r.x, r.y, r.width, r.height);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			super.mouseReleased(e);
			final Rectangle r = getTrackBounds();
			scrollbar.repaint(r.x, r.y, r.width, r.height);
		}
	}
	public static ComponentUI createUI(final JComponent c) {
		return new BaseScrollBarUI();
	}

	protected int scrollBarWidth = 17;

	protected boolean isRollover = false;

	@Override
	protected JButton createDecreaseButton(final int orientation) {
		return new BaseScrollButton(orientation, scrollBarWidth);
	}

	@Override
	protected JButton createIncreaseButton(final int orientation) {
		return new BaseScrollButton(orientation, scrollBarWidth);
	}

	@Override
	public TrackListener createTrackListener() {
		return new MyTrackListener();
	}

	@Override
	protected Dimension getMinimumThumbSize() {
		return new Dimension(scrollBarWidth, scrollBarWidth);
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
			return new Dimension(scrollBarWidth, scrollBarWidth * 3 + 16);
		} else {
			return new Dimension(scrollBarWidth * 3 + 16, scrollBarWidth);
		}
	}

	protected Color[] getThumbColors() {
		if (isRollover) {
			return AbstractLookAndFeel.getTheme().getRolloverColors();
		} else if (!JTattooUtilities.isActive(scrollbar)) {
			return AbstractLookAndFeel.getTheme().getInActiveColors();
		} else {
			return AbstractLookAndFeel.getTheme().getThumbColors();
		}
	}

	@Override
	protected void installDefaults() {
		scrollBarWidth = UIManager.getInt("ScrollBar.width");
		super.installDefaults();
	}

	@Override
	protected void paintThumb(final Graphics g, final JComponent c, final Rectangle thumbBounds) {
		if (!c.isEnabled()) {
			return;
		}

		g.translate(thumbBounds.x, thumbBounds.y);

		final Color colors[] = getThumbColors();

		final Color frameColorHi = ColorHelper.brighter(colors[1], 20);
		final Color frameColorLo = ColorHelper.darker(colors[colors.length - 1], 10);

		if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
			JTattooUtilities.fillVerGradient(g, colors, 1, 1,
					thumbBounds.width - 1, thumbBounds.height - 1);
			JTattooUtilities.draw3DBorder(g, frameColorLo,
					ColorHelper.darker(frameColorLo, 15), 0, 0,
					thumbBounds.width, thumbBounds.height);
			g.setColor(frameColorHi);
			g.drawLine(1, 1, thumbBounds.width - 2, 1);
			g.drawLine(1, 1, 1, thumbBounds.height - 2);

			final Graphics2D g2D = (Graphics2D) g;
			final Composite composite = g2D.getComposite();
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.4f);
			g2D.setComposite(alpha);

			final int dx = 5;
			int dy = thumbBounds.height / 2 - 3;
			final int dw = thumbBounds.width - 11;

			final Color c1 = Color.white;
			final Color c2 = Color.darkGray;

			for (int i = 0; i < 4; i++) {
				g.setColor(c1);
				g.drawLine(dx, dy, dx + dw, dy);
				dy++;
				g.setColor(c2);
				g.drawLine(dx, dy, dx + dw, dy);
				dy++;
			}
			g2D.setComposite(composite);
		} else // HORIZONTAL
		{
			JTattooUtilities.fillHorGradient(g, colors, 1, 1,
					thumbBounds.width - 1, thumbBounds.height - 1);
			JTattooUtilities.draw3DBorder(g, frameColorLo,
					ColorHelper.darker(frameColorLo, 10), 0, 0,
					thumbBounds.width, thumbBounds.height);
			g.setColor(frameColorHi);
			g.drawLine(1, 1, thumbBounds.width - 2, 1);
			g.drawLine(1, 1, 1, thumbBounds.height - 2);

			int dx = thumbBounds.width / 2 - 3;
			final int dy = 5;
			final int dh = thumbBounds.height - 11;

			final Graphics2D g2D = (Graphics2D) g;
			final Composite composite = g2D.getComposite();
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.4f);
			g2D.setComposite(alpha);

			final Color c1 = Color.white;
			final Color c2 = Color.darkGray;

			for (int i = 0; i < 4; i++) {
				g.setColor(c1);
				g.drawLine(dx, dy, dx, dy + dh);
				dx++;
				g.setColor(c2);
				g.drawLine(dx, dy, dx, dy + dh);
				dx++;
			}
			g2D.setComposite(composite);
		}

		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	@Override
	protected void paintTrack(final Graphics g, final JComponent c, final Rectangle trackBounds) {
		final int w = c.getWidth();
		final int h = c.getHeight();
		if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
			JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme()
					.getTrackColors(), 0, 0, w, h);
		} else {
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme()
					.getTrackColors(), 0, 0, w, h);
		}
	}
}
