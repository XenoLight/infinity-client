package org.lazygamerz.scripting.api.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.rsbot.bot.Bot;
import org.rsbot.util.color.ColorCompute;

public class OCR {

	public static Color[] COMMON_COLORS = { new Color(255, 255, 255),
		new Color(218, 218, 218), new Color(3, 232, 232) };
	private final BufferedImage[] CHARSET = new BufferedImage[123];

	public OCR() {
	}

	public BufferedImage applyFilter(final Color[] filter, final Dimension dim) {
		final BufferedImage image = new BufferedImage(dim.width, dim.height, 1);
		image.getGraphics().drawImage(
				Bot.getImage().getSubimage(0, 0, dim.width, dim.height), 0, 0,
				null);
		for (int x = 0; x < dim.width; ++x) {
			for (int y = 0; y < dim.height; ++y) {
				final int rgb = image.getRGB(x, y);
				for (final Color color : filter) {
					if (!(ColorCompute.computeTolerance(new Color(rgb), color,
							40))) {
						image.setRGB(x, y, -65536);
					} else {
						image.setRGB(x, y, -1);
					}
				}
			}
		}
		try {
			ImageIO.write(image, "png", new File("ocrimage.png"));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public Rectangle find(final BufferedImage image, final BufferedImage bitmap,
			final Rectangle searchArea) {
		Color color = new Color(bitmap.getRGB(0, 0));
		for (int x = searchArea.x; x < searchArea.x + searchArea.width; ++x) {
			for (int y = searchArea.y; y < searchArea.y + searchArea.height; ++y) {
				Color color2 = new Color(image.getRGB(x, y));
				int count = 0;
				if (ColorCompute.computeTolerance(color, color2, 5)) {
					++count;
					for (int x1 = 0; x1 < bitmap.getWidth(); ++x1) {
						for (int y1 = 0; y1 < bitmap.getHeight(); ++y1) {
							if ((x1 + x >= image.getWidth())
									|| (y1 + y >= image.getHeight())) {
								return null;
							}
							color2 = new Color(image.getRGB(x + x1, y + y1));
							color = new Color(bitmap.getRGB(x1, y1));
							if (ColorCompute.computeTolerance(color, color2, 5)) {
								++count;
							}
							if (count >= bitmap.getWidth() * bitmap.getHeight()) {
								return new Rectangle(x, y, x + x1, y + y1);
							}
						}
					}
				}
			}
		}
		return null;
	}

	public String getToptext() {
		String toptext = "";
		for (int i = 0; i < CHARSET.length; ++i) {
			if ((CHARSET[i] == null)
					|| (find(
							applyFilter(COMMON_COLORS, new Dimension(100, 30)),
							CHARSET[i], new Rectangle(0, 0, 100, 30)) == null)) {
				continue;
			}
			toptext = toptext + (char) i;
		}

		return toptext;
	}

	public void loadCharacters() {
		for (int i = 0; i < CHARSET.length; ++i) {
			final File file = new File("./bitmaps/" + i + ".bmp");
			if (!(file.exists())) {
				continue;
			}
			try {
				CHARSET[i] = ImageIO.read(file);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
}
