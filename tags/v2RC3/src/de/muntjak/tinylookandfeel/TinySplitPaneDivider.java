/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 * @(#)TinySplitPaneDivider.java	1.17 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * .... Thanks a lot to sun for not making this class public ....
 * I guess they don't want us to create look and feels ...
 */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Metal's split pane divider
 * <p>
 * <strong>Warning:</strong> Serialized objects of this class will not be
 * compatible with future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Swing. As of 1.4, support for long term storage of all
 * JavaBeans<sup><font size="-2">TM</font></sup> has been added to the
 * <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
 * 
 * @version 1.17 12/03/01
 * @author Steve Wilson
 * @author Ralph kar
 */
class TinySplitPaneDivider extends BasicSplitPaneDivider {

	/**
	 * Used to layout a TinySplitPaneDivider. Layout for the divider involves
	 * appropriately moving the left/right buttons around.
	 * <p>
	 * This inner class is marked &quot;public&quot; due to a compiler bug. This
	 * class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of TinySplitPaneDivider.
	 */
	public class MetalDividerLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(final String string, final Component c) {
		}

		@Override
		public void layoutContainer(final Container c) {
			final JButton leftButton = getLeftButtonFromSuper();
			final JButton rightButton = getRightButtonFromSuper();
			final JSplitPane splitPane = getSplitPaneFromSuper();
			final int orientation = getOrientationFromSuper();
			final int oneTouchSize = getOneTouchSizeFromSuper();
			final int oneTouchOffset = getOneTouchOffsetFromSuper();
			final Insets insets = getInsets();

			// This layout differs from the one used in BasicSplitPaneDivider.
			// It does not center justify the oneTouchExpadable buttons.
			// This was necessary in order to meet the spec of the Metal
			// splitpane divider.
			if (leftButton != null && rightButton != null
					&& c == TinySplitPaneDivider.this) {
				if (splitPane.isOneTouchExpandable()) {
					if (orientation == JSplitPane.VERTICAL_SPLIT) {
						final int extraY = (insets != null) ? insets.top : 0;
						int blockSize = getDividerSize();

						if (insets != null) {
							blockSize -= (insets.top + insets.bottom);
						}
						blockSize = Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(oneTouchOffset, extraY,
								blockSize * 2, blockSize);
						rightButton.setBounds(
								oneTouchOffset + oneTouchSize * 2, extraY,
								blockSize * 2, blockSize);
					} else {
						int blockSize = getDividerSize();
						final int extraX = (insets != null) ? insets.left : 0;

						if (insets != null) {
							blockSize -= (insets.left + insets.right);
						}
						blockSize = Math.min(blockSize, oneTouchSize);
						leftButton.setBounds(extraX, oneTouchOffset, blockSize,
								blockSize * 2);
						rightButton.setBounds(extraX, oneTouchOffset
								+ oneTouchSize * 2, blockSize, blockSize * 2);
					}
				} else {
					leftButton.setBounds(-5, -5, 1, 1);
					rightButton.setBounds(-5, -5, 1, 1);
				}
			}
		}

		@Override
		public Dimension minimumLayoutSize(final Container c) {
			return new Dimension(0, 0);
		}

		@Override
		public Dimension preferredLayoutSize(final Container c) {
			return new Dimension(0, 0);
		}

		@Override
		public void removeLayoutComponent(final Component c) {
		}
	}
	private final int inset = 2;

	private final Color controlColor = MetalLookAndFeel.getControl();

	public TinySplitPaneDivider(final BasicSplitPaneUI ui) {
		super(ui);
		setLayout(new MetalDividerLayout());
	}

	/**
	 * Creates and return an instance of JButton that can be used to collapse
	 * the left component in the metal split pane.
	 */
	@Override
	protected JButton createLeftOneTouchButton() {
		final JButton b = new JButton() {
			// Don't want the button to participate in focus traversable.
			@Override
			public boolean isFocusTraversable() {
				return false;
			}

			@Override
			public void paint(final Graphics g) {
				final JSplitPane splitPane = getSplitPaneFromSuper();

				// changed this in 1.3
				if (splitPane != null) {
					getOneTouchSizeFromSuper();
					final int orientation = getOrientationFromSuper();

					// Fill the background first ...
					g.setColor(Theme.backColor.getColor());
					g.fillRect(0, 0, this.getWidth(), this.getHeight());

					g.setColor(Theme.splitPaneButtonColor.getColor());

					if (orientation == JSplitPane.VERTICAL_SPLIT) {
						g.drawLine(2, 1, 3, 1);
						g.drawLine(1, 2, 4, 2);
						g.drawLine(0, 3, 5, 3);
					} else {
						// HORIZONTAL_SPLIT
						g.drawLine(1, 2, 1, 3);
						g.drawLine(2, 1, 2, 4);
						g.drawLine(3, 0, 3, 5);
					}
				}
			}

			@Override
			public void setBorder(final Border b) {
			}
		};
		b.setRequestFocusEnabled(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		return b;
	}

	/**
	 * Creates and return an instance of JButton that can be used to collapse
	 * the right component in the metal split pane.
	 */
	@Override
	protected JButton createRightOneTouchButton() {
		final JButton b = new JButton() {
			// Don't want the button to participate in focus traversable.
			@Override
			public boolean isFocusTraversable() {
				return false;
			}

			@Override
			public void paint(final Graphics g) {
				final JSplitPane splitPane = getSplitPaneFromSuper();

				// changed this in 1.3
				if (splitPane != null) {
					getOneTouchSizeFromSuper();
					final int orientation = getOrientationFromSuper();

					// Fill the background first ...
					g.setColor(Theme.backColor.getColor());
					g.fillRect(0, 0, this.getWidth(), this.getHeight());

					g.setColor(Theme.splitPaneButtonColor.getColor());

					if (orientation == JSplitPane.VERTICAL_SPLIT) {
						g.drawLine(2, 3, 3, 3);
						g.drawLine(1, 2, 4, 2);
						g.drawLine(0, 1, 5, 1);
					} else {
						// HORIZONTAL_SPLIT
						g.drawLine(3, 2, 3, 3);
						g.drawLine(2, 1, 2, 4);
						g.drawLine(1, 0, 1, 5);
					}
				}
			}

			@Override
			public void setBorder(final Border border) {
			}
		};

		b.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setRequestFocusEnabled(false);
		return b;
	}

	JButton getLeftButtonFromSuper() {
		return super.leftButton;
	}

	/*
	 * The following methods only exist in order to be able to access protected
	 * members in the superclass, because these are otherwise not available in
	 * any inner class.
	 */

	int getOneTouchOffsetFromSuper() {
		return super.ONE_TOUCH_OFFSET;
	}

	int getOneTouchSizeFromSuper() {
		return super.ONE_TOUCH_SIZE;
	}

	int getOrientationFromSuper() {
		return super.orientation;
	}

	JButton getRightButtonFromSuper() {
		return super.rightButton;
	}

	JSplitPane getSplitPaneFromSuper() {
		return super.splitPane;
	}

	@Override
	public void paint(final Graphics g) {
		g.setColor(controlColor);

		final Rectangle clip = g.getClipBounds();
		final Insets insets = getInsets();
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		final Dimension size = getSize();
		size.width -= inset * 2;
		size.height -= inset * 2;
		int drawX = inset;
		int drawY = inset;

		if (insets != null) {
			size.width -= (insets.left + insets.right);
			size.height -= (insets.top + insets.bottom);
			drawX += insets.left;
			drawY += insets.top;
		}

		super.paint(g);
	}
}
