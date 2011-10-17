package org.rsbot.script.wrappers;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.rsbot.bot.Bot;
import org.rsbot.client.Model;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.util.Filter;

/**
 * A screen space model.
 */
public abstract class RSModel {

	public static Filter<RSModel> newInfinityFilter(final short[] infinity) {
		return new Filter<RSModel>() {
			@Override
			public boolean accept(final RSModel m) {
				return Arrays.equals(m.indices1, infinity);
			}
		};
	}

	protected int[] xPoints;
	protected int[] yPoints;
	protected int[] zPoints;

	protected short[] indices1;
	protected short[] indices2;
	protected short[] indices3;
	private Model model = null;

	private final Methods methods = Bot.methods;

	RSModel(final Methods ctx, final Model model) {
		xPoints = model.getXPoints();
		yPoints = model.getYPoints();
		zPoints = model.getZPoints();

		indices1 = model.getIndices1();
		indices2 = model.getIndices2();
		indices3 = model.getIndices3();
		this.model = model;
	}

	/**
	 * Clicks the RSModel and clicks the menu action
	 * 
	 * @param action
	 *            the action to be clicked in the menu
	 * @param option
	 *            the option of the action to be clicked in the menu
	 * @return true if clicked, false if failed.
	 */
	public boolean action(final String action, final String option) {
		try {
			for (int i = 0; i < 10; i++) {
				methods.mouse.move(getPoint());
				if (contains(methods.mouse.getLocation()) || methods.menu.contains(action)) {
					if (methods.menu.action(action, option)) {
						return true;
					}
				}
			}
		} catch (final Exception ignored) {
		}
		return false;
	}

	/**
	 * Clicks the RSModel.
	 * 
	 * @param leftClick
	 *            if true it left clicks.
	 * @return true if clicked.
	 */
	public boolean click(final boolean leftClick) {
		try {
			for (int i = 0; i < 10; i++) {
				methods.mouse.move(getPoint());
				if (contains(methods.mouse.getLocation())) {
					methods.mouse.click(leftClick);
					return true;
				}
			}
		} catch (final Exception ignored) {
		}
		return false;
	}

