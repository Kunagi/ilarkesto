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
package ilarkesto.core.scope;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.util.List;

public abstract class Scope {

	static Log log = Log.get(Scope.class);

	/**
	 * Gets an initialized component by name.
	 */
	public abstract Object getComponent(String name);

	public abstract List getAllComponents();

	public abstract <T> T putComponent(String name, T component);

	public String getName() {
		return Str.getSimpleName(getClass());
	}

	@Override
	public String toString() {
		return getName();
	}

	// --- helper ---

	public <T> T putComponent(T component) {
		if (component == null) throw new IllegalArgumentException("component == null");
		return putComponent(ScopeUtil.getComponentName(component.getClass()), component);
	}

	public <T> T getComponent(Class<T> type) {
		String name = ScopeUtil.getComponentName(type);
		return (T) getComponent(name);
	}

	public static Scope get() {
		return ScopeManager.getInstance().getScope();
	}

}