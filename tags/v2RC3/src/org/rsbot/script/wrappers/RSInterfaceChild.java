package org.rsbot.script.wrappers;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.client.RSInterfaceNode;
import org.rsbot.script.Methods;
import org.rsbot.script.internal.HashTable;

/**
 * This class handles an Interface Child The class RSInterface references to
 * this one a lot
 * 
 * @author Qauters
 */
public class RSInterfaceChild {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	private final Methods methods = Bot.methods;
	/**
	 * The index of this interface in the parent.
	 * */
	private final int index;
	/**
	 * The parent interface containing this child.
	 * */
	private final RSInterface parInterface;
	/**
	 * The parent component
	 */
	private final RSInterfaceChild parent;

	/**
	 * Initializes the child.
	 * 
	 * @param parent
	 *            The parent interface.
	 * @param index
	 *            The child index of this child.
	 */
	RSInterfaceChild(final RSInterface parent, final int index) {
		parInterface = parent;
		this.index = index;
		this.parent = null;
	}

	/**
	 * Initializes the component.
	 * 
	 * @param ctx
	 *            The method context.
	 * @param parInterface
	 *            The parent interface.
	 * @param parent
	 *            The parent component.
	 * @param index
	 *            The child index of this child.
	 */
	RSInterfaceChild(final RSInterface parInterface,
			final RSInterfaceChild parent, final int index) {
		this.parInterface = parInterface;
		this.parent = parent;
		this.index = index;
	}

	/**
	 * Performs the given action on this RSInterfaceChild if it is showing
	 * (valid).
	 * 
	 * @param action
	 *            The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>
	 *         .
	 */
	public boolean action(final String action) {
		return action(action, null);
	}

	/**
	 * Performs the given action on this RSInterfaceChild if it is showing
	 * (valid).
	 * 
	 * @param action
	 *            The menu action to click.
	 * @param option
	 *            The option of the menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>
	 *         .
	 */
	public boolean action(final String action, final String option) {
		if (!isValid()) {
			return false;
		}
		
		final Rectangle rect = getArea();
		if (rect.x < 1 || rect.y < 1 || 
				rect.width < 1	|| rect.height < 1) {
				return false;
		}
		
		//TODO: Aug 10, 2011 - It appears that there are now models associated
		// with interface children, at least in the magic tab interface.  We need
		// this hooked.
		if (!rect.contains(methods.mouse.getLocation()) || !methods.menu.contains(action)) {
			final int min_x = rect.x + 1, min_y = rect.y + 1;
			final int max_x = min_x + rect.width - 2, max_y = min_y
			+ rect.height - 2;

			methods.mouse.move(methods.random(min_x, max_x, rect.width / 3),
					methods.random(min_y, max_y, rect.height / 3));
			methods.wait(methods.random(40, 80));
		}
		return methods.menu.action(action, option);
	}

	/**
	 * Left-clicks this component.
	 * 
	 * @return <tt>true</tt> if the component was clicked.
	 */
	public boolean click() {
		return click(true);
	}

	/**
	 * Clicks this component.
	 * 
	 * @param leftClick
	 *            <tt>true</tt> to left-click; <tt>false</tt> to right-click.
	 * @return <tt>true</tt> if the component was clicked.
	 */
	public boolean click(final boolean leftClick) {
		if (!isValid()) {
			return false;
		}

		final Rectangle rect = getArea();
		if (rect.x < 1 || rect.y < 1 || 
			rect.width < 1	|| rect.height < 1) {
			return false;
		}
		
		if (rect.contains(methods.mouse.getLocation())) {
			methods.mouse.click(true);
			return true;
		}

		final int min_x = rect.x + 1, min_y = rect.y + 1;
		final int max_x = min_x + rect.width - 2, max_y = min_y + rect.height
		- 2;

		methods.mouse.click(methods.random(min_x, max_x, rect.width / 3),
				methods.random(min_y, max_y, rect.height / 3), leftClick);
		return true;
	}

