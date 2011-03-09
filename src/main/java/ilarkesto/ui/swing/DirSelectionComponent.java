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
package ilarkesto.ui.swing;

import ilarkesto.core.logging.Log;
import ilarkesto.swing.ALazyTreeNode;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class DirSelectionComponent extends AComponent {

	private static final Log LOG = Log.get(DirSelectionComponent.class);

	private JTree tree;

	// --- dependencies ---

	private File selectedDir;

	public void setSelectedDir(File selectedFolder) {
		this.selectedDir = selectedFolder;
	}

	// --- ---

	@Override
	protected void initializeControls() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Computer", true);
		for (File file : File.listRoots()) {
			root.add(new DirNode(file));
		}
		tree = new JTree(root);
		// tree.setRootVisible(false);
	}

	@Override
	protected JComponent createComponent() {
		JScrollPane scroller = new JScrollPane(tree);

		return scroller;
	}

	@Override
	protected void updateControls() {}

	public File getSelectedDir() {
		return selectedDir;
	}

	class DirNode extends ALazyTreeNode {

		private File dir;

		public DirNode(File dir) {
			super(dir.getName().length() == 0 ? dir.getPath() : dir.getName(), true);
			this.dir = dir;
		}

		@Override
		protected void loadChildren() {
			LOG.debug("Listing", dir.getPath());
			File[] files = dir.listFiles();
			if (files == null || files.length == 0) return;
			for (File file : files) {
				if (!file.isDirectory()) continue;
				add(new DirNode(file));
			}
		}

	}

}
