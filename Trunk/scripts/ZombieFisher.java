/*
 * ZombieFisherEXTREME V7.53
 *
 * Credits:
 * BamBino/cronshaw1234/Zorlix - Updaters
 * Carmera Spin/Harpoon update - Lone Spartan
 * Welcome - Ruski
 * TBT and Aelin for scripting this.
 * The Immortal for letting me use his paint thingy :D
 * Infinity Development Team
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = {"ZombieKnight"},
category = "Fishing",
        name = "ZombieFisherEXTREME",
        version = 7.54,
        description = "<html><style type='text/css'>"
        + "body {background:url('http://lazygamerz.org/client/images/back_2.png') repeat}"
        + "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
        + "<body><center><b><font size='4' color='Blue'>ZombieFisherEXTREME v7.53</font></b><br></center>"
        + "<center><table border='0'><tr><td><center><font size='4'><b>:: Script Settings ::</b></font></center></td></tr>"
        + "<tr><td bgcolor=#C0C0C0><center><table border='0'><tr><td colspan='2'><center>"
        + "<p></p>"
        + "</center></td></tr>"
        + "<tr><td colspan='2'><hr></td></tr><tr><td><tr><td><b>Location: </b></td>"
        + "<td><center><select name=locationName>"
        + "<option>Al-Kharid"
        + "<option>Barb Village(EV)"
        + "<option>Catherby"
        + "<option>Draynor"
        + "<option>Fishing Guild"
        + "<option>Otto-Grotto"
        + "<option>[STILES]Karamja"
        + "<option>Shilo"
        + "<option>Piscatoris</select>"
        + "</center></td></tr>"
        + "<tr><td><b>Catch: </b></td>"
        + "<td><center><select name=catchName>"
        + "<option>Pike"
        + "<option>Bass/Cod/Mackerel"
        + "<option>Shrimp/Anchovies"
        + "<option>Herring/Sardines"
        + "<option>Leaping"
        + "<option>Trout/Salmon"
        + "<option>Tuna/Swordfish"
        + "<option>Tuna/Swordfish(CHARPOON)"
        + "<option>Lobsters"
        + "<option>Sharks"
        + "<option>Sharks(CHARPOON)"
        + "<option>Rainbow Fish"
        + "<option>Monkfish</select>"
        + "</center></td></tr>"
        + "<tr><td><b>Paint Color: </b></td><td>"
        + "<center><select name=pColor>"
        + "<option>PinkPanther"
        + "<option>SunKist"
        + "<option>ClearSky"
        + "<option>Monochrome"
        + "<option>Nightmare"
        + "<option>BloodShed</select>"
        + "</center></td></tr>"
        + "<tr><td><b>ZombieWalking:</b></td>"
        + "<td><center>"
        + "<input type=checkbox name=zombieWalking value=true size=20><B>Yes</b></center></td></tr>"
        + "<tr><td><b>AntiTunas:</b></td>"
        + "<td><center>"
        + "<input type=checkbox name=antiTunas value=true size20><B>Yes</b></center></td></tr>"
        + "<tr><td><b>Powerfishing Mode:</b></td>"
        + "<td><center><input type=checkbox name=powerFishing value=true><B>Yes</b></center></td></tr>"
        + "<tr><td><b>Wield Equipment/Barbarian Mode:</b></td>"
        + "<td><center><input type=checkbox name=barbarianMode value=true><B>Yes</b></center></td></tr>"
        + "<tr><td><b>Paint Report:</b></td>"
        + "<td><center><input type=checkbox name=usePaint checked=true value=true><B>Yes</b></center></td></tr>"
        + "</table></body></html>"
        )
public class ZombieFisher extends Script implements PaintListener {

    int randomInt;
    int GambleInt;

    @Override
    protected int getMouseSpeed() {
        if (powerFishing) {
            return random(8, 10);
        } else {
            return random(3, 5);
        }
    }
    // State constants:
    private enum State {S_WALKTO_BANK,  S_WALKTO_SPOT, S_FISH, S_TUNA, 
    	S_THROW_TUNAS, S_DROP_ALL, S_USE_BANK, S_DEPOSIT, S_WITHDRAW }
    
    // Bait constants:
    private final int BAIT_NONE = -1;
    private final int BAIT_BAIT = 313;
    private final int BAIT_FEATHERS = 314;
    private final int BAIT_STRIPY = 10087;
    // Gear constants:
    private final int GEAR_NET = 20891;
    private final int GEAR_ROD = 307;
    private final int GEAR_FLYROD = 309;
    private final int GEAR_HEAVYROD = 11323;
    private final int GEAR_CAGE = 301;
    private final int GEAR_CHARPOON = 14109;
    private final int GEAR_HARPOON = 311;
    private final int GEAR_BIGNET = 20892;
    private final int GEAR_NONE = -1;
    // Paths and tiles:
    private RSTile[] toBank;
    private RSTile[] toArea;
    // Runtime configuration.
    private int currentGear;
    private int currentBait;
    private String currentCommand;
    private int fishingSpotID;
    private int bankID;
    private int shopID;
    private boolean usesNPCBanking;
    // Script configuration.
    private String locationName;
    private String catchName;
    private String pColor;
    private boolean barbarianMode;
    private boolean powerFishing;
    private boolean antiTunas;
    private boolean zombieWalking;
    private boolean usePaint;
    private boolean Sound;
    // Misc variables.
    private int currentFails = 0;
    private int randomRunEnergy;
    private State state = State.S_FISH;
    private boolean runningFromCombat;
    private long scriptStartTime;
    private int playerStartXP;
    private int numberOfCatches;
    private long lastCheck;
    private long checkTime;
    private int countToNext = 0;
    private int timesAvoidedCombat;
    private int startLevel;
    private int lastExp;
    private int xpPerCatch = 0;
    private int oldExp;
    private int startExp;
    private boolean StartedY;
    int[] equipItems = {10129, 14109};
    int[] itemIDs = {10129, 14109};

    private void turnCamera() {
        char[] LR = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
        char[] UD = new char[]{KeyEvent.VK_DOWN, KeyEvent.VK_UP};
        char[] LRUD = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
            KeyEvent.VK_UP, KeyEvent.VK_DOWN};
        int random2 = random(0, 4);
        int random1 = random(0, 4);
        int random4 = random(0, 4);

        if (random(0, 2) == 2) {
        	keyboard.pressKey(LR[random1]);
            try {
                Thread.sleep(random(100, 400));
            } catch (Exception e) {
            }
            keyboard.pressKey(UD[random2]);
            try {
                Thread.sleep(random(300, 600));
            } catch (Exception e) {
            }
            keyboard.releaseKey(UD[random2]);
            try {
                Thread.sleep(random(100, 400));
            } catch (Exception e) {
            }
            keyboard.releaseKey(LR[random1]);
        } else {
            keyboard.pressKey(LRUD[random4]);
            if (random4 > 1) {
                try {
                    Thread.sleep(random(300, 600));
                } catch (Exception e) {
                }
            } else {
                try {
                    Thread.sleep(random(500, 900));
                } catch (Exception e) {
                }
            }
            keyboard.releaseKey(LRUD[random4]);
        }
    }

    /*
     * Pre-runtime configuration takes place within this method.
     */
    public boolean onStart(final Map<String, String> args) {
        // Set script start time
        scriptStartTime = System.currentTimeMillis();
        // Load script configuration from arguments.
        locationName = args.get("locationName");
        catchName = args.get("catchName");
        pColor = args.get("pColor");
        barbarianMode = args.get("barbarianMode") != null ? true : false;
        powerFishing = args.get("powerFishing") != null ? true : false;
        antiTunas = args.get("antiTunas") != null ? true : false;
        usePaint = args.get("usePaint") != null ? true : false;
        Sound = args.get("wSound") != null ? true : false;
        zombieWalking = args.get("zombieWalking") != null ? true : false;

        if (inventory.isFull())  {
        	state = State.S_WALKTO_BANK;
        }
        else  {
        	state = State.S_WALKTO_SPOT;
        }
        
        // Al Kharid locations:
        if (locationName.equals("Al-Kharid")) {
            toBank = new RSTile[]{new RSTile(3271, 3144), new RSTile(3276, 3157), new RSTile(3270, 3167)};
            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;

            if (catchName.equals("Shrimp/Anchovies")) {
                currentGear = GEAR_NET;
                currentBait = BAIT_NONE;
                fishingSpotID = 330;
                currentCommand = "Net ";
                bankID = 35647;
                return true;
            }

            if (catchName.equals("Herring/Sardines")) {
                currentGear = GEAR_ROD;
                currentBait = BAIT_BAIT;
                fishingSpotID = 330;
                currentCommand = "Bait ";
                bankID = 35647;
                return true;
            }
        }


        if (locationName.equals("Otto-Grotto")) {
            toBank = new RSTile[]{new RSTile(3101, 3432), new RSTile(3097, 3438), new RSTile(3091, 3444), new RSTile(3090, 3455), new RSTile(3087, 3463), new RSTile(3081, 3467), new RSTile(3079, 3476), new RSTile(3080, 3483), new RSTile(3085, 3488), new RSTile(3093, 3490)};
            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;


            if (catchName.equals("Leaping")) {
                currentGear = GEAR_HEAVYROD;
                currentBait = BAIT_FEATHERS;
                fishingSpotID = 2722;
                currentCommand = "Use-rod ";
                bankID = 26972;
                return true;
            }

        }

        // Barbarian Village locations:
        if (locationName.equals("Barb Village(EV)")) {
            toBank = new RSTile[]{new RSTile(3101, 3432), new RSTile(3097, 3438), new RSTile(3091, 3444), new RSTile(3090, 3455), new RSTile(3087, 3463), new RSTile(3081, 3467), new RSTile(3079, 3476), new RSTile(3080, 3483), new RSTile(3085, 3488), new RSTile(3092, 3490)};
            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;


            if (catchName.equals("Pike")) {
                currentGear = GEAR_ROD;
                currentBait = BAIT_BAIT;
                fishingSpotID = 328;
                currentCommand = "Bait ";
                bankID = 26972;
                return true;
            }

            if (catchName.equals("Trout/Salmon")) {
                currentGear = GEAR_FLYROD;
                currentBait = BAIT_FEATHERS;
                fishingSpotID = 328;
                currentCommand = "Lure ";
                bankID = 26972;
                return true;
            }

            if (catchName.equals("Rainbow Fish")) {
                currentGear = GEAR_FLYROD;
                currentBait = BAIT_STRIPY;
                fishingSpotID = 328;
                currentCommand = "Lure ";
                bankID = 26972;
                return true;
            }
        }

        // Catherby locations:
        if (locationName.equals("Catherby")) {
            toBank = new RSTile[]{new RSTile(2852, 3430), new RSTile(2839, 3434), new RSTile(2830, 3437), new RSTile(2820, 3438), new RSTile(2809, 3440)};
            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;


            if (catchName.equals("Bass/Cod/Mackerel")) {
                currentGear = GEAR_BIGNET;
                currentBait = BAIT_NONE;
                fishingSpotID = 322;
                currentCommand = "Net ";
                bankID = 2213;
                return true;
            }

            if (catchName.equals("Shrimp/Anchovies")) {
                currentGear = GEAR_NET;
                currentBait = BAIT_NONE;
                fishingSpotID = 320;
                currentCommand = "Net ";
                bankID = 2213;
                return true;
            }

            if (catchName.equals("Herring/Sardines")) {
                currentGear = GEAR_ROD;
                currentBait = BAIT_BAIT;
                fishingSpotID = 320;
                currentCommand = "Bait ";
                bankID = 2213;
                return true;
            }

            if (catchName.equals("Lobsters")) {
                currentGear = GEAR_CAGE;
                currentBait = BAIT_NONE;
                fishingSpotID = 321;
                currentCommand = "Cage ";
                bankID = 2213;
                return true;
            }

            if (catchName.equals("Tuna/Swordfish")) {
                currentGear = GEAR_HARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 321;
                currentCommand = "Harpoon ";
                bankID = 2213;
                return true;
            }


            if (catchName.equals("Tuna/Swordfish(CHARPOON")) {
                currentGear = GEAR_CHARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 321;
                currentCommand = "Harpoon ";
                bankID = 2213;
                return true;
            }

            if (catchName.equals("Sharks")) {
                currentGear = GEAR_HARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 322;
                currentCommand = "Harpoon ";
                bankID = 2213;
                return true;
            }


            if (catchName.equals("Sharks(CHARPOON")) {
                currentGear = GEAR_CHARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 322;
                currentCommand = "Harpoon ";
                bankID = 2213;
                return true;
            }

        }


        // Draynor locations.
        if (locationName.equals("Draynor")) {
            // Setup draynor paths.
            toBank = new RSTile[]{new RSTile(3086, 3232), new RSTile(3093, 3242)};
            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;

            if (catchName.equals("Shrimp/Anchovies")) {
                currentGear = GEAR_NET;
                currentBait = BAIT_NONE;
                fishingSpotID = 327;
                currentCommand = "Net ";
                bankID = 2213;
                return true;
            }

            if (catchName.equals("Herring/Sardines")) {
                currentGear = GEAR_ROD;
                currentBait = BAIT_BAIT;
                fishingSpotID = 327;
                currentCommand = "Bait ";
                bankID = 2213;
                return true;
            }
        }

        if (locationName.equals("Fishing Guild")) {
            toBank = new RSTile[]{new RSTile(2595, 3416), new RSTile(2586, 3422)};
            toArea = new RSTile[]{new RSTile(2597, 3420)};
            usesNPCBanking = true;

            if (catchName.equals("Bass/Cod/Mackerel")) {
                currentGear = GEAR_BIGNET;
                currentBait = BAIT_NONE;
                fishingSpotID = 313;
                currentCommand = "Net ";
                bankID = 49018;
                return true;
            }
            if (catchName.equals("Lobsters")) {
                currentGear = GEAR_CAGE;
                currentBait = BAIT_NONE;
                fishingSpotID = 312;
                currentCommand = "Cage ";
                bankID = 49018;
                return true;
            }

            if (catchName.equals("Tuna/Swordfish")) {
                currentGear = GEAR_HARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 312;
                currentCommand = "Harpoon ";
                bankID = 49018;
                return true;
            }

            if (catchName.equals("Tuna/Swordfish(CHARPOON)")) {
                currentGear = GEAR_CHARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 312;
                currentCommand = "Harpoon ";
                bankID = 49018;
                return true;
            }
            if (catchName.equals("Sharks")) {
                currentGear = GEAR_HARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 313;
                currentCommand = "Harpoon ";
                bankID = 49018;
                return true;
            }

            if (catchName.equals("Sharks(CHARPOON)")) {
                currentGear = GEAR_CHARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 313;
                currentCommand = "Harpoon ";
                bankID = 49018;
                return true;
            }
        }

        if (locationName.equals("[STILES]Karamja")) {
            toBank = new RSTile[]{new RSTile(2913, 3171), new RSTile(2901, 3169), new RSTile(2895, 3162), new RSTile(2885, 3158), new RSTile(2875, 3150), new RSTile(2867, 3149), new RSTile(2860, 3146), new RSTile(2852, 3143)};


            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;


            if (catchName.equals("Lobsters")) {
                currentGear = GEAR_CAGE;
                currentBait = BAIT_NONE;
                fishingSpotID = 324;
                currentCommand = "Cage ";
                bankID = 11267;
                return true;
            }

            if (catchName.equals("Tuna/Swordfish")) {
                currentGear = GEAR_HARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 324;
                currentCommand = "Harpoon ";
                bankID = 11267;
                return true;
            }

        }


        if (locationName.equals("Karamja")) {
            toBank = new RSTile[]{new RSTile(2925, 3177), new RSTile(2924, 3166), new RSTile(2929, 3152),
                        new RSTile(2942, 3146),
                        /*new RSTile(2954, 3146), new RSTile(3032, 3217),*/
                        new RSTile(3029, 3217), new RSTile(3027, 3222), new RSTile(3027, 3230),
                        new RSTile(3041, 3238), new RSTile(3051, 3246), new RSTile(3068, 3248),
                        new RSTile(3080, 3250), new RSTile(3092, 3243)};//PortSarim


            toArea = walk.reversePath(toBank);
            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;

            if (catchName.equals("Shrimp/Anchovies")) {
                currentGear = GEAR_NET;
                currentBait = BAIT_NONE;
                fishingSpotID = 323;
                currentCommand = "Net ";
                bankID = 495;
                return true;
            }

            if (catchName.equals("Herring/Sardines")) {
                currentGear = GEAR_ROD;
                currentBait = BAIT_BAIT;
                fishingSpotID = 323;
                currentCommand = "Bait ";
                bankID = 495;
                return true;
            }

            if (catchName.equals("Lobsters")) {
                currentGear = GEAR_CAGE;
                currentBait = BAIT_NONE;
                fishingSpotID = 324;
                currentCommand = "Cage ";
                bankID = 495;
                return true;
            }

            if (catchName.equals("Tuna/Swordfish")) {
                currentGear = GEAR_HARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 324;
                currentCommand = "Harpoon ";
                bankID = 495;
                return true;
            }

        }


        if (locationName.equals("Shilo")) {
            toBank = new RSTile[]{new RSTile(2864, 2971),
                        new RSTile(2850, 2967), new RSTile(2852, 2953)};

            toArea = walk.reversePath(toBank);
            usesNPCBanking = true;

            if (catchName.equals("Trout/Salmon")) {
                currentGear = GEAR_FLYROD;
                currentBait = BAIT_FEATHERS;
                fishingSpotID = 317;
                currentCommand = "Lure ";
                bankID = 499;
                return true;
            }

            if (catchName.equals("Pike")) {
                currentGear = GEAR_ROD;
                currentBait = BAIT_BAIT;
                fishingSpotID = 317;
                currentCommand = "Bait ";
                bankID = 499;
                return true;
            }
        }

        if (locationName.equals("Piscatoris")) {
            toBank = new RSTile[]{new RSTile(2339, 3697), new RSTile(2322, 3696), new RSTile(2331, 3689)};
            toArea = new RSTile[]{new RSTile(2339, 3697)};
            usesNPCBanking = true;

            if (catchName.equals("Monkfish")) {
                currentGear = GEAR_NET;
                currentBait = BAIT_NONE;
                fishingSpotID = 3848;
                currentCommand = "Net ";
                bankID = 3824;
                return true;
            }

            if (catchName.equals("Tuna/Swordfish")) {
                currentGear = GEAR_HARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 3848;
                currentCommand = "Harpoon ";
                bankID = 3824;
                return true;
            }
            if (catchName.equals("Tuna/Swordfish(BARB)")) {
                currentGear = GEAR_NONE;
                currentBait = BAIT_NONE;
                fishingSpotID = 3848;
                currentCommand = "Harpoon ";
                bankID = 3824;
                return true;
            }
            if (catchName.equals("Tuna/Swordfish(CHARPOON)")) {
                currentGear = GEAR_CHARPOON;
                currentBait = BAIT_NONE;
                fishingSpotID = 3848;
                currentCommand = "Harpoon ";
                bankID = 3824;
                return true;
            }
        }
        log("Unable to start script: Invalid combination of parameters.");
        return false;
    }

    public void onFinish() {
        // Takes a screen shot when u stop the script.
        ScreenshotUtil.takeScreenshot(bot, true);
    }
    final ScriptManifest props = getClass().getAnnotation(
            ScriptManifest.class);

    public void Reset() {
        StartedY = false;
        currentFails = 0;
    }


    public int loop() {

        randomInt = random(1, 17);
        GambleInt = random(1, 17);
        if (GambleInt == 1) {
            turnCamera();
        }

        if (!game.isLoggedIn() || isWelcomeButton() || !StartedY) {
            StartedY = true;
            wait(1000);
            return random(250, 500);
        }
        if (barbarianMode && currentGear != GEAR_NONE) {
        }
        if (barbarianMode && currentGear != GEAR_NONE) {
        }

        if (currentFails >= 1000) {
            ScreenshotUtil.takeScreenshot(bot, game.isLoggedIn());
            if (checkForLogout()) {
                stopScript();
            }
        }


        debug("State="+state);
        switch (state) {
            case S_WALKTO_BANK:

                if (zombieWalking) {
                    return zombieWalkBank();
                } else {
                    return walkToBank();
                }

            case S_THROW_TUNAS:
                return throwTunas();

            case S_WALKTO_SPOT:

                if (zombieWalking) {
                    return zombieWalkSpots();
                } else {
                    return walkToSpots();
                }

            case S_FISH:
                if (antiTunas) {
                    return stateTuna();
                } else {
                    return stateFish();
                }

            case S_DROP_ALL:
                return dropAll();

            case S_USE_BANK:
                if (usesNPCBanking) {
                    if (locationName.equals("[STILES]Karamja")) {
                        return tradeAllKaramja();
                    } else if (locationName.equals("Piscatoris")) {
                        return useBankNPCPiscatoris();
                    } else if (locationName.equals("Shilo")) {
                        return useBankNPCShilo();
                    } else {
                        return useBank();
                    }
                } else {
                    return useBankNPC();
                }

            case S_DEPOSIT:

                if (locationName.equals("Karamja")) {
                    return depositAllKaramja();
                } else {
                    return depositAllRest();
                }

            case S_WITHDRAW:
                stopScript();
                ScreenshotUtil.takeScreenshot(bot, game.isLoggedIn());
        }

        return random(500, 1000);
    }

    int antiBan() {
        int GambleInt = random(1, 6);
        switch (GambleInt) {
            case 1:
                wait(random(2000, 2500));
                break;
            case 2:
                if (random(1, 4) == 1) {
                    int x = random(0, 750);
                    int y = random(0, 500);
                    mouse.move(0, 0, x, y);

                }
                return random(1300, 1600);
            case 3:
                // Is the current tab the inventory?
                if (game.getCurrentTab() != TAB_INVENTORY) {
                    // No, so switch to the inventory tab.
                    game.openTab(TAB_INVENTORY);
                    return random(500, 750);
                } else {
                    // No, so return
                    return random(500, 750);
                }
            case 4:
                // If the player is moving, then abort.
                if (me.isMoving()) {
                    return random(750, 1000);
                }

                if (System.currentTimeMillis() - lastCheck >= checkTime) {
                    lastCheck = System.currentTimeMillis();
                    checkTime = random(60000, 180000);

                    if (game.getCurrentTab() != TAB_STATS) {
                        game.openTab(TAB_STATS);
                    }
                    mouse.move(693, 273, 28, 10);
                    return random(2000, 4000);
                }


            case 5:
                if (random(1, 8) == 2) {
                    int angle = camera.getAngle() + random(-90, 90);
                    if (angle < 0) {
                        angle = 0;
                    }
                    if (angle > 359) {
                        angle = 0;

                    }
                    camera.setRotation(angle);
                }
                return random(500, 750);
        }
        return random(500, 1000);
    }

    public int useShopNPCKaramja() {
        RSNPC Shopkeeper = npc.getNearestByID(shopID);

        if (me.isMoving()) {
            return random(250, 500);
        }

        if (runningFromCombat) {
            state = State.S_WALKTO_SPOT;
            return random(500, 750);
        }

        if (iface.get(620).isValid()) {
            state = State.S_DEPOSIT;
            return 500;
        }

        if (Shopkeeper != null) {
            if (Shopkeeper.action("trade")) {
                state = State.S_DEPOSIT;
                currentFails = 0;
                return random(500, 1000);
            } else {
                currentFails++;
                return random(500, 750);
            }
        }
        return random(500, 1000);
    }

    //TODO: We should consider testing a version of this that simply uses bank.open().
    // If it doesn't work we should adjust bank.open() so it DOES work.
    public int useBankNPCShilo() {
        RSNPC banker = npc.getNearestByID(bankID);

        if (me.isMoving()) {
            return random(250, 500);
        }

        if (runningFromCombat) {
            state = State.S_WALKTO_SPOT;
            return random(500, 750);
        }

        if (bank.isOpen()) {
            state = State.S_DEPOSIT;
            return 500;
        }

        if (banker != null) {
            if (banker.action("bank banker")) {
                state = State.S_DEPOSIT;
                currentFails = 0;
                return random(500, 1000);
            } else {
                currentFails++;
                return random(500, 750);
            }
        } else {
            currentFails++;
            return random(500, 750);
        }

    }

    public int useBankNPCPiscatoris() {
        RSNPC banker = npc.getNearestByID(bankID);

        if (me.isMoving()) {
            return random(250, 500);
        }

        if (runningFromCombat) {
            state = State.S_WALKTO_SPOT;
            return random(500, 750);
        }

        if (bank.isOpen()) {
            state = State.S_DEPOSIT;
            return 500;
        }

        if (banker != null) {
            if (banker.action("bank")) {
                state = State.S_DEPOSIT;
                currentFails = 0;
                return random(500, 1000);
            } else {
                currentFails++;
                return random(500, 750);
            }
        } else {
            currentFails++;
            return random(500, 750);
        }

    }

    public int useBankNPC() {
        RSNPC banker = npc.getNearestByID(bankID);

        if (me.isMoving()) {
            return random(250, 500);
        }

        if (runningFromCombat) {
            state = State.S_WALKTO_SPOT;
            return random(500, 750);
        }

        if (bank.isOpen()) {
            state = State.S_DEPOSIT;
            return 500;
        }

        if (banker != null) {
            if (banker.action("bank banker")) {
                state = State.S_DEPOSIT;
                currentFails = 0;
                return random(500, 1000);
            } else {
                currentFails++;
                return random(500, 750);
            }
        } else {
            currentFails++;
            return random(500, 750);
        }

    }

    private int useBank() {
        if (me.isMoving()) {
            return random(250, 500);
        }

        if (runningFromCombat) {
            state = State.S_WALKTO_SPOT;
            return random(500, 750);
        }

        if (bank.isOpen()) {
            state = State.S_DEPOSIT;
            return 500;
        }
        else  {
        	if (!bank.open())  {
        		currentFails++;
        	}
        	else  {
                state = State.S_DEPOSIT;
                currentFails = 0;
        	}
        }

        return random(500, 750);
    }

    private int walkToSpots() {
        if (takeBoatToKaramja()) {
            return random(2000, 2500);
        }

        if (randomRunEnergy <= player.getMyEnergy() && !isRunning()) {
            game.setRun(true);
            randomRunEnergy = random(20, 60);
            return random(750, 1000);
        }

        if (toArea[toArea.length - 1].distanceTo() <= 3) {
            state = State.S_FISH;
            currentFails = 0;
            return random(250, 750);
        }

        RSTile dest = walk.getDestination();
        if (!me.isMoving() || 
        	(dest != null && dest.distanceTo() < 7)) {
            walk.pathMM(walk.randomizePath(toArea, 2, 2), 15);
        }
        return random(50, 150);
    }

    private int walkToBank() {
        if (takeBoatFromKaramja()) {
            return random(2000, 2500);
        }

        // TODO: bug fix.
      /*if ( calculate.distanceTo(new RSTile(2953, 3147)) <= 4 )
        return random(250, 500);*/

        if (randomRunEnergy <= player.getMyEnergy() && !isRunning()) {
            game.setRun(true);
            randomRunEnergy = random(20, 60);
            return random(750, 1000);
        }

        if (runningFromCombat && !me.isInCombat()) {
            state = State.S_WALKTO_SPOT;
            runningFromCombat = false;
            return random(250, 750);
        }

        if (calculate.distanceTo(toBank[toBank.length - 1]) <= 4) {

            state = State.S_USE_BANK;
            return random(750, 1500);
        }

        RSTile dest = walk.getDestination();
        if (!me.isMoving() || (dest != null && dest.distanceTo() < 7)) {
            walk.pathMM(walk.randomizePath(toBank, 2, 2), 15);
        }

        return random(50, 150);
    }

    private int zombieWalkSpots() {
        if (takeBoatToKaramja()) {
            return random(2000, 2500);
        }

        if (randomRunEnergy <= player.getMyEnergy() && !isRunning()) {
            game.setRun(true);
            randomRunEnergy = random(20, 60);
            return random(750, 1000);
        }

        if (calculate.distanceTo(toArea[toArea.length - 1]) <= 3) {
        	if (!inventory.isFull())  {
        		state = State.S_FISH;
        	}
        	else  {
        		state = State.S_WALKTO_BANK;
        	}
        }
        else  {
        	state = State.S_WALKTO_SPOT;
        }

        RSTile dest = walk.getDestination();
        if (!me.isMoving() || (dest != null && dest.distanceTo() > 4)) {
            walk.pathMM(walk.randomizePath(toArea, 2, 2), 15);
        }

        currentFails = 0;
        return random(50, 150);
    }

    private int zombieWalkBank() {
        if (takeBoatFromKaramja()) {
            return random(2000, 2500);
        }

        // TODO: bug fix.
      /*if ( calculate.distanceTo(new RSTile(2953, 3147)) <= 4 )
        return random(250, 500);*/

        if (randomRunEnergy <= player.getMyEnergy() && !isRunning()) {
            game.setRun(true);
            randomRunEnergy = random(20, 60);
            return random(750, 1000);
        }

        if (runningFromCombat && !me.isInCombat()) {
            state = State.S_WALKTO_SPOT;
            runningFromCombat = false;
            return random(250, 750);
        }

        if (calculate.distanceTo(toBank[toBank.length - 1]) <= 4) {

            state = State.S_USE_BANK;
            return random(750, 1500);
        }

        RSTile dest = walk.getDestination();
        if (!me.isMoving() || (dest != null && dest.distanceTo() > 4)) {
            walk.pathMM(walk.randomizePath(toBank, 2, 2), 15);
        }

        return random(50, 150);
    }

    private int dropAll() {
        // Make an array of items to keep.
        int[] thingsToKeep = new int[]{currentGear, currentBait, 995};

        // Drop all, twice to make sure nothing is missed.
        inventory.dropAllExcept(thingsToKeep);

        state = State.S_FISH;

        return random(500, 750);
    }

    private int throwTunas() {
        // Make an array of items to keep.
        int[] thingsToKeep = new int[]{currentGear, currentBait, 995, 331, 335, 317, 321, 377, 371, 372, 383, 14664, 7944, 363, 341, 353, 327, 345, 349};

        // Drop all, twice to make sure nothing is missed.
        inventory.dropAllExcept(thingsToKeep);

        state = State.S_FISH;

        return random(500, 750);
    }

    private int tradeAllKaramja() {
        RSNPC stiles = npc.getNearestByID(11267);
        if (me.isMoving()) {
            return random(250, 500);
        }

        if (runningFromCombat) {
            state = State.S_WALKTO_SPOT;
            return random(500, 750);
        }


        if (stiles != null) {
            if (stiles.action("exchange")) {
                wait(random(500, 2500));
                state = State.S_WALKTO_SPOT;

                if (toArea[toArea.length - 1].distanceTo() <= 3) {
                    state = State.S_FISH;
                    currentFails = 0;
                    return random(250, 750);
                }
                currentFails = 0;
                return random(500, 1000);
            } else {
                currentFails++;
                return random(500, 750);
            }
        } else {
            currentFails++;
            return random(500, 750);
        }

    }

    private int depositAllKaramja() {

        // Make an array of items to keep.
        int[] thingsToKeep = new int[]{currentGear, currentBait, 995};

        if (!bank.isOpen()) {
            state = State.S_USE_BANK;
            return 500;
        }
        // Deposit all.
        bank.depositAllExcept(thingsToKeep);

        // Only switch states if thet deposit was successfull.
        if (!inventory.isFull()) {
            state = State.S_WALKTO_SPOT;
        } else {
            state = State.S_USE_BANK;
        }

        return random(500, 750);
    }

    private int depositAllRest() {

        // Make an array of items to keep.
        int[] thingsToKeep = new int[]{currentGear, currentBait};

        if (!bank.isOpen()) {
            state = State.S_USE_BANK;
            return 500;
        }
        // Deposit all.
        bank.depositAllExcept(thingsToKeep);

        // Only switch states if thet deposit was successfull.
        if (!inventory.isFull()) {
            state = State.S_WALKTO_SPOT;
        } else {
            state = State.S_USE_BANK;
        }

        return random(500, 750);
    }

    private int stateFish() {
        // Find fishing spot.
        RSNPC fishingSpot = npc.getNearestByID(fishingSpotID);
        //
        if (currentBait != BAIT_NONE && inventory.getCount(currentBait) == 0) {
            currentFails += 5;
            return random(250, 500);
        }

        //

        if (currentGear != GEAR_NONE && inventory.getCount(currentGear) == 0 && !barbarianMode) {
            currentFails += 5;
            return random(250, 500);
        }

        // Is the player currently in combat?
        if (me.isInCombat()) {
            runningFromCombat = true;
            state = State.S_WALKTO_BANK;
            timesAvoidedCombat++;
            return random(250, 500);
        }

        // Is the player's inventory full?
        if (inventory.isFull()) {
            game.openTab(TAB_INVENTORY);
            state = (powerFishing == true) ? State.S_DROP_ALL : State.S_WALKTO_BANK;
            return random(250, 500);

        }


        // Is the player current busy? If so, do antiban.
        if (me.getAnimation() != -1
                || me.isMoving()) {
            return antiBan();
        }


        if (fishingSpot == null) {
            if (calculate.distanceTo(toBank[toBank.length - 1]) <= 3) {
                state = State.S_WALKTO_SPOT;
                return random(750, 1500);
            }

            currentFails++;
            return random(250, 500);
        } else {
            if (fishingSpot.isOnScreen()) {
                if (!fishingSpot.action(currentCommand)) {
                    camera.setRotation(random(1, 359));
                }
                currentFails = 0;
                return random(2150, 2350);
            } else {
                RSTile destination = walk.randomizeTile(fishingSpot.getLocation(), 2, 2);
                walk.tileMM(destination);
                return random(500, 1000);
            }
        }
    }

    private int stateTuna() {
// Find fishing spot.
        RSNPC fishingSpot = npc.getNearestByID(fishingSpotID);
        //
        if (currentBait != BAIT_NONE && inventory.getCount(currentBait) == 0) {
            currentFails += 5;
            return random(250, 500);
        }

        if (currentGear != GEAR_NONE && inventory.getCount(currentGear) == 0 && !barbarianMode) {
            currentFails += 5;
            return random(250, 500);
        }

        // Is the player currently in combat?
        if (me.isInCombat()) {
            runningFromCombat = true;
            state = State.S_WALKTO_BANK;
            timesAvoidedCombat++;
            return random(250, 500);
        }

        // Is the player's inventory full?

        // Is the player's inventory full?
        if (!inventory.isFull()) {
            state = State.S_THROW_TUNAS;
        } else {
            state = State.S_WALKTO_BANK;
            return random(250, 500);
        }


        // Is the player current busy? If so, do antiban.
        if (me.getAnimation() != -1
                || me.isMoving()) {
            return antiBan();
        }


        if (fishingSpot == null) {
            if (toBank[toBank.length - 1].distanceTo() <= 3) {
                state = State.S_WALKTO_SPOT;
                return random(750, 1500);
            }

            currentFails++;
            return random(250, 500);
        } else {
            if (fishingSpot.isOnScreen()) {
                if (!fishingSpot.action(currentCommand)) {
                    camera.setRotation(random(1, 359));
                }
                currentFails = 0;
                return random(1500, 1700);
            } else {
                RSTile destination = walk.randomizeTile(fishingSpot.getLocation(), 2, 2);
                walk.tileMM(destination);
                return random(500, 1000);
            }
        }
    }

    private boolean takeBoatFromKaramja() {
        RSNPC customsOfficer = npc.getNearestByID(380);
        @SuppressWarnings("unused")
        RSObject plank = objects.getNearestByID(242);
        RSTile location = new RSTile(3031, 3217);


        if (!locationName.equals("Karamja")) {
            return false;
        }

        if (inventory.getCount(995) < 30) {
        }

        if (location.distanceTo() <= 20 && !me.getLocation().equals(new RSTile(3029, 3217))) {
            if (location.isOnScreen()) {
            	//TODO: We need to add action methods similar to those for RSNPC and RSObject to the
            	// RSTile class and get rid of tile.click.  It's not better than atTile.
                tile.click(location, "Cross");
                return true;
            }
        }

        if (iface.get(228).isValid()) {
        	iface.getChild(228,2).click();
            return true;
        }
        if (iface.get(242).isValid()) {
        	iface.getChild(242,6).click();
            return true;
        }

        if (iface.get(230).isValid()) {
        	iface.getChild(230,3).click();
            return true;
        }

        if (iface.get(241).isValid()) {
        	iface.getChild(241,5).click();
            return true;
        }

        if (iface.get(64).isValid()) {
        	iface.getChild(64,5).click();
            return true;
        }

        if (iface.get(228).isValid()) {
        	iface.getChild(228,2).click();
            return false;
        }

        if (iface.get(241).isValid()) {
        	iface.getChild(241,5).click();
            return false;
        }

        if (customsOfficer != null) {
            if (customsOfficer.isOnScreen()) {
                customsOfficer.action("Pay-Fare");
                return true;
            } else {
                walk.tileMM(walk.randomizeTile(customsOfficer.getLocation(), 2, 2));
                return true;
            }

        }

        return false;
    }

    private boolean takeBoatToKaramja() {
        int[] seamanIDs = new int[]{376, 377, 378}; // Pay-fare
        RSNPC seaman = npc.getNearestByID(seamanIDs);
        RSObject plank = objects.getNearestByID(2082);

        if (!locationName.equals("Karamja")) {
            return false;
        }

        if (inventory.getCount(995) < 30) {
        }

        if (plank != null) {
            plank.action("Cross");
            return true;
        }

        if (iface.get(64).isValid()) {
        	iface.getChild(64,5).click();
            return true;
        }

        if (iface.get(228).isValid()) {
        	iface.getChild(228,2).click();
            return true;
        }

        if (iface.get(241).isValid()) {
        	iface.getChild(241,5).click();
            return true;
        }
        if (seaman != null) {
            if (seaman.isOnScreen()) {
                seaman.action("Pay-fare");
                return true;
            } else {
                walk.tileMM(walk.randomizeTile(seaman.getLocation(), 2, 2));
                return true;
            }
        }

        return false;
    }

    private boolean checkForLogout() {
        for (int failed = 0; failed < 3; failed++) {
            game.logout();
            beep(3);
            wait(500);
            if (!game.isLoggedIn()) {
                return true;
            }
        }
        return false;
    }

    public void onRepaint(Graphics g) {

        long runTime = 0;
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        int index = Skills.getStatIndex("Fishing");
        @SuppressWarnings("unused")
        long untilhour = 0, untilmin = 0, untilsec = 0;
        int exp = 0;
        int expGained = 0;
        int levelsGained;

        //
        if (lastExp == 0) {
            lastExp = skills.getCurrentXP(STAT_FISHING);
        }

        if (skills.getCurrentXP(STAT_FISHING) > lastExp) {
            lastExp = skills.getCurrentXP(STAT_FISHING);
            numberOfCatches++;
        }

        if (countToNext == 0) {
            untilsec = 0;
            untilhour = 0;
            untilmin = 0;
        }

        // Return if paint is disabled.
        if (!usePaint) {
            return;
        }

        //
        if (playerStartXP == 0) {
            playerStartXP = skills.getCurrentXP(STAT_FISHING);
        }

        if (startLevel == 0) {
            startLevel = skills.getCurrentLvl(STAT_FISHING);
        }

        // Calculate current runtime.
        runTime = System.currentTimeMillis() - scriptStartTime;
        seconds = runTime / 1000;
        if (seconds >= 60) {
            minutes = seconds / 60;
            seconds -= (minutes * 60);
        }
        if (minutes >= 60) {
            hours = minutes / 60;
            minutes -= (hours * 60);
        }
        exp = skills.getCurrentXP(index) - startExp;
        if (exp > oldExp) {
            xpPerCatch = exp - oldExp;
            oldExp = exp;
            countToNext = skills.getXPToNextLvl(STAT_FISHING) / xpPerCatch
                    + 1;
        }


        // Calculate experience gained.
        expGained = skills.getCurrentXP(STAT_FISHING) - playerStartXP;
        if (pColor.equals("SunKist")) {
            g.setColor(new Color(253, 196, 0, 100));
        }
        if (pColor.equals("PinkPanther")) {
            g.setColor(new Color(255, 100, 255, 53));
        }
        if (pColor.equals("ClearSky")) {
            g.setColor(new Color(60, 155, 159, 50));
        }
        if (pColor.equals("Monochrome")) {
            g.setColor(new Color(0, 0, 0, 175));
        }
        if (pColor.equals("BloodShed")) {
            g.setColor(new Color(255, 0, 0, 50));
        }
        if (pColor.equals("Nightmare")) {
            g.setColor(new Color(0, 0, 0, 175));
        }

        int[][] paint = new int[][]{new int[]{136, 152, 168, 184, 200, 216, 232, 248, 264, 280, 296, 312, 328}, new int[]{152, 186}};
        if (barbarianMode) {
            paint[1][0] -= 16;
            paint[1][1] += 16;
        }
        if (powerFishing) {
            paint[1][0] -= 16;
            paint[1][1] += 16;
        }
        g.fillRoundRect(4, paint[1][0], 200, paint[1][1], 45, 45);

        // Calculate levels gained
        levelsGained = skills.getCurrentLvl(STAT_FISHING) - startLevel;
        if (pColor.equals("SunKist")) {
            g.setColor(Color.WHITE);
        }
        if (pColor.equals("PinkPanther")) {
            g.setColor(Color.BLACK);
        }
        if (pColor.equals("ClearSky")) {
            g.setColor(Color.BLACK);
        }
        if (pColor.equals("Monochrome")) {
            g.setColor(Color.WHITE);
        }
        if (pColor.equals("BloodShed")) {
            g.setColor(Color.BLACK);
        }
        if (pColor.equals("Nightmare")) {
            g.setColor(Color.GREEN);
        }
        final ScriptManifest props = getClass().getAnnotation(
                ScriptManifest.class);
        if (barbarianMode || powerFishing) {
            if (powerFishing && !barbarianMode) {
                g.drawString(props.name() + " v" + props.version(), 12, paint[0][1]);
                g.drawString("Fishing location: " + locationName, 12, paint[0][2]);
                g.drawString("Fishing for: " + catchName, 12, paint[0][3]);
                g.drawString("Powerfishing Mode Active", 12, paint[0][4]);
                g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
                g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
                g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
                g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
                g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
                g.drawString("Percent to next level: " + skills.getPercentToNextLvl(STAT_FISHING), 12, paint[0][10]);
                g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

            }
            if (!powerFishing && barbarianMode) {
                g.drawString(props.name() + " v" + props.version(), 12, paint[0][1]);
                g.drawString("Fishing location: " + locationName, 12, paint[0][2]);
                g.drawString("Fishing for: " + catchName, 12, paint[0][3]);
                g.drawString("Barbarian Mode Active", 12, paint[0][4]);
                g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
                g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
                g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
                g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
                g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
                g.drawString("Percent to next level: " + skills.getPercentToNextLvl(STAT_FISHING), 12, paint[0][10]);
                g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

            }
            if (powerFishing && barbarianMode) {
                g.drawString(props.name() + " v" + props.version(), 12, paint[0][0]);
                g.drawString("Fishing location: " + locationName, 12, paint[0][1]);
                g.drawString("Fishing for: " + catchName, 12, paint[0][2]);
                g.drawString("Powerfishing Mode Active", 12, paint[0][3]);
                g.drawString("Barbarian Mode Active", 12, paint[0][4]);
                g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
                g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
                g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
                g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
                g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
                g.drawString("Percent to next level: " + skills.getPercentToNextLvl(STAT_FISHING), 12, paint[0][10]);
                g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

            }
        } else {
            g.drawString(props.name() + " v" + props.version(), 12, paint[0][2]);
            g.drawString("Fishing location: " + locationName, 12, paint[0][3]);
            g.drawString("Fishing for: " + catchName, 12, paint[0][4]);
            g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
            g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
            g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
            g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
            g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
            g.drawString("Percent to next level: " + skills.getPercentToNextLvl(STAT_FISHING), 12, paint[0][10]);
            g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

        }
    }


    private void beep(int count) {
        if (!Sound) {
            return;
        }
        for (int i = 0; i < count; i++) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            wait(250);
        }
        wait(random(100, 500));
        return;
    }

    private boolean isWelcomeButton() {
        RSInterface welcomeInterface = iface.get(378);
        if (welcomeInterface.getChild(45).getAbsoluteX() > 20 || (!welcomeInterface.getChild(117).getText().equals("10.1120.190") && !welcomeInterface.getChild(117).getText().equals(""))) {
            return true;
        } else {
            return false;
        }
    }
}