import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Waterwolf" }, category = "Magic", name = "Wolf's multi stunner", version = 1.1, description = "Options are automatically detected, just run it and make sure you have at least 80 magic."
 + "<html>"
 + "Scroll magic book to the bottom<br><br>"
 + "Use stun alch or only stun<br>"
 + "<select name=\"magetype\">"
 + "<option>Only stun</option>"
 + "<option selected>Stun and alch</option>"
 + "</select><br><br>"
 + "Id of the monster you want cast stun to (zamorak mage by default)"
 + "<br>"
 + "<input type=\"text\" size=\"5\" name=\"MonsterID\" value=\"189\">"
 + "<br><br>"
 + "Target level<br>"
 + "<input type=\"text\" size=\"2\" name=\"TargLVL\" maxlength=\"2\" value=\"99\">"
 + "<br><br>"
 + "How many casts do you want to do?<br>"
 + "<input type=\"radio\" name=\"castsOption\" value=\"number\"><input type=\"text\" size=\"5\" name=\"Casts\" value=\"1000\"> - <input type=\"radio\" name=\"castsOption\" value=\"runout\" checked>Until runes run out<br><br>"
 + "Stop when 1000 xp left to<br>"
 + "<input type=\"radio\" name=\"stopWhen\" value=\"target\"> target level - "
 + "<input type=\"radio\" name=\"stopWhen\" value=\"next\"> next level - "
 + "<input type=\"radio\" name=\"stopWhen\" value=\"dont\" checked> don't stop<br>"
 + "Use this if you want to level by yourself (so you can screenshot or something)")

public class WolfsMultiStunner extends Script implements PaintListener, MouseMotionListener, MouseListener { 
	
	public long startTime = 0, runTime = 0, seconds = 0 ,minutes = 0, hours = 0;
	
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	
	public int targetStun = 189;
	
	public int targetLevel = 99;
	
	public int origTargetLevel = 99;
	
	public boolean alching = false;
	
	public int changeSlower = 0;
	
	
	/**
	 * Do we stun + alch or only stun
	 */
	public boolean stunAndAlch = false;
	
	/**
	 * The amount of casts the player wishes to do, updated onStart:76
	 */
	public int castAmount;
	
	public int failSafe = 0;
	
	public int alchFailSafe = 0;
	
	public int countFailSafe = 0;
	
    private static int[] xpTable = {0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431};
	 
	public int expToTarget = 0; 
	
	/**
	 * Updated after each cast
	 */
	public int casts; 
	
	public long fastestCastsPerHour = 0;
	
	public int stopWhen = 0;
	
	public RSTile safeSpot;
	
	/**
	 * Defined at startup in order to get the original level + xp
	 */
	public int startLevel, startXP;
	 
	
	/**
	 * Grabs the users magic experience.
	 */
	public int magicXP = skills.getCurrentXP(STAT_MAGIC);
	
	public boolean showPaint = false;
	
	public int paintTab = 0;
	
	public String currentlyDoing = "-";
	
	
	/**
	 * Updated after every cast, grabs the amount of experience
	 * gained so far by the script.
	 */
	public int gainedXP;
	
	/**
	 * Will return false if the user does not have the requirements to cast the spell.
	 */
	public boolean hasReqs = true;
	
