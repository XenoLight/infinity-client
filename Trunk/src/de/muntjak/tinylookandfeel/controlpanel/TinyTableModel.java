/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTable;

import de.muntjak.tinylookandfeel.table.SortableTableData;

/**
 * An example table model implementing SortableTableData. See
 * de.muntjak.tinylookandfeel.controlpanel.NonSortableTableModel for details
 * about the data structure.
 * 
 * @author Hans Bickel
 * 
 */
public class TinyTableModel extends NonSortableTableModel implements
SortableTableData {

	public TinyTableModel() {
		super(true);
	}

	// SortableTableData implementation
	@Override
	public boolean isColumnSortable(final int column) {
		// Note: For TinyTableModel this works fine. You may
		// take another approach depending on your kind of data.
		if (data.isEmpty())
			return false;

		// value at column #1 is of type Icon (which doesn't implement
		// Comparable)
		final Object val = getValueAt(0, column);
		return (val instanceof Comparable) || (val instanceof Boolean);
	}

	@Override
	public void sortColumns(final int[] columns, final int[] sortingDirections,
			final JTable table) {
		// Prerequisites for restoring the selection
		final int[] sr = table.getSelectedRows();
		final int[] sc = table.getSelectedColumns();
		int rowIndex = 0;

		Iterator ii = data.iterator();
		while (ii.hasNext()) {
			((Record) ii.next()).setOldRow(rowIndex++);
		}

		// The sorting part...
		if (columns.length == 0) {
			// The natural order of our data depends on first (Integer) column
			Collections.sort(data, new Comparator() {
				@Override
				public int compare(final Object o1, final Object o2) {
					// For our data we know that arguments are
					// non-null and are of type Record.
					final Record r1 = (Record) o1;
					final Record r2 = (Record) o2;
					final Comparable val1 = (Comparable) r1.getValueAt(0);
					final Comparable val2 = (Comparable) r2.getValueAt(0);

					return val1.compareTo(val2);
				}
			});
		} else {
			// Multi column sort
			Collections.sort(data, new Comparator() {
				@Override
				public int compare(final Object o1, final Object o2) {
					// For our data we know that arguments are
					// non-null and are of type Record.
					final Record r1 = (Record) o1;
					final Record r2 = (Record) o2;

					for (int i = 0; i < columns.length; i++) {
						final Object v1 = r1.getValueAt(columns[i]);
						final Object v2 = r2.getValueAt(columns[i]);
						int result = 0;

						if (v1 instanceof Comparable) {
							result = ((Comparable) v1).compareTo(v2);
						} else if (v1 instanceof Boolean) {
							if (!v1.equals(v2)) {
								if (Boolean.TRUE.equals(v1)) {
									result = 1;
								} else {
									result = -1;
								}
							}
						}

						if (result != 0) {
							if (sortingDirections[i] == SORT_DESCENDING) {
								return -result;
							}

							return result;
						}
					}

					return 0;
				}
			});
		}

		// Tell our listeners that data has changed
		fireTableDataChanged();

		// Restore selection
		rowIndex = 0;

		ii = data.iterator();
		while (ii.hasNext()) {
			((Record) ii.next()).setNewRow(rowIndex++);
		}

		final Vector temp = (Vector) data.clone();
		Collections.sort(temp, new Comparator() {
			@Override
			public int compare(final Object o1, final Object o2) {
				final Record r1 = (Record) o1;
				final Record r2 = (Record) o2;

				if (r1.getOldRow() > r2.getOldRow())
					return 1;

				return -1;
			}
		});

		// Adding one row selection interval after another is
		// probably inefficient.
		for (int i = 0; i < sr.length; i++) {
			final int row = ((Record) temp.get(sr[i])).getNewRow();
			table.addRowSelectionInterval(row, row);
		}

		for (int i = 0; i < sc.length; i++) {
			table.addColumnSelectionInterval(sc[i], sc[i]);
		}
	}

	@Override
	public boolean supportsMultiColumnSort() {
		// We support multi column sort
		return true;
	}
}
