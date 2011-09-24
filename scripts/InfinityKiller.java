
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Infinity dev team"},
email = "bobbybighoof@gmail.com",
website = "http://www.lazygamerz.org/forums/"
+ "index.php?topic=4989.msg46001#msg46001",
category = "Combat",
name = "Infinity Killer",
version = 1.00,
summary = "Run this script after you have logged into<br />"
+ "your game and put your log in in the <br />"
+ "location that you want to kill things.<br />"
+ "Remember that the NPC name that you type<br>"
+ "in the NPC name box is case sensitive.<br />"
+ "You must be exact on what you type there for<br />"
+ "the script to reconize the NPC.<br />"
+ "If you set the setting for bones to peaches<br />"
+ "be sure to have all the bones to peaches tabs<br />"
+ "that you want to use in your inventory.<br />"
+ "The script only uses bones to peaches tabs.<br />"
+ "If you set it to pick up charms you must<br />"
+ "already have the charms you want it to pick<br />"
+ "up in your inventory as well.<br />",
notes = " Mar. 10, 2011 - version 1.0 started<br />"
+ "Jun. 12, 2011 - Updated script manifest to reflect<br />"
+ "new features.<br />"
+ "Jun. 18, 2011 - updated repaint to reflect new<br />"
+ "progress paint logic designed in the client.<br />"
+ "Jun. 28, 2011 - Set up script for Infinity 2.0 client.<br />"
+ "July 20, 2011 - set up on site posts for the script<br />"
+ "to allow for website link to script.<br />"
+ "Updated manifest notes to reflect this.<br />",
description = "<style type='text/css'>"
+ "body {background:url("
+ "'http://lazygamerz.org/client/images/back_1.png') repeat}"
+ "</style><html><head><center>"
+ "<img src=\"http://lazygamerz.org/client/images/logo.png\"></head></style>"
+ "<table style=border-collapse:collapse cellpadding=0 cellspacing=0>"
+ "<tr><td class=style2 align=right bgcolor=#00FFFF colspan=2><p align=center>"
+ "<font size=5>Infinity Killer</font></td></tr>"
+ "<tr><td class=style2 align=center bgcolor=#C0C0C0 color=#000000 colspan=2>"
+ "<font size=2><font color=#0000FF>How to set up and start this script."
+ "</font></font size><br />"
+ "Start this script in the location<br />"
+ "of the NPCs you would like to kill.<br />"
+ "Be careful of places with doors or walls<br />"
+ "that you might get stuck at as this script does not<br />"
+ "walk around walls and building or go through doors.</td></tr>"
+ "<tr><td class=style2 bgcolor=#C0C0C0 color=#000000><p align=center>"
+ "NPC Name: </td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<input name=monster maxlength=20 size=10  value=\"Moss giant\"></td></tr>"
+ "<tr><td class=style2 bgcolor=#C0C0C0 color=#000000><p align=center>"
+ "Combat Style: </td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<select name=fightType><option>Attack</option><option>Strength</option><option>Defense</option><option>Range</option><option>Mage</option></select></td></tr>"
+ "<tr><td class=style2 bgcolor=#C0C0C0 color=#000000><p align=center>"
+ "Pick Up Charms: </td><td class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<font size=3><input type='checkbox' name='charm' value='true'></font size>: Yes</td></tr>"
+ "<tr><td class=style2 bgcolor=#C0C0C0 color=#000000><p align=center>"
+ "Handle Bones: </td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<select name=handle><option selected>Do Nothing</option><option>Bury Bones</option><option>Bones to Banana</option><option>Bones to Peaches</option></select></td></tr>"
+ "<tr><td class=style2 bgcolor=#C0C0C0 color=#000000><p align=center>"
+ "Bone Type: </td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<select name=bone1><option>None</option><option>Regular bones</option><option>Big bones</option></select></td></tr>"
+ "<tr><td class=style2 align=right bgcolor=#00FFFF colspan=2><p align=center>"
+ "Eating Options</td></tr>"
+ "<tr><td class=style2 bgcolor=#C0C0C0 color=#000000><p align=center>"
+ "Leftover Bones: </td><td class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<select name=extras><option>Keep Killing</option><option>Burry Bones</option></select></td></tr>"
+ "<tr><td class=style2 bgcolor=#C0C0C0 color=#000000><p align=center>"
+ "Eat Peaches Between: </td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
+ "<input name=heal1 maxlength=4 size=4  value=20> and "
+ "<input name=heal2 maxlength=4 size=4  value=30> HP</td></tr>"
+ "<tr><td class=style2 align=right bgcolor=#00FFFF colspan=2></td></tr>"
+ "</table></center></body></html>")
public class InfinityKiller extends Script implements PaintListener, MessageListener {

