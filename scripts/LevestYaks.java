import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = { "Levest28, Bobbybighoof" }, category = "Combat", name = "LevestYaks", version = 1.00, description = "<html><body bgcolor = White><font color = Black><h2> Levest Yaks</h2> Version 1.01</h2><br>\n"
		+ "Author: Levest28<br><br>\n"
		+ "Special thanks to Kaka for helping me test and improve this script."
		+ "Start the script with your account logged in at the Yaks<br>"
		+ "Select Yes or No to eat<br>"
		+ "<strong>Eat Food?</strong><br/>"
		+ "<select name='eatsies'><option>Yes<option>No</select><br/>"
		+ "Pick up arrows? "
		+ "<select name=\"Ranging\"><option>None<option>Bronze arrows<option>"
		+ "Iron arrows<option>Steel Arrow<option>Mithril Arrow<option>"
		+ "Adamant Arrow<option>Rune Arrow<option>Bronze Bolt<option"
		+ ">Bluerite Bolt<option>Bone Bolt<option>Iron Bolt<option>"
		+ "Steel Bolt<option>Black Bolt<option>Mithril Bolt<option>"
		+ "Adamant Bolt<option>Rune Bolt<option>Broad Bolt<option>"
		+ "Bronze Knife<option>Iron Knife<option>Steel Knife<option>"
		+ "Black Knife<option>Mithril Knife<option>Adamant Knife<option>"
		+ "Rune Knife<option>Bronze Dart<option>Iron Dart<option>Black Dart<option>"
		+ "Steel Dart<option>Mithril Dart<option>Adamant Dart<option>Rune Dart</select>"
		+ "<br><br><br/>")
