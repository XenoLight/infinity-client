import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.script.Script;


public abstract class DevPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2710606348750792017L;
	
	public abstract String getTabName();
	public void init() {}
	public void init(Script script) {init();}
	public void init(DevExplorer exp) {init((Script) exp);}
	public void cPaint(Graphics g) {}
	public void messageReceived (MessageEvent event) {}
	public int loop() {return 1000;}
	public void mousePress(MouseEvent e) {}
	public void mouseUpdate(Point p){}
}
