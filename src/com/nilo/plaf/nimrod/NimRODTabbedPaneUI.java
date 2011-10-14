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
 * Esta clase implementa los JTabbedPane.
 * Practicamente todo el esfuerzo se centra en pintar la pesta�a.
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class NimRODTabbedPaneUI extends BasicTabbedPaneUI {
	public class MiML extends MouseAdapter implements MouseMotionListener {
		@Override
		public void mouseDragged(final MouseEvent e) {
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			rollover = -1;
			tabPane.repaint();
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			rollover = tabForCoordinate(tabPane, e.getX(), e.getY());

			// Esto es para limitar el numero de veces que se redibuja el panel
			// Un boton se puede pintar muchas veces porque es peque�o y gasta
			// poco, pero esto es un panel que puede tener cienes y cienes de
			// controles,
			// asi que cada vez que se repinta gasta lo suyo
			if ((rollover == -1) && (antRollover == rollover)) {
				return;
			}

			tabPane.repaint();
			antRollover = rollover;
		}
	}
	public static ComponentUI createUI(final JComponent c) {
		return new NimRODTabbedPaneUI();
	}
	private Color selectColor;
	private final int inclTab = 12;
	// private int anchoFocoV = inclTab;
	private final int anchoFocoH = 0;
	private final int anchoCarpetas = 18;

	private int rollover = -1;
	private int antRollover = -1;

	private MiML miml;

	/**
	 * En este poligono se guarda la forma de la pesta�a. Es muy importante.
	 */
	private Polygon shape;

	/**
	 * Este metodo devuelve un tama�o mas grande de lo necesario, haciendo el
	 * hueco para la decoracion.
	 */
	@Override
	protected int calculateTabHeight(final int tabPlacement, final int tabIndex,
			final int fontHeight) {
		if (tabPlacement == LEFT || tabPlacement == RIGHT) {
			return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight);
		} else {
			return anchoFocoH
			+ super.calculateTabHeight(tabPlacement, tabIndex,
					fontHeight);
		}
	}

	/**
	 * Este metodo devuelve un tama�o mas grande de lo necesario, haciendoer
	 * hueco para la decoracion.
	 */
	@Override
	protected int calculateTabWidth(final int tabPlacement, final int tabIndex,
			final FontMetrics metrics) {
		return 8 + inclTab
		+ super.calculateTabWidth(tabPlacement, tabIndex, metrics);
	}

	/**
	 * Esta funcion devuelve una sombra mas opaca cuanto mas arriba este la
	 * fila. A partir de valores de fila superiores a 7 siempre devuelve el
	 * mismo color
	 * 
	 * @param fila
	 *            int la fila a pintar
	 */
	protected Color hazAlfa(final int fila) {
		int alfa = 0;
		if (fila >= 0) {
			alfa = 50 + (fila > 7 ? 70 : 8 * fila);
		}

		return new Color(0, 0, 0, alfa);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		rollover = -1;
		selectColor = MetalLookAndFeel.getFocusColor();
		tabAreaInsets.right = anchoCarpetas;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		miml = new MiML();
		tabPane.addMouseMotionListener(miml);
		tabPane.addMouseListener(miml);
	}

	@Override
	protected void layoutLabel(final int tabPlacement, final FontMetrics metrics,
			final int tabIndex, final String title, final Icon icon, final Rectangle tabRect,
			final Rectangle iconRect, final Rectangle textRect, final boolean isSelected) {
		final Rectangle tabRectPeq = new Rectangle(tabRect);
		tabRectPeq.width -= inclTab;
		super.layoutLabel(tabPlacement, metrics, tabIndex, title, icon,
				tabRectPeq, iconRect, textRect, isSelected);
	}

	/**
	 * Este metodo dibuja una se�al amarilla en la solapa que tiene el foco
	 */
	@Override
	protected void paintFocusIndicator(final Graphics g, final int tabPlacement,
			final Rectangle[] rects, final int tabIndex, final Rectangle iconRect,
			final Rectangle textRect, final boolean isSelected) {
		if (tabPane.hasFocus() && isSelected) {
			final Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			final Stroke oldStroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(2.0f));
			g2d.setColor(UIManager.getColor("ScrollBar.thumbShadow"));
			g2d.drawPolygon(shape);
			g2d.setStroke(oldStroke);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		}
	}

	@Override
	protected void paintTabArea(final Graphics g, final int tabPlacement, final int selectedIndex) {
		if (runCount > 1) {
			final int lines[] = new int[runCount];
			for (int i = 0; i < runCount; i++) {
				lines[i] = rects[tabRuns[i]].y
				+ (tabPlacement == TOP ? maxTabHeight : 0);
			}

			Arrays.sort(lines);

			final Graphics2D g2D = (Graphics2D) g;
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			if (tabPlacement == TOP) {
				int fila = runCount;
				for (int i = 0; i < lines.length - 1; i++, fila--) {
					final Polygon carp = new Polygon();
					carp.addPoint(0, lines[i]);
					carp.addPoint(tabPane.getWidth() - 2 * fila - 2, lines[i]);
					carp.addPoint(tabPane.getWidth() - 2 * fila, lines[i] + 3);

					if (i < lines.length - 2) {
						carp.addPoint(tabPane.getWidth() - 2 * fila,
								lines[i + 1]);
						carp.addPoint(0, lines[i + 1]);
					} else {
						carp.addPoint(tabPane.getWidth() - 2 * fila, lines[i]
						                                                   + rects[selectedIndex].height);
						carp.addPoint(0, lines[i] + rects[selectedIndex].height);
					}

					carp.addPoint(0, lines[i]);

					g2D.setColor(hazAlfa(fila));
					g2D.fillPolygon(carp);

					g2D.setColor(darkShadow.darker());
					g2D.drawPolygon(carp);
				}
			} else {
				int fila = 0;
				for (int i = 0; i < lines.length - 1; i++, fila++) {
					final Polygon carp = new Polygon();
					carp.addPoint(0, lines[i]);
					carp.addPoint(tabPane.getWidth() - 2 * fila - 1, lines[i]);

					carp.addPoint(tabPane.getWidth() - 2 * fila - 1,
							lines[i + 1] - 3);
					carp.addPoint(tabPane.getWidth() - 2 * fila - 3,
							lines[i + 1]);
					carp.addPoint(0, lines[i + 1]);

					carp.addPoint(0, lines[i]);

					g2D.setColor(hazAlfa(fila + 2));
					g2D.fillPolygon(carp);

					g2D.setColor(darkShadow.darker());
					g2D.drawPolygon(carp);
				}
			}

			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		}

		super.paintTabArea(g, tabPlacement, selectedIndex);
	}

	@Override
	protected void paintTabBackground(final Graphics g, final int tabPlacement,
			final int tabIndex, final int x, final int y, final int w, final int h, final boolean isSelected) {
		// Este es el primer metodo al que se llama, asi que aqui preparamos el
		// shape que dibujara despues todo...
		final Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		GradientPaint gradientShadow;

		int xp[] = null; // Para la forma
		int yp[] = null;
		switch (tabPlacement) {
		case LEFT:
			xp = new int[] { x, x, x + w, x + w, x };
			yp = new int[] { y, y + h - 3, y + h - 3, y, y };
			gradientShadow = new GradientPaint(x, y, NimRODUtils.getBrillo(),
					x, y + h, NimRODUtils.getSombra());
			break;
		case RIGHT:

			xp = new int[] { x, x, x + w - 2, x + w - 2, x };
			yp = new int[] { y, y + h - 3, y + h - 3, y, y };
			gradientShadow = new GradientPaint(x, y, NimRODUtils.getBrillo(),
					x, y + h, NimRODUtils.getSombra());
			break;
		case BOTTOM:
			xp = new int[] { x, x, x + 3, x + w - inclTab - 6,
					x + w - inclTab - 2, x + w - inclTab, x + w - 3, x };
			yp = new int[] { y, y + h - 3, y + h, y + h, y + h - 1, y + h - 3,
					y, y };
			gradientShadow = new GradientPaint(x, y, NimRODUtils.getBrillo(),
					x, y + h, NimRODUtils.getSombra());
			break;
		case TOP:
		default:
			xp = new int[] { x, x, x + 3, x + w - inclTab - 6,
					x + w - inclTab - 2, x + w - inclTab, x + w, x };
			yp = new int[] { y + h, y + 3, y, y, y + 1, y + 3, y + h, y + h };
			gradientShadow = new GradientPaint(x, y, NimRODUtils.getBrillo(),
					x, y + h, NimRODUtils.getSombra());
			break;
		}
		;

		shape = new Polygon(xp, yp, xp.length);

		// Despues ponemos el color que toque
		if (isSelected) {
			g2D.setColor(selectColor);
		} else {
			g2D.setColor(tabPane.getBackgroundAt(tabIndex));
		}

		// Encima, pintamos la pesta�a con el color que sea
		g2D.fill(shape);

		// Encima, pintamos la pesta�a con el color que le corresponde por
		// profundidad
		if (runCount > 1) {
			g2D.setColor(hazAlfa(getRunForTab(tabPane.getTabCount(), tabIndex) - 1));
			g2D.fill(shape);
		}

		// Encima, pintamos un colorin si el raton esta por encima
		if (tabIndex == rollover) {
			g2D.setColor(NimRODUtils.getRolloverColor());
			g2D.fill(shape);
		}

		// Y despues, le damos un sombreado que hace que parezca curbada (�A
		// que duele ver algunas faltas de ortografia?)
		g2D.setPaint(gradientShadow);
		g2D.fill(shape);

		// Y al final le pintamos un bordecito para definir mejor la pesta�a
		g2D.setColor(NimRODUtils.getSombra());
		g2D.draw(shape);

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_DEFAULT);
	}

	/**
	 * Este metodo dibuja el borde.
	 */
	@Override
	protected void paintTabBorder(final Graphics g, final int tabPlacement, final int tabIndex,
			final int x, final int y, final int w, final int h, final boolean isSelected) {
	}

	// ///////////////////////////////////

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		tabPane.removeMouseMotionListener(miml);
		tabPane.removeMouseListener(miml);
	}

}