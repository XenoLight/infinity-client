import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Creative"}, category = "Fishing", name = "Burgh Shark Rapist", version = 1.0, description = "<html><body bgcolor = Black><font color = White><center><"
        + "<h2>" + "Burgh Shark Rapist" + " V 1.00</h2>"
        + "Author: " + "Creative"
        + "<br><br>Start at the Docks, or Bank."
        + "<br>Built-In AntiBan."
        + "<br>Informative Paint."
        + "<br>"
        + "<br>Enter number of Shark to Fish, or DON'T TOUCH IT.<br>"
        + "Amount to Catch: <input type=\"text\" name=\"numberof\" value=\"100000\"><br>")

public class BurghSharkRapist extends Script implements MessageListener, PaintListener {

    //ID'S
    public int waitForAnim = 5108;
    public int sharkid = 383;
    public static final int fishspotid = 14882;
    public int spot = 312;
    public static final int bankid = 49018;
    public final int[] harpoonid = {10129, 311};

    //Paint
    public final long startTime = System.currentTimeMillis();
    private String status = "";
    int sharkcaught;
    int levelsGained;
    final int sharkPrice = ge.loadItemInfo(383).getPrice();
    int expGained = 0;
    public int startexp;
    public int sharkAmount;
    private int RunningEnergy = random(15, 80);

    //OnStart
    BufferedImage normal = null;
    BufferedImage clicked = null;

    final RSTile[] BankToSpot = {new RSTile(3496, 3212), new RSTile(3497, 3206), new RSTile(3496, 3200), new RSTile(3495, 3195), new RSTile(3494, 3190), new RSTile(3490, 3185)};
    final RSTile[] SpotToBank = walk.reversePath(BankToSpot);

    //LOOP
    public int loop() {
        if (sharkcaught >= sharkAmount) {
            log("We're done Fishing.");
            stopScript();
        }
        camera.setAltitude(true);

        if (HaveToBank()) {
            if (bank.nearby()) {
                if (useBank())
                    return random(800, 1000);
            } else {
                if (flag(random(4, 7))) {

                    if (walkToBank())
                        return random(800, 1000);
                } else {
                    // no ready to walk
                    return random(800, 1000);
                }
            }
        } else {
            if (atFishspot()) {
                if (player.isIdle())
                    fishShark();
                return random(800, 1000);
            } else {
                if (flag(random(4, 7))) {
                    if (walkToSpot())
                        return random(800, 1000);
                } else {

                    return random(800, 1000);
                }
            }
        }
        return random(200, 400);

    }

    //Methods
    public boolean HaveToBank() {
        return inventory.isFull();
    }

    public boolean atBank() {
        RSObject bank = objects.getNearestByID(bankid);
        if (bank == null) return false;
        return tile.onScreen(bank.getLocation());
    }

    public boolean atFishspot() {
        RSObject spot = objects.getNearestByID(fishspotid);
        if (spot == null) return false;
        return tile.onScreen(spot.getLocation());
    }

    public boolean useBank() {
        status = "Depositing";
        antiBan();
        
        if (!bank.open())  {
        	return false;
        }
        
        return bank.depositAllExcept(harpoonid);
    }

    public int fishShark() {
        status = "Fishing Sharks...";
        RSNPC spot = npc.getNearestByID(3574);
        RSTile mloc = spot.getLocation();
        if (calculate.distanceTo(mloc) > 4) {
            walk.tileMM(mloc);
            wait(random(800, 1000));
        }
        if (spot != null && tile.onScreen(spot.getLocation())) {
            npc.click(spot, "Harpoon");
            wait(random(800, 1000));
            antiBan();
        } else {
            camera.turnTo(spot.getLocation());

        }
        return random(800, 1000);
    }

