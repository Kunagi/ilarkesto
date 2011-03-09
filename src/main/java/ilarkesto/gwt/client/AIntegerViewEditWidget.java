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

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AIntegerViewEditWidget extends AViewEditWidget {

	private Label viewer;
	private TextBox editor;
	private ButtonWidget minus;
	private ButtonWidget plus;
	private SimplePanel wrapper;
	private HorizontalPanel panel;

	protected abstract void onMinusClicked();

	protected abstract void onPlusClicked();

	protected abstract void onIntegerViewerUpdate();

	@Override
	protected final Widget onViewerInitialization() {
		viewer = new Label();

		minus = new ButtonWidget(new MinusAction());
		plus = new ButtonWidget(new PlusAction());

		wrapper = new SimplePanel();
		wrapper.setStyleName("AIntegerViewEditWidget");

		return wrapper;
	}

	@Override
	protected final void onViewerUpdate() {
		boolean editable = isEditable();
		if (panel == null || (editable && panel.getWidgetCount() < 3) || (!editable && panel.getWidgetCount() > 1)) {
			panel = new HorizontalPanel();
			if (editable) {
				panel.add(minus.update());
				panel.add(Gwt.createNbsp());
			}
			panel.add(viewer);
			if (editable) {
				panel.add(Gwt.createNbsp());
				panel.add(plus.update());
			}
			wrapper.setWidget(panel);
		}
		onIntegerViewerUpdate();
	}

	@Override
	protected final Widget onEditorInitialization() {
		editor = new TextBox();
		editor.setMaxLength(10);
		editor.setWidth("50px");
		editor.addFocusListener(new EditorFocusListener());
		editor.addKeyPressHandler(new EditorKeyboardListener());
		return editor;
	}

	public final void setViewerText(String text) {
		if (Str.isBlank(text)) text = ".";
		viewer.setText(text);
	}

	public final void setViewerValue(Integer value) {
		setViewerValue(value, null);
	}

	public final void setViewerValue(Integer value, String suffix) {
		String text = null;
		if (value != null) {
			text = value.toString();
			if (suffix != null) text += " " + suffix;
		}
		setViewerText(text);
	}

	public final void setEditorValue(Integer value) {
		editor.setText(value == null ? null : value.toString());
		editor.setSelectionRange(0, editor.getText().length());
		editor.setFocus(true);
	}

	public final Integer getEditorValue() {
		String text = editor.getText();
		if (text == null) {
			return null;
		} else {
			text = text.trim();
			if (text.length() == 0) {
				return null;
			} else {
				try {
					return Integer.parseInt(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}
	}

	public final int getEditorValue(int alternativeValueForNull) {
		Integer value = getEditorValue();
		return value == null ? alternativeValueForNull : value;
	}

	private void plus() {
		if (!isEditable()) return;
		onPlusClicked();
	}

	private void minus() {
		if (!isEditable()) return;
		onMinusClicked();
	}

	private class EditorKeyboardListener implements KeyPressHandler {

		@Override
		public void onKeyPress(KeyPressEvent event) {
			char keyCode = event.getCharCode();

			if (isCancelKey(keyCode)) {
				editor.cancelKey();
			}

			if (keyCode == KeyCodes.KEY_ENTER) {
				submitEditor();
			} else if (keyCode == KeyCodes.KEY_ESCAPE) {
				cancelEditor();
			}
		}

		private boolean isCancelKey(char keyCode) {
			boolean chancelKey = true;

			chancelKey &= Character.isDigit(keyCode) == false;
			chancelKey &= keyCode != (char) KeyCodes.KEY_ENTER;
			chancelKey &= (keyCode != (char) KeyCodes.KEY_TAB);
			chancelKey &= (keyCode != (char) KeyCodes.KEY_BACKSPACE);
			chancelKey &= (keyCode != (char) KeyCodes.KEY_DELETE);
			chancelKey &= (keyCode != (char) KeyCodes.KEY_ESCAPE);
			chancelKey |= (Character.valueOf(keyCode) == 46); // 46 = "."

			return chancelKey;
		}
	}

	private class EditorFocusListener implements FocusListener {

		@Override
		public void onFocus(Widget sender) {}

		@Override
		public void onLostFocus(Widget sender) {
			submitEditor();
		}

	}

	private class MinusAction extends AAction {

		@Override
		public String getLabel() {
			return "-";
		}

		@Override
		protected void onExecute() {
			minus();
			update();
		}
	}

	private class PlusAction extends AAction {

		@Override
		public String getLabel() {
			return "+";
		}

		@Override
		protected void onExecute() {
			plus();
			update();
		}
	}

}
