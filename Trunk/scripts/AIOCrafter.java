/* * * * * * * * * * * * * * * * * * * * * * * *
 *           AIO Jewelry Crafter 2.4           *
 *                by SuperGuy411               *
 *             All Rights Reserved             *
 *                                             *
 *                                             *
 * History:                                    * 
 * January 18, 2011 - Version 1.0 Released     *
 * January 18, 2011 - Version 1.1 Released	   *
 * 		-Adds support for Al-Kharid Furnace    *
 * 		-Minor paint glitch fixed	       	   *
 * January 22, 2011 - Version 1.2 Released	   *
 * 		-Antiban added, feat AutoChatter411	   *
 * 		-Added prevention for leaving the 	   *
 * 			furnace/bank area accidently	   *
 * 			(improving script life span)	   *
 * 		-Speed increased					   *
 * January 23, 2011 - Version 1.3 Released	   *
 * 		-Numerous bug fixes that arose from	   *
 * 			the v1.2 release				   *
 * January 25, 2011 - Version 1.4 Released	   *
 * 		-Support for Neitiznot furnace		   *
 * 		-Speed increase with Al-Kharid walking *
 * 		-AutoChatter bug fix				   *
 * February 13, 2011 - Version 2.0 Released	   *
 * 		-Support for Slayer Rings and Silver   *
 * 			Jewelry							   *
 * 		-Employment of interfaces for cleaner  *
 * 			Script							   *
 * 		-General awesomeness :)				   *
 * February 14, 2011 - Version 2.1 Released	   *
 * 		-Script life status: IMMORTAL :P	   *
 * February 17, 2011 - Version 2.2 Released	   *
 * 		-GE Compatibility					   *
 * February 23, 2011 - Version 2.3 Released	   *
 * 		-Al-Kharid Walking bug fixed		   *
 * 			(UNSUCCESSFUL)   		   		   *
 * 		-Added option to disable AutoChatter   *
 * February 28, 2011 - Version 2.4 Released	   *
 * 		-Al-Kharid Walking bug fixed.		   *
 * 		-Way to catch NullPointer exception    *
 * 			in useItem() created			   *
 *                                             *
 * * * * * * * * * * * * * * * * * * * * * * * */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(
		authors = "SuperGuy411", 
        category = "Crafting", 
        name = "AIO Jewelry Crafter",
        version = 2.4, 
        description = "<html><body bgcolor=#FFF3CE><br/>" + 
        		"<table bgcolor=#DECA22 width=90% align=center><tr><td align=center>" +
        		"<font color=#FFFFFF face=Cambria size=20>" +
        		"<b>AIO Jewelry Crafter 2.4</b>" + "</font></td></tr></table><br/>" +
        		"<center><font color=#004800 face=\"Lucida Handwriting\" size=6>" +
        		"by SuperGuy411</font></center><br/>" +
                "This Script is designed to make any type of jewelry at the furnace in Edgeville, Al-Kharid, or Neitiznot" + 
                "<br/>" +
                "What you need:<br/><ul><li>Appropriate Crafting level</li><li>Appropriate mould in your inventory</li>" +
                "<li>Appropriate materials visible within your bank</li>"+
                "<li><b>Start: </b>In the bank</li></ul><h3>Options:</h3>" +
                "<b>Make:</b><br/><table><tr><td valign=middle><input type=radio name=category value=gold checked><select name=material><option>Gold</option><option>Sapphire</option><option>Emerald</option><option>Ruby</option><option>Diamond</option><option>Dragonstone</option><option>Onyx</option></select>" +
                " <select name=type><option>Ring</option><option>Necklace</option><option>Amulet</option><option>Bracelet</option></select></td></tr>" +
                "<tr><td valign=middle><input type=radio name=category value=slayerRing> <label>Slayer Ring</label></td></tr>"+
                "<tr><td valign=middle><input type=radio name=category value=silver><label> Silver</label> <select name=silverType><option>Tiara</option><option>Sickle</option><option>Bolts</option></select></td></tr></table><br/><br/>"+
                "<b>Furnace:</b><br/> <select name=furnaceName><option>Edgeville</option><option>Al-Kharid</option><option>Neitiznot</option></select><br/><br/>" +
                "<table><tr><td valign=middle><input type=radio name=makeNumber value=all checked><label>Make-All</label></td></tr><tr><td valign=middle><input type=radio name=makeNumber value=x/><label>Make-X:</label> <input type=text name=number size=10/></td></tr></table>" +
                "<b>Antiban:</b><br/><table><tr><td valign=middle><input type=checkbox name=enableAutoChat value=enable checked><label>Use Auto Chatter?</label></td></tr></table>" +
                "<center><font color=#004800 face=\"Lucida Handwriting\" size=2>" +
        		"by SuperGuy411. I really do hope you enjoy this script, as it is my first. Tell me what you think of it on the Runedev Forums! And do not try to claim this as your own.<br/>"+
        		"Thanks for using!</font></center><br/>" +
				"</body></html>" ) 
				
public class AIOCrafter extends Script implements PaintListener { 
	
	
	/* Relevant IDs
	 * Moulds:1592(R), 1597(N), 11065(B), 1595(A)
	 * Gems, sapph to onyx(cut):1607,1605,1603,1601,1615,6573
	 * Rings, gold to onyx:1635,1637,1639,1641,1643,1645,6575
	 * Necklaces, gold to onyx:1654,1656,1658,1660,1662,1664,6577
	 * Bracelets, gold to onyx:11069, 11072, 11076, 11085, 11092, 11115, 11130
	 * Amulets, gold to onyx:1673,1675,1677,1679,1681,1683,6579
	 * XCoords, gold to onyx:130,184,232,282,333,383,433
	 * ringXCoords, gold to onyx:118,166,210,256,301,346,392
	 * YCoords:107(R),170(N),229(A),289(B)
	 */ 
	
	/*************
	 * Variables *
	 *************/
	public int BARID;
	
	public long startTime = System.currentTimeMillis();
	public int startExp;
	public int expGained, productCount, totalProfit;
	public int profitPer = 0;
	public int bankFailsafe = 0;
	public int furnaceFailsafe = 0;
	public int outOfBounds = 0;
	public int antiban = 0;
	public int cameraCount = 0;
	public boolean smithIFaceOpen = false;
	public Color[] gradientColors;
	public int currCraftLevel;
	
	public int FURNACEID;
	public int locationNum;							//0 -edge, 1-al-kharid, 2 neitiznot
	boolean makeAll = false;
	public int numberToMake = 0;
	public boolean useAutoChat = true;
	
	RSTile furnaceTile; 
	RSTile[] bankToFurnace, furnaceToBank, bankTile; 
	
