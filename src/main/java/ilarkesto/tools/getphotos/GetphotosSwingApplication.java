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

import ilarkesto.concurrent.TaskManager;
import ilarkesto.di.app.ApplicationStarter;
import ilarkesto.io.IO;
import ilarkesto.swing.Swing;
import ilarkesto.ui.swing.ASwingApplication;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GetphotosSwingApplication extends ASwingApplication {

	public static void main(String[] args) {
		ApplicationStarter.startApplication(GetphotosSwingApplication.class, args);
	}

	@Override
	protected void onStartSwing() {
		getUi().getFrame().setIconImage(IO.loadImage("ilarkesto/tools/getphotos/getphotos32.png"));

		if (getArguments().length == 0) {
			JOptionPane.showMessageDialog(null, "Der Zielordner muss als Programmparameter angegeben werden.",
				"Fehler", JOptionPane.ERROR_MESSAGE);
			shutdown();
		}

		showPanel(new MessagePanel("Bitte Kamera anschliessen und einschalten..."));
		getTaskManager().start(new FindCameraTask());
	}

	public void startCopying(File dcimDir) {
		showMessagePanel("<html>Suche nach Fotos auf Kamera... <br><br><i style='font-weight: normal;'>"
				+ dcimDir.getPath() + "</i><br><br>");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
		getTaskManager().start(new CopyTask(dcimDir));
	}

	public void showMessagePanel(String message) {
		showPanel(new MessagePanel(message));
	}

	public void showPanel(final JPanel panel) {
		Swing.invokeInEventDispatchThread(new Runnable() {

			@Override
			public void run() {
				JFrame frame = getUi().getFrame();
				frame.getContentPane().removeAll();
				frame.getContentPane().add(panel);
				frame.pack();
				Swing.center(frame);
				frame.setVisible(true);
			}

		});
	}

	@Override
	protected void scheduleTasks(TaskManager tm) {}

	public File getDestinationDir() {
		return new File(getArguments()[0]);
	}

	@Override
	public void ensureIntegrity() {}

	@Override
	public String getApplicationLabel() {
		return "Fotos von Kamera laden";
	}

	public static GetphotosSwingApplication get() {
		return (GetphotosSwingApplication) ASwingApplication.get();
	}
}
