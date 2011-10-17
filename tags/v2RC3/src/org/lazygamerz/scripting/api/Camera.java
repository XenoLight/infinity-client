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

import java.awt.event.KeyEvent;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * Camera related operations.
 * 
 * @author Runedev development team. - version 1.0
 */
public class Camera {

	private final Methods methods;

	public Camera() {
		methods = Bot.methods;
	}

	/**
	 * Returns the current compass orientation in degrees, with North at 0,
	 * increasing counter-clockwise to 360.
	 * 
	 * @return The current camera angle in degrees.
	 */
	public int getAngle() {
		// the client uses fixed point radians 0 - 2^14
		// degrees = yaw * 360 / 2^14 = yaw / 45.5111...
		return (int) (methods.game.client().getCameraYaw() / 45.51);
	}

	/**
	 * Returns the angle between the current camera angle and the given angle in
	 * degrees.
	 * 
	 * @param degrees
	 *            The target angle.
	 * @return The angle between the who angles in degrees.
	 */
	public int getAngleTo(final int degrees) {
		int ca = getAngle();
		if (ca < degrees) {
			ca += 360;
		}
		int da = ca - degrees;
		if (da > 180) {
			da -= 360;
		}
		return da;
	}

	/**
	 * Returns the camera angle at which the camera would be facing a certain
	 * character.
	 * 
	 * @param n
	 *            the RSCharacter
	 * @return The angle
	 */
	public int getCharacterAngle(final RSCharacter n) {
		return getTileAngle(n.getLocation());
	}

	/**
	 * Returns the angle to a given coordinate pair.
	 * 
	 * @param x2
	 *            X coordinate
	 * @param y2
	 *            Y coordinate
	 * @return angle
	 */
	public int getCordsAngle(final int x2, final int y2) {
		final int x1 = methods.player.getMine().getLocation().getX();
		final int y1 = methods.player.getMine().getLocation().getY();
		final int x = x1 - x2;
		final int y = y1 - y2;
		final double angle = Math.toDegrees(Math.atan2(y, x));
		return ((int) angle) + methods.random(-2, 2);
	}

	/**
	 * Returns the camera angle at which the camera would be facing a certain
	 * object.
	 * 
	 * @param o
	 *            The RSObject
	 * @return The angle
	 */
	public int getObjectAngle(final RSObject o) {
		return getTileAngle(o.getLocation());
	}

	/**
	 * Returns the current percentage of the maximum pitch of the camera in an
	 * open area.
	 * 
	 * @return The current camera altitude percentage.
	 */
	public int getPitch() {
		return (int) ((methods.game.client().getCameraPitch() - 1024) / 20.48);
	}

	/**
	 * Returns the camera angle at which the camera would be facing a certain
	 * tile.
	 * 
	 * @param t
	 *            The target tile
	 * @return The angle in degrees
	 */
	public int getTileAngle(final RSTile t) {
		final int a = (methods.calculate.toTile(t) - 90) % 360;
		return a < 0 ? a + 360 : a;
	}

	/**
	 * Returns the current x position of the camera.
	 * 
	 * @return The x position.
	 */
	public int getX() {
		return methods.game.client().getCamPosX();
	}

	/**
	 * Returns the current y position of the camera.
	 * 
	 * @return The y position.
	 */
	public int getY() {
		return methods.game.client().getCamPosY();
	}

	/**
	 * Returns the current z position of the camera.
	 * 
	 * @return The z position.
	 */
	public int getZ() {
		return methods.game.client().getCamPosZ();
	}

	/**
	 * Moves the camera in a methods.random direction for a given time.
	 * 
	 * @param timeOut
	 *            The maximum time in milliseconds to move the camera for.
	 */
	public void moverandomly(final int timeOut) {
		final Timer timeToHold = new Timer(timeOut);
		final int lowestCamAltPossible = methods.random(75, 100);
		final int vertical = methods.random(0, 20) < 15 ? KeyEvent.VK_UP
				: KeyEvent.VK_DOWN;
		final int horizontal = methods.random(0, 20) < 5 ? KeyEvent.VK_LEFT
				: KeyEvent.VK_RIGHT;
		if (methods.random(0, 10) < 8) {
			methods.input.pressKey((char) vertical);
		}
		if (methods.random(0, 10) < 8) {
			methods.input.pressKey((char) horizontal);
		}
		while (timeToHold.isRunning()
				&& methods.game.client().getCamPosZ() >= lowestCamAltPossible) {
			methods.sleep(10);
		}
		methods.input.releaseKey((char) vertical);
		methods.input.releaseKey((char) horizontal);
	}

	/**
	 * sets the camera altitue all the way up or all the way down based on
	 * script imput.
	 * 
	 * @param setkey
	 */
	public void setAltitude(final boolean setkey) {
		final char key = (char) (setkey ? KeyEvent.VK_UP : KeyEvent.VK_DOWN);
		methods.input.pressKey(key);
		methods.wait(methods.random(1000, 1500));
		methods.input.releaseKey(key);
	}

