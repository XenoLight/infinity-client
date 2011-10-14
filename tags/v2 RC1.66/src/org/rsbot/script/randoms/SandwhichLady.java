package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/**
 * @author Secret Spy
 * @version 2.4 - 02/11/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "SandwichLady", version = 2.4)
public class SandwhichLady extends Random implements PaintListener {

	final static int IDInterfaceSandwhichWindow = 297;
	final static int IDInterfaceSandwhichWindowText = 48;
	final static int IDInterfaceTalk = 243;
	final static int IDInterfaceTalkText = 7;
	final static int[] IDItems = { 10728, 10732, 10727, 10730, 10726, 45666,
		10731 };
	final static int IDSandwhichLady = 8630;
	final static String[] NameItems = { "chocolate", "triangle", "roll", "pie",
		"baguette", "doughnut", "square" };
	final boolean Debug = false;

	@Override
	public boolean activateCondition() {
		final RSNPC Lady = npc.getNearestByID(IDSandwhichLady);
		return Lady != null;
	}

	@Override
	public int loop() {
		final RSInterface Chat1 = iface.get(243);
		if (Chat1.isValid()) {
			iface.clickChild(243, 7);
			return random(900, 1200);
		}
		if (!activateCondition()) {
			return -1;
		}
		// Leaves random
		final int[] PortalID = { 12731, 11373 };
		if (iface.get(242).getChild(4).containsText("The exit portal's")) {
			final RSObject Portal = objects.getNearestByID(PortalID);
			if (Portal != null) {
				if (!tile.onScreen(Portal.getLocation())) {
					walk.tileOnScreen(Portal.getLocation());
				} else {
					objects.at(Portal, "Enter");
					return random(1000, 1500);
				}
			}
		}
		// Check if we need to press continue, on the talk interface
		if (iface.get(SandwhichLady.IDInterfaceTalk).isValid()) {
			iface.clickChild(SandwhichLady.IDInterfaceTalk,
					SandwhichLady.IDInterfaceTalkText);
			return random(900, 1200);
		}

		// Check if the sandwhich window is open
		if (iface.get(SandwhichLady.IDInterfaceSandwhichWindow).isValid()) {
			final RSInterface Window = iface
			.get(SandwhichLady.IDInterfaceSandwhichWindow);
			int offset = -1;
			final String txt = Window.getChild(
					SandwhichLady.IDInterfaceSandwhichWindowText).getText();
			for (int off = 0; off < SandwhichLady.NameItems.length; off++) {
				if (txt.contains(SandwhichLady.NameItems[off])) {
					offset = off;
					if (Debug) {
						log.config("Found: " + SandwhichLady.NameItems[off]
						                                             + " - ID: " + SandwhichLady.IDItems[off]);
					}
				}
			}
			for (int i = 7; i < 48; i++) {
				final RSInterfaceChild Inf = Window.getChild(i);

				if (Debug) {
					log.config("child[" + i + "] ID: " + Inf.getModelID()
							+ " == " + SandwhichLady.IDItems[offset]);
				}
				if (Inf.getModelID() == SandwhichLady.IDItems[offset]) {
					iface.clickChild(Inf);
					sleep(random(900, 1200)); // Yea, use a sleep here! (Waits
					// are allowed in randoms.)
					if (!iface.get(SandwhichLady.IDInterfaceSandwhichWindow)
							.isValid()) {
						log.config(ScreenLog.eat
								+ SandwhichLady.NameItems[offset]);
						sleep(6000);
						return random(900, 1500);
					}
				}

			}
			return random(900, 1200);
		}
		final RSInterface Chat2 = iface.get(242);
		if (Chat2.isValid()) {
			iface.clickChild(242, 6);
			return random(900, 1200);
		}
		// Talk to the lady
		final RSNPC Lady = npc.getNearestByID(SandwhichLady.IDSandwhichLady);
		if (Lady != null && Lady.getAnimation() == -1) {
			if (!tile.onScreen(Lady.getLocation())) {
				walk.tileOnScreen(Lady.getLocation());
			} else {
				npc.action(Lady, "talk");
				return random(1000, 1500);
			}
		}
		return random(900, 1200);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.sand, 9, 330);
		ScreenMouse.paint(render);
	}
}
