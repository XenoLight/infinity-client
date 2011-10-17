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
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSCharacter.Orientation;


public class DevPlayerExplorer extends DevPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -199782223519626540L;
	
	Script s;
	DefaultListModel listm;
	JPanel infoArea;
	JList list;
	JButton updp;
	
	@Override
	public void init(final Script s) {
		this.s = s;
		
		setLayout(new BorderLayout());
		
		JPanel sidebar = new JPanel(new BorderLayout());
		JButton upd = new JButton("Update player list");
		upd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
			
		});
		sidebar.add(upd, BorderLayout.NORTH);
		
		infoArea = new JPanel();
		infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
		
		updp = new JButton("Update selected player");
		updp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateSelectedPlayer();
			}
			
		});
		
		sidebar.add(infoArea, BorderLayout.CENTER);
		
		JButton findMe = new JButton("getMyPlayer()");
		findMe.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (listm.size() == 0)
					update();
				RSPlayer me = s.player.getMine();
				if (me != null) {
					Object val = list.getSelectedValue();
					if (val != null && ((String) val).equals(me.getName()))
						updateSelectedPlayer();
					else
						list.setSelectedValue(me.getName(), true);
					
				}
			}
			
		});
		sidebar.add(findMe, BorderLayout.SOUTH);
		
		listm = new DefaultListModel();
		list = new JList(listm); 
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent event) {
				updateSelectedPlayer();
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
		
		final org.rsbot.client.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();
		if (players == null) {
			return;
		}
		
		for (final org.rsbot.client.RSPlayer element : players) {
            if (element == null) {
                continue;
            }
            final RSPlayer player = new RSPlayer(element);
            
            listm.addElement(player.getName());
		}
	}
	
	public RSPlayer findPlayer(String name) {
		final org.rsbot.client.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();
		if (players == null) {
			return null;
		}

		for (final org.rsbot.client.RSPlayer element : players) {
			if (element == null) {
				continue;
			}
			final RSPlayer player = new RSPlayer(element);
			if (player.getName().equals(name))
				return player;
		}
		return null;
	}
	
	RSPlayer selectedPlayer = null;
	
	public void updateSelectedPlayer() {
		RSPlayer player = findPlayer((String) list.getSelectedValue());
		if (player == null)
			return;
		
		infoArea.removeAll();
		
		addInfo("Name : ", player.getName());
		addInfo("Animation : ", player.getAnimation()+"");
		addInfo("Combat level : ", player.getCombatLevel()+"");
		addInfo("Team : ", player.getTeam()+"");
		Orientation or = player.getOrientation();
		addInfo("Orientation : ", (or == null ? "null (" + player.getTurnDirection() + ")" : or.name()));
		addInfo("Message : ", player.getMessage());
		addInfo("Location : ", player.getLocation()+"");
		addInfo("Interacting : ", (player.getInteracting()!=null)+"");
		addInfo("HPPercent : ", player.getHPPercent()+"");
		addInfo("Height : ", player.getHeight()+"");
		addInfo("Graphic : ", player.getGraphic()+"");
		addInfo("isMoving : ", player.isMoving()+"");
		addInfo("isPoisoned : ", player.isPoisoned()+"");
		
		selectedPlayer = player;
		
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
		return "Player explorer";
	}
	
	@Override
	public void cPaint(Graphics g) {
		if (selectedPlayer != null) {
			Point mmPoint = selectedPlayer.getMapLocation();
			if (mmPoint.x != -1 && mmPoint.y != -1) {
				g.setColor(Color.red);
				g.drawLine(mmPoint.x-5, mmPoint.y, mmPoint.x+5, mmPoint.y);
				g.drawLine(mmPoint.x, mmPoint.y-5, mmPoint.x, mmPoint.y+5);
				
				g.setColor(Color.orange);
				g.fillRect(mmPoint.x-3, mmPoint.y-3, 6, 6);
			}
			highlightTile(g, selectedPlayer.getLocation(), Color.PINK, selectedPlayer.getName());
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
