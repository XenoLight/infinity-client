// zzBowStringerSupreme by zzSleepzz
// Copyright 2009,2011 zzSleepzz 
//
// Proggy derived from pmiller624's pmCooker script
//
// Instructions: Start in any bank and let it rip.
//
// Features:
// v1.2 (27Jun2011)
// - Update to new API
// v1.1 (17Oct2010)
// - Update for new "Make all" in game
//
// v1.0 (16June2010)
// - Recovered lost script from old source and class decompile
//
// v0.2 (1June2009)
// - Fixed proggy
//
// v0.1 (13May2009)
//

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSPlayer;

@ScriptManifest(authors = {"zzSleepzz"},
category = "Fletching",
name = "zzBowStringer by zzSleepzz",
version = 1.2,
description =
"<html><head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body><center><b><font size=+2 color=\"blue\">zzBowStringer v1.2</font></b>"
+ "<br><font color=\"blue\">by zzSleepzz</font></center>"
+ "<table><tr valign=top>"
+ "<td align=right><b>Instructions:</b></td>"
+ "<td align=left> Start in any bank.  Unstrung bows and bow strings must be visible on the "
+ "current bank tab.</td></tr>"
+ "<p>"
+ "<tr><td>Select bows to string:</td>"
+ "<td><select name=\"bow\"><option>Shortbow (u)<option>Longbow (u)<option>Oak shortbow (u)<option>Oak longbow (u)<option>Willow shortbow (u)<option>Willow longbow (u)<option>Maple shortbow (u)<option>Maple longbow (u)<option>Yew shortbow (u)<option>Yew longbow (u)<option>Magic shortbow (u)<option>Magic longbow (u)"
+ "</select></td></tr></table><p><p></center></body></html>")
public class zzBowStringer extends Script
        implements MessageListener, PaintListener {

    ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);

    private static enum State {

        S_STRING, S_STRINGING, S_BANKING;
    }
    private static final String shortbow = "Shortbow (u)";
    private static final String longbow = "Longbow (u)";
    private static final String oaklong = "Oak longbow (u)";
    private static final String oakshort = "Oak shortbow (u)";
    private static final String willowlong = "Willow longbow (u)";
    private static final String willowshort = "Willow shortbow (u)";
    private static final String maplelong = "Maple longbow (u)";
    private static final String mapleshort = "Maple shortbow (u)";
    private static final String yewlong = "Yew longbow (u)";
    private static final String yewshort = "Yew shortbow (u)";
    private static final String magiclong = "Magic longbow (u)";
    private static final String magicshort = "Magic shortbow (u)";
    private static final int shortbowID = 50;
    private static final int longbowID = 48;
    private static final int oaklongID = 56;
    private static final int oakshortID = 54;
    private static final int willowlongID = 58;
    private static final int willowshortID = 60;
    private static final int maplelongID = 62;
    private static final int mapleshortID = 64;
    private static final int yewlongID = 66;
    private static final int yewshortID = 68;
    private static final int magiclongID = 70;
    private static final int magicshortID = 72;
    private static final int FLETCHING_INTERFACE_ID = 905;
    private static final int FLETCHING_INTERFACE_CHILD_ID = 14;
    private RSInterfaceChild FletchIntfc;
    private int prevInvStrings;
    private int bowToString;
    private static final int STRING_ID = 1777;
    private State scriptState;
    private long timer;

    public zzBowStringer() {
        prevInvStrings = 0;

        scriptState = null;

        timer = 0;

        itemCount = 0;
    }

    public boolean onStart(Map<String, String> paramMap) {
        String str = (String) paramMap.get("bow");

        if (str.equals(shortbow)) {
            bowToString = shortbowID;
        } else if (str.equals(longbow)) {
            bowToString = longbowID;
        } else if (str.equals(oakshort)) {
            bowToString = oakshortID;
        } else if (str.equals(oaklong)) {
            bowToString = oaklongID;
        } else if (str.equals(willowshort)) {
            bowToString = willowshortID;
        } else if (str.equals(willowlong)) {
            bowToString = willowlongID;
        } else if (str.equals(mapleshort)) {
            bowToString = mapleshortID;
        } else if (str.equals(maplelong)) {
            bowToString = maplelongID;
        } else if (str.equals(yewshort)) {
            bowToString = yewshortID;
        } else if (str.equals(yewlong)) {
            bowToString = yewlongID;
        } else if (str.equals(magicshort)) {
            bowToString = magicshortID;
        } else if (str.equals(magiclong)) {
            bowToString = magiclongID;
        }

        scriptState = null;
        prevInvStrings = 0;

        FletchIntfc = iface.getChild(FLETCHING_INTERFACE_ID, FLETCHING_INTERFACE_CHILD_ID);

        return true;
    }

    public void onFinish() {
    }

    public int loop() {
        try {
            if (!player.isIdle()) {
                return random(50, 100);
            }

            game.setRun(true);

            // This will initialize the script state when the
            // script first starts.
            if (scriptState == null) {
                debug("Determining starting state.");
                if (inventory.contains(STRING_ID) && inventory.contains(bowToString)) {
                    scriptState = State.S_STRING;
                } else {
                    scriptState = State.S_BANKING;
                }
            } else if ((scriptState == State.S_STRING) || (scriptState == State.S_STRINGING)) {
                if (!inventory.contains(STRING_ID) || !inventory.contains(bowToString)) {
                    scriptState = State.S_BANKING;
                }
            } else if (scriptState == State.S_BANKING) {
                if (inventory.contains(STRING_ID) && inventory.contains(bowToString)) {
                    scriptState = State.S_STRING;
                }
            }



            zzAntiban();

            debug("scriptState=" + scriptState);
            if (scriptState == State.S_BANKING) {
                if (bank()) {
                    debug("Finished banking");

                    if (inventory.getCount(STRING_ID) > 0 && inventory.getCount(bowToString) > 0) {
                        scriptState = State.S_STRING;
                    }
                }
            } else if (scriptState == State.S_STRING) {
                debug("Attempting to string");

                if (string()) {
                    debug("Finished starting string");
                    scriptState = State.S_STRINGING;
                } else {
                    debug("sting() returned false.");
                }

                return random(200, 300);
            } else if (scriptState == State.S_STRINGING) {
                checkAction();
            }

            return random(300, 400);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return random(50, 100);
    }

    public void messageReceived(MessageEvent paramServerMessageEvent) {
        String str = paramServerMessageEvent.getMessage();

        if (str.contains("add a string")) {
            itemCount += 1;
        }
    }

    public void checkAction() {
        int strings = 0;

        if (iface.get(FLETCHING_INTERFACE_ID).isValid()) {
            scriptState = State.S_STRING;
            return;
        }

        if (prevInvStrings != inventory.getCount(STRING_ID) || !player.isIdle()) {
            // If animation, mark the time.  This will be
            // used to check how long we may not have been
            // chopping.
            timer = (System.currentTimeMillis() + 3500L);
            prevInvStrings = inventory.getCount(STRING_ID);
        } // We think we may no longer be smelting, so we'll
        // just carry on until several seconds have passed.  If still
        // no action, then change the script state.
        else if (timer < System.currentTimeMillis()) {
            debug("Suspected idle time expired.");
            // If we get here, it's been 3 seconds and we've
            // missed any animation.  If the items in inventory
            // also hasn't changed in past 3 seconds, we must have
            // stopped, so set state to do work again.
            //
            // If no items left, then go to bank, otherwise
            // just carry on as is.
            strings = inventory.getCount(STRING_ID);
            if (strings > 0) {
                debug("Have raw items and amount unchanged...start work again.");
                scriptState = State.S_STRING;
            } else if (strings == 0) {
                debug("No strings left, banking.");
                scriptState = State.S_BANKING;
            }
        }
    }

    /**
     *
     * @return false when there are no more raw stock items in the bank
     *         true if it thinks raw stock items were withdrawn, even if it failed.
     */
    public boolean bank() // Select bank chest and open the bank interface
    {
        try {
            // The bank interface could already be open if
            // we were already here and exited, so check for
            // it first.
            if (!bank.isOpen()) {
                bank.open();
                iface.waitForOpen(bank.getInterface(), 1400);
                wait(random(400, 600));
                return false;
            }

            if (!(bank.depositAllExcept(STRING_ID, bowToString))) {
                return false;
            }
            wait(random(600, 700));

            if (bank.getCount(STRING_ID) == 0 || bank.getCount(bowToString) == 0) {
                log("The supply of bow strings or unstrung bows is finished.");
                stopScript(false);
            }

            // Get some strings
            int invct = inventory.getCount(STRING_ID);
            if (bank.getCount(STRING_ID) > 0 && invct < 14) {
                if (!(bank.withdraw(STRING_ID, 14 - invct))) {
                    return false;
                }

                inventory.waitForCount(STRING_ID, invct + 1, random(750, 850));
            }

            if (!(inventory.contains(STRING_ID))) {
                bank.depositAll();
                return false;
            }

            invct = inventory.getCount(bowToString);
            if (bank.getCount(bowToString) > 0 && invct < 14) {
                if (!(bank.withdraw(bowToString, 14 - invct))) {
                    return false;
                }
                inventory.waitForCount(bowToString, invct + 1, random(750, 850));
            }

            if (!(inventory.contains(bowToString))) {
                invct = inventory.getCount(STRING_ID);

                if (invct <= 14) {
                    bank.depositAllExcept(STRING_ID);
                } else {
                    bank.deposit(STRING_ID, invct - 14);
                }
                return false;
            }

            if (inventory.getCount(false) - inventory.getCount(bowToString, STRING_ID) > 0) {
                bank.depositAll();
                return false;
            }

            bank.close();
        } // We don't really need the strung bow IDs in this script.  We still
        // need to check if we have anything but the unstrung bows and strings
        // in the inventory though.  If so, something isn't right, so return
        // false.
        catch (Exception localException) {
            return false;
        }

        return true;
    }

    public boolean string() {
        if (bank.isOpen()) {
            bank.close();
            wait(random(650, 800));
            return false;
        }

        if (inventory.getCount(STRING_ID) == 0 || inventory.getCount(bowToString) == 0) {
            return false;
        }

        if (!FletchIntfc.isValid()) {
            int i = random(1, 100);
            if (i < 50) {
                inventory.clickItem(STRING_ID, "Use");
                wait(random(250, 350));

                if (!(inventory.isItemSelected())) {
                    return false;
                }

                wait(random(250, 350));
                if (inventory.clickItem(bowToString, "Use")) {
                    wait(random(900, 1000));
                }
            } else {
                inventory.clickItem(bowToString, "Use");
                wait(random(250, 350));

                if (!(inventory.isItemSelected())) {
                    return false;
                }

                wait(random(250, 350));
                if (inventory.clickItem(STRING_ID, "Use")) {
                    wait(random(900, 1000));
                }
            }
            // At this point we should have the smelting interface open.
            if (!iface.waitForOpen(iface.get(FLETCHING_INTERFACE_ID), 2500)) {
                return false;
            }
        } // This seems silly, but randoms could find us at this point with
        // the interface actually closed.
        else {
            if (iface.clickChild(FletchIntfc)) {
                if (player.waitForAnim(2500) == -1) {
                    return false;
                }
            }

            wait(random(400, 500));
            return true;
        }

        return false;
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
        if (!(game.isLoggedIn())) {
            return;
        }
        index = Skills.FLETCHING;
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
        Font f = g.getFont();  // Save for restoring after settings title
        Font title = f.deriveFont(Font.BOLD & Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString(props.name() + " v" + props.version(), x += 6, y += 14);

        g.setFont(f);
        g.setColor(Color.WHITE);
        g.drawString("Run time: " + hh + ":" + mm + ":" + ss, x += 3, y += 16);
        g.drawString("Bows strung: " + itemCount, x, y += 12);
        g.drawString("XP gained: " + expGained, x, y += 12);
        g.drawString("Levels gained: " + levelsGained, x, y += 12);

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
            g.drawString("Next level in " + (long) lvlmins + " minutes", x, y += 14);
            g.drawString("XP per hour: " + (expGained / runmins) * 60, x, y += 12);
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

            if (game.getCurrentTab() != Game.tabStats) {
                game.openTab(Game.tabStats);

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
            if (game.getCurrentTab() != Game.tabInventory) {
                game.openTab(Game.tabInventory);
            }
        }

        wait(random(300, 500));
    }
}
