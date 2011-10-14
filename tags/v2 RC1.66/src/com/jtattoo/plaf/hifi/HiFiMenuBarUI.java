/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

import com.jtattoo.plaf.BaseBorders;

/**
 * @author Michael Hagen
 */
public class HiFiMenuBarUI extends BasicMenuBarUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiMenuBarUI();
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		if ((c != null) && (c instanceof JMenuBar)) {
			((JMenuBar) c).setBorder(BaseBorders.getMenuBarBorder());
		}
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		HiFiUtils.fillComponent(g, c);
	}
}