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
import ilarkesto.gwt.client.AWidget;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class TextOutputWidget extends AWidget {

	private HTML viewer;
	private AFieldModel model;
	private boolean forceEmptyChar;

	public TextOutputWidget(AFieldModel model) {
		super();
		this.model = model;
	}

	@Override
	protected Widget onInitialization() {
		viewer = new HTML();
		getElement().setId(getId());
		return viewer;
	}

	@Override
	protected void onUpdate() {
		Object value = model.getValue();
		String text = value == null ? null : String.valueOf(value);
		if (forceEmptyChar && Str.isBlank(text)) {
			viewer.setHTML("&nbsp;");
		} else {
			viewer.setText(text);
		}
		viewer.setTitle(getTooltip());
	}

	public String getTooltip() {
		return model.getTooltip();
	}

	public TextOutputWidget setForceEmptyChar(boolean forceEmptyChar) {
		this.forceEmptyChar = forceEmptyChar;
		return this;
	}

	@Override
	public String getId() {
		return model.getId();
	}

}
