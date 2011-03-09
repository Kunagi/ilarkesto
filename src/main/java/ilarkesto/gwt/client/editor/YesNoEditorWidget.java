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
package ilarkesto.gwt.client.editor;

import ilarkesto.gwt.client.AViewEditWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class YesNoEditorWidget extends AViewEditWidget {

	private ABooleanEditorModel model;
	private Label viewer;
	private ListBox editor;

	public YesNoEditorWidget(ABooleanEditorModel model) {
		super();
		this.model = model;
	}

	@Override
	protected void onViewerUpdate() {
		viewer.setText(model.isTrue() ? "yes" : "no");
	}

	@Override
	protected void onEditorUpdate() {
		editor.setSelectedIndex(model.isTrue() ? 0 : 1);
	}

	@Override
	protected void onEditorSubmit() {
		model.setValue(editor.getSelectedIndex() == 0 ? true : false);
	}

	@Override
	protected final Widget onViewerInitialization() {
		viewer = new Label();
		return viewer;
	}

	@Override
	protected final Widget onEditorInitialization() {
		editor = new ListBox();
		editor.addChangeHandler(new EditorChangeListener());
		editor.addFocusListener(new EditorFocusListener());
		editor.setVisibleItemCount(2);
		editor.addItem("yes", "true");
		editor.addItem("no", "false");
		return editor;
	}

	@Override
	public boolean isEditable() {
		return model.isEditable();
	}

	@Override
	public String getTooltip() {
		return model.getTooltip();
	}

	@Override
	public String getId() {
		return model.getId();
	}

	private class EditorChangeListener implements ChangeHandler {

		@Override
		public void onChange(ChangeEvent event) {
			submitEditor();
		}

	}

	private class EditorFocusListener implements FocusListener {

		public void onFocus(Widget sender) {}

		public void onLostFocus(Widget sender) {
			submitEditor();
		}

	}
}
