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
package ilarkesto.integration.httpunit;

import org.xml.sax.SAXException;

import com.meterware.httpunit.ClientProperties;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class HttpUnit {

	public static HTMLElement getElementWithId(String id, WebResponse response) {
		try {
			return response.getElementWithID(id);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String getTitle(WebResponse response) {
		try {
			return response.getTitle();
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static WebTable getTable(String tableId, WebResponse response) {
		try {
			return response.getTableWithID(tableId);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static HTMLElement getFirstElementWithAttribute(WebResponse response, String name, String value) {
		HTMLElement[] elements;
		try {
			elements = response.getElementsWithAttribute("class", "photo");
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
		return elements == null || elements.length < 1 ? null : elements[0];
	}

	public static WebResponse loadPage(String url) {
		try {
			return createWebConversation(false).getResponse(url);
		} catch (Exception ex) {
			throw new RuntimeException("Loading URL failed: " + url, ex);
		}
	}

	public static WebConversation createWebConversation(boolean acceptCookies) {
		HttpUnitOptions.setScriptingEnabled(false);
		WebConversation webConversation = new WebConversation();
		ClientProperties props = webConversation.getClientProperties();
		props.setAcceptGzip(false);
		props.setAcceptCookies(acceptCookies);
		props.setAutoRedirect(false);
		return webConversation;
	}

}
