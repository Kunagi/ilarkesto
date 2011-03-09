/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.gwt.client.editor;

import ilarkesto.core.base.Str;
import ilarkesto.gwt.client.AAction;
import ilarkesto.gwt.client.AViewEditWidget;
import ilarkesto.gwt.client.CodemirrorEditorWidget;
import ilarkesto.gwt.client.Gwt;
import ilarkesto.gwt.client.Initializer;
import ilarkesto.gwt.client.RichtextFormater;
import ilarkesto.gwt.client.ToolbarWidget;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class RichtextEditorWidget extends AViewEditWidget {

	private HTML viewer;
	private SimplePanel editorWrapper;
	private CodemirrorEditorWidget editor;
	private String editorHeight = "300px";
	private ToolbarWidget editorToolbar;
	private String applyButtonLabel = "Apply";
	private String restoreText;

	private ATextEditorModel model;
	private ToolbarWidget bottomToolbar;

	public RichtextEditorWidget(ATextEditorModel model) {
		super();
		this.model = model;
	}

	@Override
	protected void onUpdate() {
		if (editor != null && editor.isBurned()) {
			editor = null;
			cancelEditor();
			return;
		}
		super.onUpdate();
	}

	@Override
	protected void onViewerUpdate() {
		setViewerText(model.getValue());
	}

	@Override
	protected void onEditorUpdate() {
		if (editor != null && editor.isBurned()) editor = null;
		if (editor == null) {
			editor = new CodemirrorEditorWidget();
			// editor.setObserver(new CodemirrorEditorWidget.Observer() {
			//
			// @Override
			// public void onCodemirrorDetach() {
			// editorWrapper.setWidget(null);
			// editor = null;
			// switchToViewMode(false);
			// }
			// });
			// editor.addFocusListener(new EditorFocusListener());
			editor.addKeyPressHandler(new EditorKeyboardListener());
			editor.ensureDebugId("richtext-id");
			editor.setStyleName("ARichtextViewEditWidget-editor");
			// editor.setWidth("97%");
			if (editorHeight != null) editor.setHeight(editorHeight);
			editor.initialize();

			String text = model.getValue();
			String template = model.getTemplate();
			if (template != null && Str.isBlank(text)) text = template;
			editor.setText(text);
			editorWrapper.setWidget(editor);
		}
		editor.focus();
		editor.update();
		bottomToolbar.update();
	}

	@Override
	protected void focusEditor() {
		editor.focus();
	}

	@Override
	protected void onEditorSubmit() {
		String value = getEditorText();
		// TODO check lenght
		// TODO check format/syntax
		model.changeValue(value);
		// TODO catch exceptions
	}

	@Override
	protected final Widget onViewerInitialization() {
		// viewer = new Label();
		viewer = new HTML();
		viewer.setStyleName("ARichtextViewEditWidget-viewer");
		return viewer;
	}

	protected void armToolbar(ToolbarWidget toolbar) {
		String syntaxInfoHtml = getSyntaxInfo();
		if (syntaxInfoHtml != null) {
			Label syntaxInfo = new Label("Syntax Info");
			syntaxInfo.getElement().getStyle().setMargin(5, Unit.PX);
			Gwt.addHtmlTooltip(syntaxInfo, syntaxInfoHtml);
			toolbar.add(syntaxInfo);
		}
	}

	public void setApplyButtonLabel(String applyButtonLabel) {
		this.applyButtonLabel = applyButtonLabel;
	}

	@Override
	protected final Widget onEditorInitialization() {
		editorWrapper = new SimplePanel();

		editorToolbar = new ToolbarWidget();
		armToolbar(editorToolbar);

		bottomToolbar = new ToolbarWidget();
		bottomToolbar.addButton(new AAction() {

			@Override
			public String getLabel() {
				return applyButtonLabel;
			}

			@Override
			protected void onExecute() {
				submitEditor();
			}
		});
		bottomToolbar.addButton(new AAction() {

			@Override
			public String getLabel() {
				return "Cancel";
			}

			@Override
			protected void onExecute() {
				cancelEditor();
			}
		});
		bottomToolbar.addHyperlink(new RestoreAction());

		// toolbar.add(Gwt
		// .createHyperlink("http://en.wikipedia.org/wiki/Wikipedia:Cheatsheet", "Syntax Cheatsheet", true));

		FlowPanel editorPanel = new FlowPanel();
		editorPanel.setStyleName("AEditableTextareaWidget-editorPanel");
		if (!editorToolbar.isEmpty()) editorPanel.add(editorToolbar.update());

		editorPanel.add(editorWrapper);
		editorPanel.add(bottomToolbar.update());

		Initializer<RichtextEditorWidget> initializer = Gwt.getRichtextEditorEditInitializer();
		if (initializer != null) initializer.initialize(this);

		return editorPanel;
	}

	@Override
	protected void onEditorClose() {
		super.onEditorClose();
		editor = null;
		editorWrapper.clear();
	}

	@Override
	protected void onSwitchToEditModeCompleted() {
		super.onSwitchToEditModeCompleted();
		if (!Str.isBlank(restoreText)) {
			onEditorUpdate();
		}
	}

	public ToolbarWidget getEditorToolbar() {
		return editorToolbar;
	}

	public CodemirrorEditorWidget getEditor() {
		return editor;
	}

	public final void setViewerText(String text) {
		if (Str.isBlank(text)) {
			viewer.setHTML(".");
			return;
		}
		String html = getRichtextFormater().richtextToHtml(text);
		viewer.setHTML(html);
	}

	// public final void setViewerHtml(String html) {
	// if (Str.isBlank(html)) html = ".";
	// viewer.setHTML(html);
	// }

	@Override
	protected void closeEditor() {
		boolean submit = Gwt.confirm("You have an open rich text editor. Apply changes?");
		if (submit) {
			submitEditor();
		} else {
			cancelEditor();
		}
	}

	public final String getEditorText() {
		if (editor == null) return null;
		return editor.getText();
	}

	@Override
	public boolean isEditable() {
		return model.isEditable();
	}

	protected String getSyntaxInfo() {
		return Gwt.getDefaultRichtextSyntaxInfo();
	}

	protected RichtextFormater getRichtextFormater() {
		return Gwt.getDefaultRichtextFormater();
	}

	public RichtextEditorWidget setEditorHeight(int pixels) {
		editorHeight = pixels + "px";
		return this;
	}

	@Override
	public String getTooltip() {
		return model.getTooltip();
	}

	@Override
	public String getId() {
		return model.getId();
	}

	public ATextEditorModel getModel() {
		return model;
	}

	public void setRestoreText(String restoreText) {
		this.restoreText = restoreText;
	}

	private class EditorFocusListener implements FocusListener {

		@Override
		public void onFocus(Widget sender) {}

		@Override
		public void onLostFocus(Widget sender) {
			submitEditor();
		}

	}

	private class EditorKeyboardListener implements KeyPressHandler {

		@Override
		public void onKeyPress(KeyPressEvent event) {
			char keyCode = event.getCharCode();

			if (keyCode == KeyCodes.KEY_ESCAPE) {
				cancelEditor();
				event.stopPropagation();
			}

			if (event.isControlKeyDown()) {
				if (keyCode == KeyCodes.KEY_ENTER || keyCode == 10) {
					submitEditor();
					event.stopPropagation();
				}
			}
		}

	}

	private class RestoreAction extends AAction {

		@Override
		public String getLabel() {
			return "Restore lost text";
		}

		@Override
		public String getTooltip() {
			String preview = restoreText;
			if (restoreText != null && restoreText.length() > 100) preview = restoreText.substring(0, 100) + "...";
			return "Restore text, which was not saved: \"" + preview + "\"";
		}

		@Override
		public boolean isExecutable() {
			return !Str.isBlank(restoreText);
		}

		@Override
		protected void onExecute() {
			editor.setText(restoreText);
			restoreText = null;
			bottomToolbar.update();
		}
	}

}
