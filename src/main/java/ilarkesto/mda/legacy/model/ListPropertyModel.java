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

public final class ListPropertyModel extends ACollectionPropertyModel {

	public ListPropertyModel(BeanModel entityModel, String name, boolean reference, Class contentType) {
		super(entityModel, name, reference, false, contentType);
	}

	public ListPropertyModel(BeanModel entityModel, String name, boolean reference, boolean valueObject,
			String contentType) {
		super(entityModel, name, reference, valueObject, contentType);
	}

	@Override
	protected Class getCollectionTypeClass() {
		return List.class;
	}

	@Override
	protected Class getCollectionImplClass() {
		return ArrayList.class;
	}

}
