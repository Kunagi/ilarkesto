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

public class DateAndTime implements Comparable<DateAndTime>, Serializable, IsSerializable {

	private Date date;
	private Time time;

	public DateAndTime() {
		this(new Date(), new Time());
	}

	public DateAndTime(String s) {
		assert s != null;
		s = s.trim();
		int idx = s.indexOf(' ');

		if (idx > 0) {
			String sDate = s.substring(0, idx);
			String sTime = s.substring(idx + 1);
			date = new Date(sDate);
			time = new Time(sTime);
		} else {
			if (s.indexOf('.') > 0) {
				date = new Date(s);
				time = new Time("0");
			} else {
				date = Date.today();
				time = new Time(s);
			}
		}

	}

	public DateAndTime(Date date, Time time) {
		assert date != null && time != null;
		this.date = date;
		this.time = time;
	}

	public java.util.Date toJavaDate() {
		return new java.util.Date(date.toJavaDate().getTime() + time.toMillis());
	}

	public long toMillis() {
		return date.toMillis() + time.toMillis();
	}

	public static DateAndTime now() {
		return new DateAndTime(Date.today(), Time.now());
	}

	public Date getDate() {
		return date;
	}

	public Time getTime() {
		return time;
	}

	public TimePeriod getPeriodTo(DateAndTime other) {
		return new TimePeriod(other.toMillis() - toMillis());
	}

	public TimePeriod getPeriodToNow() {
		return getPeriodTo(now());
	}

	public TimePeriod getPeriodFromNow() {
		return now().getPeriodTo(this);
	}

	public boolean isBefore(DateAndTime other) {
		return compareTo(other) < 0;
	}

	public boolean isAfter(DateAndTime other) {
		return compareTo(other) > 0;
	}

	public int compareTo(DateAndTime o) {
		int i = date.compareTo(o.date);
		if (i == 0) { return time.compareTo(o.time); }
		return i;
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
	public String toString() {
		if (date != null && time != null) {
			return date + " " + time;
		} else if (date != null) {
			return date.toString();
		} else if (time != null) { return time.toString(); }
		return "";
	}

}
