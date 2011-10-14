package org.rsbot.gui.toolactions;

import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.io.HttpClient;

/**
 * @author Sorcermus
 */
public class GoTo extends Base {

	private static final long serialVersionUID = 4443848952891372123L;

	public GoTo() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		HttpClient.openURL(GlobalConfiguration.Paths.URLs.SITE);
	}
}
