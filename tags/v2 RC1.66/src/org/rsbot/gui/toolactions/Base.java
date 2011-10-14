package org.rsbot.gui.toolactions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * @author Sorcermus
 */
public abstract class Base extends AbstractAction {

	private static final long serialVersionUID = 6858558832890334531L;

	public Base() {
	}

	public Base(final String text, final Icon icon, final String description, final char accelerator) {
		super(text, icon);
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator, Toolkit
				.getDefaultToolkit().getMenuShortcutKeyMask()));
		putValue(SHORT_DESCRIPTION, description);
	}

	@Override
	public abstract void actionPerformed(ActionEvent e);
}
