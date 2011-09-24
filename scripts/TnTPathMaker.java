import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSTile;



@ScriptManifest(authors = { "Gribonn" }, category = "Development", name = "TnT Path Maker", version = 1.0, description = "<html><head><style type='text/css'> body { background-color: #000000; } </style></head><body><div style='color: #FFFFFF;'>Path maker is in GUI</div></body></html>")
public class TnTPathMaker extends Script implements PaintListener {

	public String pathName = "PathName";
	public String beforeName = "public ";
	public String path = "";
	public RSTile previousTile;
	public boolean stopScript = false, recording = false;
	public RSTile Prevloc = new RSTile(0, 0);
	public TnTPathMakerGUI gui;
	public String[] tiles = {};
	public ArrayList<String> pathTiles = new ArrayList<String>();

	public boolean onStart(Map<String, String> args) {
		gui = new TnTPathMakerGUI();
		gui.setVisible(true);
		return true;
	}

	@Override
	public int loop() {
		if (stopScript)
			stopScript(false);
		if (previousTile != player.getMyLocation()) {
			final RSTile loc = player.getMyLocation();
			if (player.getMine() != null && loc != null) {
				gui.label5.setText("(" + loc.getX() + ", " + loc.getY() + ")");
				if (recording) {
					if (Prevloc.distanceTo(loc) >= 7) {
						addTile();
						Prevloc = loc;
					}
				}
			}
		}
		return 100;
	}

	public void refreshPath() {
		String pathInString = beforeName + "RSTile[] " + pathName + " = { ";
		int c = 0;
		if (tiles != null) {
			for (String tile : tiles) {
				if (tile != null) {
					if (c == 0) {
						pathInString = pathInString + tile;
					} else {
						pathInString = pathInString + ", " + tile;
					}
					c++;
				}
			}
		}
		pathInString = pathInString + " };";
		path = pathInString;
		gui.textArea1.setText(path);
	}

	public void addTile() {
		if (pathTiles.size() == 0 || pathTiles == null)
			pathTiles.add("new RSTile(" + player.getMyLocation().getX()
					+ ", " + player.getMyLocation().getY() + ")");
		else if (!pathTiles.get(pathTiles.size() - 1).equals(
				"new RSTile(" + player.getMyLocation().getX() + ", "
						+ player.getMyLocation().getY() + ")")) {
			pathTiles.add("new RSTile(" + player.getMyLocation().getX()
					+ ", " + player.getMyLocation().getY() + ")");
		}
		tiles = convertPath();
		refreshPath();
	}

	public void removeLast() {
		if (pathTiles.size() > 0) {
			pathTiles.remove(pathTiles.size() - 1);
		}
		tiles = convertPath();
		refreshPath();
	}

	public void clearPath() {
		pathTiles.clear();
		tiles = convertPath();
		refreshPath();
	}

	public void setNewPathName(String newName) {
		pathName = newName;
		refreshPath();
	}

	public String[] convertPath() {
		String[] ret;
		if (pathTiles.size() == 0) {
			ret = null;
		} else {
			ret = new String[pathTiles.size()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = pathTiles.get(i);
			}
		}
		return ret;
	}

	public void reverseThePath() {
		if (pathTiles != null) {
			ArrayList<String> reversedPath = new ArrayList<String>();
			for (int i = 0; i < pathTiles.size(); i++) {
				reversedPath.add(pathTiles.get(pathTiles.size() - i - 1));
			}
			pathTiles = reversedPath;
			tiles = convertPath();
			refreshPath();
		}
	}

	public void setRecord() {
		if (!recording) {
			gui.setTitle("TnT Path Maker (Recording)");
			gui.button1.setEnabled(false);
			gui.button2.setEnabled(false);
			gui.button3.setEnabled(false);
			gui.button4.setEnabled(false);
			gui.button5.setEnabled(false);
			gui.button6.setEnabled(false);
			gui.button7.setText("Stop");
			recording = true;
		} else {
			gui.setTitle("TnT Path Maker");
			gui.button1.setEnabled(true);
			gui.button2.setEnabled(true);
			gui.button3.setEnabled(true);
			gui.button4.setEnabled(true);
			gui.button5.setEnabled(true);
			gui.button6.setEnabled(true);
			Prevloc = new RSTile(0, 0);
			gui.button7.setText("Record");
			recording = false;
			addTile();
		}
	}

