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

import ilarkesto.gwt.client.animation.AnimatingFlowPanel;
import ilarkesto.gwt.client.animation.AnimatingFlowPanel.InsertCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class ObjectMappedFlowPanel<O extends Object, W extends Widget> extends Composite implements HasWidgets {

	public static Map<Object, Integer> objectHeights = new HashMap<Object, Integer>();

	private AnimatingFlowPanel<W> panel;
	private WidgetFactory<O, W> widgetFactory;
	private MoveObserver<O, W> moveObserver;

	private List<O> objectList;
	private Map<O, W> widgetMap;
	private boolean virgin = true;

	public ObjectMappedFlowPanel(WidgetFactory<O, W> widgetFactory) {
		this.widgetFactory = widgetFactory;
		objectList = new ArrayList<O>();
		widgetMap = new HashMap<O, W>();
		panel = new AnimatingFlowPanel<W>();
		initWidget(panel);
	}

	public void set(List<O> newObjects) {
		boolean animationAllowed = !virgin;
		virgin = false;
		if (objectList.equals(newObjects)) return;

		// remove old objects
		List<O> objectsToRemove = getOterObjects(newObjects);
		if (!objectsToRemove.isEmpty()) {
			boolean animate = animationAllowed && objectsToRemove.size() == 1;
			for (O object : objectsToRemove) {
				remove(object, animate);
			}
		}

		if (objectList.equals(newObjects)) return;

		// add new objects
		List<O> objectsToAdd = new ArrayList<O>(newObjects);
		objectsToAdd.removeAll(objectList);
		if (!objectsToAdd.isEmpty()) {
			boolean animate = animationAllowed && objectsToAdd.size() == 1;
			for (O object : objectsToAdd) {
				int index = newObjects.indexOf(object);
				if (index > objectList.size()) {
					index = objectList.size();
				}
				insert(index, object, animate, null);
			}
		}

		if (objectList.equals(newObjects)) return;

		// move existing objects
		int index = 0;
		for (O object : newObjects) {
			int currentIndex = objectList.indexOf(object);
			if (currentIndex != index) {
				move(index, object, false, null);
			}
			index++;
		}

		assert objectList.equals(newObjects);
	}

	private W insert(int index, O object, boolean animate, InsertCallback callback) {
		assert object != null;
		assert !objectList.contains(object);
		assert objectHeights != null;
		assert panel != null;
		W widget = createWidget(object);
		if (animate) {
			panel.insertAnimated(index, widget, objectHeights.get(object), callback);
		} else {
			panel.insert(index, widget);
			if (callback != null) callback.onInserted(index);
		}
		objectList.add(index, object);
		assert objectList.contains(object);
		assert objectList.size() == widgetMap.size();
		return widget;
	}

	private W remove(O object, boolean animate) {
		assert containsObject(object);
		W widget = getWidget(object);
		if (animate) {
			int height = widget.getElement().getOffsetHeight();
			objectHeights.put(object, height);
			panel.removeAnimated(widget);
		} else {
			panel.remove(widget);
		}
		objectList.remove(object);
		widgetMap.remove(object);
		assert !containsObject(object);
		assert objectList.size() == widgetMap.size();
		return widget;
	}

	public W move(int toIndex, O object, boolean animate, InsertCallback callback) {
		assert toIndex >= 0 && toIndex <= objectList.size();
		assert objectList.contains(object);

		W oldWidget = remove(object, animate);
		W newWidget = insert(toIndex, object, animate, callback);
		if (moveObserver != null) moveObserver.moved(object, oldWidget, newWidget);

		assert objectList.size() == widgetMap.size();
		return newWidget;
	}

	@Override
	public void clear() {
		panel.clear();
		objectList.clear();
		widgetMap.clear();
	}

	private List<O> getOterObjects(Collection<O> objects) {
		List<O> ret = new ArrayList<O>();
		for (O object : objectList) {
			if (!objects.contains(object)) ret.add(object);
		}
		return ret;
	}

	public List<O> getObjects() {
		return objectList;
	}

	public Collection<W> getWidgets() {
		return widgetMap.values();
	}

	public int indexOfObject(O object) {
		return objectList.indexOf(object);
	}

	public W getWidget(int index) {
		return getWidget(objectList.get(index));
	}

	public W getWidget(O object) {
		assert object != null;
		W widget = widgetMap.get(object);
		assert widget != null;
		return widget;
	}

	private W createWidget(O object) {
		W widget = widgetFactory.createWidget(object);
		widgetMap.put(object, widget);
		return widget;
	}

	public boolean containsObject(O object) {
		return objectList.contains(object);
	}

	public int size() {
		return objectList.size();
	}

	@Override
	public Iterator<Widget> iterator() {
		return panel.iterator();
	}

	@Override
	public void add(Widget w) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public boolean remove(Widget w) {
		return false;
	}

	public void setMoveObserver(MoveObserver<O, W> moveObserver) {
		this.moveObserver = moveObserver;
	}

	public static interface WidgetFactory<O extends Object, W extends Widget> {

		W createWidget(O object);

	}

	public static interface MoveObserver<O extends Object, W extends Widget> {

		void moved(O object, W oldWidget, W newWidget);

	}

}
