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

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;

/**
 * Keyboard related operations.
 * 
 * @author Runedev development team. - version 1.0
 */
public class Keyboard {

	public Methods methods;

	public Keyboard() {
		methods = Bot.methods;
	}

	/**
	 * Presses and holds a given key.
	 * 
	 * @param c
	 *            The character to press.
	 * @see #releaseKey(char)
	 */
	public void pressKey(final char c) {
		methods.input.pressKey(c);
	}

	/**
	 * Releases a given held key.
	 * 
	 * @param c
	 *            The character to release.
	 * @see #pressKey(char)
	 */
	public void releaseKey(final char c) {
		methods.input.releaseKey(c);
	}

	/**
	 * Presses and releases a given key.
	 * 
	 * @param c
	 *            The character to press.
	 */
	public void sendKey(final char c) {
		methods.input.sendKey(c);
	}

	/**
	 * Types a given string.
	 * 
	 * @param text
	 *            The text to press/send.
	 * @param pressEnter
	 *            <tt>true</tt> to press enter after pressing the text.
	 */
	public void sendText(final String text, final boolean pressEnter) {
		methods.input.sendKeys(text, pressEnter);
	}

	/**
	 * Types a given string instantly.
	 * 
	 * @param text
	 *            The text to press/send.
	 * @param pressEnter
	 *            <tt>true</tt> to press enter after pressing the text.
	 */
	public void sendTextInstant(final String text, final boolean pressEnter) {
		methods.input.sendKeysInstant(text, pressEnter);
	}
}
