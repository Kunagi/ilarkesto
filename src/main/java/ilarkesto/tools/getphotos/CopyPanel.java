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
package ilarkesto.tools.getphotos;

import ilarkesto.io.IO;
import ilarkesto.swing.ImageCanvas;
import ilarkesto.swing.Swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class CopyPanel extends JPanel {

	private static final float SIZE = 0.4f;

	private JProgressBar progress;
	private ImageCanvas image;
	private byte[] photoData;

	public CopyPanel() {
		super(new BorderLayout());

		image = new ImageCanvas();
		image.setAutoScale(true);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		image.setPreferredSize(new Dimension((int) (screen.width * SIZE), (int) (screen.height * SIZE)));
		add(image, BorderLayout.CENTER);

		progress = new JProgressBar();
		progress.setStringPainted(true);
		add(progress, BorderLayout.SOUTH);

		Thread updateImageThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}
					updateImage();
				}
			}

		});
		updateImageThread.setPriority(4);
		updateImageThread.start();
	}

	public void setPhotoCount(int count) {
		progress.setMaximum(count);
	}

	public void setStatus(final String message, final int index, final byte[] photoData) {
		this.photoData = photoData;
		if (index == 1) updateImage();
		Swing.invokeInEventDispatchThread(new Runnable() {

			@Override
			public void run() {
				progress.setString(message);
				progress.setValue(index + 1);
			}

		});
	}

	private synchronized void updateImage() {
		if (photoData == null) return;
		final BufferedImage img = IO.loadImage(photoData);
		photoData = null;
		Swing.invokeInEventDispatchThread(new Runnable() {

			@Override
			public void run() {
				image.setImage(img);
			}

		});
	}

}
