// zzWatchmanThiever by zzSleepzz
// Copyright 2009,2010 zzSleepzz
//
// Proggy derived from pmiller624's pmCooker script
//
// Instructions: Start in 2nd floor of Yanille Watchtower.
//
// Features:
// v0.1 (7June20009)
//

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = "zzSleepzz", category = "Thieving", name = "zzWatchmanThiever", version = 1.0, description = "<html>"
+ "<head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body>"
+ "<center>"
+ "<b><font size=+2 color=\"blue\">zzWatchmanThiever v1.0 by zzSleepzz</font></b><br>"
+ "<font size=-2>Based on the AutoAlcher script created by Zachafer</font>"
+ "<p><b>Author:</b> zzSleepzz"
+ "<p><p>"
+ "<table>"
+ "<tr valign=top>"
+ "<td align=right><b>Instructions:</b></td><td align=left> Start in Yanille watchtower's 2nd floor.  Empty inventory..</td>"
+ "</tr>"
+ "</table>"
+ "</center>"
+ "</body>\n"
+ "</html>")
public class zzWatchmanThiever extends Script implements PaintListener, MessageListener {

    private static final boolean randomActivated = false;  // Hack for stupid RSBot
    public static final int BREAD_ID = 2309;
    public static final int FLOOR3_LADDER_ID = 2797;
    public static final int FLOOR2_LADDER_ID = 17122;
    public static final int FLOOR1_LADDER_ID = 2833;
    public static final int SILENCE_GLOVES_ID = 10075;
    public static final int WATCHMAN_ID = 34;  //34 for watchman, 32 for ardy guards,
    // 23 for Ardy Knights, 2256 for ardy paladins
    public boolean stunned = false;
    public boolean getNewGloves = false;

    enum STATE {

        EAT, DROP, PICK, ON_FLOOR3, ON_FLOOR1, GOTO_FLOOR1
    }
    STATE state;

    public boolean onStart(Map<String, String> args) {
        while (!game.isLoggedIn()) {
            wait(100);
        }

        state = STATE.PICK;

        if (!equipment.contains(SILENCE_GLOVES_ID) && inventory.contains(SILENCE_GLOVES_ID)) {
            inventory.clickItem(SILENCE_GLOVES_ID, "Wear ");
        }

        return true;
    }
    // Proggy originally based on ProFisher2's proggy
    // Modified and enhanced by zzSleepzz
    private long scriptStartTime = 0;
    private int startXP = 0, lastXP = 0;
    private int startLevel = 0;
    private static final int index = STAT_THIEVING;
    private int picks = 0, fails = 0;

