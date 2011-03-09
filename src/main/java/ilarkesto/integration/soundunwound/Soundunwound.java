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
package ilarkesto.integration.soundunwound;

import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.httpunit.HttpUnit;
import ilarkesto.io.IO;

import java.io.File;

import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

public class Soundunwound {

	private static Log log = Log.get(Soundunwound.class);

	public static String extractId(String url) {
		if (Str.isBlank(url)) return null;
		String id = url;
		id = id.substring(id.lastIndexOf('/') + 1);
		int idx = id.indexOf("?");
		if (idx > 0) id = id.substring(0, idx);
		return id;
	}

	public static String determineIdByTitle(String title, boolean guess) {
		log.info("Determining Soundunwound-ID by title:", title);
		String titleSearchUrl = getTitleSearchUrl(title);
		log.debug("Loading", titleSearchUrl);
		WebResponse response = HttpUnit.loadPage(titleSearchUrl);
		WebTable table = HttpUnit.getTable("releases", response);
		if (table == null) {
			if (guess) {
				int idx = title.lastIndexOf(' ');
				if (idx > 0) return determineIdByTitle(title.substring(0, idx), guess);
			}
			return null;
		}
		int rows = table.getRowCount();
		if (rows == 0) return null;
		if (rows > 1 && !guess) return null;
		TableCell cell = table.getTableCell(0, 1);
		if (cell == null) return null;
		for (WebLink link : cell.getLinks()) {
			String url = link.getURLString();
			if (url.contains("releaseId=")) {
				String id = Str.cutFromTo(url, "releaseId=", "&");
				return id;
			} else if (url.endsWith("?ref=SR")) {
				String id = url.substring(url.lastIndexOf('/') + 1, url.length() - 7);
				return id;
			}
		}
		return null;
	}

	public static SoundunwoundRecord loadRecord(String soundunwoundId) {
		if (soundunwoundId == null) return null;
		String url = getPageUrl(soundunwoundId);
		log.info("Loading Soundunwound record:", soundunwoundId);
		WebResponse page = HttpUnit.loadPage(url);
		String title = null;
		String artist = null;
		Integer year;
		String coverId;
		try {
			String longTitle = parseLongTitle(page);
			if (longTitle != null) {
				int idx = longTitle.indexOf(" by ");
				if (idx < 0) {
					title = longTitle;
				} else {
					title = longTitle.substring(0, idx);
					artist = longTitle.substring(idx + 4);
				}
			}
			year = parseYear(page);
			coverId = parseCoverId(page);
		} catch (Exception ex) {
			throw new RuntimeException("Parsing Soundunwound page failed: " + url, ex);
		}

		return new SoundunwoundRecord(soundunwoundId, title, artist, year, coverId);
	}

	private static String parseLongTitle(WebResponse response) {
		String title = HttpUnit.getTitle(response);
		if (title == null) return null;
		return Str.cutTo(title, " - ");
	}

	private static Integer parseYear(WebResponse response) {
		HTMLElement div = HttpUnit.getElementWithId("overviewSection", response);
		if (div == null) return null;
		String s = Str.cutFrom(div.getText(), "First released:");
		if (s == null) return null;
		s = s.trim();
		int idx = s.lastIndexOf(' ');
		if (idx > 0) s = s.substring(idx + 1);
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			log.info("Parsing year from " + response.getURL() + " failed: " + s);
			return null;
		}
	}

	private static String parseCoverId(WebResponse response) {
		HTMLElement img = HttpUnit.getElementWithId("entity-main-image", response);
		if (img == null) return null;
		String s = img.getAttribute("src");
		s = Str.cutFromTo(s, "http://ecx.images-amazon.com/images/I/", ".");
		return s;
	}

	public static String getTitleSearchUrl(String title) {
		return "http://www.soundunwound.com/sp/search/find?searchPhrase=" + Str.encodeUrlParameter(title);
	}

	public static String getPageUrl(String id) {
		return "http://www.soundunwound.com/music/-/-/" + id;
	}

	public static String getCoverUrl(String coverId, int width) {
		if (coverId == null) return null;
		return "http://ecx.images-amazon.com/images/I/" + coverId + "._SL" + width + "_.jpg";
	}

	public static void downloadCover(String coverId, int width, File destinationFile) {
		String url = getCoverUrl(coverId, width);
		log.info("Downloading Soundunwound cover:", url);
		IO.downloadUrlToFile(url, destinationFile.getPath());
	}

}
