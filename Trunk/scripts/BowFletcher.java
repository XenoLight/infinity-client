import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;

@ScriptManifest(
		authors = {"Kaka"}, 
		name = "Kaka's Bow Fletcher", 
		category = "Fletching", 
		version = 1.5, 
		website = "http://www.lazygamerz.org/forums/index.php?topic=3666.0",
		description = ""

+ "<html>"
+ "<h3><center><font color=green>Bow Fletcher</h3><body>"

+ "<center><b>Bow and Log Type To Fletch: <br>" 
+ "<select name=logType>"
+ "<option>Select"
+ "<option>Shortbow"
+ "<option>Longbow"
+ "<option>Oak Shortbow"
+ "<option>Oak Longbow"
+ "<option>Willow Shortbow"
+ "<option>Willow Longbow"
+ "<option>Maple Shortbow"
+ "<option>Maple Longbow"
+ "<option>Yew Shortbow"
+ "<option>Yew Longbow"
+ "<option>Magic Shortbow"
+ "<option>Magic Longbow<br>"
+ ""
+ "<center><b>Choose Antiban: <br>" 
+ "<select name=AntibanType>"
+ "<option>Select"
+ "<option>Normal"
+ "<option>AFK"
+ "<option>None"
+ "<h5><b><center>Heavily modified by Kaka</b></h5>"
+ "<h5><b><center>Credits to Elite19 for initial motivation.</b></h5>"
+ "<h5><b><center>Credits to Secret Spy for his Paint</b></h5>"
+ "<h5><b><center>Credits to Bobbybighoof for his Antiban</b></h5>"
+ "<h5><b><center>A lazygamerz.org exclusive</b></h5>"
+ "</html>"
		)

public class BowFletcher extends Script implements MessageListener, PaintListener{


	//Variables	
	public boolean useAntiBan = true;
	public boolean AFK = false;

	public double version = 1.4;

	int logID;
	int bowID;
	int mouseSpeed = random(6, 9);
	private int knife = 946;
	private int animID = 1248;
	private int[] stockID = {9440, 9442, 9444, 9446, 9448, 9450, 9452};

	RSInterfaceChild overall;
	RSInterfaceChild xxx = iface.getChild(916,20);
	private RSInterfaceChild collect = iface.getChild(109, 14);
	private RSInterfaceChild talkTo = iface.getChild(241, 5);
	private RSInterfaceChild talkTo2 = iface.getChild(243, 7);
	private RSInterfaceChild talkTo3 = iface.getChild(232, 3);

	//Paint Variables
	long runTime = 0;
	long seconds = 0;
	long minutes = 0;
	long hours = 0;

	public long minutesToLevel = 0, hoursToLevel = 0, secToLevel = 0, expToLevel = 0;
	public float gainedExp = 0,  secExp = 0, minuteExp = 0, hourExp = 0;
	public long startTime = System.currentTimeMillis();

	public int FletchedAdded = 0,  startExp, startLevel, Fletchingstartexp, FletchedID;

	protected int getMouseSpeed(){
		return mouseSpeed;  
	}    

