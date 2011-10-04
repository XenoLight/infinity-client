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
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;

/**
 * Magic tab and spell related operations.
 * 
 * @author Runedev development team. - version 1.0
 */
public class Magic {

	public static enum Book {

		MODERN(192), ANCIENT(193), LUNAR(430), DUNGEONEERING(950);

		private int id;

		Book(final int id) {
			this.id = id;
		}

		public int getInterfaceID() {
			return id;
		}

	}

	private final Methods methods;

	/* Runes */
	public static final int FIRE = 554;
	public static final int WATER = 555;
	public static final int AIR = 556;
	public static final int EARTH = 557;
	public static final int MIND = 558;
	public static final int BODY = 559;
	public static final int DEATH = 560;
	public static final int NATURE = 561;
	public static final int CHAOS = 562;
	public static final int LAW = 563;
	public static final int COSMIC = 564;
	public static final int BLOOD = 565;
	public static final int SOUL = 566;
	public static final int ASTRAL = 9075;

	
	/* Buttons */
	public static final int INTERFACE_DEFENSIVE_STANCE = 2;
	public static final int INTERFACE_SHOW_COMBAT_SPELLS = 7;
	public static final int INTERFACE_SHOW_TELEPORT_SPELLS = 9;
	public static final int INTERFACE_SHOW_MISC_SPELLS = 11;
	public static final int INTERFACE_SHOW_SKILL_SPELLS = 13;
	public static final int INTERFACE_SORT_BY_LEVEL = 15;
	public static final int INTERFACE_SORT_BY_COMBAT = 16;
	public static final int INTERFACE_SORT_BY_TELEPORTS = 17;

	/* Normal spells */
	public static final int SPELL_HOME_TELEPORT = 24;
	public static final int SPELL_WIND_STRIKE = 25;
	public static final int SPELL_CONFUSE = 26;
	public static final int SPELL_ENCHANT_CROSSBOW_BOLT = 27;
	public static final int SPELL_WATER_STRIKE = 28;
	public static final int SPELL_LVL1_ENCHANT = 29;
	public static final int SPELL_EARTH_STRIKE = 30;
	public static final int SPELL_WEAKEN = 31;
	public static final int SPELL_FIRE_STRIKE = 32;
	public static final int SPELL_BONES_TO_BANANAS = 33;
	public static final int SPELL_WIND_BOLT = 34;
	public static final int SPELL_CURSE = 35;
	public static final int SPELL_BIND = 36;
	public static final int SPELL_MOBILISING_ARMIES_TELEPORT = 37;
	public static final int SPELL_LOW_LEVEL_ALCHEMY = 38;
	public static final int SPELL_WATER_BOLT = 39;
	public static final int SPELL_VARROCK_TELEPORT = 40;
	public static final int SPELL_LVL2_ENCHANT = 41;
	public static final int SPELL_EARTH_BOLT = 42;
	public static final int SPELL_LUMBRIDGE_TELEPORT = 43;
	public static final int SPELL_TELEKINETIC_GRAB = 44;
	public static final int SPELL_FIRE_BOLT = 45;
	public static final int SPELL_FALADOR_TELEPORT = 46;
	public static final int SPELL_CRUMBLE_UNDEAD = 47;
	public static final int SPELL_TELEPORT_TO_HOUSE = 48;
	public static final int SPELL_WIND_BLAST = 49;
	public static final int SPELL_SUPERHEAT_ITEM = 50;
	public static final int SPELL_CAMELOT_TELEPORT = 51;
	public static final int SPELL_WATER_BLAST = 52;
	public static final int SPELL_LVL3_ENCHANT = 53;
	public static final int SPELL_IBAN_BLAST = 54;
	public static final int SPELL_SNARE = 55;
	public static final int SPELL_MAGIC_DART = 56;
	public static final int SPELL_ARDOUGNE_TELEPORT = 57;
	public static final int SPELL_EARTH_BLAST = 58;
	public static final int SPELL_HIGH_LEVEL_ALCHEMY = 59;
	public static final int SPELL_CHARGE_WATER_ORB = 60;
	public static final int SPELL_LVL4_ENCHANT = 61;
	public static final int SPELL_WATCHTOWER_TELEPORT = 62;
	public static final int SPELL_FIRE_BLAST = 63;
	public static final int SPELL_CHARGE_EARTH_ORB = 64;
	public static final int SPELL_BONES_TO_PEACHES = 65;
	public static final int SPELL_SARADOMIN_STRIKE = 66;
	public static final int SPELL_CLAWS_OF_GUTHIX = 67;
	public static final int SPELL_FLAMES_OF_ZAMORAK = 68;
	public static final int SPELL_TROLLHEIM_TELEPORT = 69;
	public static final int SPELL_WIND_WAVE = 70;
	public static final int SPELL_CHARGE_FIRE_ORB = 71;
	public static final int SPELL_APE_ATOL_TELEPORT = 72;
	public static final int SPELL_WATER_WAVE = 73;
	public static final int SPELL_CHARGE_AIR_ORB = 74;
	public static final int SPELL_VULNERABILITY = 75;
	public static final int SPELL_LVL5_ENCHANT = 76;
	public static final int SPELL_EARTH_WAVE = 77;
	public static final int SPELL_ENFEEBLE = 78;
	public static final int SPELL_TELEOTHER_LUMBRIDGE = 79;
	public static final int SPELL_FIRE_WAVE = 80;
	public static final int SPELL_ENTANGLE = 81;
	public static final int SPELL_STUN = 82;
	public static final int SPELL_CHARGE = 83;
	public static final int SPELL_WIND_SURGE = 84;
	public static final int SPELL_TELEOTHER_FALADOR = 85;
	public static final int SPELL_TELEPORT_BLOCK = 86;
	public static final int SPELL_WATER_SURGE = 87;
	public static final int SPELL_LVL6_ENCHANT = 88;
	public static final int SPELL_TELEOTHER_CAMELOT = 89;
	public static final int SPELL_EARTH_SURGE = 90;
	public static final int SPELL_FIRE_SURGE = 91;

