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
package ilarkesto.gwt.client;

import ilarkesto.core.base.ToHtmlSupport;
import ilarkesto.core.base.Utl;
import ilarkesto.gwt.client.editor.RichtextEditorWidget;
import ilarkesto.gwt.client.undo.UndoManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Gwt {

	private static Widget rootWidget;
	private static RichtextFormater defaultRichtextFormater = new DoNothingRichtextFormater();
	private static Initializer<RichtextEditorWidget> richtextEditorEditInitializer;
	private static String defaultRichtextSyntaxInfo;
	private static UndoManager undoManager = new UndoManager();

	private static DateTimeFormat dtfWeekdayShort;
	private static DateTimeFormat dtfDay;
	private static DateTimeFormat dtfWeekdayMonthDay;
	private static DateTimeFormat dtfHourMinute;

	public static boolean contains(HasWidgets container, Widget widget) {
		Iterator<Widget> iterator = container.iterator();
		while (iterator.hasNext()) {
			if (iterator.next() == widget) return true;
		}
		return false;
	}

	public static String formatWeekdayMonthDay(Date date) {
		if (dtfWeekdayMonthDay == null) dtfWeekdayMonthDay = DateTimeFormat.getFormat("EEEE, MMMM d.");
		return dtfWeekdayMonthDay.format(date);
	}

	public static String formatHourMinute(Date date) {
		if (dtfHourMinute == null) dtfHourMinute = DateTimeFormat.getFormat("HH:mm");
		return dtfHourMinute.format(date);
	}

	public static String formatDay(Date date) {
		if (dtfDay == null) dtfDay = DateTimeFormat.getFormat("dd.");
		return dtfDay.format(date);
	}

	public static String formatWeekdayShort(Date date) {
		if (dtfWeekdayShort == null) dtfWeekdayShort = DateTimeFormat.getFormat("EEE");
		return dtfWeekdayShort.format(date);
	}

	public static TextBox createTextBox(String id, String value, int width) {
		return createTextBox(id, id, value, width + "px");
	}

	public static TextBox createTextBox(String id, String name, String value, String width) {
		TextBox textBox = new TextBox();
		textBox.getElement().setId(id);
		textBox.setName(name);
		textBox.setValue(value);
		textBox.setWidth(width);
		return textBox;
	}

	public static TextArea createTextArea(String id, String value, int width, int height) {
		return createTextArea(id, id, value, width + "px", height + "px");
	}

	public static TextArea createTextArea(String id, String name, String value, String width, String height) {
		TextArea textArea = new TextArea();
		textArea.getElement().setId(id);
		textArea.setName(name);
		textArea.setValue(value);
		textArea.setWidth(width);
		textArea.setHeight(height);
		return textArea;
	}

	public static SubmitButton createInvisibleSubmitButton() {
		SubmitButton button = new SubmitButton();
		button.setVisible(false);
		return button;
	}

	public static HTML addHtmlTooltip(SourcesMouseEvents widget, String tooltip) {
		HTML html = new HTML(tooltip);
		TooltipListener listener = new TooltipListener(html);
		widget.addMouseListener(listener);
		return html;
	}

	public static boolean confirm(String message) {
		return Window.confirm(message);
	}

	public static String prompt(String message, String value) {
		return Window.prompt(message, value);
	}

	public static String escapeHtml(String maybeHtml) {
		final Element div = DOM.createDiv();
		DOM.setInnerText(div, maybeHtml);
		return DOM.getInnerHTML(div);
	}

	// public static String escapeHtml(String s) {
	// if (s == null) return null;
	// s = s.replace("&", "&amp;");
	// s = s.replace("<", "&lt;");
	// s = s.replace(">", "&gt;");
	// s = s.replace("\"", "&quot;");
	// return s;
	// }

	public static boolean equals(Object a, Object b) {
		if (a == b) return true;
		if (a == null && b == null) return true;
		if (a != null) return a.equals(b);
		return b.equals(a);
	}

	public static UndoManager getUndoManager() {
		return undoManager;
	}

	public static String getMonthShort(int month) {
		switch (month) {
			case (1):
				return "Jan";
			case (2):
				return "Feb";
			case (3):
				return "Mar";
			case (4):
				return "Apr";
			case (5):
				return "May";
			case (6):
				return "Jun";
			case (7):
				return "Jul";
			case (8):
				return "Aug";
			case (9):
				return "Sep";
			case (10):
				return "Oct";
			case (11):
				return "Nov";
			case (12):
				return "Dec";
		}

		return "Invalid Month";
	}

	public static int percent(int total, int quotient) {
		return (quotient * 100) / total;
	}

	public static Predicate predicate(boolean value) {
		return value ? Predicate.TRUE : Predicate.FALSE;
	}

	public static Widget createToHtmlItemsWidget(Collection<? extends ToHtmlSupport> items) {
		return new HTML(Utl.concatToHtml(items, "<br>"));
	}

	public static HTML createServletDownloadLink(String relativeHref, String text) {
		return createServletLink(relativeHref, text, true);
	}

	public static HTML createServletLink(String relativeHref, String text, boolean targetBlank) {
		return createHyperlink(GWT.getModuleBaseURL() + relativeHref, text, targetBlank);
	}

	public static HTML createHyperlink(String href, String text, boolean targetBlank) {
		StringBuilder sb = new StringBuilder();
		sb.append("<a href='").append(href).append("'");
		if (targetBlank) sb.append(" target='_blank'");
		sb.append(">").append(text).append("</a>"); // TODO escape html
		return new HTML(sb.toString());
	}

	public static String toString(Object o) {
		if (o == null) return "<null>";
		if (o instanceof List) return o.toString();
		return o.toString();
	}

	public static Label createInline(String text) {
		Label label = new Label(text);
		label.getElement().getStyle().setProperty("display", "inline");
		return label;
	}

	public static void runLater(long delayInMillis, final Runnable action) {
		new Timer() {

			@Override
			public void run() {
				action.run();
			}
		}.schedule((int) delayInMillis);
	}

	public static void setRichtextEditorEditInitializer(
			Initializer<RichtextEditorWidget> richtextEditorToolbarInitializer) {
		Gwt.richtextEditorEditInitializer = richtextEditorToolbarInitializer;
	}

	public static void setDefaultRichtextFormater(RichtextFormater defaultRichtextFormater) {
		Gwt.defaultRichtextFormater = defaultRichtextFormater;
	}

	public static Initializer<RichtextEditorWidget> getRichtextEditorEditInitializer() {
		return richtextEditorEditInitializer;
	}

	public static RichtextFormater getDefaultRichtextFormater() {
		return defaultRichtextFormater;
	}

	public static String getDefaultRichtextSyntaxInfo() {
		return defaultRichtextSyntaxInfo;
	}

	public static void setDefaultRichtextSyntaxInfo(String defaultRichtextSyntaxInfo) {
		Gwt.defaultRichtextSyntaxInfo = defaultRichtextSyntaxInfo;
	}

	public static void setRootWidget(Widget rootWidget) {
		Gwt.rootWidget = rootWidget;
	}

	public static Widget getRootWidget() {
		return rootWidget;
	}

	public static String toString(Widget widget) {
		if (widget == null) return "<null>";
		if (widget instanceof AWidget) return widget.toString();
		if (widget instanceof HasWidgets) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			sb.append(getSimpleName(widget.getClass())).append("(");
			for (Widget subWidget : (HasWidgets) widget) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(toString(subWidget));
			}
			sb.append(")");
		}
		return getSimpleName(widget.getClass());
	}

	public static String formatHours(Integer i) {
		if (i == null || i == 0) return "nothing";
		if (i == 1) return "1 hour";
		return i + " hours";
	}

	public static Label createFieldLabel(String text) {
		Label label = new Label(text);
		label.setStyleName("fieldLabel");
		return label;
	}

	public static void update(Collection<Widget> widgets) {
		for (Widget widget : widgets) {
			if (widget == null) continue;
			if (widget instanceof AWidget) ((Updatable) widget).update();
		}
	}

	public static void update(Widget... widgets) {
		for (Widget widget : widgets) {
			update(widget);
		}
	}

	private static void update(HasWidgets container) {
		for (Widget widget : container) {
			update(widget);
		}
	}

	public static <W extends Widget> W update(W widget) {
		if (widget == null) return null;
		if (widget instanceof AWidget) {
			// GwtLogger.DEBUG("Updating:", widget);
			((Updatable) widget).update();
			return widget;
		}
		if (widget instanceof HasWidgets) {
			update((HasWidgets) widget);
			return widget;
		}
		return widget;
	}

	public static Widget createBugMarker(String text) {
		Label label = new Label(text);
		label.setStyleName("BugMarker");
		return label;
	}

	public static <O extends Object> List<O> toList(Collection<O> collection) {
		if (collection instanceof List) return (List<O>) collection;
		return new ArrayList<O>(collection);
	}

	public static <O extends Object> List<O> toList(O... objects) {
		ArrayList<O> list = new ArrayList<O>(objects.length);
		for (O o : objects) {
			list.add(o);
		}
		return list;
	}

	public static HorizontalPanel createHorizontalPanel(int spacing, Widget... widgets) {
		assert (widgets.length > 0);
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("100%");
		boolean first = true;
		for (Widget widget : widgets) {
			if (first) {
				first = false;
			} else {
				if (spacing > 0) {
					Widget spacer = createEmptyDiv("HorizontalPanel-spacer");
					panel.add(spacer);
					panel.setCellWidth(spacer, spacing + "px");

				}
			}
			panel.add(widget);
		}
		return panel;
	}

	public static SimplePanel createSpacer(int width, int height) {
		SimplePanel spacer = new SimplePanel();
		spacer.getElement().getStyle().setPropertyPx("lineHeight", 1);
		spacer.setSize(width + "px", height + "px");
		return spacer;
	}

	public static FloatingFlowPanel createFloatingFlowPanel(Widget... widgets) {
		FloatingFlowPanel panel = new FloatingFlowPanel();
		for (Widget widget : widgets) {
			panel.add(widget);
		}
		return panel;
	}

	public static FloatingFlowPanel createFloatingFlowPanelRight(Widget... widgets) {
		FloatingFlowPanel panel = new FloatingFlowPanel();
		for (Widget widget : widgets) {
			panel.add(widget, true);
		}
		return panel;
	}

	public static FlowPanel createFlowPanel(Widget... widgets) {
		return createFlowPanel(null, null, widgets);
	}

	public static FlowPanel createFlowPanel(String styleName, String elementStyleName, Widget... widgets) {
		FlowPanel panel = new FlowPanel();
		if (styleName != null) panel.setStyleName(styleName);
		for (Widget widget : widgets) {
			panel.add(elementStyleName == null ? widget : createDiv(elementStyleName, widget));
		}
		return panel;
	}

	public static Widget createNbsp() {
		return new HTML("&nbsp;");
	}

	public static Set<String> getIdsAsSet(Collection<? extends AGwtEntity> entities) {
		if (entities == null) return Collections.emptySet();
		Set<String> ret = new HashSet<String>(entities.size());
		for (AGwtEntity entity : entities) {
			ret.add(entity.getId());
		}
		return ret;
	}

	public static List<String> getIdsAsList(Collection<? extends AGwtEntity> entities) {
		List<String> ret = new ArrayList<String>(entities.size());
		for (AGwtEntity entity : entities) {
			ret.add(entity.getId());
		}
		return ret;
	}

	public static void scrollTo(Widget w) {
		if (w == null) return;
		w.getElement().scrollIntoView();
	}

	public static native void scrollTo(int posY)
	/*-{
		$wnd.scrollTo(0, posY);
	}-*/;

	public static final Widget createEmptyDiv() {
		return new SimplePanel();
	}

	public static final Widget createEmptyDiv(String styleName) {
		SimplePanel div = new SimplePanel();
		div.setStyleName(styleName);
		return div;
	}

	public static final Widget createFloatClear() {
		return createEmptyDiv("floatClear");
	}

	public static final FormPanel createForm(Widget content) {
		FormPanel form = new FormPanel();
		form.add(content);
		return form;
	}

	public static final Widget createCenterer(Widget content) {
		TableBuilder tb = new TableBuilder();
		tb.setCentered(true);
		tb.add(content);
		return tb.createTable();
	}

	public static final SimplePanel createDiv(String styleName, Widget content) {
		SimplePanel div = new SimplePanel();
		div.setStyleName(styleName);
		div.setWidget(content);
		return div;
	}

	public static final Widget createDiv(String styleName, String labelText) {
		if (labelText == null) return createEmptyDiv(styleName);
		return createDiv(styleName, new Label(labelText));
	}

	public static String getSimpleName(Class<?> type) {
		String name = type.getName();
		name = name.substring(name.lastIndexOf('.') + 1);
		return name;
	}

	public static class DoNothingRichtextFormater implements RichtextFormater {

		@Override
		public String richtextToHtml(String s) {
			return s;
		}

	}

}
