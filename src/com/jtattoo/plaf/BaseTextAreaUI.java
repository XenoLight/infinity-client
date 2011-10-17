/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

/**
 * @author Michael Hagen
 */
public class BaseTextAreaUI extends BasicTextAreaUI {

	public static ComponentUI createUI(final JComponent c) {
		return new BaseTextAreaUI();
	}

	@Override
	public void installDefaults() {
		super.installDefaults();
		updateBackground();
	}

	@Override
	protected void paintSafely(final Graphics g) {
		final Graphics2D g2D = (Graphics2D) g;
		Object savedRenderingHint = null;
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			savedRenderingHint = g2D
			.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
		}
		super.paintSafely(g);
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					savedRenderingHint);
		}
	}

	@Override
	protected void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("editable")
				|| evt.getPropertyName().equals("enabled")) {
			updateBackground();
		}
		super.propertyChange(evt);
	}

	private void updateBackground() {
		final JTextComponent c = getComponent();
		if (c.getBackground() instanceof UIResource) {
			if (!c.isEnabled() || !c.isEditable()) {
				c.setBackground(AbstractLookAndFeel
						.getDisabledBackgroundColor());
			} else {
				c.setBackground(AbstractLookAndFeel.getInputBackgroundColor());
			}
		}
	}
}
