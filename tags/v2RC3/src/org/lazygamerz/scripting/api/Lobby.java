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

import java.util.ArrayList;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSInterfaceChild;

/**
 * Methods for lobby interface Based off of the open source code at RSBot
 * 
 * @author Debauchery at http://powerbot.org - version 1.0
 * @author Runedev development team. - version 1.1
 */
public class Lobby {

	private final Methods methods;

	private static final int SELECTED_TEXTURE = 4671;

	public final static int TAB_PLAYER_INFO = 0;

	public final static int TAB_WORLD_SELECT = 1;
	public final static int TAB_FRIENDS = 2;
	public final static int TAB_FRIENDS_CHAT = 3;
	public final static int TAB_CLAN_CHAT = 4;
	public final static int TAB_OPTIONS = 5;
	public final static int PLAYER_INFO_INTERFACE = 906;

	public final static int PLAYER_INFO_INTERFACE_PLAY_BUTTON = 106;
	public final static int LOGOUT_COMPONENT = 195;
	public final static int WORLD_SELECT_INTERFACE = 910;

	public final static int WORLD_SELECT_INTERFACE_CURRENT_WORLD = 11;
	public final static int WORLD_SELECT_INTERFACE_WORLD_LIST = 77;
	public final static int WORLD_SELECT_INTERFACE_WORLD_NAME = 69;
	public final static int WORLD_SELECT_INTERFACE_AMOUNT_OF_PLAYERS = 71;
	public final static int WORLD_SELECT_INTERFACE_WORLD_ACTIVITY = 72;
	public final static int WORLD_SELECT_INTERFACE_WORLD_TYPE = 74;
	public final static int WORLD_SELECT_INTERFACE_WORLD_PING = 76;
	public final static int WORLD_SELECT_INTERFACE_SCROLL_AREA = 86;
	public final static int WORLD_SELECT_INTERFACE_SCROLL_BAR = 1;
	public final static int FRIENDS_INTERFACE = 909;

	public final static int FRIENDS_CHAT_INTERFACE = 589;

	public final static int CLAN_CHAT_INTERFACE = 912;

	public final static int OPTIONS_INTERFACE = 978;

	public final static int[] TABS = new int[] { 188, 189, 190, 191, 192, 193 };

	public final static int[] TABS_TEXTURE = new int[] { 0, 12, 11, 254, 10, 9 };
	public Lobby() {
		methods = Bot.methods;
	}

	/**
	 * Finds all available worlds if in lobby.
	 * 
	 * @param includingFull
	 *            If true it will include all full worlds when returned
	 * @return All available worlds as a String array
	 */
	public String[] getAvailableWorlds(final boolean includingFull) {
		final ArrayList<String> tempList = new ArrayList<String>();
		if (!inLobby()) {
			return new String[0];
		}
		if (!methods.iface.get(WORLD_SELECT_INTERFACE).isValid()
				|| getCurrentTab() != TAB_WORLD_SELECT) {
			open(TAB_WORLD_SELECT);
			methods.sleep(500);
		}
		for (int i = 0; i < methods.iface.getChild(WORLD_SELECT_INTERFACE,
				WORLD_SELECT_INTERFACE_WORLD_NAME).getChildren().length; i++) {
			final String amount = methods.iface.getChild(
					WORLD_SELECT_INTERFACE,
					WORLD_SELECT_INTERFACE_AMOUNT_OF_PLAYERS).getChildren()[i]
					                                                          .getText();
			final String number = methods.iface.getChild(
					WORLD_SELECT_INTERFACE, WORLD_SELECT_INTERFACE_WORLD_NAME)
					.getChildren()[i].getText();
			if (!amount.contains("OFFLINE") && !amount.contains("0")) {
				if (!includingFull) {
					if (!amount.contains("FULL")) {
						tempList.add(number);
					}
				} else {
					tempList.add(number);
				}
			}
		}
		{
			final String[] temp = new String[tempList.size()];
			tempList.toArray(temp);
			return temp;
		}
	}

	/**
	 * Gets the currently open tab.
	 * 
	 * @return The currently open tab or the logout tab by default.
	 */
	public int getCurrentTab() {
		if (!inLobby()) {
			return -1;
		}
		for (int i = 0; i < TABS.length; i++) {
			if (methods.iface.getChild(PLAYER_INFO_INTERFACE, TABS_TEXTURE[i])
					.getBackgroundColor() == SELECTED_TEXTURE) {
				return i;
			}
		}
		return 1;
	}

