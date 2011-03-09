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

import ilarkesto.base.Colors;
import ilarkesto.console.ConsoleApp.CallDescription;
import ilarkesto.console.ConsoleApp.ExecutionMode;
import ilarkesto.console.ConsoleApp.ParameterDescription;

/**
 * This is a Facade class for {@link Colors} demonstrating the use of {@link ConsoleApp}.
 * 
 * Methods in such a Facade class can also be used to handle complex input that needs to be converted to
 * objects that cannot be automatically casted from Strings.
 * 
 * The ConsoleApp is automatically created from public static methods of this class, using given Annotations.
 * Additionally, in {@link Colors}, the clean checking of input parameters is helpful, since
 * {@link ConsoleApp} catches and handles {@link IllegalArgumentException}.
 * 
 * @author ako
 */
public class ColorsConsoleApp {

	public static void main(String[] args) {
		ConsoleApp app = ConsoleApp.fromClass(ColorsConsoleApp.class);
		app.setExecutionMode(ExecutionMode.RUN_UNTIL_EXIT).showParameterNames();
		app.printUsage();
		app.execute();
	}

	@CallDescription(text = "Blends two colors a and b with the given ratio 0.0 <= r <= 1.0.")
	public static String blend(@ParameterDescription(name = "a") String a, @ParameterDescription(name = "b") String b,
			@ParameterDescription(name = "r") float r) {
		return Colors.blend(a, b, r);
	}

	@CallDescription(text = "Blends two colors a and b (with the ratio 0.5).")
	public static String blend(@ParameterDescription(name = "a") String a, @ParameterDescription(name = "b") String b) {
		return Colors.blend(a, b);
	}

	@CallDescription(text = "Darkens a color (by 0.1 where 0.0 is blac and 1.0 is white).")
	public static String darken(@ParameterDescription(name = "a") String a) {
		return Colors.darken(a);
	}

	@CallDescription(text = "Lightens a color (by 0.1 where 0.0 is blac and 1.0 is white).")
	public static String lighten(@ParameterDescription(name = "a") String a) {
		return Colors.lighten(a);
	}
}
