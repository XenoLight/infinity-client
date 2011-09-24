import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONFileHandler;
import org.json.JSONObject;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Bank;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSPolygon;
import org.rsbot.script.wrappers.RSTile;

/**
 * 
 * Changelogs: 0.3 -> 0.5 - new beta trees at port sarim added - new icon "manifest.png"
 *  - ability to pause debug console
 *  - better walking method
 *  - when walking to trees, two possibilities. Use premade path or use
 * generated-on-fly path 
 *  - antibans logged to debug console 
 *  - used walking path logged to debug console 
 *  - performance improvements in onRepaint() (uses finals) 
 *  - optional quick chat responder 
 *  - show current trees model when on extended paint mode 
 *  - better woodcutting handling 0.5 -> 0.55 
 *  - JSON File saving 
 *  - Normal format -> JSON converter included
 * 0.5 -> build 1 
 *   - move to build based version numbering 
 *   - rewrote loop() to make it cleaner and fixed a serious bug in it
 *   - tray icon for chat messages
 *   - new config loader + config saver improvements
 * build 1 -> build 2
 *   - bug with assigning next tree fixed
 * build 2 -> build 3
 *   - more stable next tree selecting
 *   - somewhat stable ivys
 *   - antiban added again
 * build 3 -> build 4
 *   - rotatecamera disabled
 *   - try catch ivy
 *   - some ivy failsafes
 *   - better handling when out of safe area
 * build 4 -> build 5
 *   - omg doors and gates
 * 
 * @author wolf
 */

