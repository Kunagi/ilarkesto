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
package ilarkesto.gwt.client.animation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;

public abstract class AObservableAnimation extends Animation {

	private List<CompletionListener> listeners = new ArrayList<CompletionListener>();

	public void addCompletionListener(CompletionListener l) {
		this.listeners.add(l);
	}

	public void fireCompletionEvent() {
		for (CompletionListener element : this.listeners) {
			element.completionEvent(this);
		}
	}

}
