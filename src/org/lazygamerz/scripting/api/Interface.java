/*
 * 2011 Runedev development team
 * http://lazygamerz.org
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 3 of the Licence, or (at your opinion) any
 * later version.
 *
 * This library is distributed in the hope that it will be usefull, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of merchantability or fitness for a
 * particular purpose. See the GNU Lesser General Public Licence for more details.
 *
 * You should have received a copy of the GNU Lesser General Public Licence along
 * with this library; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, Ma 02111-1307 USA.
 *
 * http://www.gnu.org/licenses/lgpl.html (English)
 * http://gugs.sindominio.net/gnu-gpl/lgpl-es.html (Espa�ol)
 *
 */
package org.lazygamerz.scripting.api;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.util.color.ColorUtil;

/**
 * Provides access to game interfaces.
 * 
 * @author Runedev development team - version 1.0
 */
public class Interface {
	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	private final Methods methods;

	/*
	 * A cache of all the interfaces. Only as big as the maximum size of the
	 * client's cache.
	 */
	private RSInterface[] mainCache = new RSInterface[0];

	/* If it doesn't fit in the above cache. */
	private final Map<Integer, RSInterface> sparseMap = new HashMap<Integer, RSInterface>();
	public Interface() {
		methods = Bot.methods;
	}

	/**
	 * Determines if a "can continue" interface is open.
	 * 
	 * @return <tt>true</tt> if a continue component is open; 
	 *         <tt>false</tt> otherwise.
	 */
	public boolean canContinue() {
		return getContinueChild() != null;
	}

	/**
	 * Attempts to click all the specified components.
	 * 
	 * @param leftclick
	 *            <tt>true</tt> to left-click; <tt>false</tt> to right click.
	 * @param ids
	 *            All the components to be clicked.
	 * @return <tt>true</tt> if all components were clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean click(final boolean leftclick, final RSInterfaceChild... ids) {
		boolean did = true;
		for (final RSInterfaceChild com : ids) {
			if (!com.isValid()) {
				return false;
			}
			if (com.getPoint() == null || com.getPoint().x == -1
					|| com.getPoint().y == -1) {
				return false;
			}
			if (!clickChild(com, leftclick)) {
				did = false;
			}
		}
		return did;
	}

	/**
	 * Clicks the dialogue option that contains the desired string.
	 * 
	 * @param inter
	 *            The interface of the dialogue menu.
	 * @param option
	 *            The text we want to click.
	 * @return <tt>true</tt> if the option was clicked; otherwise <tt>false</tt>
	 *         .
	 */
	public boolean click(final RSInterface inter, String option) {
		/**
		 * This is superfluous but it just makes life a little easier so you
		 * don't have to look up the component. Just grab the interface and the
		 * text you want to click.
		 */
		if (inter.isValid()) {
			option = option.toLowerCase();
			for (final RSInterfaceChild c : inter.getChildren()) {
				if (c.getText().toLowerCase().contains(option)) {
					return c.click();
				}
			}
		}
		return false;
	}

	/**
	 * Left-clicks the child interface with the given parent ID and child ID if
	 * it is showing (valid).
	 * 
	 * @param iface
	 *            The parent interface ID.
	 * @param child
	 *            The child interface ID.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 * @see #atInterface(RSInterfaceChild, String)
	 * @see #atInterface(int, int, String)
	 */
	public boolean clickChild(final int face, final int child) {
		return clickChild(getChild(face, child));
	}

	/**
	 * Performs the provided action on the child interface with the given parent
	 * ID and child ID if it is showing (valid).
	 * 
	 * @param iface
	 *            The parent interface ID.
	 * @param child
	 *            The child interface ID.
	 * @param act
	 *            The menu action to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise false.
	 * @see #atInterface(RSInterfaceChild, String)
	 * @see #atInterface(int, int)
	 */
	public boolean clickChild(final int face, final int child, final String act) {
		return clickChild(getChild(face, child), act);
	}

	/**
	 * Left-clicks the provided RSInterfaceChild if it is showing (valid).
	 * 
	 * @param child
	 *            The child interface to click.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>.
	 * @see #atInterface(RSInterfaceChild, String)
	 */
	public boolean clickChild(final RSInterfaceChild child) {
		return clickChild(child, true);
	}

