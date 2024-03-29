/*
 *                 (C) Copyright 2005 Nilo J. Gonzalez
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 2 of the Licence, or (at your opinion) any
 * later version.
 * 
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 *
 * Original author: Nilo J. Gonzalez
 *
 * And Matt G improved the code.
 */

/**
 * Esta clase implementa la barra de titulo de los internalframes
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.metal.MetalInternalFrameTitlePane;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class NimRODInternalFrameTitlePane extends MetalInternalFrameTitlePane {
	private class HackML extends MouseInputAdapter {
		@Override
		public void mouseEntered(final MouseEvent ev) {
			if (frame.getDesktopPane() != null) {
				frame.getDesktopPane().updateUI();
			}
		}

		@Override
		public void mouseExited(final MouseEvent ev) {
			if (frame.getDesktopPane() != null) {
				frame.getDesktopPane().updateUI();
			}
		}

		@Override
		public void mouseReleased(final MouseEvent ev) {
			if (frame.getDesktopPane() != null) {
				frame.getDesktopPane().updateUI();
			}
		}
	}

	private class MiML extends MouseInputAdapter {
		Insets ins = frame.getBorder().getBorderInsets(frame);

		void dodo(final MouseEvent ev) {
			if (ev.getComponent() instanceof NimRODInternalFrameTitlePane) {
				if (frame.getDesktopPane() != null) {
					frame.getDesktopPane().updateUI();
				}
			} else {
				final int x = ev.getX();
				final int w = frame.getWidth();
				final int y = ev.getY();
				final int h = frame.getHeight();

				if ((x <= 5) || (x >= w - ins.right) || (y >= h - ins.bottom)) {
					if (frame.getDesktopPane() != null) {
						frame.getDesktopPane().updateUI();
					}
				}
			}
		}

		@Override
		public void mouseDragged(final MouseEvent ev) {
			dodo(ev);
		}

		@Override
		public void mousePressed(final MouseEvent ev) {
			dodo(ev);
		}

		@Override
		public void mouseReleased(final MouseEvent ev) {
			dodo(ev);
		}
	}
	class NimRODPropertyChangeHandler extends
	BasicInternalFrameTitlePane.PropertyChangeHandler {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final String prop = evt.getPropertyName();

			if (prop.equals(JInternalFrame.IS_SELECTED_PROPERTY)) {
				final Boolean b = (Boolean) evt.getNewValue();
				iconButton.putClientProperty("paintActive", b);
				closeButton.putClientProperty("paintActive", b);
				maxButton.putClientProperty("paintActive", b);
			} else if ("JInternalFrame.messageType".equals(prop)) {
				frame.repaint();
			} else if ("icon".equals(prop)) {
				iconButton.getModel().setRollover(false);
				closeButton.getModel().setRollover(false);
				maxButton.getModel().setRollover(false);

				((NimRODDesktopIconUI) (frame.getDesktopIcon().getUI())).hasFocus = false;
			} else if ("frameIcon".equals(prop)) {
				if (frame.getDesktopPane() != null) {
					frame.getDesktopPane().updateUI();
				}
			}

			super.propertyChange(evt);
		}
	}

	private static final long serialVersionUID = 3569530640592496710L;
	private MiML miml;

	private Icon resizeIcon, antIcon;

	private final int litWidth = UIManager.getInt("NimRODInternalFrameIconLit.width");

	private final int litHeight = UIManager
	.getInt("NimRODInternalFrameIconLit.height");

	public NimRODInternalFrameTitlePane(final JInternalFrame arg0) {
		super(arg0);

		closeButton.setOpaque(false);
		closeButton.setBorderPainted(false);
		closeButton.setFocusPainted(false);

		maxButton.setOpaque(false);
		maxButton.setBorderPainted(false);
		maxButton.setFocusPainted(false);

		iconButton.setOpaque(false);
		iconButton.setBorderPainted(false);
		iconButton.setFocusPainted(false);

		final HackML hackML = new HackML();
		closeButton.addMouseListener(hackML);
		maxButton.addMouseListener(hackML);
		iconButton.addMouseListener(hackML);
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new NimRODPropertyChangeHandler();
	}

	// ****************************************************

	@Override
	public void installListeners() {
		super.installListeners();

		miml = new MiML();
		addMouseListener(miml);
		addMouseMotionListener(miml);

		frame.addMouseListener(miml);
		frame.addMouseMotionListener(miml);
	}

	@Override
	public void paintComponent(final Graphics g) {
		final int width = getWidth();
		final int height = getHeight();

		final Graphics2D g2D = (Graphics2D) g.create();

		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				NimRODUtils.getFrameOpacityFloat()));

		// Elegimos el color de la barra
		GradientPaint grad;
		if (frame.isSelected()) {
			grad = new GradientPaint(0, 0,
					MetalLookAndFeel.getPrimaryControlDarkShadow(), width, 0,
					MetalLookAndFeel.getPrimaryControl());
		} else {
			grad = new GradientPaint(0, 0, MetalLookAndFeel.getControl(),
					width, 0, MetalLookAndFeel.getControlDarkShadow());
		}
		g2D.setPaint(grad);
		g2D.fillRect(0, 1, width, height);

		g2D.dispose();

		// Esto basicamente esta copiado de la clase original
		int xOffset = frame.getComponentOrientation().isLeftToRight() ? 5
				: width - 5;

		if (frame.getFrameIcon() != antIcon) {
			final int alto = (litHeight > height ? height - 2 : litHeight);
			final int ancho = (litHeight > height ? alto : litWidth);

			antIcon = frame.getFrameIcon();
			resizeIcon = NimRODUtils.reescala(antIcon, alto, ancho);
		}

		if (resizeIcon != null) {
			final int iconY = ((height / 2) - (resizeIcon.getIconHeight() / 2));
			resizeIcon.paintIcon(frame, g, xOffset, iconY);
			xOffset += 5 + resizeIcon.getIconWidth();
		}

		String title = frame.getTitle();
		if (title != null) {
			final Font f = getFont();
			g.setFont(f);
			final FontMetrics fm = getFontMetrics(f);
			final int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent();

			int len = width;
			if (frame.isIconifiable()) {
				len = iconButton.getBounds().x;
			} else if (frame.isMaximizable()) {
				len = maxButton.getBounds().x;
			} else if (frame.isClosable()) {
				len = closeButton.getBounds().x;
			}

			len = len - xOffset - getInsets().left;

			title = getTitle(title, fm, len);

			if (frame.isSelected()) {
				NimRODUtils.paintShadowTitleFat(g, title, xOffset, yOffset,
						Color.white);
			} else {
				NimRODUtils.paintShadowTitleFat(g, title, xOffset, yOffset,
						MetalLookAndFeel.getControl());
			}
		}
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		removeMouseListener(miml);
		removeMouseMotionListener(miml);

		frame.removeMouseListener(miml);
		frame.removeMouseMotionListener(miml);

		miml = null;
	}

}
