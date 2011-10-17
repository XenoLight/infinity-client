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
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

/**
 * player such as self in game.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class Player {

	private final Methods methods;
	protected final Logger log = Logger.getLogger(this.getClass().getName());

	public Player() {
		this.methods = Bot.methods;
	}

	/**
	 * Clicks on the player with specified action Walks to the player if not on
	 * screen.
	 * 
	 * @param c
	 *            The RSCharacter you want to click.
	 * @param act
	 *            Action command to use on the Character (e.g "Attack" or
	 *            "Trade").
	 * @return<tt>true</tt> if the Character was clicked; otherwise
	 *                      <tt>false</tt>.
	 */
	public boolean action(final RSCharacter c, final String act) {
		try {
			Point p;
			for (int i = 0; i < 20; i++) {
				p = c.getScreenLocation();
				if (!c.isValid() || !Calculations.onScreen(p)) {
					log.warning("Not on screen " + act);
					return false;
				}
				if (methods.mouse.getLocation().equals(p)) {
					break;
				}
				methods.mouse.move(p);
			}
			p = c.getScreenLocation();
			if (!methods.mouse.getLocation().equals(p))
				return false;
			final String[] items = methods.menu.getItems();
			if (items.length <= 1)
				return false;
			if (items[0].toLowerCase().contains(act.toLowerCase())) {
				methods.mouse.click(p, true);
				return true;
			}
			methods.mouse.click(p, false);
			return methods.menu.action(act);
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Checks if the player's animation is one of the IDs sent.
	 * 
	 * @param ids
	 *            The animation IDs to check.
	 * @return <tt>true</tt> if the animation is in the array; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean animationIs(final int... ids) {
		for (final int id : ids) {
			if (getMine().getAnimation() == id)
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param name
	 * @return Loaded player with given name
	 */
	public RSPlayer getByName(final String name) {
		return getNearest(new Filter<RSPlayer>() {
			@Override
			public boolean accept(final RSPlayer t) {
				return t.getName().equals(name);
			}
		});
	}

	public RSPlayer[] getLoaded() {
		return getLoaded(new Filter<RSPlayer>() {
			@Override
			public boolean accept(final RSPlayer t) {
				return true;
			}
		});
	}

	public RSPlayer[] getLoaded(final Filter<RSPlayer> filter) {
		final int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		final org.rsbot.client.RSPlayer[] players = Bot.getClient()
		.getRSPlayerArray();
		final ArrayList<RSPlayer> playerList = new ArrayList<RSPlayer>();

		for (final int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			final RSPlayer player = new RSPlayer(players[element]);
			if (!filter.accept(player)) {
				continue;
			}
			playerList.add(player);
		}

		return playerList.toArray(new RSPlayer[0]);
	}

	/**
	 * gets the tile of the the logged in player
	 */
	public RSTile getMyLocation() {
		return getMine().getLocation();
	}
	
	/**
	 * gets the tile of the the login
	 */
	/**
	 * @deprecated use getMyLocation;
	 */
	public RSTile getLocation() {
		return getMine().getLocation();
	}

	/**
	 * Returns an RSPlayer object representing the current player.
	 * 
	 * @return RSPlayer object representing the player.
	 */
	public RSPlayer getMine() {
		return new RSPlayer(methods.game.client().getMyRSPlayer());
	}

	public int getMyEnergy() {
		try {
			return Integer.parseInt(methods.iface.getChild(750, 5).getText());
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * 
	 * Gets the nearest player that passes the filter
	 * 
	 * @param filter
	 *            Filter to use to filter players
	 * @return Nearest player with accepted filter
	 */
	public RSPlayer getNearest(final Filter<RSPlayer> filter) {
		int dist = Integer.MAX_VALUE;
		RSPlayer closest = null;
		final int[] validPlayers = methods.game.client()
		.getRSPlayerIndexArray();
		final org.rsbot.client.RSPlayer[] players = methods.game.client()
		.getRSPlayerArray();

		for (final int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			final RSPlayer player = new RSPlayer(players[element]);
			try {
				if (!filter.accept(player)) {
					continue;
				}
				final int distance = methods.calculate.distanceTo(player);
				if (distance < dist) {
					dist = distance;
					closest = player;
				}
			} catch (final Exception ignored) {
			}
		}
		return closest;
	}

	/**
	 * 
	 * @param level
	 * @return Nearest player with given combat level
	 */
	public RSPlayer getNearestByLevel(final int level) {
		return getNearest(new Filter<RSPlayer>() {
			@Override
			public boolean accept(final RSPlayer t) {
				return t.getCombatLevel() == level;
			}
		});
	}

	/**
	 * 
	 * @param min
	 * @param max
	 * @return Nearest player with combat level in between min and max
	 */
	public RSPlayer getNearestByLevel(final int min, final int max) {
		return getNearest(new Filter<RSPlayer>() {
			@Override
			public boolean accept(final RSPlayer t) {
				final int lvl = t.getCombatLevel();
				return lvl >= min && lvl <= max;
			}
		});
	}

	public boolean isCarryingItem(final int... ids) {
		return methods.equipment.contains(ids) || methods.inventory.contains(ids);
	}

	/**
	 * Checks whether or not the player is currently idle.
	 * 
	 * @return <tt>true</tt> if the player is neither moving nor performing an
	 *         animation; otherwise <tt>false</tt>.
	 */
	public boolean isIdle() {
		RSPlayer me = getMine();
		
		if (me==null)  {
			return false;
		}
		
		int anim = me.getAnimation();
		
		// Note that sometimes after logging out and in the animation queue has all
		// elements set to 250.  This amounts to being idle, so we added it to this
		// evaluation.
		return !me.isMoving() && (anim == -1);
	}

	/**
	 * @return <tt>true</tt> if run mode is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isRunning() {
		return methods.settings.get(173) == 1;
	}

	/**
	 * Rests until 100% energy
	 * 
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 * @see #rest(int)
	 */
	public boolean rest() {
		return rest(100);
	}

	/**
	 * Rests until a certain amount of energy is reached.
	 * 
	 * @param amount
	 *            Amount of energy at which it should stop resting.
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 */
	public boolean rest(final int amount) {
		int energy = methods.player.getMyEnergy();
		for (int d = 0; d < 5; d++) {
			methods.iface.clickChild(Game.INTERFACE_RUN_ORB, 1, "Rest");
			methods.mouse.moveSlightly();
			methods.wait(methods.random(400, 600));
			if (methods.player.getMine().getAnimation() == 12108
					|| methods.player.getMine().getAnimation() == 2033
					|| methods.player.getMine().getAnimation() == 2716
					|| methods.player.getMine().getAnimation() == 11786
					|| methods.player.getMine().getAnimation() == 5713
					|| methods.player.getMine().getAnimation() == 2230) {
				break;
			}
			if (d == 4) {
				// log("Rest failed!");
				return false;
			}
		}
		while (energy < amount) {
			methods.wait(methods.random(250, 500));
			energy = methods.player.getMyEnergy();
		}
		return true;
	}

	/**
	 * Waits up to timeout milliseconds for an animation to trigger. Will return
	 * the instant an animation begins.
	 * 
	 * @param ms
	 *            Maximum time to wait for an animation (in milliseconds).
	 * @return The animation if an animation triggered, or -1 if there was no
	 *         animation.
	 */
	public int waitForAnim(final int ms) {
		final long start = System.currentTimeMillis();
		final RSPlayer myPlayer = getMine();
		int anim = -1;

		while (System.currentTimeMillis() - start < ms) {
			if ((anim = myPlayer.getAnimation()) != -1) {
				break;
			}
			methods.wait(30);
		}
		return anim;
	}

	/**
	 * Waits up to timeout milliseconds to start moving. This will return the
	 * instant movement starts. You can handle waiting a random amount
	 * afterwards by yourself.
	 * 
	 * @param ms
	 *            Maximum time to wait to start moving (in milliseconds).
	 * @return True if we started moving, false if we reached the timeout.
	 */
	public boolean waitToMove(final int ms) {
		final long start = System.currentTimeMillis();
		final RSPlayer myPlayer = getMine();
		while (System.currentTimeMillis() - start < ms) {
			if (myPlayer.isMoving())
				return true;
			methods.sleep(2);
		}
		return false;
	}
}