	/* Ancient spells */
	public static final int SPELL_ANCIENT_ICE_RUSH = 20;
	public static final int SPELL_ANCIENT_ICE_BLITZ = 21;
	public static final int SPELL_ANCIENT_ICE_BURST = 22;
	public static final int SPELL_ANCIENT_ICE_BARRAGE = 23;
	public static final int SPELL_ANCIENT_BLOOD_RUSH = 24;
	public static final int SPELL_ANCIENT_BLOOD_BLITZ = 25;
	public static final int SPELL_ANCIENT_BLOOD_BURST = 26;
	public static final int SPELL_ANCIENT_BLOOD_BARRAGE = 27;
	public static final int SPELL_ANCIENT_SMOKE_RUSH = 28;
	public static final int SPELL_ANCIENT_SMOKE_BLITZ = 29;
	public static final int SPELL_ANCIENT_SMOKE_BURST = 30;
	public static final int SPELL_ANCIENT_SMOKE_BARRAGE = 31;
	public static final int SPELL_ANCIENT_SHADOW_RUSH = 32;
	public static final int SPELL_ANCIENT_SHADOW_BLITZ = 33;
	public static final int SPELL_ANCIENT_SHADOW_BURST = 34;
	public static final int SPELL_ANCIENT_SHADOW_BARRAGE = 35;
	public static final int SPELL_ANCIENT_MIASMIC_RUSH = 36;
	public static final int SPELL_ANCIENT_MIASMIC_BLITZ = 37;
	public static final int SPELL_ANCIENT_MIASMIC_BURST = 38;
	public static final int SPELL_ANCIENT_MIASMIC_BARRAGE = 39;
	public static final int SPELL_ANCIENT_PADDEWWA_TELEPORT = 40;
	public static final int SPELL_ANCIENT_SENNTISTEN_TELEPORT = 41;
	public static final int SPELL_ANCIENT_KHARYRLL_TELEPRT = 42;
	public static final int SPELL_ANCIENT_LASSER_TELEPORT = 43;
	public static final int SPELL_ANCIENT_DAREEYAK_TELEPORT = 44;
	public static final int SPELL_ANCIENT_CARRALLANGER_TELEPORT = 45;
	public static final int SPELL_ANCIENT_ANNAKARL_TELEPORT = 46;
	public static final int SPELL_ANCIENT_GHORROCK_TELEPORT = 47;
	public static final int SPELL_ANCIENT_ANCIENT_HOME_TELEPORT = 48;

