/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.plaf.ColorUIResource;

/**
 * HSBReference describes a (mutable) color, specified by a reference color and
 * values for brightness, hue and saturation.
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class HSBReference extends SBReference {

	protected int hue;
	protected boolean preserveGrey;

	/**
	 * Copy-constructor.
	 * 
	 * @param other
	 */
	public HSBReference(final HSBReference other) {
		super(false);

		color = new ColorUIResource(other.color);
		hue = other.hue;
		sat = other.sat;
		bri = other.bri;
		ref = other.ref;
		preserveGrey = other.preserveGrey;
	}

	/**
	 * Constructor for icon colorizers.
	 * 
	 * @param hue
	 * @param sat
	 * @param bri
	 * @param ref
	 */
	public HSBReference(final int hue, final int sat, final int bri, final int ref) {
		super();

		this.hue = hue;
		this.sat = sat;
		this.bri = bri;
		this.ref = ref;
		preserveGrey = true;
	}

	public int getHue() {
		return hue;
	}

	public boolean isPreserveGrey() {
		return preserveGrey;
	}

	@Override
	public void load(final DataInputStream in) throws IOException {
		try {
			hue = in.readInt();
			sat = in.readInt();
			bri = in.readInt();
			ref = in.readInt();
			preserveGrey = in.readBoolean();
		} catch (final Exception ex) {
			throw new IOException("HSBReference.load() : " + ex.getMessage());
		}
	}

	@Override
	public void save(final DataOutputStream out) throws IOException {
		out.writeInt(hue);
		out.writeInt(sat);
		out.writeInt(bri);
		out.writeInt(ref);
		out.writeBoolean(preserveGrey);
	}

	public void setHue(final int newHue) {
		hue = newHue;
	}

	public void setPreserveGrey(final boolean b) {
		preserveGrey = b;
	}

	@Override
	public String toString() {
		return "HSBReference[bri=" + bri + ",sat=" + sat + ",hue=" + hue
		+ ",ref=" + ref + ",c=(" + color.getRed() + ","
		+ color.getGreen() + "," + color.getBlue() + ")]";
	}

	public void update(final HSBReference other) {
		color = new ColorUIResource(other.color);
		hue = other.hue;
		sat = other.sat;
		bri = other.bri;
		ref = other.ref;
		preserveGrey = other.preserveGrey;
	}

	public void update(final HSBReference other, final Vector referenceColors) {
		color = new ColorUIResource(other.color);
		hue = other.hue;
		sat = other.sat;
		bri = other.bri;
		ref = other.ref;
		preserveGrey = other.preserveGrey;
	}
}
