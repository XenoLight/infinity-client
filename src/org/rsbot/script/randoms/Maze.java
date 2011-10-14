package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 1.8 - 04/26/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Maze", version = 1.8)
public class Maze extends Random implements PaintListener {

	public class Door {

		public RSTile doorTile;
		public char doorDir;
		public int doorID;

		public Door(final RSTile doorTile, final char doorDir, final int doorID) {
			this.doorTile = doorTile;
			this.doorDir = doorDir;
			this.doorID = doorID;
		}
	}
	public ArrayList<Door> paths = new ArrayList<Door>();
	String usePath = "None";
	public char doorDir = 'a';
	public RSTile walkToTile = null;
	public int doorIndex = 0;
	public int tryCount = 0;
	public long lastTry = 0;
	public RSTile lastDoor = null;

	public static final int TYPE_BOUNDARY = 4;

	@Override
	public boolean activateCondition() {
		if (game.isLoggedIn() && ((objects.getNearestByID(3626, 3649) != null))) {
			camera.setAltitude(true);
			return true;
		}
		return false;
	}

	public boolean atDoor(final RSTile location, final char direction) {
		if (location == null) {
			return false;
		}
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
		return atDoorTiles(location, new RSTile(x, y));
	}

	@Override
	public boolean atDoorTiles(final RSTile a, final RSTile b) {
		if (a != lastDoor) {
			lastTry = 0;
			tryCount = 0;
			lastDoor = a;
		}
		tryCount++;
		if (System.currentTimeMillis() - lastTry > random(20000, 40000)) {
			tryCount = 1;
		}
		lastTry = System.currentTimeMillis();
		if (tryCount > 4) {
			if (random(0, 10) < random(2, 4)) {
				setAngle(camera.getAngle()
						+ (random(0, 9) < random(6, 8) ? random(-20, 20)
								: random(-360, 360)));
			}
			if (random(0, 14) < random(0, 2)) {
				camera.setAltitude(random(0, 100));
			}
		}
		if (tryCount > 100) {
			log(ScreenLog.mazeWall1);
			stopScript(false);
		}
		if (!tile.onScreen(a) || !tile.onScreen(b)
				|| (a.distanceTo() > random(4, 7))) {
			if (a.isOnMinimap()) {
				walk.to(a.randomizeTile(3, 3));
				sleep(random(750, 1250));
			} else {
				log(ScreenLog.mazeWall2);
				return false;
			}
		} else {
			final ArrayList<RSTile> theObjs = new ArrayList<RSTile>();
			theObjs.add(a);
			theObjs.add(b);
			try {
				final Point[] thePoints = new Point[theObjs.size()];
				for (int c = 0; c < theObjs.size(); c++) {
					thePoints[c] = Calculations.tileToScreen(theObjs.get(c));
				}
				float xTotal = 0;
				float yTotal = 0;
				for (final Point thePoint : thePoints) {
					xTotal += thePoint.getX();
					yTotal += thePoint.getY();
				}
				final Point location = new Point(
						(int) (xTotal / thePoints.length),
						(int) (yTotal / thePoints.length) - random(0, 40));
				if ((location.x == -1) || (location.y == -1)) {
					return false;
				}
				if (Math.sqrt(Math.pow(
						(mouse.getLocation().getX() - location.getX()), 2)
						+ Math.pow(
								(mouse.getLocation().getY() - location.getY()),
								2)) < random(20, 30)) {
					for (final String command : menu.getItems()) {
						if (command.contains("Open")) {
							if (menu.action("Open")) {
								lastTry = 0;
								tryCount = 0;
								return true;
							}
						}
					}
				}
				mouse.move(location, 7, 7);
				if (menu.action("Open")) {
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

	public int getAngle() {
		// the client uses fixed point radians 0 - 2^14
		// degrees = yaw * 360 / 2^14 = yaw / 45.5111...
		return (int) (Bot.getClient().getCameraYaw() / 45.51);
	}

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

	public void getNewWall() {
		for (final Door door : paths) {
			if (door.doorID == doorIndex) {
				walkToTile = new RSTile(door.doorTile.getX(),
						door.doorTile.getY());
				doorDir = door.doorDir;
				door.doorID = doorIndex;
				log(ScreenLog.mazeWalk + walkToTile.getX() + ScreenLog.coma
						+ walkToTile.getY());
				log(ScreenLog.mazeIdx1 + doorIndex + ScreenLog.mazeIdx2
						+ doorDir);
			}
		}
	}

	public void getPath() {
		final int x = player.getMine().getLocation().getX();
		final int y = player.getMine().getLocation().getY();
		if (x >= 2920 && x <= 2940 && y >= 4572 && y <= 4600) {
			loadPurplePath();
			usePath = "purple";
			walkToTile = new RSTile(2932, 4597);
			doorDir = 'n';
			doorIndex = 0;
			log("Using purple path!");
		}
		if (x >= 2891 && x <= 2894 && y >= 4586 && y <= 4599) {
			loadBluePath();
			usePath = "blue";
			walkToTile = new RSTile(2891, 4588);
			doorDir = 'w';
			doorIndex = 0;
			log("Using blue path!");
		}
		if (x >= 2915 && x <= 2933 && y >= 4555 && y <= 4560) {
			loadCyanPath();
			usePath = "cyan";
			walkToTile = new RSTile(2930, 4555);
			doorDir = 's';
			doorIndex = 0;
			log("Using cyan path!");
		}
		if (x >= 2891 && x <= 2914 && y >= 4555 && y <= 4561) {
			loadGreenPath();
			usePath = "green";
			walkToTile = new RSTile(2903, 4555);
			doorDir = 's';
			doorIndex = 0;
			log("Using green path!");
		}
	}

	// working as of 1/31/2010
	public void loadBluePath() {

		paths.add(new Door(new RSTile(2891, 4588), 'w', 0));
		paths.add(new Door(new RSTile(2889, 4596), 'w', 1));
		paths.add(new Door(new RSTile(2893, 4600), 's', 2));
		paths.add(new Door(new RSTile(2901, 4598), 's', 3));
		paths.add(new Door(new RSTile(2897, 4596), 's', 4));
		paths.add(new Door(new RSTile(2894, 4587), 'e', 5));
		paths.add(new Door(new RSTile(2896, 4582), 'e', 6));
		paths.add(new Door(new RSTile(2898, 4570), 'e', 7));
		paths.add(new Door(new RSTile(2900, 4567), 'e', 8));
		paths.add(new Door(new RSTile(2911, 4566), 'n', 9));
		paths.add(new Door(new RSTile(2906, 4585), 'n', 10));
		paths.add(new Door(new RSTile(2916, 4586), 's', 11));
		paths.add(new Door(new RSTile(2920, 4582), 'w', 12));
		paths.add(new Door(new RSTile(2910, 4582), 's', 13));
		paths.add(new Door(new RSTile(2910, 4572), 'n', 14));
		paths.add(new Door(new RSTile(2910, 4576), 'e', 15));
		log("Loaded blue path");
	}

	// working as of 4/07/2011
	public void loadCyanPath() {

		paths.add(new Door(new RSTile(2930, 4555), 's', 0));
		paths.add(new Door(new RSTile(2912, 4553), 's', 1));
		paths.add(new Door(new RSTile(2936, 4556), 'w', 2));
		paths.add(new Door(new RSTile(2934, 4568), 'w', 3));
		paths.add(new Door(new RSTile(2932, 4575), 'w', 4));
		paths.add(new Door(new RSTile(2930, 4561), 'w', 5));
		paths.add(new Door(new RSTile(2929, 4581), 'e', 6));
		paths.add(new Door(new RSTile(2930, 4590), 'w', 7));
		paths.add(new Door(new RSTile(2924, 4592), 's', 8));
		paths.add(new Door(new RSTile(2926, 4575), 'w', 9));
		paths.add(new Door(new RSTile(2924, 4583), 'w', 10));
		paths.add(new Door(new RSTile(2916, 4586), 's', 11));
		paths.add(new Door(new RSTile(2920, 4582), 'w', 12));
		paths.add(new Door(new RSTile(2910, 4582), 's', 13));
		paths.add(new Door(new RSTile(2910, 4572), 'n', 14));
		paths.add(new Door(new RSTile(2910, 4576), 'e', 15));
		log("Loaded cyan path");
	}

	// working as of 4/07/2011
	public void loadGreenPath() {

		paths.add(new Door(new RSTile(2903, 4555), 's', 0));
		paths.add(new Door(new RSTile(2890, 4566), 'e', 1));
		paths.add(new Door(new RSTile(2892, 4578), 'e', 2));
		paths.add(new Door(new RSTile(2894, 4567), 'e', 3));
		paths.add(new Door(new RSTile(2896, 4562), 'e', 4));
		paths.add(new Door(new RSTile(2905, 4561), 's', 5));
		paths.add(new Door(new RSTile(2915, 4560), 'n', 6));
		paths.add(new Door(new RSTile(2909, 4562), 'n', 7));
		paths.add(new Door(new RSTile(2924, 4566), 'w', 8));
		paths.add(new Door(new RSTile(2922, 4575), 'w', 9));
		paths.add(new Door(new RSTile(2916, 4568), 'n', 10));
		paths.add(new Door(new RSTile(2905, 4573), 'w', 11));
		paths.add(new Door(new RSTile(2906, 4585), 'n', 12));
		paths.add(new Door(new RSTile(2916, 4586), 's', 13));
		paths.add(new Door(new RSTile(2920, 4582), 'w', 14));
		paths.add(new Door(new RSTile(2910, 4582), 's', 15));
		paths.add(new Door(new RSTile(2910, 4572), 'n', 16));
		paths.add(new Door(new RSTile(2910, 4576), 'e', 17));
		log("Loaded green path");
	}

	// working as of 2/05/2010
	public void loadPurplePath() {

		paths.add(new Door(new RSTile(2932, 4597), 'n', 0));
		paths.add(new Door(new RSTile(2921, 4599), 'n', 1));
		paths.add(new Door(new RSTile(2909, 4600), 's', 3));
		paths.add(new Door(new RSTile(2913, 4598), 's', 4));
		paths.add(new Door(new RSTile(2908, 4596), 's', 5));
		paths.add(new Door(new RSTile(2919, 4594), 's', 6));
		paths.add(new Door(new RSTile(2908, 4592), 's', 7));
		paths.add(new Door(new RSTile(2898, 4585), 'e', 8));
		paths.add(new Door(new RSTile(2903, 4588), 's', 9));
		paths.add(new Door(new RSTile(2902, 4575), 'e', 10));
		paths.add(new Door(new RSTile(2906, 4585), 'n', 11));
		paths.add(new Door(new RSTile(2916, 4586), 's', 12));
		paths.add(new Door(new RSTile(2920, 4582), 'w', 13));
		paths.add(new Door(new RSTile(2910, 4582), 's', 14));
		paths.add(new Door(new RSTile(2910, 4572), 'n', 15));
		paths.add(new Door(new RSTile(2910, 4576), 'e', 16));

		log("Loaded purple path");
	}

	@Override
	public int loop() {
		if (!activateCondition()) {
			return -1;
		}
		if (player.getMine().isMoving()) {
			return random(150, 200);
		}
		if (player.getMine().getLocation().equals(new RSTile(2911, 4576))) {
			if (player.getMine().getAnimation() == -1) {
				tile.click(new RSTile(2912, 4576), "Touch");
				return random(1000, 1200);
			}
		}
		if (usePath.equals("None")) {
			getPath();
			return random(900, 1100);
		}
		if (player.getMine().getLocation().equals(tileAfterDoor())) {
			doorIndex += 1;
			log(ScreenLog.mazeNew);
			getNewWall();
			return random(200, 300);
		}
		if ((walkToTile != null) && (walkToTile.distanceTo() >= 3)) {
			if (!walk.to(walkToTile)) {
				walk.tileMM(walkToTile.randomizeTile(2, 2));
			}
			return random(500, 600);
		}
		if ((walkToTile != null) && (walkToTile.distanceTo() <= 3)) {
			if ((doorDir != 'a') && !player.getMine().isMoving()) {
				if (((camera.getAngle() - turnCameraTo()) < 30)
						|| ((camera.getAngle() - turnCameraTo()) > 30)) {
					camera.setRotation(turnCameraTo());
				}
				final RSObject obj = objects.getTopAt(walkToTile);
				if (obj != null && obj.action("Open")
						|| atDoor(walkToTile, doorDir)) {
					return random(400, 500);
				}
			}
		}
		return random(300, 350);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.maze, 9, 330);
		ScreenMouse.paint(render);
	}

	public void setAngle(final int degrees) {
		if (getAngleTo(degrees) > 5) {
			Bot.getInputManager().pressKey((char) KeyEvent.VK_LEFT);
			while (getAngleTo(degrees) > 5) {
				wait(10);
			}
			Bot.getInputManager().releaseKey((char) KeyEvent.VK_LEFT);
		} else if (getAngleTo(degrees) < -5) {
			Bot.getInputManager().pressKey((char) KeyEvent.VK_RIGHT);
			while (getAngleTo(degrees) < -5) {
				wait(10);
			}
			Bot.getInputManager().releaseKey((char) KeyEvent.VK_RIGHT);
		}
	}

	public RSTile tileAfterDoor() {
		final int doorD = doorDir;
		if (doorD == 'a') {
			log(ScreenLog.mazeDoor);
			return new RSTile(1, 1);
		}
		if (walkToTile == null) {
			log(ScreenLog.mazeNull);
			return new RSTile(1, 1);
		}
		switch (doorD) {
		case 'n':
			return new RSTile(walkToTile.getX(), walkToTile.getY() + 1);
		case 'w':
			return new RSTile(walkToTile.getX() - 1, walkToTile.getY());
		case 'e':
			return new RSTile(walkToTile.getX() + 1, walkToTile.getY());
		case 's':
			return new RSTile(walkToTile.getX(), walkToTile.getY() - 1);
		}
		return new RSTile(1, 1);
	}

	public int turnCameraTo() {
		final int doorD = doorDir;
		if (doorD == 'a') {
			log(ScreenLog.mazeCam);
			return random(330, 380);
		}
		switch (doorD) {
		case 'n':
			return random(330, 380);
		case 's':
			return random(155, 190);
		case 'e':
			return random(245, 290);
		case 'w':
			return random(65, 110);
		}
		return random(330, 380);
	}
}
