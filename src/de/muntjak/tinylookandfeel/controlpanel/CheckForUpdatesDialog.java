/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import de.muntjak.tinylookandfeel.TinyLookAndFeel;

public class CheckForUpdatesDialog extends JDialog {

	// local - resolves to D:\\htdocs\tinylaf\...
	// private static final String CHECK_UPDATES_URL =
	// "http://localhost:8080/tinylaf/checkforupdate.html";

	private class UpdateDialog extends JDialog {

		UpdateDialog(final Dialog owner, final String version) {
			super(CheckForUpdatesDialog.this, "Update Information", true);

			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			getContentPane().setLayout(new BorderLayout());

			final String msg = "<html>"
				+ "An updated version of TinyLaF is available:<br>"
				+ version
				+ "<br>"
				+ "It can be downloaded at www.muntjak.de/hans/java/tinylaf/.";
			JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
			p.add(new JLabel(msg));
			getContentPane().add(p, BorderLayout.CENTER);

			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));

			JButton b = new JButton("Copy Link");
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					final Clipboard cb = Toolkit.getDefaultToolkit()
					.getSystemClipboard();

					if (cb == null) {
						JOptionPane.showMessageDialog(UpdateDialog.this,
								"System Clipboard not available.", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						final StringSelection ss = new StringSelection(
						"http://www.muntjak.de/hans/java/tinylaf/");
						cb.setContents(ss, ss);
					}
				}
			});
			p.add(b);

			b = new JButton("Close");
			getRootPane().setDefaultButton(b);
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					UpdateDialog.this.dispose();
				}
			});
			p.add(b);

			getContentPane().add(p, BorderLayout.SOUTH);

			pack();

			final Point loc = owner.getLocationOnScreen();
			loc.x += (owner.getWidth() - getWidth()) / 2;
			loc.y += (owner.getHeight() - getHeight()) / 2;

			setLocation(loc);
			setVisible(true);
		}
	}

	// web
	private static final String CHECK_UPDATES_URL = "http://www.muntjak.de/hans/java/tinylaf/checkforupdate.html";

	static void showDialog(final Frame parent) {
		new CheckForUpdatesDialog(parent);
	}

	private CheckForUpdatesDialog(final Frame parent) {
		super(parent, "Check for Updates", true);

		setupUI(parent);
	}

	private String checkForUpdate() {
		InputStream is = null;

		try {
			final URL url = new URL(CHECK_UPDATES_URL);

			try {
				final HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();

				conn.setRequestProperty("User-Agent", "TinyLaF");

				final Object content = conn.getContent();

				if (!(content instanceof InputStream)) {
					return "An exception occured while checking for updates."
					+ "\n\nException was: Content is no InputStream";
				}

				is = (InputStream) content;
			} catch (final IOException ex) {
				// ex.printStackTrace();
				return "An exception occured while checking for updates."
				+ "\n\nException was: " + ex.getClass().getName();
			}
		} catch (final MalformedURLException ex) {
			// ex.printStackTrace();
			return "An exception occured while checking for updates."
			+ "\n\nException was: " + ex.getClass().getName();
		}

		// read message returned from muntjak server
		try {
			final BufferedReader in = new BufferedReader(new InputStreamReader(is));

			final StringBuffer buff = new StringBuffer();
			String line;

			while ((line = in.readLine()) != null) {
				buff.append(line);
			}

			in.close();

			return buff.toString();
		} catch (final IOException ex) {
			// ex.printStackTrace();
			return "An exception occured while checking for updates."
			+ "\n\nException was: " + ex.getClass().getName();
		}
	}

	private String checkForUpdates() {
		// The string we expect is in format:
		// TinyLaF v.v.v (yyyy/m/d)
		// where v, m and d represent one or more numbers
		// and yyyy must be a 4-digit year

		// String answer = "TinyLaF 1.4.0 (2008/8/25)";
		final String answer = checkForUpdate();

		if (answer.indexOf("Exception") != -1)
			return answer;

		if (!answer
				.matches("TinyLaF \\d+\\.\\d+\\.\\d+\\s\\(\\d\\d\\d\\d/\\d+/\\d+\\)")) {
			System.out.println("? Invalid response format: '" + answer + "'");

			return "An exception occured while checking for updates."
			+ "\n\nException was: Invalid response.";
		}

		final String version = answer.substring(8);
		final String expectedVersion = TinyLookAndFeel.VERSION_STRING + " ("
		+ TinyLookAndFeel.DATE_STRING + ")";

		if (!version.equals(expectedVersion)) {
			return answer;
		} else {
			return "No updated version of TinyLaF available.";
		}
	}

	private void setupUI(final Frame frame) {
		getContentPane().setLayout(new BorderLayout(0, 0));

		final JPanel p = new JPanel(new BorderLayout(0, 12));
		final JLabel l = new JLabel(
				"<html>"
				+ "When checking for updates, TinyLaF will connect to <b>muntjak.de</b>"
				+ "<br>via HTTP. No personal data will be transmitted.");
		l.setBorder(new EmptyBorder(8, 8, 0, 8));
		p.add(l, BorderLayout.NORTH);

		JButton b = new JButton("Check for updates now");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final String msg = checkForUpdates();
				final int index = msg.indexOf("Exception was: ");

				if (index != -1) {
					final String title = msg.substring(index + 15);

					JOptionPane.showMessageDialog(CheckForUpdatesDialog.this,
							msg, title, JOptionPane.PLAIN_MESSAGE);
				} else {
					if (msg.startsWith("No ")) {
						JOptionPane
						.showMessageDialog(CheckForUpdatesDialog.this,
								msg, "Update Information",
								JOptionPane.PLAIN_MESSAGE);
					} else {
						new UpdateDialog(CheckForUpdatesDialog.this, msg);
					}
				}
			}
		});

		JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
		flow.add(b);
		p.add(flow, BorderLayout.CENTER);
		p.add(new JSeparator(), BorderLayout.SOUTH);

		getContentPane().add(p, BorderLayout.CENTER);

		b = new JButton("Close");
		getRootPane().setDefaultButton(b);
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dispose();
			}
		});
		flow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
		flow.add(b);
		getContentPane().add(flow, BorderLayout.SOUTH);

		pack();

		final Dimension size = getSize();
		setLocation(frame.getLocationOnScreen().x
				+ (frame.getWidth() - size.width) / 2,
				frame.getLocationOnScreen().y
				+ (frame.getHeight() - size.height) / 2);
		setVisible(true);
	}
}
