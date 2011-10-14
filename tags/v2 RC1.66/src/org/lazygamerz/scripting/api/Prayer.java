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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterfaceChild;

/**
 * Prayer related operations. Based off of RSBot open source
 * 
 * @author Debauchery version 1.0
 * @author Runedev development team version 1.1
 */
public class Prayer  {
	private final Methods methods;

	public Prayer() {
		this.methods = Bot.methods;
	}

	public interface Book {

		/**
		 * @return The component of the selected prayer
		 */
		public int getComponentIndex();

		/**
		 * @return The required level of the selected prayer
		 */
		public int getRequiredLevel();

		/**
		 * @return The settings value of the selected prayer
		 */
		public int getSettings();
	}
	public enum Curses implements Book {

		PROTECT_ITEM_CURSE(0, 0x1, 50), SAP_WARRIOR(1, 0x2, 50), SAP_RANGER(2,
				0x4, 52), SAP_MAGE(3, 0x8, 54), SAP_SPIRIT(4, 0x10, 56), BERSERKER(
						5, 0x20, 59), DEFLECT_SUMMONING(6, 0x40, 62), DEFLECT_MAGIC(7,
								0x80, 65), DEFLECT_MISSILE(8, 0x100, 68), DEFLECT_MELEE(9,
										0x200, 71), LEECH_ATTACK(10, 0x400, 74), LEECH_RANGE(11, 0x800,
												76), LEECH_MAGIC(12, 0x1000, 78), LEECH_DEFENCE(13, 0x2000, 80), LEECH_STRENGTH(
														14, 0x4000, 82), LEECH_ENERGY(15, 0x8000, 84), LEECH_SPECIAL_ATTACK(
																16, 0x10000, 86), WRATH(17, 0x20000, 89), SOUL_SPLIT(18,
																		0x40000, 92), TURMOIL(19, 0x80000, 95);
		int comp, setting, level;

		private Curses(final int comp, final int setting, final int level) {
			this.comp = comp;
			this.setting = setting;
			this.level = level;
		}

		@Override
		public int getComponentIndex() {
			return comp;
		}

		@Override
		public int getRequiredLevel() {
			return level;
		}

		@Override
		public int getSettings() {
			return setting;
		}
	}

	public enum Normal implements Book {

		THICK_SKIN(0, 0x1, 1), BURST_OF_STRENGTH(1, 0x2, 4), CLARITY_OF_THOUGHT(
				2, 0x4, 7), SHARP_EYE(3, 0x40000, 8), MYSTIC_WILL(4, 0x80000, 9), ROCK_SKIN(
						5, 0x8, 10), SUPERHUMAN_STRENGTH(6, 0x10, 13), IMPROVED_REFLEXES(
								7, 0x20, 16), RAPID_RESTORE(8, 0x40, 19), RAPID_HEAL(9, 0x80,
										22), PROTECT_ITEM_REGULAR(10, 0x100, 25), HAWK_EYE(11,
												0x100000, 26), MYSTIC_LORE(12, 0x200000, 27), STEEL_SKIN(13,
														0x200, 28), ULTIMATE_STRENGTH(14, 0x400, 31), INCREDIBLE_REFLEXES(
																15, 0x800, 34), PROTECT_FROM_SUMMONING(16, 0x1000000, 35), PROTECT_FROM_MAGIC(
																		17, 0x1000, 37), PROTECT_FROM_MISSILES(18, 0x2000, 40), PROTECT_FROM_MELEE(
																				19, 0x4000, 43), EAGLE_EYE(20, 0x400000, 44), MYSTIC_MIGHT(21,
																						0x800000, 45), RETRIBUTION(22, 0x8000, 46), REDEMPTION(23,
																								0x10000, 49), SMITE(24, 0x20000, 52), CHIVALRY(25, 0x2000000,
																										60), RAPID_RENEWAL(26, 0x8000000, 65), PIETY(27, 0x4000000, 70), RIGOUR(
																												28, 0x10000000, 74), AUGURY(29, 0x20000000, 77);
		int comp, setting, level;

