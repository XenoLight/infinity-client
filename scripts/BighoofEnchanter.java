/*File: BighoofEnchanter.java
 *by Bobby Bighoof (Made 01-17-2010)
 *
 *02-08-2010 banking fix
 *
 *01-21-2010 version 1.02 spell correction to progreass and report paint. More
 *code clean up
 *
 *01-20-2010 version 1.01 worked in the tab math by "zzSleepzz". Added in progress
 *paint, and log report. Touched up banking wait times and did some clean up.
 *Added antiBan.
 *
 *01-17-2010 version 1.0 started with simple math to get this dream up and going.
 *
 *Thanks to: zzSleepzz for his anti random / inventory tab math.
 *It is a big help dude :)
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
@ScriptManifest
(authors = { "Bobby Bighoof" },
category = "Magic",
name = "Bighoof Enchanter",
version = 1.03,
description = "<html>"
		+ "<head><style type=text/css> body {background-color: #000000 </style>"
		+ "</head><center><h1><font color=#FFFFFF>Bighoof Enchanter</h1><br>"
		+ "<font color=#FFFFFF>Start in the bank you want to enchant in<br>"
		+ "with the staff your going to use equipped<br>"
		+ "and all the cosmic runes you need in your<br>"
		+ "inventory</head></font>"
		+ "<center><font size=2 color=#FF0000>This Script will "
		+ "Shut off randoms while it is running<br>"
		+ "Be sure to only start this after you have logged in.<br>"
		+ "It will not log in for you after it is started...<br><br>"
		+ "</font><table style=border-collapse:collapse cellpadding=0 cellspacing=0><tr>"
		+ "<td class=style2 align=right bgcolor=#00FFFF>"
		+ "</td><td class=style1 bgcolor=#00FFFF>"
		+ "</td></tr><tr><td class=style2 bgcolor=#C0C0C0 color=#000000>"
		+ "<font color=red>Items To Be Enchanted:</font>"
		+ "</td><td align=left class=style1 bgcolor=#C0C0C0 color=#000000>"
		+ "<select name=ctype><option>None<option>Ring Of Recoil<option>"
		+ "Games Necklace<option>Amulet Of Magic<option>Bracelet Of Clay<option>"
		+ "Ring Of Duelling<option>Necklace Of Binding<option>Amulet Of Defence"
		+ "<option>Castlewars Bracelet<option>Ring Of Forging<option>"
		+ "Digsite Pendant<option>Amulet Of Strength<option>Inoculation Brace"
		+ "<option>Ring Of Life<option>Phoenix Necklace<option>Amulet Of Power"
		+ "<option>Forinthry Bracelet<option>Amulet Of Glory<option>"
		+ "Skills Necklace<option>Combat Bracelet</select>"
		+ "</td></tr></table><br><center><font color=#ffffff>"
		+ "Thanks to zzSleepzz for his help with handleing<br>"
		+ "the multi tabbing of the inventory tab... :) thanks zz</font>"
		+ "</center></body></html>")
public class BighoofEnchanter extends Script implements PaintListener {
	final ScriptManifest properties = getClass().
			getAnnotation(ScriptManifest.class);
	/* TODO add in bankers to the banking ability
	 * This list should be complete but no garentee on if it is or not
	 */

	int[] clickID = { 1637, 1639, 1641, 1643, 1645, 1656, 1658, 1660, 1662, 1664,
			1694, 1696, 1698, 1700, 1702, 11072, 11076, 11085, 11092, 11115 };
	public int madeID;
	public int cosmicID = 564;
	public int child;
	public String chantedName;
	public String status = ("Game Loading...");
	private int startXP =0;
	private int startLVL =0;
	private long scriptStartTime;
	public boolean startScript = false;
	private boolean needToBank = true;
	public boolean setAltitude = false;
	public boolean useAntiBan = true;
	public boolean onStart(Map<String, String> args) {
		
		if (args.get("ctype").equals("Ring Of Recoil")) {
			madeID = 1637; chantedName = "Ring Of Recoil"; child = 29;
		}else if (args.get("ctype").equals("Ring Of Duelling")) {
			madeID = 1639; chantedName = "Ring Of Duelling"; child = 41;
		}else if (args.get("ctype").equals("Ring Of Forging")) {
			madeID = 1641; chantedName = "Ring Of Forging"; child = 53;
		}else if (args.get("ctype").equals("Ring Of Life")) {
			madeID = 1643; chantedName = "Ring Of Life"; child = 61;
		} else if (args.get("ctype").equals("Ring Of Wealth")) {
			madeID = 1645; chantedName = "Ring Of Wealth"; child = 76;
		} else if (args.get("ctype").equals("Games Necklace")) {
			madeID = 1656; chantedName = "Games Necklace"; child = 29;
		} else if (args.get("ctype").equals("Necklace Of Binding")) {
			madeID = 1658; chantedName = "Necklace Of Binding"; child = 41;
		} else if (args.get("ctype").equals("Digsite Pendant")) {
			madeID = 1660; chantedName = "Digsite Pendant"; child = 53;
		} else if (args.get("ctype").equals("Phoenix Necklace")) {
			madeID = 1662; chantedName = "Phoenix Necklace"; child = 61;
		} else if (args.get("ctype").equals("Skills Necklace")) {
			madeID = 1664; chantedName = "Skills Necklace"; child = 76;
		} else if (args.get("ctype").equals("Amulet Of Magic")) {
			madeID = 1694; chantedName = "Amulet Of Magic"; child = 29;
		} else if (args.get("ctype").equals("Amulet Of Defence")) {
			madeID = 1696; chantedName = "Amulet Of Defence"; child = 41;
		} else if (args.get("ctype").equals("Amulet Of Strength")) {
			madeID = 1698; chantedName = "Amulet Of Strength"; child = 53;
		} else if (args.get("ctype").equals("Amulet Of Power")) {
			madeID = 1700; chantedName = "Amulet Of Power"; child = 61;
		} else if (args.get("ctype").equals("Amulet Of Glory")) {
			madeID = 1702; chantedName = "Amulet Of Glory"; child = 76;
		} else if (args.get("ctype").equals("Bracelet Of Clay")) {
			madeID = 11072; chantedName = "Bracelet Of Clay"; child = 29;
		} else if (args.get("ctype").equals("Castlewars Bracelet")) {
			madeID = 11076; chantedName = "Castlewars Bracelet"; child = 41;
		} else if (args.get("ctype").equals("Inoculation Brace")) {
			madeID = 11085; chantedName = "Inoculation Brace"; child = 53;
		} else if (args.get("ctype").equals("Forinthry Bracelet")) {
			madeID = 11092; chantedName = "Forinthry Bracelet"; child = 61;
		} else if (args.get("ctype").equals("Combat Bracelet")) {
			madeID = 11115; chantedName = "Combat Bracelet"; child = 76;
		} else if (args.get("ctype").equals("Onyx Ring")) {
			madeID = 0; chantedName = ""; child = 88;
		}
		needToBank = false;  // See if we need items to enchant NOW
		scriptStartTime = System.currentTimeMillis(); // needed start time decloration
		startScript = true; // is this going to start or what?
		return true;
	}
	
	public void onFinish() {
	}
	
	public int loop() { // what can I say you spin me round
		if (startXP == 0) { // here we start the expierence gained in a run or the script
			startXP = skills.getCurrentXP(Skills.getStatIndex("magic")); // what xp to look at
			startLVL = skills.getCurrentLvl(Skills.getStatIndex("magic")); // what lv to look at
		}
		/*This checks if the camera is set to altitude true and sets it
		 *to true if it finds that it is not. I always play in bird eye view.
		 */
		if (!setAltitude) { // this will one time only set the camera to bird eye view
			camera.setAltitude(true);
			wait(random(250, 500));
			setAltitude = true;
			return random(50, 100);
		}

		handleEnchant();


		try { // this will look into if we need to bank right now or if there is anythign in the inventory to work with
			/*Main loop
			 *This need to bank math is from zzSleepzz :) thanks
			 */
			if (needToBank) {
				int ct=0;
				boolean banked=false;
				do  {
					banked = bank();
					wait(random(100,400));
					ct++;
				} while (ct<5 && !banked);

				if (bank.isOpen())  {
					if (inventory.getCount(cosmicID)> 0
							&& inventory.getCount(madeID)> 0)  {
						bank.close();
						needToBank = false;
					}
					return random(300,400);
				}
			}
			
			if (bank.isOpen())  {
				bank.close();
				wait(random(350,450));
			}

			return random(20,30);
		} catch(Exception e)  {
			e.printStackTrace();
		}
		return 1;
	}
	
	public boolean handleEnchant() { // this is the enchant jewlery loop don't anyone freak out when it casts stuff on stuff
		status = ("Enchanting");
		int ct = 0;
		wait(antiBan(random(200, 300)));
		magic.castSpell(child);
		ct=0;
		while (ct<100 && (!inventory.isOpen()))  {
			ct++;
			wait(antiBan(random(150, 200)));
		}
		if (!inventory.contains(cosmicID)) {
			log("You have ran out of cosmic runes!  Stopping script...");
			return false;
		}
		/*This is a failsafe, see if we need to bank right now,
		 *before continuing.
		 */
		if (needToBank = needMadeID(false))  {
			return true;
		}
		needToBank = needMadeID(true);
		if (!inventory.contains(madeID))  {
			mouse.click(true);
			return true;
		}
		ct=0;
		while (ct<10 && !inventory.clickItem(madeID, "Cast"))  {
			wait(random(100, 500));
			ct++;
		}
		ct=0;
		while (ct<75 && !magic.isOpen())  {
			ct++;
			wait(random(150, 200));
		}
		return true;
	}

	public boolean bank() {
		status = ("Banking");

		if (!bank.isOpen () && !inventory.contains(madeID)) {
			wait(antiBan(random (500, 800)));
			if (bank.open ())  {
				status = ("Bank Is Open...");
			}

			return false;
		}

		/* This section is the banking and deals with checking if the
		 *bank is not already open and that there is no items to enchant
		 *in the inventory a "if not then stop script" can be added here
		 *for checks to randoms later if need be.
		 */
		status = ("Empting Inventory");
		if (bank.depositAllExcept(cosmicID))  {;
			wait (random (700, 800) );
		}
		
		if (bank.getCount (madeID) > 0) {
			wait (random (600, 800) );
			if (inventory.getCount(madeID) == 0) {
				wait (random (600, 800) );
				status = ("Getting " + madeID
						+ "...");
				if (bank.atItem(madeID, "Withdraw-All") ) {
					wait (random (400, 500) );
					
					if (inventory.contains(madeID))  {
						bank.close ();
						return true;
					}
				}
			}
		}else{
			bank.close ();
			wait (random (10000, 12000) );
			status = ("Logging out in 10 sec");
			stopScript();
		}

		needToBank = true;
		return false;
	}

	public boolean needMadeID(boolean castActive)  { // this is the math to tell us about what we have made and what
		//that is exactly
		int cosmicIDnum = inventory.getCount(cosmicID);
		int madeIDnum = inventory.getCount(madeID);
		/*This determine if we are going to need items to enchant I added the
		 *check for cosmics here so at a later time I may be able to add a bank
		 *check for cosmics and log out based on that.
		 */
		if(castActive)  {
			if (madeIDnum < 1 || cosmicIDnum < 1)  {
				return true;
			}
		}
		return false;
	}
	public void onRepaint(final Graphics render) { // omg lets render some paint for those who have to have progress paint
		if (game.isLoggedIn()) {
			final long runTime =
					System.currentTimeMillis() - scriptStartTime; // some run time for the pretty paint
			final int seconds =
					(int) ((runTime / 1000) % 60);
			final int minutes =
					(int) ((runTime / 1000) / 60) % 60;
			final int hours =
					(int) (((runTime / 1000) / 60) / 60) % 60;
			final StringBuilder t1 =
					new StringBuilder();
			if (hours < 10) {t1.append('0');
			}t1.append(hours);
			t1.append(" : ");
			if (minutes < 10) {t1.append('0');
			}t1.append(minutes);
			t1.append(" : ");
			if (seconds < 10) {t1.append('0');
			}t1.append(seconds);
			//Ok time for some skill look ups for the paint,m and some math ot look cool with
			final int currentXP = skills.getCurrentXP(Skills.MAGIC);
			final int currentLVL = skills.getCurrentLvl(Skills.MAGIC);
			final int xpTilNext = skills.getXPToNextLvl(Skills.MAGIC);
			final int percentTilNext = skills.getPercentToNextLvl(Skills.MAGIC);
			final int fillBar = (int) (1.65 * (double) percentTilNext);
			final int XPgained = currentXP - startXP;
			final int LVLgained = currentLVL - startLVL;
			//from here on we render ot show some cool crap on the paint
			render.setColor(new Color(215, 218, 231, 100));
			render.fill3DRect(4, 192, 168, 146, true);
			render.setColor(Color.black);
			render.setFont(new Font("sansserif", Font.BOLD, 12));
			render.drawString(properties.name() + ",  v" + properties.version(), 7, 206);
			render.setColor(Color.blue);
			render.drawString(properties.name() + ",  v" + properties.version(), 8, 205);
			render.setColor(Color.black);
			render.setFont(new Font("sansserif", Font.PLAIN, 12));
			render.drawString("Time running: " + t1, 7, 223);
			render.drawString("Magic Level: " + currentLVL, 7, 241);
			render.drawString("XP Gained: " + XPgained, 7, 259);
			render.drawString("LVLs Gained: " + LVLgained, 7, 277);
			render.drawString("XP to next Level: " + xpTilNext, 7, 295);
			render.setColor(new Color(0, 0, 0, 200));
			render.fillRect(6, 313 - 11, 165, 13);
			render.setColor(new Color(129, 15, 199, 200));
			render.fillRect(6, 313 - 11, fillBar, 13);
			render.setFont(new Font("arial", Font.BOLD, 12));
			render.setColor(new Color(0, 255, 0, 200));
			render.drawString(percentTilNext + "%", 7, 313);
			render.setColor(Color.blue);
			render.drawString("Status: " + status, 7, 331);
		}
	}
	public int antiBan(int retval) {
		int gamble = random(1, random(75, 100));
		int x = random(0, 750);
		int y = random(0, 500);
		int xx = random(554, 710);
		int yy = random(230, 444);
		int screenx = random(1, 510);
		int screeny = random(1, 450);
		if(!useAntiBan)
			return retval;
		switch (gamble) {
		case 1:
			return retval;
		case 2:
			mouse.move(x, y);
			return retval;
		case 3:
			game.openTab(Game.tabInventory);
			return retval;
		case 4:
			if (player.getMine().isMoving()) {
				return retval;
			}
		case 5:
			game.openTab(Game.tabStats);
			mouse.move(random(663, 711), random(325, 348));			//663, 325 711, 348
			return retval;
		case 6:
			if (game.getCurrentTab() != Game.tabStats) {
				game.openTab(Game.tabStats);
				mouse.move(xx, yy);
				return retval;

			}
		case 7:
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
			return retval;
		case 8:
			mouse.move(screenx, screeny);
			return retval;
		case 9:
			mouse.move(screenx, screeny);
			return retval;
		case 10:
			randomTab();
			wait(random(0, 250));
			return retval;
		case 11:
			wait(random(0, 250));
			mouse.move(screenx, screeny);
			return retval;
		case 12:
			wait(random(0, 250));
			mouse.move(screenx, screeny);
			return retval;
		case 13:
			wait(random(0, 250));
			mouse.move(screenx, screeny);
			return retval;
		case 14:
			//log("moving mouse off screen...");
			wait(random(100, 1000));
			mouse.move(random(-800, 800), random(-800, 800));
			return retval;
		case 15:
			//log("moving mouse off screen...");
			wait(random(100, 1000));
			mouse.move(random(-200, 800), random(-100, 800));
			return retval;

		}
		return retval;
	}


	public int randomTab() {
		int random1 = random(1, random(23, 28));
		switch (random1) {
		case 1:
			game.openTab(Game.tabStats);
			return random(100, 500);
		case 2:
			game.openTab(Game.tabAttack);
			return random(100, 500);

		case 3:
			game.openTab(Game.tabQuests);
			return random(100, 500);

		case 4:
			game.openTab(Game.tabEquipment);
			return random(100, 500);

		case 5:
			game.openTab(Game.tabInventory);
			return random(100, 500);
		case 6:
			game.openTab(Game.tabPrayer);
			return random(100, 500);
		case 7:
			game.openTab(Game.tabMagic);
			return random(100, 500);

		case 8:
			game.openTab(Game.tabSumoming);
			return random(100, 500);

		case 9:
			game.openTab(Game.tabFriends);
			return random(100, 500);
		case 10:
			game.openTab(Game.tabIgnore);
			return random(100, 500);

		case 11:
			game.openTab(Game.tabClan);
			return random(100, 500);

		case 12:
			game.openTab(TAB_CONTROLS);
			return random(100, 500);
		case 13:
			game.openTab(Game.tabMagic);
			return random(100, 500);
		case 14:
			game.openTab(TAB_OPTIONS);
			return random(100, 500);
		case 15:
			game.openTab(Game.tabStats);
			return random(100, 500);
		case 16:
			game.openTab(Game.tabStats);
			return random(100, 500);
		case 17:
			game.openTab(Game.tabInventory);
			return random(100, 500);
		case 18:
			game.openTab(Game.tabInventory);
			return random(100, 500);
		case 19:
			game.openTab(Game.tabInventory);
			return random(100, 500);
		}
		return random(100, 300);
	}
}