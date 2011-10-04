/*
 * 2011 Runedev development team
 * http://lazygamerz.org
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 3 of the Licence, or (at your opinion) any
 * later version.
 *
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 */
package org.rsbot;

import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.rsbot.bot.Bot;
import org.rsbot.gui.BotGUI;
import org.rsbot.util.SplashScreen;
import org.rsbot.util.Update;

/**
 * @author Runedev development team - version 1.0
 */
public class Application {

	private static BotGUI gui;
	private static Update updater = new Update(gui);

	/**
	 * Returns the Bot for any object loaded in its client. For internal use
	 * only (not useful for script writers).
	 * 
	 * @param o
	 *            Any object from within the client.
	 * @return The Bot for the client.
	 */
	public static Bot getBot(final Object o) {
		return gui.getBot(o);
	}

	/**
	 * Returns the size of the panel that clients should be drawn into. For
	 * internal use.
	 * 
	 * @return The client panel size.
	 */
	public static Dimension getPanelSize() {
		return gui.getPanel().getSize();
	}

	public static void main(final String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);

		updater.checkUpdate(true);
		SplashScreen splash = new SplashScreen(args);
		
		if (!splash.error) {
			splash.setModalityType(Dialog.ModalityType.MODELESS);
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					gui = new BotGUI();
					gui.addBot();
					SplashScreen.close();
					gui.setVisible(true);
				}
			});
		}
	}
}