	/* Lunar spells */
	public static final int SPELL_LUNAR_BARBARIAN_TELEPORT = 22;
	public static final int SPELL_LUNAR_CURE_OTHER = 23;
	public static final int SPELL_LUNAR_FERTILE_SOIL = 24;
	public static final int SPELL_LUNAR_CURE_GROUP = 25;
	public static final int SPELL_LUNAR_NPC_CONTACT = 26;
	public static final int SPELL_LUNAR_ENERGY_TRANSFER = 27;
	public static final int SPELL_LUNAR_MONSTERS_EXAMINE = 28;
	public static final int SPELL_LUNAR_HUMIDIFY = 29;
	public static final int SPELL_LUNAR_HUNTER_KIT = 30;
	public static final int SPELL_LUNAR_STATE_SPY = 31;
	public static final int SPELL_LUNAR_DREAM = 32;
	public static final int SPELL_LUNAR_PLANK_MAKE = 33;
	public static final int SPELL_LUNAR_SPELLBOOK_SWAP = 34;
	public static final int SPELL_LUNAR_MAGIC_IMBUE = 35;
	public static final int SPELL_LUNAR_VENGEANCE = 36;
	public static final int SPELL_LUNAR_BAKE_PIE = 37;
	public static final int SPELL_LUNAR_HOME_TELEPORT_LUNAR = 38;
	public static final int SPELL_LUNAR_FISHING_GUILD_TELEPORT = 39;
	public static final int SPELL_LUNAR_KHAZARD_TELEPORT = 40;
	public static final int SPELL_LUNAR_VENGEANCE_OTHER = 41;
	public static final int SPELL_LUNAR_MOONCLAN_TELEPORT = 42;
	public static final int SPELL_LUNAR_CATHERBY_TELEPORT = 43;
	public static final int SPELL_LUNAR_STRING_JEWELLERY = 44;
	public static final int SPELL_LUNAR_CURE_ME = 45;
	public static final int SPELL_LUNAR_WATERBIRTH_TELEPORT = 46;
	public static final int SPELL_LUNAR_SUPERGLASS_MAKE = 47;
	public static final int SPELL_LUNAR_BOOTS_POTION_SHARE = 48;
	public static final int SPELL_LUNAR_STAT_RESTORE_POT_SHARE = 49;
	public static final int SPELL_LUNAR_ICE_PLATEAU_TELEPORT = 50;
	public static final int SPELL_LUNAR_HEAL_OTHER = 51;
	public static final int SPELL_LUNAR_HEAL_GROUP = 52;
	public static final int SPELL_LUNAR_OURANIA_TELEPORT = 53;
	public static final int SPELL_LUNAR_CURE_PLANT = 54;
	public static final int SPELL_LUNAR_TELE_GROUP_MOONCLAN = 55;
	public static final int SPELL_LUNAR_TELE_GROUP_WATERBIRTH = 56;
	public static final int SPELL_LUNAR_TELE_GROUP_BARBARIAN = 57;
	public static final int SPELL_LUNAR_TELE_GROUP_KHAZARD = 58;
	public static final int SPELL_LUNAR_TELE_GROUP_FISHING_GUILD = 59;
	public static final int SPELL_LUNAR_TELE_GROUP_CATHERBY = 60;

