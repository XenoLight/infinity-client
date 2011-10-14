package org.rsbot.script;

import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.client.DefLoader;
import org.rsbot.client.Node;
import org.rsbot.client.TileData;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

public class Calculations {

	static class Render {

		static float absoluteX1 = 0.0F;
		static float absoluteX2 = 0.0F;
		static float absoluteY1 = 0.0F;
		static float absoluteY2 = 0.0F;
		static int xMultiplier = 512;
		static int yMultiplier = 512;
		static int zNear = 50;
		static int zFar = 3500;
	}
	static class RenderData {

		static float xOff = 0.0F;
		static float xX = 32768.0F;
		static float xY = 0.0F;
		static float xZ = 0.0F;
		static float yOff = 0.0F;
		static float yX = 0.0F;
		static float yY = 32768.0F;
		static float yZ = 0.0F;
		static float zOff = 0.0F;
		static float zX = 0.0F;
		static float zY = 0.0F;
		static float zZ = 32768.0F;
	}
	/**
	 * Determines whether the tile represented by destX,destY is reachable from
	 * the tile represented by startX,startY
	 * 
	 * @param startX
	 *            x coordinate for the starting tile
	 * @param startY
	 *            y coordinate for the starting tile
	 * @param destX
	 *            x coordinate for the ending tile
	 * @param destY
	 *            x coordinate for the ending tile
	 * @param isObject
	 *            specifies if the destination is an object
	 * @return <tt>true</tt> if the destination tile is reachable,
	 *         <tt>false</tt> otherwise.
	 */
	public static boolean canReach(final int startX, final int startY, final int destX,
			final int destY, final boolean isObject) {
		final int bound = 104;
		final int[][] arrayOfInt1 = new int[bound][bound];
		final int[][] arrayOfInt2 = new int[bound][bound];
		final int[] arrayOfInt3 = new int[4000];
		final int[] arrayOfInt4 = new int[4000];
		int j;
		for (int i = 0; i < bound; i++) {
			for (j = 0; j < bound; j++) {
				arrayOfInt1[i][j] = 0;
				arrayOfInt2[i][j] = 99999999;
			}
		}

		arrayOfInt1[startX][startY] = 99;
		arrayOfInt2[startX][startY] = 0;
		int k = 0;
		int m = 0;
		int i;
		arrayOfInt3[k] = startX;
		arrayOfInt4[k] = startY;
		k++;
		final int n = arrayOfInt3.length;
		final int[][] groundData = Bot.getClient().getRSGroundDataArray()[Bot
		                                                                  .getClient().getPlane()].getBlocks();
		while (m != k) {
			i = arrayOfInt3[m];
			j = arrayOfInt4[m];

			if ((!isObject) && (i == destX) && (j == destY)) {
				return true;
			}
			if ((isObject)
					&& (((i == destX) && (j == destY + 1))
							|| ((i == destX) && (j == destY - 1))
							|| ((i == destX + 1) && (j == destY)) || ((i == destX - 1) && (j == destY)))) {
				return true;
			}
			m = (m + 1) % n;

			final int i1 = arrayOfInt2[i][j] + 1;

			if ((j > 0) && (arrayOfInt1[i][(j - 1)] == 0)
					&& ((groundData[i][(j - 1)] & 0x1280102) == 0)) {
				arrayOfInt3[k] = i;
				arrayOfInt4[k] = (j - 1);
				k = (k + 1) % n;
				arrayOfInt1[i][(j - 1)] = 1;
				arrayOfInt2[i][(j - 1)] = i1;
			}

			if ((i > 0) && (arrayOfInt1[(i - 1)][j] == 0)
					&& ((groundData[(i - 1)][j] & 0x1280108) == 0)) {
				arrayOfInt3[k] = (i - 1);
				arrayOfInt4[k] = j;
				k = (k + 1) % n;
				arrayOfInt1[(i - 1)][j] = 2;
				arrayOfInt2[(i - 1)][j] = i1;
			}

			if ((j < 103) && (arrayOfInt1[i][(j + 1)] == 0)
					&& ((groundData[i][(j + 1)] & 0x1280120) == 0)) {
				arrayOfInt3[k] = i;
				arrayOfInt4[k] = (j + 1);
				k = (k + 1) % n;
				arrayOfInt1[i][(j + 1)] = 4;
				arrayOfInt2[i][(j + 1)] = i1;
			}

			if ((i < 103) && (arrayOfInt1[(i + 1)][j] == 0)
					&& ((groundData[(i + 1)][j] & 0x1280180) == 0)) {
				arrayOfInt3[k] = (i + 1);
				arrayOfInt4[k] = j;
				k = (k + 1) % n;
				arrayOfInt1[(i + 1)][j] = 8;
				arrayOfInt2[(i + 1)][j] = i1;
			}

			if ((i > 0) && (j > 0) && (arrayOfInt1[(i - 1)][(j - 1)] == 0)
					&& ((groundData[(i - 1)][(j - 1)] & 0x128010E) == 0)
					&& ((groundData[(i - 1)][j] & 0x1280108) == 0)
					&& ((groundData[i][(j - 1)] & 0x1280102) == 0)) {
				arrayOfInt3[k] = (i - 1);
				arrayOfInt4[k] = (j - 1);
				k = (k + 1) % n;
				arrayOfInt1[(i - 1)][(j - 1)] = 3;
				arrayOfInt2[(i - 1)][(j - 1)] = i1;
			}

			if ((i > 0) && (j < 103) && (arrayOfInt1[(i - 1)][(j + 1)] == 0)
					&& ((groundData[(i - 1)][(j + 1)] & 0x1280138) == 0)
					&& ((groundData[(i - 1)][j] & 0x1280108) == 0)
					&& ((groundData[i][(j + 1)] & 0x1280120) == 0)) {
				arrayOfInt3[k] = (i - 1);
				arrayOfInt4[k] = (j + 1);
				k = (k + 1) % n;
				arrayOfInt1[(i - 1)][(j + 1)] = 6;
				arrayOfInt2[(i - 1)][(j + 1)] = i1;
			}

			if ((i < 103) && (j > 0) && (arrayOfInt1[(i + 1)][(j - 1)] == 0)
					&& ((groundData[(i + 1)][(j - 1)] & 0x1280183) == 0)
					&& ((groundData[(i + 1)][j] & 0x1280180) == 0)
					&& ((groundData[i][(j - 1)] & 0x1280102) == 0)) {
				arrayOfInt3[k] = (i + 1);
				arrayOfInt4[k] = (j - 1);
				k = (k + 1) % n;
				arrayOfInt1[(i + 1)][(j - 1)] = 9;
				arrayOfInt2[(i + 1)][(j - 1)] = i1;
			}

			if ((i < 103) && (j < 103) && (arrayOfInt1[(i + 1)][(j + 1)] == 0)
					&& ((groundData[(i + 1)][(j + 1)] & 0x12801E0) == 0)
					&& ((groundData[(i + 1)][j] & 0x1280180) == 0)
					&& ((groundData[i][(j + 1)] & 0x1280120) == 0)) {
				arrayOfInt3[k] = (i + 1);
				arrayOfInt4[k] = (j + 1);
				k = (k + 1) % n;
				arrayOfInt1[(i + 1)][(j + 1)] = 12;
				arrayOfInt2[(i + 1)][(j + 1)] = i1;
			}
		}
		return false;
	}

