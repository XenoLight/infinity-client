/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import javax.swing.border.Border;

import com.jtattoo.plaf.AbstractBorderFactory;
import com.jtattoo.plaf.BaseBorders;

/**
 * @author Michael Hagen
 */
public class HiFiBorderFactory implements AbstractBorderFactory {

	private static HiFiBorderFactory instance = null;

	public static synchronized HiFiBorderFactory getInstance() {
		if (instance == null) {
			instance = new HiFiBorderFactory();
		}
		return instance;
	}

	private HiFiBorderFactory() {
	}

	@Override
	public Border getButtonBorder() {
		return HiFiBorders.getButtonBorder();
	}

	@Override
	public Border getComboBoxBorder() {
		return BaseBorders.getComboBoxBorder();
	}

	@Override
	public Border getDesktopIconBorder() {
		return BaseBorders.getDesktopIconBorder();
	}

	@Override
	public Border getInternalFrameBorder() {
		return HiFiBorders.getInternalFrameBorder();
	}

	@Override
	public Border getMenuBarBorder() {
		return BaseBorders.getMenuBarBorder();
	}

	@Override
	public Border getMenuItemBorder() {
		return BaseBorders.getMenuItemBorder();
	}

	@Override
	public Border getPaletteBorder() {
		return BaseBorders.getPaletteBorder();
	}

	@Override
	public Border getPopupMenuBorder() {
		return BaseBorders.getPopupMenuBorder();
	}

	@Override
	public Border getProgressBarBorder() {
		return BaseBorders.getProgressBarBorder();
	}

	@Override
	public Border getScrollPaneBorder() {
		return HiFiBorders.getScrollPaneBorder();
	}

	@Override
	public Border getSpinnerBorder() {
		return BaseBorders.getSpinnerBorder();
	}

	@Override
	public Border getTabbedPaneBorder() {
		return BaseBorders.getTabbedPaneBorder();
	}

	@Override
	public Border getTableHeaderBorder() {
		return BaseBorders.getTableHeaderBorder();
	}

	@Override
	public Border getTextBorder() {
		return BaseBorders.getTextBorder();
	}

	@Override
	public Border getTextFieldBorder() {
		return BaseBorders.getTextFieldBorder();
	}

	@Override
	public Border getToggleButtonBorder() {
		return HiFiBorders.getToggleButtonBorder();
	}

	@Override
	public Border getToolBarBorder() {
		return HiFiBorders.getToolBarBorder();
	}
}
