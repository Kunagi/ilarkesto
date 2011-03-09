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
import ilarkesto.gwt.client.Date;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateEditorWidget extends AViewEditWidget {

	private ADateEditorModel model;
	private Label viewer;
	private DatePicker editor;
	private FocusPanel editorWrapper;

	public DateEditorWidget(ADateEditorModel model) {
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
		editorWrapper.setFocus(true);
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
		editor = new DatePicker();
		editor.addValueChangeHandler(new DateChangeHandler());

		editorWrapper = new FocusPanel();
		editorWrapper.addKeyPressHandler(new CancelKeyPressHandler());
		editorWrapper.add(editor);
		editorWrapper.setFocus(true);

		return editorWrapper;
	}

	public final void setViewerValue(Date value) {
		viewer.setText(value == null ? "." : value.toString());
	}

	public final void setEditorValue(Date value) {
		java.util.Date javaDate = value == null ? null : value.toJavaDate();
		editor.setValue(javaDate);
		if (javaDate != null) editor.setCurrentMonth(javaDate);
	}

	public final Date getEditorValue() {
		java.util.Date javaDate = editor.getValue();
		return javaDate == null ? null : new Date(javaDate);
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

	private class DateChangeHandler implements ValueChangeHandler<java.util.Date> {

		@Override
		public void onValueChange(ValueChangeEvent<java.util.Date> event) {
			submitEditor();
		}

	}

}
