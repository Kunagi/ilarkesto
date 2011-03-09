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

import ilarkesto.richtext.RichTextFormatter;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

public class OutputFormField extends AFormField {

	private String	text;

	public OutputFormField(String name) {
		super(name);
	}

	public OutputFormField setText(String text) {
		this.text = text;
		return this;
	}

	public String getHtml() {
		if (text == null) return "";
		String html = RichTextFormatter.toHtml(text);
		return html;
	}

	public String getValueAsString() {
		return text;
	}

	public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
	// nop
	}

	public void validate() {}

}
