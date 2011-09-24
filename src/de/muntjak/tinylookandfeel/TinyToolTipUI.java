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
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToolTipUI;

/**
 * TinyToolTipUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyToolTipUI extends MetalToolTipUI {

	protected static TinyToolTipUI sharedInstance = new TinyToolTipUI();

	public static ComponentUI createUI(final JComponent list) {
		return sharedInstance;
	}

	@Override
	protected void installDefaults(final JComponent c) {
		super.installDefaults(c);
	}
}
