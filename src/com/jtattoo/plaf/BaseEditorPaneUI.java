/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;

/**
 * @author Michael Hagen
 */
public class BaseEditorPaneUI extends BasicEditorPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new BaseEditorPaneUI();
	}

	@Override
	public void installDefaults() {
		super.installDefaults();
		updateBackground();
	}

	private void updateBackground() {
		final JTextComponent c = getComponent();
		if (c.getBackground() instanceof UIResource) {
			if (!c.isEnabled() || !c.isEditable()) {
				c.setBackground(AbstractLookAndFeel
						.getDisabledBackgroundColor());
			} else {
				c.setBackground(AbstractLookAndFeel.getInputBackgroundColor());
			}
		}
	}
}
