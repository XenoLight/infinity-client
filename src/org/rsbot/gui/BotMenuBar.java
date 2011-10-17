/*
 * 2011 Runedev development team
 * http://lazygamerz.org
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 3 of the Licence, or (at your opinion) any
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
 */
package org.rsbot.gui;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.rsbot.bot.Bot;
import org.rsbot.event.impl.DrawInventory;
import org.rsbot.event.impl.DrawItems;
import org.rsbot.event.impl.DrawModel;
import org.rsbot.event.impl.DrawMouse;
import org.rsbot.event.impl.DrawNPCs;
import org.rsbot.event.impl.DrawObjects;
import org.rsbot.event.impl.DrawPlayers;
import org.rsbot.event.impl.MessageLogger;
import org.rsbot.event.impl.TAnimation;
import org.rsbot.event.impl.TCamera;
import org.rsbot.event.impl.TFPS;
import org.rsbot.event.impl.TFloorHeight;
import org.rsbot.event.impl.TLoginIndex;
import org.rsbot.event.impl.TMenu;
import org.rsbot.event.impl.TMenuActions;
import org.rsbot.event.impl.TMousePosition;
import org.rsbot.event.impl.TOrientation;
import org.rsbot.event.impl.TPlayerPosition;
import org.rsbot.event.impl.TTab;
import org.rsbot.event.impl.TUserInputAllowed;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.util.GlobalConfiguration;

/**
 * @author Runedev development team - version 1.0
 */
public class BotMenuBar extends JMenuBar {

