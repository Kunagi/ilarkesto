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
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class JavaPrinter {

	private static final Log LOG = Log.get(JavaPrinter.class);

	private String packageName;
	private String className;

	private String charset = IO.UTF_8;
	private StringBuilder sb = new StringBuilder();
	private int depth;
	private boolean lineStart = true;

	public void commentGenerated() {
		comment("// ----------> GENERATED FILE - DON'T TOUCH! <----------");
		ln();
	}

	public void comment(String text) {
		ln("// " + text);
	}

	public void loggerByClassName() {
		loggerByClassName(className);
	}

	public void loggerByClassName(String className) {
		field("protected static", Log.class.getName(), "log", Log.class.getName() + ".get(" + className + ".class)");
	}

	public void logDebug(String params) {
		log("debug", params);
	}

	public void logInfo(String params) {
		log("info", params);
	}

	public void log(String level, String params) {
		statement("log." + level + "(" + params + ")");
	}

	public void logger(String name) {
		field("protected static", Log.class.getName(), "log", Log.class.getName() + ".get(\"" + name + "\")");
	}

	public void assignment(String var, String value) {
		statement(var + " = " + value);
	}

	public void returnStatement(String statement) {
		statement("return " + statement);
	}

	public void statement(String statement) {
		ln(statement + ";");
	}

	public void protectedField(String type, String name) {
		protectedField(type, name, null);
	}

	public void protectedField(String type, String name, String value) {
		field("protected", type, name, value);
	}

	public void field(String modifiers, String type, String name, String value) {
		s(modifiers + " " + type + " " + name);
		if (value != null) s(" = " + value);
		ln(";");
		ln();
	}

	public void getter(String type, String name) {
		beginMethod(type, "get" + Str.uppercaseFirstLetter(name), null);
		returnStatement(name);
		endMethod();
	}

	public void annotationOverride() {
		annotation("Override");
	}

	public void annotation(String name) {
		s("@");
		ln(name);
	}

	public void toStringMethod(String returnStatement) {
		annotationOverride();
		beginToStringMethod();
		returnStatement(returnStatement);
		endMethod();
	}

	public void beginProcedure(String name, List<String> parameters) {
		beginMethod("void", name, parameters);
	}

	public void beginToStringMethod() {
		beginMethod("String", "toString", null);
	}

	public void beginMethod(String returnType, String name, List<String> parameters) {
		s("public " + returnType + " " + name + "(");
		if (parameters != null && !parameters.isEmpty()) s(Str.concat(parameters, ", "));
		ln(") {");
		in();
	}

	public void abstractMethod(String returnType, String name, List<String> parameters) {
		if (returnType == null) returnType = "void";
		s("public abstract " + returnType + " " + name + "(");
		if (parameters != null && !parameters.isEmpty()) s(Str.concat(parameters, ", "));
		ln(");");
		ln();
	}

	public void interfaceMethod(String returnType, String name, List<String> parameters) {
		if (returnType == null) returnType = "void";
		s(returnType + " " + name + "(");
		if (parameters != null && !parameters.isEmpty()) s(Str.concat(parameters, ", "));
		ln(");");
		ln();
	}

	public void endProcedure() {
		endMethod();
	}

	public void endMethod() {
		out();
		ln("}");
		ln();
	}

	public void beginInterface(String name, Collection<String> interfaces) {
		if (className == null) className = name;
		s("public interface " + name);
		if (interfaces != null && !interfaces.isEmpty()) {
			s(" extends ");
			boolean first = true;
			for (String iface : interfaces) {
				if (first) {
					first = false;
				} else {
					s(", ");
				}
				s(iface);
			}
		}
		ln(" {");
		in();
		ln();
	}

	public void beginIf(String condition) {
		ln("if (" + condition + ") {");
		in();
	}

	public void endIf() {
		out();
		ln("}");
	}

	public void beginSynchronized(String lock) {
		ln("synchronized (" + lock + ") {");
		in();
	}

	public void endSynchronized() {
		out();
		ln("}");
	}

	public void beginTry() {
		ln("try {");
		in();
	}

	public void beginCatchThrowable() {
		beginCatch("Throwable");
	}

	public void beginCatch(String exceptionType) {
		out();
		ln("} catch (" + exceptionType + " ex) {");
		in();
	}

	public void endCatch() {
		out();
		ln("}");
	}

	public void beginClass(String name, String superclassName, Collection<String> interfaces) {
		beginClass(false, name, superclassName, interfaces);
	}

	public void beginClass(boolean abstract_, String name, String superclassName, Collection<String> interfaces) {
		if (className == null) className = name;
		s("public");
		if (abstract_) s(" abstract");
		s(" class " + name);
		if (superclassName != null) s(" extends " + superclassName);
		if (interfaces != null && !interfaces.isEmpty()) s(" implements " + Str.concat(interfaces, ", "));
		ln(" {");
		in();
		ln();
	}

	public void endInterface() {
		endClass();
	}

	public void endClass() {
		out();
		ln("}");
		ln();
	}

	public void beginConstructor(List<String> parameters) {
		beginMethod("", className, parameters);
	}

	public void endConstructor() {
		out();
		ln("}");
		ln();
	}

	public void package_(String name) {
		if (packageName == null) packageName = name;
		ln("package " + name + ";");
		ln();
	}

	public void imports(Collection<String> imports) {
		for (String imp : imports) {
			ln("import " + imp + ";");
		}
		ln();
	}

	public void ln(String s) {
		s(s);
		ln();
	}

	public void s(String s) {
		if (lineStart) {
			indentation();
			lineStart = false;
		}
		sb.append(s);
	}

	public void in() {
		if (!lineStart) throw new IllegalStateException("lineStart == false");
		depth++;
	}

	public void out() {
		if (depth == 0) throw new IllegalStateException("depth == 0");
		depth--;
	}

	public void ln() {
		sb.append("\n");
		lineStart = true;
	}

	private void indentation() {
		for (int i = 0; i < depth; i++) {
			sb.append("    ");
		}
	}

	public void writeToFile(String basePath, boolean overwrite) {
		if (packageName == null) throw new IllegalStateException("packageName == null");
		if (className == null) throw new IllegalStateException("className == null");
		String path = basePath + "/" + packageName.replace('.', '/') + "/" + className + ".java";
		File file = new File(path);
		writeToFile(file, overwrite);
	}

	public void writeToFile(File file, boolean overwrite) {
		if (file.exists()) {
			if (!overwrite) {
				LOG.debug("File already exists:", file.getPath());
				return;
			}
		}

		if (IO.writeFileIfChanged(file, toString(), charset)) {
			LOG.info("File written:", file.getPath());
		} else {
			LOG.debug("File is up to date:", file.getPath());
		}
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
