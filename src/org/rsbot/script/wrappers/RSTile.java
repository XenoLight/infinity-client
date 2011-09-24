package org.rsbot.script.wrappers;

import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSCharacter.Orientation;

/**
 * @version 1.1 - 04/25/2011 Cleaned it up. - Henry
 */
public class RSTile {

	private final Methods methods;
	private final int x;
	private final int y;

	public RSTile(final int x, final int y) {
		methods = Bot.methods;
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the distance in tiles from the player to this tile.
	 * 
	 * @return Distance in tiles from the player to this tile.
	 */
	public int distanceTo() {
		final RSPlayer pl = new RSPlayer(Bot.getClient().getMyRSPlayer());
		final RSTile t = pl.getLocation();

		return (int) Math.hypot(t.getX() - this.x, t.getY() - this.y);
	}

	/**
	 * Returns the distance in tiles from this tile to the specified tile.
	 * 
	 * @return Distance in tiles from the player to this tile.
	 */
	public int distanceTo(final RSTile t) {
		return Calculations.distanceBetween(this, t);
	}

	/**
	 * @inheritDoc java/lang/Object#equals(java/lang/Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RSTile) {
			final RSTile tile = (RSTile) obj;
			return (tile.x == x) && (tile.y == y);
		}
		return false;
	}

	/**
	 * 
	 * Gets the Orientation of angles in degrees
	 * 
	 * Honestly couldn't find a better place for this :E
	 * 
	 * @param angle
	 * @return Orientation from degrees
	 */
	private Orientation getOrientationFromAngle(double angle) {

		angle += 180;

		if (angle > 340 || angle <= 20) {
			return Orientation.West;
		} else if (angle > 295) {
			return Orientation.NorthWest;
		} else if (angle > 250) {
			return Orientation.North;
		} else if (angle > 205) {
			return Orientation.NorthEast;
		} else if (angle > 155) {
			return Orientation.East;
		} else if (angle > 110) {
			return Orientation.SouthEast;
		} else if (angle > 175) {
			return Orientation.South;
		} else if (angle > 20) {
			return Orientation.SouthWest;
		}
		return null;

	}

	/**
	 * 
	 * Used to get the <b>Orientation</b> to this tile
	 * 
	 * @param from
	 *            Tile to calculate Orientation from
	 * @return the orientation to this tile from parameter "from" tile
	 */
	public Orientation getOrientationTo(final RSTile from) {
		return getOrientationFromAngle(Math.toDegrees(Math.atan2(
				(getY() - from.getY()), (getX() - from.getX()))));
	}

	/**
	 * Gets the tile's location on screen.
	 * 
	 * @return <b>Point</b> on screen if visible else <tt>null</tt>
	 */
	public Point getScreenLocation() {
		return Calculations.tileToScreen(this, 0);
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
	 * Turns to this tile.
	 */
	public void turnTo() {
		final int angle = getAngle();
		methods.camera.setAngle(angle);
	}

	/**
	 * Returns the camera angle at which the camera would be facing a certain
	 * tile.
	 * 
	 * @param t
	 *            The target tile
	 * @return The angle in degrees
	 */
	public int getAngle() {
		final int a = (methods.calculate.toTile(this) - 90) % 360;
		return a < 0 ? a + 360 : a;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	/**
	 * Used to determine whether the tile is on the minimap.
	 * 
	 * @return <tt>true</tt> if on the minimap; otherwise <tt>false</tt>.
	 */
	public boolean isOnMinimap() {
		final Point p = methods.calculate.worldToMinimap(this.x, this.y);
		return (p != null && p.x != -1 && p.y != -1);
	}

	/**
	 * Used to determine whether the tile is on screen.
	 * 
	 * @return <tt>true</tt> if on screen; otherwise <tt>false</tt>.
	 */
	public boolean isOnScreen() {
		final Point p = Calculations.tileToScreen(new RSTile(this.x, this.y), 0);
		return (p.x > 0 && p.y > 0);
	}

	/**
	 * Used to determine whether this tile is reachable.
	 * 
	 * @return <tt>true</tt> if this tile is reachable; otherwise <tt>false</tt>
	 *         .
	 */
	public boolean isReachable() {
		return Calculations.canReach(this, true);
	}

	/**
	 * Used to see if <b>RSTile</b> is valid.
	 * 
	 * @return <tt>True</tt> if valid otherwise <tt>False</tt>
	 */
	public boolean isValid() {
		return (x != -1) && (y != -1);
	}

	/**
	 * Randomize the tile.
	 * 
	 * @param maxXDeviation
	 *            Max X distance from tile x.
	 * @param maxYDeviation
	 *            Max Y distance from tile y.
	 * @return The randomized <b>RSTile</b>
	 */
	public RSTile randomizeTile(final int maxXDeviation, final int maxYDeviation) {
		int x = getX();
		int y = getY();
		if (maxXDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxXDeviation;
			x += (int) d;
		}
		if (maxYDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxYDeviation;
			y += (int) d;
		}
		return new RSTile(x, y);
	}

	/**
	 * Used to get <b>RSTile</b> coordinates in a <b>String</b>
	 * 
	 * @return <b>String</b> that contains x and y coordinates of <b>RSTile</b>.
	 */
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
