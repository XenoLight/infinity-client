/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class HiFiCheckBoxUI extends HiFiRadioButtonUI {

	private static HiFiCheckBoxUI checkBoxUI = null;

	public static ComponentUI createUI(final JComponent b) {
		if (checkBoxUI == null) {
			checkBoxUI = new HiFiCheckBoxUI();
		}
		return checkBoxUI;
	}

	@Override
	public void installDefaults(final AbstractButton b) {
		super.installDefaults(b);
		icon = UIManager.getIcon("CheckBox.icon");
	}
}
