/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * 
 * @author Michael Hagen
 */

// ToDo:
// - Auf dem Mac scheint es ein Problem mit dem zeichnen des Aluminium
// Hintergrunds zu geben
// - setMaximizedBounds unter Linux bei multiscreen Umgebungen funktioniert
// nicht. Aus diesem Grund
// wird in Linux die Toolbar beim maximieren verdeckt (siehe BaseTitlePane
// maximize)
public class About extends JDialog {

	public static String JTATTOO_VERSION = "Version: 1.4.2";
	private static final Dimension screenSize = Toolkit.getDefaultToolkit()
	.getScreenSize();
	private static final Dimension dlgSize = new Dimension(320, 240);
	private static int dlgPosX = (screenSize.width / 2) - (dlgSize.width / 2);
	private static int dlgPosY = (screenSize.height / 2) - (dlgSize.height / 2);

	/**
	 * Starten der Anwendung
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(final String args[]) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
			final About dlg = new About();
			dlg.setSize(dlgSize);
			dlg.setLocation(dlgPosX, dlgPosY);
			dlg.setVisible(true);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public About() {
		super((JFrame) null, "About JTattoo");
		final JPanel contentPanel = new JPanel(null);
		final JLabel titleLabel = new JLabel("JTattoo " + JTATTOO_VERSION);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setBounds(0, 20, 312, 36);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(titleLabel);

		final JLabel copyrightLabel = new JLabel(
		"(C) 2002-2010 by MH Software-Entwicklung");
		copyrightLabel.setBounds(0, 120, 312, 20);
		copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(copyrightLabel);

		final JButton okButton = new JButton("OK");
		okButton.setBounds(120, 170, 80, 24);
		contentPanel.add(okButton);

		setContentPane(contentPanel);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent ev) {
				System.exit(0);
			}
		});

		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent ev) {
				System.exit(0);
			}
		});
	}
}
