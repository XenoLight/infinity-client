
/**
 * Save As SecretAIOFletcher.java
 *
 * @author Secret Spy [ secretspy@runedev.info ]
 *
 * 
 * 
 * 
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

import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = {"Secret Spy"}, name = "SecretAIOBoner", category = "Prayer", version = 1.0, description = "<html>\n"
+ "<head></head><body>"
+ "<center><a target='_blank' href='http://img405.imageshack.us/i/secretaiobonerimagev.png/'><img src='http://img405.imageshack.us/img405/3392/secretaiobonerimagev.png' border='0'/></a></center>"
+ "</h2>\n"
+ "<b><center>Select Your Bone:</center><center><select name=\"bonename\">"
+ "<option>Select"
+ "<option>Baby Dragon"
+ "<option>Bat"
+ "<option>Big"
+ "<option>Normal"
+ "<option>Burnt"
+ "<option>Fayrg"
+ "<option>Jogre"
+ "<option>Monkey"
+ "<option>Ancient Ourg"
+ "<option>Ourg"
+ "<option>Raurg"
+ "<option>Shaikahan"
+ "<option>Wolf"
+ "<option>Wyvern"
+ "<option>Zogre"
+ "<option>Dagannoth"
+ "<option>Dragon"
+ "<option>Frost Dragon"
+ "</select></center>"
+ "<b><center>Select Your Mouse Speed:</center><center><select name=\"Speed\">"
+ "<option>Select"
+ "<option>Fast"
+ "<option>Human"
+ "<option>Normal"
+ "<option>Slow"
+ "<option>Slowest"
+ "<option>Warp Speed"
+ "</select></center><br>")
public class SecretAIOBoner extends Script implements PaintListener, MessageListener {

	private Bot bot;
    final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
    long runTime = 0;
    long seconds = 0;
    long minutes = 0;
    long hours = 0;
    public String status = "";
    private BufferedImage image = null;
    public int startExp, Prayerstartexp, startLevel, BoneID, BonesAdded = 0, mspeed, profit = 0, cost = 0, tries = 0, profitHour = 0, AmountPerHour;
    public long startTime = System.currentTimeMillis();
    public long minutesToLevel = 0, hoursToLevel = 0, secToLevel = 0, expToLevel = 0;
    public float gainedExp = 0, secExp = 0, minuteExp = 0, hourExp = 0;
    public boolean showInventory = false;
    public boolean startScript = false;
    private final ScriptManifest scriptInfo = getClass().getAnnotation(ScriptManifest.class);

    @Override
    public boolean onStart(Map<String, String> args) {
        try {
            final URL url = new URL("http://img46.imageshack.us/img46/8939/secretaioboner.png");
            image = ImageIO.read(url);
        } catch (final IOException e) {
            log("Failed to get the Picture for the paint.");
            e.printStackTrace();
        }
        status = "Loading Statistics...";
        log("Loading Statistics...");
        startTime = System.currentTimeMillis();
        startScript = true;

        args.get("bonename");

        if (args.get("bonename").equals("Baby Dragon")) {
            log("Were Burying Baby Dragon Bones");
            BoneID = 534;
        } else if (args.get("bonename").equals("Bat")) {
            log("Were Burying Bat Bones");
            BoneID = 530;
        } else if (args.get("bonename").equals("Big")) {
            log("Were Burying Big Bones");
            BoneID = 532;
        } else if (args.get("bonename").equals("Normal")) {
            log("Were Burying Normal Bones");
            BoneID = 526;
        } else if (args.get("bonename").equals("Burnt")) {
            log("Were Burying Burnt Bones");
            BoneID = 528;
        } else if (args.get("bonename").equals("Fayrg")) {
            log("Were Burying Fayrg Bones");
            BoneID = 4830;
        } else if (args.get("bonename").equals("Jogre")) {
            log("Were Burying Jogre Bones");
            BoneID = 3125;
        } else if (args.get("bonename").equals("Monkey")) {
            log("Were Burying Monkey Bones");
            BoneID = 3183;
        } else if (args.get("bonename").equals("Ancient Ourg")) {
            log("Were Burying Ancient Ourg Bones");
            BoneID = 4834;
        } else if (args.get("bonename").equals("Ourg")) {
            log("Were Burying Ourg Bones");
            BoneID = 14793;
        } else if (args.get("bonename").equals("Raurg")) {
            log("Were Burying Raurg Bones");
            BoneID = 4832;
        } else if (args.get("bonename").equals("Shaikahan")) {
            log("Were Burying Shaikahan Bones");
            BoneID = 3123;
        } else if (args.get("bonename").equals("Wolf")) {
            log("Were Burying Wolf Bones");
            BoneID = 2859;
        } else if (args.get("bonename").equals("Wyvern")) {
            log("Were Burying Wyvern Bones");
            BoneID = 6812;
        } else if (args.get("bonename").equals("Zogre")) {
            log("Were Burying Zogre Bones");
            BoneID = 4812;
        } else if (args.get("bonename").equals("Dagannoth")) {
            log("Were Burying Dagannoth Bones");
            BoneID = 6729;
        } else if (args.get("bonename").equals("Dragon")) {
            log("Were Burying Dragon Bones");
            BoneID = 536;
        } else if (args.get("bonename").equals("Frost Dragon")) {
            log("Were Burying Frost Dragon Bones");
            BoneID = 18832;
        }

        if (args.get("Speed").equals("Fast")) {
            log("Mouse Speed Set To Fast...");
            mspeed = (4);
        } else if (args.get("Speed").equals("Human")) {
            log("Mouse Speed Set To Human...");
            mspeed = (6);
        } else if (args.get("Speed").equals("Normal")) {
            log("Mouse Speed Set To Normal...");
            mspeed = (8);
        } else if (args.get("Speed").equals("Slow")) {
            log("Mouse Speed Set To Slow...");
            mspeed = (10);
        } else if (args.get("Speed").equals("Slowest")) {
            log("Mouse Speed Set To Slowest...");
            mspeed = (12);
        } else if (args.get("Speed").equals("Warp Speed")) {
            log("Mouse Speed Set To Warp Speed...");
            mspeed = (-99);
        }
        return true;
    }

    @Override
    public void onFinish() {
        ScreenshotUtil.takeScreenshot(bot, true);
    }

    @Override
    protected int getMouseSpeed() {
        return (mspeed);
    }

    protected int setMouseSpeed() {
        return (7);
    }

    private boolean ModDetector() {
        //<img=0> = Player Mod
        //<img=1> = Unknown (Jagex Mod)???
        //<img=2> = Unknown (Dual Moderator)???
        //<img=3> = Chat Bubble.
        if (iface.get(137).isValid()) { //// If Chat Log is Valid
            if (iface.get(137).containsText("<img=0>")) { ///// And if there is a Player Mod Switch Worlds.
                status = "Player Mod!!";
                log("Player Mod Found Switching Worlds");
                worldSwitch();
            }
            if (iface.get(137).containsText("<img=1>")) { ///// And if there is a Jagex Mod Switch Worlds.
                status = "Jagex Mod!!";
                log("Jagex Mod Found Switching Worlds!!");
                worldSwitch();
            }
            if (iface.get(137).containsText("<img=2>")) { ///// And if there is a Developer Switch Worlds.
                status = "Developer!!";
                log("Developer Found Switching Worlds!!");
                worldSwitch();
            }
        }
        return true;
    }

    public boolean worldSwitch() {
        status = "Switching Worlds...";
        log("Switching Worlds...");
        setMouseSpeed();
        if (game.isLoggedIn()) {
            game.logout();
            while (game.isLoggedIn() || !iface.get(906).isValid()) {
                wait(random(750, 1250));
            }
        }
        iface.getChild(906, 222).click(); //// Clicks World Select

        wait(random(1500, 2500)); //// Waits for Server To Catch Up

        final int x = (658); ////(x,y) = Gets Top Ping
        final int y = (127);

        final int x1 = (365);////(x1,y1) = Top Location
        final int y1 = (127);

        final int x2 = (557);////(x2,y2) = Gets Top Type
        final int y2 = (127);

        final int x3 = (610);////(x3,y3) = Gets Top LootShare
        final int y3 = (127);

        mouse.move(x, y);
        wait(random(250, 500));
        mouse.click(true);
        wait(random(250, 500));

        mouse.move(x1, y1);
        wait(random(250, 500));
        mouse.click(true);
        wait(random(250, 500));

        mouse.move(x2, y2);
        wait(random(250, 500));
        mouse.click(true);
        wait(random(250, 500));

        mouse.move(x3, y3);
        wait(random(250, 500));
        mouse.click(true);
        wait(random(250, 500));

        mouse.move(random(155, 520), random(145, 425)); //// Moves Mouse Randomly Within the World(s)

        wait(random(250, 500));
        mouse.click(true);
        wait(random(250, 500));
        mouse.click(true);
        wait(random(250, 500));

        iface.getChild(906,170).click(); //// Clicks To Join World

        if (iface.get(906).getChild(66).isValid()) { //// If you are logging in to the World it will Wait
            wait(random(37500, 37500));
        }
        if (iface.get(909).getChild(51).isValid()) { //// If you are logging in to a F2P World it will Reloop Through the Method
            iface.getChild(906,73).click();
        }

        return true;
    }

    private void Bury() {
        status = "Burying..";
        getMouseSpeed();
        if (bank.isOpen()) {
            bank.close();
        }

        inventory.clickItem(BoneID, "");
    }

    public void logOut() {
        status = "Logging Out";
        setMouseSpeed();
        ScreenshotUtil.takeScreenshot(bot, true);
        mouse.move(754, 10, 10, 10);
        mouse.click(true);
        mouse.move(626, 411, 20, 15);
        mouse.click(true);
        wait(random(500, 1500));
        stopScript();
    }

    public boolean needToBank() {
        return (!inventory.contains(BoneID));
    }

    public boolean openBank() {
        status = "Opening Bank...";
        int ct = 0;
        try {
            if (!bank.isOpen()) {
                if (bank.open()) {
                }
                wait(random(500, 1000));
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
        status = "Banking...";
        setMouseSpeed();
        if (bank.isOpen()) {
            if (bank.depositAll());
            wait(random(250, 500));
            if (bank.getCount(BoneID) > 0) {
                if (inventory.getCount(BoneID) == 0) {
                    if (bank.atItem(BoneID, "Withdraw-All")) {
                        bank.close();
                    }
                }
            } else {
                log("Stopping script.");
                stopScript();
            }
        }
    }

    @Override
    public int loop() {
        ModDetector();
        camera.setAltitude(true);
        if (inventory.contains(BoneID)) {
            Bury();
        }
        if (needToBank()) {
            if (openBank()) {
                useBank();
                return 0;
            }
        }
        return 0;
    }

    public void onRepaint(Graphics g) {
        if (game.isLoggedIn()) {
            long millis = System.currentTimeMillis() - startTime;
            long hour = millis / (1000 * 60 * 60);
            millis -= hour * (1000 * 60 * 60);
            long minute = millis / (1000 * 60);
            millis -= minute * (1000 * 60);
            long second = millis / 1000;

            final Color BG = new Color(0, 0, 0, 75);
            final Color RED = new Color(255, 0, 0, 255);
            final Color GREEN = new Color(0, 255, 0, 255);
            final Color BLACK = new Color(0, 0, 0, 255);

            try {
                RSNPC Banker = npc.getNearestByName("Banker");
                Point Xx1 = Banker.getLocation().getScreenLocation();
                if (Banker != null && calculate.pointOnScreen(Xx1)) {
                    g.setColor(new Color(255, 255, 255, 0));
                    g.fillRect(Xx1.x - 0, Xx1.y - 0, 5, 5);
                    g.setColor(Color.green);
                    g.drawRect(Xx1.x - 0, Xx1.y - 0, 5, 5);
                }
                //////////////////////////////
                RSNPC Banker2 = npc.getNearestByName("Banker");
                if (Banker != null && Banker2.isOnScreen()) {
                    g.setFont(new Font("Tahoma", Font.PLAIN, 11));
                    g.setColor(Color.green);
                    g.fillOval(Banker2.getMapLocation().x - 0, Banker2.getMapLocation().y - 0, 5, 5);
                    g.drawString("Banker", Banker2.getMapLocation().x + 7, Banker2.getMapLocation().y + 7);
                }
                /////////////////////////////////
                org.rsbot.script.wrappers.RSPlayer Me = player.getMine();
                Point Xx2 = Me.getLocation().getScreenLocation();
                if (calculate.pointOnScreen(Xx2)) {
                    g.setColor(new Color(250, 250, 250, 0));
                    g.fillRect(Xx2.x - 0, Xx2.y - 0, 5, 5);
                    g.setColor(Color.red);
                    g.drawRect(Xx2.x - 0, Xx2.y - 0, 5, 5);
                }
                ///////////////////////////////
                org.rsbot.script.wrappers.RSPlayer Me2 = player.getMine();
                if (Me2.isOnScreen()) {
                    g.setFont(new Font("Tahoma", Font.PLAIN, 11));
                    g.setColor(Color.red);
                    g.fillOval(Me2.getMapLocation().x - 0, Me2.getMapLocation().y - 0, 5, 5);
                    g.drawString("You", Me2.getMapLocation().x + 7, Me2.getMapLocation().y + 7);
                }
            } catch (NullPointerException e5) {
            }

            if (Prayerstartexp == 0) {
                Prayerstartexp = skills.getCurrentXP(STAT_PRAYER);
            }

            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }

            if (startExp == 0) {
                startExp = skills.getCurrentXP(Constants.STAT_PRAYER);
            }

            if (startLevel == 0) {
                startLevel = skills.getCurrentLvl(Constants.STAT_PRAYER);
            }

            runTime = System.currentTimeMillis() - startTime;
            second = runTime / 1000;
            if (second >= 60) {
                minute = second / 60;
                second -= minute * 60;
            }
            if (minute >= 60) {
                hour = minute / 60;
                minute -= hour * 60;
            }

            gainedExp = skills.getCurrentXP(Constants.STAT_PRAYER) - startExp;
            expToLevel = skills.getXPToNextLvl(Constants.STAT_PRAYER);

            if ((minute > 0 || hour > 0 || second > 0) && gainedExp > 0) {
                secExp = (float) gainedExp
                        / (float) (second + minute * 60 + hour * 60 * 60);
            }
            minuteExp = secExp * 60;
            hourExp = minuteExp * 60;

            if (secExp > 0) {
                secToLevel = (int) (expToLevel / secExp);
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

            AmountPerHour = (int) ((3600000.0 / (double) runTime) * BonesAdded);

            g.setFont(new Font("Agency FB", 0, 10));
            g.setColor(BLACK);
            g.drawString("v" + scriptInfo.version(), 495, 335);
            g.setFont(new Font("Tahoma", Font.PLAIN, 10));
            g.setColor(BG);
            g.fill3DRect(4, 187, 124, 152, true);
            g.setColor(Color.WHITE);
            g.drawString("Running for: " + hour + ":" + minute + ":" + second, 7, 215);
            g.drawString("Status: " + status, 7, 230);
            g.drawString("Buried/hour: " + BonesAdded + " (" + AmountPerHour + ")", 7, 245);
            g.drawString("Exp Gained: " + gainedExp + " (" + (skills.getCurrentLvl(Constants.STAT_PRAYER) - startLevel) + ")", 7, 260);
            g.drawString("Exp per hour: " + (int) hourExp, 7, 275);
            g.drawString("Exp to level: " + expToLevel, 7, 290);
            g.drawString("Time to level: " + hoursToLevel + ":" + minutesToLevel + ":" + secToLevel, 7, 305);
            g.drawString("Progress to next level:", 7, 320);
            g.drawImage(image, 274, 345, null);
            g.setColor(RED);
            g.fill3DRect(7, 326, 100, 11, true);
            g.setColor(GREEN);
            g.fill3DRect(7, 326, skills.getPercentToNextLvl(Constants.STAT_PRAYER), 11, true);
            g.setColor(BLACK);
            g.drawString(skills.getPercentToNextLvl(Constants.STAT_PRAYER) + "%  to " + (skills.getCurrentLvl(Constants.STAT_PRAYER) + 1), 30, 335);
            g.setFont(new Font("Tohoma", Font.PLAIN, 11));
            g.setColor(BLACK);
            g.drawString(scriptInfo.name(), 20 + 1, 201 + 1);
            g.setColor(Color.white);
            g.drawString(scriptInfo.name(), 20, 201);
        }
    }

    public void messageReceived(MessageEvent e) {
        final String serverString = e.getMessage();
        if (serverString.contains("You bury")) {
            BonesAdded++;
        }
    }
}
