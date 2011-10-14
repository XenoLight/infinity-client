package org.rsbot.script.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Methods;

public class RSObject {

	public static enum Type {

		INTERACTABLE, FLOOR_DECORATION, BOUNDARY, WALL_DECORATION
	}
	private final org.rsbot.client.RSObject obj;
	private final int x, y, type;
	private RSTile location;
	private final org.rsbot.client.Model model;
	private final Methods methods = Bot.methods;

	protected final Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Constructor for the RSObject wrapper
	 * 
	 * @param obj
	 *            - The <tt>org.rsbot.client.RSObject</tt> for this object
	 * @param x
	 *            - The x-coordinate for this object's tile.
	 * @param y
	 *            - The y-coordinate for this object's tile.
	 * @param type
	 *            - 0: Interactable object like trees 1: Ground decorations 2:
	 *            Fences/walls 3: Unknown 4: Unknown
	 */
	public RSObject(final org.rsbot.client.RSObject obj, final int x,
			final int y, final int type) {
		this.obj = obj;
		this.x = x;
		this.y = y;
		this.location = new RSTile(x,y);
		this.type = type;
		this.model = this.getObject().getModel();
	}

	/**
	 * Performs the specified action on this object.
	 * 
	 * @param action
	 *            the menu item to search and click
	 * @return returns true if clicked, false if object does not contain the
	 *         desired action
	 */
	public boolean action(final String action) {
		return action(action, null);
	}

	/**
	 * Performs the specified action on this object.
	 * 
	 * @param action
	 *            the action of the menu item to search and click
	 * @param option
	 *            the option of the menu item to search and click
	 * @return returns true if clicked, false if object does not contain the
	 *         desired action
	 */
	public boolean action(final String action, final String option) {
		final RSModel modelObj = getModel();
		
		if (modelObj != null) {
			return modelObj.action(action, option);
		}
		
		return methods.tile.click(getLocation(), action);
	}

	/**
	 * Left-clicks this object.
	 * 
	 * @return <tt>true</tt> if clicked.
	 */
	public boolean click() {
		return click(true);
	}

