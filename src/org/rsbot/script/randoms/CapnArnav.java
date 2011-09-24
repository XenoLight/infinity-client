package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;

/*
 * @author Secret Spy
 * @version 1.1 - 04/16/11
 */
@ScriptManifest(authors = "Secret Spy", name = "CapnArnav", version = 1.1)
public class CapnArnav extends Random implements PaintListener {

	private static enum STATE {
		OpenChest, Solve, Talk, Exit
	}
	private static final int[] ArnavChest = { 42337, 42338 };
	private static final int ArnavID = 2308;
	private static final int ExitPortal = 11369;
	private static final int[][] IFaceSolveIDs = { { 7, 14, 21 }, /* Bowl */
		{ 5, 12, 19 }, /* Ring */
		{ 6, 13, 20 }, /* Coin */
		{ 8, 15, 22 } }; /* Bar */
	private static final int[][] Arrows = { { 2, 3 }, { 9, 10 }, { 16, 17 } };
	private static final int TalkIFace = 228;
	private static final int ChestIFaceParent = 185;
	private static final int ChestIFaceUnlock = 28;
	private static final int ChestIFaceCenter = 23;
	private int index = -1;

	Point p;

	@Override
	public boolean activateCondition() {
		final RSNPC captain = npc.getNearestByID(ArnavID);
		if (captain != null) {
			sleep(random(1500, 1600));
			final RSObject portal = objects.getNearestByID(ExitPortal);
			return portal != null;
		}
		return false;
	}

	private STATE getState() {
		if (objects.getNearestByID(ArnavChest[1]) != null) {
			return STATE.Exit;
		} else if (iface.canContinue() || iface.get(TalkIFace) != null
				&& iface.get(TalkIFace).isValid()) {
			return STATE.Talk;
		} else if (iface.get(ChestIFaceParent) == null
				|| !iface.get(ChestIFaceParent).isValid()) {
			return STATE.OpenChest;
		} else {
			return STATE.Solve;
		}
	}

	@Override
	public int loop() {
		if (!activateCondition()) {
			return -1;
		}
		if (player.getMine().isMoving()) {
			return random(1000, 2000);
		}
		switch (getState()) {
		case Exit:
			final RSObject portal = objects.getNearestByID(ExitPortal);
			if (portal != null) {
				if (!portal.isOnScreen()) {
					camera.turnTo(portal);
				}
				if (objects.at(portal, "Enter")) {
					return random(1000, 1300);
				}
			}
			break;

		case OpenChest:
			final RSObject chest = objects.getNearestByID(ArnavChest);
			if (chest != null) {
				if (objects.at(chest, "Open")) {
					return random(1000, 1300);
				}
			}
			break;

		case Talk:
			if (iface.canContinue()) {
				iface.clickContinue();
				return random(1500, 2000);
			}
			final RSInterfaceChild okay = iface.getChild(TalkIFace, 3);
			if (okay != null && okay.isValid()) {
				iface.clickChild(okay);
			}
			return random(1500, 2000);

		case Solve:
			final RSInterface solver = iface.get(ChestIFaceParent);
			if (solver != null && solver.isValid()) {
				final String s = solver.getChild(32).getText();
				if (s.contains("Bowl")) {
					index = 0;
				} else if (s.contains("Ring")) {
					index = 1;
				} else if (s.contains("Coin")) {
					index = 2;
				} else if (s.contains("Bar")) {
					index = 3;
				}
				if (solved()) {
					iface.clickChild(ChestIFaceParent, ChestIFaceUnlock);
					return random(600, 900);
				}
				final RSInterfaceChild container = solver.getChild(ChestIFaceCenter);
				for (int i = 0; i < 3; i++) {
					int rand = random(0, 100);
					if (rand < 50) {
						rand = 0;
					} else if (rand >= 50) {
						rand = 1;
					}
					final RSInterfaceChild target = solver
					.getChild(IFaceSolveIDs[index][i]);
					final RSInterfaceChild arrow = solver.getChild(Arrows[i][rand]);
					while (container.isValid()
							&& target.isValid()
							&& !container.getArea().contains(
									new Point(target.getCenter().x + 15, target
											.getCenter().y)) && arrow.isValid()
											&& new Timer(10000).isRunning()) {
						iface.clickChild(arrow);
						sleep(random(1000, 1200));
					}
				}
			}
		}
		return random(500, 800);
	}

	@Override
	public void onFinish() {
		index = -1;
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.cap, 9, 330);
		ScreenMouse.paint(render);
	}

	private boolean solved() {
		if (index == -1) {
			return false;
		}
		final RSInterface solver = iface.get(ChestIFaceParent);
		if (solver != null && solver.isValid()) {
			final RSInterfaceChild container = solver.getChild(ChestIFaceCenter);

			final Point p1 = solver.getChild(IFaceSolveIDs[index][0]).getCenter();
			p1.setLocation(p1.x + 15, p1.y);
			final Point p2 = solver.getChild(IFaceSolveIDs[index][1]).getCenter();
			p2.setLocation(p2.x + 15, p1.y);
			final Point p3 = solver.getChild(IFaceSolveIDs[index][2]).getCenter();
			p3.setLocation(p3.x + 15, p1.y);
			return (container.getArea().contains(p1)
					&& container.getArea().contains(p2) && container.getArea()
					.contains(p3));
		}
		return false;
	}
}
