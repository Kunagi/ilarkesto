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
import ilarkesto.mda.model.NodeTypes;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class GwtServiceAsyncInterfaceGenerator extends AJavaClassGenerator implements NodeTypes {

	private Node module;

	public GwtServiceAsyncInterfaceGenerator(String srcPath, Node module) {
		super(srcPath, true);
		this.module = module;
	}

	@Override
	protected void printCode(JavaPrinter out) {
		out.package_(getPackageName());
		out.beginInterface(module.getValue() + "ServiceAsync", null);

		List<Node> calls = module.getChildrenByTypeRecursive(ServiceCall);
		for (Node call : calls) {
			List<String> params = getParameterTypesAndNames(call, "String");
			params.add(0, "int conversationNumber");
			params.add(AsyncCallback.class.getName() + "<" + getPackageName() + ".DataTransferObject> callback");
			out.interfaceMethod("void", Str.lowercaseFirstLetter(call.getValue()), params);
		}

		out.endInterface();
	}

	private String getPackageName() {
		return module.getValue().toLowerCase() + ".client";
	}
}
