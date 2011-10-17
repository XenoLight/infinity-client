
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
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Infinity Dev Team"},
name = "Barbarian Agility",
version = 1.0,
category = "Agility",
website = "http://www.lazygamerz.org/forums/index.php?topic=3112.0",
notes = "",
description = "<html><style type='text/css'>"/*start html option code*/
+ "body {background:url('http://lazygamerz.org/client/images/back_1.png') repeat}"/*background image*/
+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"/*header image*/
+ "</head><br><body>"
/*start box*/
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
+ "<td width=90% align=justify>"
+ "<center><font color=#000000>Infinity Barbarian Agility</font></center><br />"/*script title*/
+ "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"/*top box label*/
/*box statment*/
+ "<font size=3>Start this script at the Starting rope swing of the Barbarian Agility Course with the food "
+ "you want to use in your inventory. This script will stop if you are out of food, and it only works for the "
+ "regular un - extended cousre. There is no set up options just choose the login that you would like to use "
+ "and make sure that login is in the right place before you start the script...<br>"
+ "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"/*ending tag label*/
+ "</td></tr></table><br />"/*end box*/)
public class BarbarianAgility extends Script implements PaintListener {

    private int runEnergy = random(40, 95);
    private int health = 200;
    private boolean setAltitude = true;
    public String status = "";
    int startXP = 0;
    int startLvl = 0;
    long startTime = System.currentTimeMillis();
    BufferedImage img = null;

    private enum State {

        rope, log, net, ledge, ladder, wall, error
    };
    private int[] food = {1895, 1893, 1891, 4293, 2142, 291, 2140, 3228, 9980,
        7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568, 2343,
        1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351, 329,
        3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391, 3369,
        3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178, 7180,
        7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011, 2289,
        2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895, 1897,
        1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068, 1942,
        6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989, 1978,
        5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911, 5745,
        2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034, 2048,
        2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225, 2255,
        2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032, 2074,
        2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971, 4608,
        1883, 1885, 15272};

    @Override
    protected int getMouseSpeed() {
        return random(5, 7);
    }

    //moves the camera around 0 to 180
    private void moveCameraSlightly() {
        int angle = camera.getAngle() + random(-90, 90);
        if (angle < 0) {
            angle = 0;
        }
        if (angle > 359) {
            angle = 0;
        }
        camera.setRotation(angle);
    }

    private State getState() {
        if (!inventory.containsOneOf(food) && skills.getCurrentLP() < health) {
            log.info("Health is too low and out of food...");
            return State.error;
        }
        if (playerInArea(2555, 3559, 2543, 3550)) {
            return State.rope;
        }
        if (playerInArea(2553, 3549, 2544, 3542)) {
            return State.log;
        }
        if (playerInArea(2542, 3547, 2533, 3545) && game.getPlane() == 0) {
            return State.net;
        }
        if (playerInArea(2538, 3547, 2536, 3545) && game.getPlane() == 1) {
            return State.ledge;
        }
        if (playerInArea(2532, 3547, 2532, 3546) && game.getPlane() == 1) {
            return State.ladder;
        }
        if (playerInArea(2537, 3551, 2532, 3548) || (playerInArea(2532, 3549, 2532, 3546) && game.getPlane() == 0) || playerInArea(2542, 3556, 2532, 3550)) {
            return State.wall;
        }
        return State.rope;
    }

