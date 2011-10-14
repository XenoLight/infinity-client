package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSModel;
import org.rsbot.script.wrappers.RSPlayer;

public class DrawPlayers implements PaintListener {

	Methods ctx;

	public DrawPlayers(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public void onRepaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}

		final org.rsbot.client.RSPlayer[] players = Bot.getClient()
		.getRSPlayerArray();
		if (players == null) {
			return;
		}

		final FontMetrics metrics = render.getFontMetrics();
		for (final org.rsbot.client.RSPlayer element : players) {
			if (element == null) {
				continue;
			}
			final RSPlayer player = new RSPlayer(element);
			//final Point location = player.getScreenLocation();
			
			Point localPoint = player.getScreenLocation();

			// Don't use getScreenLocation() as it returns a random model point
			// and causes the NPC ID on screen to jump around.
			RSModel model = player.getModel();
			if (model!=null)  {
			Point[] mps = model.getModelPoints();
				// Use the point with the lowest y value > 0 (i.e. top of the model);
				int minY=99999;
				
				// Keep the model point with the highest screen location
				for (Point p : mps)  {
					if (p.y<minY)  {
						minY = p.y;
						localPoint = p;
					}
				}
			}
			
			if (!ctx.calculate.pointOnScreen(localPoint)) {
				continue;
			}
			render.setColor(Color.RED);
			render.fillRect((int) localPoint.getX() - 1,
					(int) localPoint.getY() - 1, 2, 2);
			String s = ""+player.getName() + " ["
			+ player.getCombatLevel() + "]";
			render.setColor(player.isInCombat() ? Color.RED
					: player.isMoving() ? Color.ORANGE : Color.GREEN);
			render.drawString(s, localPoint.x - metrics.stringWidth(s) / 2,
					localPoint.y - metrics.getHeight() / 2);
			final String msg = player.getMessage();
			boolean raised = false;

			render.setColor(Color.WHITE);
			if (player.getAnimation() != -1 || player.getGraphic() != -1) {
				s = "[Ani=" + player.getAnimation() + " Graph="
				+ player.getGraphic()+"]";
				render.drawString(s, localPoint.x - metrics.stringWidth(s) / 2,
						localPoint.y - metrics.getHeight() * 3 / 2);
				raised = true;
			}
			if (msg != null) {
				render.setColor(Color.BLUE);
				render.drawString(msg, localPoint.x - metrics.stringWidth(msg)
						/ 2, localPoint.y - metrics.getHeight()
						* (raised ? 5 : 3) / 2);
			}
		}
	}
}
