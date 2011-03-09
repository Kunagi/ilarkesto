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

import ilarkesto.auth.LoginData;
import ilarkesto.base.Reflect;
import ilarkesto.base.Url;
import ilarkesto.form.Form;
import ilarkesto.form.FormButton;
import ilarkesto.form.swing.FormDialog;
import ilarkesto.swing.ExceptionPanel;
import ilarkesto.swing.Swing;
import ilarkesto.ui.AUi;
import ilarkesto.ui.AView;

import java.awt.Component;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SwingUi extends AUi {

	private JFrame frame;

	// --- dependencies ---

	protected ASwingApplicationConfig config;
	protected ASwingApplication application;

	public void setApplication(ASwingApplication application) {
		this.application = application;
	}

	public void setConfig(ASwingApplicationConfig config) {
		this.config = config;
	}

	// --- ---

	@Override
	protected Class<? extends AView> getEntityView() {
		return null;
	}

	@Override
	public void showView(Class<? extends AView> view) {
	// TODO: SwingView
	}

	@Override
	protected void showDialog(Class<? extends AView> view) {
		showView(view); // TODO
	}

	@Override
	protected void showView(String viewId) {
		Component component = getJavaComponent(viewId);

		showView(component);
	}

	private void showView(Component component) {
		JFrame frame = getFrame();
		frame.add(component);
		frame.pack();
		frame.setMinimumSize(frame.getPreferredSize());
		Swing.center(frame);
		if (!frame.isVisible()) frame.setVisible(true);
	}

	@Override
	public void showWebPage(Url url) {
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	protected void showDialog(String viewId) {
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	public boolean isViewSet() {
		return frame != null;
	}

	private Component getJavaComponent(String id) {
		Object view = Reflect.getProperty(application, id);
		if (view == null) throw new RuntimeException("Component does not exist: " + id);
		Component javaComponent = null;
		if (view instanceof Component) {
			javaComponent = (Component) view;
		} else if (view instanceof AComponent) {
			AComponent c = (AComponent) view;
			model.autowire(c);
			javaComponent = c.getJavaComponent();
		}

		if (javaComponent == null) { throw new RuntimeException("Unsupported component type: "
				+ view.getClass().getName()); }
		return javaComponent;
	}

	public JFrame getFrame() {
		if (frame == null) {
			frame = new JFrame();
			frame.addWindowListener(application.getShutdownWindowListener());
			frame.setTitle(application.getApplicationLabel());
		}
		return frame;
	}

	public void showDirSelectionDialog(File dir, String title, String description, final ADialogAdapter<File> adapter) {
		final DirSelectionComponent component = new DirSelectionComponent();
		component.setUi(this);
		component.setSelectedDir(dir);
		showDialog(component.getJavaComponent(), title, description, localizer.string("dirSelection.ok"), localizer
				.string("dirSelection.ok.hint"), null, new ADialogAdapter() {

			@Override
			public void onSubmit(Object payload) {
				adapter.onSubmit(component.getSelectedDir());
			}

			@Override
			public void onAbort() {
				super.onAbort();
			}

		});
	}

	public void showLoginDialog(final String id, final ADialogAdapter<LoginData> adapter) {
		LoginData initialLoginData = config.getLoginData(id);
		final LoginComponent loginComponent = new LoginComponent();
		loginComponent.setUi(this);
		loginComponent.setInitialLoginData(initialLoginData);
		String description = localizer.string("login." + id + ".description");
		showDialog(loginComponent.getJavaComponent(), localizer.string("login." + id + ".title"), description,
			localizer.string("login.okLabel"), localizer.string("login.okHint"), "password", new ADialogAdapter() {

				@Override
				public void onSubmit(Object payload) {
					LoginData loginData = loginComponent.getLoginData();
					if (loginData.isSavePassword()) {
						config.setLoginData(id, loginData);
					} else {
						config.removeLoginData(id);
					}
					adapter.onSubmit(loginData);
				}

				@Override
				public void onAbort() {
					adapter.onAbort();
				}

			});
	}

	public <C extends Component> void showDialog(final C javaComponent, String title, String description,
			String okLabel, String okHint, String icon128, final ADialogAdapter<C> adapter) {
		SwingDialog dialog = new SwingDialog();
		dialog.setUi(this);
		dialog.setComponent(javaComponent);
		dialog.setTitle(title);
		dialog.setDescription(description);
		dialog.setOkLabel(okLabel);
		dialog.setOkHint(okHint);
		dialog.setParentComponent(getParentComponent());
		dialog.setIcon128(icon128);
		dialog.showDialog(new ADialogAdapter() {

			@Override
			public void onSubmit(Object payload) {
				adapter.onSubmit(javaComponent);
			}

			@Override
			public void onAbort() {
				adapter.onAbort();
			}

		});
	}

	public final FormButton showFormDialog(Form form) {
		return FormDialog.showDialog(Swing.getWindow(getParentComponent()), form);
	}

	public final void showErrorDialog(Throwable ex) {
		ExceptionPanel.showDialog(getParentComponent(), ex, localizer.string("dialog.error.title"));
	}

	public final void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(getParentComponent(), Swing.createMessageComponent(message), localizer
				.string("dialog.error.title"), JOptionPane.ERROR_MESSAGE);
	}

	public final void showInfoDialog(String message) {
		Swing.showMessageDialog(getParentComponent(), message);
	}

	public final boolean showConfirmDialog(String message) {
		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(getParentComponent(), message, localizer
				.string("dialog.confirm.title"), JOptionPane.OK_CANCEL_OPTION);
	}

	public final boolean showYesNoDialog(String message) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(getParentComponent(), message, localizer
				.string("dialog.yesno.title"), JOptionPane.YES_NO_OPTION);
	}

	public Component getParentComponent() {
		return frame;
	}

}