    /**
     * overrides default org.rsbot.script.Script.onStart
     *
     * @param onStart - a {@link Map<String, String> args}
     */
    @Override
    public boolean onStart(Map<String, String> args) {
        try {
            final URL url = new URL("http://lazygamerz.org/client/skin/agilit1.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
            log("Failed to get url image for the progress paint.");
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
        eat();
        startRunning(runEnergy);
        switch (getState()) {
            case rope:
                doRope();
                break;
            case log:
                doLog();
                break;
            case net:
                doNet();
                break;
            case ledge:
                doLedge();
                break;
            case ladder:
                doLadder();
                break;
            case wall:
                doWall();
                break;
            case error:
                stopScript();
                break;
        }
        return 5;
    }

    private void doRope() {
        status = "Swinging on rope...";
        final RSTile rope = new RSTile(2552, 3553);
        final RSTile ladder = new RSTile(2547, 9951);
        final RSTile walkHere = new RSTile(2551, 3554);
        if (playerInArea(2555, 9955, 2546, 9948)) {
            if (camera.getAngle() < 85 || camera.getAngle() > 95) {
                camera.setRotation(random(85, 95));
                sleep(random(100, 200));
                return;
            }
            if (onTile(ladder, "Ladder", "Climb-up", 0.4, 0.5, 40)) {
            }
            sleep(random(1000, 1200));
            return;
        }
        if (!playerInArea(2554, 3555, 2549, 3554)) {
            walkTile(walkHere);
            sleep(random(50, 500));
            moveCameraSlightly();
            sleep(random(100, 200));
            return;
        }
        camera.setCompass('e');
        if (onTile(rope, "Ropeswing", "Swing-on", -0.5, 0.1, 450)) {
            wait(random(2800, 3200));
        }
        while (player.getMine().getAnimation() == 751 || player.getMine().isMoving()) {
            sleep(100);
        }
        return;
    }

    private void doLog() {
        status = "Crossing log...";
        final RSTile logg = new RSTile(2550, 3546);
        if (onTile(logg, "Log balance", "Walk-across", 0.5, 0.4, 0)) {
            sleep(random(200, 500));
        }

        moveCameraSlightly();
        sleep(random(100, 300));
        mouse.move(random(50, 700), random(50, 450), 2, 2);
        sleep(random(200, 700));
        mouse.move(random(50, 700), random(50, 450), 2, 2);
        sleep(random(1300, 1600));
        while (player.getMine().isMoving() || playerInArea(2550, 3546, 2542, 3546)) {
            sleep(100);
        }
        return;
    }

    private void doNet() {
        status = "Climbing up net...";
        final RSTile net = new RSTile(2538, 3546);
        final RSTile walkHere = new RSTile(2539, 3546);
        if (game.getPlane() == 0 && playerInArea(2538, 3547, 2533, 3545)) {
            walkTile(walkHere);
            sleep(random(500, 750));
            while (player.getMine().isMoving()) {
                sleep(100);
            }
            return;
        }
        if (onTile(net, "Obstacle net", "Climb-over", random(0.51, 0.61), 0,
                200)) {
            sleep(random(1500, 2200));
        }
        while (player.getMine().getAnimation() == 828 || player.getMine().isMoving()) {
            sleep(100);
        }
        return;
    }

    private void doLedge() {
        status = "Walking on ledge...";
        final RSTile ledge = new RSTile(2535, 3547);
        if (onTile(ledge, "Balancing ledge", "Walk-across", 0.5, 0.75, 0)) {
            sleep(random(200, 700));
        }
        mouse.move(random(25, 300), random(25, 400), 2, 2);
        sleep(random(1000, 1200));
        while (player.getMine().isMoving()
                || (playerInArea(2535, 3547, 2532, 3547) && game.getPlane() == 1)) {
            sleep(100);
        }
        return;
    }

    private void doLadder() {
        status = "Climbing ladder...";
        final RSTile ladder = new RSTile(2532, 3545);
        if (onTile(ladder, "Ladder", "Climb-down", 0.5, 0.6, 0)) {
            sleep(random(50, 200));
        }
        moveCameraSlightly();
        mouse.move(random(25, 300), random(25, 400), 2, 2);
        sleep(random(800, 1000));
        while (player.getMine().isMoving() || player.getMine().getAnimation() == 827) {
            sleep(100);
        }
        return;
    }

    private void doWall() {
        status = "Climbing over wall...";
        if (player.getMine().isMoving()) {
            return;
        }
        final RSTile wall1 = new RSTile(2537, 3553);
        final RSTile wall2 = new RSTile(2542, 3553);
        final RSTile walkHere = new RSTile(2535, 3551);
        if (!tile.onScreen(wall1)) {
            walkTile(walkHere);
            sleep(random(200, 300));
            return;
        }
        if (playerInArea(2542, 3554, 2538, 3552)) {
            if (onTile(wall2, "Crumbling wall", "Climb-over", 0.9, 0.5, 5)) {
                sleep(random(500, 800));
            }
            while (player.getMine().isMoving()
                    || player.getMine().getAnimation() == 4853) {
                sleep(100);
            }
            return;
        }
        if (onTile(wall1, "Crumbling wall", "Climb-over", 0.9, 0.5, 5)) {
            sleep(random(200, 500));
        }
        mouse.move(random(25, 300), random(25, 400), 2, 2);
        sleep(random(800, 1000));
        while (player.getMine().isMoving() || player.getMine().getAnimation() == 4853) {
            sleep(100);
        }
        return;
    }

    private void startRunning(final int energy) {
        if (player.getMyEnergy() >= energy && !isRunning()) {
            runEnergy = random(40, 95);
            game.setRun(true);
            sleep(random(500, 750));
        }
    }

    private boolean playerInArea(int maxX, int maxY, int minX, int minY) {
        int x = player.getMine().getLocation().getX();
        int y = player.getMine().getLocation().getY();
        if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
            return true;
        }
        return false;
    }

    public boolean eat() {
        status = "Eating...";
        int getHP = skills.getCurrentLP();
        int RealHP = skills.getRealLvl(3) * 10;
        if (inventory.containsOneOf(food)) {
            if (getHP <= random(RealHP / 2.0, RealHP / 2.5)) {
                for (int foodID : food) {
                    if (inventory.contains(foodID)) {
                        inventory.clickItem(foodID, "Eat");
                        break;
                    }
                }
                sleep(500, 1000);
            }
        }
        return false;
    }

    public boolean onTile(RSTile tile, String search, String action, double dx,
            double dy, int height) {
        if (!tile.isValid()) {
            return false;
        }

        Point checkScreen = null;
        checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
        if (!calculate.pointOnScreen(checkScreen)) {
            walkTile(tile);
            sleep(random(400, 800));
        }


        try {
            Point screenLoc = null;
            for (int i = 0; i < 30; i++) {
                screenLoc = Calculations.tileToScreen(tile, dx, dy, height);
                if (!calculate.pointOnScreen(screenLoc)) {
                    return false;
                }
                if (menu.getItems().toString().toLowerCase().contains(
                        search.toLowerCase())) {
                    break;
                }
                if (mouse.getLocation().equals(screenLoc)) {
                    break;
                }
                mouse.move(screenLoc.getLocation().x + random(3, 6), screenLoc.getLocation().y);
            }
            screenLoc = Calculations.tileToScreen(tile, height);
            if (menu.getItems().length <= 1) {
                return false;
            }
            sleep(random(100, 200));
            if (menu.getItems().toString().toLowerCase().contains(
                    action.toLowerCase())) {
                mouse.click(true);
                return true;
            } else {
                return menu.action(action);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void walkTile(final RSTile t) {
        if (!(calculate.distanceTo(walk.getDestination()) <= random(4, 7))) {
            if (player.getMine().isMoving()) {
                return;
            }
        }
        Point s = Calculations.tileToScreen(t);
        if (calculate.pointOnScreen(s)) {
            if (player.getMine().isMoving()) {
                return;
            }
            mouse.move(s, random(-3, 4), random(-3, 4));
            walk.tileOnScreen(t);
            sleep(random(400, 600));
            return;
        } else {
            walk.tileMM(t);
            sleep(random(400, 600));
            return;
        }
    }

    public void onRepaint(Graphics g) {
        final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
        int CurrentXP = skills.getCurrentXP(Skills.AGILITY);
        int RealLvL = skills.getRealLvl(Skills.AGILITY);
        int currentXP = skills.getCurrentXP(Skills.AGILITY);
        int currentLVL = skills.getCurrentLvl(Skills.AGILITY);
        int currentPurLVL = skills.getPercentToNextLvl(Skills.AGILITY);
        int XPToNextLvL = skills.getXPToNextLvl(Skills.AGILITY);
        final double XP = 139.5;//special skill define

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
            final int done = (int) (gainedXP / XP);
            final int donePerHour = (int) ((gainedXP / XP) * 3600000.0 / (double) runTime);
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
            g.drawString("Laps: " + done, x, 410);
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
            if (mouse == null) {
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
