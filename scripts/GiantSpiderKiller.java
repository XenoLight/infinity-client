
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"zr0k5"}, category = "Combat", name = "GiantSpiderKiller", version = 1.6, description = ""
+ "<html><head><style type='text/css'>body{margin-left:5px;}</style></head><body><h2>Giant Spider Killer by zr0k5</h2><hr>"
+ "Food ID: <input type='text' size='15' name='food'><br>Some common foods:<ul><li>Lobster = 379</li><li>Monkfish = 7946</li><li>Shark = 385</li></ul>"
+ "Eat when HP is between <input type='text' size='5' name='eat1' value='300'> and <input type='text' size='5' name='eat2' value='350'><hr>"
+ "<input type='checkbox' name='edge' value='use'> Bank at edgeville <br>"
+ "<i>(If not checked, will bank at varrock.)</i><hr>"
+ "Teleport to Varrock: <select name='teleport'>"
+ "<option>don't use</option><option>use varrock telerunes</option><option>use varrock teletablets</option></select><hr>"
+ "Room changing: <select name='room'><option>change randomly every run</option><option>only fight in room 1</option>"
+ "<option>only fight in room 2</option></select><hr>"
+ "Select potions to use:<br><input type='checkbox' name='str' value='use'>Strength potion"
+ " (withdraw <input type='text' size='1' name='str_amount' value='1'> every run) <br>"
+ "<input type='checkbox' name='super_str' value='use'>Super Strength potion "
+ " (withdraw <input type='text' size='1' name='super_str_amount' value='1'> every run)<br>"
+ "<input type='checkbox' name='super_att' value='use'>Super Attack potion"
+ " (withdraw <input type='text' size='1' name='super_att_amount' value='1'> every run)<br>"
+ "<input type='checkbox' name='super_def' value='use'>Super Defense potion"
+ " (withdraw <input type='text' size='1' name='super_def_amount' value='1'> every run)<hr></body></html>")
public class GiantSpiderKiller extends Script implements PaintListener, MessageListener {

