/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */

package com.jtattoo.plaf.aero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.UIDefaults;

import com.jtattoo.plaf.AbstractBorderFactory;
import com.jtattoo.plaf.AbstractIconFactory;
import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseButtonUI;
import com.jtattoo.plaf.BaseCheckBoxMenuItemUI;
import com.jtattoo.plaf.BaseCheckBoxUI;
import com.jtattoo.plaf.BaseComboBoxUI;
import com.jtattoo.plaf.BaseEditorPaneUI;
import com.jtattoo.plaf.BaseFileChooserUI;
import com.jtattoo.plaf.BaseLabelUI;
import com.jtattoo.plaf.BaseMenuItemUI;
import com.jtattoo.plaf.BaseMenuUI;
import com.jtattoo.plaf.BasePanelUI;
import com.jtattoo.plaf.BasePopupMenuUI;
import com.jtattoo.plaf.BaseProgressBarUI;
import com.jtattoo.plaf.BaseRadioButtonMenuItemUI;
import com.jtattoo.plaf.BaseRadioButtonUI;
import com.jtattoo.plaf.BaseScrollBarUI;
import com.jtattoo.plaf.BaseScrollPaneUI;
import com.jtattoo.plaf.BaseSeparatorUI;
import com.jtattoo.plaf.BaseSliderUI;
import com.jtattoo.plaf.BaseSpinnerUI;
import com.jtattoo.plaf.BaseSplitPaneUI;
import com.jtattoo.plaf.BaseTableHeaderUI;
import com.jtattoo.plaf.BaseTableUI;
import com.jtattoo.plaf.BaseTextAreaUI;
import com.jtattoo.plaf.BaseTextFieldUI;
import com.jtattoo.plaf.BaseToggleButtonUI;
import com.jtattoo.plaf.BaseToolTipUI;
import com.jtattoo.plaf.BaseTreeUI;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * @author Michael Hagen
 */
public class AeroLookAndFeel extends AbstractLookAndFeel {
	private static AeroDefaultTheme myTheme = null;
	private static ArrayList themesList = new ArrayList();
	private static HashMap themesMap = new HashMap();
	private static Properties defaultProps = new Properties();
	private static Properties smallFontProps = new Properties();
	private static Properties largeFontProps = new Properties();
	private static Properties giantFontProps = new Properties();
	private static Properties goldProps = new Properties();
	private static Properties goldSmallFontProps = new Properties();
	private static Properties goldLargeFontProps = new Properties();
	private static Properties goldGiantFontProps = new Properties();
	private static Properties greenProps = new Properties();
	private static Properties greenSmallFontProps = new Properties();
	private static Properties greenLargeFontProps = new Properties();
	private static Properties greenGiantFontProps = new Properties();

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

		goldProps.setProperty("focusCellColor", "160 160 120");
		goldProps.setProperty("selectionBackgroundColor", "232 232 180");
		goldProps.setProperty("rolloverColor", "225 225 159");
		goldProps.setProperty("controlColorLight", "248 248 180");
		goldProps.setProperty("controlColorDark", "200 200 120");
		goldProps.setProperty("windowTitleForegroundColor", "0 0 0");
		goldProps.setProperty("windowTitleBackgroundColor", "248 248 180");
		goldProps.setProperty("windowTitleColorLight", "248 248 180");
		goldProps.setProperty("windowTitleColorDark", "200 200 120");
		goldProps.setProperty("windowBorderColor", "200 200 120");
		goldProps.setProperty("menuSelectionForegroundColor", "0 0 0");
		goldProps.setProperty("menuSelectionBackgroundColor", "232 232 180");

		greenProps.setProperty("focusCellColor", "40 100 60");
		greenProps.setProperty("selectionBackgroundColor", "150 211 176");
		greenProps.setProperty("rolloverColor", "190 228 206");
		greenProps.setProperty("controlColorLight", "150 211 176");
		greenProps.setProperty("controlColorDark", "60 142 95");
		greenProps.setProperty("windowTitleForegroundColor", "255 255 255");
		greenProps.setProperty("windowTitleBackgroundColor", "80 120 100");
		greenProps.setProperty("windowTitleColorLight", "150 211 176");
		greenProps.setProperty("windowTitleColorDark", "60 142 95");
		greenProps.setProperty("windowBorderColor", "60 142 95");
		greenProps.setProperty("menuSelectionForegroundColor", "0 0 0");
		greenProps.setProperty("menuSelectionBackgroundColor", "150 211 176");

