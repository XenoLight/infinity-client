import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rsbot.bot.Bot;
import org.rsbot.client.Node;
import org.rsbot.client.RSNPCNode;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSCharacter.Orientation;


public class DevNPCExplorer extends DevPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5957681785895166364L;
	
	Script s;
	DefaultListModel listm;
	JPanel infoArea;
	JList list;
	JButton updp;
	
	@Override
	public void init(Script s) {
		this.s = s;
		
		setLayout(new BorderLayout());
		
		JPanel sidebar = new JPanel(new BorderLayout());
		JButton upd = new JButton("Update npc list");
		upd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
			
		});
		sidebar.add(upd, BorderLayout.NORTH);
		
		infoArea = new JPanel();
		infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
		
		updp = new JButton("Update selected NPC");
		updp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateSelectedNPC();
			}
			
		});
		
		sidebar.add(infoArea, BorderLayout.CENTER);
		
		listm = new DefaultListModel();
		list = new JList(listm); 
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent event) {
				updateSelectedNPC();
			}
			
		});
		
		add(new JScrollPane(list), BorderLayout.CENTER);
		
		JScrollPane sidePane = new JScrollPane(sidebar);
		sidePane.setBorder(null);
        sidePane.setPreferredSize(new Dimension(280, 500));
		add(sidePane, BorderLayout.EAST);
	}
	
    private void addInfo(final String key, final String value) {
        final JPanel row = new JPanel();
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        for (final String data : new String[]{key, value}) {
            final JLabel label = new JLabel(data);
            label.setAlignmentY(Component.TOP_ALIGNMENT);
            row.add(label);
        }
        infoArea.add(row);
    }
	
	public void update() {
		listm.clear();
		
		for (int k : Bot.getClient().getRSNPCIndexArray()) {
            Node localNode = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), k);
            if (localNode == null || !(localNode instanceof RSNPCNode)) {
                continue;
            }
            RSNPC npc = new RSNPC(((RSNPCNode) localNode).getRSNPC());
            listm.addElement(npc.getName() + " (" + npc.getLevel() + ")");
		}
	}
	
	public RSNPC findNPC(String name) {
		int lookForId = -1;
		
		String[] slt = name.split(" \\(");
		if (slt.length >= 2) {
			lookForId = Integer.valueOf(slt[slt.length-1].replace(")", ""));
			name = name.substring(0, name.lastIndexOf(" ("));
		}
		for (int k : Bot.getClient().getRSNPCIndexArray()) {
            Node localNode = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), k);
            if (localNode == null || !(localNode instanceof RSNPCNode)) {
                continue;
            }
            RSNPC npc = new RSNPC(((RSNPCNode) localNode).getRSNPC());
            if (npc.getName().equals(name) && (lookForId == -1 || lookForId == npc.getLevel()))
            	return npc;
		}
		return null;
	}
	
	RSNPC selectedNPC = null;
	
	public void updateSelectedNPC() {
		RSNPC npc = findNPC((String) list.getSelectedValue());
		if (npc == null)
			return;
		
		infoArea.removeAll();
		
		addInfo("Name : ", npc.getName());
		addInfo("Animation : ", npc.getAnimation()+"");
		Orientation or = npc.getOrientation();
		addInfo("Orientation : ", (or == null ? "null (" + npc.getTurnDirection() + ")" : or.name()));
		addInfo("Message : ", npc.getMessage());
		addInfo("Location : ", npc.getLocation()+"");
		addInfo("Interacting : ", (npc.getInteracting()!=null)+"");
		addInfo("HPPercent : ", npc.getHPPercent()+"");
		addInfo("Height : ", npc.getHeight()+"");
		addInfo("Graphic : ", npc.getGraphic()+"");
		addInfo("isMoving : ", npc.isMoving()+"");
		addInfo("isPoisoned : ", npc.isPoisoned()+"");
		
		selectedNPC = npc;
		
		addComponent(updp);

		infoArea.validate();
		infoArea.repaint();
	}
	
	public void addComponent(Component c) {
		final JPanel row = new JPanel();
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        
        row.add(c);
        
        infoArea.add(row);
	}

	@Override
	public String getTabName() {
		return "NPC Explorer";
	}
	
	@Override
	public void cPaint(Graphics g) {
		if (selectedNPC != null) {
			Point mmPoint = selectedNPC.getMapLocation();
			if (mmPoint.x != -1 && mmPoint.y != -1) {
				g.setColor(Color.red);
				g.drawLine(mmPoint.x-5, mmPoint.y, mmPoint.x+5, mmPoint.y);
				g.drawLine(mmPoint.x, mmPoint.y-5, mmPoint.x, mmPoint.y+5);
				
				g.setColor(Color.orange);
				g.fillRect(mmPoint.x-3, mmPoint.y-3, 6, 6);
			}
			highlightTile(g, selectedNPC.getLocation(), Color.PINK, selectedNPC.getName());
		}
	}
	
	public static void highlightTile(final Graphics g, final RSTile t,
			final Color outline, final String text) {
		if (t == null)
			return;
		
		final Color fill = new Color(outline.getRed(), outline.getGreen(), outline.getBlue(), 50);

		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		if (py.x == -1 || pxy.x == -1 || px.x == -1 || pn.x == -1) {
			return;
		}
		g.setColor(outline);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.setColor(fill);
		g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		
		g.setColor(Color.white);
		g.drawString(text, py.x-25, pxy.y);
	}

}
