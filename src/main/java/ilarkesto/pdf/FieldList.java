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

public class FieldList extends APdfElement {

	private ATable table;
	private FontStyle labelFontStyle;

	public FieldList(APdfContainerElement parent) {
		super(parent);
		table = parent.table(1, 4);
	}

	public APdfContainerElement field(String label) {
		ARow row = table.row();
		row.cell(label, labelFontStyle);
		return row.cell();
	}

	public FieldList setLabelFontStyle(FontStyle labelFontStyle) {
		this.labelFontStyle = labelFontStyle;
		return this;
	}
}
