/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseBorders;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AluminiumBorders extends BaseBorders {

	// ------------------------------------------------------------------------------------
	// Implementation of border classes
	// ------------------------------------------------------------------------------------
	public static class ButtonBorder implements Border, UIResource {

		private static final Insets insets = new Insets(2, 12, 2, 12);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
		}
	} // class ButtonBorder
	public static class InternalFrameBorder extends BaseInternalFrameBorder {

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final Graphics2D g2D = (Graphics2D) g;
			Color titleColor = AbstractLookAndFeel
			.getWindowInactiveTitleBackgroundColor();
			if (isActive(c)) {
				titleColor = AbstractLookAndFeel
				.getWindowTitleBackgroundColor();
			}
			final int th = getTitleHeight(c);

			g.setColor(titleColor);
			g.fillRect(1, 1, w, dw);
			g.fillRect(1, h - dw, w, dw - 1);

			if (isActive(c)) {
				JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
						.getTheme().getWindowTitleColors(), 1, dw, dw, th + 1);
				JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
						.getTheme().getWindowTitleColors(), w - dw, dw, dw,
						th + 1);
				final Color c1 = AbstractLookAndFeel.getTheme()
				.getWindowTitleColorDark();
				final Color c2 = AbstractLookAndFeel.getTheme()
				.getWindowTitleColorLight();
				g2D.setPaint(new GradientPaint(0, dw + th + 1, c1, 0, h - th
						- (2 * dw), c2));
				g.fillRect(1, dw + th + 1, dw - 1, h - th - (2 * dw));
				g.fillRect(w - dw, dw + th + 1, dw - 1, h - th - (2 * dw));
				g2D.setPaint(null);
			} else {
				JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
						.getTheme().getWindowInactiveTitleColors(), 1, dw, dw,
						th + 1);
				JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
						.getTheme().getWindowInactiveTitleColors(), w - dw, dw,
						dw, th + 1);
				final Color c1 = AbstractLookAndFeel.getTheme()
				.getWindowInactiveTitleColorDark();
				final Color c2 = AbstractLookAndFeel.getTheme()
				.getWindowInactiveTitleColorLight();
				g2D.setPaint(new GradientPaint(0, dw + th + 1, c1, 0, h - th
						- (2 * dw), c2));
				g.fillRect(1, dw + th + 1, dw - 1, h - th - (2 * dw));
				g.fillRect(w - dw, dw + th + 1, dw - 1, h - th - (2 * dw));
				g2D.setPaint(null);
			}

			Color borderColor = AbstractLookAndFeel
			.getWindowInactiveBorderColor();
			if (isActive(c)) {
				borderColor = AbstractLookAndFeel.getWindowBorderColor();
			}
			g.setColor(borderColor);
			g.drawRect(0, 0, w - 1, h - 1);
			g.drawLine(x + dw - 1, y + insets.top + th, x + dw - 1, y + h - dw);
			g.drawLine(x + w - dw, y + insets.top + th, x + w - dw, y + h - dw);
			g.drawLine(x + dw - 1, y + h - dw, x + w - dw, y + h - dw);

			g.setColor(new Color(220, 220, 220));
			g.drawLine(1, 1, w - 3, 1);
			g.drawLine(1, 1, 1, h - 2);
		}
	}
	public static class RolloverToolButtonBorder implements Border, UIResource {

		private static final Insets insets = new Insets(1, 1, 1, 1);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final AbstractButton button = (AbstractButton) c;
			final ButtonModel model = button.getModel();
			final Color loColor = AbstractLookAndFeel.getFrameColor();
			if (model.isEnabled()) {
				if ((model.isPressed() && model.isArmed())
						|| model.isSelected()) {
					final Graphics2D g2D = (Graphics2D) g;
					final Composite composite = g2D.getComposite();
					g.setColor(loColor);
					g.drawRect(x, y, w - 1, h - 1);
					final AlphaComposite alpha = AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 0.15f);
					g2D.setComposite(alpha);
					g.setColor(Color.black);
					g.fillRect(x + 1, y + 1, w - 2, h - 2);
					g2D.setComposite(composite);
				} else if (model.isRollover()) {
					final Graphics2D g2D = (Graphics2D) g;
					final Composite composite = g2D.getComposite();
					g.setColor(loColor);
					g.drawRect(x, y, w - 1, h - 1);
					final AlphaComposite alpha = AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, 0.4f);
					g2D.setComposite(alpha);
					g.setColor(AbstractLookAndFeel.getTheme()
							.getSelectionBackgroundColor());
					g.fillRect(x + 1, y + 1, w - 2, h - 2);
					g2D.setComposite(composite);
				}
			}
		}
	} // class RolloverToolButtonBorder

	private static Border buttonBorder = null;

	private static Border rolloverToolButtonBorder = null;

	private static Border internalFrameBorder = null;

	// ------------------------------------------------------------------------------------
	// Lazy access methods
	// ------------------------------------------------------------------------------------
	public static Border getButtonBorder() {
		if (buttonBorder == null) {
			buttonBorder = new ButtonBorder();
		}
		return buttonBorder;
	}

	public static Border getInternalFrameBorder() {
		if (internalFrameBorder == null) {
			internalFrameBorder = new InternalFrameBorder();
		}
		return internalFrameBorder;
	}

	public static Border getRolloverToolButtonBorder() {
		if (rolloverToolButtonBorder == null) {
			rolloverToolButtonBorder = new RolloverToolButtonBorder();
		}
		return rolloverToolButtonBorder;
	}

	public static Border getToggleButtonBorder() {
		return getButtonBorder();
	}
} // class AluminiumBorders

