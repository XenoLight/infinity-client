/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.Insets;
import java.util.Vector;

import javax.swing.plaf.InsetsUIResource;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.HSBControl;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.SpreadControl;
import de.muntjak.tinylookandfeel.util.BooleanReference;
import de.muntjak.tinylookandfeel.util.ColoredFont;
import de.muntjak.tinylookandfeel.util.HSBReference;
import de.muntjak.tinylookandfeel.util.IntReference;
import de.muntjak.tinylookandfeel.util.SBReference;

/**
 * ParameterSet
 * 
 * @author Hans Bickel
 * 
 */
public class ParameterSet {

	static ControlPanel controlPanel;
	private final ParameterSetGenerator generator;
	private final Vector references;
	private Vector values;
	private Vector referenceColors;
	private final String name;

	/**
	 * Copy constructor.
	 * 
	 * @param ps
	 */
	public ParameterSet(final ParameterSet ps) {
		generator = ps.generator;
		name = ps.name;
		references = (Vector) ps.references.clone();
		values = (Vector) ps.values.clone();
		// Note: It's essential to retrieve the
		// current colors (and not copy from argument)
		referenceColors = getReferenceColors();
	}

	/**
	 * Creates a new ParameterSet, either from the current selection or from an
	 * entire CP.
	 * 
	 * @param generator
	 * @param name
	 */
	ParameterSet(final ParameterSetGenerator generator, final String name) {
		this.generator = generator;
		this.name = name;
		values = new Vector();
		references = new Vector();
		referenceColors = getReferenceColors();
	}

	void addParameter(final boolean value, final BooleanReference reference) {
		values.add(new Boolean(value));
		// reference is BooleanReference
		references.add(reference);
	}

	void addParameter(final ColoredFont cf) {
		values.add(new ColoredFont(cf));
		// reference is ColoredFont
		references.add(cf);
	}

	void addParameter(final HSBControl control) {
		values.add(new HSBReference(control.getHSBReference()));
		// reference is HSBReference
		references.add(control.getHSBReference());
	}

	void addParameter(final Insets value, final InsetsUIResource reference) {
		values.add(value);
		// reference is InsetsUIResource
		references.add(reference);
	}

	void addParameter(final IntControl control) {
		values.add(control.getValue());
		// reference is IntReference
		references.add(control.getIntReference());
	}

	void addParameter(final SBControl control) {
		if (control.getSBReference().isReferenceColor()) {
			// reference colors must be inserted at the beginning
			if (control.getSBReference().isAbsoluteColor()) {
				values.add(0, new SBReference(control.getSBReference()));
				// reference is SBReference
				references.add(0, control.getSBReference());
			} else { // not an absolute color
				// find index to insert
				final int end = values.size();
				int index = 0;

				for (int i = 0; i < end; i++) {
					final Object value = values.get(i);

					if (value instanceof SBReference) {
						if (!((SBReference) value).isReferenceColor()) {
							index = i;
							break;
						}
					} else {
						index = i;
						break;
					}
				}

				values.add(index, new SBReference(control.getSBReference()));
				// reference is SBReference
				references.add(index, control.getSBReference());
			}
		} else {
			values.add(new SBReference(control.getSBReference()));
			// reference is SBReference
			references.add(control.getSBReference());
		}
	}

	void addParameter(final SpreadControl control) {
		values.add(new Integer(control.getValue()));
		// reference is IntReference
		references.add(control.getIntReference());
	}

	public ParameterSetGenerator getGenerator() {
		return generator;
	}

	/**
	 * Gets vector of ColorUIResources.
	 * 
	 * @return
	 */
	private Vector getReferenceColors() {

		final Vector v = new Vector();
		v.add(Theme.mainColor.getColor());
		v.add(Theme.backColor.getColor());
		v.add(Theme.disColor.getColor());
		v.add(Theme.frameColor.getColor());
		v.add(Theme.sub1Color.getColor());
		v.add(Theme.sub2Color.getColor());
		v.add(Theme.sub3Color.getColor());
		v.add(Theme.sub4Color.getColor());
		v.add(Theme.sub5Color.getColor());
		v.add(Theme.sub6Color.getColor());
		v.add(Theme.sub7Color.getColor());
		v.add(Theme.sub8Color.getColor());

		return v;
	}