    private final double version = getClass().getAnnotation(
            ScriptManifest.class).version();
    private int foodID;
    private int eatAt[];
    private int room;
    private int stats[][];
    private int base;
    private int style;
    private int tries;
    private final int potionID[][] = {{119, 117, 115, 113},
        {161, 159, 157, 2440}, {149, 147, 145, 2436},
        {167, 165, 163, 2442}};
    private final int rewardID[] = {
        11640, 2528, 6199, 14664
    };
    private int potions[] = {0, 0, 0, 0};
    private int amounts[] = {0, 0, 0, 0};
    private final int tabConstants[] = {
        TAB_FRIENDS, TAB_CLAN, TAB_QUESTS, TAB_STATS, TAB_NOTES
    };
    private final int statConstants[] = {
        1, 2, 4, 21
    };
    private final int skillConstants[] = {
        STAT_ATTACK, STAT_STRENGTH, STAT_DEFENSE
    };
    private boolean needBank;
    private boolean atSpiders;
    private boolean changeRooms;
    private boolean teleport;
    private boolean pots;
    private boolean tabs;
    private boolean edge;
    private boolean allowPaint;
    private long startTime;
    private long mouseAntiban;
    private long antiban;
    private RSTile loc;
    private RSTile bankTile = new RSTile(3189, 3437);
    private final RSTile entranceTile = new RSTile(3081, 3421);
    private final RSTile bankingPath[] = {new RSTile(3081, 3421),
        new RSTile(3086, 3421), new RSTile(3090, 3420),
        new RSTile(3096, 3420), new RSTile(3101, 3421),
        new RSTile(3106, 3421), new RSTile(3113, 3420),
        new RSTile(3119, 3418), new RSTile(3125, 3415),
        new RSTile(3131, 3416), new RSTile(3137, 3417),
        new RSTile(3144, 3417), new RSTile(3151, 3417),
        new RSTile(3157, 3419), new RSTile(3163, 3423),
        new RSTile(3167, 3427), new RSTile(3174, 3428),
        new RSTile(3180, 3429), new RSTile(3185, 3432),
        new RSTile(3186, 3436), new RSTile(3189, 3437)};
    private final RSTile edgeBankingPath[] = {new RSTile(3081, 3421),
        new RSTile(3085, 3424), new RSTile(3089, 3428),
        new RSTile(3089, 3433), new RSTile(3090, 3439),
        new RSTile(3091, 3445), new RSTile(3090, 3453),
        new RSTile(3091, 3459), new RSTile(3085, 3465),
        new RSTile(3081, 3469), new RSTile(3081, 3475),
        new RSTile(3080, 3480), new RSTile(3084, 3485),
        new RSTile(3088, 3488), new RSTile(3094, 3491)};
    private final RSTile doors[] = {new RSTile(2132, 5257),
        new RSTile(2132, 5260), new RSTile(2132, 5278),
        new RSTile(2132, 5281), new RSTile(2138, 5294),
        new RSTile(2141, 5294), new RSTile(2148, 5299),
        new RSTile(2148, 5302)};
    private final RSArea floor1 = new RSArea(new RSTile(1856, 5236),
            new RSTile(1866, 5245));
    private final RSArea floor1Middle = new RSArea(new RSTile(1900, 5210),
            new RSTile(1917, 5230));
    private final RSArea floor2 = new RSArea(new RSTile(2037, 5239),
            new RSTile(2046, 5246));
    private final RSArea floor2Middle = new RSArea(new RSTile(2009, 5203),
            new RSTile(2034, 5228));
    private final RSArea floor3 = new RSArea(new RSTile(2116, 5250),
            new RSTile(2131, 5259));
    private final RSArea floor3Front = new RSArea(new RSTile(2131, 5253),
            new RSTile(2133, 5256));
    private final RSArea doors1 = new RSArea(new RSTile(2132, 5257),
            new RSTile(2133, 5259));
    private final RSArea doors2 = new RSArea(new RSTile(2132, 5279),
            new RSTile(2133, 5281));
    private final RSArea doors3 = new RSArea(new RSTile(2138, 5294),
            new RSTile(2140, 5295));
    private final RSArea doors4 = new RSArea(new RSTile(2148, 5299),
            new RSTile(2149, 5301));
    private final RSArea room1 = new RSArea(new RSTile(2117, 5267), new RSTile(2134, 5277));
    private final RSTile room2 = new RSTile(2150, 5306);
    private int training = -1;
    private String trainingName;
    private int totalGained;
    private int xpGained;
    private int xpHour;
    private int currentLevel;
    private int levelsGained;
    private int percent;
    private int TNL;
    private long runTime;
    private long changeTime;
    private long timeTNL;
    private long currentTime;

    @Override
    public boolean onStart(Map<String, String> args) {
        if (!game.isLoggedIn()) {
            log("login before starting!");
            return false;
        }
        try {
            foodID = Integer.parseInt(args.get("food"));
            eatAt = new int[]{0, Integer.parseInt(args.get("eat1")),
                        Integer.parseInt(args.get("eat2"))};
            eatAt[0] = random(eatAt[1], eatAt[2]);
        } catch (NumberFormatException e) {
            log("you didn't type valid number! (note: food ID must be number)");
            return false;
        }
        if (args.get("edge") != null
                && args.get("edge").equals("use")) {
            edge = true;
            bankTile = new RSTile(3094, 3491);
        }
        if (args.get("room") != null) {
            if (!args.get("room").equals("change randomly every run")) {
                changeRooms = false;
                if (args.get("room").equals("only fight in room 1")) {
                    room = 1;
                } else {
                    room = 2;
                }
            } else {
                changeRooms = true;
            }
        }
        if (args.get("teleport") != null
                && !args.get("teleport").equals("don't use")
                && skills.getRealLvl(Constants.STAT_MAGIC) >= 25
                && !edge) {
            if (args.get("teleport").equals("use varrock teletablets")) {
                tabs = true;
            }
            teleport = true;
        }
        if (args.get("str") != null && args.get("str").equals("use")) {
            potions[0] = 1;
            amounts[0] = Integer.parseInt(args.get("str_amount"));
            pots = true;
        }
        if (args.get("super_str") != null
                && args.get("super_str").equals("use")) {
            potions[1] = 1;
            amounts[1] = Integer.parseInt(args.get("super_str_amount"));
            pots = true;
        }
        if (args.get("super_att") != null
                && args.get("super_att").equals("use")) {
            potions[2] = 1;
            amounts[2] = Integer.parseInt(args.get("super_att_amount"));
            pots = true;
        }
        if (args.get("super_def") != null
                && args.get("super_def").equals("use")) {
            potions[3] = 1;
            amounts[3] = Integer.parseInt(args.get("super_def_amount"));
            pots = true;
        }
        stats = new int[][]{
                    {skills.getRealLvl(Constants.STAT_ATTACK),
                        skills.getCurrentXP(STAT_ATTACK)},
                    {skills.getRealLvl(Constants.STAT_STRENGTH),
                        skills.getCurrentXP(STAT_STRENGTH)},
                    {skills.getRealLvl(Constants.STAT_DEFENSE),
                        skills.getCurrentXP(Constants.STAT_DEFENSE)},
                    {skills.getRealLvl(Constants.STAT_HITPOINTS),
                        skills.getCurrentXP(Constants.STAT_HITPOINTS)}};
        if (settings.get(Constants.SETTING_AUTO_RETALIATE) == 1) {
            game.openTab(Constants.TAB_ATTACK);
            wait(random(800, 1000));
            if (!iface.clickChild(884, 18, "Auto")) {
                log("failed to turn auto retaliate on");
                return false;
            }
        }
        if (Bot.getClient().getCameraPitch() != 3072) {
            camera.setAltitude(true);
        }
        startTime = System.currentTimeMillis();
        antiban = startTime;
        mouseAntiban = antiban;
        allowPaint = true;
        return true;
    }

