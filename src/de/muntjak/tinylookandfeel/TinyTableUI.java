/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;

/**
 * TinyTableUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyTableUI extends BasicTableUI {

	public static ComponentUI createUI(final JComponent table) {
		return new TinyTableUI(table);
	}

	JTable table;

	public TinyTableUI() {
		super();
	}

	public TinyTableUI(final JComponent table) {
		super();
		this.table = (JTable) table;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
	}
}
