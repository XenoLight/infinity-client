// <editor-fold defaultstate="collapsed" desc="Imports">
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Bank;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
// </editor-fold>

@ScriptManifest(
		authors = {"Gribonn"}, 
		category = "Firemaking", 
		name = "Gribonn's Firemaker", 
		version = 1.0, 
		description = "<html>Settings in GUI<br><img src=http://i56.tinypic.com/jq4yzq.png /></html>")
public class GribonnsFiremaker extends Script implements PaintListener, MessageListener, MouseListener {

    // <editor-fold defaultstate="collapsed" desc="Fields">
    private boolean developer = false, starting = true, showAdditional = false,
            info, logo, fmcape, nexttile, lighttile, fireline1,
            fireline2, ofire, outtaLogs = false, video = true,
            pickupLog = false;
    Rectangle buttonRect = new Rectangle(19, 125, 129, 14);
    private int normalLogID = 1511;
    private int oakLogID = 1521;
    private int willowLogID = 1519;
    private int mapleLogID = 1517;
    private int yewLogID = 1515;
    private int magicLogID = 1513;
    private int mouseS = 0;
    private int currMouseS = 0;
    private int ABRate = 0;
    private int burned = 0;
    private int tinderboxID = 590;
    private int mouseTail = 3 * 40;
    private int[] bboothID = Bank.BankBooths;
    private int[] bankerIDs = Bank.Bankers;
    private int[] allowedOtherIDs = {23676, 23638, 23641, 23834, 23839, 23727,
        23835};
    private RSTile[] disAllowedTiles = {new RSTile(3013, 3351),
        new RSTile(3013, 3352), new RSTile(3013, 3350),
        new RSTile(3004, 3359), new RSTile(3004, 3360),
        new RSTile(3007, 3366), new RSTile(3007, 3367),
        new RSTile(2995, 3363), new RSTile(2995, 3364),
        new RSTile(2995, 3365), new RSTile(2986, 3361),
        new RSTile(3261, 3435), new RSTile(3261, 3434),
        new RSTile(3258, 3433), new RSTile(3258, 3432),
        new RSTile(3258, 3431), new RSTile(3258, 3430),
        new RSTile(3254, 3430), new RSTile(3259, 3427),
        new RSTile(3260, 3426), new RSTile(3260, 3425),
        new RSTile(3260, 3424), new RSTile(3260, 3423),
        new RSTile(3250, 3427), new RSTile(3249, 3426),
        new RSTile(3248, 3424), new RSTile(3270, 3425),
        new RSTile(3271, 3424), new RSTile(3271, 3423),
        new RSTile(3270, 3431), new RSTile(3271, 3433),
        new RSTile(3271, 3432), new RSTile(3260, 3422),
        new RSTile(3260, 3421), new RSTile(3260, 3420),
        new RSTile(3260, 3419), new RSTile(3260, 3418),
        new RSTile(3260, 3417), new RSTile(3260, 3416),
        new RSTile(3260, 3415), new RSTile(3260, 3414),
        new RSTile(3260, 3413)};
    private RSTile[] allowedTiles = {new RSTile(3258, 3428),
        new RSTile(3249, 3428), new RSTile(3105, 3503)};
    Rectangle lastPos = null;

    /* Fist of Guthix */
    private RSTile[] FireTilesFoG = {new RSTile(1712, 5597),
        new RSTile(1712, 5598), new RSTile(1712, 5599),
        new RSTile(1712, 5600), new RSTile(1712, 5601),
        new RSTile(1711, 5601), new RSTile(1711, 5600),
        new RSTile(1711, 5599), new RSTile(1711, 5598),
        new RSTile(1711, 5597), new RSTile(1710, 5597),
        new RSTile(1710, 5598), new RSTile(1710, 5599),
        new RSTile(1710, 5600), new RSTile(1710, 5601),
        new RSTile(1709, 5602), new RSTile(1709, 5601),
        new RSTile(1709, 5600), new RSTile(1709, 5599),
        new RSTile(1709, 5598), new RSTile(1709, 5597),
        new RSTile(1713, 5597), new RSTile(1713, 5598),
        new RSTile(1713, 5599), new RSTile(1713, 5600),
        new RSTile(1713, 5601), new RSTile(1714, 5601),
        new RSTile(1714, 5600), new RSTile(1714, 5599),
        new RSTile(1714, 5598), new RSTile(1714, 5597),
        new RSTile(1715, 5597), new RSTile(1715, 5598),
        new RSTile(1715, 5599), new RSTile(1715, 5600),
        new RSTile(1715, 5601), new RSTile(1716, 5601),
        new RSTile(1716, 5600), new RSTile(1716, 5599),
        new RSTile(1716, 5598), new RSTile(1716, 5597),
        new RSTile(1708, 5602), new RSTile(1708, 5601),
        new RSTile(1708, 5600), new RSTile(1708, 5599),
        new RSTile(1708, 5598), new RSTile(1708, 5597),
        new RSTile(1707, 5597), new RSTile(1707, 5598),
        new RSTile(1707, 5599), new RSTile(1707, 5600),
        new RSTile(1707, 5601), new RSTile(1707, 5602)};
    private RSTile FireLocsFoG = new RSTile(1707, 5601);
    private RSTile BankTileFoG = new RSTile(1705, 5599);
    private RSArea FoGArea = new RSArea(new RSTile(1675, 5595), new RSTile(
            1717, 5605));

    /* Edgeville */
    private RSTile[] FireTilesE = {new RSTile(3100, 3503),
        new RSTile(3100, 3504), new RSTile(3100, 3505),
        new RSTile(3101, 3505), new RSTile(3101, 3504),
        new RSTile(3101, 3503), new RSTile(3102, 3503),
        new RSTile(3102, 3504), new RSTile(3102, 3505),
        new RSTile(3103, 3505), new RSTile(3103, 3504),
        new RSTile(3103, 3503), new RSTile(3104, 3503),
        new RSTile(3104, 3504), new RSTile(3104, 3505),
        new RSTile(3099, 3505), new RSTile(3099, 3504),
        new RSTile(3099, 3503), new RSTile(3105, 3503),
        new RSTile(3105, 3504), new RSTile(3105, 3505)};
    private RSTile FireLocsE = new RSTile(3101, 3503);
    private RSTile BankTileE = new RSTile(3093, 3492);
    private RSArea BAreaE = new RSArea(new RSTile(3091, 3488), new RSTile(3098,
            3499));
    private RSArea EArea = new RSArea(new RSTile(3064, 3487), new RSTile(3116,
            3519));

    /* Falador */
    private RSTile[] FireTilesF = {new RSTile(3023, 3360),
        new RSTile(3023, 3361), new RSTile(3023, 3362),
        new RSTile(3023, 3363), new RSTile(3023, 3364),
        new RSTile(3023, 3365), new RSTile(3024, 3364),
        new RSTile(3024, 3363), new RSTile(3024, 3362),
        new RSTile(3024, 3361), new RSTile(3024, 3360),
        new RSTile(3025, 3360), new RSTile(3025, 3361),
        new RSTile(3025, 3362), new RSTile(3025, 3363),
        new RSTile(3025, 3364), new RSTile(3026, 3364),
        new RSTile(3026, 3362), new RSTile(3026, 3361),
        new RSTile(3026, 3360), new RSTile(3027, 3360),
        new RSTile(3027, 3361), new RSTile(3027, 3362),
        new RSTile(3027, 3364), new RSTile(3028, 3364),
        new RSTile(3028, 3362), new RSTile(3028, 3361),
        new RSTile(3028, 3360), new RSTile(3029, 3360),
        new RSTile(3029, 3361), new RSTile(3029, 3362),
        new RSTile(3029, 3364), new RSTile(3030, 3362),
        new RSTile(3030, 3361), new RSTile(3030, 3360),
        new RSTile(3031, 3360), new RSTile(3031, 3361),
        new RSTile(3031, 3362), new RSTile(3032, 3362),
        new RSTile(3032, 3361), new RSTile(3032, 3360),
        new RSTile(3033, 3360), new RSTile(3033, 3361),
        new RSTile(3033, 3362)};
    private RSTile FireLocsF = new RSTile(3029, 3362);
    private RSTile BankTileF = new RSTile(3013, 3356);
    private RSArea BAreaF = new RSArea(new RSTile(3009, 3355), new RSTile(3018,
            3358));
    private RSArea FArea = new RSArea(new RSTile(2936, 3307), new RSTile(3068,
            3393));

    /* VarrockW */
    private RSTile[] FireTilesVW = {new RSTile(3199, 3430),
        new RSTile(3198, 3430), new RSTile(3197, 3430),
        new RSTile(3197, 3429), new RSTile(3198, 3429),
        new RSTile(3199, 3429), new RSTile(3200, 3429),
        new RSTile(3201, 3429), new RSTile(3201, 3428),
        new RSTile(3200, 3428), new RSTile(3199, 3428),
        new RSTile(3198, 3428), new RSTile(3197, 3428)};
    private RSTile FireLocsVW = new RSTile(3200, 3429);
    private RSTile BankTileVW = new RSTile(3185, 3434);
    private RSArea BAreaVW = new RSArea(new RSTile(3182, 3432), new RSTile(
            3189, 3446));
    private RSArea VWArea = new RSArea(new RSTile(3168, 3419), new RSTile(3212,
            3448));

    /* VarrockE */
    private RSTile[] FireTilesVE = {new RSTile(3264, 3428),
        new RSTile(3265, 3428), new RSTile(3266, 3428),
        new RSTile(3267, 3428), new RSTile(3268, 3428),
        new RSTile(3268, 3429), new RSTile(3267, 3429),
        new RSTile(3265, 3429), new RSTile(3266, 3429),
        new RSTile(3264, 3429)};
    private RSTile FireLocsVE = new RSTile(3266, 3428);
    private RSTile BankTileVE = new RSTile(3253, 3421);
    private RSArea BAreaVE = new RSArea(new RSTile(3250, 3419), new RSTile(
            3257, 3423));
    private RSArea VEArea = new RSArea(new RSTile(3215, 3415), new RSTile(3272,
            3435));

