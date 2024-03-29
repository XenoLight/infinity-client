/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.metal.MetalSliderUI;

import de.muntjak.tinylookandfeel.util.ColorRoutines;

/**
 * TinySliderUI
 * 
 * @version 1.0
 * @author Hans Bickel
 */
public class TinySliderUI extends MetalSliderUI {

	/**
	 * This TrackListener extends the BasicSliderUI.TrackListener such that
	 * rollover and dragging state can be tracked.
	 */
	class MyTrackListener extends BasicSliderUI.TrackListener {
		@Override
		public void mouseDragged(final MouseEvent e) {
			if (thumbRect.contains(e.getX(), e.getY())) {
				isRollover = true;
			}

			super.mouseDragged(e);
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			isRollover = false;
			wasRollover = false;
			if (thumbRect.contains(e.getX(), e.getY())) {
				isRollover = true;
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			isRollover = false;

			if (isRollover != wasRollover) {
				slider.repaint();
				wasRollover = isRollover;
			}
		}

		@Override
		public void mouseMoved(final MouseEvent e) {
			if (thumbRect.contains(e.getX(), e.getY())) {
				isRollover = true;

				if (isRollover != wasRollover) {
					slider.repaint();
					wasRollover = isRollover;
				}
			} else {
				isRollover = false;

				if (isRollover != wasRollover) {
					slider.repaint();
					wasRollover = isRollover;
				}
			}
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			super.mousePressed(e);
			if (thumbRect.contains(e.getX(), e.getY())) {
				isDragging = true;
			}
			slider.repaint();
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			super.mouseReleased(e);
			isDragging = false;
			slider.repaint();
		}
	}

	/* the only instance of the stroke for the focus */
	private static final BasicStroke focusStroke = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[] {
			1.0f, 1.0f }, 0.0f);
	private static final Dimension sliderVertSize = new Dimension(22, 11);

	private static final Dimension sliderHorzSize = new Dimension(11, 22);
	public static ComponentUI createUI(final JComponent c) {
		return new TinySliderUI();
	}

	protected boolean isRollover = false, wasRollover = false;

	protected boolean isDragging = false;

	protected TrackListener trackListener;

	@Override
	protected TrackListener createTrackListener(final JSlider slider) {
		return new MyTrackListener();
	}

