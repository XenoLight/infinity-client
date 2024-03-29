/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.util;

import java.awt.Font;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.plaf.FontUIResource;

import de.muntjak.tinylookandfeel.Theme;

/**
 * ColoredFont describes a (mutable) font and an optional (text) color. The
 * class is used to specify, for example, the font and text color used by
 * buttons.
 * 
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class ColoredFont {

	public static void loadDummyData(final DataInputStream in) throws IOException {
		in.readUTF();
		in.readBoolean();
		in.readInt();
		in.readBoolean();
		in.readBoolean();
	}
	private SBReference sbReference;
	private FontUIResource font;

	private boolean isPlainFont, isBoldFont;

	/**
	 * Construcor for Plain Fonts without a color reference.
	 * 
	 */
	public ColoredFont() {
		font = new FontUIResource(Theme.getPlatformFont("Tahoma"), Font.PLAIN,
				11);
		isPlainFont = true;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 */
	public ColoredFont(final ColoredFont other) {
		font = new FontUIResource(other.font);

		if (other.sbReference != null) {
			sbReference = new SBReference(other.sbReference);
		}

		isPlainFont = other.isPlainFont;
		isBoldFont = other.isBoldFont;
	}

	/**
	 * Construcor for Plain Fonts.
	 * 
	 * @param ref
	 *            the color reference of this font
	 * 
	 */
	public ColoredFont(final SBReference ref) {
		this("Tahoma", Font.PLAIN, 11, ref);
		isPlainFont = true;
	}

	/**
	 * Constructs a new ColoredFont using the passed arguments.
	 * 
	 * @param fontFamily
	 * @param style
	 * @param size
	 */
	public ColoredFont(final String fontFamily, final int style, final int size) {
		font = new FontUIResource(Theme.getPlatformFont(fontFamily), style,
				size);
	}

	private ColoredFont(final String fontFamily, final int style, final int size,
			final SBReference sbReference) {
		font = new FontUIResource(Theme.getPlatformFont(fontFamily), style,
				size);
		this.sbReference = sbReference;
	}

	public FontUIResource getFont() {
		if (isPlainFont) {
			return Theme.plainFont.font;
		}

		if (isBoldFont) {
			return Theme.boldFont.font;
		}

		return font;
	}

	public SBReference getSBReference() {
		return sbReference;
	}

	public boolean isBoldFont() {
		return isBoldFont;
	}

	public boolean isPlainFont() {
		return isPlainFont;
	}

	public void load(final DataInputStream in) throws IOException {
		font = new FontUIResource(Theme.getPlatformFont(in.readUTF()),
				(in.readBoolean() ? Font.BOLD : Font.PLAIN), in.readInt());
		isPlainFont = in.readBoolean();
		isBoldFont = in.readBoolean();
	}

	public void save(final DataOutputStream out) throws IOException {
		out.writeUTF(font.getFamily());
		out.writeBoolean(font.isBold());
		out.writeInt(font.getSize());
		out.writeBoolean(isPlainFont);
		out.writeBoolean(isBoldFont);
	}

	public void setBoldFont(final boolean b) {
		isBoldFont = b;

		if (b)
			isPlainFont = false;
	}

	public void setFont(final Font font) {
		this.font = new FontUIResource(font);
	}

	public void setFont(final FontUIResource font) {
		this.font = font;
	}

	public void setFont(final String fontFamily, final int style, final int size) {
		font = new FontUIResource(fontFamily, style, size);
	}

	public void setPlainFont(final boolean b) {
		isPlainFont = b;

		if (b)
			isBoldFont = false;
	}

	public void setSBReference(final SBReference sbReference) {
		this.sbReference = sbReference;
	}

	@Override
	public String toString() {
		return "ColoredFont[ref="
		+ (sbReference == null ? "null" : sbReference.toString())
		+ ",font=" + font + "]";
	}

	/**
	 * Sets up this ColoredFont to be identical to argument.
	 * 
	 * @param other
	 */
	public void update(final ColoredFont other) {
		font = new FontUIResource(other.font);
		isPlainFont = other.isPlainFont;
		isBoldFont = other.isBoldFont;

		if (other.sbReference != null) {
			sbReference.setBrightness(other.sbReference.getBrightness());
			sbReference.setLocked(other.sbReference.isLocked());
			sbReference.setReference(other.sbReference.getReference());
			sbReference.setSaturation(other.sbReference.getSaturation());
			sbReference.setColor(other.sbReference.getColor());
		}
	}

	public void update(final ColoredFont other, final Vector referenceColors) {
		font = new FontUIResource(other.font);
		isPlainFont = other.isPlainFont;
		isBoldFont = other.isBoldFont;

		if (other.sbReference != null) {
			sbReference.update(other.sbReference, referenceColors);
		}
	}

	/**
	 * Updates a ColoredFont to be a Plain Font,
	 * 
	 * @param sbReference
	 */
	public void update(final SBReference sbReference) {
		font = new FontUIResource(Theme.getPlatformFont("Tahoma"), Font.PLAIN,
				11);
		this.sbReference = sbReference;
		isPlainFont = true;
	}

	public void update(final String fontFamily, final int style, final int size) {
		font = new FontUIResource(Theme.getPlatformFont(fontFamily), style,
				size);
	}
}
