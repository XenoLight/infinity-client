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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;

/**
 * 
 * @author Nilo J. Gonzalez 2007
 */
public class NimRODTextAreaUI extends BasicTextAreaUI {
	class MiTextML extends MouseAdapter implements FocusListener {
		@Override
		public void focusGained(final FocusEvent e) {
			focus = true;
			refreshBorder();
		}

		@Override
		public void focusLost(final FocusEvent e) {
			focus = false;
			refreshBorder();
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			rollover = true;
			refreshBorder();
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			rollover = false;
			refreshBorder();
		}

		protected void refreshBorder() {
			if (getComponent().getParent() != null) {
				final Component papi = getComponent();

				papi.getParent().repaint(papi.getX() - 5, papi.getY() - 5,
						papi.getWidth() + 10, papi.getHeight() + 10);
			}
		}
	}
	public static ComponentUI createUI(final JComponent c) {
		return new NimRODTextAreaUI(c);
	}
	private boolean rollover = false;

	private boolean focus = false;

	private MiTextML miTextML;

	public NimRODTextAreaUI(final JComponent c) {
		super();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		miTextML = new MiTextML();
		getComponent().addMouseListener(miTextML);
		getComponent().addFocusListener(miTextML);
	}

	public boolean isFocus() {
		return focus;
	}

	public boolean isRollover() {
		return rollover;
	}

	@Override
	protected void paintBackground(final Graphics g) {
		final JTextComponent c = getComponent();

		final Border bb = c.getBorder();

		if (bb != null && bb instanceof NimRODBorders.NimRODGenBorder) {
			g.setColor(getComponent().getBackground());

			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g.fillRoundRect(2, 2, c.getWidth() - 4, c.getHeight() - 4, 7, 7);

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_DEFAULT);

			if (c.isEnabled() && c.isEditable()) {
				if (focus) {
					NimRODUtils.paintFocus(g, 1, 1, c.getWidth() - 2,
							c.getHeight() - 2, 2, 2,
							MetalLookAndFeel.getFocusColor());
				} else if (rollover) {
					NimRODUtils.paintFocus(
							g,
							1,
							1,
							c.getWidth() - 2,
							c.getHeight() - 2,
							2,
							2,
							NimRODUtils.getColorAlfa(
									MetalLookAndFeel.getFocusColor(), 150));
				}
			}
		} else {
			super.paintBackground(g);
		}

	}

	// ////////////////////////

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		getComponent().removeMouseListener(miTextML);
		getComponent().removeFocusListener(miTextML);
	}
}
