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
package ilarkesto.tools.getphotos;

import ilarkesto.base.Env;
import ilarkesto.concurrent.ATask;
import ilarkesto.core.logging.Log;

import java.io.File;

public class FindCameraTask extends ATask {

	private static final Log LOG = Log.get(FindCameraTask.class);

	@Override
	protected void perform() throws InterruptedException {
		while (!isAbortRequested()) {
			for (File root : Env.get().getMountedDirs()) {
				File dcimDir = getDcimDir(root);
				if (dcimDir != null) {
					LOG.info("DCIM dir found:", dcimDir);
					GetphotosSwingApplication.get().startCopying(dcimDir);
					return;
				}
				if (isAbortRequested()) break;
			}
			sleep(1000);
		}
	}

	private File getDcimDir(File dir) {
		LOG.debug("Checking", dir);
		File dcimDir = new File(dir.getPath() + "/DCIM");
		return dcimDir.exists() ? dcimDir : null;
	}

}
