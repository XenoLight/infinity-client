package org.rsbot.gui.frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.rsbot.util.GlobalConfiguration;

/**
 * This looks up the id number of a given item input into the text field
 * @author Sorcermus
 */
public class NPCID extends JFrame {

    public static final Logger log = Logger.getLogger(NPCID.class.getName());
    public JSpinner searchNum = new JSpinner();
    private JList searchResults = new JList();
    private JTextField term = new JTextField();

    public NPCID() {
        setTitle("NPC ID Search");
		setVisible(true);
		setBackground(new Color(245, 245, 245));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
			}
		});
		setResizable(false);
		final File icon = new File(GlobalConfiguration.Paths.getIconDirectory()
				+ "/log.png");
		setIconImage(GlobalConfiguration.getImageFile(icon));

        GridBagConstraints gc;
        setLayout(new java.awt.GridBagLayout());

        final JPanel borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createTitledBorder(" NPC ID Search Resaults "));
        borderPanel.setLayout(new GridBagLayout());


        final JLabel searchLable = new JLabel();
        searchLable.setText("Search Resaults");
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(0, 6, 6, 6);
        borderPanel.add(searchLable, gc);

        searchResults.setModel(new AbstractListModel() {

            String[] strings = {""};

            public int getSize() {
                return this.strings.length;
            }

            public Object getElementAt(int i) {
                return this.strings[i];
            }
        });

        final JScrollPane searchPanel = new JScrollPane();
        searchPanel.setViewportView(searchResults);

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 1;
        gc.ipadx = 175;
        gc.ipady = 175;
        gc.insets = new Insets(0, 6, 6, 6);
        borderPanel.add(searchPanel, gc);

        final JPanel spinnerPanel = new JPanel();
        spinnerPanel.setLayout(new GridBagLayout());

        final JLabel maxLable = new JLabel();
        maxLable.setText("Max Resaults: ");
        searchNum.setValue(Integer.valueOf(500));
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(0, 30, 0, 30);
        spinnerPanel.add(maxLable, gc);
        gc = new GridBagConstraints();
        gc.gridx = 1;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(0, 20, 0, 20);
        spinnerPanel.add(searchNum, gc);

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 2;
        gc.insets = new Insets(0, 6, 6, 6);
        borderPanel.add(spinnerPanel, gc);

        term.setHorizontalAlignment(0);
        term.setText("Name of NPC");
        term.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent evt) {
                termKeyTyped(evt);
            }
        });
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 3;
        gc.ipadx = 100;
        gc.insets = new Insets(0, 6, 6, 6);
        borderPanel.add(term, gc);

        final JLabel sensLable = new JLabel();
        sensLable.setText("Search is not case sensitive");
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 4;
        gc.insets = new Insets(0, 6, 6, 6);
        borderPanel.add(sensLable, gc);

        final JButton helpButton = new JButton();
        helpButton.setText("Help?");
        helpButton.setPreferredSize(new Dimension(65, 25));
        helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
            }
        });
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 5;
        gc.insets = new Insets(9, 9, 9, 9);
        borderPanel.add(helpButton, gc);

        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(0, 6, 6, 6);
        add(borderPanel, gc);
        setLocationRelativeTo(getParent());
        pack();
    }

    private void termKeyTyped(KeyEvent evt) {
        File file = new File(GlobalConfiguration.Paths.getNPCIDDirectory());
        String search = null;
        if ((search = term.getText()) != null) {
            search = search.toLowerCase();
            if (search.length() >= 1) {
                try {
                    ArrayList list = new ArrayList();
                    final BufferedReader in = new BufferedReader(new FileReader(file));
                    BufferedReader reader = new BufferedReader(in);
                    String results = null;
                    while ((results = reader.readLine()) != null) {
                        results = results.toLowerCase();
                        if ((!results.contains(search))
                                || (list.contains(results.split(",")[1]))) {
                            continue;
                        }
                        list.add(results.split(",")[0] + " "
                                + results.split(",")[1]);
                    }

                    DefaultListModel model = new DefaultListModel();
                    if (list.size() > Integer.parseInt(searchNum.getValue().toString())) {
                        model.addElement("Search too large");
                    } else if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            model.addElement(list.get(i));
                        }
                    }
                    if (model.isEmpty()) {
                        model.addElement("No results");
                    }
                    searchResults.setModel(model);
                } catch (Exception e) {
                    log.info("Not working");
                }
            }
        }
    }
}
