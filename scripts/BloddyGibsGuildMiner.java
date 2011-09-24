import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.lazygamerz.scripting.api.GE;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = { "Bloddyharry", "Gribonn" }, name = "Bloddy Gribonn's Guild Miner", category = "Mining", version = 1.9, description = "<html>\n"
		+ "<body style='font-family: Calibri; background-color: black; color:white; padding: 0px; text-align; center;'>"
		+ "<h2>"
		+ "Bloddy Gribonn's Guild Miner 1.9"
		+ "</h2>\n"
		+ "<b>Made by Bloddyharry & Gribonn53</b>\n"
		+ "<br><br>\n"
		+ "Options in GUI! >:D")
public class BloddyGibsGuildMiner extends Script implements PaintListener,
		MessageListener {

	private Bot bot;
	final ScriptManifest info = getClass().getAnnotation(ScriptManifest.class);
	final GE grandExchange = new GE();
	public String stopReason;
	public int coalID = 453;
	public int mithID = 447;
	public int[] coalRockID = { 5770, 5771, 5772 };
	public int[] mithRockID = { 5784, 5786, 5785 };
	public int[] pickAxeID = { 1265, 1267, 1269, 1273, 1271, 1275, 15259, 15261 };
	public String status = "";
	public int mithsMined, coalsMined;
	public int ladderUpID = 2113;
	public int ladderDownID = 6226;
	public int usID = 1623, ueID = 1621, urID = 1619, udID = 1617, uyID = 1627;
	public int page = 1;
	public int startexp;
	public int gainedLvls;
	public int profit;
	public int gemsGot = 0;
	public int bankerID = 6200;
	public int wantedHours, wantedMinutes, wantedSeconds, wantedLevel,
			wantedMiths, wantedCoals;
	public int[] pickaxeID = { 1265, 1267, 1269, 1273, 1271, 1275, 15259, 15261 };
	public boolean chatResponder;
	public boolean maySayHi = true;
	public boolean guiWait = true, guiExit;
	public BloddyGibsGuildMinerGUI gui;
	public int bBoothID = 11758;
	public int coalCost = ge.loadItemInfo(coalID).getPrice();
	public int mithCost = ge.loadItemInfo(mithID).getPrice();
	public boolean paint = true;
	public boolean showPaint;
	public boolean mineMiths, mineCoals;
	public boolean showMiningInfo = false;
	public boolean letTurnPaint;
	public boolean logOut = false;
	public boolean dropGems;
	public boolean mithATM = false;
	public boolean powermine;
	public boolean finishAt, finishWhenLevel, finishWhenCoalsMined,
			finishWhenMithsMined, finishAmount;
	public boolean us, ue, ur, ud, uy, dropGems2;
	public boolean stoppedCoals, stoppedMiths;
	public int timesResponded = 0;
	public BufferedImage normal = null;
	public BufferedImage clicked = null;
	public RSTile bankTile = new RSTile(3014, 3356);
	public RSTile coalTile = new RSTile(3040, 9738);
	public RSTile coalTile2 = new RSTile(3026, 9738);
	public RSTile mithTile = new RSTile(3049, 9737);
	public RSTile ladderTile = new RSTile(3020, 3340);
	public RSTile ladderTileDown = new RSTile(3022, 9739);
	public RSTile[] bankToLadder = { new RSTile(3015, 3355),
			new RSTile(3023, 3351), new RSTile(3031, 3344),
			new RSTile(3020, 3338) };
	public RSTile[] ladderToBank = walk.reversePath(bankToLadder);
	public RSArea Guild = new RSArea(new RSTile(3056, 9756), new RSTile(3025,
			9732));
	public RSTile[] toBank = { new RSTile(3020, 3340), new RSTile(3024, 3351),
			new RSTile(3014, 3356) };
	public RSTile[] ladderTiles = { new RSTile(3019, 3338),
			new RSTile(3018, 3339), new RSTile(3019, 3341),
			new RSTile(3020, 3339) };
	public RSTile[] ladderTiles2 = { new RSTile(3019, 9738),
			new RSTile(3018, 9739), new RSTile(3019, 9741),
			new RSTile(3020, 9739) };
	public long startTime = System.currentTimeMillis();
	public RSObject coalRock = null;
	public RSObject mithRock = null;
	public int timer = 0;
	public String[] attNames = { "attack", "att", "atk", "atting", "atking",
			"attacking", "atkin", "attin", "attackin" };
	public String[] attNamesInAnswer = { "attack", "att", "atk" };
	public String[] defNames = { "defense", "defence", "block", "def", "deff",
			"defenc" };
	public String[] defNamesInAnswer = { "defence", "def", "deff" };
	public String[] strNames = { "strength", "strentgh", "strenhtg",
			"strenght", "stre", "str" };
	public String[] strNamesInAnswer = { "strength", "stre", "str" };
	public String[] hpNames = { "hp", "hitpoints", "constitution",
			"contsitution", "constiution", "health", "healt", "life",
			"lifepoints", "lp", "<3" };
	public String[] hpNamesInAnswer = { "hp", "constitution", "health" };
	public String[] rangedNames = { "ranged", "range", "rang", "rng", "rnged",
			"ranging", "rnging", "rangin", "rangeing", "rngin" };
	public String[] rangedNamesInAnswer = { "ranged", "range", "rang", "rng",
			"rnged", "ranging", "rnging", "rangin", "rngin" };
	public String[] prayerNames = { "prayer", "pray", "pry", "praying" };
	public String[] magicNames = { "magic", "mage", "mgic", "maging" };
	public String[] magicNamesInAnswer = { "magic", "mage" };
	public String[] cookingNames = { "cook", "cooking", "cookin" };
	public String[] woodcuttingNames = { "wcing", "wc", "woodcutting",
			"woodcuttin", "wcin", "chopping", "choppin", "woodcut" };
	public String[] woodcuttingNamesInAnswer = { "wcing", "wc", "woodcutting",
			"woodcuttin", "wcin", "woodcut" };
	public String[] fletchingNames = { "flech", "fletch", "fletchin",
			"fleching", "fletching", "flcin", "flching" };
	public String[] fletchingNamesInAnswer = { "fletch", "fletchin",
			"fletching", "flcin", "fltching" };
	public String[] fishingNames = { "<><", "fishin", "fishing", "<><ing",
			"<><-ing", "><>", "fish", "fshing", "><>-ing", "><>ing" };
	public String[] fishingNamesInAnswer = { "<><", "fishin", "fishing",
			"<><ing", "<><-ing", "><>", "fish", "><>-ing", "><>ing" };
	public String[] firemakingNames = { "fm", "fming", "fmin", "firemaking",
			"fmaking", "fmakin", "firemakin", "fireming", "firem" };
	public String[] firemakingNamesInAnswer = { "fm", "fming", "firemaking",
			"firemakin" };
	public String[] craftingNames = { "crafting", "craftin", "craft" };
	public String[] smithingNames = { "smithing", "smelting", "smith", "smelt",
			"smithin", "smeltin" };
	public String[] smithingNamesInAnswer = { "smithing", "smith", "smithin" };
	public String[] miningNames = { "mining", "minin" };
	public String[] herbloreNames = { "herblore", "herby", "herblaw" };
	public String[] agilityNames = { "agility", "agil", "agilit" };
	public String[] thievingNames = { "thieving", "thief", "thievin",
			"stealing", "stoling" };
	public String[] slayerNames = { "slay", "slayer", "slaying", "slayin" };
	public String[] farmingNames = { "farming", "frming", "farmin", "growing",
			"frmin", "growin", "farm", "grow" };
	public String[] farmingNamesInAnswer = { "farming", "frming", "farmin",
			"frmin", "farm" };
	public String[] runecraftingNames = { "runecrafting", "rcing", "rc",
			"runecraftin", "rcin", "runemaking", "runemakin" };
	public String[] runecraftingNamesInAnswer = { "runecrafting", "rcing",
			"rc", "runecraftin", "rcin" };
	public String[] hunterNames = { "hunter", "hunting", "huntin", "hunt" };
	public String[] constructionNames = { "construction", "constructing",
			"building", "buildin", "constructin", "con", "cons" };
	public String[] constructionNamesInAnswer = { "construction",
			"constructing", "constructin", "con", "cons" };
	public String[] summoningNames = { "summoning", "summon", "summonin" };
	public String[] dungeoneeringNames = { "dungeoneering", "dungoneering",
			"dungeon", "dung", "dungeoneerin", "dungoneerin" };
	public String[] dungeoneeringNamesInAnswer = { "dungeoneering", "dungeon",
			"dung", "dungeoneerin" };
	public String[] levelNames = { "lvl", "level", "lvel", "levl",
			"skilllevel", "skilllvl", "levvel", "lewel", "lwl", "lwel", "lewl" };
	public String[] levelNamesInAnswer = { "", "lvl", "level" };
	public String[] beforeSay = { "", "whatisyour", "whatsyour", "whatsur",
			"watsur", "whatisur", "watsyour", "ur", "your", "yar", "watsyar",
			"watisyar", "whatsyar", "whatisyar", "wutsur", "wutsyar",
			"wutsyour", "whatchur" };
	public String[] beforeSayInAnswer = { "", "Mines", "Mine", "My", "Me" };
	public String[] greetings = { "hi", "hello", "hey", "heya", "heyy",
			"heyyy", "greeting", "greetings", "greets", "morning", "evening",
			"night", "goodnight", "goodmorning", "goodafternoon", "afternoon" };
	public String[] greetingsInAnswer = { "hi", "hello", "hey", "heya", "heyy",
			"heyyy", "welcome", "yo" };
	public String[] persons = { "", "ppl", "people", "guy", "person",
			"fellows", "fellas", "youngfellas", "" };
	public String[] byes = { "bye", "byebye", "cya", "seeyou", "seeya",
			"goodbye", "gtg", "gottogo", "g2g", "got2go", "gtgcya",
			"got2gocya", "g2gcya" };
	public String[] byesInAnswer = { "bye", "bye bye", "cya", "see you",
			"see ya", "good bye" };
	public String[] qpNames = { "qp", "questpoints" };
	public String[] questpointsInAnswer = { "i have qpam qpna",
			"my qpna amount is qpam", "i have got qpam qpna",
			"i has got qpam qpna", "i has qpam qpna" };
	public String[] areyou = { "", "areyou", "areu", "u", "areya", "arey" };
	public String[] bottingNames = { "botting", "boting", "bottin", "botin",
			"botter", "boter", "autobotin", "autoboting", "autoer",
			"autobotter", "autobottin", "autobotting" };
	public String[] ends = { "!", ".", "", "", "", "", "", "", "", "'", "" };
	public String[] sups = { "sup", "wazzup", "wazup", "whatsup", "whatisup",
			"watsup", "whatareyoudoing" };
	public String[] supsInAnswer = { "nothing", "nothin", "nothing much", "nm",
			"nothing really" };
	public String[] lols = { "", "lol, ", "lmao, ", "lmfao, ", "", "", "" };
	public String[] nos = { "no", "noo", "nope", "nopee" };
	public String[] whys = { "", "", "", "", ", why?", ", afking", "" };
	public String lastMsg = "";
	public String qpamount = "";
	public boolean checkChat = false;
	public int i = 0, FWM = 0;
	public String[] previousMsgsBefoeDelete = { "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "" };
	public String[] previousMsgs = { "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "" };
	public int[] timers = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	protected int getMouseSpeed() {
		return random(3, 5);
	}

	public boolean onStart(Map<String, String> args) {
		gui = new BloddyGibsGuildMinerGUI();
		gui.setVisible(true);
		while (guiWait) {
			wait(100);
		}
		startTime = System.currentTimeMillis();
		startexp = skills.getCurrentXP(Constants.STAT_MINING);
		return !guiExit;
	}

	static final String[] browsers = { "google-chrome", "firefox", "opera",
			"konqueror", "epiphany", "seamonkey", "galeon", "kazehakase",
			"mozilla" };
	static final String errMsg = "Error attempting to launch web browser";

	public static void openURL(String url) {
		try {
			Class<?> d = Class.forName("java.awt.Desktop");
			d.getDeclaredMethod("browse", new Class[] { java.net.URI.class })
					.invoke(d.getDeclaredMethod("getDesktop").invoke(null),
							new Object[] { java.net.URI.create(url) });
		} catch (Exception ignore) {
			String osName = System.getProperty("os.name");
			try {
				if (osName.startsWith("Mac OS")) {
					Class.forName("com.apple.eio.FileManager")
							.getDeclaredMethod("openURL",
									new Class[] { String.class }).invoke(null,
									new Object[] { url });
				} else if (osName.startsWith("Windows"))
					Runtime.getRuntime().exec(
							"rundll32 url.dll,FileProtocolHandler " + url);
				else {
					boolean found = false;
					for (String browser : browsers)
						if (!found) {
							found = Runtime.getRuntime().exec(
									new String[] { "which", browser })
									.waitFor() == 0;
							if (found)
								Runtime.getRuntime().exec(
										new String[] { browser, url });
						}
					if (!found)
						throw new Exception(Arrays.toString(browsers));
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, errMsg + "\n"
						+ e.toString());
			}
		}
	}

	private class antiban1 implements Runnable {

		@Override
		public void run() {
			int randomNumber = random(1, 10);
			if (randomNumber == 1) {
				camera.setRotation(random(1, 360));
			}
		}
	}

	private void chatResponder() {
		try {
			String[] lastMessage;
			for (int neb = 0; neb < FWM; neb++) {
				if (!previousMsgsBefoeDelete[neb].equals("")
						&& !previousMsgsBefoeDelete[neb].equals(".,:,.")) {
					lastMessage = previousMsgsBefoeDelete[neb].split(".,:,.");
					final String originalMsg = lastMessage[1];
					final String user = lastMessage[0];
					final String msg = originalMsg.replace(" ", "").replace(
							"?", "").replace(".", "").replace("!", ".")
							.replace("'", "").replace(",", "").toLowerCase();
					String skillname = "";
					String greet = "";
					String type = "";
					String skillnameReal = "";
					String answer = "";
					if (!player.getMine().getName().equalsIgnoreCase(user)) {
						if (!user.equals("") && !msg.equals("")) {
							for (String say : beforeSay) {
								for (String level : levelNames) {
									for (String name : attNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = attNamesInAnswer[random(
													0,
													attNamesInAnswer.length - 1)];
											skillnameReal = "ATTACK";
										}
									}
									for (String name : defNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = defNamesInAnswer[random(
													0,
													defNamesInAnswer.length - 1)];
											skillnameReal = "DEFENCE";
										}
									}
									for (String name : strNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = strNamesInAnswer[random(
													0,
													strNamesInAnswer.length - 1)];
											skillnameReal = "STRENGTH";
										}
									}
									for (String name : hpNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = hpNamesInAnswer[random(
													0,
													hpNamesInAnswer.length - 1)];
											skillnameReal = "HITPOINTS";
										}
									}
									for (String name : rangedNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = rangedNamesInAnswer[random(
													0,
													rangedNamesInAnswer.length - 1)];
											skillnameReal = "RANGED";
										}
									}
									for (String name : prayerNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = prayerNames[random(0,
													prayerNames.length - 1)];
											skillnameReal = "PRAYER";
										}
									}
									for (String name : magicNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = magicNamesInAnswer[random(
													0,
													magicNamesInAnswer.length - 1)];
											skillnameReal = "MAGIC";
										}
									}
									for (String name : cookingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = cookingNames[random(0,
													cookingNames.length - 1)];
											skillnameReal = "COOKING";
										}
									}
									for (String name : woodcuttingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = woodcuttingNamesInAnswer[random(
													0,
													woodcuttingNamesInAnswer.length - 1)];
											skillnameReal = "WOODCUTTING";
										}
									}
									for (String name : fletchingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = fletchingNamesInAnswer[random(
													0,
													fletchingNamesInAnswer.length - 1)];
											skillnameReal = "FLETCHING";
										}
									}
									for (String name : fishingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = fishingNamesInAnswer[random(
													0,
													fishingNamesInAnswer.length - 1)];
											skillnameReal = "FISHING";
										}
									}
									for (String name : firemakingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = firemakingNamesInAnswer[random(
													0,
													firemakingNamesInAnswer.length - 1)];
											skillnameReal = "FIREMAKING";
										}
									}
									for (String name : craftingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = craftingNames[random(0,
													craftingNames.length - 1)];
											skillnameReal = "CRAFTING";
										}
									}
									for (String name : smithingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = smithingNamesInAnswer[random(
													0,
													smithingNamesInAnswer.length - 1)];
											skillnameReal = "SMITHING";
										}
									}
									for (String name : miningNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = miningNames[random(0,
													miningNames.length - 1)];
											skillnameReal = "MINING";
										}
									}
									for (String name : herbloreNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = herbloreNames[random(0,
													herbloreNames.length - 1)];
											skillnameReal = "HERBLORE";
										}
									}
									for (String name : agilityNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = agilityNames[random(0,
													agilityNames.length - 1)];
											skillnameReal = "AGILITY";
										}
									}
									for (String name : thievingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = thievingNames[random(0,
													thievingNames.length - 1)];
											skillnameReal = "THIEVING";
										}
									}
									for (String name : slayerNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = slayerNames[random(0,
													slayerNames.length - 1)];
											skillnameReal = "SLAYER";
										}
									}
									for (String name : farmingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = farmingNamesInAnswer[random(
													0,
													farmingNamesInAnswer.length - 1)];
											skillnameReal = "FARMING";
										}
									}
									for (String name : runecraftingNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = runecraftingNamesInAnswer[random(
													0,
													runecraftingNamesInAnswer.length - 1)];
											skillnameReal = "RUNECRAFTING";
										}
									}
									for (String name : hunterNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = hunterNames[random(0,
													hunterNames.length - 1)];
											skillnameReal = "HUNTER";
										}
									}
									for (String name : constructionNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = constructionNamesInAnswer[random(
													0,
													constructionNamesInAnswer.length - 1)];
											skillnameReal = "CONSTRUCTION";
										}
									}
									for (String name : summoningNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = summoningNames[random(
													0,
													summoningNames.length - 1)];
											skillnameReal = "SUMMONING";
										}
									}
									for (String name : dungeoneeringNames) {
										if (msg.equals(say + name + level)
												|| msg.equals(say + level
														+ "in" + name)
												|| msg.equals(name + level
														+ "s")
												|| msg.equals(name + level
														+ "z")
												|| (msg.equals(say + name) && !say
														.equals(""))) {
											skillname = dungeoneeringNamesInAnswer[random(
													0,
													dungeoneeringNamesInAnswer.length - 1)];
											skillnameReal = "DUNGEONEERING";
										}
									}
								}
							}
							for (String person : persons) {
								for (String greeting : greetings) {
									if (msg.equals(greeting + person)
											|| msg.equals(greeting + person
													+ "s")
											|| msg.equals(greeting + person
													+ "z")) {
										greet = greetingsInAnswer[random(0,
												greetingsInAnswer.length - 1)];
										type = "hi";
									}
								}
								for (String bye : byes) {
									if (msg.equals(bye + person)
											|| msg.equals(bye + person + "s")
											|| msg.equals(bye + person + "z")) {
										greet = byesInAnswer[random(0,
												byesInAnswer.length - 1)];
										type = "bye";
									}
								}
								for (String sup : sups) {
									if (msg.equals(sup + person)
											|| msg.equals(sup + person + "s")
											|| msg.equals(sup + person + "z")) {
										greet = supsInAnswer[random(0,
												supsInAnswer.length - 1)];
										type = "sup";
									}
								}
							}
							for (String are : areyou) {
								for (String botting : bottingNames) {
									if (msg.equals(are + botting)) {
										greet = lols[random(0, lols.length)]
												+ nos[random(0, nos.length)]
												+ whys[random(0, whys.length)];
										type = "bot";
									}
								}
							}
							if (msg.contains("questpoints")) {
								greet = questpointsInAnswer[random(0,
										questpointsInAnswer.length - 1)];
								type = "questpoints";
							}
							if (!skillname.equals("")
									&& !skillnameReal.equals("")
									&& TimersDontHave(skillnameReal + "lvl")) {
								wait(random(500, 900));
								String my = beforeSayInAnswer[random(0,
										beforeSayInAnswer.length - 1)];
								String level = levelNamesInAnswer[random(0,
										levelNamesInAnswer.length - 1)];
								int stat = skills.getRealLvl(Skills
										.getStatIndex(skillnameReal));
								if (random(1, 4) == 3) {
									answer = stat + "";
								} else {
									answer = (my + " " + skillname + " "
											+ level + " is " + stat).replace(
											"  ", " ");
								}
								String end = ends[random(0, ends.length - 1)];
								keyboard.sendText(answer + end, true);
								addTimer(skillnameReal + "lvl");
								log("Chat responder answered to: "
										+ originalMsg);
								log("With: " + answer + end);
							} else if (!greet.equals("")
									&& ((type.equals("hi") && TimersDontHave("hi"))
											|| (type.equals("bye") && TimersDontHave("bye"))
											|| (type.equals("sup") && TimersDontHave("sup"))
											|| (type.equals("questpoints") && TimersDontHave("questpoints")) || (type
											.equals("bot") && TimersDontHave("bot")))) {
								wait(random(500, 900));
								String end = ends[random(0, ends.length - 1)];
								if (type.equals("questpoints")) {
									if (qpamount.equals("")
											|| random(0, 5) == 0) {
										game.openTab(TAB_QUESTS);
										wait(random(300, 500));
										qpamount = (iface.get(
												190).getChild(2).getText()
												.replace("Quest Points:", "")
												.replace(" ", "").split("/"))[0];
										if (random(0, 2) == 0) {
											game.openTab(TAB_INVENTORY);
											wait(random(300, 500));
										}
									}
									keyboard.sendText(
											greet
													.replace("qpam", qpamount)
													.replace(
															"qpna",
															qpNames[random(
																	0,
																	qpNames.length - 1)])
													+ end, true);
									log("Chat responder answered to: "
											+ originalMsg);
									log("With: "
											+ greet
													.replace("qpam", qpamount)
													.replace(
															"qpna",
															qpNames[random(
																	0,
																	qpNames.length - 1)])
											+ end);

								} else {
									keyboard.sendText(greet + end, true);
									log("Chat responder answered to: "
											+ originalMsg);
									log("With: " + greet + end);
								}
								addTimer(type);
								log("Chat responder answered to: "
										+ originalMsg);
								log("With: " + greet);
							}
							lastMsg = originalMsg;
						}
					}
					previousMsgsBefoeDelete[neb] = "";
				}
			}
			FWM = 0;
			checkChat = false;
		} catch (Exception e) {
			checkChat = true;
		}
	}

	public void onFinish() {
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		int gainedXP = skills.getCurrentXP(Constants.STAT_MINING)
				- startexp;
		JOptionPane.showMessageDialog(null,
				"Thank You For Using Bloddy Gribonn's Guild Miner!\n"
						+ "-------------------------------------------\n"
						+ "Ran for "
						+ hours
						+ ":"
						+ minutes
						+ ":"
						+ seconds
						+ "\n"
						+ "Mined "
						+ coalsMined
						+ " Coals\n"
						+ "Mined "
						+ mithsMined
						+ " Miths\n"
						+ "Gained "
						+ gainedLvls
						+ " levels\n"
						+ "Your level is "
						+ skills.getCurrentLvl(Constants.STAT_MINING)
						+ "\n"
						+ "Gained "
						+ gainedXP
						+ "XP\n"
						+ "-------------------------------------------");

	}

	public void clickLadder(RSObject o, String direction) {
		while (calculate.distanceTo(o) >= 4) {
			if (player.isIdle()) {
				walk.tileMM(o.getLocation());
				wait(random(500, 700));
			}
		}
		Point oPoint = o.getClickableModelPoint();
		int sum = 0;
		int sum2 = 0;
		while (oPoint == null && sum2 <= 10) {
			wait(500);
			sum2++;
		}
		while (o != null && !menu.action("Climb-" + direction + " Ladder")
				&& sum < 20) {
			oPoint = o.getClickableModelPoint();
			int oPointX = random(oPoint.x - 5, oPoint.x + 5);
			int oPointY = random(oPoint.y - 5, oPoint.y + 5);
			Point oPointRandomized = new Point(oPointX, oPointY);
			mouse.move(oPointRandomized);
			sum++;
		}
	}

	@Override
	public int loop() {
		if (chatResponder && checkChat) {
			chatResponder();
		}
		if (logOut == false && dropGems == false) {
			camera.setAltitude(true);
			checkInventory();
			checkRun();
			if (player.getMine().getAnimation() == 624) {
				status = "Mining";
				final RSObject mith = objects.getTopAt(mithRock.getLocation());
				final RSObject coal = objects.getTopAt(coalRock.getLocation());
				if (mithRock != null && mith != null
						&& mith.getID() != mithRockID[0]
						&& mith.getID() != mithRockID[1]
						&& mith.getID() != mithRockID[2]
						&& mithATM) {
					mithRock = objects.getNearestByID(mithRockID);
					if (isRockInGuild(mithRock)) {
						if (calculate.distanceTo(mithRock) <= 4) {
							startMineOre(mithRock);
							mithATM = true;
						} else {
							walk.tileMM(mithRock.getLocation());
						}
						wait(random(500, 700));
					} else {
						if (isRockInGuild(coalRock)) {
							if (calculate.distanceTo(coalRock) <= 4) {
								startMineOre(coalRock);
								mithATM = false;
							} else {
								walk.tileMM(coalRock.getLocation());
							}
							wait(random(500, 700));
						}
					}
				} else if (coalRock != null && coal != null
						&& coal.getID() != coalRockID[0]
						&& coal.getID() != coalRockID[1]
						&& coal.getID() != coalRockID[2]
						&& !mithATM) {
					coalRock = objects.getNearestByID(coalRockID);
					if (isRockInGuild(coalRock)) {
						if (calculate.distanceTo(coalRock) <= 4) {
							startMineOre(coalRock);
							mithATM = false;
						} else {
							walk.tileMM(coalRock.getLocation());
						}
						wait(random(500, 700));
					}
				}
				antiBan();
			} else if (atGuild() && !inventory.isFull()) {
				Mine();
			} else if (!inventory.isFull()) {
				checkLadderUp();
				checkLadderDown();
				checkElse();
			}
			antiBan();
		} else if (logOut) {
			logOut();
		} else if (dropGems) {
			dropGems();
		}
		return 0;
	}

	public int checkElse() {
		final RSObject coalRock = objects.getNearestByID(coalRockID);
		if (!atLadderUp() && !atLadderDown() && coalRock == null && !atGuild()) {
			status = "Walking To Rocks";
			walk.pathMM(walk.randomizePath(ladderToBank, 2, 2));
		}
		return 0;
	}

	public void dropGems() {
		if (us && inventory.getCount(usID) > 0) {
			inventory.clickItem(usID, "drop");
		} else if (ue && inventory.getCount(ueID) > 0) {
			inventory.clickItem(ueID, "drop");
		} else if (ur && inventory.getCount(urID) > 0) {
			inventory.clickItem(urID, "drop");
		} else if (ud && inventory.getCount(udID) > 0) {
			inventory.clickItem(udID, "drop");
		} else if (uy && inventory.getCount(uyID) > 0) {
			inventory.clickItem(uyID, "drop");
		} else {
			dropGems = false;
		}
	}

	public int checkLadderUp() {
		final RSObject ladderUp = objects.getNearestByID(ladderUpID);
		if (atLadderUp() && ladderUp != null) {
			status = "Walking To Rocks";
			wait(random(300, 400));
			if (ladderUp != null) {
				clickLadder(ladderUp, "down");
			}
		}
		return 0;
	}

	public int checkLadderDown() {
		final RSObject ladderDown = objects.getNearestByID(ladderDownID);
		coalRock = objects.getNearestByID(coalRockID);
		mithRock = objects.getNearestByID(mithRockID);
		if (atLadderDown() && ladderDown != null) {
			status = "Walking To Rocks";
			if (mithRock != null && isRockInGuild(mithRock) && mineMiths) {
				walk.tileMM(new RSTile(3045, 9734), 4, 4);
			} else if (coalRock != null && isRockInGuild(coalRock) && mineCoals) {
				walk.tileMM(coalRock.getLocation());
			}
		}
		antiBan();
		return random(1000, 1100);
	}

	public int checkInventory() {
		if (inventory.isFull()) {
			final RSObject ladderDown = objects.getNearestByID(ladderDownID);
			if (atBank()) {
				bankOre();
			} else if (!atBank() && !onGround() && !atLadderDown()) {
				status = "Walking To Bank";
				walk.tileMM(ladderTileDown);
			} else if (atLadderDown() && ladderDown != null) {
				status = "Walking To Bank";
				clickLadder(ladderDown, "up");
			} else if (onGround()) {
				status = "Walking To Bank";
				if (player.getMine().getAnimation() == -1) {
					walk.pathMM(walk.randomizePath(ladderToBank, 2, 2));
				}
			}
		}
		return random(300, 600);
	}

	public int Mine() {
		coalRock = objects.getNearestByID(coalRockID);
		mithRock = objects.getNearestByID(mithRockID);
		if (powermine) {
			inventory.dropAllExcept(pickAxeID);
		}
		if (mithRock != null && player.getMine().getAnimation() == -1
				&& isRockInGuild(mithRock) && mineMiths == true) {
			if (player.getMine().getAnimation() == -1) {
				if (calculate.distanceTo(mithRock) <= 2 && player.getMine().isMoving()) {
					return random(100, 300);
				}
				if (tileFullyOnScreen(mithRock.getLocation()) && player.isIdle()) {
					startMineOre(mithRock);
					mithATM = true;
				} else if (!tileFullyOnScreen(mithRock.getLocation())
						&& player.isIdle()) {
					walk.tileMM(mithRock.getLocation());
				}
				antiBan();
			}
		} else if (coalRock != null && player.getMine().getAnimation() == -1
				&& isRockInGuild(coalRock) && mineCoals == true) {
			if (player.getMine().getAnimation() == -1) {
				if (calculate.distanceTo(coalRock) <= 2 && player.getMine().isMoving()) {
					return random(500, 600);
				}
				if (tileFullyOnScreen(coalRock.getLocation()) && player.isIdle()) {
					startMineOre(coalRock);
					mithATM = false;
				} else if (!tileFullyOnScreen(coalRock.getLocation())
						&& player.isIdle()) {
					walk.tileMM(coalRock.getLocation());
				}
				antiBan();
			}
		}
		return 0;
	}

	public void waitToStop() {
		while (player.getMine().isMoving()) {
			wait(500);
		}
	}

	public void checkRun() {
		if (player.getMyEnergy() == random(50, 100)) {
			game.setRun(true);
		}
	}

	public boolean atGuild() {
		return player.getMine().getLocation().getX() <= 3056
				&& player.getMine().getLocation().getX() >= 3025
				&& player.getMine().getLocation().getY() <= 9756
				&& player.getMine().getLocation().getY() >= 9732;
	}

	public boolean atBank() {
		return calculate.distanceTo(bankTile) <= 6;
	}

	public boolean atLadderUp() {
		return calculate.distanceTo(ladderTile) <= 5;
	}

	public boolean atLadderDown() {
		return calculate.distanceTo(ladderTileDown) <= 5;
	}

	public boolean onGround() {
		return calculate.distanceTo(ladderTile) <= 100;
	}

	public int returning(int time) {
		return time;
	}

	public void startMineOre(RSObject o) {
		try {
			if (i >= 5) {
				i = 0;
				returning(random(100, 300));
			} else {
				if (!objects.click(o, "Mine")) {
					i++;
					startMineOre2(o);
				}
			}
		} catch (Exception e) {

		}
	}

	public void startMineOre2(RSObject o) {
		try {
			if (i >= 5) {
				i = 0;
				returning(random(100, 300));
			} else {
				if (!objects.click(o, "Mine")) {
					i++;
					startMineOre(o);
				}
			}
		} catch (Exception e) {

		}
	}

	public boolean isRockInGuild(RSObject rock) {
		return rock.getLocation().getX() <= 3056
				&& rock.getLocation().getX() >= 3025
				&& rock.getLocation().getY() <= 9756
				&& rock.getLocation().getY() >= 9732;
	}

	public void logOut() {
		mouse.move(754, 10, 10, 10);
		mouse.click(true);
		mouse.move(642, 378, 20, 15);
		mouse.click(true);
		wait(random(2000, 3000));
		log(stopReason);
		stopScript(false);
		wait(random(500, 600));
	}

	public void bankOre() {
		final RSObject bankBooth = objects.getNearestByID(bBoothID);
		final RSNPC banker = npc.getNearestByID(bankerID);
		final int random = random(1, 2);
		if (bank.isOpen()) {
			status = "Depositing";
			if (inventory.getCount(pickaxeID) == 0) {
				bank.depositAll();
			} else {
				bank.depositAllExcept(pickaxeID);
			}
			wait(random(300, 500));
		} else if (!bank.isOpen()) {
			status = "Opening Bank";
			if (random == 1) {
				if (bankBooth != null) {
					objects.click(bankBooth, "Use-quickly");
				}
			} else if (random == 2) {
				if (banker != null) {
					npc.click(banker, "Bank Banker");
				}
			}
			wait(random(200, 300));
			waitToStop();
			return;

		}
	}

	public boolean antiBan() {
		Runnable AntiBan1Runnable = new antiban1();
		Thread AntiBan1 = new Thread(AntiBan1Runnable);
		AntiBan1.start();
		return true;
	}

	public void openRandomTab() {
		int randomNumber = random(1, 11);
		if (randomNumber <= 11) {
			if (randomNumber == 1) {
				game.openTab(TAB_STATS);
				wait(random(100, 200));
				mouse.move(676, 211, 20, 20);
				wait(random(500, 1700));
			}
			if (randomNumber == 2) {
				game.openTab(TAB_ATTACK);
			}
			if (randomNumber == 3) {
				game.openTab(TAB_EQUIPMENT);
			}
			if (randomNumber == 4) {
				game.openTab(TAB_FRIENDS);
			}
			if (randomNumber == 6) {
				game.openTab(TAB_MAGIC);
			}
			if (randomNumber == 7) {
				game.openTab(TAB_STATS);
			}
			if (randomNumber == 8) {
				game.openTab(TAB_QUESTS);
			}
			if (randomNumber == 9) {
				game.openTab(TAB_CLAN);
			}
			if (randomNumber == 10) {
				game.openTab(TAB_MUSIC);
			}
			if (randomNumber == 11) {
				game.openTab(TAB_ACHIEVEMENTDIARIES);
			}
		}
	}

	public final RenderingHints rh = new RenderingHints(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	public void ProgBar(Graphics g, int posX, int posY, int width, int height,
			int Progress, Color color1, Color color2, Color text) {
		int[] c1 = { color1.getRed(), color1.getGreen(), color1.getBlue(), 150 };
		int[] c2 = { color2.getRed(), color2.getGreen(), color2.getBlue(), 150 };
		if (c1[0] > 230) {
			c1[0] = 230;
		}
		if (c1[1] > 230) {
			c1[1] = 230;
		}
		if (c1[2] > 230) {
			c1[2] = 230;
		}
		if (c2[0] > 230) {
			c2[0] = 230;
		}
		if (c2[1] > 230) {
			c2[1] = 230;
		}
		if (c2[2] > 230) {
			c2[2] = 230;
		}

		g.setColor(new Color(c1[0], c1[1], c1[2], 200));
		g.fillRoundRect(posX, posY, width, height, 5, 12);
		g.setColor(new Color(c1[0] + 25, c1[1] + 25, c1[2] + 25, 200));
		g.fillRoundRect(posX, posY, width, height / 2, 5, 12);

		g.setColor(new Color(c2[0], c2[1], c2[2], 200));
		g.fillRoundRect(posX, posY,
				(skills.getPercentToNextLvl(Progress) * width) / 100, height,
				5, 12);
		g.setColor(new Color(c2[0] + 25, c2[1] + 25, c2[2] + 25, 150));
		g.fillRoundRect(posX, posY,
				(skills.getPercentToNextLvl(Progress) * width) / 100,
				height / 2, 5, 12);

		g.setColor(Color.LIGHT_GRAY);
		g.drawRoundRect(posX, posY, width, height, 5, 12);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height));
		g.setColor(text);
		g.drawString("" + skills.getPercentToNextLvl(Progress) + "%", posX
				+ (width / 2), posY + (height + height / 20));
	}

	@Override
	public void onRepaint(Graphics g) {
		if (!getLastChatMessage().equals(lastMsg)) {
			for (int neb = 0; neb < previousMsgsBefoeDelete.length; neb++) {
				if (previousMsgsBefoeDelete[neb].equals("")) {
					previousMsgsBefoeDelete[neb] = getLastChatMessage();
					FWM++;
					break;
				}
			}
			checkChat = true;
		} else {
			checkChat = false;
		}
		int xpGained;
		profit = (mithsMined * mithCost) + (coalsMined * coalCost);
		xpGained = skills.getCurrentXP(Constants.STAT_MINING) - startexp;
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		float xpsec = 0;
		if ((minutes > 0 || hours > 0 || seconds > 0) && xpGained > 0) {
			xpsec = ((float) xpGained)
					/ (float) (seconds + (minutes * 60) + (hours * 60 * 60));
		}
		float xpmin = xpsec * 60;
		float xphour = xpmin * 60;
		final int coalHour = (int) ((coalsMined) * 3600000D / (System
				.currentTimeMillis() - startTime));
		final int mithHour = (int) ((mithsMined) * 3600000D / (System
				.currentTimeMillis() - startTime));
		final int bothHour = (int) ((coalsMined + mithsMined) * 3600000D / (System
				.currentTimeMillis() - startTime));
		final int profitHour = (int) ((profit) * 3600000D / (System
				.currentTimeMillis() - startTime));
		if (showPaint == true && game.isLoggedIn()) {
			((Graphics2D) g).setRenderingHints(rh);
			g.setColor(new Color(0, 0, 0, 205));
			g.fillRect(375, 344, 138, 24);
			g.setFont(new Font("Comic Sans MS", 0, 11));
			g.setColor(new Color(255, 255, 255));
			if (paint == false) {
				g.drawString("Turn on paint", 394, 361);
			}
			if (paint == true) {
				int x = 15;
				int x2 = 149;
				int y = 0;
				int y2 = 0;
				g.drawString("Turn off paint", 394, 361);
				g.setColor(new Color(0, 0, 0, 205));
				g.fillRect(6, 344, 369, 129);
				g.setFont(new Font("Comic Sans MS", 0, 11));
				g.setColor(new Color(255, 255, 255));
				g.drawString(hours + ":" + minutes + ":" + seconds, 300, 365);
				g.setColor(new Color(0, 0, 0, 205));
				if (page == 1) {
					g.fillRect(375, 377, 138, 26);

					// LEFT SIDE//

					g.setColor(new Color(255, 255, 255));
					g.setFont(new Font("Comic Sans MS", 0, 18));
					g.drawString("Bloddy Gribonn's Guild Miner "
							+ info.version(), 21, 368);
					g.setFont(new Font("Comic Sans MS", 0, 11));
					if (mineCoals) {
						g.drawString("Coals mined: " + coalsMined, x, 393);
						y += 18;
					}
					if (mineMiths) {
						g.drawString("Mithrils mined: " + mithsMined, x,
								393 + y);
						y += 18;
					}
					if (!dropGems || !powermine) {
						g.drawString("Gems found: " + gemsGot, x, 393 + y);
					} else {
						g.drawString("Gems dropped: " + gemsGot, x, 393 + y);
					}
					g.drawString("Status: " + status, x, 411 + y);

					// RIGHT SIDE//

					g.drawString("XP Gained: " + xpGained, x2, 393);
					if (!powermine) {
						g.drawString("Profit: " + profit, x2, 411);
						y2 = 18;
					}
					if (chatResponder == true) {
						g.drawString("Responded: " + timesResponded, x2,
								411 + y2);
					}
				} else {
					g.setColor(new Color(0, 0, 0, 205));
					g.fillRect(385, 377, 128, 26);
				}
				if (page == 2) {
					g.setColor(new Color(0, 0, 0, 205));
					g.fillRect(375, 412, 138, 26);
					g.setFont(new Font("Comic Sans MS", 0, 18));
					g.setColor(Color.white);
					g.drawString("Bloddy Gribonn's Guild Miner "
							+ info.version(), 21, 368);
					g.setFont(new Font("Comic Sans MS", 0, 11));
					if (mineCoals) {
						g.drawString("Coals/hour: " + coalHour, x, 393 + y);
						y += 18;
					}
					if (mineMiths) {
						g.drawString("Mithrils/hour: " + mithHour, x, 393 + y);
						y += 18;
					}
					if (mineCoals && mineMiths) {
						g.drawString("Both/Hour: " + bothHour, x, 393 + y);
						y = 0;
					}

					// RIGHT SIDE//

					g.drawString("XP/hour: " + xphour, x2, 393);
					if (!powermine) {
						g.drawString("Profit/hour: " + profitHour, x2, 411);
					}

				} else {
					g.setColor(new Color(0, 0, 0, 205));
					g.fillRect(385, 412, 128, 26);
				}
				if (page == 3) {
					g.setFont(new Font("Comic Sans MS", 0, 18));
					g.setColor(Color.white);
					g.drawString("Bloddy Gribonn's Guild Miner "
							+ info.version(), 21, 368);
					g.setFont(new Font("Comic Sans MS", 0, 11));
					g.setColor(new Color(0, 0, 0, 205));
					g.fillRect(375, 447, 138, 26);
					g.setColor(Color.white);
					g.drawString(
							"Hover mouse over Progress bar for more info! :)",
							x, 411);
					ProgBar(g, 15, 385, 347, 10, Constants.STAT_MINING,
							Color.red, Color.green, Color.black);
				} else {
					g.setColor(new Color(0, 0, 0, 205));
					g.fillRect(385, 447, 128, 26);
				}
				g.setFont(new Font("Comic Sans MS", 0, 11));
				g.setColor(new Color(255, 255, 255));
				g.drawString("General Info", 394, 393);
				g.drawString("Averaging Info", 394, 428);
				g.drawString("Other info", 394, 463);
			}
			Mouse m = Bot.getClient().getMouse();
			if (m.getX() >= 385 && m.getX() < 385 + 128 && m.getY() >= 377 && m.getY() < 377 + 26) {
				page = 1;
			}
			if (m.getX() >= 385 && m.getX() < 385 + 128 && m.getY() >= 412 && m.getY() < 412 + 26) {
				page = 2;
			}
			if (m.getX() >= 385 && m.getX() < 385 + 128 && m.getY() >= 447 && m.getY() < 447 + 26) {
				page = 3;
			}
			if (m.getX() >= 15 && m.getX() < 15 + 347 && m.getY() >= 385 && m.getY() < 385 + 10
					&& page == 3) {
				showMiningInfo = true;
			} else {
				showMiningInfo = false;
			}
			if (showMiningInfo && paint) {
				g.setColor(new Color(0, 0, 0, 205));
				g.fillRect(m.getX(), m.getY() - 100, 200, 100);
				g.setColor(Color.white);
				g.drawString("MINING", m.getX() + 15, m.getY() - 75);
				g.drawString("Level: "
						+ skills.getCurrentLvl(Constants.STAT_MINING),
						m.getX() + 15, m.getY() - 60);
				g.drawString("Xp: "
						+ skills.getCurrentXP(Constants.STAT_MINING),
						m.getX() + 15, m.getY() - 45);
				g.drawString("Xp Till Next level: "
						+ skills.getXPToNextLvl(Constants.STAT_MINING),
						m.getX() + 15, m.getY() - 30);
				g.drawString("% Till Next Level: "
						+ +skills.getPercentToNextLvl(Skills
								.getStatIndex("mining")), m.getX() + 15, m.getY() - 15);

			}
			if (m.getX() >= 375 && m.getX() < 375 + 138 && m.getY() >= 344 && m.getY() < 344 + 24) {
				if (letTurnPaint) {
					if (paint == false) {
						paint = true;
					} else {
						paint = false;
					}
					letTurnPaint = false;
				}
			} else {
				letTurnPaint = true;
			}
		}
		if (normal != null) {
			final Mouse mouse = Bot.getClient().getMouse();
			final int mouse_x = mouse.getX();
			final int mouse_y = mouse.getY();
			final long mpt = System.currentTimeMillis()
					- mouse.getPressTime();
			if (mouse.getPressTime() == -1 || mpt >= 1000) {
				g.drawImage(normal, mouse_x, mouse_y, null);
			}
			if (mpt < 1000) {
				g.drawImage(clicked, mouse_x, mouse_y, null);
			}
		}
		if (hours == 2 && minutes == 0 && seconds == 0) {
			log("w00t! ran for 2 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 3 && minutes == 0 && seconds == 0) {
			log("awesome! ran for 3 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 4 && minutes == 0 && seconds == 0) {
			log("Epic! ran for 4 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 5 && minutes == 0 && seconds == 0) {
			log("Hell yeaH! ran for 5 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 6 && minutes == 0 && seconds == 0) {
			log("keep it up! ran for 6 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 7 && minutes == 0 && seconds == 0) {
			log("NICE NICE! ran for 7 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 8 && minutes == 0 && seconds == 0) {
			log("SICK! ran for 8 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 9 && minutes == 0 && seconds == 0) {
			log("DA PERFECT PROGGY! ran for 9 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == 10 && minutes == 0 && seconds == 0) {
			log("FUCKING AWESOME DUDE! ran for 10 hours! taking screenie :)");
			ScreenshotUtil.takeScreenshot(bot, true);
		}
		if (hours == wantedHours && minutes == wantedMinutes
				&& seconds == wantedSeconds && finishAt) {
			logOut = true;
			stopReason = wantedHours + " hours " + wantedMinutes + " minutes "
					+ wantedSeconds + " seconds past, stopping script";
		}
	}

	public void checkStats() {
		if (game.getCurrentTab() != TAB_STATS) {
			game.openTab(TAB_STATS);
			wait(random(500, 700));
		}
		mouse.move(random(547, 734), random(205, 464));
		wait(random(500, 900));
		game.openTab(TAB_INVENTORY);
	}

	public void addTimer(String name) {
		for (int i = timers.length - 1; i >= 0; i--) {
			if (i != 0)
				timers[i] = timers[i - 1];
			else
				timers[i] = 30;
		}
		for (int i = previousMsgs.length - 1; i >= 0; i--) {
			if (i != 0)
				previousMsgs[i] = previousMsgs[i - 1];
			else
				previousMsgs[i] = name;
		}
	}

	public boolean TimersDontHave(String msg) {
		for (String message : previousMsgs) {
			if (message.equals(msg))
				return false;
		}
		return true;
	}

	public String getLastChatMessage() {
		String originalMsg = "";
		String user = "";
		RSInterface chatinterface = iface.get(137);
		for (RSInterfaceChild child : chatinterface.getChildren()) {
			if (child.getText().contains("<col=0000ff>")) {
				String[] msg = child.getText().split(": <col=0000ff>");
				if (msg.length >= 2) {
					originalMsg = msg[1];
					user = msg[0];
				}
			}
		}
		return user + ".,:,." + originalMsg;
	}

	public boolean tileFullyOnScreen(RSTile tile) {
		Calculations.tileToScreen(tile);
		final Point I = Calculations.tileToScreen(tile.getX(), tile.getY(), 0,
				0, 0);
		final Point II = Calculations.tileToScreen(tile.getX() + 1,
				tile.getY(), 0, 0, 0);
		final Point III = Calculations.tileToScreen(tile.getX(),
				tile.getY() + 1, 0, 0, 0);
		final Point IV = Calculations.tileToScreen(tile.getX() + 1,
				tile.getY() + 1, 0, 0, 0);
		if (I.x != -1 && II.x != -1 && III.x != -1 && IV.x != -1 && I.y != -1
				&& II.y != -1 && III.y != -1 && IV.y != -1) {
			return true;
		}
		return false;
	}

	public void messageReceived(MessageEvent e) {
		final String serverString = e.getMessage();
		if (serverString.contains("mine some mithril")) {
			mithsMined++;
			if (finishWhenMithsMined && mithsMined == wantedMiths
					&& finishAmount) {
				if (finishWhenCoalsMined == false) {
					logOut = true;
					stopReason = mithsMined + " miths are mined";
				} else if (stoppedCoals && finishWhenCoalsMined) {
					logOut = true;
					stopReason = mithsMined + " miths and " + coalsMined
							+ " coals mined";
				} else if (stoppedCoals == false
						&& finishWhenCoalsMined == true) {
					mineMiths = false;
					stoppedMiths = true;
					log("Stopped mining miths because mined " + wantedMiths
							+ " as you wanted");
				}
			}
		}
		if (serverString.contains("You just found")) {
			gemsGot++;
			if (dropGems2) {
				dropGems = true;
			}
		}
		if (serverString.contains("mine some coal")) {
			coalsMined++;
			if (finishWhenCoalsMined && coalsMined == wantedCoals
					&& finishAmount) {
				if (finishWhenMithsMined == false) {
					logOut = true;
					stopReason = coalsMined + " coals are mined";
				} else if (stoppedMiths && finishWhenMithsMined) {
					logOut = true;
					stopReason = mithsMined + " miths and " + coalsMined
							+ " coals mined";
				} else if (stoppedMiths == false
						&& finishWhenMithsMined == true) {
					mineCoals = false;
					stoppedCoals = true;
					log("Stopped mining coals because mined " + wantedCoals
							+ " as you wanted");
				}
			}
		}
		if (serverString.contains("You've just advanced")) {
			log("Congrats on level up, Screenshot taken!");
			ScreenshotUtil.takeScreenshot(bot, true);
			wait(random(1500, 2500));
			if (iface.canContinue()) {
				iface.clickContinue();
			}
			gainedLvls++;
			if (skills.getCurrentLvl(Constants.STAT_MINING) == wantedLevel
					&& finishWhenLevel) {
				logOut = true;
				stopReason = "Got level "
						+ skills.getCurrentLvl(Constants.STAT_MINING)
						+ " in mining, stopping script";
			}
		}
	}

	public class BloddyGibsGuildMinerGUI extends JFrame {

		private static final long serialVersionUID = 1L;

		public BloddyGibsGuildMinerGUI() {
			initComponents();
		}

		private void checkBox4ActionPerformed(ActionEvent e) {
			if (checkBox4.isSelected()) {
				checkBox5.setSelected(true);
				checkBox6.setSelected(true);
				checkBox7.setSelected(true);
				checkBox8.setSelected(true);
				checkBox9.setSelected(true);
				checkBox10.setSelected(true);
				checkBox5.setEnabled(false);
				checkBox6.setEnabled(false);
				checkBox7.setEnabled(false);
				checkBox8.setEnabled(false);
				checkBox9.setEnabled(false);
				checkBox10.setEnabled(false);
			} else {
				checkBox5.setSelected(false);
				checkBox6.setSelected(false);
				checkBox7.setSelected(false);
				checkBox8.setSelected(false);
				checkBox9.setSelected(false);
				checkBox10.setSelected(false);
				checkBox5.setEnabled(true);
				checkBox6.setEnabled(true);
				checkBox7.setEnabled(true);
				checkBox8.setEnabled(true);
				checkBox9.setEnabled(true);
				checkBox10.setEnabled(true);
			}
		}

		private void button1ActionPerformed(ActionEvent e) {
			try {
				dropGems2 = checkBox5.isSelected();
				us = checkBox6.isSelected();
				ue = checkBox7.isSelected();
				ur = checkBox8.isSelected();
				ud = checkBox9.isSelected();
				uy = checkBox10.isSelected();
				finishAt = checkBox18.isSelected();
				wantedHours = Integer.parseInt(textField2.getText());
				wantedMinutes = Integer.parseInt(textField3.getText());
				wantedSeconds = Integer.parseInt(textField4.getText());
				finishWhenLevel = checkBox17.isSelected();
				wantedLevel = Integer.parseInt(textField1.getText());
				finishAmount = checkBox19.isSelected();
				finishWhenCoalsMined = checkBox20.isSelected();
				finishWhenMithsMined = checkBox21.isSelected();
				wantedMiths = Integer.parseInt(textField6.getText());
				wantedCoals = Integer.parseInt(textField5.getText());
				chatResponder = checkBox1.isSelected();
				mineCoals = checkBox2.isSelected();
				mineMiths = checkBox3.isSelected();
				powermine = checkBox4.isSelected();
				showPaint = checkBox16.isSelected();
				guiWait = false;
				dispose();
			} catch (Exception e1) {
				log("ERROR! if script wont work then start it again");
			}
		}

		private void button2ActionPerformed(ActionEvent e) {
			guiWait = false;
			guiExit = true;
			dispose();
		}

		private void initComponents() {
			// //GEN-BEGIN:initComponents
			label1 = new JLabel();
			button1 = new JButton();
			button2 = new JButton();
			tabbedPane1 = new JTabbedPane();
			panel1 = new JPanel();
			checkBox1 = new JCheckBox();
			checkBox2 = new JCheckBox();
			checkBox3 = new JCheckBox();
			checkBox4 = new JCheckBox();
			checkBox16 = new JCheckBox();
			panel2 = new JPanel();
			checkBox5 = new JCheckBox();
			checkBox6 = new JCheckBox();
			checkBox7 = new JCheckBox();
			checkBox8 = new JCheckBox();
			checkBox9 = new JCheckBox();
			checkBox10 = new JCheckBox();
			panel3 = new JPanel();
			checkBox17 = new JCheckBox();
			textField1 = new JTextField();
			checkBox18 = new JCheckBox();
			textField2 = new JTextField();
			textField3 = new JTextField();
			textField4 = new JTextField();
			label2 = new JLabel();
			label4 = new JLabel();
			label5 = new JLabel();
			checkBox19 = new JCheckBox();
			checkBox20 = new JCheckBox();
			checkBox21 = new JCheckBox();
			textField5 = new JTextField();
			label6 = new JLabel();
			textField6 = new JTextField();
			label7 = new JLabel();

			// ======== this ========
			setTitle("Bloddy Gibs Guild Miner GUI");
			setForeground(Color.black);
			setResizable(false);
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ---- label1 ----
			label1.setText("Bloddy Gibs Guild Miner " + info.version());
			label1.setFont(new Font("Lucida Calligraphy", Font.BOLD, 16));
			label1.setForeground(Color.red);
			contentPane.add(label1);
			label1.setBounds(10, 10, 260, 30);

			// ---- button1 ----
			button1.setText("Start");
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button1ActionPerformed(e);
				}
			});
			contentPane.add(button1);
			button1.setBounds(new Rectangle(new Point(230, 235), button1
					.getPreferredSize()));

			// ---- button2 ----
			button2.setText("Cancel");
			button2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button2ActionPerformed(e);
				}
			});
			contentPane.add(button2);
			button2.setBounds(new Rectangle(new Point(165, 235), button2
					.getPreferredSize()));

			// ======== tabbedPane1 ========
			{

				// ======== panel1 ========
				{
					panel1.setLayout(null);

					// ---- checkBox1 ----
					checkBox1.setText("Chat responder");
					checkBox1
							.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
					checkBox1.setSelected(true);
					panel1.add(checkBox1);
					checkBox1.setBounds(10, 10, 120, 20);

					// ---- checkBox2 ----
					checkBox2.setText("Mine coal");
					checkBox2
							.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
					checkBox2.setSelected(true);
					panel1.add(checkBox2);
					checkBox2.setBounds(10, 35, 120, 20);

					// ---- checkBox3 ----
					checkBox3.setText("Mine mithril");
					checkBox3
							.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
					checkBox3.setSelected(true);
					panel1.add(checkBox3);
					checkBox3.setBounds(10, 60, 120, 20);

					// ---- checkBox4 ----
					checkBox4.setText("Powermine");
					checkBox4
							.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
					checkBox4.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							checkBox4ActionPerformed(e);
						}
					});
					panel1.add(checkBox4);
					checkBox4.setBounds(new Rectangle(new Point(10, 85),
							checkBox4.getPreferredSize()));

					// ---- checkBox16 ----
					checkBox16.setText("Paint");
					checkBox16.setSelected(true);
					panel1.add(checkBox16);
					checkBox16
							.setFont(new Font("Comic Sans MS", Font.PLAIN, 11));
					checkBox16.setBounds(new Rectangle(new Point(150, 10),
							checkBox16.getPreferredSize()));

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel1.getComponentCount(); i++) {
							Rectangle bounds = panel1.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = panel1.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel1.setMinimumSize(preferredSize);
						panel1.setPreferredSize(preferredSize);
					}
				}
				tabbedPane1.addTab("Main", panel1);

				// ======== panel2 ========
				{
					panel2.setLayout(null);

					// ---- checkBox5 ----
					checkBox5.setText("Drop Gems");
					panel2.add(checkBox5);
					checkBox5.setBounds(new Rectangle(new Point(10, 10),
							checkBox5.getPreferredSize()));

					// ---- checkBox6 ----
					checkBox6.setText("Sapphire");
					panel2.add(checkBox6);
					checkBox6.setBounds(new Rectangle(new Point(30, 30),
							checkBox6.getPreferredSize()));

					// ---- checkBox7 ----
					checkBox7.setText("Emerald");
					panel2.add(checkBox7);
					checkBox7.setBounds(30, 50,
							checkBox7.getPreferredSize().width, 23);

					// ---- checkBox8 ----
					checkBox8.setText("Ruby");
					panel2.add(checkBox8);
					checkBox8.setBounds(30, 70,
							checkBox8.getPreferredSize().width, 23);

					// ---- checkBox9 ----
					checkBox9.setText("Diamond");
					panel2.add(checkBox9);
					checkBox9.setBounds(30, 90,
							checkBox9.getPreferredSize().width, 23);

					// ---- checkBox10 ----
					checkBox10.setText("Jade");
					panel2.add(checkBox10);
					checkBox10.setBounds(30, 110,
							checkBox10.getPreferredSize().width, 23);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel2.getComponentCount(); i++) {
							Rectangle bounds = panel2.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = panel2.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel2.setMinimumSize(preferredSize);
						panel2.setPreferredSize(preferredSize);
					}
				}
				tabbedPane1.addTab("Gems dropping", panel2);

				// ======== panel3 ========
				{
					panel3.setLayout(null);

					// ---- checkBox17 ----
					final int nowLvl = skills
							.getRealLvl(Constants.STAT_MINING);
					checkBox17.setText("When reached level:");
					if (nowLvl == 99)
						checkBox17.setEnabled(false);
					panel3.add(checkBox17);
					checkBox17.setBounds(new Rectangle(new Point(5, 5),
							new Dimension(
									checkBox17.getPreferredSize().width + 10,
									checkBox17.getPreferredSize().height)));

					// ---- textField1 ----
					if (game.isLoggedIn()) {
						if (nowLvl != 99)
							textField1.setText("" + ((int) (nowLvl + 1)));
						else {
							textField1.setText("" + nowLvl);
							textField1.setEnabled(false);
						}
					} else
						textField1.setText("0");
					panel3.add(textField1);
					textField1
							.setBounds(
									25 + checkBox17.getPreferredSize().width,
									5, 50, 20);

					// ---- checkBox18 ----
					checkBox18.setText("After:");
					panel3.add(checkBox18);
					checkBox18.setBounds(5, 30,
							checkBox18.getPreferredSize().width, 23);

					// ---- textField2 ----
					textField2.setText("0");
					panel3.add(textField2);
					textField2.setBounds(50, 55, 40, 20);

					// ---- textField3 ----
					textField3.setText("0");
					panel3.add(textField3);
					textField3.setBounds(115, 55, 40, 20);

					// ---- textField4 ----
					textField4.setText("0");
					panel3.add(textField4);
					textField4.setBounds(175, 55, 40, 20);

					// ---- label2 ----
					label2.setText("H:");
					panel3.add(label2);
					label2.setBounds(35, 55, 15, 20);

					// ---- label4 ----
					label4.setText("M:");
					panel3.add(label4);
					label4.setBounds(100, 55, 15, 20);

					// ---- label5 ----
					label5.setText("S:");
					panel3.add(label5);
					label5.setBounds(160, 55, 15, 20);

					// ---- checkBox19 ----
					checkBox19.setText("When mined...");
					checkBox19.setSelectedIcon(null);
					panel3.add(checkBox19);
					checkBox19.setBounds(new Rectangle(new Point(5, 85),
							checkBox19.getPreferredSize()));
					panel3.add(checkBox20);
					checkBox20.setBounds(new Rectangle(new Point(25, 110),
							checkBox20.getPreferredSize()));
					panel3.add(checkBox21);
					checkBox21.setBounds(new Rectangle(new Point(25, 130),
							checkBox21.getPreferredSize()));

					// ---- textField5 ----
					textField5.setText("0");
					panel3.add(textField5);
					textField5.setBounds(70, 110, 55, 20);

					// ---- label6 ----
					label6.setText("Coals");
					panel3.add(label6);
					label6.setBounds(new Rectangle(new Point(125, 114), label6
							.getPreferredSize()));

					// ---- textField6 ----
					textField6.setText("0");
					panel3.add(textField6);
					textField6.setBounds(70, 130, 55, 20);

					// ---- label7 ----
					label7.setText("Miths");
					panel3.add(label7);
					label7.setBounds(new Rectangle(new Point(125, 135), label7
							.getPreferredSize()));

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel3.getComponentCount(); i++) {
							Rectangle bounds = panel3.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = panel3.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel3.setMinimumSize(preferredSize);
						panel3.setPreferredSize(preferredSize);
					}
				}
				tabbedPane1.addTab("Finish", panel3);

			}
			contentPane.add(tabbedPane1);
			tabbedPane1.setBounds(15, 45, 270, 190);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width,
							preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height,
							preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			setSize(310, 295);
			setLocationRelativeTo(getOwner());
			// GEN-END:initComponents
		}

		// GEN-BEGIN:variables
		private JLabel label1;
		private JButton button1;
		private JButton button2;
		private JTabbedPane tabbedPane1;
		private JPanel panel1;
		private JCheckBox checkBox1;
		private JCheckBox checkBox2;
		private JCheckBox checkBox3;
		private JCheckBox checkBox4;
		private JCheckBox checkBox16;
		private JPanel panel2;
		private JCheckBox checkBox5;
		private JCheckBox checkBox6;
		private JCheckBox checkBox7;
		private JCheckBox checkBox8;
		private JCheckBox checkBox9;
		private JCheckBox checkBox10;
		private JPanel panel3;
		private JCheckBox checkBox17;
		private JTextField textField1;
		private JCheckBox checkBox18;
		private JTextField textField2;
		private JTextField textField3;
		private JTextField textField4;
		private JLabel label2;
		private JLabel label4;
		private JLabel label5;
		private JCheckBox checkBox19;
		private JCheckBox checkBox20;
		private JCheckBox checkBox21;
		private JTextField textField5;
		private JLabel label6;
		private JTextField textField6;
		private JLabel label7;
		// GEN-END:variables
	}

}