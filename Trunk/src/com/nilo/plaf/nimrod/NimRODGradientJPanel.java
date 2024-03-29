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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Solo para hacer pruebas...
 * 
 * @author Nilo J. Gonzalez 2007
 */
class NimRODGradientJPanel extends JPanel implements SwingConstants {
	private static final long serialVersionUID = 3064942006323344159L;

	protected int direction;
	protected Color colIni, colFin;

	public NimRODGradientJPanel() {
		super();

		init();
	}

	public NimRODGradientJPanel(final boolean isDoubleBuffered) {
		super(isDoubleBuffered);

		init();
	}

	public NimRODGradientJPanel(final LayoutManager layout) {
		super(layout);

		init();
	}

	public NimRODGradientJPanel(final LayoutManager layout, final boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);

		init();
	}

	protected void init() {
		direction = VERTICAL;
		colIni = MetalLookAndFeel.getControl();
		colFin = colIni.darker();
	}

	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D g2D = (Graphics2D) g;
		GradientPaint grad = null;

		if (direction == HORIZONTAL) {
			grad = new GradientPaint(0, 0, colIni, getWidth(), 0, colFin);
		} else {
			grad = new GradientPaint(0, 0, colIni, 0, getHeight(), colFin);
		}

		g2D.setPaint(grad);
		g2D.fillRect(0, 0, getWidth(), getHeight());
	}

	public void setGradientColors(final Color ini, final Color fin) {
		colIni = ini;
		colFin = fin;
	}

	public void setGradientDirection(final int dir) {
		direction = dir;
	}
}