		String key = null;
		String value = null;
		Iterator iter = smallFontProps.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			value = smallFontProps.getProperty(key);
			goldSmallFontProps.setProperty(key, value);
			greenSmallFontProps.setProperty(key, value);
		}
		iter = largeFontProps.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			value = largeFontProps.getProperty(key);
			goldLargeFontProps.setProperty(key, value);
			greenLargeFontProps.setProperty(key, value);
		}
		iter = giantFontProps.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			value = giantFontProps.getProperty(key);
			goldGiantFontProps.setProperty(key, value);
			greenGiantFontProps.setProperty(key, value);
		}

		iter = goldProps.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			value = goldProps.getProperty(key);
			goldSmallFontProps.setProperty(key, value);
			goldLargeFontProps.setProperty(key, value);
			goldGiantFontProps.setProperty(key, value);
		}

		iter = greenProps.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			value = greenProps.getProperty(key);
			greenSmallFontProps.setProperty(key, value);
			greenLargeFontProps.setProperty(key, value);
			greenGiantFontProps.setProperty(key, value);
		}

		themesList.add("Default");
		themesList.add("Small-Font");
		themesList.add("Large-Font");
		themesList.add("Giant-Font");

		themesList.add("Gold");
		themesList.add("Gold-Small-Font");
		themesList.add("Gold-Large-Font");
		themesList.add("Gold-Giant-Font");

		themesList.add("Green");
		themesList.add("Green-Small-Font");
		themesList.add("Green-Large-Font");
		themesList.add("Green-Giant-Font");

		themesMap.put("Default", defaultProps);
		themesMap.put("Small-Font", smallFontProps);
		themesMap.put("Large-Font", largeFontProps);
		themesMap.put("Giant-Font", giantFontProps);

		themesMap.put("Gold", goldProps);
		themesMap.put("Gold-Small-Font", goldSmallFontProps);
		themesMap.put("Gold-Large-Font", goldLargeFontProps);
		themesMap.put("Gold-Giant-Font", goldGiantFontProps);

		themesMap.put("Green", greenProps);
		themesMap.put("Green-Small-Font", greenSmallFontProps);
		themesMap.put("Green-Large-Font", greenLargeFontProps);
		themesMap.put("Green-Giant-Font", greenGiantFontProps);
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
		if (myTheme == null)
			myTheme = new AeroDefaultTheme();
		if ((myTheme != null) && (themesProps != null)) {
			myTheme.setUpColor();
			myTheme.setProperties(themesProps);
			myTheme.setUpColorArrs();
			AbstractLookAndFeel.setTheme(myTheme);
		}
	}

	public static void setTheme(final String name) {
		if (myTheme != null)
			myTheme.setInternalName(name);
		setTheme((Properties) themesMap.get(name));
	}

	public static void setTheme(final String name, final String licenseKey,
			final String logoString) {
		final Properties props = (Properties) themesMap.get(name);
		props.put("licenseKey", licenseKey);
		props.put("logoString", logoString);
		if (myTheme != null)
			myTheme.setInternalName(name);
		setTheme(props);
	}

	protected void createDefaultTheme() {
		if (myTheme == null)
			myTheme = new AeroDefaultTheme();
		setTheme(myTheme);
	}

	public AbstractBorderFactory getBorderFactory() {
		return AeroBorderFactory.getInstance();
	}

	public String getDescription() {
		return "The Aero Look and Feel";
	}

	public AbstractIconFactory getIconFactory() {
		return AeroIconFactory.getInstance();
	}

	public String getID() {
		return "Aero";
	}

	public String getName() {
		return "Aero";
	}

	protected void initClassDefaults(final UIDefaults table) {
		super.initClassDefaults(table);
		final Object[] uiDefaults = {
				// BaseLookAndFeel classes
				"LabelUI", BaseLabelUI.class.getName(), "ButtonUI",
				BaseButtonUI.class.getName(), "RadioButtonUI",
				BaseRadioButtonUI.class.getName(), "CheckBoxUI",
				BaseCheckBoxUI.class.getName(), "ToggleButtonUI",
				BaseToggleButtonUI.class.getName(), "SeparatorUI",
				BaseSeparatorUI.class.getName(), "TextFieldUI",
				BaseTextFieldUI.class.getName(), "TextAreaUI",
				BaseTextAreaUI.class.getName(), "EditorPaneUI",
				BaseEditorPaneUI.class.getName(), "ComboBoxUI",
				BaseComboBoxUI.class.getName(), "ToolTipUI",
				BaseToolTipUI.class.getName(), "TreeUI",
				BaseTreeUI.class.getName(), "TableUI",
				BaseTableUI.class.getName(), "TableHeaderUI",
				BaseTableHeaderUI.class.getName(), "ScrollBarUI",
				BaseScrollBarUI.class.getName(), "ScrollPaneUI",
				BaseScrollPaneUI.class.getName(), "ProgressBarUI",
				BaseProgressBarUI.class.getName(), "PanelUI",
				BasePanelUI.class.getName(), "SplitPaneUI",
				BaseSplitPaneUI.class.getName(), "SliderUI",
				BaseSliderUI.class.getName(), "FileChooserUI",
				BaseFileChooserUI.class.getName(),

				"MenuUI", BaseMenuUI.class.getName(), "PopupMenuUI",
				BasePopupMenuUI.class.getName(), "MenuItemUI",
				BaseMenuItemUI.class.getName(),
				"CheckBoxMenuItemUI",
				BaseCheckBoxMenuItemUI.class.getName(),
				"RadioButtonMenuItemUI",
				BaseRadioButtonMenuItemUI.class.getName(),
				"PopupMenuSeparatorUI",
				BaseSeparatorUI.class.getName(),

				// AeroLookAndFeel classes
				"TabbedPaneUI", AeroTabbedPaneUI.class.getName(), "ToolBarUI",
				AeroToolBarUI.class.getName(), "InternalFrameUI",
				AeroInternalFrameUI.class.getName(), "RootPaneUI",
				AeroRootPaneUI.class.getName(), };
		table.putDefaults(uiDefaults);
		if (JTattooUtilities.getJavaVersion() >= 1.5) {
			table.put("SpinnerUI", BaseSpinnerUI.class.getName());
		}
	}

	public boolean isNativeLookAndFeel() {
		return false;
	}

	public boolean isSupportedLookAndFeel() {
		return true;
	}

}