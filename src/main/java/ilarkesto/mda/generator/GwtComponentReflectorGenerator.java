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

import ilarkesto.core.scope.ComponentReflector;
import ilarkesto.core.scope.Scope;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeTypes;

import java.util.Arrays;
import java.util.List;

public class GwtComponentReflectorGenerator extends AJavaClassGenerator implements NodeTypes {

	private Node component;
	private Node package_;
	private Node gwtModule;

	public GwtComponentReflectorGenerator(String srcPath, Node component) {
		super(srcPath, true);
		this.component = component;

		package_ = component.getSuperparentByType(Package);
		assert package_ != null;
		gwtModule = package_.getSuperparentByType(GwtModule);
		assert gwtModule != null;

	}

	@Override
	protected void printCode(JavaPrinter out) {
		out.package_(getPackageName());
		out.beginClass(getClassName(), null, getInterfacesNames());

		out.beginProcedure("injectComponents", getInjectOutjectParameters());
		printInjections(out);
		out.endProcedure();

		out.beginProcedure("callInitializationMethods", Arrays.asList(component.getValue() + " component"));
		printInitializations(out);
		out.endProcedure();

		out.beginProcedure("outjectComponents", getInjectOutjectParameters());
		out.endProcedure();

		out.endClass();
	}

	private void printInitializations(JavaPrinter out) {
		for (Node initProc : component.getChildrenByType(InitializationProcedure)) {
			out.statement("component." + initProc.getValue() + "()");
		}
	}

	private void printInjections(JavaPrinter out) {
		for (Node dependency : component.getChildrenByType(Dependency)) {
			if (dependency.containsChildByTypeAndValueFalse(Inject)) continue;
			String type = getDependencyType(dependency);
			out.assignment("component." + dependency.getValue(), "(" + type + ") scope.getComponent(\""
					+ dependency.getValue() + "\")");
		}
	}

	private List<String> getInjectOutjectParameters() {
		return Arrays.asList(component.getValue() + " component", Scope.class.getName() + " scope");
	}

	private List<String> getInterfacesNames() {
		return Arrays.asList(ComponentReflector.class.getName() + "<" + component.getValue() + ">");
	}

	private String getClassName() {
		return "G" + component.getValue() + "Reflector";
	}

	private String getPackageName() {

		String packageName = getBasePackageName() + "." + package_.getValue();

		return packageName;
	}

	private String getBasePackageName() {
		return gwtModule.getValue().toLowerCase() + ".client";
	}
}