    public void onRepaint(Graphics g) {
        Color PERCBAR = new Color(255, 255, 0, 150);
        long runTime = 0;
        long ss = 0, mm = 0, hh = 0;
        int expGained = 0;
        int levelsGained = 0;
        if (!game.isLoggedIn()) {
            return;
        }

        if (scriptStartTime == 0) {
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

        int x = 12;
        int y = 183;
        int boxwidth = 180;

        g.setColor(new Color(255, 50, 50, 75));
        g.fillRoundRect(x - 7, y, boxwidth, 138, 15, 15);
        g.setColor(new Color(215, 40, 40, 250));
        g.drawRoundRect(x - 7, y, boxwidth, 138, 15, 15);
        g.drawRoundRect(x - 6, y - 1, boxwidth, 140, 15, 15);

        long runmins = mm + (hh * 60);
        Font f = g.getFont();  // Save for restoring after setting title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString("zzWatchmanThiever  v1.0", x, y += 16);

        g.setFont(f);
        g.setColor(Color.WHITE);
        g.drawString("Run time:  " + hh + ":" + mm + ":" + ss, x, y += 16);
        g.drawString("Successes: " + picks, x, y += 13);
        g.drawString("Failures: " + fails, x, y += 13);
        g.drawString("XP gained: " + expGained, x, y += 13);
        g.drawString("Levels gained: " + levelsGained, x, y += 13);

        //Progress bar from Jacmob's scripts
        g.setColor(Color.RED);
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);

        g.setColor(Color.WHITE);
        g.drawString("XP to level:  " + skills.getXPToNextLvl(index) + " (" + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        if (runmins > 0 && expGained > 0) {
            float lvlmins = skills.getXPToNextLvl(index) / (expGained / runmins);
            g.drawString("Time to next level: " + (long) lvlmins + " minutes", x, y += 13);
            g.drawString("XP per hour : " + (expGained / runmins) * 60, x, y += 13);
        } else {
            g.drawString("Time to next level: ", x, y += 13);
            g.drawString("XP per hour : ", x, y += 13);
        }
    }

    public int loop() {
        try {
            if (!game.isLoggedIn()) {
                return random(90, 100);
            }

            if (skills.getCurrentLvl(STAT_HITPOINTS) < 8) {
                stopScript(false);
            }

            game.setRun(true);

            if (player.getMine().isMoving()) {
                return (random(100, 200));
            }

            // Occassionally get into combat with watchman, so avoid it when
            // it happens.
            if (player.getMine().isInCombat()) {
                if (stunned) {
                    state = STATE.EAT;
                } else {
                    debug("Player in combat, evading.");
                    state = STATE.GOTO_FLOOR1;
                }
            }

            zzAntiban();  // It will determine if it's time to really antiban.

            if (inventory.getCount() == 28) {
                state = STATE.DROP;
            }

            if (game.getPlane() == 0) {
                state = STATE.ON_FLOOR1;
            } else if (game.getPlane() == 2) {
                state = STATE.ON_FLOOR3;
            }

            debug("State=" + state);
            if (state == STATE.EAT) {

                // If current HPs are not less than 3 below max,
                // don't eat.
                if (!stunned && skills.getCurrentLvl(STAT_HITPOINTS)
                        > skills.getRealLvl(STAT_HITPOINTS) - 3) {
                    state = STATE.PICK;
                    return random(20, 30);
                }

                int breads = inventory.getCount(BREAD_ID);

                if (breads == 0 && !stunned) {
                    state = STATE.PICK;
                    return random(40, 50);
                }

                inventory.clickItem(BREAD_ID, "Eat");
                player.waitForAnim(750);
                wait(random(300, 450));

                if (stunned) {
                    RSNPC w = npc.getNearestFreeByID(WATCHMAN_ID);
                    mouse.move(w.getScreenLocation(), 5, 5);

                    while (player.getMine().isInCombat()) {
                        wait(50);
                    }

                    stunned = false;
                    state = STATE.PICK;
                }

                return random(30, 40);
            } else if (state == STATE.PICK) {
                if (stunned || player.getMine().isInCombat()) {
                    state = STATE.EAT;
                    return 10;
                }

                RSNPC w = npc.getNearestFreeByID(WATCHMAN_ID);

                if (w == null) {
                    return random(100, 200);
                }

                npc.action(w, "Pickpocket");
                player.waitForAnim(1300);
                wait(random(1080, 1180));

                if (stunned || player.getMine().isInCombat()) {
                    state = STATE.EAT;
                }

                return 90;
            } else if (state == STATE.GOTO_FLOOR1) {
                if (game.getPlane() == 0) {
                    state = STATE.ON_FLOOR1;
                    return random(100, 200);
                } else if (game.getPlane() == 2) {
                    state = STATE.ON_FLOOR3;
                    return random(100, 200);
                }

                RSObject ladder = objects.getNearestByID(FLOOR2_LADDER_ID);

                if (ladder == null) {
                    return random(20, 30);
                }

                RSTile loc = ladder.getLocation();
                if (!loc.isOnScreen()) {
                    walk.tileMM(loc);

                    return random(500, 600);
                }

                ladder.action("Climb-down");
                return random(700, 800);
            } else if (state == STATE.DROP) {
                for (int n = random(5, 9); n > 0; n--) {
                    inventory.clickItem(BREAD_ID, "Drop");
                    if (randomActivated) {
                        return random(30, 40);
                    }

                    wait(random(300, 400));
                }

                state = STATE.PICK;
            } else if (state == STATE.ON_FLOOR1) {
                if (game.getPlane() == 1) {
                    state = STATE.PICK;
                    return random(100, 200);
                } else if (game.getPlane() == 2) {
                    state = STATE.ON_FLOOR3;
                    return random(100, 200);
                }

                RSObject ladder = objects.getNearestByID(FLOOR1_LADDER_ID);

                if (ladder == null) {
                    return random(20, 30);
                }

                RSTile loc = ladder.getLocation();
                if (!loc.isOnScreen()) {
                    walk.tileMM(loc);

                    return random(500, 600);
                }

                while (player.getMine().isInCombat()) {
                    wait(50);
                }

                ladder.action("Climb-up");
                return random(700, 800);
            } else if (state == STATE.ON_FLOOR3) {
                if (game.getPlane() == 1) {
                    state = STATE.PICK;
                    return random(100, 200);
                } else if (game.getPlane() == 0) {
                    state = STATE.ON_FLOOR1;
                    return random(100, 200);
                }

                RSObject ladder = objects.getNearestByID(FLOOR3_LADDER_ID);

                if (ladder == null) {
                    return random(20, 30);
                }

                RSTile loc = ladder.getLocation();
                if (!loc.isOnScreen()) {
                    walk.tileMM(loc);

                    return random(500, 600);
                }

                ladder.action("Climb-down");
                return random(700, 800);
            }
        } catch (Exception e) {
        }


        return random(10, 20);
    }

    // This  support is based on WarXperiment's, but
    // doesn't do as many things.  This will:
    //   - move the mouse to a nearby player to see name/level
    //  - move to and right click a nearby player to see the
    //    players there.
    //  - Move the mouse to select the skills tab and hover over
    //    Woodcutting for a short while.
    //  - Move the mouse to the friends list to see who's online
    //  - Tab actions will restore the inventory tab when done.
    //  - Mouse actions will move the mouse back to a slightly
    //    different location from where it originally was.
    //  - Won't duplicate the prior action.
    // HoverPlayer 1-30, ClickPlayer 31-60, SkillsTab 61-75, FriendsTab 76-80,
    // EquipmentTab 81-85
    private enum Actions {

        HoverPlayer, ClickPlayer, SkillsTab, FriendsTab,
        InventoryTab, EquipmentTab
    }
    private final Actions lastAction = Actions.EquipmentTab;
    private long antibanTime = System.currentTimeMillis() + random(90000, 120000);
    static final boolean pauseAntiban = false;

    private void zzAntiban() {

        if (pauseAntiban) {
            return;
        }

        long currTime = System.currentTimeMillis();
        if (antibanTime < currTime) {
            // Set to go off again in another 90-120 secs.
            antibanTime = currTime + random(90000, 120000);
        } else {
            return;
        }

        int r = random(1, 85);
        Point mousePos = mouse.getLocation();

        if (r < 20) {  // SkillsTab
            if (lastAction == Actions.SkillsTab) {
                zzAntiban();  // retry for diff action.
            }

            int r1 = random(1, 100);
            RSPlayer p = player.getNearestByLevel(1, 130);
            if (r1 < 36) {
                if ((p != null) && p.isOnScreen()) {
                    mouse.move(p.getScreenLocation(), 40, 40);
                    wait(random(450, 650));
                }
            } else if (r1 < 71) {
                if ((p != null) && p.isOnScreen()) {
                    mouse.move(p.getScreenLocation(), 10, 10);
                    wait(random(300, 450));
                    mouse.click(false);
                    wait(random(400, 500));
                }
            }

            if (game.getCurrentTab() != TAB_STATS) {
                final RSInterfaceChild agitab =
                        iface.get(320).getChild(134);

                game.openTab(TAB_STATS);
                mouse.move(new Point(agitab.getAbsoluteX()
                        + random(2, agitab.getWidth() - 1), agitab.getAbsoluteY()
                        + random(2, agitab.getHeight() - 1)));
                wait(random(1000, 2000));
            }
        } else if (r < 40) {  // FriendsTab
            if (lastAction == Actions.FriendsTab) {
                zzAntiban();  // retry for diff action.
            }

            int r1 = random(1, 100);
            RSPlayer p = player.getNearestByLevel(1, 130);
            if (r1 < 36) {
                if ((p != null) && p.isOnScreen()) {
                    mouse.move(p.getScreenLocation(), 40, 40);
                    wait(random(450, 650));
                }
            } else if (r1 < 71) {
                if ((p != null) && p.isOnScreen()) {
                    mouse.move(p.getScreenLocation(), 10, 10);
                    wait(random(300, 450));
                    mouse.click(false);
                    wait(random(400, 500));
                }
            }

            if (game.getCurrentTab() != TAB_FRIENDS) {
                game.openTab(TAB_FRIENDS);
            }
        } else if (r < 50) {  // EquipmentTab
            if (lastAction == Actions.EquipmentTab) {
                zzAntiban();  // retry for diff action.
            }

            int r1 = random(1, 100);
            RSPlayer p = player.getNearestByLevel(1, 130);
            if (r1 < 36) {
                if ((p != null) && p.isOnScreen()) {
                    mouse.move(p.getScreenLocation(), 40, 40);
                    wait(random(450, 650));
                }
            } else if (r1 < 71) {
                if ((p != null) && p.isOnScreen()) {
                    mouse.move(p.getScreenLocation(), 10, 10);
                    wait(random(300, 450));
                    mouse.click(false);
                    wait(random(400, 500));
                }
            }
        } else if (r > 95 && r < 101) {
            camera.setAltitude(random(85, 100));
            if (inventory.contains(SILENCE_GLOVES_ID)) {
                inventory.clickItem(SILENCE_GLOVES_ID, "Check");
            }
        }

        wait(random(800, 950));
        if (game.getCurrentTab() != TAB_INVENTORY) {
            game.openTab(TAB_INVENTORY);
        }
        wait(random(250, 350));
        mouse.move(mousePos, 40, 40);
        wait(random(300, 500));

    }

    public void messageReceived(MessageEvent e) {
        String m = e.getMessage();

        if (m.contains("fail to pick")) {
            fails++;
        } else if (m.contains("You pick the watchman's pocket")) {
            picks++;
        } else if (m.contains("enough space")) {
            state = STATE.DROP;
        } else if (m.contains("gloves of silence")) {
            getNewGloves = true;
            debug("Setting up for new gloves.");
        }

        if (m.contains("stunned")) {
            stunned = true;
            state = STATE.EAT;

            if (getNewGloves && !equipment.contains(SILENCE_GLOVES_ID)) {
                debug("Trying to put on new gloves.");
                if (inventory.clickItem(SILENCE_GLOVES_ID, "Wear")) {
                    debug("Wearing new gloves.");
                    getNewGloves = false;
                }
            }
        }


    }
}
