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
package ilarkesto.core.navig;

import ilarkesto.core.base.Str;

import java.util.LinkedHashMap;
import java.util.Map;

public class Item {

	private Plugin plugin;
	private Object payload;
	private String label;
	private Map<String, String> properties = new LinkedHashMap<String, String>();

	public Item(Plugin plugin, String label) {
		super();
		this.plugin = plugin;
		this.label = label;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public void setProperty(String label, String value) {
		properties.put(label, value);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public Object getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return Str.toStringHelper(this, plugin, label);
	}

}