	/**
	 * Clicks the provided RSInterfaceChild if it is showing (valid) with the
	 * specified mouse button.
	 * 
	 * @param com
	 *            The child interface to click.
	 *
	 * @param leftClick
	 *      	  <tt>true</tt> for left click; <tt>false</tt> otherwise.
 	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>.
	 * @see #atInterface(RSInterfaceChild, String)
	 */

	public boolean clickChild(final RSInterfaceChild com, final boolean leftClick) {
		if (com == null || com.getPoint() == null || com.getPoint().x == -1
				|| com.getPoint().y == -1) {
			return false;
		}

		methods.mouse.click(new Point(com.getPoint().x + methods.random(-5, 6),
				com.getPoint().y + methods.random(-5, 6)), leftClick);
		return true;
	}

	/**
	 * Clicks an RSComponent.
	 * 
	 * @param child
	 *            The child containing the component.
	 * @paramid The component to be clicked.
	 * @param leftclick
	 *            true to left-click, false to right-click.
	 * @return true if it successfully clicked the component.
	 */
	public boolean clickChild(final RSInterfaceChild child, final int id, final boolean leftclick) {
		if (!child.isValid()) {
			return false;
		}
		
		final RSInterfaceChild[] coms = child.getChildren();
		if (coms.length == 0 || coms.length < id - 1) {
			return false;
		}
		
		final RSInterfaceChild com = coms[id];
		if (com == null || com.getPoint() == null || com.getPoint().x == -1
				|| com.getPoint().y == -1) {
			return false;
		}
		
		methods.mouse.click(new Point(com.getPoint().x + methods.random(-5, 6),
				com.getPoint().y + methods.random(-5, 6)), leftclick);
		
		return true;
	}

	/**
	 * Clicks the component and then the action in the menu.
	 * 
	 * @param ChildInterface
	 *            The child containing the component.
	 * @param ComponentID
	 *            The component to be clicked.
	 * @param act
	 *            The menu action to be done. Left clicks if null.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>
	 *         .
	 */
	public boolean clickChild(final RSInterfaceChild child, final int id, final String act) {
		if (act==null)  {
			return clickChild(child, true);
		}
		else {
			return clickChild(child, id, false) && act!=null && methods.menu.action(act);
		}
	}

	/**
	 * Performs the given action on the provided RSInterfaceChild if it is
	 * showing (valid).
	 * 
	 * @param child
	 *            The child interface to click.
	 * @param act
	 *            The menu action to click.  If null. left clicks.
	 * @return <tt>true</tt> if the action was clicked; otherwise <tt>false</tt>.
	 * @see #atInterface(RSInterfaceChild)
	 * @see #atInterface(int, int, String)
	 */
	public boolean clickChild(final RSInterfaceChild c, final String act) {
		if (!c.isValid()) {
			return false;
		}
		
		if (act==null)  {
			return clickChild(c, true);
		}
		
		final Rectangle rect = c.getArea();
		if (rect.x == -1) {
			return false;
		}
		
		/* 1 pixel is not enough for all components */
		final int minX = rect.x + 2, minY = rect.y + 2, width = rect.width - 4, height = rect.height - 4;
		final Rectangle actual = new Rectangle(minX, minY, width, height);

		/*
		 * Check if the menu already contains the action otherwise reposition
		 * before clicking
		 */
		if (actual.contains(methods.mouse.getLocation()) &&
				act!=null && methods.menu.action(act)) {
			return true;
		}
		
		methods.mouse.move(methods.random(minX, minX + width),
				methods.random(minY, minY + height));
		methods.wait(methods.random(80,200));
		
		return methods.menu.action(act);
	}

	/**
	 * Clicks the interface to continue to the next one.
	 * 
	 * @return <tt>true</tt> if continue component was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean clickContinue() {
		final RSInterfaceChild cont = getContinueChild();
		return cont != null && cont.isValid() && cont.click(true);
	}

	/**
	 * Enlarges the cache if there are more interfaces than the cache size.
	 */
	private synchronized void enlargeCache() {
		final org.rsbot.client.RSInterface[][] inters = methods.game.client()
		.getRSInterfaceCache();
		if (inters != null && mainCache.length < inters.length) {
			/* enlarge cache */
			mainCache = Arrays.copyOf(mainCache, inters.length);
			for (int i = mainCache.length; i < mainCache.length; i++) {
				final RSInterface tmp = sparseMap.get(i);
				if (tmp != null) {
					sparseMap.remove(i);
					mainCache[i] = tmp;
				}
			}
		}
	}

