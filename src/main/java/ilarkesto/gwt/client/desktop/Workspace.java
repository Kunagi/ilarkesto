/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.gwt.client.desktop;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.core.persistance.AEntity;
import ilarkesto.gwt.client.AAction;
import ilarkesto.gwt.client.AGwtApplication;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class Workspace {

	private static Log log = Log.get(Workspace.class);

	private String titleText;
	private String windowTitlePostfix;
	private InlineLabel titleLabel;
	private InlineLabel titleContentLabel;
	private SimplePanel contentWrapper;
	private HorizontalPanel actionbar;
	private ActionButton actionMenuButton;
	private FlowPanel breadcrumb;

	private ActionsMenuAction actionsMenu;

	private BreadcrumbHelper breadcrumbResolver;

	public Workspace(BreadcrumbHelper breadcrumbResolver) {
		this(breadcrumbResolver, null);
	}

	public Workspace(BreadcrumbHelper breadcrumbResolver, String windowTitlePostfix) {
		this.breadcrumbResolver = breadcrumbResolver;
		this.windowTitlePostfix = windowTitlePostfix;

		titleLabel = new InlineLabel();
		titleLabel.addStyleName("titleLabel");
		titleLabel.getElement().getStyle().setMarginRight(Widgets.defaultSpacing, Unit.PX);

		titleContentLabel = new InlineLabel();
		// titleContentLabel.getElement().getStyle().setColor("#ff7");
		titleContentLabel.setStyleName("titleContentLabel");

		breadcrumb = new FlowPanel();
		breadcrumb.addStyleName("breadcrumb");

		actionbar = new HorizontalPanel();
		// actionbar.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		actionbar.getElement().getStyle().setMarginTop(6, Unit.PX);

		actionsMenu = new ActionsMenuAction();
		actionMenuButton = new ActionButton(actionsMenu);
		actionMenuButton.getElement().getStyle().setMarginLeft(Widgets.defaultSpacing, Unit.PX);
		actionMenuButton.getElement().getStyle().setDisplay(Display.NONE);
		actionbar.add(actionMenuButton);

		contentWrapper = new SimplePanel();
		contentWrapper.getElement().getStyle().setWidth(100, Unit.PCT);
	}

	private Workspace addToActionbar(IsWidget widget, int index) {
		actionbar.insert(widget, index);
		return this;
	}

	public Workspace setTitle(String title, String contentTitle) {
		title = Str.getFirstLine(title);
		contentTitle = Str.getFirstLine(contentTitle);

		titleLabel.setText(title);
		titleContentLabel.setText(contentTitle);

		if (Str.isBlank(contentTitle)) {
			titleText = title;
		} else {
			titleText = contentTitle + " - " + title;
		}

		Window.setTitle(windowTitlePostfix == null ? titleText : titleText + windowTitlePostfix);

		return this;
	}

	public String getTitleText() {
		return titleText;
	}

	public final String getWindowTitlePostfix() {
		return windowTitlePostfix;
	}

	public void setBreadcrumb(List elements) {
		breadcrumb.clear();
		boolean first = true;
		for (Object element : elements) {
			if (first) {
				first = false;
			} else {
				Label separator = new Label(" > ");
				Style style = separator.getElement().getStyle();
				style.setFloat(Float.LEFT);
				style.setColor(Colors.greyedText);
				breadcrumb.add(separator);
			}
			addBreadcrumbLink(element);
		}
	}

	private void addBreadcrumbLink(Object o) {
		String text = "Startseite";
		String hashtag = "#Home";

		if (o instanceof AEntity) {
			AEntity entity = (AEntity) o;
			try {
				text = breadcrumbResolver.getBreadcrumbText(entity);
			} catch (Exception ex) {
				log.error(ex);
				text = entity.getId();
			}
			hashtag = AGwtApplication.get().getTokenForEntityActivity(entity);
		} else if (o instanceof Class) {
			Class<? extends AActivity> activity = (Class<? extends AActivity>) o;
			text = AGwtApplication.get().getActivityLinkText(activity);
			hashtag = AGwtNavigator.getActivityHashtag(activity, null, true);
		}

		addBreadcrumbLink(text, hashtag);
	}

	public void addBreadcrumbLink(String text, String href) {
		Hyperlink link = new Hyperlink(text, href);
		Style style = link.getElement().getStyle();
		style.setFloat(Float.LEFT);
		breadcrumb.add(link);
	}

	public Workspace setContent(IsWidget content) {
		contentWrapper.clear();
		contentWrapper.setWidget(content);
		return this;
	}

	IsWidget getTitleWidget() {
		FlowPanel panel = new FlowPanel();
		panel.add(breadcrumb);
		panel.add(Widgets.clear());
		panel.add(titleLabel);
		panel.add(titleContentLabel);
		return panel;
	}

	IsWidget getContentWrapper() {
		return contentWrapper;
	}

	IsWidget getActionbar() {
		return actionbar;
	}

	public void addMenuActions(AAction... actions) {
		for (AAction action : actions) {
			addMenuAction(action);
		}
	}

	public void addMenuAction(AAction action) {
		actionMenuButton.getElement().getStyle().setDisplay(Display.BLOCK);
		actionsMenu.addActions(action);
	}

	public void addToolbarAction(AAction action) {
		addToolbarAction(action, false);
	}

	public void addToolbarAction(AAction action, boolean positionAtFront) {
		if (action == null) return;
		// if (true) {
		// addMenuAction(action);
		// return;
		// }
		ActionButton button = new ActionButton(action);
		Style style = button.getElement().getStyle();
		style.setMarginLeft(Widgets.defaultSpacing, Unit.PX);
		if (action.getIcon() == null) style.setPadding(4, Unit.PX);
		// button.getElement().getStyle().setMargin(0, Unit.PX);

		int index = positionAtFront ? 0 : actionbar.getWidgetCount() - 1;
		addToActionbar(button, index);
	}

	public void createBreadcrumbForEntity(AEntity entity) {
		createBreadcrumbForEntity(entity, true);
	}

	public void createBreadcrumbForEntity(AEntity entity, boolean skipEntity) {
		LinkedList<AEntity> path = new LinkedList<AEntity>();
		AEntity parent = skipEntity ? breadcrumbResolver.getBreadcrumbParent(entity) : entity;
		while (parent != null) {
			path.addFirst(parent);
			parent = breadcrumbResolver.getBreadcrumbParent(parent);
		}
		setBreadcrumb(path);
	}

}
