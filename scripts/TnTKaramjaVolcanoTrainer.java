import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = {"Gribonn"}, category = "Combat", name = "TnT Karamja Volcano Trainer", version = 1.2, description = ""
+ "<html>"
+ "Start at lessers with hatchet, tinderbox, cage and full bag of lobsters.<br>"
+ "When you stop it in the progress you may continue where you stopped it<br>"
+ "Script drops all rubish it gets (so dont have anything more than said above) from eg random events or so.<br>"
+ "Combat Style Changing "
+ "<select name='changeStyle'>"
+ "<option>Disabled"
+ "<option>Enabled"
+ "</select><br>"
+ "Chat responder "
+ "<select name='chatResponder'>"
+ "<option>Disabled" + "<option>Enabled" + "</select>" + "</html>")
public class TnTKaramjaVolcanoTrainer extends Script implements PaintListener,
        MessageListener {

	private Bot bot;
    public String status = "";
    public ScriptManifest info = getClass().getAnnotation(ScriptManifest.class);
    public DecimalFormat df = new DecimalFormat("#");
    public int lobsterID = 379;
    public int rawLobsterID = 377;
    public int logID = 1511;
    public int rubbish = 0;
    public int ropeID = 1764;
    public int tinderboxID = 590;
    public int rockID = 492;
    public int fishID = 324;
    public int lobsters = 0;
    public int minWait = 0;
    public int maxWait = 0;
    public int page = 1;
    public int trips = 0;
    public int bruntLobster = 381;
    public int[] lesserIDs = {4695, 82, 4696, 4697, 4694};
    public int[] treeIDs = {1289, 1286};
    public int[] progBarLocs = {0, 0, 0, 0};
    public int[] keep = {lobsterID, rawLobsterID, logID, tinderboxID, 1351,
        1349, 1353, 1361, 1355, 1357, 1359, 4031, 6739, 13470, 14108, 301,
        -1};
    public int defStartExp;
    public int hpStartExp;
    public int strStartExp;
    public int atkStartExp;
    public int levels = 0;
    public int styleChangeCount = 0;
    public long startTime;
    public boolean paint = true;
    public boolean showPaint = true;
    public boolean showDefenseInfo = false;
    public boolean showStrengthInfo = false;
    public boolean showAttackInfo = false;
    public boolean showHPInfo = false;
    public boolean atLessers1 = false;
    public boolean atLessers2 = false;
    public boolean atLessers3 = false;
    public boolean atSkeletons1 = false;
    public boolean atSkeletons2 = false;
    public boolean atSkeletons3 = false;
    public boolean atDeadlySpiders = false;
    public boolean enableChangeAtkStyle;
    public boolean letTurnPaint = true;
    public RSArea lesserArea = new RSArea(new RSTile(2828, 9549), new RSTile(
            2850, 9580));
    public RSTile ropeTile = new RSTile(2857, 9569);
    public RSTile rockTile = new RSTile(2856, 3167);
    public RSTile fishTile = new RSTile(2924, 3178);
    public RSTile[] ropeToLesser = {new RSTile(2856, 9570),
        new RSTile(2848, 9580), new RSTile(2836, 9578),
        new RSTile(2834, 9567), new RSTile(2837, 9561)};
    public RSTile[] lesserToRope = walk.reversePath(ropeToLesser);
    public RSTile[] rockToFish = {new RSTile(2856, 3167),
        new RSTile(2868, 3173), new RSTile(2881, 3171),
        new RSTile(2893, 3170), new RSTile(2904, 3173),
        new RSTile(2917, 3171), new RSTile(2924, 3178)};
    public RSTile[] fishToRock = walk.reversePath(rockToFish);
    public String[] attNames = {"attack", "att", "atk", "atting", "atking",
        "attacking", "atkin", "attin", "attackin"};
    public String[] attNamesInAnswer = {"attack", "att", "atk"};
    public String[] defNames = {"defense", "defence", "block", "def", "deff",
        "defenc"};
    public String[] defNamesInAnswer = {"defence", "def", "deff"};
    public String[] strNames = {"strength", "strentgh", "strenhtg",
        "strenght", "stre", "str"};
    public String[] strNamesInAnswer = {"strength", "stre", "str"};
    public String[] hpNames = {"hp", "hitpoints", "constitution",
        "contsitution", "constiution", "health", "healt", "life",
        "lifepoints", "lp", "<3"};
    public String[] hpNamesInAnswer = {"hp", "constitution", "health"};
    public String[] rangedNames = {"ranged", "range", "rang", "rng", "rnged",
        "ranging", "rnging", "rangin", "rangeing", "rngin"};
    public String[] rangedNamesInAnswer = {"ranged", "range", "rang", "rng",
        "rnged", "ranging", "rnging", "rangin", "rngin"};
    public String[] prayerNames = {"prayer", "pray", "pry", "praying"};
    public String[] magicNames = {"magic", "mage", "mgic", "maging"};
    public String[] magicNamesInAnswer = {"magic", "mage"};
    public String[] cookingNames = {"cook", "cooking", "cookin"};
    public String[] woodcuttingNames = {"wcing", "wc", "woodcutting",
        "woodcuttin", "wcin", "chopping", "choppin", "woodcut"};
    public String[] woodcuttingNamesInAnswer = {"wcing", "wc", "woodcutting",
        "woodcuttin", "wcin", "woodcut"};
    public String[] fletchingNames = {"flech", "fletch", "fletchin",
        "fleching", "fletching", "flcin", "flching"};
    public String[] fletchingNamesInAnswer = {"fletch", "fletchin",
        "fletching", "flcin", "fltching"};
    public String[] fishingNames = {"<><", "fishin", "fishing", "<><ing",
        "<><-ing", "><>", "fish", "fshing", "><>-ing", "><>ing"};
    public String[] fishingNamesInAnswer = {"<><", "fishin", "fishing",
        "<><ing", "<><-ing", "><>", "fish", "><>-ing", "><>ing"};
    public String[] firemakingNames = {"fm", "fming", "fmin", "firemaking",
        "fmaking", "fmakin", "firemakin", "fireming", "firem"};
    public String[] firemakingNamesInAnswer = {"fm", "fming", "firemaking",
        "firemakin"};
    public String[] craftingNames = {"crafting", "craftin", "craft"};
    public String[] smithingNames = {"smithing", "smelting", "smith", "smelt",
        "smithin", "smeltin"};
    public String[] smithingNamesInAnswer = {"smithing", "smith", "smithin"};
    public String[] miningNames = {"mining", "minin"};
    public String[] herbloreNames = {"herblore", "herby", "herblaw"};
    public String[] agilityNames = {"agility", "agil", "agilit"};
    public String[] thievingNames = {"thieving", "thief", "thievin",
        "stealing", "stoling"};
    public String[] slayerNames = {"slay", "slayer", "slaying", "slayin"};
    public String[] farmingNames = {"farming", "frming", "farmin", "growing",
        "frmin", "growin", "farm", "grow"};
    public String[] farmingNamesInAnswer = {"farming", "frming", "farmin",
        "frmin", "farm"};
    public String[] runecraftingNames = {"runecrafting", "rcing", "rc",
        "runecraftin", "rcin", "runemaking", "runemakin"};
    public String[] runecraftingNamesInAnswer = {"runecrafting", "rcing",
        "rc", "runecraftin", "rcin"};
    public String[] hunterNames = {"hunter", "hunting", "huntin", "hunt"};
    public String[] constructionNames = {"construction", "constructing",
        "building", "buildin", "constructin", "con", "cons"};
    public String[] constructionNamesInAnswer = {"construction",
        "constructing", "constructin", "con", "cons"};
    public String[] summoningNames = {"summoning", "summon", "summonin"};
    public String[] dungeoneeringNames = {"dungeoneering", "dungoneering",
        "dungeon", "dung", "dungeoneerin", "dungoneerin"};
    public String[] dungeoneeringNamesInAnswer = {"dungeoneering", "dungeon",
        "dung", "dungeoneerin"};
    public String[] levelNames = {"lvl", "level", "lvel", "levl",
        "skilllevel", "skilllvl", "levvel", "lewel", "lwl", "lwel", "lewl"};
    public String[] levelNamesInAnswer = {"", "lvl", "level"};
    public String[] beforeSay = {"", "whatisyour", "whatsyour", "whatsur",
        "watsur", "whatisur", "watsyour", "ur", "your", "yar", "watsyar",
        "watisyar", "whatsyar", "whatisyar", "wutsur", "wutsyar",
        "wutsyour", "whatchur"};
    public String[] beforeSayInAnswer = {"", "Mines", "Mine", "My", "Me"};
    public String[] greetings = {"hi", "hello", "hey", "heya", "heyy",
        "heyyy", "greeting", "greetings", "greets", "morning", "evening",
        "night", "goodnight", "goodmorning", "goodafternoon", "afternoon"};
    public String[] greetingsInAnswer = {"hi", "hello", "hey", "heya", "heyy",
        "heyyy", "welcome", "yo"};
    public String[] persons = {"", "ppl", "people", "guy", "person",
        "fellows", "fellas", "youngfellas", ""};
    public String[] byes = {"bye", "byebye", "cya", "seeyou", "seeya",
        "goodbye", "gtg", "gottogo", "g2g", "got2go", "gtgcya",
        "got2gocya", "g2gcya"};
    public String[] byesInAnswer = {"bye", "bye bye", "cya", "see you",
        "see ya", "good bye"};
    public String[] qpNames = {"qp", "questpoints"};
    public String[] questpointsInAnswer = {"i have qpam qpna",
        "my qpna amount is qpam", "i have got qpam qpna",
        "i has got qpam qpna", "i has qpam qpna"};
    public String[] areyou = {"", "areyou", "areu", "u", "areya", "arey"};
    public String[] bottingNames = {"botting", "boting", "bottin", "botin",
        "botter", "boter", "autobotin", "autoboting", "autoer",
        "autobotter", "autobottin", "autobotting"};
    public String[] ends = {"!", ".", "", "", "", "", "", "", "", "'", ""};
    public String[] sups = {"sup", "wazzup", "wazup", "whatsup", "whatisup",
        "watsup", "whatareyoudoing"};
    public String[] supsInAnswer = {"nothing", "nothin", "nothing much", "nm",
        "nothing really"};
    public String[] lols = {"", "lol, ", "lmao, ", "lmfao, ", "", "", ""};
    public String[] nos = {"no", "noo", "nope", "nopee"};
    public String[] whys = {"", "", "", "", ", why?", ", afking", ""};
    public String lastMsg = "";
    public String qpamount = "";
    public boolean checkChat = false, chatResponder = false;
    public String[] previousMsgs = {"", "", "", "", "", "", "", "", "", "",
        "", "", "", "", ""};
    public int[] timers = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    protected int getMouseSpeed() {
        return random(4, 6);
    }

    @Override
    public boolean onStart(Map<String, String> args) {
        final String changeAtk = args.get("changeStyle");
        if (changeAtk.equals("Enabled")) {
            enableChangeAtkStyle = true;
        } else {
            enableChangeAtkStyle = false;
        }
        final String chatrespond = args.get("chatResponder");
        if (chatrespond.equals("Enabled")) {
            chatResponder = true;
        } else {
            chatResponder = false;
        }
        game.openTab(TAB_ATTACK);
        wait(random(500, 800));
        game.openTab(TAB_INVENTORY);
        startTime = System.currentTimeMillis();
        defStartExp = skills.getCurrentXP(Constants.STAT_DEFENSE);
        hpStartExp = skills.getCurrentXP(Constants.STAT_HITPOINTS);
        strStartExp = skills.getCurrentXP(Constants.STAT_STRENGTH);
        atkStartExp = skills.getCurrentXP(Constants.STAT_ATTACK);
        return true;
    }

    public void s(String statusText) {
        status = statusText;
    }

    @Override
    public int loop() {
        final RSObject mysteriousDoor = objects.getNearestByID(52869);
        if (mysteriousDoor != null) {
            mysteriousDoor.action("Exit");
        }
        camera.setAltitude(true);
        if (checkChat && chatResponder) {
            chatResponder();
        }
        for (int i = 0; i <= timers.length - 1; i++) {
            if (previousMsgs[i] != "") {
                if (timers[i] > 0) {
                    timers[i]--;
                } else {
                    previousMsgs[i] = "";
                }
            }
        }
        checkForRubbish();
        if (player.getMine().getHPPercent() <= 50 && gotLobby()) {
            inventory.clickItem(lobsterID, "Eat");
            return random(700, 900);
        }
        if (isRunning()) {
            minWait = 500;
            maxWait = 900;
        } else {
            if (player.getMyEnergy() >= 30) {
                game.setRun(true);
            }
            minWait = 800;
            maxWait = 1200;
        }
        antiban();
        if (atLessers() && gotLobby()) {
            s("Attacking lessers");
            final RSNPC lesser = npc.getNearestFreeByID(lesserIDs);
            if (random(1, 200) == 1 && enableChangeAtkStyle) {
                s("Changing combat style");
                changeStyle();
            }
            if (lesser != null && player.getMine().getInteracting() == null) {
                if (lesser.isOnScreen()) {
                    lesser. action("Attack");
                } else {
                    walk.to(lesser.getLocation());
                }
                return random(minWait, maxWait);
            }
        } else if (underground() && !atLessers() && gotLobby()) {
            s("Walking to lessers");
            walk(ropeToLesser);
        } else if (underground() && !nearRope() && !gotLobby()
                && !gotRawLobby()) {
            s("Walking to fishes");
            walk(lesserToRope);
        } else if (underground() && nearRope() && !gotLobby() && !gotRawLobby()) {
            s("Walking to fishes");
            final RSObject rope = objects.getNearestByID(ropeID);
            if (rope != null && rope.distanceTo() <= 4 && player.getMine().isIdle()) {
                clickRope(rope);
                return random(minWait, maxWait);
            }
        } else if ((onGround() && !atFish() && !gotLobby() && !gotRawLobby())
                || (onGround() && !atFish() && inventory.getCount(rawLobsterID) < 24 && !gotLobby())) {
            s("Walking to fishes");
            walk(rockToFish);
        } else if (onGround() && atFish() && inventory.getCount() < 27
                && !gotLobby()) {
            s("Fishing");
            RSTile lobsterspot = null;
            final RSNPC fish = npc.getNearestByID(fishID);
            if (fish != null) {
                lobsterspot = fish.getLocation();
            }
            if (lobsterspot != null) {
                if (player.getMine().getAnimation() == -1) {
                    wait((random(500, 900)));
                    if (player.getMine().getAnimation() == -1) {
                        if (lobsterspot.distanceTo() <= 4) {
                            atFish(lobsterspot);
                        } else {
                            walk.to(lobsterspot);
                        }
                        return random(minWait, maxWait);
                    }
                    camera.setRotation(random(0, 360));
                }
            }
        } else if (onGround() && !atRock() && inventory.getCount() >= 27
                && gotRawLobby()) {
            s("Walking to trees");
            if (inventory.isFull()) {
                inventory.clickItem(rawLobsterID, "Drop");
                return random(1200, 1700);
            }
            walk(fishToRock);
        } else if (onGround() && atRock() && inventory.getCount() >= 27
                && !gotWood() && noFire() && gotRawLobby()) {
            s("Chopping trees to make a fire");
            final RSObject tree = objects.getNearestByID(treeIDs);
            if (player.getMine().isIdle()) {
                tree.action("Chop");
                return random(minWait, maxWait);
            }
        } else if (onGround() && atRock() && inventory.getCount() >= 27
                & gotWood() && noFire() && gotRawLobby()) {
            s("Making fire...");
            if (player.getMine().isIdle()) {
                inventory.clickItem(logID, "Use");
                wait(random(500, 900));
                inventory.clickItem(tinderboxID, "Use");
                wait(random(700, 900));
            }
        } else if (onGround() && atRock() && inventory.getCount() >= 27
                && !noFire() && inventory.getCount(lobsterID) < 24) {
            s("Cooking lobsters");
            if (player.getMine().isIdle()) {
                wait(random(600, 900));
                if (player.getMine().isIdle()) {
                    final RSObject fire = objects.getNearestByID(2732);
                    if (player.getMine().isIdle()) {
                        inventory.clickItem(rawLobsterID, "Use");
                        wait(random(300, 500));
                        if (player.getMine().isIdle()) {
                            fire.action("Use Raw lobster -> Fire");
                            wait(random(500, 700));
                            if (player.getMine().isIdle()) {
                                mouse.move(random(239, 281), random(387, 434));
                                menu.action("Cook All");
                                wait(random(500, 900));
                            }
                        }
                    }
                }
            }
        } else if (onGround() && atRock() && gotLobby() && !gotRawLobby()) {
            s("Walking to lessers");
            final RSObject rock = objects.getNearestByID(rockID);
            if (rock != null && player.getMine().isIdle()) {
                at4TiledObject(rock, "Climb-down");
                wait(random(minWait, maxWait));
            }
        }
        return random(500, 700);
    }

    public boolean atFish(RSTile t) {
        if (!tile.click(t, random(0.3, 0.7), random(0.3, 0.7), 0, "Cage")) {
            atFish(t);
        }
        return false;
    }

    public boolean clickRope(RSObject rope) {
        if (rope != null && underground()) {
            Point[] modelptz = rope.getModel().getModelPoints();
            int x = random(modelptz[16].getLocation().x - 5, modelptz[16].getLocation().x + 10);
            int y = random(modelptz[16].getLocation().y - 5, modelptz[16].getLocation().y + 10);
            mouse.move(x, y);
            if (!menu.action("Climb")) {
                clickRope(rope);
            }
        } else {
            return true;
        }
        return true;
    }

    public int checkForRubbish() {

        for (RSItem item : inventory.getItems()) {
            final int itemID = item.getID();
            int c = 0;
            for (int id : keep) {
                if (id == itemID) {
                    c++;
                }
            }
            if (c == 0) {
                if (player.getMine().isIdle()) {
                    wait(random(500, 700));
                    if (player.getMine().isIdle()) {
                        s("Dropping rubbish");
                        if (inventory.clickItem(itemID, "Drop")) {
                            rubbish++;
                        }

                    }
                }
                return (random(500, 900));
            }
        }
        return 0;
    }

    public void changeStyle() {
        if ((game.getCurrentTab() != Constants.TAB_ATTACK)) {
            game.openTab(Constants.TAB_ATTACK);
            wait(random(500, 700));
        }
        final int random = random(1, 5);
        if (random == 1) {
            if (iface.get(884).getChild(29).getBackgroundColor() != 654
                    && iface.get(884).getChild(11).isValid()
                    && iface.get(884).getChild(29).isValid()
                    && iface.get(884).getChild(11).getActions() != null) {
            	iface.getChild(884,11).click();
                styleChangeCount++;
                wait(random(500, 700));
            } else {
                changeStyle();
            }
        } else if (random == 2) {
            if (iface.get(884).getChild(26).getBackgroundColor() != 654
                    && iface.get(884).getChild(12).isValid()
                    && iface.get(884).getChild(26).isValid()
                    && iface.get(884).getChild(12).getActions() != null) {
            	iface.getChild(884,12).click();
                styleChangeCount++;
                wait(random(500, 700));
            } else {
                changeStyle();
            }
        } else if (random == 3) {
            if (iface.get(884).getChild(23).getBackgroundColor() != 654
                    && iface.get(884).getChild(13).isValid()
                    && iface.get(884).getChild(23).isValid()
                    && iface.get(884).getChild(13).getActions() != null) {
                iface.getChild(884,13).click();
                styleChangeCount++;
                wait(random(500, 700));
            } else {
                changeStyle();
            }
        } else if (random == 4) {
            if (iface.get(884).getChild(20).getBackgroundColor() != 654
                    && iface.get(884).getChild(14).isValid()
                    && iface.get(884).getChild(20).isValid()
                    && iface.get(884).getChild(14).getActions() != null) {
                iface.getChild(884,14).click();
                styleChangeCount++;
                wait(random(500, 700));
            } else {
                changeStyle();
            }
        } else if (random == 5) {
            changeStyle();
        }
    }

    public boolean noFire() {
        final RSObject fire = objects.getNearestByID(2732);
        return fire == null || fire.distanceTo() >= 5;
    }

    public boolean gotRawLobby() {
        return inventory.contains(rawLobsterID);
    }

    public boolean gotWood() {
        return inventory.contains(logID);
    }

    public boolean atRock() {
        return rockTile.distanceTo() <= 5;
    }

    public boolean atFish() {
        return fishTile.distanceTo() <= 10;
    }

    public boolean onGround() {
        return rockTile.distanceTo() <= 500;
    }

    public boolean at4TiledObject(RSObject obj, String action) {
        RSTile map[][] = new RSTile[5][5];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                RSObject fObj = objects.getTopAt(obj.getLocation().getX() - 2 + x,
                        obj.getLocation().getY() - 2 + y);
                if (fObj != null && fObj.getID() == obj.getID()) {
                    map[x][y] = fObj.getLocation();
                }
            }
        }
        RSTile tTile = null;
        int tx = 0;
        int ty = 0;
        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                if (map[x][y] != null && map[x + 1][y] != null
                        && map[x][y + 1] != null && map[x + 1][y + 1] != null) {
                    if (map[x][y].equals(obj.getLocation())
                            || map[x + 1][y].equals(obj.getLocation())
                            || map[x][y + 1].equals(obj.getLocation())
                            || map[x + 1][y + 1].equals(obj.getLocation())) {
                        tTile = map[x][y];
                        tx = x;
                        ty = y;
                        break;
                    }
                }
            }
        }

        if (tTile == null) {
            return false;
        }
        int xSum = 0;
        int ySum = 0;

        for (int x = 0; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                xSum += (int) map[tx + x][ty + y].getScreenLocation().getX();
                ySum += (int) map[tx + x][ty + y].getScreenLocation().getY();
            }
        }
        mouse.move(xSum / 4 + random(-2, 2), ySum / 4 + random(-2, 2));
        return menu.action(action);
    }

    public void antiban() {
        final int random = random(1, 13);
        if (random == 1) {
            camera.setRotation(random(0, 360));
        } else if (random == 2) {
            camera.setRotation(random(0, 360));
            mouse.move(random(0, 770), random(0, 510));
        } else if (random == 3) {
            mouse.move(random(0, 770), random(0, 510));
        }
    }

    public boolean nearRope() {
        return ropeTile.distanceTo() <= 3;
    }

    public boolean underground() {
        return ropeTile.distanceTo() <= 500;
    }

    public int walk(RSTile[] path) {
        if (walk.getDestination().distanceTo() < random(5, 12)
                || walk.getDestination().distanceTo() > 40) {
            if (!walk.pathMM(path)) {
                walk.toClosestTile(walk.randomizePath(path, 2, 2));
                return random(minWait, maxWait);
            }
        }
        return random(minWait, maxWait);
    }

    public boolean atLessers() {
        return lesserArea.contains(player.getMyLocation());
    }

    public boolean gotLobby() {
        return inventory.contains(lobsterID);
    }

    public void drawMouse(Graphics g) {
        Point botMouse = mouse.getLocation();
        if (underground()) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        g.drawLine(0, botMouse.y, 800, botMouse.y);
        g.drawLine(botMouse.x, 0, botMouse.x, 500);
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
    public final RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    public float xpHour(long hours, long minutes, long seconds, int gained) {
        float xpsec = 0;
        if ((minutes > 0 || hours > 0 || seconds > 0) && gained > 0) {
            xpsec = ((float) gained)
                    / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
        }
        float xpmin = xpsec * 60;
        float xphour = xpmin * 60;
        return xphour;
    }

    public int chatResponder() {
        try {
            final String[] lastMessage = getLastChatMessage().split(".,:,.");
            final String originalMsg = lastMessage[1];
            final String user = lastMessage[0];
            final String msg = originalMsg.replace(" ", "").replace("?", "").replace(".", "").replace("!", ".").replace("'", "").replace(",", "").toLowerCase();
            String skillname = "";
            String greet = "";
            String type = "";
            String skillnameReal = "";
            String answer = "";
            for (String say : beforeSay) {
                for (String level : levelNames) {
                    for (String name : attNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = attNamesInAnswer[random(0,
                                    attNamesInAnswer.length - 1)];
                            skillnameReal = "ATTACK";
                        }
                    }
                    for (String name : defNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = defNamesInAnswer[random(0,
                                    defNamesInAnswer.length - 1)];
                            skillnameReal = "DEFENCE";
                        }
                    }
                    for (String name : strNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = strNamesInAnswer[random(0,
                                    strNamesInAnswer.length - 1)];
                            skillnameReal = "STRENGTH";
                        }
                    }
                    for (String name : hpNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = hpNamesInAnswer[random(0,
                                    hpNamesInAnswer.length - 1)];
                            skillnameReal = "HITPOINTS";
                        }
                    }
                    for (String name : rangedNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = rangedNamesInAnswer[random(0,
                                    rangedNamesInAnswer.length - 1)];
                            skillnameReal = "RANGED";
                        }
                    }
                    for (String name : prayerNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = prayerNames[random(0,
                                    prayerNames.length - 1)];
                            skillnameReal = "PRAYER";
                        }
                    }
                    for (String name : magicNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = magicNamesInAnswer[random(0,
                                    magicNamesInAnswer.length - 1)];
                            skillnameReal = "MAGIC";
                        }
                    }
                    for (String name : cookingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = cookingNames[random(0,
                                    cookingNames.length - 1)];
                            skillnameReal = "COOKING";
                        }
                    }
                    for (String name : woodcuttingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = woodcuttingNamesInAnswer[random(0,
                                    woodcuttingNamesInAnswer.length - 1)];
                            skillnameReal = "WOODCUTTING";
                        }
                    }
                    for (String name : fletchingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = fletchingNamesInAnswer[random(0,
                                    fletchingNamesInAnswer.length - 1)];
                            skillnameReal = "FLETCHING";
                        }
                    }
                    for (String name : fishingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = fishingNamesInAnswer[random(0,
                                    fishingNamesInAnswer.length - 1)];
                            skillnameReal = "FISHING";
                        }
                    }
                    for (String name : firemakingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = firemakingNamesInAnswer[random(0,
                                    firemakingNamesInAnswer.length - 1)];
                            skillnameReal = "FIREMAKING";
                        }
                    }
                    for (String name : craftingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = craftingNames[random(0,
                                    craftingNames.length - 1)];
                            skillnameReal = "CRAFTING";
                        }
                    }
                    for (String name : smithingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = smithingNamesInAnswer[random(0,
                                    smithingNamesInAnswer.length - 1)];
                            skillnameReal = "SMITHING";
                        }
                    }
                    for (String name : miningNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = miningNames[random(0,
                                    miningNames.length - 1)];
                            skillnameReal = "MINING";
                        }
                    }
                    for (String name : herbloreNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = herbloreNames[random(0,
                                    herbloreNames.length - 1)];
                            skillnameReal = "HERBLORE";
                        }
                    }
                    for (String name : agilityNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = agilityNames[random(0,
                                    agilityNames.length - 1)];
                            skillnameReal = "AGILITY";
                        }
                    }
                    for (String name : thievingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = thievingNames[random(0,
                                    thievingNames.length - 1)];
                            skillnameReal = "THIEVING";
                        }
                    }
                    for (String name : slayerNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = slayerNames[random(0,
                                    slayerNames.length - 1)];
                            skillnameReal = "SLAYER";
                        }
                    }
                    for (String name : farmingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = farmingNamesInAnswer[random(0,
                                    farmingNamesInAnswer.length - 1)];
                            skillnameReal = "FARMING";
                        }
                    }
                    for (String name : runecraftingNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = runecraftingNamesInAnswer[random(0,
                                    runecraftingNamesInAnswer.length - 1)];
                            skillnameReal = "RUNECRAFTING";
                        }
                    }
                    for (String name : hunterNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = hunterNames[random(0,
                                    hunterNames.length - 1)];
                            skillnameReal = "HUNTER";
                        }
                    }
                    for (String name : constructionNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = constructionNamesInAnswer[random(0,
                                    constructionNamesInAnswer.length - 1)];
                            skillnameReal = "CONSTRUCTION";
                        }
                    }
                    for (String name : summoningNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = summoningNames[random(0,
                                    summoningNames.length - 1)];
                            skillnameReal = "SUMMONING";
                        }
                    }
                    for (String name : dungeoneeringNames) {
                        if (msg.equals(say + name + level)
                                || msg.equals(say + level + "in" + name)
                                || msg.equals(name + level + "s")
                                || msg.equals(name + level + "z")
                                || (msg.equals(say + name) && !say.equals(""))) {
                            skillname = dungeoneeringNamesInAnswer[random(0,
                                    dungeoneeringNamesInAnswer.length - 1)];
                            skillnameReal = "DUNGEONEERING";
                        }
                    }
                }
            }
            for (String person : persons) {
                for (String greeting : greetings) {
                    if (msg.equals(greeting + person)
                            || msg.equals(greeting + person + "s")
                            || msg.equals(greeting + person + "z")) {
                        greet = greetingsInAnswer[random(0,
                                greetingsInAnswer.length - 1)];
                        type = "hi";
                    }
                }
                for (String bye : byes) {
                    if (msg.equals(bye + person)
                            || msg.equals(bye + person + "s")
                            || msg.equals(bye + person + "z")) {
                        greet = byesInAnswer[random(0, byesInAnswer.length - 1)];
                        type = "bye";
                    }
                }
                for (String sup : sups) {
                    if (msg.equals(sup + person)
                            || msg.equals(sup + person + "s")
                            || msg.equals(sup + person + "z")) {
                        greet = supsInAnswer[random(0, supsInAnswer.length - 1)];
                        type = "sup";
                    }
                }
            }
            for (String are : areyou) {
                for (String botting : bottingNames) {
                    if (msg.equals(are + botting)) {
                        greet = lols[random(0, lols.length)]
                                + nos[random(0, nos.length)]
                                + whys[random(0, whys.length)];
                        type = "bot";
                    }
                }
            }
            if (msg.contains("questpoints")) {
                greet = questpointsInAnswer[random(0,
                        questpointsInAnswer.length - 1)];
                type = "questpoints";
            }
            if (!user.equals(player.getMine().getName())) {
                if (!skillname.equals("") && !skillnameReal.equals("")
                        && TimersDontHave(skillnameReal + "lvl")) {
                    wait(random(500, 900));
                    String my = beforeSayInAnswer[random(0,
                            beforeSayInAnswer.length - 1)];
                    String level = levelNamesInAnswer[random(0,
                            levelNamesInAnswer.length - 1)];
                    int stat = skills.getRealLvl(Skills.getStatIndex(skillnameReal));
                    if (!player.getMine().isIdle()) {
                        checkStats();
                    } else if (player.getMine().isIdle()) {
                        wait(random(500, 900));
                        if (player.getMine().isIdle()) {
                            checkStats();
                        }
                    }
                    if (random(1, 4) == 3) {
                        answer = stat + "";
                    } else {
                        answer = (my + " " + skillname + " " + level + " is " + stat).replace("  ", " ");
                    }
                    String end = ends[random(0, ends.length - 1)];
                    input.sendKeys(answer + end, true);
                    addTimer(skillnameReal + "lvl");
                    log("Chat responder answered to: " + originalMsg);
                    log("With: " + answer + end);
                } else if (!greet.equals("")
                        && ((type.equals("hi") && TimersDontHave("hi"))
                        || (type.equals("bye") && TimersDontHave("bye"))
                        || (type.equals("sup") && TimersDontHave("sup"))
                        || (type.equals("questpoints") && TimersDontHave("questpoints")) || (type.equals("bot") && TimersDontHave("bot")))) {
                    wait(random(500, 900));
                    String end = ends[random(0, ends.length - 1)];
                    if (type.equals("questpoints")) {
                        if (qpamount.equals("") || random(0, 5) == 0) {
                            game.openTab(TAB_QUESTS);
                            wait(random(300, 500));
                            qpamount = (iface.get(190).getChild(
                                    2).getText().replace("Quest Points:", "").replace(" ", "").split("/"))[0];
                            if (random(0, 2) == 0) {
                                game.openTab(TAB_INVENTORY);
                                wait(random(300, 500));
                            }
                        }
                        input.sendKeys(greet.replace("qpam", qpamount).replace(
                                "qpna", qpNames[random(0, qpNames.length - 1)])
                                + end, true);
                        log("Chat responder answered to: " + originalMsg);
                        log("With: "
                                + greet.replace("qpam", qpamount).replace(
                                "qpna",
                                qpNames[random(0, qpNames.length - 1)])
                                + end);

                    } else {
                    	input.sendKeys(greet + end, true);
                        log("Chat responder answered to: " + originalMsg);
                        log("With: " + greet + end);
                    }
                    addTimer(type);
                    log("Chat responder answered to: " + originalMsg);
                    log("With: " + greet);
                }
            }
            lastMsg = originalMsg;
        } catch (Exception e) {
        }
        return 0;
    }

    public void checkStats() {
        if (game.getCurrentTab() != TAB_STATS) {
            game.openTab(TAB_STATS);
            wait(random(500, 700));
        }
        mouse.move(random(547, 734), random(205, 464));
        wait(random(500, 900));
        game.openTab(TAB_INVENTORY);
    }

    public void addTimer(String name) {
        for (int i = timers.length - 1; i >= 0; i--) {
            if (i != 0) {
                timers[i] = timers[i - 1];
            } else {
                timers[i] = 30;
            }
        }
        for (int i = previousMsgs.length - 1; i >= 0; i--) {
            if (i != 0) {
                previousMsgs[i] = previousMsgs[i - 1];
            } else {
                previousMsgs[i] = name;
            }
        }
    }

    public boolean TimersDontHave(String msg) {
        for (String message : previousMsgs) {
            if (message.equals(msg)) {
                return false;
            }
        }
        return true;
    }

    public String getLastChatMessage() {
        String originalMsg = "";
        String user = "";
        RSInterface chatinterface = iface.get(137);
        for (RSInterfaceChild child : chatinterface.getChildren()) {
            if (child.getText().contains("<col=0000ff>")) {
                String[] msg = child.getText().split(": <col=0000ff>");
                if (msg.length >= 2) {
                    originalMsg = msg[1];
                    user = msg[0];
                }
            }
        }
        return user + ".,:,." + originalMsg;
    }

    public void onRepaint(Graphics g) {
        if (!getLastChatMessage().equals(lastMsg)) {
            checkChat = true;
        } else {
            checkChat = false;
        }
        int defXpGained = skills.getCurrentXP(Constants.STAT_DEFENSE)
                - defStartExp;
        int hpXpGained = skills.getCurrentXP(Constants.STAT_HITPOINTS)
                - hpStartExp;
        int strXpGained = skills.getCurrentXP(Constants.STAT_STRENGTH)
                - strStartExp;
        int atkXpGained = skills.getCurrentXP(Constants.STAT_ATTACK)
                - atkStartExp;
        long millis = System.currentTimeMillis() - startTime;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;
        float defXpHour = xpHour(hours, minutes, seconds, defXpGained);
        float hpXpHour = xpHour(hours, minutes, seconds, hpXpGained);
        float strXpHour = xpHour(hours, minutes, seconds, strXpGained);
        float atkXpHour = xpHour(hours, minutes, seconds, atkXpGained);
        String cStyle = "";
        if (iface.get(884).getChild(29).getBackgroundColor() == 654) {
            cStyle = iface.get(884).getChild(31).getText();
        } else if (iface.get(884).getChild(26).getBackgroundColor() == 654) {
            cStyle = iface.get(884).getChild(27).getText();
        } else if (iface.get(884).getChild(23).getBackgroundColor() == 654) {
            cStyle = iface.get(884).getChild(25).getText();
        } else if (iface.get(884).getChild(20).getBackgroundColor() == 654) {
            cStyle = iface.get(884).getChild(22).getText();
        }
        String combatLvl = iface.get(884).getChild(1).getText().replace("Combat Lvl: ", "").replace(" ", "");
        if (showPaint && game.isLoggedIn()) {
            ((Graphics2D) g).setRenderingHints(rh);
            g.setColor(new Color(0, 0, 0, 205));
            g.fillRect(375, 344, 138, 24);
            g.setFont(new Font("Comic Sans MS", 0, 11));
            g.setColor(new Color(255, 255, 255));
            if (paint == false) {
                g.drawString("Turn on paint", 394, 361);
            }
            if (paint) {
                int x = 15;
                int x2 = 149;
                g.drawString("Turn off paint", 394, 361);
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(6, 344, 369, 129);
                g.setColor(new Color(255, 255, 255));
                g.setFont(new Font("Comic Sans MS", 0, 18));
                g.drawString(info.name() + " " + info.version(), 21, 368);
                g.setFont(new Font("Comic Sans MS", 0, 11));
                if (page == 1) {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(375, 377, 138, 26);
                    g.setColor(new Color(255, 255, 255));
                    // LEFT SIDE //
                    g.drawString("Lobsters fished: " + lobsters, x, 393);
                    g.drawString("Combat Style Is Changed " + styleChangeCount
                            + " Times", x, 411);
                    g.drawString("Rubbish Dropped " + rubbish + " Times", x,
                            429);
                    g.drawString("Status: " + status, x, 447);

                    // RIGHT SIDE //
                    g.drawString("Trips: " + trips, 233, 393);
                    g.drawString("Levels gained: " + levels, 233, 411);
                    g.drawString("Combat level: " + combatLvl, 233, 429);
                    g.drawString("Combat style: " + cStyle, 233, 447);

                } else {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(385, 377, 128, 26);
                }
                if (page == 2) {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(375, 412, 138, 26);
                    g.setColor(Color.white);
                    int y4 = 0;
                    if (defXpGained > 0) {
                        g.drawString(
                                "Def Xp Gained: " + df.format(defXpGained), x,
                                393);
                        y4 += 18;
                    }
                    if (strXpGained > 0) {
                        g.drawString(
                                "Str Xp Gained: " + df.format(strXpGained), x,
                                393 + y4);
                        y4 += 18;
                    }
                    if (atkXpGained > 0) {
                        g.drawString(
                                "Atk Xp Gained: " + df.format(atkXpGained), x,
                                393 + y4);
                        y4 += 18;
                    }
                    if (hpXpGained > 0) {
                        g.drawString("HP Xp Gained: " + df.format(hpXpGained),
                                x, 393 + y4);
                        y4 += 18;
                    }

                    // RIGHT SIDE//

                    int y3 = 0;
                    if (defXpHour > 0) {
                        g.drawString("Def Xp/hour: " + df.format(defXpHour),
                                x2 + 20, 393);
                        y3 += 18;
                    }
                    if (strXpHour > 0) {
                        g.drawString("Str Xp/hour: " + df.format(strXpHour),
                                x2 + 20, 393 + y3);
                        y3 += 18;
                    }
                    if (atkXpHour > 0) {
                        g.drawString("Atk Xp/hour: " + df.format(atkXpHour),
                                x2 + 20, 393 + y3);
                        y3 += 18;
                    }
                    if (hpXpHour > 0) {
                        g.drawString("HP Xp/hour: " + df.format(hpXpHour),
                                x2 + 20, 393 + y3);
                        y3 += 18;
                    }
                    if (y3 == 0) {
                        g.drawString("Wait till the script gets to killing",
                                94, 420);
                    }
                } else {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(385, 412, 128, 26);
                }
                if (page == 3) {
                    int y = 0;
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(375, 447, 138, 26);
                    g.setColor(new Color(255, 255, 255));
                    if (defXpGained > 0) {
                        ProgBar(g, 15, 385, 347, 10, Constants.STAT_DEFENSE,
                                Color.red, Color.green, Color.black);
                        progBarLocs[0] = 385 + y;
                        y += 18;
                    }
                    if (strXpGained > 0) {
                        ProgBar(g, 15, 385 + y, 347, 10,
                                Constants.STAT_STRENGTH, Color.red,
                                Color.green, Color.black);
                        progBarLocs[1] = 385 + y;
                        y += 18;
                    }
                    if (atkXpGained > 0) {
                        ProgBar(g, 15, 385 + y, 347, 10, Constants.STAT_ATTACK,
                                Color.red, Color.green, Color.black);
                        progBarLocs[2] = 385 + y;
                        y += 18;
                    }
                    if (hpXpGained > 0) {
                        ProgBar(g, 15, 385 + y, 347, 10,
                                Constants.STAT_HITPOINTS, Color.red,
                                Color.green, Color.black);
                        progBarLocs[3] = 385 + y;
                        y += 18;
                    }
                    if (y == 0) {
                        g.drawString("Wait till the script gets to killing",
                                94, 420);
                    }
                } else {
                    g.setColor(new Color(0, 0, 0, 205));
                    g.fillRect(385, 447, 128, 26);
                }
                g.setFont(new Font("Comic Sans MS", 0, 11));
                g.setColor(new Color(255, 255, 255));
                g.drawString(hours + ":" + minutes + ":" + seconds, 301, 366);
                g.drawString("General Info", 394, 393);
                g.drawString("Averaging Info", 394, 428);
                g.drawString("Other info", 394, 463);
            }
            drawMouse(g);
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
            if (m.getX() >= 15 && m.getX() < 15 + 347 && m.getY() >= progBarLocs[0]
                    && m.getY() < progBarLocs[0] + 10 && page == 3
                    && defXpGained > 0) {
                showDefenseInfo = true;
            } else {
                showDefenseInfo = false;
            }
            if (m.getX() >= 15 && m.getX() < 15 + 347 && m.getY() >= progBarLocs[1]
                    && m.getY() < progBarLocs[1] + 10 && page == 3
                    && strXpGained > 0) {
                showStrengthInfo = true;
            } else {
                showStrengthInfo = false;
            }
            if (m.getX() >= 15 && m.getX() < 15 + 347 && m.getY() >= progBarLocs[2]
                    && m.getY() < progBarLocs[2] + 10 && page == 3
                    && atkXpGained > 0) {
                showAttackInfo = true;
            } else {
                showAttackInfo = false;
            }
            if (m.getX() >= 15 && m.getX() < 15 + 347 && m.getY() >= progBarLocs[3]
                    && m.getY() < progBarLocs[3] + 10 && page == 3 && hpXpGained > 0) {
                showHPInfo = true;
            } else {
                showHPInfo = false;
            }
            if (showDefenseInfo && paint) {
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(m.getX(), m.getY() - 100, 200, 100);
                g.setColor(Color.white);
                g.drawString("DEFENSE", m.getX() + 15, m.getY() - 75);
                g.drawString("Level: "
                        + skills.getCurrentLvl(Constants.STAT_DEFENSE),
                        m.getX() + 15, m.getY() - 60);
                g.drawString("Xp: "
                        + skills.getCurrentXP(Constants.STAT_DEFENSE),
                        m.getX() + 15, m.getY() - 45);
                g.drawString("Xp Till Next level: "
                        + skills.getXPToNextLvl(Constants.STAT_DEFENSE),
                        m.getX() + 15, m.getY() - 30);
                g.drawString(
                        "% To Next Level: "
                        + +skills.getPercentToNextLvl(Constants.STAT_DEFENSE),
                        m.getX() + 15, m.getY() - 15);

            }
            if (showStrengthInfo && paint) {
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(m.getX(), m.getY() - 100, 200, 100);
                g.setColor(Color.white);
                g.drawString("STRENGTH", m.getX() + 15, m.getY() - 75);
                g.drawString("Level: "
                        + skills.getCurrentLvl(Constants.STAT_STRENGTH),
                        m.getX() + 15, m.getY() - 60);
                g.drawString("Xp: "
                        + skills.getCurrentXP(Constants.STAT_STRENGTH),
                        m.getX() + 15, m.getY() - 45);
                g.drawString("Xp Till Next level: "
                        + skills.getXPToNextLvl(Constants.STAT_STRENGTH),
                        m.getX() + 15, m.getY() - 30);
                g.drawString(
                        "% To Next Level: "
                        + +skills.getPercentToNextLvl(Constants.STAT_STRENGTH),
                        m.getX() + 15, m.getY() - 15);

            }
            if (showAttackInfo && paint) {
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(m.getX(), m.getY() - 100, 200, 100);
                g.setColor(Color.white);
                g.drawString("ATTACK", m.getX() + 15, m.getY() - 75);
                g.drawString("Level: "
                        + skills.getCurrentLvl(Constants.STAT_ATTACK),
                        m.getX() + 15, m.getY() - 60);
                g.drawString("Xp: "
                        + skills.getCurrentXP(Constants.STAT_ATTACK),
                        m.getX() + 15, m.getY() - 45);
                g.drawString("Xp Till Next level: "
                        + skills.getXPToNextLvl(Constants.STAT_ATTACK),
                        m.getX() + 15, m.getY() - 30);
                g.drawString("% To Next Level: "
                        + +skills.getPercentToNextLvl(STAT_ATTACK), m.getX() + 15,
                        m.getY() - 15);

            }
            if (showHPInfo && paint) {
                g.setColor(new Color(0, 0, 0, 205));
                g.fillRect(m.getX(), m.getY() - 100, 200, 100);
                g.setColor(Color.white);
                g.drawString("CONSTITUTION", m.getX() + 15, m.getY() - 75);
                g.drawString("Level: "
                        + skills.getCurrentLvl(Constants.STAT_HITPOINTS),
                        m.getX() + 15, m.getY() - 60);
                g.drawString("Xp: "
                        + skills.getCurrentXP(Constants.STAT_HITPOINTS),
                        m.getX() + 15, m.getY() - 45);
                g.drawString("Xp Till Next level: "
                        + skills.getXPToNextLvl(Constants.STAT_HITPOINTS),
                        m.getX() + 15, m.getY() - 30);
                g.drawString(
                        "% To Next Level: "
                        + +skills.getPercentToNextLvl(Constants.STAT_HITPOINTS),
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

            if (hours == 2 && minutes == 0 && seconds == 0) {
                log("w00t! ran for 2 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 3 && minutes == 0 && seconds == 0) {
                log("awesome! ran for 3 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 4 && minutes == 0 && seconds == 0) {
                log("Epic! ran for 4 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 5 && minutes == 0 && seconds == 0) {
                log("Hell yeaH! ran for 5 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 6 && minutes == 0 && seconds == 0) {
                log("keep it up! ran for 6 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 7 && minutes == 0 && seconds == 0) {
                log("NICE NICE! ran for 7 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 8 && minutes == 0 && seconds == 0) {
                log("SICK! ran for 8 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 9 && minutes == 0 && seconds == 0) {
                log("DA PERFECT PROGGY! ran for 9 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
            if (hours == 10 && minutes == 0 && seconds == 0) {
                log("FUCKING AWESOME DUDE! ran for 10 hours! taking screenie :)");
                ScreenshotUtil.takeScreenshot(bot, true);
            }
        }
    }

    public void messageReceived(MessageEvent arg0) {
        final String msg = arg0.getMessage().toLowerCase();
        if (msg.contains("you catch a lobster")) {
            lobsters++;
        }
        if (msg.contains("you've just advanced a")) {
            levels++;
            game.openTab(TAB_ATTACK);
            wait(random(500, 800));
            game.openTab(TAB_INVENTORY);
        }
        if (msg.contains("you climb down through")) {
            trips++;
        }
    }
}