    public boolean walkToSpot() {
        status = "Walking To FishSpot...";
        antiBan();
        if (player.getMyEnergy() >= RunningEnergy && !isRunning()) {
            game.setRun(true);
            RunningEnergy = random(15, 30);
            wait(random(400, 500));
        }
        if (!player.getMine().isMoving() || calculate.distanceTo(walk.getDestination()) <= random(6, 8)) {
            walk.pathMM(walk.randomizePath(BankToSpot, 2, 2), 16);

            return false;
        }
        return true;
    }

    public boolean walkToBank() {
        status = "Walking To Bank...";
        antiBan();
        if (player.getMyEnergy() >= RunningEnergy && !isRunning()) {
            game.setRun(true);
            RunningEnergy = random(15, 30);
            wait(random(400, 500));
        }
        if (!player.getMine().isMoving() || calculate.distanceTo(walk.getDestination()) <= random(6, 8)) {
            walk.pathMM(walk.randomizePath(SpotToBank, 2, 2), 16);

            return false;
        }
        return true;
    }

    public boolean flag(int dist) {
        if (player.getMine().getAnimation() != -1) return false;
        if (calculate.distanceTo(walk.getDestination()) <= dist) return true;
        return false;
    }

    public void messageReceived(final MessageEvent arg0) {
        final String serverString = arg0.getMessage();
        if (serverString.contains("You catch a shark!")) {
            sharkcaught++;
        }
        if (serverString.contains("You've just advanced")) {
            levelsGained++;
            if (serverString.contains("quick reactions")) {
                expGained = expGained + 110;
                sharkcaught = sharkcaught + 2;
            }
        }
    }

    protected int getMouseSpeed() {
        return 7;
    }

    //CREDIT TO RAWR
    private void antiBan() {
        int randomNum = random(1, 30);
        int r = random(1, 35);
        if (randomNum == 6) {
            if (r == 1) {
                if (game.getCurrentTab() != Game.tabStats) {
                    game.openTab(Game.tabStats);
                    mouse.move(random(670, 690), random(400, 410));
                    wait(random(1000, 1500));
                }
            }
            if (r == 2) {
                game.openTab(random(1, 14));
                wait(random(5000, 10000));
            }
            if (r == 3) {
                int x = input.getX();
                int y = input.getY();
                mouse.move(x + random(-90, 90), y + random(-90, 90));
                wait(random(1000, 1500));
            }
            if (r == 4) {
                int x2 = input.getX();
                int y2 = input.getY();
                mouse.move(x2 + random(-90, 90), y2 + random(-90, 90));
                wait(random(1000, 1500));
            }
            if (r == 5) {
                int x3 = input.getX();
                int y3 = input.getY();
                mouse.move(x3 + random(-80, 80), y3 + random(-80, 80));
                wait(random(1000, 1500));
            }
            if (r == 6) {
                int x3 = input.getX();
                int y3 = input.getY();
                mouse.move(x3 + random(-100, 100), y3 + random(-100, 100));
                wait(random(1000, 1500));
            }
            if (r == 7) {
                int x3 = input.getX();
                int y3 = input.getY();
                mouse.move(x3 + random(-100, 100), y3 + random(-80, 80));
                wait(random(1000, 1500));
            }
            if (r == 8) {
                camera.setRotation(random(100, 360));
                wait(random(1000, 1500));
            }
            if (r == 9) {
                camera.setRotation(random(100, 360));
                wait(random(1000, 1500));
            }
            if (r == 10) {
                camera.setRotation(random(100, 360));
                wait(random(1000, 1500));
            }
        }
    }

