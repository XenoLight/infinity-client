/*
 * 2011 Runedev development team
 * http://lazygamerz.org
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 3 of the Licence, or (at your opinion) any
 * later version.
 *
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 */
package org.lazygamerz.scripting.api.color;

import java.awt.Color;
import java.awt.Rectangle;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;

/**
 * @author Sorcermus - version 1.0
 * @author Runedev development team - version 1.1
 */
public class ColorAnimation {

	Methods methods;
	private final Rectangle bounds = new Rectangle(methods.random(240, 235), 120, 50,
			80);
	private boolean blocked = false;
	private final Color[][] map = new Color[762][503];
	private double ratio = 20.0D;

	public ColorAnimation(final Methods methods) {
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
		methods.sleep(1000);

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
