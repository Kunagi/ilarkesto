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
package ilarkesto.mda.legacy.generator;

import ilarkesto.mda.legacy.model.ApplicationModel;
import ilarkesto.ui.swing.ASwingApplication;
import ilarkesto.webapp.AWebApplication;

public class ApplicationGenerator extends ABeanGenerator<ApplicationModel> {

	public ApplicationGenerator(ApplicationModel bean) {
		super(bean);
	}

	@Override
	protected String getSuperclass() {
		switch (bean.getType()) {
			case SWING:
				return ASwingApplication.class.getName();
			case WEB:
				return AWebApplication.class.getName();
		}
		throw new RuntimeException("Unsupported application type: " + bean.getType());
	}

	@Override
	protected String getName() {
		String suffix = "";
		switch (bean.getType()) {
			case SWING:
				suffix = "SwingApplication";
				break;
			case WEB:
				suffix = "WebApplication";
				break;
		}
		return "G" + bean.getName() + suffix;
	}

}
