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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Utilitiy methods for dealing with date and time. Current month, year. Date comparsions.
 */
public final class Tm {

	private Tm() {}

	public static final long SECOND = 1000;

	public static final long MINUTE = SECOND * 60;

	public static final long HOUR = MINUTE * 60;

	public static final long DAY = HOUR * 24;

	public static final long WEEK = DAY * 7;

	public static final String[] MONTHS_DE = new String[] { "Januar", "Februar", "M\u00E4rz", "April", "Mai", "Juni",
			"Juli", "August", "September", "Oktober", "November", "Dezember" };

	public static final String[] WEEKDAYS = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday" };

	public static final String[] WEEKDAYS_DE = new String[] { "Sonntag", "Montag", "Dienstag", "Mittwoch",
			"Donnerstag", "Freitag", "Samstag" };

	public static final SimpleDateFormat DATE_DE = new SimpleDateFormat("dd.MM.yyyy");

	public static final SimpleDateFormat DATE_WITH_SHORT_WEEKDAY_DE = new SimpleDateFormat("EE, dd.MM.yyyy");

	public static final SimpleDateFormat DATE_LONG_DE = new SimpleDateFormat("dd. MMMM yyyy");

	public static final SimpleDateFormat DATE_VERY_LONG_DE = new SimpleDateFormat("EEEE, dd. MMMM yyyy");

	public static final SimpleDateFormat DATE_TIME_DE = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public static final SimpleDateFormat DATE_ISO = new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat DATE_TIME_ISO = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final SimpleDateFormat DATE_TIME_LOGFILE = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

	public static final SimpleDateFormat TIME_SHORT_DE = new SimpleDateFormat("HH:mm");

	public static final TimeZone TZ_BERLIN = TimeZone.getTimeZone("Europe/Berlin");

	public static boolean isSameDay(Date day1, Date day2) {
		return getDayBegin(day1).equals(getDayBegin(day2));
	}

	public static boolean isSameDayIgnoreYear(Date day1, Date day2) {
		GregorianCalendar cal = new GregorianCalendar();

		cal.setTime(day1);
		cal.set(GregorianCalendar.YEAR, 0);
		day1 = cal.getTime();

		cal.setTime(day2);
		cal.set(GregorianCalendar.YEAR, 0);
		day2 = cal.getTime();

		return getDayBegin(day1).equals(getDayBegin(day2));
	}

	public static Date getDayBegin(Date day) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		cal.set(GregorianCalendar.MINUTE, 0);
		cal.set(GregorianCalendar.SECOND, 0);
		cal.set(GregorianCalendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getDayEnd(Date day) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(day);
		cal.set(GregorianCalendar.HOUR_OF_DAY, 23);
		cal.set(GregorianCalendar.MINUTE, 59);
		cal.set(GregorianCalendar.SECOND, 59);
		cal.set(GregorianCalendar.MILLISECOND, 999);
		return cal.getTime();
	}

	public static String formatDateTimeShortDe(Date time) {
		StringBuffer sb = new StringBuffer();
		sb.append(DATE_DE.format(time));
		if (getMillisInDay(time) > 0) {
			sb.append(TIME_SHORT_DE.format(time));
		}
		return sb.toString();
	}

	public static long getMillisInDay(Date time) {
		return time.getTime() - getDayBegin(time).getTime();
	}

	public static int getCurrentYear() {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(GregorianCalendar.YEAR);
	}

	public static int getCurrentMonth() {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(GregorianCalendar.MONTH) + 1;
	}

	public static int year(int year) {
		if (year < (getCurrentYear() + 23 - 2000)) return year + 2000;
		if (year < 1000) return year + 1900;
		return year;
	}

	public static int countYearsSince(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		return getCurrentYear() - cal.get(GregorianCalendar.YEAR);
	}

}
