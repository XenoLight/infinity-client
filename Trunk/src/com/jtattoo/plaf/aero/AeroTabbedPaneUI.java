/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseTabbedPaneUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AeroTabbedPaneUI extends BaseTabbedPaneUI {
	public static ComponentUI createUI(final JComponent c) {
		return new AeroTabbedPaneUI();
	}

	private Color sepColors[] = null;

	@Override
	protected Color[] getContentBorderColors(final int tabPlacement) {
		if (sepColors == null) {
			sepColors = new Color[5];
			sepColors[0] = ColorHelper.brighter(
					AbstractLookAndFeel.getControlColorDark(), 40);
			sepColors[1] = ColorHelper.brighter(
					AbstractLookAndFeel.getControlColorLight(), 40);
			sepColors[2] = ColorHelper.brighter(
					AbstractLookAndFeel.getControlColorLight(), 60);
			sepColors[3] = ColorHelper.brighter(
					AbstractLookAndFeel.getControlColorLight(), 20);
			sepColors[4] = ColorHelper.brighter(
					AbstractLookAndFeel.getControlColorDark(), 30);
		}
		return sepColors;
	}

	@Override
	protected Font getTabFont(final boolean isSelected) {
		if (isSelected)
			return super.getTabFont(isSelected).deriveFont(Font.BOLD);
		else
			return super.getTabFont(isSelected);
	}

	@Override
	protected void installComponents() {
		simpleButtonBorder = true;
		super.installComponents();
	}

	@Override
	protected void paintContentBorder(final Graphics g, final int tabPlacement,
			final int selectedIndex, final int x, final int y, final int w, final int h) {
		g.setColor(AbstractLookAndFeel.getTabAreaBackgroundColor());
		final int tabAreaHeight = calculateTabAreaHeight(tabPlacement, runCount,
				maxTabHeight);
		final int tabAreaWidth = calculateTabAreaWidth(tabPlacement, runCount,
				maxTabWidth);
		if (tabPlacement == SwingConstants.TOP
				|| tabPlacement == SwingConstants.LEFT) {
			g.fillRect(x, y, tabAreaWidth, tabAreaHeight);
		} else if (tabPlacement == SwingConstants.BOTTOM) {
			g.fillRect(x, h - tabAreaHeight + 1, w, tabAreaHeight);
		} else {
			g.fillRect(w - tabAreaWidth + 1, y, tabAreaWidth, h);
		}
		super.paintContentBorder(g, tabPlacement, selectedIndex, x, y, w, h);
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
			if (JTattooUtilities.getJavaVersion() >= 1.4)
				mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);

			if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
				if (isSelected) {
					final Color titleColor = AbstractLookAndFeel
					.getWindowTitleForegroundColor();
					if (ColorHelper.getGrayValue(titleColor) > 164)
						g.setColor(Color.black);
					else
						g.setColor(Color.white);
					JTattooUtilities.drawStringUnderlineCharAt(tabPane, g,
							title, mnemIndex, textRect.x + 1, textRect.y + 1
							+ metrics.getAscent());
					g.setColor(titleColor);
				} else {
					g.setColor(tabPane.getForegroundAt(tabIndex));
				}
				JTattooUtilities
				.drawStringUnderlineCharAt(tabPane, g, title,
						mnemIndex, textRect.x,
						textRect.y + metrics.getAscent());

			} else { // tab disabled
				g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
				JTattooUtilities
				.drawStringUnderlineCharAt(tabPane, g, title,
						mnemIndex, textRect.x,
						textRect.y + metrics.getAscent());
				g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
				JTattooUtilities.drawStringUnderlineCharAt(tabPane, g, title,
						mnemIndex, textRect.x - 1,
						textRect.y + metrics.getAscent() - 1);
			}
		}
	}

}