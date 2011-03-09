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

public class Edge {

	private Map<String, String> properties = new HashMap<String, String>();
	private Node from;
	private Node to;

	Edge(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Edge label(String label) {
		properties.put("label", "\"" + label + "\"");
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(from.getName()).append(" -> ").append(to.getName());
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
