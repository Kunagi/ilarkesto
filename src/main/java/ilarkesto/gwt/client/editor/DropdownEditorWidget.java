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

import ilarkesto.gwt.client.ADropdownViewEditWidget;
import ilarkesto.gwt.client.LabelProvider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DropdownEditorWidget<T> extends ADropdownViewEditWidget {

	private AOptionEditorModel<T> model;
	private LabelProvider<T> labelProvider;
	private List<T> options;

	public DropdownEditorWidget(AOptionEditorModel<T> editor, LabelProvider<T> labelProvider) {
		super();
		this.model = editor;
		this.labelProvider = labelProvider;
	}

	@Override
	protected void onViewerUpdate() {
		String label = labelProvider.getLabel(model.getValue());
		setViewerText(label);
	}

	@Override
	protected void onEditorUpdate() {
		options = model.getOptions();
		Map<String, String> optionsMap = new LinkedHashMap<String, String>();
		int index = 0;
		for (T option : options) {
			optionsMap.put(String.valueOf(index), labelProvider.getLabel(option));
			index++;
		}
		setOptions(optionsMap);
	}

	@Override
	protected void onEditorSubmit() {
		String selected = getSelectedOption();
		int index = Integer.parseInt(selected);
		model.changeValue(options.get(index));
		// TODO catch exceptions
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
