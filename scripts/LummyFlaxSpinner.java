
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
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Infinity Dev Team"},
name = "Lummy Flax Spinner",
version = 1.0,
category = "Crafting",
website = "http://www.lazygamerz.org/forums/index.php?topic=3199.new#new",
summary = "This script runs in Lumberage castle. The user "
+ "should have all the flax they would like to "
+ "use in their bank on the top row of the tab "
+ "it is in and the bank on the tab with the flax "
+ "on it.",
notes = "Version 1.0:<br>"
+ "04-29-2011: Script development started<br>",
description = "<html><style type='text/css'>"/*start html option code*/
+ "body {background:url('http://lazygamerz.org/client/images/back_1.png') repeat}"/*background image*/
+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"/*header image*/
+ "</head><br><body>"
/*start box*/
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
+ "<td width=90% align=justify>"
+ "<center><font color=#000000>Infinity Lummy Flax Spinner</font></center><br />"/*script title*/
+ "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"/*top box label*/
/*box statment*/
+ "<font size=3>Start the script in Lumbridge Castle with flax visible at the top of your bank...<br>"
+ "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"/*ending tag label*/
+ "</td></tr></table><br />"/*end box*/)
public class LummyFlaxSpinner extends Script implements PaintListener {

    private enum State {

        TOBANK, TOSPIN, OPENBANK, OPENSPIN, CLIMB, BANK, SPIN
    }
    public final int ani = 1563;
    public final int flax = 1779;
    public final int bowstring = 1777;
    public final int[] bankArea = {3207, 3210, 3217, 3220};
    public final RSTile bankTile = new RSTile(3208, 3221);
    public final RSTile spinerTile = new RSTile(3209, 3212);
    public final RSTile walkBankTile = new RSTile(3208, 3219);
    public final RSTile stairsTile = new RSTile(3205, 3208);
    public final RSTile stairsGroundTile = new RSTile(3204, 3208);
    public final RSTile Tile = new RSTile(3207, 3210);
    private int runEnergy = random(40, 95);
    private int flaxSpun = 0;
    private int flaxPrice = 0;
    private int stringPrice = 0;
    private boolean setAltitude = true;
    public String status = "";
    int startXP = 0;
    int startLvl = 0;
    long startTime = System.currentTimeMillis();
    BufferedImage img = null;

    private void antiBan(final int max) {
        final int rand = random(0, max);
        if (rand == 69) {
            if (game.getCurrentTab() == Constants.TAB_STATS) {
                game.openTab(Constants.TAB_INVENTORY);
                wait(random(50, 1000));
            }
            final Point screenLoc = Calculations.tileToScreen(player.getMine().getLocation());
            mouse.move(screenLoc, 3, 3, 5);
            wait(random(50, 300));
            mouse.click(false);
            wait(random(500, 2500));
            while (menu.isOpen()) {
                moveMouseRandomly(700);
                wait(random(100, 500));
            }
        } else if (rand == 68) {
            if (game.getCurrentTab() != Constants.TAB_STATS) {
                game.openTab(Constants.TAB_STATS);
                wait(random(200, 400));
                if (random(0, 2) == 1) {
                	mouse.move(random(575, 695), random(240, 435), 10);
                }
                mouse.move(632, 372, 7, 7);
                wait(random(800, 1400));
            } else if (game.getCurrentTab() == Constants.TAB_STATS) {
                game.openTab(Constants.TAB_INVENTORY);
                wait(random(800, 1200));
            }
        } else if (rand == 67) {
            final int rand2 = random(1, 3);
            for (int i = 0; i < rand2; i++) {
            	mouse.move(random(100, 700), random(100, 500));
                wait(random(200, 700));
            }
            mouse.move(random(0, 800), 647, 50, 100);
            wait(random(100, 1500));
            mouse.move(random(75, 400), random(75, 400), 30);
        } else if (rand == 0) {
            rotateCamera();
        } else if (rand < 4) {
            moveMousesSlightly();
        }
    }

