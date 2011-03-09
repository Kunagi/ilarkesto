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

public abstract class APdfBuilder extends APdfContainerElement {

	protected FontStyle defaultFontStyle;
	protected float marginTop = 15f;
	protected float marginBottom = 20f;
	protected float marginLeft = 20f;
	protected float marginRight = 20f;

	public APdfBuilder() {
		super(null);
	}

	enum Alignment {
		LEFT, RIGHT, CENTER, JUSTIFIED
	}

	public abstract APdfBuilder newPage();

	public abstract boolean isNewPage();

	public APdfBuilder setDefaultFontStyle(FontStyle defaultFontStyle) {
		this.defaultFontStyle = defaultFontStyle;
		return this;
	}

	public FontStyle getDefaultFontStyle() {
		return defaultFontStyle;
	}

	public APdfBuilder setMarginTop(float marginTop) {
		this.marginTop = marginTop;
		return this;
	}

	public APdfBuilder setMarginBottom(float marginBottom) {
		this.marginBottom = marginBottom;
		return this;
	}

	public APdfBuilder setMarginLeft(float marginLeft) {
		this.marginLeft = marginLeft;
		return this;
	}

	public APdfBuilder setMarginRight(float marginRight) {
		this.marginRight = marginRight;
		return this;
	}

	// --- helper ---

	protected static final int dpi = 72;

	public static float mmToPoints(double mm) {
		return (float) ((mm / 25.4f) * dpi);
	}

}
