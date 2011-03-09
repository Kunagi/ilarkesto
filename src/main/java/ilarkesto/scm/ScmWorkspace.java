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
package ilarkesto.scm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScmWorkspace {

	private File dir;
	private Collection<AScmTool> tools;

	public ScmWorkspace(File dir, Collection<AScmTool> tools) {
		this.dir = dir;
		this.tools = tools;
	}

	public List<AScmProject> getProjects() {
		List<AScmProject> ret = new ArrayList<AScmProject>();
		File[] dirs = dir.listFiles();
		if (dirs == null || dirs.length == 0) return ret;
		for (File dir : dirs) {
			if (!dir.isDirectory()) continue;
			for (AScmTool tool : tools) {
				if (tool.isProjectDir(dir)) {
					ret.add(tool.getProject(dir));
				}
			}
		}
		return ret;
	}

	public File getDir() {
		return dir;
	}

	@Override
	public String toString() {
		return "ScmWorkspace(" + dir.getPath() + ")";
	}

}
