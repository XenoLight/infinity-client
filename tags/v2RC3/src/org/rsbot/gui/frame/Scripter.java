package org.rsbot.gui.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.lazygamerz.Lexer.JavaLexer;
import org.lazygamerz.Lexer.Lexer;
import org.lazygamerz.Lexer.Token;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.GlobalConfiguration.OperatingSystem;

class DocPosition {

	private int position;

	public DocPosition(final int position) {
		this.position = position;
	}

	public DocPosition adjustPosition(final int adjustment) {
		position += adjustment;
		return this;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DocPosition) {
			final DocPosition d = (DocPosition) (obj);
			if (this.position == d.position) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	int getPosition() {
		return position;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + this.position;
		return hash;
	}

	@Override
	public String toString() {
		return "" + position;
	}
}

class DocPositionComparator implements Comparator<Object> {

	public int compare(final Object o1, final Object o2) {
		if (o1 instanceof DocPosition && o2 instanceof DocPosition) {
			final DocPosition d1 = (DocPosition) (o1);
			final DocPosition d2 = (DocPosition) (o2);
			return (d1.getPosition() - d2.getPosition());
		} else if (o1 instanceof DocPosition) {
			return -1;
		} else if (o2 instanceof DocPosition) {
			return 1;
		} else if (o1.hashCode() < o2.hashCode()) {
			return -1;
		} else if (o2.hashCode() > o1.hashCode()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof DocPositionComparator) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int hash = 7;
		return hash;
	}
}

class DocumentReader extends Reader {

	private long position = 0;

	private long mark = -1;
	private final AbstractDocument document;
	public DocumentReader(final AbstractDocument document) {
		this.document = document;
	}

	public void close() {
	}

	@Override
	public void mark(final int readAheadLimit) {
		mark = position;
	}

	@Override
	public boolean markSupported() {
		return true;
	}

	@Override
	public int read() {
		if (position < document.getLength()) {
			try {
				final char c = document.getText((int) position, 1).charAt(0);
				position++;
				return c;
			} catch (final BadLocationException x) {
				return -1;
			}
		} else {
			return -1;
		}
	}

	@Override
	public int read(final char[] cbuf) {
		return read(cbuf, 0, cbuf.length);
	}

	public int read(final char[] cbuf, final int off, final int len) {
		if (position < document.getLength()) {
			int length = len;
			if (position + length >= document.getLength()) {
				length = document.getLength() - (int) position;
			}
			if (off + length >= cbuf.length) {
				length = cbuf.length - off;
			}
			try {
				final String s = document.getText((int) position, length);
				position += length;
				for (int i = 0; i < length; i++) {
					cbuf[off + i] = s.charAt(i);
				}
				return length;
			} catch (final BadLocationException x) {
				return -1;
			}
		} else {
			return -1;
		}
	}

	@Override
	public boolean ready() {
		return true;
	}

	@Override
	public void reset() {
		if (mark == -1) {
			position = 0;
		} else {
			position = mark;
		}
		mark = -1;
	}

	public void seek(final long n) {
		if (n <= document.getLength()) {
			position = n;
		} else {
			position = document.getLength();
		}
	}

	@Override
	public long skip(final long n) {
		if (position + n <= document.getLength()) {
			position += n;
			return n;
		} else {
			final long oldPos = position;
			position = document.getLength();
			return (document.getLength() - oldPos);
		}
	}

	public void update(final int position, final int adjustment) {
		if (position < this.position) {
			if (this.position < position - adjustment) {
				this.position = position;
			} else {
				this.position += adjustment;
			}
		}
	}
}

public class Scripter extends JFrame {

	private class Colorer extends Thread {

		private class RecolorEvent {

			public int position;
			public int adjustment;

			public RecolorEvent(final int position, final int adjustment) {
				this.position = position;
				this.adjustment = adjustment;
			}
		}
		
		private final TreeSet<DocPosition> iniPositions = new TreeSet<DocPosition>(new DocPositionComparator());

		private final HashSet<DocPosition> newPositions = new HashSet<DocPosition>();

		private volatile Vector<RecolorEvent> v = new Vector<RecolorEvent>();
		private volatile int change = 0;
		private volatile int lastPosition = -1;
		private volatile boolean asleep = false;
		private final Object lock = new Object();

