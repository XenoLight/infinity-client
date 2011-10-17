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
 * Esta clase implementa las listas.
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class NimRODListUI extends BasicListUI {
	public static ComponentUI createUI(final JComponent list) {
		return new NimRODListUI(list);
	}

	public NimRODListUI(final JComponent list) {
		super();
	}

	@Override
	protected void paintCell(final Graphics g, final int row, final Rectangle rowBounds,
			final ListCellRenderer cellRenderer, final ListModel dataModel,
			final ListSelectionModel selModel, final int leadIndex) {

		rowBounds.x += 1;
		super.paintCell(g, row, rowBounds, cellRenderer, dataModel, selModel,
				leadIndex);
		rowBounds.x -= 1;

		if (list.isSelectedIndex(row)) {
			final Color oldColor = g.getColor();

			g.translate(rowBounds.x, rowBounds.y);

			final GradientPaint grad = new GradientPaint(0, 0,
					NimRODUtils.getBrillo(), 0, rowBounds.height,
					NimRODUtils.getSombra());
			final Color bgColor = MetalLookAndFeel.getMenuSelectedBackground();

			final Graphics2D g2D = (Graphics2D) g;
			g2D.setPaint(grad);
			g2D.fillRect(0, 0, rowBounds.width - 1, rowBounds.height);

			g.setColor(bgColor.darker());
			g.drawRect(0, 0, rowBounds.width - 1, rowBounds.height - 1);

			g.translate(-rowBounds.x, -rowBounds.y);
			g.setColor(oldColor);
		}
	}
}
