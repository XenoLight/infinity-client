/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import javax.swing.border.Border;

/**
 * @author Michael Hagen
 */
public interface AbstractBorderFactory {

	public Border getButtonBorder();

	public Border getComboBoxBorder();

	public Border getDesktopIconBorder();

	public Border getInternalFrameBorder();

	public Border getMenuBarBorder();

	public Border getMenuItemBorder();

	public Border getPaletteBorder();

	public Border getPopupMenuBorder();

	public Border getProgressBarBorder();

	public Border getScrollPaneBorder();

	public Border getSpinnerBorder();

	public Border getTabbedPaneBorder();

	public Border getTableHeaderBorder();

	public Border getTextBorder();

	public Border getTextFieldBorder();

	public Border getToggleButtonBorder();

	public Border getToolBarBorder();
}
