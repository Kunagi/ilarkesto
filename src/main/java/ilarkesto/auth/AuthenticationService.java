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
package ilarkesto.auth;

public class AuthenticationService<U extends AUser> {

	public U getUserByLoginAndPassword(String userName, String password) {
		if (userName == null) return null;
		if (password == null) return null;
		for (U user : userDao.getEntities()) {
			if (user.getName().equalsIgnoreCase(userName) && user.matchesPassword(password)) { return user; }
		}
		return null;
	}

	// --- dependencies ---

	private AUserDao<U> userDao;

	public void setUserDao(AUserDao<U> userDao) {
		this.userDao = userDao;
	}

}
