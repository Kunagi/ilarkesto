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

import ilarkesto.core.event.Quiet;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GwtEventGenerator extends AJavaClassGenerator implements NodeTypes {

	private static final String QUIET_FLAG = "quiet";
	private Node event;
	private Node package_;
	private Node gwtModule;

	public GwtEventGenerator(String srcPath, Node event) {
		super(srcPath, true);
		this.event = event;

		package_ = event.getSuperparentByType(Package);
		assert package_ != null;
		gwtModule = package_.getSuperparentByType(GwtModule);
		assert gwtModule != null;

	}

	@Override
	protected void printCode(JavaPrinter out) {
		out.package_(getPackageName());
		out.beginClass(event.getValue() + "Event", ilarkesto.core.event.AEvent.class.getName(), getInterfaces());

		List<Node> parameters = event.getChildrenByType(Parameter);

		for (Node parameter : parameters) {
			out.field("private", getType(parameter), parameter.getValue(), null);
		}

		List<String> constructorParameters = new ArrayList<String>(parameters.size());
		for (Node parameter : parameters) {
			constructorParameters.add(getType(parameter) + " " + parameter.getValue());
		}
		out.beginConstructor(constructorParameters);
		for (Node parameter : parameters) {
			out.assignment("this." + parameter.getValue(), parameter.getValue());
		}
		out.endConstructor();

		for (Node parameter : parameters) {
			out.getter(getType(parameter), parameter.getValue());
		}

		out.beginMethod("void", "tryToGetHandled", Arrays.asList("Object handler"));
		out.beginIf("handler instanceof " + event.getValue() + "Handler");
		if (!event.containsChild(Flag, QUIET_FLAG)) {
			out.logDebug("\"    \" + handler.getClass().getName() + \".on" + event.getValue() + "(event)\"");
		}
		out.statement("((" + event.getValue() + "Handler)handler).on" + event.getValue() + "(this)");
		out.endIf();
		out.endMethod();

		out.endClass();
	}

	private String getType(Node parameter) {
		Node typeNode = parameter.getChildByType(Type);
		return typeNode == null ? "Object" : typeNode.getValue();
	}

	private Collection<String> getInterfaces() {
		if (event.containsChild(Flag, QUIET_FLAG)) return Arrays.asList(Quiet.class.getName());
		return Collections.emptyList();
	}

	private String getPackageName() {
		String packageName = getBasePackageName() + "." + package_.getValue();
		return packageName;
	}

	private String getBasePackageName() {
		return gwtModule.getValue().toLowerCase() + ".client";
	}
}
