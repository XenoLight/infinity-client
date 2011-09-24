package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/**
 * @author Secret Spy
 * @version 1.8 - 02/24/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "GraveDigger", version = 1.8)
public class GraveDigger extends Random {

	class Group {
		// IDs used later
		int coffinID = -1;
		int graveID = -1;

		// General group data
		int graveStoneModelID;
		int[] coffinModelIDs;

		public Group(final int graveStoneModelID, final int[] coffinModelIDs) {
			this.graveStoneModelID = graveStoneModelID;
			this.coffinModelIDs = coffinModelIDs;
		}

		public boolean isGroup(final int graveStoneModelID) {
			return this.graveStoneModelID == graveStoneModelID;
		}

		public boolean isGroup(final int[] coffinModelIDs) {
			for (final int modelID : this.coffinModelIDs) {
				boolean found = false;

				for (final int coffinModelID : coffinModelIDs) {
					if (modelID == coffinModelID) {
						found = true;
					}
				}

				if (!found)
					return false;
			}

			return true;
		}

	}

	private static final int[] coffinIDs = { 7587, 7588, 7589, 7590, 7591 };
	private static final int[] graveStoneIDs = { 12716, 12717, 12718, 12719,
		12720 };
	private static final int[] filledGraveIDs = { 12721, 12722, 12723, 12724,
		12725 };
	private static final int[] emptyGraveIDs = { 12726, 12727, 12728, 12729,
		12730 };

	private static final int INTERFACE_READ_GRAVESTONE = 143;
	private static final int INTERFACE_READ_GRAVESTONE_MODEL = 2;
	private static final int INTERFACE_READ_GRAVESTONE_CLOSE = 3;
	private static final int INTERFACE_CHECK_COFFIN = 141;
	private static final int INTERFACE_CHECK_COFFIN_CLOSE = 12;
	private static final int[] INTERFACE_CHECK_COFFIN_ITEMS = { 3, 4, 5, 6, 7,
		8, 9, 10, 11 };
	public static final int INTERFACE_DEPOSIT_BOX = 11;

	@SuppressWarnings("unused")
	private static final int[] NOT_TO_DEPOSIT = { 1351, 1349, 1353, 1361, 1355,
		1357, 1359, 4031, 6739, 13470, 14108, 1265, 1267, 1269, 1296, 1273,
		1271, 1275, 15259, 303, 305, 307, 309, 311, 10129, 301, 13431, 313,
		314, 2347, 995, 10006, 10031, 10008, 10012, 11260, 10150, 10010,
		556, 558, 555, 557, 554, 559, 562, 560, 565, 8013, 4251, 8011,
		8010, 8009, 8008, 8007 };

	private final ArrayList<Group> groups = new ArrayList<Group>();

	private int tmpID = -1, tmpStatus = -1; // used to store some data across
	// loops

	public GraveDigger() {
		groups.add(new Group(7614, new int[] { 7603, 7605, 7612 }));
		groups.add(new Group(7615, new int[] { 7600, 7601, 7604 }));
		groups.add(new Group(7616, new int[] { 7597, 7606, 7607 }));
		groups.add(new Group(7617, new int[] { 7602, 7609, 7610 }));
		groups.add(new Group(7618, new int[] { 7599, 7608, 7613 }));
	}

	@Override
	public boolean activateCondition() {
		if ((settings.get(696) != 0) && (objects.getNearestByID(12731) != null)) {
			tmpID = tmpStatus = -1;
			return true;
		}
		return false;
	}

	public boolean atCloseInterface(final int parent, final int child) {
		final RSInterfaceChild i = iface.getChild(parent, child);
		if (!i.isValid())
			return false;
		final Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		final int dx = (int) (pos.getWidth() - 4) / 2;
		final int dy = (int) (pos.getHeight() - 4) / 2;
		final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		final int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
		mouse.click(midx + random(-dx, dx) - 5, midy + random(-dy, dy), true);
		return true;
	}

	@Override
	public int loop() {
		camera.setAltitude(true);
		if (npc.getNearestByName("Leo") == null) {
			return -1;
		}
		if (inventory.getCountExcept(GraveDigger.coffinIDs) > 23) {
			if (iface.canContinue()) {
				iface.clickContinue();
				sleep(random(1500, 2000));
			}
			final RSObject depo = objects.getNearestByID(12731);
			if (depo != null) {
				if (!tile.onScreen(depo.getLocation())) {
					walk.to(depo.getLocation());
					camera.turnTo(depo);
				} else {
					objects.at(depo, "Deposit");
				}
			}
			if (inventory.getCount() > 23) {
				final RSInterfaceChild i = iface.getChild(11, 17);
				if (iface.get(INTERFACE_DEPOSIT_BOX).isValid()) {
					sleep(random(700, 1200));
					if (inventory.getCount() >= 28) {
						iface.clickChild(i, 27, "Dep");
						wait(random(700, 1200));
					}
					if (inventory.getCount() >= 27) {
						iface.clickChild(i, 26, "Dep");
						wait(random(700, 1200));
					}
					if (inventory.getCount() >= 26) {
						iface.clickChild(i, 25, "Dep");
						wait(random(700, 1200));
					}
					if (inventory.getCount() >= 25) {
						iface.clickChild(i, 24, "Dep");
						wait(random(700, 1200));
					}
					if (inventory.getCount() >= 24) {
						iface.clickChild(i, 23, "Dep");
						wait(random(700, 1200));
					}
					iface.clickChild(11, 15);
					return random(500, 700);
				}
			}
			return (random(2000, 3000));
		}

		if (player.getMine().isMoving()) {

		} else if (player.getMine().getAnimation() == 827) {

		} else if (iface.get(242).isValid()) {
			// Check if we finished before
			if (iface.get(242).containsText("ready to leave")) {
				tmpStatus++;
			}
			iface.clickChild(242, 6);
		} else if (iface.get(64).isValid()) {
			iface.clickChild(64, 5);
		} else if (iface.get(241).isValid()) {
			iface.clickChild(241, 5);
		} else if (iface.get(243).isValid()) {
			iface.clickChild(243, 7);
		} else if (iface.get(220).isValid()) {
			iface.clickChild(220, 16);
		} else if (iface.get(236).isValid()) {
			if (iface.get(236).containsText("ready to leave")) {
				iface.clickChild(236, 1);
			} else {
				iface.clickChild(236, 2);
			}
		} else if (iface.get(GraveDigger.INTERFACE_CHECK_COFFIN).isValid()) {
			if (tmpID >= 0) {
				final int[] items = new int[GraveDigger.INTERFACE_CHECK_COFFIN_ITEMS.length];

				final RSInterface inters = iface
				.get(GraveDigger.INTERFACE_CHECK_COFFIN);
				for (int i = 0; i < GraveDigger.INTERFACE_CHECK_COFFIN_ITEMS.length; i++) {
					items[i] = inters.getChild(
							GraveDigger.INTERFACE_CHECK_COFFIN_ITEMS[i])
							.getChildID();
				}

				for (final Iterator<Group> it = groups.iterator(); it.hasNext()
				&& (tmpID >= 0);) {
					final Group g = it.next();
					if (g.isGroup(items)) {
						g.coffinID = tmpID;
						tmpID = -1;
					}
				}
			}
			atCloseInterface(GraveDigger.INTERFACE_CHECK_COFFIN,
					GraveDigger.INTERFACE_CHECK_COFFIN_CLOSE);
		} else if (iface.get(GraveDigger.INTERFACE_READ_GRAVESTONE).isValid()) {
			final int modelID = iface
			.get(GraveDigger.INTERFACE_READ_GRAVESTONE)
			.getChild(GraveDigger.INTERFACE_READ_GRAVESTONE_MODEL)
			.getChildID();
			for (final Group g : groups) {
				if (g.isGroup(modelID)) {
					g.graveID = tmpID;
				}
			}
			atCloseInterface(GraveDigger.INTERFACE_READ_GRAVESTONE,
					GraveDigger.INTERFACE_READ_GRAVESTONE_CLOSE);
		} else if ((tmpStatus == 0) && (tmpID != -1)) {
			for (final Group g : groups) {
				if (g.graveID == tmpID) {
					final RSObject obj = objects.getNearestByID(g.graveID);
					if ((obj == null) || !setObjectInScreen(obj)) {
						log.info(ScreenLog.graveGrave);
						game.logout();
						return -1;
					}

					inventory.useItem(inventory
							.getItemByID(GraveDigger.coffinIDs[g.coffinID]),
							obj);

					// Wait for about 10s to finish
					final long cTime = System.currentTimeMillis();
					while (System.currentTimeMillis() - cTime < 10000) {
						if (inventory
								.getItemByID(GraveDigger.coffinIDs[g.coffinID]) == null) {
							break;
						}

						sleep(random(400, 700));
					}

					break;
				}
			}

			tmpID = -1;
		} else if ((tmpStatus == -1)
				&& (objects.getNearestByID(GraveDigger.filledGraveIDs) != null)) {
			final RSObject obj = objects
			.getNearestByID(GraveDigger.filledGraveIDs);
			if ((obj == null) || !setObjectInScreen(obj)) {
				log.severe(ScreenLog.graveGrave);
				game.logout();
				return -1;
			}
			objects.at(obj, "Take-coffin");
		} else if ((tmpStatus == 0)
				&& (objects.getNearestByID(GraveDigger.emptyGraveIDs) != null)) {
			final RSObject obj = objects
			.getNearestByID(GraveDigger.emptyGraveIDs);
			final int id = obj.getID();
			for (int i = 0; i < GraveDigger.emptyGraveIDs.length; i++) {
				if (GraveDigger.emptyGraveIDs[i] == id) {
					final RSObject objGS = objects
					.getNearestByID(GraveDigger.graveStoneIDs[i]);
					if ((objGS == null) || !setObjectInScreen(objGS)) {
						log.severe(ScreenLog.graveStone);
						game.logout();
						return -1;
					}
					tmpID = obj.getID();
					objects.at(objGS, "Read");
				}
			}
		} else if (tmpStatus == -1) {
			final ArrayList<Integer> agc = new ArrayList<Integer>();
			for (int i = 0; i < GraveDigger.coffinIDs.length; i++) {
				agc.add(i);
			}

			for (final Group g : groups) {
				if (g.coffinID != -1) {
					agc.remove(new Integer(g.coffinID));
				}
			}

			if ((tmpStatus == -1) && (agc.size() == 0)) {
				tmpStatus++;
			}

			while (tmpStatus == -1) {
				final int i = random(0, agc.size());
				if (inventory.getCount(GraveDigger.coffinIDs[agc.get(i)]) > 0) {
					tmpID = agc.get(i);
					inventory.clickItem(GraveDigger.coffinIDs[agc.get(i)],
					"Check");

					return random(1800, 2400); // We are looking at the model
				}
			}
		} else if (tmpStatus == 0) {
			// Done
			final RSNPC leo = npc.getNearestByName("Leo");
			if ((leo == null) || !setCharacterInScreen(leo)) {
				log.severe(ScreenLog.graveLeo);
				game.logout();
				stopScript();
				return -1;
			}

			npc.action(leo, "Talk-to");
		}
		return random(1400, 1800);
	}

	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.grave, 9, 330);
		ScreenMouse.paint(render);
	}

	public boolean setCharacterInScreen(final RSCharacter ch) {
		// Check if it's on screen, if not make it on screen.
		for (int i = 0; i < 3; i++) {
			final Point screenLocation = ch.getScreenLocation();
			if (!calculate.pointOnScreen(screenLocation)) {
				switch (i) {
				case 0:
					camera.turnTo(ch);

					sleep(random(200, 500));

					break;
				case 1:
					walk.tileMM(walk.getClosestTileOnMap(ch.getLocation()
							.randomizeTile(2, 2)));

					sleep(random(1800, 2000));

					while (player.getMine().isMoving()) {
						sleep(random(200, 500));
					}
					break;
				default:
					return false;
				}

			}
		}

		return true;
	}

	public boolean setObjectInScreen(final RSObject obj) {
		// Check if it's on screen, if not make it on screen.
		for (int i = 0; i < 3; i++) {
			final Point screenLocation = Calculations.tileToScreen(obj
					.getLocation());
			if (!calculate.pointOnScreen(screenLocation)) {
				switch (i) {
				case 0:
					camera.turnTo(obj);

					sleep(random(200, 500));

					break;
				case 1:
					walk.tileMM(walk.getClosestTileOnMap(obj.getLocation()
							.randomizeTile(2, 2)));
					sleep(random(1800, 2000));
					while (player.getMine().isMoving()) {
						sleep(random(200, 500));
					}
					break;
				default:
					return false;
				}

			}
		}

		return true;
	}
}