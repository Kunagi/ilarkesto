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

import com.google.gwt.user.client.ui.Widget;

public class ToolbarWidget extends AWidget {

	private FloatingFlowPanel panel;

	public ToolbarWidget() {}

	@Override
	protected Widget onInitialization() {
		panel = new FloatingFlowPanel();
		return Gwt.createDiv("ToolbarWidget", panel);
	}

	public void clear() {
		if (panel != null) panel.clear();
	}

	public <W extends Widget> W add(W widget) {
		initialize();
		panel.add(widget);
		if (isInitialized()) update();
		return widget;
	}

	public <W extends Widget> W insert(W widget, int index) {
		initialize();
		panel.insert(widget, index);
		if (isInitialized()) update();
		return widget;
	}

	public ButtonWidget addButton(AAction action) {
		return add(new ButtonWidget(action));
	}

	public HyperlinkWidget addHyperlink(AAction action) {
		return add(new HyperlinkWidget(action));
	}

	public boolean isEmpty() {
		initialize();
		return panel.isEmpty();
	}

}
