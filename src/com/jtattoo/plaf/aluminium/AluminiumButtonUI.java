/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseButtonUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AluminiumButtonUI extends BaseButtonUI {

	public static ComponentUI createUI(final JComponent c) {
		return new AluminiumButtonUI();
	}

	@Override
	protected void paintBackground(final Graphics g, final AbstractButton b) {
		if (!b.isContentAreaFilled() || (b.getParent() instanceof JMenuBar)) {
			return;
		}

		if (!(b.isBorderPainted() && (b.getBorder() instanceof UIResource))) {
			super.paintBackground(g, b);
			return;
		}

		final int width = b.getWidth();
		final int height = b.getHeight();
		final ButtonModel model = b.getModel();
		final Graphics2D g2D = (Graphics2D) g;
		final Composite composite = g2D.getComposite();
		final Object savedRenderingHint = g2D
		.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (((width < 64) || (height < 16))
				&& ((b.getText() == null) || b.getText().equals(""))) {
			Color[] colors = null;
			if (b.getBackground() instanceof ColorUIResource) {
				if (!model.isEnabled()) {
					colors = AbstractLookAndFeel.getTheme().getDisabledColors();
				} else if (model.isRollover()) {
					colors = AbstractLookAndFeel.getTheme().getRolloverColors();
				} else {
					colors = AbstractLookAndFeel.getTheme().getButtonColors();
				}
			} else {
				colors = ColorHelper.createColorArr(
						ColorHelper.brighter(b.getBackground(), 20),
						ColorHelper.darker(b.getBackground(), 20), 20);
			}
			JTattooUtilities.fillHorGradient(g, colors, 0, 0, width - 1,
					height - 1);
			if (model.isEnabled()) {
				g2D.setColor(AbstractLookAndFeel.getFrameColor());
			} else {
				g2D.setColor(ColorHelper.brighter(
						AbstractLookAndFeel.getFrameColor(), 20));
			}
			g2D.drawRect(0, 0, width - 1, height - 1);
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.6f);
			g2D.setComposite(alpha);
			g2D.setColor(Color.white);
			g2D.drawRect(1, 1, width - 3, height - 3);
		} else if (model.isPressed() && model.isArmed()) {
			final int d = height - 2;
			final Color color = AbstractLookAndFeel.getTheme()
			.getSelectionBackgroundColor();
			g2D.setColor(color);
			g2D.fillRoundRect(0, 0, width - 1, height - 1, d, d);

			g2D.setColor(ColorHelper.darker(color, 40));
			g2D.drawRoundRect(0, 0, width - 1, height - 1, d, d);
		} else {
			final int d = height - 2;
			Color[] colors = null;
			if (b.getBackground() instanceof ColorUIResource) {
				if (!model.isEnabled()) {
					colors = AbstractLookAndFeel.getTheme().getDisabledColors();
				} else if (model.isRollover()) {
					colors = AbstractLookAndFeel.getTheme().getRolloverColors();
				} else {
					colors = AbstractLookAndFeel.getTheme().getButtonColors();
				}
			} else {
				colors = ColorHelper.createColorArr(
						ColorHelper.brighter(b.getBackground(), 20),
						ColorHelper.darker(b.getBackground(), 20), 20);
			}

			final Shape savedClip = g.getClip();
			final Area clipArea = new Area(savedClip);
			final Area rectArea = new Area(new RoundRectangle2D.Double(0, 0,
					width - 1, height - 1, d, d));
			rectArea.intersect(clipArea);
			g2D.setClip(rectArea);
			JTattooUtilities.fillHorGradient(g, colors, 0, 0, width - 1,
					height - 1);
			g2D.setClip(savedClip);

			if (model.isEnabled()) {
				g2D.setColor(AbstractLookAndFeel.getFrameColor());
			} else {
				g2D.setColor(ColorHelper.brighter(
						AbstractLookAndFeel.getFrameColor(), 20));
			}
			g2D.drawRoundRect(0, 0, width - 1, height - 1, d, d);

			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.6f);
			g2D.setComposite(alpha);
			g2D.setColor(Color.white);
			g2D.drawRoundRect(1, 1, width - 3, height - 3, d - 2, d - 2);

		}
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				savedRenderingHint);
		g2D.setComposite(composite);
	}

	@Override
	protected void paintFocus(final Graphics g, final AbstractButton b, final Rectangle viewRect,
			final Rectangle textRect, final Rectangle iconRect) {
		final Graphics2D g2D = (Graphics2D) g;
		final int width = b.getWidth();
		final int height = b.getHeight();
		if (!b.isContentAreaFilled() || ((width < 64) || (height < 16))
				&& ((b.getText() == null) || b.getText().equals(""))) {
			g.setColor(AbstractLookAndFeel.getFocusColor());
			BasicGraphicsUtils.drawDashedRect(g, 4, 3, width - 8, height - 6);
		} else {
			final Object savedRenderingHint = g2D
			.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setColor(AbstractLookAndFeel.getFocusColor());
			final int d = b.getHeight() - 4;
			g2D.drawRoundRect(2, 2, b.getWidth() - 5, b.getHeight() - 5, d, d);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					savedRenderingHint);
		}
	}
}
