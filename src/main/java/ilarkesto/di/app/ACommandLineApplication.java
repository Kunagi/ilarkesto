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

import ilarkesto.concurrent.TaskManager;
import ilarkesto.core.logging.Log;

public abstract class ACommandLineApplication extends AApplication {

	private static final Log LOG = Log.get(ACommandLineApplication.class);

	protected abstract int execute(String[] args);

	@Override
	protected final void onStart() {
		int rc;
		try {
			rc = execute(getArguments());
		} catch (Throwable ex) {
			LOG.fatal(ex);
			rc = 1;
		}
		Log.flush();
		System.exit(rc);
	}

	@Override
	public void ensureIntegrity() {}

	@Override
	protected final void onShutdown() {}

	@Override
	protected final void scheduleTasks(TaskManager tm) {}

}
