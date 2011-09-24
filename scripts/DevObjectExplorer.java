import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSObjectDef;
import org.rsbot.script.wrappers.RSTile;


public class DevObjectExplorer extends DevPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1083445280491710611L;
	
	Script s;
	DefaultMutableTreeNode rootNode;
	DefaultTreeModel treeModel;
	JPanel infoArea;
	JTree list;
	JButton updp;
	
	@Override
	public void init(Script s) {
		this.s = s;
		
		setLayout(new BorderLayout());
		
		JPanel sidebar = new JPanel(new BorderLayout());
		
		JPanel controls = new JPanel(new BorderLayout());
		
		JButton upd = new JButton("Update object list");
		upd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
			
		});
		controls.add(upd, BorderLayout.NORTH);
		
		JPanel specificTileFinder = new JPanel(new FlowLayout());
		final JFormattedTextField xField = new JFormattedTextField();
		xField.setColumns(4);
		xField.setValue(new Integer(0));
		
		final JFormattedTextField yField = new JFormattedTextField();
		yField.setColumns(4);
		yField.setValue(new Integer(0));
		
		specificTileFinder.add(new JLabel("x:"));
		specificTileFinder.add(xField);
		specificTileFinder.add(new JLabel("y:"));
		specificTileFinder.add(yField);
		JButton findBtn = new JButton("Find");
		findBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int x = (Integer) xField.getValue();
				int y = (Integer) yField.getValue();
				if (x > 0 && y > 0) {
					findObjAt(new RSTile(x, y));
				}
			}
			
		});
		specificTileFinder.add(findBtn);
		
		sidebar.add(specificTileFinder, BorderLayout.SOUTH);
		sidebar.add(controls, BorderLayout.NORTH);
		
		infoArea = new JPanel();
		infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
		
		updp = new JButton("Update selected object");
		updp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateSelectedObject();
			}
			
		});
		
		sidebar.add(infoArea, BorderLayout.CENTER);
		
		rootNode = new DefaultMutableTreeNode("Objects");
		treeModel = new DefaultTreeModel(rootNode);
		
		list = new JTree(treeModel); 
		list.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				updateSelectedObject();
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
		
		Object root = treeModel.getRoot();
    	while(!treeModel.isLeaf(root)) {
    		treeModel.removeNodeFromParent((MutableTreeNode)treeModel.getChild(root,0));
    	}
    	
    	DefaultMutableTreeNode interactableNode = addObject("Interactable");
    	DefaultMutableTreeNode floorDecorationNode = addObject("Floor decoration");
    	DefaultMutableTreeNode boundaryNode = addObject("Boundary");
    	DefaultMutableTreeNode wallDecorationNode = addObject("Wall decoration");
		
		int locX = s.player.getMine().getLocation().getX();
		int locY = s.player.getMine().getLocation().getY();
		
		for (int x = locX - 25; x < locX + 25; x++) {
			for (int y = locY - 25; y < locY + 25; y++) {
				final RSObject[] objects = s.objects.getAt(x, y);
				if (objects != null) {
					for (final RSObject object : objects) {
						if (object == null || object.getID() <= 0) {
							continue;
						}
						
						RSObjectDef def = object.getDef();
						
						String name = (def == null ? "null" : def.getName()) + " (" + object.getID() + ") " + object.getLocation();
						
						int type = object.getType();
						
						if (type == 0)
							addObject(interactableNode, name, false);
						else if (type == 1)
							addObject(floorDecorationNode, name, false);
						else if (type == 2)
							addObject(boundaryNode, name, false);
						else if (type == 3)
							addObject(wallDecorationNode, name, false);
						else
							System.out.println("unknown obj " + object.getID());
					}
				}
			}
		}
		
	}
	
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = list.getSelectionPath();

        if (parentPath == null) {
            //There is no selection. Default to the root node.
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);

        treeModel.insertNodeInto(childNode, parent,
                                 parent.getChildCount());

        //Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            list.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }
	
	public RSObject findObject(String name) {
		int lookForId = -1;
		RSTile lookFrom = null;
		if (name.contains(" (")) {
			lookForId = Integer.valueOf(name.split(" \\(")[1].split("\\)")[0]);
			String[] two = name.split("\\(")[2].split("\\)")[0].split(", ");
			lookFrom = new RSTile(Integer.valueOf(two[0]), Integer.valueOf(two[1]));
			name = name.substring(0, name.indexOf(" ("));
		}
		
		if (lookFrom != null) {
			for (RSObject r : s.objects.getAt(lookFrom)) {
				if (r.getID() == lookForId)
					return r;
			}
		}
		
		int locX = s.player.getMine().getLocation().getX();
		int locY = s.player.getMine().getLocation().getY();
		
		for (int x = locX - 25; x < locX + 25; x++) {
			for (int y = locY - 25; y < locY + 25; y++) {
				final RSObject[] objects = s.objects.getAt(x, y);
				if (objects != null) {
					for (final RSObject object : objects) {
						if (object == null || object.getID() <= 0) {
							continue;
						}
						
						RSObjectDef def = object.getDef();
						
						String dname = (def == null ? "null" : def.getName());
						
						if ((lookForId == -1 || lookForId == object.getID()) && (dname.equals("null") || dname.equals(name)))
							return object;
					}
				}
			}
		}
		return null;
	}
	
	RSObject selectedObject = null;
	
	public void updateSelectedObject() {
		TreePath tp = list.getSelectionPath();
		if (tp == null)
			return;
		RSObject object = findObject(tp.getLastPathComponent().toString());
		if (object == null)
			return;
		
		updateOnObject(object);
	}
	
	public void findObjAt(RSTile r) {
		RSObject obj = s.objects.getTopAt(r);
		if (obj != null)
			updateOnObject(obj);
	}
	
	public void updateOnObject(RSObject object) {
		infoArea.removeAll();
		
		RSObjectDef def = object.getDef();
		
		if (def == null) {
			addInfo("Definition : ", "Null");
		}
		else {
			addInfo("Name : ", def.getName());
			
			String[] aacts = def.getActions();
			
			if (aacts != null) {
				String allActions = "";
				for (String act : aacts)
					if (act != null && !act.isEmpty())
					allActions += act + ",";
				
				addInfo("Actions : ", allActions);
			}
			
			int[] cids = def.getChildIDs();
			
			if (cids != null) {
				String allChildIds = "";
				for (int id : cids)
					allChildIds += id + ",";
				
				addInfo("Child ids : ", allChildIds);
			}
		}
		
		addInfo("Id : ", object.getID()+"");
		addInfo("Location : ", object.getLocation()+"");
		addInfo("Type : ", object.getType()+"");
		
		selectedObject = object;
		
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
		return "Object explorer";
	}
	
	@Override
	public void cPaint(Graphics g) {
		if (selectedObject != null) {
			Point mmPoint = s.tile.toMiniMap(selectedObject.getLocation());
			if (mmPoint.x != -1 && mmPoint.y != -1) {
				g.setColor(Color.red);
				g.drawLine(mmPoint.x-5, mmPoint.y, mmPoint.x+5, mmPoint.y);
				g.drawLine(mmPoint.x, mmPoint.y-5, mmPoint.x, mmPoint.y+5);
				
				g.setColor(Color.orange);
				g.fillRect(mmPoint.x-3, mmPoint.y-3, 6, 6);
			}
			g.setColor(Color.pink);
			selectedObject.drawModel(g);

		}
	}

}
