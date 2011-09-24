package org.rsbot.script.wrappers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.rsbot.bot.Bot;
import org.rsbot.client.RSGroundEntity;
import org.rsbot.client.RSGroundObject;
import org.rsbot.script.Methods;

/**
 * Represents an item on a tile.
 * 
 * @author Jacmob
 */
public class RSGroundItem {

	private final Methods methods;
	private final RSItem groundItem;
	private final RSTile location;

	public RSGroundItem(final RSTile location, final RSItem groundItem) {
		this.location = location;
		this.groundItem = groundItem;
		this.methods = Bot.methods;
	}

	public RSGroundItem(final int x, final int y, final RSItem groundItem) {
		this.location = new RSTile(x,y);
		this.groundItem = groundItem;
		this.methods = Bot.methods;
	}

	
	/**
	 * Returns the RSItemTile that is equivalent to this RSGroundItem
	 * instance.
	 * 
	 * @deprecated Change to using RSGroundItem instances and methods.
	 */
	@Deprecated
	public RSItemTile getItemTile()  {
		return new RSItemTile(location.getX(), location.getY(), groundItem);
	}
	
	/**
	 * Performs the given action on this RSGroundItem.
	 * 
	 * @param action
	 *            The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>
	 *         .
	 * @see org.rsbot.script.wrappers.RSGroundItem#interact(String)
	 */
	public boolean action(final String action) {
		return action(action, null);
	}

	/**
	 * Performs the given action on this RSGroundItem.
	 * 
	 * @param action
	 *            The menu action to click.
	 * @param option
	 *            The option of the menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>
	 *         .
	 * @see org.rsbot.script.wrappers.RSGroundItem#interact(String, String)
	 */
	public boolean action(final String action, final String option) {
		final RSModel model = getModel();
		if (model != null) {
			return model.action(action, option);
		}
		return methods.tile.click(getLocation(), methods.random(0.45, 0.55),
				methods.random(0.45, 0.55), 0, action, option);
	}

	/**
	 * Gets the item ID of the RSItem for this ground item instance.
	 * @return <tt>int</tt> the ID of the ground item
	 */
	public int getID() {
		return groundItem.getID();
	}

	
	/**
	 * Gets the RSItem for this ground item instance.
	 * @return <tt>RSItem</tt>
	 */
	public RSItem getItem() {
		return groundItem;
	}

	/**
	 * Gets the RSTile of the item.
	 * 
	 * @return <tt>RSTile</tt> in which the item resides.
	 */
	public RSTile getLocation() {
		return location;
	}

	/**
	 * Gets the top model on the tile of this ground item.
	 * 
	 * @return The top model on the tile of this ground item.
	 */
	public RSModel getModel() {
		final int x = location.getX() - Bot.getClient().getBaseX();
		final int y = location.getY() - Bot.getClient().getBaseY();
		final int plane = Bot.getClient().getPlane();
		final org.rsbot.client.RSGround rsGround = Bot.getClient()
		.getRSGroundArray()[plane][x][y];

		if (rsGround != null) {
			final RSGroundEntity obj = rsGround.getGroundObject();
			if (obj != null) {
				final org.rsbot.client.Model model = ((RSGroundObject) rsGround
						.getGroundObject()).getModel();
				if (model != null) {
					return new RSAnimatedModel(Bot.methods, model, obj);
				}
			}
		}
		return null;
	}

	/**
	 * Used to determine whether the object is on the minimap.
	 * 
	 * @return <tt>true</tt> if on the minimap; otherwise <tt>false</tt>.
	 */
	public boolean isOnMinimap() {
		final Point p = 
				methods.calculate.worldToMinimap(this.location.getX(), this.location.getY());
		return (p != null && p.x != -1 && p.y != -1);
	}
	
	/**
	 * Used to determine whether the item is on screen.
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
	 * Returns the distance in tiles from the player to this object.
	 * 
	 * @return Distance in tiles from the player to this object.
	 */
	public int distanceTo() {
		final RSPlayer pl = new RSPlayer(Bot.getClient().getMyRSPlayer());
		final RSTile t = pl.getLocation();

		return (int) Math.hypot(t.getX() - this.location.getX(), t.getY() - this.location.getY());
	}
	
	/**
	 * Gets a screen point for the item.
	 * 
	 * @return <tt>Point</tt> on screen, if not on screen Point(-1,-1)
	 */
	public Point getScreenLocation() {
		return this.getModel().getPoint();
	}
	
	/**
	 * Gets the tile's location on the minimap.
	 * 
	 * @return <b>Point</b> on minimap if visible else <tt>null</tt>
	 */
	public Point getMapLocation() {
		return methods.calculate.worldToMinimap(this.location.getX(), this.location.getY());
	}
	
	/**
	 * Draws the wire-frame of the object on the screen. Supports every loaded
	 * model. Please use in onRepaint()
	 */
	public void drawModel(final Graphics g) {
		if (this.groundItem == null || g == null || this.getModel() == null) {
			return;
		}
		for (final Polygon p : this.getModel().getTriangles()) {
			g.drawPolygon(p);
		}
	}
}
