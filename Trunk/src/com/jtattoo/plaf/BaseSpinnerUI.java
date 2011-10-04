/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 * 
 * @author Michael Hagen
 */
public class BaseSpinnerUI extends BasicSpinnerUI {
	// ------------------------------------------------------------------------------
	private static class MyLayoutManager implements LayoutManager {
		//
		// LayoutManager
		//
		private Component nextButton = null;
		private Component previousButton = null;
		private Component editor = null;

		@Override
		public void addLayoutComponent(final String name, final Component c) {
			if ("Next".equals(name)) {
				nextButton = c;
			} else if ("Previous".equals(name)) {
				previousButton = c;
			} else if ("Editor".equals(name)) {
				editor = c;
			}
		}

		@Override
		public void layoutContainer(final Container parent) {
			final int width = parent.getWidth();
			final int height = parent.getHeight();

			final Insets insets = parent.getInsets();
			final Dimension nextD = preferredSize(nextButton);
			final Dimension previousD = preferredSize(previousButton);
			final int buttonsWidth = Math.max(nextD.width, previousD.width);
			final int editorHeight = height - (insets.top + insets.bottom);

			// The arrowButtonInsets value is used instead of the JSpinner's
			// insets if not null. Defining this to be (0, 0, 0, 0) causes the
			// buttons to be aligned with the outer edge of the spinner's
			// border, and leaving it as "null" places the buttons completely
			// inside the spinner's border.
			Insets buttonInsets = UIManager
			.getInsets("Spinner.arrowButtonInsets");
			if (buttonInsets == null) {
				buttonInsets = insets;
			}

			// Deal with the spinner's componentOrientation property.
			int editorX, editorWidth, buttonsX;
			if (parent.getComponentOrientation().isLeftToRight()) {
				editorX = insets.left;
				editorWidth = width - insets.left - buttonsWidth
				- buttonInsets.right;
				buttonsX = width - buttonsWidth - buttonInsets.right;
			} else {
				buttonsX = buttonInsets.left;
				editorX = buttonsX + buttonsWidth;
				editorWidth = width - buttonInsets.left - buttonsWidth
				- insets.right;
			}

			final int nextY = buttonInsets.top;
			final int nextHeight = (height / 2) + (height % 2) - nextY;
			final int previousY = buttonInsets.top + nextHeight;
			final int previousHeight = height - previousY - buttonInsets.bottom;

			setBounds(editor, editorX, insets.top, editorWidth, editorHeight);
			setBounds(nextButton, buttonsX, nextY, buttonsWidth, nextHeight);
			setBounds(previousButton, buttonsX, previousY, buttonsWidth,
					previousHeight);
		}

		@Override
		public Dimension minimumLayoutSize(final Container parent) {
			return preferredLayoutSize(parent);
		}

		@Override
		public Dimension preferredLayoutSize(final Container parent) {
			final Dimension nextD = preferredSize(nextButton);
			final Dimension previousD = preferredSize(previousButton);
			final Dimension editorD = preferredSize(editor);

			// Force the editors height to be a multiple of 2
			editorD.height = ((editorD.height + 1) / 2) * 2;

			final Dimension size = new Dimension(editorD.width, editorD.height);
			size.width += Math.max(nextD.width, previousD.width);
			final Insets insets = parent.getInsets();
			size.width += insets.left + insets.right;
			size.height += insets.top + insets.bottom + 4;
			return size;
		}

		private Dimension preferredSize(final Component c) {
			return (c == null) ? zeroSize : c.getPreferredSize();
		}

		@Override
		public void removeLayoutComponent(Component c) {
			if (c == nextButton) {
				c = null;
			} else if (c == previousButton) {
				previousButton = null;
			} else if (c == editor) {
				editor = null;
			}
		}

		private void setBounds(final Component c, final int x, final int y, final int width, final int height) {
			if (c != null) {
				c.setBounds(x, y, width, height);
			}
		}

	}

	// ------------------------------------------------------------------------------
	public static class SpinButton extends NoFocusButton {
		private static Dimension minSize = new Dimension(14, 12);
		private int direction = SwingConstants.NORTH;

