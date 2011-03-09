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
package ilarkesto.gwt.client;

import java.util.GregorianCalendar;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DateTest extends Assert {

	@Test
	public void addDays() {
		Date date = new Date(2010, 1, 1);

		for (int i = -10000; i < 10000; i++) {
			assertAddDays(date, i);
		}
	}

	private void assertAddDays(Date begin, int days) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(begin.toMillis());
		assertEquals(begin, new Date(calendar.getTime()));

		calendar.add(GregorianCalendar.DAY_OF_YEAR, days);

		assertEquals(begin.addDays(days), new Date(calendar.getTime()));
	}

}
