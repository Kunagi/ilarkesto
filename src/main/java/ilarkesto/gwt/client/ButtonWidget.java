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

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ButtonWidget extends AWidget {

	private Button button;
	private AAction action;
	private HTML tooltip;

	public ButtonWidget(AAction action) {
		this.action = action;
	}

	@Override
	protected Widget onInitialization() {
		setStyleName("ButtonWidget");
		button = new Button(action.getLabel(), action);
		tooltip = Gwt.addHtmlTooltip(button, "");
		return button;
	}

	@Override
	protected void onUpdate() {
		button.getElement().setId("button_" + action.getId());
		button.setText(action.getLabel());
		button.setEnabled(action.isPermitted() && action.isExecutable());
		tooltip.setHTML(action.getTooltip());
	}

	@Override
	public String toString() {
		return "ButtonWidget(" + action + ")";
	}

}
