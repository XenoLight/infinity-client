package org.rsbot.bot;

import org.rsbot.client.Callback;
import org.rsbot.client.Render;
import org.rsbot.client.RenderData;
import org.rsbot.event.events.CharacterMovedEvent;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;

public class CallbackImpl implements Callback {

	private final Bot bot;

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	@Override
	public Bot getBot() {
		return bot;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void notifyMessage(final int id, final String sender,
			final String msg) {
		final MessageEvent m = new MessageEvent(sender, id, msg);
		Bot.getEventManager().dispatchEvent(m);
		if (id == MessageEvent.MESSAGE_SERVER
				|| id == MessageEvent.MESSAGE_ACTION) {
			final ServerMessageEvent e = new ServerMessageEvent(msg);
			Bot.getEventManager().dispatchEvent(e);
		}
	}

	@Override
	public void rsCharacterMoved(final org.rsbot.client.RSCharacter c,
			final int i) {
		final CharacterMovedEvent e = new CharacterMovedEvent(bot.getMethods(), c, i);
		Bot.getEventManager().dispatchEvent(e);
	}

	@Override
	public void updateRenderInfo(final Render r, final RenderData rd) {
		final Methods ctx = bot.getMethods();
		if (ctx != null) {
			Calculations.updateRenderInfo(r, rd);
		}
	}
}
