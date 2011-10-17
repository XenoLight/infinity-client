/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.BaseComboBoxUI;
import com.jtattoo.plaf.NoFocusButton;

/**
 * @author Michael Hagen
 */
public class HiFiComboBoxUI extends BaseComboBoxUI {

	// --------------------------------------------------------------------------------------------------
	static class ArrowButtonBorder extends AbstractBorder {

		private static final Insets insets = new Insets(1, 3, 1, 2);
		private static final Color frameLoColor = new Color(120, 120, 120);
		private static final Color frameLowerColor = new Color(96, 96, 96);
		private static final Color frameLowerLoColor = new Color(64, 64, 64);
		private static final Color frameLowestColor = new Color(32, 32, 32);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final Graphics2D g2D = (Graphics2D) g;
			g.translate(x, y);

			g.setColor(frameLoColor);
			g.drawLine(1, 0, w - 1, 0);
			g.drawLine(1, 1, 1, h - 2);
			g.setColor(frameLowerColor);
			g.drawLine(w - 1, 1, w - 1, h - 2);
			g.drawLine(2, h - 1, w - 2, h - 1);

			final Composite composite = g2D.getComposite();
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.5f);
			g2D.setComposite(alpha);
			g.setColor(frameLowestColor);
			g.drawLine(2, 1, w - 2, 1);
			g.drawLine(2, 2, 2, h - 3);
			g.setColor(frameLowerLoColor);
			g.drawLine(0, 0, 0, h);
			g2D.setComposite(composite);

			g.translate(-x, -y);
		}
	}

	public static ComponentUI createUI(final JComponent c) {
		return new HiFiComboBoxUI();
	}

	@Override
	public JButton createArrowButton() {
		final JButton button = new NoFocusButton(HiFiIcons.getComboBoxIcon());
		button.setBorder(new ArrowButtonBorder());
		return button;
	}

	@Override
	protected void setButtonBorder() {
	}
}
