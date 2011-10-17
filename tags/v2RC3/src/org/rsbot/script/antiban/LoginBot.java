package org.rsbot.script.antiban;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import org.rsbot.bot.Bot;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Antiban;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;

/*
 * Cleaned and Updated By Secret Spy
 * Updated On 02/24/11 By Somanayr
 */
@ScriptManifest(authors = { "Secret Spy, Somanayr" }, name = "LoginBot", version = 1.4)
public class LoginBot extends Antiban {

	private static final int INTERFACE_MAIN = 905;
	private static final int INTERFACE_MAIN_CHILD = 59;
	private static final int INTERFACE_MAIN_CHILD_COMPONENT_ID = 4;
	private static final int INTERFACE_LOGIN_SCREEN = 596;
	private static final int INTERFACE_USERNAME = 73;
	private static final int INTERFACE_USERNAME_WINDOW = 40;
	private static final int INTERFACE_PASSWORD = 79;
	private static final int INTERFACE_PASSWORD_WINDOW = 42;
	private static final int INTERFACE_BUTTON_LOGIN = 45;
	private static final int INTERFACE_TEXT_RETURN = 14;
	private static final int INTERFACE_BUTTON_BACK = 68;
	private static final int INTERFACE_WELCOME_SCREEN = 906;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_1 = 181;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_2 = 183;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_TEXT = 184;
	// private static final int INTERFACE_WELCOME_SCREEN_BUTTON_LOGOUT = 193;
	private static final int INTERFACE_WELCOME_SCREEN_TEXT_RETURN = 221;
	private static final int INTERFACE_WELCOME_SCREEN_BUTTON_BACK = 218;
	private static final int INTERFACE_WELCOME_SCREEN_HIGH_RISK_WORLD_TEXT = 86;
	private static final int INTERFACE_WELCOME_SCREEN_HIGH_RISK_WORLD_LOGIN_BUTTON = 93;
	private static final int INTERFACE_WELCOME_SCREEN_TAB = 204;
	private static final int INTERFACE_WELCOME_SCREEN_TAB_CLICKAREA = 205;
	private static final int INTERFACE_GRAPHICS_NOTICE = 976;
	private static final int INTERFACE_GRAPHICS_LEAVE_ALONE = 6;
	private static final int INDEX_LOGGED_OUT = 3;
	private static final int INDEX_LOBBY = 7;
	private int invalidCount, worldFullCount;

	@Override
	public boolean activateCondition() {
		final int idx = game.getLoginIndex();
		return (idx == INDEX_LOGGED_OUT || idx == INDEX_LOBBY ||
				!game.isLoggedIn())
				&& !switchingWorlds() && getUsername() != null;
	}

	/* Clicks past all of the letters */
	private boolean atLoginInterface(final RSInterfaceChild i) {
		if (!i.isValid()) {
			return false;
		}
		final Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		final int dy = (int) (pos.getHeight() - 4) / 2;
		final int maxRandomX = (int) (pos.getMaxX() - pos.getCenterX());
		final int midx = (int) pos.getCenterX();
		final int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
		if (i.getIndex() == INTERFACE_PASSWORD_WINDOW) {
			mouse.click(minX(i), midy + random(-dy, dy), true);
		} else {
			mouse.click(midx + random(1, maxRandomX), midy + random(-dy, dy),
					true);
		}
		return true;
	}

	private boolean atLoginScreen() {
		return iface.get(596).isValid();
	}

	public String getUsername() {
		return Bot.getAccountName().toLowerCase().trim();
	}

	private boolean isPasswordFilled() {
		final String passWord = AccountManager
		.getPassword(Bot.getAccountName());
		return iface.get(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_PASSWORD)
		.getText().toLowerCase().length() == (passWord == null ? 0
				: passWord.length());
	}

	private boolean isUsernameFilled() {
		final String username = getUsername();
		return iface.get(INTERFACE_LOGIN_SCREEN).getChild(INTERFACE_USERNAME)
		.getText().toLowerCase().equalsIgnoreCase(username);
	}

