package org.rsbot.event.events;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.EventListener;

import org.rsbot.bot.Bot;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.listeners.PaintListener;

/**
 * A paint update event.
 */
public class PaintEvent extends RSEvent {

	private static final long serialVersionUID = -7404828108740551228L;
	public Graphics graphics;

	@Override
	public void dispatch(final EventListener el) {
		if (graphics == null) {
			try {
				if (Bot.game!=null || !Bot.game.isLoggedIn())  {
					((PaintListener) el).onRepaint(null);
				}
			} catch (final NullPointerException ignored) {
			}
			return;
		}
		final Graphics2D g2d = (Graphics2D) graphics;

		// Store settings
		final Color s_background = g2d.getBackground();
		final Shape s_clip = g2d.getClip();
		final Color s_color = g2d.getColor();
		final Composite s_composite = g2d.getComposite();
		final Font s_font = g2d.getFont();
		final Paint s_paint = g2d.getPaint();
		final RenderingHints s_renderingHints = g2d.getRenderingHints();
		final Stroke s_stroke = g2d.getStroke();
		final AffineTransform s_transform = g2d.getTransform();

		// Dispatch the event
		if (Bot.game!=null || !Bot.game.isLoggedIn())  {
			((PaintListener) el).onRepaint(graphics);
		}

		// Restore settings
		g2d.setBackground(s_background);
		g2d.setClip(s_clip);
		g2d.setColor(s_color);
		g2d.setComposite(s_composite);
		g2d.setFont(s_font);
		g2d.setPaint(s_paint);
		g2d.setRenderingHints(s_renderingHints);
		g2d.setStroke(s_stroke);
		g2d.setTransform(s_transform);
	}

	@Override
	public long getMask() {
		return EventMulticaster.PAINT_EVENT;
	}
}
