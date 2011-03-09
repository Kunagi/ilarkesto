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

import ilarkesto.core.base.Str;
import ilarkesto.gwt.client.AViewEditWidget;
import ilarkesto.gwt.client.Time;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TimeEditorWidget extends AViewEditWidget {

	private ATimeEditorModel model;
	private Label viewer;
	private TextBox editor;

	public TimeEditorWidget(ATimeEditorModel model) {
		super();
		this.model = model;
	}

	@Override
	protected void onViewerUpdate() {
		setViewerValue(model.getValue());
	}

	@Override
	protected void onEditorUpdate() {
		setEditorValue(model.getValue());
	}

	@Override
	protected void onEditorSubmit() {
		model.changeValue(getEditorValue());
	}

	@Override
	protected final Widget onViewerInitialization() {
		viewer = new Label();
		return viewer;
	}

	@Override
	protected final Widget onEditorInitialization() {
		editor = new TextBox();
		editor.addFocusListener(new SubmitEditorFocusListener());
		editor.addKeyPressHandler(new EditorKeyboardListener());
		return editor;
	}

	public final void setViewerValue(Time value) {
		viewer.setText(value == null ? "." : value.toString());
	}

	public final void setEditorValue(Time value) {
		editor.setText(value == null ? null : value.toString());
		editor.setSelectionRange(0, editor.getText().length());
		editor.setFocus(true);
	}

	public final Time getEditorValue() {
		String s = editor.getText();
		if (Str.isBlank(s)) return null;
		return new Time(s);
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

	private class EditorKeyboardListener implements KeyPressHandler {

		@Override
		public void onKeyPress(KeyPressEvent event) {
			char keyCode = event.getCharCode();

			if (keyCode == KeyCodes.KEY_ENTER) {
				submitEditor();
			} else if (keyCode == KeyCodes.KEY_ESCAPE) {
				cancelEditor();
			}
		}
	}

}
