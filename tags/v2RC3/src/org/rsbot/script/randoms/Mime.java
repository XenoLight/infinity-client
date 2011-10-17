package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 1.6 - 02/24/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Mime", version = 1.6)
public class Mime extends Random implements PaintListener {

	private enum Stage {

		click, findMime, findAnimation, clickAnimation, wait
	}

	private int animation;
	private RSNPC mime;

	@Override
	public boolean activateCondition() {
		final RSNPC Mime = npc.getNearestByID(1056);
		return (Mime != null) && (Mime.getLocation().distanceTo() < 15);
	}

	private boolean clickAnimation(final String find) {
		if (!iface.get(188).isValid()) {
			return false;
		}
		for (int a = 0; a < iface.get(188).getChildCount(); a++) {
			if (iface.get(188).getChild(a).getText().contains(find)) {
				log(ScreenLog.mimeClick2 + find);
				return iface.clickChild(188, a);
			}
		}
		return false;
	}

	private RSNPC getNPCAt(final RSTile t) {
		final int[] ValidNPCs = Bot.getClient().getRSNPCIndexArray();
		for (final int Element : ValidNPCs) {
			final Node localNode = Calculations.findNodeByID(Bot.getClient()
					.getRSNPCNC(), Element);
			if (localNode == null || !(localNode instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC Monster = new RSNPC(((RSNPCNode) localNode).getRSNPC());
			try {
				if (Monster.getLocation().equals(t)) {
					return Monster;
				}
			} catch (final Exception ignored) {
			}
		}
		return null;
	}

	private Stage getStage() {
		if (iface.canContinue()
				&& player.getMine().getLocation()
				.equals(new RSTile(2008, 4764))) {
			return Stage.click;
		} else if (mime == null) {
			return Stage.findMime;
		} else if ((iface.get(372).getChild(2).getText().contains("Watch") || iface
				.get(372).getChild(3).getText().contains("Watch"))
				&& (mime.getAnimation() != -1) && (mime.getAnimation() != 858)) {
			return Stage.findAnimation;
		} else if (iface.get(188).isValid()) {
			return Stage.clickAnimation;
		} else {
			return Stage.wait;
		}
	}

	@Override
	public int loop() {
		camera.setAltitude(true);
		if (!activateCondition()) {
			return -1;
		}
		switch (getStage()) {
		case click:
			iface.clickContinue();
			sleep(random(1500, 2000));
			return random(200, 400);

		case findMime:
			if (((mime = npc.getNearestByID(1056)) == null)
					&& ((mime = getNPCAt(new RSTile(2011, 4762))) == null)) {
				log.warning(ScreenLog.mimeMime);
				return -1;
			}
			return random(200, 400);

		case findAnimation:
			animation = mime.getAnimation();
			log.config(ScreenLog.mimeAni1 + animation);
			sleep(1000);
			if (iface.get(188).isValid()) {
				return random(400, 800);
			}
			final long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start >= 5000) {
				if (iface.get(188).isValid()) {
					return random(1000, 1600);
				}
				sleep(random(1000, 1500));
			}
			return random(200, 400);

		case clickAnimation:
			log.config(ScreenLog.mimeClick + animation);
			if ((animation != -1) && (animation != 858)) {
				switch (animation) {
				case 857:
					clickAnimation("Think");
					break;
				case 860:
					clickAnimation("Cry");
					break;
				case 861:
					clickAnimation("Laugh");
					break;
				case 866:
					clickAnimation("Dance");
					break;
				case 1128:
					clickAnimation("Glass Wall");
					break;
				case 1129:
					clickAnimation("Lean");
					break;
				case 1130:
					clickAnimation("Rope");
					break;
				case 1131:
					clickAnimation("Glass Box");
					break;
				default:
					log.config(ScreenLog.mimeAni + animation + ScreenLog.info);
					return random(2000, 3000);
				}
			}
		case wait:
			return random(200, 400);
		}
		return random(200, 400);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.mime, 9, 330);
		ScreenMouse.paint(render);
	}
}
