/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.text.View;

public class BaseToggleButtonUI extends BasicToggleButtonUI {

	private static Rectangle viewRect = new Rectangle();
	private static Rectangle textRect = new Rectangle();
	private static Rectangle iconRect = new Rectangle();
	protected static Color[] rolloverPressedColors = null;

	public static ComponentUI createUI(final JComponent b) {
		return new BaseToggleButtonUI();
	}

	@Override
	protected BasicButtonListener createButtonListener(final AbstractButton b) {
		return new BaseButtonListener(b);
	}

	@Override
	public void installDefaults(final AbstractButton b) {
		super.installDefaults(b);
		b.setOpaque(false);
		final Color cArr[] = AbstractLookAndFeel.getTheme().getPressedColors();
		rolloverPressedColors = new Color[cArr.length];
		for (int i = 0; i < cArr.length; i++) {
			rolloverPressedColors[i] = ColorHelper.brighter(cArr[i], 20);
		}
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		final Graphics2D g2D = (Graphics2D) g;

		final AbstractButton b = (AbstractButton) c;
		final Font f = c.getFont();
		g.setFont(f);
		final FontMetrics fm = g.getFontMetrics();
		final Insets insets = c.getInsets();

		viewRect.x = insets.left;
		viewRect.y = insets.top;
		viewRect.width = b.getWidth() - (insets.right + viewRect.x);
		viewRect.height = b.getHeight() - (insets.bottom + viewRect.y);

		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

		final String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(),
				b.getIcon(), b.getVerticalAlignment(),
				b.getHorizontalAlignment(), b.getVerticalTextPosition(),
				b.getHorizontalTextPosition(), viewRect, iconRect, textRect,
				b.getText() == null ? 0 : defaultTextIconGap);

		paintBackground(g, b);

		if (b.getIcon() != null) {
			if (!b.isEnabled()) {
				final Composite composite = g2D.getComposite();
				final AlphaComposite alpha = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.5f);
				g2D.setComposite(alpha);
				paintIcon(g, c, iconRect);
				g2D.setComposite(composite);
			} else {
				paintIcon(g, c, iconRect);
			}
		}

		if (text != null && !text.equals("") && textRect != null) {
			final View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null) {
				Object savedRenderingHint = null;
				if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
					savedRenderingHint = g2D
					.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
					g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							AbstractLookAndFeel.getTheme()
							.getTextAntiAliasingHint());
				}
				v.paint(g, textRect);
				if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
					g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							savedRenderingHint);
				}
			} else {
				paintText(g, b, textRect, text);
			}
		}

		if (b.isFocusPainted() && b.hasFocus()) {
			paintFocus(g, b, viewRect, textRect, iconRect);
		}
	}

	protected void paintBackground(final Graphics g, final AbstractButton b) {
		if (!b.isContentAreaFilled() || (b.getParent() instanceof JMenuBar)) {
			return;
		}

		final int width = b.getWidth();
		final int height = b.getHeight();
		final ButtonModel model = b.getModel();
		Color colors[] = null;
		if (b.isEnabled()) {
			if (JTattooUtilities.isFrameActive(b)) {
				if (b.getBackground() instanceof ColorUIResource) {
					if (model.isRollover()) {
						if (model.isSelected()) {
							colors = rolloverPressedColors;
						} else {
							colors = AbstractLookAndFeel.getTheme()
							.getRolloverColors();
						}
					} else if ((model.isPressed() && model.isArmed())
							|| model.isSelected()) {
						colors = AbstractLookAndFeel.getTheme()
						.getPressedColors();
					} else {
						colors = AbstractLookAndFeel.getTheme()
						.getButtonColors();
					}
				} else {
					if (model.isRollover()) {
						colors = ColorHelper
						.createColorArr(ColorHelper.brighter(
								b.getBackground(), 80), ColorHelper
								.brighter(b.getBackground(), 20), 20);
					} else if (model.isPressed() && model.isArmed()
							|| model.isSelected()) {
						colors = ColorHelper.createColorArr(b.getBackground(),
								ColorHelper.darker(b.getBackground(), 50), 20);
					} else {
						colors = ColorHelper.createColorArr(
								ColorHelper.brighter(b.getBackground(), 40),
								ColorHelper.darker(b.getBackground(), 20), 20);
					}
				}
			} else { // inactive
				if (model.isRollover()) {
					colors = AbstractLookAndFeel.getTheme().getRolloverColors();
				} else {
					colors = AbstractLookAndFeel.getTheme().getInActiveColors();
				}
			}
		} else { // disabled
			colors = AbstractLookAndFeel.getTheme().getDisabledColors();
		}
		JTattooUtilities
		.fillHorGradient(g, colors, 1, 1, width - 2, height - 2);
	}

	@Override
	protected void paintFocus(final Graphics g, final AbstractButton b, final Rectangle viewRect,
			final Rectangle textRect, final Rectangle iconRect) {
		g.setColor(AbstractLookAndFeel.getFocusColor());
		BasicGraphicsUtils.drawDashedRect(g, 4, 3, b.getWidth() - 8,
				b.getHeight() - 6);
	}

	@Override
	protected void paintText(final Graphics g, final AbstractButton b, final Rectangle textRect,
			final String text) {
		final ButtonModel model = b.getModel();
		final FontMetrics fm = g.getFontMetrics();
		int mnemIndex = -1;
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			mnemIndex = b.getDisplayedMnemonicIndex();
		} else {
			mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(
					b.getText(), model.getMnemonic());
		}

		if (model.isEnabled()) {
			int offs = 0;
			if ((model.isArmed() && model.isPressed()) || model.isSelected()) {
				offs = 1;
			}
			g.setColor(b.getForeground());
			JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex,
					textRect.x + offs, textRect.y + offs + fm.getAscent());
		} else {
			g.setColor(Color.white);
			JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex,
					textRect.x + 1, textRect.y + 1 + fm.getAscent());
			g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
			JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex,
					textRect.x, textRect.y + fm.getAscent());
		}
	}

	@Override
	public void uninstallDefaults(final AbstractButton b) {
		super.uninstallDefaults(b);
		b.setOpaque(true);
	}
}
