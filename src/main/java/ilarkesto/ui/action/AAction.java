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
package ilarkesto.ui.action;

import ilarkesto.base.Str;
import ilarkesto.concurrent.ATask;
import ilarkesto.core.logging.Log;
import ilarkesto.di.BeanProvider;
import ilarkesto.form.Form;
import ilarkesto.form.validator.Validator;
import ilarkesto.id.CountingIdGenerator;
import ilarkesto.id.IdGenerator;
import ilarkesto.locale.Localizer;
import ilarkesto.persistence.DaoService;
import ilarkesto.persistence.TransactionService;
import ilarkesto.ui.AUi;
import ilarkesto.ui.AView;
import ilarkesto.ui.DialogTimeoutException;
import ilarkesto.ui.Option;

import java.util.Collection;
import java.util.Set;

public abstract class AAction extends ATask {

	private static final Log LOG = Log.get(AAction.class);

	private static final IdGenerator actionIdGenerator = new CountingIdGenerator("");

	protected abstract void assertPermissions() throws InterruptedException;

	protected abstract void performAction() throws InterruptedException;

	private volatile boolean finished;
	private volatile Throwable exception;

	@Override
	public final void perform() {
		ActionPerformer.registerAction(this);
		try {
			assertPermissions();
			performAction();
		} catch (InterruptedException ex) {
			finish();
			return;
		} catch (ActionAbortedException ex) {
			transactionService.commit();
			exception = ex;
			if (isRootAction() && !ui.isViewSet()) showReturnView();
			finish();
			return;
		} catch (Throwable ex) {
			transactionService.commit();
			exception = ex;
			LOG.error(ex);
			error(ex);
			if (isRootAction() && !ui.isViewSet()) showReturnView();
			finish();
			return;
		}
		transactionService.commit();
		if (!infoDisplayed && autoShowInfoDone) infoDone();

		if (!ui.isViewSet() && isRootAction()) showReturnView();

		finish();
	}

	private void finish() {
		finished = true;
		ActionPerformer.unregisterAction(getActionId());
		synchronized (this) {
			notifyAll();
		}
	}

	protected final void showReturnView() {
		String viewId = getReturnViewId();
		if (viewId.startsWith("entity:")) {
			ui.showView(daoService.getEntityById(viewId.substring(7)), null);
			return;
		}
		ui.showView(viewId, null);
	}

	public final boolean isActionFinished() {
		return finished;
	}

	public final Throwable getException() {
		return exception;
	}

	protected AAction parentAction;

	public final AAction getParentAction() {
		return parentAction;
	}

	private final boolean isRootAction() {
		return parentAction == null;
	}

