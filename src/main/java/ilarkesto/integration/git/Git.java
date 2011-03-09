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
package ilarkesto.integration.git;

import ilarkesto.base.Proc;
import ilarkesto.scm.AScmProject;
import ilarkesto.scm.AScmTool;

import java.io.File;

public class Git extends AScmTool {

	public static final Git THIS = new Git();

	private String executable = "git";

	String exec(File workDir, String... parameters) {
		Proc proc = new Proc(executable);
		proc.addParameters(parameters);
		proc.setWorkingDir(workDir);
		proc.addEnvironmentParameter("LANG", "en_US.UTF-8");
		return proc.execute(0, 1);
	}

	@Override
	protected AScmProject createProject(File dir) {
		return new GitProject(this, dir);
	}

	@Override
	public String getVersion() {
		String output = exec(null, "version");
		output = output.trim();
		return output.substring(output.lastIndexOf(' ')).trim();
	}

	@Override
	public String getName() {
		return "git";
	}

	@Override
	public boolean isProjectDir(File dir) {
		File svnDir = new File(dir.getPath() + "/.git");
		return svnDir.exists() && svnDir.isDirectory();
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

}
