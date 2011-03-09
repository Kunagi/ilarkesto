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
package ilarkesto.gwt.client;

import ilarkesto.gwt.client.editor.ABooleanEditorModel;
import ilarkesto.gwt.client.editor.ADateAndTimeEditorModel;
import ilarkesto.gwt.client.editor.ADateEditorModel;
import ilarkesto.gwt.client.editor.AIntegerEditorModel;
import ilarkesto.gwt.client.editor.ATextEditorModel;
import ilarkesto.gwt.client.editor.DateAndTimeEditorWidget;
import ilarkesto.gwt.client.editor.DateEditorWidget;
import ilarkesto.gwt.client.editor.IntegerEditorWidget;
import ilarkesto.gwt.client.editor.RichtextEditorWidget;
import ilarkesto.gwt.client.editor.TextEditorWidget;
import ilarkesto.gwt.client.editor.YesNoEditorWidget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.Widget;

public class TableBuilder {

	private Row currentRow = new Row();
	private List<Row> rows = new ArrayList<Row>();
	private String width = "100%";
	private String[] columnWidths;
	private int cellSpacing;
	private int cellPadding;
	private boolean centered;
	private String styleName;

	public TableBuilder() {}

	public FlexTable createTable() {
		if (!currentRow.cells.isEmpty()) nextRow();
		FlexTable table = new FlexTable();
		table.setStyleName(styleName);
		table.setCellSpacing(cellSpacing);
		table.setCellPadding(cellPadding);
		if (centered) table.getElement().setAttribute("align", "center");
		String widthAsString = width == null ? "" : width;
		table.setWidth(widthAsString);
		table.getElement().setAttribute("width", widthAsString);
		if (columnWidths != null) {
			ColumnFormatter columnFormatter = table.getColumnFormatter();
			for (int i = 0; i < columnWidths.length; i++) {
				columnFormatter.setWidth(i, columnWidths[i]);
			}
		}
		int rowIndex = 0;
		for (Row row : rows) {
			int colIndex = 0;
			for (Cell cell : row.cells) {
				table.setWidget(rowIndex, colIndex, cell.widget);
				if (cell.colspan > 1) table.getFlexCellFormatter().setColSpan(rowIndex, colIndex, cell.colspan);
				if (cell.align != null) {
					table.getCellFormatter().setHorizontalAlignment(rowIndex, colIndex, cell.align);
				}
				colIndex += cell.colspan;
			}
			rowIndex++;
		}
		return table;
	}

	public void setColumnWidths(int... columnWidths) {
		this.columnWidths = new String[columnWidths.length];
		for (int i = 0; i < columnWidths.length; i++) {
			this.columnWidths[i] = columnWidths[i] + "px";
		}
	}

	public void setColumnWidths(String... columnWidths) {
		this.columnWidths = columnWidths;
	}

	public TableBuilder addFieldRow(String label, ATextEditorModel model, int colspan) {
		addField(label, model, colspan);
		nextRow();
		return this;
	}

	public TableBuilder addFieldRow(String label, Widget value, int colspan) {
		addField(label, value, colspan);
		nextRow();
		return this;
	}

	public TableBuilder addFieldRow(String label, ATextEditorModel model) {
		addField(label, model, 1);
		nextRow();
		return this;
	}

	public TableBuilder addFieldRow(String label, AIntegerEditorModel model) {
		addField(label, model, 1);
		nextRow();
		return this;
	}

	public TableBuilder addFieldRow(String label, ABooleanEditorModel model) {
		addField(label, model, 1);
		nextRow();
		return this;
	}

	public TableBuilder addFieldRow(String label, ADateAndTimeEditorModel model) {
		addField(label, model, 1);
		nextRow();
		return this;
	}

	public TableBuilder addFieldRow(String label, ADateEditorModel model) {
		addField(label, model, 1);
		nextRow();
		return this;
	}

	public TableBuilder addFieldRow(String label, Widget value) {
		addField(label, value);
		nextRow();
		return this;
	}

	public TableBuilder addField(String label, Widget value) {
		addFieldLabel(label);
		add(value);
		return this;
	}

	public TableBuilder addField(String label, ATextEditorModel model, int colspan) {
		Widget editor;
		if (model.isRichtext()) {
			editor = new RichtextEditorWidget(model);
		} else {
			editor = new TextEditorWidget(model);
			if (model.isMandatory()) {
				((TextEditorWidget) editor).switchToEditModeIfNull();
			}
		}
		// if (model.isRichtext()) {
		// FlowPanel panel = new FlowPanel();
		// panel.add(Gwt.createFieldLabel(label));
		// panel.add(editor);
		// return add(panel, colspan + 1);
		// } else {
		// }
		return addField(label, editor, colspan);
	}

