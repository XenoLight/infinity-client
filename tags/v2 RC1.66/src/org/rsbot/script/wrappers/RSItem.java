package org.rsbot.script.wrappers;

import org.rsbot.bot.Bot;
import org.rsbot.client.HardReference;
import org.rsbot.client.SoftReference;
import org.rsbot.script.Calculations;

/**
 * Inventory/Bank/Shop item.
 * 
 * @verison 1.1 04/25/2011 - Henry Code clean up.
 * */
public class RSItem {

	int id;
	int stack;
	private RSInterfaceChild component;

	public RSItem(final int id, final int stack) {
		this.id = id;
		this.stack = stack;
	}

	public RSItem(final org.rsbot.client.RSItem item) {
		id = item.getID();
		stack = item.getStackSize();
	}

	public RSItem(final RSInterfaceChild item) {
		id = item.getChildID();
		stack = item.getChildStackSize();
		component = item;
	}

	/**
	 * Performs the given action on the component wrapped by this RSItem if
	 * possible.
	 * 
	 * @param action
	 *            The action to perform.
	 * @return <tt>true</tt> if the component was clicked successfully;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean action(final String action) {
		return action(action, null);
	}

	/**
	 * Performs the given action on the component wrapped by this RSItem if
	 * possible.
	 * 
	 * @param action
	 *            The action to perform.
	 * @param option
	 *            The option of the action to perform.
	 * @return <tt>true</tt> if the component was clicked successfully;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean action(final String action, final String option) {
		return component != null && component.action(action, option);
	}

	/**
	 * Clicks item.
	 * 
	 * @return <tt>True</tt> if component exists and is clicked otherwise
	 *         <tt>False</tt>
	 */
	public boolean click(final boolean left) {
		return component != null && component.click(left);
	}

	/**
	 * Gets the component wrapped by this RSItem.
	 * 
	 * @return The wrapped component or <code>null</code>.
	 */
	public RSInterfaceChild getChild() {
		return component;
	}

	/**
	 * Gets the component wrapped by this RSItem.
	 * 
	 * @return The wrapped component or <code>null</code>.
	 */
	public RSInterfaceChild getComponent() {
		return component;
	}

	/**
	 * Gets this item's definition if available.
	 * 
	 * @return The <b>RSItemDef</b> or <code>null</code> if unavailable.
	 */
	public RSItemDef getDefinition() {
		try {
			final org.rsbot.client.Node ref = Calculations.findNodeByID(Bot
					.getClient().getRSItemDefLoader(), id);

			if (ref != null) {
				if (ref instanceof HardReference) {
					return new RSItemDef(
							(org.rsbot.client.RSItemDef) (((HardReference) ref)
									.get()));
				} else if (ref instanceof SoftReference) {
					final Object def = ((SoftReference) ref).getReference().get();

					if (def != null) {
						return new RSItemDef((org.rsbot.client.RSItemDef) def);
					}
				}
			}
			return null;
		} catch (final ClassCastException e) {
			return null;
		}
	}

	/**
	 * Gets this item's ID.
	 * 
	 * @return <b>Integer</b> ID of the itme.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gets the name of this item using the wrapped component's name if
	 * available, otherwise the definition if available.
	 * 
	 * @return The item's name or <code>null</code> if not found.
	 */
	public String getName() {
		if (component != null) {
			return component.getChildName().replaceAll("\\<.*?>", "");
		} else {
			final RSItemDef definition = getDefinition();
			if (definition != null) {
				return definition.getName().replaceAll("\\<.*?>", "");
			}
		}
		return null;
	}

	/**
	 * Gets this item's stack size.
	 * 
	 * @return <b>Integer</b> stack size of the itme.
	 */
	public int getStackSize() {
		return stack;
	}

	/**
	 * Determines if this item contains the desired action
	 * 
	 * @param action
	 *            The item menu action to check.
	 * @return <tt>true</tt> if the item has the action; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean hasAction(final String action) {
		final RSItemDef itemDef = getDefinition();
		if (itemDef != null) {
			for (final String a : itemDef.getActions()) {
				if (a != null && a.equalsIgnoreCase(action)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if definition is avaliable.
	 * 
	 * @return <tt>True</tt> if is otherwise <t>False</tt>
	 */
	public boolean hasDefinition() {
		return getDefinition() != null;
	}

	/**
	 * Checks whether or not a valid component is being wrapped.
	 * 
	 * @return <tt>true</tt> if there is a visible wrapped component.
	 */
	public boolean isComponentValid() {
		return component != null && component.isValid();
	}
}
