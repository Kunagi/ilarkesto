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

import ilarkesto.core.logging.Log;
import ilarkesto.mda.model.Node;
import ilarkesto.mda.model.RuleSet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

public class NodeListPanel extends JPanel {

	private static final Log LOG = Log.get(NodeListPanel.class);

	private SwingModelHelper swingModelHelper;

	private Node node;
	private Observer observer;

	private JScrollPane scroller;
	private JTable table;
	private NodesModel tableModel;

	private JPanel toolbar;
	private JButton addButton;
	private JButton removeButton;
	private JButton editButton;

	public NodeListPanel(SwingModelHelper swingModelHelper) {
		this.swingModelHelper = swingModelHelper;
		setLayout(new BorderLayout());
		add(createToolbar(), BorderLayout.NORTH);
		add(createTable(), BorderLayout.CENTER);
	}

	public void addNode() {
		Node newNode = swingModelHelper.addNode(node, getSelectedNode());
		if (newNode == null) return;
		tableModel.update();
		selectNode(newNode);
	}

	public void editNode() {
		Node selectedNode = getSelectedNode();
		if (selectedNode == null) return;
		swingModelHelper.editNode(selectedNode);
		tableModel.update();
		selectNode(selectedNode);
	}

	public void removeNode() {
		Node selectedNode = getSelectedNode();
		if (selectedNode == null) return;
		swingModelHelper.removeNode(selectedNode);
		tableModel.update();
	}

	public void selectNode(Node nodeToSelect) {
		int index = tableModel.nodes.indexOf(nodeToSelect);
		if (index < 0) return;
		table.getSelectionModel().setSelectionInterval(index, index);
	}

	public void setNode(Node node) {
		this.node = node;
		tableModel.update();
	}

	private Component createToolbar() {
		addButton = new JButton("+");
		addButton.setToolTipText("Create new node.");
		addButton.addActionListener(new AddActionListener());

		removeButton = new JButton("-");
		removeButton.setToolTipText("Delete selected node.");
		removeButton.addActionListener(new RemoveActionListener());

		editButton = new JButton("edit");
		editButton.setToolTipText("Edit value of selected node.");
		editButton.addActionListener(new EditActionListener());

		toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		toolbar.add(addButton);
		toolbar.add(editButton);
		toolbar.add(removeButton);
		updateToolbar(null);
		return toolbar;
	}

	private void updateToolbar(Node selectedNode) {
		RuleSet ruleSet = swingModelHelper.getModellingSession().getRuleSet();
		addButton.setEnabled(node == null || ruleSet.containsAllowedChildTypes(node));
		removeButton.setEnabled(selectedNode != null);
		editButton.setEnabled(selectedNode != null);
	}

	private Component createTable() {
		tableModel = new NodesModel();
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnSelectionAllowed(false);
		table.getSelectionModel().addListSelectionListener(new SelectionListener());
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(30);

		scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(200, 200));
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return scroller;
	}

	public Node getSelectedNode() {
		int selectedRow = table.getSelectedRow();
		return selectedRow < 0 ? null : tableModel.nodes.get(selectedRow);
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	@Override
	public String toString() {
		return "NodeListPanel(" + node + ")";
	}

	private class RemoveActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			removeNode();
		}
	}

	private class EditActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			editNode();
		}
	}

	private class AddActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			addNode();
		}
	}

	public static interface Observer {

		void onNodeSelectionChanged(NodeListPanel nodeListPanel, Node selectedNode);

	}

	private class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent evt) {
			if (evt.getValueIsAdjusting()) return;
			Node selectedNode = getSelectedNode();
			LOG.debug("selection changed:", selectedNode);
			updateToolbar(selectedNode);
			if (observer != null) observer.onNodeSelectionChanged(NodeListPanel.this, selectedNode);
		}

	}

	private class NodesModel extends AbstractTableModel {

		private List<Node> nodes = Collections.emptyList();

		public void update() {
			nodes = node.getChildren();
			Collections.sort(nodes);
			fireTableDataChanged();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Node node = nodes.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return node.getType();
				default:
					return node.getValue();
			}
		}

		@Override
		public int getRowCount() {
			return nodes.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
				case 0:
					return "Type";
				default:
					return "Value";
			}
		}

	}

}
