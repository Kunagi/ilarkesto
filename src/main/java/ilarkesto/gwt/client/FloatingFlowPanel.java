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

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class FloatingFlowPanel extends Composite implements HasWidgets {

	private FlowPanel panel;
	private String styleName;

	public FloatingFlowPanel() {
		this("FloatingFlowPanel");
	}

	public FloatingFlowPanel(String styleName) {
		super();
		this.styleName = styleName;
		panel = new FlowPanel();
		panel.setStyleName(styleName);
		panel.add(Gwt.createFloatClear());
		initWidget(panel);
	}

	@Override
	public void setStyleName(String style) {
		this.styleName = style;
		panel.setStyleName(style);
	}

	@Override
	public Iterator<Widget> iterator() {
		return panel.iterator();
	}

	@Override
	public void clear() {
		panel.clear();
		panel.add(Gwt.createFloatClear());
	}

	public boolean isEmpty() {
		return panel.getWidgetCount() <= 1;
	}

	@Override
	public void add(Widget w) {
		add(w, false);
	}

	public void add(Widget w, boolean right) {
		SimplePanel element = Gwt.createDiv(styleName + "-element-" + (right ? "right" : "left"), w);
		panel.insert(element, panel.getWidgetCount() - 1);
	}

	public void insert(Widget w, int index) {
		SimplePanel element = Gwt.createDiv(styleName + "-element-left", w);
		panel.insert(element, index);
	}

	@Override
	public boolean remove(Widget w) {
		return panel.remove(w);
	}

	@Override
	public String toString() {
		return Gwt.toString(this);
	}

}
