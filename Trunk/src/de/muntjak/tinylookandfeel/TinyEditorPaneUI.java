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
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;

/**
 * TinyEditorPaneUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinyEditorPaneUI extends BasicEditorPaneUI {

	public static ComponentUI createUI(final JComponent c) {
		return new TinyEditorPaneUI();
	}

	JTextComponent editor;

	@Override
	protected void installDefaults() {
		super.installDefaults();
	}

	@Override
	public void installUI(final JComponent c) {
		if (c instanceof JTextComponent) {
			editor = (JTextComponent) c;
		}

		super.installUI(c);
	}
}
