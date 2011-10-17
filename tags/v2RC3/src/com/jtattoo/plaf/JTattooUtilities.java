/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * @author Michael Hagen
 */
public class JTattooUtilities {

	private static final boolean isWindows = System.getProperty("os.name")
	.toLowerCase().indexOf("windows") != -1;
	private static final boolean isOS2 = System.getProperty("os.name")
	.toLowerCase().indexOf("os/2") != -1;
	private static final boolean isMac = System.getProperty("os.name")
	.toLowerCase().indexOf("mac") != -1;
	private static final boolean isLinux = System.getProperty("os.name")
	.toLowerCase().indexOf("linux") != -1;
	private static final boolean isSunOS = System.getProperty("os.name")
	.toLowerCase().indexOf("sunos") != -1;
	private static final boolean isAIX = System.getProperty("os.name")
	.toLowerCase().indexOf("aix") != -1;
	private static final boolean isHPUX = System.getProperty("os.name")
	.toLowerCase().indexOf("hpux") != -1;
	private static final boolean isFreeBSD = System.getProperty("os.name")
	.toLowerCase().indexOf("freebsd") != -1;
	private static final boolean isHiresScreen = Toolkit.getDefaultToolkit()
	.getScreenSize().width > 1280;
	private static Double javaVersion = null;
	private static final String ELLIPSIS = "...";

	public static void draw3DBorder(final Graphics g, final Color c1, final Color c2, final int x,
			final int y, final int w, final int h) {
		ColorHelper.median(c1, c2);
		final int x2 = x + w - 1;
		final int y2 = y + h - 1;
		g.setColor(c1);
		g.drawLine(x, y, x2, y);
		g.drawLine(x, y, x, y2);
		g.setColor(c2);
		g.drawLine(x + 1, y2, x2, y2);
		g.drawLine(x2, y + 1, x2, y2);
		// g.setColor(c3);
		// g.drawLine(x2, y, x2, y);
		// g.drawLine(x, y2, x, y2);
	}

	public static void drawBorder(final Graphics g, final Color c, final int x, final int y, final int w,
			final int h) {
		g.setColor(c);
		g.drawRect(x, y, w - 1, h - 1);
	}

	public static void drawRound3DBorder(final Graphics g, final Color c1, final Color c2, final int x,
			final int y, final int w, final int h) {
		final Graphics2D g2D = (Graphics2D) g;
		final int x2 = x + w;
		final int y2 = y + h;
		final int d = h;
		final int r = h / 2;
		final Color cm = ColorHelper.median(c1, c2);
		final Color c1m = ColorHelper.median(c1, cm);
		final Color c2m = ColorHelper.median(c2, cm);

		final Object savedRederingHint = g2D
		.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// oben
		g2D.setColor(c1);
		g2D.drawLine(x + r, y, x2 - r, y);
		// rechts
		g2D.drawLine(x, y + r, x, y2 - r);
		// unten
		g2D.setColor(c2);
		g2D.drawLine(x + r, y2, x2 - r, y2);
		// links
		g2D.drawLine(x2, y + r, x2, y2 - r);

		// links
		g2D.setColor(c1);
		g2D.drawArc(x, y, d, d, 90, 45);
		g2D.setColor(c1m);
		g2D.drawArc(x, y, d, d, 135, 45);
		g2D.setColor(cm);
		g2D.drawArc(x, y, d, d, 180, 45);
		g2D.setColor(c2m);
		g2D.drawArc(x, y, d, d, 225, 45);
		// rechts
		g2D.setColor(c1m);
		g2D.drawArc(x2 - d, y, d, d, 45, 45);
		g2D.setColor(cm);
		g2D.drawArc(x2 - d, y, d, d, 0, 45);
		g2D.setColor(c2m);
		g2D.drawArc(x2 - d, y, d, d, -45, 45);
		g2D.setColor(c2);
		g2D.drawArc(x2 - d, y, d, d, -90, 45);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
	}

