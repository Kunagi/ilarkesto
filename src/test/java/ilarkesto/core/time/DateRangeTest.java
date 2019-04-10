/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.core.time;

import ilarkesto.core.base.Utl;
import ilarkesto.testng.ATest;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class DateRangeTest extends ATest {

	private static final DateRange range2014 = new DateRange("2014-01-01 - 2014-12-31");
	private static final DateRange range10 = new DateRange("2014-01-11 - 2014-01-20");

	private static final DateRange january = new DateRange("2015-01-01 - 2015-01-31");
	private static final DateRange march = new DateRange("2015-03-01 - 2015-03-31");
	private static final DateRange april = new DateRange("2015-04-01 - 2015-04-30");
	private static final DateRange lateAprilEarlyMay = new DateRange("2015-04-29 - 2015-05-02");

	@Test
	public void compare() {
		DateRange fullYear = new DateRange("2019-01-01 - 2019-12-31");
		DateRange jan = new DateRange("2019-01-01 - 2019-01-31");
		DateRange firstQuater = new DateRange("2019-01-01 - 2019-03-31");
		DateRange firstHalf = new DateRange("2019-01-01 - 2019-06-31");

		assertEquals(jan.compareTo(fullYear), -1);
		assertEquals(jan.compareTo(firstQuater), -1);
		assertEquals(jan.compareTo(firstHalf), -1);
		assertEquals(firstQuater.compareTo(firstHalf), -1);

		List<DateRange> sorted = Utl.sort(Arrays.asList(fullYear, jan, firstQuater, firstHalf));
		assertEquals(sorted, Arrays.asList(jan, firstQuater, firstHalf, fullYear));
	}

	@Test
	public void splitIntoYears() {
		assertEquals(new DateRange("2015-01-01 - 2015-12-31").splitIntoYears(),
			Utl.toList(new DateRange("2015-01-01 - 2015-12-31")));
		assertEquals(new DateRange("2015-01-05 - 2017-12-25").splitIntoYears(),
			Utl.toList(new DateRange("2015-01-05 - 2015-12-31"), new DateRange("2016-01-01 - 2016-12-31"),
				new DateRange("2017-01-01 - 2017-12-25")));
	}

	@Test
	public void getYearWithMostDaysOrLatest() {
		assertEquals(new DateRange("2015-01-01 - 2015-12-31").getYearWithMostDaysOrLatest(), 2015);
		assertEquals(new DateRange("2015-01-01 - 2016-06-31").getYearWithMostDaysOrLatest(), 2015);
		assertEquals(new DateRange("2015-06-01 - 2016-12-31").getYearWithMostDaysOrLatest(), 2016);
		assertEquals(new DateRange("2015-12-30 - 2016-01-01").getYearWithMostDaysOrLatest(), 2015);
		assertEquals(new DateRange("2015-12-31 - 2016-01-01").getYearWithMostDaysOrLatest(), 2016);
		assertEquals(new DateRange("2015-06-01 - 2017-06-01").getYearWithMostDaysOrLatest(), 2016);
	}

	@Test
	public void mergeOverlappingAndAdjacent() {
		assertTrue(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(january)).contains(january));
		assertTrue(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(january)).size() == 1);

		assertTrue(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(january, march)).contains(january));
		assertTrue(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(january, march)).contains(march));
		assertFalse(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(january, march)).contains(april));
		assertTrue(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(january, march)).size() == 2);

		assertTrue(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(april, january, lateAprilEarlyMay, march))
				.contains(january));
		assertFalse(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(april, january, lateAprilEarlyMay, march))
				.contains(april));
		assertFalse(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(april, january, lateAprilEarlyMay, march))
				.contains(march));
		assertTrue(DateRange.mergeOverlappingAndAdjacent(Arrays.asList(april, january, lateAprilEarlyMay, march))
				.contains(new DateRange("2015-03-01 - 2015-05-02")));
		assertTrue(
			DateRange.mergeOverlappingAndAdjacent(Arrays.asList(april, january, lateAprilEarlyMay, march)).size() == 2);

	}

	@Test
	public void containsAll() {
		assertTrue(range2014.containsAll(range2014));
		assertFalse(range10.containsAll(new DateRange("2014-01-10 - 2014-01-15")));
		assertFalse(range10.containsAll(new DateRange("2014-01-11 - 2014-01-21")));
		assertFalse(range10.containsAll(new DateRange("2014-01-01 - 2014-01-10")));
		assertFalse(range10.containsAll(new DateRange("2014-01-21 - 2014-01-31")));
	}

	@Test
	public void containsAny() {
		assertFalse(range2014.containsAny(new DateRange("2013-01-01 - 2013-12-31")));
		assertTrue(range2014.containsAny(range2014));
		assertTrue(range2014.containsAny(range10));
		assertTrue(range2014.containsAny(new DateRange("2014-12-31 - 2015-12-31")));
		assertFalse(range2014.containsAny(new DateRange("2015-01-01 - 2015-12-31")));
	}

	@Test
	public void getOverlappingDays() {
		assertEquals(range2014.getOverlappingDays(new DateRange("2013-01-01 - 2013-12-31")), 0);
		assertEquals(range2014.getOverlappingDays(new DateRange("2015-01-01 - 2015-12-31")), 0);
		assertEquals(range2014.getOverlappingDays(new DateRange("2013-01-01 - 2014-01-01")), 1);
		assertEquals(range2014.getOverlappingDays(new DateRange("2014-12-31 - 2015-12-31")), 1);
		assertEquals(range2014.getOverlappingDays(new DateRange("2014-12-31 - 2014-12-31")), 1);
		assertEquals(range2014.getOverlappingDays(new DateRange("2014-06-01 - 2014-06-31")), 31);

		assertEquals(range10.getOverlappingDays(range10), 10);
	}

	@Test
	public void getOverlappingDaysAsPartial() {
		assertEquals(range10.getOverlappingDaysAsPartial(new DateRange("2014-01-01 - 2014-01-10")), 0, 0);
		assertEquals(range10.getOverlappingDaysAsPartial(new DateRange("2014-01-21 - 2014-01-31")), 0, 0);
		assertEquals(range10.getOverlappingDaysAsPartial(new DateRange("2014-01-01 - 2014-01-11")), 0.1, 0);
		assertEquals(range10.getOverlappingDaysAsPartial(new DateRange("2014-01-20 - 2014-12-31")), 0.1, 0);
		assertEquals(range10.getOverlappingDaysAsPartial(new DateRange("2014-01-11 - 2014-01-20")), 1, 0);
		assertEquals(range10.getOverlappingDaysAsPartial(new DateRange("2014-01-12 - 2014-01-19")), 0.8, 0);
	}

	@Test
	public void contains() {
		assertFalse(range2014.contains(new Date("2013-12-31")));
		assertTrue(range2014.contains(new Date("2014-01-01")));
		assertTrue(range2014.contains(new Date("2014-12-31")));
		assertFalse(range2014.contains(new Date("2015-01-01")));
	}

	@Test
	public void getDayCount() {
		assertEquals(new DateRange("2014-01-01 - 2014-01-01").getDayCount(), 1);
		assertEquals(new DateRange("2014-01-01 - 2014-01-02").getDayCount(), 2);
	}

	@Test
	public void getTimePeriodBetweenStartAndEnd() {
		assertEquals(new DateRange("2014-01-01 - 2014-01-01").getTimePeriodBetweenStartAndEnd(), new TimePeriod(0));
		assertEquals(new DateRange("2014-01-01 - 2014-01-02").getTimePeriodBetweenStartAndEnd(),
			new TimePeriod(Tm.HOUR * 24));
	}

	@Test
	public void constructionAndToString() {
		assertEquals(new DateRange(new Date(2014, 1, 1), new Date(2015, 1, 1)).toString(),
			new DateRange("2014-01-01 - 2015-01-01").toString());
	}

	@Test
	public void checkFail() {
		assertCheckFail("2000-01-02 - 2000-01-01");
		assertCheckFail("2000-01-01");
	}

	@Test
	public void checkOk() {
		new DateRange("2000-01-01 - 2000-01-01");
	}

	private void assertCheckFail(String s) {
		try {
			new DateRange(s);
			fail("Exception expected: " + s);
		} catch (IllegalArgumentException ex) {
			// expected
		}
	}

}
