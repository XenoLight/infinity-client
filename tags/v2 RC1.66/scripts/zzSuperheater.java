// zzSuperheater by zzSleepzz
// Copyright 2009,2011 zzSleepzz
//
// Features:
// Version 2.9
// - Updated to new script API
//
// Version 2.8
// - Handle scenario where spell has been cast causing bank unable to be opened.
// - Fixed banking to ensure correct ore withdrawal for misclicks.
//
// Version 2.7 (9Jan2010)
// - Minor cleanup
// - Adjusted to correct overwithdrawal of primary ore
//
// Version 2.6 (8Jan2010)
// - Automatically disables randoms and restores the original state
//   when the script is stopped.
//
// Version 2.5  (4Jan2010)
// - Trying to fix the problem where the spell was cast, but the ore
//   wasn't clicked and the script is trying to bank unsuccessfully.
// - Tried to fix the conditions where the script would deposit unsmelted
//   ores unnecessarily.
//
// Version 2.4  (4Jan2010)
// - Revised banking support.
//
// Version 2.31 (Dec2009)
// - Tweeked antiban to display magic stat 20% of time AND THEN do
//   smithing, instead of either or.
//
// Version 2.3 (12Nov2009)
// - Correcting banking issues resulting from not waiting long
//   enough and from misclicks.
// - Corrected timing issues with smelting ores like adamantite.
// - Added support to not deposit fire runes in case the player is not
//   using a fire staff.
//
// Version 2.2 (10Nov2009)
// - Since randoms have to be disabled, record the current
//   location.  If we find ourselves more than 20 away,
//   just stop the script.
// - Improved paint.
//
// Version 2.1 (30Oct2009)
// - Fixed some more inconsistencies in the script actions
//   most notably with banking.
//
// Version 2.0 (29Oct2009)
// - Updated to work with new inventory behavior.  Avoids
//   unnecessary cycling into inventory after each cast,
//   BUT REQUIRES RANDOMS TO BE DISABLED!!!
//   Aren't I slick?  ;-P
//
// Version 1.0
// - Ready for prime time
//
// Version 0.03
// - Tried to improve accuracy of inventory handling.
//
// Version 0.02
// - Closes collection box if opened accidentally
// - Updated antiban to not return to previous cursor position.
//
// Version 0.01
// - Built in antiban
// - Superheats every kind of ore
//   needed runes/equipment are available
// - Proggy
//

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.lazygamerz.scripting.api.Magic;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSPlayer;

