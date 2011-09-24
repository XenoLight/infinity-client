import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPolygon;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Henry" }, category = "Mining", name = "Henry's Excellent Essence Excavator", version = 1.00, description = "<html><style type='text/css'>"
		+ "body {background:url('http://lazygamerz.org/client/images/back_2.png') repeat}"
		+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
		+ "<h1><center><font color=#FFFFFF>"
		+ "Henry's Essence Miner by; Henry"
		+ "</center></font color></h1>"
		+ "</head><br><body>"
		+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
		+ "<td width=90% align=justify>"
		+ "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"
		+ "<font size=3>Start this script at the east bank in Varrock or at Yanille Have your mining pick on "
		+ "you or in your inventory...<br>"
		+ "Enable resting Yes <input type='radio' name='rest' id='rest' checked='checked' value='yes'/>  No <input type='radio' name='rest' id='rest' value='no'/> <br />"
		+ "Location: <select name='loc' id='loc'><option>Varrock</option><option>Yanille</option></select><br />"
		+ "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"
		+ "</td></tr></table><br />")
public class HenrysExcellentEssenceExcavator extends Script implements PaintListener {

	// Areas
	private RSArea BANK_AREA = new RSArea(new RSTile(3251, 3419), new RSTile(
			3257, 3423));
	private RSPolygon SHOP_AREA = new RSPolygon(new RSTile(3250, 3401),
			new RSTile(3252, 3404), new RSTile(3253, 3404), new RSTile(3255,
					3401), new RSTile(3253, 3399), new RSTile(3252, 3399));
	private RSPolygon GUILD_AREA = new RSPolygon(new RSTile(2597, 3089),
			new RSTile(2597, 3085), new RSTile(2593, 3081), new RSTile(2589,
					3081), new RSTile(2585, 3085), new RSTile(2585, 3089),
			new RSTile(2588, 3092), new RSTile(2589, 3093), new RSTile(2591,
					3093), new RSTile(2592, 3093), new RSTile(2593, 3093));
	// Path
	private RSTile[] WALKGINPATH = { new RSTile(3253, 3421),
			new RSTile(3259, 3428), new RSTile(3260, 3419),
			new RSTile(3259, 3410), new RSTile(3252, 3401) };
	private RSTile[] WALKINGPATH2 = { new RSTile(2613, 3093),
			new RSTile(2603, 3090), new RSTile(2597, 3088), };
	// Tiles
	public RSTile DOORTILE = new RSTile(3253, 3398);
	// Integers
	private int WIZARD = 462;
	private int TELEPORT = 5913;
	private int ESSENCE_ROCK = 2491;
	private int ESSENCE = 0;
	private int NORMAL_ESSENCE = 1436;
	private int PURE_ESSENCE = 7936;
	private int PORTAL = 39831;
	private int FAILSAFE = 0;
	private int BANK_FAIL = 0;
	// Arrays
	private int[] PICKAXES = { 1275, 1265, 1273, 1267, 1271, 13661 };
	private int[] MINING_INFO = new int[4];
	// Booleans
	private boolean REST = false;
	private boolean YANILLE = false;
	private boolean HAS_STARTED_MINING = false;
	// Strings
	private String STATUS = "Starting up";
	// Long
	private long STARTTIME = System.currentTimeMillis();
	// Interfaces
	private RSInterfaceChild collect = iface.getChild(109, 14);
	// Ticker
	private Ticker tick = new Ticker();
	// Color
	private final Color color1 = new Color(0, 102, 0, 145);
	private final Color color2 = new Color(51, 204, 0, 149);
	private final Color color3 = new Color(0, 30, 0);
	private final Color color4 = new Color(51, 204, 0, 155);
	private final Color color5 = new Color(0, 0, 0);
	private final Color color6 = new Color(0, 153, 0, 180);
	private final Color color7 = new Color(204, 0, 0, 205);
	// Stroke
	private final BasicStroke stroke1 = new BasicStroke(1);
	// Font
	private final Font font1 = new Font("Arial", 0, 16);
	private final Font font2 = new Font("Arial", 0, 12);
	//Char
	private char[] c = new char[]{'k', 'm', 'b', 't'};

