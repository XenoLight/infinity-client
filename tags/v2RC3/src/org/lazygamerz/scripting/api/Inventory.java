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

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSItemDef;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * Game equipment tab.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class Inventory {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private final Methods methods;
	public static final int INTERFACE_INVENTORY = 679;
	public static final int INTERFACE_INVENTORY_PRICE_CHECK = 204;
	public static final int INTERFACE_INVENTORY_SHOP = 621;
	public static final int INTERFACE_INVENTORY_EQUIPMENT_BONUSES = 670;
	public static final int INTERFACE_INVENTORY_BANK = 763;
	public static final int INTERFACE_INVENTORY_DUNGEONEERING_SHOP = 957;
	
	public static final int INTERFACE_ITEM_ACTIONS = 94;
	public static final int INTERFACE_ITEM_ACTIONS_DESTROY = 3;

	public static final int INVENTORY_X = 679;
	public static final int INVENTORY_Y = 0;
	public static final int INVENTORY_COM_X = 763;
	public static final int INVENTORY_COM_Y = 0;

	public Inventory() {
		this.methods = Bot.methods;
	}

	/**
	 * Used to determine of the inventory tab is open.
	 * 
	 * @return <tt>true</tt> if the inventory tab is open, <tt>false</tt> otherwise.
	 */
	public boolean isOpen()  {
		int currTab = methods.game.getCurrentTab();
		
		if (currTab==Game.tabInventory)  {
			return true;
		}
		
		if (methods.iface.get(INTERFACE_INVENTORY_BANK).isValid()) {
			final RSInterfaceChild bankInv = methods.iface.getChild(
					INTERFACE_INVENTORY_BANK, 0);
			if (bankInv != null && bankInv.getAbsoluteX()>50) {
				return bankInv.isValid();
			}
		}
		
		if (methods.iface.get(INTERFACE_INVENTORY_SHOP).isValid()) {
			final RSInterfaceChild shopInv = methods.iface.getChild(
					INTERFACE_INVENTORY_SHOP, 0);
			if (shopInv != null && shopInv.getAbsoluteX() > 50) {
				return shopInv.isValid();
			}
		}
		
		if (methods.iface.get(INTERFACE_INVENTORY_PRICE_CHECK).isValid()) {
			final RSInterfaceChild priceInv = methods.iface.getChild(
					INTERFACE_INVENTORY_PRICE_CHECK, 0);
			if (priceInv != null && priceInv.getAbsoluteX() > 50) {
				return priceInv.isValid();
			}
		}
		
		// This interface will appear as valid even when closed.  We must check if 
		// child 0 has any children.  If so, it is open.
		if (methods.iface.get(INTERFACE_INVENTORY_EQUIPMENT_BONUSES).isValid()) {
			final RSInterfaceChild equipInv = methods.iface.getChild(
					INTERFACE_INVENTORY_EQUIPMENT_BONUSES, 0);
			
			final RSInterfaceChild[] children = equipInv.getChildren();
			if (equipInv != null && children!=null && children.length>0) {
				return equipInv.isValid();
			}
		}
		
		if (methods.iface.get(INTERFACE_INVENTORY_DUNGEONEERING_SHOP).isValid()) {
			final RSInterfaceChild dungInv = methods.iface.getChild(
					INTERFACE_INVENTORY_DUNGEONEERING_SHOP, 0);
			if (dungInv != null && dungInv.getAbsoluteX() > 50) {
				return dungInv.isValid();
			}
		}

		return false;
	}
	
	/**
	 * Determines if the inventory can be opened.  For example, when the bank deposit 
	 * box is open, the inventory tab cannot be opened.
	 * 
	 * @return <tt>true</tt> if the inventory tab can be opened, <tt>false</tt> otherwise
	 */
	public boolean canOpen() {
		
		// Return false if the normal inventory interface for banking, shops,
		// price check, equipment bonuses or dungeoneering is open.
		if (methods.iface.get(INTERFACE_INVENTORY_BANK).isValid()) {
			final RSInterfaceChild bankInv = methods.iface.getChild(
					INTERFACE_INVENTORY_BANK, 0);
			if (bankInv != null && bankInv.getAbsoluteX()>50) {
				return !bankInv.isValid();
			}
		}
		
		if (methods.bank.isDepositOpen())  {
			return false;
		}
		
		if (methods.iface.get(INTERFACE_INVENTORY_SHOP).isValid()) {
			final RSInterfaceChild shopInv = methods.iface.getChild(
					INTERFACE_INVENTORY_SHOP, 0);
			if (shopInv != null && shopInv.getAbsoluteX() > 50) {
				return !shopInv.isValid();
			}
		}
		
		if (methods.iface.get(INTERFACE_INVENTORY_PRICE_CHECK).isValid()) {
			final RSInterfaceChild priceInv = methods.iface.getChild(
					INTERFACE_INVENTORY_PRICE_CHECK, 0);
			if (priceInv != null && priceInv.getAbsoluteX() > 50) {
				return !priceInv.isValid();
			}
		}
		
		// This interface will appear as valid even when closed.  We must check if 
		// child 0 has any children.  If so, it is open.
		if (methods.iface.get(INTERFACE_INVENTORY_EQUIPMENT_BONUSES).isValid()) {
			final RSInterfaceChild equipInv = methods.iface.getChild(
					INTERFACE_INVENTORY_EQUIPMENT_BONUSES, 0);
			
			final RSInterfaceChild[] children = equipInv.getChildren();
			if (equipInv != null && children!=null && children.length>0) {
				return !equipInv.isValid();
			}
		}
		
		if (methods.iface.get(INTERFACE_INVENTORY_DUNGEONEERING_SHOP).isValid()) {
			final RSInterfaceChild dungInv = methods.iface.getChild(
					INTERFACE_INVENTORY_DUNGEONEERING_SHOP, 0);
			if (dungInv != null && dungInv.getAbsoluteX() > 50) {
				return !dungInv.isValid();
			}
		}

		// As a failsafe, if the inventory is currently open, return false in 
		// order to prevent possible unnecessary re-opening by the caller.
		if (isOpen())  {
			return false;
		}
		else  {
			return true;
		}
	}
	
	
	/**
	 * Opens the inventory tab.
	 * 
	 * @return <tt>true</tt> if the inventory tab is open, <tt>false</tt> otherwise.
	 */
	public boolean open()  {
		// If a deposit box is open, the inventory tab is disabled, so don't
		// try to open it.
		if (methods.bank.isDepositOpen())  {
			return false;
		}
		
		if (!isOpen() && canOpen())  {
			methods.game.openTab(Game.tabInventory);
			
			methods.iface.waitForChildOpen(methods.inventory.getInterface(),
										   methods.random(1400,1600));
		}
		
		return isOpen();
	}
	

	/**
	 * Performs the provided action on a random inventory item with the given
	 * ID.
	 * 
	 * @param id
	 *            The ID of the item to look for.
	 * @param opt
	 *            The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 */
	public boolean clickItem(final int id, final String opt) {
		try {
			if (!isOpen())  {
				if (canOpen())  {
					methods.game.openTab(Game.tabInventory);
				
					methods.iface.waitForChildOpen(methods.inventory.getInterface(),
												   methods.random(1400,1600));
				}
				else  {
					return false;
				}
			}

			final RSInterfaceChild face = getInterface();
			if (face == null || face.getChildren() == null) {
				return false;
			}
			final java.util.List<RSInterfaceChild> possible = new ArrayList<RSInterfaceChild>();
			for (final RSInterfaceChild item : face.getChildren()) {
				if (item != null && item.getChildID() == id) {
					possible.add(item);
				}
			}
			if (possible.isEmpty()) {
				return false;
			}
			final int sz = possible.size();
			
			final RSInterfaceChild item = possible.get(methods.random(0,
					Math.min(2, sz==0?0:sz-1)));
			return methods.iface.clickChild(item, opt);
		} catch (final Exception e) {
			logger.warning("atInventoryItem(int itemID, String option) Error: " + e);
			Bot.logStackTrace(e);
			return false;
		}
	}

	public boolean clickItem(final RSItem item, final String action) {
		try {
			if (!isOpen())  {
				if (canOpen())  {
					methods.game.openTab(Game.tabInventory);
				
					methods.iface.waitForChildOpen(methods.inventory.getInterface(),
												   methods.random(1400,1600));
				}
				else  {
					return false;
				}
			}

			final RSInterfaceChild face = getInterface();
			if (face == null || face.getChildren() == null) {
				return false;
			}
			final java.util.List<RSInterfaceChild> possible = new ArrayList<RSInterfaceChild>();
			for (final RSInterfaceChild child : face.getChildren()) {
				if (child != null && child.getChildID() == item.getID()) {
					possible.add(child);
				}
			}
			if (possible.isEmpty()) {
				return false;
			}
			final int sz = possible.size();
			
			final RSInterfaceChild child = possible.get(methods.random(0,
					Math.min(2, sz==0?0:sz-1)));
			return methods.iface.clickChild(child, action);
		} catch (final Exception e) {
			logger.warning("atInventoryItem(RSItem item, String action) Error: " + e);
			Bot.logStackTrace(e);
			return false;
		}
	}

	/**
	 * Left-clicks on the selected item.
	 * 
	 * @return <tt>true</tt> if item was selected, </tt>false</tt> if not.
	 * @see #clickSelectedItem(boolean)
	 */
	public boolean clickSelectedItem() {
		return clickSelectedItem(true);
	}

	/**
	 * Clicks selected inventory item, if it's selected.
	 * 
	 * @param leftClick
	 *            <tt>true</tt> for left button click, <tt>false</tt> for right
	 *            button.
	 * @return <tt>true</tt> if item was selected, <tt>false</tt> if not.
	 */
	public boolean clickSelectedItem(final boolean leftClick) {
		final RSItem item = getSelectedItem();
		return item != null && item.click(true);
	}

	/**
	 * Checks if your inventory is open and contains the provided item ID.
	 * Assumes the inventory is already open and will not try to open it.
	 * 
	 * @param itemID
	 *            The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory is open and contains an item with the ID
	 *         provided; otherwise <tt>false</tt>.
	 * @see #containsOneOf(int...)
	 * @see #containsAll(int...)
	 */
	public boolean contains(final int itemID) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		return getItem(itemID) != null;
	}

	/**
	 * Checks if your inventory is open and contains the specific items.
	 * Assumes the inventory is already open and will not try to open it.
	 * 
	 * @param ids
	 *            The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains at least one of all of
	 *         the item IDs provided; otherwise <tt>false</tt>.
	 */
	public boolean contains(final int... ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		final int[] items = getArray();

		for (final int item : items) {
			for (final int i : ids) {
				if (item == i) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if your inventory is open and contains the provided item name.
	 * Assumes the inventory is already open and will not try to open it.
	 * 
	 * @param name
	 *            The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains an item with the name
	 *         provided; otherwise <tt>false</tt>.
	 */
	public boolean contains(final String name) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		return getItem(name) != null;
	}

	/**
	 * Checks if your inventory is open and contains all of the provided item
	 * IDs.  Assumes the inventory is already open and will not try to open it.

	 * 
	 * @param itemID
	 *            The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains at least one of all of
	 *         the item IDs provided; otherwise <tt>false</tt>.
	 * @see #containsOneOf(int...)
	 */
	public boolean containsAll(final int... itemID) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		for (final int i : itemID) {
			if (getItem(i) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if your inventory is open and contains at least one of the
	 * provided item IDs. 	Assumes the inventory is already open and will 
	 * not try to open it.

	 * 
	 * @param itemID
	 *            The item ID to check for.
	 * @return <tt>true</tt> if inventory contains one of the specified items;
	 *         otherwise <tt>false</tt>.
	 * @see #containsAll(int...)
	 */
	public boolean containsOneOf(final int... itemID) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		for (final RSItem item : getItems()) {
			for (final int i : itemID) {
				if (item.getID() == i) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Destroys any inventory items with the given ID.
	 * 
	 * @param itemID
	 *            The ID of items to destroy.
	 * @return <tt>true</tt> if the items were destroyed; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean destroyItem(final int itemID) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		RSItem item = getItem(itemID);
		if (item == null || !item.hasAction("Destroy")) {
			return false;
		}
		
		while ((item = getItem(itemID)) != null) {
			if (methods.iface.get(INTERFACE_ITEM_ACTIONS).isValid()) {
				methods.iface.
					getChild(INTERFACE_ITEM_ACTIONS, INTERFACE_ITEM_ACTIONS_DESTROY).click();
			} else {
				item.action("Destroy");
			}
			methods.sleep(methods.random(700, 1100));
		}
		return true;
	}

	/**
	 * Drops all items except those with one of the provided IDs.
	 * 
	 * @param leftToRight
	 *            <tt>true</tt> to drop items from left to right.
	 * @param ids
	 *            The item IDs to drop
	 * @return <tt>true</tt> if items were dropped from the inventory; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean dropAllExcept(final boolean leftToRight, final int... ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		final int startCount = getCount();
		final RSTile startLocation = methods.player.getMine().getLocation();
		boolean found_droppable = true;
		while (found_droppable && getCountExcept(ids) != 0) {
			if (methods.calculate.distanceTo(startLocation) > 100) {
				break;
			}
			found_droppable = false;

			for (int j = 0; j < 28; j++) {
				final int c = leftToRight ? j % 4 : j / 7;
				final int r = leftToRight ? j / 4 : j % 7;
				final RSItem curItem = getItems()[c + r * 4];
				if (curItem != null) {
					final int id = curItem.getID();
					if (id != -1) {
						boolean isInItems = false;
						for (final int i : ids) {
							if (i == id) {
								isInItems = true;
								break;
							}
						}
						if (!isInItems) {
							for (int d = 0; d < 3; d++) {
								if (dropItem(c, r)) {
									found_droppable = true;
									break;
								}
								methods.sleep(methods.random(100, 400));
							}
						}
					}
				}
			}
			methods.sleep(methods.random(400, 800));
		}
		return getCount() < startCount;
	}

	/**
	 * Drops all items except those with one of the provided list of IDs.
	 * 
	 * @param leftToRight
	 *            <tt>true</tt> to drop items from left to right.
	 * @param ids
	 *            A List<Integer) of the item IDs to drop
	 * @return <tt>true</tt> if items were dropped from the inventory; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean dropAllExcept(final boolean leftToRight, final List<Integer> ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		final int startCount = getCount();
		final RSTile startLocation = methods.player.getMine().getLocation();
		boolean found_droppable = true;
		while (found_droppable && getCountExcept(ids) != 0) {
			if (methods.calculate.distanceTo(startLocation) > 100) {
				break;
			}
			found_droppable = false;

			for (int j = 0; j < 28; j++) {
				final int c = leftToRight ? j % 4 : j / 7;
				final int r = leftToRight ? j / 4 : j % 7;
				final RSItem curItem = getItems()[c + r * 4];
				if (curItem != null) {
					final int id = curItem.getID();
					if (id != -1) {
						boolean isInItems = false;
						for (final int i : ids) {
							if (i == id) {
								isInItems = true;
								break;
							}
						}
						if (!isInItems) {
							for (int d = 0; d < 3; d++) {
								if (dropItem(c, r)) {
									found_droppable = true;
									break;
								}
								methods.sleep(methods.random(100, 400));
							}
						}
					}
				}
			}
			methods.sleep(methods.random(400, 800));
		}
		return getCount() < startCount;
	}
	/**
	 * Drops all items except those with one of the provided IDs. This method
	 * drops items vertically going down the inventory.
	 * 
	 * @param ids
	 *            The item IDs to drop.
	 * @return <tt>true</tt> if items were dropped from the inventory; otherwise
	 *         <tt>false</tt>.
	 * @see #dropAllExcept(boolean, int...)
	 */
	public boolean dropAllExcept(final int... ids) {
		return dropAllExcept(false, ids);
	}

	/**
	 * Drops all items except those with one of the provided IDs. This method
	 * drops items vertically going down the inventory.
	 * 
	 * This method is useful when you have an array of loot IDs you want to
	 * use for picking up, but you want that list plus some others to keep from
	 * being dropped. 
	 * 
	 * For example, if you don't want to pick up gold, but you don't want to
	 * drop any either, because you've been alching loot, use this method.
	 * 
	 *    int[] loot = {1,2,3,4,5};
	 *    int gold = 995;
	 * 
	 *    inventory.dropAllExcept(loot, gold);
	 * 
	 * @param ids1
	 *            The item IDs to drop in the form of an array
	 * @param ids2
	 *            The other IDs to drop in the form of a comma delimited list.
	 * @return <tt>true</tt> if items were dropped from the inventory; otherwise
	 *         <tt>false</tt>.
	 * @see #dropAllExcept(boolean, int...)
	 */
	public boolean dropAllExcept(final int[] ids1, final int... ids2) {
		List<Integer> ids = new ArrayList<Integer>();
		
		for (int i: ids1)  {
			ids.add(i);
		}
		for (int i: ids2)  {
			ids.add(i);
		}
		
		return dropAllExcept(false, ids);		
	}

	
	/**
	 * Drops the item in the specified column and row.
	 * 
	 * @param col
	 *            The column the item is in.
	 * @param row
	 *            The row the item is in.
	 * @return <tt>true</tt> if we tried to drop the item, <tt>false</tt> if not
	 *         (e.g., if item is undroppable)
	 */
	public boolean dropItem(final int col, final int row) {
		if (methods.iface.canContinue()) {
			methods.iface.clickContinue();
			methods.sleep(methods.random(800, 1300));
		}
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		if (col < 0 || col > 3 || row < 0 || row > 6) {
			return false;
		}
		
		final RSItem item = getItems()[col + row * 4];
		return item != null && item.getID() != -1 && item.action("Drop");
	}

	/**
	 * Gets the inventory array.
	 * 
	 * @return an array containing all items
	 */
	public int[] getArray() {
		final RSInterfaceChild face = getInterface();
		if (face != null) {
			if (face.getChildren().length > 0) {
				int len = 0;
				for (final RSInterfaceChild com : face.getChildren()) {
					if (com.getType() == 5) {
						len++;
					}
				}

				final int[] inv = new int[len];
				for (int i = 0; i < len; i++) {
					try {
						final RSInterfaceChild item = face.getChildren()[i];
						inv[item.getChildIndex()] = item.getChildID();
					} catch (final Exception e) {
						methods.wait(methods.random(500, 700));
						return getArray();
					}
				}

				return inv;
			}
		}
		/* give scripters as few nulls as possible! */
		return new int[0];
	}

	/**
	 * Gets the count of all items in your inventory, ignoring stack sizes.
	 * 
	 * @return The count.
	 */
	public int getCount() {
		return getCount(false);
	}

	/**
	 * Gets the count of all items in your inventory.
	 * 
	 * @param includeStacks
	 *            <tt>false</tt> if stacked items should be counted as a single
	 *            item; otherwise <tt>true</tt>.
	 * @return The count.  -1 if the inventory was not open and could not be opened.
	 */
	public int getCount(final boolean includeStacks) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		int count = 0;
		for (final RSItem item : getItems()) {
			final int iid = item.getID();
			if (iid != -1) {
				if (includeStacks) {
					count += item.getStackSize();
				} else {
					++count;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the count of all the items in the inventory with the any of the
	 * specified IDs.
	 * 
	 * @param includeStacks
	 *            <tt>true</tt> to count the stack sizes of each item;
	 *            <tt>false</tt> to count a stack as a single item.
	 * @param itemIDs
	 *            the item IDs to include
	 * @return The count.  -1 if the inventory was not open and could not be opened.
	 */
	public int getCount(final boolean includeStacks, final int... itemIDs) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		int total = 0;

		for (final RSItem item : getItems()) {
			if (item == null) {
				continue;
			}

			for (final int ID : itemIDs) {
				if (item.getID() == ID) {
					total += includeStacks ? item.getStackSize() : 1;
				}
			}
		}

		return total;
	}

	/**
	 * Gets the count of the items in the inventory with the any of the
	 * specified IDs. This ignores stack sizes if more than one item is specified.
	 * If only one item is specified, the stack size will be returned.
	 * 
	 * @param itemIDs
	 *            the item IDs to include
	 * @return The count.  -1 if the inventory was not open and could not be opened.
	 */
	public int getCount(final int... itemIDs) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		if (itemIDs.length>1)  {
			return getCount(false, itemIDs);
		}
		else  {
			return getCount(true, itemIDs);
		}
		
	}

	/**
	 * Gets the count of all the items in the inventory without any of the
	 * provided IDs.
	 * 
	 * @param includeStacks
	 *            <tt>true</tt> to count the stack sizes of each item;
	 *            <tt>false</tt> to count a stack as a single item.
	 * @param ids
	 *            The item IDs to exclude.
	 * @return The count.  -1 if the inventory was not open and could not be opened.
	 */
	public int getCountExcept(final boolean includeStacks, final int... ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		int count = 0;
		for (final RSItem i : getItems()) {
			if (i.getID() != -1) {
				boolean skip = false;
				for (final int id : ids) {
					if (i.getID() == id) {
						skip = true;
						break;
					}
				}
				if (!skip) {
					count += includeStacks ? i.getStackSize() : 1;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the count of all the items in the inventory without any of the
	 * provided IDs.
	 * 
	 * @param includeStacks
	 *            <tt>true</tt> to count the stack sizes of each item;
	 *            <tt>false</tt> to count a stack as a single item.
	 * @param ids
	 *            The item IDs to exclude.
	 * @return The count.  -1 if the inventory was not open and could not be opened.
	 */
	public int getCountExcept(final boolean includeStacks, final List<Integer> ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		int count = 0;
		for (final RSItem i : getItems()) {
			if (i.getID() != -1) {
				boolean skip = false;
				for (final int id : ids) {
					if (i.getID() == id) {
						skip = true;
						break;
					}
				}
				if (!skip) {
					count += includeStacks ? i.getStackSize() : 1;
				}
			}
		}
		return count;
	}
	
	/**
	 * Gets the count of all the items in the inventory without any of the
	 * provided IDs, ignoring stack sizes.
	 * 
	 * @param ids
	 *            The item IDs to exclude.
	 * @return The count.
	 */
	public int getCountExcept(final List<Integer> ids) {
		return getCountExcept(false, ids);
	}

	
	/**
	 * Gets the count of all the items in the inventory without any of the
	 * provided IDs, ignoring stack sizes.
	 * 
	 * @param ids
	 *            The item IDs to exclude.
	 * @return The count.
	 */
	public int getCountExcept(final int... ids) {
		return getCountExcept(false, ids);
	}

	/**
	 * Gets the inventory interface.
	 * 
	 * @return the current inventory interface if available; otherwise null.
	 */
	public RSInterfaceChild getInterface() {
		return getInterface(false);
	}

	/**
	 * Gets the inventory interface.  When the inventory tab is not actually open,
	 * this method still always return a valid interface.
	 * 
	 * @param cached
	 *            <tt>true</tt> to skip updating the inventory interface.
	 * @return the current inventory interface if available; otherwise null.
	 */
	public RSInterfaceChild getInterface(final boolean cached) {
		if (methods.iface.get(INTERFACE_INVENTORY_BANK).isValid()) {
			final RSInterfaceChild bankInv = methods.iface.getChild(
					INTERFACE_INVENTORY_BANK, 0);
			if (bankInv != null && bankInv.getAbsoluteX() > 50) {
				return bankInv;
			}
		}
		if (methods.iface.get(INTERFACE_INVENTORY_SHOP).isValid()) {
			final RSInterfaceChild shopInv = methods.iface.getChild(
					INTERFACE_INVENTORY_SHOP, 0);
			if (shopInv != null && shopInv.getAbsoluteX() > 50) {
				return shopInv;
			}
		}
		if (methods.iface.get(INTERFACE_INVENTORY_PRICE_CHECK).isValid()) {
			final RSInterfaceChild priceInv = methods.iface.getChild(
					INTERFACE_INVENTORY_PRICE_CHECK, 0);
			if (priceInv != null && priceInv.getAbsoluteX() > 50) {
				return priceInv;
			}
		}
		
		// This interface will appear as valid even when closed.  We must check if 
		// child 0 has any children.  If so, it is open.
		if (methods.iface.get(INTERFACE_INVENTORY_EQUIPMENT_BONUSES).isValid()) {
			final RSInterfaceChild equipInv = methods.iface.getChild(
					INTERFACE_INVENTORY_EQUIPMENT_BONUSES, 0);
			
			final RSInterfaceChild[] children = equipInv.getChildren();
			if (equipInv != null && children!=null && children.length>0) {
				return equipInv;
			}
		}
		if (methods.iface.get(INTERFACE_INVENTORY_DUNGEONEERING_SHOP).isValid()) {
			final RSInterfaceChild dungInv = methods.iface.getChild(
					INTERFACE_INVENTORY_DUNGEONEERING_SHOP, 0);
			if (dungInv != null && dungInv.getAbsoluteX() > 50) {
				return dungInv;
			}
		}

		if (!cached && !methods.iface.get(INTERFACE_INVENTORY).isValid()) {
			methods.game.openTab(Game.tabInventory);
			methods.sleep(methods.random(200, 400));
		}

		return methods.iface.getChild(INTERFACE_INVENTORY,0);
	}

	/**
	 * Gets the first item in the inventory with any of the provided IDs.
	 * 
	 * @param ids
	 *            The IDs of the item to find.
	 * @return The first <tt>RSItem</tt> for the given IDs; otherwise null.
	 */
	public RSItem getItem(final int... ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return null;
			}
		}
		
		for (final RSItem item : getItems()) {
			for (final int id : ids) {
				if (item.getID() == id) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the first item in the inventory containing any of the provided
	 * names.
	 * 
	 * @param names
	 *            The names of the item to find.
	 * @return The first <tt>RSItem</tt> for the given name(s); otherwise null.
	 */
	public RSItem getItem(final String... names) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return null;
			}
		}
		
		for (final RSItem item : getItems()) {
			String name = item.getName();
			if (name != null) {
				name = name.toLowerCase();
				for (final String n : names) {
					if (n != null && name.contains(n.toLowerCase())) {
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets inventory item at specified index.
	 * 
	 * @param index
	 *            The index of inventory item.
	 * @return The item, or <tt>null</tt> if not found.
	 */
	public RSItem getItemAt(final int index) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return null;
			}
		}
		
		final RSInterfaceChild comp = getInterface().getChild(index);
		return 0 <= index && index < 28 && comp != null ? new RSItem(comp)
		: null;
	}

	/**
	 * Returns the RSItem in the inventory for the requested ID.
	 * 
	 * @param ids
	 *            The ID of the inventory item to return.
	 * @return <tt>RSItem</tt> if your inventory contains at least one of the
	 *         requested item ID; otherwise <tt>null<tt>.
	 */
	public RSItem getItemByID(final int... ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return null;
			}
		}
		
		final RSItem[] items = getItems();

		for (final RSItem item : items) {
			for (final int id : ids) {
				if (item.getID() == id) {
					return item;
				}
			}
		}

		return null;
	}

	/**
	 * Gets the ID of an item in the inventory with a given name.
	 * 
	 * @param name
	 *            The name of the item you wish to find.
	 * @return The ID of the item or -1 if not in inventory.
	 */
	public int getItemByName(final String name) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		final RSItem item = getItem(name);
		return item != null ? item.getID() : -1;
	}

	/**
	 * Gets the ID of an item in the inventory with a given name.
	 * 
	 * @param name
	 *            The name of the item you wish to find.
	 * @return The ID of the item or -1 if not in inventory.
	 */
	public int getItemID(final String name) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		final RSItem[] items = getItems();
		int slot = -1;
		for (final RSItem item : items) {
			final RSItemDef def = item.getDefinition();
			if (def != null && def.getName().contains(name)) {
				slot = item.getID();
				break;
			}
		}
		return slot;
	}

	/**
	 * Gets the top left position of the item
	 * 
	 * @param index
	 *            The index of the item in the inventory array.
	 * @return A Point representing the screen location.  null if the inventory
	 * was not open and could not be opened.
	 */
	public Point getItemPoint(final int index) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return null;
			}
		}
		
		final RSInterfaceChild face = getInterface();
		if (face.getChildren() == null || index < 0
				|| index >= face.getChildren().length) {
			return new Point(-1, -1);
		}

		return face.getChildren()[index].getPosition();
	}

	/**
	 * Gets all the items in the inventory.
	 * 
	 * @return <tt>RSItem</tt> array of the current inventory items or an empty
	 *         <tt>RSItem[]</tt> if unavailable.
	 */
	public RSItem[] getItems() {
		return getItems(false);
	}

	/**
	 * Gets all the items in the inventory.
	 * 
	 * @param cached
	 *            If the inventory interface should be updated before returning
	 *            the items.
	 * @return <tt>RSItem</tt> array of the current inventory items or an empty
	 *         <tt>RSItem[]</tt> if unavailable.
	 */
	public RSItem[] getItems(final boolean cached) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return new RSItem[0];
			}
		}
		
		final RSInterfaceChild invIface = getInterface(cached);
		if (invIface != null) {
			final RSInterfaceChild[] comps = invIface.getChildren();
			if (comps.length > 0) {
				int len = 0;
				for (final RSInterfaceChild com : comps) {
					if (com.getType() == 5) {
						++len;
					}
				}

				final RSItem[] inv = new RSItem[len];
				for (int i = 0; i < len; ++i) {
					final RSInterfaceChild item = comps[i];
					final int idx = item.getChildIndex();
					if (idx >= 0) {
						inv[idx] = new RSItem(item);
					} else {
						return new RSItem[0];
					}
				}
				return inv;
			}
		}

		return new RSItem[0];
	}

	/**
	 * Gets all the items in the inventory matching any of the provided IDs.
	 * 
	 * @param ids
	 *            Valid IDs.
	 * @return <tt>RSItem</tt> array of the matching inventory items.
	 */
	public RSItem[] getItems(final int... ids) {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return new RSItem[0];
			}
		}
		
		final LinkedList<RSItem> items = new LinkedList<RSItem>();
		for (final RSItem item : getItems()) {
			for (final int i : ids) {
				if (item.getID() == i) {
					items.add(item);
					break;
				}
			}
		}
		return items.toArray(new RSItem[items.size()]);
	}

	/**
	 * Gets the selected inventory item.
	 * 
	 * @return The current selected item, or <tt>null</tt> if none is selected.
	 */
	public RSItem getSelectedItem() {
		final int index = getSelectedItemIndex();
		return index == -1 ? null : getItemAt(index);
	}

	/**
	 * Gets the selected item index.
	 * 
	 * @return The index of current selected item, or -1 if none is selected.
	 */
	public int getSelectedItemIndex() {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return -1;
			}
		}
		
		final RSInterfaceChild[] comps = getInterface().getChildren();
		for (int i = 0; i < Math.min(28, comps.length); ++i) {
			if (comps[i].getBorderThickness() == 2) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the selected item name.
	 * 
	 * @return The name of the current selected item, or null if none is
	 *         selected.
	 */
	public String getSelectedItemName() {
		final String name = methods.game.client().getSelectedItemName();
		return name == null ? null : name.replaceAll("<[\\w\\d]+=[\\w\\d]+>",
		"");
	}

	/**
	 * Checks if your inventory is open and is full.
	 * 
	 * @return <tt>true</tt> if your inventory contains 28 items; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isFull() {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return false;
			}
		}
		
		return getCount(false) == 28;
	}
	
	/**
	 * Checks if your inventory is open and is empty.
	 * 
	 * @return <tt>true</tt> if your inventory is empty; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isEmpty() {
		if (!isOpen())  {
			if (canOpen())  {
				methods.game.openTab(Game.tabInventory);
			
				methods.iface.waitForChildOpen(methods.inventory.getInterface(),
											   methods.random(1400,1600));
			}
			else  {
				return true;
			}
		}
		
		return getCount(false) == 0;
	}

	/**
	 * Checks if an inventory item is selected.
	 * 
	 * @return <tt>true</tt> if an item in your inventory is selected; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isItemSelected() {
		return getSelectedItemIndex() != -1;
	}

	/**
	 * 
	 * @return <tt>true</tt> if item is selected. otherwise <tt>false</tt>.
	 * @auther Sorcermus
	 */
	public boolean isItemSelected(final int id) {
		final RSItem[] items = getItems();
		final RSInterfaceChild[] comps = getInterface().getChildren();
		
		for (int i = 0; i < Math.min(28, comps.length); ++i) {
			if (comps[i].getBorderThickness() == 2) {
                            for (final RSItem item : items) {
                                if (item.getID() == id) {
                                    return true;
                                }
                            }
                        }
		}
		return false;
	}

	/**
	 * Randomizes a point.
	 * 
	 * @param inventoryPoint
	 *            The inventory point to be randomized.
	 * @return A randomized <tt>Point</tt> from the center of the given
	 *         <tt>Point</tt>.
	 */
	public Point randomizeItemPoint(final Point inventoryPoint) {
		return new Point(inventoryPoint.x + methods.random(-10, 10),
				inventoryPoint.y + methods.random(-10, 10));
	}

	/**
	 * Selects the first item in the inventory with the provided ID.
	 * 
	 * @param itemID
	 *            The ID of the item to select.
	 * @return <tt>true</tt> if the item was selected; otherwise <tt>false</tt>.
	 */
	public boolean selectItem(final int itemID) {
		final RSItem item = getItem(itemID);
		return item != null && selectItem(item);
	}

	/**
	 * Selects the specified item in the inventory
	 * 
	 * @param item
	 *            The item to select.
	 * @return <tt>true</tt> if the item was selected; otherwise <tt>false</tt>.
	 */
	public boolean selectItem(final RSItem item) {
		if (item==null)  {
			return false;
		}
		
		final int itemID = item.getID();
		RSItem selItem = getSelectedItem();
		if (selItem != null && selItem.getID() == itemID) {
			return true;
		}
		if (!item.action("Use")) {
			return false;
		}
		for (int c = 0; c < 5 && (selItem = getSelectedItem()) == null; c++) {
			methods.sleep(methods.random(200, 300));
		}
		return selItem != null && selItem.getID() == itemID;
	}

	/**
	 * Uses two items together.
	 * 
	 * @param itemID
	 *            The first item ID to use.
	 * @param targetID
	 *            The item ID you want the first parameter to be used on.
	 * @return <tt>true</tt> if the first item has been "used" on the other;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean useItem(final int itemID, final int targetID) {
		final RSItem item = getItem(itemID);
		final RSItem target = getItem(targetID);
		return item != null && target != null && useItem(item, target);
	}

	/**
	 * Uses an item on an object.
	 * 
	 * @param itemID
	 *            The item ID to use on the object.
	 * @param object
	 *            The RSObject you want the item to be used on.
	 * @return <tt>true</tt> if the "use" action had been used on both the
	 *         RSItem and RSObject; otherwise <tt>false</tt>.
	 */
	public boolean useItem(final int itemID, final RSObject object) {
		final RSItem item = getItem(itemID);
		return item != null && useItem(item, object);
	}

	/**
	 * Uses two items together.
	 * 
	 * @param item
	 *            The item to use on another item.
	 * @param targetItem
	 *            The item you want the first parameter to be used on.
	 * @return <tt>true</tt> if the "use" action had been used on both items;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean useItem(final RSItem item, final RSItem targetItem) {
		if (item==null || targetItem==null)  {
			return false;
		}
		methods.game.openTab(Game.tabInventory);
		return selectItem(item) && targetItem.action("Use");
	}

	/**
	 * Uses an item on an object.
	 * 
	 * @param item
	 *            The item to use on another item.
	 * @param targetObject
	 *            The RSObject you want the first parameter to be used on.
	 * @return <tt>true</tt> if the "use" action had been used on both the
	 *         RSItem and RSObject; otherwise <tt>false</tt>.
	 */
	public boolean useItem(final RSItem item, final RSObject targetObject) {
		if (item==null || targetObject==null)  {
			return false;
		}
		
		methods.game.openTab(Game.tabInventory);
		return selectItem(item)
		&& targetObject.action("Use", targetObject.getName());
	}

	
	/**
	 * Waits up to the specified number of milliseconds for the 
	 * inventory to become full.
	 * 
	 * @param ms
	 *            : the maximum time in MS to wait for
	 */
	public void waitForFull(final int ms) {
		final long start = System.currentTimeMillis();
		while (!isFull() && System.currentTimeMillis() - start < ms) {
			methods.wait(methods.random(50,100));
		}
		
		methods.wait(methods.random(250,500));
	}
	
	/**
	 * Waits up to the specified number of milliseconds for the 
	 * inventory to become empty.
	 * 
	 * @param ms
	 *            : the maximum time in MS to wait for
	 */
	public void waitForEmpty(final int ms) {
		final long start = System.currentTimeMillis();
		while (!isEmpty() && System.currentTimeMillis() - start < ms) {
			methods.wait(methods.random(50,100));
		}
		
		methods.wait(methods.random(250,500));
	}
	
	/**
	 * Waits up to the specified number of milliseconds for the 
	 * inventory count of the specified item to be GREATER OR EQUAL
	 * to the specified amount.
	 * 
	 * @param item
	 *            : the id of the item to wait for
	 * @param count
	 *            : the amount of item to wait for
	 * @param ms
	 *            : the maximum time in MS to wait for
	 * @return: the count of the item in the inventory.
	 */
	public int waitForCount(final int item, final int count, final int ms) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < ms) {
			if (getCount(item) >= count) {
				break;
			}
		}

		methods.wait(methods.random(250,500));
		return getCount(item);
	}
	
	/**
	 * Waits up to the specified number of milliseconds for the 
	 * inventory count of the specified item to be GREATER than
	 * the specified amount.
	 *  
	 * @param item
	 *            : the id of the item to wait for
	 * @param count
	 *            : the amount of item to wait for
	 * @param ms
	 *            : the maximum time in MS to wait for
	 * @return: the count of the item in the inventory.
	 */
	public int waitForCountGreater(final int item, final int count, final int ms) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < ms) {
			if (getCount(item) > count) {
				break;
			}
		}

		methods.wait(methods.random(250,500));
		return getCount(item);
	}
	
	/**
	 * Waits up to the specified number of milliseconds for the 
	 * inventory count to be GREATER than the specified amount.
	 * 
	 * @param count
	 *            : the inventory count to wait for
	 * @param ms
	 *            : the maximum time in MS to wait for
	 * @return: the count of the items in the inventory.
	 */
	public int waitForCountGreater(final int count, final int ms) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < ms) {
			if ((count>27 && getCount(true) > count) ||
			    getCount() > count)  {  
				break;
			}
		}
		
		methods.wait(methods.random(250,500));
		return getCount();
	}
	
	/**
	 * Waits up to the specified number of milliseconds for the 
	 * inventory count of the specified item to be LESS than
	 * the specified amount.
	 * 
	 * @param item
	 *            : the id of the item to wait for
	 * @param count
	 *            : the amount of item to wait for
	 * @param ms
	 *            : the maximum time in MS to wait for
	 * @return: the count of the item in the inventory.
	 */
	public int waitForCountLess(final int item, final int count, final int ms) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < ms) {
			if (getCount(item) < count) {
				break;
			}
		}
		
		methods.wait(methods.random(250,500));
		return getCount(item);
	}
	
	/**
	 * Waits up to the specified number of milliseconds for the 
	 * inventory count to be LESS than the specified amount.
	 * 
	 * @param count
	 *            : the inventory amount to wait for
	 * @param ms
	 *            : the maximum time in MS to wait for
	 * @return: the count of the items in the inventory.
	 */
	public int waitForCountLess(final int count, final int ms) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < ms) {
			if ((count>27 && getCount(true) < count)  ||
				getCount() < count) {
				break;
			}
		}
		
		methods.wait(methods.random(250,500));
		return getCount();
	}
	

	/**
	 * Waits up to the specified time for the inventory tab to be open.
	 * 
	 * @param ms amount of time to wait.
	 * @return <tt>true</tt> if the inventory tab is open, <tt>false</tt> otherwise.
	 */
	public boolean waitForOpen(int ms)  {
		long stop = System.currentTimeMillis()+ms;
		
		while (System.currentTimeMillis()<stop &&
				methods.game.getCurrentTab()!=Game.tabInventory)  {
			methods.wait(methods.random(80,110));
		}
		
		// This is intentionally elongated to allow for easy debugging
		// using eclipse.
		int currTab = methods.game.getCurrentTab();
		
		return currTab == Game.tabInventory;								
	}
}
