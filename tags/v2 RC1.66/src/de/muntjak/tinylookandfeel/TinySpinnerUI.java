/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.text.InternationalFormatter;

/**
 * TinySpinnerUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinySpinnerUI extends BasicSpinnerUI {

	/**
	 * Copy and paste from BasicSpinnerUI - sigh !
	 * 
	 * 
	 * A handler for spinner arrow button mouse and action events. When a left
	 * mouse pressed event occurs we look up the (enabled) spinner that's the
	 * source of the event and start the autorepeat timer. The timer fires
	 * action events until any button is released at which point the timer is
	 * stopped and the reference to the spinner cleared. The timer doesn't start
	 * until after a 300ms delay, so often the source of the initial (and final)
	 * action event is just the button logic for mouse released - which means
	 * that we're relying on the fact that our mouse listener runs after the
	 * buttons mouse listener.
	 * <p>
	 * Note that one instance of this handler is shared by all slider previous
	 * arrow buttons and likewise for all of the next buttons, so it doesn't
	 * have any state that persists beyond the limits of a single button
	 * pressed/released gesture.
	 */
	private static class ArrowButtonHandler extends AbstractAction implements
	MouseListener {
		final javax.swing.Timer autoRepeatTimer;
		final boolean isNext;
		private transient boolean mouseInsideButton = false;
		private transient int actionCount;
		private transient int modCount;
		JSpinner spinner = null;

		ArrowButtonHandler(final String name, final boolean isNext) {
			super(name);
			this.isNext = isNext;
			autoRepeatTimer = new javax.swing.Timer(10, this);
			autoRepeatTimer.setInitialDelay(300);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			// Changed in 1.4.0: Values are changing in increasing speed
			if (modCount == 0 || actionCount % modCount == 0) {
				JSpinner spinner = this.spinner;

				if (!(e.getSource() instanceof javax.swing.Timer)) {
					// Most likely resulting from being in ActionMap.
					spinner = eventToSpinner(e);
				} else if (!mouseInsideButton) {
					// new in 1.4.0
					return;
				}

				if (spinner != null) {
					try {
						final int calendarField = getCalendarField(spinner);
						spinner.commitEdit();

						if (calendarField != -1) {
							((SpinnerDateModel) spinner.getModel())
							.setCalendarField(calendarField);
						}

						final Object value = isNext ? spinner.getNextValue()
								: spinner.getPreviousValue();
						if (value != null) {
							spinner.setValue(value);
							select(spinner);
						}
					} catch (final IllegalArgumentException iae) {
						UIManager.getLookAndFeel()
						.provideErrorFeedback(spinner);
					} catch (final ParseException pe) {
						UIManager.getLookAndFeel()
						.provideErrorFeedback(spinner);
					}
				}

				if (modCount > 0) {
					modCount--;
					actionCount = 0;
				}
			}

			actionCount++;
		}

		private JSpinner eventToSpinner(final AWTEvent e) {
			Object src = e.getSource();

			while ((src instanceof Component) && !(src instanceof JSpinner)) {
				src = ((Component) src).getParent();
			}
			return (src instanceof JSpinner) ? (JSpinner) src : null;
		}

		/**
		 * Requests focus on a child of the spinner if the spinner doesn't have
		 * focus.
		 */
		private void focusSpinnerIfNecessary() {
			final Component fo = KeyboardFocusManager
			.getCurrentKeyboardFocusManager().getFocusOwner();
			if (spinner.isRequestFocusEnabled()
					&& (fo == null || !SwingUtilities.isDescendingFrom(fo,
							spinner))) {
				Container root = spinner;

				if (!root.isFocusCycleRoot()) {
					root = root.getFocusCycleRootAncestor();
				}
				if (root != null) {
					final FocusTraversalPolicy ftp = root.getFocusTraversalPolicy();
					final Component child = ftp.getComponentAfter(root, spinner);

					if (child != null
							&& SwingUtilities.isDescendingFrom(child, spinner)) {
						child.requestFocus();
					}
				}
			}
		}

		/**
		 * Returns the calendarField under the start of the selection, or -1 if
		 * there is no valid calendar field under the selection (or the spinner
		 * isn't editing dates.
		 */
		private int getCalendarField(final JSpinner spinner) {
			final JComponent editor = spinner.getEditor();

			if (editor instanceof JSpinner.DateEditor) {
				final JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) editor;
				final JFormattedTextField ftf = dateEditor.getTextField();
				final int start = ftf.getSelectionStart();
				final JFormattedTextField.AbstractFormatter formatter = ftf
				.getFormatter();

				if (formatter instanceof InternationalFormatter) {
					final Format.Field[] fields = ((InternationalFormatter) formatter)
					.getFields(start);

					for (int counter = 0; counter < fields.length; counter++) {
						if (fields[counter] instanceof DateFormat.Field) {
							int calendarField;

							if (fields[counter] == DateFormat.Field.HOUR1) {
								calendarField = Calendar.HOUR;
							} else {
								calendarField = ((DateFormat.Field) fields[counter])
								.getCalendarField();
							}
							if (calendarField != -1) {
								return calendarField;
							}
						}
					}
				}
			}

			return -1;
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			mouseInsideButton = true;
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			mouseInsideButton = false;
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)
					&& e.getComponent().isEnabled()) {
				spinner = eventToSpinner(e);
				actionCount = 0;
				modCount = 20;
				autoRepeatTimer.start();

				focusSpinnerIfNecessary();
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			autoRepeatTimer.stop();
			spinner = null;
		}

		/**
		 * Selects the passed in field, returning true if it is found, false
		 * otherwise.
		 */
		private boolean select(final JFormattedTextField ftf,
				final AttributedCharacterIterator iterator, final DateFormat.Field field) {
			final int max = ftf.getDocument().getLength();

			iterator.first();
			do {
				final Map attrs = iterator.getAttributes();

				if (attrs != null && attrs.containsKey(field)) {
					final int start = iterator.getRunStart(field);
					final int end = iterator.getRunLimit(field);

					if (start != -1 && end != -1 && start <= max && end <= max) {
						ftf.select(start, end);
					}
					return true;
				}
			} while (iterator.next() != CharacterIterator.DONE);
			return false;
		}

		/**
		 * If the spinner's editor is a DateEditor, this selects the field
		 * associated with the value that is being incremented.
		 */
		private void select(final JSpinner spinner) {
			final JComponent editor = spinner.getEditor();

			if (editor instanceof JSpinner.DateEditor) {
				final JSpinner.DateEditor dateEditor = (JSpinner.DateEditor) editor;
				final JFormattedTextField ftf = dateEditor.getTextField();
				final Format format = dateEditor.getFormat();
				Object value;

				if (format != null && (value = spinner.getValue()) != null) {
					final SpinnerDateModel model = dateEditor.getModel();
					final DateFormat.Field field = DateFormat.Field
					.ofCalendarField(model.getCalendarField());

					if (field != null) {
						try {
							final AttributedCharacterIterator iterator = format
							.formatToCharacterIterator(value);
							if (!select(ftf, iterator, field)
									&& field == DateFormat.Field.HOUR0) {
								select(ftf, iterator, DateFormat.Field.HOUR1);
							}
						} catch (final IllegalArgumentException iae) {
						}
					}
				}
			}
		}
	}
	/**
	 * The mouse/action listeners that are added to the spinner's arrow buttons.
	 * These listeners are shared by all spinner arrow buttons.
	 * 
	 * @see #createNextButton
	 * @see #createPreviousButton
	 */
	private static final ArrowButtonHandler nextButtonHandler = new ArrowButtonHandler(
			"increment", true);

	private static final ArrowButtonHandler previousButtonHandler = new ArrowButtonHandler(
			"decrement", false);

	public static ComponentUI createUI(final JComponent c) {
		return new TinySpinnerUI();
	}

	@Override
	protected Component createNextButton() {
		final JButton b = new SpecialUIButton(new TinySpinnerButtonUI(
				SwingConstants.NORTH));

		b.putClientProperty("isSpinnerButton", Boolean.TRUE);
		b.putClientProperty("isNextButton", Boolean.TRUE);
		b.setFocusable(false);
		b.addActionListener(nextButtonHandler);
		b.addMouseListener(nextButtonHandler);

		return b;
	}

	@Override
	protected Component createPreviousButton() {
		final JButton b = new SpecialUIButton(new TinySpinnerButtonUI(
				SwingConstants.SOUTH));

		b.putClientProperty("isSpinnerButton", Boolean.TRUE);
		b.putClientProperty("isNextButton", Boolean.FALSE);
		b.setFocusable(false);
		b.addActionListener(previousButtonHandler);
		b.addMouseListener(previousButtonHandler);

		return b;
	}
}
