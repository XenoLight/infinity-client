/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarUI;

public abstract class AbstractToolBarUI extends BasicToolBarUI {

	protected class MyContainerListener implements ContainerListener {

		@Override
		public void componentAdded(final ContainerEvent e) {
			final Component c = e.getChild();
			if (c instanceof AbstractButton) {
				changeButtonBorder((AbstractButton) c);
			}
		}

		@Override
		public void componentRemoved(final ContainerEvent e) {
			final Component c = e.getChild();
			if (c instanceof AbstractButton) {
				restoreButtonBorder((AbstractButton) c);
			}
		}
	}
	protected class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent e) {
			if (e.getPropertyName().equals(IS_ROLLOVER)) {
				if (e.getNewValue() != null) {
					isRolloverEnabled = ((Boolean) e.getNewValue())
					.booleanValue();
					changeBorders();
				}
			} else if ("componentOrientation".equals(e.getPropertyName())) {
				updateToolbarBorder();
			}
		}
	}
	private static class NullBorder implements Border, UIResource {

		private static final Insets insets = new Insets(0, 0, 0, 0);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
		}
	} // class NullBorder
	private final static String IS_ROLLOVER = "JToolBar.isRollover";
	private final static Insets BUTTON_MARGIN = new Insets(1, 1, 1, 1);
	private final static Border INNER_BORDER = BorderFactory.createEmptyBorder(
			2, 2, 2, 2);
	private boolean isRolloverEnabled = true;
	private MyPropertyChangeListener propertyChangeListener = null;

	private MyContainerListener containerListener = null;

	private final Hashtable orgBorders = new Hashtable();

	private final Hashtable orgMargins = new Hashtable();

	protected void changeBorders() {
		final Component[] components = toolBar.getComponents();
		for (int i = 0; i < components.length; ++i) {
			final Component comp = components[i];
			if (comp instanceof AbstractButton) {
				changeButtonBorder((AbstractButton) comp);
			}
		}
	}

	protected void changeButtonBorder(final AbstractButton b) {
		if (!orgBorders.contains(b)) {
			if (b.getBorder() != null) {
				orgBorders.put(b, b.getBorder());
			} else {
				orgBorders.put(b, new NullBorder());
			}
		}

		if (!orgMargins.contains(b)) {
			orgMargins.put(b, b.getMargin());
		}

		if (b.getBorder() != null) {
			if (isRolloverEnabled) {
				b.setBorderPainted(true);
				b.setBorder(BorderFactory.createCompoundBorder(
						getRolloverBorder(), INNER_BORDER));
				b.setMargin(BUTTON_MARGIN);
				b.setRolloverEnabled(true);
				b.setOpaque(isButtonOpaque());
				b.setContentAreaFilled(isButtonOpaque());
			} else {
				b.setBorder(BorderFactory.createCompoundBorder(
						getNonRolloverBorder(), INNER_BORDER));
				b.setMargin(BUTTON_MARGIN);
				b.setRolloverEnabled(false);
				b.setOpaque(isButtonOpaque());
				b.setContentAreaFilled(isButtonOpaque());
			}
		}
	}

	public abstract Border getNonRolloverBorder();

	public abstract Border getRolloverBorder();

	@Override
	protected void installListeners() {
		super.installListeners();
		propertyChangeListener = new MyPropertyChangeListener();
		if (propertyChangeListener != null) {
			toolBar.addPropertyChangeListener(propertyChangeListener);
		}
		containerListener = new MyContainerListener();
		if (containerListener != null) {
			toolBar.addContainerListener(containerListener);
		}
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		final Boolean isRollover = (Boolean) UIManager.get(IS_ROLLOVER);
		if (isRollover != null) {
			isRolloverEnabled = isRollover.booleanValue();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				changeBorders();
			}
		});
	}

	public abstract boolean isButtonOpaque();

	protected boolean isToolBarUnderMenubar() {
		if (toolBar != null
				&& toolBar.getOrientation() == SwingConstants.HORIZONTAL) {
			final JRootPane rp = SwingUtilities.getRootPane(toolBar);
			final JMenuBar mb = rp.getJMenuBar();
			if (mb != null) {
				Point mbPoint = new Point(0, 0);
				mbPoint = SwingUtilities.convertPoint(mb, mbPoint, rp);
				Point tbPoint = new Point(0, 0);
				tbPoint = SwingUtilities.convertPoint(toolBar, tbPoint, rp);
				tbPoint.y -= mb.getHeight() - 1;
				final Rectangle rect = new Rectangle(mbPoint, mb.getSize());
				return rect.contains(tbPoint);
			}
		}
		return false;
	}

	protected void restoreBorders() {
		final Component[] components = toolBar.getComponents();
		for (int i = 0; i < components.length; ++i) {
			final Component comp = components[i];
			if (comp instanceof AbstractButton) {
				restoreButtonBorder((AbstractButton) comp);
			}
		}
	}

	protected void restoreButtonBorder(final AbstractButton b) {
		final Border border = (Border) orgBorders.get(b);
		if (border != null) {
			if (border instanceof NullBorder) {
				b.setBorder(null);
			} else {
				b.setBorder(border);
			}
		}
		b.setMargin((Insets) orgMargins.get(b));
	}

	@Override
	protected void setBorderToNonRollover(final Component c) {
	}

	@Override
	protected void setBorderToNormal(final Component c) {
	}

	@Override
	protected void setBorderToRollover(final Component c) {
	}

	@Override
	protected void uninstallListeners() {
		if (propertyChangeListener != null) {
			toolBar.removePropertyChangeListener(propertyChangeListener);
		}
		propertyChangeListener = null;
		if (containerListener != null) {
			toolBar.removeContainerListener(containerListener);
		}
		containerListener = null;
		super.uninstallListeners();
	}

	@Override
	public void uninstallUI(final JComponent c) {
		restoreBorders();
		super.uninstallUI(c);
	}

	protected void updateToolbarBorder() {
		toolBar.revalidate();
		toolBar.repaint();
	}
}
