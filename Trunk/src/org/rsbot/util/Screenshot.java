package org.rsbot.util;

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

public class Screenshot {
	private static final Logger log = Logger.getLogger(Screenshot.class
			.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
	"yyyyMMdd-hhmmss");

	public static void takeScreenshot(final boolean hideUsername) {
		final String name = Screenshot.dateFormat.format(new Date()) + ".png";
		final File dir = new File(
				GlobalConfiguration.Paths.getScreenshotsDirectory());
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		Screenshot.takeScreenshot(new File(dir, name), "png", hideUsername);
	}

	public static void takeScreenshot(final File file, final String type,
			final boolean hideUsername) {
		try {
			final BufferedImage source = Bot.getImage();
			final WritableRaster raster = source.copyData(null);

			final BufferedImage bufferedImage = new BufferedImage(
					source.getColorModel(), raster,
					source.isAlphaPremultiplied(), null);
			final Graphics2D graphics = bufferedImage.createGraphics();

			if (hideUsername) {
				graphics.setColor(Color.black);
				graphics.fill(new Rectangle(9, 459, 100, 15));
				graphics.dispose();
			}

			ImageIO.write(bufferedImage, type, file);
			Screenshot.log.config("Screenshot saved to: " + file.getPath());
		} catch (final Exception e) {
			Screenshot.log.log(Level.SEVERE, "Could not take screenshot.", e);
		}
	}
}
