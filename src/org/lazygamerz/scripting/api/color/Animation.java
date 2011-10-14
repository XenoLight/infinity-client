package org.lazygamerz.scripting.api.color;

import java.awt.Color;
import java.awt.Rectangle;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;

public class Animation {

	Methods methods;
	private final Rectangle bounds = new Rectangle(methods.random(240, 235), 120, 50,
			80);
	private boolean blocked = false;
	private final Color[][] map = new Color[762][503];
	private double ratio = 20.0D;

	public Animation(final Methods methods) {
		this.methods = methods;
	}

	public void block() {
		blocked = true;
		populateMap();
		methods.sleep(600);
		while (blocked) {
			methods.sleep(400);
			int count = bounds.width * bounds.height;
			for (int x = bounds.x; x < bounds.x + bounds.width; ++x) {
				for (int y = bounds.y; y < bounds.y + bounds.height; ++y) {
					final Color current = new Color(Bot.getImage().getRGB(x, y));
					if (map[x][y].getRGB() != current.getRGB()) {
						--count;
					}
				}
			}
			if ((count >= bounds.width * bounds.height
					- (bounds.width * bounds.height / ratio))
					&& (count < bounds.width * bounds.height)) {
				blocked = false;
			}
			populateMap();
		}
	}

	public void filterPixel(final double ratio) {
		this.ratio = ratio;
	}

	public boolean isBlocked() {
		return blocked;
	}

	public boolean isMoving() {
		final int x = bounds.x;
		final int y = bounds.y;
		boolean isMoving1 = false;
		populateMap();
		final Color current = new Color(Bot.getImage().getRGB(x, y));
		methods.sleep(600);
		final int count = bounds.width * bounds.height;
		methods.sleep(400);

		if (map[x][y].getRGB() != current.getRGB()) {
			isMoving1 = true;
		}

		return isMoving1;
	}

	private void populateMap() {
		for (int x = bounds.x; x < bounds.x + bounds.width; ++x) {
			for (int y = bounds.y; y < bounds.y + bounds.height; ++y) {
				map[x][y] = new Color(Bot.getImage().getRGB(x, y));
			}
		}
	}
}