	/**
	 * This will set a target for the mouse to move to
	 */
	public int javaMouseX, javaMouseY, jMX, jMY;
	
	
	@Override
	public boolean onStart(Map<String, String> args) {
		if (getMagicLevel() < 80)
			hasReqs = false;
		if(!game.isLoggedIn())
			game.login();
		
		Bot.getEventManager().addListener(this, EventMulticaster.MOUSE_EVENT);
		Bot.getEventManager().addListener(this, EventMulticaster.MOUSE_MOTION_EVENT);
		
		sendStart();
		
		if (args.get("magetype").equals("Only stun")) {
			stunAndAlch = false;
		}
		else {
			stunAndAlch = true;
		}
		
		if (args.get("stopWhen").equals("dont")) {
			stopWhen = 0;
		}
		else if (args.get("stopWhen").equals("next")) {
			stopWhen = 1;
		}
		else if (args.get("stopWhen").equals("target")) {
			stopWhen = 2;
		}
		
		
		if (args.get("castsOption").equals("number")) {
			try {
				castAmount = Integer.parseInt(args.get("Casts"));
			} catch (Exception e) {
				e.printStackTrace();
				log("Please enter a number in the casts textbox >.<");
				if (game.isLoggedIn())
					game.logout();
					
				stopScript();
			}
		}
		else {
			castAmount = inventory.getCount(566);
		}
		
		if (inventory.contains(561) && stunAndAlch == false) {
			log("You have nature runes in inventory but you are still only stunning. If you want to alch and stun, please stop script and run it again with Stun and alch- option selected.");
		}
		

		try {
			targetStun = Integer.parseInt(args.get("MonsterID"));
		} catch (Exception e) {
			e.printStackTrace();
			log("Please enter a number in the target to stun textbox >.<");
			if (game.isLoggedIn())
				game.logout();
				
			stopScript();
		}
		try {
			targetLevel = Integer.parseInt(args.get("TargLVL"));
			origTargetLevel = Integer.parseInt(args.get("TargLVL"));
		} catch (Exception e) {
			e.printStackTrace();
			log("Please enter a number in the target level textbox >.<");
			if (game.isLoggedIn())
				game.logout();
				
			stopScript();
		}
			
		startLevel = getMagicLevel();
		startXP = skills.getCurrentXP(STAT_MAGIC);
		startTime = System.currentTimeMillis();
		
		
		if (targetLevel <= startLevel) {
			log("Your target level is too low."); 
			if (startLevel != 99) {
				targetLevel = startLevel + 1;
				origTargetLevel = startLevel + 1;
			}
			else {
				log("You already have 99 magic, you don't need target level");
				targetLevel = 99;
				origTargetLevel = 99;
			}
		}
        
        
        safeSpot = player.getMyLocation();
		return true;
		
	}
	
	
	/**
	 * Detects how much experience each spell gives.
	 */
	public int castXP() {
		if (stunAndAlch == true) {
			return 155;
		}
		return 90;
	}
	
	/**
	 * Declaring a getter for all the above variables.
	 */
	 
	public int getMagicLevel() {
		return skills.getCurrentLvl(STAT_MAGIC);
	}
	
	public int getCastXP() {
		return castXP();
	}
	
	public int getGainedXP() {
		return gainedXP;
	}
	
	public int getMagicXP() {
		return magicXP;
	}
	
	public int getStartXP() {
		return startXP;
	}
	
	public int getStartLevel() {
		return startLevel;
	}
	
	public int getGainedExperience() {
		return skills.getCurrentXP(STAT_MAGIC)-getStartXP();
	}
	
	public int getGainedLevels() {
		return skills.getCurrentLvl(STAT_MAGIC)-getStartLevel();
	}
	
	@Override
	public int getMouseSpeed() {
		return 9;
	}
	

	 
	public void castSpell() {
		final RSTile iAmNow = player.getMyLocation();
		if (safeSpot.getX() + 5 < iAmNow.getX()
				|| safeSpot.getX() - 5 > iAmNow.getX()
				|| safeSpot.getY() + 5 < iAmNow.getY()
				|| safeSpot.getY() - 5 > iAmNow.getY()) {

			log("Not in safe spot. Maybe in random event? Trying to walk back");

			if (walk.to(safeSpot)) {
				log("Back in safespot, continuing magic");
			} else {
				log("Walking back failed! Stopping script");
				stopScript(true);
			}
		}
		try {
			if (casts >= castAmount)
				endScript("All casts done");

			if (skills.getXPToNextLvl(STAT_MAGIC) <= 1000 && stopWhen == 1
					&& getMagicLevel() != 99) {
				log(skills.getXPToNextLvl(STAT_MAGIC) + " left to "
						+ getMagicLevel() + " magic. Stopping script.");
				stopScript();
			}
			if (expToTarget <= 1000 && stopWhen == 2 && getMagicLevel() != 99) {
				log(skills.getXPToNextLvl(STAT_MAGIC) + " left to "
						+ getMagicLevel() + " magic. Stopping script.");
				stopScript();
			}

			/*if (alching == false) {
				while (game.getCurrentTab() != Game.tabMagic) {
					game.openTab(Game.tabMagic);
					wait(random(50, 300));
				}
			}
			alching = false;*/
			atSpellInterface();
			wait(random(70, 170));
			getTileAndCast();
			wait(random(100, 160));
			if (stunAndAlch == true) {
				wait(random(15, 50));
				highAlchStuff();
				wait(random(440, 570));
			} else {
				wait(random(750, 900));
			}
			gainedXP += getCastXP();
			antiBan();
			casts++;
		} catch (Exception e) {
			log("Exception caught.");
		}
	}
	
