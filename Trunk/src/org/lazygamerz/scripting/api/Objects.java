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
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.client.RSAnimableNode;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSObjectDef;
import org.rsbot.script.wrappers.RSTile;

/**
 * In game objects methods.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class Objects {
	private Logger logger = Logger.getLogger(Bot.class.getPackage()
			.getName());
	

	private final Methods methods;
	public static final int interactable = 1;
	public static final int floorDecoration = 2;
	public static final int boundary = 4;
	public static final int wallDecoration = 8;

	public Objects() {
		this.methods = Bot.methods;
	}

	/**
	 * 
	 * @param object
	 * @param action
	 * @return
	 */
	public boolean at(final RSObject object, final String action) {
		if (object!=null)  {
			return object.action(action, null);
		}
		else  {
			return false;
		}
	}

	/**
	 * 
	 * @param object
	 * @param action
	 * @return
	 */
	public boolean at(final RSObject object, final String action, final String option) {
		return object.action(action, option);
	}

	/**
	 * @deprecated use RSObject.action instead.
	 */
	@Deprecated
	public boolean atDoor(final int id, final char direction) {
		final RSObject theDoor = getNearestByID(id);
		if (theDoor == null) {
			return false;
		}
		
		final RSTile location = theDoor.getLocation();
		int x = location.getX(), y = location.getY();
		boolean fail = false;
		
		switch (direction) {
		case 'N':
		case 'n':
			y++;
			break;
		case 'W':
		case 'w':
			x--;
			break;
		case 'E':
		case 'e':
			x++;
			break;
		case 'S':
		case 's':
			y--;
			break;
		default:
			fail = true;
		}
		if (fail) {
			throw new IllegalArgumentException();
		}
		
		return theDoor.click();
	}

	/**
	 * 
	 * @param tree
	 * @param action
	 * @return
	 *
	 * @deprecated use RSObject.action instead.
	 */
	@Deprecated
	public boolean atTree(final RSObject tree, final String action) {
		final RSTile loc1 = tree.getLocation();
		final RSTile loc4 = new RSTile(loc1.getX() + 1, loc1.getY() + 1);
		final Point sloc1 = Calculations.tileToScreen(loc1.getX(), loc1.getY(),
				10);
		final Point sloc2 = Calculations.tileToScreen(loc4.getX(), loc4.getY(),
				10);
		final Point screenLoc = new Point((sloc1.x + sloc2.x) / 2,
				(sloc1.y + sloc2.y) / 2);
		if (screenLoc.x == -1 || screenLoc.y == -1)
			return false;
		methods.mouse.move(screenLoc, 3, 3);
		return methods.menu.action(action);
	}

	public boolean canReach(final Object obj, final boolean isObject) {
		if (obj instanceof RSCharacter)
			return Calculations.canReach(((RSCharacter) obj).getLocation(),
					isObject);
		else if (obj instanceof RSTile)
			return Calculations.canReach((RSTile) obj, isObject);
		else if (obj instanceof RSObject)
			return Calculations.canReach(((RSObject) obj).getLocation(),
					isObject);
		else if (obj instanceof Point)
			return Calculations.canReach(new RSTile(((Point) obj).x,
					((Point) obj).y), isObject);
		return false; /* Couldn't recognize object */
	}

	/**
	 * Searches the RS game screen for the object by checking the menu list
	 * Clicks object once found
	 * 
	 * @param obj
	 *            The RSObject you want to click.
	 * @param action
	 *            Action command to use on the object.
	 * @return true if the object was clicked; otherwise <tt>false</tt>.
	 */
	public boolean click(final RSObject obj, final String action) {
		return click(obj, action, null);
	}

	/**
	 * Searches the RS game screen for the object by checking the menu list.
	 * Performs the provided action on the object once found.
	 * 
	 * @param obj
	 *            The RSOject you want to click.
	 * @param action
	 *            Action command to use on the object.
	 * @param name
	 *            The name of the object.
	 * @return true if the object was clicked; otherwise false.
	 */
	public boolean click(final RSObject obj, final String action,
			final String name) {
		int a;
		final String fullCommand = action + " "	+ (name == null ? obj.getName() : name);
		
		for (a = 10; a-- >= 0;) {
			final String[] menuItems = methods.menu.getItems();
			
			if (menuItems.length > 1) {
				if (methods.menu.arrayContains(menuItems, fullCommand)) {
					if (menuItems[0].contains(fullCommand)) {
						methods.mouse.click(true);
						return true;
					} else
						return methods.menu.action(fullCommand);
				}
			}
			
			if (!obj.isOnScreen())  {
				return false;
			}
			
			methods.mouse.move(obj.getPointLocation());
		}
		
		return false;
	}

	/**
	 * Returns the <tt>RSObject</tt> on the specified x,y cords.
	 * 
	 * @param x
	 *            The x cords on which to search.
	 * @param y
	 *            The y cords on which to search.
	 * @return The RSObject on the provided x,y cords; or null if none found.
	 */
	public RSObject[] getAt(final int x, final int y) {
		final org.rsbot.client.Client client = methods.game.client();
		final ArrayList<RSObject> object = new ArrayList<RSObject>();
		if (client.getRSGroundArray() == null)
			return null;

		try {
			final org.rsbot.client.RSGround rsGround = 
				client.getRSGroundArray()[client.getPlane()]
										 [x - client.getBaseX()][y - client.getBaseY()];

			if (rsGround != null) {
				org.rsbot.client.RSObject rsObj;
				org.rsbot.client.RSInteractable obj;

				/* Interactable objects (trees etc!) */
				if ((interactable) != 0) {
					for (RSAnimableNode node = rsGround.getRSAnimableList(); node != null; node = node
					.getNext()) {
						obj = node.getRSAnimable();
						if (obj != null
								&& obj instanceof org.rsbot.client.RSObject) {
							rsObj = (org.rsbot.client.RSObject) obj;
							if (rsObj.getID() != -1) {
								object.add(new RSObject(rsObj, x, y, 0));
							}
						}
					}
				}
				/* Ground Decorations */
				if ((floorDecoration) != 0) {
					obj = rsGround.getFloorDecoration();
					if (obj != null) {
						rsObj = (org.rsbot.client.RSObject) obj;
						if (rsObj.getID() != -1) {
							object.add(new RSObject(rsObj, x, y, 1));
						}
					}
				}
				/* Boundaries / Doors / Fences / Walls */
				if ((boundary) != 0) {
					obj = rsGround.getBoundary1();
					if (obj != null) {
						rsObj = (org.rsbot.client.RSObject) obj;
						if (rsObj.getID() != -1) {
							object.add(new RSObject(rsObj, x, y, 2));
						}
					}

					obj = rsGround.getBoundary2();
					if (obj != null) {
						rsObj = (org.rsbot.client.RSObject) obj;
						if (rsObj.getID() != -1) {
							object.add(new RSObject(rsObj, x, y, 2));
						}
					}
				}
				/* Wall Decorations */
				if ((wallDecoration) != 0) {
					obj = rsGround.getWallDecoration1();
					if (obj != null) {
						rsObj = (org.rsbot.client.RSObject) obj;
						if (rsObj.getID() != -1) {
							object.add(new RSObject(rsObj, x, y, 3));
						}
					}

					obj = rsGround.getWallDecoration2();
					if (obj != null) {
						rsObj = (org.rsbot.client.RSObject) obj;
						if (rsObj.getID() != -1) {
							object.add(new RSObject(rsObj, x, y, 3));
						}
					}
				}
			}
		} catch (final Exception ignored) {
		}
		if (object.size() < 1)
			return new RSObject[0];
		else
			return object.toArray(new RSObject[object.size()]);
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public RSObject[] getAt(final RSTile t) {
		if (t == null)
			return null;
		else
			return getAt(t.getX(), t.getY());
	}

	/**
	 * Returns the <tt>RSObject</tt> that is nearest out of all objects that are
	 * accepted by the provided Filter.
	 * 
	 * @param filter
	 *            Filters out unwanted objects.
	 * @return An <tt>RSObject</tt> representing the nearest object that was
	 *         accepted by the filter; or null if there are no matching objects
	 *         in the current region.
	 */
	public RSObject getNearByFilter(final Filter<RSObject> filter) {
		RSObject cur = null;
		RSTile loc = methods.player.getMyLocation();
		int px = loc.getX();
		int py = loc.getY();
		
		double dist = -1;
		for (int x = -52; x < 52; x++) {
			for (int y = -52; y < 52; y++) {

				final RSObject[] objs = 
					getAt(px + x,  py + y );
				
				if (objs==null)  {
					continue;
				}
				
				for (final RSObject o : objs) {
					if (o != null && filter.accept(o)) {
						final double distTmp = Calculations.distanceBetween(
								methods.player.getMine().getLocation(),
								o.getLocation());
						if (cur == null) {
							dist = distTmp;
							cur = o;
						} else if (distTmp < dist) {
							cur = o;
							dist = distTmp;
						}
						break;
					}
				}
			}
		}
		return cur;
	}

	/**
	 * Returns the nearest RSObject having one of the specified IDs and contained
	 * within the specified RSArea.
	 * 
	 * @param area
	 * 			The area in which any returned object must reside.
	 * @param ids
	 *          IDs of the objects for which to search.
	 *         
	 * @return <tt>RSObject</tt> the nearest RSObject having one of the 
	 *  		specified IDs and residing in the specified RSArea.
	 */
	public RSObject getNearestByIdInArea(final RSArea area, final int...ids)  {
		RSObject cur = null;
		double dist = -1;
		
		RSTile[][] areaTiles = area.getTiles();
		
		int xx = areaTiles.length;
		for (int x = 0; x < xx; x++) {
			
			int yy = areaTiles[x].length;
			for (int y = 0; y < yy; y++) {
				Bot.debug(logger, "Processing objects at "+ areaTiles[x][y].toString());
				final RSObject[] oo = getAt(areaTiles[x][y]);
				
				if (oo != null) {
					for (final RSObject o : oo) {
						if (o != null) {
							boolean isObject = false;
				
							for (final int id : ids) {
								Bot.debug(logger, 
										String.format("\t checking object(%d) == %d", o.getID(), id));
					
								if (o.getID()==id) {
									isObject = true;
									break;
								}
							}
							
							if (isObject) {
								final double distTmp = 
										methods.calculate.distance(
											methods.player.getMine().getLocation(), 
											o.getLocation());
								
								if (cur == null) {
									dist = distTmp;
									cur = o;
								} else if (distTmp < dist) {
									cur = o;
									dist = distTmp;
								}
							}
						}
					}
				}
			}
		}
		
		return cur;
	}
	
	
	/**
	 * Returns the <tt>RSObject</tt> that is nearest by ID
	 * 
	 * @param ids
	 * @return An <tt>RSObject</tt> representing the nearest object by the ID;
	 *         or null if there are no matching objects in the current region.
	 */
	public RSObject getNearestByID(final int... ids) {
		RSObject cur = null;
		double dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final RSObject[] oo = getAt(x
						+ methods.game.client().getBaseX(), y
						+ methods.game.client().getBaseY());
				if (oo != null) {
					for (final RSObject o : oo) {
						if (o != null) {
							boolean isObject = false;
							for (final int id : ids) {
								if (o.getID()==id) {
									isObject = true;
									break;
								}
							}
							
							if (isObject) {
								final double distTmp = 
										methods.calculate.distance(
											methods.player.getMine().getLocation(), 
											o.getLocation());
								
								if (cur == null) {
									dist = distTmp;
									cur = o;
								} else if (distTmp < dist) {
									cur = o;
									dist = distTmp;
								}
							}
						}
					}
				}
			}
		}
		
		return cur;
	}

	/**
	 * Returns the <tt>RSObject</tt> that is nearest by name.
	 * 
	 * Caution: This method only works after the object being requested by name
	 * has appeared on screen.  Otherwise, it returns null.
	 * 
	 * @param names
	 *            of the <tt>RSObject</tt>
	 * @return An <tt>RSObject</tt> representing the nearest object by name; or
	 *         null if there are no matching objects in the current region.
	 */
	public RSObject getNearestByName(final String... names) {
		RSObject cur = null;
		double dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final RSObject[] obj = getAt(x
						+ methods.game.client().getBaseX(), y
						+ methods.game.client().getBaseY());
				if (obj == null) {
					continue;
				}
				for (final RSObject o : obj) {
					if (o == null) {
						continue;
					}
					final RSObjectDef def = o.getDef();
					if (def == null) {
						continue;
					}
					String objectName = def.getName();
					if (objectName == null) {
						continue;
					}
					objectName = objectName.toLowerCase();
					boolean isObject = false;
					for (String name : names) {
						name = name.toLowerCase();
						if (objectName.contains(name)
								|| objectName.equals(name)) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						final double distTmp = methods.calculate.distance(
								methods.player.getMine().getLocation(),
								o.getLocation());
						if (cur == null) {
							dist = distTmp;
							cur = o;
						} else if (distTmp < dist) {
							cur = o;
							dist = distTmp;
						}
					}

				}
			}
		}
		return cur;
	}

	/**
	 * Returns the <tt>RSObject</tt> that is nearest, out of all of the
	 * RSObjects with the provided name(s).
	 * 
	 * @param names
	 *            The name(s) of the RSObject that you are searching.
	 * @return An <tt>RSObject</tt> representing the nearest object with one of
	 *         the provided names; or null if there are no matching objects in
	 *         the current region.
	 */
	public RSObject getNearFilterName(final String... names) {
		return getNearByFilter(new Filter<RSObject>() {

			@Override
			public boolean accept(final RSObject o) {
				final String name = o.getName();
				if (!name.isEmpty() && !name.equals("null")) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name))
							return true;
					}
				}
				return false;
			}
		});
	}

	public int getRealDistanceTo(final RSTile t, final boolean object) {
		final RSTile curPos = methods.player.getMine().getLocation();
		return methods.calculate.getRealDistanceTo(curPos.getX()
				- methods.game.client().getBaseX(), /* startX */
				curPos.getY() - methods.game.client().getBaseY(), /* startY */
				t.getX() - methods.game.client().getBaseX(), /* destX */
				t.getY() - methods.game.client().getBaseY(), /* destY */
				object); /* if it's an object, calculate path to it */
	}

	/**
	 * Returns the top <tt>RSObject</tt> on the specified x,y cords.
	 * 
	 * @param x
	 *            The x cords on which to search.
	 * @param y
	 *            The y cords on which to search.
	 * @return The top RSObject on the provided x,y cords; or null if none
	 *         found.
	 */
	public RSObject getTopAt(final int x, final int y) {
		final RSObject[] object = getAt(x, y);
		return object != null && object.length > 0 ? object[0] : null;
	}

	/**
	 * Returns the top <tt>RSObject</tt> on the specified tile.
	 * 
	 * @param t
	 *            The tile on which to search.
	 * @return The top RSObject on the provided tile; or null if none found.
	 */
	public RSObject getTopAt(final RSTile t) {
		return getTopAt(t.getX(), t.getY());
	}
}
