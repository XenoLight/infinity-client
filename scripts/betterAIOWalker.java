
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.event.listeners.PaintListener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

@ScriptManifest(name = "betterAIOWalker",
category = "Other",
        authors = "Waterwolf",
        version = 0.01,
        description = "<html><style type='text/css'>"
        + "body {background:url('http://lazygamerz.org/client/images/back_2.png') repeat}"
        + "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
        + "<h1><center><font color=#FFFFFF>"
        + "betterAIOWalker by; Waterwolf"
        + "</center></font color></h1>"
        + "</head><br><body>"
        + "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
        + "<td width=90% align=justify>"
        + "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"
        + "<font size=3>Click 'OK' to start the script all set up in the GUI...<br>"
        + "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"
        + "</td></tr></table><br />"
        )
public class betterAIOWalker extends Script implements PaintListener {

    private RSTile walkToTile;
    private boolean rest, restmusician;
    private RSTile[] path;
    AIOWalkerCheckStuck checkStuck;
    Thread t;
    public BufferedImage RunningIcon;
    public int restStatus = 0; // 0nothing 1resting needed 2npc found 3resting via npc 4resting without npc
    public int[] musicianID = {8707 /* al kharid*/,
        8700 /*plank making and varrock between*/,
        29 /*NE of lumby castle*/,
        30 /* draynor village*/,
        5439 /* south falador*/,
        5442 /* north falador*/,
        8699 /* varrock west */,
        8701 /*in front of alkharid free entrance*/,
        8702 /*west of grand tree place*/,
        8703 /*fishing guild*/};
    public int nextRest = random(10, 40), nextRestM = random(70, 90);
    public RSTile[] bankLocations = {new RSTile(3185, 3439) /*varrock west*/,
        new RSTile(3253, 3421)/*varrock east*/,
        new RSTile(3270, 3166)/*al kharid*/,
        new RSTile(3207, 3210) /*lumbridge*/,
        new RSTile(3093, 3243) /*draynor village*/,
        new RSTile(3013, 3356) /*fala east*/,
        new RSTile(2945, 3370) /*fala west*/,
        new RSTile(3094, 3493) /* edgeville*/,
        new RSTile(2725, 3486) /* seer's bank*/}; //TODO add locs
    public RSTile[] barLocations = {new RSTile(2959, 3373) /*falador*/,
        null};
    public boolean walkByYourself = false;
    public int startDistance = 0;
    public boolean start = false;
    public long startTime = System.currentTimeMillis();

    public boolean onStart(final Map<String, String> args) {

        final WalkerGui gui = new WalkerGui();
        gui.setVisible(true);

        start = false;

        while (gui.isVisible()) {
            wait(random(500, 600));
        }
        if (!start) {
            return false;
        }

        /*
        if (args.get("RestModeM") != null) {
        restmusician = true;
        }
        if (args.get("RestMode") != null) {
        rest = true;
        }
        if (args.get("mode").equals("gps")) {
        walkByYourself = true;
        }
        if (args.get("Walk").equals("Custom Location")) {
        walkToTile = new RSTile(Integer.parseInt(args.get("XCoordinate")),
        Integer.parseInt(args.get("YCoordinate")));
        }
        else if (args.get("Walk").equals("Special location")) {
        walkToTile = getByName(args.get("Spec"));
        }
        else {
        walkToTile = loadPlaces().get(args.get("Walk"));
        }
         */

        try {
            final URL agi = new URL(
                    "http://images1.wikia.nocookie.net/__cb20100116042232/runescape/images/2/2e/Agility_logo_detail.png");
            RunningIcon = ImageIO.read(agi);
        } catch (MalformedURLException e) {
            log("Unable to buffer cursor.");
        } catch (IOException e) {
            log("Unable to open cursor image.");
        }

        checkStuck = new AIOWalkerCheckStuck();
        t = new Thread(checkStuck);
        return walkToTile != null;
    }

    protected int getMouseSpeed() {
        return random(6, 11);
    }

