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

import de.muntjak.tinylookandfeel.util.IntReference;

/**
 * IntControl controls one single int value.
 * 
 * @author Hans Bickel
 * @since 1.4.0
 * 
 */
public class IntControl extends JSpinner implements ChangeListener {

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
			((IntControl) ii.next()).confirmChange();
		}

		armedControls.clear();
	}
	private final IntReference ref;
	private final String description;
	private int oldValue;

	private boolean changeState = true;

	/**
	 * 
	 * @param model
	 * @param ref
	 * @param forceUpdate
	 *            if true, applySettingsButton will be enabled each time the
	 *            value changes
	 */
	IntControl(final SpinnerModel model, final IntReference ref, final boolean forceUpdate,
			final String description) {
		super(model);

		this.ref = ref;
		this.description = description;
		oldValue = ((Integer) model.getValue()).intValue();

		addChangeListener(this);
	}

	/**
	 * Sets the argument as the current value and immediately updates the
	 * IntReference.
	 * 
	 * @param value
	 */
	public void commitValue(final int value) {
		changeState = false;
		oldValue = value;
		super.setValue(new Integer(value));
		ref.setValue(value);

		changeState = true;
	}

	private void confirmChange() {
		UndoManager.storeUndoData(this, oldValue);
		oldValue = ((Integer) getValue()).intValue();

		ref.setValue(oldValue);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof IntControl))
			return false;

		if (description == null) {
			return (((IntControl) o).description == null);
		}

		return description.equals(((IntControl) o).description);
	}

	public String getDescription() {
		return description;
	}

	public IntReference getIntReference() {
		return ref;
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
}