    @Override
    public int loop() {
        if (!inventory.contains(foodID)) {
            needBank = true;
            atSpiders = false;
            if (changeRooms) {
                room = random(1, 3);
            }
        }
        if (eat()) {
            return random(100, 250);
        }
        if (dropRewards()) {
            return random(600, 700);
        }
        if (atSpiders) {
            if ((RSNPC) player.getMine().getInteracting() == null) {
                attackSpider();
            } else {
                if (!drinkPots()) {
                    mouseAntiban();
                    antiban();
                }
            }
            return random(100, 250);
        }
        loc = player.getMine().getLocation();
        if (loc != null) {
            base = Bot.getClient().getBaseY();
            if (needBank && teleport && base > 4000 && teleport()) {
                return random(5000, 6000);
            }
            if (needBank && inventory.contains(229) && base < 4000) {
                if (dropVial()) {
                    return random(600, 700);
                }
            }
            if (base < 4000) {
                if (needBank) {
                    if (calculate.distanceTo(bankTile) < 4) {
                        int ct = 0;
                        try {
                            if (!bank.isOpen()) {
                                if (bank.open()) {
                                    wait(random(500, 800));
                                }
                                while (!bank.isOpen()) {
                                    wait(180);
                                    ct++;
                                    if (ct > 30) {
                                        tries++;
                                    }
                                }
                            }
                        } catch (final Exception e) {
                        }
                        if (bank.isOpen()) {
                            if (tries != 0) {
                                tries = 0;
                            }
                            if (teleport) {
                                if (tabs) {
                                    if (bank.getCount(8007) > 0
                                            && !inventory.contains(8007)) {
                                        bank.withdraw(8007, 1);
                                        return random(800, 1000);
                                    }
                                } else {
                                    if (!inventory.contains(563)
                                            || inventory.getCount(556) < 3
                                            || !inventory.contains(554)) {
                                        if (!inventory.contains(563)
                                                && bank.getCount(563) > 0) {
                                            bank.withdraw(563, 1);
                                            return random(800, 1000);
                                        }
                                        if (!inventory.contains(554)
                                                && bank.getCount(554) > 0) {
                                            bank.withdraw(554, 1);
                                            return random(800, 1000);
                                        }
                                        if (inventory.getCount(556) < 3
                                                && bank.getCount(556) >= 3) {
                                            bank.withdraw(556, 3);
                                            return random(800, 1000);
                                        }
                                    }
                                }
                            }
                            if (pots) {
                                if (potions[0] == 1
                                        && bank.getCount(potionID[0]) > 0
                                        && inventory.getCount(potionID[0]) < amounts[0]) {
                                    withdrawPotion(potionID[0], amounts[0]);
                                    return random(800, 1000);
                                } else if (potions[1] == 1
                                        && bank.getCount(potionID[1]) > 0
                                        && inventory.getCount(potionID[1]) < amounts[1]) {
                                    withdrawPotion(potionID[1], amounts[1]);
                                    return random(800, 1000);
                                } else if (potions[2] == 1
                                        && bank.getCount(potionID[2]) > 0
                                        && inventory.getCount(potionID[2]) < amounts[2]) {
                                    withdrawPotion(potionID[2], amounts[2]);
                                    return random(800, 1000);
                                } else if (potions[3] == 1
                                        && bank.getCount(potionID[3]) > 0
                                        && inventory.getCount(potionID[3]) < amounts[3]) {
                                    withdrawPotion(potionID[3], amounts[3]);
                                    return random(800, 1000);
                                }
                            }
                            if (inventory.contains(foodID)) {
                                needBank = false;
                            } else {
                                if (bank.getCount(foodID) < 1) {
                                    log("no food left, stopping script...");
                                    stopScript();
                                }
                                bank.withdraw(foodID, 0);
                                return random(800, 1000);
                            }
                        } else {
                            if (tries > 3) {
                                walk.to(bankTile);
                                tries = 0;
                            } else {
                                try {
                                    if (!bank.isOpen()) {
                                        if (bank.open()) {
                                            wait(random(500, 800));
                                        }
                                        while (!bank.isOpen()) {
                                            wait(180);
                                            ct++;
                                            if (ct > 30) {
                                                tries++;
                                            }
                                        }
                                    }
                                } catch (final Exception e) {
                                }
                                //clickObject((!edge ? 782 : 26972), "Use-quickly");

                            }
                        }
                    } else {
                        walk.pathMM((!edge ? bankingPath : edgeBankingPath));
                    }
                } else {
                    if (calculate.distanceTo(entranceTile) < 5) {
                        clickObject(16154, "Climb-down");
                    } else {
                        walk.pathMM(walk.reversePath((!edge ? bankingPath : edgeBankingPath)));
                    }
                }
            } else if (base >= 5192 && base < 5200 && floor1.contains(loc)) {
                if (!needBank) {
                    clickObject(16150, "Enter");
                } else {
                    clickObject(16148, "Climb-up");
                }
            } else if (base >= 5168 && base < 5192
                    && floor1Middle.contains(loc)) {
                clickObject(16149, "Climb-down");
            } else if (base >= 5192 && base < 5200 && floor2.contains(loc)) {
                if (!needBank) {
                    clickObject(16082, "Enter");
                } else {
                    clickObject(16080, "Climb-up");
                }
            } else if (base >= 5192 && base < 5200
                    && floor2Middle.contains(loc)) {
                clickObject(16081, "Climb-down");
            } else if (base >= 5200 && floor3.contains(loc)) {
                if (!needBank) {
                    clickDoor(doors[0], 's');
                } else {
                    clickObject(16114, "Climb-up");
                }
            } else if (base >= 5200 && floor3Front.contains(loc)) {
                if (!needBank) {
                    clickDoor(doors[0], 's');
                } else {
                    clickObject(16114, "Climb-up");
                }
            } else if (base >= 5200 && doors1.contains(loc)) {
                if (!needBank) {
                    clickDoor(doors[1], 's');
                } else {
                    clickDoor(doors[0], 's');
                }
            } else if (base >= 5200 && loc.getY() >= 5260 && loc.getY() <= 5278) {
                if (!needBank) {
                    if (room == 1) {
                        if (room1.contains(loc)) {
                            atSpiders = true;
                        } else {
                            walk.to(room1.getRandomTile());
                        }
                    } else {
                        clickDoor(doors[2], 'n');
                    }
                } else {
                    clickDoor(doors[1], 's');
                }
            } else if (base >= 5200 && doors2.contains(loc)) {
                if (!needBank && room == 2) {
                    clickDoor(doors[3], 'n');
                } else {
                    clickDoor(doors[2], 'n');
                }
            } else if (base >= 5200 && loc.getY() >= 5282 && loc.getY() <= 5295
                    && loc.getX() <= 2137) {
                if (!needBank && room == 2) {
                    clickDoor(doors[4], 'w');
                } else {
                    clickDoor(doors[3], 'n');
                }
            } else if (base >= 5200 && doors3.contains(loc)) {
                if (!needBank && room == 2) {
                    clickDoor(doors[5], 'w');
                } else {
                    clickDoor(doors[4], 'w');
                }
            } else if (base >= 5200 && loc.getY() >= 5294 && loc.getY() <= 5298
                    && loc.getX() >= 2141) {
                if (!needBank && room == 2) {
                    clickDoor(doors[6], 's');
                } else {
                    clickDoor(doors[5], 'w');
                }
            } else if (base >= 5200 && doors4.contains(loc)) {
                if (!needBank && room == 2) {
                    clickDoor(doors[7], 's');
                } else {
                    clickDoor(doors[6], 's');
                }
            } else if (base >= 5200 && loc.getY() >= 5302) {
                if (!needBank && room == 2) {
                    walk.to(room2);
                    atSpiders = true;
                } else {
                    clickDoor(doors[7], 's');
                }
            }
        }
        return random(100, 250);
    }

