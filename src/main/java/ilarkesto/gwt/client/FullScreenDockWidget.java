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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FullScreenDockWidget extends AWidget {

	private DockPanel dock;

	private Widget north;
	private SimplePanel northWrapper;
	private int northHeight;

	private Widget west;
	private SimplePanel westWrapper;
	private int westWidth;

	private Widget center;
	private SimplePanel centerWrapper;

	public FullScreenDockWidget(Widget north, int northHeight, Widget west, int westWidth, Widget center) {
		this.north = north;
		this.northHeight = northHeight;
		this.west = west;
		this.westWidth = westWidth;
		this.center = center;
	}

	@Override
	protected Widget onInitialization() {
		Window.enableScrolling(false);
		setHeight100();

		dock = new DockPanel();
		dock.setStyleName("FullScreenDockWidget");
		// dock.setBorderWidth(1);
		dock.setSpacing(0);
		dock.setWidth("100%");
		dock.setHeight("100%");

		northWrapper = new SimplePanel();
		northWrapper.setWidget(north);
		northWrapper.setStyleName("FullScreenDockWidget-north");
		northWrapper.setWidth("100%");
		northWrapper.setHeight(northHeight + "px");
		dock.add(northWrapper, DockPanel.NORTH);
		dock.setCellWidth(northWrapper, "100%");
		dock.setCellHeight(northWrapper, northHeight + "px");

		westWrapper = new SimplePanel();
		westWrapper.setWidget(west);
		westWrapper.setStyleName("FullScreenDockWidget-west");
		westWrapper.setWidth(westWidth + "px");
		westWrapper.setHeight("100%");
		dock.add(westWrapper, DockPanel.WEST);
		dock.setCellWidth(westWrapper, westWidth + "px");
		dock.setCellHeight(westWrapper, "100%");

		centerWrapper = new SimplePanel();
		centerWrapper.setWidget(center);
		centerWrapper.setStyleName("FullScreenDockWidget-center");
		// DOM.setStyleAttribute(getElement(), "overflowY", "scroll");
		centerWrapper.setWidth("100%");
		centerWrapper.setHeight("100%");
		dock.add(centerWrapper, DockPanel.CENTER);
		dock.setCellWidth(centerWrapper, "100%");
		dock.setCellHeight(centerWrapper, "100%");

		// Window.addResizeHandler(new DockResizeHandler());

		return dock;
	}

	@Override
	protected void onUpdate() {
		super.onUpdate();
		updateCenterSize();
	}

	private void updateCenterSize() {
		int width = Window.getClientWidth() - westWidth - 20;
		int height = Window.getClientHeight() - northHeight - 20;
		centerWrapper.setSize(width + "px", height + "px");
		dock.setCellWidth(centerWrapper, width + "px");
		dock.setCellHeight(centerWrapper, height + "px");
	}

	private class DockResizeHandler implements ResizeHandler {

		public void onResize(ResizeEvent event) {
			if (dock == null) return;
			updateCenterSize();
		}
	}

}
