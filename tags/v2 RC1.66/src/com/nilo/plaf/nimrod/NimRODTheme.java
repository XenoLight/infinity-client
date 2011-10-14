/*
 *                 (C) Copyright 2005 Nilo J. Gonzalez
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser Gereral Public Licence as published by the Free
 * Software Foundation; either version 2 of the Licence, or (at your opinion) any
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
 *
 * Original author: Nilo J. Gonzalez
 */

/**
 * Esta clase implementa los colores por defecto del NimRODLookAndFeel.
 * @author Nilo J. Gonzalez
 */

package com.nilo.plaf.nimrod;

import java.awt.Color;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 * Define un <I>tema</I> de color para el NimRODLookAndFeel. En realidad, valen
 * para cualquier Look&Feel que herede de MetalLookAndFeel.<BR>
 * Se usa asi:
 * 
 * <PRE>
 * NimRODLookAndFeel NimRODLF = new NimRODLookAndFeel();
 * NimRODLF.setCurrentTheme(new NimRODTheme());
 * UIManager.setLookAndFeel(NimRODLF);
 * </PRE>
 * 
 * Con esto se pone un color gris oscuro. Tambien define temas partiendo de un
 * color base, modificando los valores primarios.
 * 
 * <PRE>
 * NimRODLookAndFeel NimRODLF = new NimRODLookAndFeel();
 * NimRODLF.setCurrentTheme( new NimRODTheme( <I>unColor</I>));
 * UIManager.setLookAndFeel(NimRODLF);
 * </PRE>
 * 
 * o partiendo de dos colores base, uno para los valores primarios y otro para
 * los secundarios.
 * 
 * <PRE>
 * NimRODLookAndFeel NimRODLF = new NimRODLookAndFeel();
 * NimRODLF.setCurrentTheme( new NimRODTheme( <I>unColorPrimario</I>, <I>unColorSecundario</I>));
 * UIManager.setLookAndFeel(NimRODLF);
 * </PRE>
 * 
 * Para entender como va el temita de los colores, puede ayudar mucho consultar
 * esta pagina: <a href=
 * 'http://java.sun.com/products/jlf/ed1/dg/higg.htm'>http://java.sun.com/products/jlf/ed1/dg/higg.htm</
 * a >
 */
public class NimRODTheme extends DefaultMetalTheme {
	public static final int DEFAULT_MENU_OPACITY = 195;
	public static final int DEFAULT_FRAME_OPACITY = 180;

	// primarios
	private ColorUIResource primary1 = new ColorUIResource(229, 189, 0);

	private ColorUIResource primary2 = new ColorUIResource(239, 199, 0);

	private ColorUIResource primary3 = new ColorUIResource(249, 209, 0);

	// secondarios
	private ColorUIResource secondary1 = new ColorUIResource(217, 215, 173);

	private ColorUIResource secondary2 = new ColorUIResource(227, 225, 183);

	private ColorUIResource secondary3 = new ColorUIResource(237, 235, 193);

	private ColorUIResource black = new ColorUIResource(0, 0, 0);

	private ColorUIResource white = new ColorUIResource(255, 255, 255);

	// la fuente
	private FontUIResource font = new FontUIResource("SansSerif", Font.PLAIN,
			12);
	private FontUIResource boldFont = new FontUIResource("SansSerif",
			Font.BOLD, 12);

	// la opacidadMenu de los menus
	private int opacidadMenu = DEFAULT_MENU_OPACITY;

	// la opacidadMenu de los InternalFrames
	private int opacidadFrame = DEFAULT_FRAME_OPACITY;

	public NimRODTheme() {
		super();
	}

	/**
	 * Este constructor recibe por parametro el color que se desea utilizar como
	 * color principal de "fondo". Es el color que se usara como fondo de los
	 * botones, dialogos, menus... El resto de los colores de fondo se calculan
	 * oscureciendo este en diversa medida.
	 * 
	 * @param base
	 *            Color el color de fondo.
	 */
	public NimRODTheme(final Color base) {
		super();

		setPrimary(base);
	}

	/**
	 * Este constructor recibe por parametro los colores que se desea utilizar.
	 * Base es el color que se usara como fondo de los botones, dialogos,
	 * menus... y prim es el color que se usara para los objetos seleccionados.
	 * En palabras de Sun, Prim es el color que da "personalidad" al tema... El
	 * resto de los colores se calculan oscureciendo estos en diversa medida.
	 * 
	 * @param prim
	 *            Color el color a usar en las selecciones.
	 * @param base
	 *            Color el color de fondo.
	 */
	public NimRODTheme(final Color prim, final Color sec) {
		super();

		setPrimary(prim);
		setSecondary(sec);
	}

