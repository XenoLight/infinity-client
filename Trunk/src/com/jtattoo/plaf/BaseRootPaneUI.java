/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * This source is a modified copy of javax.swing.plaf.metal.MetalRootPaneUI
 * Provides the base look and feel implementation of <code>RootPaneUI</code>.
 * <p>
 * <code>BaseRootPaneUI</code> provides support for the
 * <code>windowDecorationStyle</code> property of <code>JRootPane</code>.
 * <code>BaseRootPaneUI</code> does this by way of installing a custom
 * <code>LayoutManager</code>, a private <code>Component</code> to render the
 * appropriate widgets, and a private <code>Border</code>. The
 * <code>LayoutManager</code> is always installed, regardless of the value of
 * the <code>windowDecorationStyle</code> property, but the <code>Border</code>
 * and <code>Component</code> are only installed/added if the
 * <code>windowDecorationStyle</code> is other than <code>JRootPane.NONE</code>.
 * <p>
 * <strong>Warning:</strong> Serialized objects of this class will not be
 * compatible with future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Swing. As of 1.4, support for long term storage of all
 * JavaBeans<sup><font size="-2">TM</font></sup> has been added to the
 * <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
 * 
 * @version 1.20 04/27/04
 * @author Terry Kellerman
 * @author Michael Hagen
 * @since 1.4
 */
public class BaseRootPaneUI extends BasicRootPaneUI {
	// Konstanten aus javax.swing.JRootPane damit Attribute aus Java 1.4 sich
	// mit Java 1.3 uebersetzen lassen

	// ------------------------------------------------------------------------------
	private static class BaseRootLayout implements LayoutManager2 {

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
				if (DecorationHelper.getWindowDecorationStyle(root) != NONE
						&& (root.getUI() instanceof BaseRootPaneUI)) {
					final BaseTitlePane titlePane = ((BaseRootPaneUI) root.getUI())
					.getTitlePane();
					int titleHeight = 0;
					if (titlePane != null) {
						titleHeight = titlePane.getSize().height;
					}
					root.getGlassPane().setBounds(i.left, i.top + titleHeight,
							w, h - titleHeight);
				} else {
					root.getGlassPane().setBounds(i.left, i.top, w, h);
				}
			}
			// Note: This is laying out the children in the layeredPane,
			// technically, these are not our children.
			if (DecorationHelper.getWindowDecorationStyle(root) != NONE
					&& (root.getUI() instanceof BaseRootPaneUI)) {
				final BaseTitlePane titlePane = ((BaseRootPaneUI) root.getUI())
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
			return MAXIMUM_SIZE;
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
			return MINIMUM_SIZE;
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

			if (DecorationHelper.getWindowDecorationStyle(root) != NONE
					&& (root.getUI() instanceof BaseRootPaneUI)) {
				final BaseTitlePane titlePane = ((BaseRootPaneUI) root.getUI())
				.getTitlePane();
				if (titlePane != null) {
					tpd = titlePane.getPreferredSize();
					if (tpd != null) {
						tpWidth = tpd.width;
					}
				}
			}

			return new Dimension(Math.max(Math.max(cpWidth, mbWidth), tpWidth)
					+ i.left + i.right, cpHeight + mbHeight + tpWidth + i.top
					+ i.bottom);
		}

