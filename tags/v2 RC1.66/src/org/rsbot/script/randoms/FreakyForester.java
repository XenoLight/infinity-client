package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSModel;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/*
 * @author Secret Spy
 * @version 2.7 - 04/16/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "FreakyForester", version = 2.7)
public class FreakyForester extends Random implements PaintListener,
MessageListener {

	static class Models {

		static final short[] oneTail = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2,
			3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 8, 8, 8, 8, 9, 9, 10,
			10, 10, 10, 10, 11, 11, 11, 11, 11, 12, 12, 13, 13, 14, 14, 14,
			14, 15, 15, 15, 16, 16, 17, 17, 17, 18, 18, 18, 18, 18, 19, 19,
			34, 23, 23, 24, 25, 25, 25, 25, 25, 26, 26, 27, 27, 28, 28, 29,
			29, 29, 30, 30, 31, 31, 32, 32, 32, 32, 33, 33, 60, 60, 60, 63,
			63, 65, 65, 65, 60, 64, 64, 66, 66, 66, 66, 66, 70, 70, 70, 70,
			70, 70, 71, 71, 71, 71, 74, 74, 74, 74, 74, 74, 74, 73, 73, 61,
			59, 72, 69, 69, 69, 69, 57, 57, 50, 50, 50, 56, 57, 77, 79, 82,
			41, 41, 41, 41, 42, 42, 42, 42, 43, 43, 43, 43, 43, 44, 44, 44,
			95, 95, 95, 95, 95, 95, 85, 85, 86, 86, 86, 87, 87, 101, 101,
			101, 101, 101, 101, 102, 102, 102, 102, 103, 103, 103, 104,
			104, 104, 105, 105, 105, 105, 105, 105, 105, 105, 106, 106,
			107, 107, 108, 108, 108, 94, 35, 35, 36, 36, 36, 37, 37, 38,
			110, 111, 111, 111, 111, 111, 111, 112, 113, 99, 99, 99, 100,
			100, 100, 131, 97, 98, 129, 129, 129, 130, 130, 130, 130, 130,
			130, 130, 131, 131, 131, 131, 131, 131, 126, 126, 116, 146,
			118, 109, 120, 90, 136, 136, 143, 148, 139, 139, 149, 149, 149,
			153, 154, 154, 156, 157, 157, 150, 151, 151, 151, 152, 152,
			152, 155, 158, 159, 159, 159, 160, 160, 161, 161, 161, 164,
			164, 164, 164, 165, 165, 167, 167, 164, 164, 164, 164, 172,
			172, 172, 172, 172, 172, 171, 171, };
		static final short[] twoTail = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2,
			3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 8, 8, 8, 8, 9, 9, 10,
			10, 10, 10, 10, 11, 11, 11, 11, 11, 12, 12, 13, 13, 14, 14, 14,
			14, 15, 15, 15, 16, 16, 17, 17, 17, 18, 18, 18, 18, 18, 19, 19,
			34, 23, 23, 24, 25, 25, 25, 25, 25, 26, 26, 27, 27, 28, 28, 29,
			29, 29, 30, 30, 31, 31, 32, 32, 32, 32, 33, 33, 60, 60, 60, 63,
			63, 65, 65, 65, 60, 64, 64, 66, 66, 66, 66, 66, 70, 70, 70, 70,
			70, 70, 71, 71, 71, 71, 74, 74, 74, 74, 74, 74, 74, 73, 73, 61,
			59, 72, 69, 69, 69, 69, 57, 57, 50, 50, 50, 56, 57, 77, 79, 82,
			41, 41, 41, 41, 42, 42, 42, 42, 43, 43, 43, 43, 43, 44, 44, 44,
			95, 95, 95, 95, 95, 95, 85, 85, 86, 86, 86, 87, 87, 101, 101,
			101, 101, 101, 101, 102, 102, 102, 102, 103, 103, 103, 104,
			104, 104, 105, 105, 105, 105, 105, 105, 105, 105, 106, 106,
			107, 107, 108, 108, 108, 94, 35, 35, 36, 36, 36, 37, 37, 38,
			110, 111, 111, 111, 111, 111, 111, 112, 113, 99, 99, 99, 100,
			100, 100, 131, 97, 98, 129, 129, 129, 130, 130, 130, 130, 130,
			130, 130, 131, 131, 131, 131, 131, 131, 126, 126, 116, 146,
			118, 109, 120, 90, 136, 136, 143, 148, 139, 139, 149, 149, 149,
			149, 149, 149, 150, 150, 150, 150, 151, 151, 151, 151, 152,
			152, 152, 152, 153, 153, 153, 154, 154, 154, 154, 155, 155,
			166, 166, 156, 165, 155, 155, 156, 156, 169, 169, 172, 172,
			172, 172, 172, 172, 161, 162, 162, 162, 163, 163, 163, 164,
			158, 158, 158, 175, 175, 175, 175, 176, 176, 176, 176, 184,
			184, 185, 185, 178, 182, 182, 182, 182, 177, 189, 189, 189,
			189, 190, 190, 188, 188, 179, 179, 179, 160, };
		static final short[] threeTail = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2,
			2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 8, 8, 8, 8, 9, 9,
			10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 12, 12, 13, 13, 14, 14,
			14, 14, 15, 15, 15, 16, 16, 17, 17, 17, 18, 18, 18, 18, 18, 19,
			19, 34, 23, 23, 24, 25, 25, 25, 25, 25, 26, 26, 27, 27, 28, 28,
			29, 29, 29, 30, 30, 31, 31, 32, 32, 32, 32, 33, 33, 60, 60, 60,
			63, 63, 65, 65, 65, 60, 64, 64, 66, 66, 66, 66, 66, 70, 70, 70,
			70, 70, 70, 71, 71, 71, 71, 74, 74, 74, 74, 74, 74, 74, 73, 73,
			61, 59, 72, 69, 69, 69, 69, 57, 57, 50, 50, 50, 56, 57, 77, 79,
			82, 41, 41, 41, 41, 42, 42, 42, 42, 43, 43, 43, 43, 43, 44, 44,
			44, 95, 95, 95, 95, 95, 95, 85, 85, 86, 86, 86, 87, 87, 101,
			101, 101, 101, 101, 101, 102, 102, 102, 102, 103, 103, 103,
			104, 104, 104, 105, 105, 105, 105, 105, 105, 105, 105, 106,
			106, 107, 107, 108, 108, 108, 94, 35, 35, 36, 36, 36, 37, 37,
			38, 110, 111, 111, 111, 111, 111, 111, 112, 113, 99, 99, 99,
			100, 100, 100, 131, 97, 98, 129, 129, 129, 130, 130, 130, 130,
			130, 130, 130, 131, 131, 131, 131, 131, 131, 126, 126, 116,
			146, 118, 109, 120, 90, 136, 136, 143, 148, 139, 139, 149, 149,
			149, 149, 149, 149, 150, 150, 150, 150, 151, 151, 151, 151,
			152, 152, 152, 152, 152, 153, 153, 153, 154, 154, 154, 154,
			155, 155, 167, 167, 156, 166, 155, 155, 156, 156, 170, 170,
			173, 173, 173, 173, 173, 173, 176, 176, 176, 176, 176, 176,
			163, 163, 164, 164, 164, 180, 180, 180, 180, 181, 181, 183,
			183, 181, 178, 180, 180, 180, 180, 188, 188, 188, 188, 188,
			188, 187, 187, 192, 192, 192, 158, 158, 158, 158, 159, 159,
			159, 160, 160, 160, 161, 200, 200, 200, 200, 199, 201, 201,
			201, 201, 203, 203, 203, 203, 197, 197, 197, 197, 202, 202,
			206, 206, 209, 209, };
		static final short[] fourTail = { 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2,
			2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 6, 6, 6, 7, 8, 8, 8, 8, 9, 9,
			10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 12, 12, 13, 13, 14, 14,
			14, 14, 15, 15, 15, 16, 16, 17, 17, 17, 18, 18, 18, 18, 18, 19,
			19, 34, 23, 23, 24, 25, 25, 25, 25, 25, 26, 26, 27, 27, 28, 28,
			29, 29, 29, 30, 30, 31, 31, 32, 32, 32, 32, 33, 33, 60, 60, 60,
			63, 63, 65, 65, 65, 60, 64, 64, 66, 66, 66, 66, 66, 70, 70, 70,
			70, 70, 70, 71, 71, 71, 71, 74, 74, 74, 74, 74, 74, 74, 73, 73,
			61, 59, 72, 69, 69, 69, 69, 57, 57, 50, 50, 50, 56, 57, 77, 79,
			82, 41, 41, 41, 41, 42, 42, 42, 42, 43, 43, 43, 43, 43, 44, 44,
			44, 95, 95, 95, 95, 95, 95, 85, 85, 86, 86, 86, 87, 87, 101,
			101, 101, 101, 101, 101, 102, 102, 102, 102, 103, 103, 103,
			104, 104, 104, 105, 105, 105, 105, 105, 105, 105, 105, 106,
			106, 107, 107, 108, 108, 108, 94, 35, 35, 36, 36, 36, 37, 37,
			38, 110, 111, 111, 111, 111, 111, 111, 112, 113, 99, 99, 99,
			100, 100, 100, 131, 97, 98, 129, 129, 129, 130, 130, 130, 130,
			130, 130, 130, 131, 131, 131, 131, 131, 131, 126, 126, 116,
			146, 118, 109, 120, 90, 136, 136, 143, 148, 139, 139, 149, 149,
			149, 149, 149, 149, 150, 150, 150, 150, 150, 150, 150, 151,
			151, 152, 152, 164, 164, 166, 167, 167, 169, 169, 153, 154,
			154, 154, 155, 155, 155, 155, 156, 156, 156, 156, 177, 177,
			177, 177, 163, 165, 165, 168, 168, 170, 171, 171, 171, 171,
			171, 181, 181, 181, 181, 172, 172, 173, 173, 184, 184, 173,
			180, 180, 180, 180, 180, 180, 180, 180, 161, 161, 162, 162,
			160, 190, 190, 190, 190, 190, 190, 190, 190, 195, 195, 195,
			195, 158, 158, 157, 176, 176, 176, 176, 157, 157, 198, 198,
			201, 201, 182, 182, 183, 183, 204, 204, 205, 205, 206, 206,
			189, 189, 189, 189, 208, 208, 210, 210, 208, 208, 192, 192,
			211, 211, 213, 213, 211, 211, 214, 214, 215, 215, 160, 160,
			160, 160, 193, 193, 218, 218, 219, 219, 157, 157, };
	}
	private RSNPC Forester;
	private static final int ForesterID = 2458;
	private static final int SearchIfaceID = 242;
	private static final int IFaceDepoisitBox = 11;
	private static final int IFaceDepoisitBoxCloseButton = 15;
	private static final int PortalID = 15645;
	private static final RSTile walkToTile = new RSTile(2610, 4775);
	private boolean unequip = false;
	private boolean done = false;
	short[] phe = {};

	Filter<RSNPC> pheasantFilter = new Filter<RSNPC>() {

		@Override
		public boolean accept(final RSNPC npc) {
			// log("phe.length = " + phe.length);
			final Filter<RSModel> modelFilter = RSModel.newInfinityFilter(phe);
			return modelFilter.accept(npc.getModel());
		}
	};

	@Override
	public boolean activateCondition() {
		if (!game.isLoggedIn()) {
			return false;
		}
		Forester = npc.getNearestByID(ForesterID);
		if (Forester != null) {
			sleep(random(2000, 3000));
			if (npc.getNearestByID(ForesterID) != null) {
				final RSObject portal = objects.getNearestByID(PortalID);
				return portal != null;
			}
		}
		return false;
	}

	public boolean DepositOpen() {
		return iface.getChild(IFaceDepoisitBox, IFaceDepoisitBoxCloseButton)
		.isValid();
	}

	public int getState() {
		if (done) {
			return 3;
		} else if (iface.canContinue()) {
			return 1;
		} else if (phe.length == 0) {
			return 0;
		} else if (inventory.contains(6178)) {
			return 0;
		} else if (phe.length > 0) {
			return 2;
		} else {
			return 0;
		}
	}

	@Override
	public int loop() {
		Forester = npc.getNearestByID(ForesterID);
		if (Forester == null) {
			return -1;
		}
		if (player.getMine().getAnimation() != -1) {
			return random(3000, 5000);
		} else if (player.getMine().isMoving()) {
			return random(200, 500);
		}
		if (!done) {
			done = searchText(241, "Thank you")
			|| iface.getChild(242, 4).containsText("leave");
		}
		if (inventory.contains(6179)) {
			phe = new short[] {};
			inventory.clickItem(6179, "Drop");
			return random(500, 900);
		}
		if (unequip && (inventory.getCount(false) != 28)) {
			if (game.getCurrentTab() != TAB_EQUIPMENT) {
				game.openTab(TAB_EQUIPMENT);
				sleep(random(1000, 1500));
				iface.clickChild(INTERFACE_EQUIPMENT, 17);
				return (random(1000, 1500));
			}
			return (random(100, 500));
		}
		if ((DepositOpen() || inventory.isFull()) && !inventory.contains(6178)) {
			if (DepositOpen() && inventory.getCount() == 28) {
				iface.clickChild(11, (random(21, 27)), "Deposit");
				return random(1000, 1500);
			} else if (DepositOpen()) {
				bank.close();
				return random(1000, 1500);
			}
			final RSObject box = objects.getNearestByID(32931);
			if ((!tile.onScreen(box.getLocation()) && ((walk.getDestination()
					.distanceTo()) < 8))
					|| (walk.getDestination().distanceTo() > 40)) {
				if (!walk.tileMM(box.getLocation())) {
					walk.tileMM(box.getLocation().randomizeTile(3, 3));
				}
				sleep(random(1200, 1400));
			}
			if (objects.at(box, "Deposit")) {
				return random(800, 1200);
			}
		}
		final int state = getState();
		switch (state) {
		case 0: // Talk to forester
			if (tile.onScreen(Forester.getLocation())
					&& (Forester.getLocation().distanceTo() <= 5)) {
				npc.action(Forester, "Talk");
			} else if (Forester.getLocation().distanceTo() >= 5) {
				walk.tileMM(walk.getClosestTileOnMap(Forester.getLocation()
						.randomizeTile(3, 3)));
				camera.turnTo(Forester.getLocation().randomizeTile(3, 3));
			}
			return random(500, 800);
		case 1: // Talking
			// log("Talking"); //debug REMOVEME
			if (searchText(SearchIfaceID, " one")) {
				phe = Models.oneTail;
			} else if (searchText(SearchIfaceID, " two")) {
				phe = Models.twoTail;
			} else if (searchText(SearchIfaceID, " three")) {
				phe = Models.threeTail;
			}
			if (searchText(SearchIfaceID, " four")) {
				phe = Models.fourTail;
			}
			if (iface.clickContinue()) {
				return random(500, 800);
			}
			return random(200, 500);
		case 2: // Kill pheasant
			if (phe.length == 0) {
				return random(200, 500);
			}
			final RSNPC pheasant = npc.getNearByFilter(pheasantFilter);
			final RSGroundItem t = ground.getNearestItemByID(6178);
			if (t != null) {
				t.action("Take");
				return random(600, 900);
			} else if (pheasant != null) {
				// log("Pheasant ID = " + pheasant.getID()); //debug remove
				if (tile.onScreen(pheasant.getLocation())
						&& (pheasant.getLocation().distanceTo() <= 5)) {
					npc.action(pheasant, "Attack");
					return random(1000, 1500);
				} else if (pheasant.getLocation().distanceTo() >= 5) {
					walk.tileMM(walk.getClosestTileOnMap(pheasant.getLocation()
							.randomizeTile(3, 3)));
					camera.turnTo(pheasant.getLocation().randomizeTile(3, 3));
				}
			} else {
				return random(2000, 5000);
			}
		case 3: // Get out
			if (!tile.onScreen(walkToTile)) {
				if (walkToTile.isOnMinimap()) {
					walk.tileMM(walkToTile);
				} else {
					walk.tileMM(Forester.getLocation().randomizeTile(5, 5));
				}
				return random(900, 1200);
			}

			final RSObject Portal = objects.getNearestByID(PortalID);
			if (Portal == null) {
				log.warning(ScreenLog.forestPort);
				return random(800, 1200);
			}
			if (objects.at(Portal, "Enter")) {
				return random(800, 1200);
			}
			return random(200, 500);
		}
		return random(1000, 1500);
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		final String serverString = e.getMessage();
		if (serverString.contains("no ammo left")) {
			unequip = true;
		}
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.forest, 9, 330);
		ScreenMouse.paint(render);
	}

	public boolean searchText(final int interfac, final String text) {
		final RSInterface talkFace = iface.get(interfac);
		if (!talkFace.isValid()) {
			return false;
		}
		for (int i = 0; i < talkFace.getChildCount(); i++) {
			if (talkFace.getChild(i).containsText(text)) {
				return true;
			}
		}

		return false;
	}
}
