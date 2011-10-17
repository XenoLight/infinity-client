// zzChinerator by zzSleepzz
//
// This is a hunter script based on Marneus901's Red Chinchompa Hunter.  Credits to
// elements Marneus901 used from other scripts that are still in this script remain,
// mainly to the atTileTake method.
//
// Start this script anywhere you can box trap without bait.  It does not have ANY
// fixed locations...it works based on where you are standing when the script is
// started.  Make sure you have plenty of box traps in your inventory in case you
// need extras.  Turn off break handler or you will eventually lose ALL your traps.
//
// The script will not attempt to lay traps in tiles that already have traps or other
// 

// v3.1  20July2011 - Switched to using tile.click instead of homegrown atTileTake
// v3.03 26Jun2011 - Updated for new Infinity API
// v3.01 22Dec2010 - updated for Infinity on 4/4/2011
//  - Added intentional, randomized menu misclicking when taking traps
//
// v3.00 22Dec2010
//  - Add support for leaf-trapping jadinkos
//  - Further corrections to queue handling to prevent loss of traps
//
// v2.00 4May2010
//  - Trap states managed using queues to minimize full traps falling
//    over before being checked.
//
// v1.53 15Apr2010
//  - Disables break handler
//
// v1.52 29Mar2010
//  - Some speed improvements
//  - Added code to allow laying of traps over non-interactable
//    
//
// v1.51 12Jan2010
//  - Minor adjustments to processing.
//  - Fixed the MM clicks to walk to tile
//
// v1.5 1Jan2010
//	- Updated to pickup (not lay) any traps in a location when there are more
//    than one object or item present.
// v1.4 31Dec2009
//  - Added check for no traps in inventory, stopping the script if there are none.
// v1.3 28Dec2009
//  - Additional degug logging
//  - Attempts to adjust atTileTake to fix hangs
//
// v1.2 27Dec2009
//  - Corrected to work for grey chins
//
// v1.1 17Dec2009
//	- Picks up traps when script stops.
//  - Resets trap immediately after check or dismantle

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;


//TODO: Fix the onFinish/PrepareBreak to ensure they pick up all the traps.

@ScriptManifest(
        authors = {"zzSleepzz"},
        category = "Hunter",
        name = "zzChinerator",
        version = 3.1,
        description =
        	"<html>"
          + "<head><style type='text/css'> body {background-color: #FFEFD5 </style></head>"
          + "<body>"
          + "<center>"
          + "<b><font size=+2 color='blue'>zzChinerator v3.1</font></b>"
          + "<br><font color='blue'>by zzSleepzz</font>"
          + "<p>"
          + "<font size=-1>Based on an early version of the Red Chinchompa Catcher by Marneus901</font>"
          + "<p>"
          + "<table>"
          + "<tr valign=top>"
          + "<td align=right><b>Instructions:</b></td><td align=left>Stand in desired location with sufficient box traps in your inventory."
          + "  Traps anything that uses a box trap and does NOT require bait.</b></td>"
          + "</tr></table>" + "</center>" + "</body>\n" + "</html>")
          
public class zzChinerator extends Script implements PaintListener, MessageListener {
    private final ScriptManifest props = getClass()
            .getAnnotation(ScriptManifest.class);

    private boolean breakHandlerState;
    
    private static final int LEAF_TRAP_ID = 19965;
    private static final int BOX_TRAP_ID = 10008;
    private static final int[] TRAP_IDS = { BOX_TRAP_ID, LEAF_TRAP_ID };
    private static final int[] ACTIVE_TRAP_IDS = { 19187, 56806 };
    private static final int[] FALLEN_TRAP_IDS = { 19192,  56807, 56810, 56812, 56813 };
    private static final int[] FULL_TRAP_IDS = 
	    { 19189, 19190, 19194, 19195, 19196, 56819, 56820, 56824,
	      56830,56832
	    };
    
