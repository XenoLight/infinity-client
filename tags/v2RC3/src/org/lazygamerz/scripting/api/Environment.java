/*
 * 2011 Runedev development team
 * http://lazygamerz.org
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 3 of the Licence, or (at your opinion) any
 * later version.
 *
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 */
package org.lazygamerz.scripting.api;

import java.awt.image.BufferedImage;

import org.rsbot.bot.Bot;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.util.io.Screenshot;

/**
 * Bot environment related operations.
 * 
 * @author Runedev development team. - version 1.0
 */
public class Environment {

	public Bot bot;
	public static final int inputMouse = 1;
	public static final int inputKeyboard = 2;

	public Environment() {
	}

	/**
	 * Disables a random event solver.
	 * 
	 * @param name
	 *            the anti-random's (manifest) name (case insensitive)
	 * @return <tt>true</tt> if random was found and set to disabled; otherwise
	 *         <tt>false</tt>
	 */
	public boolean disableRandom(final String name) {
		for (final Random random : Bot.getScriptHandler().getRandoms()) {
			if (random.getClass().getAnnotation(ScriptManifest.class).name()
					.toLowerCase().equals(name.toLowerCase())) {
				if (!random.isEnabled()) {
					return true;
				} else {
					random.setEnabled(false);
					return true;
				}
			}
		}
		return false;
	}

	public void disableRandoms() {
		for (final Random random : Bot.getScriptHandler().getRandoms()) {
			if (random.isEnabled()) {
				random.setEnabled(false);
			}
		}
	}

	/**
	 * Enables a random event solver.
	 * 
	 * @param name
	 *            the anti-random (manifest) name (case insensitive)
	 * @return <tt>true</tt> if random was found and set to enabled; otherwise
	 *         <tt>false</tt>
	 */
	public boolean enableRandom(final String name) {
		for (final Random random : Bot.getScriptHandler().getRandoms()) {
			if (random.getClass().getAnnotation(ScriptManifest.class).name()
					.toLowerCase().equals(name.toLowerCase())) {
				if (random.isEnabled()) {
					return true;
				} else {
					random.setEnabled(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Enables all random event solvers.
	 */
	public void enableRandoms() {
		for (final Random random : Bot.getScriptHandler().getRandoms()) {
			if (!random.isEnabled()) {
				random.setEnabled(true);
			}
		}
	}

	/**
	 * Takes and saves a screen shot.
	 * 
	 * @param hideUsername
	 *            <tt>true</tt> to cover the player's username; otherwise
	 *            <tt>false</tt>
	 */
	public void saveScreenshot(final boolean hideUsername) {
		Screenshot.saveScreenshot(bot, hideUsername);
	}

	public void saveScreenshot(final boolean hideUsername, final String filename) {
		Screenshot.saveScreenshot(bot, hideUsername, filename);
	}

	/**
	 * Controls the available means of user input when user input is disabled.
	 * <p/>
	 * <br />
	 * Disable all: <code>setUserInput(0);</code> <br />
	 * Enable keyboard only:
	 * <code>setUserInput(Environment.INPUT_KEYBOARD);</code> <br />
	 * Enable mouse & keyboard:
	 * <code>setUserInput(Environment.INPUT_MOUSE | Environment.INPUT_KEYBOARD);</code>
	 * 
	 * @param mask
	 *            flags indicating which types of input to allow
	 */
	public void setUserInput(final int mask) {
		Bot.getScriptHandler().updateInput(bot, mask);
	}

	/**
	 * Takes a screenshot.
	 * 
	 * @param hideUsername
	 *            <tt>true</tt> to cover the player's username; otherwise
	 *            <tt>false</tt>
	 * @return The screen capture image.
	 */
	public BufferedImage takeScreenshot(final boolean hideUsername) {
		return Screenshot.takeScreenshot(bot, hideUsername);
	}
}
