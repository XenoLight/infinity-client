package org.rsbot.gui.toolactions;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import org.rsbot.gui.BotGUI;

/**
 * @author Mothma for the RSBot client at powerbot.org - version 1.0
 */
public class KeyboardActions {
	private static final HashMap<Object, String> SHORTCUT_MAP = new HashMap<Object, String>();

	static {
                SHORTCUT_MAP.put(KeyEvent.VK_A, "Client" + "." + "Accounts");
		SHORTCUT_MAP.put(KeyEvent.VK_R, "Client" + "." + "Run Script");
		SHORTCUT_MAP.put(KeyEvent.VK_S, "Client" + "." + "Stop Script");
		SHORTCUT_MAP.put(KeyEvent.VK_P, "Client" + "." + "Pause Script");
		SHORTCUT_MAP.put(KeyEvent.VK_I, "Client" + "." + "Save ScreenShot");
		SHORTCUT_MAP.put(KeyEvent.VK_Q, "Client" + "." + "Exit");
	}

	public KeyboardActions(KeyboardFocusManager manager, BotGUI botgui) {
		manager.addKeyEventDispatcher(new KeyDispatcher(manager, botgui));
	}

	public class KeyDispatcher implements KeyEventDispatcher {
		final KeyboardFocusManager manager;
		final BotGUI botgui;

		KeyDispatcher(KeyboardFocusManager manager, BotGUI botgui) {
			System.out.println("Dispatcher created.");
			this.manager = manager;
			this.botgui = botgui;
		}

		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.isControlDown()) {
				if (SHORTCUT_MAP.containsKey(e.getKeyCode())) {
					if (e.getID() == KeyEvent.KEY_PRESSED) {
						botgui.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, SHORTCUT_MAP.get(e.getKeyCode())));
					}
				}
				return false;
			} else {
				manager.dispatchKeyEvent(e);
			}
			return true;
		}
	}
}


