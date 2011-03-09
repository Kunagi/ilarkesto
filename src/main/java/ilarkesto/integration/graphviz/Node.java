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
package ilarkesto.integration.graphviz;

import java.util.HashMap;
import java.util.Map;

public class Node {

	private Map<String, String> properties = new HashMap<String, String>();
	private String name;

	Node(String name) {
		this.name = name;
		fontsize(10);
	}

	public Node label(String label) {
		return property("label", "\"" + label + "\"");
	}

	public Node color(String color) {
		return property("color", color);
	}

	public Node margin(float leftRight, float topBottom) {
		return property("margin", leftRight + "," + topBottom);
	}

	public Node shape(String shape) {
		return property("shape", shape);
	}

	public Node fontsize(int pts) {
		return property("fontsize", String.valueOf(pts));
	}

	public Node shapeBox() {
		return shape("box");
	}

	private Node property(String name, String value) {
		if (value != null) properties.put(name, value);
		return this;
	}

	String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" [ ");
		boolean first = true;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue());
		}
		sb.append(" ];");
		return sb.toString();
	}

}
