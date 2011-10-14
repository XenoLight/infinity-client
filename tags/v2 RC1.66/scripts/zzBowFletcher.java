// zzBowFletcher by zzSleepzz
// Copyright 2009,2011 zzSleepzz 
//
// Proggy derived from pmiller624's pmCooker script
//
// Instructions: Start in any bank with a knife in the inventory and the logs to use
//               visible in the current bank tab.
//
// Features:
// v1.3
// - Updated to new client API
// - Fixed collection box closing
//
// v1.24-1.26
// - Updated how the fletching interface child is identified for clicking to 
//   initiate fletching.  Thanks to Henry for showing me that the interface component
//   to target has a component ID that is the same as the target unstrung bow ID!
// v1.23 (8Nov2010)
// - Added support to close the collection box that sometimes gets opened when
//   using a banker instead of a booth or chest.
// v1.22 (17Oct2010)
// - Updated for new Runedev script support
// - Update for new "Make all" in game
//
// v1.21 (12June20100
// - Adjusting the checkAction() so it doesn't hang when
//   lag causes less than the full inventory to be 
//   specified for Make-X.
// v1.2 (7June2010)
// - Fixed some behavior that caused script to get stuck.
// v1.1 (4June2010)
// - Updated for RuneDev
// v1.0 (5June2009)
// - Revised proggy
// - Fixed problem where it repeatedly tries to withdraw logs when
//   inventory is still full of fletched bows.
// - Adding support to check for random reward box on ground.  If so, drop some items
//   if no room and pick up the box.  The ImprovedRewardsBox script should take it from
//   there.
//
// v0.2 (25May2009)
// - Added message handling so it doesn't get stuck on leveling.
// - Fixed broken item count for proggy.
//
// v0.1 (12May2009)
//
//

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Antiban;
import org.rsbot.script.Bank;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.antiban.ImprovedRewardsBox;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSPlayer;

@ScriptManifest(authors = {"zzSleepzz"},
category = "Fletching",
name = "zzBowFletcher",
version = 1.4,
description =
"<html>"
+ "<head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body>"
+ "<center>"
+ "<b><font size=+2 color=\"blue\">zzBowFletcher v1.4</font></b>"
+ "<br><font color=\"blue\">by zzSleepzz</font>"
+ "<p>"
+ "<table>"
+ "<tr valign=top>"
+ "<td align=right><b>Instructions:</b></td><td align=left> Start in any bank with a knife in the inventory and the logs to fletch visible in the current bank tab.</td>"
+ "</tr></table>"
+ "<p>"
+ "<table>"
+ "<tr align=left><td align=right><b>Select Log:</b></td><td aligh=left><select name=\"logs\">"
+ "<option>Normal</option>"
+ "<option>Oak</option>"
+ "<option>Willow</option>"
+ "<option>Maple</option>"
+ "<option>Yew</option>"
+ "<option>Magic</option>"
+ "</select></td></tr>"
+ "<tr align=left><td align=right><b>Select Bow:</b></td><td aligh=left><select name=\"bows\">"
+ "<option>Short</option>"
+ "<option>Long</option>"
+ "</select></td></tr>"
+ "</table>"
+ "</center>"
+ "Be sure to set your Improved Rewards Box to Experience because this script will pick up the box and open it after a random occurs."
+ "</body>\n"
+ "</html>")
public class zzBowFletcher extends Script implements MessageListener, PaintListener {

