package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Methods;

public class DrawInventory implements PaintListener {

	private final Methods ctx;

	public DrawInventory(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public void onRepaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}

		if (!ctx.inventory.isOpen()) {
			return;
		}

		render.setColor(Color.WHITE);
		final int[] inventory = ctx.inventory.getArray();
		for (int off = 0; off < inventory.length; off++) {
			if (inventory[off] != -1) {
				final Point location = ctx.inventory.getItemPoint(off);
				render.drawString("[" + inventory[off] + "]", location.x,
						location.y);
			}
		}
	}
}
