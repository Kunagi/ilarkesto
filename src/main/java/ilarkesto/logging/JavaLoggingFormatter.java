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
package ilarkesto.logging;

import ilarkesto.base.Str;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JavaLoggingFormatter extends Formatter {

	private static final int LOGGER_WIDTH = 30;

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(Str.fillUpRight(Str.cutRight(record.getLevel().getName(), 4), " ", 5));
		sb.append(Str.fillUpRight(Str.cutLeft(record.getLoggerName(), LOGGER_WIDTH - 1, ".."), " ", LOGGER_WIDTH));
		sb.append("-> ");
		sb.append(record.getMessage());
		sb.append("\n");
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter out = new PrintWriter(sw);
			record.getThrown().printStackTrace(out);
			out.flush();
			sb.append(sw.toString());
		}
		return sb.toString();
	}

	public static final JavaLoggingFormatter INSTANCE = new JavaLoggingFormatter();

	public static void install() {
		Handler[] handler = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handler.length; i++) {
			handler[i].setFormatter(INSTANCE);
		}
	}

}
