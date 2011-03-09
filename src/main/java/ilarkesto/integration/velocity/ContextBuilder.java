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
package ilarkesto.integration.velocity;

import ilarkesto.base.Str;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;

public class ContextBuilder {

	private Map<String, Object> map = new HashMap<String, Object>();

	public <T> T put(String name, T value) {
		map.put(name, value);
		return value;
	}

	public <T> T add(String listName, T value) {
		List list = (List) map.get(listName);
		if (list == null) {
			list = new ArrayList();
			map.put(listName, list);
		}
		list.add(value);
		return value;
	}

	public ContextBuilder putSubContext(String name) {
		ContextBuilder sub = new ContextBuilder();
		put(name, sub.getMap());
		return sub;
	}

	public ContextBuilder addSubContext(String listName) {
		ContextBuilder sub = new ContextBuilder();
		add(listName, sub.getMap());
		return sub;
	}

	public VelocityContext toVelocityContext() {
		return Velocity.createContext(map);
	}

	public Map<String, Object> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return "ContextBuilder(" + Str.format(map) + ")";
	}

}
