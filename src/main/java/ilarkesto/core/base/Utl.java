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
package ilarkesto.core.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

public class Utl {

	public static <T> List<T> toList(T... elements) {
		if (elements == null) return null;
		List<T> ret = new ArrayList<T>(elements.length);
		for (T element : elements) {
			if (element == null) continue;
			ret.add(element);
		}
		return ret;
	}

	public static boolean equals(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}

	public static int compare(int i1, int i2) {
		if (i1 > i2) return 1;
		if (i1 < i2) return -1;
		return 0;
	}

	public static int compare(Comparable a, Comparable b) {
		if (a == null && b == null) return 0;
		if (a == null && b != null) return -1;
		if (a != null && b == null) return 1;
		return a.compareTo(b);
	}

	public static int parseHex(String hex) {
		return Integer.parseInt(hex, 16);
	}

	public static String concatToHtml(Collection<? extends ToHtmlSupport> items, String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (ToHtmlSupport entity : items) {
			if (first) {
				first = false;
			} else {
				sb.append(separator);
			}
			sb.append(entity.toHtml());
		}
		return sb.toString();
	}

	@Deprecated
	public static String getSimpleName(Class type) {
		return Str.getSimpleName(type);
	}

	public static <T> List<T> toList(Enumeration<T> e) {
		if (e == null) return null;
		List<T> ret = new ArrayList<T>();
		while (e.hasMoreElements()) {
			ret.add(e.nextElement());
		}
		return ret;
	}

}