	/**
	 * Gets the <tt>RSInterface<tt> having the specified index.
	 * @param index
	 *            The index of the interface.
	 * @return The <tt>RSInterface</tt> for the given index.
	 */
	public synchronized RSInterface get(final int index) {
		RSInterface inter;
		final int cacheLen = mainCache.length;
		if (index < cacheLen) {
			inter = mainCache[index];
			if (inter == null) {
				inter = new RSInterface(index);
				mainCache[index] = inter;
			}
		} else {
			inter = sparseMap.get(index);
			if (inter == null) {
				enlargeCache();
				if (index < cacheLen) {
					inter = mainCache[index];
					if (inter == null) {
						inter = new RSInterface(index);
						mainCache[index] = inter;
					}
				} else {
					inter = new RSInterface(index);
					sparseMap.put(index, inter);
				}
			}
		}
		return inter;
	}

	/**
	 * Gets all the valid interfaces.
	 * 
	 * @return <code>RSInterface</code> array containing all valid interfaces.
	 */
	public synchronized RSInterface[] getAll() {
		enlargeCache();
		final org.rsbot.client.RSInterface[][] inters = methods.game.client()
		.getRSInterfaceCache();
		if (inters == null) {
			return new RSInterface[0];
		}
		
		final List<RSInterface> out = new ArrayList<RSInterface>();
		for (int i = 0; i < inters.length; i++) {
			if (inters[i] != null) {
				final RSInterface in = get(i);
		
				if (in.isValid()) {
					out.add(in);
				}
			}
		}
		
		return out.toArray(new RSInterface[out.size()]);
	}

	/**
	 * Gets all valid interfaces containing the specified text.
	 * @param text
	 *            The text to search each interface for.
	 * @return <tt>RSInterface</tt> array of the interfaces containing specified
	 *         text.
	 */
	public RSInterface[] getAllContaining(final String text) {
		final List<RSInterface> results = new LinkedList<RSInterface>();
		for (final RSInterface iface : getAll()) {
			if (iface.getText().toLowerCase().contains(text.toLowerCase())) {
				results.add(iface);
			}
		}
		
		return results.toArray(new RSInterface[results.size()]);
	}

	
	/**
	 * Gets the rectangle defined by the specified interface child.
	 * @param com
	 *			The <tt>RSInterfaceChild</tt> whose area is to be returned.
	 * @return <tt>Rectangle</tt> for the area of the interface child.
	 */
	public Rectangle getBoxArea(final RSInterfaceChild com) {
		final Rectangle boxArea = new Rectangle(com.getAbsoluteX(),
				com.getAbsoluteY(), com.getWidth(), com.getHeight());
		return boxArea;
	}

	/**
	 * Gets the interface childe for the specified packed interface id.
	 * @param id
	 *            The packed interface index ((x << 16) | (y & 0xFFFF)).
	 * @return <tt>RSInterfaceChild</tt> for the given interface id.
	 */
	public RSInterfaceChild getChild(final int id) {
		final int x = id >> 16;
		final int y = id & 0xFFFF;
		return get(x).getChild(y);
	}

	/**
	 * Gets the specified interface child for the interface with the specified index.
	 * @param index
	 *            The parent interface index
	 * @param childIndex
	 *            The child index
	 * @return <tt>RSInterfaceChild</tt> for the given parent index and child index.
	 */
	public RSInterfaceChild getChild(final int index, final int childIndex) {
		return get(index).getChild(childIndex);
	}

	/**
	 * Gets the currently valid interface that contains the text:
	 * "Click here to continue".
	 * 
	 * @return <tt>RSInterface</tt> containing the continue text.
	 */
	public RSInterface getContinue() {
		if (methods.game.client().getRSInterfaceCache() == null) {
			return null;
		}
		final RSInterface[] valid = getAll();
		for (final RSInterface face : valid) {
			if (face.containsText("Click here to continue")) {
				return face;
			}
		}
		return null;
	}

