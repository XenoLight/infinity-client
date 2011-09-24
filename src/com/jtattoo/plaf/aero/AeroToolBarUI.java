/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractToolBarUI;
import com.jtattoo.plaf.BaseBorders;

/**
 * @author Michael Hagen
 */
public class AeroToolBarUI extends AbstractToolBarUI {
	public static ComponentUI createUI(final JComponent c) {
		return new AeroToolBarUI();
	}

	@Override
	public Border getNonRolloverBorder() {
		return BaseBorders.getToolButtonBorder();
	}

	@Override
	public Border getRolloverBorder() {
		return AeroBorders.getRolloverToolButtonBorder();
	}

	@Override
	public boolean isButtonOpaque() {
		return false;
	}

}
