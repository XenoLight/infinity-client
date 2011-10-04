package org.lazygamerz.scripting.api;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

/**
 * Walking related operations.
 */
public class Walk {

	private final Methods methods;
	public ArrayList<WalkerNode> nodes = new ArrayList<WalkerNode>();
	private final java.util.Random random = new java.util.Random();
	private boolean mapLoaded = false;
	public final int INTERFACE_RUN_ORB = 750;

	public Walk() {
		methods = Bot.methods;
	}

	/**
	 * This method will remove any duplicates tiles in a RSTile[] path. This is
	 * preferably to be used with generateFixedPath. For instance:walk.PathMM
	 * (cleanPath(generatedFixedPath(tile)));
	 * 
	 * @param path
	 *            The messy RSTile[] path with duplicate tiles.
	 * @return RSTile[] path with no duplicate tiles.
	 * @author Taha
	 */
	public RSTile[] cleanPath(final RSTile[] path) {
		final ArrayList<RSTile> tempPath = new ArrayList<RSTile>();
		for (int i = 0; i < path.length; i++) {
			if (!tempPath.contains(path[i])) {
				tempPath.add(path[i]);
			}
		}
		final RSTile[] cleanedPath = new RSTile[tempPath.size()];
		for (int i = 0; i < tempPath.size(); i++) {
			cleanedPath[i] = tempPath.get(i);
		}
		return cleanedPath;
	}

