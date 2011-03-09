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
package ilarkesto.core.menu;

import ilarkesto.core.changeindicator.ChangeIndicator;
import ilarkesto.core.changeindicator.HasChangeIndicator;

import java.util.ArrayList;
import java.util.List;

public class StaticMenu implements Menu<StaticMenuItem>, HasChangeIndicator {

	List<StaticMenuItem> items = new ArrayList<StaticMenuItem>();
	ChangeIndicator changeIndicator = new ChangeIndicator();

	@Override
	public List<StaticMenuItem> getItems() {
		return items;
	}

	@Override
	public StaticMenuItem getSelectedItem() {
		for (StaticMenuItem item : items) {
			if (item.isSelected()) return item;
		}
		return null;
	}

	public StaticMenuItem getItemByPayload(Object payload) {
		for (StaticMenuItem item : items) {
			if (item.isPayload(payload)) return item;
			if (item instanceof StaticSubmenu) {
				StaticSubmenu subMenu = (StaticSubmenu) item;
				StaticMenuItem foundItem = subMenu.getMenu().getItemByPayload(payload);
				if (foundItem != null) return foundItem;
			}
		}
		return null;
	}

	public <I extends StaticMenuItem> I addItem(I item) {
		item.setMenu(this);
		items.add(item);
		changeIndicator.markChanged();
		return item;
	}

	public void deselectAll() {
		for (StaticMenuItem item : items)
			item.deselect();
	}

	public void selectFirstItem() {
		if (items.isEmpty()) return;
		items.get(0).select();
	}

	@Override
	public ChangeIndicator getChangeIndicator() {
		return changeIndicator;
	}

}
