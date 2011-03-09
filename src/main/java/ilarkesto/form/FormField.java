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
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public interface FormField {

    // --- creating ---

    FormField setLabel(String value);

    FormField setHintText(String value);

    FormField setRequired(boolean required);

    FormField addFormFieldChangeListener(FormFieldChangeListener listener);

    // --- rendering ---

    String getName();

    String getLabel();

    String getHintText();

    boolean isRequired();

    String getErrorMessage();

    // --- rendering + submitting ---

    String getValueAsString();

    // --- submitting ---

    Form getForm();

    void setErrorMessage(String value);

    void update(Map<String, String> data, Collection<FileItem> uploadedFiles);

    void validate() throws ValidationException;

}
