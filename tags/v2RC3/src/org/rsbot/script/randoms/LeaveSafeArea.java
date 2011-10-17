package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;

/**
 * @author Secret Spy
 * @version 1.1 - 02/10/11
 */
@ScriptManifest(name = "LeaveSafeArea", authors = "Secret Spy", version = 1.1)
public class LeaveSafeArea extends Random {

	@Override
	public boolean activateCondition() {
		return (iface.getChild(212, 2).containsText("things can get more")
				&& (iface.getChild(212, 2).getAbsoluteY() > 380) && (iface
						.getChild(212, 2).getAbsoluteY() < 410))
						|| (iface.getChild(236, 1).containsText("the starting area")
								&& (iface.getChild(236, 1).getAbsoluteY() > 390) && (iface
										.getChild(236, 1).getAbsoluteY() < 415));
	}

	@Override
	public int loop() {
		if (iface.canContinue()) {
			iface.clickContinue();
			sleep(random(1000, 1200));
		}
		iface.clickChild(236, 1);
		return -1;
	}

}
