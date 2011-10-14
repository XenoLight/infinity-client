/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BasePanelUI;

/**
 * @author Michael Hagen
 */
public class HiFiPanelUI extends BasePanelUI {

	private static HiFiPanelUI panelUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (panelUI == null) {
			panelUI = new HiFiPanelUI();
		}
		return panelUI;
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		if (c.getBackground() instanceof ColorUIResource) {
			if (c.isOpaque()) {
				HiFiUtils.fillComponent(g, c);
			}
		} else {
			super.update(g, c);
		}
	}
}
