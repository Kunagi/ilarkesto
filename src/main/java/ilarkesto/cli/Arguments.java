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

import ilarkesto.base.Str;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Arguments {

	private Collection options = new ArrayList();
	private boolean optionsDisabled;
	private Collection parameters = new ArrayList();
	private String[] remainder;
	private boolean acceptRemainder;
	private String remainderSyntax;
	private ACommand command;
	private Map remainderParameterDescriptionMap = new HashMap();

	public Arguments(ACommand command) {
		this.command = command;
	}

	public Arguments(ACommand command, String remainderSyntax) {
		this(command);
		if (remainderSyntax != null) {
			acceptRemainder = true;
			this.remainderSyntax = remainderSyntax;
		}
	}

	public void addRemainderParameterDescription(String name, String description) {
		remainderParameterDescriptionMap.put(name, description);
	}

	public String getUsage() {
		StringBuffer sb = new StringBuffer();

		// commandline
		if (options.size() > 0) {
			sb.append(" [OPTIONS]");
		}
		for (Iterator iter = parameters.iterator(); iter.hasNext();) {
			AParameter parameter = (AParameter) iter.next();
			sb.append(" <").append(parameter.getName()).append(">");
		}
		if (remainderSyntax != null) {
			sb.append(" ").append(remainderSyntax);
		}

		// command description
		sb.append("\n\n").append(command.getDescription());

		// parameter descriptions

		if (parameters.size() > 0 || remainderParameterDescriptionMap.size() > 0) {
			sb.append("\n\nParameters:");
			int len = 0;
			Map parameterNameUsageMap = new HashMap();
			for (Iterator iter = parameters.iterator(); iter.hasNext();) {
				AParameter parameter = (AParameter) iter.next();
				String name = parameter.getName();
				if (name.length() > len) len = name.length();
				parameterNameUsageMap.put(name, parameter.getDescription());
			}
			for (Iterator iter = remainderParameterDescriptionMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Entry) iter.next();
				String name = (String) entry.getKey();
				if (name.length() > len) len = name.length();
				parameterNameUsageMap.put(name, entry.getValue());
			}
			len += 3;
			for (Iterator iter = parameterNameUsageMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				sb.append("\n  ").append(Str.fillUpRight((String) entry.getKey(), " ", len));
				sb.append(entry.getValue());
			}
		}

		// option descriptions
		if (options.size() > 0) {
			sb.append("\n\nOptions:");
			int len = 0;
			Map optionNameUsageMap = new HashMap();
			for (Iterator iter = options.iterator(); iter.hasNext();) {
				AOption option = (AOption) iter.next();
				String name = option.getUsageSyntax();
				if (name.length() > len) len = name.length();
				optionNameUsageMap.put(name, option.getUsageDescription());
			}
			len += 3;
			for (Iterator iter = optionNameUsageMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				sb.append("\n  ").append(Str.fillUpRight((String) entry.getKey(), " ", len));
				sb.append(entry.getValue());
			}
		}

		return sb.toString();
	}

	public void update(String[] args) throws BadSyntaxException {
		if (args == null) args = new String[0];

		int i = 0;
		if (!optionsDisabled) {
			// options
			for (; i < args.length; i++) {
				String arg = args[i];
				String nextArg = i >= args.length - 1 ? null : args[i + 1];
				if (arg.startsWith("-")) {
					AOption option = getOption(arg.substring(1));
					if (option == null) { throw new BadSyntaxException(command, "Unsupported option: " + arg); }
					if (option instanceof ValueOption) {
						if (nextArg == null) throw new BadSyntaxException(command, "Option needs argument: " + arg);
						i++;
					}
					option.setValue(nextArg);
				} else {
					break;
				}
			}
		}

		// parameters
		for (Iterator iter = parameters.iterator(); iter.hasNext();) {
			AParameter parameter = (AParameter) iter.next();
			if (i >= args.length) throw new BadSyntaxException(command, "Missing parameter: " + parameter.getName());
			parameter.setValue(args[i]);
			i++;
		}

		// remainder
		if (acceptRemainder) {
			remainder = new String[args.length - i];
			System.arraycopy(args, i, remainder, 0, args.length - i);
		} else {
			if (i < args.length - 1) throw new BadSyntaxException(command, "Too much parameters");
		}
	}

	public String[] getRemainder() {
		return remainder;
	}

	public String getRemainderAsString() {
		if (remainder.length == 0) return null;
		return Str.concat(remainder, " ");
	}

	public boolean hasReminder() {
		return remainder != null && remainder.length > 0;
	}

	public void disableOptions() {
		optionsDisabled = true;
	}

	protected void add(AOption option) {
		options.add(option);
	}

	public void add(AParameter parameter) {
		parameters.add(parameter);
	}

	private AOption getOption(String name) {
		for (Iterator iter = options.iterator(); iter.hasNext();) {
			AOption option = (AOption) iter.next();
			if (name.equals(option.getName())) return option;
		}
		return null;
	}

}
