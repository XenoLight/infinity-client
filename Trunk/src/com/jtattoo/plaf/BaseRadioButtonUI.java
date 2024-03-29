/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.text.View;

/**
 * @author Michael Hagen
 */
public class BaseRadioButtonUI extends BasicRadioButtonUI {

	private static BaseRadioButtonUI radioButtonUI = null;
	/*
	 * These Dimensions/Rectangles are allocated once for all
	 * RadioButtonUI.paint() calls. Re-using rectangles rather than allocating
	 * them in each paint call substantially reduced the time it took paint to
	 * run. Obviously, this method can't be re-entered.
	 */
	private static Dimension size = new Dimension();
	private static Rectangle viewRect = new Rectangle();
	private static Rectangle iconRect = new Rectangle();
	private static Rectangle textRect = new Rectangle();

	public static ComponentUI createUI(final JComponent c) {
		if (radioButtonUI == null) {
			radioButtonUI = new BaseRadioButtonUI();
		}
		return radioButtonUI;
	}

	@Override
	public void installDefaults(final AbstractButton b) {
		super.installDefaults(b);
		b.setRolloverEnabled(true);
		icon = UIManager.getIcon("RadioButton.icon");
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		final AbstractButton b = (AbstractButton) c;
		final Font f = c.getFont();
		g.setFont(f);
		final FontMetrics fm = g.getFontMetrics();

		final Insets i = c.getInsets();
		size = b.getSize(size);
		viewRect.x = i.left;
		viewRect.y = i.top;
		viewRect.width = size.width - (i.right + viewRect.x);
		viewRect.height = size.height - (i.bottom + viewRect.y);
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		final Icon altIcon = b.getIcon();
		int iconTextGap = getDefaultTextIconGap(b);
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			iconTextGap = b.getIconTextGap();
		}
		final String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(),
				altIcon != null ? altIcon : getDefaultIcon(),
						b.getVerticalAlignment(), b.getHorizontalAlignment(),
						b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
						viewRect, iconRect, textRect, iconTextGap);

		// fill background
		if (c.isOpaque()) {
			paintBackground(g, c);
		}

		paintIcon(g, c, iconRect);

		if (text != null) {
			paintText(g, c, text, textRect);
		}

		if (b.hasFocus() && b.isFocusPainted() && (textRect.width > 0)
				&& (textRect.height > 0)) {
			paintFocus(g, textRect, size);
		}
	}

	protected void paintBackground(final Graphics g, final JComponent c) {
		g.setColor(c.getBackground());
		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}

	@Override
	protected void paintFocus(final Graphics g, final Rectangle t, final Dimension d) {
		g.setColor(AbstractLookAndFeel.getFocusColor());
		BasicGraphicsUtils.drawDashedRect(g, t.x, t.y - 1, t.width + 1,
				t.height + 1);
	}

	@Override
	protected void paintIcon(final Graphics g, final JComponent c, final Rectangle iconRect) {
		final AbstractButton b = (AbstractButton) c;
		final ButtonModel model = b.getModel();
		Icon ico = null;
		if (!model.isEnabled()) {
			if (b.isSelected()) {
				ico = b.getDisabledSelectedIcon();
			} else {
				ico = b.getDisabledIcon();
			}
		} else {
			if (model.isPressed()) {
				ico = b.getPressedIcon();
			} else {
				if (model.isRollover()) {
					if (b.isSelected()) {
						ico = b.getRolloverSelectedIcon();
					} else {
						ico = b.getRolloverIcon();
					}
				} else {
					if (b.isSelected()) {
						ico = b.getSelectedIcon();
					} else {
						ico = b.getIcon();
					}
				}
			}
		}

		if (ico != null) {
			ico.paintIcon(c, g, iconRect.x, iconRect.y - 1);
		} else {
			getDefaultIcon().paintIcon(c, g, iconRect.x, iconRect.y - 1);
		}
	}

	protected void paintText(final Graphics g, final JComponent c, final String text,
			final Rectangle textRect) {
		final View v = (View) c.getClientProperty(BasicHTML.propertyKey);
		if (v != null) {
			final Graphics2D g2D = (Graphics2D) g;
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
			final AbstractButton b = (AbstractButton) c;
			final ButtonModel model = b.getModel();
			final Font f = c.getFont();
			g.setFont(f);
			final FontMetrics fm = g.getFontMetrics();
			int mnemIndex = -1;
			if (JTattooUtilities.getJavaVersion() >= 1.4) {
				mnemIndex = b.getDisplayedMnemonicIndex();
			} else {
				mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(
						b.getText(), model.getMnemonic());
			}
			if (model.isEnabled()) {
				g.setColor(b.getForeground());
				JTattooUtilities.drawStringUnderlineCharAt(c, g, text,
						mnemIndex, textRect.x, textRect.y + fm.getAscent());
			} else {
				g.setColor(Color.white);
				JTattooUtilities.drawStringUnderlineCharAt(c, g, text,
						mnemIndex, textRect.x + 1,
						textRect.y + 1 + fm.getAscent());
				g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
				JTattooUtilities.drawStringUnderlineCharAt(c, g, text,
						mnemIndex, textRect.x, textRect.y + fm.getAscent());
			}
		}
	}
}
