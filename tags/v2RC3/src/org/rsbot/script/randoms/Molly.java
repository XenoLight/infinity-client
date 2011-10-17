package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 2.1 - 04/16/11
 */
@ScriptManifest(authors = { "PwnZ, Secret Spy" }, name = "Molly", version = 2.1)
public class Molly extends Random implements PaintListener {

	static final int ClawID = 14976;
	static final int ControlPanelID = 14978;
	static final int DoorID = 14982;
	static final int MollyChatboxIFaceGroup = 228;
	static final int MollyChatBoxNoThanks = 3;
	static final int MollyChatBoxSkip = 2;
	static final int ControlIFaceGroup = 240;
	static final int ControlsGrab = 28;
	static final int ControlsUp = 29;
	static final int ControlsDown = 30;
	static final int ControlsLeft = 31;
	static final int ControlsRight = 32;
	static final int ControlsExit = 33;
	private RSNPC molly;
	private RSObject controlPanel;
	private int MollyID = -1;
	private boolean CameraSet;
	private boolean TalkedToMolly;
	private boolean Finished;
	private long DelayTime;

	@Override
	public boolean activateCondition() {
		molly = npc.getNearestByName("Molly");
		controlPanel = objects.getNearestByID(Molly.ControlPanelID);
		return (molly != null && molly.isInteractingWithLocalPlayer())
		|| (controlPanel != null);
	}

	private boolean hasClawMoved(final RSTile prevClawLoc) {
		final RSObject claw = objects.getNearestByID(Molly.ClawID);
		if (claw == null) {
			return false;
		}
		final RSTile currentClawLoc = claw.getLocation();
		return (currentClawLoc.getX() - prevClawLoc.getX() != 0)
		|| (currentClawLoc.getY() - prevClawLoc.getY() != 0);
	}

	private boolean inControlInterface() {
		final RSInterface i = iface.get(Molly.ControlIFaceGroup);
		return (i != null) && i.isValid();
	}

	private boolean inControlRoom() {
		final RSObject o = objects.getNearestByID(DoorID);
		return (o != null)
		&& (player.getMine().getLocation().getX() > o.getLocation()
				.getX());
	}

