package org.rsbot.gui.toolactions;

import org.rsbot.util.io.HttpClient;

/**
 * @author Sorcermus
 */
public class GoToFace extends Base {

	private static final long serialVersionUID = 4454656573454372123L;

	public GoToFace() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		HttpClient
		.openURL("http://www.facebook.com/pages/LazyGamerzorg/213068622042708");
	}
}