	public void toClipboard(String str) {
		StringSelection stringSelection = new StringSelection(str);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	public void makeDotsOnMinimap(Graphics g) {
		if (tiles != null) {
			for (String tile : tiles) {
				if (tile != null) {
					final String[] tileOnly = (tile.replace("new RSTile(", "")
							.replace(" ", "").replace(")", "")).split(",");
					final RSTile tileInTileFormat = new RSTile(Integer
							.parseInt(tileOnly[0]), Integer
							.parseInt(tileOnly[1]));
					final Point tileOnMinimap = tileInTileFormat.getMapLocation();
					if (tileOnMinimap.x != -1 && tileOnMinimap.y != -1) {
						g.setColor(Color.black);
						g.fillRect(tileOnMinimap.x + 1, tileOnMinimap.y + 1, 3,
								3);
						g.setColor(Color.red);
						g.fillRect(tileOnMinimap.x + 2, tileOnMinimap.y + 2, 1,
								1);
					}
				}
			}
		}
	}

	public void onRepaint(Graphics g) {
		makeDotsOnMinimap(g);
	}

	public class TnTPathMakerGUI extends JFrame {
		public static final long serialVersionUID = 1L;

		public TnTPathMakerGUI() {
			initComponents();
		}

		public void button1ActionPerformed(ActionEvent e) {
			addTile();
		}

		public void button2ActionPerformed(ActionEvent e) {
			removeLast();
		}

		public void button3ActionPerformed(ActionEvent e) {
			clearPath();
		}

		public void button4ActionPerformed(ActionEvent e) {
			toClipboard(textArea1.getText());
		}

		public void button5ActionPerformed(ActionEvent e) {
			final String bf = comboBox1.getSelectedItem().toString();
			if (!bf.equals("(none)"))
				beforeName = bf + " ";
			else
				beforeName = "";
			setNewPathName(textField1.getText());
		}

		public void thisWindowClosed(WindowEvent e) {
			stopScript = true;
		}

		public void button6ActionPerformed(ActionEvent e) {
			reverseThePath();
		}

		private void button7ActionPerformed(ActionEvent e) {
			setRecord();
		}

		public void initComponents() {
			// GEN-BEGIN:initComponents
			label1 = new JLabel();
			label2 = new JLabel();
			label3 = new JLabel();
			label5 = new JLabel();
			label6 = new JLabel();
			button1 = new JButton();
			button2 = new JButton();
			button3 = new JButton();
			button4 = new JButton();
			button5 = new JButton();
			button6 = new JButton();
			button7 = new JButton();
			textField1 = new JTextField();
			scrollPane1 = new JScrollPane();
			textArea1 = new JTextArea();
			comboBox1 = new JComboBox();

			// ======== this ========
			setTitle("TnT Path Maker");
			setAlwaysOnTop(true);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setResizable(false);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					thisWindowClosed(e);
				}
			});
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ---- label1 ----
			label1.setText("T  T Path Maker");
			label1.setFont(new Font("Segoe Script",
					label1.getFont().getStyle(),
					label1.getFont().getSize() + 13));
			contentPane.add(label1);
			label1.setBounds(new Rectangle(new Point(10, 10), label1
					.getPreferredSize()));

			// ---- label2 ----
			label2.setText("n");
			label2.setFont(new Font("Segoe Script",
					label2.getFont().getStyle(),
					label2.getFont().getSize() + 13));
			label2.setForeground(Color.red);
			contentPane.add(label2);
			label2.setBounds(new Rectangle(new Point(30, 10), label2
					.getPreferredSize()));

