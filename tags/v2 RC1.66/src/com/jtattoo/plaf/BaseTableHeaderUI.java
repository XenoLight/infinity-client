/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * 
 * @author Michael Hagen
 */
public class BaseTableHeaderUI extends BasicTableHeaderUI {

	public static ComponentUI createUI(final JComponent c) {
		return new BaseTableHeaderUI();
	}
	protected MouseAdapter myMouseAdapter = null;
	protected MouseMotionAdapter myMouseMotionAdapter = null;

	protected int rolloverCol = -1;

	private Dimension createHeaderSize(long width) {
		// None of the callers include the intercell spacing, do it here.
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, getHeaderHeight());
	}

	private int getHeaderHeight() {
		int height = 0;
		boolean accomodatedDefault = false;
		final TableColumnModel columnModel = header.getColumnModel();
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			final TableColumn aColumn = columnModel.getColumn(column);
			// Configuring the header renderer to calculate its preferred size
			// is expensive.
			// Optimise this by assuming the default renderer always has the
			// same height.
			if (aColumn.getHeaderRenderer() != null || !accomodatedDefault) {
				final Component comp = getHeaderRenderer(column);
				final int rendererHeight = comp.getPreferredSize().height;
				height = Math.max(height, rendererHeight);
				// If the header value is empty (== "") in the
				// first column (and this column is set up
				// to use the default renderer) we will
				// return zero from this routine and the header
				// will disappear altogether. Avoiding the calculation
				// of the preferred size is such a performance win for
				// most applications that we will continue to
				// use this cheaper calculation, handling these
				// issues as `edge cases'.
				if (rendererHeight > 0) {
					accomodatedDefault = true;
				}
			}
		}
		return height + 2;
	}

	private Component getHeaderRenderer(final int col) {
		final TableColumn tabCol = header.getColumnModel().getColumn(col);
		TableCellRenderer renderer = tabCol.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}
		return renderer.getTableCellRendererComponent(header.getTable(),
				tabCol.getHeaderValue(), false, false, -1, col);
	}

	/**
	 * Return the preferred size of the header. The preferred height is the
	 * maximum of the preferred heights of all of the components provided by the
	 * header renderers. The preferred width is the sum of the preferred widths
	 * of each column (plus inter-cell spacing).
	 */
	@Override
	public Dimension getPreferredSize(final JComponent c) {
		long width = 0;
		final Enumeration enumeration = header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			final TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return createHeaderSize(width);
	}

	@Override
	public void installListeners() {
		super.installListeners();
		myMouseAdapter = new MouseAdapter() {

			@Override
			public void mouseEntered(final MouseEvent e) {
				if (!header.getReorderingAllowed()) {
					return;
				}
				rolloverCol = header.getColumnModel().getColumnIndexAtX(
						e.getX());
				header.repaint();
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				if (!header.getReorderingAllowed()) {
					return;
				}
				rolloverCol = -1;
				header.repaint();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (!header.getReorderingAllowed()) {
					return;
				}
				if (header.getBounds().contains(e.getPoint())) {
					rolloverCol = header.getColumnModel().getColumnIndexAtX(
							e.getX());
					header.repaint();
				} else {
					rolloverCol = -1;
					header.repaint();
				}
			}
		};
		myMouseMotionAdapter = new MouseMotionAdapter() {

			@Override
			public void mouseDragged(final MouseEvent e) {
				if (!header.getReorderingAllowed()) {
					return;
				}
				if (header.getDraggedColumn() != null) {
					try {
						rolloverCol = header.getColumnModel().getColumnIndex(
								header.getDraggedColumn().getIdentifier());
					} catch (final Exception ex) {
					}
				} else if (header.getResizingColumn() != null) {
					rolloverCol = -1;
				}
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
				if (!header.getReorderingAllowed()) {
					return;
				}
				if (header.getDraggedColumn() == null) {
					rolloverCol = header.getColumnModel().getColumnIndexAtX(
							e.getX());
					header.repaint();
				}
			}
		};
		header.addMouseListener(myMouseAdapter);
		header.addMouseMotionListener(myMouseMotionAdapter);
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		if (header.getColumnModel().getColumnCount() <= 0) {
			return;
		}
		final boolean ltr = header.getComponentOrientation().isLeftToRight();

		final Rectangle clip = g.getClipBounds();
		final Point left = clip.getLocation();
		final Point right = new Point(clip.x + clip.width - 1, clip.y);
		final TableColumnModel cm = header.getColumnModel();
		int cMin = header.columnAtPoint(ltr ? left : right);
		int cMax = header.columnAtPoint(ltr ? right : left);
		// This should never happen.
		if (cMin == -1) {
			cMin = 0;
		}
		// If the table does not have enough columns to fill the view we'll get
		// -1.
		// Replace this with the index of the last column.
		if (cMax == -1) {
			cMax = cm.getColumnCount() - 1;
		}

		final TableColumn draggedColumn = header.getDraggedColumn();
		final Rectangle cellRect = header.getHeaderRect(ltr ? cMin : cMax);
		int columnWidth;
		TableColumn aColumn;
		if (ltr) {
			for (int column = cMin; column <= cMax; column++) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth;
				if (aColumn != draggedColumn) {
					paintCell(g, cellRect, column);
				}
				cellRect.x += columnWidth;
			}
		} else {
			for (int column = cMax; column >= cMin; column--) {
				aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth;
				if (aColumn != draggedColumn) {
					paintCell(g, cellRect, column);
				}
				cellRect.x += columnWidth;
			}
		}

		// Paint the dragged column if we are dragging.
		if (draggedColumn != null) {
			final int draggedColumnIndex = viewIndexForColumn(draggedColumn);
			final Rectangle draggedCellRect = header
			.getHeaderRect(draggedColumnIndex);
			// Draw a gray well in place of the moving column.
			g.setColor(header.getParent().getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);
			draggedCellRect.x += header.getDraggedDistance();

			// Fill the background.
			g.setColor(header.getBackground());
			g.fillRect(draggedCellRect.x, draggedCellRect.y,
					draggedCellRect.width, draggedCellRect.height);
			paintCell(g, draggedCellRect, draggedColumnIndex);
		}

		// Remove all components in the rendererPane.
		rendererPane.removeAll();
	}

	protected void paintBackground(final Graphics g, final Rectangle cellRect, final int col) {
		final Component component = getHeaderRenderer(col);
		final int x = cellRect.x;
		final int y = cellRect.y;
		final int w = cellRect.width;
		final int h = cellRect.height;
		if (col == rolloverCol && component.isEnabled()) {
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme()
					.getRolloverColors(), x, y, w, h);
		} else if (JTattooUtilities.isFrameActive(header)) {
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme()
					.getColHeaderColors(), x, y, w, h);
		} else {
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme()
					.getInActiveColors(), x, y, w, h);
		}
	}

	protected void paintCell(final Graphics g, final Rectangle cellRect, final int col) {
		final Component component = getHeaderRenderer(col);
		paintBackground(g, cellRect, col);
		rendererPane.paintComponent(g, component, header, cellRect.x,
				cellRect.y, cellRect.width, cellRect.height, true);
	}

	@Override
	public void uninstallListeners() {
		header.removeMouseListener(myMouseAdapter);
		header.removeMouseMotionListener(myMouseMotionAdapter);
		super.uninstallListeners();
	}

	private int viewIndexForColumn(final TableColumn aColumn) {
		final TableColumnModel cm = header.getColumnModel();
		for (int column = 0; column < cm.getColumnCount(); column++) {
			if (cm.getColumn(column) == aColumn) {
				return column;
			}
		}
		return -1;
	}
}
