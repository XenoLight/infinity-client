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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;

/**
 * Obtains information on tradeable items from the Grand Exchange website.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class GE {

	public static class GEItem {

		private final String name;
		private final String description;
		private final int id;
		private final int price;
		private final double last30;
		private final double last90;
		private final double last180;

		GEItem(final String name, final String descript, final int id, final double[] values, final int gp) {
			this.name = name;
			this.description = descript;
			this.id = id;
			this.price = gp;
			last30 = values[0];
			last90 = values[1];
			last180 = values[2];
		}

		/**
		 * Gets the description of this item.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Gets the ID of this item.
		 */
		public int getID() {
			return id;
		}

		/**
		 * Gets the change in price for the last 180 days of this item.
		 */
		public double getLast180Days() {
			return last180;
		}

		/**
		 * Gets the change in price for the last 30 days of this item.
		 */
		public double getLast30Days() {
			return last30;
		}

		/**
		 * Gets the change in price for the last 90 days of this item.
		 */
		public double getLast90Days() {
			return last90;
		}

		/**
		 * Gets the name of this item.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the set market price of this item.
		 */
		public int getPrice() {
			return price;
		}
	}
	private final Methods methods;
	private static final String host = "http://services.runescape.com";
	private static final String get = "/m=itemdb_rs/viewitem.ws?obj=";
	public static final int ifaceGEWindow = 105;
	public static final int ifaceGESellInvitory = 107;
	public static final int ifacesearchbox = 389;
	public static final int[] sellButton = { 29, 45, 61, 77, 93, 109 };
	public static final int[] buyBotton = { 30, 46, 62, 78, 94, 110 };
	public static final int[] offerBoxes = { 19, 35, 51, 67, 83, 99 };
	public static final int GECollectionBox1 = 209;
	public static final int GECollectionBox2 = 211;
	public static final int[] teller = { 6528, 6529 };
	private GEItem last = null;

	private static final Pattern pattern = Pattern
	.compile("(?i)<td><img src=\".+obj_sprite\\.gif\\?id=(\\d+)\" alt=\"(.+)\"");

	public GE() {
		this.methods = Bot.methods;
	}

	/**
	 * Will check a slot for to see if an item has completed.
	 * 
	 * @param slot
	 *            The slot to check.
	 * @return <tt>true</tt> if Complete, otherwise <tt>false</tt>
	 */
	public boolean checkCompleted(final int slot) {
		final int itemSlot = offerBoxes[slot];
		if (!checkSlotIsEmpty(slot)) {
			if (slot != 0) {
				if (methods.iface.clickChild(ifaceGEWindow, itemSlot, "Abort Offer"))  {
					return false;
				} else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks GE slot and returns ID
	 * 
	 * @param slot
	 *            The slot to check
	 * @return The item name as a string equal to the item being sold or brought
	 *         Will return null if no items are being sold.
	 */
	public String checkSlot(final int slot) {
		try {
			final int slotComponent = offerBoxes[slot];
			if (isOpen()) {
				if (methods.iface.getChild(ifaceGEWindow, slotComponent)
						.getChild(10).isValid()) {
					return methods.iface.getChild(ifaceGEWindow, slotComponent)
							.getChild(10).getText();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Checks GE slots for an any activity (1-6)
	 * 
	 * @param slot
	 *            An int for the corresponding slot.
	 * @return <tt>True</tt> if the slot is free from activity.
	 */
	public boolean checkSlotIsEmpty(final int slot) {
		try {
			final int slotComponent = offerBoxes[slot];
			if (isOpen()) {
				if (methods.iface.getChild(ifaceGEWindow,slotComponent)
						.getChild(10).containsText("Empty")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Checks GE slots for an item.
	 * 
	 * @param name
	 *            The name of the item to check for.
	 * @return An int of the corresponding slot. 0 = Not found.
	 */
	public int findItem(final String name) {
		for (int i = 1; i <= 6; i++) {
			if (isOpen()) {
				if (checkSlotIsEmpty(i)) {
					final int slotComponent = offerBoxes[i];
					final String s = 
							methods.iface.getChild(ifaceGEWindow,slotComponent)
							.getChild(18).getText();
					if (s.equals(name)) {
						return i;
					}
				}
			}
		}
		return 0;
	}

	/**
	 * Finds first empty slot.
	 * 
	 * @return An int of the corresponding slot. 0 = No empty slots.
	 */
	public int freeSlot() {
		for (int i = 1; i <= 6; i++) {
			if (checkSlotIsEmpty(i)) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Gets any items that may be in the offer. TODO; Add a collect from bank.
	 * 
	 * @param slot
	 *            An int for the corresponding slot, of which to check
	 */
	public void getItem(final int slot) {
		final int itemslot = offerBoxes[slot];
		if (isOpen()) {
			open();
		}
		if (isOpen()) {
			if (methods.iface.getChild(ifaceGEWindow,itemslot)
					.containsAction("Veiw Offer"))  {
				methods.sleep(methods.random(700, 1200));
				
				if (methods.iface.getChild(ifaceGEWindow,GECollectionBox2)
						.containsAction("Collect")) {
					methods.iface.getChild(ifaceGEWindow,GECollectionBox2)
					.action("Collect");
					methods.sleep(methods.random(400, 900));
				}
				
				if (methods.iface.getChild(ifaceGEWindow,GECollectionBox1)
						.containsAction("Collect")) {
					methods.iface.getChild(ifaceGEWindow,GECollectionBox1)
					.action("Collect");
					methods.sleep(methods.random(400, 900));
				}
			}
		}
	}

	/**
	 * Gets the ID of the given item name. Should not be used.
	 * 
	 * @param itemName
	 *            The name of the item to look for.
	 * @return The ID of the given item name or -1 if unavailable.
	 * @see GE#lookup(java.lang.String)
	 */
	public int getItemID(final String itemName) {
		final GEItem geItem = lookup(itemName);
		if (geItem != null) {
			return geItem.getID();
		}
		return -1;
	}

	/**
	 * Gets the name of the given item ID. Should not be used.
	 * 
	 * @see GE#lookup(int)
	 */
	public String getItemName(final int itemID) {
		final GEItem geItem = loadItemInfo(itemID);
		if (geItem != null) {
			return geItem.getName();
		}
		return "";
	}

	/**
	 * Checks if GE is open.
	 * 
	 * @return True if it's open, otherwise false.
	 */
	public boolean isOpen() {
		return methods.iface.get(ifaceGEWindow).isValid();
	}

	/**
	 * Collects data for a given item ID from the Grand Exchange website.
	 * 
	 * @param itemID
	 *            The item ID.
	 * @return An instance of GrandExchange.GEItem; <code>null</code> if unable
	 *         to fetch data.
	 */
	public GEItem loadItemInfo(final int itemID) {
		try {
			if (last != null && last.getID() == itemID) {
				return last;
			}
			final URL url = new URL(host + get + itemID);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String input;
			boolean exists = false;
			int i = 0;
			final double[] values = new double[3];
			int guide = 0;
			String name = "", examine = "", line = "";
			while ((input = br.readLine()) != null) {
				if (input.contains("<div class=\"brown_box main_ge_page")
						&& !exists) {
					if (!input.contains("vertically_spaced")) {
						return null;
					}
					exists = true;
					br.readLine();
					br.readLine();
					name = br.readLine();
				} else if (input.contains("<img id=\"item_image\" src=\"")) {
					examine = br.readLine();
				} else if (input.contains(" Days:</b> <span class=")) {
					final int start = (input.indexOf(" Days:</b> <span class=") + 7);
					final int end = input.indexOf("</span>", start);
					values[i] = parse(input.substring(start, end));
					i++;
				} else if (input.contains("<b>Current guide price:</b>")) {
					line = input.replace("<b>Current guide price:</b>", "");
					guide = (int) parse(line);
				} else if (input.matches("<div id=\"legend\">")) {
					break;
				}
			}
			last = new GEItem(name, examine, itemID, values, guide);
			return new GEItem(name, examine, itemID, values, guide);
		} catch (final IOException ignore) {
		}
		return null;
	}

	/**
	 * Collects data for a given item name from the Grand Exchange website.
	 * 
	 * @param itemName
	 *            The name of the item.
	 * @return An instance of GrandExchange.GEItem; <code>null</code> if unable
	 *         to fetch data.
	 */
	public GEItem lookup(final String itemName) {
		try {
			final URL url = new URL(host + "/m=itemdb_rs/results.ws?query="
					+ itemName + "&price=all&members=");
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String input;
			while ((input = br.readLine()) != null) {
				if (input.contains("<div id=\"search_results_text\">")) {
					input = br.readLine();
					if (input.contains("Your search for")) {
						return null;
					}
				} else if (input.startsWith("<td><img src=")) {
					final Matcher matcher = pattern.matcher(input);
					if (matcher.find()) {
						if (matcher.group(2).contains(itemName)) {
							return loadItemInfo(Integer.parseInt(matcher
									.group(1)));
						}
					}
				}
			}
		} catch (final IOException ignored) {
		}
		return null;
	}

	/**
	 * Opens GE window.
	 * 
	 * @return True if it's open, otherwise false.
	 */
	public boolean open() {
		if (!methods.iface.get(ifaceGEWindow).isValid()) {
			methods.npc.getNearestByID(teller).action("Exchange");
		}
		return methods.iface.get(ifaceGEWindow).isValid();
	}

	private double parse(String str) {
		if (str != null && !str.isEmpty()) {
			str = stripFormatting(str);
			str = str.substring(str.indexOf(58) + 2, str.length());
			str = str.replace(",", "");
			if (!str.endsWith("%")) {
				if (!str.endsWith("k") && !str.endsWith("m")) {
					return Double.parseDouble(str);
				}
				return Double.parseDouble(str.substring(0, str.length() - 1))
				* (str.endsWith("m") ? 1000000 : 1000);
			}
			final int k = str.startsWith("+") ? 1 : -1;
			str = str.substring(1);
			return Double.parseDouble(str.substring(0, str.length() - 1)) * k;
		}
		return -1D;
	}

	private String stripFormatting(final String str) {
		if (str != null && !str.isEmpty()) {
			return str.replaceAll("(^[^<]+>|<[^>]+>|<[^>]+$)", "");
		}
		return "";
	}
}