    private final ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);
    // IDs for random rewards that might be dropped on ground.
    // Heck, we might get somebody elses!
    private final int lampID = 2528;
    private final int bookOfKnowledgeID = 11640;
    private final int mysteryBoxID = 6199;
    private final int rewardsBoxID = 14664;
    private final int[] rewards = {rewardsBoxID, mysteryBoxID, bookOfKnowledgeID, lampID};
    private int prevInvLogs = 0;
    private static final int FLETCH_IFACE_ID = 905;
    private int logID;
    private int bowID;
    private boolean badFletch = false;
    private int unwanted[];
    private static final int NORMAL_LOG = 1511;
    private static final int OAK_LOG = 1521;
    private static final int WILLOW_LOG = 1519;
    private static final int MAPLE_LOG = 1517;
    private static final int YEW_LOG = 1515;
    private static final int MAGIC_LOG = 1513;
    private static final int SHORT_UNSTR = 50;
    private static final int OAK_SHORT_UNSTR = 54;
    private static final int WILLOW_SHORT_UNSTR = 60;
    private static final int MAPLE_SHORT_UNSTR = 64;
    private static final int YEW_SHORT_UNSTR = 68;
    private static final int MAGIC_SHORT_UNSTR = 72;
    private static final int LONG_UNSTR = 48;
    private static final int OAK_LONG_UNSTR = 56;
    private static final int WILLOW_LONG_UNSTR = 58;
    private static final int MAPLE_LONG_UNSTR = 62;
    private static final int YEW_LONG_UNSTR = 66;
    private static final int MAGIC_LONG_UNSTR = 70;
    private static final int XBOW_STOCK = 9440;
    private static final int OAK_XBOW_STOCK = 9442;
    private static final int WILLOW_XBOW_STOCK = 9444;
    private static final int MAPLE_XBOW_STOCK = 9448;
    private static final int YEW_XBOW_STOCK = 9452;
    private int knifeID = 946, clayKnifeID = 14111;
    private int useKnifeID = 0;

    private enum State {

        S_BANK, S_FLETCH, S_FLETCHING
    };
    private State scriptState = null;

    protected int getMouseSpeed() {
        return random(5, 9);
    }

    public boolean onStart(Map<String, String> args) {

        // Establish the logs and bows to fletch
        String logArg = args.get("logs");
        String bowArg = args.get("bows");

        boolean longBow = bowArg.equals("Long");
        boolean shortBow = bowArg.equals("Short");

        // We'll set the fletching child interface selection for
        // longbow or shortbow here, for all but normal logs.
        // We'll modify the setting below if the selection was
        // for normal logs, since the child indices are different for them.

        // Setup the log ID and bow ID
        if (logArg.equals("Normal")) {
            logID = NORMAL_LOG;

            if (shortBow) {
                bowID = SHORT_UNSTR;
                unwanted = new int[]{XBOW_STOCK, LONG_UNSTR};
            } else if (longBow) {
                bowID = LONG_UNSTR;
                unwanted = new int[]{XBOW_STOCK, SHORT_UNSTR};
            }
        } else if (logArg.equals("Oak")) {
            logID = OAK_LOG;

            if (shortBow) {
                bowID = OAK_SHORT_UNSTR;
                unwanted = new int[]{OAK_XBOW_STOCK, OAK_LONG_UNSTR};
            } else if (longBow) {
                bowID = OAK_LONG_UNSTR;
                unwanted = new int[]{OAK_XBOW_STOCK, OAK_SHORT_UNSTR};
            }
        } else if (logArg.equals("Willow")) {
            logID = WILLOW_LOG;

            if (shortBow) {
                bowID = WILLOW_SHORT_UNSTR;
                unwanted = new int[]{WILLOW_XBOW_STOCK, WILLOW_LONG_UNSTR};
            } else if (longBow) {
                bowID = WILLOW_LONG_UNSTR;
                unwanted = new int[]{WILLOW_XBOW_STOCK, WILLOW_SHORT_UNSTR};
            }
        } else if (logArg.equals("Maple")) {
            logID = MAPLE_LOG;

            if (shortBow) {
                bowID = MAPLE_SHORT_UNSTR;
                unwanted = new int[]{MAPLE_XBOW_STOCK, MAPLE_LONG_UNSTR};
            } else if (longBow) {
                bowID = MAPLE_LONG_UNSTR;
                unwanted = new int[]{MAPLE_XBOW_STOCK, MAPLE_SHORT_UNSTR};
            }
        } else if (logArg.equals("Yew")) {
            logID = YEW_LOG;

            if (shortBow) {
                bowID = YEW_SHORT_UNSTR;
                unwanted = new int[]{YEW_XBOW_STOCK, YEW_LONG_UNSTR};
            } else if (longBow) {
                bowID = YEW_LONG_UNSTR;
                unwanted = new int[]{YEW_XBOW_STOCK, YEW_SHORT_UNSTR};
            }
        } else if (logArg.equals("Magic")) {
            logID = MAGIC_LOG;

            if (shortBow) {
                bowID = MAGIC_SHORT_UNSTR;
                unwanted = new int[]{MAGIC_LONG_UNSTR};
            } else if (longBow) {
                bowID = MAGIC_LONG_UNSTR;
                unwanted = new int[]{MAGIC_SHORT_UNSTR};
            }
        }

        scriptState = null;
        prevInvLogs = 0;
        useKnifeID = 0;

        return true;
    }

    public void onFinish() {
        log("--------> zzBowFletcher <--------");
        log(String.format("  Elapsed time: %s", 
        	String.format("%s:%s:%s",  hh, mm, ss)));
        log(String.format("  Levels gained: %d", levelsGained));
        log(String.format("  XP gained:     %d", expGained));
        log(String.format("  XP rate:    %d/hr", (expGained / runmins) * 60));
        log(String.format("  Items fletched: %d", count));
    }
    
    private long time = 0;  // Used to mark time since last player
    // animation, to identify when fletching is done.

    public int loop() {

        try {
            // Close the collection box if it's open.
            if (bank.isCollectionOpen()) {
                iface.clickChild(Bank.INTERFACE_COLLECTION_BOX, 
                		         Bank.INTERFACE_COLLECTION_BOX_CLOSE);
                return random(300, 450);
            }

            if (!inventory.contains(knifeID)) {
                stopScript(false);
            }

            RSGroundItem reward = ground.getItemByID(rewards);
            if (reward != null || inventory.contains(rewards)) {
                if (inventory.contains(rewards)) {
                    Antiban irb = new ImprovedRewardsBox();
                    irb.runAntiban();

                    return random(80, 100);
                }

                // If inventory full and box not in inventory, make room
                // and grab it.
                if (inventory.isFull()) {
                    if (inventory.contains(bowID)) {
                        inventory.clickItem(bowID, "Drop");
                        wait(random(300, 500));
                    } else if (inventory.contains(logID)) {
                        inventory.clickItem(logID, "Drop");
                        wait(random(300, 500));
                    }
                }

                // At this point, we should have an open inventory
                // slot.  If not, return and we'll get it next time
                // through the loop method.
                if (inventory.isFull()) {
                    return random(20, 30);
                }

                reward = ground.getItemByID(rewards);
                if (reward != null) {
                    if (reward.isOnScreen()) {
                        reward.action("Take Random");
                        inventory.waitForCount(reward.getItem().getID(), 1, 3000);
                    } else {
                        walk.tileMM(reward.getLocation());
                        wait(random(1500, 2000));
                        return random(50, 80);
                    }
                }

                return random(500, 750);
            }


            if (!player.isIdle() && !badFletch) {
                return random(50, 100);
            }

            // This will initialize the script state when the
            // script first starts.
            if (scriptState == null) {
                // Determine knife to use
                if (inventory.contains(knifeID)) {
                    useKnifeID = knifeID;
                } else if (inventory.contains(clayKnifeID)) {
                    useKnifeID = clayKnifeID;
                } else {
                    log("There is no usable fletching knife in your inventory.  Script is stopped.");
                    return -1;
                }

                debug("Determining starting state.");
                if (inventory.contains(logID)) {
                    scriptState = State.S_FLETCH;
                } else {
                    scriptState = State.S_BANK;
                }
            } else if ((scriptState == State.S_FLETCH || scriptState == State.S_FLETCHING)
                    && !inventory.contains(logID)) {
                scriptState = State.S_BANK;
            }

            // Can you say, "Don't ban me!"?
            zzAntiban();

            debug("scriptState=" + scriptState.toString());
            if (scriptState == State.S_BANK) {
                if (bank()) {
                    debug("Finished banking");

                    if (inventory.getCount(logID) > 0) {
                        scriptState = State.S_FLETCH;
                    }
                }
            } else if (scriptState == State.S_FLETCH) {
                if (fletch()) {
                    debug("Finished starting fletch");
                    scriptState = State.S_FLETCHING;
                }
            } else if (scriptState == State.S_FLETCHING) {
                if (inventory.contains(unwanted) && !badFletch) {
                    badFletch = true;
                    scriptState = State.S_FLETCH;
                    return 10;
                }


                checkAction();
            }

            return random(300, 400);
        } catch (Exception e) {
            e.printStackTrace();
            return random(50, 100);
        }
    }

    public void messageReceived(MessageEvent e) { //Searches for Messages in chatbox
        String msg = e.getMessage();

        if (msg.contains("carefully cut the")) {
            count++;
        } else if (msg.contains("just advanced a Fletching level")) {
            scriptState = State.S_FLETCH;
        } else if (msg.contains("run out")) {
            scriptState = State.S_BANK;
        }

    }

    public void checkAction() {
        int logct = 0;

        if (bank.isOpen()) {
            bank.close();
            wait(random(650, 800));
            return;
        }

        if (iface.get(FLETCH_IFACE_ID).isValid() && inventory.contains(logID)) {
            scriptState = State.S_FLETCH;
            return;
        }

        if (inventory.contains()) {
            debug("prev logs=" + prevInvLogs + ", inv ct=" + inventory.getCount(logID));
        }

        if (prevInvLogs != inventory.getCount(logID)) {
            // If animation, mark the time.  This will be
            // used to check how long we may not have been
            // chopping.
            time = System.currentTimeMillis() + random(3400, 3600);
            prevInvLogs = inventory.getCount(logID);
        } else {
            // We think we may no longer be fletching, so we'll
            // just carry on until several seconds have passed.  If still
            // no action, then change the script state.
            if (time < System.currentTimeMillis()) {

                debug("Suspected idle time expired.");
                // If we get here, it's been 3 seconds and we've
                // missed any animation.  If the steel bars in inventory
                // also hasn't changed in past 3 seconds, we must have
                // stopped, so set state to smelt.
                //
                // If no steel bars left, then go to bank, otherwise
                // just carry on as is.
                logct = inventory.getCount(logID);
                if (logct > 0) {
                    debug("Have logs and amount unchanged...fletch");
                    scriptState = State.S_FLETCH;
                } else if (logct == 0) {
                    debug("No logs left, banking.");
                    scriptState = State.S_BANK;
                }
            }
        }
    }

    /**
     *
     * @return false when there are no more steel bars in the bank
     *         true if it thinks steel bars were withdrawn, even if it failed.
     */
    public boolean bank() {
        // Select bank chest and open the bank interface

        // The bank interface could already be open if
        // we were already here and exited, so check for
        // it first.
        bank.open();

        // Proceed only if the interface is in fact open
        if (bank.isOpen()) {
            if (inventory.getCount() > 1) {
                if (!bank.depositAllExcept(knifeID, clayKnifeID))  {
                	return false;
                }
            }

            if (inventory.getCount(logID) > 0) {
                return true;
            }

            if (bank.getCount(logID) > 0) {
                int ct = inventory.getCount(logID);	
                bank.withdraw(logID, 0);
               
                ct = inventory.waitForCountGreater(logID, ct, random(2000,3000));
                
                if (ct == 0) {
                    return false;
                }
            } else {
                log("All available logs have been fletched.");
                stopScript(false);
            }
        }

        if (bank.isOpen()) {
            bank.close();
        }

        return true;
    }

    public boolean fletch() {
        if (bank.isOpen()) {
            bank.close();
            wait(random(650, 800));
            return false;
        }

        if (!iface.get(FLETCH_IFACE_ID).isValid() && inventory.contains(logID)) {
            int ct = 0;
            while (ct++ < 10 && !inventory.isItemSelected()) {
                debug("Selecting knife.");
                inventory.clickItem(useKnifeID, "Use ");
                wait(random(200, 300));
            }

            if (inventory.isItemSelected()) {
                debug("Selecting log.");
                inventory.clickItem(logID, "Use ");
                iface.waitForOpen(iface.get(FLETCH_IFACE_ID), 1500);
            }

            // At this point we should have the fletching interface open.
            if (!iface.get(FLETCH_IFACE_ID).isValid()) {
                return false;
            }
        }

        // This seems silly, but randoms could find us at this point with
        // the interface actually closed.
        if (iface.get(FLETCH_IFACE_ID).isValid()) {
            // The child interfaces for the fletching objects should always be
            // 14, 15, or 16.  To make this simpler, we'll check the children on
            // either side of the expected child.  We'll select whichever one's
            // text contains the bow we are trying to fletch.  This should avoid
            // the apparent misclicks in selecting the wrong bow.
            int ct = 0;
            boolean fl = false;

            RSInterfaceChild rica[] = iface.get(FLETCH_IFACE_ID).getChildren();
            RSInterfaceChild ric = null;

            for (RSInterfaceChild rch : rica) {
                for (RSInterfaceChild rchc : rch.getChildren()) {

                    debug("Interface(" + FLETCH_IFACE_ID + "), Child(" + rch.getIndex() + "), comp(" + rchc.getIndex() + "), compID(" + rchc.getChildID() + ")");

                    // Credit to Henry (from Runedev) for the idea that the
                    // component ID of the desired interface child's target
                    // component will be the actual unstrung bow's ID.
                    if (rchc.getChildID() == bowID) {
                        debug("Found the component.");
                        ric = rch;
                        break;
                    }
                }

                if (ric != null) {
                    break;
                }
            }

            do {
                fl = iface.clickChild(ric);
            } while (ct++ < 3 && !fl);

            if (player.waitForAnim(2000) != -1) {
                return false;
            } else {
                badFletch = false;

                return true;
            }
        }

        return false;
    }
    private long scriptStartTime = 0;
    private int startXP = 0, lastXP = 0;
    private int startLevel = 0;
    private int index = STAT_FLETCHING;
    private int count = 0;

    int expGained = 0;
    int levelsGained = 0;
    long runTime = 0;
    long ss = 0, mm = 0, hh = 0;
    long runmins=0;
    
    public void onRepaint(Graphics g) {
        Color PERCBAR = new Color(255, 255, 0, 150);

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

        int x = 8;
        int y = 210;

        int boxwidth = 180;
        g.setColor(new Color(255, 50, 50, 75));
        g.fillRoundRect(x, y, boxwidth, 126, 15, 15);
        g.setColor(Color.RED);
        g.drawRoundRect(x, y, boxwidth, 126, 15, 15);
        g.setColor(Color.GREEN);
        g.drawRoundRect(x - 1, y - 1, boxwidth + 2, 128, 15, 15);
        g.setColor(Color.BLUE);
        g.drawRoundRect(x - 2, y - 2, boxwidth + 4, 130, 15, 15);
        g.drawRoundRect(x - 3, y - 3, boxwidth + 6, 132, 15, 15);

        x += 4;
        y += 4;
        runmins = mm + (hh * 60);
        Font f = g.getFont();  // Save for restoring after setting title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString(props.name() + "  v" + props.version(), x, y += 12);

        title = f.deriveFont(Font.ITALIC);
        g.setFont(title);
        x += 5;
        g.drawString("by zzSleepzz", x, y += 12);

        x -= 5;
        g.setFont(f);
        g.setColor(Color.WHITE);
        g.drawString("Run time:  " + hh + ":" + mm + ":" + ss, x, y += 16);
        g.drawString("Bow count:  " + count, x, y += 12);
        g.drawString("XP gained: " + expGained, x, y += 12);
        g.drawString("Levels gained: " + levelsGained, x, y += 12);

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
            g.drawString("Level " + (skills.getCurrentLvl(index) + 1) + " in " + (long) lvlmins + " minutes", x, y += 14);
            g.drawString("XP per hour : " + (expGained / runmins) * 60, x, y += 12);
        }
    }

    // This antiban support is based on WarXperiment's, but
    // doesn't do as many things. This will:
    // - move the mouse to a nearby player to see name/level
    // - move to and right click a nearby player to see the
    // players there.
    // - Move the mouse to select the skills tab and hover over
    // script's primary skill for a short while.
    // - Move the mouse to the friends list to see who's online
    // - Tab actions will restore the inventory tab when done.
    // - Mouse actions will move the mouse back to a slightly
    // different location from where it originally was.
    // - Won't duplicate the prior action.
    private enum Actions {

        HoverPlayer, ClickPlayer, SkillsTab, FriendsTab, InventoryTab, EquipmentTab
    };
    private Actions lastAction = Actions.EquipmentTab;
    private long nextAntiban = System.currentTimeMillis() + random(3000, 10000);

    private void zzAntiban() {
        // Can you say, "Don't ban me!"?
        long currTime = System.currentTimeMillis();
        if (nextAntiban > currTime) {
            return;
        } else {
            // Set to go off again in another 1.5-10 secs.
            nextAntiban = currTime + random(15000, 35000);
        }

        int r = random(1, 999);

        if (r < 20) { // SkillsTab
            if (lastAction == Actions.SkillsTab) {
                zzAntiban(); // retry for diff action.
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

                int r2 = random(0, 999);

                if (r2 < 20) {
                    // Random skill
                    mouse.move(random(572, 245), random(694, 436));
                } else {
                    if (r2 < 500) {
                        // Check strength, why not?
                        mouse.move(random(559, 265), random(694, 282));
                        wait(random(1900, 2300));
                    }

                    // Yeah, and fishing too!  What?  Not RC?
                    mouse.move(random(668, 296), random(708, 318));
                    wait(random(1700, 2200));
                }


                wait(random(1000, 2000));
            }
        } else if (r < 40) { // FriendsTab
            if (lastAction == Actions.FriendsTab) {
                zzAntiban(); // retry for diff action.
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
        } else if (r < 50) { // EquipmentTab
            if (lastAction == Actions.EquipmentTab) {
                zzAntiban(); // retry for diff action.
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
        } else if (r < 500) {
            mouse.moveRandomly(1000);
        } else if (r < 900) {
            mouse.moveSlightly();
        }

        if (random(0, 99) < 50) {
            wait(random(800, 950));
            if (game.getCurrentTab() != TAB_INVENTORY) {
                game.openTab(TAB_INVENTORY);
            }
        }

        wait(random(300, 500));
    }
}
