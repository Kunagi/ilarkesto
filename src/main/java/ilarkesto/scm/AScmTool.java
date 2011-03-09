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

public abstract class AScmTool {

	public abstract String getName();

	public abstract String getVersion();

	public abstract boolean isProjectDir(File dir);

	protected abstract AScmProject createProject(File dir);

	public AScmProject getProject(File projectDir) {
		if (!projectDir.exists()) throw new RuntimeException("Project does not exist: " + projectDir.getPath());
		if (!isProjectDir(projectDir))
			throw new RuntimeException("Not a " + getName() + " project: " + projectDir.getPath());
		return createProject(projectDir);
	}

	public boolean isAvailable() {
		try {
			getVersion();
		} catch (Throwable ex) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getName();
	}

}
