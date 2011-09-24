package org.rsbot.script.antiban;

import org.rsbot.script.Antiban;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;

/*
 * Cleaned and Updated By Secret Spy
 * Updated On 10/07/10
 * Updated By Gribonn On 11/02/24
 */
@ScriptManifest(authors = { "Secret Spy", "Gribonn" }, name = "Bank pins manager", version = 2.2)
public class BankPins extends Antiban {

	private String Pin;

	@Override
	public boolean activateCondition() {
		return iface.get(13).getChild(26)
		.containsText("Please enter your PIN");
	}

	public void enterCode(final String pin) {
		final RSInterface bankPinInterface = iface.get(13);
		if (!bankPinInterface.isValid()) {
			return;
		}
		boolean solved = false;
		int number = 0;
		while (!solved) {
			for (int i = 1; i <= 4; i++) {
				if (bankPinInterface.getChild(i).getText().contains("?")) {
					number = i - 1;
					break;
				}
			}
			final int i = 6 + Integer
			.parseInt(Character.toString(pin.toCharArray()[number]));
			iface.clickChild(bankPinInterface.getChild(i));
			wait(random(500, 800));
			if (number == 3) {
				solved = true;
			}
		}
		if (random(1, 4) == 1) {
			mouse.moveSlightly();
		}
	}

	@Override
	public int loop() {
		Pin = game.getAccountPin();
		if (iface.get(13).isValid() && ((Pin == null) || (Pin.length() != 4))) {
			log("It Seems You Dont Have A Pin Added To This Account.");
			stopScript();
			return -1;
		}
		if (iface.get(14).isValid() || !iface.get(13).isValid()) {
			iface.clickChild(iface.get(14).getChild(3));
			return -1;
		}
		enterCode(Pin);
		if (iface.get(211).isValid()) {
			iface.clickChild(iface.get(211).getChild(3));
		} else if (iface.get(217).isValid()) {
			wait(random(10500, 12000));
		}
		return 500;
	}
}
