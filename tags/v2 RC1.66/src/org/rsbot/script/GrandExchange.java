package org.rsbot.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Obtains information on tradeable items from the Grand Exchange website.
 * <p/>
 * Due to a recent update for the Wilderness the Item lookup has been changed
 * with a new lookup.
 * 
 * @author Aelin
 */
@Deprecated
public class GrandExchange {

	@Deprecated
	public static class GEItem {

		private final String name;
		private final String examine;

		private final int id;

		private final int guidePrice;

		private final double change30;
		private final double change90;
		private final double change180;

		@Deprecated
		GEItem(final String name, final String examine, final int id, final double[] values, final int guide) {
			this.name = name;
			this.examine = examine;
			this.id = id;
			this.guidePrice = guide;
			change30 = values[0];
			change90 = values[1];
			change180 = values[2];
		}

		/**
		 * Gets the change in price for the last 180 days of this item.
		 */
		@Deprecated
		public double getChange180Days() {
			return change180;
		}

		/**
		 * Gets the change in price for the last 30 days of this item.
		 */
		@Deprecated
		public double getChange30Days() {
			return change30;
		}

		/**
		 * Gets the change in price for the last 90 days of this item.
		 */
		@Deprecated
		public double getChange90Days() {
			return change90;
		}

		/**
		 * Gets the description of this item.
		 */
		@Deprecated
		public String getDescription() {
			return examine;
		}

		/**
		 * Gets the minimum market price of this item.
		 */
		@Deprecated
		public int getGuidePrice() {
			return guidePrice;
		}

		/**
		 * Gets the ID of this item.
		 */
		@Deprecated
		public int getID() {
			return id;
		}

		/**
		 * Gets the name of this item.
		 */
		@Deprecated
		public String getName() {
			return name;
		}
	}
	private static final String HOST = "http://services.runescape.com";

	private static final String GET = "/m=itemdb_rs/viewitem.ws?obj=";

	private GEItem last = null;

	private static final Pattern PATTERN = Pattern
	.compile("(?i)<td><img src=\".+obj_sprite\\.gif\\?id=(\\d+)\" alt=\"(.+)\"");

	/**
	 * Gets the ID of the given item name. Should not be used.
	 * 
	 * @param itemName
	 *            The name of the item to look for.
	 * @return The ID of the given item name or -1 if unavailable.
	 * @see GrandExchange#lookup(java.lang.String)
	 */
	@Deprecated
	public int getItemID(final String itemName) {
		final GEItem geItem = lookup(itemName);
		if (geItem != null) {
			return geItem.getID();
		}
		return -1;
	}

	/**
	 * Gets the name of the given item ID. Should not be used.
	 * 
	 * @see GrandExchange#lookup(int)
	 */
	@Deprecated
	public String getItemName(final int itemID) {
		final GEItem geItem = loadItemInfo(itemID);
		if (geItem != null) {
			return geItem.getName();
		}
		return "";
	}

	/**
	 * Collects data for a given item ID from the Grand Exchange website.
	 * 
	 * @param itemID
	 *            The item ID.
	 * @return An instance of GrandExchange.GEItem; <code>null</code> if unable
	 *         to fetch data.
	 */
	@Deprecated
	public GEItem loadItemInfo(final int itemID) {
		try {
			if (last != null && last.getID() == itemID)
				return last;
			final URL url = new URL(HOST + GET + itemID);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String input;
			boolean exists = false;
			int i = 0;
			final double[] values = new double[3];
			int guide = 0;
			String name = "", examine = "", line = "";
			while ((input = br.readLine()) != null) {
				if (input.contains("<div class=\"brown_box main_ge_page")
						&& !exists) {
					if (!input.contains("vertically_spaced")) {
						return null;
					}
					exists = true;
					br.readLine();
					br.readLine();
					name = br.readLine();
				} else if (input.contains("<img id=\"item_image\" src=\"")) {
					examine = br.readLine();
				} else if (input.contains(" Days:</b> <span class=")) {
					final int start = (input.indexOf(" Days:</b> <span class=") + 7);
					final int end = input.indexOf("</span>", start);
					values[i] = parse(input.substring(start, end));
					i++;
				} else if (input.contains("<b>Current guide price:</b>")) {
					line = input.replace("<b>Current guide price:</b>", "");
					guide = (int) parse(line);
				} else if (input.matches("<div id=\"legend\">"))
					break;
			}
			last = new GEItem(name, examine, itemID, values, guide);
			return new GEItem(name, examine, itemID, values, guide);
		} catch (final IOException ignore) {
		}
		return null;
	}

	/**
	 * Collects data for a given item name from the Grand Exchange website.
	 * 
	 * @param itemName
	 *            The name of the item.
	 * @return An instance of GrandExchange.GEItem; <code>null</code> if unable
	 *         to fetch data.
	 */
	@Deprecated
	public GEItem lookup(final String itemName) {
		try {
			final URL url = new URL(GrandExchange.HOST
					+ "/m=itemdb_rs/results.ws?query=" + itemName
					+ "&price=all&members=");
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String input;
			while ((input = br.readLine()) != null) {
				if (input.contains("<div id=\"search_results_text\">")) {
					input = br.readLine();
					if (input.contains("Your search for")) {
						return null;
					}
				} else if (input.startsWith("<td><img src=")) {
					final Matcher matcher = GrandExchange.PATTERN.matcher(input);
					if (matcher.find()) {
						if (matcher.group(2).contains(itemName)) {
							return loadItemInfo(Integer.parseInt(matcher
									.group(1)));
						}
					}
				}
			}
		} catch (final IOException ignored) {
		}
		return null;
	}

	@Deprecated
	private double parse(String str) {
		if (str != null && !str.isEmpty()) {
			str = stripFormatting(str);
			str = str.substring(str.indexOf(58) + 2, str.length());
			str = str.replace(",", "");
			if (!str.endsWith("%")) {
				if (!str.endsWith("k") && !str.endsWith("m")) {
					return Double.parseDouble(str);
				}
				return Double.parseDouble(str.substring(0, str.length() - 1))
				* (str.endsWith("m") ? 1000000 : 1000);
			}
			final int k = str.startsWith("+") ? 1 : -1;
			str = str.substring(1);
			return Double.parseDouble(str.substring(0, str.length() - 1)) * k;
		}
		return -1D;
	}

	@Deprecated
	private String stripFormatting(final String str) {
		if (str != null && !str.isEmpty())
			return str.replaceAll("(^[^<]+>|<[^>]+>|<[^>]+$)", "");
		return "";
	}
}
