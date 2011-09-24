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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

import de.muntjak.tinylookandfeel.borders.TinyFrameBorder;

/**
 * TinyInternalFrameTitlePane is not an UI-delegate but a JComponent.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyInternalFrameTitlePane extends BasicInternalFrameTitlePane
implements LayoutManager {
	/**
	 * This listener is added to the maximize, minimize and close button to
	 * manage the rollover status of the buttons
	 * 
	 */
	class RolloverListener implements MouseListener {
		JButton button;

		public RolloverListener(final JButton b) {
			button = b;
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			button.getModel().setRollover(true);

			if (!button.isEnabled()) {
				button.setEnabled(true);
			}

			button.repaint();
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			button.getModel().setRollover(false);

			if (!frame.isSelected()) {
				button.setEnabled(false);
			}

			button.repaint();
		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}
	}

	class TinyPropertyChangeHandler extends
	BasicInternalFrameTitlePane.PropertyChangeHandler {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final String prop = evt.getPropertyName();

			if (prop.equals(JInternalFrame.IS_SELECTED_PROPERTY)) {
				final Boolean b = (Boolean) evt.getNewValue();

				iconButton.putClientProperty("paintActive", b);
				closeButton.putClientProperty("paintActive", b);
				maxButton.putClientProperty("paintActive", b);
			}

			super.propertyChange(evt);
		}
	}

	protected boolean isPalette = false;

	static TinyWindowButtonUI iconButtonUI;

	static TinyWindowButtonUI maxButtonUI;

	static TinyWindowButtonUI closeButtonUI;

	/**
	 * This constructor creates a title pane for the given internal frame
	 * instance.
	 * 
	 * @param frame
	 *            The internal frame that needs a title pane.
	 */
	public TinyInternalFrameTitlePane(final JInternalFrame frame) {
		super(frame);
	}

	public void activate() {
		closeButton.setEnabled(true);
		iconButton.setEnabled(true);
		maxButton.setEnabled(true);
	}

	/**
	 * Adds the specified component with the specified name to the layout.
	 * 
	 * @param name
	 *            the component name
	 * @param mainColor
	 *            the component to be added
	 */
	@Override
	public void addLayoutComponent(final String name, final Component c) {
	}

	@Override
	protected void addSystemMenuItems(final JMenu systemMenu) {
		JMenuItem item = systemMenu.add(restoreAction);
		item.setIcon(MenuItemIconFactory.getSystemRestoreIcon());
		item.setMnemonic('R');

		// moveAction and sizeAction are currently undefined (J 1.6) -
		// we remove them because they don't show up in frame system menu.
		// item = (JMenuItem)systemMenu.add(moveAction);
		// item.setMnemonic('M');
		//
		// item = (JMenuItem)systemMenu.add(sizeAction);
		// item.setMnemonic('S');

		item = systemMenu.add(iconifyAction);
		item.setIcon(MenuItemIconFactory.getSystemIconifyIcon());
		item.setMnemonic('n');

		item = systemMenu.add(maximizeAction);
		item.setIcon(MenuItemIconFactory.getSystemMaximizeIcon());
		item.setMnemonic('x');

		systemMenu.add(new JSeparator());
		item = systemMenu.add(closeAction);
		item.setIcon(MenuItemIconFactory.getSystemCloseIcon());
		item.setMnemonic('C');
	}

	/**
	 * Creates the buttons of the title pane and initilizes their actions.
	 */
	@Override
	protected void createButtons() {
		if (iconButtonUI == null) {
			iconButtonUI = TinyWindowButtonUI
			.createButtonUIForType(TinyWindowButtonUI.MINIMIZE);
			maxButtonUI = TinyWindowButtonUI
			.createButtonUIForType(TinyWindowButtonUI.MAXIMIZE);
			closeButtonUI = TinyWindowButtonUI
			.createButtonUIForType(TinyWindowButtonUI.CLOSE);
		}

		iconButton = new SpecialUIButton(iconButtonUI);
		iconButton.addActionListener(iconifyAction);
		iconButton.setText(null);
		iconButton.setRolloverEnabled(true);
		iconButton.addMouseListener(new RolloverListener(iconButton));

		maxButton = new SpecialUIButton(maxButtonUI);
		maxButton.addActionListener(maximizeAction);
		maxButton.setText(null);
		maxButton.setRolloverEnabled(true);
		maxButton.addMouseListener(new RolloverListener(maxButton));

		closeButton = new SpecialUIButton(closeButtonUI);
		closeButton.addActionListener(closeAction);
		closeButton.setText(null);
		closeButton.setRolloverEnabled(true);
		closeButton.addMouseListener(new RolloverListener(closeButton));

		iconButton.putClientProperty(
				TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.FALSE);
		maxButton.putClientProperty(
				TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.FALSE);
		closeButton.putClientProperty(
				TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.FALSE);

		iconButton
		.getAccessibleContext()
		.setAccessibleName(
				UIManager
				.getString("InternalFrameTitlePane.iconifyButtonAccessibleName"));
		maxButton
		.getAccessibleContext()
		.setAccessibleName(
				UIManager
				.getString("InternalFrameTitlePane.maximizeButtonAccessibleName"));
		closeButton
		.getAccessibleContext()
		.setAccessibleName(
				UIManager
				.getString("InternalFrameTitlePane.closeButtonAccessibleName"));

		if (frame.isSelected()) {
			activate();
		} else {
			deactivate();
		}
	}

	/**
	 * Creates the layout manager for the title pane.
	 * 
	 * @return The layout manager for the title pane.
	 */
	@Override
	protected LayoutManager createLayout() {
		return this;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new TinyPropertyChangeHandler();
	}

	@Override
	protected JMenu createSystemMenu() {
		final JMenu menu = new JMenu("");

		// New in 1.4.0: Don't paint rollovers on top menus
		// as long as a system menu is showing
		menu.addMenuListener(new MenuListener() {
			@Override
			public void menuCanceled(final MenuEvent e) {
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
				TinyMenuUI.systemMenuShowing = false;
			}

			@Override
			public void menuSelected(final MenuEvent e) {
				if (frameHasMenuBar()) {
					TinyMenuUI.systemMenuShowing = true;
				}
			}
		});

		// We use this property to prevent the menu from drawing rollovers
		menu.putClientProperty(TinyMenuUI.IS_SYSTEM_MENU_KEY, Boolean.TRUE);

		return menu;
	}
	public void deactivate() {
		closeButton.setEnabled(false);
		iconButton.setEnabled(false);
		maxButton.setEnabled(false);
	}
	private boolean frameHasMenuBar() {
		if (frame != null) {
			return (frame.getJMenuBar() != null);
		}

		return false;
	}

	/**
	 * @see java.awt.Component#getFont()
	 */
	@Override
	public Font getFont() {
		Font f = null;

		if (isPalette) {
			f = UIManager.getFont("InternalFrame.paletteTitleFont");
		} else {
			f = UIManager.getFont("InternalFrame.normalTitleFont");
		}

		// if(f == null) {
		// f = new Font("SansSerife", Font.BOLD, 12);
		// }

		return f;
	}

	/**
	 * The minimum size of the frame. This is used, for example, during resizing
	 * to find the minimum allowable size. Providing at least some minimum size
	 * fixes a bug which breaks horizontal resizing. <b>Note</b>: the Motif plaf
	 * allows for a 0,0 min size, but we provide a reasonable minimum here.
	 * <b>Future</b>: calculate min size based upon contents.
	 */
	@Override
	public Dimension getMinimumSize() {
		isPalette = (frame.getClientProperty("isPalette") == Boolean.TRUE);
		final int height = (isPalette ? TinyFrameBorder.FRAME_PALETTE_TITLE_HEIGHT
				: TinyFrameBorder.FRAME_INTERNAL_TITLE_HEIGHT);

		return new Dimension(TinyLookAndFeel.MINIMUM_INTERNAL_FRAME_WIDTH,
				height);
	}

	/**
	 * Gets the preferred size of the given container.
	 * 
	 * @return The preferred size of the given container.
	 */
	public Dimension getPreferredSize(final Container c) {
		isPalette = (frame.getClientProperty("isPalette") == Boolean.TRUE);
		int width = 22;

		if (frame.isClosable()) {
			width += 19;
		}
		if (frame.isMaximizable()) {
			width += 19;
		}
		if (frame.isIconifiable()) {
			width += 19;
		}

		final FontMetrics fm = getFontMetrics(getFont());
		final String frameTitle = frame.getTitle();
		final int title_w = frameTitle != null ? fm.stringWidth(frameTitle) : 0;
		final int title_length = frameTitle != null ? frameTitle.length() : 0;

		// Leave room for three characters in the title.
		if (title_length > 3) {
			final int subtitle_w = fm.stringWidth(frameTitle.substring(0, 3) + "...");
			width += (title_w < subtitle_w) ? title_w : subtitle_w;
		} else {
			width += title_w;
		}

		// height
		final int height = (isPalette ? TinyFrameBorder.FRAME_PALETTE_TITLE_HEIGHT
				: TinyFrameBorder.FRAME_INTERNAL_TITLE_HEIGHT);

		final Dimension dim = new Dimension(width, height);

		// Take into account the border insets if any.
		if (getBorder() != null) {
			final Insets insets = getBorder().getBorderInsets(c);
			dim.height += insets.top + insets.bottom;
			dim.width += insets.left + insets.right;
		}

		return dim;
	}

	public boolean isFrameMaximized() {
		return frame.isMaximum();
	}

	public boolean isFrameSelected() {
		return frame.isSelected();
	}

	public boolean isPalette() {
		return isPalette;
	}

	/**
	 * Lays out the container in the specified panel.
	 * 
	 * @param c
	 *            the component which needs to be laid out
	 */
	@Override
	public void layoutContainer(final Container c) {
		isPalette = (frame.getClientProperty("isPalette") == Boolean.TRUE);
		final boolean leftToRight = frame.getComponentOrientation().isLeftToRight();
		final int buttonHeight = closeButton.getPreferredSize().height;
		final int h = getHeight();
		final int w = getWidth();
		int x = leftToRight ? w : 0;
		final int y = (h - buttonHeight) / 2 + 1;
		int spacing;

		int buttonWidth = 0;

		if (isPalette) {
			buttonWidth = TinyWindowButtonUI.framePaletteButtonSize.width;
		} else {
			buttonWidth = TinyWindowButtonUI.frameInternalButtonSize.width;
		}

		final Icon icon = frame.getFrameIcon();
		int iconHeight = 0;

		if (icon != null) {
			iconHeight = icon.getIconHeight();
		}

		x = (leftToRight) ? 4 : w - 16 - 4;
		menuBar.setBounds(x, (h - iconHeight) / 2, 16, 16);

		x = leftToRight ? w : 0;

		if (frame.isClosable()) {
			spacing = 2;
			x += leftToRight ? -spacing - buttonWidth : spacing;
			closeButton.setBounds(x, y, buttonWidth, buttonHeight);

			if (!leftToRight)
				x += buttonWidth;
		}

		if (frame.isMaximizable()) {
			spacing = 2;
			x += leftToRight ? -spacing - buttonWidth : spacing;
			maxButton.setBounds(x, y, buttonWidth, buttonHeight);
			if (!leftToRight)
				x += buttonWidth;
		}

		if (frame.isIconifiable()) {
			spacing = 2;
			x += leftToRight ? -spacing - buttonWidth : spacing;
			iconButton.setBounds(x, y, buttonWidth, buttonHeight);
			if (!leftToRight)
				x += buttonWidth;
		}
	}

	/**
	 * Calculates the minimum size dimensions for the specified panel given the
	 * components in the specified parent container.
	 */
	@Override
	public Dimension minimumLayoutSize(final Container c) {
		return preferredLayoutSize(c);
	}

	/**
	 * Paints this component.
	 * 
	 * @param g
	 *            The graphics context to use.
	 */
	@Override
	public void paintComponent(final Graphics g) {
		frame.setOpaque(false);

		final boolean leftToRight = frame.getComponentOrientation().isLeftToRight();
		final boolean isSelected = frame.isSelected();

		final int width = getWidth();
		final int height = getHeight();
		final String frameTitle = frame.getTitle();

		if (frameTitle != null) {
			int xOffset = leftToRight ? 4 + 16 + 4 : width - 4 - 16 - 4;
			final Font f = getFont();
			g.setFont(f);
			final FontMetrics fm = g.getFontMetrics();
			final int titleLength = fm.stringWidth(frameTitle);

			final int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent() + 1;
			if (!leftToRight)
				xOffset -= titleLength;

			// New in 1.4.0: Title text painted antialiased
			if (g instanceof Graphics2D) {
				final Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}

			if (isSelected) {
				// Paint shadow
				g.setColor(Theme.frameTitleShadowColor.getColor());
				g.drawString(frameTitle, xOffset + 1, yOffset + 1);

				g.setColor(Theme.frameTitleColor.getColor());
				g.drawString(frameTitle, xOffset, yOffset);
			} else {
				// for an inactive window
				g.setColor(Theme.frameTitleDisabledColor.getColor());
				g.drawString(frameTitle, xOffset, yOffset);
			}
		}
	}

	/**
	 * Paints the title pane for a palette.
	 * 
	 * @param g
	 *            The graphics context to use.
	 */
	public void paintPalette(final Graphics g) {
	}

	@Override
	protected void paintTitleBackground(final Graphics g) {
	}

	/**
	 * Calculates the preferred size dimensions for the specified panel given
	 * the components in the specified parent container.
	 * 
	 * @param mainColor
	 *            the component to be laid out
	 */
	@Override
	public Dimension preferredLayoutSize(final Container c) {
		return getPreferredSize(c);
	}

	/**
	 * Removes the specified component from the layout.
	 * 
	 * @param mainColor
	 *            the component to be removed
	 */
	@Override
	public void removeLayoutComponent(final Component c) {
	}

	/**
	 * Overridden to do nothing.
	 */
	@Override
	protected void setButtonIcons() {
	}

	public void setPalette(final boolean b) {
		isPalette = b;
	}
}
