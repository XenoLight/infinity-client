package org.rsbot.script;

import java.util.EventListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.rsbot.bot.Bot;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.gui.BotToolBar;
import org.rsbot.script.antiban.BankPins;
import org.rsbot.script.antiban.BreakHandler;
import org.rsbot.script.antiban.ImprovedRewardsBox;
import org.rsbot.script.antiban.InterfaceCloser;
import org.rsbot.script.antiban.LoginBot;
import org.rsbot.script.antiban.SystemUpdate;
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
import org.rsbot.script.wrappers.RSPlayer;

public abstract class Script extends Methods implements EventListener {

	Set<Script> delegates = new HashSet<Script>();
	Methods methods;
	public int ID = -1;
	public volatile boolean isActive = false;
	public volatile boolean isPaused = false;
	private volatile boolean random = false;
	private volatile boolean ban = false;
	
	/**
	 * Local reference for the player.getMine().  Must be
	 * refreshed after a BreakHandler break.
	 */
	public RSPlayer me = null;

	private void blockEvents(final boolean paint) {
		for (final Script s : delegates) {
			Bot.getEventManager().removeListener(s);
			if (paint && s instanceof PaintListener) {
				Bot.getEventManager().addListener(s,
						EventMulticaster.PAINT_EVENT);
			}
		}
		Bot.getEventManager().removeListener(this);
		if (paint && this instanceof PaintListener) {
			Bot.getEventManager().addListener(this,
					EventMulticaster.PAINT_EVENT);
		}
	}

