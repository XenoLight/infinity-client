
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.imageio.ImageIO;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Infinity Dev Team"},
name = "Al-kharid Cooker",
version = 1.00,
category = "Cooking",
website = "http://www.lazygamerz.org/forums/index.php?topic=2395.0",
notes = "",
description = "<style type='text/css'>"
+ "body {background:url('http://lazygamerz.org/client/images/back_1.png') repeat}"
+ "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
+ "<h1><center><font color=#FFFFFF>"
+ "Al-kharid Cooker"
+ "</center></font color></h1>"
+ "</head><br><body>"
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
+ "<td width=90%>"
+ "<center>This runs only in Al-Kalrid. Start At the bank.<br />"
+ "<font color=#0000FF size=2>Pick options below</font></center></font size>"
+ "</td></tr></table>"
+ "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=000000>"
+ "<td width=90%>"
+ "<center><font color=#FFFFFF>Select the type of food to cook:<br /><br />"
+ "<select name='food'>"
+ "<option>Beef</option"
+ "<option>Shrimp</option"
+ "<option>Crayfish</option>"
+ "<option>Chicken</option>"
+ "<option>Anchovies</option>"
+ "<option>Sardine</option>"
+ "<option>Karambwan</option>"
+ "<option>Herring</option>"
+ "<option>Mackerel</option>"
+ "<option>Trout</option>"
+ "<option>Cod</option>"
+ "<option>Pike</option>"
+ "<option>Salmon</option>"
+ "<option>Tuna</option>"
+ "<option>Rainbow Fish</option>"
+ "<option>Lobster</option>"
+ "<option>Bass</option>"
+ "<option>Swordfish</option>"
+ "<option>Monkfish</option>"
+ "<option>Shark</option>"
+ "<option>Sea Turtle</option>"
+ "<option>Cave Fish</option>"
+ "<option>Manta Ray</option>"
+ "<option>Rocktail</option>"
+ "</select><br /><br />"
+ "Once you have chosen the food to cook; "
+ "Click the 'OK' button to begin the script"
+ "</td></tr></table></html>")
public class Al_kharidCooker extends Script implements MessageListener, PaintListener {

    RSTile[] walkRange = {new RSTile(3269, 3169),
        new RSTile(3276, 3175), new RSTile(3275, 3180)};
    RSTile[] walkBank = walk.reversePath(walkRange);
    RSTile[] toRange = walk.cleanPath(walk.fixPath(walkRange));
    RSTile[] toBank = walk.cleanPath(walk.fixPath(walkBank));
    private static final int RANGE = 25730;
    private static final int COOKANIM = 896;
    private int foodBurnt = 0;
    private int foodID = 0;
    private int foodXP;
    private String uncook;
    public String status = "";
    public int startXP = 0;
    public int startLvl = 0;
    public long startTime = System.currentTimeMillis();
    private BufferedImage img = null;
    private boolean setAltitude = true;

