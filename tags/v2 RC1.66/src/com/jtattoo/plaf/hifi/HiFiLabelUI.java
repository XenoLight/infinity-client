/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiLabelUI extends BasicLabelUI {

	private static HiFiLabelUI labelUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (labelUI == null) {
			labelUI = new HiFiLabelUI();
		}
		return labelUI;
	}

	@Override
	protected void paintDisabledText(final JLabel l, final Graphics g, final String s, final int textX,
			final int textY) {
		int mnemIndex = -1;
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			mnemIndex = l.getDisplayedMnemonicIndex();
		} else {
			mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(
					l.getText(), l.getDisplayedMnemonic());
		}
		g.setColor(Color.black);
		JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex,
				textX + 1, textY + 1);
		g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
		JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX,
				textY);
	}

	@Override
	protected void paintEnabledText(final JLabel l, final Graphics g, final String s, final int textX,
			final int textY) {
		int mnemIndex = -1;
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			mnemIndex = l.getDisplayedMnemonicIndex();
		} else {
			mnemIndex = JTattooUtilities.findDisplayedMnemonicIndex(
					l.getText(), l.getDisplayedMnemonic());
		}
		final Color fc = l.getForeground();
		if (ColorHelper.getGrayValue(fc) > 128) {
			g.setColor(Color.black);
			JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex,
					textX + 1, textY + 1);
		}
		g.setColor(fc);
		JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX,
				textY);
	}
}
