package org.rsbot.script;

import java.util.ArrayList;
import java.util.logging.Level;

import org.rsbot.bot.Bot;

public abstract class Random extends Methods {
	/**
	 * @param String
	 *            name: name or part of name of anti-random as stated in
	 *            ScriptManifest
	 * @param boolean setting: set all randoms' isUsed to setting.
	 */
	public static void setAllAntiRandom(final boolean setting) {
		final ArrayList<Random> randoms = (ArrayList<Random>) Bot.getScriptHandler()
		.getRandoms();
		for (final Random r : randoms) {
			r.isUsed = setting;
			break;
		}
	}
	/**
	 * @param String
	 *            name: name or part of name of anti-random as stated in
	 *            ScriptManifest
	 * @param boolean setting: set random's isUsed to setting.
	 */
	public static void setAntiRandom(final String name, final boolean setting) {
		final ArrayList<Random> randoms = (ArrayList<Random>) Bot.getScriptHandler()
		.getRandoms();
		for (final Random r : randoms) {
			if (r.getClass().getAnnotation(ScriptManifest.class).name()
					.contains(name)) {
				r.isUsed = setting;
				break;
			}
		}
	}
	public boolean isActive = false;

	public boolean isUsed = true;

	private Script script;

	/**
	 * Detects whether or not this anti-random should activate.
	 * 
	 * @return <tt>true</tt> if the current script should be paused and control
	 *         passed to this anti-random's loop.
	 */
	public abstract boolean activateCondition();

	/**
	 * Override to provide a time limit in seconds for this anti-random to
	 * complete.
	 * 
	 * @return The number of seconds after activateCondition returns
	 *         <tt>true</tt> before the anti-random should be detected as having
	 *         failed. If this time is reached the random and running script
	 *         will be stopped.
	 */
	public int getTimeout() {
		return 0;
	}

	public final boolean isActive() {
		return script != null;
	}

	public final boolean isEnabled() {
		return isUsed;
	}

	public abstract int loop();

	public void onFinish() {

	}

	public final boolean runRandom() {
		if (!isUsed || !activateCondition()) {
			return false;
		}
		isActive = true;
		final String name = getClass().getAnnotation(ScriptManifest.class)
		.name();
		log.config("Random event started: " + name);
		int timeout = getTimeout();
		if (timeout > 0) {
			timeout *= 1000;
			timeout += System.currentTimeMillis();
		}
		while (isActive) {
			try {
				final int wait = loop();
				if (wait == -1) {
					break;
				}
				if (timeout > 0 && System.currentTimeMillis() >= timeout) {
					log.warning("Time limit reached for " + name + ".");
					stopScript();
				}
				wait(wait);
			} catch (final Exception ex) {
				log.log(Level.WARNING, "", ex);
				break;
			}
		}
		isActive = false;
		onFinish();
		log.config("Random event finished: " + name);
		return true;
	}

	public final void setEnabled(final boolean enabled) {
		this.isUsed = enabled;
	}
}
