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

import java.util.ArrayList;
import java.util.List;

public class ApplicationModel extends BeanModel {

	public enum Type {
		WEB, SWING
	}

	private Type type;
	private List<GwtServiceModel> gwtServices = new ArrayList<GwtServiceModel>();
	private List<ActionModel> actions = new ArrayList<ActionModel>();

	public ApplicationModel(Type type, String name, String packageName) {
		super(name, packageName);
		this.type = type;
	}

	public ActionModel addAction(String name, String packageName) {
		ActionModel action = new ActionModel(name, packageName);
		actions.add(action);
		return action;
	}

	public ActionModel addCreateAction(EntityModel entity) {
		return addAction("Create" + entity.getName(), entity.getPackageName());
	}

	public List<ActionModel> getActions() {
		return actions;
	}

	public Type getType() {
		return type;
	}

	public void addGwtService(GwtServiceModel serviceModel) {
		gwtServices.add(serviceModel);
	}

	public List<GwtServiceModel> getGwtServices() {
		return gwtServices;
	}

}
