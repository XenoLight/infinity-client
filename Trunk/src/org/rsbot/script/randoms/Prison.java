package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSModel;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 1.7 - 04/16/11
 */
@ScriptManifest(authors = { "Iscream, Secret Spy" }, name = "PrisonPete", version = 1.7)
public class Prison extends Random implements PaintListener {

	private static class Balloons {

		static final short[] Fatty = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1,
			2, 2, 3, 3, 4, 4, 5, 6, 6, 7, 7, 2, 2, 8, 8, 5, 5, 9, 9, 10,
			11, 11, 15, 16, 17, 13, 14, 20, 20, 20, 20, 20, 21, 21, 21, 22,
			22, 23, 23, 24, 24, 24, 25, 25, 26, 26, 26, 27, 28, 29, 29, 30,
			30, 31, 32, 33, 33, 36, 36, 36, 36, 36, 37, 37, 37, 38, 38, 39,
			39, 40, 40, 41, 42, 45, 45, 45, 45, 45, 46, 46, 46, 46, 46, 46,
			46, 46, 47, 47, 48, 48, 49, 49, 50, 51, 51, 52, 52, 47, 47, 53,
			53, 50, 50, 54, 54, 55, 22, 22, 59, 60, 61, 57, 58, 64, 64, 64,
			64, 64, 65, 65, 65, 65, 65, 65, 65, 65, 66, 66, 67, 67, 68, 68,
			43, 69, 69, 70, 70, 66, 66, 71, 71, 43, 43, 72, 72, 73, 74, 74,
			78, 79, 80, 76, 77, 83, 83, 83, 83, 83, 84, 84, 84, 85, 85, 86,
			86, 87, 87, 89, 90, 92, 92, 92, 92, 93, 93, 94, 94, 95, 5, 98,
			98, 98, 98, 98, 99, 99, 99, 22, 22, 100, 100, 101, 101, 101,
			102, 102, 103, 103, 103, 104, 105, 106, 106, 107, 107, 108,
			109, 110, 110, 113, 113, 113, 113, 113, 114, 114, 114, 22, 22,
			115, 115, 116, 116, 116, 117, 117, 118, 118, 118, 119, 120,
			121, 121, 122, 122, 123, 124, 125, 125, 128, 128, 128, 128,
			128, 129, 129, 129, 22, 22, 130, 130, 131, 131, 131, 132, 132,
			133, 133, 133, 134, 135, 136, 136, 137, 137, 138, 32, 139, 139 };
		static final short[] Horny = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1,
			2, 2, 3, 3, 4, 4, 5, 6, 6, 7, 7, 2, 2, 8, 8, 5, 5, 9, 9, 10,
			11, 11, 15, 16, 17, 13, 14, 20, 20, 20, 20, 20, 21, 21, 21, 22,
			22, 23, 23, 24, 24, 24, 25, 25, 26, 26, 26, 27, 28, 29, 29, 30,
			30, 31, 32, 33, 33, 36, 36, 36, 36, 36, 37, 37, 37, 38, 38, 39,
			39, 40, 40, 22, 41, 41, 42, 43, 44, 47, 47, 47, 47, 47, 48, 48,
			48, 48, 48, 48, 48, 48, 49, 49, 50, 50, 51, 51, 52, 53, 53, 54,
			54, 49, 49, 55, 55, 52, 52, 56, 56, 57, 22, 22, 61, 62, 63, 59,
			60, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 67, 67, 67, 68, 68,
			69, 69, 70, 70, 43, 71, 71, 72, 72, 68, 68, 73, 73, 43, 43, 74,
			74, 75, 76, 76, 80, 81, 82, 78, 79, 85, 85, 85, 85, 85, 86, 86,
			86, 87, 87, 88, 88, 89, 89, 90, 91, 91, 92, 43, 93, 96, 96, 96,
			96, 97, 97, 98, 98, 99, 5, 102, 102, 102, 102, 102, 102, 103,
			103, 103, 103, 104, 104, 105, 105, 106, 106, 106, 107, 107,
			108, 109, 109, 110, 110, 110, 111, 112, 113, 117, 115, 119,
			119, 119, 119, 119, 119, 120, 120, 120, 120, 121, 121, 122,
			122, 123, 123, 123, 124, 124, 125, 126, 126, 127, 127, 127,
			128, 129, 130, 134, 132, 136, 136, 136, 136, 136, 137, 137,
			137, 22, 22, 138, 138, 139, 139, 139, 140, 140, 141, 141, 141,
			142, 143, 144, 144, 145, 145, 146, 147, 148, 148, 151, 151,
			151, 151, 151, 152, 152, 152, 22, 22, 153, 153, 154, 154, 154,
			155, 155, 156, 156, 156, 157, 158, 159, 159, 160, 160, 161,
			162, 163, 163 };
		static final short[] SkinnyBentTail = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1,
			1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 6, 7, 7, 2, 2, 8, 8, 5, 5, 9,
			9, 10, 11, 11, 15, 16, 17, 13, 14, 20, 20, 20, 20, 20, 21, 21,
			21, 22, 22, 23, 23, 24, 24, 24, 25, 25, 26, 26, 26, 27, 28, 29,
			29, 30, 30, 31, 32, 33, 33, 36, 36, 36, 36, 36, 37, 37, 37, 38,
			38, 39, 39, 40, 40, 22, 41, 41, 42, 43, 44, 47, 47, 47, 47, 47,
			48, 48, 48, 48, 48, 48, 48, 48, 49, 49, 50, 50, 51, 51, 52, 53,
			53, 54, 54, 49, 49, 55, 55, 52, 52, 56, 56, 57, 22, 22, 61, 62,
			63, 59, 60, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 67, 67, 67,
			68, 68, 69, 69, 70, 70, 43, 71, 71, 72, 72, 68, 68, 73, 73, 43,
			43, 74, 74, 75, 76, 76, 80, 81, 82, 78, 79, 85, 85, 85, 85, 85,
			86, 86, 86, 87, 87, 88, 88, 89, 89, 90, 91, 91, 92, 43, 93, 96,
			96, 96, 96, 96, 97, 97, 97, 98, 98, 99, 99, 100, 100, 5, 101,
			101, 102, 103, 104, 107, 107, 107, 107, 107, 108, 108, 108,
			109, 109, 110, 110, 111, 111, 103, 112, 112, 113, 114, 115 };
		static final short[] SkinnyNormalTail = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1,
			1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 6, 7, 7, 2, 2, 8, 8, 5, 5, 9,
			9, 10, 11, 11, 15, 16, 17, 13, 14, 20, 20, 20, 20, 20, 21, 21,
			21, 22, 22, 23, 23, 24, 24, 24, 25, 25, 26, 26, 26, 27, 28, 29,
			29, 30, 30, 31, 32, 33, 33, 36, 36, 36, 36, 37, 37, 38, 38, 39,
			22, 42, 42, 42, 42, 42, 43, 43, 43, 43, 43, 43, 43, 43, 44, 44,
			45, 45, 46, 46, 47, 48, 48, 49, 49, 44, 44, 50, 50, 47, 47, 51,
			51, 52, 22, 22, 56, 57, 58, 54, 55, 61, 61, 61, 61, 61, 62, 62,
			62, 62, 62, 62, 62, 62, 63, 63, 64, 64, 65, 65, 38, 66, 66, 67,
			67, 63, 63, 68, 68, 38, 38, 69, 69, 70, 71, 71, 75, 76, 77, 73,
			74, 80, 80, 80, 80, 80, 81, 81, 81, 82, 82, 82, 83, 83, 83, 84,
			84, 5, 85, 85, 86, 86, 87, 87, 88, 89, 89, 90, 90, 92, 93, 96,
			96, 96, 96, 97, 97, 98, 98, 99, 100, 103, 103, 103, 103, 104,
			104, 105, 105, 106, 107 };
	}
	private static final int PrisonMate = 3118, LeverID = 10817,
	DoorKey = 6966;
	private int Unlocked, State = 0;
	private RSNPC BalloonToPop;
	private RSNPC Pete;
	private boolean TalkedToPete = false;
	private boolean Key = false;

	private boolean Lucky = false;

	@Override
	public boolean activateCondition() {
		if (game.isLoggedIn()) {
			Pete = npc.getNearestByName("Prison Pete");
			if (Pete != null) {
				return objects.getNearestByID(LeverID) != null;
			}
		}
		return false;
	}

	public boolean atLever() {
		if (iface.get(273).getChild(3).isValid()) {
			final Filter<RSModel> filter = RSModel
			.newInfinityFilter(setItemIDs(iface.get(273).getChild(3)
					.getModelID()));
			BalloonToPop = npc.getNearByFilter(new Filter<RSNPC>() {

				@Override
				public boolean accept(final RSNPC n) {
					return filter.accept(n.getModel());
				}
			});
			if (BalloonToPop != null) {
				return true;
			}
		}
		return false;
	}

	public boolean interfaceContains(final String s) {
		final RSInterface[] all = iface.getAll();
		for (final RSInterface iface : all) {
			if (iface != null) {
				final int count = iface.getChildren().length;
				for (int i = 0; i < count; i++) {
					if (iface.getChild(i).getText() != null
							&& iface.getChild(i).getText().contains(s)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int loop() {
		if (npc.getNearestByName("Prison Pete") == null) {
			return -1;
		}
		if (!TalkedToPete) {
			camera.setAltitude(true);
			if ((camera.getAngle() < 175) || (camera.getAngle() > 185)) {
				camera.setRotation(random(175, 185));
				return random(500, 750);
			}
		}
		switch (State) {
		case 0:
			Pete = npc.getNearestByName("Prison Pete");
			if (interfaceContains("Lucky you!")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Lucky = true;
				return random(500, 600);
			}
			if (interfaceContains("should leave")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Unlocked = 10;
				return random(500, 600);
			}
			if ((inventory.getCount(false) == 28)
					&& !inventory.contains(DoorKey)) {
				log(ScreenLog.prisonEnough);
				final RSObject depo = objects.getNearestByID(32924);
				if (depo != null) {
					if (!tile.onScreen(depo.getLocation())) {
						if (!walk
								.tileMM(depo.getLocation().randomizeTile(3, 3))) {
							walk.tileMM(depo.getLocation().randomizeTile(3, 3));
							return random(500, 700);
						}
						return random(1000, 1500);
					}
					camera.turnTo(depo, 20);
					if (objects.at(depo, "Deposit")) {
						sleep(random(1800, 2000));
						if (player.getMine().isMoving()) {
							sleep(random(200, 500));
						}
						if (iface.get(Constants.INTERFACE_DEPOSITBOX).isValid()) {
							sleep(random(700, 1200));
							final RSInterfaceChild fff = iface.getChild(11, 17);
							iface.clickChild(fff, (random(16, 17)), "Dep");
							sleep(random(700, 1200));
							iface.clickChild(11, 15);
						}
						return random(400, 500);
					}
					return random(500, 800);
				}
				return random(500, 600);
			}

			if (player.getMine().isMoving()) {
				return random(250, 500);
			}
			if (interfaceContains("minute")) {
				TalkedToPete = true;
				if (iface.canContinue()) {
					iface.clickContinue();
					return random(500, 600);
				}
				return random(500, 600);
			}

			if (iface.get(228).isValid()
					&& iface.get(228).containsText("How do")) {
				iface.clickChild(228, 3);
				return random(500, 600);
			}
			if (iface.canContinue()) {
				iface.clickContinue();
				return random(1000, 1200);
			}
			if (!TalkedToPete && Pete != null && !(iface.get(228).isValid())
					&& !iface.canContinue()) {
				if (!tile.onScreen(Pete.getLocation())) {
					walk.tileMM(Pete.getLocation());
					return random(1000, 1400);
				}
				if (npc.action(Pete, "talk")) {
					return random(1500, 1600);
				} else {
					camera.turnTo(Pete);
					return random(500, 600);
				}
			}
			if (Unlocked == 3) {
				State = 4;
				return random(250, 500);
			}
			if (Unlocked <= 2 && TalkedToPete) {
				State = 1;
				return random(500, 600);
			}
			return random(350, 400);

		case 1:
			// Figures out the balloon
			if (interfaceContains("Lucky you!")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Lucky = true;
				return random(500, 600);
			}
			if (interfaceContains("should leave")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Unlocked = 10;
				return random(500, 600);
			}
			if (iface.get(273).getChild(3).isValid()) {
				if (atLever()) {
					if (BalloonToPop != null
							&& iface.clickChild(273, 4, "Close")) {
						State = 2;
						return random(800, 900);
					}
					return random(500, 700);
				}
			}
			final RSObject lever = objects.getNearestByID(LeverID);
			if ((lever != null) && TalkedToPete) {
				if (!tile.onScreen(lever.getLocation())) {
					walk.tileMM(lever.getLocation());
					return random(1000, 1200);
				}
				if (!player.getMine().isMoving()
						&& tile.onScreen(lever.getLocation())) {
					// if (tiles.doAction(lever.getLocation(), 0.5, 0.5, 170,
					// "Pull")) {
					if (objects.at(lever, "Pull")) {
						sleep(random(1400, 1600));
						if (atLever()) {
							if (BalloonToPop != null
									&& iface.clickChild(273, 4, "Close")) {
								State = 2;
								return random(800, 900);
							}
							return random(500, 700);
						}
						return random(500, 600);
					} else {
						camera.turnTo(lever);
						return random(500, 600);
					}
				}
			}
			if (!TalkedToPete) {
				State = 0;
				return random(500, 600);
			}
			return random(500, 600);
		case 2:
			// Finds animal and pops it
			if (interfaceContains("Lucky you!")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Lucky = true;
				return random(500, 600);
			}
			if (interfaceContains("should leave")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Unlocked = 10;
				return random(500, 600);
			}
			if (player.getMine().isMoving()) {
				return random(250, 500);
			}
			if (BalloonToPop == null && Unlocked <= 2) {
				State = 1;
				return random(500, 700);
			}
			if (Unlocked == 3) {
				State = 4;
			}

			if (!inventory.contains(DoorKey)) {
				if (tile.onScreen(BalloonToPop.getLocation())) {
					npc.action(BalloonToPop, "Pop");
					return random(1200, 1400);
				} else {
					if (!player.getMine().isMoving()) {
						walk.tileMM(BalloonToPop.getLocation().randomizeTile(2,
								2));
						return random(500, 750);
					}
					return random(500, 750);
				}
			}
			if (inventory.contains(DoorKey)) {
				Key = false;
				State = 3;
				return random(500, 700);
			}
			return random(350, 400);

		case 3:
			// Goes to pete
			Pete = npc.getNearestByName("Prison Pete");
			if (player.getMine().isMoving()) {
				return random(250, 500);
			}
			if (interfaceContains("Lucky you!")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Lucky = true;
				return random(500, 600);
			}
			if (interfaceContains("should leave")) {
				if (iface.canContinue()) {
					iface.clickContinue();
				}
				State = 4;
				Unlocked = 10;
				return random(500, 600);
			}
			if (interfaceContains("you got all the keys")) {
				Key = true;
				Unlocked = 5;
				State = 4;
				BalloonToPop = null;
				if (iface.canContinue()) {
					iface.clickContinue();
					return random(500, 600);
				}
				return random(250, 500);
			}
			if (interfaceContains("Hooray")) {
				Key = true;
				if (iface.canContinue()) {
					iface.clickContinue();
					return random(500, 600);
				}
			}
			if (iface.canContinue()) {
				iface.clickContinue();
				return random(500, 600);
			}
			if (Pete != null && !tile.onScreen(Pete.getLocation())
					&& !(iface.get(243).isValid())) {
				walk.tileMM(Pete.getLocation());
				return random(400, 600);
			}
			if (!inventory.contains(DoorKey)
					&& (npc.getNearestByID(PrisonMate) != null)
					&& (Unlocked <= 2) && Key) {
				Unlocked++;
				State = 0;
				BalloonToPop = null;
				return random(350, 400);
			}

			if (inventory.contains(DoorKey) && !player.getMine().isMoving()) {
				inventory.clickItem(DoorKey, "Return");
				return random(1000, 2000);
			}
			if (!inventory.contains(DoorKey)
					&& (npc.getNearestByID(PrisonMate) != null)
					&& (Unlocked <= 2) && !Key) {
				State = 0;
				BalloonToPop = null;
				return random(350, 400);
			}

			return random(350, 400);
		case 4:
			// exits
			final RSTile doorTile = new RSTile(2086, 4459);
			if (Unlocked <= 2 && !Lucky) {
				State = 0;
				return random(500, 600);
			}
			if (!tile.onScreen(doorTile)) {
				walk.tileMM(doorTile);
				return random(400, 500);
			}
			if (tile.onScreen(doorTile)) {
				final RSObject gate = objects.getNearestByID(11177, 11178);
				if (gate != null) {
					objects.at(gate, "Open");
				}
				// tiles.doAction(new RSTile(2085, 4459), 1, 0, 30, "Open");
				return random(500, 600);
			}
			return random(200, 400);
		}
		return random(200, 400);
	}

	@Override
	public void onFinish() {
		if (Lucky) {
			log.warning(ScreenLog.prisonFail);
			sleep(5000, 10000);
			stopScript(false);
		}
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.prison, 9, 330);
		ScreenMouse.paint(render);
	}

	public short[] setItemIDs(final int b2p) {
		// sets the proper balloon id
		switch (b2p) {
		case 10749: // skinny bend at end of tail
			return Balloons.SkinnyBentTail;
		case 10750: // long tail, no bend at end of tail
			return Balloons.SkinnyNormalTail;
		case 10751: // fatty
			return Balloons.Fatty;
		case 10752: // horny
			return Balloons.Horny;
		}
		return new short[] {};
	}
}
