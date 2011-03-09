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
package ilarkesto.integration.google;

import ilarkesto.integration.jdom.JDom;
import ilarkesto.io.StringOutputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.ParameterStyle;
import net.oauth.OAuth.Parameter;
import net.oauth.client.OAuthClient;
import net.oauth.client.OAuthResponseMessage;
import net.oauth.client.httpclient4.HttpClient4;
import net.oauth.client.httpclient4.HttpClientPool;

import org.apache.commons.cli.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.jdom.Document;

import com.google.oacurl.dao.AccessorDao;
import com.google.oacurl.dao.ConsumerDao;
import com.google.oacurl.dao.ServiceProviderDao;
import com.google.oacurl.options.FetchOptions;
import com.google.oacurl.options.FetchOptions.Method;
import com.google.oacurl.util.MultipartRelatedInputStream;
import com.google.oacurl.util.PropertiesProvider;

public class Oacurl {

	public static Document fetchXml(String url, InputStream in) {
		String xml = fetchString(url, in);
		return JDom.createDocument(xml);
	}

	public static String fetchString(String url, InputStream in) {
		StringOutputStream out = new StringOutputStream();
		fetch(url, in, out);
		return out.toString();
	}

	public static void fetch(String url, InputStream in, OutputStream out) {
		FetchOptions options = new FetchOptions();
		try {
			options.parse(new String[] { url });
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}

		ServiceProviderDao serviceProviderDao = new ServiceProviderDao();
		ConsumerDao consumerDao = new ConsumerDao();
		AccessorDao accessorDao = new AccessorDao();

		Properties loginProperties = null;
		try {
			loginProperties = new PropertiesProvider(options.getLoginFileName()).get();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(
					".oacurl.properties file not found in homedir. Make sure you've run oacurl-login first!", e);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		OAuthServiceProvider serviceProvider = serviceProviderDao.nullServiceProvider();
		OAuthConsumer consumer = consumerDao.loadConsumer(loginProperties, serviceProvider);
		OAuthAccessor accessor = accessorDao.loadAccessor(loginProperties, consumer);

		OAuthClient client = new OAuthClient(new HttpClient4(SingleClient.HTTP_CLIENT_POOL));

		try {
			OAuthMessage request;

			List<Entry<String, String>> related = options.getRelated();

			Method method = options.getMethod();
			if (method == Method.POST || method == Method.PUT) {
				InputStream bodyStream;
				if (related != null) {
					bodyStream = new MultipartRelatedInputStream(related);
				} else if (options.getFile() != null) {
					bodyStream = new FileInputStream(options.getFile());
				} else {
					bodyStream = in;
				}
				request = accessor.newRequestMessage(method.toString(), url, null, bodyStream);
				request.getHeaders().add(new OAuth.Parameter("Content-Type", options.getContentType()));
			} else {
				request = accessor.newRequestMessage(method.toString(), url, null, null);
			}

			List<Parameter> headers = options.getHeaders();
			addHeadersToRequest(request, headers);

			OAuthResponseMessage response = client.access(request, ParameterStyle.AUTHORIZATION_HEADER);

			// Dump the bytes in the response's encoding.
			InputStream bodyStream = response.getBodyAsStream();
			byte[] buf = new byte[1024];
			int count;
			while ((count = bodyStream.read(buf)) > -1) {
				out.write(buf, 0, count);
			}
			out.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void addHeadersToRequest(OAuthMessage request, List<Parameter> headers) {
		// HACK(phopkins): If someone added their own Expect header, then tell
		// Apache not to add its own. This is a bit hacky, but gets around that
		// the RequestExpectContinue class doesn't check for an existing header
		// before adding its own.
		//
		// Fix for: http://code.google.com/p/oacurl/issues/detail?id=1
		boolean hasExpect = false;
		for (Parameter param : headers) {
			if (param.getKey().equalsIgnoreCase(HTTP.EXPECT_DIRECTIVE)) {
				hasExpect = true;
				break;
			}
		}

		if (hasExpect) {
			HttpProtocolParams.setUseExpectContinue(SingleClient.HTTP_CLIENT_POOL.getHttpClient().getParams(), false);
		}

		request.getHeaders().addAll(headers);
	}

	/**
	 * Broken out of {@link HttpClient4} so that we can get access to the underlying {@link DefaultHttpClient}
	 * object.
	 */
	private static class SingleClient implements HttpClientPool {

		public static final SingleClient HTTP_CLIENT_POOL = new SingleClient();

		private SingleClient() {
			HttpClient client = new DefaultHttpClient();
			ClientConnectionManager mgr = client.getConnectionManager();
			if (!(mgr instanceof ThreadSafeClientConnManager)) {
				HttpParams params = client.getParams();
				client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
			}

			this.client = client;
		}

		private final HttpClient client;

		public HttpClient getHttpClient() {
			return client;
		}

		public HttpClient getHttpClient(URL server) {
			return client;
		}
	}
}
