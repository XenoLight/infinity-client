/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.Popup;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;

/**
 * @author Michael Hagen
 */
public class BasePopupMenuUI extends BasicPopupMenuUI {

	private static class MyPopupMenuListener implements PopupMenuListener {

		private BasePopupMenuUI popupMenuUI = null;

		public MyPopupMenuListener(final BasePopupMenuUI aPopupMenuUI) {
			popupMenuUI = aPopupMenuUI;
		}

		@Override
		public void popupMenuCanceled(final PopupMenuEvent e) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
			if (popupMenuUI.screenImage != null) {
				final JPopupMenu popup = (JPopupMenu) e.getSource();
				final JRootPane root = popup.getRootPane();
				final Point ptPopup = popup.getLocationOnScreen();
				final Point ptRoot = root.getLocationOnScreen();
				final Graphics g = popup.getRootPane().getGraphics();
				g.drawImage(popupMenuUI.screenImage, ptPopup.x - ptRoot.x,
						ptPopup.y - ptRoot.y, null);
				popupMenuUI.resetScreenImage();
			}
		}

		@Override
		public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
		}
	}
	private static Robot robot = null;
	public static ComponentUI createUI(final JComponent c) {
		return new BasePopupMenuUI();
	}

	private BufferedImage screenImage = null;

	private MyPopupMenuListener myPopupListener = null;

	@Override
	public Popup getPopup(final JPopupMenu popupMenu, final int x, final int y) {
		if (!isMenuOpaque()) {
			try {
				final Dimension size = popupMenu.getPreferredSize();
				final Rectangle screenRect = new Rectangle(x, y, size.width,
						size.height);
				screenImage = getRobot().createScreenCapture(screenRect);
			} catch (final Exception ex) {
				screenImage = null;
			}
		}
		return super.getPopup(popupMenu, x, y);
	}

	private Robot getRobot() {
		if (robot == null) {
			try {
				robot = new Robot();
			} catch (final Exception ex) {
			}
		}
		return robot;
	}

	@Override
	public void installListeners() {
		super.installListeners();
		if (!isMenuOpaque()) {
			myPopupListener = new MyPopupMenuListener(this);
			popupMenu.addPopupMenuListener(myPopupListener);
		}
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
	}

	private boolean isMenuOpaque() {
		return (AbstractLookAndFeel.getTheme().isMenuOpaque() || (getRobot() == null));
	}

	private void resetScreenImage() {
		screenImage = null;
	}

	@Override
	public void uninstallListeners() {
		if (!isMenuOpaque()) {
			popupMenu.removePopupMenuListener(myPopupListener);
		}
		super.uninstallListeners();
	}

	@Override
	public void uninstallUI(final JComponent c) {
		super.uninstallUI(c);
		c.setOpaque(true);
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		if (screenImage != null) {
			g.drawImage(screenImage, 0, 0, null);
		} else {
			g.setColor(Color.white);
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
		}
	}
}
