/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;

/**
 * author Michael Hagen
 */
public class BaseTreeUI extends BasicTreeUI {

	public static ComponentUI createUI(final JComponent c) {
		return new BaseTreeUI();
	}

	@Override
	protected void paintHorizontalLine(final Graphics g, final JComponent c, final int y,
			final int left, final int right) {
		drawDashedHorizontalLine(g, y, left, right);
	}

	@Override
	protected void paintVerticalLine(final Graphics g, final JComponent c, final int x, final int top,
			final int bottom) {
		drawDashedVerticalLine(g, x, top, bottom);
	}
}
