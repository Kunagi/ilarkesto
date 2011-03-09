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
package ilarkesto.ui.swing;

import ilarkesto.core.logging.Log;
import ilarkesto.persistence.TransactionService;
import ilarkesto.swing.Swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public abstract class AComponent {

	private static final Log LOG = Log.get(AComponent.class);

	protected abstract void initializeControls();

	protected abstract JComponent createComponent();

	protected abstract void updateControls();

	private JComponent component;
	private boolean initialized;

	// --- dependencies ---

	protected SwingUi ui;
	protected TransactionService transactionService;

	public final void setUi(SwingUi ui) {
		this.ui = ui;
	}

	public final void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	public final void initialize() {
		if (initialized) throw new RuntimeException(getName() + " already initialized");
		initialized = true;

		initializeControls();
	}

	// --- ---

	public final JComponent getJavaComponent() {
		if (!initialized) initialize();
		if (component == null) {
			Swing.invokeInEventDispatchThread(new Runnable() {

				@Override
				public void run() {
					updateControls();
					component = createComponent();
				}

			});
		}
		return component;
	}

	public final void resetComponent() {
		component = null;
	}

	protected String getStringKeyPrefix() {
		return getName();
	}

	protected String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public String toString() {
		return getName();
	}

	// --- helper ---

	public final void updateControlsInEventDispatchThreadLater() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				updateControls();
			}

		});
	}

	protected final JButton createButton(String name) {
		JButton c = new JButton();
		c.setText(string(name + ".label"));
		c.setToolTipText(string(name + ".hint"));
		return c;
	}

	protected final JButton createButton(String name, String icon) {
		JButton button = createButton(name);
		button.setIcon(Swing.getIcon16(icon));
		return button;
	}

	protected final String string(String key, Object... parameters) {
		return ui.getLocalizer().string(getStringKeyPrefix() + "." + key, parameters);
	}

	protected abstract class ListModel<I> extends AbstractTableModel implements ListSelectionListener {

		protected abstract void onItemsSelected(List<I> items);

		protected abstract String getName();

		private List<I> items = Collections.emptyList();
		private JTable table;
		private List<Column<I>> columns;
		private boolean settingItems;

		public ListModel() {}

		public void addColumn(Column<I> column) {
			if (columns == null) columns = new ArrayList<Column<I>>();
			columns.add(column);
			column.setListModel(this);
			fireTableStructureChanged();
			updateColumns();
		}

		private void updateColumns() {
			TableColumnModel tcm = table.getColumnModel();
			int count = tcm.getColumnCount();
			for (int i = 0; i < count; i++) {
				columns.get(i).update(tcm.getColumn(i));
			}
		}

		public Object getValueAt(int columnIndex, I item) {
			if (columns == null) return item.toString();
			return columns.get(columnIndex).getValue(item);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			I item = items.get(rowIndex);
			return getValueAt(columnIndex, item);
		}

		public void setTable(JTable table) {
			this.table = table;
		}

		public void setItems(Collection<I> items) {
			List<I> selectedItems = getSelectedItems();
			this.items = new ArrayList(items);
			settingItems = true;
			fireTableDataChanged();
			settingItems = false;
			setSelectedItems(selectedItems);
		}

		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public int getColumnCount() {
			if (columns == null) return 1;
			return columns.size();
		}

		@Override
		public String getColumnName(int column) {
			if (columns == null) return null;
			return columns.get(column).getLabel();
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) return;
			if (settingItems) return;
			onItemsSelected(getSelectedItems());
		}

		public void setSelectedItems(List<I> selectedItems) {
			selectedItems.retainAll(items);
			ListSelectionModel selectionModel = table.getSelectionModel();
			selectionModel.clearSelection();
			for (int index : getIndexes(selectedItems)) {
				selectionModel.addSelectionInterval(index, index);
			}
		}

		public List<I> getSelectedItems() {
			return getItems(table.getSelectedRows());
		}

		public I getSelectedItem() {
			int index = table.getSelectedRow();
			return index < 0 ? null : items.get(index);
		}

		private int[] getIndexes(Collection<I> items) {
			int[] ret = new int[items.size()];
			int i = 0;
			for (I item : items) {
				ret[i] = this.items.indexOf(item);
				i++;
			}
			return ret;
		}

		private List<I> getItems(int[] indexes) {
			List<I> ret = new ArrayList<I>(indexes.length);
			for (int index : indexes) {
				ret.add(items.get(index));
			}
			return ret;
		}
	}

	protected abstract class Column<I> {

		private ListModel<I> listModel;

		public abstract String getName();

		public abstract Object getValue(I item);

		public String getLabel() {
			return string(listModel.getName() + "." + getName());
		}

		public void setListModel(ListModel<I> listModel) {
			this.listModel = listModel;
		}

		public void update(TableColumn tc) {}

	}

	protected abstract class DropdownModel<I> implements ComboBoxModel, ActionListener {

		protected abstract void onItemSelected(I item);

		private List<ListDataListener> listeners = new ArrayList<ListDataListener>(1);
		private List<I> items = Collections.emptyList();
		private I selectedItem;
		private JComboBox dropdown;

		public DropdownModel() {}

		public void setDropdown(JComboBox dropdown) {
			this.dropdown = dropdown;
		}

		public void setItems(Collection<I> items) {
			this.items = new ArrayList(items);
			ListDataEvent event = new ListDataEvent(AComponent.this, ListDataEvent.CONTENTS_CHANGED, 0,
					items.size() - 1);
			if (selectedItem != null) {
				if (!items.contains(selectedItem)) setSelectedItem(null);
			}
			for (ListDataListener listener : listeners) {
				listener.contentsChanged(event);
			}
			if (selectedItem == null && !this.items.isEmpty()) setSelectedItem(this.items.get(0));
		}

		public void setSelectedItem(Object anItem) {
			if (this.selectedItem == anItem) return;
			this.selectedItem = (I) anItem;
			onItemSelected(selectedItem);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public Object getElementAt(int index) {
			return items.get(index);
		}

		@Override
		public int getSize() {
			return items.size();
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}

		public I getSelectedItem() {
			return selectedItem;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setSelectedItem(dropdown.getSelectedItem());
		}

	}
}
