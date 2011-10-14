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

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.lazygamerz.scripting.api.Environment;
import org.rsbot.bot.Bot;
import org.rsbot.gui.toolactions.KeyboardActions;
import org.rsbot.log.TextAreaLogHandler;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptHandler;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.internal.event.ScriptListener;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.Screenshot;
import org.rsbot.util.io.HttpClient;

/**
 * @author Runedev development team - version 1.0
 */
public class BotGUI extends JFrame implements ActionListener, ScriptListener {

	private static final long serialVersionUID = -2678464873236578L;
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Bot.class.getPackage()
			.getName());
	public static final int PANEL_WIDTH = 765, PANEL_HEIGHT = 502,
	LOG_HEIGHT = 120;
	private static final Logger log = Logger.getLogger(BotGUI.class.getName());
	public static final Map<String, String> themes = new HashMap<String, String>();
        public KeyboardActions keyact = null;
	public JScrollPane textScroll;
	public ButtonPanel buttonPanel = new ButtonPanel(this);
	private BotPanel panel;
	private BotToolBar toolBar;
	private BotMenuBar menuBar;
	private BotHome home;
	private TrayIcon trayIcon;
	private final List<Bot> bots = new ArrayList<Bot>();

	
	public BotGUI() {
		init();
		setTitle(null);
		setLocationRelativeTo(getOwner());
		setMinimumSize(getSize());
		setResizable(true);
	}

	public void addInternalFrame(JInternalFrame frame)  {
		panel.add(frame);
	}
	
	public void actionPerformed(final ActionEvent evt) {
		final String action = evt.getActionCommand();
		String menu;
		final String[] option = new String[3];
		final int z = action.indexOf('.');
		if (z == -1) {
			menu = action;
			option[1] = "";
			option[2] = "";
		} else {
			menu = action.substring(0, z);
			option[1] = action.substring(z + 1);
			option[2] = action.substring(z + 2);
		}
		if (menu.equals("Client")) {
			final Bot current = getCurrentBot();
			if (current != null) {
				if (option[1].equals("Accounts")) {
					AccountManager.getInstance().showGUI();
				} else if (option[1].equals("Run Script")) {
					showScriptSelector(current);
				} else if (option[1].equals("Stop Script")) {
					showStopScript(current);
				} else if (option[1].equals("Pause Script")) {
					pauseScript(current);
				} else if (option[1].equals("Save ScreenShot")) {
					Screenshot.takeScreenshot(current.getMethods().game
							.isLoggedIn());
				} else if (option[1].equals("Save ScreenShot (UnCen)")) {
					Screenshot.takeScreenshot(false);
				} else if (option[1].equals("Force Input")) {
					final boolean selected = ((JCheckBoxMenuItem) evt.getSource())
					.isSelected();
					current.overrideInput = selected;
					toolBar.setOverrideInput(selected);
				} else if (option[1].equals("Less CPU")) {
					current.disableRendering = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
                                } else if (option[1].equals("Disable Game Screen")) {
					current.disableGraphics = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Exit")) {
					System.exit(0);
				}
			}
		} else if (menu.equals("Anti-Bans")) {
			final Bot current = getCurrentBot();
			if (current != null) {
				if (option[1].equals("Disable All Anti-Bans")) {
					current.disableAntibans = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Auto Login")) {
					current.disableAutoLogin = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Break Handler")) {
					current.disableBreakHandler = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				}
			}
		} else if (menu.equals("Randoms")) {
			final Bot current = getCurrentBot();
			if (current != null) {
				if (option[1].equals("Disable All Random Solving")) {
					current.disableRandoms = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Bee Hive")) {
					current.disableBeehiveSolver = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Capn Arnav")) {
					current.disableCapnArnav = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Certer")) {
					current.disableCerter = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Drill Demon")) {
					current.disableDrillDemon = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Exam")) {
					current.disableExam = ((JCheckBoxMenuItem) evt.getSource())
					.isSelected();
				} else if (option[1].equals("Disable First Time Death")) {
					current.disableFirstTimeDeath = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Freaky Forester")) {
					current.disableFreakyForester = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Frog Cave")) {
					current.disableFrogCave = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Grave Digger")) {
					current.disableGraveDigger = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Leave Safe Area")) {
					current.disableLeaveSafeArea = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Lost And Found")) {
					current.disableLostAndFound = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Maze")) {
					current.disableMaze = ((JCheckBoxMenuItem) evt.getSource())
					.isSelected();
				} else if (option[1].equals("Disable Mime")) {
					current.disableMime = ((JCheckBoxMenuItem) evt.getSource())
					.isSelected();
				} else if (option[1].equals("Disable Molly")) {
					current.disableMolly = ((JCheckBoxMenuItem) evt.getSource())
					.isSelected();
				} else if (option[1].equals("Disable Pillory")) {
					current.disablePillory = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Pinball")) {
					current.disablePinball = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Prison")) {
					current.disablePrison = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Quiz Solver")) {
					current.disableQuizSolver = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Sandwhich Lady")) {
					current.disableSandwhichLady = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				} else if (option[1].equals("Disable Scape Rune Island")) {
					current.disableScapeRuneIsland = ((JCheckBoxMenuItem) evt
							.getSource()).isSelected();
				}
			}
		} else if (menu.equals("Close")) {
			final boolean selected = ((JCheckBoxMenuItem) evt.getSource())
			.isSelected();
			if (option[1].equals("Toolbar")) {
				toggleViewStateHi(toolBar, selected);
			} else if (option[1].equals("Option Buttons")) {
				toggleViewStateWi(getButtonPanel(), selected);
			} else if (option[1].equals("Log")) {
				toggleViewStateHi(textScroll, selected);
			}
		} else if (menu.equals("Skins")) {
			setTheme(option[1]);
		} else if (menu.equals("Features")) {
			if (option[1].equals("Script Editor")) {
			}
		} else if (menu.equals("Debug")) {
			final Bot current = getCurrentBot();
			final boolean selected = ((JCheckBoxMenuItem) evt.getSource())
			.isSelected();
			if (current != null) {
				if (option[1].equals("All Debugging")) {
					for (final String key : BotMenuBar.DEBUG_MAP.keySet()) {
						final Class<?> el = BotMenuBar.DEBUG_MAP.get(key);
						final boolean wasSelected = menuBar.getCheckBox(key)
						.isSelected();
						menuBar.getCheckBox(key).setSelected(selected);
						if (selected) {
							if (!wasSelected) {
								current.addListener(el);
							}
						} else {
							if (wasSelected) {
								current.removeListener(el);
							}
						}
					}
				} else if (option[1].equals("Debug Client")) {
					current.debugLogging = ((JCheckBoxMenuItem) evt.getSource())
					.isSelected();
				} else if (option[1].equals("Debug Script")) {
					current.debugScriptLogging = ((JCheckBoxMenuItem) evt.getSource())
					.isSelected();
				} else {
					final Class<?> el = BotMenuBar.DEBUG_MAP.get(option[1]);
					menuBar.getCheckBox(option[1]).setSelected(selected);
					if (selected) {
						current.addListener(el);
					} else {
						menuBar.getCheckBox("All Debugging").setSelected(false);
						current.removeListener(el);
					}
				}
			}
		} else if (menu.equals("Help")) {
			if (option[1].equals("Troubleshooting")) {
				HttpClient
				.openURL("http://www.lazygamerz.org/forums/index.php?board=11.0");
			} else if (option[1].equals("Site")) {
				HttpClient.openURL(GlobalConfiguration.Paths.URLs.SITE);
			} else if (option[1].equals("Forums")) {
				HttpClient
				.openURL("http://www.lazygamerz.org/forums/index.php");
			} else if (option[1].equals("Wiki")) {
				HttpClient
				.openURL("http://runedev.wikia.com/wiki/RuneDev_Wiki");
			} else if (option[1].equals("Facebook")) {
				HttpClient
				.openURL("http://www.facebook.com/pages/LazyGamerzorg/213068622042708");
			} else if (option[1].equals("Project")) {
				HttpClient.openURL(GlobalConfiguration.Paths.URLs.PROJECT);
			} else if (option[1].equals("The Client")) {
				JOptionPane.showMessageDialog(this, new String[] {
						"An open source Client.",
						"Developed by a hands on team\n"
						+ "Who works closely with the community\n"
						+ "For more information, visit\n"
						+ GlobalConfiguration.Paths.URLs.SITE },
						"About the Clinet", JOptionPane.INFORMATION_MESSAGE);
			} else if (option[1].equals("The Makers")) {
				HttpClient
				.openURL("http://code.google.com/p/noipdevteam/wiki/Aboutthemakers?ts="
						+ "1268920860&updated=Aboutthemakers");
			}
		} else if (menu.equals("Tab")) {
			final Bot curr = getCurrentBot();
			menuBar.setBot(curr);
			panel.setBot(curr);
			panel.repaint();
			toolBar.setHome(curr == null);
			if (curr == null) {
				setTitle(null);
				toolBar.setScriptButton(BotToolBar.RUN_SCRIPT);
				toolBar.setOverrideInput(false);
				toolBar.setInputState(Environment.inputKeyboard
						| Environment.inputMouse);
			} else {
				setTitle(curr.getAccountName());
				final Map<Integer, Script> scriptMap = curr.getScriptHandler()
				.getRunningScripts();
				if (scriptMap.size() > 0) {
					if (scriptMap.values().iterator().next().isPaused()) {
						toolBar.setScriptButton(BotToolBar.RESUME_SCRIPT);
					} else {
						toolBar.setScriptButton(BotToolBar.PAUSE_SCRIPT);
					}
				} else {
					toolBar.setScriptButton(BotToolBar.RUN_SCRIPT);
				}
				toolBar.setOverrideInput(curr.overrideInput);
				toolBar.setInputState(curr.inputFlags);
			}
		} else if (menu.equals("Run")) {
			final Bot current = getCurrentBot();
			if (current != null) {
				showScriptSelector(current);
			}
		} else if (menu.equals("Pause") || menu.equals("Resume")) {
			final Bot current = getCurrentBot();
			if (current != null) {
				pauseScript(current);
			}
		} else if (menu.equals("Input")) {
			final Bot current = getCurrentBot();
			if (current != null) {
				final boolean override = !current.overrideInput;
				current.overrideInput = override;
				menuBar.setOverrideInput(override);
				toolBar.setOverrideInput(override);
				toolBar.updateInputButton();
			}
		}
	}

	public void addBot() {
		final Bot bot = new Bot();
		bots.add(bot);
		toolBar.addTab();
		Bot.getScriptHandler().addScriptListener(this);
		new Thread(new Runnable() {

			public void run() {
				bot.start();
				home.setBots(bots);
				
				// Refresh the menu bar to pick up the saved debug
				// settings.  This can only be done here, after
				// the bot is initialized and it's listeners are
				/// setup.
				if (bots.size()==1)  {
					refreshMenuBar();
				}
			}
		}).start();
	}

	public Bot getBot(final Object o) {
		final ClassLoader cl = o.getClass().getClassLoader();
		for (final Bot bot : bots) {
			if (cl == bot.getLoader().getClient().getClass().getClassLoader()) {
				panel.offset();
				return bot;
			}
		}
		return null;
	}

	/**
	 * This method initializes URLPanel
	 * 
	 * @return org.rsbot.gui.URLPanel
	 */
	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	public Bot getCurrentBot() {
		final int idx = toolBar.getCurrentTab() - 1;
		if (idx >= 0) {
			return bots.get(idx);
		}
		return null;
	}

	public BotPanel getPanel() {
		return panel;
	}

	private void init() {
		home = new BotHome();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {

			public void windowClosing(final WindowEvent e) {
				if (safeClose()) {
					dispose();
					BotMenuBar.shutdown();
				}
			}
		});

		setIconImage(GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.ICON));
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		try {
			UIManager.setLookAndFeel(UIManager
                                .getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		themes.put("RuneDev",
                        "de.muntjak.tinylookandfeel.TinyLookAndFeel");
		themes.put("System",
                        UIManager.getSystemLookAndFeelClassName());
		themes.put("Quirlion",
                        "com.nilo.plaf.nimrod.NimRODLookAndFeel");
		themes.put("Windows",
                        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		themes.put("Aero", "com.jtattoo.plaf.aero.AeroLookAndFeel");
		themes.put("Aluminium",
                        "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
		themes.put("Hifi",
                        "com.jtattoo.plaf.hifi.HiFiLookAndFeel");

		panel = new BotPanel(home);
		panel.setFocusTraversalKeys(0, new HashSet<AWTKeyStroke>());
                KeyboardFocusManager manager = KeyboardFocusManager
                        .getCurrentKeyboardFocusManager();
		keyact = new KeyboardActions(manager, this);

		menuBar = new BotMenuBar(this);
		menuBar.setBot(null);
		setJMenuBar(menuBar);

		toolBar = new BotToolBar(this);
		toolBar.setHome(true);

		textScroll = new JScrollPane(TextAreaLogHandler.TEXT_AREA,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScroll.setBorder(null);
                textScroll.setPreferredSize(new Dimension(PANEL_WIDTH, LOG_HEIGHT));
		textScroll.setVisible(true);

		add(toolBar, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(getButtonPanel(), BorderLayout.EAST);
		add(textScroll, BorderLayout.SOUTH);
		
		pack();
		
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				trayIcon();
			}
		});
	}

	public void inputChanged(final Bot bot, final int mask) {
		bot.inputFlags = mask;
		toolBar.setInputState(mask);
		toolBar.updateInputButton();
	}

	void pauseScript(final Bot bot) {
		final ScriptHandler sh = bot.getScriptHandler();
		final Map<Integer, Script> running = sh.getRunningScripts();
		if (running.size() > 0) {
			final int id = running.keySet().iterator().next();
			sh.pauseScript(id);
		}
	}

	public void removeBot(final Bot bot) {
		final int idx = bots.indexOf(bot);
		if (idx >= 0) {
			toolBar.removeTab(idx + 1);
		}
		bots.remove(idx);
		Bot.getScriptHandler().stopAllScripts();
		Bot.getScriptHandler().removeScriptListener(this);
		home.setBots(bots);
		new Thread(new Runnable() {

			public void run() {
				bot.stop();
				System.gc();
			}
		}).start();
	}

	public void runScript(final String name, final Script script, final Map<String, String> args) {
		Bot.setAccount(name);
		Bot.getScriptHandler().runScript(script, args);
	}

	/**
	 * Allows for a safe close of the client by
	 * 
	 * @return
	 */
	private boolean safeClose() {
		boolean pass = true;

		if (Bot.methods != null && Bot.methods.game != null) {
			if (Bot.methods.game.isLoggedIn()) {
				final int result = JOptionPane.showConfirmDialog(this,
						"Are you sure you would like to quit?", "Close",
						JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION);
				pass = result == JOptionPane.YES_OPTION;
			} else if (!Bot.methods.game.isLoggedIn()) {
				BotMenuBar.shutdown();
				pass = false;
			}
		}

		return pass;
	}

	public void scriptPaused(final ScriptHandler handler, final Script script) {
		if (handler.getBot() == getCurrentBot()) {
			toolBar.setScriptButton(BotToolBar.RESUME_SCRIPT);
			menuBar.setPauseScript(true);
		}
	}

	public void scriptResumed(final ScriptHandler handler, final Script script) {
		if (handler.getBot() == getCurrentBot()) {
			toolBar.setScriptButton(BotToolBar.PAUSE_SCRIPT);
			menuBar.setPauseScript(false);
		}
	}

	public void scriptStarted(final ScriptHandler handler, final Script script) {
		final Bot bot = handler.getBot();
		if (bot == getCurrentBot()) {
			bot.inputFlags = Environment.inputKeyboard;
			bot.overrideInput = false;
			toolBar.setScriptButton(BotToolBar.PAUSE_SCRIPT);
			toolBar.setInputState(bot.inputFlags);
			toolBar.setOverrideInput(false);
			menuBar.setOverrideInput(false);
			final String acct = bot.getAccountName();
			toolBar.updateInputButton();
			setTitle(acct);
		}
	}

	public void scriptStopped(final ScriptHandler handler, final Script script) {
		final Bot bot = handler.getBot();
		if (bot == getCurrentBot()) {
			bot.inputFlags = Environment.inputKeyboard | Environment.inputMouse;
			bot.overrideInput = false;
			toolBar.setScriptButton(BotToolBar.RUN_SCRIPT);
			toolBar.setInputState(bot.inputFlags);
			toolBar.setOverrideInput(false);
			menuBar.setOverrideInput(false);
			menuBar.setPauseScript(false);
			toolBar.updateInputButton();
			setTitle(null);
		}
	}

	/**
	 * Sets the names of the themes into the skins menu
	 * 
	 * @param Name
	 *            The name of the UIManger theme
	 */
	void setTheme(final String Name) {
		for (final String key : themes.keySet()) {
			final boolean match = key.equals(Name);
			if (match) {
				try {
					UIManager.setLookAndFeel(themes.get(key));
				} catch (final Exception e) {
				}
				for (final Frame frame : BotGUI.getFrames()) {
					SwingUtilities.updateComponentTreeUI(frame);
					frame.pack();

				}

			}
			menuBar.getCheckBox(key).setSelected(match);
		}
	}

	@Override
	//TODO: Restore version as float when finally released
	public void setTitle(final String title) {
		if (title != null) {
			/*
			super.setTitle(title + " - " + GlobalConfiguration.NAME + " v"
					+ ((float) GlobalConfiguration.getVersion() / 100));
			*/
			super.setTitle(title + " - " + GlobalConfiguration.NAME + " v2.0 [RC "
					+ (GlobalConfiguration.getVersion() / 100)
					+ "]");
		} else {
			/*
			super.setTitle(GlobalConfiguration.NAME + " v"
					+ ((float) GlobalConfiguration.getVersion() / 100));
			*/
			super.setTitle(GlobalConfiguration.NAME + " v2.0 [RC "
					+ (GlobalConfiguration.getVersion() / 100)
					+ "]");
		}
	}

	private void showScriptSelector(final Bot bot) {
		if (AccountManager.getAccountNames().size() == 0) {
			JOptionPane
			.showMessageDialog(this,
					"No accounts found! Please create one before using the bot.");
			AccountManager.getInstance().showGUI();
		} else if (bot.getMethods() == null) {
			JOptionPane.showMessageDialog(this,
			"The client is not currently loaded!");
		} else {
			ScriptSelector.getInstance(this).showSelector();
		}
	}

	/**
	 * Sets up the stop script action with a warning
	 */
	private void showStopScript(final Bot bot) {
		final ScriptHandler sh = Bot.getScriptHandler();
		final Map<Integer, Script> running = sh.getRunningScripts();
		if (running.size() > 0) {
			final int id = running.keySet().iterator().next();
			final Script s = running.get(id);
			final ScriptManifest prop = s.getClass().getAnnotation(
					ScriptManifest.class);
			final int result = JOptionPane.showConfirmDialog(
					this,
					"Would you like to stop the script "
					+ (prop == null ? s.getClass().getName() : prop
							.name()) + "?", "Script",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				sh.stopScript(id);
				toolBar.setScriptButton(BotToolBar.RUN_SCRIPT);
			}
		}
	}

	/**
	 * Sets up the stop script action with no warning
	 */
	public void stopScriptNoWarn(final Bot bot) {
		final ScriptHandler sh = Bot.getScriptHandler();
		final Map<Integer, Script> running = sh.getRunningScripts();
		if (running.size() > 0) {
			final int id = running.keySet().iterator().next();
			sh.stopScript(id);
			toolBar.setScriptButton(BotToolBar.RUN_SCRIPT);
		}
	}

	private void toggleViewStateHi(final Component component, final boolean visible) {
		final Dimension size = getSize();
		size.height += component.getSize().height * (visible ? -1 : 1);
		component.setVisible(!visible);
		setMinimumSize(size);
		if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
			pack();
		}
	}

	private void toggleViewStateWi(final Component component, final boolean visible) {
		final Dimension size = getSize();
		size.width += component.getSize().width * (visible ? -1 : 1);
		component.setVisible(!visible);
		setMinimumSize(size);
		if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
			pack();
		}
	}

	private void trayIcon() {
		if (SystemTray.isSupported()) {
			final SystemTray tray = SystemTray.getSystemTray();
			final Image icon = GlobalConfiguration
			.getImage(GlobalConfiguration.Paths.Resources.ICON);
			final ActionListener exitListener = new ActionListener() {

				public void actionPerformed(final ActionEvent e) {
					System.exit(0);
				}
			};

			final ActionListener rightClickListener = new ActionListener() {

				public void actionPerformed(final ActionEvent e) {
					final String msg = "You have controled the view of the client by\n"
						+ "right clicking on the system tray icon,\n"
						+ "and chosing the control client view option.\n"
						+ "To again toggle the view state of the client,\n"
						+ "simply click the system tray icon again.";
					trayIcon.displayMessage("Controling, " + getTitle(), msg,
							MessageType.INFO);
					setVisible(!isVisible());
				}
			};

			final ActionListener clickListener = new ActionListener() {

				public void actionPerformed(final ActionEvent e) {

					final String msg = "You have controled the view of the client by,\n"
						+ "clicking on the system tray icon.\n"
						+ "To again toggle the view state of the client,\n"
						+ "simply click the system tray icon again.";
					trayIcon.displayMessage("Controling, " + getTitle(), msg,
							MessageType.INFO);
					setVisible(!isVisible());

				}
			};

			final PopupMenu popup = new PopupMenu();
			final MenuItem exitOption = new MenuItem("Exit Client");
			exitOption.addActionListener(exitListener);
			final MenuItem hideTaskBarIcon = new MenuItem("Control Client View");
			hideTaskBarIcon.addActionListener(rightClickListener);

			popup.add(hideTaskBarIcon);
			popup.add(exitOption);

			trayIcon = new TrayIcon(icon, getTitle(), popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(clickListener);

			try {
				tray.add(trayIcon);
			} catch (final Exception e) {
				BotGUI.log.warning("System tray could not be created.");
			}
		} else {
			BotGUI.log.warning("System tray is not supported.");

		}

	}
	
	// This method is added to drive a refresh of the menubar in BotGUI.  
	// This prevents hanging of the client initialization due to debug 
	// listeners getting set.
	//
	// This method is called from the addBot method after the new bot has been
	// initialized.  It is only called for the first bot.
	public void refreshMenuBar()  {
		menuBar.initListeners();
	}
}
