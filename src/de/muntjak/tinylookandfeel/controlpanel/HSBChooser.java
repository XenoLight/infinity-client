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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.util.DrawRoutines;

/**
 * ColorizeDialog
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class HSBChooser extends JDialog {

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
					bri = val;
					briSlider.setValue(val);
					keyInput = false;
					performAction();
				} catch (final NumberFormatException ignore) {
				}
			} catch (final BadLocationException ignore) {
			}
		}
	}

	class CancelAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			result = false;
			setVisible(false);
		}
	}
	class HueInputListener implements DocumentListener {
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
					hue = val;
					hueSlider.setValue(val);
					keyInput = false;
					performAction();
				} catch (final NumberFormatException ignore) {
				}
			} catch (final BadLocationException ignore) {
			}
		}
	}
	class IconPanel extends JPanel {

		class BorderLabel extends JLabel {

			private final Dimension size = new Dimension(48, 48);

			BorderLabel(final Icon icon) {
				super(icon);

				setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Color.DARK_GRAY),
						BorderFactory.createEmptyBorder(8, 8, 8, 8)));
				setOpaque(true);

				if (hsbControl != null) {
					if (hsbControl.controlsTreeIcon()) {
						setBackground(Theme.treeBgColor.getColor());
					} else if (hsbControl.controlsFrameIcon()) {
						setBackground(Theme.frameColor.getColor());
					} else {
						setBackground(Theme.backColor.getColor());
					}
				}
			}

			@Override
			public Dimension getPreferredSize() {
				return size;
			}
		}

		JLabel colorizedLabel;

		IconPanel() {
			super(new GridLayout(2, 1, 4, 8));

			setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
			add(new BorderLabel(null));
			add(new BorderLabel(null));
		}

		void installIcons() {
			removeAll();

			colorizedLabel = new BorderLabel(colorizedIcon);
			add(colorizedLabel);
			add(new BorderLabel(icon));
		}

		void updateColorizedIcon() {
			colorizedLabel.setIcon(colorizedIcon);
		}
	}
	class OKAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			result = true;
			setVisible(false);
		}
	}
	class PreserveAction implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			performAction();
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
					sat = val;
					satSlider.setValue(val);
					keyInput = false;
					performAction();
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
				if (e.getSource().equals(hueSlider)) {
					hue = hueSlider.getValue();
					hueField.setText("" + hue);
				} else if (e.getSource().equals(briSlider)) {
					bri = briSlider.getValue();
					briField.setText("" + bri);
				} else if (e.getSource().equals(satSlider)) {
					sat = satSlider.getValue();
					satField.setText("" + sat);
				}

				performAction();
			}
		}
	}
	private static HSBChooser myInstance;
	private static int hue, sat, bri;

	public static void deleteInstance() {
		myInstance = null;
	}

	public static boolean showDialog(final Frame frame,
			final ControlPanel.HSBControl control) {
		if (myInstance == null) {
			myInstance = new HSBChooser(frame);
		}

		myInstance.hsbControl = control;
		myInstance.result = false;
		myInstance.setParams(control);
		myInstance.setVisible(true);

		return myInstance.result;
	}

	private ImageIcon icon, colorizedIcon;

	private boolean result = false;

	private ControlPanel.HSBControl hsbControl;

	private IconPanel iconPanel;

	private JSlider hueSlider, satSlider, briSlider;

	private JTextField hueField, satField, briField;

	private JCheckBox preserveGrey;

	private boolean keyInput = false;

	private boolean valueIsAdjusting = false;

	private HSBChooser(final Frame frame) {
		super(frame, "Hue/Saturation/Brightness", true);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(16, 4));
		setupUI(frame);
	}

	private void performAction() {
		if (valueIsAdjusting)
			return;

		hsbControl.setHue(hue);
		hsbControl.setSaturation(sat);
		hsbControl.setBrightness(bri);
		hsbControl.setPreserveGrey(preserveGrey.isSelected());
		colorizedIcon = DrawRoutines.colorizeIcon(icon.getImage(),
				hsbControl.getHSBReference());
		iconPanel.updateColorizedIcon();
		// ActionEvent ae = new ActionEvent(hsbControl, Event.ACTION_EVENT, "");
		// hsbControl.actionPerformed(ae);

	}

	private void setParams(final ControlPanel.HSBControl control) {
		hue = control.getHue();
		sat = control.getSaturation();
		bri = control.getBrightness();

		icon = (ImageIcon) control.getUncolorizedIcon();
		colorizedIcon = DrawRoutines.colorizeIcon(icon.getImage(),
				control.getHSBReference());

		valueIsAdjusting = true;
		hueSlider.setValue(hue);
		satSlider.setValue(sat);
		briSlider.setValue(bri);
		preserveGrey.setSelected(control.isPreserveGrey());
		valueIsAdjusting = false;

		iconPanel.installIcons();
	}

	private void setupUI(final Frame frame) {
		final ChangeListener sliderAction = new SliderAction();

		final JPanel p1 = new JPanel(new BorderLayout(12, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		final JPanel p2 = new JPanel(new GridLayout(3, 1, 0, 4));
		JPanel p3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 4));
		JPanel p4 = new JPanel(new BorderLayout(4, 0));

		// sliders
		p4.add(new JLabel("Hue"), BorderLayout.NORTH);
		hueSlider = new JSlider(0, 360, hue);
		hueSlider.addChangeListener(sliderAction);
		hueSlider.setMajorTickSpacing(180);
		hueSlider.setPaintTicks(true);
		p4.add(hueSlider, BorderLayout.CENTER);

		JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		hueField = new JTextField("" + hueSlider.getValue(), 4);
		hueField.getDocument().addDocumentListener(new HueInputListener());
		hueField.addKeyListener(new ArrowKeyAction(hueField, 0, 360));
		hueField.setHorizontalAlignment(SwingConstants.CENTER);
		p5.add(hueField);
		p4.add(p5, BorderLayout.EAST);

		p2.add(p4);

		p5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p4 = new JPanel(new BorderLayout(4, 0));
		p4.add(new JLabel("Saturation"), BorderLayout.NORTH);
		satSlider = new JSlider(0, 100, sat);
		satSlider.addChangeListener(sliderAction);
		satSlider.setMajorTickSpacing(50);
		satSlider.setPaintTicks(true);
		p4.add(satSlider, BorderLayout.CENTER);

		satField = new JTextField("" + satSlider.getValue(), 4);
		satField.getDocument().addDocumentListener(new SatInputListener());
		satField.addKeyListener(new ArrowKeyAction(satField, 0, 100));
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

		p5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 6));
		preserveGrey = new JCheckBox("Preserve grey values", true);
		preserveGrey.addActionListener(new PreserveAction());
		p5.add(preserveGrey);

		p1.add(p5, BorderLayout.SOUTH);

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

		p5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
		iconPanel = new IconPanel();
		p5.add(iconPanel);
		getContentPane().add(p5, BorderLayout.EAST);

		pack();

		getSize();
		setLocation(frame.getLocationOnScreen().x
				+ (frame.getWidth() - getSize().width) / 2,
				frame.getLocationOnScreen().y
				+ (frame.getHeight() - getSize().height) / 2);
	}
}