    @Override
    public int loop() {
        try {
            if (!t.isAlive() && !walkByYourself) {
                t.start();
            }
            if (path == null) {
                log("Generating a path from your current location...");
                path = walk.cleanPath(walk.generateFixedPath(walkToTile));
                log("Mission started.");
            }
            if (walkByYourself) {
                if (calculate.distanceTo(path[path.length - 1]) < 5) {
                    log("Mission accomplished.");
                    stopScript(false);
                }
                return 100;
            }
            if (calculate.distanceTo(path[path.length - 1]) > 5) {
                getMouseSpeed();
                camera.setAltitude(true);
                RSNPC musician = npc.getNearestByID(musicianID);
                if (restmusician && !rest && player.getMyEnergy() < 20 && musician == null) {
                    restStatus = 1;
                }
                if (restmusician && player.getMyEnergy() < nextRestM && musician != null) {
                    restStatus = 2;
                    boolean failRest = false;
                    walk.to(musician.getLocation());
                    player.waitToMove(2000);
                    while (player.getMine().isMoving()) {
                        wait(200);
                    }
                    for (int d = 0; d < 5; d++) {
                       npc.click(musician, "Listen-to");
                        wait(random(800, 1600));
                        if (player.getMine().getAnimation() == 12108 || player.getMine().getAnimation() == 2033 || player.getMine().getAnimation() == 2716 || player.getMine().getAnimation() == 11786 || player.getMine().getAnimation() == 5713) {
                            break;
                        }
                        if (d == 4) {
                            log("Rest failed!");
                            failRest = true;
                        }
                    }
                    if (!failRest) {
                        restStatus = 3;
                        int energy = 0;
                        int restto = random(80, 100);
                        while (energy < restto && restStatus == 3) {
                            wait(random(250, 500));
                            energy = player.getMyEnergy();
                        }
                        restStatus = 0;
                        nextRestM = random(70, 90);
                    }
                }
                if (rest && player.getMyEnergy() < nextRest && musician == null) {
                    restStatus = 4;
                    player.rest(random(85, 95));
                    restStatus = 0;
                    nextRest = random(10, 40);
                }
                if (player.getMyEnergy() > 60 && random(1, 10) == 0 || player.getMyEnergy() > 80) {
                    game.setRun(true);
                }

                if (calculate.distanceTo(walk.getDestination()) < random(7, 13)
                        || calculate.distanceTo(walk.getDestination()) > 40) {
                    if (!walk.pathMM(path)) {
                        //walkToClosestTile(path);
                        RSTile furthestTile = getFurthestTileOnMap(path);
                        walk.to(furthestTile);
                    }
                }
                return (random(200, 400));
            } else {
                log("Mission accomplished.");
                return -1;
            }
        } catch (final Exception e) {
            log("Uh oh! Found an exception! Contact the script author if this persists!");
        }
        return random(400, 600);
    }

    public void onFinish() {
        checkStuck.stopThread = true;
    }

    public RSTile getByName(String name) {
        if (name.equals("Nearest bank")) {
            return getClosestTile(bankLocations);
        }
        if (name.equals("Nearest bar")) {
            return getClosestTile(barLocations); // for LULZ
        }
        log(name);
        return null;
    }