@ScriptManifest(authors = {"zzSleepzz"},
category = "Smithing",
name = "zzSuperheater",
version = 2.9,
description =
"<html>"
+ "<head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body>"
+ "<center>"
+ "<b><font size=+2 color=\"blue\"> zzSuperheater v2.9<br>"
+ "by zzSleepzz</font></b><br>"
+ "<font size=-2>Based on the AutoAlcher script created by Zachafer</font>"
+ "<p>"
+ "<table>"
+ "<tr valign=top>"
+ "<td align=right><b>Instructions:</b></td><td align=left> Start with fire or lava staff weilded and nature runes in inventory.  "
+ "All required ores must be visible in the current bank tab.</td>"
+ "</tr>"
+ "</table>"
+ "<table><tr align=left>"
+ "<br><td align=right><b>Select bar to create:</b></td>"
+ "<td><select name=\"bar\">"
+ "<option value=\"Runite\">Runite</option>"
+ "<option value=\"Adamantite\">Adamantite</option>"
+ "<option value=\"Mithril\">Mithril</option>"
+ "<option value=\"Gold\">Gold</option>"
+ "<option value=\"Silver\">Silver</option>"
+ "<option value=\"Steel\">Steel</option>"
+ "<option value=\"Iron\">Iron</option>"
+ "<option value=\"Bronze\">Bronze</option>"
+ "</select></td>"
+ "</tr>"
+ "<p>"
+ "</center>"
+ "</body>\n"
+ "</html>")
public class zzSuperheater extends Script implements PaintListener {

    ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);

    private static final int coalOreID = 453;
    private static final int runeOreID = 451;
    private static final int addyOreID = 449;
    private static final int mithOreID = 447;
    private static final int ironOreID = 440;
    private static final int goldOreID = 444;
    private static final int silverOreID = 442;
    private static final int copperOreID = 436;
    private static final int tinOreID = 438;
    private static final int runeBarID = 2363;
    private static final int addyBarID = 2361;
    private static final int mithBarID = 2359;
    private static final int goldBarID = 2357;
    private static final int silverBarID = 2355;
    private static final int steelBarID = 2353;
    private static final int ironBarID = 2351;
    private static final int bronzeBarID = 2349;
    private static final int natureID = 561;
    private static final int fireID = 554;
    private boolean needToBank = true;
    private long startTime;
    private int startSmithLevel;
    private int startMagicLevel;
    private int startSmithXP;
    private int startMagicXP;
    private int bar;
    private int ore;
    private int ore2;
    private int ore1ct;
    private int ore2ct;
    private int ore2fact;
    private long Time;
    private boolean onStart = true;
    private static final Color statusBoxBG = new Color(162, 80, 45, 100);
    private static final Color statusBoxFG = Color.WHITE;

    private int superheatSafetyCt=0;
    public int loop() {
        try {
            while (!game.isLoggedIn() && onStart) {
                wait(100);
            }

            // Antiban will determine intervals of when to run and will
            // just return if pauseAntiban is set.
            zzAntiban();

            if (needToBank) {
                debug("Need to bank");

               if (!bank()) {
            	   return random(150,250);
               }

                if (bank.isOpen()) {
                    if (inventory.getCount(ore) > 0 && inventory.getCount(ore2) >= ore2fact) {
                        bank.close();
                        needToBank = false;
                    }
                    return random(300, 400);
                }
            }

            if (bank.isOpen()) {
                bank.close();
                wait(random(350, 450));
            }

            debug("Superheating");
            if (!superheat()) {
            	if (superheatSafetyCt++>6)  {
            		log("Superheat failed.  Stopping script.");
            		return -1;
            	}
            } else {
            	superheatSafetyCt=0;
                return random(20, 30);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }

    // This should only return false if the bank faild to open.
    public boolean bank() {
        // Check if the superheat spell is selected and if so, click the mouse so the
        // spell is dismissed and the bank can be opened.
        if (getMenuActions()[0].contains("Superheat Item")) {
            mouse.click(true);
            wait(random(200, 350));
        }

        debug("Opening the bank.");
        if (!bank.open())  {
            if (getMenuActions()[0].contains("Walk")) {
                mouse.click(true);
            }

            return false;
        }
        else  {
        	debug("Bank was successfully opened.");
        }

        int invct = inventory.getCount(natureID, fireID, ore, ore2);
        
        if (inventory.getCount()>invct)  {
            debug("Depositing...");

            if (bank.depositAllExcept(natureID, fireID, ore, ore2))  {
            	inventory.waitForCountLess(invct+1, 2000);
            }
        }

    	invct = inventory.getCount(ore);
        if (invct > ore1ct)  {
        	debug("Depositing excess ore1...");

        	if (bank.deposit(ore, 0))  {
        		inventory.waitForCount(ore, 0, 2000);
        	}
        }

        if (bank.getCount(ore) == 0 || bank.getCount(ore2) < ore2fact) {
            log("Insufficient ore to continue");
            log("Ore 1 count=" + bank.getCount(ore) + ", Ore 2 count=" + bank.getCount(ore2));
            return false;
        }

        if (ore2ct > 0) {
            int x = inventory.getCount(ore2);
            if (x + inventory.getCountExcept(ore2) == 28) {
				debug("Inventory is full of ore2, making room for ore1...");
				
				invct = inventory.getCount();
				
				if (bank.deposit(ore2, ore1ct))  {
					inventory.waitForCountLess(invct, 2000);
				}
				else  {
					return false;
				}
            }
        }


        invct = inventory.getCount();
        if (invct == 28 || inventory.getCount(ore) > ore1ct) {
            if (bank.depositAllExcept(fireID, natureID))  {
            	inventory.waitForCountLess(invct, 2000);
            }
            else  {
            	return false;
            }
        }

        invct = inventory.getCount(ore);
        if (invct < ore1ct) {
        	debug("Withdrawing ore1...");
            if (bank.withdraw(ore, ore1ct - inventory.getCount(ore)))  {
            	inventory.waitForCountGreater(ore,invct,2000);
            }
            else  {
            	return false;
            }
        }

        // Handle misclicks on withdrawing
        invct = inventory.getCount(ore);
        if (invct > ore1ct) {
            if (bank.deposit(ore, invct - ore1ct))  {
            	inventory.waitForCountLess(ore,invct,2000);
            }
            else  {
            	return false;
            }
        }

        
        invct = inventory.getCount();
        if (invct == 28) {
        	debug("Depositing excess ore2...");
            if (bank.depositAllExcept(ore, fireID, natureID))  {
            	inventory.waitForCountLess(invct, 2000);
            }
        }

        
        debug("Withdrawing ore2 (" + ore2 + ")");
        invct = inventory.getCount(ore2);
        if (bank.withdraw(ore2, 0))  {
        	inventory.waitForCountGreater(ore2, invct, 2000);
        }


        // Check to make sure the withdrawals didn't mess up and give us
        // all seondary ore by accident.  If it did, deposit enough to make
        // room to withdraw the primary ore.
        if (inventory.getCount(ore) == 0 && ore2ct > 0) {
            debug("Depositing excess ore2.");

            invct = inventory.getCount();
            if (bank.deposit(ore2, ore1ct))  {
            	inventory.waitForCountLess(invct, 2000);
            }
            else  {
            	return false;
            }
        }

        bank.close();

        needToBank = false;

        return true;
    }

    // This method checks to see if ore will be needed and if so,
    // sets the needToBank flag.
    //
    // The method takes a boolean that is used to determine if
    // needOre() was called during a superheat cycle and
    // castSpell() has already been called.  This is a special
    // case, as needOre(true) means "do we need ore AFTER we
    // finish casting this spell", rather than do we need more
    // ore NOW.
    public boolean needOre(boolean castActive) {
        int ore1num = inventory.getCount(ore);
        int ore2num = inventory.getCount(ore2);

        if (castActive) {  // Determine if we are going to need ore
            if (bar == runeBarID && (ore2num < 9 || ore1num < 2)) {
                return true;
            } else if (bar == addyBarID && (ore2num < 16 || ore1num < 2)) {
                return true;
            } else if (bar == mithBarID && (ore2num < 8 || ore1num < 2)) {
                return true;
            } else if (bar == goldBarID && ore1num < 2) {
                return true;
            } else if (bar == silverBarID && ore1num < 2) {
                return true;
            } else if (bar == steelBarID && (ore2num < 4 || ore1num < 2)) {
                return true;
            } else if (bar == ironBarID && ore1num < 2) {
                return true;
            } else if (bar == bronzeBarID && (ore1num < 2 || ore2num < 2)) {
                return true;
            }
        } else { // Determine if we need ore NOW.
            if (bar == runeBarID && (ore2num < 8 || ore1num < 1)) {
                return true;
            } else if (bar == addyBarID && (ore2num < 6 || ore1num < 1)) {
                return true;
            } else if (bar == mithBarID && (ore2num < 4 || ore1num < 1)) {
                return true;
            } else if (bar == goldBarID && ore1num == 0) {
                return true;
            } else if (bar == silverBarID && ore1num == 0) {
                return true;
            } else if (bar == steelBarID && (ore2num < 2 || ore1num < 1)) {
                return true;
            } else if (bar == ironBarID && ore1num == 0) {
                return true;
            } else if (bar == bronzeBarID && (ore1num == 0 || ore2num == 0)) {
                return true;
            }

        }

        return false;
    }

    
    private int SmithXPChange;
    private int MagicXPChange;
    private int SmithLevelChange;
    private int MagicLevelChange;
    private long hours;
    private long minutes;
    private long seconds;
    private long runmins;

    
    public void onRepaint(Graphics g) {

        if (game.isLoggedIn()) {
            Color PERCBAR = new Color(255, 255, 0, 150);

            long millis = System.currentTimeMillis() - startTime;
            hours = millis / (1000 * 60 * 60);
            millis -= hours * 1000 * 60 * 60;
            minutes = millis / (1000 * 60);
            millis -= minutes * 1000 * 60;
            seconds = millis / 1000;
            runmins = minutes + (hours);

            SmithXPChange = skills.getCurrentXP(STAT_SMITHING) - startSmithXP;
            MagicXPChange = skills.getCurrentXP(STAT_MAGIC) - startMagicXP;

            SmithLevelChange = skills.getCurrentLvl(STAT_SMITHING) - startSmithLevel;
            MagicLevelChange = skills.getCurrentLvl(STAT_MAGIC) - startMagicLevel;

            int boxX = 9;
            int boxY = 152;
            int x = boxX + 3;
            int y = boxY;
            int boxwidthbase = 186;
            int boxwidth=180;

            g.setFont(g.getFont().deriveFont(Font.BOLD + Font.ITALIC));
            g.setColor(statusBoxBG);
            g.fillRoundRect(boxX, boxY, boxwidth, 182, 10, 10);
            
	        g.setColor( Color.RED );
	        g.drawRoundRect(boxX, boxY, boxwidth, 182, 15, 15);
	        g.setColor( Color.GREEN );
	        g.drawRoundRect(boxX-1, boxY-1, boxwidth+2, 184, 15, 15);
	        g.setColor( Color.BLUE );
	        g.drawRoundRect(boxX-2, boxY-2, boxwidth+4, 186, 15, 15);
	        g.drawRoundRect(boxX-3, boxY-3, boxwidth+6, 188, 15, 15);
            
            g.setColor(Color.ORANGE);
            g.drawString("  " + props.name() + " v" + props.version(), x, y += 16);

            g.setFont(g.getFont().deriveFont(Font.PLAIN));
            g.setColor(statusBoxFG);
            g.drawString("Time running: " + hours + "." + minutes + "." + seconds,
                    x, y += 18);

            g.drawString("Magic Level: "
                    + skills.getCurrentLvl(STAT_MAGIC), x, y += 16);
            g.drawString("Gained " + MagicLevelChange + " levels, " + MagicXPChange
                    + " exp.", x, y += 12);

            //Progress bar from Jacmob's scripts
            g.setColor(Color.RED);
            boxwidth = boxwidthbase;
            g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
            PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(STAT_MAGIC))), 255, 0, 150);
            g.setColor(PERCBAR);
            boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(STAT_MAGIC) / 100;
            g.fill3DRect(x, y, boxwidth, 15, true);

            g.setColor(Color.WHITE);
            g.drawString("XP to level " + (skills.getRealLvl(STAT_MAGIC) + 1) + ":  " + skills.getXPToNextLvl(STAT_MAGIC) + " (" + skills.getPercentToNextLvl(STAT_MAGIC) + "%)", x + 2, y += 12);

            if (runmins > 0 && MagicXPChange > 0) {
                float lvlmins = skills.getXPToNextLvl(STAT_MAGIC) / (MagicXPChange / runmins);
                g.drawString("Next level in " + (long) lvlmins + " minutes", x, y += 14);
                g.drawString("Magic XP per hour: " + (MagicXPChange / runmins) * 60, x, y += 12);
            }


            g.drawString("Smith Level: "
                    + skills.getCurrentLvl(STAT_SMITHING), x, y += 18);
            g.drawString("Gained " + SmithLevelChange + " levels, " + SmithXPChange
                    + " exp.", x, y += 12);

            //Progress bar from Jacmob's scripts
            g.setColor(Color.RED);
            boxwidth = boxwidthbase;
            g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
            PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(STAT_SMITHING))), 255, 0, 150);
            g.setColor(PERCBAR);
            boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(STAT_SMITHING) / 100;
            g.fill3DRect(x, y, boxwidth, 15, true);

            g.setColor(Color.WHITE);
            g.drawString("XP to level " + (skills.getRealLvl(STAT_SMITHING) + 1) + ":  " + skills.getXPToNextLvl(STAT_SMITHING) + " (" + skills.getPercentToNextLvl(STAT_SMITHING) + "%)", x + 2, y += 12);

            if (runmins > 0 && SmithXPChange > 0) {
                float lvlmins = skills.getXPToNextLvl(STAT_SMITHING) / (SmithXPChange / runmins);
                g.drawString("Next level in " + (long) lvlmins + " minutes", x, y += 14);
                g.drawString("Magic XP per hour: " + (SmithXPChange / runmins) * 60, x, y += 12);
            }
        }
    }

    public boolean onStart(Map<String, String> args) {
        onStart = true;

        // Get what bar to make and setup for it
        String baropt = args.get("bar");
        if (baropt == null) {
            log("An invalid selection for the bar to create was made.");
            return false;
        } else if (baropt.equals("Runite")) {
            bar = runeBarID;
            ore = runeOreID;
            ore2 = coalOreID;
            ore1ct = 3;
            ore2fact = 8;
            ore2ct = ore1ct * ore2fact;
        } else if (baropt.equals("Adamantite")) {
            bar = addyBarID;
            ore = addyOreID;
            ore2 = coalOreID;
            ore1ct = 3;
            ore2fact = 7;
            ore2ct = ore1ct * ore2fact;
        } else if (baropt.equals("Mithril")) {
            bar = mithBarID;
            ore = mithOreID;
            ore2 = coalOreID;
            ore1ct = 5;
            ore2fact = 4;
            ore2ct = ore1ct * ore2fact;
        } else if (baropt.equals("Gold")) {
            bar = goldBarID;
            ore = goldOreID;
            ore2 = 0;
            ore1ct = 27;
            ore2fact = 0;
            ore2ct = ore1ct * ore2fact;
        } else if (baropt.equals("Silver")) {
            bar = silverBarID;
            ore = silverOreID;
            ore2 = 0;
            ore1ct = 27;
            ore2fact = 0;
            ore2ct = ore1ct * ore2fact;
        } else if (baropt.equals("Steel")) {
            bar = steelBarID;
            ore = ironOreID;
            ore2 = coalOreID;
            ore1ct = 9;
            ore2fact = 2;
            ore2ct = ore1ct * ore2fact;
        } else if (baropt.equals("Iron")) {
            bar = ironBarID;
            ore = ironOreID;
            ore2 = 0;
            ore1ct = 27;
            ore2fact = 0;
            ore2ct = ore1ct * ore2fact;
        } else if (baropt.equals("Bronze")) {
            bar = bronzeBarID;
            ore = copperOreID;
            ore2 = tinOreID;
            ore1ct = 13;
            ore2fact = 1;
            ore2ct = ore1ct * ore2fact;
        }

        // Establish time for  to run, in 90s-120s
        // intervals.
        Time = System.currentTimeMillis() + random(90000, 120000);

        while (!game.isLoggedIn()) {
            wait(100);
        }
        while (skills.getCurrentXP(STAT_HITPOINTS) == 0) {
            wait(100);
        }

        startTime = System.currentTimeMillis();
        startSmithLevel = skills.getCurrentLvl(STAT_SMITHING);
        startMagicLevel = skills.getCurrentLvl(STAT_MAGIC);
        startSmithXP = skills.getCurrentXP(STAT_SMITHING);
        startMagicXP = skills.getCurrentXP(STAT_MAGIC);

        needToBank = needOre(false);  // See if we need ore NOW

        onStart = false;

        log("Script is starting.");
        return true;
    }


    public void onFinish() {
        log("--------> zzSuperheater <--------");
        log(String.format("  Elapsed time: %s", 
        	String.format("%s:%s:%s",  hours, minutes, seconds)));
        log(String.format("  Magic Levels gained: %d", MagicLevelChange));
        log(String.format("  Magic XP gained:     %d", MagicXPChange));
        log(String.format("  Magic XP rate:    %d/hr", (MagicXPChange / runmins) * 60));
        log("");
        log(String.format("  Smith Levels gained: %d", SmithLevelChange));
        log(String.format("  Smith XP gained:     %d", SmithXPChange));
        log(String.format("  Smith XP rate :   %d/hr", (SmithXPChange / runmins) * 60));
        log("");
    }

    public boolean superheat() {
        
        debug("Casting superheat");
		if (magic.castSpell(Magic.SPELL_SUPERHEAT_ITEM))  {
			if (!inventory.waitForOpen(random(2000,2500)))  {
				return false;
			}
		}

        debug("Inventory should be open now");
        // The new RunTek5 interface causes RSBot to have to open the
        // inventory interface in order to do stuff like inventory.getCount.
        // castSpell causes the inventory to get opened anyway, so let's
        // see if we can't take advantage of that.
        //
        // This is the time to do any inventory checking an not incur
        // an unnecessary trip to the inventory tab.

        if (inventory.isOpen() && !inventory.contains(natureID)) {
            log("Out of nature runes!  Stopping script.");
            return false;
        }

        //This is a failsafe, see if we need to bank right now,
        //before continuing.
        if (needToBank = needOre(false)) {
            return true;
        }

        needToBank = needOre(true);

        if (!inventory.contains(ore)) {
            mouse.click(true);
            return true;
        }

        if (!inventory.clickItem(ore, "Cast"))  {
        	return false;
        }

        return magic.waitForOpen(random(2000,3000));
    }

    // This  support is based on WarXperiment's, but
    // doesn't do as many things.  This will:
    //	- move the mouse to a nearby player to see name/level
    //  - move to and right click a nearby player to see the
    //    players there.
    //  - Move the mouse to select the skills tab and hover over
    //    Woodcutting for a short while.
    //  - Move the mouse to the friends list to see who's online
    //  - Mouse actions will move the mouse back to a slightly
    //    different location from where it originally was.
    //  - Won't duplicate the prior action.
    // HoverPlayer 1-30, ClickPlayer 31-60, SkillsTab 61-75, FriendsTab 76-80,
    // EquipmentTab 81-85
    private enum Actions {

        HoverPlayer, ClickPlayer, SkillsTab, FriendsTab,
        EquipmentTab
    }
    private Actions lastAction = Actions.EquipmentTab;
    boolean pauseAntiban = false;

    private void zzAntiban() {
        long currTime = System.currentTimeMillis();

        if (pauseAntiban || (Time > currTime)) {
            return;
        }

        // Set to go off again in another 3-4 minutes.
        Time = currTime + random(180000, 240000);

        int r = random(1, 85);
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

            if (game.getCurrentTab() != Game.tabStats) {
                RSInterfaceChild tab;
                game.openTab(Game.tabStats);

                if (random(1, 100) < 20) {
                    // Check magic 20% of the time
                    tab = iface.get(320).getChild(128);
                    mouse.move(new Point(tab.getAbsoluteX()
                            + random(2, tab.getWidth() - 1), tab.getAbsoluteY()
                            + random(2, tab.getHeight() - 1)));
                    wait(random(900, 1800));
                }

                tab = iface.get(320).getChild(140);
                mouse.move(new Point(tab.getAbsoluteX()
                        + random(2, tab.getWidth() - 1), tab.getAbsoluteY()
                        + random(2, tab.getHeight() - 1)));
                wait(random(900, 2000));
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

            if (game.getCurrentTab() != Game.tabFriends) {
                game.openTab(Game.tabFriends);
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

    }
}
