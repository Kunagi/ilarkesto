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
package ilarkesto.persistence;

public abstract class AStructure extends ADatob {

	private transient ADatob.StructureManager manager;

	public AStructure(AStructure template) {}

	final void setManager(ADatob.StructureManager manager) {
		this.manager = manager;
	}

	@Override
	protected final ADatob.StructureManager getManager() {
		return manager;
	}

	public final AStructure clone(ADatob.StructureManager manager) {
		AStructure result;
		try {
			result = getClass().getConstructor(new Class[] { getClass() }).newInstance(new Object[] { this });
		} catch (NoSuchMethodException ex) {
			throw new RuntimeException("Missing copy constructor in " + getClass(), ex);
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
		result.manager = manager;
		return result;
	}

}