		public void color(final int position, final int adjustment) {
			if (position < lastPosition) {
				if (lastPosition < position - adjustment) {
					change -= lastPosition - position;
				} else {
					change += adjustment;
				}
			}
			synchronized (lock) {
				v.add(new RecolorEvent(position, adjustment));
				if (asleep) {
					this.interrupt();
				}
			}
		}

		@Override
		public void run() {
			int position = -1;
			int adjustment = 0;
			boolean tryAgain = false;
			for (;;) {
				synchronized (lock) {
					if (v.size() > 0) {
						final RecolorEvent re = (RecolorEvent) (v.elementAt(0));
						v.removeElementAt(0);
						position = re.position;
						adjustment = re.adjustment;
					} else {
						tryAgain = false;
						position = -1;
						adjustment = 0;
					}
				}
				if (position != -1) {
					SortedSet<DocPosition> workingSet;
					Iterator<DocPosition> workingIt;
					final DocPosition startRequest = new DocPosition(position);
					final DocPosition endRequest = new DocPosition(position
							+ ((adjustment >= 0) ? adjustment : -adjustment));
					DocPosition dp;
					DocPosition dpStart = null;
					DocPosition dpEnd = null;

					try {
						workingSet = iniPositions.headSet(startRequest);
						dpStart = ((DocPosition) workingSet.last());
					} catch (final NoSuchElementException x) {
						dpStart = new DocPosition(0);
					}

					if (adjustment < 0) {
						workingSet = iniPositions.subSet(startRequest,
								endRequest);
						workingIt = workingSet.iterator();
						while (workingIt.hasNext()) {
							workingIt.next();
							workingIt.remove();
						}
					}

					workingSet = iniPositions.tailSet(startRequest);
					workingIt = workingSet.iterator();
					while (workingIt.hasNext()) {
						((DocPosition) workingIt.next())
						.adjustPosition(adjustment);
					}

					workingSet = iniPositions.tailSet(dpStart);
					workingIt = workingSet.iterator();
					dp = null;
					if (workingIt.hasNext()) {
						dp = (DocPosition) workingIt.next();
					}
					try {
						Token t;
						boolean done = false;
						dpEnd = dpStart;
						synchronized (doclock) {
							syntaxLexer.reset(documentReader, 0,
									dpStart.getPosition(), 0);
							documentReader.seek(dpStart.getPosition());
							t = syntaxLexer.getNextToken();
						}
						newPositions.add(dpStart);
						while (!done && t != null) {
							synchronized (doclock) {
								if (t.getCharEnd() <= document.getLength()) {
									document.setCharacterAttributes(
											t.getCharBegin() + change,
											t.getCharEnd() - t.getCharBegin(),
											getStyle(t.getDescription()), true);
									dpEnd = new DocPosition(t.getCharEnd());
								}
								lastPosition = (t.getCharEnd() + change);
							}
							if (t.getState() == Token.INITIAL_STATE) {
								while (dp != null
										&& dp.getPosition() <= t.getCharEnd()) {
									if (dp.getPosition() == t.getCharEnd()
											&& dp.getPosition() >= endRequest
											.getPosition()) {
										done = true;
										dp = null;
									} else if (workingIt.hasNext()) {
										dp = (DocPosition) workingIt.next();
									} else {
										dp = null;
									}
								}
								newPositions.add(dpEnd);
							}
							synchronized (doclock) {
								t = syntaxLexer.getNextToken();
							}
						}

						workingIt = iniPositions.subSet(dpStart, dpEnd)
						.iterator();
						while (workingIt.hasNext()) {
							workingIt.next();
							workingIt.remove();
						}

						workingIt = iniPositions.tailSet(
								new DocPosition(document.getLength()))
								.iterator();
						while (workingIt.hasNext()) {
							workingIt.next();
							workingIt.remove();
						}

						iniPositions.addAll(newPositions);
						newPositions.clear();
					} catch (final IOException x) {
					}
					synchronized (doclock) {
						lastPosition = -1;
						change = 0;
					}
					tryAgain = true;
				}
				asleep = true;
				if (!tryAgain) {
					try {
						sleep(0xffffff);
					} catch (final InterruptedException x) {
					}

				}
				asleep = false;
			}
		}
	}
	@SuppressWarnings("serial")
	private class HighLightedDocument extends DefaultStyledDocument {

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a)
		throws BadLocationException {
			synchronized (doclock) {
				super.insertString(offs, str, a);
				color(offs, str.length());
				documentReader.update(offs, str.length());
			}
		}