	public TableBuilder addField(String label, ADateAndTimeEditorModel model, int colspan) {
		return addField(label, new DateAndTimeEditorWidget(model), colspan);
	}

	public TableBuilder addField(String label, ADateEditorModel model) {
		return addField(label, model, 1);
	}

	public TableBuilder addField(String label, ADateEditorModel model, int colspan) {
		return addField(label, new DateEditorWidget(model), colspan);
	}

	public TableBuilder addField(String label, AIntegerEditorModel model) {
		return addField(label, new IntegerEditorWidget(model), 1);
	}

	public TableBuilder addField(String label, AIntegerEditorModel model, int colspan) {
		return addField(label, new IntegerEditorWidget(model), colspan);
	}

	public TableBuilder addField(String label, ABooleanEditorModel model, int colspan) {
		return addField(label, new YesNoEditorWidget(model), colspan);
	}

	public TableBuilder addField(String label, Widget value, int colspan) {
		addFieldLabel(label);
		add(value, colspan, null);
		return this;
	}

	public TableBuilder addFieldLabel(String text) {
		add(Gwt.createFieldLabel(text), 1, HasHorizontalAlignment.ALIGN_RIGHT);
		return this;
	}

	public TableBuilder addRow(Widget widget, int colspan) {
		add(widget, colspan, null);
		nextRow();
		return this;
	}

	public TableBuilder addRow(Widget... widgets) {
		add(widgets);
		nextRow();
		return this;
	}

	public TableBuilder addSpacer(int width, int height) {
		add(Gwt.createSpacer(width, height));
		return this;
	}

	public TableBuilder add(Widget... widgets) {
		for (Widget widget : widgets) {
			add(widget);
		}
		return this;
	}

	public TableBuilder add(Widget widget) {
		return add(widget, 1, null);
	}

	public TableBuilder add(Widget widget, int colspan) {
		return add(widget, colspan, null);
	}

	public TableBuilder add(Widget widget, int colspan, HorizontalAlignmentConstant align) {
		Cell cell = new Cell();
		cell.widget = widget;
		cell.align = align;
		cell.colspan = colspan;
		currentRow.cells.add(cell);
		return this;
	}

	public TableBuilder nextRow() {
		rows.add(currentRow);
		currentRow = new Row();
		return this;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setCellSpacing(int cellSpacing) {
		this.cellSpacing = cellSpacing;
	}

	public void setCellPadding(int cellPadding) {
		this.cellPadding = cellPadding;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
		if (centered) setWidth(null);
	}

	public TableBuilder setStyleName(String styleName) {
		this.styleName = styleName;
		return this;
	}

	private static class Row {

		private List<Cell> cells = new ArrayList<Cell>();

	}

	private static class Cell {

		private Widget widget;
		private int colspan = 1;
		private HorizontalAlignmentConstant align;

	}

	public static FlexTable row(int spacing, Widget... widgets) {
		return row(true, spacing, widgets);
	}

	public static FlexTable row(boolean width100, int spacing, Widget... widgets) {
		assert widgets.length > 0;

		TableBuilder tb = new TableBuilder();
		if (!width100) tb.setWidth(null);

		if (widgets.length == 1) {
			tb.add(widgets[0]);
			return tb.createTable();
		}

		int columnWidth = 100 / widgets.length;
		String[] widths = new String[(widgets.length * 2) - 1];
		int colIndex = 0;
		for (int i = 0; i < widgets.length; i++) {
			if (colIndex > 0) {
				widths[colIndex] = spacing + "pt";
				colIndex++;
			}
			widths[colIndex] = columnWidth + "%";
			colIndex++;
		}

		tb.setColumnWidths(widths);
		boolean first = true;
		for (Widget widget : widgets) {
			if (first) {
				first = false;
			} else {
				tb.add(Gwt.createSpacer(spacing, 1));
			}
			tb.add(widget);
		}
		tb.nextRow();
		return tb.createTable();
	}

	public static FlexTable column(int spacing, Widget... widgets) {
		assert widgets.length > 0;

		TableBuilder tb = new TableBuilder();

		boolean first = true;
		for (Widget widget : widgets) {
			if (first) {
				first = false;
			} else {
				tb.addRow(Gwt.createSpacer(1, spacing));
			}
			tb.addRow(widget);
		}

		return tb.createTable();
	}
}