    private String formatTime(final long time) {
        final int sec = (int) (time / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":"
                + (s < 10 ? "0" + s : s);
    }
    public Font font = new Font("Tahoma", Font.BOLD, 11);

    public void onRepaint(Graphics g1) {
        if (!allowPaint) {
            return;
        }
        final Graphics2D g2 = (Graphics2D) g1;
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(350, 5, 160, 155);
        g2.setColor(Color.WHITE);
        g2.drawString("Giant Spider Killer v." + version, 355, 20);
        g2.setFont(new Font("Tahoma", Font.PLAIN, 11));
        runTime = System.currentTimeMillis() - startTime;

        if (style != settings.get(SETTING_COMBAT_STYLE)) { //if player changes attack style
            totalGained += xpGained;
            resetXP();
            training = -1;
            style = settings.get(SETTING_COMBAT_STYLE);
        }
        if (training == -1) {
            final int trained = getTrainedSkill();
            if (trained != -1) {
                training = trained;
                changeTime = System.currentTimeMillis();
            }
            g2.drawString("Calculating...", 355, 35);
            return;
        }

        currentTime = System.currentTimeMillis() - changeTime;

        if (training == 0) {
            trainingName = "Attack";
            currentLevel = skills.getRealLvl(STAT_ATTACK);
            xpGained = skills.getCurrentXP(Constants.STAT_ATTACK)
                    - stats[0][1];
            levelsGained = skills.getRealLvl(STAT_ATTACK) - stats[0][0];
            xpHour = (int) ((3600000.0 / (double) currentTime) * xpGained);
            TNL = skills.getXPToNextLvl(STAT_ATTACK);
            percent = skills.getPercentToNextLvl(STAT_ATTACK);
        } else if (training == 1) {
            trainingName = "Strength";
            currentLevel = skills.getRealLvl(STAT_STRENGTH);
            xpGained = skills.getCurrentXP(Constants.STAT_STRENGTH)
                    - stats[1][1];
            levelsGained = skills.getRealLvl(STAT_STRENGTH)
                    - stats[1][0];
            xpHour = (int) ((3600000.0 / (double) currentTime) * xpGained);
            TNL = skills.getXPToNextLvl(STAT_STRENGTH);
            percent = skills.getPercentToNextLvl(STAT_STRENGTH);
        } else if (training == 2) {
            trainingName = "Defense";
            currentLevel = skills.getRealLvl(STAT_DEFENSE);
            levelsGained = skills.getRealLvl(STAT_DEFENSE) - stats[2][0];
            xpGained = skills.getCurrentXP(Constants.STAT_DEFENSE)
                    - stats[2][1];
            xpHour = (int) ((3600000.0 / (double) currentTime) * xpGained);
            TNL = skills.getXPToNextLvl(STAT_DEFENSE);
            percent = skills.getPercentToNextLvl(STAT_DEFENSE);
        }
        timeTNL = (long) ((double) TNL / (double) xpHour * 3600000);
        g2.drawString("Runtime: " + formatTime(runTime), 355, 35);
        g2.drawString("Training: " + trainingName, 355, 50);
        g2.drawString("Current level: " + currentLevel, 355, 65);
        g2.drawString("Levels gained: " + levelsGained, 355, 80);
        g2.drawString("XP gained: " + (xpGained + totalGained), 356, 95);
        g2.drawString("XP / hour: " + xpHour, 356, 110);
        g2.drawString("TNL: " + TNL, 355, 125);
        g2.drawString("Time TNL: " + formatTime(timeTNL), 355, 140);
        g2.setColor(Color.RED);
        g2.fillRect(355, 148, 100, 4);
        g2.setColor(Color.GREEN);
        g2.fillRect(355, 148, percent, 4);
        g2.setColor(Color.WHITE);
    }

    private int getHP() {
        try {
            return Integer.parseInt(iface.get(748).getChild(8).getText());
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean eat() {
        try {
            final int hp = getHP();
            if (hp != -1 && hp <= eatAt[0]) {
                final RSNPC current = (RSNPC) player.getMine().getInteracting();
                if (inventory.clickItem(foodID, "Eat")) {
                    wait(random(600, 750));
                    if (current != null) {
                        waitSafely(random(1500, 1600));
                    }
                    eatAt[0] = random(eatAt[1], eatAt[2]);
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private void waitSafely(int timeout) {
        final long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            final int hp = getHP();
            if (hp != -1 && hp <= eatAt[0]) {
                break;
            }
            wait(random(100, 250));
        }
    }

    private boolean drinkPots() {
        if (pots) {
            if (potions[0] == 1) {
                if (inventory.getCount(potionID[0]) > 0
                        && (skills.getCurrentLvl(Constants.STAT_STRENGTH) - skills.getRealLvl(Constants.STAT_STRENGTH)) <= random(
                        0, 5)) {
                    return drinkPotion(potionID[0]);
                }
            }
            if (potions[1] == 1) {
                if (inventory.getCount(potionID[1]) > 0
                        && (skills.getCurrentLvl(Constants.STAT_STRENGTH) - skills.getRealLvl(Constants.STAT_STRENGTH)) <= random(
                        0, 5)) {
                    return drinkPotion(potionID[1]);
                }
            }
            if (potions[2] == 1) {
                if (inventory.getCount(potionID[2]) > 0
                        && (skills.getCurrentLvl(Constants.STAT_ATTACK) - skills.getRealLvl(Constants.STAT_ATTACK)) <= random(
                        0, 5)) {
                    return drinkPotion(potionID[2]);
                }
            }
            if (potions[3] == 1) {
                if (inventory.getCount(potionID[3]) > 0
                        && (skills.getCurrentLvl(Constants.STAT_DEFENSE) - skills.getRealLvl(Constants.STAT_DEFENSE)) <= random(
                        0, 5)) {
                    return drinkPotion(potionID[3]);
                }
            }
        }
        return false;
    }

    private boolean drinkPotion(final int id[]) {
        for (int i : id) {
            if (inventory.contains(i)) {
                try {
                    final RSNPC current = (RSNPC) player.getMine().getInteracting();
                    if (inventory.clickItem(i, "Drink")) {
                        wait(random(600, 700));
                        if (current != null) {
                            waitSafely(random(1500, 1600));
                        }
                        return true;
                    }
                } catch (Exception e) {
                }
                break;
            }
        }
        return false;
    }

    private boolean dropVial() {
        if (inventory.contains(229)) {
            try {
                return inventory.clickItem(229, "Drop");
            } catch (final Exception e) {
            }
        }
        return false;
    }

    private boolean dropRewards() {
        for (int id : rewardID) {
            if (inventory.contains(id)) {
                try {
                    return inventory.clickItem(id, "Drop");
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    private boolean teleport() {
        if (teleport) {
            if (!tabs) {
                if (inventory.contains(563) && inventory.getCount(556) >= 3
                        && inventory.contains(554)) {
                    return magic.castSpell(Constants.SPELL_VARROCK_TELEPORT);
                }
            } else {
                if (inventory.contains(8007)) {
                    try {
                        return inventory.clickItem(8007, "Break");
                    } catch (final Exception e) {
                    }
                }
            }
        }
        return false;
    }

    private void attackSpider() {
        RSNPC spider = npc.getNearestFreeToAttackByID(4400);
        run();
        if (spider != null) {
            try {
                if (spider.isOnScreen() && spider.action("Attack")) {
                    if (tries != 0) {
                        tries = 0;
                    }
                    wait(random(800, 900));
                    waitToStop();
                    waitSafely(random(2200, 2400));
                } else {
                    if (tries > 60) { //if there is no spider on screen in 6-12 seconds
                        if (room == 1) {
                            walk.to(room1.getRandomTile());
                        } else {
                            walk.to(room2);
                        }
                        tries = 0;
                    } else {
                        tries++;
                    }
                }
            } catch (final Exception e) {
            }
        }
    }

    private void waitToStop() {
        while (player.getMine().isMoving()) {
            wait(random(200, 250));
        }
    }

    private void clickObject(final int id, final String action) {
        final RSObject obj = objects.getNearestByID(id);
        if (obj != null) {
            try {
                if (tile.onScreen(obj.getLocation())) {
                    if (obj.action(action)) {
                        wait(random(2500, 3000));
                        waitToStop();
                    } else {
                        rotateCameraRandomly();
                    }
                } else {
                    camera.turnTo(obj, random(20, 60));
                    if (!tile.onScreen(obj.getLocation())) {
                        walk.to(obj.getLocation());
                    }
                }
            } catch (final Exception e) {
            }
        }
    }

    public void clickDoor(RSTile location, char direction) {
        if (Bot.getClient().getCameraPitch() != 3072) {
            camera.setAltitude(true);
        }

        if (location.isOnScreen()) {
        	RSObject door = objects.getTopAt(location);
        	if (!door.action("Open"))  {
                rotateCameraRandomly();
            } else {
            	if (door.distanceTo()>0)  {
            		player.waitToMove(3000);
            	}
            	
                player.waitForAnim(random(2500, 3000));
                waitToStop();
            }
        } else {
            walk.to(location);
        }
    }

    private void rotateCameraRandomly() {
        int angle = camera.getAngle() + random(30, 150);
        if (angle > 360) {
            angle -= 360;
        }
        camera.setRotation(angle);
    }


    private void run() {
        if (player.getMyEnergy() >= random(30, 50) && !isRunning()) {
            game.setRun(true);
            wait(random(800, 1000));
        }
    }

    private void antiban() {
        if (System.currentTimeMillis() - antiban > random(120000, 420000)) {
            if (random(0, 2) == 1) {
                camera.setRotation(random(1, 360));
            } else {
                final int tab = tabConstants[random(0, tabConstants.length)];
                if (tab == TAB_STATS) {
                    game.openTab(tab);
                    wait(random(600, 700));
                    final RSInterface statInterface = iface.get(320);
                    if (statInterface != null) {
                        try {
                            final RSInterfaceChild stat = statInterface.getChild(statConstants[random(0, statConstants.length)]);
                            if (stat != null) {
                                mouse.move(stat.getAbsoluteX(), stat.getAbsoluteY(), stat.getWidth(), stat.getHeight());
                                waitSafely(random(1100, 1600));
                            }
                        } catch (Exception e) {
                        }
                    }
                } else {
                    game.openTab(tab);
                    waitSafely(random(1100, 1600));
                }
            }
            antiban = System.currentTimeMillis();
        }
    }

    private void mouseAntiban() {
        if (System.currentTimeMillis() - mouseAntiban > random(25000, 60000)) {
            mouse.moveRandomly(600);
            mouseAntiban = System.currentTimeMillis();
        }
    }

    private void withdrawPotion(int id[], int amount) {
        for (int i = id.length - 1; i >= 0; i--) {
            if (bank.getCount(id[i]) > 0) {
                bank.withdraw(id[i], amount);
                return;
            }
        }
    }

    private int getTrainedSkill() {
        int gained = 1;
        int index = -1;
        for (int i = 0; i < 3; i++) {
            final int difference = skills.getCurrentXP(skillConstants[i]) - stats[i][1];
            if (difference > gained) {
                index = i;
                gained = difference;
            }
        }
        return index;
    }

    private void resetXP() {
        stats[0][1] = skills.getCurrentXP(STAT_ATTACK);
        stats[1][1] = skills.getCurrentXP(STAT_STRENGTH);
        stats[2][1] = skills.getCurrentXP(STAT_DEFENSE);
    }

    public void messageReceived(MessageEvent e) {
        String msg = e.getMessage();
        if (msg != null && msg.contains("You can't reach that")) {
            atSpiders = false;
        }
    }
}
