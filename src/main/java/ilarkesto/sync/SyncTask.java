/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.sync;

import ilarkesto.concurrent.ATask;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class SyncTask extends ATask {

	private static final Log LOG = Log.get(SyncTask.class);

	private Set<String> synced;
	private Processor processor = new Processor();
	private SyncSource master;
	private SyncSource slave;
	private Set<String> lastItems;
	private SyncItem currentItem;
	private int totalItemCount;

	// --- dependencies ---

	private SyncSource left;
	private SyncSource right;
	private File itemsFile;

	public SyncTask(SyncSource left, SyncSource right, File itemsFile) {
		this.left = left;
		this.right = right;
		this.itemsFile = itemsFile;
	}

	// --- ---

	@Override
	protected void perform() {
		loadLastItems();
		totalItemCount = lastItems.size();
		synced = new HashSet<String>();
		sync(left, right);
		currentItem = null;
		sync(right, left);
		currentItem = null;
	}

	private void sync(SyncSource master, SyncSource slave) {
		this.master = master;
		this.slave = slave;
		master.iterate(processor);
	}

	private void sync(SyncItem m, SyncItem s) {
		String id = m.getId();

		if (synced.contains(id)) return;

		if (s == null) {
			syncMissing(m, null);
			return;
		}

		long diff = m.getLastModified() - s.getLastModified();

		if (diff > 0) {
			LOG.debug(id, ":", master, "->", slave);
			slave.updateSyncItem(s, m);
		} else if (diff < 0) {
			LOG.debug(id, ":", slave, "->", master);
			master.updateSyncItem(m, s);
		}

		if (!lastItems.contains(id)) {
			lastItems.add(id);
			saveLastItems();
		}

		synced.add(id);
	}

	private void syncMissing(SyncItem m, SyncItem s) {
		String id = m.getId();
		if (lastItems.contains(id)) {
			// item deleted
			LOG.debug(id, ": X", master);
			master.deleteSyncItem(m);
			lastItems.remove(id);
		} else {
			// item created
			LOG.debug(id, ":", master, "->", slave);
			slave.updateSyncItem(s, m);
			lastItems.add(id);
		}
		saveLastItems();
	}

	private void saveLastItems() {
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(itemsFile)));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		for (String id : lastItems) {
			out.println(id);
		}
		IO.close(out);
	}

	private void loadLastItems() {
		lastItems = new HashSet<String>();
		if (!itemsFile.exists()) {
			LOG.info("Items file does not exist. Must be first sync.", itemsFile.getPath());
			return;
		}
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(itemsFile));
			String line;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) continue;
				lastItems.add(line);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		IO.close(in);
		return;
	}

	@Override
	public String getProgressMessage() {
		return currentItem == null ? super.getProgressMessage() : currentItem.getId();
	}

	@Override
	public float getProgress() {
		if (totalItemCount == 0) return super.getProgress();
		int count = synced.size();
		return (float) count / (float) totalItemCount;
	}

	private class Processor implements SyncItemProcessor {

		@Override
		public void process(SyncItem m) {
			currentItem = m;
			SyncItem s = slave.getSyncItem(m.getId());
			sync(m, s);
		}

	}

}
