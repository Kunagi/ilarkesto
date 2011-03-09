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

import ilarkesto.gwt.client.AIntegerViewEditWidget;

public class IntegerEditorWidget extends AIntegerViewEditWidget {

	private AIntegerEditorModel model;

	public IntegerEditorWidget(AIntegerEditorModel editor) {
		super();
		this.model = editor;
	}

	@Override
	protected void onIntegerViewerUpdate() {
		setViewerValue(model.getValue());
	}

	@Override
	protected void onMinusClicked() {
		Integer value = model.getValue();
		if (value == null || value <= model.getMin()) return;
		model.decrement();
	}

	@Override
	protected void onPlusClicked() {
		Integer value = model.getValue();
		if (value != null && value >= model.getMax()) return;
		if (value == null) model.setValue(0);
		model.increment();
	}

	@Override
	protected void onEditorSubmit() {
		model.changeValue(getEditorValue());
	}

	@Override
	protected void onEditorUpdate() {
		setEditorValue(model.getValue());
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

}
