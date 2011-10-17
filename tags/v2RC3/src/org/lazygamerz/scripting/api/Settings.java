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
 * Provides access to game settings.
 * 
 * @author Runedev development team.
 */
public class Settings {

	private final Methods methods;
	public static final int combatStyle = 43;
	public static final int toggleRun = 173;
	public static final int bankRearange = 304;
	public static final int toggleFirstaid = 427;
	public static final int mouseButtons = 170;
	public static final int chatEffect = 171;
	public static final int plitPrivateChat = 287;
	public static final int screenBrightness = 166;
	public static final int musicVolume = 168;
	public static final int soundEffectVolume = 169;
	public static final int areaSoundEffectVolume = 872;
	public static final int auotRetaliate = 172;
	public static final int swapQuestDiary = 1002;
	public static final int toggleLoopMusic = 19;
	public static final int toggleBankWithdrawMode = 115;
	public static final int typeShop = 118;
	public static final int enableSpecialAttack = 301;

	public Settings() {

		methods = Bot.methods;
	}

	/**
	 * Gets the setting at a given index.
	 * 
	 * @param setting
	 *            The setting index to return the value of.
	 * @return <tt>int</tt> representing the setting of the given setting id;
	 *         otherwise <tt>-1</tt>.
	 */
	public int get(final int setting) {
		final int[] settings = getArray();
		if (setting < settings.length) {
			return settings[setting];
		}
		return -1;
	}

	/**
	 * Gets the settings array.
	 * 
	 * @return <tt>int</tt> array representing all of the settings values;
	 *         otherwise <tt>new int[0]</tt>.
	 */
	public int[] getArray() {
		final org.rsbot.client.Settings settingArray = methods.game.client()
		.getSettingArray();
		if (settingArray == null || settingArray.getData() == null) {
			return new int[0];
		}
		/* NEVER return pointer */
		return settingArray.getData().clone();
	}
}
