/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * @author Michael Hagen
 */
public class BaseLabelUI extends BasicLabelUI {

	private static BaseLabelUI baseLabelUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (baseLabelUI == null) {
			baseLabelUI = new BaseLabelUI();
		}
		return baseLabelUI;
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
		g.setColor(Color.white);
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
		g.setColor(l.getForeground());
		JTattooUtilities.drawStringUnderlineCharAt(l, g, s, mnemIndex, textX,
				textY);
	}
}
