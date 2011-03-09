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
package ilarkesto.console;

import ilarkesto.core.base.Str;

import java.util.ArrayList;
import java.util.Arrays;

public class ConsoleTable {

	private final String EMPTY = "";
	private final String SPACE = " ";
	private final String LINEBREAK = "\n";

	// private final Alignment DEFAULT_ALIGNMENT = Alignment.LEFT;
	// private ArrayList<Alignment> alignments = new ArrayList<Alignment>();

	private int padding = 1;

	private ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	private ArrayList<Integer> maxColLenghts = new ArrayList<Integer>();
	private int maxRowLength = 0;

	public ConsoleTable addRow(Object... columns) {
		rows.add(new ArrayList<String>());
		return appendRow(columns);
	}

	public <T> ConsoleTable appendRow(T... moreColumns) {
		ArrayList<String> lastRow = lastRow();

		// update maximal row length
		maxRowLength = Math.max(maxRowLength, lastRow.size() + moreColumns.length);

		// update maximal column lengths
		for (int i = 0; i < moreColumns.length; i++) {
			int colNumber = lastRow.size() + i;
			if (colNumber >= maxColLenghts.size()) {
				maxColLenghts.add(colNumber, moreColumns[i].toString().length());
			} else {
				maxColLenghts
						.set(colNumber, Math.max(maxColLenghts.get(colNumber), moreColumns[i].toString().length()));
			}

		}

		// append columns to row
		for (Object col : moreColumns) {
			lastRow.add(col.toString());
		}

		return this;
	}

	public <T> ConsoleTable appendRowFromColumn(int fromCol, T... moreColumns) {
		ArrayList<String> lastRow = lastRow();

		if (fromCol < lastRow.size())
			throw new IllegalArgumentException("Cannot append from column " + fromCol + ", because there are already "
					+ lastRow.size() + " columns.");

		String[] emptyCols = new String[fromCol - lastRow.size() - 1];
		Arrays.fill(emptyCols, "");
		appendRow(emptyCols);
		appendRow(moreColumns);

		return this;
	}

	private ArrayList<String> lastRow() {
		return rows.get(rows.size() - 1);
	}

	public static enum Alignment {
		LEFT, CENTER, RIGHT
	};

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (ArrayList<String> row : rows) {
			for (int i = 0; i < maxRowLength; i++) {
				String cell = i < row.size() ? row.get(i) : EMPTY;
				sb.append(createPadding(padding));
				sb.append(Str.fillUpRight(cell, SPACE, maxColLenghts.get(i)));
				sb.append(createPadding(padding));
			}
			sb.append(LINEBREAK);
		}
		return sb.toString();
	}

	private Object createPadding(int width) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < width; i++) {
			sb.append(SPACE);
		}
		return sb.toString();
	}
}