		private Normal(final int comp, final int setting, final int level) {
			this.comp = comp;
			this.setting = setting;
			this.level = level;
		}

		@Override
		public int getComponentIndex() {
			return comp;
		}

		@Override
		public int getRequiredLevel() {
			return level;
		}

		@Override
		public int getSettings() {
			return setting;
		}
	}

	public enum ProtectPrayer {

		MEELE(Prayer.Normal.PROTECT_FROM_MELEE, Prayer.Curses.DEFLECT_MELEE), MAGE(
				Prayer.Normal.PROTECT_FROM_MAGIC, Prayer.Curses.DEFLECT_MAGIC), RANGE(
						Prayer.Normal.PROTECT_FROM_MISSILES,
						Prayer.Curses.DEFLECT_MISSILE);
		private Prayer.Book normal;
		private Prayer.Book curses;

		ProtectPrayer(final Prayer.Book normal, final Prayer.Book curses) {
			this.normal = normal;
			this.curses = curses;
		}
	}

	public static final int INTERFACE_PRAYER = 271;

	public static final int INTERFACE_PRAYER_ORB = 749;

	public boolean deactivateAll() {
		for (final Book pray : getSelected()) {
			set(pray, false);
		}
		return getSelected().length < 1;
	}

	/**
	 * Gets the percentage of prayer points left based on the players current
	 * prayer level.
	 * 
	 * @return percentage of prayer points left.
	 */
	public int getPercentLeft() {
		return 100 * getPointsLeft() / methods.skills.getCurrentLvl(Skills.PRAYER);
	}

	/**
	 * Gets the remaining prayer points.
	 * 
	 * @return number of prayer points left.
	 */
	public int getPointsLeft() {
		return Integer.parseInt(methods.iface.getChild(Game.INTERFACE_PRAYER_ORB, 4)
				.getText());
	}

	/**
	 * Returns an array of all current active prayers.
	 * 
	 * @return array of all current active prayers.
	 * @auther Bool
	 */
	public Book[] getSelected() {
		final int bookSetting = isCursing() ? 1582 : 1395;
		final List<Book> activePrayers = new LinkedList<Book>();
		for (final Book prayer : isCursing() ? Curses.values() : Normal.values()) {
			if ((methods.settings.get(bookSetting) & (prayer.getSettings())) == prayer
					.getSettings()) {
				activePrayers.add(prayer);
			}
		}
		return activePrayers.toArray(new Book[activePrayers.size()]);
	}

	public RSInterfaceChild[] getSelection() {
		final ArrayList<RSInterfaceChild> selected = new ArrayList<RSInterfaceChild>();
		final RSInterfaceChild[] prayers = methods.iface.getChild(Game.tabPrayer, 7)
		.getChildren();
		for (final RSInterfaceChild prayer : prayers) {
			if (prayer.getBackgroundColor() != -1) {
				selected.add(prayer);
			}
		}
		return selected.toArray(new RSInterfaceChild[selected.size()]);
	}

	/**
	 * Checks if the player's prayer book is set to Curses.
	 * 
	 * @return <tt>true</tt> if Curses are enabled; otherwise <tt>false</tt>.
	 */
	public boolean isCursing() {
		return methods.settings.get(1584) % 2 != 0;
	}

