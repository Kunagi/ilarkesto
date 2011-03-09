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
package ilarkesto.ui.swing;

import ilarkesto.auth.LoginData;
import ilarkesto.di.app.AApplicationConfig;
import ilarkesto.properties.APropertiesStore;

public class ASwingApplicationConfig extends AApplicationConfig {

	public ASwingApplicationConfig(APropertiesStore p) {
		super(p);
	}

	public final LoginData getLoginData(String id) {
		String login = p.get("login." + id + ".login");
		String password = p.getCrypted("login." + id + ".password");
		if (login == null && password == null) return null;
		return new LoginData(login, password, true);
	}

	public final void setLoginData(String id, LoginData loginData) {
		p.set("login." + id + ".login", loginData.getLogin());
		p.setCrypted("login." + id + ".password", loginData.getPassword());
	}

	public final void removeLoginData(String id) {
		p.remove("login." + id + ".login");
		p.remove("login." + id + ".password");
	}

}