    @Override
    public boolean onStart(final Map<String, String> args) {
        try {
            final URL url = new URL("http://lazygamerz.org/client/skin/paint.png");
            img = ImageIO.read(url);
        } catch (final IOException e) {
        }
        if (args.get("food").equals("Shrimp")) {
            foodID = 317;
            foodXP = 30;
            uncook = "Raw Shrimps";
        } else if (args.get("food").equals("Beef")) {
            foodID = 2132;
            foodXP = 30;
            uncook = "Raw beef";
        } else if (args.get("food").equals("Crayfish")) {
            foodID = 13435;
            foodXP = 30;

            uncook = "Raw Crayfish";
        } else if (args.get("food").equals("Chicken")) {
            foodID = 2138;
            foodXP = 30;
            uncook = "Raw Chicken";
        } else if (args.get("food").equals("Anchovies")) {
            foodID = 321;
            foodXP = 30;
            uncook = "Raw Anchovies";
        } else if (args.get("food").equals("Sardine")) {
            foodID = 327;
            foodXP = 40;
            uncook = "Raw Sardine";
        } else if (args.get("food").equals("Karambwan")) {
            foodID = 3142;
            foodXP = 190;
            uncook = "Raw Karambwan";
        } else if (args.get("food").equals("Herring")) {
            foodID = 345;
            foodXP = 50;
            uncook = "Raw Herring";
        } else if (args.get("food").equals("Mackerel")) {
            foodID = 9978;
            foodXP = 60;
            uncook = "Raw Mackerel";
        } else if (args.get("food").equals("Trout")) {
            foodID = 335;
            foodXP = 70;
            uncook = "Raw Trout";
        } else if (args.get("food").equals("Cod")) {
            foodID = 341;
            foodXP = 75;
            uncook = "Raw Cod";
        } else if (args.get("food").equals("Pike")) {
            foodID = 349;
            foodXP = 80;
            uncook = "Raw Pike";
        } else if (args.get("food").equals("Salmon")) {
            foodID = 331;
            foodXP = 90;
            uncook = "Raw Salmon";
        } else if (args.get("food").equals("Tuna")) {
            foodID = 359;
            foodXP = 100;
            uncook = "Raw Tuna";
        } else if (args.get("food").equals("Rainbow Fish")) {
            foodID = 10138;
            foodXP = 110;
            uncook = "Raw Rainbow Fish";
        } else if (args.get("food").equals("Lobster")) {
            foodID = 377;
            foodXP = 120;
            uncook = "Raw Lobster";
        } else if (args.get("food").equals("Bass")) {
            foodID = 363;
            foodXP = 130;
            uncook = "Raw Bass";
        } else if (args.get("food").equals("Swordfish")) {
            foodID = 371;
            foodXP = 140;
            uncook = "Raw Swordfish";
        } else if (args.get("food").equals("Monkfish")) {
            foodID = 7944;
            foodXP = 150;
            uncook = "Raw Monkfish";
        } else if (args.get("food").equals("Shark")) {
            foodID = 383;
            foodXP = 210;
            uncook = "Raw Shark";
        } else if (args.get("food").equals("Sea Turtle")) {
            foodID = 395;
            foodXP = 211;
            uncook = "Raw Sea Turtle";
        } else if (args.get("food").equals("Cave Fish")) {
            foodID = 15264;
            foodXP = 214;
            uncook = "Raw Cave Fish";
        } else if (args.get("food").equals("Manta Ray")) {
            foodID = 389;
            foodXP = 216;
            uncook = "Raw Manta Ray";
        } else if (args.get("food").equals("Rocktail")) {
            foodID = 15270;
            foodXP = 225;
            uncook = "Raw Rocktail";
        }
        return true;
    }

