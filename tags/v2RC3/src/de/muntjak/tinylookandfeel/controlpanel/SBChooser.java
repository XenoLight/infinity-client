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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * SBChooser
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class SBChooser extends JDialog {

	class ArrowKeyAction extends KeyAdapter implements ActionListener {

		private final JTextField theField;
		private final javax.swing.Timer keyTimer;
		private int step;
		private final int min, max;

		ArrowKeyAction(final JTextField field, final int min, final int max) {
			theField = field;
			this.min = min;
			this.max = max;
			keyTimer = new javax.swing.Timer(20, this);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			changeVal();
		}

		private void changeVal() {
			int val = Integer.parseInt(theField.getText()) + step;

			if (val > max)
				val = max;
			else if (val < min)
				val = min;

			theField.setText("" + val);
		}

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == 38) { // up => decrease
				step = 1;
				if (e.getModifiers() == InputEvent.SHIFT_MASK) {
					step = 10;
				}

				changeVal();
				keyTimer.setInitialDelay(300);
				keyTimer.start();
			} else if (e.getKeyCode() == 40) { // up => increase
				step = -1;
				if (e.getModifiers() == InputEvent.SHIFT_MASK) {
					step = -10;
				}

				changeVal();
				keyTimer.setInitialDelay(300);
				keyTimer.start();
			}
		}

		@Override
		public void keyReleased(final KeyEvent e) {
			keyTimer.stop();
		}
	}
	class BriInputListener implements DocumentListener {
		@Override
		public void changedUpdate(final DocumentEvent e) {
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			update(e);
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			update(e);
		}

		private void update(final DocumentEvent e) {
			final Document doc = e.getDocument();

			try {
				final String text = doc.getText(0, doc.getLength());

				try {
					final int val = Integer.parseInt(text);

					keyInput = true;
					briSlider.setValue(val);
					keyInput = false;
				} catch (final NumberFormatException ignore) {
				}
			} catch (final BadLocationException ignore) {
			}
		}
	}
	class CancelAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			outColor = null;
			setVisible(false);
		}
	}
	class ColorField extends JPanel {

		private final Dimension size = new Dimension(60, 38);

		ColorField(final Color c) {
			setBorder(new LineBorder(Color.GRAY, 1));
			setBackground(c);
		}

		@Override
		public Dimension getPreferredSize() {
			return size;
		}
	}
	class OKAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			setVisible(false);
		}
	}
	class SatInputListener implements DocumentListener {
		@Override
		public void changedUpdate(final DocumentEvent e) {
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			update(e);
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			update(e);
		}

		private void update(final DocumentEvent e) {
			final Document doc = e.getDocument();

			try {
				final String text = doc.getText(0, doc.getLength());

				try {
					final int val = Integer.parseInt(text);

					keyInput = true;
					satSlider.setValue(val);
					keyInput = false;
				} catch (final NumberFormatException ignore) {
				}
			} catch (final BadLocationException ignore) {
			}
		}
	}
	class SliderAction implements ChangeListener {

		@Override
		public void stateChanged(final ChangeEvent e) {
			if (!keyInput) {
				if (e.getSource().equals(satSlider)) {
					satField.setText("" + satSlider.getValue());
				} else {
					briField.setText("" + briSlider.getValue());
				}
			}

			if (valueIsAdjusting)
				return;

			showColor(satSlider.getValue(), briSlider.getValue());
		}
	}
	class TwoColorField extends JPanel {

		private final Dimension size = new Dimension(60, 68);
		private Color upperColor, lowerColor;

		TwoColorField(final Color c) {
			setBorder(new LineBorder(Color.BLACK, 1));

			upperColor = outColor;
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
			redField.setText("" + c.getRed());
			greenField.setText("" + c.getGreen());
			blueField.setText("" + c.getBlue());
			repaint(0);
		}
	}
	private static SBChooser myInstance;
	private static int sat, bri;

	public static void deleteInstance() {
		myInstance = null;
	}

	public static int getBrightness() {
		return bri;
	}

	public static int getSaturation() {
		return sat;
	}

	public static Color showSBChooser(final Frame frame, final Color ref, final Color inColor,
			final int s, final int b) {
		if (myInstance == null) {
			myInstance = new SBChooser(frame);
		}

		myInstance.setColor(ref, inColor, s, b);
		myInstance.setVisible(true);

		return myInstance.outColor;
	}

	public static Color showSBChooser(final Frame frame, final SBControl hsb) {
		if (myInstance == null) {
			myInstance = new SBChooser(frame);
		}

		myInstance.setColor(hsb);
		myInstance.setVisible(true);

		return myInstance.outColor;
	}

	private Color reference, outColor;

	private JSlider satSlider, briSlider;

	private JTextField satField, briField;

	private JTextField redField, greenField, blueField;

	private TwoColorField twoColorField;

	private ColorField referenceField;

	private boolean keyInput = false;

	private boolean valueIsAdjusting = false;

	public SBChooser(final Frame frame) {
		super(frame, "Saturation/Brightness", true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		setupUI(frame);
	}

	private void adjustColor() {
		outColor = ColorRoutines.getAdjustedColor(reference, sat, bri);

		twoColorField.setUpperColor(outColor);
	}

	public void setColor(final Color ref, final Color inColor, final int s, final int b) {
		reference = ref;
		outColor = inColor;
		sat = s;
		bri = b;

		valueIsAdjusting = true;
		satSlider.setValue(sat);
		briSlider.setValue(bri);
		valueIsAdjusting = false;

		referenceField.setBackground(reference);
		twoColorField.setLowerColor(inColor);
		adjustColor();
	}

	public void setColor(final SBControl hsb) {
		reference = hsb.getSBReference().getReferenceColor();
		outColor = hsb.getBackground();
		sat = hsb.getSBReference().getSaturation();
		bri = hsb.getSBReference().getBrightness();

		valueIsAdjusting = true;
		satSlider.setValue(sat);
		briSlider.setValue(bri);
		valueIsAdjusting = false;

		referenceField.setBackground(reference);
		twoColorField.setLowerColor(outColor);
		adjustColor();
	}

	private void setupUI(final Frame frame) {
		final ChangeListener sliderAction = new SliderAction();

		final JPanel p1 = new JPanel(new BorderLayout(12, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		JPanel p2 = new JPanel(new GridLayout(2, 1, 0, 8));
		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 4));
		JPanel p4 = new JPanel(new BorderLayout(4, 0));

		// sliders
		JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p4.add(new JLabel("Saturation"), BorderLayout.NORTH);
		satSlider = new JSlider(-100, 100, sat);
		satSlider.addChangeListener(sliderAction);
		satSlider.setMajorTickSpacing(100);
		satSlider.setPaintTicks(true);
		p4.add(satSlider, BorderLayout.CENTER);

		satField = new JTextField("" + satSlider.getValue(), 4);
		satField.getDocument().addDocumentListener(new SatInputListener());
		satField.addKeyListener(new ArrowKeyAction(satField, -100, 100));
		satField.setHorizontalAlignment(SwingConstants.CENTER);
		p5.add(satField);
		p4.add(p5, BorderLayout.EAST);

		p2.add(p4);

		p5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p4 = new JPanel(new BorderLayout(4, 0));
		p4.add(new JLabel("Brightness"), BorderLayout.NORTH);
		briSlider = new JSlider(-100, 100, bri);
		briSlider.addChangeListener(sliderAction);
		briSlider.setMajorTickSpacing(100);
		briSlider.setPaintTicks(true);
		p4.add(briSlider, BorderLayout.CENTER);

		briField = new JTextField("" + briSlider.getValue(), 4);
		briField.getDocument().addDocumentListener(new BriInputListener());
		briField.addKeyListener(new ArrowKeyAction(briField, -100, 100));
		briField.setHorizontalAlignment(SwingConstants.CENTER);
		p5.add(briField);
		p4.add(p5, BorderLayout.EAST);

		p2.add(p4);
		p3.add(p2);
		p1.add(p3, BorderLayout.CENTER);

		// color panel
		p2 = new JPanel(new BorderLayout(0, 6));
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		twoColorField = new TwoColorField(reference);
		p2.add(twoColorField, BorderLayout.NORTH);

		referenceField = new ColorField(reference);
		p2.add(referenceField, BorderLayout.CENTER);

		p3.add(p2);

		p1.add(p3, BorderLayout.EAST);

		// RGB fields
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 8));
		p3.add(new JLabel("R:"));
		redField = new JTextField(4);
		redField.setHorizontalAlignment(SwingConstants.CENTER);
		redField.setEditable(false);
		p3.add(redField);

		p3.add(new JLabel("  G:"));
		greenField = new JTextField(4);
		greenField.setHorizontalAlignment(SwingConstants.CENTER);
		greenField.setEditable(false);
		p3.add(greenField);

		p3.add(new JLabel("  B:"));
		blueField = new JTextField(4);
		blueField.setHorizontalAlignment(SwingConstants.CENTER);
		blueField.setEditable(false);
		p3.add(blueField);

		p1.add(p3, BorderLayout.SOUTH);

		getContentPane().add(p1, BorderLayout.CENTER);

		// buttons
		p3 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		p3.setBorder(new EtchedBorder());

		JButton b = new JButton("Cancel");
		b.setMnemonic(KeyEvent.VK_C);
		b.addActionListener(new CancelAction());
		p3.add(b);

		b = new JButton("OK");
		b.setMnemonic(KeyEvent.VK_O);
		getRootPane().setDefaultButton(b);
		b.addActionListener(new OKAction());
		p3.add(b);

		getContentPane().add(p3, BorderLayout.SOUTH);

		pack();

		getSize();
		setLocation(frame.getLocationOnScreen().x
				+ (frame.getWidth() - getSize().width) / 2,
				frame.getLocationOnScreen().y
				+ (frame.getHeight() - getSize().height) / 2);
	}

	private void showColor(final int s, final int b) {
		sat = s;
		bri = b;
		adjustColor();
	}
}
