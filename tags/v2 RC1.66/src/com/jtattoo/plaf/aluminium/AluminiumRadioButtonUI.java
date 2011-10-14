/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseRadioButtonUI;

/**
 * @author Michael Hagen
 */
public class AluminiumRadioButtonUI extends BaseRadioButtonUI {

	private static AluminiumRadioButtonUI radioButtonUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (radioButtonUI == null) {
			radioButtonUI = new AluminiumRadioButtonUI();
		}
		return radioButtonUI;
	}

	@Override
	public void paintBackground(final Graphics g, final JComponent c) {
		if (c.isOpaque()) {
			if ((c.getBackground().equals(AbstractLookAndFeel
					.getBackgroundColor()))
					&& (c.getBackground() instanceof ColorUIResource)) {
				AluminiumUtils.fillComponent(g, c);
			} else {
				g.setColor(c.getBackground());
				g.fillRect(0, 0, c.getWidth(), c.getHeight());
			}
		}
	}
}
