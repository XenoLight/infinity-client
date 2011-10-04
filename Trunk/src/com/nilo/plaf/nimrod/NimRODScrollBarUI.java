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
 * Esta clase implementa las barras de scroll.
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;

public class NimRODScrollBarUI extends MetalScrollBarUI {
	public class MiML extends MetalScrollBarUI.TrackListener {
		NimRODScrollBarUI papi;

		public MiML(final NimRODScrollBarUI papi) {
			this.papi = papi;
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			super.mouseDragged(e);

			if (papi.rollOver && !thumbRect.contains(e.getX(), e.getY())) {
				rollOver = false;
				scrollbar.repaint();
			} else if (!papi.rollOver && thumbRect.contains(e.getX(), e.getY())) {
				papi.rollOver = true;
				scrollbar.repaint();
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			super.mouseEntered(e);

			papi.rollOver = true;
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			super.mouseExited(e);

			papi.rollOver = false;
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			super.mouseMoved(e);

			if (papi.rollOver && !thumbRect.contains(e.getX(), e.getY())) {
				rollOver = false;
				scrollbar.repaint();
			} else if (!papi.rollOver && thumbRect.contains(e.getX(), e.getY())) {
				papi.rollOver = true;
				scrollbar.repaint();
			}
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);

			papi.clicked = true;
			scrollbar.repaint();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			super.mouseReleased(e);

			papi.clicked = false;
			scrollbar.repaint();
		}
	}
	public static ComponentUI createUI(final JComponent c) {
		return new NimRODScrollBarUI();
	}

	private boolean clicked;

	private boolean rollOver;

	@Override
	protected JButton createDecreaseButton(final int orientation) {
		decreaseButton = new NimRODScrollButton(orientation, scrollBarWidth,
				isFreeStanding);
		return decreaseButton;
	}

	@Override
	protected JButton createIncreaseButton(final int orientation) {
		increaseButton = new NimRODScrollButton(orientation, scrollBarWidth,
				isFreeStanding);
		return increaseButton;
	}

	@Override
	protected TrackListener createTrackListener() {
		return new MiML(this);
	}

	@Override
	protected void paintThumb(final Graphics g, final JComponent c, final Rectangle thumbBounds) {
		final Color thumbColor = UIManager.getColor("ScrollBar.thumb");
		final Color thumbShadow = UIManager.getColor("ScrollBar.thumbShadow");

		g.translate(thumbBounds.x, thumbBounds.y);

		g.setColor(thumbColor);
		g.fillRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 1);

		g.setColor((rollOver ? thumbShadow.darker() : thumbShadow));
		g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 1);

		Icon icDecor = null;
		if (scrollbar.getOrientation() == Adjustable.HORIZONTAL) {
			icDecor = UIManager.getIcon("ScrollBar.horizontalThumbIconImage");
		} else {
			icDecor = UIManager.getIcon("ScrollBar.verticalThumbIconImage");
		}

		final int w = icDecor.getIconWidth();
		final int h = icDecor.getIconHeight();
		final int x = (thumbBounds.width - w) / 2;
		final int y = (thumbBounds.height - h) / 2;

		if (((scrollbar.getOrientation() == Adjustable.HORIZONTAL) && (thumbBounds.width >= w))
				|| ((scrollbar.getOrientation() == Adjustable.VERTICAL) && (thumbBounds.height >= h))) {
			icDecor.paintIcon(c, g, x, y);
		}

		g.translate(-thumbBounds.x, -thumbBounds.y);

		final Graphics2D g2D = (Graphics2D) g;
		GradientPaint grad = null;

		Color colA, colB;
		if (clicked) {
			colA = NimRODUtils.getSombra();
			colB = NimRODUtils.getBrillo();
		} else {
			colA = NimRODUtils.getBrillo();
			colB = NimRODUtils.getSombra();
		}

		if (scrollbar.getOrientation() == Adjustable.HORIZONTAL) {
			grad = new GradientPaint(thumbBounds.x, thumbBounds.y, colA,
					thumbBounds.x, thumbBounds.height, colB);
		} else {
			grad = new GradientPaint(thumbBounds.x, thumbBounds.y, colA,
					thumbBounds.width, thumbBounds.y, colB);
			/*
			 * ImageIcon icSombra = (ImageIcon)UIManager.getIcon(
			 * "BordeGenSup"); g.drawImage( icSombra.getImage(),
			 * thumbBounds.x,thumbBounds.y+thumbBounds.height,
			 * thumbBounds.width, icSombra.getIconHeight(), null);
			 */
		}

		g2D.setPaint(grad);
		g2D.fill(thumbBounds);
	}

	// ///////////////////////////////////

	@Override
	protected void paintTrack(final Graphics g, final JComponent c, final Rectangle trackBounds) {
		final Graphics2D g2D = (Graphics2D) g;
		GradientPaint grad = null;

		if (scrollbar.getOrientation() == Adjustable.HORIZONTAL) {
			grad = new GradientPaint(trackBounds.x, trackBounds.y,
					NimRODUtils.getSombra(), trackBounds.x, trackBounds.y
					+ trackBounds.height, NimRODUtils.getBrillo());
		} else {
			grad = new GradientPaint(trackBounds.x, trackBounds.y,
					NimRODUtils.getSombra(), trackBounds.x + trackBounds.width,
					trackBounds.y, NimRODUtils.getBrillo());
		}

		g2D.setPaint(grad);
		g2D.fill(trackBounds);
	}
}
