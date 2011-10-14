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

import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSTile;

/**
 * Tile related operations.
 * 
 * @author Runedev development team. - version 1.0
 */
public class Tile {

	private final Methods methods;
	private RSTile lastDoor = null;
	private long lastTry = 0;
	private int tryCount = 0;

	public Tile() {
		methods = Bot.methods;
	}

	/**
	 * Clicks a tile if it is on screen with given offsets in 3D space.
	 * 
	 * @param tile
	 *            The <code>RSTile</code> to do the action at.
	 * @param xd
	 *            Distance from bottom left of the tile to bottom right. Ranges
	 *            from 0-1.
	 * @param yd
	 *            Distance from bottom left of the tile to top left. Ranges from
	 *            0-1.
	 * @param h
	 *            Height to click the <code>RSTile</code> at. Use 1 for tables,
	 *            0 by default.
	 * @param action
	 *            The action to perform at the given <code>RSTile</code>.
	 * @return <tt>true</tt> if no exceptions were thrown; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean click(final RSTile tile, final double xd, final double yd,
			final int h, final String action) {
		return click(tile, xd, yd, h, action, null);
	}

	/**
	 * Clicks a tile if it is on screen with given offsets in 3D space.
	 * 
	 * @param tile
	 *            The <code>RSTile</code> to do the action at.
	 * @param xd
	 *            Distance from bottom left of the tile to bottom right. Ranges
	 *            from 0-1.
	 * @param yd
	 *            Distance from bottom left of the tile to top left. Ranges from
	 *            0-1.
	 * @param h
	 *            Height to click the <code>RSTile</code> at. Use 1 for tables,
	 *            0 by default.
	 * @param action
	 *            The action to perform at the given <code>RSTile</code>.
	 * @param option
	 *            The option to perform at the given <code>RSTile</code>.
	 * @return <tt>true</tt> if no exceptions were thrown; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean click(final RSTile tile, final double xd, final double yd,
			final int h, final String action, final String option) {
		final Point location = Calculations.tileToScreen(tile, xd, yd, h);
		if (location.x != -1 && location.y != -1) {
			methods.mouse.move(location, 3, 3);
			methods.sleep(methods.random(20, 100));
			return methods.menu.action(action, option);
		}
		return false;
	}

	/**
	 * Clicks a tile if it is on screen. It will left-click if the action is
	 * available as the default option, otherwise it will right-click and check
	 * for the action in the context methods.menu.
	 * 
	 * @param tile
	 *            The RSTile that you want to click.
	 * @param action
	 *            Action command to use click
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean click(final RSTile tile, final String action) {
		return click(tile, action, null);
	}

	/**
	 * Clicks a tile if it is on screen. It will left-click if the action is
	 * available as the default option, otherwise it will right-click and check
	 * for the action in the context menu.
	 * 
	 * @param t
	 *            The RSTile that you want to click.
	 * @param act
	 *            Action command to use on the Character (e.g "Attack" or
	 *            "Trade").
	 * @param path
	 *            Whether or not you want it to move the mouse using
	 *            {@link #moveMouseByPath(Point)}.
	 * @return <tt>true</tt> if the Character was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean click(final RSTile t, final String act, final boolean path) {
		try {
			int counter = 0;
			try {
				Point p = Calculations.tileToScreen(t);
				if (p.x == -1 || p.y == -1) {
					return false;
				}
				if (!path) {
					methods.mouse.move(p, 5, 5);
				}
				while (!methods.menu.getItems()[0].toLowerCase().contains(
						act.toLowerCase())
						&& counter < 5) {
					p = Calculations.tileToScreen(t);
					methods.mouse.move(p, 5, 5);
					counter++;
				}
				if (methods.menu.getItems()[0].toLowerCase().contains(
						act.toLowerCase())) {
					methods.mouse.click(true);
				} else {
					methods.mouse.click(false);
					methods.menu.action(act);
				}
				return true;
			} catch (final Exception e) {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Clicks a tile if it is on screen. It will left-click if the action is
	 * available as the default menu action, otherwise it will right-click and
	 * check for the action in the context methods.menu.
	 * 
	 * @param tile
	 *            The RSTile that you want to click.
	 * @param action
	 *            Action of the menu entry to click
	 * @param option
	 *            Option of the menu entry to click
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean click(final RSTile tile, final String action,
			final String option) {
		try {
			for (int i = 0; i++ < 5;) {
				final Point location = Calculations.tileToScreen(tile);
				if (location.x == -1 || location.y == -1) {
					return false;
				}
				methods.mouse.move(location, 5, 5);
				if (methods.menu.action(action, option)) {
					return true;
				}
			}
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	public boolean clickDoor(final RSTile t1, final RSTile t2) {
		if (t1 != lastDoor) {
			lastTry = 0;
			tryCount = 0;
			lastDoor = t2;
		}
		tryCount++;
		if (System.currentTimeMillis() - lastTry > methods.random(20000, 40000)) {
			tryCount = 1;
		}
		lastTry = System.currentTimeMillis();
		if (tryCount > 4) {
			if (methods.random(0, 10) < methods.random(2, 4)) {
				methods.camera
				.setRotation(methods.camera.getAngle()
						+ (methods.random(0, 9) < methods.random(6, 8) ? methods
								.random(-20, 20) : methods.random(-360,
										360)));
			}
			if (methods.random(0, 14) < methods.random(0, 2)) {
				methods.camera.setAltitude(methods.random(0, 100));
			}
		}
		if (tryCount > 100) {
			methods.stopScript();
		}
		if (!onScreen(t1) || !onScreen(t2)
				|| methods.calculate.distanceTo(t1) > methods.random(4, 7)) {
			if (methods.tile.onMap(t1)) {
				methods.walk.randomizeTile(t1, 3, 3);
				methods.wait(methods.random(750, 1250));
			} else {
				return false;
			}
		} else {
			final ArrayList<RSTile> objs = new ArrayList<RSTile>();
			objs.add(t1);
			objs.add(t2);
			try {
				final Point[] p = new Point[objs.size()];
				for (int c = 0; c < objs.size(); c++) {
					p[c] = Calculations.tileToScreen(objs.get(c));
				}
				float xTotal = 0;
				float yTotal = 0;
				for (final Point thePoint : p) {
					xTotal += thePoint.getX();
					yTotal += thePoint.getY();
				}
				final Point location = new Point((int) (xTotal / p.length),
						(int) (yTotal / p.length) - methods.random(0, 40));
				if (location.x == -1 || location.y == -1) {
					return false;
				}
				if (Math.sqrt(Math.pow(
						(methods.mouse.getLocation().getX() - location.getX()),
						2)
						+ Math.pow(
								(methods.mouse.getLocation().getY() - location
										.getY()), 2)) < methods.random(20, 30)) {
					final String[] commands = methods.menu.getItems();
					for (final String command : commands) {
						if (command.contains("Open")) {
							if (methods.menu.action("Open")) {
								lastTry = 0;
								tryCount = 0;
								return true;
							}
						}
					}
				}
				methods.mouse.move(location, 7, 7);
				if (methods.menu.action("Open")) {
					lastTry = 0;
					tryCount = 0;
					return true;
				}
			} catch (final Exception e) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Use by looping walkTo with this method as the argument.
	 * 
	 * @param t
	 *            The destination tile.
	 * @return Returns the closest tile to the destination on the MiniMap.
	 */
	public RSTile getClosestOnMap(final RSTile t) {
		if (!t.isOnMinimap() && methods.game.isLoggedIn()) {
			try {
				final RSTile loc = methods.player.getMyLocation();
				final RSTile walk = new RSTile((loc.getX() + t.getX()) / 2,
						(loc.getY() + t.getY()) / 2);
				return walk.isOnMinimap() ? walk : getClosestOnMap(walk);
			} catch (final StackOverflowError ignored) {
			}
		}
		return t;
	}

