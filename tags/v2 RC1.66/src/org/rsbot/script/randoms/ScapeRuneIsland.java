package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 2.4 - 02/24/11
 */
@ScriptManifest(authors = "Secret Spy", name = "ScapeRuneIsland", version = 2.4)
public class ScapeRuneIsland extends Random implements PaintListener {

	public int[] StatueIDs = { 8992, 8993, 8990, 8991 };
	public RSTile CenterTile = new RSTile(3421, 4777);
	public RSObject Direction;
	public boolean Finished;
	public boolean Fishing;
	public boolean ForceTalk;

	@Override
	public boolean activateCondition() {
		return CenterTile.distanceTo() < 50;
	}

	public int getBoxCount(final int... ids) {
		if (!iface.get(11).isValid()) {
			return -1;
		}
		int count = 0;
		for (int i = 0; i < 28; ++i) {
			for (final int id : ids) {
				if (iface.get(11).getChild(17).isValid()
						&& iface.get(11).getChild(17).getChildID() == id) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public int loop() {
		camera.setAltitude(true);
		if (!activateCondition()) {
			return -1;
		}
		final RSObject Statue1 = objects.getNearestByID(StatueIDs[0]);
		final RSObject Statue2 = objects.getNearestByID(StatueIDs[1]);
		final RSObject Statue3 = objects.getNearestByID(StatueIDs[2]);
		final RSObject Statue4 = objects.getNearestByID(StatueIDs[3]);
		if (player.getMine().isMoving()
				|| player.getMine().getAnimation() == 620) {
			return random(550, 700);
		}
		if (iface.get(241).getChild(4).isValid()
				&& iface.get(241).getChild(4).getText().contains("catnap")) {
			Finished = true;
		}
		if (iface.get(64).getChild(4).isValid()
				&& iface.get(64).getChild(4).getText()
				.contains("fallen asleep")) {
			Finished = true;
		}
		if (iface.get(242).getChild(4).isValid()
				&& iface.get(242).getChild(4).getText()
				.contains("Wait! Before")) {
			ForceTalk = true;
		}
		if (iface.canContinue()) {
			if (iface.clickContinue()) {
				return random(500, 1000);
			}
		}
		if (ForceTalk) {
			RSNPC Servant = npc.getNearestByID(2481);
			if (Servant != null && Direction == null && settings.get(344) == 0) {
				if (!tile.onScreen(Servant.getLocation())) {
					walk.tileMM(walk.getClosestTileOnMap(Servant.getLocation()));
					return 700;
				}
				if (npc.action(Servant, "Talk-to")) {
					ForceTalk = false;
				}
				return random(1000, 2000);
			}
			if (Servant == null) {
				Servant = npc.getNearestByID(2481);
				if (Servant == null) {
					walk.tileMM(walk.getClosestTileOnMap(CenterTile));
					return random(1000, 2000);
				}
				return random(50, 100);
			}
		}
		if (Finished) {
			final RSObject Portal = objects.getNearestByID(8987);
			if (Portal != null) {
				if (!tile.onScreen(Portal.getLocation())) {
					walk.tileMM(walk.getClosestTileOnMap(Portal.getLocation()));
					return random(500, 1000);
				} else {
					if (objects.at(Portal, "Enter")) {
						return random(3000, 5000);
					}
					return random(500, 1000);
				}
			} else {
				walk.tileMM(walk.getClosestTileOnMap(CenterTile));
			}
		}
		if (iface.get(11).isValid()
				&& getBoxCount() - getBoxCount(6209, 6202, 6200) >= 27) {
			final RSInterfaceChild RandomItem = iface.getChild(11, (random(16, 26)));
			final int RandomID = RandomItem.getChildID();
			if (RandomID < 0) {
				return random(50, 100);
			}
			log("Item with ID " + RandomID + " was deposited.");
			if (iface.clickChild(11, (random(16, 26)), "Dep")) {
				return random(500, 1000);
			}
			return random(50, 100);
		}
		if (iface.get(11).isValid()
				&& getBoxCount() - getBoxCount(6209, 6202, 6200) < 27) {
			bank.close();
			return random(500, 1000);
		}
		if (inventory.getCountExcept(6209, 6202, 6200) >= 27) {
			final RSObject Box = objects.getNearestByID(32930);
			if (!tile.onScreen(Box.getLocation())) {
				walk.tileMM(walk.getClosestTileOnMap(Box.getLocation()));
				return random(1000, 2000);
			} else {
				log("Depositing item(s) to make room.");
				if (objects.at(Box, "Deposit")) {
					wait(random(1800, 2000));
					mouse.click(410 + random(2, 4), 235 + random(2, 1), false);
					menu.action("Deposit-1");
					wait(random(1200, 1400));
					mouse.click(435 + random(2, 4), 40 + random(2, 1), true);
					return random(800, 1200);
				}
				return random(500, 1000);
			}
		}
		if (inventory.getCount(6202) > 0) {
			final RSObject Pot = objects.getNearestByID(8985);
			if (Pot != null) {
				if (!tile.onScreen(Pot.getLocation())) {
					walk.tileMM(walk.getClosestTileOnMap(Pot.getLocation()));
					return random(400, 800);
				}
				inventory.clickItem(6202, "Use");
				sleep(random(800, 1000));
				if (objects.at(Pot, "Use")) {
					sleep(1000);
				}
				return random(2000, 2400);
			} else {
				walk.tileMM(walk.getClosestTileOnMap(CenterTile));
			}
		}
		if (Fishing && inventory.getCount(6209) == 0) {
			final RSGroundItem Net = ground.getItemByID(6209);
			if (Net != null) {
				if (!Net.isOnScreen()) {
					walk.tileMM(walk.getClosestTileOnMap(Net));
					return random(800, 1000);
				} else {
					Net.action("Take");
					return random(800, 1000);
				}
			} else {
				walk.tileMM(walk.getClosestTileOnMap(CenterTile));
			}
		}
		if (iface.get(246).getChild(5).containsText("contains")
				&& settings.get(334) == 1 && Direction == null) {
			sleep(2000);
			if (tile.onScreen(Statue1.getLocation())) {
				Direction = Statue1;
				Fishing = true;
			}
			if (tile.onScreen(Statue2.getLocation())) {
				Direction = Statue2;
				Fishing = true;
			}
			if (tile.onScreen(Statue3.getLocation())) {
				Direction = Statue3;
				Fishing = true;
			}
			if (tile.onScreen(Statue4.getLocation())) {
				Direction = Statue4;
				Fishing = true;
			}
			log("Checking direction");
			return random(2000, 3000);
		}
		if (Direction != null && inventory.getCount(6200) < 1) {
			wait(random(1000, 1200));
			if (!tile.onScreen(Direction.getLocation())) {
				walk.tileMM(walk.getClosestTileOnMap(Direction.getLocation()));
				return random(400, 600);
			}
			final RSObject Spot = objects.getNearestByID(8986);
			if (Spot != null) {
				if (!tile.onScreen(Spot.getLocation())) {
					camera.turnTo(Spot.getLocation());
				}
				if (!tile.onScreen(Spot.getLocation())
						&& walk.tileMM(Spot.getLocation())) {
					sleep(random(1000, 2000));
					if (!tile.onScreen(Spot.getLocation())) {
						sleep(1000);
					}
				}
				tile.click(Spot.getLocation(), "Net");
				return random(2000, 2500);
			} else {
				walk.tileMM(walk.getClosestTileOnMap(CenterTile));
			}
		}
		if (inventory.getCount(6200) > 0) {
			final RSNPC Cat = npc.getNearestByID(2479);
			if (Cat != null) {
				if (!tile.onScreen(Cat.getLocation())) {
					walk.tileMM(walk.getClosestTileOnMap(Cat.getLocation()));
				}
				inventory.clickItem(6200, "Use");
				sleep(random(500, 1000));
				npc.action(Cat, "Use Raw fish-like thing -> Evil bob");
			} else {
				walk.tileMM(walk.getClosestTileOnMap(CenterTile));
			}
			return random(1900, 2200);
		}
		RSNPC Servant = npc.getNearestByID(2481);
		if (Servant != null && Direction == null && settings.get(344) == 0) {
			if (!tile.onScreen(Servant.getLocation())) {
				walk.tileMM(walk.getClosestTileOnMap(Servant.getLocation()));
				return 700;
			}
			npc.action(Servant, "Talk-to");
			return random(1000, 2000);
		}
		if (Servant == null) {
			Servant = npc.getNearestByID(2481);
			if (Servant == null) {
				walk.tileMM(walk.getClosestTileOnMap(CenterTile));
				return random(1000, 2000);
			}
			return random(50, 100);
		}
		log("Setting 344: " + settings.get(344));
		return random(800, 1200);
	}

	@Override
	public void onFinish() {
		Direction = null;
		Finished = false;
		Fishing = false;
	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.scape, 9, 330);
		ScreenMouse.paint(render);
	}
}