    //Credits to purefocus for progress bar.
    public static void ProgBar(Graphics g, int posX, int posY, int width, int height, int Progress, Color color1, Color color2, Color text) {
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
        g.fillRoundRect(posX, posY, (Progress * width) / 100, height, 5, 12);
        g.setColor(new Color(c2[0] + 25, c2[1] + 25, c2[2] + 25, 150));
        g.fillRoundRect(posX, posY, (Progress * width) / 100, height / 2, 5, 12);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRoundRect(posX, posY, width, height, 5, 12);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height));
        g.setColor(text);
        g.drawString(Progress + "%", posX + (width / 6), posY + (height + height / 20));
    }


    //CREDIT TO FAMOUS
    public void onRepaint(final Graphics g) {
        long millis = System.currentTimeMillis() - startTime;
        int sharkPerHour = 0;
        int expGained = 0;
        int profit = sharkPrice * sharkcaught;
        if (startexp == 0) {
            startexp = skills.getCurrentXP(Constants.STAT_FISHING);
        }
        expGained = skills.getCurrentXP(Constants.STAT_FISHING) - startexp;
        if (sharkcaught > 0)
            sharkPerHour = (int) (sharkcaught * 3600 / (millis / 1000));

        final long hours = millis / (1000 * 60 * 60);
        millis -= hours * 1000 * 60 * 60;
        final long minutes = millis / (1000 * 60);
        millis -= minutes * 1000 * 60;
        final long seconds = millis / 1000;
        final int xx = 561;
        final int yy = 225;
        g.setColor(new Color(0, 0, 0, 175));
        g.fillRoundRect(555, 210, 175, 250, 10, 10);
        g.setColor(Color.black);
        g.drawString("Burgh Shark Rapist", xx + 1, yy + 1);
        g.setColor(Color.red);
        g.drawString("Burgh Shark Rapist", xx, yy);
        g.setColor(Color.white);
        g.drawString("Current Level: " + skills.getCurrentLvl(Constants.STAT_FISHING), 561, 255);
        g.drawString("Sharks Caught: " + Integer.toString(sharkcaught), 561, 275);
        g.drawString("Sharks/Hour:   " + sharkPerHour, 561, 295);
        g.drawString("Exp Gained:   " + expGained, 561, 315);
        g.drawString("Profit:   " + profit + " Gp", 561, 335);
        g.drawString("Levels Gained: " + Integer.toString(levelsGained), 561, 355);
        g.drawString("EXP Till Next Level: " + skills.getXPToNextLvl(Constants.STAT_FISHING), 561, 375);
        g.drawString("Time Running:", 561, 400);
        g.drawString(String.valueOf(hours) + ":" + minutes + ":" + seconds, 561, 415);
        g.drawString("Status: " + status, 561, 440);
        g.setColor(Color.red);
        final int percent = skills.getPercentToNextLvl(Constants.STAT_FISHING);
        ProgBar(g, 11, 323, 145, 11, percent, Color.black, Color.green, Color.white);

        if (normal != null) {
            final Mouse mouse = Bot.getClient().getMouse();
            final int mouse_x = mouse.getX();
            final int mouse_y = mouse.getY();
            final int mouse_x2 = mouse.getPressX();
            final int mouse_y2 = mouse.getPressY();
            final long mpt = System.currentTimeMillis()
                    - mouse.getPressTime();
            if (mouse.getPressTime() == -1 || mpt >= 1000) {
                g.drawImage(normal, mouse_x - 8, mouse_y - 8, null); //this show the mouse when its not clicked
            }
            if (mpt < 1000) {
                g.drawImage(clicked, mouse_x2 - 8, mouse_y2 - 8, null); //this show the four squares where you clicked.
                g.drawImage(normal, mouse_x - 8, mouse_y - 8, null); //this show the mouse as normal when its/just clicked
            }
        }
    }

    //OnStart
    public boolean onStart(Map<String, String> args) {
        sharkAmount = Integer.parseInt(args.get("numberof"));
        try {
            final URL cursorURL = new URL(
                    "http://i48.tinypic.com/313623n.png");
            final URL cursor80URL = new URL(
                    "http://i46.tinypic.com/9prjnt.png");
            normal = ImageIO.read(cursorURL);
            clicked = ImageIO.read(cursor80URL);
        } catch (MalformedURLException e) {
            log("Unable to buffer cursor.");
        } catch (IOException e) {
            log("Unable to open cursor image.");
        }
        return true;
    }


    //Finish
    public void onFinish() {
        log("Thanks for using my script - Creative.");
        log("Sharks Caught: " + sharkcaught + ".");

    }
}