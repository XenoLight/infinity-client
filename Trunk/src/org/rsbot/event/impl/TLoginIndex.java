package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;
import org.rsbot.util.StringUtil;

public class TLoginIndex implements TextPaintListener {

	Methods ctx;

	public TLoginIndex(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		StringUtil.drawLine(render, idx++,
				"Client State: " + ctx.game.getLoginIndex());
		return idx;
	}
}