	public int distance(final WalkerNode start, final int endX, final int endY) {
		final int dx = start.x - endX;
		final int dy = start.y - endY;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public WalkerNode[] findPath(final WalkerNode start, final WalkerNode end) {
		if (!mapLoaded) {
			loadMap();
		}
		try {
			final ArrayList<WalkerNode> Q = new ArrayList<WalkerNode>();
			for (final WalkerNode thisNode : nodes) {
				thisNode.distance = Integer.MAX_VALUE;
				thisNode.previous = null;
				Q.add(thisNode);
			}
			start.distance = 0;
			while (!Q.isEmpty()) {
				WalkerNode nearestNode = Q.get(0);
				for (final WalkerNode thisNode : Q) {
					if (thisNode.distance < nearestNode.distance) {
						nearestNode = thisNode;
					}
				}
				Q.remove(Q.indexOf(nearestNode));
				if (nearestNode == end) {
					break;
				} else {
					for (final WalkerNode neighbourNode : nearestNode.neighbours) {
						final int alt = nearestNode.distance
						+ nearestNode.distance(neighbourNode);
						if (alt < neighbourNode.distance) {
							neighbourNode.distance = alt;
							neighbourNode.previous = nearestNode;
						}
					}
				}
			}
			final ArrayList<WalkerNode> nodePath = new ArrayList<WalkerNode>();
			nodePath.add(end);
			WalkerNode previousNode = end.previous;
			while (previousNode != null) {
				nodePath.add(previousNode);
				previousNode = previousNode.previous;
			}
			if (nodePath.size() == 1) {
				return null;
			}
			final WalkerNode[] nodeArray = new WalkerNode[nodePath.size()];
			for (int i = nodePath.size() - 1; i >= 0; i--) {
				nodeArray[nodePath.size() - i - 1] = nodePath.get(i);
			}
			return nodeArray;
		} catch (final Exception e) {
		}
		return null;
	}

	public RSTile[] fixPath(final RSTile[] path) {
		final ArrayList<RSTile> newPath = new ArrayList<RSTile>();
		for (int i = 0; i < path.length - 1; i++) {
			newPath.addAll(fixPath2(path[i], path[i + 1]));
		}
		return newPath.toArray(new RSTile[newPath.size()]);
	}

	/*
	 * Credits: Aftermath
	 */
	public List<RSTile> fixPath2(int startX, int startY, final int endX, final int endY) {
		double dx, dy;
		final ArrayList<RSTile> list = new ArrayList<RSTile>();
		list.add(new RSTile(startX, startY));
		while (Math.hypot(endY - startY, endX - startX) > 8) {
			dx = endX - startX;
			dy = endY - startY;
			final int gamble = methods.random(14, 17);
			while (Math.hypot(dx, dy) > gamble) {
				dx *= .95;
				dy *= .95;
			}
			startX += (int) dx;
			startY += (int) dy;
			list.add(new RSTile(startX, startY));
		}
		list.add(new RSTile(endX, endY));
		return list;
	}

	public List<RSTile> fixPath2(final RSTile t) {
		return fixPath2(methods.player.getMyLocation(), t);
	}

	public List<RSTile> fixPath2(final RSTile t1, final RSTile t2) {
		return fixPath2(t1.getX(), t1.getY(), t2.getX(), t2.getY());
	}

	public RSTile[] generateFixedPath(final int x, final int y) {
		return fixPath(generateProperPath(x, y));
	}

	public RSTile[] generateFixedPath(final RSTile t) {
		return fixPath(generateProperPath(t));
	}

	public RSTile[] generateProperPath(final int x, final int y) {
		if (!mapLoaded) {
			loadMap();
		}
		final int mx = methods.player.getMine().getLocation().getX();
		final int my = methods.player.getMine().getLocation().getY();
		final WalkerNode target = new WalkerNode(x, y);
		WalkerNode startNode = nodes.get(0), end = startNode;
		int dis = distance(startNode, mx, my);
		for (final WalkerNode node : nodes) {
			if (distance(node, mx, my) < dis) {
				startNode = node;
				dis = distance(node, mx, my);
			}
		}
		dis = distance(end, x, y);
		for (final WalkerNode node : nodes) {
			if (node.distance(target) < dis) {
				end = node;
				dis = node.distance(target);
			}
		}
		final WalkerNode[] nodePath = findPath(startNode, end);
		if (nodePath == null) {
			return new RSTile[] { new RSTile(mx, my), new RSTile(x, y) };
		} else {
			final RSTile[] tilePath = new RSTile[nodePath.length];
			tilePath[0] = new RSTile(mx, my);
			for (int i = 1; i < tilePath.length - 1; i++) {
				tilePath[i] = new RSTile(nodePath[i - 1].x, nodePath[i - 1].y);
			}
			tilePath[tilePath.length - 1] = new RSTile(x, y);
			return tilePath;
		}
	}

	public RSTile[] generateProperPath(final RSTile t) {
		return generateProperPath(t.getX(), t.getY());
	}

	/**
	 * Returns the closest tile on the minimap to a given tile.
	 * 
	 * @param tile
	 *            The destination tile.
	 * @return Returns the closest tile to the destination on the minimap.
	 */
	public RSTile getClosestTileOnMap(final RSTile tile) {
		if (!methods.tile.onMap(tile) && methods.game.isLoggedIn()) {
			final RSTile loc = methods.player.getMine().getLocation();
			final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2,
					(loc.getY() + tile.getY()) / 2);
			return methods.tile.onMap(walk) ? walk : getClosestTileOnMap(walk);
		}
		return tile;
	}
	
	/**
	 * Returns the closest tile on the minimap to a given ground item.
	 * 
	 * @param tile
	 *            The destination tile.
	 * @return Returns the closest tile to the destination on the minimap.
	 */
	public RSTile getClosestTileOnMap(final RSGroundItem item) {
		if (!item.isOnMinimap() && methods.game.isLoggedIn()) {
			final RSTile loc = methods.player.getMine().getLocation();
			final RSTile itemloc = item.getLocation();
			final RSTile walk = new RSTile((loc.getX() + itemloc.getX()) / 2,
					(loc.getY() + itemloc.getY()) / 2);
			return methods.tile.onMap(walk) ? walk : getClosestTileOnMap(walk);
		}
		
		return item.getLocation();
	}

