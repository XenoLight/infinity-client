package org.rsbot.bot;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.EventListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.lazygamerz.scripting.api.Camera;
import org.lazygamerz.scripting.api.Combat;
import org.lazygamerz.scripting.api.Environment;
import org.lazygamerz.scripting.api.Equipment;
import org.lazygamerz.scripting.api.GE;
import org.lazygamerz.scripting.api.Game;
import org.lazygamerz.scripting.api.GroundItems;
import org.lazygamerz.scripting.api.Interface;
import org.lazygamerz.scripting.api.Inventory;
import org.lazygamerz.scripting.api.Keyboard;
import org.lazygamerz.scripting.api.Lobby;
import org.lazygamerz.scripting.api.Magic;
import org.lazygamerz.scripting.api.Menu;
import org.lazygamerz.scripting.api.Mouse;
import org.lazygamerz.scripting.api.NPC;
import org.lazygamerz.scripting.api.Nodes;
import org.lazygamerz.scripting.api.Objects;
import org.lazygamerz.scripting.api.Player;
import org.lazygamerz.scripting.api.Prayer;
import org.lazygamerz.scripting.api.Screen;
import org.lazygamerz.scripting.api.Settings;
import org.lazygamerz.scripting.api.Store;
import org.lazygamerz.scripting.api.Summoning;
import org.lazygamerz.scripting.api.Tile;
import org.lazygamerz.scripting.api.Walk;
import org.rsbot.Application;
import org.rsbot.client.Client;
import org.rsbot.client.input.Canvas;
import org.rsbot.event.EventManager;
import org.rsbot.event.events.PaintEvent;
import org.rsbot.event.events.TextPaintEvent;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Bank;
import org.rsbot.script.Calculations;
import org.rsbot.script.GrandExchange;
import org.rsbot.script.InputManager;
import org.rsbot.script.Methods;
import org.rsbot.script.ScriptHandler;
import org.rsbot.script.Skills;
import org.rsbot.script.antiban.BreakHandler;

public class Bot {
	private static Logger logger = Logger.getLogger(Bot.class.getPackage()
			.getName());

	private static String account;
	private BotStub botStub;
	private static Client client;
	public static Methods methods;
	private Component panel;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	private static EventManager eventManager;
	private BufferedImage backBuffer;
	private static BufferedImage image;
	private static InputManager im;
	private RSLoader loader;
	private static ScriptHandler sh;
	private final BreakHandler bh;
	private final Map<String, EventListener> listeners;

	/**
	 * Enable/disable internal client logging
	 */
	public static boolean debugLogging = false;

	/**
	 * Enable/disable script debug logging
	 */
	public static boolean debugScriptLogging = false;

	
	/**
	 * Writes a client debugging log message to the log if client debugging is
	 * enabled via the Debug->Log messages setting.
	 * 
	 * The caller supplies the log instance use to log the message in order to
	 * ensure the correct class is attributed to the message.
	 * 
	 * Example: Logger logger = Logger.getLogger(this.getClass().getName());
	 * 
	 * debug(logger, "your message here");
	 * 
	 * @param logger
	 *            Caller supplied logger used to log the messsge
	 * @param s
	 *            String containing the message to be logged.
	 */
	public static void debug(final Logger logger, final String s) {
		if (debugLogging && logger != null)
			logger.info(s);
	}

	/**
	 * Logs the specified string as an INFO record using the Bot class' Logger.
	 * 
	 * @param s
	 */
	public static void debug(final String s) {
		if (debugLogging)
			logger.info(s);
	}

	/**
	 * Whether or not user input is allowed despite a script's preference.
	 */
	public volatile boolean overrideInput = false;

	/**
	 * Whether or not rendering is enabled.
	 */
	public volatile boolean disableRendering = false;

	/**
	 * Whether or not graphics are enabled.
	 */
	public volatile boolean disableGraphics = false;

