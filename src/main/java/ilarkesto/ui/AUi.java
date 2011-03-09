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
package ilarkesto.ui;

import ilarkesto.base.Reflect;
import ilarkesto.base.Str;
import ilarkesto.base.Tm;
import ilarkesto.base.Url;
import ilarkesto.core.logging.Log;
import ilarkesto.di.BeanContainer;
import ilarkesto.di.BeanProvider;
import ilarkesto.di.MultiBeanProvider;
import ilarkesto.di.ReflectionBeanProvider;
import ilarkesto.locale.Localizer;
import ilarkesto.persistence.AEntity;
import ilarkesto.ui.action.AAction;
import ilarkesto.ui.usermessage.UserMessageService;

public abstract class AUi {

	private static final Log LOG = Log.get(AUi.class);

	public static final String PARENT_VIEW_ID_PARAMETER = "parentViewId";
	public static final String VIEW_ID_PARAMETER = "viewId";
	public static final String URI_EXTENSION = ".xhtml";

	public static final long DEFAULT_DIALOG_TIMEOUT = 1 * Tm.HOUR;

	protected BeanProvider model;

	// --- dependencies ---

	private UserMessageService userMessageService;

	public void setUserMessageService(UserMessageService userMessageService) {
		this.userMessageService = userMessageService;
	}

	protected Localizer localizer;

	public final void setLocalizer(Localizer localizer) {
		this.localizer = localizer;
	}

	public abstract void showView(Class<? extends AView> view);

	@Deprecated
	protected abstract void showView(String viewId);

	protected abstract void showDialog(Class<? extends AView> view);

	@Deprecated
	protected abstract void showDialog(String viewId);

	protected abstract Class<? extends AView> getEntityView();

	public abstract void showWebPage(Url url);

	public abstract boolean isViewSet();

	// --- ---

	public final void showView(Class<? extends AView> view, Object model) {
		setModel(model);
		showView(view);
	}

	public final void showView(AEntity entity, Object model) {
		if (entity == null) throw new IllegalArgumentException("entity == null");
		MultiBeanProvider multiBeanProvider = new MultiBeanProvider();
		multiBeanProvider.addBeanProvider(new ReflectionBeanProvider(model));
		multiBeanProvider.addBeanProvider(new BeanContainer().put("entity", entity));
		showView(getEntityView(), multiBeanProvider);
	}

	@Deprecated
	public final void showView(String viewId, Object model) {
		setModel(model);
		showView(viewId);
	}

	public final void showDialog(Class<? extends AView> view, Object model, Object blocker)
			throws DialogTimeoutException, InterruptedException {
		showDialog(view, model, blocker, DEFAULT_DIALOG_TIMEOUT);
	}

	@Deprecated
	public final void showDialog(String viewId, Object model, Object blocker) throws DialogTimeoutException,
			InterruptedException {
		showDialog(viewId, model, blocker, DEFAULT_DIALOG_TIMEOUT);
	}

	public final void showDialog(Class<? extends AView> view, Object model, Object blocker, long timeout)
			throws InterruptedException, DialogTimeoutException {
		setModel(model);
		showDialog(view);
		if (blocker != null) {
			long waitStart = System.currentTimeMillis();
			// block until actionProvider triggers notify() (by next request)
			synchronized (blocker) {
				// TODO while (blocker.isNotReady())
				blocker.wait(timeout);
			}
			if (timeout != 0) {
				timeout = Math.round(timeout * 0.95);
				long duration = System.currentTimeMillis() - waitStart;
				if (duration >= timeout) {
					LOG.info("Dialog timeout:", view);
					throw new DialogTimeoutException(view.getSimpleName());
				}
			}
		}
	}

	public final void showDialog(String viewId, Object model, Object blocker, long timeout)
			throws InterruptedException, DialogTimeoutException {
		setModel(model);
		showDialog(viewId);
		if (blocker != null) {
			long waitStart = System.currentTimeMillis();
			// block until actionProvider triggers notify() (by next request)
			synchronized (blocker) {
				// TODO while (blocker.isNotReady())
				blocker.wait(timeout);
			}
			if (timeout != 0) {
				timeout = Math.round(timeout * 0.95);
				long duration = System.currentTimeMillis() - waitStart;
				if (duration >= timeout) {
					LOG.info("Dialog timeout:", viewId);
					throw new DialogTimeoutException(viewId);
				}
			}
		}
	}

	private void setModel(Object model) {
		if (model != null) {
			this.model = model instanceof BeanProvider ? ((BeanProvider) model) : new ReflectionBeanProvider(model);
		}
	}

	public final BeanProvider getModel() {
		return model;
	}

	public final UserMessageService getUserMessageService() {
		if (userMessageService == null) {
			userMessageService = new UserMessageService();
		}
		return userMessageService;
	}

	public final Localizer getLocalizer() {
		return localizer;
	}

	// --- helper ---

	public static String getViewId(AEntity entity) {
		return "entity:" + entity.getId();
	}

	public static String getViewIcon(Class<? extends AView> viewClass) {
		String icon = (String) Reflect.getFieldValue(viewClass, "ICON");
		if (icon != null) return icon;
		return getViewId(viewClass);
	}

	public static String getViewId(Class<? extends AView> viewClass) {
		String id = viewClass.getSimpleName();
		id = Str.removeSuffix(id, "WebView");
		id = Str.lowercaseFirstLetter(id);
		return id;
	}

	public static Url getViewUrl(Class<? extends AView> viewClass) {
		return new Url(getViewId(viewClass));
	}

	@Deprecated
	public static Url getViewUrl(String viewId) {
		return new Url(viewId);
	}

	public static Url getViewUrl(AEntity entity) {
		return new Url(entity.getId());
	}

	public static Url createViewUrl(AEntity entity, String invokingViewId, String invokingViewParentViewId) {
		Url url = AUi.getViewUrl(entity);
		return url;
	}

	public static Url createActionUrl(String actionId) {
		return new Url(actionId);
	}

	public static Url createActionUrl(Class<? extends AAction> actionClass, AEntity entity) {
		return createActionUrl(actionClass).put("entity", entity.getId());
	}

	public static Url createActionUrl(Class<? extends AAction> actionClass) {
		return createActionUrl(actionClass.getName());
	}

}
