/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Graphics;

import javax.swing.JRootPane;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseRootPaneUI;
import com.jtattoo.plaf.BaseTitlePane;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AluminiumTitlePane extends BaseTitlePane {

	public AluminiumTitlePane(final JRootPane root, final BaseRootPaneUI ui) {
		super(root, ui);
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
			JTattooUtilities.drawString(rootPane, g, title, x + 1, y + 1);
			g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
			JTattooUtilities.drawString(rootPane, g, title, x, y);
		} else {
			g.setColor(AbstractLookAndFeel.getWindowTitleBackgroundColor());
			JTattooUtilities.drawString(rootPane, g, title, x + 1, y + 1);
			g.setColor(AbstractLookAndFeel
					.getWindowInactiveTitleForegroundColor());
			JTattooUtilities.drawString(rootPane, g, title, x, y);
		}
	}
}