	/**
	 * Determines if the destination tile is reachable from the current player's
	 * location.
	 * 
	 * @param dest
	 *            The destination tile
	 * @param isObject
	 *            specifies if the destination is an object
	 * @return <tt>true</tt> if the destination tile is reachable,
	 *         <tt>false</tt> otherwise.
	 */
	public static boolean canReach(final RSTile dest, final boolean isObject) {
		final Methods methods = Bot.methods;
		return canReach(methods.player.getMine().getLocation(), dest, isObject);
	}

	/**
	 * Determines if the destination tile is reachable from the specified start
	 * tile.
	 * 
	 * @param start
	 *            The starting tile
	 * @param dest
	 *            The destination tile
	 * @param isObject
	 *            specifies if the destination is an object
	 * @return <tt>true</tt> if the destination tile is reachable,
	 *         <tt>false</tt> otherwise.
	 */
	public static boolean canReach(final RSTile start, final RSTile dest, final boolean isObject) {
		return canReach(start.getX() - Bot.getClient().getBaseX(), start.getY()
				- Bot.getClient().getBaseY(), dest.getX()
				- Bot.getClient().getBaseX(), dest.getY()
				- Bot.getClient().getBaseY(), isObject);
	}

	/**
	 * Returns the distance between the two tiles.
	 * 
	 * @param t1
	 *            The first tile.
	 * @param t2
	 *            The second tile.
	 * @return The distance between the tiles.
	 */
	public static int distanceBetween(final RSTile t1, final RSTile t2) {
		return (int) Math.hypot(t2.getX() - t1.getX(), t2.getY() - t1.getY());
	}

