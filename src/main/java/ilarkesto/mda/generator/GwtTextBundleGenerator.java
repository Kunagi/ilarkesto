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

public class GwtTextBundleGenerator extends AJavaClassGenerator implements NodeTypes {

	private Node bundle;
	private Node gwtModule;

	public GwtTextBundleGenerator(String srcPath, Node bundle) {
		super(srcPath, true);
		this.bundle = bundle;

		gwtModule = bundle.getSuperparentByType(GwtModule);
		assert gwtModule != null;

	}

	@Override
	protected void printCode(JavaPrinter out) {
		out.package_(getBasePackageName() + ".i18n");
		out.beginClass(getClassName(), null, null);

		out.loggerByClassName();

		for (Node text : bundle.getChildrenByType(Text)) {
			out.beginMethod("String", text.getValue(), null);
			String en = text.getChildValueByType(EN);
			if (Str.isBlank(en)) {
				out.returnStatement("null");
			} else {
				out.returnStatement("\"" + Str.escapeEscapeSequences(en) + "\"");
			}
			out.endMethod();
		}

		out.endClass();
	}

	private String getClassName() {
		return "TextBundle" + bundle.getValue();
	}

	private String getBasePackageName() {
		return gwtModule.getValue().toLowerCase() + ".client";
	}
}
