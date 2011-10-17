/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class BaseCheckBoxUI extends BaseRadioButtonUI {

	private static BaseCheckBoxUI checkBoxUI = null;

	public static ComponentUI createUI(final JComponent b) {
		if (checkBoxUI == null) {
			checkBoxUI = new BaseCheckBoxUI();
		}
		return checkBoxUI;
	}

	@Override
	public void installDefaults(final AbstractButton b) {
		super.installDefaults(b);
		icon = UIManager.getIcon("CheckBox.icon");
	}
}
