package org.rsbot.script.wrappers;

import java.awt.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.client.Client;

/**
 * This is the class that handles an Interface. Notice it handles a whole
 * interface, through this you can access his children.
 * 
 * @author Qauters
 */
public class RSInterface implements Iterable<RSInterfaceChild> {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Bot.class.getPackage()
			.getName());

	/**
	 * Cache of this interface children.
	 */
	private RSInterfaceChild[] childCache = new RSInterfaceChild[0];
	private final Object childLock = new Object();
	/**
	 * The index of this interface.
	 */
	private final int index;

	/**
	 * The init method. Only statics should use this.
	 * 
	 * @param iface
	 *            The id of the interface, e.g. 149.
	 * @param b
	 *            Here until we can drop the public method.
	 */
	public RSInterface(final int iface) {
		if (iface < 0) {
			throw new IndexOutOfBoundsException(iface + " < 0");
		}
		index = iface;
	}

	/*
	 * Searches all it's actions, to find your phrase
	 * 
	 * @param phrase Text to search for
	 * 
	 * @return true if found
	 */
	public boolean containsAction(final String phrase) {
		for (final RSInterfaceChild child : getChildren()) {
			if (child == null) {
				continue;
			}
			if (child.getActions() == null) {
				return false;
			}
			for (final String action : child.getActions()) {
				if (action == null) {
					continue;
				}
				if (action.toLowerCase().contains(phrase.toLowerCase())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Searches all it's text, to find your phrase
	 * 
	 * @param phrase
	 *            Text to search for
	 * @return true if found, false if null
	 */
	public boolean containsText(final String phrase) {
		return getText().contains(phrase);
	}

	/**
	 * @inheritDoc java/lang/Object#equals(java/lang/Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RSInterface) {
			final RSInterface inter = (RSInterface) obj;
			return inter.index == index;
		}
		return false;
	}

	/**
	 * Returns the center point of this interface
	 * 
	 * @return The center point of this interface
	 */
	public Point getCenter(final RSInterfaceChild Child) {
		return new Point(Child.getAbsoluteX() + Child.getWidth() / 2,
				Child.getAbsoluteY() + Child.getHeight() / 2);
	}

	/**
	 * Gets the child component at the given index.
	 * 
	 * @param id
	 *            The index of the child.
	 * @return The child component.
	 */
	public RSInterfaceChild getChild(final int id) { // TODO sparseMap
		synchronized (childCache) {
			final org.rsbot.client.RSInterface[] children = getChildrenInternal();
			final int ensureLen = Math.max(children != null ? children.length
					: 0, id + 1);
			if (childCache.length < ensureLen) { // extend if necessary
				final int prevLen = childCache.length;
				childCache = Arrays.copyOf(childCache, ensureLen);
				for (int i = prevLen; i < ensureLen; i++) {
					childCache[i] = new RSInterfaceChild(this, i);
				}
			}
			return childCache[id];
		}
	}

	/**
	 * Gets the amount of child components.
	 * 
	 * @return the amount of children, or 0 if null
	 */
	public int getChildCount() {
		final org.rsbot.client.RSInterface[] children = getChildrenInternal();
		if (children != null) {
			return children.length;
		}
		return 0;
	}

	/**
	 * Gets all child components of this interface.
	 * 
	 * @return the component array
	 */
	public RSInterfaceChild[] getChildren() {
		synchronized (childLock) {
			final org.rsbot.client.RSInterface[] children = getChildrenInternal();
			if (children == null) {
				return childCache.clone(); // return as is
			} else {
				if (childCache.length < children.length) { // extend if
					// necessary
					final int prevLen = childCache.length;
					childCache = Arrays.copyOf(childCache, children.length);
					for (int i = prevLen; i < childCache.length; i++) {
						childCache[i] = new RSInterfaceChild(this, i);
					}
				}
				return childCache.clone();
			}
		}
	}

	/**
	 * Safely gets the array of children.
	 * 
	 * @return The child interfaces of the client.
	 * */
	org.rsbot.client.RSInterface[] getChildrenInternal() {
		final Client c = Bot.getClient();
		if (c == null) {
			return null;
		}
		final org.rsbot.client.RSInterface[][] inters = c.getRSInterfaceCache();
		if ((inters != null) && (index < inters.length)) {
			return inters[index];
		}
		return null;
	}

	/**
	 * Use getChild();
	 * 
	 * @return don't use
	 */
	@Deprecated
	public RSInterfaceChild getComponent(final int id) {
		return getChild(id);
	}

	/**
	 * Use getChildren();
	 */
	@Deprecated
	public RSInterfaceChild[] getComponents() {
		return getChildren();
	}

	/**
	 * @return The index of this interface.
	 * */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the location of the interface
	 * 
	 * @return the exact location of the interface, return (-1, -1) if interface
	 *         was null
	 */
	public Point getLocation() {
		final org.rsbot.client.RSInterface[] children = getChildrenInternal();
		if (children != null) {
			for (final org.rsbot.client.RSInterface child : children) {
				if (child != null) {
					if ((child.getMasterX() != -1)
							&& (child.getMasterY() != -1)) {
						return new Point(child.getMasterX(), child.getMasterY());
					}
				}
			}
		}
		return new Point(-1, -1);
	}

	/**
	 * Finds all the text in it, searches all his children for it.
	 * 
	 * @return all the text found separated by newlines, empty if null
	 */
	public String getText() {
		final StringBuilder sb = new StringBuilder();
		final org.rsbot.client.RSInterface[] children = getChildrenInternal();
		if (children != null) {
			for (final org.rsbot.client.RSInterface child : children) {
				String string;
				if ((child != null) && ((string = child.getText()) != null)) {
					sb.append(string);
					sb.append("\r\n");
				}
			}
		}
		if (sb.length() > 2) {
			sb.setLength(sb.length() - 2);
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return index;
	}

	/**
	 * Checks whether or not the interface is valid or not
	 * 
	 * @return true if its valid
	 */
	public boolean isValid() {
		// everything is thread hot so make sure you copy pointers to it
		if (getChildrenInternal() == null) {
			return false;
		}
		final int idx = getIndex();
		final boolean[] validArray = Bot.getClient().getValidRSInterfaceArray();
		
		Point faceLoc = this.getLocation();
		
		return (!faceLoc.equals(new Point(-1, -1))) &&
				(validArray != null) && (idx < validArray.length) && validArray[idx];
	}

	/**
	 * Iterated over the children of the interface. Will never return null even
	 * if the underlying interface is null.
	 */
	@Override
	public Iterator<RSInterfaceChild> iterator() {
		return new Iterator<RSInterfaceChild>() {

			private int nextIdx = 0;

			@Override
			public boolean hasNext() {
				return !isValid() && (getChildCount() >= nextIdx);
			}

			@Override
			public RSInterfaceChild next() {
				final RSInterfaceChild child = getChild(nextIdx);
				nextIdx++;
				return child;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	void setChild(final RSInterfaceChild child) {
		synchronized (childLock) {
			/* safe that the index isn't execisve since it comes from child */
			final int idx = child.getIndex();
			if (idx >= childCache.length) {
				getChild(idx);
				childCache[idx] = child;
			}
		}
	}
}
