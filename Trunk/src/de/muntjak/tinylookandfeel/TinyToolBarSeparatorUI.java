/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

/**
 * ToolBarSeparatorUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolBarSeparatorUI extends BasicToolBarSeparatorUI {

	private static final int YQ_SIZE = 7;

	public static ComponentUI createUI(final JComponent c) {
		return new TinyToolBarSeparatorUI();
	}

	protected void drawXpToolBarSeparator(final Graphics g, final JComponent c) {
		final JToolBar.Separator sep = (JToolBar.Separator) c;

		if (sep.getOrientation() == SwingConstants.HORIZONTAL) {
			final int y = sep.getHeight() / 2; // centered if height is odd

			g.setColor(Theme.toolSeparatorColor.getColor());
			g.drawLine(0, y, sep.getWidth(), y);
		} else {
			final int x = sep.getWidth() / 2; // centered if width is odd

			g.setColor(Theme.toolSeparatorColor.getColor());
			g.drawLine(x, 0, x, sep.getHeight());
		}
	}

	@Override
	public Dimension getMaximumSize(final JComponent c) {
		final JToolBar.Separator sep = (JToolBar.Separator) c;
		final Dimension size = sep.getSeparatorSize();

		if (sep.getOrientation() == SwingConstants.HORIZONTAL) {
			if (size != null)
				return new Dimension(32767, size.height);

			return new Dimension(32767, YQ_SIZE);
		} else {
			if (size != null)
				return new Dimension(32767, size.width);

			return new Dimension(YQ_SIZE, 32767);
		}
	}

	@Override
	public Dimension getMinimumSize(final JComponent c) {
		final JToolBar.Separator sep = (JToolBar.Separator) c;

		if (sep.getOrientation() == SwingConstants.HORIZONTAL) {
			return new Dimension(0, 1);
		} else {
			return new Dimension(1, 0);
		}
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		final JToolBar.Separator sep = (JToolBar.Separator) c;

		final Dimension size = sep.getSeparatorSize();

		if (size != null)
			return size.getSize();

		if (sep.getOrientation() == SwingConstants.HORIZONTAL) {
			return new Dimension(0, YQ_SIZE);
		} else {
			return new Dimension(YQ_SIZE, 0);
		}
	}

	/**
	 * Overridden to do nothing
	 */
	@Override
	protected void installDefaults(final JSeparator s) {
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		drawXpToolBarSeparator(g, c);
	}
}
