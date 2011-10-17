// zzBarbRodFisher by zzSleepzz
// Copyright 2009, 2010 zzSleepzz
//
// Proggy derived from pmiller624's pmCooker script
//
// Instructions: Start at Otto's fishing spot south of barb outpost.
//
// Features:
// v1.61 (21Feb2010)
// - Fixed drop selection to work when NOT selected
// - Changed to NOT drop fish bait.
//
// v1.6 (20Feb2010)
// - Added support for regular fish bait.
//
// v1.5 (1Feb2010)
//  - Added option to drop fish.  Also made it smart enough to assume dropping
//    if no knife in inventory.
//
// v1.4 (29Jan2010
//  - Adopted modified atNPC to prevent more wild mouse flings
//  - Attempts to use a FREE npc first, rather than piling on the
//    nearest one with everyone else.
//
// v1.3 (28Jan2010)
//  - Fixed incorrect version.
//  - Numerous tweeks to prevent the wild cursor to top left
//  - Tweeks to prevent selected item in inventory when
//    starting fishing.
//
// v1.2 (19JAN2010)
//  - A bunch of clean up
//  - Some speedup in not waiting for animations to complete
//  - Worked around the ghost cutting interface
//
// v1.1 (25Nov2009)
//   - Fixed scriptmanifest to add description.
//
// v1.0 (29June2009)
//
// The general idea for arious stylistic elements such as proggy and startup screen
// originated from various other scripts.
//

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"zzSleepzz"}, category = "Fishing", name = "zzBarbarian Rod Fisher", version = 1.61, description = "<html>"
+ "<head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body>"
+ "<center>"
+ "<b><font size=+2 color=\"blue\">Barbarian Rod Fisher v1.61</font></b><br>"
+ "<p><b>Author:</b> zzSleepzz"
+ "<p><p>"
+ "<table>"
+ "<tr valign=top>"
+ "<td align=right><b>Instructions:</b></td><td align=left> Start at Otto's Grotto with a barbarian fishing rod, a knife and a <b>maximum</b> of 25 feathers (for initial bait). <b>Leave one inventory slot free!</b></td>"
+ "</tr>"
+ "<tr><td align=\"right\" valign=\"center\"><input type=\"checkbox\" name=\"dropFish\" value=\"true\" checked=\"yes\"></td><td align=\"left\"<b>Drop fish</b></td></tr>"
+ "</table>" + "</center>" + "</body>\n" + "</html>")
public class zzBarbRodFisher extends Script implements MessageListener,
        PaintListener {

    final ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);

    public static final int FISHSPOT_ID = 14882;
    public static final int LEAPINGTROUT_ID = 11328;
    public static final int LEAPINGSALMON_ID = 11330;
    public static final int LEAPINGSTURGEON_ID = 11332;
    public static final int ROE_ID = 11324;
    public static final int OFFCUT_ID = 11334;
    public static final int CAVIAR_ID = 11326;
    public static final int FEATHER_ID = 314;
    public static final int FISHBAIT_ID = 313;
    public static final int KNIFE_ID = 946;
    public static final int ROD_ID = 11323;
    public static final RSTile[] CENTER_TILE = {new RSTile(2507, 3519),
        new RSTile(2499, 3504)};
    public static final int CUTTING_INTERFACE_ID = 905;
    public static final int CUTTING_INTERFACE_MAKE_ALL = 14;
    public boolean dropFish = true;
    private int retries;
    private int fish;

    private enum State {

        FISH, CUT, DROP
    }
    private State scriptState = State.FISH;
    // For proggy
    long hh = 0, mm = 0, ss = 0;
    long rawtime, startTime;

    @Override
    protected int getMouseSpeed() {
        return random(7, 10);
    }

    @Override
    public boolean onStart(Map<String, String> args) {
        if (!inventory.containsOneOf(CAVIAR_ID, FEATHER_ID, OFFCUT_ID, FISHBAIT_ID)
                && (!inventory.contains(KNIFE_ID) && !inventory.containsOneOf(
                LEAPINGSALMON_ID, LEAPINGTROUT_ID, LEAPINGSTURGEON_ID))) {

            log("No bait or no way to cut fish for bait...script will not start.");
            return false;
        }

        if (args.get("dropFish") != null || !inventory.contains(KNIFE_ID)) {
            dropFish = true;
            log("The script will drop all caught fish.");
        } else {
            dropFish = false;
        }

        if (!dropFish && inventory.containsOneOf(FISHBAIT_ID) && inventory.containsOneOf(FEATHER_ID)) {
            log("Inventory contains both bait and feathers.  One must be dropped for script to run.");
        }

        debug("Drop Fish: " + dropFish);

        return true;
    }

    @Override
    public void onFinish() {
    }

    @Override
    public int loop() {

        try {
            game.setRun(true);

            // Before hitting the busy/return catcher, check for interface 210
            // being
            // open and has the text that we've either run out of bait or don't
            // have any more room. This should speed the script up as it doesn't
            // pause, waiting for the current animation to finish.
            if (iface.get(210).isValid()) {
                RSInterfaceChild ch = iface.getChild(210, 1);

                if (ch != null) {
                    if (ch.containsText("carry any more")) {
                        debug("Out of room, dropping offcuts");
                        scriptState = State.DROP;
                    } else if (ch.containsText("any suitable bait")) {
                        debug("No bait left, cutting fish.");
                        scriptState = State.CUT;
                    }
                }
            } else if (!player.isIdle()) {
                RSPlayer p = player.getMine();
                if (inventory.containsOneOf(OFFCUT_ID, FISHBAIT_ID, FEATHER_ID,
                        CAVIAR_ID, ROE_ID)
                        && (p.getAnimation() == 623 || p.getAnimation() == 622)) {

                    if (random(1, 1000) < 10) {
                        mouse.moveSlightly();
                    }

                    return random(100, 150);
                } else if (inventory.containsOneOf(fish)
                        && p.getAnimation() == 6702) {
                    if (random(1, 1000) < 10) {
                        mouse.moveSlightly();
                    }

                    return random(100, 150);
                }
            }

            debug("scriptState=" + scriptState.toString());
            if (scriptState == State.FISH) {

                if (inventory.isFull()) {
                    // The only way to get inventory full is with offcuts
                    scriptState = State.DROP;
                    return 10;
                } else if (!inventory.containsOneOf(ROE_ID, FISHBAIT_ID,
                        CAVIAR_ID, FEATHER_ID)) {
                    scriptState = State.CUT;
                    return 10;
                }

                if (fish()) {
                    player.waitForAnim(1000);

                    if (random(1, 100) < 30) {
                        mouse.moveSlightly();
                    }
                }

                return random(80, 90);
            } else if (scriptState == State.CUT) {
                if (!(inventory.contains(LEAPINGTROUT_ID)
                        || inventory.contains(LEAPINGSALMON_ID) || inventory.contains(LEAPINGSTURGEON_ID))) {
                    if (inventory.isFull()) {
                        scriptState = State.DROP;
                        return 10;
                    } else {
                        scriptState = State.FISH;
                        return 10;
                    }
                }

                cut();

                return random(80, 90);
            } else if (scriptState == State.DROP) {
                retries = 0;

                if (dropFish) {
                    inventory.dropAllExcept(ROD_ID, FEATHER_ID, FISHBAIT_ID, KNIFE_ID);
                } else {
                    int ct = 0;

                    while (ct < 5 && inventory.contains(OFFCUT_ID)) {
                        inventory.clickItem(OFFCUT_ID, "Drop");
                        wait(random(1100, 1300));
                        ct++;
                    }
                }

                if (!inventory.contains(OFFCUT_ID)) {
                    scriptState = State.FISH;
                }

                return random(80, 90);
            }

            return random(80, 90);
        } catch (Exception e) {
            e.printStackTrace();
            return random(50, 100);
        }
    }

    public void messageReceived(MessageEvent e) { // Searches for
        // Messages in
        // chatbox
        String msg = e.getMessage();

        if (msg.contains("catch a leaping")) {
            itemsObtained++;
        } else if (msg.contains("can't carry")) {
            if (dropFish) {
                inventory.dropAllExcept(ROD_ID, FISHBAIT_ID, FEATHER_ID);
            }
        }
        /*
         * else if (msg.contains("any suitable bait")) { cut(); }
         */
    }

    public boolean fish() {
        if (!player.isIdle() && walk.getDestination().distanceTo() > 4) {
            return true;
        }

        // Find fishing spot.
        RSNPC fishingSpot = npc.getNearestByID(2722);

        if (fishingSpot == null || !fishingSpot.isValid()
                || !fishingSpot.getLocation().isValid()) {
            debug("No spots found.");

            if (CENTER_TILE[0].distanceTo() < CENTER_TILE[1].distanceTo()) {
                walk.to(walk.randomizeTile(CENTER_TILE[0], 1, 1));
            } else {
                walk.to(walk.randomizeTile(CENTER_TILE[1], 1, 1));
            }

            return false;
        } else if (!player.isIdle()) {
            debug("Player isn't idle.");
            return false;
        }

        if (fishingSpot.isOnScreen()) {
            boolean rv = false;

            if (fishingSpot.getLocation().isValid()) {
                debug("Found a spot: " + fishingSpot.getLocation().toString());

                if (inventory.isItemSelected()) {
                    inventory.clickItem(KNIFE_ID, "Use");
                }

                rv = myAtNPC(fishingSpot, "Use-rod", false);
                if (rv) {
                    wait(random(150, 350));
                    mouse.moveSlightly();
                    player.waitForAnim(2000);
                }
                debug("atNPC: " + rv);
            }

            if (rv && fishingSpot.distanceTo() > 1) {
                player.waitToMove(2000);
            }

            return rv;
        }

        debug("Spot not on screen.");
        RSTile t = npc.getNearestByID(2722).getLocation();
        if (t != null) {
            debug("Walking to fishing spot.");
            walk.to(t);
            player.waitToMove(1500);
        }

        return false;
    }

    public boolean cut() {
        debug("Initiating cut");

        RSInterface cutIntfc = iface.get(CUTTING_INTERFACE_ID);

        if (!cutIntfc.isValid() || cutIntfc.getLocation().equals(new Point(-1, -1))) {
            debug("Cut interface is not open yet.");

            retries = 0;
            while (retries < 10 && !inventory.isItemSelected()) {
                inventory.clickItem(KNIFE_ID, "Use");
                retries++;
                wait(random(250, 350));
            }

            if (inventory.contains(LEAPINGTROUT_ID)) {
                fish = LEAPINGTROUT_ID;
            } else if (inventory.contains(LEAPINGSALMON_ID)) {
                fish = LEAPINGSALMON_ID;
            } else if (inventory.contains(LEAPINGSTURGEON_ID)) {
                fish = LEAPINGSTURGEON_ID;
            }

            retries = 0;
            while (retries < 10 && !inventory.clickItem(fish, "Use")) {
                if (iface.waitForOpen(cutIntfc, 2500)) {
                    break;
                }

                retries++;
            }

            wait(random(900, 1000));
        }

        if (!cutIntfc.isValid() || cutIntfc.getLocation().equals(new Point(-1, -1))) {
            debug("Cut interface isn't open, can't specify make all");
            return false;
        }

        retries = 0;
        while (retries < 20
                && !iface.clickChild(CUTTING_INTERFACE_ID,
                				CUTTING_INTERFACE_MAKE_ALL)) {
            wait(random(250, 350));
            retries++;
        }
        wait(random(100, 200));
        mouse.moveSlightly();

        // Handle misclicks...either miss or Make X
        // if (iface.get(CUTTING_INTERFACE_ID).isValid()) {
        // debug("Exiting cut - cutting interface still open");
        // return false;
        // }

        retries = 0;
        while (retries < 10 && inventory.getCount(fish) > 0) {
            wait(250);
            retries++;
        }

        debug("Exiting cut(5)");

        return true;
    }
    // Proggy originally based on ProFisher2's proggy
    // Modified and enhanced by zzSleepzz
    private long scriptStartTime;
    private int startXP;
    private int startLevel;
    private int lastExp;
    private int index = STAT_FISHING;
    public int itemsObtained = 0;

    public void onRepaint(Graphics g) {
        Color PERCBAR = new Color(255, 255, 0, 150);

        long runTime = 0;
        long ss = 0, mm = 0, hh = 0;
        int expGained = 0;
        int levelsGained = 0;

        if (!game.isLoggedIn()) {
            return;
        }

        index = STAT_FISHING;
        if (scriptStartTime == 0) {
            scriptStartTime = System.currentTimeMillis();
        }

        if (lastExp == 0) {
            lastExp = skills.getCurrentXP(index);
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

        int y = 164;
        int x = 8;
        int boxwidthbase = 200;
        int boxwidth;

        g.setColor(new Color(255, 50, 50, 75));
        g.fillRoundRect(x, y, boxwidthbase, 164, 15, 15);

        // Calculate levels gained
        levelsGained = skills.getCurrentLvl(index) - startLevel;

        long runmins = mm + (hh * 60);
        Font f = g.getFont(); // Save for restoring after setting title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString(props.name() + " v" + props.version(), x += 6, y += 14);

        g.setFont(f);
        g.setColor(Color.WHITE);
        g.drawString("Run time: " + hh + ":" + mm + ":" + ss, x += 3, y += 16);
        g.drawString("Fish caught: " + itemsObtained, x, y += 12);
        g.drawString("Fish XP gained: " + expGained, x, y += 12);
        g.drawString("Fish Levels gained: " + levelsGained, x, y += 12);

        // Progress bar from Jacmob's scripts
        g.setColor(Color.RED);
        boxwidth = boxwidthbase;
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);

        g.setColor(Color.WHITE);
        g.drawString("XP to level " + (skills.getRealLvl(index) + 1)
                + ":  " + skills.getXPToNextLvl(index) + " ("
                + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        index = STAT_STRENGTH;
        y += 3;
        g.setColor(Color.RED);
        boxwidth = boxwidthbase;
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);
        g.setColor(Color.WHITE);
        g.drawString("XP to Str lvl " + (skills.getRealLvl(index) + 1)
                + ":  " + skills.getXPToNextLvl(index) + " ("
                + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        index = STAT_AGILITY;
        g.setColor(Color.RED);
        boxwidth = boxwidthbase;
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);
        g.setColor(Color.WHITE);
        g.drawString("XP to Agil lvl " + (skills.getRealLvl(index) + 1)
                + ":  " + skills.getXPToNextLvl(index) + " ("
                + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        index = STAT_COOKING;
        g.setColor(Color.RED);
        boxwidth = boxwidthbase;
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);
        g.setColor(Color.WHITE);
        g.drawString("XP to Cook lvl " + (skills.getRealLvl(index) + 1)
                + ":  " + skills.getXPToNextLvl(index) + " ("
                + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        if (runmins > 0 && expGained > 0) {
            index = STAT_FISHING;
            float lvlmins = skills.getXPToNextLvl(index)
                    / (expGained / runmins);
            g.drawString("Next fish level in " + (long) lvlmins + " minutes",
                    x, y += 14);
            g.drawString("Fish XP per hour: " + (expGained / runmins) * 60, x,
                    y += 12);
        }
    }

    /**
     * Clicks a humanoid character (tall and skinny).
     *
     * @param someNPC   The RSNPC to be clicked.
     * @param option    The option to be clicked (If available).
     * @param mousepath Whether or not to use {@link #mouse.moveByPath(Point)} rather
     *                  than {@link #mouse.move(Point)}.
     * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
     * @see #mouse.moveByPath(Point)
     * @see #atMenu(String)
     */
    public boolean myAtNPC(final RSNPC someNPC, final String option,
            final boolean mousepath) {
        for (int i = 0; i < 20; i++) {

            // Using someNPC.getScreenLocation() uses the RSCharacter.x/y
            // values for the screen point. The RSCharacter hooks must not
            // be right because they return -1,-1 when the NPC's location
            // is clearly on the screen.
            //
            // RSTile's getScreenLocation() doesn't appear to have the same
            // problem, so we can use the NPC's
            // getLocation().getScreenLocation()
            // instead.
            if (someNPC == null || !someNPC.isOnScreen()) {
                debug("NPC point: " + someNPC.getScreenLocation().toString());
                return false;
            }

            Point p = someNPC.getScreenLocation();
            p = new Point((int) Math.round(p.getX()) + random(-5, 5),
                    (int) Math.round(p.getY()) + random(-5, 5));
            debug("Original NPC Point: " + someNPC.getScreenLocation());
            debug("Using NPC point: " + p.toString());

            if (!mousepath) {
                mouse.move(p);
            } else {
                mouse.move(p, true);
            }
            if (menu.getItems()[0].toLowerCase().contains(
                    option.toLowerCase())) {
                mouse.click(true);
                return true;
            } else {
                final String[] menuItems = menu.getItems();
                for (final String item : menuItems) {
                    if (item.toLowerCase().contains(option.toLowerCase())) {
                        mouse.click(false);
                        return menu.action(option);
                    }
                }
            }
        }
        return false;
    }
}