    private State getState() {
        if (game.getPlane() == 0) {
            return State.CLIMB;
        } else if (inventory.contains(flax)) {
            if (game.getPlane() == 2) {
                return State.TOSPIN;
            } else if (iface.get(916).isValid() && iface.get(905).isValid()) {
                return State.SPIN;
            } else {
                return State.OPENSPIN;
            }
        } else {
            if (game.getPlane() == 1) {
                return State.TOBANK;
            } else if (bank.isOpen()) {
                return State.BANK;
            } else {
                return State.OPENBANK;
            }
        }
    }

    private void startRunning(final int energy) {
        if (player.getMyEnergy() >= energy && !isRunning()) {
            runEnergy = random(40, 95);
            game.setRun(true);
            sleep(random(500, 750));
        }
    }

    @Override
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
        startRunning(runEnergy);
        final State state = getState();
        int tries = 0;
        antiBan(65);
        switch (state) {
            case TOSPIN:
                if (tile.onScreen(stairsTile)) {
                    if (bank.isOpen()) {
                        bank.close();
                    }
                    if (!tile.click(stairsTile, "Climb-down")
                            && !player.getMine().isMoving()) {
                        rotateCamera();
                    }
                    while (game.getPlane() != 1 && tries < 10) {
                        tries++;
                        wait(random(100, 200));
                    }
                } else {
                    walkToTile(stairsTile);
                    while (calculate.distanceTo(stairsTile) > 4 && tries < 10) {
                        tries++;
                        wait(random(400, 500));
                    }
                }
                break;
            case TOBANK:
                if (iface.get(916).isValid() && inventory.contains(flax)) {
                    iface.clickChild(905, 16, "Make All");
                    //clickMouse(random(480, 485), random(42, 48), true);
                }
                if (tile.onScreen(stairsTile)) {
                    if (!tile.click(stairsTile, "Climb-up")
                            && !player.getMine().isMoving()) {
                        rotateCamera();
                        break;
                    } else {
                        if (random(0, 5) != 1) {
                        	mouse.move(random(608, 640), random(50, 90), 5);
                            if (random(0, 5) != 1) {
                                moveMouseAway(5);
                            }
                        }
                    }
                    while (game.getPlane() != 2 && tries < 15) {
                        tries++;
                        wait(random(200, 400));
                    }
                } else {
                    walkToTile(stairsTile);
                    while (calculate.distanceTo(stairsTile) > 4 && tries < 10) {
                        tries++;
                        wait(random(400, 500));
                    }
                }
                break;
            case OPENBANK:
                if (playerIsInArea(bankArea)) {
                    tile.click(bankTile, "Use-quickly");
                    while (!bank.isOpen() && tries < 5) {
                        tries++;
                        wait(random(400, 600));
                    }
                } else {
                    walkToTile(walkBankTile);
                    wait(random(200, 700));
                    while (!playerIsInArea(bankArea) && tries < 10) {
                        tries++;
                        wait(random(400, 600));
                    }
                }
                break;
            case OPENSPIN:
                if (tile.onScreen(spinerTile)) {
                    if (tile.click(spinerTile, "Spin")) {
                        moveMouseAway(50);
                    } else {
                        break;
                    }
                    while (!iface.get(916).isValid() && tries < 10) {
                        if (player.getMine().isMoving()) {
                            tries = 2;
                        }
                        tries++;
                        wait(random(400, 600));
                        antiBan(65);
                    }
                } else {
                    camera.turnTo(spinerTile, 20);
                }
                break;
            case SPIN:
                int stringsPreviouslyHeld = inventory.getCount(bowstring);
                if (iface.clickChild(905, 16, "Make All")) {
                    if (random(0, 2) == 1) {
                        moveMouseAway(50);
                    }
                    wait(random(1000, 1200));
                    if (iface.get(916).isValid()) {
                        break;
                    }
                } else {
                    break;
                }
                while (tries < 15 && inventory.contains(flax)) {
                    if (player.getMine().getAnimation() == ani) {
                        tries = 0;
                    } else {
                        tries++;
                    }
                    wait(random(300, 500));
                    antiBan(80);
                    flaxSpun += inventory.getCount(bowstring)
                            - stringsPreviouslyHeld;
                    stringsPreviouslyHeld = inventory.getCount(bowstring);
                }
                break;
            case BANK:
                while (inventory.getCount() > 0) {
                    bank.depositAll();
                    wait(random(290, 520));
                }
                while (inventory.getCount(flax) == 0) {
                    bank.atItem(flax, "Withdraw-All");
                    wait(random(800, 1200));
                    if (inventory.getCount(flax) == 0
                            && bank.getItemByID(flax) == null) {
                        while (bank.isOpen()) {
                            bank.close();
                            wait(random(200, 500));
                        }
                        log.info("No Flax Found");
                        stopScript();
                        break;
                    }
                }
                wait(random(150, 400));
                if (random(0, 3) == 0) {
                    bank.close();
                }
            case CLIMB:
                if (tile.onScreen(stairsTile)) {
                    if (!tile.click(stairsGroundTile, "Climb-up")
                            && !player.getMine().isMoving()) {
                        rotateCamera();
                    }
                    wait(random(400, 600));
                } else {
                    walk.to(tile.getClosestOnMap(Tile), 1, 1);
                    wait(random(1000, 2000));
                }
                break;
            default:
                break;
        }
        return random(400, 700);
    }

    private void moveMouseAway(final int moveDist) {
        final Point pos = mouse.getLocation();
        mouse.move(pos.x - moveDist, pos.y - moveDist, moveDist * 2,
                moveDist * 2);
    }

    @Override
    public void onFinish() {
    }

    public void onRepaint(final Graphics g) {
        final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
        int CurrentXP = skills.getCurrentXP(Constants.STAT_CRAFTING);
        int RealLvL = skills.getRealLvl(Constants.STAT_CRAFTING);
        int currentXP = skills.getCurrentXP(Constants.STAT_CRAFTING);
        int currentLVL = skills.getCurrentLvl(Constants.STAT_CRAFTING);
        int currentPurLVL = skills.getPercentToNextLvl(Constants.STAT_CRAFTING);
        int XPToNextLvL = skills.getXPToNextLvl(Constants.STAT_CRAFTING);
        final double XP = 15;//special skill define
        final int profit = flaxSpun * (stringPrice - flaxPrice);

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
            final int gpPerHour = (int) (profit * 3600000.0 / (double) runTime);
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
            g.drawString("spun: " + done, x, 410);
            g.drawString("PH: " + donePerHour, x1, 410);
            g.drawString("Lvl: " + currentLVL, x, 426);
            g.drawString("Gained: " + gainedLVL, x1, 426);
            g.drawString("Xp: " + gainedXP, x, 442);
            g.drawString("PH: " + expPerHour, x1, 442);
            g.drawString("Xp To Next Level: " + XPToNextLvL, x, 458);
            g.drawString("GP: " + profit, x, 474);
            g.drawString("PH: " + gpPerHour, x1, 474);

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

    @Override
    public boolean onStart(final Map<String, String> args) {
        try {
            final URL url = new URL("http://lazygamerz.org/client/skin/paint.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
            log("Failed to get url image for the progress paint.");
        }
        stringPrice = ge.loadItemInfo(bowstring).getPrice();
        log.info("bow strings valued at " + stringPrice + " gp.");
        flaxPrice = ge.loadItemInfo(flax).getPrice();
        log.info("flax valued at " + flaxPrice + " gp.");
        if (flaxPrice == 0 || stringPrice == 0) {
            log.info("Grand Exchange prices could not be loaded");
        }
        return true;
    }

    private boolean playerIsInArea(final int[] bounds) {
        final RSTile pos = player.getMine().getLocation();
        return pos.getX() >= bounds[0] && pos.getX() <= bounds[1]
                && pos.getY() >= bounds[2] && pos.getY() <= bounds[3];
    }

    private void rotateCamera() {
        int angle = camera.getAngle() + random(-40, 40);
        if (angle < 0) {
            angle += 359;
        }
        if (angle > 359) {
            angle -= 359;
        }

        camera.setRotation(angle);
    }

    private boolean walkToTile(final RSTile t) {
        if (tile.onScreen(t)) {
            return tile.click(t, "Walk");
        }
        return walk.to(t);
    }

    void moveMousesSlightly() {
        final int x = random(0, 300);
        final int y = random(0, 200);
        mouse.move(x, y, 10, 10);
        wait(random(300, 800));
        mouse.move(x, y, 5, 5);
        wait(random(400, 700));
        mouse.move(x, y, 2, 2);
    }
}