	RSArea bankArea;
	RSArea furnaceArea;
	
	public int MOULDID, GEMID, JEWELRYID;
	public Color gemColor;
	public int AVars, BVars;
	public String jewelryType,gemType;				//
	public int jewelryChildID, jewelryIFaceID;		//For the smithing interface
	public int withdrawNumber;						//27+ if gold jewelry, 13 if gem
	public double xpPerItem;							//for tracking the xp and number of items made
	public int levelCount = 0;
	
	public RSTile[] edgeBarrTiles = { new RSTile(3117, 3509), 
				new RSTile(3105, 3511), new RSTile(3089, 3505), //Will help in keeping character in bounds
				new RSTile(3083, 3485) };
	
	/***********
	 * Antiban *
	 ***********/
	
	public void antiban411() {		//Antiban by SuperGuy411(with limited AutoChatter)

		antiban = 0; 
		int keyNum = random(0,25);
		RSInterfaceChild craft = iface.getChild(320, 77);
		int xCoordHover = craft.getAbsoluteX() + (random(0,craft.getWidth()));
		int yCoordHover = craft.getAbsoluteY() + (random(0,craft.getHeight()));
		switch(keyNum) {
		case 0:
			camera.setRotation(camera.getAngle() + (random(-3,3)));
			break;
		case 1:
			mouse.move(7, 337, 196, 45, 45);
			wait(random(530,670));
			break;
		case 2:
			game.openTab(Game.tabStats);
			wait(random(440,650));					//checks xp till level
			mouse.move(7, xCoordHover, yCoordHover);	//Crafting	
			wait(random(1340,1720));
			game.openTab(Game.tabInventory);
			wait(random(530,670));
			break;
		case 3:
			moveMouseRandomly(20);
			wait(random(530,670));

			break;
		case 4:
			if(useAutoChat) {
				int chatNum = random(0,80);
				switch(chatNum) {					//<--SuperGuy411's AutoChatter411, still needs work, semi-basic
				case 0:
					keyboard.sendText(" /lol",
		                    true);
					break;
				case 1:
					keyboard.sendText("Smith lvls?",
		                    true);
					break;
				case 2:
					keyboard.sendText("i dont think so",
		                    true);
					wait(random(400,670));
					keyboard.sendText("wc",
		                    true);
					break;
				case 3:
					keyboard.sendText("hey evryne",
		                    true);
					wait(random(810,970));
					keyboard.sendText("evryone*",
		                    true);
					break;
				case 4:
					keyboard.sendText("  /ik",
		                    true);
					break;
				case 5:
					keyboard.sendText("no",
		                    true);
					break;
				case 6:
					keyboard.sendText("lol",
		                    true);
					break;
				case 7:
					keyboard.sendText("Craft lvls?",
		                    true);
					break;
				case 9:
					keyboard.sendText("smithing lvls?",
		                    true);
					break;
				case 8:
					keyboard.sendText("smithing levels?",
		                    true);
					break;
				case 10:
					keyboard.sendText("crft lvls?",
		                    true);
					break;
				case 11:
					keyboard.sendText("Crafting levels?",
		                    true);
					break;
				case 12:
					keyboard.sendText("Crafting lvls?",
		                    true);
					break;
				case 13:
					keyboard.sendText("Hey guys",
		                    true);
					break;
				case 14:
					keyboard.sendText("Hi ppl",
		                    true);
					break;
				case 15:
					keyboard.sendText("hi",
		                    true);
					break;
				case 16:
					keyboard.sendText("hey",
		                    true);
					break;
				case 17:
					keyboard.sendText("helllo",
		                    true);
					break;
				case 18:
					keyboard.sendText("hello everyone",
		                    true);
					break;
				case 19:
					keyboard.sendText("hi everyone",
		                    true);
					break;
				case 20:
					keyboard.sendText("sup",
		                    true);
					break;
				case 21:
					keyboard.sendText("sorry :(",
		                    true);
					wait(random(400,670));
					keyboard.sendText("wc",
		                    true);
					break;
				case 22:
					keyboard.sendText("why not? lol",
		                    true);
					wait(random(400,670));
					keyboard.sendText("wc*",
		                    true);
					break;
				case 23:
					keyboard.sendText("okay...",
		                    true);
					wait(random(400,670));
					keyboard.sendText("wrong chat",
		                    true);
					break;
				case 24:
					keyboard.sendText("Hahahaha yeah",
		                    true);
					wait(random(400,670));
					keyboard.sendText("wrong chat >.<",
		                    true);
					break;
				case 25:
					keyboard.sendText("lolz yes..",
		                    true);
					wait(random(400,670));
					keyboard.sendText("wrong chat",
		                    true);
					break;
				case 26:
					keyboard.sendText("lolz",
		                    true);
					break;
				default:
					break;
				}
			}
			break;
		case 5:
			camera.setAltitude(true); 
			break;
		case 6:
			game.showAllChatMessages();
			wait(random(300,500));
			break;
		case 7:
			game.showGameChatMessages();
			wait(random(1000,2000));
			game.showAllChatMessages();
			wait(random(300,500));
			break;
		case 8:
			wait(random(4000,8000));
			break;
		case 9:
			game.openTab(Game.tabClan);
			wait(random(1200,1670));					//checks clan chat
			game.openTab(Game.tabInventory);
			wait(random(530,670));
			break;
		case 10:
			game.openTab(Game.tabFriends);						//checks online friends
			wait(random(1340,1720));
			game.openTab(Game.tabInventory);
			wait(random(530,670));
			break;
		case 11:
			wait(random(4000,8000));		//Best type of Antiban is random waiting :P
			break;
		case 12:
			wait(random(4000,8000));
			break;
		case 13:
			wait(random(4000,8000));
			break;
		case 14:
			wait(random(4000,8000));
			break;
		default:
			break;
		}
	}