		public SpinButton(final int aDirection) {
			super();
			setInheritsPopupMenu(true);
			direction = aDirection;
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension size = super.getPreferredSize();
			size.width = Math.max(size.width, minSize.width);
			size.height = Math.max(size.height, minSize.height);
			return size;
		}

		@Override
		public void paint(final Graphics g) {
			Color colors[] = null;
			final ButtonModel model = getModel();
			if (isEnabled()) {
				if (model.isPressed() && model.isArmed())
					colors = AbstractLookAndFeel.getTheme().getPressedColors();
				else {
					if (model.isRollover())
						colors = AbstractLookAndFeel.getTheme()
						.getRolloverColors();
					else if (JTattooUtilities.isFrameActive(this))
						colors = AbstractLookAndFeel.getTheme()
						.getButtonColors();
					else
						colors = AbstractLookAndFeel.getTheme()
						.getInActiveColors();
				}
			} else
				colors = AbstractLookAndFeel.getTheme().getDisabledColors();
			JTattooUtilities.fillHorGradient(g, colors, 0, 0, getWidth(),
					getHeight());
			paintBorder(g);
			g.setColor(getForeground());
			final int w = 4;
			final int h = 3;
			final int x = (getWidth() - w) / 2;
			final int y = (getHeight() - h) / 2;
			if (direction == SwingConstants.NORTH) {
				for (int i = 0; i < h; i++) {
					g.drawLine(x + (h - i) - 1, y + i, x + w - (h - i) + 1, y
							+ i);
				}
			} else {
				for (int i = 0; i < h; i++) {
					g.drawLine(x + i, y + i, x + w - i, y + i);
				}
			}
		}

	}

	/**
	 * Used by the default LayoutManager class - SpinnerLayout for missing
	 * (null) editor/nextButton/previousButton children.
	 */
	private static final Dimension zeroSize = new Dimension(0, 0);

	/**
	 * Returns a new instance of BaseSpinnerUI. SpinnerListUI delegates are
	 * allocated one per JSpinner.
	 * 
	 * @param c
	 *            the JSpinner (not used)
	 * @see ComponentUI#createUI
	 * @return a new BasicSpinnerUI object
	 */
	public static ComponentUI createUI(final JComponent c) {
		return new BaseSpinnerUI();
	}

	private MyLayoutManager myLayoutManager = null;

	/**
	 * Create a <code>LayoutManager</code> that manages the <code>editor</code>,
	 * <code>nextButton</code>, and <code>previousButton</code> children of the
	 * JSpinner. These three children must be added with a constraint that
	 * identifies their role: "Editor", "Next", and "Previous". The default
	 * layout manager can handle the absence of any of these children.
	 * 
	 * @return a LayoutManager for the editor, next button, and previous button.
	 * @see #createNextButton
	 * @see #createPreviousButton
	 * @see #createEditor
	 */
	@Override
	protected LayoutManager createLayout() {
		if (myLayoutManager == null) {
			myLayoutManager = new MyLayoutManager();
		}
		return myLayoutManager;
	}

	@Override
	protected Component createNextButton() {
		final JButton button = new SpinButton(SwingConstants.NORTH);
		if (JTattooUtilities.isLeftToRight(spinner)) {
			final Border border = BorderFactory.createMatteBorder(0, 1, 1, 0,
					AbstractLookAndFeel.getFrameColor());
			button.setBorder(border);
		} else {
			final Border border = BorderFactory.createMatteBorder(0, 0, 1, 1,
					AbstractLookAndFeel.getFrameColor());
			button.setBorder(border);
		}
		installNextButtonListeners(button);
		return button;
	}

	@Override
	protected Component createPreviousButton() {
		final JButton button = new SpinButton(SwingConstants.SOUTH);
		if (JTattooUtilities.isLeftToRight(spinner)) {
			final Border border = BorderFactory.createMatteBorder(0, 1, 0, 0,
					AbstractLookAndFeel.getFrameColor());
			button.setBorder(border);
		} else {
			final Border border = BorderFactory.createMatteBorder(0, 0, 0, 1,
					AbstractLookAndFeel.getFrameColor());
			button.setBorder(border);
		}
		installPreviousButtonListeners(button);
		return button;
	}

}
