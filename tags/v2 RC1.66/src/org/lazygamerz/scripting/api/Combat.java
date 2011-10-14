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
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;

/**
 * Combat related operations.
 * 
 * @author Runedev development team. - version 1.0
 */
public class Combat {
	private final Methods methods;

	public Combat() {
		methods = Bot.methods;
	}

	/**
	 * Gets the attack mode.
	 * 
	 * @return The current fight mode setting.
	 */
	public int getFightMode() {
		return methods.settings.get(Settings.combatStyle);
	}

	/**
	 * Gets the current player's health as a percentage of full health.
	 * 
	 * @return The current percentage health remaining.
	 */
	public int getHealth() {
		return getLifePoints() * 10
		/ methods.skills.getRealLvl(Skills.CONSTITUTION);
	}

	/**
	 * Gets the current player's life points.
	 * 
	 * @return The current life points if the interface is valid; otherwise 0.
	 */
	public int getLifePoints() {
		try {
			return Integer.parseInt(methods.iface.get(748).getChild(8)
					.getText());
		} catch (final NumberFormatException ex) {
			return 0;
		}
	}

	/**
	 * Gets the current player's prayer points.
	 * 
	 * @return The current prayer points if the interface is valid; otherwise 0.
	 */
	public int getPrayerPoints() {
		try {
			return Integer.parseInt(methods.iface
					.get(Game.INTERFACE_PRAYER_ORB).getChild(4).getText()
					.trim());
		} catch (final NumberFormatException ex) {
			return 0;
		}
	}

	/**
	 * Gets the special bar energy amount.
	 * 
	 * @return The current spec energy.
	 */
	public int getSpecialBarEnergy() {
		return methods.settings.get(300) / 10;
	}

	/**
	 * Gets the current Wilderness Level. Written by Speed.
	 * 
	 * @return The current wilderness level otherwise, 0.
	 * @auther Speed
	 */
	public int getWildernessLevel() {
		return methods.iface.get(381).getChild(2).isValid() ? Integer
				.parseInt(methods.iface.get(381).getChild(2).getText()
						.replace("Level: ", "").trim()) : 0;
	}

	/**
	 * Checks if your character is interacting with an Npc.
	 * 
	 * @param npc
	 *            The Npc we want to fight.
	 * @return <tt>true</tt> if interacting; otherwise <tt>false</tt>.
	 */
	public boolean isAttacking(final RSNPC npc) {
		final RSCharacter interact = methods.player.getMine().getInteracting();
		return interact != null && interact.equals(npc);
	}

	/**
	 * Returns whether or not the auto-retaliate option is enabled.
	 * 
	 * @return <tt>true</tt> if retaliate is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isAutoRetaliateEnabled() {
		return methods.settings.get(Settings.auotRetaliate) == 0;
	}

	/**
	 * Returns whether or not we're poisoned.
	 * 
	 * @return <tt>true</tt> if poisoned; otherwise <tt>false</tt>.
	 */
	public boolean isPoisoned() {
		return methods.settings.get(102) > 0
		|| methods.iface.getChild(748, 4).getBackgroundColor() == 1801;
	}

	/**
	 * Returns whether or not the special-attack option is enabled.
	 * 
	 * @return <tt>true</tt> if special is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isSpecialEnabled() {
		return methods.settings.get(Settings.enableSpecialAttack) == 1;
	}

	/**
	 * Turns auto-retaliate on or off in the combat tab.
	 * 
	 * @param enable
	 *            <tt>true</tt> to enable; <tt>false</tt> to disable.
	 */
	public void setAutoRetaliate(final boolean enable) {
		if (isAutoRetaliateEnabled() != enable) {
			methods.game.openTab(Game.tabAttack);
			final RSInterfaceChild autoRetal = methods.iface.getChild(884, 15);
			if (autoRetal != null) {
				autoRetal.click();
			}
		}
	}

	/**
	 * Sets the attack mode.
	 * 
	 * @param fightMode
	 *            The fight mode to set it to. From 0-3 corresponding to the 4
	 *            attacking modes; Else if there is only 3 attacking modes then,
	 *            from 0-2 corresponding to the 3 attacking modes
	 * @return <tt>true</tt> if the interface was clicked; otherwise
	 *         <tt>false</tt>.
	 * @see #getFightMode()
	 */
	public boolean setFightMode(final int fightMode) {
		if (fightMode != getFightMode()) {
			methods.game.openTab(Game.tabAttack);
			if (fightMode == 0) {
				return methods.iface.getChild(884, 11).click();
			} else if (fightMode == 1) {
				return methods.iface.getChild(884, 12).click();
			} else if (fightMode == 2 || fightMode == 3
					&& methods.iface.getChild(884, 14).getActions() == null) {
				return methods.iface.getChild(884, 13).click();
			} else if (fightMode == 3) {
				return methods.iface.getChild(884, 14).click();
			}
		}
		return false;
	}

	/**
	 * Sets the special attack option on or off.
	 * 
	 * @param enabled
	 *            <tt>true</tt> enable; <tt>false</tt> to disable.
	 * @return <tt>true</tt> if the special bar was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean setSpecialAttack(final boolean enabled) {
		if (isSpecialEnabled() != enabled) {
			methods.game.openTab(Game.tabAttack);
			final RSInterfaceChild specBar = methods.iface.getChild(884, 4);
			if (specBar != null && isSpecialEnabled() != enabled) {
				return specBar.click();
			}
		}
		return false;
	}
}
