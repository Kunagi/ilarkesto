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
package ilarkesto.concurrent.locker;

import ilarkesto.base.time.DateAndTime;
import ilarkesto.base.time.TimePeriod;

public class Lock {

	private Object object;
	private Object locker;
	private DateAndTime time;
	private TimePeriod maxLockTime;

	Lock(Object object, Object locker, TimePeriod maxLockTime) {
		this.object = object;
		this.locker = locker;
		this.maxLockTime = maxLockTime;

		this.time = DateAndTime.now();
	}

	public Object getObject() {
		return object;
	}

	public Object getLocker() {
		return locker;
	}

	public DateAndTime getTime() {
		return time;
	}

	public boolean isTimedOut() {
		return time.getPeriodToNow().isGreaterThen(maxLockTime);
	}

	@Override
	public String toString() {
		return object + " locked by " + locker + " since " + time;
	}

}
