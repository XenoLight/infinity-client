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
 * Esta clase implementa los campos de password.
 * Esta clase cambia los asteriscos habituales por unos cuadrados con bordes redondeados
 * @see NimRODPasswordFieldUI
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.JPasswordField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PasswordView;
import javax.swing.text.Position;

public class NimRODPasswordView extends PasswordView {
	protected static int ancho = 9;
	protected static int hueco = 3;

	public NimRODPasswordView(final Element elem) {
		super(elem);
	}

	@Override
	protected int drawEchoCharacter(final Graphics g, final int x, final int y, final char c) {
		int w = getFontMetrics().charWidth(c);
		w = (w < ancho ? ancho : w);
		final int h = (getContainer().getHeight() - ancho) / 2;

		final Graphics2D g2D = (Graphics2D) g;
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2D.fillOval(x, h + 1, w, w);

		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_DEFAULT);

		return x + w + hueco;
	}

	@Override
	public Shape modelToView(final int pos, final Shape a, final Position.Bias b)
	throws BadLocationException {
		final Container c = getContainer();
		if (c instanceof JPasswordField) {
			final JPasswordField f = (JPasswordField) c;
			if (!f.echoCharIsSet()) {
				return super.modelToView(pos, a, b);
			}

			final char echoChar = f.getEchoChar();
			int w = f.getFontMetrics(f.getFont()).charWidth(echoChar);
			w = (w < ancho ? ancho : w) + hueco;

			final Rectangle alloc = adjustAllocation(a).getBounds();
			final int dx = (pos - getStartOffset()) * w;
			alloc.x += dx - 2;
			if (alloc.x <= 5) {
				alloc.x = 6;
			}
			alloc.width = 1;

			return alloc;
		}

		return null;
	}

	@Override
	public int viewToModel(final float fx, final float fy, Shape a, final Position.Bias[] bias) {
		bias[0] = Position.Bias.Forward;
		int n = 0;
		final Container c = getContainer();
		if (c instanceof JPasswordField) {
			final JPasswordField f = (JPasswordField) c;
			if (!f.echoCharIsSet()) {
				return super.viewToModel(fx, fy, a, bias);
			}

			final char echoChar = f.getEchoChar();
			int w = f.getFontMetrics(f.getFont()).charWidth(echoChar);
			w = (w < ancho ? ancho : w) + hueco;

			a = adjustAllocation(a);
			final Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a
					.getBounds();
			n = ((int) fx - alloc.x) / w;
			if (n < 0) {
				n = 0;
			} else if (n > (getStartOffset() + getDocument().getLength())) {
				n = getDocument().getLength() - getStartOffset();
			}
		}

		return getStartOffset() + n;
	}
}
