/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractToolBarUI;
import com.jtattoo.plaf.BaseBorders;

/**
 * @author Michael Hagen
 */
public class HiFiToolBarUI extends AbstractToolBarUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiToolBarUI();
	}

	@Override
	public Border getNonRolloverBorder() {
		return BaseBorders.getToolButtonBorder();
	}

	@Override
	public Border getRolloverBorder() {
		return HiFiBorders.getRolloverToolButtonBorder();
	}

	@Override
	public boolean isButtonOpaque() {
		return true;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		HiFiUtils.fillComponent(g, c);
	}
}