		@Override
		public void remove(final int offs, final int len) throws BadLocationException {
			synchronized (doclock) {
				super.remove(offs, len);
				color(offs, -len);
				documentReader.update(offs, -len);
			}
		}
	}
	class JavaFilter extends javax.swing.filechooser.FileFilter {

		public boolean accept(final File file) {
			final String filename = file.getName();
			return filename.endsWith(".java");
		}

		public String getDescription() {
			return "*.java";
		}
	}
	private static final long serialVersionUID = 8326421682467537173L;
	public static File file = new File("");
	private javax.swing.JPopupMenu.Separator jSeparator1;
	protected JTextPane textPane;
	protected HighLightedDocument document;
	protected DocumentReader documentReader;
	protected Lexer syntaxLexer;
	protected Colorer colorer;

	private final Object doclock = new Object();

	private final Dimension minSize = new Dimension(767, 530);

	public static String defaultLayout = ("import java.awt.Graphics; \n"
			+ "import java.util.Map; \n"
			+ "\n"
			+ "import org.rsbot.event.listeners.PaintListener; \n"
			+ "import org.rsbot.script.Script; \n"
			+ "import org.rsbot.script.ScriptManifest; \n"
			+ "\n"
			+ "@ScriptManifest(\n"
			+ "       authors = { \"Your Name Here\" },\n"
			+ "       name = \"Script Name Here\",\n"
			+ "       category = \"\", \n"
			+ "       version = 1.00, \n"
			+ "       description = \"\" \n"
			+ "       ) \n"
			+ "       public class YourScriptNameHere extends Script implements PaintListener { \n"
			+ "\n"
			+ "       public int loop() {\n"
			+ "           return (5);\n"
			+ "       }\n"
			+ "\n"
			+ "       public void onRepaint(final Graphics g) {\n"
			+ "       }\n"
			+ "\n"
			+ "       @Override\n"
			+ "       public boolean onStart(final Map<String, String> args) {\n"
			+ "          return true;\n" + "       }\n" + "\n"
			+ "       @Override\n" + "       public void onFinish() {\n"
			+ "       }\n" + "}");

	private final Hashtable<String, SimpleAttributeSet> styles = 
			new Hashtable<String, SimpleAttributeSet>();

