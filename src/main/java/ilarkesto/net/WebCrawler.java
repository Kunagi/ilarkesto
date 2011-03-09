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
package ilarkesto.net;

import ilarkesto.base.Str;
import ilarkesto.core.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebCrawler {

	private static Log log = Log.get(WebCrawler.class);

	private Filter filter;
	private Consumer consumer;

	private Set<String> crawledUrls = new HashSet<String>();

	public static void download(String url, String destinationDir) {
		WebCrawler wc = new WebCrawler();
		wc.activateDownloading(destinationDir);
		wc.crawl(url);
	}

	public void crawl(String url) {
		if (filter == null) filter = new HostFilter(url);
		crawl(Utl.toList(url));
	}

	private void crawl(Collection<String> urls) {
		Set<String> newUrls = new HashSet<String>();
		for (String url : urls) {
			Set<String> parsedUrls = doCrawl(url);
			if (!parsedUrls.isEmpty()) log.debug("  parsed", parsedUrls.size(), "URLs");
			newUrls.addAll(parsedUrls);
		}
		List<String> nextUrls = new ArrayList<String>();
		for (String url : newUrls) {
			if (crawledUrls.contains(url)) continue;
			if (filter != null && !filter.accept(url)) {
				log.debug("  filtered out:", url);
				continue;
			}
			nextUrls.add(url);
		}
		if (nextUrls.isEmpty()) return;
		crawl(nextUrls);
	}

	private Set<String> doCrawl(String url) {
		log.debug("Crawling:", url);
		crawledUrls.add(url);
		if (!isProbablyHtml(url)) {
			if (consumer == null || consumer.skipNonHtml(url)) return Collections.emptySet();
		}
		URLConnection connection = IO.openUrlConnection(url, null, null);
		String type;
		try {
			connection.connect();
			if (consumer != null) consumer.onConnected(url, connection);
			type = connection.getContentType();
		} catch (Exception ex) {
			if (consumer != null) {
				try {
					consumer.onError(ex, url, connection);
				} catch (Exception ex1) {
					throw new RuntimeException(ex1);
				}
				return Collections.emptySet();
			} else {
				throw new RuntimeException(ex);
			}
		}
		if (Str.isBlank(type)) type = "application/unknown";
		if (type.startsWith("text/html")) {
			String encoding = connection.getContentEncoding();
			if (Str.isBlank(encoding)) encoding = IO.UTF_8;
			String html;
			try {
				html = IO.readToString(connection.getInputStream(), encoding);
			} catch (FileNotFoundException ex) {
				log.debug("  not found:", url);
				if (consumer != null) consumer.onNotFound(url);
				return Collections.emptySet();
			} catch (IOException ex) {
				throw new RuntimeException("Loading URL failed: " + url, ex);
			}
			if (consumer != null) consumer.onHtml(url, html);
			return parseUrls(html, url);
		}
		if (consumer != null) consumer.onUnknown(url, connection);
		return Collections.emptySet();
	}

	static boolean isProbablyHtml(String url) {
		String s;
		try {
			s = new URL(url).getPath();
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
		s = s.toLowerCase();
		int idx = s.lastIndexOf('/');
		if (idx > 0) s = s.substring(idx);
		if (!s.contains(".")) return true;
		if (s.endsWith(".html")) return true;
		if (s.endsWith(".htm")) return true;
		if (s.endsWith(".jsp")) return true;
		if (s.endsWith(".php")) return true;
		return false;
	}

	static String normalizeUrl(String url) {
		int idx = url.indexOf('#');
		if (idx >= 0) url = url.substring(0, idx);

		url = url.replace("/./", "/");

		while (url.contains("/../")) {
			int index = url.indexOf("/../");
			int from = index - 1;
			while (url.charAt(from) != '/') {
				from--;
			}
			url = url.substring(0, from) + url.substring(index + 3);
		}

		return url;
	}

	private Set<String> parseUrls(String html, String sourceUrl) {
		Set<String> urls = new HashSet<String>();
		HtmlParser parser = new HtmlParser(html);
		String url;
		while ((url = parser.nextUrl()) != null) {
			url = normalizeUrl(url);
			if (Str.isBlank(url)) continue;
			url = concatUrlWithRelative(sourceUrl, url);
			url = normalizeUrl(url);
			urls.add(url);
		}
		return urls;
	}

	static String concatUrlWithRelative(String sourceUrl, String relativeUrl) {
		if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) return relativeUrl;
		String baseUrl = getBaseUrl(sourceUrl);
		return baseUrl + relativeUrl;
	}

	static String getBaseUrl(String url) {
		int fromIdx = 7;
		if (url.startsWith("https://")) fromIdx++;
		int idx = url.lastIndexOf('/');
		if (idx > fromIdx) url = url.substring(0, idx);
		if (!url.endsWith("/")) url = url + '/';
		return url;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	public void activateDownloading(String destinationDir) {
		setConsumer(new DownloadConsumer(destinationDir));
	}

	public Set<String> getCrawledUrls() {
		return crawledUrls;
	}

	private class HtmlParser {

		private String html;

		public HtmlParser(String html) {
			super();
			this.html = html;
		}

		public String nextUrl() {
			int idx = Str.indexOf(html, new String[] { "href=", "src=" }, 0);
			if (idx < 0) return null;
			html = html.substring(idx);
			if (html.startsWith("href=")) {
				html = html.substring(5);
				return nextAttributeValue();
			}
			if (html.startsWith("src=")) {
				html = html.substring(4);
				return nextAttributeValue();
			}
			throw new IllegalStateException(html);
		}

		private String nextAttributeValue() {
			int idx = Str.indexOf(html, new String[] { "\"", "'" }, 0);
			if (idx < 0) return nextUrl();
			char quote = html.charAt(idx);
			html = html.substring(idx + 1);
			idx = html.indexOf(quote);
			if (idx < 0) return nextUrl();
			String url = html.substring(0, idx);
			html = html.substring(idx + 1);
			if (url.contains("\" + gaJsHost + \"")) return nextUrl();
			return url;
		}
	}

	public static interface Filter {

		boolean accept(String url);

	}

	public static class HostFilter implements Filter {

		private String host;

		public HostFilter(String url) {
			super();
			URL u;
			try {
				u = new URL(url);
			} catch (MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
			this.host = u.getProtocol() + "://" + u.getHost();
			// System.out.println("------------------> host: " + host);
		}

		@Override
		public boolean accept(String url) {
			return url.startsWith(host);
		}

	}

	public static interface Consumer {

		void onConnected(String url, URLConnection connection);

		void onNotFound(String url);

		boolean skipNonHtml(String url);

		void onError(Exception ex, String url, URLConnection connection) throws Exception;

		void onUnknown(String url, URLConnection connection);

		void onHtml(String url, String html);

	}

	public static class DownloadConsumer implements Consumer {

		private String destinationDir;
		private boolean skipNonHtml;

		public DownloadConsumer(String destinationDir) {
			super();
			this.destinationDir = destinationDir;
		}

		@Override
		public void onHtml(String url, String html) {
			File file = getFile(url);
			log.info("Storing:", file);
			IO.writeFile(file, html, IO.UTF_8);
		}

		@Override
		public void onUnknown(String url, URLConnection connection) {
			File file = getFile(url);
			log.info("Storing:", file);
			IO.downloadToFile(connection, file);
		}

		private File getFile(String url) {
			URL u;
			try {
				u = new URL(url);
			} catch (MalformedURLException ex) {
				throw new RuntimeException(ex);
			}
			String path = u.getPath();
			if (Str.isBlank(path)) path = "/";
			if (path.endsWith("/")) path = "_.html";
			return new File(destinationDir + "/" + u.getHost() + "/" + path);
		}

		@Override
		public boolean skipNonHtml(String url) {
			return skipNonHtml;
		}

		@Override
		public void onNotFound(String url) {}

		@Override
		public void onError(Exception ex, String url, URLConnection connection) throws Exception {
			throw ex;
		}

		@Override
		public void onConnected(String url, URLConnection connection) {}

	}

}
