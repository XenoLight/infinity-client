package org.rsbot.script.wrappers;

import org.rsbot.client.Model;
import org.rsbot.client.RSCharacter;
import org.rsbot.script.Methods;

class RSCharacterModel extends RSModel {

	protected RSCharacter c;

	private final int[] x_base, z_base;
	public static final int[] SIN_TABLE = new int[16384];
	public static final int[] COS_TABLE = new int[16384];

	RSCharacterModel(final Methods ctx, final Model model, final RSCharacter c) {
		super(ctx, model);
		tables();
		this.c = c;
		x_base = xPoints;
		z_base = zPoints;
		xPoints = new int[xPoints.length];
		zPoints = new int[zPoints.length];
	}

	@Override
	protected int getLocalX() {
		return c.getX();
	}

	@Override
	protected int getLocalY() {
		return c.getY();
	}

	@Override
	protected void refresh() {
	}

	private void tables() {
		final double d = 0.00038349519697141029D;
		for (int i = 0; i < 16384; i++) {
			SIN_TABLE[i] = (int) (32768D * Math.sin(i * d));
			COS_TABLE[i] = (int) (32768D * Math.cos(i * d));
		}
	}

	protected void update() {
		final int theta = c.getOrientation() & 0x3fff;
		final int sin = SIN_TABLE[theta];
		final int cos = COS_TABLE[theta];
		for (int i = 0; i < x_base.length; ++i) {
			xPoints[i] = x_base[i] * cos + z_base[i] * sin >> 15;
		zPoints[i] = z_base[i] * cos - x_base[i] * sin >> 15;
		}
	}

}