	boolean hasSetToCombatOnly = false;
	
	public boolean atSpellInterface() {
		while (game.getCurrentTab() != Game.tabMagic) {
			game.openTab(Game.tabMagic);
		}
		if (!hasSetToCombatOnly) {
			RSInterfaceChild comp = iface.getChild(192, 94).getChildren()[1];
			mouse.move(comp.getPoint());
			mouse.drag(random(comp.getAbsoluteX(), comp.getAbsoluteX()+comp.getWidth()), random(430, 490));
			//atInterface(192, 16);
			hasSetToCombatOnly = true;
		}
		
		
		atInterfaceSpell(82);
		
		//mouse.move((int) random(661, 678), (int) random(351, 369));
		//wait(random(50, 150));
		//mouse.click(true);
		return true;
	}
	
	public void getTileAndCast() {
		while (game.getCurrentTab() != Game.tabMagic) {
			game.openTab(Game.tabMagic);
			//atSpellInterface();
		}
		RSNPC stunTarget = npc.getNearestByID(targetStun);
		if (failSafe > 4) {
			log("Script couldn't find stun target. Stopping script");
			stopScript();
		}
		if (!npc.action(stunTarget, "Cast")) {
			log("Couldn't find stun target or something is wrong");
			failSafe++;
		}
		else {
			failSafe = 0;
		}
		wait(random(100, 300));
		
	}
	
	public boolean highAlchStuff() {
		while (game.getCurrentTab() != Game.tabMagic) {
			game.openTab(Game.tabMagic);
			wait(random(50,300));
		}
		//mouse.move((int) random(691, 703), (int) random(257, 273));
		//wait(random(50, 100));
		//mouse.click(true);
		
		//atInterfaceSpell(59);
		mouse.move(random(566, 585), random(291, 300));
		wait(random(30, 60));
		mouse.click(true);
		
		alching = true;
		
		int[] inventoryArray = inventory.getArray();
		if (inventoryArray[8] > 0) {
			alchFailSafe = 0;
		}
		else {
			alchFailSafe++;
		}
		
		if (!inventory.contains(566)) {
			countFailSafe++;
		} else {
			countFailSafe = 0;
		}

		if (!inventory.contains(561)) {
			log("Out of nature runes. Moving to only stun");
			stunAndAlch = false;
		}
		if (!inventory.contains(554)) {
			log("Out of fire runes. Moving to only stun");
			stunAndAlch = false;
		}
		
		wait(random(80, 110));
		mouse.click(true);
		wait(random(300, 410));
		return true;
	}
	
	public void atInterfaceSpell(int spell) {
		RSInterfaceChild child = iface.getChild(192,spell);
		int y = child.getAbsoluteY()-83;
		int x = child.getAbsoluteX();
		mouse.move(random(x, x+child.getWidth()), random(y, y+child.getHeight()));
		wait(40);
		mouse.click(true);
	}
	
	public void sendStart() {
		if (getMagicLevel() > 79)
			log("Script started, your current magic level is: " +getMagicLevel()+ "!");
		else {
			log("You need a magic level of higher than " +getMagicLevel()+ " in order to");
			log("Use this script, try getting level 80 magic and then running it.");
			
		}
	}
	
	public void sendFinish() {
		log("Script ended, your current magic level is: " +getMagicLevel()+ "!");
	}
	
	public void endScript(String message) {
		log(message+ ", stopping script.");
		stopScript();
	}
	 
	public void antiBan() {
		int randomNum = random(1, 11);
		int commenceAntiban = random(1,20);
		if (12 > commenceAntiban && commenceAntiban > 7) {
			switch (randomNum) {
			case 3:
				javaMouseX = random(250, 750);
				javaMouseY = random(222, 500);
				mouse.move(0, 0, javaMouseX, javaMouseY);
			break;
			case 8:
			case 9:
				javaMouseX = random(0, 762);
				javaMouseY = random(0, 502);
				mouse.move(javaMouseX, javaMouseY);
			break;
			}
		}
	}
	 

	
	@Override
	public int loop() {
		if (countFailSafe > 4) {
			endScript("Out of soul runes");
		}

		if (hasReqs == false)
			return -1;

		castSpell();
		
		return random(100, 200);
	}
	
