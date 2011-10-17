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
 * @version 1.1 - 02/10/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "DrillDemon", version = 1.1)
public class DrillDemon extends Random implements PaintListener {

	public int demonID = 2790;
	public int sign1;
	public int sign2;
	public int sign3;
	public int sign4;

	@Override
	public boolean activateCondition() {
		return playerInArea(3167, 4822, 3159, 4818);
	}

	@Override
	public int loop() {
		camera.setAltitude(true);
		camera.setCompass('N');

		if (player.getMine().isMoving()
				|| (player.getMine().getAnimation() != -1)) {
			return random(1200, 1500);
		}

		final RSNPC demon = npc.getNearestByID(demonID);
		final RSObject mat1 = objects.getNearestByID(10076);
		final RSObject mat2 = objects.getNearestByID(10077);
		final RSObject mat3 = objects.getNearestByID(10078);
		final RSObject mat4 = objects.getNearestByID(10079);

		if (demon == null) {
			return -1;
		}

		myclickContinue();
		sleep(random(750, 1000));

		if (iface.get(148).isValid()) {
			switch (settings.get(531)) {
			case 668:
				sign1 = 1;
				sign2 = 2;
				sign3 = 3;
				sign4 = 4;
				break;
			case 675:
				sign1 = 2;
				sign2 = 1;
				sign3 = 3;
				sign4 = 4;
				break;
			case 724:
				sign1 = 1;
				sign2 = 3;
				sign3 = 2;
				sign4 = 4;
				break;
			case 738:
				sign1 = 3;
				sign2 = 1;
				sign3 = 2;
				sign4 = 4;
				break;
			case 787:
				sign1 = 2;
				sign2 = 3;
				sign3 = 1;
				sign4 = 4;
				break;
			case 794:
				sign1 = 3;
				sign2 = 2;
				sign3 = 1;
				sign4 = 4;
				break;
			case 1116:
				sign1 = 1;
				sign2 = 2;
				sign3 = 4;
				sign4 = 3;
				break;
			case 1123:
				sign1 = 2;
				sign2 = 1;
				sign3 = 4;
				sign4 = 3;
				break;
			case 1228:
				sign1 = 1;
				sign2 = 4;
				sign3 = 2;
				sign4 = 3;
				break;
			case 1249:
				sign1 = 4;
				sign2 = 1;
				sign3 = 2;
				sign4 = 3;
				break;
			case 1291:
				sign1 = 2;
				sign2 = 4;
				sign3 = 1;
				sign4 = 3;
				break;
			case 1305:
				sign1 = 4;
				sign2 = 2;
				sign3 = 1;
				sign4 = 3;
				break;
			case 1620:
				sign1 = 1;
				sign2 = 3;
				sign3 = 4;
				sign4 = 2;
				break;
			case 1634:
				sign1 = 3;
				sign2 = 1;
				sign3 = 4;
				sign4 = 2;
				break;
			case 1676:
				sign1 = 1;
				sign2 = 4;
				sign3 = 3;
				sign4 = 2;
				break;
			case 1697:
				sign1 = 4;
				sign2 = 1;
				sign3 = 3;
				sign4 = 2;
				break;
			case 1802:
				sign1 = 3;
				sign2 = 4;
				sign3 = 1;
				sign4 = 2;
				break;
			case 1809:
				sign1 = 4;
				sign2 = 3;
				sign3 = 1;
				sign4 = 2;
				break;
			case 2131:
				sign1 = 2;
				sign2 = 3;
				sign3 = 4;
				sign4 = 1;
				break;
			case 2138:
				sign1 = 3;
				sign2 = 2;
				sign3 = 4;
				sign4 = 1;
				break;
			case 2187:
				sign1 = 2;
				sign2 = 4;
				sign3 = 3;
				sign4 = 1;
				break;
			case 2201:
				sign1 = 4;
				sign2 = 2;
				sign3 = 3;
				sign4 = 1;
				break;
			case 2250:
				sign1 = 3;
				sign2 = 4;
				sign3 = 2;
				sign4 = 1;
				break;
			case 2257:
				sign1 = 4;
				sign2 = 3;
				sign3 = 2;
				sign4 = 1;
				break;
			}
		}

		if (iface.getChild(148, 1).getText().contains("jumps")) {
			if (sign1 == 1) {
				if (new RSTile(3167, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3160, 4820), 0, 0));
					objects.at(mat1, "Use");
				} else {
					objects.at(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 1) {
				objects.at(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 1) {
				objects.at(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 1) {
				if (new RSTile(3159, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3166, 4820), 0, 0));
					objects.at(mat4, "Use");
				} else {
					objects.at(mat4, "Use");
				}
				return random(1000, 1500);
			}
		} else if (iface.getChild(148, 1).getText().contains("push ups")) {
			if (sign1 == 2) {
				if (new RSTile(3167, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3160, 4820), 0, 0));
					objects.at(mat1, "Use");
				} else {
					objects.at(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 2) {
				objects.at(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 2) {
				objects.at(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 2) {
				if (new RSTile(3159, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3166, 4820), 0, 0));
					objects.at(mat4, "Use");
				} else {
					objects.at(mat4, "Use");
				}
				return random(1000, 1500);
			}
		} else if (iface.getChild(148, 1).getText().contains("sit ups")) {
			if (sign1 == 3) {
				if (new RSTile(3167, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3160, 4820), 0, 0));
					objects.at(mat1, "Use");
				} else {
					objects.at(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 3) {
				objects.at(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 3) {
				objects.at(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 3) {
				if (new RSTile(3159, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3166, 4820), 0, 0));
					objects.at(mat4, "Use");
				} else {
					objects.at(mat4, "Use");
				}
				return random(1000, 1500);
			}
		} else if (iface.getChild(148, 1).getText().contains("jog on")) {
			if (sign1 == 4) {
				if (new RSTile(3167, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3160, 4820), 0, 0));
					objects.at(mat1, "Use");
				} else {
					objects.at(mat1, "Use");
				}
				return random(1000, 1500);
			} else if (sign2 == 4) {
				objects.at(mat2, "Use");
				return random(1000, 1500);
			} else if (sign3 == 4) {
				objects.at(mat3, "Use");
				return random(1000, 1500);
			} else if (sign4 == 4) {
				if (new RSTile(3159, 4820).distanceTo() < 2) {
					walk.tileMM(walk
							.randomizeTile(new RSTile(3166, 4820), 0, 0));
					objects.at(mat4, "Use");
				} else {
					objects.at(mat4, "Use");
				}
				return random(1000, 1500);
			}
		}

		if (!myclickContinue()) {
			npc.action(demon, "Talk-to");
		}

		return random(1000, 1500);
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
		render.drawString(ScreenLog.dd, 9, 330);
		ScreenMouse.paint(render);
	}

	public boolean playerInArea(final int maxX, final int maxY, final int minX,
			final int minY) {
		final int x = player.getMine().getLocation().getX();
		final int y = player.getMine().getLocation().getY();
		if ((x >= minX) && (x <= maxX) && (y >= minY) && (y <= maxY)) {
			return true;
		}
		return false;
	}
}
