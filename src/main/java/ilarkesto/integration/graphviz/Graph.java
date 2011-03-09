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

import java.util.ArrayList;
import java.util.List;

public class Graph {

	private List<Node> nodes = new ArrayList<Node>();
	private List<Edge> edges = new ArrayList<Edge>();
	private int nodeCount;

	public Node node(String label) {
		Node node = new Node("n" + ++nodeCount);
		node.label(label);
		nodes.add(node);
		return node;
	}

	public Edge edge(Node from, Node to) {
		Edge edge = new Edge(from, to);
		edges.add(edge);
		return edge;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n");
		for (Node node : nodes)
			sb.append("  ").append(node).append("\n");
		for (Edge edge : edges) {
			sb.append("  ").append(edge).append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
}
