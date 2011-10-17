package org.rsbot.gui.toolactions;

import org.rsbot.util.io.HttpClient;

/**
 * @author Sorcermus
 */
public class GoToTwitter extends Base {

	private static final long serialVersionUID = 3854474262656272123L;

	public GoToTwitter() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		HttpClient.openURL("http://twitter.com/bobbybighoof");
	}
}