	@Override
	public int loop() {
		if (!activateCondition()) {
			log(ScreenLog.mollyFinish);
			sleep(500);
			if (!activateCondition()) {
				return -1;
			}
		}
		controlPanel = objects.getNearestByID(Molly.ControlPanelID);
		while (player.getMine().isMoving()
				|| (player.getMine().getAnimation() != -1)) {
			sleep(random(800, 1300));
		}
		if (iface.canContinue()) {
			iface.clickContinue();
			return random(250, 750);
		}
		if (MollyID == -1) {
			MollyID = molly.getID();
			log(ScreenLog.mollyMID + Integer.toString(MollyID));
			log(ScreenLog.mollyETID + Integer.toString(MollyID - 40));
		}
		if (iface.canContinue()) {
			setCamera();
			iface.clickContinue();
			return random(500, 800);
		}
		final RSInterfaceChild skipInterface = iface.get(
				Molly.MollyChatboxIFaceGroup).getChild(Molly.MollyChatBoxSkip);
		if ((skipInterface != null) && skipInterface.isValid()
				&& skipInterface.getAbsoluteY() > 5
				&& skipInterface.containsText("Yes, I")) {
			iface.clickChild(skipInterface);
			return random(600, 1000);
		}
		final RSInterfaceChild noThanksInterface = iface.get(
				Molly.MollyChatboxIFaceGroup).getChild(
						Molly.MollyChatBoxNoThanks);
		if ((noThanksInterface != null) && noThanksInterface.isValid()
				&& noThanksInterface.getAbsoluteY() > 5) {
			setCamera();
			sleep(random(800, 1200));
			iface.clickChild(noThanksInterface);
			TalkedToMolly = true;
			return random(600, 1000);
		}
		if (!CameraSet) {
			camera.setAltitude(true);
			CameraSet = true;
			return (random(300, 500));
		}
		if (Finished && !inControlRoom()) {
			if (!tile.onScreen(molly.getLocation())) {
				walk.tileOnScreen(molly.getLocation());
				return random(1000, 2000);
			}
			npc.action(molly, "Talk");
			return (random(1000, 1200));
		}
		if (Finished && inControlRoom()) {
			if (!openDoor()) {
				return (random(1000, 1500));
			}
			return (random(400, 600));
		}
		if (!inControlRoom()) {
			if (TalkedToMolly
					&& !Finished
					&& (iface.get(Molly.MollyChatboxIFaceGroup) == null || iface
							.get(Molly.MollyChatboxIFaceGroup).getChild(0)
							.getAbsoluteY() < 2)
							&& (iface.get(Molly.MollyChatBoxNoThanks) == null || iface
									.get(Molly.MollyChatBoxNoThanks).getChild(0)
									.getAbsoluteY() < 2)) {
				openDoor();
				sleep(random(800, 1200));
			} else {
				npc.action(molly, "Talk");
				TalkedToMolly = true;
				sleep(random(800, 1200));
			}
		} else {
			if (npc.getNearestByName("Molly") != null) {
				Finished = true;
				sleep(random(800, 1200));
			} else {
				if (!inControlInterface()) {
					if (tile.onScreen(controlPanel.getLocation())) {
						objects.at(controlPanel, "Use");
						sleep(random(1200, 2000));
					} else {
						walk.tileOnScreen(controlPanel.getLocation());
						camera.setAltitude(true);
						camera.turnTo(controlPanel);
					}
				} else {
					navigateClaw();
					DelayTime = System.currentTimeMillis();
					while (!iface.canContinue()
							&& (System.currentTimeMillis() - DelayTime < 15000)) {
					}
					if (iface.canContinue()) {
						iface.clickContinue();
					}
					sleep(random(300, 400));
				}
			}
		}
		return random(200, 400);
	}

	private void navigateClaw() {
		if (!inControlInterface() || (MollyID < 1)) {
			return;
		}
		RSObject claw;
		RSNPC suspect;
		while ((claw = objects.getNearestByID(Molly.ClawID)) != null
				&& (suspect = npc.getNearestByID(MollyID - 40)) != null) {
			final RSTile clawLoc = claw.getLocation();
			final RSTile susLoc = suspect.getLocation();
			final ArrayList<Integer> options = new ArrayList<Integer>();
			if (susLoc.getX() > clawLoc.getX()) {
				options.add(Molly.ControlsLeft);
			}
			if (susLoc.getX() < clawLoc.getX()) {
				options.add(Molly.ControlsRight);
			}
			if (susLoc.getY() > clawLoc.getY()) {
				options.add(Molly.ControlsDown);
			}
			if (susLoc.getY() < clawLoc.getY()) {
				options.add(Molly.ControlsUp);
			}
			if (options.isEmpty()) {
				options.add(Molly.ControlsGrab);
			}
			final RSInterface i = iface.get(Molly.ControlIFaceGroup);
			if ((i != null) && i.isValid()) {
				iface.clickChild(Molly.ControlIFaceGroup,
						options.get(random(0, options.size())));
			}
			DelayTime = System.currentTimeMillis();
			while (!hasClawMoved(clawLoc)
					&& (System.currentTimeMillis() - DelayTime < 3500)) {
				sleep(10);
			}
		}
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.molly, 9, 330);
		ScreenMouse.paint(render);
	}

	private boolean openDoor() {
		final RSObject door = objects.getNearestByID(Molly.DoorID);
		if (door == null) {
			return false;
		}
		if (!tile.onScreen(door.getLocation())) {
			walk.tileOnScreen(door.getLocation());
			sleep(1000, 2000);
			return false;
		}
		door.action("Open");
		return false;
	}

	private void setCamera() {
		if ((random(0, 6) == 3) && !CameraSet) {
			camera.setAltitude(true);
			CameraSet = true;
		}
	}
}
