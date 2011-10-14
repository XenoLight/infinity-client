package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

public class DrawGround implements PaintListener {

	private final Methods ctx;

	public DrawGround(final Bot bot) {
		this.ctx = bot.getMethods();
	}

	@Override
	public void onRepaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		final RSPlayer player = ctx.player.getMine();
		if (player == null) {
			return;
		}
		render.setColor(Color.WHITE);
		final RSTile location = player.getLocation();
		for (int x = location.getX() - 25; x < location.getX() + 25; x++) {
			for (int y = location.getY() - 25; y < location.getY() + 25; y++) {
				final List<RSGroundItem> item = ctx.ground.getItemsAt(x, y);
				if ((item == null) || (item.size() == 0)) {
					continue;
				}
				if (!item.get(0).isOnScreen()) {
					continue;
				}
				render.drawString("" + item.get(0).getID(),
						location.getX() - 10, location.getY());
			}
		}
	}
}
