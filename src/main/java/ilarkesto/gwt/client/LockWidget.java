/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.gwt.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LockWidget extends SwitcherWidget {

	private Widget content;
	private Widget locker;

	public LockWidget(Widget content) {
		super(false);
		this.content = content;
		show(content);
	}

	public final void lock(Widget locker) {
		this.locker = locker;
		show(locker);
	}

	public final void lock(String message) {
		lock(createMessageLocker(message));
	}

	public final void unlock() {
		if (!isLocked()) return;
		show(content);
	}

	public final boolean isLocked() {
		return locker != null;
	}

	protected Widget createMessageLocker(String message) {
		return new Label(message);
	}

}
