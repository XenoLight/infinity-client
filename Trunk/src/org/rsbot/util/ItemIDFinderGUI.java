package org.rsbot.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 * This looks up the id number of a given item input into the text field
 * @author Sorcermus
 */
public class ItemIDFinderGUI extends JFrame {

	private static final long serialVersionUID = -4351237412136068747L;
	public static final Logger log = Logger.getLogger(ItemIDFinderGUI.class.getName());
    private JPanel jPanel1 = new JPanel();
    private JLabel jLabel1 = new JLabel();
    private JLabel jLabel2 = new JLabel();
    private JLabel jLabel3 = new JLabel();
    private JScrollPane jScrollPane1 = new JScrollPane();
    public JSpinner searchNum = new JSpinner();
    private JList searchResults = new JList();
    private JTextField term = new JTextField();

    public ItemIDFinderGUI() {

    	setIconImage(new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/log.png").getImage());

        setTitle("Item ID Search");
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(" Item ID Lookup "));

        searchResults.setModel(new AbstractListModel() {

			private static final long serialVersionUID = 1L;
			String[] strings = {" "};

            public int getSize() {
                return this.strings.length;
            }

            public Object getElementAt(int i) {
                return this.strings[i];
            }
        });
        jScrollPane1.setViewportView(searchResults);

        jLabel1.setText("Max Results:");

        searchNum.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        searchNum.setValue(Integer.valueOf(1000));

        term.setHorizontalAlignment(0);
        term.setText("Name of item");
        term.addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent evt) {
                termKeyTyped(evt);
            }
        });

        jLabel2.setText("Search is case sensitive");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Search Results");

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(jLabel3, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE).addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(searchNum, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)).addComponent(term, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE).addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)).addContainerGap()));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(searchNum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(term, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2).addContainerGap(80, Short.MAX_VALUE)));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap(18, Short.MAX_VALUE).addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap(10, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE).addContainerGap(5, Short.MAX_VALUE)));
        pack();
    }

    private void termKeyTyped(KeyEvent evt) {
        File file = new File(GlobalConfiguration.Paths.getItemIDCache());
        String search = null;
        if ((search = term.getText()) != null) {
            search = search.toLowerCase();
            if (search.length() >= 1) {
                try {
                    ArrayList<String> list = new ArrayList<String>();

                    final BufferedReader in = new BufferedReader(new FileReader(file));
                    BufferedReader reader = new BufferedReader(in);
                    String results = null;
                    while ((results = reader.readLine()) != null) {
                        results = results.toLowerCase();
                        if ((!results.contains(search)) || (list.contains(results.split(",")[1]))) {
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
