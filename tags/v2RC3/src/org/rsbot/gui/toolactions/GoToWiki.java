package org.rsbot.gui.toolactions;

import org.rsbot.util.io.HttpClient;

/**
 * @author Sorcermus
 */
public class GoToWiki extends Base {

	private static final long serialVersionUID = 4443848952891372123L;

	public GoToWiki() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		HttpClient.openURL("http://runedev.wikia.com/wiki/RuneDev_Wiki");
	}
}
