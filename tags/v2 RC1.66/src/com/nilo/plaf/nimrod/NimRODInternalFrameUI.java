/*
 *                 (C) Copyright 2005 Nilo J. Gonzalez
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 2 of the Licence, or (at your opinion) any
 * later version.
 * 
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 *
 * Original author: Nilo J. Gonzalez
 */

/**
 * Esta clase implementa los internalframes
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalInternalFrameUI;

public class NimRODInternalFrameUI extends MetalInternalFrameUI {
	public static ComponentUI createUI(final JComponent c) {
		return new NimRODInternalFrameUI((JInternalFrame) c);
	}

	NimRODInternalFrameTitlePane titlePane;

	public NimRODInternalFrameUI(final JInternalFrame arg0) {
		super(arg0);
	}

	@Override
	protected JComponent createNorthPane(final JInternalFrame w) {
		super.createNorthPane(w);

		titlePane = new NimRODInternalFrameTitlePane(w);
		return titlePane;
	}

	@Override
	public void update(final Graphics g, final JComponent c) {
		paint(g, c);
	}

}
