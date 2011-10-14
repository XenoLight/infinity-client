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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;

/**
 * Game and settings.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class Game {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	public enum CHAT_MODE {
		VIEW, ON, FRIENDS, OFF, HIDE
	}

	private final Methods methods;
	public boolean openTabs = true;
	public static final int[] indexLogin = { 10, 11 };
	public static final int indexLoginScreen = 3;
	public static final int indexLobby = 7;
	public static final int indexScreenFixed = 746;
	public static final String[] tabNames = new String[] { "Combat Styles",
		"Stats", "Quest List", "Achievement Diaries", "Inventory",
		"Worn Equipment", "Prayer List", "Magic Spellbook", "Objectives",
		"Friends List", "Ignore List", "Clan Chat", "Options", "Emotes",
		"Music Player", "Notes", "Log Out", "Friends Chat" };
	public static final int tabAttack = 0;
	public static final int tabStats = 1;
	public static final int tabQuests = 2;
	public static final int tabAchieve = 3;
	public static final int tabInventory = 4;
	public static final int tabEquipment = 5;
	public static final int tabPrayer = 6;
	public static final int tabMagic = 7;
	public static final int tabSumoming = 8;
	public static final int tabFriends = 9;
	public static final int tabIgnore = 10;
	public static final int tabClan = 11;
	public static final int tabOptions = 12;
	public static final int tabControls = 13;
	public static final int tabMusic = 14;
	public static final int tabNotes = 15;
	public static final int tabLogout = 16;
	public static final int tabFriendsChat = 17;
	/* chat */
	public static final int CHAT_OPTION = 751;
	public static final int CHAT_OPTION_ALL = 2;
	public static final int CHAT_OPTION_GAME = 3;
	public static final int CHAT_OPTION_PUBLIC = 4;
	public static final int CHAT_OPTION_PRIVATE = 5;
	public static final int CHAT_OPTION_FRIENDS = 7;
	public static final int CHAT_OPTION_CLAN = 6;
	public static final int CHAT_OPTION_TRADE = 8;
	public static final int CHAT_OPTION_ASSIST = 9;
	/* chat button */
	public static final int CHAT_BUTTON_CENTER_Y = 491;
	public static final int CHAT_BUTTON_CENTER_X = 33;
	public static final int CHAT_BUTTON_DIFF_X = 58;
	public static final int CHAT_BUTTON_MAX_DY = 8;
	public static final int CHAT_BUTTON_MAX_DX = 23;
	/* ifaces */
	public static final int[] INTERFACE_TALKS = new int[] { 211, 241, 251, 101,
		242, 102, 161, 249, 243, 64, 65, 244, 255, 249, 230, 372, 421 };
	public static final int INTERFACE_CHAT_BOX = 137;
	public static final int INTERFACE_GAME_SCREEN = 548;
	public static final int INTERFACE_LEVEL_UP = 740;
	public static final int INTERFACE_LOGOUT = 182;
	public static final int INTERFACE_LOGOUT_LOBBY = 1;
	public static final int INTERFACE_LOGOUT_COMPLETE = 6;
	public static final int INTERFACE_LOGOUT_BUTTON_FIXED = 181;
	public static final int INTERFACE_LOGOUT_BUTTON_RESIZED = 172;
	public static final int INTERFACE_WELCOME_SCREEN = 907;
	public static final int INTERFACE_WELCOME_SCREEN_CHILD = 150;
	public static final int INTERFACE_WELCOME_SCREEN_PLAY = 18;
	public static final int INTERFACE_HP_ORB = 748;
	public static final int INTERFACE_PRAYER_ORB = 749;
	public static final int INTERFACE_RUN_ORB = 750;
	/* indexes */
	public static final int[] INDEX_LOGGED_IN = { 10, 11 };
	public static final int INDEX_LOGIN_SCREEN = 3;
	public static final int INDEX_LOBBY_SCREEN = 7;
	public static final int INDEX_FIXED = 746;

	public Game() {
		this.methods = Bot.methods;
	}

	/**
	 * Click chat button. Saves space, actually works.
	 * 
	 * @param button
	 *            Which button? Left-to right, 0 to 6. 7 Would land you on
	 *            Report Abuse.
	 * @param left
	 *            Left or right button? Left = true. Right = false.
	 */
	public void clickChatButton(final int button, final boolean left) {
		int x = CHAT_BUTTON_CENTER_X + CHAT_BUTTON_DIFF_X * button;
		x = methods.random(x - CHAT_BUTTON_MAX_DX, x + CHAT_BUTTON_MAX_DX);
		int y = CHAT_BUTTON_CENTER_Y;
		y = methods.random(y - CHAT_BUTTON_MAX_DY, y + CHAT_BUTTON_MAX_DY);
		methods.mouse.move(x, y);
		methods.wait(methods.random(200, 300));
		methods.mouse.click(left);
	}

	public org.rsbot.client.Client client() {
		return Bot.getClient();
	}

	public String getAccountName() {
		return Bot.getAccountName();
	}

	/**
	 * Gets the pin for the account
	 * 
	 * @return Pin or -1 if no pin
	 */
	public String getAccountPin() {
		return AccountManager.getPin(getAccountName());
	}

	/**
	 * Gets the x coordinate of the loaded map area (far west).
	 * 
	 * @return The region base x.
	 */
	public int getBaseX() {
		return client().getBaseX();
	}

	/**
	 * Gets the y coordinate of the loaded map area (far south).
	 * 
	 * @return The region base y.
	 */
	public int getBaseY() {
		return client().getBaseY();
	}

	/**
	 * Gets the game state.
	 * 
	 * @return The game state.
	 */
	public int getClientState() {
		return client().getLoginIndex();
	}

	/**
	 * Gets a color corresponding to x and y coordinates from the current game
	 * screen.
	 * 
	 * @param x
	 *            The x coordinate at which to get the color.
	 * @param y
	 *            The y coordinate at which to get the color.
	 * @return Color
	 * @see java.awt.color
	 */
	public Color getColorAtPoint(final int x, final int y) {
		final BufferedImage image = methods.enviro.takeScreenshot(false);
		return new Color(image.getRGB(x, y));
	}

	public int getCurrentTab() {
		int retVal = -1;
		
		for (int i = 0; i < tabNames.length; i++) {
			/* Logout button, since we can't check it. */
			if (i == tabLogout) {
				retVal = tabLogout;
				break;
			}

			/* Get tab */
			final org.rsbot.client.RSInterface tab = methods.screen.getTab(i);
			if (tab == null) {
				continue;
			}

			/* Check if tab is selected */
			if (tab.getTextureID() != -1) {
				retVal = i;
				break;
			}
		}

		return retVal;
	}

	/**
	 * Gets the canvas height.
	 * 
	 * @return The canvas height.
	 */
	public int getHeight() {
		return Bot.getCanvas().getHeight();
	}

	/**
	 * @author Mouchicc.
	 */
	public String getIDToName(final int... ids) {
		try {
			for (final int r : ids) {
				final URL url = new URL(
						"http://itemdb-rs.runescape.com/viewitem.ws?obj=" + r);
				final BufferedReader reader = new BufferedReader(
						new InputStreamReader(url.openStream()));

				String line;
				int i = 0;
				while ((line = reader.readLine()) != null) {
					if (line.equals("<div class=" + '"' + "subsectionHeader"
							+ '"' + ">")) {
						i++;
						continue;
					}
					if (i == 1) {
						reader.close();
						return line;
					}
				}
			}
		} catch (final Exception e) {
			// log("Error converting ID to Name! Error: " + e);
		}
		return null;
	}

	/**
	 * Access the last message spoken by a player.
	 * 
	 * @return The last message spoken by a player or "" if none
	 */
	public String getLastMessage() {
		final RSInterface face = methods.iface.get(INTERFACE_CHAT_BOX);
		/* Valid text is from 58 to 157 */
		for (int i = 157; i >= 58; i--) {
			final String text = face.getChild(i).getText();
			if (!text.isEmpty() && text.contains("<")) {
				return text;
			}
		}
		return "";
	}

	public int getLoginIndex() {
		return client().getLoginIndex();
	}

	/*
	 * Credits to Mouchicc.
	 */
	public int getNameToID(final String name) {
		int ID = 0;
		try {
			final URL url = new URL(
					"http://services.runescape.com/m=itemdb_rs/results.ws?query="
					+ name + "&price=all&members=");
			final BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains('"' + name + '"')
						&& line.contains("sprite.gif?")) {
					String str = line;
					str = str.substring(str.indexOf("id=") + 3,
							str.indexOf("\" alt=\""));
					ID = Integer.parseInt(str);
					reader.close();
					return ID;
				}
			}
		} catch (final Exception e) {
			// log("Error converting Name to ID! Error: " + e);
		}
		return ID;
	}

	/**
	 * Gets the plane we are currently on. Typically 0 (ground level), but will
	 * increase when going up ladders. You cannot be on a negative plane. Most
	 * dungeons/basements are on plane 0 elsewhere on the world map.
	 * 
	 * @return The current plane.
	 */
	public int getPlane() {
		return client().getPlane();
	}

	public String getSelectedItemName() {
		return client().getSelectedItemName();
	}

	public RSInterfaceChild getTalkInterface() {
		for (final int talk : INTERFACE_TALKS) {
			final RSInterfaceChild child = methods.iface.getChild(talk, 0);
			if (child.isValid()) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Gets the canvas height.
	 * 
	 * @return The canvas width.
	 */
	public int getWidth() {
		return Bot.getCanvas().getWidth();
	}

	/**
	 * Determines whether or not the client is currently in the fixed display
	 * mode.
	 * 
	 * @return <tt>true</tt> if in fixed mode; otherwise <tt>false</tt>.
	 */
	public boolean isFixed() {
		return client().getGUIRSInterfaceIndex() != indexScreenFixed;
	}

	/**
	 * Determines whether or not the client is currently logged in to an
	 * account.
	 * 
	 * @return <tt>true</tt> if logged in; otherwise <tt>false</tt>.
	 */
	public boolean isLoggedIn() {
		final int[] logIndex = { 10, 11 };
		final org.rsbot.client.Client client = client();
		final int index = client == null ? -1 : client.getLoginIndex();
		for (final int idx : logIndex) {
			if (index == idx) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return <tt>true</tt> if the client is showing the login screen;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean isLoginScreen() {
		return client().getLoginIndex() == 3;
	}

	/**
	 * 
	 * @return true <tt>true<tt/> if logout tab/window is open.
	 * @auther Iscream
	 */
	public boolean isOnLogoutTab() {
		for (int i = 0; i < tabNames.length; i++) {
			final org.rsbot.client.RSInterface tab = methods.screen.getTab(i);
			if (tab == null) {
				continue;
			}
			if (tab.getTextureID() != -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns whether or not run is enabled.
	 * 
	 * @return <tt>true</tt> if run mode is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isRunOn() {
		return methods.settings.get(173) == 1;
	}

	/**
	 * @return <tt>true</tt> if the client is showing the welcome screen;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean isWelcomeScreen() {
		return methods.iface.get(INTERFACE_WELCOME_SCREEN).getChild(150)
		.getAbsoluteY() > 2;
	}

	public boolean login() {
		return new org.rsbot.script.antiban.LoginBot().runAntiban();
	}

	/**
	 * Closes the bank if it is open and logs out.
	 * 
	 * @return <tt>true</tt> if the player was logged out.
	 */
	public boolean logout() {
		while (methods.bank.isOpen()) {
			methods.bank.close();
			methods.wait(methods.random(200, 400));
		}
		while (client().isSpellSelected() || methods.inventory.isItemSelected()) {
			while (methods.iface.get(620).isValid()) {
				methods.iface.clickChild(620, 7);
				methods.wait(methods.random(1000, 1300));
			}
			final int currentTab = getCurrentTab();
			int randomTab = methods.random(1, 6);
			while (randomTab == currentTab) {
				randomTab = methods.random(1, 6);
			}
			do {
				openTab(randomTab);
				methods.wait(methods.random(400, 800));
			} while (client().isSpellSelected()
					|| methods.inventory.isItemSelected() == true);
		}
		while (!isOnLogoutTab()) {
			methods.iface.clickChild(548, 181);
			/* Logout button in the top right hand corner */
			int timesToWait = 0;
			while (!isOnLogoutTab() && timesToWait < 5) {
				methods.wait(methods.random(200, 400));
				timesToWait++;
			}
		}
		methods.iface.clickChild(182, 7);
		/* Final logout button in the logout tab */

		switch (methods.random(0, 1)) {
		case 0:
			methods.iface.clickChild(182, 1);
			methods.wait(methods.random(600, 900));
			break;
		case 1:
			methods.iface.clickChild(182, 6);
			methods.wait(methods.random(600, 900));
			break;
		}
		return !isLoggedIn();
	}

	/**
	 * Opens the specified tab.  Waits randomly up to 1-2 seconds for the tab to
	 * open.  There is a small wait prior to returning if the tab was opened
	 * successfully.
	 * 
	 * @param tab 
	 * 		Tab to be opened.
	 * @return
	 * 		<tt>true</tt> if the tab is open, <tt>false</tt> otherwise
	 */
	public boolean openTab(final int tab) {
		if (tab == getCurrentTab()) {
			return true;
		}
		
		final org.rsbot.client.RSInterface iTab = methods.screen.getTab(tab);
		if (iTab == null) {
			return false;
		}
		
		/* Check if tab is selected */
		else if (iTab.getTextureID() != -1) {
			return true;
		}
		
		if (methods.iface.clickChild(methods.iface.getChild(iTab.getID())))  {
			return waitForTab(tab, 2000);
		}
		else  {
			return false;
		}
	}
	
	/**
	 * Waits approximately the specified number of milliseconds for the
	 * tab to be open.
	 * 
	 * @param tab 
	 * 		The index of the tab to wait for
	 * @param ms 
	 * 		The number of milliseconds to wait
	 * @return
	 * 		<tt>true</tt> if the tab is open, <tt>false</tt> otherwise
	 */
	public boolean waitForTab(int tab, int ms)  {
		
		final org.rsbot.client.RSInterface iTab = methods.screen.getTab(tab);
		if (iTab == null) {
			return false;
		}
		
		long end = System.currentTimeMillis()+ms;

		while (end>System.currentTimeMillis() && iTab.getTextureID() == -1)  {
			methods.wait(methods.random(80,120));
		}
		
		// This is just a little additional wait if the tab is now open,
		// the human response delay, if you will...
		if (iTab.getTextureID() != -1)  {
			methods.wait(methods.random(100, 220));
		}
		
		return iTab.getTextureID()!=-1;
	}

	public boolean setAssistMode(final CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE)) {
			throw new IllegalArgumentException("Bad mode: HIDE");
		}
		clickChatButton(6, false);
		return methods.menu.action(mode.toString());
	}

	public boolean setClanMode(final CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE)) {
			throw new IllegalArgumentException("Bad mode: HIDE");
		}
		clickChatButton(4, false);
		return methods.menu.action(mode.toString());
	}

	/**
	 * Whether or not tabs such as inventory and equipment should be opened
	 * automatically by the bot when this script attempts to get data from them.
	 * If you disable this feature, be aware that the data in these tabs will
	 * not be refreshed until they are next opened. The default value is
	 * <tt>true</tt>.
	 * 
	 * @param force
	 *            <tt>true</tt> if tabs should be opened whenever an attempt is
	 *            made to get data from them.
	 */
	public void setEnforceTabFocus(final boolean force) {
		openTabs = force;
	}

	public boolean setPrivateChat(final CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE)) {
			throw new IllegalArgumentException("Bad mode: HIDE");
		}
		clickChatButton(3, false);
		return methods.menu.action(mode.toString());
	}

	public boolean setPublicChat(final CHAT_MODE mode) {
		clickChatButton(2, false);
		return methods.menu.action(mode.toString());
	}

	/**
	 * Turns run on or off using the new l33t mini map controls :3
	 * 
	 * @param enable
	 *            Turns run on if true, off if false.
	 */
	public void setRun(final boolean enable) {
		if (methods.player.isRunning() == enable) {
			return;
		}
		methods.iface.clickChild(INTERFACE_RUN_ORB, 0);
	}

	public boolean setTradeMode(final CHAT_MODE mode) {
		if (mode.equals(CHAT_MODE.HIDE)) {
			throw new IllegalArgumentException("Bad mode: HIDE");
		}
		clickChatButton(5, false);
		return methods.menu.action(mode.toString());
	}

	public void showAllChatMessages() {
		clickChatButton(0, true);
	}

	public void showGameChatMessages() {
		clickChatButton(1, true);
	}

	/**
	 * Switches to a given world.
	 * 
	 * @param world
	 *            the world to switch to, must be valid.
	 * @return If worlds were switched.
	 */
	public boolean switchWorld(final int world) {
		methods.enviro.disableRandom("Login");
		if (isLoggedIn()) {
			logout();
			for (int i = 0; i < 50; i++) {
				methods.sleep(100);
				if (methods.iface.get(906).isValid()
						&& getClientState() == INDEX_LOBBY_SCREEN) {
					break;
				}
			}
		}

		if (!methods.iface.get(906).isValid()) {
			methods.enviro.enableRandom("Login");
			return false;
		}
		if (!methods.iface.get(910).isValid()) {
			final RSInterfaceChild worldSelect = methods.iface.getChild(906,
					189);
			if (worldSelect.click()) {
				methods.sleep(1000);
			}
		}
		if (methods.lobby.switchWorlds(world)) {
			methods.sleep(methods.random(1000, 2000));
			methods.enviro.enableRandom("Login");
			return true;
		}
		return false;
	}
}