	/**
	 * Finds out which world is selected from the lobby interface.
	 * 
	 * @return The world number that is currently selected
	 */
	public int getSelectedWorld() {
		if (!inLobby()) {
			return -1;
		}
		if (!methods.iface.get(WORLD_SELECT_INTERFACE).isValid()
				|| getCurrentTab() != TAB_WORLD_SELECT) {
			open(TAB_WORLD_SELECT);
		}
		if (methods.iface.getChild(WORLD_SELECT_INTERFACE,
				WORLD_SELECT_INTERFACE_CURRENT_WORLD).isValid()) {
			final String worldText = methods.iface
			.getChild(WORLD_SELECT_INTERFACE,
					WORLD_SELECT_INTERFACE_CURRENT_WORLD)
					.getText()
					.trim()
					.substring(
							methods.iface
							.getChild(WORLD_SELECT_INTERFACE,
									WORLD_SELECT_INTERFACE_CURRENT_WORLD)
									.getText().trim().indexOf("World ") + 6);
			return Integer.parseInt(worldText);
		}
		return -1;
	}

	/**
	 * Gets the component of any world on the lobby interface
	 * 
	 * @param world
	 *            The world to get the component of.
	 * @return The component corresponding to the world.
	 */
	public RSInterfaceChild getWorldComponent(final int world) {
		if (!inLobby()) {
			return null;
		}
		if (!methods.iface.get(WORLD_SELECT_INTERFACE).isValid()) {
			open(TAB_WORLD_SELECT);
		}
		for (int i = 0; i < methods.iface.getChild(WORLD_SELECT_INTERFACE,
				WORLD_SELECT_INTERFACE_WORLD_NAME).getChildren().length; i++) {
			final RSInterfaceChild comp = methods.iface.getChild(
					WORLD_SELECT_INTERFACE, WORLD_SELECT_INTERFACE_WORLD_NAME)
					.getChildren()[i];
			if (comp != null) {
				final String number = comp.getText();
				if (Integer.parseInt(number) == world) {
					return methods.iface.getChild(WORLD_SELECT_INTERFACE,
							WORLD_SELECT_INTERFACE_WORLD_LIST).getChildren()[i];
				}
			}
		}
		return null;
	}

	/**
	 * Checks that current game is in lobby.
	 * 
	 * @return <tt>true</tt> if the tab is opened.
	 */
	public boolean inLobby() {
		return methods.game.getClientState() == Game.INDEX_LOBBY_SCREEN;
	}

	/**
	 * Checks if the chosen world is open.
	 * 
	 * @param world
	 *            The world to check.
	 * @param includeFull
	 *            Return even if it's full?
	 * @return <tt>true</tt> is available, else <tt>false</tt>
	 */
	public boolean isAvailable(final int world, final boolean includeFull) {
		for (final String s : getAvailableWorlds(includeFull)) {
			if (Integer.parseInt(s) == world) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Used for logging out if in lobby
	 * 
	 * @return <tt>true</tt> if correctly logged out else false
	 */
	public boolean logout() {
		if (inLobby()) {
			methods.iface.getChild(PLAYER_INFO_INTERFACE, LOGOUT_COMPONENT)
			.click();
		}
		return !methods.game.isLoggedIn();
	}

	/**
	 * Opens the specified tab at the specified index.
	 * 
	 * @param i
	 *            The tab to open.
	 * @return <tt>true</tt> if tab successfully selected; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean open(final int i) {
		if (inLobby()) {
			if (i == getCurrentTab()) {
				return true;
			} else {
				methods.iface.getChild(PLAYER_INFO_INTERFACE, TABS[i]).click();
				methods.sleep(methods.random(400, 700));
			}
		}
		return i == getCurrentTab();
	}

	/**
	 * Enters a world from the lobby.
	 * 
	 * @param world
	 *            The world to switch to.
	 * @return <tt>true</tt> If correctly entered the world else <tt>false</tt>
	 * @see org.lazygamerz.scripting.api.Game switchWorld(int world)
	 */
	public boolean switchWorlds(final int world) {
		if (!inLobby()) {
			return false;
		}
		if (!methods.iface.get(WORLD_SELECT_INTERFACE).isValid()
				|| getCurrentTab() != TAB_WORLD_SELECT) {
			open(TAB_WORLD_SELECT);
			methods.sleep(methods.random(600, 800));
		}
		if (getSelectedWorld() == world) {
			methods.iface.getChild(PLAYER_INFO_INTERFACE,
					PLAYER_INFO_INTERFACE_PLAY_BUTTON).click();
		}
		if (isAvailable(world, false)) {
			final RSInterfaceChild comp = getWorldComponent(world);
			if (comp != null) {
				methods.iface.scrollTo(comp, methods.iface.getChild(
						WORLD_SELECT_INTERFACE,
						WORLD_SELECT_INTERFACE_SCROLL_AREA));
				comp.click();
				methods.sleep(methods.random(500, 800));
				if (getSelectedWorld() == world) {
					methods.iface.getChild(PLAYER_INFO_INTERFACE,
							PLAYER_INFO_INTERFACE_PLAY_BUTTON).click();
					return true;
				}
			}
		}
		return false;
	}

}
