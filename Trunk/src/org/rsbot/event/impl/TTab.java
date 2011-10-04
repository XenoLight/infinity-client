package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Methods;
import org.rsbot.util.StringUtil;

public class TTab implements TextPaintListener {

	Methods ctx;

	public TTab(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final int cTab = ctx.getCurrentTab();
		StringUtil.drawLine(render, idx++, "Current Tab: " + cTab
				+ (cTab != -1 ? " (" + Constants.TAB_NAMES[cTab] + ")" : ""));
		return idx;
	}
}
