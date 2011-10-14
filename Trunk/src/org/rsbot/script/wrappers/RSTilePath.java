package org.rsbot.script.wrappers;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;

public final class RSTilePath {

	private final int maxDist;
	public int maxSleepTime;
	public final Methods methods;
	public final RSTile[] tile;
	public RSTile[] tilerev;

	public RSTilePath(final RSTile[] tile) {
		methods = Bot.methods;
		maxSleepTime = 100;
		maxDist = 18;
		this.tile = tile;
	}

	public RSTile endTile() {
		return tile[tile.length - 1];
	}

	private RSTile nextTile(final RSTile path[], final int maxDist) {
		final int randomdis = random(3, 5);
		for (int i = path.length - 1; i >= 0; i--) {
			if (methods.calculate.distanceTo(path[i]) <= maxDist
					&& methods.calculate.distanceTo(path[path.length - 1]) > randomdis) {
				return path[i];
			}
		}
		return null;
	}

	public int random(final int min, final int max) {
		return (int) (Math.random() * (max - min)) + min;
	}

	public RSTile[] randomizePath(final int deviation) {
		return randomizePath(tile.clone(), deviation);
	}

	public RSTile[] randomizePath(final RSTile tile[], final int deviation) {
		for (int i = 0; i < tile.length - 1; i++) {
			tile[i] = new RSTile(
					tile[i].getX() + random(-deviation, deviation),
					tile[i].getY() + random(-deviation, deviation));
		}
		return tile;
	}

	public RSTile[] reversePath() {
		tilerev = tile.clone();
		int end = tile.length - 1;
		for (int begin = 0; begin < end; end--) {
			final RSTile temp = tilerev[begin];
			tilerev[begin] = tilerev[end];
			tilerev[end] = temp;
			begin++;
		}
		return tilerev;
	}

	public RSTile startTile() {
		return tile[0];
	}

	public boolean walkPath(final RSTile[] path) throws InterruptedException {
		final int randomdis = random(2, 6);
		RSTile nextTile = nextTile(path, maxDist);
		if (nextTile == null) {
			return false;
		}
		if (nextTile.distanceTo() > maxDist) {
			return false;
		}
		final RSTile lastTile = path[path.length - 1];
		do {
			nextTile = nextTile(path, maxDist);
			if (!methods.walk.tileMM(nextTile, 0, 0)) {
				return false;
			}
			Thread.sleep(200L);
			for (; methods.player.getMine().isMoving()
			&& methods.calculate.distanceTo(nextTile) > randomdis;) {
				Thread.sleep(maxSleepTime);
			}
		} while (methods.calculate.distanceTo(lastTile) > randomdis);
		return methods.calculate.distanceTo(lastTile) < randomdis;
	}

	public boolean walkToEnd() throws InterruptedException {
		final RSTile[] randomizedPath = randomizePath(1);
		return walkPath(randomizedPath);
	}

	public boolean walkToStart() throws InterruptedException {
		final RSTile[] randomizedPath = randomizePath(reversePath(), 1);
		return walkPath(randomizedPath);
	}
}
