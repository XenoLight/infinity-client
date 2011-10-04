package org.rsbot.script.wrappers;

import org.rsbot.client.Model;
import org.rsbot.client.RSObject;
import org.rsbot.script.Methods;

public class RSObjectModel extends RSModel {

	protected RSObject object;

	RSObjectModel(final Methods methods, final Model model, final RSObject object) {
		super(methods, model);
		this.object = object;
	}

	@Override
	protected int getLocalX() {
		return object.getX();
	}

	@Override
	protected int getLocalY() {
		return object.getY();
	}

	@Override
	protected void refresh() {

	}

}