	/**
	 * 
	 * @param id
	 *            The id of the node
	 * @return A <tt>Node</tt> object corresponding to the ID in the loader.
	 */
	public static Node findNodeByID(final DefLoader loader, final long id) {
		if ((loader == null) || (loader.getCache() == null)) {
			return null;
		}
		return findNodeByID(loader.getCache().getTable(), id);
	}

	/**
	 * 
	 * @param nc
	 *            The HashTable to check
	 * @param id
	 *            The id of the node
	 * @return A <tt>Node</tt> object corresponding to the ID in the HashTable.
	 */
	public static Node findNodeByID(final org.rsbot.client.HashTable nc,
			final long id) {
		try {
			if ((nc == null) || (nc.getBuckets() == null) || (id < 0)) {
				return null;
			}

			final Node n = nc.getBuckets()[(int) (id & nc.getBuckets().length - 1)];
			for (Node node = n.getPrevious(); node != n; node = node
			.getPrevious()) {
				if (node.getID() == id) {
					return node;
				}
			}
		} catch (final Exception ignored) {
		}
		return null;
	}

	public static boolean onScreen(final Point screenPoint) {
		final int x = screenPoint.x;
		final int y = screenPoint.y;
		return (x > 4) && (x < Bot.getCanvas().getWidth() - 253) && (y > 4)
		&& (y < Bot.getCanvas().getHeight() - 169);
	}

	public static int tileHeight(final int tileX, final int tileY) {
		int i = Bot.getClient().getPlane();
		final int j = tileX >> 9;
		final int k = tileY >> 9;

			if ((j >= 0) && (j < 104) && (k >= 0) && (k < 104)) {
				if ((i <= 3)
						&& ((Bot.getClient().getGroundByteArray()[1][j][k] & 0x2) != 0)) {
					i++;
				}

				final TileData[] arrayOfTileData = Bot.getClient().getTileData();

				if ((i < arrayOfTileData.length) && (arrayOfTileData[i] != null)) {
					final int[][] arrayOfInt = arrayOfTileData[i].getHeights();
					if (arrayOfInt != null) {
						final int m = tileX & 0x1FF;
						final int n = tileY & 0x1FF;
						final int i1 = arrayOfInt[j][k] * (512 - m)
						+ arrayOfInt[(j + 1)][k] * m >> 9;
				final int i2 = arrayOfInt[j][(1 + k)] * (512 - m)
				+ arrayOfInt[(j + 1)][(k + 1)] * m >> 9;
			return i1 * (512 - n) + i2 * n >> 9;
					}
				}
			}

			return 0;
	}

	public static Point tileToScreen(final int tileX, final int tileY, final double innerOffsetX,
			final double innerOffsetY, final int height) {
		return worldToScreen(
				(int) ((tileX - Bot.getClient().getBaseX() + innerOffsetX) * 512.0D),
				(int) ((tileY - Bot.getClient().getBaseY() + innerOffsetY) * 512.0D),
				height);
	}

	public static Point tileToScreen(final int tileX, final int tileY, final int height) {
		return tileToScreen(tileX, tileY, 0.5D, 0.5D, height);
	}

	public static Point tileToScreen(final RSTile t) {
		return tileToScreen(t.getX(), t.getY(), 0.5D, 0.5D, 0);
	}

	public static Point tileToScreen(final RSTile t, final double innerOffsetX,
			final double innerOffsetY, final int height) {
		return tileToScreen(t.getX(), t.getY(), innerOffsetX, innerOffsetY,
				height);
	}

	public static Point tileToScreen(final RSTile t, final int height) {
		return tileToScreen(t.getX(), t.getY(), 0.5D, 0.5D, height);
	}

