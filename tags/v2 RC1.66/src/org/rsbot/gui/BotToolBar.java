package org.rsbot.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.lazygamerz.scripting.api.Environment;
import org.rsbot.gui.toolactions.Account;
import org.rsbot.gui.toolactions.Shot;
import org.rsbot.gui.toolactions.ShotUn;
import org.rsbot.gui.toolactions.StopScript;
import org.rsbot.util.GlobalConfiguration;

public class BotToolBar extends JToolBar {

	private static final long serialVersionUID = -1861866523519184211L;
	public static final int RUN_SCRIPT = 0;
	public static final int PAUSE_SCRIPT = 1;
	public static final int RESUME_SCRIPT = 2;
	private final JButton userInputButton;
	private final JButton runScriptButton;
	public static JButton stopScriptButton;
	public ActionListener listener;
	private int idx;
	private int inputState = Environment.inputKeyboard | Environment.inputMouse;
	private boolean inputOverride = true;
	final ImageIcon a = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/account.png");
	final ImageIcon b = new ImageIcon(
			GlobalConfiguration.Paths.getIconDirectory() + "/shot.png");

	public BotToolBar(final ActionListener listener) {
		this.listener = listener;

		userInputButton = new JButton("Input", new ImageIcon(getInputImage(
				inputOverride, inputState)));
		userInputButton.addActionListener(listener);
		userInputButton.setFocusable(false);

		runScriptButton = new JButton("Run", new ImageIcon(
				GlobalConfiguration.Paths.getIconDirectory() + "/play.png"));
		runScriptButton.addActionListener(listener);
		runScriptButton.setFocusable(false);

		stopScriptButton = getScriptButton(new StopScript(), "Stop",
				"Stop the script that is running", new ImageIcon(
						GlobalConfiguration.Paths.getIconDirectory()
						+ "/stop.png"));
		stopScriptButton.setFocusable(false);
		stopScriptButton.setEnabled(false);

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setFloatable(false);
		JButton accountsButton = new JButton();
		accountsButton = getDefaultButton(new Account(),
				"Show Account Management Window", a);
		add(accountsButton);
		add(Box.createHorizontalGlue());
		JButton shotButton = new JButton();
		shotButton = getDefaultButton(new Shot(),
				"Screenshot With Account Name Blocked", b);
		add(shotButton);
		JButton shotUnButton = new JButton();
		shotUnButton = getDefaultButton(new ShotUn(),
				"Screenshot With Account Name UnBlocked", b);
		add(shotUnButton);
		add(stopScriptButton);
		add(runScriptButton);
		add(userInputButton);

		updateSelection(false);
	}

	public void addTab() {
		final int idx = getComponentCount() - 6;
		validate();
		setSelection(idx);
	}

	public int getCurrentTab() {
		if (idx > -1 && idx < getComponentCount() - 5) {
			return idx;
		} else {
			return -1;
		}
	}

	private JButton getDefaultButton(final Action a, final String tip, final ImageIcon i) {
		final JButton button = new JButton(a);
		button.setToolTipText(tip);
		button.setIcon(i);
		button.setFocusable(false);
		button.setMargin(new Insets(1, 0, 1, 0));
		button.setPreferredSize(new Dimension(28, 24));
		button.setMaximumSize(new Dimension(28, 24));
		button.setBorder(new EmptyBorder(3, 3, 3, 3));

		return button;
	}

	private Image getInputImage(final boolean override, final int state) {
		if (override
				|| state == (Environment.inputKeyboard | Environment.inputMouse)) {
			final File icon = new File(GlobalConfiguration.Paths.getIconDirectory()
					+ "/tick.png");
			return GlobalConfiguration.getImageFile(icon);
		} else if (state == Environment.inputKeyboard) {
			final File icon = new File(GlobalConfiguration.Paths.getIconDirectory()
					+ "/keyboard.png");
			return GlobalConfiguration.getImageFile(icon);
		} else if (state == Environment.inputKeyboard) {
			final File icon = new File(GlobalConfiguration.Paths.getIconDirectory()
					+ "/mouse.png");
			return GlobalConfiguration.getImageFile(icon);
		} else {
			final File icon = new File(GlobalConfiguration.Paths.getIconDirectory()
					+ "/delete.png");
			return GlobalConfiguration.getImageFile(icon);
		}
	}

	public int getScriptButton() {
		final String label = runScriptButton.getText();
		if (label.equals("Run")) {
			return RUN_SCRIPT;
		} else if (label.equals("Pause")) {
			return PAUSE_SCRIPT;
		} else if (label.equals("Resume")) {
			return RESUME_SCRIPT;
		} else {
			throw new IllegalStateException("Illegal script button state!");
		}
	}

	private JButton getScriptButton(final Action a, final String text, final String tip,
			final ImageIcon i) {
		final JButton button = new JButton(a);
		button.setText(text);
		button.setToolTipText(tip);
		button.setIcon(i);
		button.setFocusable(false);
		button.setMargin(new Insets(1, 0, 1, 0));
		button.setPreferredSize(new Dimension(60, 24));
		button.setMaximumSize(new Dimension(60, 24));
		button.setBorder(new EmptyBorder(0, 0, 0, 0));

		return button;
	}

	public void removeTab(final int idx) {
		remove(idx);
		revalidate();
		repaint();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				setSelection(0);
			}
		});
	}

	public void setHome(final boolean home) {
		userInputButton.setEnabled(!home);
		runScriptButton.setEnabled(!home);
	}

	public void setInputState(final int state) {
		inputState = state;
	}

	public void setOverrideInput(final boolean selected) {
		inputOverride = selected;
	}

	public void setScriptButton(final int state) {
		String text, pathResource;

		if (state == RUN_SCRIPT) {
			text = "Run";
			pathResource = GlobalConfiguration.Paths.getIconDirectory()
			+ "/play.png";
		} else if (state == PAUSE_SCRIPT) {
			text = "Pause";
			pathResource = GlobalConfiguration.Paths.getIconDirectory()
			+ "/pause.png";
		} else if (state == RESUME_SCRIPT) {
			text = "Resume";
			pathResource = GlobalConfiguration.Paths.getIconDirectory()
			+ "/play.png";
		} else {
			throw new IllegalArgumentException("Illegal button state: " + state
					+ "!");
		}

		runScriptButton.setText(text);
		runScriptButton.setIcon(new ImageIcon(pathResource));
		revalidate();
	}

	private void setSelection(final int idx) {
		updateSelection(true);
		this.idx = idx;
		updateSelection(false);
		listener.actionPerformed(new ActionEvent(this, 0, "Tab"));
	}

	public void updateInputButton() {
		userInputButton.setIcon(new ImageIcon(getInputImage(inputOverride,
				inputState)));
	}

	private void updateSelection(final boolean enabled) {
		final int idx = getCurrentTab();
		if (idx >= 0) {
			getComponent(idx).setEnabled(enabled);
			getComponent(idx).repaint();
		}
	}
}
