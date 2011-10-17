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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.rsbot.bot.Bot;
import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSNPC;

/**
 * Non player game characters.
 * 
 * @author Runedev Development Team - version 1.0
 */
public class NPC {

	private final Methods methods;

	public NPC() {
		this.methods = Bot.methods;
	}

	/**
	 * Clicks a humanoid character (tall and skinny) without any randomly
	 * generated mouse paths.
	 * 
	 * @param rsNPC
	 *            The RSNPC to be clicked.
	 * @param option
	 *            The option to be clicked (If available).
	 * @return <tt>true</tt> if the option was found; otherwise <tt>false</tt>.
	 * @see #leftClick(RSNPC, String, boolean)
	 */
	public boolean action(final RSNPC rsNPC, final String option) {
		return action(rsNPC, option, false);
	}

	/**
	 * Clicks a humanoid character (tall and skinny).
	 * 
	 * @param rsNPC
	 *            The RSNPC to be clicked.
	 * @param option
	 *            The option to be clicked (If available).
	 * @param mousepath
	 *            Whether or not to use {@link #moveMouseByPath(Point)} rather
	 *            than {@link #moveMouse(Point)}.
	 * @return <tt>true</tt>if the option was found; otherwise <tt>false</tt>.
	 * @see #moveMouseByPath(Point)
	 * @see #menu.action(String)
	 */
	public boolean action(final RSNPC rsNPC, final String option, final boolean path) {
		for (int i = 0; i < 20; i++) {
			if (rsNPC == null
					|| !rsNPC.isOnScreen()) {
				return false;
			}
			if (!path) {
				methods.mouse.move(new Point((int) Math.round(rsNPC
						.getScreenLocation().getX()) + methods.random(-5, 5),
						(int) Math.round(rsNPC.getScreenLocation().getY())
						+ methods.random(-5, 5)));
			} else {
				methods.mouse.move(new Point((int) Math.round(rsNPC
						.getScreenLocation().getX()) + methods.random(-5, 5),
						(int) Math.round(rsNPC.getScreenLocation().getY())
						+ methods.random(-5, 5)));
			}
			if (methods.menu.getItems()[0].toLowerCase().contains(
					option.toLowerCase())) {
				methods.mouse.click(true);
				return true;
			} else {
				final String[] menuItems = methods.menu.getItems();
				for (final String item : menuItems) {
					if (item.toLowerCase().contains(option.toLowerCase())) {
						methods.mouse.click(false);
						return methods.menu.action(option);
					}
				}
			}
		}
		return false;
	}

	/**
	 * Searches the RS game screen for the NPC by checking the menu list Clicks
	 * NPC once found
	 * 
	 * @param npc
	 *            The RSNPC you want to click.
	 * @param action
	 *            Action command to use on the NPC (e.g "Attack" or "Talk").
	 * @return true if the NPC was clicked; otherwise <tt>false</tt>.
	 */
	public boolean click(final RSNPC rsNPC, final String action) {
		return click(rsNPC, action, null);
	}

	/**
	 * Searches the RS game screen for the NPC by checking the menu list.
	 * Performs the provided action on the NPC once found.
	 * 
	 * @param npc
	 *            The RSNPC you want to click.
	 * @param action
	 *            Action command to use on the NPC (e.g "Attack" or "Talk").
	 * @param name
	 *            The name of the NPC.
	 * @return true if the NPC was clicked; otherwise false.
	 */
	public boolean click(final RSNPC rsNPC, final String action, final String name) {
		int a;
		final String fullCommand = action + " "
		+ (name == null ? rsNPC.getName() : name);
		for (a = 10; a-- >= 0;) {
			final String[] menuItems = methods.menu.getItems();
			if (menuItems.length > 1) {
				if (methods.menu.arrayContains(menuItems, fullCommand)) {
					if (menuItems[0].contains(fullCommand)) {
						methods.mouse.click(true);
						return true;
					} else {
						return methods.menu.action(fullCommand);
					}
				}
			}
			if (!rsNPC.isOnScreen()) {
				return false;
			}
			
			methods.mouse.move(rsNPC.getScreenLocation());
		}
		return false;
	}

