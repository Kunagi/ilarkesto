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
package ilarkesto.gwt.client.animation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class AnimatingFlowPanel<W extends Widget> extends Composite implements HasWidgets {

	private static boolean animationsDisabled = false;

	private FlowPanel panel;
	private boolean actionRunning;
	private List<Runnable> actions = new LinkedList<Runnable>();
	private double animationDelayFactor = 2;

	public AnimatingFlowPanel() {
		panel = new FlowPanel();
		initWidget(panel);
	}

	public AnimatingFlowPanel(double animationDelayFactor) {
		this();
		this.animationDelayFactor = animationDelayFactor;
	}

	private void executeNextAction() {
		if (actionRunning) return;
		if (actions.isEmpty()) return;
		Runnable action = actions.get(0);
		actions.remove(action);
		action.run();
	}

	private void execute(Runnable action) {
		actions.add(action);
		executeNextAction();
	}

	public void insertAnimated(int index, W widget, Integer height, InsertCallback runAfter) {
		if (animationsDisabled) {
			insert(index, widget);
			return;
		}
		execute(new InsertAction(index, widget, height, runAfter));
	}

	public void insert(int index, W widget) {
		if (actionRunning) {
			execute(new InsertAction(index, widget));
		} else {
			if (index < 0) index = panel.getWidgetCount();
			panel.insert(widget, index);
		}
	}

	@Override
	public boolean remove(final Widget widget) {
		if (actionRunning) {
			execute(new RemoveAction(widget));
		} else {
			panel.remove(widget);
		}
		return true;
	}

	public void removeAnimated(final W widget) {
		if (animationsDisabled) {
			remove(widget);
			return;
		}
		execute(new RemoveAction(widget));
	}

	@Override
	public void clear() {
		panel.clear();
	}

	@Override
	public Iterator<Widget> iterator() {
		return panel.iterator();
	}

	@Override
	public void add(Widget w) {
		insertAnimated(-1, (W) w, null, null);
	}

	public void setAnimationDelayFactor(double animationDelayFactor) {
		this.animationDelayFactor = animationDelayFactor;
	}

	private class RemoveAction implements Runnable {

		private Widget widget;
		private boolean animated;

		public RemoveAction(Widget widget) {
			this.widget = widget;
			this.animated = true;
		}

		@Override
		public void run() {
			if (!animated) {
				panel.remove(widget);
				return;
			}

			actionRunning = true;
			DisappearAnimation animation = new DisappearAnimation(widget, animationDelayFactor);
			animation.addCompletionListener(new CompletionListener() {

				@Override
				public void completionEvent(AObservableAnimation source) {
					panel.remove(widget);
					actionRunning = false;
					executeNextAction();
				}
			});
			animation.run(250);

		}

	}

	private class InsertAction implements Runnable {

		private Widget widget;
		private int index;
		private boolean animated;
		private Integer animationHeight;
		private InsertCallback callback;

		public InsertAction(int index, Widget widget, Integer animationHeight, InsertCallback callback) {
			this.index = index;
			this.widget = widget;
			this.animationHeight = animationHeight;
			this.callback = callback;
			this.animated = true;
		}

		public InsertAction(int index, Widget widget) {
			this.index = index;
			this.widget = widget;
			this.animated = false;
		}

		@Override
		public void run() {
			if (index < 0) index = panel.getWidgetCount();

			if (!animated) {
				panel.insert(widget, index);
				return;
			}

			actionRunning = true;
			AppearAnimation animation = new AppearAnimation(animationHeight, widget, animationDelayFactor);
			panel.insert(widget, index);
			animation.addCompletionListener(new CompletionListener() {

				@Override
				public void completionEvent(AObservableAnimation source) {
					actionRunning = false;
					if (callback != null) callback.onInserted(index);
					executeNextAction();
				}
			});
			animation.run(250);
		}

	}

	public static interface MoveObserver {

		void onMoved();
	}

	public static interface InsertCallback {

		void onInserted(int index);

	}

}
