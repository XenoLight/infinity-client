/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * @author Michael Hagen
 */
public class BasePanelUI extends BasicPanelUI {

	private static BasePanelUI panelUI = null;

	public static ComponentUI createUI(final JComponent c) {
		if (panelUI == null) {
			panelUI = new BasePanelUI();
		}
		return panelUI;
	}

	@Override
	protected void installDefaults(final JPanel p) {
		super.installDefaults(p);
		p.setFont(AbstractLookAndFeel.getTheme().getControlTextFont());
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		final Graphics2D g2D = (Graphics2D) g;
		Object savedRenderingHint = null;
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			savedRenderingHint = g2D
			.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
		}
		super.paint(g, c);
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					savedRenderingHint);
		}
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		if (c.isOpaque()) {
			g.setColor(c.getBackground());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}
	}
}