	/**
	 * Este constructor crea un tema partiendo de un fichero de tema
	 * **COMPLETO** situado en la ruta marcada por el parametro nomFich. Si no
	 * se encuentra en el sistema de ficheros, se busca en al classpath. Si el
	 * fichero de tema esta incompleto, salta una excepcion
	 * NumberFormatException con el valor que ha dado el problema
	 * 
	 * @param nomFich
	 *            el nombre del fichero
	 */
	public NimRODTheme(String nomFich) {
		super();

		final Properties props = new Properties();
		InputStream res = null;

		try {
			res = new FileInputStream(nomFich); // Primero, se carga el fichero
		} catch (final Exception ex) {
			nomFich = "/" + nomFich;
			res = this.getClass().getResourceAsStream(nomFich); // Si no hay
			// fichero, se
			// busca en el
			// classpath/jar
		}

		if (res != null) {
			try {
				props.load(res);
				res.close();
				initFromProps(props);
			} catch (final Exception ex) {
				ex.printStackTrace();
				return; // Si no esta en ningun sitio, esto dara una excepcion y
				// deja los colores por defecto
			}
		}
	}

	/**
	 * Este constructor crea un tema partiendo de un fichero de tema
	 * **COMPLETO** situado en la URL apuntada por el parametro url. Si el
	 * fichero de tema esta incompleto, salta una excepcion
	 * NumberFormatException con el valor que ha dado el problema
	 * 
	 * @param url
	 *            la url del tema
	 */
	public NimRODTheme(final URL url) {
		super();

		final Properties props = new Properties();
		InputStream res = null;

		try {
			final URLConnection con = url.openConnection();
			res = con.getInputStream();
			props.load(res);
			res.close();
			initFromProps(props);
		} catch (final Exception ex) {
			return; // Si no esta en ningun sitio, esto dara una excepcion y
			// deja los colores por defecto
		}
	}

	protected String encode(final Color col) {
		final String r = Integer.toHexString(col.getRed()).toUpperCase();
		final String g = Integer.toHexString(col.getGreen()).toUpperCase();
		final String b = Integer.toHexString(col.getBlue()).toUpperCase();

		return "#" + (r.length() == 1 ? "0" + r : r)
		+ (g.length() == 1 ? "0" + g : g)
		+ (b.length() == 1 ? "0" + b : b);
	}

	protected String encode(final Font ff) {
		final StringBuilder res = new StringBuilder();

		res.append(ff.getName() + "-");

		if (ff.isPlain()) {
			res.append("PLAIN-");
		} else if (ff.isBold() && ff.isItalic()) {
			res.append("BOLDITALIC-");
		} else if (ff.isBold()) {
			res.append("BOLD-");
		} else if (ff.isItalic()) {
			res.append("ITALIC-");
		}

		res.append(ff.getSize());

		return res.toString();
	}

	@Override
	protected ColorUIResource getBlack() {
		return black;
	}

	@Override
	public FontUIResource getControlTextFont() {
		return font;
	}

	public int getFrameOpacity() {
		return opacidadFrame;
	}

	public int getMenuOpacity() {
		return opacidadMenu;
	}

	@Override
	public FontUIResource getMenuTextFont() {
		return font;
	}

	@Override
	public String getName() {
		return "NimROD Theme";
	}

	public int getOpacity() {
		return getMenuOpacity();
	}

	@Override
	protected ColorUIResource getPrimary1() {
		return primary1;
	}

	@Override
	protected ColorUIResource getPrimary2() {
		return primary2;
	}

	@Override
	protected ColorUIResource getPrimary3() {
		return primary3;
	}

	@Override
	protected ColorUIResource getSecondary1() {
		return secondary1;
	}

	@Override
	protected ColorUIResource getSecondary2() {
		return secondary2;
	}

	@Override
	protected ColorUIResource getSecondary3() {
		return secondary3;
	}

	@Override
	public FontUIResource getSubTextFont() {
		return font;
	}

	@Override
	public FontUIResource getSystemTextFont() {
		return boldFont;
	}

	@Override
	public FontUIResource getUserTextFont() {
		return font;
	}

	@Override
	protected ColorUIResource getWhite() {
		return white;
	}

	@Override
	public FontUIResource getWindowTitleFont() {
		return boldFont;
	}

