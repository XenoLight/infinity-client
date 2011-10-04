/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BasePanelUI;

/**
 * @author Michael Hagen
 */
public class AluminiumPanelUI extends BasePanelUI {

	private static AluminiumPanelUI panelUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (panelUI == null) {
			panelUI = new AluminiumPanelUI();
		}
		return panelUI;
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		if (c.getBackground() instanceof ColorUIResource) {
			if (c.isOpaque()) {
				AluminiumUtils.fillComponent(g, c);
			}
		} else {
			super.update(g, c);
		}
	}
}
