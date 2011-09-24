package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.gui.BotGUI;

public class ScreenMouse {

	public static void paint(final Graphics render) {
		final Mouse mouse = Bot.getClient().getMouse();
		final int w = BotGUI.PANEL_WIDTH, h = BotGUI.PANEL_HEIGHT;
		if (mouse == null) {
			return;
		}

		final int mouse_x = mouse.getX();
		final int mouse_y = mouse.getY();

		render.setColor(new Color(0, 0, 0, 100));
		render.fillRect(0, 0, mouse_x - 1, mouse_y - 1);
		render.fillRect(mouse_x + 1, 0, w - (mouse_x + 1), mouse_y - 1);
		render.fillRect(0, mouse_y + 1, mouse_x - 1, h - (mouse_y - 1));
		render.fillRect(mouse_x + 1, mouse_y + 1, w - (mouse_x + 1), h
				- (mouse_y - 1));

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

	public ScreenMouse() {
	}
}
