// zzArzinianGoldMiner by zzSleepzz
// Copyright 2009,2010 zzSleepzz
//
// Features:
// - Built in antiban
// - Requires wearing golden helmet
// - Counts extra ores from Varrock Armour as additional rocks
// - Proggy
// - Full banking support, best with ring of charos worn.
//

//TODO add server message from tubbie additional ores to count
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.lazygamerz.scripting.api.Equipment;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"zzSleepzz"},
category = "Mining",
name = "zzArzinianGoldMiner",
version = 2.0,
description = "<html>"
+ "<head><style type=\"text/css\"> body {background-color: #FFEFD5 </style></head>"
+ "<body>"
+ "<center>"
+ "<b><font size=+2 color=\"blue\">zzArzinianGoldMiner v2.0</font></b>"
+ "<br><font color=\"blue\">by zzSleepzz</font>"
+ "<p>"
+ "<i>Special thanks to <b>Bobby Bighoof</b> for helping to work out the path walking "
+ "issues and for his excellent feedback.</i><p>"
+ "<table><tr valign=top>"
+ "<td align=right><b>Instructions:</b></td><td align=left> Start with pick either wielded or in inventory.</td>"
+ "</tr></table>" + "<p><p>" + "</center>" + "</body>\n" + "</html>")
public class zzArzinianGoldMiner extends Script
        implements PaintListener, MessageListener {

    private final ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);
    
    private static final int[] goldRocks = {2098, 2099, 34976, 34977, 5989};
    private static final int boatmanID = 1845;
    private static final int dondakanID = 1836;
    private static final int goldOre = 444;
    private static final int GoldenHelmet = 4567;
    private static final int[] DROP_IDS = {1617, 1619, 1621, 1623, 995};
    private boolean altSet = false;
    // private static final int [] keeperIDs = {1617, goldOre, GoldenHelmet};
    // These two aren't used at the moment. We could work them in
    // to try to speed up mining a bit. The delay between rocks
    // is pretty small, so this isn't really necessary.
    // private static final int [] spentRocks = {33400, 33401, 33402};
    // private static RSTile currentRock;
    private final RSTile[] MINE_TO_BOATMAN = {new RSTile(2825, 10169),
        new RSTile(2835, 10166), new RSTile(2845, 10162),
        new RSTile(2855, 10164), new RSTile(2865, 10167),
        new RSTile(2873, 10166)};
    private final RSTile[] MineToBoatman = walk.cleanPath(walk.fixPath(MINE_TO_BOATMAN));
    private final RSTile[] BoatmanToMine = walk.cleanPath(walk.fixPath(walk.reversePath(MINE_TO_BOATMAN)));
    private int randomRun = random(40, 75);

    // Script States
    private static enum State {

        S_ENTER_MINE, S_EXIT_MINE, S_BANK, S_MINE,
        S_WALK_MINETOBOATMAN, S_WALK_BOATMANTOMINE
    }
    private State state = State.S_MINE;

    protected int getMouseSpeed() {
        return random(6, 9);
    }

    public boolean onStart(Map<String, String> args) {
        altSet = false;

        return true;
    }

    public void onFinish() {
    }

    State getState() {
        State st = State.S_MINE;

        RSTile loc = player.getMyLocation();
        if (loc.getX() >= 2817 && loc.getX() <= 2876) {
            RSNPC d = npc.getNearestByID(dondakanID);
            RSNPC b = npc.getNearestByID(boatmanID);

            if (inventory.getCount() > 26) {
                if (b != null && b.isOnScreen()) {
                    st = State.S_BANK;
                } else {
                    st = State.S_WALK_MINETOBOATMAN;
                }
            } else {
                if (d != null && d.isOnScreen()) {
                    st = State.S_ENTER_MINE;
                } else {
                    st = State.S_WALK_BOATMANTOMINE;
                }
            }
        }
        if (loc.getX() >= 2498 && loc.getX() <= 2618) {
            if (inventory.getCount() > 26) {
                // Check for full inventory and drop an ore if so
                if (inventory.isFull()) {
                    inventory.clickItem(goldOre, "Drop");
                    wait(random(300, 500));
                }

                st = State.S_EXIT_MINE;
            } else {
                st = State.S_MINE;
            }
        }

        return st;
    }

    private RSTile currRockTile=null;
    RSObject rock = null;
    
    public int loop() {
        // Don't allow the script to do ANYTHING unless we are really logged in.
        while (!game.isLoggedIn()) {
            return (100);
        }

        // Does the player have enough energy to run and is the player already
        // running?
        if (player.getMyEnergy() >= randomRun && !isRunning()) {
            // All conditions met, set run mode.
            game.setRun(true);

            randomRun = random(25, 35);
            return random(500, 750);
        }

        zzAntiBan();
        // Always set the antiban pause to true AFTER calling it.
        // If mining, it will set it to false for us.
        pauseAntiban = true;

        // Perform script actions based on state. Don't you love finite state
        // machines?
        try {
            if (player.getMine().getAnimation() != -1) {
                return random(100, 250);
            }

            state = getState();
            debug("State(" + state.toString() + ")");

            // Enter Mine
            if (state.equals(State.S_ENTER_MINE)) {
                if (inventory.getCount() > 26) {
                    state = State.S_WALK_MINETOBOATMAN;
                    return random(30, 40);
                }
                
                if (!inventory.isOpen())  {
                	inventory.open();
                	return random(300,450);
                }
                
                if (inventory.contains(GoldenHelmet))  {
                	if (inventory.clickItem(GoldenHelmet, "Wear "))  {
                		inventory.waitForCount(GoldenHelmet, 0, 3500);
                	}
                	return random(250,350);
                }

                // Talk to Dondakan
                RSNPC dondakan = npc.getNearestByID(dondakanID);
                if (dondakan == null) {
                    return random(50, 100);
                }

                if (dondakan.isOnScreen()) {
                    if (dondakan.action("Fire-into")) {
                        wait(random(4400, 4800));
                        state = State.S_MINE;
                    }
                } else {
                    walk.to(dondakan.getLocation());
                    player.waitToMove(1500);
                }

                return random(300, 400);
                
            // Exit Mine
            } else if (state.equals(State.S_EXIT_MINE)) {
                // Drop any gems that we might have accumulated.
                // If we have gems, just return so we go back to
                // mining again.  Otherwise, carry on with
                // the bank run.
                while (inventory.contains(DROP_IDS)) {
                    debug("Dropping undesireables...");
                    for (int id : DROP_IDS) {
                        debug("Dropping item id: " + id);
                        inventory.clickItem(id, "Drop");
                        wait(random(480, 550));
                    }
                }

                // Now, if we still have room in the inventory, get
                // back to mining.
                if (inventory.getCount() < 27) {
                    return random(100, 200);
                }

                // Go to equipment tab and unequip the helmet
                // Put back on before proceeding.

                // Open the magic tab
                while (game.getCurrentTab() != TAB_EQUIPMENT) {
                    game.openTab(TAB_EQUIPMENT);
                    for (int i = 0; i < 20; i++) {
                        wait(20);
                        if (game.getCurrentTab() != TAB_EQUIPMENT) {
                            break;
                        }
                    }
                }

                RSTile loc = player.getMyLocation();

                // Remove the helmet to exit the mine.
                game.openTab(TAB_EQUIPMENT);
                if (iface.get(INTERFACE_TAB_EQUIPMENT).isValid()) {
                    iface.clickChild(INTERFACE_TAB_EQUIPMENT, Equipment.helmet);
                    wait(random(1400, 1500));

                    if (inventory.getCount(GoldenHelmet) == 0) {
                        return random(100, 200);
                    }
                }

                if (!loc.equals(player.getMyLocation())) {
                    state = State.S_WALK_MINETOBOATMAN;
                }

                return random(200, 300);
                
            // Bank
            } else if (state.equals(State.S_BANK)) {
            	rock = null;
            	currRockTile = null;
                // If we end up in the banking state somehow, like on
                // startup, if we have a nearly full inventory, just
                // do the banking.
                if (inventory.getCount(goldOre) < 21) {
                    state = State.S_WALK_BOATMANTOMINE;
                    return random(30, 40);
                }

                // Talk to boatman to deposit ore
                RSNPC boatman = npc.getNearestByID(boatmanID);
                if (boatman == null) {
                    return random(50, 100);
                }

                // Let's try a more dynamic way where the mouse tracks
                // the boatman until it gets a clear shot at the deliver-gold
                // option.  This should provide for fewer retries and misclicks.
                if (boatman.action("Deliver-gold"))  {
                	iface.waitForOpen(iface.get(243), 1500);
                }
                

                if (iface.get(243).isValid()) {
                    iface.clickChild(243, 7);
                } else {
                    return 100;
                }
                wait(random(1300, 1500));

                if (iface.get(228).isValid()) {
                    iface.clickChild(228, 2);
                } else {
                    return 100;
                }
                wait(random(1300, 1500));

                if (iface.get(64).isValid()) {
                    iface.clickChild(64, 5);
                } else {
                    return 100;
                }
                wait(random(1300, 1500));

                if (iface.get(242).isValid()) {
                    iface.clickChild(242, 6);
                } else {
                    return 100;
                }
                wait(random(1300, 1500));

                if (inventory.getCount() == 0) {
                    state = State.S_WALK_BOATMANTOMINE;
                }

                return random(300, 400);
                
            // Mine gold
            } else if (state.equals(State.S_MINE)) {
                pauseAntiban = false;

                if (!altSet) {
                    camera.setAltitude(true);
                    altSet = true;
                }

                if (!inventory.isOpen())  {
                	inventory.open();
                	return random(100,200);
                }
                
                if (inventory.getCount(goldOre) > 26) {
                    log("Inventory is full, banking.");
                    state = State.S_EXIT_MINE;
                    return random(80,120);
                }

                if (rock==null || rock.getID()!=objects.getTopAt(currRockTile).getID()) {
                    debug("Getting a rock to mine.");
                    rock = objects.getNearestByID(goldRocks);
                    currRockTile = rock.getLocation();

                    return random(80,150);
                }               

                // We only get here if we actually found a rock.
                debug("Found rock (ID=" + rock.getID() + ").  Location="
                        + rock.getLocation().toString());

                if (!player.isIdle()) {
                    return random(100, 250);
                }

                if (rock.isOnScreen()) {               	
                    rock.action("Mine ");
                    if (rock.distanceTo() < 2) {
                        if (player.waitForAnim(1500) != -1) {
                            return random(250, 350);
                        }
                    } else {
                        if (player.waitToMove(1500)) {
                        	return random(250, 350);
                        }
                    }
                } else {
                    walk.tileMM(rock.getLocation());
                    player.waitToMove(1000);
                    wait(random(2000, 2200));
                }

                return random(100, 200);
                
            // Walk to bank
            } else if (state.equals(State.S_WALK_MINETOBOATMAN)) {
                if (!inventory.isOpen())  {
                	inventory.open();
                	return random(500,600);
                }
                
                if (inventory.contains(GoldenHelmet))  {
                	inventory.clickItem(GoldenHelmet, "Wear ");
                	inventory.waitForCount(GoldenHelmet, 0, 2500);
                	return random(500,600);
                }
                
            	if (inventory.getCount(goldOre) < 20) {
                    state = State.S_WALK_BOATMANTOMINE;
                    return random(50, 100);
                }

            	RSTile dest = walk.getDestination();
                if (dest!=null && dest.distanceTo() > random(4,6)) {
                    return (random(200, 400));
                }

                // Put the walking into a try block to catch the NPE that
                // we'll get at the end of the path. This way we
                // can move on to the NPC check which will change the
                // script state.
                try {
                    if (!walk.pathMM(MineToBoatman)) {
                        // If false was returned, we could not reach the
                        // next tile because the bot thinks it is too
                        // far away. Therefore, get the next
                        // closest tile in the path and use walk.to
                        // to jumpstart the walking.
                        walk.to(walk.nextTile(MineToBoatman, 16, false));
                        player.waitToMove(1500);
                    }
                } catch (final Exception e) {
                }

                RSNPC b = npc.getNearestByID(boatmanID);

                if (b != null && b.isOnScreen()) {
                    state = State.S_BANK;
                    return random(50, 100);
                }
                
            // Walk to mine
            } else if (state.equals(State.S_WALK_BOATMANTOMINE)) {
                if (inventory.getCount() > 26) {
                    state = State.S_WALK_MINETOBOATMAN;
                    return random(50, 100);
                }

                RSTile dest = walk.getDestination();
                if (dest!=null && dest.distanceTo() > random(4, 6)) {
                    return (random(200, 400));
                }

                // Put the walking into a try block to catch the NPE that
                // we'll get at the end of the path. This way we
                // can move on to the NPC check which will change the
                // script state.
                try {
                    if (!walk.pathMM(BoatmanToMine)) {
                        // If false was returned, we could not reach the
                        // next tile because the bot thinks it is too
                        // far away. Therefore, get the next
                        // closest tile in the path and use walk.to
                        // to jumpstart the walking.
                        walk.to(walk.nextTile(BoatmanToMine, 16, false));
                        player.waitToMove(1500);
                    }
                } catch (final Exception e) {
                }

                RSNPC d = npc.getNearestByID(dondakanID);

                if (d != null && d.isOnScreen()) {
                    state = State.S_ENTER_MINE;
                    return random(50, 100);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return random(500, 750);
    }
    // Proggy originally based on ProFisher2's proggy
    // Modified and enhanced by zzSleepzz
    private long scriptStartTime = 0;
    private int startXP = 0, lastXP = 0;
    private int startLevel = 0;
    private static final int index = STAT_MINING;
    private int rocks = 0;

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
        int y = 185;
        int boxwidth = 180;

        g.setColor(new Color(255, 50, 50, 75));
        g.fillRoundRect(x-7, y, boxwidth, 130, 15, 15);
        g.setColor( Color.RED );
        g.drawRoundRect(x-8, y, boxwidth, 130, 15, 15);
        g.setColor( Color.GREEN );
        g.drawRoundRect(x-9, y-1, boxwidth+2, 132, 15, 15);
        g.setColor( Color.BLUE );
        g.drawRoundRect(x-10, y-2, boxwidth+4, 134, 15, 15);
        g.drawRoundRect(x-11, y-3, boxwidth+6, 136, 15, 15);

        long runmins = mm + (hh * 60);
        Font f = g.getFont(); // Save for restoring after setting title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString(props.name() + "  v" + props.version(), x, y += 13);

        g.setFont(f);
        g.setColor(Color.ORANGE);
        g.drawString("  by " + props.authors()[0], x, y += 13);

        g.setColor(Color.WHITE);
        g.drawString("Run time:  " + hh + ":" + mm + ":" + ss, x, y += 18);
        g.drawString("Ores mined: " + rocks, x, y += 13);
        g.drawString("XP gained: " + expGained, x, y += 13);
        g.drawString("Levels gained: " + levelsGained, x, y += 13);

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
            float lvlmins = skills.getXPToNextLvl(index) / (expGained / runmins);
            int lvlhrs = (int) lvlmins / 60;
            g.drawString("Level " + (skills.getCurrentLvl(index) + 1) + " in: "
                    + lvlhrs + " hrs, " + (int) (lvlmins - (lvlhrs * 60)) + " mins", x, y += 14);
            g.drawString("XP per hour : " + (expGained / runmins) * 60, x, y += 13);
        } else {
            g.drawString("Level " + (skills.getCurrentLvl(index) + 1) + " in: ", x, y += 14);
            g.drawString("XP per hour : ", x, y += 13);
        }
        
        if (rock!=null)  {
        	rock.drawModel(g);
        }
    }

    // ============= Antiban Support ====================
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

        HoverPlayer, ClickPlayer, SkillsTab, FriendsTab, InventoryTab
    }
    private final Actions lastAction = Actions.SkillsTab;
    private long antibanTriggerTime = System.currentTimeMillis() + random(90000, 120000);
    private boolean pauseAntiban = true;

    private void zzAntiBan() {

        // See if the 90-120 second interval has expired. If not
        // return without doing anything. Otherwise, set the next
        // antiban interfal and proceed with antiban.
        long currTime = System.currentTimeMillis();
        if (antibanTriggerTime < currTime && !pauseAntiban) {
            // Set to go off again in another 60-90 secs.
            antibanTriggerTime = currTime + random(90000, 120000);
        } else {
            return;
        }

        debug("zzAntiban activated.");

        int r = random(1, 85);
        Point mousePos = mouse.getLocation();

        if (r < 20) { // SkillsTab
            if (lastAction == Actions.SkillsTab) {
                zzAntiBan(); // retry for diff action.
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
                RSInterfaceChild tab = iface.get(320).getChild(
                        139);
                game.openTab(Constants.TAB_STATS);

                mouse.move(new Point(tab.getAbsoluteX()
                        + random(2, tab.getWidth() - 1), tab.getAbsoluteY()
                        + random(2, tab.getHeight() - 1)));
                wait(random(1000, 2000));
            }

        } else if (r < 40) { // FriendsTab
            if (lastAction == Actions.FriendsTab) {
                zzAntiBan(); // retry for diff action.
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
                wait(random(1500, 1800));
            }

        } else if (r < 80) {
            camera.setRotation(camera.getAngle() + random(-20, 20));
        } else if (r < 101) {
            camera.setAltitude(random(85, 100));
        }

        wait(random(800, 950));
        if (!inventory.isOpen()) {
            game.openTab(TAB_INVENTORY);
        }
        wait(random(250, 350));
        mouse.move(mousePos, 40, 40);
        wait(random(300, 500));
    }

    public void messageReceived(MessageEvent e) { // Searches for
        // Messages in
        // chatbox
        String msg = e.getMessage();

        if (msg.contains("manage to mine")) {
            rocks++;
        }
        if (msg.contains("additional ore")) {
            rocks++;
        }
        if (msg.contains("too full"))  {
        	state = State.S_EXIT_MINE;
        }
    }
}