	/*Dungeoneering Spells */
	public static final int SPELL_DUNGEONEERING_DUNGEON_HOME_TELEPORT = 24;
	public static final int SPELL_DUNGEONEERING_WIND_STRIKE = 25;
	public static final int SPELL_DUNGEONEERING_CONFUSE = 26;
	public static final int SPELL_DUNGEONEERING_WATER_STRIKE = 27;
	public static final int SPELL_DUNGEONEERING_EARTH_STRIKE = 28;
	public static final int SPELL_DUNGEONEERING_WEAKEN = 29;
	public static final int SPELL_DUNGEONEERING_FIRE_STRIKE = 30;
	public static final int SPELL_DUNGEONEERING_BONES_TO_BANANAS = 31;
	public static final int SPELL_DUNGEONEERING_WIND_BOLT = 32;
	public static final int SPELL_DUNGEONEERING_CURSE = 33;
	public static final int SPELL_DUNGEONEERING_BIND = 34;
	public static final int SPELL_DUNGEONEERING_LOW_LEVEL_ALCHEMY = 35;
	public static final int SPELL_DUNGEONEERING_WATER_BOLT = 36;
	public static final int SPELL_DUNGEONEERING_EARTH_BOLT = 37;
	public static final int SPELL_DUNGEONEERING_CREATE_GATESTONE = 38;
	public static final int SPELL_DUNGEONEERING_GATESTONE_TELEPORT = 39;
	public static final int SPELL_DUNGEONEERING_FIRE_BOLT = 41;
	public static final int SPELL_DUNGEONEERING_WIND_BLAST = 42;
	public static final int SPELL_DUNGEONEERING_WATER_BLAST = 43;
	public static final int SPELL_DUNGEONEERING_SNARE = 44;
	public static final int SPELL_DUNGEONEERING_EARTH_BLAST = 45;
	public static final int SPELL_DUNGEONEERING_HIGH_LEVEL_ALCHEMY = 46;
	public static final int SPELL_DUNGEONEERING_FIRE_BLAST = 47;
	public static final int SPELL_DUNGEONEERING_WIND_WAVE = 48;
	public static final int SPELL_DUNGEONEERING_GROUP_GATESTONE_TELEPORT = 40;
	public static final int SPELL_DUNGEONEERING_WATER_WAVE = 49;
	public static final int SPELL_DUNGEONEERING_VULNERABILITY = 50;
	public static final int SPELL_DUNGEONEERING_MONSTER_EXAMINE = 51;
	public static final int SPELL_DUNGEONEERING_CURE_OTHER = 52;
	public static final int SPELL_DUNGEONEERING_HUMIDIFY = 53;
	public static final int SPELL_DUNGEONEERING_EARTH_WAVE = 54;
	public static final int SPELL_DUNGEONEERING_CURE_ME = 55;
	public static final int SPELL_DUNGEONEERING_ENFEEBLE = 56;
	public static final int SPELL_DUNGEONEERING_CURE_GROUP = 57;
	public static final int SPELL_DUNGEONEERING_FIRE_WAVE = 58;
	public static final int SPELL_DUNGEONEERING_ENTANGLE = 59;
	public static final int SPELL_DUNGEONEERING_STUN = 60;
	public static final int SPELL_DUNGEONEERING_WIND_SURGE = 61;
	public static final int SPELL_DUNGEONEERING_WATER_SURGE = 62;
	public static final int SPELL_DUNGEONEERING_EARTH_SURGE = 63;
	public static final int SPELL_DUNGEONEERING_VENGEANCE_OTHER = 64;
	public static final int SPELL_DUNGEONEERING_VENGEANCE = 65;
	public static final int SPELL_DUNGEONEERING_VENGEANCE_GROUP = 66;
	public static final int SPELL_DUNGEONEERING_FIRE_SURGE = 67;
	
	public Magic() {
		methods = Bot.methods;
	}