	/**
	 * @param p
	 *            A point on the screen
	 * @return true if the point is inside the model.
	 */
	private boolean contains(final Point p) {
		if (this == null) {
			return false;
		}

		final Polygon[] triangles = this.getTriangles();
		for (final Polygon poly : triangles) {
			if (poly.contains(p)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if the provided object is an RSModel with the same x, y and
	 * z points as this model. This method compares all of the values in the
	 * three vertex arrays.
	 * 
	 * @return <tt>true</tt> if the provided object is a model with the same
	 *         points as this.
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof RSModel) {
			final RSModel m = (RSModel) o;
			return Arrays.equals(xPoints, m.xPoints)
			&& Arrays.equals(yPoints, m.yPoints)
			&& Arrays.equals(zPoints, m.zPoints);
		}
		return false;
	}

	protected abstract int getLocalX();

	protected abstract int getLocalY();

	/**
	 * This method returns an array of quadruplets where each is the screen
	 * coordinates of one facet/face of the objet's model. Since all facets are
	 * triangular, point 0 and point 3 are the same.
	 * 
	 * @return <tt>Point[][]</tt> An array of sets of four Points representing a
	 *         facet.
	 */
	public Point[][] getModelFacets() {
		if (model == null) {
			return null;
		}

		final Point screenCoords[] = this.getModelPoints();
		if (screenCoords.length == 0 || screenCoords == null)
			return new Point[0][0];
		final int length = model.getIndices3().length;
		final Point facets[][] = new Point[length][4];

		for (int i = 0; i < length; i++) {
			if (model.getIndices1()[i] >= screenCoords.length
					|| model.getIndices2()[i] >= screenCoords.length
					|| model.getIndices3()[i] >= screenCoords.length)
				continue;
			facets[i][0] = screenCoords[model.getIndices1()[i]];
			facets[i][1] = screenCoords[model.getIndices2()[i]];
			facets[i][2] = screenCoords[model.getIndices3()[i]];
			facets[i][3] = screenCoords[model.getIndices1()[i]];
		}

		return facets;
	}

	/**
	 * Gets the model points associated with the object if a model is available.
	 * 
	 * @return <tt>Point[]</tt>
	 */
	public Point[] getModelPoints() {
		final Polygon[] all = this.getTriangles();
		if (all.length == 0 || all == null)
			return new Point[0];
		final ArrayList<Point> list = new ArrayList<Point>();
		for (final Polygon poly : all) {
			for (int j = 0; j < poly.xpoints.length; j++) {
				final Point p = new Point(poly.xpoints[j], poly.ypoints[j]);
				if (methods.calculate.pointOnScreen(p)) {
					list.add(p);
				}
			}
		}
		return list.toArray(new Point[list.size()]);
	}

	/**
	 * Returns a random screen point.
	 * 
	 * @return A screen point, or Point(-1, -1) if the model is not on screen.
	 */
	public Point getPoint() {
		refresh();
		final int len = indices1.length;
		final int sever = methods.random(0, len-1);
		Point point = getPointInRange(sever, len);
		if (point != null) {
			return point;
		}
		point = getPointInRange(0, sever);
		if (point != null) {
			return point;
		}
		return new Point(-1, -1);
	}

	private Point getPointInRange(final int start, final int end) {
		final int locX = getLocalX();
		final int locY = getLocalY();
		final int height = Calculations.tileHeight(locX, locY);
		for (int i = start; i < end; ++i) {
			final Point one = Calculations.worldToScreen(locX + xPoints[indices1[i]],
					locY + zPoints[indices1[i]],
					((height + yPoints[indices1[i]]) * -1) + height);
			int x = -1, y = -1;
			if (one.x >= 0) {
				x = one.x;
				y = one.y;
			}
			final Point two = Calculations.worldToScreen(locX + xPoints[indices2[i]],
					locY + zPoints[indices2[i]],
					((height + yPoints[indices2[i]]) * -1) + height);
			if (two.x >= 0) {
				if (x >= 0) {
					x = (x + two.x) / 2;
					y = (y + two.y) / 2;
				} else {
					x = two.x;
					y = two.y;
				}
			}
			final Point three = Calculations.worldToScreen(locX
					+ xPoints[indices3[i]], locY + zPoints[indices3[i]],
					((height + yPoints[indices3[i]]) * -1) + height);
			if (three.x >= 0) {
				if (x >= 0) {
					x = (x + three.x) / 2;
					y = (y + three.y) / 2;
				} else {
					x = three.x;
					y = three.y;
				}
			}
			if (x >= 0) {
				return new Point(x, y);
			}
		}
		return null;
	}

	/**
	 * Returns an array of triangles containing the screen points of this model.
	 * 
	 * @return The on screen triangles of this model.
	 */
	public Polygon[] getTriangles() {
		refresh();
		if (model == null)
			return new Polygon[0];
		final LinkedList<Polygon> polygons = new LinkedList<Polygon>();
		final int locX = getLocalX();
		final int locY = getLocalY();
		final int height = Calculations.tileHeight(locX, locY);
		// Noobish code is noobish
		for (int i = 0; i < indices1.length; ++i) {
			final Point r = Calculations.worldToScreen(locX + xPoints[indices1[i]],
					locY + zPoints[indices1[i]],
					((height + yPoints[indices1[i]]) * -1) + height);
			final Point b = Calculations.worldToScreen(locX + xPoints[indices2[i]],
					locY + zPoints[indices2[i]],
					((height + yPoints[indices2[i]]) * -1) + height);
			final Point g = Calculations.worldToScreen(locX + xPoints[indices3[i]],
					locY + zPoints[indices3[i]],
					((height + yPoints[indices3[i]]) * -1) + height);
			if (r.x >= 0 && b.x >= 0 && g.x >= 0) {
				// Polygon here :)
				polygons.add(new Polygon(new int[] { r.x, b.x, g.x },
						new int[] { r.y, b.y, g.y }, 3));
			}
		}
		return polygons.toArray(new Polygon[polygons.size()]);
	}

	/**
	 * Moves the mouse onto the RSModel.
	 */
	public void hover() {
		methods.mouse.move(getPoint());
	}

	protected abstract void refresh();

}
