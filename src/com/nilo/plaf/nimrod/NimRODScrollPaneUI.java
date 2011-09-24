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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

/**
 * 
 * @author Nilo J. Gonzalez 2007
 * @author Mark Senne
 */
public class NimRODScrollPaneUI extends BasicScrollPaneUI {

	public static ComponentUI createUI(final JComponent x) {
		return new NimRODScrollPaneUI();
	}

	protected boolean oldOpaque;

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);

		// Esto es gracias a Mark Senne
		final JScrollPane sp = (JScrollPane) c;
		if (sp.getHorizontalScrollBar() != null) {
			sp.getHorizontalScrollBar().putClientProperty(
					MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
		}
		if (sp.getVerticalScrollBar() != null) {
			sp.getVerticalScrollBar().putClientProperty(
					MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
		}

		oldOpaque = sp.isOpaque();
		sp.setOpaque(false);

		// Si el componente alojado tiene borde, hay que quitarselo para que no
		// quede
		// feo.
		final Component cc = sp.getViewport().getView();
		if (cc != null) {
			try {
				final JComponent ccc = (JComponent) cc;

				// Esto esta aqui para solucionar un bug descubierto por Michael
				// Flor
				if (ccc.getBorder() != null
						&& ccc.getBorder() instanceof NimRODBorders.NimRODGenBorder) {
					ccc.setBorder(null);
				}
			} catch (final Exception ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		final JScrollPane sp = (JScrollPane) c;

		// Esto es para solucionar un bug encontrado por Fabian Voith que
		// consiste en que cuando se cambia el
		// tama�o del JScrollPane se reevalua el tama�o necesario, lo que
		// puede provocar un relayaut si ha cambiado
		// el tam�o de lo que contiene, por ejemplo, si se despliegan unos
		// nodos en un arbol...
		// Para solucionarlo se guarda el tama�o del scrollpane al empezar a
		// pintar y se pone al terminar.
		// Gracias a Fabian Voith.
		final Dimension dim = sp.getSize();

		if (sp.getViewportBorder() != null) {
			// Parece que el popup de los combos contiene un scrollpane que
			// contiene un panel,
			// asi que hay que asegurarse de que si papi es un combo no se pinta
			// el borde
			// para que el combo no quede "raro"
			Component cc = c.getParent();
			while (cc != null) {
				if (cc.toString().startsWith(
				"javax.swing.plaf.basic.BasicComboPopup")) {
					sp.setViewportBorder(null);
					break;
				}
				cc = cc.getParent();
			}

			// Ahora vamos a ver si el componente alojado dentro del scrollpane
			// tiene un
			// borde y se lo quitamos. Esto tambien se hace en installUI, pero
			// hay que
			// repetirlo en cada repintado porque si se cambia el tema no se
			// vuelve a
			// llamar el metodo principal.
			cc = sp.getViewport().getView();
			if (cc != null) {
				try {
					final JComponent ccc = (JComponent) cc;

					final Border bb = sp.getViewportBorder();
					if (bb != null
							&& bb instanceof NimRODBorders.NimRODGenBorder) {
						final int x = bb.getBorderInsets(sp).left
						+ bb.getBorderInsets(sp).right - 1;

						g.setColor(ccc.getBackground());

						final Graphics2D g2d = (Graphics2D) g;
						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);

						g.fillRoundRect(0, 0, x + cc.getWidth(), c.getHeight(),
								7, 7);

						g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_DEFAULT);

						// Aqui se vuelve a poner el tama�o que tenia al
						// principio, por el bug ese descubierto por Fabian
						// Voith.
						sp.setPreferredSize(dim);
					}

					// Esto esta aqui para solucionar un bug descubierto por
					// Michael Flor
					if (ccc.getBorder() != null
							&& ccc.getBorder() instanceof NimRODBorders.NimRODGenBorder) {
						ccc.setBorder(null);
					}
				} catch (final Exception ex) { /* si no se puede, pues no se puede... */
				}
			}
		}

		super.paint(g, c);
	}

	@Override
	public void uninstallUI(final JComponent c) {
		super.uninstallUI(c);

		c.setOpaque(oldOpaque);
	}
}
