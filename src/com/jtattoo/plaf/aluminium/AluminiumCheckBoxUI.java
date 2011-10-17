/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class AluminiumCheckBoxUI extends AluminiumRadioButtonUI {

	private static AluminiumCheckBoxUI checkBoxUI = null;

	public static ComponentUI createUI(final JComponent b) {
		if (checkBoxUI == null) {
			checkBoxUI = new AluminiumCheckBoxUI();
		}
		return checkBoxUI;
	}

	@Override
	public void installDefaults(final AbstractButton b) {
		super.installDefaults(b);
		icon = UIManager.getIcon("CheckBox.icon");
	}
}
