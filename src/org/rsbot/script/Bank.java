package org.rsbot.script;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * This class is for all the Bank operations.
 */
public class Bank implements MessageListener  {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());


	private final Methods methods;
	public static String[] banks = { "Bank booth", "Bank Booth", "Shantay chest",
		"Bank chest", "Deposit chest", "Chest" };
	public static String[] bankers = { "Banker", "Fremennik banker",
	"Emerald Benedict" };

	public static final int[] Bankers = { 44, 45, 494, 495, 499, 553, 958,
		1036, 2271, 2354, 2355, 2759, 3824, 4456, 4467, 4458, 4459, 5488,
		5901, 5912, 5913, 6362, 6532, 6533, 6534, 6535, 7605, 8948, 9710,
		14367 };
	
	public static final int[] nonBankers = { 782, 2012, 2015, 2213, 6084,
		11402, 11758, 12759, 14367, 19230, 22819, 24914, 25808, 26972,
		27663, 29085, 34752, 35647,
		4483, 12308, 21301, 27663, 42192,
		6404, 9398, 20228, 25937, 26969, 36788,
		2012, 2015, 6396
	};
	
	public static final int[] BankBooths = { 782, 2012, 2015, 2213, 6084,
		11402, 11758, 12759, 14367, 19230, 22819, 24914, 25808, 26972,
		27663, 29085, 34752, 35647 };
	
	public static final int[] BankCounters = { 2012, 2015 };
	public static final int[] BankChests = { 4483, 12308, 21301, 27663, 42192 };
	public static final int[] BankDepositBox = { 6404, 9398, 20228, 25937, 26969, 36788 };

	/* Constants */
	public static final int INTERFACE_BANK = 762;
	public static final int INTERFACE_BANK_BUTTON_CLOSE = 43;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_BEAST_INVENTORY = 38;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS = 34;
	public static final int INTERFACE_BANK_BUTTON_DEPOSIT_WORN_ITEMS = 36;
	public static final int INTERFACE_BANK_BUTTON_HELP = 44;
	public static final int INTERFACE_BANK_BUTTON_INSERT = 15;
	public static final int INTERFACE_BANK_BUTTON_ITEM = 19;
	public static final int INTERFACE_BANK_BUTTON_NOTE = 19;
	public static final int INTERFACE_BANK_BUTTON_SEARCH = 17;
	public static final int INTERFACE_BANK_BUTTON_SWAP = 15;
	public static final int INTERFACE_BANK_BUTTON_OPEN_EQUIP = 117;
	public static final int INTERFACE_BANK_INVENTORY = 93;
	public static final int INTERFACE_BANK_ITEM_FREE_COUNT = 29;
	public static final int INTERFACE_BANK_ITEM_FREE_MAX = 30;
	public static final int INTERFACE_BANK_ITEM_MEMBERS_COUNT = 31;
	public static final int INTERFACE_BANK_ITEM_MEMBERS_MAX = 32;
	public static final int INTERFACE_BANK_SCROLLBAR = 114;
	public static final int INTERFACE_BANK_SEARCH = 752;
	public static final int INTERFACE_BANK_SEARCH_INPUT = 5;
	public static final int INTERFACE_EQUIPMENT = 667;
	public static final int INTERFACE_EQUIPMENT_COMPONENT = 7;
	public static final int INTERFACE_COLLECTION_BOX = 109;
	public static final int INTERFACE_COLLECTION_BOX_CLOSE = 14;
	public static final int[] INTERFACE_BANK_TAB = { 63, 61, 59, 57, 55, 53,
		51, 49, 47 };
	public static final int[] INTERFACE_BANK_TAB_FIRST_ITEM = { 78, 79, 80, 81,
		82, 83, 84, 85, 86 };
	public static final int INTERFACE_DEPOSIT_BOX = 11;
	public static final int INTERFACE_DEPOSIT_BOX_BUTTON_CLOSE = 15;
	public static final int INTERFACE_DEPOSIT_BOX_CONTENTS = 17;
	public static final int INTERFACE_DEPOSIT_BUTTON_DEPOSIT_BEAST_INVENTORY = 22;
	public static final int INTERFACE_DEPOSIT_BUTTON_DEPOSIT_CARRIED_ITEMS = 19;
	public static final int INTERFACE_DEPOSIT_BUTTON_DEPOSIT_WORN_ITEMS = 23;
	public static final int SETTING_BANK_TOGGLE_REARRANGE_MODE = 304;
	public static final int SETTING_BANK_TOGGLE_WITHDRAW_MODE = 115;

	public Bank() {
		this.methods = Bot.methods;
	}

	/**
	 * Given a bank booth object, uses the object model to perform the specified
	 * option.
	 * 
	 * @param booth
	 *            Bank booth object
	 * @param option
	 *            If null or "", left clicks the booth, otherwise the specified
	 *            option is performed.
	 * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
	 */
	public boolean atBankBooth(final RSObject booth, final String option) {
		boolean rslt;

		if (booth == null || !booth.isOnScreen()) {
			return false;
		}

		int ct = 0;
		while (ct++ < 10 && methods.menu.isOpen()) {
			methods.mouse.moveRandomly(150);
		}

		if (methods.menu.isOpen()) {
			return false;
		}

		if (option == null || option == "") {
			rslt = booth.action("Use-quickly")
			|| booth.action("Open Shantay chest")
			|| booth.action("Use Bank chest");
		} else {
			rslt = booth.action(option);
		}

		return rslt;
	}

	public boolean atBankBooth(final RSTile boothtile, final String option) {
		if (!boothtile.isOnScreen()) {
			return false;
		}

		final RSObject booth = methods.objects.getTopAt(boothtile);

		return atBankBooth(booth, option);
	}

	/**
	 * Performs a given action on the specified item ID.
	 * 
	 * @param itemID
	 *            The ID of the item.
	 * @param txt
	 *            The action to perform (see {@link Methods#atMenu}).
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
	 */
	public boolean atItem(final int itemID, final String txt) {
		if (!methods.game.isLoggedIn() || !isOpen()) {
			return false;
		}
		final RSInterfaceChild item = getItemByID(itemID);
		return (item != null) && item.isValid()
		&& methods.iface.clickChild(item, txt);

	}

	/**
	 * Closes the bank interface.  Waits up to 2-3 seconds for the
	 * bank to close before returning.
	 * 
	 * @return <tt>true</tt> if the bank interface is no longer open.
	 */
	public boolean close() {
		if (!isOpen()) {
			return true;
		}
		
		RSInterfaceChild child = null;
		
		if (isBankOpen()) {
			child = methods.iface.getChild(INTERFACE_BANK, 
					INTERFACE_BANK_BUTTON_CLOSE);
		}
		
		if (isDepositOpen()) {
			child = methods.iface.getChild(INTERFACE_DEPOSIT_BOX, 
					INTERFACE_DEPOSIT_BOX_BUTTON_CLOSE);
		}
		
		if (child!=null)  {
			child.click();
			
			if (methods.iface.waitForChildClose(child, methods.random(2000,3000)))  {
				methods.wait(methods.random(180,460));
			}
		}
		
		return !isOpen();
	}

	/**
	 * If bank is open, deposits specified amount of an item into the bank.
	 * Supports deposit boxes.
	 * 
	 * @param itemID
	 *            The ID of the item.
	 * @param number
	 *            The amount to deposit. 0 deposits All. 1,5,10 deposit
	 *            corresponding amount while other numbers deposit X.
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
	 */
	public boolean deposit(final int itemID, final int number) {
		if (number < 0) {
			throw new IllegalArgumentException("numberToDepsoit < 0 (" + number
					+ ")");
		}
		RSInterfaceChild item = null;
		int itemCount = 0;
		int inventoryCount = 0; 
		
		if (isBankOpen())  {
			inventoryCount = methods.inventory.getCount(itemID);
		}
		else if (isDepositOpen())  {
			inventoryCount = getBoxCount(itemID);
		}
		
		if (!isOpen()) {
			boolean match = false;
			for (int i = 0; i < 28; i++) {
				final RSInterfaceChild comp = 
					methods.iface.get(11).getChild(17).getChild(i);
				
				if (comp.getChildID() == itemID) {
					itemCount += comp.getChildStackSize();
					
					if (!match) {
						item = comp;
						match = true;
					}
				}
				if (itemCount > 1) {
					break;
				}
			}
		} else {
			RSItem it = methods.inventory.getItem(itemID);
			if (it!=null)  {
				item = methods.inventory.getItem(itemID).getComponent();
				itemCount = methods.inventory.getCount(true, itemID);
			}
			else  {
				return true;
			}
		}
		
		if (item == null) {
			return true;
		}

		switch (number) {
		case 0: /* Deposit All */
			methods.inventory.clickItem(itemID, "Deposit-All");
			break;
		case 1: /* Deposit 1 */
			methods.inventory.clickItem(itemID, "Deposit");
			break;
		case 5: /* Deposit 5 */
			methods.inventory.clickItem(itemID, "Deposit-" + number);
			break;
		default: /* Deposit x */
			if (!methods.inventory.clickItem(itemID, "Deposit-" + number)) {
				if (methods.inventory.clickItem(itemID, "Deposit-X")) {
					methods.wait(100);
					if (methods.iface.isTextInputOpen()) {
						methods.wait(methods.random(300, 800));
						Bot.getInputManager().sendKeys("" + number, true);
					}
				}
				break;
			}
		}
			
		int cInvCount = 99;
		
		if (isBankOpen())  {
			cInvCount = methods.inventory.getCount(itemID);
		}
		else if (isDepositOpen())  {
			cInvCount = getBoxCount(itemID);
		}

		return cInvCount < inventoryCount || cInvCount == 0;

	}

	/**
	 * Deposits all items in inventory.  Waits up to 2-3 seconds for
	 * the inventory to empty before returning.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean depositAll() {
		RSInterfaceChild child = null;
		
		if (isBankOpen())  {
			child = methods.iface.getChild(INTERFACE_BANK,
					INTERFACE_BANK_BUTTON_DEPOSIT_CARRIED_ITEMS);
			
			if (methods.inventory.getCount()==0)  {
				return true;
			}
			
			Bot.debug(logger, "Depositing all in inventory.");
			if (child!=null && child.click())  {
				methods.inventory.waitForEmpty(methods.random(2000,3000));
			}
			
			methods.wait(methods.random(150,250));
			return methods.inventory.isEmpty();
		}
		else if (isDepositOpen())  {
			child = methods.iface.getChild(INTERFACE_DEPOSIT_BOX,
					INTERFACE_DEPOSIT_BUTTON_DEPOSIT_CARRIED_ITEMS);
			
			// Deposit boxes require some more detailed work to determine
			// when the deposit is done.  Wait up to 3 seconds for the
			// box to empty.
			int currentInv = getBoxCount();
			if (currentInv==0)  {
				return true;
			}
			
			Bot.debug(logger, "Depositing all in deposit box.");
			if (child!=null && child.click())  {
				int ct=0;
				while (ct++<35 && currentInv == getBoxCount())  {
					methods.wait(100);
				}
				Bot.debug(logger, "Box deposit took "+(ct*100)+" ms");

				// Just add a little human response time delay.
				methods.wait(methods.random(150,250));
				
				boolean ret = getBoxCount() < currentInv;;
				Bot.debug(logger, "Returning "+ret);
				return  ret;
			}
		}
		
		return false;
	}

	/**
	 * Deposit everything your player has equipped. Supports deposit boxes.
	 * 
	 * @return <tt>true</tt> on success.
	 * @since 6 March 2009.
	 */
	public boolean depositAllEquipped() {
		if (isBankOpen()) {
			return methods.iface.clickChild(INTERFACE_BANK,
					INTERFACE_BANK_BUTTON_DEPOSIT_WORN_ITEMS);
		}
		
		return isDepositOpen()
				&& methods.iface.clickChild(INTERFACE_DEPOSIT_BOX,
						INTERFACE_DEPOSIT_BUTTON_DEPOSIT_WORN_ITEMS);
	}

	
	/**
	 * Deposits all items in inventory except for the given IDs. If no such IDs
	 * in inventory uses button.
	 * 
	 * @param items
	 *            The items not to deposit.
	 * @return <tt>true</tt> on success.
	 */
	public boolean depositAllExcept(final List<Integer> items) {
		if (isBankOpen())  {
			int inventoryCount = methods.inventory.getCount();
			
			if (inventoryCount==0)  {
				return true;
			}
			
			int[] inventoryArray = methods.inventory.getArray();
			
			outer: for (int off = 0; off < inventoryArray.length; off++) {
				if (inventoryArray[off] == -1) {
					continue;
				}
				
				for (final int item : items) {
					if (inventoryArray[off] == item) {
						continue outer;
					}
				}
	
				for (int tries = 0; tries < 5; tries++) {
					depositAllItem(inventoryArray[off]);
					methods.wait(methods.random(500, 700));
				
					if (methods.inventory.getCount() < inventoryCount) {
						break;
					}
					
					// If the stopScript flag was set, return false.  The
					// script should evaludate the stopScript setting prior
					// to proceeding.
					if (stopScript)  {
						return false;
					}
				}
				
				if (methods.inventory.getCount() >= inventoryCount) {
					return false;
				}
				
				inventoryArray = methods.inventory.getArray();
				inventoryCount = methods.inventory.getCount();
			}
			return true;
		}
		else if(isDepositOpen())  {
			int boxCount = getBoxCount();
			
			if (boxCount==0)  {
				return true;
			}
			
			RSInterfaceChild box = methods.iface.getChild(INTERFACE_DEPOSIT_BOX, INTERFACE_DEPOSIT_BOX_CONTENTS);
			
			if (box==null)  {
				return false;
			}
			RSInterfaceChild boxitems[] = box.getChildren();
						
			outer: for (int off = 0; off < boxitems.length; off++) {
				if (boxitems[off].getChildID() == -1) {
					continue;
				}
				
				int depBoxItemID = boxitems[off].getChildID();
				
				for (final int item : items) {
					if (depBoxItemID == item) {
						Bot.debug(logger, String.format("Item %d is an exception, not being deposited", depBoxItemID));
						continue outer;
					}
				}
	
				Bot.debug(logger, String.format("Depositing all of item id ", depBoxItemID));
				for (int tries = 0; tries < 5; tries++) {
					boxitems[off].action("Deposit-all");
					
					int ct=0;
					while (ct++<20 && getBoxCount()>=boxCount)  {
						methods.wait(methods.random(60, 100));
					}
					
					if (getBoxCount()<boxCount) {
						break;
					}
					
					// If the stopScript flag was set, return false.  The
					// script should evaludate the stopScript setting prior
					// to proceeding.
					if (stopScript)  {
						return false;
					}
				}
				
				if (getBoxCount()>=boxCount) {
					return false;
				}
				
				boxitems = box.getChildren();
				boxCount = getBoxCount();
			}
			
			return true;
		}
		
		return false;	}

	
	/**
	 * Deposits all items in inventory except for the given IDs. If no such IDs
	 * in inventory uses button.
	 * 
	 * @param items
	 *            The items not to deposit.
	 * @return <tt>true</tt> on success.
	 */
	public boolean depositAllExcept(final int... items) {
		List<Integer> itemList = new ArrayList<Integer>();
		
		for (int i : items)  {
			itemList.add(i);
		}
		
		return depositAllExcept(itemList);
	}
	

	/**
	 * Deposits everything your familiar is carrying. Supports deposit boxes.
	 * 
	 * @return <tt>true</tt> on success
	 * @since 6 March 2009.
	 */
	public boolean depositAllFamiliar() {
		if (isBankOpen()) {
			return methods.iface.clickChild(INTERFACE_BANK,
					INTERFACE_BANK_BUTTON_DEPOSIT_BEAST_INVENTORY);
		}
		
		return isDepositOpen()
				&& methods.iface.clickChild(INTERFACE_DEPOSIT_BOX,
						INTERFACE_DEPOSIT_BUTTON_DEPOSIT_BEAST_INVENTORY);
	}

	/**
	 * {@link deposit, item}
	 * 
	 * Deposits based on item stack amount. This is much more human in action
	 * that standard bank.depositAll
	 */
	public boolean depositAllItem(final int item) {

		if (methods.inventory.getCount(item) > 0) {
			switch (methods.inventory.getCount(item)) {
			case 1:
				methods.inventory.clickItem(item, "Deposit");
				break;
			case 5:
				if (methods.random(1, 3) > 1) {
					methods.inventory.clickItem(item, "Deposit-5");
					break;
				}
			case 10:
				if (methods.random(1, 3) > 1) {
					methods.inventory.clickItem(item, "Deposit-10");
					break;
				}
			default:
				methods.inventory.clickItem(item, "Deposit-All");
				break;
			}
			final long inventoryTimeCheck = System.currentTimeMillis();
			while (methods.inventory.getCount(item) != 0
					&& System.currentTimeMillis() - inventoryTimeCheck < 2000) {
				methods.wait(50);
			}
			if (methods.inventory.getCount(item) == 0) {
				return true;
			} else {
				depositAllItem(item);
			}
		}

		return false;
	}

	

	/**
	 * Gets the count of deposit box items ignoring stack sizes while
	 * deposit box is open.
	 * 
	 * @return The count.
	 */
	public int getBoxCount() {
		if (!isDepositOpen()) {
			return -1;
		}
		
		int count = 0;
		
		for (int i = 0; i < 28; i++) {
			if (methods.iface.getChild(INTERFACE_DEPOSIT_BOX,INTERFACE_DEPOSIT_BOX_CONTENTS).isValid()
					&& methods.iface.getChild(INTERFACE_DEPOSIT_BOX, INTERFACE_DEPOSIT_BOX_CONTENTS).getChild(i)
					.getChildID() != -1) {
				count++;
			}
		}
		return count;
	}
	
	
	/**
	 * Determines of the collection box inventory contains any of the specified
	 * items.
	 * 
	 * @param items
	 * 			The items the deposit box may contain.
	 * @return  <tt>true</tt> if the box contains any of the specified items,
	 * 	<tt>false</tt> otherwise.
	 */
	public boolean boxContains(int... items)  {
		return getBoxCount(items)>0;
	}
	
	/**
	 * Gets the count of deposit box items ignoring stack sizes while
	 * deposit box is open.
	 * 
	 * @return The count.
	 */
	public int getBoxCount(int... items) {
		if (!isDepositOpen()) {
			return -1;
		}
		
		int count = 0;
		
		List<Integer> itemsList = new ArrayList<Integer>();
		for (int item: items)  {
			if (item!=-1)  {
				itemsList.add(item);
			}
		}
		
		for (int i = 0; i < 28; i++) {
			if (methods.iface.getChild(INTERFACE_DEPOSIT_BOX,INTERFACE_DEPOSIT_BOX_CONTENTS).isValid())  {
				RSInterfaceChild item = methods.iface.getChild(INTERFACE_DEPOSIT_BOX, INTERFACE_DEPOSIT_BOX_CONTENTS).getChild(i);
			
				if (item!=null && itemsList.contains(item.getChildID())) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	/**
	 * Gets the count of deposit box items ignoring stack sizes and 
	 * excluding the specified items.
	 * 
	 * @return The count.
	 */
	public int getBoxCountExcept(int items[]) {
		if (!isDepositOpen()) {
			return -1;
		}
		
		int count = 0;
		
		List<Integer> itemsList = new ArrayList<Integer>();
		for (int item: items)  {
			if (item!=-1)  {
				itemsList.add(item);
			}
		}
		
		for (int i = 0; i < 28; i++) {
			if (methods.iface.getChild(INTERFACE_DEPOSIT_BOX,INTERFACE_DEPOSIT_BOX_CONTENTS).isValid())  {
				RSInterfaceChild item = methods.iface.getChild(INTERFACE_DEPOSIT_BOX, INTERFACE_DEPOSIT_BOX_CONTENTS).getChild(i);
			
				if (item!=null && item.getChildID()!=-1 && 
					!itemsList.contains(item.getChildID())) {
					count++;
				}
			}
		}
		
		return count;
	}

	/**
	 * Gets the deposit box interface.
	 * 
	 * @return The deposit box <code>RSInterface</code>.
	 */
	public RSInterface getBoxInterface() {
		return methods.iface.get(INTERFACE_BANK);
	}

	/**
	 * Returns the sum of the count of the given items in the bank.
	 * 
	 * @param items
	 *            The array of items.
	 * @return The sum of the stacks of the items.
	 */
	public int getCount(final int... items) {
		int itemCount = 0;
		final RSItem[] inventoryArray = getItems();
		for (final RSItem item : inventoryArray) {
			for (final int id : items) {
				if (item.getID() == id) {
					itemCount += item.getStackSize();
				}
			}
		}
		return itemCount;
	}

	/**
	 * Get current tab open in the bank.
	 * 
	 * @return int of tab (0-8), or -1 if none are selected (bank is not open).
	 */
	public int getCurrentTab() {
		for (int i = 0; i < INTERFACE_BANK_TAB.length; i++) {
			if (methods.iface.get(INTERFACE_BANK)
					.getChild(INTERFACE_BANK_TAB[i] - 1).getBackgroundColor() == 1419) {
				return i;
			}
		}
		return -1; /* no selected ones. Bank may not be open. */
	}

	/**
	 * Gets the bank interface.
	 * 
	 * @return The bank <code>RSInterface</code>.
	 */
	public RSInterface getInterface() {
		return  methods.iface.get(INTERFACE_BANK);
	}
	
	/**
	 * Gets the deposit box interface.
	 * 
	 * @return The bank <code>RSInterface</code>.
	 */
	public RSInterface getDepositBoxInterface() {
		return  methods.iface.get(INTERFACE_DEPOSIT_BOX);
	}

	/**
	 * Gets the first item with the provided ID in the bank.
	 * 
	 * @param id
	 *            ID of the item to get.
	 * @return The component of the item; otherwise null.
	 */
	public RSItem getItem(final int id) {
		final RSItem[] items = getItems();
		if (items != null) {
			for (final RSItem item : items) {
				if (item.getID() == id) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the array of item IDs in the bank.
	 * 
	 * @return The item IDs array.
	 */
	public int[] getItemArray() {
		final RSInterfaceChild[] items = getInterface().getChild(
				INTERFACE_BANK_INVENTORY).getChildren();
		if (items != null) {
			final int[] value = new int[items.length];
			for (final RSInterfaceChild item : items) {
				value[item.getChildIndex()] = item.getChildID();
			}
			return value;
		}

		return new int[0];
	}

	/**
	 * Gets the <code>RSComponent</code> of the given item at the specified
	 * index.
	 * 
	 * @param index
	 *            The index of the item.
	 * @return <code>RSComponent</code> if item is found at index; otherwise
	 *         null.
	 */
	public RSItem getItemAt(final int index) {
		final RSItem[] items = getItems();
		if (items != null) {
			for (final RSItem item : items) {
				if (item.getComponent().getChildIndex() == index) {
					return item;
				}
			}
		}

		return null;
	}

	/**
	 * Makes it easier to get Items in the bank. Written by Fusion89k.
	 * 
	 * @param id
	 *            ID of the item to get.
	 * @return The component of the item.
	 */
	public RSInterfaceChild getItemByID(final int id) {
		final RSInterfaceChild[] items = getItemsComponent();
		if (items != null) {
			for (final RSInterfaceChild item : items) {
				if (item.getChildID() == id) {
					return item;
				}
			}
		}

		return null;
	}

	public RSInterfaceChild getItemComponent(final int index) {
		final RSInterfaceChild[] items = getItemsComponent();
		if (items != null) {
			for (final RSInterfaceChild item : items) {
				if (item.getChildIndex() == index) {
					return item;
				}
			}
		}

		return null;
	}

	public String[] getItemNames() {
		final RSInterfaceChild[] items = getInterface().getChild(
				INTERFACE_BANK_INVENTORY).getChildren();
		if (items != null) {
			final String[] value = new String[items.length];
			for (final RSInterfaceChild item : items) {
				value[item.getChildIndex()] = item.getChildName();
			}
			return value;
		}

		return new String[0];
	}

	/**
	 * Gets the point on the screen for a given item. Numbered left to right
	 * then top to bottom.
	 * 
	 * @param slot
	 *            The index of the item.
	 * @return The point of the item or new Point(-1, -1) if null.
	 */
	public Point getItemPoint(final int slot) {
		if (slot < 0) {
			throw new IllegalArgumentException("slot < 0 " + slot);
		}
		final RSItem item = getItemAt(slot);
		if (item != null) {
			return item.getComponent().getLocation();
		}
		return new Point(-1, -1);
	}

	/**
	 * Gets all the items in the bank's inventory.
	 * 
	 * @return an <code>RSItem</code> array of the bank's inventory interface.
	 */
	public RSItem[] getItems() {
		if (getInterface() == null
				|| getInterface().getChild(INTERFACE_BANK_INVENTORY) == null) {
			return new RSItem[0];
		}
		final RSInterfaceChild[] components = getInterface().getChild(
				INTERFACE_BANK_INVENTORY).getChildren();
		final RSItem[] items = new RSItem[components.length];
		for (int i = 0; i < items.length; ++i) {
			items[i] = new RSItem(components[i]);
		}
		return items;
	}

	public RSInterfaceChild[] getItemsComponent() {
		if ((getInterface() == null)
				|| (getInterface().getChild(INTERFACE_BANK_INVENTORY) == null)) {
			return new RSInterfaceChild[0];
		}

		return getInterface().getChild(INTERFACE_BANK_INVENTORY).getChildren();
	}

	/**
	 * Gets the array of item stack sizes in the bank.
	 * 
	 * @return The stack sizes array.
	 */
	public int[] getStackSizes() {
		final RSInterfaceChild[] items = getInterface().getChild(
				INTERFACE_BANK_INVENTORY).getChildren();
		if (items != null) {
			final int[] value = new int[items.length];
			for (final RSInterfaceChild item : items) {
				value[item.getChildIndex()] = item.getChildStackSize();
			}
			return value;
		}

		return new int[0];
	}

	/**
	 * Checks whether or not the collection box is open.
	 * 
	 * @return <tt>true</tt> if the collection box interface is open; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isCollectionOpen() {
		return methods.iface.get(INTERFACE_COLLECTION_BOX).isValid();
	}
	
	/**
	 * Checks whether or not the deposit box is open.
	 * 
	 * @return <tt>true</tt> if the deposit box interface is open; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isDepositOpen() {
		return methods.iface.get(INTERFACE_DEPOSIT_BOX).isValid();
	}

	/**
	 * @return <tt>true</tt> if the bank or deposit box interfaces are open; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isOpen() {
		return getInterface().isValid() || methods.iface.get(INTERFACE_DEPOSIT_BOX).isValid();
	}
	
	/**
	 * @return <tt>true</tt> if the bank interface is open; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isBankOpen() {
		return getInterface().isValid();
	}

	/**
	 * @return <tt>true</tt> if currently searching the bank.
	 */
	public boolean isSearchOpen() {
		// Setting 1248 is -2147483648 when search is enabled and -2013265920
		return methods.settings.get(1248) == -2147483648;
	}

	/**
	 * Opens one of the supported banker NPCs, booths, or chests nearby. If they
	 * are not nearby, and they are not null, it will automatically walk to the
	 * closest one.  Waits randomly 2-4 seconds for the bank to open.
	 * 
	 * @return <tt>true</tt> if the bank was opened, otherwise <tt>false</tt>.
	 */
	public boolean open() {
		Bot.debug(logger, "Start of open method.");
		
		if (isOpen()) {
			Bot.debug(logger, "Bank is already open.");
			
			return true;
		}
		
		try {
			if (methods.menu.isOpen()) {
				methods.mouse.moveSlightly();
				methods.wait(methods.random(20, 30));
			}
			
			RSObject bankBooth = methods.objects.getNearFilterName(banks);
			RSNPC banker = methods.npc.getNearFilterName(bankers);
			
			// Chests like the necromancer chest in the lumby dungeon don't seem
			// to be visible by name as a regular object.  So, as a failsafe,
			// if the first try yields a null bankBooth, try getting them by ID.
			if (bankBooth==null)  {
				bankBooth = methods.objects.getNearestByID(nonBankers);
			}
			
			/*
			 * Find closese one, others are set to null. Remember distance and
			 * tile.
			 */
			int lowestDist = Integer.MAX_VALUE;
			int boothDist = Integer.MAX_VALUE;
			int bankerDist = Integer.MAX_VALUE;
			
			RSTile tile = null;
			if (bankBooth != null) {
				tile = bankBooth.getLocation();
				boothDist = bankBooth.distanceTo();
				Bot.debug(logger, String.format("Bank booth available to open: %s", bankBooth.getName()));
			}
			if (banker != null && banker.distanceTo() < lowestDist) {
				tile = banker.getLocation();
				bankerDist = banker.distanceTo();
				Bot.debug(logger, String.format("Banker booth available to open: %s", banker.getName()));
			}
			
			if (banker!=null && bankerDist<boothDist)  {
				bankBooth=null;
				lowestDist=bankerDist;
			}
			else if (bankBooth!=null && boothDist<bankerDist)  {
				banker=null;
				lowestDist=boothDist;
			}
			
			Bot.debug(logger, "Opening closest bank object");

			/* Open closest one, if any found */
			if (tile.isOnScreen()) {
				Bot.debug(logger, String.format("Opening object at distance of %d", lowestDist));
				
				boolean didAction = false; 
				
				if (bankBooth != null) {
					Bot.debug(logger, String.format("Opening bank object: %s", bankBooth.getName()));
					
					// In order to properly handle the different open actions, we must see which
					// ones are there.  Therefore, we have to move the mouse to the target spot
					// and examing the actions.
					Point target = bankBooth.getScreenLocation();
					if (target!=null && !target.equals(new Point(-1,-1)))  {
						methods.mouse.move(target);
						methods.wait(methods.random(150,220));
						
						List<String> menActs = Arrays.asList(methods.menu.getItems());
						String allacts = null;
						
						for (String act: menActs)  {
							allacts = String.format("%s%s",	allacts!=null?allacts+", ":"",act);
						}
						
						Bot.debug(logger, "Moved mouse to determine actions: "+allacts);
					}
					
					String action = "Use ";
					boolean foundAction = false;
					if (methods.menu.contains("Use-quickly", bankBooth.getName()))  {
						action = "Use-quickly";
						foundAction=true;
					}
					else if (methods.menu.contains("Open", "Shantay chest"))  {
						action = "Use-quickly";
						foundAction=true;
					}
					else if (methods.menu.contains("Use Bank chest"))  {
						action = "Use Bank chest";
						foundAction=true;
					}
					else if (methods.menu.contains("Deposit", "Deposit Chest"))  {
						action = "Deposit Deposit chest";
						foundAction=true;
					}
					else if (methods.menu.contains("Bank Chest"))  {
						action = "Bank Chest";
						foundAction=true;
					}
					Bot.debug(logger, String.format("Opening reachable object using action: %s", action));

					if (foundAction)  {
						didAction = bankBooth.action(action);
					}
				} else if (banker != null) {
					Bot.debug(logger, String.format("Opening banker: %s", banker.getName()));
					didAction = banker.action("Bank " + banker.getName());
				}
				
				if (didAction) {
					// Random wait from 2 - 4 seconds for bank to open.
					int count = 0;
					while (!isOpen() && !isCollectionOpen() && ++count < 10) {
						methods.wait(methods.random(200, 400));
						
						if (methods.player.getMine().isMoving()) {
							count = 0;
						}
					}
				} else {
					methods.camera.turnTo(tile);
				}
			} else if (tile!=null && tile.isOnMinimap()) {
				Bot.debug(logger, "Walking to bank object");
				methods.walk.tileMM(tile);
			}
			
			// If the collection box got opened, close it and return.
			if (isCollectionOpen())  {
				methods.iface.clickChild(INTERFACE_COLLECTION_BOX, INTERFACE_COLLECTION_BOX_CLOSE);
			}
			
			// If the bank did open, add a small delay before returning,
			// otherwise return immediately.
			if (isOpen())  {
				methods.wait(methods.random(350,550));
				return true;
			}
			
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens one of the supported deposit boxes nearby. If they are not nearby,
	 * and they are not null, it will automatically walk to the closest one.
	 * Waits randomly 2-4 seconds for the deposit box to be open.
	 * 
	 * @return <tt>true</tt> if the deposit box was opened, otherwise <tt>false</tt>.
	 */
	public boolean openDepositBox() {
		try {
			if (!isDepositOpen()) {
				if (methods.menu.isOpen()) {
					methods.mouse.moveSlightly();
					methods.wait(methods.random(20, 30));
				}
				
				final RSObject depositBox = methods.objects.getNearestByID(BankDepositBox);
				
				if (depositBox != null
						&& depositBox.isOnScreen()) {
					if (depositBox.action("Deposit")) {
						int count = 0;
						while (!isDepositOpen() && ++count < 10) {
							methods.wait(methods.random(200, 400));

							if (methods.player.getMine().isMoving()) {
								count = 0;
							}
						}
					} else {
						methods.camera.turnTo(depositBox, 20);
					}
				} else {
					if (depositBox!=null) {
						methods.walk.to(depositBox.getLocation());
					}
				}
			}
			
			return isDepositOpen();
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Opens the bank tab.
	 * 
	 * @param tabNumber
	 *            The tab number - e.g. view all is 1.
	 * @return <tt>true</tt> on success.
	 * @since 6 March 2009.
	 */
	public boolean openTab(final int tabNumber) {
		return isBankOpen()
		&& methods.iface.clickChild(INTERFACE_BANK,
				INTERFACE_BANK_TAB[tabNumber - 1]);
	}

	/**
	 * Searches for an item in the bank. Returns true if succeeded (does not
	 * necessarily mean it was found).
	 * 
	 * @param itemName
	 *            The item name to find.
	 * @return <tt>true</tt> on success.
	 */
	public boolean searchItem(final String itemName) {
		if (!isBankOpen()) {
			return false;
		}

		methods.iface.clickChild(INTERFACE_BANK, INTERFACE_BANK_BUTTON_SEARCH,
		"Search");
		methods.wait(methods.random(1000, 2000));

		if (isBankOpen() && methods.iface.get(INTERFACE_BANK_SEARCH).isValid()) {
			Bot.getInputManager().sendKeys(itemName, false);
			methods.wait(methods.random(300, 700));
			return true;
		}
		return false;
	}

	/**
	 * Sets the bank rearrange mode to insert.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setRearrangeModeToInsert() {
		if (!isBankOpen()) {
			return false;
		}
		if (methods.settings.get(SETTING_BANK_TOGGLE_REARRANGE_MODE) != 1) {
			methods.iface.clickChild(INTERFACE_BANK, INTERFACE_BANK_BUTTON_INSERT);
			methods.wait(methods.random(500, 700));
		}
		return methods.settings.get(SETTING_BANK_TOGGLE_REARRANGE_MODE) == 1;
	}

	/**
	 * Sets the bank rearrange mode to swap.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setRearrangeModeToSwap() {
		if (!isBankOpen()) {
			return false;
		}
		if (methods.settings.get(SETTING_BANK_TOGGLE_REARRANGE_MODE) != 0) {
			methods.iface.clickChild(INTERFACE_BANK, INTERFACE_BANK_BUTTON_SWAP);
			methods.wait(methods.random(500, 700));
		}
		return methods.settings.get(SETTING_BANK_TOGGLE_REARRANGE_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to item.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setWithdrawModeToItem() {
		if (!isBankOpen()) {
			return false;
		}
		if (methods.settings.get(SETTING_BANK_TOGGLE_WITHDRAW_MODE) != 0) {
			methods.iface.clickChild(INTERFACE_BANK, INTERFACE_BANK_BUTTON_ITEM);
			methods.wait(methods.random(500, 700));
		}
		return methods.settings.get(SETTING_BANK_TOGGLE_WITHDRAW_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to note.
	 * 
	 * @return <tt>true</tt> on success.
	 */
	public boolean setWithdrawModeToNote() {
		if (!isBankOpen()) {
			return false;
		}
		if (methods.settings.get(SETTING_BANK_TOGGLE_WITHDRAW_MODE) != 1) {
			methods.iface.clickChild(INTERFACE_BANK, INTERFACE_BANK_BUTTON_NOTE);
			methods.wait(methods.random(500, 700));
		}
		return methods.settings.get(SETTING_BANK_TOGGLE_WITHDRAW_MODE) == 1;
	}

	/**
	 * @param item
	 *            the id of the item to wait for
	 * @param count
	 *            the amount of item to wait for
	 * @param timeout
	 *            the maximum time in milli seconds to wait for
	 * @return the count of the item in the bank.
	 */
	public int waitForBankCount(final int item, final int count, final int timeout) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeout) {
			if (getCount(item) >= count) {
				break;
			}
		}
		return getCount(item);
	}

	/**
	 * Tries to withdraw an item. 0 is All. -1 is All but one, 1, 5, 10 use
	 * Withdraw 1, 5, 10 while other numbers Withdraw X.
	 * 
	 * @param itemID
	 *            The ID of the item.
	 * @param count
	 *            The number to withdraw.
	 * @return <tt>true</tt> on success.
	 */
	public boolean withdraw(final int itemID, final int count) {
		if (!isOpen()) {
			return false;
		}
		
		if (count < -1) {
			throw new IllegalArgumentException("count (" + count + ") < -1");
		}
		
		final RSItem rsi = getItem(itemID);
		if (rsi == null || rsi.getID() == -1) {
			return false;
		}
		
		final RSInterfaceChild item = rsi.getComponent();
		if (item == null) {
			return false;
		}
		
		Bot.debug(logger, String.format("item relativeX=%d relativeY=%d, current bank tab=%d", 
				item.getRelativeX(), item.getRelativeY(), methods.bank.getCurrentTab()));
		
		//TODO: We can determine what tab an item is on from it's relative index in
		// The bank interface's child index 93 folder.  Use this to determine which tab
		// to open.
		
		// The relative X of the item will normally never be zero.  But if it is
		// and the current tab is not zero, open tab 0.
		int t = 0;
		while (item.getRelativeX() == 0 && methods.bank.getCurrentTab() != 0
				&& t < 5) {
			if (methods.iface.getChild(Bank.INTERFACE_BANK,
					Bank.INTERFACE_BANK_TAB[0]).click()) {
				methods.sleep(methods.random(800, 1300));
			}
			t++;
		}
		
		if (!methods.iface.scrollTo(item, (Bank.INTERFACE_BANK << 16)
				+ Bank.INTERFACE_BANK_SCROLLBAR)) {
			return false;
		}
		
		final int invCount = methods.inventory.getCount(itemID);
		item.click(count == 1 ? true : false);
		
		final String defaultAction = "Withdraw-" + count;
		String action = null;
		
		switch (count) {
		case 0:
			action = "Withdraw-All";
			break;
		case -1:
			action = "Withdraw-All but one";
			break;
		case 1:
			break;
		case 5:
			action = defaultAction;
			break;
		case 10:
			action = defaultAction;
			break;
		default:
			int i = -1;
			try {
				i = Integer.parseInt(item.getActions()[3].toLowerCase().trim()
						.replaceAll("\\D", ""));
			} catch (final Exception e) {
				e.printStackTrace();
			}
		
			if (i == count) {
				action = defaultAction;
			} else if (item.action("Withdraw-X")) {
				methods.sleep(methods.random(1000, 1300));
				methods.keyboard.sendText(String.valueOf(count), true);
			}
		}
		
		if (action != null && item.action(action)) {
			methods.inventory.waitForCountGreater(invCount, 3000);
		}
		
		int newinvct = methods.inventory.getCount(itemID);
		Bot.debug(logger, String.format("Inventory count after withdraw: %d, before: %d",
				newinvct, invCount));
		return newinvct > invCount;
	}
	
	/**
	 * A flag that scripts can check to determine if banking advises that the
	 * script be stopped.
	 * 
	 * Set to <tt>true</tt> when the "no room in the bank" message is issued.
	 */
	public boolean stopScript = false;
	
	@Override
	public void messageReceived(MessageEvent e) {
		String msg = e.getMessage();
		
		if (msg.contains("no room"))  {
			logger.warning("Bank is full, cannot deposit the requested items.  stopScript flag set to true.");
			stopScript = true;
		}
		
	}

	/**
	 * Determines if a bank, bank booth, bank chest or deposit box is 
	 * on the screen. 
	 * 
	 * @return <tt>true</tt> If a bank is on the screen, <tt>false</tt> otherwise.
	 */
	public boolean nearby()  {
		RSObject bankBooth = methods.objects.getNearFilterName(banks);
		RSNPC banker = methods.npc.getNearFilterName(bankers);
		
		// Chests like the necromancer chest in the lumby dungeon don't seem
		// to be visible by name as a regular object.  So, as a failsafe,
		// if the first try yields a null bankBooth, try getting them by ID.
		if (bankBooth==null)  {
			bankBooth = methods.objects.getNearestByID(nonBankers);
		}
		
		return (banker!=null && banker.isOnScreen()) || 
			   (bankBooth!=null && bankBooth.isOnScreen());		
	}
}
