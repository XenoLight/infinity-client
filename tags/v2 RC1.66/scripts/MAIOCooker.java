/**
 * AIO Cooker
 * 
 * @author Masacrator
 * @version 1.00
 * 
 * 1.00 -> Release
 * 1.01 -> Fixed some NullPointerException errors and Rogues Den location errors
 * 1.02 -> Updated to work with new interfaces
 * 1.03 -> Fixed the no-cooking error and removed some code to avoid NullPointers
 * 1.04 -> Added Nardah
 * 1.05 -> Fixed more NullPointers
 * 
 * Made only for Infninity client and users 
 * 
 * Thanks to Gribbon for helping me with doors!
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = { "Masacrator" }, category = "Cooking", name = "AIO Cooker", version = 1.03, description = "<html><center>"
		+ "<br></br>"
		+ "Masacrator's AIO Cooker<br>"
		+ "<br></br>"
		+ "<br>Settings in GUI</br>" + "<br></br>" + "</center></html>")
public class MAIOCooker extends Script implements PaintListener,
		MessageListener {

	private int foodID = 0, bankID = 0, rangeID = 0, doorID = 0;
	private String rangeType = "";
	private long cooked;
	private RSTile[] pathToBank, pathToRange;
	private RSTile[] akBankToCook = { new RSTile(3270, 3168), new RSTile(3273, 3180) };
	private RSTile[] akCookToBank = walk.reversePath(akBankToCook);
	private int akCookPlace = 25730, akBank = 35647;
	private int rdCookPlace = 2732, rdBank = 2271;
	private int naCookPlace = 10377, naBank = 10517;
	private int cgCookPlace = 24283, cgBank = 19230;
	private RSTile[] nBankToCook = { new RSTile(2337, 3806), new RSTile(2342, 3810) };
	private RSTile[] nCookToBank = walk.reversePath(nBankToCook);
	private int neCookPlace = 21302, neBank = 21301;
	private RSTile[] dBankToCook = { new RSTile(3092, 3247), new RSTile(3102, 3258) };
	private RSTile[] dCookToBank = walk.reversePath(nBankToCook);
	private int dCookPlace = 2724, dBank = 2213, dDoor = 1530;

	private BufferedImage normal;
	private BufferedImage clicked;

	private float secExp = 0;
	private int secToLevel = 0, minutesToLevel = 0, hoursToLevel = 0;

	private final Color color1 = new Color(153, 0, 153, 180);
	private final Color color2 = new Color(255, 255, 0);
	private final Color color3 = new Color(255, 255, 0, 180);
	private final Color color4 = new Color(153, 0, 153);

	private final BasicStroke stroke1 = new BasicStroke(1);

	private final Font font1 = new Font("Traditional Arabic", 0, 13);
	private final Font font2 = new Font("Tw Cen MT", 0, 12);
	private final Font font3 = new Font("Tw Cen MT", 0, 10);

	private Image img1 = null;

	private int expGained = 0, expPerHour = 0, 
			startExp = skills.getCurrentXP(STAT_COOKING), foodCooked = 0, foodPerHour = 0,
			startLevel = skills.getCurrentLvl(STAT_COOKING),
			levelsGained = 0;
	private long hours, minutes, seconds, startTime;

	private boolean guiWait = true, guiExit;
	private RSObject range;


	public boolean hasFood() {
		return inventory.contains(foodID);
	}

	public boolean atRange() {
		RSObject range = objects.getNearestByID(rangeID);
		return range != null && range.isOnScreen();
	}

	public boolean atDoor() {
		RSObject door = objects.getNearestByID(doorID);
		return door != null && door.isOnScreen();
	}

	public boolean onStart(final Map<String, String> args) {
		new MAIOCookerGUI();
		while (guiWait) {
			wait(100);
		}
		try {
			URL localURL1 = new URL("http://dl.dropbox.com/u/3900566/Mouse.png");
			URL localURL2 = new URL("http://dl.dropbox.com/u/3900566/click.png");
			img1 = getImage("http://images3.wikia.nocookie.net/__cb20091205043728/runescape/images/f/f7/Cooking-icon.png");
			normal = ImageIO.read(localURL1);
			clicked = ImageIO.read(localURL2);
		} catch (MalformedURLException localMalformedURLException) {
			log("Unable to buffer cursor.");
		} catch (IOException localIOException) {
			log("Unable to open cursor image.");
		}
		startTime = System.currentTimeMillis();
		return !guiExit;
	}

	public int getMouseSpeed() {
		return random(3, 5);
	}

	public int loop() {
		if (iface.canContinue()) {
			iface.clickContinue();
			wait(random(3000, 4000));
		}
		if (!isRunning()) {
			game.setRun(true);
		}
		if (player.getMyEnergy() <= 15) {
			player.rest(95);
		}
		cookFood();
		walkToRange();
		walkToBank();
		bankFood();
		openDoor(doorID);
		return random(38,120);
	}

	public boolean openDoor(int doorsID) {
		RSObject door = objects.getNearestByID(doorsID);
		
		if (door==null)  {
			return false;
		}
		
		return door.action("Open");
	}

	public boolean cookFood() {
		range = objects.getNearestByID(rangeID);
		RSInterface cookIface = iface.get(905);
		RSInterfaceChild cookIfaceChild = cookIface.getChild(14);

		// General note.  The game is such that interface 905 will show as valid even if it is not
		// being displayed.  Interface 916 is the element of 905 that lets you set the default selection
		// of 5, 10, All, etc.  THAT interface, 916, will be invalid if 905 is not displayed and only 
		// valid if 905 is displayed.  So we should use 916 to determine if 905 is valid.
		//
		// Alternatively, if 905's child 14 has children of its own, only then is 905 really valid and
		// usable.
		if (hasFood()) {
			if (!bank.isOpen()) {
				if (range.isOnScreen()) {
					if (me.isIdle() &&
							System.currentTimeMillis() - cooked >= 4000) {
						if (!inventory.isItemSelected() && !iface.get(916).isValid()) {
							inventory.clickItem(foodID, "Use");
							wait(random(800, 900));
							if (inventory.isItemSelected() && 
								range.action("-> " + rangeType))  {
									iface.waitForOpen(iface.get(916), 3000);
							}
						} else {
							String tip = cookIfaceChild.getTooltip();
							if (tip!=null && tip.contains("All")) {
								if (iface.clickChild(cookIfaceChild))  {
									iface.waitForClose(iface.get(916), 3000);
									cooked = System.currentTimeMillis();
								}
							}
						}
					} else {
						performAntiban();
					}
				} 
			} else {
				bank.close();
			}
		}

		return false;
	}

	public boolean bankFood() {
		RSNPC bankNpc = null;
		RSObject banker = null;

		if (bankID == rdBank) {
			bankNpc = npc.getNearestByID(bankID);
		} else {
			banker = objects.getNearestByID(bankID);
		}
		
		if (!hasFood()) {
			if (bank.nearby()) {
				if (bank.isOpen()) {
					if (bank.getCount(foodID) > 0) {
						if (bank.depositAll()) {
							inventory.waitForCount(foodID, 0, 2000);
							bank.withdraw(foodID, 0);
							inventory.waitForCount(foodID, 28, 2000);
						}
					}
					else  {
						log("There are no more raw fish left in the bank.  Stopping the script.");
						bank.close();
						stopScript();
					}
				} else {
					bank.open();
				}
			} else {
				if (bankID == rdBank) {
					walk.tileMM(bankNpc.getLocation());
				} else if (bankID == naBank) {
					walk.tileMM(banker.getLocation());
				} else {
					walk.pathMM(pathToBank);
				}
			}
		}

		return false;
	}

	public boolean walkToRange() {
		if (!atRange() && hasFood()) {
			range = objects.getNearestByID(rangeID);
			RSTile dest = walk.getDestination();
			
			if (dest==null || dest.distanceTo() < random (4,6))  {					
				if (rangeID == rdCookPlace || rangeID == naCookPlace) {
					if (walk.to(range))  {
						player.waitToMove(random(2000,2500));
					}
				} else {
					walk.pathMM(pathToRange, 0);
				}
			}
		}
		
		return false;
	}

	public boolean walkToBank() {
		if (!bank.nearby() && !hasFood()) {
			RSTile dest = walk.getDestination();
			
			if (dest==null || dest.distanceTo() <= random(3, 5)) {
				walk.pathMM(pathToBank, 0);
			}
		}
		return false;
	}

	public void messageReceived(MessageEvent e) {
		String msg = e.getMessage();
		if (msg.contains("successfully cook") || msg.contains("manage to cook")
				|| msg.contains("You roast")) {
			foodCooked++;
			cooked = System.currentTimeMillis();
		}
		if (msg.contains("manage to burn")) {
			cooked = System.currentTimeMillis();
		}
		if (msg.contains("accidentally burn")) {
			cooked = System.currentTimeMillis();
		}
	}

	public void performAntiban() {
		int antibanInt = random(1, 350);
		if (antibanInt == 10 || antibanInt == 50 || antibanInt == 220) {
			mouse.move(random(40, 720), random(30, 490), 2, 2);
		} else if (antibanInt == 120) {
			game.openTab(TAB_STATS);
			wait(random(500, 600));
			mouse.move(674 + (random(1, 45)), 294 + (random(1, 22)));
			wait(random(3500, 6000));
		} else if (antibanInt == 200 || antibanInt == 290) {
			camera.setRotation(random(1, 360));
		}
	}

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	public void onRepaint(Graphics g1) {
		if (!guiWait) {
			((Graphics2D) g1).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int currentLevel = skills.getCurrentLvl(STAT_COOKING);
			levelsGained = currentLevel - startLevel;
			expGained = skills.getCurrentXP(STAT_COOKING) - startExp;
			expPerHour = (int) ((expGained) * 3600000D / (System
					.currentTimeMillis() - startTime));
			foodPerHour = (int) ((foodCooked) * 3600000D / (System
					.currentTimeMillis() - startTime));
			long millis = System.currentTimeMillis() - startTime;
			hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			seconds = millis / 1000;
			if ((minutes > 0 || hours > 0 || seconds > 0) && expGained > 0) {
				secExp = (float) expGained
						/ (float) (seconds + minutes * 60 + hours * 60 * 60);
			}
			if (secExp > 0) {
				secToLevel = (int) (skills.getXPToNextLvl(STAT_COOKING) / secExp);
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

			Graphics2D g = (Graphics2D) g1;
			g.setColor(color1);
			g.fillRoundRect(553, 212, 176, 246, 16, 16);
			g.setColor(color2);
			g.setStroke(stroke1);
			g.drawRoundRect(553, 212, 176, 246, 16, 16);
			g.drawImage(img1, 688, 220, null);
			g.setFont(font1);
			g.drawString("AIO Cooker", 571, 240);
			g.drawLine(553, 247, 729, 247);
			g.setFont(font2);
			g.drawString("Time running: " + hours + ":" + minutes + ":"
					+ seconds, 565, 262);
			g.drawString("Experience gained: " + expGained, 565, 282);
			g.drawString("Experience p/h: " + expPerHour, 565, 302);
			g.drawString("Fishes cooked: " + foodCooked, 565, 322);
			g.drawString("Fishes cooked p/h: " + foodPerHour, 565, 342);
			g.drawString(
					"Experience TNL: " + skills.getXPToNextLvl(STAT_COOKING),
					565, 362);
			g.drawString("Levels gained: " + levelsGained, 565, 382);
			g.drawString(
					"Current level: " + skills.getCurrentLvl(STAT_COOKING),
					565, 402);
			g.drawString("Estimated time TNL: " + hoursToLevel + ":"
					+ minutesToLevel + ":" + secToLevel, 565, 422);
			g.setColor(color3);
			g.fillRect(562, 435, 157, 15);
			g.setColor(color2);
			g.drawRect(562, 435, 157, 15);
			g.setFont(font3);
			g.setColor(color4);
			g.drawString("Script made by Masacrator - LazyGamerz", 567, 446);

			if (normal != null) {
				Mouse localMouse = Bot.getClient().getMouse();
				int i4 = localMouse.getX();
				int i5 = localMouse.getY();
				int i6 = localMouse.getPressX();
				int i7 = localMouse.getPressY();
				long l3 = System.currentTimeMillis()
						- localMouse.getPressTime();

				if ((localMouse.getPressTime() == -1L) || (l3 >= 1000L)) {
					g.drawImage(normal, i4 - 8, i5 - 8, null);
				}
				if (l3 < 1000L) {
					g.drawImage(clicked, i6 - 8, i7 - 8, null);
					g.drawImage(normal, i4 - 8, i5 - 8, null);
				}
			}
		}
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(bot, true);
	}

	public class MAIOCookerGUI extends JPanel {
		private static final long serialVersionUID = 1L;

		public MAIOCookerGUI() {
			initComponents();
		}

		private void button1ActionPerformed(ActionEvent e) {
			final String foodE = comboBox1.getSelectedItem().toString();
			final String locE = comboBox2.getSelectedItem().toString();
			if (locE.equals("Al-Kharid")) {
				bankID = akBank;
				rangeID = akCookPlace;
				pathToBank = akCookToBank;
				pathToRange = akBankToCook;
				rangeType = "Range";
			} else if (locE.equals("Rogues Den")) {
				bankID = rdBank;
				rangeID = rdCookPlace;
				rangeType = "Fire";
			} else if (locE.equals("Cooking Guild")) {
				bankID = cgBank;
				rangeID = cgCookPlace;
				rangeType = "Range";
			} else if (locE.equals("Neitiznot")) {
				bankID = neBank;
				rangeID = neCookPlace;
				pathToBank = nCookToBank;
				pathToRange = nBankToCook;
				rangeType = "Clay";
			} else if (locE.equals("Draynor Village")) {
				bankID = dBank;
				rangeID = dCookPlace;
				pathToBank = dCookToBank;
				pathToRange = dBankToCook;
				doorID = dDoor;
				rangeType = "Fire";
			} else if (locE.equals("Nardah")) {
				bankID = naBank;
				rangeID = naCookPlace;
				rangeType = "Clay";
			}
			if (foodE.equals("Shrimp")) {
				foodID = 317;
			} else if (foodE.equals("Crayfish")) {
				foodID = 13435;
			} else if (foodE.equals("Chicken")) {
				foodID = 2138;
			} else if (foodE.equals("Rabbit")) {
				foodID = 0;
			} else if (foodE.equals("Anchovies")) {
				foodID = 321;
			} else if (foodE.equals("Karambwan")) {
				foodID = 3142;
			} else if (foodE.equals("Herring")) {
				foodID = 345;
			} else if (foodE.equals("Mackerel")) {
				foodID = 353;
			} else if (foodE.equals("Trout")) {
				foodID = 335;
			} else if (foodE.equals("Cod")) {
				foodID = 341;
			} else if (foodE.equals("Pike")) {
				foodID = 349;
			} else if (foodE.equals("Salmon")) {
				foodID = 331;
			} else if (foodE.equals("Tuna")) {
				foodID = 359;
			} else if (foodE.equals("Rainbow Fish")) {
				foodID = 10138;
			} else if (foodE.equals("Lobster")) {
				foodID = 377;
			} else if (foodE.equals("Bass")) {
				foodID = 363;
			} else if (foodE.equals("Swordfish")) {
				foodID = 371;
			} else if (foodE.equals("Monkfish")) {
				foodID = 7944;
			} else if (foodE.equals("Shark")) {
				foodID = 383;
			} else if (foodE.equals("Sea Turtle")) {
				foodID = 395;
			} else if (foodE.equals("Cave Fish")) {
				foodID = 15264;
			} else if (foodE.equals("Manta Ray")) {
				foodID = 389;
			} else if (foodE.equals("Rocktail")) {
				foodID = 15270;
			}
			guiWait = false;
			frame1.dispose();
		}

		private void initComponents() {
			frame1 = new JFrame();
			label1 = new JLabel();
			label2 = new JLabel();
			comboBox1 = new JComboBox();
			label3 = new JLabel();
			comboBox2 = new JComboBox();
			button1 = new JButton();

			// ======== frame1 ========
			{
				frame1.setVisible(true);
				frame1.setTitle("AIO Cooker GUI");
				Container frame1ContentPane = frame1.getContentPane();
				frame1ContentPane.setLayout(null);
				frame1.setAlwaysOnTop(true);
				frame1.setResizable(false);
				frame1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

				// ---- label1 ----
				label1.setText("AIO Cooker - by Masacrator");
				label1.setHorizontalAlignment(SwingConstants.CENTER);
				label1.setForeground(Color.red);
				label1.setFont(new Font("Tw Cen MT", Font.PLAIN, 16));
				frame1ContentPane.add(label1);
				label1.setBounds(0, 0, 220, 25);

				// ---- label2 ----
				label2.setText("Food to cook:");
				label2.setHorizontalAlignment(SwingConstants.CENTER);
				frame1ContentPane.add(label2);
				label2.setBounds(50, 50, 120, label2.getPreferredSize().height);

				// ---- comboBox1 ----
				comboBox1.setModel(new DefaultComboBoxModel(new String[] {
						"Crayfish", "Chicken", "Rabbit", "Anchovies",
						"Karambwan", "Herring", "Mackerel", "Trout", "Cod",
						"Pike", "Salmon", "Tuna", "Rainbow Fish", "Lobster",
						"Bass", "Swordfish", "Monkfish", "Shark", "Sea Turtle",
						"Cave Fish", "Manta Ray", "Rocktail" }));
				frame1ContentPane.add(comboBox1);
				comboBox1.setBounds(50, 69, 120,
						comboBox1.getPreferredSize().height);

				// ---- label3 ----
				label3.setText("Location:");
				label3.setHorizontalAlignment(SwingConstants.CENTER);
				frame1ContentPane.add(label3);
				label3.setBounds(50, 119, 120, label3.getPreferredSize().height);

				// ---- comboBox2 ----
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Al-Kharid", "Draynor Village", "Rogues Den",
						"Cooking Guild", "Neitiznot", "Nardah" }));
				frame1ContentPane.add(comboBox2);
				comboBox2.setBounds(50, 138, 120,
						comboBox2.getPreferredSize().height);

				// ---- button1 ----
				button1.setText("Start");
				button1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						button1ActionPerformed(e);
					}
				});
				frame1ContentPane.add(button1);
				button1.setBounds(75, 188, 70,
						button1.getPreferredSize().height);

				{
					Dimension preferredSize = new Dimension();
					for (int i = 0; i < frame1ContentPane.getComponentCount(); i++) {
						Rectangle bounds = frame1ContentPane.getComponent(i)
								.getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width,
								preferredSize.width);
						preferredSize.height = Math.max(bounds.y
								+ bounds.height, preferredSize.height);
					}
					Insets insets = frame1ContentPane.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					frame1ContentPane.setMinimumSize(preferredSize);
					frame1ContentPane.setPreferredSize(preferredSize);
				}
				frame1.pack();
				frame1.setLocationRelativeTo(frame1.getOwner());
			}
		}

		private JFrame frame1;
		private JLabel label1;
		private JLabel label2;
		private JComboBox comboBox1;
		private JLabel label3;
		private JComboBox comboBox2;
		private JButton button1;
	}
}