	/**
	 * Gets the collision flags for a given floor level in the loaded region.
	 * 
	 * @param plane
	 *            The floor level (0, 1, 2 or 3).
	 * @return the collision flags.
	 */
	public int[][] getCollisionFlags(final int plane) {
		return methods.game.client().getRSGroundDataArray()[plane].getBlocks();
	}

	/**
	 * Returns the collision map offset from the current region base on a given
	 * plane.
	 * 
	 * @param plane
	 *            The floor level.
	 * @return The offset as an RSTile.
	 */
	public RSTile getCollisionOffset(final int plane) {
		final org.rsbot.client.RSGroundData data = methods.game.client()
		.getRSGroundDataArray()[plane];
		return new RSTile(data.getX(), data.getY());
	}

	/**
	 * Gets the destination tile (where the flag is on the minimap). If there is
	 * no destination currently, null will be returned.
	 * 
	 * @return The current destination tile, or null if no destination.
	 */
	public RSTile getDestination() {
		if (methods.game.client().getDestX() <= 0) {
			return null;
		}
		return new RSTile(methods.game.client().getDestX()
				+ methods.game.client().getBaseX(), methods.game.client()
				.getDestY() + methods.game.client().getBaseY());
	}
	
	/**
	 * Gets the distance from the player to the 
	 * destination tile (where the flag is on the minimap). If there is
	 * no destination currently, 0 will be returned.
	 * 
	 * @return The current destination tile, or null.
	 */
	public int distanceToDestination() {
		if (methods.game.client().getDestX() <= 0) {
			return 0;
		}
		
		RSTile dest = new RSTile(methods.game.client().getDestX()
				+ methods.game.client().getBaseX(), methods.game.client()
				.getDestY() + methods.game.client().getBaseY());
		return dest.distanceTo();
	}

	/**
	 * Determines whether or not a given tile is in the loaded map area.
	 * 
	 * @param tile
	 *            The tile to check.
	 * @return <tt>true</tt> if local; otherwise <tt>false</tt>.
	 */
	public boolean isLocal(final RSTile tile) {
		final int[][] flags = getCollisionFlags(methods.game.getPlane());
		final int x = tile.getX() - methods.game.getBaseX();
		final int y = tile.getY() - methods.game.getBaseY();
		return flags != null && x >= 0 && y >= 0 && x < flags.length
		&& y < flags.length;
	}

	public void loadLinks() {
		final String[] matrix = Walker.getWalkerLinks().split(" ");
		for (int i = 0; i < matrix.length; i += 2) {
			final int x = Integer.parseInt(matrix[i]);
			final int y = Integer.parseInt(matrix[i + 1]);
			WalkerNode node = nodes.get(x);
			node.neighbours.add(nodes.get(y));
			node = nodes.get(y);
			node.neighbours.add(nodes.get(x));
		}
	}

	public void loadMap() {
		mapLoaded = true;
		loadNodes();
		loadLinks();
	}

	public void loadNodes() {
		final String[] matrix = Walker.getWalkerNodes().split(" ");
		for (int i = 0; i < matrix.length; i += 2) {
			nodes.add(new WalkerNode(Integer.parseInt(matrix[i]), Integer
					.parseInt(matrix[i + 1])));
		}
	}

	/**
	 * Returns the next tile in the specified path, applying a default
	 * maximum distance of 16 between tiles.
	 * @param path The path being walked.
	 * @return <tt>RSTile</tt> of the next tile in the path.
	 */
	public RSTile nextTile(final RSTile path[]) {
		return nextTile(path, 16);
	}

	/**
	 * Returns the next tile in the specified path, applying the specified
	 * maximum distance of 16 between tiles.
	 * @param path The path being walked.
	 * @return <tt>RSTile</tt> of the next tile in the path.
	 */
	public RSTile nextTile(final RSTile path[], final int max) {
		return nextTile(path, max, true);
	}

