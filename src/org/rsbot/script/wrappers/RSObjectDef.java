package org.rsbot.script.wrappers;

/**
 * @version 1.1 04/25/2011 - Henry. Code clean up.
 */

public class RSObjectDef {

	org.rsbot.client.RSObjectDef od;

	public RSObjectDef(final org.rsbot.client.RSObjectDef od) {
		this.od = od;
	}

	/**
	 * Gets actions.
	 * 
	 * @return <b>String</b> array containing actions.
	 */
	public String[] getActions() {
		return od.getActions();
	}

	/**
	 * Gets child id's.
	 * 
	 * @return <b>Integer</b> array containing child id's.
	 */
	public int[] getChildIDs() {
		return od.getChildrenIDs();
	}

	/**
	 * @Deprecated Returns -1
	 */
	@Deprecated
	public int getID() {
		/* return od.getType(); */
		return -1;
	}

	/**
	 * Gets object name.
	 * 
	 * @return <b>String</b> containing object name.
	 */
	public String getName() {
		return od.getName();
	}

}
