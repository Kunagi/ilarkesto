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
package ilarkesto.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

// http://codemirror.net/manual.html
public class CodemirrorEditorWidget extends AWidget {

	private TextArea textArea = new MyTextArea();
	private JavaScriptObject editor;
	private String height = "200px";
	private Observer observer;
	private boolean burned;

	private native JavaScriptObject createEditor(String textAreaId, String height)
	/*-{
		var editor = new $wnd.CodeMirror($wnd.CodeMirror.replace(textAreaId), {
			parserfile: ["parsewiki.js"],
			path: "codemirror/js/",
			stylesheet: "codemirror/css/wikicolors.css",
			height: height,
			lineNumbers: false,
			enterMode: "flat",
		    tabMode: "spaces"
		});
		
		editor.ensureWindowLoaded = function() {
			if (this.editor == null) alert("Waiting for internal frame to load. This is a temporary workaround.");
		}
		
		editor.execute = function(funct) {
			if (funct == null) return;
			if (this.editor == null) {
				setTimeout(function() {
					editor.execute(funct);
				}, 100);
			} else {
				funct();
			}
		}
		
		editor.isReady = function() {
			return this.editor != null;
		}
		
		return editor;
	}-*/;

	@Override
	protected Widget onInitialization() {
		return textArea;
	}

	private void createEditor() {
		if (burned) return;
		if (editor == null) {
			String text = textArea.getText();
			// Log.DEBUG("Creating editor for:", text);
			String id = textArea.getElement().getId();
			editor = createEditor(id, height);
			setText(prepareText(text));
			focus(editor);
		}
	}

	private String prepareText(String s) {
		if (s == null) return "\n";
		if (s.endsWith("\n")) return s;
		return s + "\n";
	}

	public void setText(String text) {
		textArea.setText(text);
		if (editor != null) setCode(editor, prepareText(text));
	}

	public void focus() {
		// if (editor != null) focus(editor);
	}

	public void addKeyPressHandler(KeyPressHandler listener) {
		// TODO
	}

	public String getText() {
		if (!isReady()) return textArea.getText();
		String text = editorGetCode(editor);
		textArea.setText(text);
		return text;
	}

	@Override
	public void setHeight(String height) {
		this.height = height;
	}

	public void wrapSelection(String prefix, String suffix) {
		wrapSelection(editor, prefix, suffix);
	}

	public void wrapLine(String prefix, String suffix) {
		wrapLine(editor, prefix, suffix);
	}

	public String getSelectedText() {
		if (!isReady()) return null;
		return selection(editor);
	}

	private boolean isReady() {
		return editor != null && isReady(editor);
	}

	private native boolean isReady(JavaScriptObject editor)
	/*-{
		return editor.isReady();
	}-*/;

	private native String selection(JavaScriptObject editor)
	/*-{
	    editor.ensureWindowLoaded();
		return editor.selection();
	}-*/;

	private native String editorGetCode(JavaScriptObject editor)
	/*-{
	    editor.ensureWindowLoaded();
		return editor.getCode();
	}-*/;

	private native void setCode(JavaScriptObject editor, String text)
	/*-{
		editor.execute( function() {
			editor.setCode(text);
		});
	}-*/;

	private native void wrapLine(JavaScriptObject editor, String prefix, String suffix)
	/*-{
	 	editor.execute( function() {
		    cursorPosition = editor.cursorPosition(true);
		    selection = editor.selection();
		    if (selection==null) selection = "";
		    line = editor.lineContent(cursorPosition.line); 
			editor.setLineContent(cursorPosition.line, prefix + line + suffix);
			from = cursorPosition.character+prefix.length;
			to = cursorPosition.character+prefix.length+selection.length;
			editor.selectLines(cursorPosition.line, from, cursorPosition.line, to);
		});
	}-*/;

	private native void wrapSelection(JavaScriptObject editor, String prefix, String suffix)
	/*-{
	 	editor.execute( function() {
		    cursorPosition = editor.cursorPosition(true);
		    selection = editor.selection(); 
		    if (selection==null) selection = "";
			editor.replaceSelection(prefix + selection + suffix);
			from = cursorPosition.character+prefix.length;
			to = cursorPosition.character+prefix.length+selection.length;
			editor.selectLines(cursorPosition.line, from, cursorPosition.line, to);
		});
	}-*/;

	private native void focus(JavaScriptObject editor)
	/*-{
	    editor.execute( function() {
			editor.focus();
	    });
	}-*/;

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	private class MyTextArea extends TextArea {

		public MyTextArea() {
			setWidth("100%");
			getElement().setId("CodeMirror" + System.currentTimeMillis());
			// setVisible(false);
		}

		@Override
		protected void onAttach() {
			// Log.DEBUG("-------- onAttach()");
			super.onAttach();
			createEditor();
		}

		@Override
		protected void onDetach() {
			// Log.DEBUG("-------- onDetach()");
			textArea.setText(editorGetCode(editor));
			editor = null;
			burned = true;
			super.onDetach();
			if (observer != null) observer.onCodemirrorDetach();
		}

	}

	public boolean isBurned() {
		return burned;
	}

	public static interface Observer {

		void onCodemirrorDetach();

	}

}