	public static void updateRenderInfo(
			final org.rsbot.client.Render newRender,
			final org.rsbot.client.RenderData newRenderData) {
		if ((newRender == null) || (newRenderData == null)) {
			return;
		}
		Render.absoluteX1 = newRender.getAbsoluteX1();
		Render.absoluteX2 = newRender.getAbsoluteX2();
		Render.absoluteY1 = newRender.getAbsoluteY1();
		Render.absoluteY2 = newRender.getAbsoluteY2();

		Render.xMultiplier = newRender.getXMultiplier();
		Render.yMultiplier = newRender.getYMultiplier();

		Render.zNear = newRender.getZNear();
		Render.zFar = newRender.getZFar();

		RenderData.xOff = newRenderData.getXOff();
		RenderData.xX = newRenderData.getXX();
		RenderData.xY = newRenderData.getXY();
		RenderData.xZ = newRenderData.getXZ();

		RenderData.yOff = newRenderData.getYOff();
		RenderData.yX = newRenderData.getYX();
		RenderData.yY = newRenderData.getYY();
		RenderData.yZ = newRenderData.getYZ();

		RenderData.zOff = newRenderData.getZOff();
		RenderData.zX = newRenderData.getZX();
		RenderData.zY = newRenderData.getZY();
		RenderData.zZ = newRenderData.getZZ();
	}

	public static Point w2s(final int x, final int y, final int z) {
		final float f = RenderData.zX * x + RenderData.zY * y + RenderData.zZ * z
		+ RenderData.zOff;
		if ((f < Render.zNear) || (f > Render.zFar)) {
			return new Point(-1, -1);
		}
		final int i = (int) (Render.xMultiplier
				* (RenderData.xX * x + RenderData.xY * y + RenderData.xZ * z + RenderData.xOff) / f);
		final int j = (int) (Render.yMultiplier
				* (RenderData.yX * x + RenderData.yY * y + RenderData.yZ * z + RenderData.yOff) / f);

		if ((i >= Render.absoluteX1) && (i <= Render.absoluteX2)
				&& (j >= Render.absoluteY1) && (j <= Render.absoluteY2)) {
			return new Point((int) (i - Render.absoluteX1) + 4,
					(int) (j - Render.absoluteY1) + 4);
		}
		return new Point(-1, -1);
	}

	public static Point worldToScreen(final int x, final int y, final int z) {
		if ((Bot.getClient().getGroundByteArray() == null)
				|| (Bot.getClient().getTileData() == null) || (x < 512)
				|| (y < 512) || (x > 52224) || (y > 52224)) {
			return new Point(-1, -1);
		}
		final int i = tileHeight(x, y) - z;

		final Point localPoint = w2s(x, i, y);

		if ((localPoint != null) && (onScreen(localPoint))) {
			return localPoint;
		}
		return new Point(-1, -1);
	}

	private final Methods methods;

	private static final int[] CURVESIN = new int[16384];

	private static final int[] CURVECOS = new int[16384];

	static {
		for (int i = 0; i < 16384; i++) {
			CURVESIN[i] = (int) (32768.0D * Math
					.sin(i * 0.0003834951969714103D));
			CURVECOS[i] = (int) (32768.0D * Math
					.cos(i * 0.0003834951969714103D));
		}
	}

	public Calculations() {
		methods = Bot.methods;
	}

	public double distance(final RSTile t, final RSTile dest) {
		return Math.sqrt((t.getX() - dest.getX()) * (t.getX() - dest.getX())
				+ (t.getY() - dest.getY()) * (t.getY() - dest.getY()));
	}

	public int distanceTo(final RSCharacter c) {
		return c == null ? -1 : distanceTo(c.getLocation());
	}

	public int distanceTo(final RSObject o) {
		return o == null ? -1 : distanceTo(o.getLocation());
	}

	public int distanceTo(final RSTile t) {
		return t == null ? -1 : Calculations.distanceBetween(methods.player
				.getMine().getLocation(), t);
	}

