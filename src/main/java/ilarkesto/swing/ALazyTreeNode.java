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
package ilarkesto.swing;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public abstract class ALazyTreeNode extends DefaultMutableTreeNode {

	private boolean childrenLoaded;

	protected abstract void loadChildren();

	public ALazyTreeNode(Object userObject, boolean allowesChildren) {
		super(userObject, allowesChildren);
	}

	private synchronized void loadChildrenInternal() {
		if (childrenLoaded) return;
		childrenLoaded = true;
		loadChildren();
	}

	@Override
	public int getChildCount() {
		loadChildrenInternal();
		return super.getChildCount();
	}

	@Override
	public TreeNode getChildAt(int index) {
		loadChildrenInternal();
		return super.getChildAt(index);
	}

	@Override
	public Enumeration children() {
		loadChildrenInternal();
		return super.children();
	}

}
