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

import ilarkesto.base.MissingDependencyException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public final class RadioSelectFormField<T> extends AFormField {

	private List<T> options;
	private T value;
	private boolean required = true;
	private boolean vertical;

	public RadioSelectFormField(String name) {
		super(name);
	}

	public RadioSelectFormField<T> setOptions(Collection<T> options) {
		this.options = new ArrayList<T>(options);
		return this;
	}

	public RadioSelectFormField<T> setValue(T value) {
		this.value = value;
		return this;
	}

	public RadioSelectFormField<T> setVertical(boolean vertical) {
		this.vertical = vertical;
		return this;
	}

	public boolean isVertical() {
		return vertical;
	}

	public List<T> getOptions() {
		if (options == null) throw new MissingDependencyException("options");
		return options;
	}

	public T getValue() {
		return value;
	}

	public String getValueAsString() {
		return value == null ? null : value.toString();
	}

	public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
		String indexAsString = data.get(getName());
		if (indexAsString == null) {
			value = null;
		} else {
			value = options.get(Integer.parseInt(indexAsString));
		}
	}

	public void validate() throws ValidationException {
		if (value == null && required) throw new ValidationException("Eingabe erforderlich");
	}

}
