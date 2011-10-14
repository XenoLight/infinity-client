
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = "Infinity Dev Team",
category = "Woodcutting",
name = "Draynor Willows",
version = 1.003,
website = "http://www.lazygamerz.org/forums/index.php?topic=3202.new#new",
notes = "",
description = "<style type='text/css'>"
+ "body {background:url('http://lazygamerz.org/client/images/back_1.png') repeat}"
+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\"></head></style>"
+ "<html><h1><center><font color=#FFFFFF>"
+ "Bighoof Draynor Willows" //script name here
+ "</center></font color></h1>"
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
+ "<td width=90%>"
+ "Start at the trees or in the bank bank at Draynor village with your wood cutting axe " //script description here
+ "on you either equiped or in your inventory. The script will log out and stop if you "
+ "do not have an axe, but will work with the axe in the inventory. Please report any problems "
+ "to the forums thread for thsi script...."
+ "</td></tr></table></center></body></html>")
public class DraynorWillows extends Script implements PaintListener {

    RSTile[] bankToTrees = {new RSTile(3092, 3243), new RSTile(3087, 3237)};
    RSTile[] treesToBank = walk.reversePath(bankToTrees);
    private final int[] treeID = {58006, 38616, 38627};
    private int[] nestID = {5070, 5071, 5072, 5073, 5074, 5075, 5076, 7413, 11966};
    private int[] woodaxe = {1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739};
    private boolean checked = false;
    private boolean setAltitude = false;
    public String status = "";
    int startXP = 0;
    int startLvl = 0;
    long startTime = System.currentTimeMillis();
    BufferedImage img = null;