	/**
	 * Determines the real distance between the tile represented by
	 * startX,startY and the tile represented by destX,destY.
	 * 
	 * @param startX
	 *            x coordinate for the starting tile
	 * @param startY
	 *            y coordinate for the starting tile
	 * @param destX
	 *            x coordinate for the ending tile
	 * @param destY
	 *            x coordinate for the ending tile
	 * @param isObject
	 *            specifies if the destination is an object
	 * @return <tt>true</tt> if the destination tile is reachable,
	 *         <tt>false</tt> otherwise.
	 */
	public int getRealDistanceTo(final int startX, final int startY, final int destX, final int destY,
			final boolean isObject) {
		final int[][] arrayOfInt1 = new int[104][104];
		final int[][] arrayOfInt2 = new int[104][104];
		final int[] arrayOfInt3 = new int[4000];
		final int[] arrayOfInt4 = new int[4000];

		for (int i = 0; i < 104; i++) {
			for (int j = 0; j < 104; j++) {
				arrayOfInt1[i][j] = 0;
				arrayOfInt2[i][j] = 99999999;
			}
		}

		int i = startX;
		int j = startY;
		arrayOfInt1[startX][startY] = 99;
		arrayOfInt2[startX][startY] = 0;
		int k = 0;
		int m = 0;
		arrayOfInt3[k] = startX;
		arrayOfInt4[(k++)] = startY;
		int n = 0;
		final int i1 = arrayOfInt3.length;
		final int[][] arrayOfInt5 = Bot.getClient().getRSGroundDataArray()[Bot
		                                                                   .getClient().getPlane()].getBlocks();
		while (m != k) {
			i = arrayOfInt3[m];
			j = arrayOfInt4[m];

			if ((!isObject) && (i == destX) && (j == destY)) {
				n = 1;
				break;
			}
			if ((isObject)
					&& (((i == destX) && (j == destY + 1))
							|| ((i == destX) && (j == destY - 1))
							|| ((i == destX + 1) && (j == destY)) || ((i == destX - 1) && (j == destY)))) {
				n = 1;
				break;
			}

			m = (m + 1) % i1;

			final int i2 = arrayOfInt2[i][j] + 1;
			if ((j > 0) && (arrayOfInt1[i][(j - 1)] == 0)
					&& ((arrayOfInt5[i][(j - 1)] & 0x1280102) == 0)) {
				arrayOfInt3[k] = i;
				arrayOfInt4[k] = (j - 1);
				k = (k + 1) % i1;
				arrayOfInt1[i][(j - 1)] = 1;
				arrayOfInt2[i][(j - 1)] = i2;
			}
			if ((i > 0) && (arrayOfInt1[(i - 1)][j] == 0)
					&& ((arrayOfInt5[(i - 1)][j] & 0x1280108) == 0)) {
				arrayOfInt3[k] = (i - 1);
				arrayOfInt4[k] = j;
				k = (k + 1) % i1;
				arrayOfInt1[(i - 1)][j] = 2;
				arrayOfInt2[(i - 1)][j] = i2;
			}
			if ((j < 103) && (arrayOfInt1[i][(j + 1)] == 0)
					&& ((arrayOfInt5[i][(j + 1)] & 0x1280120) == 0)) {
				arrayOfInt3[k] = i;
				arrayOfInt4[k] = (j + 1);
				k = (k + 1) % i1;
				arrayOfInt1[i][(j + 1)] = 4;
				arrayOfInt2[i][(j + 1)] = i2;
			}
			if ((i < 103) && (arrayOfInt1[(i + 1)][j] == 0)
					&& ((arrayOfInt5[(i + 1)][j] & 0x1280180) == 0)) {
				arrayOfInt3[k] = (i + 1);
				arrayOfInt4[k] = j;
				k = (k + 1) % i1;
				arrayOfInt1[(i + 1)][j] = 8;
				arrayOfInt2[(i + 1)][j] = i2;
			}
			if ((i > 0) && (j > 0) && (arrayOfInt1[(i - 1)][(j - 1)] == 0)
					&& ((arrayOfInt5[(i - 1)][(j - 1)] & 0x128010E) == 0)
					&& ((arrayOfInt5[(i - 1)][j] & 0x1280108) == 0)
					&& ((arrayOfInt5[i][(j - 1)] & 0x1280102) == 0)) {
				arrayOfInt3[k] = (i - 1);
				arrayOfInt4[k] = (j - 1);
				k = (k + 1) % i1;
				arrayOfInt1[(i - 1)][(j - 1)] = 3;
				arrayOfInt2[(i - 1)][(j - 1)] = i2;
			}
			if ((i > 0) && (j < 103) && (arrayOfInt1[(i - 1)][(j + 1)] == 0)
					&& ((arrayOfInt5[(i - 1)][(j + 1)] & 0x1280138) == 0)
					&& ((arrayOfInt5[(i - 1)][j] & 0x1280108) == 0)
					&& ((arrayOfInt5[i][(j + 1)] & 0x1280120) == 0)) {
				arrayOfInt3[k] = (i - 1);
				arrayOfInt4[k] = (j + 1);
				k = (k + 1) % i1;
				arrayOfInt1[(i - 1)][(j + 1)] = 6;
				arrayOfInt2[(i - 1)][(j + 1)] = i2;
			}
			if ((i < 103) && (j > 0) && (arrayOfInt1[(i + 1)][(j - 1)] == 0)
					&& ((arrayOfInt5[(i + 1)][(j - 1)] & 0x1280183) == 0)
					&& ((arrayOfInt5[(i + 1)][j] & 0x1280180) == 0)
					&& ((arrayOfInt5[i][(j - 1)] & 0x1280102) == 0)) {
				arrayOfInt3[k] = (i + 1);
				arrayOfInt4[k] = (j - 1);
				k = (k + 1) % i1;
				arrayOfInt1[(i + 1)][(j - 1)] = 9;
				arrayOfInt2[(i + 1)][(j - 1)] = i2;
			}
			if ((i < 103) && (j < 103) && (arrayOfInt1[(i + 1)][(j + 1)] == 0)
					&& ((arrayOfInt5[(i + 1)][(j + 1)] & 0x12801E0) == 0)
					&& ((arrayOfInt5[(i + 1)][j] & 0x1280180) == 0)
					&& ((arrayOfInt5[i][(j + 1)] & 0x1280120) == 0)) {
				arrayOfInt3[k] = (i + 1);
				arrayOfInt4[k] = (j + 1);
				k = (k + 1) % i1;
				arrayOfInt1[(i + 1)][(j + 1)] = 12;
				arrayOfInt2[(i + 1)][(j + 1)] = i2;
			}
		}
		if (n != 0) {
			return arrayOfInt2[i][j];
		}
		return -1;
	}

