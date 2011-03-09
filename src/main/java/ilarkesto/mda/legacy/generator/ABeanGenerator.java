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

import ilarkesto.base.Reflect;
import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.mda.legacy.model.BeanModel;
import ilarkesto.mda.legacy.model.CompositeModel;
import ilarkesto.mda.legacy.model.DependencyModel;
import ilarkesto.mda.legacy.model.EventModel;
import ilarkesto.mda.legacy.model.ParameterModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class ABeanGenerator<B extends BeanModel> extends AClassGenerator {

	protected B bean;

	public ABeanGenerator(B bean) {
		super();
		this.bean = bean;
	}

	@Override
	protected void writeContent() {
		writeDependencies();
		writeComposites();
		writeEvents();
	}

	private void writeEvents() {
		if (bean.getEvents().isEmpty()) return;
		section("events");
		List<String> multiEventListenerLines = new ArrayList<String>();
		for (EventModel event : bean.getEvents()) {
			String name = event.getName();
			String nameL = Str.lowercaseFirstLetter(name);
			String listenerType = name + "Listener";
			String listenersVar = nameL + "Listeners";
			String paramDeclaration = "";
			String paramList = "";
			for (ParameterModel param : event.getParameters()) {
				paramDeclaration += ", " + param.getType() + " " + param.getName();
				paramList += ", " + param.getName();
			}
			ln();
			comment(name);
			ln();
			ln("    public static interface", listenerType, "{");
			ln("        void on" + name + "(" + bean.getName() + " eventSource" + paramDeclaration + ");");
			ln("    }");
			ln();
			ln("    private List<" + listenerType + ">", listenersVar + ";");
			ln();
			ln("    public final " + bean.getName() + " add" + name + "Listener(" + listenerType + " listener) {");
			ln("        if (" + listenersVar + " == null) " + listenersVar + " = new ArrayList<" + listenerType
					+ ">(3);");
			ln("        " + listenersVar + ".add(listener);");
			ln("        return (" + bean.getName() + ")this;");
			ln("    }");
			ln();
			ln("    protected final void fire" + name + "("
					+ (paramDeclaration.length() == 0 ? "" : paramDeclaration.substring(2)) + ") {");
			ln("        if (" + listenersVar + " == null) return;");
			ln("        for (" + listenerType + " listener : " + listenersVar + ") {");
			ln("            listener.on" + name + "((" + bean.getName() + ")this" + paramList + ");");
			ln("        }");
			ln("    }");

			multiEventListenerLines.add("        if (listener instanceof " + listenerType + ") add" + name
					+ "Listener((" + listenerType + ")listener);");
		}

		if (multiEventListenerLines.size() > 1) {
			ln();
			comment("multi event listener");
			ln();
			ln("    public final " + bean.getName() + " addMultiEventListener(Object listener) {");
			for (String line : multiEventListenerLines) {
				ln(line);
			}
			ln("        return (" + bean.getName() + ") this;");
			ln("    }");
		}
	}

	protected void writeDependencies() {
		if (bean.getDependencies().isEmpty()) return;
		ln();
		section("dependencies");
		for (DependencyModel dependencyModel : bean.getDependencies()) {
			dependency(dependencyModel.getType(), dependencyModel.getName(), true, false);
		}
	}

	private void writeComposites() {
		if (bean.getComposites().isEmpty()) return;
		ln();
		section("composites");

		for (CompositeModel c : bean.getComposites()) {
			String name = Str.lowercaseFirstLetter(c.getName());
			String nameUpper = Str.uppercaseFirstLetter(c.getName());
			ln();
			comment(name);
			ln();
			ln("    private " + c.getType() + " " + name + ";");
			ln();
			ln("    public final " + c.getType() + " get" + nameUpper + "() {");
			ln("        if (" + name + " == null) {");
			ln("            " + name + " = create" + nameUpper + "();");
			ln("            initialize" + nameUpper + "(" + name + ");");
			ln("        }");
			ln("        return " + name + ";");
			ln("    }");
			ln();
			ln("    protected " + c.getType() + " create" + nameUpper + "() {");
			ln("        return " + name + " = " + Reflect.class.getName() + ".newInstance(" + c.getType() + ".class);");
			ln("    }");
			ln();
			ln("    protected void initialize" + nameUpper + "(" + c.getType() + " bean) {");
			ln("        autowire(bean);");
			ln("        " + Reflect.class.getName() + ".invokeInitializeIfThere(bean);");
			ln("    }");
			ln();
			ln("    public final void reset" + nameUpper + "() {");
			ln("        " + name + " = null;");
			ln("    }");
		}
	}

	@Override
	protected String getName() {
		return "G" + bean.getName();
	}

	@Override
	protected String getPackage() {
		return bean.getPackageName();
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected Set<String> getImports() {
		Set<String> result = new LinkedHashSet<String>();
		result.addAll(super.getImports());
		result.add("ilarkesto.persistence.*");
		result.add(Log.class.getName());
		result.add("ilarkesto.base.*");
		result.add("ilarkesto.base.time.*");
		result.add("ilarkesto.auth.*");
		return result;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

}
