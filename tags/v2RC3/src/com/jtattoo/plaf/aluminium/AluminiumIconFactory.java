/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import javax.swing.Icon;

import com.jtattoo.plaf.AbstractIconFactory;
import com.jtattoo.plaf.BaseIcons;

/**
 * @author Michael Hagen
 */
public class AluminiumIconFactory implements AbstractIconFactory {

	private static AluminiumIconFactory instance = null;

	public static synchronized AluminiumIconFactory getInstance() {
		if (instance == null) {
			instance = new AluminiumIconFactory();
		}
		return instance;
	}

	private AluminiumIconFactory() {
	}

	@Override
	public Icon getCheckBoxIcon() {
		return BaseIcons.getCheckBoxIcon();
	}

	@Override
	public Icon getCloseIcon() {
		return AluminiumIcons.getCloseIcon();
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
		return AluminiumIcons.getIconIcon();
	}

	@Override
	public Icon getLeftArrowIcon() {
		return BaseIcons.getLeftArrowIcon();
	}

	@Override
	public Icon getMaxIcon() {
		return AluminiumIcons.getMaxIcon();
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
		return AluminiumIcons.getMinIcon();
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
		return AluminiumIcons.getSplitterDownArrowIcon();
	}

	@Override
	public Icon getSplitterHorBumpIcon() {
		return BaseIcons.getSplitterHorBumpIcon();
	}

	@Override
	public Icon getSplitterLeftArrowIcon() {
		return AluminiumIcons.getSplitterLeftArrowIcon();
	}

	@Override
	public Icon getSplitterRightArrowIcon() {
		return AluminiumIcons.getSplitterRightArrowIcon();
	}

	@Override
	public Icon getSplitterUpArrowIcon() {
		return AluminiumIcons.getSplitterUpArrowIcon();
	}

	@Override
	public Icon getSplitterVerBumpIcon() {
		return BaseIcons.getSplitterVerBumpIcon();
	}

	@Override
	public Icon getThumbHorIcon() {
		return AluminiumIcons.getThumbHorIcon();
	}

	@Override
	public Icon getThumbHorIconRollover() {
		return AluminiumIcons.getThumbHorIconRollover();
	}

	@Override
	public Icon getThumbVerIcon() {
		return AluminiumIcons.getThumbVerIcon();
	}

	@Override
	public Icon getThumbVerIconRollover() {
		return AluminiumIcons.getThumbVerIconRollover();
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
