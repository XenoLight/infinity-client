/*
 * 2011 Runedev development team
 * http://lazygamerz.org
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 3 of the Licence, or (at your opinion) any
 * later version.
 *
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 */
package org.rsbot.util;

import java.awt.Window;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.rsbot.gui.UpdateGUI;

/**
 * Handles the lookup and download of the client jar files when updated
 * 
 * @Auther Sorcermus for RuneDev - version 1.0
 */
public class Update {

	public static int update = -1;
	private final Window parent;
	public static UpdateGUI download = null;
	private static byte[] buffer = new byte[1024];

	private static int getLatestVersion() {
		try {
			final InputStream is = new URL(GlobalConfiguration.Paths.URLs.VERSION)
			.openConnection().getInputStream();
			int off = 0;
			final byte[] b = new byte[2];
			while ((off += is.read(b, off, 2 - off)) != 2) {
			}

			return ((0xFF & b[0]) << 8) + (0xFF & b[1]);
		} catch (final IOException e) {
			return -1;
		}
	}

	public Update(final Window parent) {
		this.parent = parent;
	}

	public void checkUpdate(final boolean checkup) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
		}
		if (GlobalConfiguration.getVersion() >= getLatestVersion()) {
			return;
		}
		if (getLatestVersion() > GlobalConfiguration.getVersion()) {
			update = JOptionPane.showConfirmDialog(parent,
					"A newer version of the client is available.\n\n"
					+ "Do you wish to update?\n\n"
					+ "Choosing not to update may result\n"
					+ "in problems running the client...",
					"Update Found", JOptionPane.YES_NO_OPTION);
			if (update != 0) {
				return;
			}
			try {
				if (update == 0) {
					updateBot();
				}
			} catch (final Exception e) {
			}
		}
	}

	public void download(final String address, final String localFileName) {
		try {
			final java.io.BufferedInputStream in = new java.io.BufferedInputStream(
					new java.net.URL(address).openStream());
			final java.io.FileOutputStream file = new java.io.FileOutputStream(
					localFileName);
			final java.io.BufferedOutputStream out = new BufferedOutputStream(file,
					65535);

			int downloaded = 0;
			int percent = 0;
			int numRead = 0;
			final int totalBytes = in.available() * 3;

			while ((numRead = in.read(buffer, 0, 1024)) >= 0) {
				out.write(buffer, 0, numRead);
				downloaded += numRead / 1024;
				percent = downloaded / 24;
				UpdateGUI.jLabel4.setText("" + totalBytes);
				UpdateGUI.jLabel6.setText("" + downloaded);
				UpdateGUI.jLabel8.setText(percent + " %");
			}

			out.flush();
			out.close();
			in.close();
		} catch (final Exception exception) {
		}
	}

	public void updateBot() {
		if (download == null) {
			download = new UpdateGUI();
		}
		final String jarNew = GlobalConfiguration.NAME + ".jar";
		download(GlobalConfiguration.Paths.URLs.DOWNLOAD, jarNew);
		final Runtime run = Runtime.getRuntime();
		try {
			run.exec("java -jar " + jarNew);
			System.exit(0);
		} catch (final IOException e) {
		}

		return;
	}
}