@ScriptManifest(authors = "Waterwolf", category = "Woodcutting", name = "All WCer", version = 5, description = "<html>dev</html>", website = "http://www.lazygamerz.org/forums/index.php?topic=2060.0")
public class wolfsAllWcer extends Script implements PaintListener,
		MessageListener, MouseListener, MouseMotionListener {

	public final int build = (int) getClass().getAnnotation(
			ScriptManifest.class).version();
	public long startTime = System.currentTimeMillis();

	public StartPhase startPhase = StartPhase.ChooseMode;

	//public ArrayList<RSTile> tilepoints = new ArrayList<RSTile>();
	public RSPolygon tilepoints = new RSPolygon();
	
	public boolean lastDown = false;

	public RSObject treeSpot = null;

	public PathTile[] pathToOrigSpot = null;

	public boolean useMousekeys = false;

	public WCMode chopBankIvy = WCMode.Powerlevel;

	public enum WCMode {
		Powerlevel, Bank, Ivy;
	}

	public RSTile bankTile = new RSTile(0, 0);
	public RSTile woodsTile = new RSTile(0, 0);
	public PathTile[] pathToWoods = new PathTile[0];


	public ArrayList<PathTile> tempPathToWoods = new ArrayList<PathTile>();
	public class PathTile extends RSTile {
		
		int obstacle = -1;

		public PathTile(int x, int y) {
			super(x, y);
		}
		
		public PathTile(RSTile r) {
			super(r.getX(), r.getY());
		}
		
		public PathTile(int x, int y, int obstacleId) {
			super(x, y);
			this.obstacle = obstacleId;
		}
		
		public boolean isObstacle() {
			return obstacle != -1;
		}
		
		public int getObstacleId() {
			return obstacle;
		}
		
		public PathTile setObstacle(int id) {
			this.obstacle = id;
			return this;
		}
		
	}

	public Tree selectedTree = Tree.Tree;

	// public TreeParticle tp = null;

	public enum Tree {

		Tree(1511, new int[] { 1276, 1278, 38760, 38783, 38787, 9355, 9387,
				3300, 9388, 9354, 9366, 11866, 3293 }, new int[] { 1342, 40356,
				11862, 10951, 11865, 11855, 11059, 11864, 9389 }), Oak(1521,
				new int[] { 1281
				/* beta trees */, 38731, 38732 }, new int[] { 1356, 38754 }),

		Willow(1519, new int[] { 1308, 2210, 5551, 5552, 5553, 139, 142, 2372
		/* beta trees */, 38627, 38616 }, new int[] { 5554, 7399, 38725 }), Teak(
				6333, new int[] { 9036 }, new int[] { 9037 }), Maple(1517,
				new int[] { 1307 }, new int[] { 7400 }), Yew(1515, new int[] {
				1309, 38755 }, new int[] { 7402, 38759 }), Magic(1513,
				new int[] { 1306 }, new int[] { 7401 }), ;

		private int logId;
		private int[] treeId;
		private int[] cutDownId;
		private int price = 0;

		private Tree(int logId, int[] treeId, int[] cutDownId) {
			this.logId = logId;
			this.treeId = treeId;
			this.cutDownId = cutDownId;
		}

		public int getLogId() {
			return logId;
		}

		public int[] getTreeId() {
			return treeId;
		}

		public int[] getCutDownId() {
			return cutDownId;
		}

		public int getPrice() {
			return price;
		}

		public void setPrice(int price) {
			this.price = price;
		}
	}

	public long lastTileCap = 0;

	//public Polygon tempWoodArea = null;

	public int logsChopped = 0;

	public int startLvl = 0, startXP = 0;

	public int[] nest_ids = {
	// Credits to whoever gathered these first
			5070, 5071, 5072, 5073, 5074, 5075, 5076, 7413, 11966 };

	RSObject nextTree = null;

	public long lastAntiban = 0;
	public long antibanDelay = 0;

	public boolean running = true;

	// ivy mode

	public final int[] IVY_ID = { 46318, 46320, 46322, 46324, 470, 670, 673,
			675 };
	public final int[] IVY_WALL_ID = { 23818, 11686, 28680, 36411, 17088}; // used to get approx
														// models of ivys
	// ge, north fala, taverley, castle wars, yanille added

	public boolean QCresponder = true;

	public ArrayList<String> messageQue = new ArrayList<String>();

	public int[] hatchetIds = { 1351, 1349, 1353, 1361, 1355, 1357, 1359, 6739 }; // bronze
																					// -
																					// dragon

	public bankHandler baHa = new bankHandler();

	HashMap<String, BufferedImage> PaintImages = new HashMap<String, BufferedImage>();

	TreeTracker tt = new TreeTracker();

	int mouseSpeed = 7;

	@Override
	public int getMouseSpeed() {
		return mouseSpeed + random(-2, 2);
	}

	TrayIcon trayIco = null;

	public boolean onStart(Map<String, String> args) {
		startPhase = StartPhase.ChooseMode;

		startLvl = skills.getCurrentLvl(STAT_WOODCUTTING);
		startXP = skills.getCurrentXP(STAT_WOODCUTTING);

		Bot.getEventManager().addListener(this, EventMulticaster.MOUSE_EVENT);
		Bot.getEventManager().addListener(this,
				EventMulticaster.MOUSE_MOTION_EVENT);

		new Thread(new PaintImageLoader()).start();
		new Thread(new Antiban()).start();

		rotateCamera = random(0, 10) < 5;
		// new Thread(tt).start(); // TODO experimental

		return true;
	}
	
	boolean rotateCamera = false;
	
	public class Antiban implements Runnable {

		@Override
		public void run() {
			while (isActive) {
				
				if (startPhase != StartPhase.Ready) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				
				int selectAntiban = random(0, 1000);

				int anim =player.getMine().getAnimation();
				
				debug("Antiban " + selectAntiban + " gambled");
				
				if (selectAntiban < 250) {
					if (anim != -1)
						mouse.move(random(0, 700), -1);
				}
				else if (selectAntiban < 300) {
					if (random(0, 10) < 5)
						mouse.move(random(0, 700), -1);
					sleepTime = random(1000, 5000);
				}
				/*
				else if (selectAntiban < 375) {
					
					if (anim != -1 && rotateCamera) {
						
						int rot = random(0, 360);
						
						if (treeSpot != null && chopBankIvy == WCMode.Ivy) {
							debug("Rotation antiban with ivy");
							Orientation o = treeSpot.getLocation().getOrientationTo(player.getMyLocation());
							if (o != null) {
								debug("o not null");
								int h = 0;
								for (Orientation or : Orientation.values()) {
									if (or.equals(o))
										break;
									h++;
								}
								debug("o is " + h + "th");
								h *= 8;
								h += 90;
								debug("newrot is " + h);
								rot = random(h-25, h+25);
								debug("finalrot is " + rot);
							}
						}
						
						setCameraRotation(rot);
					}
				}*/
				
				try {
					Thread.sleep(random(7000, 25000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public class PaintImageLoader implements Runnable {

		public void downloadAndExtract(URL sourceUrl, File folder) {
			try {
				ZipInputStream in = new ZipInputStream(sourceUrl.openStream());

				ZipEntry ze = null;

				while ((ze = in.getNextEntry()) != null) {
					FileOutputStream fout = new FileOutputStream(folder
							.getAbsolutePath()
							+ File.separator + ze.getName());
					for (int c = in.read(); c != -1; c = in.read()) {
						fout.write(c);
					}
					in.closeEntry();
					fout.close();
				}

				in.close();
			} catch (IOException e) {
			}
		}

		@Override
		public void run() {
			// String[] imgs = {"tool.png", "pen.png", "back.png", "pie.png",
			// "stats.png", "tag.png", "camera.png"};
			File allWcFolder = new File("Allwc");

			File allWcImgFolder = new File(allWcFolder.getAbsolutePath()
					+ File.separator + "img");

			if (!allWcFolder.exists())
				allWcFolder.mkdir();

			if (!allWcImgFolder.exists()) {
				int ret = JOptionPane
						.showConfirmDialog(
								null,
								"Press yes to allow downloading icon files from internet and cacheing them in a folder. If you press no, no icon files will be used in paint.",
								"Allow internet usage?",
								JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					allWcImgFolder.mkdir();
					try {
						downloadAndExtract(
								new URL(
										"http://dl.dropbox.com/u/18458187/allwcicons/zipimgs.zip"),
								allWcImgFolder);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else {
					return; // no need to try to even parse them because there
							// are none
				}
			}

			File[] imgs = allWcImgFolder.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".png");
				}

			});

			for (File f : imgs) {
				if (!f.exists()) {
					log(f.getName() + " not found");
					continue;
				}
				try {
					BufferedImage b = ImageIO.read(f);
					if (b != null)
						PaintImages.put(f.getName(), b);
				} catch (IOException ioe) {
					log("Error while loading " + f.getName() + ":");
					log(ioe.getMessage());
				}
			}

		}

	}

	public void onFinish() {
		running = false;
		if (trayIco != null)
			SystemTray.getSystemTray().remove(trayIco);
	}

	public class TreeTracker implements Runnable {

		HashMap<RSObject, Long> treesUp = new HashMap<RSObject, Long>();

		@Override
		public void run() {
			while (running) {
				if (selectedTree != null && tilepoints.size() > 1) {
					RSObject[] trees = getObjectsInAreaByID(selectedTree
							.getTreeId());
					for (RSObject t : trees) {
						treesUp.put(t, System.currentTimeMillis());
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private String cTime(long eTime) {
		final long hrs = eTime / 1000 / 3600;
		eTime -= hrs * 3600 * 1000;
		final long mins = eTime / 1000 / 60;
		eTime -= mins * 60 * 1000;
		final long secs = eTime / 1000;
		return String.format("%1$02d:%2$02d:%3$02d", hrs, mins, secs);
	}

	boolean togglePaint = true;

	final Color aliveTreeModelColor = new Color(0, 255, 0, 70);
	final Color hoveredTreeModelColor = new Color(255, 127, 0, 70);

	public void onRepaint(Graphics g) {

		if (!togglePaint) {

			if (PaintImages.containsKey("plus.png"))
				g.drawImage(PaintImages.get("plus.png"), 10, 10, null);
			else {
				g.setColor(Color.green);
				g.fillRect(10, 10, 20, 20);
			}
			return;
		}

		mw.draw(g);

		if (warnAboutOpenDoor != null) {
			g.setColor(Color.red);
			warnAboutOpenDoor.drawModel(g);
		}
		
		if (tempPathToWoods.size() > 0) {
			g.setColor(Color.pink);
			for (PathTile tp : tempPathToWoods) {
				if (tp == null)
					continue;
				if (tp.isObstacle()) {
					RSObject obj = objects.getTopAt(tp);
					if (obj != null && obj.getID() == tp.getObstacleId()) {
						obj.drawModel(g);
					}
				}
				else
					highlightTile(g, tp, Color.pink);
			}
		}
		if (pathToWoods != null && pathToWoods.length > 0) {
			g.setColor(Color.magenta);
			for (PathTile tp : pathToWoods) {
				if (tp == null)
					continue;
				if (tp.isObstacle()) {
					RSObject obj = objects.getTopAt(tp);
					if (obj != null && obj.getID() == tp.getObstacleId()) {
						obj.drawModel(g);
					}
				}
				else
					highlightTile(g, tp, Color.magenta);
			}
		}

		if (treeSpot != null) {
			colorMinimapTile(g, Color.blue, treeSpot.getLocation());
		}

		if (tilepoints.size() > 1) {

			
			/*
			 * TODO maybe add again?
			for (RSTile lol : tilepoints) {
				if (lol != null) {
					if (startPhase != StartPhase.Ready)
						colorMinimapTile(g, Color.red, lol);

					final Point l = tileToMinimap(lol);
					if (l.x != -1 && l.y != -1)
						tempWoodArea.addPoint(l.x, l.y);
				}
			}
			*/
			
			drawTilepoints(g, Color.green);
		}

		if (selectedTree != null && tilepoints.size() > 1) {
			final RSObject[] trees = getObjectsInAreaByID(selectedTree
					.getTreeId());
			for (RSObject t : trees) {
				drawOnTree(g, t, new Color(0, 255, 0, 100));
			}

			final RSObject[] cuttedDownt = getObjectsInAreaByID(selectedTree
					.getCutDownId());
			for (RSObject t : cuttedDownt) {
				drawOnTree(g, t, new Color(255, 0, 0, 100));
			}
		}

		/*
		 * TODO
		 * 
		 * for (RSObject tree : tt.treesUp.keySet()) { if (tree == null)
		 * continue; Point scr = Calculations.tileToScreen(tree.getLocation());
		 * if (scr.x == -1 || scr.y == -1) continue;
		 * g.drawString((System.currentTimeMillis()-tt.treesUp.get(tree))+"",
		 * scr.x, scr.y); }
		 */

		if (treeSpot != null) {
			g.setColor(aliveTreeModelColor);
			treeSpot.drawModel(g);
		}
		
		if (hovered != null) {
			g.setColor(hoveredTreeModelColor);
			hovered.drawModel(g);
		}

		if (startPhase == StartPhase.SelectTreeType) {
			final Tree[] trees = Tree.values();
			if (mw.hoveredOption != -1 && trees.length > mw.hoveredOption) {
				final int[] hoveredId = trees[mw.hoveredOption].getTreeId();
				final int[] hoveredDeadId = trees[mw.hoveredOption]
						.getCutDownId();

				final RSObject[] hoveredTrees = getObjectsByID(null, hoveredId);
				for (RSObject t : hoveredTrees) {
					drawOnTree(g, t, new Color(0, 255, 0, 100));
				}

				final RSObject[] hoveredDeadTrees = getObjectsByID(null, hoveredDeadId);
				for (RSObject t : hoveredDeadTrees) {
					drawOnTree(g, t, new Color(255, 0, 0, 100));
				}
			}
		}

	}
	
	public void drawTilepoints(Graphics g, Color c) {
		
		Polygon mapPoly = new Polygon();
		
		RSTile[] tiles = tilepoints.getTileArray();
		
		for (RSTile t : tiles) {
			Point p = t.getMapLocation();
			if (p.x == -1 || p.y == -1)
				continue;
			mapPoly.addPoint(p.x, p.y);
		}
		
		if (mapPoly.npoints == 0)
			return;
		
		g.setColor(c);
		g.drawPolygon(mapPoly);
		g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
		g.fillPolygon(mapPoly);
	}

	public RSTile getTileByLoc(int x, int y) {
		RSTile me = player.getMyLocation();
		for (int xx = -26; xx < 26; xx++) {
			for (int yy = -26; yy < 26; yy++) {
				RSTile t = new RSTile(me.getX() + xx, me.getY() + yy);
				
				if (distanceBetweenInt(t.getMapLocation(), x, y) < 4) {
					return new RSTile(me.getX() + xx, me.getY() + yy);
				}
			}
		}
		return null;
	}

	public static int distanceBetweenInt(Point p, int x, int y) {
		return (int) Math.hypot(p.x - x, p.y - y);
	}

	public boolean colorMinimapTile(Graphics g, Color c, RSTile t) {
		g.setColor(c);

		final Point p = t.getMapLocation();
		if (p.x != -1 || p.y != -1) {
			g.fillRect(p.x + 1, p.y + 1, 3, 3);
			return true;
		}
		return false;
	}

	public boolean areaVisible() {
		int all = tilepoints.size();
		int visible = 0;
		for (RSTile r : tilepoints.getTileArray()) {
			if (r.isOnMinimap())
				visible++;
		}
		return visible > all / 10;
	}
	
	public void highlightTile(final Graphics g, final RSTile t,
			final Color outline) {
		if (t == null)
			return;

		final Color fill = new Color(outline.getRed(), outline.getGreen(),
				outline.getBlue(), 50);

		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		if (py.x == -1 || pxy.x == -1 || px.x == -1 || pn.x == -1) {
			return;
		}
		g.setColor(outline);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.setColor(fill);
		g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
	}

	public long lastQC = 0;

	public long lastCall = 0;
	
	public int sleepTime = 0;

	public int loop() {
		if (sleepTime > 0) {
			debug("Executing sleepTime of " + sleepTime);
			wait(sleepTime);
			debug("sleepTime completed");
			sleepTime = 0;
		}
		lastCall = System.currentTimeMillis();
		return newLoop();
	}
	
	RSObject hovered = null;
	
	boolean usedYanilleIvyFix = false;
	
	public RSObject findIvyWall(RSTile tile, boolean yanilleFix) {
		RSObject[] objectsUnder = objects.getAt(tile);
		RSObject wall = null;
		for (RSObject und : objectsUnder) {
			for (int id : IVY_WALL_ID) {
				if (und.getID() == id)
					wall = und;
			}
		}
		if (wall == null && !yanilleFix)
			return findIvyWall(new RSTile(tile.getX(), tile.getY()-1), true);
		
		if (wall != null)
			usedYanilleIvyFix = yanilleFix;
		
		return wall;
	}
	
	int ivyFailsafe = 0;
	RSTile latestIvyTile = null;
	boolean reChopInstantly = false;
	
	boolean doorDetection = false;
	
	RSObject warnAboutOpenDoor = null;

	public int newLoop() {
		if (startPhase == StartPhase.MakePath) {
			
			if (doorDetection) {
				RSObject door = objects.getNearestByName("Door", "Gate");
				if (door != null && door.distanceTo() < 2) {
					PathTile constructTile = new PathTile(door.getLocation());
					constructTile.setObstacle(door.getID());
					
					if (!tempPathToWoods.contains(constructTile)) {
						String[] defact = door.getDef().getActions();
						if (defact != null && defact[0].equals("Close")) {
							warnAboutOpenDoor = door;
						}
						else {
							tempPathToWoods.add(constructTile);
							log("Door detected unadded");
							warnAboutOpenDoor = null;
						}
					}
				}
				else {
					warnAboutOpenDoor = null;
				}
			}
			
			if (warnAboutOpenDoor != null)
				return 500;
			
			PathTile ploc = new PathTile(player.getMyLocation());
			if (!tempPathToWoods.contains(ploc)) {
				tempPathToWoods.add(ploc);
			}
			
			return 500;
		}
		if (startPhase != StartPhase.Ready)
			return 100;
		
		if (messageQue.size() > 0) {
			if (lastQC < System.currentTimeMillis() - 35000) {
				debug("QCMessage detected");
				detectQC(messageQue.remove(0));
				debug("detectQC dispatched");
				lastQC = System.currentTimeMillis();
				return random(500, 1000);
			}
			else {
				messageQue.clear();
			}
		}
		
		if (!isRunning() && player.getMyEnergy() > 80) {
			debug("Setting run");
			game.setRun(true);
			debug("Run set");
		}
		
		RSGroundItem nest = ground.getNearestItemByID(nest_ids);
		if (nest != null && !inventory.isFull() && chopBankIvy != WCMode.Powerlevel) {
			debug("Nest detected");
			if (nest.isOnScreen()) {
				debug("Adding some surprisesleeping to nest clicking to make it more humanlike");
				wait(random(500, 1500));
				nest.action("Take", "Bird");
				debug("Attempting to pick up a bird nest");
				waitUntilMove(1000);
				return random(500, 1000);
			}
			else {
				debug("Adding some surprisesleeping to nest finding to make it more humanlike");
				wait(random(1500, 2500));
				walk.to(nest);
				debug("Attempting to walk to bird nest");
				waitUntilMove(1000);
				return random(500, 1000);
			}
		}
		
		if (inventory.isFull()) {
			if (chopBankIvy == WCMode.Powerlevel) {
				debug("Dropping stuff in inventory");
				if (useMousekeys)
					mouseKeysDrop();
				else
					normalDrop();
				return 100;
			} else if (chopBankIvy == WCMode.Bank) {
				if (!baHa.bankOnScreen()) {
					if (bankTile.isOnMinimap()) {
						walk.to(bankTile);
						
						player.waitToMove(1000);
						while (player.getMine().isMoving() && !baHa.bankOnScreen())
							wait(random(100, 200));
						
						return random(100, 200);
					} else {
						walk.pathMM(walk.reversePath(pathToWoods));
						return random(500, 1000);
					}
				} else {
					if (!baHa.bankOpen())
						baHa.openBank();
					if (baHa.waitForBank(3000)) {
						wait(random(300, 600));
						baHa.depositLogs();
					}
					return random(500, 1000);
				}
			}
		}
		
		if (chopBankIvy == WCMode.Ivy) {
			RSObject ivy = objects.getNearestByID(IVY_ID);

			if (ivy != null) {
				RSObject wall = findIvyWall(ivy.getLocation(), false);
				if (wall == null) {
					debug("Wall null but ivy not?");
				} else {
					if (latestIvyTile == null)
						latestIvyTile = player.getMyLocation();
					if (player.getMine().getAnimation() == -1) {
						treeSpot = wall; //wall object so we get epic paints
						if (wall.distanceTo() > 4) {
							walk.to(wall.getLocation());
							return random(1000, 1500);
						}
						if (player.waitForAnim(850) == -1 || reChopInstantly) {
							if (usedYanilleIvyFix) {
								Point p = Calculations.tileToScreen(wall.getLocation(), random(-150, -543));
								if (p.x != -1 && p.y != -1) {
									mouse.move(p);
									menu.action("Chop");
								}
							}
							else {
								try {
									Point p = wall.getClickableModelPoint();
									if (p == null || p.x == -1 || p.y == -1 || !atPoint(p, "Chop")) {
										reChopInstantly = true;
										return random(100, 350);
									}
									reChopInstantly = false;
								}
								catch (ArrayIndexOutOfBoundsException e) {
									debug("aioobe in ivychop");
								}
							}
						}
						return random(1000, 1800);
					}
				}
				ivyFailsafe = 0;
			} else {
				if (latestIvyTile != null) {
					debug("Attempting to walk back to ivy tile");
					walk.to(latestIvyTile);
					return random(1000, 2500);
				}
				if (ivyFailsafe > 30) {
					log("ivy not found in 30 seconds. Stopping script");
					debug("ivy not found in 30 seconds. Stopping script");
					stopScript(true);
					return -1;	
				}
				debug("Ivy null? " + ivyFailsafe++);
				return 1000;
			}
		}
		else {
			if (!tilepoints.contains(player.getMyLocation())
					|| (woodsTile != null && woodsTile.distanceTo() > 25)
					|| baHa.bankOpen()) {

				
				if ((woodsTile != null && woodsTile.isOnMinimap()) && pathToWoods.length < 2) {
					pathToOrigSpot = new PathTile[]{new PathTile(woodsTile)};
					debug("walk path = woodsTile");
				}
				else if (pathTileOnMM(pathToWoods)) {
					pathToOrigSpot = pathToWoods;
					debug("walk path = pathToWoods");
				} else {
					debug("walk path = generated");
					RSTile[] rstpath = walk.cleanPath(walk.generateFixedPath(woodsTile));
					PathTile[] ptpath = new PathTile[rstpath.length];
					for (int n = 0;n < rstpath.length; n++) {
						ptpath[n] = new PathTile(rstpath[n]);
					}
					pathToOrigSpot = ptpath;
				}
				debug("Attempting to walk to woodsTile");
				PathTile nt = findNextTile(pathToOrigSpot);
				if (nt == null)
					debug("nt null!!");
				else {
					if (nt.isObstacle()) {
						RSObject ntobj = objects.getTopAt(nt);
						if (ntobj != null && ntobj.getID() == nt.getObstacleId()) {
							if (ntobj.isOnScreen())
								ntobj.action("Open");
							else
								walk.to(nt);
						}
						return random(400, 800);
					}
					
					if (nt.isOnScreen() && !baHa.bankOpen())
						walk.tileOnScreen(nt);
					else
						walk.tileMM(nt);
					
				}
				return random(1000, 2000);
			}
			
			int anim =player.getMine().getAnimation();
			
			
			
			
			if (anim == -1) {
				treeSpot = getNearestObjectByIDInArea(selectedTree.getTreeId());
				if (treeSpot != null) {
					if (treeSpot.isOnScreen()) {
						debug("Treespot on screen, clicking");
						
						boolean success = false;
						
						try {
							if (treeSpot.action("Chop")) {
								hovered = null;
								success = true;
							}
						}
						catch (ArrayIndexOutOfBoundsException e) {
							debug("aioobe on .action" + e.toString());
							debug("Using atObject as fallback method");
							
							if (treeSpot.action("Chop")) {
								hovered = null;
								success = true;
							}
						}
						
						if (!success) {
							return random(100, 400);
						}
						
						debug("moving");
						waitUntilMove(1000);
						return random(700, 1000);
					}
					else {
						walk.to(treeSpot.getLocation());
						if (player.waitToMove(1000))
							while (player.getMine().isMoving() && !treeSpot.isOnScreen())
								wait(random(200, 350));
						return random(200, 400); // as quick click on tree after walk
					}
				}
			}
			nextTree = getNearestObjectByIDInAreaExcluding(false, treeSpot,
					selectedTree.getTreeId());
			
			if (anim != -1 && nextTree != null && (hovered == null || distCalc(false, hovered, nextTree) > 1.6)){
				if (nextTree != null) {
					Point p = null;
					
					if (nextTree.isOnScreen()) {
						try {
							p = nextTree.getClickableModelPoint();
						}
						catch (ArrayIndexOutOfBoundsException e) {
							debug("aioobe on nt point " + e.toString());
							debug("using location's screenpoint as fallback");
							p = nextTree.getLocation().getScreenLocation();
							
						}
					}
					else if (nextTree.isOnMinimap()) {
						p = nextTree.getMapLocation();
					}
					
					if (p != null && p.x != -1 && p.y != -1) {
						mouse.move(p);
						hovered = nextTree;
					}
				}
			}
		}
		
		
		return 100;
	}
	
	public boolean atPoint(Point p, String action) {
		mouse.move(p);
		return menu.action(action);
	}
	
	public boolean waitUntilMove(int timeout) {
		if (player.waitToMove(timeout)) {
			while(player.getMine().isMoving())
				wait(100);
			return true;
		}
		return false;
	}
	
	public PathTile findNextTile(PathTile[] path) {
		PathTile lastlastOnMM = null;
		PathTile lastOnMM = null;
		PathTile lastWithObject = null;

		for (PathTile tile : path) { // TODO fix
			
			if (tile.isOnMinimap()) {
				if (tile.isObstacle()) {
					RSObject obj = objects.getTopAt(tile);
					if (obj != null && obj.getID() == tile.getObstacleId()) {
						lastWithObject = tile;
					}
					else {
						log("obst not valid");
					}
					log("obs found tho");
				}
				else {
					if (lastOnMM != null)
						lastlastOnMM = lastOnMM;
					lastOnMM = tile;
				}
			}
		}
		if (lastWithObject != null &&
				(lastlastOnMM == null || lastlastOnMM.distanceTo() > lastWithObject.distanceTo()))
			return lastWithObject;
		if (lastlastOnMM != null)
			return lastlastOnMM;
		return lastOnMM;
	}

	public boolean pathTileOnMM(RSTile[] path) {
		for (RSTile tile : path) {
			if (tile != null && tile.isOnMinimap())
				return true;
		}
		return false;
	}

	public int[] toIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		int i = 0;
		for (Integer e : list)
			ret[i++] = e.intValue();
		return ret;
	}

	public void normalDrop() {
		int tries = 40;
		int[] invArray = inventory.getArray();
		ArrayList<Integer> dropThese = new ArrayList<Integer>();
		int[][] dontDrop = { nest_ids, hatchetIds };
		for (int i : invArray) {
			boolean doDrop = true;
			for (int[] dd : dontDrop) {
				for (int d : dd) {
					if (d == i) {
						doDrop = false;
					}
				}
			}
			if (doDrop && !dropThese.contains(i))
				dropThese.add(i);
		}

		int[] dropTheseArray = toIntArray(dropThese);

		while (tries > 0 && inventory.contains(dropTheseArray)) {
			for (int i : dropTheseArray) {
				if (inventory.contains(i)) {
					inventory.clickItem(i, "Drop");
					break;
				}
			}
			tries--;
			wait(random(150, 300));
		}
	}

	public void mouseKeysDrop() {
		Mouse m = Bot.getClient().getMouse();
		wait(random(500, 1000));

		long start = System.currentTimeMillis();

		int[][] dontDrop = { nest_ids, hatchetIds };

		int[] ar = inventory.getArray();

		int dropped = 0;

		for (int xrow = 0; xrow < 4; xrow++) {
			boolean skipToNext = false;
			for (int[] dd : dontDrop) {
				for (int d : dd) {
					if (ar[0 + xrow] == d) {
						skipToNext = true;
					}
				}
			}
			mouse.move(random(575, 585) + (xrow * 40), (skipToNext ? random(260,
					268) : random(221, 230)));

			wait(random(500, 700));
			for (int yrow = (skipToNext ? 1 : 0); yrow < 6; yrow++) {
				int thisID = ar[getArLocation(xrow, yrow)];
				boolean skipThis = false;
				for (int[] dd : dontDrop) {
					for (int d : dd) {
						if (thisID == d) {
							skipThis = true;
						}
					}
				}

				if (!skipThis) {
					mouse.click(false);
					mouse.move(-99, m.getPressX(), m.getPressY() + 37, 0, 0);
					mouse.click(true);
					dropped++;
				} else {
					mouse.move(m.getX(), m.getY() + random(30, 40));
				}
				wait(random(100, 200));
			}
			wait(random(500, 700));
		}
		debug("Dropped " + dropped + " logs in "
				+ (System.currentTimeMillis() - start) + " ms. Problem, Jagex?");
	}

	public int getArLocation(int x, int y) {
		return y * 3 + y + x;
	}

	public RSObject getNearestObjectByIDInAreaExcluding(boolean doNull,
			RSObject excluding, int... ids) {
		RSObject cur = null;
		double dist = -1;
		boolean curVisible = false;
		
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject o = objects.getTopAt(x + Bot.getClient().getBaseX(), y
						+ Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (int id : ids) {
						if (o.getID() == id) {
							isObject = true;
						}
					}
					if (isObject
							&& tilepoints.contains(o.getLocation())
							&& distCalc(doNull, o, excluding) > 1.6) {
						double distTmp = player.getMyLocation().distanceTo(o.getLocation());
						boolean thisVisible = o.isOnScreen();
						if (cur == null || distTmp < dist || (!curVisible && thisVisible)) {
							dist = distTmp;
							cur = o;
							curVisible = thisVisible;
						}
						
					}
				}
			}
		}
		return cur;
	}

	public void drawOnTree(Graphics g, RSObject tree, Color c) {
		Point ptree = tree.getMapLocation();
		if (ptree.x != -1 && ptree.y != -1) {
			g.setColor(c);
			g.fillOval(ptree.x - 5, ptree.y - 5, 10, 10);
		}
	}

	public RSObject[] getObjectsInAreaByID(int... ids) {
		return getObjectsByID(tilepoints, ids);
	}
	
	public RSObject[] getObjectsByID(RSPolygon area, int... ids) {
		ArrayList<RSObject> cur = new ArrayList<RSObject>();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject o = objects.getTopAt(x + Bot.getClient().getBaseX(), y
						+ Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (int id : ids) {
						if (o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject
							&& (area == null || area.contains(o.getLocation()))) {
						cur.add(o);
					}
				}
			}
		}
		List<RSObject> list = cur;
		return list.toArray(new RSObject[list.size()]);
	}

	public double distCalc(boolean doNull, RSObject o, RSObject b) {
		if (doNull)
			return 2;
		if (o == null || b == null)
			return 0;
		return b.getLocation().distanceTo(o.getLocation());
	}

	public boolean isSameLoc(RSObject one, RSObject two) {
		if (one == null || two == null)
			return false;
		RSTile onee = one.getLocation();
		RSTile twoo = two.getLocation();
		return onee.getX() == twoo.getX() && onee.getY() == twoo.getY();
	}

	public RSObject getNearestObjectByIDInArea(int... ids) {
		return getNearestObjectByIDInAreaExcluding(true, null, ids);
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		final String mess = e.getMessage();
		if (e.getID() == MessageEvent.MESSAGE_ACTION
				&& (mess.contains("You get some") || mess
						.contains("chop away some ivy"))) {
			logsChopped++;
		} else if (e.getID() == 17) { 
			messageQue.add(e.getMessage());
		}
		
		// 17 == quick chat
		
		if (e.getID() == 17 || e.getID() == MessageEvent.MESSAGE_CHAT || e.getID() == MessageEvent.MESSAGE_PRIVATE_IN) {
			if (trayIco != null) {
				trayIco.displayMessage("AllWcer chat msg!", e.getSender()
						+ ": " + e.getMessage(), TrayIcon.MessageType.WARNING);
			}
		}
		debug("chat:" + e.getID() + ":" + e.getSender() + ":" + e.getMessage());
	}

	public void debug(String msg) {
		detailsWindow.addDebugMsg(msg);
	}

	public RSPlayer getNearestPlayerByLevelNotMe(int min, int max) {
		int Dist = 20;
		RSPlayer closest = null;
		int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		org.rsbot.client.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();

		for (int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			RSPlayer aplayer = new RSPlayer(players[element]);
			try {
				if (aplayer.getCombatLevel() < min
						|| aplayer.getCombatLevel() > max
						|| aplayer == player.getMine()) {
					continue;
				}
				int distance = aplayer.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = aplayer;
				}
			} catch (Exception ignored) {
			}
		}
		return closest;
	}

	public class bankHandler {
		public RSNPC banker = null;
		public RSObject bankDeposit = null;
		public RSObject bankBooth = null;

		public boolean bankExists() {
			setBanks();
			if (banker != null)
				return true;
			if (bankDeposit != null)
				return true;
			if (bankBooth != null)
				return true;
			return false;
		}

		public boolean bankNear(int dist) {
			if (!bankExists())
				return false;
			if (banker != null && banker.distanceTo() < dist)
				return true;
			if (bankDeposit != null && bankDeposit.distanceTo() < dist)
				return true;
			if (bankBooth != null && bankBooth.distanceTo() < dist)
				return true;
			return false;
		}

		public boolean bankOpen() {
			if (bank.isOpen())
				return true;
			if (iface.get(11).isValid())
				return true;
			return false;
		}

		public boolean openBank() {
			if (banker != null && banker.isOnScreen()) {
				npc.action(banker, "Bank Banker");
				return true;
			}
			if (bankDeposit != null && bankDeposit.isOnScreen()) {
				bankDeposit.action("Deposit ");
				return true;
			}
			if (bankBooth != null && bankBooth.isOnScreen()) {
				bank.open();
				return true;
			}
			return false;
		}

		public void depositLogs() {
			if (iface.get(INTERFACE_BANK).isValid()) {
				bank.depositAllExcept(hatchetIds);
			} else if (iface.get(11).isValid()) {
				RSInterfaceChild[] items = iface.getChild(11, 17)
						.getChildren();
				boolean depositAll = true;
				hatchetChecker: for (RSInterfaceChild i : items) {
					for (int ha : hatchetIds) {
						if (ha == i.getChildID()) {
							depositAll = false;
							break hatchetChecker;
						}
					}
				}
				if (depositAll) {
					iface.clickChild(11, 18);
				} else {
					singleItemDepositer: for (RSInterfaceChild i : items) {
						if (i.getChildID() == -1)
							continue;

						// boolean isHatchet = false;

						for (int ha : hatchetIds) {
							if (ha == i.getChildID()) {
								// isHatchet = true;
								continue singleItemDepositer;
							}
						}

						// if (isHatchet)
						// continue;

						atInterfaceComponent(i, "Deposit-All");
						try {
							wait(random(600, 800));
						} catch (InterruptedException e) {
						} catch (IllegalMonitorStateException e2) {

						}
					}
				}
				for (int i = 0; i < 10; i++) {
					if (!iface.get(11).isValid()) {
						// log("depositer not valid anymore :)");
						break;
					}
					// log("trying to hit close btn");
					iface.clickChild(17, 11);
					try {
						wait(random(100, 400));
					} catch (InterruptedException e) {
					} catch (IllegalMonitorStateException e2) {

					}
				}

			}
		}

		public boolean atInterfaceComponent(RSInterfaceChild i,
				String actionContains) {
			if (!i.isValid())
				return false;
			Rectangle pos = i.getArea();
			if (pos.x == -1 || pos.y == -1 || pos.width == -1
					|| pos.height == -1)
				return false;
			int dx = (int) (pos.getWidth() - 4) / 2;
			int dy = (int) (pos.getHeight() - 4) / 2;
			int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
			int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
			mouse.move(midx + random(-dx, dx), midy + random(-dy, dy));
			try {
				wait(random(50, 60));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IllegalMonitorStateException e2) {

			}
			return menu.action(actionContains);
		}

		public boolean waitForBank(int timeout) {
			if (timeout < 0)
				return false;

			RSInterface iface1 = iface.get(INTERFACE_BANK);
			RSInterface iface2 = iface.get(11);

			long startTime = System.currentTimeMillis();

			while (System.currentTimeMillis() - startTime <= timeout) {
				if (iface1.isValid() || iface2.isValid())
					return true;
				try {
					wait(125);
				} catch (InterruptedException e) {

				} catch (IllegalMonitorStateException e2) {

				}
			}

			return false;
		}

		public boolean bankOnScreen() {
			if (!bankExists())
				return false;
			if (banker != null && banker.isOnScreen())
				return true;
			if (bankDeposit != null && bankDeposit.isOnScreen())
				return true;
			if (bankBooth != null && bankBooth.isOnScreen())
				return true;
			return false;
		}

		public void setBanks() {
			banker = npc.getNearestByID(Bank.Bankers);
			bankDeposit = objects.getNearestByID(Bank.BankDepositBox);
			int[] bothID = intFromTwo(Bank.BankBooths, Bank.BankChests);
			bankBooth = objects.getNearestByID(bothID);
		}

		public int[] intFromTwo(int[] one, int[] two) {
			ArrayList<Integer> wut = new ArrayList<Integer>();
			for (int s : one) {
				wut.add(s);
			}
			for (int b : two) {
				wut.add(b);
			}
			List<Integer> list = wut;
			return toIntArray(list);
		}

		int[] toIntArray(List<Integer> list) {
			int[] ret = new int[list.size()];
			int i = 0;
			for (Integer e : list)
				ret[i++] = e.intValue();
			return ret;
		}

	}

	// QC handler below

	public void detectQC(String msg) {
		ArrayList<String> greetings = new ArrayList<String>();
		greetings.add("Hey");
		greetings.add("Hello");
		greetings.add("Hi");
		greetings.add("Good day");
		greetings.add("Hey!");
		greetings.add("Yo");
		/*
		 * String[] parseNameMsg = msg.split("<img=3>:"); if
		 * (parseNameMsg.length <= 1) { log("parseNameMsg failed"); return; }
		 * //String user = parseNameMsg[0]; String message =
		 * parseNameMsg[1].replace(" <col=0000ff>", "");
		 */

		String message = msg;

		boolean answered = false, skipped = false;
		if (message.contains("level is")) {
			int parseLevel = Integer.parseInt(message.split("level is ")[1]
					.replace(".", ""));
			if (parseLevel < 60) {
				int wannaSend = random(0, 2);
				if (wannaSend == 1) {
					sendQC(KeyEvent.VK_G, KeyEvent.VK_R, KeyEvent.VK_3);
					answered = true;
				} else if (wannaSend == 2) {
					sendQC(KeyEvent.VK_G, KeyEvent.VK_R, KeyEvent.VK_M,
							KeyEvent.VK_M, KeyEvent.VK_4);
				} else {
					skipped = true;
				}
			} else {
				sendQC(KeyEvent.VK_G, KeyEvent.VK_O, KeyEvent.VK_G, randomFrom(
						KeyEvent.VK_1, KeyEvent.VK_0));
				answered = true;
			}
		} else if (message.contains("can only use Quick")) { // Cerberus -.-
			sendQC(KeyEvent.VK_G, KeyEvent.VK_O, KeyEvent.VK_G, randomFrom(
					KeyEvent.VK_1, KeyEvent.VK_0));
			answered = true;
		} else if (message.contains("hat is your level in")) {
			String parseLevelName = message.split("your level in ")[1].replace(
					"?", "");
			sendAnswerForSkill(parseLevelName);
			answered = true;
		} else if (stringContainsAnother(message, greetings)) {
			sendQC(KeyEvent.VK_G, KeyEvent.VK_H, randomFrom(KeyEvent.VK_1,
					KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_6, KeyEvent.VK_8));
			answered = true;
		} else if (message.contains("ow are you")) {
			sendQC(KeyEvent.VK_G, KeyEvent.VK_M, randomFrom(KeyEvent.VK_1,
					KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5,
					KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8));
			answered = true;
		}
		if (answered)
			debug("Answered to " + message);
		if (skipped)
			debug(message + " skipped because of gamble/something else");
	}

	public boolean stringContainsAnother(String a, ArrayList<String> b) {
		for (String lulz : b) {
			if (a.contains(lulz))
				return true;
		}
		return false;
	}

	public char getLetterForSkill(String skill) {
		debug("Searching for letter for " + skill);
		char letter = (char) KeyEvent.VK_J; // default for woodcutting
		RSInterfaceChild letterz = iface.get(137).getChild(20);
		for (int i = 0; i < letterz.getChildren().length; i++) {
			String[] both = letterz.getChildren()[i].getText().split(
					". <col=000000>");
			String skillname = both[1].split(" <img")[0];
			if (skillname.equals(skill)) {
				String one = both[0].substring(both[0].indexOf(">") + 1);
				// log("Found letter " + one + " for " + skill);
				letter = one.toCharArray()[0];
			}
			debug(skillname + " searched");
		}
		return letter;
	}

	public int randomFrom(int... rands) {
		// in random(x, y) y is exclusive therefore no need to subtract from rands.length
		return rands[random(0, rands.length)];
		/*
		ArrayList<Integer> lulz = new ArrayList<Integer>();
		for (int u : rands)
			lulz.add(u);
		Collections.shuffle(lulz);
		return lulz.get(0);
		*/
	}

	public void sendQC(int... msgs) {
		input.sendKey((char) KeyEvent.VK_ENTER);
		wait(random(300, 500));
		for (int one : msgs) {
			input.sendKey((char) one);
			wait(random(700, 2300));
		}
	}

	public void sendAnswerForSkill(String skill) {
		input.sendKey((char) KeyEvent.VK_ENTER);
		wait(random(300, 500));
		input.sendKey((char) KeyEvent.VK_S);
		wait(random(1000, 2000));
		input.sendKey(getLetterForSkill(skill));
		wait(random(1000, 2000));
		input.sendKey((char) KeyEvent.VK_2);
		wait(random(1000, 2000));
	}

	// QC handler above

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	Point lastDragLoc = null;
	int lastDragBtn = 0;

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (!togglePaint)
			return;
		lastDragLoc = null;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (!togglePaint) {
			if (arg0.getX() > 10 && arg0.getX() < 30 && arg0.getY() > 10
					&& arg0.getY() < 30)
				togglePaint = true;
			return;
		}
		
		mw.curopt.mousePress(arg0);

		if (startPhase == StartPhase.SelectTrees && inMM(arg0.getPoint()))
			tilepoints.reset();

		lastDragBtn = arg0.getButton();
		mw.dispatchClick();
		mw.checkHoveredLocation(arg0.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (!togglePaint)
			return;
		lastDragLoc = null;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (!togglePaint)
			return;

		boolean doMove = lastDragBtn == MouseEvent.BUTTON1;

		if (lastDragLoc != null) {
			if (startPhase == StartPhase.SelectTrees && inMM(arg0.getPoint())) {
				RSTile watnao = getTileByLoc(arg0.getX(), arg0.getY());
				if (watnao != null && !tilepoints.contains(watnao))
					tilepoints.addTile(watnao);
			} else if (doMove && mw.contains(arg0.getPoint())) {
				mw.x = mw.x + arg0.getX() - lastDragLoc.x;
				mw.y = mw.y + arg0.getY() - lastDragLoc.y;
			} else if (!doMove && mw.contains(arg0.getPoint())) {
				mw.resize(mw.width + arg0.getX() - lastDragLoc.x, mw.height
						+ arg0.getY() - lastDragLoc.y);
			}
		}
		lastDragLoc = arg0.getPoint();
	}

	public boolean inMM(Point p) {
		return new Rectangle(550, 5, 155, 157).contains(p);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (!togglePaint)
			return;
		
		mw.curopt.mouseMove(arg0);

		mw.checkHoveredLocation(arg0.getPoint());
	}

	MiniWindow mw = new MiniWindow();

	// Window constants
	DetailsWindow detailsWindow = new DetailsWindow();

	ChooseModeWindow chooseModeWindow = new ChooseModeWindow();
	LoadPreviousWindow loadPreviousWindow = new LoadPreviousWindow();
	UseMouseKeysWindow useMouseKeysWindow = new UseMouseKeysWindow();
	DetectBankWindow detectBankWindow = new DetectBankWindow();
	WalkToTreesWindow makePathWindow = new WalkToTreesWindow();
	SelectTreeTypeWindow selectTreeTypeWindow = new SelectTreeTypeWindow();
	SelectTreesWindow selectTreesWindow = new SelectTreesWindow();
	UseQCResponderWindow useQCResponderWindow = new UseQCResponderWindow();
	UseChatNotifierWindow useChatNotifierWindow = new UseChatNotifierWindow();
	SaveConfigWindow saveConfigWindow = new SaveConfigWindow();

	// end window constants

	public class MiniWindow {
		int x = 10;
		int y = 10;
		int width = 480;
		int height = 220;

		int optionHeight = 40;

		int hoveredOption = -1;

		int transparency = 100;

		public void resize(int w, int h) {
			if (w > 149)
				width = w;
			if (h > 149)
				height = h;
		}

		WindowOptions curopt = new ChooseModeWindow();

		public void draw(Graphics g) {
			g.setColor(new Color(0, 0, 0, transparency));
			g.fillRect(x, y, width, height);

			drawText(g);
			drawOptions(g);
		}

		private void drawText(Graphics g) {
			if (curopt == null)
				return;
			ArrayList<String> txt = curopt.getText();
			int cury = y + 15;
			g.setColor(Color.white);
			boolean continuingmsg = false;
			for (String t : txt) {
				if (!continuingmsg) {
					g.drawString(t, x + 5, cury);
					cury += 15;
				} else {
					g
							.drawString("Resize paint for more details..",
									x + 5, cury);
					break;
				}

				if (cury + 5 > y + height - optionHeight) {
					continuingmsg = true;
				}
			}
		}

		private void drawOptions(Graphics g) {
			if (curopt == null)
				return;
			ArrayList<WindowOption> opt = curopt.getOptions();
			if (opt.size() == 0)
				return;
			int onewidth = width / opt.size();
			for (int xloc = 0; xloc < opt.size(); xloc++) {
				if (xloc == hoveredOption)
					g.setColor(new Color(50, 205, 50, 180));
				else
					g.setColor(new Color(255, 255, 255, transparency));
				int hx = x + xloc * onewidth;
				g.fillRect(hx, y + height - optionHeight, onewidth,
						optionHeight);

				int textX = hx + 8;

				BufferedImage icon = opt.get(xloc).getIcon();
				if (icon != null) {
					g.drawImage(icon, hx + 5, y + height - (optionHeight / 3)
							- 15/* 22approxheight */, null);
					textX += icon.getWidth() + 3;
				}

				g.setColor(Color.GREEN);
				g.drawString(opt.get(xloc).getText(), textX, y + height
						- (optionHeight / 3) - 4);

			}
		}

		public void checkHoveredLocation(Point p) {
			if (curopt == null)
				return;
			ArrayList<WindowOption> opt = curopt.getOptions();
			if (opt.size() == 0)
				return;
			int onewidth = width / opt.size();
			for (int xloc = 0; xloc < opt.size(); xloc++) {
				if (new Rectangle(x + xloc * onewidth, y + height
						- optionHeight, onewidth, optionHeight).contains(p)) {
					hoveredOption = xloc;
					return;
				}
			}
			hoveredOption = -1;
		}

		public void dispatchClick() {
			if (curopt == null || curopt.getOptions().size() == 0)
				return;
			if (hoveredOption == -1
					|| hoveredOption > curopt.getOptions().size())
				return;
			StartPhase sp = curopt.onPress(curopt.getOptions().get(
					hoveredOption));
			if (sp != null && !sp.equals(startPhase)) {
				WindowOptions wo = getWindowByPhase(sp);
				if (wo != null) {
					startPhase = sp;
					curopt = wo;
				}
			}
		}

		public boolean contains(Point p) {
			return p.x > x && p.x < x + width && p.y > y && p.y < y + height;
		}

		public void toggleAlpha() {
			transparency = 290 - transparency;
		}

		public WindowOptions getWindowByPhase(StartPhase sp) {
			if (sp == StartPhase.ChooseMode)
				return chooseModeWindow;
			else if (sp == StartPhase.LoadPrevious)
				return loadPreviousWindow;
			else if (sp == StartPhase.UseMouseKeys)
				return useMouseKeysWindow;
			else if (sp == StartPhase.DetectBank)
				return detectBankWindow;
			else if (sp == StartPhase.MakePath)
				return makePathWindow;
			else if (sp == StartPhase.SelectTreeType)
				return selectTreeTypeWindow;
			else if (sp == StartPhase.SelectTrees)
				return selectTreesWindow;
			else if (sp == StartPhase.SaveConfig)
				return saveConfigWindow;
			else if (sp == StartPhase.UseQCResponder)
				return useQCResponderWindow;
			else if (sp == StartPhase.UseChatNotifier)
				return useChatNotifierWindow;
			else if (sp == StartPhase.Ready)
				return detailsWindow;
			return null;
		}
	}
	
	static wolfsAllWcer root;

	public enum StartPhase {
		/*
		ChooseMode (new ChooseModeWindow()),
		LoadPrevious (new LoadPreviousWindow()),
		UseMouseKeys (new UseMouseKeysWindow()),
		DetectBank (new DetectBankWindow()),
		MakePath (new WalkToTreesWindow()),
		SelectTreeType (new SelectTreeTypeWindow()),
		SelectTrees (new SelectTreesWindow()),
		SaveConfig (new SaveConfigWindow()),
		UseQCResponder (new UseQCResponderWindow()),
		UseChatNotifier (new UseChatNotifierWindow()),
		Ready (new DetailsWindow());
		*/
		
		ChooseMode,
		LoadPrevious,
		UseMouseKeys,
		DetectBank,
		MakePath,
		SelectTreeType,
		SelectTrees,
		SaveConfig,
		UseQCResponder ,
		UseChatNotifier ,
		Ready ;
		
		private WindowOptions myOption;
		
		private StartPhase(WindowOptions wo) {
			this.myOption = wo;
		}
		private StartPhase() {}
		
		public WindowOptions getOption() {
			return myOption;
		}
	}

	public StartPhase nextPhase(StartPhase curPhase) {
		boolean returnNext = false;
		for (StartPhase sp : StartPhase.values()) {
			if (returnNext)
				return sp;
			if (sp.equals(curPhase)) {
				returnNext = true;
				continue;
			}
		}
		return null;
	}

	public class ChooseModeWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public ChooseModeWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("calculator.png"))
						return PaintImages.get("calculator.png");
					return null;
				}

				@Override
				public String getText() {
					return "Banking";
				}

				@Override
				public String getIdentifier() {
					return "wcmode.bank";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("clock.png"))
						return PaintImages.get("clock.png");
					return null;
				}

				@Override
				public String getText() {
					return "Powerleveling";
				}

				@Override
				public String getIdentifier() {
					return "wcmode.power";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("leaf.png"))
						return PaintImages.get("leaf.png");
					return null;
				}

				@Override
				public String getText() {
					return "Ivy";
				}

				@Override
				public String getIdentifier() {
					return "wcmode.ivy";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("folder.png"))
						return PaintImages.get("folder.png");
					return null;
				}

				@Override
				public String getText() {
					return "Load config";
				}

				@Override
				public String getIdentifier() {
					return "wcmode.load";
				}
			});
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			return opt;
		}

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("wcmode.bank")) {
				chopBankIvy = WCMode.Bank;
				return StartPhase.DetectBank;
			} else if (option.getIdentifier().equals("wcmode.power")) {
				chopBankIvy = WCMode.Powerlevel;
				return StartPhase.UseMouseKeys;
			} else if (option.getIdentifier().equals("wcmode.ivy")) {
				JOptionPane.showMessageDialog(null, "Warning: Ivys are still very experimental");
				chopBankIvy = WCMode.Ivy;
				return StartPhase.UseQCResponder;
			} else if (option.getIdentifier().equals("wcmode.load")) {
				return StartPhase.LoadPrevious;
			}

			return StartPhase.ChooseMode;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			text.add("Please choose the wcing mode");
			return text;
		}
	}

	public class LoadPreviousWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();
		private WindowOption next = null;

		public LoadPreviousWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("back.png"))
						return PaintImages.get("back.png");
					return null;
				}

				@Override
				public String getText() {
					return "Back";
				}

				@Override
				public String getIdentifier() {
					return "loadprevious.back";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("note.png"))
						return PaintImages.get("note.png");
					return null;
				}

				@Override
				public String getText() {
					return "Open list";
				}

				@Override
				public String getIdentifier() {
					return "loadprevious.open";
				}
			});
			next = new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("next.png"))
						return PaintImages.get("next.png");
					return null;
				}

				@Override
				public String getText() {
					return "Load selected";
				}

				@Override
				public String getIdentifier() {
					return "loadprevious.load";
				}
			};
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			ArrayList<WindowOption> ret = opt;
			if (selected == null && ret.contains(next))
				ret.remove(next);
			else if (selected != null && !ret.contains(next))
				ret.add(next);
			return ret;
		}

		String selected = null;
		ConfigSelector cs = null;

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("loadprevious.back")) {
				return StartPhase.ChooseMode;
			}else if (option.getIdentifier().equals("loadprevious.open")) {
				if (cs == null)
					cs = new ConfigSelector();
				else
					cs.setVisible(true);
			}
			else if (option.getIdentifier().equals("loadprevious.load")) {
				

				File configFile = new File("Allwc" + File.separator + "config"
						+ File.separator + selected + ".txt");

				try {
					loadWithJSON(configFile);
				} catch (Exception e) {
					log("Error with loading JSON config file");
				}

				return StartPhase.UseQCResponder;
			}

			return StartPhase.LoadPrevious;
		}
		

		public void loadWithJSON(File f) throws IOException, JSONException {

			if (!isValidJSONFile(f)) {
				log("Attempting to make " + f.getName() + " into JSON format");
				txtToObj(f);
			}

			JSONObject obj = JSONFileHandler.readFile(f);

			if (obj.has("WoodcuttingStyle")) {
				WCMode mode = WCMode.valueOf(obj.getString("WoodcuttingStyle"));
				if (mode != null)
					chopBankIvy = mode;
			}

			if (obj.has("TreeType")) {
				final String ct = obj.getString("TreeType");
				new Thread(new Runnable() {

					@Override
					public void run() {

						for (Tree t : Tree.values()) {
							if (t.name().equalsIgnoreCase(ct)) {
								selectedTree = t;
								t.setPrice(ge.loadItemInfo(t.getLogId()).getPrice());
								break;
							}
						}
					}
				}).start();
			}

			if (obj.has("TreePoints")) {

				JSONArray tiles = obj.getJSONArray("TreePoints");

				RSPolygon temptp = new RSPolygon();
				for (int tn = 0; tn < tiles.length(); tn++) {
					JSONObject t = tiles.getJSONObject(tn);
					if (t.has("x") && t.has("y"))
						temptp.addTile(new RSTile(t.getInt("x"), t.getInt("y")));
				}

				tilepoints = temptp;
			}

			if (obj.has("PathToWoods")) {
				JSONArray tiles = obj.getJSONArray("PathToWoods");

				ArrayList<PathTile> temptp = new ArrayList<PathTile>();
				for (int tn = 0; tn < tiles.length(); tn++) {
					JSONObject t = tiles.getJSONObject(tn);
					if (t.has("x") && t.has("y")) {
						PathTile pt = new PathTile(t.getInt("x"), t.getInt("y"));
						if (t.has("obstacle"))
							pt.setObstacle(t.getInt("obstacle"));
						
						temptp.add(pt);
					}
				}
				List<PathTile> list = temptp;
				pathToWoods = list.toArray(new PathTile[list.size()]);
			}

			if (obj.has("WoodTile")) {
				JSONObject ts = obj.getJSONObject("WoodTile");
				if (ts.has("x") && ts.has("y"))
					woodsTile = new RSTile(ts.getInt("x"), ts.getInt("y"));
			}

			if (obj.has("BankTile")) {
				JSONObject ts = obj.getJSONObject("BankTile");
				if (ts.has("x") && ts.has("y"))
					bankTile = new RSTile(ts.getInt("x"), ts.getInt("y"));
			}

			if (obj.has("UseMouseKeys")) {
				if (obj.getBoolean("UseMouseKeys") == false)
					useMousekeys = false;
				else
					useMousekeys = true;
			}
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> ret = new ArrayList<String>();
			ret.add("Selected config: " + selected);
			return ret;
		}
		
		public class ConfigSelector extends JFrame {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 7793469780045032923L;
			
			DefaultListModel model;
			JList list;
			
			public ConfigSelector() {
				setLayout(new BorderLayout());
				setTitle("Config selector");
				
				model = new DefaultListModel();
				list = new JList(model);
				list.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						Object selectedv = list.getSelectedValue();
						if (selectedv != null)
							selected = (String) selectedv;
						else
							selected = null;
					}
					
				});
				
				add(new JScrollPane(list), BorderLayout.CENTER);
				
				JPanel buttons = new JPanel(new FlowLayout());
				
				JButton okay = new JButton("Ok");
				
				okay.setIcon(new ImageIcon(PaintImages.get("yes.png")));
				
				okay.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
					
				});
				JButton refresh = new JButton("Refresh list");
				
				refresh.setIcon(new ImageIcon(PaintImages.get("refresh.png")));
				
				refresh.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						reload();
					}
					
				});
				
				buttons.add(okay);
				buttons.add(refresh);
				
				add(buttons, BorderLayout.SOUTH);
				
				Dimension size = new Dimension(500, 250);
				setMinimumSize(size);
				setSize(size);
				
				reload();
				setVisible(true);
			}
			
			public void reload() {
				new Thread(new Runnable() {

					@Override
					public void run() {
						File allWcFolder = new File("Allwc");
						if (!allWcFolder.exists())
							allWcFolder.mkdir();
						File allWcConfigFolder = new File(allWcFolder
								.getAbsolutePath()
								+ File.separator + "config");
						if (!allWcConfigFolder.exists()) {
							allWcConfigFolder.mkdir();
							//text
							//		.add("Config folder created at Infinity/Allwc/config");
							return;
						}

						model.clear();

						File[] configz = allWcConfigFolder
								.listFiles(new FilenameFilter() {

									@Override
									public boolean accept(File arg0, String arg1) {
										return arg1.endsWith(".txt");
									}

								});

						for (File conf : configz) {
							model.addElement(conf.getName().split(".txt")[0]);
						}

					}

				}).start();
			}
		}
	}

	public class UseMouseKeysWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public UseMouseKeysWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("no.png"))
						return PaintImages.get("no.png");
					return null;
				}

				@Override
				public String getText() {
					return "No";
				}

				@Override
				public String getIdentifier() {
					return "mousekeys.no";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("yes.png"))
						return PaintImages.get("yes.png");
					return null;
				}

				@Override
				public String getText() {
					return "Yes";
				}

				@Override
				public String getIdentifier() {
					return "mousekeys.yes";
				}
			});
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			return opt;
		}

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("mousekeys.yes"))
				useMousekeys = true;
			else if (option.getIdentifier().equals("mousekeys.no"))
				useMousekeys = false;
			return StartPhase.SelectTreeType;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			text.add("Do you want to use mousekeys to drop logs?");
			text.add("It's VERY fast and Jagex themself have stated");
			text.add("it's legit.");
			return text;
		}
	}

	public class DetectBankWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public DetectBankWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("next.png"))
						return PaintImages.get("next.png");
					return null;
				}

				@Override
				public String getText() {
					return "Next";
				}

				@Override
				public String getIdentifier() {
					return "detectbank.next";
				}
			});
		}

		boolean bankNear = false;

		@Override
		public ArrayList<WindowOption> getOptions() {
			if (!baHa.bankNear(10)) {
				bankNear = false;
				//return new ArrayList<WindowOption>(); TODO ODSAGMD
			}
			bankNear = true;
			return opt;
		}

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("detectbank.next")) {
				bankTile = player.getMyLocation();

				return StartPhase.MakePath;
			}
			return StartPhase.DetectBank;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			if (bankNear) {
				text.add("Bank detected! Press next to continue");
			} else {
				text
						.add("Please walk to nearby bank. It will be automatically detected");
			}
			return text;
		}
	}

	public class WalkToTreesWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public WalkToTreesWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("next.png"))
						return PaintImages.get("next.png");
					return null;
				}

				@Override
				public String getText() {
					return "Next";
				}

				@Override
				public String getIdentifier() {
					return "walktotrees.next";
				}
			});
		}

		boolean enoughTiles = false;
		
		

		@Override
		public ArrayList<WindowOption> getOptions() {
			ArrayList<WindowOption> ret = new ArrayList<WindowOption>();
			
			ret.add(new WindowOption() {

				@Override
				public BufferedImage getIcon() {
					return (doorDetection ? PaintImages.get("yes.png") : PaintImages.get("no.png"));
				}

				@Override
				public String getIdentifier() {
					return "walktotrees.doordetect";
				}

				@Override
				public String getText() {
					return "Automatic door detection";
				}
				
			});
			
			if (tempPathToWoods.size() > 2) {
				enoughTiles = true;
				ret.addAll(opt);
				return ret;
			}
			enoughTiles = false;
			return ret;
		}

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("walktotrees.next")) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						List<PathTile> list = tempPathToWoods;
						pathToWoods = list.toArray(new PathTile[list.size()]);

						tempPathToWoods.clear();
					}

				}).start();

				return StartPhase.SelectTreeType;
			}
			else if (option.getIdentifier().equals("walktotrees.doordetect")) {
				doorDetection = !doorDetection;
				if (!doorDetection) {
					warnAboutOpenDoor = null;
				}
			}
			return StartPhase.MakePath;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			text.add("Please walk to the trees (turn off input before clicking next)");
			text.add("Door detection automatically adds nearby doors/obstacles to the path");
			if (warnAboutOpenDoor != null) {
				text.add(" ");
				text.add("WARNING! DoorDetector has detected open door. If DoorDetector just");
				text.add("registered closed version of this door, you can ignore this. Otherwise");
				text.add("please close the door and open it so DoorDetector can detect it");
				text.add("and banking path won't fail");
				text.add("PathMaking is disabled until you close the door or disable DoorDetection");
				text.add("Detected open door is highlighted in red");
			}
			return text;
		}
	}

	public class SelectTreeTypeWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public SelectTreeTypeWindow() {
			for (final Tree treeName : Tree.values()) {
				opt.add(new WindowOption() {

					@Override
					public BufferedImage getIcon() {
						return null;
					}

					@Override
					public String getIdentifier() {
						return "selecttreetype." + treeName.name();
					}

					@Override
					public String getText() {
						return treeName.name();
					}

				});
			}
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			return opt;
		}

		@Override
		public StartPhase onPress(final WindowOption option) {
			if (option.getIdentifier().startsWith("selecttreetype.")) {

				new Thread(new Runnable() {

					@Override
					public void run() {
						String getChosenTree = option.getText();
						for (Tree t : Tree.values()) {
							if (t.name().equalsIgnoreCase(getChosenTree)) {
								t.setPrice(ge.loadItemInfo(t.getLogId()).getPrice());
								selectedTree = t;
								break;
							}
						}
					}

				}).start();

				return StartPhase.SelectTrees;
			}
			return StartPhase.SelectTreeType;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			text.add("Please select tree type");
			return text;
		}
	}

	public class SelectTreesWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public SelectTreesWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("next.png"))
						return PaintImages.get("next.png");
					return null;
				}

				@Override
				public String getText() {
					return "Next";
				}

				@Override
				public String getIdentifier() {
					return "selecttrees.next";
				}
			});
		}

		boolean allowNext = false;

		@Override
		public ArrayList<WindowOption> getOptions() {
			if (tilepoints.size() > 1) {
				allowNext = true;
				return opt;
			}
			allowNext = false;
			return new ArrayList<WindowOption>();
		}

		@Override
		public StartPhase onPress(final WindowOption option) {
			if (option.getIdentifier().equals("selecttrees.next")) {
				woodsTile = player.getMyLocation();
				return StartPhase.SaveConfig;
			}
			return StartPhase.SelectTrees;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			if (allowNext)
				text.add("Good! Click next to continue or draw again.");
			else {
				text.add("Draw a polygon in minimap over trees you want to chop");
				text.add("The area should contain a few tiles free space");
				text.add("on the edges to make sure script doesn't think");
				text.add("we're outside the area");
			}

			return text;
		}
	}

	public class SaveConfigWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public SaveConfigWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("no.png"))
						return PaintImages.get("no.png");
					return null;
				}

				@Override
				public String getText() {
					return "No";
				}

				@Override
				public String getIdentifier() {
					return "saveconfig.no";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("yes.png"))
						return PaintImages.get("yes.png");
					return null;
				}

				@Override
				public String getText() {
					return "Yes";
				}

				@Override
				public String getIdentifier() {
					return "saveconfig.yes";
				}
			});
			text.add("Do you want to make a config file from current setup?");
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			return opt;
		}

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("saveconfig.yes")) {
				File allWcFolder = new File("Allwc");
				if (!allWcFolder.exists())
					allWcFolder.mkdir();
				File allWcConfigFolder = new File(allWcFolder.getAbsolutePath()
						+ File.separator + "config");
				if (!allWcConfigFolder.exists()) {
					allWcConfigFolder.mkdir();
					text.add("Config folder created at Infinity/Allwc/config");
				}
				
				String lastName = "";
				
				do {
					String name = JOptionPane
							.showInputDialog("Please give a name for config file", lastName);
					if (name != null) {
						File configFile = new File(allWcFolder
								.getAbsolutePath()
								+ File.separator
								+ "config"
								+ File.separator
								+ name + ".txt");
						if (configFile.exists()) {
							JOptionPane.showMessageDialog(null, "Config file "
									+ name + " exists already :(");
							lastName = name;
							continue;
						} else {
							saveWithJSON(configFile);
							break;
						}
					}
					else {
						return StartPhase.SaveConfig;
					}
				} while (true);
			}
			return StartPhase.UseQCResponder;
		}

		ArrayList<String> text = new ArrayList<String>();

		public void saveWithJSON(File configFile) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("WoodcuttingStyle", chopBankIvy.name());
				obj.put("TreeType", selectedTree.name());

				obj.put("WoodTile", new JSONObject().put("x", woodsTile.getX())
						.put("y", woodsTile.getY()));
				obj.put("BankTile", new JSONObject().put("x", bankTile.getX())
						.put("y", bankTile.getY()));
				obj.put("UseMouseKeys", useMousekeys);
				for (PathTile rt : pathToWoods) {
					obj.append("PathToWoods", new JSONObject().put("x",
							rt.getX()).put("y", rt.getY()).put("obstacle", rt.getObstacleId()));
				}
				for (RSTile rt : tilepoints.getTileArray()) {
					obj.append("TreePoints", new JSONObject().put("x",
							rt.getX()).put("y", rt.getY()));
				}

				JSONFileHandler.saveFile(configFile, obj);

			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public ArrayList<String> getText() {
			return text;
		}
	}

	public class UseChatNotifierWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public UseChatNotifierWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("no.png"))
						return PaintImages.get("no.png");
					return null;
				}

				@Override
				public String getText() {
					return "No";
				}

				@Override
				public String getIdentifier() {
					return "chatnotifier.no";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("yes.png"))
						return PaintImages.get("yes.png");
					return null;
				}

				@Override
				public String getText() {
					return "Yes";
				}

				@Override
				public String getIdentifier() {
					return "chatnotifier.yes";
				}
			});
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			return opt;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			text.add("Do you want to use SystemTray chat notifier.");
			text.add("It's easy to disable when in use");
			text.add("Suggested to use on low-population wcing areas");
			return text;
		}

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("chatnotifier.yes")) {
				add();
			}

			startTime = System.currentTimeMillis();
			return StartPhase.Ready;
		}

		public void add() {
			if (SystemTray.isSupported()) {
				trayIco = new TrayIcon(PaintImages.get("fire.png"));

				PopupMenu popup = new PopupMenu();
				MenuItem me = new MenuItem("Disable chat notifier");

				me.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						SystemTray.getSystemTray().remove(trayIco);
						trayIco = null;
					}

				});
				popup.add(me);

				trayIco.setPopupMenu(popup);

				try {
					SystemTray.getSystemTray().add(trayIco);
				} catch (AWTException e) {
					log("Error while adding tray icon");
					e.printStackTrace();
					trayIco = null;
				}
			} else {
				JOptionPane
						.showMessageDialog(null,
								"Unfortunately System Tray is not available on your system");
			}
		}

	}

	public class UseQCResponderWindow extends WindowOptions {

		private ArrayList<WindowOption> opt = new ArrayList<WindowOption>();

		public UseQCResponderWindow() {
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("no.png"))
						return PaintImages.get("no.png");
					return null;
				}

				@Override
				public String getText() {
					return "No";
				}

				@Override
				public String getIdentifier() {
					return "qcresponder.no";
				}
			});
			opt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("yes.png"))
						return PaintImages.get("yes.png");
					return null;
				}

				@Override
				public String getText() {
					return "Yes";
				}

				@Override
				public String getIdentifier() {
					return "qcresponder.yes";
				}
			});
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			return opt;
		}

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("qcresponder.yes"))
				QCresponder = true;
			else if (option.getIdentifier().equals("qcresponder.no"))
				QCresponder = false;

			return StartPhase.UseChatNotifier;
		}

		@Override
		public ArrayList<String> getText() {
			ArrayList<String> text = new ArrayList<String>();
			text.add("Do you want to use quickchat responder?");
			return text;
		}
	}

	public class DetailsWindow extends WindowOptions {

		private ArrayList<WindowOption> normalOpt = new ArrayList<WindowOption>();
		private ArrayList<WindowOption> configOpt = new ArrayList<WindowOption>();
		private ArrayList<WindowOption> debugOpt = new ArrayList<WindowOption>();

		public DetailsWindow() {
			normalOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("tag.png"))
						return PaintImages.get("tag.png");
					return null;
				}

				@Override
				public String getText() {
					return "Hide paint";
				}

				@Override
				public String getIdentifier() {
					return "details.togglepaint";
				}
			});
			normalOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("tool.png"))
						return PaintImages.get("tool.png");
					return null;
				}

				@Override
				public String getText() {
					return "Options";
				}

				@Override
				public String getIdentifier() {
					return "details.options";
				}
			});
			normalOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("clipboard.png"))
						return PaintImages.get("clipboard.png");
					return null;
				}

				@Override
				public String getText() {
					return "Debug console";
				}

				@Override
				public String getIdentifier() {
					return "details.debug";
				}
			});

			configOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("back.png"))
						return PaintImages.get("back.png");
					return null;
				}

				@Override
				public String getText() {
					return "Return";
				}

				@Override
				public String getIdentifier() {
					return "details.options.return";
				}
			});
			/*configOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					return null;
				}

				@Override
				public String getText() {
					return "ForceStop";
				}

				@Override
				public String getIdentifier() {
					return "details.options.forcestop";
				}
			});*/
			configOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("fire.png"))
						return PaintImages.get("fire.png");
					return null;
				}

				@Override
				public String getText() {
					return "Transparency";
				}

				@Override
				public String getIdentifier() {
					return "details.options.alpha";
				}
			});

			debugOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("back.png"))
						return PaintImages.get("back.png");
					return null;
				}

				@Override
				public String getText() {
					return "Return";
				}

				@Override
				public String getIdentifier() {
					return "details.debug.return";
				}
			});

			debugOpt.add(new WindowOption() {
				@Override
				public BufferedImage getIcon() {
					if (PaintImages.containsKey("manifest.png"))
						return PaintImages.get("manifest.png");
					return null;
				}

				@Override
				public String getText() {
					return (paused ? "Resume" : "Pause") + " messageadding";

				}

				@Override
				public String getIdentifier() {
					return "details.debug.pause";
				}
			});
		}

		@Override
		public ArrayList<WindowOption> getOptions() {
			if (page == 0)
				return normalOpt;
			else if (page == 1)
				return configOpt;
			return debugOpt;
		}

		private int page = 0; // 0 for details 1 for options 2 for debug console

		@Override
		public StartPhase onPress(WindowOption option) {
			if (option.getIdentifier().equals("details.options"))
				page = 1;
			else if (option.getIdentifier().equals("details.debug"))
				page = 2;
			else if (option.getIdentifier().equals("details.togglepaint"))
				togglePaint = false;
			else if (option.getIdentifier().equals("details.options.return"))
				page = 0;
			else if (option.getIdentifier().equals("details.options.forcestop")) {
				//Bot.getScriptHandler().stopScript(ID, true); TODO
			}
			else if (option.getIdentifier().equals("details.options.alpha"))
				mw.toggleAlpha();
			else if (option.getIdentifier().equals("details.debug.return"))
				page = 0;
			else if (option.getIdentifier().equals("details.debug.pause"))
				paused = !paused;

			// log("User pressed " + option.getText());
			return StartPhase.Ready;
		}

		ArrayList<String> text = new ArrayList<String>();
		ArrayList<String> debugs = new ArrayList<String>();

		boolean paused = false;

		public void addDebugMsg(String msg) {
			if (paused)
				return;
			if (debugs.size() > 30)
				debugs.remove(debugs.size() - 1);
			debugs.add(0, msg);
		}

		@Override
		public ArrayList<String> getText() {

			if (page == 0) {

				text.clear();

				long runTime = System.currentTimeMillis() - startTime;

				int gainedXP = skills.getCurrentXP(STAT_WOODCUTTING)
						- startXP;
				int gainedLvls = skills.getCurrentLvl(STAT_WOODCUTTING)
						- startLvl;

				int profit = selectedTree.getPrice() * logsChopped, profitPerHour = 0;
				if (profit != 0) {
					profitPerHour = (int) ((3600000.0 / (double) runTime) * profit);
				}

				int xpPerHour = (int) ((3600000.0 / (double) runTime) * gainedXP);
				int logsPerHour = (int) ((3600000.0 / (double) runTime) * logsChopped);
				long timeToLevel = (long) (skills
						.getXPToNextLvl(STAT_WOODCUTTING) / (((float) gainedXP) / ((int) runTime / 1000))) * 1000;

				text.add("Wolf's all wcer - Build " + build + (usedYanilleIvyFix ? " - Using yanille ivy fix (buggy)" : ""));
				text.add("Runtime:" + cTime(runTime));
				text.add("XP gained:" + gainedXP + " (" + gainedLvls
						+ " levels)");
				text.add("Current level:"
						+ skills.getCurrentLvl(STAT_WOODCUTTING));
				text.add("XP/Hour:" + xpPerHour);
				text.add("TTL:" + cTime(timeToLevel));
				text.add("Logs chopped:" + logsChopped);
				text.add("Logs/Hour:" + logsPerHour);
				text.add("Profit:" + profit);
				text.add("Profit/Hour:" + profitPerHour);
				text.add(" == Debug == ");
				text.add("lastCall: " + (System.currentTimeMillis() - lastCall)
						+ " ago");
			} else if (page == 1) {
				text.clear();
				text.add("Use buttons below to change");
				text.add("settings. Press return to go");
				text.add("back to details");
			} else if (page == 2) {
				return debugs;
			}
			return text;
		}
	}

	public abstract class WindowOptions {
		public abstract StartPhase onPress(WindowOption option);

		public abstract ArrayList<WindowOption> getOptions();

		public abstract ArrayList<String> getText();
		
		public void mouseMove(MouseEvent e) {}
		public void mousePress(MouseEvent e) {}
	}

	public interface WindowOption {
		public String getText();

		public BufferedImage getIcon();

		public String getIdentifier();
	}

	public void txtToObj(File f) throws JSONException, IOException {
		JSONObject ret = new JSONObject();

		HashMap<String, String> handleData = new HashMap<String, String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));

			String line;
			while ((line = br.readLine()) != null) {
				if (!line.contains(":"))
					continue;
				String[] args = line.split(":", 2);
				if (args.length > 1)
					handleData.put(args[0], args[1]);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (handleData.containsKey("WoodcuttingStyle")) {
			WCMode mode = WCMode.valueOf(handleData.get("WoodcuttingStyle"));
			if (mode != null)
				ret.put("WoodcuttingStyle", mode.name());
		}

		if (handleData.containsKey("TreeType")) {
			final String ct = handleData.get("TreeType");
			for (final Tree t : Tree.values()) {
				if (!t.name().equalsIgnoreCase(ct))
					continue;
				ret.put("TreeType", ct);

				new Thread(new Runnable() {

					@Override
					public void run() {
						t.setPrice(ge.loadItemInfo(t.getLogId()).getPrice());
					}
				}).start();

				break;
			}

		}

		if (handleData.containsKey("TreePoints")) {
			String[] tiles = handleData.get("TreePoints").split(">");
			for (String t : tiles) {
				if (!t.contains("x"))
					continue;
				String[] ts = t.split("x");
				try {
					int x = Integer.valueOf(ts[0]);
					int y = Integer.valueOf(ts[1]);
					ret.append("TreePoints", new JSONObject().put("x", x).put(
							"y", y));
				} catch (Exception e) {
				}
			}
		}

		if (handleData.containsKey("PathToWoods")) {
			String[] tiles = handleData.get("PathToWoods").split(">");
			for (String t : tiles) {
				if (!t.contains("x"))
					continue;
				String[] ts = t.split("x");
				try {
					int x = Integer.valueOf(ts[0]);
					int y = Integer.valueOf(ts[1]);
					ret.append("PathToWoods", new JSONObject().put("x", x).put(
							"y", y));
				} catch (Exception e) {
				}
			}
		}

		if (handleData.containsKey("WoodTile")) {
			String[] ts = handleData.get("WoodTile").split("x");
			if (ts.length == 2) {
				try {
					int x = Integer.valueOf(ts[0]);
					int y = Integer.valueOf(ts[1]);
					ret.put("WoodTile", new JSONObject().put("x", x)
							.put("y", y));
				} catch (Exception e) {
				}
			}
		}

		if (handleData.containsKey("BankTile")) {
			String[] ts = handleData.get("BankTile").split("x");
			if (ts.length == 2) {
				try {
					int x = Integer.valueOf(ts[0]);
					int y = Integer.valueOf(ts[1]);
					ret.put("BankTile", new JSONObject().put("x", x)
							.put("y", y));
				} catch (Exception e) {
				}
			}
		}

		if (handleData.containsKey("UseMouseKeys")) {
			ret.put("UseMouseKeys", !handleData.get("UseMouseKeys").equals(
					"false"));
		}

		JSONFileHandler.saveFile(f, ret);

	}

	public boolean isValidJSONFile(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));

		boolean isValid = br.readLine().startsWith("{");

		br.close();

		return isValid;
	}
}
