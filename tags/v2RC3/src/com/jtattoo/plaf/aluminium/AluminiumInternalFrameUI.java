/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseInternalFrameUI;

/**
 * @author Michael Hagen
 */
public class AluminiumInternalFrameUI extends BaseInternalFrameUI {

	public static ComponentUI createUI(final JComponent c) {
		return new AluminiumInternalFrameUI((JInternalFrame) c);
	}

	public AluminiumInternalFrameUI(final JInternalFrame b) {
		super(b);
	}

	@Override
	protected JComponent createNorthPane(final JInternalFrame w) {
		titlePane = new AluminiumInternalFrameTitlePane(w);
		return titlePane;
	}
}