	/**
	 * Whether or not all randoms are enabled.
	 */
	public static boolean disableRandoms = false;
	public static boolean disableBeehiveSolver = false;
	public static boolean disableCapnArnav = false;
	public static boolean disableCerter = false;
	public static boolean disableDrillDemon = false;
	public static boolean disableExam = false;
	public static boolean disableFirstTimeDeath = false;
	public static boolean disableFreakyForester = false;
	public static boolean disableFrogCave = false;
	public static boolean disableGraveDigger = false;
	public static boolean disableLeaveSafeArea = false;
	public static boolean disableLostAndFound = false;
	public static boolean disableMaze = false;
	public static boolean disableMime = false;
	public static boolean disableMolly = false;
	public static boolean disablePillory = false;
	public static boolean disablePinball = false;
	public static boolean disablePrison = false;
	public static boolean disableQuizSolver = false;
	public static boolean disableSandwhichLady = false;
	public static boolean disableScapeRuneIsland = false;
	/**
	 * Whether or not all antibans Solving is enabled.
	 */
	public static boolean disableAntibans = false;
	public static boolean disableBreakHandler = false;
	public static boolean disableAutoLogin = false;
	public static boolean disableBankPins = false;
	public static boolean disableImprovedRewardsBox = false;
	public static boolean disableInterfaceCloser = false;
	public static boolean disableSystemUpdate = false;
	/**
	 * Method singletons.
	 */
	public static Bank bank;
	public static Calculations calculate;
	public static Camera camera;
	public static Combat combat;
	public static Environment enviro;
	public static Equipment equipment;
	public static Game game;
	public static GrandExchange grandExchange;
	public static GE ge;
	public static GroundItems ground;
	public static Interface iface;
	public static Inventory inventory;
	public static Keyboard keyboard;
	public static Lobby lobby;
	public static Magic magic;
	public static Menu menu;
	public static Mouse mouse;
	public static NPC npc;
	public static Nodes nodes;
	public static Objects objects;
	public static Player player;
	public static Prayer prayer;
	public static Settings settings;
	public static Skills skills;
	public static Store store;
	public static Screen screen;
	public static Summoning summoning;
	public static Tile tile;
	public static Walk walk;
	public static String getAccountName() {
		return account;
	}

	public static BufferedImage getBotBuffer() {
		return getImage();
	}

	public static Canvas getCanvas() {
		if (client == null) {
			return null;
		}
		return (Canvas) client.getCanvas();
	}

	public static Client getClient() {
		return client;
	}

	public static EventManager getEventManager() {
		return eventManager;
	}

	public static BufferedImage getImage() {
		return image;
	}

	public static InputManager getInputManager() {
		return im;
	}

	public static ScriptHandler getScriptHandler() {
		return sh;
	}

	public static boolean setAccount(final String name) {
		boolean exist = false;

		for (final String s : AccountManager.getAccountNames().toArray(new String[0])) {
			if (s.toLowerCase().equals(name.toLowerCase())) {
				exist = true;
			}
		}
		if (exist) {
			account = name;
			return true;
		}
		account = null;
		return false;
	}

	/**
	 * Defines what types of input are enabled when overrideInput is false.
	 * Defaults to 'keyboard only' whenever a script is started.
	 */
	public volatile int inputFlags = Environment.inputKeyboard
	| Environment.inputMouse;

	public Bot() {
		im = new InputManager();
		loader = new RSLoader();
		final Dimension size = Application.getPanelSize();
		loader.setCallback(new Runnable() {

			@Override
			public void run() {
				try {
					setClient((Client) loader.getClient());
					resize(size.width, size.height);
					methods.menu.setupListener();
				} catch (final Exception ignored) {
				}
			}
		});
		sh = new ScriptHandler(this);
		bh = new BreakHandler();
		backBuffer = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
		eventManager = new EventManager();
		listeners = new TreeMap<String, EventListener>();

	}

	public void addListener(final Class<?> clazz) {
		final EventListener el = instantiateListener(clazz);
		listeners.put(clazz.getName(), el);
		eventManager.addListener(el);
	}

