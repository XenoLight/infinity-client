import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Map;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Shadow Storm"}, category = "Thieving", name = "Thieves Guild Pickpocketer 2.6", version = 2.6, description = "<html><body style=\"font-family: Arial; padding: 7px;\">"
+ "<center><strong>Thieve's Guild Pickpocketer 2.0</strong>"
+ "<br></br>"
+ "<br>Special thanks to Bonebag789 for testing, suggestions and comments.</br>"
+ "<br></br>"
+ "<input type='checkbox' name='using' value='true'> Using gloves of silence"
+ "<br></br>"
+ "<br>If using gloves of silence, please have enough replacements in bag.</br>"
+ "<br></br>"
+ "<br>Select the paint you want to use.</br>"
+ "<br></br>"
+ "<select name='painter'><option>Paint 1</option><option>Paint 2</option><option>No paint</option></select><br></font>"
+ "<br></br>"
+ "<br>Select your mouse speed, the lower, the faster.</br>"
+ "<br></br>"
+ "<select name='mspeed'><option>1<option>2<option>3<option>4<option>5<option>6<option>7<option>8<option>9<option>10</option></select><br></font>"
+ "<br></br>"
+ "<br>If you find ANY bug, report it on forums.</br>"
+ "<br></br>"
+ "<br>Enjoy thieving!"
+ "<br></br>"
+ "<br></br></center>")
public class ThievesGuildPP extends Script implements PaintListener,
        MessageListener {

    public int[] trainers = {11281, 11283, 11287, 11285};
    public int gloves = 10075;
    public long startTime = System.currentTimeMillis();
    public int timesPickpocketed, glovesUsed, gloveCount;
    public int ppcketPerHour;
    public int startLevel = skills.getCurrentLvl(STAT_THIEVING);
    public int startExp = skills.getCurrentXP(STAT_THIEVING);
    public int gainedLevels;
    public int expGained;
    public int expPerHour;
    public int failures, check, lastPCount, lastExp, lastPicks;
    public int failurePerHour;
    private int mspeed = 0;
    public long seconds, minutes, hours, trigerTime, lastPick;
    public boolean needGloves = false, usingGloves = false;
    public String scriptRunner = "Shadow Storm";
    public int paint = 0;
    private final Color color1 = new Color(204, 0, 204, 140);
    private final Color color2 = new Color(0, 0, 0);
    private final BasicStroke stroke1 = new BasicStroke(1);
    private final Font font1 = new Font("Calibri", 0, 14);
    private final Font font2 = new Font("Calibri", 0, 13);

    public boolean onStart(Map<String, String> args) {
        camera.setAltitude(true);
        getMouseSpeed();
        log("Setting up...");
        gloveCount = inventory.getCount(gloves);
        scriptRunner = args.get("userToStats");
        usingGloves = args.get("using") != null ? true : false;
        if (args.get("painter").equals("Paint 1")) {
            paint = 1;
        } else if (args.get("painter").equals("Paint 2")) {
            paint = 2;
        } else if (args.get("painter").equals("No paint")) {
            paint = 0;
        }
        if (args.get("mspeed").equals("1")) {
            mspeed = 1;
        } else if (args.get("mspeed").equals("2")) {
            mspeed = 2;
        } else if (args.get("mspeed").equals("3")) {
            mspeed = 3;
        } else if (args.get("mspeed").equals("4")) {
            mspeed = 4;
        } else if (args.get("mspeed").equals("5")) {
            mspeed = 5;
        } else if (args.get("mspeed").equals("6")) {
            mspeed = 6;
        } else if (args.get("mspeed").equals("7")) {
            mspeed = 7;
        } else if (args.get("mspeed").equals("8")) {
            mspeed = 8;
        } else if (args.get("mspeed").equals("9")) {
            mspeed = 9;
        } else if (args.get("mspeed").equals("10")) {
            mspeed = 10;
        }
        log("Done...Enjoy");
        return true;
    }

    @Override
    public int getMouseSpeed() {
        return random((mspeed - 2), (mspeed + 2));
    }

    public int loop() {
        if (System.currentTimeMillis() - lastPick >= 6000) {
            failsafe();
        } else {
            pickPocket();
            performStun();
            useGloves();
        }
        return random(400, 500);
    }

    public void pickPocket() {
        RSNPC theNPC = npc.getNearestByID(trainers);
        if (theNPC != null) {
            if (theNPC.distanceTo() <= 6) {
                getMouseSpeed();
                theNPC.action("Pickpocket Pick");
                wait(random(500, 600));
                while (player.getMine().isMoving()) {
                    wait(random(400, 450));
                }
            } else if (theNPC.distanceTo() >= 7) {
                getMouseSpeed();
                walk.tileMM(theNPC.getLocation());
                while (player.getMine().isMoving()) {
                    wait(random(300, 350));
                }
            }
        }
    }

    public boolean performStun() {
        while (System.currentTimeMillis() - trigerTime < 4500) {
            antiBan();
        }
        return false;
    }

    public boolean useGloves() {
        if (usingGloves = true && needGloves != false
                && inventory.contains(gloves)) {
            inventory.clickItem(gloves, "Wear");
            glovesUsed++;
        } else {
            return false;
        }
        needGloves = false;
        return false;
    }

    public boolean failsafe() {
        RSNPC theNPC = npc.getNearestByID(trainers);
        if (theNPC.distanceTo() >= 7) {
            walk.tileMM(theNPC.getLocation());
            while (player.getMine().isMoving()) {
                wait(random(300, 350));
            }
        } else {
            pickPocket();
        }
        return false;
    }

    public void onFinish() {
        if (lastExp == 0) {
            lastExp = skills.getCurrentXP(STAT_THIEVING);
        }
        if (lastPicks == 0) {
            lastPicks = timesPickpocketed;
        }
        log(" [-------------- Finished ---------------]");
        log(" [ Experience gained = " + expGained);
        log(" [ Levels gained = " + gainedLevels);
        log(" [ Time ran = " + hours + ":" + minutes + ":" + seconds);
        log(" [ Experience p/hour = " + expPerHour);
        log(" [--------- Thanks for using me! --------]");

    }

    private void drawTile(Graphics render, RSTile tile, Color color,
            boolean drawCardinalDirections) {
        Point southwest = Calculations.tileToScreen(tile, 0, 0, 0);
        Point southeast = Calculations.tileToScreen(new RSTile(tile.getX() + 1,
                tile.getY()), 0, 0, 0);
        Point northwest = Calculations.tileToScreen(new RSTile(tile.getX(),
                tile.getY() + 1), 0, 0, 0);
        Point northeast = Calculations.tileToScreen(new RSTile(tile.getX() + 1,
                tile.getY() + 1), 0, 0, 0);

        if (calculate.pointOnScreen(southwest) && calculate.pointOnScreen(southeast)
                && calculate.pointOnScreen(northwest) && calculate.pointOnScreen(northeast)) {
            render.setColor(Color.BLACK);
            render.drawPolygon(new int[]{(int) northwest.getX(),
                        (int) northeast.getX(), (int) southeast.getX(),
                        (int) southwest.getX()}, new int[]{
                        (int) northwest.getY(), (int) northeast.getY(),
                        (int) southeast.getY(), (int) southwest.getY()}, 4);
            render.setColor(color);
            render.fillPolygon(new int[]{(int) northwest.getX(),
                        (int) northeast.getX(), (int) southeast.getX(),
                        (int) southwest.getX()}, new int[]{
                        (int) northwest.getY(), (int) northeast.getY(),
                        (int) southeast.getY(), (int) southwest.getY()}, 4);

            if (drawCardinalDirections) {
                render.setColor(Color.BLACK);
                render.drawString(".", southwest.x, southwest.y);
                render.drawString(".", southeast.x, southeast.y);
                render.drawString(".", northwest.x, northwest.y);
                render.drawString(".", northeast.x, northeast.y);
            }
        }
    }

    public void onRepaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        RSNPC theNPC = npc.getNearestByID(trainers);
        expGained = skills.getCurrentXP(STAT_THIEVING) - startExp;
        ppcketPerHour = (int) (3600000D / (System.currentTimeMillis() - startTime) * (timesPickpocketed));
        long millis = System.currentTimeMillis() - startTime;
        expPerHour = (int) ((expGained) * 3600000D / (System.currentTimeMillis() - startTime));
        gainedLevels = skills.getCurrentLvl(STAT_THIEVING) - startLevel;
        hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        seconds = millis / 1000;

        if (paint == 1) {
            g.setColor(new Color(0, 0, 0, 146));
            g.fillRect(6, 6, 163, 225);
            g.setFont(new Font("Tempus Sans ITC", 0, 13));
            g.setColor(new Color(204, 0, 204));
            g.drawString("Thieves Guild Pickpocketer", 13, 28);
            g.setFont(new Font("Tempus Sans ITC", 0, 12));
            g.drawString("Exp. gained: " + expGained, 13, 54);
            g.drawString("Exp. per hour: " + expPerHour, 13, 76);
            g.drawString("Exp. to level: "
                    + skills.getXPToNextLvl(STAT_THIEVING), 13, 98);
            g.drawString("Levels gained: " + gainedLevels, 13, 120);
            g.drawString("Current level: "
                    + skills.getCurrentLvl(STAT_THIEVING), 13, 144);
            g.drawString("Pickpockets made: " + timesPickpocketed, 13, 164);
            g.drawString("Pickpockets failed: " + failures, 13, 185);
            g.drawString("Gloves used: " + glovesUsed, 13, 204);
            g.drawString("Time running: " + hours + ":" + minutes + ":"
                    + seconds, 13, 225);
            g.drawLine(11, 211, 160, 211);
            g.drawLine(160, 211, 160, 211);

            // Mouse
            Point m = mouse.getLocation();
            g.setFont(new Font("Tempus Sans ITC", 0, 13));
            g.setColor(new Color(204, 0, 204));
            g.drawString("TGPP", m.x + 2, m.y + 12);
            g.drawRect(m.x, m.y, 33, 13);
            g.drawOval(m.x, m.y, 3, 2);
            g.fillOval(m.x - 2, m.y - 2, 5, 5);

            // Tile
            if (theNPC != null) {
                if (theNPC.isOnScreen()) {
                    drawTile(g, theNPC.getLocation(), new Color(204, 0, 204,
                            190), true);
                }
            }
        } else if (paint == 2) {
            g.setColor(color1);
            g.fillRoundRect(4, 4, 511, 94, 16, 16);
            g.setColor(color2);
            g.setStroke(stroke1);
            g.drawRoundRect(4, 4, 511, 94, 16, 16);
            g.setFont(font1);
            g.drawString("Thieve's Guild Pickpocketer", 173, 25);
            g.drawRoundRect(160, 10, 180, 20, 16, 16);
            g.setFont(font2);
            g.drawString("Time running: " + hours + ":" + minutes + ":"
                    + seconds, 26, 52);
            g.drawString("Exp gained: " + expGained, 26, 71);
            g.drawString("Exp per hour: " + expPerHour, 26, 91);
            g.drawString("Exp to level: "
                    + skills.getXPToNextLvl(STAT_THIEVING), 211, 51);
            g.drawString("Current level: "
                    + skills.getCurrentLvl(STAT_THIEVING), 211, 71);
            g.drawString("Levels gained: " + gainedLevels, 211, 90);
            g.drawString("Pickpockets: " + timesPickpocketed, 391, 51);
            g.drawString("Failures: " + failures, 391, 71);
            g.drawString("Gloves used: " + glovesUsed, 391, 90);

            // Mouse
            Point m = mouse.getLocation();
            g.setColor(color1);
            g.fillOval(m.x - 2, m.y - 2, 7, 7);

            // Tile
            if (theNPC != null) {
                if (theNPC.isOnScreen()) {
                    drawTile(g, theNPC.getLocation(), color1, true);
                }
            }
        } else if (paint == 0) {
        }

    }

    public void messageReceived(final MessageEvent e) {
        final String message = e.getMessage();
        if (message.contains("You retrieve")) {
            timesPickpocketed++;
            lastPick = System.currentTimeMillis();
        }
        if (message.contains("fail")) {
            failures++;
            check++;
            trigerTime = System.currentTimeMillis();
        }
        if (message.contains("worn out")) {
            needGloves = true;
        }
    }

    public boolean antiBan() {
        int randomNumber = random(1, 4);
        if (randomNumber <= 15) {
            if (randomNumber == 1) {
                game.openTab(TAB_STATS);
                mouse.move(random(620, 665), random(300, 320));
                wait(random(2000, 2100));
            }
            if (randomNumber == 2) {
                mouse.move(random(50, 700), random(50, 450), 2, 2);
                wait(random(1200, 1300));
                mouse.move(random(50, 700), random(50, 450), 2, 2);
            }
            if (randomNumber == 3) {
                mouse.move(522, 188, 220, 360);
                wait(random(1000, 1250));
            }
            if (randomNumber == 4) {
                camera.setRotation(random(1, 360));
            }
            if (randomNumber == 5) {
                game.openTab(TAB_STATS);
                mouse.move(random(619, 665), random(300, 320));
                wait(random(1000, 1500));
            }
        }
        return true;
    }
}