	/**
	 * Checks the actions of the child for a given text phrase
	 * 
	 * @param phrase
	 *            The phrase to check for
	 * @return true if found
	 */
	public boolean containsAction(final String phrase) {
		for (final String action : getActions()) {
			if (action.toLowerCase().contains(phrase.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks the text of the child for a given text phrase.
	 * No children of the child are checked.
	 * 
	 * @param phrase
	 *            The phrase to check for
	 * @return Whether the text contained the phrase or not
	 */
	public boolean containsText(final String phrase) {
		return getText().contains(phrase);
	}

	/**
	 * Checks the text of the child for a given text phrase.
	 * No children of the child are checked.
	 * 
	 * @param phrase
	 *            The phrase to check for
	 * @param searchComponents
	 *            <tt>true</tt> if all children of this child should be
	 *            included; <tt>false</tt> otherwise.
	 * @return <tt>true</tt> if the phrase was found; <tt>false</tt> otherwise.
	 */
	public boolean containsText(final String phrase, boolean searchComponents) {	
		boolean found = false;
		RSInterfaceChild[] comps = getChildren();
		
		if (!searchComponents || comps.length==0)  {
			return containsText(phrase);
		}
		
		for (RSInterfaceChild comp: comps)  {
			if (comp.containsText(phrase, true))  {
				Bot.debug(logger, "Found interface text: "+phrase);
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RSInterfaceChild) {
			final RSInterfaceChild child = (RSInterfaceChild) obj;
			return (index == child.index)
			&& child.parInterface.equals(parInterface);
		}
		return false;
	}

	/**
	 * Gets the absolute x position of the child, calculated from the beginning
	 * of the screen
	 * 
	 * @return the absolute x or -1 if null
	 */
	public int getAbsoluteX() {
		/* Get internal Interface */
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter == null) {
			return -1;
		}

		/* Define x */
		int x = 0;

		/* Find parentX */
		final int parentID = getParentID();
		if (parentID != -1) {
			x = methods.iface.getChild(parentID >> 16, parentID & 0xFFFF)
			.getAbsoluteX();
		} else /* No parentX so get the baseX, using bounds or masterX */{
			/* Get bounds array */
			final Rectangle[] bounds = Bot.getClient()
			.getRSInterfaceBoundsArray();

			/* Get bounds array index */
			final int bi = inter.getBoundsArrayIndex();
			if ((bi >= 0) && (bounds != null) && (bi < bounds.length)
					&& (bounds[bi] != null)) {
				return bounds[bi].x; // Return x here, since it already contains
			} /* our x! */else {
				x = inter.getMasterX();
			}
		}

		/* Add our x */
		x += inter.getX();

		/* Return x */
		return x;
	}

	/**
	 * Gets the absolute y position of the child, calculated from the beginning
	 * of the screen
	 * 
	 * @return the absolute y position or -1 if null
	 */
	public int getAbsoluteY() {
		/* Get internal Interface */
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter == null) {
			return -1;
		}

		/* Define y */
		int y = 0;

		/* Find parentY */
		final int parentID = getParentID();
		if (parentID != -1) {
			y = methods.iface.getChild(parentID >> 16, parentID & 0xFFFF)
			.getAbsoluteY();
		} else /* No parentY so get the baseY, using bounds or masterY */{
			/* / Get bounds array */
			final Rectangle[] bounds = Bot.getClient()
			.getRSInterfaceBoundsArray();

			/* Get bounds array index */
			final int bi = inter.getBoundsArrayIndex();
			if ((bi >= 0) && (bounds != null) && (bi < bounds.length)
					&& (bounds[bi] != null)) {
				return bounds[bi].y; /* Return y here, since it already contains */
			} /* our y! */else {
				y = inter.getMasterY();
			}
		}

		/* Add our y */
		y += inter.getY();

		/* Return y */
		return y;
	}

	/**
	 * Gets the actions of the child. The elements will never be null.
	 * 
	 * @return the actions or an empty array if null
	 */
	public String[] getActions() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getActions();
		}
		return new String[0];
	}

	/**
	 * Gets the Area of the child, calculated from it's absolute position
	 * 
	 * @return the area or new Rectangle(-1, -1, -1, -1) if null
	 */
	public Rectangle getArea() {
		return new Rectangle(getAbsoluteX(), getAbsoluteY(), getWidth(),
				getHeight());
	}
	
	/**
	 * Gets the background color of the child
	 * 
	 * @return the background color or -1 if null
	 */
	public int getBackgroundColor() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getTextureID();
		}
		return -1;
	}

	public int getBorderThickness() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getBorderThickness();
		}
		return -1;
	}

	public int getBoundsArrayIndex() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getBoundsArrayIndex();
		}

		return -1;
	}

	public Rectangle getBoxArea(final RSInterfaceChild com) {
		final Rectangle boxArea = new Rectangle(com.getAbsoluteX(),
				com.getAbsoluteY(), com.getWidth(), com.getHeight());
		return boxArea;
	}

	/**
	 * Returns the center point of this interface
	 * 
	 * @return The center point of this interface
	 */
	public Point getCenter() {
		return new Point(getAbsoluteX() + getWidth() / 2, getAbsoluteY()
				+ getHeight() / 2);
	}

	/**
	 * Gets the child component at a given index
	 * 
	 * @param idx
	 *            The child index
	 * @return The child component, or null
	 */
	public RSInterfaceChild getChild(final int idx) {
		final RSInterfaceChild[] components = getChildren();
		if (idx >= 0 && idx < components.length) {
			return components[idx];
		}
		return null;
	}

	/**
	 * Return component ID of child used for "unsolvable" randoms. Written by
	 * PwnZ.
	 * 
	 * @return Component ID.
	 */
	public int getChildID() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getComponentID();
		}
		return -1;
	}

	/**
	 * Gets the index of this component
	 * 
	 * @return The index of this component, or -1 if component == null
	 */
	public int getChildIndex() {
		final org.rsbot.client.RSInterface component = getInterfaceInternal();
		if (component != null) {
			return component.getComponentIndex();
		}

		return -1;
	}

	/**
	 * Gets the name of this component
	 * 
	 * @return The name of this component, or "" if component == null
	 */
	public String getChildName() {
		final org.rsbot.client.RSInterface component = getInterfaceInternal();
		if (component != null) {
			return component.getComponentName();
		}

		return "";
	}

	/**
	 * The child components (bank items etc) of this component.
	 * 
	 * @return The components or RSInterfaceChildt[0] if null
	 */
	public RSInterfaceChild[] getChildren() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null && inter.getComponents() != null) {
			final RSInterfaceChild[] components = new RSInterfaceChild[inter
			                                                           .getComponents().length];
			for (int i = 0; i < components.length; i++) {
				components[i] = new RSInterfaceChild(parInterface, this, i);
			}
			return components;
		}
		return new RSInterfaceChild[0];
	}

	/**
	 * Gets the stack size of this component
	 * 
	 * @return The stack size of this component, or -1 if component == null
	 */
	public int getChildStackSize() {
		final org.rsbot.client.RSInterface component = getInterfaceInternal();
		if (component != null) {
			return component.getComponentStackSize();
		}

		return -1;
	}

	/**
	 * Use getChild();
	 */
	@Deprecated
	public RSInterfaceChild getComponent(final int idx) {
		return getChild(idx);
	}

	/**
	 * Use getChildID();
	 */
	@Deprecated
	public int getComponentID() {
		return getChildID();
	}

	/**
	 * Use getChildIndex();
	 */
	@Deprecated
	public int getComponentIndex() {
		return getChildIndex();
	}

	/**
	 * Use getChildName();
	 */
	@Deprecated
	public String getComponentName() {
		return getChildName();
	}

	/**
	 * use getChildren();
	 */
	@Deprecated
	public RSInterfaceChild[] getComponents() {
		return getChildren();
	}

	/**
	 * use getChildStackSize();
	 */
	@Deprecated
	public int getComponentStackSize() {
		return getChildStackSize();
	}

	/**
	 * Gets the height of the child
	 * 
	 * @return the height of the child or -1 if null
	 */
	public int getHeight() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHeight() - 4;
		}
		return -1;
	}

	/*
	 * Use getScrollableContentWidth();
	 */
	@Deprecated
	public int getHorizontalScrollBarSize() {
		return getScrollableContentWidth();

	}

	/*
	 * Use getHorizontalScrollPosition();
	 */
	@Deprecated
	public int getHorizontalScrollBarThumbPosition() {
		return getHorizontalScrollPosition();
	}

	/*
	 * Use getRealWidth();
	 */
	@Deprecated
	public int getHorizontalScrollBarThumbSize() {
		return getRealWidth();
	}

	public int getHorizontalScrollPosition() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHorizontalScrollBarThumbPosition();
		}
		return -1;
	}

	public int getID() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getID();
		}
		return -1;

	}

	/**
	 * Returns the index of this interface in the parent. If this component does
	 * not have a parent component, this represents the index in the parent
	 * interface; otherwise this represents the component index in the parent
	 * component.
	 * 
	 * @return The index of this interface.
	 * @see #getInterface()
	 * @see #getParent()
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the parent interface of this component. This component may be nested
	 * from its parent interface in parent components.
	 * 
	 * @return The parent interface.
	 */
	public RSInterface getInterface() {
		return parInterface;
	}

	/**
	 * @return The interface represented by this object.
	 */
	org.rsbot.client.RSInterface getInterfaceInternal() {
		if (parent != null) {
			final org.rsbot.client.RSInterface p = parent
			.getInterfaceInternal();
			if (p != null) {
				final org.rsbot.client.RSInterface[] components = p
				.getComponents();
				if (components != null && index >= 0
						&& index < components.length) {
					return components[index];
				}
			}
		} else {
			final org.rsbot.client.RSInterface[] children = parInterface
			.getChildrenInternal();
			if (children != null && index < children.length) {
				return children[index];
			}
		}
		return null;
	}

	/**
	 * Gets the absolute position of the child
	 * 
	 * @return the absolute position or new Point(-1, -1) if null
	 */
	public Point getLocation() {
		return new Point(getAbsoluteX(), getAbsoluteY());
	}

	/**
	 * Gets the model ID of this component
	 * 
	 * @return the model ID or -1 if null
	 */
	public int getModelID() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getModelID();
		}

		return -1;
	}

	/**
	 * Gets the model type of this component
	 * 
	 * @return the model type or -1 if null
	 */
	public int getModelType() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getModelType();
		}

		return -1;
	}

	public int getModelZoom() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getModelZoom();
		}
		return -1;

	}

	/**
	 * Gets the parent component of this component, or null if this is a
	 * top-level component.
	 * 
	 * @return The parent component, or null.
	 */
	public RSInterfaceChild getParent() {
		return parent;
	}

	/**
	 * Gets the parent id of this interface. It will first look at the internal
	 * parentID, if that's -1 then it will search the RSInterfaceNC to find it's
	 * parent.
	 * 
	 * @return the parentID or -1 if none
	 */
	public int getParentID() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter == null) {
			return -1;
		}

		if (inter.getParentID() != -1) {
			return inter.getParentID();
		}

		final int mainID = getID() >>> 16;
				final HashTable ncI = new HashTable(Bot.getClient().getRSInterfaceNC());

				for (RSInterfaceNode node = (RSInterfaceNode) ncI.getFirst(); node != null; node = (RSInterfaceNode) ncI
				.getNext()) {
					if (mainID == node.getMainID()) {
						return (int) node.getID();
					}
				}

				return -1;
	}

	/**
	 * @return The parent interface.
	 * */
	public RSInterface getParInterface() {
		return parInterface;
	}

	/**
	 * Returns the center point of the Component. Written by Fusion89k.
	 * 
	 * @return The center point of the Component
	 */
	public Point getPoint() {
		return new Point(getAbsoluteX() + getWidth() / 2, getAbsoluteY()
				+ getHeight() / 2);
	}

	/**
	 * Gets the absolute position of the child
	 * 
	 * @return the absolute position or new Point(-1, -1) if null
	 */
	public Point getPosition() {
		return new Point(getAbsoluteX(), getAbsoluteY());
	}

	public int getRealHeight() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getVerticalScrollBarThumbSize();
		}
		return -1;
	}

	public int getRealWidth() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHorizontalScrollBarThumbSize();
		}
		return -1;
	}

	/**
	 * Gets the relative x position of the child, calculated from the beginning
	 * of the interface
	 * 
	 * @return the relative x position or -1 if null
	 */
	public int getRelativeX() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getX();
		}
		return -1;
	}

	/**
	 * Gets the relative y position of the child, calculated from the beginning
	 * of the interface
	 * 
	 * @return the relative y position -1 if null
	 */
	public int getRelativeY() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getY();
		}
		return -1;
	}

	public int getScrollableContentHeight() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getVerticalScrollBarSize();
		}
		return -1;
	}

	public int getScrollableContentWidth() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHorizontalScrollBarSize();
		}
		return -1;
	}

	/**
	 * Gets the selected action name of this component
	 * 
	 * @return the selected action name or "" if null
	 */
	public String getSelectedActionName() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getSelectedActionName();
		}
		return "";
	}

	public int getShadowColor() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getShadowColor();
		}
		return -1;

	}

	public int getSpecialType() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getSpecialType();
		}

		return -1;
	}

	/**
	 * Gets the spell name of the child
	 * 
	 * @return the spell name or "" if null
	 */
	public String getSpellName() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getSpellName();
		}
		return "";
	}

	/**
	 * Gets the text of the child
	 * 
	 * @return the text or "" if null
	 */
	public String getText() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getText();
		}
		return "";
	}

	/**
	 * Gets the text color of the child
	 * 
	 * @return the text color or -1 if null
	 */
	public int getTextColor() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getTextColor();
		}
		return -1;
	}

	/**
	 * Gets the tool tip of the child
	 * 
	 * @return the tool tip or "" if null
	 */
	public String getTooltip() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getToolTip();
		}
		return "";
	}

	/**
	 * Gets the type of the child
	 * 
	 * @return the type or -1 if null
	 */
	public int getType() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getType();
		}
		return -1;
	}

	/**
	 * Gets the value index array of the child Haven't checked what it does yet
	 * 
	 * @return the value index array or new int [0][0] if null
	 */
	public int[][] getValueIndexArray() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			final int[][] vindex = childInterface.getValueIndexArray();
			if (vindex != null) { /* clone does NOT deep copy */
				final int[][] out = new int[vindex.length][0];
				for (int i = 0; i < vindex.length; i++) {
					final int[] cur = vindex[i];
					if (cur != null) {
						out[i] = cur.clone();
					}
				}
				return out;
			}
		}
		/* clone, otherwise you have a pointer */
		return new int[0][0];
	}

	/*
	 * Use getVerticalScrollPosition();
	 */
	@Deprecated
	public int getVerticalScrollBarPosition() {
		return getVerticalScrollPosition();
	}

	/*
	 * Use getScrollableContentHeight();
	 */
	@Deprecated
	public int getVerticalScrollBarSize() {
		return getScrollableContentHeight();

	}

	/*
	 * Use getRealHeight();
	 */
	@Deprecated
	public int getVerticalScrollBarThumbSize() {
		return getRealHeight();

	}

	public int getVerticalScrollPosition() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getVerticalScrollBarPosition();
		}
		return -1;
	}

	/**
	 * Gets the width of the child
	 * 
	 * @return the width of the child or -1 if null
	 */
	public int getWidth() {
		final org.rsbot.client.RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getWidth() - 4;
		}
		return -1;
	}

	/**
	 * Get the xRotation of the interface.
	 * 
	 * @return xRotation of the interface
	 */
	public int getXRotation() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getXRotation();
		}
		return -1;

	}

	public int getYRotation() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getYRotation();
		}
		return -1;

	}

	public int getZRotation() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getZRotation();
		}
		return -1;
	}

	@Override
	public int hashCode() {
		return parInterface.getIndex() * 31 + index;
	}

	/**
	 * Moves the mouse over this component (with normally distributed
	 * randomness) if it is not already.
	 * 
	 * @return <tt>true</tt> if the mouse was moved; otherwise <tt>false</tt>.
	 */
	public boolean hover() {
		if (!isValid()) {
			return false;
		}

		final Rectangle rect = getArea();
		if (rect.x < 1 || rect.y < 1 || 
				rect.width < 1	|| rect.height < 1) {
				return false;
		}
		
		if (rect.contains(methods.mouse.getLocation())) {
			return false;
		}

		final int min_x = rect.x + 1, min_y = rect.y + 1;
		final int max_x = min_x + rect.width - 2, max_y = min_y + rect.height
		- 2;

		methods.mouse.move(methods.random(min_x, max_x, rect.width / 3),
				methods.random(min_y, max_y, rect.height / 3));
		return true;
	}

	public boolean isHorizontallyFlipped() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		return (inter != null) && inter.isHorizontallyFlipped();

	}

	public boolean isInScrollableArea() {
		// Check if we have a parent
		if (getParentID() == -1) {
			return false;
		}

		// Find scrollable area
		RSInterfaceChild scrollableArea = methods.iface.getChild(getParentID());
		while (scrollableArea.getScrollableContentHeight() == 0
				&& scrollableArea.getParentID() != -1) {
			scrollableArea = methods.iface.getChild(scrollableArea
					.getParentID());
		}

		// Return if we are in a scrollable area
		return scrollableArea.getScrollableContentHeight() != 0;
	}

	/**
	 * Whether the child is an inventory interface or not
	 * 
	 * @return True if it's an inventory interface, else false
	 */
	public boolean isInventory() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		return (inter != null) && inter.isInventoryRSInterface();
	}

	/**
	 * @return Whether or not the child is valid.
	 */
	public boolean isValid() {
		return parInterface.isValid()
		&& (parInterface.getChildrenInternal() != null);
	}

	public boolean isVerticallyFlipped() {
		final org.rsbot.client.RSInterface inter = getInterfaceInternal();
		return (inter != null) && inter.isVerticallyFlipped();
	}
	
	/**
	 * Hover the mouse in the area occupied by the interface child.
	 * @param ms
	 * 		The number of milliseconds to hover.
	 */
	public void hover(int ms)  {
		hover();
		methods.wait(ms);
	}
}
