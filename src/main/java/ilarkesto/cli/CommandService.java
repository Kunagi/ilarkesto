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
package ilarkesto.cli;

import ilarkesto.auth.LoginRequiredException;
import ilarkesto.base.Str;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CommandService {

	private Collection commands = new ArrayList();

	public Collection getCommands() {
		return this.commands;
	}

	public Object execute(String commandLine) throws NoSuchCommandException, CommandExecutionFailedException,
			LoginRequiredException {
		String[] sa = Str.tokenize(commandLine);
		if (sa.length < 1) throw new NoSuchCommandException(commandLine);
		return execute(sa[0], Str.subarray(sa, 1));
	}

	public Object execute(String command, String[] arguments) throws NoSuchCommandException,
			CommandExecutionFailedException, LoginRequiredException {
		ACommand c = getCommand(command);
		if (c == null) throw new NoSuchCommandException(command);

		return execute(c, arguments);
	}

	public static Object execute(ACommand c, String[] arguments) throws CommandExecutionFailedException,
			LoginRequiredException {
		Arguments a = c.createArguments();
		a.update(arguments);
		c.assertPermissions();
		return c.execute(a);
	}

	public ACommand getCommand(String name) {
		for (Iterator iter = commands.iterator(); iter.hasNext();) {
			ACommand command = (ACommand) iter.next();
			if (name.equals(command.getName())) return command;
			for (Iterator iterator = command.getAliases().iterator(); iterator.hasNext();) {
				if (name.equals(iterator.next())) return command;
			}
		}
		return getCommandByShortcut(name);
	}

	private ACommand getCommandByShortcut(String name) {
		for (Iterator iter = commands.iterator(); iter.hasNext();) {
			ACommand command = (ACommand) iter.next();
			if (command.getName().startsWith(name)) return command;
			for (Iterator iterator = command.getAliases().iterator(); iterator.hasNext();) {
				if (((String) iterator.next()).startsWith(name)) return command;
			}
		}
		return null;
	}

	public void add(ACommand command) {
		// TODO check for alias/name-collision
		commands.add(command);
	}

	// --- dependencies ---

}
