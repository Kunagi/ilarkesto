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

import ilarkesto.gwt.client.AGwtApplication;
import ilarkesto.mda.legacy.model.ApplicationModel;

public class GwtApplicationGenerator extends AClassGenerator {

	private ApplicationModel application;

	public GwtApplicationGenerator(ApplicationModel application) {
		super();
		this.application = application;
	}

	@Override
	protected String getName() {
		return "G" + application.getName() + "GwtApplication";
	}

	@Override
	protected boolean isInterface() {
		return false;
	}

	@Override
	protected boolean isOverwrite() {
		return true;
	}

	@Override
	protected String getSuperclass() {
		return AGwtApplication.class.getName();
	}

	@Override
	protected String getPackage() {
		return application.getPackageName().replace(".server", ".client");
	}

	@Override
	protected void writeContent() {}
}