	private final ArrayList<Integer> trapIDs = new ArrayList<Integer>();    
	private final ArrayList<Integer> fullTrapIDs = new ArrayList<Integer>();
	private final ArrayList<Integer> activeTrapIDs = new ArrayList<Integer>();
	private final ArrayList<Integer> fallenTrapIDs = new ArrayList<Integer>();

    private static final int[] OK_SPOT_IDS = 
      	{19824,19825,19826,19827,19828,19829,19830,19831,19832,19833,19834,19835,19836,19837,
    	 56767,56768,56770,56771,56773,56776,56777,56778,56779,56780,56791,56793};

    private ArrayList<Integer> okSpots = new ArrayList<Integer>(); 

	private class BoxLocation extends RSTile  {
		public boolean queued;
		
		public BoxLocation()  {
			super(-1,-1);
			queued = false;
		}
		
		public BoxLocation(int x, int y) {
			super(x, y);
			queued = false;
		}
		
	}
    private final BoxLocation[] boxLocations = new BoxLocation[5];

    private Queue<BoxLocation> fullQ = new LinkedList<BoxLocation>();
    private Queue<BoxLocation> emptyQ = new LinkedList<BoxLocation>();
    private Queue<BoxLocation> fallenQ = new LinkedList<BoxLocation>();
    private Queue<BoxLocation> newQ = new LinkedList<BoxLocation>();
	
    private RSTile trapCenterSpot;
    private RSObject tileObjects;

    private int numLocs = 0;

    private final int[] dx = new int[5];
    private final int[] dy = new int[5];

    protected int getMouseSpeed() {
        return random(5, 9);
    }

    /**
     * ****************************
     * Thanks to Drizzt1112's Swamp Lizard Hunter script for this function. It
     * is very useful!
     *
     * @param RSTile
     * @param action
     * @return *****************************
     */
/*    
    boolean atTileTake(RSTile tile, String action) {
        int ct = 0;
        boolean rv;

        while (ct < 20 && player.getMine().isMoving()) {
            wait(100);
            ct++;
        }
        try {
            Point location = Calculations.tileToScreen(tile);
            if (location.x == -1 || location.y == -1)
                return false;

        	if (action.toLowerCase().contains("lay"))  {
        		ct=0;
        		while (ct++<10 && !menu.getItems()[0].toLowerCase().contains(
                        action.toLowerCase()))  {
        			mouse.move(location.x+random(12,20),location.y+random(2,8));
        		}
        	} 
        	else  {
        		mouse.move(location.x+random(5,8),location.y+random(-2,4));
        	}
        	wait(random(50, 100));
            ct++;

            String dflt = menu.getItems()[0].toLowerCase();
            
            if (dflt.contains("pick"))
            	action = "Pick";
            
            debug("Mouse moved to new point. Action ("+action+") found: "
                    + menu.getItems()[0].toLowerCase().contains(
                    action.toLowerCase()));

            rv = menu.action(action);

            wait(random(50, 100));
            return rv;
        } catch (Exception e) {
            return false;
        }
    }
*/
    void ifLost() {
        int myX = player.getMine().getLocation().getX();
        int myY = player.getMine().getLocation().getY();
        int ctrX = trapCenterSpot.getX();
        int ctrY = trapCenterSpot.getY();

        if (myX < ctrX - 4
                || myX > ctrX + 4
                || myY < ctrY - 4
                || myY > ctrY + 4) {
            // Find a way to teleport to CW then walk back
            if (!walk.tileMM(trapCenterSpot)) {
                log("Were are not at the Red Chinchompa hunting spot.");
                stopScript();
            }
        }
    }

