import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.rsbot.bot.Bot;

public class DevSettingsExplorer extends DevPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3740194548541445155L;


	@Override
	public String getTabName() {
		return "Settings explorer";
	}
	
    private final int TIMEOUT = 3000;

    private HashMap<Integer, String> knownUses = new HashMap<Integer, String>();
    
    private int[] oldSettings = new int[0];
    private int[] lastSettings = new int[0];
    private long[] settingsAge = new long[0];
    
    DefaultTableModel model;
    JTable list;
    
    
    JToggleButton toggle;
    JToggleButton onlyUpdated;
    

	@Override
	public void init() {
		
		setLayout(new BorderLayout());
		
		model = new DefaultTableModel();
		model.addColumn("Known use");
		model.addColumn("Active");
		model.addColumn("Id");
		model.addColumn("Value");
		list = new JTable(model);
		
		list.getColumnModel().getColumn(1).setCellRenderer(new ColoredTableCellRenderer());
		JButton update = new JButton("Update");
		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
			
		});
		
		toggle = new JToggleButton("Auto poller");
		onlyUpdated = new JToggleButton("Show only updated entries");
		onlyUpdated.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.setRowCount(0);
				update();
			}
			
		});
		
		JPanel top = new JPanel(new BorderLayout());
		
		top.add(update, BorderLayout.WEST);
		top.add(onlyUpdated, BorderLayout.CENTER);
		top.add(toggle, BorderLayout.EAST);
		
		add(top, BorderLayout.NORTH);
		add(new JScrollPane(list), BorderLayout.CENTER);
		
		setKnownUses();
	}
	
	@Override
	public int loop() {
		if (toggle.isSelected())
			update();
		return 500;
	}
	
	public void update() {

		//model.setRowCount(0);
		
		final int[] settings = Bot.getClient().getSettingArray() != null ? Bot
				.getClient().getSettingArray().getData() : null;
		if (settings != null) {
			if (settings.length > lastSettings.length) {
				lastSettings = Arrays.copyOf(lastSettings, settings.length);
				oldSettings = Arrays.copyOf(oldSettings, settings.length);
				settingsAge = Arrays.copyOf(settingsAge, settings.length);
			}
			
			final long curTime = System.currentTimeMillis();
			final long cutoffTime = curTime - TIMEOUT;
			for (int i = 0; i < settings.length; i++) {
				if (settingsAge[i] == 0) {
					settingsAge[i] = settings[i] == 0 ? cutoffTime : curTime;
				}
				if (lastSettings[i] != settings[i]) {
					settingsAge[i] = curTime;
					oldSettings[i] = lastSettings[i];
					lastSettings[i] = settings[i];
				}
				final boolean highlight = settingsAge[i] > cutoffTime;
				final boolean show = settings[i] != 0;
				if ((show && !onlyUpdated.isSelected()) || highlight) {
					//model.get
					String use = knownUses.get(i);
					editRow(use, highlight, i, settings[i], (highlight ? "from " + oldSettings[i] + " to " + settings[i] : settings[i]+""));
					//model.addRow(new Object[] { use, highlight, i, settings[i] });
				}
				else {
					try {
						removeKeyRow(i);
					}
					catch (Exception ignored) {}
				}
			}
		}
	}
	
	public void removeKeyRow(int key) {
		int realCount = model.getRowCount();
		for (int r = 0; r < realCount; r++) {
			int val = -1;
			try {
				val = (Integer) model.getValueAt(r, 2);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				realCount = model.getRowCount();
				continue;
			}
			if (val == key) {
				model.removeRow(r);
			}
		}
	}
	
	public void editRow(String use, boolean highlight, int index, int value, String putVal) {
		int realCount = model.getRowCount();
		for (int r = 0; r < realCount; r++) {
			int val = -1;
			try {
				val = (Integer) model.getValueAt(r, 2);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				realCount = model.getRowCount();
				continue;
			}
			if (val == index) {
				model.setValueAt(use, r, 0);
				model.setValueAt(highlight, r, 1);
				model.setValueAt(putVal, r, 3);
				return;
			}
		}
		model.addRow(new Object[]{use, highlight, index, putVal});
	}

	public class ColoredTableCellRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7091710186454581701L;

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			setEnabled(table == null || table.isEnabled()); 
			
			if ((Boolean) value == true)
				setBackground(Color.green);
			else
				setBackground(null);

			super.getTableCellRendererComponent(table, value, selected,
					focused, row, column);

			return this;
		}
	}
	
	public void setKnownUses() {
		
		/* Thanks to
		 *  Cherry
		 *  
		 *  for collection interface ids
		 */
		
		
		knownUses.put(43, "CombatStyle");
		knownUses.put(172, "Auto-retaliate disabled");
		knownUses.put(173, "Run enabled");
		knownUses.put(300, "SpecialAttackAmount");
		knownUses.put(301, "SpecialEnabled");
		knownUses.put(674, "CanoeMakingState");
		knownUses.put(965, "OpenedSkillGuide");
		knownUses.put(1054, "ClanChat");
		knownUses.put(1055, "AssistChat");
		knownUses.put(1056, "GameFilter");
		//knownUses.put(563, "BankPin"); seems to change to 4 (from 0) after the bank pin is entered
		knownUses.put(1433, "RestCounter"); // counts rests to 5 and resets. For different resting anims?
		knownUses.put(1384, "QuestFilter");
		knownUses.put(1395, "Prayerinuse?");
		knownUses.put(1396, "QuickPray"); // 0 for nothing, 1 for selection, 2 for enabled
		knownUses.put(1801, "XPGain"); // divide by 10
		knownUses.put(2159, "FriendChat");
	}

}
