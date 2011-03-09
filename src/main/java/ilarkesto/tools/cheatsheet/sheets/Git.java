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
package ilarkesto.tools.cheatsheet.sheets;

import ilarkesto.tools.cheatsheet.Cheat;
import ilarkesto.tools.cheatsheet.CheatGroup;
import ilarkesto.tools.cheatsheet.CheatSheet;

public class Git {

	public static CheatSheet create() {
		CheatSheet cs = new CheatSheet("Git");
		cs.addGroup(workingLocal());
		cs.addGroup(workingRemote());
		cs.addGroup(svn());
		return cs;
	}

	private static CheatGroup workingLocal() {
		CheatGroup group = new CheatGroup("Working local");
		group.addCheat(new Cheat("git init", "Initialize a new repository in current dir."));
		group.addCheat(new Cheat("git add .", "Add all modified/new files to index"));
		group.addCheat(new Cheat("git commit -a", "Commit all modified files"));
		return group;
	}

	private static CheatGroup workingRemote() {
		CheatGroup group = new CheatGroup("Working with remote repositories");
		group.addCheat(new Cheat("git clone -ssh user@server:/path", "Clone from SSH")); // TODO
		group.addCheat(new Cheat("git pull", "Pull (fetch and merge) commits from origin"));
		group.addCheat(new Cheat("git push", "Push commits to origin"));
		return group;
	}

	private static CheatGroup svn() {
		CheatGroup group = new CheatGroup("Subversion");
		group.addCheat(new Cheat("git svn clone http://server/path", "Clone from Subversion repository"));
		group.addCheat(new Cheat("git svn rebase", "Fetch and rebase commits from SVN"));
		group.addCheat(new Cheat("git svn dcommit", "Commit local commits to remote SVN"));
		return group;
	}

}