	public final void setParentAction(AAction action) {
		this.parentAction = action;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	// --------------
	// --- helper ---
	// --------------

	protected String getReturnViewId() {
		if (returnToView != null) {
			if (isViewIdValid(returnToView)) return returnToView;
		}
		return "overview"; // TODO replace string
	}

	private final boolean isViewIdValid(String viewId) {
		if (viewId == null) return false;
		if ("option".equals(viewId)) return false;
		return true;
	}

	protected final <T> T autowire(T bean) {
		return beanProvider.autowire(bean);
	}

	// --- action ---

	protected final <A extends AAction> A createAction(Class<A> actionClass) {
		try {
			return beanProvider.autowire(actionClass.newInstance());
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

	protected final void performAction(AAction action) {
		actionPerformer.performSubAction(action, this);
	}

	// --- ui ---

	protected final void showDialog(Class<? extends AView> view) throws InterruptedException {
		try {
			ui.showDialog(view, this, this);
		} catch (DialogTimeoutException ex) {
			throw new ActionAbortedException(ex.getMessage());
		}
	}

	protected final void showDialog(Form form) throws InterruptedException {
		try {
			ui.showDialog("simpleForm", this, this);
		} catch (DialogTimeoutException ex) {
			throw new ActionAbortedException(ex.getMessage());
		}
	}

	protected final <T> void showDialog(Collection<Option<T>> options, String message) throws InterruptedException {
		try {
			ui.showDialog("option", this, this);
		} catch (DialogTimeoutException ex) {
			throw new ActionAbortedException(ex.getMessage());
		}
	}

	// --- inputAction ---

	protected final String showInputDialog() {
		return showInputDialog(getStringKeyPrefix(), null);
	}

	protected final String showInputDialog(String stringKeyPrefix, Validator validator) {
		InputAction action = beanProvider.autowire(new InputAction());
		action.setValidator(validator);
		action.setStringKeyPrefix(stringKeyPrefix);
		actionPerformer.performSubAction(action, this);
		return action.getInputString();
	}

	// --- optionAction ---

	protected final void showConfirmDialog(Object... messageParameters) {
		showOptionDialog("confirm", messageParameters);
	}

	protected final String showOptionDialog(String name, Object[] messageParameters, String... options) {
		if (options == null || options.length == 0) options = new String[] { "ok" };
		OptionAction<String> action = autowire(new OptionAction());
		action.setMessage(string(name + ".message", messageParameters));
		action.setHorizontal(true);
		for (String option : options) {
			action.addOption(new Option<String>(option, string(name + "." + option), option, option));
		}
		actionPerformer.performSubAction(action, this);
		return action.getSelectedPayload();
	}

	protected final <T> T showOptionDialog(String messageString, Collection<T> optionPayloads) {
		return OptionAction.showDialog(beanProvider, actionPerformer, this, string(messageString), optionPayloads);
	}

	protected final <T> Set<T> showMultiOptionDialog(String messageString, Collection<T> optionPayloads) {
		return MultiOptionAction.showDialog(beanProvider, actionPerformer, this, string(messageString), optionPayloads);
	}

	protected final <F extends Form> FormAction<F> showFormDialog(F form) {
		FormAction<F> action = new FormAction<F>();
		action.setForm(form);
		actionPerformer.performSubAction(action, this);
		return action;
	}

	// --- strings ---

	private String stringKeyPrefix;

	public void setStringKeyPrefix(String stringKeyPrefix) {
		this.stringKeyPrefix = stringKeyPrefix;
	}

	protected String getStringKeyPrefix() {
		return stringKeyPrefix != null ? stringKeyPrefix : getStringKey(getClass());
	}

	protected final String string(String key, Object... parameters) {
		return ui.getLocalizer().string(getStringKeyPrefix() + "." + key, parameters);
	}

	// --- user messages ---

	protected boolean infoDisplayed;

	protected final void infoDone(Object... parameters) {
		info("done", parameters);
	}

	protected final void info(String key, Object... parameters) {
		infoDisplayed = true;
		ui.getUserMessageService().info(string("info." + key, parameters));
	}

	protected final void error(Throwable t) {
		error("exception", Str.format(t));
	}

	protected final void error(String key, Object... parameters) {
		infoDisplayed = true;
		ui.getUserMessageService().error(string("error." + key, parameters));
	}

	protected final String getReturnToView() {
		return returnToView;
	}

	// ------------------
	// --- model data ---
	// ------------------

	public final String getResponseActionId() {
		return actionId;
	}

	// --------------------
	// --- dependencies ---
	// --------------------

	protected TransactionService transactionService;

	public final void setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	protected DaoService daoService;

	public final void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	private String actionId;

	public final String getActionId() {
		if (actionId == null) {
			actionId = actionIdGenerator.generateId() + "Action";
		}
		return actionId;
	}

	protected boolean autoShowInfoDone = true;

	public final void setAutoShowInfoDone(boolean autoShowInfoDone) {
		this.autoShowInfoDone = autoShowInfoDone;
	}

	protected ActionPerformer actionPerformer;

	public void setActionPerformer(ActionPerformer actionPerformer) {
		this.actionPerformer = actionPerformer;
	}

	private AUi ui;

	public AUi getUi() {
		return ui;
	}

	public final void setUi(AUi ui) {
		this.ui = ui;
	}

	protected Localizer localizer;

	public final void setLocalizer(Localizer localizer) {
		this.localizer = localizer;
	}

	protected BeanProvider beanProvider;

	public final void setBeanProvider(BeanProvider beanProvider) {
		this.beanProvider = beanProvider;
	}

	private String returnToView;

	public final void setReturnToView(String returnToView) {
		this.returnToView = returnToView;
	}

	// -------------
	// --- utils ---
	// -------------

	public static String getStringKey(Class<? extends AAction> actionClass) {
		return actionClass.getSimpleName();
	}

}
