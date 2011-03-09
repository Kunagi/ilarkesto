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

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;

public class DllInstaller {

	private static final Log LOG = Log.get(DllInstaller.class);

	private DllInstaller() {}

	public static void installDll(String dllName, boolean deleteOnExit) {
		File file = new File(dllName + ".dll").getAbsoluteFile();
		IO.copyResource("dll/" + dllName + ".dll", file.getPath());
		LOG.info(dllName, "installed to", file.getPath());
		if (deleteOnExit) file.deleteOnExit();
	}

}
