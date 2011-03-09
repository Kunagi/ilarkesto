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
package ilarkesto.base;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapuslation of platform specific operations. Executing programs and files. System directories.
 */
public abstract class Env {

	public static void main(String[] args) {
		System.out.println(Env.get().getFileSize(new File("/home/witek/inbox")));
	}

	private static final Log LOG = Log.get(Env.class);

	private static Env env;

	/**
	 * Starts the default file browser with the given directory or file.
	 */
	public abstract void startFileBrowser(File file);

	/**
	 * Gets the directory where applications are stored.
	 */
	public abstract File getApplicationsDir();

	/**
	 * Creates an application starter (icon).
	 * 
	 * @param file Starter file.
	 * @param targetPath Command which the starter starts.
	 * @param arguments Arguments for the command.
	 * @param description Starters description.
	 * @param iconPath Icon location for the starter.
	 * @param workingDir Working directory for the command.
	 */
	public abstract void createStarter(File file, String targetPath, String arguments, String description,
			String iconPath, String workingDir);

	/**
	 * Executes a VisualBasic script.
	 * 
	 * @param script The script itself. Not a path to the script.
	 */
	public abstract void executeVbScript(String script);

	/**
	 * Executes/opens the given file.
	 */
	public abstract void executeFile(File file, boolean block);

	public abstract File getStartMenuDir();

	public abstract File getAutostartDir();

	public abstract File getSendtoDir();

	public abstract File getMyfilesDir();

	public abstract File getMusicDir();

	public abstract File getSystemDir();

	/**
	 * Gets all mounted directories. Particularily removable devices.
	 */
	public abstract List<File> getMountedDirs();

	public static Env get() {
		if (env == null) {
			env = Sys.isWindows() ? new Windows() : new Linux();
		}
		return env;
	}

	static class Windows extends Env {

		private File applicationsDir;
		private File startMenuDir;
		private File autostartDir;
		private File sendtoDir;
		private File myfilesDir;
		private File musicDir;
		private File systemDir;
		private File windowsDir;

		@Override
		public List<File> getMountedDirs() {
			List<File> mountedDirs = new ArrayList<File>();
			File sysDir = getWindowsDir();
			if (sysDir != null) sysDir = sysDir.getParentFile();
			for (File root : File.listRoots()) {
				if (sysDir != null && sysDir.getPath().equals(root.getPath())) continue;
				mountedDirs.add(root);
			}
			return mountedDirs;
		}

		@Override
		public File getSystemDir() {
			if (systemDir == null) {
				systemDir = getWindowsDir().getParentFile();
			}
			return systemDir;
		}

		public File getWindowsDir() {
			if (windowsDir == null) {
				for (File root : File.listRoots()) {
					File dir = new File(root.getAbsolutePath() + "/windows");
					if (dir.exists()) {
						windowsDir = dir;
						break;
					}
					dir = new File(root.getAbsolutePath() + "/winnt");
					if (dir.exists()) {
						windowsDir = dir;
						break;
					}
				}
			}
			return windowsDir;
		}

		@Override
		public File getMyfilesDir() {
			if (myfilesDir == null) {
				myfilesDir = new File(Sys.getUsersHomePath() + "/Eigene Dateien");
				if (!myfilesDir.exists()) {
					myfilesDir = new File(Sys.getUsersHomePath() + "/My Files");
				}
			}
			return myfilesDir;
		}

		@Override
		public File getMusicDir() {
			if (musicDir == null) {
				musicDir = new File(getMyfilesDir().getPath() + "/Eigene Musik");
			}
			return musicDir;
		}

		@Override
		public File getStartMenuDir() {
			if (startMenuDir == null) {
				String home = Sys.getUsersHomePath();
				startMenuDir = new File(home + "/Startmen\u00FC");
				if (!startMenuDir.exists()) {
					startMenuDir = new File(home + "/Startmenu");
					if (!startMenuDir.exists()) {
						IO.createDirectory(startMenuDir);
					}
				}
			}
			return startMenuDir;
		}

		@Override
		public File getAutostartDir() {
			if (autostartDir == null) {
				File startMenuFolder = getStartMenuDir();
				autostartDir = new File(startMenuFolder.getPath() + "/Programme/Autostart");
				if (!autostartDir.exists()) {
					autostartDir = new File(startMenuFolder.getPath() + "/Programs/Autostart");
					if (!autostartDir.exists()) {
						IO.createDirectory(autostartDir);
					}
				}
			}
			return autostartDir;
		}

		@Override
		public File getSendtoDir() {
			if (sendtoDir == null) {
				String home = Sys.getUsersHomePath();
				sendtoDir = new File(home + "/SendTo");
				if (!sendtoDir.exists()) {
					IO.createDirectory(sendtoDir);
				}
			}
			return sendtoDir;
		}

