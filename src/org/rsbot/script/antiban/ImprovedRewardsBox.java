package org.rsbot.script.antiban;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.rsbot.script.Antiban;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.util.GlobalConfiguration;

/*
 * Cleaned and Updated By Secret Spy
 * Updated On 10/07/10
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Improved Rewards Box", version = 1.2)
public class ImprovedRewardsBox extends Antiban {

	Rectangle Temporary;
	String Filename;
	public String[] Choices = { "Cash", "Runes", "Coal", "Essence", "Ore",
			"Bars", "Gems", "Herbs", "Seeds", "Charms", "XP item", "Surprise",
			"Emote", "Costume", "Drop" };
	String XPChoice = "Attack";
	final int BookOfKnowledgeID = 11640;// Random Even Book
	final int LampID = 2528; // Random Event lamp
	final int MysteryBoxID = 6199;
	final int BoxID = 14664; // Random Event Box
	final int BoxIF = 202;
	final int BoxConfirmIF = 28;
	final int BoxSelectionIF = 15;
	final int BoxScrollBarIF = 24;
	int OptionSelected = 999;
	boolean Drop = false;
	final int XPIF = 1139;
	final int AgilityID = 5;
	final int AttackID = 4;
	final int CookingID = 15;
	final int ConstructionID = 19;
	final int CraftingID = 26;
	final int DefenseID = 24;
	final int DungeoneeringID = 28;
	final int FarmingID = 11;
	final int FiremakingID = 16;
	final int FishingID = 7;
	final int FletchingID = 18;
	final int HerbloreID = 6;
	final int HitpointsID = 25;
	final int HunterID = 14;
	final int MagicID = 23;
	final int MiningID = 12;
	final int PrayerID = 27;
	final int RangedID = 22;
	final int RunecraftingID = 9;
	final int SlayerID = 10;
	final int SmithingID = 13;
	final int StrengthID = 21;
	final int SummoningID = 20;
	final int ThievingID = 8;
	final int WoodcuttingID = 17;
	final int ConfirmID = 2;
	int ScrollBarBottomLength;
	int ScrollBarTopLength;
	int HiddenScreenHeight;
	double Difference;
	int EndOfSelection = 0;

	@Override
	public boolean activateCondition() {
		return game.isLoggedIn()
		&& !player.getMine().isInCombat()
		&& game.getCurrentTab() == game.tabInventory
		&& (inventory.contains(BoxID)
				|| inventory.contains(BookOfKnowledgeID)
				|| inventory.contains(LampID) || inventory.contains(MysteryBoxID));

	}

	public int getActualY(final RSInterfaceChild Component) {
		int boxYPos;
		final RSInterfaceChild[] selection = iface.get(202).getChild(15)
		.getChildren();
		final RSInterfaceChild[] scrollbar = iface.get(202).getChild(24)
		.getChildren();
		for (int end = 0; end < selection.length; end++) {
			if (selection[end].containsText(":")) {
				EndOfSelection = (end - 6);
			}
			if (selection[end].containsText("emote")) {
				EndOfSelection = (end - 6);
			}
			if (selection[end].containsText("costume")) {
				EndOfSelection = (end - 6);
			}
		}
		final int viewableScreenHeight = (iface.get(202).getChild(15).getHeight() - 11);
		final int totalScreenHeight = (selection[EndOfSelection].getAbsoluteY()
				+ selection[EndOfSelection].getHeight() - selection[0]
				                                                    .getAbsoluteY());
		HiddenScreenHeight = (totalScreenHeight - viewableScreenHeight);
		if (HiddenScreenHeight > 0) {
			ScrollBarTopLength = (scrollbar[1].getAbsoluteY() - scrollbar[0]
			                                                              .getAbsoluteY());
			Difference = (Double.parseDouble(Integer
					.toString(ScrollBarTopLength))
					/ Double.parseDouble(Integer
							.toString(ScrollBarBottomLength)) * Double
							.parseDouble(Integer.toString(HiddenScreenHeight)));
			boxYPos = (Component.getAbsoluteY() - (int) Difference);
		} else {
			boxYPos = Component.getAbsoluteY();
		}
		return boxYPos;
	}

	public Rectangle getBoxArea(final RSInterfaceChild Component) {
		final Rectangle boxArea = new Rectangle(Component.getAbsoluteX(),
				getActualY(Component), Component.getWidth(),
				Component.getHeight());
		return boxArea;
	}

	@Override
	public int loop() {
		if (iface.get(BoxIF).isValid()) {

			Filename = new File(
					GlobalConfiguration.Paths.getSettingsDirectory())
			+ File.separator + "PRds";
			final File RewardsChoiceFile = new File(Filename);
			if (RewardsChoiceFile.exists()) {
				try {
					final BufferedReader in = new BufferedReader(new FileReader(
							Filename));
					String inputLine = "";
					int choicenumber = 0;
					while ((inputLine = in.readLine()) != null) {
						if (choicenumber > (Choices.length - 1)) {
							XPChoice = inputLine;
							break;
						}
						Choices[choicenumber] = inputLine;
						choicenumber++;
					}
				} catch (final Exception e) {
					log("Error opening");
				}
			}
			RSInterfaceChild[] selection = iface.get(BoxIF).getChild(
					BoxSelectionIF).getChildren();
			for (int o = 0; o < Choices.length; o++) {
				if (Choices[o].toLowerCase().equals("drop")) {
					if (inventory.getCount(BoxID) >= 1) {
						Drop = true;
						break;
					} else {
						Drop = false;
						continue;
					}
				}
				for (int i = 0; i < selection.length; i++) {
					if (selection[i].getText().toLowerCase()
							.contains(Choices[o].toLowerCase())) {
						OptionSelected = i - 6;
						break;
					}
				}
				if (OptionSelected != 999) {
					break;
				}
			}
			if (OptionSelected == 999) {
				OptionSelected = 0;
			}
			Temporary = getBoxArea(selection[OptionSelected]);
			if (getBoxArea(selection[OptionSelected]).y > 278) {
				final RSInterfaceChild[] scrollbar = iface.get(BoxIF).getChild(
						BoxScrollBarIF).getChildren();
				ScrollBarBottomLength = (scrollbar[5].getAbsoluteY()
						- scrollbar[3].getAbsoluteY()
						+ scrollbar[3].getHeight() - 6);
				mouse.move(scrollbar[1].getPoint().x + random(-4, 4),
						scrollbar[1].getPoint().y + random(-15, 15));
				int toDragtoY = (int) mouse.getLocation().getY()
				+ (getBoxArea(selection[OptionSelected]).y - selection[0]
				                                                       .getAbsoluteY());
				if ((toDragtoY - (int) mouse.getLocation().getY()) > (scrollbar[5]
				                                                               .getAbsoluteY()
				                                                               - scrollbar[3].getAbsoluteY()
				                                                               + scrollbar[3].getHeight() - 6)) {
					toDragtoY = (int) mouse.getLocation().getY()
					+ (scrollbar[5].getAbsoluteY()
							- scrollbar[3].getAbsoluteY()
							+ scrollbar[3].getHeight() - 6);
				}
				mouse.drag((int) mouse.getLocation().getX(), toDragtoY);
			}
			wait(random(3000, 4000));
			selection = iface.get(BoxIF).getChild(BoxSelectionIF)
			.getChildren();
			final int boxX = getBoxArea(selection[OptionSelected]).x + 15;
			final int boxY = getBoxArea(selection[OptionSelected]).y + 15;
			final int boxWidth = getBoxArea(selection[OptionSelected]).width - 30;
			final int boxHeight = getBoxArea(selection[OptionSelected]).height - 30;
			Temporary = getBoxArea(selection[OptionSelected]);
			mouse.move(random(boxX, boxX + boxWidth),
					random(boxY, boxY + boxHeight));
			mouse.click(true);
			iface.clickChild(BoxIF, BoxConfirmIF);
			wait(random(3000, 4000));
		}
		if (iface.get(XPIF).isValid()) {
			final String filename = new File(
					GlobalConfiguration.Paths.getSettingsDirectory())
			+ File.separator + "PRds";
			final File RewardsChoiceFile = new File(filename);
			if (RewardsChoiceFile.exists()) {
				try {
					final BufferedReader in = new BufferedReader(new FileReader(
							filename));
					String inputLine = "";
					int ChoiceNumber = 0;
					while ((inputLine = in.readLine()) != null) {
						if (ChoiceNumber > (Choices.length - 1)) {
							XPChoice = inputLine;
							break;
						}
						Choices[ChoiceNumber] = inputLine;
						ChoiceNumber++;
					}
				} catch (final Exception e) {
					log("Error opening");
				}
			}
			int XPSelection = 0;
			if (XPChoice.contains("Attack")) {
				XPSelection = AttackID;
			}
			if (XPChoice.contains("Strength")) {
				XPSelection = StrengthID;
			}
			if (XPChoice.contains("Defence")) {
				XPSelection = DefenseID;
			}
			if (XPChoice.contains("Ranged")) {
				XPSelection = RangedID;
			}
			if (XPChoice.contains("Prayer")) {
				XPSelection = PrayerID;
			}
			if (XPChoice.contains("Magic")) {
				XPSelection = MagicID;
			}
			if (XPChoice.contains("Runecrafting")) {
				XPSelection = RunecraftingID;
			}
			if (XPChoice.contains("Construction")) {
				XPSelection = ConstructionID;
			}
			if (XPChoice.contains("Dungeoneering")) {
				XPSelection = DungeoneeringID;
			}
			if (XPChoice.contains("Hitpoints")) {
				XPSelection = HitpointsID;
			}
			if (XPChoice.contains("Agility")) {
				XPSelection = AgilityID;
			}
			if (XPChoice.contains("Herblore")) {
				XPSelection = HerbloreID;
			}
			if (XPChoice.contains("Thieving")) {
				XPSelection = ThievingID;
			}
			if (XPChoice.contains("Crafting")) {
				XPSelection = CraftingID;
			}
			if (XPChoice.contains("Fletching")) {
				XPSelection = FletchingID;
			}
			if (XPChoice.contains("Slayer")) {
				XPSelection = SlayerID;
			}
			if (XPChoice.contains("Hunter")) {
				XPSelection = HunterID;
			}
			if (XPChoice.contains("Mining")) {
				XPSelection = MiningID;
			}
			if (XPChoice.contains("Smithing")) {
				XPSelection = SmithingID;
			}
			if (XPChoice.contains("Fishing")) {
				XPSelection = FishingID;
			}
			if (XPChoice.contains("Cooking")) {
				XPSelection = CookingID;
			}
			if (XPChoice.contains("Firemaking")) {
				XPSelection = FiremakingID;
			}
			if (XPChoice.contains("Woodcutting")) {
				XPSelection = WoodcuttingID;
			}
			if (XPChoice.contains("Farming")) {
				XPSelection = FarmingID;
			}
			if (XPChoice.contains("Summoning")) {
				XPSelection = SummoningID;
			}
			iface.clickChild(XPIF, XPSelection);
			iface.clickChild(XPIF, ConfirmID);
			wait(random(3000, 4000));
		}
		if (inventory.contains(BoxID) && Drop == true) {
			if (inventory.getCount(BoxID) >= 1) {
				inventory.clickItem(BoxID, "Drop");
			}
		}
		if (inventory.contains(BoxID)) {
			inventory.clickItem(BoxID, "Open");
			return random(3000, 4000);
		}
		if (inventory.contains(BookOfKnowledgeID)) {
			inventory.clickItem(BookOfKnowledgeID, "Read");
			return random(3000, 4000);
		}
		if (inventory.contains(LampID)) {
			inventory.clickItem(LampID, "Rub");
			return random(3000, 4000);
		}
		if (inventory.contains(MysteryBoxID)) {
			inventory.clickItem(MysteryBoxID, "Open");
			return random(3000, 4000);
		}
		return -1;
	}

	@Override
	public void onFinish() {
	}
}
