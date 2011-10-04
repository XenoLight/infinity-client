
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
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Infinity Dev Team"},
name = "Pine Shafter",
version = 1.0,
category = "Fletching",
website = "",
notes = "",
description = "<html><style type='text/css'>"/*start html option code*/
+ "body {background:url('http://lazygamerz.org/client/images/back_1.png') repeat}"/*background image*/
+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"/*header image*/
+ "</head><br><body>"
/*start box*/
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
+ "<td width=90% align=justify>"
+ "<center><font color=#000000>Infinity Pine Shafter</font></center><br />"/*script title*/
+ "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"/*top box label*/
/*box statment*/
+ "<font size=3>Start this script near the evergreens West of Barbarian Village. Have a regular knife in your "
+ "invintory for cutting logs to arrow shafts, and a wood cutting axe equiped...<br>"
+ "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"/*ending tag label*/
+ "</td></tr></table><br />"/*end box*/)
public class ArrowShaftCutter extends Script implements PaintListener {

    public int lostCount = 0;
    public final int ShaftID = 52;
    private static final int knifeID = 946;
    private static final int logID = 1511;
    public long returningShaftCount = inventory.getCount(ShaftID);
    private static final int[] treeID = {1276, 1278, 1315, 1316};
    public RSTile[] woodTiles = new RSTile[]{new RSTile(3045, 3442),
        new RSTile(3053, 3448), new RSTile(3051, 3432)};
    public RSObject tree;
    private boolean setAltitude = true;
    public String status = "";
    int startXP = 0;
    int startLvl = 0;
    long startTime = System.currentTimeMillis();
    BufferedImage img = null;

