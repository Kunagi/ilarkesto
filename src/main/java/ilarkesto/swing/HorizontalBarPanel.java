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
package ilarkesto.swing;

import ilarkesto.core.logging.Log;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class HorizontalBarPanel extends JPanel {

	private static final Log LOG = Log.get(HorizontalBarPanel.class);

	public static void main(String[] args) {
		HorizontalBarPanel panel = new HorizontalBarPanel();
		panel.addColumn(new JLabel("Column 1"));
		panel.addColumn(new JLabel("Column 2"));
		panel.addColumn(new JLabel("Column 3"));
		panel.addColumn(new JLabel("Column 4"));
		Swing.showInJFrame(panel);
	}

	private JPanel grid;

	public HorizontalBarPanel() {
		setLayout(new BorderLayout());
		add(createGrid(), BorderLayout.CENTER);
	}

	public void addColumn(Component component) {
		LOG.debug("addColumn:", component);
		grid.add(component);
		grid.updateUI();
	}

	public void removeColumn(Component component) {
		LOG.debug("removeColumn:", component);
		grid.remove(component);
		grid.updateUI();
	}

	public void removeColumnsAfter(Component component) {
		LOG.debug("removeColumnsAfter:", component);
		int count = grid.getComponentCount();
		for (int i = count - 1; i >= 0; i--) {
			if (grid.getComponent(i) == component) break;
			grid.remove(i);
		}
		grid.updateUI();
	}

	public void removeAllColumns() {
		LOG.debug("removeAllColumns");
		grid.removeAll();
	}

	private Component createGrid() {
		grid = new JPanel(new GridLayout(1, 0, 10, 0));

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(grid, BorderLayout.WEST);

		JScrollPane scroller = new JScrollPane(wrapper);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		return scroller;
	}

}