	/**
	 * Checks whether a point is in the game screen. excludes any points that
	 * are less than 253 pixels from the right of the screen or less than 169
	 * pixels from the bottom of the screen, giving a rough area.
	 * 
	 * @param p
	 *            The point to check.
	 * @return <tt>true</tt> if the point is within the rectangle; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean pointOnScreen(final Point p) {
		final int x = p.x, y = p.y;
		return x > 4 && x < Bot.getCanvas().getWidth() - 253 && y > 4
		&& y < Bot.getCanvas().getHeight() - 169;
	}

	/**
	 * Returns the angle to a given tile in degrees anti-clockwise from the
	 * positive x axis (where the x-axis is from west to east).
	 * 
	 * @param t
	 *            The target tile
	 * @return The angle in degrees
	 */
	public int toTile(final RSTile t) {
		final RSTile me = methods.player.getMine().getLocation();
		final int angle = (int) Math.toDegrees(Math.atan2(t.getY() - me.getY(),
				t.getX() - me.getX()));
		return angle >= 0 ? angle : 360 + angle;
	}

	public Point worldToMinimap(double tileX, double tileY) {
		tileX -= Bot.getClient().getBaseX();
		tileY -= Bot.getClient().getBaseY();
		final int i = (int) (tileX * 4.0D + 2.0D)
		- Bot.getClient().getMyRSPlayer().getX() / 128;
		final int j = (int) (tileY * 4.0D + 2.0D)
		- Bot.getClient().getMyRSPlayer().getY() / 128;
		try {
			final org.rsbot.client.RSInterface localface = Bot.methods.screen
			.getMinimapInterface();
			if (localface == null) {
				return new Point(-1, -1);
			}
			final RSInterfaceChild localChild = methods.iface.getChild(localface
					.getID());

			final int k = i * i + j * j;

			final int m = 10 + Math.max(localChild.getWidth() / 2,
					localChild.getHeight() / 2);
			if (m * m >= k) {
				int n = 0x3FFF & (int) Bot.getClient().getMinimapOffset();
				if (Bot.getClient().getMinimapSetting() != 4) {
					n = 0x3FFF & (int) Bot.getClient().getMinimapAngle()
					+ Bot.getClient().getMinimapOffset();
				}

				int i1 = CURVESIN[n];
				int i2 = CURVECOS[n];
				if (Bot.getClient().getMinimapSetting() != 4) {
					final int i3 = 256 + Bot.getClient().getMinimapScale();
					i1 = 256 * i1 / i3;
					i2 = 256 * i2 / i3;
				}

				final int i3 = i2 * i + i1 * j >> 15;
			final int i4 = i2 * j - i1 * i >> 15;

			final int i5 = i3 + localChild.getAbsoluteX() + localChild.getWidth()
			/ 2;
			final int i6 = -i4 + localChild.getAbsoluteY()
			+ localChild.getHeight() / 2;

			if ((Math.max(i4, -i4) <= localChild.getWidth() / 2.0D * 0.8D)
					&& (Math.max(i3, -i3) <= localChild.getHeight() / 2 * 0.8D)) {
				return new Point(i5, i6);
			}
			return new Point(-1, -1);
			}
		} catch (final NullPointerException localNullPointerException) {
		}
		return new Point(-1, -1);
	}
}
