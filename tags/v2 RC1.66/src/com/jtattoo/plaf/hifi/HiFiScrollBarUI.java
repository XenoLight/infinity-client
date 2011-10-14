/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Adjustable;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseScrollBarUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * 
 * @author Michael Hagen
 */
public class HiFiScrollBarUI extends BaseScrollBarUI {

	private static final Color frameHiColor = new Color(128, 128, 128);
	private static final Color frameLoColor = new Color(96, 96, 96);

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiScrollBarUI();
	}

	@Override
	protected JButton createDecreaseButton(final int orientation) {
		return new HiFiScrollButton(orientation, scrollBarWidth);
	}

	@Override
	protected JButton createIncreaseButton(final int orientation) {
		return new HiFiScrollButton(orientation, scrollBarWidth);
	}

	@Override
	protected Color[] getThumbColors() {
		if (isRollover && !isDragging) {
			return AbstractLookAndFeel.getTheme().getRolloverColors();
		} else if (!JTattooUtilities.isActive(scrollbar)) {
			return AbstractLookAndFeel.getTheme().getInActiveColors();
		} else {
			return AbstractLookAndFeel.getTheme().getThumbColors();
		}
	}

	@Override
	protected void paintThumb(final Graphics g, final JComponent c, final Rectangle thumbBounds) {
		if (!c.isEnabled()) {
			return;
		}

		final Graphics2D g2D = (Graphics2D) g;
		final Composite composite = g2D.getComposite();

		final int x = thumbBounds.x;
		final int y = thumbBounds.y;
		final int width = thumbBounds.width;
		final int height = thumbBounds.height;

		g.translate(x, y);

		final Color[] colors = getThumbColors();
		if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
			JTattooUtilities.fillVerGradient(g, colors, 1, 1, width - 1,
					height - 1);
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.6f);
			g2D.setComposite(alpha);
			final int dx = 5;
			int dy = height / 2 - 3;
			final int dw = width - 12;
			final Color c1 = ColorHelper.brighter(colors[0], 60);
			final Color c2 = ColorHelper.darker(colors[0], 30);
			for (int i = 0; i < 4; i++) {
				g.setColor(c1);
				g.drawLine(dx, dy, dx + dw, dy);
				dy++;
				g.setColor(c2);
				g.drawLine(dx, dy, dx + dw, dy);
				dy++;
			}
		} else // HORIZONTAL
		{
			JTattooUtilities.fillHorGradient(g, colors, 1, 1, width - 1,
					height - 1);
			int dx = width / 2 - 3;
			final int dy = 5;
			final int dh = height - 12;
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.8f);
			g2D.setComposite(alpha);
			final Color c1 = ColorHelper.brighter(colors[0], 60);
			final Color c2 = ColorHelper.darker(colors[0], 30);
			for (int i = 0; i < 4; i++) {
				g.setColor(c1);
				g.drawLine(dx, dy, dx, dy + dh);
				dx++;
				g.setColor(c2);
				g.drawLine(dx, dy, dx, dy + dh);
				dx++;
			}
		}

		g2D.setComposite(composite);
		JTattooUtilities.draw3DBorder(g, Color.darkGray, Color.black, 0, 0,
				width, height);
		g.setColor(frameHiColor);
		g.drawLine(1, 1, width - 2, 1);
		g.drawLine(1, 1, 1, height - 3);
		g.setColor(frameLoColor);
		g.drawLine(width - 2, 1, width - 2, height - 3);
		g.drawLine(2, height - 2, width - 3, height - 2);

		AlphaComposite alpha = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.5f);
		g2D.setComposite(alpha);
		final Color fc = colors[colors.length - 1];
		g2D.setColor(fc);
		g.drawLine(3, 2, width - 4, 2);
		g.drawLine(2, 3, 2, height - 4);

		g.setColor(ColorHelper.darker(fc, 30));
		g.drawLine(width - 1, 1, width - 1, height - 3);
		g.drawLine(3, height - 1, width - 3, height - 1);
		alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
		g2D.setComposite(alpha);
		g.drawLine(1, height - 2, 2, height - 1);
		g.drawLine(width - 1, height - 2, width - 2, height - 1);

		g.translate(-x, -y);
		g2D.setComposite(composite);
	}
}
