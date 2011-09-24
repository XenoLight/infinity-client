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
package org.lazygamerz.scripting.api;

import org.rsbot.bot.Bot;
import org.rsbot.client.RSInterface;
import org.rsbot.script.Methods;

/**
 * For internal use
 * 
 * @author Qauters version 1.0
 * @author Rundev development team version 1.1
 */
public class Screen {

	private final Methods methods;
	private int ind_GUI;
	private int ind_Minimap;
	private int ind_Compass;
	private int[] ind_Tabs;

	public Screen() {
		methods = Bot.methods;
		resetIDs();
	}

	/* If GUI is out of sync, resets GUI. */
	private synchronized void checkGUI() {
		if (ind_GUI != methods.game.client().getGUIRSInterfaceIndex()) {
			resetIDs();
			ind_GUI = methods.game.client().getGUIRSInterfaceIndex();
		}
	}

	/**
	 * @return The compasses <tt>RSInterface</tt>;otherwise null.
	 */
	public synchronized RSInterface getCompass() {
		/* Check for GUI changes */
		checkGUI();

		/* Get GUI interface */
		final RSInterface[] gui = ind_GUI != -1 ? methods.game.client()
				.getRSInterfaceCache()[ind_GUI] : null;
				if (gui == null) {
					return null;
				}

				/* Check if we need to find a new compass index */
				if (ind_Compass == -1) {
					for (int i = 0; i < gui.length; i++) {
						if (gui[i] != null && gui[i].getActions() != null
								&& gui[i].getActions().length == 1
								&& gui[i].getActions()[0].equals("Face North")) {
							ind_Compass = i;
							break;
						}
					}
				}

				/* Return the compass interface */
				if (ind_Compass != -1) {
					return gui[ind_Compass];
				}

				return null;
	}

	/**
	 * @return The minimaps <tt>RSInterface</tt>; otherwise null.
	 */
	public synchronized RSInterface getMinimapInterface() {
		/* Check for GUI changes */
		checkGUI();

		/* Get the GUI interface */
		final RSInterface[] gui = ind_GUI != -1 ? methods.game.client()
				.getRSInterfaceCache()[ind_GUI] : null;
				if (gui == null) {
					return null;
				}

				/* Check if we need to find the new minimap index */
				if (ind_Minimap == -1) {
					for (int i = 0; i < gui.length; i++) {
						if (gui[i] != null && gui[i].getSpecialType() == 1338) {
							ind_Minimap = i;
							break;
						}
					}
				}

				/* Return minimap interface */
				if (ind_Minimap != -1) {
					return gui[ind_Minimap];
				}

				return null;
	}

	/**
	 * @param tab
	 *            The tab.
	 * @return The specified tab <tt>RSInterface</tt>; otherwise null.
	 */
	public synchronized RSInterface getTab(final int id) {
		if ((id < 0) || (id >= ind_Tabs.length)) {
			return null;
		}

		/* Check for GUI changes */
		checkGUI();

		/* Get GUI interface */
		final RSInterface[] gui = ind_GUI != -1 ? methods.game.client()
				.getRSInterfaceCache()[ind_GUI] : null;
				if (gui != null) {
					/* Check if we need to find a new tab index */
					if (ind_Tabs[id] == -1) {
						for (int i = 0; i < gui.length; i++) {
							if (gui[i] != null) {
								final String[] actions = gui[i].getActions();
								if (actions != null && actions.length > 0
										&& actions[0].equals(Game.tabNames[id])) {
									ind_Tabs[id] = i;
									break;
								}
							}
						}
					}

					/* Return the tab interface */
					if (ind_Tabs[id] != -1) {
						return gui[ind_Tabs[id]];
					}
				}
				return null;
	}

	/* Resets the GameGUI class IDs. */
	private synchronized void resetIDs() {
		ind_GUI = -1;
		ind_Minimap = -1;
		ind_Compass = -1;

		ind_Tabs = new int[17];
		for (int i = 0; i < ind_Tabs.length; i++) {
			ind_Tabs[i] = -1;
		}
	}
}
