/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseScrollPaneUI;

/**
 * @author Michael Hagen
 */
public class AluminiumScrollPaneUI extends BaseScrollPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new AluminiumScrollPaneUI();
	}

	@Override
	public void installDefaults(final JScrollPane p) {
		super.installDefaults(p);
		p.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
	}
}
