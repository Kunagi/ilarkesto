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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public class FloatFormField extends AFormField {

    private DecimalFormat format = new DecimalFormat("0.##");
    private String value;
    private int width = 10;
    private String suffix;

    public FloatFormField(String name) {
        super(name);
    }

    public FloatFormField setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public String getSuffix() {
        return suffix;
    }

    public FloatFormField setWidth(int value) {
        this.width = value;
        return this;
    }

    public FloatFormField setValue(Float value) {
        this.value = value == null ? null : format.format(value);
        return this;
    }

    public int getWidth() {
        return width;
    }

    public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
        value = data.get(getName());
        if (value != null) {
            value = value.trim();
        }
        if (value != null && value.length() == 0) {
            value = null;
        }
    }

    public void validate() throws ValidationException {
        if (value == null) {
            if (isRequired()) throw new ValidationException("Eingabe erforderlich");
        } else {
            try {
                format.parse(value);
            } catch (Exception ex) {
                throw new ValidationException("Hier wird eine Zahl erwartet");
            }
        }
    }

    public String getValueAsString() {
        return value;
    }

    public Float getValue() {
        try {
            return value == null ? null : format.parse(value).floatValue();
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

}
