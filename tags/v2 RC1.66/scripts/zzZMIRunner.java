// zzZMIRunner by zzSleepzz
// ZZopyright 2009,2010 zzSleepzz
//
// Version 1.8 (15July2010) requires Runedev v7.90 or higher
// - Change altars and ladder to use models to click.
// - Added support to use Moonclan Group Tele
// - Fixed broken pouch repair
// - Added antiban toggle to diable while banking and running through ZMI cave.
//
// Version 1.7 (15July2010)
// - Cleaned up pouch handling.
// - Added support to use contact npc spell to repair degraded pouches
// - Improved instructions on startup panel.
// - Added logout failsafe when life points are below 80.
//
// Version 1.6 (24May2010)
// - Borrowed the prayerEnabled() method from Bool to provide more reliable
//   prayer activation.  Delaying activation till further in on the altar run.
// - Using different bank method to open bank since we need to use a specific
//   tile.  The NPC part isn't working well.
//
// Version 1.5 (24May2010)
// - Rewrote path walking from scratch.  Fixed hang at south
//   side of top of chaos altar ladder.
// - Path walking is now more smooth and human-like.
//
// Version 1.4 (all over the place)
// - Trying desperately to get banking and path
//   walking to behave...not to mention pouches.
//
// Version 1.3 (12Jan2010)
// - Reworked pathing to eliminate RSTilePath
//
// Version 1.2 (13Dec2009)
// - Revised some path walking to alleviate hangs
// - Revise state detection to be more clean and structured
//
// Version 1.1 (5Nov2009)
// - Reverted back to using RSTilePaths for walking.
// - Defined RSAreas and test to ensure player is
//   within them, otherwise, tele to Ourania or
//   Moonclan depending on ess in inventory.

