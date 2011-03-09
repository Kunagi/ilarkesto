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

import ilarkesto.base.Str;
import ilarkesto.base.Sys;
import ilarkesto.base.Tm;
import ilarkesto.base.Utl;
import ilarkesto.base.time.Time;
import ilarkesto.concurrent.ATask;
import ilarkesto.concurrent.TaskManager;
import ilarkesto.core.logging.Log;
import ilarkesto.di.Context;
import ilarkesto.integration.xstream.XStreamSerializer;
import ilarkesto.io.ExclusiveFileLock;
import ilarkesto.io.ExclusiveFileLock.FileLockedException;
import ilarkesto.io.IO;
import ilarkesto.logging.DefaultLogDataHandler;
import ilarkesto.persistence.DaoListener;
import ilarkesto.persistence.DaoService;
import ilarkesto.persistence.EntityStore;
import ilarkesto.persistence.FileEntityStore;
import ilarkesto.persistence.Serializer;
import ilarkesto.persistence.TransactionService;
import ilarkesto.properties.FilePropertiesStore;

import java.io.File;
import java.util.Properties;
import java.util.Set;

/**
 * Base class of a custom application
 * 
 * @author Witoslaw Koczewski
 */
public abstract class AApplication {

	private static Log log = Log.get(AApplication.class);

	private ExclusiveFileLock exclusiveFileLock;

	protected abstract void onStart();

	protected abstract void onShutdown();

	protected abstract void scheduleTasks(TaskManager tm);

	protected Context context;
	private String[] arguments = new String[0];

	public void ensureIntegrity() {
		log.info("Ensuring application integrity");
		getDaoService().ensureIntegrity();
		getTransactionService().commit();
	}

	protected boolean isSingleton() {
		return true;
	}

	public final void start() {
		if (instance != null) { throw new RuntimeException("An Application already started: " + instance); }
		instance = this;

		log.info("\n\n     DATA PATH:", getApplicationDataDir(), "\n\n");

		context = Context.createRootContext("app:" + getApplicationName());
		context.addBeanProvider(this);

		if (isSingleton()) {
			File lockFile = new File(getApplicationDataDir() + "/.lock");
			for (int i = 0; i < 10; i++) {
				try {
					exclusiveFileLock = new ExclusiveFileLock(lockFile);
					break;
				} catch (FileLockedException ex) {
					log.info("Application already running. Lock file locked: " + lockFile.getAbsolutePath());
				}
				Utl.sleep(1000);
			}
			if (exclusiveFileLock == null) {
				log.fatal("Application startup failed. Another instance is running. Lock file: "
						+ lockFile.getAbsolutePath());
				shutdown();
				return;
			}
		}

		try {
			ensureIntegrity();
			onStart();
		} catch (Throwable ex) {
			APPLICATION_LOCK = null;
			throw new RuntimeException("Application startup failed.", ex);
		}

		scheduleTasks(getTaskManager());
	}

