package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSCharacter.Orientation;
import org.rsbot.util.StringUtil;

public class TOrientation implements TextPaintListener {

	Methods ctx;

	public TOrientation(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		Orientation or = null;
		if (ctx.game.isLoggedIn()) {
			or = ctx.player.getMine().getOrientation();
		}
		StringUtil.drawLine(
				render,
				idx++,
				"Orientation "
				+ (or == null ? "-1 (null)" : or.getOrientationId()
						+ " (" + or.name() + ")"));
		return idx;
	}
}
