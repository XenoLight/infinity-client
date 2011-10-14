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
 * Esta clase implementa las entradas de menu.
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class NimRODMenuUI extends BasicMenuUI {
	public static ComponentUI createUI(final JComponent x) {
		return new NimRODMenuUI();
	}

	@Override
	protected void paintBackground(final Graphics g, final JMenuItem menuItem, final Color bgColor) {
		NimRODUtils.pintaBarraMenu(g, menuItem, bgColor);
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		final JMenu menu = (JMenu) c;
		if (menu.isTopLevelMenu()) {
			menu.setOpaque(false);

			final ButtonModel model = menu.getModel();
			if (model.isArmed() || model.isSelected()) {
				g.setColor(MetalLookAndFeel.getFocusColor());
				g.fillRoundRect(1, 1, c.getWidth() - 2, c.getHeight() - 3, 2, 2);
			}
		} else {
			menuItem.setBorderPainted(false);
			menuItem.setOpaque(false);
		}

		super.update(g, c);
	}
}