	public final void shutdown() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (getApplicationLock()) {
					if (instance == null) throw new RuntimeException("Application not started yet.");
					log.info("Shutdown initiated:", getApplicationName());

					getTaskManager().shutdown(10000);
					Set<ATask> tasks = getTaskManager().getRunningTasks();
					if (!tasks.isEmpty()) {
						log.warn("Aborting tasks on shutdown failed:", tasks);
					}

					if (context != null) context.destroy();

					if (exclusiveFileLock != null) exclusiveFileLock.release();
					Log.flush();
					onShutdown();
					DefaultLogDataHandler.stopLogging();
				}
			}

		});
		thread.setName(getApplicationName() + " shutdown");
		thread.start();
	}

	public final <T> T autowire(T bean) {
		return context.autowire(bean);
	}

	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

	public String[] getArguments() {
		return arguments;
	}

	private static AApplication instance;

	public static AApplication get() {
		if (instance == null) throw new RuntimeException("No application started yet");
		return instance;
	}

	public static boolean isStarted() {
		return instance != null;
	}

	private static String APPLICATION_LOCK = "APPLICATION_LOCK";

	public Object getApplicationLock() {
		return APPLICATION_LOCK;
	}

	public final String getApplicationPackageName() {
		return getClass().getPackage().getName();
	}

	public String getApplicationLabel() {
		return getApplicationName();
	}

	public AApplication getApplication() {
		return this;
	}

	private String applicationName;

	public String getApplicationName() {
		if (applicationName == null) {
			applicationName = getClass().getSimpleName();
			applicationName = Str.lowercaseFirstLetter(applicationName);
			applicationName = Str.removeSuffix(applicationName, "Application");
		}
		return applicationName;
	}

	private String applicationDataDir;

	public String getApplicationDataDir() {
		if (applicationDataDir == null) applicationDataDir = Sys.getUsersHomePath() + "/." + getApplicationName();
		return applicationDataDir;
	}

	private String applicationTempDir;

	public String getApplicationTempDir() {
		if (applicationTempDir == null) {
			applicationTempDir = getApplicationDataDir() + "/tmp";
		}
		return applicationTempDir;
	}

	private FilePropertiesStore applicationConfig;

	public FilePropertiesStore getApplicationConfig() {
		if (applicationConfig == null) {
			applicationConfig = new FilePropertiesStore(getApplicationDataDir() + "/config.properties", false);
		}
		return applicationConfig;
	}

	private String releaseLabel;

	public String getReleaseLabel() {
		if (releaseLabel == null) {
			releaseLabel = getBuildProperties().getProperty("release.label");
			if (releaseLabel == null || releaseLabel.equals("@release-label@"))
				releaseLabel = "dev[" + getBuild() + "]";
		}
		return releaseLabel;
	}

	public String getBuild() {
		String date = getBuildProperties().getProperty("date");
		if (date == null || "@build-date@".equals(date)) date = Time.now().toString();
		return date;
	}

	protected Properties buildProperties;

	public final Properties getBuildProperties() {
		if (buildProperties == null) {
			try {
				buildProperties = IO.loadProperties(IO.getResource(getClass(), "build.properties"), IO.UTF_8);
			} catch (Throwable t) {
				log.error(t);
				buildProperties = new Properties();
			}
		}
		return buildProperties;
	}

	public void deleteOldBackupFiles(String backupDir) {
		log.info("Deleting old backup files from:", backupDir);
		final long deadline = System.currentTimeMillis() - Tm.DAY * 3;
		IO.FileProcessor processor = new IO.FileProcessor() {

			@Override
			public boolean isAbortRequested() {
				return false;
			}

			@Override
			public void onFile(File file) {
				if (file.lastModified() < deadline) IO.delete(file);
			}

			@Override
			public boolean onFolderBegin(File folder) {
				return true;
			}

			@Override
			public void onFolderEnd(File folder) {
				folder.delete();
			}

		};
		IO.process(backupDir, processor);
	}

	public boolean isDevelopmentMode() {
		return Sys.isDevelopmentMode();
	}

	public final boolean isProductionMode() {
		return !isDevelopmentMode();
	}

	@Override
	public final String toString() {
		return getApplicationName();
	}

	// --- beans / services ---

	private TaskManager taskManager;

	public TaskManager getTaskManager() {
		if (taskManager == null) taskManager = Context.get().autowire(new TaskManager());
		return taskManager;
	}

	private FileEntityStore entityStore;

	public final EntityStore getEntityStore() {
		if (entityStore == null) {
			entityStore = new FileEntityStore();
			entityStore.setDir(getApplicationDataDir() + "/entities");
			File backupDir = new File(getApplicationDataDir() + "/entities-rescue");

			File backupDirOld = new File(getApplicationDataDir() + "/backup/entities");
			if (backupDirOld.exists()) {
				backupDirOld.renameTo(backupDir);
				backupDirOld.delete();
				backupDirOld.getParentFile().delete();
			}

			entityStore.setBackupDir(backupDir.getPath());
			entityStore.setVersion(getDataVersion());
			Context.get().autowire(entityStore);
		}
		return entityStore;
	}

	protected int getDataVersion() {
		return -1;
	}

	private XStreamSerializer beanSerializer;

	public final Serializer getBeanSerializer() {
		if (beanSerializer == null) {
			beanSerializer = new XStreamSerializer();
			Context.get().autowire(beanSerializer);
		}
		return beanSerializer;
	}

	private DaoService daoService;

	public final DaoService getDaoService() {
		if (daoService == null) {
			daoService = new DaoService();
			Context.get().autowire(daoService);
			daoService.initialize(context);
			for (DaoListener listener : Context.get().getBeansByType(DaoListener.class))
				daoService.addListener(listener);
		}
		return daoService;
	}

	private TransactionService transactionService;

	public final TransactionService getTransactionService() {
		if (transactionService == null) {
			transactionService = new TransactionService();
			Context.get().autowire(transactionService);
		}
		return transactionService;
	}

}
