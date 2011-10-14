package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

public class DrawObjects implements PaintListener {

	private final Methods ctx;

	private static final HashMap<RSObject.Type, Color> color_map = new HashMap<RSObject.Type, Color>();

	// 0 for first object in tile, 1 for second etc..
	private static final Color[] ObjectColors = new Color[] { Color.white,
		Color.yellow, Color.orange, Color.red };

	static {
		color_map.put(RSObject.Type.BOUNDARY, Color.BLACK);
		color_map.put(RSObject.Type.FLOOR_DECORATION, Color.YELLOW);
		color_map.put(RSObject.Type.INTERACTABLE, Color.WHITE);
		color_map.put(RSObject.Type.WALL_DECORATION, Color.GRAY);
	}

	public DrawObjects(final Bot bot) {
		ctx = bot.getMethods();
	}

	private Color getColorForInt(final int index) {
		if (index >= ObjectColors.length)
			return ObjectColors[0];
		return ObjectColors[index];
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
				final Point screen = Calculations.tileToScreen(x, y, 0);
				if (!ctx.calculate.pointOnScreen(screen)) {
					continue;
				}
				final RSObject[] objects = ctx.objects.getAt(x, y);
				if (objects != null) {
					int objectInTile = 0;
					for (final RSObject object : objects) {
						if (object == null || object.getID() <= 0) {
							continue;
						}
						// render.setColor(Color.RED);

						render.setColor(color_map.get(object.getType()));

						render.fillRect((int) screen.getX() - 1,
								(int) screen.getY() - 1, 3, 3);

						final String s = "" + object.getID();
						final int ty = screen.y - tHeight / 2
						+ (objectInTile * 15);
						final int tx = screen.x - metrics.stringWidth(s) / 2;
						render.setColor(getColorForInt(objectInTile));
						render.drawString(s, tx, ty);

						objectInTile++;
					}
				}
			}
		}
	}
}