    /**
     * overrides default org.rsbot.script.Script.onStart
     *
     * @param onStart - a {@link Map<String, String> args}
     */
    @Override
    public boolean onStart(Map<String, String> args) {
        try {
            final URL url = new URL("http://lazygamerz.org/client/skin/paint.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
            log("Failed to get url image for the progress paint.");
        }
        if (!inventory.containsOneOf(knifeID)) {
            log("GET A Knife");
            stopScript();
        }
        return true;
    }

    public int loop() {
        if (!game.isLoggedIn()) {
            return 5;
        }
        if (setAltitude) {
            camera.setAltitude(true);
            sleep(200);
            setAltitude = false;
            return 5;
        }
        if (itemSelected() != 0) {
            unselectItem();
            return random(400, 1000);
        }
        if (isLost()) {
            lostCount++;
            status = "Walking to Woods.";
            walkToWoods();
        }
        if (!inventory.isFull()) {
            status = "Cutting Trees";
            waitIdle();
            while (player.getMine().isMoving()) {
                wait(300, 500);
            }
            tree = objects.getNearestByID(treeID);
            if (tree != null) {
                for (RSTile tile : woodTiles) {
                    if (tile.distanceTo(tree.getLocation()) > 25) {
                        walkToWoods();
                        return 100;
                    }
                }
				getNear(tree.getLocation());
				if (atMultiTiledObject_4(tree, "Chop")){
					wait(800, 1500);
				}

            } else {
                walkToWoods();
                player.waitToMove(2000);
                while (player.getMine().isMoving()) {
                    wait(150, 300);
                }
            }
            return random(1000, 1500);
        }
        if (inventory.contains(logID)) {
            if (!isCutting()) {
            status = "Fletching";
            if (iface.canContinue()) {
                iface.clickContinue();
            }
            if (itemSelected() != 0) {
                unselectItem();
                return random(700, 1400);
            }

            inventory.clickItem(knifeID, "Use");
            wait(random(400, 700));
            inventory.clickItem(logID, "Use");
            iface.waitForOpen(iface.get(916), 6000);
            while (!iface.get(916).isValid()) {
                wait(100, 250);
            }
            if (iface.get(916).isValid()) {
                wait(random(600, 800));
                iface.clickChild(905, 14, "Make All");
                wait(random(850, 1200));
                while (isCutting()) {
                wait(random(150, 500));
            }
            }
            
        } else if (inventory.contains(logID)) {
            status = "Fletching";
            if (iface.canContinue()) {
                iface.clickContinue();
            }
            if (itemSelected() != 0) {
                unselectItem();
                return random(700, 1400);
            }

            inventory.clickItem(knifeID, "Use");
            wait(random(400, 700));
            inventory.clickItem(logID, "Use");
            wait(1200, 1800);
            mouseBox(new Point(53, 393), new Point(111, 453), false);
            wait(200, 500);
            menu.action("Make All");
            wait(1500, 2500);
        }
        }
        return 100;
    }

    public boolean isCutting() {
        int i, j;
        j = 0;
        for (i = 1; i <= 10; i++) {
            if (player.getMine().getAnimation() == 1248) {
                j = j + 1;
            }
            wait(random(25, 50));
        }
        if (j > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isLost() {
        for (RSTile tile : woodTiles) {
            if (tile.distanceTo() > 25) {
                return true;
            }
        }
        return false;
    }

    public int itemSelected() {
        for (final RSInterfaceChild com : inventory.getInterface().getChildren()) {
            if (com.getBorderThickness() == 2) {
                return com.getChildID();
            }
        }
        return 0;
    }

    public void unselectItem() {
        for (final RSInterfaceChild com : inventory.getInterface().getChildren()) {
            if (com.getBorderThickness() == 2) {
                iface.clickChild(com);
                return;
            }
        }
    }

    public void walkToWoods() {
        switch (random(1, 5)) {
            case 1:
                walk.to(woodTiles[0], 10, 10);
                break;
            case 2:
                walk.to(woodTiles[1], 10, 10);
                break;
            case 3:
                walk.to(woodTiles[2], 10, 10);
                break;
            default:
                walk.to(woodTiles[1], 20, 20);
                break;
        }
        wait(random(1000, 2500));
        while (player.getMine().isMoving()) {
            wait(200, 500);
        }

    }

    private void mouseBox(Point p1, Point p2, boolean leftClick) {
        int x = (int) random(p1.getX(), p2.getX());
        int y = (int) random(p1.getY(), p2.getY());
        mouse.click(new Point(x, y), leftClick);
    }

    public void wait(int min, int max) {
        wait(random(min, max));
    }

    public boolean isFarAway(RSTile location) {
        for (RSTile t : woodTiles) {
            if (t.distanceTo(location) > 25) {
                return true;
            }
        }
        return false;
    }

    public void getNear(RSTile t){
		if (isFarAway(t))
			return;
		if (t.distanceTo() > 12 || !calculate.pointOnScreen(t.getScreenLocation())){
			int x = randomButZero(5);
			int y = randomButZero(5);
			walk.to(new RSTile(x,y));
			wait(1000, 1500);
			while (player.getMine().isMoving()){
				wait(300,500);
			}
		}
	}

    /**
     * BY: RedDevil~~
     * @param object <RSObject> Ojbect to search for
     * @param action <String> What action to do?
     * @return <boolean> true if it found the object, and preformed the action.
     */
    public boolean atMultiTiledObject_4(RSObject object, String action){
    	try {
			if (object == null)
				return false;
			int x = object.getLocation().getX();
			int y = object.getLocation().getY();
			RSTile[] objT = new RSTile[4];  // The tiles well be using
			RSTile[] testTiles = new RSTile[] {new RSTile(x-1,y+1), new RSTile(x,y+1), new RSTile(x+1, y+1),
												new RSTile(x-1, y), new RSTile(x,y), new RSTile(x+1, y),
												new RSTile(x-1, y-1), new RSTile(x, y-1), new RSTile(x+1, y-1)};
			for (RSTile t : testTiles){
				if (objects.getTopAt(t) != null){
					if (objects.getTopAt(t).getID() == object.getID()){
						for (int i=0; i<4; i++){
							if (objT[i] == null){
								objT[i] = t;
							}
						}
					}
				}
			}
			for (RSTile t : objT){
				if (t == null){
					return false;
				}
			}
			Point[] objTP = new Point[4];// Screen points of all tiles with object on them
			for (int i=0; i<4; i++){
				objTP[i] = objT[i].getScreenLocation();
			}
			int X1 = (int) Math.round((objTP[0].getX()+objTP[1].getX())/2);
			int X2 = (int) Math.round((objTP[2].getX()+objTP[3].getX())/2);
			int Y1 = (int) Math.round((objTP[0].getY()+objTP[1].getY())/2);
			int Y2 = (int) Math.round((objTP[2].getY()+objTP[3].getY())/2);
			int mX = Math.round((X1+X2)/2);
			int mY = Math.round((Y1+Y2)/2);
			if (!calculate.pointOnScreen(new Point(mX,mY))){
				return false;
			}
			mouse.move(mX, mY, 15, 15);
			return menu.action(action);
		} catch (Exception e) {
			e.printStackTrace();
			log("Exception at - atMultiTiledObject_4");
			return false;
		}
    }

    public void waitTillStill(int timeout) {
        for (int i = 0; i < Math.round(timeout / 50); i++) {
            if (player.getMine().isMoving()) {
                break;
            }
            wait(50);
        }
        while (player.getMine().isMoving()) {
            wait(random(150, 300));
        }
    }

    public void waitIdle() {
        while (player.getMine().getAnimation() != -1) {
            wait(random(200, 500));
        }
    }

    public int randomButZero(int range) {
        int answer = 0;
        while (answer == 0) {
            answer = random(-range, range);
        }
        return answer;
    }

    @Override
    public void onRepaint(Graphics g) {
        final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
        int CurrentXP = skills.getCurrentXP(Skills.FLETCHING);
        int RealLvL = skills.getRealLvl(Skills.FLETCHING);
        int currentXP = skills.getCurrentXP(Skills.FLETCHING);
        int currentLVL = skills.getCurrentLvl(Skills.FLETCHING);
        int currentPurLVL = skills.getPercentToNextLvl(Skills.FLETCHING);
        int XPToNextLvL = skills.getXPToNextLvl(Skills.FLETCHING);
        int STL = (int) (((skills.getXPToNextLvl(Skills.FLETCHING)) / 5) + 1) * 15;
        final double XP = 5;//special skill define

        if (game.isLoggedIn()) {
            //sets up the paint visual run timer for the user
            //is is set up in 00 set up rather than typical 0
            long runTime = System.currentTimeMillis() - startTime;
            final int seconds = (int) ((runTime / 1000) % 60);
            final int minutes = (int) ((runTime / 1000) / 60) % 60;
            final int hours = (int) (((runTime / 1000) / 60) / 60) % 60;
            final StringBuilder t1 = new StringBuilder();
            if (hours < 10) {
                t1.append('0');
            }
            t1.append(hours);
            t1.append(" : ");
            if (minutes < 10) {
                t1.append('0');
            }
            t1.append(minutes);
            t1.append(" : ");
            if (seconds < 10) {
                t1.append('0');
            }
            t1.append(seconds);
            if (startLvl == 0) {
                startXP = CurrentXP;
                startLvl = RealLvL;
            }
            //base number used for left alignment of text. I could just type 9 in all x spaces
            //but what fun would that be?
            final int x = 315;
            final int x1 = 400;

            int gainedXP = currentXP - startXP;
            final int done = (int) (gainedXP / XP * 15);
            final int donePerHour = (int) ((gainedXP / XP * 15) * 3600000.0 / (double) runTime);
            final int expPerHour = (int) ((currentXP - startXP) * 3600000.0 / (double) runTime);
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
            g.drawString("Run Time: " + t1, x, 394);
            g.drawString("Made: " + done, x, 410);
            g.drawString("PH: " + donePerHour, x1, 410);
            g.drawString("Lvl: " + currentLVL, x, 426);
            g.drawString("Gained: " + gainedLVL, x1, 426);
            g.drawString("Xp: " + gainedXP, x, 442);
            g.drawString("PH: " + expPerHour, x1, 442);
            g.drawString("XP TNL: " + XPToNextLvL, x, 458);
            g.drawString("Shafts: " + STL, x1, 458);
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
