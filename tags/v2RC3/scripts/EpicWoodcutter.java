import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.lazygamerz.scripting.api.Game;
import org.rsbot.bot.Bot;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@org.rsbot.script.ScriptManifest(
	authors = { "Epic_" }, 
	category = "Woodcutting", 
	name = "EpicWoodcutter", 
	version = 3.5, 
	description = 
		"<html><head></head><body bgcolor='#222222'><center>" +
		"<table><tr><td width='350' valign='top'><center>" +
		"	<div style='font-family:Calibri, Arial;color:#FF3300; font-size:28px'>EpicWoodcutter</div>" +
		"<div style='font-family:Calibri, Arial;color:#FFFFFF; font-size:12px'>v3.5 by Epic_ <br /> </div>" +
		"<div style='font-family:Calibri, Arial;color:#FF3300; font-size:12px'>Quick Select</div>" +
		"<div><table bordercolor='#FF3300' border='1' cellpadding='0' cellspacing='0' ><tr><td align='center' style='font-family:Calibri, Arial;color:#FFFFFF; font-size:10px'><b>Tree Locations</b> <br /><select name='trees'><option>Trees 1<option>Trees 2<option>Trees 3</select> <select name='oaks'><option>Oaks 1</select> <select name='willows'><option>Willows 1<option>Willows 2<option>Willows 3</select> <br /><strong>Hatchet Retrieval</strong> <br />[Mithril<input type='checkbox' name='getMithrilAxeArg' value='true' checked='checked'>][Adamant<input type='checkbox' name='getAdamantAxeArg' value='true' checked='checked'>][Rune<input type='checkbox' name='getRuneAxeArg' value='true' checked='checked'>] <br /><strong>Yews</strong> <br /><select name='cutYewsArg'><option>Yews from 65<option>Yews from 60<option>Yews from 70<option>never</select></td></tr></table></div><div style='font-family:Calibri, Arial;color:#FF3300; font-size:12px'>Instructions</div><div style='font-family:Calibri, Arial;color:#FFFFFF; font-size:11px' align='justify'>This script is a woodcutter that trains around Rimmington. It has a built in deathwalk and will find its way to the trees wherever you start it. It is intended for training your woodcutting from 1 to yews as fast as possible, and trains in the most efficient way to get there. Locations are ranked in order best to worst, so only use other locations if the first ones are full. <br /> <br /> It will retrieve the selected axes from the bank when you reach the required level to use them. Note that it is best to start with the axe in the inventory, not wielded. The detauls of what trees the script will cut can be found below. It is important to not that while it powercuts normal, oak and willow trees, it does bank yew trees for profit. <ul><li> 1-20 - Normal Trees </li><li> 20-35 - Oak Trees </li><li> 35-Selected - Willow Trees </li><li> Selected onwards - Yew Trees </li></ul>If you have any further questions, do not hesitate to post them in the official thread (the link can be found at the top) and I will get on to you as soon as possible. Also, feedback, progress reports and new ideas are extremely welcome!</div></center> </td></tr></table></center></body></html>")
public class EpicWoodcutter extends Script implements PaintListener {

    ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);

