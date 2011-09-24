
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

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Infinity dev team"},
category = "Thieving",
name = "S-Tea-ler",
version = 1.0,
description = "<html><style type='text/css'>"/*start html option code*/
+ "body {background:url('http://lazygamerz.org/client/images/back_1.png') repeat}"/*background image*/
+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"/*header image*/
+ "</head><br><body>"
/*start box*/
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
+ "<td width=90% align=justify>"
+ "<center><font color=#000000>Infinity S-Tea-ler</font></center><br />"/*script title*/
+ "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"/*top box label*/
/*box statment*/
+ "<font size=3>Start at the Tea stall in Varrock East"
+ "You can find the stall South East of the East Varrock Bank"
+ "Be sure to have nothing in your invitory as this drops everything"
+ "Except for the strange rocks. It will keep those for you...<br>"
+ "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"/*ending tag label*/
+ "</td></tr></table><br />"/*end box*/)
public class S_Tea_ler extends Script implements MessageListener, PaintListener {

    final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
    private int startXP = 0;
    private int startLvl = 0;
    private int teaStolen = 0;
    private int[] rocksArray = new int[]{15527, 15526};
    private BufferedImage img = null;
    private long startTime = 0;
    private boolean setAltitude = false;
    final RSTile stallTile = new RSTile(3268, 3410);
    final RSTile stallPoint = new RSTile(3269, 3410);
    final Point p = Calculations.tileToScreen(stallPoint);

    //moves the mouse around random
    private void moveMouses() {
        final int x = random(0, 750);
        final int y = random(0, 500);
        mouse.move(x, y, 10, 10);
        wait(random(400, 700));
    }

    //moves the mouse around random slightly
    private void moveMousesSlightly() {
        final int x = random(0, 300);
        final int y = random(0, 200);
        mouse.move(x, y, 10, 10);
        wait(random(700, 1000));
        mouse.move(x, y, 5, 5);
        wait(random(700, 1000));
        mouse.move(x, y, 2, 2);
    }

    //moves the camera around 0 to 360
    private void moveCamera() {
        int angle = camera.getAngle() + random(-90, 90);
        if (angle < 0) {
            angle = 0;
        }
        if (angle > 359) {
            angle = 0;
        }
        camera.setRotation(angle);
    }

    //moves the camera around 0 to 180
    private void moveCameraSlightly() {
        int angle = camera.getAngle() + random(-45, 45);
        if (angle < 0) {
            angle = 0;
        }
        if (angle > 359) {
            angle = 0;
        }
        camera.setRotation(angle);
    }

    //opens a random tab
    private void openRandomTab() {
        switch (random(0, 12)) {
            case 1:
                game.openTab(TAB_STATS);
                break;
            case 2:
                game.openTab(TAB_ATTACK);
                break;
            case 3:
                game.openTab(TAB_EQUIPMENT);
                break;
            case 4:
                game.openTab(TAB_FRIENDS);
                break;
            case 5:
                game.openTab(TAB_MAGIC);
                break;
            case 6:
                game.openTab(TAB_NOTES);
        }
    }

    private void antiBan() {
        switch (random(0, 14)) {
            case 1:
                moveCameraSlightly();
                break;
            case 2:
                moveCamera();
                break;
            case 3:
                moveMouseRandomly(300);
                break;
            case 4:
                moveMouses();
                break;
            case 5:
                moveMousesSlightly();
                break;
            case 6:
                openRandomTab();
                break;
        }
    }

    /**
     * Sets the start up arguments
     * @param args
     * @return
     */
    public boolean onStart(Map<String, String> args) {
        try {
            final URL url = new URL("http://lazygamerz.org/client/skin/theft.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
            log("Failed to get the theme for the paint.");
            e.printStackTrace();
        }
        startTime = System.currentTimeMillis();
        return true;
    }

    /**
     * Sets the continued script running math
     * @return
     */
    public int loop() {
        if (!setAltitude) {
           camera.setAltitude(true);
            wait(random(250, 500));
            setAltitude = true;
            return (50);
        }
        if (!player.getMyLocation().equals(stallTile)) {
            tile.click(stallTile, "Walk");
            if (player.waitToMove(1000)) {
                while (player.getMine().isMoving()) {
                    wait(15);
                }
            }
            return (50);
        }
        RSObject teaStall = objects.getNearestByID(635);
        if (teaStall != null) {
            RSTile stallTile = teaStall.getLocation();
            if (tile.click(stallTile, "Steal")) {
                wait(random(1500, 2000));
                if (inventory.contains(1978)) {
                    wait(random(1500, 2000));
                    inventory.dropAllExcept(rocksArray);
                }
                antiBan();
                mouse.move(p);
                return (50);
            }
        }
        return (50);
    }

    /**
     * Implements the message listener
     *
     * @param messageReceived - a {@link m}
     */
    public void messageReceived(final MessageEvent m) {
        if (m.getMessage().contains("You've just")) {
        }
        if (m.getMessage().contains("You steal")) {
            teaStolen++;
        }
    }

    /**
     * Sets the {@link g}
     * @param g - a {@link Graphics}
     */
    public void onRepaint(Graphics g) {
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
                startXP = skills.getCurrentXP(Constants.STAT_THIEVING);
                startLvl = skills.getRealLvl(Constants.STAT_THIEVING);
            }
            //base number used for left alignment of text. I could jsut type 9 in all x spaces
            //but what fun would that be?
            final int x = 315;
            final int x1 = 400;
            int currentXP = skills.getCurrentXP(Constants.STAT_THIEVING);
            int gainedXP = currentXP - startXP;
            final int TeaPerHour = (int) ((gainedXP / 16) * 3600000.0 / (double) runTime);
            final int expPerHour = (int) ((currentXP - startXP) * 3600000.0 / (double) runTime);
            int currentLVL = skills.getCurrentLvl(Constants.STAT_THIEVING);
            int gainedLVL = currentLVL - startLvl;
            final int currentPurLVL = skills.getPercentToNextLvl(Constants.STAT_THIEVING);
            final int fillBar = (int) (2 * (double) currentPurLVL);

            g.setFont(new Font("Palatino Linotype", Font.PLAIN, 13));
            g.setColor(Color.RED);//color changes for % box
            g.fill3DRect(318, 342, 162, 14, true);//fill % box
            g.setColor(Color.GREEN);//% fill bar color
            g.fill3DRect(318, 342, fillBar, 14, true);

            g.drawImage(img, 250, 318, null);
            g.setFont(new Font("arial", Font.PLAIN, 10));
            g.setColor(new Color(225, 225, 225, 255));
            g.drawString("Infinity " + properties.name(), x, 378);
            g.drawString("Run Time: " + t1, x, 394);
            g.drawString("Tea Stolen: " + teaStolen, x, 410);
            g.drawString("PH: " + TeaPerHour, x1, 410);
            g.drawString("Lvl: " + currentLVL, x, 426);
            g.drawString("Gained: " + gainedLVL, x1, 426);
            g.drawString("Xp: " + gainedXP, x, 442);
            g.drawString("PH: " + expPerHour, x1, 442);
            g.drawString("Xp To Next Level: " + skills.getXPToNextLvl(Constants.STAT_THIEVING), x, 458);

            g.drawString("" + properties.version(), 494, 474);
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
