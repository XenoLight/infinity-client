package org.rsbot.script;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;

/**
 * This class is for all the skill calculations.
 * <p/>
 * Example usage: skills.getRealLevel(Skills.ATTACK);
 */
public class Skills {

	private final Methods methods;
	/**
	 * This is the stats array.
	 */
	public static String[] statsArray = { "attack", "defense", "strength",
		"hitpoints", "range", "prayer", "magic", "cooking", "woodcutting",
		"fletching", "fishing", "firemaking", "crafting", "smithing",
		"mining", "herblore", "agility", "thieving", "slayer", "farming",
		"runecrafting", "hunter", "construction", "summoning",
	"dungeoneering" }; // "-unused-"
	/**
	 * The xp you have at each level.
	 * 
	 * @author Chemfy/Linux_Communist
	 */
	private static int[] xpTable = { 0, 0, 83, 174, 276, 388, 512, 650, 801,
		969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973,
		4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031,
		13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408,
		33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127,
		83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636,
		184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599,
		407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445,
		899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200,
		1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594,
		3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253,
		7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431 };
	/* stats */
	public static final int ATTACK = 0;
	public static final int DEFENSE = 1;
	public static final int STRENGTH = 2;
	public static final int CONSTITUTION = 3;
	public static final int RANGE = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUTTING = 8;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLORE = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
	public static final int SLAYER = 18;
	public static final int FARMING = 19;
	public static final int RUNECRAFTING = 20;
	public static final int HUNTER = 21;
	public static final int CONSTRUCTION = 22;
	public static final int SUMMONING = 23;
	public static final int DUNGEONEERING = 24;
	
	/* tab location interfaces */
	public static final int INTERFACE_TAB_STATS = 320;
	public static final int INTERFACE_ATTACK = 1;
	public static final int INTERFACE_DEFENSE = 22;
	public static final int INTERFACE_STRENGTH = 4;
	public static final int INTERFACE_CONSTITUTION = 2;
	public static final int INTERFACE_RANGE = 46;
	public static final int INTERFACE_PRAYER = 70;
	public static final int INTERFACE_MAGIC = 87;
	public static final int INTERFACE_COOKING = 62;
	public static final int INTERFACE_WOODCUTTING = 102;
	public static final int INTERFACE_FLETCHING = 95;
	public static final int INTERFACE_FISHING = 38;
	public static final int INTERFACE_FIREMAKING = 85;
	public static final int INTERFACE_CRAFTING = 78;
	public static final int INTERFACE_SMITHING = 20;
	public static final int INTERFACE_MINING = 3;
	public static final int INTERFACE_HERBLORE = 30;
	public static final int INTERFACE_AGILITY = 12;
	public static final int INTERFACE_THIEVING = 54;
	public static final int INTERFACE_SLAYER = 112;
	public static final int INTERFACE_FARMING = 120;
	public static final int INTERFACE_RUNECRAFTING = 104;
	public static final int INTERFACE_HUNTER = 136;
	public static final int INTERFACE_CONSTRUCTION = 128;
	public static final int INTERFACE_SUMMONING = 144;
	public static final int INTERFACE_DUNGEONEERING = 152;

	// Added facilitate antiban's ability to randomly inspect a skill stat.
	public static final int[] INTERFACE_ALL_STATS = {
		INTERFACE_ATTACK,
		INTERFACE_DEFENSE,
		INTERFACE_STRENGTH,
		INTERFACE_CONSTITUTION,
		INTERFACE_RANGE,
		INTERFACE_PRAYER,
		INTERFACE_MAGIC,
		INTERFACE_COOKING,
		INTERFACE_WOODCUTTING,
		INTERFACE_FLETCHING,
		INTERFACE_FISHING,
		INTERFACE_FIREMAKING,
		INTERFACE_CRAFTING,
		INTERFACE_SMITHING,
		INTERFACE_MINING,
		INTERFACE_HERBLORE,
		INTERFACE_AGILITY,
		INTERFACE_THIEVING,
		INTERFACE_SLAYER,
		INTERFACE_FARMING,
		INTERFACE_RUNECRAFTING,
		INTERFACE_HUNTER,
		INTERFACE_CONSTRUCTION,
		INTERFACE_SUMMONING,
		INTERFACE_DUNGEONEERING
	};
	
	//TODO: zzSleepzz - Constants for skill should be in skills
	// Need to reverse this deprecation and get rid of Constants.STAT_*.
	@Deprecated
	public static final int STAT_ATTACK = Constants.STAT_ATTACK;

	@Deprecated
	public static final int STAT_DEFENSE = Constants.STAT_DEFENSE;

	@Deprecated
	public static final int STAT_STRENGTH = Constants.STAT_STRENGTH;

	@Deprecated
	public static final int STAT_HITPOINTS = Constants.STAT_HITPOINTS;

	@Deprecated
	public static final int STAT_LIFEPOINTS = Constants.STAT_LIFEPOINTS;