	public boolean onStart(Map<String, String> args) {

		if(args.get("logType").equals("Shortbow")) {
			overall = iface.getChild(905,15);
			logID = 1511; 
			bowID = 50;
		}

		if(args.get("logType").equals("Longbow")) {
			overall = iface.getChild(905,16);
			logID = 1511; 
			bowID = 48;
		}

		if(args.get("logType").equals("Oak Shortbow")) {
			overall = iface.getChild(905,14);
			logID = 1521; 
			bowID = 54;
		}

		if(args.get("logType").equals("Oak Longbow")) {
			logID = 1521; 
			bowID = 56;
			overall = iface.getChild(905,15);
		}

		if(args.get("logType").equals("Willow Shortbow")) {
			overall = iface.getChild(905,14);
			logID = 1519; 
			bowID = 60;
		}

		if(args.get("logType").equals("Willow Longbow")) {
			logID = 1519; 
			bowID = 58;
			overall = iface.getChild(905,15);
		}

		if(args.get("logType").equals("Maple Shortbow")) {
			overall = iface.getChild(905,14);
			logID = 1517; 
			bowID = 64;
		}

		if(args.get("logType").equals("Maple Longbow")) {	
			logID = 1517; 
			bowID = 62;
			overall = iface.getChild(905,15);
		}

		if(args.get("logType").equals("Yew Shortbow")) {
			overall = iface.getChild(905,14);
			logID = 1515; 
			bowID = 68;
		}

		if(args.get("logType").equals ("Yew Longbow")) {
			logID = 1515; 
			bowID = 66;
			overall = iface.getChild(905,15);
		}

		if(args.get("logType").equals("Magic Shortbow")) {
			overall = iface.getChild(905,14);
			logID = 1513; 
			bowID = 72;
		}

		if(args.get("logType").equals( "Magic Longbow")) {	
			logID = 1513;
			bowID = 70;
			overall = iface.getChild(905,15);
		}

		if (args.get("AntibanType").equals("Normal")) {
			useAntiBan = true;
			AFK = false;
			log("Antiban enabled.");
		}
		if (args.get("AntibanType").equals("AFK")){
			useAntiBan = false;
			AFK = true;
			log("AFKing Enabled");
		}
		if (args.get("AntibanType").equals("None")) {
			useAntiBan = false;
			AFK = false;
			log("Antiban disabled.");
		}
		return true;
	}

	public void moveMouseOffScreen() {
		if(AFK = true){	
			switch (random(0, 4)) {
			case 0: // up
				mouse.move(random(-10, 766 + 10), random(-100, -10));
				break;
			case 1: // down
				mouse.move(random(-10, 766 + 10), 505 + random(10, 100));
				break;
			case 2: // left
				mouse.move(random(-100, -10), random(-10, 505 + 10));
				break;
			case 3: // right
				mouse.move(random(10, 100) + 766, random(-10, 505 + 10));
				break;
			}
		}
	}

	public void Wait(){
		switch (random(0, random(245, 260))){
		case 0:
			log("Waiting(AFK)");
			wait(random(230402,273323));
		}
	}

