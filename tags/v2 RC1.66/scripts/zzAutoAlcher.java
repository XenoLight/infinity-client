//zzSleepzz Enhancements
//
// v1.8 7Sep2011
// - Removed code to disable ImprovedRewardsBox...the inventory opening is no
//   longer an issue.
//
// v1.7 24June2011
// - Updated to new Infinity API
//
// v1.62 16June2010
// - Fixed calling ImprovedRewardsBox 
//
// v1.61 14Jun2010
// - Changed over to self-contained zzAntiban.
// - Add check for ImprovedRewardsBox random while inventory is already open.
// - Undoes the spell being cast in order to attempt rewards box.
//
// v1.51 23Jan2010
// - Disables the ImprovedRewardsBox random to prevent inventory cycling.
// 
// v1.5 4Jan2010
// - Fixed the unnecessary cycling through the inventory when casting spells due
//   to check for nature runes added in v1.4
// - Changed to monitoring server messages to know when there were not enough
//   runes to continue.
//
// v1.4 23Dec2009
// - Added check for nature runes and stops script of none left.
//
// v1.3 27Oct2009
// - Changed loop() so that when randoms are disabled, the script doesn't
//   keep cycling the inventory tab.
//
// version 1.2
// - Change loop() to use state machine rather than while loops in a function
//   to accomplish alching.
// - Changed to use castSpell(), which also eliminates the need for dealing 
//   with game.openTab().
// - Adopted z's standard proggy.
//
// version 1.1
// - Now alchs everything in inventory except fire/nature runes and gold
// - Fixed spell indices
// - Disable allantiban and use internal antiban instead
// - Added option to enable allantiban
// - Changed to use iface.clickChild to activate spells
// - Changed to inventory.clickItem to select items being alched.
// - No longer requires items to be in specific inventory slots.
// - Requires starting with items (noted or otherwise) in inventory.

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
import org.rsbot.script.wrappers.RSPlayer;

