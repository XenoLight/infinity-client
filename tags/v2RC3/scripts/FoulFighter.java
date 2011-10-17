import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.util.GlobalConfiguration;


@ScriptManifest(
		authors = {"Foulwerp"}, 
		category = "Combat", 
		name = "FoulFighter v2.07", 
		version = 2.07, 
		description = "Settings In GUI")
public class FoulFighter extends Script implements PaintListener,
        MessageListener {
    /*
     * Credits to Ruski for Food ID's
     */

    final int[] foodID = {1895, 1893, 1891, 4293, 2142, 291, 2140, 3228, 9980,
        7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568, 2343,
        1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351, 329,
        3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391, 3369,
        3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178, 7180,
        7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011, 2289,
        2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895, 1897,
        1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068, 1942,
        6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989, 1978,
        5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911, 5745,
        2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034, 2048,
        2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225, 2255,
        2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032, 2074,
        2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971, 4608,
        1883, 1885, 15272};
    final int[] pBones = {526, 532, 530, 528, 3183, 2859};
    final int[] bBones = {526, 532, 530, 528, 3183, 2859, 534, 3125, 4834,
        14793, 4812, 3123, 4832, 6729, 536};
    final int[] fRune = {554, 4694, 4699, 4697};
    final int[] charm = {12158, 12159, 12160, 12163};
    final int[] fStaffs = {1387, 1401, 1393, 3054, 3053, 11738, 11736};
    final int[] sPot = {113, 115, 117, 119, 2440, 157, 159, 161, 9739, 9741,
        9743, 9745};
    final int[] dPot = {2442, 163, 165, 167, 137, 135, 133, 2432};
    final int[] aPot = {2436, 145, 147, 149, 2428, 121, 123, 125, 9739, 9741,
        9743, 9745};
    final int[] bPTab = {8015};
    private int[] itemids = {};
    private String[] itemnames = {};
    private int[] alchable = {};
    private int[] npcID = {};
    private int startExp[] = null;
    public int spec, getExpH, xpph, alchNum, rndSpecCtr;
    public Integer origWeap=0, origShield=0, specWeap=0, specPercent=100;
    private Boolean useFood=false, useBTP=false, buryBones=false, charms=true, 
    		attack=false, strength=false,
            defence=false, usepotion=false, useSpec=false;
    private Boolean paint = true;
    private RSNPC monster;
    private RSGroundItem tileB, tileA;
    private long start;
    private FoulFighterGUI gui;
    private String state = "Nothing";
    private int rndSpec = 100;
    private int tempRndSpec;
    private int[] excludedNPCs = {5918 /* Stray dog */};
    private List<Integer> excludedNPCsList = new ArrayList<Integer>();
    
    private static double getVersion() {
        return 2.06;
    }

    private int slayerLeft() {
        return settings.get(394);
    }

    public int getCurrentLifePoints() {
        return Integer.parseInt(iface.get(748).getChild(8).getText());
    }

    private enum State {
        FIGHTING, ATTACK, PICKUP, POTION, BURY, BTP, BONES, ALCH, SPECIAL
    }

    private State getState() {
    	
    	RSNPC inter = npc.getInteracting();
    	
        if (inter != null && !excludedNPCsList.contains(inter.getID())) {
            if (useSpec && settings.get(300) >= rndSpec * 10) {
                rndSpecCtr = 0;
                return State.SPECIAL;
            } else {
                state = "   FIGHTING";
                return State.FIGHTING;
            }
        } else if (itemsOnGround()) {
            state = "    PICKUP";
            return State.PICKUP;
        } else if (((inventory.getCount() < 26) && useBTP && bonesOnGround())
                || ((inventory.getCount() < 26) && buryBones && bonesOnGround())) {
            state = "     BONES";
            return State.BONES;
        } else if ((inventory.getCount() >= 26) && buryBones
                && (inventory.getCount(bBones) != 0)) {
            state = "   BURYING";
            return State.BURY;
        } else if ((inventory.getCount(foodID) == 0) && useBTP
                && (inventory.getCount(pBones) != 0)) {
            state = "      BTP";
            return State.BTP;
        } else if (usepotion && hasPotions()) {
            if ((strength && (skills.getRealLvl(STAT_STRENGTH)
                    + (random(3, 5)) >= skills.getCurrentLvl(STAT_STRENGTH)))
                    || (attack && (skills.getRealLvl(STAT_ATTACK)
                    + (random(3, 5)) >= skills.getCurrentLvl(STAT_ATTACK)))
                    || (defence && (skills.getRealLvl(STAT_DEFENSE)
                    + (random(3, 5)) >= skills.getCurrentLvl(STAT_DEFENSE)))) {
                state = "    POTION";
                return State.POTION;
            }
        }
        
    	inter = npc.getInteracting();

        if (inventory.getCount(alchable) != 0
                && (inter != null && !excludedNPCsList.contains(inter.getID()))) {
            state = "   ALCHING";
            return State.ALCH;
        }
        state = " ATTACKING";
        return State.ATTACK;
    }

    @Override
    public boolean onStart(final Map<String, String> s) {
        start = System.currentTimeMillis();
        gui = new FoulFighterGUI();
        while (gui.isVisible()) {
            wait(random(100, 500));
        }
        for (int i = 0; i < itemids.length; i++) {
            log(itemids[i] + "," + itemnames[i]);
        }
        startExp = new int[20];
        for (int i = 0; i < 20; i++) {
            startExp[i] = skills.getCurrentXP(i);
        }
        if (origWeap != 0) {
            log("Original Weapon - " + origWeap);
        }
        if (origShield != 0) {
            log("Original Shield - " + origShield);
        }
        if (specWeap != 0) {
            log("Special Weapon - " + specWeap);
        }
        if (specPercent != 0) {
            log("Special Percent - " + specPercent + "%");
        }
        
        for (int npcid: excludedNPCs)  {
        	excludedNPCsList.add(npcid);
        }
        
        return true;
    }

    @Override
    public int loop() {
        if (!game.isLoggedIn()) {
            return random(200, 700);
        }
        if (!isRunning() && player.getMyEnergy() > random(50, 75)) {
            game.setRun(true);
            wait(random(400, 800));
        }
        if (itemSelected() != 0) {
        	tile.click(player.getMine().getLocation(), "Cancel");
            wait(random(300, 600));
        }
        if ((inventory.getCount(bBones) != 0) && (inventory.getCount() >= 27)) {
            doInventoryItem(bBones, "Drop");
            wait(random(600, 750));
        }
        if (useSpec) {
            if (rndSpecCtr == 0 && settings.get(300) / 10 >= specPercent) {
                if (settings.get(301) != 1) {
                    rndSpec = settings.get(300) / 10;
                } else if (settings.get(301) == 1) {
                    tempRndSpec = (settings.get(300) / 10) - specPercent;
                    if (tempRndSpec >= specPercent) {
                        rndSpec = tempRndSpec;
                    } else {
                        getRndSpec();
                    }
                }
                rndSpecCtr = 1;
            } else {
                if (settings.get(300) < specPercent * 10) {
                    while (inventory.contains(origWeap)) {
                        inventory.clickItem(origWeap, "Wield");
                        wait(random(1000, 1100));
                    }
                    while (inventory.contains(origShield)) {
                        inventory.clickItem(origShield, "Wear");
                        wait(random(1000, 1100));
                    }
                    if (rndSpecCtr == 0) {
                        getRndSpec();
                        rndSpecCtr = 1;
                    }
                }
            }
        }
        
        State state = getState();
        debug("State="+state.toString());
        
        switch (state) {
            case FIGHTING:
                if (useFood) {
                    int CurrHP = getCurrentLifePoints() / 10;
                    int RealHP = skills.getRealLvl(STAT_HITPOINTS);
                    if (CurrHP <= random(RealHP / 2, RealHP / 1.5)) {
                        if (inventory.getCount(foodID) != 0) {
                            if (game.getCurrentTab() != Game.tabInventory) {
                                game.openTab(Game.tabInventory);
                                wait(random(400, 600));
                            }
                            doInventoryItem(foodID, "Eat");
                            if (player.waitForAnim(829) != -1) {
                                while (player.getMine().getAnimation() != -1) {
                                    wait(random(300, 600));
                                }

                                RSNPC inter = npc.getInteracting();
                                if (inter != null && !excludedNPCsList.contains(inter.getID())) {
                                    npc.getInteracting().action("Attack");
                                    if (player.waitToMove(750)) {
                                        while (player.getMine().isMoving()) {
                                            wait(random(20, 30));
                                        }
                                    }
                                }
                            }
                        } else {
                            if ((inventory.getCount(foodID) == 0) && game.isLoggedIn()) {
                                if (useBTP) {
                                    if (inventory.getCount(bPTab) == 0) {
                                        log("Out of Bones to Peaches Tabs! Stopping Script!");
                                        stopScript();
                                    } else {
                                        if (inventory.getCount(pBones) == 0) {
                                            log("Out of Bones for Bones to Peaches! Stopping Script!");
                                            stopScript();
                                        } else {
                                            if (inventory.getCount(bPTab) != 0) {
                                                if ((inventory.getCount(foodID) == 0)
                                                        && (inventory.getCount(pBones) != 0)) {
                                                    if (game.getCurrentTab() != Game.tabInventory) {
                                                        game.openTab(Game.tabInventory);
                                                        wait(random(300, 500));
                                                    }
                                                    doInventoryItem(bPTab, "Break");
                                                    if (player.waitForAnim(1500) != -1) {
                                                        while (player.getMine().getAnimation() != -1) {
                                                            wait(random(1250, 1500));
                                                        }
                                                    }
                                                    if (inventory.getCount(foodID) != 0) {
                                                        log("Used a Bones to Peaches Tab!");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    log("Out of Food! Stopping Script!");
                                    stopScript();
                                }
                            }
                        }
                    }
                }
                return antiban();
            case PICKUP:
                for (int i = 0; i < itemids.length; i++) {
                    while ((tileA = ground.getItemByID(itemids[i])) != null) {
                        if (!tileA.isOnScreen()) {
                            walk.tileOnScreen(walk.randomizeTile(tileA.getLocation(), 1, 1));
                            if (player.waitToMove(1000)) {
                                while (player.getMine().isMoving()) {
                                    wait(random(20, 60));
                                }
                            }
                        }
                        if (inventory.isFull()) {
                            if ((inventory.getCount(tileA.getItem().getID()) == 0)
                                    || (inventory.getItemByID(tileA.getItem().getID()).getStackSize() == 1)) {
                                if (buryBones && (inventory.getCount(bBones) > 0)) {
                                    doInventoryItem(bBones, "Bury");
                                    wait(random(1000, 1500));
                                } else {
                                    if (useBTP && (inventory.getCount(pBones) > 0)) {
                                        doInventoryItem(pBones, "Drop");
                                        wait(random(750, 1000));
                                    } else {
                                        if (useFood
                                                && (inventory.getCount(foodID) > 0)) {
                                            doInventoryItem(foodID, "Eat");
                                            wait(random(750, 1000));
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        tileA.action("Take", itemnames[i]);
                        if (player.waitToMove(1000)) {
                            while (player.getMine().isMoving()) {
                                wait(random(20, 60));
                            }
                        }
                    }
                }
                if (charms) {
                    for (int i = 0; i < charm.length; i++) {
                        while ((tileA = ground.getItemByID(charm[i])) != null) {
                            if (!tileA.isOnScreen()) {
                                walk.tileOnScreen(walk.randomizeTile(tileA.getLocation(), 1, 1));
                                if (player.waitToMove(1000)) {
                                    while (player.getMine().isMoving()) {
                                        wait(random(20, 60));
                                    }
                                }
                            }
                            if (inventory.isFull()
                                    && (inventory.getCount(tileA.getItem().getID()) == 0)) {
                                if (buryBones && (inventory.getCount(bBones) > 0)) {
                                    doInventoryItem(bBones, "Bury");
                                    wait(random(1000, 1500));
                                } else {
                                    if (useBTP && (inventory.getCount(pBones) > 0)) {
                                        doInventoryItem(pBones, "Drop");
                                        wait(random(750, 1000));
                                    } else {
                                        if (useFood
                                                && (inventory.getCount(foodID) > 0)) {
                                            doInventoryItem(foodID, "Eat");
                                            wait(random(750, 1000));
                                        } else {
                                            break;
                                        }
                                    }
                                }
                            }
                            String action2 = "charm";
                            tileA.action(action2);
                            if (player.waitToMove(1000)) {
                                while (player.getMine().isMoving()) {
                                    wait(random(20, 60));
                                }
                            }
                        }
                    }
                }
                break;
            case BONES:
                if (useBTP) {
                    for (int i = 0; i < pBones.length; i++) {
                        while ((tileB = ground.getNearestItemByID(pBones[i])) != null) {
                            if (inventory.getCount() >= 26) {
                                break;
                            }
                            if (!tileB.isOnScreen()) {
                                break;
                            }
                            String action = "ones";
                            tileB.action(action);
                            if (player.waitToMove(1000)) {
                                while (player.getMine().isMoving()) {
                                    wait(random(20, 60));
                                }
                            }
                        }
                    }
                }
                if (buryBones) {
                    for (int i = 0; i < bBones.length; i++) {
                        while ((tileB = ground.getNearestItemByID(bBones[i])) != null) {
                            if (inventory.getCount() >= 26) {
                                break;
                            }
                            if (!tileB.isOnScreen()) {
                                break;
                            }
                            String action = "ones";
                            tileB.action(action);
                            if (player.waitToMove(1000)) {
                                while (player.getMine().isMoving()) {
                                    wait(random(20, 60));
                                }
                            }
                        }
                    }
                }
                break;
            case ALCH:
                if ((inventory.getCount(alchable) > inventory.getCount(561))
                        || (inventory.getCount(alchable) > inventory.getCount(fRune))) {
                    alchNum = inventory.getCount(561);
                } else {
                    alchNum = inventory.getCount(alchable);
                }
                if ((inventory.getCount(561) >= alchNum)
                        && (inventory.getCount(fRune) >= alchNum * 5)) {
                    while (alchNum > 0) {
                        if (game.getCurrentTab() != Game.tabMagic) {
                            game.openTab(Game.tabMagic);
                            wait(random(500, 750));
                        }
                        magic.castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
                        doInventoryItem(alchable, "Cast");
                        if (player.waitForAnim(1000) != -1) {
                            wait(random(1400, 1600));
                        }
                        alchNum--;
                    }
                }
                break;
            case BURY:
                if (buryBones) {
                    if (inventory.getCount() >= 26) {
                        RSNPC inter = npc.getInteracting();
                        if (inter != null && !excludedNPCsList.contains(inter.getID())) {
                            while (inventory.getCount(bBones) != 0) {
                                if (npc.getInteracting() != null) {
                                    break;
                                }
                                if (doInventoryItem(bBones, "Bury")) {
                                    if (player.waitForAnim(300) != -1) {
                                        while (player.getMine().getAnimation() != -1) {
                                            wait(random(100, 300));
                                        }
                                    }
                                }
                            }
                            log("Bury Bones Complete!");
                        }
                    }
                }
                break;
            case POTION:
                if (strength) {
                    if (skills.getCurrentLvl(STAT_STRENGTH) <= skills.getRealLvl(STAT_STRENGTH)
                            + random(3, 5)) {
                        if (inventory.getCount(sPot) != 0) {
                            if (game.getCurrentTab() != Game.tabInventory) {
                                game.openTab(Game.tabInventory);
                                wait(random(200, 300));
                            }
                            doInventoryItem(sPot, "Drink");
                            if (player.waitForAnim(829) != -1) {
                                while (player.getMine().getAnimation() != -1) {
                                    wait(random(100, 300));
                                }
                            }
                        }
                    }
                }
                if (defence) {
                    if (skills.getCurrentLvl(STAT_DEFENSE) <= skills.getRealLvl(STAT_DEFENSE)
                            + random(3, 5)) {
                        if (inventory.getCount(dPot) != 0) {
                            if (game.getCurrentTab() != Game.tabInventory) {
                                game.openTab(Game.tabInventory);
                                wait(random(200, 300));
                            }
                            doInventoryItem(dPot, "Drink");
                            if (player.waitForAnim(829) != -1) {
                                while (player.getMine().getAnimation() != -1) {
                                    wait(random(100, 300));
                                }
                            }
                        }
                    }
                }
                if (attack) {
                    if (skills.getCurrentLvl(STAT_ATTACK) <= skills.getRealLvl(STAT_ATTACK)
                            + random(3, 5)) {
                        if (inventory.getCount(aPot) != 0) {
                            if (game.getCurrentTab() != Game.tabInventory) {
                                game.openTab(Game.tabInventory);
                                wait(random(200, 300));
                            }
                            doInventoryItem(aPot, "Drink");
                            if (player.waitForAnim(829) != -1) {
                                while (player.getMine().getAnimation() != -1) {
                                    wait(random(100, 300));
                                }
                            }
                        }
                    }
                }
                break;
            case BTP:
                if (useBTP) {
                    if (inventory.getCount(bPTab) != 0
                            && game.getCurrentTab() == Game.tabInventory) {
                        if ((inventory.getCount(foodID) == 0)
                                && (inventory.getCount(pBones) != 0)) {
                            if (game.getCurrentTab() != Game.tabInventory) {
                                game.openTab(Game.tabInventory);
                                wait(random(300, 500));
                            }
                            doInventoryItem(bPTab, "Break");
                            if (player.waitForAnim(1500) != -1) {
                                while (player.getMine().getAnimation() != -1) {
                                    wait(random(1250, 1500));
                                }
                            }
                            log("Used a Bones to Peaches Tab!");
                        }
                    } else {
                        log("Out of Bones to Peaches tabs stopping Script!");
                        stopScript();
                    }
                }
                break;
            case ATTACK:
                RSNPC inter = npc.getInteracting();
                
                if (inter!=null && !excludedNPCsList.contains(inter.getID())) {
                	monster = inter;
                } 
                else if (inter == null) {
                	monster = npc.getNearestFreeToAttackByID(npcID);
                }

                if (monster == null) {
                    return antiban();
                }
                
                if (!monster.isOnScreen()
                        && (player.getMine().getInteracting() == null)) {
                    walk();
                    if (player.waitToMove(1000)) {
                        while (player.getMine().isMoving()) {
                            wait(random(20, 30));
                        }
                    }
                }
                                
                if (monster.isOnScreen()
                        && (player.getMine().getInteracting() == null)) {
                    monster.action("Attack " + monster.getName());
                    if (player.waitToMove(1000)) {
                        while (player.getMine().isMoving()) {
                            wait(random(20, 30));
                        }
                    }
                } else {
                    wait(random(20, 30));
                }
                break;
            case SPECIAL:
                while (inventory.contains(specWeap)) {
                    inventory.clickItem(specWeap, "Wield");
                    wait(random(1000, 1100));
                }
                
                 inter = npc.getInteracting();

                while (inter!=null && !excludedNPCsList.contains(inter.getID())
                        && settings.get(300) >= specPercent * 10) {
                	
                    if (player.getMine().getInteracting() == null) {
                        break;
                    }
                    if (game.getCurrentTab() != Game.tabAttack) {
                        game.openTab(Game.tabAttack);
                        wait(random(400, 600));
                    }
                    if (settings.get(301) != 1) {
                        iface.clickChild(884, 4);
                        wait(random(900, 1000));
                    } else {
                        wait(random(100, 300));
                    }
                }
                break;
        }
        return 100;
    }

    private int antiban() {
        int i = random(0, 30);
        int ii = random(0, 25);
        if (i == 2) {
            mouse.move(random(14, 710), random(10, 440));
            return random(0, 400);
        } else if ((ii == 3) || (ii == 12)) {
            char dir = 37;
            if (random(0, 3) == 2) {
                dir = 39;
            }
            Bot.getInputManager().pressKey(dir);
            wait(random(500, 2000));
            Bot.getInputManager().releaseKey(dir);
            return random(0, 500);
        } else if ((i == 5) || (i == 10) || (i == 11) || (i == 13) || (i == 18)
                || (i == 27)) {
            moveMouseRandomly(random(-4, 4));
        } else if ((i == 1) || (i == 8) || (i == 15) || (i == 20)) {
            Thread m = new Thread() {

                @Override
                public void run() {
                    mouse.move(random(14, 710), random(10, 440));
                }
            };
            if (i == 1) {
                m.start();
            }
            while (m.isAlive()) {
                wait(random(100, 300));
                return random(300, 700);
            }
        }
        return random(1000, 1500);
    }

    private void walk() {
        monster = npc.getNearestFreeToAttackByID(npcID);
        if ((calculate.distanceTo(monster.getLocation()) <= 10)) {
            walk.tileOnScreen(monster.getLocation().randomizeTile(1, 1));
        } else {
            if (!tile.onScreen(monster.getLocation())
                    && tile.onMap(monster.getLocation())) {
                walk.tileMM(monster.getLocation().randomizeTile(2, 2));
            } else {
                return;
            }
        }
    }

    @Override
    public int getMouseSpeed() {
        return (random(6, 8));
    }

    private int itemSelected() {
        for (final RSInterfaceChild com : inventory.getInterface().getChildren()) {
            if (com.getBorderThickness() == 2) {
                return com.getChildID();
            }
        }
        return 0;
    }

    private boolean doInventoryItem(int[] ids, String action) {
        ArrayList<RSInterfaceChild> possible = new ArrayList<RSInterfaceChild>();
        for (RSInterfaceChild com : inventory.getInterface().getChildren()) {
            for (int i : ids) {
                if (i == com.getChildID()) {
                    possible.add(com);
                }
            }
        }
        if (possible.size() == 0) {
            return false;
        }
        RSInterfaceChild winner = possible.get(random(0,
                possible.size() - 1));
        Rectangle loc = winner.getArea();
        mouse.move((int) loc.getX() + 3, (int) loc.getY() + 3, (int) loc.getWidth() - 3, (int) loc.getHeight() - 3);
        wait(random(100, 300));
        String top = menu.getItems()[0].toLowerCase();
        if (top.contains(action.toLowerCase())) {
            mouse.click(true);
            return true;
        } else if (menu.contains(action)) {
            return menu.action(action);
        }
        return false;
    }

    private boolean itemsOnGround() {
        for (int i = 0; i < itemids.length; i++) {
            while (((tileA = ground.getItemByID(itemids[i])) != null)) {
                return true;
            }
        }
        if (charms) {
            while (((tileA = ground.getItemByID(charm)) != null)
                    && tileA.isOnScreen()) {
                return true;
            }
        }
        return false;
    }

    private boolean bonesOnGround() {
        if (useBTP) {
            while (((tileA = ground.getItemByID(pBones)) != null)
                    && tileA.isOnScreen()) {
                return true;
            }
        } else if (buryBones) {
            while (((tileA = ground.getItemByID(bBones)) != null)
                    && tileA.isOnScreen()) {
                return true;
            }
        }
        return false;
    }

    private void getRndSpec() {
        rndSpec = random(specPercent, 100);
        log("Special will be used next at " + rndSpec + "%");
    }

    public void messageReceived(MessageEvent arg0) {
        String serverString = arg0.getMessage();
        if (serverString.contains("You've just")
                || serverString.contains("Congratulations")) {
            log("You just advanced a level, attempting to click continue!");
            wait(random(1500, 2500));
            if (iface.canContinue()) {
                iface.clickContinue();
            }
        }
    }

    private boolean hasPotions() {
        if ((attack && (inventory.getCount(aPot) != 0))
                || (strength && (inventory.getCount(sPot) != 0))
                || (defence && (inventory.getCount(dPot) != 0))) {
            return true;
        }
        return false;
    }

    public void onRepaint(Graphics g) {
        if (paint) {
            final Mouse mouse = Bot.getClient().getMouse();
            final int mouse_x = mouse.getX();
            final int mouse_y = mouse.getY();
            final int mouse_press_x = mouse.getPressX();
            final int mouse_press_y = mouse.getPressY();
            final long mouse_press_time = mouse.getPressTime();
            g.setFont(new Font("Century Gothic", Font.BOLD, 13));
            if (System.currentTimeMillis() - mouse_press_time < 100) {
                g.setColor(new Color(70, 130, 180, 250));
                g.drawString("C", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 200)
                    && (System.currentTimeMillis() - mouse_press_time > 99)) {
                g.setColor(new Color(70, 130, 180, 225));
                g.drawString("Cl", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 300)
                    && (System.currentTimeMillis() - mouse_press_time > 199)) {
                g.setColor(new Color(70, 130, 180, 200));
                g.drawString("Cli", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 400)
                    && (System.currentTimeMillis() - mouse_press_time > 299)) {
                g.setColor(new Color(70, 130, 180, 175));
                g.drawString("Clic", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 500)
                    && (System.currentTimeMillis() - mouse_press_time > 399)) {
                g.setColor(new Color(70, 130, 180, 150));
                g.drawString("Click", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 600)
                    && (System.currentTimeMillis() - mouse_press_time > 499)) {
                g.setColor(new Color(70, 130, 180, 125));
                g.drawString("Click", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 700)
                    && (System.currentTimeMillis() - mouse_press_time > 599)) {
                g.setColor(new Color(70, 130, 180, 100));
                g.drawString("Click", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 800)
                    && (System.currentTimeMillis() - mouse_press_time > 699)) {
                g.setColor(new Color(70, 130, 180, 75));
                g.drawString("Click", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 900)
                    && (System.currentTimeMillis() - mouse_press_time > 799)) {
                g.setColor(new Color(70, 130, 180, 50));
                g.drawString("Click", mouse_press_x, mouse_press_y);
            } else if ((System.currentTimeMillis() - mouse_press_time < 1000)
                    && (System.currentTimeMillis() - mouse_press_time > 899)) {
                g.setColor(new Color(70, 130, 180, 25));
                g.drawString("Click", mouse_press_x, mouse_press_y);
            }
            Polygon po = new Polygon();
            po.addPoint(mouse_x, mouse_y);
            po.addPoint(mouse_x, mouse_y + 15);
            po.addPoint(mouse_x + 10, mouse_y + 10);
            g.setColor(new Color(70, 130, 180, 125));
            g.fillPolygon(po);
            g.drawPolygon(po);
            int lY = (int) iface.get(752).getChild(7).getArea().getY();
            int lW = (int) iface.get(137).getChild(0).getArea().getWidth();
            g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
            g.setColor(Color.BLACK);
            g.drawRoundRect(lW - 90, lY - 15, 100, 15, 10, 10);
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRoundRect(lW - 90, lY - 15, 100, 15, 10, 10);
            g.setColor(Color.WHITE);
            g.drawString(state, lW - 81, lY - 3);
            int x = 0;
            int y = 0;
            long millis = System.currentTimeMillis() - start;
            final long hours = millis / (1000 * 60 * 60);
            millis -= hours * 1000 * 60 * 60;
            final long minutes = millis / (1000 * 60);
            millis -= minutes * 1000 * 60;
            final long seconds = millis / 1000;
            paintBar(g, x, y, "FoulFighter Time Running : " + hours + ":"
                    + minutes + ":" + seconds);
            String ver = Double.toString(getVersion());
            if (slayerLeft() != 0) {
                String sl = Integer.toString(slayerLeft());
                g.drawString("Left of Task: " + sl, 240, y + 13);
            }
            g.drawString("Version " + ver, 436, y + 13);
            y += 15;
            for (int i = 0; i < 7; i++) {
                if ((startExp != null)
                        && ((skills.getCurrentXP(i) - startExp[i]) > 0)) {
                    paintSkillBar(g, x, y, i, startExp[i]);
                    y += 15;
                }
            }
            if ((startExp != null)
                    && (skills.getCurrentXP(18) - startExp[18] > 0)) {
                paintSkillBar(g, x, y, 18, startExp[18]);
                y += 15;
            }
            g.setColor(new Color(255, 0, 0, 90));
            g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
            g.setColor(Color.GREEN);
            g.fillRoundRect(416, y + 3, settings.get(300) / 10, 9, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(380, y, 136, 15, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(416, y + 3, settings.get(300) / 10, 9, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
            g.setColor(new Color(0, 0, 0, 90));
            g.fillRoundRect(380, y, 136, 15, 10, 10);
            g.setColor(Color.WHITE);
            g.drawString(Integer.toString(settings.get(300) / 10) + "%", 385,
                    y + 13);
        }
    }

    public void paintSkillBar(Graphics g, int x, int y, int skill, int start) {
        if (paint) {
            g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
            int gained = (skills.getCurrentXP(skill) - start);
            String s = SkillToString(skill) + " Exp Gained: " + gained;
            String firstLetter = s.substring(0, 1);
            String remainder = s.substring(1);
            String capitalized = firstLetter.toUpperCase() + remainder;
            String exp = Integer.toString(skills.getXPToNextLvl(skill));
            g.setColor(new Color(255, 0, 0, 90));
            g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
            g.setColor(new Color(0, 255, 0, 255));
            g.fillRoundRect(416, y + 3, skills.getPercentToNextLvl(skill), 9,
                    10, 10);
            g.setColor(Color.BLACK);
            g.drawRoundRect(416, y + 3, skills.getPercentToNextLvl(skill), 9,
                    10, 10);
            g.setColor(new Color(0, 200, 255));
            paintBar(g, x, y, capitalized);
            g.drawString("Exp To Level: " + exp, 240, y + 13);
        }
    }

    public static void paintBar(Graphics g, int x, int y, String s) {
        g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
        int width = 516;
        int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
        g.setColor(Color.BLACK);
        g.drawRoundRect(0, y, width, height, 10, 10);

        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(0, y, width, height, 10, 10);

        g.setColor(new Color(255, 255, 255));
        g.drawString(s, x + 7, y + height - 2);
    }

    private static String SkillToString(int skill) {
        return Skills.statsArray[skill];
    }

    class FoulFighterGUI extends JFrame implements ListSelectionListener,
            ActionListener {

        private static final long serialVersionUID = 1L;
        private final DefaultListModel mobsToAttackModel;
        private final DefaultListModel mobsInAreaModel;
        private final DefaultListModel itemsPickupModel;
        private final DefaultListModel alchModel;
        private final JPanel contentPane;
        private final JTextField txtItemId;
        private final JTextField txtAlchable;
        private final JTextField txtItemName;
        private final JTextField txtOrigWeap;
        private final JTextField txtSpecWeap;
        private final JTextField txtOrigShield;
        private final JTextField txtSpecPercent;
        private final JList itemsPickupList;
        private final JList mobsToAttackList;
        private final JList mobsInAreaList;
        private final JList alchList;
        private final JButton btnStartScript;
        private final JButton btnAdd;
        private final JButton btnAdd2;
        private final JButton btnLoad;
        private final JScrollPane scrollPane;
        private final JScrollPane scrollPane_1;
        private final JScrollPane scrollPane_2;
        private final JCheckBox chckbxUseFood;
        private final JCheckBox chckbxuseBonesToPeaches;
        private final JCheckBox chckbxBuryBones;
        private final JCheckBox chckbxCharms;
        private final JCheckBox chckbxUsePotion;
        private final JCheckBox chckbxStrength;
        private final JCheckBox chckbxDefence;
        private final JCheckBox chckbxAttack;
        private final JCheckBox chckbxDisablePaint;
        private final JCheckBox chckbxUseSpec;
        private final JLabel MobsInArea;
        private final JLabel MobsToAttack;
        private final JLabel itemPickup;
        private JLabel AddItem;
        private final JLabel Alch;
        private final JLabel AddAlch;
        private JLabel AddNote;

        private Properties settings = new Properties();
        private String settingsFileName = 
        		String.format("%s/%s", 
        			GlobalConfiguration.Paths.getSettingsDirectory(), 
        			"FoulFighter.txt");

        
        public FoulFighterGUI() {
        	// Load the settings.
        	String settingsFileName = 
        		String.format("%s/%s", GlobalConfiguration.Paths.getSettingsDirectory(), "FoulFighter.txt");
        	FileInputStream inStream;
			File settingsFile = new File(settingsFileName);
        	
			if (settingsFile.exists())  {
        	
	        	try {
					inStream = new FileInputStream(settingsFileName);
				
					settings.load(inStream);
					
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
			
            setTitle("FoulFighter");
            setBounds(100, 100, 450, 450);
            contentPane = new JPanel();
            contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setContentPane(contentPane);
            contentPane.setLayout(null);
            {
                JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
                tabbedPane.setBounds(10, 10, 414, 354);
                contentPane.add(tabbedPane);
                {
                    JPanel panel = new JPanel();
                    tabbedPane.addTab("Fighting", null, panel, null);
                    panel.setLayout(null);
                    {
                        mobsToAttackModel = new DefaultListModel();
                        {
                            MobsToAttack = new JLabel();
                            MobsToAttack.setBounds(10, 150, 390, 9);
                            MobsToAttack.setText("These are the NPC's you will attack. Click a NPC to remove it from the attack itemsPickupList");
                            panel.add(MobsToAttack);
                            scrollPane = new JScrollPane();
                            scrollPane.setBounds(10, 160, 390, 125);
                            panel.add(scrollPane);
                            mobsToAttackList = new JList(mobsToAttackModel);
                            
                            String set = settings.getProperty("MobsToAttack", "");
                            if (set!=null && !set.isEmpty())  {
                            	for (String opt: set.split(":"))  {
                            		mobsToAttackModel.addElement(opt);
                            	}
                            }                            
                            
                            scrollPane.setViewportView(mobsToAttackList);
                            mobsToAttackList.addListSelectionListener(this);
                            mobsToAttackList.setBorder(new LineBorder(
                                    new Color(0, 0, 0)));
                        }
                    }
                    {
                        mobsInAreaModel = new DefaultListModel();
                        {
                            MobsInArea = new JLabel();
                            MobsInArea.setBounds(10, 10, 390, 9);
                            MobsInArea.setText("These are the NPC's in your area. Click a NPC to add it to the attack itemsPickupList");
                            panel.add(MobsInArea);
                            scrollPane_1 = new JScrollPane();
                            scrollPane_1.setBounds(10, 20, 390, 125);
                            panel.add(scrollPane_1);
                            mobsInAreaList = new JList(mobsInAreaModel);
                            scrollPane_1.setViewportView(mobsInAreaList);
                            mobsInAreaList.addListSelectionListener(this);
                            mobsInAreaList.setBorder(new LineBorder(
                                    new Color(0, 0, 0)));
                        }
                    }
                    {
                        chckbxUseFood = new JCheckBox(
                                "Eat Food (Eats when your HP is between HPLvl/2 and HPLvl/1.5)");
                        chckbxUseFood.setBounds(10, 300, 390, 13);

                        boolean set = Boolean.parseBoolean(settings.getProperty("UseFood","false"));
                        chckbxUseFood.setSelected(set);                        
                        panel.add(chckbxUseFood);
                    }
                }
                {
                    JPanel panel = new JPanel();
                    tabbedPane.addTab("Items", null, panel, null);
                    panel.setLayout(null);
                    {
                        scrollPane_2 = new JScrollPane();
                        itemsPickupModel = new DefaultListModel();
                        itemPickup = new JLabel();
                        itemPickup.setBounds(10, 7, 390, 12);
                        itemPickup.setText("These Items are to be picked up. Click an Item to remove it from the pickup itemsPickupList");
                        panel.add(itemPickup);                       
                        
                        String set = settings.getProperty("PickupItems", "");
                        if (set!=null && !set.isEmpty())  {
                        	for (String opt: set.split(":"))  {
                        		itemsPickupModel.addElement(opt);
                        	}
                        }
                        
                        itemsPickupList = new JList(itemsPickupModel);
                        itemsPickupList.setBorder(new LineBorder(new Color(0, 0, 0)));
                        scrollPane_2.setBounds(10, 20, 390, 125);
                        panel.add(scrollPane_2);
                        scrollPane_2.setViewportView(itemsPickupList);
                        itemsPickupList.addListSelectionListener(this);
                    }
                    {
                        AddItem = new JLabel();
                        AddItem.setBounds(10, 150, 100, 12);
                        AddItem.setText("Item ID");
                        panel.add(AddItem);
                    }
                    {
                        AddItem = new JLabel();
                        AddItem.setBounds(130, 150, 100, 12);
                        AddItem.setText("Item Name");
                        panel.add(AddItem);
                    }
                    {
                        btnAdd = new JButton("Add");
                        btnAdd.addActionListener(this);
                        btnAdd.setBounds(230, 163, 79, 23);
                        panel.add(btnAdd);
                    }
                    {
                        btnLoad = new JButton("Load");
                        btnLoad.addActionListener(this);
                        btnLoad.setBounds(319, 163, 79, 23);
                        panel.add(btnLoad);
                    }
                    {
                        txtItemId = new JTextField();
                        txtItemId.setBounds(10, 163, 100, 19);
                        panel.add(txtItemId);
                        txtItemId.setColumns(20);
                    }
                    {
                        txtItemName = new JTextField();
                        txtItemName.setBounds(120, 163, 100, 19);
                        panel.add(txtItemName);
                        txtItemName.setColumns(20);
                    }
                    {
                        chckbxBuryBones = new JCheckBox(
                                "Bury Bones (Will bury when inventory is full)");
                        chckbxBuryBones.addActionListener(this);
                        chckbxBuryBones.setBounds(10, 217, 390, 13);     
                                                
                        boolean set = Boolean.parseBoolean(settings.getProperty("BuryBones","false"));
                        chckbxBuryBones.setSelected(set);
                        panel.add(chckbxBuryBones);
                    }
                    {
                        chckbxCharms = new JCheckBox(
                                "Pickup Charms (Gold, Green, Crimson, Blue)");
                        chckbxCharms.addActionListener(this);
                        chckbxCharms.setBounds(10, 188, 390, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("Charms","false"));
                        chckbxCharms.setSelected(set);
                        panel.add(chckbxCharms);
                    }
                    {
                        chckbxuseBonesToPeaches = new JCheckBox(
                                "Bones to Peaches (Uses tab when out of food)");
                        chckbxuseBonesToPeaches.addActionListener(this);
                        chckbxuseBonesToPeaches.setBounds(10, 247, 390, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("BonesToPeaches","false"));
                        chckbxuseBonesToPeaches.setSelected(set);
                        panel.add(chckbxuseBonesToPeaches);
                    }
                }
                {
                    JPanel panel = new JPanel();
                    tabbedPane.addTab("Alch Items", null, panel, null);
                    panel.setLayout(null);
                    {
                        alchModel = new DefaultListModel();
                        Alch = new JLabel();
                        Alch.setBounds(10, 7, 380, 12);
                        Alch.setText("These are the Items you will cast high alch on if in your inventory");
                        panel.add(Alch);
                        
                        String set = settings.getProperty("Alch", "");
                        for (String opt: set.split(","))  {
                        	if (opt!=null && !opt.isEmpty())  {
                        		alchModel.addElement(Integer.decode(opt));
                        	}
                        }
                        
                        alchList = new JList(alchModel);
                        alchList.setBorder(new LineBorder(new Color(0, 0, 0)));
                        alchList.setBounds(10, 20, 390, 125);
                        
                        panel.add(alchList);
                        alchList.addListSelectionListener(this);
                    }
                    {
                        btnAdd2 = new JButton("Add");
                        btnAdd2.addActionListener(this);
                        btnAdd2.setBounds(180, 163, 89, 23);
                        panel.add(btnAdd2);
                    }
                    {
                        AddAlch = new JLabel();
                        AddAlch.setBounds(10, 150, 390, 12);
                        AddAlch.setText("If adding an item to alch only add the items ID, item name is not needed");
                        panel.add(AddAlch);
                    }
                    {
                        txtAlchable = new JTextField();
                        txtAlchable.setBounds(10, 163, 150, 19);
                        panel.add(txtAlchable);
                        txtAlchable.setText("");
                        txtAlchable.setColumns(20);
                    }
                    {
                        AddNote = new JLabel();
                        AddNote.setBounds(10, 200, 390, 12);
                        AddNote.setText("Note: High Alching currently only works with runes does not support staffs");
                        panel.add(AddNote);
                    }
                }
                {
                    JPanel panel = new JPanel();
                    tabbedPane.addTab("Potions", null, panel, null);
                    panel.setLayout(null);
                    {
                        chckbxUsePotion = new JCheckBox("Use Potions");
                        chckbxUsePotion.addActionListener(this);
                        chckbxUsePotion.setBounds(10, 10, 390, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("UsePotion","false"));
                        chckbxUsePotion.setSelected(set);
                        panel.add(chckbxUsePotion);
                    }
                    {
                        chckbxStrength = new JCheckBox(
                                "Strength Potions (Super, Regular or Combat)");
                        chckbxStrength.addActionListener(this);
                        chckbxStrength.setBounds(30, 50, 370, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("Strength","false"));
                        chckbxStrength.setSelected(set);
                        panel.add(chckbxStrength);
                    }
                    {
                        chckbxAttack = new JCheckBox(
                                "Attack Potion (Super, Regular or Combat)");
                        chckbxAttack.addActionListener(this);
                        chckbxAttack.setBounds(30, 70, 370, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("Attack","false"));
                        chckbxAttack.setSelected(set);
                        panel.add(chckbxAttack);
                    }
                    {
                        chckbxDefence = new JCheckBox(
                                "Defence Potion (Super and Regular)");
                        chckbxDefence.addActionListener(this);
                        chckbxDefence.setBounds(30, 90, 370, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("Defence","false"));
                        chckbxDefence.setSelected(set);
                        panel.add(chckbxDefence);
                    }
                    {
                        btnStartScript = new JButton("Start Script");
                        btnStartScript.addActionListener(this);
                        btnStartScript.setBounds(10, 375, 414, 23);
                        contentPane.add(btnStartScript);
                    }
                }
                {
                    JPanel panel = new JPanel();
                    tabbedPane.addTab("Spec", null, panel, null);
                    panel.setLayout(null);
                    {
                        chckbxUseSpec = new JCheckBox("Use Weap Special");
                        chckbxUseSpec.addActionListener(this);
                        chckbxUseSpec.setBounds(10, 10, 390, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("UseSpec","false"));
                        chckbxUseSpec.setSelected(set);
                        useSpec = set;
                        panel.add(chckbxUseSpec);
                    }
                    {
                        AddNote = new JLabel();
                        AddNote.setBounds(30, 38, 390, 12);
                        AddNote.setText("Original Weap ID");
                        panel.add(AddNote);
                    }
                    {
                        txtOrigWeap = new JTextField();
                        txtOrigWeap.setBounds(30, 50, 100, 19);
                               
                        String set = settings.getProperty("OrigWeap","");
                        txtOrigWeap.setText(set);

                        panel.add(txtOrigWeap);
                        txtOrigWeap.setColumns(20);
                    }
                    {
                        AddNote = new JLabel();
                        AddNote.setBounds(30, 74, 390, 12);
                        AddNote.setText("Original Shield ID");
                        panel.add(AddNote);
                    }
                    {
                        txtOrigShield = new JTextField();
                        txtOrigShield.setBounds(30, 87, 100, 19);
                        
		                 String set = settings.getProperty("OrigShield","");
		                 txtOrigShield.setText(set);

                        panel.add(txtOrigShield);
                        txtOrigShield.setColumns(20);
                    }
                    {
                        AddNote = new JLabel();
                        AddNote.setBounds(30, 111, 390, 12);
                        AddNote.setText("Spec Weapon ID");
                        panel.add(AddNote);
                    }
                    {
                        txtSpecWeap = new JTextField();
                        txtSpecWeap.setBounds(30, 124, 100, 19);
						
						String set = settings.getProperty("SpecWeap","");
						txtSpecWeap.setText(set);
						
                        panel.add(txtSpecWeap);
                        txtSpecWeap.setColumns(20);
                    }
                    {
                        AddNote = new JLabel();
                        AddNote.setBounds(30, 148, 390, 12);
                        AddNote.setText("Weapon Spec %");
                        panel.add(AddNote);
                    }
                    {
                        txtSpecPercent = new JTextField();
                        txtSpecPercent.setBounds(30, 161, 100, 19);
						
						String set = settings.getProperty("SpecPercent","");
						txtSpecPercent.setText(set);
						
                        panel.add(txtSpecPercent);
                        txtSpecPercent.setColumns(20);
                    }
                }
                {
                    JPanel panel = new JPanel();
                    tabbedPane.addTab("Other Options", null, panel, null);
                    panel.setLayout(null);
                    {
                        chckbxDisablePaint = new JCheckBox("Disable Paint");
                        chckbxDisablePaint.addActionListener(this);
                        chckbxDisablePaint.setBounds(10, 10, 390, 13);
                        
                        boolean set = Boolean.parseBoolean(settings.getProperty("DisablePaint","false"));
                        chckbxDisablePaint.setSelected(set);
                        panel.add(chckbxDisablePaint);
                    }
                    setVisible(true);
                    npcupdater.start();
                }
            }
        }

        public void valueChanged(final ListSelectionEvent arg0) {
            if (arg0.getSource() == itemsPickupList) {
                String i = (String) itemsPickupList.getSelectedValue();
                if (i == null) {
                    return;
                }
                itemsPickupModel.remove(itemsPickupList.getSelectedIndex());
            }
            if (arg0.getSource() == mobsInAreaList) {
                String text = (String) mobsInAreaList.getSelectedValue();
                if ((text == null) || text.isEmpty()) {
                    return;
                }
                mobsToAttackModel.addElement(text);
                mobsInAreaModel.remove(mobsInAreaList.getSelectedIndex());
            }
            if (arg0.getSource() == mobsToAttackList) {
                String text = (String) mobsToAttackList.getSelectedValue();
                if ((text == null) || text.isEmpty()) {
                    return;
                }
                mobsInAreaModel.addElement(text);
                mobsToAttackModel.remove(mobsToAttackList.getSelectedIndex());
            }
            if (arg0.getSource() == alchList) {
                Integer i = (Integer) alchList.getSelectedValue();
                if (i == null) {
                    return;
                }
                alchModel.remove(alchList.getSelectedIndex());
            }
        }

        public void actionPerformed(final ActionEvent arg0) {
            if (arg0.getSource() == chckbxuseBonesToPeaches) {
                useBTP = chckbxuseBonesToPeaches.isSelected();
                settings.setProperty("BonesToPeaches", useBTP.toString());
            }
            if (arg0.getSource() == chckbxBuryBones) {
                buryBones = chckbxBuryBones.isSelected();
                settings.setProperty("BuryBones", buryBones.toString());
            }
            if (arg0.getSource() == chckbxDisablePaint) {
                paint = !chckbxDisablePaint.isSelected();
                Boolean disablePaint = !paint;
                settings.setProperty("DisablePaint", disablePaint.toString());
            }
            if (arg0.getSource() == chckbxUsePotion) {
                usepotion = chckbxUsePotion.isSelected();
                settings.setProperty("UsePotion", usepotion.toString());
            }
            if (arg0.getSource() == chckbxStrength) {
                strength = chckbxStrength.isSelected();
                settings.setProperty("Strength", strength.toString());
            }
            if (arg0.getSource() == chckbxDefence) {
                defence = chckbxDefence.isSelected();
                settings.setProperty("Defence", defence.toString());
            }
            if (arg0.getSource() == chckbxAttack) {
                attack = chckbxAttack.isSelected();
                settings.setProperty("Attack", attack.toString());
            }
            if (arg0.getSource() == chckbxCharms) {
                charms = chckbxCharms.isSelected();
                settings.setProperty("Charms", charms.toString());
            }
            if (arg0.getSource() == chckbxUseSpec) {
                useSpec = chckbxUseSpec.isSelected();
                settings.setProperty("UseSpec", useSpec.toString());
            }
            if (arg0.getSource() == btnAdd) {
                try {
                    String s = (txtItemId.getText());
                    String j = (txtItemName.getText());
                    String firstLetter = j.substring(0, 1);
                    String remainder = j.substring(1);
                    String n = firstLetter.toUpperCase()
                            + remainder.toLowerCase();
                    itemsPickupModel.addElement(s + "," + n);
                    txtItemId.setText("");
                    txtItemName.setText("");
                } catch (Exception ignored) {
                }
            }
            if (arg0.getSource() == btnAdd2) {
                try {
                    Integer i = Integer.parseInt(txtAlchable.getText());
                    alchModel.addElement(i);
                } catch (Exception ignored) {
                }
            }
            
            if (arg0.getSource() == btnLoad) {
                try {
                	// The imported file format is as follows:
                	//  item1ID,item1Name
                	//  item2ID,item2Name
                	//  etc...
                    JFileChooser fc = new JFileChooser();
                    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        BufferedReader in = new BufferedReader(new FileReader(
                                fc.getSelectedFile().getPath()));
                        String str;
                        while ((str = in.readLine()) != null) {
                            if (!itemsPickupModel.contains(str)) {
                                String delims = "[,]";
                                String[] tokens = str.split(delims);
                                String s = (tokens[1]);
                                String firstLetter = s.substring(0, 1);
                                String remainder = s.substring(1);
                                String cap = firstLetter.toUpperCase()
                                        + remainder.toLowerCase();
                                itemsPickupModel.addElement(tokens[0] + "," + cap);
                            }
                        }
                        in.close();                     
                    }
                } catch (IOException e) {
                }
            }
            
            if (arg0.getSource() == btnStartScript) {
            	// For saving the settings
            	FileOutputStream settingsFile = null;
            	
            	// Save the pick up items list setting.
                String set = "";
                for (int i=0; i<itemsPickupModel.size(); i++)  {
                	String elem = (String)itemsPickupModel.get(i);
                	
                	if (set.isEmpty())  {
                		set = elem;
                	}
                	else  {
                		set = set.concat(":"+elem);
                	}
                }
                settings.put("PickupItems", set);
                
            	// Save the alch items list setting.
                set = "";
                for (int i=0; i<alchModel.size(); i++)  {
                	Integer elem = (Integer) alchModel.get(i);
                	
                	if (set.isEmpty())  {
                		set = elem.toString();
                	}
                	else  {
                		set = set.concat(","+elem.toString());
                	}
                }
                settings.put("Alch", set);
                
            	// Save the attack mobs list setting.
                set = "";
                for (int i=0; i<mobsToAttackModel.size(); i++)  {
                	String elem = (String)mobsToAttackModel.get(i);
                	
                	if (set.isEmpty())  {
                		set = elem;
                	}
                	else  {
                		set = set.concat(":"+elem);
                	}
                }
                settings.put("MobsToAttack", set);
                
                npcID = new int[mobsToAttackModel.size()];
                for (int i = 0; i < mobsToAttackModel.getSize(); i++) {
                    String idNameLevel = (String) mobsToAttackModel.get(i);
                    String delims = "[ -]";
                    String[] tokens = idNameLevel.split(delims);
                    npcID[i] = Integer.parseInt(tokens[0]);
                }
                
                itemids = new int[itemsPickupModel.size()];
                for (int i = 0; i < itemsPickupModel.getSize(); i++) {
                    String delims = "[,]";
                    String[] tokens = ((String) itemsPickupModel.get(i)).split(delims);
                    int in = Integer.parseInt(tokens[0]);
                    itemids[i] = in;
                }
                
                itemnames = new String[itemsPickupModel.size()];
                for (int i = 0; i < itemsPickupModel.getSize(); i++) {
                    String delims = "[,]";
                    String[] tokens = ((String) itemsPickupModel.get(i)).split(delims);
                    itemnames[i] = tokens[1];
                }
                
                alchable = new int[alchModel.size()];
                for (int i = 0; i < alchModel.getSize(); i++) {
                    int in = (Integer) alchModel.get(i);
                    alchable[i] = in;
                    log("Alching - " + alchable[i]);
                }
                
                if (txtOrigWeap.getText() == null
                        || txtOrigWeap.getText().isEmpty()) {
                    origWeap = 0;
                } else {
                    origWeap = Integer.parseInt(txtOrigWeap.getText());
                }
                settings.setProperty("OrigWeap", origWeap.toString());
                
                if (txtOrigShield.getText() == null
                        || txtOrigShield.getText().isEmpty()) {
                    origShield = 0;
                } else {
                    origShield = Integer.parseInt(txtOrigShield.getText());
                }
                settings.setProperty("OrigShield", origShield.toString());
                
                if (txtSpecWeap.getText() == null
                        || txtSpecWeap.getText().isEmpty()) {
                    specWeap = 0;
                } else {
                    specWeap = Integer.parseInt(txtSpecWeap.getText());
                }
                settings.setProperty("SpecWeap", specWeap.toString());
                
                if (txtSpecPercent.getText() == null
                        || txtSpecPercent.getText().isEmpty()) {
                    specPercent = 0;
                } else {
                    specPercent = Integer.parseInt(txtSpecPercent.getText());
                }
                settings.setProperty("SpecPercent", specPercent.toString());

                useFood = chckbxUseFood.isSelected();
                settings.setProperty("UseFood", useFood.toString());
                
                try {
					settingsFile = new FileOutputStream(settingsFileName);
					settings.store(settingsFile, "FoulFighter Saved Settings");  
					settingsFile.close();
	            } catch (FileNotFoundException e) {
					e.printStackTrace();				
				} catch (IOException e) {
					e.printStackTrace();
				}
                
                dispose();
            }
        }
        final Thread npcupdater = new Thread() {

            @Override
            public void run() {
                while (isVisible()) {
                    final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
                    for (final int element : validNPCs) {
                        Node localNode = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
                        if (localNode == null || !(localNode instanceof RSNPCNode)) {
                            continue;
                        }
                        RSNPC Monster = new RSNPC(((RSNPCNode) localNode).getRSNPC());
                        String monster = Monster.getID() + " - " + Monster.getName() + "(Level " + Monster.getLevel()+")";
                        if (!mobsInAreaModel.contains(monster) && 
                        	!mobsToAttackModel.contains(monster) &&
                            (Monster != null)) {
                            mobsInAreaModel.add(mobsInAreaModel.getSize(), monster);
                        }
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }
}