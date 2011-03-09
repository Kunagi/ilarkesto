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

public class ReferencePropertyModel extends SimplePropertyModel {

	private EntityModel referencedEntity;
	private boolean master;
	private BackReferenceModel backReference;

	public ReferencePropertyModel(BeanModel entityModel, String name, EntityModel referencedEntity) {
		super(entityModel, name, true, false, referencedEntity.getPackageName() + "." + referencedEntity.getName());
		this.referencedEntity = referencedEntity;
	}

	@Override
	public EntityModel getReferencedEntity() {
		return referencedEntity;
	}

	public ReferencePropertyModel createBackReference(String name) {
		if (!getBean().isEntity()) return this;
		backReference = new BackReferenceModel(name, this);
		referencedEntity.addBackReference(backReference);
		return this;
	}

	public ReferencePropertyModel setBackReferenceName(String name) {
		backReference.setName(name);
		return this;
	}

	public ReferencePropertyModel setMaster(boolean master) {
		if (master && (!isReference() || isCollection()))
			throw new RuntimeException("Only a simple reference property can be a master");
		this.master = master;
		setMandatory(master);
		return this;
	}

	public boolean isMaster() {
		return master;
	}

}