	private static final long serialVersionUID = 971579975301998332L;
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Bot.class.getPackage()
			.getName());
	public static final Map<String, Class<?>> DEBUG_MAP = new LinkedHashMap<String, Class<?>>();
	private static File menuSetting = new File(
			GlobalConfiguration.Paths.getMenuCache());
	private JMenu optionMenu = null;
	public static final String[] TITLES;
	public static final String[][] ELEMENTS;
	public static final String[] TITLES2;
	public static final String[][] ELEMENTS2;
	public static final String[] TITLES3;
	public static final String[][] ELEMENTS3;
	public static final String[] TITLES4;
	public static final String[][] ELEMENTS4;
	public static final String[] TITLES5;
	public static final String[][] ELEMENTS5;
	public static final String[] TITLES6;
	public static final String[][] ELEMENTS6;
	public static final ImageIcon account = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/account.png");
	public static final ImageIcon start = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/play.png");
	public static final ImageIcon stop = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/stop.png");
	public static final ImageIcon pauseico = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/pause.png");
	public static final ImageIcon input = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/tick.png");
	public static final ImageIcon noinput = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/delete.png");
	public static final ImageIcon cpu = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/cpu.png");
	public static final ImageIcon exit = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/gui_close.png");
	public static final ImageIcon shot = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/shot.png");
	public static final ImageIcon bugpen = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/pencil.png");
	public static final ImageIcon help = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/help.png");
	public static final ImageIcon gui = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/gui.png");
	public static final ImageIcon mouse = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/mouse.png");
	public static final ImageIcon home = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/home.png");
	public static final ImageIcon wiki = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/wiki.png");
	public static final ImageIcon face = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/face.png");
	public static final ImageIcon twit = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/twit.png");
	public static final ImageIcon web = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/web.png");

	static {
		System.out.println(String.format("Running from jar: %s", GlobalConfiguration.RUNNING_FROM_JAR));

		/* Debug map Text */
		DEBUG_MAP.put("Game State", TLoginIndex.class);
		DEBUG_MAP.put("Current Tab", TTab.class);
		DEBUG_MAP.put("Camera", TCamera.class);
		DEBUG_MAP.put("Animation", TAnimation.class);
		DEBUG_MAP.put("Floor Height", TFloorHeight.class);
		DEBUG_MAP.put("Player Position", TPlayerPosition.class);
		DEBUG_MAP.put("Orientation", TOrientation.class);
		DEBUG_MAP.put("Mouse Position", TMousePosition.class);
		DEBUG_MAP.put("User Input Allowed", TUserInputAllowed.class);
		DEBUG_MAP.put("Menu Actions", TMenuActions.class);
		DEBUG_MAP.put("Menu", TMenu.class);
		DEBUG_MAP.put("FPS", TFPS.class);
		/* Paint */
		DEBUG_MAP.put("Players", DrawPlayers.class);
		DEBUG_MAP.put("NPCs", DrawNPCs.class);
		DEBUG_MAP.put("Objects", DrawObjects.class);
		DEBUG_MAP.put("Models", DrawModel.class);
		DEBUG_MAP.put("Mouse", DrawMouse.class);
		DEBUG_MAP.put("Inventory", DrawInventory.class);
		DEBUG_MAP.put("Ground Items", DrawItems.class);
		/* Other */
		DEBUG_MAP.put("Log Messages", MessageLogger.class);
		DEBUG_MAP.put("Debug Client", null);
		DEBUG_MAP.put("Debug Script", null);


		/* menu strings */
		TITLES = new String[] { "Client" };
		ELEMENTS = new String[][] { { "Accounts", "-", "Run Script",
			"Stop Script", "Pause Script", "-", "Save ScreenShot",
			"Save ScreenShot (UnCen)", "-", "ToggleF Force Input",
			"ToggleF Less CPU", "ToggleF Disable Game Screen", "-", "Exit" } };
		/* menu string */
		TITLES2 = new String[] { "Anti-Bans" };
		/* drop down menu lists */
		ELEMENTS2 = new String[][] { constructAntibans() };
		/* menu string */
		TITLES3 = new String[] { "Randoms" };
		/* drop down menu lists */
		ELEMENTS3 = new String[][] { constructRandoms() };
		/* menu strings */
		TITLES4 = new String[] { "Close" };
		/* drop down menu lists */
		ELEMENTS4 = new String[][] { constructClose() };
		/* menu strings */
		TITLES5 = new String[] { "Skins" };
		/* drop down menu lists */
		ELEMENTS5 = new String[][] { constructThemes() };
		/* menu strings */
		TITLES6 = new String[] { "Debug", "Help" };
		/* drop down menu lists */
		ELEMENTS6 = new String[][] { constructDebugs(), constrconuctHelp() };
	}

	public static Map<String, JCheckBoxMenuItem> eventCheckMap = new HashMap<String, JCheckBoxMenuItem>();

	public static Map<String, JCheckBoxMenuItem> commandCheckMap = new HashMap<String, JCheckBoxMenuItem>();

	public static Map<String, JMenuItem> commandMenuItem = new HashMap<String, JMenuItem>();

	private static String[] constrconuctHelp() {
		final List<String> helpItems = new ArrayList<String>();
		helpItems.add("Troubleshooting");
		helpItems.add("Site");
		helpItems.add("Forums");
		helpItems.add("Wiki");
		helpItems.add("Facebook");
		helpItems.add("Project");
		helpItems.add("The Client");
		helpItems.add("The Makers");
		return helpItems.toArray(new String[helpItems.size()]);
	}

	private static String[] constructAntibans() {
		final List<String> antibanItems = new ArrayList<String>();
		antibanItems.add("Disable All Anti-Bans");
		antibanItems.add("Disable Auto Login");
		antibanItems.add("Disable Break Handler");

		for (final ListIterator<String> it = antibanItems.listIterator(); it
		.hasNext();) {
			final String s = it.next();
			if (!s.equals("-")) {
				it.set("ToggleF " + s);
			}
		}
		return antibanItems.toArray(new String[antibanItems.size()]);
	}

	private static String[] constructClose() {
		final List<String> hideItems = new ArrayList<String>();
		hideItems.add("Toolbar");
		hideItems.add("Option Buttons");
		hideItems.add("Log");
		for (final ListIterator<String> it = hideItems.listIterator(); it.hasNext();) {
			final String s = it.next();
			if (!s.equals("-")) {
				it.set("ToggleF " + s);
			}
		}
		return hideItems.toArray(new String[hideItems.size()]);
	}

	private static String[] constructDebugs() {
		final List<String> debugItems = new ArrayList<String>();
		debugItems.add("All Debugging");
		debugItems.add("-");

		/* Add the Paint Listeners */
		for (final String key : DEBUG_MAP.keySet()) {
			final Class<?> el = DEBUG_MAP.get(key);
			if (el != null && PaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}

		/* Add the Text Listeners */
		debugItems.add("-");
		for (final String key : DEBUG_MAP.keySet()) {
			final Class<?> el = DEBUG_MAP.get(key);
			if (el != null && TextPaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}

		/* Add the non-Text/Paint Listeners, like Log Messages and Client Debug */
		debugItems.add("-");
		for (final String key : DEBUG_MAP.keySet()) {
			final Class<?> el = DEBUG_MAP.get(key);
			if (el == null || !(TextPaintListener.class.isAssignableFrom(el))
					&& !(PaintListener.class.isAssignableFrom(el))) {
				debugItems.add(key);
			}
		}

		for (final ListIterator<String> it = debugItems.listIterator(); it.hasNext();) {
			final String s = it.next();
			if (!s.equals("-")) {
				it.set("ToggleF " + s);
			}
		}

		return debugItems.toArray(new String[debugItems.size()]);
	}

	private static String[] constructRandoms() {
		final List<String> randomItems = new ArrayList<String>();
		randomItems.add("Disable All Random Solving");
		randomItems.add("Disable Bee Hive");
		randomItems.add("Disable Capn Arnav");
		randomItems.add("Disable Certer");
		randomItems.add("Disable Drill Demon");
		randomItems.add("Disable Exam");
		randomItems.add("Disable First Time Death");
		randomItems.add("Disable Freaky Forester");
		randomItems.add("Disable Frog Cave");
		randomItems.add("Disable Grave Digger");
		randomItems.add("Disable Leave Safe Area");
		randomItems.add("Disable Lost And Found");
		randomItems.add("Disable Maze");
		randomItems.add("Disable Mime");
		randomItems.add("Disable Molly");
		randomItems.add("Disable Pillory");
		randomItems.add("Disable Pinball");
		randomItems.add("Disable Prison");
		randomItems.add("Disable Quiz Solver");
		randomItems.add("Disable Sandwhich Lady");
		randomItems.add("Disable Scape Rune Island");

		for (final ListIterator<String> it = randomItems.listIterator(); it.hasNext();) {
			final String s = it.next();
			if (!s.equals("-")) {
				it.set("ToggleF " + s);
			}
		}
		return randomItems.toArray(new String[randomItems.size()]);
	}
	private static String[] constructThemes() {
		final String[] themeSet = new String[BotGUI.themes.size()];
		int themeIndex = 0;
		for (final String key : BotGUI.themes.keySet()) {
			themeSet[themeIndex] = "Toggle"
				+ (key.equals("System") ? "F" : "F") + " " + key;
			themeIndex++;
		}
		return themeSet;
	}
	public static void disable(final String... items) {
		for (final String item : items) {
			commandCheckMap.get(item).setSelected(false);
			commandCheckMap.get(item).setEnabled(false);
		}
	}
	public static void enable(final String item, final boolean selected) {
		commandCheckMap.get(item).setSelected(selected);
		commandCheckMap.get(item).setEnabled(true);
	}

	/**
	 * This will save the client menu settings when the client is closed., It
	 * will not save the debug and skin setting as those will not function
	 * properly if saved.
	 */
	public static void shutdown() {
		try {
			final BufferedWriter bw = new BufferedWriter(new FileWriter(
					menuSetting));
			boolean f = true;
			for (final JCheckBoxMenuItem item : BotMenuBar.commandCheckMap
					.values()) {
				if (item == null) {
					continue;
				}
				if (item.isSelected()
						/* disallow certain debug menu saves that are causing
						 * game crashes.
						 */
						/*
						&& !item.getText().startsWith("All Debugging")
						&& !item.getText().startsWith("Game State")
						&& !item.getText().startsWith("Current Tab")
						&& !item.getText().startsWith("Camera")
						&& !item.getText().startsWith("Animation")
						&& !item.getText().startsWith("Floor Height")
						&& !item.getText().startsWith("Player Position")
						&& !item.getText().startsWith("Orientation")
						&& !item.getText().startsWith("Mouse Position")
						&& !item.getText().startsWith("User Input Allowed")
						&& !item.getText().startsWith("Menu Actions")
						&& !item.getText().startsWith("Menu")
						&& !item.getText().startsWith("FPS")
					
						&& !item.getText().startsWith("Players")
						&& !item.getText().startsWith("NPCs")
						
						&& !item.getText().startsWith("Objects")
						&& !item.getText().startsWith("Models")
						&& !item.getText().startsWith("Mouse")
						&& !item.getText().startsWith("Inventory")
						&& !item.getText().startsWith("Ground Items")
						&& !item.getText().startsWith("Log Messages")
						*/
						/* disallowing skin setttings saves */
						/*
						&& !item.getText().startsWith("RuneDev")
						&& !item.getText().startsWith("System")
						&& !item.getText().startsWith("Quirlion")
						&& !item.getText().startsWith("Windows")
						&& !item.getText().startsWith("Aero")
						&& !item.getText().startsWith("Aluminium")
						&& !item.getText().startsWith("Hifi")
						*/
						) {
					if (!f) {
						bw.newLine();
					}
					f = false;
					bw.write(item.getText());
				}
			}
			bw.close();
		} catch (final Exception e) {
		}
		System.exit(0);
	}

	private final ActionListener listener;

	public BotMenuBar(final ActionListener listener) {
		this.listener = listener;
		for (int i = 0; i < TITLES.length; i++) {
			final String title = TITLES[i];
			final String[] elems = ELEMENTS[i];
			add(constructMenu(title, elems));
		}
		add(getOptionMenu());
		for (int i = 0; i < TITLES6.length; i++) {
			final String title6 = TITLES6[i];
			final String[] elems6 = ELEMENTS6[i];
			add(constructMenu(title6, elems6));
		}
	}

	private JMenu constructMenu(final String title, final String[] elems) {
		final JMenu menu = new JMenu(title);
		for (String e : elems) {
			if (e.equals("-")) {
				menu.add(new JSeparator());
			} else {
				JMenuItem jmi;
				if (e.startsWith("Toggle")) {
					e = e.substring("Toggle".length());
					final char state = e.charAt(0);
					e = e.substring(2);
					jmi = new JCheckBoxMenuItem(e);
					if ((state == 't') || (state == 'T')) {
						jmi.setSelected(true);
					}
					if (DEBUG_MAP.containsKey(e)) {
						eventCheckMap.put(e, (JCheckBoxMenuItem) jmi);
					}
					commandCheckMap.put(e, (JCheckBoxMenuItem) jmi);
				} else {
					jmi = new JMenuItem(e);
					commandMenuItem.put(e, jmi);
				}
				jmi.addActionListener(listener);
				jmi.setActionCommand(title + "." + e);
				menu.add(jmi);
			}
		}
		return menu;
	}

	public void doClick(final String item) {
		commandMenuItem.get(item).doClick();
	}

	public void doTick(final String item) {
		commandCheckMap.get(item).doClick();
	}

	public JCheckBoxMenuItem getCheckBox(final String key) {
		return commandCheckMap.get(key);
	}

	public JCheckBoxMenuItem getCheckBoxEvent(final String key) {
		return eventCheckMap.get(key);
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getOptionMenu() {
		if (optionMenu == null) {
			optionMenu = new JMenu();
			optionMenu.setText("Options");
			for (int i = 0; i < TITLES2.length; i++) {
				final String title2 = TITLES2[i];
				final String[] elems2 = ELEMENTS2[i];
				optionMenu.add(constructMenu(title2, elems2));
			}
			for (int i = 0; i < TITLES3.length; i++) {
				final String title3 = TITLES3[i];
				final String[] elems3 = ELEMENTS3[i];
				optionMenu.add(constructMenu(title3, elems3));
			}
			for (int i = 0; i < TITLES4.length; i++) {
				final String title4 = TITLES4[i];
				final String[] elems4 = ELEMENTS4[i];
				optionMenu.add(constructMenu(title4, elems4));
			}
			for (int i = 0; i < TITLES5.length; i++) {
				final String title5 = TITLES5[i];
				final String[] elems5 = ELEMENTS5[i];
				optionMenu.add(constructMenu(title5, elems5));
			}
		}
		return optionMenu;
	}

	/**
	 * Sets the menu check box settings based on the saved list on the settings
	 * cache
	 */
	protected void initListeners() {
		if (!menuSetting.exists()) {
			try {
				menuSetting.createNewFile();
			} catch (final IOException e) {
			}
		} else {
			try {
				final BufferedReader br = new BufferedReader(new FileReader(
						menuSetting));
				String s;
				while ((s = br.readLine()) != null) {
					final JCheckBoxMenuItem item = commandCheckMap.get(s);
					if (item != null) {
						item.doClick();
					}
				}
			} catch (final IOException e) {
			}
		}

	}

	public boolean isTicked(final String item) {
		return commandCheckMap.get(item).isSelected();
	}

	public void setBot(final Bot bot) {
		commandMenuItem.get("Accounts").setIcon(account);
		commandMenuItem.get("Run Script").setIcon(start);
		commandMenuItem.get("Stop Script").setIcon(stop);
		commandMenuItem.get("Pause Script").setIcon(pauseico);
		commandMenuItem.get("Save ScreenShot").setIcon(shot);
		commandMenuItem.get("Save ScreenShot (UnCen)").setIcon(shot);
		commandCheckMap.get("Force Input").setIcon(noinput);
		commandCheckMap.get("Less CPU").setIcon(cpu);
		commandMenuItem.get("Exit").setIcon(exit);
		/* debug */
		commandCheckMap.get("All Debugging").setIcon(bugpen);
		eventCheckMap.get("Game State").setIcon(bugpen);
		eventCheckMap.get("Current Tab").setIcon(bugpen);
		eventCheckMap.get("Camera").setIcon(shot);
		eventCheckMap.get("Animation").setIcon(bugpen);
		eventCheckMap.get("Floor Height").setIcon(bugpen);
		eventCheckMap.get("Player Position").setIcon(account);
		eventCheckMap.get("Orientation").setIcon(bugpen);
		eventCheckMap.get("Mouse Position").setIcon(mouse);
		eventCheckMap.get("User Input Allowed").setIcon(bugpen);
		eventCheckMap.get("Menu Actions").setIcon(bugpen);
		eventCheckMap.get("Menu").setIcon(bugpen);
		eventCheckMap.get("FPS").setIcon(bugpen);
		eventCheckMap.get("Players").setIcon(account);
		eventCheckMap.get("NPCs").setIcon(bugpen);
		eventCheckMap.get("Objects").setIcon(bugpen);
		eventCheckMap.get("Models").setIcon(bugpen);
		eventCheckMap.get("Mouse").setIcon(mouse);
		eventCheckMap.get("Inventory").setIcon(bugpen);
		eventCheckMap.get("Ground Items").setIcon(bugpen);
		eventCheckMap.get("Log Messages").setIcon(bugpen);
		eventCheckMap.get("Debug Client").setIcon(bugpen);
		eventCheckMap.get("Debug Script").setIcon(bugpen);


		/* skins */
		commandCheckMap.get("RuneDev").setIcon(bugpen);
		commandCheckMap.get("System").setIcon(bugpen);
		commandCheckMap.get("Quirlion").setIcon(bugpen);
		commandCheckMap.get("Windows").setIcon(bugpen);
		commandCheckMap.get("Aero").setIcon(bugpen);
		commandCheckMap.get("Aluminium").setIcon(bugpen);
		commandCheckMap.get("Hifi").setIcon(bugpen);
		/* help menu */
		commandMenuItem.get("Troubleshooting").setIcon(help);
		commandMenuItem.get("Site").setIcon(home);
		commandMenuItem.get("Forums").setIcon(web);
		commandMenuItem.get("Wiki").setIcon(wiki);
		commandMenuItem.get("Facebook").setIcon(face);
		commandMenuItem.get("Project").setIcon(web);
		commandMenuItem.get("The Client").setIcon(gui);
		commandMenuItem.get("The Makers").setIcon(gui);

		if (bot == null) {
			commandMenuItem.get("Run Script").setEnabled(false);
			commandMenuItem.get("Stop Script").setEnabled(false);
			commandMenuItem.get("Pause Script").setEnabled(false);
			commandMenuItem.get("Save ScreenShot").setEnabled(false);
			for (final JCheckBoxMenuItem item : eventCheckMap.values()) {
				item.setSelected(false);
				item.setEnabled(false);
			}

			disable("All Debugging", "Force Input", "Less CPU", "Disable Game Screen",
					"Disable All Random Solving", "Disable All Anti-Bans",
					"Disable Auto Login", "Disable Break Handler",
					"Disable Capn Arnav", "Disable Bee Hive", "Disable Certer",
					"Disable Drill Demon", "Disable Exam",
					"Disable First Time Death", "Disable Freaky Forester",
					"Disable Frog Cave", "Disable Grave Digger",
					"Disable Leave Safe Area", "Disable Lost And Found",
					"Disable Maze", "Disable Mime", "Disable Molly",
					"Disable Pillory", "Disable Pinball", "Disable Prison",
					"Disable Quiz Solver", "Disable Sandwhich Lady",
			"Disable Scape Rune Island");
		} else {
			commandMenuItem.get("Run Script").setEnabled(true);
			commandMenuItem.get("Stop Script").setEnabled(true);
			commandMenuItem.get("Pause Script").setEnabled(true);
			commandMenuItem.get("Save ScreenShot").setEnabled(true);
			int selections = 0;
			for (final Map.Entry<String, JCheckBoxMenuItem> entry : eventCheckMap
					.entrySet()) {
				entry.getValue().setEnabled(true);
				final boolean selected = bot
				.hasListener(DEBUG_MAP.get(entry.getKey()));
				entry.getValue().setSelected(selected);
				if (selected) {
					++selections;
				}
			}
			enable("All Debugging", selections == commandCheckMap.size());
			enable("Force Input", bot.overrideInput);
			enable("Less CPU", bot.disableRendering);
                        enable("Disable Game Screen", bot.disableGraphics);
			/* Antibans */
			enable("Disable All Anti-Bans", Bot.disableRandoms);
			enable("Disable Auto Login", Bot.disableAutoLogin);
			enable("Disable Break Handler", Bot.disableBreakHandler);
			/* Randoms */
			enable("Disable All Random Solving", Bot.disableRandoms);
			enable("Disable Bee Hive", Bot.disableBeehiveSolver);
			enable("Disable Capn Arnav", Bot.disableCapnArnav);
			enable("Disable Capn Arnav", Bot.disableCapnArnav);
			enable("Disable Certer", Bot.disableCerter);
			enable("Disable Drill Demon", Bot.disableDrillDemon);
			enable("Disable Exam", Bot.disableExam);
			enable("Disable First Time Death", Bot.disableFirstTimeDeath);
			enable("Disable Freaky Forester", Bot.disableFreakyForester);
			enable("Disable Frog Cave", Bot.disableFrogCave);
			enable("Disable Grave Digger", Bot.disableGraveDigger);
			enable("Disable Leave Safe Area", Bot.disableLeaveSafeArea);
			enable("Disable Lost And Found", Bot.disableLostAndFound);
			enable("Disable Maze", Bot.disableMaze);
			enable("Disable Mime", Bot.disableMime);
			enable("Disable Molly", Bot.disableMolly);
			enable("Disable Pillory", Bot.disablePillory);
			enable("Disable Pinball", Bot.disablePinball);
			enable("Disable Prison", Bot.disablePrison);
			enable("Disable Quiz Solver", Bot.disableQuizSolver);
			enable("Disable Sandwhich Lady", Bot.disableSandwhichLady);
			enable("Disable Scape Rune Island", Bot.disableScapeRuneIsland);
		}
	}

	public void setEnabled(final String item, final boolean mode) {
		commandCheckMap.get(item).setEnabled(mode);
	}

	public void setOverrideInput(final boolean force) {
		commandCheckMap.get("Force Input").setSelected(force);
		commandCheckMap.get("Force Input").setIcon(input);
	}

	public void setPauseScript(final boolean pause) {
		commandMenuItem.get("Pause Script").setText(
				pause ? "Resume Script" : "Pause Script");
		commandMenuItem.get("Pause Script").setIcon(pause ? start : pauseico);
	}
}
