/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import com.jtattoo.plaf.BaseSplitPaneUI;

/**
 * @author Michael Hagen
 */
public class AluminiumSplitPaneUI extends BaseSplitPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new AluminiumSplitPaneUI();
	}

	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		return new AluminiumSplitPaneDivider(this);
	}
}
