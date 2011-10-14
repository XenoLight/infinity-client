import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.rsbot.client.LDModel;
import org.rsbot.client.RSAnimable;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Bank;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;

@ScriptManifest(authors = { "Henry" },
category = "Mining",
        name = "Henry's Miner",
        version = 1.00,
        description = "<html><style type='text/css'>"
        + "body {background:url('http://lazygamerz.org/client/images/back_2.png') repeat}"
        + "</style><html><head><center><img src=\"http://lazygamerz.org/client/images/logo.png\">"
        + "<h1><center><font color=#FFFFFF>"
        + "Henry's Miner by; Henry"
        + "</center></font color></h1>"
        + "</head><br><body>"
        + "<center><table border=0 cellpadding=1 cellspacing=1 style=border-collapse:collapse width=80% id=AutoNumber1 bgcolor=CCCCCC>"
        + "<td width=90% align=justify>"
        + "<font size=2><center><font color=#0000FF>How to set up and start this script.</font></font size></center>"
        + "<font size=3>Username & Password filed in GUI is for my website login - NOT FOR YOUR RUNESCAPE USERNAME & PASSWORD!!!<br>"
        + "<center><font color=#0000FF>Good luck and bot safe</font></center></font size>"
        + "</td></tr></table><br />"
        )
        
public class henrysMiner6 extends Script implements PaintListener {
	// AUTORESPONDERS ANSWERS AND QUESTIONS
	public String[] lvlQ = { "mining lvls", "lvls?", "mining lvl",
			"mining levels", "levels?", "mining level",
			"what is your level in mining", "whats your level in mining",
			"what is your mining lvl", "whats your mining lvl",
			"what is your mining level", "whats your mining level" };
	public String[] lvlA = { "My mining lvl is ", " " };
	public String[] heyQ = { "hey", "hay", "hello", "hi" };
	public String[] heyA = { "hey", "hay", "hello", "hi" };
	// Mining ID's
	public int[] currentOreID = { 9717, 9718 };
	private RSObject currentOre = null;
	private int currentOreInventoryID = 440;
	private int currentOrePrice = 0;
	public RSTile[] currentPath = { new RSTile(3047, 3236),
			new RSTile(3038, 3236), new RSTile(3030, 3236),
			new RSTile(3022, 3242), new RSTile(3013, 3242),
			new RSTile(3004, 3242), new RSTile(2998, 3240),
			new RSTile(2992, 3236), new RSTile(2985, 3233),
			new RSTile(2979, 3234) };
	public RSTile[] currentPath2 = { new RSTile(3047, 3236),
			new RSTile(3038, 3236), new RSTile(3030, 3236),
			new RSTile(3022, 3242), new RSTile(3013, 3242),
			new RSTile(3004, 3242), new RSTile(2998, 3240),
			new RSTile(2992, 3236), new RSTile(2985, 3233),
			new RSTile(2979, 3234) };
	public RSArea[] currentAreas = {
			new RSArea(new RSTile(2980, 3232), new RSTile(2983, 3235)),
			new RSArea(new RSTile(2967, 3235), new RSTile(2972, 3243)) };
	public RSArea currentMineArea = new RSArea(new RSTile(2966, 3230),
			new RSTile(2989, 3251));
	public RSArea currentBankArea = new RSArea(new RSTile(3046, 3235),
			new RSTile(3049, 3238));
	public RSArea[] currentLadderAreas = {
			new RSArea(new RSTile(3016, 3336), new RSTile(3022, 3342)),
			new RSArea(new RSTile(3016, 9736), new RSTile(3022, 9742)) };
	ArrayList<RSTile> lastRocks = new ArrayList<RSTile>();
	public int maxPlayers = 12;
	public RSArea selectedArea = null;
	public int failSafe = 0;
	public boolean mouseMoved = false;
	// GUI stuff
	public boolean yesMouse = true;
	public boolean isDung = true;
	public boolean hugePlace = true;
	public boolean worldHoping = true;
	public boolean BankStuff = true;
	public boolean isDepositBox = false;
	public boolean isLadder = true;
	public boolean dropGems = false;
	public int ladderD = 2113;
	public int ladderU = 6226;
	public int switching = 0;
	private final File Sf = new File(
			GlobalConfiguration.Paths.getSettingsDirectory() + File.separator
					+ "henrysMinerSettings.dat");
	// Ores
	public int[] ironOreID = { 14914, 14913, 9719, 9717, 9718, 31073, 31071, 31072, 11956,
			11955, 11954, 37307, 37309, 37308, 5775, 5773, 5774, 21281, 21282 };
	private int ironOreInventoryID = 440;
	public int[] coalOreID = { 5770, 5771, 5772, 31069, 31068, 31070, 32426,
			32427, 32428, 11930, 11931, 11932, 21287, 3032, 3233, 2097, 2096 };
	private int coalOreInventoryID = 453;
	public int[] silverOreID = { 11950, 11948, 11949, 37304, 37305, 37306 };
	private int silverOreInventoryID = 442;
	public int[] clayOreID = { 11504, 11503, 11505, 9711, 9713, 15503, 15505 };
	private int clayOreInventoryID = 434;
	public int[] addyOreID = { 31083, 31085, 32437, 32436, 32435, 11939, 11941,
			5782, 5783, 5782, 5783, 3273, 3040, 29233, 29235 };
	private int addyOreInventoryID = 449;
	public int[] copperOreID = { 31080, 31081, 31082, 9708, 9709, 9710, 11960,
			11961, 11962, 11938, 11937, 5779, 5780, 5781, 14906, 14907 };
	private int copperOreInventoryID = 436;
	public int[] goldOreID = { 31065, 31066, 9722, 9720, 37301, 37310, 37312,
			5768, 5769, 2098, 2099, 5771, 9720, 9722, 34977, 34976, 45067, 45068, 2109, 2108 };
	private int goldOreInventoryID = 444;
	public int[] mithrilOreID = { 31088, 31086, 32439, 32438, 32440, 32439,
			11944, 11943, 11942, 5786, 5784, 5785, 3280, 3041 };
	private int mithrilOreInventoryID = 447;
	public int[] tinOreID = { 31078, 31077, 31079, 9714, 9716, 11957, 11959,
			11958, 11933, 11934, 11935, 5776, 5777, 5778 };
	private int tinOreInventoryID = 438;

	public int[] pickaxes = { 1269, 1275, 1265, 1273, 1267, 1271, 13661, 561,
			15259 };
	public int[] gems = { 1621, 1617, 1619, 1623 };
	public int[] pickaxesandgems = { 1269, 1621, 1617, 1619, 1623, 1275, 1265,
			1273, 1267, 1271, 13661, 15259 };
	public int Sv = 2;
	public ArrayList<Integer> usedWorlds = new ArrayList<Integer>();
	//
	// Locations
	//
	// Rimmington stuff
	public RSTile[] faladorBankToRimmington = { new RSTile(3012, 3355),
			new RSTile(3007, 3349), new RSTile(3007, 3340),
			new RSTile(3006, 3331), new RSTile(3007, 3322),
			new RSTile(3005, 3312), new RSTile(3002, 3304),
			new RSTile(2994, 3295), new RSTile(2989, 3286),
			new RSTile(2985, 3277), new RSTile(2980, 3270),
			new RSTile(2978, 3262), new RSTile(2977, 3253),
			new RSTile(2974, 3244), new RSTile(2975, 3237) };
	public RSTile[] portSarimToRimmington = { new RSTile(3047, 3236),
			new RSTile(3038, 3236), new RSTile(3030, 3236),
			new RSTile(3022, 3242), new RSTile(3013, 3242),
			new RSTile(3004, 3242), new RSTile(2998, 3240),
			new RSTile(2992, 3236), new RSTile(2985, 3233),
			new RSTile(2979, 3234) };
	public RSArea faladorBankArea = new RSArea(new RSTile(3009, 3354),
			new RSTile(3018, 3358));
	public RSArea porSarimDepositBoxArea = new RSArea(new RSTile(3046, 3235),
			new RSTile(3049, 3238));
	public RSArea rimmingtonMineArea = new RSArea(new RSTile(2966, 3230),
			new RSTile(2989, 3251));
	public RSArea[] rimmingtonIronAreas = {
			new RSArea(new RSTile(2980, 3232), new RSTile(2983, 3235)),
			new RSArea(new RSTile(2967, 3235), new RSTile(2972, 3243)) };
	public RSArea[] rimmingtonGoldAreas = { new RSArea(new RSTile(2974, 3232),
			new RSTile(2978, 3235)) };
	public RSArea[] rimmingtonTinAreas = { new RSArea(new RSTile(2983, 3234),
			new RSTile(2988, 3238)) };
	public RSArea[] rimmingtonCopperAreas = { new RSArea(
			new RSTile(2975, 3244), new RSTile(2981, 3249)) };