    /* GE North */
    private RSTile[] FireTilesGENorth = {new RSTile(3175, 3496),
        new RSTile(3176, 3496), new RSTile(3177, 3496),
        new RSTile(3178, 3496), new RSTile(3178, 3497),
        new RSTile(3177, 3497), new RSTile(3176, 3497),
        new RSTile(3175, 3497), new RSTile(3175, 3501),
        new RSTile(3176, 3501), new RSTile(3177, 3501),
        new RSTile(3178, 3501), new RSTile(3178, 3502),
        new RSTile(3177, 3502), new RSTile(3176, 3502),
        new RSTile(3175, 3502), new RSTile(3175, 3503),
        new RSTile(3176, 3503), new RSTile(3177, 3503),
        new RSTile(3178, 3503)};
    private RSTile FireLocsGENorth = new RSTile(3175, 3498);
    private RSTile BankTileGENorth = new RSTile(3167, 3492);
    private RSArea GENorthArea = new RSArea(new RSTile(3143, 3490), new RSTile(
            3188, 3508));

    /* GE South */
    private RSTile[] FireTilesGESouth = {new RSTile(3178, 3482),
        new RSTile(3178, 3483), new RSTile(3178, 3478),
        new RSTile(3178, 3477), new RSTile(3178, 3476),
        new RSTile(3178, 3475), new RSTile(3178, 3474),
        new RSTile(3179, 3474), new RSTile(3179, 3475),
        new RSTile(3179, 3476), new RSTile(3179, 3477),
        new RSTile(3179, 3478), new RSTile(3179, 3483),
        new RSTile(3179, 3482), new RSTile(3180, 3482),
        new RSTile(3180, 3483), new RSTile(3180, 3478),
        new RSTile(3180, 3477), new RSTile(3180, 3476),
        new RSTile(3180, 3475), new RSTile(3180, 3474)};
    private RSTile FireLocsGESouth = new RSTile(3176, 3482);
    private RSTile BankTileGESouth = new RSTile(3164, 3487);
    private RSArea GESouthArea = new RSArea(new RSTile(3144, 3469), new RSTile(
            3190, 3489));

    /* Seers Village */
    private RSTile[] FireTilesS = {new RSTile(2727, 3485),
        new RSTile(2727, 3484), new RSTile(2728, 3484),
        new RSTile(2728, 3485), new RSTile(2729, 3485),
        new RSTile(2729, 3484), new RSTile(2730, 3484),
        new RSTile(2730, 3485), new RSTile(2731, 3485),
        new RSTile(2731, 3484), new RSTile(2722, 3485),
        new RSTile(2722, 3484), new RSTile(2723, 3484),
        new RSTile(2723, 3485), new RSTile(2724, 3485),
        new RSTile(2724, 3484), new RSTile(2725, 3484),
        new RSTile(2725, 3485), new RSTile(2726, 3485),
        new RSTile(2726, 3484)};
    private RSTile FireLocsS = new RSTile(2727, 3485);
    private RSTile BankTileS = new RSTile(2723, 3492);
    private RSArea BAreaS = new RSArea(new RSTile(2721, 3487), new RSTile(2730,
            3493));
    private RSArea SArea = new RSArea(new RSTile(2682, 3474), new RSTile(2742,
            3502));

    /* Yanille */
    private RSTile[] FireTilesY = {new RSTile(2607, 3099),
        new RSTile(2607, 3098), new RSTile(2607, 3097),
        new RSTile(2606, 3097), new RSTile(2606, 3098),
        new RSTile(2606, 3099), new RSTile(2605, 3099),
        new RSTile(2605, 3098), new RSTile(2605, 3097),
        new RSTile(2604, 3097), new RSTile(2604, 3098),
        new RSTile(2604, 3099), new RSTile(2603, 3099),
        new RSTile(2603, 3098), new RSTile(2603, 3097),
        new RSTile(2602, 3097), new RSTile(2602, 3098),
        new RSTile(2602, 3099), new RSTile(2601, 3099),
        new RSTile(2601, 3098), new RSTile(2601, 3097),
        new RSTile(2600, 3099), new RSTile(2600, 3098),
        new RSTile(2600, 3097)};
    private RSTile FireLocsY = new RSTile(2604, 3097);
    private RSTile BankTileY = new RSTile(2611, 3092);
    private RSArea BAreaY = new RSArea(new RSTile(2609, 3088), new RSTile(2613,
            3097));
    private RSArea YArea = new RSArea(new RSTile(2532, 3071), new RSTile(2624,
            3113));

    /* General */
    private RSTile[] FireTiles = {};
    private RSTile FireLocs = null;
    private RSTile BankTile = null;
    private int logID = 0;
    private int startExp = 0;
    private int startLevel = 0;
    private long startTime;
    private String logname;
    private String command;
    private boolean FMing = false, stopScript = false;
    private ArrayList<RSTile> lightFireTiles = new ArrayList<RSTile>();
    private ArrayList<RSTile> thisFiresTiles = new ArrayList<RSTile>();
    private RSTile locOnLight = null;
    private RSTile startFmTile = null;
    private RSTile nextTile = null;
    private RSTile prevWalkTile = null;
    private RSArea BArea = null;
    private boolean guiWait = true, guiExit;
    GribonnsFiremakerGUI gui;

    /* Paint */
    private final Color color1 = new Color(225, 90, 0, 200);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(0, 0, 0);
    private final Color color4 = new Color(0, 51, 255);
    private final BasicStroke stroke1 = new BasicStroke(1);
    private final Font font1 = new Font("Arial", 0, 9);
    private final Image img1 = getImage("http://i56.tinypic.com/2ppy2w1.png");
    private final Image img2 = getImage("http://i53.tinypic.com/2ro2np5.png");
    private final Image img3 = getImage("http://i52.tinypic.com/igdc1u.png");
    private final Image img4 = getImage("http://i51.tinypic.com/2qu67og.png");
    private final Image img5 = getImage("http://i53.tinypic.com/eamrsx.png");
    private ArrayList<Point> mouseHistory = new ArrayList<Point>();

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Booleans">
    private boolean canFM() {
        return inventory.contains(logID) && inventory.contains(tinderboxID);
    }

    private boolean canBank() {
        return player.isIdle() || ground.getItemsAt(player.getMyLocation()).size() == 0;
    }

    private boolean gotFMPath() {
        return startFmTile != null;
    }

    private boolean standingOnFire() {
        final RSObject obj = objects.getTopAt(player.getMyLocation());
        return obj != null && obj.getID() == 2732;
    }

    private boolean needFailsafeCheck() {
        return calculate.distanceTo(nextTile) >= 1 && FMing && player.isIdle() && inventory.contains(logID);
    }

    private boolean mayPerformAntiban() {
        return player.isIdle() || FMing;
    }

    private boolean isBiggerThan(int FirstValue, int... allOthers) {
        if (FirstValue == 0) {
            return false;
        }
        for (int i : allOthers) {
            if (FirstValue < i) {
                return false;
            }
        }
        return true;
    }

    private boolean onLastTile() {
        if (!thisFiresTiles.isEmpty()) {
            return thisFiresTiles.get(thisFiresTiles.size() - 1).equals(player.getMyLocation());
        } else {
            return false;
        }
    }

    private boolean hasLogsUnder() {
        for (RSGroundItem item : ground.getItemsAt(player.getMyLocation())) {
            if (item.getItem().getID() == logID) {
                return true;
            }
        }
        return false;
    }


