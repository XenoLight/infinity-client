package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;

public class DrawSettings implements PaintListener {

	private static final Font monoFont = Font.decode(Font.MONOSPACED);
	private static final int TIMEOUT = 5000;
	private int[] lastSettings = new int[0];
	private long[] settingsAge = new long[0];

	public DrawSettings(final Bot bot) {
	}

	@Override
	public void onRepaint(final Graphics render) {
		final int[] settings = Bot.getClient().getSettingArray() != null ? Bot
				.getClient().getSettingArray().getData() : null;
				if (settings != null) {
					final Font prev = render.getFont();
					render.setFont(DrawSettings.monoFont);
					if (settings.length > lastSettings.length) {
						lastSettings = Arrays.copyOf(lastSettings, settings.length);
						settingsAge = Arrays.copyOf(settingsAge, settings.length);
					}
					int id = 0;
					final long curTime = System.currentTimeMillis();
					final long cutoffTime = curTime - DrawSettings.TIMEOUT;
					for (int i = 0; i < settings.length; i++) {
						if (settingsAge[i] == 0) {
							settingsAge[i] = settings[i] != 0 ? cutoffTime : curTime;
						}
						if (lastSettings[i] != settings[i]) {
							settingsAge[i] = curTime;
							lastSettings[i] = settings[i];
						}
						final boolean highlight = settingsAge[i] > cutoffTime;
						final boolean show = settings[i] != 0;
						if (show || highlight) {
							final int x = 10 + 140 * (id % 4);
							final int y = 70 + 12 * (id / 4);
							final String s = String.format("%4d: %d", i, settings[i]);
							render.setColor(Color.black);
							render.drawString(s, x, y + 1);
							render.setColor(highlight ? Color.red : Color.green);
							render.drawString(s, x, y);
							id++;
						}
					}
					render.setFont(prev);
				}
	}
}