	public Scripter() {
		setTitle("Runedev Script Editor");
		setVisible(true);
		setBackground(new Color(245, 245, 245));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				// BotGUI.scripter = null;
			}
		});
		setResizable(false);
		final File icon = new File(GlobalConfiguration.Paths.getIconDirectory()
				+ "/edit.png");
		setIconImage(GlobalConfiguration.getImageFile(icon));

		document = new HighLightedDocument();
		jSeparator1 = new JPopupMenu.Separator();

		textPane = new JTextPane(document);
		textPane.setCaretPosition(0);
		textPane.setMargin(new Insets(5, 5, 5, 5));
		textPane.setPreferredSize(new Dimension(minSize));
		final JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setPreferredSize(new Dimension(minSize));

		final JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollPane, BorderLayout.CENTER);
		setContentPane(contentPane);

		final JMenuBar menuBar = new JMenuBar();

		final JMenu fileMenu = new JMenu("File");
		final JMenuItem newScript = new JMenuItem("New");
		newScript.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N,
				java.awt.event.InputEvent.CTRL_MASK));
		newScript.setIcon(new ImageIcon(GlobalConfiguration.Paths
				.getIconDirectory() + "/pencil.png"));
		fileMenu.add(newScript);

		final JMenuItem openScript = new JMenuItem("Open");
		openScript.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_O,
				java.awt.event.InputEvent.CTRL_MASK));
		final Image Open = (Toolkit.getDefaultToolkit()
				.getImage(GlobalConfiguration.Paths.Resources.PLAY));
		openScript.setIcon(new ImageIcon(Open));
		fileMenu.add(openScript);

		final JMenuItem saveScript = new JMenuItem("Save");
		saveScript.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_MASK));
		final Image Save = (Toolkit.getDefaultToolkit()
				.getImage(GlobalConfiguration.Paths.Resources.SAVE));
		saveScript.setIcon(new ImageIcon(Save));
		fileMenu.add(saveScript);
		menuBar.add(fileMenu);

		final JMenu editMenu = new JMenu("Edit");
		final JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X,
				java.awt.event.InputEvent.CTRL_MASK));
		final Image Cut = (Toolkit.getDefaultToolkit()
				.getImage(GlobalConfiguration.Paths.Resources.CUT));
		cut.setIcon(new javax.swing.ImageIcon(Cut));
		editMenu.add(cut);

		final JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C,
				java.awt.event.InputEvent.CTRL_MASK));
		final Image Copy = (Toolkit.getDefaultToolkit()
				.getImage(GlobalConfiguration.Paths.Resources.COPY));
		copy.setIcon(new javax.swing.ImageIcon(Copy));
		editMenu.add(copy);

		final JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_V,
				java.awt.event.InputEvent.CTRL_MASK));
		final Image Paste = (Toolkit.getDefaultToolkit()
				.getImage(GlobalConfiguration.Paths.Resources.PASTE));
		paste.setIcon(new javax.swing.ImageIcon(Paste));
		editMenu.add(paste);
		editMenu.add(jSeparator1);

		final JMenuItem selectAll = new JMenuItem("Select All");
		selectAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_A,
				java.awt.event.InputEvent.CTRL_MASK));
		editMenu.add(selectAll);
		menuBar.add(editMenu);

		final JMenu infoMenu = new JMenu("Info");
		final JMenuItem troubleshooting = new JMenuItem("Troubleshooting");
		troubleshooting.setIcon(new ImageIcon(GlobalConfiguration.Paths
				.getIconDirectory() + "/web.png"));
		infoMenu.add(troubleshooting);

		final JMenuItem about = new JMenuItem("about");
		about.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F1,
				java.awt.event.InputEvent.CTRL_MASK));
		about.setIcon(new ImageIcon(GlobalConfiguration.Paths
				.getIconDirectory() + "/gui.png"));
		infoMenu.add(about);
		menuBar.add(infoMenu);

		class newClick implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				if (JOptionPane
						.showConfirmDialog(
								contentPane,
								"You really want to start a new script? \n"
								+ "All un-saved work on this script will be lost.") == 0) {
					try {
						document.remove(0, document.getLength());
						document.insertString(document.getLength(),
								defaultLayout, getStyle("text"));
					} catch (final BadLocationException e1) {
					}
				}
			}
		}

		class openClick implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				final JFileChooser fc = new JFileChooser(file);
				fc.setCurrentDirectory(new File("./scripts/"));
				fc.addChoosableFileFilter(new JavaFilter());
				if (fc.showOpenDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					try {
						document.remove(0, document.getLength());
						final BufferedReader in = new BufferedReader(new FileReader(
								file));
						String line = null;
						while ((line = in.readLine()) != null) {
							if (document.getLength() != 0) {
								document.insertString(document.getLength(),
										"\n", getStyle("text"));
							}
							document.insertString(document.getLength(), line,
									getStyle("text"));
						}
					} catch (final Exception ee) {
					}
				}
			}
		}

		class saveClick implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				final JFileChooser fc = new JFileChooser(file);
				fc.setCurrentDirectory(new File("./scripts/"));
				fc.addChoosableFileFilter(new JavaFilter());
				if (fc.showSaveDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					try {
						file = fc.getSelectedFile();
						final BufferedWriter out = new BufferedWriter(new FileWriter(
								file));
						out.write(document.getText(0, document.getLength()));

						out.close();
					} catch (final Exception ee) {
					}
				}
			}
		}

		class Cut implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				textPane.cut();
			}
		}

		class Copy implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				textPane.copy();
			}
		}

		class Paste implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				textPane.paste();
			}
		}

		class SelectAll implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				textPane.selectAll();
			}
		}

		class tsClick implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				openURL("http://forum.runedev.info/viewtopic.php?f=40&t=2629");
			}

			public void openURL(final String url) {
				final OperatingSystem os = GlobalConfiguration
				.getCurrentOperatingSystem();
				try {
					if (os == OperatingSystem.MAC) {
						final Class<?> fileMgr = Class
						.forName("com.apple.eio.FileManager");
						final Method openURL = fileMgr.getDeclaredMethod(
								"openURL", new Class[] { String.class });
						openURL.invoke(null, url);
					} else if (os == OperatingSystem.WINDOWS) {
						Runtime.getRuntime().exec(
								"rundll32 url.dll,FileProtocolHandler " + url);
					} else { /* assume Unix or Linux */
						final String[] browsers = { "firefox", "opera",
								"konqueror", "epiphany", "mozilla", "netscape" };
						String browser = null;
						for (int count = 0; (count < browsers.length)
						&& (browser == null); count++) {
							if (Runtime
									.getRuntime()
									.exec(new String[] { "which",
											browsers[count] }).waitFor() == 0) {
								browser = browsers[count];
							}
						}
						if (browser == null) {
							throw new Exception("Could not find web browser");
						} else {
							Runtime.getRuntime().exec(
									new String[] { browser, url });
						}
					}
				} catch (final Exception e) {
				}
			}
		}

		class aboutClick implements ActionListener {

			public void actionPerformed(final ActionEvent e) {
				JOptionPane.showMessageDialog(contentPane, new String[] {
						"A Script Editor Made for RuneDev.",
						"\nThis Editor was designed by Sorcermus\n"
						+ "for the RuneDev gaming client\n"
						+ "For more information, \nvisit; "
						+ GlobalConfiguration.Paths.URLs.SITE }, "\n"
						+ "About this editor, and the game client",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		newScript.addActionListener(new newClick());
		openScript.addActionListener(new openClick());
		saveScript.addActionListener(new saveClick());
		cut.addActionListener(new Cut());
		copy.addActionListener(new Copy());
		paste.addActionListener(new Paste());
		selectAll.addActionListener(new SelectAll());
		troubleshooting.addActionListener(new tsClick());
		about.addActionListener(new aboutClick());

		setJMenuBar(menuBar);

		final JTextArea lines = new JTextArea("");
		lines.setSelectionEnd(document.getLength());
		lines.setFont(new Font("Monospaced", Font.PLAIN, 12));
		lines.setBackground(new Color(210, 210, 210));
		lines.setEditable(false);
		lines.setMargin(new Insets(5, 2, 5, 2));

		document.addDocumentListener(new DocumentListener() {

			public void changedUpdate(final DocumentEvent de) {
				lines.setText(getText());
			}

			public String getText() {
				final int caretPosition = document.getEndPosition().getOffset();
				final Element root = document.getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
					text += i + System.getProperty("line.separator");
				}
				return text;
			}

			public void insertUpdate(final DocumentEvent de) {
				lines.setText(getText());
			}

			public void removeUpdate(final DocumentEvent de) {
				lines.setText(getText());
			}
		});

		scrollPane.setRowHeaderView(lines);

		colorer = new Colorer();
		colorer.start();

		initStyles(12);

		documentReader = new DocumentReader(document);

		initDocument();

		pack();
		setVisible(true);
	}

	public void color(final int position, final int adjustment) {
		colorer.color(position, adjustment);
	}

	public void colorAll() {
		color(0, document.getLength());
	}

	private SimpleAttributeSet getStyle(final String styleName) {
		return ((SimpleAttributeSet) styles.get(styleName));
	}

	private void initDocument() {

		syntaxLexer = new JavaLexer(documentReader);

		try {
			document.insertString(document.getLength(), defaultLayout,
					getStyle("text"));
		} catch (final BadLocationException ble) {
			System.err.println("Couldn't insert initial text.");
		}
	}

	private void initStyles(final int fontSize) {
		SimpleAttributeSet style;

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("body", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.blue);
		StyleConstants.setBold(style, true);
		StyleConstants.setItalic(style, false);
		styles.put("tag", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.blue);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("endtag", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("reference", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, new Color(0xB03060));
		StyleConstants.setBold(style, true);
		StyleConstants.setItalic(style, false);
		styles.put("name", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, new Color(0xB03060));
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, true);
		styles.put("value", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setBold(style, true);
		StyleConstants.setItalic(style, false);
		styles.put("text", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.blue);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("reservedWord", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("identifier", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, new Color(0xB03060));
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("literal", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, new Color(0x000080));
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("separator", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setBold(style, true);
		StyleConstants.setItalic(style, false);
		styles.put("operator", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.green.darker());
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("comment", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, new Color(0xA020F0).darker());
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("preprocessor", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.black);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("whitespace", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.red);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("error", style);

		style = new SimpleAttributeSet();
		StyleConstants.setFontFamily(style, "Monospaced");
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBackground(style, Color.white);
		StyleConstants.setForeground(style, Color.orange);
		StyleConstants.setBold(style, false);
		StyleConstants.setItalic(style, false);
		styles.put("unknown", style);
	}
}
