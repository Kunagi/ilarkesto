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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class DropdownMenuButtonWidget extends AWidget {

	private LinkedHashMap<String, List<AAction>> actionGroups;
	private MenuBar menu;
	private String icon;
	private String label;

	@Override
	protected Widget onInitialization() {
		setStyleName("DropdownMenuButtonWidget");
		actionGroups = new LinkedHashMap<String, List<AAction>>();

		menu = new MenuBar(true);

		MenuBar menuBar = new MenuBar();
		String title = "<img src='dropdown.png' alt='Dropdown Menu' width='16' height='16'>";
		if (icon != null) title = "<img src='" + icon + "' width='16' height='16' alt='ico'>" + title;
		if (label != null) title = "<div style='float: left; margin-top: 4px;'>" + label + "</div> " + title;
		menuBar.addItem(title, true, menu);
		menuBar.setPopupPosition(MenuBar.PopupPosition.LEFT);

		Widget wrapper = TableBuilder.row(false, 0, menuBar);
		return wrapper;
	}

	@Override
	protected void onUpdate() {
		menu.clearItems();
		if (actionGroups.isEmpty()) {
			menu.setVisible(false);
		} else {
			menu.setVisible(true);
			int i = 0;
			for (List<AAction> actions : actionGroups.values()) {
				for (AAction action : actions) {
					if (!action.isExecutable()) continue;

					MenuItem menuItem;
					if (action.isPermitted()) {
						menuItem = new MenuItem(action.getLabel(), action);
					} else {
						menuItem = new MenuItem(action.getLabel(), (Command) null);
						menuItem.addStyleName("MenuItem-disabled");
					}
					menuItem.setTitle(action.getTooltip());
					// Gwt.addHtmlTooltip(menuItem, action.getTooltip());
					menu.addItem(menuItem);
				}
				i++;
				if (i < actionGroups.size()) menu.addSeparator();
			}
		}
	}

	public void addAction(AAction action) {
		addAction("default", action);
	}

	public void addAction(String groupName, AAction action) {
		initialize();
		if (actionGroups.get(groupName) == null) {
			actionGroups.put(groupName, new LinkedList<AAction>());
		}
		actionGroups.get(groupName).add(action);
	}

	public void addSeparator() {
		menu.addSeparator();
	}

	public void clear() {
		initialize();
		actionGroups.clear();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