	/**
	 * Returns the next tile to walk to on a path.
	 * 
	 * @param path
	 *            The path.
	 * @param maxDist
	 *            The maximum distance that a path tile should be from the
	 *            player in order for it to be considered the next tile. The
	 *            method searches from the end of the path to the beginning.
	 * @param enable
	 *            If false, this method will ignore the int maxDist.
	 * @return The next tile to walk to on the provided path.
	 */
	public RSTile nextTile(final RSTile path[], final int max, final boolean enable) {
		final int randomdis = methods.random(3, 5);
		int closest = -1, sDist = -1;
		
		if (methods.calculate.distanceTo(path[path.length - 1]) <= randomdis) {
			return null;
		}
		
		for (int i = path.length - 1; i >= 0; i--) {
			final int dist = methods.calculate.distanceTo(path[i]);
			if (sDist == -1 || dist < sDist) {
				if (methods.player.getMine().getLocation().equals(path[i])
						|| methods.calculate.distanceTo(path[i]) <= 6) {
					return path[i + 1];
				} else {
					sDist = dist;
					closest = i;
				}
			}
			if (enable && dist <= max) {
				return path[i];
			}
		}
		
		return path[closest];
	}

	/**
	 * Walks to the end of a path applying a default randomization of 2,2 and
	 * a default maximum distance between tiles of 16. 
	 * This method should be looped.

	 * Use {@link #pathMM(RSTile[], int, int) to specify an explicit 
	 * randomization.
	 * 
	 * @param path
	 *            The path to walk along.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean pathMM(final RSTile[] path) {
		return pathMM(path, 16, 2, 2);
	}

	/**
	 * Walks to the end of a path applying a default randomization of 2,2 and 
	 * applies the specified maximum distance between tiles. 
	 * This method should be looped.
	 * 
	 * 
	 * Use {@link #pathMM(RSTile[], max, int, int) to specify an explicit 
	 * randomization.
	 *
	 * @param path
	 *            The path to walk along.
	 * @param max
	 *            See {@link #nextTile(RSTile[], int)}.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean pathMM(final RSTile[] path, final int max) {
		return pathMM(path, max, 2, 2);
	}


	/**
	 * Walks to the end of a path applying the specified randomization and 
	 * a default maximum distance between tiles of 16. 
	 * This method should be looped.
	 * 
	 * @param path
	 *            The path to walk along.
	 * @param x
	 *            The x randomness (between 0 and x-1).
	 * @param y
	 *            The y randomness (between 0 and y-1).
	 * @return 
	 *			  <tt>true</tt> if the next tile was reached; otherwise  <tt>false</tt>.
	 */
	public boolean pathMM(final RSTile[] path, final int x, final int y) {
		return pathMM(path, 16, x, y);
	}

