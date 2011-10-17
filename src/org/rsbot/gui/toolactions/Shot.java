package org.rsbot.gui.toolactions;

import org.rsbot.bot.Bot;
import org.rsbot.util.Screenshot;

/**
 * @author Sorcermus
 */
public class Shot extends Base {

	private static final long serialVersionUID = 89567527547437753L;

	public Shot() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		Screenshot.takeScreenshot(Bot.game.isLoggedIn());
	}
}
