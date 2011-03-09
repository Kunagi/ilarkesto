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

import java.util.ArrayList;
import java.util.List;

public class Page {

	private Plugin plugin;
	private String label;
	private List<Item> items = new ArrayList<Item>();
	private Object payload;

	public Page(Plugin plugin) {
		this.plugin = plugin;
	}

	public void add(Item item) {
		items.add(item);
	}

	public void remove(Item item) {
		items.remove(item);
	}

	public List<Item> getItems() {
		return items;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return Str.toStringHelper(this, label, items.size());
	}

}
