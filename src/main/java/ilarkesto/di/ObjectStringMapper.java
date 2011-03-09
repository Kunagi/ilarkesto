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
package ilarkesto.di;

import java.util.Map;

/**
 * Enables converting of Strings to Objects and Objects to Strings.
 */
public interface ObjectStringMapper {

	/**
	 * Indicates if an object type is mappable.
	 */
	boolean isTypeSupported(Class type);

	/**
	 * Map string value to object of specified type.
	 */
	<T extends Object> T stringToObject(String value, Class<T> type);

	/**
	 * Map object to string.
	 */
	String objectToString(Object object);

	/**
	 * Map all object values form given map to string values in new map.
	 */
	Map<String, String> objectsToStrings(Map<String, ? extends Object> map);

	// BeanStorage<Object> createBeanStorageProxy(BeanStorage<String> target);

}