	private void initFromProps(final Properties props) {
		setPrimary1(Color.decode(props.getProperty("nimrodlf.p1")));
		setPrimary2(Color.decode(props.getProperty("nimrodlf.p2")));
		setPrimary3(Color.decode(props.getProperty("nimrodlf.p3")));

		setSecondary1(Color.decode(props.getProperty("nimrodlf.s1")));
		setSecondary2(Color.decode(props.getProperty("nimrodlf.s2")));
		setSecondary3(Color.decode(props.getProperty("nimrodlf.s3")));

		setWhite(Color.decode(props.getProperty("nimrodlf.w")));
		setBlack(Color.decode(props.getProperty("nimrodlf.b")));

		setMenuOpacity(Integer.parseInt(props
				.getProperty("nimrodlf.menuOpacity")));
		setFrameOpacity(Integer.parseInt(props
				.getProperty("nimrodlf.frameOpacity")));

		if (props.getProperty("nimrodlf.font") != null) {
			setFont(Font.decode(props.getProperty("nimrodlf.font")));
		}
	}

	public void setBlack(final Color col) {
		black = new ColorUIResource(col);
	}

	public void setFont(final Font ff) {
		font = new FontUIResource(ff);
		boldFont = new FontUIResource(ff.deriveFont(Font.BOLD));
	}

	public void setFrameOpacity(final int val) {
		if (val < 0 || val > 255)
			throw new NumberFormatException(
					"MenuOpacity out of range [0,255]: " + val);
		opacidadFrame = val;
	}

	public void setMenuOpacity(final int val) {
		if (val < 0 || val > 255)
			throw new NumberFormatException(
					"MenuOpacity out of range [0,255]: " + val);
		opacidadMenu = val;
	}

	public void setOpacity(final int val) {
		setMenuOpacity(val);
	}

	public void setPrimary(final Color selection) {
		final int r = selection.getRed();
		final int g = selection.getGreen();
		final int b = selection.getBlue();

		primary1 = new ColorUIResource(new Color((r > 20 ? r - 20 : 0),
				(g > 20 ? g - 20 : 0), (b > 20 ? b - 20 : 0)));
		primary2 = new ColorUIResource(new Color((r > 10 ? r - 10 : 0),
				(g > 10 ? g - 10 : 0), (b > 10 ? b - 10 : 0)));
		primary3 = new ColorUIResource(selection);
	}

	public void setPrimary1(final Color col) {
		primary1 = new ColorUIResource(col);
	}

	public void setPrimary2(final Color col) {
		primary2 = new ColorUIResource(col);
	}

	public void setPrimary3(final Color col) {
		primary3 = new ColorUIResource(col);
	}

	public void setSecondary(final Color background) {
		final int r = background.getRed();
		final int g = background.getGreen();
		final int b = background.getBlue();

		secondary1 = new ColorUIResource(new Color((r > 20 ? r - 20 : 0),
				(g > 20 ? g - 20 : 0), (b > 20 ? b - 20 : 0)));
		secondary2 = new ColorUIResource(new Color((r > 10 ? r - 10 : 0),
				(g > 10 ? g - 10 : 0), (b > 10 ? b - 10 : 0)));
		secondary3 = new ColorUIResource(background);
	}

	public void setSecondary1(final Color col) {
		secondary1 = new ColorUIResource(col);
	}

	public void setSecondary2(final Color col) {
		secondary2 = new ColorUIResource(col);
	}

	public void setSecondary3(final Color col) {
		secondary3 = new ColorUIResource(col);
	}

	public void setWhite(final Color col) {
		white = new ColorUIResource(col);
	}

	@Override
	public String toString() {
		final StringBuffer cad = new StringBuffer();

		cad.append("nimrodlf.p1=" + encode(primary1) + "\n");
		cad.append("nimrodlf.p2=" + encode(primary2) + "\n");
		cad.append("nimrodlf.p3=" + encode(primary3) + "\n");
		cad.append("nimrodlf.s1=" + encode(secondary1) + "\n");
		cad.append("nimrodlf.s2=" + encode(secondary2) + "\n");
		cad.append("nimrodlf.s3=" + encode(secondary3) + "\n");

		cad.append("nimrodlf.w=" + encode(white) + "\n");
		cad.append("nimrodlf.b=" + encode(black) + "\n");
		cad.append("nimrodlf.menuOpacity=" + opacidadMenu + "\n");
		cad.append("nimrodlf.frameOpacity=" + opacidadFrame + "\n");

		cad.append("nimrodlf.font=" + encode(font) + "\n");

		return cad.toString();
	}
}
