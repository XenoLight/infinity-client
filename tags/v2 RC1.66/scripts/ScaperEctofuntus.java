import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.io.ScreenshotUtil;

@ScriptManifest(authors = { "Scaper" }, category = "Beta", name = "EctoFuntus", version = 1.0, description = "<html>"
		+ "Does EctoFuntus" + "</html>")
public class ScaperEctofuntus extends Script implements PaintListener,
		MessageListener {

	private Bot bot;
	int NBones = 526, BBones = 532, DBones = 536, OBones = 4834,
			Ectophial = 4251, Stairs = 37454, Stairs2 = 5281, Stairs3 = 0,
			Stairs4 = 0, Stairs5 = 0, Bucket = 1925, TrapDoorClosed = 5267,
			TrapDoorOpen = 5268, Pot = 1931, Barrier = 5259,
			PotOfBoneMeal = 4255, BucketOfSlime = 4286, StartPrayExP = 0,
			Trips = 0, Prays = 0, EctoToken = 4278, EctofuntusAltar = 5282,
			BoneGrinder = 11162, PoolOfSlime = 0000, GhostCollector = 1686,
			BankBooth = 5276;
	int AgilLvl;
	String StatusMsg = "";
	long startTime = 0;
	RSTile[] BankToBarrier = new RSTile[] { new RSTile(3688, 3466),
			new RSTile(3690, 3471), new RSTile(3685, 3473),
			new RSTile(3680, 3474), new RSTile(3675, 3474),
			new RSTile(3670, 3474), new RSTile(3666, 3477),
			new RSTile(3666, 3482), new RSTile(3666, 3487),
			new RSTile(3663, 3492), new RSTile(3662, 3497),
			new RSTile(3662, 3502), new RSTile(3659, 3507), };
	RSTile[] BarrierToStairs = new RSTile[] { new RSTile(3659, 3509),
			new RSTile(3660, 3514), new RSTile(3665, 3516), };
	RSTile[] StairsToBoneGrinder = new RSTile[] { new RSTile(3667, 3522),
			new RSTile(3662, 3524), };
	RSTile[] BoneGrinderToStairs = new RSTile[] { new RSTile(3660, 3524),
			new RSTile(3665, 3524), new RSTile(3666, 3523) };
	RSTile[] StairsToTrapDoor = new RSTile[] { new RSTile(3666, 3517),
			new RSTile(3661, 3516), new RSTile(3654, 3519), };
	RSTile[] TrapDoorLadderToSlimePoolLevel3 = new RSTile[] {
			new RSTile(3669, 9888), new RSTile(3669, 9883),
			new RSTile(3672, 9878), new RSTile(3676, 9875),
			new RSTile(3681, 9875), new RSTile(3686, 9876),
			new RSTile(3689, 9880), new RSTile(3691, 9885), };
	RSTile[] Level3ToSlimePoolLevel2 = new RSTile[] { new RSTile(3688, 9888),
			new RSTile(3688, 9883), new RSTile(3685, 9879),
			new RSTile(3680, 9877), new RSTile(3675, 9878),
			new RSTile(3672, 9882), new RSTile(3671, 9887), };
	RSTile[] Level2ToSlimePool = new RSTile[] { new RSTile(3675, 9888),
			new RSTile(3674, 9883), new RSTile(3678, 9879),
			new RSTile(3683, 9879), new RSTile(3686, 9883),
			new RSTile(3687, 9888), };
	RSTile[] SlimePoolToLevel2 = new RSTile[] { new RSTile(3687, 9888),
			new RSTile(3686, 9893), new RSTile(3682, 9897),
			new RSTile(3677, 9896), new RSTile(3675, 9888), };
	RSTile[] Level2ToLevel3 = new RSTile[] { new RSTile(3671, 9886),
			new RSTile(3673, 9881), new RSTile(3677, 9877),
			new RSTile(3682, 9877), new RSTile(3686, 9880),
			new RSTile(3688, 9887), };
	RSTile[] Level3ToTrapDoorLadder = new RSTile[] { new RSTile(3692, 9887),
			new RSTile(3692, 9882), new RSTile(3688, 9878),
			new RSTile(3684, 9875), new RSTile(3679, 9875),
			new RSTile(3674, 9876), new RSTile(3671, 9880),
			new RSTile(3669, 9888), };
	RSTile BankRSTile = new RSTile(3688, 3466);
	RSTile[] EctophialTeleportToStairs = new RSTile[] { new RSTile(3658, 3522),
			new RSTile(3662, 3519), new RSTile(3667, 3517), };
	RSTile[] EctophialTeleportToTrapDoor = new RSTile[] {
			new RSTile(3659, 3522), new RSTile(3654, 3519), };
	RSTile[] BarrierToBank = walk.reversePath(BankToBarrier);
	RSTile[] EctofuntusTeleportAltarToBarrier = new RSTile[] {
			new RSTile(3659, 3522), new RSTile(3656, 3518),
			new RSTile(3659, 3513), new RSTile(3660, 3509) };
	RSTile AgilRSTile = new RSTile(3670, 9888);
	private static final int[] expTable = { 0, 0, 83, 174, 276, 388, 512, 650,
			801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523,
			3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031,
			13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408,
			33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127,
			83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636,
			184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599,
			407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445,
			899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200,
			1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594,
			3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253,
			7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431 };

	public int getPercentageToNextLevel(int skill) {
		int level = skills.getCurrentLvl(skill);
		if (level == -1) {
			return -1;
		}
		if (level == 99) {
			return 0;
		}
		int experience = skills.getCurrentXP(skill);
		int needed = expTable[level + 1];
		int lastLvl = expTable[level];
		double notYet = (double) (experience - lastLvl)
				/ (double) (needed - lastLvl) * (double) 100;
		return (int) notYet;

	}

	public boolean onStart(Map<String, String> args) {
		try {
			StartPrayExP = skills.getCurrentXP(Constants.STAT_PRAYER);
			AgilLvl = skills.getCurrentLvl(Constants.STAT_AGILITY);
		} catch (Exception e) {
			log(e.getMessage());
			e.printStackTrace();
		}
		return true;
	}

	public Point getRandomPointInside() {
		Rectangle rect = iface.getChild(750, 1).getArea();
		int randomX = random(rect.x, rect.x + rect.width);
		int randomY = random(rect.y, rect.y + rect.height);
		return new Point(randomX, randomY);
	}

	public int loop() {
		if (player.getMyEnergy() > 40) {
			game.setRun(true);
		}
		if (player.getMyEnergy() < 10 && !bank.isOpen() && iface.get(620) == null) {
			if (iface.getChild(750, 1).isValid() && iface.getChild(750, 1) != null) {
				iface.clickChild(750, 1);
				wait(100);
				if (menu.action("Rest")) {
					StatusMsg = "Resting";
				}
				if (calculate.distanceTo(BankRSTile) < 4) {
					if (!bank.isOpen()) {
						final RSObject BANK_BOOTH = objects.getNearestByID(BankBooth);
						if (BANK_BOOTH != null) {
							BANK_BOOTH.action("quickly");
						}
					} else {
						if (bank.getItem(NBones) == null) {
							if (bank.getItem(BBones) != null) {
								if (bank.getItem(DBones) != null) {
									if (bank.getItem(OBones) != null) {
									}
								}
								log("No more Bones...");
								bank.close();
								game.logout();
								stopScript();
							} else {
								bank.withdraw(NBones, 9);
								if (bank.withdraw(BBones, 9))
									;
								if (bank.withdraw(DBones, 9))
									;
								if (bank.withdraw(OBones, 9))
									;
								StatusMsg = "Banking for more Bones...";
								bank.close();
								Trips++;
								inventory.clickItem(Ectophial, "Empty");
								StatusMsg = "Teleporting...";
								if (player.getMine().getAnimation() != -1) {
									return 100;
								}
								walk.pathMM(walk.fixPath(EctophialTeleportToStairs));
								RSObject stairway = objects.getNearestByID(Stairs);
								if (stairway != null) {
									stairway.action("Climb-up Staircase");
								}
								walk.pathMM(walk.fixPath(StairsToBoneGrinder));
								RSObject Bonegrinder = objects.getNearestByID(BoneGrinder);
								if (Bonegrinder != null) {
									Bonegrinder.click(false);
								}
								if (player.getMine().getAnimation() != -1) {
									return 100;
								}
								StatusMsg = "Grinding teh bones";
								inventory.clickItem(Ectophial, "Empty");
								if (player.getMine().getAnimation() != -1) {
									return 100;
								}
								StatusMsg = "Teleporting...Again..";
								walk.pathMM(walk.fixPath(EctophialTeleportToTrapDoor));
								RSObject TrapDoorC = objects.getNearestByID(TrapDoorClosed);
								if (TrapDoorC != null) {
									TrapDoorC.click(false);
									StatusMsg = "Opening Trap door....";
								}
								RSObject TrapDoorO = objects.getNearestByID(TrapDoorOpen);
								if (TrapDoorO != null) {
									TrapDoorO.click(false);
									StatusMsg = "Going down Ladder....";
									if (AgilLvl > 58)
										;

								} else {
									walk.pathMM(walk.fixPath(TrapDoorLadderToSlimePoolLevel3));
									RSObject Stairway3 = objects.getNearestByID(Stairs3);
									if (Stairway3 != null) {
										Stairway3.click(false);
										StatusMsg = "Going down Level 3 Stairs...";
									}
									walk.pathMM(walk.fixPath(Level3ToSlimePoolLevel2));
									RSObject Stairway4 = objects.getNearestByID(Stairs4);
									if (Stairway4 != null) {
										Stairway4.click(false);
										StatusMsg = "Going down Level 2 Stairs....";
									}
									walk.pathMM(walk.fixPath(Level2ToSlimePool));
									RSObject Stairway5 = objects.getNearestByID(Stairs5);
									if (Stairway5 != null) {
										Stairway5.click(false);
										StatusMsg = "Going down to Slime pool.....";
									}
								}

							}
						} else {
							final RSObject ECTOPHIAL = objects.getNearestByID(Ectophial);
							if (ECTOPHIAL == null)
								;
							walk.pathMM(walk.fixPath(BankToBarrier));
							StatusMsg = "Walking to Barrier....";
							RSObject Barrierz = objects.getNearestByID(Barrier);
							if (Barrierz != null) {
								Barrierz.click(false);
							}
							walk.pathMM(walk.fixPath(BarrierToStairs));
							RSObject stairway = objects.getNearestByID(Stairs);
							if (stairway != null) {
								stairway.action("Climb-up Staircase");
							}
							walk.pathMM(walk.fixPath(StairsToBoneGrinder));
							RSObject Bonegrinder = objects.getNearestByID(BoneGrinder);
							if (Bonegrinder != null) {
								Bonegrinder.click(false);
							}
							if (player.getMine().getAnimation() != -1) {
								return 100;
							}
							StatusMsg = "Grinding teh bones";
							walk.pathMM(walk.fixPath(BoneGrinderToStairs));
							StatusMsg = "Walking back to stairs, cause you be a lazy nub!";
							RSObject stairway2 = objects.getNearestByID(Stairs2);
							if (stairway2 != null) {
								stairway2.action("Climb-down Staircase");
							}
							walk.pathMM(walk.fixPath(StairsToTrapDoor));
							RSObject TrapDoorC = objects.getNearestByID(TrapDoorClosed);
							if (TrapDoorC != null) {
								TrapDoorC.click(false);
								StatusMsg = "Opening Trap door....";
							}
							RSObject TrapDoorO = objects.getNearestByID(TrapDoorOpen);
							if (TrapDoorO != null) {
								TrapDoorO.click(false);
								StatusMsg = "Going down Ladder....";
							}

						}
						return 50;
					}
					return 50;
				}
				return 50;
			}
		}
		return 50;
	}

	public void messageReceived(MessageEvent arg0) {
		final String e = arg0.getMessage();
		if (e.contains("You put some ectoplasm and bonemeal into the Ectofuntus, and worship it."))
			;
		Prays++;
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(bot, true);
	}

	public void onRepaint(Graphics g) {
		long runTime = System.currentTimeMillis() - startTime;

		int secs = ((int) ((runTime / 1000) % 60));
		int mins = ((int) (((runTime / 1000) / 60) % 60));
		int hours = ((int) ((((runTime / 1000) / 60) / 60) % 60));

		int x = 9;
		int y = 308;
		g.setColor(new Color(255, 0, 0, 70));
		g.fill3DRect(x, y, 145, 25, true);
		x += 5;
		y += 15;
		g.setColor(Color.BLACK);
		g.drawString("Status: " + StatusMsg, x, y);
		x = 12;
		y = 70;
		g.setColor(new Color(0, 0, 255, 70));
		g.fill3DRect(x, y, 205, 40, true);
		x += 5;
		y += 15;
		g.setColor(Color.BLACK);
		g.drawString("Welcome to Scaper's Ectofuntus Script! <3", x, y);
		y += 15;
		g.drawString("Run time: " + (hours < 10 ? "0" : "") + hours + ":"
				+ (mins < 10 ? "0" : "") + mins + ":" + (secs < 10 ? "0" : "")
				+ secs, x, y);
		x = 305;
		y = 235;
		g.setColor(new Color(170, 0, 200, 70));
		g.fill3DRect(x, y, 205, 85, true);
		x += 5;
		y += 15;
		g.setColor(Color.BLACK);
		g.drawString("Prayer Level: ", x, y);
		y += 15;
		g.drawString("Prayer Experience Gained: ", x, y);
		y += 15;
		g.drawString("Times Prayed: " + Prays, x, y);
		y += 15;
		g.drawString("Trips: " + Trips, x, y);
		y += 15;

	}
}
