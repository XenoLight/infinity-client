package org.rsbot.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.rsbot.bot.Bot;
import org.rsbot.script.DevManifest;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptHandler;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.internal.event.ScriptListener;
import org.rsbot.script.internal.util.Global;
import org.rsbot.script.internal.util.UncachedClassLoader;
import org.rsbot.util.GlobalConfiguration;

/**
 * This will handle the selection process of scripts.<br>
 * Fully implemented searching using MergeSort and various parameters.<br>
 * 
 * @author Fusion89k
 */
public class ScriptSelector extends JDialog implements ActionListener,
TreeSelectionListener, ScriptListener {
	protected final Logger log = Logger.getLogger(this.getClass().getName());

	public class Searcher implements KeyListener, FocusListener {

		@Override
		public void focusGained(final FocusEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void focusLost(final FocusEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyPressed(final KeyEvent arg0) {
			final String tb = quickSearch.getText();
			if (tb.isEmpty()) {
				return;
			}
			jTreeSearch(tb);
		}

		@Override
		public void keyReleased(final KeyEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyTyped(final KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
	}
	/**
	 * 
	 * Callback of loadScripts().
	 * 
	 * @author wolf
	 */
	public interface SLListener {

		void scriptLoaded(String name, ScriptManifest sm);
	}

	/**
	 * The different parameters to search by. Information loaded from
	 * ScriptManifest
	 */
	private enum TYPE {

		AUTHOR, CATEGORY, VERSION, NAME, EMAIL, WEBSITE
	}

	static ImageIcon moreButtons = null;

	static ImageIcon lessButtons = null;
	static {
		ImageIcon icon = null;
		icon = new ImageIcon(
				GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.UPARROW));
		moreButtons = icon;
		icon = new ImageIcon(
				GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.DOWNARROW));
		lessButtons = icon;
	}

	private static final long serialVersionUID = 5475451138208522510L;

	/**
	 * Randomly generated category for DevManifest classes. Used to make sure no
	 * one uses it with ScriptManifest
	 */
	private static final String DevManifestCategory = "DM" + Math.random();

	/**
	 * Enables singleton
	 * 
	 * @param gui
	 *            BotGUI from which to run the script
	 * @return Single instance of ScriptSelector
	 */
	public static ScriptSelector getInstance(final BotGUI gui) {
		if (ScriptSelector.instance == null) {
			ScriptSelector.instance = new ScriptSelector(gui);
		}
		return ScriptSelector.instance;
	}

	/**
	 * Loads all of the scripts along with their manifests
	 * 
	 * @param callback
	 *            if not null, called everytime a new script is loaded
	 * @return a mapping of the script names and manifests
	 */
	public static LinkedHashMap<String, ScriptManifest> loadScripts(
			final SLListener callback) {
		final LinkedHashMap<String, ScriptManifest> scripts = new LinkedHashMap<String, ScriptManifest>();
		UncachedClassLoader loader;
		final ArrayList<String> paths = new ArrayList<String>(2);
		/* Add documents script directory */
		paths.add(GlobalConfiguration.Paths.getScriptsDirectory());

		/* Add documents pre-compiled script directory */
		paths.add(GlobalConfiguration.Paths.getScriptsPrecompiledDirectory());

		/* Add all jar files in the pre-compiled scripts directory */
		final File psdir = new Global(
				GlobalConfiguration.Paths.getScriptsPrecompiledDirectory());
		if (psdir.exists()) {
			for (final File file : psdir.listFiles()) {
				if (file.getName().endsWith(".jar!")) {
					paths.add(file.getPath());
				}
			}
		}
		if (!GlobalConfiguration.RUNNING_FROM_JAR) {
			final String rel = "." + File.separator
			+ GlobalConfiguration.Paths.SCRIPTS_NAME_SRC;
			paths.add(rel);
		} else {
			/* Generate the path of the scripts folder in the jar */
			final URL version = GlobalConfiguration.class.getClassLoader()
			.getResource(GlobalConfiguration.Paths.Resources.VERSION);
			String p = version
			.toString()
			.replace("jar:file:", "")
			.replace(GlobalConfiguration.Paths.Resources.VERSION,
					GlobalConfiguration.Paths.SCRIPTS);
			try {
				p = URLDecoder.decode(p, "UTF-8");
			} catch (final UnsupportedEncodingException ignored) {
			}
			paths.add(p);
		}

		/* Loop through paths to find scripts */
		for (final String path : paths) {
			final Global dir = new Global(path);
			if (!dir.exists()) {
				continue;
			}
			String url;
			try {
				url = dir.toURI().toURL().toString();
			} catch (final MalformedURLException e) {
				continue;
			}
			loader = new UncachedClassLoader(url,
					UncachedClassLoader.class.getClassLoader());
			for (final File file : dir.listFiles()) {
				String name = file.getName();
				final String ext = ".class";
				if (name.endsWith(ext) && !name.contains("$")
						&& !scripts.containsKey(name)) {
					try {
						name = name.substring(0, name.length() - ext.length());
						final Class<?> cls = loader.loadClass(name);

						if (cls.isAnnotationPresent(ScriptManifest.class)) {
							final ScriptManifest os = scripts.get(name);
							final ScriptManifest ns = cls
							.getAnnotation(ScriptManifest.class);
							if ((os != null) && (ns != null)
									&& (os.version() >= ns.version())) {
								continue;
							}

							scripts.put(name,
									cls.getAnnotation(ScriptManifest.class));

							if (callback != null) {
								callback.scriptLoaded(name,
										cls.getAnnotation(ScriptManifest.class));
							}
						} else if (cls.isAnnotationPresent(DevManifest.class)) {
							// to allow accessing from devClassManifest
							final String className = name;

							// Automatically generated manifest for scripts
							// without it. Should allow faster debug script
							// writing etc..
							final ScriptManifest devClassManifest = new ScriptManifest() {

								@Override
								public Class<? extends Annotation> annotationType() {
									return null;
								}

								@Override
								public String[] authors() {
									return new String[] { "Runedev" };
								}

								@Override
								public String category() {
									return DevManifestCategory;
								}

								@Override
								public String description() {
									final String desc = "Automatically generated ScriptManifest for DevManifest class \""
										+ className + "\"";
									return desc;
								}

								@Override
								public String email() {
									return "";
								}

								@Override
								public String name() {
									return className;
								}

								@Override
								public String notes() {
									return "";
								}

								@Override
								public String summary() {
									return "";
								}

								@Override
								public double version() {
									return 1.0;
								}

								@Override
								public String website() {/*
								 * return
								 * GlobalConfiguration
								 * .Paths.URLs.SITE;
								 */
									return "";
								}
							};

							scripts.put(name, devClassManifest);

							if (callback != null) {
								callback.scriptLoaded(name,
										cls.getAnnotation(ScriptManifest.class));
							}
						}
					} catch (final Exception e) {
					}
				}
			}
		}
		return scripts;
	}
	public static void main(final String[] args) {
		new ScriptSelector(null).showSelector();
	}
	private final String defaultText = "<html><body style='padding: 10px; text-align: center;'>"
		+ "This Script Does Not Have A Description</body></html>";
	private LinkedHashMap<String, ScriptManifest> scriptList;
	private JTree tree;
	@SuppressWarnings("unused")
	private Bot bot; // TODO
	private final BotGUI gui;
	private JPanel rightPane;
	private JPanel leftPane;
	private JTextField quickSearch;
	private JButton okay;
	private JButton site;
	private JButton extend;
	private JButton detailsToggle;
	private JComboBox accounts;
	private JTextPane description;
	private JScrollPane scrollDescription;
	private JScrollPane scroll;
	private JTextPane sDetails;

	private JSplitPane splits;

	private static ScriptSelector instance;

	final Dimension regularSize = new Dimension(350, 360);

	final Dimension extendedSize = new Dimension(350, 325);

	boolean detailsVisible = false;

	boolean extended = false;

	/**
	 * Private allows for singleton
	 * 
	 * @param parent
	 *            BotGUI from which to run the script.
	 */
	private ScriptSelector(final BotGUI parent) {
		super(parent, "Infinity Script Selector", true);
		gui = parent;
	}

	/**
	 * Handles all of the actions that take place.
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		if (event.getSource() instanceof JRadioButton) {/* Sort Paramters */
			final JPanel pane = (JPanel) ((JRadioButton) event.getSource()).getParent();
			TYPE t = null;
			boolean asc = false;
			JComboBox box = null;
			Object index = null;
			for (final Component c : pane.getComponents()) {
				if ((c instanceof JRadioButton) && (t == null)) {
					final JRadioButton r = (JRadioButton) c;
					if (r.getText().equals("Ascending")) {
						asc = r.isSelected();
					} else if (!r.getText().contains("ing") && r.isSelected()) {
						t = TYPE.valueOf(r.getText().toUpperCase());
					}
				} else if (c instanceof JComboBox) {
					box = (JComboBox) c;
					pane.remove(c);
				}
			}
			final String[] scripts = sort(getScriptsNames(), t, asc);
			if (t.equals(TYPE.CATEGORY) || t.equals(TYPE.AUTHOR)) {
				if (box != null) {
					index = box.getSelectedItem();
				}
				final String[] types = getMappingOfType(scripts, t).keySet()
				.toArray(new String[0]);
				final String[] all = new String[types.length + 1];
				all[0] = "All";
				System.arraycopy(types, 0, all, 1, types.length);
				box = new JComboBox(all);
				box.setName(t.toString() + (asc ? "A" : "D"));
				box.setSelectedIndex(0);
				box.addActionListener(this);
				if (index == null) {
					index = box.getSelectedItem();
				}
			}
			if (index == null) {
				remakeTree(scripts, t, asc);
				resetTree();
				resetRightSide();
			} else {
				box.setSelectedItem(index);
				resetRightSide();
			}

		} else if (event.getSource() instanceof JComboBox) {/* Filter Parameters */
			final JComboBox box = (JComboBox) event.getSource();
			final String name = box.getName();
			final TYPE t = TYPE.valueOf(name.substring(0, name.length() - 1));
			final boolean asc = name.charAt(name.length() - 1) == 'A';
			String[] scripts = sort(getScriptsNames(), t, asc);
			final LinkedHashMap<String, ArrayList<String>> map = getMappingOfType(
					scripts, t);
			for (final String s : map.keySet()) {
				if (s.equals(box.getSelectedItem())) {
					scripts = map.get(s).toArray(new String[0]);
				}
			}
			remakeTree(scripts, t, asc);
		} else if (event.getSource() instanceof JButton) {/* OK button */
			final String command = event.getActionCommand().intern();
			if ("Author Site".equals(command)) {

				final String url = findUrlForSelectedScript();

				if (!url.isEmpty()) {
					openUrl(url);
				}

			} else if ("Details".equals(command)) {

				toggleDetails();

			} else if ("OK".equals(command)) {
				if (getScript() == null) {
					JOptionPane.showMessageDialog(this,
							"Please select a script!", "Error!", 2);
				} else {
					gui.runScript(accounts.getSelectedItem().toString(),
							getScript(), getArguments());
					dispose();
				}
			}
		}
	}

	/**
	 * Compare function used when sorting
	 */
	private int compare(final String one, final String two, final TYPE t) {
		switch (t) {
		case NAME:
			return scriptList.get(one).name().toLowerCase()
			.compareTo(scriptList.get(two).name().toLowerCase());
		case CATEGORY:
			return scriptList.get(one).category()
			.compareTo(scriptList.get(two).category());
		case VERSION:
			return new Double(scriptList.get(one).version())
			.compareTo(scriptList.get(two).version());
		case WEBSITE:
			return scriptList.get(one).website()
			.compareTo(scriptList.get(two).website());
		case EMAIL:
			return scriptList.get(one).email()
			.compareTo(scriptList.get(two).email());
		default:
			return scriptList.get(one).authors()[0].toLowerCase().compareTo(
					scriptList.get(two).authors()[0].toLowerCase());
		}
	}

	public void extend() {
		extended = !extended;
		resetRightSide();
	}

	/**
	 * 
	 * Gets ScriptManifest of selected script. Returns null if not found
	 * 
	 * @return ScriptManifest of select Script
	 */
	public ScriptManifest findSelectedScriptManifest() {
		final TreePath selectPath = tree.getSelectionPath();
		if (selectPath == null) {
			return null;
		}
		final String name = getScriptForName(selectPath.getLastPathComponent()
				.toString());

		return scriptList.get(name);
	}

	/**
	 * 
	 * Gets url of selected script. "" if not found/null
	 * 
	 * @return Url as String
	 */
	public String findUrlForSelectedScript() {
		final ScriptManifest mf = findSelectedScriptManifest();
		if (mf == null) {
			return "";
		}
		return mf.website();
	}

	/**
	 * Returns the arguments in a HashMap
	 * 
	 * @return HashMap of the arguments
	 */
	private Map<String, String> getArguments() {
		final Document doc = description.getDocument();
		final Map<String, String> args = new HashMap<String, String>();
		for (final Element elem : doc.getRootElements()) {
			getArguments(args, elem);
		}
		return args;
	}

	/**
	 * Helper method for getArguments()
	 */
	private void getArguments(final Map<String, String> args, final Element elem) {
		final int len = elem.getElementCount();
		if (elem.getName().equalsIgnoreCase("input")
				|| elem.getName().equalsIgnoreCase("select")) {
			final AttributeSet as = elem.getAttributes();
			final Object model = as.getAttribute(StyleConstants.ModelAttribute);
			final String name = as.getAttribute(HTML.Attribute.NAME).toString();
			if (model instanceof PlainDocument) {
				final PlainDocument pd = (PlainDocument) model;
				String value = null;
				try {
					value = pd.getText(0, pd.getLength());
				} catch (final BadLocationException e) {
				}
				args.put(name, value);
			} else if (model instanceof ToggleButtonModel) {
				final ToggleButtonModel buttonModel = (ToggleButtonModel) model;
				if (!args.containsKey(name)) {
					args.put(name, null);
				}
				if (buttonModel.isSelected()) {
					args.put(name, as.getAttribute(HTML.Attribute.VALUE)
							.toString());
				}
			} else if (model instanceof DefaultComboBoxModel) {
				args.put(name, ((DefaultComboBoxModel) model).getSelectedItem()
						.toString());
			} else {
				throw new Error("Unknown model [" + model.getClass().getName()
						+ "]");
			}
		}
		for (int i = 0; i < len; i++) {
			final Element e = elem.getElement(i);
			getArguments(args, e);
		}
	}
	/**
	 * Access the different types of scripts based on the parameters
	 * 
	 * @param scripts
	 *            Script names array
	 * @param t
	 *            TYPE to distinguish by
	 * @return A mapping of the types and the scripts that are of that type
	 */
	private LinkedHashMap<String, ArrayList<String>> getMappingOfType(
			final String[] scripts, final TYPE t) {
		final LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
		for (String s : scripts) {
			String key = s;
			s = getScriptForName(s);
			switch (t) {
			case CATEGORY:
				key = scriptList.get(s).category();
				break;
			case VERSION:
				key = scriptList.get(s).version() + "";
				break;
			case AUTHOR:
				key = scriptList.get(s).authors()[0];
				break;
			case WEBSITE:
				key = scriptList.get(s).website();
				break;
			case EMAIL:
				key = scriptList.get(s).email();
				break;
			}
			ArrayList<String> temp = map.get(key);
			if (temp != null) {
				temp.add(s);
			} else {
				temp = new ArrayList<String>();
				temp.add(s);
			}
			map.put(key, temp);
		}
		return map;
	}

	/**
	 * Will load the and return the selected Script
	 * 
	 * @return Script that is selected
	 */
	private Script getScript() {
		final TreePath selectPath = tree.getSelectionPath();
		if (selectPath == null) {
			return null;
		}
		final String name = getScriptForName(selectPath.getLastPathComponent()
				.toString());
		if (scriptList.get(name) == null) {
			return null;
		}
		/*
		 * just try all the loaders TODO store which loader was used to load the
		 * script info
		 * 
		 * Waterwolf edit: next time, please tell why you want to store the
		 * loader so we actually have a reason to implement it thx
		 */
		Class<?> newest = null;
		for (final UncachedClassLoader loader : ScriptHandler.getLoaders()) {
			try {
				final Class<?> cls = loader.loadClass(name);
				if (cls.isAnnotationPresent(ScriptManifest.class)
						&& (newest != null)) {
					final ScriptManifest os = newest
					.getAnnotation(ScriptManifest.class);
					final ScriptManifest ns = cls
					.getAnnotation(ScriptManifest.class);
					if ((os != null) && (ns != null)
							&& (os.version() >= ns.version())) {
						continue;
					}
				}

				newest = cls;
			} catch (final Exception ignored) {
			}
		}

		try {
			if (newest != null) {
				return newest.asSubclass(Script.class).newInstance();
			}
		} catch (final Exception ex) {
			log.severe(ex.toString());
			
			StackTraceElement[] trace = ex.getStackTrace();
			for (StackTraceElement t : trace)  {
				log.severe(t.toString());
			}
		}

		return null;
	}

	/**
	 * Will access the ClassFile name of the given annotation name
	 * 
	 * @param name
	 *            Name as it appears on the ScriptManifest
	 * @return Name of the ClassFile
	 */
	private String getScriptForName(final String name) {

		if (scriptList.get(name) != null) {
			return name;
		}
		for (final String s : scriptList.keySet()) {
			if (scriptList.get(s).name().equals(name)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Will access the name of the scripts from the ScriptManifest
	 * 
	 * @return Array of all the script Names
	 */
	private String[] getScriptsNames() {
		final LinkedList<String> names = new LinkedList<String>();
		for (final String s : scriptList.keySet()) {
			names.add(scriptList.get(s).name());

		}
		return names.toArray(new String[names.size()]);
	}

	@Override
	public void inputChanged(final Bot bot, final int mask) {
	}

	public void jTreeSearch(final String text) {
		final TreePath match = tree.getNextMatch(text, 0, Position.Bias.Forward);
		if (match == null) {
			return;
		}
		tree.scrollPathToVisible(match);
		tree.setSelectionPath(match);
		// TODO
	}

	/**
	 * Helper method for sort().
	 */
	private String[] merge(final String[] left, final String[] right,
			final TYPE t, final boolean asc) {
		final String[] result = new String[left.length + right.length];
		int leftIndex = 0, rightIndex = 0, resultIndex = 0;
		while ((leftIndex < left.length) && (rightIndex < right.length)) {
			if (compare(getScriptForName(left[leftIndex]),
					getScriptForName(right[rightIndex]), t) * (asc ? 1 : -1) < 0) {
				result[resultIndex] = left[leftIndex];
				leftIndex++;
			} else {
				result[resultIndex] = right[rightIndex];
				rightIndex++;
			}
			resultIndex++;
		}
		String[] rest;
		int restIndex;
		if (leftIndex >= left.length) {
			rest = right;
			restIndex = rightIndex;
		} else {
			rest = left;
			restIndex = leftIndex;
		}
		for (int i = restIndex; i < rest.length; i++) {
			result[resultIndex] = rest[i];
			resultIndex++;
		}
		return result;
	}

	/**
	 * Attempts to open URL using user's default browser. Asks for confirmation
	 * before opening
	 * 
	 * @param url
	 */
	public void openUrl(final String url) {

		final int confirmation = JOptionPane
		.showConfirmDialog(
				this,
				"Author's site is "
				+ url
				+ "\n\nMake sure that the link above doesn't lead to a phishing/malicious site before clicking OK.\nThe official home for "
				+ GlobalConfiguration.NAME + " is "
				+ GlobalConfiguration.Paths.URLs.SITE, "",
				JOptionPane.OK_CANCEL_OPTION);
		if (confirmation == JOptionPane.CANCEL_OPTION) {
			return;
		}

		if (!java.awt.Desktop.isDesktopSupported()) {
			System.err.println("Desktop is not supported (fatal)");
			System.exit(1);
		}
		final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			System.err
			.println("Desktop doesn't support the browse action (fatal)");
			System.exit(1);
		}

		try {
			final java.net.URI uri = new java.net.URI(url);
			desktop.browse(uri);
		} catch (final Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Update the JTree information
	 */
	private void remakeTree(final String[] scripts, final TYPE t,
			final boolean asc) {
		final LinkedHashMap<String, ArrayList<String>> list = getMappingOfType(
				scripts, t);
		if (!t.equals(TYPE.NAME)) {
			final DefaultMutableTreeNode root = new DefaultMutableTreeNode(
			"Root");

			// DevManifest stuff

			if (list.containsKey(DevManifestCategory)) {
				final DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				"DevManifest");
				if (!asc) {
					for (int i = list.get(DevManifestCategory).size() - 1; i >= 0; i--) {
						top.add(new DefaultMutableTreeNode(scriptList.get(
								list.get(DevManifestCategory).get(i)).name()));
					}
				} else {
					for (final String name : list.get(DevManifestCategory)) {
						top.add(new DefaultMutableTreeNode(scriptList.get(name)
								.name()));
					}
				}
				root.add(top);

				list.remove(DevManifestCategory);
			}

			for (final String s : list.keySet()) {

				final DefaultMutableTreeNode top = new DefaultMutableTreeNode(s);
				if (!asc) {
					for (int i = list.get(s).size() - 1; i >= 0; i--) {
						top.add(new DefaultMutableTreeNode(scriptList.get(
								list.get(s).get(i)).name()));
					}
				} else {
					for (final String name : list.get(s)) {
						top.add(new DefaultMutableTreeNode(scriptList.get(name)
								.name()));
					}
				}
				root.add(top);
			}
			tree = new JTree(root);
		} else {
			tree = new JTree(sort(getScriptsNames(), t, asc));
		}

		tree.addTreeSelectionListener(this);
		resetTree();
	}

	/**
	 * Will redisplay the right side of the splitPane. Used when switching
	 * between search and display
	 * 
	 * @param buttons
	 *            True if you want buttons on the pane
	 * @param box
	 *            True if you want the comboBox on the pane
	 * @param cbox
	 *            The comboBox to put on the pane or null if no box.
	 */
	private void resetRightSide() {

		extend.setText((extended ? "Less Detail" : "More Detail"));
		extend.setIcon((extended ? lessButtons : moreButtons));

		final JPanel rightstuff = new JPanel(new BorderLayout());

		rightPane.removeAll();

		if (extended) {
			sDetails.setPreferredSize(extendedSize);
			scrollDescription.setPreferredSize(extendedSize);
		} else {
			sDetails.setPreferredSize(regularSize);
			scrollDescription.setPreferredSize(regularSize);
		}

		if (detailsVisible) {
			final JScrollPane dPane = new JScrollPane(sDetails);
			dPane.setBorder(null);
			rightstuff.add(dPane, BorderLayout.NORTH);
		} else {
			rightstuff.add(scrollDescription, BorderLayout.NORTH);
		}

		final JPanel btns = new JPanel(new GridLayout((extended ? 2 : 1), 1));

		final JPanel topBtns = new JPanel(new BorderLayout());

		if (extended) {
			topBtns.add(extend, BorderLayout.WEST);
		}

		topBtns.add(site, BorderLayout.CENTER);
		topBtns.add(detailsToggle, BorderLayout.EAST);

		final JPanel bottomBtns = new JPanel(new BorderLayout());

		if (!extended) {
			bottomBtns.add(extend, BorderLayout.WEST);
		}
		bottomBtns.add(accounts, BorderLayout.CENTER);
		bottomBtns.add(okay, BorderLayout.EAST);

		if (extended) {
			btns.add(topBtns);
		}
		btns.add(bottomBtns);

		rightstuff.add(btns);

		rightPane.add(rightstuff, BorderLayout.CENTER);

		rightPane.validate();
		rightPane.repaint();

	}

	/**
	 * Redraws the tree and applies the correct settings. Made method to reduce
	 * repetitive code
	 */
	private void resetTree() {
		final DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();

		/*
		 * Requested that we keep the icons render.setLeafIcon(null); //Gay
		 * little grey dots render.setClosedIcon(null); //Gay Folders
		 * render.setOpenIcon(null);
		 */
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		// tree.setCellRenderer(new MyRenderer()); // TODO
		tree.setCellRenderer(render);

		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);
		for (int row = 0; row < tree.getRowCount(); row++) {
			tree.expandPath(tree.getPathForRow(row));
		}
		scroll.setViewportView(tree);
		splits.validate();
	}

	@Override
	public void scriptPaused(final ScriptHandler handler, final Script script) {
	}

	@Override
	public void scriptResumed(final ScriptHandler handler, final Script script) {
	}

	@Override
	public void scriptStarted(final ScriptHandler handler, final Script script) {
	}

	@Override
	public void scriptStopped(final ScriptHandler handler, final Script script) {
	}
	/**
	 * Created and displays the ScriptSelector
	 */
	public void showSelector() {

		setIconImage(GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.ICON));

		tree = new JTree();

		getContentPane().removeAll();

		final JPanel pane = new JPanel(new GridLayout(1, 2));
		pane.setBorder(null);

		rightPane = new JPanel(new BorderLayout());
		rightPane.setBorder(null);

		final Dimension size = new Dimension(350, 300);
		new Dimension(350, 20);

		rightPane.setMaximumSize(size);
		rightPane.setSize(size);
		rightPane.setMinimumSize(size);

		leftPane = new JPanel(new BorderLayout());

		quickSearch = new JTextField();

		quickSearch.addKeyListener(new Searcher());

		// final GridBagConstraints c = new GridBagConstraints();
		accounts = new JComboBox(AccountManager.getAccountNames().toArray());

		okay = new JButton("OK", new ImageIcon(
				GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.PLAY)));
		okay.addActionListener(this);
		okay.setPreferredSize(new Dimension(80, 20));

		extend = new JButton();
		extend.setIcon(moreButtons);
		extend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				extend();
			}
		});

		site = new JButton("Author Site", new ImageIcon(
				GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.HOME)));
		site.addActionListener(this);
		site.setEnabled(false);

		detailsToggle = new JButton("Manifest", new ImageIcon(
				GlobalConfiguration
				.getImage(GlobalConfiguration.Paths.Resources.PLAY)));
		detailsToggle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				toggleDetails();
			}
		});
		// TODO actions
		detailsToggle.setEnabled(false);

		scroll = new JScrollPane(tree);
		scroll.setBorder(null);

		description = new JTextPane();
		description.setBorder(null);
		description.setEditable(false);

		sDetails = new JTextPane();
		sDetails.setBorder(null);
		sDetails.setEditable(false);

		scrollDescription = new JScrollPane(description);
		scrollDescription.setBorder(null);

		resetRightSide();

		leftPane.add(scroll, BorderLayout.NORTH);
		leftPane.add(new JLabel("Search scripts by name below:"),
				BorderLayout.CENTER);
		leftPane.add(quickSearch, BorderLayout.SOUTH);

		splits = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane,
				rightPane);
		splits.setBorder(null);
		splits.setEnabled(false);

		resetTree();

		setResizable(false);

		tree.setSelectionRow(0);
		tree.requestFocusInWindow();
		tree.addTreeSelectionListener(this);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(final WindowEvent e) {
				quickSearch.requestFocus();
			}
		});

		pane.add(splits);
		add(pane);

		scriptList = ScriptSelector.loadScripts(null);
		updateLoadingProgressToTree();
		// startLoadingThread();

		pack();

		scroll.setPreferredSize(scroll.getSize());

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(getParent());
		setVisible(true);

		splits.setBorder(null);
		description.setBorder(null);
	}

	/**
	 * Sorts the Scripts based on the information sent. Implements MergeSort
	 * 
	 * @param all
	 *            List of the scripts to sort
	 * @param t
	 *            TYPE to sort by
	 * @param asc
	 *            True if ascending order.
	 * @return Sorted list of Scripts
	 */
	private String[] sort(final String[] all, final TYPE t, final boolean asc) {
		if (all.length <= 1) {
			return all;
		}
		final String[] left = new String[all.length / 2], right = new String[all.length
		                                                                     - left.length];
		System.arraycopy(all, 0, left, 0, left.length);
		System.arraycopy(all, left.length, right, 0, right.length);
		return merge(sort(left, t, asc), sort(right, t, asc), t, asc);
	}

	// this will load scripts in thread in future
	@SuppressWarnings("unused")
	private void startLoadingThread() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				scriptList = ScriptSelector.loadScripts(new SLListener() {

					@Override
					public void scriptLoaded(final String name, final ScriptManifest sm) {
						// System.out.println("Script loaded " + name);
						// tempUpdater();
						// updateLoadingProgressToTree();
						// System.out.println("Progress updated to tree");
					}
				});

				updateLoadingProgressToTree();
			}
		}).start();

	}

	/**
	 * Some work for future (?) details toggler. Don't modify :P
	 * 
	 * @author Waterwolf
	 */
	public void toggleDetails() {

		toggleDetails(!detailsVisible);

		/*
		 * 
		 * 
		 * final String detailInfo = mf.name() + " details: <br>" + "Author: " +
		 * mf.authors().toString() + "<br>" + "Version: " + mf.version() +"<br>"
		 * + "Category: " + mf.category() +"<br>" + "Website: " + mf.website() +
		 * "<br>" + "Email: " + mf.email() + "<br>" + "Summary: " + mf.summary()
		 * + "<br>" + "Notes: " + mf.notes();
		 * 
		 * description.setText(detailInfo);
		 */
	}

	public void toggleDetails(final boolean state) {
		if (state == detailsVisible) {
			return;
		}

		final ScriptManifest mf = findSelectedScriptManifest();
		if (mf == null) {
			return;
		}

		detailsVisible = state;

		detailsToggle.setText(state ? "Options" : "Manifest");

		if (detailsVisible) {
			sDetails.setContentType("text/plain");
			sDetails.setContentType("text/html");

			String allAuthors = "";
			for (final String a : mf.authors()) {
				allAuthors += a + " ";
			}

			sDetails.setText("<b>Script details for " + mf.name() + "</b>:<br>"
					+ "Authors: " + allAuthors + "<br>" + "Version: "
					+ mf.version() + "<br>" + "Category: " + mf.category()
					+ "<br>" + "Website: " + mf.website() + "<br>" + "Email: "
					+ mf.email() + "<br>" + "Summary: " + mf.summary() + "<br>"
					+ "Notes: <br>" + mf.notes());
		}

		resetRightSide();
	}

	/**
	 * Remakes tree again using scriptList
	 */
	private void updateLoadingProgressToTree() {
		final String[] names = sort(getScriptsNames(), TYPE.CATEGORY, true);

		final LinkedHashMap<String, ScriptManifest> ans = new LinkedHashMap<String, ScriptManifest>();
		for (String s : names) {
			// System.out.print(s + " ");
			s = getScriptForName(s);
			ans.put(s, scriptList.get(s));
		}
		scriptList = ans;

		remakeTree(names, TYPE.CATEGORY, false);
	}

	/**
	 * Handles the different selections of the JTree.
	 */
	@Override
	public void valueChanged(final TreeSelectionEvent event) {
		if (((JTree) event.getSource()).getSelectionPath() == null) {
			return;
		}
		description.setContentType("text/plain");
		description.setContentType("text/html");

		description.setText("Loading description..");

		final ScriptManifest info = scriptList
		.get(getScriptForName(((JTree) event.getSource())
				.getSelectionPath().getLastPathComponent().toString()));

		if (info == null) {
			description.setText("");
		} else if ((info.description() == null)
				|| info.description().equals("")) {
			description.setText(defaultText);
		} else {
			description.setText(info.description());
		}
		description.validate();
		description.setCaretPosition(0);

		if (findSelectedScriptManifest() == null) {
			detailsToggle.setEnabled(false);
		} else {
			detailsToggle.setEnabled(true);
		}

		if (findUrlForSelectedScript().isEmpty()) {
			site.setEnabled(false);
		} else {
			site.setEnabled(true);
		}

		toggleDetails(false);
	}
}
