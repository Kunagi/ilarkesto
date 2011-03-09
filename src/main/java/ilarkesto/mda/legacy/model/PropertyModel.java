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

import ilarkesto.base.Str;

public abstract class PropertyModel {

	public abstract String getNameSingular();

	public abstract String getType();

	public abstract String getContentType();

	public abstract String getCollectionType();

	public abstract String getCollectionImpl();

	public abstract boolean isCollection();

	public abstract boolean isPrimitive();

	public abstract boolean isBoolean();

	public abstract boolean isString();

	private boolean mandatory;

	private String editablePredicate;

	private String tooltip;

	private boolean fireModified = true;

	private boolean virtual;

	public PropertyModel setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public boolean isVirtual() {
		return virtual;
	}

	public PropertyModel setVirtual(boolean virtual) {
		this.virtual = virtual;
		return this;
	}

	public boolean isFireModified() {
		return fireModified;
	}

	public PropertyModel setFireModified(boolean fireModified) {
		this.fireModified = fireModified;
		return this;
	}

	public String getTooltip() {
		return tooltip;
	}

	public PropertyModel setEditablePredicate(String editablePredicate) {
		this.editablePredicate = editablePredicate;
		return this;
	}

	public String getEditablePredicate() {
		return editablePredicate;
	}

	public boolean isOptionRestricted() {
		return false;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public PropertyModel setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
		return this;
	}

	public final String getName() {
		return name;
	}

	private boolean reference;

	public boolean isReference() {
		return reference;
	}

	private boolean valueObject;

	public boolean isValueObject() {
		return valueObject;
	}

	public String getDaoName() {
		if (!isReference()) throw new UnsupportedOperationException("not a reference: " + getContentType());

		return Str.lowercaseFirstLetter(getContentTypeName()) + "Dao";
	}

	public String getContentTypeName() {
		String type = getContentType();
		int idx = type.lastIndexOf('.');
		return type.substring(idx + 1);
	}

	public final boolean isSearchable() {
		return searchable;
	}

	public boolean isAbstract() {
		return _abstract;
	}

	public BeanModel getBean() {
		return beanModel;
	}

	public EntityModel getEntity() {
		return (EntityModel) getBean();
	}

	// --- dependencies ---

	private BeanModel beanModel;

	private String name;

	public PropertyModel(BeanModel beanModel, String name, boolean reference, boolean valueObject) {
		this.beanModel = beanModel;
		this.name = name;
		this.reference = reference;
		this.valueObject = valueObject;
	}

	private boolean _abstract;

	public void setAbstract(boolean _abstract) {
		this._abstract = _abstract;
	}

	private boolean searchable;

	public PropertyModel setSearchable(boolean searchable) {
		this.searchable = searchable;
		return this;
	}

	private boolean unique;

	public final boolean isUnique() {
		return unique;
	}

	public final PropertyModel setUnique(boolean unique) {
		this.unique = unique;
		return this;
	}

}
