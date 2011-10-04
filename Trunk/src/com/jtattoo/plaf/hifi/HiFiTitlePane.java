/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JRootPane;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseRootPaneUI;
import com.jtattoo.plaf.BaseTitlePane;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class HiFiTitlePane extends BaseTitlePane {

	private static Color fgColor = new Color(244, 244, 244);

	public HiFiTitlePane(final JRootPane root, final BaseRootPaneUI ui) {
		super(root, ui);
	}

	@Override
	public void paintText(final Graphics g, final int x, final int y, final String title) {
		g.setColor(Color.black);
		JTattooUtilities.drawString(rootPane, g, title, x + 1, y + 1);
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
		} else {
			g.setColor(AbstractLookAndFeel
					.getWindowInactiveTitleForegroundColor());
		}
		JTattooUtilities.drawString(rootPane, g, title, x, y);
	}
}
