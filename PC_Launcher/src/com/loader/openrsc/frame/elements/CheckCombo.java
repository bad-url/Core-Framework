package com.loader.openrsc.frame.elements;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.listeners.CheckComboListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class CheckCombo extends JComboBox implements ListCellRenderer {
	public RadioButton checkBox;
	public JComboBox combo;
	boolean keepMenuOpen;

	@Override
	public void setPopupVisible(boolean v) {
		super.setPopupVisible(v);
	}

	public class store {
		public String text;
		public Boolean state;

		public store(String id, Boolean state) {
			this.text = id;
			this.state = state;
		}
	}

	public CheckCombo() {
		store[] stores = new CheckCombo.store[]{new store("none", true), new store("okay", false)};
		init(stores);
	}

	public void setContents(store[] stores) {
		this.combo = new JComboBox(stores);
		this.combo.setRenderer(this);
		this.combo.setBackground(Color.black);
		this.combo.setForeground(Color.white);
		this.combo.addActionListener(new CheckComboListener());
		this.combo.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				keepMenuOpen = true;

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						combo.setPopupVisible(keepMenuOpen);
					}
				});
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				keepMenuOpen = false;
			}
		});
		this.combo.setVisible(true);
	}

	private void init(store[] stores) {
		checkBox = new RadioButton("", new Rectangle(0,0,20,15));
		checkBox.setContentAreaFilled(true);

		setContents(stores);
	}

	public Component getListCellRendererComponent(JList list, Object value,
												  int index, boolean isSelected, boolean cellHasFocus) {
		store store = (store) value;
		checkBox.setText(store.text);
		checkBox.setSelected(((Boolean) store.state).booleanValue());
		checkBox.setBackground(isSelected ? new Color(0, 32, 66) : Color.black);
		checkBox.setForeground(isSelected ? Color.white : Color.white);

		return checkBox;

	}

	public void loadSpritePacks() {
		CheckCombo.store[] stores = null;

		try {
			File configFile = new File(Constants.CONF_DIR, "config.txt");
			configFile.createNewFile();

			File spDir = new File(Constants.SPRITEPACK_DIR);
			File[] spritePacks = spDir.listFiles(File::isDirectory);

			if (spritePacks.length > 0) {
				ArrayList<String> packsAvailable = new ArrayList<>();
				Map<String, Boolean> packsSettings = new HashMap<>();

				for (File spritePack : spritePacks)
					packsAvailable.add(spritePack.getName());

				BufferedReader br = new BufferedReader(new FileReader(configFile));
				String line;
				while ((line = br.readLine()) != null) {
					String[] packageName = line.split(":");
					//Check to make sure the user hasn't deleted the pack
					if (packsAvailable.contains(packageName[0])) {
						packsSettings.put(packageName[0], Integer.parseInt(packageName[1]) == 1);
					}


				}
				br.close();

				Iterator look = packsAvailable.iterator();
				FileWriter write = new FileWriter(configFile, true);
				PrintWriter writer = new PrintWriter(write);
				while (look.hasNext()) {
					//Check to see if the user added a pack
					String nextPack = (String)look.next();
					if (packsSettings.get(nextPack) == null) {
						writer.println(nextPack + ":0");
						packsSettings.put(nextPack,false);
					}
				}
				writer.close();
				write.close();
				//Prepare the packs to load into the combo box
				if (packsSettings.size() > 0) {
					stores = new CheckCombo.store[packsSettings.size()];
					Iterator it = packsSettings.entrySet().iterator();
					int j = 0;
					while (it.hasNext()) {
						Map.Entry pair = (Map.Entry) it.next();
						stores[j++] = new store((String) pair.getKey(), (Boolean)pair.getValue());
					}
				}

			}
		} catch (IOException a) {
			a.printStackTrace();
		}

		//Load the packs into the combo box
		if (stores != null)
			this.combo = new JComboBox(stores);

		this.combo.repaint();
	}
}


