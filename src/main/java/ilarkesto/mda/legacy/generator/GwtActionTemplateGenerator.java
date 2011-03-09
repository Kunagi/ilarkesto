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
package ilarkesto.mda.legacy.generator;

import ilarkesto.mda.legacy.model.ActionModel;
import ilarkesto.mda.legacy.model.ParameterModel;

public class GwtActionTemplateGenerator extends AClassGenerator {

	private ActionModel action;

	public GwtActionTemplateGenerator(ActionModel action) {
		super();
		this.action = action;
	}

	@Override
	protected final String getName() {
		return action.getName() + "Action";
	}

	@Override
	protected final String getPackage() {
		return action.getPackageName().replace(".server", ".client");
	}

	@Override
	protected final boolean isInterface() {
		return false;
	}

	@Override
	protected void writeContent() {
		if (!action.getParameters().isEmpty()) writeConstructor();
	}

	private void writeConstructor() {
		ln();
		StringBuilder sig = new StringBuilder();
		{
			boolean first = true;
			for (ParameterModel parameter : action.getParameters()) {
				if (first) {
					first = false;
				} else {
					sig.append(", ");
				}
				sig.append(parameter.getType().replace(".server", ".client")).append(" ").append(parameter.getName());
			}
		}
		ln("    public " + getName() + "(" + sig + ") {");
		StringBuilder params = new StringBuilder();
		{
			boolean first = true;
			for (ParameterModel parameter : action.getParameters()) {
				if (first) {
					first = false;
				} else {
					params.append(", ");
				}
				params.append(parameter.getName());
			}
		}
		ln("        super(" + params + ");");
		ln("    }");
	}

	@Override
	protected final String getSuperclass() {
		return "G" + action.getName() + "Action";
	}

	@Override
	protected boolean isAbstract() {
		return false;
	}

	@Override
	protected boolean isOverwrite() {
		return false;
	}

}