		@Override
		public void createStarter(File file, String targetPath, String arguments, String description, String iconPath,
				String workingDir) {
			targetPath = targetPath.replace('/', '\\');
			if (workingDir != null) workingDir = workingDir.replace('/', '\\');
			StringBuilder sb = new StringBuilder();
			sb.append("Set oWS = WScript.CreateObject(\"WScript.Shell\")\n");
			sb.append("Set oLink = oWS.CreateShortcut( \"").append(file.getAbsolutePath()).append("\" )\n");
			sb.append("oLink.TargetPath = \"").append(targetPath).append("\"\n");
			if (arguments != null) sb.append("oLink.Arguments = \"").append(arguments).append("\"\n");
			if (description != null) sb.append("oLink.Description = \"").append(description).append("\"\n");
			if (iconPath != null) sb.append("oLink.IconLocation = \"").append(iconPath).append("\"\n");
			if (workingDir != null) sb.append("oLink.WorkingDirectory = \"").append(workingDir).append("\"\n");
			sb.append("oLink.Save\n");
			executeVbScript(sb.toString());
		}

		@Override
		public void executeVbScript(String script) {
			LOG.debug("Executing Visual Basic Script", "\n--- VBS BEGIN ---\n" + script + "--- VBS END ---\n");
			File file;
			try {
				file = File.createTempFile("Env.executeVbScript.", ".vbs");
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			IO.writeFile(file.getPath(), script, Sys.getFileEncoding());
			try {
				executeFile(file, true);
			} finally {
				IO.delete(file);
			}
		}

		@Override
		public void executeFile(File file, boolean block) {
			Proc proc = new Proc("CMD");
			proc.addParameter("/C");
			proc.addParameter(file.getAbsolutePath());
			proc.start();
			if (block) {
				int returnCode = proc.getReturnCode();
				if (returnCode != 0) throw new RuntimeException("ReturnCode: " + returnCode);
			}
		}

		@Override
		public void startFileBrowser(File file) {
			Proc proc = new Proc("explorer");
			proc.addParameter(file.getAbsolutePath());
			proc.start();
			// return proc;
		}

		@Override
		public File getApplicationsDir() {
			if (applicationsDir == null) {
				String drive = System.getenv("SystemDrive");
				if (drive == null) drive = "c:";
				File f = new File(drive + "/Programme");
				if (!f.exists()) f = new File(drive + "/Program Files");
				applicationsDir = f;
			}
			return applicationsDir;
		}

	}

	static class Linux extends Env {

		private File myfilesDir;
		private File musicDir;
		private File systemDir;

		@Override
		public long getFileSize(File file) {
			if (!file.exists()) return 0;
			if (file.isFile()) return file.length();
			BufferedReader in = null;
			Process proc = null;
			try {
				proc = Runtime.getRuntime().exec(new String[] { "du", "-s0", file.getAbsolutePath() });
				in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String s = in.readLine().trim();
				int idx = s.indexOf('\t');
				if (idx < 1) throw new RuntimeException("Unexpected output from 'du' command: " + s);
				s = s.substring(0, idx);
				return Long.parseLong(s) * 1000;
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			} finally {
				IO.closeQuiet(in);
				if (proc != null) {
					try {
						proc.destroy();
					} catch (Throwable ex) {}
				}
			}
		}

		@Override
		public List<File> getMountedDirs() {
			File root = new File("/");
			List<File> mountedDirs = new ArrayList<File>();
			mountedDirs.add(root);
			for (File f : IO.listFiles(new File("/media"))) {
				if (f.getTotalSpace() != root.getTotalSpace()) mountedDirs.add(f);
			}
			for (File f : IO.listFiles(new File("/mnt"))) {
				if (f.getTotalSpace() != root.getTotalSpace()) mountedDirs.add(f);
			}
			return mountedDirs;
		}

		@Override
		public File getSystemDir() {
			if (systemDir == null) {
				systemDir = new File("/");
			}
			return systemDir;
		}

		@Override
		public File getMyfilesDir() {
			if (myfilesDir == null) {
				myfilesDir = Sys.getUsersHomeDir();
			}
			return myfilesDir;
		}

		@Override
		public File getMusicDir() {
			if (musicDir == null) {
				musicDir = new File(Sys.getUsersHomePath() + "/music");
			}
			return musicDir;
		}

		@Override
		public File getStartMenuDir() {
			throw new RuntimeException("Not implemented yet.");
		}

		@Override
		public File getSendtoDir() {
			throw new RuntimeException("Not implemented yet.");
		}

		@Override
		public File getAutostartDir() {
			throw new RuntimeException("Not implemented yet.");
		}

		@Override
		public void executeFile(File file, boolean block) {
			throw new RuntimeException("Not implemented yet.");
		}

		@Override
		public void executeVbScript(String script) {
			throw new RuntimeException("Not implemented yet.");
		}

		@Override
		public void createStarter(File file, String targetPath, String arguments, String description, String iconPath,
				String workingDir) {
			throw new RuntimeException("Not implemented yet.");
		}

		@Override
		public void startFileBrowser(File file) {
			try {
				Runtime.getRuntime().exec(new String[] { "nautilus", file.getAbsolutePath() });
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		private File applicationsDir;

		@Override
		public File getApplicationsDir() {
			if (applicationsDir == null) {
				applicationsDir = new File("/opt");
			}
			return applicationsDir;
		}
	}

	public long getFileSize(File file) {
		if (file.isFile()) return file.length();
		if (file.isDirectory()) {
			long size = 0;
			File[] subfiles = file.listFiles();
			if (subfiles != null) {
				for (File f : subfiles) {
					size += getFileSize(f);
				}
			}
			return size;
		}
		return 0;
	}

}
