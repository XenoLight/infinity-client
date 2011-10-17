/*FILE: BighoofAIOFurnaceBasic.java
 *Bighoof AIO Furnace Basic, by BobbyBighoof, Version 1.0 (01-29-2010)
 *
 * version 1.18 - (11-21-2010) fix for cannonball math for new interfaces in game.
 *
 * version1.17 - (10-20-2010) fix for all bars as the make bar math changed messing up
 * the amount set for the make X that was i nthe math. Should be working for all
 * bars now. Still no cannon ball math that will be coming soon.
 *
 * version 1.16 - (10-13-2010) Updated the script to comply with the 623 interface
 * changes for smithing. 1.17 will have to be for cannon balls I do not at this time
 * have an account ot check that math but will i nthe near future.
 *
 * version 1.15 - (07-02-2010) cleaned up code, fixed cannonball smelting, fixed
 * log out when out of items to use to work with on cannonballs and crafting, Cleaned
 * up the handle bank math to reflect new math set by zzSleepzz :) thanks zz.
 *
 * version 1.14 - (02-28-2010) added some wait time to both item withdraws. Changed
 * iron withdraw to be withdraw-10 instead of 9 to try and combat text spam in chat
 *
 *
 *Thanks to: zzSleepzz for helping push me in the right direction.
 *Thanks to: idontPLAYrs my always valued alpha and closed bata tester, also my
 *idea and chat about it guy :) glad to have you on my team dude.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"BobbyBighoof"},
category = "Smithing",
name = "Bighoof AIO FurnaceBasic",
email = "bobbybighoof@gmail.com",
version = 2.00,
description = "<style type='text/css'>"
+ "body {background:url('http://bighoofscripts.webs.com/images/back-1.jpg') repeat}"
+ "</style><html><head><center><img src=\"http://bighoofscripts.webs.com/images/aiof/hoof-aiofurn.jpg\">"
+ "</center></head>"
+ "<center><div style=width: 100%; padding: 0px; padding-bottom: 2px; background-color: #e4c674>"
+ "<table style=border-collapse:collapse cellpadding=0 cellspacing=0><tr>"
+ "</tr><br><tr>"
+ "<td class=style2 align=right bgcolor=#00FFFF><b>Item ,"
+ "</b></td><td class=style1 bgcolor=#00FFFF><b>Options"
+ "</b></td></tr><tr><td class=style2 bgcolor=#C0C0C0 color=#000000>"
+ "Use Gold Gauntlets:"
+ "</td><td class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<INPUT TYPE=checkbox name=goldGloves VALUE=true>Yes"
+ "</td></tr><tr><td class=style2 align=right bgcolor=#00FFFF>"
+ "<b>Make ,</b></td><td class=style1 bgcolor=#00FFFF><b>Options"
+ "</b></td></tr><tr><td class=style2 bgcolor=#C0C0C0 color=#000000>"
+ "Smithing:"
+ "</td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<select name=stype><option selected>None</option><option>Bronze Bar<option>Blurite Bar"
+ "<option>Iron Bar<option>Silver Bar<option>Steel Bar<option>Gold Bar<option>Mithril Bar"
+ "<option>Adamantite Bar<option>Runite Bar<option>Cannon Ball</select>"
+ "</td></tr><tr><td class=style2 align=right bgcolor=#00FFFF><b>Location ,"
+ "</b></td><td class=style1 bgcolor=#00FFFF><b>Options"
+ "</b></td></tr><tr><td class=style2 bgcolor=#C0C0C0 color=#000000>"
+ "Location:"
+ "</td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<select name=location><option>Falador<option selected>Al Kharid"
+ "<option>Edgeville<option>Neitiznot</select>"
+ "</tr></tr></table></center></body></html>")
public class BighoofAIOFurnaceBasic extends Script implements PaintListener,
        MessageListener {

    private final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
    private final RSTile[] FSmithPath = {new RSTile(2951, 3379),
        new RSTile(2961, 3380), new RSTile(2971, 3378), new RSTile(2973, 3373),
        new RSTile(2974, 3369)};
    private final RSTile[] FSmithing = walk.cleanPath(walk.fixPath(FSmithPath));
    private final RSTile[] FBankPath = {new RSTile(2965, 3377),
        new RSTile(2956, 3381), new RSTile(2951, 3380), new RSTile(2950, 3376),
        new RSTile(2945, 3371), new RSTile(2947, 3368)};
    private final RSTile[] FBanking = walk.cleanPath(walk.fixPath(FBankPath));
    RSTile[] FDoor = {new RSTile(0000, 0000)};
    private final RSTile[] ASmithPath = {new RSTile(3276, 3173),
        new RSTile(3276, 3184)};
    private final RSTile[] ASmithing = walk.cleanPath(walk.fixPath(ASmithPath));
    private final RSTile[] ABankPath = {new RSTile(3276, 3173),
        new RSTile(3269, 3169)};
    private final RSTile[] ABanking = walk.cleanPath(walk.fixPath(ABankPath));
    private final RSTile[] ESmithPath = {new RSTile(3098, 3496),
        new RSTile(3108, 3500)};
    private final RSTile[] ESmithing = walk.cleanPath(walk.fixPath(ESmithPath));
    private final RSTile[] EBankPath = {new RSTile(3108, 3500),
        new RSTile(3098, 3496)};
    private final RSTile[] EBanking = walk.cleanPath(walk.fixPath(EBankPath));
    private final RSTile[] NSmithPath = {new RSTile(2338, 3807),
        new RSTile(2343, 3810)};
    private final RSTile[] NSmithing = walk.cleanPath(walk.fixPath(NSmithPath));
    private final RSTile[] NBankPath = {new RSTile(2343, 3810),
        new RSTile(2338, 3807)};
    private final RSTile[] NBanking = walk.cleanPath(walk.fixPath(NBankPath));
    private RSTile[] Banking;
    private RSTile[] Smithing;
    private BufferedImage img = null;
    //SYSTEM INTS
    private int BankBooth;
    private int Furnace;
    private int Child;
    private int BarCount = 0;
    private int Ore1;
    private int Ore2;
    private int Ore1Amount;
    private int Ore2Amount;
    public int BarAmount;
    public int BarID;
    private int startXP;
    private int startLVL;
    private int Barxp;
    public int[] smithani = {3243, 899, 827};
    //SYSTEM DOUBLES
    private static final double B0 = 8;
    private static final double B1 = 6.2;
    private static final double B2 = 12.5;
    private static final double B3 = 13.7;
    private static final double B4 = 17.5;
    private static final double B5 = 22.5;
    private static final double B6 = 30;
    private static final double B7 = 37.5;
    private static final double B8 = 50;
    private static final double B51 = 56.2;
    private String BarName;
    private String ClickName;
    private String OreName1;
    private String status = ("Loading...");
    private long scriptStartTime;
    private boolean setAltitude = false;
    public boolean useAntiBan = true;

    public void messageReceived(final MessageEvent e) {
        final String message = e.getMessage();
        if (message.contains("to make that.")) {
            stopScript();
        }
        if (message.contains("The magic of the Varrock armour enables "
                + "you to smelt 2 bars at the same time")) {
            status = ("Smelted a " + BarName + " Bar...");
            BarCount++;
        }
        if (message.contains("You retrieve a bar")) {
            status = ("Smelted a " + BarName + " Bar...");
            BarCount++;
        }
        if (message.contains("You have run out of")) {
            status = ("Out Of Ore...");
        }
    }

    void run() {
        if (player.getMyEnergy() > 40 + random(1, 60) || player.getMyEnergy() > 71) {
            wait(random(200, 300));
            game.setRun(true);
            wait(random(300, 500));
        }
    }

    @Override
    public void onFinish() {
        logProgressReport();
        return;
    }

    public int loop() {
        if (!game.isLoggedIn()) {
            return random(800, 1200);
        }
        run();
        if (!setAltitude) {
            camera.setAltitude(true);
            wait(random(500, 800));
            setAltitude = true;
            return random(300, 600);
        }

        if (oreIDCheck()) {
            if (atBank()) {
                handleBank();
                handleWithdraw();
                return (1);
            } else {
                if (walkToBank());
                return (1);
            }
        } else {
            if (atFurnace()) {
                handleBars();
                return (1);
            } else {
                if (walkToFurnace());
                return (1);
            }
        }
    }

    boolean oreIDCheck() {//ORE CHECK
        status = ("Checking Inventory");
        inventory.open();
        return (!inventory.contains(Ore1) || (!inventory.contains(Ore2)));
    }

    boolean atBank() {
        status = ("Finding Bank");
        return (calculate.distanceTo(objects.getNearestByID(BankBooth)) < 8);
    }

    public boolean handleBank() {
        if (!bank.isOpen()) {
            if (bank.open());
                for (int i = 0; i < 100 && !bank.isOpen(); i++) {
                    sleep(20);
                }
        }
        return bank.isOpen();
    }

    void handleWithdraw() {
        if (bank.isOpen()) {
            wait(random(200, 500));
            if (bank.getCount(Ore1) > Ore1Amount) {
                if (inventory.getCount() > 1) {
                    bank.depositAll();
                    wait(random(800, 1000));
                }
                if (!inventory.contains(Ore1)) {
                    bank.withdraw(Ore1, Ore1Amount);
                    status = ("Withdrawing " + OreName1 + "...");
                    for (int i = 0; i < 100
                            && !inventory.contains(Ore1); i++) {
                        sleep(20);
                    }
                }

            } else {
                log("You have ran out of " + OreName1 + ". Stopping script...");
                bank.close();
                status = ("Logging Out...");
                log.warning("Logging out in 10 sec...");
                wait(random(10000, 12000));
                game.logout();
                stopScript();
            }
            if (Ore2Amount != 0) {
                wait(random(500, 800));
                if (bank.getCount(Ore2) > Ore2Amount) {
                    if (!inventory.contains(Ore2)) {
                        bank.withdraw(Ore2, -1);
                        for (int i = 0; i < 100
                                && !inventory.contains(Ore2); i++) {
                            sleep(20);
                        }
                    }
                } else {//OUT OF ORE LOG OUT
                    bank.close();
                    status = ("Logging Out...");
                    log("Logging out in 10 sec...");
                    wait(random(10000, 12000));
                    game.logout();
                    stopScript();
                }
            } else {
                status = ("Path Set To Furnace...");
            }
        }
    }

    boolean walkToBank() {// WALK PATH TO BANK
        if (calculate.distanceTo(walk.getDestination()) <= random(8, 12)) {
            status = ("Going To Bank...");
            walk.to(walk.nextTile(Banking, 12, false));
        }
        return true;
    }

    boolean atFurnace() {
        status = ("Finding Furnace");
        return (calculate.distanceTo(objects.getNearestByID(Furnace)) < 12);
    }

    public boolean isSmelting() {
        int i;
        int j;
        j = 0;
        for (i = 0; i < 10; i++) {
            if (player.getMine().getAnimation() == 3243
                    || player.getMine().getAnimation() == 899
                    || player.getMine().getAnimation() == 827) {
                j++;
            }
            wait(antiBan(random(150, 200)));
            inventory.open();
        }
        return j > 0;
    }

    void handleBars() {
        final RSObject furnace = objects.getNearestByID(Furnace);
        if (!isSmelting()) {
            status = ("Clicking Furnace...");
            if (!iface.get(916).isValid()) {
                if (Smithing == NSmithing) {
                    objects.at(furnace, "Smelt Clay forge");
                } else {
                    objects.at(furnace, "Smelt Furnace");
                }
                for (int i = 0; i < 100 && !iface.get(916).isValid(); i++) {
                    sleep(20);
                }
            } else if (iface.get(916).isValid()) {
                iface.clickChild(905, Child, "Make All");
            }
        }
    }

    boolean walkToFurnace() {// SETTING PATH TO FURNACE
        if (calculate.distanceTo(walk.getDestination()) <= random(8, 12)) {
            status = ("Going To Furnace...");
            walk.to(walk.nextTile(Smithing, 12, false));
        }
        return true;
    }

    void logProgressReport() {
        long millis = System.currentTimeMillis() - scriptStartTime;
        String time = Timer.format(millis);
        log("Thank you for using this script.");
        log("Please visit its page on the lazygamerz.org site");
        log(properties.name() + ", " + properties.version());
        log("Run Time: " + time);
        log(ClickName + " Bars Made: " + BarCount);
    }

    /**
     * Sets the {@link render}
     *
     * @param Repaint - a {@link render}
     */
    public void onRepaint(Graphics g) {
        //if logged in to the game start the paint
        if (game.isLoggedIn()) {
            int RealLvL = skills.getRealLvl(Skills.SMITHING);
            int currentXP = skills.getCurrentXP(Skills.SMITHING);
            int currentLVL = skills.getCurrentLvl(Skills.SMITHING);
            int currentPurLVL = skills.getPercentToNextLvl(Skills.SMITHING);
            int XPToNextLvL = skills.getXPToNextLvl(Skills.SMITHING);
            final double XP = Barxp;//special skill define

            //sets up the paint visual run timer for the user
            //it is set up in 00 set up rather than typical 0
            long millis = System.currentTimeMillis() - scriptStartTime;
            String time = Timer.format(millis);
            if (startXP == 0) {
                startXP = currentXP;
                startLVL = RealLvL;
            }
            //base number used for left alignment of text. I could just type 9 in all x spaces
            //but what fun would that be?
            final int x = 315;
            final int x1 = 400;

            int gainedXP = currentXP - startXP;
            final int done = (int) (gainedXP / XP);
            final int donePerHour = (int) ((gainedXP / XP) * 3600000.0 / (double) millis);
            final int expPerHour = (int) ((currentXP - startXP) * 3600000.0 / (double) millis);
            int gainedLVL = currentLVL - startLVL;
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
            g.drawString("Smithed: " + done, x, 410);
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

    @Override
    public boolean onStart(final Map<String, String> args) {
        try {
            final URL url = new URL("http://lazygamerz.org/client/skin/paint.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
        }

        if (args.get("location").equals("Falador")) {
            Smithing = FSmithing;
            Banking = FBanking;
            BankBooth = 11758;
            Furnace = 11666;
        } else if (args.get("location").equals("Al Kharid")) {
            Smithing = ASmithing;
            Banking = ABanking;
            BankBooth = 35647;
            Furnace = 11666;
        } else if (args.get("location").equals("Edgeville")) {
            Smithing = ESmithing;
            Banking = EBanking;
            BankBooth = 26972;
            Furnace = 26814;
        } else if (args.get("location").equals("Neitiznot")) {
            Smithing = NSmithing;
            Banking = NBanking;
            BankBooth = 21301;
            Furnace = 21303;
        }

        if (args.get("stype").equals("Bronze Bar")) {
            BarName = "Bronze Bar";
            ClickName = "Bronze";
            OreName1 = "Copper ore";
            BarID = 2349;
            Barxp = (int) (B1);
            Ore1 = 436;
            Ore2 = 438;
            Ore1Amount = 14;
            Ore2Amount = 14;
            Child = 14;
            BarAmount = 28;
        } else if (args.get("stype").equals("Blurite Bar")) {
            BarName = "Blurite Bar";
            ClickName = "Blurite";
            OreName1 = "blurite ore";
            BarID = 9467;
            Barxp = (int) (B0);
            Ore1 = 668;
            Ore2 = 668;
            Ore1Amount = 28;
            Ore2Amount = 0;
            Child = 15;
            BarAmount = 28;
        } else if (args.get("stype").equals("Iron Bar")) {
            BarName = "Iron Bar";
            ClickName = "Iron";
            OreName1 = "Iron ore";
            BarID = 2351;
            Barxp = (int) (B2);
            Ore1 = 440;
            Ore2 = 440;
            Ore1Amount = 28;
            Ore2Amount = 0;
            Child = 16;
            BarAmount = 28;
        } else if (args.get("stype").equals("Silver Bar")) {
            BarName = "Silver Bar";
            ClickName = "Silver";
            OreName1 = "Silver ore";
            BarID = 2355;
            Barxp = (int) (B3);
            Ore1 = 442;
            Ore2 = 442;
            Ore1Amount = 28;
            Ore2Amount = 0;
            Child = 17;
            BarAmount = 28;
        } else if (args.get("stype").equals("Steel Bar")) {
            BarName = "Steel Bar";
            ClickName = "Steel";
            OreName1 = "Iron ore";
            BarID = 2353;
            Barxp = (int) (B4);
            Ore1 = 440;
            Ore2 = 453;
            Ore1Amount = 9;
            Ore2Amount = 18;
            Child = 18;
            BarAmount = 9;
        } else if (args.get("stype").equals("Gold Bar")) {
            BarName = "Gold Bar";
            ClickName = "Gold";
            OreName1 = "Gold ore";
            BarID = 2357;
            Ore1 = 444;
            Ore2 = 444;
            Ore1Amount = 28;
            Ore2Amount = 0;
            Child = 19;
            BarAmount = 28;
            if (args.get("goldGloves") != null) {
                Barxp = (int) (B51);
            } else {
                Barxp = (int) (B5);
            }
        } else if (args.get("stype").equals("Mithril Bar")) {
            BarName = "Mithril Bar";
            ClickName = "Mithril";
            OreName1 = "Mithril ore";
            BarID = 2359;
            Barxp = (int) (B6);
            Ore1 = 447;
            Ore2 = 453;
            Ore1Amount = 5;
            Ore2Amount = 20;
            Child = 20;
            BarAmount = 5;
        } else if (args.get("stype").equals("Adamantite Bar")) {
            BarName = "Adamantite Bar";
            ClickName = "Adamantite";
            OreName1 = "Adamantite ore";
            BarID = 2361;
            Barxp = (int) (B7);
            Ore1 = 449;
            Ore2 = 453;
            Ore1Amount = 4;
            Ore2Amount = 24;
            Child = 21;
            BarAmount = 4;
        } else if (args.get("stype").equals("Runite Bar")) {
            BarName = "Runite Bar";
            ClickName = "Runite";
            OreName1 = "Runite ore";
            BarID = 2363;
            Barxp = (int) (B8);
            Ore1 = 451;
            Ore2 = 453;
            Ore1Amount = 3;
            Ore2Amount = 24;
            Child = 22;
            BarAmount = 3;
        }

        scriptStartTime = System.currentTimeMillis();
        return true;

    }

    public int antiBan(int retval) {
        int gamble = random(1, random(75, 100));
        int x = random(0, 750);
        int y = random(0, 500);
        int xx = random(554, 710);
        int yy = random(230, 444);
        int screenx = random(1, 510);
        int screeny = random(1, 450);
        if (!useAntiBan) {
            return retval;
        }
        switch (gamble) {
            case 1:
                return retval;
            case 2:
                mouse.move(x, y);
                return retval;
            case 3:
                game.openTab(Game.tabInventory);
                return retval;
            case 4:
                if (player.getMine().isMoving()) {
                    return retval;
                }
            case 5:
                game.openTab(Game.tabStats);
                mouse.move(random(663, 711), random(325, 348));
                return retval;
            case 6:
                    game.openTab(Game.tabStats);
                    mouse.move(xx, yy);
                    return retval;

            case 7:
                if (random(1, 8) == 2) {
                    int angle = camera.getAngle() + random(-90, 90);
                    if (angle < 0) {
                        angle = 0;
                    }
                    if (angle > 359) {
                        angle = 0;
                    }

                    camera.setRotation(angle);
                }
                return retval;
            case 8:
                mouse.move(screenx, screeny);
                return retval;
            case 9:
                mouse.move(screenx, screeny);
                return retval;
            case 10:
                randomTab();
                wait(random(0, 250));
                return retval;
            case 11:
                wait(random(0, 250));
                mouse.move(screenx, screeny);
                return retval;
            case 12:
                wait(random(0, 250));
                mouse.move(screenx, screeny);
                return retval;
            case 13:
                wait(random(0, 250));
                mouse.move(screenx, screeny);
                return retval;
            case 14:
                //log("moving mouse off screen...");
                wait(random(100, 1000));
                mouse.move(random(-800, 800), random(-800, 800));
                return retval;
            case 15:
                //log("moving mouse off screen...");
                wait(random(100, 1000));
                mouse.move(random(-200, 800), random(-100, 800));
                return retval;

        }
        return retval;
    }

    public int randomTab() {
        int random1 = random(1, random(23, 28));
        switch (random1) {
            case 1:
                game.openTab(Game.tabStats);
                return random(100, 500);
            case 2:
                game.openTab(Game.tabAttack);
                return random(100, 500);
            case 3:
                game.openTab(Game.tabQuests);
                return random(100, 500);
            case 4:
                game.openTab(Game.tabEquipment);
                return random(100, 500);
            case 5:
                game.openTab(Game.tabInventory);
                return random(100, 500);
            case 6:
                game.openTab(Game.tabPrayer);
                return random(100, 500);
            case 7:
                game.openTab(Game.tabMagic);
                return random(100, 500);
            case 8:
                game.openTab(Game.tabSumoming);
                return random(100, 500);
            case 9:
                game.openTab(Game.tabFriends);
                return random(100, 500);
            case 10:
            case 11:
                game.openTab(Game.tabClan);
                return random(100, 500);
            case 12:
            case 13:
                game.openTab(Game.tabMusic);
                return random(100, 500);
            case 14:
            case 15:
                game.openTab(Game.tabStats);
                return random(100, 500);
            case 16:
                game.openTab(Game.tabStats);
                return random(100, 500);
            case 17:           
            case 18:
            case 19:
                game.openTab(Game.tabInventory);
                return random(100, 500);
        }
        return random(100, 300);
    }
}