	/**
	 * Returns true if designated prayer is turned on.
	 * 
	 * @param prayer
	 *            The prayer to check.
	 * @return <tt>true</tt> if enabled; otherwise <tt>false</tt>.
	 */
	public boolean isOn(final Book prayer) {
		for (final Book pray : getSelected()) {
			if (pray == prayer) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if designated prayer is turned on.
	 * 
	 * @param index
	 *            The prayer to check.
	 * @auther Iscream
	 */
	public boolean isOn(final int index) {
		final RSInterfaceChild[] prayers = methods.iface.getChild(Game.tabPrayer, 7)
		.getChildren();
		for (final RSInterfaceChild prayer : prayers) {
			if (prayer.getChildIndex() == index
					&& prayer.getBackgroundColor() != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the quick prayer interface has been used to activate
	 * prayers.
	 * 
	 * @return <tt>true</tt> if quick prayer is on; otherwise <tt>false</tt>.
	 */
	public boolean isQuickOn() {
		return methods.iface.getChild(Game.INTERFACE_PRAYER_ORB, 2)
		.getBackgroundColor() == 782;
	}

	private boolean isQuickSet(final Book thePrayer) {
		return methods.iface.getChild(INTERFACE_PRAYER, 42)
		.getChild(thePrayer.getComponentIndex()).getBackgroundColor() == 181;
	}

	private boolean isQuickSet(final Book... prayers) {
		for (final Book effect : prayers) {
			if (!isQuickSet(effect)) {
				return false;
			}
		}
		return true;
	}

	public boolean protectFrom(final ProtectPrayer ppray) {
		final Book pray = (isCursing() ? ppray.curses : ppray.normal);
		if (methods.skills.getRealLvl(Skills.PRAYER) >= pray.getRequiredLevel()
				&& !isOn(pray)) {
			set(pray, true);
		}
		return isOn(pray);
	}

	/**
	 * Activates/deactivates a prayer via interfaces.
	 * 
	 * @param pray
	 *            The prayer to activate.
	 * @param active
	 *            <tt>true</tt> to activate; <tt>false</tt> to deactivate.
	 * @return <tt>true</tt> if the interface was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean set(final Book pray, final boolean active) {
		if (isOn(pray) == active) {
			return true;
		} else {
			if (methods.skills.getRealLvl(Skills.PRAYER) < pray
					.getRequiredLevel()) {
				return false;
			}
			
			methods.game.openTab(Game.tabPrayer);
			final RSInterfaceChild component = 
				methods.iface.getChild(INTERFACE_PRAYER, 7).getChild(pray.getComponentIndex());
			
			if (component.isValid()) {
				component.action(active ? "Activate" : "Deactivate");
			}
		}
		return isOn(pray) == active;
	}

	/**
	 * Activates/deactivates a prayer via interfaces.
	 * 
	 * @param pray
	 *            The integer that represents the prayer by counting from left
	 *            to right.
	 * @param enable
	 *            <tt>true</tt> to activate; <tt>false</tt> to deactivate.
	 * @return <tt>true</tt> if the interface was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean set(final int pray, final boolean enable) {
		return methods.iface.getChild(INTERFACE_PRAYER, 7).getChildren()[pray]
		                                                         .getBackgroundColor() == -1
		                                                         && methods.iface.clickChild(methods.iface.getChild(INTERFACE_PRAYER, 7)
		                                                        		 .getChildren()[pray], enable ? "Activate"
		                                                        				 : "Deactivate");
	}

	/**
	 * Sets the character's quick prayers to the given prayers.
	 * 
	 * @param prayers
	 *            The prayers to set the quick prayers to.
	 * @return <tt>true</tt> if the quick prayers were set; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean setQuick(final Book... prayers) {
		if (!isQuickOn()) {
			methods.iface.getChild(INTERFACE_PRAYER_ORB, 1).action(
			"Select quick prayers");
		}
		for (final Book effect : prayers) {
			if (isQuickSet(effect)) {
				continue;
			}
			methods.iface.getChild(INTERFACE_PRAYER, 42)
			.getChild(effect.getComponentIndex()).action("Select");
			methods.sleep(methods.random(750, 1100));
		}
		return isQuickSet(prayers)
		&& methods.iface.getChild(INTERFACE_PRAYER, 42).getChild(43)
		.action("Confirm Selection");
	}

	/**
	 * Turns quick prayers on and off
	 * @param activate - true to turn quick prayer on, false to turn them off
	 * @return true if the desired state was set, false otherwise 
	 */
	public boolean setQuick(final boolean activate) {
		if (isQuickOn() != activate) {
			if (getPointsLeft() > 1) {
				methods.iface.getChild(INTERFACE_PRAYER_ORB, 2).click();
			}
		}
		
		return isQuickOn() == activate;
	}
}
