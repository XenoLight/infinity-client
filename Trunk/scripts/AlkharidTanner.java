
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
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Infinity Dev Team"},
category = "Money",
name = "Al-Kharid Tanner",
version = 1.0,
website = "http://www.lazygamerz.org/forums/index.php?topic=3176.0",
notes = "<html>Date 04-28-2011:"
+ "Version 1.0:"
+ "set base math from older tanning script. Input template"
+ "dev team progress paint and option html.",
description = "<style type='text/css'>"
+ "body {background:url('http://lazygamerz.org/client/images/back_1.png') repeat}"
+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
+ "<h1><center><font color=#FFFFFF>"
+ "Al-kharid Tanner"
+ "</center></font color></h1>"
+ "</head><br><body>"
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
+ "<td width=90%>"
+ "<center>This runs only in Al-Kalrid. Start At the bank.<br />"
+ "<font color=#0000FF size=2>Pick options below</font></center></font size>"
+ "</td></tr></table>"
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=000000>"
+ "<td width=90%>"
+ "<center><font color=#FFFFFF>Select the type of leather to tan:<br /><br />"
+ "<select name=\"WHICH\">"
+ "<option>Soft</option>"
+ "<option>Hard</option>"
+ "<option>Green</option>"
+ "<option>Blue</option>"
+ "<option>Red</option>"
+ "<option>Black</option></select><br /><br />"
+ "Once you have chosen the food to cook; "
+ "Click the 'OK' button to begin the script"
+ "</td></tr></table></html>")
public class AlkharidTanner extends Script implements PaintListener {
//Hides

