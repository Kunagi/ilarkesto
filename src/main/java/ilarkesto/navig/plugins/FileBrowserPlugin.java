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
package ilarkesto.navig.plugins;

import ilarkesto.base.Env;
import ilarkesto.core.base.Str;
import ilarkesto.core.navig.Item;
import ilarkesto.core.navig.Navigator;
import ilarkesto.core.navig.Page;
import ilarkesto.core.navig.Plugin;

import java.io.File;

public class FileBrowserPlugin implements Plugin {

	private Item rootItem;

	public FileBrowserPlugin() {
		rootItem = new Item(this, "File Browser");
	}

	@Override
	public void initialize(Navigator navigator) {
		navigator.getRootPage().add(rootItem);
	}

	@Override
	public void execute(Navigator navigator, Item item) {
		if (item == rootItem) {
			Page page = createPage(File.listRoots());
			page.setLabel(rootItem.getLabel());
			navigator.goNext(page);
			return;
		}
		File file = (File) item.getPayload();
		if (file.isDirectory()) {
			Page page = createPage(file.listFiles());
			page.setLabel(file.getName());
			navigator.goNext(page);
			return;
		}
		Env.get().executeFile(file, false);
	}

	private Page createPage(File[] files) {
		Page page = new Page(this);
		for (File file : files) {
			page.add(createItem(file));
		}
		return page;
	}

	private Item createItem(File file) {
		String name = file.getName();
		if (Str.isBlank(name)) name = file.getPath();
		Item item = new Item(this, name);
		item.setPayload(file);
		return item;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