	/**
	 * Returns an array of all loaded RSNPCs that are accepted by the provided
	 * filter
	 * 
	 * @param filter
	 *            Filters out unwanted matches.
	 * @return Array of the loaded RSNPCs.
	 */
	public RSNPC[] getAllByFilter(final Filter<RSNPC> filter) {
		final int[] indices = methods.game.client().getRSNPCIndexArray();
		final Set<RSNPC> rsNPCs = new HashSet<RSNPC>();
		for (final int index : indices) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), index);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			if (node instanceof RSNPCNode) {
				final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
				if (rsNPC != null && filter.accept(rsNPC)) {
					rsNPCs.add(rsNPC);
				}
			}
		}
		return rsNPCs.toArray(new RSNPC[rsNPCs.size()]);
	}

	/**
	 * 
	 * @param busy
	 * @return
	 */
	public RSNPC[] getArray(final boolean busy) {
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();
		final ArrayList<RSNPC> rsNPCs = new ArrayList<RSNPC>();
		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null
					|| !(node instanceof RSNPCNode)
					|| busy
					&& methods.player.getMine().getInteracting() != null
					&& methods.player.getMine().getInteracting()
					.equals(new RSNPC(((RSNPCNode) node).getRSNPC()))) {
				continue;
			}
			rsNPCs.add(new RSNPC(((RSNPCNode) node).getRSNPC()));
		}
		final RSNPC[] temp = new RSNPC[rsNPCs.size()];
		for (int i = 0; i < temp.length; i++) {
			temp[i] = rsNPCs.get(i);
		}
		return temp;
	}

	/**
	 * Returns the RSNPC that is nearest out of all of loaded RSNPCs accepted by
	 * the provided Filter.
	 * 
	 * @param filter
	 *            Filters out unwanted matches.
	 * @return RSNPC object representing the nearest RSNPC accepted by the
	 *         provided Filter; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public RSNPC getNearByFilter(final Filter<RSNPC> filter) {
		int min = 20;
		RSNPC closest = null;
		final int[] indices = methods.game.client().getRSNPCIndexArray();

		for (final int index : indices) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), index);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			if (node instanceof RSNPCNode) {
				final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
				if (rsNPC != null && filter.accept(rsNPC)) {
					final int distance = rsNPC.distanceTo();
					if (distance < min) {
						min = distance;
						closest = rsNPC;
					}
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s). Can return null.
	 * 
	 * @param ids
	 *            The ID(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs; or null if there are no matching NPCs in the
	 *         current region.
	 * @see #getNearestFreeByID(id)
	 * @see #getNearestToAttackByID(id)
	 * @see #getNearestFreeToAttackByID(id)
	 */
	public RSNPC getNearestByID(final int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final int id : ids) {
				if (id != rsNPC.getID()) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided name(s). Can return null.
	 * 
	 * @param names
	 *            The name(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided names; or null if there are no matching NPCs in the
	 *         current region.
	 * @see #getNearestByID(names)
	 * @see #getNearestFreeByID(names)
	 * @see #getNearestToAttackByID(names)
	 * @see #getNearestFreeToAttackByID(names)
	 */
	public RSNPC getNearestByName(final String... names) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final String name : names) {
				String npcName = rsNPC.getName();
				if (npcName!=null)  {
					npcName = npcName.toLowerCase();
				}

				if (name == null || !name.toLowerCase().equals(npcName)) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s), that is not currently in combat. Can return null.
	 * 
	 * @param ids
	 *            The ID(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs that is not in combat; or null if there are no
	 *         matching NPCs in the current region.
	 * @see #getNearestByID(id)
	 * @see #getNearestToAttackByID(id)
	 * @see #getNearestFreeToAttackByID(id)
	 */
	public RSNPC getNearestFreeByID(final int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final int id : ids) {
				if (id != rsNPC.getID() || rsNPC.isInCombat()) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided name(s), that is not currently in combat. Can return null.
	 * 
	 * @param names
	 *            The name(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided names that is not in combat; or null if there are no
	 *         matching NPCs in the current region.
	 * @see #getNearestByID(name)
	 * @see #getNearestFreeByID(name)
	 * @see #getNearestToAttackByID(name)
	 * @see #getNearestFreeToAttackByID(name)
	 */
	public RSNPC getNearestFreeByName(final String... names) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final String name : names) {
				String npcName = rsNPC.getName();
				if (npcName!=null)  {
					npcName = npcName.toLowerCase();
				}

				if (name == null || !name.toLowerCase().equals(npcName)
						|| rsNPC.isInCombat()) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s), that is not currently in combat and does not have 0% HP.
	 * Can return null.
	 * 
	 * @param ids
	 *            The ID(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs that is not in combat and does not have 0% HP (is
	 *         attackable); or null if there are no matching NPCs in the current
	 *         region.
	 * @see #getNearestByID(id)
	 * @see #getNearestFreeByID(id)
	 * @see #getNearestToAttackByID(id)
	 */
	public RSNPC getNearestFreeToAttackByID(final int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final int id : ids) {
				if (id != rsNPC.getID() || rsNPC.isInCombat()
						|| rsNPC.getHPPercent() == 0) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided name(s), that is not currently in combat and does not have 0%
	 * HP. Can return null.
	 * 
	 * @param names
	 *            The names(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided names that is not in combat and does not have 0% HP (is
	 *         attackable); or null if there are no matching NPCs in the current
	 *         region.
	 * @see #getNearestByID(names)
	 * @see #getNearestFreeByID(names)
	 * @see #getNearestToAttackByID(names)
	 * @see #getNearestFreeToAttackByID(names)
	 */
	public RSNPC getNearestFreeToAttackByName(final String... names) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final String name : names) {
				String npcName = rsNPC.getName();
				if (npcName!=null)  {
					npcName = npcName.toLowerCase();
				}
				if (name == null || !name.toLowerCase().equals(npcName)
						|| rsNPC.isInCombat() || rsNPC.getHPPercent() == 0) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided ID(s), that does not have 0% HP. Can return null.
	 * 
	 * @param ids
	 *            The ID(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs that does not have 0% HP (is attackable); null if
	 *         there are no matching NPCs in the current region.
	 * @see #getNearestNPCByID(id)
	 * @see #getNearestFreeNPCByID(id)
	 * @see #getNearestNPCToAttackByID(id)
	 * @see #getNearestFreeNPCToAttackByID(id)
	 */
	public RSNPC getNearestToAttackByID(final int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final int id : ids) {
				if (id != rsNPC.getID() || rsNPC.getHPPercent() == 0) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest, out of all of the RSPNCs with the
	 * provided names(s), that does not have 0% HP (is attackable). Can return
	 * null.
	 * 
	 * @param names
	 *            The name(s) of the NPCs that you are searching.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided name(s) that does not have 0% HP (is attackable); or
	 *         null if there are no matching NPCs in the current region.
	 * @see #getNearestByID(names)
	 * @see #getNearestFreeByID(names)
	 * @see #getNearestToAttackByID(names)
	 * @see #getNearestFreeNPCToAttackByID(names)
	 */
	public RSNPC getNearestToAttackByName(final String... names) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = methods.game.client().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			final Node node = Calculations.findNodeByID(methods.game.client()
					.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNPC = new RSNPC(((RSNPCNode) node).getRSNPC());
			for (final String name : names) {
				String npcName = rsNPC.getName();
				if (npcName!=null)  {
					npcName = npcName.toLowerCase();
				}

				if (name == null || !name.toLowerCase().equals(npcName)
						|| rsNPC.getHPPercent() == 0) {
					continue;
				}
				final int distance = rsNPC.distanceTo();
				if (distance < Dist) {
					Dist = distance;
					closest = rsNPC;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the RSNPC that is nearest out of all of the RSNPCs with the
	 * provided ID(s). Can return null.
	 * 
	 * @param ids
	 *            Allowed NPC IDs.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided IDs; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public RSNPC getNearFilterID(final int... ids) {
		return getNearByFilter(new Filter<RSNPC>() {

			@Override
			public boolean accept(final RSNPC rsNPC) {
				if (rsNPC != null) {
					for (final int id : ids) {
						if (rsNPC.getID() == id) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the RSNPC that is nearest out of all of the RSNPCs with the
	 * provided name(s). Can return null.
	 * 
	 * @param names
	 *            Allowed NPC names.
	 * @return RSNPC object representing the nearest RSNPC with one of the
	 *         provided names; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public RSNPC getNearFilterName(final String... names) {
		return getNearByFilter(new Filter<RSNPC>() {

			@Override
			public boolean accept(final RSNPC rsNPC) {
				final String name = rsNPC != null ? rsNPC.getName() : null;
				if (name != null) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}
	
	/**
	 * Gets the RSNPC currently interacting with the player.
	 * @return <tt>RSNPC</tt> of the NPC interacting with the player.
	 */
    public RSNPC getInteracting() {
        final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

        for (final int element : validNPCs) {
            Node localNode = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
            if (localNode == null || !(localNode instanceof RSNPCNode)) {
                continue;
            }
            RSNPC Monster = new RSNPC(((RSNPCNode) localNode).getRSNPC());
            if (Monster.getInteracting() != null) {
                if (Monster.getInteracting().equals(methods.player.getMine())) {
                    return Monster;
                }
            }
        }
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
