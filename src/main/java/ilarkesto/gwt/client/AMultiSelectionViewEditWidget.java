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

import ilarkesto.core.base.Str;
import ilarkesto.core.base.ToHtmlSupport;
import ilarkesto.core.base.Utl;

import java.util.Collection;
import java.util.List;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public abstract class AMultiSelectionViewEditWidget<I extends Object> extends AViewEditWidget {

	private HTML viewer;
	private MultiSelectionWidget<I> editor;

	@Override
	protected final Widget onViewerInitialization() {
		viewer = new HTML();
		return viewer;
	}

	@Override
	protected final Widget onEditorInitialization() {
		editor = new MultiSelectionWidget<I>() {

			@Override
			protected CheckBox createCheckbox(I item) {
				return new CheckBox(toHtml(item), true);
			}
		};

		ToolbarWidget toolbar = new ToolbarWidget();
		toolbar.addButton(new AAction() {

			@Override
			public String getLabel() {
				return "Apply";
			}

			@Override
			protected void onExecute() {
				submitEditor();
			}
		});
		toolbar.addButton(new AAction() {

			@Override
			public String getLabel() {
				return "Cancel";
			}

			@Override
			protected void onExecute() {
				cancelEditor();
			}
		});

		FlowPanel container = new FlowPanel();
		container.add(editor);
		Widget w = getExtendedEditorContent();
		if (w != null) container.add(w);
		container.add(toolbar);

		FocusPanel focusPanel = new FocusPanel(container);
		focusPanel.addFocusListener(new EditorFocusListener());

		return focusPanel;
	}

	protected Widget getExtendedEditorContent() {
		return null;
	}

	protected String toHtml(I item) {
		if (item == null) return null;
		if (item instanceof ToHtmlSupport) return ((ToHtmlSupport) item).toHtml();
		return Str.toHtml(item.toString());
	}

	public final void setViewerItems(Collection items, String separatorHtml) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object item : items) {
			if (first) {
				first = false;
			} else {
				sb.append(separatorHtml);
			}
			sb.append(Str.toHtml(item.toString()));
		}
		viewer.setHTML(sb.toString());
	}

	public final void setViewerItemsAsHtml(Collection<? extends ToHtmlSupport> items) {
		if (items.isEmpty()) {
			viewer.setText(".");
			return;
		}
		viewer.setHTML(Utl.concatToHtml(items, "<br>"));
	}

	public void setEditorItems(Collection<I> items) {
		editor.setItems(items);
	}

	public void setEditorSelectedItems(Collection<I> items) {
		editor.setSelected(items);
	}

	public List<I> getEditorSelectedItems() {
		return editor.getSelected();
	}

	protected MultiSelectionWidget<I> getEditor() {
		return editor;
	}

	private class EditorFocusListener implements FocusListener {

		@Override
		public void onFocus(Widget sender) {}

		@Override
		public void onLostFocus(Widget sender) {
			// submitEditor();
		}

	}
}
