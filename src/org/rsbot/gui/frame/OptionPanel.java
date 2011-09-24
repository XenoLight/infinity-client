package org.rsbot.gui.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.rsbot.util.GlobalConfiguration;

/**
 * This is the main side panel construction
 * @author Sorcermus
 */
public class OptionPanel extends JPanel {

    private static final long serialVersionUID = 2951376566864605030L;
    public Font fut = new Font("Futura Md BT", 0, 10);
    public static final ImageIcon devel = new ImageIcon(
            GlobalConfiguration.getImage(
            GlobalConfiguration.Paths.getIconDirectory() + "/log.png"));

    /**
     * This is the default constructor
     */
    public OptionPanel() {
        JTabbedPane WindowOptions = new JTabbedPane();
        ItemIDTab itemID = new ItemIDTab();
        WindowOptions.addTab("Item ID", devel, itemID);
        WindowOptions.setFont(fut);
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(250, 523));
        panel.setMaximumSize(new Dimension(250, 523));
        panel.add(WindowOptions);
        add(panel, BorderLayout.NORTH);
    }
}