	@Deprecated
	public static final int STAT_RANGE = Constants.STAT_RANGE;

	@Deprecated
	public static final int STAT_PRAYER = Constants.STAT_PRAYER;

	@Deprecated
	public static final int STAT_MAGIC = Constants.STAT_MAGIC;

	@Deprecated
	public static final int STAT_COOKING = Constants.STAT_COOKING;

	@Deprecated
	public static final int STAT_WOODCUTTING = Constants.STAT_WOODCUTTING;

	@Deprecated
	public static final int STAT_FLETCHING = Constants.STAT_FLETCHING;

	@Deprecated
	public static final int STAT_FISHING = Constants.STAT_FISHING;

	@Deprecated
	public static final int STAT_FIREMAKING = Constants.STAT_FIREMAKING;

	@Deprecated
	public static final int STAT_CRAFTING = Constants.STAT_CRAFTING;

	@Deprecated
	public static final int STAT_SMITHING = Constants.STAT_SMITHING;

	@Deprecated
	public static final int STAT_MINING = Constants.STAT_MINING;

	@Deprecated
	public static final int STAT_HERBLORE = Constants.STAT_HERBLORE;

	@Deprecated
	public static final int STAT_AGILITY = Constants.STAT_AGILITY;

	@Deprecated
	public static final int STAT_THIEVING = Constants.STAT_THIEVING;

	@Deprecated
	public static final int STAT_SLAYER = Constants.STAT_SLAYER;

	@Deprecated
	public static final int STAT_FARMING = Constants.STAT_FARMING;

	@Deprecated
	public static final int STAT_RUNECRAFTING = Constants.STAT_RUNECRAFTING;

	@Deprecated
	public static final int STAT_HUNTER = Constants.STAT_HUNTER;

	@Deprecated
	public static final int STAT_CONSTRUCTION = Constants.STAT_CONSTRUCTION;

	@Deprecated
	public static final int STAT_SUMMONING = Constants.STAT_SUMMONING;

	@Deprecated
	public static final int STAT_DUNGEONEERING = Constants.STAT_DUNGEONEERING;

	/**
	 * Gets the level at the given experience.
	 * 
	 * @param exp
	 *            The experience.
	 * @return The level based on the experience given.
	 * @see #XP_TABLE
	 */
	public static int getLevelAt(final int exp) {
		for (int i = Skills.xpTable.length - 1; i > 0; i--) {
			if (exp > Skills.xpTable[i]) {
				return i;
			}
		}
		return 1;
	}

	/**
	 * Gets the index of the skill with a given name. This is not case
	 * sensitive.
	 * 
	 * @param statName
	 *            The skill's name.
	 * @return The index of the specified skill; otherwise -1.
	 * @see #SKILL_NAMES
	 */
	public static int getStatIndex(final String statName) {
		for (int i = 0; i < Skills.statsArray.length; i++) {
			if (Skills.statsArray[i].equalsIgnoreCase(statName)) {
				return i;
			}
		}
		return -1;
	}
	public Skills() {
		this.methods = Bot.methods;
	}
	
	/**
	 * @deprecated use getCurrentHP();
	 */
	@Deprecated
	public int getCurrentHitPoints() {
		return getCurrentHP();
	}
	/**
	 * Finds the current hitpoints (lifepoints/10) of the player.
	 * 
	 * @return The current hitpoints (lifepoints/10) of the player.
	 */
	public int getCurrentHP() {
		return Math.round(getCurrentLP() / 10);
	}
	
	/**
	 * @deprecated use getCurrentLP()
	 */
	@Deprecated
	public int getCurrentLifePoints() {
		return getCurrentLP();
	}
	
	/**
	 * Gets the current life points of the player. This is the over all
	 * constitution points of the player
	 */
	public int getCurrentLP() {
		return Integer.parseInt(methods.iface.get(748).getChild(8).getText());
	}
	
	/**
	 * Gets the current prayer points of the player. This is similar to the 
	 * life points of the player.  There are 10 prayer points for each prayer
	 * skill level.
	 */
	public int getCurrentPrayerPoints() {
		return Integer.parseInt(methods.iface.get(749).getChild(4).getText());
	}
	
	/**
	 * Gets the effective level of the given skill (accounting for temporary
	 * boosts and reductions).
	 * 
	 * @param index
	 *            The index of the skill.
	 * @return The current level of the given Skill.
	 */
	public int getCurrentLvl(final int index) {
		return methods.game.client().getSkillLevels()[index];
	}
	
	/**
	 * @deprecated use getCurrentXP()
	 */
	@Deprecated
	public int getCurrentSkillExp(final int index) {
		return getCurrentXP(index);
	}
	
	/**
	 * @deprecated use getCurrentLvl()
	 */
	@Deprecated
	public int getCurrentSkillLevel(final int index) {
		if (index == Constants.STAT_LIFEPOINTS) {
			return getCurrentLifePoints();
		} else {
			return getCurrentLvl(index);
		}
	}
	
