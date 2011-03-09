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

import ilarkesto.io.IO;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class PanelBuilder {

	public static void main(String[] args) {
		PanelBuilder pb = new PanelBuilder();
		pb.setBackgroundImage(IO.loadImage(new File(
				"g:/home/wko/eclipse-workspace/asistanto/src/main/resources/img/bg.jpg")));
		pb.add(new JButton("Button 1")).span(2);
		pb.add(new JButton("Button 2"));
		pb.add(new JButton("Button 3")).nl();
		pb.add(new JButton("Button 4"));
		pb.add(new JButton("Button 5"));
		JPanel panel = pb.toPanel();
		Swing.showInJFrame(panel);
	}

	private List<Cell> cells = new ArrayList<Cell>();
	private Cell lastCell;
	private GridBagConstraints defaultConstraints = new GridBagConstraints();
	private Border border;
	private BufferedImage backgroundImage;
	private boolean opaque = false;
	private Dimension preferredSize;
	private Color background;

	public PanelBuilder() {
		defaultConstraints.weightx = 1;
	}

	public PanelBuilder setupAsButtonMenu(String title) {
		setDefaultPadding(2, 2, 2, 2);
		setDefaultFillToHorizontal();
		if (title != null) setBorder(new TitledBorder(new EtchedBorder(), title));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <C extends Component> Cell<C> add(C component) {
		lastCell = new Cell<C>(component);
		cells.add(lastCell);
		return lastCell;
	}

	// public Cell<JButton> add(AView.Button button) {
	// return add(button.toJButton());
	// }

	public Cell<JLabel> addEmpty() {
		return add("");
	}

	public Cell<JLabel> add(String label) {
		return add(new JLabel(label));
	}

	public <C extends Component> Cell<C> addLn(String label, C component) {
		Insets insets = new Insets(defaultConstraints.insets.top, defaultConstraints.insets.left,
				defaultConstraints.insets.bottom, defaultConstraints.insets.right + 3);
		add(label).setInsets(insets);
		Cell<C> cell = add(component);
		nl();
		return cell;
	}

	public void nl() {
		if (lastCell == null) return;
		lastCell.constraints.gridwidth = GridBagConstraints.REMAINDER;
	}

	public PanelBuilder setBackground(Color background) {
		this.background = background;
		this.opaque = true;
		return this;
	}

	public PanelBuilder setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
		return this;
	}

	public PanelBuilder setOpaque(boolean opaque) {
		this.opaque = opaque;
		return this;
	}

	public PanelBuilder setBackgroundImage(BufferedImage backgroundImage) {
		this.backgroundImage = backgroundImage;
		return this;
	}

	public PanelBuilder setBorder(Border border) {
		this.border = border;
		return this;
	}

	public PanelBuilder setDefaultWeightX(double value) {
		defaultConstraints.weightx = value;
		return this;
	}

	public PanelBuilder setDefaultWeightY(double value) {
		defaultConstraints.weightx = value;
		return this;
	}

	public PanelBuilder setDefaultAnchorToNorthWest() {
		defaultConstraints.anchor = GridBagConstraints.NORTHWEST;
		return this;
	}

	public PanelBuilder setDefaultAnchorToNorth() {
		defaultConstraints.anchor = GridBagConstraints.NORTH;
		return this;
	}

	public PanelBuilder setDefaultPadding(int top, int bottom, int left, int right) {
		defaultConstraints.insets = new Insets(top, left, bottom, right);
		return this;
	}

	public PanelBuilder setDefaultFillToHorizontal() {
		defaultConstraints.fill = GridBagConstraints.HORIZONTAL;
		return this;
	}

	public PanelBuilder setDefaultFillToBoth() {
		defaultConstraints.fill = GridBagConstraints.BOTH;
		return this;
	}

	public JPanel toPanel() {
		GridBagLayout layout = new GridBagLayout();
		JPanel panel = backgroundImage == null ? new JPanel(layout) : new MyPanel(layout, backgroundImage);
		for (Cell cell : cells) {
			layout.addLayoutComponent(cell.component, cell.constraints);
			panel.add(cell.component);
		}
		if (border != null) panel.setBorder(border);
		if (preferredSize != null) panel.setPreferredSize(preferredSize);
		if (background != null) panel.setBackground(background);
		panel.setOpaque(opaque);
		return panel;
	}

	public class Cell<C extends Component> {

		private C component;
		private GridBagConstraints constraints;

		public Cell(C component) {
			if (component == null) throw new NullPointerException("component");
			this.component = component;
			constraints = (GridBagConstraints) defaultConstraints.clone();
			setAnchorToNorthWest();
		}

		public Cell setInsets(Insets insets) {
			constraints.insets = insets;
			return this;
		}

		public Cell setAnchorToCenter() {
			constraints.anchor = GridBagConstraints.CENTER;
			return this;
		}

		public Cell setAnchorToNorthEast() {
			constraints.anchor = GridBagConstraints.NORTHEAST;
			return this;
		}

		public Cell setAnchorToEast() {
			constraints.anchor = GridBagConstraints.EAST;
			return this;
		}

		public Cell setAnchorToWest() {
			constraints.anchor = GridBagConstraints.WEST;
			return this;
		}

		public Cell setAnchorToNorthWest() {
			constraints.anchor = GridBagConstraints.NORTHWEST;
			return this;
		}

		public Cell setFillToHorizontal() {
			constraints.fill = GridBagConstraints.HORIZONTAL;
			return this;
		}

		public Cell setFillToVertical() {
			constraints.fill = GridBagConstraints.VERTICAL;
			return this;
		}

		public Cell setFillToBoth() {
			constraints.fill = GridBagConstraints.BOTH;
			return this;
		}

		public Cell setFillToNone() {
			constraints.fill = GridBagConstraints.NONE;
			return this;
		}

		public Cell setPadding(int top, int bottom, int left, int right) {
			constraints.insets = new Insets(top, left, bottom, right);
			return this;
		}

		public Cell setWeightX(double value) {
			constraints.weightx = value;
			return this;
		}

		public Cell setWeightY(double value) {
			constraints.weighty = value;
			return this;
		}

		public Cell span(int columns) {
			constraints.gridwidth = columns;
			return this;
		}

		public void nl() {
			PanelBuilder.this.nl();
		}

		public C getComponent() {
			return component;
		}

	}

	class MyPanel extends JPanel {

		private BufferedImage backgroundImage;

		public MyPanel(LayoutManager layout, BufferedImage backgroundImage) {
			super(layout);
			this.backgroundImage = backgroundImage;
		}

		@Override
		public void paint(Graphics g) {
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, getBackground(), null);
			}
			super.paint(g);
		}
	}

}
