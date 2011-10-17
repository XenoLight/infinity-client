/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import de.muntjak.tinylookandfeel.borders.TinyInternalFrameBorder;

/**
 * TinyInternalFrameUI
 * 
 * 6.4.06 Removed getDesktopManager() and createDesktopManager() (will now be
 * handled by the base class).
 * 
 * @version 1.3.04
 * @author Hans Bickel
 */
public class TinyInternalFrameUI extends BasicInternalFrameUI {

	public class TinyInternalFramePropertyChangeListener extends
	InternalFramePropertyChangeListener {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final String prop = evt.getPropertyName();
			final JInternalFrame f = (JInternalFrame) evt.getSource();

			final TinyInternalFrameUI ui = (TinyInternalFrameUI) f.getUI();

			if (prop.equals("JInternalFrame.isPalette")) {
				if (evt.getNewValue() != null) {
					ui.setPalette(((Boolean) evt.getNewValue()).booleanValue());
				} else {
					ui.setPalette(false);
				}
			}

			super.propertyChange(evt);
		}
	}

	/**
	 * Creates the UI delegate for the given component.
	 * 
	 * @param mainColor
	 *            The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(final JComponent c) {
		return new TinyInternalFrameUI((JInternalFrame) c);
	}

	private TinyInternalFrameBorder frameBorder;

	/**
	 * The TinyLaF version of the internal frame title pane.
	 */
	private TinyInternalFrameTitlePane titlePane;

	/**
	 * Creates the UI delegate for the given frame.
	 * 
	 * @param frame
	 *            The frame to create its UI delegate.
	 */
	public TinyInternalFrameUI(final JInternalFrame frame) {
		super(frame);
	}

	@Override
	protected void activateFrame(final JInternalFrame f) {
		super.activateFrame(f);
		frameBorder.setActive(true);
		titlePane.activate();
	}

	/**
	 * Creates the north pane (the internal frame title pane) for the given
	 * frame.
	 * 
	 * @param frame
	 *            The frame to create its north pane.
	 */
	@Override
	protected JComponent createNorthPane(final JInternalFrame frame) {
		super.createNorthPane(frame);

		titlePane = new TinyInternalFrameTitlePane(frame);

		return titlePane;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new TinyInternalFramePropertyChangeListener();
	}

	/**
	 * This method is called when the frame is no longer selected. This action
	 * is delegated to the desktopManager.
	 */
	@Override
	protected void deactivateFrame(final JInternalFrame f) {
		super.deactivateFrame(f);
		frameBorder.setActive(false);
		titlePane.deactivate();
	}

	JDesktopPane getDesktopPane(final JComponent frame) {
		JDesktopPane pane = null;
		Component c = frame.getParent();

		// Find the JDesktopPane
		while (pane == null) {
			if (c instanceof JDesktopPane) {
				pane = (JDesktopPane) c;
			} else if (c == null) {
				break;
			} else {
				c = c.getParent();
			}
		}

		return pane;
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);

		frameBorder = new TinyInternalFrameBorder();
		frame.setBorder(frameBorder);
		frame.setOpaque(false);
	}

	/**
	 * Changes this internal frame mode from / to palette mode. This affect only
	 * the title pane.
	 * 
	 * @param isPalette
	 *            The target palette mode.
	 */
	public void setPalette(final boolean isPalette) {
		// the following call caused iconify and maximize
		// buttons to disappear for palettes
		// super.setPalette(isPalette);

		titlePane.setPalette(isPalette);

		frame.setBorder(frameBorder);
		frame.putClientProperty("isPalette", isPalette ? Boolean.TRUE
				: Boolean.FALSE);
	}
}