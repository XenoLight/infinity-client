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
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseButtonUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiButtonUI extends BaseButtonUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiButtonUI();
	}

	@Override
	protected void paintBackground(final Graphics g, final AbstractButton b) {
		final int w = b.getWidth();
		final int h = b.getHeight();
		final Graphics2D g2D = (Graphics2D) g;
		final Shape savedClip = g.getClip();
		if ((b.getBorder() != null) && b.isBorderPainted()
				&& (b.getBorder() instanceof UIResource)) {
			final Area clipArea = new Area(savedClip);
			final Area rectArea = new Area(new Rectangle2D.Double(1, 1, w - 2, h - 2));
			rectArea.intersect(clipArea);
			g2D.setClip(rectArea);
		}
		super.paintBackground(g, b);
		g2D.setClip(savedClip);
	}

	@Override
	protected void paintText(final Graphics g, final AbstractButton b, final Rectangle textRect) {
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
		if (model.isPressed() && model.isArmed()) {
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
		JTattooUtilities.drawStringUnderlineCharAt(b, g, b.getText(),
				mnemIndex, textRect.x + offs + 1,
				textRect.y + offs + fm.getAscent() + 1);
		g2D.setComposite(composite);
		g2D.setColor(fc);
		JTattooUtilities.drawStringUnderlineCharAt(b, g, b.getText(),
				mnemIndex, textRect.x + offs,
				textRect.y + offs + fm.getAscent());
	}
}
