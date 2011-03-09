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
import ilarkesto.gwt.server.AGwtServiceImpl;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeTypes;

import java.util.Arrays;
import java.util.List;

public class GwtServiceImplGenerator extends AJavaClassGenerator implements NodeTypes {

	private Node module;

	public GwtServiceImplGenerator(String srcPath, Node module) {
		super(srcPath, true);
		this.module = module;
	}

	@Override
	protected void printCode(JavaPrinter out) {
		out.package_(getServerPackageName());
		out.beginClass(true, "G" + module.getValue() + "ServiceImpl", AGwtServiceImpl.class.getName(), Arrays
				.asList(getGwtPackageName() + "." + module.getValue() + "Service"));

		out.loggerByClassName(module.getValue() + "ServiceImpl");

		List<Node> calls = module.getChildrenByTypeRecursive(ServiceCall);
		for (Node call : calls) {
			if (call.getValue().equals("StartConversation")) {
				calls.remove(call);
				break;
			}
		}

		for (Node call : calls) {
			List<String> params = getParameterTypesAndNames(call, "String");
			params.add(0, "GwtConversation conversation");
			out.abstractMethod("void", "on" + call.getValue(), params);
		}

		for (Node call : calls) {
			List<String> params = getParameterTypesAndNames(call, "String");
			params.add(0, "int conversationNumber");
			out.beginMethod(getGwtPackageName() + ".DataTransferObject", Str.lowercaseFirstLetter(call.getValue()),
				params);
			if (!call.getValue().equals("Ping")) {
				out.logDebug("\"Handling service call: " + call.getValue() + "\"");
			}
			out.statement("WebSession session = (WebSession) getSession()");
			out.beginSynchronized("session");
			out.statement("GwtConversation conversation = session.getGwtConversation(conversationNumber)");
			out.statement("ilarkesto.di.Context context = ilarkesto.di.Context.get()");
			out.statement("context.setName(\"gwt-srv:" + call.getValue() + "\")");
			out.statement("context.bindCurrentThread()");
			out.beginTry();
			List<String> parameterNames = getParameterNames(call);
			parameterNames.add(0, "conversation");
			out.statement("on" + call.getValue() + "(" + Str.concat(parameterNames, ", ") + ")");
			out.beginCatchThrowable();
			out.statement("handleServiceMethodException(conversationNumber, \"" + call.getValue() + "\", ex)");
			out.statement("throw new RuntimeException(ex)");
			out.endCatch();
			out.statement(getGwtPackageName() + ".DataTransferObject ret = (" + getGwtPackageName()
					+ ".DataTransferObject) conversation.popNextData()");
			out.statement("onServiceMethodExecuted(context)");
			out.returnStatement("ret");
			out.endSynchronized();
			out.endMethod();
		}

		out.endClass();
	}

	private String getServerPackageName() {
		return module.getValue().toLowerCase() + ".server";
	}

	private String getGwtPackageName() {
		return module.getValue().toLowerCase() + ".client";
	}
}
