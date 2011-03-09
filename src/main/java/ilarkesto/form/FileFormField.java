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

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public class FileFormField extends AFormField {

    private File value;
    private boolean folder;

    public FileFormField(String name) {
        super(name);
    }

    public void setValue(File value) {
        this.value = value;
    }

    public File getValue() {
        return value;
    }

    public FileFormField setFolder(boolean folder) {
        this.folder = folder;
        return this;
    }

    public boolean isFolder() {
        return folder;
    }

    public String getValueAsString() {
        return value == null ? null : value.getPath();
    }

    public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
        String path = data.get(getName());
        value = path == null ? null : new File(path);
    }

    public void validate() throws ValidationException {
        if (value == null) {
            if (isRequired()) throw new ValidationException("Eingabe erforderlich");
        }
    }

}
