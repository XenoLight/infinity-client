import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = {"Scaper"}, category = "Woodcutting", name = "Seers Multi Location Mage Cutter", version = 2.5, description = "<html>"
+ "<head>"
+ "<style type=\"text/css\"> hr {color: yellow} p {margin-left: 10px} body {background-image: url(\"http://scaper-mage-cutter.webs.com/images/description1.png\")}</style>"
+ "</head>"
+ "<body>"
+ "<br /><br /><br />"
+ "<center>"
+ "<div><font size=\"4\" color=\"yellow\"<ul><b>Features:</b>"
+ "<br />"
+ "<font size=\"4\" color=\"yellow\"<li>Fast tree switching!</li>"
+ "<font size=\"4\" color=\"yellow\"<li>Flawless nest picking up!</li>"
+ "<font size=\"4\" color=\"yellow\"<li>Good Built In antiban!!!!!!</li>"
+ "<font size=\"4\" color=\"yellow\"<li>Hatchets Can Be Equipped or In Inventory</li>"
+ "<font size=\"4\" color=\"yellow\"<li>Takes ScreenShot On Finish</li>"
+ "</center>"
+ "</div>"
+ "<b><font size=\"4\" color=\"blue\">Which location would you like to woodcut in?</font></b><br /><select name='location'><option>Sorcers Garden</option><option>Range Guild</option>"
+ "<br></br>"
+ "<b><font size=\"4\" color=\"blue\">Use Cammy Tab To Bank?</font></b><br /><input type=checkbox name=useTeleTab value=true><br>"
+ "</ul>" + "</html></body>")
public class ScaperMageCutter extends Script implements PaintListener,
        MessageListener {

	private Bot bot;
    private final RSTile[] seersToBank = new RSTile[]{new RSTile(2749, 3477),
        new RSTile(2739, 3481), new RSTile(2728, 3485),
        new RSTile(2726, 3491)};
    final RSTile waitTile = new RSTile(2702, 3398);
    public RSTile seersTeleTile = new RSTile(2757, 3479);
    public RSTile[] loc, ToBank, treeLocs, endTile;
    public Image cape, hood, logo;
    private final Color GREEN = new Color(51, 255, 0, 180);
    private final Color RED = new Color(255, 0, 0, 177);
    final NumberFormat nf = NumberFormat.getInstance();
    private final int[] nestIds = {5070, 5071, 5072, 5073, 5074, 5075, 5076,
        7413, 11966};
    public final int[] junk = {1779, 1511, 995};
    public int speed = random(6, 11);
    final int[] axeID = new int[]{1351, 1349, 1353, 1361, 1355, 1357, 1359, 6739,
        13470};
    final int[] mageTree = new int[]{1306, 1308};
    static final int bankID = 25808;
    int treeID = 1309;
    static final int mageID = 1513;
    static final int tabID = 8010;
    int priceOfMageLog = 0;
    int startingLevel = 0;
    int startingExperience = 0;
    int waitAfterMoving = -1;
    int failCount = 0;
    int wait1 = 400;
    int wait2 = 700;
    final int index8 = Skills.getStatIndex("woodcutting");
    int checkTime1 = random(
            240000, 480000);
    int mageCut, nestFound, xpPerMage, countToNext, lastExp, oldExp, chopped,
            checkTime, currenttab, randomInt, GambleInt, woodcutting, new8,
            previouseSpeed, lastTreeIndex;
    long lastCheck, lastcheck1 = -1, lastCheck2, lastCheck3 = System.currentTimeMillis(), timer = System.currentTimeMillis(),
            startTime;
    public String[] thingsToSayIdle = new String[]{
        "Mages take soooooooo long man!", "Boreeeed",
        "cba anymore really annoying how long 1 mage takes to cut :/"};
    public String status = "Starting up";
    boolean useTeleTab = false;
    public double countUp = 0;
    Thread RS2_TIMER;
    public long Last_timer;
    public int lastEXP;
    public double x1;
    public static final double x2 = 2.5;
    public RSObject tree;

    public double getVersion() {
        return props.version();
    }

    public static String getAuthor() {
        return "Scapers Flawless Mage Cutter";
    }
    final ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);

    public void getMouseSpeed(final int speed) {
        this.speed = speed;
        mouse.getSpeed();
    }

    public int closestTree(final RSTile[] tiles) {
        int closest = -1;
        for (int i = 0; i < tiles.length; i++) {
            final RSTile tile = tiles[i];
            if (objects.getTopAt(tile) != null) {
                if (closest == -1 && isTree(objects.getTopAt(tile).getID())) {
                    closest = i;
                    continue;
                }
                if (closest == -1) {
                    continue;
                }
                final RSObject treeObj = objects.getTopAt(tile);
                if (treeObj != null
                        && isTree(treeObj.getID())
                        && treeObj.distanceTo() < objects.getTopAt(tiles[closest]).distanceTo()) {
                    closest = i;
                }
            }
        }
        return closest;
    }

    public boolean atTree(final RSTile tile) {
        try {
            final Point location = Calculations.tileToScreen(tile.getX(), tile.getY(), x1, x2, 0);

            if (location.x == -1 || location.y == -1 || location.x > 513
                    || location.y > 335 || location.x < 0 || location.y < 0) {
                return false;
            }
            if (getMenuActions()[0].toLowerCase().contains("down")) {
                mouse.click(true, 15);
                wait(random(1000, 2000));
                return true;
            }
            return false;
        } catch (final Exception e) {
        }
        return true;
    }

    public RSObject getClosestTreeByID(final int... ids) {
        RSObject cur = null;
        double dist = -1;

        for (int x = 0; x < 104; x++) {
            outer:
            for (int y = 0; y < 104; y++) {
                final RSObject o = objects.getTopAt(x + Bot.getClient().getBaseX(),
                        y + Bot.getClient().getBaseY());

                if (o != null) {
                    boolean isObject = false;
                    for (final int id : ids) {
                        if (o.getID() == id) {
                            isObject = true;
                            break;
                        }
                    }
                    if (isObject) {
                        final RSObject tl = objects.getTopAt(x
                                + Bot.getClient().getBaseX() - 1, y
                                + Bot.getClient().getBaseY());
                        final RSObject tb = objects.getTopAt(x
                                + Bot.getClient().getBaseX(), y
                                + Bot.getClient().getBaseY() - 1);
                        final int id = o.getID();
                        if (tl != null && tl.getID() == id || tb != null
                                && tb.getID() == id) {
                            continue outer;
                        }
                        final double distTmp = player.getMyLocation().distanceTo(o.getLocation());

                        if (cur == null || distTmp < dist) {
                            dist = distTmp;
                            cur = o;
                        }
                    }
                }
            }
        }
        return cur;
    }

    public boolean isTree(final int treeID) {
        for (final int id : mageTree) {
            if (id == treeID) {
                return true;
            }
        }
        return false;
    }

    public boolean atWelcomButton() {
        RSInterface welcomeInterface = iface.get(378);
        if (welcomeInterface.getChild(45).getAbsoluteX() > 20
                || (!welcomeInterface.getChild(117).getText().equals(
                "10.1120.190") && welcomeInterface.getChild(117).getText().length() != 0)) {
            status = "Welcome Screen";
            mouse.click(random(215, 555), random(420, 440), true);
            return true;
        } else {
            return false;
        }
    }

    public boolean clickInventoryItem(int itemID, boolean click) {
        if (game.getCurrentTab() != Constants.TAB_INVENTORY
                && !iface.get(INTERFACE_BANK).isValid()
                && !iface.get(INTERFACE_STORE).isValid()) {
            game.openTab(Constants.TAB_INVENTORY);
        }
        int[] items = inventory.getArray();
        java.util.List<Integer> possible = new ArrayList<Integer>();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == itemID) {
                possible.add(i);
            }
        }
        if (possible.isEmpty()) {
            return false;
        }
        int idx = possible.get(random(0, possible.size()));
        Point t = inventory.getItemPoint(idx);
        mouse.click(t, 5, 5, click);
        return true;
    }

    private boolean inCamelot() {
        return player.getMyLocation().getY() >= 3474
                && player.getMyLocation().getY() <= 3481
                && player.getMyLocation().getX() >= 2755
                && player.getMyLocation().getX() <= 2760;

    }

    private void walkToBank() {
        RSTile[] randomTTBPath = walk.randomizePath(seersToBank, 1, 1);
        if (player.getMyEnergy() > random(55, 100) && !isRunning()) {
            game.setRun(true);
        }
        for (RSTile aRandomTTBPath : randomTTBPath) {
           walk.tileMM(aRandomTTBPath);
            while (aRandomTTBPath.distanceTo() > 3) {
                wait(random(300, 600));
            }
        }
    }

    /**
     * BY: Scaper~~
     *
     * @param object <RSObject> Object to search for
     * @param action <String> What action to do?
     * @return <boolean> true if it found the object, and performed the action.
     */
    public boolean atMultiTiledTree_4(RSObject object, String action) {
        RSTile rstile = object.getLocation();
        RSTile rstile4 = new RSTile(rstile.getX() + 1, rstile.getY() + 1);
        Point point = Calculations.tileToScreen(rstile.getX(), rstile.getY(),
                10);
        Point point1 = Calculations.tileToScreen(rstile4.getX(),
                rstile4.getY(), 10);
        Point point2 = new Point((point.x + point1.x) / 2,
                (point.y + point1.y) / 2);
        if (point2.x == -1 || point2.y == -1) {
            return false;
        } else {
            mouse.move(point2, 3, 3);
            return menu.action(action);
        }
    }

    public void waitWhileMoving() {
        wait(random(800, 1000));
        while (player.getMine().isMoving()) {
            wait(random(50, 100));
        }
    }

    public boolean walkTile(RSTile t) {
        if (t.isOnScreen()) {
            tile.click(t, "Walk");
        } else {
            walk.to(t);
        }
        return true;
    }

    public int loop() {

        try {
            randomInt = random(1, 17);
            GambleInt = random(1, 17);
            if (atWelcomButton()) {
                return 500;
            }
            if (GambleInt == 1) {
                turnCamera();
            }
            final RSObject bankBooth = objects.getNearestByID(bankID);
            tree = getClosestTreeByID(mageTree);
            setMaxAltitude();
            nest();
            mouse.getSpeed();
            if (player.getMyEnergy() > random(60, 100)) {
                game.setRun(true);
            }
            if (inventory.getCount(junk) > 0) {
                clickInventoryArray(junk, "Drop");
                return 100;
            }
            if (!inventory.isFull()
                    && player.getMyLocation().distanceTo(treeLocs[3]) > 15) {
                try {
                    walk.pathMM(loc, 5, 2);
                } catch (final Exception e) {
                }
            }
            if (inventory.isFull()
                    && player.getMyLocation().distanceTo(new RSTile(2727, 3493)) > 5) {
                try {
                    walk.pathMM(ToBank, 3, 2);
                } catch (final Exception e) {
                }
            }
            if (inventory.isFull() && (useTeleTab)) {
                if (inventory.contains(tabID)) {
                    inventory.clickItem(tabID, "");
                }
                while (!inCamelot()) {
                    wait(10);
                }
                camera.setAltitude(true);
                camera.setCompass('N');
                mouse.moveSlightly();
                wait(random(500, 800));
                walkToBank();
            }
            if (player.getMyLocation().distanceTo(new RSTile(2727, 3493)) <= 5) {
                if (inventory.isFull()) {
                    if (bankBooth.isOnScreen()) {
                        bankBooth.action("use-quickly");
                        wait(random(1000, 2000));
                    }
                    if (bank.isOpen()) {
                        if (inventoryContainsAny(axeID)) {
                            bank.depositAllExcept(axeID);
                            if (inventoryContainsAny(tabID)) {
                                bank.depositAllExcept(tabID);
                            }
                        } else {
                            hitDepositButton();
                            wait(random(1000, 2000));
                        }/*
                         * try { bank.depositAllExcept(axeID); } catch (final
                         * Exception e) { }
                         */
                        bank.close();
                    }
                }
                if (!inventory.isFull()) {
                    status = "Walking To Trees";
                    try {
                        turnCamera();
                        walk.pathMM(loc, 5, 5);
                        antiBan();
                    } catch (final Exception e) {
                    }
                }
                return random(200, 400);
            }

            if (player.getMyLocation().distanceTo(treeLocs[3]) <= 15) {
                status = "Chopping Mages...";
                if (!inventory.isFull()) {
                    final int treeIndex = closestTree(treeLocs);
                    if (treeIndex != -1
                            && treeLocs[treeIndex].distanceTo() <= 14) {
                        if (inventory.contains(1511)) {
                            if (player.getMine().getAnimation() != -1
                                    && lastTreeIndex == treeIndex) {
                                return random(100, 200);
                            }
                        }
                        lastTreeIndex = treeIndex;
                        if (treeLocs[treeIndex].isOnScreen()
                                && player.getMine().getAnimation() == -1) {

                            atMultiTiledTree_4(tree, "Chop");
                            wait(random(100, 500));
                            return random(500, 1000);
                        }
                        if (player.getMine().getAnimation() == -1) {
                            walk.tileMM(new RSTile(treeLocs[treeIndex].getX(),
                                    treeLocs[treeIndex].getY() - 1));
                            antiBan();
                        }
                        return random(350, 700);
                    }
                    turnCamera();
                    if (player.getMine().getAnimation() == -1) {
                        tree = getClosestTreeByID(mageTree);
                        if (tree == null) {
                            return antiBan();
                        }
                        status = "Waiting To Chop...";
                        atTree(tree.getLocation());
                        wait(random(300, 1250));
                        return antiBan();
                    }
                    return antiBan();

                }
                if (inventory.isFull()) {
                    status = "Walking To Bank...";
                    try {
                        turnCamera();
                        walk.pathMM(ToBank);
                    } catch (final Exception e) {
                    }
                }
            }
        } catch (final Exception e) {
        }

        return random(500, 1000);
    }

    public void nest() {
        final RSGroundItem nest = ground.getItemByID(nestIds);
        if (nest != null && !inventory.isFull()) {
            status = "Picking Up Nest...";
            nest.action("Take");
            // sendText(nestTalk[random(0, nestTalk.length - 1)], true);
        }
    }

    private static void highlightTile(final Graphics g, final RSTile t,
            final Color outline, final Color fill) {
        final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
        final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
                0, 0);
        final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
                0, 0);
        final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
                0, 0, 0);
        if (py.x == -1 || pxy.x == -1 || px.x == -1 || pn.x == -1) {
            return;
        }
        g.setColor(outline);
        g.drawPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
                    pxy.y, px.y, pn.y}, 4);
        g.setColor(fill);
        g.fillPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
                    pxy.y, px.y, pn.y}, 4);
    }

    public void drawPlayer(final Graphics g) {
        final RSTile t = player.getMyLocation();
        Calculations.tileToScreen(t);
        final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
        final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
                0, 0);
        final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
                0, 0);
        final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
                0, 0, 0);
        player.getMine().getHeight();
        g.setColor(Color.BLACK);
        g.drawPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
                    pxy.y, px.y, pn.y}, 4);
        g.setColor(new Color(102, 0, 102, 75));
        g.fillPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
                    pxy.y, px.y, pn.y}, 4);
    }

    public void drawMouse(final Graphics g) {
        final Point mmmm = mouse.getLocation();
        g.setColor(new Color(204, 204, 0, 141));
        g.drawRoundRect(mmmm.x - 6, mmmm.y, 15, 3, 5, 5);
        g.drawRoundRect(mmmm.x, mmmm.y - 6, 3, 15, 5, 5);
        g.setColor(new Color(204, 204, 0, 141));
        g.fillRoundRect(mmmm.x - 6, mmmm.y, 15, 3, 5, 5);
        g.fillRoundRect(mmmm.x, mmmm.y - 6, 3, 15, 5, 5);
    }

    public void onRepaint(final Graphics g) {

        // if (isLoggedIn()) {
        long millis = System.currentTimeMillis() - startTime;
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        DecimalFormat d = new DecimalFormat("#,#00");
        d.setMaximumFractionDigits(0);

        int exp = 0, type = STAT_WOODCUTTING, perNextLvl = skills.getPercentToNextLvl(type), nextLvl = skills.getCurrentLvl(type) + 1;
        exp = skills.getCurrentXP(STAT_WOODCUTTING) - startingExperience;

        if (exp > oldExp) {
            xpPerMage = exp - oldExp;
            oldExp = exp;
            chopped++;
            countToNext = skills.getXPToNextLvl(STAT_WOODCUTTING) / xpPerMage
                    + 1;
        }

        if (lastExp == 0) {
            lastExp = skills.getCurrentXP(Constants.STAT_WOODCUTTING);
        }

        if (skills.getCurrentXP(Constants.STAT_WOODCUTTING) > lastExp) {
            lastExp = skills.getCurrentXP(Constants.STAT_WOODCUTTING);
            chopped++;
        }

        if (startingLevel == 0 || startingExperience == 0) {
            startingLevel = skills.getCurrentLvl(Constants.STAT_WOODCUTTING);
            startingExperience = skills.getCurrentXP(Constants.STAT_WOODCUTTING);
        }

        final long hours = millis / (1000 * 60 * 60);
        millis -= hours * 1000 * 60 * 60;
        final long minutes = millis / (1000 * 60);
        millis -= minutes * 1000 * 60;
        final long seconds = millis / 1000;
        if ((System.currentTimeMillis() - startTime) > 1) {
            final int expph = (int) ((exp) * 3600000D / (System.currentTimeMillis() - startTime));
            final int logsph = (int) ((mageCut) * 3600000D / (System.currentTimeMillis() - startTime));
            final int moneyph = (int) logsph * priceOfMageLog;
            final int moneyGained = (int) ((mageCut) * priceOfMageLog);

            // overlayTile(g,player.getMyLocation());
            /** Drawings **/
            g.drawImage(logo, 75, 30, null);
            g.drawImage(cape, 10, 10, null);
            g.drawImage(hood, 434, 10, null);

            // if(game.getCurrentTab() == TAB_INVENTORY) {

            /** Title Box **/
            g.setColor(new Color(204, 204, 0));
            g.drawRect(551, 206, 183, 39);
            g.setColor(new Color(102, 0, 102, 162));
            g.fillRect(553, 207, 180, 38);

            /** Main Box OutLine **/
            g.setColor(new Color(204, 204, 0));
            g.drawRect(550, 249, 183, 213);
            g.setColor(new Color(102, 0, 102, 165));
            g.fillRect(552, 251, 180, 210);

            /** Title **/
            g.setColor(new Color(0, 0, 0));
            g.drawString("Scaper Mage Cutter v2.4", 559, 233);
            g.setFont(new Font("Arial Black", 0, 12));
            g.setColor(new Color(255, 255, 0, 237));
            g.drawString("Scaper Mage Cutter v2.4", 559, 234);

            /** Main Box **/
            g.setFont(new Font("Tahoma", Font.BOLD, 11));
            g.setColor(new Color(255, 255, 0));
            g.drawString("TimeRunning: " + d.format(hours) + ":" + d.format(minutes)
                    + ":" + d.format(seconds), 557, 268);
            g.drawString("Mages Chopped: " + insertCommas(String.valueOf(mageCut)), 557,
                    286);
            g.drawString("Mages Per Hour: " + (String.valueOf(logsph)), 557, 302);
            g.drawString("Money Gained: " + insertCommas(String.valueOf(moneyGained))
                    + "Gp", 557, 318);
            g.drawString(
                    "Money Per Hour: " + insertCommas(String.valueOf(moneyph)) + "Gp",
                    557, 334);
            g.drawString("Exp Gained: " + insertCommas(String.valueOf(exp)) + "xp", 557,
                    349);
            g.drawString("Exp Per Hour: " + insertCommas(String.valueOf(expph)) + "xp",
                    557, 364);
            g.drawString(
                    "Gained "
                    + (skills.getCurrentLvl(Constants.STAT_WOODCUTTING) - startingLevel)
                    + " Levels", 556, 379);
            g.drawString("Found " + nestFound + " Nests.", 557, 394);
            g.drawString("Percent To Next Level:", 557, 408);

            /** Progress Bar **/
            if (millis != 0) {
                g.setColor(new Color(0, 0, 0));
                g.draw3DRect(558, 422, 166, 19, true);
                g.setFont(new Font("Arial Black", Font.BOLD, 11));

                g.setColor(RED);
                g.fill3DRect(558, 422, 165, 18, true);

                g.setColor(GREEN);
                g.fill3DRect(558, 422, perNextLvl * 165 / 100, 18, true);
                g.setColor(new Color(0, 0, 0));
                g.drawString(perNextLvl + "% to " + nextLvl, 610, 436);

            }
            // }
            if (objects.getNearestByID(mageTree) != null) {
                highlightTile(g, objects.getNearestByID(mageTree).getLocation(),
                        new Color(0, 255, 0, 80), new Color(0, 0, 255, 20));
            }
            drawPlayer(g);
            drawMouse(g);
        }
    }

    private static String insertCommas(final String str) {
        return str.length() < 4 ? str : (insertCommas(str.substring(0, str.length() - 3))
                + "." + str.substring(str.length() - 3, str.length()));
    }

    public String formatTime(final int milliseconds) {
        final long t_seconds = milliseconds / 1000;
        final long t_minutes = t_seconds / 60;
        final long t_hours = t_minutes / 60;
        final int seconds = (int) (t_seconds % 60);
        final int minutes = (int) (t_minutes % 60);
        final int hours = (int) (t_hours % 60);
        return (nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds));
    }

    private boolean inventoryContainsAny(int... items) {
        for (int item : inventory.getArray()) {
            for (int id : items) {
                if (item == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public void dropJunk() {
        if (inventory.contains(junk)) {
            clickInventoryArray(junk, "Drop");
            wait(random(500, 700));
        }
    }

    private boolean clickInventoryArray(int[] itemID, String option) {
        if (game.getCurrentTab() != TAB_INVENTORY
                && !iface.get(INTERFACE_BANK).isValid()
                && !iface.get(INTERFACE_STORE).isValid()) {
            game.openTab(TAB_INVENTORY);
        }
        int[] items = inventory.getArray();
        java.util.List<Integer> possible = new ArrayList<Integer>();
        for (int X = 0; X < itemID.length; X++) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] == itemID[X]) {
                    possible.add(i);
                }
            }
        }
        if (possible.isEmpty()) {
            return false;
        } else {
            int idx = possible.get(random(0, possible.size()));
            Point t = inventory.getItemPoint(idx);
            mouse.click(t, 3, 5, false);
            return menu.action(option);
        }
    }

    public boolean hitDepositButton() {
        if (!bank.isOpen()) {
            return false;
        }
        Point p = mouse.getLocation();
        if (!(p.x < 413 && p.x > 381 && p.y < 320 && p.y > 297)) {
            mouse.move(random(381, 413), random(297, 320));
        }
        mouse.click(true);
        return true;
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

    @Override
    public boolean onStart(Map<String, String> args) {
        try {
            RS2_TIMER = new RS_TIMER();
            RS2_TIMER.start();
            startTime = System.currentTimeMillis();
            priceOfMageLog = getMarketPriceOfItem(mageID);
            wait(random(2000, 3000));
            cape = Toolkit.getDefaultToolkit().getImage(
                    new URL("http://i47.tinypic.com/ek48xx.png"));
            hood = Toolkit.getDefaultToolkit().getImage(
                    new URL("http://i47.tinypic.com/ek48xx.png"));

            /*
             * logo = Toolkit .getDefaultToolkit() .getImage( new URL(
             * "http://i50.tinypic.com/30js0p4.png"
             * ));http://i46.tinypic.com/16c71v9.png
             */

            logo = Toolkit.getDefaultToolkit().getImage(
                    new URL("http://scaper-mage-cutter.webs.com/images/logomage.png"));

        } catch (final Exception e) {
        }
        final String location = args.get("location");
        if (location.equals("Sorcers Garden")) {
            loc = new RSTile[]{new RSTile(2726, 3491),
                        new RSTile(2727, 3483), new RSTile(2726, 3474),
                        new RSTile(2727, 3465), new RSTile(2727, 3457),
                        new RSTile(2731, 3450), new RSTile(2731, 3439),
                        new RSTile(2723, 3433), new RSTile(2717, 3425),
                        new RSTile(2719, 3413), new RSTile(2721, 3404),
                        new RSTile(2715, 3396), new RSTile(2702, 3393),
                        new RSTile(2702, 3398)};

            treeLocs = new RSTile[]{new RSTile(2699, 3398),
                        new RSTile(2699, 3396), new RSTile(2705, 3399),
                        new RSTile(2705, 3397)};

            ToBank = walk.reversePath(loc);

            endTile = new RSTile[]{new RSTile(2702, 3398)};
        } else if (location.equals("Range Guild")) {
            loc = new RSTile[]{new RSTile(2725, 3492),
                        new RSTile(2725, 3485), new RSTile(2725, 3475),
                        new RSTile(2723, 3467), new RSTile(2722, 3455),
                        new RSTile(2713, 3450), new RSTile(2703, 3441),
                        new RSTile(2703, 3430), new RSTile(2698, 3425),};
            treeLocs = new RSTile[]{new RSTile(2697, 3424),
                        new RSTile(2692, 3425), new RSTile(2691, 3427),
                        new RSTile(2694, 3425)};
            ToBank = walk.reversePath(loc);

            endTile = new RSTile[]{new RSTile(2698, 3425)};
        }
        if (args.get("useTeleTab") != null) {
            useTeleTab = true;
        }

        return true;
    }


    @Override
    public void onFinish() {
        ScreenshotUtil.takeScreenshot(bot, true);
    }

    public void check() {

        final long currentTime = System.currentTimeMillis();
        if (lastCheck == -1) {
            woodcutting = skills.getCurrentXP(index8);
            lastCheck = 1;
        }
        if ((currentTime - lastcheck1) / 60000 >= 1) {
            lastcheck1 = System.currentTimeMillis();
            lastCheck = -1;
        }
    }

    int antiBan() {
        int GambleInt = random(1, 6);
        switch (GambleInt) {
            case 1:
                wait(random(1000, 1500));
                break;
            case 2:
                if (random(1, 4) == 1) {
                    int x = random(0, 750);
                    int y = random(0, 500);
                    mouse.move(0, 0, x, y);
                }
                return random(1300, 1600);
            case 3:
                if (game.getCurrentTab() != TAB_INVENTORY) {
                    game.openTab(TAB_INVENTORY);
                    return random(500, 750);
                } else {
                    return random(500, 750);
                }
            case 4:
                if (player.getMine().isMoving()) {
                    return random(750, 1000);
                }

                if (System.currentTimeMillis() - lastCheck >= checkTime) {
                    lastCheck = System.currentTimeMillis();
                    checkTime = random(60000, 180000);

                    if (game.getCurrentTab() != Constants.TAB_STATS) {
                        game.openTab(Constants.TAB_STATS);
                    }
                    mouse.move(693, 403);
                    return random(5000, 8000);
                }

            case 5:
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
                return random(500, 750);
        }
        return random(500, 1000);
    }

    public void messageReceived(final MessageEvent e) {
        if (e.getMessage().contains("You get some magic logs.")) {
            mageCut++;
        }
        if (e.getMessage().contains(
                "You've just advanced an Woodcutting level! You have reached level")) {
            ScreenshotUtil.takeScreenshot(bot, true);
        }
        if (e.getMessage().contains("A bird's nest falls out of the tree.")) {
            nestFound++;
        }
        if (e.getMessage().contains("<col=ffff00>System update in")) {
            stopScript(true);
        }
        if (e.getMessage().contains("Oh dear, you are dead!")) {
            status = "Dead";
            log("We somehow died :S, shutting down");
            stopScript(true);
        }
    }

    public void setMaxAltitude() {
        Bot.getInputManager().pressKey((char) 38);
        wait(random(500, 1000));
        Bot.getInputManager().releaseKey((char) 38);
    }

    public void turnCamera() {
        char[] LR = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
        char[] UD = new char[]{KeyEvent.VK_DOWN, KeyEvent.VK_UP};
        char[] LRUD = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN};
        int random2 = random(0, 2);
        int random1 = random(0, 2);
        int random4 = random(0, 4);

        if (random(0, 2) == 2) {
            Bot.getInputManager().pressKey(LR[random1]);
            try {
                Thread.sleep(random(100, 400));
            } catch (Exception e) {
            }
            Bot.getInputManager().pressKey(UD[random2]);
            try {
                Thread.sleep(random(300, 600));
            } catch (Exception e) {
            }
            Bot.getInputManager().releaseKey(UD[random2]);
            try {
                Thread.sleep(random(100, 400));
            } catch (Exception e) {
            }
            Bot.getInputManager().releaseKey(LR[random1]);
        } else {
            Bot.getInputManager().pressKey(LRUD[random4]);
            if (random4 > 1) {
                try {
                    Thread.sleep(random(300, 600));
                } catch (Exception e) {
                }
            } else {
                try {
                    Thread.sleep(random(500, 900));
                } catch (Exception e) {
                }
            }
            Bot.getInputManager().releaseKey(LRUD[random4]);
        }
    }

    public static int getMarketPriceOfItem(final int id) {
        String pageSource = "";
        int begin = 0;
        int end = 0;
        try {
            final URL theUrl = new URL(
                    "http://services.runescape.com/m=itemdb_rs/Magic_logs/viewitem.ws?obj="
                    + id);
            final URLConnection theUrlConnection = theUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    theUrlConnection.getInputStream()));
            String inputLine = "";
            while ((inputLine = in.readLine()) != null) {
                pageSource += inputLine;
            }
            in.close();
            pageSource = pageSource.replaceAll("\n", "");
            pageSource = pageSource.replaceAll("\t", "");
            pageSource = pageSource.replaceAll(",", "");
            begin = pageSource.indexOf("<b>Market price:</b> ")
                    + "<b>Market price:</b> ".length();
            end = pageSource.indexOf("</span><span><b>Maximum price:</b>");
        } catch (final Exception e) {
            System.out.println("http://services.runescape.com/m=itemdb_rs/Magic_logs/viewitem.ws?obj="
                    + id);
        }
        return (int) new Integer(pageSource.substring(begin, end)).intValue();
    }

    public static class RS_TIMER extends Thread {

        @Override
        public void run() {
            while (true) {
            }
        }
    }
}