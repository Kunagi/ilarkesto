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

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Utility methods. Randomization, array and collection conversions.
 */
public class Utl extends ilarkesto.core.base.Utl {

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			System.out.println(randomInt(3, 5));
		}
	}

	public static File getFirstExistingFile(String... paths) {
		for (String path : paths) {
			File file = new File(path);
			if (file.exists()) return file;
		}
		return null;
	}

	public static <T> T getElement(Collection<T> collection, int index) {
		int i = 0;
		for (T t : collection) {
			if (i == index) return t;
			i++;
		}
		return null;
	}

	public static Color parseHtmlColor(String color) {
		return new Color(parseHex(color.substring(1)));
	}

	public static Throwable getRootCause(Throwable ex) {
		Throwable cause = ex.getCause();
		return cause == null ? ex : getRootCause(cause);
	}

	private static final Random random = new Random(System.currentTimeMillis());

	public static boolean equals(Set<?> objects) {
		Object first = null;
		for (Object o : objects) {
			if (first == null) {
				first = o;
			} else {
				if (!first.equals(o)) return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(String s) {
		if (s == null) return true;
		if (s.length() == 0) return true;
		if (s.trim().length() == 0) return true;
		return false;
	}

	public static <K, V> Map<K, V> subMap(Map<K, V> source, K... keys) {
		Map<K, V> ret = new HashMap<K, V>();
		for (K key : keys)
			ret.put(key, source.get(key));
		return ret;
	}

	public static String toStringWithType(Object o) {
		return o == null ? "?: null" : o.getClass().getSimpleName() + ": " + o;
	}

	public static String toString(Object o) {
		return o == null ? null : o.toString();
	}

	public static String randomElement(String... elements) {
		return elements[random.nextInt(elements.length)];
	}

	public static <T> T randomElement(List<T> elements) {
		return elements.get(random.nextInt(elements.size()));
	}

	public static boolean randomBoolean() {
		return random.nextBoolean();
	}

	public static int randomInt(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	public static char randomChar(String charSet) {
		int index = randomInt(0, charSet.length() - 1);
		return charSet.charAt(index);
	}

	public static File[] toFileArray(Collection<File> elements) {
		File[] ret = new File[elements.size()];
		System.arraycopy(elements.toArray(), 0, ret, 0, ret.length);
		return ret;
	}

	public static <E> Set<E> toSet(E... elements) {
		Set<E> ret = new HashSet<E>(elements.length);
		for (E element : elements) {
			ret.add(element);
		}
		return ret;
	}

	public static <T> List<T> toList(Enumeration<T> enumeration) {
		List<T> ret = new ArrayList<T>();
		while (enumeration.hasMoreElements()) {
			ret.add(enumeration.nextElement());
		}
		return ret;
	}

	public static <T> List<T> getHighest(Collection<T> collection, int count, Comparator<T> comparator) {
		// TODO performance optimization: sort not necessary
		List<T> list = sort(collection, comparator);
		List<T> result = new ArrayList<T>(count);
		for (int i = 0; i < count && i < list.size(); i++)
			result.add(list.get(i));
		return result;
	}

	public static <T extends Comparable> List<T> sort(Collection<T> collection) {
		List<T> result = new ArrayList<T>(collection);
		Collections.sort(result);
		return result;
	}

	public static <T> List<T> sort(Collection<T> collection, Comparator<T> comparator) {
		List<T> result = new ArrayList<T>(collection);
		Collections.sort(result, comparator);
		return result;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

}
