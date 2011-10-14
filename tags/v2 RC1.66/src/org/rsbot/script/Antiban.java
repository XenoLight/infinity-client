package org.rsbot.script;

import java.util.logging.Level;

public abstract class Antiban extends Methods {
	public boolean isActive = false;
	public boolean isUsed = true;
	private Script script;

	/**
	 * Detects whether or not this anti-random should activate.
	 * 
	 * @return <tt>true</tt> if the current script should be paused and control
	 *         passed to this anti-ban's loop.
	 */
	public abstract boolean activateCondition();

	public final boolean isActive() {
		return script != null;
	}

	public final boolean isEnabled() {
		return isUsed;
	}

	public abstract int loop();

	public void onFinish() {

	}

	public final boolean runAntiban() {
		try {
			if (!activateCondition())
				return false;
		} catch (final ThreadDeath td) {
			throw td;
		} catch (final Throwable e) {
			log.log(Level.WARNING, "", e);
			return false;
		}
		isActive = true;
		final String name = getClass().getAnnotation(ScriptManifest.class)
		.name();
		log.config("Antiban event started: " + name);
		while (isActive) {
			try {
				final int timeOut = loop();
				if (timeOut == -1) {
					break;
				}
				wait(timeOut);
			} catch (final ThreadDeath td) {
				isActive = false;
				throw td;
			} catch (final Throwable e) {
				log.log(Level.WARNING, "", e);
				break;
			}
		}
		onFinish();
		log.config("Antiban event finished: " + name);
		return true;
	}

	public final void setEnabled(final boolean enabled) {
		this.isUsed = enabled;
	}
}
