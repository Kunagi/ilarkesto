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

import java.util.HashMap;
import java.util.Map;

public class Log {

	private static final Log ANONYMOUS = new Log("?");
	private static final Map<String, Log> LOGGERS = new HashMap<String, Log>();
	private static boolean debugEnabled = true;
	private static LogRecordHandler logRecordHandler = new PrintStreamLogDataHandler(System.err);

	private String name;

	public Log(String name) {
		this.name = name;
	}

	public void log(Level level, Object... parameters) {
		logRecordHandler.log(new LogRecord(name, level, parameters));
	}

	/**
	 * Logs an fatal error to the system admin. A fatal error indicates an error that prevents the system from
	 * working at all.
	 */
	public void fatal(Object... s) {
		log(Level.FATAL, s);
	}

	/**
	 * Logs an error to the system admin.
	 */
	public void error(Object... s) {
		log(Level.ERROR, s);
	}

	/**
	 * Logs a warning to the system admin.
	 */
	public void warn(Object... s) {
		log(Level.WARN, s);
	}

	/**
	 * Logs an information to the system admin.
	 */
	public void info(Object... s) {
		log(Level.INFO, s);
	}

	/**
	 * Indicates if debug is enabled. If it is not, {@link #debug(Object[])} does nothing.
	 * 
	 * @see #debug(Object[])
	 */
	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	/**
	 * Logs a debug information. Could be disabled.
	 * 
	 * @see #isDebugEnabled()
	 */
	public void debug(Object... s) {
		if (!isDebugEnabled()) return;
		log(Level.DEBUG, s);
	}

	public static void flush() {
		logRecordHandler.flush();
	}

	public static final Log get(Class type) {
		return get(Str.getSimpleName(type));
	}

	public static final Log get(String name) {
		Log logger = LOGGERS.get(name);
		if (logger == null) {
			logger = new Log(name);
			LOGGERS.put(name, logger);
		}
		return logger;

	}

	public static void setDebugEnabled(boolean debugEnabled) {
		if (Log.debugEnabled == debugEnabled) return;
		Log.debugEnabled = debugEnabled;
		if (debugEnabled) {
			Log.get(Log.class).info("Debug-logging enabled.");
		} else {
			Log.get(Log.class).info("Debug-logging disabled.");
		}
	}

	public static void DEBUG(Object... s) {
		ANONYMOUS.debug(s);
	}

	public static void setLogRecordHandler(LogRecordHandler logDataHandler) {
		Log.logRecordHandler = logDataHandler;
	}

	public static enum Level {
		DEBUG, INFO, WARN, ERROR, FATAL;

		public boolean isWarnOrWorse() {
			return isErrorOrWorse() || (this == WARN);
		}

		public boolean isErrorOrWorse() {
			return (this == FATAL) || (this == ERROR);
		}
	}

}
