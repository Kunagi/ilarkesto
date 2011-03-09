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
package ilarkesto.mda.generator;

import ilarkesto.base.Str;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeByIndexComparator;
import ilarkesto.mda.model.NodeTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AJavaClassGenerator {

	private String srcPath;
	private boolean overwriteAllowed;

	protected abstract void printCode(JavaPrinter out);

	public AJavaClassGenerator(String srcPath, boolean overwriteAllowed) {
		super();
		this.srcPath = srcPath;
		this.overwriteAllowed = overwriteAllowed;
	}

	public void generate() {
		JavaPrinter out = new JavaPrinter();
		if (overwriteAllowed) out.commentGenerated();
		printCode(out);
		out.writeToFile(srcPath, overwriteAllowed);
	}

	// --- helper ---

	public String getDependencyType(Node dependency) {
		Node module = dependency.getSuperparentByType(NodeTypes.GwtModule);
		Node type = dependency.getChildByType(NodeTypes.Type);
		if (type != null) return type.getValue();
		String name = dependency.getValue();
		name = Str.uppercaseFirstLetter(name);
		Node component = module.getChildRecursive(NodeTypes.Component, name);
		if (component != null)
			return getModulePackage(module) + "." + component.getSuperparentByType(NodeTypes.Package).getValue() + "."
					+ component.getValue();
		Node entity = module.getChildRecursive(NodeTypes.Entity, name);
		if (entity != null)
			return getModulePackage(module) + "." + entity.getSuperparentByType(NodeTypes.Package).getValue() + "."
					+ entity.getValue();
		throw new RuntimeException("Can not determine type for dependency: " + dependency);
	}

	public String getModulePackage(Node module) {
		return module.getValue().toLowerCase() + ".client";
	}

	public List<String> getParameterNames(Node parent) {
		List<Node> parameters = parent.getChildrenByType(NodeTypes.Parameter);
		Collections.sort(parameters, new NodeByIndexComparator());
		List<String> ret = new ArrayList<String>(parameters.size());
		for (Node parameter : parameters) {
			ret.add(parameter.getValue());
		}
		return ret;
	}

	public List<String> getParameterTypesAndNames(Node parent, String defaultType) {
		List<Node> parameters = parent.getChildrenByType(NodeTypes.Parameter);
		Collections.sort(parameters, new NodeByIndexComparator());
		List<String> ret = new ArrayList<String>(parameters.size());
		for (Node parameter : parameters) {
			ret.add(getParameterTypeAndName(parameter, defaultType));
		}
		return ret;
	}

	public String getParameterTypeAndName(Node parameter, String defaultType) {
		return getParameterType(parameter, defaultType) + " " + parameter.getValue();
	}

	private String getParameterType(Node parameter, String defaultType) {
		Node typeNode = parameter.getChildByType(NodeTypes.Type);
		return typeNode == null ? defaultType : typeNode.getValue();
	}

}
