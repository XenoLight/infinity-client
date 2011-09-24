/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.hifi;

import javax.swing.Icon;

import com.jtattoo.plaf.AbstractIconFactory;
import com.jtattoo.plaf.BaseIcons;

/**
 * @author Michael Hagen
 */
public class HiFiIconFactory implements AbstractIconFactory {

	private static HiFiIconFactory instance = null;

	public static synchronized HiFiIconFactory getInstance() {
		if (instance == null) {
			instance = new HiFiIconFactory();
		}
		return instance;
	}

	private HiFiIconFactory() {
	}

	@Override
	public Icon getCheckBoxIcon() {
		return HiFiIcons.getCheckBoxIcon();
	}

	@Override
	public Icon getCloseIcon() {
		return HiFiIcons.getCloseIcon();
	}

	@Override
	public Icon getComboBoxIcon() {
		return HiFiIcons.getComboBoxIcon();
	}

	@Override
	public Icon getDownArrowIcon() {
		return HiFiIcons.getDownArrowIcon();
	}

	@Override
	public Icon getFileChooserDetailViewIcon() {
		return BaseIcons.getFileChooserDetailViewIcon();
	}

	@Override
	public Icon getFileChooserHomeFolderIcon() {
		return BaseIcons.getFileChooserHomeFolderIcon();
	}

	@Override
	public Icon getFileChooserListViewIcon() {
		return BaseIcons.getFileChooserListViewIcon();
	}

	@Override
	public Icon getFileChooserNewFolderIcon() {
		return BaseIcons.getFileChooserNewFolderIcon();
	}

	@Override
	public Icon getFileChooserUpFolderIcon() {
		return BaseIcons.getFileChooserUpFolderIcon();
	}

	@Override
	public Icon getIconIcon() {
		return HiFiIcons.getIconIcon();
	}

	@Override
	public Icon getLeftArrowIcon() {
		return HiFiIcons.getLeftArrowIcon();
	}

	@Override
	public Icon getMaxIcon() {
		return HiFiIcons.getMaxIcon();
	}

	@Override
	public Icon getMenuArrowIcon() {
		return HiFiIcons.getMenuArrowIcon();
	}

	@Override
	public Icon getMenuCheckBoxIcon() {
		return BaseIcons.getMenuCheckBoxIcon();
	}

	@Override
	public Icon getMenuIcon() {
		return BaseIcons.getMenuIcon();
	}

	@Override
	public Icon getMenuRadioButtonIcon() {
		return BaseIcons.getMenuRadioButtonIcon();
	}

	@Override
	public Icon getMinIcon() {
		return HiFiIcons.getMinIcon();
	}

	@Override
	public Icon getOptionPaneErrorIcon() {
		return HiFiIcons.getOptionPaneErrorIcon();
	}

	@Override
	public Icon getOptionPaneInformationIcon() {
		return HiFiIcons.getOptionPaneInformationIcon();
	}

	@Override
	public Icon getOptionPaneQuestionIcon() {
		return HiFiIcons.getOptionPaneQuestionIcon();
	}

	@Override
	public Icon getOptionPaneWarningIcon() {
		return HiFiIcons.getOptionPaneWarningIcon();
	}

	@Override
	public Icon getPaletteCloseIcon() {
		return BaseIcons.getPaletteCloseIcon();
	}

	@Override
	public Icon getRadioButtonIcon() {
		return HiFiIcons.getRadioButtonIcon();
	}

	@Override
	public Icon getRightArrowIcon() {
		return HiFiIcons.getRightArrowIcon();
	}

	@Override
	public Icon getSplitterDownArrowIcon() {
		return HiFiIcons.getSplitterDownArrowIcon();
	}

	@Override
	public Icon getSplitterHorBumpIcon() {
		return HiFiIcons.getSplitterHorBumpIcon();
	}

	@Override
	public Icon getSplitterLeftArrowIcon() {
		return HiFiIcons.getSplitterLeftArrowIcon();
	}

	@Override
	public Icon getSplitterRightArrowIcon() {
		return HiFiIcons.getSplitterRightArrowIcon();
	}

	@Override
	public Icon getSplitterUpArrowIcon() {
		return HiFiIcons.getSplitterUpArrowIcon();
	}

	@Override
	public Icon getSplitterVerBumpIcon() {
		return HiFiIcons.getSplitterVerBumpIcon();
	}

	@Override
	public Icon getThumbHorIcon() {
		return HiFiIcons.getThumbHorIcon();
	}

	@Override
	public Icon getThumbHorIconRollover() {
		return HiFiIcons.getThumbHorIconRollover();
	}

	@Override
	public Icon getThumbVerIcon() {
		return HiFiIcons.getThumbVerIcon();
	}

	@Override
	public Icon getThumbVerIconRollover() {
		return HiFiIcons.getThumbVerIconRollover();
	}

	@Override
	public Icon getTreeCollapsedIcon() {
		return HiFiIcons.getTreeControlIcon(true);
	}

	@Override
	public Icon getTreeComputerIcon() {
		return BaseIcons.getTreeComputerIcon();
	}

	@Override
	public Icon getTreeExpandedIcon() {
		return HiFiIcons.getTreeControlIcon(false);
	}

	@Override
	public Icon getTreeFloppyDriveIcon() {
		return BaseIcons.getTreeFloppyDriveIcon();
	}

	@Override
	public Icon getTreeFolderIcon() {
		return BaseIcons.getTreeFolderIcon();
	}

	@Override
	public Icon getTreeHardDriveIcon() {
		return BaseIcons.getTreeHardDriveIcon();
	}

	@Override
	public Icon getTreeLeafIcon() {
		return BaseIcons.getTreeLeafIcon();
	}

	@Override
	public Icon getUpArrowIcon() {
		return HiFiIcons.getUpArrowIcon();
	}
}
