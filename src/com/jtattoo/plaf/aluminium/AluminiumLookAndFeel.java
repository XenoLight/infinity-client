/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.aluminium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.UIDefaults;

import com.jtattoo.plaf.AbstractBorderFactory;
import com.jtattoo.plaf.AbstractIconFactory;
import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.AbstractTheme;
import com.jtattoo.plaf.BaseCheckBoxMenuItemUI;
import com.jtattoo.plaf.BaseComboBoxUI;
import com.jtattoo.plaf.BaseEditorPaneUI;
import com.jtattoo.plaf.BaseFileChooserUI;
import com.jtattoo.plaf.BaseLabelUI;
import com.jtattoo.plaf.BaseMenuItemUI;
import com.jtattoo.plaf.BaseMenuUI;
import com.jtattoo.plaf.BasePopupMenuUI;
import com.jtattoo.plaf.BaseProgressBarUI;
import com.jtattoo.plaf.BaseRadioButtonMenuItemUI;
import com.jtattoo.plaf.BaseScrollBarUI;
import com.jtattoo.plaf.BaseSeparatorUI;
import com.jtattoo.plaf.BaseSpinnerUI;
import com.jtattoo.plaf.BaseTableHeaderUI;
import com.jtattoo.plaf.BaseTableUI;
import com.jtattoo.plaf.BaseTextAreaUI;
import com.jtattoo.plaf.BaseTextFieldUI;
import com.jtattoo.plaf.BaseToolTipUI;
import com.jtattoo.plaf.BaseTreeUI;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AluminiumLookAndFeel extends AbstractLookAndFeel {

	private static AluminiumDefaultTheme myTheme = null;
	private static ArrayList themesList = new ArrayList();
	private static HashMap themesMap = new HashMap();
	private static Properties defaultProps = new Properties();
	private static Properties smallFontProps = new Properties();
	private static Properties largeFontProps = new Properties();
	private static Properties giantFontProps = new Properties();

	static {
		smallFontProps.setProperty("controlTextFont", "Dialog 10");
		smallFontProps.setProperty("systemTextFont", "Dialog 10");
		smallFontProps.setProperty("userTextFont", "Dialog 10");
		smallFontProps.setProperty("menuTextFont", "Dialog 10");
		smallFontProps.setProperty("windowTitleFont", "Dialog bold 10");
		smallFontProps.setProperty("subTextFont", "Dialog 8");

		largeFontProps.setProperty("controlTextFont", "Dialog 14");
		largeFontProps.setProperty("systemTextFont", "Dialog 14");
		largeFontProps.setProperty("userTextFont", "Dialog 14");
		largeFontProps.setProperty("menuTextFont", "Dialog 14");
		largeFontProps.setProperty("windowTitleFont", "Dialog bold 14");
		largeFontProps.setProperty("subTextFont", "Dialog 12");

		giantFontProps.setProperty("controlTextFont", "Dialog 18");
		giantFontProps.setProperty("systemTextFont", "Dialog 18");
		giantFontProps.setProperty("userTextFont", "Dialog 18");
		giantFontProps.setProperty("menuTextFont", "Dialog 18");
		giantFontProps.setProperty("windowTitleFont", "Dialog 18");
		giantFontProps.setProperty("subTextFont", "Dialog 16");

		themesList.add("Default");
		themesList.add("Small-Font");
		themesList.add("Large-Font");
		themesList.add("Giant-Font");

		themesMap.put("Default", defaultProps);
		themesMap.put("Small-Font", smallFontProps);
		themesMap.put("Large-Font", largeFontProps);
		themesMap.put("Giant-Font", giantFontProps);

	}

	public static Properties getThemeProperties(final String name) {
		return ((Properties) themesMap.get(name));
	}

	public static java.util.List getThemes() {
		return themesList;
	}

	public static void setCurrentTheme(final Properties themesProps) {
		setTheme(themesProps);
	}

	public static void setTheme(final Properties themesProps) {
		if (myTheme == null) {
			myTheme = new AluminiumDefaultTheme();
		}
		if ((myTheme != null) && (themesProps != null)) {
			myTheme.setUpColor();
			myTheme.setProperties(themesProps);
			myTheme.setUpColorArrs();
			AbstractLookAndFeel.setTheme(myTheme);
		}
	}

	public static void setTheme(final String name) {
		if (myTheme != null) {
			AbstractTheme.setInternalName(name);
		}
		setTheme((Properties) themesMap.get(name));
	}

	public static void setTheme(final String name, final String licenseKey,
			final String logoString) {
		final Properties props = (Properties) themesMap.get(name);
		props.put("licenseKey", licenseKey);
		props.put("logoString", logoString);
		if (myTheme != null) {
			AbstractTheme.setInternalName(name);
		}
		setTheme(props);
	}

	@Override
	protected void createDefaultTheme() {
		if (myTheme == null) {
			myTheme = new AluminiumDefaultTheme();
		}
		setTheme(myTheme);
	}

	@Override
	public AbstractBorderFactory getBorderFactory() {
		return AluminiumBorderFactory.getInstance();
	}

	@Override
	public String getDescription() {
		return "The Aluminium Look and Feel";
	}

	@Override
	public AbstractIconFactory getIconFactory() {
		return AluminiumIconFactory.getInstance();
	}

	@Override
	public String getID() {
		return "Aluminium";
	}

	@Override
	public String getName() {
		return "Aluminium";
	}

	@Override
	protected void initClassDefaults(final UIDefaults table) {
		super.initClassDefaults(table);
		final Object[] uiDefaults = {
				// BaseLookAndFeel classes
				"LabelUI", BaseLabelUI.class.getName(), "SeparatorUI",
				BaseSeparatorUI.class.getName(), "TextFieldUI",
				BaseTextFieldUI.class.getName(), "TextAreaUI",
				BaseTextAreaUI.class.getName(), "EditorPaneUI",
				BaseEditorPaneUI.class.getName(),
				"ComboBoxUI",
				BaseComboBoxUI.class.getName(),
				"ToolTipUI",
				BaseToolTipUI.class.getName(),
				"TreeUI",
				BaseTreeUI.class.getName(),
				"TableUI",
				BaseTableUI.class.getName(),
				"TableHeaderUI",
				BaseTableHeaderUI.class.getName(),
				"ProgressBarUI",
				BaseProgressBarUI.class.getName(),
				"ScrollBarUI",
				BaseScrollBarUI.class.getName(),
				"FileChooserUI",
				BaseFileChooserUI.class.getName(),
				"MenuUI",
				BaseMenuUI.class.getName(),
				"PopupMenuUI",
				BasePopupMenuUI.class.getName(),
				"MenuItemUI",
				BaseMenuItemUI.class.getName(),
				"CheckBoxMenuItemUI",
				BaseCheckBoxMenuItemUI.class.getName(),
				"RadioButtonMenuItemUI",
				BaseRadioButtonMenuItemUI.class.getName(),
				"PopupMenuSeparatorUI",
				BaseSeparatorUI.class.getName(),
				// AluminiumLookAndFeel classes
				"CheckBoxUI", AluminiumCheckBoxUI.class.getName(),
				"RadioButtonUI", AluminiumRadioButtonUI.class.getName(),
				"ButtonUI", AluminiumButtonUI.class.getName(),
				"ToggleButtonUI", AluminiumToggleButtonUI.class.getName(),
				"SliderUI", AluminiumSliderUI.class.getName(), "PanelUI",
				AluminiumPanelUI.class.getName(), "ScrollPaneUI",
				AluminiumScrollPaneUI.class.getName(), "TabbedPaneUI",
				AluminiumTabbedPaneUI.class.getName(), "SplitPaneUI",
				AluminiumSplitPaneUI.class.getName(), "ToolBarUI",
				AluminiumToolBarUI.class.getName(), "MenuBarUI",
				AluminiumMenuBarUI.class.getName(), "PopupMenuSeparatorUI",
				AluminiumPopupMenuSeparatorUI.class.getName(),
				"InternalFrameUI", AluminiumInternalFrameUI.class.getName(),
				"RootPaneUI", AluminiumRootPaneUI.class.getName(),
				"DesktopPaneUI", AluminiumDesktopPaneUI.class.getName(), };
		table.putDefaults(uiDefaults);
		if (JTattooUtilities.getJavaVersion() >= 1.5) {
			table.put("SpinnerUI", BaseSpinnerUI.class.getName());
		}
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}
}