	private void drawXpThumb(final Graphics g) {
		final int x1 = thumbRect.x;
		final int y1 = thumbRect.y;
		final int x2 = x1 + thumbRect.width - 1;
		final int y2 = y1 + thumbRect.height - 1;
		final Color c = g.getColor(); // background

		if (slider.getPaintTicks()) {
			// draw arrow-like thumb
			if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
				g.fillRect(x1 + 1, y1 + 4, thumbRect.width - 4,
						thumbRect.height - 8);
				g.drawLine(x1 + 5, y2 - 3, x1 + 5, y2 - 3);

				g.setColor(ColorRoutines.darken(c, 10));
				g.drawLine(x2 - 2, y1 + 4, x2 - 2, y2 - 6);
				g.setColor(ColorRoutines.darken(c, 20));
				g.drawLine(x2 - 1, y1 + 4, x2 - 1, y2 - 7);

				final Color bc = Theme.sliderBorderColor.getColor();
				g.setColor(bc);
				g.drawLine(x1 + 1, y1, x2 - 1, y1);
				g.drawLine(x1, y1 + 1, x1, y2 - 5);
				g.drawLine(x1 + 1, y2 - 4, x1 + 1, y2 - 4);
				g.drawLine(x1 + 2, y2 - 3, x1 + 2, y2 - 3);
				g.drawLine(x1 + 3, y2 - 2, x1 + 3, y2 - 2);
				g.drawLine(x1 + 4, y2 - 1, x1 + 4, y2 - 1);

				g.setColor(Theme.sliderDarkColor.getColor());
				g.drawLine(x2, y1 + 1, x2, y2 - 5);
				g.drawLine(x2 - 1, y2 - 4, x2 - 1, y2 - 4);
				g.drawLine(x2 - 2, y2 - 3, x2 - 2, y2 - 3);
				g.drawLine(x2 - 3, y2 - 2, x2 - 3, y2 - 2);
				g.drawLine(x2 - 4, y2 - 1, x2 - 4, y2 - 1);
				g.drawLine(x2 - 5, y2, x2 - 5, y2);

				Color col = null;
				if (!isDragging && isRollover
						&& Theme.sliderRolloverEnabled.getValue()) {
					col = Theme.sliderThumbRolloverColor.getColor();
				} else {
					col = Theme.sliderLightColor.getColor();
				}

				final Color c2 = ColorRoutines.getAdjustedColor(col, 67, 39);
				g.setColor(c2);
				g.drawLine(x1 + 1, y1 + 1, x2 - 1, y1 + 1);
				g.drawLine(x1 + 1, y2 - 6, x1 + 1, y2 - 6);
				g.drawLine(x1 + 2, y2 - 5, x1 + 2, y2 - 5);
				g.drawLine(x1 + 3, y2 - 4, x1 + 3, y2 - 4);

				g.setColor(ColorRoutines.getAverage(bc, c2));
				g.drawLine(x1 + 1, y2 - 5, x1 + 1, y2 - 5);
				g.drawLine(x1 + 2, y2 - 4, x1 + 2, y2 - 4);
				g.drawLine(x1 + 3, y2 - 3, x1 + 3, y2 - 3);

				final Color c3 = ColorRoutines.getAverage(col, c2);
				g.setColor(c3);
				g.drawLine(x1 + 1, y1 + 2, x2 - 1, y1 + 2);
				g.drawLine(x1 + 4, y2 - 3, x1 + 4, y2 - 3);
				g.drawLine(x1 + 5, y2 - 2, x1 + 5, y2 - 2);
				g.drawLine(x1 + 6, y2 - 3, x1 + 6, y2 - 3);

				g.setColor(ColorRoutines.getAverage(bc, c3));
				g.drawLine(x1 + 4, y2 - 2, x1 + 4, y2 - 2);
				g.drawLine(x1 + 5, y2 - 1, x1 + 5, y2 - 1);
				g.drawLine(x1 + 6, y2 - 2, x1 + 6, y2 - 2);

				g.setColor(col);
				g.drawLine(x1 + 1, y1 + 3, x2 - 1, y1 + 3);
				g.drawLine(x1 + 9, y2 - 6, x1 + 9, y2 - 6);
				g.drawLine(x1 + 8, y2 - 5, x1 + 8, y2 - 5);
				g.drawLine(x1 + 7, y2 - 4, x1 + 7, y2 - 4);

				g.setColor(ColorRoutines.getAverage(bc, col));
				g.drawLine(x1 + 9, y2 - 5, x1 + 9, y2 - 5);
				g.drawLine(x1 + 8, y2 - 4, x1 + 8, y2 - 4);
				g.drawLine(x1 + 7, y2 - 3, x1 + 7, y2 - 3);
			} else { // VERTICAL
				g.fillRect(x1 + 4, y1 + 1, thumbRect.width - 8,
						thumbRect.height - 4);
				g.drawLine(x2 - 3, y1 + 5, x2 - 3, y1 + 5);

				g.setColor(ColorRoutines.darken(c, 10));
				g.drawLine(x1 + 4, y2 - 2, x2 - 6, y2 - 2);
				g.setColor(ColorRoutines.darken(c, 20));
				g.drawLine(x1 + 4, y2 - 1, x2 - 7, y2 - 1);

				final Color bc = Theme.sliderBorderColor.getColor();
				g.setColor(bc);
				g.drawLine(x1, y1 + 1, x1, y2 - 1);
				g.drawLine(x1 + 1, y1, x2 - 5, y1);
				g.drawLine(x2 - 4, y1 + 1, x2 - 4, y1 + 1);
				g.drawLine(x2 - 3, y1 + 2, x2 - 3, y1 + 2);
				g.drawLine(x2 - 2, y1 + 3, x2 - 2, y1 + 3);
				g.drawLine(x2 - 1, y1 + 4, x2 - 1, y1 + 4);

				g.setColor(Theme.sliderDarkColor.getColor());
				g.drawLine(x1 + 1, y2, x2 - 5, y2);
				g.drawLine(x2 - 4, y2 - 1, x2 - 4, y2 - 1);
				g.drawLine(x2 - 3, y2 - 2, x2 - 3, y2 - 2);
				g.drawLine(x2 - 2, y2 - 3, x2 - 2, y2 - 3);
				g.drawLine(x2 - 1, y2 - 4, x2 - 1, y2 - 4);
				g.drawLine(x2, y2 - 5, x2, y2 - 5);

				Color col = null;
				if (!isDragging && isRollover
						&& Theme.sliderRolloverEnabled.getValue()) {
					col = Theme.sliderThumbRolloverColor.getColor();
				} else {
					col = Theme.sliderLightColor.getColor();
				}

				final Color c2 = ColorRoutines.getAdjustedColor(col, 67, 39);
				g.setColor(c2);
				g.drawLine(x1 + 1, y1 + 1, x1 + 1, y2 - 1);
				g.drawLine(x2 - 6, y1 + 1, x2 - 6, y1 + 1);
				g.drawLine(x2 - 5, y1 + 2, x2 - 5, y1 + 2);
				g.drawLine(x2 - 4, y1 + 3, x2 - 4, y1 + 3);

				g.setColor(ColorRoutines.getAverage(bc, c2));
				g.drawLine(x2 - 5, y1 + 1, x2 - 5, y1 + 1);
				g.drawLine(x2 - 4, y1 + 2, x2 - 4, y1 + 2);
				g.drawLine(x2 - 3, y1 + 3, x2 - 3, y1 + 3);

				final Color c3 = ColorRoutines.getAverage(col, c2);
				g.setColor(c3);
				g.drawLine(x1 + 2, y1 + 1, x1 + 2, y2 - 1);
				g.drawLine(x2 - 3, y1 + 4, x2 - 3, y1 + 4);
				g.drawLine(x2 - 2, y1 + 5, x2 - 2, y1 + 5);
				g.drawLine(x2 - 3, y1 + 6, x2 - 3, y1 + 6);

				g.setColor(ColorRoutines.getAverage(bc, c3));
				g.drawLine(x2 - 2, y1 + 4, x2 - 2, y1 + 4);
				g.drawLine(x2 - 1, y1 + 5, x2 - 1, y1 + 5);
				g.drawLine(x2 - 2, y1 + 6, x2 - 2, y1 + 6);

				g.setColor(col);
				g.drawLine(x1 + 3, y1 + 1, x1 + 3, y2 - 1);
				g.drawLine(x2 - 6, y1 + 9, x2 - 6, y1 + 9);
				g.drawLine(x2 - 5, y1 + 8, x2 - 5, y1 + 8);
				g.drawLine(x2 - 4, y1 + 7, x2 - 4, y1 + 7);

				g.setColor(ColorRoutines.getAverage(bc, col));
				g.drawLine(x2 - 5, y1 + 9, x2 - 5, y1 + 9);
				g.drawLine(x2 - 4, y1 + 8, x2 - 4, y1 + 8);
				g.drawLine(x2 - 3, y1 + 7, x2 - 3, y1 + 7);
			}
		} else { // no ticks painted
			// draw rectangular thumb
			if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
				g.fillRect(x1 + 1, y1 + 1, thumbRect.width - 4,
						thumbRect.height - 4);

				g.setColor(ColorRoutines.darken(c, 10));
				g.drawLine(x2 - 2, y1 + 3, x2 - 2, y2 - 3);

				g.setColor(ColorRoutines.darken(c, 20));
				g.drawLine(x2 - 1, y1 + 3, x2 - 1, y2 - 3);

				g.setColor(Theme.sliderBorderColor.getColor());
				g.drawLine(x1, y1 + 1, x1, y2 - 1);
				g.drawLine(x1 + 1, y1, x2 - 1, y1);

				g.setColor(Theme.sliderDarkColor.getColor());
				g.drawLine(x1 + 1, y2, x2 - 1, y2);
				g.drawLine(x2, y1 + 1, x2, y2 - 1);

				Color col = null;
				if (!isDragging && isRollover
						&& Theme.sliderRolloverEnabled.getValue()) {
					col = Theme.sliderThumbRolloverColor.getColor();
				} else {
					col = Theme.sliderLightColor.getColor();
				}

				final Color c2 = ColorRoutines.getAdjustedColor(col, 67, 39);
				g.setColor(c2);
				g.drawLine(x1 + 1, y1 + 1, x2 - 1, y1 + 1);

				g.setColor(ColorRoutines.getAverage(col, c2));
				g.drawLine(x1 + 1, y1 + 2, x2 - 1, y1 + 2);
				g.drawLine(x1 + 1, y2 - 2, x2 - 1, y2 - 2);

				g.setColor(col);
				g.drawLine(x1 + 1, y2 - 1, x2 - 1, y2 - 1);
			} else { // VERTICAL
				g.fillRect(x1 + 1, y1 + 1, thumbRect.width - 4,
						thumbRect.height - 4);

				g.setColor(ColorRoutines.darken(c, 10));
				g.drawLine(x1 + 3, y2 - 2, x2 - 3, y2 - 2);
				g.setColor(ColorRoutines.darken(c, 20));
				g.drawLine(x1 + 3, y2 - 1, x2 - 3, y2 - 1);

				g.setColor(Theme.sliderBorderColor.getColor());
				g.drawLine(x1 + 1, y1, x2 - 1, y1);
				g.drawLine(x1, y1 + 1, x1, y2 - 1);

				g.setColor(Theme.sliderDarkColor.getColor());
				g.drawLine(x2, y1 + 1, x2, y2 - 1);
				g.drawLine(x1 + 1, y2, x2 - 1, y2);

				Color col = null;
				if (!isDragging && isRollover
						&& Theme.sliderRolloverEnabled.getValue()) {
					col = Theme.sliderThumbRolloverColor.getColor();
				} else {
					col = Theme.sliderLightColor.getColor();
				}

				final Color c2 = ColorRoutines.getAdjustedColor(col, 67, 39);
				g.setColor(c2);
				g.drawLine(x1 + 1, y1 + 1, x1 + 1, y2 - 1);

				g.setColor(ColorRoutines.getAverage(col, c2));
				g.drawLine(x1 + 2, y1 + 1, x1 + 2, y2 - 1);
				g.drawLine(x2 - 2, y1 + 1, x2 - 2, y2 - 1);

				g.setColor(col);
				g.drawLine(x2 - 1, y1 + 1, x2 - 1, y2 - 1);
			}
		}
	}

	private void drawXpThumbDisabled(final Graphics g) {
		final int x1 = thumbRect.x;
		final int y1 = thumbRect.y;
		final int x2 = x1 + thumbRect.width - 1;
		final int y2 = y1 + thumbRect.height - 1;
		final Color c = g.getColor(); // background
		final Color c1 = ColorRoutines.getAdjustedColor(c, 0, -3);
		final Color c2 = ColorRoutines.getAdjustedColor(c, 0, -7);

		final Color bc = Theme.sliderBorderDisabledColor.getColor(); // 214/212/198
		final Color bc1 = ColorRoutines.getAdjustedColor(bc, -19, -1); // 210/208/198
		final Color bc2 = ColorRoutines.getAdjustedColor(bc, 0, -3); // 206/204/192
		final Color bc3 = ColorRoutines.getAdjustedColor(bc, 0, -10); // 194/192/179
		final Color bc4 = ColorRoutines.getAdjustedColor(bc, 3, -12); // 191/188/173
		final Color bc5 = ColorRoutines.getAdjustedColor(bc, 0, -13); // 186/184/172

		if (slider.getPaintTicks()) {
			// draw arrow-like thumb
			if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
				g.fillRect(x1 + 1, y1 + 3, 7, 13);
				g.drawLine(x1 + 2, y2 - 5, x1 + 6, y2 - 5);
				g.drawLine(x1 + 3, y2 - 4, x1 + 5, y2 - 4);
				g.drawLine(x1 + 4, y2 - 3, x1 + 4, y2 - 3);

				g.setColor(c1);
				g.drawLine(x2 - 2, y1 + 3, x2 - 2, y2 - 3);
				g.drawLine(x2 - 3, y2 - 5, x2 - 3, y2 - 5);
				g.drawLine(x2 - 4, y2 - 4, x2 - 4, y2 - 4);
				g.drawLine(x2 - 5, y2 - 3, x2 - 5, y2 - 3);
				g.drawLine(x2 - 5, y2 - 2, x2 - 5, y2 - 2);

				g.setColor(c2);
				g.drawLine(x2 - 1, y1 + 3, x2 - 1, y2 - 6);
				g.drawLine(x2 - 2, y2 - 5, x2 - 2, y2 - 5);
				g.drawLine(x2 - 3, y2 - 4, x2 - 3, y2 - 4);
				g.drawLine(x2 - 4, y2 - 3, x2 - 4, y2 - 3);

				g.setColor(bc);
				g.drawLine(x1 + 1, y1, x2 - 1, y1);
				g.drawLine(x1, y1 + 1, x1, y2 - 5);

				g.setColor(bc1);
				g.drawLine(x1 + 1, y1 + 1, x2, y1 + 1);

				g.setColor(bc2);
				g.drawLine(x1 + 1, y1 + 2, x2, y1 + 2);
				g.drawLine(x1 + 1, y2 - 4, x1 + 1, y2 - 4);
				g.drawLine(x1 + 2, y2 - 3, x1 + 2, y2 - 3);
				g.drawLine(x1 + 3, y2 - 2, x1 + 3, y2 - 2);
				g.drawLine(x1 + 4, y2 - 1, x1 + 4, y2 - 1);

				g.setColor(bc3);
				g.drawLine(x1 + 1, y2 - 5, x1 + 1, y2 - 5);
				g.drawLine(x1 + 2, y2 - 4, x1 + 2, y2 - 4);
				g.drawLine(x1 + 3, y2 - 3, x1 + 3, y2 - 3);
				g.drawLine(x1 + 4, y2 - 2, x1 + 4, y2 - 2);
				g.drawLine(x1 + 5, y2 - 1, x1 + 5, y2);

				g.setColor(bc4);
				g.drawLine(x2, y1 + 3, x2, y2 - 5);
				g.drawLine(x2 - 1, y2 - 4, x2 - 1, y2 - 4);
				g.drawLine(x2 - 2, y2 - 3, x2 - 2, y2 - 3);
				g.drawLine(x2 - 3, y2 - 2, x2 - 3, y2 - 2);
				g.drawLine(x2 - 4, y2 - 1, x2 - 4, y2 - 1);
				g.drawLine(x2 - 5, y2, x2 - 5, y2);

				g.setColor(bc5);
				g.drawLine(x2 - 1, y2 - 5, x2 - 1, y2 - 5);
				g.drawLine(x2 - 2, y2 - 4, x2 - 2, y2 - 4);
				g.drawLine(x2 - 3, y2 - 3, x2 - 3, y2 - 3);
				g.drawLine(x2 - 4, y2 - 2, x2 - 4, y2 - 2);
			} else { // VERTICAL
				g.fillRect(x1 + 3, y1 + 1, 13, 7);
				g.drawLine(x2 - 5, y1 + 2, x2 - 5, y1 + 6);
				g.drawLine(x2 - 4, y1 + 3, x2 - 4, y1 + 5);
				g.drawLine(x2 - 3, y1 + 4, x2 - 3, y1 + 4);

				g.setColor(c1);
				g.drawLine(x1 + 3, y2 - 2, x2 - 6, y2 - 2);
				g.drawLine(x2 - 5, y2 - 3, x2 - 5, y2 - 3);
				g.drawLine(x2 - 4, y2 - 4, x2 - 4, y2 - 4);
				g.drawLine(x2 - 3, y2 - 5, x2 - 3, y2 - 5);

				g.setColor(c2);
				g.drawLine(x1 + 3, y2 - 1, x2 - 6, y2 - 1);
				g.drawLine(x2 - 5, y2 - 2, x2 - 5, y2 - 2);
				g.drawLine(x2 - 4, y2 - 3, x2 - 4, y2 - 3);
				g.drawLine(x2 - 3, y2 - 4, x2 - 3, y2 - 4);
				g.drawLine(x2 - 2, y2 - 5, x2 - 2, y2 - 5);

				g.setColor(bc);
				g.drawLine(x1, y1 + 1, x1, y2 - 1);
				g.drawLine(x1 + 1, y1, x2 - 5, y1);

				g.setColor(bc1);
				g.drawLine(x1 + 1, y1, x1 + 1, y2);

				g.setColor(bc2);
				g.drawLine(x1 + 2, y1, x1 + 2, y2);
				g.drawLine(x2 - 5, y1 + 1, x2 - 4, y1 + 1);
				g.drawLine(x2 - 4, y1 + 2, x2 - 3, y1 + 2);
				g.drawLine(x2 - 3, y1 + 3, x2 - 2, y1 + 3);
				g.drawLine(x2 - 2, y1 + 4, x2 - 1, y1 + 4);

				g.setColor(bc3);
				g.drawLine(x2 - 1, y1 + 5, x2, y1 + 5);

				g.setColor(bc4);
				g.drawLine(x1 + 3, y2, x2 - 5, y2);

				g.setColor(bc5);
				g.drawLine(x2 - 5, y2 - 1, x2 - 4, y2 - 1);
				g.drawLine(x2 - 4, y2 - 2, x2 - 3, y2 - 2);
				g.drawLine(x2 - 3, y2 - 3, x2 - 2, y2 - 3);
				g.drawLine(x2 - 2, y2 - 4, x2 - 1, y2 - 4);
			}
		} else { // no ticks painted
			// draw rectangular thumb
			if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
				g.fillRect(x1 + 1, y1 + 3, 7, 16);

				g.setColor(c1);
				g.drawLine(x2 - 2, y1 + 3, x2 - 2, y2 - 3);

				g.setColor(c2);
				g.drawLine(x2 - 1, y1 + 3, x2 - 1, y2 - 3);

				g.setColor(bc);
				g.drawLine(x1, y1 + 1, x1, y2 - 3);
				g.drawLine(x1 + 1, y1, x2 - 1, y1);

				g.setColor(bc1);
				g.drawLine(x1, y1 + 1, x2, y1 + 1);

				g.setColor(bc2);
				g.drawLine(x1, y1 + 2, x2, y1 + 2);

				g.setColor(bc3);
				g.drawLine(x1, y2 - 2, x2, y2 - 2);

				g.setColor(bc5);
				g.drawLine(x1, y2 - 1, x2, y2 - 1);

				g.setColor(bc4);
				g.drawLine(x1 + 1, y2, x2 - 1, y2);
				g.drawLine(x2, y1 + 3, x2, y2 - 3);
			} else { // VERTICAL
				g.fillRect(x1 + 3, y1 + 1, thumbRect.width - 6,
						thumbRect.height - 4);

				g.setColor(c1);
				g.drawLine(x1 + 3, y2 - 2, x2 - 3, y2 - 2);

				g.setColor(c2);
				g.drawLine(x1 + 3, y2 - 1, x2 - 3, y2 - 1);

				g.setColor(bc);
				g.drawLine(x1, y1 + 1, x1, y2 - 1);
				g.drawLine(x1 + 1, y1, x2 - 1, y1);

				g.setColor(bc1);
				g.drawLine(x1 + 1, y1, x1 + 1, y2);

				g.setColor(bc2);
				g.drawLine(x1 + 2, y1, x1 + 2, y2);

				g.setColor(bc3);
				g.drawLine(x2 - 2, y1, x2 - 2, y2);

				g.setColor(bc5);
				g.drawLine(x2 - 1, y1, x2 - 1, y2);

				g.setColor(bc4);
				g.drawLine(x2, y1 + 1, x2, y2 - 1);
				g.drawLine(x1 + 3, y2, x2 - 3, y2);
			}
		}
	}

	private void drawXpTrack(final Graphics g) {
		final int x1 = trackRect.x;
		final int x2 = x1 + trackRect.width;
		final int y1 = trackRect.y;
		final int y2 = y1 + trackRect.height;

		// Draw the track
		if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
			final int y = y1 + (trackRect.height - 4) / 2;

			g.setColor(Theme.sliderTrackColor.getColor());
			g.drawLine(x1 + 1, y + 2, x2 - 2, y + 2);

			g.setColor(Theme.sliderTrackDarkColor.getColor());
			g.drawLine(x1 + 1, y + 1, x2 - 2, y + 1);
			g.drawLine(x2 - 1, y + 1, x2 - 1, y + 2);
			g.setColor(ColorRoutines.darken(
					Theme.sliderTrackDarkColor.getColor(), 10));
			g.drawLine(x1, y, x1, y);
			g.drawLine(x1, y + 3, x1, y + 3);

			g.setColor(Theme.sliderTrackLightColor.getColor());
			g.drawLine(x1 + 1, y + 3, x2 - 1, y + 3);
			g.drawLine(x2, y + 1, x2, y + 3);

			g.setColor(Theme.sliderTrackBorderColor.getColor());
			g.drawLine(x1 + 1, y, x2, y);
			g.setColor(ColorRoutines.lighten(
					Theme.sliderTrackBorderColor.getColor(), 20));
			g.drawLine(x1, y + 1, x1, y + 2);
		} else {
			final int x = x1 + (trackRect.width - 4) / 2;

			g.setColor(Theme.sliderTrackBorderColor.getColor());
			g.drawLine(x, y1 + 1, x, y2 - 1);
			g.setColor(ColorRoutines.lighten(
					Theme.sliderTrackBorderColor.getColor(), 20));
			g.drawLine(x + 1, y1, x + 1, y1);
			g.drawLine(x + 1, y2, x + 1, y2);

			g.setColor(Theme.sliderTrackDarkColor.getColor());
			g.drawLine(x + 1, y1 + 1, x + 1, y2 - 1);
			g.setColor(ColorRoutines.darken(
					Theme.sliderTrackDarkColor.getColor(), 10));
			g.drawLine(x, y1, x, y1);
			g.drawLine(x, y2, x, y2);

			g.setColor(Theme.sliderTrackLightColor.getColor());
			g.drawLine(x + 3, y1, x + 3, y2);
			g.drawLine(x + 2, y1, x + 2, y1);
			g.drawLine(x + 2, y2, x + 2, y2);

			g.setColor(Theme.sliderTrackColor.getColor());
			g.drawLine(x + 2, y1 + 1, x + 2, y2 - 1);
		}
	}

	@Override
	protected int getThumbOverhang() {
		if (slider.getOrientation() == SwingConstants.VERTICAL) {
			return (int) (getThumbSize().getWidth() - getTrackWidth()) / 2;
		} else {
			return (int) (getThumbSize().getHeight() - getTrackWidth()) / 2;
		}
	}

	@Override
	protected Dimension getThumbSize() {
		if (slider.getOrientation() == SwingConstants.VERTICAL) {
			return sliderVertSize;
		} else {
			return sliderHorzSize;
		}
	}

	/**
	 * Returns the shorter dimension of the track.
	 */
	@Override
	protected int getTrackWidth() {
		return 4;
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);

		c.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	}

	@Override
	public void paintFocus(final Graphics g) {
		if (!Theme.sliderFocusEnabled.getValue())
			return;
		if (!(g instanceof Graphics2D))
			return;

		final Graphics2D g2d = (Graphics2D) g;
		final Stroke memStroke = g2d.getStroke();

		g2d.setStroke(focusStroke);
		g2d.setColor(Theme.sliderFocusColor.getColor());
		g2d.drawRect(0, 0, slider.getWidth() - 1, slider.getHeight() - 1);

		g2d.setStroke(memStroke);
	}

	@Override
	protected void paintMajorTickForHorizSlider(final Graphics g,
			final Rectangle tickBounds, final int x) {
		g.setColor(slider.isEnabled() ? Theme.sliderTickColor.getColor()
				: Theme.sliderTickDisabledColor.getColor());
		g.drawLine(x, 0, x, tickBounds.height - 2);
	}

	@Override
	protected void paintMajorTickForVertSlider(final Graphics g,
			final Rectangle tickBounds, final int y) {
		g.setColor(slider.isEnabled() ? Theme.sliderTickColor.getColor()
				: Theme.sliderTickDisabledColor.getColor());
		g.drawLine(0, y, tickBounds.width - 2, y);
	}

	@Override
	protected void paintMinorTickForHorizSlider(final Graphics g,
			final Rectangle tickBounds, final int x) {
		g.setColor(slider.isEnabled() ? Theme.sliderTickColor.getColor()
				: Theme.sliderTickDisabledColor.getColor());
		g.drawLine(x, 0, x, tickBounds.height / 2 - 1);
	}

	@Override
	protected void paintMinorTickForVertSlider(final Graphics g,
			final Rectangle tickBounds, final int y) {
		g.setColor(slider.isEnabled() ? Theme.sliderTickColor.getColor()
				: Theme.sliderTickDisabledColor.getColor());
		g.drawLine(0, y, tickBounds.width / 2 - 1, y);
	}

	@Override
	public void paintThumb(final Graphics g) {
		if (!slider.isEnabled()) {
			g.setColor(Theme.sliderThumbDisabledColor.getColor());
			drawXpThumbDisabled(g);
			return;
		}

		if (isDragging) {
			g.setColor(Theme.sliderThumbPressedColor.getColor());
		} else if (isRollover && Theme.sliderRolloverEnabled.getValue()) {
			// sliderThumbColor is correct, sliderRolloverThumbColor
			// is only for left/right or top/bottom sides
			g.setColor(Theme.sliderThumbColor.getColor());
		} else {
			g.setColor(Theme.sliderThumbColor.getColor());
		}

		drawXpThumb(g);
	}

	@Override
	public void paintTrack(final Graphics g) {
		drawXpTrack(g);
	}
}
