/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.UIResource;

import de.muntjak.tinylookandfeel.borders.TinyFrameBorder;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinyTitlePane is responsible for painting the title bar of frames and
 * dialogs.
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyTitlePane extends JComponent {

	/**
	 * CaptionKey is used as key in the cache HashMap. Overrides equals() and
	 * hashCode(). Note: With 1.4.0 additionally the frame caption color is
	 * considered, else theme switching doesn't work well.
	 */
	private static class CaptionKey {

		private final Color frameCaptionColor;
		private final boolean isActive;
		private final int titleHeight;

		CaptionKey(final Color frameCaptionColor, final boolean isActive, final int titleHeight) {
			this.frameCaptionColor = frameCaptionColor;
			this.isActive = isActive;
			this.titleHeight = titleHeight;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == null)
				return false;
			if (!(o instanceof CaptionKey))
				return false;

			final CaptionKey other = (CaptionKey) o;

			return frameCaptionColor.equals(other.frameCaptionColor)
			&& isActive == other.isActive
			&& titleHeight == other.titleHeight;
		}

		@Override
		public int hashCode() {
			return frameCaptionColor.hashCode() * (isActive ? 1 : 2)
			* titleHeight;
		}
	}

	/**
	 * Actions used to <code>close</code> the <code>Window</code>.
	 */
	private class CloseAction extends AbstractAction {
		public CloseAction() {
			super(UIManager.getString("MetalTitlePane.closeTitle", getLocale()));

			// System.out.println("closeTitle=" +
			// UIManager.getString("MetalTitlePane.closeTitle",
			// getLocale()));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			close();
		}
	}

	/**
	 * Actions used to <code>iconfiy</code> the <code>Frame</code>.
	 */
	private class IconifyAction extends AbstractAction {
		public IconifyAction() {
			super(UIManager.getString("MetalTitlePane.iconifyTitle",
					getLocale()));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			iconify();
		}
	}
	/**
	 * Actions used to <code>restore</code> the <code>Frame</code>.
	 */
	private class MaximizeAction extends AbstractAction {

		public MaximizeAction() {
			super(UIManager.getString("MetalTitlePane.maximizeTitle",
					getLocale()));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			maximize();
		}
	}

	/**
	 * PropertyChangeListener installed on the Window. Updates the necessary
	 * state as the state of the Window changes.
	 */
	private class PropertyChangeHandler implements PropertyChangeListener {
		@Override
		public void propertyChange(final PropertyChangeEvent pce) {
			final String name = pce.getPropertyName();

			// Frame.state isn't currently bound.
			if ("resizable".equals(name) || "state".equals(name)) {
				final Frame frame = getFrame();

				if (frame != null) {
					setState(frame.getExtendedState(), true);
				}

				if ("resizable".equals(name)) {
					getRootPane().repaint();
				}
			} else if ("title".equals(name)) {
				repaint();
			} else if ("componentOrientation".equals(name)) {
				revalidate();
				repaint();
			} else if ("iconImage".equals(name)) {
				updateSystemIcon();
				revalidate();
				repaint();
			}
		}
	}
	/**
	 * Actions used to <code>restore</code> the <code>Frame</code>.
	 */
	private class RestoreAction extends AbstractAction {
		public RestoreAction() {
			super(UIManager.getString("MetalTitlePane.restoreTitle",
					getLocale()));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			restore();
		}
	}
	/**
	 * Class responsible for drawing the system menu. Looks up the image to draw
	 * from the Frame associated with the <code>JRootPane</code>.
	 */
	private class SystemMenuBar extends JMenuBar {

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			if (systemIcon != null) {
				return new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT);
			} else {
				final Icon icon = UIManager.getIcon("InternalFrame.icon");

				if (icon != null) {
					return new Dimension(icon.getIconWidth(),
							icon.getIconHeight());
				}

				final Dimension size = super.getPreferredSize();

				return new Dimension(Math.max(IMAGE_WIDTH, size.width),
						Math.max(size.height, IMAGE_HEIGHT));
			}
		}

		@Override
		public void paint(final Graphics g) {
			final int height = getHeight();

			if (systemIcon != null) {
				g.drawImage(systemIcon, 0, (height - IMAGE_HEIGHT) / 2 + 1,
						IMAGE_WIDTH, IMAGE_HEIGHT, null);
			} else {
				final Icon icon = UIManager.getIcon("InternalFrame.icon");

				if (icon != null) {
					icon.paintIcon(this, g, 0,
							(height - icon.getIconHeight()) / 2 + 1);
				}
			}
		}
	}

	/**
	 * This inner class is marked &quot;public&quot; due to a compiler bug. This
	 * class should be treated as a &quot;protected&quot; inner class.
	 * Instantiate it only within subclasses of <Foo>.
	 */
	private class TitlePaneLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(final String name, final Component c) {
		}

		private int computeHeight() {
			final Window window = getWindow();

			if (window instanceof Frame) {
				setMaximizeBounds(getFrame());

				return TinyFrameBorder.FRAME_TITLE_HEIGHT;
			} else if (window instanceof Dialog) {
				return TinyFrameBorder.FRAME_TITLE_HEIGHT;
			} else {
				return TinyFrameBorder.FRAME_INTERNAL_TITLE_HEIGHT;
			}
		}

		@Override
		public void layoutContainer(final Container c) {
			if (getWindowDecorationStyle() == JRootPane.NONE) {
				return;
			}

			final boolean leftToRight = (window == null) ? getRootPane()
					.getComponentOrientation().isLeftToRight() : window
					.getComponentOrientation().isLeftToRight();

					final int w = getWidth();
					int x;
					int spacing;
					int buttonHeight;
					int buttonWidth;

					if (closeButton != null) {
						buttonHeight = closeButton.getPreferredSize().height;
						buttonWidth = closeButton.getPreferredSize().width;
					} else {
						buttonHeight = IMAGE_HEIGHT;
						buttonWidth = IMAGE_WIDTH;
					}

					// Changed with 1.4.0: For frames and dialogs, move buttons one
					// pixel down.
					final int yd = (getHeight() == TinyFrameBorder.FRAME_TITLE_HEIGHT ? 1 : 0);
					final int y = (getHeight() - buttonHeight) / 2 + yd;

					// assumes all buttons have the same dimensions,
					// these dimensions include the borders
					spacing = 5;
					x = leftToRight ? spacing : w - buttonWidth - spacing;

					if (menuBar != null) {
						menuBar.setBounds(x, y, buttonWidth, buttonHeight);
					}

					x = leftToRight ? w : 0;
					spacing = 2;
					x += leftToRight ? -spacing - buttonWidth : spacing;

					if (closeButton != null) {
						closeButton.setBounds(x, y, buttonWidth, buttonHeight);
					}

					if (!leftToRight)
						x += buttonWidth;

					if (Toolkit.getDefaultToolkit().isFrameStateSupported(
							Frame.MAXIMIZED_BOTH)) {
						if (toggleButton.getParent() != null) {
							x += leftToRight ? -spacing - buttonWidth : spacing;
							toggleButton.setBounds(x, y, buttonWidth, buttonHeight);

							if (!leftToRight) {
								x += buttonWidth;
							}
						}
					}

					if (iconifyButton != null && iconifyButton.getParent() != null) {
						x += leftToRight ? -spacing - buttonWidth : spacing;
						iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);

						if (!leftToRight) {
							x += buttonWidth;
						}
					}
		}

		@Override
		public Dimension minimumLayoutSize(final Container c) {
			return preferredLayoutSize(c);
		}

		@Override
		public Dimension preferredLayoutSize(final Container c) {
			return new Dimension(TinyLookAndFeel.MINIMUM_FRAME_WIDTH,
					computeHeight());
		}

		@Override
		public void removeLayoutComponent(final Component c) {
		}
	}

	/**
	 * WindowListener installed on the Window, updates the state as necessary.
	 */
	private class WindowHandler extends WindowAdapter {
		@Override
		public void windowActivated(final WindowEvent ev) {
			setActive(true);
		}

		@Override
		public void windowDeactivated(final WindowEvent ev) {
			setActive(false);
		}
	}

	class WindowMoveListener extends ComponentAdapter {

		@Override
		public void componentMoved(final ComponentEvent e) {
			if (getWindowDecorationStyle() == JRootPane.NONE)
				return;

			// paint the non-opaque upper edges
			final Window w = getWindow();

			if (!w.isShowing())
				return;

			w.repaint(0, 0, w.getWidth(), 5);
		}

		@Override
		public void componentResized(final ComponentEvent e) {
			if (getWindowDecorationStyle() == JRootPane.NONE)
				return;

			// paint the non-opaque upper edges
			final Window w = getWindow();

			if (!w.isShowing())
				return;

			w.repaint(0, 0, w.getWidth(), 5);
		}
	}

	/* cache for already painted captions */
	private static final HashMap cache = new HashMap();

	/* Used to paint window buttons. */
	public static Color buttonUpperColor, buttonLowerColor;

	private static final int IMAGE_HEIGHT = 16;

	private static final int IMAGE_WIDTH = 16;

	private static TinyWindowButtonUI iconButtonUI;

	private static TinyWindowButtonUI maxButtonUI;

	private static TinyWindowButtonUI closeButtonUI;

	public static void clearCache() {
		cache.clear();
	}

	/**
	 * PropertyChangeListener added to the JRootPane.
	 */
	private PropertyChangeListener propertyChangeListener;

	/**
	 * JMenuBar, typically renders the system menu items.
	 */
	private JMenuBar menuBar;

	private Image systemIcon;

	/**
	 * Action used to close the Window.
	 */
	private Action closeAction;

	/**
	 * Action used to iconify the Frame.
	 */
	private Action iconifyAction;

	/**
	 * Action to restore the Frame size.
	 */
	private Action restoreAction;

	/**
	 * Action to restore the Frame size.
	 */
	private Action maximizeAction;

	/**
	 * Button used to maximize or restore the Frame.
	 */
	private JButton toggleButton;

	/**
	 * Button used to maximize or restore the Frame.
	 */
	private JButton iconifyButton;

	/**
	 * Button used to maximize or restore the Frame.
	 */
	private JButton closeButton;

	/**
	 * Listens for changes in the state of the Window listener to update the
	 * state of the widgets.
	 */
	private WindowListener windowListener;

	private ComponentListener windowMoveListener;

	/**
	 * Window we're currently in.
	 */
	private Window window;

	/**
	 * JRootPane rendering for.
	 */
	private final JRootPane rootPane;

	/**
	 * Buffered Frame.state property. As state isn't bound, this is kept to
	 * determine when to avoid updating widgets.
	 */
	private int state;

	public TinyTitlePane(final JRootPane root, final TinyRootPaneUI ui) {
		rootPane = root;
		state = -1;

		installSubcomponents();
		installDefaults();
		setLayout(createLayout());
	}

	@Override
	public void addNotify() {
		super.addNotify();

		uninstallListeners();

		window = SwingUtilities.getWindowAncestor(this);

		if (window != null) {
			if (window instanceof Frame) {
				setState(((Frame) window).getExtendedState());
			} else {
				setState(0);
			}

			setActive(window.isActive());
			installListeners();
			updateSystemIcon();
		}
	}

	/**
	 * Adds the necessary <code>JMenuItem</code>s to the passed in menu.
	 */
	private void addSystemMenuItems(final JMenu menu, final boolean isFrame) {
		getRootPane().getLocale();
		JMenuItem item = null;

		if (isFrame) {
			item = menu.add(restoreAction);
			item.setIcon(MenuItemIconFactory.getSystemRestoreIcon());
			int mnemonic = getInt("MetalTitlePane.restoreMnemonic", -1);
			if (mnemonic != -1) {
				item.setMnemonic(mnemonic);
			}

			item = menu.add(iconifyAction);
			item.setIcon(MenuItemIconFactory.getSystemIconifyIcon());
			mnemonic = getInt("MetalTitlePane.iconifyMnemonic", -1);
			if (mnemonic != -1) {
				item.setMnemonic(mnemonic);
			}

			if (Toolkit.getDefaultToolkit().isFrameStateSupported(
					Frame.MAXIMIZED_BOTH)) {
				item = menu.add(maximizeAction);
				item.setIcon(MenuItemIconFactory.getSystemMaximizeIcon());
				mnemonic = getInt("MetalTitlePane.maximizeMnemonic", -1);
				if (mnemonic != -1) {
					item.setMnemonic(mnemonic);
				}
			}

			menu.addSeparator();
		}

		item = menu.add(closeAction);
		item.setIcon(MenuItemIconFactory.getSystemCloseIcon());
		final int mnemonic = getInt("MetalTitlePane.closeMnemonic", -1);

		if (mnemonic != -1) {
			item.setMnemonic(mnemonic);
		}
	}

	/**
	 * Convenience method to clip the passed in text to the specified size.
	 */
	private String clippedText(String text, final FontMetrics fm, final int availTextWidth) {
		if ((text == null) || (text.equals(""))) {
			return "";
		}
		final int textWidth = SwingUtilities.computeStringWidth(fm, text);
		final String clipString = "...";
		if (textWidth > availTextWidth) {
			int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
			int nChars;
			for (nChars = 0; nChars < text.length(); nChars++) {
				totalWidth += fm.charWidth(text.charAt(nChars));
				if (totalWidth > availTextWidth) {
					break;
				}
			}
			text = text.substring(0, nChars) + clipString;
		}
		return text;
	}

	/**
	 * Closes the Window.
	 */
	private void close() {
		final Window window = getWindow();

		if (window != null) {
			window.dispatchEvent(new WindowEvent(window,
					WindowEvent.WINDOW_CLOSING));
		}
	}

	/**
	 * Create the <code>Action</code>s that get associated with the buttons and
	 * menu items.
	 */
	private void createActions() {
		closeAction = new CloseAction();
		iconifyAction = new IconifyAction();
		restoreAction = new RestoreAction();
		maximizeAction = new MaximizeAction();
	}

	/**
	 * Creates the buttons of the title pane and initializes their actions.
	 */
	protected void createButtons(final int decorationStyle) {
		if (iconButtonUI == null) {
			iconButtonUI = TinyWindowButtonUI
			.createButtonUIForType(TinyWindowButtonUI.MINIMIZE);
			maxButtonUI = TinyWindowButtonUI
			.createButtonUIForType(TinyWindowButtonUI.MAXIMIZE);
			closeButtonUI = TinyWindowButtonUI
			.createButtonUIForType(TinyWindowButtonUI.CLOSE);
		}

		iconifyButton = new SpecialUIButton(iconButtonUI);
		iconifyButton.setAction(iconifyAction);
		iconifyButton.setText(null);
		iconifyButton.setRolloverEnabled(true);

		toggleButton = new SpecialUIButton(maxButtonUI);
		toggleButton.setAction(maximizeAction);
		toggleButton.setText(null);
		toggleButton.setRolloverEnabled(true);

		closeButton = new SpecialUIButton(closeButtonUI);
		closeButton.setAction(closeAction);
		closeButton.setText(null);
		closeButton.setRolloverEnabled(true);

		closeButton.getAccessibleContext().setAccessibleName("Close");
		iconifyButton.getAccessibleContext().setAccessibleName("Iconify");
		toggleButton.getAccessibleContext().setAccessibleName("Maximize");

		// Note: Not checking the decorationStyle here was a bug before 1.4.0
		// because window buttons were set even if dialogs were created.
		if (TinyLookAndFeel.controlPanelInstantiated
				&& decorationStyle == JRootPane.FRAME) {
			ControlPanel.setWindowButtons(new JButton[] { iconifyButton,
					toggleButton, closeButton });
		}
	}

	/**
	 * Returns the <code>LayoutManager</code> that should be installed on the
	 * <code>TinyTitlePane</code>.
	 */
	private LayoutManager createLayout() {
		return new TitlePaneLayout();
	}

	/**
	 * Returns the <code>JMenu</code> displaying the appropriate menu items for
	 * manipulating the Frame.
	 */
	private JMenu createMenu() {
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
				if (windowHasMenuBar()) {
					TinyMenuUI.systemMenuShowing = true;
				}
			}
		});

		if (getWindowDecorationStyle() == JRootPane.FRAME) {
			addSystemMenuItems(menu, true);
			// We use this property to prevent the menu from drawing rollovers
			menu.putClientProperty(TinyMenuUI.IS_SYSTEM_MENU_KEY, Boolean.TRUE);
		} else if (getWindowDecorationStyle() != JRootPane.NONE) {
			addSystemMenuItems(menu, false);
			// we use this property to prevent the Menu from drawing rollovers
			menu.putClientProperty(TinyMenuUI.IS_SYSTEM_MENU_KEY, Boolean.TRUE);
		}

		return menu;
	}

	/**
	 * Returns the <code>JMenuBar</code> displaying the appropriate system menu
	 * items.
	 */
	protected JMenuBar createMenuBar() {
		menuBar = new SystemMenuBar();
		menuBar.setFocusable(false);
		menuBar.setBorderPainted(true);
		menuBar.add(createMenu());

		return menuBar;
	}

	/**
	 * Returns the <code>WindowListener</code> to add to the <code>Window</code>
	 * .
	 */
	private WindowListener createWindowListener() {
		return new WindowHandler();
	}

	/**
	 * Returns the <code>PropertyChangeListener</code> to install on the
	 * <code>Window</code>.
	 */
	private PropertyChangeListener createWindowPropertyChangeListener() {
		return new PropertyChangeHandler();
	}

	/**
	 * Returns the Frame rendering in. This will return null if the
	 * <code>JRootPane</code> is not contained in a <code>Frame</code>.
	 */
	private Frame getFrame() {
		final Window window = getWindow();

		if (window instanceof Frame) {
			return (Frame) window;
		}

		return null;
	}

	private int getInt(final Object key, final int defaultValue) {
		final Object value = UIManager.get(key);

		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (final NumberFormatException nfe) {
			}
		}
		return defaultValue;
	}

	/**
	 * Returns the <code>JRootPane</code> this was created for.
	 */
	@Override
	public JRootPane getRootPane() {
		return rootPane;
	}

	/**
	 * Returns the String to display as the title.
	 */
	private String getTitle() {
		final Window w = getWindow();

		if (w instanceof Frame) {
			return ((Frame) w).getTitle();
		} else if (w instanceof Dialog) {
			return ((Dialog) w).getTitle();
		}

		return null;
	}

	/**
	 * Returns the <code>Window</code> the <code>JRootPane</code> is contained
	 * in. This will return null if there is no parent ancestor of the
	 * <code>JRootPane</code>.
	 */
	private Window getWindow() {
		return window;
	}

	/**
	 * Returns the decoration style of the <code>JRootPane</code>.
	 */
	private int getWindowDecorationStyle() {
		return getRootPane().getWindowDecorationStyle();
	}

	private Image getWindowIcon(final Window window) {
		if (window == null)
			return null;

		if (window instanceof Frame) {
			return ((Frame) window).getIconImage();
		} else {
			try {
				// available since java 1.6
				final Method getIconImages = window.getClass().getMethod(
						"getIconImages", (Class[]) null); // NOI18N
				final List icons = (List) getIconImages.invoke(window,
						(Object[]) null);

				if (icons != null) {
					if (icons.size() == 0) {
						return getWindowIcon(window.getOwner());
					} else if (icons.size() == 1) {
						return (Image) icons.get(0);
					} else {
						for (int i = 0; i < icons.size(); i++) {
							final Image img = (Image) icons.get(i);

							if (img.getWidth(this) == IMAGE_WIDTH
									&& img.getHeight(this) == IMAGE_HEIGHT) {
								return img;
							}
						}

						return ((Image) icons.get(0)).getScaledInstance(
								IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
					}
				}
			} catch (final Exception ex) {
				// no hope to get icon for this window :(
				// return the parent icon
				return getWindowIcon(window.getOwner());
			}
		}

		return null;
	}

	/**
	 * Iconifies the Frame.
	 */
	private void iconify() {
		final Frame frame = getFrame();

		if (frame != null) {
			frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
		}
	}

	/**
	 * Installs the fonts and necessary properties on the TinyTitlePane.
	 */
	private void installDefaults() {
		setFont(UIManager.getFont("Frame.titleFont", getLocale()));
	}

	/**
	 * Installs the necessary listeners.
	 */
	private void installListeners() {
		if (window != null) {
			windowListener = createWindowListener();
			window.addWindowListener(windowListener);
			propertyChangeListener = createWindowPropertyChangeListener();
			window.addPropertyChangeListener(propertyChangeListener);
			windowMoveListener = new WindowMoveListener();
			window.addComponentListener(windowMoveListener);

			if (window instanceof JDialog) {
				TinyPopupFactory.addDialog((JDialog) window);
			}
		}
	}

	/**
	 * Adds any sub-Components contained in the <code>TinyTitlePane</code>.
	 */
	private void installSubcomponents() {
		final int decorationStyle = getWindowDecorationStyle();

		// New in 1.4.0: Create system menu bar for frames and plain dialogs
		if (decorationStyle == JRootPane.FRAME) {
			createActions();
			menuBar = createMenuBar();
			add(menuBar);
			createButtons(decorationStyle);
			add(iconifyButton);
			add(toggleButton);
			add(closeButton);
			iconifyButton.putClientProperty(
					TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
			toggleButton.putClientProperty(
					TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
			closeButton.putClientProperty(
					TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
		} else if (decorationStyle == JRootPane.PLAIN_DIALOG
				|| decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG
				|| decorationStyle == JRootPane.FILE_CHOOSER_DIALOG) {
			createActions();
			menuBar = createMenuBar();
			add(menuBar);
			createButtons(decorationStyle);
			add(closeButton);
			closeButton.putClientProperty(
					TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
		} else if (decorationStyle == JRootPane.INFORMATION_DIALOG
				|| decorationStyle == JRootPane.ERROR_DIALOG
				|| decorationStyle == JRootPane.QUESTION_DIALOG
				|| decorationStyle == JRootPane.WARNING_DIALOG) {
			createActions();
			createButtons(decorationStyle);
			add(closeButton);
			closeButton.putClientProperty(
					TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
		}
	}

	public boolean isFrameMaximized() {
		final Frame frame = getFrame();

		if (frame != null) {
			return ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH);
		}

		return false;
	}

	public boolean isSelected() {
		final Window window = getWindow();
		return (window == null) ? true : window.isActive();
	}

	/**
	 * Maximizes the Frame.
	 */
	private void maximize() {
		final Frame frame = getFrame();

		if (frame != null) {
			setMaximizeBounds(frame);
			frame.setExtendedState(frame.getExtendedState()
					| Frame.MAXIMIZED_BOTH);
		}
	}

	private void paintCaption(final Graphics g, final int w, final int h, final boolean isActive,
			final int titleHeight, final Window window) {
		if (TinyLookAndFeel.controlPanelInstantiated) {
			paintXPCaptionNoCache(g, w, h, isActive, titleHeight, window);
		} else {
			paintXPCaption(g, w, h, isActive, titleHeight, window);
		}
	}

	/**
	 * Renders the TitlePane.
	 */
	@Override
	public void paintComponent(final Graphics g) {
		// As state isn't bound, we need a convenience place to check
		// if it has changed.
		if (getFrame() != null) {
			setState(getFrame().getExtendedState());
		}

		final Window window = getWindow();
		final boolean leftToRight = (window == null) ? getRootPane()
				.getComponentOrientation().isLeftToRight() : window
				.getComponentOrientation().isLeftToRight();
				final boolean isActive = (window == null) ? true : window.isActive();
				final int width = getWidth();
				final int height = getHeight();
				int xOffset = leftToRight ? 5 : width - 5;

				// New in 1.4.0: Since JRE 1.6.0_10-beta we must paint the
				// frame caption from here instead of painting it as part
				// of the frame border
				paintCaption(g, width, height, isActive,
						TinyFrameBorder.FRAME_TITLE_HEIGHT, window);

				// Changed with 1.4.0: Non-optionPane dialogs have a system menu
				final int decorationStyle = getWindowDecorationStyle();

				if (decorationStyle == JRootPane.FRAME
						|| decorationStyle == JRootPane.PLAIN_DIALOG
						|| decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG
						|| decorationStyle == JRootPane.FILE_CHOOSER_DIALOG) {
					xOffset += leftToRight ? IMAGE_WIDTH + 5 : -IMAGE_WIDTH - 5;
				}

				String theTitle = getTitle();

				if (theTitle != null) {
					final FontMetrics fm = g.getFontMetrics();

					// Changed with 1.4.0: For frames and dialogs, move text two pixels
					// down.
					final int yd = (height == TinyFrameBorder.FRAME_TITLE_HEIGHT ? 2 : 0);
					final int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent() + yd;
					Rectangle rect = new Rectangle(0, 0, 0, 0);

					if (iconifyButton != null && iconifyButton.getParent() != null) {
						rect = iconifyButton.getBounds();
					}

					int titleW;

					if (leftToRight) {
						if (rect.x == 0) {
							rect.x = window.getWidth() - window.getInsets().right - 2;
						}

						titleW = rect.x - xOffset - 4;
						theTitle = clippedText(theTitle, fm, titleW);
					} else {
						titleW = xOffset - rect.x - rect.width - 4;
						theTitle = clippedText(theTitle, fm, titleW);
						xOffset -= SwingUtilities.computeStringWidth(fm, theTitle);
					}

					SwingUtilities.computeStringWidth(fm, theTitle);

					// New in 1.4.0: Title text painted antialiased
					if (g instanceof Graphics2D) {
						final Graphics2D g2d = (Graphics2D) g;
						g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					}

					if (isActive) {
						// Paint shadow
						g.setColor(Theme.frameTitleShadowColor.getColor());
						g.drawString(theTitle, xOffset + 1, yOffset + 1);

						g.setColor(Theme.frameTitleColor.getColor());
						g.drawString(theTitle, xOffset, yOffset);
					} else {
						// for an inactive window
						g.setColor(Theme.frameTitleDisabledColor.getColor());
						g.drawString(theTitle, xOffset, yOffset);
					}
				}
	}

	private void paintXPCaption(final Graphics g, final int w, final int h, final boolean isActive,
			final int titleHeight, final Window window) {
		Color c = null;

		if (isActive) {
			c = Theme.frameCaptionColor.getColor();
		} else {
			c = Theme.frameCaptionDisabledColor.getColor();
		}

		g.setColor(c);

		final int x = 0;
		final int y = 0;
		int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		int y2 = y;
		Color borderColor = null;

		if (isActive) {
			borderColor = Theme.frameBorderColor.getColor();
			spread1 = Theme.frameSpreadDark.getValue();
			spread2 = Theme.frameSpreadLight.getValue();
		} else {
			borderColor = Theme.frameBorderDisabledColor.getColor();
		}

		// always paint the semi-transparent parts
		// 1
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		y2++;
		// 2
		final Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x, y2, x + 2, y2); // left
		g.drawLine(x + w - 3, y2, x + w - 1, y2); // right
		y2++;
		// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2); // left
		g.drawLine(x + w - 3, y2, x + w - 1, y2); // right
		// darker border
		g.setColor(c);
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		y2++;
		// 4
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		y2++;
		// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 2, y2, x + 2, y2); // left
		g.drawLine(x + x + w - 3, y2, x + w - 3, y2); // right
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(c);
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		y2++;

		// now either paint from cache or create cached image
		final CaptionKey key = new CaptionKey(Theme.frameCaptionColor.getColor(),
				isActive, titleHeight);
		final Object value = cache.get(key);

		if (value != null) {
			// image is cached - paint and return
			g.drawImage((Image) value, x + 3, y, x + w - 3, y + 5, 0, 0, 1, 5,
					window);
			g.drawImage((Image) value, x, y + 5, x + w, y + titleHeight, 0, 5,
					1, titleHeight, window);

			// store button colors
			buttonUpperColor = ColorRoutines.darken(c, 4 * spread1);
			buttonLowerColor = ColorRoutines.lighten(c, 10 * spread2);

			return;
		}

		final Image img = new BufferedImage(1, titleHeight,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics imgGraphics = img.getGraphics();
		// 1
		imgGraphics.setColor(borderColor);
		imgGraphics.drawLine(0, 0, 1, 0);
		// 2
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 1, 1, 1);
		// 3
		imgGraphics.setColor(ColorRoutines.lighten(c, 10 * spread2));
		imgGraphics.drawLine(0, 2, 1, 2);
		// 4
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 3, 1, 3);
		// 5
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 4, 1, 4);
		// 6
		buttonUpperColor = ColorRoutines.darken(c, 4 * spread1);
		imgGraphics.setColor(buttonUpperColor);
		imgGraphics.drawLine(0, 5, 1, 5);
		// 7 - 8
		imgGraphics.setColor(ColorRoutines.darken(c, 4 * spread1));
		imgGraphics.drawLine(0, 6, 1, 6);
		imgGraphics.drawLine(0, 7, 1, 7);
		// 9 - 12
		imgGraphics.setColor(ColorRoutines.darken(c, 3 * spread1));
		imgGraphics.drawLine(0, 8, 1, 8);
		imgGraphics.drawLine(0, 9, 1, 9);
		imgGraphics.drawLine(0, 10, 1, 10);
		imgGraphics.drawLine(0, 11, 1, 11);
		// 13 - 15
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 12, 1, 12);
		imgGraphics.drawLine(0, 13, 1, 13);
		imgGraphics.drawLine(0, 14, 1, 14);
		// 16 - 17
		imgGraphics.setColor(ColorRoutines.darken(c, spread1));
		imgGraphics.drawLine(0, 15, 1, 15);
		imgGraphics.drawLine(0, 16, 1, 16);
		// 18 - 19
		imgGraphics.setColor(c);
		imgGraphics.drawLine(0, 17, 1, 17);
		imgGraphics.drawLine(0, 18, 1, 18);
		// 20...
		imgGraphics.setColor(ColorRoutines.lighten(c, 2 * spread2));
		imgGraphics.drawLine(0, 19, 1, 19);
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 20, 1, 20);
		imgGraphics.setColor(ColorRoutines.lighten(c, 5 * spread2));
		imgGraphics.drawLine(0, 21, 1, 21);
		imgGraphics.setColor(ColorRoutines.lighten(c, 6 * spread2));
		imgGraphics.drawLine(0, 22, 1, 22);
		imgGraphics.setColor(ColorRoutines.lighten(c, 8 * spread2));
		imgGraphics.drawLine(0, 23, 1, 23);
		imgGraphics.setColor(ColorRoutines.lighten(c, 9 * spread2));
		imgGraphics.drawLine(0, 24, 1, 24);
		// Note: Not specifying buttonLowerColor here was
		// a bug before 1.4.0
		buttonLowerColor = ColorRoutines.lighten(c, 10 * spread2);
		imgGraphics.setColor(buttonLowerColor);
		imgGraphics.drawLine(0, 25, 1, 25);
		// 27
		imgGraphics.setColor(ColorRoutines.lighten(c, 4 * spread2));
		imgGraphics.drawLine(0, 26, 1, 26);
		// 28
		imgGraphics.setColor(ColorRoutines.darken(c, 2 * spread1));
		imgGraphics.drawLine(0, 27, 1, 27);
		// 29
		if (isActive) {
			imgGraphics.setColor(Theme.frameLightColor.getColor());
		} else {
			imgGraphics.setColor(Theme.frameLightDisabledColor.getColor());
		}
		imgGraphics.drawLine(0, 28, 1, 28);

		// dispose of image graphics
		imgGraphics.dispose();

		// paint image
		g.drawImage(img, x + 3, y, x + w - 3, y + 5, 0, 0, 1, 5, window);
		g.drawImage(img, x, y + 5, x + w, y + titleHeight, 0, 5, 1,
				titleHeight, window);

		// add the image to the cache
		cache.put(key, img);

		if (TinyLookAndFeel.PRINT_CACHE_SIZES) {
			System.out.println("TinyFrameBorder.cache.size=" + cache.size());
		}
	}

	private void paintXPCaptionNoCache(final Graphics g, final int w, final int h,
			final boolean isActive, final int titleHeight, final Window window) {
		Color c = null;

		if (isActive) {
			c = Theme.frameCaptionColor.getColor();
		} else {
			c = Theme.frameCaptionDisabledColor.getColor();
		}

		g.setColor(c);

		final int x = 0;
		final int y = 0;
		int spread1 = Theme.frameSpreadDarkDisabled.getValue();
		int spread2 = Theme.frameSpreadLightDisabled.getValue();
		int y2 = y;
		Color borderColor = null;

		if (isActive) {
			borderColor = Theme.frameBorderColor.getColor();
			spread1 = Theme.frameSpreadDark.getValue();
			spread2 = Theme.frameSpreadLight.getValue();
		} else {
			borderColor = Theme.frameBorderDisabledColor.getColor();
		}

		// paint the semi-transparent parts
		// 1
		// blend
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 82));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 156));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.getAlphaColor(borderColor, 215));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		y2++;
		// 2
		final Color c2 = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(c2);
		g.drawLine(x, y2, x + 2, y2); // left
		g.drawLine(x + w - 3, y2, x + w - 1, y2); // right
		y2++;
		// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 1, y2, x + 2, y2); // left
		g.drawLine(x + w - 3, y2, x + w - 1, y2); // right
		// darker border
		g.setColor(c);
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		y2++;
		// 4
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(ColorRoutines.lighten(c, 7 * spread2));
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);
		g.setColor(ColorRoutines.lighten(c, 3 * spread2));
		g.drawLine(x + 2, y2, x + 2, y2);
		g.drawLine(x + w - 3, y2, x + w - 3, y2);
		y2++;
		// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 2, y2, x + 2, y2); // left
		g.drawLine(x + x + w - 3, y2, x + w - 3, y2); // right
		// blend from lightest color
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x, y2, x, y2);
		g.drawLine(x + w - 1, y2, x + w - 1, y2);
		g.setColor(c);
		g.drawLine(x + 1, y2, x + 1, y2);
		g.drawLine(x + w - 2, y2, x + w - 2, y2);

		// paint solid lines
		y2 = y;
		// 1
		g.setColor(borderColor);
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2++;
		// 2
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2++;

		// 3
		g.setColor(ColorRoutines.lighten(c, 10 * spread2));
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2++;
		// 4
		g.setColor(c);
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2++;
		// 5
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x + 3, y2, x + w - 4, y2);
		y2++;
		// 6
		buttonUpperColor = ColorRoutines.darken(c, 4 * spread1);
		g.setColor(buttonUpperColor);
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		// 7 - 8
		g.setColor(ColorRoutines.darken(c, 4 * spread1));
		g.fillRect(x, y2, x + w, 2);
		y2 += 2;
		// 9 - 12
		g.setColor(ColorRoutines.darken(c, 3 * spread1));
		g.fillRect(x, y2, x + w, 4);
		y2 += 4;
		// 13 - 15
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.fillRect(x, y2, x + w, 3);
		y2 += 3;
		// 16 - 17
		g.setColor(ColorRoutines.darken(c, 1 * spread1));
		g.fillRect(x, y2, x + w, 2);
		y2 += 2;
		// 18 - 19
		g.setColor(c);
		g.fillRect(x, y2, x + w, 2);
		y2 += 2;
		// 20...
		g.setColor(ColorRoutines.lighten(c, 2 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		g.setColor(ColorRoutines.lighten(c, 5 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		g.setColor(ColorRoutines.lighten(c, 6 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		g.setColor(ColorRoutines.lighten(c, 8 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		g.setColor(ColorRoutines.lighten(c, 9 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		buttonLowerColor = ColorRoutines.lighten(c, 10 * spread2);
		g.setColor(buttonLowerColor);
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		// 27
		g.setColor(ColorRoutines.lighten(c, 4 * spread2));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		// 28
		g.setColor(ColorRoutines.darken(c, 2 * spread1));
		g.drawLine(x, y2, x + w - 1, y2);
		y2++;
		// 29
		if (isActive) {
			g.setColor(Theme.frameLightColor.getColor());
		} else {
			g.setColor(Theme.frameLightDisabledColor.getColor());
		}

		g.drawLine(x, y2, x + w - 1, y2);
	}

	@Override
	public void removeNotify() {
		super.removeNotify();

		uninstallListeners();
		window = null;
	}

	/**
	 * Restores the Frame size.
	 */
	private void restore() {
		final Frame frame = getFrame();

		if (frame == null) {
			return;
		}

		if ((frame.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
			frame.setExtendedState(state & ~Frame.ICONIFIED);
		} else {
			frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
		}
	}

	/**
	 * Updates state dependant upon the Window's active state.
	 */
	private void setActive(final boolean isActive) {
		if (getWindowDecorationStyle() == JRootPane.FRAME) {
			final Boolean activeB = isActive ? Boolean.TRUE : Boolean.FALSE;

			iconifyButton.putClientProperty("paintActive", activeB);
			closeButton.putClientProperty("paintActive", activeB);
			toggleButton.putClientProperty("paintActive", activeB);

			iconifyButton.setEnabled(isActive);
			closeButton.setEnabled(isActive);
			toggleButton.setEnabled(isActive);
		}
		// Repaint the whole thing as the Borders that are used have
		// different colors for active vs inactive
		getRootPane().repaint();
	}

	protected void setMaximizeBounds(final Frame frame) {
		// Changed in 1.4.0 to calculate the available screen area each time.
		final Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				getGraphicsConfiguration());
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// spare any Systemmenus or Taskbars or ??...
		final int w = screenSize.width - screenInsets.left - screenInsets.right;
		final int h = screenSize.height - screenInsets.top - screenInsets.bottom;
		final Rectangle maxBounds = new Rectangle(screenInsets.left,
				screenInsets.top, w, h);
		// System.out.println(getTitle() + ".screenSize=" + screenSize +
		// "\n screenInsets=" + screenInsets +
		// "\n  maxBounds=" + maxBounds);

		frame.setMaximizedBounds(maxBounds);
	}

	/**
	 * Sets the state of the Window.
	 */
	private void setState(final int state) {
		setState(state, false);
	}

	/**
	 * Sets the state of the window. If <code>updateRegardless</code> is true
	 * and the state has not changed, this will update anyway.
	 */
	private void setState(final int state, final boolean updateRegardless) {
		final Window w = getWindow();

		if (w != null && getWindowDecorationStyle() == JRootPane.FRAME) {
			if (this.state == state && !updateRegardless) {
				return;
			}
			final Frame frame = getFrame();

			if (frame != null) {
				final JRootPane rootPane = getRootPane();

				if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH
						&& (rootPane.getBorder() == null || (rootPane
								.getBorder() instanceof UIResource))
								&& frame.isShowing()) {
					// rootPane.setBorder(null);
				} else if ((state & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
					// This is a croak, if state becomes bound, this can
					// be nuked.
					// rootPaneUI.installBorder(rootPane);
				}
				if (frame.isResizable()) {
					if ((state & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT
							|| (state & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ) {
						updateToggleButton(restoreAction);
						maximizeAction.setEnabled(false);
						restoreAction.setEnabled(true);
					} else {
						updateToggleButton(maximizeAction);
						maximizeAction.setEnabled(true);
						restoreAction.setEnabled(false);
					}
					if (toggleButton.getParent() == null
							|| iconifyButton.getParent() == null) {
						add(toggleButton);
						add(iconifyButton);
						revalidate();
						repaint();
					}
					toggleButton.setText(null);
				} else {
					maximizeAction.setEnabled(false);
					restoreAction.setEnabled(false);
					if (toggleButton.getParent() != null) {
						remove(toggleButton);
						revalidate();
						repaint();
					}
				}
			} else {
				// Not contained in a Frame
				maximizeAction.setEnabled(false);
				restoreAction.setEnabled(false);
				iconifyAction.setEnabled(false);
				remove(toggleButton);
				remove(iconifyButton);
				revalidate();
				repaint();
			}

			closeAction.setEnabled(true);
			this.state = state;
		}
	}

	/**
	 * Uninstalls the necessary listeners.
	 */
	private void uninstallListeners() {
		if (window != null) {
			window.removeWindowListener(windowListener);
			window.removePropertyChangeListener(propertyChangeListener);
			window.removeComponentListener(windowMoveListener);
		}
	}

	/**
	 * Update the image used for the system icon
	 */
	private void updateSystemIcon() {
		systemIcon = getWindowIcon(getWindow());
		// System.out.println(getTitle() + ".updateSystemIcon: " + systemIcon +
		// " at " + System.currentTimeMillis());
		// new Exception(getTitle() + ".updateSystemIcon: " +
		// systemIcon).printStackTrace();
	}

	/**
	 * Updates the toggle button to contain the Icon <code>icon</code>, and
	 * Action <code>action</code>.
	 */
	private void updateToggleButton(final Action action) {
		toggleButton.setAction(action);
		toggleButton.setText(null);
	}

	private boolean windowHasMenuBar() {
		final Window w = getWindow();

		if (w instanceof JFrame) {
			return ((JFrame) w).getJMenuBar() != null;
		} else if (w instanceof JDialog) {
			return ((JDialog) w).getJMenuBar() != null;
		}

		return false;
	}
}
