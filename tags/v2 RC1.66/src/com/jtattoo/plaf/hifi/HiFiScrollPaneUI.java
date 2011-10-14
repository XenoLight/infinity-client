/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseScrollPaneUI;

/**
 * @author Michael Hagen
 */
public class HiFiScrollPaneUI extends BaseScrollPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiScrollPaneUI();
	}

	@Override
	public void installDefaults(final JScrollPane p) {
		super.installDefaults(p);
		p.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
	}
}
