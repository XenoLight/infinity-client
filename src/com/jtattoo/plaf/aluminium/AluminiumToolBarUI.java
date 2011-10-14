/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractToolBarUI;
import com.jtattoo.plaf.BaseBorders;

/**
 * @author Michael Hagen
 */
public class AluminiumToolBarUI extends AbstractToolBarUI {

	public static ComponentUI createUI(final JComponent c) {
		return new AluminiumToolBarUI();
	}

	@Override
	public Border getNonRolloverBorder() {
		return BaseBorders.getToolButtonBorder();
	}

	@Override
	public Border getRolloverBorder() {
		return AluminiumBorders.getRolloverToolButtonBorder();
	}

	@Override
	public boolean isButtonOpaque() {
		return false;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		AluminiumUtils.fillComponent(g, c);
	}
}
