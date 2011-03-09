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
package ilarkesto.base;

import java.util.Comparator;

/**
 * A Comparator wrapper, which inverts the order of the inner Comparator.
 */
public class ReverseComparator implements Comparator {

	private Comparator comparator;

	public ReverseComparator(Comparator comparator) {
		this.comparator = comparator;
	}

	public final int compare(Object o1, Object o2) {
		return -comparator.compare(o1, o2);
	}

}
