import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import org.rsbot.bot.Bot;
import org.rsbot.script.Methods;

import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;


public class DevInterfaceExplorer extends DevPanel implements TreeSelectionListener {

    private Methods methods = Bot.methods;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1896222178889723654L;

	@Override
	public String getTabName() {
		return "Interfaces";
	}
	
	DefaultMutableTreeNode rootNode;
	DefaultTreeModel treeModel;
	JTree tree;
	
	JPanel infoArea;
	
	Rectangle highlightArea = null;
	
	private HashMap<Integer, String> knownUses = new HashMap<Integer, String>();
	
	enum SearchMode {
		All, TextOnly, ActionsOnly, BGColorOnly, CompIDOnly
	}
	
	SearchMode searchMode = SearchMode.TextOnly;

	@Override
	public void init() {
		setLayout(new BorderLayout());
		setKnownUses();
		
		JPanel top = new JPanel(new FlowLayout());
		JPanel center = new JPanel(new BorderLayout());
		
		top.add(new JLabel("Search text: "));
		
		final JTextField srchField = new JTextField(14);
		
		ActionListener searchAction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateInterfaces(srchField.getText());
			}
			
		};
		
		
		srchField.addActionListener(searchAction);
		top.add(srchField);
		
		DefaultComboBoxModel selectModel = new DefaultComboBoxModel();
		JComboBox searchModeSelect = new JComboBox(selectModel);
		searchModeSelect.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				searchMode = SearchMode.valueOf((String) arg0.getItem());
			}
			
		});
		
		for (SearchMode mode : SearchMode.values())
			selectModel.addElement(mode.name());
		
		searchModeSelect.setSelectedIndex(1);
		
		
		top.add(searchModeSelect);
		
		JButton searchBtn = new JButton("Search");
		
		searchBtn.addActionListener(searchAction);
		top.add(searchBtn);
		JButton updateBtn = new JButton("Update");
		updateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateInterfaces("");
			}
			
		});
		top.add(updateBtn);
		
		rootNode = new DefaultMutableTreeNode("Interfaces");
		treeModel = new DefaultTreeModel(rootNode);
		
		tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		
		infoArea = new JPanel();
		infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(infoArea);
		scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(280, 500));
		
		center.add(new JScrollPane(tree));
		center.add(scrollPane, BorderLayout.EAST);
		
		updateInterfaces("");
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		
		
	}
	
	public void updateInterfaces(String search) {
		tree.removeAll();
		
    	Object root = treeModel.getRoot();
    	while(!treeModel.isLeaf(root)) {
    		treeModel.removeNodeFromParent((MutableTreeNode)treeModel.getChild(root,0));
    	}
		
        for (final RSInterface face : methods.iface.getAll()) {
        	String knownUse = knownUses.get(face.getIndex());
        	DefaultMutableTreeNode ifParent = addObject("Interface " + face.getIndex() + " " + (knownUse == null ? "" : "("+knownUse+")"));
            for (final RSInterfaceChild child : face.getChildren()) {
            	if (!searchMatches(child, search))
            		continue;
            	DefaultMutableTreeNode cParent = addObject(ifParent, "Child " + child.getIndex(), false);
                for (final RSInterfaceChild component : child.getChildren()) {
                	if (!searchMatches(component, search))
                		continue;
                	addObject(cParent, "Component " + component.getIndex(), false);
                }
            }
        }
	}
	
    public boolean searchMatches(final RSInterfaceChild iface,
            final String contains) {
    	if (contains.isEmpty())
    		return true;
    	
    	boolean sAll = searchMode == SearchMode.All;
    		
    	if (sAll || searchMode == SearchMode.ActionsOnly) {
    		String[] act = iface.getActions();
    		if (act != null) {
    			for (String oa : act) {
        			if (oa != null && (oa.toLowerCase().contains(contains.toLowerCase()) || oa.equalsIgnoreCase(contains)))
        				return true;
        		}
    		}
    	}
    	if (sAll || searchMode == SearchMode.TextOnly) {
    		final String ifaceText = iface.getText();
    		if (ifaceText != null && (ifaceText.toLowerCase().contains(contains.toLowerCase()) || ifaceText.equalsIgnoreCase(contains)))
    			return true;
    	}
    	if (sAll || searchMode == SearchMode.BGColorOnly) {
    		// cheap way to compare integers
    		if ((iface.getBackgroundColor()+"").equals(contains.toLowerCase()))
    			return true;
    	}
    	if (sAll || searchMode == SearchMode.CompIDOnly) {
    		if ((iface.getChildID()+"").equals(contains.toLowerCase()))
    			return true;
    	}
    	
    	return false;
    }
    
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

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
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		Object ifa = pathToIFace(event.getPath());
		/*
		if (ifa instanceof RSInterfaceChild) {
			System.out.println("interface component");
		}
		else if (ifa instanceof RSInterfaceChild) {
			System.out.println("iface child");
		}
		else if (ifa != null) {
			System.out.println("iface");
		}
		*/
		//System.out.println();
		if (ifa != null) {
			RSInterfaceChild iface;
			if (ifa instanceof RSInterfaceChild) {
				
				iface = (RSInterfaceChild) ifa;
			}
			else if (ifa instanceof RSInterfaceChild) {
				iface = (RSInterfaceChild) ifa;
			}
			else {
				infoArea.removeAll();
				highlightArea = null;
				return;
			}
			
			highlightArea = iface.getArea();
				
			infoArea.removeAll();
			
			addInfo("Type : ", "" + iface.getType());
            addInfo("SpecialType : ", "" + iface.getSpecialType());
            addInfo("Bounds Array Index: ", "" + iface.getBoundsArrayIndex());
            addInfo("Model ID : ", "" + iface.getModelID());
            addInfo("Background color : ", "" + iface.getBackgroundColor());
            addInfo("Text : ", "" + iface.getText());
            addInfo("Tooltip : ", "" + iface.getTooltip());
            addInfo("Selected Action Name : ", "" + iface.getSelectedActionName());
            
            if (iface.getActions() != null) {
                String actions = "";
                for (final String action : iface.getActions()) {
                	if (action == null || action.isEmpty())
                		continue;
                    actions += action + ","; // TODO
                }
                addInfo("Actions : ", actions);
            }
            
            addInfo("Component ID : ", "" + iface.getChildID());
            addInfo("Component Stack Size : ", "" + iface.getChildStackSize());
            addInfo("XYZ Rotation : ", iface.getXRotation() + "x" + iface.getYRotation() + "x" + iface.getZRotation());
            addInfo("Text color : ", iface.getTextColor()+"");
            addInfo("Shadow color : ", iface.getShadowColor()+"");
            addInfo("Size : ", iface.getWidth() + "x" + iface.getHeight());
            addInfo("Absolute location : ", iface.getAbsoluteX() + "x" + iface.getAbsoluteY());
            addInfo("Border thickness : ", iface.getBorderThickness()+"");
            addInfo("Scrollbar data", "(size, thumbsize, thumbpos)");
            addInfo("Horizontal : ", iface.getScrollableContentWidth() + ", " + iface.getRealWidth() + ", " + iface.getHorizontalScrollPosition());
            addInfo("Vertical : ", iface.getScrollableContentHeight() + ", " + iface.getRealHeight() + ", " + iface.getVerticalScrollPosition());
            // horizontal has thumb position and vertical has position
            // TODO wat ^
            
            infoArea.validate();
            infoArea.repaint();
		}
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
	
	public int getObjectIndex(Object o) {
		if (!(o instanceof String))
			throw new NumberFormatException("Object not string");
		String s = (String) o;
		String[] toPieces = s.split(" ");
		if (toPieces.length < 2)
			throw new NumberFormatException("String didn't contain a space");
		return Integer.valueOf(toPieces[1]);
	}
	
	public Object pathToIFace(TreePath tp) {
		final int pathCount = tp.getPathCount();
		if (pathCount < 2 || pathCount > 4)
			return null;
		
		
		RSInterface face = methods.iface.get(getObjectIndex(tp.getPathComponent(1).toString()));
		RSInterfaceChild iChild = null;
		RSInterfaceChild iComp = null;
		
		if (pathCount >= 3) {
			iChild = face.getChild(getObjectIndex(tp.getPathComponent(2).toString()));
		}
		if (pathCount == 4) {
			iComp = iChild.getChildren()[getObjectIndex(tp.getPathComponent(3).toString())];
		}
		
		if (iComp != null)
			return iComp;
		
		if (iChild != null)
			return iChild;
		
		return face;
	}

	@Override
	public void cPaint(Graphics g) {
		if (highlightArea != null) {
			Color orange = Color.orange;
			g.setColor(orange);
			g.drawRect(highlightArea.x, highlightArea.y, highlightArea.width, highlightArea.height);
			g.setColor(new Color(orange.getRed(), orange.getGreen(), orange.getBlue(), 40));
			g.fillRect(highlightArea.x, highlightArea.y, highlightArea.width, highlightArea.height);
		}
	}
	public void setKnownUses() {
		
		/* Thanks to
		 *  Henry
		 *  Cherry
		 *  
		 *  for collection interface ids
		 */
		
		knownUses.put(17, "ItemsKeptOnDeath");
		knownUses.put(34, "Notes");		
		knownUses.put(79, "Summoning Pouch/Scroll Creation");		
		knownUses.put(137, "Chat");
		knownUses.put(149, "Inventory");
		knownUses.put(182, "Logout");
		knownUses.put(187, "Music");
		knownUses.put(190, "Quests");
		knownUses.put(192, "NormalMagics");
		knownUses.put(206, "PriceChecker");
		knownUses.put(230, "NPCTalkOptions");
		knownUses.put(232, "NPCTalkOptions");
		knownUses.put(234, "NPCTalkOptions");
		knownUses.put(241, "NPCTalk");
		knownUses.put(261, "Options");
		knownUses.put(271, "NormalPrayers"); // ? different for other prayer books
		knownUses.put(320, "Statistics");
		knownUses.put(398, "HouseOptions");		
		knownUses.put(387, "Equipment");
		knownUses.put(464, "Emotes");
		knownUses.put(548, "XPCounter");
		knownUses.put(550, "FriendsList");
		knownUses.put(590, "Emotes");
		knownUses.put(620, "Shop");
		knownUses.put(662, "Familiar");
		knownUses.put(667, "EquipmentBonus");
		knownUses.put(679, "Inventory(Tab)");
		knownUses.put(747, "OverallGame");
		knownUses.put(747, "SummoningIcon");
		knownUses.put(748, "HPIcon");
		knownUses.put(749, "QuickPrayers");
		knownUses.put(750, "RunIcon");
		knownUses.put(751, "ChatOptionBar");
		knownUses.put(754, "ExternalPrivateChat"); // ?
		knownUses.put(755, "Map");
		knownUses.put(762, "Bank");
		knownUses.put(880, "SummLeftOption");
		knownUses.put(884, "CombatStyles");
		knownUses.put(917, "TaskList");
		knownUses.put(982, "ChatSetup");
		knownUses.put(1055, "TaskSystem");
		knownUses.put(1056, "TaskSystem");
		knownUses.put(1096, "ClanSystem");
		knownUses.put(1109, "FriendChat");
		knownUses.put(1110, "ClanChat");
		knownUses.put(1139, "XP Lamp");
	}
	

}
