package org.rsbot.gui.toolactions;

import java.awt.Point;

import javax.swing.JFrame;

import org.rsbot.gui.BotGUI;
import org.rsbot.util.ItemIDFinderGUI;


public class ItemIdGui extends Base {

	private static final long serialVersionUID = 89567527547437753L;
	private BotGUI gui;

	public ItemIdGui(BotGUI gui) {
		this.gui = gui;
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		JFrame item = new ItemIDFinderGUI();
		
		item.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Point loc = gui.getLocation();
		loc.x += gui.getWidth()+1;
		
		item.setLocation(loc);
		item.setVisible(true);
	}
}
