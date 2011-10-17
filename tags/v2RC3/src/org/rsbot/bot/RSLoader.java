package org.rsbot.bot;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rsbot.Application;
import org.rsbot.client.Loader;
import org.rsbot.loader.GameLoader;
import org.rsbot.util.GlobalConfiguration;

/**
 * @author Qauters
 */
public class RSLoader extends Applet implements Runnable, Loader {

	private final Logger log = Logger.getLogger(RSLoader.class.getName());
	private static final long serialVersionUID = 6288499508495040201L;
	/**
	 * The applet of the client
	 */
	private Applet client;
	private Runnable loadedCallback;
	private String targetName;
	private Dimension size = Application.getPanelSize();
	/**
	 * The game class loader
	 */
	public RSClassLoader classLoader;

	@Override
	public final synchronized void destroy() {
		if (client != null) {
			client.destroy();
		}
	}

        public RSClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Applet getClient() {
		return client;
	}

	@Override
	public final Dimension getSize() {
		return size;
	}

	public String getTargetName() {
		return targetName;
	}

	@Override
	public final synchronized void init() {
		if (client != null) {
			client.init();
		}
	}

	@Override
	public boolean isShowing() {
		return true;
	}

	public void load() {
		try {
			final GameLoader gl = GameLoader.getInstance();
			targetName = gl.getTargetName();
			classLoader = new RSClassLoader(gl.getClasses(), new URL("http://" + targetName + ".com/"));
		} catch (final IOException ex) {
			log.severe("Unable to load client: " + ex.getMessage());
		}
	}

	@Override
	public final void paint(final Graphics graphics) {
		if (client != null) {
			client.paint(graphics);
		} else {
			final Font font = new Font("Helvetica", 1, 13);
			final FontMetrics fontMetrics = getFontMetrics(font);
			graphics.setColor(Color.black);
			graphics.fillRect(0, 0, 768, 503);
			graphics.setColor(new Color(150, 0, 0));
			graphics.drawRect(230, 233, 303, 33);
			final String s = "Loading...";
			graphics.setFont(font);
			graphics.setColor(Color.WHITE);
			graphics.drawString(s, (768 - fontMetrics.stringWidth(s)) / 2, 255);
		}
	}

	/**
	 * The run void of the loader
	 */
	@Override
	public void run() {
		try {
			final Class<?> c = classLoader.loadClass("client");
			client = (Applet) c.newInstance();
			loadedCallback.run();
			c.getMethod("provideLoaderApplet",
					new Class[] { java.applet.Applet.class })
					.invoke(null, this);
			client.init();
			client.start();
		} catch (final Throwable e) {
			log.severe("Unable to load client, please check your firewall and internet connection.");
			final File versionFile = new File(
					GlobalConfiguration.Paths.getVersionCache());
			if (versionFile.exists() && !versionFile.delete()) {
				log.warning("Unable to clear cache.");
			}

			log.log(Level.SEVERE, "Error reason:", e);
		}
	}

	public void setCallback(final Runnable r) {
		loadedCallback = r;
	}

	@Override
	public final void setSize(final int width, final int height) {
		super.setSize(width, height);
		size = new Dimension(width, height);
	}

	/**
	 * Overridden void start()
	 */
	@Override
	public final synchronized void start() {
		if (client != null) {
			client.start();
		}
	}

	/**
	 * Overridden void deactivate()
	 */
	@Override
	public final synchronized void stop() {
		if (client != null) {
			client.stop();
		}
	}

	/**
	 * Overridden void update(Graphics)
	 */
	@Override
	public final void update(final Graphics graphics) {
		if (client != null) {
			client.update(graphics);
		} else {
			paint(graphics);
		}
	}
}