	@Override
	public void onFinish() {
	
		sendFinish();
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
		final Color backGround = new Color(0, 0, 0, 175);
		final Color lineColor = new Color(255, 255, 255, 125);
		
		
		int X = 617;
		int Y = 183;
		long xpPerHour = 0;
		long castPerHour = 0;
		
		long timeToLevel = 0;
		
		long timeToTarget = 0;
		expToTarget = 0;
		long castToTarget = 0;
		
		int castToLevel = skills.getXPToNextLvl(STAT_MAGIC) / castXP();
		
		
		runTime = System.currentTimeMillis() - startTime;
		
		int castsLeft = castAmount - casts;
		

		
		if (getGainedExperience() != 0) {
			xpPerHour = (int) ((3600000.0 / (double) runTime) * getGainedExperience());
			timeToLevel = (long) (skills.getXPToNextLvl(STAT_MAGIC)/(((float) getGainedExperience())/((int) runTime/1000))) * 1000;
			castPerHour = (int) ((3600000.0 / (double) runTime) * casts);
		}
		if (castPerHour > fastestCastsPerHour) {
			fastestCastsPerHour = castPerHour;
		}
		
		if (targetLevel > getMagicLevel()) {
			expToTarget = xpTable[targetLevel] - skills.getCurrentXP(STAT_MAGIC);
			timeToTarget = (long) (expToTarget/(((float) getGainedExperience())/((int) runTime/1000))) * 1000;
			castToTarget = expToTarget / castXP();
		}
		
		while (jMX != javaMouseX || jMY != javaMouseY) {
			if (jMX != javaMouseX) {
				if (jMX > javaMouseX) {
					for (int i = 0; i > jMX-javaMouseX; jMX--) {
						g.fill3DRect(jMX, 0, 1, 505, true);
					}
				}
				else if (jMX < javaMouseX) {
					for (int i = 0; i > jMX-javaMouseX; jMX++) {
						g.fill3DRect(jMX, 0, 1, 505, true);
					}
				}
			}
			if (jMY != javaMouseY) {
				if (jMY > javaMouseY) {
					for (int i = 0; i > jMY-javaMouseY; jMY--) {
						g.fill3DRect(0, jMY, 766, 1, true);
					}
				}
				else if (jMY < javaMouseY) {
					for (int i = 0; i > jMY-javaMouseY; jMY++) {
						g.fill3DRect(0, jMY, 766, 1, true);
					}
				}
			}
			jMX = javaMouseX;
			jMY = javaMouseY;
			g.setColor(Color.WHITE);
		}
		
		g.setColor(backGround);
		g.fill3DRect(523, 170, 238, 31, true);
		g.setColor(lineColor);
		g.drawLine(523, 201, 761, 201);
		g.setColor(Color.WHITE);
		g.drawString("Show paint.", X, Y);
		
		String whatDoing = "Stunning";
		if (stunAndAlch) {
			whatDoing = "Stunning and alching";
		}
		
		currentlyDoing = whatDoing;
		if (showPaint == true) {
			g.setColor(backGround);
			g.fill3DRect(523, 202, 237, 263, true);
			g.setColor(Color.WHITE);
			X = 550;
			Y = 223;
			
			if (paintTab == 0) {
				// Main
				g.drawString("Runtime: " + cTime(runTime), X, Y);
				Y += 15;
				g.drawString("Version: " + Double.toString(properties.version()), X, Y);
				Y += 15;
				g.drawString("Fastest casting speed: " + fastestCastsPerHour + "p/h", X, Y);
				Y += 15;
				g.drawString(whatDoing, X, Y);
				Y += 15;
			}
			else if (paintTab == 1) {
				// Info
				g.drawString("Current Magic Level: " +getMagicLevel(), X, Y);
				Y += 15;
				g.drawString("Levels Gained: " +getGainedLevels(), X, Y);
				Y += 15;
				g.drawString("Current Magic XP: " +skills.getCurrentXP(STAT_MAGIC), X, Y);
				Y += 15;
				g.drawString("Experience gained: " +getGainedExperience(), X, Y);
				Y += 15;
				g.drawString("Experience Per Hour: " +xpPerHour, X, Y);
				Y += 15;
				g.drawString("XP until next level: " + skills.getXPToNextLvl(STAT_MAGIC) + " (" + castToLevel + " casts)", X, Y);
				Y += 15;
				g.drawString(skills.getPercentToNextLvl(STAT_MAGIC)+ "% of the way to next level!", X, Y);
				Y += 20;
				g.drawString("Spells cast: " +casts, X, Y);
				Y += 15;
				g.drawString("Casts left: " + castsLeft, X, Y);
				Y += 15;
				g.drawString("Casts per hour: " + castPerHour, X, Y);
				Y += 15;
				g.drawString("Time to level: " + cTime(timeToLevel), X, Y);
			}
			else if (paintTab == 2) {
				// Target
				g.drawString("Target level: " + targetLevel, X, Y);
				Y += 15;
				g.drawString("Time to target level: " + cTime(timeToTarget), X, Y);
				Y += 15;
				g.drawString("Exp to target level: " + expToTarget, X, Y);
				Y += 15;
				g.drawString("Casts to target level: " + castToTarget, X, Y);
				Y += 30;
				g.drawString("Change target temporarily: ", X, Y);
				g.setColor(Color.WHITE);
				g.drawString("+1", 562, 319);
				g.drawString("-1", 625, 319);
				g.drawString("Orig.", 683, 319);
				g.setColor(backGround);
				g.fill3DRect(547, 300, 63, 33, true);
				g.fill3DRect(610, 300, 63, 33, true);
				g.fill3DRect(673, 300, 63, 33, true);


				
				/*if (changeSlower % 10 == 0 && changeSlower != 0) {
					if (changeSlower < 0) {
						targetLevel--;
					}
					else if (changeSlower > 0) {
						targetLevel++;
					}
					changeSlower = 0;
				}*/
			}
			g.setColor(Color.WHITE);
			g.drawString("Main", 562, 452);
			g.drawString("Stats", 625, 452);
			g.drawString("Target", 683, 452);
			g.setColor(lineColor);
			g.drawLine(609, 433, 609, 466);
			g.drawLine(673, 433, 673, 466);
			g.setColor(backGround);
			g.fill3DRect(547, 433, 63, 33, true);
			g.fill3DRect(610, 433, 63, 33, true);
			g.fill3DRect(673, 433, 63, 33, true);
		}
		g.setColor(backGround);
		g.fill3DRect(523, 466, 238, 37, true);
		g.fill3DRect(4, 318, 512, 20, true);
		X = 617;
		Y = 484;
		g.setColor(lineColor);
		g.drawLine(523, 466, 761, 466);
		g.setColor(Color.WHITE);
		g.drawString("Hide paint.", X, Y);
		X = 10;
		Y = 332;
		g.drawString("Waterwolf's Multi Stunner", X, Y);
		X += 155;
		g.drawString(currentlyDoing, X, Y);

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		Point mouse = arg0.getPoint();
		if (mouse.x >= 547 && mouse.x <= 609 && mouse.y >= 433 && mouse.y <= 469) {
			paintTab = 0;
		}
		if (mouse.x >= 610 && mouse.x <= 672 && mouse.y >= 433 && mouse.y <= 469) {
			paintTab = 1;
		}
		if (mouse.x >= 673 && mouse.x <= 736 && mouse.y >= 433 && mouse.y <= 469) {
			paintTab = 2;
		}
		
		if (mouse.x >= 523 && mouse.x <= 760 && mouse.y >= 169 && mouse.y <= 201) {
			showPaint = true;
		}
		if (mouse.x >= 523 && mouse.x <= 760 && mouse.y >= 466 && mouse.y <= 497) {
			showPaint = false;
		}
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		
		if (paintTab == 2) {
			Point mouse = arg0.getPoint();
			if (mouse.x >= 547 && mouse.x <= 609 && mouse.y >= 300 && mouse.y <= 333) {
				if (targetLevel < 99) { 
					targetLevel--;
				}
			}
			if (mouse.x >= 610 && mouse.x <= 672 && mouse.y >= 300 && mouse.y <= 333) {
				if (targetLevel > getMagicLevel() && getMagicLevel() < 99) {
					targetLevel++;
				}
			}
			if (mouse.x >= 673 && mouse.x <= 736 && mouse.y >= 300 && mouse.y <= 333) {
				targetLevel = origTargetLevel;
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/*public void serverMessageRecieved(final ServerMessageEvent e) {
		String message = e.getMessage();
		if (message.contains("You've received")) {
		    if ((getInterface(241).getChild(4).containsText("You have received"))) {
	            atInterface(241, 5);
	            wait(250 + random(500, 500));
	        }
		}
	}
	*/
	
	/**
	 * End of class
	 */

}