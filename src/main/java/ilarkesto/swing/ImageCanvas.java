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
package ilarkesto.swing;

import ilarkesto.io.IO;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageCanvas extends Component {

	public static void main(String[] args) throws Throwable {
		ImageCanvas ic = new ImageCanvas();
		ic.setImage(Swing.captureScreen(Swing.getWindow(ic)));
		ic.setPreferredSize(new Dimension(300, 300));
		Swing.showInJFrame(ic, "ImageCanvas", null, true);
	}

	private Color backgroundColor = Color.DARK_GRAY;

	private BufferedImage image;
	private boolean autoScale;

	private ImagePreloader preloader;

	public ImageCanvas() {}

	public ImageCanvas(BufferedImage image) {
		setImage(image);
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		if (image != null) {
			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		}
		repaint();
	}

	public void setImage(File image) {
		setImage((BufferedImage) null);
		new ImageLoadThread(image).start();
	}

	public void preloadImage(File image) {
		getPreloader().add(image);
	}

	@Override
	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		if (autoScale && preloader != null) preloader.setAutoScale(width, height);

		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);

		if (image == null) return;

		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		if (autoScale) {
			if (imageWidth > width || imageHeight > height) {
				image = IO.toBufferedImage(IO.getScaled(this.image, width, height));
				imageWidth = image.getWidth();
				imageHeight = image.getHeight();
			}
		}

		int x = 0;
		int y = 0;

		if (width > imageWidth) x = (width - imageWidth) / 2;
		if (height > imageHeight) y = (height - imageHeight) / 2;

		g.drawImage(image, x, y, null);
	}

	public BufferedImage getImage() {
		return image;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setAutoScale(boolean autoScale) {
		this.autoScale = autoScale;
		if (autoScale && preloader != null) preloader.setAutoScale(getWidth(), getHeight());
	}

	public boolean isAutoScale() {
		return autoScale;
	}

	public synchronized ImagePreloader getPreloader() {
		if (preloader == null) {
			preloader = new ImagePreloader();
			if (autoScale) preloader.setAutoScale(getWidth(), getHeight());
		}
		return preloader;
	}

	class ImageLoadThread extends Thread {

		private File file;

		public ImageLoadThread(File file) {
			super();
			this.file = file;
		}

		@Override
		public void run() {
			setName("ImageLoadThread:" + file.getPath());
			final BufferedImage image = getPreloader().get(file);
			Swing.invokeInEventDispatchThread(new Runnable() {

				@Override
				public void run() {
					setImage(image);
				}
			});
		}
	}

}
