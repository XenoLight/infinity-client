/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

/**
 * @author Michael Hagen
 */
public class BaseMenuBarUI extends BasicMenuBarUI {

	public static ComponentUI createUI(final JComponent c) {
		return new BaseMenuBarUI();
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		final int w = c.getWidth();
		final int h = c.getHeight();
		JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme()
				.getMenuBarColors(), 0, 0, w, h);
	}
}