		@Override
		public void removeLayoutComponent(final Component comp) {
		}
	}
	// ------------------------------------------------------------------------------
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
		 * Set to true if the drag operation is resizing the window.
		 */
		private boolean isResizingWindow;
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
		private Container savedContentPane = null;
		private ResizingPanel resizingPanel = null;

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
			if (ev.getSource() instanceof Window) {
				final Window window = (Window) ev.getSource();
				if (!(window instanceof Frame)) {
					return;
				}
				final Frame frame = (Frame) window;
				final Point convertedPoint = SwingUtilities.convertPoint(window,
						ev.getPoint(), getTitlePane());
				final int state = DecorationHelper.getExtendedState(frame);
				if (getTitlePane() != null
						&& getTitlePane().contains(convertedPoint)) {
					if ((ev.getClickCount() % 2) == 0
							&& ((ev.getModifiers() & InputEvent.BUTTON1_MASK) != 0)) {
						if (frame.isResizable()) {
							if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0) {
								DecorationHelper.setExtendedState(frame, state
										& ~BaseRootPaneUI.MAXIMIZED_BOTH);
							} else {
								DecorationHelper.setExtendedState(frame, state
										| BaseRootPaneUI.MAXIMIZED_BOTH);
							}
							return;
						}
					}
				}
			}
		}

		@Override
		public void mouseDragged(final MouseEvent ev) {
			if (ev.getSource() instanceof Window) {
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
					final Dimension min = MINIMUM_SIZE;
					switch (dragCursor) {
					case Cursor.E_RESIZE_CURSOR:
						adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX)
								- r.width, 0);
						break;
					case Cursor.S_RESIZE_CURSOR:
						adjust(r, min, 0, 0, 0, pt.y
								+ (dragHeight - dragOffsetY) - r.height);
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
						w.setLocation(r.x, r.y);
						w.setSize(r.width, r.height);
						w.validate();
						getRootPane().repaint();
					}
				}
			}
		}

		@Override
		public void mouseEntered(final MouseEvent ev) {
			mouseMoved(ev);
		}

		@Override
		public void mouseExited(final MouseEvent ev) {
			if (ev.getSource() instanceof Window) {
				final Window w = (Window) ev.getSource();
				w.setCursor(savedCursor);
			}
		}

		@Override
		public void mouseMoved(final MouseEvent ev) {
			if (ev.getSource() instanceof Window) {
				final JRootPane root = getRootPane();
				if (DecorationHelper.getWindowDecorationStyle(root) == NONE) {
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
						&& ((f != null && (f.isResizable() && (DecorationHelper
								.getExtendedState(f) & BaseRootPaneUI.MAXIMIZED_BOTH) == 0)) || (d != null && d
										.isResizable()))) {
					w.setCursor(Cursor.getPredefinedCursor(cursor));
				} else {
					w.setCursor(savedCursor);
				}
			}
		}

		@Override
		public void mousePressed(final MouseEvent ev) {
			if (ev.getSource() instanceof Window) {
				final JRootPane root = getRootPane();
				if (DecorationHelper.getWindowDecorationStyle(root) == NONE) {
					return;
				}

				final Point dragWindowOffset = ev.getPoint();
				final Window w = (Window) ev.getSource();
				if (w != null) {
					w.toFront();
				}

				final Point convertedDragWindowOffset = SwingUtilities.convertPoint(
						w, dragWindowOffset, getTitlePane());

				Frame f = null;
				Dialog d = null;

				if (w instanceof Frame) {
					f = (Frame) w;
				} else if (w instanceof Dialog) {
					d = (Dialog) w;
				}

				final int frameState = (f != null) ? DecorationHelper
						.getExtendedState(f) : 0;

						if (getTitlePane() != null
								&& getTitlePane().contains(convertedDragWindowOffset)) {
							if ((f != null
									&& ((frameState & BaseRootPaneUI.MAXIMIZED_BOTH) == 0) || (d != null))
									&& dragWindowOffset.y >= BORDER_DRAG_THICKNESS
									&& dragWindowOffset.x >= BORDER_DRAG_THICKNESS
									&& dragWindowOffset.x < w.getWidth()
									- BORDER_DRAG_THICKNESS) {
								isMovingWindow = true;
								dragOffsetX = dragWindowOffset.x;
								dragOffsetY = dragWindowOffset.y;
								if (window instanceof JFrame) {
									final JFrame frame = (JFrame) window;
									final PropertyChangeListener[] pcl = frame
									.getPropertyChangeListeners();
									for (int i = 0; i < pcl.length; i++) {
										pcl[i].propertyChange(new PropertyChangeEvent(
												window, "windowMoving", Boolean.FALSE,
												Boolean.FALSE));
									}
								}
								if (window instanceof JDialog) {
									final JDialog dialog = (JDialog) window;
									final PropertyChangeListener[] pcl = dialog
									.getPropertyChangeListeners();
									for (int i = 0; i < pcl.length; i++) {
										pcl[i].propertyChange(new PropertyChangeEvent(
												window, "windowMoving", Boolean.FALSE,
												Boolean.FALSE));
									}
								}
							}
						} else if (f != null && f.isResizable()
								&& ((frameState & BaseRootPaneUI.MAXIMIZED_BOTH) == 0)
								|| (d != null && d.isResizable())) {
							isResizingWindow = true;
							if (!isDynamicLayout()) {
								savedContentPane = getRootPane().getContentPane();
								final GraphicsConfiguration gc = GraphicsEnvironment
								.getLocalGraphicsEnvironment()
								.getDefaultScreenDevice()
								.getDefaultConfiguration();
								final BufferedImage bi = gc.createCompatibleImage(
										savedContentPane.getWidth(),
										savedContentPane.getHeight());
								savedContentPane.paint(bi.getGraphics());
								resizingPanel = new ResizingPanel(bi);
								getRootPane().setContentPane(resizingPanel);
							}
							dragOffsetX = dragWindowOffset.x;
							dragOffsetY = dragWindowOffset.y;
							dragWidth = w.getWidth();
							dragHeight = w.getHeight();
							dragCursor = getCursor(calculateCorner(w,
									dragWindowOffset.x, dragWindowOffset.y));
							if (window instanceof JFrame) {
								final JFrame frame = (JFrame) window;
								final PropertyChangeListener[] pcl = frame
								.getPropertyChangeListeners();
								for (int i = 0; i < pcl.length; i++) {
									pcl[i].propertyChange(new PropertyChangeEvent(
											window, "windowResizing", Boolean.FALSE,
											Boolean.FALSE));
								}
							}
							if (window instanceof JDialog) {
								final JDialog dialog = (JDialog) window;
								final PropertyChangeListener[] pcl = dialog
								.getPropertyChangeListeners();
								for (int i = 0; i < pcl.length; i++) {
									pcl[i].propertyChange(new PropertyChangeEvent(
											window, "windowResizing", Boolean.FALSE,
											Boolean.FALSE));
								}
							}
						}
			}
		}

		@Override
		public void mouseReleased(final MouseEvent ev) {
			if (ev.getSource() instanceof Window) {
				final Window w = (Window) ev.getSource();
				if (w != null) {
					if (!isDynamicLayout() && isResizingWindow) {
						getRootPane().setContentPane(savedContentPane);
						getRootPane().updateUI();
						resizingPanel = null;
					} else if (dragCursor != 0 && !window.isValid()) {
						// Some Window systems validate as you resize, others
						// won't,
						// thus the check for validity before repainting.
						w.validate();
						getRootPane().repaint();
					}

					if (window instanceof JFrame) {
						final JFrame frame = (JFrame) window;
						final PropertyChangeListener[] pcl = frame
						.getPropertyChangeListeners();
						for (int i = 0; i < pcl.length; i++) {
							if (isMovingWindow) {
								pcl[i].propertyChange(new PropertyChangeEvent(
										window, "windowMoved", Boolean.FALSE,
										Boolean.FALSE));
							} else {
								pcl[i].propertyChange(new PropertyChangeEvent(
										window, "windowResized", Boolean.FALSE,
										Boolean.FALSE));
							}
						}
					}
					if (window instanceof JDialog) {
						final JDialog dialog = (JDialog) window;
						final PropertyChangeListener[] pcl = dialog
						.getPropertyChangeListeners();
						for (int i = 0; i < pcl.length; i++) {
							if (isMovingWindow) {
								pcl[i].propertyChange(new PropertyChangeEvent(
										window, "windowMoved", Boolean.FALSE,
										Boolean.FALSE));
							} else {
								pcl[i].propertyChange(new PropertyChangeEvent(
										window, "windowResized", Boolean.FALSE,
										Boolean.FALSE));
							}
						}
					}
				}
				isMovingWindow = false;
				isResizingWindow = false;
				dragCursor = 0;
			}
		}
	}
	// ------------------------------------------------------------------------------
	private static class ResizingPanel extends JPanel {

		private BufferedImage bi = null;

		public ResizingPanel(final BufferedImage bi) {
			super();
			this.bi = bi;
		}

		@Override
		public void paint(final Graphics g) {
			super.paint(g);
			g.drawImage(bi, 0, 0, null);
		}
	}
	public static final int NONE = 0;
	public static final int FRAME = 1;
	public static final int PLAIN_DIALOG = 2;
	public static final int INFORMATION_DIALOG = 3;
	public static final int ERROR_DIALOG = 4;
	public static final int COLOR_CHOOSER_DIALOG = 5;
	public static final int FILE_CHOOSER_DIALOG = 6;
	public static final int QUESTION_DIALOG = 7;
	public static final int WARNING_DIALOG = 8;
	// Konstanten aus java.awt.Frame damit Attribute aus Java 1.4 sich mit Java
	// 1.3 uebersetzen lassen
	public static final int MAXIMIZED_HORIZ = 2;
	public static final int MAXIMIZED_VERT = 4;
	public static final int MAXIMIZED_BOTH = MAXIMIZED_VERT | MAXIMIZED_HORIZ;
	private static final String[] borderKeys = new String[] { null,
		"RootPane.frameBorder", "RootPane.plainDialogBorder",
		"RootPane.informationDialogBorder", "RootPane.errorDialogBorder",
		"RootPane.colorChooserDialogBorder",
		"RootPane.fileChooserDialogBorder",
		"RootPane.questionDialogBorder", "RootPane.warningDialogBorder" };
	/**
	 * The minimum/maximum size of a Window
	 */
	private static final Dimension MINIMUM_SIZE = new Dimension(120, 80);
	private static final Dimension MAXIMUM_SIZE = Toolkit.getDefaultToolkit()
	.getScreenSize();
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
	private BaseTitlePane titlePane;
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
	 * <code>Cursor</code> used to track the cursor set by the user. This is
	 * initially <code>Cursor.DEFAULT_CURSOR</code>.
	 */
	/**
	 * Creates a UI for a <code>JRootPane</code>.
	 * 
	 * @param c
	 *            the JRootPane the RootPaneUI will be created for
	 * @return the RootPaneUI implementation for the passed in JRootPane
	 */
	public static ComponentUI createUI(final JComponent c) {
		return new BaseRootPaneUI();
	}

	private Cursor savedCursor = null;

	/**
	 * Returns a <code>LayoutManager</code> that will be set on the
	 * <code>JRootPane</code>.
	 */
	public LayoutManager createLayoutManager() {
		return new BaseRootLayout();
	}

	/**
	 * Returns the <code>JComponent</code> to render the window decoration
	 * style.
	 */
	public BaseTitlePane createTitlePane(final JRootPane root) {
		return new BaseTitlePane(root, this);
	}

	/**
	 * Returns a <code>MouseListener</code> that will be added to the
	 * <code>Window</code> containing the <code>JRootPane</code>.
	 */
	public MouseInputListener createWindowMouseInputListener(final JRootPane root) {
		return new MouseInputHandler();
	}

	public JRootPane getRootPane() {
		return root;
	}

	/**
	 * Returns the <code>JComponent</code> rendering the title pane. If this
	 * returns null, it implies there is no need to render window decorations.
	 * 
	 * @return the current window title pane, or null
	 * @see #setTitlePane
	 */
	public BaseTitlePane getTitlePane() {
		return titlePane;
	}

	public void installBorder(final JRootPane root) {
		final int style = DecorationHelper.getWindowDecorationStyle(root);
		if (style == NONE) {
			LookAndFeel.uninstallBorder(root);
		} else {
			LookAndFeel.installBorder(root, borderKeys[style]);
		}
	}

	public void installClientDecorations(final JRootPane root) {
		installBorder(root);
		setTitlePane(root, createTitlePane(root));
		installWindowListeners(root, root.getParent());
		installLayout(root);
		if (window != null) {
			savedCursor = window.getCursor();
			root.revalidate();
			root.repaint();
		}
	}

	/**
	 * Installs the appropriate LayoutManager on the <code>JRootPane</code> to
	 * render the window decorations.
	 */
	public void installLayout(final JRootPane root) {
		if (layoutManager == null) {
			layoutManager = createLayoutManager();
		}
		savedOldLayout = root.getLayout();
		root.setLayout(layoutManager);
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		root = (JRootPane) c;
		if (DecorationHelper.getWindowDecorationStyle(root) != NONE) {
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
	public void installWindowListeners(final JRootPane root, final Component parent) {
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
			// fixes a problem with netbeans, decorated windows and java 1.5
			// the MetalLookAndFeel seems to have the same problem
			if ((JTattooUtilities.getJavaVersion() >= 1.5)
					&& (JTattooUtilities.getJavaVersion() <= 1.6)) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (window != null) {
							if (window instanceof JFrame) {
								final JFrame frame = (JFrame) window;
								frame.update(frame.getGraphics());
							}
						}
					}
				});
			}
		}
	}

	private boolean isDynamicLayout() {
		return AbstractLookAndFeel.getTheme().isDynamicLayout();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		super.propertyChange(e);

		final String propertyName = e.getPropertyName();
		if (propertyName == null) {
			return;
		}

		final JRootPane root = (JRootPane) e.getSource();
		if (propertyName.equals("windowDecorationStyle")) {
			final int style = DecorationHelper.getWindowDecorationStyle(root);

			// This is potentially more than needs to be done,
			// but it rarely happens and makes the install/uninstall process
			// simpler. BaseTitlePane also assumes it will be recreated if
			// the decoration style changes.
			uninstallClientDecorations(root);
			if (style != NONE) {
				installClientDecorations(root);
			}
		} else if (propertyName.equals("ancestor")) {
			uninstallWindowListeners(root);
			if (DecorationHelper.getWindowDecorationStyle(root) != NONE) {
				installWindowListeners(root, root.getParent());
			}
		}
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
	public void setTitlePane(final JRootPane root, final BaseTitlePane titlePane) {
		final JLayeredPane layeredPane = root.getLayeredPane();
		final BaseTitlePane oldTitlePane = getTitlePane();

		if (oldTitlePane != null) {
			oldTitlePane.setVisible(false);
			layeredPane.remove(oldTitlePane);
		}
		if (titlePane != null) {
			layeredPane.add(titlePane, JLayeredPane.FRAME_CONTENT_LAYER);
			titlePane.setVisible(true);
		}
		this.titlePane = titlePane;
	}

	/**
	 * Removes any border that may have been installed.
	 */
	public void uninstallBorder(final JRootPane root) {
		LookAndFeel.uninstallBorder(root);
	}

	public void uninstallClientDecorations(final JRootPane root) {
		uninstallBorder(root);
		uninstallWindowListeners(root);
		setTitlePane(root, null);
		uninstallLayout(root);
		final int style = DecorationHelper.getWindowDecorationStyle(root);
		if (style == NONE) {
			root.repaint();
			root.revalidate();
		}
		// Reset the cursor, as we may have changed it to a resize cursor
		if (window != null) {
			window.setCursor(savedCursor);
		}
		window = null;
	}

	public void uninstallLayout(final JRootPane root) {
		if (savedOldLayout != null) {
			root.setLayout(savedOldLayout);
			savedOldLayout = null;
		}
	}

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
	public void uninstallWindowListeners(final JRootPane root) {
		if (window != null) {
			window.removeMouseListener(mouseInputListener);
			window.removeMouseMotionListener(mouseInputListener);
		}
	}
}