@ScriptManifest(authors = {"Zachafer, zzSleepzz"},
category = "Magic",
name = "zzAutoAlcher",
version = 1.8,
description =
"<html>"
+ "<head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body>"
+ "<center>"
+ "<b><font size=+2 color=\"blue\"> zzAutoAlcher 1.8</font></b><br>"
+ "<font color='blue'><b>Authors:</b> Zachafer & zzSleepzz</font>"
+ "<p>"
+ "<b><i><font color='red'>The script disables randoms while running to prevent extra opening of the inventory.</font></i></b>"
+ "<p>"
+ "<table>"
+ "<tr valign=top>"
+ "<td align=right><b>Instructions:</b></td><td align=left> Start with noted items to be alched in your inventory.</td>"
+ "</tr>"
+ "<tr valign=top>"
+ "<td></td><td align=left><b><font size=+1 color=red>This script alchs everything in your inventory except fire/nature runes and gold!!!</font></td>"
+ "</tr>"
+ "</table>"
+ "<table><tr align=left>"
+ "<td align=right><b>Options:</b></td>"
+ "<td><input type=\"radio\" name=\"alch\" value=\"high\" checked=\"true\" align=right></td><td align=left> High alch</td>"
+ "</tr>"
+ "<tr align=left>"
+ "<td></td><td><input type=\"radio\" name=\"alch\" value=\"low\" align=left></td><td align=left> Low alch</td>"
+ "</tr>"
+ "</center>"
+ "</body>\n"
+ "</html>")
public class zzAutoAlcher extends Script implements PaintListener, MessageListener {

    ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);
    private boolean debug = false;
    private int spellid;
    private int item = 0;
    private static int NATURE_ID = 561;
    private static int FIRE_ID = 554;
    private static int GOLD_ID = 995;
    private long stateTime;

    enum STATE {

        CAST_SPELL, SEL_ITEM
    };
    STATE state;
    private boolean onStart = true;

    @Override
    public void onFinish() {
    }

    @Override
    public boolean onStart(Map<String, String> args) {
        state = STATE.CAST_SPELL;

        if (args.get("alch").equals("low")) { //they chose low alching
            spellid = SPELL_LOW_LEVEL_ALCHEMY;
        } else { //they chose high alching
            spellid = SPELL_HIGH_LEVEL_ALCHEMY;
        }

        item = 0;  // Initialize to zero so checkItems() will 
        // get called on first pass through loop()

        onStart = true;

        return true;
    }
    // Proggy originally based on ProFisher2's proggy
    // Modified and enhanced by zzSleepzz
    private long scriptStartTime;
    private int startXP;
    private int startLevel;
    private int lastExp;
    private int index;
    public int itemCount = 0;

    public void onRepaint(Graphics g) {
        Color PERCBAR = new Color(255, 255, 0, 150);

        long runTime = 0;
        long ss = 0, mm = 0, hh = 0;
        int expGained = 0;
        int levelsGained = 0;

        if (!game.isLoggedIn()) {
            return;
        }

        index = STAT_MAGIC;
        if (scriptStartTime == 0) {
            itemCount = 0;
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

        int y = 196;
        int x = 8;
        int boxwidthbase = 200;
        int boxwidth;

        g.setColor(new Color(255, 50, 50, 75));
        g.fillRoundRect(x, y, boxwidthbase, 132, 15, 15);

        // Calculate levels gained
        levelsGained = skills.getCurrentLvl(index) - startLevel;

        long runmins = mm + (hh * 60);
        Font f = g.getFont();  // Save for restoring after setting title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString(props.name() + " v" + props.version(), x += 6, y += 14);
        title = f.deriveFont(Font.ITALIC);
        g.setFont(title);
        g.drawString("by zzSleepzz", x, y += 12);

        g.setFont(f);
        g.setColor(Color.WHITE);
        g.drawString("Run time: " + hh + ":" + mm + ":" + ss, x += 5, y += 16);
        g.drawString("Items alched: " + itemCount, x, y += 12);
        g.drawString("Magic XP gained: " + expGained, x, y += 12);
        g.drawString("Magic Levels gained: " + levelsGained, x, y += 12);

        //Progress bar from Jacmob's scripts
        g.setColor(Color.RED);
        boxwidth = boxwidthbase;
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);

        g.setColor(Color.WHITE);
        g.drawString("XP to level " + (skills.getRealLvl(index) + 1) + ":  " + skills.getXPToNextLvl(index) + " (" + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        if (runmins > 0 && expGained > 0) {
            float lvlmins = skills.getXPToNextLvl(index) / (expGained / runmins);
            g.drawString("Next magic level in " + (long) lvlmins + " minutes", x, y += 14);
            g.drawString("Magic XP per hour: " + (expGained / runmins) * 60, x, y += 12);
        }
    }

    public void debug(String s) {
        if (debug) {
            log(s);
        }
    }

    public int loop() {
        while (!game.isLoggedIn() && onStart) {
            wait(100);
        }

        if (game.isLoggedIn() && onStart) {
            onStart = false;
        }

        zzAntiban();



        if (stateTime < System.currentTimeMillis()) {
            debug("State timeout, setting STATE.CAST_SPELL");
            state = STATE.CAST_SPELL;
            stateTime = System.currentTimeMillis() + random(3000, 3500);
        }

        if (state == STATE.CAST_SPELL) {
            debug("State=" + state);

            // Click the magic spell, switches to inventory, so wait for it
            magic.castSpell(spellid);
            int ct = 0;
            while (ct < 20 && game.getCurrentTab() != TAB_INVENTORY) {
                wait(100);
                ct++;
            }

            state = STATE.SEL_ITEM;
            stateTime = System.currentTimeMillis() + random(5000, 6500);
            return random(100, 150);
        } else if (state == STATE.SEL_ITEM) {
            debug("State=" + state);

            if (game.getCurrentTab() == TAB_MAGIC) {
                return random(50, 80);
            } else if (game.getCurrentTab() == TAB_INVENTORY) {
                debug("Selecting inventory item to alch");

                // This ensures we keep alching the same item
                // until there are no more.
                if (item == 0 || inventory.getCount(item) == 0) {
                    item = checkItems();
                    if (item == -1) {
                        stopScript(false);
                        return -1;
                    }
                }

                inventory.clickItem(item, "Cast");

                int ct = 0;
                while (ct < 20 && game.getCurrentTab() != TAB_MAGIC) {
                    wait(100);
                    ct++;
                }

                if (game.getCurrentTab() == TAB_INVENTORY) {
                    return 10;
                }

                itemCount++;
                state = STATE.CAST_SPELL;
                stateTime = System.currentTimeMillis() + random(4000, 5000);

                return random(500, 700);
            }
        }

        return random(10, 20);
    }

    // Returns -1 of no items left in inventory, otherwise
    // it returns an item ID
    public int checkItems() {
        if (inventory.getCountExcept(NATURE_ID, FIRE_ID, GOLD_ID) == 0) {
            return -1;
        }

        int[] items = inventory.getArray();

        for (int itm : items) {
            if (itm == -1) {
                continue;
            }

            if (itm != NATURE_ID && itm != FIRE_ID && itm != GOLD_ID) {
                return itm;
            }
        }

        return -1;
    }

    // This  support is based on WarXperiment's, but 
    // doesn't do as many things.  This will:
    //   - move the mouse to a nearby player to see name/level
    //  - move to and right click a nearby player to see the
    //    player there.
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
    };
    private Actions lastAction = Actions.EquipmentTab;
    private long nextAntiban = System.currentTimeMillis() + random(3000, 10000);

    private void zzAntiban() {

        int r = random(1, 85);

        // Can you say, "Don't ban me!"?
        long currTime = System.currentTimeMillis();
        if (nextAntiban > currTime) {
            return;
        } else {
            // Set to go off again in another 1.5-10 secs.
            nextAntiban = currTime + random(90000, 120000);
        }

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
                game.openTab(TAB_STATS);
                mouse.move(random(554, 603), random(387, 412));
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
        }


        wait(random(250, 350));
        mouse.move(mousePos, 40, 40);
        wait(random(300, 500));

    }

    public void messageReceived(MessageEvent e) {
        String m = e.getMessage();
        if (m.contains("do not have enough")) {
            debug("Script is stopped.  Insufficient runes available.");
            stopScript(false);
        }

    }
}
