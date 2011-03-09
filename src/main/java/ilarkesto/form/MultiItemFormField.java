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
package ilarkesto.form;

import ilarkesto.base.StringProvider;
import ilarkesto.id.CountingIdGenerator;
import ilarkesto.id.IdGenerator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;

public class MultiItemFormField<T> extends AFormField {

	private Set<T> value;

	private AddButton addButton;

	private RemoveAllButton removeAllButton;

	private Collection<T> selectableItems;

	private Collection<MultiItem<T>> selectableMultiItems = Collections.emptyList();

	private Map<T, RemoveButton<T>> removeButtons = new HashMap<T, RemoveButton<T>>();

	private StringProvider<T> itemTooltipProvider;

	private StringProvider<T> itemLabelProvider;

	private String noSelectionLabel;

	public MultiItemFormField(String name) {
		super(name);

		addButton = (AddButton) new AddButton().setValidateForm(false).setLabel("Hinzuf\u00FCgen...").setIcon("add");
		removeAllButton = (RemoveAllButton) new RemoveAllButton().setValidateForm(false).setLabel("Alle entferenen")
				.setIcon("delete");
	}

	public MultiItemFormField<T> setNoSelectionLabel(String noSelectionLabel) {
		this.noSelectionLabel = noSelectionLabel;
		return this;
	}

	public String getNoSelectionLabel() {
		return noSelectionLabel;
	}

	public MultiItemFormField<T> setItemLabelProvider(StringProvider<T> itemLabelProvider) {
		this.itemLabelProvider = itemLabelProvider;
		return this;
	}

	public StringProvider<T> getItemLabelProvider() {
		return itemLabelProvider;
	}

	public MultiItemFormField<T> setItemTooltipProvider(StringProvider<T> itemTooltipProvider) {
		this.itemTooltipProvider = itemTooltipProvider;
		return this;
	}

	public StringProvider<T> getItemTooltipProvider() {
		return itemTooltipProvider;
	}

	public MultiItemFormField<T> setSelectableItems(Collection<T> items) {
		this.selectableItems = items == null ? new HashSet<T>() : items;
		return this;
	}

	public Collection<T> getSelectableItems() {
		Set<T> result = new HashSet<T>(selectableItems);
		result.removeAll(value);
		return result;
	}

	public MultiItemFormField<T> setSelectableMultiItems(Collection<MultiItem<T>> multiItmes) {
		this.selectableMultiItems = multiItmes;
		return this;
	}

	public Collection<MultiItem<T>> getSelectableMultiItems() {
		return selectableMultiItems;
	}

	public AddButton getAddButton() {
		return addButton;
	}

	public RemoveAllButton getRemoveAllButton() {
		return removeAllButton;
	}

	public RemoveButton<T> getRemoveButton(T item) {
		return removeButtons.get(item);
	}

	public RemoveButton<T> getRemoveButton(String id) {
		for (RemoveButton<T> button : removeButtons.values()) {
			if (button.getId().equals(id)) return button;
		}
		throw new RuntimeException("button does not exist: " + id);
	}

	public MultiItemFormField<T> setValue(Collection<T> value) {
		this.value = value == null ? null : new HashSet<T>(value);
		removeButtons.clear();
		for (T item : value) {
			RemoveButton button = (RemoveButton) new RemoveButton(item).setLabel("Entfernen").setIcon("delete");
			removeButtons.put(item, button);
		}
		return this;
	}

	public void removeValueItem(T item) {
		value.remove(item);
		setValue(value);
	}

	public void removeAllItems() {
		value.clear();
		setValue(value);
	}

	public void addValueItem(T item) {
		value.add(item);
		setValue(value);
	}

	public void addValueItems(Collection<T> items) {
		value.addAll(items);
		setValue(value);
	}

	public Set<T> getValue() {
		return value;
	}

	public String getValueAsString() {
		return value == null ? "0" : String.valueOf(value.size());
	}

	public void update(Map<String, String> data, Collection<FileItem> uploadedFiles) {
	// nop
	}

	public void validate() throws ValidationException {
		if (isRequired() && (value == null || value.size() == 0))
			throw new ValidationException("Hier ist eine Auswahl erforderlich.");
	}

	public class AddButton extends FormButton {

		public AddButton() {
			super(Form.BUTTON_PREFIX + Form.ADD_ITEM_BUTTON_NAME_PREFIX + MultiItemFormField.this.getName());
		}

		public MultiItemFormField getField() {
			return MultiItemFormField.this;
		}

	}

	public class RemoveAllButton extends FormButton {

		public RemoveAllButton() {
			super(Form.BUTTON_PREFIX + Form.REMOVE_ALLITEMS_BUTTON_NAME_PREFIX + MultiItemFormField.this.getName());
		}

		public MultiItemFormField getField() {
			return MultiItemFormField.this;
		}

	}

	public class RemoveButton<T> extends FormButton {

		private String id;

		private T item;

		public RemoveButton(T item) {
			this(item, buttonIdGenerator.generateId());
		}

		private RemoveButton(T item, String id) {
			super(Form.BUTTON_PREFIX + Form.REMOVE_ITEM_BUTTON_NAME_PREFIX + MultiItemFormField.this.getName() + "_"
					+ id);
			this.item = item;
			this.id = id;

			setValidateForm(false);
		}

		public MultiItemFormField<T> getField() {
			return (MultiItemFormField<T>) MultiItemFormField.this;
		}

		public T getItem() {
			return item;
		}

		public String getId() {
			return id;
		}

	}

	public class EditButton<T> extends FormButton {

		private String id;

		private T item;

		public EditButton(T item) {
			this(item, buttonIdGenerator.generateId());
		}

		private EditButton(T item, String id) {
			super(Form.BUTTON_PREFIX + Form.REMOVE_ITEM_BUTTON_NAME_PREFIX + MultiItemFormField.this.getName() + "_"
					+ id);
			this.item = item;
			this.id = id;

			setValidateForm(false);
		}

		public MultiItemFormField<T> getField() {
			return (MultiItemFormField<T>) MultiItemFormField.this;
		}

		public T getItem() {
			return item;
		}

		public String getId() {
			return id;
		}

	}

	// --- dependencies ---

	private static IdGenerator buttonIdGenerator = new CountingIdGenerator(MultiItemFormField.class.getSimpleName());

	// --- ---

}
