/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JInternalFrame;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseInternalFrameTitlePane;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiInternalFrameTitlePane extends BaseInternalFrameTitlePane {

	public HiFiInternalFrameTitlePane(final JInternalFrame f) {
		super(f);
	}

	@Override
	public void paintBorder(final Graphics g) {
	}

	@Override
	public void paintText(final Graphics g, final int x, final int y, final String title) {
		g.setColor(Color.black);
		JTattooUtilities.drawString(frame, g, title, x + 1, y + 1);
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
		} else {
			g.setColor(AbstractLookAndFeel
					.getWindowInactiveTitleForegroundColor());
		}
		JTattooUtilities.drawString(frame, g, title, x, y);
	}
}
