import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;



public class DevTestPane extends DevPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8408575536618021809L;

	ArrayList<Particle> pas = new ArrayList<Particle>();
	
	@Override
	public String getTabName() {
		return "Home";
	}

	@Override
	public void init(final DevExplorer exp) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("Welcome to Infinity Science Labs"), BorderLayout.CENTER);
		
		final JButton terminate = new JButton("Stop script without disposing gui (might stop painting on game screen)");
		terminate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				terminate.setEnabled(false);
				exp.terminateWithoutGui();
			}
			
		});
		add(terminate, BorderLayout.CENTER);
	}
	
	Point p = new Point(250, 250);
	
	double size = 2;
	
	@Override
	public void mousePress(MouseEvent e) {
		if (size < 100)
			size += 15;
	}
	
	@Override
	public int loop() {
		
		if (size > 100) {
			size -= 1;
		}
		
		if (size > 2) {
			size -= 0.25;
		}
		
		double randAngle = Math.toRadians(360 * Math.random());
		
		pas.add(new Particle(randAngle, 
				new Point((int) (p.x-size/2), (int) (p.y-size/2)),
				(int) size));
		
		Iterator<Particle> it = pas.iterator();
		while (it.hasNext()) {
			if (!it.next().tick())
				it.remove();
		}
		return 10;
	}
	
	@Override
	public void mouseUpdate(Point p) {
		this.p = p;
	}
	
	@Override
	public void cPaint(Graphics g) {
		Iterator<Particle> it = pas.iterator();
		while (it.hasNext()) {
			it.next().draw(g);
		}
	}
	
	private class Particle {
		public double curx;
		public double cury;
		
		private final double xcos;
		private double ysin;
		
		public int life;
		public int ticks = 0;
		
		private double speed = 1.6;
		private Color color;
		private int size;
		
		public Particle(double angle, Point start, int size) {
			this.curx = start.x;
			this.cury = start.y;
			
			this.xcos = Math.cos(angle);
			this.ysin = Math.sin(angle);
			
			this.life = (int) (20 + Math.random()*100);
			this.size = size;
			
			this.color = new Color((int) (Math.random()*255), (int) (Math.random()*255), (int) (Math.random()*255));
		}
		
		public boolean tick() {
			if (ticks >= life)
				return false;
			
			ysin += 0.01; // gravity
			
			curx += xcos*speed;
			cury += ysin*speed;
			
			ticks++;
			return true;
		}
		
		public void draw(Graphics g) {
			g.setColor(color);
			g.fillRect((int) curx, (int) cury, size, size);
		}
	}
}
