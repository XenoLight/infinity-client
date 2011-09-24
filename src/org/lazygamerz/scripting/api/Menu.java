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

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.rsbot.bot.Bot;
import org.rsbot.client.MenuGroupNode;
import org.rsbot.client.MenuItemNode;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.internal.Deque;
import org.rsbot.script.internal.Queue;

/**
 * Game menus.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class Menu {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final Pattern HTML_TAG = Pattern
	.compile("(^[^<]+>|<[^>]+>|<[^>]+$)");
	private final Methods methods;
	private final Object menuCacheLock = new Object();
	private String[] menuOptionsCache = new String[0];
	private String[] menuActionsCache = new String[0];
	private boolean menuListenerStarted = false;

	public Menu() {
		this.methods = Bot.methods;
	}

	public boolean action(final int i, final int column) {
		if (!isOpen()) {
			return false;
		}
		try {
			final Point p = getLocation();
			final String[] items = getItems();
			int longest = 0;
			for (final String s : items) {
				if (s.length() > longest) {
					longest = s.length();
				}
			}
			final int xOff = 563 + column * 42 + methods.random(0, 32);
			final int yOff = methods.random(21, 29) + 15 * i;
			methods.mouse.click(xOff, p.y + yOff, 2, 2, true);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Clicks the menu option. Will left-click if the menu item is the first,
	 * otherwise open menu and click the option.
	 * 
	 * @param action
	 *            The action (or action substring) to click.
	 * @return <tt>true</tt> if the menu item was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean action(final String action) {
		return action(action, null);
	}

	public boolean action(final String opt, final int col) {
		final int idx = getIndex(opt);
		if (!isOpen()) {
			if (idx == -1) {
				return false;
			}
			if (idx == 0) {
				methods.mouse.click(true);
				return true;
			}
			methods.mouse.click(false);
			methods.wait(methods.random(100,200));
			return clickIndex(idx);
		} else {
			if (idx == -1) {
				while (isOpen()) {
					methods.mouse.moveRandomly(300);
					methods.wait(methods.random(300, 600));
				}
				return false;
			} else {
				action(idx, col);
				return true;
			}
		}
	}

	/**
	 * Clicks the menu option. Will left-click if the menu item is the first,
	 * otherwise open menu and click the option.
	 * 
	 * @param action
	 *            The action (or action substring) to click.
	 * @param option
	 *            The option (or option substring) of the action to click.
	 * @return <tt>true</tt> if the menu item was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean action(final String action, final String option) {
		final int idx = getIndex(action, option);
		if (!isOpen()) {
			if (idx == -1) {
				return false;
			}

			if (idx == 0) {
				methods.mouse.click(true);
				return true;
			}
			methods.mouse.click(false);
		} else if (idx == -1) {
			while (isOpen()) {
				methods.mouse.moveRandomly(300);
				methods.sleep(methods.random(300, 600));
			}
			
			return false;
		}
		
		return clickIndex(idx);
	}

	/**
	 * Search a menu for multiple Strings and click the first occurrence that is
	 * found.
	 * 
	 * @param i
	 *            The Strings to search the menu for.
	 * @return <tt>true</tt> if the menu item was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean action(final String[] i) {
		for (final String target : getItems()) {
			if (arrayContains(i, target)) {
				return action(target);
			}
		}
		while (isOpen()) {
			methods.mouse.moveSlightly();
			methods.wait(methods.random(500, 1000));
		}
		return false;
	}

	/**
	 * Searches a String array to see if it contains a search String.
	 * 
	 * @param items
	 *            The {@code String} array to check.
	 * @param search
	 *            The {@code String} to search for.
	 * @return <tt>true</tt> if the searchString appears in items; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean arrayContains(final String[] items, final String search) {
		for (final String item : items) {
			if (item.equalsIgnoreCase(search)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Left clicks at the given index.
	 * 
	 * @param i
	 *            The index of the item.
	 * @return <tt>true</tt> if the mouse was clicked; otherwise <tt>false</tt>.
	 */
	public boolean clickIndex(final int i) {
		if (!isOpen()) {
			return false;
		}
		
		final String[] items = getItems();
		if (items.length <= i) {
			return false;
		}
		
		if (isCollapsed()) {
			final Queue<MenuGroupNode> groups = new Queue<MenuGroupNode>(
					methods.game.client().getCollapsedMenuItems());
			int idx = 0, mainIdx = 0;
		
			for (MenuGroupNode g = groups.getHead(); g != null; 
				 g = groups.getNext(), ++mainIdx) {
				final Queue<MenuItemNode> subItems = new Queue<MenuItemNode>(
						g.getItems());
				int subIdx = 0;
				
				for (MenuItemNode item = subItems.getHead(); item != null; 
					 item = subItems.getNext(), ++subIdx) {
					if (idx++ == i) {
						return subIdx == 0 ? clickMain(items, mainIdx)
								: clickSub(items, mainIdx, subIdx);
					}
				}
			}
			
			return false;
		} else {
			return clickMain(items, i);
		}
	}

	private boolean clickMain(final String[] items, final int i) {
		// Inject occassional random misclick due to overshooting with the
		// mouse.  About once every 250 menu clicks.
		int idx = i;
		if (methods.random(1,2500)<10)  {
			idx++;
		}
		
		final Point menu = getLocation();
		final int xOff = methods.random(4, items[idx].length() * 4);
		final int yOff = 21 + 16 * idx + methods.random(3, 12);
		
		methods.mouse.move(menu.x + xOff, menu.y + yOff);
		
		if (isOpen()) {
			methods.wait(methods.random(80,140));
			methods.mouse.click(true);
			return true;
		}
		return false;
	}

	private boolean clickSub(final String[] items, final int mIdx,
			final int sIdx) {
		final Point menuLoc = getLocation();
		int x = methods.random(4, items[mIdx].length() * 4);
		int y = 21 + 16 * mIdx + methods.random(3, 12);
		methods.mouse.move(menuLoc.x + x, menuLoc.y + y);
		methods.sleep(methods.random(125, 150));
		if (isOpen()) {
			final Point subLoc = getSubMenuLocation();
			final Point start = methods.mouse.getLocation();
			final int subOff = subLoc.x - start.x;
			final int moves = methods.random(subOff,
					subOff + methods.random(0, items[sIdx].length() * 2));
			x = methods.random(4, items[sIdx].length() * 4);
			if (subOff > 0) {
				final int speed = methods.mouse.getSpeed() / 3;
				for (int c = 0; c < moves; c++) {
					methods.mouse.hop(start.x + c, start.y);
					methods.sleep(methods.random(speed / 2, speed));
				}
			} else {
				methods.mouse.move(subLoc.x + x, methods.mouse.getLocation().y);
			}
			methods.sleep(methods.random(125, 150));
			if (isOpen()) {
				y = 16 * sIdx + methods.random(3, 12) + 21;
				methods.mouse.move(subLoc.x + x, subLoc.y + y);
				methods.sleep(methods.random(125, 150));
				if (isOpen()) {
					methods.mouse.click(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether or not a given action (or action substring) is present in
	 * the menu.
	 * 
	 * @param action
	 *            The action or action substring.
	 * @return <tt>true</tt> if present, otherwise <tt>false</tt>.
	 */
	public boolean contains(final String action) {
		return getIndex(action) != -1;
	}

	/**
	 * Checks whether or not a given action with given option is present in the
	 * menu.
	 * 
	 * @param action
	 *            The action or action substring.
	 * @param option
	 *            The option or option substring.
	 * @return <tt>true</tt> if present, otherwise <tt>false</tt>.
	 */
	public boolean contains(final String action, final String option) {
		return getIndex(action, option) != -1;
	}

	/**
	 * Lists the menu by possible ways the game designer will handle the menu
	 * taken from RSBot.
	 * 
	 * @param firstPart
	 * @return output
	 * @author RSBot dev team
	 */
	private String[] get(final boolean firstPart) {
		final LinkedList<String> itemsList = new LinkedList<String>();
		String action = null;
		final LinkedList<String> output = new LinkedList<String>();
		
	try  {
		if (isCollapsed()) {
			final Queue<MenuGroupNode> menu = new Queue<MenuGroupNode>(
					methods.game.client().getCollapsedMenuItems());
			for (MenuGroupNode mgn = menu.getHead(); mgn != null; mgn = menu
			.getNext()) {
				final Queue<MenuItemNode> submenu = new Queue<MenuItemNode>(
						mgn.getItems());
				for (MenuItemNode min = submenu.getHead(); min != null; min = submenu
				.getNext()) {
					itemsList
					.add(firstPart ? min.getAction() : min.getOption());
					if (action == null) {
						action = min.getAction();
					}
				}
			}
		} else {
			final Deque<MenuItemNode> menu = new Deque<MenuItemNode>(
					methods.game.client().getMenuItems());
			for (MenuItemNode min = menu.getHead(); min != null; min = menu
			.getNext()) {
				itemsList.add(firstPart ? min.getAction() : min.getOption());
				if (action == null) {
					action = min.getAction();
				}
			}
		}
		final String[] items = itemsList.toArray(new String[itemsList.size()]);

		if (isCollapsed()) {
			for (final String item : items) {
				output.add(item == null ? "" : stripFormatting(item));
			}
		} else {
			for (int i = items.length - 1; i >= 0; i--) {
				final String item = items[i];
				output.add(item == null ? "" : stripFormatting(item));
			}
		}
		action = action == null ? "" : stripFormatting(action);
		if (output.size() > 1 && action != null && action.equals("Cancel")) {
			Collections.reverse(output);
		}
	} catch (Exception ex)  {
		logger.severe("Exception caught in menu processing: "+ex.getMessage());
		Bot.logStackTrace(ex);
	}
	
		return output.toArray(new String[output.size()]);
	}

	/**
	 * Returns an array of the first parts of each item in the current menu
	 * context.
	 * 
	 * @return The first half. "Walk here", "Trade with", "Follow".
	 */
	public String[] getActions() {
		return get(true);
	}

	/**
	 * Returns the index in the menu for a given action. Starts at 0.
	 * 
	 * @param action
	 *            The action that you want the index of.
	 * @return index of the given option in the context menu; otherwise -1.
	 */
	public int getIndex(String action) {
		if (action==null)  {
			return -1;
		}
		
		action = action.toLowerCase();
		final String[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].toLowerCase().contains(action)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index in the menu for a given action with a given option.
	 * Starts at 0.
	 * 
	 * @param action
	 *            The action of the menu entry of which you want the index.
	 * @param option
	 *            The option of the menu entry of which you want the index. If
	 *            option is null, operates like getIndex(String action).
	 * @return index of the given option in the context menu; otherwise -1.
	 */
	public int getIndex(String action, String option) {
		if (option == null) {
			return getIndex(action);
		}
		action = action.toLowerCase();
		option = option.toLowerCase();
		final String[] actions = getActions();
		final String[] options = getOptions();
		
		final List<String> actionsList = new ArrayList<String>();
		final List<String> optionsList = new ArrayList<String>();

		for (String act: actions)  {
			if (act!=null)  {
				actionsList.add(act.trim());
			}
		}
		
		for (String opt: options)  {
			if (opt!=null)  {
				optionsList.add(opt.trim());
			}
		}

		/* Throw exception if lengths unequal? */
		if (actionsList.size() > 1 && actionsList.get(0).equals("Cancel")) {
			Collections.reverse(actionsList);
			Collections.reverse(optionsList);
		}
		
		/* Throw exception if lengths unequal? */
		int idx=-1;
		for (String act: actionsList)  {
			idx++;
			if (act.toLowerCase().contains(action) &&
				optionsList.get(idx).toLowerCase().contains(option))  {
				return idx;
			}
		}

		return -1;
	}

	/**
	 * Returns an array of each item in the current menu context.
	 * This method guarantees that the items returned will always have "Cancel"
	 * as the last entry, even if its index was 0.
	 * 
	 * @return First half + second half. As displayed in the game.
	 */
	public String[] getItems() {
		String[] options;
		String[] actions;

		synchronized (menuCacheLock) {
			options = menuOptionsCache;
			actions = menuActionsCache;
		}

		final List<String> output = new ArrayList<String>();

		final int len = Math.min(options.length, actions.length);
		for (int i = 0; i < len; i++) {
			final String option = options[i];
			final String action = actions[i];
			if (option != null && action != null) {
				final String text = action + " " + option;
				output.add(text.trim());
			}
		}

		if (output.size() > 1 && output.get(0).equals("Cancel")) {
			Collections.reverse(output);
		}

		return output.toArray(new String[output.size()]);
	}

	/**
	 * Returns the menu's location.
	 * 
	 * @return The screen space point if the menu is open; otherwise null.
	 */
	public Point getLocation() {
		if (isOpen()) {
			int x = methods.game.client().getMenuX();
			int y = methods.game.client().getMenuY();
			x += 4;
			y += 4;
			return new Point(x, y);
		}
		return null;
	}

	/**
	 * Returns an array of the second parts of each item in the current menu
	 * context.
	 * 
	 * @return The second half. "<user name>".
	 */
	public String[] getOptions() {
		return get(false);
	}

	/**
	 * Returns the menu's item count.
	 * 
	 * @return The menu size.
	 */
	public int getSize() {
		return getItems().length;
	}

	/**
	 * Returns the submenu's location.
	 * 
	 * @return The screen space point of the submenu if the menu is collapsed;
	 *         otherwise null.
	 */
	public Point getSubMenuLocation() {
		if (isCollapsed()) {
			return new Point(methods.game.client().getSubMenuX() + 4,
					methods.game.client().getSubMenuY() + 4);
		}
		return null;
	}

	/**
	 * Checks whether or not the menu is collapsed.
	 * 
	 * @return <tt>true</tt> if the menu is collapsed; otherwise <tt>false</tt>.
	 */
	public boolean isCollapsed() {
		return methods.game.client().isMenuCollapsed();
	}

	/**
	 * Determines if the current menu's default action contains the specified
	 * string. "Cancel" is always the last item in any menu, so use it's
	 * position to determine the menu orientation.
	 * 
	 * @param act
	 * @return
	 * @author zzSleepzz
	 */
	public boolean isDefaultAction(final String act) {
		final int items = getItems().length;
		String itemDefault = getItems()[0].toLowerCase();
		final boolean item0Default = !itemDefault.contains("cancel");

		if (!item0Default) {
			itemDefault = getItems()[items - 1].toLowerCase();
		}

		return itemDefault.contains(act.toLowerCase());
	}

	/**
	 * Checks whether or not the menu is open.
	 * 
	 * @return <tt>true</tt> if the menu is open; otherwise <tt>false</tt>.
	 */
	public boolean isOpen() {
		return methods.game.client().isMenuOpen();
	}

	/**
	 * For internal use only: sets up the menuListener.
	 */
	public void setupListener() {
		if (menuListenerStarted) {
			return;
		}
		menuListenerStarted = true;
		Bot.getEventManager().addListener(new PaintListener() {

			@Override
			public void onRepaint(final Graphics g) {
				synchronized (menuCacheLock) {
					menuOptionsCache = getOptions();
					menuActionsCache = getActions();
				}
			}
		}, EventMulticaster.PAINT_EVENT);
	}

	/**
	 * Removes HTML tags.
	 * 
	 * @param input
	 *            The string you want to parse.
	 * @return The parsed {@code String}.
	 */
	public String stripFormatting(final String input) {
		return HTML_TAG.matcher(input).replaceAll("");
	}
}
