/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseSliderUI;

/**
 * @author Michael Hagen
 */
public class HiFiSliderUI extends BaseSliderUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiSliderUI((JSlider) c);
	}

	public HiFiSliderUI(final JSlider slider) {
		super(slider);
	}

	@Override
	public void paintBackground(final Graphics g, final JComponent c) {
		if (c.isOpaque()) {
			final Component parent = c.getParent();
			if ((parent != null)
					&& (parent.getBackground() instanceof ColorUIResource)) {
				HiFiUtils.fillComponent(g, c);
			} else {
				if (parent != null) {
					g.setColor(parent.getBackground());
				} else {
					g.setColor(c.getBackground());
				}
				g.fillRect(0, 0, c.getWidth(), c.getHeight());
			}
		}
	}
}
