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
package ilarkesto.integration.svn;

import ilarkesto.base.Proc;
import ilarkesto.base.Str;
import ilarkesto.scm.AScmTool;

import java.io.File;

public class Svn extends AScmTool {

	public static final Svn THIS = new Svn();

	private String executable = "svn";

	String exec(File workDir, String... parameters) {
		Proc proc = new Proc(executable);
		proc.addParameters(parameters);
		proc.setWorkingDir(workDir);
		proc.addEnvironmentParameter("LANG", "en_US.UTF-8");
		return proc.execute();
	}

	@Override
	public String getVersion() {
		String output = exec(null, "--version");
		output = Str.getFirstLine(output);
		output = output.substring(13, output.lastIndexOf(' '));
		return output;
	}

	@Override
	protected SvnProject createProject(File dir) {
		return new SvnProject(this, dir);
	}

	@Override
	public String getName() {
		return "svn";
	}

	@Override
	public boolean isProjectDir(File dir) {
		File svnDir = new File(dir.getPath() + "/.svn");
		return svnDir.exists() && svnDir.isDirectory();
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}
}
