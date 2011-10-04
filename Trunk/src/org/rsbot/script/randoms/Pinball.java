package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSObject;

/**
 * @author Secret Spy
 * @version 2.9 - 02/24/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Pinball", version = 2.9)
public class Pinball extends Random implements PaintListener {

	private static final int[] OBJ_PILLARS = { 15000, 15002, 15004, 15006,
		15008 };
	private static final int[] OBJ_ACTIVATE = { 15000, 15002, 15004, 15006,
		15007, 15008 };
	private static final int INTERFACE_PINBALL = 263;

	// private int continueCounter = 0;
	@Override
	public boolean activateCondition() {
		return game.isLoggedIn()
		&& objects.getNearestByID(OBJ_ACTIVATE) != null;
	}


	private int getScore() {
		final RSInterfaceChild score = iface.get(INTERFACE_PINBALL).getChild(1);
		try {
			return Integer.parseInt(score.getText().split(" ")[1]);
		} catch (final java.lang.ArrayIndexOutOfBoundsException t) {
			return 10;
		}
	}

	@Override
	public int loop() {
		camera.setAltitude(true);
		if (!activateCondition()) {
			return -1;
		}
		//
		// if (interfaces.iface.canContinue() && continueCounter < 10) {
		// log.info("trying to continue");
		// interfaces.iface.clickContinue();
		// continueCounter++;
		//
		// log.info("Clicked continue");
		// return random(1000, 1200);
		// }
		// continueCounter = 0;

		if (player.getMine().isMoving()
				|| player.getMine().getAnimation() != -1) {
			return random(200, 300);
		}

		if (getScore() >= 10) {
			final int OBJ_EXIT = 15010;
			final RSObject exit = objects.getNearestByID(OBJ_EXIT);
			if (exit != null) {
				if (tile.onScreen(exit.getLocation())
						&& objects.at(exit, "Exit")) {
					sleep(random(2000, 2200));
					objects.at(exit, "Exit");
					return random(1000, 1200);
				} else {
					camera.setCompass('s');
					walk.tileOnScreen(exit.getLocation());
					return random(1400, 1500);
				}
			}
		}

		final RSObject pillar = objects.getNearestByID(OBJ_PILLARS);

		if (pillar != null) {
			// log("Found pillar: " + pillar.getID() + " - " +
			// pillar.getLocation());
			if (!pillar.isOnScreen()) {
				walk.tileOnScreen(pillar.getLocation());
				return random(200, 2500);
			}

			wait(random(300, 500));

			
			if (!pillar.action("Tag")) {
				return random(500, 1000);
			}
			
			final int before = getScore();
			for (int i = 0; i < 100; i++) {
				if (getScore() > before) {
					return random(50, 100);
				}
				wait(random(25, 75));
			}
			return random(800, 1100);
		}
		return random(200, 400);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.pin, 9, 330);
		ScreenMouse.paint(render);
	}
}
