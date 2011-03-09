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

import java.io.File;

public abstract class APdfContainerElement extends APdfElement {

	public abstract AParagraph paragraph();

	public abstract AImage image(File file);

	public abstract AImage image(byte[] data);

	public abstract ATable table(float... cellWidths);

	public abstract ATable table(int columnCount);

	public APdfContainerElement(APdfElement parent) {
		super(parent);
	}

	public FieldList fieldList() {
		return new FieldList(this);
	}

	public APdfContainerElement text(Object text) {
		paragraph().text(text);
		return this;
	}

	public APdfContainerElement nl(FontStyle fontStyle) {
		paragraph().nl(fontStyle);
		return this;
	}

	public APdfContainerElement nl() {
		paragraph().nl();
		return this;
	}

}
