/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Frame;
import java.awt.Toolkit;
import java.lang.reflect.Method;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;

/**
 * @author Michael Hagen
 */
public class DecorationHelper {

	public static void decorateWindows(final Boolean decorate) {
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			try {
				final Class classParams[] = { Boolean.TYPE };
				Method m = JFrame.class.getMethod(
						"setDefaultLookAndFeelDecorated", classParams);
				final Object methodParams[] = { decorate };
				m.invoke(null, methodParams);
				m = JDialog.class.getMethod("setDefaultLookAndFeelDecorated",
						classParams);
				m.invoke(null, methodParams);
				System.setProperty("sun.awt.noerasebackground", "true");
			} catch (final Exception ex) {
			}
		}
	}

	public static int getExtendedState(final Frame frame) {
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			try {
				final Class paramTypes[] = null;
				final Object args[] = null;
				final Method m = frame.getClass().getMethod("getExtendedState",
						paramTypes);
				final Integer i = (Integer) m.invoke(frame, args);
				return i.intValue();
			} catch (final Exception ex) {
			}
		}
		return 0;
	}

	public static int getWindowDecorationStyle(final JRootPane root) {
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			try {
				final Class paramTypes[] = null;
				final Object args[] = null;
				final Method m = root.getClass().getMethod(
						"getWindowDecorationStyle", paramTypes);
				final Integer i = (Integer) m.invoke(root, args);
				return i.intValue();
			} catch (final Exception ex) {
			}
		}
		return 0;
	}

	public static boolean isFrameStateSupported(final Toolkit tk, final int state) {
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			try {
				final Class classParams[] = { Integer.TYPE };
				final Method m = tk.getClass().getMethod("isFrameStateSupported",
						classParams);
				final Object methodParams[] = { new Integer(state) };
				final Boolean b = (Boolean) m.invoke(tk, methodParams);
				return b.booleanValue();
			} catch (final Exception ex) {
			}
		}
		return false;
	}

	public static void setExtendedState(final Frame frame, final int state) {
		if (JTattooUtilities.getJavaVersion() >= 1.4) {
			try {
				final Class classParams[] = { Integer.TYPE };
				final Method m = frame.getClass().getMethod("setExtendedState",
						classParams);
				final Object methodParams[] = { new Integer(state) };
				m.invoke(frame, methodParams);
			} catch (final Exception ex) {
			}
		}
	}

	private DecorationHelper() {
	}
}
