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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GwtServiceCallGenerator extends AJavaClassGenerator implements NodeTypes {

	private Node call;
	private Node package_;
	private Node gwtModule;

	public GwtServiceCallGenerator(String srcPath, Node call) {
		super(srcPath, true);
		this.call = call;

		package_ = call.getSuperparentByType(Package);
		assert package_ != null;
		gwtModule = package_.getSuperparentByType(GwtModule);
		assert gwtModule != null;

	}

	@Override
	protected void printCode(JavaPrinter out) {
		out.package_(getPackageName());
		out.beginClass(call.getValue() + "ServiceCall", "scrum.client.core.AServiceCall", null);

		List<Node> parameters = call.getChildrenByType(Parameter);
		Collections.sort(parameters, new NodeByIndexComparator());

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

		StringBuilder callParameters = new StringBuilder();
		for (Node parameter : parameters) {
			callParameters.append(parameter.getValue()).append(", ");
		}

		out.beginMethod("void", "execute", Arrays.asList("Runnable returnHandler"));
		out.statement("serviceCaller.onServiceCall()");
		out.statement("serviceCaller.getService()." + Str.lowercaseFirstLetter(call.getValue())
				+ "(serviceCaller.getConversationNumber(), " + callParameters + "new DefaultCallback(returnHandler))");
		out.endMethod();

		Node dispensable = call.getChildByType(Dispensable);
		if (dispensable != null) {
			out.annotationOverride();
			out.beginMethod("boolean", "isDispensable", null);
			out.returnStatement(dispensable.getValue());
			out.endMethod();
		}

		out.toStringMethod('\"' + call.getValue() + '\"');

		out.endClass();
	}

	private String getType(Node parameter) {
		Node typeNode = parameter.getChildByType(Type);
		return typeNode == null ? "String" : typeNode.getValue();
	}

	private String getPackageName() {
		String packageName = getBasePackageName() + "." + package_.getValue();
		return packageName;
	}

	private String getBasePackageName() {
		return gwtModule.getValue().toLowerCase() + ".client";
	}
}
