import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSTile;


public class DevPathMaker extends DevPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -562265890066829523L;

	@Override
	public String getTabName() {
		return "Path maker";
	}
	
	Script s;
	JTabbedPane tabs;
	PathPanel pathPanel;
	PolygonPanel polygonPanel;
	JPopupMenu popup;
	
	PopupListener pl = new PopupListener();
	
	@Override
	public void init(Script s) {
		this.s = s;
		
		setLayout(new BorderLayout());
		
		tabs = new JTabbedPane();
		
		popup = new JPopupMenu();
	    JMenuItem menuItem = new JMenuItem("Remove");
	    menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (tabs.getSelectedIndex() == 0) {
					pathPanel.listModel.remove(pathPanel.tileList.getSelectedIndex());
				}
				else if (tabs.getSelectedIndex() == 1) {
					polygonPanel.listModel.remove(polygonPanel.tileList.getSelectedIndex());
				}
			}
	    	
	    });
	    popup.add(menuItem);
		
		
		pathPanel = new PathPanel();
		polygonPanel = new PolygonPanel();
		
		tabs.add("Path", pathPanel);
		tabs.add("Polygon", polygonPanel);
		
		add(tabs, BorderLayout.CENTER);
	}
	
	RSTile lastAddedTile = null;
	
	@Override
	public int loop() {
		if (tabs.getSelectedIndex() == 0 && pathPanel.toggleAutomaticAdding.isSelected()) {
			
			if (pathPanel.addWithTime || (lastAddedTile == null || lastAddedTile.distanceTo() > pathPanel.valueBetweenAutomaticTiles.getValue())) {
				lastAddedTile = s.player.getMine().getLocation();
				pathPanel.addTile(lastAddedTile);
			}
			
			
			if (pathPanel.addWithTime)
				return pathPanel.valueBetweenAutomaticTiles.getValue(); // TODO
		}
		return 100;
	}
	
	@Override
	public void mousePress(MouseEvent p) {
		if (tabs.getSelectedIndex() == 1) {
			polygonPanel.click(p.getPoint());
		}
	}
	
	@Override
	public void cPaint(Graphics g) {
		if (tabs.getSelectedIndex() == 0) {
			pathPanel.draw(g);
		}
		else if (tabs.getSelectedIndex() == 1) {
			polygonPanel.drawPoly(g);
		}
	}
	
	public class PathPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 9002764437719115225L;
		
		JToggleButton toggleAutomaticAdding;
		DefaultListModel listModel;
		JList tileList;
		JSlider valueBetweenAutomaticTiles;
		boolean addWithTime = true;
		
		public void addTile(RSTile r) {
			String tileinfo = r.getX() + "x" + r.getY();
			if (!listModel.contains(tileinfo))
				listModel.addElement(tileinfo);

		}
		
		public void draw(Graphics g) {

			Point lastp = null;
			for (int n = 0;n < listModel.size(); n++) {
				String[] sp = ((String) listModel.get(n)).split("x");
				if (sp.length != 2)
					continue;
				int x = Integer.valueOf(sp[0]);
				int y = Integer.valueOf(sp[1]);
				Point po = s.tile.toMiniMap(new RSTile(x, y));
				if (po.x != -1 && po.y != -1) {
					
					if (lastp != null) {
						g.setColor(Color.magenta);
						g.drawLine(lastp.x, lastp.y, po.x, po.y);
					}
					lastp = po;
					
					if (tileList.getSelectedIndex() == n)
						g.setColor(Color.RED);
					else
						g.setColor(Color.CYAN);
					
					g.fillRect(po.x-2, po.y-2, 3, 3);
				}
			}

		}
		
		public PathPanel() {
			setLayout(new BorderLayout());
			
			listModel = new DefaultListModel();
			tileList = new JList(listModel);
			tileList.addMouseListener(pl);
			
			add(new JScrollPane(tileList));
			
			JPanel options = new JPanel();
			options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));
			
			JButton addTile = new JButton("Add current tile to path");
			addTile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addTile(s.player.getMine().getLocation());
				}
			});
			toggleAutomaticAdding = new JToggleButton("Toggle automatic tile adding");
			
			JButton clearTile = new JButton("Clear tilelist");
			clearTile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					listModel.clear();
				}
			});
			DefaultComboBoxModel model = new DefaultComboBoxModel();
			model.addElement("Time");
			model.addElement("Distance");
			
			final JComboBox automaticAdding = new JComboBox(model);
			
			automaticAdding.setMaximumSize(new Dimension(900, 62));

			automaticAdding.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {

					if (automaticAdding.getSelectedIndex() == 0) {
						addWithTime = true;
						valueBetweenAutomaticTiles.setMinimum(100);
						valueBetweenAutomaticTiles.setMaximum(5000);
						valueBetweenAutomaticTiles.setValue(1000);
						valueBetweenAutomaticTiles.setBorder(BorderFactory.createTitledBorder("Time between autotiles"));
						
					}
					else if (automaticAdding.getSelectedIndex() == 1) {
						addWithTime = false;
						valueBetweenAutomaticTiles.setMinimum(1);
						valueBetweenAutomaticTiles.setMaximum(30);
						valueBetweenAutomaticTiles.setValue(8);
						valueBetweenAutomaticTiles.setBorder(BorderFactory.createTitledBorder("Distance between autotiles"));
					}
					
				}
				
			});
			
			valueBetweenAutomaticTiles = new JSlider(100, 5000, 1000);
			valueBetweenAutomaticTiles.setBorder(BorderFactory.createTitledBorder("Time between autotiles"));
			
			JButton listToCode = new JButton("Show code");
			listToCode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String path = "new RSTile[]{";
					
					for (int o = 0;o < listModel.size(); o++) {
						String td = (String) listModel.get(o);
						String[] spl = td.split("x");
						if (spl.length != 2)
							continue;
						
						path += "new RSTile(" + spl[0] + ", " + spl[1] + ")" + (o == listModel.size()-1 ? "" : ", ");
					}
					
					path += "}";
					
					new CodeShower(path);
				}
			});

			options.add(addTile);
			options.add(toggleAutomaticAdding);


			automaticAdding.setBorder(BorderFactory.createTitledBorder("Automatically add new tiles using"));
			options.add(automaticAdding);
			
			options.add(valueBetweenAutomaticTiles);

			options.add(clearTile);
			options.add(listToCode);
			
			add(options, BorderLayout.EAST);
		}
		
		public void setClipboard(String str) {
		    StringSelection ss = new StringSelection(str);
		    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		}
	}
	
	public class PolygonPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7255896276661707902L;
		
		DefaultListModel listModel;
		JList tileList;
		
		public void addTile(RSTile r) {
			String tileinfo = r.getX() + "x" + r.getY();
			if (!listModel.contains(tileinfo))
				listModel.addElement(tileinfo);
		}
		
		public void drawPoly(Graphics g) {
			if (listModel.size() == 0)
				return;
			
			Polygon p = new Polygon();
			
			for (int n = 0;n < listModel.size(); n++) {
				String[] sp = ((String) listModel.get(n)).split("x");
				if (sp.length != 2)
					continue;
				int x = Integer.valueOf(sp[0]);
				int y = Integer.valueOf(sp[1]);
				Point po = s.tile.toMiniMap(new RSTile(x, y));
				if (po.x != -1 && po.y != -1) {
					
					if (tileList.getSelectedIndex() == n)
						g.setColor(Color.RED);
					else
						g.setColor(Color.white);
					
					g.fillRect(po.x-2, po.y-2, 3, 3);
					p.addPoint(po.x, po.y);
				}
			}
			g.setColor(new Color(202, 31, 123, 100));
			g.fillPolygon(p);
			
		}
		
		public PolygonPanel() {
			setLayout(new GridLayout(1, 2));
			
			listModel = new DefaultListModel();
			tileList = new JList(listModel);
			tileList.addMouseListener(pl);
			add(new JScrollPane(tileList));
			
			JPanel options = new JPanel();
			options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));

			JButton clearTile = new JButton("Clear tilelist");
			clearTile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					listModel.clear();
				}
			});
			
			JButton listToCode = new JButton("Show code");
			listToCode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String path = "new RSPolygon[]{";
					
					for (int o = 0;o < listModel.size(); o++) {
						String td = (String) listModel.get(o);
						String[] spl = td.split("x");
						if (spl.length != 2)
							continue;
						
						path += "new RSTile(" + spl[0] + ", " + spl[1] + ")" + (o == listModel.size()-1 ? "" : ", ");
					}
					
					path += "}";
					
					new CodeShower(path);
				}
			});

			options.add(new JLabel("Click on runescape minimap to add points"));
			options.add(clearTile);
			options.add(listToCode);
			
			add(options);
		}
		
		

		public RSTile getTileByLoc(int x, int y) {
			RSTile me = s.player.getMine().getLocation();
			for (int xx = -26; xx < 26; xx++) {
				for (int yy = -26; yy < 26; yy++) {
					if (distanceBetweenInt(s.tile.toMiniMap(new RSTile(me.getX()
							+ xx, me.getY() + yy)), x, y) < 4) {
						return new RSTile(me.getX() + xx, me.getY() + yy);
					}
				}
			}
			return null;
		}
		
		public void click(Point p) {
			RSTile tile = getTileByLoc(p.x, p.y);
			if (tile != null)
				addTile(tile);
		}

		public int distanceBetweenInt(Point p, int x, int y) {
			return (int) Math.hypot(p.x - x, p.y - y);
		}
	}
	
	public void setClipboard(String str) {
	    StringSelection ss = new StringSelection(str);
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
	
	public class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	        	JList sauce = ((JList) e.getSource());
	        	int index = sauce.locationToIndex(e.getPoint());
	        	sauce.setSelectedIndex(index);
	            popup.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}
	
	public class CodeShower extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1071557484441601974L;

		public CodeShower(String text) {
			setLayout(new BorderLayout());
			setTitle("CodeShower");
			final JTextArea area = new JTextArea(text);
			area.setLineWrap(true);
			
			add(area, BorderLayout.CENTER);
			JPanel bottomMenu = new JPanel(new BorderLayout());
			JButton okay = new JButton("Close");
			okay.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					dispose();
				}
				
			});
			JButton copyToClipboard = new JButton("Copy to clipboard");
			copyToClipboard.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					setClipboard(area.getText());
				}
				
			});
			bottomMenu.add(okay, BorderLayout.WEST);
			bottomMenu.add(copyToClipboard, BorderLayout.CENTER);
			add(bottomMenu, BorderLayout.SOUTH);
			
			setMinimumSize(new Dimension(500, 300));
			setSize(new Dimension(500, 300));
			
			setVisible(true);
			
			area.setSelectionStart(0);
			area.setSelectionEnd(area.getText().length());
		}
	}

}
