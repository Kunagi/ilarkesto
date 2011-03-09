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
package ilarkesto.core.changeindicator;

import java.util.ArrayList;
import java.util.List;

public class ChangeIndicator {

	private long changeTime = System.currentTimeMillis();
	private List<ChangeListener> changeListeners;

	public void markChanged() {
		changeTime = System.currentTimeMillis();
		if (changeListeners != null) {
			for (ChangeListener listener : changeListeners) {
				listener.onChange();
			}
		}
	}

	public long getChangeTime() {
		return changeTime;
	}

	public boolean hasChangedSince(long time) {
		return changeTime > time;
	}

	public void addChangeListener(ChangeListener listener) {
		if (changeListeners == null) changeListeners = new ArrayList<ChangeListener>(1);
		changeListeners.add(listener);
	}

}
