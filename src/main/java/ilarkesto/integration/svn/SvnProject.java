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

import ilarkesto.integration.jdom.JDom;
import ilarkesto.scm.AScmProject;
import ilarkesto.scm.AScmTool;

import java.io.File;

public class SvnProject extends AScmProject {

	SvnProject(AScmTool workspace, File dir) {
		super(workspace, dir);
	}

	@Override
	public boolean isDirty() {
		String output = exec("status");
		output = output.trim();
		return output.length() > 0;
	}

	@Override
	public String getLogAndDiffSince(String version) {
		if (version == null) version = "PREV";
		StringBuilder sb = new StringBuilder();
		sb.append("# Log since ").append(version).append(":\n\n");
		sb.append(getLogSince(version));
		sb.append("\n# Diff since ").append(version).append(":\n\n");
		sb.append(getDiffSince(version));
		return sb.toString();
	}

	public String getLogSince(String version) {
		if (version == null) throw new IllegalArgumentException("version == null");
		return exec("log", "--non-interactive", "-r", version + ":BASE");
	}

	public String getDiffSince(String version) {
		if (version == null) throw new IllegalArgumentException("version == null");
		return exec("diff", "--non-interactive", "-r", version);
	}

	@Override
	public boolean pullFromOrigin() {
		String output = exec("update", "--non-interactive");
		output = output.trim();
		return !output.startsWith("At revision ");
	}

	@Override
	public String getVersion() {
		String xml = exec("info", "--non-interactive", "--xml");
		return JDom.getChildAttributeValue(JDom.createDocument(xml), "entry", "revision");
	}

	private synchronized String exec(String... parameters) {
		return getTool().exec(getDir(), parameters);
	}

	@Override
	public Svn getTool() {
		return (Svn) super.getTool();
	}

}
