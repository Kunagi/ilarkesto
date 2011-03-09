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
import ilarkesto.mda.model.Node;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class NodeValuePanel extends JPanel implements NodeSelectionChangedHandler {

	@In
	SwingModelHelper swingModelHelper;

	public NodeValuePanel() {
		super(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	@Override
	public void onNodeSelectionChanged(NodeSelectionChangedEvent event) {
		removeAll();
		Node node = event.getSelectedNode();
		if (node != null) {
			Component component = swingModelHelper.createValueComponent(node);
			add(createWrapper(component), BorderLayout.CENTER);
		}
		updateUI();
	}

	private Component createWrapper(Component component) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component, BorderLayout.CENTER);
		panel.setBorder(new LineBorder(Color.DARK_GRAY));
		return panel;
	}

}
