package org.rsbot.script;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.logging.Logger;

import org.lazygamerz.scripting.api.Camera;
import org.lazygamerz.scripting.api.Combat;
import org.lazygamerz.scripting.api.Environment;
import org.lazygamerz.scripting.api.Equipment;
import org.lazygamerz.scripting.api.GE;
import org.lazygamerz.scripting.api.Game;
import org.lazygamerz.scripting.api.GroundItems;
import org.lazygamerz.scripting.api.Interface;
import org.lazygamerz.scripting.api.Inventory;
import org.lazygamerz.scripting.api.Keyboard;
import org.lazygamerz.scripting.api.Lobby;
import org.lazygamerz.scripting.api.Magic;
import org.lazygamerz.scripting.api.Menu;
import org.lazygamerz.scripting.api.Mouse;
import org.lazygamerz.scripting.api.NPC;
import org.lazygamerz.scripting.api.Nodes;
import org.lazygamerz.scripting.api.Objects;
import org.lazygamerz.scripting.api.Player;
import org.lazygamerz.scripting.api.Prayer;
import org.lazygamerz.scripting.api.Screen;
import org.lazygamerz.scripting.api.Settings;
import org.lazygamerz.scripting.api.Store;
import org.lazygamerz.scripting.api.Summoning;
import org.lazygamerz.scripting.api.Tile;
import org.lazygamerz.scripting.api.Walk;
import org.rsbot.bot.Bot;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.StringUtil;
import org.rsbot.util.color.ColorUtil;

/**
 * Methods that can be used by Infinity scripts.
 * <p/>
 * If you want to edit this methods file from the official Infinity download,
 * then please create an issue at:
 * http://http://code.google.com/p/infinity-client/issues/list
 * 
 * @author This is an open-source project, therefore there are too many to list.
 */
public class Methods implements Constants {

	/**
	 * @deprecated Use StringUtil.drawLine();
	 */
	@Deprecated
	public static void drawLine(final Graphics render, final int row, final String text) {
		StringUtil.drawLine(render, row, text);
	}
	/**
	 * The singleton list.
	 */
	public Skills skills = Bot.skills;
	public Bank bank = Bot.bank;
	public Bot bot;
	public Calculations calculate = Bot.calculate;
	public Camera camera = Bot.camera;
	public Combat combat = Bot.combat;
	public Store store = Bot.store;
	public Game game = Bot.game;
	public GrandExchange grandExchange = Bot.grandExchange;
	public Equipment equipment = Bot.equipment;
	public Environment enviro = Bot.enviro;
	public GE ge = Bot.ge;
	public GroundItems ground = Bot.ground;
	public InputManager input = Bot.getInputManager();
	public Inventory inventory = Bot.inventory;
	public Interface iface = Bot.iface;
	public Keyboard keyboard = Bot.keyboard;
	public Lobby lobby = Bot.lobby;
	public Magic magic = Bot.magic;
	public Menu menu = Bot.menu;
	public Mouse mouse = Bot.mouse;
	public NPC npc = Bot.npc;
	public Nodes nodes = Bot.nodes;
	public Objects objects = Bot.objects;
	public Player player = Bot.player;
	public Prayer prayer = Bot.prayer;
	public Screen screen = Bot.screen;
	public Settings settings = Bot.settings;
	public Summoning summoning = Bot.summoning;
	public Tile tile = Bot.tile;
	public Walk walk = Bot.walk;
	private final java.util.Random random = new java.util.Random();

	protected final Logger log = Logger.getLogger(this.getClass().getName());

        protected void log(final String message) {
		log.info(message);
	}        
        
	/**
	 * Returns a random double in a specified range
	 * 
	 * @param min
	 *            Minimum value (inclusive).
	 * @param max
	 *            Maximum value (exclusive).
	 * @return The random <code>double</code> generated.
	 */
	public double random(final double min, final double max) {
		double nmin = min;
		double nmax = max;
		
		// If max is actually less than min, swap them.
		if (min>max)  {
			nmin = max;
			nmax = min;
		}
		return Math.min(nmin, nmax) + random.nextDouble() * Math.abs(nmax - nmin);
	}

	/**
	 * Returns a random integer in a specified range.
	 * 
	 * @param min
	 *            Minimum value (inclusive).
	 * @param max
	 *            Maximum value (exclusive).
	 * @return The random <code>int</code> generated.
	 */
	public int random(final int min, final int max) {
		int nmin=min;
		int nmax=max;
		
		// If max is actually less than min, swap them.
		if (min>max)  {
			nmin = max;
			nmax = min;
		}

		final int n = Math.abs(nmax - nmin);
		return Math.min(nmin, nmax) + (n == 0 ? 0 : random.nextInt(n));
	}

	// Sometimes random is given a positive or zero value for min and a negative value for max.
	// We need to handle these kinds of conditions.
	public int random(final int min, final int max, final int sd) {
		int nmin=min;
		int nmax=max;
		
		// If max is actually less than min, swap them.
		if (min>max)  {
			nmin = max;
			nmax = min;
		}
		
		final int mean = nmin + (nmax - nmin) / 2;
		int rand;
		
		do {
			rand = (int) (random.nextGaussian() * sd + mean);
		} while (rand < nmin || rand >= nmax);
		
		return rand;
	}
        
        /**
	 * @param ms
	 *            The time to sleep in milliseconds.
	 */
	public void sleep(final int ms) {
		try {
			final long start = System.currentTimeMillis();
			Thread.sleep(ms);
			long now; // Guarantee minimum sleep
			while (start + ms > (now = System.currentTimeMillis())) {
				Thread.sleep(start + ms - now);
			}
		} catch (final InterruptedException ignored) {
		}
	}

	/**
	 * 
	 * @param min
	 *            to sleep in milliseconds
	 * @param max
	 *            to sleep in milliseconds
	 */
	public void sleep(final int min, final int max) {
		sleep(random(min, max));
	}
        
        public void stopScript() {
		stopScript(true);
	}

	public void stopScript(final boolean logout) {
		if (bank.isOpen()) {
			bank.close();
		}
		if (game.isLoggedIn() && logout) {
			game.logout();
		}
                log.config("Script stopped.");
		Bot.getScriptHandler().stopScript();
	}

