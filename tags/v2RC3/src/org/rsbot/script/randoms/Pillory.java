package org.rsbot.script.randoms;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

/**
 * @author Secret Spy
 * @version 3.9 - 02/11/11
 */
@ScriptManifest(authors = { "Secret Spy" }, name = "Pillory", version = 3.9)
public class Pillory extends Random implements MessageListener, PaintListener {

	public int fail = 0;
	private final int GameInterface = 189;
	private final String pilloryMessage = "Solve the Pillory";
	public boolean inCage = false;
	public RSTile myLoc;
	public int getBaseX;
	public int getBaseY;
	public static final int TYPE_INTERACTABLE = 1;
	public static final int TYPE_FLOOR_DECORATION = 2;
	public static final int TYPE_BOUNDARY = 4;
	public static final int TYPE_WALL_DECORATION = 8;
	public RSTile South = new RSTile(2606, 3105);
	RSTile[] cagetiles = { new RSTile(2608, 3105), new RSTile(2606, 3105),
			new RSTile(2604, 3105), new RSTile(3226, 3407),
			new RSTile(3228, 3407), new RSTile(3230, 3407),
			new RSTile(2685, 3489), new RSTile(2683, 3489),
			new RSTile(2681, 3489) };

	@Override
	public boolean activateCondition() {
		if (!game.isLoggedIn()) {
			return false;
		}
		for (final RSTile cagetile : cagetiles) {
			if (player.getMine().getLocation().equals(cagetile)) {
				return true;
			}
		}
		if (!inCage) {
			inCage = iface.getChild(372, 3).getText()
			.contains("Solve the pillory");
		}
		if (!inCage) {
			inCage = iface.getChild(372, 3).getText().contains("swinging");
		}
		return inCage;
	}

	public RSObject findMYObject(final int... ids) {
		RSObject cur = null;
		int dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final RSObject o = objects.getTopAt(x
						+ Bot.getClient().getBaseX(), y
						+ Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (final int id : ids) {
						if (o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						final int distTmp = o.getLocation().distanceTo();
						if (distTmp != -1) {
							if (cur == null) {
								dist = distTmp;
								cur = o;
							} else if (distTmp < dist) {
								cur = o;
								dist = distTmp;
							}
						}
					}
				}
			}
		}
		return cur;
	}

	private int getKey() {
		int key = 0;
		log.config("\t  Key needed :");
		switch (iface.get(GameInterface).getChild(4).getModelID()) {
		case 9753:
			key = 9749;
			log.info("\t   Diamond");
			break;
		case 9754:
			key = 9750;
			log.info("\t   Square");
			break;
		case 9755:
			key = 9751;
			log.info("\t   Circle");
			break;
		case 9756:
			key = 9752;
			log.info("\t   Triangle");
			break;
		}
		if (iface.get(GameInterface).getChild(5).getModelID() == key) {
			return 1;
		}
		if (iface.get(GameInterface).getChild(6).getModelID() == key) {
			return 2;
		}
		if (iface.get(GameInterface).getChild(7).getModelID() == key) {
			return 3;
		}
		return -1;
	}

	@Override
	public int loop() {
		camera.setAltitude(true);
		if (South.distanceTo() <= 10) {
			camera.setRotation(180);
		} else {
			camera.setRotation(360);
		}
		if (fail > 20) {
			stopScript(false);
		}
		if (myLoc == null) {
			myLoc = player.getMine().getLocation();
			return random(1000, 2000);
		}
		if (!player.getMine().getLocation().equals(myLoc)) {
			log.config(ScreenLog.pillSol);
			myLoc = null;
			inCage = false;
			return -1;
		}
		if (!iface.get(GameInterface).isValid()) {
			final Point ObjectPoint = new Point(
					Calculations.tileToScreen(myLoc));
			final Point Lock = new Point((int) ObjectPoint.getX() + 10,
					(int) ObjectPoint.getY() - 30);
			mouse.click(Lock.x, Lock.y + random(0, 15), false);
			sleep(random(600, 800));
			if (menu.action("unlock")) {
				log.config(ScreenLog.pillSuc);
				return random(1000, 2000);
			} else {
				fail++;
			}
		}
		if (iface.get(GameInterface).isValid()) {
			final int key = getKey();
			log.config(String.valueOf(key));
			switch (key) {
			case 1:
				mouse.click(iface.get(GameInterface).getChild(5).getArea()
						.getLocation().x
						+ random(10, 13), iface.get(GameInterface).getChild(5)
						.getArea().getLocation().y
						+ random(46, 65), true);
				break;
			case 2:
				mouse.click(iface.get(GameInterface).getChild(6).getArea()
						.getLocation().x
						+ random(10, 13), iface.get(GameInterface).getChild(6)
						.getArea().getLocation().y
						+ random(46, 65), true);
				break;
			case 3:
				mouse.click(iface.get(GameInterface).getChild(7).getArea()
						.getLocation().x
						+ random(10, 13), iface.get(GameInterface).getChild(7)
						.getArea().getLocation().y
						+ random(46, 65), true);
				break;
			default:
				log.warning(ScreenLog.pillBad);
				fail++;
				break;
			}
			return random(1000, 1600);
		}
		return -1;
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		final String str = e.getMessage();
		if (str != null && str.contains(pilloryMessage)) {
			inCage = true;
		}

	}

	@Override
	public void onRepaint(final Graphics render) {
		render.setColor(Color.BLUE);
		render.drawString(ScreenLog.pill, 9, 330);
		ScreenMouse.paint(render);
	}
}
