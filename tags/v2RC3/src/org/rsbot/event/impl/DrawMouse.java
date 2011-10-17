package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.client.Client;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.listeners.PaintListener;

public class DrawMouse implements PaintListener {

	private final Client client;

	public DrawMouse(final Bot bot) {
		client = Bot.getClient();
	}

	@Override
	public void onRepaint(final Graphics render) {
		final Mouse mouse = client.getMouse();
		if (mouse != null) {
			final int mouse_x = mouse.getX();
			final int mouse_y = mouse.getY();
			final int mouse_press_x = mouse.getPressX();
			final int mouse_press_y = mouse.getPressY();
			final long mouse_press_time = mouse.getPressTime();

			render.setColor(Color.YELLOW);
			render.drawLine(mouse_x - 7, mouse_y - 7, mouse_x + 7, mouse_y + 7);
			render.drawLine(mouse_x + 7, mouse_y - 7, mouse_x - 7, mouse_y + 7);
			if (System.currentTimeMillis() - mouse_press_time < 1000) {
				render.setColor(Color.RED);
				render.drawLine(mouse_press_x - 7, mouse_press_y - 7,
						mouse_press_x + 7, mouse_press_y + 7);
				render.drawLine(mouse_press_x + 7, mouse_press_y - 7,
						mouse_press_x - 7, mouse_press_y + 7);
			}

			if (mouse.isPresent()) {
				render.setColor(mouse.isPressed() ? Color.WHITE : Color.BLACK);
				render.drawLine(mouse.getX() - 7, mouse.getY() - 7,
						mouse.getX() + 7, mouse.getY() + 7);
				render.drawLine(mouse.getX() + 7, mouse.getY() - 7,
						mouse.getX() - 7, mouse.getY() + 7);
			}
		}
	}
}
