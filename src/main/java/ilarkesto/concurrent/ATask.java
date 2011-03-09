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
package ilarkesto.concurrent;

import ilarkesto.core.logging.Log;

public abstract class ATask {

	private static final Log LOG = Log.get(ATask.class);

	private volatile boolean finished;
	private long finishTime = -1;
	private volatile boolean started;
	private long startTime = -1;
	private volatile boolean abortRequested;
	private Thread thread;

	// --- dependencies ---

	private long maxSleepAtOnce = 1000;

	public final void setMaxSleepAtOnce(long maxSleepAtOnce) {
		this.maxSleepAtOnce = maxSleepAtOnce;
	}

	protected abstract void perform() throws InterruptedException;

	public float getProgress() {
		return isFinished() ? 1 : 0;
	}

	public String getProgressMessage() {
		return null;
	}

	// --- ---

	public final boolean isFinished() {
		return finished;
	}

	public final long getFinishTime() {
		return finishTime;
	}

	public final boolean isStarted() {
		return started;
	}

	public final long getStartTime() {
		return startTime;
	}

	public final boolean isAbortRequested() {
		return abortRequested;
	}

	public void abort() {
		this.abortRequested = true;
	}

	public final boolean isRunning() {
		return started && !finished;
	}

	public final long getRunTime() {
		if (startTime < 0) return -1;
		long endTime = finishTime < 0 ? System.currentTimeMillis() : finishTime;
		return endTime - startTime;
	}

	public void reset() {
		if (isRunning()) {
			abort();
			try {
				waitForFinish();
			} catch (InterruptedException ex) {
				// nop
			}
		}
		started = false;
		finished = false;
		abortRequested = false;
	}

	public final void run() {
		this.thread = Thread.currentThread();
		if (started) throw new RuntimeException("Task already started: " + this);
		started = true;
		startTime = System.currentTimeMillis();
		try {
			perform();
		} catch (InterruptedException ex) {
			// all right
		} catch (Throwable ex) {
			LOG.error("Task execution failed:", this, ex);
			throw new RuntimeException(ex);
		} finally {
			finished = true;
			finishTime = System.currentTimeMillis();
			synchronized (this) {
				this.notifyAll();
			}
			thread = null;
		}
	}

	public final void waitForFinish() throws InterruptedException {
		while (!isFinished()) {
			synchronized (this) {
				this.wait(1000);
			}
		}
	}

	public final void sleep(long millis) throws InterruptedException {
		while (!abortRequested && millis > 0) {
			long sleep = millis > maxSleepAtOnce ? maxSleepAtOnce : millis;
			Thread.sleep(sleep);
			millis -= sleep;
		}
	}

	public final Thread getThread() {
		return thread;
	}

	public final String getThreadName() {
		if (thread == null) return null;
		return thread.getName();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
