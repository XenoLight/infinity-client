/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Graphics;

import javax.swing.JInternalFrame;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseInternalFrameTitlePane;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AluminiumInternalFrameTitlePane extends BaseInternalFrameTitlePane {

	public AluminiumInternalFrameTitlePane(final JInternalFrame f) {
		super(f);
	}

	@Override
	public void paintBorder(final Graphics g) {
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getTheme().getWindowBorderColor());
		} else {
			g.setColor(AbstractLookAndFeel.getTheme()
					.getWindowInactiveBorderColor());
		}
		g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

	@Override
	public void paintText(final Graphics g, final int x, final int y, final String title) {
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
			JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
			g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
			JTattooUtilities.drawString(frame, g, title, x, y);
		} else {
			g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
			JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
			g.setColor(AbstractLookAndFeel
					.getWindowInactiveTitleForegroundColor());
			JTattooUtilities.drawString(frame, g, title, x, y);
		}
	}
}
