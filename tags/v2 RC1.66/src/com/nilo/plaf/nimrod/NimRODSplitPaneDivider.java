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
 * Esta clase implementa los divisores de paneles.
 * ���UN PUTO INFIERNO!!!
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class NimRODSplitPaneDivider extends BasicSplitPaneDivider {
	protected class Boton extends JButton {
		private static final long serialVersionUID = 1L;

		public static final int IZQ = 0;
		public static final int DER = 1;

		private final JSplitPane splitPane;
		private final int ots, dir;

		public Boton(final int dir, final JSplitPane sp, final int ots) {
			this.dir = dir;
			splitPane = sp;
			this.ots = ots;

			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			setFocusPainted(false);
			setBorderPainted(false);
			setRequestFocusEnabled(false);
			setOpaque(false);
		}

		@Override
		public boolean isFocusable() {
			return false;
		}

		@Override
		public void paint(final Graphics g) {
			if (splitPane != null) {
				final int blocksize = Math.min(getDividerSize(), ots);

				g.setColor(MetalLookAndFeel.getFocusColor());

				int[] xs = new int[3];
				int[] ys = new int[3];

				if (orientation == JSplitPane.VERTICAL_SPLIT && dir == DER) {
					xs = new int[] { 0, blocksize / 2, blocksize };
					ys = new int[] { 0, blocksize, 0 };
				} else if (orientation == JSplitPane.VERTICAL_SPLIT
						&& dir == IZQ) {
					xs = new int[] { 0, blocksize / 2, blocksize };
					ys = new int[] { blocksize, 0, blocksize };
				} else if (orientation == JSplitPane.HORIZONTAL_SPLIT
						&& dir == DER) {
					xs = new int[] { 0, 0, blocksize };
					ys = new int[] { 0, blocksize, blocksize / 2 };
				} else if (orientation == JSplitPane.HORIZONTAL_SPLIT
						&& dir == IZQ) {
					// g.drawRect( blocksize-1,0, 2,blocksize);
					xs = new int[] { 0, blocksize, blocksize };
					ys = new int[] { blocksize / 2, 0, blocksize };
				}

				g.fillPolygon(xs, ys, 3);
				g.setColor(MetalLookAndFeel.getFocusColor().darker());
				g.drawPolygon(xs, ys, 3);
			}
		}

		@Override
		public void setBorder(final Border border) {
		}
	}

	private static final long serialVersionUID = 1L;

	public NimRODSplitPaneDivider(final BasicSplitPaneUI p) {
		super(p);
	}

	@Override
	protected JButton createLeftOneTouchButton() {
		final JButton b = new Boton(Boton.IZQ, super.splitPane,
				BasicSplitPaneDivider.ONE_TOUCH_SIZE);
		final Boolean boo = ((Boolean) UIManager
				.get("SplitPane.oneTouchButtonsOpaque"));
		if (boo != null) {
			b.setOpaque(boo.booleanValue());
		}

		return b;
	}

	@Override
	protected JButton createRightOneTouchButton() {
		final JButton b = new Boton(Boton.DER, super.splitPane,
				BasicSplitPaneDivider.ONE_TOUCH_SIZE);
		final Boolean boo = ((Boolean) UIManager
				.get("SplitPane.oneTouchButtonsOpaque"));
		if (boo != null) {
			b.setOpaque(boo.booleanValue());
		}

		return b;
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);

		final Graphics2D g2D = (Graphics2D) g;
		GradientPaint grad = null;
		if (super.splitPane.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
			grad = new GradientPaint(0, 0, NimRODUtils.getBrillo(), 0,
					getHeight(), NimRODUtils.getSombra());
		} else {
			grad = new GradientPaint(0, 0, NimRODUtils.getBrillo(), getWidth(),
					0, NimRODUtils.getSombra());
		}

		g2D.setPaint(grad);
		g2D.fillRect(0, 0, getWidth(), getHeight());
	}
}
