import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rsbot.bot.Bot;
import org.rsbot.event.EventMulticaster;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;


@ScriptManifest(authors = "Waterwolf", category = "Development", name = "Infinity Science", website = "http://www.lazygamerz.org/forums/index.php?topic=3302.0")
public class DevExplorer extends Script implements PaintListener, MessageListener, MouseMotionListener {

	MainGui mg = new MainGui(this);
	
	@Override
	public boolean onStart(Map<String, String> args) {
		registerListeners();
		opengui();
		return true;
	}
	
	public void registerListeners() {
		Bot.getEventManager().addListener(new DevPressListener(), EventMulticaster.MOUSE_EVENT);
		Bot.getEventManager().addListener(this, EventMulticaster.MOUSE_MOTION_EVENT);
		Bot.getEventManager().addListener(this, EventMulticaster.PAINT_EVENT);
	}
	
	boolean terminated = false;
	
	public void opengui() {
		mg.init();
		new Thread(new LoopCaller()).start();
	}
	
	public void onFinish() {
		if (mg.isVisible() && !terminated)
			mg.dispose();
		
		if (!terminated)
			registerListeners();
	}
	
	public static void main(String[] args) {
		new DevExplorer().opengui();
	}
	
	@Override
	public int loop() {
		if (!mg.isVisible())
			return -1;
		return 2000;
	}
	
	public class LoopCaller implements Runnable {

		@Override
		public void run() {
			while (mg.isVisible()) {
				try {
					DevPanel sdp = mg.getSelectedPanel();
					if (sdp != null) {
						Thread.sleep(sdp.loop());
						continue;
					}
					
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		}
		
	}
	
	public class MainGui extends JFrame {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3797651023712864932L;
		
		private final DevPanel[] tabs = {new DevTestPane(), new DevInterfaceExplorer(), new DevSettingsExplorer(), new DevPlayerExplorer(), new DevNPCExplorer(), new DevObjectExplorer(), new DevPathMaker(), new DevModelDraw()};
		private DevPanel selectedPanel = null;
		
		private DevExplorer s;
		public MainGui(DevExplorer s) {
			this.s = s;
		}
		
		public void init() {
			setTitle("DevExplorer");
			
			JTabbedPane tabPane = new JTabbedPane();
			
			for (DevPanel dp : tabs) {
				dp.init(s);
				tabPane.add(dp.getTabName(), dp);
			}
			
			selectedPanel = tabs[0];
			tabPane.setSelectedIndex(0);
			
			tabPane.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent event) {
					JTabbedPane sourceTabbedPane = (JTabbedPane) event.getSource();
			        int index = sourceTabbedPane.getSelectedIndex();
			        selectedPanel = tabs[index];
				}
				
			});
			
			add(tabPane, BorderLayout.CENTER);
			
			Dimension size = new Dimension(610, 500);
			setMinimumSize(size);
			setPreferredSize(size);
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			setVisible(true);
		}
		
		public DevPanel getSelectedPanel() {
			return selectedPanel;
		}
	}

	@Override
	public void onRepaint(Graphics render) {
		DevPanel sdp = mg.getSelectedPanel();
		if (sdp != null)
			sdp.cPaint(render);
	}

	@Override
	public void messageReceived(MessageEvent e) {
		DevPanel sdp = mg.getSelectedPanel();
		if (sdp != null)
			sdp.messageReceived(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		DevPanel sdp = mg.getSelectedPanel();

		if (sdp != null)
			sdp.mouseUpdate(e.getPoint());
	}
	
	public class DevPressListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			DevPanel sdp = mg.getSelectedPanel();

			if (sdp != null)
				sdp.mousePress(e);
		}
	}
	
	public void terminateWithoutGui() {
		terminated = true;
		stopScript(false);
	}

}
