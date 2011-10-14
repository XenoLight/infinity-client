package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

public class DrawItems implements PaintListener {

	private final Methods ctx;

	public DrawItems(final Bot bot) {
		ctx = bot.getMethods();
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
		final FontMetrics metrics = render.getFontMetrics();
		final RSTile location = player.getLocation();
		final int locX = location.getX();
		final int locY = location.getY();
		final int tHeight = metrics.getHeight();
		for (int x = locX - 25; x < locX + 25; x++) {
			for (int y = locY - 25; y < locY + 25; y++) {
				final Point screen = Calculations.tileToScreen(new RSTile(x, y));
				if (!ctx.calculate.pointOnScreen(screen)) {
					continue;
				}
				final List<RSGroundItem> items = ctx.ground.getItemsAt(x, y);
				int i=0;
				for (RSGroundItem item: items)  {
					render.setColor(Color.RED);
					render.fillRect((int) screen.getX() - 1,
							(int) screen.getY() - 1, 2, 2);
					final String s = "[" + item.getID() + "]";
					final int ty = screen.y - tHeight * (i + 1) + tHeight / 2;
					final int tx = screen.x - metrics.stringWidth(s) / 2;
					render.setColor(Color.green);
					render.drawString(s, tx, ty);
					i++;
					
					// Draw the model if present
					item.drawModel(render);
				}
			}
		}
	}
}
