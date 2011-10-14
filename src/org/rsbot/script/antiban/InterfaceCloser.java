package org.rsbot.script.antiban;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.script.Antiban;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;

/*
 * Cleaned and Updated By Secret Spy for Infinity
 * Updated On 04/16/11
 */
@ScriptManifest(authors = { "Pervy Shuya", "Secret Spy" }, name = "Interface Closer", version = 1.3)
public class InterfaceCloser extends Antiban {
	private static Logger logger = Logger.getLogger(Bot.class.getPackage().getName());

	private final Map<RSInterfaceChild, String> components = new HashMap<RSInterfaceChild, String>();
	{
		addChild( 743,  20, "Audio");
		addChild( 767,  10, "Bank of RuneScape - Help");
		addChild( 499,  29, "Stats");
		addChild( 594,  48, "Report Abuse");
		addChild( 275,   8, "Quest");
		addChild( 206,  13, "Price check");
		addChild( 266,   1, "Tombstone");
		addChild( 266,  11, "Grove");
		addChild( 102,  13, "Death items");
		addChild(  14,  11, "Pin settings");
		addChild( 157,  13, "Quick chat help");
		addChild( 764,  18, "Objectives");
		addChild( 895,  19, "Advisor");
		addChild( 109,  14, "Grand exchange collection");
		addChild( 667,  74, "Equipment Bonus");
		addChild( 742,  18, "Graphic");
		addChild( 917,  73, "Task List");
		addChild(1107, 174, "Clan Vexillum");
		addChild( 276,  76, "Soul Wars Rewards");
		addChild(1011,  51, "Pest Control Rewards ( Commendation Rewards )");
		addChild( 732, 208, "Fist of Guthx Reward Shop");
		addChild(1083, 181, "Livid Farm Rewards");
	}

	@Override
	public boolean activateCondition() {
		if (game.isLoggedIn()) {
			if (iface.get(755).getChild(44).isValid()) { // World map
				if (iface.getChild(755, 0).getChildren().length > 0) {
					Bot.debug(logger, "Interface 755 (World map) is open");
					return true;
				}
			}
			
			for (final RSInterfaceChild c : components.keySet()) {
				// Include check for the bank interface NOT being open.
				// It appears that sometimes, interface closer interfaces get
				// opened when some banks (namely Lunar) are opened, therefore,
				// this should only trigger if the bank is NOT also open.
				if (c.isValid() && c.getAbsoluteX() != -1
						&& c.getAbsoluteY() != -1 && !bank.isBankOpen()) {
					Bot.debug(logger, String.format("Interface %d, child %d (%s) is open with x=%d and y=%d", 
							c.getParentID(), c.getChildID(), components.get(c), c.getAbsoluteX(), c.getAbsoluteY()));
					return true;
				}
			}
		}
		return false;
	}

	private void addChild(final int parent, final int idx, String name) {
		components.put(iface.get(parent).getChild(idx), name);
	}

	@Override
	public int loop() {
		wait(random(500, 900));

		if (iface.get(755).isValid()
				&& (iface.getChild(755, 0).getChildren().length > 0)) {
			log("Closing the world map interface");
			iface.clickChild(755, 44);
			return random(500, 900);
		}
		for (final RSInterfaceChild c : components.keySet()) {
			if (c.isValid()) {
				log(String.format("Closing interface: %s", components.get(c)));
				iface.clickChild(c);
				wait(random(500, 900));
				break;
			}
		}
		return -1;
	}
}