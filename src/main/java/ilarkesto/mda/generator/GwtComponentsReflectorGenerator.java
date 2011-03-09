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
import ilarkesto.core.scope.ComponentReflector;
import ilarkesto.core.scope.Scope;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GwtComponentsReflectorGenerator extends AJavaClassGenerator implements NodeTypes {

	private Node gwtModule;
	private List<Node> components = new ArrayList<Node>();

	public GwtComponentsReflectorGenerator(String srcPath, Node gwtModule) {
		super(srcPath, true);
		this.gwtModule = gwtModule;
	}

	@Override
	protected void printCode(JavaPrinter out) {
		out.package_(getBasePackageName());
		out.beginClass(getClassName(), null, Arrays.asList(ComponentReflector.class.getName()));

		for (Node component : components)
			printField(out, component);

		out.beginProcedure("injectComponents", Arrays.asList("Object component", Scope.class.getName() + " scope"));
		for (Node component : components)
			out.statement("if (component instanceof " + getType(component) + ") " + getName(component)
					+ "Reflector.injectComponents(component, scope)");
		out.endProcedure();

		out.beginProcedure("callInitializationMethods", Arrays.asList("Object component"));
		for (Node component : components)
			out.statement("if (component instanceof " + getType(component) + ") " + getName(component)
					+ "Reflector.callInitializationMethods(component)");
		out.endProcedure();

		out.beginProcedure("outjectComponents", Arrays.asList("Object component", Scope.class.getName() + " scope"));
		for (Node component : components)
			out.statement("if (component instanceof " + getType(component) + ") " + getName(component)
					+ "Reflector.outjectComponents(component, scope)");
		out.endProcedure();

		for (Node component : components)
			printCreateMethod(out, component);

		out.endClass();
	}

	private void printCreateMethod(JavaPrinter out, Node component) {
		out.beginMethod(ComponentReflector.class.getName(), "create" + component.getValue() + "Reflector", null);
		out.returnStatement("new " + getReflectorType(component) + "()");
		out.endMethod();
	}

	private void printField(JavaPrinter out, Node component) {
		out.protectedField(ComponentReflector.class.getName(), getName(component) + "Reflector", "create"
				+ component.getValue() + "Reflector()");
	}

	private String getName(Node component) {
		return Str.lowercaseFirstLetter(component.getValue());
	}

	private String getType(Node component) {
		return getPackageName(component) + "." + component.getValue();
	}

	private String getReflectorType(Node component) {
		return getPackageName(component) + ".G" + component.getValue() + "Reflector";
	}

	private String getPackageName(Node component) {
		Node package_ = component.getSuperparentByType(Package);
		return getBasePackageName() + "." + package_.getValue();
	}

	private String getClassName() {
		return "G" + gwtModule.getValue() + "ComponentsReflector";
	}

	private String getBasePackageName() {
		return gwtModule.getValue().toLowerCase() + ".client";
	}

	public void addComponent(Node component) {
		components.add(component);
	}

}
