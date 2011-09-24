/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseTabbedPaneUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiTabbedPaneUI extends BaseTabbedPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiTabbedPaneUI();
	}

	@Override
	protected Color[] getContentBorderColors(final int tabPlacement) {
		final Color SEP_COLORS[] = {
				ColorHelper
				.darker(AbstractLookAndFeel.getBackgroundColor(), 40),
				ColorHelper.brighter(AbstractLookAndFeel.getBackgroundColor(),
						20),
						ColorHelper
						.darker(AbstractLookAndFeel.getBackgroundColor(), 20),
						ColorHelper
						.darker(AbstractLookAndFeel.getBackgroundColor(), 40), };
		return SEP_COLORS;
	}

	@Override
	protected void paintContentBorder(final Graphics g, final int tabPlacement,
			final int selectedIndex, final int x, final int y, final int w, final int h) {
		HiFiUtils.fillComponent(g, tabPane);
		super.paintContentBorder(g, tabPlacement, selectedIndex, x, y, w, h);
	}

	@Override
	protected void paintRoundedTopTabBorder(final int tabIndex, final Graphics g, final int x1,
			final int y1, final int x2, final int y2, final boolean isSelected) {
		super.paintRoundedTopTabBorder(tabIndex, g, x1, y1, x2, y2, isSelected);
		g.setColor(tabAreaBackground);
		g.drawLine(x1 + 1, y1 + 1, x1 + 1, y1 + 1);
		g.drawLine(x2 - 1, y1 + 1, x2 - 1, y1 + 1);
	}

	@Override
	protected void paintText(final Graphics g, final int tabPlacement, final Font font,
			final FontMetrics metrics, final int tabIndex, final String title,
			final Rectangle textRect, final boolean isSelected) {
		g.setFont(font);
		final View v = getTextViewForTab(tabIndex);
		if (v != null) {
			// html
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
			// plain text
			int mnemIndex = -1;
			if (JTattooUtilities.getJavaVersion() >= 1.4) {
				mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);
			}

			final Graphics2D g2D = (Graphics2D) g;
			final Composite composite = g2D.getComposite();
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.6f);
			g2D.setComposite(alpha);
			Color fc = tabPane.getForegroundAt(tabIndex);
			if (isSelected) {
				fc = AbstractLookAndFeel.getTheme().getButtonForegroundColor();
			}
			if (!tabPane.isEnabled() || !tabPane.isEnabledAt(tabIndex)) {
				fc = AbstractLookAndFeel.getTheme()
				.getDisabledForegroundColor();
			}
			if (ColorHelper.getGrayValue(fc) > 128) {
				g2D.setColor(Color.black);
			} else {
				g2D.setColor(Color.white);
			}
			JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title,
					mnemIndex, textRect.x + 1,
					textRect.y + 1 + metrics.getAscent());
			g2D.setComposite(composite);
			g2D.setColor(fc);
			JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title,
					mnemIndex, textRect.x, textRect.y + metrics.getAscent());
		}
	}
}