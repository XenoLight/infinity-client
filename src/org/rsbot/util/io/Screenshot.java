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
package org.rsbot.util.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.rsbot.bot.Bot;
import org.rsbot.util.GlobalConfiguration;

/**
 * 
 * @author Runedev development team - version 1.0
 */
public class Screenshot {

	private static final Logger log = Logger.getLogger(Screenshot.class
			.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
	"yyyyMMdd-hhmmss");

	public static void saveScreenshot(final Bot bot, final boolean hideUsername) {
		final String name = Screenshot.dateFormat.format(new Date()) + ".png";
		final File dir = new File(
				GlobalConfiguration.Paths.getScreenshotsDirectory());
		if (dir.isDirectory() || dir.mkdirs()) {
			Screenshot.saveScreenshot(bot, new File(dir, name), "png",
					hideUsername);
		}
	}

	public static void saveScreenshot(final Bot bot,
			final boolean hideUsername, String filename) {
		if (!filename.endsWith(".png")) {
			filename = filename.concat(".png");
		}

		final File dir = new File(
				GlobalConfiguration.Paths.getScreenshotsDirectory());
		if (dir.isDirectory() || dir.mkdirs()) {
			Screenshot.saveScreenshot(bot, new File(dir, filename), "png",
					hideUsername);
		}
	}

	private static void saveScreenshot(final Bot bot, final File file,
			final String type, final boolean hideUsername) {
		try {
			final BufferedImage image = takeScreenshot(bot, hideUsername);

			ImageIO.write(image, type, file);
			Screenshot.log.config("Screenshot saved to: " + file.getPath());
		} catch (final Exception e) {
			Screenshot.log.log(Level.SEVERE, "Could not take screenshot.", e);
		}
	}

	public static BufferedImage takeScreenshot(final Bot bot,
			final boolean hideUsername) {
		final BufferedImage source = Bot.getImage();
		final WritableRaster raster = source.copyData(null);

		final BufferedImage bufferedImage = new BufferedImage(
				source.getColorModel(), raster, source.isAlphaPremultiplied(),
				null);
		final Graphics2D graphics = bufferedImage.createGraphics();

		if (hideUsername) {
			if (bot.getMethods().game.isFixed()) {
				graphics.setColor(Color.black);
				graphics.fill(new Rectangle(9, 459, 100, 15));
				graphics.dispose();
			} else {
				graphics.setColor(Color.black);
				graphics.drawRect(8, 555, 100, 15);
				graphics.dispose();
			}
		}
		return source;
	}
}