    private static Map<String, RSTile> loadPlaces() {
        final Map<String, RSTile> ret = new HashMap<String, RSTile>();
        ret.put("Varrock", new RSTile(3214, 3424));
        ret.put("Lumbridge", new RSTile(3221, 3219));
        ret.put("Camelot", new RSTile(2964, 3380));
        ret.put("Falador", new RSTile(2964, 3378));
        ret.put("Yanille", new RSTile(2604, 3094));
        ret.put("The Grand Exchange", new RSTile(3165, 3487));
        ret.put("East Ardougne", new RSTile(2661, 3300));
        ret.put("Burthorpe", new RSTile(2899, 3546));
        ret.put("Rellekka", new RSTile(2643, 3677));
        ret.put("Edgeville", new RSTile(3094, 3493));
        ret.put("Mage Store", new RSTile(3253, 3402));
        ret.put("Draynor Village", new RSTile(3093, 3243));
        ret.put("Rimmington", new RSTile(2957, 3214));
        ret.put("Port Sarim", new RSTile(3023, 3208));
        ret.put("Al Kharid", new RSTile(3293, 3170));
        ret.put("Barbarian Village", new RSTile(3082, 3419));
        ret.put("Vampire Slayer", new RSTile(3099, 3269));
        ret.put("Ernest the Chicken", new RSTile(3111, 3329));
        ret.put("The Restless Ghost", new RSTile(3242, 3205));
        ret.put("The Cook's Assistant", new RSTile(3208, 3216));
        ret.put("Rune Mysteries(Have to Go Upstairs By Urself)",
                new RSTile(3205, 3209));
        ret.put("Sheep Shearer", new RSTile(3189, 3276));
        ret.put("Demon Slayer", new RSTile(3203, 3423));
        ret.put("Romeo & Juliet", new RSTile(3213, 3424));
        ret.put("Shield of Arrav", new RSTile(3210, 3489));
        ret.put("Dragon Slayer (Champion's Guild)", new RSTile(3191,
                3363));
        ret.put("The Knight's Sword", new RSTile(2978, 3343));
        ret.put(
                "Black Knight's Fortress (go in, go up two sets of stairs)",
                new RSTile(2965, 3339));
        ret.put("Doric's Quest", new RSTile(2949, 3450));
        ret.put("Goblin Diplomacy", new RSTile(2958, 3509));
        ret.put("Imp Catcher(in 2 flights up)", new RSTile(3109, 3167));
        ret.put("Pirate's Treasure", new RSTile(3052, 3248));
        ret.put("Witch's Potion", new RSTile(2964, 3206));
        ret.put("Wizard Tower", new RSTile(3109, 3167));
        ret.put("Dwarven Mines", new RSTile(3018, 3450));
        ret.put("Seer's Bank", new RSTile(2725, 3486));
        ret.put("Druid's Circle", new RSTile(2926, 3482));
        ret.put("White Wolf Mountain", new RSTile(2848, 3498));
        ret.put("Catherby", new RSTile(2813, 3447));
        ret.put("Ranging Guild", new RSTile(2665, 3430));
        ret.put("Fishing Guild", new RSTile(2603, 3414));
        ret.put("Agility (Barbarian Outpost)", new RSTile(2541, 3546));
        ret.put("Grand Tree (upper)", new RSTile(2480, 3488));
        ret.put("Grand Tree (lower)", new RSTile(2466, 3490));
        ret.put("Tree Gnome Stronghold", new RSTile(2461, 3443));
        ret.put("West Ardougne", new RSTile(2535, 3305));
        ret.put("Prifddinas", new RSTile(2242, 3278));
        ret.put("Elf camp (Tirannwn)", new RSTile(2197, 3252));
        ret.put("Isafdar (Tirannwn)", new RSTile(2241, 3238));
        ret.put("Duel Arena", new RSTile(3360, 3213));
        ret.put("Desert Mining Camp", new RSTile(3286, 3023));
        ret.put("Bedabin Camp", new RSTile(3171, 3026));
        ret.put("Bandit Camp", new RSTile(3176, 2987));
        ret.put("Pollnivneach", new RSTile(3365, 2970));
        ret.put("Pyramid", new RSTile(3233, 2901));
        ret.put("Sophanem", new RSTile(3305, 2755));
        ret.put("Ruins of Uzer", new RSTile(3490, 3090));
        ret.put("Mort'ton", new RSTile(3489, 3288));
        ret.put("Canifis", new RSTile(3506, 3496));
        ret.put("Port Phasmatys", new RSTile(3687, 3502));
        ret.put("Fenkenstrain's Castle", new RSTile(3550, 3548));
        ret.put("Dig Site", new RSTile(3354, 3402));
        ret.put("Exam Centre", new RSTile(3354, 3344));
        ret.put("Crafting Guild", new RSTile(2933, 3285));
        ret.put("Fight Arena", new RSTile(2585, 3150));
        ret.put("Tree Gnome Village (outside)", new RSTile(2521, 3177));
        ret.put("Tree Gnome Village (inside)", new RSTile(2525, 3167));
        ret.put("Port Khazard", new RSTile(2665, 3161));
        ret.put("Monastery", new RSTile(3051, 3490));
        ret.put("Crandor", new RSTile(2851, 3238));
        ret.put("Tzhaar", new RSTile(2480, 5175));
        ret.put("Bob's Island", new RSTile(2526, 4777));
        ret.put("Mining Guild", new RSTile(3049, 9737));
        ret.put("Ape Atoll", new RSTile(2801, 2704));
        ret.put("Cooking Guild", new RSTile(3143, 3442));
        ret.put("Monk Guild", new RSTile(3050, 3487));
        ret.put("Hero's Guild", new RSTile(2902, 3510));
        ret.put("Death Altar (N/A)", new RSTile(2207, 4836));
        ret.put("Cosmic Altar (N/A)", new RSTile(2162, 4833));
        ret.put("Air Altar", new RSTile(3129, 3405));
        ret.put("Water Altar", new RSTile(3185, 3163));
        ret.put("Earth Altar", new RSTile(3304, 3473));
        ret.put("Fire Altar", new RSTile(3313, 3253));
        ret.put("Body Altar", new RSTile(3053, 3443));
        ret.put("Law Altar (N/A)", new RSTile(2464, 4834));
        ret.put("Mind Altar", new RSTile(2980, 3514));
        ret.put("Weird Altar (N/A)", new RSTile(2528, 4833));
        ret.put("Chaos Altar (N/A)", new RSTile(2269, 4843));
        ret.put("Desert City", new RSTile(3291, 2764));
        ret.put("Games Room", new RSTile(2196, 4961));
        ret.put("Slayer Tower", new RSTile(3429, 3429));
        ret.put("Gul'Tanoth", new RSTile(2516, 3044));
        ret.put("Entrana", new RSTile(2834, 3335));
        ret.put("Mage Arena", new RSTile(3107, 3937));
        ret.put("Camelot Flax Field", new RSTile(2744, 3444));
        ret.put("Red Bird (Hunting)", new RSTile(2354, 3585));
        ret.put("Castle Wars", new RSTile(2443, 3086)); // fix by bonebag
        ret.put("Trollheim", new RSTile(2910, 3612));
        ret.put("Warrior's Guild", new RSTile(2877, 3546));
        ret.put("Hunter Guide", new RSTile(2525, 2916)); // guide located by bonebag
        return ret;
    }

