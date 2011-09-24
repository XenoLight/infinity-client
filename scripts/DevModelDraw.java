import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;


public class DevModelDraw extends DevPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6993729294997644615L;
	
	BufferedImage transmitter = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
	Graphics tg = transmitter.getGraphics();
	
	Script s;
	
	RSObject hilite = null;
	
	@Override
	public String getTabName() {
		return "Models";
	}

	@Override
	public void init(Script s) {
		this.s = s;
	}
	
	@Override
	public int loop() {
		transmitModels();
		repaint();
		return 20;
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint(Graphics g) {
		g.drawImage(transmitter, 0, 0, this);
	}
	
	final Color almostWhite = new Color(255, 255, 255, 80);
	
	public void transmitModels() {
		final RSPlayer player = s.player.getMine();
		if (player == null) {
			return;
		}
		
		tg.clearRect(0, 0, 700, 400);
		
		final RSTile location = player.getLocation();
		final int locX = location.getX();
		final int locY = location.getY();
		for (int x = locX - 25; x < locX + 25; x++) {
			for (int y = locY - 25; y < locY + 25; y++) {
				final Point screen = Calculations.tileToScreen(x, y, 0);
				if (!s.calculate.pointOnScreen(screen)) {
					continue;
				}
				final RSObject object = s.objects.getTopAt(x, y);
				if (object != null && (object.getID() != 0 && object.getObject().getModel() != null)) {
					
					if (hilite != null && object.getLocation().equals(hilite.getLocation()))
						tg.setColor(Color.orange);
					else
						tg.setColor(almostWhite);
					for(Polygon p : object.getModel().getTriangles()){
						if(p != null) {
							tg.drawPolygon(p);
						}
					}
					/*tg.setColor(Color.magenta);
					int xx = screen.x+10;
					int yy = screen.y+10;
					
					RSObjectDef def = object.getDef();
					
					tg.drawString(object.getID()+" "+(def == null ? "nd" : def.getName()), xx, yy);
					*/
				}
			}
		}
	}
	
	@Override
	public void mouseUpdate(Point p) {
		hilite = getTileByXY(p);
	}
	
    public RSObject getTileByXY(Point p) {
    	RSTile loc = s.player.getMyLocation();
        for (int xx = -52; xx < 52; xx++){
                for (int yy = -52; yy < 52; yy++){
                	
                	RSTile mee = new RSTile(loc.getX()+xx, loc.getY()+yy);
                	
                	final Point screen = Calculations.tileToScreen(mee.getX(), mee.getY(), 0);
    				if (!s.calculate.pointOnScreen(screen)) {
    					continue;
    				}
    				final RSObject object = s.objects.getTopAt(mee.getX(), mee.getY());
    				if (object != null && object.getModel() != null) {
    					for(final Polygon po : object.getModel().getTriangles()) {
    						if (po == null)
    							continue;
    						if (po.contains(p))
    							return object;
    					}
    				}
                }
        }
        return null;
    }

}