	public String getUndoString() {
		return "Paste " + name + " Parameters";
	}

	/**
	 * Sets the values of all references to the current values stored.
	 * 
	 * @param storeUndoData
	 */
	public void pasteParameters(final boolean storeUndoData) {
		if (storeUndoData)
			ControlPanel.instance.storeUndoData(this);

		// set all references to stored value
		final int end = values.size();

		for (int i = 0; i < end; i++) {
			final Object value = values.get(i);
			final Object reference = references.get(i);

			if (reference instanceof BooleanReference) {
				// value is Boolean
				((BooleanReference) reference).setValue(((Boolean) value)
						.booleanValue());
			}
			// because HSBReference *is a* SBReference,
			// it must come before SBReference
			else if (reference instanceof HSBReference) {
				// value is HSBReference
				((HSBReference) reference).update((HSBReference) value,
						referenceColors);
			} else if (reference instanceof SBReference) {
				// value is SBReference
				((SBReference) reference).update((SBReference) value,
						referenceColors);
			} else if (reference instanceof IntReference) {
				// value is Integer
				((IntReference) reference).setValue(((Integer) value)
						.intValue());
			} else if (reference instanceof InsetsUIResource) {
				// value is Insets
				final InsetsUIResource r = (InsetsUIResource) reference;
				final Insets v = (Insets) value;

				r.top = v.top;
				r.left = v.left;
				r.bottom = v.bottom;
				r.right = v.right;
			} else if (reference instanceof ColoredFont) {
				// value is SBReference
				((ColoredFont) reference).update((ColoredFont) value,
						referenceColors);
			}
		}

		generator.init(true); // not called from setTheme() sequence
		ControlPanel.instance.initPanels();
		ControlPanel.instance.setTheme();

	}

	@Override
	public String toString() {
		final StringBuffer buff = new StringBuffer("ParameterSet:");

		final int end = values.size();

		for (int i = 0; i < end; i++) {
			final Object value = values.get(i);
			final Object reference = references.get(i);

			buff.append("\n  reference: " + reference);
			buff.append("\n      value: " + value);
		}

		return buff.toString();
	}

	/**
	 * Stores current reference colors.
	 * 
	 */
	public void updateReferenceColors() {
		referenceColors = getReferenceColors();
	}

	/**
	 * Sets all values to the current value of the reference. This allows for
	 * doing a redo after an undo (or vice versa). Changes values only.
	 * 
	 */
	public void updateValues() {
		final Vector temp = new Vector(values.size());
		final int end = values.size();

		for (int i = 0; i < end; i++) {
			values.get(i);
			final Object reference = references.get(i);

			if (reference instanceof BooleanReference) {
				// value is Boolean
				temp.add(new Boolean(((BooleanReference) reference).getValue()));
			}
			// because HSBReference *is a* SBReference,
			// it must come before SBReference
			else if (reference instanceof HSBReference) {
				// value is HSBReference
				temp.add(new HSBReference((HSBReference) reference));
			} else if (reference instanceof SBReference) {
				// value is SBReference
				temp.add(new SBReference((SBReference) reference));
			} else if (reference instanceof IntReference) {
				// value is Integer
				temp.add(new Integer(((IntReference) reference).getValue()));
			} else if (reference instanceof InsetsUIResource) {
				// value is Insets
				final InsetsUIResource r = (InsetsUIResource) reference;

				temp.add(new Insets(r.top, r.left, r.bottom, r.right));
			} else if (reference instanceof ColoredFont) {
				// value is ColoredFont
				temp.add(new ColoredFont((ColoredFont) reference));
			}
		}

		values = temp;
	}

	/**
	 * Clones argument's value vector.
	 * 
	 * @param other
	 */
	public void updateValues(final ParameterSet other) {
		values = (Vector) other.values.clone();
	}
}
