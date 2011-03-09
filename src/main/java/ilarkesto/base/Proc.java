/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.base;

import ilarkesto.core.logging.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for easy handling of external processes and executing commands.
 */
public final class Proc {

	public static void main(String[] args) {
		Proc proc = new Proc("sudo");
		proc.addParameter("ls");
		proc.start();
		proc.getOutput();
	}

	private static final Log LOG = Log.get(Proc.class);

	/**
	 * Starts the process and waits until it ends. Returns output when return code is 0. Otherwise throws
	 * Exception.
	 */
	public static String execute(String command, String... parameters) throws UnexpectedReturnCodeException {
		return execute(null, command, parameters);
	}

	public static String execute(File workDir, String command, String... parameters)
			throws UnexpectedReturnCodeException {
		Proc proc = new Proc(command);
		proc.setWorkingDir(workDir);
		for (String parameter : parameters) {
			proc.addParameter(parameter);
		}
		return proc.execute();
	}

	/**
	 * Starts the process and waits until it ends. Returns output when return code is 0. Otherwise throws
	 * Exception.
	 */
	public String execute() throws UnexpectedReturnCodeException {
		return execute(0);
	}

	public String execute(int... acceptableReturnCodes) throws UnexpectedReturnCodeException {
		start();
		int rc = getReturnCode();
		boolean rcOk = false;
		for (int acceptableReturnCode : acceptableReturnCodes) {
			if (rc == acceptableReturnCode) {
				rcOk = true;
				continue;
			}
		}
		if (!rcOk) {
			StringBuilder cmdline = new StringBuilder();
			cmdline.append(command);
			if (parameters != null) {
				for (String parameter : parameters) {
					cmdline.append(" ").append(parameter);
				}
			}
			throw new UnexpectedReturnCodeException(rc, cmdline.toString(), getOutput());
		}
		return getOutput();
	}

	/**
	 * Start the process.
	 */
	public synchronized void start() {
		if (process != null) throw new RuntimeException("Process already started.");

		int paramLen = parameters == null ? 0 : parameters.size();
		String[] cmdarray = new String[paramLen + 1];
		cmdarray[0] = command;
		if (paramLen > 0) System.arraycopy(parameters.toArray(), 0, cmdarray, 1, paramLen);
		if (LOG.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			if (workingDir == null) {
				sb.append(">");
			} else {
				sb.append(workingDir).append(">");
			}
			for (String s : cmdarray) {
				sb.append(" ").append(s);
			}
			LOG.debug(sb.toString());
		}
		try {
			process = Runtime.getRuntime().exec(cmdarray, getEnvParameters(), workingDir);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		output = new StringBuffer();
		new StreamGobbler(process.getInputStream());
		new StreamGobbler(process.getErrorStream());
	}

	public Process getProcess() {
		return process;
	}

	/**
	 * Wait until the process is finishes.
	 */
	public void waitFor() {
		getReturnCode();
	}

	/**
	 * Gets the return code of the process. Waits until process finishes.
	 */
	public int getReturnCode() {
		if (returnCode == null) {
			if (process == null) throw new RuntimeException("Process not started yet.");
			try {
				process.waitFor();
			} catch (InterruptedException ex) {
				throw new RuntimeException("Command interrupted: " + command, ex);
			}
			returnCode = process.exitValue();
			LOG.debug("    " + command + ":", "rc:", returnCode);
		}
		return returnCode;
	}

	/**
	 * Gets the standard output of the process.
	 */
	public String getOutput() {
		if (output == null) throw new RuntimeException("Process not started yet.");
		return output.toString().trim();
	}

	private Process process;

	private StringBuffer output;

	private Integer returnCode;

	// --- static convinience methods ---

	// public static int execAndGetReturnCode(String command, String... parameters) {
	// Proc proc = new Proc(command);
	// proc.setParameters(parameters);
	// proc.start();
	// return proc.getReturnCode();
	// }

	// --- sub classes ---

	private class StreamGobbler extends Thread {

		private InputStream is;

		StreamGobbler(InputStream is) {
			this.is = is;
			setDaemon(true);
			super.start();
		}

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					synchronized (output) {
						output.append(line).append("\n");
					}
				}
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public Proc addEnvironmentProperty(String name, String value) {
		if (environment == null) environment = new HashMap<String, String>();
		environment.put(name, value);
		return this;
	}

	private String[] getEnvParameters() {
		if (environment == null || environment.size() == 0) return null;
		String[] env = new String[environment.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : environment.entrySet()) {
			env[i++] = entry.getKey() + "=" + entry.getValue();
		}
		return env;
	}

	// --- dependencies ---

	private Map<String, String> environment;

	public void setEnvironment(Map<String, String> environment) {
		this.environment = environment;
	}

	public void addEnvironmentParameter(String name, String value) {
		if (environment == null) environment = new HashMap<String, String>();
		environment.put(name, value);
	}

	private String command;

	public Proc(String command) {
		this.command = command;
	}

	private List<String> parameters;

	public Proc setParameters(List<String> parameters) {
		this.parameters = parameters;
		return this;
	}

	public Proc addParameter(String parameter) {
		if (parameters == null) parameters = new ArrayList<String>(1);
		parameters.add(parameter);
		return this;
	}

	public Proc addParameters(String... parameters) {
		for (String parameter : parameters)
			addParameter(parameter);
		return this;
	}

	private File workingDir;

	public Proc setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
		return this;
	}

	public static class UnexpectedReturnCodeException extends RuntimeException {

		private int rc;
		private String cmdline;
		private String output;

		public UnexpectedReturnCodeException(int rc, String cmdline, String output) {
			super("Command rc=" + rc + ": " + cmdline + "\n" + output);
			this.rc = rc;
			this.cmdline = cmdline;
			this.output = output;
		}

		public int getRc() {
			return rc;
		}

		public String getOutput() {
			return output;
		}

		public String getCmdline() {
			return cmdline;
		}
	}

}