	public int loop() {
		if (collect.getParInterface().isValid()) {
			wait(random(150, 200));
			mouse.move(collect.getPosition());
			wait(random(70, 100));
			mouse.click(true);
		}
		if(!inventory.contains(knife)){
			stopScript();
			game.logout();
		}

		if(!inventory.contains(logID) && bank.isOpen()){		
			if(inventory.getCount() == 1){	
				getMouseSpeed();
				bank.atItem(logID, "Withdraw-All");
				wait(random(1100, 1300));
				if(inventory.getCount(logID) == 27) {
					bank.close();
				}
			}
		}

		if(!inventory.contains(logID) && !bank.isOpen()){
			wait(random(2564, 5438));
			bank.open();
			wait(random(500, 600));
			//Check collect screen		     
			if (collect.getParInterface().isValid()) {
				wait(random(150, 200));
				mouse.move(collect.getPosition());
				wait(random(70, 100));
				mouse.click(true);
			}
			//Check talkTo screen
			if(talkTo.isValid()){
				iface.clickChild(talkTo);
				mouse.click(true);
				wait(random(200, 300));
				iface.clickChild(talkTo2);
				mouse.click(true);
				wait(random(200, 300));
				iface.clickChild(talkTo3);
				mouse.click(true);
				wait(random(200, 300));
			}
			if(bank.isOpen()) {
				wait(random(100, 200));
				if(inventory.contains(bowID)) {
					getMouseSpeed();
					bank.depositAllExcept(knife); }
				wait(random(400, 500)); 
				if (bank.getCount(logID) == 0) {
					stopScript();
					game.logout();
				}
				if(inventory.getCount() == 1){
					getMouseSpeed();
					bank.atItem(logID, "Withdraw-All");
					wait(random(1100, 1300));
					if(inventory.getCount(logID) == 27) {
						bank.close();
					}
				}
			}
		}
		if(inventory.contains(logID) && inventory.getCount(bowID) < 27) {
			//if already fletching wait
			if(player.animationIs(animID)) {
				if(inventory.contains(stockID) && inventory.getCount(bowID)== 0 && inventory.contains(logID)){
					inventory.clickItem(knife, "Use");
					getMouseSpeed();
					wait(random(342, 496));
					inventory.clickItem(logID, "Use");

					wait(random(942, 978));

					if(xxx.isValid()){
						getMouseSpeed();
						iface.clickChild(overall,"Make All");
						wait(random(1700, 2031));
						mouse.moveOffScreen();
						wait(random(2300, 2800));
						Wait();
						if (useAntiBan = true) {
							wait(antiBan(random(300, 500)));
						}
						if(player.animationIs(animID)){
							if (useAntiBan = true){
								wait(antiBan(random(200, 300)));
								return 120;
							}
						}
					}
				}
				return 100; }
			if(bank.isOpen() == true) {
				bank.close();
				return 10;
			}
			if(inventory.contains(logID) && !bank.getInterface().isValid() && inventory.getCount(bowID) == 0 && player.isIdle() ); {
				wait(random(100, 200));
				inventory.clickItem(knife, "Use");
				getMouseSpeed();
				wait(random(342, 496));
				inventory.clickItem(logID, "Use");

				wait(random(942, 978));

				if(xxx.isValid()){
					getMouseSpeed();
					iface.clickChild(overall,"Make All");
					wait(random(1700, 2031));
					mouse.moveOffScreen();
					wait(random(1200, 1300));
					if (useAntiBan = true){
						wait(antiBan(random(200, 300)));
					}
					Wait();
				}
				return 120;

			}
		} else {


			if(inventory.contains(bowID) && !inventory.contains(logID)){
				wait(random(2564, 5438));
				bank.open();
				wait(random(500, 600));
				//Check collect screen		     
				if (collect.getParInterface().isValid()) {
					wait(random(150, 200));
					mouse.move(collect.getPosition());
					wait(random(70, 100));
					mouse.click(true);
				}
				//Check talkTo screen
				if(talkTo.isValid()){
					iface.clickChild(talkTo);
					mouse.click(true);
					wait(random(200, 300));
					iface.clickChild(talkTo2);
					mouse.click(true);
					wait(random(200, 300));
					iface.clickChild(talkTo3);
					mouse.click(true);
					wait(random(200, 300));
				}
				if(bank.isOpen()) {
					wait(random(100, 200));
					if(inventory.contains(bowID)) {
						getMouseSpeed();
						bank.depositAllExcept(knife); }
					wait(random(400, 500)); 
					if (bank.getCount(logID) == 0) {
						stopScript();
						game.logout();
					}
					if(inventory.getCount() == 1){
						getMouseSpeed();
						bank.atItem(logID, "Withdraw-All");
						wait(random(1100, 1300));
						if(inventory.getCount(logID) == 27) {
							bank.close();
						}
					}
				}

			}
			return 120;	
		}
	}	

