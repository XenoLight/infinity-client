/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseToggleButtonUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiToggleButtonUI extends BaseToggleButtonUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiToggleButtonUI();
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
		int offs = 0;
		if (model.isArmed() && model.isPressed()) {
			offs = 1;
		}

		final Graphics2D g2D = (Graphics2D) g;
		final Composite composite = g2D.getComposite();
		final AlphaComposite alpha = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.6f);
		g2D.setComposite(alpha);
		Color fc = b.getForeground();
		if (!JTattooUtilities.isFrameActive(b)) {
			fc = Color.white;
		}
		if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
			fc = AbstractLookAndFeel.getTheme().getSelectionForegroundColor();
		}
		if (!model.isEnabled()) {
			fc = AbstractLookAndFeel.getTheme().getDisabledForegroundColor();
		}
		if (ColorHelper.getGrayValue(fc) > 128) {
			g2D.setColor(Color.black);
		} else {
			g2D.setColor(Color.white);
		}
		JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex,
				textRect.x + offs + 1, textRect.y + offs + fm.getAscent() + 1);
		g2D.setComposite(composite);
		g2D.setColor(fc);
		JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex,
				textRect.x + offs, textRect.y + offs + fm.getAscent());
	}
}
