/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Dimension;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;

/**
 * @author Michael Hagen
 */
public class BaseFileChooserUI extends MetalFileChooserUI {

	// ------------------------------------------------------------------------------
	protected class BaseFileView extends BasicFileView {

		@Override
		public Icon getIcon(final File f) {
			Icon icon = getCachedIcon(f);
			if (icon != null) {
				return icon;
			}
			if (f != null) {
				icon = getFileChooser().getFileSystemView().getSystemIcon(f);
			}
			if (icon == null) {
				icon = super.getIcon(f);
			}
			cacheIcon(f, icon);
			return icon;
		}
	}

	public static ComponentUI createUI(final JComponent c) {
		return new BaseFileChooserUI((JFileChooser) c);
	}
	private FileView fileView = null;
	// Preferred and Minimum sizes for the dialog box
	private static int PREF_WIDTH = 580;

	private static int PREF_HEIGHT = 340;

	private static Dimension PREF_SIZE = new Dimension(PREF_WIDTH, PREF_HEIGHT);

	public BaseFileChooserUI(final JFileChooser fileChooser) {
		super(fileChooser);
		fileView = new BaseFileView();
	}

	@Override
	public FileView getFileView(final JFileChooser fc) {
		if (JTattooUtilities.getJavaVersion() < 1.4) {
			return super.getFileView(fc);
		} else {
			return fileView;
		}
	}

	/**
	 * Returns the preferred size of the specified <code>JFileChooser</code>.
	 * The preferred size is at least as large, in both height and width, as the
	 * preferred size recommended by the file chooser's layout manager.
	 * 
	 * @param c
	 *            a <code>JFileChooser</code>
	 * @return a <code>Dimension</code> specifying the preferred width and
	 *         height of the file chooser
	 */
	@Override
	public Dimension getPreferredSize(final JComponent c) {
		final int prefWidth = PREF_SIZE.width;
		final Dimension d = c.getLayout().preferredLayoutSize(c);
		if (d != null) {
			return new Dimension(d.width < prefWidth ? prefWidth : d.width,
					d.height < PREF_SIZE.height ? PREF_SIZE.height : d.height);
		} else {
			return new Dimension(prefWidth, PREF_SIZE.height);
		}
	}
}
