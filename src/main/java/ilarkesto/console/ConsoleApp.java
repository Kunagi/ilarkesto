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
package ilarkesto.console;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

public class ConsoleApp {

	private final Map<String, Command> commands = new LinkedHashMap<String, Command>();

	public enum ExecutionMode {
		RUN_ONCE, ASK_TO_CONTINUE, RUN_UNTIL_EXIT
	};

	private ExecutionMode mode = ExecutionMode.RUN_ONCE;
	private boolean exit = false;

	private boolean showParameterNames = false;

	public static ConsoleApp fromClass(Class<?> clazz) {
		ConsoleApp app = new ConsoleApp();
		ArrayList<Method> methods = new ArrayList<Method>(Arrays.asList(clazz.getDeclaredMethods()));
		Map<String, ArrayList<Method>> methodsByName = new HashMap<String, ArrayList<Method>>();
		for (Method m : methods) {
			if (isPublicAndStatic(m) && !isMain(m)) {
				// sort into buckets by name
				if (methodsByName.get(m.getName()) == null) methodsByName.put(m.getName(), new ArrayList<Method>());

				methodsByName.get(m.getName()).add(m);
			}
		}

		// add commands
		for (Entry<String, ArrayList<Method>> entry : methodsByName.entrySet()) {
			Method[] methodArray = new Method[entry.getValue().size()];
			entry.getValue().toArray(methodArray);
			app.addCommand(entry.getKey(), methodArray);
		}

		return app;
	}

	private static boolean isPublicAndStatic(Method m) {
		int modifiers = m.getModifiers();
		return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
	}

	private static boolean isMain(Method m) {
		return isPublicAndStatic(m) && m.getName().equals("main");
	}

	public void addCommand(String name, Method... methods) {
		addCommand(null, name, methods);
	}

	private void addCommand(Object object, String name, Method... methods) {
		if (methods.length <= 0) throw new IllegalArgumentException("At least one method must be added.");

		if (methods.length > 1 && !assertOverloading(methods))
			throw new IllegalArgumentException("Only overloaded actions should be added in one addAction call.");

		if (commands.containsKey(name))
			throw new IllegalArgumentException("There is already a command named '" + name + "'.");

		commands.put(name, new Command(object, methods));
	}

	private boolean assertOverloading(Method[] methods) {
		Class<?> clazz = methods[0].getDeclaringClass();
		String name = methods[0].getName();

		for (Method m : methods) {
			if (clazz != m.getDeclaringClass() || !name.equals(m.getName())) return false;
		}

		return true;
	}

	public void printUsage() {
		int maxParams = getMaxNumberOfParams();
		ConsoleTable table = new ConsoleTable();
		for (String cmd : commands.keySet()) {
			Command command = commands.get(cmd);

			Iterator<Method> it = command.getMethods().iterator();
			Method m = null;
			while (it.hasNext()) {
				table.addRow(m == null ? cmd : "");
				m = it.next();
				if (showParameterNames) {
					table.appendRow(getParameterNames(m));
				} else {
					table.appendRow(getSimpleParameterTypes(m));
				}
				table.appendRowFromColumn(maxParams + 2, getCallDescription(m));
			}
		}
		System.out.println(table);
	}

	public ConsoleApp showParameterNames() {
		this.showParameterNames = true;
		return this;
	}

	private String[] getSimpleParameterTypes(Method method) {
		List<String> types = new LinkedList<String>();
		for (Class<?> clazz : method.getParameterTypes()) {
			types.add(clazz.getSimpleName());
		}
		String[] result = new String[types.size()];
		types.toArray(result);
		return result;
	}

	private String[] getParameterNames(Method method) {
		String[] names = new String[method.getParameterTypes().length];

		Annotation[][] annotations = method.getParameterAnnotations();
		outer: for (int i = 0; i < names.length; i++) {
			for (Annotation a : annotations[i]) {
				if (a instanceof ParameterDescription) {
					names[i] = ((ParameterDescription) a).name();
					continue outer;
				}
				throw new IllegalStateException(
						"Cannot use parameter names, because parameter annotations are missing.");
			}
		}
		return names;
	}

	private String getCallDescription(Method method) {
		CallDescription desc = method.getAnnotation(CallDescription.class);
		return (desc != null) ? desc.text() : "";
	}

