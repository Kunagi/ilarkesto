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

import ilarkesto.gwt.client.editor.AEditorModel;
import ilarkesto.gwt.client.undo.AUndoOperation;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for entities.
 */
public abstract class AGwtEntity {

	private String id;
	private boolean inCreation;
	private transient long localModificationTime = -7;

	public abstract String getEntityType();

	protected abstract AGwtDao getDao();

	public AGwtEntity() {
		this.id = getDao().getNewEntityId();
		inCreation = true;
		updateLocalModificationTime();
	}

	public AGwtEntity(Map data) {
		this.id = (String) data.get("id");
		updateLocalModificationTime();
	}

	public long getLocalModificationTime() {
		return localModificationTime;
	}

	public void updateLocalModificationTime() {
		localModificationTime = System.currentTimeMillis();
	}

	public final String getId() {
		return id;
	}

	void setCreated() {
		this.inCreation = false;
	}

	protected final void propertyChanged(String property, Object value) {
		if (inCreation) return;
		if (value instanceof Date) value = value.toString();
		if (value instanceof Time) value = value.toString();
		if (value instanceof DateAndTime) value = value.toString();
		getDao().entityPropertyChanged(this, property, value);
		updateLocalModificationTime();
	}

	public void storeProperties(Map properties) {
		properties.put("id", getId());
	}

	public Map createPropertiesMap() {
		Map properties = new HashMap();
		storeProperties(properties);
		return properties;
	}

	public boolean matchesKey(String key) {
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		if (!(obj instanceof AGwtEntity)) return false;
		if (this == obj) return true;
		return id.equals(((AGwtEntity) obj).id);
	}

	@Override
	public String toString() {
		return getId();
	}

	// --- helper ---

	protected static boolean matchesKey(Object object, String key) {
		if (object == null) return false;
		return object.toString().toLowerCase().indexOf(key) >= 0;
	}

	protected final String toString(Integer value) {
		return value == null ? null : value.toString();
	}

	protected final String toString(Boolean value) {
		return value == null ? null : value.toString();
	}

	protected final boolean equals(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null && b != null) return false;
		if (a != null && b == null) return false;
		return a.equals(b);
	}

	protected final boolean equals(String id, AGwtEntity entity) {
		if (id == null && entity == null) return true;
		if (id == null && entity != null) return false;
		if (id != null && entity == null) return false;
		return id.equals(entity.getId());
	}

	protected void addUndo(AEditorModel editorModel, Object oldValue) {
		Gwt.getUndoManager().add(new EditorModelUndo(editorModel, oldValue));
	}

	protected class EditorModelUndo extends AUndoOperation {

		private AEditorModel editorModel;
		private Object oldValue;

		public EditorModelUndo(AEditorModel editorModel, Object oldValue) {
			super();
			this.editorModel = editorModel;
			this.oldValue = oldValue;
		}

		@Override
		public String getLabel() {
			return "Undo Change on " + AGwtEntity.this.toString();
		}

		@Override
		protected void onUndo() {
			editorModel.setValue(oldValue);
		}

	}

}
