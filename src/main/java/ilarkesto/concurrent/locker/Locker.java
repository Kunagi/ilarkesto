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

import ilarkesto.base.Str;
import ilarkesto.base.time.TimePeriod;

import java.util.HashMap;
import java.util.Map;

public class Locker {

	private Map<Object, Lock> locks = new HashMap<Object, Lock>();

	public void lock(Object object, Object locker, boolean allowRelockBySameLocker, TimePeriod lockTime)
			throws LockingException {
		if (object == null) throw new IllegalArgumentException("object == null");
		synchronized (locks) {
			Lock lock = locks.get(object);
			if (lock != null) {
				if (lock.isTimedOut()) {
					locks.remove(object);
				} else {
					if (allowRelockBySameLocker && lock.getLocker() == locker) {
						// locked by locker
						return;
					}
					throw new LockingException(lock);
				}
			}
			lock = new Lock(object, locker, lockTime);
			locks.put(object, lock);
		}
	}

	public void unlock(Object object) {
		synchronized (locks) {
			locks.remove(object);
		}
	}

	@Override
	public String toString() {
		synchronized (locks) {
			return Str.format(locks.values());
		}
	}

}
