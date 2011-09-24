package org.rsbot.script.wrappers;

import java.awt.Point;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.client.Model;
import org.rsbot.client.Node;
import org.rsbot.client.RSMessageData;
import org.rsbot.client.RSNPCNode;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.InputManager;
import org.rsbot.script.Methods;

public class RSCharacter {

	public static enum Orientation {
		North(8192), NorthEast(10240), East(12288), SouthEast(14336), South(0), SouthWest(
				2048), West(4096), NorthWest(6144);

		public static Orientation getOrientation(final int intor) {
			for (final Orientation or : values()) {
				if (or.getOrientationId() == intor)
					return or;
			}
			return null;
		}

		private int orientation;

		private Orientation(final int orientation) {
			this.orientation = orientation;
		}

		public int getOrientationId() {
			return orientation;
		}
	}
	
	public final InputManager input = Bot.getInputManager();
	public final Methods methods = Bot.methods;

	protected org.rsbot.client.RSCharacter c;
	protected org.rsbot.client.RSPlayer p=null;
	protected org.rsbot.client.RSNPC n=null;


	public RSCharacter(final org.rsbot.client.RSCharacter c) {
		this.c = c;
		
		if (c instanceof org.rsbot.client.RSPlayer)  {
			p = (org.rsbot.client.RSPlayer)c;
		}
		else if (c instanceof org.rsbot.client.RSNPC)  {
			n = (org.rsbot.client.RSNPC)c;
		}
	}

	/**
	 * Left or Right clicks on this character.
	 * 
	 * @param leftClick
	 *            <tt>true</tt> for left click, <tt>false</tt> for right click.
	 * @return <tt>true</tt> if successful, <tt>false</tt> otherwise.
	 */
	public boolean action(final boolean leftClick) {
		return this.action(null);
	}

	/**
	 * Performs an action on the humanoid character instance (tall and skinny)
	 * without any randomly generated mousepaths.
	 * 
	 * @param option
	 *            The option to be clicked (If available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 * @see #do(RSNPC, String, boolean)
	 */
	public boolean action(final String option) {
		return this.action(option, false);
	}

