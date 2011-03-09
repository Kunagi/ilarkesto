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
package ilarkesto.base.time;

import ilarkesto.base.Tm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Time implements Comparable {

	public static final transient SimpleDateFormat FORMAT_HOUR_MINUTE_SECOND = new SimpleDateFormat("HH:mm:ss");

	public static final transient SimpleDateFormat FORMAT_HOUR_MINUTE_SECOND_NOSEP = new SimpleDateFormat("HHmmss");

	public static final transient SimpleDateFormat FORMAT_HOUR_MINUTE = new SimpleDateFormat("HH:mm");

	private int hour;

	private int minute;

	private int second;

	public Time(int hour, int minute) {
		this(hour, minute, 0);
	}

	public Time(int hour, int minute, int second) {
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}

	public Time(String timeString) {
		int idx = timeString.indexOf(':');
		if (idx >= 0) {
			hour = Integer.parseInt(timeString.substring(0, idx));
			timeString = timeString.substring(idx + 1);
		} else {
			hour = Integer.parseInt(timeString);
			return;
		}
		idx = timeString.indexOf(':');
		if (idx >= 0) {
			minute = Integer.parseInt(timeString.substring(0, idx));
			timeString = timeString.substring(idx + 1);
		} else {
			minute = Integer.parseInt(timeString);
			return;
		}
		second = Integer.parseInt(timeString);
	}

	public Time(GregorianCalendar calendar) {
		this(calendar.get(GregorianCalendar.HOUR_OF_DAY), calendar.get(GregorianCalendar.MINUTE), calendar
				.get(GregorianCalendar.SECOND));
	}

	public long toMillis() {
		return (second * Tm.SECOND) + (minute * Tm.MINUTE) + (hour * Tm.HOUR);
	}

	public int getHour() {
		return hour;
	}

	public int getMinute() {
		return minute;
	}

	public int getSecond() {
		return second;
	}

	public boolean isBefore(Time other) {
		return compareTo(other) < 0;
	}

	public boolean isBeforeOrSame(Time other) {
		return compareTo(other) <= 0;
	}

	public boolean isAfter(Time other) {
		return compareTo(other) > 0;
	}

	public boolean isAfterOrSame(Time other) {
		return compareTo(other) >= 0;
	}

	public TimePeriod getPeriodTo(Time other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	// --- Comparable ---

	public final int compareTo(Object o) {
		Time other = (Time) o;
		if (hour > other.hour) return 1;
		if (hour < other.hour) return -1;
		if (minute > other.minute) return 1;
		if (minute < other.minute) return -1;
		if (second > other.second) return 1;
		if (second < other.second) return -1;
		return 0;
	}

	public final java.util.Date toJavaDate(Date day) {
		return new java.util.Date(day.toMillis() + toMillis());
	}

	public final String toString(DateFormat format) {
		return format.format(toJavaDate(Date.today()));
	}

	public final String toString(Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (hour < 10) sb.append("0");
		sb.append(hour);
		sb.append(":");
		if (minute < 10) sb.append("0");
		sb.append(minute);
		return sb.toString();
	}

	// --- Object ---

	private transient int hashCode;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + hour;
			hashCode = hashCode * 37 + minute;
			hashCode = hashCode * 37 + second;
		}
		return hashCode;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) return false;
		Time other = (Time) obj;
		return hour == other.hour && minute == other.minute && second == other.second;
	}

	@Override
	public final String toString() {
		// TODO cache string
		StringBuilder sb = new StringBuilder();
		if (hour < 10) sb.append("0");
		sb.append(hour);
		sb.append(":");
		if (minute < 10) sb.append("0");
		sb.append(minute);
		if (second > 0) {
			sb.append(":");
			if (second < 10) sb.append("0");
			sb.append(second);
		}
		return sb.toString();
	}

	// --- static ---

	public static String toStringBeginEnd(Time begin, Time end) {
		if (begin == null && end == null) return null;
		StringBuilder sb = new StringBuilder();
		if (begin != null) {
			sb.append(begin);
		}
		if (end != null) {
			sb.append("-");
			sb.append(end);
		}
		return sb.toString();
	}

	public static Time now() {
		GregorianCalendar gc = new GregorianCalendar();
		return new Time(gc.get(GregorianCalendar.HOUR_OF_DAY), gc.get(GregorianCalendar.MINUTE), gc
				.get(GregorianCalendar.SECOND));
	}

}
