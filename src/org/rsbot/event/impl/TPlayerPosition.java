package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.StringUtil;

public class TPlayerPosition implements TextPaintListener {

	private final Methods ctx;

	public TPlayerPosition(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final RSTile position = ctx.getMyPlayer().getLocation();
		StringUtil.drawLine(render, idx++, "Position: " + position);
		return idx;
	}
}
