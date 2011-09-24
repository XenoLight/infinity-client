package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/**
 * @author Secret Spy
 * @version 2.2 - 02/10/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Exam", version = 2.2)
public class Exam extends Random implements PaintListener {

	public class NextObjectQuestion {

		int One = -1;
		int Two = -1;
		int Three = -1;

		public NextObjectQuestion() {
		}

		public boolean arrayContains(final int[] arr, final int i) {
			boolean returnt = false;
			for (final int num : arr) {
				if (num == i) {
					returnt = true;
				}
			}

			return returnt;
		}

		public boolean clickAnswer() {
			int[] Answers;
			if ((Answers = returnAnswer()) == null) {
				return false;
			}

			for (int i = 10; i <= 13; i++) {
				if (arrayContains(Answers, iface.get(nextObjectInterface)
						.getChild(i).getChildID())) {
					return iface.clickChild(nextObjectInterface, i);
				}
			}

			return false;
		}

		public boolean getObjects() {
			One = -1;
			Two = -1;
			Three = -1;
			One = iface.getChild(nextObjectInterface, 6).getChildID();
			Two = iface.getChild(nextObjectInterface, 7).getChildID();
			Three = iface.getChild(nextObjectInterface, 8).getChildID();

			return (One != -1) && (Two != -1) && (Three != -1);
		}

		public void guess() {
			final int[] objects = new int[4];
			objects[0] = iface.getChild(nextObjectInterface, 10).getChildID();
			objects[1] = iface.getChild(nextObjectInterface, 11).getChildID();
			objects[2] = iface.getChild(nextObjectInterface, 12).getChildID();
			objects[3] = iface.getChild(nextObjectInterface, 13).getChildID();

			//TODO:  Not sure what compare is supposed to do, but the 
			// if (compare <= 10) always evaluates to true, bypassing the 
			// for loop that follows.
			int lowest = 120;
			int click = 10;
			final int compare = 0;
			if (compare <= 10) {
				iface.clickChild(nextObjectInterface, (random(10, 13)));
				return;
			}

			//TODO: This is the dead code
			for (int i = 0; i < objects.length; i++) {
				if (Math.abs(objects[i] - compare) <= lowest) {
					lowest = Math.abs(objects[i] - compare);
				}
				click = 10 + i;
			}
			iface.clickChild(nextObjectInterface, click);
			//TODO: End of dead code
		}

		public int[] returnAnswer() {
			final int[] count = new int[items.length];
			int firstcard = 0;
			int secondcard = 0;
			int thirdcard = 0;

			for (int i = 0; i < count.length; i++) {
				count[i] = 0;
			}
			// Will verify that all IDs are IDs which we currently have
			for (final int[] item : items) {
				for (final int anItem : item) {
					if (anItem == One) {
						firstcard = 1;
					}
					if (anItem == Two) {
						secondcard = 1;
					}
					if (anItem == Three) {
						thirdcard = 1;
					}
				}
			}
			if (firstcard == 0) {
				log.severe(ScreenLog.exam1Obj);
				log.severe(ScreenLog.examError2 + Integer.toString(One));
			}
			if (secondcard == 0) {
				log.severe(ScreenLog.exam2Obj);
				log.severe(ScreenLog.examError2 + Integer.toString(Two));
			}
			if (thirdcard == 0) {
				log.severe(ScreenLog.exam3Obj);
				log.severe(ScreenLog.examError2 + Integer.toString(Three));
			}

			for (int i = 0; i < items.length; i++) {
				for (int j = 0; j < items[i].length; j++) {
					if (items[i][j] == One) {
						count[i]++;
					}
					if (items[i][j] == Two) {
						count[i]++;
					}
					if (items[i][j] == Three) {
						count[i]++;
					}
					if (count[i] >= 2) {
						log.config(ScreenLog.examFound);
						return items[i];
					}
				}
			}

			return null;
		}
	}

	public class SimilarObjectQuestion {

		String question;
		int[] Answers;

		public SimilarObjectQuestion(final String q, final int[] Answers) {
			question = q.toLowerCase();
			this.Answers = Answers;
		}

		public boolean accept() {
			return iface.clickChild(relatedCardsInterface, 26);
		}

		public boolean activateCondition() {
			if (!iface.get(relatedCardsInterface).isValid()) {
				return false;
			}

			if (iface.getChild(relatedCardsInterface, 25).getText()
					.toLowerCase().contains(question)) {
				log.config(ScreenLog.examQuest + question);
				return true;
			}

			return false;
		}

		public boolean clickObjects() {
			int count = 0;
			for (int i = 42; i <= 56; i++) {
				for (final int answer : Answers) {
					if (iface.getChild(relatedCardsInterface, i).getChildID() == answer) {
						if (iface.clickChild(relatedCardsInterface, i)) {
							sleep(random(600, 1000));
						}
						count++;
						if (count >= 3) {
							return true;
						}
					}
				}
			}
			log.config(ScreenLog.examFalse);
			return false;
		}
	}

	public static final int nextObjectInterface = 103;
	public static final int relatedCardsInterface = 559;
	public static final int[] Ranged = { 11539, 11540, 11541, 11614, 11615,
		11633 };
	public static final int[] Cooking = { 11526, 11529, 11545, 11549, 11550,
		11555, 11560, 11563, 11564, 11607, 11608, 11616, 11620, 11621,
		11622, 11623, 11628, 11629, 11634, 11639, 11641, 11649, 11624 };
	public static final int[] Fishing = { 11527, 11574, 11578, 11580, 11599,
		11600, 11601, 11602, 11603, 11604, 11605, 11606, 11625 };
	public static final int[] Combat = { 11528, 11531, 11536, 11537, 11579,
		11591, 11592, 11593, 11597, 11627, 11631, 11635, 11636, 11638,
		11642, 11648, 11617 };
	public static final int[] Farming = { 11530, 11532, 11547, 11548, 11554,
		11556, 11571, 11581, 11586, 11610, 11645 };
	public static final int[] Magic = { 11533, 11534, 11538, 11562, 11567,
		11582 };
	public static final int[] Firemaking = { 11535, 11551, 11552, 11559, 11646 };
	public static final int[] Hats = { 11540, 11557, 11558, 11560, 11570,
		11619, 11626, 11630, 11632, 11637, 11654 };
	public static final int[] Pirate = { 11570, 11626, 11558 };
	public static final int[] Jewellery = { 11572, 11576, 11652 };
	public static final int[] Jewellery2 = { 11572, 11576, 11652 };
	public static final int[] Drinks = { 11542, 11543, 11544, 11644, 11647 };
	public static final int[] Woodcutting = { 11573, 11595 };
	public static final int[] Boots = { 11561, 11618, 11650, 11651 };
	public static final int[] Crafting = { 11546, 11553, 11565, 11566, 11568,
		11569, 11572, 11575, 11576, 11577, 11581, 11583, 11584, 11585,
		11643, 11652, 11653 };
	public static final int[] Mining = { 11587, 11588, 11594, 11596, 11598,
		11609, 11610 };
	public static final int[] Smithing = { 11611, 11612, 11613 };
	public static final int[][] items = { Ranged, Cooking, Fishing, Combat,
		Farming, Magic, Firemaking, Hats, Drinks, Woodcutting, Boots,
		Crafting, Mining, Smithing };
	public SimilarObjectQuestion[] simObjects = {
			new SimilarObjectQuestion(
					"I never leave the house without some sort of jewellery.",
					Jewellery),
					new SimilarObjectQuestion("There is no better feeling than",
							Jewellery2),
							new SimilarObjectQuestion("I'm feeling dehydrated", Drinks),
							new SimilarObjectQuestion("All this work is making me thirsty",
									Drinks),
									new SimilarObjectQuestion("quenched my thirst", Drinks),
									new SimilarObjectQuestion("light my fire", Firemaking),
									new SimilarObjectQuestion("fishy", Fishing),
									new SimilarObjectQuestion("fishing for answers", Fishing),
									new SimilarObjectQuestion("fish out of water", Drinks),
									new SimilarObjectQuestion("strange headgear", Hats),
									new SimilarObjectQuestion("tip my hat", Hats),
									new SimilarObjectQuestion("thinking cap", Hats),
									new SimilarObjectQuestion("wizardry here", Magic),
									new SimilarObjectQuestion("rather mystical", Magic),
									new SimilarObjectQuestion("abracada", Magic),
									new SimilarObjectQuestion("hide one's face", Hats),
									new SimilarObjectQuestion("shall unmask", Hats),
									new SimilarObjectQuestion("hand-to-hand", Combat),
									new SimilarObjectQuestion("melee weapon", Combat),
									new SimilarObjectQuestion("prefers melee", Combat),
									new SimilarObjectQuestion("me hearties", Pirate),
									new SimilarObjectQuestion("puzzle for landlubbers", Pirate),
									new SimilarObjectQuestion("mighty pirate", Pirate),
									new SimilarObjectQuestion("mighty archer", Ranged),
									new SimilarObjectQuestion("as an arrow", Ranged),
									new SimilarObjectQuestion("Ranged attack", Ranged),
									new SimilarObjectQuestion("shiny things", Crafting),
									new SimilarObjectQuestion("igniting", Firemaking),
									new SimilarObjectQuestion("sparks from my synapses.", Firemaking),
									new SimilarObjectQuestion("fire.", Firemaking),
									new SimilarObjectQuestion("disguised", Hats),
									// added diguised Feb 04,2010

									// Default questions just incase the bot gets stuck
									new SimilarObjectQuestion("range", Ranged),
									new SimilarObjectQuestion("arrow", Ranged),
									new SimilarObjectQuestion("drink", Drinks),
									new SimilarObjectQuestion("logs", Firemaking),
									new SimilarObjectQuestion("light", Firemaking),
									new SimilarObjectQuestion("headgear", Hats),
									new SimilarObjectQuestion("hat", Hats),
									new SimilarObjectQuestion("cap", Hats),
									new SimilarObjectQuestion("mine", Mining),
									new SimilarObjectQuestion("mining", Mining),
									new SimilarObjectQuestion("ore", Mining),
									new SimilarObjectQuestion("fish", Fishing),
									new SimilarObjectQuestion("fishing", Fishing),
									new SimilarObjectQuestion("thinking cap", Hats),
									new SimilarObjectQuestion("cooking", Cooking),
									new SimilarObjectQuestion("cook", Cooking),
									new SimilarObjectQuestion("bake", Cooking),
									new SimilarObjectQuestion("farm", Farming),
									new SimilarObjectQuestion("farming", Farming),
									new SimilarObjectQuestion("cast", Magic),
									new SimilarObjectQuestion("magic", Magic),
									new SimilarObjectQuestion("craft", Crafting),
									new SimilarObjectQuestion("boot", Boots),
									new SimilarObjectQuestion("chop", Woodcutting),
									new SimilarObjectQuestion("cut", Woodcutting),
									new SimilarObjectQuestion("tree", Woodcutting), };
	public RSObject door = null;

	@Override
	public boolean activateCondition() {
		door = null;
		final RSNPC n = npc.getNearestByName("Mr. Mordaut");
		return n != null;
	}


	@Override
	public int loop() {
		camera.setAltitude(true);
		final RSNPC Mordaut = npc.getNearestByName("Mr. Mordaut");
		if (Mordaut == null) {
			return -1;
		}

		if (player.getMine().isMoving()
				|| (player.getMine().getAnimation() != -1)) {
			return random(800, 1200);
		}

		if (door != null) {
			if (door.distanceTo() > 3) {
				walk.to(door.getLocation());
				wait(random(1400, 2500));
			}
			if (!door.isOnScreen()) {
				walk.tileMM(door.getLocation());
				sleep(random(1400, 2500));
			}
			if (door.getID() == 2188) {
				camera.setCompass('w');
			}
			if (door.getID() == 2193) {
				camera.setCompass('e');
			}
			if (door.getID() == 2189) {
				camera.setCompass('w');
			}
			if (door.getID() == 2192) {
				camera.setCompass('n');
			}
			
			door.action("Open");
			//clickObject(door, "Open");
			
			return random(500, 1000);
		}
		final RSInterfaceChild inter = searchInterfacesText("To exit,");
		if (inter != null) {
			if (inter.getText().toLowerCase().contains("red")) {
				door = objects.getNearestByID(2188);
			}
			if (inter.getText().toLowerCase().contains("green")) {
				door = objects.getNearestByID(2193);
			}
			if (inter.getText().toLowerCase().contains("blue")) {
				door = objects.getNearestByID(2189);
			}
			if (inter.getText().toLowerCase().contains("purple")) {
				door = objects.getNearestByID(2192);
			}
		}
		if (!iface.get(nextObjectInterface).isValid()
				&& !player.getMine().isMoving()
				&& !iface.get(relatedCardsInterface).isValid()
				&& !iface.canContinue() && door == null) {
			if (Mordaut.isOnScreen()) {
				walk.tileMM(Mordaut.getLocation());
				int fail = 0;
				while (fail++ <= 10 && !player.getMine().isMoving()) {
					sleep(300, 500);
				}
			}
			while (player.getMine().isMoving())
				sleep(300);
			npc.click(Mordaut, "Talk-to");
			return (random(1500, 1700));
		}
		if (iface.get(nextObjectInterface).isValid()) {
			log.config(ScreenLog.examType1);
			final NextObjectQuestion noq = new NextObjectQuestion();
			if (noq.getObjects()) {
				if (noq.clickAnswer()) {
					return random(800, 1200);
				} else {
					noq.guess();
					return random(800, 1200);
				}
			} else {
				log.config(ScreenLog.examNoObj);
				noq.guess();
				return random(800, 1200);
			}
		}

		if (iface.get(relatedCardsInterface).isValid()) {
			log.config(ScreenLog.examType);
			int z = 0;
			for (final SimilarObjectQuestion obj : simObjects) {
				if (obj.activateCondition()) {
					z = 1;
					if (obj.clickObjects()) {
						obj.accept();
					}
				}
			}
			if (z == 0) {
				log.severe(ScreenLog.examError);
				log.severe(ScreenLog.examError1);
				log.severe(ScreenLog.examError2);
				log(iface.get(nextObjectInterface).getChild(25).getText()
						.toLowerCase());
			}
			return random(800, 1200);
		}

		if (iface.clickContinue()) {
			return random(800, 3500);
		}

		return random(800, 1200);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.exam, 9, 330);
		ScreenMouse.paint(render);
	}

	public RSInterfaceChild searchInterfacesText(final String string) {
		final RSInterface[] inters = iface.getAll();
		for (final RSInterface inter : inters) {
			for (final RSInterfaceChild interfaceChild : inter) {
				if (interfaceChild.getText().toLowerCase()
						.contains(string.toLowerCase())) {
					return interfaceChild;
				}
			}
		}

		return null;
	}
}
