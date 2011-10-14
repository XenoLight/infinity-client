package org.rsbot.script.wrappers;

/**
 * @version 1.1 04/25/2011 - Henry. Code clean up.
 */

public class RSItemDef {

	org.rsbot.client.RSItemDef id;

	public RSItemDef(final org.rsbot.client.RSItemDef id) {
		this.id = id;
	}

	/**
	 * Gets actions.
	 * 
	 * @return <b>String</b> array containing actions.
	 */
	public String[] getActions() {
		return id.getActions();
	}

	/**
	 * Gets ground actions.
	 * 
	 * @return <b>String</b> array containing ground actions.
	 */
	public String[] getGroundActions() {
		return id.getGroundActions();
	}

	/**
	 * Gets item name.
	 * 
	 * @return <b>String</b> containing item name.
	 */
	public String getName() {
		return id.getName();
	}

	/**
	 * @Deprecated Returns -1
	 */
	@Deprecated
	public int getTeam() {
		/* return id.getTeam(); */
		return -1;
	}

	/**
	 * Checks if item is members only.
	 * 
	 * @return <tt>True</tt> if item is members, otherwise <tt>False</tt>
	 */
	public boolean isMembers() {
		return id.isMembersObject();
	}

}
