/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.plaf.basic.BasicButtonListener;

public class BaseButtonListener extends BasicButtonListener {

	public BaseButtonListener(final AbstractButton b) {
		super(b);
	}

	@Override
	public void focusGained(final FocusEvent e) {
		final AbstractButton b = (AbstractButton) e.getSource();
		b.repaint();
	}

	@Override
	public void focusLost(final FocusEvent e) {
		final AbstractButton b = (AbstractButton) e.getSource();
		b.repaint();
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		super.mouseEntered(e);
		final AbstractButton button = (AbstractButton) e.getSource();
		button.getModel().setRollover(true);
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		super.mouseExited(e);
		final AbstractButton button = (AbstractButton) e.getSource();
		button.getModel().setRollover(false);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		super.mouseReleased(e);
	}
}
