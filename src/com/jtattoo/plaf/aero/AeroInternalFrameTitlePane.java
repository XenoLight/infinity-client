/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JInternalFrame;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseInternalFrameTitlePane;
import com.jtattoo.plaf.BaseTitleButton;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AeroInternalFrameTitlePane extends BaseInternalFrameTitlePane {

	// ------------------------------------------------------------------------------
	private class TitleButton extends BaseTitleButton {

		public TitleButton(final Action action, final String accessibleName, final Icon icon) {
			super(action, accessibleName, icon, 1.0f);
		}

		@Override
		public void paint(final Graphics g) {
			final boolean isPressed = getModel().isPressed();
			final boolean isArmed = getModel().isArmed();
			final boolean isRollover = getModel().isRollover();
			final int width = getWidth();
			final int height = getHeight();
			Color colors[] = AbstractLookAndFeel.getTheme().getButtonColors();
			if (isRollover)
				colors = AbstractLookAndFeel.getTheme().getRolloverColors();
			if (isPressed && isArmed)
				colors = AbstractLookAndFeel.getTheme().getPressedColors();
			JTattooUtilities.fillHorGradient(g, colors, 0, 0, width, height);
			g.setColor(Color.lightGray);
			g.drawLine(0, 0, 0, height);
			g.drawLine(0, height - 1, width, height - 1);
			g.setColor(Color.white);
			g.drawLine(1, 0, 1, height);
			getIcon().paintIcon(this, g, 1, 0);
		}
	}

	public AeroInternalFrameTitlePane(final JInternalFrame f) {
		super(f);
	}

	@Override
	protected void createButtons() {
		iconButton = new TitleButton(iconifyAction, ICONIFY, iconIcon);
		maxButton = new TitleButton(maximizeAction, MAXIMIZE, maxIcon);
		closeButton = new TitleButton(closeAction, CLOSE, closeIcon);
		setButtonIcons();
	}

	@Override
	protected int getHorSpacing() {
		return 0;
	}

	@Override
	protected int getVerSpacing() {
		return 0;
	}

	@Override
	public void paintBorder(final Graphics g) {
		if (isActive())
			g.setColor(ColorHelper.brighter(
					AbstractLookAndFeel.getWindowTitleColorDark(), 50));
		else
			g.setColor(ColorHelper.darker(
					AbstractLookAndFeel.getWindowInactiveTitleColorDark(), 10));
		g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

	@Override
	public void paintText(final Graphics g, final int x, final int y, final String title) {
		if (isActive()) {
			final Color titleColor = AbstractLookAndFeel
			.getWindowTitleForegroundColor();
			if (ColorHelper.getGrayValue(titleColor) > 164)
				g.setColor(Color.black);
			else
				g.setColor(Color.white);
			JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
			g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
			JTattooUtilities.drawString(frame, g, title, x, y);
		} else {
			g.setColor(AbstractLookAndFeel
					.getWindowInactiveTitleForegroundColor());
			JTattooUtilities.drawString(frame, g, title, x, y);
		}
	}

}
