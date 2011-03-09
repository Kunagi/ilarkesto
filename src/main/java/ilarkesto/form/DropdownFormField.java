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
package ilarkesto.form;

import ilarkesto.base.Str;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.fileupload.FileItem;

public class DropdownFormField<E> extends AFormField {

	private E value;
	private SortedMap<String, E> selectableItems;
	private boolean localizeItems;

	public DropdownFormField(String name) {
		super(name);
	}

	public DropdownFormField<E> setLocalizeItems(boolean localizeItems) {
		this.localizeItems = localizeItems;
		return this;
	}

	public boolean isLocalizeItems() {
		return localizeItems;
	}

	public DropdownFormField<E> setSelectableItems(Collection<E> items) {
		List<E> itemList = (List<E>) (items instanceof List ? items : new ArrayList<E>(items));
		if (!itemList.isEmpty() && itemList.get(0) instanceof Comparable) Collections.sort((List) itemList);
		this.selectableItems = new TreeMap<String, E>();
		int i = 0;
		for (E item : itemList) {
			String key = Str.fillUpLeft(String.valueOf(i++), "0", 4);
			selectableItems.put(key, item);
		}
		return this;
	}

	public Map<String, E> getSelectableItems() {
		return selectableItems;
	}

	public DropdownFormField<E> setValue(E value) {
		this.value = value;
		return this;
	}

	public E getValue() {
		return value;
	}

	public String getValueAsString() {
		return value == null ? null : value.toString();
	}

	public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
		value = selectableItems.get(data.get(getName()));
	}

	public void validate() throws ValidationException {
		if (value == null && isRequired()) throw new ValidationException("Eingabe erforderlich.");
	}

}
