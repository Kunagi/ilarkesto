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

import ilarkesto.core.changeindicator.ChangeListener;

public class StaticSubmenu extends StaticMenuItem implements Submenu<StaticMenuItem> {

	private StaticMenu submenu = new StaticMenu();

	public StaticSubmenu(String label) {
		super(label);
		setOnSelect(new OnSelect());
		setOnDeselect(new OnDeselect());
		submenu.getChangeIndicator().addChangeListener(new SubmenuChangeListener());
	}

	@Override
	public StaticMenu getMenu() {
		return submenu;
	}

	class OnSelect implements Runnable {

		@Override
		public void run() {
			if (submenu.getSelectedItem() == null) submenu.selectFirstItem();
		}
	}

	class OnDeselect implements Runnable {

		@Override
		public void run() {
			submenu.deselectAll();
		}
	}

	class SubmenuChangeListener implements ChangeListener {

		@Override
		public void onChange() {
			menu.getChangeIndicator().markChanged();
		}
	}

}