	// Guild stuff
	public RSTile[] faladorBankToGuildEntrance = { new RSTile(3012, 3355),
			new RSTile(3018, 3361), new RSTile(3022, 3355),
			new RSTile(3026, 3350), new RSTile(3029, 3344),// 30941 Climb-up
			new RSTile(3028, 3337), new RSTile(3020, 3338) };// 2113 Climb-down
	public RSTile[] guildEntranceToGuild = { new RSTile(3019, 9737),
			new RSTile(3025, 9739), new RSTile(3034, 9737),
			new RSTile(3040, 9737) };
	public RSArea guildMineArea = new RSArea(new RSTile(3026, 9730),
			new RSTile(3056, 9749));
	public RSArea[] guildAreas = {
			new RSArea(new RSTile(3041, 9730), new RSTile(3056, 9749)),
			new RSArea(new RSTile(3026, 9730), new RSTile(3041, 9743)) };
	public RSArea[] guildLadderAreas = {
			new RSArea(new RSTile(3016, 3336), new RSTile(3022, 3342)),
			new RSArea(new RSTile(3015, 9737), new RSTile(3024, 9741)) };
	// Dwarven Stuff
	public RSTile[] faladorBankToDwarvenMineEntrance = {
			new RSTile(3013, 3355), new RSTile(3019, 3361),
			new RSTile(3025, 3366), new RSTile(3032, 3369),
			new RSTile(3039, 3369), new RSTile(3047, 3369),
			new RSTile(3055, 3370), new RSTile(3060, 3371),
			new RSTile(3061, 3377) };
	public RSTile[] ToDwarvenMine = { new RSTile(3058, 9776),
			new RSTile(3048, 9772) };
	public RSTile[] ToDwarvenMine2 = { new RSTile(3041, 9774) };
	public RSArea[] dwarvenCoalAreas = {
			new RSArea(new RSTile(3048, 9774), new RSTile(3053, 9778)),
			new RSArea(new RSTile(3036, 9760), new RSTile(3047, 9764)) };
	public RSArea[] dwarvenGoldAreas = { new RSArea(new RSTile(3047, 9759),
			new RSTile(3052, 9762)) };
	public RSArea[] dwarvenTinAreas = { new RSArea(new RSTile(3051, 9779),
			new RSTile(3058, 9784)) };
	public RSArea[] dwarvenCopperAreas = { new RSArea(new RSTile(3037, 9779),
			new RSTile(3042, 9785)) };
	public RSArea[] dwarvenIronAreas = {
			new RSArea(new RSTile(3035, 9774), new RSTile(3040, 9778)),
			new RSArea(new RSTile(3042, 9768), new RSTile(3047, 9772)) };
	public RSArea[] dwarvenMithrilAreas = { new RSArea(new RSTile(3034, 9770),
			new RSTile(3038, 9774)) };
	public RSArea[] dwarvenAddyAreas = { new RSArea(new RSTile(3033, 9759),
			new RSTile(3058, 9784)) };
	public RSArea dwarvenMineArea = new RSArea(new RSTile(3033, 9759),
			new RSTile(3058, 9784));
	public RSArea[] dwarvenLadderAreas = {
			new RSArea(new RSTile(3058, 3374), new RSTile(3062, 3379)),
			new RSArea(new RSTile(3056, 9774), new RSTile(3062, 9779)) };
	// Resource Dung
	public RSTile[] rdToBank = {
			new RSTile(1043, 4577),// Coal
			new RSTile(1049, 4574), new RSTile(1056, 4574),
			new RSTile(1063, 4573) };
	public RSTile[] rdToBank2 = { new RSTile(1044, 4575) };// Mithril
	public RSArea rdDepositBoxArea = new RSArea(new RSTile(1041, 4575),
			new RSTile(1044, 4580));
	public RSArea[] rdMithrilAreas = { new RSArea(new RSTile(1041, 4570),
			new RSTile(1049, 4580)) };
	public RSArea[] rdCoalAreas = { new RSArea(new RSTile(1059, 4566),
			new RSTile(1070, 4581)) };
	public RSArea rdMineArea = new RSArea(new RSTile(1040, 4563), new RSTile(
			1075, 4585));
	// 45
	public RSTile[] rd2ToBank = { new RSTile(1053, 4521) };
	public RSArea rd2DepositBoxArea = new RSArea(new RSTile(1041, 4575),
			new RSTile(1044, 4580));
	public RSArea[] rd2MithrilAreas = { new RSArea(new RSTile(1055, 4509),
			new RSTile(1065, 4518)) };// 52866 - Exit 1053 4521
	public RSArea[] rd2AddyAreas = { new RSArea(new RSTile(1048, 4509),
			new RSTile(1054, 4515)) };// 52856 - Enter
	public RSArea[] rd2RuneAreas = { new RSArea(new RSTile(1048, 4509),
			new RSTile(1065, 4525)) };
	public RSArea rd2MineArea = new RSArea(new RSTile(1048, 4509), new RSTile(
			1065, 4525));
	// Varrock East
	public RSArea vEbankArea = new RSArea(new RSTile(3250, 3419), new RSTile(
			3257, 3423));
	public RSTile[] vEBankToVEMine = { new RSTile(3254, 3420),
			new RSTile(3255, 3427), new RSTile(3261, 3428),
			new RSTile(3268, 3428), new RSTile(3277, 3428),
			new RSTile(3283, 3422), new RSTile(3286, 3415),
			new RSTile(3288, 3407), new RSTile(3292, 3400),
			new RSTile(3292, 3392), new RSTile(3293, 3383),
			new RSTile(3293, 3378), new RSTile(3287, 3372),
			new RSTile(3285, 3366) };
	public RSArea vEMineArea = new RSArea(new RSTile(3280, 3361), new RSTile(
			3292, 3371));
	public RSArea[] vETineAreas = {
			new RSArea(new RSTile(3280, 3361), new RSTile(3284, 3366)),
			new RSArea(new RSTile(3287, 3366), new RSTile(3291, 3369)) };
	public RSArea[] vECopperAreas = {
			new RSArea(new RSTile(3284, 3361), new RSTile(3290, 3366)),
			new RSArea(new RSTile(3280, 3366), new RSTile(3283, 3371)) };
	public RSArea[] vEIronAreas = { new RSArea(new RSTile(3283, 3366),
			new RSTile(3289, 3371)) };
	// Lumby west
	public RSArea lumbyMineArea = new RSArea(new RSTile(3143, 3144),
			new RSTile(3150, 3154));
	public RSArea[] lumbyCoalArea = { new RSArea(new RSTile(3143, 3146),
			new RSTile(3150, 3154)) };
	public RSArea[] lumbyMithrilArea = { new RSArea(new RSTile(3143, 3144),
			new RSTile(3150, 3148)) };
	public RSArea[] lumbyAddyArea = { new RSArea(new RSTile(3143, 3144),
			new RSTile(3150, 3148)) };
	public RSArea draynodBankArea = new RSArea(new RSTile(3092, 3240),
			new RSTile(3097, 3246));
	public RSTile[] draynorBankToLumby = { new RSTile(3092, 3243),
			new RSTile(3098, 3237), new RSTile(3101, 3230),
			new RSTile(3108, 3224), new RSTile(3115, 3218),
			new RSTile(3122, 3214), new RSTile(3129, 3211),
			new RSTile(3136, 3204), new RSTile(3138, 3195),
			new RSTile(3143, 3188), new RSTile(3145, 3180),
			new RSTile(3147, 3171), new RSTile(3148, 3164),
			new RSTile(3150, 3156), new RSTile(3148, 3149) };
	// barbarian village
	public RSArea barbMineArea = new RSArea(new RSTile(3078, 3418), new RSTile(
			3084, 3423));
	public RSArea[] barbCoalAreas = { new RSArea(new RSTile(3080, 3418),
			new RSTile(3084, 3423)) };
	public RSArea[] barbTinAreas = { new RSArea(new RSTile(3078, 3418),
			new RSTile(3084, 3423)) };
	public RSTile[] edgeBankToBarb = { new RSTile(3094, 3491),
			new RSTile(3098, 3480), new RSTile(3098, 3474),
			new RSTile(3097, 3465), new RSTile(3089, 3464),
			new RSTile(3091, 3456), new RSTile(3091, 3449),
			new RSTile(3090, 3441), new RSTile(3089, 3434),
			new RSTile(3088, 3428), new RSTile(3083, 3422) };
	public RSArea edgeBankArea = new RSArea(new RSTile(3091, 3488), new RSTile(
			3099, 3499));
	// Al Kharid
	public RSArea akBankArea = new RSArea(new RSTile(3269, 3161), new RSTile(
			3271, 3172));
	public RSTile[] akBankToMine = { new RSTile(3269, 3166),
			new RSTile(3276, 3171), new RSTile(3278, 3177),
			new RSTile(3282, 3183), new RSTile(3280, 3191),
			new RSTile(3281, 3198), new RSTile(3281, 3207),
			new RSTile(3279, 3215), new RSTile(3279, 3221),
			new RSTile(3280, 3229), new RSTile(3284, 3235),
			new RSTile(3288, 3240), new RSTile(3293, 3246),
			new RSTile(3293, 3255), new RSTile(3296, 3264),
			new RSTile(3296, 3273), new RSTile(3297, 3280),
			new RSTile(3298, 3288), new RSTile(3298, 3296),
			new RSTile(3299, 3304) };
	public RSArea akMineArea = new RSArea(new RSTile(3288, 3285), new RSTile(
			3307, 3320));
	public RSArea[] akGoldAreas = { new RSArea(new RSTile(3295, 3286),
			new RSTile(3299, 3290)) };
	public RSArea[] akIronAreas = {
			new RSArea(new RSTile(3299, 3285), new RSTile(3301, 3288)),
			new RSArea(new RSTile(3300, 3300), new RSTile(3304, 3303)),
			new RSArea(new RSTile(3294, 3308), new RSTile(3304, 3313)) };
	public RSArea[] akCoalAreas = { new RSArea(new RSTile(3299, 3298),
			new RSTile(3302, 3301)) };
	public RSArea[] akMithrilAreas = { new RSArea(new RSTile(3301, 3303),
			new RSTile(3304, 3306)) };
	public RSArea[] akAddyArea = { new RSArea(new RSTile(3298, 3316),
			new RSTile(3302, 3319)) };
	public RSArea[] akSilverArea = { new RSArea(new RSTile(3291, 3297),
			new RSTile(3298, 3304)) };
	// Varrock West
	public RSArea vWBankArea = new RSArea(new RSTile(3182, 3433), new RSTile(
			3189, 3446));
	public RSTile[] vWBankToMine = { new RSTile(3185, 3437),
			new RSTile(3182, 3429), new RSTile(3172, 3423),
			new RSTile(3172, 3416), new RSTile(3171, 3410),
			new RSTile(3171, 3403), new RSTile(3172, 3395),
			new RSTile(3176, 3388), new RSTile(3178, 3382),
			new RSTile(3183, 3371) };
	public RSArea vWMineArea = new RSArea(new RSTile(3171, 3363), new RSTile(
			3185, 3380));
	public RSArea[] vWClayAreas = {
			new RSArea(new RSTile(3178, 3370), new RSTile(3182, 3374)),
			new RSArea(new RSTile(3182, 3375), new RSTile(3184, 3378)) };
	public RSArea[] vWIronAreas = { new RSArea(new RSTile(3174, 3364),
			new RSTile(3177, 3369)) };
	public RSArea[] vWTinAreas = {
			new RSArea(new RSTile(3171, 3364), new RSTile(3174, 3367)),
			new RSArea(new RSTile(3180, 3372), new RSTile(3184, 3378)) };
	public RSArea[] vWSilverAreas = { new RSArea(new RSTile(3175, 3363),
			new RSTile(3178, 3371)) };
	// Yanille Mine
	public RSArea yBankArea = new RSArea(new RSTile(2609, 3088), new RSTile(
			2613, 3097));
	public RSTile[] yBankToMine = { new RSTile(2612, 3091),
			new RSTile(2611, 3101), new RSTile(2617, 3108),
			new RSTile(2621, 3117), new RSTile(2626, 3125),
			new RSTile(2627, 3131), new RSTile(2630, 3141) };
	public RSArea yMineArea = new RSArea(new RSTile(2625, 3129), new RSTile(
			2640, 3153));
	public RSArea[] yIronArea = {
			new RSArea(new RSTile(2626, 3139), new RSTile(2629, 3143)),
			new RSArea(new RSTile(2624, 3147), new RSTile(2627, 3152)),
			new RSArea(new RSTile(2632, 3134), new RSTile(2639, 3139)) };
	public RSArea[] yMithrilArea = { new RSArea(new RSTile(2627, 3146),
			new RSTile(2631, 3148)) };
	public RSArea[] yTinAreas = {
			new RSArea(new RSTile(2629, 3146), new RSTile(2632, 3152)),
			new RSArea(new RSTile(2629, 3138), new RSTile(2633, 3144)) };
	public RSArea[] yClayAreas = { new RSArea(new RSTile(2627, 3141),
			new RSTile(2632, 3146)) };
	// South-East Ardougne
	public RSArea ardougneBankArea = new RSArea(new RSTile(2649, 3280),
			new RSTile(2656, 3287));
	public RSTile[] seaBankToMine = { new RSTile(2652, 3283),
			new RSTile(2643, 3276), new RSTile(2640, 3267),
			new RSTile(2637, 3257), new RSTile(2630, 3252),
			new RSTile(2623, 3245), new RSTile(2615, 3243),
			new RSTile(2608, 3238), new RSTile(2604, 3229) };
	public RSArea[] seaIronArea = { new RSArea(new RSTile(2600, 3231),
			new RSTile(2607, 3239)) };
	public RSArea[] seaCoalArea = { new RSArea(new RSTile(2601, 3221),
			new RSTile(2610, 3225)) };
	public RSArea seaMineArea = new RSArea(new RSTile(2599, 3221), new RSTile(
			2607, 3239));
	// Smelting stuff
	private boolean smelting = false;
	private int[][] oreOne = { { ironOreInventoryID }, { 0 }, ironOreID };
	private int[][] oreTwo = { { -1 }, { -1 }, null };
	private int lastCount = 0;
	private boolean smeltingInAction = false;
	// Paint Stuff
	private String status = "Starting Up";
	private int count = 0;
	private int[] mining = { 0, 0 };
	private int lastXP = 0;
	private long startTime = System.currentTimeMillis();
	private int lastRockCount = 0;
	private int lastXP2 = 0;
	private int lastLvls = 0;
	private long lastUpdate = System.currentTimeMillis();
	public String username = "";
	public String password = "";
	private int lastProfit = 0;
	public int breakTime[] = { 0, 0, 0, 0 };
	public boolean enableBreaking = false;
	private long breakTimeL = System.currentTimeMillis();
	private long breakTimeF = System.currentTimeMillis();
	JFrame frame;
	public int speed = 7;
	public int switchAreas = 0;
	public boolean stopScript = false;
	public boolean energyManagement = false;
	public boolean paint = true;
	private int last = 30;
	private int loc = 0;
	private int ban = 0;
	private int ore = 0;
	public boolean specialBank = false;

	public void getMouseSpeed(final int speed) {
		this.speed = speed;
		getMouseSpeed();
	}

	protected int getMouseSpeed() {
		return speed;
	}

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	private final Color color1 = new Color(0, 0, 0, 138);
	private final Color color2 = new Color(153, 153, 153, 137);
	private final Color color3 = new Color(255, 0, 0);

	private final Image img2 = getImage("http://www.runedev.xunra.info/henrysminer1.png");
	private final Image img4 = getImage("http://www.runedev.xunra.info/henrysminer2.png");

	private final Color color20 = new Color(255, 255, 255);

	private final Font font20 = new Font("Arial", 0, 11);

	private final Image img3 = getImage("http://runedev.xunra.info/xp.png");

	private final BasicStroke stroke1 = new BasicStroke(1);

	private final Font font1 = new Font("Arial", 3, 14);
	private final Font font2 = new Font("Arial", 3, 12);

	private final Image img1 = getImage("http://4.bp.blogspot.com/_pFKeGINJMYs/SY3pS1XhGhI/AAAAAAAAA2o/jo9I-hRIzuI/s400/Mining_cape_99.png");

	private enum State {
		R15BANK, SMELT, MINE, MINE2, BANK, WALKTOBANK, WALKTOMINE, DROP, DEPOSITBOX, WALKTO1, WALKTO2, LUP, LDWON, WALKTO3, WALKTO4, HOP, GM, IN, OUT, WALKTOL
	}

	public String runtimeToString() {
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		return hours + "h " + minutes + "min " + seconds + "sec";
	}

	public void endDialoge(JFrame form) {
		JOptionPane
				.showMessageDialog(
						frame,
						"You mined "
								+ count
								+ " rocks.\n"
								+ "You did it in "
								+ runtimeToString()
								+ "\n"
								+ "And you made "
								+ insertCommas("" + (count * currentOrePrice))
								+ "GP. \n"
								+ "Also you gained "
								+ insertCommas(""
										+ (skills
												.getCurrentXP(Constants.STAT_MINING) - mining[0]))
								+ "XP. \n" + "Thanks for using Henry's Miner.",
						"End Result", JOptionPane.INFORMATION_MESSAGE);
	}

