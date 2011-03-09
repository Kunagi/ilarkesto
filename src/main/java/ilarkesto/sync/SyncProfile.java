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

public class SyncProfile {

	private int lastFileCount;

	// --- dependencies ---

	private String name;
	private SyncSource left;
	private SyncSource right;

	public SyncProfile(String name, SyncSource left, SyncSource right) {
		this.name = name;
		this.left = left;
		this.right = right;
	}

	public SyncProfile(String name, String left, String right) {
	// this(name,null,null);
	}

	// --- ---

	public void setName(String name) {
		this.name = name;
	}

	public SyncSource getLeft() {
		return left;
	}

	public SyncSource getRight() {
		return right;
	}

	// public String getDestinationPath() {
	// return destinationPath;
	// }

	public int getLastFileCount() {
		return lastFileCount;
	}

	public void setLastFileCount(int lastFileCount) {
		this.lastFileCount = lastFileCount;
	}

	public void setRight(SyncSource right) {
		this.right = right;
	}

	public void setLeft(SyncSource left) {
		this.left = left;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
