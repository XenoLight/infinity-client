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
 */

package com.nilo.plaf.nimrod;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class NimRODDesktopIconUI extends BasicDesktopIconUI {
	// ******************************+
	private class HackML extends MouseInputAdapter {
		void dodo(final MouseEvent ev) {
			if (desktopIcon != null) {
				desktopIcon.getDesktopPane().updateUI();
			}
		}

		@Override
		public void mouseDragged(final MouseEvent ev) {
			dodo(ev);
		}

		@Override
		public void mouseEntered(final MouseEvent ev) {
			hasFocus = true;
			dodo(ev);
		}

		@Override
		public void mouseExited(final MouseEvent ev) {
			hasFocus = false;
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

	public static ComponentUI createUI(final JComponent c) {
		return new NimRODDesktopIconUI();
	}
	boolean hasFocus;

	private final int width = UIManager.getInt("NimRODDesktopIcon.width");
	private final int height = UIManager.getInt("NimRODDesktopIcon.height");

	private final int bigWidth = UIManager.getInt("NimRODDesktopIconBig.width");
	private final int bigHeight = UIManager.getInt("NimRODDesktopIconBig.height");

	private final HackML hackML;

	private Icon resizeIcon, antIcon;

	public NimRODDesktopIconUI() {
		super();

		hackML = new HackML();
	}

	@Override
	public Dimension getMaximumSize(final JComponent c) {
		return getMinimumSize(c);
	}

	@Override
	public Dimension getMinimumSize(final JComponent c) {
		return new Dimension(width, height);
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		return getMinimumSize(c);
	}

	protected String getTitle(final String title, final FontMetrics fm, final int len) {
		if (title == null || title.equals("")) {
			return "";
		}

		final int lTit = fm.stringWidth(title);
		if (lTit <= len) {
			return title;
		}

		int lPuntos = fm.stringWidth("...");
		if (len - lPuntos <= 0) {
			return "";
		}

		int i = 1;
		do {
			final String aux = title.substring(0, i++) + "...";
			lPuntos = fm.stringWidth(aux);
		} while (lPuntos < len);

		return title.substring(0, i - 1) + "...";
	}

	@Override
	protected void installComponents() {
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.uninstallBorder(desktopIcon);
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		if (frame != null) {
			desktopIcon.addMouseListener(hackML);
			desktopIcon.addMouseMotionListener(hackML);
		}
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		if (frame.getFrameIcon() != antIcon) {
			antIcon = frame.getFrameIcon();
			resizeIcon = NimRODUtils.reescala(antIcon, bigWidth, bigHeight);
		}
		String title = frame.getTitle();

		int x = 0;
		if (resizeIcon != null) {
			x = (width - resizeIcon.getIconWidth()) / 2;
			resizeIcon.paintIcon(c, g, x, 2);
		}

		g.setFont(UIManager.getFont("DesktopIcon.font"));
		final FontMetrics fm = g.getFontMetrics();

		if (hasFocus) {
			int y = 0;
			String auxTit = getTitle(title, fm, width - 10); // Los anglos se
			// mearan de
			// risa al ver
			// el nombre de
			// esta
			// variable...
			while (auxTit.length() > 0) {
				if (auxTit.endsWith("...")) {
					auxTit = auxTit.substring(0, auxTit.length() - 3);
				}

				final Rectangle2D rect = fm.getStringBounds(auxTit, g);
				x = (int) (width - rect.getWidth()) / 2;
				y += rect.getHeight();

				NimRODUtils.paintShadowTitleFat(g, auxTit, x, y, Color.white);

				title = title.substring(auxTit.length());
				auxTit = getTitle(title, fm, width - 10);
			}
		} else {
			title = getTitle(title, fm, width - 10);
			final Rectangle2D rect = fm.getStringBounds(title, g);
			x = (int) (width - rect.getWidth()) / 2;
			NimRODUtils.paintShadowTitleFat(g, title, x, height
					- NimRODUtils.MATRIX_FAT, Color.white);
		}
	}

	@Override
	protected void uninstallComponents() {
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		desktopIcon.removeMouseListener(hackML);
		desktopIcon.removeMouseMotionListener(hackML);
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		paint(g, c);
	}
}
