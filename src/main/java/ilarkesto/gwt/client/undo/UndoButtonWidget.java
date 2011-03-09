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
package ilarkesto.gwt.client.undo;

import ilarkesto.gwt.client.DropdownMenuButtonWidget;
import ilarkesto.gwt.client.Gwt;

import com.google.gwt.user.client.ui.Widget;

public class UndoButtonWidget extends DropdownMenuButtonWidget {

	private UndoManager undoManager;

	public UndoButtonWidget() {
		setLabel("Undo");
	}

	@Override
	protected Widget onInitialization() {
		undoManager = Gwt.getUndoManager();
		Widget widget = super.onInitialization();
		return Gwt.createDiv("UndoButtonWidget", widget);
	}

	@Override
	protected void onUpdate() {
		clear();
		if (undoManager != null) {
			for (AUndoOperation operation : undoManager.getOperations()) {
				addAction(new UndoAction(undoManager, operation));
			}
		}
		super.onUpdate();
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

}
