/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * PSColorChooser
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class PSColorChooser extends JDialog {

	class CancelAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			outColor = null;
			setVisible(false);
		}
	}
	class ColorSelector extends JPanel {

		class Mousey extends MouseAdapter {

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.getX() < 1 || e.getX() > 256)
					return;
				if (e.getY() < 1 || e.getY() > 256)
					return;

				s = e.getX() - 1;
				if (s < 0)
					s = 0;
				else if (s > 255)
					s = 255;

				int y = e.getY() - 1;
				if (y < 0)
					y = 0;
				else if (y > 255)
					y = 255;

				mousePressed = true;

				b = 255 - y;
				paint(ColorSelector.this.getGraphics());
				theColor = Color.getHSBColor(hue, (float) (s / 255.0),
						(float) (b / 255.0));
				colorChanged(theColor);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				mousePressed = false;
			}
		}
		class MouseyDrag extends MouseMotionAdapter {

			@Override
			public void mouseDragged(final MouseEvent e) {
				s = e.getX() - 1;
				if (s < 0)
					s = 0;
				else if (s > 255)
					s = 255;

				int y = e.getY() - 1;
				if (y < 0)
					y = 0;
				else if (y > 255)
					y = 255;

				b = 255 - y;
				paint(ColorSelector.this.getGraphics());
				theColor = Color.getHSBColor(hue, (float) (s / 255.0),
						(float) (b / 255.0));
				colorChanged(theColor);
			}
		}
		private final Dimension size = new Dimension(258, 258);
		private float hue;
		private int h, s, b;
		private int circleX, circleY;

		private Color theColor;

		private boolean mousePressed = false;

		ColorSelector(final Color c) {
			h = ColorRoutines.getHue(c);
			s = ColorRoutines.getSaturation(c) * 255 / 100;
			b = ColorRoutines.getBrightness(c) * 255 / 100;
			hue = (float) (h / 360.0);
			theColor = c;

			setBorder(new LineBorder(Color.BLACK, 1));
			addMouseListener(new Mousey());
			addMouseMotionListener(new MouseyDrag());
			setCursor(cs_cursor);
		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}

		@Override
		public void paint(final Graphics g) {
			super.paintBorder(g);

			if (mousePressed) {
				g.setClip(circleX, circleY, 11, 11);
			}

			Color c;
			float sat;

			for (int x = 0; x < 256; x++) {
				sat = (float) (x / 255.0);
				c = Color.getHSBColor(hue, sat, 1.0f);
				g.setColor(c);
				g.drawLine(x + 1, 1, x + 1, 256);
			}

			g.drawImage(brightmask, 1, 1, this);

			circleX = s - 4; // 0 => 0, 100 => 255
			circleY = 255 - b - 4; // 100 => 0, 0 => 255

			if (mousePressed) {
				g.setClip(circleX, circleY, 11, 11);
			}

			if (b < 160) {
				g.setColor(Color.WHITE);
			} else {
				g.setColor(Color.BLACK);
			}

			g.drawLine(circleX + 3, circleY, circleX + 7, circleY);
			g.drawLine(circleX + 3, circleY + 10, circleX + 7, circleY + 10);
			g.drawLine(circleX, circleY + 3, circleX, circleY + 7);
			g.drawLine(circleX + 10, circleY + 3, circleX + 10, circleY + 7);

			g.drawLine(circleX + 2, circleY + 1, circleX + 2, circleY + 1);
			g.drawLine(circleX + 8, circleY + 1, circleX + 8, circleY + 1);
			g.drawLine(circleX + 1, circleY + 2, circleX + 1, circleY + 2);
			g.drawLine(circleX + 9, circleY + 2, circleX + 9, circleY + 2);

			g.drawLine(circleX + 1, circleY + 8, circleX + 1, circleY + 8);
			g.drawLine(circleX + 9, circleY + 8, circleX + 9, circleY + 8);
			g.drawLine(circleX + 2, circleY + 9, circleX + 2, circleY + 9);
			g.drawLine(circleX + 8, circleY + 9, circleX + 8, circleY + 9);
		}

		void setColor(final Color c) {
			h = hueField.getValue();
			s = satField.getValue() * 255 / 100;
			b = briField.getValue() * 255 / 100;
			hue = (float) (h / 360.0);

			repaint(0);
		}

		@Override
		public void update(final Graphics g) {
			paint(g);
		}
	}
	class HSBAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (spinnerUpdate)
				return;

			final int h = hueField.getValue();
			final int s = satField.getValue();
			final int b = briField.getValue();

			final Color c = Color.getHSBColor((h / 360.0f), (s / 100.0f),
					(b / 100.0f));
			twoColorField.setUpperColor(c);

			spinnerUpdate = true;
			redField.setValue(c.getRed());
			greenField.setValue(c.getGreen());
			blueField.setValue(c.getBlue());
			spinnerUpdate = false;

			hueSelector.setHue(h);
			colorSelector.setColor(c);
		}
	}
	class HueSelector extends JPanel {

		class Mousey extends MouseAdapter {

			@Override
			public void mousePressed(final MouseEvent e) {
				if (e.getY() < 5 || e.getY() > 260)
					return;

				hue = (float) ((255 - (e.getY() - 5)) / 255.0);

				repaint();
				hueChanged((int) (hue * 360.0));
			}
		}
		class MouseyDrag extends MouseMotionAdapter {

			@Override
			public void mouseDragged(final MouseEvent e) {
				int y = e.getY() - 5;
				if (y < 0)
					y = 0;
				else if (y > 255)
					y = 255;

				hue = (float) ((255 - y) / 255.0);
				repaint();
				hueChanged((int) (hue * 360.0));
			}
		}
		private final Color darkColor = new Color(128, 128, 128);
		private final Dimension size = new Dimension(35, 266);
		private float hue;

		private int arrowY;

		HueSelector(final int hue) {
			this.hue = (float) (hue / 360.0);
			addMouseListener(new Mousey());
			addMouseMotionListener(new MouseyDrag());
		}

		private void drawArrows(final Graphics g) {
			arrowY = 255 + 5 - (int) (hue * 255.0);

			g.setColor(Color.BLACK);
			g.drawLine(0, arrowY - 5, 0, arrowY + 5);
			g.drawLine(1, arrowY - 4, 1, arrowY - 4);
			g.drawLine(2, arrowY - 3, 2, arrowY - 3);
			g.drawLine(3, arrowY - 2, 3, arrowY - 2);
			g.drawLine(4, arrowY - 1, 4, arrowY - 1);
			g.drawLine(5, arrowY, 5, arrowY);
			g.drawLine(0, arrowY + 5, 0, arrowY + 5);
			g.drawLine(1, arrowY + 4, 1, arrowY + 4);
			g.drawLine(2, arrowY + 3, 2, arrowY + 3);
			g.drawLine(3, arrowY + 2, 3, arrowY + 2);
			g.drawLine(4, arrowY + 1, 4, arrowY + 1);

			g.drawLine(34, arrowY - 5, 34, arrowY + 5);
			g.drawLine(33, arrowY - 4, 33, arrowY - 4);
			g.drawLine(32, arrowY - 3, 32, arrowY - 3);
			g.drawLine(31, arrowY - 2, 31, arrowY - 2);
			g.drawLine(30, arrowY - 1, 30, arrowY - 1);
			g.drawLine(29, arrowY, 29, arrowY);
			g.drawLine(34, arrowY + 5, 34, arrowY + 5);
			g.drawLine(33, arrowY + 4, 33, arrowY + 4);
			g.drawLine(32, arrowY + 3, 32, arrowY + 3);
			g.drawLine(31, arrowY + 2, 31, arrowY + 2);
			g.drawLine(30, arrowY + 1, 30, arrowY + 1);
		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}

		@Override
		public void paint(final Graphics g) {
			g.setColor(Theme.backColor.getColor());
			g.fillRect(0, 0, 35, 266);

			drawArrows(g);

			// border
			g.setColor(darkColor);
			g.drawLine(6, 3, 27, 3);
			g.drawLine(6, 3, 6, 261);

			g.setColor(Color.WHITE);
			g.drawLine(6, 262, 28, 262);
			g.drawLine(28, 3, 28, 261);

			g.setColor(Color.BLACK);
			g.drawRect(7, 4, 20, 257);

			// gradients
			final int x1 = 8;
			final int x2 = 26;
			final int y1 = 5;

			for (int y = 0; y < 256; y++) {
				final float h = (float) ((255 - y) / 255.0);
				g.setColor(Color.getHSBColor(h, 1.0f, 1.0f));
				g.drawLine(x1, y + y1, x2, y + y1);
			}
		}

		void setHue(final int hue) {
			this.hue = (float) (hue / 360.0);
			repaint(0);
		}
	}
	class OKAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			setVisible(false);
		}
	}
	class RGBAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (spinnerUpdate)
				return;

			final int r = redField.getValue();
			final int g = greenField.getValue();
			final int b = blueField.getValue();

			final Color c = new Color(r, g, b);
			twoColorField.setUpperColor(c);

			spinnerUpdate = true;
			final int hue = ColorRoutines.getHue(c);
			hueField.setValue(hue);
			satField.setValue(ColorRoutines.getSaturation(c));
			briField.setValue(ColorRoutines.getBrightness(c));
			spinnerUpdate = false;

			hueSelector.setHue(hue);
			colorSelector.setColor(c);
		}
	}
	class TwoColorField extends JPanel {

		private final Dimension size = new Dimension(60, 68);
		private Color upperColor, lowerColor;

		TwoColorField(final Color c) {
			setBorder(new LineBorder(Color.BLACK, 1));

			upperColor = c;
			lowerColor = c;
		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}

		@Override
		public void paint(final Graphics g) {
			super.paintBorder(g);

			g.setColor(upperColor);
			g.fillRect(1, 1, 58, 33);

			g.setColor(lowerColor);
			g.fillRect(1, 34, 58, 33);
		}

		void setLowerColor(final Color c) {
			lowerColor = c;
			repaint(0);
		}

		void setUpperColor(final Color c) {
			upperColor = c;
			outColor = c;
			repaint(0);
		}
	}
	public static void deleteInstance() {
		instance = null;
	}

	public static Color showColorChooser(final Frame frame, final Color inColor) {
		if (instance == null) {
			instance = new PSColorChooser(frame, inColor);
		}

		instance.setColor(inColor);
		instance.setVisible(true);
		return instance.outColor;
	}
	private Color inColor, outColor;
	private static PSColorChooser instance;

	private ColorSelector colorSelector;

	private HueSelector hueSelector;

	private TwoColorField twoColorField;

	private NumericTextField redField, greenField, blueField;

	private NumericTextField satField, briField, hueField;

	private boolean spinnerUpdate = false;

	private static Cursor cs_cursor;

	private static BufferedImage brightmask;

	private static GraphicsConfiguration conf;

	static {
		final GraphicsEnvironment ge = GraphicsEnvironment
		.getLocalGraphicsEnvironment();
		conf = ge.getDefaultScreenDevice().getDefaultConfiguration();
		brightmask = loadBrightmask();
		cs_cursor = loadCursor();
	}

	private static BufferedImage loadBrightmask() {
		final ImageIcon img = loadImageIcon("/de/muntjak/tinylookandfeel/cp_icons/brightmask.png");
		final BufferedImage bimg = conf.createCompatibleImage(256, 256,
				Transparency.TRANSLUCENT);
		final Graphics g = bimg.getGraphics();
		g.drawImage(img.getImage(), 0, 0, 256, 256, 0, 0, 1, 256, null);

		return bimg;
	}

	private static Cursor loadCursor() {
		ImageIcon img = null;
		Cursor c = null;
		final Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(16, 16);

		if (size.width == 32) {
			img = loadImageIcon("/de/muntjak/tinylookandfeel/cp_icons/cs32.gif");
			c = Toolkit.getDefaultToolkit().createCustomCursor(img.getImage(),
					new Point(15, 15), "cs_cursor");
		} else if (size.width == 16) {
			img = loadImageIcon("/de/muntjak/tinylookandfeel/cp_icons/cs16.gif");
			c = Toolkit.getDefaultToolkit().createCustomCursor(img.getImage(),
					new Point(7, 7), "cs_cursor");
		}

		return c;
	}

	protected static ImageIcon loadImageIcon(final String fn) {
		return new ImageIcon(PSColorChooser.class.getResource(fn));
	}

	private PSColorChooser(final Frame frame, final Color inColor) {
		super(frame, "Color Chooser", true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		this.inColor = inColor;
		outColor = inColor;

		setupUI(frame, inColor);
	}

	private void colorChanged(final Color c) {
		spinnerUpdate = true;
		final int hue = ColorRoutines.getHue(c);
		final int sat = ColorRoutines.getSaturation(c);
		final int bri = ColorRoutines.getBrightness(c);

		satField.setValue(sat);
		briField.setValue(bri);
		hueField.setValue(hue);
		redField.setValue(c.getRed());
		greenField.setValue(c.getGreen());
		blueField.setValue(c.getBlue());

		twoColorField.setUpperColor(c);
		spinnerUpdate = false;
	}

	private JPanel createNumericTextFields() {
		final ActionListener rgbAction = new RGBAction();
		final ActionListener hsbAction = new HSBAction();

		final JPanel p2 = new JPanel(new GridBagLayout());
		final GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(0, 0, 4, 2);
		gc.anchor = GridBagConstraints.WEST;
		gc.gridx = 0;
		gc.gridy = 0;

		p2.add(new JLabel("H:"), gc);
		gc.gridx++;
		hueField = new NumericTextField(3, 0, 0, 360);
		hueField.addActionListener(hsbAction);
		p2.add(hueField, gc);

		gc.gridx = 0;
		gc.gridy++;
		p2.add(new JLabel("S:"), gc);
		gc.gridx++;
		satField = new NumericTextField(3, 0, 0, 100);
		satField.addActionListener(hsbAction);
		p2.add(satField, gc);

		gc.gridx = 0;
		gc.gridy++;
		p2.add(new JLabel("B:"), gc);
		gc.gridx++;
		briField = new NumericTextField(3, 0, 0, 100);
		briField.addActionListener(hsbAction);
		p2.add(briField, gc);

		gc.insets = new Insets(8, 0, 4, 2);
		gc.gridx = 0;
		gc.gridy++;
		p2.add(new JLabel("R:"), gc);
		gc.gridx++;
		redField = new NumericTextField(3, 0, 0, 255);
		redField.addActionListener(rgbAction);
		p2.add(redField, gc);

		gc.gridx = 0;
		gc.gridy++;
		gc.insets = new Insets(0, 0, 4, 2);
		p2.add(new JLabel("G:"), gc);
		gc.gridx++;
		greenField = new NumericTextField(3, 0, 0, 255);
		greenField.addActionListener(rgbAction);
		p2.add(greenField, gc);

		gc.gridx = 0;
		gc.gridy++;
		p2.add(new JLabel("B:"), gc);
		gc.gridx++;
		blueField = new NumericTextField(3, 0, 0, 255);
		blueField.addActionListener(rgbAction);
		p2.add(blueField, gc);

		return p2;
	}

	private void hueChanged(final int hue) {
		spinnerUpdate = true;
		final int sat = satField.getValue();
		final int bri = briField.getValue();

		hueField.setValue(hue);

		final Color c = Color.getHSBColor((hue / 360.0f), (sat / 100.0f),
				(bri / 100.0f));

		redField.setValue(c.getRed());
		greenField.setValue(c.getGreen());
		blueField.setValue(c.getBlue());

		twoColorField.setUpperColor(c);
		colorSelector.setColor(c);
		spinnerUpdate = false;
	}

	public void setColor(final Color c) {
		spinnerUpdate = true;
		inColor = c;
		outColor = inColor;
		final int hue = ColorRoutines.getHue(c);
		final int sat = ColorRoutines.getSaturation(c);
		final int bri = ColorRoutines.getBrightness(c);

		satField.setValue(sat);
		briField.setValue(bri);
		hueField.setValue(hue);
		redField.setValue(c.getRed());
		greenField.setValue(c.getGreen());
		blueField.setValue(c.getBlue());

		colorSelector.setColor(c);
		hueSelector.setHue(hue);
		twoColorField.setUpperColor(c);
		twoColorField.setLowerColor(c);
		spinnerUpdate = false;
	}

	private void setupUI(final Frame frame, final Color inColor) {
		JPanel p1 = new JPanel(new BorderLayout());
		final JPanel p2 = new JPanel(new BorderLayout());

		// ColorSelector
		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
		final int hue = ColorRoutines.getHue(inColor);
		colorSelector = new ColorSelector(inColor);
		p3.add(colorSelector);

		p1.add(p3, BorderLayout.WEST);

		// HueSelector
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
		hueSelector = new HueSelector(hue);
		p3.add(hueSelector);

		p1.add(p3, BorderLayout.CENTER);
		p2.add(p1, BorderLayout.CENTER);

		// TwoColorField
		p1 = new JPanel(new BorderLayout());

		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
		twoColorField = new TwoColorField(inColor);
		p3.add(twoColorField);
		p1.add(p3, BorderLayout.NORTH);

		// Spinners
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 7));
		p3.add(createNumericTextFields());

		p1.add(p3, BorderLayout.CENTER);

		p2.add(p1, BorderLayout.EAST);

		getContentPane().add(p2, BorderLayout.NORTH);

		// buttons
		p3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		p3.setBorder(new EtchedBorder());

		JButton b = new JButton("Cancel");
		b.setMnemonic(KeyEvent.VK_C);
		b.addActionListener(new CancelAction());
		p3.add(b);

		b = new JButton("OK");
		b.setMnemonic(KeyEvent.VK_O);
		b.addActionListener(new OKAction());
		getRootPane().setDefaultButton(b);
		p3.add(b);

		getContentPane().add(p3, BorderLayout.SOUTH);

		pack();

		getSize();
		setLocation(frame.getLocationOnScreen().x
				+ (frame.getWidth() - getSize().width) / 2,
				frame.getLocationOnScreen().y
				+ (frame.getHeight() - getSize().height) / 2);
	}
}
