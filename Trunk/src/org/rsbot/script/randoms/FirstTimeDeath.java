package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 1.2 - 02/10/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "FirstTimeDeath", version = 1.2)
public class FirstTimeDeath extends Random implements PaintListener {

	private int step;
	private boolean exit;
	private RSNPC reaper;

	@Override
	public boolean activateCondition() {
		return ((reaper = npc.getNearestByID(8869)) != null)
		|| ((reaper = npc.getNearestByID(8870)) != null);
	}

	@Override
	public int loop() {
		if (!activateCondition()) {
			return -1;
		}
		camera.setAltitude(true);
		if (iface.canContinue() && !exit) {
			if (iface.getChild(241, 4).getText().contains("Yes?")) {
				step++;
				exit = true;
				return random(200, 400);
			} else if (iface.getChild(242, 5).getText().contains("Enjoy!")) {
				step++;
				exit = true;
			}
			iface.clickContinue();
			return random(200, 400);
		}
		switch (step) {
		case 0:
			final RSObject reaperChair = objects.getNearestByID(45802);
			objects.at(reaperChair, "Talk-to");
			sleep(random(1000, 1200));
			if (!iface.canContinue()) {
				walk.tileOnScreen(new RSTile(reaper.getLocation().getX() + 2,
						reaper.getLocation().getY() + 1));
				camera.turnTo(reaperChair);
			}
			break;

		case 1:
			final int portalID = 45803;
			final RSObject portal = objects.getNearestByID(portalID);
			final RSTile loc = player.getMine().getLocation();
			objects.at(portal, "Enter");
			sleep(random(1000, 1200));
			if (loc.distanceTo() < 10) {
				camera.turnTo(portal);
				if (!tile.onScreen(portal.getLocation())) {
					walk.tileOnScreen(portal.getLocation());
				}
			}
			break;
		}
		return random(200, 400);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.death, 9, 330);
		ScreenMouse.paint(render);
	}
}
