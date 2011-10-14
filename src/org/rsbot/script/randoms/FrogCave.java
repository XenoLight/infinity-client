package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;

/**
 * @author Secret Spy
 * @version 2.4 - 02/10/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "FrogCave", version = 2.4)
public class FrogCave extends Random {

	private RSNPC frog;
	private boolean talkedToHerald, talkedToFrog;
	private int tries;

	@Override
	public boolean activateCondition() {
		if (!game.isLoggedIn()) {
			return false;
		} else if ((npc.getNearestByName("Frog Herald") != null)
				&& (objects.getNearestByID(5917) != null)) {
			sleep(random(2000, 3000));
			return (npc.getNearestByName("Frog Herald") != null)
			&& (objects.getNearestByID(5917) != null);
		}
		return false;
	}

	private RSNPC FindFrog() {
		final int[] FrogNPC = Bot.getClient().getRSNPCIndexArray();
		for (final int NPCIndex : FrogNPC) {
			final Node Node1 = Calculations.findNodeByID(
					Bot.getClient().getRSNPCNC(), NPCIndex);
			if (Node1 == null || !(Node1 instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC npc = new RSNPC(((RSNPCNode) Node1).getRSNPC());
			try {
				if (npc.isMoving()) {
					continue;
				}
				if (npc.getHeight() == -278) {
					return npc;
				}
			} catch (final Exception e) {
			}
		}
		return null;
	}

	@Override
	public int loop() {
		try {
			if (!activateCondition()) {
				talkedToHerald = false;
				frog = null;
				tries = 0;
				return -1;
			}
			if (willContinue()) {
				if (!talkedToHerald) {
					final RSInterfaceChild heraldTalkComp = iface.getChild(242,
							4);
					talkedToHerald = heraldTalkComp.isValid()
					&& (heraldTalkComp.containsText("crown") || heraldTalkComp
							.containsText("is still waiting"));
				}
				if (!iface.clickContinue()) {
					iface.clickChild(65, 6);
				}
				return random(600, 800);
			}
			if (player.getMine().isMoving()) {
				return random(600, 800);
			}
			if (!talkedToHerald) {
				final RSNPC herald = npc.getNearestByName("Frog Herald");
				if (herald.distanceTo() < 5) {
					if (!tile.onScreen(herald.getLocation())) {
						camera.turnTo(herald);
					}
					npc.action(herald, "Talk-to");
					return random(500, 1000);
				} else {
					walk.tileMM(herald.getLocation());
					return random(500, 700);
				}
			}
			if (frog == null) {
				frog = FindFrog();
				if (frog != null) {
					log(ScreenLog.frogPrince + frog.getID());
				}
			}
			if (frog != null && frog.getLocation() != null
					&& (!talkedToFrog || !willContinue())) {
				if (frog.distanceTo() < 5) {
					if (!tile.onScreen(frog.getLocation())) {
						camera.turnTo(frog);
					}
					if (npc.action(frog, "Talk-to Frog")) {
						wait(750, 1250);
						talkedToFrog = willContinue();
					}
					return random(900, 1000);
				} else {
					walk.tileMM(frog.getLocation());
					return random(500, 700);
				}
			} else {
				tries++;
				if (tries > 200) {
					tries = 0;
					talkedToHerald = false;
				}
				return random(200, 400);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return random(200, 400);
	}

	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.frog, 9, 330);
		ScreenMouse.paint(render);
	}

	private boolean willContinue() {
		return iface.canContinue() || iface.getChild(65, 6).isValid();
	}
}
