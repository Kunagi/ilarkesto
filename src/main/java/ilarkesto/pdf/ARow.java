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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ARow {

	private List<ACell> cells = new ArrayList<ACell>();
	private Color defaultBackgroundColor;

	public ACell cell() {
		ACell cell = table.cell();
		cell.setBackgroundColor(defaultBackgroundColor);
		cells.add(cell);
		return cell;
	}

	public ACell cell(Object text, FontStyle fontStyle) {
		ACell cell = table.cell(text, fontStyle);
		cell.setBackgroundColor(defaultBackgroundColor);
		cells.add(cell);
		return cell;
	}

	public ACell cell(Object text) {
		ACell cell = table.cell(text);
		cell.setBackgroundColor(defaultBackgroundColor);
		cells.add(cell);
		return cell;
	}

	public ARow setDefaultBackgroundColor(Color backgroundColor) {
		this.defaultBackgroundColor = backgroundColor;
		return this;
	}

	public ARow setBorder(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorder(color, width);
		return this;
	}

	public ARow setBorderTop(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderTop(color, width);
		return this;
	}

	public ARow setBorderBottom(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderBottom(color, width);
		return this;
	}

	public ARow setBorderLeft(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderLeft(color, width);
		return this;
	}

	public ARow setBorderRight(Color color, float width) {
		for (ACell cell : cells)
			cell.setBorderRight(color, width);
		return this;
	}

	// --- dependencies ---

	private ATable table;

	public ARow(ATable table) {
		this.table = table;
	}

}