// Version 1.0 (5Nov2009)
// - Added state detection support to recover from
//   misclicking during teleporting.
// - Revised banking to prevent repeated attempts to
//   fill a filled pouch.
// - Resolved chopiness of running
// - Resolved clicking ladder twice at chaos altar.
//
// Version 0.2 (3Nov2009)
// - Adding pouches
// - Adding recharge prayer and turn on (sketchy though)
//
// Version 0.1 (2Nov2009)
// - Basic Moonclan route support
//
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"zzSleepzz"},
category = "Runecrafting",
name = "zzZMIRunner by zzSleepzz",
version = 1.8,
description =
"<html>"
+ "<head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body>"
+ "<center>"
+ "<b><font size=+2 color=\"blue\">zzZMIRunner v1.8</font></b>"
+ "<br><font color=blue><b>Author:</b> zzSleepzz</font>"
+ "</center>"
+ "<p><b>Description:</b> Runs runes to ZMI using Moonclan Tele route."
+ "<p><font size='-1'>"
+ "Setup your quick prayers for the desired protection before starting this script."
+ "<p>Pure Essence must be in the first bank slot.  Cosmic and Air runes must be"
+ "visible in the same bank tab as the pure essence so the script can use the "
+ "contact NPC spell to repair pouches."
+ "<p><font color='red'>Have <b>ALL</b> law and astral runes withdrawn as well as any pouches to be used.  "
+ "Then make sure the bank is COMPLETELY filled, with junk if necessary, or the banking "
+ "<b>WILL NOT WORK</b>.</font>"
+ "<p><b>NOTE: THIS SCRIPT DOES NOT SUPPORT EATING!!!</b>"
+ "</font>"
+ "</body>"
+ "</html>")
public class zzZMIRunner extends Script
        implements MessageListener, PaintListener {

    ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);

    private static final int JACK_ID = 5901;
    private static final RSTile LADDER_TOP_TILE = new RSTile(2452, 3231);
    private static final int LADDER_TOP_ID = 26849;
    private static final RSTile LADDER_BOTTOM_TILE = new RSTile(3271, 4861);
    private static final int INTERFACE_LUNAR_SPELLS = 430;
    private static final int SPELL_MOONCLAN_TELE = 42;
    private static final int SPELL_OURANIA_TELE = 53;
    private static final int SPELL_MOONCLAN_GROUPTELE = 55;
    private static final int SPELL_CONTACT_NPC = 26;
    private static final RSTile MOONCLAN_TELE_SPOT = new RSTile(2110, 3915);
    private static final RSTile MOONCLAN_BANK_SPOT = new RSTile(2099, 3918);
    //private static final RSTile MOONCLAN_BANK_TILE = new RSTile(2099, 3920);
    private static final RSTile OURANIA_TELE_SPOT = new RSTile(2467, 3244);
    private static final RSTile CHAOS_ALTAR_TILE = new RSTile(2455, 3231);
    private static final int CHAOS_ALTAR_ID = 411;
    private static final RSTile ZMI_ALTAR_TILE = new RSTile(3316, 4811);
    private static final int ZMI_ALTAR_ID = 26847;
    private String srvMsg = null;
    /*
    private RSTilePath ZMIAltarPath = new RSTilePath(
    new RSTile[] { new RSTile(3270,4849),
    new RSTile(3272,4845), new RSTile(3271,4830),
    new RSTile(3275,4815), new RSTile(3289,4810),
    new RSTile(3303,4811), new RSTile(3314,4811)
    },
    this
    );
     */
    private static final RSTile[] BankPath = {MOONCLAN_TELE_SPOT, new RSTile(2105, 3915),
        new RSTile(2108, 3915), MOONCLAN_BANK_SPOT};
    private static final RSTile[] ZMIAltarPath = {new RSTile(3271, 4841),
        new RSTile(3270, 4833), new RSTile(3271, 4822), new RSTile(3275, 4812),
        new RSTile(3287, 4812), new RSTile(3300, 4811), new RSTile(3314, 4811)
    };
    private RSTile[] ChaosAltarPath = {new RSTile(2456, 3249), new RSTile(2455, 3238), new RSTile(2455, 3232)};
    private static final int PURE_ESS_ID = 7936;
    private static final int LAW_ID = 563;
    private static final int ASTRAL_ID = 9075;
    private static final int AIR_ID = 556;
    private static final int COSMIC_ID = 564;
    private static final int[] RUNES = {554, 555, 556, 557, 558, 559, 560, 561, 562, 564, 565, 566};
    private static final int GIANT_POUCH_ID = 5514;
    private static final int GIANT_POUCH_DMGD_ID = 5515;
    private static final int LARGE_POUCH_ID = 5512;
    private static final int LARGE_POUCH_DMGD_ID = 5513;
    private static final int MED_POUCH_ID = 5510;
    private static final int MED_POUCH_DMGD_ID = 5511;
    private static final int GIANT_POUCH_IDS[] = {GIANT_POUCH_ID, GIANT_POUCH_DMGD_ID};
    private static final int LARGE_POUCH_IDS[] = {LARGE_POUCH_ID, LARGE_POUCH_DMGD_ID};
    private static final int MED_POUCH_IDS[] = {MED_POUCH_ID, MED_POUCH_DMGD_ID};
    private static final int SMALL_POUCH_ID = 5509;
    private int giantPouch = 0;
    private int largePouch = 0;
    private int medPouch = 0;
    private boolean outOfEss = false;
    private boolean pouchRepaired = false;
    private Boolean giantFilled = null,
            largeFilled = null,
            medFilled = null,
            smallFilled = null,
            pouchFull = false;
    private static final RSArea CHAOS_AREA =
            new RSArea(new RSTile(2451, 3229), new RSTile(2478, 3253));
    private static final RSArea ZMI_AREA =
            new RSArea(new RSTile(3265, 4804), new RSTile(3323, 4862));
    private static final RSArea LUNAR_AREA =
            new RSArea(new RSTile(2092, 3905), new RSTile(2116, 3923));
    private static final RSArea ALTAR_AREA =
            new RSArea(new RSTile(3311, 4804), new RSTile(3322, 4816));
    private int retries = 0;

    private enum State {

        TELE_MOONCLAN, RUN_TO_BANK, BANKING, TELE_OURANIA,
        RUN_TO_TOP_LADDER, CLIMB_DOWN, RUN_TO_ZMI_ALTAR, CRAFT
    };
    private State scriptState = State.CRAFT;

    protected int getMouseSpeed() {
        return random(6, 10);
    }

    // Credit to Bool for this
    private boolean prayerEnabled() {
        return settings.get(1395) > 0;
    }

    public boolean onStart(Map<String, String> args) {
        startXP = 0;
        pouchFull = false;

        while (!game.isLoggedIn()) {
            wait(100);
        }
        //while (skills.getCurrentLP()==0) wait(100);

        debug("Checking for pouches.");

        // If the inventory contains pouches, check if
        // they are full or not.

        if (!inventory.contains(SMALL_POUCH_ID)) {
            smallFilled = false;
        } else {
            smallFilled = null;
            while (smallFilled == null && inventory.contains(SMALL_POUCH_ID)) {
                srvMsg = null;

                inventory.clickItem(SMALL_POUCH_ID, "Check ");

                int ct = 0;
                while (ct++ < 20 && srvMsg == null) {
                    wait(100);
                }

                if (srvMsg != null && srvMsg.contains("pure essences in")) {
                    smallFilled = true;
                } else if (srvMsg != null && srvMsg.contains("no essences in")) {
                    smallFilled = false;
                }

                log("Small pouch filled: " + smallFilled);
            }
        }

        if (!inventory.contains(MED_POUCH_IDS)) {
            medFilled = false;
        } else {
            medFilled = null;
            while (medFilled == null && inventory.contains(MED_POUCH_IDS)) {
                for (int pouch : MED_POUCH_IDS) {
                    srvMsg = null;
                    if (inventory.contains(pouch)) {
                        inventory.clickItem(pouch, "Check ");

                        int ct = 0;
                        while (ct++ < 20 && srvMsg == null) {
                            wait(100);
                        }

                        if (srvMsg != null && srvMsg.contains("pure essences in")) {
                            medFilled = true;
                        } else if (srvMsg != null && srvMsg.contains("no essences in")) {
                            medFilled = false;
                        }
                    }
                }
                log("Medium pouch filled: " + medFilled);
            }
        }

        if (!inventory.contains(LARGE_POUCH_IDS)) {
            largeFilled = false;
        } else {
            largeFilled = null;
            while (largeFilled == null && inventory.contains(LARGE_POUCH_IDS)) {
                for (int pouch : LARGE_POUCH_IDS) {
                    srvMsg = null;
                    if (inventory.contains(pouch)) {
                        inventory.clickItem(pouch, "Check ");

                        int ct = 0;
                        while (ct++ < 20 && srvMsg == null) {
                            wait(100);
                        }

                        if (srvMsg != null && srvMsg.contains("pure essences in")) {
                            largeFilled = true;
                        } else if (srvMsg != null && srvMsg.contains("no essences in")) {
                            largeFilled = false;
                        }
                    }
                }
                log("Large pouch filled: " + largeFilled);
            }
        }

        if (!inventory.contains(GIANT_POUCH_IDS)) {
            giantFilled = false;
        } else {
            giantFilled = null;
            while (giantFilled == null && inventory.contains(GIANT_POUCH_IDS)) {
                for (int pouch : GIANT_POUCH_IDS) {
                    srvMsg = null;
                    if (inventory.contains(pouch)) {
                        inventory.clickItem(pouch, "Check ");

                        int ct = 0;
                        while (ct++ < 20 && srvMsg == null) {
                            wait(100);
                        }

                        if (srvMsg != null && srvMsg.contains("pure essences in")) {
                            giantFilled = true;
                        } else if (srvMsg != null && srvMsg.contains("no essences in")) {
                            giantFilled = false;
                        }
                    }
                }
                log("Giant pouch filled: " + giantFilled);
            }
        }

        return true;
    }

    public void onFinish() {
    }

    public boolean myWalkPath(RSTile[] path) {
        return myWalkPath(path, 16, 1, 1);
    }

    public boolean myWalkPath(RSTile[] path, int maxDist, int randX, int randY) {
        int dist = walk.getDestination().distanceTo();

        // The maximum number of tiles from the center of the MM to the edge
        // is 18.  If maxDist is greater than 16, we force it to 16.  If we
        // use 17 or 18, we occassionally get stuck spam clicking the MM ring till
        // we get banned.
        if (maxDist > 16) {
            maxDist = 16;
        }

        // First, check if we are getting close to the destination,
        // if so, wait a short while.  Then, if the player is still
        // moving, return false, otherwise, continue the walk.
        if (dist < maxDist - 2 && dist > random(3, 5)) {
            if (player.getMine().isMoving()) {

                return false;
            }
        }

        // Put the walking into a try block to catch the NPE that
        // we'll get at the end of the path.
        try {
            RSTile next = walk.nextTile(path, maxDist);

            if (next == null) {
                return true;
            } else {
                // If randX or randY specified set t to randomized
                // tile based on next, otherwise, just set it to next.
                RSTile t = next;

                if (randX != 0 || randY != 0) {
                    t = walk.randomizeTile(next, randX, randY);
                }

                // If distance to t is more than maxDist, get the
                // nearest tile on the MM.
                if (!t.isOnMinimap() || t.distanceTo() > 16) {
                    t = this.tile.getClosestOnMap(t);
                }

                walk.tileMM(t, 0, 0);  // Tile t is already randomized.
                player.waitToMove(850);
            }
        } catch (final Exception e) {
            return false;
        }

        return false;
    }

    public int loop() {

        try {
            if (!game.isLoggedIn()) {
                return (500);
            }

            if (skills.getCurrentLP() < 80) {
                log("Life points below 80, trying to logout.");
                game.logout();
            }

            if (player.isIdle() && player.getMine().getInteracting() == null) {
                zzAntiban();
            }

            if (!isRunning()) {
                game.setRun(true);
                wait(random(250, 350));
            }

            // Setup the pouches to use.  This covers the case of
            // pouches degrading over time.  We'll keep using them.
            if (inventory.contains(GIANT_POUCH_IDS[0])) {
                giantPouch = GIANT_POUCH_IDS[0];
            }
            if (inventory.contains(GIANT_POUCH_IDS[1])) {
                giantPouch = GIANT_POUCH_IDS[1];
            }

            if (inventory.contains(LARGE_POUCH_IDS[0])) {
                largePouch = LARGE_POUCH_IDS[0];
            }
            if (inventory.contains(LARGE_POUCH_IDS[1])) {
                largePouch = LARGE_POUCH_IDS[1];
            }

            if (inventory.contains(MED_POUCH_IDS[0])) {
                medPouch = MED_POUCH_IDS[0];
            }
            if (inventory.contains(MED_POUCH_IDS[1])) {
                medPouch = MED_POUCH_IDS[1];
            }

            scriptState = getState();

            /*
            if (scriptState != null)
            debug("scriptState=" + scriptState.toString());
             */

            if (scriptState == State.TELE_OURANIA) {
                if (bank.isOpen()) {
                    bank.close();
                    wait(random(500, 600));
                }

                if (!castLunar(SPELL_OURANIA_TELE)) {
                    return random(100, 150);
                }

                if (player.waitForAnim(2000) != 1) {
                    retries = 0;
                    while (retries++ < 10 && OURANIA_TELE_SPOT.distanceTo() > 15) {
                        wait(100);
                    }
                    wait(random(300, 400));
                }
            } else if (scriptState == State.RUN_TO_TOP_LADDER
                    && CHAOS_AREA.contains(player.getMyLocation())) {

                // If chaos altar is not on screen we walk.
                // Walk is finished when tile is on screen.
                if (!CHAOS_ALTAR_TILE.isOnScreen() && !myWalkPath(ChaosAltarPath)) {
                    //debug("Walking to chaos altar.");
                    return random(100, 150);
                }

                debug("End of walking to chaos altar.");

                if (skills.getCurrentLvl(STAT_PRAYER) < random(29, 31)) {
                    int pray;
                    retries = 0;
                    debug("Recharging prayer");
                    do {
                        pray = skills.getCurrentLvl(STAT_PRAYER);
                        wait(random(500, 800));
                        RSObject altar = objects.getNearestByID(CHAOS_ALTAR_ID);

                        while (player.getMine().isMoving()) {
                            wait(random(100, 200));
                        }

                        altar.action("Pray-at");
                        if (player.waitForAnim(3000) != -1) {
                            wait(random(250, 350));
                        }
                    } while (retries++ < 10
                            && skills.getCurrentLvl(STAT_PRAYER) <= pray
                            && pray < skills.getRealLvl(STAT_PRAYER));
                }
            } else if (scriptState == State.CLIMB_DOWN
                    && CHAOS_AREA.contains(player.getMyLocation())) {
                RSObject ladder = objects.getNearestByID(LADDER_TOP_ID);
                ladder.action("Climb");

                if (player.waitForAnim(1000) != -1) {
                    retries = 0;
                    do {
                        wait(150);
                    } while (retries++ < 20 && !ZMI_AREA.contains(player.getMyLocation()));
                }
            } else if (scriptState == State.RUN_TO_ZMI_ALTAR
                    && ZMI_AREA.contains(player.getMyLocation())) {
                antibanPaused = true;

                if (LADDER_BOTTOM_TILE.distanceTo() < 9) {
                    if (player.getMyEnergy() < 30) {
                        player.rest(100);
                    }
                }


                if (!ZMI_ALTAR_TILE.isOnScreen() && !myWalkPath(ZMIAltarPath)) {
                    //debug("Walking to ZMI altar.");

                    if (!prayerEnabled() && skills.getCurrentLvl(STAT_PRAYER) > 0
                            && LADDER_BOTTOM_TILE.distanceTo() > 30) {
                        iface.clickChild(Constants.INTERFACE_PRAYER_ORB, 1, "Turn quick ");
                    }

                    return random(800, 1200);
                }

                debug("End of walking to ZMI altar.");
            } else if (scriptState == State.CRAFT
                    && ALTAR_AREA.contains(player.getMyLocation())) {
                antibanPaused = false;

                craftRunes();

                // We're done if the bank has no more ess.
                if (outOfEss) {
                    debug("2. Out of essence from last bank run.");
                    stopScript(false);
                }
            } else if (scriptState == State.TELE_MOONCLAN) {
                int spell = SPELL_MOONCLAN_GROUPTELE;
                if (skills.getCurrentLvl(Constants.STAT_MAGIC) < 70) {
                    spell = SPELL_MOONCLAN_TELE;
                }

                if (!castLunar(spell)) {
                    return random(100, 150);
                }
                wait(random(1000, 1100));

                retries = 0;
                while (retries++ < 10 && MOONCLAN_TELE_SPOT.distanceTo() > 15) {
                    wait(100);
                }
                wait(random(300, 400));
            } else if (scriptState == State.RUN_TO_BANK
                    && LUNAR_AREA.contains(player.getMyLocation())) {

                if (!MOONCLAN_BANK_SPOT.isOnScreen()
                        && !myWalkPath(BankPath)) {
                    //debug("Walking to bank. "+walk.getDestination().distanceTo()+" "+getDestination().toString());
                    return random(1000, 1200);
                }
            } else if (scriptState == State.BANKING
                    && LUNAR_AREA.contains(player.getMyLocation())) {
                debug("Banking started.");
                antibanPaused = true;
                if (bank()) {
                    debug("Finished banking");
                }
                antibanPaused = false;
            }

            return random(180, 220);
        } catch (Exception e) {
            e.printStackTrace();
            return random(180, 220);
        }
    }

    private void craftRunes() {
        debug("Crafting runes.");
        RSObject altar = objects.getNearestByID(ZMI_ALTAR_ID);

        while (player.getMine().isMoving()) {
            wait(random(100, 150));
        }

        int ct = 0;
        debug("Crafting 1.");
        while (ct++ < 5 && inventory.contains(PURE_ESS_ID)) {
            debug("Clicking altar");
            // Don't use object clicking since the object is so big,
            // it clicks off the edge on other side too often,
            // causing player to run around it.
            //atTile(walk.randomizeTile(ZMI_ALTAR_TILE,1,1), "Craft");
            altar.action("Craft");

            ct = 0;
            while (ct++ < 20 && inventory.contains(PURE_ESS_ID)) {
                wait(100);
            }
        }
        debug("Done crafting 1.");

        // We need to apply some intelligence to the
        // pouch use.  Determine space available,
        // and keep track of pouches used so we don't
        // unnecessarily keep emptying them.
        while (giantFilled || largeFilled
                || medFilled || smallFilled) {

            int inv = inventory.getCount();
            int ess = inventory.getCount(PURE_ESS_ID);
            if (inventory.contains(giantPouch)
                    && giantFilled && inv < (28 - 11)) {
                debug("Emptying giant pouch");

                ct = 0;
                do {
                    srvMsg = null;
                    pouchFull = true;
                    inventory.clickItem(giantPouch, "Empty");

                    int ct1 = 0;
                    while (ct1++ < 11 && srvMsg == null) {
                        wait(100);
                    }

                    if (!pouchFull || ess > inventory.getCount(PURE_ESS_ID)) {
                        debug("Giant pouch is now empty");
                        giantFilled = false;
                    }
                } while (ct++ < 5 && ess > inventory.getCount(PURE_ESS_ID));
            }

            inv = inventory.getCount();
            ess = inventory.getCount(PURE_ESS_ID);
            if (inventory.contains(largePouch)
                    && largeFilled && inv < (28 - 8)) {
                debug("Emptying large pouch");

                ct = 0;
                do {
                    srvMsg = null;
                    pouchFull = true;
                    inventory.clickItem(largePouch, "Empty");
                    int ct1 = 0;
                    while (ct1++ < 11 && srvMsg == null) {
                        wait(100);
                    }

                    if (!pouchFull || ess > inventory.getCount(PURE_ESS_ID)) {
                        debug("Large pouch is now empty");
                        largeFilled = false;
                    }
                } while (ct++ < 5 && ess > inventory.getCount(PURE_ESS_ID));
            }

            inv = inventory.getCount();
            ess = inventory.getCount(PURE_ESS_ID);
            if (inventory.contains(medPouch)
                    && medFilled && inv < (28 - 5)) {
                debug("Emptying medium pouch");

                ct = 0;
                do {
                    srvMsg = null;
                    pouchFull = true;
                    inventory.clickItem(medPouch, "Empty");
                    int ct1 = 0;
                    while (ct1++ < 11 && srvMsg == null) {
                        wait(100);
                    }

                    if (!pouchFull || ess > inventory.getCount(PURE_ESS_ID)) {
                        debug("Medium pouch is now empty");
                        medFilled = false;
                    }
                } while (ct++ < 5 && ess > inventory.getCount(PURE_ESS_ID));
            }

            inv = inventory.getCount();
            ess = inventory.getCount(PURE_ESS_ID);
            if (inventory.contains(SMALL_POUCH_ID)
                    && smallFilled && inv < (28 - 2)) {
                debug("Emptying small pouch");

                ct = 0;
                do {
                    srvMsg = null;
                    pouchFull = true;
                    inventory.clickItem(SMALL_POUCH_ID, "Empty");
                    int ct1 = 0;
                    while (ct1++ < 11 && srvMsg == null) {
                        wait(100);
                    }

                    if (!pouchFull || ess > inventory.getCount(PURE_ESS_ID)) {
                        debug("Small pouch is now empty");
                        smallFilled = false;
                    }
                } while (ct++ < 5 && ess > inventory.getCount(PURE_ESS_ID));
            }

            // Don't change state if we still have ess.
            if (!inventory.contains(PURE_ESS_ID)) {
                scriptState = State.TELE_MOONCLAN;
                break;
            } else {
                while (player.getMine().isMoving()) {
                    wait(random(100, 150));
                }

                ct = 0;
                debug("Crafting 2.");
                while (ct++ < 5 && inventory.contains(PURE_ESS_ID)) {
                    debug("Clicking altar");
                    //atTile(walk.randomizeTile(ZMI_ALTAR_TILE,1,1), "Craft");
                    altar.action("Craft");

                    ct = 0;
                    while (ct++ < 20 && inventory.contains(PURE_ESS_ID)) {
                        wait(100);
                    }
                }
                debug("Done crafting 2.");
            }
        }
    }

    // Returns false if there aren't enough runes to cast the
    // teleport.  Assumes player is holding a mud, lava or earth
    // staff.
    private boolean castLunar(int spellID) {
        if (!inventory.contains(LAW_ID)
                || !inventory.contains(ASTRAL_ID)) {

            log("Insufficient law or astral runes to teleport.  We're done.");
            stopScript(false);
            return false;
        }

        game.openTab(TAB_MAGIC);

        iface.waitForOpen(iface.get(INTERFACE_LUNAR_SPELLS), 2500);

        return game.getCurrentTab() == Constants.TAB_MAGIC
                && iface.clickChild(INTERFACE_LUNAR_SPELLS, spellID);
    }

    public void messageReceived(MessageEvent e) {
        srvMsg = e.getMessage();
        if (srvMsg.contains("pouch is full")) {
            pouchFull = true;
        } else if (srvMsg.contains("no essences")) {
            pouchFull = false;
        } else if (srvMsg.contains("degraded")) {
            debug("A pouch has degraded.");
            pouchRepaired = false;
        }
    }

    private boolean bank() {
        // The bank interface could already be open if
        // we were already here and exited, so check for
        // it first.

        debug("distance to bank is " + MOONCLAN_BANK_SPOT.distanceTo());
        if (!MOONCLAN_BANK_SPOT.isOnScreen() && MOONCLAN_BANK_SPOT.distanceTo() < 20) {
            scriptState = State.RUN_TO_BANK;
            return false;
        }

        if (!bank.isOpen() && MOONCLAN_BANK_SPOT.isOnScreen()) {
            debug("1. Trying to open the bank.");
            RSNPC jack = npc.getNearestByID(JACK_ID);

            while (player.getMine().isMoving()) {
                wait(100);
            }
            //bank.atBankBooth(MOONCLAN_BANK_TILE, "Use-quickly ");
            npc.action(jack, "Bank");

            if (iface.waitForOpen(iface.get(INTERFACE_BANK), 4000)) {
                wait(random(600, 750));
            } else {
                RSInterfaceChild ifc1 = game.getTalkInterface();
                RSInterface ifc2 = iface.get(228);
                if (!bank.isOpen()
                        || (ifc1 != null && ifc1.isValid())
                        || (ifc2 != null && ifc2.isValid())) {
                    return false;
                }
            }
        }

        // Proceed only if the interface is in fact open
        if (bank.isOpen()) {
            debug("Bank is open now.");

            if (inventory.contains(RUNES)) {
                bank.depositAll();
                wait(random(1000, 1200));
            }

            // Handle degraded pouches now.
            if (inventory.contains(MED_POUCH_DMGD_ID)
                    || inventory.contains(LARGE_POUCH_DMGD_ID)
                    || inventory.contains(GIANT_POUCH_DMGD_ID)) {
                pouchRepaired = false;

                int ct = 0;
                while (ct++ < 10 && !pouchRepaired) {
                    pouchRepaired = repairPouches();
                }

                if (pouchRepaired) {
                    log("Pouches have been repaired");
                }
                return false;
            }

            int essct = bank.getCount(PURE_ESS_ID);
            if (essct == 0) {
                log("1. Out of essence, stopping now.");
                stopScript(false);
            }

            if (bank.withdraw(PURE_ESS_ID, 0)) {

                // This loop is to wait for the withdraw to complete
                // or timeout.
                retries = 0;
                while (retries++ < 12 && !inventory.contains(PURE_ESS_ID)) {
                    wait(150);
                }

                if (bank.getCount(PURE_ESS_ID) == essct) {
                    return false;
                }
            }

            if (bank.getCount(PURE_ESS_ID) == 0) {
                debug("1. Out of ess detected.");
                outOfEss = true;
            }
        } else {
            return false;
        }

        // Now close the bank and load up the pouches.
        if (!inventory.isFull()) {
            return false;
        } else {
            bank.close();

            retries = 0;
            while (retries++ < 12 && bank.isOpen()) {
                wait(110);
            }
        }

        fillPouches();

        if (!bank.isOpen()
                && MOONCLAN_BANK_SPOT.isOnScreen() && !inventory.isFull()) {
            debug("2. Trying to open the bank.");
            while (player.getMine().isMoving()) {
                wait(100);
            }
            //bank.atBankBooth(MOONCLAN_BANK_TILE, "Use-quickly ");
            RSNPC jack = npc.getNearestByID(JACK_ID);
            npc.action(jack, "Bank");

            iface.waitForOpen(iface.get(INTERFACE_BANK), 4000);
            wait(random(600, 750));

            RSInterfaceChild ifc1 = game.getTalkInterface();
            RSInterface ifc2 = iface.get(228);
            if (!bank.isOpen()
                    || (ifc1 != null && ifc1.isValid())
                    || (ifc2 != null && ifc2.isValid())) {
                return false;
            }
        }

        if (!inventory.isFull() && !outOfEss && bank.isOpen()) {
            // Proceed only if the interface is in fact open and
            // the previous withdraw didn't take the last of the pess.
            bank.withdraw(PURE_ESS_ID, 0);
            retries = 0;
            while (retries++ < 12 && !inventory.contains(PURE_ESS_ID)) {
                wait(150);
            }
            wait(random(500, 600));

            if (bank.getCount(PURE_ESS_ID) == 0) {
                debug("2. Out of ess detected.");
                outOfEss = true;
            }
        }

        // Now close the bank and load up the pouches.
        if (!inventory.isFull()) {
            return false;
        } else {
            bank.close();

            retries = 0;
            while (retries++ < 12 && bank.isOpen()) {
                wait(110);
            }
        }

        fillPouches();

        if (!inventory.isFull() && !outOfEss) {
            if (!bank.isOpen()
                    && MOONCLAN_BANK_SPOT.isOnScreen()) {
                debug("3. Trying to open the bank.");
                while (player.getMine().isMoving()) {
                    wait(100);
                }
                //bank.atBankBooth(MOONCLAN_BANK_TILE, "Use-quickly ");
                RSNPC jack = npc.getNearestByID(JACK_ID);
                npc.action(jack, "Bank");

                iface.waitForOpen(iface.get(INTERFACE_BANK), 4000);
                wait(random(600, 750));

                RSInterfaceChild ifc1 = game.getTalkInterface();
                RSInterface ifc2 = iface.get(228);
                if (!bank.isOpen()
                        || (ifc1 != null && ifc1.isValid())
                        || (ifc2 != null && ifc2.isValid())) {
                    return false;
                }
            }

            // Proceed only if the interface is in fact open
            if (bank.isOpen()) {
                bank.withdraw(PURE_ESS_ID, 0);
                retries = 0;
                while (retries++ < 12 && !inventory.contains(PURE_ESS_ID)) {
                    wait(150);
                }
                wait(random(700, 800));

                if (bank.getCount(PURE_ESS_ID) == 0) {
                    debug("3. Out of ess detected.");
                    outOfEss = true;
                }
            }
        }

        return true;
    }

    /**
     * Uses the contact NPC spell to repair the damaged pouches.
     * This must only be run with the bank open so the cosmic and
     * air runes can be withdrawn.
     *
     * There must be space in the inventory prior to calling this.
     */
    private boolean repairPouches() {
        int ct = 0;

        log("Repairing pouches");
        if (inventory.isFull()) {
            return false;
        }

        if (bank.isOpen()) {
            ct = 0;
            while (ct++ < 5 && !inventory.contains(COSMIC_ID) && !bank.withdraw(COSMIC_ID, 1)) {
                wait(random(200, 300));
            }

            ct = 0;
            while (ct++ < 5 && inventory.getCount(AIR_ID) < 2 && !bank.withdraw(AIR_ID, 2)) {
                wait(random(200, 300));
            }
        }

        if (inventory.getCount(COSMIC_ID) < 1 || inventory.getCount(AIR_ID) < 2) {
            debug("Repair: Don't have the correct amount of cosmic and airs");
            return false;
        }

        debug("Repair: Have the runes now");

        ct = 0;
        while (ct++ < 5 && bank.isOpen()) {
            bank.close();
            wait(random(500, 700));
        }

        ct = 0;
        boolean wasCast = false;
        while (ct++ < 5 && !(wasCast = castLunar(SPELL_CONTACT_NPC))) {
            if (!wasCast) {
                debug("Repair: Cast of contact NPC failed.");
                return false;
            } else if (!iface.waitForOpen(iface.get(88), 6000)) {
                debug("Repair: NPC's interface never opened.");
                return false;
            }
        }

        wait(random(300, 400));
        debug("Repair: Establishing interface slider position");
        // This logic based on pouch repair in Garrett's Astral RC script.
        RSInterfaceChild[] scroller;
        int wt = 0;
        do {
            scroller = iface.getChild(88, 20).getChildren();
            ct = 0;
            while (ct++ < 20 && !(scroller.length > 1)) {
                wait(200);
            }
            debug("Waiting for scroller");
        } while (wt++ < 80 && !(scroller.length > 1));

        if (scroller.length > 1) {
            Point scrollpt =
                    new Point(scroller[5].getAbsoluteX() + random(4, 12),
                    scroller[5].getAbsoluteY() - random(12, 32));
            mouse.click(scrollpt, true);
            debug("Slider clicked");
        } else {
            debug("Repair: Slider isn't valid");
            return false;
        }

        ct = 0;
        RSInterfaceChild darkMage;
        do {
            darkMage = iface.getChild(88, 22);
            wait(200);
        } while (ct++ < 40 && !darkMage.isValid());

        if (!darkMage.isValid()) {
            return false;
        }
        debug("Attempting to speak to dark mage.");
        if (!iface.clickChild(darkMage, 4, "Speak-to")) {
            return false;
        }

        while (inventory.contains(MED_POUCH_DMGD_ID, LARGE_POUCH_DMGD_ID, GIANT_POUCH_DMGD_ID)) {
            wait(random(500, 1000));

            if (iface.get(88).isValid()) {
                return false;
            }

            RSInterface int241 = iface.get(241);

            if (int241 != null && int241.isValid() && !int241.containsText("Dark mage")) {
                break;
            }

            iface.clickContinue();

            RSInterface i = iface.get(230);
            if (i.isValid()) {
                debug("Repair: Clicking repair directive.");
                iface.clickChild(230, 3);
                if (random(1, 1000) < 70) {
                    mouse.moveSlightly();
                }
                wait(random(500, 750));
            }
        }

        if (inventory.contains(MED_POUCH_DMGD_ID, LARGE_POUCH_DMGD_ID, GIANT_POUCH_DMGD_ID)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Fills any pouches in the inventory until they are full or there
     * is no more essence.
     */
    void fillPouches() {
        int esscnt;

        // Only tries to fill each pouche if there's enough ess to do so.

        debug(" giant pouch: " + inventory.contains(giantPouch) + " " + !giantFilled);
        esscnt = inventory.getCount(PURE_ESS_ID);
        if (inventory.contains(giantPouch) && !giantFilled && esscnt > 0) {
            debug("Filling giant pouch.");

            retries = 0;
            pouchFull = false;
            while (retries++ < 10 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                inventory.clickItem(giantPouch, "Fill ");

                int ct = 0;
                while (ct++ < 20 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                    wait(100);
                }
            }

            giantFilled = true;
        }

        debug(" med pouch: " + inventory.contains(medPouch) + " " + medFilled);
        esscnt = inventory.getCount(PURE_ESS_ID);
        if (inventory.contains(medPouch) && !medFilled && esscnt > 0) {
            debug("Filling med pouch.");

            retries = 0;
            pouchFull = false;
            while (retries++ < 10 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                inventory.clickItem(medPouch, "Fill ");

                int ct = 0;
                while (ct++ < 20 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                    wait(100);
                }
            }

            medFilled = true;
        }

        debug(" small pouch: " + inventory.contains(SMALL_POUCH_ID) + " " + smallFilled);
        esscnt = inventory.getCount(PURE_ESS_ID);
        if (inventory.contains(SMALL_POUCH_ID) && !smallFilled && esscnt > 0) {
            debug("Filling small pouch.");

            retries = 0;
            pouchFull = false;
            while (retries++ < 10 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                inventory.clickItem(SMALL_POUCH_ID, "Fill ");

                int ct = 0;
                while (ct++ < 20 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                    wait(100);
                }
            }

            smallFilled = true;
        }

        debug(" lg pouch: " + inventory.contains(largePouch) + " " + largeFilled);
        esscnt = inventory.getCount(PURE_ESS_ID);
        if (inventory.contains(largePouch) && !largeFilled && esscnt > 0) {
            debug("Filling large pouch.");

            retries = 0;
            pouchFull = false;
            while (retries++ < 10 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                inventory.clickItem(largePouch, "Fill ");

                int ct = 0;
                while (ct++ < 20 && !pouchFull && esscnt == inventory.getCount(PURE_ESS_ID)) {
                    wait(100);
                }
            }

            largeFilled = true;
        }
    }
    // Credits: This proggy is based on proggies from pmiller624 and Jacmob.
    // Modularized and enhanced with my own touches and style.
    private long scriptStartTime;
    private int startXP;
    private int startLevel;
    private int lastExp;
    private int index = Constants.STAT_RUNECRAFTING;

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

        int y = 224;
        int x = 5;
        int boxwidth = 190;
        g.setColor(new Color(255, 50, 50, 75));
        g.fillRoundRect(x, y, boxwidth, 104, 15, 15);

        // Calculate levels gained
        levelsGained = skills.getCurrentLvl(index) - startLevel;

        long runmins = mm + (hh * 60);
        Font f = g.getFont(); // Save for restoring after setting title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString("zzZMIRunner v" + props.version()
                + " by " + props.authors()[0], x += 5, y += 14);

        g.setFont(f);
        g.setColor(Color.WHITE);
        g.drawString("Run time: " + hh + ":" + mm + ":" + ss, x += 5, y += 20);
        g.drawString("XP gained: " + expGained, x, y += 12);
        g.drawString("Levels gained: " + levelsGained, x, y += 12);

        // Progress bar from Jacmob's scripts
        g.setColor(Color.RED);
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills.getPercentToNextLvl(index))), 255, 0, 150);
        g.setColor(PERCBAR);
        boxwidth = (boxwidth - x) * skills.getPercentToNextLvl(index) / 100;
        g.fill3DRect(x, y, boxwidth, 15, true);

        g.setColor(Color.WHITE);
        g.drawString("XP to level:  " + skills.getXPToNextLvl(index) + " ("
                + skills.getPercentToNextLvl(index) + "%)", x + 2, y += 12);

        if (runmins > 0 && expGained > 0) {
            float lvlmins = skills.getXPToNextLvl(index)
                    / (expGained / runmins);
            g.drawString("Time to next level: " + (long) lvlmins + " minutes",
                    x, y += 14);
            g.drawString("XP per hour : " + (expGained / runmins) * 60, x,
                    y += 12);
        } else {
            g.drawString("Time to next level: ... minutes", x, y += 14);
            g.drawString("XP per hour : ", x, y += 12);

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
    private boolean antibanPaused = false;

    private void zzAntiban() {
        // Can you say, "Don't ban me!"?
        long currTime = System.currentTimeMillis();
        if (antibanPaused || (nextAntiban > currTime)) {
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

    /**
     * Finds the closest tile in the path based on the player's destination.
     * Walks to the tile by generating the shortest path to it. While this
     * method is walking, false will be returned. When the destination is
     * reached, true will be returned.
     *
     * @param t
     *            The destination tile.
     * @param maxDist
     *            The maximum distance between two tiles in the path. 16 is
     *            recommended.
     * @param x
     *            The x randomness passed to
     *            {@link #walk.tileMM(RSTile, int, int)}.
     * @param y
     *            The y randomness passed to
     *            {@link #walk.tileMM(RSTile, int, int)}.
     * @return <tt>true</tt> if the destination was reached; otherwise
     *         <tt>false</tt>.
     * /
    public boolean walkToClosestTile(RSTile[] t, int maxDist, int x, int y) {
    RSTile next = nextTile(t, 16, false);
    int dist = walk.getDestination().distanceTo();

    if (dist<maxDist && dist>random(3,6)) {
    wait (random(200, 400));
    return false;
    }

    if (next!=null)  {
    if (dist>maxDist || !tileOnMap(next))
    return walkTo(next);

    return walk.tileMM(next,1,1);
    }
    return false;
    }
     */
    public State getState() {
        State st = null;

        // Lunar Island Area - Tele spot to Bank
        if (LUNAR_AREA.contains(player.getMyLocation())) {
            if (inventory.contains(PURE_ESS_ID) && inventory.isFull()) {
                st = State.TELE_OURANIA;
            } else if (MOONCLAN_BANK_SPOT.distanceTo() > 5) {
                st = State.RUN_TO_BANK;
            } else {
                st = State.BANKING;
            }
        } // Chaos Altar Area - Tele spot to Chaos Altar
        else if (CHAOS_AREA.contains(player.getMyLocation())) {
            if (!inventory.contains(PURE_ESS_ID) || !inventory.isFull()) {
                st = State.TELE_MOONCLAN;
                return st;
            }

            if (LADDER_TOP_TILE.isOnScreen()
                    && skills.getCurrentLvl(STAT_PRAYER) > 30) {
                st = State.CLIMB_DOWN;
            } else {
                st = State.RUN_TO_TOP_LADDER;
            }
        } // ZMI Altar Area - Check this first since there
        // may be some overlap between this and the ZMI Area.
        // Otherwise we'll keep trying to run even though we
        // are at the ZMI altar.
        else if (ALTAR_AREA.contains(player.getMyLocation())
                || ZMI_ALTAR_TILE.isOnScreen()) {
            if (inventory.contains(PURE_ESS_ID)) {
                st = State.CRAFT;
            } else {
                st = State.TELE_MOONCLAN;
            }
        } // ZMI Area - Ladder to ZMI Altar
        else if (ZMI_AREA.contains(player.getMyLocation())) {
            if (!inventory.contains(PURE_ESS_ID)) {
                st = State.TELE_MOONCLAN;
            } else {
                st = State.RUN_TO_ZMI_ALTAR;
            }
        }

        return st;
    }
}
