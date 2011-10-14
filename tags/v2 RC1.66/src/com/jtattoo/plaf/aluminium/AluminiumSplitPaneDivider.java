/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.jtattoo.plaf.BaseSplitPaneDivider;

/**
 * @author Michael Hagen
 */
public class AluminiumSplitPaneDivider extends BaseSplitPaneDivider {

	public AluminiumSplitPaneDivider(final AluminiumSplitPaneUI ui) {
		super(ui);
	}

	@Override
	public void paint(final Graphics g) {
		AluminiumUtils.fillComponent(g, this);
		final Graphics2D g2D = (Graphics2D) g;
		final Composite composite = g2D.getComposite();
		final AlphaComposite alpha = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.3f);
		g2D.setComposite(alpha);
		super.paint(g);
		g2D.setComposite(composite);
	}
}
