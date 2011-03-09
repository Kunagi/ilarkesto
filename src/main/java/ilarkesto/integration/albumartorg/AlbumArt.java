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
package ilarkesto.integration.albumartorg;

import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;
import java.io.FileNotFoundException;

public class AlbumArt {

	private static Log log = Log.get(AlbumArt.class);

	public static String determineCoverUrl(String title) {
		log.info("Determining cover for:", title);
		String html = IO
				.downloadUrlToString("http://www.albumart.org/index.php?itempage=1&newsearch=1&searchindex=Music&srchkey="
						+ Str.encodeUrlParameter(title));
		if (Str.isBlank(html)) return null;
		String coverId = Str.cutFromTo(html, "<a href=\"http://ecx.images-amazon.com/images/I/", "\"");
		if (coverId == null) return null;
		return "http://ecx.images-amazon.com/images/I/" + coverId;
	}

	public static boolean downloadCover(String title, File destinationFile) {
		String url = determineCoverUrl(title);
		if (url == null) return false;
		log.info("Downloading cover:", url);
		try {
			IO.downloadUrlToFile(url, destinationFile.getPath());
		} catch (Exception ex) {
			if (ex instanceof FileNotFoundException) return false;
			log.info("Downloading cover image failed:", url, ex);
			return false;
		}
		return true;
	}

}
