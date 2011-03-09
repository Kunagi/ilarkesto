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

import ilarkesto.core.logging.Log;
import ilarkesto.core.menu.MenuItem;
import ilarkesto.core.menu.StaticMenu;
import ilarkesto.core.menu.StaticMenuItem;
import ilarkesto.core.menu.Submenu;
import ilarkesto.gwt.client.animation.AnimatingFlowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigatorWidget<K extends Object> extends AWidget {

	private static final Log log = Log.get(NavigatorWidget.class);

	private FlowPanel panel;
	StaticMenu menu;
	private MenuItem lastAnimatedItem;
	private long lastUpdateTime;

	@Override
	protected Widget onInitialization() {
		if (menu == null) menu = new StaticMenu();

		panel = new FlowPanel();
		panel.setStyleName("NavigatorWidget");

		return panel;
	}

	@Override
	protected void onUpdate() {
		if (!menu.getChangeIndicator().hasChangedSince(lastUpdateTime)) return;

		panel.clear();
		panel.add(Gwt.createEmptyDiv("NavigatorWidget-head"));
		for (StaticMenuItem item : menu.getItems()) {
			panel.add(createItemWidget(item));
		}
		super.onUpdate();
		lastUpdateTime = System.currentTimeMillis();
	}

	protected String getHref(MenuItem item) {
		return null;
	}

	private Widget createItemWidget(final MenuItem item) {
		final ImageAnchor a = new ImageAnchor(null, item.getLabel());
		String href = getHref(item);
		if (href != null) a.setHref(href);
		a.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				log.debug("Item clicked:", item.getLabel());
				event.stopPropagation();
				item.select();
				a.setFocus(false);
				update();
			}
		});

		FlowPanel itemPanel = new FlowPanel();
		itemPanel.setStyleName("NavigatorWidget-item");
		SimplePanel itemLink = Gwt.createDiv("NavigatorWidget-item-link", a);
		itemPanel.add(itemLink);
		if (item.isSelected()) {
			if (item instanceof Submenu) {
				boolean animate = lastAnimatedItem != item;
				// log.debug("---------- animate:", animate);
				Widget submenuPanel = animate ? new AnimatingFlowPanel(0.5) : new FlowPanel();
				if (animate) lastAnimatedItem = item;
				submenuPanel.setStyleName("NavigatorWidget-submenu");
				itemPanel.add(submenuPanel);
				Submenu<MenuItem> submenu = (Submenu) item;
				for (MenuItem subItem : submenu.getMenu().getItems()) {
					((HasWidgets) submenuPanel).add(createItemWidget(subItem));
				}
			} else {
				itemLink.addStyleDependentName("selected");
			}
		}

		return itemPanel;
	}

	public void addItem(String label, K key, Runnable selecionListener) {
		initialize();

		boolean menuEmpty = menu.getItems().isEmpty();

		StaticMenuItem item = menu.addItem(new StaticMenuItem(label));
		if (menuEmpty) item.select();
		item.setPayload(key);
		item.setOnSelect(selecionListener);
	}

	public void select(K key) {
		StaticMenuItem item = menu.getItemByPayload(key);
		if (item == null) return;
		item.select();
		update();
	}

	public void setMenu(StaticMenu menu) {
		this.menu = menu;
	}

	@Override
	public String toString() {
		return "NavigatorWidget(" + menu + ")";
	}
}