        /**
	 * Pauses for a specified number of milliseconds. Try not to use this
	 * method.
	 *
	 * @param ms
	 *            Time in milliseconds to pause.
	 * @see #waitToMove(int)
	 * @see #waitForAnim(int)
	 * @see #waitForIface(RSInterface, int)
	 */
	public void wait(final int ms) {
		try {
			final long start = System.currentTimeMillis();
			Thread.sleep(ms);
			/* Guarantee minimum sleep */
			long now;
			while (start + ms > (now = System.currentTimeMillis())) {
				Thread.sleep(start + ms - now);
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @deprecated Use player.animationIs();
	 */
	@Deprecated
	public boolean animationIs(final int... ids) {
		return player.animationIs(ids);
	}

	/**
	 * @deprecated use menu.arrayContains();
	 */
	@Deprecated
	public boolean arrayContains(final String[] items, final String search) {
		return menu.arrayContains(items, search);
	}

	/**
	 * @deprecated use iface.clickChild();
	 */
	@Deprecated
	public boolean atComponent(final RSInterfaceChild child, final int id, final String act) {
		return iface.clickChild(child, id, false) && atMenu(act);
	}

	/**
	 * @deprecated use iface.clickChild();
	 */
	@Deprecated
	public boolean atComponent(final RSInterfaceChild com, final String act) {
		return iface.clickChild(com, act);
	}

	/**
	 * @deprecated use objects.atDoor();
	 */
	@Deprecated
	public boolean atDoor(final int id, final char direction) {
		return objects.atDoor(id, direction);
	}

	/**
	 * @deprecated use tile.clickDoor();
	 */
	@Deprecated
	public boolean atDoorTiles(final RSTile t1, final RSTile t2) {
		return tile.clickDoor(t1, t2);
	}

	/**
	 * @deprecated use menu.action()
	 */
	@Deprecated
	public boolean atDropMenu(final String act) {
		return menu.action(act);
	}

	/**
	 * @deprecated use menu.action();
	 */
	@Deprecated
	public boolean atDropMenu(final String opt, final int col) {
		return menu.action(opt, col);
	}

	/**
	 * @deprecated use menu.action();
	 */
	@Deprecated
	public boolean atDropMenu(final String act, final String opt) {
		return menu.action(act, opt);
	}

	/**
	 * @deprecated use menu.action();
	 */
	@Deprecated
	public boolean atDropMenuItem(final int i, final int column) {
		return menu.action(i, column);
	}

	/**
	 * @deprecated use equipment.clickItem();
	 */
	@Deprecated
	public boolean atEquippedItem(final int id, final String act) {
		return equipment.clickItem(id, act);
	}

	/**
	 * @deprecated use iface.clickChild();
	 */
	@Deprecated
	public boolean atInterface(final int face, final int child) {
		return iface.clickChild(face, child);
	}

	/**
	 * @deprecated use iface.clickChild();
	 */
	@Deprecated
	public boolean atInterface(final int face, final int child, final String act) {
		return iface.clickChild(face, child);
	}

	/**
	 * @deprecated use iface.clickChild();
	 */
	@Deprecated
	public boolean atInterface(final RSInterfaceChild child) {
		return iface.clickChild(child);
	}

	/**
	 * @deprecated use iface.clickChild();
	 */
	@Deprecated
	public boolean atInterface(final RSInterfaceChild child, final boolean leftClick) {
		return iface.clickChild(child, leftClick);
	}

	/**
	 * @deprecated use iface.clickChild();
	 */
	@Deprecated
	public boolean atInterface(final RSInterfaceChild child, final String act) {
		return iface.clickChild(child, act);
	}

	/**
	 * @deprecated use inventory.clickItem()();
	 */
	@Deprecated
	public boolean atInventoryItem(final int id, final String opt) {
		return inventory.clickItem(id, opt);

	}

	/**
	 * @deprecated use inventory.clickItem();
	 */
	@Deprecated
	public boolean atInventoryItem(final RSItem id, final String opt) {
		return inventory.clickItem(id, opt);
	}

	/**
	 * @deprecated use menu.action();
	 */
	@Deprecated
	public boolean atMenu(final String opt) {
		return menu.action(opt);
	}

	/**
	 * @deprecated use menu.action();
	 */
	@Deprecated
	public boolean atMenu(final String[] i) {
		return menu.action(i);
	}

	/**
	 * @deprecated use menu.clickIndex();
	 */
	@Deprecated
	public boolean atMenuItem(final int i) {
		return menu.clickIndex(i);
	}

	/**
	 * @deprecated Use npc.action();
	 */
	@Deprecated
	public boolean atNPC(final RSNPC rsNPC, final String opt) {
		return npc.action(rsNPC, opt, false);
	}

	/**
	 * @deprecated Use npc.action();
	 */
	@Deprecated
	public boolean atNPC(final RSNPC rsNPC, final String opt, final boolean path) {
		return npc.action(rsNPC, opt, true);
	}

	/**
	 * @deprecated Use objects.at();
	 */
	@Deprecated
	public boolean atObject(final RSObject object, final String act) {
		return objects.at(object, act);
	}

	/**
	 * @deprecated Use Player.action();
	 */
	@Deprecated
	public boolean atPlayer(final RSCharacter c, final String act) {
		return player.action(c, act);
	}

	/**
	 * @deprecated use tile.click();
	 */
	@Deprecated
	public boolean atTile(final RSTile t, final double xd, final double yd,
			final int h, final String act, final String opt) {
		return tile.click(t, xd, yd, h, act, opt);
	}

	/**
	 * @deprecated use tile.click();
	 */
	@Deprecated
	public boolean atTile(final RSTile t, final int h, final double xd, final double yd, final String act) {
		return tile.click(t, xd, yd, h, act);
	}

	/**
	 * @deprecated use tile.click();
	 */
	@Deprecated
	public boolean atTile(final RSTile t, final String act) {
		return tile.click(t, act);
	}

	/**
	 * @deprecated use tile.click();
	 */
	@Deprecated
	public boolean atTile(final RSTile t, final String act, final boolean path) {
		return tile.click(t, act, path);
	}

	/**
	 * @deprecated use tile.click();
	 */
	@Deprecated
	public boolean atTile(final RSTile t, final String act, final String opt) {
		return tile.click(t, act, opt);
	}

	/**
	 * @deprecated Use objects.atTree();
	 */
	@Deprecated
	public boolean atTree(final RSObject tree, final String act) {
		return objects.atTree(tree, act);
	}

	/**
	 * @deprecated use magic.autoCastSpell();
	 */
	@Deprecated
	public boolean autoCastSpell(final int spell) {
		return magic.autoCastSpell(spell);
	}

	/**
	 * @deprecated Use calculate.distance();
	 */
	@Deprecated
	public double calculateDistance(final RSTile t, final RSTile dest) {
		return calculate.distance(t, dest);
	}

	/**
	 * @deprecated Use iface.canContinue();
	 */
	@Deprecated
	public boolean canContinue() {
		return iface.canContinue();
	}

	/**
	 * @deprecated Use objects.canReach();
	 */
	@Deprecated
	public boolean canReach(final Object obj, final boolean can) {
		return objects.canReach(obj, can);
	}

	/**
	 * @deprecated use magic.castSpell();
	 */
	@Deprecated
	public boolean castSpell(final int id) {
		return magic.castSpell(id);
	}

	/**
	 * @deprecated use walk.cleanPath();
	 */
	@Deprecated
	public RSTile[] cleanPath(final RSTile[] path) {
		return walk.cleanPath(path);
	}

	/**
	 * @deprecated use player.action();
	 */
	@Deprecated
	public boolean clickCharacter(final RSCharacter c, final String act) {
		return player.action(c, act);
	}

	/**
	 * @deprecated Use iface.clickContinue();
	 */
	@Deprecated
	public boolean clickContinue() {
		return iface.clickContinue();
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final boolean leftClick) {
		mouse.click(leftClick);
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final boolean leftClick, final int move) {
		mouse.click(leftClick, move);
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final int x, final int y, final boolean leftClick) {
		mouse.click(x, y, 0, 0, leftClick);
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final int x, final int y, final int width, final int height,
			final boolean leftClick) {
		mouse.click(x, y, width, height, leftClick);
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final int x, final int y, final int width, final int height,
			final boolean leftClick, final int move) {
		mouse.click(x, y, width, height, leftClick, move);
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final Point p, final boolean leftClick) {
		mouse.click(p.x, p.y, leftClick);
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final Point p, final int x2, final int y2, final boolean leftClick) {
		mouse.click(p.x, p.y, x2, y2, leftClick);
	}

	/**
	 * @deprecated Use mouse.click();
	 */
	@Deprecated
	public void clickMouse(final Point p, final int x2, final int y2, final boolean leftClick, final int move) {
		mouse.click(p, x2, y2, leftClick, move);
	}

	/**
	 * @deprecated Use iface.clickChild();
	 */
	@Deprecated
	public boolean clickRSComponent(final RSInterfaceChild com, final boolean leftclick) {
		return iface.clickChild(com, leftclick);
	}

	/**
	 * @deprecated Use iface.clickChild();
	 */
	@Deprecated
	public boolean clickRSComponent(final RSInterfaceChild child, final int id,
			final boolean leftclick) {
		return iface.clickChild(child, id, leftclick);
	}

	/**
	 * @deprecated use iface.click();
	 */
	@Deprecated
	public boolean clickRSComponents(final boolean leftclick,
			final RSInterfaceChild... coms) {
		return iface.click(leftclick, coms);
	}

	/**
	 * @deprecated Use npc.click();
	 */
	@Deprecated
	public boolean clickRSNPC(final RSNPC rsNPC, final String act) {
		return npc.click(rsNPC, act, null);
	}

	/**
	 * @deprecated Use npc.click();
	 */
	@Deprecated
	public boolean clickRSNPC(final RSNPC rsNPC, final String act, final String name) {
		return npc.click(rsNPC, act, name);
	}

	/**
	 * @deprecated Use tile1.distanceTo(tile2);
	 */
	@Deprecated
	public int distanceBetween(final RSTile t1, final RSTile t2) {
		return t1.distanceTo(t2);
	}

	/**
	 * @deprecated use character.distanceTo();
	 */
	@Deprecated
	public int distanceTo(final RSCharacter c) {
		return c.distanceTo();
	}

	/**
	 * @deprecated use object.distanceTo();
	 */
	@Deprecated
	public int distanceTo(final RSObject o) {
		return o.distanceTo();
	}

	/**
	 * @deprecated use tile.distanceTo();
	 */
	@Deprecated
	public int distanceTo(final RSTile t) {
		return t.distanceTo();
	}

	/**
	 * @deprecated use mouse.drag();
	 */
	@Deprecated
	public void dragMouse(final int x, final int y) {
		mouse.drag(x, y);
	}

	/**
	 * @deprecated Use mouse.drag();
	 */
	@Deprecated
	public void dragMouse(final Point p) {
		mouse.drag(p.x, p.y);
	}

	/**
	 * @deprecated use inventory.dropAllExcept();
	 */
	@Deprecated
	public void dropAllExcept(final boolean to, final int... ids) {
		inventory.dropAllExcept(to, ids);
	}

	/**
	 * @deprecated use inventory.dropAllExcept();
	 */
	@Deprecated
	public boolean dropAllExcept(final int... ids) {
		return inventory.dropAllExcept(ids);
	}

	/**
	 * @deprecated use inventory.dropItem();
	 */
	@Deprecated
	public void dropItem(final int col, final int row) {
		inventory.dropItem(col, row);
	}

	/**
	 * @deprecated use equipment.contains();
	 */
	@Deprecated
	public boolean equipmentContains(final int... ids) {
		return equipment.contains(ids);
	}

	/**
	 * @deprecated use equipment.containsOneOf()
	 */
	@Deprecated
	public boolean equipmentContainsOneOf(final int... ids) {
		return equipment.containsOneOf(ids);
	}

	/**
	 * @deprecated use ColorUtil.findColorInArea();
	 */
	@Deprecated
	public Point[] findColorInArea(final Rectangle q, final Color desired, final Color tolerance) {
		return ColorUtil.findColorInArea(q, desired, tolerance);
	}

	/**
	 * @deprecated use objects.getNearestByID();
	 */
	@Deprecated
	public RSObject findObject(final int... ids) {
		return objects.getNearestByID(ids);
	}

	/**
	 * @deprecated use object.getNearestByID();
	 */
	@Deprecated
	public RSObject findObject(final int amount, final int id) {
		return objects.getNearestByID(id);
	}

	/**
	 * @deprecated Use object.getNearestByID();
	 */
	@Deprecated
	public RSObject findObject(final int amount, final int... ids) {
		return objects.getNearestByID(ids);
	}

	/**
	 * @deprecated use objects.getNearestByName();
	 */
	@Deprecated
	public RSObject findObjectByName(final String... name) {
		return objects.getNearestByName(name);
	}

	/**
	 * @deprecated use walk.fixPath(path);
	 */
	@Deprecated
	public RSTile[] fixPath(final RSTile[] path) {
		return walk.fixPath(path);
	}

	/**
	 * @deprecated use walk.fixPath2(startX, startY, endX, endY);
	 */
	@Deprecated
	public List<RSTile> fixPath2(final int startX, final int startY, final int endX, final int endY) {
		return walk.fixPath2(startX, startY, endX, endY);
	}

	/**
	 * @deprecated use walk.fixPath2(t);
	 */
	@Deprecated
	public List<RSTile> fixPath2(final RSTile t) {
		return walk.fixPath2(t);
	}

	/**
	 * @deprecated use walk.fixPath2(t1,t2);
	 */
	@Deprecated
	public List<RSTile> fixPath2(final RSTile t1, final RSTile t2) {
		return walk.fixPath2(t1, t2);
	}

	/**
	 * @deprecated use walk.generateFixedPath(x, y);
	 */
	@Deprecated
	public RSTile[] generateFixedPath(final int x, final int y) {
		return walk.generateFixedPath(x, y);
	}

	/**
	 * @deprecated use walk.generateFixedPath(t);
	 */
	@Deprecated
	public RSTile[] generateFixedPath(final RSTile t) {
		return walk.generateFixedPath(t);
	}

	/**
	 * @deprecated Use mouse.generatePath();
	 */
	@Deprecated
	public Point[] generateMousePath(final int amount, final Point end) {
		return mouse.generatePath(amount, end);
	}

	/**
	 * @deprecated use walk.generateProperPath(x, y);
	 */
	@Deprecated
	public RSTile[] generateProperPath(final int x, final int y) {
		return walk.generateProperPath(x, y);
	}

	/**
	 * @deprecated use walk.generateProperPath(t);
	 */
	@Deprecated
	public RSTile[] generateProperPath(final RSTile t) {
		return walk.generateProperPath(t);
	}

	/*
	 * @deprecated Use game.getAccountName();
	 */
	public String getAccountName() {
		return game.getAccountName();
	}

	/**
	 * @deprecated use game.getAccountPin();
	 */
	@Deprecated
	public String getAccountPin() {
		return game.getAccountPin();
	}

	/**
	 * @deprecated use camera.getCharacterAngle(c);
	 */
	@Deprecated
	public int getAngleToCharacter(final RSCharacter c) {
		return camera.getCharacterAngle(c);
	}

	/**
	 * @deprecated use camera.getCordsAngle(x2, y2);
	 */
	@Deprecated
	public int getAngleToCoordinates(final int x2, final int y2) {
		return camera.getCordsAngle(x2, y2);
	}

	/**
	 * @deprecated use camera.getObjectAngle();
	 */
	@Deprecated
	public int getAngleToObject(final RSObject o) {
		return camera.getObjectAngle(o);
	}

	/**
	 * @deprecated use camera.getTileAngle(t);
	 */
	@Deprecated
	public int getAngleToTile(final RSTile t) {
		return camera.getTileAngle(t);
	}

	/**
	 * @deprecated use camera.getAngle();
	 */
	@Deprecated
	public int getCameraAngle() {
		return camera.getAngle();
	}

	/**
	 * @deprecated Use iface.getChild();
	 */
	@Deprecated
	public RSInterfaceChild getChildInterface(final int index, final int child) {
		return iface.getChild(index, child);
	}

	/**
	 * @deprecated use game.getClient();
	 */
	@Deprecated
	public org.rsbot.client.Client getClient() {
		return game.client();
	}

	/**
	 * @deprecated use tile.getClosestOnMap();
	 */
	@Deprecated
	public RSTile getClosestTileOnMap(final RSTile t) {
		return tile.getClosestOnMap(t);
	}

	/**
	 * @deprecated use getContinueChild();
	 */
	@Deprecated
	public RSInterfaceChild getContinueChildInterface() {
		return iface.getContinueChild();
	}

	/**
	 * @deprecated use getContinue();
	 */
	@Deprecated
	public RSInterface getContinueInterface() {
		return iface.getContinue();
	}

	/**
	 * @deprected use skills.getCurrentHitPoints();
	 */
	public int getCurrentHitPoints() {
		return skills.getCurrentHitPoints();
	}

	/**
	 * @deprecated use skills.getCurrentLifePoints()
	 */
	public int getCurrentLifePoints() {
		return skills.getCurrentLifePoints();
	}

	/**
	 * @deprecated use game.getCurrentTab();
	 */
	public int getCurrentTab() {
		return game.getCurrentTab();
	}

	/**
	 * @deprecated use tile.getDestination();
	 */
	@Deprecated
	public RSTile getDestination() {
		return tile.getDestination();
	}

	/**
	 * @deprecated use player.getMyEnergy();
	 */
	@Deprecated
	public int getEnergy() {
		return player.getMyEnergy();
	}

	/**
	 * @deprecated use equipment.getArray();
	 */
	@Deprecated
	public RSItem[] getEquipmentArray() {
		return equipment.getArray();
	}

	/**
	 * @deprecated use equipment.getCount();
	 */
	@Deprecated
	public int getEquipmentCount() {
		return 11 - getEquipmentCount(-1);
	}

	/**
	 * @deprecated use equipment.getCount();
	 */
	@Deprecated
	public int getEquipmentCount(final int id) {
		return equipment.getCount(id);
	}

	/**
	 * @deprecated use equipment.getInterface();
	 */
	@Deprecated
	public RSInterface getEquipmentInterface() {
		return equipment.getInterface();
	}

	/**
	 * @deprecated use equipment.getStackArray();
	 */
	@Deprecated
	public int[] getEquipmentStackArray() {
		return equipment.getStackArray();
	}

	/**
	 * @deprecated use combat.getFightMode();
	 */
	@Deprecated
	public int getFightMode() {
		return combat.getFightMode();
	}

	/**
	 * @deprecated use ground.getItem();
	 */
	@Deprecated
	public RSItemTile getGroundItem(final int amount) {
		return ground.getItem(amount).getItemTile();
	}

	/**
	 * @deprecated use ground.getItemArray();
	 */
	@Deprecated
	public RSItemTile[] getGroundItemArray(final int amount) {
		return ground.getItemTileArray(amount);
	}

	/**
	 * @deprecated use ground.getItemByID();
	 */
	@Deprecated
	public RSItemTile getGroundItemByID(final int id) {
		return ground.getItemByID(id).getItemTile();
	}

	/**
	 * @deprecated use ground.getItemByID();
	 */
	@Deprecated
	public RSItemTile getGroundItemByID(final int amount, final int id) {
		return ground.getItemByID(amount, id).getItemTile();
	}

	/**
	 * @deprecated use ground.getItemByID();
	 */
	@Deprecated
	public RSItemTile getGroundItemByID(final int amount, final int[] ids) {
		return ground.getItemByID(amount, ids).getItemTile();
	}

	/**
	 * @deprecated use ground.getItemByID(ids);
	 */
	@Deprecated
	public RSItemTile getGroundItemByID(final int[] ids) {
		return ground.getItemByID(ids).getItemTile();
	}

	/**
	 * @deprecated use ground.getItemsAt();
	 */
	@Deprecated
	public RSItemTile[] getGroundItemsAt(final int x, final int y) {
		return ground.getItemTilesAt(x, y);
	}

	/**
	 * @deprecated use ground.getItemsAt();
	 */
	@Deprecated
	public RSItemTile[] getGroundItemsAt(final RSTile t) {
		return ground.getItemTilesAt(t.getX(), t.getY());
	}

	/**
	 * @deprecated use skills.getHPPercent()
	 */
	public double getHPPercent() {
		return skills.getHPPercent();
	}

	/**
	 * @deprecated use game.getIDToName()
	 */
	@Deprecated
	public String getIDtoName(final int... ids) {
		return game.getIDToName(ids);
	}

	/**
	 * @deprecated use iface.get();
	 */
	@Deprecated
	public RSInterface getInterface(final int id) {
		return iface.get(id);
	}

	/**
	 * @deprecated use iface.getChild();
	 */
	@Deprecated
	public RSInterfaceChild getInterface(final int index, final int child) {
		return iface.getChild(index, child);
	}

	/**
	 * @deprecated use iface.getContaingText
	 */
	@Deprecated
	public RSInterface[] getInterfacesContainingText(final String text) {
		return iface.getAllContaining(text);
	}

	/**
	 * @deprecated use inventory.getArray();
	 */
	@Deprecated
	public int[] getInventoryArray() {
		return inventory.getArray();
	}

	/**
	 * @deprecated use inventory.getCount();
	 */
	@Deprecated
	public int getInventoryCount() {
		return inventory.getCount();
	}

	/**
	 * @deprecated use inventory.getCount(stacks);
	 */
	@Deprecated
	public int getInventoryCount(final boolean stacks) {
		return inventory.getCount(stacks);
	}

	/**
	 * @deprecated use inventory.getCount();
	 */
	@Deprecated
	public int getInventoryCount(final boolean stacks, final int... ids) {
		return inventory.getCount(stacks, ids);
	}

	/**
	 * @deprecated use inventory.getCount(ids);
	 */
	@Deprecated
	public int getInventoryCount(final int... ids) {
		return inventory.getCount(ids);
	}

	/**
	 * @deprecated use inventory.getCountExcept();
	 */
	@Deprecated
	public int getInventoryCountExcept(final int... ids) {
		return inventory.getCountExcept(ids);
	}

	/**
	 * @deprecated use inventory.getInterface();
	 */
	@Deprecated
	public RSInterfaceChild getInventoryInterface() {
		return inventory.getInterface();
	}

	/**
	 * @deprecated use inventory.getItem();
	 */
	@Deprecated
	public RSItem[] getInventoryItem() {
		return inventory.getItems();
	}

	/**
	 * @deprecated use inventory.getItem();
	 */
	@Deprecated
	public RSItem getInventoryItem(final int... ids) {
		return inventory.getItem(ids);
	}

	/**
	 * @deprecated use inventory.getItemAt();
	 */
	@Deprecated
	public RSItem getInventoryItemAt(final int index) {
		return inventory.getItemAt(index);
	}

	/**
	 * @deprecated use inventory.getItemByID();
	 */
	@Deprecated
	public RSItem getInventoryItemByID(final int... ids) {
		return inventory.getItemByID(ids);
	}

	/**
	 * @deprecated use inventory.getItemID();
	 */
	@Deprecated
	public int getInventoryItemIDByName(final String name) {
		return inventory.getItemID(name);
	}

	/**
	 * @deprecated use inventory.getItemPoint();
	 */
	@Deprecated
	public Point getInventoryItemPoint(final int index) {
		return inventory.getItemPoint(index);
	}

	/**
	 * @deprecated use inventory.getItems();
	 */
	@Deprecated
	public RSItem[] getInventoryItems() {
		return inventory.getItems();
	}

	/**
	 * @deprecated use inventory.getArray();
	 */
	@Deprecated
	public int[] getInventoryStackArray() {
		return inventory.getArray();
	}

	/**
	 * @deprecated use game.getLastMessage();
	 */
	@Deprecated
	public String getLastMessage() {
		return game.getLastMessage();
	}

	/**
	 * @deprecated use player.getLocation();
	 */
	@Deprecated
	public RSTile getLocation() {
		return player.getMyLocation();
	}

	/**
	 * @deprecated use game.getLoginIndex();
	 */
	@Deprecated
	public int getLoginIndex() {
		return game.getLoginIndex();
	}

	/**
	 * @deprcated use menu.getActions();
	 */
	public String[] getMenuActions() {
		return menu.getActions();
	}

	/**
	 * @deprecated use menu.getIndex();
	 */
	@Deprecated
	public int getMenuIndex(final String opt) {
		return menu.getIndex(opt);
	}

	/**
	 * @deprecated use menu.getIndex()
	 */
	@Deprecated
	public int getMenuIndex(final String act, final String opt) {
		return menu.getIndex(act, opt);
	}

	/**
	 * @deprecated use menu.getItems();
	 */
	@Deprecated
	public String[] getMenuItems() {
		return menu.getItems();
	}

	/**
	 * @deprecated use menu.getLocation();
	 */
	@Deprecated
	public Point getMenuLocation() {
		return menu.getLocation();
	}

	/**
	 * @deprecated use menu.getOptions();
	 */
	@Deprecated
	public String[] getMenuOptions() {
		return menu.getOptions();
	}

	/**
	 * @deprecated use mouse.getLocation
	 */
	@Deprecated
	public Point getMouseLocation() {
		return mouse.getLocation();
	}

	/**
	 * @deprecated Use mouse.get.path();
	 */
	@Deprecated
	protected boolean getMousePath() {
		return mouse.getPath();
	}

	/**
	 * @deprecated Use mous.getSpeed();
	 */
	@Deprecated
	protected int getMouseSpeed() {
		return mouse.getSpeed();
	}

	/**
	 * @deprecated Use player.getMine()
	 */
	@Deprecated
	public RSPlayer getMyPlayer() {
		return player.getMine();
	}

	/**
	 * @deprecated use game.getNameToID();
	 */
	@Deprecated
	public int getNametoID(final String name) {
		return game.getNameToID(name);
	}

	/**
	 * @deprecated Use npc.getNearestFreeByID();
	 */
	@Deprecated
	public RSNPC getNearestFreeNPCByID(final int... ids) {
		return npc.getNearestFreeByID(ids);
	}

	/**
	 * @deprecated Use npc.getNearestFreeByName();
	 */
	@Deprecated
	public RSNPC getNearestFreeNPCByName(final String... names) {
		return npc.getNearestFreeByName(names);
	}

	/**
	 * @deprecated Use npc.getNearestFreeToAttackByID();
	 */
	@Deprecated
	public RSNPC getNearestFreeNPCToAttackByID(final int... ids) {
		return npc.getNearestFreeToAttackByID(ids);
	}

	/*
	 * @deprecated Use npc.getNearestFreeToAttackByName();
	 */
	public RSNPC getNearestFreeNPCToAttackByName(final String... names) {
		return npc.getNearestFreeToAttackByName(names);
	}

	/**
	 * @deprecated use ground.getNearestByID()
	 */
	@Deprecated
	public RSItemTile getNearestGroundItemByID(final int... ids) {
		return ground.getNearestItemByID(ids).getItemTile();
	}

	/**
	 * @deprecated use ground.getNearestItemInAreaByID();
	 */
	@Deprecated
	public RSItemTile getNearestGroundItemInAreaByID(final RSArea search, final int... ids) {
		return ground.getNearestItemInAreaByID(search, ids).getItemTile();
	}

	/**
	 * @deprecated Use npc.getNearestNPCByID();
	 */
	@Deprecated
	public RSNPC getNearestNPCByID(final int... ids) {
		return npc.getNearestByID(ids);
	}

	/**
	 * @deprecated Use npc.getNearestNPCByName();
	 */
	@Deprecated
	public RSNPC getNearestNPCByName(final String... names) {
		return npc.getNearestByName(names);
	}

	/**
	 * @deprecated Use npc.getNearestToAttackByID();
	 */
	@Deprecated
	public RSNPC getNearestNPCToAttackByID(final int... ids) {
		return npc.getNearestToAttackByID(ids);
	}

	/**
	 * @deprecated Use npc.getNearestToAttackByName();
	 */
	@Deprecated
	public RSNPC getNearestNPCToAttackByName(final String... names) {
		return npc.getNearestToAttackByName(names);
	}

	/**
	 * @deprecated Use objects.getNearestByID();
	 */
	@Deprecated
	public RSObject getNearestObjectByID(final int... ids) {
		return objects.getNearestByID(ids);
	}

	/**
	 * @deprecated Use objects.getNearestByName();
	 */
	@Deprecated
	public RSObject getNearestObjectByName(final String... names) {
		return objects.getNearestByName(names);
	}

	/**
	 * @deprecated Use player.getNearestByLevel();
	 */
	@Deprecated
	public RSPlayer getNearestPlayerByLevel(final int level) {
		return player.getNearestByLevel(level);
	}

	/**
	 * @deprecated Use player.getNearestByLevel();
	 */
	@Deprecated
	public RSPlayer getNearestPlayerByLevel(final int min, final int max) {
		return player.getNearestByLevel(min, max);
	}

	/**
	 * @deprecated Use player.getNearestByName
	 */
	@Deprecated
	public RSPlayer getNearestPlayerByName(final String name) {
		return player.getByName(name);
	}

	/*
	 * 8
	 * 
	 * @deprecated Use npc.getArray();
	 */
	public RSNPC[] getNPCArray(final boolean interacting) {
		return npc.getArray(interacting);
	}

	/**
	 * @deprecated Use npc.getNearByFilter();
	 */
	@Deprecated
	public RSNPC getNPCByFilter(final Filter<RSNPC> filter) {
		return npc.getNearByFilter(filter);
	}

	/**
	 * @deprecated use getObejctsAt or getTopObjectAt instead
	 */
	@Deprecated
	public RSObject getObjectAt(final int x, final int y) {
		return getTopObjectAt(x, y);
	}

	/**
	 * @deprecated use getObejctsAt or getTopObjectAt instead
	 */
	@Deprecated
	public RSObject getObjectAt(final RSTile t) {
		return getTopObjectAt(t);
	}

	/**
	 * @deprecated use objects.getAt
	 */
	@Deprecated
	public RSObject[] getObjectsAt(final int x, final int y) {
		return objects.getAt(x, y);
	}

	/**
	 * @deprecated use object.getAt
	 */
	@Deprecated
	public RSObject[] getObjectsAt(final RSTile t) {
		return objects.getAt(t);
	}

	/**
	 * @deprecated use game.getPlane();
	 */
	@Deprecated
	public int getPlane() {
		return game.getPlane();
	}

	/**
	 * @deprecated Use mouse.getRandomX();
	 */
	@Deprecated
	public int getRandomMouseX(final int max) {
		return mouse.getRandomX(max);
	}

	/**
	 * @deprecated Use.mouse.getRandomY();
	 */
	@Deprecated
	public int getRandomMouseY(final int max) {
		return mouse.getRandomY(max);
	}

	/**
	 * @deprecated use objects.getRealDistanceTo();
	 */
	@Deprecated
	public int getRealDistanceTo(final RSTile t, final boolean obj) {
		return objects.getRealDistanceTo(t, obj);
	}

	/**
	 * @deprecated use game.client().isItemSelected();
	 */
	@Deprecated
	public int getSelectedItemID() {
		return game.client().isItemSelected();
	}

	/**
	 * @deprecated use game.getSelectedItemName();
	 */
	@Deprecated
	public String getSelectedItemName() {
		return game.getSelectedItemName();
	}

	/**
	 * @deprecated use prayer.getSelection();
	 */
	@Deprecated
	public RSInterfaceChild[] getSelectedPrayers() {
		return prayer.getSelection();
	}

	/**
	 * @deprecated Use settings.get();
	 */
	@Deprecated
	public int getSetting(final int set) {
		return settings.get(set);
	}

	/**
	 * @deprecated Use settings.getArray();
	 */
	@Deprecated
	public int[] getSettingArray() {
		return settings.getArray();
	}

	/**
	 * @deprecated Use game.getTalkInterface();
	 */
	@Deprecated
	public RSInterfaceChild getTalkInterface() {
		return game.getTalkInterface();
	}

	/**
	 * @deprecated use tile.getOnScreen();
	 */
	@Deprecated
	public RSTile getTileOnScreen(final RSTile t) {
		return tile.getOnScreen(t);
	}

	/**
	 * @Deprecated use tile.getUnderMouse();
	 */
	public RSTile getTileUnderMouse() {
		return tile.getUnderMouse();
	}

	/**
	 * @deprecated use objects.getTopAt
	 */
	@Deprecated
	public RSObject getTopObjectAt(final int x, final int y) {
		return objects.getTopAt(x, y);
	}

	/**
	 * @deprecated use objects.getTopAt
	 */
	@Deprecated
	public RSObject getTopObjectAt(final RSTile t) {
		return objects.getTopAt(t);
	}

	/**
	 * @deprecated USe combat.getWildernessLevel();
	 */
	@Deprecated
	public int getWildernessLevel() {
		return combat.getWildernessLevel();
	}

	/**
	 * @deprecated use ColorUtil.grabColorAt(x, y);
	 */
	@Deprecated
	public Color grabColorAt(final int x, final int y) {
		return ColorUtil.grabColorAt(x, y);
	}

	public void init() {
		this.bank = Bot.bank;
		this.calculate = Bot.calculate;
		this.camera = Bot.camera;
		this.combat = Bot.combat;
		this.game = Bot.game;
		this.grandExchange = Bot.grandExchange;
		this.equipment = Bot.equipment;
		this.enviro = Bot.enviro;
		this.ge = Bot.ge;
		this.ground = Bot.ground;
		this.iface = Bot.iface;
		this.input = Bot.getInputManager();
		this.inventory = Bot.inventory;
		this.keyboard = Bot.keyboard;
		this.lobby = Bot.lobby;
		this.magic = Bot.magic;
		this.menu = Bot.menu;
		this.mouse = Bot.mouse;
		this.npc = Bot.npc;
		this.nodes = Bot.nodes;
		this.objects = Bot.objects;
		this.player = Bot.player;
		this.prayer = Bot.prayer;
		this.skills = Bot.skills;
		this.store = Bot.store;
		this.screen = Bot.screen;
		this.settings = Bot.settings;
		this.summoning = Bot.summoning;
		this.tile = Bot.tile;
		this.walk = Bot.walk;
	}

	/**
	 * @deprecated use inventory.contains()
	 */
	@Deprecated
	public boolean inventoryContains(final int... ids) {
		return inventory.contains(ids);
	}

	/**
	 * @deprecated use inventory.containsOneOf();
	 */
	@Deprecated
	public boolean inventoryContainsOneOf(final int... id) {
		return inventory.containsOneOf(id);
	}

	/**
	 * @deprecated use inventory.dropAllExcept();
	 */
	@Deprecated
	public boolean inventoryEmptyExcept(final int... ids) {
		return inventory.dropAllExcept(ids);
	}

	/**
	 * @deprecated Use player.isCarringItem();
	 */
	@Deprecated
	public boolean isCarryingItem(final int... ids) {
		return player.isCarryingItem(ids);
	}

	/**
	 * @deprecated use menu.isDefaultAction(act);
	 */
	@Deprecated
	public boolean isDefaultAction(final String act) {
		return menu.isDefaultAction(act);
	}

	/**
	 * @deprecated Use player.isIdle();
	 */
	@Deprecated
	public boolean isIdle() {
		return player.isIdle();
	}

	/**
	 * @deprecated use inventory.isFull();
	 */
	@Deprecated
	public boolean isInventoryFull() {
		return inventory.isFull();
	}

	/**
	 * @deprecated use inventory.isItemSelected();
	 */
	@Deprecated
	public boolean isItemSelected() {
		return inventory.isItemSelected();
	}

	/**
	 * @deprecated use inventory.isItemSelected();
	 */
	@Deprecated
	public boolean isItemSelected(final int id) {
		return inventory.isItemSelected(id);
	}

	/**
	 * @deprecated @see {@link org.lazygamerz.scripting.api.Game#isLoggedIn()}
	 */
	@Deprecated
	public boolean isLoggedIn() {
		return game.isLoggedIn();
	}

	/**
	 * @deprecated @see
	 *             {@link org.lazygamerz.scripting.api.Game#isLoginScreen()}
	 */
	@Deprecated
	public boolean isLoginScreen() {
		return game.isLoginScreen();
	}

	/**
	 * @deprecated use menu.isOpen();
	 */
	@Deprecated
	public boolean isMenuOpen() {
		return menu.isOpen();
	}

	/**
	 * @deprecated Use game.isOnLogoutTab();
	 */
	public boolean isOnLogoutTab() {
		return game.isOnLogoutTab();
	}

	/**
	 * @deprecated use prayer.isOn()
	 */
	@Deprecated
	public boolean isPrayerOn(final int index) {
		return prayer.isOn(index);
	}

	/**
	 * @deprecated use combat.isAutoRetaliateEnabled();
	 */
	public boolean isRetaliateEnabled() {
		return combat.isAutoRetaliateEnabled();
	}

	/**
	 * @deprecated Use player.isRunning();
	 */
	@Deprecated
	public boolean isRunning() {
		return player.isRunning();
	}

	/**
	 * @deprecated use game.isRunOn();
	 */
	@Deprecated
	public boolean isRunOn() {
		return game.isRunOn();
	}

	/**
	 * @deprecated use iface.isTextInputOpen();
	 */
	@Deprecated
	public boolean isTextInputOpen() {
		return iface.isTextInputOpen();
	}

	/**
	 * @deprecated use game.isWelcomeScreen();
	 */
	@Deprecated
	public boolean isWelcomeScreen() {
		return game.isWelcomeScreen();
	}

	/**
	 * @deprecated use game.login();
	 */
	public boolean login() {
		return game.login();
	}

	/**
	 * @deprecated Use game.logout();
	 */
	public boolean logout() {
		return game.logout();
	}

	/**
	 * @deprecated use menu.contains();
	 */
	public boolean menuContains(final String item) {
		return menu.contains(item);
	}

	/**
	 * @deprecated Use game.clickChatButton();
	 */
	public void mouseChatButton(final int button, final boolean left) {
		game.clickChatButton(button, left);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final int x, final int y) {
		mouse.move(mouse.getSpeed(), x, y, 0, 0);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final int x, final int y, final boolean paths) {
		mouse.move(mouse.getSpeed(), x, y, 0, 0, 0, paths);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final int x, final int y, final int offset) {
		moveMouse(mouse.getSpeed(), x, y, 0, 0, offset);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final int x, final int y, final int randX, final int randY) {
		mouse.move(mouse.getSpeed(), x, y, randX, randY, 0);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final int speed, final int x, final int y, final int randX, final int randY) {
		mouse.move(speed, x, y, randX, randY, 0);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final int speed, final int x, final int y, final int randX, final int randY,
			final int afterOffset) {
		mouse.move(speed, x, y, randX, randY, afterOffset, mouse.getPath());
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final int speed, final int x, final int y, final int randX, final int randY,
			final int afterOffset, final boolean mousePaths) {
		mouse.move(speed, x, y, randX, randY, afterOffset, mousePaths);

	}

	/**
	 * @deprecated Use.mouse.move();
	 */
	public void moveMouse(final int Speed, final Point p, final boolean paths) {
		mouse.move(Speed, p, paths);
	}

	/**
	 * @deprecated Use mouse.move()
	 */
	public void moveMouse(final Point p) {
		mouse.move(p);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final Point p, final boolean paths) {
		mouse.move(p, paths);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final Point p, final int offset, final boolean paths) {
		mouse.move(getMouseSpeed(), p.x, p.y, 0, 0, offset, paths);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final Point p, final int randX, final int randY) {
		mouse.move(p.x, p.y, randX, randY);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final Point p, final int randX, final int randY, final boolean paths) {
		mouse.move(getMouseSpeed(), p.x, p.y, randX, randY, 0, paths);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouse(final Point p, final int randX, final int randY, final int offset) {
		mouse.move(getMouseSpeed(), p.x, p.y, randX, randY, offset);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouseByPath(final Point p) {
		mouse.move(p, random(1, 6), 0, 0);
	}

	/**
	 * @deprecated Use mouse.move();
	 */
	public void moveMouseByPath(final Point p, final int amount) {
		mouse.move(p, amount, 0, 0);
	}

	/*
	 * @deprecated Use mouse.move();
	 */
	public void moveMouseByPath(final Point p, final int randX, final int randY) {
		mouse.move(p, random(1, 6), randX, randY);
	}

	/*
	 * @deprecated Use mouse.move();
	 */
	public void moveMouseByPath(final Point p, final int amount, final int randX, final int randY) {
		mouse.move(mouse.generatePath(amount, p), randX, randY);
	}

	/*
	 * @deprecated Use mouse.move();
	 */
	public void moveMousePath(final Point[] path, final int randX, final int randY) {
		mouse.move(path, randX, randY);
	}

	/*
	 * @deprecated Use mouse.moveRandomly();
	 */
	public void moveMouseRandomly(final int max) {
		mouse.moveRandomly(max);
	}

	/**
	 * @deprecated Use mouse.moveSlightly();
	 */
	public void moveMouseSlightly() {
		mouse.moveSlightly();
	}

	/**
	 * @deprecated use walk.nextTile();
	 */
	public RSTile nextTile(final RSTile path[]) {
		return walk.nextTile(path);
	}

	/**
	 * @deprecated use walk.nextTile();
	 */
	public RSTile nextTile(final RSTile path[], final int max) {
		return walk.nextTile(path, max);
	}

	/**
	 * @deprecated use walk.nextTile();
	 */
	public RSTile nextTile(final RSTile path[], final int max, final boolean enable) {
		return walk.nextTile(path, max, enable);
	}

	/**
	 * @deprecated use tile.click();
	 */
	public boolean onTile(final RSTile t, final String search, final String act) {
		return tile.click(t, search, act);
	}

	/**
	 * @deprecated Use game.openTab();
	 */
	public void openTab(final int tab) {
		game.openTab(tab);
	}

	/**
	 * @deprecated use player.isCarryingItem();
	 */
	public boolean playerHasOneOf(final int... ids) {
		return player.isCarryingItem(ids);
	}

	/**
	 * @deprecated use calculate.pointOnScreen();
	 */
	public boolean pointOnScreen(final Point p) {
		return calculate.pointOnScreen(p);
	}

	/**
	 * @deprecated use inventory.randomizeItemPoint(p);
	 */
	public Point randomiseInventoryItemPoint(final Point p) {
		return inventory.randomizeItemPoint(p);
	}

	/**
	 * @deprecated use walk.randomizePath();
	 */
	public RSTile[] randomizePath(final RSTile[] path, final int X, final int Y) {
		return walk.randomizePath(path, X, Y);
	}

	/**
	 * @deprecated use walk.randomizeTile();
	 */
	public RSTile randomizeTile(final RSTile tile, final int maxX, final int maxY) {
		return walk.randomizeTile(tile, maxX, maxY);
	}

	/**
	 * @deprecated use player.rest();
	 */
	public boolean rest() {
		return player.rest(100);
	}

	/**
	 * @deprecated use player.rest();
	 */
	public boolean rest(final int amount) {
		return player.rest(amount);
	}

	/**
	 * @deprecated use walk.reversePath(r);
	 */
	public RSTile[] reversePath(final RSTile[] r) {
		return walk.reversePath(r);
	}

	/**
	 * @deprecated use keyboard.sendKey();
	 */
	public void sendKey(final char c) {
		input.sendKey(c);
	}

	/**
	 * @deprecated use keyboard.sendText();
	 */
	public void sendText(final String text, final boolean enter) {
		input.sendKeys(text, enter);
	}

	/**
	 * @deprecated use game.setAssistMode();
	 */
	public boolean setAssistMode(
			final org.lazygamerz.scripting.api.Game.CHAT_MODE mode) {
		return game.setAssistMode(mode);
	}

	/**
	 * @deprecated Use camera.setAltitude();
	 */
	public void setCameraAltitude(final boolean setKey) {
		camera.setAltitude(setKey);
	}

	/**
	 * @deprecated use camera.setAltitude();
	 */
	public boolean setCameraAltitude(final double percent) {
		return camera.setAltitude(percent);
	}

	/**
	 * @deprecated use camera.setRotation();
	 */
	public void setCameraRotation(final int degrees) {
		camera.setRotation(degrees);
	}

	/**
	 * @deprecated Use game.setClanMode();
	 */
	public boolean setClanMode(final org.lazygamerz.scripting.api.Game.CHAT_MODE mode) {
		return game.setClanMode(mode);
	}

	/**
	 * @deprecated Use camera.setCompass(direction);
	 */
	public void setCompass(final char direction) {
		camera.setCompass(direction);
	}

	/**
	 * @deprecated use game.getEnforceTabFocus();
	 */
	@Deprecated
	public void setEnforceTabFocus(final boolean force) {
		game.setEnforceTabFocus(force);
	}

	/**
	 * @deprecated Use combat.setFightMode();
	 */
	public void setFightMode(final int mode) {
		combat.setFightMode(mode);
	}

	/**
	 * @deprecated use prayer.set();
	 */
	public boolean setPrayer(final int pray, final boolean enable) {
		return prayer.set(pray, enable);
	}

	/**
	 * @deprecated Use game.setPrivateChat();
	 */
	public boolean setPrivateChat(
			final org.lazygamerz.scripting.api.Game.CHAT_MODE mode) {
		return game.setPrivateChat(mode);
	}

	/**
	 * @deprecated Use game.setPublicChat();
	 */
	public boolean setPublicChat(
			final org.lazygamerz.scripting.api.Game.CHAT_MODE mode) {
		return game.setPublicChat(mode);
	}

	/**
	 * @deprecated use game.setRun();
	 */
	public void setRun(final boolean enable) {
		game.setRun(enable);
	}

	/**
	 * @deprecated use game.setTradeMode();
	 */
	public boolean setTradeMode(final org.lazygamerz.scripting.api.Game.CHAT_MODE mode) {
		return game.setTradeMode(mode);
	}

	/**
	 * @deprecated use menu.setupListener();
	 */
	public void setupListener() {
		menu.setupListener();
	}

	/**
	 * @deprecated Use game.showAllChatMessages();
	 */
	public void showAllChatMessages() {
		game.showAllChatMessages();
	}

	/**
	 * @deprecated use game.showGameChatMessages();
	 */
	public void showGameChatMessages() {
		game.showGameChatMessages();
	}

	/**
	 * @deprecated Use {@link #stopScript()} instead.
	 */
	public void stopAllScripts() {
	}

	/**
	 * @deprecated use menu.stripFormatting();
	 */
	@Deprecated
	public String stripFomatting(final String input) {
		return menu.stripFormatting(input);
	}

	/**
	 * @deprecated use game.switchWorld()
	 */
	public void switchWorld(final int world) {
		game.switchWorld(world);
	}

	/**
	 * @deprecated use RSTile's isOnMinimap() method.
	 */
	public boolean tileOnMap(final RSTile t) {
		return t.isOnMinimap();
	}

	/**
	 * @deprecated use RSTile's isOnScreen method.
	 */
	public boolean tileOnScreen(final RSTile t) {
		return t.isOnScreen();
	}

	/**
	 * @deprecated use RSTile's getScreenLocation() method
	 */
	public Point tileToMinimap(final RSTile t) {
		return t.getMapLocation();
	}

	/**
	 * @deprecated use RSCharacter's turnTo() method;
	 */
	public void turnToCharacter(final RSCharacter c) {
		c.turnTo();
	}

	/**
	 * @deprecated use camera.turnTo();
	 */
	public void turnToCharacter(final RSCharacter c, final int max) {
		camera.turnTo(c, max);
	}

	/**
	 * @deprecated use camera.turnTo();
	 */
	public void turnToCoordinates(final int x, final int y) {
		camera.turnTo(x, y);
	}

	/**
	 * @deprecated use camera.turnTo();
	 */
	public void turnToCoordinates(final int x, final int y, final int max) {
		camera.turnTo(x, y, max);
	}

	/**
	 * @deprecated use RSObject's turnTo() method
	 */
	public void turnToObject(final RSObject o) {
		o.turnTo();
	}

	/**
	 * @deprecated use camera.turnTo();
	 */
	public void turnToObject(final RSObject o, final int max) {
		camera.turnTo(o, max);
	}

	/**
	 * @deprecated use camera.turnTo();
	 */
	public void turnToTile(final RSTile t) {
		camera.turnTo(t);
	}

	/**
	 * @deprecated use.camera.turnTo();
	 */
	public void turnToTile(final RSTile t, final int max) {
		camera.turnTo(t, max);
	}

	/**
	 * @deprecated use inventory.useItem(item, to);
	 */
	public boolean useItem(final RSItem item, final RSItem to) {
		return inventory.useItem(item, to);
	}

	/**
	 * @deprecated use inventory.useItem(item, object);
	 */
	public boolean useItem(final RSItem item, final RSObject Object) {
		return inventory.useItem(item, Object);
	}

	/**
	 * @deprecated use player.waitForAnim();
	 */
	public int waitForAnim(final int ms) {
		return player.waitForAnim(ms);
	}

	/**
	 * @deprecated use iface.waitForOpen();
	 */
	public boolean waitForIface(final RSInterface face, final int ms) {
		return iface.waitForOpen(face, ms);
	}

	/**
	 * @deprecated Use iface.waitforChildOpen();
	 */
	public boolean waitForInterface(final RSInterfaceChild child, final int ms) {
		return iface.waitForChildOpen(child, ms);
	}

	/**
	 * @deprecated use inventory.waitForCount();
	 */
	public int waitForInventoryCount(final int item, final int count, final int ms) {
		return inventory.waitForCount(item, count, ms);
	}

	/**
	 * @deprecated use player.waitToMove();
	 */
	public boolean waitToMove(final int ms) {
		return player.waitToMove(ms);
	}

	/**
	 * @deprecated use walk.pathMM();
	 */
	public boolean Walk(final RSTile[] path) {
		return walk.pathMM(path);
	}

	/**
	 * @deprecated use walk.PathMM();
	 */
	public boolean walkPathMiniM(final RSTile[] path) {
		return walk.pathMM(path);
	}

	/**
	 * @deprecated use walk.pathMM();
	 */
	public boolean walkPathMiniM(final RSTile[] path, final int max) {
		return walk.pathMM(path, max);
	}

	/**
	 * @deprecated use walk.pathMM();
	 */
	public boolean walkPathMiniM(final RSTile[] path, final int x, final int y) {
		return walk.pathMM(path, x, y);
	}

	/**
	 * @deprecated use walk.pathMM();
	 */
	public boolean walkPathMiniM(final RSTile[] path, final int max, final int x, final int y) {
		return walk.pathMM(path, max, x, y);
	}

	/**
	 * @deprecated use walk.pathOnScreen();
	 */
	public boolean walkPathOnScreen(final RSTile[] path) {
		return walk.pathOnScreen(path);
	}

	/**
	 * @deprecated use walk.pathOnScreen(path, max);
	 */
	public boolean walkPathOnScreen(final RSTile[] path, final int max) {
		return walk.pathOnScreen(path, max);
	}

	/**
	 * @deprecated use walk.tileMM();
	 */
	public boolean walkTileMiniM(final RSTile t) {
		return walk.tileMM(t);
	}

	/**
	 * @deprecated use walk.tileMM();
	 */
	public boolean walkTileMiniM(final RSTile t, final int x, final int y) {
		return walk.tileMM(t, x, y);
	}

	/**
	 * @deprecated use walk.tileOnScreen();
	 */
	public boolean walkTileOnScreen(final RSTile t) {
		return walk.tileOnScreen(t);
	}

	/**
	 * @deprecated use walk.to();
	 */
	public boolean walkTo(final RSTile t) {
		return walk.to(t, 2, 2);
	}

	/**
	 * @deprecated use walk.to();
	 */
	public boolean walkTo(final RSTile t, final int x, final int y) {
		return walk.to(t, x, y);
	}

	/**
	 * @deprecated use walk.toClosestTile();
	 */
	public boolean walkToClosestTile(final RSTile[] t) {
		return walk.toClosestTile(t);
	}

	/**
	 * @deprecated use walk.toClosestTile();
	 */
	public boolean walkToClosestTile(final RSTile[] t, final int x, final int y) {
		return walk.toClosestTile(t, x, y);
	}
}
