package org.rsbot.script.wrappers;

/**
 * @version 1.1 04/25/2011 - Henry. Code clean up.
 */

public class RSItemTile extends RSTile {

	RSItem groundItem;

	public RSItemTile(final int x, final int y, final RSItem groundItem) {
		super(x, y);
		this.groundItem = groundItem;
	}

	/**
	 * Gets <b>RSItem</b> from <b>RSTile</b>.
	 * 
	 * @return <b>RSItem</b> that is on tile.
	 */
	public RSItem getItem() {
		return groundItem;
	}

}