    public int loop() {
        if (!game.isLoggedIn()) {
            wait(1000);
            return random(250, 500);
        }
        ifLost();

        if (game.getCurrentTab()!=Game.tabInventory)
            game.openTab(Game.tabInventory);

        skills.getCurrentLvl(STAT_HUNTER);

        
        // Phase 1: Examine each tile location to determine its status.
        // First check all for fallen, then full, then empty, queueing 
        // a trap to the serviceTrapQ if it is one of those states.
        // This process will result in traps being processed in the order
        // they were inserted into the queue.
        
        // 1a: Fallen traps
        debug("Checking fallen traps (and empty ones for herblore habitat)");
        for (int i = 0; i < numLocs; ++i) {
            List<RSGroundItem> itemTile = ground.getItemsAt(boxLocations[i]);
            if (itemTile != null) {
            	debug("\tProcessing items on ground at "+boxLocations[i].toString());
                for (int k = 0; k < itemTile.size(); ++k) {
                    if (trapIDs.contains(itemTile.get(k).getItem().getID())) {
                    	debug("\t\tQueued fallen trap at "+boxLocations[i].toString());
                    	boxLocations[i].queued=true;
                    	fallenQ.add(boxLocations[i]);
                    	continue;
                    }
                }
            }
        }

        // 1b: Full traps
        debug("Checking full traps.");
        for (int i = 0; i < numLocs; ++i) {
            tileObjects = objects.getTopAt(boxLocations[i]);
            if (!boxLocations[i].queued && tileObjects != null
                    && fullTrapIDs.contains(tileObjects.getID())
                    && !inventory.isFull()) {
            	debug("\tQueued full trap at "+boxLocations[i].toString());
            	boxLocations[i].queued=true;
            	fullQ.add(boxLocations[i]);
            }
        }

        // 1c: Queue traps for empty spots
        debug("Checking traps for empty spots");
        for (int i = 0; i < numLocs; ++i) {
            tileObjects = objects.getTopAt(boxLocations[i]);
            int to_id = -1;
            
            if (tileObjects!=null)  {
            	to_id = tileObjects.getID();
            } 
			if (!boxLocations[i].queued && tileObjects==null || (to_id!=-1 && okSpots.contains(to_id))) {
            	debug("\tQueued new trap for empty spot at "+boxLocations[i].toString());
				boxLocations[i].queued=true;
				newQ.add(boxLocations[i]);
            }
        } 
        
        // 1d: Empty traps
        debug("Checking empty traps");
        for (int i = 0; i < numLocs; ++i) {
            tileObjects = objects.getTopAt(boxLocations[i]);
            if (!boxLocations[i].queued && tileObjects != null && fallenTrapIDs.contains(tileObjects.getID())
            	&& !inventory.isFull()) {
            	debug("\tQueued empty trap at "+boxLocations[i].toString());

            	boxLocations[i].queued=true;
            	emptyQ.add(boxLocations[i]);
            }
        }
        
        // First check for fallen traps and set them back up, unless there
        // is already an empty, set or full trap in the same location.
        debug("Processing fallenQ");
        for (BoxLocation t : fallenQ) {
            List<RSGroundItem> itemTile = ground.getItemsAt(t);
            if (itemTile != null) {
            	            	
                for (int k = 0; k < itemTile.size(); ++k) {
                    if (trapIDs.contains(itemTile.get(k).getItem().getID())) {
                        // Determine if there is an object in the same location,
                        // if so, it must be another trap, so we'll pick
                        // everything up and lay a new trap.
                        RSObject trap = objects.getTopAt(t);

                        int ct = 0;
                        while (ct < 5 && trap!=null && 
                        		fallenTrapIDs.contains(trap.getID())) {
                            if (!tile.click(t, "Dismantle"))
                            	tile.click(t, "Take");

                            if (t.distanceTo() > 1) {
                                debug("\ttaking compound trap: waiting to move.");
                                if (player.waitToMove(1500))
                                    waitWhileMoving();
                                debug("\ttaking compound trap: reached trap");
                            }

                            if (player.waitForAnim(1000) != -1)
                                waitDuringAnim();

                            trap = objects.getTopAt(t);

                            ct++;
                        }

                        // If there is only one item on the ground, just set
                        // the trap.
                        if (itemTile.size() == 1) {
//                            if (!atTileTake(t, "Lay")) {
                          if (!tile.click(t, "Lay")) {

                            	t.queued = false;
                                return random(50, 100);
                            }

                            if (t.distanceTo() > 1) {
                                debug("\tLaying fallen trap: waiting to move.");
                                if (player.waitToMove(1500))
                                    waitWhileMoving();
                                debug("\tLaying fallen trap: reached trap");
                            }
                            if (player.waitForAnim(1000) != -1)
                                waitDuringAnim();

                            wait(random(100, 200));
                        }

                        // Otherwise, take any fallen traps that are now
                        // viewed as items.
                        else if (!tile.click(t, "Take")) {
                            return random(50, 100);
                        }

                        if (t.distanceTo() > 1) {
                            debug("\ttaking fallen trap: waiting to move.");
                            if (player.waitToMove(1500))
                                waitWhileMoving();
                            debug("\ttaking fallen trap: reached trap");
                        }
                        if (player.waitForAnim(1000) != -1)
                            waitDuringAnim();

                    	t.queued = false;
                        wait(random(100, 200));
                    }
                }
            }
        }

        // Then handle any full traps
        debug("Processing fullQ");
        for (BoxLocation t: fullQ) {
            tileObjects = objects.getTopAt(t);
            if (tileObjects != null
                    && fullTrapIDs.contains(tileObjects.getID())
                    && !inventory.isFull()) {

                if (!tile.click(t, "Check"))  {
                	return random(50, 80);
                }
            	t.queued = false;

                if (t.distanceTo() > 1)
                    player.waitToMove(500);

                if (player.waitForAnim(2000) != -1)

                wait(random(800, 900));
                setupBox(t);
                return random(100, 200);
            }
        }

        // Then setup boxes in empty locations
        debug("Processing newQ");
        for (BoxLocation t: newQ) {
            tileObjects = objects.getTopAt(t);
			if (tileObjects==null  || okSpots.contains(tileObjects.getID())) {
                setupBox(t);
            	t.queued = false;

                return random(100, 200);
            }
        }

        // Then dismantle any tripped traps that haven't fallen yet.
        debug("Processing emptyQ");
        for (BoxLocation t: emptyQ) {
            tileObjects = objects.getTopAt(t);
            if (tileObjects!=null && fallenTrapIDs.contains(tileObjects.getID())
                    && !inventory.isFull()) {
                if (!tile.click(t, "Dismantle")) {
                    return random(50, 80);
                }

            	t.queued = false;
            	
                if (t.distanceTo() > 1)
                    player.waitToMove(500);
                player.waitForAnim(1000);

                wait(random(800, 950));
                setupBox(t);
                return random(100, 200);
            }
        }

        return random(100, 200);
    }

