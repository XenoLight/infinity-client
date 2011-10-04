package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

public class DrawModel implements PaintListener {

	Methods ctx;

	public DrawModel(final Bot bot) {
		this.ctx = bot.getMethods();
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		if (!ctx.isLoggedIn()) {
			return;
		}
		final RSPlayer player = ctx.getMyPlayer();
		if (player == null) {
			return;
		}
		final RSTile location = player.getLocation();
		final int locX = location.getX();
		final int locY = location.getY();
		for (int x = locX - 25; x < locX + 25; x++) {
			for (int y = locY - 25; y < locY + 25; y++) {
				final Point screen = Calculations.tileToScreen(x, y, 0);
				if (!ctx.pointOnScreen(screen)) {
					continue;
				}
				final RSObject[] objects = ctx.getObjectsAt(x, y);
				if (objects != null) {
					for (final RSObject object : objects) {
						if ((object != null)
								&& (object.getID() != 0 && object.getObject()
										.getModel() != null)) {
							for (final Polygon p : object.getModel().getTriangles()) {
								if (p != null)
									render.drawPolygon(p);
							}
						}
					}
				}
			}
		}
	}
}
