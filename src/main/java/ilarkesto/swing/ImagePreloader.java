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

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ImagePreloader {

	private static Log log = Log.get(ImagePreloader.class);

	private BlockingQueue<ImageWrapper> queue = new LinkedBlockingQueue<ImageWrapper>();
	private Set<ImageWrapper> cache = new LinkedHashSet<ImageWrapper>();

	private boolean shutdown;
	private long size;
	private long maxSize = 64 * 1024 * 1024;

	private boolean autoScale;
	private int maxWidth = 128;
	private int maxHeight = 128;

	public ImagePreloader() {
		Thread sysoutThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						if (shutdown) return;
						ImageWrapper image = queue.poll(Long.MAX_VALUE, TimeUnit.SECONDS);
						image.load();
						log.debug("preloaded:", image.file);
					} catch (InterruptedException ignored) {
						shutdown = true;
					}
				}
			}
		});
		sysoutThread.setName(ImagePreloader.class.getName());
		sysoutThread.start();
	}

	public BufferedImage get(File file) {
		synchronized (cache) {
			for (ImageWrapper image : cache) {
				if (image.getFile() != file) continue;
				image.load();
				return image.getImage();
			}
		}
		ImageWrapper image = new ImageWrapper(file);
		image.load();
		return image.getImage();
	}

	public void add(File file) {
		synchronized (cache) {
			for (ImageWrapper image : cache) {
				if (image.getFile() == file) return;
			}
			ImageWrapper image = new ImageWrapper(file);
			if (image.getSize() > maxSize) return;
			queue.add(image);
			cache.add(image);
			size += image.getSize();
			while (size > maxSize && !cache.isEmpty()) {
				unloadOne();
			}
		}
	}

	private void unloadOne() {
		synchronized (cache) {
			if (cache.isEmpty()) return;
			ImageWrapper image = cache.iterator().next();
			cache.remove(image);
			size -= image.getSize();
			log.debug("unloaded:", image.file);
		}
	}

	public void setAutoScale(int maxWidth, int maxHeight) {
		if (autoScale && maxWidth == this.maxWidth && maxHeight == this.maxHeight) return;
		clear();
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.autoScale = true;
	}

	private void clear() {
		synchronized (cache) {
			cache.clear();
			queue.clear();
			size = 0;
		}
		log.debug("cache cleared");
	}

	class ImageWrapper {

		private File file;
		private BufferedImage image;
		private long size;

		public ImageWrapper(File file) {
			super();
			this.file = file;
			this.size = file.length();
		}

		public long getSize() {
			return size;
		}

		public boolean isLoaded() {
			return image != null;
		}

		public synchronized void load() {
			if (isLoaded()) return;
			image = IO.loadImage(file);
			if (autoScale) scale();
		}

		private void scale() {
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();

			if (imageWidth > maxWidth || imageHeight > maxHeight) {
				image = IO.toBufferedImage(IO.getScaled(this.image, maxWidth, maxHeight));
				imageWidth = image.getWidth();
				imageHeight = image.getHeight();
			}
		}

		public File getFile() {
			return file;
		}

		public BufferedImage getImage() {
			return image;
		}

		@Override
		public int hashCode() {
			return file.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ImageWrapper)) return false;
			return file.equals(((ImageWrapper) obj).file);
		}

	}

}