	@Override
	public void onRepaint(Graphics g) {
		if (game.isLoggedIn()) {
			long millis = System.currentTimeMillis() - startTime;
			long hour = millis / (1000 * 60 * 60);
			millis -= hour * (1000 * 60 * 60);
			long minute = millis / (1000 * 60);
			millis -= minute * (1000 * 60);
			long second = millis / 1000;

			final Color BG = new Color(0, 0, 0, 75);
			final Color RED = new Color(255, 0, 0, 255);
			final Color GREEN = new Color(0, 255, 0, 255);
			final Color BLACK = new Color(0, 0, 0, 255);

			if (Fletchingstartexp == 0) {
				Fletchingstartexp = skills.getCurrentXP(STAT_FLETCHING);
			}

			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}

			if (startExp == 0) {
				startExp = skills.getCurrentXP(Constants.STAT_FLETCHING);
			}

			if (startLevel == 0) {
				startLevel = skills.getCurrentLvl(Constants.STAT_FLETCHING);
			}

			runTime = System.currentTimeMillis() - startTime;
			second = runTime / 1000;
			if (second >= 60) {
				minute = second / 60;
				second -= minute * 60;
			}
			if (minute >= 60) {
				hour = minute / 60;
				minute -= hour * 60;
			}

			gainedExp = skills.getCurrentXP(Constants.STAT_FLETCHING) - startExp;
			expToLevel = skills.getXPToNextLvl(Constants.STAT_FLETCHING);

			if ((minute > 0 || hour > 0 || second > 0) && gainedExp > 0) {
				secExp = (float) gainedExp
						/ (float) (second + minute * 60 + hour * 60 * 60);
			}
			minuteExp = secExp * 60;
			hourExp = minuteExp * 60;

			if (secExp > 0) {
				secToLevel = (int) (expToLevel / secExp);
			}
			if (secToLevel >= 60) {
				minutesToLevel = secToLevel / 60;
				secToLevel -= minutesToLevel * 60;
			} else {
				minutesToLevel = 0;
			}
			if (minutesToLevel >= 60) {
				hoursToLevel = minutesToLevel / 60;
				minutesToLevel -= hoursToLevel * 60;
			} else {
				hoursToLevel = 0;
			}

			g.setColor(BG);
			g.fill3DRect(4, 200, 129, 137, true);
			g.setColor(Color.white);
			g.setFont(new Font("Tahoma", Font.BOLD, 10));
			g.drawString("Kaka's Bow Fletcher " + version , 8, 211);
			g.drawLine(8, 213, 129, 213);
			g.setFont(new Font("Tahoma", Font.PLAIN, 10));
			g.setColor(Color.yellow);
			g.drawString("Running for: " + hour + ":" + minute + ":" + second, 8, 226);
					g.drawString("Bows Fletched: " + FletchedAdded, 8, 241);
					g.drawString("Exp Gained: " + gainedExp + " (" + (skills.getCurrentLvl(Constants.STAT_FLETCHING) - startLevel) + ")", 8, 256);
					g.drawString("Exp per hour: " + (int) hourExp, 8, 271);
					g.drawString("Exp to level: " + expToLevel, 8, 286);
					g.drawString("Time to level: " + hoursToLevel + ":" + minutesToLevel + ":" + secToLevel, 8, 301);
					g.drawString("Progress to next level:", 8, 316);
					g.setColor(RED);
					g.fill3DRect(8, 322, 100, 11, true);
					g.setColor(GREEN);
					g.fill3DRect(8, 322, skills.getPercentToNextLvl(Constants.STAT_FLETCHING), 11, true);
					g.setFont(new Font("Tahoma", Font.BOLD, 11));
					g.setColor(BLACK);
					g.drawString(skills.getPercentToNextLvl(Constants.STAT_FLETCHING) + "%  to " + (skills.getCurrentLvl(Constants.STAT_FLETCHING) + 1), 31, 331);

					//DRAW MOUSE
					final Mouse m = Bot.getClient().getMouse();
					final Point loc = mouse.getLocation();
					if (mouse == null) {
						return;
					}

					final int mouse_x = m.getX();
					final int mouse_y = m.getY();
					final int mouse_press_x = m.getPressX();
					final int mouse_press_y = m.getPressY();
					final long mouse_press_time = mouse.getPressTime();

					g.setColor(Color.yellow);
					g.drawLine(mouse_x - 8, mouse_y - 8, mouse_x + 8, mouse_y + 8);
					g.drawLine(mouse_x + 8, mouse_y - 8, mouse_x - 8, mouse_y + 8);
					if (System.currentTimeMillis() - mouse_press_time < 1000) {
						g.setColor(Color.green);
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

	public int antiBan(int retval) {
		int gamble = random(1, random(75, 100));
		int x = random(0, 750);
		int y = random(0, 500);
		int xx = random(554, 710);
		int yy = random(230, 444);
		int screenx = random(1, 510);
		int screeny = random(1, 450);
		if(useAntiBan = true){
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
				mouse.move(random(617, 666), random(356, 371));
				wait(random(1134 ,2367));
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
		case 9:
			game.openTab(Game.tabFriends);
			return random(100, 500);
		case 15:
			game.openTab(Game.tabStats);
			return random(100, 500);
		case 16:
			game.openTab(Game.tabStats);
			return random(100, 500);
		case 17:
		case 18:
		case 19:
			game.openTab(Game.tabInventory);
			return random(100, 500);
		}
		return random(100, 300);
	}

	@Override
	public void messageReceived(MessageEvent e) {
		final String serverString = e.getMessage();
		if (serverString.contains("You've just advanced")) {
			iface.clickContinue();
		}else 
			if (serverString.contains("You carefully")) {
				FletchedAdded++;
			}
	}
}