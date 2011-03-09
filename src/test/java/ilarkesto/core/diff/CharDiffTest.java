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
package ilarkesto.core.diff;

import ilarkesto.core.diff.CharDiff;
import ilarkesto.core.diff.TxtDiffMarker;
import ilarkesto.testng.ATest;

import org.testng.annotations.Test;

public class CharDiffTest extends ATest {

	@Test
	public void same() {
		assertDiff("a", "a", "a");
		assertDiff("hello world", "hello world", "hello world");
	}

	@Test
	public void addedAtEnd() {
		assertDiff("a", "ab", "a[+b]");
		assertDiff("hello", "hello world", "hello[+ world]");
	}

	@Test
	public void removedAtEnd() {
		assertDiff("ab", "a", "a[-b]");
		assertDiff("hello world", "hello", "hello[- world]");
	}

	@Test
	public void removedAtBeginning() {
		assertDiff("ab", "b", "[-a]b");
		assertDiff("hello world", "world", "[-hello ]world");
	}

	@Test
	public void addedAtMiddle() {
		assertDiff("ac", "abc", "a[+b]c");
		assertDiff("hello world", "hello happy world", "hello [+happy ]world");
	}

	@Test
	public void addedFromNothing() {
		assertDiff(null, "a", "[+a]");
		assertDiff(null, "hello world", "[+hello world]");
	}

	@Test
	public void removedToNothing() {
		assertDiff("a", null, "[-a]");
		assertDiff("hello world", null, "[-hello world]");
	}

	private static void assertDiff(String left, String right, String expectedDiff) {
		CharDiff diff = new CharDiff(left, right, new TxtDiffMarker());
		diff.diff();
		assertEquals(diff.toString(), expectedDiff);
	}

}
