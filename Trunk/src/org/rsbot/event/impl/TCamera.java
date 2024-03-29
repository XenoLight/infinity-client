package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.client.Client;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.util.StringUtil;

public class TCamera implements TextPaintListener {

	private final Client client;

	public TCamera(final Bot bot) {
		client = Bot.getClient();
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final String camPos = "Camera Position (x,y,z): ("
			+ client.getCamPosX() + ", " + client.getCamPosY() + ", "
			+ client.getCamPosZ() + ")";
		final String camAngle = "Camera Angle (pitch, yaw): ("
			+ client.getCameraPitch() + ", " + client.getCameraYaw() + ")";

		StringUtil.drawLine(render, idx++, camPos);
		StringUtil.drawLine(render, idx++, camAngle);
		return idx;
	}
}
