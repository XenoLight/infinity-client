package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.Methods;

public class TMenuActions extends Methods implements TextPaintListener {

	public TMenuActions(final Bot bot) {
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final String[] items = menu.getItems();
		int i = 0;
		for (final String item : items) {
			Methods.drawLine(render, idx++, i++ + ": [red]" + item);
		}
		return idx;
	}
}
