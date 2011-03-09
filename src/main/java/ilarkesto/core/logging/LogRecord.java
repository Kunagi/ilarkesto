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
package ilarkesto.core.logging;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log.Level;

public class LogRecord {

	public final String name;
	public final Level level;
	public final Object[] parameters;
	public String context;

	public LogRecord(String name, Level level, Object[] parameters) {
		super();
		this.name = name;
		this.level = level;
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		String nameFormated = Str.cutLeft(name, 20);
		nameFormated = Str.fillUpRight(nameFormated, " ", 20);

		StringBuilder sb = new StringBuilder();

		// level
		if ((level != Level.DEBUG) && (level != Level.INFO)) sb.append("\n    ");
		if (level != Level.DEBUG) sb.append(level);

		// logger
		sb.append(" ").append(nameFormated);

		// text
		StringBuilder text = new StringBuilder();
		if (parameters == null) {
			text.append(" <null>");
		} else {
			for (Object parameter : parameters) {
				text.append(' ');
				if (parameter instanceof Throwable) {
					text.append("\n").append(Str.getStackTrace((Throwable) parameter));
				} else {
					text.append(Str.format(parameter));
				}
			}
		}
		sb.append(Str.fillUpRight(text.toString(), " ", 100));

		// context
		if (context != null) sb.append(" | ").append(context);

		// extra line for high prio logs
		if ((level != Level.DEBUG) && (level != Level.INFO)) sb.append('\n');

		return sb.toString();
	}

}
