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
package ilarkesto.io;

import ilarkesto.base.Assert;
import ilarkesto.core.logging.Log;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageAnalyzer {

	private static final Log LOG = Log.get(ImageAnalyzer.class);

	private BufferedImage image;

	public ImageAnalyzer(BufferedImage img) {
		this.image = img;
		LOG.debug(image);
	}

	public ImageAnalyzer(File f) {
		this(IO.loadImage(f));
	}

	public int findColorFromEast(int color, int x, int y) {
		for (; x >= 0; x--) {
			int c = image.getRGB(x, y);
			if (c == color) return x;
		}
		return -1;
	}

	public void assertColor(int x, int y, int color) {
		Assert.equal(image.getRGB(x, y), color);
	}

	public void assertWidth(int w) {
		Assert.equal(image.getWidth(), w);
	}

	public void assertHeight(int h) {
		Assert.equal(image.getHeight(), h);
	}

	public ImageAnalyzer getSubimage(int x, int y, int w, int h) {
		return new ImageAnalyzer(image.getSubimage(x, y, w, h));
	}

	public ImageAnalyzer getSubimageFromNorthEast(int width, int height) {
		int x = image.getWidth() - width;
		return getSubimage(x, 0, width, height);
	}

	public ImageAnalyzer getSubimageFromEast(int width) {
		int x = image.getWidth() - width;
		return getSubimage(x, 0, width, image.getHeight());
	}

	public ImageAnalyzer getSubimageFromWest(int width) {
		return getSubimage(0, 0, width, image.getHeight());
	}

	public BufferedImage getImage() {
		return image;
	}

}
