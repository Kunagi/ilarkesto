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

import ilarkesto.testng.ATest;

import java.io.File;

import org.testng.annotations.Test;

public class WebCrawlerTest extends ATest {

	@Test
	public void normalizeUrl() {
		assertEquals(WebCrawler.normalizeUrl("http://koczewski.de/#a"), "http://koczewski.de/");
		assertEquals(WebCrawler.normalizeUrl("http://koczewski.de/./index.html"), "http://koczewski.de/index.html");
		assertEquals(WebCrawler.normalizeUrl("http://koczewski.de/test/../index.html"),
			"http://koczewski.de/index.html");
	}

	@Test
	public void getBaseUrl() {
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de"));
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de/"));
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de/index.html"));
		assertEquals("http://koczewski.de/", WebCrawler.getBaseUrl("http://koczewski.de/start"));
	}

	@Test
	public void isProbablyHtml() {
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/index.html"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/index.php"));
		assertTrue(WebCrawler.isProbablyHtml("http://koczewski.de/index.jsp"));
		assertFalse(WebCrawler.isProbablyHtml("http://koczewski.de/image.png"));
	}

	@Test
	public void crawl() {
		WebCrawler wc = new WebCrawler();
		wc.crawl(new File("etc/WebCrawler.html").toURI().toString());
	}

	@Test
	public void download() {
		WebCrawler.download("http://koczewski.de", OUTPUT_DIR + "/webcrawler");
	}

}