	public static void drawRoundBorder(final Graphics g, final Color c, final int x, final int y,
			final int w, final int h, final int r) {
		final Graphics2D g2D = (Graphics2D) g;
		final Object savedRederingHint = g2D
		.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setColor(c);
		g2D.drawRoundRect(x, y, w - 1, h - 1, r, r);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRederingHint);
	}

	public static void drawString(final JComponent c, final Graphics g, final String text, final int x,
			final int y) {
		final Graphics2D g2D = (Graphics2D) g;
		Object savedRenderingHint = null;
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			savedRenderingHint = g2D
			.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
		}
		if (getJavaVersion() >= 1.6) {
			try {
				final Class swingUtilities2Class = Class
				.forName("sun.swing.SwingUtilities2");
				final Class classParams[] = { JComponent.class, Graphics.class,
						String.class, Integer.TYPE, Integer.TYPE };
				final Method m = swingUtilities2Class.getMethod("drawString",
						classParams);
				final Object methodParams[] = { c, g, text, new Integer(x),
						new Integer(y) };
				m.invoke(null, methodParams);
			} catch (final Exception ex) {
				g.drawString(text, x, y);
			}
		} else {
			g.drawString(text, x, y);
		}
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					savedRenderingHint);
		}
	}

	public static void drawStringUnderlineCharAt(final JComponent c, final Graphics g,
			final String text, final int underlinedIndex, final int x, final int y) {
		final Graphics2D g2D = (Graphics2D) g;
		Object savedRenderingHint = null;
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			savedRenderingHint = g2D
			.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					AbstractLookAndFeel.getTheme().getTextAntiAliasingHint());
		}
		if (getJavaVersion() >= 1.6) {
			try {
				final Class swingUtilities2Class = Class
				.forName("sun.swing.SwingUtilities2");
				final Class classParams[] = { JComponent.class, Graphics.class,
						String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE };
				final Method m = swingUtilities2Class.getMethod(
						"drawStringUnderlineCharAt", classParams);
				final Object methodParams[] = { c, g, text,
						new Integer(underlinedIndex), new Integer(x),
						new Integer(y) };
				m.invoke(null, methodParams);
			} catch (final Exception ex) {
				BasicGraphicsUtils.drawString(g, text, underlinedIndex, x, y);
			}
		} else if (getJavaVersion() >= 1.4) {
			BasicGraphicsUtils.drawStringUnderlineCharAt(g, text,
					underlinedIndex, x, y);
		} else {
			BasicGraphicsUtils.drawString(g, text, underlinedIndex, x, y);
		}
		if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					savedRenderingHint);
		}
	}

	public static void fillHorGradient(final Graphics g, final Color[] colors, final int x,
			final int y, final int w, final int h) {
		final int steps = colors.length;
		final double dy = (double) h / (double) (steps);
		if (dy <= 3.001) {
			int y1 = y;
			for (int i = 0; i < steps; i++) {
				final int y2 = y + (int) Math.round(i * dy);
				g.setColor(colors[i]);
				if (i == (steps - 1)) {
					g.fillRect(x, y1, w, y + h - y1);
				} else {
					g.fillRect(x, y1, w, y2 - y1);
				}
				y1 = y2;
			}
		} else {
			smoothFillHorGradient(g, colors, x, y, w, h);
		}
	}

	public static void fillInverseHorGradient(final Graphics g, final Color[] colors,
			final int x, final int y, final int w, final int h) {
		final int steps = colors.length;
		final double dy = (double) h / (double) steps;
		if (dy <= 3.001) {
			int y1 = y;
			for (int i = 0; i < steps; i++) {
				final int y2 = y + (int) Math.round(i * dy);
				g.setColor(colors[colors.length - i - 1]);
				if (i == (steps - 1)) {
					g.fillRect(x, y1, w, y + h - y1);
				} else {
					g.fillRect(x, y1, w, y2 - y1);
				}
				y1 = y2;
			}
		} else {
			smoothFillInverseHorGradient(g, colors, x, y, w, h);
		}

	}

	public static void fillInverseVerGradient(final Graphics g, final Color[] colors,
			final int x, final int y, final int w, final int h) {
		final int steps = colors.length;
		final double dx = (double) w / (double) steps;
		int x1 = x;
		for (int i = 0; i < steps; i++) {
			final int x2 = x + (int) Math.round(i * dx);
			g.setColor(colors[colors.length - i - 1]);
			if (i == (steps - 1)) {
				g.fillRect(x1, y, x + w - x1, h);
			} else {
				g.fillRect(x1, y, x2 - x1, h);
			}
			x1 = x2;
		}
	}

	public static void fillVerGradient(final Graphics g, final Color[] colors, final int x,
			final int y, final int w, final int h) {
		final int steps = colors.length;
		final double dx = (double) w / (double) steps;
		int x1 = x;
		for (int i = 0; i < steps; i++) {
			final int x2 = x + (int) Math.round(i * dx);
			g.setColor(colors[i]);
			if (i == (steps - 1)) {
				g.fillRect(x1, y, x + w - x1, h);
			} else {
				g.fillRect(x1, y, x2 - x1, h);
			}
			x1 = x2;
		}
	}

	public static int findDisplayedMnemonicIndex(final String text, final int mnemonic) {
		if (text == null || mnemonic == '\0') {
			return -1;
		}

		final char uc = Character.toUpperCase((char) mnemonic);
		final char lc = Character.toLowerCase((char) mnemonic);

		final int uci = text.indexOf(uc);
		final int lci = text.indexOf(lc);

		if (uci == -1) {
			return lci;
		} else if (lci == -1) {
			return uci;
		} else {
			return (lci < uci) ? lci : uci;
		}
	}

	public static String getClippedText(final String text, final FontMetrics fm,
			final int maxWidth) {
		if ((text == null) || (text.length() == 0)) {
			return "";
		}
		final int width = SwingUtilities.computeStringWidth(fm, text);
		if (width > maxWidth) {
			int totalWidth = SwingUtilities.computeStringWidth(fm, ELLIPSIS);
			for (int i = 0; i < text.length(); i++) {
				totalWidth += fm.charWidth(text.charAt(i));
				if (totalWidth > maxWidth) {
					return text.substring(0, i) + ELLIPSIS;
				}
			}
		}
		return text;
	}

	public static Dimension getFrameSize(final Component c) {
		final Container parent = getRootContainer(c);
		if (parent != null) {
			return parent.getSize();
		}
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public static double getJavaVersion() {
		if (javaVersion == null) {
			try {
				final String ver = System.getProperties().getProperty("java.version");
				String version = "";
				boolean firstPoint = true;
				for (int i = 0; i < ver.length(); i++) {
					if (ver.charAt(i) == '.') {
						if (firstPoint) {
							version += ver.charAt(i);
						}
						firstPoint = false;
					} else if (Character.isDigit(ver.charAt(i))) {
						version += ver.charAt(i);
					}
				}
				javaVersion = new Double(version);
			} catch (final Exception ex) {
				javaVersion = new Double(1.3);
			}
		}
		return javaVersion.doubleValue();
	}

	public static Point getRelLocation(final Component c) {
		if (c == null || !c.isShowing()) {
			return new Point(0, 0);
		}

		final Container parent = getRootContainer(c);
		if ((parent != null) && parent.isShowing()) {
			final Point p1 = c.getLocationOnScreen();
			final Point p2 = parent.getLocationOnScreen();
			return new Point(p1.x - p2.x, p1.y - p2.y);
		}

		return new Point(0, 0);
	}

	public static Container getRootContainer(final Component c) {
		if (c != null) {
			Container parent = c.getParent();
			while ((parent != null) && !(parent instanceof JPopupMenu)
					&& !(parent instanceof JInternalFrame)
					&& !(parent instanceof Window)
					&& (parent.getParent() != null)) {
				parent = parent.getParent();
			}
			return parent;
		}
		return null;
	}

	public static boolean isActive(final JComponent c) {
		if (c == null) {
			return false;
		}

		boolean active = true;
		if (c instanceof JInternalFrame) {
			active = ((JInternalFrame) c).isSelected();
		}
		if (active) {
			Container parent = c.getParent();
			while (parent != null) {
				if (parent instanceof JInternalFrame) {
					active = ((JInternalFrame) parent).isSelected();
					break;
				}
				parent = parent.getParent();
			}
		}
		if (active) {
			active = isFrameActive(c);
		}
		return active;
	}

	public static boolean isAIX() {
		return isAIX;
	}

	public static boolean isFrameActive(final JComponent c) {
		if (c == null) {
			return false;
		}

		if (c.getTopLevelAncestor() instanceof Window) {
			return isWindowActive((Window) c.getTopLevelAncestor());
		}

		return true;
	}

	public static boolean isFreeBSD() {
		return isFreeBSD;
	}

	public static boolean isHiresScreen() {
		return isHiresScreen;
	}

	public static boolean isHPUX() {
		return isHPUX;
	}

	public static boolean isLeftToRight(final Component c) {
		return c.getComponentOrientation().isLeftToRight();
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isMac() {
		return isMac;
	}

	public static boolean isOS2() {
		return isOS2;
	}

	public static boolean isSunOS() {
		return isSunOS;
	}

	// -------------------------------------------------------------------------------------------

	public static boolean isWindowActive(final Window window) {
		if (getJavaVersion() >= 1.4) {
			try {
				final Class paramTypes[] = null;
				final Object args[] = null;
				final Method m = window.getClass().getMethod("isActive", paramTypes);
				final Boolean b = (Boolean) m.invoke(window, args);
				return b.booleanValue();
			} catch (final Exception ex) {
			}
		}
		return true;
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static void smoothFillHorGradient(final Graphics g, final Color[] colors, final int x,
			final int y, final int w, final int h) {
		final Graphics2D g2D = (Graphics2D) g;
		final int steps = colors.length;
		final double dy = (double) h / (double) (steps - 1);
		int y1 = y;
		for (int i = 0; i < steps; i++) {
			final int y2 = y + (int) Math.round(i * dy);
			if (i == (steps - 1)) {
				g2D.setPaint(null);
				g2D.setColor(colors[i]);
				g.fillRect(x, y1, w, y + h - y1);
			} else {
				g2D.setPaint(new GradientPaint(0, y1, colors[i], 0, y2,
						colors[i + 1]));
				g.fillRect(x, y1, w, y2 - y1);
			}
			y1 = y2;
		}
	}

	public static void smoothFillInverseHorGradient(final Graphics g, final Color[] colors,
			final int x, final int y, final int w, final int h) {
		final Graphics2D g2D = (Graphics2D) g;
		final int steps = colors.length;
		final double dy = (double) h / (double) steps;
		int y1 = y;
		for (int i = 0; i < steps; i++) {
			final int y2 = y + (int) Math.round(i * dy);
			g.setColor(colors[colors.length - i - 1]);
			if (i == (steps - 1)) {
				g2D.setPaint(null);
				g2D.setColor(colors[colors.length - i - 1]);
				g.fillRect(x, y1, w, y + h - y1);
			} else {
				g2D.setPaint(new GradientPaint(0, y1, colors[colors.length - i
				                                             - 1], 0, y2, colors[colors.length - i - 2]));
				g.fillRect(x, y1, w, y2 - y1);
			}
			y1 = y2;
		}
	}
}
