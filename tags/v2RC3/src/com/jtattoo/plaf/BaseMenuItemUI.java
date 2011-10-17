/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/**
 * @author Michael Hagen
 */
public class BaseMenuItemUI extends BasicMenuItemUI {

	public static ComponentUI createUI(final JComponent c) {
		return new BaseMenuItemUI();
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
	}

	protected void paintBackground(final Graphics g, final JComponent c, final int x, final int y,
			final int w, final int h) {
		final JMenuItem b = (JMenuItem) c;
		final ButtonModel model = b.getModel();
		if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
			g.setColor(AbstractLookAndFeel.getMenuSelectionBackgroundColor());
			g.fillRect(x, y, w, h);
		} else if (!AbstractLookAndFeel.getTheme().isMenuOpaque()) {
			final Graphics2D g2D = (Graphics2D) g;
			final Composite composite = g2D.getComposite();
			final AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, AbstractLookAndFeel.getTheme()
					.getMenuAlpha());
			g2D.setComposite(alpha);
			g.setColor(AbstractLookAndFeel.getMenuBackgroundColor());
			g.fillRect(x, y, w, h);
			g2D.setComposite(composite);
		} else {
			g.setColor(AbstractLookAndFeel.getMenuBackgroundColor());
			g.fillRect(x, y, w, h);
		}
		if (menuItem.isSelected() && menuItem.isArmed()) {
			g.setColor(AbstractLookAndFeel.getMenuSelectionForegroundColor());
		} else {
			g.setColor(AbstractLookAndFeel.getMenuForegroundColor());
		}
	}

	@Override
	protected void paintBackground(final Graphics g, final JMenuItem menuItem, final Color bgColor) {
		if (menuItem.isOpaque()) {
			final int w = menuItem.getWidth();
			final int h = menuItem.getHeight();
			paintBackground(g, menuItem, 0, 0, w, h);
		}
	}

	@Override
	protected void paintText(final Graphics g, final JMenuItem menuItem,
			final Rectangle textRect, final String text) {
		final Graphics2D g2D = (Graphics2D) g;
		Object savedRenderingHint = null;
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			savedRenderingHint = g2D
			.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
		}
		if (menuItem.isSelected() && menuItem.isArmed()) {
			g.setColor(AbstractLookAndFeel.getMenuSelectionForegroundColor());
		} else {
			g.setColor(AbstractLookAndFeel.getMenuForegroundColor());
		}
		super.paintText(g, menuItem, textRect, text);
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					savedRenderingHint);
		}
	}

	@Override
	public void uninstallUI(final JComponent c) {
		c.setOpaque(true);
		super.uninstallUI(c);
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		paintBackground(g, c, 0, 0, c.getWidth(), c.getHeight());
		paint(g, c);
	}
}
