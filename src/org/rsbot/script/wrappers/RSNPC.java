package org.rsbot.script.wrappers;

import java.awt.Point;

import org.rsbot.script.Calculations;

public class RSNPC extends RSCharacter {

	private final org.rsbot.client.RSNPC npc;

	/**
	 * @version 1.1 - 04/25/2011 Cleaned it up. - Henry
	 */
	public RSNPC(final org.rsbot.client.RSNPC npc) {
		super(npc);
		c = npc;
		this.npc = npc;
	}

	/**
	 * Used to get actions available to be done on <b>RSNPC</b>.
	 * 
	 * @return <b>String</b> array that contains actions available for this
	 *         <b>RSNPC</b>.
	 */
	public String[] getActions() {
		final org.rsbot.client.RSNPCDef def = getDefInternal();
		if (def != null) {
			return def.getActions();
		}
		return new String[0];
	}

	org.rsbot.client.RSNPCDef getDefInternal() {
		if (npc == null) {
			return null;
		} else {
			return npc.getRSNPCDef();
		}
	}

	/**
	 * Used to get the <b>RSNPC</b> ID.
	 * 
	 * @return <b>Integer</b> that contains the ID of <b>RSNPC</b>.
	 */
	public int getID() {
		final org.rsbot.client.RSNPCDef def = getDefInternal();
		if (def == null) {
			return -1;
		} else {
			return def.getType();
		}
	}

	/**
	 * Used to get <b>RSNPC</b> combat level.
	 * 
	 * @return <b>Integer</b> that contains the current level of <b>RSNPC</b>.
	 */
	@Override
	public int getLevel() {
		if (npc == null) {
			return -1;
		} else {
			return npc.getLevel();
		}
	}


	/**
	 * Determines whether the RSNPC is dead or dying
	 * 
	 * @return <tt>true</tt> if the <b>RSNPC</b> is dead/dying; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isDead() {
		return !isValid()
		|| (getHPPercent() == 0 && getAnimation() != -1 && getInteracting() == null);
	}

	/**
	 * @return <tt>true</tt> if RSNPC is interacting with RSPlayer; otherwise
	 *         <tt>false</tt>.
	 */
	@Override
	public boolean isInteractingWithLocalPlayer() {
		final RSNPC npcs = methods.npc.getNearestByID(getID());
		return npcs.getInteracting() != null
		&& npcs.getInteracting().equals(methods.player.getMine());
	}
	
	/**
	 * Moves the mouse over this NPC.  Useful for obtaining the menu 
	 * actions.
	 * 
	 * @return <tt>true</tt> if the mouse was moved.
	 */
	public void hover() {
		final RSModel model = getModel();
		if (model != null) {
			model.hover();
		} else {
			final Point p = Calculations.tileToScreen(getLocation());
			if (methods.calculate.pointOnScreen(p)) {
				methods.mouse.move(p);
			}
		}
	}

	/**
	 * Used to get <b>RSNPC</b> information in a <b>String</b>
	 * 
	 * @return <b>String</b> that contains name and actions of <b>RSNPC</b>.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final String act : getActions()) {
			sb.append(act);
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return "NPC[" + getName() + "], ID=[" + getID() + "], actions=["
		+ sb.toString() + "]" + super.toString();
	}
}