    @Override
    public boolean onStart(final Map<String, String> args) {
        try {
            final URL url = new URL("http://lazygamerz.org/client/skin/wood.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
            log.warning("Failed to get url image for the progress paint.");
        }
        return true;
    }

    RSObject currTree = null;
    int currTreeID = -1;

    public int loop() {
        if (!setAltitude) {
            camera.setAltitude(true);
            wait(random(500, 800));
            setAltitude = true;
            log.config("camera altitude set to birdeye view.");
            return random(300, 600);
        }
        camera.setRotation(359);
        if (player.getMyEnergy() < random(40, 65)) {
            game.setRun(true);
            log("Turned run on");
        }
        if (!checked) {
            if (!equipment.containsOneOf(woodaxe) && !inInventory(woodaxe)) {
                log.warning("no axe found on log in. Stoping script.");
                wait(random(10000, 12000));
                game.logout();
                stopScript();
            } else {
                checked = true;
            }
        }
        RSGroundItem birdNest = ground.getItemByID(nestID);
        if (birdNest != null)  {
        	if (!inventory.isFull()) {
	        	birdNest.action("Take ");
	            wait(random(1000, 1500));
	        } else if (inventory.isFull()) {
	            inventory.clickItem(1519, "Drop");
	            wait(random(100,200));
	            birdNest.action("Take ");
	            wait(random(1000, 1500));
	        }
        }
        while (player.getMine().isMoving()) {
            return random(500, 600);
        }
        inventory.open();
        if (inventory.isFull()) {
            walk.pathMM(treesToBank, 3, 3);
            if (!bank.isOpen()) {
            if (bank.open());
                for (int i = 0; i < 100 && !bank.isOpen(); i++) {
                    sleep(20);
                }
        }
            if (bank.isOpen()) {
                if (inInventory(woodaxe)) {
                    bank.depositAllExcept(woodaxe);
                } else {
                    bank.depositAll();
                }
            }
            return 500;
        } else {
            walk.pathMM(bankToTrees, 1, 1);
            if (player.getMine().getAnimation() == -1) {
            	RSObject thistree = null;
            	
            	if (currTree!=null)  {
            		thistree = objects.getTopAt(currTree.getLocation());
            		
            		if (thistree.getID()!=currTreeID)  {
            			currTree=null;
            			currTreeID=-1;
            		}
            	}
            	
            	if (currTree==null)  {
            		debug("Selecting a new tree.");
	                RSObject tree = objects.getNearestByID(treeID);
	                if (tree != null) {
	                    if (tree.isOnScreen()) {
	                    	if (tree.action("Chop down"))  {
	                    		currTree = tree;
	                    		currTreeID = tree.getID();
	                    		wait(random(300, 500));
	                    	}
	
	                        while (player.getMine().isMoving()) {
	                            wait(random(700, 900));
	                            RSTile dest = walk.getDestination();
	                            if (dest!=null && dest.distanceTo() <= random(5, 7)) {
	                                break;
	                            }
	                        }
	                    }
	                }
                }
            }
        }

        return random(800, 1000);
    }

    public boolean inInventory(int... itemID) {
        try {
            for (int i : itemID) {
                if (inventory.getCount(i) != 0) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Sets the {@link render}
     *
     * @param Repaint - a {@link render}
     */
    public void onRepaint(Graphics g) {
        final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
        int RealLvL = skills.getRealLvl(Skills.WOODCUTTING);
        int currentXP = skills.getCurrentXP(Skills.WOODCUTTING);
        int currentLVL = skills.getCurrentLvl(Skills.WOODCUTTING);
        int currentPurLVL = skills.getPercentToNextLvl(Skills.WOODCUTTING);
        int XPToNextLvL = skills.getXPToNextLvl(Skills.WOODCUTTING);
        final double XP = 68;//special skill define

        if (game.isLoggedIn()) {
            //sets up the paint visual run timer for the user
            //is is set up in 00 set up rather than typical 0
            long millis = System.currentTimeMillis() - startTime;
            String time = Timer.format(millis);
            if (startLvl == 0) {
                startXP = currentXP;
                startLvl = RealLvL;
            }
            //base number used for left alignment of text. I could just type 9 in all x spaces
            //but what fun would that be?
            final int x = 315;
            final int x1 = 400;

            int gainedXP = currentXP - startXP;
            final int done = (int) (gainedXP / XP);
            final int donePerHour = (int) ((gainedXP / XP) * 3600000.0 / (double) millis);
            final int expPerHour = (int) ((currentXP - startXP) * 3600000.0 / (double) millis);
            int gainedLVL = currentLVL - startLvl;
            final int fillBar = (int) (2 * (double) currentPurLVL);

            //This is the box that will draw to insure that if the image does not load
            //that there will still be a black box behind the words to look decent for the user
            g.setColor(Color.BLACK);
            g.fill3DRect(310, 342, 210, 135, true);

            g.setColor(Color.RED);//color changes for % box
            g.fill3DRect(318, 342, 162, 14, true);//fill % box
            g.setColor(Color.GREEN);//% fill bar color
            g.fill3DRect(318, 342, fillBar, 14, true);

            g.drawImage(img, 250, 318, null);
            g.setFont(new Font("arial", Font.PLAIN, 10));
            g.setColor(new Color(225, 225, 225, 255));
            g.drawString("Infinity " + properties.name(), x, 378);
            g.drawString("Run Time: " + time, x, 394);
            g.drawString("Chopped: " + done, x, 410);
            g.drawString("PH: " + donePerHour, x1, 410);
            g.drawString("Lvl: " + currentLVL, x, 426);
            g.drawString("Gained: " + gainedLVL, x1, 426);
            g.drawString("Xp: " + gainedXP, x, 442);
            g.drawString("PH: " + expPerHour, x1, 442);
            g.drawString("Xp To Next Level: " + XPToNextLvL, x, 458);
            g.drawString("Status: " + status, x, 474);

            //version define
            g.drawString("" + properties.version(), 494, 474);
            //percent of fill bar done
            g.drawString("" + currentPurLVL, 502, 355);

            //DRAW MOUSE
            final Mouse m = Bot.getClient().getMouse();
            final Point loc = mouse.getLocation();
            if (m == null) {
                return;
            }

            final int mouse_x = m.getX();
            final int mouse_y = m.getY();
            final int mouse_press_x = m.getPressX();
            final int mouse_press_y = m.getPressY();
            final long mouse_press_time = mouse.getPressTime();

            g.setColor(Color.YELLOW);
            g.drawLine(mouse_x - 8, mouse_y - 8, mouse_x + 8, mouse_y + 8);
            g.drawLine(mouse_x + 8, mouse_y - 8, mouse_x - 8, mouse_y + 8);
            if (System.currentTimeMillis() - mouse_press_time < 1000) {
                g.setColor(Color.GREEN);
                g.drawLine(mouse_press_x - 8, mouse_press_y - 8, mouse_press_x + 8, mouse_press_y + 8);
                g.drawLine(mouse_press_x + 8, mouse_press_y - 8, mouse_press_x - 8, mouse_press_y + 8);
                g.drawLine(mouse_press_x - 7, mouse_press_y - 7, mouse_press_x + 7, mouse_press_y + 7);
                g.drawLine(mouse_press_x + 7, mouse_press_y - 7, mouse_press_x - 7, mouse_press_y + 7);
                g.drawLine(mouse_press_x - 6, mouse_press_y - 6, mouse_press_x + 6, mouse_press_y + 6);
                g.drawLine(mouse_press_x + 6, mouse_press_y - 6, mouse_press_x - 6, mouse_press_y + 6);
                g.setColor(new Color(0, 0, 0, 50));
                g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
            } else {
                g.setColor(Color.BLACK);
            }

            g.drawLine(0, loc.y, 766, loc.y);
            g.drawLine(loc.x, 0, loc.x, 505);
        }
    }
}