	@Override
	public void onRepaint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		if (MINING_INFO[2] < skills.getCurrentXP(Constants.STAT_MINING)) {
			MINING_INFO[3]++;
			MINING_INFO[2] = skills.getCurrentXP(Constants.STAT_MINING);
			if (ESSENCE == 0) {
				if (inventory.getCount(NORMAL_ESSENCE) > 0)
					ESSENCE = NORMAL_ESSENCE;
				else
					ESSENCE = PURE_ESSENCE;
			}
		}
		String gained = coolFormat(skills.getCurrentXP(Constants.STAT_MINING) - MINING_INFO[0],0);
		g.setColor(color1);
		g.fillRoundRect(2, 311, 516, 27, 32, 32);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRoundRect(2, 311, 516, 27, 32, 32);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString(skills.getCurrentLvl(Constants.STAT_MINING)+"/99 || "+gained+" XP", 341, 331);
		tick.run(g);
		g.setColor(color4);
		g.fillRect(187, 317, 139, 15);
		g.setColor(color5);
		g.drawRect(187, 317, 139, 15);
		g.setColor(color6);
		g.fillRect(188, 318, 138, 7);
		g.setColor(color7);
		int p = skills.getPercentToNextLvl(Constants.STAT_MINING);
		int y = (int) (1.38 * p);
		g.fillRect(188, 318, y, 14);
		g.fillRect(188, 318, y, 7);
	}
	
	private String coolFormat(double n, int iteration) {
	    double d = ((long) n / 100) / 10.0;
	    boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
	    return (d < 1000? //this determines the class i.e. 'k', 'm' etc
	        ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
	         (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 bye bye decimal
	         ) + "" + c[iteration]) 
	        : coolFormat(d, iteration+1));

	}


	public void onFinish() {
	}

	public boolean onStart(java.util.Map<java.lang.String, java.lang.String> map) {
		MINING_INFO[0] = skills.getCurrentXP(Constants.STAT_MINING);
		MINING_INFO[1] = skills.getCurrentLvl(Constants.STAT_MINING);
		MINING_INFO[2] = MINING_INFO[0];
		if (map.get("rest").equals("yes")) {
			REST = true;
			log("We are resting.");
		}
		if (map.get("loc").equals("Yanille")) {
			WALKGINPATH = WALKINGPATH2;
			SHOP_AREA = GUILD_AREA;
			TELEPORT = WIZARD;
			DOORTILE = new RSTile(2597, 3088);
			log(DOORTILE.toString());
			BANK_AREA = new RSArea(new RSTile(2609, 3088), new RSTile(2613,
					3097));
			YANILLE = true;
			log("We are in Yanille");
		}
		STARTTIME = System.currentTimeMillis();
		return true;
	}

	@Override
	public int loop() {
		try {
			// Diffrent bugfixes
			if (objects.getNearestByID(PORTAL) != null && !HAS_STARTED_MINING
					&& !isOnGround()) {
				RSObject p = objects.getNearestByID(PORTAL);
				RSTile o = p.getLocation();
				RSTile m = player.getMyLocation();
				if (o.getX() == m.getX() && Math.abs(o.getY() - m.getY()) == 1) {
					STATUS = "Dam YOU JAGEX!";
					log("Jagex 'BUG'");
					if (doLeaving())
						return 100;
					else
						return 10;
				}
			}
			if (collect.getParInterface().isValid()) {
				STATUS = "Collection Screen";
				sleep(500, 700);
				iface.clickChild(collect);
				int fail = 0;
				while (fail++ < 30 && collect.getParInterface().isValid()) {
					sleep(100, 200);
				}
				return 10;
			}
			// Main loop
			if (inventory.isFull()) {
				if (isOnGround()) {
					if (BANK_AREA.contains(player.getMyLocation())) {
						STATUS = "Banking";
						HAS_STARTED_MINING = false;
						if (doBanking())
							return 100;
						else
							return 10;
					} else {
						if (YANILLE && SHOP_AREA.contains(player.getMyLocation())) {
							STATUS = "Opening the door";
							RSTile me = player.getMyLocation();
							if (atDoor(objects.getTopAt(DOORTILE), "Open")) {
								int fail = 0;
								while (fail++ < 100 && me.equals(player.getMyLocation())) {
									sleep(30, 40);
								}
								return 100;
							} else
								return 10;
						}
						STATUS = "Walking to Bank";
						doWalking();
						return 100;
					}
				} else {
					STATUS = "Leaving the Mine";
					BANK_FAIL = 0;
					if (doLeaving())
						return 100;
					else
						return 10;
				}
			} else {
				if (!isOnGround()) {
					if (iface.getChild(211, 3).isValid()) {
						log("No Pickaxe.");
						return -1;
					}
					STATUS = "Mining";
					if (doMining())
						return 100;
					else
						return 10;
				} else {
					if (BANK_AREA.contains(player.getMyLocation())) {
						STATUS = "Walking to Teleport";
						doWalking();
						return 100;
					} else if (SHOP_AREA.contains(player.getMyLocation())) {
						STATUS = "Entering the Mine";
						HAS_STARTED_MINING = false;
						BANK_FAIL = 0;
						if (doEntering())
							return 100;
						else
							return 10;
					} else {
						if (calculate.distanceTo(DOORTILE) < 4) {
							if (!YANILLE) {
								if (isDoorOpen()) {
									STATUS = "Getting inside the Shop";
									if (npc.getNearestByID(TELEPORT) != null) {
										walk.tileMM(
												npc.getNearestByID(TELEPORT)
														.getLocation(), 0, 0);
										int fail = 0;
										while (!SHOP_AREA
												.contains(player.getMyLocation())) {
											fail++;
											if (fail >= 8)
												return 10;
											if (player.getMine().isMoving())
												fail = 0;
											if (player.getMine().getAnimation() != -1)
												fail = 0;
											wait(500);
										}
										return 100;
									} else
										return 10;
								} else {
									STATUS = "Opening the Door";
									RSTile me = player.getMyLocation();
									if (atDoor(objects.getTopAt(DOORTILE), "Open")) {
										int fail = 0;
										while (fail++ < 100
												&& me.equals(player.getMyLocation())) {
											sleep(30, 40);
										}
										return 100;
									} else
										return 10;
								}
							} else {
								STATUS = "Getting inside the Guild";
								RSTile me = player.getMyLocation();
								if (atDoor(objects.getTopAt(DOORTILE), "Open")) {
									int fail = 0;
									while (fail++ < 100
											&& me.equals(player.getMyLocation())) {
										sleep(30, 40);
									}
									return 100;
								} else
									return 10;
							}
						} else {
							STATUS = "Walking";
							doWalking();
							return 100;
						}
					}
				}
			}
		} catch (Exception ignored) {
		}
		return 1;
	}

	public boolean doEntering() {
		setRunning();
		if (SHOP_AREA.contains(player.getMyLocation())) {
			RSNPC a = npc.getNearestByID(TELEPORT);
			if (a != null) {
				if (a.action("Teleport")) {
					int fail = 0;
					while (SHOP_AREA.contains(player.getMyLocation())) {
						fail++;
						if (fail >= 40)
							return false;
						if (player.getMine().isMoving())
							fail = 0;
						if (player.getMine().getAnimation() != -1)
							fail = 0;
						wait(100);
					}
					return true;
				}
			}
		}
		return false;
	}

	public boolean doLeaving() {
		setRunning();
		RSObject p = objects.getNearestByID(PORTAL);
		if (p != null) {
			if (!p.isOnScreen()) {
				if (p.isOnMinimap()) {
					walk.tileMM(p.getLocation());
				} else {
					walkPath(walk.cleanPath(walk.generateFixedPath(p.getLocation())));
				}
				int fail = 0;
				while (!p.isOnScreen()) {
					fail++;
					wait(10);
					if (player.getMine().isMoving())
						fail = 0;
					if (player.getMine().getAnimation() != -1)
						fail = 0;
					if (fail >= 300)
						return false;
				}
			}
			if (clickModel(p,"Enter")) {
				wait(100);
				int fail = 0;
				while (objects.getNearestByID(PORTAL) != null) {
					fail++;
					if (player.getMine().isMoving())
						fail = 0;
					if (player.getMine().getAnimation() != -1)
						fail = 0;
					if (fail >= 400)
						return false;
					wait(10);
				}
				return true;
			}
		}
		return false;
	}

	public boolean doMining() {
		if (!isMining()) {
			STATUS = "Startng to Mine";
			RSObject r = objects.getNearestByID(ESSENCE_ROCK);
			if (r != null) {
				if (!r.isOnScreen() || calculate.distanceTo(r) > 3) {
					if (r.isOnMinimap()) {
						walk.tileMM(r.getLocation(), 0, 0);
					} else {
						walkPath(walk.generateFixedPath(r.getLocation()));
						return true;
					}
					int fail = 0;
					while (calculate.distanceTo(r) > 3) {
						fail++;
						wait(10);
						if (player.getMine().isMoving())
							fail = 0;
						if (player.getMine().getAnimation() != -1)
							fail = 0;
						if (fail >= 300)
							return false;
					}
				}
				if (objects.getNearestByID(ESSENCE_ROCK).isOnScreen()) {
					STATUS = "Cliking";
					sleep(100, 200);
					if (clickModel(r, "Mine")) {
						int fail = 0;
						while (player.getMine().getAnimation() == -1) {
							fail++;
							if (player.getMine().isMoving())
								fail = 0;
							wait(100);
							if (fail >= 20)
								return false;
						}
						HAS_STARTED_MINING = true;
						return true;
					}
				}
			}
			return false;
		} else {
			STATUS = "Idle";
			antiBan();
			return true;
		}
	}

	public void doWalking() {
		if (inventory.isFull()) {
			walkPath(walk.reversePath(WALKGINPATH));
			int fail = 0;
			int largeFail = 0;
			while (!BANK_AREA.contains(player.getMyLocation()) && isDoorOpen()) {
				fail++;
				if (largeFail >= 5)
					return;
				if (fail >= 10) {
					if (calculate.distanceTo(BANK_AREA.getRandomTile()) > 4)
						walkPath(walk.cleanPath(walk.generateFixedPath(BANK_AREA
								.getRandomTile())));
					else
						walk.tileMM(BANK_AREA.getRandomTile(), 0, 0);
					fail = 0;
					largeFail++;
				}
				if (player.getMine().isMoving())
					fail = 0;
				if (player.getMine().getAnimation() != -1)
					fail = 0;
				wait(100);
			}
		} else {
			walkPath(WALKGINPATH);
			int fail = 0;
			int largeFail = 0;
			while (!SHOP_AREA.contains(player.getMyLocation()) && isDoorOpen()) {
				fail++;
				if (largeFail >= 5)
					return;
				if (fail >= 10) {
					walk.tileMM(npc.getNearestByID(TELEPORT).getLocation(), 0,
							0);
					fail = 0;
					largeFail++;
				}
				if (player.getMine().isMoving())
					fail = 0;
				if (player.getMine().getAnimation() != -1)
					fail = 0;
				wait(100);
			}
		}
	}

	public boolean doBanking() {
		if (!bank.isOpen()) {
			while (player.getMine().isMoving())
				sleep(50, 100);
			if (bank.open()) {
				int fail = 0;
				while (fail++ <= 100 && !bank.isOpen()) {
					wait(50);
				}
				if (fail >= 100)
					return false;
			} else {
				if (BANK_FAIL > 5) {
					camera.setRotation(random(0, 360));
					BANK_FAIL = 0;
				} else {
					BANK_FAIL++;
				}
			}
		}
		if (bank.isOpen()) {
			sleep(400, 600);
			if (inventory.contains(PICKAXES)) {
				bank.depositAllExcept(PICKAXES);
			} else {
				bank.depositAll();
			}
			int fail = 0;
			while (fail++ <= 100 && inventory.getCount(ESSENCE) > 0) {
				wait(50);
			}
			if (fail >= 100)
				return false;
			return true;
		}
		return false;
	}

	public boolean isOnGround() {
		RSTile loc = player.getMyLocation();
		if (YANILLE) {
			return loc.getX() > 2550 && loc.getX() < 2650 && loc.getY() > 3000
					&& loc.getY() < 3100;
		} else {
			return loc.getX() > 3200 && loc.getX() < 3300 && loc.getY() > 3300
					&& loc.getY() < 3500;
		}
	}

	public boolean isDoorOpen() {
		return objects.getTopAt(DOORTILE) == null
				|| objects.getTopAt(DOORTILE).getID() == 83;
	}

	public boolean atDoor(RSObject door, String action) {
		if (door != null) {
			if (!door.isOnScreen()) {
				if (door.isOnMinimap()) {
					walk.tileMM(door.getLocation());
				} else {
					walkPath(walk.cleanPath(walk.generateFixedPath(door.getLocation())));
				}
				int fail = 0;
				while (!door.isOnScreen()) {
					fail++;
					wait(10);
					if (player.getMine().isMoving())
						fail = 0;
					if (player.getMine().getAnimation() != -1)
						fail = 0;
					if (fail >= 300)
						return false;
				}
			}
			while (player.getMine().isMoving())
				sleep(100, 200);
			sleep(600, 800);
			door = objects.getTopAt(door.getLocation());
			if (clickModel(door, action)) {
				int fail = 0;
				while (!player.getMine().isMoving()) {
					fail++;
					wait(10);
					if (player.getMine().isMoving())
						fail = 0;
					if (player.getMine().getAnimation() != -1)
						fail = 0;
					if (fail >= 300)
						return false;
				}
				while (player.getMine().isMoving())
					sleep(100, 200);
				return true;
			}
		}
		return false;
	}

	public void walkPath(RSTile[] path) {
		if (path == null)
			return;
		setRunning();
		if (calculate.distanceTo(path[path.length - 1]) < 5)
			return;
		while (calculate.distanceTo(path[path.length - 1]) > 4) {
			RSTile cur = getNearest(path);
			walk.tileMM(cur);
			int fail = 0;
			while (!player.getMine().isMoving()) {
				fail++;
				if (fail >= 7) {
					camera.setRotation(random(0, 360));
					return;
				}
				sleep(300);
			}
			while (player.getMine().isMoving() && calculate.distanceTo(cur) > 4)
				sleep(100);
			if (REST && (player.getMyEnergy() < 10 + random(1, 10) || player.getMyEnergy() < 15)) {
				player.rest(random(80, 100));
			}
		}
	}

	public RSTile getNearest(RSTile[] path) {
		RSTile best = new RSTile(-1, -1);
		for (RSTile c : path) {
			if (c == null)
				continue;
			if (c.isOnMinimap() && calculate.distanceTo(c) < 14) {
				best = c;
			}
		}
		return best;
	}

	public void setRunning() {
		if (game.isLoggedIn()) {
			if (player.getMyEnergy() > random(60, 71) && !isRunning()) {
				game.setRun(true);
				wait(random(300, 500));
			}
		}
	}

	public boolean isMining() {
		if (calculate.distanceTo(objects.getNearestByID(ESSENCE_ROCK)) > 7)
			return false;
		if (player.getMine().isMoving()) {
			FAILSAFE = 0;
			return true;
		}
		if (player.getMine().getAnimation() != -1) {
			HAS_STARTED_MINING = true;
			FAILSAFE = 0;
			return true;
		} else {
			if (!HAS_STARTED_MINING)
				return false;
			if (FAILSAFE == -1) {
				FAILSAFE++;
				return false;
			}
			if (FAILSAFE >= 30) {
				FAILSAFE = -1;
				return false;
			}
			FAILSAFE++;
			if (calculate.distanceTo(objects.getNearestByID(ESSENCE_ROCK)) < 7)
				FAILSAFE++;// Double SPEED
			return true;
		}
	}

	public boolean clickModel(RSObject o, String action) {
		if (o != null) {
			Point[] p = o.getModel().getModelPoints();
			int fail = 0;
			for (Point c : p) {
				c = p[random(0, p.length)];
				if (fail++ > 10)
					return false;
				if (calculate.pointOnScreen(c)) {
					mouse.move(c);
					if (menu.contains(action)){
						mouse.click(true);
						return true;
					}
				}
			}
		}
		return false;
	}

	public void antiBan() {
		int r = random(0, 6001);
		if (r >= 0 && r <= 10) {
			mouse.moveRandomly(50);
			return;
		}
		if (r == 20) {
			mouse.moveRandomly(300);
			return;
		}
		if (r == 30) {
			int an = camera.getAngle();
			an += random(-45, 46);
			if (an < 0)
				an = 0;
			if (an > 360)
				an = 0;
			camera.setRotation(an);
			return;
		}
	}

	public class Ticker {

		private String text = "";
		private long lastUpdate = 0;
		private int update = 47;
		private int width = 155;
		private int last = 0;

		public void run(Graphics g) {
			long millis = System.currentTimeMillis() - STARTTIME;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;
			int MINED_HOUR = (int) Math.round(MINING_INFO[3] * 3600000D
					/ (System.currentTimeMillis() - STARTTIME));
			setText("Run Time:" + hours + "h " + minutes + "min " + seconds
					+ "sec  ||  " + "Status: " + STATUS + "  ||  Mined: "
					+ MINING_INFO[3] + "  ||  Mined/Hour: " + MINED_HOUR + "");
			draw(g);
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setSpeed(int a) {
			update = a;
		}

		public void draw(Graphics g) {
			int x = 17;
			int y = 329;
			g.setColor(color3);
			g.setFont(font2);
			if (System.currentTimeMillis() - lastUpdate >= update) {
				int total_w = g.getFontMetrics().stringWidth(text);
				if (total_w <= width) {
					g.drawString(text, x, y);
					return;
				}
				if (last >= text.length())
					last = 0;
				if (g.getFontMetrics().stringWidth(text.substring(last)) < width) {
					text = text + "  ||  " + text;
				}
				String next = "";
				for (int i = last; i < text.length(); i++) {
					String temp = text.substring(last, i);
					if (g.getFontMetrics().stringWidth(temp) <= width) {
						next = temp;
					}
				}
				g.drawString(next, x, y);
				last++;
				lastUpdate = System.currentTimeMillis();
			} else {
				int total_w = g.getFontMetrics().stringWidth(text);
				if (total_w <= width) {
					g.drawString(text, x, y);
					return;
				}
				if (last >= text.length())
					last = 0;
				String next = "";
				if (g.getFontMetrics().stringWidth(text.substring(last)) < width) {
					text = text + "  ||  " + text;
				}
				for (int i = last; i < text.length(); i++) {
					String temp = text.substring(last, i);
					if (g.getFontMetrics().stringWidth(temp) <= width) {
						next = temp;
					}
				}
				g.drawString(next, x, y);
			}
		}

	}

}
