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
package ilarkesto.pdf;

public class TextChunk extends AParagraphElement {

	private StringBuilder text;

	public TextChunk(APdfElement parent) {
		super(parent);
	}

	public TextChunk text(Object s) {
		if (s == null) return this;
		if (text == null) text = new StringBuilder();
		text.append(s);
		return this;
	}

	public String getText() {
		if (text == null) return null;
		return text.toString();
	}

	private FontStyle fontStyle = DEFAULT_FONT_STYLE;

	public TextChunk setFontStyle(FontStyle fontStyle) {
		this.fontStyle = fontStyle == null ? DEFAULT_FONT_STYLE : fontStyle;
		return this;
	}

	public FontStyle getFontStyle() {
		return fontStyle;
	}

	// --- dependencies ---

	public static final FontStyle DEFAULT_FONT_STYLE = new FontStyle();

}
