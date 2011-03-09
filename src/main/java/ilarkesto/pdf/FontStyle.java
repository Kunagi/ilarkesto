/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.pdf;

import java.awt.Color;

public final class FontStyle {

	public static final String FONT_UBUNTU = "fonts/Ubuntu-R.ttf";
	public static final String FONT_LIBERATION_MONO = "fonts/LiberationMono-Regular.ttf";
	public static final String FONT_DEFAULT = FONT_UBUNTU;
	public static final String FONT_DEFAULT_FIXED = FONT_LIBERATION_MONO;

	private String font = FONT_DEFAULT;
	private float size = 4f;
	private boolean italic;
	private boolean bold;
	private Color color = Color.BLACK;

	public boolean isItalic() {
		return italic;
	}

	public FontStyle setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}

	public boolean isBold() {
		return bold;
	}

	public FontStyle setBold(boolean bold) {
		this.bold = bold;
		return this;
	}

	public float getSize() {
		return size;
	}

	public FontStyle setSize(float size) {
		this.size = size;
		return this;
	}

	public Color getColor() {
		return color;
	}

	public FontStyle setColor(Color color) {
		this.color = color;
		return this;
	}

	public String getFont() {
		return font;
	}

	public FontStyle setFont(String font) {
		this.font = font;
		return this;
	}

	// --- ---

	public FontStyle(FontStyle style) {
		this.font = style.font;
		this.size = style.size;
		this.bold = style.bold;
		this.italic = style.italic;
		this.color = style.color;
	}

	public FontStyle() {}

}
