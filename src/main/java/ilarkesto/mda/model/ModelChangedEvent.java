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
package ilarkesto.mda.model;

import ilarkesto.core.event.AEvent;

public class ModelChangedEvent extends AEvent {

	@Override
	public void tryToGetHandled(Object handler) {
		log.info("Testing event handler:", handler);
		if (handler instanceof ModelChangedHandler) {
			log.info("Calling event handler:", handler);
			((ModelChangedHandler) handler).onModelChanged(this);
		}
	}
}