    public void onFinish() {

        // Note the use of i setup to be 3 times the maximum
        // number of traps and the use of modulo 5 (%5) to
        // guarantee a proper index. This is done to provide
        // for 5 passes through the trap set in order to ensure
        // they all get picked up.
        if (player.waitForAnim(500) != -1)
            waitDuringAnim();
        wait(random(500, 600));

        int ct = 0;
        while (ct < 20 && !player.isIdle()) {
            ct++;
            wait(100);
        }

        
        for (int i=0; i<5; i++)  {
            int start_boxct = inventory.getCount(TRAP_IDS);
	
	        while (start_boxct==inventory.getCount(TRAP_IDS)) {
	            if (boxLocations[i] != null) {
	            	while(!player.isIdle())  {
	            		wait(250);
	            	}
	            	
	                // Handle empty trap
	                tileObjects = objects.getTopAt(boxLocations[i]);
	                
	                if (tileObjects != null &&
	                    (fallenTrapIDs.contains(tileObjects.getID()) || 
	                     activeTrapIDs.contains(tileObjects.getID())) && 
	                     !inventory.isFull()) {
	                	tile.click(boxLocations[i], "Dismantle");
	                    if (boxLocations[i].distanceTo() > 1)
	                        player.waitToMove(500);
	                    player.waitForAnim(1000);
	                    wait(random(800, 950));
	                    continue;
	                }
	
	                // Handle full trap
	                if (tileObjects != null
	                        && fullTrapIDs.contains(tileObjects.getID())
	                        && !inventory.isFull()) {
	                	tile.click(boxLocations[i], "Check");
	                    if (boxLocations[i].distanceTo() > 1)
	                        player.waitToMove(500);
	                    if (player.waitForAnim(2000) != -1)
	                        waitDuringAnim();
	                    wait(random(500, 600));
	                    continue;
	                }
	
	                // Handle fallen trap
	                List<RSGroundItem> itemTile = ground.getItemsAt(boxLocations[i]);
	                if (itemTile != null) {
	                    for (int k = 0; k < itemTile.size(); ++k) {
	                        if (trapIDs.contains(itemTile.get(k).getItem().getID())) {
	                        	tile.click(boxLocations[i], "Take");
	                            if (boxLocations[i].distanceTo() > 1) {
	                                if (player.waitToMove(1500))
	                                    waitWhileMoving();
	                            }
	                            if (player.waitForAnim(2000) != -1)
	                                waitDuringAnim();
	                            wait(random(400, 500));
	                            continue;
	                        }
	                    }
	                }
	            }
        	}
        }

		Bot.disableBreakHandler = breakHandlerState;
		
        return;
    }