	/**
	 * Gets the destination tile. Where the flag is. WARNING: This method can
	 * return null.
	 * 
	 * @return The current destination tile.
	 * 
	 * @deprecated Use walk.getDestination()
	 */
	public RSTile getDestination() {
		return methods.walk.getDestination();
	}

	/**
	 * Will return the closest tile that is on screen to the given tile.
	 * 
	 * @param t
	 *            Tile you want to get to.
	 * @return Tile that is onScreen.
	 */
	public RSTile getOnScreen(final RSTile t) {
		try {
			if (onScreen(t)) {
				return t;
			} else {
				final RSTile halfWayTile = new RSTile((t.getX() + 
						methods.player.getMyLocation().getX()) / 2, (t.getY() + 
						methods.player.getMyLocation().getY()) / 2);
				if (onScreen(halfWayTile)) {
					return halfWayTile;
				} else {
					return getOnScreen(halfWayTile);
				}
			}
		} catch (final StackOverflowError soe) {
			// log("getTileOnScreen() error: " + soe.getMessage());
			return null;
		}
	}

	/**
	 * Returns the RSTile under the mouse.
	 * 
	 * @return <code>RSTile</code> under the mouse, or null if the mouse is not
	 *         over the viewport.
	 */
	public RSTile getUnderMouse() {
		final Point p = methods.mouse.getLocation();
		if (!methods.calculate.pointOnScreen(p)) {
			return null;
		}
		return getUnderPoint(p);
	}

	/**
	 * Gets the tile under a point.
	 * 
	 * @param p
	 *            The point.
	 * @return RSTile at the point's location
	 */
	public RSTile getUnderPoint(final Point p) {
		if (!methods.calculate.pointOnScreen(p)) {
			return null;
		}
		RSTile close = null;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final RSTile t = new RSTile(x
						+ methods.game.client().getBaseX(), y
						+ methods.game.client().getBaseY());
				final Point s = Calculations.tileToScreen(t);
				if (s.x != -1 && s.y != -1) {
					if (close == null) {
						close = t;
					}
					if (Calculations.tileToScreen(close).distance(p) > Calculations
							.tileToScreen(t).distance(p)) {
						close = t;
					}
				}
			}
		}
		return close;
	}

	/**
	 * Checks if the tile "t" is closer to the player than the tile "tt"
	 * 
	 * @param t
	 *            First tile.
	 * @param tt
	 *            Second tile.
	 * @return True if the first tile is closer to the player than the second
	 *         tile, otherwise false.
	 */
	public boolean isCloser(final RSTile t, final RSTile tt) {
		return methods.calculate.distanceTo(t) < methods.calculate
		.distanceTo(tt);
	}

	public boolean onMap(final RSTile t) {
		final Point p = toMiniMap(t);
		return p != null && p.x != -1 && p.y != -1;
	}

	public boolean onScreen(final RSTile t) {
		final Point p = Calculations.tileToScreen(t, 0);
		return p.getX() > 0 && p.getY() > 0;
	}

	public Point toMiniMap(final RSTile t) {
		return methods.calculate.worldToMinimap(t.getX(), t.getY());
	}
}
