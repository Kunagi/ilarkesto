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
package ilarkesto.mda.swingeditor;

import ilarkesto.core.scope.In;
import ilarkesto.mda.model.ModellingSession;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.NodeTypes;
import ilarkesto.swing.Swing;

import java.awt.Component;
import java.awt.Insets;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SwingModelHelper {

	@In
	Component dialogParent;

	@In
	ModellingSession modellingSession;

	public void removeNode(Node node) {
		if (node.containsChildren()) {
			String message = "Delete " + node.getLabel() + " with all its child nodes?";
			if (JOptionPane.showConfirmDialog(dialogParent, message, "Confirm deletion", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
				return;
		}
		node.removeFromParent();

	}

	public void editNode(Node node) {
		String value = queryNewNodeValue(node.getType(), node.getParent(), node.getValue());
		if (value == null) return;
		node.setValue(value);
	}

	public Node addNode(Node parent, Node template) {
		String type = queryNewNodeType(parent, template);
		if (type == null) return null;
		String value = queryNewNodeValue(type, parent, null);
		if (value == null) return null;
		Node node = parent.addChild(type, value);
		return node;
	}

	public String queryNewNodeValue(String type, Node parent, String oldValue) {
		return queryNewNodeTextValue(type, parent, oldValue);
	}

	// public String queryChoice(NodeType type, Object... options) {
	// Object preselectedOption = options[0];
	// String title = "Set node value";
	// String message = "What ist the value for " + type.getName() + "?";
	// String selectedOption = (String) JOptionPane.showInputDialog(dialogParent,
	// message,
	// title, JOptionPane.QUESTION_MESSAGE, null, options, preselectedOption);
	// return selectedOption;
	// }

	public String queryNewNodeTextValue(String type, Node parent, String oldValue) {
		boolean multiline = NodeTypes.DE.equals(type) || NodeTypes.EN.equals(type);
		return multiline ? queryNewNodeTextMultilineValue(type, parent, oldValue) : queryNewNodeTextLineValue(type,
			parent);
	}

	public String queryNewNodeTextLineValue(String type, Node parent) {
		String value = JOptionPane.showInputDialog(dialogParent, "What is the value for " + type + "?",
			"Set node value", JOptionPane.QUESTION_MESSAGE);
		return value;
	}

	public String queryNewNodeTextMultilineValue(String type, Node parent, String oldValue) {
		String value = Swing.showTextEditorDialog(dialogParent, oldValue, "Set node value");
		return value;
	}

	public void showNodeValueError(String error) {
		String title = "Invalid node value";
		JOptionPane.showMessageDialog(dialogParent, error, title, JOptionPane.ERROR_MESSAGE);
	}

	public String queryNewNodeType(Node parent, Node template) {
		List<String> typesAvailableToAdd = modellingSession.getRuleSet().getAllowedChildTypes(parent);
		Object[] options = typesAvailableToAdd.toArray();
		if (options.length == 0) return null;
		Object preselectedOption = options[0];
		if (template != null) {
			String type = template.getType();
			if (typesAvailableToAdd.contains(type)) preselectedOption = type;
		}
		String message = "Which node type would you like to add?";
		String title = "Select node type";
		String selectedNodeType = (String) JOptionPane.showInputDialog(dialogParent, message, title,
			JOptionPane.QUESTION_MESSAGE, null, options, preselectedOption);
		return selectedNodeType;
	}

	public ModellingSession getModellingSession() {
		return modellingSession;
	}

	public Component createValueComponent(Node node) {
		if (node.isType(NodeTypes.EN, NodeTypes.DE)) return createHtmlValueComponent(node.getValue());
		return createTextValueComponent(node.getValue());
	}

	public Component createTextValueComponent(String value) {
		JTextArea textarea = new JTextArea();
		textarea.setEditable(false);
		textarea.setText(value);
		textarea.setMargin(new Insets(5, 5, 5, 5));
		// textarea.setBorder(new LineBorder(Color.BLACK));
		return textarea;
	}

	public Component createHtmlValueComponent(String html) {
		JEditorPane pane = new JEditorPane("text/html", html);
		pane.setEditable(false);
		// pane.setBorder(new LineBorder(Color.BLACK));
		pane.setMargin(new Insets(5, 5, 5, 5));
		return new JScrollPane(pane);
	}

}
