package org.rsbot.gui.toolactions;

import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.io.HttpClient;

/**
 * @author Sorcermus
 */
public class GoToProject extends Base {

	private static final long serialVersionUID = 4455772293454372123L;

	public GoToProject() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		HttpClient.openURL(GlobalConfiguration.Paths.URLs.PROJECT);
	}
}
