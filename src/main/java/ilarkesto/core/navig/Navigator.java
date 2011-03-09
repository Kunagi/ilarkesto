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
package ilarkesto.core.navig;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Navigator {

	private static final Log log = Log.get(Navigator.class);

	private Page rootPage = new Page(null);
	private List<Page> pages = new LinkedList<Page>();
	private List<Plugin> plugins = new ArrayList<Plugin>();
	private NavigatorObserver observer;

	public Navigator() {
		goBackToRoot();
	}

	public void execute(Item item) {
		if (item == null) return;
		log.info("execute:", item);
		Plugin plugin = item.getPlugin();
		if (plugin == null) return;
		plugin.execute(this, item);
	}

	public void goBack() {
		if (pages.size() <= 1) return;
		pages.remove(0);
		firePageChanged();
	}

	public void goNext(Page page) {
		log.info("goNext:", page);
		for (Item item : page.getItems()) {
			log.debug(" -", item);
		}
		pages.add(0, page);
		firePageChanged();
	}

	public int getDepth() {
		return pages.size();
	}

	public Page getPage() {
		return pages.get(0);
	}

	public void goBackToRoot() {
		pages.clear();
		pages.add(rootPage);
		firePageChanged();
	}

	public Page getRootPage() {
		return rootPage;
	}

	public void setObserver(NavigatorObserver observer) {
		this.observer = observer;
	}

	private void firePageChanged() {
		if (observer != null) observer.onPageChanged(this);
	}

	public void addPlugin(Plugin plugin) {
		plugins.add(plugin);
		plugin.initialize(this);
		if (getPage() == rootPage) firePageChanged();
	}

	@Override
	public String toString() {
		return Str.toStringHelper(this, getDepth(), getPage());
	}

}