	public BotStub getBotStub() {
		return botStub;
	}

	public BreakHandler getBreakHandler() {
		return bh;
	}

	public Graphics getBufferGraphics() {
		final Graphics back = backBuffer.getGraphics();
		if (disableGraphics) {
			paintEvent.graphics = null;
			textPaintEvent.graphics = null;
			eventManager.processEvent(paintEvent);
			eventManager.processEvent(textPaintEvent);
			return backBuffer.getGraphics();
		}
		paintEvent.graphics = back;
		textPaintEvent.graphics = back;
		textPaintEvent.idx = 0;
		eventManager.processEvent(paintEvent);
		eventManager.processEvent(textPaintEvent);
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	public RSLoader getLoader() {
		return loader;
	}

	public Methods getMethods() {
		return methods;
	}

	public boolean hasListener(final Class<?> clazz) {
		return clazz != null && listeners.get(clazz.getName()) != null;
	}

	private EventListener instantiateListener(final Class<?> clazz) {
		try {
			EventListener listener;
			try {
				final Constructor<?> constructor = clazz
				.getConstructor(Bot.class);
				listener = (EventListener) constructor.newInstance(this);
			} catch (final Exception e) {
				listener = clazz.asSubclass(EventListener.class).newInstance();
			}
			return listener;
		} catch (final Exception ignored) {
		}
		return null;
	}

	public void removeListener(final Class<?> clazz) {
		final EventListener el = listeners.get(clazz.getName());
		listeners.remove(clazz.getName());
		eventManager.removeListener(el);
	}

	public void resize(final int width, final int height) {
		backBuffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		/* client reads size of loader applet for drawing */
		loader.setSize(width, height);
		/* simulate loader repaint awt event dispatch */
		loader.update(backBuffer.getGraphics());
		loader.paint(backBuffer.getGraphics());
	}

	private void setClient(final Client cl) {
		client = cl;
		client.setCallback(new CallbackImpl(this));

		methods = new Methods();
		/* methods file loads */
		bank = new Bank();
		calculate = new Calculations();
		camera = new Camera();
		combat = new Combat();
		equipment = new Equipment();
		enviro = new Environment();
		game = new Game();
		grandExchange = new GrandExchange();
		ge = new GE();
		ground = new GroundItems();
		iface = new Interface();
		inventory = new Inventory();
		keyboard = new Keyboard();
		lobby = new Lobby();
		magic = new Magic();
		menu = new Menu();
		mouse = new Mouse();
		npc = new NPC();
		nodes = new Nodes();
		objects = new Objects();
		player = new Player();
		prayer = new Prayer();
		settings = new Settings();
		skills = new Skills();
		store = new Store();
		screen = new Screen();
		summoning = new Summoning();
		tile = new Tile();
		walk = new Walk();

		methods.init();

		sh.addRandoms();
		sh.addAntiban();
	}

	public void setPanel(final Component c) {
		this.panel = c;
	}

	public void start() {
		try {
			loader.paint(image.getGraphics());
			loader.load();
			if (loader.getTargetName() == null) {
				return;
			}
			botStub = new BotStub(loader);
			loader.setStub(botStub);
			eventManager.start();
			botStub.setActive(true);
			final ThreadGroup tg = new ThreadGroup("RSClient-" + hashCode());
			final Thread thread = new Thread(tg, loader, "Loader");
			thread.start();
		} catch (final Exception ignored) {
		}
	}

	public void stop() {
		eventManager.killThread(false);
		sh.stopScript();
		loader.stop();
		loader.destroy();
		loader = null;
	}
	
	static public void logStackTrace(Exception ex)  {
		String msg = ex.getMessage();
		StackTraceElement[] stack = ex.getStackTrace();
		logger.severe(msg);
		
		for (StackTraceElement trace : stack)  {
			logger.severe(trace.toString());
		}
	}
}
