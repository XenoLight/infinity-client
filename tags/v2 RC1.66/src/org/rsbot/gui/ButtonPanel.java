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
package org.rsbot.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.rsbot.gui.toolactions.Account;
import org.rsbot.gui.toolactions.GoTo;
import org.rsbot.gui.toolactions.GoToFace;
import org.rsbot.gui.toolactions.GoToProject;
import org.rsbot.gui.toolactions.GoToTwitter;
import org.rsbot.gui.toolactions.GoToWiki;
import org.rsbot.gui.toolactions.Shot;
import org.rsbot.gui.toolactions.ShotUn;
import org.rsbot.util.GlobalConfiguration;

/**
 * This is the main side panel construction
 * 
 * @author Sorcermus for Runedev @ runedev.info - version 1.0
 * @author Runedev development team - version 1.1
 */
public class ButtonPanel extends JPanel {

	private static final long serialVersionUID = 2951376566864605030L;

	/**
	 * This is the default constructor
	 */
	public ButtonPanel() {
                

                final JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
                add(Box.createVerticalGlue());

		JButton rewards = new JButton();
                final ImageIcon a = new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/reward.png");
		rewards = getDefaultButton(new Account(), "Show pick rewards option panel", a);
		panel.add(rewards);

                JButton rate = new JButton();
                final ImageIcon b = new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/delay.png");
		rate = getDefaultButton(new GoToProject(), "Show image frame rate setting option panel", b);
		panel.add(rate);

		JButton itemID = new JButton();
                final ImageIcon c = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/log.png");
		itemID = getDefaultButton(new Shot(), "Show item ID lookup option panel", c);
		panel.add(itemID);

		JButton ShotUnButton = new JButton();
		ShotUnButton = getDefaultButton(new ShotUn(), "Show NPC lookup option panel", c);
		panel.add(ShotUnButton);

                final JPanel panel2 = new JPanel(new GridLayout(0, 1));
                panel2.setBorder(javax.swing.BorderFactory.createTitledBorder("URLs"));

		JButton homeButton = new JButton();
                final ImageIcon d = new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/home.png");
		homeButton = getDefaultButton(new GoTo(), "Visit lazygamerz.org", d);
		panel2.add(homeButton);

		JButton wikiButton = new JButton();
                final ImageIcon e = new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/wiki.png");
		wikiButton = getDefaultButton(new GoToWiki(), "Visit runedev Wiki", e);
		panel2.add(wikiButton);

		JButton faceButton = new JButton();
                final ImageIcon f = new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/face.png");
		faceButton = getDefaultButton(new GoToFace(), "Visit LazyGamerz Facebook", f);
		panel2.add(faceButton);

		JButton twitterButton = new JButton();
                final ImageIcon g = new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/twit.png");
		twitterButton = getDefaultButton(new GoToTwitter(), "Visit runedev Twitter", g);
		panel2.add(twitterButton);

		JButton projectButton = new JButton();
                final ImageIcon h = new ImageIcon(GlobalConfiguration.Paths.getIconDirectory() + "/web.png");
		projectButton = getDefaultButton(new GoToProject(), "Visit runedev Project", h);
		panel2.add(projectButton);
                //add(panel);
                add(panel2);
	}

	private JButton getDefaultButton(final Action a, final String tip, final ImageIcon i) {
		final JButton button = new JButton(a);
		button.setToolTipText(tip);
		button.setIcon(i);
		button.setFocusable(false);
		button.setMargin(new Insets(2, 0, 2, 0));
		button.setPreferredSize(new Dimension(28, 28));
		button.setMaximumSize(new Dimension(28, 28));
		button.setBorder(new EmptyBorder(3, 3, 3, 3));

		return button;
	}
}