    public boolean isCooking() {
        int i, j;
        j = 0;
        for (i = 1; i <= 10; i++) {
            if (player.getMine().getAnimation() == COOKANIM) {
                j = j + 1;
            }
            wait(random(25, 50));
        }
        if (j > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void Cook() {
        final RSObject range = objects.getNearestByID(RANGE);
        
        if (!isCooking()) {
            status = ("Cooking...");
            if (inventory.getCount(foodID) > 0) {
                if (!iface.get(916).isValid()) {
                    inventory.clickItem(foodID, "Use");
                    status = ("Clicking on Food");
                    wait(random(250, 350));
                    if (range.isOnScreen() && inventory.isItemSelected(foodID)) {
                        if (range.action(" Range"))  {
                        	iface.waitForOpen(iface.get(916), 3500);
                        }
                        
                        if (!iface.get(916).isValid()) {
                            wait(random(600, 800));
                            camera.setRotation(camera.getObjectAngle(range) + random(-45, 45));
                            return;
                        }
                    }
                } else if (iface.get(916).isValid()) {
                    wait(random(250, 400));
                    status = ("Cooking Food...");
                    if (iface.clickChild(905, 14, "Cook All"))  {
                    	iface.waitForChildClose(iface.getChild(905,14),4000);
                    }
                }
            }
        }
    }

    @Override
    protected int getMouseSpeed() {
        return random(5, 10);
    }

    public int loop() {
        if (!game.isLoggedIn()) {
            status = ("Logged in");
            return random(1000, 15000);
        }

        if (setAltitude) {
            camera.setAltitude(true);
            sleep(random(250, 500));
            setAltitude = false;
            return 50;
        }
        if (!isRunning()) {
            if (player.getMyEnergy() >= random(50, 100)) {
                game.setRun(true);
            }
        }
        if (!inventory.contains(foodID)) {
            if (bank.nearby() && !me.isMoving()) {
                if (bank.open())  {
                	useBank();
                }
                return random(1000, 1500);
            } else {
                if (walkToBank());
                status = ("Going to bank");
                return random(400, 500);
            }
        } 
        else {
            if (!me.isMoving() && nearRange()) {
                Cook();
            } else {
                if (walkToRange());
                status = ("Going to Range");
                return random(400, 500);
            }
        }

        return random(80,100);
    }


    public boolean nearRange() {
        final RSObject range = objects.getNearestByID(RANGE);
        return range != null && range.isOnScreen();
    }

    /**
     * Sets the {@link render}
     *
     * @param Repaint - a {@link render}
     */
    public void onRepaint(Graphics g) {
        //if logged in to the game start the paint
        if (game.isLoggedIn()) {
            final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
            int CurrentXP = skills.getCurrentXP(Constants.STAT_COOKING);
            int RealLvL = skills.getRealLvl(Constants.STAT_COOKING);
            int currentXP = skills.getCurrentXP(Constants.STAT_COOKING);
            int currentLVL = skills.getCurrentLvl(Constants.STAT_COOKING);
            int currentPurLVL = skills.getPercentToNextLvl(Constants.STAT_COOKING);
            int XPToNextLvL = skills.getXPToNextLvl(Constants.STAT_COOKING);
            final double XP = foodXP;//special skill define
                //sets up the paint visual run timer for the user
                //it is set up in 00 set up rather than typical 0
                long millis = System.currentTimeMillis() - startTime;
		String time = Timer.format(millis);
                if (startLvl == 0) {
                    startXP = CurrentXP;
                    startLvl = RealLvL;
                }
                //base number used for left alignment of text. I could just type 9 in all x spaces
                //but what fun would that be?
                final int x = 315;
                final int x1 = 400;

                int gainedXP = currentXP - startXP;
                final int done = (int) (gainedXP / XP);
                final int donePerHour = (int) ((gainedXP / XP) * 3600000.0 / (double) millis);
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
                g.drawString("Cooked: " + done, x, 410);
                g.drawString("PH: " + donePerHour + " Bnt: " + foodBurnt, x1, 410);
                g.drawString("Lvl: " + currentLVL, x, 426);
                g.drawString("Gained: " + gainedLVL, x1, 426);
                g.drawString("Xp: " + gainedXP, x, 442);
                g.drawString("PH: " + expPerHour, x1, 442);
                g.drawString("Xp To Next Level: " + XPToNextLvL, x, 458);
                g.drawString("Status: " + status, x, 474);

                //version define
                g.drawString("" + properties.version(), 494, 474);
                //percent of fill bar done
                g.drawString("" + currentPurLVL, 502, 355);

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


    public void messageReceived(final MessageEvent e) {
        final String word = e.getMessage().toLowerCase();
        if (word.contains("manage to burn") || word.contains("accidentally burn")) {
            foodBurnt++;
        }
    }

    public void useBank() {
        if (bank.isOpen()) {
            if (bank.depositAll());  {
            	inventory.waitForEmpty(3000);
            }
            
            if (bank.getCount(foodID) > 0) {
                if (inventory.getCount(foodID) == 0) {
                    status = ("Getting " + uncook);
                    if (bank.atItem(foodID, "Withdraw-All")) {
                        wait(random(300, 500));
                    }
                }
            } else {
                bank.close();
                wait(random(4000, 8000));
                status = ("Logging out");
                game.logout();
                stopScript();
            }
        }
    }

    private boolean walkToBank() {
        try {
        	RSTile dest = walk.getDestination();
        	
            if (dest!=null && dest.isOnMinimap() && dest.distanceTo() <= random(2, 4)) {
            }
            if (!walk.pathMM(toBank, 18, 2, 2)) {
                walk.to(walk.nextTile(toBank, 18, false));
            }
        } catch (final Exception ignored) {
            return false;
        }
        return true;
    }

    private boolean walkToRange() {
        try {
        	RSTile dest = walk.getDestination();
            if (dest!=null && dest.isOnMinimap() && dest.distanceTo() <= random(6, 8)) {
            }
            if (!walk.pathMM(toRange, 18, 1, 1)) {
                walk.to(walk.nextTile(toRange, 18, false));
            }
        } catch (final Exception ignored) {
            return false;
        }
        return true;
    }
}
