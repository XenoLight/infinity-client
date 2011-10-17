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

/**
 * Esta clase implementa los TextField.
 * Esta clase se usa desde un monton de sitios (Combos, PasswordField...), asi que extenderla
 * tiene resultados mas alla de los campos de texto.
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;

public class NimRODTextFieldUI extends BasicTextFieldUI {
	class MiTextML extends MouseAdapter implements FocusListener {
		@Override
		public void focusGained(final FocusEvent e) {
			focus = true;
			refresh();
		}

		@Override
		public void focusLost(final FocusEvent e) {
			focus = false;
			refresh();
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			rollover = true;
			refresh();
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			rollover = false;
			refresh();
		}

		protected void refresh() {
			if (getComponent().getParent() != null) {
				final Component papi = getComponent();

				papi.getParent().repaint(papi.getX() - 5, papi.getY() - 5,
						papi.getWidth() + 10, papi.getHeight() + 10);
			}
		}
	}
	public static ComponentUI createUI(final JComponent c) {
		return new NimRODTextFieldUI(c);
	}
	private boolean rollover = false;

	private boolean focus = false;

	private MiTextML miTextML;

	protected boolean oldOpaque, canijo;

	NimRODTextFieldUI(final JComponent c) {
		super();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		oldOpaque = getComponent().isOpaque();
		getComponent().setOpaque(false);
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
	}

	protected void paintFocus(final Graphics g) {
		final JTextComponent c = getComponent();

		if (c.isEnabled() && c.isEditable() && !canijo) {
			if (focus) {
				NimRODUtils.paintFocus(g, 1, 1, c.getWidth() - 2,
						c.getHeight() - 2, 4, 4, 3,
						MetalLookAndFeel.getFocusColor());
			} else if (rollover) {
				NimRODUtils.paintFocus(
						g,
						1,
						1,
						c.getWidth() - 2,
						c.getHeight() - 2,
						4,
						4,
						3,
						NimRODUtils.getColorAlfa(
								MetalLookAndFeel.getFocusColor(), 150));
			}
		}
	}

	@Override
	protected void paintSafely(final Graphics g) {
		paintFocus(g);

		paintTodo(g);

		super.paintSafely(g);
	}

	protected void paintTodo(final Graphics g) {
		final JTextComponent c = getComponent();

		final Border bb = c.getBorder();

		if (bb != null && bb instanceof NimRODBorders.NimRODGenBorder) {
			final Insets ins = NimRODBorders.getTextFieldBorder().getBorderInsets(c);

			// Si cabe todo, le ponemos un borde guay. Si no, pues le dejamos un
			// borde cutrecillo
			if (c.getSize().height + 2 < (c.getFont().getSize() + ins.top + ins.bottom)) {
				c.setBorder(NimRODBorders.getThinGenBorder());
				canijo = true;
			} else {
				c.setBorder(NimRODBorders.getTextFieldBorder());
				canijo = false;
			}

			if (!c.isEditable() || !c.isEnabled()) {
				g.setColor(UIManager.getColor("TextField.inactiveBackground"));
			} else {
				g.setColor(c.getBackground());
			}

			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			g.fillRoundRect(2, 2, c.getWidth() - 4, c.getHeight() - 4, 7, 7);

			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		} else {
			super.paintBackground(g);
		}
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		getComponent().setOpaque(oldOpaque);
	}

	// ////////////////////////

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		getComponent().removeMouseListener(miTextML);
		getComponent().removeFocusListener(miTextML);
	}
}
