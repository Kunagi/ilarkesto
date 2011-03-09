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
package ilarkesto.base;

import ilarkesto.core.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple cache, where keys are mapped to value objects. When no value-object is assigned to a key, then a
 * create method is called on the user provided factory.
 */
public class Cache<K, V> {

	private static final Log LOG = Log.get(Cache.class);

	private Map<K, V> cache = new HashMap<K, V>();

	private Factory<K, V> factory;

	public Cache(Factory<K, V> factory) {
		this.factory = factory;
	}

	Cache() {}

	void setFactory(Factory<K, V> factory) {
		this.factory = factory;
	}

	public void clear() {
		cache = new HashMap<K, V>();
	}

	public V get(K key) {
		V value = cache.get(key);
		if (value == null) {
			value = factory.create(key);
			// LOG.debug("Returning new:", key, "->", value);
			cache.put(key, value);
			// } else {
			// LOG.debug("Returning cached:", key, "->", value);
		}
		return value;
	}

	public static interface Factory<K, V> {

		V create(K key);

	}

}
