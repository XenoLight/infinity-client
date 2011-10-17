package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 1.3 - 02/24/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Certer", version = 1.3)
public class Certer extends Random implements PaintListener {

	private final int[] MODEL_IDS = { 2807, 8828, 8829, 8832, 8833, 8834, 8835,
			8836, 8837 };
	private final int[] bookPiles = { 42352, 42354 };
	private final String[] ITEM_NAMES = { "bowl", "battleaxe", "fish",
			"shield", "helmet", "ring", "shears", "sword", "spade" };

	private boolean readyToLeave = false;
	private int failCount = 0;

	@Override
	public boolean activateCondition() {
		return game.isLoggedIn() && objects.getNearestByID(bookPiles) != null;
	}

	@Override
	public int loop() {
		camera.setAltitude(true);
		if (!activateCondition() && readyToLeave) {
			readyToLeave = false;
			failCount = 0;
			log(ScreenLog.cerFinish);
			return -1;
		}

		if (iface.getChild(241, 4).containsText("Ahem, ")) {
			readyToLeave = false;
		}

		if (iface.getChild(241, 4).containsText("Correct.")
				|| iface.getChild(241, 4).containsText("You can go now.")) {
			readyToLeave = true;
		}

		if (readyToLeave) {
			final int PORTAL_ID = 11368;
			final RSObject portal = objects.getNearestByID(PORTAL_ID);
			if (portal != null) {
				final RSTile portalLocation = portal.getLocation();
				if (portal.distanceTo() < 4) {
					objects.at(portal, "Enter");
					return random(3000, 4000);
				} else {
					walk.tileMM(walk.randomizeTile(
							new RSTile(portalLocation.getX() - 1,
									portalLocation.getY()), 1, 1));
					return random(6000, 8000);
				}
			}
		}

		if (iface.getChild(184, 0).isValid()) {
			final int modelID = iface.getChild(184, 8).getChildren()[3]
			                                                         .getModelID();
			String itemName = null;
			for (int i = 0; i < MODEL_IDS.length; i++) {
				if (MODEL_IDS[i] == modelID) {
					itemName = ITEM_NAMES[i];
				}
			}

			if (itemName == null) {
				log(ScreenLog.cerID + modelID);
				failCount++;
				if (failCount > 10) {
					stopScript(false);
					return -1;
				}
				return random(1000, 2000);
			}

			for (int j = 0; j < 3; j++) {
				final RSInterfaceChild ifa = iface.getChild(184, 8)
				.getChildren()[j];
				if (ifa.containsText(itemName)) {
					ifa.click();
					return random(3000, 5000);
				}
			}
		}

		if (iface.canContinue()) {
			iface.clickContinue();
			return random(3000, 4000);
		}

		final RSNPC certer = npc.getNearestByName("Niles", "Miles", "Giles");
		if (certer != null) {
			if (certer.distanceTo() < 4) {
				npc.action(certer, "Talk-to");
				return random(4000, 5000);
			} else {
				final RSTile certerLocation = certer.getLocation();
				walk.tileMM(walk.randomizeTile(new RSTile(
						certerLocation.getX() + 2, certerLocation.getY()), 1, 1));
				return random(6000, 8000);
			}
		}

		failCount++;
		if (failCount > 10) {
			stopScript(false);
			return -1;
		}
		return random(1000, 2000);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.cer, 9, 330);
		ScreenMouse.paint(render);
	}
}