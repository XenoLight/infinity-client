/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Action;
import javax.swing.Icon;

/**
 * @author Michael Hagen
 */
public class BaseTitleButton extends NoFocusButton {

	private float alpha = 1.0f;

	public BaseTitleButton(final Action action, final String accessibleName, final Icon icon,
			final float alpha) {
		setContentAreaFilled(false);
		setBorderPainted(false);
		setAction(action);
		setText(null);
		setIcon(icon);
		putClientProperty("paintActive", Boolean.TRUE);
		getAccessibleContext().setAccessibleName(accessibleName);
		this.alpha = Math.max(0.2f, alpha);
	}

	@Override
	public void paint(final Graphics g) {
		if (JTattooUtilities.isActive(this) || (alpha >= 1.0)) {
			super.paint(g);
		} else {
			final Graphics2D g2D = (Graphics2D) g;
			final Composite composite = g2D.getComposite();
			final AlphaComposite alphaComposite = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, alpha);
			g2D.setComposite(alphaComposite);
			super.paint(g);
			g2D.setComposite(composite);
		}
	}
}
