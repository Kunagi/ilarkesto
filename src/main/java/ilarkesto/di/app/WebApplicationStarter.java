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

import ilarkesto.core.logging.Log;
import ilarkesto.di.BeanContainer;
import ilarkesto.webapp.AWebApplication;

public class WebApplicationStarter extends ApplicationStarter {

	private static final Log LOG = Log.get(WebApplicationStarter.class);

	public static AWebApplication startWebApplication(String applicationClassName, String applicationName) {
		AWebApplication result;
		BeanContainer beanProvider = new BeanContainer();
		if (applicationName != null) {
			beanProvider.put("applicationName", applicationName);
		}
		try {
			result = startApplication((Class<? extends AWebApplication>) Class.forName(applicationClassName),
				beanProvider);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		LOG.debug("Triggering Garbage Collection");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}

		return result;
	}

}
