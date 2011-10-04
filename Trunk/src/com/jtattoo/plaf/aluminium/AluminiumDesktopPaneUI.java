/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

/**
 * @author Michael Hagen
 */
public class AluminiumDesktopPaneUI extends BasicDesktopPaneUI {

	private static AluminiumDesktopPaneUI desktopPaneUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (desktopPaneUI == null) {
			desktopPaneUI = new AluminiumDesktopPaneUI();
		}
		return desktopPaneUI;
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		AluminiumUtils.fillComponent(g, c);
	}
}
