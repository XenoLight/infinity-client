package org.rsbot.gui.toolactions;

import org.rsbot.util.Screenshot;

/**
 * @author Sorcermus
 */
public class ShotUn extends Base {

	private static final long serialVersionUID = 89567527547437753L;

	public ShotUn() {
	}

	@Override
	public void actionPerformed(final java.awt.event.ActionEvent e) {
		Screenshot.takeScreenshot(false);
	}
}