	/**
	 * Clicks this object.
	 * 
	 * @param leftClick
	 *            <tt>true</tt> to left-click; <tt>false</tt> to right-click.
	 * @return <tt>true</tt> if clicked.
	 */
	public boolean click(final boolean leftClick) {
		final RSModel model = getModel();
		if (model != null) {
			return model.click(leftClick);
		} else {
			Point p = Calculations.tileToScreen(getLocation());
			if (methods.pointOnScreen(p)) {
				methods.moveMouse(p);
				if (methods.pointOnScreen(p)) {
					methods.clickMouse(leftClick);
					return true;
				} else {
					p = Calculations.tileToScreen(getLocation());
					if (methods.pointOnScreen(p)) {
						methods.moveMouse(p);
						methods.clickMouse(leftClick);
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * Returns the distance in tiles from the player to this object.
	 * 
	 * @return Distance in tiles from the player to this object.
	 */
	public int distanceTo() {
		final RSPlayer pl = new RSPlayer(Bot.getClient().getMyRSPlayer());
		final RSTile t = pl.getLocation();

		return (int) Math.hypot(t.getX() - this.x, t.getY() - this.y);
	}


	/**
	 * Draws the wire-frame of the object on the screen. Supports every loaded
	 * model. Please use in onRepaint()
	 */
	public void drawModel(final Graphics g) {
		if (this.obj == null || g == null || this.getModel() == null) {
			return;
		}
		for (final Polygon p : this.getModel().getTriangles()) {
			g.drawPolygon(p);
		}
	}

	/**
	 * Returns the angle to this object
	 * 
	 * @return The angle
	 */
	public int getAngle() {
		return methods.getAngleToCoordinates(this.x, this.y);
	}

	/**
	 * Selects a slightly randomized point on the screen from within the
	 * object's model. Accomplishes this by selected a facet of the model and
	 * then establishing a point that lies within the bounds of that facet.
	 * 
	 * @return A point on the screen that lies within the object's model.
	 *         Returns null after several retries if unsuccessful in selecting a
	 *         facet. If the object has not model, returns a point based on the
	 *         tile location.
	 */
	public Point getClickableModelPoint() {
		Point[][] facets = new Point[0][0];
		Point bad = new Point(-1,-1);
		
		if (this.getModel() != null) {
			facets = this.getModel().getModelFacets();
		}
		
		if (facets == null)  {
			return bad;
		}
		
		Point location;
		Point[] facet = null;

		if (this.model == null || facets.length == 0) {
			// No model for the object, use brute force via tile
			location = Calculations.tileToScreen(new RSTile(this.x, this.y));
			if (location.equals(bad))  {
				return bad;
			}

			int ct = 0;
			do {
				if (this.model == null) {
					location.x += methods.random(0, 5);
					location.y += methods.random(0, 5);
				}
			} while (ct++ < 20 && !methods.calculate.pointOnScreen(location));

			return location;
		}

		// At this point, we know we have a model to work with.
		int retries = 0;
		do {
			location = getModelPoint();
			for (int i = 0; i < facets.length; i++) {
				final Point[] f = facets[i];
				final Point dmy = new Point(-1, -1);

				if ((f[0].equals(location) && f[0].x != -1 && f[0].y != -1
						&& !f[1].equals(dmy) && !f[2].equals(dmy))
						|| (f[1].equals(location) && f[1].x != -1
								&& f[1].y != -1 && !f[0].equals(dmy) && !f[2]
								                                           .equals(dmy))
								                                           || (f[2].equals(location) && f[2].x != -1 && f[2].y != -1)
								                                           && !f[0].equals(dmy) && !f[1].equals(dmy)) {
					facet = f;
					break;
				}
			}
		} while (retries++ < 20 && facet == null);

		// There are facets with points that are either -1,-1 or have the x or y
		// coordinate as -1. If so, facet will be null, so return null since we
		// already tried 20 times to get a good facet..
		if (facet == null) {
			return null;
		}

		// At this stage, if we have a model, we now also know which facet to
		// click.
		// Now get a point that lies WITHIN that facet. To do this we use the
		// middle x value and the middle y value of the three points. Lines
		// drawn
		// vertically through the middle x point and horizontally through the
		// middle y point intersect within the triagular facet.
		//
		// For facets with a vertical or horizontal (or both) edge, the x or y
		// value for that edge will be treated as the middle value.
		//
		// Then randomize these x,y coordinates a bit, making sure that they
		// remain withing the facit. We'll use the Polygon class to do this.
		int x = -1, y = -1;

		// 1. first check for horizontal or vertical edges.
		if (facet[0].x == facet[1].x) {
			x = facet[0].x;
		} else if (facet[1].x == facet[2].x) {
			x = facet[1].x;
		} else if (facet[2].x == facet[3].x) {
			x = facet[2].x;
		}

		if (facet[0].y == facet[1].y) {
			y = facet[0].y;
		} else if (facet[1].y == facet[2].y) {
			y = facet[1].y;
		} else if (facet[2].y == facet[3].y) {
			y = facet[2].y;
		}

		// 2. If x or y hasn't been set yet, get middle value from facet.
		// First, deterimine the bounds of the values in the facet.
		int minx = 99999;
		int miny = 99999;
		int maxx = -1;
		int maxy = -1;
		for (final Point pt : facet) {
			if (pt.x < minx) {
				minx = pt.x;
			}
			if (pt.x > maxx) {
				maxx = pt.x;
			}
			if (pt.y < miny) {
				miny = pt.y;
			}
			if (pt.y > maxy) {
				maxy = pt.y;
			}
		}

		// If x or y have not already been identified as part of a
		// horizontal or vertical edge, they will be set to the
		// value for the point that lies between the min and max.
		for (final Point pt : facet) {
			if (x == -1 && pt.x > minx && pt.x < maxx) {
				x = pt.x;
			}

			if (y == -1 && pt.y > miny && pt.y < maxy) {
				y = pt.y;
			}
		}

		// 3. Now x and y should identify a point within the facet.
		// Let's randomize a little until we've done so and are
		// still within the facet;
		final Polygon poly = new Polygon();
		poly.addPoint(facet[0].x, facet[0].y);
		poly.addPoint(facet[1].x, facet[1].y);
		poly.addPoint(facet[2].x, facet[2].y);
		poly.addPoint(facet[3].x, facet[3].y);

		int deltaxLow = 0, deltaxHi = 0;
		if (x == minx) {
			deltaxLow = 0;
			deltaxHi = (maxx - minx) / 3;
		} else if (x == maxx) {
			deltaxLow = -(maxx - minx) / 3;
			deltaxHi = 0;
		} else {
			deltaxLow = -(maxx - minx) / 3;
			deltaxHi = (maxx - minx) / 3;
		}

		int deltayLow = 0, deltayHi = 0;
		if (y == miny) {
			deltayLow = 0;
			deltayHi = (maxy - miny) / 3;
		} else if (y == maxy) {
			deltayLow = -(maxy - miny) / 3;
			deltayHi = 0;
		} else {
			deltayLow = -(maxy - miny) / 3;
			deltayHi = (maxy - miny) / 3;
		}

		int newx;
		int newy;
		int ct = 0;
		do {
			newx = x + methods.random(deltaxLow, deltaxHi);
			newy = y + methods.random(deltayLow, deltayHi);
			location = new Point(newx, newy);
		} while (ct++ < 100 && !poly.contains(location)
				|| !methods.pointOnScreen(location));

		if (ct == 100) {
			return null;
		}
		// Now we have a new point from within the model that is randomized
		// and is know to be on the screen.

		if (location.x == -1 || location.y == -1) {
			return null;
		} else {
			return location;
		}
	}

	/**
	 * Gets the object definition of this object.
	 * 
	 * @return The RSObjectDef if available, otherwise <code>null</code>.
	 */
	public RSObjectDef getDef() {
		final org.rsbot.client.Node ref = Calculations.findNodeByID(Bot.getClient()
				.getRSObjectDefLoader(), getID());
		if (ref != null) {
			if (ref instanceof org.rsbot.client.HardReference) {
				return new RSObjectDef(
						(org.rsbot.client.RSObjectDef) (((org.rsbot.client.HardReference) ref)
								.get()));
			} else if (ref instanceof org.rsbot.client.SoftReference) {
				final Object def = ((org.rsbot.client.SoftReference) ref)
				.getReference().get();
				if (def != null) {
					return new RSObjectDef((org.rsbot.client.RSObjectDef) def);
				}
			}
		}
		return null;
	}

	/**
	 * Obtains the ID of this object.
	 * 
	 * @return ID
	 */
	public int getID() {
		return this.obj.getID();
	}

	/**
	 * Obtains the RSTile for the location of this object
	 * 
	 * @return <tt>RSTile</tt>
	 */
	public RSTile getLocation() {
		return new RSTile(this.x, this.y);
	}

	/**
	 * Obtains the model for this object, if one is available.
	 * 
	 * @return <tt>RSObjectModel</tt> or <tt>null</tt> if an exception occurs
	 */
	public RSObjectModel getModel() {
		if (this.getObject().getModel() == null) {
			return null;
		}
		try {
			return new RSObjectModel(Bot.methods, this.getObject().getModel(),
					this.getObject());
		} catch (final AbstractMethodError e) {
			return null;
		}
	}

	/**
	 * Selects a random point from the model from the points that are between
	 * 40% to 60% of the min-to-max distance from the player...i.e. the
	 * mid-range points relative to the player. The point selected will be part
	 * of a full facet (i.e. one having not points of (-1,-1).
	 * 
	 * @return A point from the model
	 */
	public Point getModelPoint() {
		final Point p[] = this.getModel().getModelPoints();

		// Keep the top 10% of the model points that are closes to the
		// player and choose from them. This prevents what can appear to be
		// wildly random clicking of the model.

		// Create a hash map of the distances from the player for each point
		// in the model. The key is the index into p.
		final HashMap<Integer, Integer> dists = new HashMap<Integer, Integer>();
		int maxDist = 0, minDist = 9999;

		Point playerPt = methods.player.getMyLocation().getScreenLocation();
		for (int i = 0; i < p.length; i++) {
			final int dist = (int) p[i].distance(playerPt);
			if (dist > maxDist) {
				maxDist = dist;
			}
			if (dist < minDist) {
				minDist = dist;
			}

			dists.put(i, dist);
		}

		int lowerDistBound = (int) (minDist + (maxDist - minDist) * .4);
		int upperDistBound = (int) (minDist + (maxDist - minDist) * .6);

		final List<Point> targetPts = new ArrayList<Point>();

		// Now run through the distances map and save the points whose
		// distance from the player is not more than distBound.
		while (targetPts.size() == 0) {
			for (final Map.Entry<Integer, Integer> entry : dists.entrySet()) {
				final int d = entry.getValue();
				if (d > lowerDistBound && d < upperDistBound) {
					targetPts.add(p[entry.getKey()]);
				}
			}

			if (targetPts.size() == 0) {
				lowerDistBound--;
				upperDistBound++;
			}
		}

		return targetPts.get(methods.random(0, targetPts.size() - 1));
	}

	/**
	 * Returns the name of the object.
	 * 
	 * @return The object name if the definition is available; otherwise "".
	 */
	public String getName() {
		final RSObjectDef objectDef = getDef();
		return objectDef != null ? objectDef.getName() : "";
	}

	/**
	 * Obtains the accessor for this object.
	 * 
	 * @return <tt>org.rsbot.client.RSObject</tt>
	 */
	public org.rsbot.client.RSObject getObject() {
		return this.obj;
	}

	/**
	 * Character point on screen.
	 * 
	 * @return <b>Point</b> on screen, if not on screen null
	 */
	public Point getPointLocation() {
		final RSModel model = getModel();
		if (model == null) {
			return Calculations.worldToScreen(
					Bot.getClient().getBaseX() + obj.getX() / 512, Bot
					.getClient().getBaseY() + obj.getY() / 512, -1);
		} else {
			return model.getPoint();
		}
	}

	/**
	 * Get a point on screen for the object.
	 * 
	 * @return <b>Point</b> on screen, if not on screen null
	 */
	public Point getScreenLocation() {
		return getModelPoint();
	}
	
	/**
	 * Gets the tile's location on the minimap.
	 * 
	 * @return <b>Point</b> on minimap if visible else <tt>null</tt>
	 */
	public Point getMapLocation() {
		return methods.calculate.worldToMinimap(this.x,this.y);
	}

	/**
	 * Returns a number between 0 and 4.</br> 0: Interactable object like trees
	 * 1: Ground decorations 2: Fences / walls 3: Unknown 4: Unknown
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Moves the mouse over this object.  Useful to enable obtaining the 
	 * menu actions.
	 * 
	 * @return <tt>true</tt> if the mouse was moved.
	 */
	public void hover() {
		final RSModel model = getModel();
		if (model != null) {
			model.hover();
		} else {
			final Point p = Calculations.tileToScreen(getLocation());
			if (methods.calculate.pointOnScreen(p)) {
				methods.mouse.move(p);
			}
		}
	}

	/**
	 * Used to determine whether the object is on the minimap.
	 * 
	 * @return <tt>true</tt> if on the minimap; otherwise <tt>false</tt>.
	 */
	public boolean isOnMinimap() {
		final Point p = methods.calculate.worldToMinimap(this.x, this.y);
		return (p != null && p.x != -1 && p.y != -1);
	}

	/**
	 * Used to determine whether the object is on screen.
	 * 
	 * @return <tt>true</tt> if on screen; otherwise <tt>false</tt>.
	 */
	public boolean isOnScreen() {
		final RSModel model = getModel();
		
		if (model == null) {
			return location.isOnScreen();
		} else {
			return methods.calculate.pointOnScreen(model.getPoint());
		}
	}

	/**
	 * Used to determine whether the object's tile is reachable.
	 * 
	 * @return <tt>true</tt> if this object's tile is reachable; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isReachable() {
		return Calculations.canReach(this.getLocation(), true);
	}

	/**
	 * Turns to the object with a random deviation of 2 degrees
	 */
	public void turnTo() {
		final int angle = this.getAngle() + methods.random(-2, 2);
		methods.setCameraRotation(angle);
	}

	/**
	 * Uses the specified inventory item on this object.
	 * 
	 * @param item
	 * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
	 */
	public boolean useItemOn(final RSItem item) {
		if (methods.getCurrentTab() != Constants.TAB_INVENTORY) {
			methods.openTab(Constants.TAB_INVENTORY);
		}

		return methods.atInventoryItem(item.getID(), "Use") && this.click(true);
	}
}
