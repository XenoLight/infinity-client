package org.rsbot.gui.toolactions;

import org.rsbot.gui.AccountManager;

/**
 * @author Sorcermus
 */
public class Account extends Base {

	private static final long serialVersionUID = 4443848952891372123L;

	public Account() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		AccountManager.getInstance().showGUI();
	}
}