	private boolean checkForAntiban() {
		if (Bot.disableAntibans) {
			return false;
		}
		
		for (final Antiban antiban : Bot.getScriptHandler().getAntiban()) {
			if (antiban instanceof BreakHandler) {
				if (Bot.disableBreakHandler) {
					continue;
				}
			}
			if (antiban instanceof LoginBot) {
				if (Bot.disableAutoLogin) {
					continue;
				}
			}
			if (antiban instanceof BankPins) {
				if (Bot.disableBankPins) {
					continue;
				}
			}
			if (antiban instanceof ImprovedRewardsBox) {
				if (Bot.disableImprovedRewardsBox) {
					continue;
				}
			}
			if (antiban instanceof InterfaceCloser) {
				if (Bot.disableInterfaceCloser) {
					continue;
				}
			}
			if (antiban instanceof SystemUpdate) {
				if (Bot.disableSystemUpdate) {
					continue;
				}
			}
			
			if (antiban.isEnabled()) {
				if (antiban.activateCondition()) {
					
					// For BreakHandler and SystemUpdate, we call the prepareForBreak()
					// method to give the script a chance to clean up.
					// If that returns false, skip running BreakHandler.
					if ((antiban instanceof BreakHandler ||
						 antiban instanceof SystemUpdate))  {
						
						log("BreakHandler or SystemUpdate pending.  " +
								"Notifying  the script by calling prepareForBreak()");
						// If prepare returned false and this is a BreakHandler
						// antiban, forego the break.
						if  (!prepareForBreak())  {
							if (antiban instanceof BreakHandler)  {
								return false;
							}
						}
					}
					
					this.ban = true;
					blockEvents(false);
					antiban.runAntiban();
					unblockEvents();
					this.ban = false;
					
					// If we just ran the LoginBot, refresh the "me" reference.
					if (antiban instanceof LoginBot)  {
						if (game.isLoggedIn())  {
							me = player.getMine();
						}
					}
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * The notification method to inform scripts about an impending BreakHandler
	 * break.  For example, hunter scripts would attempt to collect their traps.
	 * 
	 * @return <tt>true</tt> if the script is ready for the break to commence, 
	 * <tt>false</tt> otherwise, which aborts the BreakHandler attempt.  
	 */
	public boolean prepareForBreak() {
		return true;
	}

	private boolean checkForRandoms() {
		if (Bot.disableRandoms) {
			return false;
		}
		for (final Random randoms : Bot.getScriptHandler().getRandoms()) {
			if (randoms instanceof BeehiveSolver) {
				if (Bot.disableBeehiveSolver) {
					continue;
				}
			}
			if (randoms instanceof CapnArnav) {
				if (Bot.disableCapnArnav) {
					continue;
				}
			}
			if (randoms instanceof Certer) {
				if (Bot.disableCerter) {
					continue;
				}
			}
			if (randoms instanceof DrillDemon) {
				if (Bot.disableDrillDemon) {
					continue;
				}
			}
			if (randoms instanceof Exam) {
				if (Bot.disableExam) {
					continue;
				}
			}
			if (randoms instanceof FirstTimeDeath) {
				if (Bot.disableFirstTimeDeath) {
					continue;
				}
			}
			if (randoms instanceof FirstTimeDeath) {
				if (Bot.disableFirstTimeDeath) {
					continue;
				}
			}
			if (randoms instanceof FreakyForester) {
				if (Bot.disableFreakyForester) {
					continue;
				}
			}
			if (randoms instanceof FrogCave) {
				if (Bot.disableFrogCave) {
					continue;
				}
			}
			if (randoms instanceof GraveDigger) {
				if (Bot.disableGraveDigger) {
					continue;
				}
			}
			if (randoms instanceof LeaveSafeArea) {
				if (Bot.disableLeaveSafeArea) {
					continue;
				}
			}
			if (randoms instanceof LostAndFound) {
				if (Bot.disableLostAndFound) {
					continue;
				}
			}
			if (randoms instanceof Maze) {
				if (Bot.disableMaze) {
					continue;
				}
			}
			if (randoms instanceof Mime) {
				if (Bot.disableMime) {
					continue;
				}
			}
			if (randoms instanceof Molly) {
				if (Bot.disableMolly) {
					continue;
				}
			}
			if (randoms instanceof Molly) {
				if (Bot.disableMolly) {
					continue;
				}
			}
			if (randoms instanceof Pillory) {
				if (Bot.disablePillory) {
					continue;
				}
			}
			if (randoms instanceof Pinball) {
				if (Bot.disablePinball) {
					continue;
				}
			}
			if (randoms instanceof Prison) {
				if (Bot.disablePrison) {
					continue;
				}
			}
			if (randoms instanceof QuizSolver) {
				if (Bot.disableQuizSolver) {
					continue;
				}
			}
			if (randoms instanceof SandwhichLady) {
				if (Bot.disableSandwhichLady) {
					continue;
				}
			}
			if (randoms instanceof ScapeRuneIsland) {
				if (Bot.disableScapeRuneIsland) {
					continue;
				}
			}
			if (randoms.isEnabled()) {
				if (randoms.activateCondition()) {
					this.random = true;
					blockEvents(false);
					randoms.runRandom();
					unblockEvents();
					this.random = false;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * For internal use only. Deactivates this script if the appropriate id is
	 * provided.
	 * 
	 * @param id
	 *            The id from ScriptHandler.
	 */
	public final void deactivate(final int id) {
		if (id != this.ID) {
			throw new IllegalStateException("Invalid id!");
		}
		this.isActive = false;
	}

	/**
	 * Initializes the provided script with this script's method context and
	 * adds the delegate as a listener to the event manager, allowing it to
	 * receive client events. The script will be stored as a delegate of this
	 * script and removed from the event manager when this script is stopped.
	 * The onStart(), loop() and onFinish() methods are not automatically called
	 * on the delegate.
	 * 
	 * @param script
	 *            The script to delegate to.
	 */
	public final void delegateTo(final Script script) {
		script.init(methods);
		Bot.getEventManager().addListener(script);
		delegates.add(script);
	}

	/**
	 * Initializes this script with a given context.
	 * 
	 * @param methods
	 *            The Methods.
	 */
	public final void init(final Methods methods) {
		this.methods = methods;
	}

	/**
	 * Logs the specified string for the script if script debug logging
	 * has been turned on in the client.  Debug->Debug Script
	 * 
	 * @param s
	 * 		String to be logged.
	 */
	public void debug(String s) {if (Bot.debugScriptLogging) log(s);}
	
	/**
	 * Initializes this script with another script's context.
	 * 
	 * @param script
	 *            The context providing Script.
	 * @see #delegateTo(Script)
	 */
	public final void init(final Script script) {
		init(script.methods);
	}

	/**
	 * Returns whether or not the loop of this script is able to receive control
	 * (i.e. not paused, stopped or in random).
	 * 
	 * @return <tt>true</tt> if active; otherwise <tt>false</tt>.
	 */
	public final boolean isActive() {
		return isActive && !isPaused && !random && !ban;
	}

	/**
	 * Returns whether or not this script is paused.
	 * 
	 * @return <tt>true</tt> if paused; otherwise <tt>false</tt>.
	 */
	public final boolean isPaused() {
		return isPaused;
	}

	/**
	 * Returns whether or not this script has started and not stopped.
	 * 
	 * @return <tt>true</tt> if running; otherwise <tt>false</tt>.
	 */
	@Override
	public final boolean isRunning() {
		return isActive;
	}

	/**
	 * The main loop. Called if you return true from onStart, then continuously
	 * until a negative integer is returned or the script stopped externally.
	 * When this script is paused this method will not be called until the
	 * script is resumed. Avoid causing execution to pause using sleep() within
	 * this method in favor of returning the number of milliseconds to sleep.
	 * This ensures that pausing and anti-andoms perform normally.
	 * 
	 * @return The number of milliseconds that the manager should sleep before
	 *         calling it again. Returning a negative number will deactivate the
	 *         script.
	 */
	public abstract int loop();

	/**
	 * Perform any clean up such as unregistering any event listeners.
	 */
	public void onFinish() {
	}

	/**
	 * Called before loop() is first called, after this script has been
	 * initialized with all method providers. Override to perform any
	 * initialization or prevent script start.
	 * 
	 * @return <tt>true</tt> if the script can start.
	 */
	public boolean onStart() {
		return true;
	}

	/**
	 * The start method. Called before loop() is first called. If <tt>false</tt>
	 * is returned, the script will not start and loop() will never be called.
	 * 
	 * @param map
	 *            The arguments passed in from the description.
	 * @return <tt>true</tt> if the script should be started.
	 */
	public boolean onStart(final Map<String, String> map) {
		final String args = map.get("args");
		return args == null ? onStart(new String[0]) : onStart(args.split(","));
	}

	/**
	 * The start method. Return true if you where able to start up successfully
	 * with the given arguments.
	 * 
	 * @param args
	 *            A comma-separated list of arguments.
	 * @return <tt>true</tt> if the script should be started.
	 */
	public boolean onStart(final String[] args) {
		return onStart();
	}

	public final void run(final Map<String, String> map) {
		Bot.getEventManager().addListener(this);
		menu.setupListener();
		BotToolBar.stopScriptButton.setEnabled(true);
		log.config("Script started.");
		boolean start = false;
		try {
			start = onStart(map);
			
			// Initialize the local player reference.
			if (start && game.isLoggedIn())  {
				me = player.getMine();
			}
		} catch (final ThreadDeath ignored) {
		} catch (final Throwable e) {
			log.log(Level.SEVERE, "Error starting script: ", e);
		}
		if (start) {
			isActive = true;
			try {
				while (isActive) {
					if (!isPaused) {
						if (checkForRandoms()) {
							continue;
						}
						if (checkForAntiban()) {
							continue;
						}
						int timeOut = -1;
						try {
							// Ensure that the "me" reference gets set.
							if (me==null && game.isLoggedIn())  {
								me = player.getMine();
							}
							
							timeOut = loop();
						} catch (final ThreadDeath td) {
							break;
						} catch (final Throwable e) {
							log.log(Level.WARNING,
									"Uncaught exception from script: ", e);
						}
						if (timeOut == -1) {
							break;
						}
						try {
							sleep(timeOut);
						} catch (final ThreadDeath td) {
							break;
						} catch (final RuntimeException e) {
							break;
						}
					} else {
						try {
							sleep(1000);
						} catch (final ThreadDeath td) {
							break;
						} catch (final RuntimeException e) {
							break;
						}
					}
				}
				try {
					onFinish();
				} catch (final ThreadDeath ignored) {
				} catch (final RuntimeException e) {
					e.printStackTrace();
				}
			} catch (final ThreadDeath td) {
				onFinish();
			}
			isActive = false;
			log.warning("Script stopped.");
		} else {
			log.severe("Failed to start up.");
		}
		mouse.moveOffScreen();
		for (final Script s : delegates) {
			Bot.getEventManager().removeListener(s);
		}
		delegates.clear();
		Bot.getEventManager().removeListener(this);
		Bot.getScriptHandler().stopScript(ID);
		ID = -1;
	}

	/**
	 * For internal use only. Sets the pool id of this script.
	 * 
	 * @param id
	 *            The id from ScriptHandler.
	 */
	public final void setID(final int id) {
		if (this.ID != -1) {
			throw new IllegalStateException("Already added to pool!");
		}
		this.ID = id;
	}

	/**
	 * Pauses/resumes this script.
	 * 
	 * @param paused
	 *            <tt>true</tt> to pause; <tt>false</tt> to resume.
	 */
	public final void setPaused(final boolean paused) {
		if (isActive && !random && !ban) {
			if (paused) {
				blockEvents(true);
			} else {
				unblockEvents();
			}
		}
		this.isPaused = paused;
	}

	/**
	 * Stops the current script without logging out.
	 */
	@Override
	public void stopScript() {
		stopScript(false);
	}

	/**
	 * Stops the current script; player can be logged out before the script is
	 * stopped.
	 * 
	 * @param logout
	 *            <tt>true</tt> if the player should be logged out before the
	 *            script is stopped.
	 */
	@Override
	public void stopScript(final boolean logout) {
		log.warning("Script stopping...");
		if (logout) {
			if (bank.isOpen()) {
				bank.close();
			}
			if (methods.game.isLoggedIn()) {
				methods.game.logout();
			}
		}
		isActive = false;
	}

	private void unblockEvents() {
		for (final Script s : delegates) {
			Bot.getEventManager().removeListener(s);
			Bot.getEventManager().addListener(s);
		}
		Bot.getEventManager().removeListener(this);
		Bot.getEventManager().addListener(this);
	}
}
