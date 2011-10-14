package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;
import org.rsbot.util.StringUtil;

public class TFloorHeight implements TextPaintListener {

	Methods ctx;

	public TFloorHeight(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final int floor = ctx.game.getPlane();
		StringUtil.drawLine(render, idx++, "Floor " + floor);
		return idx;
	}
}
