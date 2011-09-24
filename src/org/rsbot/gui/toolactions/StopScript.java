package org.rsbot.gui.toolactions;

import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptHandler;

/**
 * @author Sorcermus
 */
public class StopScript extends Base {

	private static final long serialVersionUID = 6432657548769992247L;

	public StopScript() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		final ScriptHandler sh = Bot.getScriptHandler();
		final Map<Integer, Script> running = sh.getRunningScripts();
		if (running.size() > 0) {
			final int id = running.keySet().iterator().next();
			sh.stopScript(id);

		}
	}
}
