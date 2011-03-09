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

import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeTypes;

import java.util.Arrays;

public class GwtEventHandlerGenerator extends AJavaClassGenerator implements NodeTypes {

	private Node event;
	private Node package_;
	private Node gwtModule;

	public GwtEventHandlerGenerator(String srcPath, Node event) {
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
		out.beginInterface(event.getValue() + "Handler", null);

		out.abstractMethod(null, "on" + event.getValue(), Arrays.asList(event.getValue() + "Event event"));

		out.endInterface();
	}

	private String getPackageName() {
		String packageName = getBasePackageName() + "." + package_.getValue();
		return packageName;
	}

	private String getBasePackageName() {
		return gwtModule.getValue().toLowerCase() + ".client";
	}
}