    public void highlightTile(final Graphics g, final RSTile t,
            final Color outline, final Color fill) {
        final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
        final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
                0, 0);
        final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
                0, 0);
        final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
                0, 0, 0);
        if (py.x == -1 || pxy.x == -1 || px.x == -1 || pn.x == -1) {
            return;
        }
        g.setColor(outline);
        g.drawPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
                    pxy.y, px.y, pn.y}, 4);
        g.setColor(fill);
        g.fillPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
                    pxy.y, px.y, pn.y}, 4);
    }

    public boolean colorMinimapTile(Graphics g, Color c) {
        Point lastPoint = null;
        if (path == null) {
            return false;
        }

        int pointsSeen = 0;

        for (RSTile place : path) {
            final Point p = tile.toMiniMap(place);
            if (p.x != -1 || p.y != -1) {
                pointsSeen++;
                g.setColor(Color.black);
                if (lastPoint != null) {
                    g.drawLine(lastPoint.x, lastPoint.y, p.x, p.y);
                }
                int size = 3;
                g.setColor(c);
                if (place == path[path.length - 1]) {
                    g.setColor(Color.red);
                    size = 5;
                }
                if (place == path[0]) {
                    size = 5;
                    g.setColor(Color.yellow);
                }

                g.fillRect(p.x + (4 - size), p.y + (4 - size), size, size);
                highlightTile(g, place, g.getColor(), colorParse(g.getColor(), 0));
                lastPoint = p;
            }
        }
        if (pointsSeen == 0) {
            g.setColor(Color.blue);
            Point me = player.getMyLocation().getScreenLocation();
            RSTile lol = walk.generateFixedPath(getClosestTile(path))[0];
            Point wh = tile.toMiniMap(lol);
            g.drawLine(me.x, me.y, wh.x, wh.y);

            //TODO doesn't work. (use getangletotile?)
        } else if (npc.getNearestByID(musicianID) != null) {
            final Point p = tile.toMiniMap(npc.getNearestByID(musicianID).getLocation());
            if (p.x != -1 || p.y != -1) {
                g.setColor(Color.cyan);
                g.fillRect(p.x - 1, p.y - 1, 5, 5);
                g.setColor(Color.blue);
                Point me = player.getMyLocation().getMapLocation();
                g.drawLine(me.x, me.y, p.x, p.y);
            }
        } else if (walk.getDestination() != null) {
            g.setColor(Color.blue);
            Point me = player.getMyLocation().getMapLocation();
            Point dest = tile.toMiniMap(walk.getDestination());
            g.drawLine(me.x, me.y, dest.x, dest.y);
        }


        return true;
    }

    public RSTile getClosestTile(RSTile[] tile) {
        if (!game.isLoggedIn()) {
            return null;
        }
        int closest = 10000;
        RSTile cclosest = null;
        for (RSTile one : tile) {
            if (distanceT(player.getMyLocation(), one) < closest) {
                closest = distanceT(player.getMyLocation(), one);
                cclosest = one;
            }
        }
        return cclosest;
    }

    public int getClosestTileIndex(RSTile[] tile) {
        if (!game.isLoggedIn()) {
            return -1;
        }
        int closest = 10000;
        RSTile cclosest = null;
        for (RSTile one : tile) {
            if (distanceT(player.getMyLocation(), one) < closest) {
                closest = distanceT(player.getMyLocation(), one);
                cclosest = one;
            }
        }
        for (int p = 0; p < tile.length; p++) {
            if (tile[p] == cclosest) {
                return p;
            }
        }
        return -1;
    }

    public int getTileIndex(RSTile t) {
        for (int p = 0; p < path.length; p++) {
            if (path[p] == t) {
                return p;
            }
        }
        return -1;
    }

    public RSTile getFurthestTileOnMap(RSTile[] tile) {
        if (!game.isLoggedIn()) {
            return null;
        }
        int furthest = 0;
        RSTile ffurthest = null;
        for (RSTile one : tile) {
            if (distanceT(player.getMyLocation(), one) > furthest && one.isOnMinimap() && getClosestTileIndex(path) <= getTileIndex(one)) {
                furthest = distanceT(player.getMyLocation(), one);
                ffurthest = one;
            }
        }
        return ffurthest;
    }

    public int calcDistance(RSTile[] pathh) {
        int d = 0;
        RSTile old = null;
        for (RSTile one : pathh) {
            if (old == null) {
                old = one;
            } else {
                d += distanceT(old, one);
                old = one;
            }
        }
        return d;
    }

    public int getPathLeft(RSTile[] pathh) {
        int d = 0;
        RSTile old = null;
        for (RSTile one : pathh) {
            if (old == null && calculate.distanceTo(one) < 20) {
                old = one;
            } else if (old != null) {
                d += distanceT(old, one);
                old = one;
            }
        }
        return d;
    }

    public Color colorParse(Color c, int t) {
        if (t == 0) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), 80);
        }
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
    }

    private String cTime(long eTime) {
        final long hrs = eTime / 1000 / 3600;
        eTime -= hrs * 3600 * 1000;
        final long mins = eTime / 1000 / 60;
        eTime -= mins * 60 * 1000;
        final long secs = eTime / 1000;
        return String.format("%1$02d:%2$02d:%3$02d", hrs, mins, secs);
    }

    public void onRepaint(Graphics g) {
        colorMinimapTile(g, new Color(50, 205, 50));

        long runTime = System.currentTimeMillis() - startTime;
        g.setColor(new Color(204, 255, 153, 150));
        g.fillRect(4, 268, 458, 69);

        if (RunningIcon != null) {
            g.drawImage(RunningIcon, 403, 285, null);
        }

        g.setColor(Color.black);
        g.drawString("Runtime: " + cTime(runTime), 10, 330);

        int curDistance = 0;

        if (startDistance == 0 && path != null) {
            startDistance = calcDistance(path);
            g.drawString("Path length: " + startDistance, 10, 315);
        } else if (path != null) {
            curDistance = getPathLeft(path);
            RSTile closest = getClosestTile(path);
            if (curDistance == 0 || closest == null) {
                return;
            }
            g.drawString("Path left/total: " + curDistance + "/" + startDistance + " <- calculates by closest rstile [" + closest.getX() + "-" + closest.getY() + "])", 10, 315);
        }



        String rs = "None";
        switch (restStatus) {
            case 1:
                rs = "Needs rest (searching for musician)";
                break;
            case 2:
                rs = "Musician found";
                break;
            case 3:
                rs = "Resting.. (musician)";
                break;
            case 4:
                rs = "Resting.";
                break;
        }


        g.drawString("Resting status: " + rs, 10, 300);

        if (curDistance != 0 && startDistance != 0) {
            long timeToFinish = (long) (curDistance / (((float) (startDistance - curDistance)) / ((int) runTime / 1000))) * 1000;
            g.drawString("Approx. time till finish: " + cTime(timeToFinish), 10, 285);
        }




    }

    private int distanceT(RSTile from, RSTile to) {
        int dx = from.getX() - to.getX();
        int dy = from.getY() - to.getY();
        return (int) Math.sqrt(dx * dx + dy * dy);
    }

    private class AIOWalkerCheckStuck implements Runnable {

        private boolean stopThread;

        public void run() {
            while (!stopThread && !walkByYourself) {
                try {
                    if (player.getMine().getAnimation() != 12108
                            && player.getMine().getAnimation() != 2033
                            && player.getMine().getAnimation() != 2716
                            && player.getMine().getAnimation() != 11786
                            && player.getMine().getAnimation() != 5713) {
                        RSTile oldLoc = player.getMyLocation();
                        int i = 0;
                        for (i = 0; i < 20; i++) {
                            Thread.sleep(random(500, 1000));
                            if (player.getMine().getLocation().equals(oldLoc)) {
                                i += 2;
                            }
                        }
                        if (i > 20) {
                            log("Detected the same player coordinates for quite some time!");
                            walk.tileMM(tile.getClosestOnMap(path[path.length - 1]));
                            log("Generating a new path...");
                            path = walk.cleanPath(walk.generateFixedPath(walkToTile));
                            restStatus = 0;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class WalkerGui extends javax.swing.JFrame {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        /** Creates new form WalkerGui */
        public WalkerGui() {
            initComponents();
        }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        private void initComponents() {

            buttonGroup1 = new javax.swing.ButtonGroup();
            buttonGroup2 = new javax.swing.ButtonGroup();
            jTabbedPane1 = new javax.swing.JTabbedPane();
            jPanel1 = new javax.swing.JPanel();
            jCheckBox1 = new javax.swing.JCheckBox();
            jCheckBox2 = new javax.swing.JCheckBox();
            jRadioButton1 = new javax.swing.JRadioButton();
            jRadioButton2 = new javax.swing.JRadioButton();
            jRadioButton3 = new javax.swing.JRadioButton();
            jComboBox1 = new javax.swing.JComboBox();
            jComboBox2 = new javax.swing.JComboBox();
            jComboBox3 = new javax.swing.JComboBox();
            jLabel1 = new javax.swing.JLabel();
            jFormattedTextField1 = new javax.swing.JFormattedTextField();
            jFormattedTextField2 = new javax.swing.JFormattedTextField();
            jLabel2 = new javax.swing.JLabel();
            jRadioButton4 = new javax.swing.JRadioButton();
            jLabel3 = new javax.swing.JLabel();
            jRadioButton5 = new javax.swing.JRadioButton();
            jButton1 = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setBounds(new java.awt.Rectangle(0, 0, 0, 0));
            setName("Form"); // NOI18N

            jTabbedPane1.setName("jTabbedPane1"); // NOI18N

            jPanel1.setName("jPanel1"); // NOI18N

            jCheckBox1.setText("Enable rest"); // NOI18N

            jCheckBox2.setSelected(true);
            jCheckBox2.setText("Enable rest using musicians"); // NOI18N

            buttonGroup1.add(jRadioButton1);
            jRadioButton1.setSelected(true);
            jRadioButton1.setText("Choose normal location"); // NOI18N

            buttonGroup1.add(jRadioButton2);
            jRadioButton2.setText("Choose special location"); // NOI18N

            buttonGroup1.add(jRadioButton3);
            jRadioButton3.setText("Choose custom location"); // NOI18N

            jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Main cities", "Main places", "Quests", "Miscellanious"})); //TODO lol
            jComboBox1.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    catChanged();
                }
            });

            jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(catItems[0]));


            jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Nearest bank", "Nearest bar"}));

            jLabel1.setText("X:"); // NOI18N

            try {
                jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####")));
            } catch (java.text.ParseException ex) {
                ex.printStackTrace();
            }
            jFormattedTextField1.setText("0000"); // NOI18N

            try {
                jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####")));
            } catch (java.text.ParseException ex) {
                ex.printStackTrace();
            }
            jFormattedTextField2.setText("0000"); // NOI18N

            jLabel2.setText("Y:"); // NOI18N

            buttonGroup2.add(jRadioButton4);
            jRadioButton4.setSelected(true);
            jRadioButton4.setText("Walk automatically"); // NOI18N
            jRadioButton4.setToolTipText("Walks automatically using the script"); // NOI18N

            jLabel3.setText("Walking mode:"); // NOI18N

            buttonGroup2.add(jRadioButton5);
            jRadioButton5.setText("Walk manually"); // NOI18N
            jRadioButton5.setToolTipText("You have to walk by yourself but you will see suggested path in minimap and useful info like. runtime, approx. time left and path length"); // NOI18N

            jButton1.setText("Go"); // NOI18N
            jButton1.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startScript();
                }
            });



            javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(21, 21, 21).addComponent(jLabel3).addContainerGap()).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jRadioButton5).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 417, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(jCheckBox1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jCheckBox2)).addComponent(jRadioButton1, javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jComboBox2, 0, 160, Short.MAX_VALUE)).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(jRadioButton4).addGap(30, 30, 30).addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(jRadioButton2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jComboBox3, 0, 110, Short.MAX_VALUE)).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(jRadioButton3).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))).addGap(261, 261, 261).addComponent(jComboBox2, 0, 0, Short.MAX_VALUE))).addGap(43, 43, 43)))));
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jCheckBox1).addComponent(jCheckBox2)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jRadioButton1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jRadioButton2).addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(11, 11, 11).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jRadioButton3).addComponent(jLabel1).addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel2).addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(12, 12, 12).addComponent(jLabel3).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jRadioButton4).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRadioButton5)).addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(63, 63, 63)));

            jTabbedPane1.addTab("General", jPanel1);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE));


            pack();
        }// </editor-fold>

        public void startScript() {
            if (jRadioButton1.isSelected() && jComboBox2.getSelectedItem().toString() == "-") {
                JOptionPane.showInputDialog("You must select a location!");
                return;
            }
            if (jCheckBox1.isSelected()) {
                rest = true;
            }
            if (jCheckBox2.isSelected()) {
                restmusician = true;
            }
            if (jRadioButton5.isSelected()) {
                walkByYourself = true;
            }
            if (jRadioButton1.isSelected()) {
                walkToTile = loadPlaces().get(jComboBox2.getSelectedItem());
            } else if (jRadioButton2.isSelected()) {
                walkToTile = getByName(jComboBox3.getSelectedItem().toString());
            } else if (jRadioButton3.isSelected()) {
                walkToTile = new RSTile(Integer.parseInt(jFormattedTextField1.getText()),
                        Integer.parseInt(jFormattedTextField2.getText()));
            }
            start = true;
            dispose();
        }

        public void catChanged() {
            int newCat = jComboBox1.getSelectedIndex();
            log(newCat + "");
            DefaultComboBoxModel hi = new javax.swing.DefaultComboBoxModel(catItems[newCat]);
            jComboBox2.setModel(hi);
        }
        /*
        if (args.get("RestModeM") != null) {
        restmusician = true;
        }
        if (args.get("RestMode") != null) {
        rest = true;
        }
        if (args.get("mode").equals("gps")) {
        walkByYourself = true;
        }
        if (args.get("Walk").equals("Custom Location")) {
        walkToTile = new RSTile(Integer.parseInt(args.get("XCoordinate")),
        Integer.parseInt(args.get("YCoordinate")));
        }
        else if (args.get("Walk").equals("Special location")) {
        walkToTile = getByName(args.get("Spec"));
        }
        else {
        walkToTile = loadPlaces().get(args.get("Walk"));
        }
         */
        // Variables declaration - do not modify
        private javax.swing.ButtonGroup buttonGroup1;
        private javax.swing.ButtonGroup buttonGroup2;
        private javax.swing.JButton jButton1;
        private javax.swing.JCheckBox jCheckBox1;
        private javax.swing.JCheckBox jCheckBox2;
        private javax.swing.JComboBox jComboBox1;
        private javax.swing.JComboBox jComboBox2;
        private javax.swing.JComboBox jComboBox3;
        private javax.swing.JFormattedTextField jFormattedTextField1;
        private javax.swing.JFormattedTextField jFormattedTextField2;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JPanel jPanel1;
        private javax.swing.JRadioButton jRadioButton1;
        private javax.swing.JRadioButton jRadioButton2;
        private javax.swing.JRadioButton jRadioButton3;
        private javax.swing.JRadioButton jRadioButton4;
        private javax.swing.JRadioButton jRadioButton5;
        private javax.swing.JTabbedPane jTabbedPane1;
        private String[][] catItems = {{"Varrock", "Lumbridge", "Camelot", "Falador", "Yanille", "East Ardougne", "Burthorpe", "Rellekka", "Edgeville", "Draynor Village", "Rimmington", "Port Sarim", "Al Kharid", "Barbarian Village", "Seer's bank", "West Ardougne"}, //cities
            {"The Grand Exchange"}, //places
            {"Vampire Slayer", "Ernest the Chicken", "The Restless Ghost", "The Cook's Assistant", "Rune Mysteries(Have to Go Upstairs By Urself)", "Sheep Shearer", "Demon Slayer", "Romeo & Juliet", "Shield of Arrav", "Dragon Slayer (Champion's Guild)", "The Knight's Sword", "Black Knight's Fortress (go in, go up two sets of stairs)", "Doric's Quest", "Goblin Diplomacy", "Imp Catcher(in 2 flights up)", "Pirate's Treasure", "Witch's Potion"}, //quest
            {"Mage Store", "Wizard Tower", "Dwarven Mines", "Druid's Circle", "White Wolf Mountain", "Catherby", "Ranging Guild", "Fishing Guild", "Agility (Barbarian Outpost)", "Grand Tree (upper)", "Grand Tree (lower)",
                "Tree Gnome Stronghold", "Prifddinas", "Elf camp (Tirannwn)", "Isafdar (Tirannwn)", "Duel Arena", "Desert Mining Camp",
                "Bebadin Camp", "Bandit Camp", "Pollnivneach", "Pyramid", "Sophanem", "Ruins of Uzer", "Mort'ton", "Canifis", "Port Phasmatys",
                "Fenkenstrain's Castle", "Dig Site", "Exam Centre", "Crafting Guild", "Fight Arena", "Tree Gnome Village (outside)", "Tree Gnome Village (inside)",
                "Port Khazard", "Monastery", "Crandor", "Tzhaar", "Bob's Island", "Mining Guild", "Ape Atoll", "Cooking Guild",
                "Monk Guild", "Hero's Guild", "Death Altar (N/A)", "Cosmic Altar (N/A)", "Air Altar", "Water Altar", "Earth Altar",
                "Fire Altar", "Body Altar", "Law Altar (N/A)", "Mind Altar", "Weird Altar (N/A)", "Chaos Altar (N/A)", "Desert City",
                "Games Room", "Slayer Tower", "Gul'Tanoth", "Entrana", "Mage Arena", "Camelot Flax Field", "Red Bird (Hunting", "Castle Wars", "Trollheim", "Warrior's Guild", "Hunter Guide"}, //misc
        };

        /*
         *
         *


        ret.put("Chaos Altar (N/A)", new RSTile(2269, 4843));
        ret.put("Desert City", new RSTile(3291, 2764));
        ret.put("Games Room", new RSTile(2196, 4961));
        ret.put("Slayer Tower", new RSTile(3429, 3429));
        ret.put("Gul'Tanoth", new RSTile(2516, 3044));
        ret.put("Entrana", new RSTile(2834, 3335));
        ret.put("Mage Arena", new RSTile(3107, 3937));
        ret.put("Camalot Flax Field", new RSTile(2744, 3444));
        ret.put("Red Bird (Hunting)", new RSTile(2354, 3585));
        ret.put("Castle Wars", new RSTile(2400, 3103));
        ret.put("Trollheim", new RSTile(2910, 3612));
        ret.put("Warrior's Guild", new RSTile(2877, 3546));
         *
         */
        // End of variables declaration
        protected ImageIcon createImageIcon(String path,
                String description) {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                return new ImageIcon(imgURL, description);
            } else {
                System.err.println("Couldn't find file: " + path);
                return null;
            }

        }
    }
}
