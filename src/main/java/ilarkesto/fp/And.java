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
package ilarkesto.fp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class And<E> implements Predicate<E> {

	private List<Predicate<E>>	predicates	= new ArrayList<Predicate<E>>();

	public And() {}

	public And(Predicate<E>... predicates) {
		this.predicates.addAll(Arrays.asList(predicates));
	}

	public void add(Predicate<E> predicate) {
		predicates.add(predicate);
	}

	public boolean test(E parameter) {
		for (Predicate<E> predicate : predicates) {
			if (!predicate.test(parameter)) return false;
		}
		return true;
	}

}
