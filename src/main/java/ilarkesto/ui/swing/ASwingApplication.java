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
package ilarkesto.ui.swing;

import ilarkesto.base.Str;
import ilarkesto.di.app.AApplication;
import ilarkesto.locale.LearningLocalizer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

public abstract class ASwingApplication extends AApplication {

	protected abstract void onStartSwing();

	protected void onShutdownSwing() {}

	@Override
	protected final void onStart() {
		onStartSwing();
	}

	@Override
	protected final void onShutdown() {
		onShutdownSwing();
		getTaskManager().shutdown(3000);
		System.exit(0);
	}

	// --- default beans ---

	private SwingUi ui;

	public SwingUi getUi() {
		if (ui == null) {
			ui = new SwingUi();
			autowire(ui);
		}
		return ui;
	}

	private LearningLocalizer localizer;

	public LearningLocalizer getLocalizer() {
		if (localizer == null) {
			localizer = new LearningLocalizer();
			autowire(localizer);
			localizer.setLocale(Locale.GERMANY);
		}
		return localizer;
	}

	private WindowAdapter shutdownWindowListener;

	public WindowAdapter getShutdownWindowListener() {
		if (shutdownWindowListener == null) {
			shutdownWindowListener = new WindowAdapter() {

				@Override
				public void windowClosing(WindowEvent e) {
					shutdown();
				}

			};
			autowire(shutdownWindowListener);
		}
		return shutdownWindowListener;
	}

	// --- ---

	@Override
	public String getApplicationName() {
		return Str.removeSuffix(super.getApplicationName(), "Swing");
	}

}
