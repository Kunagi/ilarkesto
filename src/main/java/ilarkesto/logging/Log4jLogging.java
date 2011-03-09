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
import ilarkesto.core.logging.Log;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class Log4jLogging {

	private static Log log = Log.get(Log4jLogging.class);

	public static void redirectToLoggers() {

		Logger rootLogger = Logger.getRootLogger();
		rootLogger.removeAllAppenders();

		rootLogger.addAppender(new LoggerRedirectionAppender());
		log.debug("Redirecting Log4j Logging to ilarkesto loggers");
	}

	private static void redirectToLogger(LoggingEvent event) {
		String loggerName = event.getLoggerName();
		int idx = loggerName.lastIndexOf('.');
		if (idx > 0) {
			loggerName = loggerName.substring(idx + 1);
		}

		Log logger = Log.get(loggerName);
		logger.log(toLevel(event.getLevel()), getMessage(event));
	}

	public static String getMessage(LoggingEvent event) {
		return Str.format(event.getMessage());
	}

	public static Log.Level toLevel(Level log4jLevel) {
		if (log4jLevel == Level.TRACE || log4jLevel == Level.DEBUG || log4jLevel == Level.OFF
				|| log4jLevel == Level.INFO) {
			return Log.Level.DEBUG;
		} else if (log4jLevel == Level.WARN) {
			return Log.Level.WARN;
		} else if (log4jLevel == Level.FATAL) {
			return Log.Level.FATAL;
		} else {
			return Log.Level.INFO;
		}
	}

	static class LoggerRedirectionAppender implements Appender {

		private ErrorHandler errorHandler;
		private String name;
		private Layout layout;
		private Filter filter;

		@Override
		public void doAppend(LoggingEvent event) {
			redirectToLogger(event);
		}

		@Override
		public void close() {
			Log.flush();
		}

		@Override
		public void addFilter(Filter filter) {
			this.filter = filter;
		}

		@Override
		public void clearFilters() {
			filter = null;
		}

		@Override
		public Filter getFilter() {
			return filter;
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		@Override
		public void setLayout(Layout layout) {
			this.layout = layout;
		}

		@Override
		public Layout getLayout() {
			return layout;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setErrorHandler(ErrorHandler handler) {
			this.errorHandler = handler;
		}

		@Override
		public ErrorHandler getErrorHandler() {
			return errorHandler;
		}

	}
}
