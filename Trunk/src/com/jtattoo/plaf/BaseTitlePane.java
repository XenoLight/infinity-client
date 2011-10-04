/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

/**
 * This class is a modified copy of the javax.swing.plaf.metal.MetalTitlePaneUI
 * 
 * Class that manages a JLF awt.Window-descendant class's title bar.
 * <p>
 * This class assumes it will be created with a particular window decoration
 * style, and that if the style changes, a new one will be created.
 * 
 * @version 1.12 01/23/03
 * @author Terry Kellerman
 * @author Michael Hagen
 * 
 * @since 1.4
 */
public class BaseTitlePane extends JComponent {

	protected class CloseAction extends AbstractAction {

		public CloseAction() {
			super(UIManager.getString("MetalTitlePane.closeTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			close();
		}
	}
	protected class IconifyAction extends AbstractAction {

		public IconifyAction() {
			super(UIManager.getString("MetalTitlePane.iconifyTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			iconify();
		}
	}
	protected class MaximizeAction extends AbstractAction {

		public MaximizeAction() {
			super(UIManager.getString("MetalTitlePane.maximizeTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			maximize();
		}
	}
	// -----------------------------------------------------------------------------------------------
	protected class PropertyChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent pce) {
			final String name = pce.getPropertyName();

			// Frame.state isn't currently bound.
			if ("resizable".equals(name) || "state".equals(name)) {
				final Frame frame = getFrame();
				if (frame != null) {
					setState(DecorationHelper.getExtendedState(frame), true);
				}
				if ("resizable".equals(name)) {
					getRootPane().repaint();
				}
			} else if ("title".equals(name)) {
				repaint();
			} else if ("componentOrientation".equals(name)) {
				revalidate();
				repaint();
			} else if ("windowMoved".equals(name)) {
				final Frame frame = getFrame();
				if (frame != null) {
					final GraphicsConfiguration gc = frame.getGraphicsConfiguration();
					final Rectangle screenBounds = gc.getBounds();
					final Insets screenInsets = Toolkit.getDefaultToolkit()
					.getScreenInsets(gc);
					screenInsets.bottom = Math.max(screenInsets.bottom, 1);
					final int x = Math.max(0, screenInsets.left);
					final int y = Math.max(0, screenInsets.top);
					final int w = screenBounds.width
					- (screenInsets.left + screenInsets.right);
					final int h = screenBounds.height
					- (screenInsets.top + screenInsets.bottom);
					// Keep taskbar visible
					frame.setMaximizedBounds(new Rectangle(x, y, w, h));
				}
			}
		}
	}
	protected class RestoreAction extends AbstractAction {

		public RestoreAction() {
			super(UIManager.getString("MetalTitlePane.restoreTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			restore();
		}
	}
	// -----------------------------------------------------------------------------------------------
	protected class SystemMenuBar extends JMenuBar {

		protected int computeHeight() {
			final FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(
					getFont());
			return fm.getHeight() + 4;
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension size = super.getPreferredSize();
			final Frame frame = getFrame();
			final Image image = (frame != null) ? frame.getIconImage() : null;
			if (image != null) {
				int iw = image.getWidth(null);
				int ih = image.getHeight(null);
				final int th = computeHeight();
				if (ih > th) {
					final double scale = (double) th / (double) ih;
					iw = (int) (scale * iw);
					ih = (int) (scale * ih);
				}
				return new Dimension(Math.max(iw, size.width), Math.max(ih,
						size.height));
			} else {
				return size;
			}
		}

		@Override
		public void paint(final Graphics g) {
			final Image image = getFrameIconImage();
			if (image != null) {
				int iw = image.getWidth(null);
				int ih = image.getHeight(null);
				if (ih > getHeight()) {
					final double scale = (double) getHeight() / (double) ih;
					iw = (int) (scale * iw);
					ih = (int) (scale * ih);
				}
				final int ix = 0;
				final int iy = (getHeight() - ih) / 2;
				g.drawImage(image, ix, iy, iw, ih, null);

			} else {
				final Icon icon = UIManager.getIcon("InternalFrame.icon");
				if (icon != null) {
					icon.paintIcon(this, g, 2, 2);
				}
			}
		}
	}
	// -----------------------------------------------------------------------------------------------
	protected class TitlePaneLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(final String name, final Component c) {
		}

		protected int computeHeight() {
			final FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(
					getFont());
			return fm.getHeight() + 6;
		}

		@Override
		public void layoutContainer(final Container c) {
			final boolean leftToRight = isLeftToRight();

			final int spacing = getHorSpacing();
			final int w = getWidth();
			final int h = getHeight();

			// assumes all buttons have the same dimensions these dimensions
			// include the borders
			final int buttonHeight = h - getVerSpacing();
			final int buttonWidth = buttonHeight;

			int x = leftToRight ? spacing : w - buttonWidth - spacing;
			final int y = Math.max(0, ((h - buttonHeight) / 2) - 1);

			if (menuBar != null) {
				final int mw = menuBar.getPreferredSize().width;
				final int mh = menuBar.getPreferredSize().height;
				if (leftToRight) {
					menuBar.setBounds(2, (h - mh) / 2, mw, mh);
				} else {
					menuBar.setBounds(getWidth() - mw, (h - mh) / 2, mw, mh);
				}
			}

			x = leftToRight ? w - spacing : 0;
			if (closeButton != null) {
				x += leftToRight ? -buttonWidth : spacing;
				closeButton.setBounds(x, y, buttonWidth, buttonHeight);
				if (!leftToRight) {
					x += buttonWidth;
				}
			}

			if ((maxButton != null) && (maxButton.getParent() != null)) {
				if (DecorationHelper.isFrameStateSupported(
						Toolkit.getDefaultToolkit(),
						BaseRootPaneUI.MAXIMIZED_BOTH)) {
					x += leftToRight ? -spacing - buttonWidth : spacing;
					maxButton.setBounds(x, y, buttonWidth, buttonHeight);
					if (!leftToRight) {
						x += buttonWidth;
					}
				}
			}

			if ((iconifyButton != null) && (iconifyButton.getParent() != null)) {
				x += leftToRight ? -spacing - buttonWidth : spacing;
				iconifyButton.setBounds(x, y, buttonWidth, buttonHeight);
				if (!leftToRight) {
					x += buttonWidth;
				}
			}
			buttonsWidth = leftToRight ? w - x : x;
		}

		@Override
		public Dimension minimumLayoutSize(final Container c) {
			return preferredLayoutSize(c);
		}

		@Override
		public Dimension preferredLayoutSize(final Container c) {
			final int height = computeHeight();
			return new Dimension(height, height);
		}

		@Override
		public void removeLayoutComponent(final Component c) {
		}
	}
	// -----------------------------------------------------------------------------------------------
	protected class WindowHandler extends WindowAdapter {

		@Override
		public void windowActivated(final WindowEvent ev) {
			setActive(true);
		}

		@Override
		public void windowDeactivated(final WindowEvent ev) {
			setActive(false);
		}
	}
	public static final String PAINT_ACTIVE = "paintActive";
	public static final String ICONIFY = "Iconify";
	public static final String MAXIMIZE = "Maximize";
	public static final String CLOSE = "Close";
	static int getInt(final Object key, final int defaultValue) {
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
	protected PropertyChangeListener propertyChangeListener;
	protected Action closeAction;
	protected Action iconifyAction;
	protected Action restoreAction;
	protected Action maximizeAction;
	protected JMenuBar menuBar;
	protected JButton iconifyButton;
	protected JButton maxButton;
	protected JButton closeButton;
	protected Icon iconifyIcon;
	protected Icon maximizeIcon;
	protected Icon minimizeIcon;

	protected Icon closeIcon;

	protected WindowListener windowListener;

	protected Window window;

	protected JRootPane rootPane;

	protected BaseRootPaneUI rootPaneUI;

	protected int buttonsWidth;

	protected int state;

	protected BufferedImage backgroundImage = null;

	protected float alphaValue = 0.85f;

	public BaseTitlePane(final JRootPane root, final BaseRootPaneUI ui) {
		this.rootPane = root;
		rootPaneUI = ui;
		state = -1;
		iconifyIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
		maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
		minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
		closeIcon = UIManager.getIcon("InternalFrame.closeIcon");

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
				setState(DecorationHelper.getExtendedState((Frame) window));
			} else {
				setState(0);
			}
			setActive(JTattooUtilities.isWindowActive(window));
			installListeners();
		}
	}

	protected void close() {
		if (window != null) {
			window.dispatchEvent(new WindowEvent(window,
					WindowEvent.WINDOW_CLOSING));
		}
	}

	protected void createActions() {
		closeAction = new CloseAction();
		iconifyAction = new IconifyAction();
		restoreAction = new RestoreAction();
		maximizeAction = new MaximizeAction();
	}

	public void createButtons() {
		iconifyButton = new BaseTitleButton(iconifyAction, ICONIFY,
				iconifyIcon, 1.0f);
		maxButton = new BaseTitleButton(restoreAction, MAXIMIZE, maximizeIcon,
				1.0f);
		closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
	}

	public LayoutManager createLayout() {
		return new TitlePaneLayout();
	}

	protected void createMenuBar() {
		menuBar = new SystemMenuBar();
		if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			final JMenu menu = new JMenu("");

			JMenuItem mi = menu.add(restoreAction);
			int mnemonic = getInt("MetalTitlePane.restoreMnemonic", -1);
			if (mnemonic != -1) {
				mi.setMnemonic(mnemonic);
			}
			mi = menu.add(iconifyAction);
			mnemonic = getInt("MetalTitlePane.iconifyMnemonic", -1);
			if (mnemonic != -1) {
				mi.setMnemonic(mnemonic);
			}

			if (DecorationHelper.isFrameStateSupported(
					Toolkit.getDefaultToolkit(), BaseRootPaneUI.MAXIMIZED_BOTH)) {
				mi = menu.add(maximizeAction);
				mnemonic = getInt("MetalTitlePane.maximizeMnemonic", -1);
				if (mnemonic != -1) {
					mi.setMnemonic(mnemonic);
				}
			}
			menu.add(new JSeparator());
			mi = menu.add(closeAction);
			mnemonic = getInt("MetalTitlePane.closeMnemonic", -1);
			if (mnemonic != -1) {
				mi.setMnemonic(mnemonic);
			}

			menuBar.add(menu);
		}
	}

	protected WindowListener createWindowListener() {
		return new WindowHandler();
	}

	protected PropertyChangeListener createWindowPropertyChangeListener() {
		return new PropertyChangeHandler();
	}

	protected Frame getFrame() {
		if (window instanceof Frame) {
			return (Frame) window;
		}
		return null;
	}

	protected Image getFrameIconImage() {
		return (getFrame() != null) ? getFrame().getIconImage() : null;
	}

	protected int getHorSpacing() {
		return 3;
	}

	@Override
	public JRootPane getRootPane() {
		return rootPane;
	}

	protected String getTitle() {
		if (window instanceof Frame) {
			return ((Frame) window).getTitle();
		} else if (window instanceof Dialog) {
			return ((Dialog) window).getTitle();
		}
		return null;
	}

	protected int getVerSpacing() {
		return 4;
	}

	protected Window getWindow() {
		return window;
	}

	protected int getWindowDecorationStyle() {
		return DecorationHelper.getWindowDecorationStyle(rootPane);
	}

	protected void iconify() {
		final Frame frame = getFrame();
		if (frame != null) {
			DecorationHelper.setExtendedState(frame, state | Frame.ICONIFIED);
		}
	}

	protected void installDefaults() {
		setFont(UIManager.getFont("InternalFrame.titleFont"));
	}

	protected void installListeners() {
		if (window != null) {
			windowListener = createWindowListener();
			window.addWindowListener(windowListener);
			propertyChangeListener = createWindowPropertyChangeListener();
			window.addPropertyChangeListener(propertyChangeListener);
		}
	}

	protected void installSubcomponents() {
		if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			createActions();
			createMenuBar();
			createButtons();
			add(menuBar);
			add(iconifyButton);
			add(maxButton);
			add(closeButton);
		} else {
			createActions();
			createButtons();
			add(closeButton);
		}
	}

	protected boolean isActive() {
		return (window == null) ? true : JTattooUtilities
				.isWindowActive(window);
	}

	protected boolean isLeftToRight() {
		return (window == null) ? getRootPane().getComponentOrientation()
				.isLeftToRight() : window.getComponentOrientation()
				.isLeftToRight();
	}

	protected void maximize() {
		final Frame frame = getFrame();
		if (frame != null) {
			DecorationHelper.setExtendedState(frame, state
					| BaseRootPaneUI.MAXIMIZED_BOTH);
			final PropertyChangeListener[] pcl = frame.getPropertyChangeListeners();
			for (int i = 0; i < pcl.length; i++) {
				pcl[i].propertyChange(new PropertyChangeEvent(this,
						"windowMaximized", Boolean.FALSE, Boolean.FALSE));
			}
		}
	}

	public void paintBackground(final Graphics g) {
		if (isActive()) {
			final Graphics2D g2D = (Graphics2D) g;
			final Composite composite = g2D.getComposite();
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, null);
				final AlphaComposite alpha = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, alphaValue);
				g2D.setComposite(alpha);
			}
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme()
					.getWindowTitleColors(), 0, 0, getWidth(), getHeight());
			g2D.setComposite(composite);
		} else {
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme()
					.getWindowInactiveTitleColors(), 0, 0, getWidth(),
					getHeight());
		}
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (getFrame() != null) {
			setState(DecorationHelper.getExtendedState(getFrame()));
		}

		paintBackground(g);

		final boolean leftToRight = isLeftToRight();
		final int width = getWidth();
		final int height = getHeight();
		int titleWidth = width - buttonsWidth - 4;
		int xOffset = leftToRight ? 4 : width - 4;
		if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			final int mw = menuBar.getWidth() + 4;
			xOffset += leftToRight ? mw : -mw;
			titleWidth -= height;
		}

		g.setFont(getFont());
		final FontMetrics fm = g.getFontMetrics();
		final String frameTitle = JTattooUtilities.getClippedText(getTitle(), fm,
				titleWidth);
		final int titleLength = fm.stringWidth(frameTitle);
		final int yOffset = ((height - fm.getHeight()) / 2) + fm.getAscent() - 1;
		if (!leftToRight) {
			xOffset -= titleLength;
		}
		paintText(g, xOffset, yOffset, frameTitle);
	}

	public void paintText(final Graphics g, final int x, final int y, final String title) {
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
		} else {
			g.setColor(AbstractLookAndFeel
					.getWindowInactiveTitleForegroundColor());
		}
		JTattooUtilities.drawString(rootPane, g, title, x, y);
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		uninstallListeners();
		window = null;
	}

	protected void restore() {
		final Frame frame = getFrame();
		if (frame == null) {
			return;
		}
		if ((state & Frame.ICONIFIED) != 0) {
			DecorationHelper.setExtendedState(frame, state & ~Frame.ICONIFIED);
		} else {
			DecorationHelper.setExtendedState(frame, state
					& ~BaseRootPaneUI.MAXIMIZED_BOTH);
		}
		final PropertyChangeListener[] pcl = frame.getPropertyChangeListeners();
		for (int i = 0; i < pcl.length; i++) {
			pcl[i].propertyChange(new PropertyChangeEvent(this,
					"windowRestored", Boolean.FALSE, Boolean.FALSE));
		}
	}

	protected void setActive(final boolean flag) {
		if (getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			final Boolean active = flag ? Boolean.TRUE : Boolean.FALSE;
			iconifyButton.putClientProperty(PAINT_ACTIVE, active);
			closeButton.putClientProperty(PAINT_ACTIVE, active);
			maxButton.putClientProperty(PAINT_ACTIVE, active);
		}
		getRootPane().repaint();
	}

	public void setAlphaTransparency(final float alpha) {
		alphaValue = alpha;
	}

	public void setBackgroundImage(final BufferedImage bgImage) {
		backgroundImage = bgImage;
	}

	protected void setState(final int state) {
		setState(state, false);
	}

	protected void setState(final int state, final boolean updateRegardless) {
		if (window != null
				&& getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			if (this.state == state && !updateRegardless) {
				return;
			}

			final Frame frame = getFrame();
			if (frame != null) {

				final GraphicsConfiguration gc = frame.getGraphicsConfiguration();
				final Rectangle screenBounds = gc.getBounds();
				final Insets screenInsets = Toolkit.getDefaultToolkit()
				.getScreenInsets(gc);
				screenInsets.bottom = Math.max(screenInsets.bottom, 1);
				final int x = Math.max(0, screenInsets.left);
				final int y = Math.max(0, screenInsets.top);
				final int w = screenBounds.width
				- (screenInsets.left + screenInsets.right);
				final int h = screenBounds.height
				- (screenInsets.top + screenInsets.bottom);
				// Keep taskbar visible
				frame.setMaximizedBounds(new Rectangle(x, y, w, h));

				if (((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0)
						&& (rootPane.getBorder() == null || (rootPane
								.getBorder() instanceof UIResource))
								&& frame.isShowing()) {
					rootPane.setBorder(null);
				} else if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) == 0) {
					rootPaneUI.installBorder(rootPane);
				}

				if (frame.isResizable()) {
					if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0) {
						updateMaxButton(restoreAction, minimizeIcon);
						maximizeAction.setEnabled(false);
						restoreAction.setEnabled(true);
					} else {
						updateMaxButton(maximizeAction, maximizeIcon);
						maximizeAction.setEnabled(true);
						restoreAction.setEnabled(false);
					}
					if (maxButton.getParent() == null
							|| iconifyButton.getParent() == null) {
						add(maxButton);
						add(iconifyButton);
						revalidate();
						repaint();
					}
					maxButton.setText(null);
				} else {
					maximizeAction.setEnabled(false);
					restoreAction.setEnabled(false);
					if (maxButton.getParent() != null) {
						remove(maxButton);
						revalidate();
						repaint();
					}
				}
			} else {
				// Not contained in a Frame
				maximizeAction.setEnabled(false);
				restoreAction.setEnabled(false);
				iconifyAction.setEnabled(false);
				remove(maxButton);
				remove(iconifyButton);
				revalidate();
				repaint();
			}
			closeAction.setEnabled(true);
			this.state = state;
		}
	}

	protected void uninstall() {
		uninstallListeners();
		window = null;
		removeAll();
	}

	protected void uninstallDefaults() {
	}

	protected void uninstallListeners() {
		if (window != null) {
			window.removeWindowListener(windowListener);
			window.removePropertyChangeListener(propertyChangeListener);
		}
	}

	protected void updateMaxButton(final Action action, final Icon icon) {
		maxButton.setAction(action);
		maxButton.setIcon(icon);
	}
}
