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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Date implements Comparable<Date>, Serializable, IsSerializable {

	private static final int DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

	private int year;
	private int month;
	private int day;

	Date() {
		this(new java.util.Date());
	}

	public Date(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public Date(String date) {
		if (date.length() != 10) throw new RuntimeException("Illegal date format: " + date);

		int y = Integer.parseInt(date.substring(0, 4));
		int m = Integer.parseInt(date.substring(5, 7));
		int d = Integer.parseInt(date.substring(8, 10));

		this.year = y;
		this.month = m;
		this.day = d;
	}

	public Date(java.util.Date javaDate) {
		this.year = javaDate.getYear() + 1900;
		this.month = javaDate.getMonth() + 1;
		this.day = javaDate.getDate();
	}

	public static List<Date> getDaysInMonth(int year, int month) {
		List<Date> dates = new ArrayList<Date>();
		Date d = new Date(year, month, 1);

		while (d.getMonth() == month) {
			dates.add(d);
			d = d.nextDay();
		}

		return dates;
	}

	public static List<Date> getDaysOverMonth(int year, int month) {
		List<Date> dates = new ArrayList<Date>();
		Date d = new Date(year, month, 1);

		int days = getDaysInMonth(year, month).size();
		if (d.getWeekday() != 1) {
			// from monday till first day of month
			while (d.getWeekday() != 1) {
				d = d.prevDay();
				days++;
			}
		}

		for (int i = 0; i < days; i++) {
			dates.add(d);
			d = d.nextDay();
		}

		if (d.getWeekday() != 0) {
			// from last day of month till sunday
			while (d.getWeekday() != 0) {
				dates.add(d);
				d = d.nextDay();
			}
			dates.add(d);
		}

		return dates;

	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public String getWeekdayLabel() {
		return Gwt.formatWeekdayShort(toJavaDate());
	}

	public int getWeekday() {
		return toJavaDate().getDay();
	}

	public int getWeek() {
		java.util.Date jFirstJan = new Date(year, 1, 1).toJavaDate();
		int firstMonday = jFirstJan.getDay() < 1 ? 2 : (jFirstJan.getDay() > 1 ? 9 - jFirstJan.getDay() : 1);
		TimePeriod firstMondayTillNow = new Date(year, 1, firstMonday).getPeriodTo(this);

		int weeks = -1;
		if (firstMonday == 1) {
			weeks = firstMondayTillNow.toWeeks() + 1;
		} else {
			java.util.Date jFirstMondayDate = new Date(year, 1, firstMonday).toJavaDate();
			java.util.Date jThis = toJavaDate();
			if (jThis.before(jFirstMondayDate)) {
				weeks = 1;
			} else if (jThis.after(jFirstMondayDate)) {
				weeks = firstMondayTillNow.toWeeks() + (firstMondayTillNow.toDays() % 7 >= 0 ? 2 : 1);
			} else {
				weeks = 2;
			}
		}

		return weeks;
	}

	@SuppressWarnings("deprecation")
	// GWT
	public Date addDays(int days) {
		java.util.Date javaDate = toJavaDate();
		javaDate.setDate(javaDate.getDate() + days);
		return new Date(javaDate);
	}

	public Date prevDay() {
		return addDays(-1);
	}

	public Date nextDay() {
		return addDays(1);
	}

	public boolean isBetween(Date begin, Date end, boolean includingBoundaries) {
		if (includingBoundaries) {
			return isSameOrAfter(begin) && isSameOrBefore(end);
		} else {
			return isAfter(begin) && isBefore(end);
		}
	}

	public boolean isSameOrAfter(Date other) {
		return compareTo(other) >= 0;
	}

	public boolean isAfter(Date other) {
		return compareTo(other) > 0;
	}

	public boolean isSameOrBefore(Date other) {
		return compareTo(other) <= 0;
	}

	public boolean isBefore(Date other) {
		return compareTo(other) < 0;
	}

	public java.util.Date toJavaDate() {
		return new java.util.Date(year - 1900, month - 1, day);
	}

	public long toMillis() {
		return toJavaDate().getTime();
	}

	public TimePeriod getPeriodTo(Date other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public static Date today() {
		return new Date(new java.util.Date());
	}

	public boolean isToday() {
		return equals(today());
	}

	@Override
	public int compareTo(Date o) {
		if (year > o.year) return 1;
		if (year < o.year) return -1;
		if (month > o.month) return 1;
		if (month < o.month) return -1;
		if (day > o.day) return 1;
		if (day < o.day) return -1;
		return 0;
	}

	private transient int hashCode;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + year;
			hashCode = hashCode * 37 + month;
			hashCode = hashCode * 37 + day;
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Date)) return false;
		Date other = (Date) obj;
		return day == other.day && month == other.month && year == other.year;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append("-");
		if (month < 10) sb.append('0');
		sb.append(month);
		sb.append("-");
		if (day < 10) sb.append('0');
		sb.append(day);
		return sb.toString();
	}

}
