package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;

/**
 * @author Secret Spy
 * @version 2.6 - 02/09/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "BeeHive", version = 2.6)
public class BeehiveSolver extends Random implements PaintListener {

	RSNPC BeehiveKeeper;
	private static final int BeehiveKeeperID = 8649;
	private static final int[] DestInterfaceIDs = { 16, 17, 18, 19 };
	private static final int IDDown = 16034;
	private static final int IDMiddown = 16022;
	private static final int IDMidup = 16025;
	private static final int IDTop = 16036;
	private static final int[] BeehiveArrays = { IDTop, IDMidup, IDMiddown,
		IDDown };
	private static final String[] ModelNames = { "Top", "Middle Up",
		"Middle Down", "Down" };
	boolean Solved;
	private static final int[] StartInterfaceIDs = { 12, 13, 14, 15 };
	private static final int InterfaceBeehiveWindow = 420;
	private static final int BuildBeehive = 40;
	private static final int CloseWindow = 38;

	@Override
	public boolean activateCondition() {
		if (!game.isLoggedIn()) {
			return false;
		}
		if (npc.getNearestByID(BeehiveKeeperID) != null
				&& objects.getNearestByID(16168) != null) {
			Solved = false;
			return true;
		}
		return false;
	}

	public boolean dragInterfaces(final RSInterfaceChild child1,
			final RSInterfaceChild child2) {
		final Point start = returnMidInterface(child1);
		final Point finish = returnMidInterface(child2);

		mouse.move(start);
		mouse.drag(finish);
		return true;
	}

	public RSInterface getBeehiveInterface() {
		return iface.get(420);
	}

	@Override
	public int loop() {
		BeehiveKeeper = npc.getNearestByID(BeehiveKeeperID);
		if (BeehiveKeeper == null) {
			return -1;
		}
		if (myclickContinue()) {
			return 200;
		}
		if (iface.clickChild(236, 2)) {
			return random(800, 1200);
		}
		if (getBeehiveInterface().isValid()) {
			for (int i = 1; i < 5; i++) {
				log.config("Checking ID: " + i);
				final int id = returnIdAtSlot(i);
				dragInterfaces(
						getBeehiveInterface()
						.getChild(StartInterfaceIDs[i - 1]),
						getBeehiveInterface().getChild(returnDragTo(id)));
			}
			sleep(2000);
			if (settings.get(805) == 109907968) {
				Solved = true;
				log(ScreenLog.beeFinish);
			} else {
				iface.clickChild(InterfaceBeehiveWindow, CloseWindow);
				return random(500, 1000);
			}
			if (Solved
					&& iface.clickChild(InterfaceBeehiveWindow, BuildBeehive)) {
				return random(900, 1600);
			}
		} else {
			log.config(ScreenLog.beeIFace);
		}
		if (player.getMine().getInteracting() == null) {
			final RSNPC n = npc.getNearestByID(BeehiveKeeperID);
			if (n != null) {
				if (!npc.click(n, "Talk-to")) {
					camera.setRotation(camera.getAngle() + random(-30, 30));
				}
			}
		}
		return random(500, 1000);
	}

	public boolean myclickContinue() {
		sleep(random(800, 1000));
		return iface.clickChild(243, 7) || iface.clickChild(241, 5)
		|| iface.clickChild(242, 6) || iface.clickChild(244, 8)
		|| iface.clickChild(64, 5);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.bee, 9, 330);
		ScreenMouse.paint(render);
	}

	public int returnDragTo(final int Model) {
		switch (Model) {
		case 16036:
			return DestInterfaceIDs[0];
		case 16025:
			return DestInterfaceIDs[1];
		case 16022:
			return DestInterfaceIDs[2];
		case 16034:
			return DestInterfaceIDs[3];
		default:
			return -1;
		}
	}

	public int returnIdAtSlot(final int slot) {
		if ((slot < 1) || (slot > 4)) {
			log.warning("Invalid Slot.");
			iface.clickChild(InterfaceBeehiveWindow, CloseWindow);
		}
		final int Model_ID = getBeehiveInterface().getChild(returnSlotId(slot))
		.getModelID();
		if (Model_ID == -1) {
			log.warning(ScreenLog.beeID2);
			iface.clickChild(InterfaceBeehiveWindow, CloseWindow);
		}
		for (int i = 0; i < BeehiveArrays.length; i++) {
			if (Model_ID == BeehiveArrays[i]) {
				log.config("Slot " + slot + " contains section: " + ModelNames[i]);
				return Model_ID;
			}
		}
		return -1;
	}

	public Point returnMidInterface(final RSInterfaceChild child) {
		Point point = new Point(-1, -1);
		final Rectangle rect = child.getArea();
		if (rect != null) {
			point = new Point((int) rect.getCenterX(), (int) rect.getCenterY());
		}
		return point;
	}

	public int returnSlotId(final int slot) {
		switch (slot) {
		case 1:
			return 25;
		case 2:
			return 22;
		case 3:
			return 23;
		case 4:
			return 21;
		default:
			log.warning(ScreenLog.beeID);
			iface.clickChild(InterfaceBeehiveWindow, CloseWindow);
			break;
		}
		return -1;
	}
}
