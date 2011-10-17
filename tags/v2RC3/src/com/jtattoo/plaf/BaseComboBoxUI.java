/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class BaseComboBoxUI extends BasicComboBoxUI {

	public static class ArrowButton extends NoFocusButton {

		@Override
		public void paint(final Graphics g) {
			final Dimension size = getSize();
			if (isEnabled()) {
				if (getModel().isArmed() && getModel().isPressed()) {
					JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
							.getTheme().getPressedColors(), 0, 0, size.width,
							size.height);
				} else if (getModel().isRollover()) {
					JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
							.getTheme().getRolloverColors(), 0, 0, size.width,
							size.height);
				} else if (JTattooUtilities.isActive(this)) {
					JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
							.getTheme().getButtonColors(), 0, 0, size.width,
							size.height);
				} else {
					JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
							.getTheme().getInActiveColors(), 0, 0, size.width,
							size.height);
				}
			} else {
				JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel
						.getTheme().getDisabledColors(), 0, 0, size.width,
						size.height);
			}
			final Icon icon = BaseIcons.getComboBoxIcon();
			final int x = (size.width - icon.getIconWidth()) / 2;
			final int y = (size.height - icon.getIconHeight()) / 2;
			if (getModel().isPressed() && getModel().isArmed()) {
				icon.paintIcon(this, g, x + 2, y + 1);
			} else {
				icon.paintIcon(this, g, x + 1, y);
			}
			paintBorder(g);
		}
	}

	public class PropertyChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent e) {
			final String name = e.getPropertyName();
			if (name.equals("componentOrientation")) {
				setButtonBorder();
			}
		}
	}

	public static ComponentUI createUI(final JComponent c) {
		return new BaseComboBoxUI();
	}

	protected PropertyChangeListener propertyChangeListener;

	@Override
	public JButton createArrowButton() {
		final JButton button = new ArrowButton();
		if (JTattooUtilities.isLeftToRight(comboBox)) {
			final Border border = BorderFactory.createMatteBorder(0, 1, 0, 0,
					AbstractLookAndFeel.getFrameColor());
			button.setBorder(border);
		} else {
			final Border border = BorderFactory.createMatteBorder(0, 0, 0, 1,
					AbstractLookAndFeel.getFrameColor());
			button.setBorder(border);
		}
		return button;
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		final Dimension size = super.getPreferredSize(c);
		return new Dimension(size.width + 2, size.height + 2);
	}

	@Override
	protected void installListeners() {
		super.installListeners();
		propertyChangeListener = new PropertyChangeHandler();
		comboBox.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		comboBox.setRequestFocusEnabled(true);
		if (comboBox.getEditor() != null) {
			if (comboBox.getEditor().getEditorComponent() instanceof JTextField) {
				((JTextField) (comboBox.getEditor().getEditorComponent()))
				.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			}
		}
	}

	protected void setButtonBorder() {
		if (JTattooUtilities.isLeftToRight(comboBox)) {
			final Border border = BorderFactory.createMatteBorder(0, 1, 0, 0,
					AbstractLookAndFeel.getFrameColor());
			arrowButton.setBorder(border);
		} else {
			final Border border = BorderFactory.createMatteBorder(0, 0, 0, 1,
					AbstractLookAndFeel.getFrameColor());
			arrowButton.setBorder(border);
		}
	}

	// -----------------------------------------------------------------------------

	@Override
	protected void uninstallListeners() {
		comboBox.removePropertyChangeListener(propertyChangeListener);
		propertyChangeListener = null;
		super.uninstallListeners();
	}
}
