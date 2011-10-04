package org.rsbot.script.antiban;

import org.rsbot.script.Antiban;
import org.rsbot.script.ScriptManifest;

/*
 * Cleaned and Updated By Secret Spy
 * Updated On 10/07/10
 */
@ScriptManifest(authors = { "Secret Spy" }, category = "AntiBan", name = "System Update", version = 1.6)
public class SystemUpdate extends Antiban {

	private int LogoutMinutes;
	private int LogoutSeconds;

	@Override
	public boolean activateCondition() {
		if (game.isLoggedIn()
				&& iface.getChild(754, 5).getText().startsWith(
				"<col=ffff00>System update in")
				&& !player.getMine().isInCombat()) {
			check();
		}
		return false;
	}

	private void check() {
		LogoutMinutes = random(1, getMinutes());
		LogoutSeconds = random(10, getSeconds());
		while (!checkForLogout()) {
			wait(1000);
		}
		log("I Have Detected A System Update.");
		wait(random(250, 500));
		log("Initializing Protocol:KillServer.");
		wait(random(250, 500));
		log("Initialization Sucessfull.");
		wait(random(250, 500));
		log("Termination Eminent.");
	}

	private boolean checkForLogout() {
		if ((getMinutes() < LogoutMinutes) && (getSeconds() < LogoutSeconds)) {
			stopScript(true);
			return true;
		} else {
			return false;
		}
	}

	private int getMinutes() {
		return Integer.parseInt(iface.getChild(754, 5).getText().substring(29)
				.trim().split(":")[0]);
	}

	private int getSeconds() {
		return Integer.parseInt(iface.getChild(754, 5).getText().substring(29)
				.trim().split(":")[1]);
	}

	@Override
	public int loop() {
		return -1;
	}
}