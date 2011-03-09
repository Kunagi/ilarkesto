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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public final class DateAndTime implements Comparable<DateAndTime> {

	public static final transient SimpleDateFormat FORMAT_WEEKDAY_DAY_LONGMONTH_YEAR_HOUR_MINUTE = new SimpleDateFormat(
			"EEE, dd. MMMM yyyy, HH:mm");
	public static final transient SimpleDateFormat FORMAT_WEEKDAY_LONGMONTH_DAY_YEAR_HOUR_MINUTE = new SimpleDateFormat(
			"EEE, MMM d, yyyy, HH:mm");
	public static final transient SimpleDateFormat FORMAT_DAY_MONTH_YEAR_HOUR_MINUTE = new SimpleDateFormat(
			"dd.MM.yyyy, HH:mm");
	public static final transient SimpleDateFormat FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static final transient SimpleDateFormat FORMAT_LOG = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	public static final transient SimpleDateFormat FORMAT_RFC822 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",
			Locale.ENGLISH);

	private Date date;

	private Time time;

	public DateAndTime(java.util.Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		this.date = new Date(calendar);
		this.time = new Time(calendar);
	}

	public DateAndTime(long millis) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(millis);
		this.date = new Date(calendar);
		this.time = new Time(calendar);
	}

	public DateAndTime(GregorianCalendar calendar) {
		this(new Date(calendar), new Time(calendar));
	}

	public DateAndTime(Date date, Time time) {
		this.date = date;
		this.time = time;
	}

	public DateAndTime(int year, int month, int day, int hour, int minute, int second) {
		this(new Date(year, month, day), new Time(hour, minute, second));
	}

	public DateAndTime(String s) {
		StringTokenizer tok = new StringTokenizer(s, " ");
		if (!tok.hasMoreElements()) throw new RuntimeException("Invalid DateAndTime format: " + s);
		date = new Date(tok.nextToken());
		if (tok.hasMoreElements()) {
			time = new Time(tok.nextToken());
		} else {
			time = new Time(0, 0);
		}
	}

	/**
	 * Assume instance as in given time zone and convert to UTC.
	 */
	public DateAndTime toUtc(TimeZone timeZone) {
		long millis = toMillis();
		int offset = timeZone.getOffset(millis);
		return new DateAndTime(millis - offset);
	}

	/**
	 * Assume instance as in system default time zone and convert to UTC.
	 */
	public DateAndTime toUtc() {
		return toUtc(TimeZone.getDefault());
	}

	/**
	 * Assume instance as in UTC and convert to given time zone.
	 */
	public DateAndTime toTimezone(TimeZone timeZone) {
		long millis = toMillis();
		int offset = timeZone.getOffset(millis);
		return new DateAndTime(millis + offset);
	}

	public DateAndTime addDays(int days) {
		GregorianCalendar gc = toGregorianCalendar();
		gc.add(GregorianCalendar.DAY_OF_YEAR, days);
		return new DateAndTime(gc);
	}

	public DateAndTime addHours(int hours) {
		GregorianCalendar gc = toGregorianCalendar();
		gc.add(GregorianCalendar.HOUR_OF_DAY, hours);
		return new DateAndTime(gc);
	}

	public DateAndTime addMinutes(int minutes) {
		GregorianCalendar gc = toGregorianCalendar();
		gc.add(GregorianCalendar.MINUTE, minutes);
		return new DateAndTime(gc);
	}

	public Date getDate() {
		return date;
	}

	public Time getTime() {
		return time;
	}

	public GregorianCalendar toGregorianCalendar() {
		GregorianCalendar gc = (GregorianCalendar) date.getGregorianCalendar().clone();
		gc.add(GregorianCalendar.MILLISECOND, (int) time.toMillis());
		return gc;
	}

	public java.util.Date toJavaDate() {
		return toGregorianCalendar().getTime();
	}

	public long toMillis() {
		return date.toMillis() + time.toMillis();
	}

	public TimePeriod getPeriodTo(DateAndTime other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public TimePeriod getPeriodToNow() {
		return getPeriodTo(now());
	}

	public String toString(DateFormat format) {
		return format.format(toJavaDate());
	}

	public String toString(Locale locale) {
		StringBuilder sb = new StringBuilder();
		sb.append(date.toString(locale));
		sb.append(", ");
		sb.append(time.toString(locale));
		return sb.toString();
	}

	public boolean isBefore(DateAndTime other) {
		return compareTo(other) < 0;
	}

	public boolean isBeforeOrSame(DateAndTime other) {
		return compareTo(other) <= 0;
	}

	public boolean isAfter(DateAndTime other) {
		return compareTo(other) > 0;
	}

	public boolean isAfterOrSame(DateAndTime other) {
		return compareTo(other) >= 0;
	}

	public boolean isFuture() {
		return isAfter(now());
	}

	// --- static ---

	public static DateAndTime now() {
		GregorianCalendar gc = new GregorianCalendar();
		return new DateAndTime(gc.get(GregorianCalendar.YEAR), gc.get(GregorianCalendar.MONTH) + 1,
				gc.get(GregorianCalendar.DAY_OF_MONTH), gc.get(GregorianCalendar.HOUR_OF_DAY),
				gc.get(GregorianCalendar.MINUTE), gc.get(GregorianCalendar.SECOND));
	}

	public static DateAndTime parse(String s, DateFormat... formats) throws ParseException {
		ParseException ex = null;
		for (DateFormat format : formats) {
			try {
				return new DateAndTime(format.parse(s));
			} catch (ParseException e) {
				ex = e;
			}
		}
		throw ex;
	}

	// --- object ---

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(date.toString());
		sb.append(" ");
		sb.append(time.toString());
		return sb.toString();
	}

	private transient int hashCode;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + date.hashCode();
			hashCode = hashCode * 37 + time.hashCode();
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return date.equals(((DateAndTime) obj).date) && time.equals(((DateAndTime) obj).time);
	}

	@Override
	public int compareTo(DateAndTime o) {
		int i = date.compareTo(o.date);
		if (i == 0) { return time.compareTo(o.time); }
		return i;
	}

}
