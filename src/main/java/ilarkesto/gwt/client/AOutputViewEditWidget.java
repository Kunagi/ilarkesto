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

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AOutputViewEditWidget extends AViewEditWidget {

	private SimplePanel wrapper;

	public void setViewer(Widget viewer) {
		initialize();
		wrapper.setWidget(viewer);
	}

	@Override
	protected Widget onViewerInitialization() {
		wrapper = new SimplePanel();
		return wrapper;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	protected Widget onEditorInitialization() {
		return null;
	}

	@Override
	protected void onEditorSubmit() {}

	@Override
	protected void onEditorUpdate() {}

}
