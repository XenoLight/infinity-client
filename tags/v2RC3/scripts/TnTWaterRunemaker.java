
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(
	authors = {"Gribonn"}, 
	category = "Runecrafting", 
	name = "TnT Water Rune Maker", 
	version = 1.20, 
	description = "Start this script at wizards tower equipping pickaxe and water tiara, please do not bank water runes when the script is running (even if paused)")
public class TnTWaterRunemaker extends Script implements PaintListener,
        MessageListener {

    final ScriptManifest info = getClass().getAnnotation(ScriptManifest.class);
    public final RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    public String status = "Starting";
    public RSTile[] TowerToEss = {new RSTile(3109, 3168),
        new RSTile(3113, 3180), new RSTile(3112, 3191),
        new RSTile(3112, 3202), new RSTile(3121, 3210),
        new RSTile(3131, 3205), new RSTile(3138, 3197),
        new RSTile(3148, 3191), new RSTile(3158, 3188),
        new RSTile(3165, 3179), new RSTile(3172, 3170),
        new RSTile(3178, 3164), new RSTile(3183, 3164)};
    public RSTile[] EssToTower = walk.reversePath(TowerToEss);
    public RSTile[] MineTiles = {new RSTile(2895, 4846),
        new RSTile(2924, 4847), new RSTile(2926, 4816),
        new RSTile(2896, 4817)};
    public RSTile[] Door1Tiles = {new RSTile(3109, 3167),
        new RSTile(3109, 3166)};
    public RSTile Door2Tile = new RSTile(3107, 3162);
    public RSTile[] Door3Tiles = {new RSTile(3108, 9570),
        new RSTile(3107, 9570)};
    public RSTile ladderTileUp = new RSTile(3104, 3162);
    public RSTile ladderTileDown = new RSTile(3103, 9576);
    public RSTile TowerTile = new RSTile(3109, 3162);
    public RSTile UndergroundTile = new RSTile(3105, 9576);
    public RSTile AtEssTile = new RSTile(3183, 3164);
    public RSArea altar = new RSArea(new RSTile(3473 ,4822), new RSTile(3497,4845));
    public RSArea mines = new RSArea(new RSTile(9564, 4127), new RSTile(9616, 4175));
    public RSArea mine1 = new RSArea(new RSTile(9600, 4163), new RSTile(9614, 4173));
    public RSArea mine2 = new RSArea(new RSTile(9603, 4130), new RSTile(9616, 4143));
    public RSArea mine3 = new RSArea(new RSTile(9565, 4127), new RSTile(9580, 4141));
    public RSArea mine4 = new RSArea(new RSTile(9564, 4158), new RSTile(9581, 4175));
    public RSArea tower1stRoom = new RSArea(new RSTile(3106, 3161), new RSTile(
            3113, 3166));
    public int[] doorClosedIDs = {33060, 11993};
    public int sedridorID = 300;
    public int state = 0;
    public int[] essID = {1436,7936};
    public int essRockID = 2491;
    public int ruinsID = 2454;
    public int altarID = 2480;
    public int waterID = 555;
    public int talismanID = 1444;
    public int[] portalID = {39831, 2467};
    public int nextSec = 0;
    public int maxEssCount = 27;
    public int waterCount = 0;
    public int essCount = 0;
    public int minWait = 600;
    public int maxWait = 1000;
    public int page = 1;
    public int rcstartexp = 0;
    public int miningstartexp = 0;
    public int beforeWaterCount = 0;
    public int beforeEssCount = 0;
    public int waterCost = ge.loadItemInfo(waterID).getPrice();
    public boolean showPaint = true, paint = true, showMiningInfo = false,
            showRcingInfo = false, letTurnPaint = true;
    public long startTime = System.currentTimeMillis();

    @Override
    protected int getMouseSpeed() {
        return random(4, 6);
    }

    @Override
    public boolean onStart(final Map<String, String> args) {
        nextSec = random(1, 4);
        startTime = System.currentTimeMillis();
        rcstartexp = skills.getCurrentXP(Constants.STAT_RUNECRAFTING);
        miningstartexp = skills.getCurrentXP(Constants.STAT_MINING);
        beforeWaterCount = inventory.getCount(waterID);
        beforeEssCount = inventory.getCount(essID);
        camera.setAltitude(true);
        return true;
    }

    @Override
    public int loop() {
        if (isRunning()) {
            minWait = 600;
            maxWait = 1000;
        } else {
            minWait = 1500;
            maxWait = 1900;
        }
        
        debug(String.format("atTower=%s, atEss=%s, insideEss=%s, gotEss=%s", atTower(),atEss(),insideEss(),gotEss()));
        if (atTower() && !gotEss()) {
            state = 0;
        } else if (underground() && !gotEss()) {
            state = 1;
        } else if (atMines() && !gotEss()) {
            state = 2;
        } else if (atMines() && gotEss()) {
            state = 3;
        } else if (underground() && gotEss()) {
            state = 4;
        } else if (atTower() && gotEss()) {
            state = 5;
        } else if (!atTower() && !atEss() && !insideEss() && gotEss()) {
            state = 6;
        } else if (atEss() && gotEss()) {
            state = 7;
        } else if (insideEss() && gotEss()) {
            state = 8;
        } else if (insideEss() && !gotEss()) {
            state = 9;
        } else {
            state = 10;
        }

        debug("State="+state);
        switch (state) {
            case 0:
                checkDoor(Door1Tiles);
                checkDoor(Door2Tile);
                sa();
                wait(random(minWait, maxWait));
                sea();
                
                if (ladderTileUp.distanceTo() <= 4) {
                    if (player.getMine().isIdle()) {
                    	RSObject door = objects.getTopAt(Door2Tile);
                    	if (door!=null)  {
                    		mouse.move(door.getScreenLocation());
                    		if (menu.contains("Open"))  {
                    			mouse.click(true);
                    		}
                    	}
                    	
                    	RSObject ladder = objects.getTopAt(ladderTileUp);
                    	if (ladder!=null)  {
                    		ladder.action("Climb-down");
                    	}
                        return random(300, 600);
                    }
                } else {
                    if (player.getMine().isIdle()) {
                        if (player.getMyEnergy() >= 30) {
                            sa();
                            game.setRun(true);
                            ea();
                            wait(random(100, 300));
                            sea();
                        }
                        walk.to(ladderTileUp);
                        sa();
                        wait(random(minWait, maxWait));
                        sea();
                    }
                }
                break;
            case 1:
                checkDoor(Door3Tiles);
                wait(random(minWait, maxWait));
                final RSNPC Sedridor = npc.getNearestByID(sedridorID);
                if (Sedridor != null) {
                    if (player.getMine().isIdle()) {
                        if (Sedridor.isOnScreen()) {
                        	RSObject door = objects.getTopAt(Door3Tiles[0]);
                        	
                        	if (door!=null)  {
                        		mouse.move(door.getScreenLocation());
                        		if (menu.contains("Open"))  {
                        			mouse.click(true);
                        		}
                        	}
                            if (npc.action(Sedridor, "Teleport Sedridor")) {
                                sa();
                                wait(random(minWait, maxWait));
                                sea();
                            }
                            return random(500, 800);
                        } else {
                            if (player.getMine().isIdle()) {
                                if (player.getMyEnergy() >= 30) {
                                    sa();
                                    game.setRun(true);
                                    ea();
                                    wait(random(100, 300));
                                    sea();
                                }
                                walk.to(Sedridor);
                                sa();
                                wait(random(300, 500));
                                sea();
                            }
                        }
                    }
                }
                break;
            case 2:
            	RSObject essRock = objects.getNearestByID(essRockID);
            	
                if (atMines() && essRock!=null && !essRock.isOnScreen())  {
                    if (player.getMyEnergy() >= 30) {
                        sa();
                        game.setRun(true);
                        ea();
                        wait(random(100, 300));
                        sea();
                    }
                    
                	walk.to(essRock);
                    //walk.to(MineTiles[nextSec]);
                    s("Walking to Essence Rock");
                    return random(300, 600);
                } else if (essRock.isOnScreen()) {
                	
                    if (player.getMine().isIdle() && essRock.action("Mine")) {
                    	player.waitForAnim(3000);
                        antiban();
                        s("Mining Essence");
                        return random(500, 700);
                    } 
                    else  {
                        return random(500, 700);
                    }
                }

                break;
            case 3:
                if (inventory.isFull()) {
                    final RSObject portal = objects.getNearestByID(portalID);
                    if (portal != null) {
                        if (portal.distanceTo() <= 5) {
                            if (player.getMine().isIdle()) {
                                portal.action("Enter Portal");
                            }
                        } else {
                            if (player.getMyEnergy() >= 30) {
                                sa();
                                game.setRun(true);
                                ea();
                                wait(random(100, 300));
                                sea();
                            }
                            walk.to(portal);
                        }
                        wait(random(minWait, maxWait));
                    }
                }
                break;
            case 4:
                checkDoor(Door3Tiles);
                wait(random(minWait, maxWait));
                if (player.getMine().isIdle()) {
                    if (ladderTileDown.isOnScreen()) {
                    	RSObject ladder = objects.getTopAt(ladderTileDown);
                    	if (ladder!=null)  {
                    		if (!ladder.action("Climb-up")) {
                                camera.setRotation(random(1, 360));
                    		}                    		
                    	}
                    	
                        wait(random(minWait, maxWait));
                    } else {
                        if (player.getMyEnergy() >= 30) {
                            sa();
                            game.setRun(true);
                            ea();
                            wait(random(100, 300));
                            sea();
                        }
                        walk.to(ladderTileDown);
                        wait(random(minWait, maxWait));
                    }
                }
                break;
            case 5:
                checkDoor(Door2Tile);
                checkDoor(Door1Tiles);
                wait(random(300, 500));
                while (atTower()) {
                	RSTile t = new RSTile(3104, 3165); 
                    if (t.distanceTo() <= 1) {
                        checkDoor(Door2Tile);
                    }
                    if (tower1stRoom.contains(player.getMyLocation())) {
                        checkDoor(Door1Tiles);
                    }
                    if (player.getMyEnergy() >= 30) {
                        game.setRun(true);
                        wait(random(100, 300));
                    }
                    
                    RSTile dest = walk.getDestination();
                    if (dest==null || dest.distanceTo() < random(5, 12)
                            || dest.distanceTo() > 40) {
                        if (!walk.pathMM(TowerToEss)) {
                            walk.toClosestTile(walk.randomizePath(TowerToEss, 2, 2));
                        }
                    }
                    wait(random(minWait, maxWait));
                }
                break;
            case 6:
                walk.pathMM(TowerToEss);
                break;
            case 7:
                final RSObject ruins = objects.getNearestByID(ruinsID);
                if (ruins != null) {
                    if (ruins.distanceTo() <= 3) {
                        if (player.getMine().isIdle()) {
                            if (inventory.contains(talismanID)) {
                                inventory.clickItem(talismanID, "Use");
                                wait(random(500, 700));
                                if (ruins.action("Use"))  {
                                	wait(random(minWait, maxWait));
                                }
                            } else {
                                if (ruins.action("Enter"))  {
                                	wait(random(minWait, maxWait));
                                }
                            }
                        }
                    } else {
                        if (player.getMyEnergy() >= 30) {
                            sa();
                            game.setRun(true);
                            ea();
                            wait(random(100, 300));
                            sea();
                        }
                        walk.to(ruins);
                        wait(random(minWait, maxWait));
                    }
                }
                break;
            case 8:
                final RSObject altar = objects.getNearestByID(altarID);
                if (altar != null) {
                    if (altar.distanceTo() <= 4) {
                        beforeWaterCount = inventory.getCount(waterID);
                        if (player.getMine().isIdle()) {
                            if (altar.action("Craft-rune")) {
                                antiban();
                                wait(random(minWait, maxWait));
                            }
                        }
                    } else {
                        if (player.getMyEnergy() >= 30) {
                            sa();
                            game.setRun(true);
                            ea();
                            wait(random(100, 300));
                            sea();
                        }
                        walk.to(altar);
                        wait(random(minWait, maxWait));
                    }
                }
                break;
            case 9:
                final RSObject portal = objects.getNearestByID(portalID);
                if (portal != null) {
                    if (portal.isOnScreen()) {
                        if (player.getMine().isIdle()) {
                            portal.action("Enter");
                            player.waitToMove(2000);
                            wait(random(minWait, maxWait));
                        }
                    } else {
                        if (player.getMyEnergy() >= 30) {
                            sa();
                            game.setRun(true);
                            ea();
                            wait(random(100, 300));
                            sea();
                        }
                        walk.to(portal);
                        wait(random(minWait, maxWait));
                    }
                }
                break;
            case 10:
                walk.pathMM(EssToTower);
                break;
        }
        return random(300, 500);
    }

    public void sa() {
        final int random = random(1, 10);
        if (random == 1) {
            Bot.getInputManager().pressKey((char) 37);
        } else if (random == 2) {
            Bot.getInputManager().pressKey((char) 39);
        }
    }

    public void ea() {
        final int random = random(1, 5);
        if (random == 1) {
            Bot.getInputManager().releaseKey((char) 37);
            Bot.getInputManager().releaseKey((char) 39);
        }
    }

    public void sea() {
        Bot.getInputManager().releaseKey((char) 37);
        Bot.getInputManager().releaseKey((char) 39);
    }

    public boolean gotWater() {
        return inventory.contains(waterID);
    }

    public boolean insideEss() {
        return altar.contains(player.getMyLocation());
    }

    public boolean atEss() {
        return AtEssTile.distanceTo() <= 5;
    }

    public void s(String action) {
        status = action;
    }


    public void antiban() {
        final int random = random(1, 10);
        switch (random) {
            case 1:
                camera.setRotation(random(0, 360));
            case 2:
                mouse.move(random(0, 765), random(0, 502));
        }
    }

    public boolean atTower() {
        return TowerTile.distanceTo() <= 9;
    }

    public boolean gotEss() {
        return inventory.getCount(essID) >= maxEssCount;
    }

    public boolean underground() {
        return UndergroundTile.distanceTo() <= 30;
    }

    public boolean atMines() {
    	RSObject tracks = objects.getNearestByID(454, 455, 467, 468, 469, 494, 495);
    	return  (tracks!=null && tracks.isOnScreen());
        //return mines.contains(player.getMyLocation());
    }

    public boolean atSec1() {
        return mine1.contains(player.getMyLocation());
    }

    public boolean atSec2() {
        return mine2.contains(player.getMyLocation());
    }

    public boolean atSec3() {
        return mine3.contains(player.getMyLocation());
    }

    public boolean atSec4() {
        return mine4.contains(player.getMyLocation());
    }

    public boolean returnNow() {
        return true;
    }

    public void checkDoor(RSTile... tiles) {
        sea();
        int c = 0;
        RSObject door;
        for (RSTile tile : tiles) {
            for (int id : doorClosedIDs) {
                if (objects.getTopAt(tile) != null
                        && objects.getTopAt(tile).getID() == id) {
                    c++;                 
                }
            }
        }
        if (c >= 1) {
            if (tiles[0].distanceTo() >= 5) {
                walk.to(tiles[0]);
            }
            wait(random(300, 500));
            if (tiles.length == 1) {
                for (RSTile t : tiles) {
                	door = objects.getTopAt(t);
                    
                	if (!door.action("Open Door")) {
                        returnNow();
                    }
                	else  {
                		player.waitToMove(3000);
                		while (player.getMine().isMoving())  {
                			wait(100);
                		}
                	}
                }
            } else {
                if (!tile.clickDoor(tiles[0], tiles[1])) {
                    returnNow();
                }
                else  {
                	player.waitToMove(3000);
            		while (player.getMine().isMoving())  {
            			wait(100);
            		}                	
                }
            }
        }
    }

    public int walk(RSTile[] path) {
        if (player.getMyEnergy() >= 30) {
            game.setRun(true);
            wait(random(100, 300));
        }
        
        RSTile dest = walk.getDestination();

        if ((dest==null || dest.distanceTo() < random(5, 12))
                || dest.distanceTo() > 40) {
            if (!walk.pathMM(path)) {
                walk.toClosestTile(walk.randomizePath(path, 2, 2));
                return random(minWait, maxWait);
            }
        }
        return random(minWait, maxWait);
    }

    public void ProgBar(Graphics g, int posX, int posY, int width, int height,
            int Progress, Color color1, Color color2, Color text) {
        int[] c1 = {color1.getRed(), color1.getGreen(), color1.getBlue(), 150};
        int[] c2 = {color2.getRed(), color2.getGreen(), color2.getBlue(), 150};
        if (c1[0] > 230) {
            c1[0] = 230;
        }
        if (c1[1] > 230) {
            c1[1] = 230;
        }
        if (c1[2] > 230) {
            c1[2] = 230;
        }
        if (c2[0] > 230) {
            c2[0] = 230;
        }
        if (c2[1] > 230) {
            c2[1] = 230;
        }
        if (c2[2] > 230) {
            c2[2] = 230;
        }

        g.setColor(new Color(c1[0], c1[1], c1[2], 200));
        g.fillRoundRect(posX, posY, width, height, 5, 12);
        g.setColor(new Color(c1[0] + 25, c1[1] + 25, c1[2] + 25, 200));
        g.fillRoundRect(posX, posY, width, height / 2, 5, 12);

        g.setColor(new Color(c2[0], c2[1], c2[2], 200));
        g.fillRoundRect(posX, posY,
                (skills.getPercentToNextLvl(Progress) * width) / 100, height,
                5, 12);
        g.setColor(new Color(c2[0] + 25, c2[1] + 25, c2[2] + 25, 150));
        g.fillRoundRect(posX, posY,
                (skills.getPercentToNextLvl(Progress) * width) / 100,
                height / 2, 5, 12);

        g.setColor(Color.LIGHT_GRAY);
        g.drawRoundRect(posX, posY, width, height, 5, 12);

        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height));
        g.setColor(text);
        g.drawString("" + skills.getPercentToNextLvl(Progress) + "%", posX
                + (width / 2), posY + (height + height / 20));
    }

    public void onRepaint(Graphics g) {
        if (inventory.getCount(waterID) != beforeWaterCount) {
            waterCount += inventory.getCount(waterID) - beforeWaterCount;
            beforeWaterCount = inventory.getCount(waterID);
        }
        if (inventory.getCount(essID) != beforeEssCount) {
            if (inventory.getCount(essID) != 0) {
                essCount++;
                beforeEssCount = inventory.getCount(essID);
            }
        }
        int rcxpgain;
        int miningxpgain;
        int profit = (waterCount * waterCost);
        rcxpgain = skills.getCurrentXP(Constants.STAT_RUNECRAFTING)
                - rcstartexp;
        miningxpgain = skills.getCurrentXP(Constants.STAT_MINING)
                - miningstartexp;
        long millis = System.currentTimeMillis() - startTime;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;
        float rcxpsec = 0;
        if ((minutes > 0 || hours > 0 || seconds > 0) && rcxpgain > 0) {
            rcxpsec = ((float) rcxpgain)
                    / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
        }
        float rcxpmin = rcxpsec * 60;
        float rcxphour = rcxpmin * 60;
        float miningxpsec = 0;
        if ((minutes > 0 || hours > 0 || seconds > 0) && miningxpgain > 0) {
            miningxpsec = ((float) miningxpgain)
                    / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
        }
        float miningxpmin = miningxpsec * 60;
        float miningxphour = miningxpmin * 60;
        final int waterHour = (int) ((waterCount) * 3600000D / (System.currentTimeMillis() - startTime));
        final int essHour = (int) ((essCount) * 3600000D / (System.currentTimeMillis() - startTime));
        final int profitHour = (int) ((profit) * 3600000D / (System.currentTimeMillis() - startTime));
        if (showPaint == true && game.isLoggedIn()) {
            ((Graphics2D) g).setRenderingHints(rh);
            g.setColor(new Color(0, 0, 0, 205));
            g.fillRect(375, 344, 138, 24);
            g.setFont(new Font("Comic Sans MS", 0, 11));
            g.setColor(new Color(255, 255, 255));
            if (paint == false) {
                g.drawString("Turn on paint", 394, 361);
            }
            if (paint == true) {
                int x = 15;
                int x2 = 149;
                g.drawString("Turn off paint", 394, 361);
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(6, 344, 369, 129);
                if (page == 1) {
                    g.fillRect(375, 377, 138, 26);

                    // LEFT SIDE //

                    g.setColor(new Color(255, 255, 255));
                    g.setFont(new Font("Comic Sans MS", 0, 18));
                    g.drawString("TnT Water Maker " + info.version(), 21, 368);
                    g.setFont(new Font("Comic Sans MS", 0, 11));
                    g.drawString("Waters Crafted: " + waterCount, x, 393);
                    g.drawString("Essence Mined: " + essCount, x, 411);
                    g.drawString("Status: " + status, x, 429);
                    g.drawString("Run Time: " + hours + ":" + minutes + ":"
                            + seconds, x, 464);

                    // RIGHT SIDE //

                    g.drawString("Runecrafting xp gained: " + rcxpgain,
                            x2, 393);
                    g.drawString("Mining xp gained: " + miningxpgain, x2, 411);

                } else {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(385, 377, 128, 26);
                }
                if (page == 2) {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(375, 412, 138, 26);
                    g.setFont(new Font("Comic Sans MS", 0, 18));
                    g.setColor(Color.white);
                    g.drawString("TnT Water Maker " + info.version(), 21, 368);
                    g.setFont(new Font("Comic Sans MS", 0, 11));
                    g.drawString("Runes/hour: " + waterHour, x, 393);
                    g.drawString("Essence/hour: " + essHour, x, 411);
                    g.drawString("Run Time: " + hours + ":" + minutes + ":"
                            + seconds, x, 464);

                    // RIGHT SIDE//

                    g.drawString("Runecrafting xp/hour: " + rcxphour, x2, 393);
                    g.drawString("Mining xp/hour: " + miningxphour, x2, 411);
                    g.drawString("Profit/hour: " + profitHour, x2, 429);

                } else {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(385, 412, 128, 26);
                }
                if (page == 3) {
                    g.setFont(new Font("Comic Sans MS", 0, 18));
                    g.setColor(Color.white);
                    g.drawString("TnT Water Maker " + info.version(), 21, 368);
                    g.setFont(new Font("Comic Sans MS", 0, 11));
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(375, 447, 138, 26);
                    g.setColor(Color.white);
                    g.drawString(
                            "Hover mouse over Progress bar for more info! :)",
                            x, 431);
                    g.drawString("Run Time: " + hours + ":" + minutes + ":"
                            + seconds, x, 464);
                    ProgBar(g, 15, 385, 347, 10, Constants.STAT_MINING,
                            Color.red, Color.green, Color.black);
                    ProgBar(g, 15, 405, 347, 10, Constants.STAT_RUNECRAFTING,
                            Color.red, Color.green, Color.black);
                } else {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(385, 447, 128, 26);
                }
                g.setFont(new Font("Comic Sans MS", 0, 11));
                g.setColor(new Color(255, 255, 255));
                g.drawString("General Info", 394, 393);
                g.drawString("Averaging Info", 394, 428);
                g.drawString("Other info", 394, 463);
            }
            Mouse m = Bot.getClient().getMouse();
            if (m.getX() >= 385 && m.getX() < 385 + 128 && m.getY() >= 377 && m.getY() < 377 + 26) {
                page = 1;
            }
            if (m.getX() >= 385 && m.getX() < 385 + 128 && m.getY() >= 412 && m.getY() < 412 + 26) {
                page = 2;
            }
            if (m.getX() >= 385 && m.getX() < 385 + 128 && m.getY() >= 447 && m.getY() < 447 + 26) {
                page = 3;
            }
            if (m.getX() >= 15 && m.getX() < 15 + 347 && m.getY() >= 385 && m.getY() < 385 + 10
                    && page == 3) {
                showMiningInfo = true;
            } else {
                showMiningInfo = false;
            }
            if (m.getX() >= 15 && m.getX() < 15 + 347 && m.getY() >= 405 && m.getY() < 405 + 10
                    && page == 3) {
                showRcingInfo = true;
            } else {
                showRcingInfo = false;
            }
            if (showMiningInfo && paint) {
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(m.getX(), m.getY() - 100, 200, 100);
                g.setColor(Color.white);
                g.drawString("MINING", m.getX() + 15, m.getY() - 75);
                g.drawString("Level: "
                        + skills.getCurrentLvl(Constants.STAT_MINING) + "/"
                        + skills.getRealLvl(Constants.STAT_MINING),
                        m.getX() + 15, m.getY() - 60);
                g.drawString("Xp: "
                        + skills.getCurrentXP(Constants.STAT_MINING),
                        m.getX() + 15, m.getY() - 45);
                g.drawString("Xp Till Next level: "
                        + skills.getXPToNextLvl(Constants.STAT_MINING),
                        m.getX() + 15, m.getY() - 30);
                g.drawString("% To Next Level: "
                        + +skills.getPercentToNextLvl(Constants.STAT_MINING),
                        m.getX() + 15, m.getY() - 15);

            }
            if (showRcingInfo && paint) {
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(m.getX(), m.getY() - 100, 200, 100);
                g.setColor(Color.white);
                g.drawString("RUNECRAFTING", m.getX() + 15, m.getY() - 75);
                g.drawString(
                        "Level: "
                        + skills.getCurrentLvl(Constants.STAT_RUNECRAFTING)
                        + "/"
                        + skills.getRealLvl(Constants.STAT_RUNECRAFTING),
                        m.getX() + 15, m.getY() - 60);
                g.drawString(
                        "Xp: "
                        + skills.getCurrentXP(Constants.STAT_RUNECRAFTING),
                        m.getX() + 15, m.getY() - 45);
                g.drawString("Xp Till Next level: "
                        + skills.getXPToNextLvl(Constants.STAT_RUNECRAFTING),
                        m.getX() + 15, m.getY() - 30);
                g.drawString(
                        "% To Next Level: "
                        + +skills.getPercentToNextLvl(Constants.STAT_RUNECRAFTING),
                        m.getX() + 15, m.getY() - 15);

            }
            if (m.getX() >= 375 && m.getX() < 375 + 138 && m.getY() >= 344 && m.getY() < 344 + 24) {
                if (letTurnPaint) {
                    if (paint == false) {
                        paint = true;
                    } else {
                        paint = false;
                    }
                    letTurnPaint = false;
                }
            } else {
                letTurnPaint = true;
            }
        }
    }

    public void messageReceived(MessageEvent arg0) {
    }
}
