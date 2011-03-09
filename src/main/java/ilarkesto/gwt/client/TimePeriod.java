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

import com.google.gwt.user.client.rpc.IsSerializable;

public class TimePeriod implements Comparable<TimePeriod>, Serializable, IsSerializable {

	public static final transient long SECOND = 1000;
	public static final transient long MINUTE = SECOND * 60;
	public static final transient long HOUR = MINUTE * 60;
	public static final transient long DAY = HOUR * 24;
	public static final transient long WEEK = DAY * 7;
	public static final transient long MONTH = DAY * 30;
	public static final transient long YEAR = DAY * 360;

	private long millis;

	TimePeriod() {
		this(0);
	}

	public TimePeriod(long millis) {
		this.millis = millis;
	}

	public TimePeriod(String s) {
		this.millis = Long.parseLong(s);
	}

	public TimePeriod subtract(TimePeriod difference) {
		return new TimePeriod(millis - difference.millis);
	}

	public TimePeriod add(TimePeriod summand) {
		return new TimePeriod(millis + summand.millis);
	}

	public TimePeriod addDays(int days) {
		return new TimePeriod(millis + DAY);
	}

	public TimePeriod multiplyBy(int factor) {
		return new TimePeriod(millis * factor);
	}

	public TimePeriod abs() {
		return millis < 0 ? new TimePeriod(-millis) : this;
	}

	public TimePeriod getPeriodTo(Time other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public String toShortestString() {
		StringBuilder sb = new StringBuilder();
		long m = millis >= 0 ? millis : -millis;
		if (m >= (MONTH * 2)) {
			int i = toMonths();
			sb.append(i);
			sb.append(i == 1 || i == -1 ? " month" : " months");
		} else if (m >= (WEEK * 2)) {
			int i = toWeeks();
			sb.append(i);
			sb.append(i == 1 || i == -1 ? " week" : " weeks");
		} else if (m >= DAY) {
			int i = toDays();
			sb.append(i);
			sb.append(i == 1 || i == -1 ? " day" : " days");
		} else if (m >= ((HOUR * 2) - (MINUTE - 20))) {
			long l = toHours();
			sb.append(l);
			sb.append(l == 1 || l == -1 ? " hour" : " hours");
		} else if (m >= MINUTE) {
			long l = toMinutes();
			sb.append(l);
			sb.append(l == 1 || l == -1 ? " minute" : " minutes");
		} else if (m >= SECOND) {
			long l = toSeconds();
			sb.append(l);
			sb.append(l == 1 || l == -1 ? " second" : " seconds");
		} else {
			sb.append(m);
			sb.append(" millis");
		}
		return sb.toString();
	}

	public long toMillis() {
		return millis;
	}

	public long toSeconds() {
		return millis / 1000;
	}

	public long toMinutes() {
		return toSeconds() / 60;
	}

	public long toHours() {
		return toMinutes() / 60;
	}

	public int toDays() {
		return (int) (toHours() / 24);
	}

	public boolean isNegative() {
		return millis < 0;
	}

	public boolean isPositive() {
		return millis > 0;
	}

	public int toWeeks() {
		return toDays() / 7;
	}

	public int toMonths() {
		return toDays() / 30;
	}

	public int toYears() {
		return toDays() / 360;
	}

	public String toHoursAndMinutes() {
		long hours = toHours();
		long remainingMillis = millis - (hours * 3600000);
		long minutes = remainingMillis / 60000;
		return hours + (minutes > 9 ? ":" : ":0") + minutes;
	}

	@Override
	public int compareTo(TimePeriod o) {
		if (millis == o.millis) return 0;
		return millis > o.millis ? 1 : -1;
	}

	@Override
	public int hashCode() {
		return (int) millis;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof TimePeriod)) return false;
		return ((TimePeriod) obj).millis == millis;
	}

	@Override
	public String toString() {
		return String.valueOf(millis);
	}

	public static TimePeriod seconds(int seconds) {
		return new TimePeriod(seconds * 1000);
	}

	public static TimePeriod minutes(int minutes) {
		return seconds(minutes * 60);
	}

	public static TimePeriod hours(int hours) {
		return minutes(hours * 60);
	}

	public static TimePeriod days(int days) {
		return hours(days * 24);
	}

	public static TimePeriod weeks(int weeks) {
		return days(weeks * 7);
	}

}
