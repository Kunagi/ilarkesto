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

import ilarkesto.base.Env;
import ilarkesto.base.Str;
import ilarkesto.base.time.Date;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.concurrent.ACollectionTask;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JOptionPane;

public class CopyTask extends ACollectionTask<File> {

	private static final Log LOG = Log.get(CopyTask.class);

	private File dcimDir;
	private CopyPanel copyPanel;
	private Collection<File> photos;
	private Collection<File> copiedPhotos = new ArrayList<File>();
	private File destinationDir;
	private byte[] lastCopiedPhoto;
	private Date date;

	public CopyTask(File dcimDir) {
		this.dcimDir = dcimDir;
		copyPanel = new CopyPanel();
	}

	@Override
	protected Collection<File> prepare() throws InterruptedException {
		photos = getPhotos(dcimDir);
		GetphotosSwingApplication.get().showPanel(copyPanel);

		if (photos.isEmpty()) {
			JOptionPane.showMessageDialog(copyPanel, "Keine Fotos auf Kamera " + dcimDir.getPath() + " gefunden.",
				"Keine Fotos", JOptionPane.INFORMATION_MESSAGE);
			abort();
			GetphotosSwingApplication.get().shutdown();
			return null;
		}

		String dirName = DateAndTime.now().toString(DateAndTime.FORMAT_LOG);
		destinationDir = new File(GetphotosSwingApplication.get().getDestinationDir() + "/" + dirName);

		copyPanel.setPhotoCount(photos.size());

		return photos;
	}

	@Override
	protected void perform(File file) throws InterruptedException {
		copyPanel.setStatus("Kopiere " + file.getName(), getIndex(), lastCopiedPhoto);
		File destinationFile = new File(destinationDir.getPath() + "/" + file.getName());
		if (destinationFile.exists()) {
			destinationFile = new File(destinationDir.getPath() + "/" + System.currentTimeMillis() + "_"
					+ file.getName());
		}
		lastCopiedPhoto = IO.readToByteArray(file);
		IO.copyDataToFile(lastCopiedPhoto, destinationFile);
		copiedPhotos.add(file);
		date = new Date(file.lastModified());
	}

	@Override
	protected void cleanup() throws InterruptedException {
		super.cleanup();
		if (isAbortRequested()) return;

		if (date == null) date = new Date();
		String name = JOptionPane
				.showInputDialog(
					copyPanel,
					"<html>Fotos kopiert. Bitte Bezeichnung für das Album eingeben.<br><br><i style='font-weight: normal;'>Z.B. Ort, wo die Fotos gemacht wurden</i><br><br>",
					"Wie soll das Album heissen?", JOptionPane.QUESTION_MESSAGE);
		name = Str.toFileCompatibleString(name).replace(' ', '-');
		name = date.toString(Date.FORMAT_YEAR_MONTH_DAY) + "_" + name;

		File newDestinationDir = new File(destinationDir.getParent() + "/" + name);
		if (destinationDir.renameTo(newDestinationDir)) {
			destinationDir = newDestinationDir;
		}

		int option = JOptionPane.showConfirmDialog(copyPanel, "Sollen die Fotos von der Kamera gelöscht werden?",
			"Kamera löschen?", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			GetphotosSwingApplication.get().showMessagePanel("Lösche kopierte Fotos von Kamera...");
			for (File photo : copiedPhotos) {
				LOG.debug("Deleting photo", photo);
				if (GetphotosSwingApplication.get().isDevelopmentMode()) {
					// sleep(100);
				} else {
					photo.delete();
				}
			}
		}
		Env.get().startFileBrowser(destinationDir);
		sleep(2000);
		GetphotosSwingApplication.get().shutdown();
	}

	private Collection<File> getPhotos(File dir) {
		LOG.debug("Searching for photos:", dir);
		if (isAbortRequested()) return Collections.emptyList();
		Collection<File> photos = new ArrayList<File>();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					photos.addAll(getPhotos(file));
				} else {
					if (isPhoto(file)) photos.add(file);
				}
			}
		}

		return photos;
	}

	private boolean isPhoto(File file) {
		String name = file.getName().toLowerCase();
		return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith("gif");
	}
}
