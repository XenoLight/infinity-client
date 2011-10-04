/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.util;

import java.awt.Color;

/**
 * ColorRoutines is a collection of static utility methods related to color
 * calculations.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class ColorRoutines {

	private static final int RGB = 1;
	private static final int RBG = 2;
	private static final int GBR = 3;
	private static final int GRB = 4;
	private static final int BRG = 5;
	private static final int BGR = 6;
	private static float hsb[] = new float[3];

	public static int calculateHue(final Color c) {
		hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
		return (int) Math.round(360.0 * hsb[0]);
	}
	private static int colorToInt(final int r, final int g, final int b, final int a) {
		return b + g * 256 + r * (256 * 256) + a * (256 * 256 * 256);
	}
	public static Color darken(final Color c, final int amount) {
		if (amount < 0 || amount > 100)
			return c;

		final int r = (int) Math.round(c.getRed() * (100 - amount) / 100.0);
		final int g = (int) Math.round(c.getGreen() * (100 - amount) / 100.0);
		final int b = (int) Math.round(c.getBlue() * (100 - amount) / 100.0);

		return new Color(r, g, b, c.getAlpha());
	}
	public static Color getAdjustedColor(final Color inColor, final int sat, final int bri) {
		Color briColor = inColor;

		// first do brightening
		if (bri < 0) {
			briColor = ColorRoutines.darken(inColor, -bri);
		} else if (bri > 0) {
			briColor = ColorRoutines.lighten(inColor, bri);
		}

		// then do saturation
		final Color satColor = getMaxSaturation(briColor, getHue(inColor));
		int r, g, b;

		if (sat >= 0) {
			final int dr = briColor.getRed() - satColor.getRed();
			final int dg = briColor.getGreen() - satColor.getGreen();
			final int db = briColor.getBlue() - satColor.getBlue();

			r = briColor.getRed() - (int) Math.round(dr * sat / 100.0);
			g = briColor.getGreen() - (int) Math.round(dg * sat / 100.0);
			b = briColor.getBlue() - (int) Math.round(db * sat / 100.0);
		} else {
			final float d = ColorRoutines.getGreyValue(briColor);
			final float dr = briColor.getRed() - d;
			final float dg = briColor.getGreen() - d;
			final float db = briColor.getBlue() - d;

			r = (int) Math.round(briColor.getRed() + dr * sat / 100.0);
			g = (int) Math.round(briColor.getGreen() + dg * sat / 100.0);
			b = (int) Math.round(briColor.getBlue() + db * sat / 100.0);
		}

		return new Color(r, g, b);
	}
	public static Color getAlphaColor(final Color c, final int a) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
	}
	public static Color getAverage(final Color c1, final Color c2) {
		final int r = (int) Math.round((c1.getRed() + c2.getRed()) / 2.0);
		final int g = (int) Math.round((c1.getGreen() + c2.getGreen()) / 2.0);
		final int b = (int) Math.round((c1.getBlue() + c2.getBlue()) / 2.0);

		return new Color(r, g, b);
	}

	public static int getBrightness(final Color c) {
		return getBrightness(c.getRed(), c.getGreen(), c.getBlue());
	}

	private static int getBrightness(final int r, final int g, final int b) {
		if (r >= g && r >= b) {
			return (int) Math.round(100 * r / 255.0);
		} else if (g >= r && g >= b) {
			return (int) Math.round(100 * g / 255.0);
		} else if (b >= r && b >= g) {
			return (int) Math.round(100 * b / 255.0);
		}

		return -1;
	}

	// i >= 0 <= d
	// c1 ist Einblendfarbe
	// c2 ist Hintergrundfarbe
	public static Color getGradient(final Color c1, final Color c2, final int d, final int i) {
		if (i == 0)
			return c1;
		if (i == d)
			return c2;

		final double d2 = i * 1.1 / d;
		final double d1 = 1.0 - d2;

		final int r = (int) Math.round(c1.getRed() * d1 + c2.getRed() * d2);
		final int g = (int) Math.round(c1.getGreen() * d1 + c2.getGreen() * d2);
		final int b = (int) Math.round(c1.getBlue() * d1 + c2.getBlue() * d2);

		return new Color(r, g, b);
	}

	private static float getGreyValue(final Color c) {
		final int r = c.getRed();
		final int g = c.getGreen();
		final int b = c.getBlue();
		int tb = 0, tc = 0;

		if (r >= g && r >= b) {
			if (r == 0)
				return 0; // black

			tc = r;

			if (g >= b) {
				tb = b;
			} else {
				tb = g;
			}
		} else if (g >= r && g >= b) {
			tc = g;

			if (r >= b) {
				tb = b;
			} else {
				tb = r;
			}
		} else if (b >= r && b >= g) {
			tc = b;

			if (r >= g) {
				tb = g;
			} else {
				tb = r;
			}
		}

		return (float) ((tc + tb) / 2.0);
	}

	public static int getHue(final Color c) {
		return getHue(c.getRed(), c.getGreen(), c.getBlue());
	}

	private static int getHue(final int r, final int g, final int b) {
		int ta = 0, tb = 0, tc = 0;
		int mapping = RGB;

		if (r >= g && r >= b) {
			tc = r;

			if (g == b) {
				return 0;
			} else if (g > b) {
				ta = g;
				tb = b;
				mapping = RGB;
			} else {
				tb = g;
				ta = b;
				mapping = RBG;
			}
		} else if (g >= r && g >= b) {
			tc = g;

			if (r == b) {
				return 120;
			} else if (r > b) {
				ta = r;
				tb = b;
				mapping = GRB;
			} else {
				tb = r;
				ta = b;
				mapping = GBR;
			}
		} else if (b >= r && b >= g) {
			tc = b;

			if (r == g) {
				return 240;
			} else if (r > g) {
				ta = r;
				tb = g;
				mapping = BRG;
			} else {
				tb = r;
				ta = g;
				mapping = BGR;
			}
		}

		// normalize
		final double na = (ta * 255.0 / tc);
		final double nb = (tb * 255.0 / tc);

		final double val = ((na - nb) * 255.0 / (255 - nb));

		final int w = (int) Math.round(60 * val / 255.0);

		switch (mapping) {
		case RGB:
			return w; // 0 - 60
		case RBG:
			return 360 - w; // 300 - 360
		case GBR:
			return 120 + w; // 120 - 180
		case GRB:
			return 120 - w; // 60 - 120
		case BRG:
			return 240 + w; // 240 - 300
		case BGR:
			return 240 - w; // 180 - 240
		default:
			return -1;
		}
	}

	private static Color getMaxSaturation(final Color c, final int memH) {
		final int r = c.getRed();
		final int g = c.getGreen();
		final int b = c.getBlue();

		if (r == g && r == b)
			return c;

		int ta = 0, tb = 0, tc = 0;
		int mapping = RGB;

		if (r >= g && r >= b) {
			tc = r;

			if (g == b) {
				ta = g;
				tb = b;
				mapping = RGB;
			} else if (g > b) {
				ta = g;
				tb = b;
				mapping = RGB;
			} else {
				tb = g;
				ta = b;
				mapping = RBG;
			}
		} else if (g >= r && g >= b) {
			tc = g;

			if (r == b) {
				ta = r;
				tb = b;
				mapping = GRB;
			} else if (r > b) {
				ta = r;
				tb = b;
				mapping = GRB;
			} else {
				tb = r;
				ta = b;
				mapping = GBR;
			}
		} else if (b >= r && b >= g) {
			tc = b;

			if (r == g) {
				ta = r;
				tb = g;
				mapping = BRG;
			} else if (r > g) {
				ta = r;
				tb = g;
				mapping = BRG;
			} else {
				tb = r;
				ta = g;
				mapping = BGR;
			}
		}

		if (tb == 0) {
			return c;
		}

		final int nc = Math.min(255, tc + tb);
		final int nb = Math.max(0, tc + tb - 255);
		int na = ta;
		int h = 0, mh = 0;
		int ba = 0, delta = 360;
		Color rc = null;

		switch (mapping) {
		case RGB:
			h = getHue(nc, na, nb);
			mh = h;
			while (h != memH && na < 256) {
				h = getHue(nc, ++na, nb);
				if (na == 256)
					break;

				if (h == memH) {
					return new Color(nc, na, nb);
				} else if ((mh < memH && h > memH) || (mh > memH && h < memH)) {
					return new Color(nc, na, nb);
				} else if (Math.abs(h - memH) < delta) {
					delta = Math.abs(h - memH);
					ba = na;
				}
				mh = h;
			}

			if (h != memH) {
				h = getHue(nc, na, nb);
				mh = h;
				na = ta;
				while (h != memH && na >= 0) {
					h = getHue(nc, --na, nb);
					if (na == -1)
						break;

					if (h == memH) {
						return new Color(nc, na, nb);
					} else if ((mh < memH && h > memH)
							|| (mh > memH && h < memH)) {
						return new Color(nc, na, nb);
					} else if (Math.abs(h - memH) < delta) {
						delta = Math.abs(h - memH);
						ba = na;
					}
					mh = h;
				}
			}
			if (na == 256 | na == -1) {
				na = ba;
			}
			rc = new Color(nc, na, nb);
			break;
		case RBG:
			h = getHue(nc, nb, na);
			mh = h;
			while (h != memH && na < 256) {
				h = getHue(nc, nb, ++na);
				if (na == 256)
					break;

				if (h == memH) {
					return new Color(nc, nb, na);
				} else if ((mh < memH && h > memH) || (mh > memH && h < memH)) {
					return new Color(nc, nb, na);
				} else if (Math.abs(h - memH) < delta) {
					delta = Math.abs(h - memH);
					ba = na;
				}
				mh = h;
			}

			if (h != memH) {
				h = getHue(nc, na, nb);
				mh = h;
				na = ta;
				while (h != memH && na >= 0) {
					h = getHue(nc, nb, --na);
					if (na == -1)
						break;

					if (h == memH) {
						return new Color(nc, nb, na);
					} else if ((mh < memH && h > memH)
							|| (mh > memH && h < memH)) {
						return new Color(nc, nb, na);
					} else if (Math.abs(h - memH) < delta) {
						delta = Math.abs(h - memH);
						ba = na;
					}
					mh = h;
				}
			}
			if (na == 256 | na == -1) {
				na = ba;
			}
			rc = new Color(nc, nb, na);
			break;
		case GBR:
			h = getHue(nb, nc, na);
			mh = h;
			while (h != memH && na < 256) {
				h = getHue(nb, nc, ++na);
				if (na == 256)
					break;

				if (h == memH) {
					return new Color(nb, nc, na);
				} else if ((mh < memH && h > memH) || (mh > memH && h < memH)) {
					return new Color(nb, nc, na);
				} else if (Math.abs(h - memH) < delta) {
					delta = Math.abs(h - memH);
					ba = na;
				}
				mh = h;
			}

			if (h != memH) {
				h = getHue(nc, na, nb);
				mh = h;
				na = ta;
				while (h != memH && na >= 0) {
					h = getHue(nb, nc, --na);
					if (na == -1)
						break;

					if (h == memH) {
						return new Color(nb, nc, na);
					} else if ((mh < memH && h > memH)
							|| (mh > memH && h < memH)) {
						return new Color(nb, nc, na);
					} else if (Math.abs(h - memH) < delta) {
						delta = Math.abs(h - memH);
						ba = na;
					}
					mh = h;
				}
			}

			if (na == 256 | na == -1) {
				na = ba;
			}

			rc = new Color(nb, nc, na);
			break;
		case GRB:
			h = getHue(na, nc, nb);
			mh = h;
			while (h != memH && na < 256) {
				h = getHue(++na, nc, nb);
				if (na == 256)
					break;

				if (h == memH) {
					return new Color(na, nc, nb);
				} else if ((mh < memH && h > memH) || (mh > memH && h < memH)) {
					return new Color(na, nc, nb);
				} else if (Math.abs(h - memH) < delta) {
					delta = Math.abs(h - memH);
					ba = na;
				}
				mh = h;
			}

			if (h != memH) {
				h = getHue(nc, na, nb);
				mh = h;
				na = ta;
				while (h != memH && na >= 0) {
					h = getHue(--na, nc, nb);
					if (na == -1)
						break;

					if (h == memH) {
						return new Color(na, nc, nb);
					} else if ((mh < memH && h > memH)
							|| (mh > memH && h < memH)) {
						return new Color(na, nc, nb);
					} else if (Math.abs(h - memH) < delta) {
						delta = Math.abs(h - memH);
						ba = na;
					}
					mh = h;
				}
			}
			if (na == 256 | na == -1) {
				na = ba;
			}
			rc = new Color(na, nc, nb);
			break;
		case BRG:
			h = getHue(na, nb, nc);
			mh = h;
			while (h != memH && na < 256) {
				h = getHue(++na, nb, nc);
				if (na == 256)
					break;

				if (h == memH) {
					return new Color(na, nb, nc);
				} else if ((mh < memH && h > memH) || (mh > memH && h < memH)) {
					return new Color(na, nb, nc);
				} else if (Math.abs(h - memH) < delta) {
					delta = Math.abs(h - memH);
					ba = na;
				}
				mh = h;
			}

			if (h != memH) {
				h = getHue(nc, na, nb);
				mh = h;
				na = ta;
				while (h != memH && na >= 0) {
					h = getHue(--na, nb, nc);
					if (na == -1)
						break;

					if (h == memH) {
						return new Color(na, nb, nc);
					} else if ((mh < memH && h > memH)
							|| (mh > memH && h < memH)) {
						return new Color(na, nb, nc);
					} else if (Math.abs(h - memH) < delta) {
						delta = Math.abs(h - memH);
						ba = na;
					}
					mh = h;
				}
			}
			if (na == 256 | na == -1) {
				na = ba;
			}
			rc = new Color(na, nb, nc);
			break;
		case BGR:
			h = getHue(nb, na, nc);
			mh = h;
			while (h != memH && na < 256) {
				h = getHue(nb, ++na, nc);
				if (na == 256)
					break;

				if (h == memH) {
					return new Color(nb, na, nc);
				} else if ((mh < memH && h > memH) || (mh > memH && h < memH)) {
					return new Color(nb, na, nc);
				} else if (Math.abs(h - memH) < delta) {
					delta = Math.abs(h - memH);
					ba = na;
				}
				mh = h;
			}

			if (h != memH) {
				h = getHue(nc, na, nb);
				mh = h;
				na = ta;
				while (h != memH && na >= 0) {
					h = getHue(nb, --na, nc);
					if (na == -1)
						break;

					if (h == memH) {
						return new Color(nb, na, nc);
					} else if ((mh < memH && h > memH)
							|| (mh > memH && h < memH)) {
						return new Color(nb, na, nc);
					} else if (Math.abs(h - memH) < delta) {
						delta = Math.abs(h - memH);
						ba = na;
					}
					mh = h;
				}
			}
			if (na == 256 | na == -1) {
				na = ba;
			}
			rc = new Color(nb, na, nc);
			break;
		}

		return rc;
	}

	public static int getSaturation(final Color c) {
		return getSaturation(c.getRed(), c.getGreen(), c.getBlue());
	}

	private static int getSaturation(final int r, final int g, final int b) {
		int tb = 0, tc = 0;

		if (r >= g && r >= b) {
			if (r == 0)
				return 0; // black

			tc = r;

			if (g >= b) {
				tb = b;
			} else {
				tb = g;
			}
		} else if (g >= r && g >= b) {
			tc = g;

			if (r >= b) {
				tb = b;
			} else {
				tb = r;
			}
		} else if (b >= r && b >= g) {
			tc = b;

			if (r >= g) {
				tb = g;
			} else {
				tb = r;
			}
		}

		return 100 - (int) Math.round(100.0 * tb / tc);
	}

	public static boolean isColorDarker(final Color c1, final Color c2) {
		return c1.getRed() + c1.getGreen() + c1.getBlue() < c2.getRed()
		+ c2.getGreen() + c2.getBlue();
	}

	public static Color lighten(final Color c, int amount) {
		if (amount < 0)
			return c;

		if (amount > 100)
			amount = 100;

		final int dr = (int) Math.round((255 - c.getRed()) * amount / 100.0);
		final int dg = (int) Math.round((255 - c.getGreen()) * amount / 100.0);
		final int db = (int) Math.round((255 - c.getBlue()) * amount / 100.0);

		return new Color(c.getRed() + dr, c.getGreen() + dg, c.getBlue() + db,
				c.getAlpha());
	}

	private final boolean preserveGrey;

	private int chue, csat, cbri;

	private final int fr, fg, fb;

	private boolean hiIsR, hiIsG, hiIsB;

	private boolean mdIsR, mdIsG, mdIsB;

	private boolean loIsR, loIsG, loIsB;

	ColorRoutines(final HSBReference hsbRef) {
		chue = hsbRef.hue;
		csat = hsbRef.getSaturation();
		cbri = hsbRef.getBrightness();
		preserveGrey = hsbRef.isPreserveGrey();

		final Color c = Color.getHSBColor((float) (chue / 360.0), 1.0f, 1.0f);
		fr = c.getRed();
		fg = c.getGreen();
		fb = c.getBlue();

		// sort colors - 6 options
		if (fr >= fg && fg >= fb) {
			hiIsR = true;
			mdIsG = true;
			loIsB = true;
		} else if (fr >= fb && fb >= fg) {
			hiIsR = true;
			mdIsB = true;
			loIsG = true;
		} else if (fg >= fr && fr >= fb) {
			hiIsG = true;
			mdIsR = true;
			loIsB = true;
		} else if (fg >= fb && fb >= fr) {
			hiIsG = true;
			mdIsB = true;
			loIsR = true;
		} else if (fb >= fg && fg >= fr) {
			hiIsB = true;
			mdIsG = true;
			loIsR = true;
		} else if (fb >= fr && fr >= fg) {
			hiIsB = true;
			mdIsR = true;
			loIsG = true;
		}
	}

	// Parameter: the original icon pixel
	int colorize(final int r, final int g, final int b, final int a) {
		if (cbri == 100) {
			return colorToInt(255, 255, 255, a);
		} else if (cbri == -100) {
			return colorToInt(0, 0, 0, a);
		}

		// first calculate the grey value
		int hi1 = r;
		if (g >= r && g >= b)
			hi1 = g;
		else if (b >= r && b >= g)
			hi1 = b;

		int lo1 = r;
		if (g <= r && g <= b)
			lo1 = g;
		else if (b <= r && b <= g)
			lo1 = b;

		int grey = (hi1 + lo1) / 2; // floor

		// if in-colors are equal and preserveGrey is true, return grey values
		// Note: In pre-1.4.0 releases, brightness setting was considered
		// before calling colorToInt(...). This made preserveGrey less
		// usefull...
		if (preserveGrey) {
			if (r == g && r == b) {
				return colorToInt(grey, grey, grey, a);
			}
		}

		// compute with cbri
		if (cbri < 0) {
			grey += grey * cbri / 100;
		} else if (cbri > 0) {
			grey += (255 - grey) * cbri / 100;
		}

		// now calculate the output colors
		// csat = 0 => output = grey
		// grey = 127 => output = full saturation
		int hr = 0;
		int hg = 0;
		int hb = 0;
		int diff = 0;

		if (grey >= 127) {
			diff = 255 - grey;
		} else {
			diff = grey;
		}
		// hi value is always 255
		if (hiIsR) {
			hr = grey + diff * csat / 100;
		} else if (hiIsG) {
			hg = grey + diff * csat / 100;
		} else if (hiIsB) {
			hb = grey + diff * csat / 100;
		}

		// md value is between 0 and 255
		if (mdIsR) {
			if (grey >= 127) {
				diff = fr + (255 - fr) * (grey - 127) / 128 - grey;
			} else {
				diff = fr * grey / 127 - grey;
			}
			hr = grey + diff * csat / 100;
		} else if (mdIsG) {
			if (grey >= 127) {
				diff = fg + (255 - fg) * (grey - 127) / 128 - grey;
			} else {
				diff = fg * grey / 127 - grey;
			}
			hg = grey + diff * csat / 100;
		} else if (mdIsB) {
			if (grey >= 127) {
				diff = fb + (255 - fb) * (grey - 127) / 128 - grey;
			} else {
				diff = fb * grey / 127 - grey;
			}
			hb = grey + diff * csat / 100;
		}

		diff = grey - (255 - grey);
		if (diff < 0)
			diff = 0;
		diff = grey - diff;

		// lo value = 0
		if (loIsR) {
			hr = grey - diff * csat / 100;
		} else if (loIsG) {
			hg = grey - diff * csat / 100;
		} else if (loIsB) {
			hb = grey - diff * csat / 100;
		}

		return colorToInt(hr, hg, hb, a);
	}
}