	/**
	 * Gets the current experience for the given skill.
	 * 
	 * @param index
	 *            The index of the skill.
	 * @return -1 if the skill is unavailable
	 */
	public int getCurrentXP(final int index) {
		final int[] skills = methods.game.client().getSkillExperiences();

		if (index > skills.length - 1) {
			return -1;
		}

		return methods.game.client().getSkillExperiences()[index];
	}
	
	/**
	 * @deprecated use getCurrentLvl(index);
	 */
	@Deprecated
	public int getCurrSkillLevel(final int index) {
		return getCurrentLvl(index);
	}
	
	/**
	 * @deprecated use getHPPercent()
	 */
	@Deprecated
	public double getHitPointPercent() {
		return getHPPercent();
	}
	
	/**
	 * Gets you current LifePoints percent
	 * 
	 * @returns HPPercent
	 */
	public double getHPPercent() {
		final int HPpercent = (Math.round(getCurrentLP()
				/ (getRealLvl(CONSTITUTION) * 10) * 100));
		try {
			methods.wait(10);
		} catch (final Exception e) {
		}
		return HPpercent;
	}
	
	/**
	 * @deprecated use getLvlByXP();
	 */
	@Deprecated
	public int getLvlByExp(final int exp) {
		return getLvlByXP(exp);
	}
	
	/**
	 * Returns the level when given the exp.
	 */
	public int getLvlByXP(final int exp) {
		for (int i = Skills.xpTable.length - 1; i > 0; i--) {
			if (exp > Skills.xpTable[i]) {
				return i;
			}
		}
		return 1;
	}
	
	/**
	 * Gets the maximum level of a given skill.
	 * 
	 * @param index
	 *            The index of the skill.
	 * @return The max level of the skill.
	 */
	public int getMaxLvl(final int index) {
		return methods.game.client().getSkillLevelMaxes()[index];
	}
	
	/**
	 * Gets the maximum experience of a given skill.
	 * 
	 * @param index
	 *            The index of the skill.
	 * @return The max experience of the skill.
	 */
	public int getMaxXP(final int index) {
		return methods.game.client().getSkillExperiencesMax()[index];
	}
	
	/**
	 * @deprecated use getPercentToNextLvl(index);
	 */
	@Deprecated
	public int getPercentToNextLevel(final int index) {
		return getPercentToNextLvl(index);
	}
	
	/**
	 * Gets the percentage to the next level in a given skill.
	 * 
	 * @param index
	 *            The index of the skill.
	 * @return The percent to the next level of the provided skill or 0 if level
	 *         of skill is 99.
	 */
	public int getPercentToNextLvl(final int index) {
		final int lvl = getRealLvl(index);
		if (lvl == 99) {
			return 0;
		}
		final int xpTotal = Skills.xpTable[lvl + 1] - Skills.xpTable[lvl];
		if (xpTotal == 0) {
			return 0;
		}
		final int xpDone = getCurrentXP(index) - Skills.xpTable[lvl];
		return 100 * xpDone / xpTotal;
	}
	
	/**
	 * Gets the player's current level in a skill based on their experience in
	 * that skill.
	 * 
	 * @param index
	 *            The index of the skill.
	 * @return The real level of the skill.
	 * @see #getRealLevel(int)
	 */
	public int getRealLvl(final int index) {
		return getLvlByXP(getCurrentXP(index));
	}
	
	/**
	 * @deprecated use getRealLvl();
	 */
	@Deprecated
	public int getRealSkillLevel(final int index) {
		return getRealLvl(index);
	}
	
	/**
	 * @deprecated use getMaxLvl();
	 */
	@Deprecated
	public int getSkillMax(final int index) {
		return getMaxLvl(index);
	}
	
	/**
	 * @deprecated use getMaxXP();
	 */
	@Deprecated
	public int getSkillMaxExp(final int index) {
		return getMaxXP(index);
	}
	
	/**
	 * @deprecated use getXPToNextLvl()
	 */
	@Deprecated
	public int getXPToNextLevel(final int index) {
		return getXPToNextLvl(index);
	}
	
	/**
	 * Gets the experience remaining until reaching the next level in a given
	 * skill.
	 * 
	 * @param index
	 *            The index of the skill.
	 * @return The experience to the next level of the skill.
	 */
	public int getXPToNextLvl(final int index) {
		final int lvl = getRealLvl(index);
		if (lvl == 99) {
			return 0;
		}
		return Skills.xpTable[lvl + 1] - getCurrentXP(index);
	}
	
	/**
	 * Moves the mouse over a given component in the stats tab.
	 * 
	 * @param component
	 *            The component index.
	 * @return <tt>true</tt> if the mouse was moved over the given component
	 *         index.
	 */
	public boolean hover(final int component) {
		methods.game.openTab(Game.tabStats);
		methods.sleep(methods.random(10, 100));
		return methods.iface.getChild(INTERFACE_TAB_STATS, component).hover();
	}
}