/*   Change Log
 *   3.4 Added new tree IDs for the region south of Falador/Rimmington.
 */
	
	
   // VARIABLES //
   public int startLevel;
   public int startEXP;
   public long EXPGained;
   public int levelsGained;
   public long EXPPerHour;
   public String status = "Starting up";
   public String logType = "logs";
   public boolean getRuneAxe = false;
   public boolean getAdamantAxe = false;
   public boolean getMithrilAxe = false;
   public boolean cutYews = false;
   public int cutYewsAt = 100;
   public int YEWLOGID = 1515;
   public int yewPrice = 0;

   public int[] axeTypes = { 1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739 };
   public int runeAxe = 1359;
   public int adamantAxe = 1357;
   public int mithrilAxe = 1355;
   public long startTime = 0;

   public int[] NORMALTREES = { 5004, 5005, 5045, 3879, 3881, 3882, 3883,
   3885, 3886, 3887, 3888, 3889, 3890, 3891, 3892, 3893, 3928, 3967,
   3968, 4048, 4049, 4050, 4051, 4052, 4053, 4054, 3033, 3034, 3035,
   3036, 2409, 2447, 2448, 1330, 1331, 1332, 1310, 1305, 1304, 1303,
   1301, 1276, 1277, 1278, 1279, 1280, 8742, 8743, 8973, 8974, 1315,
   1316, 38760, 38782, 38783, 38784, 38785, 38786, 38787, 38788};
   public int[] OAKTREES = { 1281, 3037, 8462, 8463, 8464, 8465, 8466, 8467, 38731, 38732 };
   public int[] WILLOWTREES = { 139, 142, 1308, 2372, 5551, 5552, 5553, 8481, 8482, 8483,
   8484, 8485, 8486, 8487, 8488, 38616, 38627, 38730};
   public int[] YEWTREES = { 1309, 8503, 8504, 8505, 8506, 8507, 8508, 8509,
   8510, 8511, 8512, 8513, 38755 };

   public int[] currentTrees;
   public int bankBooth = 11758;

   // RSTiles
   public RSTile normalTreesLocation = new RSTile(2996, 3228); // new
   // RSTile(2990,3206);
   // new
   // RSTile(2995,3252);
   public RSTile oakTreesLocation = new RSTile(2984, 3205);
   public RSTile willowTreesLocation = new RSTile(2989, 3187); // new
   // RSTile(2970,3195);
   // new
   // RSTile(2997,3167);
   public RSTile yewTreesLocation = new RSTile(2938, 3232);

   public RSTile halfWayWalkPoint = new RSTile(3070, 3276);
   public RSTile faladorEastBank = new RSTile(3012, 3355);
   public RSTile lumbridge = new RSTile(3222, 3218);

   public RSTile[] bankToYews = new RSTile[] { new RSTile(3012, 3355),
   new RSTile(3005, 3318), new RSTile(2968, 3284),
   new RSTile(2958, 3241), new RSTile(2938, 3232) };
   public RSTile[] yewsToBank = walk.reversePath(bankToYews);

   private final Color backgroundTop = new Color(255,255,255,30),
   backgroundBottom = new Color(52, 42, 31, 200),
   title = new Color(252, 180, 72, 200),
   black = new Color(0, 0, 0, 160),
   white = new Color(255,255,255,160),
   green = new Color(30,255,30,200);

   public boolean atWelcomButton() {
      RSInterface welcomeInterface = iface.get(378);
      if (welcomeInterface.getChild(45).getAbsoluteX() > 20
         || (!welcomeInterface.getChild(117).getText().equals(
                                                 "10.1120.190") && !welcomeInterface.getChild(117)
            .getText().equals(""))) {
         status = "Welcome Screen";
         mouse.click(random(215, 555), random(420, 440), true);
         return true;
      } else {
         return false;
      }
   }

   public boolean onStart(Map<String, String> args) {

      if (args.get("getRuneAxeArg") != null) {
         if (args.get("getRuneAxeArg").equals("true")) {
            getRuneAxe = true;
         }
      }
      if (args.get("getAdamantAxeArg") != null) {
         if (args.get("getAdamantAxeArg").equals("true")) {
            getAdamantAxe = true;
         }
      }
      if (args.get("getMithrilAxeArg") != null) {
         if (args.get("getMithrilAxeArg").equals("true")) {
            getMithrilAxe = true;
         }
      }

      if (args.get("cutYewsArg").equals("Yews from 60")) {
         cutYewsAt = 60;
         cutYews = true;
      }
      if (args.get("cutYewsArg").equals("Yews from 65")) {
         cutYewsAt = 65;
         cutYews = true;
      }
      if (args.get("cutYewsArg").equals("Yews from 70")) {
         cutYewsAt = 70;
         cutYews = true;
      }
      if (args.get("cutYewsArg").equals("never")) {
         cutYewsAt = 100;
         cutYews = false;
      }

      if (cutYews == true) {
         //final GEItemInfo yew = grandExchange.loadItemInfo(YEWLOGID);
         yewPrice = ge.loadItemInfo(YEWLOGID).getPrice();
      }

      if (args.get("trees").equals("Trees 1"))
         normalTreesLocation = new RSTile(2996, 3228);
      if (args.get("trees").equals("Trees 2"))
         normalTreesLocation = new RSTile(2995, 3252);
      if (args.get("trees").equals("Trees 3"))
         normalTreesLocation = new RSTile(2990, 3206);

      if (args.get("oaks").equals("Oaks 1"))
         oakTreesLocation = new RSTile(2984, 3205);

      if (args.get("willows").equals("Willows 1"))
         willowTreesLocation = new RSTile(2989, 3187);
      if (args.get("willows").equals("Willows 2"))
         willowTreesLocation = new RSTile(2998, 3166);
      if (args.get("willows").equals("Willows 3"))
         willowTreesLocation = new RSTile(2970, 3195);
      	return true;
   		}

   public void onRepaint(Graphics g) {
      if (game.isLoggedIn()) {
         long millis = System.currentTimeMillis() - startTime;
         long hours = millis / (1000 * 60 * 60);
         millis -= hours * (1000 * 60 * 60);
         long minutes = millis / (1000 * 60);
         millis -= minutes * (1000 * 60);
         long seconds = millis / 1000;

         /*
         g.setColor(new Color(0, 0, 0, 100));
         g.fillRoundRect(10, 209, 110, 123, 10, 10);
         */

         long totalSeconds = ((System.currentTimeMillis() - startTime) / 1000);

         EXPGained = skills.getCurrentXP(8) - startEXP;
         levelsGained = skills.getCurrentLvl(8) - startLevel;
         if (totalSeconds == 0) {
            EXPPerHour = 0;
         } else {
            EXPPerHour = (EXPGained * 3600) / totalSeconds;
         }

         long secondsToLvl;

         if (EXPPerHour == 0) {
            secondsToLvl = 0;
         } else {
            secondsToLvl = (skills.getXPToNextLvl(8) * 3600) / EXPPerHour;
         }
         ;
         long hoursToLvl = secondsToLvl / (60 * 60);
         secondsToLvl -= hoursToLvl * (60 * 60);
         long minutesToLvl = secondsToLvl / (60);
         secondsToLvl -= minutesToLvl * (60);

         g.setColor(backgroundBottom);
         g.fillRect(0,319,519,20);

         g.setColor(new Color(255, 30, 30, 200));
         g.fillRoundRect(15, 324, 100, 10, 5, 10);
         g.setColor(new Color(30, 255, 30, 200));
         g.fillRoundRect(15, 324, skills.getPercentToNextLvl(8), 10, 5, 10);
         g.setColor(new Color(200,200,200,200));
         g.drawRoundRect(15, 324, 100, 10, 5, 10);

         g.setFont(new Font("Calibri", Font.BOLD, 14));

         g.setColor(white);
         drawStringWithShadow(""+skills.getPercentToNextLvl(8)+"%",125,333,g);


         g.drawRect(517,319,1,19);
         g.drawRect(417,319,1,19);
         g.drawRect(317,319,1,19);
         g.drawRect(217,319,1,19);


         g.setFont(new Font("Calibri", Font.BOLD, 16));
         g.setColor(title);
         drawStringWithShadow("Status", 245, 334, g);
         drawStringWithShadow("Experience", 332, 334, g);
         drawStringWithShadow("Profit", 450, 334, g);

         g.setColor(backgroundTop);
         g.fillRect(0,319,519,10);

         int y=220;
         int x;
         int spacing = 15;

         g.setFont(new Font("Calibri", Font.BOLD, 12));

            Mouse m = Bot.getClient().getMouse();
         Point p = new Point(m.getX(),m.getY());


         if (isWithinBounds(p,217,319,100,20)) {
            g.setColor(backgroundBottom);
            g.fillRect(217,235,102,84);
            x = 222;
            y=235;
            g.setColor(green);
            drawStringWithShadow(""+status,x, y+= spacing, g);
            g.setColor(title);
            drawStringWithShadow("Runtime: "+hours+":"+minutes+":"+seconds, x, y+= spacing, g);
            drawStringWithShadow("Version: "+props.version(), x, y+= spacing, g);


         }
         if (isWithinBounds(p,317,319,100,20)) {
            g.setColor(backgroundBottom);
            g.fillRect(317,235,102,84);
            x = 322;
            y = 235;
            g.setColor(title);
            drawStringWithShadow("Current Level: "+skills.getCurrentLvl(8),x, y+= spacing, g);
            drawStringWithShadow("Levels Gained: "+levelsGained,x, y+= spacing, g);
            drawStringWithShadow("EXP Gain: "+EXPGained,x, y+= spacing, g);
            drawStringWithShadow("EXP/Hour: "+EXPPerHour, x, y+= spacing, g);
            drawStringWithShadow("Level in: "+hoursToLvl+":"+minutesToLvl+":"+secondsToLvl, x, y+= spacing, g);
         }
         if (isWithinBounds(p,417,319,100,20)) {
            g.setColor(backgroundBottom);
            g.fillRect(417,235,102,84);
            x = 422;
            y = 235;
            g.setColor(title);
            if (logType == "yews") {
               drawStringWithShadow("Log Type: Yews",x, y+= spacing, g);
               drawStringWithShadow("Log Price: " + yewPrice + "gp",x, y+= spacing, g);
               drawStringWithShadow("Logs Banked: " + EXPGained / 175,x, y+= spacing, g);
               drawStringWithShadow("Profit: " + EXPGained / 175 * yewPrice + "gp",x, y+= spacing, g);
               drawStringWithShadow("GP/Hour: " + (EXPPerHour * yewPrice) / 175,x, y+= spacing, g);
            } else {
               drawStringWithShadow("Log Type: "+logType,x, y+= spacing, g);
               drawStringWithShadow("Log Price: N/A",x, y+= spacing, g);
               drawStringWithShadow("Logs Banked: N/A",x, y+= spacing, g);
               drawStringWithShadow("Profit: N/A",x, y+= spacing, g);
               drawStringWithShadow("GP/Hour: N/A",x, y+= spacing, g);
            }
         }
      }
   }


   public boolean isWithinBounds(Point p, int x, int y, int w, int h) {
      if (p.x > x && p.x < x+w && p.y > y && p.y < y+h) {
         return true;
      } else {
         return false;
      }
   }

   public void drawStringWithShadow(String text, int x, int y, Graphics g) {
        Color col = g.getColor();
        g.setColor(black);
        g.drawString(text, x+1, y+1);
        g.setColor(col);
        g.drawString(text, x, y);
    }

   public int loop() {
      if (atWelcomButton()) {
         return 500;
      }

      if (game.isLoggedIn() && skills.getCurrentLvl(8) != 0) {

         camera.setAltitude(true);

         if (player.getMyEnergy() > random(30, 60)) {
            game.setRun(true);
         }

         if (startTime == 0 && skills.getCurrentLvl(8) != 0) {
            startTime = System.currentTimeMillis();
            startLevel = skills.getCurrentLvl(8);
            startEXP = skills.getCurrentXP(8);
         }

         if (getRuneAxe == true) {
            if ((skills.getCurrentLvl(8) >= 41)
               && !inventory.contains(runeAxe)) {
               status = "Walking to bank";
               walk.to(faladorEastBank);
               wait(random(1000, 2000));
               swapAxe(runeAxe);
               return random(500, 800);
            }
         }
         if (getAdamantAxe == true) {
            if ((skills.getCurrentLvl(8) >= 31)
               && (skills.getCurrentLvl(8) < 41)
               && !inventory.contains(adamantAxe)) {
               status = "Walking to bank";
               walk.to(faladorEastBank);
               wait(random(1000, 2000));
               swapAxe(adamantAxe);
               return random(500, 800);
            }
         }
         if (getMithrilAxe == true) {
            if ((skills.getCurrentLvl(8) >= 21)
               && (skills.getCurrentLvl(8) < 31)
               && !inventory.contains(mithrilAxe)) {
               status = "Walking to bank";
               walk.to(faladorEastBank);
               wait(random(1000, 2000));
               swapAxe(mithrilAxe);
               return random(500, 800);
            }
         }

         if (player.getMyLocation().getX() > halfWayWalkPoint.getX() + 5) {
            status = "Walking";
            walk.to(halfWayWalkPoint);
            return random(200, 500);
         }


         if (skills.getCurrentLvl(8) < 20) {
            logType = "logs";
         } else if (skills.getCurrentLvl(8) < 35) {
            logType = "oaks";
         } else if (skills.getCurrentLvl(8) < cutYewsAt
                  || cutYews == false) {
            logType = "willows";
         } else {
            logType = "yews";
         }

         if (inventory.isFull() || (bank.getInterface().isValid() && inventory.getCount() == 14)) {
            if (logType == "yews") {
               status = "Walking to bank";
               walk.pathMM(yewsToBank);
               if (calculate.distanceTo(faladorEastBank) < random (2,5)) {
                  status = "Banking yews";
                  depositYews();
               } else {
                  walk.tileMM(faladorEastBank);
               }
               return random(300, 800);
            } else {
               status = "Dropping " + logType;
               inventory.dropAllExcept(axeTypes);
            }
         }


         if (skills.getCurrentLvl(8) < 20) {
            currentTrees = NORMALTREES;
            if (calculate.distanceTo(normalTreesLocation) > 12) {
               status = "Walking to trees";
               walk.to(normalTreesLocation);
               // status = "Return";
               return random(500, 800);
            }
         } else if (skills.getCurrentLvl(8) < 35) {
            currentTrees = OAKTREES;
            if (calculate.distanceTo(oakTreesLocation) > 12) {
               status = "Walking to oaks";
               walk.to(oakTreesLocation);
               // status = "Return";
               return random(500, 800);
            }
         } else if (skills.getCurrentLvl(8) < cutYewsAt
                  || cutYews == false) {
            currentTrees = WILLOWTREES;
            if (calculate.distanceTo(willowTreesLocation) > 12) {
               status = "Walking to willows";
               walk.to(willowTreesLocation);
               // status = "Return";
               return random(500, 800);
            }
         } else {
            currentTrees = YEWTREES;
            if (calculate.distanceTo(yewTreesLocation) > 12 && !inventory.isFull()) {
               status = "Walking to yews";
               walk.pathMM(bankToYews);
               // status = "Return";
               return random(500, 800);
            }
         }

         if ((me.getAnimation() != -1 || (me.isMoving()))) {
            return antiBan();
         } else {
            status = "Cutting " + logType;
            chopTree(currentTrees);
            return random(500, 800);
         }
      }
      return (random(500, 800));
   }

   // ///////////////////////
   // FUNCTIONS //
   // ///////////////////////

   public RSObject getLowestCorner(final RSObject testObj) {
      RSObject cur = null;
      int testID = testObj.getID();
      int lowestX = -1;
      int lowestY = -1;
      int x, y;

      for (x = 0; x < 5; x++) {
         final RSObject o = objects.getTopAt(testObj.getLocation().getX() - x, testObj.getLocation().getY());
         if (o != null) {
            if (o.getID() != testID && lowestX == -1) {
               lowestX = testObj.getLocation().getX() - x + 1;
            }
         } else if (lowestX == -1) {
            lowestX = testObj.getLocation().getX() - x + 1;
         }

      }
      for (y = 0; y < 5; y++) {
         final RSObject o = objects.getTopAt(lowestX, testObj.getLocation().getY() - y);
         if (o != null) {
            if (o.getID() != testID && lowestY == -1) {
               lowestY = testObj.getLocation().getY() - y + 1;
            }
         } else if (lowestY == -1) {
            lowestY = testObj.getLocation().getY() - y + 1;
         }
      }

      cur = objects.getTopAt(lowestX, lowestY);
      return cur;

   }

   public boolean chopTree(int treeIds[]) {
      RSObject Tree = objects.getNearestByID(treeIds);
      if (Tree == null)
         return false;
      final RSTile treeLoc = Tree.getLocation();

      /*if (calculate.distanceTo(treeLoc) == 5) {
       turnToTile(treeLoc);
       wait(random(300, 500));
       }
       */
      if (calculate.distanceTo(treeLoc) > random(4, 5)) {
         walk.tileMM(treeLoc);
         wait(random(200, 500));
      }
      if (random(1,20) == 10) {
         walk.tileMM(treeLoc);
         wait(random(200, 500));
      }


      RSObject finalTree = getLowestCorner(Tree);

      if (finalTree != null)  {
         return finalTree.action("Chop down");
      } 
      
      return false;
   }


   public void swapAxe(int axeID) {
      try {
         if (calculate.distanceTo(faladorEastBank) < random (2,4) && !bank.getInterface().isValid()) {
            walk.tileMM(faladorEastBank);
            wait(random(200,500));
         }
         
         bank.open();

         if (bank.isOpen()) {
            if (bank.getCount(axeID) == 0) {
               if (axeID == runeAxe) {
                  getRuneAxe = false;
                  log("Could not find rune hatchet in bank. Next time don't select it at the start!");
               }
               if (axeID == adamantAxe) {
                  getAdamantAxe = false;
                  log("Could not find adamant hatchet in bank. Next time don't select it at the start!");
               }
               if (axeID == mithrilAxe) {
                  getMithrilAxe = false;
                  log("Could not find mithril hatchet in bank. Next time don't select it at the start!");
               }

               wait(500);
               bank.close();
               
               return;
            }
            bank.depositAll();
            wait(random(400, 800));
            bank.atItem(axeID, "Withdraw-1");
            wait(random(1500, 2100));
            bank.close();
         }
      } catch (final Exception e) {
      }
      return;
   }

   public void depositYews() {
      try {
    	  bank.open();

         if (bank.isOpen()) {
            bank.depositAllExcept(axeTypes);
            wait(random(400, 800));
         }
      } catch (final Exception e) {
      }
      return;
   }


   public int antiBan() {
      final int ranNo = random(0,2000);
      if (ranNo == 2) {
         if (game.getCurrentTab() != Game.tabStats) {
            game.openTab(Game.tabStats);
            mouse.move(random(677, 726), random(356, 370));
            wait(random(500, 750));
            return random(100, 200);
         }
      } else if (ranNo == 4) {
         mouse.move(random(0, 700), random(0, 500));
         return random(200, 400);
      } else if (ranNo == 5) {
         mouse.move(random(0, 450), random(0, 400));
         return random(200, 400);
      }
      return random(200, 450);
   }
}