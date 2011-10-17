package org.rsbot.script.wrappers;

import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * @author Waterwolf
 * @version 1.2 1.1: Clean up and getArea method. - Henry 1.2: addTile, size(),
 *          reset and rename of getTiles() - Waterwolf
 */
public class RSPolygon {
	private final Polygon polyTiles = new Polygon();

	/**
	 * @param tiles
	 *            The <b>RSTiles</b> to be added to <b>RSPolygon</b>.
	 */
	public RSPolygon(final RSTile... tiles) {
		for (final RSTile tile : tiles)
			this.polyTiles.addPoint(tile.getX(), tile.getY());
	}

	/**
	 * @param r
	 *            The <b>RSTile</b> to add to <b>RSPolygon</b>
	 */
	public void addTile(final RSTile r) {
		this.polyTiles.addPoint(r.getX(), r.getY());
	}

	/**
	 * @param tile
	 *            The <b>RSTile</b> to check.
	 * @return True if The <b>RSPolygon</b> contains the <b>RSTile</b>.
	 */
	public boolean contains(final RSTile tile) {
		return this.polyTiles.contains(tile.getX(), tile.getY());
	}

	/**
	 * @return <b>RSArea</b> from current <b>RSPolygon</b>
	 */
	public RSArea getArea() {
		return new RSArea(this.polyTiles.getBounds().x + 1,
				this.polyTiles.getBounds().y + 1, this.getWidth(),
				this.getHeight());
	}

	/**
	 * @return The bounding box of the <b>RSPolygon</b>.
	 */
	public Rectangle getBounds() {
		return new Rectangle(this.polyTiles.getBounds().x + 1,
				this.polyTiles.getBounds().y + 1, this.getWidth(),
				this.getHeight());
	}

	/**
	 * @return The distance between the the <b>RSTile</b> that's most
	 *         <i>South</i> and the <b>RSTile</b> that's most <i>North</i>.
	 */
	public int getHeight() {
		return this.polyTiles.getBounds().height;
	}

	/**
	 * @return <b>RSTiles</b> the <b>RSPolygon</b> contains in array.
	 */
	public RSTile[] getTileArray() {
		final RSTile[] ret = new RSTile[this.polyTiles.npoints];
		for (int n = 0; n < this.polyTiles.npoints; n++) {
			ret[n] = new RSTile(this.polyTiles.xpoints[n],
					this.polyTiles.ypoints[n]);
		}
		return ret;
	}

	/**
	 * @deprecated Use getTilesInsidePolygon() instead. (same method but
	 *             different name)
	 */
	@Deprecated
	public RSTile[][] getTiles() {
		return getTilesInsidePolygon();
	}

	/**
	 * @return The <b>RSTiles</b> the area of <b>RSPolygon</b> contains.
	 */
	public RSTile[][] getTilesInsidePolygon() {
		final RSTile[][] tiles = new RSTile[this.getWidth()][this.getHeight()];
		for (int i = 0; i < this.getWidth(); ++i) {
			for (int j = 0; j < this.getHeight(); ++j) {
				if (this.polyTiles.contains(this.getX() + i, this.getY() + j)) {
					tiles[i][j] = new RSTile(this.getX() + i, this.getY() + j);
				}
			}
		}
		return tiles;
	}

	/**
	 * @return The distance between the the <b>RSTile</b> that's most
	 *         <i>East</i> and the <b>RSTile</b> that's most <i>West</i>.
	 */
	public int getWidth() {
		return this.polyTiles.getBounds().width;
	}

	/**
	 * @return The X axle of the <b>RSTile</b> that's most <i>West</i>.
	 */
	public int getX() {
		return this.polyTiles.getBounds().x;
	}

	/**
	 * @return The Y axle of the <b>RSTile</b> that's most <i>South</i>.
	 */
	public int getY() {
		return this.polyTiles.getBounds().y;
	}

	/**
	 * Empty all polygons
	 * 
	 * @return <b>RSTile</b>s before reseting
	 */
	public RSTile[] reset() {
		final RSTile[] tiles = getTileArray();
		this.polyTiles.reset();
		return tiles;
	}

	/**
	 * @return The size of <b>RSPolygon</b>'s tiles
	 */
	public int size() {
		return this.polyTiles.npoints;
	}
}
