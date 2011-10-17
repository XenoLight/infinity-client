/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import javax.swing.Icon;

import com.jtattoo.plaf.AbstractIconFactory;
import com.jtattoo.plaf.BaseIcons;

/**
 * @author Michael Hagen
 */
public class AeroIconFactory implements AbstractIconFactory {
	private static AeroIconFactory instance = null;

	public static synchronized AeroIconFactory getInstance() {
		if (instance == null)
			instance = new AeroIconFactory();
		return instance;
	}

	private AeroIconFactory() {
	}

	@Override
	public Icon getCheckBoxIcon() {
		return BaseIcons.getCheckBoxIcon();
	}

	@Override
	public Icon getCloseIcon() {
		return AeroIcons.getCloseIcon();
	}

	@Override
	public Icon getComboBoxIcon() {
		return BaseIcons.getComboBoxIcon();
	}

	@Override
	public Icon getDownArrowIcon() {
		return BaseIcons.getDownArrowIcon();
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
		return AeroIcons.getIconIcon();
	}

	@Override
	public Icon getLeftArrowIcon() {
		return BaseIcons.getLeftArrowIcon();
	}

	@Override
	public Icon getMaxIcon() {
		return AeroIcons.getMaxIcon();
	}

	@Override
	public Icon getMenuArrowIcon() {
		return BaseIcons.getMenuArrowIcon();
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
		return AeroIcons.getMinIcon();
	}

	@Override
	public Icon getOptionPaneErrorIcon() {
		return BaseIcons.getOptionPaneErrorIcon();
	}

	@Override
	public Icon getOptionPaneInformationIcon() {
		return BaseIcons.getOptionPaneInformationIcon();
	}

	@Override
	public Icon getOptionPaneQuestionIcon() {
		return BaseIcons.getOptionPaneQuestionIcon();
	}

	@Override
	public Icon getOptionPaneWarningIcon() {
		return BaseIcons.getOptionPaneWarningIcon();
	}

	@Override
	public Icon getPaletteCloseIcon() {
		return BaseIcons.getPaletteCloseIcon();
	}

	@Override
	public Icon getRadioButtonIcon() {
		return BaseIcons.getRadioButtonIcon();
	}

	@Override
	public Icon getRightArrowIcon() {
		return BaseIcons.getRightArrowIcon();
	}

	@Override
	public Icon getSplitterDownArrowIcon() {
		return BaseIcons.getSplitterDownArrowIcon();
	}

	@Override
	public Icon getSplitterHorBumpIcon() {
		return BaseIcons.getSplitterHorBumpIcon();
	}

	@Override
	public Icon getSplitterLeftArrowIcon() {
		return BaseIcons.getSplitterLeftArrowIcon();
	}

	@Override
	public Icon getSplitterRightArrowIcon() {
		return BaseIcons.getSplitterRightArrowIcon();
	}

	@Override
	public Icon getSplitterUpArrowIcon() {
		return BaseIcons.getSplitterUpArrowIcon();
	}

	@Override
	public Icon getSplitterVerBumpIcon() {
		return BaseIcons.getSplitterVerBumpIcon();
	}

	@Override
	public Icon getThumbHorIcon() {
		return BaseIcons.getThumbHorIcon();
	}

	@Override
	public Icon getThumbHorIconRollover() {
		return BaseIcons.getThumbHorIconRollover();
	}

	@Override
	public Icon getThumbVerIcon() {
		return BaseIcons.getThumbVerIcon();
	}

	@Override
	public Icon getThumbVerIconRollover() {
		return BaseIcons.getThumbVerIconRollover();
	}

	@Override
	public Icon getTreeCollapsedIcon() {
		return BaseIcons.getTreeControlIcon(true);
	}

	@Override
	public Icon getTreeComputerIcon() {
		return BaseIcons.getTreeComputerIcon();
	}

	@Override
	public Icon getTreeExpandedIcon() {
		return BaseIcons.getTreeControlIcon(false);
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
		return BaseIcons.getUpArrowIcon();
	}
}
