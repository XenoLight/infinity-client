package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;

/**
 * @author Secret Spy
 * @version 1.1 - 02/11/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Quiz", version = 1.1)
public class QuizSolver extends Random implements PaintListener {

	public class QuizQuestion {

		int IDOne;
		int IDTwo;
		int IDThree;
		int Answer;

		public QuizQuestion(final int One, final int Two, final int Three) {
			IDOne = One;
			IDTwo = Two;
			IDThree = Three;
		}

		public boolean activateCondition() {
			if (IDToSlot(IDOne) != -1) {
				if (IDToSlot(IDTwo) != -1) {
					if (IDToSlot(IDThree) != -1) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean arrayContains(final int[] arr, final int i) {
			boolean returnt = false;
			for (final int Num : arr) {
				if (Num == i) {
					returnt = true;
				}
			}
			return returnt;
		}

		public boolean clickAnswer() {
			Answer = -1;
			int count = 0;
			for (int j = 0; j < Items.length; j++) {
				if (arrayContains(Items[j], IDOne)) {
					log.warning("Slot 1: " + Names[j]);
					count++;
				}
				if (arrayContains(Items[j], IDTwo)) {
					log.warning("Slot 2: " + Names[j]);
					count++;
				}
				if (arrayContains(Items[j], IDThree)) {
					log.warning("Slot 3: " + Names[j]);
					count++;
				}
				if (count >= 2) {
					log.config("Type Found: " + Names[j]);
					Answer = j;
					break;
				}
			}
			if (Answer != -1) {
				int Slot;
				if ((Slot = findNotInAnswerArray()) != -1) {
					return atSlot(Slot);
				} else {
					log.warning(ScreenLog.quizAFail);
					return false;
				}
			} else {
				log.warning(ScreenLog.quizFail);
				return false;
			}
		}

		public int findNotInAnswerArray() {
			if (!arrayContains(Items[Answer], IDOne)) {
				return 1;
			} else if (!arrayContains(Items[Answer], IDTwo)) {
				return 2;
			} else if (!arrayContains(Items[Answer], IDThree)) {
				return 3;
			} else {
				return -1;
			}
		}
	}

	public int QuizInterface = 191;
	public int[] Fish = { 6190, 6189 };
	public int[] Jewelry = { 6198, 6197 };
	public int[] Weapons = { 6192, 6194 };
	public int[] Farming = { 6195, 6196 };
	public int[][] Items = { Fish, Jewelry, Weapons, Farming };
	public String[] Names = { "Fish", "Jewelry", "Weapons", "Farming" };

	@Override
	public boolean activateCondition() {
		final RSNPC QuizMaster = npc.getNearestByName("Quiz Master");
		return QuizMaster != null;
	}

	public void atRandom() {
		atSlot(random(1, 3));
	}

	public boolean atSlot(final int slot) {
		switch (slot) {
		case 1:
			return iface.clickChild(QuizInterface, 3);
		case 2:
			return iface.clickChild(QuizInterface, 4);
		case 3:
			return iface.clickChild(QuizInterface, 5);
		default:
			return false;
		}
	}

	public int IDToSlot(final int Id) {
		if (SlotToID(1) == Id) {
			return 1;
		} else if (SlotToID(2) == Id) {
			return 2;
		} else if (SlotToID(3) == Id) {
			return 3;
		} else {
			return -1;
		}
	}

	@Override
	public int loop() {
		final RSNPC QuizMaster = npc.getNearestByName("Quiz Master");
		if (QuizMaster == null) {
			return -1;
		}
		if (SlotToID(1) != -1) {
			log.config(ScreenLog.quizDet);
			final QuizQuestion Question = new QuizQuestion(SlotToID(1),
					SlotToID(2), SlotToID(3));
			if (Question.clickAnswer()) {
				return random(2200, 3000);
			} else {
				log.config(ScreenLog.quizTry);
				atRandom();
				return random(1200, 2200);
			}
		} else {
			if (iface.clickContinue()) {
				return random(800, 1200);
			}
		}
		return random(1200, 2000);
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.quiz, 9, 330);
		ScreenMouse.paint(render);
	}

	public int SlotToID(final int Slot) {
		switch (Slot) {
		case 1:
			return iface.get(QuizInterface).getChild(6).getChildID();
		case 2:
			return iface.get(QuizInterface).getChild(7).getChildID();
		case 3:
			return iface.get(QuizInterface).getChild(8).getChildID();
		default:
			return -1;
		}
	}
}