    private int hide;
    private int product;
    private int cowhide = 1739;
    private int sleather = 1741;
    private int hleather = 1743;
    private int ugreen = 1753;
    private int tgreen = 1745;
    private int ublue = 1751;
    private int tblue = 2505;
    private int ured = 1749;
    private int tred = 2507;
    private int ublack = 1747;
    private int tblack = 2509;
    private int tanner = 2824;
    private int tanscreen = 324;
    private int bankbooth = 35647;
    private int coins = 995;
    private int action;
    RSTile[] BanktoTanner = {new RSTile(3270, 3167), new RSTile(3276, 3174), new RSTile(3282, 3181),
        new RSTile(3273, 3191)};
    RSTile[] TannertoBank = walk.reversePath(BanktoTanner);
    RSTile bankboothspot = new RSTile(3270, 3167);
    RSTile tannerspot = new RSTile(3276, 3191);
    private int RanTimes;
    private int HideCount;
    public String status = "";
    long startTime = System.currentTimeMillis();
    BufferedImage img = null;
    public boolean doingtrade = false;

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
        }
        if (args.get("WHICH").equals("Soft")) {
            hide = cowhide;
            product = sleather;
        }
        if (args.get("WHICH").equals("Hard")) {
            hide = cowhide;
            product = hleather;
        }
        if (args.get("WHICH").equals("Green")) {
            hide = ugreen;
            product = tgreen;
        }
        if (args.get("WHICH").equals("Blue")) {
            hide = ublue;
            product = tblue;
        }
        if (args.get("WHICH").equals("Red")) {
            hide = ured;
            product = tred;
        }
        if (args.get("WHICH").equals("Black")) {
            hide = ublack;
            product = tblack;
        }
        return true;
    }

    private void CheckMoney() {
        if (game.isLoggedIn() && !iface.get(378).isValid()) {
            if (!inventory.contains(coins)) {
                log("Out of GP!");
                stopScript();
            }
        }
    }

    private boolean isinlocation(int smallestx, int smallesty, int biggestx, int biggesty) {
        int x = player.getMine().getLocation().getX();
        int y = player.getMine().getLocation().getY();
        if (smallestx <= x && x <= biggestx && smallesty <= y && y <= biggesty) {
            return true;
        } else {
            return false;
        }
    }

    private void DetermineAction() {
        if (isinlocation(3269, 3161, 3275, 3171)) {
            action = 1;
        } else if (isinlocation(3271, 3189, 3280, 3196) && !iface.get(tanscreen).getChild(0).isValid()) {
            action = 2;
        } else if (iface.get(tanscreen).getChild(0).isValid()) {
            action = 3;
        } else {
            action = 4;
        }
    }

    public boolean openBank() {
        int ct = 0;
        try {
            if (!bank.isOpen()) {
                if (bank.open()) {
                    wait(random(300, 500));
                }
                while (!bank.isOpen()) {
                    wait(180);
                    ct++;
                    if (ct > 30) {
                        return false;
                    }
                }
            }
        } catch (final Exception e) {
        }
        return true;
    }

    public void useBank() {
        if (bank.isOpen()) {
            if (inventory.contains(product)) {
                RanTimes++;
            }
            HideCount = HideCount + inventory.getCount(product);
            status = "Depositing..";
            if (bank.depositAllExcept(coins));
            wait(random(1000, 2000));
            if (bank.getCount(hide) > 0) {
                if (inventory.getCount(hide) == 0) {
                    status = "Withdrawing..";
                    if (bank.atItem(hide, "Withdraw-all")) {
                    }
                }
            } else {
                bank.close();
                status = "Out of hides..";
                wait(random(4000, 8000));
                game.logout();
                stopScript();
            }
        }
    }

    private void Tan() {
        RSNPC tanzor = npc.getNearestByID(tanner);
        if (inventory.contains(hide)) {
            if (nearTanner()) {
                if (doingtrade == true) {
                    if (product == sleather) {
                        mouse.click(random(50, 137), random(70, 170), false);
                    }
                    if (product == hleather) {
                    	mouse.click(random(160, 241), random(70, 170), false);
                    }
                    if (product == tgreen) {
                    	mouse.click(random(53, 135), random(200, 300), false);
                    }
                    if (product == tblue) {
                    	mouse.click(random(160, 250), random(200, 300), false);
                    }
                    if (product == tred) {
                    	mouse.click(random(280, 360), random(200, 300), false);
                    }
                    if (product == tblack) {
                    	mouse.click(random(390, 470), random(200, 300), false);

                    }
                    wait(random(405, 650));
                    menu.action("Tan All");
                    doingtrade = false;
                }
            } else {
                walkPath(BanktoTanner);
                npc.click(tanzor, "Trade");
                doingtrade = true;
                wait(random(405, 650));
            }
        } else {
            walkPath(TannertoBank);
        }
    }

    private void walkPath(final RSTile[] path) {
        if (game.isLoggedIn()) {
            if (player.getMyEnergy() > 40 + random(1, 60)
                    || player.getMyEnergy() > 71 && !isRunning()) {
                wait(random(500, 900));
                game.setRun(true);
                wait(random(300, 500));
            }
        }
        if (calculate.distanceTo(walk.getDestination()) <= random(2, 3) || !player.getMine().isMoving()) {
            walk.pathMM(walk.randomizePath(path, 1, 1), 10);
        }
    }

    public boolean nearBank() {
        final RSObject booth = objects.getNearestByID(bankbooth);
        return !(booth == null || booth.distanceTo() >= 5);
    }

    public boolean nearTanner() {
        final RSNPC taner = npc.getNearestByID(tanner);
        return !(taner == null || taner.distanceTo() >= 5);
    }

    public int loop() {
        CheckMoney();
        DetermineAction();
        switch (action) {
            case 1: // bank
                if (!inventory.contains(hide)) {
                    if (nearBank()) {
                        openBank();
                        useBank();
                    } else {
                        walk.tileMM(bankboothspot);
                    }
                } else {
                    walkPath(BanktoTanner);
                }
                break;
            case 2: //tanner
                if (inventory.contains(hide)) {
                    RSNPC tanzor = npc.getNearestByID(tanner);
                    if (nearTanner()) {
                        if (inventory.contains(hide)) {
                            status = "Trading Tanner..";
                        }
                        npc.click(tanzor, "Trade");
                        doingtrade = true;
                        wait(random(405, 650));
                    } else {
                        walk.tileMM(tannerspot);
                    }
                } else {
                    walkPath(TannertoBank);
                }
                break;
            case 3: //tan interface
                status = "Tanning..";
                Tan();
                break;
            case 4: // in between (while walking)
                if (inventory.contains(hide)) {
                    walkPath(BanktoTanner);
                } else {
                    walkPath(TannertoBank);
                }
                break;
        }

        return (50);
    }

    public void onRepaint(Graphics g) {
        final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
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
            //base number used for left alignment of text. I could just type 9 in all x spaces
            //but what fun would that be?
            final int x = 315;
            final int x1 = 400;
            final int RanPerHour = (int) ((RanTimes) * 3600000.0 / (double) runTime);
            final int HidesperHour = (int) ((HideCount) * 3600000.0 / (double) runTime);
            //This is the box that will draw to insure that if the image does not load
            //that there will still be a black box behind the words to look decent for the user
            g.setColor(Color.BLACK);
            g.fill3DRect(310, 342, 210, 135, true);
            g.drawImage(img, 250, 318, null);
            g.setFont(new Font("arial", Font.PLAIN, 10));
            g.setColor(new Color(225, 225, 225, 255));
            g.drawString("Infinity " + properties.name(), x, 378);
            g.drawString("Run Time: " + t1, x, 394);
            g.drawString("Tanned: " + HideCount, x, 410);
            g.drawString("PH: " + HidesperHour, x1, 410);
            g.drawString("Ran: " + RanTimes, x, 426);
            g.drawString("PH: " + RanPerHour, x1, 426);
            g.drawString("Status: " + status, x, 474);

            //version define
            g.drawString("" + properties.version(), 494, 474);

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