	/**
	 * Set the camera to a certain percentage of the maximum pitch. Don't rely
	 * on the return value too much - it should return whether the camera was
	 * successfully set, but it isn't very accurate near the very extremes of
	 * the height.
	 * <p/>
	 * <p/>
	 * This also depends on the maximum camera angle in a region, as it changes
	 * depending on situation and surroundings. So in some areas, 68% might be
	 * the maximum altitude. This method will do the best it can to switch the
	 * camera altitude to what you want, but if it hits the maximum or stops
	 * moving for any reason, it will return.
	 * <p/>
	 * <p/>
	 * <p/>
	 * Mess around a little to find the altitude percentage you like. In later
	 * versions, there will be easier-to-work-with methods regarding altitude.
	 * 
	 * @param percent
	 *            The percentage of the maximum pitch to set the camera to.
	 * @return true if the camera was successfully moved; otherwise false.
	 */
	public boolean setAltitude(final double percent) {
		final int alt = (int) (percent / 100 * -1237 - 1226);
		int curAlt = methods.game.client().getCamPosZ();
		int lastAlt = 0;
		if (curAlt == alt) {
			return true;
		} else if (curAlt > alt) {
			methods.input.pressKey((char) KeyEvent.VK_UP);
			long start = System.currentTimeMillis();
			while (curAlt > alt && System.currentTimeMillis() - start < 30) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;

				methods.wait(1);
				curAlt = methods.game.client().getCamPosZ();
			}
			methods.input.releaseKey((char) KeyEvent.VK_UP);
			return true;
		} else {
			methods.input.pressKey((char) KeyEvent.VK_DOWN);
			long start = System.currentTimeMillis();
			while (curAlt < alt && System.currentTimeMillis() - start < 30) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;
				methods.wait(1);
				curAlt = methods.game.client().getCamPosZ();
			}
			methods.input.releaseKey((char) KeyEvent.VK_DOWN);
			return true;
		}
	}

	/**
	 * Rotates the camera to a specific angle in the closest direction.
	 * 
	 * @param degrees
	 *            The angle to rotate to.
	 */
	public void setAngle(final int degrees) {
		if (getAngleTo(degrees) > 5) {
			methods.input.pressKey((char) KeyEvent.VK_LEFT);
			while (getAngleTo(degrees) > 5) {
				methods.sleep(10);
			}
			methods.input.releaseKey((char) KeyEvent.VK_LEFT);
		} else if (getAngleTo(degrees) < -5) {
			methods.input.pressKey((char) KeyEvent.VK_RIGHT);
			while (getAngleTo(degrees) < -5) {
				methods.sleep(10);
			}
			methods.input.releaseKey((char) KeyEvent.VK_RIGHT);
		}
	}

	/**
	 * Rotates the camera to the specified cardinal direction.
	 * 
	 * @param direction
	 *            The char direction to turn the map. char options are w,s,e,n
	 *            and defaults to north if character is unrecognized.
	 */
	public void setCompass(final char direction) {
		switch (direction) {
		case 'n':
			setAngle(359);
			break;
		case 'w':
			setAngle(89);
			break;
		case 's':
			setAngle(179);
			break;
		case 'e':
			setAngle(269);
			break;
		default:
			setAngle(359);
			break;
		}
	}

	/**
	 * Uses the compass component to set the camera to face north.
	 */
	public void setNorth() {
		methods.iface.getChild(methods.screen.getCompass().getID()).click();
	}

	/**
	 * Sets the altitude to max or minimum.
	 * 
	 * @param up
	 *            True to go up. False to go down.
	 * @return <tt>true</tt> if the altitude was changed.
	 */
	public boolean setPitch(final boolean up) {
		if (up) {
			return setPitch(100);
		} else {
			return setPitch(0);
		}
	}

	/**
	 * Set the camera to a certain percentage of the maximum pitch. Don't rely
	 * on the return value too much - it should return whether the camera was
	 * successfully set, but it isn't very accurate near the very extremes of
	 * the height.
	 * <p/>
	 * <p/>
	 * This also depends on the maximum camera angle in a region, as it changes
	 * depending on situation and surroundings. So in some areas, 68% might be
	 * the maximum altitude. This method will do the best it can to switch the
	 * camera altitude to what you want, but if it hits the maximum or stops
	 * moving for any reason, it will return.
	 * <p/>
	 * <p/>
	 * <p/>
	 * Mess around a little to find the altitude percentage you like. In later
	 * versions, there will be easier-to-work-with methods regarding altitude.
	 * 
	 * @param percent
	 *            The percentage of the maximum pitch to set the camera to.
	 * @return true if the camera was successfully moved; otherwise false.
	 */
	public boolean setPitch(final int percent) {
		int curAlt = getPitch();
		int lastAlt = 0;
		if (curAlt == percent) {
			return true;
		} else if (curAlt < percent) {
			methods.input.pressKey((char) KeyEvent.VK_UP);
			long start = System.currentTimeMillis();
			while (curAlt < percent
					&& System.currentTimeMillis() - start < methods.random(50,
							100)) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;
				methods.sleep(methods.random(5, 10));
				curAlt = getPitch();
			}
			methods.input.releaseKey((char) KeyEvent.VK_UP);
			return true;
		} else {
			methods.input.pressKey((char) KeyEvent.VK_DOWN);
			long start = System.currentTimeMillis();
			while (curAlt > percent
					&& System.currentTimeMillis() - start < methods.random(50,
							100)) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;
				methods.sleep(methods.random(5, 10));
				curAlt = getPitch();
			}
			methods.input.releaseKey((char) KeyEvent.VK_DOWN);
			return true;
		}
	}

	/**
	 * Rotates the camera to a specific angle in the closest direction.
	 * 
	 * @param degrees
	 *            The angle to rotate to.
	 */
	public void setRotation(int degrees) {
		final char left = 37;
		final char right = 39;
		char whichDir = left;
		int start = getAngle();
		if (start < 180) {
			start += 360;
		}
		if (degrees < 180) {
			degrees += 360;
		}
		if (degrees > start) {
			if (degrees - 180 < start) {
				whichDir = right;
			}
		} else if (start > degrees) {
			if (start - 180 >= degrees) {
				whichDir = right;
			}
		}
		degrees %= 360;
		methods.input.pressKey(whichDir);
		int timeWaited = 0;
		while (getAngle() > degrees + 5 || getAngle() < degrees - 5) {
			methods.wait(10);
			timeWaited += 10;
			if (timeWaited > 500) {
				final int time = timeWaited - 500;
				if (time == 0) {
					methods.input.pressKey(whichDir);
				} else if (time % 40 == 0) {
					methods.input.pressKey(whichDir);
				}
			}
		}
		methods.input.releaseKey(whichDir);
	}

	/**
	 * Turns the camera to a tile specified by x and y coordinates.
	 * 
	 * @param x
	 *            The tile x coordinate
	 * @param y
	 *            The tile y coordinate
	 */
	public void turnTo(final int x, final int y) {
		turnTo(new RSTile(x, y));
	}

	/**
	 * Turns the camera to within a few degrees of a tile specified by x and y
	 * coordinates.
	 * 
	 * @param x
	 *            The tile x coordinate
	 * @param y
	 *            The tile y coordinate
	 * @param dev
	 *            Maximum difference in angle between actual and chosen
	 *            rotation.
	 */
	public void turnTo(final int x, final int y, final int max) {
		turnTo(new RSTile(x, y), max);
	}

	/**
	 * Turns to a RSCharacter (RSNPC or RSPlayer).
	 * 
	 * @param c
	 *            The RSCharacter to turn to.
	 */
	public void turnTo(final RSCharacter c) {
		final int angle = getCharacterAngle(c);
		setAngle(angle);
	}

	/**
	 * Turns to within a few degrees of an RSCharacter (RSNPC or RSPlayer).
	 * 
	 * @param c
	 *            The RSCharacter to turn to.
	 * @param dev
	 *            The maximum difference in the angle.
	 */
	public void turnTo(final RSCharacter c, final int dev) {
		int angle = getCharacterAngle(c);
		angle = methods.random(angle - dev, angle + dev + 1);
		setAngle(angle);
	}

	/**
	 * Turns to an RSObject.
	 * 
	 * @param o
	 *            The RSObject to turn to.
	 */
	public void turnTo(final RSObject o) {
		final int angle = getObjectAngle(o);
		setAngle(angle);
	}

	/**
	 * Turns to within a few degrees of an RSObject.
	 * 
	 * @param o
	 *            The RSObject to turn to.
	 * @param dev
	 *            The maximum difference in the turn angle.
	 */
	public void turnTo(final RSObject o, final int dev) {
		int angle = getObjectAngle(o);
		angle = methods.random(angle - dev, angle + dev + 1);
		setAngle(angle);
	}

	/**
	 * Turns to a specific RSTile.
	 * 
	 * @param tile
	 *            Tile to turn to.
	 */
	public void turnTo(final RSTile tile) {
		final int angle = getTileAngle(tile);
		setAngle(angle);
	}

	/**
	 * Turns within a few degrees to a specific RSTile.
	 * 
	 * @param tile
	 *            Tile to turn to.
	 * @param dev
	 *            Maximum deviation from the angle to the tile.
	 */
	public void turnTo(final RSTile tile, final int dev) {
		int angle = getTileAngle(tile);
		angle = methods.random(angle - dev, angle + dev + 1);
		setAngle(angle);
	}
}
