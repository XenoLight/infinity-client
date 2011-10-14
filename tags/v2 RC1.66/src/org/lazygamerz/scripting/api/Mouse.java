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
import java.util.Arrays;
import java.util.Vector;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.MouseHandler;

/**
 * Use of the mouse on your computer for the game.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class Mouse {

	private final Methods methods;
	private int mouseSpeed = MouseHandler.DEFAULT_MOUSE_SPEED;

	public Mouse() {
		this.methods = Bot.methods;
	}

	/**
	 * Clicks the mouse at its current location.
	 * 
	 * @param leftClick
	 *            <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 */
	public void click(final boolean leftClick) {
		click(leftClick, MouseHandler.DEFAULT_MAX_MOVE_AFTER);
	}

	public synchronized void click(final boolean leftClick, final int moveAfter) {
		methods.input.clickMouse(leftClick);
		if (moveAfter > 0) {
			methods.sleep(methods.random(50, 350));
			final Point pos = getLocation();
			move(pos.x - moveAfter, pos.y - moveAfter, moveAfter * 2,
					moveAfter * 2);
		}
	}

	/**
	 * Moves the mouse to a given location then clicks.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param leftClick
	 *            <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 */
	public void click(final int x, final int y, final boolean leftClick) {
		click(x, y, 0, 0, leftClick);
	}

	/**
	 * Moves the mouse to a given location with given randomness then clicks.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param randX
	 *            x randomness (added to x)
	 * @param randY
	 *            y randomness (added to y)
	 * @param leftClick
	 *            <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 * @see #move(int, int, int, int)
	 */
	public synchronized void click(final int x, final int y, final int randX,
			final int randY, final boolean leftClick) {
		move(x, y, randX, randY);
		methods.sleep(methods.random(50, 350));
		click(leftClick, MouseHandler.DEFAULT_MAX_MOVE_AFTER);
	}

	/**
	 * Moves the mouse to a given location with given randomness then clicks,
	 * then moves a random distance up to <code>afterOffset</code>.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param randX
	 *            x randomness (added to x)
	 * @param randY
	 *            y randomness (added to y)
	 * @param leftClick
	 *            <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 * @param moveAfterDist
	 *            The maximum distance in pixels to move on both axes shortly
	 *            after moving to the destination.
	 */
	public synchronized void click(final int x, final int y, final int randX,
			final int randY, final boolean leftClick, final int moveAfterDist) {
		move(x, y, randX, randY);
		methods.sleep(methods.random(50, 350));
		click(leftClick, moveAfterDist);
	}

	/**
	 * Moves the mouse to a given location then clicks.
	 * 
	 * @param p
	 *            The point to click.
	 * @param leftClick
	 *            <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 */
	public void click(final Point p, final boolean leftClick) {
		click(p.x, p.y, leftClick);
	}

	public void click(final Point p, final int x, final int y,
			final boolean leftClick) {
		click(p.x, p.y, x, y, leftClick);
	}

	/**
	 * Moves the mouse to a given location with given randomness then clicks,
	 * then moves a random distance up to <code>afterOffset</code>.
	 * 
	 * @param p
	 *            The destination Point.
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param leftClick
	 *            <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 * @param moveAfterDist
	 *            The maximum distance in pixels to move on both axes shortly
	 *            after moving to the destination.
	 */
	public void click(final Point p, final int x, final int y,
			final boolean leftClick, final int moveAfterDist) {
		click(p.x, p.y, x, y, leftClick, moveAfterDist);
	}

	/**
	 * Moves the mouse slightly depending on where it currently is and clicks.
	 */
	public void clickSlightly() {
		final Point p = new Point(
				(int) (getLocation().getX() + (Math.random() * 50 > 25 ? 1 : -1)
						* (30 + Math.random() * 90)), (int) (getLocation()
								.getY() + (Math.random() * 50 > 25 ? 1 : -1)
								* (30 + Math.random() * 90)));
		if (p.getX() < 1 || p.getY() < 1 || p.getX() > 761 || p.getY() > 499) {
			clickSlightly();
			return;
		}
		click(p, true);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 * 
	 * @param x
	 *            The x coordinate to drag to.
	 * @param y
	 *            The y coordinate to drag to.
	 */
	public void drag(final int x, final int y) {
		methods.input.dragMouse(x, y);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 * 
	 * @param p
	 *            The point to drag to.
	 * @see #drag(int, int)
	 */
	public void drag(final Point p) {
		drag(p.x, p.y);
	}

	/**
	 * Generates a randomized point array, used as a mouse path.
	 * 
	 * @param amount
	 *            The amount of points for the mouse path to contain, including
	 *            the destination.
	 * @param end
	 *            The destination.
	 * @return A point array, used as a mouse path or null if failed (Most
	 *         likely because the amount was negative or 0). If you enter 1 as
	 *         amount, the return would be the destination.
	 */
	public Point[] generatePath(final int amount, final Point end) {
		try {
			if (amount <= 0) {
				return null;
			}
			final Point[] path = new Point[amount];
			final Point curPos = getLocation();
			for (int i = 0; i < path.length; i++) {
				path[i] = new Point();
				if (i == path.length - 1) {
					path[i].setLocation(end);
				} else if (i != 0) {
					path[i].setLocation(
							path[i - 1].x > end.x ? methods.random(end.x,
									path[i - 1].x) : methods.random(
											path[i - 1].x, end.x),
											path[i - 1].y > end.y ? methods.random(end.y,
													path[i - 1].y) : methods.random(
															path[i - 1].y, end.y));
				} else {
					path[i].setLocation(
							curPos.x > end.x ? methods.random(end.x, curPos.x)
									: methods.random(curPos.x, end.x),
									curPos.y > end.y ? methods.random(end.y, curPos.y)
											: methods.random(curPos.y, end.y));
				}
			}
			final Vector<Point> unsorted = new Vector<Point>();
			unsorted.addAll(Arrays.asList(path));
			final Vector<Point> sorted = new Vector<Point>();
			for (final Point element : path) {
				if (element == null) {
					continue;
				}
				int b = 0;
				int dist = 0;
				for (int a = 0; a < unsorted.size(); a++) {
					if ((int) Math.hypot(unsorted.get(a).getX() - end.getX(),
							unsorted.get(a).getY() - end.getY()) >= dist) {
						dist = (int) Math.hypot(
								unsorted.get(a).getX() - end.getX(), unsorted
								.get(a).getY() - end.getY());
						b = a;
					}
				}
				sorted.add(unsorted.get(b));
				unsorted.remove(b);
			}
			final Point[] Path = new Point[sorted.size()];
			for (int i = 0; i < sorted.size(); i++) {
				Path[i] = sorted.get(i);
			}
			return Path;
		} catch (final Exception e) {
		}
		return null;
	}

	/**
	 * The location of the bot mouse; or Point(-1, -1) if off screen.
	 * 
	 * @return A <tt>Point</tt> containing the bot mouse x & y coordinates.
	 */
	public Point getLocation() {
		methods.game.client().getMouse();
		return new Point(methods.input.getX(), methods.input.getY());
	}

	/**
	 * @return <tt>true</tt> if Sweed_Raver's mouse methods should be used by
	 *         default; otherwise <tt>false</tt>.
	 */
	public boolean getPath() {
		return !MouseHandler.DEFAULT_MOUSE_PATH;
	}

	/**
	 * @return The <tt>Point</tt> at which the bot mouse was last clicked.
	 */
	public Point getPressLocation() {
		final org.rsbot.client.input.Mouse m = methods.game.client().getMouse();
		return new Point(m.getPressX(), m.getPressY());
	}

	/**
	 * @return The system time when the bot mouse was last pressed.
	 */
	public long getPressTime() {
		final org.rsbot.client.input.Mouse mouse = methods.game.client()
		.getMouse();
		return mouse == null ? 0 : mouse.getPressTime();
	}

	/**
	 * @param max
	 *            The maximum distance outwards.
	 * @return A random x value between the current client location and the max
	 *         distance outwards.
	 */
	public int getRandomX(final int max) {
		final Point p = getLocation();
		if (p.x < 0 || max <= 0) {
			return -1;
		}
		if (methods.random(0, 2) == 0) {
			return p.x - methods.random(0, p.x < max ? p.x : max);
		} else {
			final int dist = methods.game.getWidth() - p.x;
			return p.x + methods.random(1, dist < max && dist > 0 ? dist : max);
		}
	}

	/**
	 * @param max
	 *            The maximum distance outwards.
	 * @return A random y value between the current client location and the max
	 *         distance outwards.
	 */
	public int getRandomY(final int max) {
		final Point p = getLocation();
		if (p.y < 0 || max <= 0) {
			return -1;
		}
		if (methods.random(0, 2) == 0) {
			return p.y - methods.random(0, p.y < max ? p.y : max);
		} else {
			final int dist = methods.game.getHeight() - p.y;
			return p.y + methods.random(1, dist < max && dist > 0 ? dist : max);
		}
	}

	/**
	 * Gets the mouse speed.
	 * 
	 * @return the current mouse speed.
	 * @see #setSpeed(int)
	 */
	public int getSpeed() {
		return mouseSpeed;
	}

	/**
	 * Hops mouse to the specified coordinate.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate
	 */
	public synchronized void hop(final int x, final int y) {
		methods.input.hopMouse(x, y);
	}

	/**
	 * Hops mouse to the certain coordinate.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param randX
	 *            The x coordinate randomization.
	 * @param randY
	 *            The y coordinate randomization.
	 * @see #hop(int, int)
	 */
	public void hop(final int x, final int y, final int randX, final int randY) {
		hop(x + methods.random(-randX, randX),
				y + methods.random(-randX, randY));
	}

	/**
	 * Hops mouse to the specified point.
	 * 
	 * @param p
	 *            The coordinate point.
	 * @see #hop(Point)
	 */
	public void hop(final Point p) {
		hop(p.x, p.y);
	}

	/**
	 * Hops mouse to the certain point.
	 * 
	 * @param p
	 *            The coordinate point.
	 * @param randX
	 *            The x coordinate randomization.
	 * @param randY
	 *            The y coordinate randomization.
	 * @see #hop(int, int, int, int)
	 */
	public void hop(final Point p, final int randX, final int randY) {
		hop(p.x, p.y, randX, randY);
	}

	/**
	 * @return <tt>true</tt> if the bot mouse is present.
	 */
	public boolean isPresent() {
		final org.rsbot.client.input.Mouse mouse = methods.game.client()
		.getMouse();
		return mouse != null && mouse.isPresent();
	}

	/**
	 * @return <tt>true</tt> if the bot mouse is pressed.
	 */
	public boolean isPressed() {
		final org.rsbot.client.input.Mouse mouse = methods.game.client()
		.getMouse();
		return mouse != null && mouse.isPressed();
	}

	/**
	 * Moves mouse to location (x,y) at default speed.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @see #move(int, int, int, int)
	 * @see #setSpeed(int)
	 */
	public void move(final int x, final int y) {
		move(getSpeed(), x, y, 0, 0, 0);
	}

	public void move(final int x, final int y, final boolean paths) {
		move(getSpeed(), x, y, 0, 0, 0, paths);
	}

	/**
	 * @see #move(int, int, int, int, int, int)
	 */
	public void move(final int x, final int y, final int offset) {
		move(getSpeed(), x, y, 0, 0, offset);
	}

	/**
	 * Moves the mouse to the specified point at default speed.
	 * 
	 * @param x
	 *            The x destination.
	 * @param y
	 *            The y destination.
	 * @param randX
	 *            x-axis randomness (added to x).
	 * @param randY
	 *            y-axis randomness (added to y).
	 * @see #move(int, int, int, int, int, int)
	 * @see #setSpeed(int)
	 */
	public void move(final int x, final int y, final int randX, final int randY) {
		move(getSpeed(), x, y, randX, randY, 0);
	}

	/**
	 * Moves the mouse to the specified point at a certain speed.
	 * 
	 * @param speed
	 *            The lower, the faster.
	 * @param x
	 *            The x destination.
	 * @param y
	 *            The y destination.
	 * @param randX
	 *            x-axis randomness (added to x).
	 * @param randY
	 *            y-axis randomness (added to y).
	 * @see #move(int, int, int, int, int, int)
	 */
	public void move(final int speed, final int x, final int y,
			final int randX, final int randY) {
		move(speed, x, y, randX, randY, 0);
	}

	/**
	 * Moves the mouse to the specified point at a certain speed, then moves a
	 * random distance up to <code>afterOffset</code>.
	 * 
	 * @param speed
	 *            The lower, the faster.
	 * @param x
	 *            The x destination.
	 * @param y
	 *            The y destination.
	 * @param randX
	 *            X-axis randomness (added to x).
	 * @param randY
	 *            X-axis randomness (added to y).
	 * @param offset
	 *            The maximum distance in pixels to move on both axes shortly
	 *            after moving to the destination.
	 */
	public synchronized void move(final int speed, final int x, final int y,
			final int randX, final int randY, final int offset) {
		if (x != -1 || y != -1) {
			methods.input.moveMouse(speed, x, y, randX, randY);
			if (offset > 0) {
				methods.sleep(methods.random(60, 300));
				final Point pos = getLocation();
				move(pos.x - offset, pos.y - offset, offset * 2, offset * 2);
			}
		}
	}

	/**
	 * Moves the mouse to the specified point at a certain speed.
	 * 
	 * @param speed
	 *            The lower, the faster.
	 * @param x
	 *            The x destination.
	 * @param y
	 *            The y destination.
	 * @param randX
	 *            X-axis randomness (added to x).
	 * @param randY
	 *            X-axis randomness (added to y).
	 * @param afterOffset
	 *            The maximum distance in pixels to move on both axes shortly
	 *            after moving to the destination.
	 * @param mousePaths
	 *            <tt>true</tt> to enable Sweed's mouse splines, otherwise
	 *            <tt>false</tt>.
	 * @see #getMouseSpeed()
	 */
	public void move(final int speed, final int x, final int y, final int randX, final int randY,
			final int afterOffset, final boolean mousePaths) {
		if (x != -1 || y != -1) {
			methods.input.moveMouse(speed, x, y, randX, randY, mousePaths);
			if (afterOffset > 0) {
				methods.wait(methods.random(60, 300));
				final Point pos = getLocation();
				move(pos.x - afterOffset, pos.y - afterOffset, afterOffset * 2,
						afterOffset * 2);
			}
		}
	}

	/**
	 * @see #move(int, int, int, int, int, int)
	 */
	public void move(final int speed, final Point p) {
		move(speed, p.x, p.y, 0, 0, 0);
	}

	public void move(final int Speed, final Point p, final boolean paths) {
		move(Speed, p.x, p.y, 0, 0, 0, paths);
	}

	/**
	 * @see #move(int, int, int, int)
	 */
	public void move(final Point p) {
		move(p.x, p.y, 0, 0);
	}

	public void move(final Point p, final boolean paths) {
		move(getSpeed(), p.x, p.y, 0, 0, 0, paths);
	}

	/**
	 * @see #move(int, int, int, int, int, int)
	 */
	public void move(final Point p, final int afterOffset) {
		move(getSpeed(), p.x, p.y, 0, 0, afterOffset);
	}

	public void move(final Point p, final int offset, final boolean paths) {
		move(getSpeed(), p.x, p.y, 0, 0, offset, paths);
	}

	/**
	 * @see #move(int, int, int, int)
	 */
	public void move(final Point p, final int randX, final int randY) {
		move(p.x, p.y, randX, randY);
	}

	public void move(final Point p, final int randX, final int randY, final boolean paths) {
		move(getSpeed(), p.x, p.y, randX, randY, 0, paths);
	}

	/**
	 * @see #move(int, int, int, int, int, int)
	 */
	public void move(final Point p, final int randX, final int randY,
			final int offset) {
		move(getSpeed(), p.x, p.y, randX, randY, offset);
	}

	/**
	 * Moves mouse through a specified mouse path.
	 * 
	 * @param path
	 *            The path to move mouse through.
	 * @param randX
	 *            The amount each point can be randomized in the X-axis.
	 * @param randY
	 *            The amount each point can be randomized in the Y-axis.
	 */
	public void move(final Point[] path, final int randX, final int randY) {
		for (final Point p : path) {
			move(p, randX, randY);
		}
	}

	/**
	 * Moves the mouse off the screen in a random direction.
	 */
	public void moveOffScreen() {
		if (isPresent()) {
			switch (methods.random(0, 4)) {
			case 0: // up
				move(methods.random(-10, methods.game.getWidth() + 10),
						methods.random(-100, -10));
				break;
			case 1: // down
				move(methods.random(-10, methods.game.getWidth() + 10),
						methods.game.getHeight() + methods.random(10, 100));
				break;
			case 2: // left
				move(methods.random(-100, -10),
						methods.random(-10, methods.game.getHeight() + 10));
				break;
			case 3: // right
				move(methods.random(10, 100) + methods.game.getWidth(),
						methods.random(-10, methods.game.getHeight() + 10));
				break;
			}
		}
	}

	/**
	 * Moves the mouse a random distance between 1 and maxDistance from the
	 * current position of the mouse by generating a random vector and then
	 * multiplying it by a random number between 1 and maxDistance. The maximum
	 * distance is cut short if the mouse would go off screen in the direction
	 * it chose.
	 * 
	 * @param max
	 *            The maximum distance the cursor will move (exclusive)
	 * @author Enfilade
	 */
	public void moveRandomly(final int max) {
		moveRandomly(1, max);
	}

	/**
	 * Moves the mouse a random distance between minDistance and maxDistance
	 * from the current position of the mouse by generating random vector and
	 * then multiplying it by a random number between minDistance and
	 * maxDistance. The maximum distance is cut short if the mouse would go off
	 * screen in the direction it chose.
	 * 
	 * @param min
	 *            The minimum distance the cursor will move
	 * @param max
	 *            The maximum distance the cursor will move (exclusive)
	 * @author Enfilade
	 */
	public void moveRandomly(final int min, final int max) {
		/* Generate a random vector for the direction the mouse will move in */
		double xvec = Math.random();
		if (methods.random(0, 2) == 1) {
			xvec = -xvec;
		}
		double yvec = Math.sqrt(1 - xvec * xvec);
		if (methods.random(0, 2) == 1) {
			yvec = -yvec;
		}
		/* Start the maximum distance at maxDistance */
		double distance = max;
		/* Get the current location of the cursor */
		final Point p = getLocation();
		/* Calculate the x coordinate if the mouse moved the maximum distance */
		final int maxX = (int) Math.round(xvec * distance + p.x);
		/*
		 * If the maximum x is offscreen, subtract that distance/xvec from the
		 * maximum distance so the maximum distance will give a valid X
		 * coordinate
		 */
		distance -= Math.abs((maxX - Math.max(0,
				Math.min(methods.game.getWidth(), maxX)))
				/ xvec);
		/* Do the same thing with the Y coordinate */
		final int maxY = (int) Math.round(yvec * distance + p.y);
		distance -= Math.abs((maxY - Math.max(0,
				Math.min(methods.game.getHeight(), maxY)))
				/ yvec);
		/*
		 * If the maximum distance in the generated direction is too small,
		 * don't move the mouse at all
		 */
		if (distance < min) {
			return;
		}
		/*
		 * With the calculated maximum distance, pick a random distance to move
		 * the mouse between maxDistance and the calculated maximum distance
		 */
		distance = methods.random(min, (int) distance);
		/* Generate the point to move the mouse to and move it there */
		move((int) (xvec * distance) + p.x, (int) (yvec * distance) + p.y);
	}

	/**
	 * Moves the mouse slightly depending on where it currently is.
	 */
	public void moveSlightly() {
		final Point p = new Point(
				(int) (getLocation().getX() + (Math.random() * 50 > 25 ? 1 : -1)
						* (30 + Math.random() * 90)), (int) (getLocation()
								.getY() + (Math.random() * 50 > 25 ? 1 : -1)
								* (30 + Math.random() * 90)));
		if (p.getX() < 1 || p.getY() < 1 || p.getX() > 761 || p.getY() > 499) {
			moveSlightly();
			return;
		}
		move(p);
	}

	/**
	 * Changes the mouse speed
	 * 
	 * @param speed
	 *            The speed to move the mouse at. 4-10 is advised, 1 being the
	 *            fastest.
	 * @see #getSpeed()
	 */
	public void setSpeed(final int speed) {
		mouseSpeed = speed;
	}
}
