package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSModel;
import org.rsbot.script.wrappers.RSNPC;

public class DrawNPCs implements PaintListener {

	Methods ctx;

	public DrawNPCs(final Bot bot) {
		ctx = bot.getMethods();
	}

	@Override
	public void onRepaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		final FontMetrics localFontMetrics = render.getFontMetrics();
		for (final int k : Bot.getClient().getRSNPCIndexArray()) {
			final Node localNode = Calculations.findNodeByID(Bot.getClient()
					.getRSNPCNC(), k);
			if (localNode == null || !(localNode instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC localRSNPC = new RSNPC(((RSNPCNode) localNode).getRSNPC());
//			final Point localPoint = localRSNPC.getScreenLocation();
			Point localPoint = localRSNPC.getScreenLocation();

			// Don't use getScreenLocation() as it returns a random model point
			// and causes the NPC ID on screen to jump around.
			RSModel model = localRSNPC.getModel();
			
			if (model!=null)  {
				Point[] mps = model.getModelPoints();
				// Use the point with the lowest y value > 0 (i.e. top of the model);
				int minY=99999;
				
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
			String str = "ID: [ " + localRSNPC.getID() + " ]";
			render.setColor((localRSNPC.isInCombat()) ? Color.RED : (localRSNPC
					.isMoving()) ? Color.ORANGE : Color.GREEN);
			render.drawString(str,
					localPoint.x - localFontMetrics.stringWidth(str) / 2,
					localPoint.y - localFontMetrics.getHeight() / 2);

			if (localRSNPC.getAnimation() != -1
					|| localRSNPC.getGraphic() != -1) {
				str = "[Ani] : " + localRSNPC.getAnimation() + " | [Graph] : "
				+ localRSNPC.getGraphic();
				render.drawString(str,
						localPoint.x - localFontMetrics.stringWidth(str) / 2,
						localPoint.y - localFontMetrics.getHeight() * 3 / 2);
			}
		}
	}
}
