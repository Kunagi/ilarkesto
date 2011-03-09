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

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FullscreenPanel extends SimplePanel {

	public FullscreenPanel() {
		setStyleName("FullscreenPanel");
		updateHeight();
		Window.addResizeHandler(new Autoresizer());
	}

	public FullscreenPanel(Widget content) {
		this();
		setWidget(content);
	}

	public void updateHeight() {
		int height = Window.getClientHeight() - 25;
		setHeight(height + "px");
	}

	@Override
	public String toString() {
		return "FullscreenPanel(" + Gwt.toString(getWidget()) + ")";
	}

	private class Autoresizer implements ResizeHandler {

		public void onResize(ResizeEvent event) {
			updateHeight();
		}
	}
}
