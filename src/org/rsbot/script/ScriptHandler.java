package org.rsbot.script;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rsbot.bot.Bot;
import org.rsbot.gui.BotToolBar;
import org.rsbot.script.antiban.BankPins;
import org.rsbot.script.antiban.BreakHandler;
import org.rsbot.script.antiban.ImprovedRewardsBox;
import org.rsbot.script.antiban.InterfaceCloser;
import org.rsbot.script.antiban.LoginBot;
import org.rsbot.script.antiban.SystemUpdate;
import org.rsbot.script.internal.event.ScriptListener;
import org.rsbot.script.internal.util.Global;
import org.rsbot.script.internal.util.UncachedClassLoader;
import org.rsbot.script.randoms.BeehiveSolver;
import org.rsbot.script.randoms.CapnArnav;
import org.rsbot.script.randoms.Certer;
import org.rsbot.script.randoms.DrillDemon;
import org.rsbot.script.randoms.Exam;
import org.rsbot.script.randoms.FirstTimeDeath;
import org.rsbot.script.randoms.FreakyForester;
import org.rsbot.script.randoms.FrogCave;
import org.rsbot.script.randoms.GraveDigger;
import org.rsbot.script.randoms.LeaveSafeArea;
import org.rsbot.script.randoms.LostAndFound;
import org.rsbot.script.randoms.Maze;
import org.rsbot.script.randoms.Mime;
import org.rsbot.script.randoms.Molly;
import org.rsbot.script.randoms.Pillory;
import org.rsbot.script.randoms.Pinball;
import org.rsbot.script.randoms.Prison;
import org.rsbot.script.randoms.QuizSolver;
import org.rsbot.script.randoms.SandwhichLady;
import org.rsbot.script.randoms.ScapeRuneIsland;
import org.rsbot.util.GlobalConfiguration;

