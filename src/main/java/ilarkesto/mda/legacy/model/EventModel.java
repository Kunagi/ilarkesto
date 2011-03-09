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
package ilarkesto.mda.legacy.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class EventModel extends AModel {

	private Set<ParameterModel> parameters = new LinkedHashSet<ParameterModel>();
	private boolean quiet;

	public EventModel(String name) {
		super(name);
		if (!Character.isUpperCase(name.charAt(0)))
			throw new RuntimeException("First letter of event name needs to be uppercase: " + name);
	}

	public EventModel addParameter(String name, String type) {
		ParameterModel parameter = new ParameterModel(name, type);
		parameters.add(parameter);
		return this;
	}

	public EventModel addParameter(String name, Class type) {
		return addParameter(name, type.getName());
	}

	public Set<ParameterModel> getParameters() {
		return parameters;
	}

	public boolean isQuiet() {
		return quiet;
	}

	public EventModel setQuiet(boolean quiet) {
		this.quiet = quiet;
		return this;
	}

}
