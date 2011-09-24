/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class BaseInternalFrameUI extends BasicInternalFrameUI {

	private static class MyPropertyChangeHandler implements
	PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent e) {
			final JInternalFrame jif = (JInternalFrame) e.getSource();
			if (!(jif.getUI() instanceof BaseInternalFrameUI)) {
				return;
			}

			final BaseInternalFrameUI ui = (BaseInternalFrameUI) jif.getUI();
			final String name = e.getPropertyName();
			if (name.equals("JInternalFrame.frameType")) {
				if (e.getNewValue() instanceof String) {
					if ("palette".equals(e.getNewValue())) {
						LookAndFeel.installBorder(ui.frame,
						"InternalFrame.paletteBorder");
						ui.setPalette(true);
					} else {
						LookAndFeel.installBorder(ui.frame,
						"InternalFrame.border");
						ui.setPalette(false);
					}
				}
			} else if (name.equals("JInternalFrame.isPalette")) {
				if (e.getNewValue() != null) {
					ui.setPalette(((Boolean) e.getNewValue()).booleanValue());
				} else {
					ui.setPalette(false);
				}
			} else if (name.equals(JInternalFrame.CONTENT_PANE_PROPERTY)) {
				ui.stripContentBorder();
			}
		}
	} // end class MyPropertyChangeHandler

	private static final PropertyChangeListener myPropertyChangeListener = new MyPropertyChangeHandler();

	public static ComponentUI createUI(final JComponent c) {
		return new BaseInternalFrameUI((JInternalFrame) c);
	}

	public BaseInternalFrameUI(final JInternalFrame b) {
		super(b);
	}

	@Override
	protected void activateFrame(final JInternalFrame f) {
		getDesktopManager().activateFrame(f);
	}

	@Override
	protected JComponent createNorthPane(final JInternalFrame w) {
		titlePane = new BaseInternalFrameTitlePane(w);
		return titlePane;
	}

	@Override
	protected void deactivateFrame(final JInternalFrame f) {
		getDesktopManager().deactivateFrame(f);
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		frame.addPropertyChangeListener(myPropertyChangeListener);
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		final Object paletteProp = c.getClientProperty("JInternalFrame.isPalette");
		if (paletteProp != null) {
			setPalette(((Boolean) paletteProp).booleanValue());
		}
		stripContentBorder();
	}

	public void setPalette(final boolean isPalette) {
		if (isPalette) {
			frame.setBorder(UIManager.getBorder("InternalFrame.paletteBorder"));
		} else {
			frame.setBorder(UIManager.getBorder("InternalFrame.border"));
		}

		if (titlePane instanceof BaseInternalFrameTitlePane) {
			((BaseInternalFrameTitlePane) titlePane).setPalette(isPalette);
		}
	}

	public void stripContentBorder() {
		final Container content = frame.getContentPane();
		if (content instanceof JComponent) {
			final JComponent contentPane = (JComponent) content;
			contentPane.setBorder(BorderFactory.createEmptyBorder());
		}
	}

	@Override
	protected void uninstallComponents() {
		titlePane = null;
		super.uninstallComponents();
	}

	@Override
	protected void uninstallListeners() {
		frame.removePropertyChangeListener(myPropertyChangeListener);
		super.uninstallListeners();
	}
}
