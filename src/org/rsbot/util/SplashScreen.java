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
package org.rsbot.util;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import org.rsbot.loader.GameLoader;
import org.rsbot.log.LabelLogHandler;
import org.rsbot.log.LogOutputStream;
import org.rsbot.log.SystemConsoleHandler;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;

/**
 * This is the main splash or load screen for the app. It handles the start up
 * of the client app logic in the gonfig file and other areas such as gameloader
 * update files
 * 
 * @auther Sorcermus - version 1.0 April 21, 2010
 * @author Sorcermus - version 1.1 May 29, 2011
 * @authoe Runedev development team - version 2.0
 */
public class SplashScreen extends JDialog {

    private final static Logger log = Logger.getLogger(SplashScreen.class.getName());
    private static final long serialVersionUID = 5520543482560560389L;

    private static void bootstrap() {
        Logger.getLogger("").addHandler(new SystemConsoleHandler());
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            private final Logger log = Logger.getLogger("EXCEPTION");

            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                final String ex = "Exception", msg = t.getName() + ": ";
                if (GlobalConfiguration.RUNNING_FROM_JAR) {
                    Logger.getLogger(ex).severe(msg + e.toString());
                } else {
                    log.logp(Level.SEVERE, ex, "", msg, e);
                }
            }
        });
        if (!GlobalConfiguration.RUNNING_FROM_JAR) {
            System.setErr(new PrintStream(new LogOutputStream(Logger
                    .getLogger("STDERR"), Level.SEVERE), true));
        }
    }

    public static void close() {
        if (instance != null) {
            instance.dispose();
        }
    }
    
    public final boolean error;
    private String taskError;
    private static SplashScreen instance = null;
    private final String[] args;

    public SplashScreen(final String[] args) {
    	instance = this;
        this.args = args;
        
        setModalityType(Dialog.ModalityType.MODELESS);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception ignored) {
        }
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(1);
            }
        });
		setTitle(GlobalConfiguration.NAME + " v"
				+ ((float) GlobalConfiguration.getVersion() / 100)
				+ " [Beta]");
        setIconImage(GlobalConfiguration
                .getImage(GlobalConfiguration.Paths.Resources.ICON));
        final ImageIcon icon = new ImageIcon();
        icon.setImage(GlobalConfiguration
                .getImage(GlobalConfiguration.Paths.Resources.SPLASH));
        final JLabel label1 = new JLabel();
        label1.setIcon(icon);
        final LabelLogHandler handler = new LabelLogHandler();
        Logger.getLogger("").addHandler(handler);
        handler.label.setBorder(javax.swing.BorderFactory.createTitledBorder(" Startup Events "));
        final Font font = handler.label.getFont();
        handler.label.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
        handler.label.setPreferredSize(new Dimension(400, 30 + 12));
        final JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setOpaque(true);
        add(label1, BorderLayout.NORTH);
        add(progress, BorderLayout.CENTER);
        add(handler.label, BorderLayout.SOUTH);
        pack();
        log.info("Loading");
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setVisible(true);
        setModalityType(Dialog.ModalityType.MODELESS);
        setAlwaysOnTop(true);
        final List<Callable<Object>> tasks = new ArrayList<Callable<Object>>(8);

        String error = null;
        log.info("Starting Bootstrap");
        bootstrap();

        log.info("Creating Directories");
        GlobalConfiguration.createDirectories();

        log.info("Extracting Resources");
        tasks.add(Executors.callable(new Runnable() {

            @Override
            public void run() {
                try {
                    extractResources();
                } catch (final IOException ignored) {
                }
            }
        }));

        log.info("Downloading Resources");
        for (final Entry<String, File> item :
            GlobalConfiguration.Paths.getDownloadCaches().entrySet()) {
            try {
                HttpClient.download(new URL(item.getKey()), item.getValue());
            } catch (final IOException ignored) {
            }
        }

        log.info("Starting game client");
        tasks.add(Executors.callable(new Runnable() {

            public void run() {
                try {
                    GameLoader.getInstance().load();
                } catch (final Exception e) {
                    taskError = "Client error: " + e.getMessage();
                }
            }
        }));

        if (error == null) {
            log.info("Running tasks (may take a few minutes)");
            final ExecutorService pool = Executors.newCachedThreadPool();
            try {
                pool.invokeAll(tasks);
                pool.shutdown();
                if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
                    error = "Could not complete tasks";
                }
            } catch (final InterruptedException ignored) {
            }

            log.info("Checking for client updates");
            if (GameLoader.getInstance().isOutdated()) {
                error = "Bot is outdated, please wait and try again later";
            }
        }


        error = error == null ? taskError : error;

        if (error == null) {
            this.error = false;
            log.info("Loading Client");
            GlobalConfiguration.registerLogging();
            Logger.getLogger("").removeHandler(handler);
        } else {
            this.error = true;
            progress.setIndeterminate(false);
            log.severe(error);
        }
    }

    private void extractResources() throws IOException {
        final ClassLoader loader = getClass().getClassLoader();
        final String root = GlobalConfiguration.RUNNING_FROM_JAR ? GlobalConfiguration.Paths.Resources.ROOT
                + "/"
                : GlobalConfiguration.Paths.ROOT + File.separator;

        if (GlobalConfiguration.RUNNING_FROM_JAR) {
            try {
                if (GlobalConfiguration.getCurrentOperatingSystem() == GlobalConfiguration.OperatingSystem.WINDOWS) {
                    IOHelper.saveto(
                            loader.getResourceAsStream(root
                            + GlobalConfiguration.Paths.COMPILE_SCRIPTS_BAT),
                            GlobalConfiguration.Paths.getHomeDirectory()
                            + File.separator
                            + GlobalConfiguration.Paths.COMPILE_SCRIPTS_BAT);
                    IOHelper.saveto(
                            loader.getResourceAsStream(root
                            + GlobalConfiguration.Paths.COMPILE_FIND_JDK),
                            GlobalConfiguration.Paths.getHomeDirectory()
                            + File.separator
                            + GlobalConfiguration.Paths.COMPILE_FIND_JDK);
                } else {
                    IOHelper.saveto(
                            loader.getResourceAsStream(root
                            + GlobalConfiguration.Paths.COMPILE_SCRIPTS_SH),
                            GlobalConfiguration.Paths.getHomeDirectory()
                            + File.separator
                            + GlobalConfiguration.Paths.COMPILE_SCRIPTS_SH);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } else {
            if (args.length > 2) {
                if (args[0].toLowerCase().startsWith("delete")) {
                    final File jarOld = new File(args[1]);
                    if (jarOld.exists()) {
                        jarOld.delete();
                    }
                }
            }
        }
    }
}