    public BufferedImage img = null;
    public String monster = "";
    public int BoneID = -1;
    public int heal1 = 0;
    public int heal2 = 0;
    public int trainingSkill = 0;
    public int combatTrain = 0;
    public int healRandom = 0;
    public long startTime = System.currentTimeMillis();
    public int bonecount = 0;
    public int lvl = 0;
    public long exp = 0;
    public long hourlyEXP = 0;
    int startXP = 0;
    int startLvl = 0;
    public int start = 0;
    public String status = "";
    public String PickB = "No";
    public String PeachB = "No";
    public boolean killbat = false;
    public boolean setAltitude = false;
    public boolean charmPickup = false;
    public boolean burryExtra = false;
    public int runes[] = {
        554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565
    };
    public int projectial[] = {
        882, 884, 886
    };
    public int charms[] = {
        12158, 12159, 12160, 12163
    };

    @Override
    protected int getMouseSpeed() {
        return 8;
    }

    @Override
    public boolean onStart(Map<String, String> args) {
        try {
            final URL url = new URL(
                    "http://lazygamerz.org/client/images/combat3.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
        }
        final String fightType = args.get("fightType");
        final String bone2 = args.get("bone1");
        final String extra = args.get("extras");
        monster = args.get("monster");
        heal1 = Integer.parseInt(args.get("heal1"));
        heal2 = Integer.parseInt(args.get("heal2"));
        healRandom = random(heal1, heal2);
        PickB = args.get("handle");
        if (args.get("charm") != null) {
            charmPickup = true;
        }

        if (extra.equals("Burry Bones")) {
            burryExtra = true;
        }

        if (fightType.equals("Range")) {
            trainingSkill = Skills.RANGE;
            combatTrain = 11;
        } else if (fightType.equals("Mage")) {
            trainingSkill = Skills.MAGIC;
            combatTrain = 11;
        } else if (fightType.equals("Strength")) {
            trainingSkill = Skills.STRENGTH;
            combatTrain = 12;
        } else if (fightType.equals("Attack")) {
            trainingSkill = Skills.ATTACK;
            combatTrain = 11;
        } else if (fightType.equals("Defense")) {
            trainingSkill = Skills.DEFENSE;
            combatTrain = 14;
        }

        startXP = skills.getCurrentXP(trainingSkill);
        startLvl = skills.getCurrentLvl(trainingSkill);

        if (bone2.equals("Regular bones")) {
            BoneID = 526;
        } else if (bone2.equals("Big bones")) {
            log("Big Bones");
            BoneID = 532;
        }

        return true;
    }

    private enum State {

        eat, tabs, animation, attack, burybones, pickbones, sleep, charm,
        trune, tarrow, stop, drop, bone;
    }

    private State getState() {
        if (game.isLoggedIn()) {

            if (player.getMine().getInteracting() != null) {
                return State.animation;
            } else if (skills.getCurrentLP() < healRandom) {
                healRandom = random(heal1, heal2);
                if (inventory.contains(6883) || inventory.contains(1963)) {
                    return State.eat;
                } else {
                    return State.tabs;
                }
            } else if (inventory.contains(995)) {
                return State.drop;
            } else if (ground.getItemByID(5, charms) != null) {
                if (charmPickup) {
                    if (inventory.isFull()) {
                        if (inventory.contains(charms)) {
                            return State.charm;
                        } else {
                            return State.attack;
                        }
                    }
                    return State.charm;
                } else {
                    return State.attack;
                }
            } else if (ground.getItemByID(5, runes) != null) {
                if (inventory.isFull()) {
                    if (inventory.contains(runes)) {
                        return State.trune;
                    } else {
                        return State.attack;
                    }
                }
                return State.trune;
            } else if (ground.getItemByID(5, projectial) != null) {
                if (inventory.isFull()) {
                    if (inventory.contains(projectial)) {
                        return State.tarrow;
                    } else {
                        return State.attack;
                    }
                }
                return State.tarrow;
            } else if (ground.getItemByID(5, BoneID) != null) {
                if (PickB.equals("Bury Bones")) {
                    if (player.getMine().getInteracting() == null) {
                        if (inventory.isFull()) {
                            if (inventory.contains(BoneID)) {
                                return State.burybones;
                            }
                        }
                        return State.pickbones;
                    }
                } else if (PickB.equals("Bones to Peaches")
                        || PickB.equals("Bones to Banana")) {
                    if (!inventory.isFull()) {
                        return State.pickbones;
                    } else if (inventory.isFull()) {
                        if (burryExtra) {
                            return State.bone;
                        } else {
                            return State.attack;
                        }
                    }
                }
            }
            return State.attack;
        }
        return State.sleep;
    }

    public int loop() {
        if (!game.isLoggedIn()) {
            return random(800, 1000);
        }

        try {
            if (game.isLoggedIn()) {
                if (!killbat) {
                    game.openTab(Game.tabAttack);
                    wait(random(500, 800));
                    if (iface.get(884).isValid()) {
                        iface.clickChild(884, combatTrain);
                        game.openTab(Game.tabInventory);
                        killbat = true;
                        return random(400, 800);
                    }
                }
                if (!setAltitude) {
                    camera.setAltitude(true);
                    wait(random(500, 800));
                    setAltitude = true;
                    return random(400, 800);
                }

                State state = getState();
                debug(String.format("State=%s", state.toString()));

                switch (getState()) {
                    case drop:
                        inventory.clickItem(995, "Drop Coins");
                        return 25;
                    case trune:
                        status = "Picking up Runes";
                        RSGroundItem prunes = ground.getItemByID(6, runes);

                        if (!prunes.isOnScreen()) {
                            walk.to(prunes);
                        } else if (!player.getMine().isMoving()) {
                            prunes.action("Take" + prunes.getItem().getName());
                            return random(800, 1000);
                        }

                        return 25;
                    case tarrow:
                        status = "Picking up Arrows";
                        RSGroundItem parrow = ground.getItemByID(6, projectial);

                        if (!parrow.isOnScreen()) {
                            walk.to(parrow);
                        } else if (!player.getMine().isMoving()) {
                            parrow.action("Take", parrow.getItem().getName());
                            return random(800, 1000);
                        }

                        return 25;
                    case charm:
                        status = "Picking up Charms";
                        RSGroundItem charm = ground.getItemByID(6, charms);

                        if (!charm.isOnScreen()) {
                            walk.to(charm);
                        } else if (!player.getMine().isMoving()) {
                            charm.action("Take", charm.getItem().getName());
                            return random(800, 1000);
                        }

                        return 25;
                    case eat:
                        status = "Eating food";
                        if (inventory.contains(1963)) {
                            inventory.clickItem(1963, "Eat");
                        } else if (inventory.contains(6883)) {
                            inventory.clickItem(6883, "Eat");
                        }
                        for (int i = 0; i < 100; i++) {
                            wait(random(25, 50));
                            if (player.getMine().getInteracting() != null) {
                                break;
                            }
                        }
                        return 25;

                    case tabs:
                        status = "Making food";
                        if (PickB.equals("Bones to Banana")) {
                            if (inventory.contains(8014)) {
                                inventory.clickItem(8014, "Break");
                                wait(random(500, 1000));
                            } else {
                                log("Out of Bones to Banana tabs.");
                                return -1;
                            }
                        } else if (PickB.equals("Bones to Peaches")) {
                            if (inventory.contains(8015)) {
                                inventory.clickItem(8015, "Break");
                                wait(random(500, 1000));
                            } else {
                                log("Out of Bones to Peaches tabs.");
                                return -1;
                            }
                        }
                        for (int i = 0; i < 100; i++) {
                            wait(random(25, 50));
                            if (player.getMine().getInteracting() != null) {
                                break;
                            }
                        }
                        return 25;

                    case animation:
                        status = "Fighting - " + monster;
                        wait(random(750, 1250));
                        for (int i = 0; i < 100; i++) {
                            wait(random(100, 200));
                            if (player.getMine().getInteracting() == null) {
                                break;
                            }
                        }
                        return 25;

                    case attack:
                        status = "Attack - " + monster;
                        if ((npc.getNearestFreeByName(monster) == null) && (start == 0)) {
                            log.warning("No NPC called '" + monster + "' could not be found.");
                            log.warning("Please check the case sensitivity and if NPC is on the game screen..");
                            return -1;
                        }
                        if (start == 0) {
                            start++;
                        }
                        if (calculate.pointOnScreen(npc.getNearestFreeByName(monster).getScreenLocation())) {
                            RSNPC npc1 = npc.getNearestFreeByName(monster);
                            npc.action(npc.getNearestFreeByName(monster), "Attack");
                            wait(random(1000, 2000));
                            for (int i = 0; i < 100; i++) {
                                wait(random(25, 50));
                                if (!player.getMine().isMoving()) {
                                    break;
                                }
                            }
                            for (int i = 0; i < 100; i++) {
                                wait(random(20, 30));
                                if (npc.getNearestToAttackByName(monster) != npc1) {
                                    break;
                                }
                                if (player.getMine().getInteracting() != null) {
                                    break;
                                }
                            }
                        } else {
                            walk.tileMM((npc.getNearestFreeByName(monster).getLocation()));
                            wait(random(1000, 2000));
                            for (int i = 0; i < 100; i++) {
                                wait(random(25, 50));
                                if (!player.getMine().isMoving()) {
                                    break;
                                }
                            }
                        }
                        return 100;

                    case pickbones:
                        status = "Picking up Bones";
                        RSGroundItem boneloc = ground.getItemByID(6, BoneID);

                        if (!boneloc.isOnScreen()) {
                            walk.to(boneloc);
                        } else if (!player.getMine().isMoving()) {
                            boneloc.action("Take" + boneloc.getItem().getName());
                            return random(800, 1000);
                        }

                        return 25;

                    case burybones:
                        status = "Burying Bones";
                        while (inventory.contains(BoneID)) {
                            inventory.clickItem(BoneID, "Bury ");
                        }

                        return 25;

                    case bone:
                        status = "Burying Bones";
                        inventory.clickItem(BoneID, "Bury ");
                        wait(random(1000, 1200));
                        return 25;
                    case sleep:
                        status = "Sleep";
                        wait(random(100, 200));
                        return 0;

                    case stop:
                        status = "Stop Script";
                        return -1;

                }
            }
        } catch (final Exception e) {
            log.warning(e.toString() + " Caused non fatal loop failure. Resetting loop ");
        }
        return 100;
    }

    public void messageReceived(final MessageEvent e) {
        final String word = e.getMessage().toLowerCase();
        if (word.contains("bury the bones")) {
            bonecount++;
        }
    }

    /**
     * Sets the {@link render}
     *
     * @param Repaint - a {@link render}
     */
    public void onRepaint(Graphics g) {
        final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
        int RealLvL = skills.getRealLvl(trainingSkill);
        int currentXP = skills.getCurrentXP(trainingSkill);
        int currentLVL = skills.getCurrentLvl(trainingSkill);
        int currentPurLVL = skills.getPercentToNextLvl(trainingSkill);
        int XPToNextLvL = skills.getXPToNextLvl(trainingSkill);
        final double XP = 68;//special skill define

        if (game.isLoggedIn()) {
            //sets up the paint visual run timer for the user
            //is is set up in 00 set up rather than typical 0
            long millis = System.currentTimeMillis() - startTime;
            String time = Timer.format(millis);

            //base number used for left alignment of text. I could just type 9 in all x spaces
            //but what fun would that be?
            final int x = 315;
            final int x1 = 400;

            int gainedXP = currentXP - startXP;
            final int expPerHour = (int) ((currentXP - startXP) * 3600000.0 / (double) millis);
            int gainedLVL = currentLVL - startLvl;
            final int fillBar = (int) (2 * (double) currentPurLVL);

            //This is the box that will draw to insure that if the image does not load
            //that there will still be a black box behind the words to look decent for the user
            g.setColor(Color.BLACK);
            g.fill3DRect(310, 342, 210, 135, true);

            g.setColor(Color.RED);//color changes for % box
            g.fill3DRect(318, 342, 162, 14, true);//fill % box
            g.setColor(Color.GREEN);//% fill bar color
            g.fill3DRect(318, 342, fillBar, 14, true);

            g.drawImage(img, 250, 318, null);
            g.setFont(new Font("arial", Font.PLAIN, 10));
            g.setColor(new Color(225, 225, 225, 255));
            g.drawString("Infinity " + properties.name(), x, 378);
            g.drawString("Run Time: " + time, x, 394);
            g.drawString("Bones Burried: " + bonecount, x, 410);
            g.drawString("Lvl: " + currentLVL, x, 426);
            g.drawString("Gained: " + gainedLVL, x1, 426);
            g.drawString("Xp: " + gainedXP, x, 442);
            g.drawString("PH: " + expPerHour, x1, 442);
            g.drawString("Xp To Next Level: " + XPToNextLvL, x, 458);
            g.drawString("Status: " + status, x, 474);

            //version define
            g.drawString(""+ properties.version(), 494, 474);
            //percent of fill bar done
            g.drawString(""+ currentPurLVL, 502, 355);

            //Draw player
            RSTile p = player.getMine().getLocation();
            Point n = Calculations.tileToScreen(p.getX(), p.getY(), 0);
            Point xp = Calculations.tileToScreen(p.getX() + 1, p.getY(), 0);
            Point yp = Calculations.tileToScreen(p.getX(), p.getY() + 1, 0);
            Point xm = Calculations.tileToScreen(p.getX() - 1, p.getY(), 0);
            Point ym = Calculations.tileToScreen(p.getX(), p.getY() - 1, 0);
            g.setColor(new Color(125, 20, 145, 75));
            g.fillRect(n.x, n.y, 10, 10);
            g.setColor(Color.RED);
            g.fillRect(xp.x, xp.y, 2, 2);
            g.fillRect(yp.x, yp.y, 2, 2);
            g.fillRect(xm.x, xm.y, 2, 2);
            g.fillRect(ym.x, ym.y, 2, 2);
            g.setColor(Color.BLACK);
            g.drawRect(n.x, n.y, 10, 10);

            //DRAW MOUSE
            final Mouse m = Bot.getClient().getMouse();
            final Point loc = mouse.getLocation();
            if (m == null) {
                return;
            }

            final int mouse_x = m.getX();
            final int mouse_y = m.getY();
            final int mouse_press_x = m.getPressX();
            final int mouse_press_y = m.getPressY();
            final long mouse_press_time = mouse.getPressTime();

            g.setColor(Color.YELLOW);
            g.drawLine(mouse_x - 8, mouse_y - 8, mouse_x + 8, mouse_y + 8);
            g.drawLine(mouse_x + 8, mouse_y - 8, mouse_x - 8, mouse_y + 8);
            if (System.currentTimeMillis() - mouse_press_time < 1000) {
                g.setColor(Color.GREEN);
                g.drawLine(mouse_press_x - 8, mouse_press_y - 8, mouse_press_x + 8, mouse_press_y + 8);
                g.drawLine(mouse_press_x + 8, mouse_press_y - 8, mouse_press_x - 8, mouse_press_y + 8);
                g.drawLine(mouse_press_x - 7, mouse_press_y - 7, mouse_press_x + 7, mouse_press_y + 7);
                g.drawLine(mouse_press_x + 7, mouse_press_y - 7, mouse_press_x - 7, mouse_press_y + 7);
                g.drawLine(mouse_press_x - 6, mouse_press_y - 6, mouse_press_x + 6, mouse_press_y + 6);
                g.drawLine(mouse_press_x + 6, mouse_press_y - 6, mouse_press_x - 6, mouse_press_y + 6);
                g.setColor(new Color(0, 0, 0, 50));
                g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
            } else {
                g.setColor(Color.BLACK);
            }

            g.drawLine(0, loc.y, 766, loc.y);
            g.drawLine(loc.x, 0, loc.x, 505);
        }
    }
}
