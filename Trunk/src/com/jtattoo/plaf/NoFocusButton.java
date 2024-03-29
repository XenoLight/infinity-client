/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Michael Hagen
 */
public class NoFocusButton extends JButton {

	public NoFocusButton() {
		super();
		init();
	}

	public NoFocusButton(final Icon ico) {
		super(ico);
		init();
	}

	private void init() {
		setFocusPainted(false);
		setRolloverEnabled(true);
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			setFocusable(false);
		}
	}

	@Override
	public boolean isFocusTraversable() {
		return false;
	}

	@Override
	public void requestFocus() {
	}
}
