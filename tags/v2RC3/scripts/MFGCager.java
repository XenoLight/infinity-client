import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = { "Masacrator, Secret Spy" }, category = "Fishing", name = "MFGCager", version = 2.05, description = "This is a Revamped version of MGFCager By Secret Spy")
public class MFGCager extends Script implements PaintListener, MessageListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	// Ints
	private Bot bot;
	public int cage = 301;
	public int spot = 312;
	public int bankID = 49018;
	public int lobbie = 377;
	public int lobPrice;
	public int expgain;
	public int startlevel, levelsgained;
	public long levelIn;
	private long startTime;
	private int startExp;
	private int startLevel;
	public long runTime = 0, seconds = 0, minutes = 0, hours = 0;
	public int gainedExp = 0;
	public int expToLevel = 0;
	public long secToLevel = 0;
	public long minutesToLevel = 0;
	public long hoursToLevel = 0;
	public float secExp = 0;
	public float minuteExp = 0;
	public float hourExp = 0;
	private int numCaught = 0;
	private final double expPer = 110;
	boolean AntiBanDebug = true;
	// New Tiles
	RSTile bankTile = new RSTile(2585, 3422);
	RSTile fishTile = new RSTile(2598, 3420);
	RSTile[] toBank = { new RSTile(2598, 3420), new RSTile(2593, 3420),
			new RSTile(2588, 3422), new RSTile(2585, 3422) };
	RSTile[] toFish = walk.reversePath(toBank);
	RSTile[] toNewSpots = { new RSTile(2598, 3421), new RSTile(2596, 3420),
			new RSTile(2590, 3421), new RSTile(2586, 3422) };

	@Override
	public boolean onStart(final Map<String, String> args) {
		log("Thank you for using MFGCager, if you found any bugs, report it on forums.");
		//final GEItemInfo lobsterGE = grandExchange.loadItemInfo(lobbie);

		lobPrice = ge.loadItemInfo(lobbie).getPrice();
		return true;
	}

	@Override
	public void onFinish() {
		ScreenshotUtil.takeScreenshot(bot, true);
		log("Thanks for using MFGCager, you caught :" + numCaught
				+ " and got : " + gainedExp + ".");
	}

	@Override
	public int getMouseSpeed() {
		return random(5, 9);
	}

	private final ScriptManifest scriptInfo = getClass().getAnnotation(
			ScriptManifest.class);

	public boolean starting() {
		if (iface.canContinue() || iface.clickContinue()) {
			wait(random(500, 1000));
			return true;
		}
		return false;
	}

	public boolean checkIfLevelUp() {
		if (iface.get(INTERFACE_LEVELUP).isValid()) {
			ScreenshotUtil.takeScreenshot(bot, true);
			return true;
		}
		return true;
	}

	public boolean lookSpots() {
		RSNPC spottier = npc.getNearestByID(spot);
		if (spottier == null) {
			walk.pathMM(toNewSpots);
		}
		return true;
	}

	public int loop() {
		if (!inventory.contains(cage)) {
			if (starting()) {
				return random(250, 500);
			} else {
				log("Withdrawing implements");
				RSObject guy = objects.getNearestByID(bankID);
				if (guy == null) {
					return random(500, 1000);
				}
				objects.at(guy, "Quickly");
				wait(random(900, 1000));
				bank.atItem(311, "aw-1");
				wait(random(500, 600));
			}
		}

		checkIfLevelUp();
		switch (getState()) {
		case USE_BANK:
			bank.depositAllExcept(cage);
			break;
		case OPEN_BANK:
			RSObject banky = objects.getNearestByID(bankID);
			if (banky == null) {
				return random(500, 1000);
			}
			if (!tile.onScreen(banky.getLocation())) {
				walk.tileMM(bankTile);
				camera.setAltitude(random(60, 120));
				camera.setRotation(random(158, 347));
				wait(random(1000, 1500));
			}
			if (tile.onScreen(banky.getLocation())) {
				objects.at(banky, "Quickly");
				log("Banking");
			} else {
				camera.turnTo(banky.getLocation());
			}
			wait(random(900, 1000));
			break;
		case WALKIN_TO_BANK:
			walkPathMiniM2(toBank, 10);
			camera.setAltitude(random(40, 152));
			camera.setRotation(random(121, 333));
			return random(1000, 2000);
		case WALKIN_TO_FISHING_AREA:
			walkPathMiniM2(toFish, 10);
			camera.setAltitude(random(69, 169));
			camera.setRotation(random(107, 290));
			return random(1000, 2000);
		case FISH:
			RSNPC spotty = npc.getNearestByID(spot);
			if (spotty == null) {
				return random(500, 1000);
			}
			RSTile sloc = spotty.getLocation();
			if (calculate.distanceTo(sloc) > 50 && !tile.onScreen(sloc)) {
				walk.tileMM(sloc);
				camera.setAltitude(random(15, 190));
				camera.setRotation(random(100, 285));
				wait(random(12000, 15000));
			}
			if (spotty != null && tile.onScreen(sloc)) {
				spotty.action("cage");
				wait(random(12000, 15000));
			} else {
				camera.turnTo(sloc);
			}
			break;
		case WAIT:
			wait(random(12000, 15000));
		}
		return random(300, 400);
	}

	// Original Paint By Weirded Edited By Secret Spy
	public void onRepaint(final Graphics g) {
		if (!game.isLoggedIn() || g == null) {
			return;
		}
		g.setFont(new Font("Tahoma", Font.PLAIN, 10));
		g.setColor(Color.WHITE);
		final Color BG = new Color(0, 0, 0, 75);
		final Color RED = new Color(255, 0, 0, 255);
		final Color GREEN = new Color(0, 255, 0, 255);
		final Color BLACK = new Color(0, 0, 0, 255);
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		if (startExp == 0) {
			startExp = skills.getCurrentXP(Constants.STAT_FISHING);
		}
		if (startLevel == 0) {
			startLevel = skills.getCurrentLvl(Constants.STAT_FISHING);
		}
		runTime = System.currentTimeMillis() - startTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}
		gainedExp = skills.getCurrentXP(Constants.STAT_FISHING)
				- startExp;
		expToLevel = skills.getXPToNextLvl(Constants.STAT_FISHING);
		if ((minutes > 0 || hours > 0 || seconds > 0) && gainedExp > 0) {
			secExp = (float) gainedExp
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
		}
		minuteExp = secExp * 60;
		hourExp = minuteExp * 60;
		if (secExp > 0) {
			secToLevel = (int) (expToLevel / secExp);
		}
		if (secToLevel >= 60) {
			minutesToLevel = secToLevel / 60;
			secToLevel -= minutesToLevel * 60;
		} else {
			minutesToLevel = 0;
		}
		if (minutesToLevel >= 60) {
			hoursToLevel = minutesToLevel / 60;
			minutesToLevel -= hoursToLevel * 60;
		} else {
			hoursToLevel = 0;
		}
		g.setFont(new Font("Tahoma", Font.PLAIN, 10));
		g.setColor(BG);
		g.fill3DRect(5, 200, 156, 137, true);
		g.setColor(BG);
		g.fill3DRect(7, 459, 190, 15, true);
		g.setColor(BLACK);
		g.drawString(scriptInfo.name() + " v" + scriptInfo.version(), 96 + 1,
				469 + 1);
		g.setColor(Color.white);
		g.drawString(scriptInfo.name() + " v" + scriptInfo.version(), 96, 469);
		g.setColor(BLACK);
		g.drawString(scriptInfo.name() + " v" + scriptInfo.version(), 8 + 1,
				211 + 1);
		g.setColor(Color.white);
		g.drawString(scriptInfo.name() + " v" + scriptInfo.version(), 8, 211);
		g.drawString("Running for: " + hours + ":" + minutes + ":" + seconds,
				8, 226);
		g.drawString(
				"Exp Gained: "
						+ gainedExp
						+ " ("
						+ (skills.getCurrentLvl(Constants.STAT_FISHING) - startLevel)
						+ ")", 8, 241);
		g.drawString("Lobsters Caught: " + numCaught, 8, 256);
		g.drawString("Exp per hour: " + (int) hourExp, 8, 271);
		g.drawString("Exp to level: " + expToLevel + " ("
				+ (int) (expToLevel / expPer + 0.5) + " catches)", 8, 286);
		g.drawString("Time to level: " + hoursToLevel + ":" + minutesToLevel
				+ ":" + secToLevel, 8, 301);
		g.drawString("Progress to next level:", 8, 316);
		g.setColor(RED);
		g.fill3DRect(8, 322, 100, 11, true);
		g.setColor(GREEN);
		g.fill3DRect(8, 322,
				skills.getPercentToNextLvl(Constants.STAT_FISHING), 11, true);
		g.setColor(BLACK);
		g.drawString(skills.getPercentToNextLvl(Constants.STAT_FISHING)
				+ "%  to "
				+ (skills.getCurrentLvl(Constants.STAT_FISHING) + 1), 31,
				331);
	}

	private enum State {
		USE_BANK, FISH, WALKIN_TO_FISHING_AREA, WALKIN_TO_BANK, OPEN_BANK, WAIT
	}

	private State getState() {
		if (inventory.isFull()) {
			if (bank.isOpen()) {
				return State.USE_BANK;
			} else if (calculate.distanceTo(bankTile) <= 6 && !player.getMine().isMoving()) {
				return State.OPEN_BANK;
			} else {
				return State.WALKIN_TO_BANK;
			}
		} else {
			if (player.getMine().getAnimation() != -1) {
				return State.WAIT;
			} else if (calculate.distanceTo(fishTile) <= 5 && !player.getMine().isMoving()) {
				return State.FISH;
			} else {
				return State.WALKIN_TO_FISHING_AREA;
			}
		}
	}

	public void AntiBan(int r, final boolean Random) { //
		if (Random) {
			r = random(0, 15);
		}
		if (AntiBanDebug) {
			log("Antiban Case: " + r);
		}
		switch (r) { // Credits for antiban go to Drizzt1112
		case 1:
			camera.setRotation(random(1, 359));
			return;
		case 2:
			camera.setAltitude(random(1.0, 99.0));
			return;
		case 3:
			mouse.move(random(1, 760), random(1, 499));
			return;
		case 4:
			game.openTab(random(0, 12));
			return;
		case 5:
			camera.setRotation(random(1, 359));
			camera.setAltitude(random(1.0, 99.0));
			return;
		case 6:
			int x = input.getX();
			int y = input.getY();
			mouse.move(x + random(-70, 70), y + random(-70, 70));
			x = input.getX();
			y = input.getY();
			mouse.move(x + random(-70, 70), y + random(-70, 70));
			x = input.getX();
			y = input.getY();
			mouse.move(x + random(-70, 70), y + random(-70, 70));
			x = input.getX();
			y = input.getY();
			mouse.move(x + random(-70, 70), y + random(-70, 70));
			wait(random(50, 150));
			return;
		case 7:
			if (game.getCurrentTab() != Game.tabStats) {
				game.openTab(Game.tabStats);
			}
			mouse.click(random(716, 721), random(415, 430), true);
			mouse.move(random(613, 633), random(421, 441));
			wait(random(1000, 2000));
			return;
		case 8:
			final int x2 = input.getX();
			final int y2 = input.getY();
			mouse.move(x2 + random(-80, 80), y2 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 9:
			final int x3 = input.getX();
			final int y3 = input.getY();
			mouse.move(x3 + random(-80, 80), y3 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 10:
			final int x4 = input.getX();
			final int y4 = input.getY();
			mouse.move(x4 + random(-80, 80), y4 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 11:
			final int x5 = input.getX();
			final int y5 = input.getY();
			mouse.move(x5 + random(-80, 80), y5 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 12:
			final int x6 = input.getX();
			final int y6 = input.getY();
			mouse.move(x6 + random(-80, 80), y6 + random(-80, 80));
			wait(random(50, 150));
			return;
		default:
			return;
		}
	}

	public boolean walkPathMiniM2(final RSTile[] path, final int maxDist) {
		AntiBan(random(1, 50), false);
		try {
			final RSTile next = walk.nextTile(path, maxDist);
			if (next != null) {
				walkTileMM2(next);
				wait(random(400, 900));
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
		return true;
	}

	public boolean walkTileMM2(final RSTile t) {
		final Point p = tile.toMiniMap(t);
		if (p.x == -1 || p.y == -1) {
			return false;
		}
		mouse.click(p, 0, 0, true);
		return true;
	}

	public void messageReceived(final MessageEvent e) {
		final String message = e.getMessage().toLowerCase();
		if (message.contains("catch")) {
			numCaught++;
			expgain += 90;
		}
		if (message.contains("You've just advanced")) {
			ScreenshotUtil.takeScreenshot(bot, true);
		}
	}
}
