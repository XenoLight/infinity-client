/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;

/**
 * @author Michael Hagen
 */
public class BaseScrollPaneUI extends BasicScrollPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new BaseScrollPaneUI();
	}

	@Override
	public void installDefaults(final JScrollPane p) {
		super.installDefaults(p);
		p.setFont(AbstractLookAndFeel.getTheme().getControlTextFont());
		p.setBackground(AbstractLookAndFeel.getTheme().getBackgroundColor());
		p.getViewport().setBackground(
				AbstractLookAndFeel.getTheme().getBackgroundColor());
	}
}
