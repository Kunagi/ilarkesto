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
import ilarkesto.richtext.RichTextFormatter;

public class TextareaFormField extends TextFormField {

	public TextareaFormField(String name) {
		super(name);
	}

	@Override
	protected String preProcessValue(String s) {
		if (s == null) return null;
		if (!html) return super.preProcessValue(s);
		if (s.startsWith("<html")) {
			String plain = Str.removeHtmlTags(s).trim();
			if (plain.length() == 0 && !s.toLowerCase().contains("<img")) s = plain;
			if (s.length() > 0) s = "<html>" + cleanupEditorsHtml(s);
		}
		return super.preProcessValue(s);
	}

	private String cleanupEditorsHtml(String s) {
		if (s == null) return null;
		if (s.length() == 0) return s;
		s = Str.cutHtmlAndHeaderAndBody(s);
		s = s.replace("<title></title>", "");
		return s;
	}

	@Override
	public TextFormField setValue(String value) {
		if (!html) return super.setValue(value);
		if (value == null || value.startsWith("<html")) return super.setValue(value);
		return super.setValue(RichTextFormatter.toHtml(value));
	}

	private int lines = 15;

	public TextareaFormField setLines(int value) {
		this.lines = value;
		return this;
	}

	public int getLines() {
		return lines;
	}

	private boolean html = true;

	public boolean isHtml() {
		return html;
	}

	public TextareaFormField setHtml(boolean html) {
		this.html = html;
		return this;
	}

	public TextareaFormField setForceHtml(boolean forceHtml) {
		if (forceHtml) setHtml(true);
		return this;
	}

	public boolean isForceHtml() {
		return true; // TODO forceHtml;
	}

}