	@Override
	public int loop() {
		final String username = getUsername();
		String returnText = iface.get(INTERFACE_LOGIN_SCREEN)
		.getChild(INTERFACE_TEXT_RETURN).getText().toLowerCase();
		int textlength;
		if (game.getLoginIndex() != INDEX_LOGGED_OUT) {
			if (!game.isWelcomeScreen()) {
				wait(random(1000, 2000));
			}
			if (game.getLoginIndex() == INDEX_LOBBY) {
				if (iface.get(INTERFACE_WELCOME_SCREEN)
						.getChild(INTERFACE_WELCOME_SCREEN_TAB)
						.getBackgroundColor() == 4672) {
					iface.clickChild(INTERFACE_WELCOME_SCREEN,
							INTERFACE_WELCOME_SCREEN_TAB_CLICKAREA);
					sleep(700);
				}

				final RSInterface welcome_screen = iface
				.get(INTERFACE_WELCOME_SCREEN);
				if (welcome_screen
						.getChild(INTERFACE_WELCOME_SCREEN_BUTTON_TEXT)
						.getText().equals("Play")) {
					final RSInterfaceChild welcome_screen_button_play_1 = welcome_screen
					.getChild(INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_1);
					final RSInterfaceChild welcome_screen_button_play_2 = welcome_screen
					.getChild(INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_2);

					mouse.click(
							welcome_screen_button_play_1.getAbsoluteX(),
							welcome_screen_button_play_1.getAbsoluteY(),
							welcome_screen_button_play_2.getAbsoluteX()
							+ welcome_screen_button_play_2.getWidth()
							- welcome_screen_button_play_1
							.getAbsoluteX(),
							welcome_screen_button_play_1.getHeight(), true);

					for (int i = 0; i < 4 && game.getLoginIndex() == 6; i++) {
						sleep(500);
					}
				}

				returnText = iface.get(INTERFACE_WELCOME_SCREEN)
				.getChild(INTERFACE_WELCOME_SCREEN_TEXT_RETURN)
				.getText().toLowerCase();

				if (returnText.contains("total skill level of")) {
					log("Log back in when you total level of 1000+");
					iface.clickChild(INTERFACE_WELCOME_SCREEN,
							INTERFACE_WELCOME_SCREEN_BUTTON_BACK);
					stopScript(false);
				} else if (returnText.contains("total skill level of")) {
					log("Log back in when you total level of 1500+");
					iface.clickChild(INTERFACE_WELCOME_SCREEN,
							INTERFACE_WELCOME_SCREEN_BUTTON_BACK);
					stopScript(false);
				}
				if (iface.get(INTERFACE_WELCOME_SCREEN)
						.getChild(INTERFACE_WELCOME_SCREEN_BUTTON_BACK)
						.isValid()) {
					iface.clickChild(INTERFACE_WELCOME_SCREEN,
							INTERFACE_WELCOME_SCREEN_BUTTON_BACK);
				}

				if (returnText.contains("login limit exceeded")) {
					if (iface.get(INTERFACE_WELCOME_SCREEN_BUTTON_BACK)
							.isValid()) {
						iface.clickChild(INTERFACE_WELCOME_SCREEN,
								INTERFACE_WELCOME_SCREEN_BUTTON_BACK);
					}
				}

				if (returnText.contains("your account has not logged out")) {
					iface.clickChild(INTERFACE_WELCOME_SCREEN,
							INTERFACE_WELCOME_SCREEN_BUTTON_BACK);
					if (invalidCount > 10) {
						log.warning("Unable to login after 10 attempts. Stopping script.");
						log.severe("It seems you are actually already logged in?");
						stopScript(false);
					}
					invalidCount++;
					log.severe("Waiting for logout..");
					sleep(5000, 15000);
				}

				if (returnText.contains("member")) {
					log("Unable to login to a members world. Stopping script.");
					final RSInterfaceChild back_button1 = iface.get(
							INTERFACE_WELCOME_SCREEN).getChild(228);
					final RSInterfaceChild back_button2 = iface.get(
							INTERFACE_WELCOME_SCREEN).getChild(231);
					mouse.click(
							back_button1.getAbsoluteX(),
							back_button1.getAbsoluteY(),
							back_button2.getAbsoluteX()
							+ back_button2.getWidth()
							- back_button1.getAbsoluteX(),
							back_button1.getHeight(), true);
					iface.clickChild(INTERFACE_WELCOME_SCREEN, 203);
					stopScript(false);
				}

				if (iface
						.get(INTERFACE_WELCOME_SCREEN)
						.getChild(INTERFACE_WELCOME_SCREEN_HIGH_RISK_WORLD_TEXT)
						.getText().toLowerCase().trim()
						.contains("high-risk wilderness world")) {
					iface.clickChild(INTERFACE_WELCOME_SCREEN,
							INTERFACE_WELCOME_SCREEN_HIGH_RISK_WORLD_LOGIN_BUTTON);
				}
			}
			return -1;
		}
		if (!game.isLoggedIn()) {
			if (iface.get(INTERFACE_LOGIN_SCREEN)
					.getChild(INTERFACE_BUTTON_BACK).isValid()) {
				iface.clickChild(INTERFACE_LOGIN_SCREEN, INTERFACE_BUTTON_BACK);
			}
			if (returnText.contains("no reply from login server")) {
				if (invalidCount > 10) {
					log.warning("Unable to login after 10 attempts. Stopping script.");
					log.severe("It seems the login server is down.");
					stopScript(false);
				}
				invalidCount++;
				return random(500, 2000);
			}
			if (returnText.contains("update")) {
				log("Runescape has been updated, please reload RSBot.");
				stopScript(false);
			}
			if (returnText.contains("disable")) {
				log.severe("Your account is banned/disabled.");
				stopScript(false);
			}
			if (returnText.contains("your account has not logged out")) {
				iface.clickChild(INTERFACE_LOGIN_SCREEN, INTERFACE_BUTTON_BACK);
				if (invalidCount > 10) {
					log.warning("Unable to login after 10 attempts. Stopping script.");
					log.severe("It seems you are actually already logged in?");
					stopScript(false);
				}
				invalidCount++;
				log.severe("Waiting for logout..");
				sleep(5000, 15000);
			}
			if (returnText.contains("incorrect")) {
				log.warning("Failed to login five times in a row. Stopping script.");
				stopScript(false);
			}
			if (returnText.contains("invalid")) {
				if (invalidCount > 6) {
					log.warning("Unable to login after 6 attempts. Stopping script.");
					log("Please verify that your RSBot account profile is correct.");
					stopScript(false);
				}
				invalidCount++;
				return random(500, 2000);
			}
			if (returnText.contains("error connecting")) {
				iface.clickChild(INTERFACE_LOGIN_SCREEN, INTERFACE_BUTTON_BACK);
				stopScript(false);
				return random(500, 2000);
			}
			if (returnText.contains("full")) {
				if (worldFullCount > 30) {
					log("World Is Full. Waiting for 15 seconds.");
					sleep(random(10000, 15000));
					worldFullCount = 0;
				}
				sleep(random(1000, 1200));
				worldFullCount++;
			}
			if (returnText.contains("login limit exceeded")) {
				if (invalidCount > 10) {
					log.warning("Unable to login after 10 attempts. Stopping script.");
					log.severe("It seems you are actually already logged in?");
					stopScript(false);
				}
				invalidCount++;
				sleep(5000, 15000);
			}
			if (returnText.contains("world")) {
				return random(1500, 2000);
			}
			if (returnText.contains("performing login")) {
				return random(1500, 2000);
			}
		}
		if (game.getLoginIndex() == INDEX_LOGGED_OUT) {
			if (iface.get(INTERFACE_GRAPHICS_NOTICE)
					.getChild(INTERFACE_GRAPHICS_LEAVE_ALONE).isValid()) {
				iface.clickChild(INTERFACE_GRAPHICS_NOTICE,
						INTERFACE_GRAPHICS_LEAVE_ALONE);
				if (iface.get(INTERFACE_BUTTON_BACK).isValid()) {
					iface.clickChild(INTERFACE_LOGIN_SCREEN, INTERFACE_BUTTON_BACK);
				}
				return random(500, 600);
			}
			if (!atLoginScreen()) {
				iface.get(INTERFACE_MAIN).getChild(INTERFACE_MAIN_CHILD)
				.getChild(INTERFACE_MAIN_CHILD_COMPONENT_ID).action("");
				return random(500, 600);
			}
			if (isUsernameFilled() && isPasswordFilled()) {
				if (random(0, 2) == 0) {
					input.sendKey((char) KeyEvent.VK_ENTER);
				} else {
					iface.clickChild(INTERFACE_LOGIN_SCREEN, INTERFACE_BUTTON_LOGIN);
				}
				return random(500, 600);
			}
			if (!isUsernameFilled()) {
				atLoginInterface(iface.get(INTERFACE_LOGIN_SCREEN).getChild(
						INTERFACE_USERNAME_WINDOW));
				sleep(random(500, 700));
				textlength = iface.get(INTERFACE_LOGIN_SCREEN)
				.getChild(INTERFACE_USERNAME).getText().length()
				+ random(3, 5);
				for (int i = 0; i <= textlength + random(1, 5); i++) {
					input.sendKeys("\b", false);
					if (random(0, 2) == 1) {
						sleep(random(25, 100));
					}
				}
				input.sendKeys(username, false);
				return random(500, 600);
			}
			if (isUsernameFilled() && !isPasswordFilled()) {
				atLoginInterface(iface.get(INTERFACE_LOGIN_SCREEN).getChild(
						INTERFACE_PASSWORD_WINDOW));
				sleep(random(500, 700));
				textlength = iface.get(INTERFACE_LOGIN_SCREEN)
				.getChild(INTERFACE_PASSWORD).getText().length()
				+ random(3, 5);
				for (int i = 0; i <= textlength + random(1, 5); i++) {
					input.sendKeys("\b", false);
					if (random(0, 2) == 1) {
						sleep(random(25, 100));
					}
				}
				input.sendKeys(AccountManager.getPassword(Bot.getAccountName()),
						false);
			}
		}
		return random(500, 2000);
	}

	/*
	 * Returns x int based on the letters in a Child. Only the password text is
	 * needed as the username text cannot reach past the middle of the interface
	 */
	private int minX(final RSInterfaceChild a) {
		int x = 0;
		final Rectangle pos = a.getArea();
		final int dx = (int) (pos.getWidth() - 4) / 2;
		final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return 0;
		}
		for (int i = 0; i < iface.get(INTERFACE_LOGIN_SCREEN)
		.getChild(INTERFACE_PASSWORD).getText().length(); i++) {
			x += 11;
		}
		if (x > 44) {
			return (int) (pos.getMinX() + x + 15);
		} else {
			return midx + random(-dx, dx);
		}
	}

	private boolean switchingWorlds() {
		return iface.get(INTERFACE_WELCOME_SCREEN)
		.getChild(INTERFACE_WELCOME_SCREEN_TEXT_RETURN).isValid()
		&& iface.get(INTERFACE_WELCOME_SCREEN)
		.getChild(INTERFACE_WELCOME_SCREEN_TEXT_RETURN)
		.containsText("just left another world");
	}
}