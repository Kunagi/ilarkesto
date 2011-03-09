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

public class FormButton  {

    private String name;
    private String label;
    private boolean updateFields = true;
    private boolean validateForm = true;
    private Character accessKey;
    private String icon;

    public FormButton(String name) {
        this.name = name;
        this.label = name;
    }

    public FormButton setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public FormButton setAccessKey(Character accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public Character getAccessKey() {
        return accessKey;
    }

    public FormButton setLabel(String value) {
        this.label = value;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public boolean isUpdateFields() {
        return updateFields;
    }

    public FormButton setUpdateFields(boolean updateFields) {
        this.updateFields = updateFields;
        return this;
    }

    public boolean isValidateForm() {
        return validateForm;
    }

    public FormButton setValidateForm(boolean validateForm) {
        this.validateForm = validateForm;
        return this;
    }

    public boolean isAbort() {
        return Form.ABORT_BUTTON_NAME.equals(name);
    }

    @Override
    public String toString() {
        return name;
    }

}