	/**
	 * Performs an action on the humanoid character instance (tall and skinny).
	 * 
	 * @param option
	 *            The option to be clicked (If available). If null, simply left
	 *            clicks the NPC.
	 * @param mousepath
	 *            Whether or not to use {@link #mouse.moveByPath(Point)} rather
	 *            than {@link #mouse.move(Point)}.
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 * @see #mouse.moveByPath(Point)
	 * @see #atMenu(String)
	 */
	public boolean action(final String option, final boolean mousepath) {
		for (int i = 0; i < 10; i++) {
			if (!this.isOnScreen()) {
				return false;
			}

			if (!mousepath) {
				methods.mouse.move(new Point((int) Math.round(this
						.getScreenLocation().getX()) + methods.random(-5, 5),
						(int) Math.round(this.getScreenLocation().getY())
						+ methods.random(-5, 5)));
			} else {
				methods.mouse.move(new Point((int) Math.round(this
						.getScreenLocation().getX()) + methods.random(-5, 5),
						(int) Math.round(this.getScreenLocation().getY())
						+ methods.random(-5, 5)));
			}

			if (option == null) {
				methods.mouse.click(true);
				return true;
			} else {
				if (methods.menu.action(option)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Performs an action on a humanoid character (tall and skinny).
	 * 
	 * @param action
	 *            The action of the menu entry to be clicked (if available).
	 * @param option
	 *            The option of the menu entry to be clicked (if available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 */
	public boolean action(final String action, final String option) {
		if (isValid()) {
			final RSModel model = getModel();
			if (model != null) {
				return model.action(action, option);
			}
			try {
				Point screenLoc;
				for (int i = 0; i < 10; i++) {
					screenLoc = getScreenLocation();
					if (!isValid()
							|| !methods.calculate.pointOnScreen(screenLoc)) {
						break;
					}
					if (!methods.mouse.getLocation().equals(screenLoc)
							&& methods.menu.action(action, option)) {
						return true;
					}
					methods.mouse.move(screenLoc);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * Use action();
	 */
	@Deprecated
	public boolean click(final boolean leftClick) {
		return this.action(null);
	}

	/**
	 * Obtains the distance in tiles from the current player to the character.
	 * 
	 * @return Distance in tiles from the current player to the character.
	 */
	public int distanceTo() {
		return this.getLocation().distanceTo();
	}

	/**
	 * @inheritDoc java/lang/Object#equals(java/lang/Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof org.rsbot.script.wrappers.RSCharacter) {
			final org.rsbot.script.wrappers.RSCharacter cha = (org.rsbot.script.wrappers.RSCharacter) obj;
			return cha.c == c;
		}
		return false;
	}

	/**
	 * Returns the angle to this character
	 * 
	 * @return The angle
	 */
	public int getAngle() {
		final RSTile loc = this.getLocation();
		return methods.camera.getCordsAngle(loc.getX(), loc.getY());
	}

	/**
	 * @return animation of character as integer
	 */
	public int getAnimation() {
		final int[] q = c.getAnimationQueue();
                return q == null || q.length == 0 ? -1 : q[0];
	}

	public int getGraphic() {
		return c.getGraphicsData()[0].getID();
	}

	public int getHeight() {
		return c.getHeight();
	}

	/**
	 * Returns the % of HP. Returns 100 if not in combat.
	 */
	public int getHPPercent() {
		return isInCombat() ? c.getHPRatio() * 100 / 255 : 100;
	}

	public RSCharacter getInteracting() {

		final int interact = c.getInteracting();
		if (interact == -1) {
			return null;
		}

		if (interact < 32768) {
			final Node localNode = Calculations.findNodeByID(Bot.getClient()
					.getRSNPCNC(), interact);
			if (localNode == null || !(localNode instanceof RSNPCNode)) {
				return null;
			}
			return new RSNPC(((RSNPCNode) localNode).getRSNPC());
		} else if (interact >= 32768) {
			int index = interact - 32768;
			if (index == Bot.getClient().getSelfInteracting()) {
				index = 2047;
			}

			return new org.rsbot.script.wrappers.RSPlayer(Bot.getClient()
					.getRSPlayerArray()[index]);
		}

		return null;
	}

	/**
	 * @Deprecated Returns -1
	 */
	@Deprecated
	public int getLevel() {
		return -1; // should be overridden as well
	}

	public RSTile getLocation() {
		if (c == null) {
			return new RSTile(-1, -1);
		}
		final int x = Bot.getClient().getBaseX() + (c.getX() >> 9);
		final int y = Bot.getClient().getBaseY() + (c.getY() >> 9);
		return new RSTile(x, y);
	}

	public String getMessage() {
		final RSMessageData messageData = c.getMessageData();
		return messageData != null ? messageData.getMessage() : null;
	}

	/**
	 * Gets the tile's location on the minimap.
	 * 
	 * @return <b>Point</b> on minimap if visible else <tt>null</tt>
	 */
	public Point getMapLocation() {
		final RSTile t = getLocation();
		
		/*
		 * final int cX = Bot.getClient().getBaseX() + (c.getX() / 32 - 2) / 4;
		 * final int cY = Bot.getClient().getBaseY() + (c.getY() / 32 - 2) / 4;
		 */
		return methods.calculate.worldToMinimap(t.getX(),t.getY());
	}
	
	/**
	 * Get's the minimap location, of the character. Note: This does work when
	 * it's walking!
	 * 
	 * @return The location of the character on the minimap.
	 * 
	 * @deprecated use getMapLocation()
	 */
	@Deprecated
	public Point getMinimapLocation() {
		return this.getMapLocation();
	}

	/**
	 * Character model if available.
	 * 
	 * @return <b>RSModel</b> if available else null
	 */
	public RSModel getModel() {
		if (c != null) {
			final Model model = c.getModel();
			if (model != null) {
				return new RSCharacterModel(methods, model, c);
			}
		}
		return null;
	}

	/**
	 * Gets the name of the RSCharacter.
	 * 
	 * @return name of the character if it is a player or NPC, "UNDEFINED" otherwise.
	 */
	public String getName() {
		String name = null;
		
		if (p!=null)  {
			name = p.getName();
		}
		else if (n!=null)  {
			final org.rsbot.client.RSNPCDef def = n.getRSNPCDef();
			if (def != null) {
				name = def.getName();
			}
		}
		
		if (name==null)  {
			name = "UNDEFINED";
		}
		
		return name;
	}

	/**
	 * @return Orientation aka turn direction
	 */
	public Orientation getOrientation() {
		return Orientation.getOrientation(c.getOrientation());
	}

	/**
	 * Character point on screen.
	 * 
	 * @return <b>Point</b> on screen, if not on screen null
	 */
	public Point getScreenLocation() {
		final RSModel model = getModel();
		if (model == null) {
			return Calculations.worldToScreen(c.getX(), c.getY(),
					c.getHeight() / 2);
		} else {
			return model.getPoint();
		}
	}

	public int getTurnDirection() {
		return c.getOrientation();
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(c);
	}

	/**
	 * Checks to see if the Player is in Combat
	 * 
	 * @return <tt>True</tt> if is in combat, otherwise <tt>False</tt>
	 */
	public boolean isInCombat() {
		if (!methods.game.isLoggedIn()) {
			return false;
		}
				
		return methods.game.isLoggedIn() && getInteracting()!=null
				&& Bot.getClient().getLoopCycle() < c.getLoopCycleStatus();
	}

	public boolean isInteractingWithLocalPlayer() {
		return c.getInteracting() - 32768 == Bot.getClient()
		.getSelfInteracting();
	}

	/**
	 * Checks if character is moving.
	 * 
	 * @return <tt>true</tt> if moving, <tt>false</tt> otherwise
	 */
	public boolean isMoving() {
		return c.isMoving() != 0;
	}

	/**
	 * Checks if character is on minimap.
	 * 
	 * @return <tt>true</tt> if on minimap, <tt>false</tt> otherwise
	 */
	public boolean isOnMinimap() {
		final RSTile loc = this.getLocation();
		return (loc != null);
	}

	/**
	 * Checks if character is on screen.
	 * 
	 * @return <tt>true</tt> if on screen, <tt>false</tt> otherwise
	 */
	/**
	 * Used to determine whether the object is on screen.
	 * 
	 * @return <tt>true</tt> if on screen; otherwise <tt>false</tt>.
	 */
	public boolean isOnScreen() {
		final RSModel model = getModel();
		
		if (model == null) {
			return getLocation().isOnScreen();
		} else {
			return methods.calculate.pointOnScreen(model.getPoint());
		}
	}
	/**
	 * Checks to see if the Player is Poisoned
	 * 
	 * @return <tt>True</tt> if is poisoned, otherwise <tt>False</tt>
	 */
	public boolean isPoisoned() {
		if (!methods.game.isLoggedIn()) {
			return false;
		}
		return methods.game.isLoggedIn() && methods.settings.get(102) > 0
		|| methods.iface.getChild(748, 4).getBackgroundColor() == 1801;
	}

	public boolean isValid() {
		return c != null;
	}

	@Override
	public String toString() {
		final RSCharacter inter = getInteracting();
		return "[anim="
		+ getAnimation()
		+ ",msg="
		+ getMessage()
		+ ",interact="
		+ (inter == null ? "null" : inter.isValid() ? inter
				.getMessage() : "Invalid") + "]";
	}

	/**
	 * Turns to the character with a random deviation of 2 degrees
	 */
	public void turnTo() {
		final int angle = this.getAngle() + methods.random(-2, 2);
		methods.camera.setRotation(angle);
	}

	/**
	 * Uses the specified inventory item on this character.
	 * 
	 * @param item
	 * @return <tt>true</tt> if successful, <tt>false</tt> otherwise
	 */
	public boolean useItemOn(final RSItem item) {
		if (methods.game.getCurrentTab() != Constants.TAB_INVENTORY) {
			methods.game.openTab(Constants.TAB_INVENTORY);
		}

		return methods.inventory.clickItem(item.getID(), "Use")
		&& this.action(true);
	}
}
