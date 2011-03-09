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
package ilarkesto.di.app;

import ilarkesto.cli.ACommand;
import ilarkesto.cli.BadSyntaxException;
import ilarkesto.cli.CommandExecutionFailedException;
import ilarkesto.cli.CommandService;
import ilarkesto.core.logging.Log;

public class CommandApplication extends ACommandLineApplication {

	private static final Log LOG = Log.get(CommandApplication.class);

	// --- dependencies ---

	private Class<? extends ACommand> commandClass;

	public void setCommandClass(Class<? extends ACommand> commandClass) {
		this.commandClass = commandClass;
	}

	// --- ---

	@Override
	protected int execute(String[] args) {
		ACommand command;
		try {
			command = commandClass.newInstance();
		} catch (InstantiationException ex1) {
			throw new RuntimeException(ex1);
		} catch (IllegalAccessException ex1) {
			throw new RuntimeException(ex1);
		}
		autowire(command);
		Object result;
		try {
			result = CommandService.execute(command, args);
		} catch (BadSyntaxException ex) {
			System.out.println("Bad Syntax: " + ex.getMessage());
			System.out.println("Syntax:\n\n" + command.getUsage());
			return 1;
		} catch (CommandExecutionFailedException ex) {
			throw new RuntimeException(ex);
		}
		if (result != null) {
			System.out.println(result);
		}
		return 0;
	}

}
