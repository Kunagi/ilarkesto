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

import ilarkesto.auth.AUser;
import ilarkesto.auth.Auth;
import ilarkesto.fp.Predicate;
import ilarkesto.id.Identifiable;
import ilarkesto.search.Searchable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Persist {

	public static List<String> getIdsAsList(Collection<? extends Identifiable> entities) {
		List<String> ret = new ArrayList<String>(entities.size());
		for (Identifiable entity : entities) {
			ret.add(entity.getId());
		}
		return ret;
	}

	public static List<Map> createPropertiesMaps(Collection<? extends AEntity> entities) {
		List<Map> result = new ArrayList<Map>(entities.size());
		for (AEntity entity : entities) {
			result.add(entity.createPropertiesMap());
		}
		return result;
	}

	public static boolean test(AEntity entity, Predicate<Class> typeFilter, Predicate<AEntity> entityFilter) {
		if (typeFilter != null && !typeFilter.test(entity.getClass())) return false;
		if (entityFilter != null && !entityFilter.test(entity)) return false;
		return true;
	}

	public static boolean matchesKeys(Searchable e, Collection<String> keys) {
		for (String key : keys) {
			if (!e.matchesKey(key)) return false;
		}
		return true;
	}

	public static List<AEntity> getVisible(Collection<AEntity> entities, AUser user) {
		List<AEntity> result = new ArrayList<AEntity>(entities.size());
		for (AEntity entity : entities)
			if (Auth.isVisible(entity, user)) result.add(entity);
		return result;
	}

}
