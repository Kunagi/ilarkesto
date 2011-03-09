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


import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;

public class ItemFormField<T> extends AFormField {

    private T value;

    private ClearButton clearButton;

    private SelectButton selectButton;

    private Collection<T> selectableItems;

    private boolean autoTrigger;

    public ItemFormField(String name) {
        super(name);

        clearButton = (ClearButton) new ClearButton(Form.BUTTON_PREFIX + Form.CLEAR_ITEM_BUTTON_NAME_PREFIX + name)
                .setValidateForm(false).setLabel("Entfernen").setIcon("delete");

        selectButton = (SelectButton) new SelectButton(Form.BUTTON_PREFIX + Form.SELECT_ITEM_BUTTON_NAME_PREFIX + name)
                .setValidateForm(false).setLabel("Auswahl...").setIcon("edit");
    }

    public ItemFormField setAutoTrigger(boolean autoTrigger) {
        this.autoTrigger = autoTrigger;
        return this;
    }

    public boolean isAutoTrigger() {
        return autoTrigger && value == null;
    }

    public ItemFormField setSelectableItems(Collection<T> items) {
        this.selectableItems = items;
        return this;
    }

    public Collection getSelectableItems() {
        Set<T> result = new HashSet<T>(selectableItems);
        if (value != null) result.remove(value);
        return result;
    }

    public ClearButton getClearButton() {
        if (isRequired() || value == null) return null;
        return clearButton;
    }

    public SelectButton getSelectButton() {
        return selectButton;
    }

    public ItemFormField setValue(T value) {
        this.value = value;
        fireFieldValueChanged();
        return this;
    }

    public T getValue() {
        return value;
    }

    public String getValueAsString() {
        return value == null ? null : value.toString();
    }

    public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {}

    public void validate() throws ValidationException {
        if (isRequired() && value == null) throw new ValidationException("Hier ist eine Auswahl erforderlich.");
    }

    public class SelectButton extends FormButton {

        public SelectButton(String name) {
            super(name);
        }

        public ItemFormField getField() {
            return ItemFormField.this;
        }

    }

    public class ClearButton extends FormButton {

        public ClearButton(String name) {
            super(name);
        }

        public ItemFormField getField() {
            return ItemFormField.this;
        }

    }

}