public class LevestYaks extends Script implements PaintListener,
		MessageListener {

	private Bot bot;
	final ScriptManifest properties = (ScriptManifest) super.getClass()
			.getAnnotation(ScriptManifest.class);
	private final int[] yakID = { 5529 }, Junk = { 10814, 10816, 10818, 526 };

	int checkTime, Hour, Minute, Second, speed = random(6, 8), startLevel = 0,
			startXP = 0, Action = 0, hpLvl, atkExp, atkLvl, defExp, defLvl,
			hpExp, strExp, rangedExp, strLvl, rangedLvl, strGained, atkGained,
			rgeGained, defGained, hpGained, startAtkExp, startDefExp,
			startStrExp, startRangedExp, startHpExp, yaksKilled, yaksPerHour,
			arrowID = -1, bronzeArrow = 882, ironArrow = 884, steelArrow = 886,
			mithrilArrow = 888, adamantArrow = 890, runeArrow = 892,
			bronzeBolt = 877, boneBolt = 8882, blueriteBolt = 9139,
			ironBolt = 9140, steelBolt = 9141, blackBolt = 13083,
			mithrilBolt = 9142, adamantBolt = 9143, runeBolt = 9144,
			broadBolt = 13280, bronzeKnife = 864, ironKnife = 863,
			steelKnife = 865, blackKnife = 869, mithrilKnife = 866,
			adamantKnife = 867, runeKnife = 868, specialCost = 0,
			lastSpecialValue = 0, bronzeDart = 806, ironDart = 807,
			steelDart = 808, blackDart = 3093, mithrilDart = 809,
			adamantDart = 810, runeDart = 811;
	final int XPChange = skills.getCurrentXP(14) - startXP;
	final int levelChange = skills.getCurrentLvl(14) - startLevel;
	private long startTime = System.currentTimeMillis();
	private long time = System.currentTimeMillis();
	private long hours;
	private long minutes;
	private long seconds;
	RSNPC yak = null;
	private String arrowName;
	private final RSTile yakTile = new RSTile(2324, 3792);
	private boolean wants2Eat;
	private int[] foodID = { 1895, 1893, 1891, 4293, 2142, 291, 2140, 3228,
			9980, 7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568,
			2343, 1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351,
			329, 3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391,
			3369, 3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178,
			7180, 7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011,
			2289, 2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895,
			1897, 1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068,
			1942, 6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989,
			1978, 5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911,
			5745, 2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034,
			2048, 2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225,
			2255, 2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032,
			2074, 2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971,
			4608, 1883, 1885 };
	private int[] arrowPack = { 882, 884, 886, 888, 890, 892, 877, 8882, 9139,
			9140, 9141, 13083, 9142, 9143, 9144, 13280, 864, 863, 865, 869,
			866, 867, 868, 806, 807, 808, 3093, 809, 810, 811 };

	@Override
	protected int getMouseSpeed() {
		return speed;
	}

	private int getHealth() {
		return Integer.parseInt(iface.get(748).getChild(8)
				.getText());
	}

	private boolean pickupArrows(int[] id, String itemName) {
		boolean back = false;
		try {
			RSGroundItem loots = ground.getItemByID(17, id);
			Point toscreen = loots.getScreenLocation();
			if ((loots != null) && (!player.getMine().isMoving())) {
				back = true;
				if (calculate.pointOnScreen(toscreen)) {
					mouse.move(toscreen, 3, 3);
					wait(random(100, 200));
					if ((menu.getItems().length > 1)
							&& (menu.arrayContains(menu.getItems(), itemName))) {
						if (((String) menu.getItems()[0]).contains(itemName)) {
							mouse.click(true);
							wait(random(750, 1000));
						} else {
							mouse.click(false);
							wait(random(500, 750));
							menu.action(itemName);
							wait(random(750, 1000));
						}
					}
				}
			}
		} catch (Exception localException) {
		}
		return back;
	}


	private void handleArrows() {
		if ((arrowID != -1) && (player.getMine().getInteracting() == null)) {

			pickupArrows(arrowPack, "Take " + arrowName);
			wait(random(400, 600));
		}
	}

	private boolean clickInventoryItem(int[] ids, String command) {
		try {
			if ((game.getCurrentTab() != 4)
					&& (!iface.get(762).isValid())
					&& (!iface.get(620).isValid())) {
				game.openTab(4);
			}
			int[] items = inventory.getArray();
			List<Integer> possible = new ArrayList<Integer>();
			for (int i = 0; i < items.length; ++i) {
				for (int item : ids) {
					if (items[i] == item) {
						possible.add(Integer.valueOf(i));
					}
				}
			}
			if (possible.size() == 0) {
				return false;
			}
			int idx = ((Integer) possible.get(random(0, possible.size())))
					.intValue();
			Point t = inventory.getItemPoint(idx);
			mouse.move(t, 5, 5);
			wait(random(100, 290));
			if (menu.action(command)) {
				mouse.click(true);
				return true;
			}

			return menu.action(command);
		} catch (Exception e) {
			log.log(Level.SEVERE, "clickInventoryFood(int...) error: ", e);
		}
		return false;
	}

	private int getAngleToCoord(RSTile loc) {
		int x1 = player.getMine().getLocation().getX();
		int y1 = player.getMine().getLocation().getY();
		int x = x1 - loc.getX();
		int y = y1 - loc.getY();
		double angle = Math.toDegrees(Math.atan2(y, x));
		log("Yak's Angle: " + (int) angle + "�");
		return (int) angle;
	}

	private boolean clickNPC(RSNPC npc, String action) {
		if (npc == null) {
			return false;
		}
		RSTile tile = npc.getLocation();
		if (!tile.isValid()) {
			return false;
		}

		try {
			Point screenLoc = npc.getScreenLocation();
			if (calculate.distanceTo(tile) > 6 || !calculate.pointOnScreen(screenLoc)) {
				camera.turnTo(tile);
			}
			if (calculate.distanceTo(tile) > 20) {
				walk.tileMM(tile);
				return false;
			}
			for (int i = 0; i < 12; i++) {
				screenLoc = npc.getScreenLocation();
				if (!npc.isValid() || !calculate.pointOnScreen(screenLoc)) {
					return false;
				}
				mouse.move(screenLoc, 5, 5);
				if (menu.getItems()[0].toLowerCase()
						.contains(npc.getName().toLowerCase())) {
					break;
				}
				if (mouse.getLocation().equals(screenLoc)) {
					break;
				}
			}
			String[] menuItems = menu.getItems();
			for (int a = 0; a < menuItems.length; a++) {
				if (menuItems[a].toLowerCase()
						.contains(npc.getName().toLowerCase())) {
					if (menuItems[0].toLowerCase()
							.contains(action.toLowerCase())) {
						mouse.click(true);
						return true;
					} else {
						mouse.click(false);
						return menu.action(action);
					}
				}
			}
		} catch (Exception e) {
			System.out.print("clickNPC(RSNPC, String) error: " + e);
			return false;
		}
		return false;
	}

	private int getAction() {
		if (game.isLoggedIn()) {
			if (calculate.distanceTo(yakTile) < 50) {
				return 0;
			}
			return 1;
		}
		return random(100, 300);
	}

	public int loop() {
		int randomNumber = random(1, 3);

		if (randomNumber == 1) {
			antiBan();
		}
		if (randomNumber == 2) {
		}
		if (randomNumber == 3) {
		}

		if ((wants2Eat) && (inventory.getCount(foodID) >= 1)) {
			int CurrHP = getHealth();
			int RealHP = skills.getRealLvl(3) * 10;
			if (CurrHP <= random(RealHP / 2, RealHP / 1.5D)) {

				clickInventoryItem(foodID, "Eat");
			} else if (inventory.getCount(foodID) == 0) {

				wait(random(8000, 9000));
				game.logout();
				stopScript();
			}
		}

		if (inventory.getCount(Junk) >= 1) {

			clickInventoryItem(Junk, "Drop");
			return random(400, 550);
		}

		if (player.getMine().getInteracting() != null) {

			return random(300, 450);
		}

		if (npc.getNearestFreeToAttackByID(yakID) != null) {
			Point nextNPC = npc.getNearestFreeToAttackByID(yakID)
					.getScreenLocation();
			if ((nextNPC != null) && (player.getMine().getInteracting() != null)) {

				mouse.move(nextNPC);
			}
		}

		getMouseSpeed();

		Action = getAction();
		switch (Action) {
		case 0:
			runControl();

			handleArrows();
			RSGroundItem rangeStuff = ground.getNearestItemByID(new int[] { arrowID });
			if (rangeStuff != null) {
				return 100;
			}
			if (inventory.getCount(new int[] { arrowID }) == random(50, 100)) {
				if (game.getCurrentTab() != 4) {
					game.openTab(4);
				}

				inventory.clickItem(arrowID, "Wield");
				return random(15000, 30000);
			}

			RSNPC Yak = npc.getNearestFreeByID(yakID);
			if (Yak != null) {
				if ((Yak.getInteracting() != null)
						&& (player.getMine().getInteracting() == null)) {
					return random(100, 200);
				}
				if ((calculate.pointOnScreen(Yak.getScreenLocation()))
						&& (player.getMine().getInteracting() == null)) {

					clickNPC(Yak, "attack");
					mouse.moveRandomly(random(-4, 4));
					return random(800, 1400);
				}
				int yakAngle = getAngleToCoord(Yak.getLocation());

				antiBan();
				camera.setRotation(yakAngle);
				return random(200, 400);
			}

			return random(500, 1000);
		case 1:
			log.warning("Stopping script get to the Yak Pen on Neitiznot.");
			stopScript();
			return random(100, 200);
		}

		return random(400, 800);
	}

	@Override
	public boolean onStart(Map<String, String> args) {
		if (((String) args.get("eatsies")).equals("Yes")) {
			log("Eating");
			wants2Eat = true;
		} else if (((String) args.get("eatsies")).equals("No")) {
			log("Not Eating");
			wants2Eat = false;
		}

		if (((String) args.get("Ranging")).equals("Bronze arrows")) {
			arrowID = bronzeArrow;
			arrowName = "Bronze arrow";
		} else if (((String) args.get("Ranging")).equals("Iron arrows")) {
			arrowID = ironArrow;
			arrowName = "Iron arrow";
		} else if (((String) args.get("Ranging")).equals("Steel Arrow")) {
			arrowID = steelArrow;
			arrowName = "Steel arrow";
		} else if (((String) args.get("Ranging")).equals("Mithril Arrow")) {
			arrowID = mithrilArrow;
			arrowName = "Mithril arrow";
		} else if (((String) args.get("Ranging")).equals("Adamant Arrow")) {
			arrowID = adamantArrow;
			arrowName = "Adamant arrow";
		} else if (((String) args.get("Ranging")).equals("Rune Arrow")) {
			arrowID = runeArrow;
			arrowName = "Rune arrow";
		} else if (((String) args.get("Ranging")).equals("Bronze Bolt")) {
			arrowID = bronzeBolt;
			arrowName = "Bronze bolts";
		} else if (((String) args.get("Ranging")).equals("Bluerite Bolt")) {
			arrowID = blueriteBolt;
			arrowName = "Bluerite bolts";
		} else if (((String) args.get("Ranging")).equals("Bone Bolt")) {
			arrowID = boneBolt;
			arrowName = "Bone bolts";
		} else if (((String) args.get("Ranging")).equals("Iron Bolt")) {
			arrowID = ironBolt;
			arrowName = "Iron bolts";
		} else if (((String) args.get("Ranging")).equals("Steel Bolt")) {
			arrowID = steelBolt;
			arrowName = "Steel bolts";
		} else if (((String) args.get("Ranging")).equals("Black Bolt")) {
			arrowID = blackBolt;
			arrowName = "Bronze bolts";
		} else if (((String) args.get("Ranging")).equals("Mithril Bolt")) {
			arrowID = mithrilBolt;
			arrowName = "Mithril bolts";
		} else if (((String) args.get("Ranging")).equals("Adamant Bolt")) {
			arrowID = adamantBolt;
			arrowName = "Adamant bolts";
		} else if (((String) args.get("Ranging")).equals("Rune Bolt")) {
			arrowID = runeBolt;
			arrowName = "Rune bolts";
		} else if (((String) args.get("Ranging")).equals("Broad Bolt")) {
			arrowID = broadBolt;
			arrowName = "Broad bolts";
		} else if (((String) args.get("Ranging")).equals("Bronze Knife")) {
			arrowID = bronzeKnife;
			arrowName = "Bronze knife";
		} else if (((String) args.get("Ranging")).equals("Iron Knife")) {
			arrowID = ironKnife;
			arrowName = "Iron knife";
		} else if (((String) args.get("Ranging")).equals("Steel Knife")) {
			arrowID = steelKnife;
			arrowName = "Steel knife";
		} else if (((String) args.get("Ranging")).equals("Black Knife")) {
			arrowID = blackKnife;
			arrowName = "Black knife";
		} else if (((String) args.get("Ranging")).equals("Mithril Knife")) {
			arrowID = mithrilKnife;
			arrowName = "Mithril knife";
		} else if (((String) args.get("Ranging")).equals("Adamant Knife")) {
			arrowID = adamantKnife;
			arrowName = "Adamant knife";
		} else if (((String) args.get("Ranging")).equals("Rune Knife")) {
			arrowID = runeKnife;
			arrowName = "Rune knife";
		} else if (((String) args.get("Ranging")).equals("Bronze Dart")) {
			arrowID = bronzeDart;
			arrowName = "Bronze dart";
		} else if (((String) args.get("Ranging")).equals("Iron Dart")) {
			arrowID = ironDart;
			arrowName = "Iron dart";
		} else if (((String) args.get("Ranging")).equals("Steel Dart")) {
			arrowID = steelDart;
			arrowName = "Steel dart";
		} else if (((String) args.get("Ranging")).equals("Black Dart")) {
			arrowID = blackDart;
			arrowName = "Black dart";
		} else if (((String) args.get("Ranging")).equals("Mithril Dart")) {
			arrowID = mithrilDart;
			arrowName = "Mithril dart";
		} else if (((String) args.get("Ranging")).equals("Adamant Dart")) {
			arrowID = adamantDart;
			arrowName = "Adamant dart";
		} else if (((String) args.get("Ranging")).equals("Rune Dart")) {
			arrowID = runeDart;
			arrowName = "Rune dart";
		}
		if (game.isLoggedIn()) {
			camera.setAltitude(true);
			return true;
		}
		return false;
	}

	private void runControl() {
		if ((!isRunning()) && (player.getMyEnergy() > random(20, 30))) {
			game.setRun(true);
		}
	}

	public void messageReceived(MessageEvent e) {
		String serverString = e.getMessage();

		if (serverString.contains("<col=ffff00>System update in")) {
			stopScript();
		}
		if (serverString.contains("Oh dear, you are dead!")) {

			stopScript();
		}
		if (serverString.contains("Someone else is fighting that")) {

			if (npc.getNearestFreeByID(yakID) != null) {
				walk.to(npc.getNearestFreeByID(yakID).getLocation());
			} else {
				mouse.clickSlightly();
			}
		}
		if (serverString.contains("I can't reach that!")) {
			RSObject Gate = objects.getNearestByID(new int[] { 21600 });
			if ((Gate != null)
					&& (calculate.pointOnScreen(Gate.getLocation().getScreenLocation()))) {
				Gate.action("Open");
			} else if (!calculate.pointOnScreen(Gate.getLocation().getScreenLocation())) {
				walk.to(Gate.getLocation());
				wait(500);
				Gate.action("Open");
			}
		}
		if (serverString.contains("already under attack")) {
			wait(random(2000, 3000));
			yak = null;
		}
		if (serverString.contains("There is no ammo left in your quiver.")) {
			stopScript();
		}

		if (serverString
				.contentEquals("You can't log out until 10 seconds after the end of combat.")) {
			wait(random(10100, 11000));
			stopScript();
		}
		if (serverString.contains("You've just advanced")) {
			ScreenshotUtil.takeScreenshot(bot, true);
			if (iface.canContinue()) {
				iface.clickContinue();
			}
		}
	}

	private final Color color1 = new Color(153, 153, 153, 140);
	private final Color color2 = new Color(0, 0, 0);
	private final Color color3 = new Color(0, 0, 204, 141);

	private final BasicStroke stroke1 = new BasicStroke(1);

	private final Font font1 = new Font("Arial Black", 1, 10);

	public void onRepaint(Graphics g1) {

		atkExp = skills.getCurrentXP(0);
		strExp = skills.getCurrentXP(2);
		defExp = skills.getCurrentXP(1);
		hpExp = skills.getCurrentXP(3);
		rangedExp = skills.getCurrentXP(4);
		int xpGained = atkExp - startAtkExp + (strExp - startStrExp)
				+ (defExp - startDefExp) + (rangedExp - startRangedExp)
				+ (hpExp - startHpExp);
		atkGained = (atkExp - startAtkExp);
		strGained = (strExp - startStrExp);
		defGained = (defExp - startDefExp);
		rgeGained = (rangedExp - startRangedExp);
		hpGained = (hpExp - startHpExp);
		time = (System.currentTimeMillis() - startTime);
		seconds = (time / 1000L);
		if (seconds >= 60L) {
			minutes = (seconds / 60L);
			seconds -= minutes * 60L;
		}
		if (minutes >= 60L) {
			hours = (minutes / 60L);
			minutes -= hours * 60L;
		}
		if (startAtkExp == 0) {
			startAtkExp = skills.getCurrentXP(0);
		}
		if (startStrExp == 0) {
			startStrExp = skills.getCurrentXP(2);
		}
		if (startDefExp == 0) {
			startDefExp = skills.getCurrentXP(1);
		}
		if (startHpExp == 0) {
			startHpExp = skills.getCurrentXP(3);
		}
		if (startRangedExp == 0) {
			startRangedExp = skills.getCurrentXP(4);
		}

		int xpHour = (int) (3600000.0D / time * xpGained);
		float xpSec = 0.0F;
		if ((minutes > 0L) || (hours > 0L)
				|| ((seconds > 0L) && (xpGained > 0))) {
			xpSec = xpGained
					/ (float) (seconds + minutes * 60L + hours * 60L * 60L);
		}
		float xpMin = xpSec * 60.0F;
		float xphour = xpMin * 60.0F;
		yaksKilled = (xpGained / 200);
		yaksPerHour = (xpHour / 200);

		Graphics2D g = (Graphics2D) g1;
		g.setColor(color1);
		g.fillRoundRect(548, 205, 188, 260, 16, 16);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRoundRect(548, 205, 188, 260, 16, 16);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString("Levest Yaks", 556, 225);
		g.drawString("By Levest28", 550, 266);
		g.drawString("Attack EXP Gained: " + atkGained, 550, 285);
		g.drawString("Strength EXP Gained: " + strGained, 550, 304);
		g.drawString("Defence EXP Gained: " + defGained, 550, 323);
		g.drawString("Ranged EXP Gained: " + rgeGained, 550, 342);
		g.drawString("HP EXP Gained: " + hpGained, 550, 361);
		g.drawString("EXP Per Hour: " + Integer.toString((int) xphour), 550,
				380);
		g.drawString("Yaks Killed: " + Integer.toString(yaksKilled), 550, 399);
		g.drawString("www.runedev.info", 575, 456);

		if (hours == 0 && minutes == 30 && seconds == 0) {
			log("Nice! ran for 30 min! taking screenie :|");
			log("Please post your proggy on the site!");
			ScreenshotUtil.takeScreenshot(bot, true);

		} else if (hours == 1 && minutes == 0 && seconds == 0) {
			log("Yay! ran for 1 hours! taking screenie :]");
			log("Please post your proggy on the site!");
			ScreenshotUtil.takeScreenshot(bot, true);

		} else if (hours == 2 && minutes == 0 && seconds == 0) {
			log("w00t! ran for 2 hours! taking screenie :)");
			log("Please post your proggy on the site!");
			ScreenshotUtil.takeScreenshot(bot, true);

		} else if (hours == 5 && minutes == 0 && seconds == 0) {
			log("Hell yeah! ran for 5 hours! taking screenie :[]");
			log("Please post your proggy on the site!");
			ScreenshotUtil.takeScreenshot(bot, true);

		} else if (hours == 10 && minutes == 0 && seconds == 0) {
			log("THE PERFECT PROGGY! ran for 10 hours! taking screenie =D");
			log("Please post your proggy on the site!");
			ScreenshotUtil.takeScreenshot(bot, true);

		}
	}

	@Override
	public void onFinish() {
		long millis = System.currentTimeMillis() - startTime;
		long Hours = millis / 3600000L;
		millis -= Hours * 3600000L;
		long Minutes = millis / 60000L;
		millis -= Minutes * 60000L;
		int xpGained = atkExp - startAtkExp + (strExp - startStrExp)
				+ (defExp - startDefExp) + (rangedExp - startRangedExp)
				+ (hpExp - startHpExp);
		int lvlGained = atkLvl + strLvl + defLvl + rangedLvl + hpLvl;
		ScreenshotUtil.takeScreenshot(bot, true);
		JOptionPane.showMessageDialog(null,
				"Thank you for using Levest Yaks!\n"
						+ "-------------------------------------------\n"
						+ "Script by Levest28.\n" + "You have gained "
						+ xpGained + " experience." + "You have gained"
						+ lvlGained + "Levels.\n" + "Killed " + yaksKilled
						+ " Yaks.\n"
						+ "Script will log you out after 15 seconds.\n"
						+ "-------------------------------------------");

		wait(random(15000, 16000));
		game.logout();
		wait(random(500, 501));
		stopScript();
	}

	public void moveMouse() {

		final int x = random(0, 750);
		final int y = random(0, 500);

		mouse.move(x, y, 10, 10);
		wait(random(800, 1000));
	}

	public void moveCamera() {
		int angle = camera.getAngle() + random(-90, 90);
		if (angle < 0) {
			angle = 0;
		}
		if (angle > 359) {
			angle = 0;
		}

		camera.setRotation(angle);
	}

	// ANTIBAN
	public boolean antiBan() {
		int randomNumber = random(1, 24);
		if (randomNumber <= 15) {
			if (randomNumber == 1) {
				randomHoverPlayer();
			}
			if (randomNumber == 2) {
				mouse.move(random(50, 700), random(50, 450), 2, 2);
				wait(random(1000, 1500));
				mouse.move(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 3) {
				openRandomTab();
				wait(random(100, 500));
				mouse.move(522, 188, 220, 360);
				wait(random(500, 2800));
			}
			if (randomNumber == 4) {
				wait(random(100, 200));
				mouse.move(random(50, 700), random(50, 450), 2, 2);
				camera.setRotation(random(1, 360));
				mouse.move(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 6) {
				mouse.move(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 7) {
				mouse.move(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 8) {
				wait(random(100, 200));
				mouse.move(random(50, 700), random(50, 450), 2, 2);
				wait(random(200, 500));
				if (randomNumber == 9) {
					wait(random(100, 200));
					mouse.move(random(50, 700), random(50, 450), 2, 2);
					if (randomNumber == 10) {
						mouse.move(random(50, 700), random(50, 450), 2, 2);
					}
					if (randomNumber == 11) {
						camera.setRotation(random(1, 360));
						mouse.move(random(50, 700), random(50, 450), 2, 2);
					}
					if (randomNumber == 12) {
						game.openTab(Game.tabStats);
						wait(random(50, 100));
						mouse.move(675, 268, 20, 20);
						wait(random(500, 1700));
					}
					if (randomNumber == 13) {
						mouse.move(random(50, 700), random(50, 450), 2, 2);
						camera.setRotation(random(1, 360));
					}
					if (randomNumber == 14) {
						game.openTab(Game.tabStats);
						wait(random(50, 100));
						mouse.move(675, 268, 20, 20);
						wait(random(500, 1700));
					}
					if (randomNumber == 15) {
						randomHoverPlayer();
					}
				}

			}
		}
		return true;
	}

	private void randomHoverPlayer() {
		int randomNumber = random(1, 10);
		if (randomNumber <= 10) {
			if (randomNumber == 1) {
				RSPlayer p = player.getNearestByLevel(1, 130);
				if ((p != null) && tile.onScreen(p.getLocation())) {
					mouse.move(p.getScreenLocation(), 40, 40);
					wait(random(450, 650));
				}
				if (randomNumber == 2) {
					if ((p != null) && tile.onScreen(p.getLocation())) {
						mouse.move(p.getScreenLocation(), 40, 40);
						wait(random(100, 400));
						mouse.click(false);
						wait(random(1000, 1700));
						mouse.move(random(50, 700), random(50, 450), 2, 2);
					}
				}
			}
		}
	}

	private void openRandomTab() {
		int randomNumber = random(1, 15);
		if (randomNumber <= 11) {
			if (randomNumber == 1) {
				game.openTab(Game.tabStats);
				wait(random(100, 200));
				mouse.move(675, 268, 20, 20);
				wait(random(500, 1700));
			}
			if (randomNumber == 2) {
				game.openTab(Game.tabAttack);
			}
			if (randomNumber == 3) {
				game.openTab(Game.tabEquipment);
			}
			if (randomNumber == 4) {
				game.openTab(Game.tabFriends);
			}
			if (randomNumber == 6) {
				game.openTab(Game.tabMagic);
			}
			if (randomNumber == 7) {
				game.openTab(Game.tabStats);
			}
			if (randomNumber == 8) {
				game.openTab(Game.tabQuests);
			}
			if (randomNumber == 9) {
				game.openTab(Game.tabClan);
			}
			if (randomNumber == 10) {
				game.openTab(Game.tabMusic);
			}
			if (randomNumber == 11) {
				game.openTab(Game.tabTasks);
			}
		}
	}
}
