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

import java.util.ArrayList;
import java.util.List;

public final class BatchTask extends ATask {

	private List<TaskWrapper> tasks = new ArrayList<TaskWrapper>();
	private TaskWrapper currentTask;
	private float progressed;

	@Override
	protected void perform() {
		int totalWeight = 0;
		for (TaskWrapper wrapper : tasks) {
			totalWeight += wrapper.weight;
		}
		for (TaskWrapper wrapper : tasks) {
			wrapper.effectiveWeight = (float) wrapper.weight / (float) totalWeight;
		}

		while (!tasks.isEmpty() && !isAbortRequested()) {
			currentTask = tasks.get(0);
			tasks.remove(currentTask);
			currentTask.task.run();
			progressed += currentTask.effectiveWeight;
		}
	}

	@Override
	public void abort() {
		if (currentTask != null) currentTask.task.abort();
		super.abort();
	}

	@Override
	public void reset() {
		for (TaskWrapper taskWrapper : tasks) {
			if (taskWrapper.task.isFinished()) continue;
			taskWrapper.task.abort();
		}
		super.reset();
	}

	@Override
	public String getProgressMessage() {
		return currentTask == null ? null : currentTask.task.getProgressMessage();
	}

	@Override
	public float getProgress() {
		if (currentTask == null) return super.getProgress();
		return progressed + (currentTask.effectiveWeight * currentTask.task.getProgress());
	}

	public void addTask(ATask task) {
		addTask(task, 1);
	}

	public void addTask(ATask task, int weight) {
		tasks.add(new TaskWrapper(task, weight));
	}

	private static class TaskWrapper {

		private ATask task;
		private int weight;
		private float effectiveWeight;

		public TaskWrapper(ATask task, int weight) {
			this.task = task;
			this.weight = weight;
		}

	}

}
