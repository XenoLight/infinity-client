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
import java.util.List;

import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.internal.Deque;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSTile;

/**
 * items on the ground of the game.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class GroundItems {

	private final Methods methods;
	public static final Filter<RSGroundItem> filter = new Filter<RSGroundItem>() {

		@Override
		public boolean accept(final RSGroundItem item) {
			return true;
		}
	};

	public GroundItems() {
		this.methods = Bot.methods;
	}


	/**
	 * Returns the first (but not the closest) item found in a square within
	 * (range) away from you.
	 * 
	 * @param amount
	 *            The maximum distance.
	 * @return The first ground item found; or null if none were found.
	 */
	public RSGroundItem getItem(final int amount) {
		final int pX = methods.player.getMine().getLocation().getX();
		final int pY = methods.player.getMine().getLocation().getY();
		final int minX = pX - amount;
		final int minY = pY - amount;
		final int maxX = pX + amount;
		final int maxY = pY + amount;
		
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				final List<RSGroundItem> items = getItemsAt(x, y);
				if (items.size() > 0) {
					return items.get(0);
				}
			}
		}
		return null;
	}

	public RSGroundItem[] getItemArray(final int amount)  {
		return (RSGroundItem[]) getItemList(amount).toArray();
	}
	
	
	/**
	 * Returns the RSItemTile array.
	 * 
	 * @deprecated Change to @see getItemList() and RSGroundItem instances and methods.
	 */
	@Deprecated
	public RSItemTile[] getItemTileArray(final int amount)  {
		List<RSGroundItem> list = getItemList(amount);
		RSItemTile[] array = new RSItemTile[amount];
		
		int i=0;
		for (RSGroundItem item : list)  {
			array[i] = item.getItemTile();
			i++;
		}
		
		return array;
	}
	
	public List<RSGroundItem> getItemList(final int amount) {
		final List<RSGroundItem> temp = new ArrayList<RSGroundItem>();
		final int pX = methods.player.getMyLocation().getX();
		final int pY = methods.player.getMyLocation().getY();
		final int minX = pX - amount;
		final int minY = pY - amount;
		final int maxX = pX + amount;
		final int maxY = pY + amount;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				final List<RSGroundItem> items = getItemsAt(x, y);
				if (items.size() > 0) {
					temp.add(items.get(0));
				}
			}
		}
		if (temp.isEmpty()) {
			return null;
		}
		
		final List<RSGroundItem> list = new ArrayList<RSGroundItem>();
		for (int i = 0; i < temp.size(); i++) {
			list.add(i, temp.get(i));
		}
		return list;
	}

	/**
	 * Returns the first (but not the closest) item with a specified id in the
	 * playable(visible) area.
	 * 
	 * @param id
	 *            The ID of the item to look for.
	 * @return The first matching ground item found; or null if none were found.
	 */
	public RSGroundItem getItemByID(final int id) {
		return getItemByID(52, new int[] { id });
	}

	/**
	 * Returns the first (but not the closest) item with a specified id found in
	 * a square within (range) away from you.
	 * 
	 * @param amount
	 *            The maximum distance in tiles.
	 * @param id
	 *            The ID of the item to look for.
	 * @return The first matching ground item found; or null if none were found.
	 */
	public RSGroundItem getItemByID(final int amount, final int id) {
		return getItemByID(amount, new int[] { id });
	}

	/**
	 * Returns the first (but not the closest) item with any of the specified
	 * IDs in a square within (range) away from you.
	 * 
	 * @param amount
	 *            The maximum distance.
	 * @param ids
	 *            The IDs of the items to look for.
	 * @return The first matching ground item found; or null if none were found.
	 */
	public RSGroundItem getItemByID(final int amount, final int[] ids) {
		final int pX = methods.player.getMine().getLocation().getX();
		final int pY = methods.player.getMine().getLocation().getY();
		final int minX = pX - amount;
		final int minY = pY - amount;
		final int maxX = pX + amount;
		final int maxY = pY + amount;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				final List<RSGroundItem> items = getItemsAt(x, y);
				for (final RSGroundItem item : items) {
					final int iId = item.getItem().getID();
					for (final int id : ids) {
						if (iId == id) {
							return item;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the first (but not the closest) item with a specified id in the
	 * playable(visible) area.
	 * 
	 * @param ids
	 *            The IDs of the items to look for.
	 * @return The first matching ground item found; or null if none.
	 */
	public RSGroundItem getItemByID(final int[] ids) {
		return getItemByID(52, ids);
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 * 
	 * @param x
	 *            The x position of the tile in the world.
	 * @param y
	 *            The y position of the tile in the world.
	 * @return An array of the ground items on the specified tile. 
	 *
	 * @deprecated Change to @see getItemsAt(int x, int y) and RSGroundItem instances and methods.
	 */
	@Deprecated
	public RSItemTile[] getItemTilesAt(final int x, final int y) {
		List<RSGroundItem> items = getItemsAt(x,y);
		RSItemTile[] itemtiles = new RSItemTile[items.size()];
		
		int i=0;
		for(RSGroundItem item : items)  {
			itemtiles[i] = item.getItemTile();
			i++;
		}
		
		return itemtiles;
	}
	
	/**
	 * Returns all the ground items at a tile on the current plane.
	 * 
	 * @param tile
	 *            The tile in the world.
	 * @param y
	 *            The y position of the tile in the world.
	 * @return An array of the ground items on the specified tile. 
	 *
	 * @deprecated Change to @see getItemsAt(RSTile tile) and RSGroundItem instances and methods.
	 */
	@Deprecated
	public RSItemTile[] getItemTilesAt(final RSTile tile) {
		return getItemTilesAt(tile.getX(), tile.getY());
	}
	
	/**
	 * Returns all the ground items at a tile on the current plane.
	 * 
	 * @param t
	 *            The tile.
	 * @return An array of the ground items on the specified tile.
	 */
	public List<RSGroundItem> getItemsAt(final RSTile t) {
		return getItemsAt(t.getX(), t.getY());
	}
	
	/**
	 * Returns all the ground items at a tile on the current plane.
	 * 
	 * @param x
	 *            The x position of the tile in the world.
	 * @param y
	 *            The y position of the tile in the world.
	 * @return An array of the ground items on the specified tile.
	 */
	public List<RSGroundItem> getItemsAt(final int x, final int y) {
		final List<RSGroundItem> list = new ArrayList<RSGroundItem>();

		if (!methods.game.isLoggedIn()) {
			return list;
		}

		
		final org.rsbot.client.HashTable itemNC = 
				methods.game.client().getRSItemHashTable();
		final int id = x | y << 14 | methods.game.client().getPlane() << 28;

		final org.rsbot.client.NodeListCache itemNLC = 
			(org.rsbot.client.NodeListCache) Calculations.findNodeByID(itemNC, id);

		if (itemNLC == null) {
			return list;
		}
		
		@SuppressWarnings("rawtypes")
		final Deque itemNL = new Deque(itemNLC.getNodeList());
		for (org.rsbot.client.RSItem item = (org.rsbot.client.RSItem) itemNL
				.getHead(); item != null; item = (org.rsbot.client.RSItem) itemNL
				.getNext()) {
			list.add(new RSGroundItem(x, y, new RSItem(item)));
		}

		return list;
	}

	/**
	 * Returns an RSItemTile representing the nearest item on the ground with an
	 * ID that matches any of the IDS provided. Can return null. RSItemTile is a
	 * subclass of RSTile.
	 * 
	 * @param ids
	 *            The IDs to look for.
	 * @return RSItemTile of the nearest item with the an ID that matches any in
	 *         the array of IDs provided; or null if no matching ground items
	 *         were found.
	 */
	public RSGroundItem getNearestItemByID(final int... ids) {
		int dist = 9999999;
		final int pX = methods.player.getMine().getLocation().getX();
		final int pY = methods.player.getMine().getLocation().getY();
		final int minX = pX - 52;
		final int minY = pY - 52;
		final int maxX = pX + 52;
		final int maxY = pY + 52;
		RSGroundItem itm = null;
		
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				final List<RSGroundItem> items = getItemsAt(x, y);
				for (final RSGroundItem item : items) {
					final int iId = item.getItem().getID();
					for (final int id : ids) {
						if (iId == id
								&& item.distanceTo() < dist) {
							dist = item.distanceTo();
							itm = item;
						}
					}
				}
			}
		}
		return itm;
	}

	/**
	 * Searches for an item on the ground within the specified area. New
	 * getNearestItemByID
	 * 
	 * @param search
	 *            The area to search in for ground items
	 * @param ids
	 *            The items to search for by ID
	 * @return
	 * @author RSHelper
	 */
	public RSGroundItem getNearestItemInAreaByID(final RSArea search, final int... ids) {
		int dist = 9999999;
		final RSTile[][] t = search.getTiles();
		RSGroundItem itm = null;
		
		for (final RSTile[] element : t) {
			for (int y = 0; y < element.length; y++) {
				final List<RSGroundItem> items = getItemsAt(element[y]);
				for (final RSGroundItem item : items) {
					final int iId = item.getItem().getID();
					for (final int id : ids) {
						if (iId == id
								&& item.distanceTo() < dist) {
							dist = item.distanceTo();
							itm = item;
						}
					}
				}
			}
		}
		return itm;
	}

}
