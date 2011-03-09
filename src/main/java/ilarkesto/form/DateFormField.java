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

import ilarkesto.base.Utl;
import ilarkesto.base.time.Date;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public class DateFormField extends AFormField {

	private String value;

	public DateFormField(String name) {
		super(name);
	}

	public DateFormField setValue(Date value) {
		this.value = value == null ? null : value.toString(Locale.GERMANY);
		return this;
	}

	public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
		String newValue = prepareValue(data.get(getName()));
		if (Utl.equals(value, newValue)) return;
		value = newValue;
		fireFieldValueChanged();
	}

	private static String prepareValue(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.length() == 0) return null;
		try {
			return Date.parseTolerant(s).toString(Locale.GERMANY);
		} catch (ParseException ex) {
			return s;
		}
	}

	public void validate() throws ValidationException {
		if (value == null) {
			if (isRequired()) throw new ValidationException("Eingabe erforderlich");

		} else {
			try {
				Date.parseTolerant(value);
			} catch (ParseException ex) {
				throw new ValidationException("Eingabe muss ein Datum sein. " + ex.getMessage());
			}
		}
	}

	public String getValueAsString() {
		return value;
	}

	public Date getValueAsDate() {
		try {
			return value == null ? null : Date.parseTolerant(value);
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
	}

}
