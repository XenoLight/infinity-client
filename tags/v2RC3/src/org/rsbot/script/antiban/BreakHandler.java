package org.rsbot.script.antiban;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.script.Antiban;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.util.GlobalConfiguration;

/*
 * Cleaned and Updated By Secret Spy
 * Updated On 10/07/10
 */
@ScriptManifest(authors = { "Secret Spy" }, category = "AntiBan", name = "Break Handler", version = 3.1)
public class BreakHandler extends Antiban implements MessageListener {

	protected final class Break {

		private final long BreakAtMax;
		private final long BreakAtMin;
		private final long LengthMax;
		private final long LengthMin;
		private long RandBreak = 0;

		Break(final long BreakAtMin, final long BreakAtMax,
				final long LengthMin, final long LengthMax) {
			this.BreakAtMin = BreakAtMin;
			this.BreakAtMax = BreakAtMax;
			this.LengthMin = LengthMin;
			this.LengthMax = LengthMax;
			RandBreak = randBreakAt();
		}

		public long getBreakAtMax() {
			return BreakAtMax;
		}

		public long getBreakAtMin() {
			return BreakAtMin;
		}

		public long getLengthMax() {
			return LengthMax;
		}

		public long getLengthMin() {
			return LengthMin;
		}

		public long randBreakAt() {
			return randLong(BreakAtMin, BreakAtMax);
		}

		public long randLength() {
			return randLong(LengthMin, LengthMax);
		}

		private long randLong(final long min, final long max) {
			return min + (long) (java.lang.Math.random() * (max - min));
		}

		public boolean shouldBreak(final long startTime, final long curTime) {
			if (curTime - startTime > RandBreak) {
				RandBreak = randBreakAt();
				return true;
			} else {
				return false;
			}
		}
	}

	boolean TenSecondsWaiting = false;

	private final ArrayList<Break> Breaks = new ArrayList<Break>();
	private Break CurrentBreak;
	private long CurrentTime = System.currentTimeMillis();
	private Iterator<Break> it;
	private boolean Reset;
	private boolean SetConfigs = true;
	private long StartTime = System.currentTimeMillis();
	private final File breaksFile = new File(
			GlobalConfiguration.Paths.getBreaksCache());
	private String[] Props = new String[] { "15|45, 2|4", "75|105, 2|4",
			"135|165, 10|20", "205|235, 2|4", "265|295, 2|4",
			"330|360, 120|180", "540|570, 2|4", "600|630, 2|4",
			"660|690, 10|20", "750|780, 2|4", "810|840, 2|4",
	"900|960, 360|480" };
	@Override
	public boolean activateCondition() {
		if (player.getMine().isInCombat()) {
			return false;
		}
		if (SetConfigs) {
			getConfig();
			StartTime = System.currentTimeMillis();
			SetConfigs = false;
		}
		if (Breaks.isEmpty()) {
			return false;
		}
		if (Reset) {
			it = Breaks.iterator();
			StartTime = System.currentTimeMillis();
			Reset = false;
		}
		if (CurrentBreak == null) {
			CurrentBreak = it.next();
		}
		CurrentTime = System.currentTimeMillis();
		if (CurrentBreak.shouldBreak(StartTime, CurrentTime)) {
			return true;
		}
		return false;
	}

	private String cTime(long eTime) {
		final long hrs = eTime / 1000 / 3600;
		eTime -= hrs * 3600 * 1000;
		final long mins = eTime / 1000 / 60;
		eTime -= mins * 60 * 1000;
		final long secs = eTime / 1000;
		return String.format("%1$02d:%2$02d:%3$02d", hrs, mins, secs);
	}

	public void getConfig() {
		if (!breaksFile.exists() || parseBreaks() == null
				|| parseBreaks().length != 12) {
			try {
				if (breaksFile.createNewFile()) {
					final BufferedWriter out = new BufferedWriter(
							new FileWriter(breaksFile));
					for (int i = 0; i < Props.length; i++) {
						out.write(Props[i]);
						if (i + 1 < Props.length) {
							out.write(":");
						}
					}
					out.close();
				}
			} catch (final IOException ignored) {
			}
		} else {
			Props = parseBreaks();
		}

		for (final String val : Props) {
			final String breakVal = val.substring(0, val.indexOf(',')).trim();
			final String lengthVal = val.substring(val.indexOf(',') + 1).trim();
			long breakAtMin, breakAtMax, lengthMin, lengthMax;

			if (breakVal.indexOf('|') != -1) {
				breakAtMin = Long.parseLong(breakVal.substring(0,
						breakVal.indexOf('|')).trim());
				breakAtMax = Long.parseLong(breakVal.substring(
						breakVal.indexOf('|') + 1).trim());
			} else {
				breakAtMax = Long.parseLong(breakVal);
				breakAtMin = breakAtMax - breakAtMax / 4;
			}

			if (lengthVal.indexOf('|') != -1) {
				lengthMin = Long.parseLong(lengthVal.substring(0,
						lengthVal.indexOf('|')).trim());
				lengthMax = Long.parseLong(lengthVal.substring(
						lengthVal.indexOf('|') + 1).trim());
			} else {
				lengthMax = Long.parseLong(lengthVal);
				lengthMin = lengthMax / 2;
			}

			/* convert to ms */
			breakAtMin *= 60000;
			breakAtMax *= 60000;
			lengthMin *= 60000;
			lengthMax *= 60000;

			final Break b = new Break(breakAtMin, breakAtMax, lengthMin,
					lengthMax);
			Breaks.add(b);
		}

		Collections.sort(Breaks, new Comparator<Break>() {

			@Override
			public int compare(final Break b1, final Break b2) {
				return (int) (b1.getBreakAtMin() - b2.getBreakAtMin());
			}
		});

		it = Breaks.iterator();
	}

	@Override
	public boolean isItemSelected() { // Credits to ByteCode for function
		for (final RSInterfaceChild com : inventory.getInterface().getChildren()) {
			if (com.getBorderThickness() == 2) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int loop() {
		if (CurrentBreak == null) {
			return -1;
		}
		final long breakLength = CurrentBreak.randLength();
		log("I Have Botted For: " + cTime(CurrentTime - StartTime) + "");
		log("I'm Now Taking A Break For: " + cTime(breakLength) + "");
		do {
			for (int i = 0; TenSecondsWaiting && (i < 20); i++) {
				wait(random(500, 650));
				if (player.getMine().isInCombat()) {
					log.warning("To My Knowledge You were attacked while waiting");
					log.warning("I Am Now Returning To The Script.");
					return -1;
				}
			}
			game.logout();
			wait(random(2000, 4000));
		} while (game.isLoggedIn());
		CurrentBreak = null;
		if (!it.hasNext()) {
			Reset = true;
		}
		return (int) breakLength;
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		if (e.getMessage().contains("10 seconds")) {
			TenSecondsWaiting = true;
		}
	}

	private String[] parseBreaks() {
		try {
			String[] temp = null;
			if (breaksFile.exists()) {
				final BufferedReader in = new BufferedReader(new FileReader(
						breaksFile));
				String line;
				while ((line = in.readLine()) != null) {
					if (line.contains(":")) {
						temp = line.split(":");
					}
				}
				in.close();
			}
			return temp;
		} catch (final Exception e) {
			return null;
		}
	}
}