	/**
	 * Auto-casts a spell via the magic tab.
	 * 
	 * @param spell
	 *            The spell to auto-cast.
	 * @return <tt>true</tt> if the "Auto-cast" interface option was clicked;
	 *         otherwise <tt>false</tt>.
	 * @author Mouchicc
	 */
	public boolean autoCastSpell(final int spell) {
		if (methods.settings.get(43) != 4) {
			if (!isOpen()) {
				methods.game.openTab(Game.tabMagic);
				methods.sleep(methods.random(150, 250));
			}
			final RSInterface inter = getInterface();
			if (inter != null) {
				final RSInterfaceChild comp = inter.getChild(spell);
				return comp != null && comp.action("Autocast");
			}
		}
		return false;
	}

	/**
	 * Clicks a specified spell, opens magic tab if not open and uses interface
	 * of the spell to click it, so it works if the spells are layout in any
	 * way.
	 * 
	 * This method works for all spellbooks.  
	 * Use the SPELL_DUNGEONEERING_* constants for the Dungeoneering spellbook.
	 * Use the SPELL_ANCIENT_* constants for the Ancient spellbook.
	 * Use the SPELL_LUNAR_* constants for the Lunar spellbook.
	 * Use the remaining SPELL_* constants for the Modern spellbook.
	 * 
	 * @param spell
	 *            The spell to cast.
	 * @return <tt>true</tt> if the spell was clicked; otherwise <tt>false</tt>.
	 */
	public boolean castSpell(final int spell) {
		if (!isOpen()) {
			if (!methods.game.openTab(Game.tabMagic))  {
				return false;				
			}
		}
		
		if (methods.magic.isOpen()) {
			final RSInterface inter = getInterface();
			
			if (inter != null) {
				final RSInterfaceChild comp = inter.getChild(spell);
				return comp != null && comp.action("Cast");
			}
		}
		return false;
	}

	/**
	 * Gets the open magic book interface.  Works for the Modern, Ancient and
	 * Lunar spellbooks.
	 * 
	 * @return The current magic RSInterface.
	 */
	public RSInterface getInterface() {
		RSInterface inter = methods.iface.get(Book.MODERN.getInterfaceID());
		
		if (!inter.isValid()) {
			inter = methods.iface.get(Book.ANCIENT.getInterfaceID());
			
			if (!inter.isValid()) {
				inter = methods.iface.get(Book.LUNAR.getInterfaceID());
			
				if (!inter.isValid()) {
					inter = methods.iface.get(Book.DUNGEONEERING.getInterfaceID());
				
				if (!inter.isValid()) {
					return null;
				}
			}
		}
	}
		return inter;
	}

	/**
	 * Checks whether or not a spell is selected.
	 * 
	 * @return <tt>true</tt> if a spell is selected; otherwise <tt>false</tt>.
	 */
	public boolean isSpellSelected() {
		return methods.game.client().isSpellSelected();
	}

	/**
	 * Waits up to the specified time for the magic tab to be open.
	 * 
	 * @param ms amount of time to wait.
	 * @return <tt>true</tt> if the magic tab is open, <tt>false</tt> otherwise.
	 */
	public boolean waitForOpen(int ms)  {
		long stop = System.currentTimeMillis()+ms;
		
		while (System.currentTimeMillis()<stop &&
				methods.game.getCurrentTab()!=Game.tabMagic)  {
			methods.wait(methods.random(80,110));
		}
		
		// This is intentionally elongated to allow for easy debugging
		// using eclipse.
		int currTab = methods.game.getCurrentTab();
		
		return currTab == Game.tabMagic;		
	}
	
	/**
	 * Determines whether the magic tab's interface is open.
	 * 
	 * @return <tt>true</tt> if open, <tt>false</tt> otherwise
	 */
	public boolean isOpen()  {
		int currTab = methods.game.getCurrentTab();
		return currTab==Game.tabMagic;
	}
}