			// ---- label3 ----
			label3.setText("Current tile :");
			label3.setFont(new Font("Segoe Script",
					label3.getFont().getStyle(), label3.getFont().getSize()));
			contentPane.add(label3);
			label3.setBounds(new Rectangle(new Point(285, 20), label3
					.getPreferredSize()));

			// ---- label5 ----
			label5.setText("(00000, 00000)");
			label5.setFont(new Font("Segoe Script",
					label5.getFont().getStyle(), label5.getFont().getSize()));
			contentPane.add(label5);
			label5.setBounds(new Rectangle(new Point(365, 20), label5
					.getPreferredSize()));

			// ---- label6 ----
			label6.setText("Path :");
			label6.setFont(new Font("Segoe Script",
					label6.getFont().getStyle(), label6.getFont().getSize()));
			contentPane.add(label6);
			label6.setBounds(new Rectangle(new Point(15, 45), label6
					.getPreferredSize()));

			// ---- button1 ----
			button1.setText("Add Tile");
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button1ActionPerformed(e);
				}
			});
			contentPane.add(button1);
			button1.setBounds(405, 65, 95, button1.getPreferredSize().height);

			// ---- button2 ----
			button2.setText("Remove Last");
			button2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button2ActionPerformed(e);
				}
			});
			contentPane.add(button2);
			button2.setBounds(405, 90, 95, button2.getPreferredSize().height);

			// ---- button3 ----
			button3.setText("Clear");
			button3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button3ActionPerformed(e);
				}
			});
			contentPane.add(button3);
			button3.setBounds(405, 115, 95, button3.getPreferredSize().height);

			// ---- button4 ----
			button4.setText("Copy");
			button4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button4ActionPerformed(e);
				}
			});
			contentPane.add(button4);
			button4.setBounds(405, 140, 95, button4.getPreferredSize().height);

			// ---- button5 ----
			button5.setText("Save");
			button5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button5ActionPerformed(e);
				}
			});
			contentPane.add(button5);
			button5.setBounds(345, 190, 57, 23);

			// ---- button6 ----
			button6.setText("Reverse");
			button6.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button6ActionPerformed(e);
				}
			});
			contentPane.add(button6);
			button6.setBounds(405, 165, 95, button6.getPreferredSize().height);

			// ---- button7 ----
			button7.setText("Record");
			button7.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button7ActionPerformed(e);
				}
			});
			contentPane.add(button7);
			button7.setBounds(405, 190, 95, 23);

			// ---- textField1 ----
			textField1.setText("PathName");
			textField1.setForeground(Color.red);
			textField1.setFont(new Font("Monospaced", Font.PLAIN, 11));
			contentPane.add(textField1);
			textField1.setBounds(145, 190, 195, 23);

			// ======== scrollPane1 ========
			{

				// ---- textArea1 ----
				textArea1.setEditable(false);
				textArea1.setFont(textArea1.getFont().deriveFont(
						textArea1.getFont().getSize() + 2f));
				textArea1.setLineWrap(true);
				textArea1.setForeground(Color.red);
				textArea1.setText("public RSTile[] PathName = {  };");
				scrollPane1.setViewportView(textArea1);
			}
			contentPane.add(scrollPane1);
			scrollPane1.setBounds(15, 65, 385, 120);

			// ---- comboBox1 ----
			comboBox1.setModel(new DefaultComboBoxModel(new String[] {
					"public", "private", "final", "(none)" }));
			contentPane.add(comboBox1);
			comboBox1.setBounds(15, 190, 125, 23);

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width,
							preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height,
							preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			setSize(515, 245);
			setLocationRelativeTo(getOwner());
			// GEN-END:initComponents
		}

		// GEN-BEGIN:variables
		public JLabel label1;
		public JLabel label2;
		public JLabel label3;
		public JLabel label5;
		public JLabel label6;
		public JButton button1;
		public JButton button2;
		public JButton button3;
		public JButton button4;
		public JButton button5;
		public JButton button6;
		public JButton button7;
		public JTextField textField1;
		public JScrollPane scrollPane1;
		public JTextArea textArea1;
		public JComboBox comboBox1;
		// GEN-END:variables
	}
}