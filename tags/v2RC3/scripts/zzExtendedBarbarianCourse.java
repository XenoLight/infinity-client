
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

// zzSleepzz - Added extended barbarian course support to original script.
@ScriptManifest(authors = {"Jacmob, zzSleepzz"},
category = "Agility",
name = "zzExtended Barbarian Course",
version = 2.0,
description =
"<html>\n<body style=\"font-family: Arial; background-color: #DDFFDD;\">\n"
+ "<div style=\"width: 100%; height: 35px; background-color: #BBEEBB; text-align: center;\"\n<h2 style=\"color: #118811;\">Extended Barbarian Course</h2>\n</div>\n"
+ "<div style=\"width:100%; background-color: #007700; text-align:center; color: #FFFFFF; height: 15px;\">Jacmob and zzSleepzz"
+ " | Version 2.0</div>\n"
+ "<div style=\"width: 100%; padding: 10px; padding-bottom: 12px; background-color: #EEFFEE;\">Start in the Barbarian Agility Course.<br><br>Food and energy potions are supported.</div>\n<div style=\"width: 100%; padding: 10px;\">\n<h3>Auto Stop (Enter Runtime to Enable)</h3><input type=\"text\" name=\"hours\" id=\"hrs\" size=3 /><label for=\"hrs\" > : </label><input type=\"text\" name=\"mins\" id=\"mins\" size=3 /><label for=\"mins\"> : </label><input type=\"text\" name=\"secs\" id=\"secs\" size=3 /><label for=\"secs\"> (hrs:mins:secs)</label><br /><br /><input type=\"checkbox\" name=\"chkXP\" id=\"chkXP\" value=\"true\" /><label for=\"debug\">Check XP (Extra AntiBan)</label></div>\n</body>\n</html")
//TODO: check for being totally away from area (in random) and stop script if
//         random handler doesn't catch it.
public class zzExtendedBarbarianCourse extends Script implements PaintListener {