    private boolean tileFullyOnScreen(RSTile tile) {
        if (tile != null) {
            Calculations.tileToScreen(tile);
            final Point I = Calculations.tileToScreen(tile.getX(), tile.getY(),
                    0, 0, 0);
            final Point II = Calculations.tileToScreen(tile.getX() + 1, tile.getY(), 0, 0, 0);
            final Point III = Calculations.tileToScreen(tile.getX(), tile.getY() + 1, 0, 0, 0);
            final Point IV = Calculations.tileToScreen(tile.getX() + 1, tile.getY() + 1, 0, 0, 0);
            if (I.x != -1 && II.x != -1 && III.x != -1 && IV.x != -1
                    && I.y != -1 && II.y != -1 && III.y != -1 && IV.y != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBank(RSTile t, boolean needToBeAtBank) {
        if (BArea != null) {
            return BArea.contains(t);
        } else {
            return needToBeAtBank && BankTile.isOnScreen();
        }
    }

    private boolean contains(int[] a, int b) {
        boolean returnment = false;
        for (int c : a) {
            if (c == b) {
                returnment = true;
            }
        }
        return returnment;
    }

    private boolean contains(RSTile[] a, RSTile b) {
        for (RSTile c : a) {
            if (c.equals(b)) {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Methods">

    private void fm() {
        if (mayPerformAntiban()) {
            new Thread(new antiban()).start();
        }
        if (!gotFMPath()) {
            getBestPlaceToFm();
        } else if (!isInBank(player.getMyLocation(),false)) {
            burnFailsafe();
            if (calculate.distanceTo(nextTile) >= 1 && !FMing) {
                if (!checkFromUrself()) {
                    if (bank.isOpen() || !tileFullyOnScreen(nextTile)) {
                        setToRun();
                        if (player.isIdle() && inventory.contains(logID)) {
                            walkToFixed(nextTile);
                        }
                    } else {
                        if (player.isIdle() && inventory.contains(logID)) {
                            walk.tileOnScreen(nextTile);
                        }
                    }
                }

            } else if (calculate.distanceTo(nextTile) == 0) {
                if (!standingOnFire()) {
                    burn(false);
                } else {
                    startFmTile = null;
                    nextTile = null;
                    thisFiresTiles.clear();
                    lightFireTiles.clear();
                    FMing = false;
                    return;
                }
            }
        }
    }

    private int bank() {
        FMing = false;

        if (bank.isOpen()) {
            resetPaths();
            if (bank.getCount(logID) > 0) {
                bank.depositAllExcept(tinderboxID, logID);
                
                while (!inventory.contains(tinderboxID)) {
                    bank.withdraw(tinderboxID, 1);
                    devLog(true, "Withdrawed tinderbox");
                    inventory.waitForCount(tinderboxID, 1, random(700, 1000));
                    bank.depositAllExcept(tinderboxID, logID);
                }
                if (inventory.getCount(tinderboxID) > 1) {
                    bank.deposit(tinderboxID, inventory.getCount() - 1);
                }
                
                while (!inventory.contains(logID)) {
                	int ct = inventory.getCount();
                	bank.withdraw(logID, 0);
                    inventory.waitForCountGreater(ct, random(300, 600));
                    bank.depositAllExcept(tinderboxID, logID);
                }
                if (inventory.contains(logID)) {
                    devLog(true, "Withdrawing logs succeed");
                } else {
                    devLog(false, "Withdrawing logs failed");
                }
                bank.depositAllExcept(tinderboxID, logID);
            } else {
                devLog(false, "Logs: " + bank.getCount(logID) + ", log's id: "
                        + logID);
                if (logID == 0) {
                    getAutoLogs();
                } else {
                    stopScript = true;
                    outtaLogs = true;
                }
            }
        } else {
            checkBeforeBank();
            walkToAndOpenBank();
        }
        return random(200, 400);
    }

    private int mainFailsafe() {
        if (BankTile == null) {
            getAutoLoc();
            return random(500, 900);
        }
        if (logID == 0) {
            getAutoLogs();
            return random(500, 900);
        }
        if (FMing && player.isIdle()) {
            if (lightFireTiles != null && !lightFireTiles.isEmpty()) {
                boolean isTrue = false;
                if (player.getMyLocation().equals(nextTile) || player.getMyLocation().equals(locOnLight)) {
                    isTrue = true;
                }
                if (!isTrue) {
                	walk.tileOnScreen(nextTile);
                    return random(500, 700);
                }
            }
        }
        if (stopScript) {
            if (bank.isOpen()) {
                bank.close();
            }
            if (outtaLogs) {
                log("Out of logs to burn, logging out");
            } else {
                log("Stopping script because of error");
            }
            stopScript(true);
            return random(500, 700);
        }
        if (getCameraAltitude() < 68) {
            devLog(false, "Resetting camera altitude");
            camera.setAltitude(true);
        }
        if (inventory.getCount(logID) == 1 && inventory.isItemSelected()) {
            inventory.clickItem(logID, "Use");
        }
        if (pickupLog) {
            if (inventory.isFull()) {
                pickupLog = false;
                return random(500, 900);
            }
            RSTile[] plls = {player.getMyLocation(), new RSTile(player.getMyLocation().getX() + 1, player.getMyLocation().getY())};
            for (RSTile pll : plls) {
                List<RSGroundItem> items = ground.getItemsAt(pll);
                for (RSGroundItem item : items) {
                    if (item.getItem().getID() == logID) {
                        final int invCount = inventory.getCount(logID);
                        while (!player.isIdle()) {
                        }
                        if (!tileFullyOnScreen(pll)) {
                            walkToFixed(pll);
                            pickupLog = true;
                            devLog(false, "Walking to the missing log");
                            return random(200, 500);
                        } else {
                        	tile.click(pll, "Take");
                            wait(random(900, 1500));
                            if (inventory.getCount(logID) > invCount) {
                                pickupLog = false;
                                devLog(true, "Successfully picked up the missing log");
                            } else {
                                pickupLog = true;
                                devLog(false, "Failed picking up the missing log");
                            }
                        }
                        break;
                    }
                }
            }
            return random(500, 900);
        }
        if (!bank.isOpen() && inventory.contains(592)) {
            devLog(false, "Dropping ashes");
            inventory.clickItem(592, "Drop");
            return random(500, 900);
        }
        return -1;
    }

    private void burn(boolean isFailsafe) {
        if (!player.isIdle() && !FMing) {
            return;
        }
        if ((nextTile.equals(startFmTile)
                || !inventory.isItemSelected(tinderboxID)) && !onLastTile()) {
        	RSItem item = inventory.getSelectedItem();
        	if (item==null)  {
        		inventory.clickItem(tinderboxID, "Use");
        	}
        }
        locOnLight = player.getMyLocation();
        int i = 0;
        if (!menu.action(command)) {
            if (!inventory.clickItem(logID, "Use ")) {
                i = 100;
            }
        }
        if (i == 100) {
            checkBeforeBank();
            return;
        } else {
            if (nextTile != null) {
                if (inventory.getCount(logID) > 1) {
                    nextTile = new RSTile(nextTile.getX() - 1, nextTile.getY());
                } else {
                    nextTile = null;
                }
            } else if (gotFMPath()) {
                if (inventory.getCount(logID) > 1) {
                    nextTile = new RSTile(startFmTile.getX() - 1, startFmTile.getY());
                } else {
                    nextTile = null;
                }
            } else {
                getBestPlaceToFm();
            }
            if (!FMing) {
                FMing = true;
            }
            
        	RSItem item = inventory.getSelectedItem();
        	if (item==null)  {
        		inventory.clickItem(tinderboxID, "Use");
        	}
            MoveMouseToItem(logID, !isFailsafe);
        }
    }

    private void burnFailsafe() {
        int c = 0;
        while (needFailsafeCheck()) {
            if (standingOnFire()) {
                resetPaths();
                c = 0;
                break;
            }
            if (c >= 20) {
                if (player.isIdle()) {
                    burn(true);
                    c = 0;
                    break;
                }
            }
            c++;
            wait(random(100, 200));
        }
        if (c > 10) {
            devLog(true, "Broke failsafe at " + c + " of 20");
        }
    }

    public void checkBeforeBank() {
        if (onLastTile()) {
            while (!player.isIdle()) {
            }
            int l = 0;
            while (hasLogsUnder() && l < 200) {
                wait(random(10, 15));
                l++;
            }
            while (!player.isIdle()) {
            }
            wait(random(200, 500));
        }
    }

    private void setNewMouseSpeed() {
        if (mouseS > 1) {
            currMouseS = random((mouseS - 1), (mouseS + 2));
        } else {
            currMouseS = random(1, 3);
        }
    }

    private void getAutoLogs() {
        String detectedLog = null;
        if (inventory.contains(magicLogID)) {
            detectedLog = "Magic logs";
        } else if (inventory.contains(yewLogID)) {
            detectedLog = "Yew logs";
        } else if (inventory.contains(mapleLogID)) {
            detectedLog = "Maple logs";
        } else if (inventory.contains(willowLogID)) {
            detectedLog = "Willow logs";
        } else if (inventory.contains(oakLogID)) {
            detectedLog = "Oak logs";
        } else if (inventory.contains(normalLogID)) {
            detectedLog = "Normal logs";
        } else {
            if (!bank.isOpen()) {
                walkToAndOpenBank();
            } else {
                int normalCount = bank.getCount(normalLogID);
                int oakCount = bank.getCount(oakLogID);
                int willowCount = bank.getCount(willowLogID);
                int mapleCount = bank.getCount(mapleLogID);
                int yewCount = bank.getCount(yewLogID);
                int magicCount = bank.getCount(magicLogID);
                int[] allList = {magicCount, normalCount, oakCount,
                    willowCount, mapleCount, yewCount, magicCount};
                if (isBiggerThan(yewCount, allList)) {
                    detectedLog = "Yew logs";
                } else if (isBiggerThan(mapleCount, allList)) {
                    detectedLog = "Maple logs";
                } else if (isBiggerThan(willowCount, allList)) {
                    detectedLog = "Willow logs";
                } else if (isBiggerThan(oakCount, allList)) {
                    detectedLog = "Oak logs";
                } else if (isBiggerThan(normalCount, allList)) {
                    detectedLog = "Normal logs";
                } else if (isBiggerThan(magicCount, allList)) {
                    detectedLog = "Magic logs";
                }
            }
        }
        if (detectedLog != null) {
            logname = detectedLog;
            if (detectedLog.equals("Normal logs")) {
                logname = "Logs";
                logID = normalLogID;
            } else if (detectedLog.equals("Oak logs")) {
                logID = oakLogID;
            } else if (detectedLog.equals("Willow logs")) {
                logID = willowLogID;
            } else if (detectedLog.equals("Maple logs")) {
                logID = mapleLogID;
            } else if (detectedLog.equals("Yew logs")) {
                logID = yewLogID;
            } else if (detectedLog.equals("Magic logs")) {
                logID = magicLogID;
            }
            command = "Use Tinderbox -> " + logname;
            log("Detected log type: " + detectedLog);
        }
    }

    private void getAutoLoc() {
        String loc = null;
        if (YArea.contains(player.getMyLocation())) {
            loc = "Yanille";
        } else if (SArea.contains(player.getMyLocation())) {
            loc = "Seers Village";
        } else if (FArea.contains(player.getMyLocation())) {
            loc = "Falador";
        } else if (EArea.contains(player.getMyLocation())) {
            loc = "Edgeville";
        } else if (FoGArea.contains(player.getMyLocation())) {
            loc = "Fist of Guthix";
        } else if (VWArea.contains(player.getMyLocation())) {
            loc = "Varrock (West)";
        } else if (VEArea.contains(player.getMyLocation())) {
            loc = "Varrock (East)";
        } else if (GENorthArea.contains(player.getMyLocation())) {
            loc = "Grand Exchange (North)";
        } else if (GESouthArea.contains(player.getMyLocation())) {
            loc = "Grand Exchange (South)";
        } else {
            log("I Don't support that location, stopping");
            stopScript = true;
        }
        if (loc != null) {
            log("Detected location: " + loc);
        }
        if (loc.equals("Falador")) {
            FireTiles = FireTilesF;
            FireLocs = FireLocsF;
            BankTile = BankTileF;
            BArea = BAreaF;
        } else if (loc.equals("Edgeville")) {
            FireTiles = FireTilesE;
            FireLocs = FireLocsE;
            BankTile = BankTileE;
            BArea = BAreaE;
        } else if (loc.equals("Fist of Guthix")) {
            FireTiles = FireTilesFoG;
            FireLocs = FireLocsFoG;
            BankTile = BankTileFoG;
        } else if (loc.equals("Varrock (West)")) {
            FireTiles = FireTilesVW;
            FireLocs = FireLocsVW;
            BankTile = BankTileVW;
            BArea = BAreaVW;
        } else if (loc.equals("Varrock (East)")) {
            FireTiles = FireTilesVE;
            FireLocs = FireLocsVE;
            BankTile = BankTileVE;
            BArea = BAreaVE;
        } else if (loc.equals("Grand Exchange (North)")) {
            FireTiles = FireTilesGENorth;
            FireLocs = FireLocsGENorth;
            BankTile = BankTileGENorth;
        } else if (loc.equals("Grand Exchange (South)")) {
            FireTiles = FireTilesGESouth;
            FireLocs = FireLocsGESouth;
            BankTile = BankTileGESouth;
        } else if (loc.equals("Seers Village")) {
            FireTiles = FireTilesS;
            FireLocs = FireLocsS;
            BankTile = BankTileS;
            BArea = BAreaS;
        } else if (loc.equals("Yanille")) {
            FireTiles = FireTilesY;
            FireLocs = FireLocsY;
            BankTile = BankTileY;
            BArea = BAreaY;
        } else {
            log("Didn't find any locations you are in, stopping script");
        }
    }

    private void walkToFixed(RSTile t) {
        walkToFixed(t, 2, 2);
    }

    private boolean walkToFixed(RSTile t, int x, int y) {
        if (prevWalkTile == null || calculate.distanceTo(prevWalkTile) <= rrandom(3, 5) || player.isIdle()) {
            int c = 0;
            boolean mayGoOn = false;
            while (!mayGoOn) {
                RSTile randTile = walk.randomizeTile(t, x, y);
                try {
                    if (randTile.isOnMinimap()) {
                        mouse.move(tile.toMiniMap(randTile), 0, 0);
                        prevWalkTile = randTile;
                    } else {
                        RSTile[] temp = walk.randomizePath(walk.generateFixedPath(randTile), x, y);
                        RSTile walkTile = walk.nextTile(temp, 12);
                        Point p = tile.toMiniMap(walkTile);
                        mouse.move(p, 0, 0);
                        prevWalkTile = walkTile;
                    }
                    if (!menu.action("Walk here")) {
                        devLog(false, "Failed walking to " + randTile.toString()
                                + ", rotating camera");
                        mayGoOn = false;
                        camera.setRotation(random(camera.getAngle() - 50, camera.getAngle() + 50));
                        c++;
                    } else {
                        devLog(true, "Walking succeed to " + randTile.toString());
                        mayGoOn = true;
                        wait(random(500, 800));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    c++;
                    mayGoOn = false;
                    devLog(false, "Got error in walking: " + e.getMessage());
                }
                if (c >= 10) {
                    devLog(false, "Walking failed 10 times, quitting");
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private int walkToAndOpenBank() {
        if (isInBank(player.getMyLocation(), true)) {
            final RSObject bbooth = objects.getNearestByID(bboothID);
            final RSNPC banker = npc.getNearestByID(bankerIDs);
            if (player.isIdle()) {
                if (bbooth != null
                        && ((banker != null && calculate.distanceTo(banker) > calculate.distanceTo(bbooth)) || banker == null)) {
                    if (tileFullyOnScreen(bbooth.getLocation())) {
                        if (bank.open()) {
                            int c = 0;
                            while (!bank.isOpen()) {
                                if (bank.open()) {
                                    return random(300, 500);
                                }
                                if (c == 5) {
                                    return random(500, 700);
                                }
                                c++;
                                wait(random(500, 700));
                            }
                        }
                    } else {
                        setToRun();
                        walkToFixed(bbooth.getLocation());
                    }
                } else if (banker != null) {
                    if (tileFullyOnScreen(banker.getLocation())) {
                        if (bank.open()) {
                            int c = 0;
                            while (!bank.isOpen()) {
                                if (bank.open()) {
                                    return random(300, 500);
                                }
                                if (c == 5) {
                                    return random(500, 700);
                                }
                                c++;
                                wait(random(500, 700));
                            }
                        }
                    } else {
                        setToRun();
                        walkToFixed(banker.getLocation());
                    }
                }
            }
        } else {
            setToRun();
            if (player.isIdle() || calculate.distanceTo(BankTile) > 10) {
                if (player.isIdle()) {
                    walkToFixed(BankTile);
                    return random(900, 1300);
                }
            }
        }
        return random(500, 700);
    }

    private void setToRun() {
        if (player.getMyEnergy() >= 30 && !isRunning()) {
            game.setRun(true);
            devLog(true, "Set to run");
            wait(random(500, 700));
        }
    }

    private int getCameraAltitude() {
        return Bot.getClient().getCameraPitch() / 45;
    }


    private boolean MoveMouseToItem(int itemID, boolean checkLast) {
        if (game.getCurrentTab() != Game.tabInventory
                && !iface.get(INTERFACE_BANK).isValid()
                && !iface.get(INTERFACE_STORE).isValid()) {
            game.openTab(Game.tabInventory);
        }

        RSInterfaceChild inv = inventory.getInterface();
        if (inv == null || inv.getChildren() == null) {
            return false;
        }

        RSInterfaceChild gnabe = null;
        for (RSInterfaceChild item : inv.getChildren()) {
            if (item != null && item.getChildID() == itemID) {
                if (checkLast == false
                        || (lastPos == null || !lastPos.equals(item.getArea()))) {
                    gnabe = item;
                    lastPos = item.getArea();
                    break;
                }
            }
        }
        if (gnabe == null) {
            return false;
        }
        Rectangle pos = gnabe.getArea();
        if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
            return false;
        }
        int dx = (int) (pos.getWidth() - 4) / 2;
        int dy = (int) (pos.getHeight() - 4) / 2;
        int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
        int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
        mouse.move(midx + random(-dx + 5, dx - 4), midy
                + random(-dy + 5, dy - 4));
        return true;
    }

    private boolean checkFromUrself() {
        if (contains(FireTiles, player.getMyLocation())) {
            final RSTile fmTile2 = player.getMyLocation();
            ArrayList<RSTile> setFiresTiles2 = new ArrayList<RSTile>();
            boolean foundPlace2 = true;
            for (int x = 0; x <= inventory.getCount(logID) - 1; x++) {
                final RSObject object = objects.getTopAt(new RSTile(fmTile2.getX()
                        - x, fmTile2.getY()));
                if (object != null
                        && (object.getType() != 1
                        && (!contains(allowedOtherIDs, object.getID()) && !contains(
                        allowedTiles, new RSTile(fmTile2.getX()
                        - x, fmTile2.getY()))) || contains(
                        disAllowedTiles, new RSTile(fmTile2.getX() - x,
                        fmTile2.getY())))
                        || isInBank(new RSTile(fmTile2.getX() - x, fmTile2.getY()), false)) {
                    foundPlace2 = false;
                }
                setFiresTiles2.add(new RSTile(fmTile2.getX() - x, fmTile2.getY()));
            }
            if (foundPlace2 && !isInBank(player.getMyLocation(), false)) {
                devLog(true, "Getting FM Place");
                thisFiresTiles = setFiresTiles2;
                startFmTile = fmTile2;
                nextTile = fmTile2;
                if (!tileFullyOnScreen(startFmTile) || bank.isOpen()) {
                    walkToFixed(startFmTile);
                } else if (!player.getMyLocation().equals(startFmTile)) {
                	walk.tileOnScreen(startFmTile);
                }
                wait(random(500, 700));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void getBestPlaceToFm() {
        if (player.isIdle()) {
            wait(random(100, 300));
        }
        if (!checkFromUrself()) {
            ArrayList<RSTile> chance = new ArrayList<RSTile>();
            ArrayList<ArrayList<RSTile>> chancesFireTiles = new ArrayList<ArrayList<RSTile>>();
            for (int i = 1; i < 28; i++) {
                for (RSTile fmTile : FireTiles) {
                    ArrayList<RSTile> setFiresTiles = new ArrayList<RSTile>();
                    boolean foundPlace = true;
                    for (int x = 0; x <= inventory.getCount(logID) - i; x++) {
                        final RSObject object = objects.getTopAt(new RSTile(fmTile.getX()
                                - x, fmTile.getY()));
                        if (object != null
                                && (object.getType() != 1
                                && (!contains(allowedOtherIDs, object.getID()) && !contains(
                                allowedTiles, new RSTile(fmTile.getX()
                                - x, fmTile.getY()))) || contains(
                                disAllowedTiles, new RSTile(fmTile.getX()
                                - x, fmTile.getY())))
                                || isInBank(new RSTile(fmTile.getX() - x,
                                fmTile.getY()), false)) {
                            foundPlace = false;
                        }
                        setFiresTiles.add(new RSTile(fmTile.getX() - x, fmTile.getY()));
                    }
                    if (foundPlace) {
                        chance.add(fmTile);
                        chancesFireTiles.add(setFiresTiles);
                    }
                }
                if (!chancesFireTiles.isEmpty() && !chance.isEmpty()) {
                    devLog(true, "Found FM Place");
                    int random = rrandom(0, chance.size() - 1);
                    thisFiresTiles = chancesFireTiles.get(random);
                    startFmTile = chance.get(random);
                    nextTile = chance.get(random);
                    if (!tileFullyOnScreen(startFmTile) || bank.isOpen()) {
                        walkToFixed(startFmTile, 3, 3);
                    } else if (!player.getMyLocation().equals(startFmTile)) {
                    	walk.tileOnScreen(startFmTile);
                    }
                    wait(random(500, 700));
                    walkToFixed(FireLocs);
                    break;
                } else {
                    devLog(false, "Failed to get FM Place, trying again with smaller firepath");
                }
            }
        }
    }

    private int rrandom(int min, int max) {
        if (max == 1) {
            return 1;
        }
        int random = random(min, max + 1);
        while (random >= max) {
            random -= 1;
        }
        if (random == -1) {
            return 0;
        }
        return random;
    }

    private void resetPaths() {
        devLog(true, "----Reset----");
        startFmTile = null;
        nextTile = null;
        thisFiresTiles.clear();
        lightFireTiles.clear();
        FMing = false;
        lastPos = null;
        locOnLight = null;
    }

    private ArrayList<RSObject> getAllObjectsByID(int... ids) {
        ArrayList<RSObject> cur = new ArrayList<RSObject>();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                RSObject o = objects.getTopAt(x + Bot.getClient().getBaseX(), y
                        + Bot.getClient().getBaseY());
                if (o != null) {
                    for (int id : ids) {
                        if (o.getID() == id) {
                            cur.add(o);
                        }
                    }
                }
            }
        }
        return cur;
    }

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatTime(long mstl) {
        long timeMillis = mstl;
        long time = timeMillis / 1000;
        String seconds = Integer.toString((int) (time % 60));
        String minutes = Integer.toString((int) ((time % 3600) / 60));
        String hours = Integer.toString((int) (time / 3600));
        for (int i = 0; i < 2; i++) {
            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }
        return hours + ":" + minutes + ":" + seconds;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Paint">

    private void drawBankItem(Graphics g, int... itemIDs) {
        if (bank.isOpen()) {
            for (int id : itemIDs) {
                final RSInterfaceChild r = bank.getItemByID(id);
                if (r != null) {
                    int color = id;
                    int c2 = id;
                    int c3 = id;
                    while (color > 255) {
                        color = color / 2;
                    }
                    while (c2 > 255) {
                        c2 = c2 / 3;
                    }
                    while (c3 > 255) {
                        c3 = c3 / 4;
                    }
                    g.setColor(new Color(color, c2, c3));
                    g.drawRoundRect(r.getAbsoluteX() - 5, r.getAbsoluteY() - 5,
                            r.getWidth() + 10, r.getHeight() + 10, 0, 0);
                }
            }
        }
    }

    private void drawNextTile(final Graphics g, final Color borderColor,
            final Color color) {
        if (nextTile != null) {
            final RSTile Myloc = nextTile;
            if (Myloc != null) {
                Calculations.tileToScreen(Myloc);
                final Point I = Calculations.tileToScreen(Myloc.getX(), Myloc.getY(), 0, 0, 0);
                final Point II = Calculations.tileToScreen(Myloc.getX() + 1,
                        Myloc.getY(), 0, 0, 0);
                final Point III = Calculations.tileToScreen(Myloc.getX(), Myloc.getY() + 1, 0, 0, 0);
                final Point IV = Calculations.tileToScreen(Myloc.getX() + 1,
                        Myloc.getY() + 1, 0, 0, 0);
                if (I.x != -1 && II.x != -1 && III.x != -1 && IV.x != -1
                        && I.y != -1 && II.y != -1 && III.y != -1 && IV.y != -1) {
                    player.getMine().getHeight();
                    g.setColor(borderColor);
                    g.drawPolygon(new int[]{III.x, IV.x, II.x, I.x},
                            new int[]{III.y, IV.y, II.y, I.y}, 4);
                    g.setColor(color);
                    g.fillPolygon(new int[]{III.x, IV.x, II.x, I.x},
                            new int[]{III.y, IV.y, II.y, I.y}, 4);
                }
                final Point tileOnMinimap = tile.toMiniMap(nextTile);
                if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                    g.setColor(Color.black);
                    g.fillRect(tileOnMinimap.x + 1, tileOnMinimap.y + 1, 3, 3);
                    g.setColor(Color.cyan);
                    g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 1, 1);
                }
            }
        }
    }

    private void drawNextTiles(final Graphics g, final Color borderColor,
            final Color color) {
        for (RSTile tile : thisFiresTiles) {
            if (tile != null
                    && (lightFireTiles != null && !lightFireTiles.contains(tile))
                    && (nextTile != null && !nextTile.equals(tile))
                    && (locOnLight != null && !locOnLight.equals(tile))) {
                if (!bank.isOpen() && tile != null) {
                    final RSTile Myloc = tile;
                    Calculations.tileToScreen(Myloc);
                    final Point I = Calculations.tileToScreen(Myloc.getX(), Myloc.getY(), 0, 0, 0);
                    final Point II = Calculations.tileToScreen(Myloc.getX() + 1, Myloc.getY(), 0, 0, 0);
                    final Point III = Calculations.tileToScreen(Myloc.getX(), Myloc.getY() + 1, 0, 0, 0);
                    final Point IV = Calculations.tileToScreen(Myloc.getX() + 1, Myloc.getY() + 1, 0, 0, 0);
                    if (I.x != -1 && II.x != -1 && III.x != -1 && IV.x != -1 && I.y != -1 && II.y != -1 && III.y != -1 && IV.y != -1) {
                        player.getMine().getHeight();
                        g.setColor(borderColor);
                        g.drawPolygon(new int[]{III.x, IV.x, II.x, I.x}, new int[]{III.y, IV.y, II.y, I.y}, 4);
                        g.setColor(color);
                        g.fillPolygon(new int[]{III.x, IV.x, II.x, I.x}, new int[]{III.y, IV.y, II.y, I.y}, 4);
                    }
                }
            }
        }
    }

    private void drawAllTiles(final Graphics g, final Color borderColor,
            final Color color) {
        if (lightFireTiles != null) {
            for (RSTile tile : lightFireTiles) {
                if (tile != null && !bank.isOpen()) {
                    final RSTile Myloc = tile;
                    Calculations.tileToScreen(Myloc);
                    final Point I = Calculations.tileToScreen(Myloc.getX(), Myloc.getY(), 0, 0, 0);
                    final Point II = Calculations.tileToScreen(Myloc.getX() + 1, Myloc.getY(), 0, 0, 0);
                    final Point III = Calculations.tileToScreen(Myloc.getX(), Myloc.getY() + 1, 0, 0, 0);
                    final Point IV = Calculations.tileToScreen(Myloc.getX() + 1, Myloc.getY() + 1, 0, 0, 0);
                    if (I.x != -1 && II.x != -1 && III.x != -1 && IV.x != -1 && I.y != -1 && II.y != -1 && III.y != -1 && IV.y != -1) {
                        player.getMine().getHeight();
                        g.setColor(borderColor);
                        g.drawPolygon(new int[]{III.x, IV.x, II.x, I.x}, new int[]{III.y, IV.y, II.y, I.y}, 4);
                        g.setColor(color);
                        g.fillPolygon(new int[]{III.x, IV.x, II.x, I.x}, new int[]{III.y, IV.y, II.y, I.y}, 4);
                    }
                }
            }
        }
    }

    private void drawOthersTiles(final Graphics g, final Color borderColor,
            final Color color) {
        final ArrayList<RSObject> fires = getAllObjectsByID(2732);
        for (RSObject o : fires) {
            if (o != null) {
                final RSTile tile = o.getLocation();
                if (tile != null && !lightFireTiles.contains(tile)) {
                    if (!bank.isOpen() && tile != null) {
                        final RSTile Myloc = tile;
                        Calculations.tileToScreen(Myloc);
                        final Point I = Calculations.tileToScreen(Myloc.getX(),
                                Myloc.getY(), 0, 0, 0);
                        final Point II = Calculations.tileToScreen(
                                Myloc.getX() + 1, Myloc.getY(), 0, 0, 0);
                        final Point III = Calculations.tileToScreen(Myloc.getX(), Myloc.getY() + 1, 0, 0, 0);
                        final Point IV = Calculations.tileToScreen(
                                Myloc.getX() + 1, Myloc.getY() + 1, 0, 0, 0);
                        if (I.x != -1 && II.x != -1 && III.x != -1
                                && IV.x != -1 && I.y != -1 && II.y != -1
                                && III.y != -1 && IV.y != -1) {
                            player.getMine().getHeight();
                            g.setColor(borderColor);
                            g.drawPolygon(new int[]{III.x, IV.x, II.x, I.x},
                                    new int[]{III.y, IV.y, II.y, I.y}, 4);
                            g.setColor(color);
                            g.fillPolygon(new int[]{III.x, IV.x, II.x, I.x},
                                    new int[]{III.y, IV.y, II.y, I.y}, 4);
                        }
                    }
                }
            }
        }
    }

    private void drawLightingTile(final Graphics g, final Color borderColor,
            final Color color) {
        if ((lightFireTiles != null && !lightFireTiles.isEmpty()) && locOnLight != null && !lightFireTiles.get(lightFireTiles.size() - 1).equals(locOnLight) && !locOnLight.equals(nextTile)) {
            if (locOnLight != null && !bank.isOpen()) {
                final RSTile Myloc = locOnLight;
                Calculations.tileToScreen(Myloc);
                final Point I = Calculations.tileToScreen(Myloc.getX(), Myloc.getY(), 0, 0, 0);
                final Point II = Calculations.tileToScreen(Myloc.getX() + 1,
                        Myloc.getY(), 0, 0, 0);
                final Point III = Calculations.tileToScreen(Myloc.getX(), Myloc.getY() + 1, 0, 0, 0);
                final Point IV = Calculations.tileToScreen(Myloc.getX() + 1,
                        Myloc.getY() + 1, 0, 0, 0);
                if (I.x != -1 && II.x != -1 && III.x != -1 && IV.x != -1
                        && I.y != -1 && II.y != -1 && III.y != -1 && IV.y != -1) {
                    player.getMine().getHeight();
                    g.setColor(borderColor);
                    g.drawPolygon(new int[]{III.x, IV.x, II.x, I.x},
                            new int[]{III.y, IV.y, II.y, I.y}, 4);
                    g.setColor(color);
                    g.fillPolygon(new int[]{III.x, IV.x, II.x, I.x},
                            new int[]{III.y, IV.y, II.y, I.y}, 4);
                }
                final Point tileOnMinimap = tile.toMiniMap(Myloc);
                if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                    g.setColor(Color.black);
                    g.fillRect(tileOnMinimap.x + 1, tileOnMinimap.y + 1, 3, 3);
                    g.setColor(new Color(255, 200, 0));
                    g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 1, 1);
                }
            }
        }
    }

    private void makeDotsOnMinimap(Graphics g) {
        if (ofire) {
            final ArrayList<RSObject> fires = getAllObjectsByID(2732);
            for (RSObject o : fires) {
                if (o != null) {
                    final RSTile t = o.getLocation();
                    if (t != null) {
                        final Point tileOnMinimap = tile.toMiniMap(t);
                        if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                            g.setColor(Color.black);
                            g.fillRect(tileOnMinimap.x + 1,
                                    tileOnMinimap.y + 1, 3, 3);
                            g.setColor(Color.green);
                            g.fillRect(tileOnMinimap.x + 2,
                                    tileOnMinimap.y + 2, 1, 1);
                        }
                    }
                }
            }
        }
        if (fireline2) {
            for (RSTile t : thisFiresTiles) {
                if (t != null) {
                    final Point tileOnMinimap = tile.toMiniMap(t);
                    if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                        g.setColor(Color.black);
                        g.fillRect(tileOnMinimap.x + 1, tileOnMinimap.y + 1, 3,
                                3);
                        g.setColor(Color.gray);
                        g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 1,
                                1);
                    }
                }
            }
        }
        if (fireline1 && startFmTile != null) {
            final Point tileOnMinimap = tile.toMiniMap(startFmTile);
            if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                g.setColor(Color.black);
                g.fillRect(tileOnMinimap.x + 1, tileOnMinimap.y + 1, 3, 3);
                g.setColor(Color.red);
                g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 1, 1);
            }
        }
        if (fireline1) {
            for (RSTile t : lightFireTiles) {
                if (tile != null) {
                    final Point tileOnMinimap = tile.toMiniMap(t);
                    if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                        g.setColor(Color.black);
                        g.fillRect(tileOnMinimap.x + 1, tileOnMinimap.y + 1, 3,
                                3);
                        g.setColor(Color.red);
                        g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 1,
                                1);
                    }
                }
            }
        }
        if (developer) {
            g.setColor(Color.red);
            for (RSTile t : disAllowedTiles) {
                if (t != null) {
                    final RSTile tileInTileFormat = t;
                    final Point tileOnMinimap = tile.toMiniMap(tileInTileFormat);
                    if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                        g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 2,
                                2);
                    }
                }
            }
            g.setColor(Color.green);
            for (RSTile t : allowedTiles) {
                if (t != null) {
                    final RSTile tileInTileFormat = t;
                    final Point tileOnMinimap = tile.toMiniMap(tileInTileFormat);
                    if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                        g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 2,
                                2);
                    }
                }
            }
            g.setColor(Color.black);
            if (BArea != null) {
                for (RSTile[] bXtiles : BArea.getTiles()) {
                    for (RSTile bTile : bXtiles) {
                        if (bTile != null) {
                            final RSTile tileInTileFormat = bTile;
                            final Point tileOnMinimap = tile.toMiniMap(tileInTileFormat);
                            if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                                g.fillRect(tileOnMinimap.x + 1,
                                        tileOnMinimap.y + 1, 4, 4);
                            }
                        }
                    }
                }
            }
            g.setColor(Color.orange);
            if (BankTile != null) {
                final RSTile tileInTileFormat = BankTile;
                final Point tileOnMinimap = tile.toMiniMap(tileInTileFormat);
                if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
                    g.fillRect(tileOnMinimap.x - 1, tileOnMinimap.y - 1, 5, 5);
                }
            }
            g.setColor(Color.cyan);
            for (RSTile t : FireTiles) {
                if (t != null) {
                    final RSTile tileInTileFormat2 = t;
                    final Point tileOnMinimap2 = tile.toMiniMap(tileInTileFormat2);
                    if (tileOnMinimap2.x != -1 && tileOnMinimap2.y != -1) {
                        g.fillRect(tileOnMinimap2.x + 1, tileOnMinimap2.y + 1,
                                1, 1);
                    }
                }
            }
            g.setColor(Color.orange);
            for (int id : allowedOtherIDs) {
                final ArrayList<RSObject> objects = getAllObjectsByID(id);
                for (RSObject object : objects) {
                    if (object != null) {
                        final RSTile tileInTileFormat3 = object.getLocation();
                        if (tileInTileFormat3 != null) {
                            final Point tileOnMinimap3 = tile.toMiniMap(tileInTileFormat3);
                            if (tileOnMinimap3.x != -1
                                    && tileOnMinimap3.y != -1) {
                                g.fillRect(tileOnMinimap3.x + 2,
                                        tileOnMinimap3.y + 2, 2, 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private void drawPaint(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        if (info) {
            if (startExp == 0 && game.isLoggedIn()) {
                startExp = skills.getCurrentXP(Constants.STAT_FIREMAKING);
            }
            if (startLevel == 0 && game.isLoggedIn()) {
                startLevel = skills.getRealLvl(Constants.STAT_FIREMAKING);
            }
            final int xpGained = skills.getCurrentXP(Constants.STAT_FIREMAKING)
                    - startExp;
            final int currLevel = skills.getRealLvl(Constants.STAT_FIREMAKING);
            final int lvls = skills.getRealLvl(Constants.STAT_FIREMAKING)
                    - startLevel;
            long millis = System.currentTimeMillis() - startTime;
            long hours = millis / (1000 * 60 * 60);
            millis -= hours * (1000 * 60 * 60);
            long minutes = millis / (1000 * 60);
            millis -= minutes * (1000 * 60);
            long seconds = millis / 1000;
            g.setColor(color1);
            g.fillRect(19, 18, 129, 107);
            g.setColor(color2);
            g.setStroke(stroke1);
            g.drawRect(19, 18, 129, 107);
            g.setFont(font1);
            g.setColor(color3);
            g.drawString(hours + ":" + minutes + ":" + seconds, 60, 39);
            g.drawString(Integer.toString(burned), 60, 77);
            g.drawString(Integer.toString(xpGained), 60, 112);
            g.drawImage(img1, 26, 26, null);
            g.drawImage(img2, 26, 52, null);
            g.drawImage(img3, 27, 99, null);
            g.setColor(color1);
            g.fillRect(19, 125, 129, 14);
            g.setColor(color2);
            g.drawRect(19, 125, 129, 14);
            g.setColor(color3);
            g.drawString("Additional Details", 47, 135);
            if (showAdditional) {
                float xpsec = 0;
                if ((minutes > 0 || hours > 0 || seconds > 0) && xpGained > 0) {
                    xpsec = ((float) xpGained)
                            / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
                }
                final float xpmin = xpsec * 60;
                final float xphour = xpmin * 60;
                final int burnHour = (int) ((burned) * 3600000D / (System.currentTimeMillis() - startTime));
                int timeToNextLevel = (int) ((skills.getXPToNextLvl(Constants.STAT_FIREMAKING) / xphour) * 3600000);
                g.setColor(color1);
                g.fillRect(19, 139, 187, 110);
                g.setColor(color2);
                g.drawRect(19, 139, 187, 110);
                g.setColor(color3);
                g.drawString("Exp per hour: " + Math.round(xphour), 26, 152);
                g.drawString("Fires per hour: " + Integer.toString(burnHour),
                        26, 164);
                if (!video) {
                    g.drawString("Current Level: "
                            + Integer.toString(currLevel), 26, 176);
                } else {
                    g.drawString("Current Level: **", 26, 176);
                }
                g.drawString("Levels gained: " + Integer.toString(lvls), 27,
                        188);
                g.drawString("Time to next level: "
                        + formatTime(timeToNextLevel), 26, 200);
                g.drawString("Exp to next level: "
                        + Integer.toString(skills.getXPToNextLvl(Constants.STAT_FIREMAKING)),
                        27, 212);
                g.fillRect(28, 230, 162, 11);
                g.setColor(color2);
                g.drawRect(28, 230, 162, 11);
                g.setColor(color4);
                g.fillRect(
                        28,
                        230,
                        (skills.getPercentToNextLvl(Constants.STAT_FIREMAKING) * 162) / 100,
                        11);
                g.setColor(color2);
                g.drawRect(
                        28,
                        230,
                        (skills.getPercentToNextLvl(Constants.STAT_FIREMAKING) * 162) / 100,
                        11);
                g.setColor(color3);
                if (!video) {
                    g.drawString(Integer.toString(skills.getPercentToNextLvl(Constants.STAT_FIREMAKING))
                            + "% to level " + Integer.toString(currLevel + 1),
                            28, 224);
                } else {
                    g.drawString(Integer.toString(skills.getPercentToNextLvl(Constants.STAT_FIREMAKING))
                            + "% to level **", 28, 224);
                }
            }
        }
        if (logo) {
            g.drawImage(img5, 152, 40, null);
        }
        if (fmcape) {
            g.drawImage(img4, 449, 14, null);
        }
    }

    public void drawMouse(Graphics g) {
        for (int i = 0; i < mouseHistory.size() - 1; i++) {
            g.setColor(new Color(255, 130, 30, 255 / mouseHistory.size() * i));
            Point currentPoint = mouseHistory.get(i);
            Point nextPoint = mouseHistory.get(i + 1);
            g.drawLine(currentPoint.x, currentPoint.y, nextPoint.x, nextPoint.y);
        }
        int px = Bot.getClient().getMouse().getX();
        int py = Bot.getClient().getMouse().getY();
        boolean pressed = false;
        if (Bot.getClient().getMouse().getPressTime() + 250 > System.currentTimeMillis() || Bot.getClient().getMouse().isPressed()) {
            pressed = true;
        }
        g.setColor((!pressed) ? Color.red : Color.blue);
        g.fillOval(px - 3, py - 3, 6, 6);
        g.setColor((!pressed) ? Color.orange : Color.cyan);
        g.fillOval(px - 2, py - 2, 4, 4);
    }

    @Override
    public void onRepaint(Graphics g) {
        if (video) {
            drawHiding(g, Color.black);
        }
        if (!starting) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (BankTile != null) {
                makeDotsOnMinimap(g);
                if (fireline1) {
                    drawAllTiles(g, Color.black, new Color(128, 0, 0, 75));
                }
                if (ofire) {
                    drawOthersTiles(g, Color.black, new Color(40, 75, 0, 75));
                }
                if (nexttile) {
                    drawNextTile(g, Color.black, new Color(0, 255, 255, 75));
                }
                if (fireline2) {
                    drawNextTiles(g, Color.black, new Color(175, 175, 175, 75));
                }
                if (lighttile) {
                    drawLightingTile(g, Color.black, new Color(255, 200, 0, 75));
                }
            }
            drawBankItem(g, tinderboxID, logID);
            drawPaint(g);
            drawMouse(g);
        }
    }

    private void drawHiding(Graphics g, Color c) {
        g.setColor(c);
        RSInterface HPIf = iface.get(748);
        if (HPIf.isValid()) {
            RSInterfaceChild HPIc = HPIf.getChild(8);
            g.fillRect(HPIc.getAbsoluteX(), HPIc.getAbsoluteY(), HPIc.getWidth(), HPIc.getHeight());
        }
        RSInterface PIf = iface.get(749);
        if (PIf.isValid()) {
            RSInterfaceChild PIc = PIf.getChild(4);
            g.fillRect(PIc.getAbsoluteX(), PIc.getAbsoluteY(), PIc.getWidth(),
                    PIc.getHeight());
        }
        RSInterface CIf = iface.get(137);
        if (CIf.isValid()) {
            RSInterfaceChild CIc = CIf.getChild(54);
            g.fillRect(CIc.getAbsoluteX(), CIc.getAbsoluteY(), 121, 15);
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="GUI">

    private class GribonnsFiremakerGUI extends JFrame {

        private static final long serialVersionUID = 8454772110219551612L;

        private GribonnsFiremakerGUI() {
            initComponents();
        }

        private void button1ActionPerformed(ActionEvent e) {
            final String log = comboBox2.getSelectedItem().toString();
            final String loc = comboBox1.getSelectedItem().toString();
            logname = log;
            if (log.equals("Normal logs")) {
                logname = "Logs";
                logID = normalLogID;
            } else if (log.equals("Oak logs")) {
                logID = oakLogID;
            } else if (log.equals("Willow logs")) {
                logID = willowLogID;
            } else if (log.equals("Maple logs")) {
                logID = mapleLogID;
            } else if (log.equals("Yew logs")) {
                logID = yewLogID;
            } else if (log.equals("Magic logs")) {
                logID = magicLogID;
            }
            command = "Use Tinderbox -> " + logname;
            if (loc.equals("Falador")) {
                FireTiles = FireTilesF;
                FireLocs = FireLocsF;
                BankTile = BankTileF;
                BArea = BAreaF;
            } else if (loc.equals("Edgeville")) {
                FireTiles = FireTilesE;
                FireLocs = FireLocsE;
                BankTile = BankTileE;
                BArea = BAreaE;
            } else if (loc.equals("Fist of Guthix")) {
                FireTiles = FireTilesFoG;
                FireLocs = FireLocsFoG;
                BankTile = BankTileFoG;
            } else if (loc.equals("Varrock (West)")) {
                FireTiles = FireTilesVW;
                FireLocs = FireLocsVW;
                BankTile = BankTileVW;
                BArea = BAreaVW;
            } else if (loc.equals("Varrock (East)")) {
                FireTiles = FireTilesVE;
                FireLocs = FireLocsVE;
                BankTile = BankTileVE;
                BArea = BAreaVE;
            } else if (loc.equals("Grand Exchange (North)")) {
                FireTiles = FireTilesGENorth;
                FireLocs = FireLocsGENorth;
                BankTile = BankTileGENorth;
            } else if (loc.equals("Grand Exchange (South)")) {
                FireTiles = FireTilesGESouth;
                FireLocs = FireLocsGESouth;
                BankTile = BankTileGESouth;
            } else if (loc.equals("Seers Village")) {
                FireTiles = FireTilesS;
                FireLocs = FireLocsS;
                BankTile = BankTileS;
                BArea = BAreaS;
            } else if (loc.equals("Yanille")) {
                FireTiles = FireTilesY;
                FireLocs = FireLocsY;
                BankTile = BankTileY;
                BArea = BAreaY;
            }
            info = checkBox1.isSelected();
            logo = checkBox2.isSelected();
            fmcape = checkBox3.isSelected();
            nexttile = checkBox8.isSelected();
            lighttile = checkBox9.isSelected();
            fireline1 = checkBox4.isSelected();
            fireline2 = checkBox6.isSelected();
            ofire = checkBox5.isSelected();
            developer = checkBox7.isSelected();
            video = checkBox10.isSelected();
            mouseS = slider1.getValue();
            ABRate = slider2.getValue();
            guiWait = false;
            dispose();
        }

        private void button2ActionPerformed(ActionEvent e) {
            guiWait = false;
            guiExit = true;
            dispose();
        }

        private void button10ActionPerformed(ActionEvent e) {
            checkBox1.setSelected(false);
            checkBox2.setSelected(false);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
            checkBox5.setSelected(false);
            checkBox6.setSelected(false);
            checkBox7.setSelected(false);
            checkBox8.setSelected(false);
            checkBox9.setSelected(false);
        }

        private void button5ActionPerformed(ActionEvent e) {
            checkBox1.setSelected(true);
            checkBox2.setSelected(true);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
            checkBox5.setSelected(false);
            checkBox6.setSelected(false);
            checkBox7.setSelected(false);
            checkBox8.setSelected(false);
            checkBox9.setSelected(false);
        }

        private void button8ActionPerformed(ActionEvent e) {
            checkBox1.setSelected(true);
            checkBox2.setSelected(true);
            checkBox3.setSelected(true);
            checkBox4.setSelected(false);
            checkBox5.setSelected(false);
            checkBox6.setSelected(false);
            checkBox7.setSelected(false);
            checkBox8.setSelected(true);
            checkBox9.setSelected(true);
        }

        private void button6ActionPerformed(ActionEvent e) {
            checkBox1.setSelected(true);
            checkBox2.setSelected(true);
            checkBox3.setSelected(true);
            checkBox4.setSelected(true);
            checkBox5.setSelected(false);
            checkBox6.setSelected(false);
            checkBox7.setSelected(false);
            checkBox8.setSelected(true);
            checkBox9.setSelected(true);
        }

        private void button7ActionPerformed(ActionEvent e) {
            checkBox1.setSelected(true);
            checkBox2.setSelected(true);
            checkBox3.setSelected(true);
            checkBox4.setSelected(true);
            checkBox5.setSelected(true);
            checkBox6.setSelected(true);
            checkBox7.setSelected(false);
            checkBox8.setSelected(true);
            checkBox9.setSelected(true);
        }

        private void button9ActionPerformed(ActionEvent e) {
            checkBox1.setSelected(true);
            checkBox2.setSelected(true);
            checkBox3.setSelected(false);
            checkBox4.setSelected(false);
            checkBox5.setSelected(false);
            checkBox6.setSelected(false);
            checkBox7.setSelected(true);
            checkBox8.setSelected(false);
            checkBox9.setSelected(false);
        }

        private void initComponents() {
            button1 = new JButton();
            button2 = new JButton();
            tabbedPane1 = new JTabbedPane();
            panel1 = new JPanel();
            comboBox2 = new JComboBox();
            comboBox1 = new JComboBox();
            label1 = new JLabel();
            label2 = new JLabel();
            slider1 = new JSlider();
            label3 = new JLabel();
            label4 = new JLabel();
            label5 = new JLabel();
            label6 = new JLabel();
            slider2 = new JSlider();
            label7 = new JLabel();
            label8 = new JLabel();
            panel2 = new JPanel();
            checkBox1 = new JCheckBox();
            checkBox2 = new JCheckBox();
            checkBox3 = new JCheckBox();
            checkBox4 = new JCheckBox();
            checkBox5 = new JCheckBox();
            checkBox6 = new JCheckBox();
            checkBox8 = new JCheckBox();
            checkBox9 = new JCheckBox();
            button5 = new JButton();
            button6 = new JButton();
            button7 = new JButton();
            button8 = new JButton();
            button9 = new JButton();
            checkBox7 = new JCheckBox();
            button10 = new JButton();
            checkBox10 = new JCheckBox();

            // ======== this ========
            setTitle("Gribonn's Firemaker settings");
            setResizable(false);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setAlwaysOnTop(true);
            Container contentPane = getContentPane();
            contentPane.setLayout(null);

            // ---- button1 ----
            button1.setText("Start");
            button1.setSelectedIcon(null);
            button1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    button1ActionPerformed(e);
                }
            });
            contentPane.add(button1);
            button1.setBounds(7, 255, 375, button1.getPreferredSize().height);

            // ---- button2 ----
            button2.setText("Cancel");
            button2.setSelectedIcon(null);
            button2.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    button1ActionPerformed(e);
                    button2ActionPerformed(e);
                }
            });
            contentPane.add(button2);
            button2.setBounds(7, 280, 375, 23);

            // ======== tabbedPane1 ========
            {

                // ======== panel1 ========
                {
                    panel1.setLayout(null);

                    // ---- comboBox2 ----
                    comboBox2.setModel(new DefaultComboBoxModel(new String[]{
                                "Get log type automatically", "Normal logs",
                                "Oak logs", "Willow logs", "Maple logs",
                                "Yew logs", "Magic logs"}));
                    comboBox2.setFont(comboBox2.getFont().deriveFont(
                            comboBox2.getFont().getSize() + 5f));
                    panel1.add(comboBox2);
                    comboBox2.setBounds(115, 10, 260, 40);

                    // ---- comboBox1 ----
                    comboBox1.setModel(new DefaultComboBoxModel(new String[]{
                                "Get location automatically", "Falador",
                                "Fist of Guthix", "Edgeville", "Varrock (West)",
                                "Varrock (East)", "Grand Exchange (North)",
                                "Grand Exchange (South)", "Seers Village",
                                "Yanille"}));
                    comboBox1.setFont(comboBox1.getFont().deriveFont(
                            comboBox1.getFont().getSize() + 5f));
                    panel1.add(comboBox1);
                    comboBox1.setBounds(115, 55, 260, 40);

                    // ---- label1 ----
                    label1.setText("Log type:");
                    label1.setFont(label1.getFont().deriveFont(
                            label1.getFont().getStyle() & ~Font.BOLD,
                            label1.getFont().getSize() + 9f));
                    panel1.add(label1);
                    label1.setBounds(new Rectangle(new Point(15, 16), label1.getPreferredSize()));

                    // ---- label2 ----
                    label2.setText("Location:");
                    label2.setFont(label2.getFont().deriveFont(
                            label2.getFont().getStyle() & ~Font.BOLD,
                            label2.getFont().getSize() + 9f));
                    panel1.add(label2);
                    label2.setBounds(new Rectangle(new Point(15, 61), label2.getPreferredSize()));

                    // ---- slider1 ----
                    slider1.setPaintTicks(true);
                    slider1.setPaintLabels(true);
                    slider1.setMajorTickSpacing(1);
                    slider1.setMaximum(10);
                    slider1.setValue(5);
                    panel1.add(slider1);
                    slider1.setBounds(145, 95, 235,
                            slider1.getPreferredSize().height);

                    // ---- label3 ----
                    label3.setText("Fast");
                    panel1.add(label3);
                    label3.setBounds(new Rectangle(new Point(150, 140), label3.getPreferredSize()));

                    // ---- label4 ----
                    label4.setText("Slow");
                    panel1.add(label4);
                    label4.setBounds(new Rectangle(new Point(355, 140), label4.getPreferredSize()));

                    // ---- label5 ----
                    label5.setText("Mouse Speed:");
                    label5.setFont(label5.getFont().deriveFont(
                            label5.getFont().getStyle() & ~Font.BOLD,
                            label5.getFont().getSize() + 9f));
                    panel1.add(label5);
                    label5.setBounds(new Rectangle(new Point(15, 106), label5.getPreferredSize()));

                    // ---- label6 ----
                    label6.setText("Anti-ban rate:");
                    label6.setFont(label6.getFont().deriveFont(
                            label6.getFont().getStyle() & ~Font.BOLD,
                            label6.getFont().getSize() + 9f));
                    panel1.add(label6);
                    label6.setBounds(new Rectangle(new Point(15, 160), label6.getPreferredSize()));

                    // ---- slider2 ----
                    slider2.setPaintTicks(true);
                    slider2.setPaintLabels(true);
                    slider2.setMajorTickSpacing(10);
                    slider2.setMinorTickSpacing(1);
                    slider2.setValue(30);
                    panel1.add(slider2);
                    slider2.setBounds(140, 155, 240,
                            slider2.getPreferredSize().height);

                    // ---- label7 ----
                    label7.setText("Rapid");
                    panel1.add(label7);
                    label7.setBounds(new Rectangle(new Point(145, 200), label7.getPreferredSize()));

                    // ---- label8 ----
                    label8.setText("Rare");
                    panel1.add(label8);
                    label8.setBounds(new Rectangle(new Point(355, 200), label8.getPreferredSize()));
                }
                tabbedPane1.addTab("Script", panel1);

                // ======== panel2 ========
                {
                    panel2.setLayout(null);

                    // ---- checkBox1 ----
                    checkBox1.setText("Info");
                    checkBox1.setSelected(true);
                    panel2.add(checkBox1);
                    checkBox1.setBounds(15, 10,
                            checkBox1.getPreferredSize().width, 20);

                    // ---- checkBox2 ----
                    checkBox2.setText("Logo");
                    checkBox2.setSelected(true);
                    panel2.add(checkBox2);
                    checkBox2.setBounds(15, 30,
                            checkBox2.getPreferredSize().width, 20);

                    // ---- checkBox3 ----
                    checkBox3.setText("Guy with fm cape");
                    panel2.add(checkBox3);
                    checkBox3.setBounds(15, 50,
                            checkBox3.getPreferredSize().width, 20);

                    // ---- checkBox4 ----
                    checkBox4.setText("Fireline (lighted tiles)");
                    panel2.add(checkBox4);
                    checkBox4.setBounds(15, 110,
                            checkBox4.getPreferredSize().width, 20);

                    // ---- checkBox5 ----
                    checkBox5.setText("Others fire tiles");
                    panel2.add(checkBox5);
                    checkBox5.setBounds(15, 150,
                            checkBox5.getPreferredSize().width, 20);

                    // ---- checkBox6 ----
                    checkBox6.setText("Fireline (not lighted tiles)");
                    panel2.add(checkBox6);
                    checkBox6.setBounds(15, 130,
                            checkBox6.getPreferredSize().width, 20);

                    // ---- checkBox8 ----
                    checkBox8.setText("Next tile");
                    panel2.add(checkBox8);
                    checkBox8.setBounds(15, 70,
                            checkBox8.getPreferredSize().width, 20);

                    // ---- checkBox9 ----
                    checkBox9.setText("Lighting tile");
                    panel2.add(checkBox9);
                    checkBox9.setBounds(15, 90,
                            checkBox9.getPreferredSize().width, 20);

                    // ---- button5 ----
                    button5.setText("Simple");
                    button5.setForeground(new Color(0, 206, 255));
                    button5.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button5ActionPerformed(e);
                            button5ActionPerformed(e);
                        }
                    });
                    panel2.add(button5);
                    button5.setBounds(240, 60, 135,
                            button5.getPreferredSize().height);

                    // ---- button6 ----
                    button6.setText("Semi-advanced");
                    button6.setForeground(new Color(229, 0, 255));
                    button6.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button6ActionPerformed(e);
                        }
                    });
                    panel2.add(button6);
                    button6.setBounds(240, 110, 135,
                            button6.getPreferredSize().height);

                    // ---- button7 ----
                    button7.setText("Advanced");
                    button7.setForeground(Color.red);
                    button7.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button7ActionPerformed(e);
                        }
                    });
                    panel2.add(button7);
                    button7.setBounds(240, 135, 135,
                            button7.getPreferredSize().height);

                    // ---- button8 ----
                    button8.setText("Cool");
                    button8.setForeground(new Color(0, 40, 255));
                    button8.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button8ActionPerformed(e);
                        }
                    });
                    panel2.add(button8);
                    button8.setBounds(240, 85, 135,
                            button8.getPreferredSize().height);

                    // ---- button9 ----
                    button9.setText("Developer");
                    button9.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button9ActionPerformed(e);
                        }
                    });
                    panel2.add(button9);
                    button9.setBounds(240, 160, 135,
                            button9.getPreferredSize().height);

                    // ---- checkBox7 ----
                    checkBox7.setText("Developer (also logs stuff)");
                    panel2.add(checkBox7);
                    checkBox7.setBounds(15, 170,
                            checkBox7.getPreferredSize().width, 20);

                    // ---- button10 ----
                    button10.setText("Un-check all");
                    button10.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            button10ActionPerformed(e);
                        }
                    });
                    panel2.add(button10);
                    button10.setBounds(240, 35, 135, button10.getPreferredSize().height);

                    // ---- checkBox10 ----
                    checkBox10.setText("Video mode");
                    panel2.add(checkBox10);
                    checkBox10.setBounds(15, 190, 153, 20);

                    { // compute preferred size
                        Dimension preferredSize = new Dimension();
                        for (int i = 0; i < panel2.getComponentCount(); i++) {
                            Rectangle bounds = panel2.getComponent(i).getBounds();
                            preferredSize.width = Math.max(bounds.x
                                    + bounds.width, preferredSize.width);
                            preferredSize.height = Math.max(bounds.y
                                    + bounds.height, preferredSize.height);
                        }
                        Insets insets = panel2.getInsets();
                        preferredSize.width += insets.right;
                        preferredSize.height += insets.bottom;
                        panel2.setMinimumSize(preferredSize);
                        panel2.setPreferredSize(preferredSize);
                    }
                }
                tabbedPane1.addTab("Paint", panel2);

            }
            contentPane.add(tabbedPane1);
            tabbedPane1.setBounds(0, 0, 390, 250);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for (int i = 0; i < contentPane.getComponentCount(); i++) {
                    Rectangle bounds = contentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width,
                            preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height,
                            preferredSize.height);
                }
                Insets insets = contentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                contentPane.setMinimumSize(preferredSize);
                contentPane.setPreferredSize(preferredSize);
            }
            setSize(395, 335);
            setLocationRelativeTo(getOwner());
            // GEN-END:initComponents
        }
        // GEN-BEGIN:variables
        private JTabbedPane tabbedPane1;
        private JPanel panel1;
        private JPanel panel2;
        private JComboBox comboBox2;
        private JComboBox comboBox1;
        private JSlider slider1;
        private JSlider slider2;
        private JLabel label1;
        private JLabel label2;
        private JLabel label3;
        private JLabel label4;
        private JLabel label5;
        private JLabel label6;
        private JLabel label7;
        private JLabel label8;
        private JCheckBox checkBox1;
        private JCheckBox checkBox2;
        private JCheckBox checkBox3;
        private JCheckBox checkBox4;
        private JCheckBox checkBox5;
        private JCheckBox checkBox6;
        private JCheckBox checkBox7;
        private JCheckBox checkBox8;
        private JCheckBox checkBox9;
        private JCheckBox checkBox10;
        private JButton button1;
        private JButton button2;
        private JButton button5;
        private JButton button6;
        private JButton button7;
        private JButton button8;
        private JButton button9;
        private JButton button10;
        // JFormDesigner - End of variables declaration //GEN-END:variables
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Listeners">

    @Override
    public void mouseClicked(MouseEvent e) {
        if (buttonRect.contains(e.getPoint())) {
            showAdditional = !showAdditional;
        }
    }


    @Override
    public void messageReceived(MessageEvent e) {
        final String msg = e.getMessage().toLowerCase();
        if (msg.contains("you can't")) {
            devLog(false, "Cannot make a fire there", "Picking up current log");
            pickupLog = true;
            resetPaths();
        } else if (msg.contains("logs begin to burn")) {
            if (lightFireTiles == null) {
                lightFireTiles = new ArrayList<RSTile>();
            }
            if (thisFiresTiles != null && !thisFiresTiles.isEmpty() && lightFireTiles.size() < thisFiresTiles.size() && thisFiresTiles.get(lightFireTiles.size()) != null) {
                lightFireTiles.add(thisFiresTiles.get(lightFireTiles.size()));
            }
            burned++;
        }
    }

    @Override
    public int loop() {
        setNewMouseSpeed();
        if (mainFailsafe() == -1) {
            if (canFM()) {
                fm();
            } else if (canBank()) {
                bank();
            }
        }
        return random(200, 400);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Classes">
    private class antiban implements Runnable {

        @Override
        public void run() {
            int randomNumber = random(0, ABRate + 2);
            if (randomNumber == 0) {
                if (ABRate > 15) {
                    try {
                        wait(random(0, 1000));
                    } catch (Exception ex) {
                    }
                }
                devLog(true, "Performing antiban: Camera rotation");
                camera.setRotation(random(0, 361));
            }
        }
    }

    private class mouseLocationGetter implements Runnable {

        @Override
        public void run() {
            while (isActive) {
                try {
                    Point mousePoint = new Point(Bot.getClient().getMouse().getX(), Bot.getClient().getMouse().getY());
                    mouseHistory.add(mousePoint);
                    if (mouseHistory.size() > mouseTail) {
                        mouseHistory.remove(0);
                    }
                    Thread.sleep(25);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Others">

    @Override
    protected int getMouseSpeed() {
        return currMouseS;
    }

    @Override
    public boolean onStart(Map<String, String> args) {
        startTime = System.currentTimeMillis();
        starting = true;
        gui = new GribonnsFiremakerGUI();
        gui.setVisible(true);
        while (guiWait) {
            wait(100);
        }
        starting = false;
        devLog(true, "Script started");
        new Thread(new mouseLocationGetter()).start();
        return !guiExit;
    }

    @Override
    public void onFinish() {
        starting = true;
        devLog(true, "Script finished");
    }

    private void devLog(boolean positive, String... s) {
        if (developer) {
            if (positive) {
                for (String st : s) {
                    log("Developer: " + st);
                }
            } else {
                for (String st : s) {
                    log.warning("Developer: " + st);
                }
            }
        }
    }
    // </editor-fold>

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
