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

import ilarkesto.base.Str;
import ilarkesto.base.Tm;

import java.util.Locale;

public final class TimePeriod implements Comparable<TimePeriod> {

	public static void main(String[] args) {
		System.out.println(new TimePeriod("03:0").toHoursAndMinutesString());
	}

	public static final TimePeriod ZERO = new TimePeriod(0);

	private long millis;

	public TimePeriod(long millis) {
		this.millis = Math.abs(millis);
	}

	public TimePeriod(String s) {
		if (s.indexOf(':') >= 0) {
			millis = 0;
			String[] sa = Str.tokenize(s, ":");
			if (sa.length != 2) throw new RuntimeException("Illegal TimePeriod: " + s);
			millis += Integer.parseInt(sa[0]) * Tm.HOUR;
			millis += Integer.parseInt(sa[1]) * Tm.MINUTE;
		} else {
			this.millis = Long.parseLong(s);
		}
	}

	public TimePeriod abs() {
		return millis < 0 ? new TimePeriod(-millis) : this;
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

	public int toWeeks() {
		return toDays() / 7;
	}

	public boolean isGreaterThen(TimePeriod other) {
		return millis > other.millis;
	}

	public boolean isLessThen(TimePeriod other) {
		return millis < other.millis;
	}

	public TimePeriod divide(int divisor) {
		return new TimePeriod(millis / divisor);
	}

	public TimePeriod add(TimePeriod tp) {
		return new TimePeriod(millis + tp.millis);
	}

	public TimePeriod subtract(TimePeriod tp) {
		return new TimePeriod(millis - tp.millis);
	}

	public TimePeriod subtract(Time time) {
		return new TimePeriod(millis - time.toMillis());
	}

	public TimePeriod multiplyBy(double factor) {
		return new TimePeriod(Math.round(millis * factor));
	}

	public String toHoursAndMinutesString() {
		long hours = toHours();
		long minutes = toMinutes() - (hours * 60);
		StringBuilder sb = new StringBuilder();
		sb.append(hours);
		sb.append(':');
		if (minutes < 10) sb.append('0');
		sb.append(minutes);
		return sb.toString();
	}

	public String toShortestString(Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (millis >= (Tm.WEEK * 2)) {
			int i = toWeeks();
			sb.append(i);
			sb.append(" Wo.");
		} else if (millis >= Tm.DAY) {
			int i = toDays();
			sb.append(i);
			sb.append(" Tag.");
		} else if (millis >= ((Tm.HOUR * 2) - (Tm.MINUTE - 20))) {
			long l = toHours();
			sb.append(l);
			sb.append(" Std.");
		} else if (millis >= Tm.MINUTE) {
			long l = toMinutes();
			sb.append(l);
			sb.append(" Min.");
		} else if (millis >= Tm.SECOND) {
			long l = toSeconds();
			sb.append(l);
			sb.append(" Sek.");
		} else {
			sb.append(millis);
			sb.append(" ms.");
		}
		return sb.toString();
	}

	// --- Comparable ---

	public int compareTo(TimePeriod o) {
		if (millis == o.millis) return 0;
		return millis > o.millis ? 1 : -1;
	}

	// --- Object ---

	private int hashCode;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 23 * 37 + (int) (millis ^ (millis >>> 32));
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TimePeriod)) return false;
		return millis == ((TimePeriod) obj).millis;
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