    public ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);
    public final int[] Food = new int[]{333, 385, 379, 285, 373, 365,
        7946, 361, 397, 391, 1963, 329, 2118};
    public final int[] energyPot = new int[]{3014, 3012, 3010, 3008,
        3022, 3020, 3018, 3016};
    public int LapsDone = 0;
    private boolean randomsState;
    private boolean lapJustDone = false;
    private boolean lapBegun = false;
    private boolean checkXP = false;
    private long nextXPcheck = -1;
    private int RunningEnergy = random(15, 30);
    private int DrinkingEnergy = -1;
    private int currentFails = 0;
    private long stopTime = -1;
    private RSObject paintit;

    protected int getMouseSpeed() {
        return random(5, 9);
    }

    private RSTile checkTile(final RSTile tile) { // most credits to Fusion89k
        if (tile.distanceTo() < 15) {
            return tile;
        }
        final RSTile loc = player.getMine().getLocation();
        final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc.getY() + tile.getY()) / 2);
        return walk.isOnMinimap() ? walk : checkTile(walk);
    }

    private void Drink() {
        while (inventory.getCount(this.energyPot) >= 1
                && player.getMyEnergy() <= DrinkingEnergy) {
            DrinkingEnergy = random(10, 40);
            for (final int element : this.energyPot) {
                if (inventory.getCount(element) == 0) {
                    continue;
                }
                log("Drinking energy potion.");
                inventory.clickItem(element, "Drink");
                wait(random(500, 800));
                break;
            }
        }
    }

    private boolean Eat() {
        for (final int element : this.Food) {
            if (inventory.getCount(element) >= 1) {
                inventory.clickItem(element, "Eat");
                return true;
            }
        }
        return false;
    }

    private String getFormattedTime(final long timeMillis) {
        long ss = 0, mm = 0, hh = 0;

        ss = timeMillis / 1000;
        if (ss >= 60) {
            mm = ss / 60;
            ss -= (mm * 60);
        }
        if (mm >= 60) {
            hh = mm / 60;
            mm -= (hh * 60);
        }

        String hoursString = "";
        String minutesString = "";
        String secondsString = ss + "";
        String type = "seconds";

        if (mm > 0) {
            minutesString = mm + ":";
            type = "minutes";
        } else if (hh > 0 && ss > 0) {
            minutesString = "0:";
        }
        if (hh > 0) {
            hoursString = hh + ":";
            type = "hours";
        }
        if (mm < 10 && type != "seconds") {
            minutesString = "0" + minutesString;
        }
        if (hh < 10 && type == "hours") {
            hoursString = "0" + hoursString;
        }
        if (ss < 10 && type != "seconds") {
            secondsString = "0" + secondsString;
        }

        return hoursString + minutesString + secondsString + " " + type;
    }
    private static final RSTile StartCoord = new RSTile(2553, 3554);
    private static final RSTile FailCoord = new RSTile(2548, 9951);
    private static final RSTile Fail2Coord = new RSTile(2548, 3551);
    private static final RSTile LogCoord = new RSTile(2552, 3549);
    private static final RSTile Log2Coord = new RSTile(2550, 3546);
    private static final RSTile LogFailCoord = new RSTile(2545, 3542);
    private static final RSTile Wall1Coord = new RSTile(2538, 3541);
    private static final RSTile Wall2Coord = new RSTile(2537, 3546);
    private static final RSTile SpringCoord = new RSTile(2532, 3544);
    private static final RSArea SpringPlatform =
            new RSArea(SpringCoord, new RSTile(2537, 3548));
    private static final RSTile RopeCoord = new RSTile(2534, 3553);
    private static final RSTile GapCoord = new RSTile(2537, 3553);
    private static final RSTile RoofCoord = new RSTile(2540, 3553);
    private static final RSTile EndCoord = new RSTile(2543, 3553);
    //private RSTile rope;

    private int getState() {
        if (StartCoord.distanceTo() < 3) {
            /*
            int roll = random(1,1000);

            if (roll < 500)  {
            rope = new RSTile(2551,3553);
            }
            else
            rope = new RSTile (2551,3554);
            }
             */
            //rope = new RSTile(2551, 3553);

            return 1;
        }
        if (Fail2Coord.distanceTo() < 2 && game.getPlane() == 0) {
            return 10;
        }
        if ((LogCoord.distanceTo() < 3 || Log2Coord.distanceTo() < 2)
                && game.getPlane() == 0) {
            return 2;
        }
        if (LogFailCoord.distanceTo() < 4 && game.getPlane() == 0) {
            return 3;
        }
        if (Wall1Coord.isOnScreen() && game.getPlane() == 0) {
            return 4;
        }
        if (Wall2Coord.distanceTo() < 4 && game.getPlane() == 2) {
            return 5;
        }
        if (SpringPlatform.contains(player.getMyLocation()) && game.getPlane() == 3) {
            return 6;
        }
        RSTile t = new RSTile(2536, 3553);
        if (t.distanceTo() > 2 && RopeCoord.isOnScreen() && game.getPlane() == 3) {
            return 7;
        }
        if (player.getMyLocation().equals(new RSTile(2536, 3553)) && GapCoord.isOnScreen()
                && player.getMine().getLocation().getX() < 2538 && game.getPlane() == 3) {
            return 8;
        }
        if (RoofCoord.distanceTo() < 4
                && player.getMine().getLocation().getX() < 2543 && game.getPlane() == 2) {
            return 9;
        }
        if (EndCoord.distanceTo() < 5 && game.getPlane() == 0) {
            return 10;
        }
        if (FailCoord.distanceTo() < 20) {
            return 11;
        }

        return -1;
    }

    public double getVersion() {
        return 1.0;
    }

    private void hoverAgility() {
        final RSInterfaceChild agitab = iface.get(320).getChild(
                132);
        game.openTab(Game.tabStats);
        log("Checking agility stat.");
        mouse.move(new Point(agitab.getAbsoluteX()
                + random(2, agitab.getWidth() - 1), agitab.getAbsoluteY()
                + random(2, agitab.getHeight() - 1)));
        wait(random(900, 2000));
    }

    @Override
    public int loop() {
        if (currentFails > 80) {
            log("The script has failed multiple times. Logging off.");
            stopScript(false);
        }

        if (stopTime != -1 && scriptStartTime != -1
                && System.currentTimeMillis() - scriptStartTime > stopTime) {
            log("Stop Time Reached. Logging off in 10 seconds.");
            wait(random(10000, 12000));
            stopScript(false);
        }

        if (checkXP && System.currentTimeMillis() >= nextXPcheck) {
            nextXPcheck = System.currentTimeMillis()
                    + random(1000 * 60 * 3, 1000 * 60 * 5);
            hoverAgility();
        }

        int energy = player.getMyEnergy();
        if (energy >= RunningEnergy && !isRunning()) {
            game.setRun(true);
            RunningEnergy = random(15, 30);
            wait(random(400, 500));
        }

        if (energy != 0 && energy <= DrinkingEnergy) {
            Drink();
        }

        if (skills.getCurrentLvl(3) <= 15) {
            log("Health is below 15. Eating food...");
            if (!Eat()) {
                log("No food to eat. Waiting 10 seconds.");
                wait(random(10000, 12000));
                if (skills.getCurrentLvl(3) > 17) {
                    log("Failure correction activated. Logout cancelled.");
                    return random(100, 200);
                }
                game.logout();
                log("No food to eat. Logged out.");
                stopScript(false);
            } else {
                wait(random(800, 1000));
                Eat();
                return random(400, 500);
            }
        }

        final RSPlayer me = player.getMine();
        final int state = getState();

        if (me.getAnimation() != -1 || me.isMoving()) {
            if (lapJustDone) {
                // camera.setRotation(random(178, 182));
                LapsDone++;
                lapJustDone = false;
            }
            return random(50, 200);
        }

        switch (state) {
            case 0:
                break;
            case -1: // Failure!
                if (currentFails > 30 && game.getPlane() == 0) {
                    log("Unknown Location - Returning To Start");
                    walk.to(StartCoord);
                }
                currentFails++;
                break;

            case 1: // Rope
                if (StartCoord.distanceTo() > 4) {
                    walk.to(StartCoord);
                    //camera.setRotation(random(85,95));
                    wait(random(50, 100));
                } else {
                	RSObject rope = objects.getNearestByName("Rope swing");
                	if (rope==null)  {
                		return 10;
                	}
                	paintit = rope;
                	
                    // For now use a hard-coded screen area to click
                    // requires camera pointing to west to work.
                    //if (clickSpot(182, 190, 67, 170, "Swing-on",90))  {
                    //int x1 = rope.getScreenLocation().x;
                    //int y1 = rope.getScreenLocation().y;
                    //if (clickSpot(x1 - 30, x1 - 25, y1 + 20, y1 - 20, "Swing-on", 90)) {
                    if (rope.action("Swing-on")) {
                        //if (atTile3(rope, "Swing-on",0,-15,8)) {
                        player.waitForAnim(1400);
                        wait(random(1100, 1300));
                    } //else {
                    //camera.setRotation(random(85,95));
                    //currentFails++;
                    //}
                }
                lapBegun = true;
                break;
            case 2: // Log balance
            	mouse.move(Log2Coord.getScreenLocation());
            	RSObject logbal = objects.getNearestByName("Log balance");
            	if (logbal==null)  {
            		return 100;
            	}

            	// This object's model is such that it causes its draw to loop
            	// for a very long time.
            	paintit = null;
            	
                if (logbal.action("Walk-across")) {
                    camera.setRotation(random(88, 92));
                    player.waitToMove(2000);
                    wait(random(500, 600));
                } else {
                    currentFails++;
                }
                break;
            case 3: // Log Failed
                walk.to(new RSTile(2551, 3546));
                wait(random(500, 700));
                break;
            case 4: // Wall 1
            	RSObject wall1 = objects.getTopAt(Wall1Coord);
            	if (wall1==null) {
            		return 100;
            	}
            	
            	paintit = wall1;
            	if (wall1.action("Run-up"))  {
                //if (atTile3(Wall1Coord, "Run-up", 0, -10)) {
                    wait(random(200, 300));
                    player.waitForAnim(1500);
                    paintit=null;
                    wait(random(2100, 2300));
                } else {
                    currentFails++;
                }
                break;
            case 5: // Wall 2
            	RSObject wall2 = objects.getNearestByName("Wall");
            	if (wall2==null)  {
            		return 100;
            	}
            	
            	// This object's model is such that it causes its draw to loop
            	// for a very long time.
            	paintit = null;
            	
                if (wall2.action("Climb-up")) {              
                //if (atTile3(Wall2Coord, "Climb-up", 7, 0)) {
                    player.waitForAnim(1500);
                    wait(random(1100, 1300));
                } else if (currentFails > 0 && currentFails % 20 == 0) {
                    camera.turnTo(new RSTile(2535, 3547));
                } else {
                    currentFails++;
                }
                break;
            case 6: // Spring device
            	RSObject spring = objects.getNearestByName("Spring device");
            	paintit = spring;
            	
                if (spring.action("Fire")) {
                //if (atTile3(SpringCoord, "Fire")) {
                    player.waitForAnim(2000);
                    wait(random(3200, 3400));
                } else if (currentFails > 0 && currentFails % 20 == 0) {
                    camera.turnTo(new RSTile(2532, 3545));
                } else {
                    currentFails++;
                }
                break;
            case 7: // Balance beam
                randomsState = Bot.disableRandoms;
                Bot.disableRandoms = true;
                
            	RSObject bbeam = objects.getNearestByName("Balance beam");
            	paintit = bbeam;
            	
                bbeam.action("Cross");                
                //atTile3(RopeCoord, "Cross", 0, 0, 5);
                player.waitForAnim(500);
                wait(random(1300, 1500));
                break;
            case 8: // Gap
                Bot.disableRandoms = randomsState;
                
            	RSObject gap = objects.getNearestByName("Gap");
            	paintit = gap;
            	
                if (gap.action("Jump-over")) {
                //if (atTile3(GapCoord, "Jump-over")) {
                    player.waitForAnim(2000);
                    wait(random(600, 800));
                } else if (currentFails > 0 && currentFails % 20 == 0) {
                    camera.turnTo(new RSTile(2537, 3553));
                } else {
                    currentFails++;
                }
                break;
            case 9: // Roof
            	RSObject roof = objects.getNearestByName("Roof");
            	paintit = roof;
            	
                if (roof.action("Slide-down")) {
                //if (atTile3(RoofCoord, "Slide-down", 0, 15)) {
                    player.waitForAnim(2000);
                    wait(random(400, 600));
                    if (lapBegun) {
                        lapJustDone = true;
                    }
                    lapBegun = false;
                } else if (currentFails > 0 && currentFails % 20 == 0) {
                    camera.turnTo(new RSTile(2543, 3553));
                } else {
                    currentFails++;
                }
                break;
            case 10: // Return To Start
                currentFails = 0;
                walk.to(checkTile(new RSTile(2552, 3553)), 0, 1);
                wait(random(400, 750));
                break;
            case 11:
            	RSTile t = new RSTile(2547, 9951);
                if (t.distanceTo() > 5) {
                    walk.to(new RSTile(2548, 9951));
                }
                
                RSObject ladder = objects.getNearestByName("Ladder");
                
                paintit=ladder;
                
                if (ladder.action("Climb-up"))  {
                    wait(random(400, 600));
                } else if (currentFails > 0 && currentFails % 20 == 0) {
                	 camera.turnTo(new RSTile(2547, 9951));
                } else {
                    currentFails++;
                }
                break;
            default: // Stop Script
                return -1;
        }

        return random(100, 200);
    }

    // Let's try a different tactic from random...
    // Let's start from -40,-40 off the point and work to 40,40 and
    // see if we sweep across the hot spots.
    public boolean clickSpot(int x1, int x2, int y1, int y2, String action, int dir) {
        // Often, the mouse will have landed on the correct spot during a
        // previous attempt.  Take advantage of it if possible.
        if (menu.action(action)) {
            return true;
        }

        // Only change the angle to the requested angle
        // if the current is more than +/-5 of the
        // requested angle.
        int angle = camera.getAngle();
        if (angle < dir - 3 || angle > dir + 3) {
            camera.setAltitude(true);
            wait(random(20, 30));
            camera.setRotation(dir);
            wait(random(100, 150));
        }

        Point p;

        p = new Point(random(x1, x2), random(y1, y2));

        if ((p != null) && calculate.pointOnScreen(p)) {
            mouse.move(p);

            return menu.action(action);
            //wait(random(1,3));
            //if (getMenuActions().contains(action))  {
            //   mouse.click(true);
            //   return true;
            //}
        }

        return false;
    }

    public void onFinish() {
        log("Gained "
                + (skills.getCurrentXP(Constants.STAT_AGILITY) - startXP)
                + " XP ("
                + (skills.getRealLvl(Constants.STAT_AGILITY) - startLevel) + " levels) in "
                + getFormattedTime(System.currentTimeMillis() - scriptStartTime)
                + ".");
    }
    // Proggy originally based on ProFisher2's proggy
    // Modified and enhanced by zzSleepzz
    private long scriptStartTime = -1;
    private int startXP = 0, lastXP = 0;
    private int startLevel = 0;
    private int index = STAT_AGILITY;

    public void onRepaint(Graphics g) {
        Color PERCBAR = new Color(255, 255, 0, 150);

        long runTime = 0;
        long ss = 0, mm = 0, hh = 0;
        int expGained = 0;
        int levelsGained = 0;

        if (!game.isLoggedIn()) {
            return;
        }

        if (scriptStartTime == -1) {
            scriptStartTime = System.currentTimeMillis();
        }

        if (lastXP == 0) {
            lastXP = skills.getCurrentXP(index);
        }

        if (startXP == 0) {
            startXP = skills.getCurrentXP(index);
        }

        if (startLevel == 0) {
            startLevel = skills.getCurrentLvl(index);
        }

        // Calculate current runtime.
        runTime = System.currentTimeMillis() - scriptStartTime;
        ss = runTime / 1000;
        if (ss >= 60) {
            mm = ss / 60;
            ss -= (mm * 60);
        }
        if (mm >= 60) {
            hh = mm / 60;
            mm -= (hh * 60);
        }

        // Calculate experience gained.
        expGained = skills.getCurrentXP(index) - startXP;

        // Calculate levels gained
        levelsGained = skills.getCurrentLvl(index) - startLevel;

        int x = 552;
        int y = 324;
        int boxwidth = 190;

        g.setColor(new Color(255, 50, 50, 75));
        g.fillRoundRect(x - 7, y, boxwidth, 138, 15, 15);
        g.setColor(new Color(215, 40, 40, 250));
        g.drawRoundRect(x - 7, y, boxwidth, 138, 15, 15);
        g.drawRoundRect(x - 6, y - 1, boxwidth, 140, 15, 15);

        long runmins = mm + (hh * 60);
        Font f = g.getFont();  // Save for restoring after setting title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC, 11);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString(props.name() + "  v" + props.version(), x, y += 13);

        g.setFont(f);
        g.setColor(Color.ORANGE);
        g.drawString("  by " + props.authors()[0], x, y += 13);

        g.setColor(Color.WHITE);
        g.drawString("Run time:  " + hh + ":" + mm + ":" + ss, x, y += 18);
        g.drawString("Laps: " + LapsDone, x, y += 13);
        g.drawString("XP gained: " + expGained, x, y += 13);
        g.drawString("Levels gained: " + levelsGained, x, y += 13);

        //Progress bar from Jacmob's scripts
        g.setColor(Color.RED);
        g.fill3DRect(x, y += 3, boxwidth - 10, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - 10) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);

        g.setColor(Color.WHITE);
        g.drawString("XP to level:  " + skills.getXPToNextLvl(index) + " (" + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        if (runmins > 0 && expGained > 0) {
            float lvlmins = skills.getXPToNextLvl(index) / (expGained / runmins);
            int lvlhrs = (int) lvlmins / 60;
            g.drawString("Level " + (skills.getCurrentLvl(index) + 1) + " in: "
                    + lvlhrs + " hours, " + (int) (lvlmins - (lvlhrs * 60)) + " mins", x, y += 14);
            g.drawString("XP per hour : " + (expGained / runmins) * 60, x, y += 13);
        } else {
            g.drawString("Level " + (skills.getCurrentLvl(index) + 1) + " in: ", x, y += 14);
            g.drawString("XP per hour : ", x, y += 13);
        }
        
        if (paintit!=null)  {
        	paintit.drawModel(g);
        }
    }

    @Override
    public boolean onStart(final Map<String, String> args) {
        if (args.get("chkXP") == null) {
            nextXPcheck = System.currentTimeMillis() + random(1000 * 60 * 3, 1000 * 60 * 5);
            checkXP = false;
        }
        if (!(args.get("hours").equals("") && args.get("mins").equals("") && args.get("secs").equals(""))) {
            int sHours = 0, sMins = 0, sSecs = 0;
            if (!args.get("hours").equals("")) {
                sHours = Integer.parseInt(args.get("hours"));
            }
            if (!args.get("mins").equals("")) {
                sMins = Integer.parseInt(args.get("mins"));
            }
            if (!args.get("secs").equals("")) {
                sSecs = Integer.parseInt(args.get("secs"));
            }
            stopTime = sHours * 3600000 + sMins * 60000 + sSecs * 1000;
            log("Script will stop after " + getFormattedTime(stopTime));
        }

        randomsState = Bot.disableRandoms;
        return true;
    }
}