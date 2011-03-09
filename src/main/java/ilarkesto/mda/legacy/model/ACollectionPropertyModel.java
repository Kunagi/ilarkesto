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

public abstract class ACollectionPropertyModel extends PropertyModel {

	protected abstract Class getCollectionTypeClass();

	protected abstract Class getCollectionImplClass();

	@Override
	public final String getType() {
		return getCollectionType() + "<" + getContentType() + ">";
	}

	@Override
	public String getCollectionType() {
		return getCollectionTypeClass().getName();
	}

	@Override
	public String getCollectionImpl() {
		return getCollectionImplClass().getName();
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public final boolean isCollection() {
		return true;
	}

	@Override
	public String getNameSingular() {
		String name = getName();
		if (!name.endsWith("s")) throw new RuntimeException("property name must end with 's', but does not: " + name);
		return name.substring(0, name.length() - 1);
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isString() {
		return false;
	}

	// --- dependencies ---

	private String contentType;

	public ACollectionPropertyModel(BeanModel entityModel, String name, boolean reference, boolean valueObject,
			String contentType) {
		super(entityModel, name, reference, valueObject);
		this.contentType = contentType;
	}

	public ACollectionPropertyModel(BeanModel entityModel, String name, boolean reference, boolean valueObject,
			Class contentType) {
		this(entityModel, name, reference, valueObject, contentType.getName());
	}

}
