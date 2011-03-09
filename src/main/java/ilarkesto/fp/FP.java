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

import ilarkesto.base.Tuple;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Static methods for functional programming.
 * 
 */
public abstract class FP {

	public static <I, K, V> Map<K, V> map(Collection<I> elements, Function<I, Tuple<K, V>> mapFunction) {
		Map<K, V> ret = new HashMap<K, V>();
		for (I e : elements) {
			Tuple<K, V> keyValue = mapFunction.eval(e);
			ret.put(keyValue.getA(), keyValue.getB());
		}
		return ret;
	}

	/**
	 * Executes a function on each element in a list. The function results are returned in a list.
	 * 
	 * @param <E> The element type.
	 * @param elements Elements to execute the function on.
	 * @param mapFunction The function to execute on each element.
	 * @return The function results.
	 */
	public static <I, O> List<O> foreach(Collection<I> elements, Function<I, O> mapFunction) {
		List<O> result = new ArrayList<O>(elements.size());
		for (I e : elements) {
			result.add(mapFunction.eval(e));
		}
		return result;
	}

	/**
	 * Executes a function on each element in a list.
	 * 
	 * @param <E> The element type.
	 * @param elements Elements to execute the function on.
	 * @param function The function to execute on each element.
	 */
	public static <E> void foreachVoid(Collection<E> elements, Function<E, ?> function) {
		for (E e : elements) {
			function.eval(e);
		}
	}

	/**
	 * Groups elements from a collection by a group function.
	 * 
	 * @param <G> The grouping object type. (Used as key in the result map).
	 * @param <E> The element type.
	 * @param elements The elements to group.
	 * @param groupFunction The function used to group elements
	 * @return
	 */
	public static <G, E> Map<G, List<E>> group(Collection<E> elements, Function<E, G> groupFunction) {
		Map<G, List<E>> result = new HashMap<G, List<E>>();
		for (E e : elements) {
			G key = groupFunction.eval(e);
			List<E> bucket = result.get(key);
			if (bucket == null) {
				bucket = new ArrayList<E>();
				result.put(key, bucket);
			}
			bucket.add(e);
		}
		return result;
	}

	public static <T> Predicate<T> and(Predicate<T>... predicates) {
		return new And<T>(predicates);
	}

	/**
	 * Filters a collection of elements by a predicate and returns a list.
	 * 
	 * @param <T> The type of the elements.
	 * @param predicate The filter predicate.
	 * @param list The collection of elements.
	 */
	public static <T> List<T> filterList(Predicate<T> predicate, Collection<T> list) {
		List<T> result = new ArrayList<T>();
		for (T element : list) {
			if (predicate.test(element)) result.add(element);
		}
		return result;
	}

	/**
	 * Filters a collection of elements by a predicate and returns a set.
	 * 
	 * @param <T> The type of the elements.
	 * @param predicate The filter predicate.
	 * @param list The collection of elements.
	 */
	public static <T> Set<T> filterSet(Predicate<T> predicate, Collection<T> list) {
		Set<T> result = new HashSet<T>();
		for (T element : list) {
			if (predicate.test(element)) result.add(element);
		}
		return result;
	}

	/**
	 * Gets specified values from a map. Returns a list with the values for which the keys are given.
	 * 
	 * @param <K> Type of the keys.
	 * @param <V> Type of the values.
	 * @param keys The keys for which to return the values.
	 * @param map The map with the values to return.
	 */
	public static <K, V> List<V> values(Collection<K> keys, Map<K, V> map) {
		List<V> result = new ArrayList<V>();
		for (K key : keys)
			result.add(map.get(key));
		return result;
	}

	// --- functions / predicates ---

	public static final Function<File, String> FILE_PATH = new Function<File, String>() {

		@Override
		public String eval(File e) {
			return e.getPath();
		}

	};

	public static List<String> filePaths(Collection<File> files) {
		return foreach(files, FILE_PATH);
	}

	public static final Predicate<File> FILE_EXISTS = new Predicate<File>() {

		@Override
		public boolean test(File e) {
			return e.exists();
		}

	};

	public static List<File> existingFilesList(Collection<File> files) {
		return filterList(FILE_EXISTS, files);
	}

	public static Set<File> existingFilesSet(Collection<File> files) {
		return filterSet(FILE_EXISTS, files);
	}

	public static List<File> filterFilesList(Collection<File> files, final FileFilter filter) {
		return FP.filterList(new Predicate<File>() {

			@Override
			public boolean test(File file) {
				return filter.accept(file);
			}

		}, files);
	}
}