    // Proggy originally based on ProFisher2's proggy
    // Modified and enhanced by zzSleepzz

    private long scriptStartTime = 0;
    private int startXP = 0, lastXP = 0;
    private int startLevel = 0;
    private static final int index = STAT_HUNTER;
    private int catches = 0;

    public void onRepaint(Graphics g) {
    	
        Color PERCBAR = new Color(255, 255, 0, 150);

        long runTime = 0;
        long ss = 0, mm = 0, hh = 0;
        int expGained = 0;
        int levelsGained = 0;

        if (!game.isLoggedIn())
            return;

        if (scriptStartTime == 0)
            scriptStartTime = System.currentTimeMillis();

        if (lastXP == 0)
            lastXP = skills.getCurrentXP(index);

        if (startXP == 0)
            startXP = skills.getCurrentXP(index);

        if (startLevel == 0)
            startLevel = skills.getCurrentLvl(index);

        // Mark the player's traps
        for (int i = 0; i < 5; ++i) {
            if (boxLocations[i] == null)
                continue;

            List<RSGroundItem> itemTile = ground.getItemsAt(boxLocations[i]);
            RSObject obj = objects.getTopAt(boxLocations[i]);
            Point p1;
            int id, x, y;
            RSTile t;

            if (obj != null) {
                id = obj.getID();

                p1 = obj.getLocation().getScreenLocation();
                t = new RSTile(obj.getLocation().getX() + 1, obj.getLocation()
                        .getY());

                x = (int) (p1.getX()-8);
                y = (int) (p1.getY()-14);

                if (activeTrapIDs.contains(id)) {
                    g.setColor(new Color(25, 255, 25, 175));
                    g.drawRoundRect(x, y, 20, 20, 3, 3);
                    g.drawRoundRect(x+1, y+1, 18, 18, 3, 3);
                } else if (fullTrapIDs.contains(id)) {
                    g.setColor(new Color(25, 25, 255, 175));
                    g.drawRoundRect(x, y, 20, 20, 3, 3);
                    g.drawRoundRect(x+1, y+1, 18, 18, 3, 3);
                } else if (fallenTrapIDs.contains(id)) {
                    g.setColor(new Color(200, 100, 50, 175));
                    g.drawRoundRect(x, y, 20, 20, 3, 3);
                    g.drawRoundRect(x+1, y+1, 18, 18, 3, 3);
                    g.drawRoundRect(x+2, y+2, 16, 16, 3, 3);
                }
            }

            if (itemTile != null) {
                for (int k = 0; k < itemTile.size(); ++k) {
                	RSGroundItem item = itemTile.get(k);
                    t = item.getLocation();
                    id = item.getID();

                    if (trapIDs.contains(id)) {
                        p1 = t.getScreenLocation();
                        t = new RSTile(t.getX() + 1, t.getY());

                        x = (int) (p1.getX() - 4);
                        y = (int) (p1.getY() - 10);

                        g.setColor(new Color(255, 25, 25, 175));
                        g.drawRoundRect(x, y, 20, 20, 3, 3);
                        g.drawRoundRect(x+1, y+1, 18, 18, 3, 3);
                    }
                }
            }
        }

        // Calculate current runtigetMyPlayer().
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
        g.fillRoundRect(x - 7, y, boxwidth, 138, 15, 15);
        g.setColor(new Color(215, 40, 40, 250));
        g.drawRoundRect(x - 7, y, boxwidth, 138, 15, 15);
        g.drawRoundRect(x - 6, y - 1, boxwidth, 140, 15, 15);

        long runmins = mm + (hh * 60);
        Font f = g.getFont(); // Save for restoring after settings title
        Font title = f.deriveFont(Font.BOLD + Font.ITALIC);
        g.setFont(title);
        g.setColor(Color.ORANGE);
        g.drawString(props.name() + "  v" + props.version(), x, y += 13);

        g.setFont(f);
        g.setColor(Color.ORANGE);
        g.drawString("  by " + props.authors()[0], x, y += 13);

        g.setColor(Color.WHITE);
        g.drawString("Run time:  " + hh + ":" + mm + ":" + ss, x, y += 18);
        g.drawString("Catches: " + catches, x, y += 13);
        g.drawString("XP gained: " + expGained, x, y += 13);
        g.drawString("Levels gained: " + levelsGained, x, y += 13);

        // Progress bar from Jacmob's scripts
        g.setColor(Color.RED);
        g.fill3DRect(x, y += 3, boxwidth - x, 15, true);
        PERCBAR = new Color((int) (255 - Math.floor(2.55 * (double) skills
                .getPercentToNextLvl(index))), 255, 0, 150);
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
                    x, y += 13);
            g.drawString("XP per hour : " + (expGained / runmins) * 60, x,
                    y += 13);
        } else {
            g.drawString("Time to next level: ", x, y += 13);
            g.drawString("XP per hour : ", x, y += 13);
        }
    }

    public boolean onStart(Map<String, String> args) {

        while (!game.isLoggedIn()) {
            wait(100);
        }

        camera.setAltitude(true);

        System.currentTimeMillis();
        
        for (int id: TRAP_IDS)
	        trapIDs.add(id);
        for (int id: FULL_TRAP_IDS)
	        fullTrapIDs.add(id);
        for (int id: ACTIVE_TRAP_IDS)
        	activeTrapIDs.add(id);
        for (int id: FALLEN_TRAP_IDS)
        	fallenTrapIDs.add(id);
        for (int id : OK_SPOT_IDS)
        	okSpots.add(id);

		// Turn off break handler
		breakHandlerState = Bot.disableBreakHandler;
		Bot.disableBreakHandler = true;

        setCoords();

        // Setup the initial traps
        for (int i = 0; i < numLocs; ++i) {
            if (boxLocations[i] != null) {
            	boxLocations[i].queued = true;
            	newQ.add(boxLocations[i]);
                tileObjects = objects.getTopAt(boxLocations[i]);
            }
        }

        log("Script initialized, starting regular operations.");
        return true;
    }

    @Override
    public void messageReceived(MessageEvent e) {
        String m = e.getMessage();
        if (m.contains("caught a"))
            catches++;
        else if (m.contains("can't have more than") || m.contains("can't lay")) {
        	tile.click(player.getMine().getLocation(), "Take");
            if (player.waitForAnim(2000) != -1)
                waitDuringAnim();
        }
    }

    /**
     * ****************************
     * Sets up the RSTiles that will be used for setting up box traps. Location
     * formation is based on the level of the player. 80 - 5 traps 60 - 4 traps
     * 40 - 3 traps 20 - 2 traps
     */
    void setCoords() {
        int huntlvl = skills.getCurrentLvl(STAT_HUNTER);

        // Setup different patterns based on level
        // 1 trap - current spot
        // 2 traps - next to each other
        // 3 traps - x
        // x x
        // 4 traps - x
        // x x
        // x
        // 5 traps - x x
        // x
        // x x
        // zzSleepzz updated to use the current spot
        int trapcount = inventory.getCount(TRAP_IDS);
        if (trapcount == 0)
            stopScript(false);

        // Determine number of traps that can be set and initialize
        // the dx/dy arrays used to determine trap placement relative
        // to the player's current location.
        if ((huntlvl >= 80) && trapcount > 4) {
            numLocs = 5;
            dx[0] = 0;
            dx[1] = -1;
            dx[2] = -1;
            dx[3] = 1;
            dx[4] = 1;
            dy[0] = 0;
            dy[1] = -1;
            dy[2] = 1;
            dy[3] = 1;
            dy[4] = -1;

        } else if ((huntlvl >= 60) || trapcount == 4) {
            numLocs = 4;
            dx[0] = 0;
            dx[1] = -1;
            dx[2] = 1;
            dx[3] = 0;
            dy[0] = -1;
            dy[1] = 0;
            dy[2] = 0;
            dy[3] = 1;
        } else if ((huntlvl >= 40) || trapcount == 3) {
            numLocs = 3;
            dx[0] = 0;
            dx[1] = -2;
            dx[2] = 0;
            dy[0] = 0;
            dy[1] = 0;
            dy[2] = 1;

        } else if ((huntlvl >= 20) || trapcount == 2) {
            numLocs = 2;
            dx[0] = 0;
            dx[1] = -2;
            dy[0] = 0;
            dy[1] = 0;

        } else if (huntlvl < 20 || trapcount == 1) {
            numLocs = 1;
            dx[0] = 0;
            dy[0] = 0;
        }

        // Initialize the array of locations to nulls.
        for (int i = 0; i < 5; i++)
            boxLocations[i] = null;

        trapCenterSpot = player.getMine().getLocation();
        int x = trapCenterSpot.getX();
        int y = trapCenterSpot.getY();

        for (int i = 0; i < numLocs; i++) {
            // Now setup the location tile, see if we can find a
            // reasonable close empty spot. There is a possibility
            // that the same tile could be assigned to more than
            // one box if a poor location is chosen.
			RSObject o = objects.getTopAt(x,y);
			if (o==null || okSpots.contains(o.getID()))
                boxLocations[i] = new BoxLocation(x + dx[i], y + dy[i]);
            else
                for (int ddx = dx[i] - 1; ddx < dx[i] + 2
                        && boxLocations[i] == null; ddx++) {
                    for (int ddy = dy[i] - 1; ddy < dy[i] + 2
                            && boxLocations[i] == null; ddy++) {
                        if (objects.getTopAt(x + ddx, y + ddy) == null) {
                            boxLocations[i] = new BoxLocation(x + ddx, y + ddy);
                        }
                    }
                }
        }

        log("Trap locations setup for " + numLocs + " traps");
        for (int i = 0; i < 5; i++)
            if (boxLocations[i] != null)
                debug(" " + i + ": " + boxLocations[i].toString());
            else
                debug(" " + i + ": null");
    }

    void waitDuringAnim() {
        int ct = 0;

        while (ct < 30 && player.getMine().getAnimation() != -1) {
            wait(100);
            ct++;
        }
    }

    void waitWhileMoving() {
        int ct = 0;
        while (ct < 30 && player.getMine().isMoving()) {
            wait(100);
            ct++;
        }
    }

    /********************
     * Sets up a box trap
     *
     * @param index
     * @return
     */
    private int setupBox(RSTile t) {
    	for (int i = 0; i < 10; i++) {

    		if (!player.getMyLocation().equals(t)) {
    			// Add some intentional misclick
    			int r = random(0,1000);
    			if (r<10) {
    				tile.click(t, "Examine");
    				wait(random(80,120));
    			}
    			else if (r<20)  {
    				tile.click(t, "Check");
    				wait(random(80,120));
    			}
    			tile.click(t, "Walk");
    			if (player.waitToMove(900))
    				waitWhileMoving();
    		}
    		else  {
    			RSObject o = objects.getTopAt(t);
    			if ((o==null || okSpots.contains(o.getID()) &&
    				player.getMyLocation().equals(t))) {

	    			// Just in case the player happened to be moving.
	    			waitWhileMoving();
	
	    			if (inventory.containsOneOf(TRAP_IDS)) {
	    				debug("Setting up box " + t.toString());
	    				int trap_id=-1;
	    				
	    				for (int id  : TRAP_IDS)  {
	    					if (inventory.containsOneOf(id))  {
	    						trap_id = id;
	    						break;
	    					}
	    				}
	    				
	    				while (!inventory.clickItem(trap_id, "Lay"))
	    					wait(20);
	    				if (player.waitForAnim(2000) != -1) {
	    					debug("\tLay new: animation started");
	    					waitDuringAnim();
	
	    					debug("\tLay new: animation ended");
	    					if (player.waitToMove(1250)) {
	    						debug("\tLay new: move started");
	    						// wait(random(100,190));
	    						// debug("Lay new: move ended");
	    					}
	    				}
	    				return random(100, 150);
	    			}
    			}
    		}
    	}

    	return random(5, 15);
    }

    /**
     * Method called by the break handler random to allow the script to prepare for a
     * pending break.  Trappers use this to pick up traps before the break.
     */
