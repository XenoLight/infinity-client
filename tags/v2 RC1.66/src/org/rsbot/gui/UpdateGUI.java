package org.rsbot.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.rsbot.util.GlobalConfiguration;

public class UpdateGUI extends JFrame {

	private static final long serialVersionUID = 8646183995455154141L;
	public static JLabel jLabel1 = new JLabel();
	public static JLabel jLabel2 = new JLabel();
	public static JLabel jLabel3 = new JLabel();
	public static JLabel jLabel4 = new JLabel();
	public static JLabel jLabel5 = new JLabel();
	public static JLabel jLabel6 = new JLabel();
	public static JLabel jLabel7 = new JLabel();
	public static JLabel jLabel8 = new JLabel();

	public UpdateGUI() {
		GridBagConstraints gc;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception e) {
		}
		setIconImage(GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.ICON));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(1);
			}
		});
		setResizable(false);
		setTitle("Runedev Updater");

		final JPanel panel = new JPanel();
		setLayout(new GridBagLayout());

		panel.setBorder(BorderFactory
				.createTitledBorder(" Downloading update... "));
		panel.setLayout(new GridBagLayout());

		jLabel1.setIcon(new ImageIcon(GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.DOWNLOAD)));
		gc = new GridBagConstraints();
		gc.insets = new Insets(5, 20, 5, 20);
		panel.add(jLabel1, gc);

		jLabel2.setText("Please be patient wile your new client is being downloaded...");
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(20, 5, 20, 5);
		panel.add(jLabel2, gc);

		jLabel3.setText("Total KBs:");
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = new Insets(5, 0, 5, 0);
		panel.add(jLabel3, gc);

		jLabel4.setText("");
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5, 5, 5, 5);
		panel.add(jLabel4, gc);

		jLabel5.setText("Downloaded KBs:");
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = new Insets(5, 0, 5, 0);
		panel.add(jLabel5, gc);

		jLabel6.setText("");
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5, 5, 5, 5);
		panel.add(jLabel6, gc);

		jLabel7.setText("Percent:");
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.EAST;
		gc.insets = new Insets(5, 0, 5, 0);
		panel.add(jLabel7, gc);

		jLabel8.setText("");
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5, 5, 5, 5);
		panel.add(jLabel8, gc);

		gc = new GridBagConstraints();
		gc.insets = new Insets(11, 10, 11, 10);
		add(panel, gc);

		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}
}