	/**
	 * Gets the interface child containing the continue text:
	 * "Click here to continue".
	 * 
	 * @return <tt>RSInterfaceChild</tt> containing "Click here to continue";
	 *         otherwise null.
	 */
	public RSInterfaceChild getContinueChild() {
		if (methods.game.client().getRSInterfaceCache() == null) {
			return null;
		}
		final RSInterface[] valid = getAll();
		for (final RSInterface iface : valid) {
			if (iface.getIndex() != 137) {
				final int len = iface.getChildCount();
				for (int i = 0; i < len; i++) {
					final RSInterfaceChild child = iface.getChild(i);
					if (child.containsText("Click here to continue")
							&& child.isValid() && child.getAbsoluteX() > 10
							&& child.getAbsoluteY() > 300) {
						return child;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get the maximum interface cache size.
	 * 
	 * @return The maximum known interface cache size.
	 */
	@SuppressWarnings("unused")
	private synchronized int getMaxCacheSize() {
		enlargeCache();
		return mainCache.length;
	}


	/**
	 * Determines if a text input interface is open.
	 * 
	 * @return <tt>true</tt> if a text input interface is open; <tt>false</tt> otherwise.
	 */
	public boolean isTextInputOpen() {
		if (!getChild(137, 55).containsText(methods.player.getMine().getName())) {
			return false;
		}

		final RSInterfaceChild put = getChild(137, 54);
		if (put.isValid()) {
			final Rectangle area = put.getArea();
			area.width += 100;
			final Point[] points = ColorUtil.findColorInArea(area, new Color(219, 219,
					219), new Color(10, 10, 10));
		
			if (points != null && points.length > 5) {
				return false;
			} else {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Scrolls to the component using the specified scroll bar ID.
	 * 
	 * @param component
	 *            component to scroll to
	 * @param scrollBarID
	 *            scrollbar to scroll with
	 * @return true when scrolled successfully
	 */
	public boolean scrollTo(final RSInterfaceChild component,
			final int scrollBarID) {
		final RSInterfaceChild scrollBar = getChild(scrollBarID);

		return scrollTo(component, scrollBar);
	}

	/**
	 * Scrolls to the component using the specified scroll bar interface.
	 * 
	 * @param component
	 *            component to scroll to
	 * @param scrollBar
	 *            scrollbar to scroll with
	 * @return true when scrolled successfully
	 */
	public boolean scrollTo(final RSInterfaceChild component,
			final RSInterfaceChild scrollBar) {
		/* Check arguments */
		if (component == null || scrollBar == null || !component.isValid()) {
			return false;
		}

		if (scrollBar.getChildren().length != 6) {
			/* no scrollbar, so probably not scrollable */
			return true;
		}

		/* Find scrollable area */
		RSInterfaceChild scrollableArea = component;
		while (scrollableArea.getScrollableContentHeight() == 0
				&& scrollableArea.getParentID() != -1) {
			scrollableArea = getChild(scrollableArea.getParentID());
		}

		/* Check scrollable area */
		if (scrollableArea.getScrollableContentHeight() == 0) {
			return false;
		}

		/* Get scrollable area height */
		final int areaY = scrollableArea.getAbsoluteY();
		final int areaHeight = scrollableArea.getRealHeight();

		Bot.debug(logger, String.format("item absoluteX=%d, absoluteY=%d, relativeX=%d, relativeY=%d", 
				component.getAbsoluteX(),component.getAbsoluteY(),
				component.getRelativeX(),component.getRelativeY()));
		
		/* Check if the component is already visible */
		if (component.getAbsoluteY() >= areaY
				&& component.getAbsoluteY() <= areaY + areaHeight
				- component.getRealHeight()) {
			return true;
		}

		/* Calculate scroll bar position to click */
		final RSInterfaceChild scrollBarArea = scrollBar.getChild(0);
		final int contentHeight = scrollableArea.getScrollableContentHeight();

		int pos = (int) ((float) scrollBarArea.getRealHeight() / contentHeight * (component
				.getRelativeY() + methods.random(-areaHeight / 2, areaHeight
						/ 2 - component.getRealHeight())));
		/* inner */
		if (pos < 0) {
			pos = 0;
		} else if (pos >= scrollBarArea.getRealHeight()) {
			pos = scrollBarArea.getRealHeight() - 1; // outer
		}

		/* Click on the scrollbar */
		methods.mouse.click(
				scrollBarArea.getAbsoluteX()
				+ methods.random(0, scrollBarArea.getRealWidth()),
				scrollBarArea.getAbsoluteY() + pos, true);

		/* Wait a bit */
		methods.sleep(methods.random(200, 400));

		/* Scroll to it if we missed it */
		Bot.debug(logger, String.format("item absoluteX=%d, absoluteY=%d, relativeX=%d, relativeY=%d", 
				component.getAbsoluteX(),component.getAbsoluteY(),
				component.getRelativeX(),component.getRelativeY()));

		while (component.getAbsoluteY() < areaY
				|| component.getAbsoluteY() > areaY + areaHeight
				- component.getRealHeight()) {
			final boolean scrollUp = component.getAbsoluteY() < areaY;
			scrollBar.getChild(scrollUp ? 4 : 5).action("");

			methods.sleep(methods.random(100, 200));
		}

		/* Return whether or not the component is visible now. */
		return component.getAbsoluteY() >= areaY
		&& component.getAbsoluteY() <= areaY + areaHeight
		- component.getRealHeight();
	}

	/**
	 * Waits for an interface to be closed/opened.
	 * 
	 * @param face
	 *            The interface to wait for.
	 * @param timer
	 *            Milliseconds to wait for the interface to open/close.
	 * @param valid
	 *            True if open, false if close.
	 * @return <tt>true</tt> if the interface was successfully closed/opened.
	 */
	public boolean waitFor(final RSInterface face, final int timer,	final boolean valid) {
		if (face==null)  {
			return false;
		}
		
		// Changed this to a while loop in order to add debug capability
		int w=0;
		while (w++ < timer/100 && face.isValid() != valid) {
			methods.sleep(100);
		}
		
		Bot.debug(logger, "Time waited for interface "+face.getIndex()+" was "+ (w*100) + "ms");
		return face.isValid() == valid;
	}

	/**
	 * Waits for an interface to be closed/opened.
	 * 
	 * @param child
	 *            The interface child to wait for.
	 * @param timer
	 *            Milliseconds to wait for the interface to open/close.
	 * @param valid
	 *            True if open, false if close.
	 * @return <tt>true</tt> if the interface was successfully closed/opened.
	 */
	public boolean waitForChild(final RSInterfaceChild child, final int timer, final boolean valid) {
		if (child==null)  {
			return false;
		}
		
		// Changed this to a while loop in order to add debug capability
		int w=0;
		while (w++ < timer/100 && child.isValid() != valid) {
			methods.sleep(100);
		}
		
		Bot.debug(logger, "Time waited for interface child "+child.getParInterface().getIndex()+","+ child.getIndex()+" was "+ (w*100) + "ms");
		return child.isValid() == valid;
	}

	/**
	 * waits for the interface child to be not valid or closed on the screen
	 * 
	 * @param child
	 *            interface child to wait for
	 * @param ms
	 *            the amount of time to wait
	 * @return <tt>true</tt> if closed
	 */
	public boolean waitForChildClose(final RSInterfaceChild child, final int ms) {
		return waitForChild(child, ms, false);
	}

	/**
	 * waits for the interface child to be valid on the screen
	 * 
	 * @param child
	 *            interface child to wait for
	 * @param ms
	 *            the amount of time to wait
	 * @return <tt>true</tt> if opened, <tt>false</tt> otherwise
	 */
	public boolean waitForChildOpen(final RSInterfaceChild child, final int ms) {
		return waitForChild(child, ms, true);
	}

	/**
	 * Waits for the the interface to be close
	 * 
	 * @param face
	 *            the interface to wait for
	 * @param ms
	 *            the amount of time to wait
	 * @return <tt>true</tt> if the interface closed, <tt>false</tt> otherwise
	 */
	public boolean waitForClose(final RSInterface face, final int ms) {
		return waitFor(face, ms, false);
	}

	/**
	 * Waits for the the interface to be open
	 * 
	 * @param face
	 *            the interface to wait for
	 * @param ms
	 *            the amount of time to wait
	 * @return <tt>true</tt> if opened, <tt>false</tt> otherwise.
	 */
	public boolean waitForOpen(final RSInterface face, final int ms) {
		return waitFor(face, ms, true);
	}
}