	public void respondToLevelInc(int stat) {	//Responds to level increase, extra antiban. Simply call this when a level is gained
		//We want to click continue, check what we've earned, and announce it to everyone
		//By SuperGuy411 - tell me what you think of this on the Runedev forums
		
		String[] statName = new String[2];
		int tabIfaceID = 0;
		
		switch(stat) {
		case STAT_ATTACK:
			statName[0] = "attk";
			statName[1] = "attack";
			tabIfaceID = 1;
			break;
		case STAT_STRENGTH:
			statName[0] = "str";
			statName[1] = "strength";
			tabIfaceID = 4;
			break;
		case STAT_DEFENSE:
			statName[0] = "def";
			statName[1] = "defense";
			tabIfaceID = 22;
			break;
		case STAT_RANGE:
			statName[0] = "range";
			statName[1] = "ranging";
			tabIfaceID = 45;
			break;
		case STAT_PRAYER:
			statName[0] = "pray";
			statName[1] = "prayer";
			tabIfaceID = 69;
			break;
		case STAT_MAGIC:
			statName[0] = "magic";
			statName[1] = "mage";
			tabIfaceID = 87;
			break;
		case STAT_RUNECRAFTING:
			statName[0] = "rc";
			statName[1] = "runecraft";
			tabIfaceID = 104;
			break;
		case STAT_CONSTRUCTION:
			statName[0] = "con";
			statName[1] = "construction";
			tabIfaceID = 127;
			break;
		case STAT_DUNGEONEERING:
			statName[0] = "dg";
			statName[1] = "dung";
			tabIfaceID = 151;
			break;
		case STAT_HITPOINTS:
			statName[0] = "hp";
			statName[1] = "constitution";
			tabIfaceID = 2;
			break;
		case STAT_AGILITY:
			statName[0] = "agil";
			statName[1] = "agility";
			tabIfaceID = 12;
			break;
		case STAT_HERBLORE:
			statName[0] = "herb";
			statName[1] = "herby";
			tabIfaceID = 29;
			break;
		case STAT_THIEVING:
			statName[0] = "thief";
			statName[1] = "thieve";
			tabIfaceID = 53;
			break;
		case STAT_CRAFTING:
			statName[0] = "craft";
			statName[1] = "crafting";
			tabIfaceID = 77;
			break;
		case STAT_FLETCHING:
			statName[0] = "fletch";
			statName[1] = "fletching";
			tabIfaceID = 94;
			break;
		case STAT_SLAYER:
			statName[0] = "slay";
			statName[1] = "slayer";
			tabIfaceID = 111;
			break;
		case STAT_HUNTER:
			statName[0] = "hunt";
			statName[1] = "hunter";
			tabIfaceID = 135;
			break;
		case STAT_MINING:
			statName[0] = "mine";
			statName[1] = "mining";
			tabIfaceID = 3;
			break;
		case STAT_SMITHING:
			statName[0] = "smith";
			statName[1] = "smithing";
			tabIfaceID = 20;
			break;
		case STAT_FISHING:
			statName[0] = "fish";
			statName[1] = "fishing";
			tabIfaceID = 37;
			break;
		case STAT_COOKING:
			statName[0] = "cook";
			statName[1] = "cooking";
			tabIfaceID = 61;
			break;
		case STAT_FIREMAKING:
			statName[0] = "firemaking";
			statName[1] = "fm";
			tabIfaceID = 85;
			break;
		case STAT_WOODCUTTING:
			statName[0] = "wc";
			statName[1] = "woodcutting";
			tabIfaceID = 102;
			break;
		case STAT_FARMING:
			statName[0] = "farm";
			statName[1] = "farming";
			tabIfaceID = 119;
			break;
		case STAT_SUMMONING:
			statName[0] = "summon";
			statName[1] = "summoning";
			tabIfaceID = 143;
			break;
		default:
			log("INVALID ARGUMENT FOR respondToLevelInc METHOD!");
			return;
		}
		
		wait(random(400,930));
		iface.clickContinue();
		wait(random(1200,1800));
		int chooseTxt = random(0,6);
		int statNameNum = random(0,1);
		if(useAutoChat) {
			switch(chooseTxt) {
			case 0:
				keyboard.sendText("yay! " + skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum] +"!", true);
				break;
			case 1:
				keyboard.sendText("w00t! " + skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum] , true);
				break;
			case 2:
				keyboard.sendText(skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum] +" :[)", true);
				break;
			case 3:
				keyboard.sendText("w00! " + skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum] +"! :)", true);
				break;
			case 4:
				keyboard.sendText(skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum] +"!!@@!@!! woo!", true);
				break;
			case 5:
				keyboard.sendText(":) " +skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum], true);
				break;
			case 6:
				keyboard.sendText(skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum]+"!", true);
				break;
			case 7:
				keyboard.sendText("woo! " +skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum] +"!", true);
				break;
			case 8:
				keyboard.sendText(skills.getCurrentLvl(stat) + " " + 
						statName[statNameNum] +"! finally lol", true);
				break;
			default:
				
				break;
			}
		}
		wait(random(1010,1440));
		game.openTab(Game.tabStats);						//checks new abilities
		wait(random(440,650));					
		iface.clickChild(320,tabIfaceID);  	
		wait(random(5340,6720));
		
		RSInterface levelGainedIface = iface.get(741);
		if(levelGainedIface.isValid()) {
			RSInterfaceChild child = levelGainedIface.getChild(2);
			RSInterfaceChild[] components = child.getChildren();
			for(RSInterfaceChild comp : components) {
				if(comp.containsText("<")) {
					log("Ability gained: " + comp.getText().split(">")[1].split("<")[0] + "!");
				}
			}
			
			iface.clickChild(741, 9, "Close");
		}
		wait(random(1440,1650));
		game.openTab(Game.tabInventory);
		wait(random(530,670));
	}

	
	/***********
	 * Methods *
	 ***********/
	
	public boolean inBank() {				//in the bank?
		if (bankArea.contains(player.getMyLocation())) 
			return true;
		 else 
			return false;
	}
	
	public boolean inFurnace() {			//In the furnace area?
		if (furnaceArea.contains(player.getMyLocation())) 
			return true;
		 else 
			return false;
	}
	
	public boolean inTanningRoom() {
		if((new RSArea(new RSTile(3269, 3189), new RSTile(3278, 3195))).contains(player.getMyLocation())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean atFurnaceTile() {		//Checks if at the furnace tile
		if(furnaceTile.distanceTo() == 0)
			return true;
		else
			return false;
	}
	
	public boolean atBankTile() {			//Checks if at the bank booth tile
		boolean returnVal = false;
		for(int i = 0; i< bankTile.length ; i++) {
			
			if(bankTile[i].isOnScreen())
				returnVal=  true;
		}
		return returnVal;
	}
	
	public void resetFailsafes(int exclude) {	//Resets the failsafes excluding the specified
		//Argument key:
		//0 = reset all
		//1 = exclude bank
		//2 = exclude furnace
		//3 = exclude out of bounds
		switch(exclude) {
			case 0:
				bankFailsafe = 0;
				furnaceFailsafe = 0;
				outOfBounds = 0;
				break;
			case 1:
				furnaceFailsafe = 0;
				outOfBounds = 0;
				break;
			case 2:
				bankFailsafe = 0;
				outOfBounds = 0;
				break;
			case 3:
				bankFailsafe = 0;
				furnaceFailsafe = 0;
				break;
			default:
				log("Error in method resetFailsafes(), invalid arg");
		}
	}
		
	public boolean isSmithing() {			//Checks to see if the person is at the furnace tile, and moving
		int moving;
		//log("Called method isSmithing()");
		if(atFurnaceTile()){
			moving = player.waitForAnim(random(1000,1200));		//if -1 then we're not
		} else {
			return false;
		}
		
		if(moving == 3243) {
			//log("smithing");
			return true;
		} else {
			//log("not smithing");
			return false;
		}
	}
	
	private boolean walkBankToFurnace() {	//for walking from bank to furnace
        RSTile[] randomizedPath = walk.randomizePath(bankToFurnace, 1, 1);
        return walk.pathMM(randomizedPath, 15);
        
    }

	private boolean walkFurnaceToBank() {	//for walking from furnace to bank
        RSTile[] randomizedPath = walk.randomizePath(furnaceToBank, 1, 1);
        return walk.pathMM(randomizedPath, 15);
    }
	
	public boolean readyToWalk() {
		if(player.waitToMove(300))
			return false;
		else
			return true; 
	}
	
	public int waitForAnimation(int timeMS) {
    	int pause = timeMS/5;
    	int i = 0;
    	int returnVal = -1;
    	while(i<5) {
    		wait(pause);
    		if(player.getMine().getAnimation() != -1) {
    			returnVal = player.getMine().getAnimation();
    			break;
    		}
    		i++;
    	}
    	return returnVal;
    }
	
	public void walkToNextTile(int pathNum) {
		//1 if bank to furnace
		//2 if furnace to bank
		RSTile victimTile = null;
		if(pathNum == 1) {
			for(int i = 0; i<bankToFurnace.length ;i++) {
				if(bankToFurnace[i].distanceTo() < 4) {
					//log("Attempting to walk to tile number " + (i+2));
					victimTile = bankToFurnace[(i+1)]; //new RSTile(currTile.getX(), currTile.getY());
				}
			}
			if(victimTile == null){
				victimTile = bankToFurnace[0];
			}
		} else if(pathNum == 2) {
			for(int i = 0; i<furnaceToBank.length ;i++) {
				if(furnaceToBank[i].distanceTo() < 4) {
					//log("Attempting to walk to tile number " + (i+2));
					victimTile = furnaceToBank[(i+1)];//new RSTile(currTile.getX(), currTile.getY());
				}
			}
			if(victimTile == null){
				victimTile = furnaceToBank[0];
			}
		}
		
		walk.tileMM(victimTile, 1,1);

		
	}
	
	/*public void walkToNearestBarrTile() {//this will walk to the nearest barrier tile and keep the character in bounds
		if(!inBank() && !inFurnace()) {
			if(locationNum == 0) {
				RSTile victimTile = null;
				int distance = 40;
				int finalI = 0;
				
				for(int i = 0; i<edgeBarrTiles.length ;i++) {
					if(edgeBarrTiles[i].distanceTo() < distance) {
						distance = edgeBarrTiles[i].distanceTo();
						victimTile = edgeBarrTiles[i]; 
						finalI = i;
						log("Assigning barr tile number " + (i+1));
					}
				}
				if(victimTile == null){
					log("Out of bounds in method");
					stopScript();				//all tiles are over 40 away
					return;
				}
				walkTileMiniM(victimTile, 1,1);
				log("Walking to nearest Barr tile");
				wait(7000);
				if(finalI < 2) {				//closer to furnace
					walkTileMiniM(new RSTile(3109,3503), 1,1);
					wait(7000);
				} else if (finalI == 2){
					walkTileMiniM(new RSTile(3095,3496), 1,1);//closer to bank
					wait(7000);
				} else if(finalI == 3) {
					walkTileMiniM(new RSTile(3093,3491), 1,1);//closer to bank
					wait(7000);

				}
			}
		} else {
			//We are on course
		}
	}*/
	
	public boolean useItem411(RSItem item, RSObject obj) {
		if(isSmithing())
			return true;
		RSTile tile = player.getMyLocation();
		if(!inventory.useItem(item, obj))
			return false;
		
		player.waitToMove(2000);
		while(true) {
			if(tile.distanceTo() > 4) {	//This means we haven't successfully done it... so walk back to the tile
				//log("Walking back");
				walk.tileMM(tile);
				return false;
			}
			if(player.isIdle()) {
				//log("Success");
				return true;
			}
		}
	}
	
	public void craftJewelry() {				//uses bar on furnace then clicks "make all"
		RSInterface jewelryIFace = iface.get(jewelryIFaceID);
		int count = 0;
		while(!useItem411(inventory.getItemByID(BARID),
				objects.getNearestByID(FURNACEID))) {
			count++;
			if(count > 4)
				return;		//We're most likely not in the furnace any more, so go back to the loop
			wait(random(700,1200));
		}
		
		iface.waitForOpen(jewelryIFace, 3000);
	}
	
	public void useInterface() {
		wait(random(600,1000));
		iface.clickChild(jewelryIFaceID, jewelryChildID, "Make All");
	}
	
	public boolean useBank() {				//for use after opening bank
		if(inFurnace() || !atBankTile()) {					//BUG FIX
			//log("in the furnace or bank tile not on screen, can't use bank");
			bank.close();
			return false;
		}

		if(!bank.isOpen()) {				//BUG FIX
			//log("bank isn't open, can't use it");
			bank.close();
			return false;
		}
		if(!inventory.contains(MOULDID)) {
			if((bank.getCount(MOULDID)) == 0) {			//no moulds
				log("No mould in bank.");
				stopScript();
				return false;
			}

			bank.searchItem("mould");

			bank.withdraw(MOULDID, 1);
			wait(random(500,760));
		}
		bank.depositAllExcept(MOULDID);
		
		wait(random(600,1000));
		
		if(withdrawNumber == 27)  {
			if (bank.getCount(BARID)==0)  {
				log("No bars in bank.");
				stopScript();
				return false;
			}

			bank.withdraw(BARID, 0);
		}
		else {
			if (bank.getCount(BARID)==0 || bank.getCount(GEMID)==0)  {
				log("No bars and/or gems in bank.");
				stopScript();
				return false;
			}

			bank.withdraw(GEMID, 13);
			wait(random(200,500));
			bank.withdraw(BARID, 13);
		}
		wait(random(200,500));
		return bank.close();
	}
	
	
	
	/********************
	 * Override Methods *
	 ********************/
	
	
	public void onRepaint(Graphics g) {
        if (game.isLoggedIn()) {
        	if(game.getCurrentTab() == Game.tabInventory){
	        	Graphics2D g2 = (Graphics2D) g;
	        	
	        	long millis = System.currentTimeMillis() - startTime;
	            long hours = millis / (1000 * 60 * 60);
	            millis -= hours * (1000 * 60 * 60);
	            long minutes = millis / (1000 * 60);
	            millis -= minutes * (1000 * 60);
	            long seconds = millis / 1000;
	            
	            expGained = skills.getCurrentXP(STAT_CRAFTING) - startExp;
	            if(xpPerItem > 0.0)
	            	productCount = (int)(expGained/xpPerItem);
	            totalProfit = profitPer * productCount;
	            
	            float xpsec = 0;
	            if ((minutes > 0 || hours > 0 || seconds > 0) && expGained > 0) {
	            xpsec = ((float) expGained)/(float)(seconds + (minutes*60) + (hours*60*60));
	            }
	            float xphour = xpsec * 60 *60;
	            
	            float gpsec = 0;
	            if ((minutes > 0 || hours > 0 || seconds > 0) && totalProfit != 0) {
	            gpsec = ((float) totalProfit)/(float)(seconds + (minutes*60) + (hours*60*60));
	            }
	            float gphour = gpsec * 60 * 60;
	            
	            g.setColor(Color.black);					//black outline
	            g.drawRoundRect(558, 213, 170, 246, 25, 25);
	            
	            g.setColor(new Color(224,215,57,168));		//gold fill
	            g.fillRoundRect(559, 214, 169, 245, 25, 25);
	            
	            g.setColor(Color.black);					//"AIO Jewelry Crafter"
	            g.setFont(new Font("Cambria", Font.BOLD, 18));
	            g.drawString("AIO Jewelry", 592, 235);
	            g.drawString("Crafter v2.4", 592, 255);
	            
	            g.drawRect(564, 283, 157, 4);				//<hr>
	            
	            g.setColor(new Color(0,72,0));
	            g.setFont(new Font("Lucida Handwriting", Font.BOLD, 14));
	            g.drawString("SuperGuy411", 591, 278);
	            
	            g.setColor(new Color(255,255,255,168));
	            g.fillRect(565, 284, 156, 3);				//<hr> fill
	            
	            g.setFont(new Font("Tahoma", Font.BOLD, 12));
	            g.setColor(Color.black);
	            g.drawString("Time running: " + hours + ":" + minutes + ":" + seconds + "." , 570, 307);
	            
	            g.setColor(gemColor);
	            g.drawString(gemType + " " + jewelryType, 570, 322);
	            
	            g.setColor(Color.black);
	            if(makeAll) {
		            g.drawString("Made: " + productCount, 570, 337);
	    		} else {
	    			g.drawString("Made: " + productCount + " of " + numberToMake, 570, 337);
	    		}
	            g.drawString("XP gained: " + expGained, 570, 352);
	            g.drawString("XP per hour: " + (int)xphour, 570, 367);
	            
	            if(totalProfit < 0) {
	            	g.drawString("GP loss:", 570, 382);
	            	g.drawString("Loss per hour:", 570, 397);
	            	g.setColor(new Color(155,7,17));
	            } else if(totalProfit > 0) {
	            	g.drawString("GP gain:", 570, 382);
	            	g.drawString("Gain per hour:", 570, 397);
	            	g.setColor(new Color(13,138,13));
	            } else {
	            	g.drawString("GP gain:", 570, 382);
	            	g.drawString("Gain per hour:", 570, 397);
	            }
	            
	            g.drawString(Integer.toString(totalProfit), 625, 382);
	            g.drawString(Integer.toString((int)gphour),660,397);
	            
	            g.setColor(Color.black);
	            g.drawString("% Till level " + (skills.getCurrentLvl(STAT_CRAFTING) +1)+":", 565, 418);
	            
	            //Progress bar
	            int percentTillLevel = (int)skills.getPercentToNextLvl(STAT_CRAFTING);
	            double decTillLevel = (((double)skills.getPercentToNextLvl(STAT_CRAFTING))/100);
	            double barWidth =  155 * decTillLevel;
	            g2.setColor(new Color(87,69,39));
	            g2.draw3DRect(565, 420, 155, 30, true);
	            
	            g.setColor(new Color(225,225,225,120));
	            g.fillRect(566, 421, 154, 29);
	            
	            g.setColor(new Color(13,138,13,168));
	            g.fillRect(566, 421, ((int)barWidth)-1, 29);
	            
	            g.setColor(Color.black);
	            g.drawString(percentTillLevel + "%", 568, 440);
        	}
        }
    }
	
    @Override
	public boolean onStart(Map <String, String> args) { 
        log("AIO Jewelry Crafter Started");
            	
    	if (args.get("makeNumber").equals("all")) 
    		makeAll = true;
    	
    	if(makeAll) {
    		log("Making all.");
    	} else {
    		try
    	    {
    	      numberToMake = Integer.parseInt(((String)args.get("number")).trim());
    	    }
    	    catch (NumberFormatException nfe)
    	    {
    	    	log("You must enter a valid Make-X number!");
    	    	stopScript();
    	    }

    		log("Making " + numberToMake + ".");
    	}

        
        if(((String)args.get("furnaceName")).equals("Edgeville")) {
        	//Edgeville furnace
        	FURNACEID = 26814;
        	locationNum = 0;
        	furnaceTile = new RSTile(3109,3502);
        	
        	bankTile = new RSTile[4];
        	bankTile[0] = new RSTile(3097,3496);
        	bankTile[1] = new RSTile(3094,3493);
        	bankTile[2] = new RSTile(3094,3491);
        	bankTile[3] = new RSTile(3094,3489);
        	
        	bankToFurnace = new RSTile[2];
        	bankToFurnace[0] = new RSTile(3099,3497);
        	bankToFurnace[1] = new RSTile(3109,3500);
        	
        	furnaceToBank = new RSTile[2];
        	furnaceToBank[0]= new RSTile(3108,3500);
        	furnaceToBank[1] = new RSTile(3097,3496); 

        	bankArea = new RSArea(new RSTile(3092,3489), new RSTile(3102, 3500));
        	furnaceArea = new RSArea(new RSTile(3103,3496), new RSTile(3112, 3503));
        	
        	camera.setRotation(random(40,50));
        	
        	log("Using Edgeville Furnace");
        	
        } else if(((String)args.get("furnaceName")).equals("Al-Kharid")){
        	FURNACEID = 11666;
        	locationNum = 1;
        	furnaceTile = new RSTile(3274,3186);
        	
        	bankTile = new RSTile[5];		
        	bankTile[0] = new RSTile(3269,3169);
        	bankTile[1] = new RSTile(3269,3168);
        	bankTile[2] = new RSTile(3269,3167);
        	bankTile[3] = new RSTile(3269,3166);
        	bankTile[4] = new RSTile(3269,3164);
        	
        	bankToFurnace = new RSTile[3];
        	bankToFurnace[0] = new RSTile(3270,3167);
        	bankToFurnace[1] = new RSTile(3276,3174);
        	bankToFurnace[2] = new RSTile(3275,3186);
        	
        	furnaceToBank = new RSTile[3];
        	furnaceToBank[0]= new RSTile(3276,3186);
        	furnaceToBank[1]= new RSTile(3276,3174);
        	furnaceToBank[2] = new RSTile(3269,3168);

        	bankArea = new RSArea(new RSTile(3268,3160), new RSTile(3272,3175));		//<--
        	furnaceArea = new RSArea(new RSTile(3271,3183), new RSTile(3280,3188));	//<--
        	
        	camera.setRotation(random(265,275));
        	
        	log("Using Al-Kharid Furnace");
        } else if(((String)args.get("furnaceName")).equals("Neitiznot")){
        	FURNACEID = 21303;
        	locationNum = 2;
        	furnaceTile = new RSTile(2344, 3810);
        	
        	bankTile = new RSTile[6];		
        	bankTile[0] = new RSTile(2334,3807);
        	bankTile[1] = new RSTile(2335,3807);
        	bankTile[2] = new RSTile(2336,3808);
        	bankTile[3] = new RSTile(2337,3808);
        	bankTile[4] = new RSTile(2338,3807);
        	bankTile[5] = new RSTile(2339,3807);
        	
        	bankToFurnace = new RSTile[2];
        	bankToFurnace[0] = new RSTile(2337,3807);
        	bankToFurnace[1] = new RSTile(2343,3810);
        	
        	furnaceToBank = new RSTile[2];
        	furnaceToBank[0]= new RSTile(2343,3810);
        	furnaceToBank[1]= new RSTile(2337,3807);

        	bankArea = new RSArea(new RSTile(2334,3804), new RSTile(2339,3809));		//<--
        	furnaceArea = new RSArea(new RSTile(2341,3808), new RSTile(2345,3813));	//<--
        	
        	camera.setRotation(random(310,320));
        	
        	log("Using Neitiznot  Furnace");
        }
        if(((String)args.get("category")).equals("gold")) {
        	jewelryIFaceID = 446;
        	BARID = 2357;
        	gemType = ((String) args.get("material"));
	        if(((String) args.get("material")).equals("Gold")){
	        	AVars = 1;
	        	gemColor = new Color(122,96,4);
	        	//log((String) args.get("material"));
	        } else if(((String) args.get("material")).equals("Sapphire")){
	        	AVars = 2;
	        	gemColor = new Color(0,0,128);
	        	//log((String) args.get("material"));
	        } else if(((String) args.get("material")).equals("Emerald")){
	        	AVars = 3;
	        	gemColor = new Color(0,72,0);
	        	//log((String) args.get("material"));
	        } else if(((String) args.get("material")).equals("Ruby")){
	        	AVars = 4;
	        	gemColor = new Color(128,0,0);
	        	//log((String) args.get("material"));
	        } else if(((String) args.get("material")).equals("Diamond")){
	        	AVars = 5;
	        	gemColor = Color.white;
	        	//log((String) args.get("material"));
	        } else if(((String) args.get("material")).equals("Dragonstone")){
	        	AVars = 6;
	        	gemColor = new Color(128,0,128);
	        	//log((String) args.get("material"));
	        } else if(((String) args.get("material")).equals("Onyx")){
	        	AVars = 7;
	        	gemColor = Color.black;
	        	//log((String) args.get("material"));
	        } else {
	        	log("Error in setting AVars");
	        	stopScript();
	        }
	        
	        if(((String) args.get("type")).equals("Ring")){
	        	BVars = 1;
	        	//log((String) args.get("type"));
	        } else if(((String) args.get("type")).equals("Necklace")){
	        	BVars = 2;
	        	//log((String) args.get("type"));
	        } else if(((String) args.get("type")).equals("Amulet")){
	        	BVars = 3;
	        	//log((String) args.get("type"));
	        } else if(((String) args.get("type")).equals("Bracelet")){
	        	BVars = 4;
	        	//log((String) args.get("type"));
	        } else {
	        	log("Error in setting BVars");
	        	stopScript();
	        }
	        	
	        switch(BVars){
	        case 1:	//rings
	        	jewelryType = "Rings";
	        	MOULDID = 1592;
	        	switch(AVars) {
	        	case 1:	//gold
	        		GEMID = -1;
	        		JEWELRYID = 1635;
	        		jewelryChildID = 82;
	        		withdrawNumber = 27;
	        		xpPerItem = 15;
	        		break;
	        	case 2:	//sapphire
	        		GEMID = 1607;
	        		JEWELRYID = 1637;
	        		jewelryChildID = 84;
	        		withdrawNumber = 13;
	        		xpPerItem = 40;
	        		break;
	        	case 3:	//emerald
	        		GEMID = 1605;
	        		JEWELRYID = 1639;
	        		jewelryChildID = 86;
	        		withdrawNumber = 13;
	        		xpPerItem = 55;
	        		break;
	        	case 4:	//ruby
	        		GEMID = 1603;
	        		JEWELRYID = 1641;
	        		jewelryChildID = 88;
	        		withdrawNumber = 13;
	        		xpPerItem = 70;
	        		break;
	        	case 5:	//diamond
	        		GEMID = 1601;
	        		JEWELRYID = 1643;
	        		jewelryChildID = 90;
	        		withdrawNumber = 13;
	        		xpPerItem = 85;
	        		break;
	        	case 6:	//dragonstone
	        		GEMID = 1615;
	        		JEWELRYID = 1645;
	        		jewelryChildID = 92;
	        		withdrawNumber = 13;
	        		xpPerItem = 100;
	        		break;
	        	case 7:	//onyx
	        		GEMID = 6573;
	        		JEWELRYID = 6575;
	        		jewelryChildID = 94;
	        		withdrawNumber = 13;
	        		xpPerItem = 115;
	        		break;
	        	default:
	        		log("error");
	        		break;
	        	}
	        	break;
	  
	        case 2:	//Necklaces
	        	jewelryType = "Necklaces";
	        	MOULDID = 1597;
	        	switch(AVars) {
	        	case 1:	//gold
	        		GEMID = -1;
	        		JEWELRYID = 1654; 
	        		jewelryChildID = 68;
	        		withdrawNumber = 27;
	        		xpPerItem = 20; 
	        		break;
	        	case 2:	//sapphire
	        		GEMID = 1607;
	        		JEWELRYID = 1656;
	        		jewelryChildID = 70;
	        		withdrawNumber = 13;
	        		xpPerItem = 55;
	        		break;
	        	case 3:	//emerald
	        		GEMID = 1605;
	        		JEWELRYID = 1658;
	        		jewelryChildID = 72;
	        		withdrawNumber = 13;
	        		xpPerItem = 60;
	        		break;
	        	case 4:	//ruby
	        		GEMID = 1603;
	        		JEWELRYID = 1660;
	        		jewelryChildID = 74;
	        		withdrawNumber = 13;
	        		xpPerItem = 75;
	        		break;
	        	case 5:	//diamond
	        		GEMID = 1601;
	        		JEWELRYID = 1662;
	        		jewelryChildID = 76;
	        		withdrawNumber = 13;
	        		xpPerItem = 90;
	        		break;
	        	case 6:	//dragonstone
	        		GEMID = 1615;
	        		JEWELRYID = 1664;
	        		jewelryChildID = 78;
	        		withdrawNumber = 13;
	        		xpPerItem = 105;
	        		break;
	        	case 7:	//onyx
	        		GEMID = 6573;
	        		JEWELRYID = 6577;
	        		jewelryChildID = 80;
	        		withdrawNumber = 13;
	        		xpPerItem = 120;
	        		break;
	        	default:
	        		log("error");
	        		break;
	        	}
	        	break;
	        	
	        case 3:	//Amulet
	        	jewelryType = "Amulets";
	        	MOULDID = 1595;
	        	switch(AVars) {
	        	case 1:	//gold
	        		GEMID = -1;
	        		JEWELRYID = 1673; 
	        		jewelryChildID = 53;
	        		withdrawNumber = 27;
	        		xpPerItem = 30; 
	        		break;
	        	case 2:	//sapphire
	        		GEMID = 1607;
	        		JEWELRYID = 1675;
	        		jewelryChildID = 55;
	        		withdrawNumber = 13;
	        		xpPerItem = 65;
	        		break;
	        	case 3:	//emerald
	        		GEMID = 1605;
	        		JEWELRYID = 1677;
	        		jewelryChildID = 57;
	        		withdrawNumber = 13;
	        		xpPerItem = 70;
	        		break;
	        	case 4:	//ruby
	        		GEMID = 1603;
	        		JEWELRYID = 1679;
	        		jewelryChildID = 59;
	        		withdrawNumber = 13;
	        		xpPerItem = 85;
	        		break;
	        	case 5:	//diamond
	        		GEMID = 1601;
	        		JEWELRYID = 1681;
	        		jewelryChildID = 61;
	        		withdrawNumber = 13;
	        		xpPerItem = 100;
	        		break;
	        	case 6:	//dragonstone
	        		GEMID = 1615;
	        		JEWELRYID = 1683;
	        		jewelryChildID = 63;
	        		withdrawNumber = 13;
	        		xpPerItem = 150;
	        		break;
	        	case 7:	//onyx
	        		GEMID = 6573;
	        		JEWELRYID = 6579;
	        		jewelryChildID = 65;
	        		withdrawNumber = 13;
	        		xpPerItem = 165;
	        		break;
	        	default:
	        		log("error");
	        		break;
	        	}
	        	break;
	        	
	        case 4:	//Bracelet
	        	jewelryType = "Bracelets";
	        	MOULDID = 11065;
	        	switch(AVars) {
	        	case 1:	//gold
	        		GEMID = -1;
	        		JEWELRYID = 11069; 
	        		jewelryChildID = 33;
	        		withdrawNumber = 27;
	        		xpPerItem = 25; 
	        		break;
	        	case 2:	//sapphire
	        		GEMID = 1607;
	        		JEWELRYID = 11072;
	        		jewelryChildID = 35;
	        		withdrawNumber = 13;
	        		xpPerItem = 60;
	        		break;
	        	case 3:	//emerald
	        		GEMID = 1605;
	        		JEWELRYID = 11076;
	        		jewelryChildID = 37;
	        		withdrawNumber = 13;
	        		xpPerItem = 65;
	        		break;
	        	case 4:	//ruby
	        		GEMID = 1603;
	        		JEWELRYID = 11085;
	        		jewelryChildID = 39;
	        		withdrawNumber = 13;
	        		xpPerItem = 80;
	        		break;
	        	case 5:	//diamond
	        		GEMID = 1601;
	        		JEWELRYID = 11092;
	        		jewelryChildID = 41;
	        		withdrawNumber = 13;
	        		xpPerItem = 95;
	        		break;
	        	case 6:	//dragonstone
	        		GEMID = 1615;
	        		JEWELRYID = 11115;
	        		jewelryChildID = 43;
	        		withdrawNumber = 13;
	        		xpPerItem = 110;
	        		break;
	        	case 7:	//onyx
	        		GEMID = 6573;
	        		JEWELRYID = 11130;
	        		jewelryChildID = 45;
	        		withdrawNumber = 13;
	        		xpPerItem = 125;
	        		break;
	        	default:
	        		log("error");
	        		break;
	        	}
	        	break;
	        }
	        
        } else if(((String)args.get("category")).equals("slayerRing")) {
        	gemColor = new Color(170,141,224);
        	BARID = 2357;
        	jewelryIFaceID = 446;
        	gemType = "Slayer";
        	jewelryType = "Rings";
        	GEMID = 4155;
    		JEWELRYID = 13281; 
    		jewelryChildID = 97;
    		withdrawNumber = 13;
    		xpPerItem = 15; 
        	MOULDID = 1592;
        } else if(((String)args.get("category")).equals("silver")) {
        	gemColor = new Color(180,178,178);
        	BARID = 2355;
        	jewelryIFaceID = 438;
        	gemType = "Silver";
        	GEMID = -1;
    		withdrawNumber = 27;

        	if(((String)args.get("silverType")).equals("Tiara")) {
        		xpPerItem = 52.5; 
            	MOULDID = 5523;
        		JEWELRYID = 5525; 
            	jewelryType = "Tiaras";
        		jewelryChildID = 44;
        	} else if(((String)args.get("silverType")).equals("Bolts")) {
        		xpPerItem = 50; 
            	MOULDID = 9434;
        		JEWELRYID = 9382; 
            	jewelryType = "Bolts";
        		jewelryChildID = 66;
        	} else if(((String)args.get("silverType")).equals("Sickle")) {
        		xpPerItem = 50; 
            	MOULDID = 2976;
        		JEWELRYID = 2961; 
            	jewelryType = "Sickles";
        		jewelryChildID = 30;
        	}
        }
        
        log("Making " + gemType + " " + jewelryType);
        if(args.get("enableAutoChat") == null) {
        	log("AutoChat Disabled");
        	useAutoChat = false;
        } else {
        	log("AutoChat Enabled");
        	useAutoChat = true;
        }

        /*if(!inventoryContains(MOULDID)){
        	log("You must have the appropriate mould in your inventory!");
        	stopScript();
        }*/
        
        camera.setAltitude(68.0);
        startTime = System.currentTimeMillis();
        startExp = skills.getCurrentXP(STAT_CRAFTING);
        
        gradientColors = new Color[2];
        gradientColors[0] = new Color(134,239,101);
        gradientColors[1] = new Color(30,96,10);
        
        profitPer = (ge.loadItemInfo(JEWELRYID).getPrice())
        				- (ge.loadItemInfo(BARID).getPrice());
        if(withdrawNumber != 27)
        	profitPer = profitPer - (ge.loadItemInfo(GEMID).getPrice());
         
        currCraftLevel = skills.getCurrentLvl(STAT_CRAFTING);
        
		return true;
	}
    
	@Override   
	public void onFinish() {
		log("Finished AIO Jewelry Crafter");
		if(totalProfit < 0) {
			log("You have made " + productCount + " " + jewelryType +
					", gained " + expGained + " XP, and lost " + (-1*totalProfit)+ " gp.");
		} else {
			log("You have made " + productCount + " " + jewelryType +
					", gained " + expGained + " XP, and gained " + totalProfit + " gp.");
		}
		log("You have also gained "+levelCount+" levels.");		
	}

	@Override   
	public int loop() {
		
		if(!makeAll) {
			if(productCount >= numberToMake) {
				log("Successfully made all " + numberToMake + " " + jewelryType +"!");
				stopScript();
			}
		}
		
		cameraCount++;							//rotates the camera back every 4-6 loops
		if(cameraCount > random(8,11)) {
			cameraCount = 0;
			if(locationNum == 0) {
				camera.setRotation(random(40,50));
				camera.setAltitude(random(80.0,100.0));
			} else if(locationNum == 1) {
				camera.setRotation(random(265,275));
				camera.setAltitude(random(80.0,100.0));
			} else if(locationNum == 2) {
				camera.setAltitude(true); 
				camera.setRotation(random(310,320));
			}
		}
		
		//Check for failsafes!
		
		if(outOfBounds > 29){
			log("Error in script, out of bounds for too long");
			stopScript();
		}
		if(bankFailsafe > 4) {
			resetFailsafes(0);
			walkBankToFurnace();
			//return random(500,1000);
		}
		if(furnaceFailsafe > 14) {
			resetFailsafes(0);
			walkFurnaceToBank();
			//return random(500,1000);
		}
		
		
		/*if(locationNum == 0) {
			if(!(inBank()) && !(inFurnace())) {
				walkToNearestBarrTile();
				return(random(500,1000));
			} 
		}*/
			
		if(skills.getCurrentLvl(STAT_CRAFTING) > currCraftLevel) {			//we have gained a level!
			log("Gained a Crafting level!");
			currCraftLevel = skills.getCurrentLvl(STAT_CRAFTING);
			levelCount++;
			respondToLevelInc(STAT_CRAFTING);
		}
		
		antiban++;
		if(antiban > random(18,23))
			antiban411();
		
		if (!player.isIdle()){       //doing something
			//log("Active");
			resetFailsafes(3);
			if(locationNum == 1) {
				//log("using loc 1");
				if((bankToFurnace[1]).distanceTo() < 4) {		//if we're near the middle tile
					//log("We are near the middle tile");
					if(inventory.contains(BARID)) {
						walkToNextTile(1);
					} else {
						walkToNextTile(2);
					}
				}
			}
			return random(1500,1700);		//Then do nothing
		} else if (bank.isOpen()){          //bank open
			if(inFurnace()){				//BUG FIX
				//log("banking error caught, detected at furnace");
				bank.close();
				return random(1000,1500);
			}
			if(!atBankTile()) {				//BUG FIX
				//log("Not on the bank tile, can't use bank");
				bank.close();
				return random(1500,2000);
			}
			//log("Bank open");
			resetFailsafes(3);
			useBank();
			return random(1000,1500);		//Then do nothing
		} else if (iface.get(jewelryIFaceID).isValid()){ //if about to smelt
			//log("Smith interface open");
			resetFailsafes(3);
			useInterface();
			return random(1000,1500);		//Then do nothing
		} /*else if (isSmithing()){           //if smelting
			//log("smelting currently");
			resetFailsafes(3);
			return random(1000,1500);		//Then do nothing
		}*/ else {							//If we're not moving
			
			if(locationNum == 1) {
				//log("using loc 1");
				if((bankToFurnace[1]).distanceTo() < 5) {		//if we're near the middle tile
					//log("We are near the middle tile");
					if(inventory.contains(BARID)) {
						walkToNextTile(1);
					} else {
						walkToNextTile(2);
					}
				}
			}
			
			if (inBank()) {
				resetFailsafes(1);
				bankFailsafe++;
				if(!inventory.contains(MOULDID)){
					bank.open();
					return random(500,1000);
				}
				//log("Bank Failsafe at count " + Integer.toString(bankFailsafe));
				if(withdrawNumber == 27) {
					if(inventory.contains(JEWELRYID) || !(inventory.contains(BARID))) {
						bank.open();
						return random(500,1000);
					} else {
						if(readyToWalk())
							walkToNextTile(1);
					}
				} else {
					if(inventory.contains(JEWELRYID) || !(inventory.contains(BARID))
							|| !(inventory.contains(GEMID))) {
						bank.open();
						return random(500,1000);
					} else {
						if(readyToWalk())
							walkToNextTile(1);
					}
				}
			} else if(inFurnace()) {
				if (isSmithing()){           //if smelting
					//log("smelting currently");
					resetFailsafes(3);
					return random(1000,1500);		//Then do nothing
				} else {
					resetFailsafes(2);
					furnaceFailsafe++;
					if(!inventory.contains(MOULDID)) {
						if(readyToWalk())
							walkToNextTile(2);
					}
					//log("Furnace Failsafe at count " + Integer.toString(furnaceFailsafe));
					if(withdrawNumber == 27){
						if(inventory.contains(BARID)){		//Need to smelt
							craftJewelry();
							return random(450,750);
						} else {
							if(readyToWalk())
								walkToNextTile(2);
						}
					} else {
						if(inventory.contains(BARID) && inventory.contains(GEMID)){		//Need to smelt
							craftJewelry();
							return random(450,750);
						} else {
							if(readyToWalk())
								walkToNextTile(2);
						}
					}
				}
			} else if(inTanningRoom()){
				walk.tileMM(furnaceTile,1,1);
				return random(6000,9000);
			} else {
				//Not in either
				resetFailsafes(3);
				outOfBounds++;						//If out of bounds for too long, script stops. FAILSAFE
				//log("OOB Failsafe at count " + Integer.toString(outOfBounds));
			}
		}
		return random(1000,1500);
	}

}