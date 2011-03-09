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
package ilarkesto.tools.mkproject;

import java.io.File;

/**
 * Command line tool for creating a java-project. Prepared for Subversion, Maven and Eclipse.
 * 
 * @author wko
 */
public class MkProject {

	public static void main(String[] args) {
		if (args.length != 1) throw new RuntimeException("A project name must be specified as parameter.");

		String name = args[0];
		File dir = new File(name);

		mkdir(name);

		mkdir(name + "/tags");
		mkdir(name + "/branches");
		String trunkPath = name + "/trunk";
		mkdir(trunkPath);

		mkdir(trunkPath + "/src/main/java");
		mkdir(trunkPath + "/src/main/resources");
		mkdir(trunkPath + "/src/test/java");
		mkdir(trunkPath + "/src/test/resources");
		mkdir(trunkPath + "/target/classes");
		mkdir(trunkPath + "/target/test-classes");
	}

	private static void mkdir(String path) {
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) return;
		if (!dir.mkdirs()) throw new RuntimeException("Failed to create dir: " + dir.getPath());
	}

	// --- dependencies ---

}
