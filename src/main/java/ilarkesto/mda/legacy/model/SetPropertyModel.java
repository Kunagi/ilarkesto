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

import java.util.HashSet;
import java.util.Set;

public class SetPropertyModel extends ACollectionPropertyModel {

	public SetPropertyModel(BeanModel entityModel, String name, boolean reference, Class contentType) {
		super(entityModel, name, reference, false, contentType);
	}

	public SetPropertyModel(BeanModel entityModel, String name, boolean reference, boolean valueObject,
			String contentType) {
		super(entityModel, name, reference, valueObject, contentType);
	}

	@Override
	protected Class getCollectionTypeClass() {
		return Set.class;
	}

	@Override
	protected Class getCollectionImplClass() {
		return HashSet.class;
	}

}
