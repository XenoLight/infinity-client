package org.rsbot.script.internal;

import org.rsbot.client.NodeDeque;

@SuppressWarnings("unchecked")
public class Deque<N> {

	private final NodeDeque nl;
	private org.rsbot.client.Node current;

	public Deque(final NodeDeque nl) {
		this.nl = nl;
	}

	public N getHead() {
		final org.rsbot.client.Node node = nl.getTail().getNext();

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		
		if (node!=null)  {
			current = node.getNext();
		}

		return (N) node;
	}

	public N getNext() {
		final org.rsbot.client.Node node = current;

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		
		if (node!=null)  {
			current = node.getNext();
		}

		return (N) node;
	}

	public N getTail() {
		final org.rsbot.client.Node node = nl.getTail().getPrevious();

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		
		if (node!=null)  {
			current = node.getPrevious();
		}

		return (N) node;
	}

	public int size() {
		int size = 0;
		org.rsbot.client.Node node = nl.getTail().getPrevious();

		while (node != nl.getTail()) {
			node = node.getPrevious();
			size++;
		}

		return size;
	}

}
