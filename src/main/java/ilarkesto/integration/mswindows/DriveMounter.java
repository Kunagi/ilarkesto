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
package ilarkesto.integration.mswindows;

import ilarkesto.base.Proc;

/**
 * Utility for mounting and unmounting of drives on MsWindows.
 * 
 * @author wko
 */
public class DriveMounter {

	public static void main(String[] args) {
		mount('b', "\\\\devsrv1\\bpm", "DA100001\\a101zi8", "!23geheim", false);
		// unmount('b');
	}

	// TODO public static Map<Character,String> getMountedDrives();

	public static void mount(char driveLetter, String networkSharePath, String user, String password,
			boolean persistent) {
		Proc proc = new Proc("NET");
		proc.addParameter("USE");
		proc.addParameter(driveLetter + ":");
		proc.addParameter(networkSharePath);
		if (password != null) proc.addParameter(password);
		if (user != null) proc.addParameter("/USER:" + user);
		proc.addParameter("/PERSISTENT:" + (persistent ? "YES" : "NO"));

		proc.start();
		if (proc.getReturnCode() != 0) { throw new RuntimeException(proc.getOutput()); }
	}

	public static void unmount(char driveLetter) {
		Proc proc = new Proc("NET");
		proc.addParameter("USE");
		proc.addParameter(driveLetter + ":");
		proc.addParameter("/DELETE");

		proc.start();
		if (proc.getReturnCode() != 0) { throw new RuntimeException(proc.getOutput()); }
	}

	private DriveMounter() {}

}
