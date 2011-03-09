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

public class LoginData implements LoginDataProvider {

	private String login;
	private String password;
	private boolean savePassword;

	public LoginData(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public LoginData(String login, String password, boolean savePassword) {
		this(login, password);
		this.savePassword = savePassword;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public boolean isSavePassword() {
		return savePassword;
	}

	@Override
	public LoginData getLoginData() {
		return this;
	}

	@Override
	public String toString() {
		return login + ":" + (password == null ? "null" : "*****") + ":" + savePassword;
	}
}
