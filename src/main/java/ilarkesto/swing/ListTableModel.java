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
package ilarkesto.swing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class ListTableModel<E> extends AbstractTableModel {

    private Column[] columns;
    private LinkedList<E> elements = new LinkedList<E>();

    public ListTableModel() {
        columns = new Column[] { new DefaultColumn() };
    }

    public void moveToBottom(E element) {
        elements.remove(element);
        elements.add(element);
        fireTableDataChanged();
    }

    public void moveToTop(Collection<E> elements) {
        this.elements.removeAll(elements);
        this.elements.addAll(0, elements);
        fireTableDataChanged();
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column... columns) {
        this.columns = columns;
        fireTableStructureChanged();
    }

    public final void setElements(Collection<E> elements) {
        this.elements = new LinkedList<E>(elements);
        fireTableDataChanged();
    }

    public final void removeElements(Collection<E> elements) {
        this.elements.removeAll(elements);
        fireTableDataChanged();
    }

    public void removeAllElements() {
        elements.clear();
        fireTableDataChanged();
    }

    public final void addElements(Collection<E> elements) {
        this.elements.addAll(elements);
        fireTableDataChanged();
    }

    public final void addElement(E element) {
        elements.add(element);
        fireTableDataChanged();
    }

    public final E getElement(int index) {
        return elements.get(index);
    }

    public int getElementsCount() {
        return elements.size();
    }

    public final int getColumnCount() {
        return columns.length;
    }

    @Override
    public final String getColumnName(int column) {
        return columns[column].getLabel();
    }

    public final int getRowCount() {
        return elements.size();
    }

    public final Object getValueAt(int rowIndex, int columnIndex) {
        return columns[columnIndex].getValue(getElement(rowIndex));
    }

    public static interface Column<E> {

        String getLabel();

        Object getValue(E element);

    }

    public List<E> getElements(int[] selectedRows) {
        List<E> result = new ArrayList<E>(selectedRows.length);
        for (int row : selectedRows) {
            result.add(getElement(row));
        }
        return result;
    }

    class DefaultColumn implements Column {

        public String getLabel() {
            return "";
        }

        public Object getValue(Object element) {
            return element.toString();
        }

    }

}
