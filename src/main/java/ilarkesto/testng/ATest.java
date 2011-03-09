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
package ilarkesto.testng;

import java.util.Collection;

import org.testng.Assert;

public class ATest extends Assert {

	public static final String OUTPUT_DIR = "test-output";

	public static <T> void assertContains(String string, String substring) {
		assertTrue(string.contains(substring), "<" + string + "> expected to contain <" + substring + ">");
	}

	public static <T> void assertContains(Collection<T> collection, T element) {
		assertTrue(collection.contains(element), "Collection expected to contain <" + element + ">");
	}

	public static void assertNotEmpty(Collection collection) {
		assertFalse(collection.isEmpty(), "Collection expected to be not empty, but it is");
	}

	public static void assertStartsWith(String actual, String expectedPrefix) {
		assertTrue(actual.startsWith(expectedPrefix), "<" + actual + "> expected to start with <" + expectedPrefix
				+ "> |");
	}

}
