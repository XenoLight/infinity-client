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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalToolBarUI;

import de.muntjak.tinylookandfeel.borders.TinyToolButtonBorder;

/**
 * TinyToolBarUI
 * 
 * @version 1.1
 * @author Hans Bickel
 */
public class TinyToolBarUI extends MetalToolBarUI {

	/**
	 * An array of WeakReferences that point to JComponents. This will contain
	 * instances of JToolBars and JMenuBars and is used to find
	 * JToolBars/JMenuBars that border each other.
	 */
	private static java.util.List components = new ArrayList();

	public static final String IS_TOOL_BAR_BUTTON_KEY = "isToolbarButton";
	public static final int FLOATABLE_GRIP_SIZE = 8;

	/**
	 * The Border used for buttons in a toolbar
	 */
	private static Border toolButtonBorder = new TinyToolButtonBorder();

	/**
	 * Creates the UI delegate for the given component.
	 * 
	 * @param c
	 *            The component to create its UI delegate.
	 * @return The UI delegate for the given component.
	 */
	public static ComponentUI createUI(final JComponent c) {
		return new TinyToolBarUI();
	}

	/**
	 * Returns true if the passed in JMenuBar is above a horizontal JToolBar.
	 */
	public static boolean doesMenuBarBorderToolBar(final JMenuBar c) {
		final JToolBar tb = (JToolBar) TinyToolBarUI.findRegisteredComponentOfType(c,
				JToolBar.class);

		if (tb != null && tb.getOrientation() == SwingConstants.HORIZONTAL) {
			final JRootPane rp = SwingUtilities.getRootPane(c);
			Point point = new Point(0, 0);
			point = SwingUtilities.convertPoint(c, point, rp);
			final int menuX = point.x;
			final int menuY = point.y;
			point.x = point.y = 0;
			point = SwingUtilities.convertPoint(tb, point, rp);

			return (point.x == menuX && menuY + c.getHeight() == point.y && c
					.getWidth() == tb.getWidth());
		}

		return false;
	}

	/**
	 * Finds a previously registered component of class <code>target</code> that
	 * shares the JRootPane ancestor of <code>from</code>.
	 */
	synchronized static Object findRegisteredComponentOfType(final JComponent from,
			final Class target) {
		final JRootPane rp = SwingUtilities.getRootPane(from);

		if (rp != null) {
			for (int counter = components.size() - 1; counter >= 0; counter--) {
				final Object component = ((WeakReference) components.get(counter))
				.get();

				if (component == null) {
					// WeakReference has gone away, remove the WeakReference
					components.remove(counter);
				} else if (target.isInstance(component)
						&& SwingUtilities.getRootPane((Component) component) == rp) {
					return component;
				}
			}
		}

		return null;
	}

	/**
	 * Registers the specified component.
	 */
	synchronized static void register(final JComponent c) {
		if (c == null) {
			// Exception is thrown as convenience for callers that are
			// typed to throw an NPE.
			throw new NullPointerException("JComponent must be non-null");
		}

		components.add(new WeakReference(c));
	}

	/**
	 * Unregisters the specified component.
	 */
	synchronized static void unregister(final JComponent c) {
		for (int counter = components.size() - 1; counter >= 0; counter--) {
			components.get(counter);
			final Object target = ((WeakReference) components.get(counter)).get();

			if (target == c || target == null) {
				components.remove(counter);
			}
		}
	}

	/**
	 * Overrides BasicToolBarUI.createFloatingWindow() to return a simple dialog
	 * (which works with TinyLaF). Creates a window which contains the toolbar
	 * after it has been dragged out from its container
	 * 
	 * @return a <code>RootPaneContainer</code> object, containing the toolbar.
	 */
	@Override
	protected RootPaneContainer createFloatingWindow(final JToolBar toolbar) {
		JDialog dialog;
		final Window window = SwingUtilities.getWindowAncestor(toolbar);

		if (window instanceof Frame) {
			dialog = new JDialog((Frame) window, toolbar.getName(), false);
		} else if (window instanceof Dialog) {
			dialog = new JDialog((Dialog) window, toolbar.getName(), false);
		} else {
			dialog = new JDialog((Frame) null, toolbar.getName(), false);
		}

		dialog.setTitle(toolbar.getName());
		dialog.setResizable(false);
		final WindowListener wl = createFrameListener();
		dialog.addWindowListener(wl);

		return dialog;
	}

	/**
	 * Installs some default values for the given toolbar. The gets a rollover
	 * property.
	 * 
	 * @param mainColor
	 *            The reference of the toolbar to install its default values.
	 */
	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);

		c.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		register(c);
	}

	/**
	 * Paints the given component.
	 * 
	 * @param g
	 *            The graphics context to use.
	 * @param c
	 *            The component to paint.
	 */
	@Override
	public void paint(final Graphics g, final JComponent c) {
		if (c.getBackground() instanceof ColorUIResource) {
			g.setColor(Theme.toolBarColor.getColor());
		} else {
			g.setColor(c.getBackground());
		}

		g.fillRect(0, 0, c.getWidth(), c.getHeight());
	}

	@Override
	protected void setBorderToNormal(final Component c) {
		if (!(c instanceof AbstractButton))
			return;
		if (c instanceof JCheckBox)
			return;
		if (c instanceof JRadioButton)
			return;

		final AbstractButton b = (AbstractButton) c;

		b.setRolloverEnabled(true);
		b.putClientProperty(IS_TOOL_BAR_BUTTON_KEY, Boolean.TRUE);

		if (!(b.getBorder() instanceof UIResource)
				&& !(b.getBorder() instanceof TinyToolButtonBorder)) {
			// user has installed her own border
			return;
		}

		b.setBorder(toolButtonBorder);
	}

	/**
	 * Rewritten in 1.3. Now the border is defined through button margin.
	 */
	@Override
	protected void setBorderToRollover(final Component c) {
		setBorderToNormal(c);
	}

	@Override
	public void uninstallUI(final JComponent c) {
		super.uninstallUI(c);

		c.putClientProperty("JToolBar.isRollover", null);
		unregister(c);
	}
}