package org.rsbot.script.internal.event;

import org.rsbot.bot.Bot;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptHandler;

public interface ScriptListener {

	public void inputChanged(Bot bot, int mask);

	public void scriptPaused(ScriptHandler handler, Script script);

	public void scriptResumed(ScriptHandler handler, Script script);

	public void scriptStarted(ScriptHandler handler, Script script);

	public void scriptStopped(ScriptHandler handler, Script script);

}
