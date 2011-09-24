package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;
import org.rsbot.util.StringUtil;

public class TAnimation implements TextPaintListener {

	private final Methods ctx;

	public TAnimation(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		int animation;
		if (ctx.isLoggedIn()) {
			animation = ctx.getMyPlayer().getAnimation();
		} else {
			animation = -1;
		}
		StringUtil.drawLine(render, idx++, "Animation " + animation);
		return idx;
	}
}