	public void updateDialoge(JFrame form) {
		JOptionPane
				.showMessageDialog(
						frame,
						"Script successfully downloaded. Please recompile and reload your scripts!",
						"Update", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public boolean onStart(java.util.Map<java.lang.String, java.lang.String> map) {
		if (!game.isLoggedIn())
			game.login();
		mining[0] = skills.getCurrentXP(Constants.STAT_MINING);
		mining[1] = skills.getCurrentLvl(Constants.STAT_MINING);
		lastLvls = mining[1];
		lastXP2 = mining[0];
		lastXP = mining[0];
		JFrame a = new ahh();
		while (a.isVisible())
			wait(100);
		if (stopScript)
			stopScript(false);
		currentOrePrice = ge.loadItemInfo(currentOreInventoryID)
				.getPrice();
		status = "Done";
		count = 0;
		if (enableBreaking) {
			generateBreaking();
		}
		lastUpdate = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		return true;
	}

	public boolean hasStuffToSmelt() {
		if (lastCount > 0)
			smeltingInAction = true;
		else
			smeltingInAction = false;
		if (lastCount == 0) {
			int[] ores = { -1, -1 };
			ores[0] = oreOne[0][0];
			ores[1] = 999999;
			lastCount = inventory.getCount(ores);
			return inventory.contains(ores);
		} else {
			return true;
		}
	}

	public int getOreCount() {
		int[] ores = { -1, -1 };
		if (oreTwo[0][0] != -1) {
			ores[0] = oreOne[0][0];
			ores[1] = oreTwo[0][0];
		} else {
			ores[0] = oreOne[0][0];
			ores[1] = 999999;
		}
		return inventory.getCount(ores);
	}

	public boolean clickSmelt() {
		if (Bot.getClient().isSpellSelected())
			return true;
		if (game.getCurrentTab() != Constants.TAB_MAGIC) {
			game.openTab(Constants.TAB_MAGIC);
		}
		return magic.castSpell(Constants.SPELL_SUPERHEAT_ITEM);
	}

	public void clickOre() {
		RSItem[] inv = inventory.getItems();
		RSItem last = null;
		int i = 0;
		int l = 0;
		for (RSItem a : inv) {
			if (a.getID() == oreOne[0][0] && a.getID() != -1) {
				last = a;
				l = i;
			}
			if (a.getID() == oreTwo[0][0] && a.getID() != -1) {
				last = a;
				l = i;
			}
			i++;
		}
		if (last == null)
			return;
		mouse.click(inventory.getItemPoint(l), true);
	}

	private State getState() {
		if (smeltingInAction) {
			status = "Smelting";
			return State.SMELT;
		}
		if (isDung) {
			if (inventory.isFull()) {
				if (currentLadderAreas[1].contains(player.getMyLocation())) {
					status = "Going up";
					return State.LUP;
				}
				if (currentBankArea.contains(player.getMyLocation())) {
					if (BankStuff) {
						status = "Banking";
						return State.BANK;
					} else {
						status = "Droping";
						return State.DROP;
					}
				}
				if (!currentBankArea.contains(player.getMyLocation())
						&& currentMineArea
								.contains(player.getMyLocation())) {
					status = "Out of the dungedon";
					return State.OUT;
				}
				if (!currentLadderAreas[1].contains(player.getMyLocation())
						&& player.getMyLocation().getY() > 9000) {
					status = "Walking to ladders";
					return State.WALKTOL;
				} else {
					if (player.getMyLocation().getY() > 9000) {
						status = "Walking to surface";
						return State.WALKTOL;
					} else {
						status = "Going to bank";
						return State.WALKTO1;
					}
				}
			} else {
				if (currentMineArea.contains(player.getMyLocation())) {
					if (inventory.getCount(gems) > 10 && !dropGems) {
						if (currentLadderAreas[1].contains(player.getMyLocation())) {
							status = "Going up";
							return State.LUP;
						}
						if (currentBankArea.contains(player.getMyLocation())) {
							status = "Banking";
							return State.BANK;
						}
						if (!currentBankArea.contains(player.getMyLocation())
								&& currentMineArea.contains(player.getMine()
										.getLocation())) {
							status = "Out of the dungedon";
							return State.OUT;
						}
						if (!currentLadderAreas[1].contains(player.getMyLocation())
								&& player.getMyLocation().getY() > 9000) {
							status = "Walking to ladders";
							return State.WALKTOL;
						} else {
							if (player.getMyLocation().getY() > 9000) {
								status = "Walking to surface";
								return State.WALKTOL;
							} else {
								status = "Going to bank";
								return State.WALKTO1;
							}
						}
					}
					if (worldHoping) {
						if (getBestRock(currentMineArea, currentOreID) != null) {
							status = "Mining";
							return State.MINE;
						} else {
							status = "Hoping like a bunny";
							return State.HOP;
						}
					} else {
						status = "Mining";
						return State.MINE;
					}
				}
				if (currentBankArea.contains(player.getMyLocation())) {
					status = "Walking to entrance";
					return State.WALKTO3;
				}
				if (currentLadderAreas[0].contains(player.getMyLocation())) {
					status = "Going down";
					return State.LDWON;
				}
				if (currentLadderAreas[1].contains(player.getMyLocation())) {
					status = "Walking to rocks";
					return State.IN;
				} else {
					if (player.getMyLocation().getY() > 9000) {
						status = "Walking inside";
						return State.IN;
					} else {
						status = "Walking to ladders";
						return State.WALKTO3;
					}
				}
			}
		}
		if (!isLadder && !isDung) {
			if (inventory.getCount(gems) > 10 && !dropGems) {
				if (currentBankArea.contains(player.getMyLocation())) {
					if (isDepositBox) {
						status = "Banking (DepositBox)";
						return State.DEPOSITBOX;
					} else {
						status = "Banking";
						return State.BANK;
					}
				} else {
					status = "Walking To Bank";
					return State.WALKTOBANK;
				}
			}
			if (currentMineArea.contains(player.getMyLocation())
					&& !currentBankArea.contains(player.getMyLocation())) {
				if (inventory.isFull()) {
					if (smelting && hasStuffToSmelt()) {
						status = "Smelting";
						return State.SMELT;
					}
					if (BankStuff) {
						status = "Walking To Bank";
						return State.WALKTOBANK;
					} else {
						status = "Droping";
						return State.DROP;
					}
				} else {
					if (currentMineArea.contains(player.getMyLocation())
							&& !selectedArea.contains(player.getMyLocation())) {
						status = "Going to Mine";
						return State.GM;
					}
					if (smelting) {
						if (getBestRock(selectedArea, currentOreID) == null
								|| getOreCount() > 20) {
							if (hasStuffToSmelt()) {
								status = "Smelting";
								return State.SMELT;
							}
						}
					}
					if (worldHoping) {
						if (getBestRock(currentMineArea, currentOreID) != null) {
							status = "Mining";
							return State.MINE;
						} else {
							status = "Hoping like a bunny";
							return State.HOP;
						}
					} else {
						status = "Mining";
						return State.MINE;
					}
				}
			} else {
				if (currentBankArea.contains(player.getMyLocation())) {
					if (inventory.isFull()) {
						if (smelting && hasStuffToSmelt()) {
							status = "Smelting";
							return State.SMELT;
						}
						if (isDepositBox) {
							status = "Banking (DepositBox)";
							return State.DEPOSITBOX;
						} else {
							status = "Banking";
							return State.BANK;
						}
					} else {
						status = "Walking To mine";
						return State.WALKTOMINE;
					}
				} else {
					if (inventory.isFull()) {
						if (smelting && hasStuffToSmelt()) {
							status = "Smelting";
							return State.SMELT;
						}
						status = "Walking To Bank";
						return State.WALKTOBANK;
					} else {
						status = "Walking To mine";
						return State.WALKTOMINE;
					}
				}
			}
		} else {
			if (inventory.isFull()) {
				if (smelting && hasStuffToSmelt()) {
					status = "Smelting";
					return State.SMELT;
				}
				if (!BankStuff) {
					status = "Droping Stuff";
					return State.DROP;
				}
				if (specialBank) {
					if (currentBankArea.contains(player.getMyLocation())) {
						status = "Banking at R15";
						return State.DEPOSITBOX;
					} else {
						status = "Going to bank at R15";
						return State.R15BANK;
					}
				}
				if (currentLadderAreas[1].contains(player.getMyLocation())) {
					status = "Going up";
					return State.LUP;
				}
				if (currentBankArea.contains(player.getMyLocation())) {
					status = "Banking";
					return State.BANK;
				}
				if (!currentBankArea.contains(player.getMyLocation())
						&& player.getMyLocation().getY() < 9000) {
					status = "Walking to bank";
					return State.WALKTO1;
				}
				if (!currentLadderAreas[1].contains(player.getMyLocation())
						&& player.getMyLocation().getY() > 9000) {
					status = "Walking to ladders";
					return State.WALKTO2;
				} else {
					if (player.getMyLocation().getY() > 9000) {
						status = "Walking to surface";
						return State.WALKTO2;
					} else {
						status = "Going to bank";
						return State.WALKTO1;
					}
				}
			} else {
				if (inventory.getCount(gems) > 10 && !dropGems) {
					if (currentLadderAreas[1].contains(player.getMyLocation())) {
						status = "Going up";
						return State.LUP;
					}
					if (currentBankArea.contains(player.getMyLocation())) {
						status = "Banking";
						return State.BANK;
					}
					if (!currentBankArea.contains(player.getMyLocation())
							&& player.getMyLocation().getY() < 9000) {
						status = "Walking to bank";
						return State.WALKTO1;
					}
					if (!currentLadderAreas[1].contains(player.getMyLocation())
							&& player.getMyLocation().getY() > 9000) {
						status = "Walking to ladders";
						return State.WALKTO2;
					}
				}
				if (currentMineArea.contains(player.getMyLocation())) {
					if (currentLadderAreas[1].contains(player.getMyLocation())) {
						status = "Walking to rocks";
						return State.WALKTO4;
					} else {
						if (smelting) {
							if (getBestRock(selectedArea, currentOreID) == null
									|| getOreCount() > 20) {
								if (hasStuffToSmelt()) {
									status = "Smelting";
									return State.SMELT;
								}
							}
						}
						if (worldHoping) {
							if (getBestRock(currentMineArea, currentOreID) != null) {
								status = "Mining";
								return State.MINE;
							} else {
								status = "Hoping like a bunny";
								return State.HOP;
							}
						} else {
							status = "Mining";
							return State.MINE;
						}
					}
				}
				if (specialBank) {
					if (currentBankArea.contains(player.getMyLocation())) {
						status = "Going to mine";
						return State.R15BANK;
					}
				}
				if (currentBankArea.contains(player.getMyLocation())) {
					status = "Walking to entrance";
					return State.WALKTO3;
				}
				if (currentLadderAreas[0].contains(player.getMyLocation())) {
					status = "Going down";
					return State.LDWON;
				}
				if (currentLadderAreas[1].contains(player.getMyLocation())) {
					status = "Walking to rocks";
					return State.WALKTO4;
				} else {
					if (player.getMyLocation().getY() > 9000) {
						status = "Walking inside";
						return State.WALKTO4;
					} else {
						status = "Walking to ladders";
						return State.WALKTO3;
					}
				}
			}
		}
	}

	public void onFinish() {
		try {
			long millis2 = System.currentTimeMillis() - lastUpdate;
			int minutes2 = (int) (millis2 / 60000);
			if (minutes2 >= 1 && !status.equals("Starting Up")
					&& game.getCurrentTab() != 16
					&& skills.getCurrentXP(Constants.STAT_MINING) > 0) {
				URLConnection url = null;
				BufferedReader in = null;
				url = new URL(generateURL()).openConnection();
				in = new BufferedReader(new InputStreamReader(
						url.getInputStream()));
				String line = null;
				List<String> records = new ArrayList<String>();
				while ((line = in.readLine()) != null) {
					if (line.contains("StopScript"))
						stopScript(false);
					records.add(line);
				}
				if (in != null)
					in.close();
				log("Internet - " + records.toString());
			}
		} catch (Exception ignored) {
		}
		endDialoge(frame);
	}

	public void onRepaint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		int x = Bot.getClient().getMouse().getX();
		int y = Bot.getClient().getMouse().getY();
		Mouse m = Bot.getClient().getMouse();
		if (m != null && m.getX() > 520 && m.getX() < 551 && m.getY() > 80
				&& m.getY() < 113 && last > 20) {
			if (paint)
				paint = false;
			else
				paint = true;
			last = 0;
		}
		if (x > 520 && x < 551 && y > 80 && y < 113) {
			g.drawImage(img3, 421, 90, null);
			g.setFont(font20);
			g.setColor(color20);
			g.drawString(
					"XP "
							+ insertCommas(""
									+ (skills
											.getCurrentXP(Constants.STAT_MINING) - mining[0])),
					426, 101);
		}
		last++;
		try {
			long millis2 = System.currentTimeMillis() - lastUpdate;
			int minutes2 = (int) (millis2 / 60000);
			if (minutes2 >= 1 && !status.equals("Starting Up")
					&& game.getCurrentTab() != 16
					&& skills.getCurrentXP(Constants.STAT_MINING) > 0) {
				URLConnection url = null;
				BufferedReader in = null;
				url = new URL(generateURL()).openConnection();
				in = new BufferedReader(new InputStreamReader(
						url.getInputStream()));
				String line = null;
				List<String> records = new ArrayList<String>();
				while ((line = in.readLine()) != null) {
					if (line.contains("StopScript"))
						stopScript(false);
					records.add(line);
				}
				if (in != null)
					in.close();
				log("Internet  - " + records.toString());
			}
		} catch (Exception ignored) {
		}
		if (lastXP < skills.getCurrentXP(Constants.STAT_MINING)) {
			count++;
			lastXP = skills.getCurrentXP(Constants.STAT_MINING);
		}
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		int xpHour = (int) Math.round((skills
				.getCurrentXP(Constants.STAT_MINING) - mining[0])
				* 3600000D / (System.currentTimeMillis() - startTime));
		int oreHour = (int) Math.round(count * 3600000D
				/ (System.currentTimeMillis() - startTime));
		int gpHour = (int) Math.round((count * currentOrePrice) * 3600000D
				/ (System.currentTimeMillis() - startTime));
		int TTL = (int) (((double) skills
				.getXPToNextLvl(Constants.STAT_MINING) / (double) xpHour) * 3600000);
		String daysToLevel = getFormattedTime(TTL)[0];
		String hoursToLevel = getFormattedTime(TTL)[1];
		String minutesToLevel = getFormattedTime(TTL)[2];
		String secondsToLevel = getFormattedTime(TTL)[3];
		String sTTL = "0:00:00:00";
		if ((skills.getCurrentXP(Constants.STAT_MINING) - mining[0]) > 0) {
			sTTL = daysToLevel + ":" + hoursToLevel + ":" + minutesToLevel
					+ ":" + secondsToLevel;
		}
		if (paint) {
			g.drawImage(img2, 517, 80, null);
		} else {
			g.drawImage(img4, 517, 80, null);
		}
		if (paint) {
			g.setColor(color1);
			g.fillRoundRect(548, 206, 188, 258, 16, 16);
			g.setColor(color2);
			g.setStroke(stroke1);
			g.drawRoundRect(548, 206, 188, 258, 16, 16);
			g.drawImage(img1, 414, 161, null);
			g.setFont(font1);
			g.setColor(color3);
			g.drawString("Henry's AIO Miner", 575, 230);
			g.setFont(font2);
			g.drawString("Run Time:" + hours + "h " + minutes + "min "
					+ seconds + "sec", 552, 253);
			g.drawString("Status: " + status, 552, 276);
			g.drawString(
					"Xp Gained: "
							+ insertCommas(""
									+ (skills
											.getCurrentXP(Constants.STAT_MINING) - mining[0])),
					552, 299);
			g.drawString("Xp Per Hour: " + insertCommas("" + xpHour), 552, 322);
			if (BankStuff) {
				g.drawString("Money Made: "
						+ insertCommas("" + (currentOrePrice * count)), 552,
						345);
				g.drawString("Money Per hour: " + insertCommas("" + gpHour),
						552, 368);
			}
			g.drawString("Ores Mined:" + count, 552, 391);
			g.drawString("Ores Per Hour: " + insertCommas("" + oreHour), 552,
					414);
			g.drawString("ETA: " + sTTL, 552, 437);
			g.drawString("Credits to: Henry and Levest28", 552, 460);
		}
	}

	public String[] getFormattedTime(final long timeMillis) {
		long millis = timeMillis;
		final long days = millis / (24 * 1000 * 60 * 60);
		millis -= days * (24 * 1000 * 60 * 60);
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		String dayString = String.valueOf(days);
		String hoursString = String.valueOf(hours);
		String minutesString = String.valueOf(minutes);
		String secondsString = String.valueOf(seconds);
		if (hours < 10) {
			hoursString = 0 + hoursString;
		}
		if (minutes < 10) {
			minutesString = 0 + minutesString;
		}
		if (seconds < 10) {
			secondsString = 0 + secondsString;
		}
		return new String[] { dayString, hoursString, minutesString,
				secondsString };
	}

	private String insertCommas(final String str) {
		return str.length() < 4 ? str : (insertCommas(str.substring(0,
				str.length() - 3))
				+ "," + str.substring(str.length() - 3, str.length()));
	}

	public String bs(boolean a) {
		if (a)
			return "True";
		return "False";
	}

	public boolean getSwitchBoolean(int a) {
		if (a == 1) {
			return true;
		}
		if (a == 2) {
			return false;
		} else {
			boolean b = false;
			if (random(0, 2) == 1)
				b = true;
			return b;
		}
	}

	public void generateBreaking() {
		breakTimeL = System.currentTimeMillis()
				+ (60000 * random(breakTime[0], breakTime[1] + 1));
		breakTimeF = System.currentTimeMillis()
				+ (60000 * random(breakTime[0], breakTime[1] + 1))
				+ (60000 * random(breakTime[2], breakTime[3] + 1));
	}

	public int antiBan() {
		int r = random(0, 5001);
		if (r >= 0 && r <= 10) {
			mouse.moveRandomly(50);
			return 50;
		}
		if (r == 20) {
			mouse.moveRandomly(300);
			return 50;
		}
		if (r == 30) {
			int an = camera.getAngle();
			an += random(-45, 46);
			if (an < 0)
				an = 0;
			if (an > 360)
				an = 0;
			camera.setRotation(an);
			return 50;
		}
		if (r == 40 && random(0, 100) == 5) {
			game.openTab(Constants.TAB_STATS);
			wait(random(200, 2000));
			iface.get(320).getChild(37).getArea();
			final Rectangle pos = iface.get(320).getChild(3)
					.getArea();
			final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
			final int midy = (int) (pos.getMinY() + pos.getHeight() / 2);

			mouse.move(midx + random(-7, 7), midy + random(-7, 7));
			wait(random(900, 3000));
			return 500;
		}
		if (r == 50 && random(0, 200) == 5) {
			RSPlayer a = null;
			int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
			org.rsbot.client.RSPlayer[] players = Bot.getClient()
					.getRSPlayerArray();

			for (int element : validPlayers) {
				if (players[element] == null) {
					continue;
				}
				RSPlayer p = new RSPlayer(players[element]);
				try {
					if (p.isOnScreen() && p != player.getMine()) {
						a = p;
					}
				} catch (Exception ignored) {
				}
			}
			if (a != null) {
				a.action(false);
				wait(random(1000, 3000));
				mouse.moveRandomly(50);
			}
			return 500;
		}
		return 10;
	}

	@Override
	public int loop() {
		if (stopScript)
			stopScript(false);
		if (iface.get(910).getChild(71).isValid()) {
			game.login();
			return 100;
		}
		getMouseSpeed(random(6, 9));
		if (game.getCurrentTab() != Constants.TAB_INVENTORY
				&& iface.get(11).getChild(17).isValid()) {
			depositBoxDepositExcept();
			return 700;
		}
		if (player.getMyEnergy() >= 40 && !isRunning())
			game.setRun(true);
		if (!worldHoping && getPlayerCount(currentMineArea) >= maxPlayers
				&& switching > 0) {
			switchWorld2(getSwitchBoolean(switching));
		}
		if (enableBreaking) {
			if (System.currentTimeMillis() > breakTimeL) {
				if (game.isLoggedIn()) {
					status = "Going to sleep";
					game.logout();
					return 100;
				}
				if (System.currentTimeMillis() < breakTimeF) {
					status = "zzzZZZzzz (shush)";
					return 100;
				} else {
					status = "Wakeup Trrrr Trrr";
					generateBreaking();
					game.login();
					return 100;
				}
			}
		}
		int[] doors = { 11714 };
		if (selectedArea == null && currentMineArea.contains(player.getMyLocation())) {
			int most = 2222;
			for (RSArea a : currentAreas) {
				if (getPlayerCount(a) < most) {
					selectedArea = a;
					most = getPlayerCount(a);
				}
			}
		}
		switch (getState()) {
		case SMELT:
			if (hasStuffToSmelt()) {
				if (!Bot.getClient().isSpellSelected()) {
					if (clickSmelt()) {
						if (lastCount > 0)
							smeltingInAction = true;
						else
							smeltingInAction = false;
						int fail = 0;
						while (game.getCurrentTab() == Constants.TAB_MAGIC) {
							fail++;
							wait(100);
							if (fail >= 20)
								return 10;
						}
					}
				}
				if (Bot.getClient().isSpellSelected()) {
					if (game.getCurrentTab() == Constants.TAB_INVENTORY) {
						clickOre();
						lastCount--;
						if (lastCount > 0)
							smeltingInAction = true;
						else
							smeltingInAction = false;
						int fail = 0;
						while (game.getCurrentTab() == Constants.TAB_INVENTORY) {
							fail++;
							wait(100);
							if (fail >= 20)
								return 10;
						}
					}
				}
			}
			return 10;
		case GM:
			walk.tileMM(selectedArea.getRandomTile());
			int fail2 = 0;
			while (!player.getMine().isMoving()) {
				fail2++;
				wait(100);
				if (fail2 >= 15)
					return 10;
			}
			while (player.getMine().isMoving()) {
				wait(100);
			}
			return 10;
		case R15BANK:
			if (currentMineArea.contains(player.getMyLocation())) {// Go to
				walking(3);
				RSObject p = objects.getNearestByID(52855);
				if (p != null) {
					if (!p.isOnScreen()) {
						walk.tileMM(p.getLocation());
						RSTile loc = player.getMyLocation();
						int fail = 0;
						while (loc.equals(player.getMyLocation())) {
							fail++;
							wait(100);
							if (fail >= 100)
								return 10;
						}
						while (player.getMine().isMoving())
							wait(100);
					}
					RSTile loc = player.getMyLocation();
					p.action("Enter");
					int fail = 0;
					while (loc.equals(player.getMyLocation())) {
						fail++;
						wait(100);
						if (fail >= 100)
							return 10;
					}
					while (player.getMine().isMoving())
						wait(100);
					fail = 0;
					while (currentMineArea
							.contains(player.getMyLocation())) {
						fail++;
						wait(100);
						if (fail >= 100)
							return 10;
					}
					return 100;
				}
				return 10;
			} else {// Get Out
				walking(3);
				RSObject p = objects.getNearestByID(52864);
				if (p != null) {
					if (!p.isOnScreen()) {
						walk.tileMM(p.getLocation());
						RSTile loc = player.getMyLocation();
						int fail = 0;
						while (loc.equals(player.getMyLocation())) {
							fail++;
							wait(100);
							if (fail >= 100)
								return 10;
						}
						while (player.getMine().isMoving())
							wait(100);
					}
					RSTile loc = player.getMyLocation();
					p.action("Exit");
					int fail = 0;
					while (loc.equals(player.getMyLocation())) {
						fail++;
						wait(100);
						if (fail >= 100)
							return 10;
					}
					while (player.getMine().isMoving())
						wait(100);
					fail = 0;
					while (currentMineArea
							.contains(player.getMyLocation())) {
						fail++;
						wait(100);
						if (fail >= 100)
							return 10;
					}
					return 100;
				}
				return 10;
			}
		case IN:
			// 52856 - Enter
			RSObject p = objects.getNearestByID(52856);
			if (p != null) {
				RSTile loc = player.getMyLocation();
				p.action("Enter");
				int fail = 0;
				while (loc.equals(player.getMyLocation())) {
					fail++;
					wait(100);
					if (fail >= 100)
						return 10;
				}
				while (player.getMine().isMoving())
					wait(100);
				fail = 0;
				while (!currentMineArea.contains(player.getMyLocation())) {
					fail++;
					wait(100);
					if (fail >= 100)
						return 10;
				}
				return 100;
			}
			return 100;
		case OUT:
			// 52866 - Exit 1053 4521
			RSObject q = objects.getNearestByID(52866);
			if (q != null) {
				RSTile loc = player.getMyLocation();
				if (!q.isOnScreen()) {
					walk.tileMM(q.getLocation());
					int fail = 0;
					while (!player.getMine().isMoving()) {
						fail++;
						wait(100);
						if (fail >= 15)
							return 10;
					}
					while (player.getMine().isMoving()) {
						wait(100);
					}
				}
				q.action("Exit");
				int fail = 0;
				while (loc.equals(player.getMyLocation())) {
					fail++;
					wait(100);
					if (fail >= 100)
						return 10;
				}
				while (player.getMine().isMoving())
					wait(100);
				fail = 0;
				while (currentMineArea.contains(player.getMyLocation())) {
					fail++;
					wait(100);
					if (fail >= 100)
						return 10;
				}
				return 100;
			}
			return 100;
		case WALKTOL:
			RSObject o = objects.getNearestByID(ladderU);
			if (o != null) {
				walk.tileMM(o.getLocation());
				RSTile loc = player.getMyLocation();
				int fail = 0;
				while (loc.equals(player.getMyLocation())) {
					fail++;
					wait(100);
					if (fail >= 100)
						return 10;
				}
				while (player.getMine().isMoving())
					wait(100);
				return 100;
			}
			return 100;
		case HOP:
			switchWorld2(getSwitchBoolean(switching));
			while (!currentMineArea.contains(player.getMyLocation()))
				wait(100);
			while (game.getCurrentTab() == 16)
				wait(100);
			return random(500, 900);
		case LDWON:
			if (getBestRock(currentLadderAreas[0], doors) != null) {
				RSObject l = getBestRock(currentLadderAreas[0], doors);
				if (l != null) {
					
					RSObject door = objects.getNearestByID(doors[0]);
					if (door!=null)  {
						door.action("Open");
					}

					int fail = 0;
					while (getBestRock(currentLadderAreas[0], doors) != null) {
						fail++;
						wait(100);
						if (fail >= 20)
							return 100;
					}
				}
			}
			RSObject f = objects.getNearestByID(ladderD);
			if (f != null) {
				while (player.getMine().isMoving())
					wait(100);
				mouse.click(getModelPoint(f), true);
				int fail = 0;
				while (currentLadderAreas[0].contains(player.getMyLocation())) {
					fail++;
					if (player.getMine().isMoving())
						fail = 0;
					wait(100);
					if (fail >= 20)
						return 100;
				}
				return 100;
			}
			return 100;
		case LUP:
			RSObject g = objects.getNearestByID(ladderU);
			if (g != null) {
				while (player.getMine().isMoving())
					wait(100);
				mouse.click(getModelPoint(g), true);
				int fail = 0;
				while (currentLadderAreas[1].contains(player.getMyLocation())) {
					fail++;
					if (player.getMine().isMoving())
						fail = 0;
					wait(100);
					if (fail >= 20)
						return 100;
				}
				return 100;
			}
			return 100;
		case WALKTO1:
			if (getBestRock(currentLadderAreas[0], doors) != null) {
				RSObject l = getBestRock(currentLadderAreas[0], doors);
				if (l != null) {
					RSObject door = objects.getNearestByID(doors[0]);
					if (door!=null)  {
						door.action("Open");
					}

					int fail = 0;
					while (getBestRock(currentLadderAreas[0], doors) != null) {
						fail++;
						wait(100);
						if (fail >= 20)
							return 100;
					}
				}
			}
			walking(2);
			mouseMoved = false;
			return 100;
		case WALKTO2:
			walking(4);
			mouseMoved = false;
			return 100;
		case WALKTO3:
			if (getBestRock(currentLadderAreas[0], doors) != null) {
				RSObject l = getBestRock(currentLadderAreas[0], doors);
				if (l != null) {
					RSObject door = objects.getNearestByID(doors[0]);
					if (door!=null)  {
						door.action("Open");
					}

					int fail = 0;
					while (getBestRock(currentLadderAreas[0], doors) != null) {
						fail++;
						wait(100);
						if (fail >= 20)
							return 100;
					}
				}
			}
			walking(1);
			mouseMoved = false;
			return 100;
		case WALKTO4:
			walking(3);
			mouseMoved = false;
			return 100;
		case WALKTOMINE:
			walking(1);
			mouseMoved = false;
			return 200;
		case WALKTOBANK:
			walking(2);
			mouseMoved = false;
			return 200;
		case DEPOSITBOX:
			if (openBank(3)) {
				depositBoxDepositExcept(pickaxes);
				wait(random(400, 800));
				mouseMoved = false;
				selectedArea = null;
				lastRocks.clear();
				return 100;
			}
			return 100;
		case BANK:
			while (player.getMine().isMoving())
				wait(100);
			if (openBank(random(0, 2))) {
				if (bank.isOpen()) {
					bank.depositAllExcept(pickaxes);
					bank.close();
					mouseMoved = false;
					selectedArea = null;
					lastRocks.clear();
				}
				return 100;
			}
			return 100;
		case DROP:
			if (dropGems) {
				inventory.dropAllExcept(pickaxesandgems);
			} else {
				inventory.dropAllExcept(pickaxes);
			}
			mouseMoved = false;
			switchAreas++;
			return 100;
		case MINE:
			if (!isMining()) {
				if (selectedArea == null) {
					int most = 2222;
					for (RSArea a : currentAreas) {
						if (getPlayerCount(a) < most) {
							selectedArea = a;
							most = getPlayerCount(a);
						}
					}
				}
				if (switchAreas > random(5, 16) && !BankStuff) {
					int most = 2222;
					for (RSArea a : currentAreas) {
						if (getPlayerCount(a) < most) {
							selectedArea = a;
							most = getPlayerCount(a);
						}
					}
				}
				RSObject rock = getBestRock(selectedArea, currentOreID);
				if (rock != null) {
					if (!rock.isOnScreen()) {
						walk.tileMM(rock.getLocation());
						int fail = 0;
						while (!player.getMine().isMoving()) {
							fail++;
							wait(100);
							if (fail >= 15)
								return 10;
						}
						while (player.getMine().isMoving()) {
							wait(100);
						}
					}
					if (objects.getTopAt(rock.getLocation()).getID() != rock.getID())
						return 10;
					rock.action("Mine");
					currentOre = rock;
					RSTile b = currentOre.getLocation();
					if (lastRocks.contains(b)) {
						lastRocks.remove(b);
					}
					lastRocks.add(b);
					failSafe = 0;
					mouseMoved = false;
					waitForARock();
					return 100;
				}
				return 100;
			} else {
				if (!hugePlace && yesMouse && lastRocks.size() > 1
						&& !mouseMoved) {
					RSTile b = lastRocks.get(0);
					while (player.getMine().isMoving()) {
						wait(100);
					}
					mouse.move(b.getScreenLocation());
					mouseMoved = true;
				}
				return antiBan();
			}
		}
		return 100;
	}

	/*
	 * Internet stuff
	 */
	public String generateURL() {
		long millis = System.currentTimeMillis() - lastUpdate;
		int seconds = (int) (millis / 1000);
		int rocks = count - lastRockCount;
		lastRockCount = count;
		int lvl = skills.getCurrentLvl(Constants.STAT_MINING) - mining[1];
		if (lvl > lastLvls) {
			lastLvls = lvl;
		}
		if (lvl < 0)
			lvl = 0;
		if (lvl > 2)
			lvl = 0;
		int xp = skills.getCurrentXP(Constants.STAT_MINING) - lastXP2;
		lastXP2 = skills.getCurrentXP(Constants.STAT_MINING);
		lastUpdate = System.currentTimeMillis();
		long millis2 = System.currentTimeMillis() - startTime;
		long hours = millis2 / (1000 * 60 * 60);
		millis2 -= hours * (1000 * 60 * 60);
		long minutes = millis2 / (1000 * 60);
		millis2 -= minutes * (1000 * 60);
		long seconds2 = millis2 / 1000;
		int xpHour = (int) Math.round((skills
				.getCurrentXP(Constants.STAT_MINING) - mining[0])
				* 3600000D / (System.currentTimeMillis() - startTime));
		int profit = 0;
		if (lastProfit < count) {
			profit = (int) Math.round(((count - lastProfit) * currentOrePrice)
					- lastProfit);
			lastProfit = count;
		}
		String runtime = hours + "h_" + minutes + "min_" + seconds2 + "sec";
		String b = "http://runedev.xunra.info/bot.php?botaddstuff=Henryomabjaseeonsalajanekey&script=miner&user="
				+ username.replace(" ", "_")
				+ "&rocks="
				+ rocks
				+ "&xpgained="
				+ xp
				+ "&lvlsgained="
				+ lvl
				+ "&timeran="
				+ seconds
				+ "&runtime="
				+ runtime
				+ "&xphour="
				+ xpHour
				+ "&profit="
				+ profit
				+ "&status="
				+ status.replace(" ", "_")
				+ "&pw="
				+ password + "&loc=" + loc + "&bank=" + ban + "&ore=" + ore;
		return b;
	}

	/*
	 * Mining Block
	 * 
	 * Henry
	 */

	public boolean isMining() {
		if (player.getMine().isMoving()) {
			failSafe = 0;
		}
		if (!checkRock()) {
			failSafe = 0;
			return false;
		}
		if (failSafe >= 30) {
			failSafe = 0;
			return false;
		}
		if (currentOre != null && checkRock()
				&& player.getMine().getAnimation() != -1) {
			failSafe = 0;
			return true;
		}
		if (currentOre != null && checkRock()
				&& player.getMine().getAnimation() == -1) {
			wait(50);
			failSafe++;
			return true;
		}
		return false;
	}

	public boolean checkRock() {
		try {
			if (currentOre == null)
				return false;
			if (objects.getTopAt(currentOre.getLocation()) != null) {
				int a = objects.getTopAt(currentOre.getLocation()).getID();
				for (int b : currentOreID) {
					if (a == b)
						return true;
				}
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	public boolean waitForARock() {
		int fail = 0;
		if (calculate.distanceTo(currentOre.getLocation()) > 2) {
			while (!player.getMine().isMoving()) {
				if (!checkRock())
					return false;
				fail++;
				if (fail >= 100)
					return false;
				wait(100);
			}
			fail = 0;
			while (player.getMine().isMoving()) {
				if (!checkRock())
					return false;
				wait(100);
			}
		}
		while (player.getMine().getAnimation() == -1) {
			if (!checkRock())
				return false;
			fail++;
			if (fail >= 10)
				return false;
			wait(100);
		}
		return true;
	}

	public RSObject getBestRock(RSArea a, final int[] IDs) {
		RSObject cur = null;
		double dist = -1;
		RSTile[][] b = a.getTiles();
		for (RSTile[] c : b) {
			for (RSTile d : c) {
				RSObject o = objects.getTopAt(d);
				if (o != null) {
					boolean isObject = false;
					for (int id : IDs) {
						if (a.contains(o.getLocation()) && o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						double distTmp = calculate.distance(player.getMine()
								.getLocation(), o.getLocation());
						if (cur == null) {
							dist = distTmp;
							cur = o;
						} else if (hugePlace && distTmp < dist
								&& !someoneMining(o)) {
							cur = o;
							dist = distTmp;
						} else if (!hugePlace && distTmp < dist) {
							cur = o;
							dist = distTmp;
						}
					}
				}
			}
		}
		return cur;
	}

	public boolean someoneMining(RSObject obj) {
		RSArea o = new RSArea(new RSTile(obj.getLocation().getX() - 1, obj
				.getLocation().getY() - 1), new RSTile(
				obj.getLocation().getX() + 1, obj.getLocation().getY() + 1));
		if (calculate.distanceTo(obj) < 2)
			return false;
		if (getPlayerCount(o) > 0) {
			return true;
		}
		return false;
	}

	public int getPlayerCount(RSArea a) {
		int c = 0;
		int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		org.rsbot.client.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();

		for (int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			RSPlayer p = new RSPlayer(players[element]);
			try {
				if (a.contains(p.getLocation())
						&& !p.getName().equals(player.getMine().getName())) {
					c++;
				}
			} catch (Exception ignored) {
			}
		}
		return c;
	}

	private Point getModelPoint(final RSObject obj) {
		final LDModel model = (LDModel) obj.getModel();
		final int[] xPoints = model.getXPoints(), yPoints = model.getYPoints(), zPoints = model
				.getZPoints();

		final int i = random(0, model.getIndices3().length);
		final int i1 = model.getIndices1()[i], i2 = model.getIndices2()[i], i3 = model
				.getIndices3()[i];

		final RSAnimable animable = (RSAnimable) obj.getObject();
		final int ax = animable.getX(), ay = animable.getY();

		final Point[] indicePoints = new Point[3];
		indicePoints[0] = Calculations.w2s(xPoints[i1] + ax, yPoints[i1]
				+ Calculations.tileHeight(ax, ay), zPoints[i1] + ay);
		indicePoints[1] = Calculations.w2s(xPoints[i2] + ax, yPoints[i2]
				+ Calculations.tileHeight(ax, ay), zPoints[i2] + ay);
		indicePoints[2] = Calculations.w2s(xPoints[i3] + ax, yPoints[i3]
				+ Calculations.tileHeight(ax, ay), zPoints[i3] + ay);

		final int xPoint = blend(
				min(indicePoints[0].x, indicePoints[1].x, indicePoints[2].x),
				max(indicePoints[0].x, indicePoints[1].x, indicePoints[2].x),
				random(0.0, 1.0));
		final int[][] xIndexes = new int[2][2];
		for (int xIndex = 0, xIndexCount = 0; xIndex < 3 && xIndexCount < 2; xIndex++) {
			final int x1 = indicePoints[xIndex].x;
			final int x2 = indicePoints[xIndex == 2 ? 0 : xIndex + 1].x;
			if (Math.min(x1, x2) <= Math.max(x1, x2)) {
				xIndexes[xIndexCount++] = new int[] { xIndex,
						xIndex == 2 ? 0 : xIndex + 1 };
			}
		}
		final int d1 = Math.min(indicePoints[xIndexes[0][0]].x,
				indicePoints[xIndexes[0][1]].x)
				+ Math.abs(indicePoints[xIndexes[0][0]].x
						- indicePoints[xIndexes[0][1]].x);
		final int d2 = Math.min(indicePoints[xIndexes[1][0]].x,
				indicePoints[xIndexes[1][1]].x)
				+ Math.abs(indicePoints[xIndexes[1][0]].x
						- indicePoints[xIndexes[1][1]].x);
		final double xRatio1 = d1 == 0 ? 0.0 : xPoint / d1;
		final double xRatio2 = d2 == 0 ? 0.0 : xPoint / d2;
		final int yLimit1 = (int) (Math.abs(indicePoints[xIndexes[0][0]].y
				- indicePoints[xIndexes[0][1]].y) * xRatio1);
		final int yLimit2 = (int) (Math.abs(indicePoints[xIndexes[1][0]].y
				- indicePoints[xIndexes[1][1]].y) * xRatio2);

		final int yPoint = min(indicePoints[0].y, indicePoints[1].y,
				indicePoints[2].y) + random(yLimit1, yLimit2);

		return new Point(xPoint, yPoint);
	}

	private int blend(final int a, final int b, final double factor) {
		return (int) (Math.min(a, b) + Math.abs(a - b) * factor);
	}

	private int min(final int... values) {
		int min = values[0];
		for (final int value : values) {
			if (value < min) {
				min = value;
			}
		}

		return min;
	}

	private int max(final int... values) {
		int max = values[0];
		for (final int value : values) {
			if (value > max) {
				max = value;
			}
		}

		return max;
	}

	/*
	 * Walking Block Starts Updated with better functions and stuff :)
	 * 
	 * @Author - Henry/Henry`/Henry#/sm1l3
	 */

	public void energyManagement() {
		if (player.getMyEnergy() >= 40)
			game.setRun(true);
		if (player.getMyEnergy() < 10 && energyManagement) {
			player.rest();
		}
	}

	public boolean walking(int a) {
		if (a == 1) {
			energyManagement();
			walkPathMM2(currentPath);
		}
		if (a == 2) {
			energyManagement();
			walkPathMM2(walk.reversePath(currentPath));
		}
		if (a == 3) {
			energyManagement();
			walkPathMM2(currentPath2);
		}
		if (a == 4) {
			energyManagement();
			walkPathMM2(walk.reversePath(currentPath2));
		}
		return true;
	}

	public void walkPathMM2(RSTile[] a) {
		int c = 0;
		int fail2 = 0;
		while (calculate.distanceTo(a[a.length - 1]) > 3) {
			RSTile b = a[c];
			if (calculate.distanceTo(b) > 15)
				return;
			if (calculate.distanceTo(b) < 4)
				c++;
			b = a[c];
			if (calculate.distanceTo(b) < 4)
				c++;
			if (calculate.distanceTo(b) > 15)
				return;
			walk.tileMM(b);
			if (player.getMine().isMoving())
				fail2 = 0;
			if (fail2 >= 50)
				return;
			fail2++;
			int fail = 0;
			while (calculate.distanceTo(b) > 3) {
				wait(100);
				fail++;
				if (player.getMine().isMoving())
					fail = 0;
				if (fail >= 50) {
					c--;
					break;
				}
			}
			c++;
		}
	}

	public RSTile getNearestSpotInPath(RSTile[] a) {
		for (RSTile b : a) {
			if (calculate.distanceTo(b) < 13)
				return b;
		}
		return null;
	}

	/*
	 * Banking Block Starts
	 * 
	 * @Author - Henry/Henry`/Henry#/sm1l3
	 */

	public boolean depositBoxDepositExcept(final int... items) {
		if (iface.get(11).getChild(17).isValid()) {
			wait(random(400, 800));
			boolean depositAll = true;
			for (RSInterfaceChild i : iface.get(11)
					.getChild(17).getChildren()) {
				for (int a : items) {
					if (a == i.getChildID()) {
						depositAll = false;
					}
				}
			}
			if (depositAll) {
				iface.clickChild(iface.get(11).getChild(19));
				wait(random(500, 1000));
			} else {
				for (RSInterfaceChild i : iface.get(11)
						.getChild(17).getChildren()) {
					boolean okToBank = true;
					int item = i.getChildID();
					if (item != -1) {
						for (int a : items) {
							if (a == i.getChildID()) {
								okToBank = false;
							}
						}
						if (okToBank) {
							iface.clickChild(i, "Deposit-All");
							int fail = 0;
							while (i.getChildID() == item) {
								fail++;
								wait(100);
								if (fail >= 100)
									return false;
							}
						}
					}
				}
			}
			return iface.clickChild(iface.get(11).getChild(15));
		}
		return false;
	}

	public boolean depositAllExcept(final int... items) {
		int inventoryCount = inventory.getCount();
		int[] inventoryArray = inventory.getArray();
		outer: for (int off = 0; off < inventoryArray.length; off++) {
			if (inventoryArray[off] == -1) {
				continue;
			}
			for (final int item : items) {
				if (inventoryArray[off] == item) {
					continue outer;
				}
			}
			if (inventory.contains(inventoryArray[off])) {
				inventory.clickItem(inventoryArray[off], "Deposit-All");
				int fail = 0;
				while (inventory.contains(inventoryArray[off])) {
					fail++;
					wait(100);
					if (30 >= fail)
						break;
				}
			}
			if (inventory.getCount() >= inventoryCount)
				return false;
			inventoryArray = inventory.getArray();
			inventoryCount = inventory.getCount();
		}
		return true;
	}

	public boolean openBank(int a) {
		if (bank.isOpen())
			return true;
		if (a == 0 || a == 2 || a == 3) {
			int[] thing = Bank.BankBooths;
			String cmd = "Use-quickly";
			if (a == 0) {
				thing = Bank.BankBooths;
				cmd = "Use-quickly";
			}
			if (a == 2) {
				thing = Bank.BankChests;
				cmd = "Bank";
			}
			if (a == 3) {
				if (currentBankArea == porSarimDepositBoxArea) {
					thing = Bank.BankDepositBox;
					thing[thing.length - 1] = 36788;
				} else {
					thing = Bank.BankDepositBox;
					thing[0] = 25937;
				}
				cmd = "Deposit";
			}
			RSObject bb = getNearestObjectByID2(thing);
			if (bb != null) {
				bb.action(cmd);
				int fail = 0;
				if (!isDepositBox) {
					while (!bank.isOpen()) {
						fail++;
						if (player.getMine().isMoving())
							fail = 0;
						wait(100);
						if (fail >= 50)
							return false;
					}
				} else {
					while (!iface.get(11).isValid()) {
						fail++;
						if (player.getMine().isMoving())
							fail = 0;
						wait(100);
						if (fail >= 50)
							return false;
					}
				}
				return true;
			}
			return false;
		} else if (a == 1) {
			RSNPC bn = getNearestNPCByID2(Bank.Bankers);
			if (bn != null) {
				bn.action("Bank Banker");
				int fail = 0;
				while (!bank.isOpen()) {
					fail++;
					if (player.getMine().isMoving())
						fail = 0;
					wait(100);
					if (fail >= 50)
						return false;
				}
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean waitFor(int id, int x, int sec) {
		int fail = 0;
		if (x > 0) {
			while (!inventory.contains(id) && inventory.getCount(id) != x) {
				fail++;
				wait(100);
				if (sec * 10 >= fail)
					return false;
			}
		} else {
			while (!inventory.contains(id)) {
				fail++;
				wait(100);
				if (sec * 10 >= fail)
					return false;
			}
		}
		return true;
	}

	public boolean waitTill(int id, int sec) {
		if (!inventory.contains(id))
			return true;
		int fail = 0;
		while (inventory.contains(id)) {
			fail++;
			wait(100);
			if (sec * 10 >= fail)
				return false;
		}
		return true;
	}

	public RSObject getNearestObjectByID2(int[] ids) {
		RSTile aa = null;
		for (int i = 0; i < 4; i++) {
			if (i == 0) {
				aa = new RSTile(player.getMyLocation().getX() + 1,
						player.getMyLocation().getY());
			}
			if (i == 1) {
				aa = new RSTile(player.getMyLocation().getX() - 1,
						player.getMyLocation().getY());
			}
			if (i == 2) {
				aa = new RSTile(player.getMyLocation().getX(),
						player.getMyLocation().getY() + 1);
			}
			if (i == 3) {
				aa = new RSTile(player.getMyLocation().getX(),
						player.getMyLocation().getY() - 1);
			}
			RSObject a = objects.getTopAt(aa);
			if (a != null) {
				for (int id : ids) {
					if (a.getID() == id)
						return a;
				}
			}
		}
		return objects.getNearestByID(ids);
	}

	public RSNPC getNearestNPCByID2(int[] ids) {
		RSTile aa = null;
		for (int i = 0; i < 4; i++) {
			if (i == 0) {
				aa = new RSTile(player.getMyLocation().getX() + 2,
						player.getMyLocation().getY());
			}
			if (i == 1) {
				aa = new RSTile(player.getMyLocation().getX() - 2,
						player.getMyLocation().getY());
			}
			if (i == 2) {
				aa = new RSTile(player.getMyLocation().getX(),
						player.getMyLocation().getY() + 2);
			}
			if (i == 3) {
				aa = new RSTile(player.getMyLocation().getX(),
						player.getMyLocation().getY() - 2);
			}
			for (RSNPC a : getNPCArray(false)) {
				for (int id : ids) {
					if (a.getID() == id && a.getLocation().equals(aa))
						return a;
				}
			}
		}
		return npc.getNearestByID(ids);
	}

	/*
	 * World Switching
	 * 
	 * Made by Henry
	 */

	// True if free world, false if p2p
	public boolean switchWorld2(boolean fm) {
		try {
			if (!game.isWelcomeScreen()) {// We are in game
				mouse.click(random(750, 758), random(3, 13), true);
				int fail = 0;
				while (game.getCurrentTab() != 16) {
					fail++;
					wait(100);
					if (fail >= 100)
						return false;
				}
				wait(500);
				iface.clickChild(iface.get(182).getChild(6));
				fail = 0;
				while (!game.isWelcomeScreen()) {
					fail++;
					wait(100);
					if (fail >= 200)
						return false;
				}
			}
			while (!iface.get(910).isValid())
				wait(100);
			if (game.isWelcomeScreen()) {
				mouse.click(iface.get(906).getChild(199)
						.getPosition(), true);
				int fail = 0;
				while (iface.get(910).getChild(71)
						.getChildren().length < 2) {// Wait until the world
														// list is loaded
					fail++;
					wait(100);
					if (fail >= 100)
						return false;
				}
				if (needsSorting()) {// Check if it needs to be sorted by world
										// id's
					mouse.click(iface.get(910).getChild(54)
							.getChildren()[1].getPoint(), true);
					wait(random(1000, 2000));
				}
				ArrayList<Integer> freeworlds = new ArrayList<Integer>();
				ArrayList<Integer> paidworlds = new ArrayList<Integer>();
				for (RSInterfaceChild i : iface.get(910)
						.getChild(69).getChildren()) {// Find p2p and f2p
														// worlds available
					if (i.getBackgroundColor() == 1532
							&& !iface.get(910).getChild(71)
									.getChildren()[i.getChildIndex()]
									.getText().contains("PvP")
							&& !iface.get(910).getChild(71)
									.getChildren()[i.getChildIndex()]
									.getText().contains("Bounty")) {
						freeworlds.add(i.getChildIndex());
					}
					if (i.getBackgroundColor() == 1531
							&& !iface.get(910).getChild(71)
									.getChildren()[i.getChildIndex()]
									.getText().contains("PvP")
							&& !iface.get(910).getChild(71)
									.getChildren()[i.getChildIndex()]
									.getText().contains("Bounty")) {
						paidworlds.add(i.getChildIndex());
					}
				}
				int b = 0;
				if (fm) {// Get a free world
					while (b == 0) {
						int a = freeworlds
								.get(random(0, freeworlds.size() - 1));
						int c = Integer.parseInt(iface.get(910)
								.getChild(68).getChildren()[a].getText());
						if (c != 1 && c != 2 && c != 3 && c != currentWorld()
								&& !usedWorlds.contains(c)) {
							b = c;
						}
					}
				} else {
					while (b == 0) {
						int a = paidworlds
								.get(random(0, paidworlds.size() - 1));
						int c = Integer.parseInt(iface.get(910)
								.getChild(68).getChildren()[a].getText());
						if (c != 1 && c != 2 && c != 3 && c != currentWorld()
								&& !usedWorlds.contains(c)) {
							b = c;
						}
					}
				}
				usedWorlds.add(b);
				if (usedWorlds.size() > 9) {
					usedWorlds.remove(0);
				}
				log("Switching to world - " + b);
				mouse.click(699, (int) getYForClick(b), true);// Double click for
																// safety
				wait(random(100, 200));
				mouse.click(699, (int) getYForClick(b), true);
				wait(random(500, 600));
				clickWorld();
				wait(random(250, 500));
				if (currentWorld() != b) {// If not correct world then fix it
					checkWorld(b);
					wait(random(250, 500));
				}
				mouse.click(random(75, 575), random(443, 478), true);
				fail = 0;
				while (iface.get(910).isValid()) {// And while
																	// loop for
																	// waiting
																	// time.
					fail++;
					wait(100);
					if (fail >= 400)
						return false;
				}
				return true;
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	public int getTimeLeft() {// Gets waiting time left when trying to enter a
								// world
		if (iface.get(906).getChild(36).getText().length() < 2)
			return -1;
		String a = iface.get(906)
				.getChild(36)
				.getText()
				.replace(
						"YOU HAVE ONLY JUST LEFT ANOTHER WORLD. YOUR PROFILE WILL BE TRANSFERRED IN<br>",
						"");
		a.replace(" SECOND.", "");
		int b = Integer.parseInt(a);
		return b;
	}

	public boolean needsSorting() {// Checks 10 first worlds to see if they need
									// sorting
		int c = 0;
		for (RSInterfaceChild i : iface.get(910)
				.getChild(68).getChildren()) {
			if (Integer.parseInt(i.getText()) != (c + 1)) {
				return true;
			}
			if (c >= 10)
				return false;
			c++;
		}
		return true;
	}

	// World number, if no such world returns false.
	public boolean switchWorld2(int w) {
		try {
			boolean OK = false;
			for (RSInterfaceChild i : iface.get(910)
					.getChild(68).getChildren()) {
				if (Integer.parseInt(i.getText()) == w) {
					OK = true;
				}
			}
			if (!OK)
				return false;
			if (!game.isWelcomeScreen()) {
				mouse.click(random(750, 758), random(3, 13), true);
				int fail = 0;
				while (game.getCurrentTab() != 16) {
					fail++;
					wait(100);
					if (fail >= 100)
						return false;
				}
				iface.clickChild(iface.get(182).getChild(7));
				fail = 0;
				while (!game.isWelcomeScreen()) {
					fail++;
					wait(100);
					if (fail >= 100)
						return false;
				}
			}
			while (!iface.get(910).isValid())
				wait(100);
			if (game.isWelcomeScreen()) {
				mouse.click(iface.get(906).getChild(199)
						.getPosition(), true);
				int fail = 0;
				while (iface.get(910).getChild(71)
						.getChildren().length < 2) {
					fail++;
					wait(100);
					if (fail >= 100)
						return false;
				}
				if (needsSorting()) {
					mouse.click(iface.get(910).getChild(54)
							.getChildren()[1].getPoint(), true);
					wait(random(1000, 2000));
				}
				mouse.click(699, (int) getYForClick(w), true);
				wait(random(100, 200));
				mouse.click(699, (int) getYForClick(w), true);
				wait(random(500, 600));
				clickWorld();
				wait(random(250, 500));
				if (currentWorld() != w) {
					checkWorld(w);
					wait(random(250, 500));
				}
				mouse.click(random(75, 575), random(443, 478), true);
				fail = 0;
				while (iface.get(910).isValid()) {
					fail++;
					wait(100);
					if (fail >= 400)
						return false;
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	public void clickWorld() {// Click a supposed world, 60-80% correct one
		mouse.click(
				iface.get(910).getChild(76).getChildren()[7]
						.getPoint(),
				true);
	}

	public int currentWorld() {// Get the current world
		if (iface.get(910).getChild(10).getText() != null) {
			return 0;
		}
		String a = iface.get(910).getChild(10).getText()
				.replace("World ", "");
		int b = Integer.parseInt(a);
		return b;
	}

	public boolean checkWorld(int w) {// Check and fix our mistake
		int b = 0;
		if (iface.get(910).getChild(10).getText() != null) {
			String a = iface.get(910).getChild(10).getText()
					.replace("World ", "");
			b = Integer.parseInt(a);
		}
		if (b == w) {// No need
			return true;
		} else {// Get the current and wanted world
			RSInterfaceChild f = null;
			RSInterfaceChild g = null;
			for (RSInterfaceChild i : iface.get(910)
					.getChild(68).getChildren()) {
				if (Integer.parseInt(i.getText()) == w) {
					f = i;
				}
				if (Integer.parseInt(i.getText()) == b) {
					g = i;
				}
			}
			if (f == null)
				return false;
			if (g == null)
				return false;
			int c = f.getChildIndex() - g.getChildIndex();// Get the
																	// number of
																	// components
																	// between
																	// them
			int x = 0;
			x = c * 20;// 20 is the height of a world box
			Point d = iface.get(910).getChild(76)
					.getChildren()[7].getPoint();// Get the current point
			Point e = new Point((int) d.getX(), (int) d.getY() + x);// Fix it
			mouse.click(e, true);
			return true;
		}
	}

	public int getYForClick(int w) {
		RSInterfaceChild a = null;
		for (RSInterfaceChild i : iface.get(910)
				.getChild(68).getChildren()) {
			if (Integer.parseInt(i.getText()) == w) {
				a = i;
			}
		}// Get our wanted component
		double b = (double) a.getRelativeY() / 3000;// 3000 is the total height
													// of the box
		b = b * 100;
		return (int) Math.round(b * 2.06 + 214);// some rounding and we have our
												// number
	}

	/*
	 * 
	 * GUI
	 * 
	 * @Author - Henry/Henry#/Henry`/Sm1l3
	 */

	public class ahh extends JFrame {
		private static final long serialVersionUID = 1L;

		private void loadSettings(final File file) {
			if (!file.exists()) {
				return;
			}
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));

				if (Integer.parseInt(in.readLine()) != Sv) {
					log("Wrong version settings. Deleted the file.");
					file.delete();
					return;
				}

				comboBox1.setSelectedItem(in.readLine());
				comboBox2.setSelectedItem(in.readLine());
				comboBox3.setSelectedItem(in.readLine());
				comboBox4.setSelectedItem(in.readLine());
				comboBox5.setSelectedItem(in.readLine());
				comboBox6.setSelectedItem(in.readLine());
				comboBox7.setSelectedItem(in.readLine());
				comboBox8.setSelectedItem(in.readLine());
				textField1.setText(in.readLine());
				textField2.setText(in.readLine());
				comboBox9.setSelectedItem(in.readLine());
				comboBox10.setSelectedItem(in.readLine());
				comboBox11.setSelectedItem(in.readLine());
				comboBox12.setSelectedItem(in.readLine());
				checkBox1.setSelected(toBoolean(in.readLine()));

			} catch (final IOException e) {
				log("Can't read settings file: " + e.getMessage());
			} finally {
				try {
					in.close();
				} catch (final IOException e) {
					log("Can't close stream: " + e);
				}
			}
		}

		private void saveSettings(final File file) {
			DataOutputStream out = null;
			try {
				out = new DataOutputStream(new BufferedOutputStream(
						new FileOutputStream(file)));

				out.writeBytes("" + Sv + "\n");
				out.writeBytes(comboBox1.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox2.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox3.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox4.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox5.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox6.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox7.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox8.getSelectedItem().toString() + "\n");
				out.writeBytes(textField1.getText() + "\n");
				out.writeBytes(textField2.getText() + "\n");
				out.writeBytes(comboBox9.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox10.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox11.getSelectedItem().toString() + "\n");
				out.writeBytes(comboBox12.getSelectedItem().toString() + "\n");
				out.writeBytes(toString(checkBox1.isSelected()) + "\n");
			} catch (final IOException e) {
				log("Can't save settings: " + e.getMessage());
			} finally {
				try {
					out.close();
				} catch (final IOException e) {
					log("Can't close settings stream: " + e);
				}
			}
		}

		public boolean toBoolean(String a) {
			if (a.equals("True"))
				return true;
			return false;
		}

		public String toString(Boolean a) {
			if (a)
				return "true";
			return "false";
		}

		public ahh() {
			initComponents();
			loadSettings(Sf);
			setVisible(true);
			setAlwaysOnTop(true);
		}

		private void comboBox1ActionPerformed(ActionEvent e) {
			if (comboBox1.getSelectedItem().toString().contains("Clay")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Varrock West", "Yanille Mine" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Silver")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Varrock West", "Al Kharid" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Tin")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Dwarven Mine", "Rimmington", "Varrock East",
						"Varrock West", "Yanille Mine" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Copper")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Dwarven Mine", "Rimmington", "Varrock East" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Iron")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Dwarven Mine", "Rimmington", "Varrock East",
						"Al Kharid", "Varrock West", "Yanille Mine",
						"South-East Ardougne" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Coal")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Resource dungeon (15)", "Mining Guild",
						"Dwarven Mine", "Lumbridge West", "Al Kharid",
						"Barbarian Village", "South-East Ardougne" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Gold")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Dwarven Mine", "Rimmington", "Al Kharid" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Mithril")) {
				comboBox2.setModel(new DefaultComboBoxModel(new String[] {
						"Resource dungeon (15)", "Resource dungeon (45)",
						"Mining Guild", "Dwarven Mine", "Lumbridge West",
						"Al Kharid", "Yanille Mine" }));
			}
			if (comboBox1.getSelectedItem().toString().contains("Adamant")) {
				comboBox2
						.setModel(new DefaultComboBoxModel(new String[] {
								"Resource dungeon (45)", "Dwarven Mine",
								"Al Kharid" }));
			}
		}

		private void comboBox2ActionPerformed(ActionEvent e) {
			if (comboBox2.getSelectedItem().toString()
					.contains("Resource dungeon (15)")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Resource dungeon (15)" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("Resource dungeon (45)")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Falador Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox2.getSelectedItem().toString().contains("Mining Guild")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Falador Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox2.getSelectedItem().toString().contains("Dwarven Mine")) {
				comboBox3.setModel(new DefaultComboBoxModel(new String[] {
						"Falador Bank", "Resource dungeon (15)" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					comboBox12.setModel(new DefaultComboBoxModel(new String[] {
							"Off", "Iron" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Coal")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Copper")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Tin")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Rimmington")) {
				comboBox3.setModel(new DefaultComboBoxModel(new String[] {
						"Falador Bank", "Port Sarim DepositBox" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					comboBox12.setModel(new DefaultComboBoxModel(new String[] {
							"Off", "Iron" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Copper")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Tin")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Varrock East")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Varrock East Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					comboBox12.setModel(new DefaultComboBoxModel(new String[] {
							"Off", "Iron" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Copper")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Tin")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Varrock West")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Varrock West Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					comboBox12.setModel(new DefaultComboBoxModel(new String[] {
							"Off", "Iron" }));
				} else {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				}
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("Lumbridge West")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Draynor Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox2.getSelectedItem().toString().contains("Al Kharid")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Al Kharid Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					comboBox12.setModel(new DefaultComboBoxModel(new String[] {
							"Off", "Iron" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Copper")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Coal")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else if (comboBox1.getSelectedItem().toString()
						.contains("Tin")) {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				} else {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				}
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("Barbarian Village")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Edgeville Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(
						new String[] { "Off" }));
			}
			if (comboBox2.getSelectedItem().toString().contains("Yanille Mine")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Yanille Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					comboBox12.setModel(new DefaultComboBoxModel(new String[] {
							"Off", "Iron" }));
				} else {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				}
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("South-East Ardougne")) {
				comboBox3.setModel(new DefaultComboBoxModel(
						new String[] { "Ardougne Bank" }));
				comboBox12.setModel(new DefaultComboBoxModel(new String[] {
						"Off", "Iron" }));
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					comboBox12.setModel(new DefaultComboBoxModel(new String[] {
							"Off", "Iron" }));
				} else {
					comboBox12.setModel(new DefaultComboBoxModel(
							new String[] { "Off" }));
				}
			}
		}

		private void button1ActionPerformed(ActionEvent e) {
			/*
			 * Dwarven Mine Rimmington Varrock East Al Kharid Resource dungeon
			 * (15) Mining Guild Lumbridge West Resource dungeon (45) Barbarian
			 * Village
			 */
			worldHoping = true;
			isDung = false;
			if (comboBox2.getSelectedItem().toString()
					.contains("Barbarian Village")) {
				loc = 1;
				currentPath = edgeBankToBarb;
				currentMineArea = barbMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Coal")) {
					currentAreas = barbCoalAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("Resource dungeon (45)")) {
				loc = 2;
				currentPath = faladorBankToGuildEntrance;
				currentPath2 = rd2ToBank;
				currentMineArea = rd2MineArea;
				isDung = true;
				isLadder = true;
				currentLadderAreas = guildLadderAreas;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Mithril")) {
					currentAreas = rd2MithrilAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Adamant")) {
					currentAreas = rd2AddyAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("Lumbridge West")) {
				loc = 3;
				currentPath = draynorBankToLumby;
				currentMineArea = lumbyMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Mithril")) {
					currentAreas = lumbyMithrilArea;
				}
				if (comboBox1.getSelectedItem().toString().contains("Coal")) {
					currentAreas = lumbyCoalArea;
				}
				if (comboBox1.getSelectedItem().toString().contains("Adamant")) {
					currentAreas = lumbyAddyArea;
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Mining Guild")) {
				loc = 4;
				currentPath = faladorBankToGuildEntrance;
				currentMineArea = guildMineArea;
				currentLadderAreas = guildLadderAreas;
				currentPath2 = guildEntranceToGuild;
				isLadder = true;
				isDung = false;
				hugePlace = true;
				ladderD = 2113;
				ladderU = 6226;
				if (comboBox1.getSelectedItem().toString().contains("Coal")) {
					currentAreas = guildAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("Resource dungeon (15)")) {
				loc = 5;
				currentPath = rdToBank;
				currentMineArea = rdMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Mithril")) {
					currentAreas = rdMithrilAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Coal")) {
					currentAreas = rdCoalAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Al Kharid")) {
				loc = 6;
				currentPath = akBankToMine;
				currentMineArea = akMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					currentAreas = akIronAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Coal")) {
					currentAreas = akCoalAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Gold")) {
					currentAreas = akGoldAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Mithril")) {
					currentAreas = akMithrilAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Adamant")) {
					currentAreas = akAddyArea;
				}
				if (comboBox1.getSelectedItem().toString().contains("Silver")) {
					currentAreas = akSilverArea;
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Varrock East")) {
				loc = 7;
				currentPath = vEBankToVEMine;
				currentMineArea = vEMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Tin")) {
					currentAreas = vETineAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Copper")) {
					currentAreas = vECopperAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					currentAreas = vEIronAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Rimmington")) {
				loc = 8;
				if (comboBox3.getSelectedItem().toString()
						.contains("Port Sarim DepositBox")) {
					currentPath = portSarimToRimmington;
				} else {
					currentPath = faladorBankToRimmington;
				}
				currentMineArea = rimmingtonMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Tin")) {
					currentAreas = rimmingtonTinAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Copper")) {
					currentAreas = rimmingtonCopperAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					currentAreas = rimmingtonIronAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Gold")) {
					currentAreas = rimmingtonGoldAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Dwarven Mine")) {
				loc = 9;
				if (comboBox3.getSelectedItem().toString()
						.contains("Resource dungeon (15)")) {
					currentPath = faladorBankToDwarvenMineEntrance;
					currentPath2 = ToDwarvenMine2;
					specialBank = true;
				} else {
					currentPath = faladorBankToDwarvenMineEntrance;
					currentPath2 = ToDwarvenMine;
					specialBank = false;
				}
				currentMineArea = dwarvenMineArea;
				currentLadderAreas = dwarvenLadderAreas;
				isLadder = true;
				hugePlace = false;
				ladderD = 30944;
				ladderU = 30943;
				if (comboBox1.getSelectedItem().toString().contains("Tin")) {
					currentAreas = dwarvenTinAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Copper")) {
					currentAreas = dwarvenCopperAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					currentAreas = dwarvenIronAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Coal")) {
					currentAreas = dwarvenCoalAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Gold")) {
					currentAreas = dwarvenGoldAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Mithril")) {
					currentAreas = dwarvenMithrilAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Adamant")) {
					currentAreas = dwarvenAddyAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Varrock West")) {
				loc = 10;
				currentPath = vWBankToMine;
				currentMineArea = vWMineArea;
				isLadder = false;
				isDung = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Tin")) {
					currentAreas = vWTinAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Clay")) {
					currentAreas = vWClayAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					currentAreas = vWIronAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Silver")) {
					currentAreas = vWSilverAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString().contains("Yanille Mine")) {
				loc = 11;
				currentPath = yBankToMine;
				currentMineArea = yMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					currentAreas = yIronArea;
				}
				if (comboBox1.getSelectedItem().toString().contains("Clay")) {
					currentAreas = yClayAreas;
				}
				if (comboBox1.getSelectedItem().toString().contains("Tin")) {
					currentAreas = yTinAreas;
				}
			}
			if (comboBox2.getSelectedItem().toString()
					.contains("South-East Ardougne")) {
				loc = 12;
				currentPath = seaBankToMine;
				currentMineArea = seaMineArea;
				isLadder = false;
				hugePlace = false;
				if (comboBox1.getSelectedItem().toString().contains("Iron")) {
					currentAreas = seaIronArea;
				}
				if (comboBox1.getSelectedItem().toString().contains("Coal")) {
					currentAreas = seaCoalArea;
				}
			}
			if (comboBox1.getSelectedItem().toString().contains("Tin")) {
				ore = 1;
				currentOreID = tinOreID;
				currentOreInventoryID = tinOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Copper")) {
				ore = 2;
				currentOreID = copperOreID;
				currentOreInventoryID = copperOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Iron")) {
				ore = 3;
				currentOreID = ironOreID;
				currentOreInventoryID = ironOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Coal")) {
				ore = 4;
				currentOreID = coalOreID;
				currentOreInventoryID = coalOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Gold")) {
				ore = 5;
				currentOreID = goldOreID;
				currentOreInventoryID = goldOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Mithril")) {
				ore = 6;
				currentOreID = mithrilOreID;
				currentOreInventoryID = mithrilOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Adamant")) {
				ore = 7;
				currentOreID = addyOreID;
				currentOreInventoryID = addyOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Clay")) {
				ore = 8;
				currentOreID = clayOreID;
				currentOreInventoryID = clayOreInventoryID;
			}
			if (comboBox1.getSelectedItem().toString().contains("Silver")) {
				ore = 9;
				currentOreID = silverOreID;
				currentOreInventoryID = silverOreInventoryID;
			}
			if (comboBox3.getSelectedItem().toString()
					.contains("Varrock East Bank")) {
				ban = 1;
				currentBankArea = vEbankArea;
				isDepositBox = false;
			}
			if (comboBox3.getSelectedItem().toString()
					.contains("Port Sarim DepositBox")) {
				ban = 2;
				currentBankArea = porSarimDepositBoxArea;
				isDepositBox = true;
			}
			if (comboBox3.getSelectedItem().toString()
					.contains("Edgeville Bank")) {
				ban = 3;
				currentBankArea = edgeBankArea;
				isDepositBox = false;
			}
			if (comboBox3.getSelectedItem().toString().contains("Draynor Bank")) {
				ban = 4;
				currentBankArea = draynodBankArea;
				isDepositBox = false;
			}
			if (comboBox3.getSelectedItem().toString().contains("Falador Bank")) {
				ban = 5;
				currentBankArea = faladorBankArea;
				isDepositBox = false;
			}
			if (comboBox3.getSelectedItem().toString()
					.contains("Resource dungeon (15)")) {
				ban = 6;
				currentBankArea = rdDepositBoxArea;
				isDepositBox = true;
			}
			if (comboBox3.getSelectedItem().toString()
					.contains("Al Kharid Bank")) {
				ban = 7;
				currentBankArea = akBankArea;
				isDepositBox = false;
			}
			if (comboBox3.getSelectedItem().toString()
					.contains("Varrock West Bank")) {
				ban = 8;
				currentBankArea = vWBankArea;
				isDepositBox = false;
			}
			if (comboBox3.getSelectedItem().toString().contains("Yanille Bank")) {
				ban = 9;
				currentBankArea = yBankArea;
				isDepositBox = false;
			}
			if (comboBox3.getSelectedItem().toString()
					.contains("Ardougne Bank")) {
				ban = 10;
				currentBankArea = ardougneBankArea;
				isDepositBox = false;
			}
			if (comboBox4.getSelectedItem().toString().contains("No")) {
				switching = 0;
			}
			if (comboBox4.getSelectedItem().toString().contains("Free W")) {
				switching = 1;
			}
			if (comboBox4.getSelectedItem().toString().contains("Paid W")) {
				switching = 2;
			}
			if (comboBox4.getSelectedItem().toString().contains("Both")) {
				switching = 3;
			}
			if (comboBox5.getSelectedItem().toString().contains("No")) {
				worldHoping = false;
			}
			if (comboBox6.getSelectedItem().toString().contains("Banking")) {
				BankStuff = true;
			}
			if (comboBox6.getSelectedItem().toString().contains("Droping")) {
				BankStuff = false;
			}
			smelting = false;
			if (comboBox12.getSelectedItem().toString().contains("Iron")) {
				smelting = true;
			}
			maxPlayers = Integer.parseInt(comboBox7.getSelectedItem()
					.toString());
			if (textField1.getText().length() > 2) {
				username = textField1.getText();
			} else {
				JOptionPane.showMessageDialog(textField1,
						"Username is less than 3 characters.", "1337 Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (textField2.getText().length() > 2) {
				password = textField2.getText();
			} else {
				JOptionPane.showMessageDialog(textField1,
						"Password is less than 3 characters.", "1337 Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (comboBox8.getSelectedItem().toString().contains("Drop")) {
				dropGems = true;
			} else {
				dropGems = false;
			}
			enableBreaking = checkBox1.isSelected();
			if (enableBreaking) {
				String[] a = sepa(comboBox9.getSelectedItem().toString());
				breakTime[0] = Integer.parseInt(a[0]);
				breakTime[1] = Integer.parseInt(a[1]);
				String[] b = sepa(comboBox10.getSelectedItem().toString());
				breakTime[2] = Integer.parseInt(b[0]);
				breakTime[3] = Integer.parseInt(b[1]);
			}
			if (comboBox11.getSelectedItem().toString().contains("On"))
				energyManagement = true;
			yesMouse = checkBox2.isSelected();
			saveSettings(Sf);
			setVisible(false);
			dispose();
		}

		@SuppressWarnings("null")
		private String[] sepa(String aString) {
			String[] splittArray = null;
			if (aString != null || !aString.equals("")) {
				splittArray = aString.split("\\-");
			}
			return splittArray;
		}

		private void formWindowClosing(java.awt.event.WindowEvent evt) {
			stopScript = true;
			stopScript(false);
			dispose();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			label1 = new JLabel();
			label2 = new JLabel();
			comboBox1 = new JComboBox();
			label3 = new JLabel();
			comboBox2 = new JComboBox();
			label4 = new JLabel();
			comboBox3 = new JComboBox();
			label5 = new JLabel();
			comboBox4 = new JComboBox();
			label6 = new JLabel();
			comboBox5 = new JComboBox();
			label7 = new JLabel();
			comboBox6 = new JComboBox();
			label8 = new JLabel();
			comboBox7 = new JComboBox();
			label9 = new JLabel();
			comboBox8 = new JComboBox();
			label16 = new JLabel();
			label10 = new JLabel();
			textField1 = new JTextField();
			label11 = new JLabel();
			textField2 = new JTextField();
			label17 = new JLabel();
			label12 = new JLabel();
			comboBox9 = new JComboBox();
			label13 = new JLabel();
			comboBox10 = new JComboBox();
			label14 = new JLabel();
			checkBox1 = new JCheckBox();
			button1 = new JButton();
			label15 = new JLabel();
			checkBox2 = new JCheckBox();
			label18 = new JLabel();
			comboBox11 = new JComboBox();
			label19 = new JLabel();
			comboBox12 = new JComboBox();

			// ======== this ========
			Container contentPane = getContentPane();
			setTitle("Henry's Miner");
			setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
			addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent evt) {
					formWindowClosing(evt);
				}
			});
			contentPane.setLayout(new GridBagLayout());
			((GridBagLayout) contentPane.getLayout()).columnWidths = new int[] {
					162, 165, 0 };
			((GridBagLayout) contentPane.getLayout()).rowHeights = new int[] {
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0 };
			((GridBagLayout) contentPane.getLayout()).columnWeights = new double[] {
					0.0, 0.0, 1.0E-4 };
			((GridBagLayout) contentPane.getLayout()).rowWeights = new double[] {
					0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4 };

			// ---- label1 ----
			label1.setText("Mining Options");
			label1.setFont(label1.getFont().deriveFont(
					label1.getFont().getStyle() | Font.BOLD));
			contentPane.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- label2 ----
			label2.setText("Ore to mine:");
			contentPane.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox1 ----
			comboBox1.setModel(new DefaultComboBoxModel(new String[] { "Clay",
					"Tin", "Copper", "Iron", "Silver", "Coal", "Gold",
					"Mithril", "Adamant" }));
			comboBox1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comboBox1ActionPerformed(e);
				}
			});
			contentPane.add(comboBox1, new GridBagConstraints(1, 1, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label3 ----
			label3.setText("Place to mine:");
			contentPane.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox2 ----
			comboBox2
					.setModel(new DefaultComboBoxModel(new String[] {
							"Dwarven Mine", "Rimmington", "Varrock East",
							"Al Kharid" }));
			comboBox2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					comboBox2ActionPerformed(e);
				}
			});
			contentPane.add(comboBox2, new GridBagConstraints(1, 2, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label4 ----
			label4.setText("Where to bank:");
			contentPane.add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox3 ----
			comboBox3.setModel(new DefaultComboBoxModel(new String[] { "" }));
			contentPane.add(comboBox3, new GridBagConstraints(1, 3, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label5 ----
			label5.setText("Switch Worlds when full:");
			contentPane.add(label5, new GridBagConstraints(0, 4, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox4 ----
			comboBox4.setModel(new DefaultComboBoxModel(new String[] { "No",
					"Free Worlds", "Paid Worlds", "Both Free and Paid" }));
			contentPane.add(comboBox4, new GridBagConstraints(1, 4, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label15 ----
			label15.setText("Hover mouse:");
			contentPane.add(label15, new GridBagConstraints(0, 5, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- checkBox2 ----
			checkBox2.setText("Yes");
			contentPane.add(checkBox2, new GridBagConstraints(1, 5, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label6 ----
			label6.setText("Hop worlds:");
			contentPane.add(label6, new GridBagConstraints(0, 6, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox5 ----
			comboBox5.setModel(new DefaultComboBoxModel(new String[] { "No",
					"Runite", "Adamant", "Gold" }));
			contentPane.add(comboBox5, new GridBagConstraints(1, 6, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label7 ----
			label7.setText("Mining Style:");
			contentPane.add(label7, new GridBagConstraints(0, 7, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox6 ----
			comboBox6.setModel(new DefaultComboBoxModel(new String[] {
					"Banking", "Droping" }));
			contentPane.add(comboBox6, new GridBagConstraints(1, 7, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label8 ----
			label8.setText("Max. people near you:");
			contentPane.add(label8, new GridBagConstraints(0, 8, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox7 ----
			comboBox7.setModel(new DefaultComboBoxModel(new String[] { "5",
					"10", "12", "15", "20" }));
			contentPane.add(comboBox7, new GridBagConstraints(1, 8, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label9 ----
			label9.setText("What do to with gems:");
			contentPane.add(label9, new GridBagConstraints(0, 9, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox8 ----
			comboBox8.setModel(new DefaultComboBoxModel(new String[] { "Bank",
					"Drop" }));
			contentPane.add(comboBox8, new GridBagConstraints(1, 9, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));
			// ---- label18 ----
			label18.setText("Resting:");
			contentPane.add(label18, new GridBagConstraints(0, 10, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox11 ----
			comboBox11.setModel(new DefaultComboBoxModel(new String[] { "On",
					"Off" }));
			contentPane.add(comboBox11, new GridBagConstraints(1, 10, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			// ---- label19 ----
			label19.setText("Smelting:");
			contentPane.add(label19, new GridBagConstraints(0, 11, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox12 ----
			comboBox12.setModel(new DefaultComboBoxModel(new String[] { "Off",
					"Bronze", "Iron", "Steel" }));
			contentPane.add(comboBox12, new GridBagConstraints(1, 11, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
			// ---- label16 ----
			label16.setText("Internet Options");
			label16.setFont(label16.getFont().deriveFont(
					label16.getFont().getStyle() | Font.BOLD));
			contentPane.add(label16, new GridBagConstraints(0, 12, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- label10 ----
			label10.setText("Username:");
			contentPane.add(label10, new GridBagConstraints(0, 13, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
			contentPane.add(textField1, new GridBagConstraints(1, 13, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			// ---- label11 ----
			label11.setText("Password:");
			contentPane.add(label11, new GridBagConstraints(0, 14, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));
			contentPane.add(textField2, new GridBagConstraints(1, 14, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			// ---- label17 ----
			label17.setText("Breaking Options");
			label17.setFont(label17.getFont().deriveFont(
					label17.getFont().getStyle() | Font.BOLD));
			contentPane.add(label17, new GridBagConstraints(0, 15, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- label12 ----
			label12.setText("Break every ");
			contentPane.add(label12, new GridBagConstraints(0, 16, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox9 ----
			comboBox9.setModel(new DefaultComboBoxModel(new String[] { "30-45",
					"60-120", "90-120", "120-180" }));
			contentPane.add(comboBox9, new GridBagConstraints(1, 16, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- label13 ----
			label13.setText("Break lenght");
			contentPane.add(label13, new GridBagConstraints(0, 17, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- comboBox10 ----
			comboBox10.setModel(new DefaultComboBoxModel(new String[] { "5-10",
					"10-15", "15-30", "30-60" }));
			contentPane.add(comboBox10, new GridBagConstraints(1, 17, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			// ---- label14 ----
			label14.setText("Enable Breaking");
			contentPane.add(label14, new GridBagConstraints(0, 18, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 5), 0, 0));

			// ---- checkBox1 ----
			checkBox1.setText("Yes");
			checkBox1.setSelected(true);
			contentPane.add(checkBox1, new GridBagConstraints(1, 18, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 5, 0), 0, 0));

			// ---- button1 ----
			button1.setText("Start the Script");
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button1ActionPerformed(e);
				}
			});
			contentPane.add(button1, new GridBagConstraints(0, 19, 2, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			pack();
			setLocationRelativeTo(getOwner());
			// JFormDesigner - End of component initialization
			// //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JLabel label1;
		private JLabel label2;
		private JComboBox comboBox1;
		private JLabel label3;
		private JComboBox comboBox2;
		private JLabel label4;
		private JComboBox comboBox3;
		private JLabel label5;
		private JComboBox comboBox4;
		private JLabel label6;
		private JComboBox comboBox5;
		private JLabel label7;
		private JComboBox comboBox6;
		private JLabel label8;
		private JComboBox comboBox7;
		private JLabel label9;
		private JComboBox comboBox8;
		private JLabel label16;
		private JLabel label10;
		private JTextField textField1;
		private JLabel label11;
		private JTextField textField2;
		private JLabel label17;
		private JLabel label12;
		private JComboBox comboBox9;
		private JLabel label13;
		private JComboBox comboBox10;
		private JLabel label14;
		private JCheckBox checkBox1;
		private JButton button1;
		private JLabel label15;
		private JCheckBox checkBox2;
		private JLabel label18;
		private JComboBox comboBox11;
		private JLabel label19;
		private JComboBox comboBox12;
		// JFormDesigner - End of variables declaration //GEN-END:variables
	}

}