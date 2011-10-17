/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * TinyRootPaneUI
 * 
 * 10.9.07 Added mechanism to substitute actions added in BasicPopupUI to make
 * menus work as in XP (the hook is the FocusListener registered at
 * installUI()).
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyRootPaneUI extends BasicRootPaneUI implements FocusListener {

	private class KeyPostProcessor implements KeyEventPostProcessor {

		@Override
		public boolean postProcessKeyEvent(final KeyEvent e) {
			if (!topMenuToClose)
				return false;
			if (e.getID() != KeyEvent.KEY_PRESSED)
				return false;
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				return false;

			final MenuElement path[] = MenuSelectionManager.defaultManager()
			.getSelectedPath();
			final Iterator ii = registeredKeyCodes.iterator();
			while (ii.hasNext()) {
				final int keyCode = ((Integer) ii.next()).intValue();

				if (keyCode == e.getKeyCode()) {
					if (path.length > 2) {
						// Key press re-opened popup
						topMenuToClose = false;
						removeEscapeMenuHandlers();
					}

					return false;
				}
			}

			// Key pressed is not registered in InputMap
			if (path.length == 2) {
				MenuSelectionManager.defaultManager().clearSelectedPath();
			}

			topMenuToClose = false;
			removeEscapeMenuHandlers();

			return false;
		}
	}

	/*
	 * Is a sun.swing.UIAction in BasicPopupMenuUI, but sun.swing.UIAction is
	 * new in JDK 1.5
	 */
	private class MenuActions extends AbstractAction {

		private final String name;

		MenuActions(final String name) {
			super(name);

			this.name = name;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (CANCEL.equals(name)) {
				cancel();
			} else if (SELECT_PARENT.equals(name)) {
				selectParentChild(PARENT);
			} else if (SELECT_CHILD.equals(name)) {
				selectParentChild(CHILD);
			} else if (RETURN.equals(name)) {
				doReturn();
			}
		}

		/**
		 * Called if user pressed ESCAPE. In contrast to Java LAF, we select a
		 * top menu as its popup is closed.
		 * 
		 */
		private void cancel() {
			// 4234793: This action should call JPopupMenu.firePopupMenuCanceled
			// but it's
			// a protected method. The real solution could be to make
			// firePopupMenuCanceled public and call it directly.
			final JPopupMenu lastPopup = getLastPopup();

			if (lastPopup != null) {
				lastPopup.putClientProperty("JPopupMenu.firePopupMenuCanceled",
						Boolean.TRUE);
			}

			final MenuElement path[] = MenuSelectionManager.defaultManager()
			.getSelectedPath();
			// System.out.println("path.length=" + path.length);

			// if(path.length > 4) { /* PENDING(arnaud) Change this to 2 when a
			// mouse grabber is available for MenuBar */
			if (path.length > 2) {
				final int newSize = Math.max(2, path.length - 2);
				final MenuElement newPath[] = new MenuElement[newSize];
				System.arraycopy(path, 0, newPath, 0, newSize);
				MenuSelectionManager.defaultManager().setSelectedPath(newPath);

				if (newSize == 2 && !topMenuToClose) {
					topMenuToClose = true;
					addEscapeMenuHandlers();
				}
			} else {
				MenuSelectionManager.defaultManager().clearSelectedPath();

				if (topMenuToClose) {
					topMenuToClose = false;
					removeEscapeMenuHandlers();
				}
			}
		}

		/**
		 * Called if user pressed RETURN or SPACE. In contrast to Java LAF, we
		 * select the first enabled entry of newly opened popups.
		 * 
		 */
		private void doReturn() {
			final KeyboardFocusManager fmgr = KeyboardFocusManager
			.getCurrentKeyboardFocusManager();
			final Component focusOwner = fmgr.getFocusOwner();

			if (focusOwner != null && !(focusOwner instanceof JRootPane)) {
				return;
			}

			final MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			final MenuElement path[] = msm.getSelectedPath();
			MenuElement lastElement;

			if (path.length > 0) {
				lastElement = path[path.length - 1];

				if (lastElement instanceof JMenu) {
					final JPopupMenu popup = ((JMenu) lastElement).getPopupMenu();
					final MenuElement nextItem = findEnabledChild(
							popup.getSubElements(), -1, true);

					if (nextItem != null) {
						final MenuElement newPath[] = new MenuElement[path.length + 2];
						System.arraycopy(path, 0, newPath, 0, path.length);
						newPath[path.length] = popup;
						newPath[path.length + 1] = nextItem;
						msm.setSelectedPath(newPath);
					} else {
						final MenuElement newPath[] = new MenuElement[path.length + 1];
						System.arraycopy(path, 0, newPath, 0, path.length);
						newPath[path.length] = popup;
						msm.setSelectedPath(newPath);
					}
				} else if (lastElement instanceof JMenuItem) {
					final JMenuItem mi = (JMenuItem) lastElement;

					if (mi.getUI() instanceof TinyMenuItemUI) {
						((TinyMenuItemUI) mi.getUI()).doClick(msm);
					} else {
						msm.clearSelectedPath();
						mi.doClick(0);
					}
				}
			}
		}

		private MenuElement findEnabledChild(final MenuElement e[], final int fromIndex,
				final boolean forward) {
			MenuElement result = null;
			if (forward) {
				result = nextEnabledChild(e, fromIndex + 1, e.length - 1);
				if (result == null)
					result = nextEnabledChild(e, 0, fromIndex - 1);
			} else {
				result = previousEnabledChild(e, fromIndex - 1, 0);
				if (result == null)
					result = previousEnabledChild(e, e.length - 1,
							fromIndex + 1);
			}
			return result;
		}

		private MenuElement findEnabledChild(final MenuElement e[], final MenuElement elem,
				final boolean forward) {
			for (int i = 0; i < e.length; i++) {
				if (e[i] == elem) {
					return findEnabledChild(e, i, forward);
				}
			}
			return null;
		}

		private JPopupMenu getLastPopup() {
			final MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			final MenuElement[] p = msm.getSelectedPath();
			JPopupMenu popup = null;

			for (int i = p.length - 1; popup == null && i >= 0; i--) {
				if (p[i] instanceof JPopupMenu)
					popup = (JPopupMenu) p[i];
			}

			return popup;
		}

		private MenuElement nextEnabledChild(final MenuElement e[], final int fromIndex,
				final int toIndex) {
			for (int i = fromIndex; i <= toIndex; i++) {
				if (e[i] != null) {
					final Component comp = e[i].getComponent();
					if (comp != null
							&& (comp.isEnabled() || UIManager
									.getBoolean("MenuItem.disabledAreNavigable"))
									&& comp.isVisible()) {
						return e[i];
					}
				}
			}
			return null;
		}

		private MenuElement previousEnabledChild(final MenuElement e[],
				final int fromIndex, final int toIndex) {
			for (int i = fromIndex; i >= toIndex; i--) {
				if (e[i] != null) {
					final Component comp = e[i].getComponent();
					if (comp != null
							&& (comp.isEnabled() || UIManager
									.getBoolean("MenuItem.disabledAreNavigable"))
									&& comp.isVisible()) {
						return e[i];
					}
				}
			}
			return null;
		}

		/**
		 * Called if user pressed LEFT resp. RIGHT. In contrast to Java LAF, we
		 * select the first enabled entry of newly opened popups.
		 * 
		 * @param direction
		 */
		private void selectParentChild(final boolean direction) {
			final MenuSelectionManager msm = MenuSelectionManager.defaultManager();
			final MenuElement path[] = msm.getSelectedPath();
			final int len = path.length;

			if (direction == PARENT) {
				// selecting parent (LEFT)
				int popupIndex = len - 1;

				if (len > 2
						&&
						// check if we have an open submenu. A submenu item may
						// or
						// may not be selected, so submenu popup can be either
						// the
						// last or next to the last item.
						(path[popupIndex] instanceof JPopupMenu || path[--popupIndex] instanceof JPopupMenu)
						&& !((JMenu) path[popupIndex - 1]).isTopLevelMenu()) {
					// we have a submenu, just close it
					final MenuElement newPath[] = new MenuElement[popupIndex];
					System.arraycopy(path, 0, newPath, 0, popupIndex);
					msm.setSelectedPath(newPath);
					return;
				}
			} else {
				// selecting child (RIGHT)
				if (len > 0 && path[len - 1] instanceof JMenu
						&& !((JMenu) path[len - 1]).isTopLevelMenu()) {
					// we have a submenu, open it
					final JMenu menu = (JMenu) path[len - 1];
					final JPopupMenu popup = menu.getPopupMenu();
					final MenuElement[] subs = popup.getSubElements();
					final MenuElement item = findEnabledChild(subs, -1, true);
					MenuElement[] newPath;

					if (item == null) {
						newPath = new MenuElement[len + 1];
					} else {
						newPath = new MenuElement[len + 2];
						newPath[len + 1] = item;
					}

					System.arraycopy(path, 0, newPath, 0, len);
					newPath[len] = popup;
					msm.setSelectedPath(newPath);
					return;
				}
			}

			// check if we have a toplevel menu selected.
			// If this is the case, we select another toplevel menu
			if (len > 1 && path[0] instanceof JMenuBar) {
				final MenuElement currentMenu = path[1];
				final MenuElement nextMenu = findEnabledChild(
						path[0].getSubElements(), currentMenu, direction);

				if (nextMenu != null && nextMenu != currentMenu) {
					MenuElement newSelection[] = null;
					if (len == 2) {
						// menu is selected but its popup not shown
						newSelection = new MenuElement[2];
						newSelection[0] = path[0];
						newSelection[1] = nextMenu;
					} else {
						// menu is selected and its popup is shown
						final JPopupMenu popup = ((JMenu) nextMenu).getPopupMenu();
						final MenuElement nextItem = findEnabledChild(
								popup.getSubElements(), -1, true);

						if (nextItem != null) {
							newSelection = new MenuElement[4];
							newSelection[0] = path[0];
							newSelection[1] = nextMenu;
							newSelection[2] = popup;
							newSelection[3] = nextItem;
						} else {
							// popup has no enabled child
							newSelection = new MenuElement[3];
							newSelection[0] = path[0];
							newSelection[1] = nextMenu;
							newSelection[2] = popup;
						}
					}
					msm.setSelectedPath(newSelection);
				}
			}
		}
	}

	/**
	 * A custom layout manager that is responsible for the layout of
	 * layeredPane, glassPane, menuBar and titlePane, if one has been installed.
	 */
	// NOTE: Ideally this would extends JRootPane.RootLayout, but that
	// would force this to be non-static.
	private static class MetalRootLayout implements LayoutManager2 {

		@Override
		public void addLayoutComponent(final Component comp, final Object constraints) {
		}

		@Override
		public void addLayoutComponent(final String name, final Component comp) {
		}

		@Override
		public float getLayoutAlignmentX(final Container target) {
			return 0.0f;
		}

		@Override
		public float getLayoutAlignmentY(final Container target) {
			return 0.0f;
		}

		@Override
		public void invalidateLayout(final Container target) {
		}

		/**
		 * Instructs the layout manager to perform the layout for the specified
		 * container.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 */
		@Override
		public void layoutContainer(final Container parent) {
			final JRootPane root = (JRootPane) parent;
			final Rectangle b = root.getBounds();
			final Insets i = root.getInsets();
			int nextY = 0;
			final int w = b.width - i.right - i.left;
			final int h = b.height - i.top - i.bottom;

			if (root.getLayeredPane() != null) {
				root.getLayeredPane().setBounds(i.left, i.top, w, h);
			}
			if (root.getGlassPane() != null) {
				root.getGlassPane().setBounds(i.left, i.top, w, h);
			}
			// Note: This is laying out the children in the layeredPane,
			// technically, these are not our children.
			if (root.getWindowDecorationStyle() != JRootPane.NONE
					&& (root.getUI() instanceof TinyRootPaneUI)) {
				final JComponent titlePane = ((TinyRootPaneUI) root.getUI())
				.getTitlePane();
				if (titlePane != null) {
					final Dimension tpd = titlePane.getPreferredSize();
					if (tpd != null) {
						final int tpHeight = tpd.height;
						titlePane.setBounds(0, 0, w, tpHeight);
						nextY += tpHeight;
					}
				}
			}
			if (root.getJMenuBar() != null) {
				final Dimension mbd = root.getJMenuBar().getPreferredSize();
				root.getJMenuBar().setBounds(0, nextY, w, mbd.height);
				nextY += mbd.height;
			}
			if (root.getContentPane() != null) {
				root.getContentPane().getPreferredSize();
				root.getContentPane().setBounds(0, nextY, w,
						h < nextY ? 0 : h - nextY);
			}
		}

		/**
		 * Returns the maximum amount of space the layout can use.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 * @return a Dimension object containing the layout's maximum size
		 */
		@Override
		public Dimension maximumLayoutSize(final Container target) {
			Dimension cpd, mbd, tpd;
			int cpWidth = Integer.MAX_VALUE;
			int cpHeight = Integer.MAX_VALUE;
			int mbWidth = Integer.MAX_VALUE;
			int mbHeight = Integer.MAX_VALUE;
			int tpWidth = Integer.MAX_VALUE;
			int tpHeight = Integer.MAX_VALUE;
			final Insets i = target.getInsets();
			final JRootPane root = (JRootPane) target;

			if (root.getContentPane() != null) {
				cpd = root.getContentPane().getMaximumSize();
				if (cpd != null) {
					cpWidth = cpd.width;
					cpHeight = cpd.height;
				}
			}

			if (root.getJMenuBar() != null) {
				mbd = root.getJMenuBar().getMaximumSize();
				if (mbd != null) {
					mbWidth = mbd.width;
					mbHeight = mbd.height;
				}
			}

			if (root.getWindowDecorationStyle() != JRootPane.NONE
					&& (root.getUI() instanceof TinyRootPaneUI)) {
				final JComponent titlePane = ((TinyRootPaneUI) root.getUI())
				.getTitlePane();
				if (titlePane != null) {
					tpd = titlePane.getMaximumSize();
					if (tpd != null) {
						tpWidth = tpd.width;
						tpHeight = tpd.height;
					}
				}
			}

			int maxHeight = Math.max(Math.max(cpHeight, mbHeight), tpHeight);
			// Only overflows if 3 real non-MAX_VALUE heights, sum to >
			// MAX_VALUE
			// Only will happen if sums to more than 2 billion units. Not
			// likely.
			if (maxHeight != Integer.MAX_VALUE) {
				maxHeight = cpHeight + mbHeight + tpHeight + i.top + i.bottom;
			}

			int maxWidth = Math.max(Math.max(cpWidth, mbWidth), tpWidth);
			// Similar overflow comment as above
			if (maxWidth != Integer.MAX_VALUE) {
				maxWidth += i.left + i.right;
			}

			return new Dimension(maxWidth, maxHeight);
		}

		/**
		 * Returns the minimum amount of space the layout needs.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 * @return a Dimension object containing the layout's minimum size
		 */
		@Override
		public Dimension minimumLayoutSize(final Container parent) {
			Dimension cpd, mbd, tpd;
			int cpWidth = 0;
			int cpHeight = 0;
			int mbWidth = 0;
			int mbHeight = 0;
			int tpWidth = 0;
			int tpHeight = 0;
			final Insets i = parent.getInsets();
			final JRootPane root = (JRootPane) parent;

			if (root.getContentPane() != null) {
				cpd = root.getContentPane().getMinimumSize();
			} else {
				cpd = root.getSize();
			}

			if (cpd != null) {
				cpWidth = cpd.width;
				cpHeight = cpd.height;
			}

			if (root.getJMenuBar() != null) {
				mbd = root.getJMenuBar().getMinimumSize();

				if (mbd != null) {
					mbWidth = mbd.width;
					mbHeight = mbd.height;
				}
			}
			if (root.getWindowDecorationStyle() != JRootPane.NONE
					&& (root.getUI() instanceof TinyRootPaneUI)) {
				final JComponent titlePane = ((TinyRootPaneUI) root.getUI())
				.getTitlePane();
				if (titlePane != null) {
					tpd = titlePane.getMinimumSize();

					if (tpd != null) {
						tpWidth = tpd.width;
						tpHeight = tpd.height;
					}
				}
			}

			return new Dimension(Math.max(Math.max(cpWidth, mbWidth), tpWidth)
					+ i.left + i.right, cpHeight + mbHeight + tpHeight + i.top
					+ i.bottom);
		}

		/**
		 * Returns the amount of space the layout would like to have.
		 * 
		 * @param the
		 *            Container for which this layout manager is being used
		 * @return a Dimension object containing the layout's preferred size
		 */
		@Override
		public Dimension preferredLayoutSize(final Container parent) {
			Dimension cpd, mbd, tpd;
			int cpWidth = 0;
			int cpHeight = 0;
			int mbWidth = 0;
			int mbHeight = 0;
			int tpWidth = 0;
			int tpHeight = 0;
			final Insets i = parent.getInsets();
			final JRootPane root = (JRootPane) parent;

			if (root.getContentPane() != null) {
				cpd = root.getContentPane().getPreferredSize();
			} else {
				cpd = root.getSize();
			}

			if (cpd != null) {
				cpWidth = cpd.width;
				cpHeight = cpd.height;
			}

			if (root.getJMenuBar() != null) {
				mbd = root.getJMenuBar().getPreferredSize();

				if (mbd != null) {
					mbWidth = mbd.width;
					mbHeight = mbd.height;
				}
			}

			if (root.getWindowDecorationStyle() != JRootPane.NONE
					&& (root.getUI() instanceof TinyRootPaneUI)) {
				final JComponent titlePane = ((TinyRootPaneUI) root.getUI())
				.getTitlePane();

				if (titlePane != null) {
					tpd = titlePane.getPreferredSize();

					if (tpd != null) {
						tpWidth = tpd.width;
						tpHeight = tpd.height;
					}
				}
			}

			return new Dimension(Math.max(Math.max(cpWidth, mbWidth), tpWidth)
					+ i.left + i.right, cpHeight + mbHeight + tpHeight + i.top
					+ i.bottom);
		}

		@Override
		public void removeLayoutComponent(final Component comp) {
		}
	}

	private class MouseHandler implements AWTEventListener {

		@Override
		public void eventDispatched(final AWTEvent e) {
			if (!topMenuToClose)
				return;

			// We don't have to check for the kind of event,
			// the only important thing is that a top menu
			// can be deactivated
			MenuSelectionManager.defaultManager().clearSelectedPath();

			topMenuToClose = false;
			removeEscapeMenuHandlers();
		}
	}

	/**
	 * MouseInputHandler is responsible for handling resize/moving of the
	 * Window. It sets the cursor directly on the Window when then mouse moves
	 * over a hot spot.
	 */
	private class MouseInputHandler implements MouseInputListener {

		/**
		 * Set to true if the drag operation is moving the window.
		 */
		private boolean isMovingWindow;

		/**
		 * Used to determine the corner the resize is occuring from.
		 */
		private int dragCursor;

		/**
		 * X location the mouse went down on for a drag operation.
		 */
		private int dragOffsetX;

		/**
		 * Y location the mouse went down on for a drag operation.
		 */
		private int dragOffsetY;

		/**
		 * Width of the window when the drag started.
		 */
		private int dragWidth;

		/**
		 * Height of the window when the drag started.
		 */
		private int dragHeight;

		private void adjust(final Rectangle bounds, final Dimension min, final int deltaX,
				final int deltaY, final int deltaWidth, final int deltaHeight) {
			bounds.x += deltaX;
			bounds.y += deltaY;
			bounds.width += deltaWidth;
			bounds.height += deltaHeight;

			if (min != null) {
				if (bounds.width < min.width) {
					final int correction = min.width - bounds.width;

					if (deltaX != 0) {
						bounds.x -= correction;
					}

					bounds.width = min.width;
				}

				if (bounds.height < min.height) {
					final int correction = min.height - bounds.height;

					if (deltaY != 0) {
						bounds.y -= correction;
					}

					bounds.height = min.height;
				}
			}
		}

		/**
		 * Returns the corner that contains the point <code>x</code>,
		 * <code>y</code>, or -1 if the position doesn't match a corner.
		 */
		private int calculateCorner(final Component c, final int x, final int y) {
			final int xPosition = calculatePosition(x, c.getWidth());
			final int yPosition = calculatePosition(y, c.getHeight());

			if (xPosition == -1 || yPosition == -1) {
				return -1;
			}

			return yPosition * 5 + xPosition;
		}

		/**
		 * Returns an integer indicating the position of <code>spot</code> in
		 * <code>width</code>. The return value will be: 0 if <
		 * BORDER_DRAG_THICKNESS 1 if < CORNER_DRAG_WIDTH 2 if >=
		 * CORNER_DRAG_WIDTH && < width - BORDER_DRAG_THICKNESS 3 if >= width -
		 * CORNER_DRAG_WIDTH 4 if >= width - BORDER_DRAG_THICKNESS 5 otherwise
		 */
		private int calculatePosition(final int spot, final int width) {
			if (spot < BORDER_DRAG_THICKNESS) {
				return 0;
			}
			if (spot < CORNER_DRAG_WIDTH) {
				return 1;
			}
			if (spot >= (width - BORDER_DRAG_THICKNESS)) {
				return 4;
			}
			if (spot >= (width - CORNER_DRAG_WIDTH)) {
				return 3;
			}
			return 2;
		}

		/**
		 * Returns the Cursor to render for the specified corner. This returns 0
		 * if the corner doesn't map to a valid Cursor
		 */
		private int getCursor(final int corner) {
			if (corner == -1) {
				return 0;
			}

			return cursorMapping[corner];
		}

		@Override
		public void mouseClicked(final MouseEvent ev) {
		}

		@Override
		public void mouseDragged(final MouseEvent ev) {
			final Window w = (Window) ev.getSource();
			final Point pt = ev.getPoint();

			if (isMovingWindow) {
				final Point windowPt = w.getLocationOnScreen();

				windowPt.x += pt.x - dragOffsetX;
				windowPt.y += pt.y - dragOffsetY;
				w.setLocation(windowPt);
			} else if (dragCursor != 0) {
				final Rectangle r = w.getBounds();
				final Rectangle startBounds = new Rectangle(r);
				final Dimension min = w.getMinimumSize();

				switch (dragCursor) {
				case Cursor.E_RESIZE_CURSOR:
					adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX)
							- r.width, 0);
					break;
				case Cursor.S_RESIZE_CURSOR:
					adjust(r, min, 0, 0, 0, pt.y + (dragHeight - dragOffsetY)
							- r.height);
					break;
				case Cursor.N_RESIZE_CURSOR:
					adjust(r, min, 0, pt.y - dragOffsetY, 0,
							-(pt.y - dragOffsetY));
					break;
				case Cursor.W_RESIZE_CURSOR:
					adjust(r, min, pt.x - dragOffsetX, 0,
							-(pt.x - dragOffsetX), 0);
					break;
				case Cursor.NE_RESIZE_CURSOR:
					adjust(r, min, 0, pt.y - dragOffsetY, pt.x
							+ (dragWidth - dragOffsetX) - r.width,
							-(pt.y - dragOffsetY));
					break;
				case Cursor.SE_RESIZE_CURSOR:
					adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX)
							- r.width, pt.y + (dragHeight - dragOffsetY)
							- r.height);
					break;
				case Cursor.NW_RESIZE_CURSOR:
					adjust(r, min, pt.x - dragOffsetX, pt.y - dragOffsetY,
							-(pt.x - dragOffsetX), -(pt.y - dragOffsetY));
					break;
				case Cursor.SW_RESIZE_CURSOR:
					adjust(r, min, pt.x - dragOffsetX, 0,
							-(pt.x - dragOffsetX), pt.y
							+ (dragHeight - dragOffsetY) - r.height);
					break;
				default:
					break;
				}

				if (!r.equals(startBounds)) {
					w.setBounds(r);

					// Defer repaint/validate on mouseReleased unless dynamic
					// layout is active.
					// [not active on my system... (Win 2000 Server)]
					if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
						w.validate();
						getRootPane().repaint();
					}
				}
			}
		}

		@Override
		public void mouseEntered(final MouseEvent ev) {
			final Window w = (Window) ev.getSource();
			lastCursor = w.getCursor();

			mouseMoved(ev);
		}

		@Override
		public void mouseExited(final MouseEvent ev) {
			final Window w = (Window) ev.getSource();
			w.setCursor(lastCursor);
		}

		@Override
		public void mouseMoved(final MouseEvent ev) {
			final JRootPane root = getRootPane();

			if (root.getWindowDecorationStyle() == JRootPane.NONE) {
				return;
			}

			final Window w = (Window) ev.getSource();

			Frame f = null;
			Dialog d = null;

			if (w instanceof Frame) {
				f = (Frame) w;
			} else if (w instanceof Dialog) {
				d = (Dialog) w;
			}

			// Update the cursor
			final int cursor = getCursor(calculateCorner(w, ev.getX(), ev.getY()));

			if (cursor != 0
					&& ((f != null && (f.isResizable()
							&& (f.getExtendedState() & Frame.MAXIMIZED_VERT) != Frame.MAXIMIZED_VERT && (f
									.getExtendedState() & Frame.MAXIMIZED_HORIZ) != Frame.MAXIMIZED_HORIZ)) || (d != null && d
											.isResizable()))) {
				w.setCursor(Cursor.getPredefinedCursor(cursor));
			} else {
				w.setCursor(lastCursor);
			}
		}

		@Override
		public void mousePressed(final MouseEvent ev) {
			final JRootPane rootPane = getRootPane();

			if (rootPane.getWindowDecorationStyle() == JRootPane.NONE) {
				return;
			}

			final Point dragWindowOffset = ev.getPoint();
			final Window w = (Window) ev.getSource();
			final Point convertedDragWindowOffset = SwingUtilities.convertPoint(w,
					dragWindowOffset, getTitlePane());

			Frame f = null;
			Dialog d = null;

			if (w instanceof Frame) {
				f = (Frame) w;
			} else if (w instanceof Dialog) {
				d = (Dialog) w;
			}

			final int frameState = (f != null ? f.getExtendedState() : 0);

			if (getTitlePane() != null
					&& getTitlePane().contains(convertedDragWindowOffset)) {
				if (ev.getClickCount() == 2) {
					if (f != null && f.isResizable()) {
						if ((frameState & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ
								|| (frameState & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT) {
							f.setExtendedState(frameState
									& ~Frame.MAXIMIZED_BOTH);
						} else {
							f.setExtendedState(frameState
									| Frame.MAXIMIZED_BOTH);
						}
						return;
					}
				}

				if (((f != null && ((frameState & Frame.MAXIMIZED_HORIZ) != Frame.MAXIMIZED_HORIZ && (frameState & Frame.MAXIMIZED_VERT) != Frame.MAXIMIZED_VERT)) || (d != null))
						&& dragWindowOffset.y >= BORDER_DRAG_THICKNESS
						&& dragWindowOffset.x >= BORDER_DRAG_THICKNESS
						&& dragWindowOffset.x < w.getWidth()
						- BORDER_DRAG_THICKNESS) {
					isMovingWindow = true;
					dragOffsetX = dragWindowOffset.x;
					dragOffsetY = dragWindowOffset.y;
					return;
				}
			}

			if ((f != null && f.isResizable() && ((frameState & Frame.MAXIMIZED_HORIZ) != Frame.MAXIMIZED_HORIZ && (frameState & Frame.MAXIMIZED_VERT) != Frame.MAXIMIZED_VERT))
					|| (d != null && d.isResizable())) {
				dragOffsetX = dragWindowOffset.x;
				dragOffsetY = dragWindowOffset.y;
				dragWidth = w.getWidth();
				dragHeight = w.getHeight();
				final int corner = calculateCorner(w, dragWindowOffset.x,
						dragWindowOffset.y);
				dragCursor = getCursor(corner);
			}
		}

		@Override
		public void mouseReleased(final MouseEvent ev) {
			if (dragCursor != 0 && window != null && !window.isValid()) {
				// Some Window systems validate as you resize, others won't,
				// thus the check for validity before repainting.
				window.validate();
				getRootPane().repaint();
			}

			isMovingWindow = false;
			dragCursor = 0;
			final Window w = (Window) ev.getSource();

			if (w.getCursor() != lastCursor) {
				w.setCursor(lastCursor);
			}
		}
	}

	/**
	 * Keys to lookup borders in defaults table.
	 */
	private static final String[] borderKeys = new String[] { null,
		"RootPane.frameBorder", "RootPane.plainDialogBorder",
		"RootPane.informationDialogBorder", "RootPane.errorDialogBorder",
		"RootPane.colorChooserDialogBorder",
		"RootPane.fileChooserDialogBorder",
		"RootPane.questionDialogBorder", "RootPane.warningDialogBorder" };

	/**
	 * The amount of space (in pixels) that the cursor is changed on.
	 */
	private static final int CORNER_DRAG_WIDTH = 16;

	/**
	 * Region from edges that dragging is active from.
	 */
	private static final int BORDER_DRAG_THICKNESS = 5;

	/**
	 * Window the <code>JRootPane</code> is in.
	 */
	private Window window;

	/**
	 * <code>JComponent</code> providing window decorations. This will be null
	 * if not providing window decorations.
	 */
	private JComponent titlePane;

	/**
	 * <code>MouseInputListener</code> that is added to the parent
	 * <code>Window</code> the <code>JRootPane</code> is contained in.
	 */
	private MouseInputListener mouseInputListener;

	/**
	 * The <code>LayoutManager</code> that is set on the <code>JRootPane</code>.
	 */
	private LayoutManager layoutManager;

	/**
	 * <code>LayoutManager</code> of the <code>JRootPane</code> before we
	 * replaced it.
	 */
	private LayoutManager savedOldLayout;

	/**
	 * <code>JRootPane</code> providing the look and feel for.
	 */
	private JRootPane root;

	/**
	 * <code>Cursor</code> used to track the cursor set by the user. This is
	 * initially <code>Cursor.DEFAULT_CURSOR</code>.
	 */
	private Cursor lastCursor = Cursor
	.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

	/**
	 * The following support key presses on menus to work as in XP
	 */
	private final AWTEventListener mouseHandler = new MouseHandler();

	private final KeyEventPostProcessor keyPostProcessor = new KeyPostProcessor();

	private boolean topMenuToClose = false;

	private Vector registeredKeyCodes;

	private static final boolean PARENT = false;

	private static final boolean CHILD = true;

	private static final boolean FORWARD = true;

	private static final String CANCEL = "cancel";

	private static final String RETURN = "return";

	private static final String SELECT_PARENT = "selectParent";

	private static final String SELECT_CHILD = "selectChild";

	/**
	 * Maps from positions to cursor type. Refer to calculateCorner and
	 * calculatePosition for details of this.
	 */
	private static final int[] cursorMapping = new int[] {
		Cursor.NW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR,
		Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
		Cursor.NE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, 0, 0, 0,
		Cursor.NE_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR, 0, 0, 0,
		Cursor.E_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, 0, 0, 0,
		Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
		Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
		Cursor.SE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };

	/**
	 * Creates a UI for a <code>JRootPane</code>.
	 * 
	 * @param c
	 *            the JRootPane the RootPaneUI will be created for
	 * @return the RootPaneUI implementation for the passed in JRootPane
	 */
	public static ComponentUI createUI(final JComponent c) {
		return new TinyRootPaneUI();
	}

	private void addEscapeMenuHandlers() {
		// We add a mouse handler which will deactivate
		// any selected top menu as soon as the mouse is
		// moved or mouse button is pressed
		java.security.AccessController
		.doPrivileged(new java.security.PrivilegedAction() {
			@Override
			public Object run() {
				Toolkit.getDefaultToolkit().addAWTEventListener(
						mouseHandler,
						AWTEvent.MOUSE_EVENT_MASK
						| AWTEvent.MOUSE_MOTION_EVENT_MASK
						| AWTEvent.MOUSE_WHEEL_EVENT_MASK);

				// System.out.println("escapeMenuHandler added");
				return null;
			}
		});

		// We add a key handler which will deactivate
		// any selected top menu as soon as a key is pressed
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addKeyEventPostProcessor(keyPostProcessor);
	}

	/**
	 * Returns a <code>LayoutManager</code> that will be set on the
	 * <code>JRootPane</code>.
	 */
	private LayoutManager createLayoutManager() {
		return new MetalRootLayout();
	}

	/**
	 * Returns the <code>JComponent</code> to render the window decoration
	 * style.
	 */
	private JComponent createTitlePane(final JRootPane root) {
		return new TinyTitlePane(root, this);
	}

	/**
	 * Returns a <code>MouseListener</code> that will be added to the
	 * <code>Window</code> containing the <code>JRootPane</code>.
	 */
	private MouseInputListener createWindowMouseInputListener(final JRootPane root) {
		return new MouseInputHandler();
	}

	// FocusListener impl
	@Override
	public void focusGained(final FocusEvent e) {
		if (topMenuToClose) {
			topMenuToClose = false;
			removeEscapeMenuHandlers();
		} else {
			final JRootPane root = (JRootPane) e.getSource();

			// store a copy of all registered key codes
			registeredKeyCodes = getRegisteredKeyCodes(root);

			// Replace some actions from BasicPopupMenuUI.
			// We assume that there is a corresponding entry
			// for each key in input map.
			final ActionMap am = getMapForKey(root.getActionMap(), CANCEL);

			if (am != null) {
				am.put(CANCEL, new MenuActions(CANCEL));
				am.put(RETURN, new MenuActions(RETURN));
				am.put(SELECT_PARENT, new MenuActions(SELECT_PARENT));
				am.put(SELECT_CHILD, new MenuActions(SELECT_CHILD));
			}
		}
	}

	@Override
	public void focusLost(final FocusEvent e) {
	}

	private ActionMap getMapForKey(final ActionMap am, final String key) {
		if (am == null)
			return null;

		if (am != null && am.keys() != null) {
			final Object[] keys = am.keys();

			// for(int i = 0; i < keys.length; i++) {
			// System.out.println(keys[i] + " -> " + am.get(keys[i]));
			// }

			for (int i = 0; i < keys.length; i++) {
				if (key.equals(keys[i]))
					return am;
			}
		}

		return getMapForKey(am.getParent(), key);
	}

	private Vector getRegisteredKeyCodes(final JRootPane root) {
		final Vector v = new Vector();

		final InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		if (im == null)
			return v;

		final KeyStroke[] keys = im.allKeys();

		if (keys == null)
			return v;

		for (int i = 0; i < keys.length; i++) {
			v.add(new Integer(keys[i].getKeyCode()));
		}

		return v;
	}

	/**
	 * Returns the <code>JRootPane</code> we're providing the look and feel for.
	 */
	private JRootPane getRootPane() {
		return root;
	}

	/**
	 * Returns the <code>JComponent</code> rendering the title pane. If this
	 * returns null, it implies there is no need to render window decorations.
	 * 
	 * @return the current window title pane, or null
	 * @see #setTitlePane
	 */
	private JComponent getTitlePane() {
		return titlePane;
	}

	/**
	 * Installs the appropriate <code>Border</code> onto the
	 * <code>JRootPane</code>.
	 */
	void installBorder(final JRootPane root) {
		final int style = root.getWindowDecorationStyle();

		if (style == JRootPane.NONE) {
			LookAndFeel.uninstallBorder(root);
		} else {
			// installs an instance of TinyFrameBorder
			LookAndFeel.installBorder(root, borderKeys[style]);
		}
	}

	/**
	 * Installs the necessary state onto the JRootPane to render client
	 * decorations. This is ONLY invoked if the <code>JRootPane</code> has a
	 * decoration style other than <code>JRootPane.NONE</code>.
	 */
	private void installClientDecorations(final JRootPane root) {
		installBorder(root);

		final JComponent titlePane = createTitlePane(root);

		setTitlePane(root, titlePane);
		installWindowListeners(root, root.getParent());
		installLayout(root);

		if (window != null) {
			root.revalidate();
			root.repaint();
		}
	}

	/**
	 * Installs the appropriate LayoutManager on the <code>JRootPane</code> to
	 * render the window decorations.
	 */
	private void installLayout(final JRootPane root) {
		if (layoutManager == null) {
			layoutManager = createLayoutManager();
		}
		savedOldLayout = root.getLayout();
		root.setLayout(layoutManager);
	}

	@Override
	protected void installListeners(final JRootPane root) {
		super.installListeners(root);

		root.addFocusListener(this);
	}

	/**
	 * Invokes supers implementation of <code>installUI</code> to install the
	 * necessary state onto the passed in <code>JRootPane</code> to render the
	 * metal look and feel implementation of <code>RootPaneUI</code>. If the
	 * <code>windowDecorationStyle</code> property of the <code>JRootPane</code>
	 * is other than <code>JRootPane.NONE</code>, this will add a custom
	 * <code>Component</code> to render the widgets to <code>JRootPane</code>,
	 * as well as installing a custom <code>Border</code> and
	 * <code>LayoutManager</code> on the <code>JRootPane</code>.
	 * 
	 * @param c
	 *            the JRootPane to install state onto
	 */
	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);

		root = (JRootPane) c;
		final int style = root.getWindowDecorationStyle();

		if (style != JRootPane.NONE) {
			installClientDecorations(root);
		}
	}

	/**
	 * Installs the necessary Listeners on the parent <code>Window</code>, if
	 * there is one.
	 * <p>
	 * This takes the parent so that cleanup can be done from
	 * <code>removeNotify</code>, at which point the parent hasn't been reset
	 * yet.
	 * 
	 * @param parent
	 *            The parent of the JRootPane
	 */
	private void installWindowListeners(final JRootPane root, final Component parent) {
		if (parent instanceof Window) {
			window = (Window) parent;
		} else {
			window = SwingUtilities.getWindowAncestor(parent);
		}

		if (window != null) {
			if (mouseInputListener == null) {
				mouseInputListener = createWindowMouseInputListener(root);
			}
			window.addMouseListener(mouseInputListener);
			window.addMouseMotionListener(mouseInputListener);
		}
	}

	boolean isTopMenuToClose() {
		return topMenuToClose;
	}

	/**
	 * Invoked when a property changes. <code>TinyRootPaneUI</code> is primarily
	 * interested in events originating from the <code>JRootPane</code> it has
	 * been installed on identifying the property
	 * <code>windowDecorationStyle</code>. If the
	 * <code>windowDecorationStyle</code> has changed to a value other than
	 * <code>JRootPane.NONE</code>, this will add a <code>Component</code> to
	 * the <code>JRootPane</code> to render the window decorations, as well as
	 * installing a <code>Border</code> on the <code>JRootPane</code>. On the
	 * other hand, if the <code>windowDecorationStyle</code> has changed to
	 * <code>JRootPane.NONE</code>, this will remove the <code>Component</code>
	 * that has been added to the <code>JRootPane</code> as well resetting the
	 * Border to what it was before <code>installUI</code> was invoked.
	 * 
	 * @param e
	 *            A PropertyChangeEvent object describing the event source and
	 *            the property that has changed.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		super.propertyChange(e);

		final String propertyName = e.getPropertyName();
		if (propertyName == null) {
			return;
		}

		if (propertyName.equals("windowDecorationStyle")) {
			final JRootPane root = (JRootPane) e.getSource();
			final int style = root.getWindowDecorationStyle();

			// This is potentially more than needs to be done,
			// but it rarely happens and makes the install/uninstall process
			// simpler. MetalTitlePane also assumes it will be recreated if
			// the decoration style changes.
			uninstallClientDecorations(root);

			if (style != JRootPane.NONE) {
				installClientDecorations(root);
			}
		} else if (propertyName.equals("ancestor")) {
			uninstallWindowListeners(root);

			if (((JRootPane) e.getSource()).getWindowDecorationStyle() != JRootPane.NONE) {
				installWindowListeners(root, root.getParent());
			}
		}
		return;
	}

	private void removeEscapeMenuHandlers() {
		// Remove the handlers registered at addEscapeMenuHandlers()
		java.security.AccessController
		.doPrivileged(new java.security.PrivilegedAction() {

			@Override
			public Object run() {
				Toolkit.getDefaultToolkit().removeAWTEventListener(
						mouseHandler);

				// System.out.println("escapeMenuHandler removed");
				return null;
			}
		});

		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.removeKeyEventPostProcessor(keyPostProcessor);
	}

	/**
	 * Sets the window title pane -- the JComponent used to provide a plaf a way
	 * to override the native operating system's window title pane with one
	 * whose look and feel are controlled by the plaf. The plaf creates and sets
	 * this value; the default is null, implying a native operating system
	 * window title pane.
	 * 
	 * @param content
	 *            the <code>JComponent</code> to use for the window title pane.
	 */
	private void setTitlePane(final JRootPane root, final JComponent titlePane) {
		final JLayeredPane layeredPane = root.getLayeredPane();
		final JComponent oldTitlePane = getTitlePane();

		if (oldTitlePane != null) {
			oldTitlePane.setVisible(false);
			layeredPane.remove(oldTitlePane);
		}

		if (titlePane != null) {
			layeredPane.add(titlePane, JLayeredPane.FRAME_CONTENT_LAYER);
			titlePane.setVisible(true);
		}

		this.titlePane = titlePane;
		root.validate();
		root.repaint();
	}

	/**
	 * Removes any border that may have been installed.
	 */
	private void uninstallBorder(final JRootPane root) {
		LookAndFeel.uninstallBorder(root);
	}

	/**
	 * Uninstalls any state that <code>installClientDecorations</code> has
	 * installed.
	 * <p>
	 * NOTE: This may be called if you haven't installed client decorations yet
	 * (ie before <code>installClientDecorations</code> has been invoked).
	 */
	private void uninstallClientDecorations(final JRootPane root) {
		uninstallBorder(root);
		uninstallWindowListeners(root);
		setTitlePane(root, null);
		uninstallLayout(root);
		root.repaint();
		root.revalidate();
		// Reset the cursor, as we may have changed it to a resize cursor
		if (window != null) {
			window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		window = null;
	}

	/**
	 * Uninstalls the previously installed <code>LayoutManager</code>.
	 */
	private void uninstallLayout(final JRootPane root) {
		if (savedOldLayout != null) {
			root.setLayout(savedOldLayout);
			savedOldLayout = null;
		}
	}

	@Override
	protected void uninstallListeners(final JRootPane root) {
		super.uninstallListeners(root);

		root.removeFocusListener(this);
	}

	/**
	 * Invokes supers implementation to uninstall any of its state. This will
	 * also reset the <code>LayoutManager</code> of the <code>JRootPane</code>.
	 * If a <code>Component</code> has been added to the <code>JRootPane</code>
	 * to render the window decoration style, this method will remove it.
	 * Similarly, this will revert the Border and LayoutManager of the
	 * <code>JRootPane</code> to what it was before <code>installUI</code> was
	 * invoked.
	 * 
	 * @param c
	 *            the JRootPane to uninstall state from
	 */
	@Override
	public void uninstallUI(final JComponent c) {
		super.uninstallUI(c);
		uninstallClientDecorations(root);

		layoutManager = null;
		mouseInputListener = null;
		root = null;
	}

	/**
	 * Uninstalls the necessary Listeners on the <code>Window</code> the
	 * Listeners were last installed on.
	 */
	private void uninstallWindowListeners(final JRootPane root) {
		if (window != null) {
			window.removeMouseListener(mouseInputListener);
			window.removeMouseMotionListener(mouseInputListener);
		}
	}
}
