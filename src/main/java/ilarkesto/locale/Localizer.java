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
package ilarkesto.locale;

import ilarkesto.base.Str;
import ilarkesto.persistence.AEntity;

import java.util.Locale;

/**
 * Provides localized strings for a specified locale.
 */
public abstract class Localizer {

	public abstract String string(String key, Object... parameters);

	public abstract Locale getLocale();

	public final String string(AEntity scope, String key, Object... parameters) {
		return string("entity." + scope.getDao().getEntityName() + "." + key, parameters);
	}

	public final String string(Class scope, String key, Object... parameters) {
		if (scope == null) return string(key, parameters);
		if (AEntity.class.isAssignableFrom(scope))
			return string("entity." + Str.lowercaseFirstLetter(scope.getSimpleName()) + "." + key, parameters);
		return string(key, parameters);
	}

}