public class ScriptHandler {
	private final Bot bot;
	public static HashMap<Integer, Script> scripts = new HashMap<Integer, Script>();
	private static HashMap<Integer, Thread> scriptThreads = new HashMap<Integer, Thread>();
	public static List<UncachedClassLoader> getLoaders() {
		final List<UncachedClassLoader> loaders = new ArrayList<UncachedClassLoader>();
		final ArrayList<String> paths = new ArrayList<String>(2);
		if (!GlobalConfiguration.RUNNING_FROM_JAR) {
			final String rel = "." + File.separator
			+ GlobalConfiguration.Paths.SCRIPTS_NAME_SRC;
			paths.add(rel);
		} else {
			/*
			 * Generate the path of the scripts folder in the jar
			 */
			final URL version = GlobalConfiguration.class.getClassLoader()
			.getResource(GlobalConfiguration.Paths.Resources.VERSION);
			String p = version
			.toString()
			.replace("jar:file:", "")
			.replace(GlobalConfiguration.Paths.Resources.VERSION,
					GlobalConfiguration.Paths.SCRIPTS);
			try {
				p = URLDecoder.decode(p, "UTF-8");
			} catch (final UnsupportedEncodingException ignored) {
			}
			paths.add(p);
		}
		paths.add(GlobalConfiguration.Paths.getScriptsDirectory());
		paths.add(GlobalConfiguration.Paths.getScriptsPrecompiledDirectory());

		/*
		 * Add all jar files in the precompiled scripts directory
		 */
		final File psdir = new Global(
				GlobalConfiguration.Paths.getScriptsPrecompiledDirectory());
		if (psdir.exists()) {
			for (final File file : psdir.listFiles()) {
				if (file.getName().endsWith(".jar!")) {
					paths.add(file.getPath());
				}
			}
		}

		for (final String path : paths) {
			try {
				final String url = new Global(path).toURI().toURL().toString();
				loaders.add(new UncachedClassLoader(url,
						UncachedClassLoader.class.getClassLoader()));
			} catch (final MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return loaders;
	}
	private final Set<ScriptListener> listeners = Collections
	.synchronizedSet(new HashSet<ScriptListener>());

	private volatile boolean screenshotOnFinish = false;

	private final ArrayList<org.rsbot.script.Random> randoms = new ArrayList<org.rsbot.script.Random>();

	private final ArrayList<org.rsbot.script.Antiban> antiban = new ArrayList<org.rsbot.script.Antiban>();
	public ScriptHandler(final Bot bot) {
		this.bot = bot;
	}

	public void addAntiban() {
		try {
			antiban.add(new BankPins());
			antiban.add(new BreakHandler());
			antiban.add(new ImprovedRewardsBox());
			antiban.add(new InterfaceCloser());
			antiban.add(new LoginBot());
			antiban.add(new SystemUpdate());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void addRandoms() {
		try {
			randoms.add(new BeehiveSolver());
			randoms.add(new CapnArnav());
			randoms.add(new Certer());
			randoms.add(new DrillDemon());
			randoms.add(new FreakyForester());
			randoms.add(new FrogCave());
			randoms.add(new GraveDigger());
			randoms.add(new LostAndFound());
			randoms.add(new Maze());
			randoms.add(new Mime());
			randoms.add(new Molly());
			randoms.add(new Exam());
			randoms.add(new Pillory());
			randoms.add(new Pinball());
			randoms.add(new Prison());
			randoms.add(new QuizSolver());
			randoms.add(new SandwhichLady());
			randoms.add(new ScapeRuneIsland());
			randoms.add(new FirstTimeDeath());
			randoms.add(new LeaveSafeArea());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void addScriptListener(final ScriptListener l) {
		listeners.add(l);
	}

	private void addScriptToPool(final Script ss, final Thread t) {
		for (int off = 0; off < scripts.size(); off++) {
			if (!scripts.containsKey(off)) {
				scripts.put(off, ss);
				ss.setID(off);
				scriptThreads.put(off, t);
				return;
			}
		}
		ss.setID(scripts.size());
		scripts.put(scripts.size(), ss);
		scriptThreads.put(scriptThreads.size(), t);
	}

	public Collection<org.rsbot.script.Antiban> getAntiban() {
		return antiban;
	}

	public Bot getBot() {
		return bot;
	}

	public Collection<org.rsbot.script.Random> getRandoms() {
		return randoms;
	}

	public Map<Integer, Script> getRunningScripts() {
		return Collections.unmodifiableMap(scripts);
	}

	public boolean isScreenshotOnFinish() {
		return screenshotOnFinish;
	}

	public void pauseScript(final int id) {
		final Script s = scripts.get(id);
		s.setPaused(!s.isPaused());
		if (s.isPaused()) {
			for (final ScriptListener l : listeners) {
				l.scriptPaused(this, s);
			}
		} else {
			for (final ScriptListener l : listeners) {
				l.scriptResumed(this, s);
			}
		}

	}

	public void removeScriptListener(final ScriptListener l) {
		listeners.remove(l);
	}

	public void runScript(final Script script, final Map<String, String> map) {
		script.init(bot.getMethods());

		for (final ScriptListener l : listeners) {
			l.scriptStarted(this, script);
		}

		String tName = "";

		if (script.getClass().isAnnotationPresent(ScriptManifest.class))
			tName = script.getClass().getAnnotation(ScriptManifest.class)
			.name();
		else
			tName = script.getClass().getName();

		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				script.run(map);
			}
		}, "Script-" + tName);

		addScriptToPool(script, t);
		t.start();
	}

	public void setScreenshotOnFinish(final boolean screenshotOnFinish) {
		this.screenshotOnFinish = screenshotOnFinish;
	}

	public void stopAllScripts() {
		for (final int i : scripts.keySet()) {
			stopScript(i);
		}
	}

	public void stopScript() {
		final Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			final Script script = scripts.get(i);
			if (script != null && script.isRunning()) {
				if (scriptThreads.get(i) == curThread) {
					stopScript(i);
				}
			}
		}
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	public void stopScript(final int id) {
		stopScript(id, false);
		BotToolBar.stopScriptButton.setEnabled(false);
	}

	/**
	 * @param id
	 *            Id of the script to stop
	 * @param forceStop
	 *            Interrupt script thread
	 */
	public void stopScript(final int id, final boolean forceStop) {
		final Script script = scripts.get(id);
		if (script != null) {
			script.deactivate(id);
			scripts.remove(id);

			final Thread t = scriptThreads.get(id);
			if (t != null && forceStop)
				t.interrupt();

			scriptThreads.remove(id);
			for (final ScriptListener l : listeners) {
				l.scriptStopped(this, script);
			}
		}
	}

	public void stopScriptAntiBan(final boolean activateAntiban) {
		final Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			final Script script = scripts.get(i);
			if (script != null && script.isRunning()) {
				if (scriptThreads.get(i) == curThread) {
					stopScript(i);
				}
			}
		}
		Bot.disableAntibans = activateAntiban;
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	public void stopScriptAntiRand(final boolean activateRandom) {
		final Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			final Script script = scripts.get(i);
			if (script != null && script.isRunning()) {
				if (scriptThreads.get(i) == curThread) {
					stopScript(i);
				}
			}
		}
		Bot.disableRandoms = activateRandom;
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	public void updateInput(final Bot bot, final int mask) {
		for (final ScriptListener l : listeners) {
			l.inputChanged(bot, mask);
		}
	}
}