	private int getMaxNumberOfParams() {
		int n = 0;
		for (Command command : commands.values()) {
			n = Math.max(n, command.getMaxNumberOfParams());
		}
		return n;
	}

	public ConsoleApp setExecutionMode(ExecutionMode mode) {
		if (this.mode == ExecutionMode.RUN_UNTIL_EXIT && this.mode != mode) removeExitCommand();
		if (mode == ExecutionMode.RUN_UNTIL_EXIT) addExitCommand();
		this.mode = mode;
		return this;
	}

	private void addExitCommand() {
		try {
			addCommand(this, "exit", ConsoleApp.class.getDeclaredMethod("exit"));
		} catch (NoSuchMethodException e) {
			// should never happen
			throw new RuntimeException(e);
		}
	}

	private void removeExitCommand() {
		commands.remove("exit");
	}

	@SuppressWarnings("unused")
	@CallDescription(text = "Quits the program.")
	private void exit() {
		this.exit = true;
	}

	public void execute() {
		Scanner in = new Scanner(System.in);

		do {
			System.out.print("> ");
			String input = in.nextLine();

			if (input.trim().isEmpty()) continue;

			Command command = parseCommand(input);
			if (command == null) {
				System.out.println("Unknown command '" + parseCommandName(input) + "'.");
			} else {
				try {
					if (!command.execute(stripCommandName(input)))
						System.out.println("Invalid arguments for '" + parseCommandName(input) + "'.");
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				}
			}
		} while (!stopRunning());
	}

	private boolean stopRunning() {
		switch (mode) {
			case RUN_ONCE:
				return true;
			case RUN_UNTIL_EXIT:
				return exit;
			case ASK_TO_CONTINUE:
				return !askToContinue();
			default:
				throw new RuntimeException("ExecutionMode unknown.");
		}
	}

	private boolean askToContinue() {
		System.out.print("Continue? [Y/n]: ");
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		if (input.toLowerCase().startsWith("n")) { return false; }
		return true;
	}

	private String parseCommandName(String input) {
		int firstSpace = input.indexOf(' ');
		if (firstSpace == -1) { return input.substring(0); }
		return input.substring(0, firstSpace);
	}

	private String stripCommandName(String input) {
		int firstSpace = input.indexOf(' ');
		if (firstSpace == -1) return "";
		return input.substring(input.indexOf(' ')).trim();
	}

	private Command parseCommand(String input) {
		return commands.get(parseCommandName(input));
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface CallDescription {

		String text();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ParameterDescription {

		String name();

		String text() default "";
	}

	private static class Command {

		private Object object;
		private List<Method> methods;

		public Command(Object object, Method[] methods) {
			this.object = object;
			this.methods = new ArrayList<Method>(Arrays.asList(methods));
			// sort by descending number of arguments
			Collections.sort(this.methods, new Comparator<Method>() {

				@Override
				public int compare(Method o1, Method o2) {
					return o2.getParameterTypes().length - o1.getParameterTypes().length;
				}
			});
		}

		public boolean execute(String input) {
			input = input.trim();
			int n = (input.isEmpty()) ? 0 : input.split(" ").length;
			for (Method m : methods) {
				if (numParams(m) > n) continue;
				if (numParams(m) < n) break;

				Object[] parameters = ParameterMatcher.match(input, m.getParameterTypes());
				if (parameters != null) {
					Object result = null;
					try {
						m.setAccessible(true);
						result = m.invoke(getObject(), parameters);
					} catch (IllegalArgumentException e) {
						// rethrow
						throw e;
					} catch (IllegalAccessException e) {
						// should never happen
						throw new RuntimeException(e);
					} catch (InvocationTargetException e) {
						// if it's caused by an IllegalArgumentException, rethrow IllegalArgumentException
						if (e.getCause() != null && e.getCause() instanceof IllegalArgumentException)
							throw (IllegalArgumentException) e.getCause();
					}
					if (m.getReturnType() != void.class) System.out.println(result);
					return true;
				}
			}
			return false;
		}

		public Object getObject() {
			return object;
		}

		public List<Method> getMethods() {
			return methods;
		}

		public int getMaxNumberOfParams() {
			return numParams(methods.get(0));
		}

		private int numParams(Method m) {
			return m.getParameterTypes().length;
		}
	}
}
