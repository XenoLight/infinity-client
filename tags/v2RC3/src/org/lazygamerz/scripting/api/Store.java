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

/**
 * This class is for all the Store operations.
 * 
 * @author Runedev development team.
 */
public class Store {

	private final Methods methods;
	private int stock = 24;
	public static final int INTERFACE_STORE = 620;
	public static final int INTERFACE_STORE_BUTTON_CLOSE = 18;
	public static final int INTERFACE_STORE_ITEMS = 25;

	public Store() {
		this.methods = Bot.methods;
	}

	/**
	 * Performs a given action on the specified item id. Returns atMenu.
	 * 
	 * @param itemID
	 *            the id of the item
	 * @param txt
	 *            the action to perform (see {@link Methods#atMenu})
	 * @return true on success
	 */
	public boolean atItem(final int itemID, final String txt) {
		if (!methods.game.isLoggedIn() || !isOpen()) {
			return false;
		}
		/*
		 * final int[] itemArray = getItemArray(); for (int off = 0; off <
		 * itemArray.length; off++) { if (itemArray[off] == itemID) {
		 * methods.clickMouse(getItemPoint(off), 5, 5, false); return
		 * methods.atMenu(txt);
		 */
		final RSInterfaceChild item = getItemByID(itemID);
		if ((item != null) && item.isValid()) {
			return methods.iface.clickChild(item, txt);
		}

		return false;
	}

	/**
	 * Tries to buy an item.
	 * <p/>
	 * 0 is All. 1,5,10 use buy 1,5,10 while other numbers buy X.
	 * 
	 * @param itemID
	 *            The id of the item.
	 * @param count
	 *            The number to buy.
	 * @return true on success
	 */
	public boolean buy(final int itemID, final int count) {
		if (count < 0) {
			throw new IllegalArgumentException("count < 0 " + count);
		}
		if (!isOpen()) {
			return false;
		}
		final int inventoryCount = methods.inventory.getCount(true);
		for (int tries = 0; tries < 5; tries++) {
			switch (count) {
			case 0:
				/*
				 * Withdraw All
				 */
				atItem(itemID, "Buy All");
				break;
			case 1:
				/*
				 * Withdraw 1
				 */
				atItem(itemID, "Buy 1");
				break;
			case 5:
				/*
				 * Withdraw 5
				 */
				atItem(itemID, "Buy 5");
				break;
			case 10:
				/*
				 * Withdraw 10
				 */
				atItem(itemID, "Buy 10");
				break;
			case 50:
				/*
				 * Withdraw 50
				 */
				atItem(itemID, "Buy 50");
			default:
				/*
				 * Withdraw x
				 */
				atItem(itemID, "Buy X");
				methods.wait(methods.random(900, 1100));
				Bot.getInputManager().sendKeys("" + count, true);
			}
			methods.wait(methods.random(500, 700));
			if (methods.inventory.getCount(true) > inventoryCount) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Closes the Store interface. Temp Fix until interfaces are fixed
	 * 
	 * @return true if the interface is no longer open
	 */
	public boolean close() {
		if (!isOpen()) {
			return true;
		}
		/*
		 * methods.clickMouse(new Point(methods.random(481, 496),
		 * methods.random( 27, 42)), true);
		 */
		methods.iface.getChild(620, 18).click();
		methods.wait(methods.random(500, 600));
		return !isOpen();
	}

	/**
	 * Gets the store interface.
	 * 
	 * @return the store interface
	 */
	public RSInterface getInterface() {
		return methods.iface.get(620);
	}

	public RSInterfaceChild getItem(final int index) {
		final RSInterfaceChild[] items = getItems();
		if (items != null) {
			for (final RSInterfaceChild item : items) {
				if (item.getChildIndex() == index) {
					return item;
				}
			}
		}

		return null;
	}

	/**
	 * Makes it easier to get Items in the store Written by Fusion89k
	 * 
	 * @param id
	 *            ID of the item to get
	 * @return the component of the item
	 */
	public RSInterfaceChild getItemByID(final int id) {
		final RSInterfaceChild[] items = getItems();
		if (items != null) {
			for (final RSInterfaceChild item : items) {
				if (item.getChildID() == id) {
					return item;
				}
			}
		}

		return null;
	}

	public String[] getItemNames() {
		final RSInterfaceChild[] items = getInterface().getChild(stock)
		.getChildren();
		if (items != null) {
			final String[] value = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				value[items[i].getChildIndex()] = items[i].getChildName();
			}
			return value;
		}

		return new String[0];
	}

	/**
	 * Gets the point on the screen for a given item. Numbered left to right
	 * then top to bottom. Written by Qauters.
	 * 
	 * @param slot
	 *            the index of the item
	 * @return the point of the item
	 */
	public Point getItemPoint(final int slot) {
		/*
		 * And I will strike down upon thee with great vengeance and furious
		 * anger those who attempt to replace the following code with fixed
		 * constants!
		 */

		if (slot < 0) {
			throw new IllegalArgumentException("slot < 0 " + slot);
		}

		final RSInterfaceChild item = getItem(slot);
		if (item != null) {
			return item.getPosition();
		}

		return new Point(-1, -1);
	}

	public RSInterfaceChild[] getItems() {
		if ((getInterface() == null)
				|| (getInterface().getChild(stock) == null)) {
			return new RSInterfaceChild[0];
		}

		return getInterface().getChild(stock).getChildren();
	}

	/**
	 * Gets the array of item stack sizes in the store
	 * 
	 * @return the stack sizes
	 */
	public int[] getStackSizes() {
		final RSInterfaceChild[] items = getInterface().getChild(stock)
		.getChildren();
		if (items != null) {
			final int[] value = new int[items.length];
			for (int i = 0; i < items.length; i++) {
				value[i] = items[i].getChildStackSize();
			}
			return value;
		}

		return new int[0];
	}

	/**
	 * @return true if the store interface is open, false otherwise
	 */
	public boolean isOpen() {
		return getInterface().isValid();
	}

	/**
	 * Allows switching between main stock and player stock Written by Fusion89k
	 * 
	 * @param mainStock
	 *            <tt>true</tt> for MainStock; <tt>false</tt> for PlayerStock
	 */
	public void switchStock(final boolean mainStock) {
		stock = mainStock ? 24 : 26;
	}
}
