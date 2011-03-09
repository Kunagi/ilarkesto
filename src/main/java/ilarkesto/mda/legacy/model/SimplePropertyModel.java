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

public class SimplePropertyModel extends PropertyModel {

	private String type;
	private boolean optionRestricted;

	public SimplePropertyModel(BeanModel entityModel, String name, boolean reference, boolean valueObject, String type) {
		super(entityModel, name, reference, valueObject);
		this.type = type;
	}

	public SimplePropertyModel setOptionRestricted(boolean optioned) {
		this.optionRestricted = optioned;
		return this;
	}

	@Override
	public boolean isOptionRestricted() {
		return optionRestricted;
	}

	public EntityModel getReferencedEntity() {
		return null;
	}

	@Override
	public final String getType() {
		return type;
	}

	@Override
	public String getContentType() {
		return getType();
	}

	@Override
	public String getCollectionType() {
		throw new UnsupportedOperationException("not a collection property");
	}

	@Override
	public String getCollectionImpl() {
		throw new UnsupportedOperationException("not a collection property");
	}

	@Override
	public final boolean isCollection() {
		return false;
	}

	@Override
	public String getNameSingular() {
		return getName();
	}

	@Override
	public boolean isBoolean() {
		return "boolean".equals(getType()) || Boolean.class.getName().equals(getType());
	}

	@Override
	public boolean isString() {
		return String.class.getName().equals(getType());
	}

	@Override
	public boolean isPrimitive() {
		if (isBoolean()) return true;
		if (int.class.getName().equals(type)) return true;
		if (long.class.getName().equals(type)) return true;
		return false;
	}

}
