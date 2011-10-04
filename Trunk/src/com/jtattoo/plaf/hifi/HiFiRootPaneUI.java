/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseRootPaneUI;
import com.jtattoo.plaf.BaseTitlePane;

/**
 * @author Michael Hagen
 */
public class HiFiRootPaneUI extends BaseRootPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiRootPaneUI();
	}

	@Override
	public BaseTitlePane createTitlePane(final JRootPane root) {
		return new HiFiTitlePane(root, this);
	}
}