/*	@Override
	public boolean PrepareBreak()  {
		// Note the use of i setup to be 3 times the maximum
		// number of traps and the use of modulo 5 (%5) to
		// guarantee a proper index. This is done to provide
		// for 5 passes through the trap set in order to ensure
		// they all get picked up.
		if (player.waitForAnim(500) != -1)
			waitDuringAnim();
		wait(random(500, 600));

		int ct = 0;
		while (ct < 20 && !player.isIdle()) {
			ct++;
			wait(100);
		}

		for (int i = 0; i < 25; i++) {
			if (boxLocations[i % 5] != null) {

				// Handle empty trap
				tileObjects = objects.getTopAt(boxLocations[i % 5]);
				if (tileObjects != null
						&& (tileObjects.getID() == failed_trap_id || tileObjects
								.getID() == active_trap_id) && !inventory.isFull()) {
					atTileTake(boxLocations[i % 5], "Dismantle");
					if (distanceTo(boxLocations[i % 5]) > 1)
						player.waitToMove(500);
					player.waitForAnim(1000);
					wait(random(800, 950));
					continue;
				}

				// Handle full trap
				if (tileObjects != null
						&& fullTrapID.contains(tileObjects.getID())
						&& !inventory.isFull()) {
					atTileTake(boxLocations[i % 5], "Check");
					if (distanceTo(boxLocations[i % 5]) > 1)
						player.waitToMove(500);
					if (player.waitForAnim(2000) != -1)
						waitDuringAnim();
					wait(random(500, 600));
					continue;
				}

				// Handle fallen trap
				RSGroundItem[] itemTile = ground.getGroundItemsAt(boxLocations[i % 5]);
				if (itemTile != null) {
					for (int k = 0; k < itemTile.length; ++k) {
						if (itemTile[k].getItem().getID() == trap_id) {
							atTileTake(boxLocations[i % 5], "Take");
							if (distanceTo(boxLocations[i % 5]) > 1) {
								if (player.waitToMove(1500))
									waitWhileMoving();
							}
							if (player.waitForAnim(2000) != -1)
								waitDuringAnim();
							wait(random(400, 500));
							continue;
						}
					}
				}
			}
		}
		return true;
	}
*/
}