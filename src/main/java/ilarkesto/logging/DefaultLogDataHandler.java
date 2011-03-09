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
import ilarkesto.base.Sys;
import ilarkesto.core.logging.Log;
import ilarkesto.core.logging.LogRecord;
import ilarkesto.core.logging.LogRecordHandler;
import ilarkesto.io.IO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class DefaultLogDataHandler implements LogRecordHandler {

	private static final Log LOG = Log.get(DefaultLogDataHandler.class);
	public static final DateFormat LOG_TIME_FORMAT = new SimpleDateFormat("EEE, dd. MMMM yyyy, HH:mm");
	public static final DefaultLogDataHandler INSTANCE = new DefaultLogDataHandler();

	private BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<LogRecord>();
	private File logFile;
	private Thread sysoutThread;
	private boolean shutdown = false;

	public static void activate() {}

	private DefaultLogDataHandler() {
		System.err.println("Initializing logging system");
		sysoutThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						synchronized (Log.class) {
							if (shutdown && queue.isEmpty()) {
								System.err.println("Shutting down logging system");
								break;
							}
						}
						LogRecord record = queue.poll(Long.MAX_VALUE, TimeUnit.SECONDS);
						System.err.println(record.toString());
					} catch (InterruptedException ignored) {
						shutdown = true;
					}
				}
			}
		});
		sysoutThread.setName(Log.class.getName());
		sysoutThread.setPriority(Thread.MIN_PRIORITY);
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				stopLogging();
			}
		});
		sysoutThread.start();

		Log.setLogRecordHandler(this);
	}

	public static void stopLogging() {
		INSTANCE.flush();
		synchronized (Log.class) {
			INSTANCE.shutdown = true;
		}
		if (INSTANCE.sysoutThread != null) INSTANCE.sysoutThread.interrupt();
	}

	@Override
	public void log(LogRecord record) {
		record.context = Thread.currentThread().getName();
		try {
			queue.put(record);
		} catch (InterruptedException e) {
			return;
		}

		if (record.level.isWarnOrWorse()) appendToFile(record.toString());
	}

	@Override
	public void flush() {
		while (!queue.isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public static boolean setLogFile(File file) {
		if (!IO.isFileWritable(file)) return false;
		INSTANCE.logFile = file;
		LOG.info("Log-file:", file);
		return true;
	}

	public static File getLogFile() {
		return INSTANCE.logFile;
	}

	public static boolean setLogFileToHomeOrWorkdir(String name) {
		if (setLogFileToWorkdir(name)) return true;
		return setLogFileToHome(name);
	}

	public static boolean setLogFileToHome(String name) {
		boolean ok = setLogFile(new File(Sys.getUsersHomePath() + "/" + name + ".log"));
		if (ok) return true;
		return setLogFile(new File(Sys.getUsersHomePath() + "/webapps/" + name + ".log"));
	}

	public static boolean setLogFileToWorkdir(String name) {
		boolean ok = setLogFile(new File(Sys.getWorkDir() + "/" + name + ".log"));
		if (ok) return true;
		return setLogFile(new File(Sys.getWorkDir() + "/webapps/" + name + ".log"));
	}

	private void appendToFile(String record) {
		if (logFile == null) {
			File runtimedataDir = new File("runtimedata");
			if (runtimedataDir.exists() && runtimedataDir.isDirectory()) {
				setLogFile(new File("runtimedata/error.log"));
			} else {
				setLogFile(new File("error.log"));
			}
		}
		if (logFile == null) return;
		synchronized (logFile) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(logFile, logFile.length() < 1048576));
				out.write("--------------------------------------------------------------------------------\n");
				out.write(LOG_TIME_FORMAT.format(new Date()));
				out.write(" -> ");
				out.write(record);
				out.write('\n');
				out.close();
			} catch (Exception e) {
				System.err.println("Failed to write to logFile: " + logFile.getAbsolutePath() + ": " + Str.format(e));
			}
		}
	}

}
