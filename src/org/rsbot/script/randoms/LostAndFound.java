package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 1.2 - 02/10/11
 */
@ScriptManifest(authors = { "Garrett" }, name = "LostAndFound", version = 1.2)
public class LostAndFound extends Random implements PaintListener {

	final int appendN = 8995;
	final int appendE = 8994;
	final int appendS = 8997;
	final int appendW = 8996;
	final int answerN[] = { 32, 64, 135236, 67778, 135332, 34017, 202982,
			101443, 101603, 236743, 33793, 67682, 135172, 236743, 169093,
			33889, 202982, 67714, 101539 };
	final int answerE[] = { 4, 6, 101474, 101473, 169124, 169123, 67648,
			135301, 135298, 67651, 169121, 33827, 67652, 236774, 101479, 33824,
			202951 };
	final int answerS[] = { 4228, 32768, 68707, 167011, 38053, 230433, 164897,
			131072, 168068, 65536, 35939, 103589, 235718, 204007, 100418,
			133186, 99361, 136357, 1057, 232547 };
	final int answerW[] = { 105571, 37921, 131204, 235751, 1024, 165029,
			168101, 68674, 203974, 2048, 100451, 6144, 39969, 69698, 32801,
			136324 };
	final int settingg = 531;

	@Override
	public boolean activateCondition() {
		return game.isLoggedIn() && objects.getNearestByID(appendN) != null;
	}

	public RSObject getFarthestObjectByID(final int... ids) {
		RSObject cur = null;
		double dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final RSObject O = objects.getTopAt(x
						+ Bot.getClient().getBaseX(), y
						+ Bot.getClient().getBaseY());
				if (O != null) {
					boolean isObject = false;
					for (final int id : ids) {
						if (O.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						final double distTmp = Calculations
						.distanceBetween(
								player.getMine().getLocation(),
								O.getLocation());
						if (cur == null) {
							dist = distTmp;
							cur = O;
						} else if (distTmp > dist) {
							cur = O;
							dist = distTmp;
						}
					}
				}
			}
		}
		return cur;
	}

	private int getOddAppendage() {
		final int[] settings = this.settings.getArray();
		try {
			for (final int element : answerN) {
				if (settings[settingg] == element) {
					return appendN;
				}
			}
			for (final int element : answerE) {
				if (settings[settingg] == element) {
					return appendE;
				}
			}
			for (final int element : answerS) {
				if (settings[settingg] == element) {
					return appendS;
				}
			}
			for (final int element : answerW) {
				if (settings[settingg] == element) {
					return appendW;
				}
			}
		} catch (final Exception ignored) {
		}
		return random(8994, 8998);
	}

	@Override
	public int loop() {
		if (iface.canContinue()) {
			iface.clickContinue();
		}

		if (objects.getNearestByID(appendN) == null) {
			return -1;
		}

		final int appendage = getOddAppendage();

		try {
			final RSObject obj = getFarthestObjectByID(appendage);
			final RSTile tile = obj.getLocation();
			if (!tile.isOnScreen()) {
				walk.to(tile);
				sleep(random(700, 900));
				while (player.getMine().isMoving()) {
					sleep(100);
				}
			}
			if (objects.at(obj, "Operate")) {
				sleep(random(1000, 1500));
				while (player.getMine().isMoving()) {
					sleep(100);
				}
			}
		} catch (final Exception ignored) {
		}

		return random(50, 100);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.lost, 9, 330);
		ScreenMouse.paint(render);
	}
}
