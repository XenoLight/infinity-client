/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.InsetsUIResource;

/**
 * InsetsControl is a JSpinner controlling one side of an Insets object.
 * 
 * @author Hans Bickel
 */
public class InsetsControl extends JSpinner implements ChangeListener {

	public static final int TOP = 1;
	public static final int LEFT = 2;
	public static final int BOTTOM = 3;
	public static final int RIGHT = 4;

	private static final Vector armedControls = new Vector();
	/**
	 * Should be called after 'Apply Settings' button was clicked.
	 * 
	 */
	static void confirmChanges() {
		if (armedControls.isEmpty())
			return;

		final Iterator ii = armedControls.iterator();
		while (ii.hasNext()) {
			((InsetsControl) ii.next()).confirmChange();
		}

		armedControls.clear();
	}
	private final InsetsUIResource ref;
	private final int position;
	private int oldValue;

	boolean changeState = true;

	public InsetsControl(final SpinnerModel model, final InsetsUIResource ref, final int position) {
		super(model);

		this.ref = ref;
		this.position = position;
		oldValue = ((Integer) model.getValue()).intValue();

		addChangeListener(this);
	}

	/**
	 * Sets the argument as the current value and immediately updates Insets
	 * reference.
	 * 
	 * @param value
	 */
	public void commitValue(final int value) {
		changeState = false;
		oldValue = value;
		super.setValue(new Integer(value));
		updateInsets();

		changeState = true;
	}

	private void confirmChange() {
		UndoManager.storeUndoData(this, oldValue);
		oldValue = ((Integer) getValue()).intValue();

		updateInsets();
	}

	public int getIntValue() {
		return ((Integer) getValue()).intValue();
	}

	public String getPositionString() {
		if (position == TOP)
			return "top";
		if (position == LEFT)
			return "left";
		if (position == BOTTOM)
			return "bottom";
		else
			return "right";
	}

	/**
	 * Sets the argument as the current value but doesn't update Insets
	 * reference.
	 * 
	 * @param value
	 */
	public void setValue(final int value) {
		changeState = false;
		oldValue = value;
		super.setValue(new Integer(value));
		changeState = true;
	}

	// ChangeListener impl
	@Override
	public void stateChanged(final ChangeEvent e) {
		if (!changeState)
			return;

		if (!armedControls.contains(this)) {
			armedControls.add(this);
		}

		if (!ControlPanel.instance.applySettingsButton.isEnabled()) {
			ControlPanel.instance.applySettingsButton.setEnabled(true);
		}
	}

	private void updateInsets() {
		switch (position) {
		case TOP:
			ref.top = oldValue;
			break;
		case LEFT:
			ref.left = oldValue;
			break;
		case BOTTOM:
			ref.bottom = oldValue;
			break;
		case RIGHT:
			ref.right = oldValue;
			break;
		}
	}
}
