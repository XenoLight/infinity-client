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

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;

/**
 * Game equipment tab.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class Equipment {

	private final Methods methods;
	public static final int slots = 11;
	public static final int iFaceEquipment = 387;
	public static final int helmet = 8;
	public static final int cape = 11;
	public static final int necklace = 14;
	public static final int weapon = 17;
	public static final int body = 20;
	public static final int shield = 23;
	public static final int legs = 26;
	public static final int hands = 29;
	public static final int feet = 32;
	public static final int ring = 35;
	public static final int ammo = 38;

	public Equipment() {
		this.methods = Bot.methods;
	}

	/**
	 * Used to determine of the equipment tab is open.
	 * 
	 * @return <tt>true</tt> if the equipment tab is open, <tt>false</tt> otherwise.
	 */
	public boolean isOpen()  {	
		return methods.game.getCurrentTab()==Game.tabEquipment &&
				methods.iface.get(iFaceEquipment).isValid();
	}
	
	/**
	 * Opens the equipment tab.
	 * 
	 * @return <tt>true</tt> if the equipment tab is open, <tt>false</tt> otherwise.
	 */
	public boolean open()  {
		// If a deposit box is open, the inventory tab is disabled, so don't
		// try to open it.
		if (methods.bank.isDepositOpen())  {
			return false;
		}
		
		if (!isOpen() && canOpen())  {
			methods.game.openTab(Game.tabEquipment);
			
			methods.iface.waitForOpen(methods.equipment.getInterface(),
										   methods.random(1400,1600));
		}
		
		return isOpen();
	}
	
	/**
	 * Determines if the equipment can be opened.  For example, when the bank deposit 
	 * box is open, the equipment tab cannot be opened.
	 * 
	 * @return <tt>true</tt> if the equipment tab can be opened, <tt>false</tt> otherwise
	 */
	public boolean canOpen() {
		
		// Return false if the normal inventory interface for banking, shops,
		// price check, equipment bonuses or dungeoneering is open.
		if (methods.iface.get(Inventory.INTERFACE_INVENTORY_BANK).isValid()) {
			final RSInterfaceChild bankInv = methods.iface.getChild(
					Inventory.INTERFACE_INVENTORY_BANK, 0);
			if (bankInv != null && bankInv.getAbsoluteX()>50) {
				return !bankInv.isValid();
			}
		}
		
		if (methods.bank.isDepositOpen())  {
			return false;
		}
		
		if (methods.iface.get(Inventory.INTERFACE_INVENTORY_SHOP).isValid()) {
			final RSInterfaceChild shopInv = methods.iface.getChild(
					Inventory.INTERFACE_INVENTORY_SHOP, 0);
			if (shopInv != null && shopInv.getAbsoluteX() > 50) {
				return !shopInv.isValid();
			}
		}
		
		if (methods.iface.get(Inventory.INTERFACE_INVENTORY_PRICE_CHECK).isValid()) {
			final RSInterfaceChild priceInv = methods.iface.getChild(
					Inventory.INTERFACE_INVENTORY_PRICE_CHECK, 0);
			if (priceInv != null && priceInv.getAbsoluteX() > 50) {
				return !priceInv.isValid();
			}
		}
		
		// This interface will appear as valid even when closed.  We must check if 
		// child 0 has any children.  If so, it is open.
		if (methods.iface.get(Inventory.INTERFACE_INVENTORY_EQUIPMENT_BONUSES).isValid()) {
			final RSInterfaceChild equipInv = methods.iface.getChild(
					Inventory.INTERFACE_INVENTORY_EQUIPMENT_BONUSES, 0);
			
			final RSInterfaceChild[] children = equipInv.getChildren();
			if (equipInv != null && children!=null && children.length>0) {
				return !equipInv.isValid();
			}
		}
		
		if (methods.iface.get(Inventory.INTERFACE_INVENTORY_DUNGEONEERING_SHOP).isValid()) {
			final RSInterfaceChild dungInv = methods.iface.getChild(
					Inventory.INTERFACE_INVENTORY_DUNGEONEERING_SHOP, 0);
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
	 * Performs an action on a given equipped item ID by clicking it in the
	 * equipment tab. Written by Garrett.
	 * 
	 * @param id
	 *            The ID of the item to look for.
	 * @param act
	 *            The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 */
	public boolean clickItem(final int id, final String act) {
		if (methods.game.getCurrentTab() != Game.tabEquipment) {
			methods.game.openTab(Game.tabEquipment);
			methods.wait(methods.random(900, 1500));
		}
		final RSInterfaceChild[] equip = getInterface().getChildren();
		for (int i = 0; i < 11; i++) {
			if (equip[i * 3 + 8].getChildID() == id) {
				final int x = equip[i * 3 + 8].getAbsoluteX() + 2;
				final int y = equip[i * 3 + 8].getAbsoluteY() + 2;
				final int width = equip[i * 3 + 8].getWidth() - 2;
				final int height = equip[i * 3 + 8].getHeight() - 2;
				methods.mouse.move(new Point(methods.random(x, x + width),
						methods.random(y, y + height)));
				methods.wait(methods.random(50, 100));
				return methods.menu.action(act);
			}
		}
		return false;
	}

	/**
	 * Checks whether the player has all of the given items equipped.
	 * 
	 * @param id
	 *            The item ID to check for. Same as the equipment/ item id in
	 *            the inventory.
	 * @return <tt>true</tt> if specified item is currently equipped; otherwise
	 *         <tt>false</tt>.
	 * @see #getArray()
	 */
	public boolean contains(final int... ids) {
		final RSItem[] equipID = getArray();
		int count = 0;
		for (final int item : ids) {
			for (final RSItem equip : equipID) {
				if (equip.getID() == item) {
					count++;
					break;
				}
			}
		}
		return count == ids.length;
	}

	/**
	 * Checks if the player has one (or more) of the given items equipped.
	 * 
	 * @param items
	 *            The IDs of items to check for.
	 * @return <tt>true</tt> if the player has one (or more) of the given items
	 *         equipped; otherwise <tt>false</tt>.
	 */
	public boolean containsOneOf(final int... items) {
		for (final RSItem item : getArray()) {
			for (final int id : items) {
				if (item.getID() == id) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the equipment array.
	 * 
	 * @return array containing all equipped items
	 */
	public RSItem[] getArray() {
		final RSInterfaceChild[] equip = getInterface().getChildren();
		final RSItem[] items = new RSItem[slots];
		for (int i = 0; i < items.length; i++) {
			items[i] = new RSItem(equip[i * 3 + 8]);
		}
		return items;
	}

	/**
	 * Gets the cached equipment array (i.e. does not open the interface).
	 * 
	 * @return The items equipped as seen when the equipment tab was last
	 *         opened.
	 */
	public RSItem[] getCachedItems() {
		final RSInterface equipment = methods.iface.get(iFaceEquipment);
		final RSInterfaceChild[] components = equipment.getChildren();
		final RSItem[] items = new RSItem[slots];
		for (int i = 0; i < items.length; i++) {
			items[i] = new RSItem(components[i * 3 + 8]);
		}
		return items;
	}

	/**
	 * Returns the number of items equipped excluding stack sizes.
	 * 
	 * @return Amount of items currently equipped.
	 */
	public int getCount() {
		return slots - getCount(-1);
	}

	/**
	 * Returns the number of items matching a given ID equipped excluding stack
	 * sizes.
	 * 
	 * @param id
	 *            The item ID to count. Same as the equipment/item id in the
	 *            inventory.
	 * @return Amount of specified item currently equipped.
	 * @see #getArray()
	 */
	public int getCount(final int id) {
		int count = 0;
		for (final RSItem item : getArray()) {
			if (item.getID() == id) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Gets the equipment interface.
	 * 
	 * @return the equipment interface
	 * @see #setEnforceTabFocus(boolean)
	 */
	public RSInterface getInterface() {
		/* Tab needs to be open for it to update it's content -.- */
		if (methods.game.openTabs
				&& methods.game.getCurrentTab() != Game.tabEquipment) {
			if (methods.bank.isOpen()) {
				methods.bank.close();
			}
			methods.game.openTab(Game.tabEquipment);
			methods.wait(methods.random(900, 1500));
		}
		return methods.iface.get(iFaceEquipment);
	}

	/**
	 * Gets the equipment item at a given index.
	 * 
	 * @param index
	 *            The item index.
	 * @return The equipped item.
	 */
	public RSItem getItem(final int index) {
		return new RSItem(getInterface().getChildren()[index]);
	}

	/**
	 * Gets the equipment array.
	 * 
	 * @return An array containing all equipped items
	 */
	public RSItem[] getItems() {
		final RSInterfaceChild[] equip = getInterface().getChildren();
		final RSItem[] items = new RSItem[slots];
		for (int i = 0; i < items.length; i++) {
			items[i] = new RSItem(equip[i * 3 + 8]);
		}
		return items;
	}

	public int[] getStackArray() {
		return new int[0];
	}
}
