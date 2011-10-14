/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * @author Michael Hagen
 */
public class BaseBorders {

	public static class BaseInternalFrameBorder extends AbstractBorder
	implements UIResource {

		protected final int dw = 5;
		protected final int trackWidth = 22;
		protected final Insets insets = new Insets(dw, dw, dw, dw);
		protected final Insets paletteInsets = new Insets(3, 3, 3, 3);

		public BaseInternalFrameBorder() {
			insets.top = dw;
		}

		@Override
		public Insets getBorderInsets(final Component c) {
			if (isResizable(c)) {
				return insets;
			} else {
				return paletteInsets;
			}
		}

		public int getTitleHeight(final Component c) {
			int th = 21;
			final int fh = getBorderInsets(c).top + getBorderInsets(c).bottom;
			if (c instanceof JDialog) {
				final JDialog dialog = (JDialog) c;
				th = dialog.getSize().height
				- dialog.getContentPane().getSize().height - fh - 1;
				if (dialog.getJMenuBar() != null) {
					th -= dialog.getJMenuBar().getSize().height;
				}
			} else if (c instanceof JInternalFrame) {
				final JInternalFrame frame = (JInternalFrame) c;
				th = frame.getSize().height
				- frame.getRootPane().getSize().height - fh - 1;
				if (frame.getJMenuBar() != null) {
					th -= frame.getJMenuBar().getSize().height;
				}
			} else if (c instanceof JRootPane) {
				final JRootPane jp = (JRootPane) c;
				if (jp.getParent() instanceof JFrame) {
					final JFrame frame = (JFrame) c.getParent();
					th = frame.getSize().height
					- frame.getContentPane().getSize().height - fh - 1;
					if (frame.getJMenuBar() != null) {
						th -= frame.getJMenuBar().getSize().height;
					}
				} else if (jp.getParent() instanceof JDialog) {
					final JDialog dialog = (JDialog) c.getParent();
					th = dialog.getSize().height
					- dialog.getContentPane().getSize().height - fh - 1;
					if (dialog.getJMenuBar() != null) {
						th -= dialog.getJMenuBar().getSize().height;
					}
				}
			}
			return th;
		}

		public boolean isActive(final Component c) {
			boolean active = true;
			if (c instanceof JDialog) {
				final JDialog dlg = (JDialog) c;
				if (dlg.getParent() instanceof JComponent) {
					return JTattooUtilities.isActive((JComponent) (dlg
							.getParent()));
				}
			} else if (c instanceof JInternalFrame) {
				final JInternalFrame frame = (JInternalFrame) c;
				active = frame.isSelected();
				if (active) {
					return JTattooUtilities.isActive(frame);
				}
			} else if (c instanceof JRootPane) {
				final JRootPane jp = (JRootPane) c;
				if (jp.getTopLevelAncestor() instanceof Window) {
					final Window window = (Window) jp.getTopLevelAncestor();
					return JTattooUtilities.isWindowActive(window);
				}
			}
			return active;
		}

		public boolean isResizable(final Component c) {
			boolean resizable = true;
			if (c instanceof JDialog) {
				final JDialog dialog = (JDialog) c;
				resizable = dialog.isResizable();
			} else if (c instanceof JInternalFrame) {
				final JInternalFrame frame = (JInternalFrame) c;
				resizable = frame.isResizable();
			} else if (c instanceof JRootPane) {
				final JRootPane jp = (JRootPane) c;
				if (jp.getParent() instanceof JFrame) {
					final JFrame frame = (JFrame) c.getParent();
					resizable = frame.isResizable();
				} else if (jp.getParent() instanceof JDialog) {
					final JDialog dialog = (JDialog) c.getParent();
					resizable = dialog.isResizable();
				}
			}
			return resizable;
		}
	} // class BaseInternalFrameBorder
	public static class ComboBoxBorder extends AbstractBorder implements
	UIResource {

		private static final Insets insets = new Insets(1, 1, 1, 1);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y,
				final int width, final int height) {
			g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
			g.drawRect(x, y, width - 1, height - 1);
		}
	} // class ComboBoxBorder
	public static class Down3DBorder extends AbstractBorder implements
	UIResource {

		private static final Insets insets = new Insets(1, 1, 1, 1);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final Color frameColor = AbstractLookAndFeel.getTheme()
			.getBackgroundColor();
			JTattooUtilities.draw3DBorder(g,
					ColorHelper.darker(frameColor, 20),
					ColorHelper.brighter(frameColor, 80), x, y, w, h);
		}
	} // class Down3DBorder
	public static class MenuItemBorder extends AbstractBorder implements
	UIResource {

		private static final Insets insets = new Insets(2, 2, 2, 2);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final JMenuItem b = (JMenuItem) c;
			final ButtonModel model = b.getModel();
			final Color borderColorLo = AbstractLookAndFeel.getFrameColor();
			final Color borderColorHi = ColorHelper.brighter(
					AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 50);
			if (c.getParent() instanceof JMenuBar) {
				if (model.isArmed() || model.isSelected()) {
					g.setColor(borderColorLo);
					g.drawLine(x, y, x + w - 1, y);
					g.drawLine(x, y, x, y + h - 1);
					g.drawLine(x + w - 1, y + 1, x + w - 1, y + h - 1);
					g.setColor(borderColorHi);
					g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
					g.drawLine(x + 1, y + 1, x + 1, y + h - 2);
				}
			} else {
				if (model.isArmed()
						|| (c instanceof JMenu && model.isSelected())) {
					g.setColor(borderColorLo);
					g.drawLine(x, y, x + w - 1, y);
					g.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
					g.setColor(borderColorHi);
					g.drawLine(x, y + 1, x + w - 2, y + 1);
				}
			}
		}
	} // class MenuItemBorder
	public static class PaletteBorder extends AbstractBorder implements
	UIResource {

		private static final Insets insets = new Insets(1, 1, 1, 1);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			if (JTattooUtilities.isFrameActive((JComponent) c)) {
				g.setColor(AbstractLookAndFeel.getWindowBorderColor());
			} else {
				g.setColor(AbstractLookAndFeel.getWindowInactiveBorderColor());
			}
			g.drawRect(x, y, w - 1, h - 1);
		}
	} // class PaletteBorder
	public static class PopupMenuBorder extends AbstractBorder implements
	UIResource {

		protected static final Font logoFont = new Font("Dialog", Font.BOLD, 12);
		protected Insets logoInsets = new Insets(2, 18, 1, 1);
		protected Insets insets = new Insets(2, 1, 1, 1);

		@Override
		public Insets getBorderInsets(final Component c) {
			if (hasLogo()) {
				return logoInsets;
			} else {
				return insets;
			}
		}

		public boolean hasLogo() {
			return ((AbstractLookAndFeel.getTheme().getLogoString() != null) && (AbstractLookAndFeel
					.getTheme().getLogoString().length() > 0));
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final int dx = getBorderInsets(c).left;
			final Color logoColor = AbstractLookAndFeel
			.getMenuSelectionBackgroundColor();
			final Color menuColor = AbstractLookAndFeel.getMenuBackgroundColor();
			final Color borderColorLo = AbstractLookAndFeel.getFrameColor();
			final Color borderColorHi = ColorHelper.brighter(
					AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 50);
			g.setColor(logoColor);
			g.fillRect(x, y, dx - 1, h - 1);
			if (hasLogo()) {
				paintLogo((Graphics2D) g, dx, h);
			}
			g.setColor(borderColorHi);
			g.drawLine(x + 1, y + 1, x + dx, y + 1);
			g.drawLine(x + 1, y + 1, x + 1, y + h - 1);
			g.setColor(ColorHelper.brighter(menuColor, 50.0));
			g.drawLine(x + dx, y + 1, x + w - 2, y + 1);
			g.setColor(borderColorLo);
			g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - 1);
			g.drawRect(x, y, w - 1, h - 1);
		}

		public void paintLogo(final Graphics2D g2D, final int w, final int h) {
			final BufferedImage image = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_ARGB);
			final Graphics2D imageGraphics = image.createGraphics();
			final Color logoColor = AbstractLookAndFeel.getTheme()
			.getMenuSelectionBackgroundColor();
			imageGraphics.setColor(logoColor);
			imageGraphics.fillRect(0, 0, w, h);

			imageGraphics.setFont(logoFont);
			final FontMetrics fm = imageGraphics.getFontMetrics();
			final AffineTransform at = new AffineTransform();
			at.setToRotation(Math.PI + (Math.PI / 2));
			imageGraphics.setTransform(at);
			final int xs = -h + 4;
			final int ys = fm.getAscent() + 2;

			imageGraphics.setColor(ColorHelper.darker(logoColor, 20));
			imageGraphics.drawString(
					JTattooUtilities.getClippedText(AbstractLookAndFeel
							.getTheme().getLogoString(), fm, h - 16), xs - 1,
							ys + 1);

			imageGraphics.setColor(Color.white);
			imageGraphics.drawString(
					JTattooUtilities.getClippedText(AbstractLookAndFeel
							.getTheme().getLogoString(), fm, h - 16), xs, ys);

			final Rectangle2D r2D = new Rectangle2D.Double(0, 0, w, h);
			final TexturePaint texturePaint = new TexturePaint(image, r2D);
			g2D.setPaint(texturePaint);
			g2D.fillRect(0, 0, w, h);
		}
	} // class PopupMenuBorder
	public static class PopupMenuShadowBorder extends PopupMenuBorder {

		private static final int shadowSize = 3;

		public PopupMenuShadowBorder() {
			logoInsets = new Insets(2, 18, shadowSize + 1, shadowSize + 1);
			insets = new Insets(2, 1, shadowSize + 1, shadowSize + 1);
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final Graphics2D g2D = (Graphics2D) g;
			final Composite composite = g2D.getComposite();
			AlphaComposite alpha = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.6f);
			g2D.setComposite(alpha);
			final int dx = getBorderInsets(c).left;
			final Color logoColor = AbstractLookAndFeel.getTheme()
			.getMenuSelectionBackgroundColor();
			final Color borderColorLo = AbstractLookAndFeel.getFrameColor();
			final Color borderColorHi = ColorHelper.brighter(
					AbstractLookAndFeel.getMenuSelectionBackgroundColor(), 50);
			g.setColor(logoColor);
			g.fillRect(x, y, dx - 1, h - 1 - shadowSize);
			if (hasLogo()) {
				paintLogo(g2D, dx, h - shadowSize);
			}
			alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					AbstractLookAndFeel.getTheme().getMenuAlpha());
			g2D.setComposite(alpha);
			g.setColor(borderColorHi);
			g.drawLine(x + 1, y + 1, x + dx, y + 1);
			g.drawLine(x + 1, y + 1, x + 1, y + h - shadowSize - 1);
			g.setColor(Color.white);
			g.drawLine(x + dx, y + 1, x + w - 2, y + 1);
			g.setColor(borderColorLo);
			g.drawLine(x + dx - 1, y + 1, x + dx - 1, y + h - shadowSize - 1);
			g.drawRect(x, y, w - shadowSize - 1, h - shadowSize - 1);

			// paint the shadow
			g2D.setColor(Color.black);
			float alphaValue = 0.6f;
			for (int i = 0; i < shadowSize; i++) {
				alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						alphaValue);
				g2D.setComposite(alpha);
				g.drawLine(x + w - shadowSize + i, y + shadowSize, x + w
						- shadowSize + i, y + h - shadowSize - 1 + i);
				g.drawLine(x + shadowSize, y + h - shadowSize + i, x + w
						- shadowSize + i, y + h - shadowSize + i);
				alphaValue -= (alphaValue / 2);
			}

			g2D.setComposite(composite);
		}
	} // class PopupMenuShadowBorder
	public static class SpinnerBorder extends AbstractBorder implements
	UIResource {

		private static final Insets insets = new Insets(1, 1, 1, 1);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y,
				final int width, final int height) {
			g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
			g.drawRect(x, y, width - 1, height - 1);
		}
	} // class SpinnerBorder
	public static class TableHeaderBorder extends AbstractBorder implements
	UIResource {

		private static final Insets insets = new Insets(2, 2, 2, 0);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final Color cHi = MetalLookAndFeel.getControlHighlight();
			final Color cLo = MetalLookAndFeel.getControlShadow();
			JTattooUtilities.draw3DBorder(g, cHi, cLo, x, y, w, h);
		}
	} // class TableHeaderBorder
	// ------------------------------------------------------------------------------------
	// Implementation of border classes
	// ------------------------------------------------------------------------------------
	public static class TextFieldBorder extends AbstractBorder implements
	UIResource {

		private static final Insets insets = new Insets(2, 2, 2, 2);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y,
				final int width, final int height) {
			g.setColor(AbstractLookAndFeel.getTheme().getFrameColor());
			g.drawRect(x, y, width - 1, height - 1);
		}
	} // class TextFieldBorder
	public static class ToolBarBorder extends AbstractBorder implements
	UIResource, SwingConstants {

		private static final Color shadow = new Color(160, 160, 160);

		@Override
		public Insets getBorderInsets(final Component c) {
			final Insets insets = new Insets(2, 2, 2, 2);
			if (((JToolBar) c).isFloatable()) {
				if (((JToolBar) c).getOrientation() == HORIZONTAL) {
					if (JTattooUtilities.isLeftToRight(c)) {
						insets.left = 15;
					} else {
						insets.right = 15;
					}
				} else {
					insets.top = 15;
				}
			}
			final Insets margin = ((JToolBar) c).getMargin();
			if (margin != null) {
				insets.left += margin.left;
				insets.top += margin.top;
				insets.right += margin.right;
				insets.bottom += margin.bottom;
			}
			return insets;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, int x, final int y, final int w,
				final int h) {
			if (((JToolBar) c).isFloatable()) {
				final Graphics2D g2D = (Graphics2D) g;
				final Composite composite = g2D.getComposite();
				AlphaComposite alpha = alpha = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, 0.5f);
				g2D.setComposite(alpha);
				if (((JToolBar) c).getOrientation() == HORIZONTAL) {
					if (!JTattooUtilities.isLeftToRight(c)) {
						x += w - 15;
					}
					g.setColor(Color.white);
					g.drawLine(x + 3, y + 4, x + 3, h - 5);
					g.drawLine(x + 6, y + 3, x + 6, h - 4);
					g.drawLine(x + 9, y + 4, x + 9, h - 5);
					g.setColor(shadow);
					g.drawLine(x + 4, y + 4, x + 4, h - 5);
					g.drawLine(x + 7, y + 3, x + 7, h - 4);
					g.drawLine(x + 10, y + 4, x + 10, h - 5);
				} else // vertical
				{
					g.setColor(Color.white);
					g.drawLine(x + 3, y + 3, w - 4, y + 3);
					g.drawLine(x + 3, y + 6, w - 4, y + 6);
					g.drawLine(x + 3, y + 9, w - 4, y + 9);
					g.setColor(shadow);
					g.drawLine(x + 3, y + 4, w - 4, y + 4);
					g.drawLine(x + 3, y + 7, w - 4, y + 7);
					g.drawLine(x + 3, y + 10, w - 4, y + 10);
				}
				g2D.setComposite(composite);
			}
		}
	} // class ToolBarBorder
	public static class ToolButtonBorder implements Border, UIResource {

		private static final Insets insets = new Insets(2, 2, 2, 2);

		@Override
		public Insets getBorderInsets(final Component c) {
			return insets;
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w,
				final int h) {
			final AbstractButton button = (AbstractButton) c;
			final ButtonModel model = button.getModel();
			final Color frameColor = AbstractLookAndFeel.getToolbarBackgroundColor();
			final Color frameHiColor = ColorHelper.brighter(frameColor, 10);
			final Color frameLoColor = ColorHelper.darker(frameColor, 30);
			JTattooUtilities.draw3DBorder(g, frameHiColor, frameLoColor, x, y,
					w, h);
			if ((model.isPressed() && model.isArmed()) || model.isSelected()) {
				JTattooUtilities.draw3DBorder(g, frameLoColor, frameHiColor, x,
						y, w, h);
			} else {
				JTattooUtilities.draw3DBorder(g, frameLoColor, frameHiColor, x,
						y, w, h);
				JTattooUtilities.draw3DBorder(g, frameHiColor, frameLoColor,
						x + 1, y + 1, w - 2, h - 2);
			}
		}
	} // class ToolButtonBorder
	private static Border textFieldBorder = null;

	private static Border spinnerBorder = null;

	private static Border comboBoxBorder = null;

	private static Border progressBarBorder = null;

	private static Border tableHeaderBorder = null;

	private static Border popupMenuBorder = null;

	private static Border menuItemBorder = null;

	private static Border toolBarBorder = null;

	private static Border toolButtonBorder = null;

	private static Border paletteBorder = null;

	private static Border scrollPaneBorder = null;

	private static Border tabbedPaneBorder = null;

	private static Border desktopIconBorder = null;

	public static Border getComboBoxBorder() {
		if (comboBoxBorder == null) {
			comboBoxBorder = new ComboBoxBorder();
		}
		return comboBoxBorder;
	}

	public static Border getDesktopIconBorder() {
		if (desktopIconBorder == null) {
			desktopIconBorder = new BorderUIResource.CompoundBorderUIResource(
					new LineBorder(AbstractLookAndFeel.getWindowBorderColor(),
							1), new MatteBorder(2, 2, 1, 2,
									AbstractLookAndFeel.getWindowBorderColor()));
		}
		return desktopIconBorder;
	}

	public static Border getMenuBarBorder() {
		return BorderFactory.createEmptyBorder(1, 1, 1, 1);
	}

	public static Border getMenuItemBorder() {
		if (menuItemBorder == null) {
			menuItemBorder = new MenuItemBorder();
		}
		return menuItemBorder;
	}

	public static Border getPaletteBorder() {
		if (paletteBorder == null) {
			paletteBorder = new PaletteBorder();
		}
		return paletteBorder;
	}

	public static Border getPopupMenuBorder() {
		if (popupMenuBorder == null) {
			if (AbstractLookAndFeel.getTheme().isMenuOpaque()) {
				popupMenuBorder = new PopupMenuBorder();
			} else {
				popupMenuBorder = new PopupMenuShadowBorder();
			}
		}
		return popupMenuBorder;
	}

	public static Border getProgressBarBorder() {
		if (progressBarBorder == null) {
			progressBarBorder = BorderFactory
			.createLineBorder(AbstractLookAndFeel.getFrameColor());
		}
		return progressBarBorder;
	}

	public static Border getScrollPaneBorder() {
		if (scrollPaneBorder == null) {
			scrollPaneBorder = new Down3DBorder();
		}
		return scrollPaneBorder;
	}

	public static Border getSpinnerBorder() {
		if (spinnerBorder == null) {
			spinnerBorder = new SpinnerBorder();
		}
		return spinnerBorder;
	}

	public static Border getTabbedPaneBorder() {
		if (tabbedPaneBorder == null) {
			tabbedPaneBorder = BorderFactory.createEmptyBorder(1, 1, 1, 1);
		}
		return tabbedPaneBorder;
	}

	public static Border getTableHeaderBorder() {
		if (tableHeaderBorder == null) {
			tableHeaderBorder = new TableHeaderBorder();
		}
		return tableHeaderBorder;
	}

	// ------------------------------------------------------------------------------------
	// Lazy access methods
	// ------------------------------------------------------------------------------------
	public static Border getTextBorder() {
		if (textFieldBorder == null) {
			textFieldBorder = new TextFieldBorder();
		}
		return textFieldBorder;
	}

	public static Border getTextFieldBorder() {
		return getTextBorder();
	}

	public static Border getToolBarBorder() {
		if (toolBarBorder == null) {
			toolBarBorder = new ToolBarBorder();
		}
		return toolBarBorder;
	}

	public static Border getToolButtonBorder() {
		if (toolButtonBorder == null) {
			toolButtonBorder = new ToolButtonBorder();
		}
		return toolButtonBorder;
	}

	public static void initDefaults() {
		textFieldBorder = null;
		spinnerBorder = null;
		comboBoxBorder = null;
		progressBarBorder = null;
		tableHeaderBorder = null;
		popupMenuBorder = null;
		menuItemBorder = null;
		toolBarBorder = null;
		toolButtonBorder = null;
		paletteBorder = null;
		scrollPaneBorder = null;
		tabbedPaneBorder = null;
		desktopIconBorder = null;
	}
} // class BaseBorders
