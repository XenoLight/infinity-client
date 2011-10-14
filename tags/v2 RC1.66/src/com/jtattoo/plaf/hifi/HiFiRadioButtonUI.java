/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseRadioButtonUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiRadioButtonUI extends BaseRadioButtonUI {

	private static HiFiRadioButtonUI radioButtonUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (radioButtonUI == null) {
			radioButtonUI = new HiFiRadioButtonUI();
		}
		return radioButtonUI;
	}

	@Override
	public void paintBackground(final Graphics g, final JComponent c) {
		if (c.isOpaque()) {
			if ((c.getBackground().equals(AbstractLookAndFeel
					.getBackgroundColor()))
					&& (c.getBackground() instanceof ColorUIResource)) {
				HiFiUtils.fillComponent(g, c);
			} else {
				g.setColor(c.getBackground());
				g.fillRect(0, 0, c.getWidth(), c.getHeight());
			}
		}
	}

	@Override
	protected void paintText(final Graphics g, final JComponent c, final String text,
			final Rectangle textRect) {
		final View v = (View) c.getClientProperty(BasicHTML.propertyKey);
		if (v != null) {
			v.paint(g, textRect);
		} else {
			final AbstractButton b = (AbstractButton) c;
			final ButtonModel model = b.getModel();
			int mnemIndex = -1;
			if (JTattooUtilities.getJavaVersion() >= 1.4) {
				mnemIndex = b.getDisplayedMnemonicIndex();
			} else {
				mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(
						b.getText(), model.getMnemonic());
			}
			final Font f = c.getFont();
			g.setFont(f);
			final FontMetrics fm = g.getFontMetrics();
			if (model.isEnabled()) {
				final Color fc = b.getForeground();
				if (ColorHelper.getGrayValue(fc) > 128) {
					g.setColor(Color.black);
					JTattooUtilities.drawStringUnderlineCharAt(c, g, text,
							mnemIndex, textRect.x + 1,
							textRect.y + 1 + fm.getAscent());
				}
				g.setColor(fc);
				JTattooUtilities.drawStringUnderlineCharAt(c, g, text,
						mnemIndex, textRect.x, textRect.y + fm.getAscent());
			} else {
				g.setColor(Color.black);
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