	/**
	 * Walks to the end of a path applying the specified randomization and 
	 * applies the specified maximum distance between tiles. 
	 * This method should be looped.
	 * 
	 * @param path
	 *            The path to walk along.
	 * @param max
	 *            See {@link #nextTile(RSTile[], int)}.
	 * @param x
	 *            The x randomness (between 0 and x-1).
	 * @param y
	 *            The y randomness (between 0 and y-1).            
	 * @return 
	 * 			  <tt>true</tt> if the next tile was reached; otherwise <tt>false</tt>.
	 */
	public boolean pathMM(final RSTile[] path, final int max, final int x, final int y) {
		try {
			RSPlayer me = methods.player.getMine();
			RSTile dest = methods.walk.getDestination();
			
			// If we are still walking and not within 3-6 tiles of the destination, just return.
			if (me.isMoving() && dest!=null && dest.isOnMinimap() && dest.distanceTo()>methods.random(4,6))  {
				return false;
			}
			
			final RSTile next = nextTile(path, max);
			
			if (next!=null)  {
				// If the next tile is closer than the current destination,
				// don't do anything.  This avoids unnecessary spam clicks.
				if (dest!=null && dest.isOnMinimap() && (next.distanceTo() < dest.distanceTo()))  {
					return false;
				}	
			
				if (tileMM(next, x, y))  {					
					methods.player.waitToMove(methods.random(1400,1700));
					return true;
				}
			}
			
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Walks a path using onScreen clicks and not the MiniMap. If the next tile
	 * is not on the screen, it will find the closest tile that is on screen and
	 * it will walk there instead.	 
	 * 
	 * This method applies a default maximum distance of 16 between tiles. 
	 * There is no randomization applied to tiles in the path.
	 * This method should be looped.
	 * 
	 * @param path
	 *            The path to walk along.
	 * @param max
	 *            See {@link #nextTile(RSTile[], int)}.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean pathOnScreen(final RSTile[] path) {
		return pathOnScreen(path, 16);
	}

	/**
	 * Walks a path using onScreen clicks and not the MiniMap. If the next tile
	 * is not on the screen, it will find the closest tile that is on screen and
	 * it will walk there instead.
	 * 
	 * @param path
	 *            Path to walk.
	 * @param max
	 *            Max distance between tiles in the path.
	 * @return True if successful.
	 */
	public boolean pathOnScreen(final RSTile[] path, final int max) {
		try {
			RSPlayer me = methods.player.getMine();
			RSTile dest = methods.walk.getDestination();
			
			// If we are still walking and not within 3-6 tiles of the destination, just return.
			if (me.isMoving() && dest!=null && dest.distanceTo()>methods.random(4,6))  {
				return false;
			}
			
			final RSTile next = nextTile(path, max);

			if (next != null) {
				boolean retVal = false;
				
				if (methods.tile.click(methods.tile.getOnScreen(next), "walk"))  {
					retVal=true;
					methods.player.waitToMove(methods.random(1400,1700));
				}
				
				return retVal;
			} else {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Randomized path of tiles.
	 * 
	 * @param path
	 *            The RSTiles to randomize.
	 * @param maxX
	 *            Max X distance from tile.getX().
	 * @param maxY
	 *            Max Y distance from tile.getY().
	 * @return The new, randomized path.
	 */
	public RSTile[] randomizePath(final RSTile[] path, final int X, final int Y) {
		final RSTile[] t = new RSTile[path.length];
		for (int i = 0; i < path.length; i++) {
			t[i] = randomizeTile(path[i], X, Y);
		}
		return t;
	}

	/**
	 * Randomizes a single tile.
	 * 
	 * @param tile
	 *            The RSTile to randomize.
	 * @param maxX
	 *            Max X distance from tile.getX().
	 * @param maxY
	 *            Max Y distance from tile.getY().
	 * @return The randomized tile.
	 */
	public RSTile randomizeTile(final RSTile tile, final int maxX, final int maxY) {
		int x = tile.getX();
		int y = tile.getY();
		if (maxX > 0) {
			double d = random.nextDouble() * 2;
			d -= 1.0;
			d *= maxX;
			x += (int) d;
		}
		if (maxY > 0) {
			double d = random.nextDouble() * 2;
			d -= 1.0;
			d *= maxY;
			y += (int) d;
		}
		return new RSTile(x, y);
	}

	public RSTile[] reversePath(final RSTile[] r) {
		final RSTile[] t = new RSTile[r.length];
		for (int i = 0; i < t.length; i++) {
			t[i] = r[r.length - i - 1];
		}
		return t;
	}

	/**
	 * Walks to the given tile using the minimap.
	 * 
	 * No randomness is applied by this method.
	 * Use {@link #tileMM(RSTile, int, int) to specify an explicit 
	 * randomization.
	 * 
	 * @param t
	 *            The tile to walk to.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 * @see #walkTileMM(RSTile, int, int)
	 */
	public boolean tileMM(final RSTile t) {
		return tileMM(t, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 * 
	 * @param t
	 *            The tile to walk to.
	 * @param r
	 *            The maximum deviation from the tile to allow.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean tileMM(final RSTile t, final int r) {
		return tileMM(t, r, r, 0, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 * 
	 * @param t
	 *            The tile to walk to.
	 * @param x
	 *            The x randomness (between 0 and x-1).
	 * @param y
	 *            The y randomness (between 0 and y-1).
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean tileMM(final RSTile t, final int x, final int y) {
		return tileMM(t, x, y, 0, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 * 
	 * @param t
	 *            The tile to walk to.
	 * @param x
	 *            The x randomness (between 0 and x-1).
	 * @param y
	 *            The y randomness (between 0 and y-1).
	 * @param rx
	 *            The mouse gaussian randomness (x).
	 * @param ry
	 *            The mouse gaussian randomness (y).
	 * @param rm
	 *            The mouse movement distance after click.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean tileMM(final RSTile t, final int x, final int y,
			final int rx, final int ry, final int rm) {
		int xx = t.getX(), yy = t.getY();
		if (x > 0) {
			if (methods.random(1, 3) == methods.random(1, 3)) {
				xx += methods.random(0, x);
			} else {
				xx -= methods.random(0, x);
			}
		}
		if (y > 0) {
			if (methods.random(1, 3) == methods.random(1, 3)) {
				yy += methods.random(0, y);
			} else {
				yy -= methods.random(0, y);
			}
		}
		RSTile dest = new RSTile(xx, yy);
		if (!methods.tile.onMap(dest)) {
			dest = getClosestTileOnMap(dest);
		}
		final Point p = methods.tile.toMiniMap(dest);
		if (p.x != -1 && p.y != -1) {
			methods.mouse.move(p, rx, ry, rm);
			final Point p2 = methods.tile.toMiniMap(dest);
			if (p2.x != -1 && p2.y != -1) {
				if (!methods.mouse.getLocation().equals(p2)) {// Perfect
					// alignment.
					methods.mouse.move(p2);
				}
				if (!methods.mouse.getLocation().equals(p2)) {// We must've
					// moved while
					// walking, move
					// again!
					methods.mouse.move(p2);
				}
				if (!methods.mouse.getLocation().equals(p2)) {// Get exact since
					// we're
					// moving...
					// should be
					// removed?
					methods.mouse.hop(p2);
				}
				methods.mouse.click(true, rm);
				return true;
			}
		}
		return false;
	}

	/**
	 * Walks to a tile using onScreen clicks and not the MiniMap. If the tile is
	 * not on the screen, it will find the closest tile that is on screen and it
	 * will walk there instead.
	 *
	 * No randomization is applied to the specified tile.
	 * 
	 * @param tileToWalk
	 *            Tile to walk.
	 * @return True if successful.
	 */
	public boolean tileOnScreen(final RSTile tileToWalk) {
		return methods.tile.click(methods.tile.getOnScreen(tileToWalk), "Walk ");
	}

	/**
	 * Walks to a tile using onScreen clicks and not the MiniMap. If the tile is
	 * not on the screen, it will find the closest tile that is on screen and it
	 * will walk there instead.
	 *
	 * The specified randomization is applied to the specified tile.
	 * 
	 * @param tileToWalk
	 *          Tile to walk.
	 * @param x
	 * 			Randomization for tile's x-coordinate
	 * @param y           
	 * 			Randomization for tile's y-coordinate
	 * @return True if successful.
	 */
	public boolean tileOnScreen(final RSTile tileToWalk, final int x, final int y) {
		RSTile tile = new RSTile(tileToWalk.getX() + methods.random(0,x), 
								 tileToWalk.getY() + methods.random(0,y));
		
		if (tile.isOnScreen())  {
			return methods.tile.click(methods.tile.getOnScreen(tile), "Walk ");
		}
		else if (tileToWalk.isOnScreen())  {
			return  methods.tile.click(methods.tile.getOnScreen(tileToWalk), "Walk ");
		}
		
		return false;
	}
	
	/**
	 * Walks to the provided tile by generating the shortest path to it, and
	 * walking along it. This method will immediately return false if progress
	 * is not made in 10 loop iterations. When the destination is reached, true
	 * will be returned. 
	 * 
	 * This method applies a randomization of 2,2.
	 * Use {@link #to(RSTile, int, int) to specify an explicit 
	 * randomization.
	 * 
	 * In order to walk to a destination, you can loop this
	 * method until true is returned. Be careful not to loop this without
	 * returning from the loop() method, otherwise the bot will not be able to
	 * check for random events (i.e. looping this with a nested while() is
	 * discouraged).
	 * 
	 * @param t
	 *            The destination tile.
	 *
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean to(final RSTile t) {
		return to(t, 2, 2);
	}

	/**
	 * Walks to the provided ground item by generating the shortest path to it, and
	 * walking along it. This method will immediately return false if progress
	 * is not made in 10 loop iterations. When the destination is reached, true
	 * will be returned. 
	 * 
	 * This method applies a randomization of 2,2.
	 * Use {@link #to(RSTile, int, int) to specify an explicit 
	 * randomization.
	 * 
	 * In order to walk to a destination, you can loop this
	 * method until true is returned. Be careful not to loop this without
	 * returning from the loop() method, otherwise the bot will not be able to
	 * check for random events (i.e. looping this with a nested while() is
	 * discouraged).
	 * 
	 * @param item
	 *            The destination tile.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean to(final RSGroundItem item) {
		return to(item.getLocation(), 2, 2);
	}
	
	/**
	 * Walks to the provided NPC by generating the shortest path to it, and
	 * walking along it. This method will immediately return false if progress
	 * is not made in 10 loop iterations. When the destination is reached, true
	 * will be returned. 
	 * 
	 * This method applies a randomization of 2,2.
	 * Use {@link #to(RSTile, int, int) to specify an explicit 
	 * randomization.
	 * 
	 * In order to walk to a destination, you can loop this
	 * method until true is returned. Be careful not to loop this without
	 * returning from the loop() method, otherwise the bot will not be able to
	 * check for random events (i.e. looping this with a nested while() is
	 * discouraged).
	 * 
	 * @param npc
	 *            The target npc.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean to(final RSNPC npc) {
		return to(npc.getLocation(), 2, 2);
	}
	
	/**
	 * Walks to the provided player by generating the shortest path to it, and
	 * walking along it. This method will immediately return false if progress
	 * is not made in 10 loop iterations. When the destination is reached, true
	 * will be returned. 
	 * 
	 * This method applies a randomization of 2,2.
	 * Use {@link #to(RSTile, int, int) to specify an explicit 
	 * randomization.
	 * 
	 * In order to walk to a destination, you can loop this
	 * method until true is returned. Be careful not to loop this without
	 * returning from the loop() method, otherwise the bot will not be able to
	 * check for random events (i.e. looping this with a nested while() is
	 * discouraged).
	 * 
	 * @param player
	 *            The target player.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean to(final RSPlayer player) {
		return to(player.getLocation(), 2, 2);
	}

	/**
	 * Walks to the provided object by generating the shortest path to it, and
	 * walking along it. This method will immediately return false if progress
	 * is not made in 10 loop iterations. When the destination is reached, true
	 * will be returned. 
	 * 
	 * This method applies a randomization of 2,2.
	 * Use {@link #to(RSTile, int, int) to specify an explicit 
	 * randomization.
	 * 
	 * In order to walk to a destination, you can loop this
	 * method until true is returned. Be careful not to loop this without
	 * returning from the loop() method, otherwise the bot will not be able to
	 * check for random events (i.e. looping this with a nested while() is
	 * discouraged).
	 * 
	 * @param object
	 *            The target object.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean to(final RSObject object) {
		return to(object.getLocation(), 2, 2);
	}
	
	/**
	 * Walks to the provided tile by generating the shortest path to it, and
	 * walking along it. This method will immediately return false if progress
	 * is not made in 10 loop iterations. When the destination is reached, true
	 * will be returned. In order to walk to a destination, you can loop this
	 * method until true is returned. Be careful not to loop this without
	 * returning from the loop() method, otherwise the bot will not be able to
	 * check for random events (i.e. looping this with a nested while() is
	 * discouraged).
	 * 
	 * @param t
	 *            The destination tile.
	 * @param x
	 *            The x randomness passed to
	 *            {@link #walkTileMM(RSTile, int, int)}.
	 * @param y
	 *            The y randomness passed to
	 *            {@link #walkTileMM(RSTile, int, int)}.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean to(final RSTile t, final int x, final int y) {
		RSPlayer me = methods.player.getMine();
		RSTile dest = methods.walk.getDestination();
		
		// If we are still walking and not within 3-6 tiles of the destination, just return.
		if (me.isMoving() && dest!=null && dest.distanceTo()>methods.random(4,6))  {
			return false;
		}
		
			
			final Point p = methods.tile.toMiniMap(t);
		if (p.x == -1 || p.y == -1) {
			final RSTile[] temp = cleanPath(generateFixedPath(t));
			for (int i = 0; i < 10; i++) {
				if (methods.calculate.distanceTo(temp[temp.length - 1]) < 6) {
					return true;
				}
				final RSTile next = nextTile(temp, 16);
				if (next != null) {
					// If the next tile is closer than the current destination,
					// don't do anything.  This avoids unnecessary spam clicks.
					if (dest!=null && (next.distanceTo() < dest.distanceTo()))  {
						return false;
					}
					
					if (tileMM(next, x, y)) {
						methods.player.waitToMove(methods.random(1400,1700));
						return true;
					}
				} else {
					if (tileMM(nextTile(temp, 20)))  {
						methods.player.waitToMove(methods.random(1400,1700));
					}
				}
				methods.wait(methods.random(200, 400));
			}
			return false;
		}
		methods.mouse.click(p, x, y, true);
		return true;
	}

	/**
	 * Finds the closest tile in the path based on the player's destination.
	 * Walks to the tile by generating the shortest path to it. While this
	 * method is walking, false will be returned. When the destination is
	 * reached, true will be returned.
	 * 
	 * This method applies a randomization of 2,2.  
	 * Use {@link #toClosestTile(RSTile[], int, int) to specify an explicit 
	 * randomization.
	 * 
	 * @param t
	 *            The destination tile.
	 * @param x
	 *            The x randomness passed to {@link #walkTo(RSTile, int, int)}.
	 * @param y
	 *            The y randomness passed to {@link #walkTo(RSTile, int, int)}.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean toClosestTile(final RSTile[] t) {
		return toClosestTile(t, 2, 2);
	}

	/**
	 * Finds the closest tile in the path based on the player's destination.
	 * Walks to the tile by generating the shortest path to it. While this
	 * method is walking, false will be returned. When the destination is
	 * reached, true will be returned.
	 * 
	 * @param t
	 *            The destination tile.
	 * @param x
	 *            The x randomness passed to {@link #walkTo(RSTile, int, int)}.
	 * @param y
	 *            The y randomness passed to {@link #walkTo(RSTile, int, int)}.
	 * @return <tt>true</tt> if the destination was reached; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean toClosestTile(final RSTile[] t, final int x, final int y) {
		final RSTile next = nextTile(t, 16, false);
		final RSTile dest = methods.walk.getDestination();
		
		if (next!=null)  {
			// If the next tile is closer than the current destination,
			// don't do anything.  This avoids unnecessary spam clicks.
			if (dest!=null && (next.distanceTo() < dest.distanceTo()))  {
				return false;
			}
			else  {
				return to(next,x,y);
			}
		}
		
		return false;
	}
}
