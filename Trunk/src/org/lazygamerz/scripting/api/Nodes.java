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

import org.rsbot.client.DefLoader;
import org.rsbot.client.HashTable;
import org.rsbot.client.Node;

/**
 * For internal use to reference data stored in the engine's Node-based
 * structures. Based off of the open source code of RSBot
 * 
 * @author RSBot dev team - version 1.0
 * @author Runedev development team - version 1.1
 */
public class Nodes {

	public Nodes() {
	}

	/**
	 * @param id
	 *            The id of the node
	 * @return A <tt>Node</tt> object corresponding to the ID in the loader.
	 */
	public Node lookup(final DefLoader loader, final long id) {
		if ((loader == null) || (loader.getCache() == null)) {
			return null;
		}
		return lookup(loader.getCache().getTable(), id);
	}

	/**
	 * @param nc
	 *            The node cache to check
	 * @param id
	 *            The id of the node
	 * @return A <tt>Node</tt> object corresponding to the ID in the nodecache.
	 */
	public Node lookup(final HashTable nc, final long id) {
		try {
			if ((nc == null) || (nc.getBuckets() == null) || (id < 0))
				return null;

			final Node n = nc.getBuckets()[(int) (id & nc.getBuckets().length - 1)];
			for (Node node = n.getPrevious(); node != n; node = node
			.getPrevious()) {
				if (node.getID() == id)
					return node;
			}
		} catch (final Exception ignored) {
		}
		return null;
	}

}
