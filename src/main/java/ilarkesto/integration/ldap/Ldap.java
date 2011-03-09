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
package ilarkesto.integration.ldap;

import ilarkesto.auth.AuthenticationFailedException;
import ilarkesto.auth.WrongPasswordException;
import ilarkesto.core.logging.Log;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Ldap {

	private static Log log = Log.get(Ldap.class);

	public static void main(String[] args) {
		System.out.println(authenticateUserGetEmail("ldap://adcsv10:389/", "bind user", "bind password", "base dn",
			"user filter", "user", "password"));
	}

	public static String authenticateUserGetEmail(String url, String bindUser, String bindPassword, String baseDn,
			String userFilterRegex, String user, String password) throws AuthenticationFailedException {
		log.info("LDAP authentication for ", user, "on", url);
		NamingEnumeration<SearchResult> searchResultEnum;
		try {
			DirContext ctx = Ldap.createDirContext(url, bindUser, bindPassword);
			String filter = userFilterRegex == null ? user : userFilterRegex.replaceAll("%u", user);
			SearchControls cons = new SearchControls(SearchControls.SUBTREE_SCOPE, 0, 0, new String[] { "mail" }, true,
					false);
			searchResultEnum = ctx.search(baseDn, filter, cons);
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
		SearchResult searchResult = searchResultEnum.nextElement();
		if (searchResult == null) throw new AuthenticationFailedException("User does not exist.");

		try {
			Ldap.createDirContext(url, searchResult.getName() + "," + baseDn, password);
		} catch (AuthenticationException ex) {
			throw new WrongPasswordException();
		}

		try {
			Attribute mailAttribute = searchResult.getAttributes().get("mail");
			if (mailAttribute != null) return mailAttribute.get().toString();
		} catch (NamingException e) {
			// mail field not handled by ldap tree. can happen on some ldap server or with specific structures
		}
		return null;
	}

	public static DirContext createDirContext(String url, String user, String password) throws AuthenticationException {
		Hashtable env = new Hashtable();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// TODO depends on sun-jdk?

		env.put(Context.PROVIDER_URL, url);

		if (user != null) {
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, user);
			env.put(Context.SECURITY_CREDENTIALS, password);
		}

		log.debug("Connecting LDAP:", url);
		try {
			return new InitialDirContext(env);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
	}

}
