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
package ilarkesto.integration.imdb;

import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.httpunit.HttpUnit;
import ilarkesto.io.IO;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebImage;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;

public class Imdb {

	private static final String TITLE_URL_PREFIX = "http://akas.imdb.com/title/";

	private static Log log = Log.get(Imdb.class);

	public static String determineIdByTitle(String title, boolean guess) {
		log.info("Determining IMDB-ID by title:", title);
		WebResponse response = HttpUnit.loadPage(getTitleSearchUrl(title));
		String url = response.getHeaderField("LOCATION");
		if (!Str.isBlank(url) && url.startsWith(TITLE_URL_PREFIX)) {
			url = Str.removePrefix(url, TITLE_URL_PREFIX);
			if (url.contains("/")) url = Str.cutTo(url, "/");
			return url;
		}

		if (guess) {
			WebLink[] links;
			try {
				links = response.getLinks();
			} catch (SAXException ex) {
				throw new RuntimeException(ex);
			}
			for (WebLink link : links) {
				String linkUrl = link.getURLString();
				if (Str.isBlank(linkUrl)) continue;
				linkUrl = Str.removePrefix(linkUrl, "http://akas.imdb.com");
				if (linkUrl.startsWith("/title/")) {
					String id = Str.removePrefix(linkUrl, "/title/");
					return Str.removeSuffix(id, "/");
				}
			}
		}

		return null;
	}

	public static ImdbRecord loadRecord(String imdbId) {
		if (imdbId == null) return null;
		String url = getPageUrl(imdbId);
		log.info("Loading IMDB record:", imdbId);
		WebResponse akasPage = HttpUnit.loadPage(url);
		String title;
		Integer year;
		String coverId;
		String trailerId;
		try {
			title = parseTitle(akasPage);
			year = parseYear(akasPage);
			coverId = parseCoverId(akasPage);
			trailerId = null;
			String tagline = parseInfoContent(akasPage, "Tagline");
			String plot = parseInfoContent(akasPage, "Plot");
			String awards = parseInfoContent(akasPage, "Awards");
		} catch (Exception ex) {
			throw new RuntimeException("Parsing IMDB page failed: " + url, ex);
		}

		url = getPageUrlDe(imdbId);
		WebResponse dePage = HttpUnit.loadPage(url);
		String titleDe = parseTitle(dePage);

		return new ImdbRecord(imdbId, title, titleDe, year, coverId, trailerId);
	}

	private static String parseInfoContent(WebResponse response, String label) {
		String text;
		try {
			text = response.getText();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		text = Str.cutFromTo(text, "<h5>" + label + ":</h5>", "</div>");
		if (text == null) return null;
		text = Str.cutFrom(text, "<div class=\"info-content\">");
		if (text == null) return null;
		if (text.contains("<a ")) text = Str.cutTo(text, "<a ");
		return Str.html2text(text.trim());
	}

	private static Integer parseYear(WebResponse response) {
		String title;
		try {
			title = response.getTitle();
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
		if (title == null) return null;

		int idx = title.lastIndexOf(" (");
		if (idx < 1) return null;
		String s = Str.cutFromTo(title.substring(idx), "(", ")");
		if (s == null) return null;
		// s = Str.removePrefix(s, "Video");
		// s = Str.removePrefix(s, "TV");
		// s = Str.removeSuffix(s, "?");
		// s = Str.removeSuffix(s, "–");
		while (s.contains(" ") && !(s.startsWith("20") || s.startsWith("19"))) {
			s = Str.cutFrom(s, " ");
		}
		s = s.trim();
		return Integer.parseInt(s.substring(0, 4));
	}

	private static String parseTitle(WebResponse response) {
		String title;
		try {
			title = response.getTitle();
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
		if (title == null) return null;
		int idx = title.indexOf(" (");
		if (idx < 1) return title;
		return title.substring(0, idx);
	}

	private static String parseCoverId(WebResponse response) {
		HTMLElement img;
		try {
			img = response.getElementWithID("primary-poster");
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		}
		if (img == null) {
			TableCell td;
			try {
				td = (TableCell) response.getElementWithID("img_primary");
			} catch (SAXException ex1) {
				throw new RuntimeException(ex1);
			}
			WebImage[] images = td.getImages();
			if (images != null && images.length > 0) img = images[0];
		}

		if (img == null) return null;
		String url = img.getAttribute("src");
		if (url == null) return null;
		if (!url.startsWith("http://ia.media-imdb.com/images/M/")) return null;
		if (!url.contains("._")) return null;
		String id = Str.removePrefix(url, "http://ia.media-imdb.com/images/M/");
		id = id.substring(0, id.indexOf("._"));
		return id;
	}

	public static String getTitleSearchUrl(String title) {
		return "http://akas.imdb.com/find?s=tt&q=" + Str.encodeUrlParameter(title);
	}

	public static String getPageUrl(String imdbId) {
		return TITLE_URL_PREFIX + imdbId + "/";
	}

	public static String getPageUrlDe(String imdbId) {
		return "http://www.imdb.de/title/" + imdbId + "/";
	}

	public static void downloadCover(String coverId, File destinationFile) {
		String url = getCoverUrl(coverId);
		log.info("Downloading IMDB cover:", url);
		IO.downloadUrlToFile(url, destinationFile.getPath());
	}

	public static String getCoverUrl(String coverId) {
		if (coverId == null) return null;
		return "http://ia.media-imdb.com/images/M/" + coverId + "._V1._SX510_SY755_.jpg";
	}

	public static String extractId(String url) {
		if (Str.isBlank(url)) return null;
		String id = url;
		id = Str.removePrefix(id, TITLE_URL_PREFIX);
		id = Str.removePrefix(id, "http://www.imdb.de/title/");
		id = Str.removeSuffix(id, "/");
		return id;
	}

}
