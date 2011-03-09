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
package ilarkesto.mda.model;

import ilarkesto.core.logging.Log;

public class Model implements NodeTypes {

	private static final Log LOG = Log.get(Model.class);

	private Node root;

	public Model() {
		createRootNode();
	}

	public void clear() {
		createRootNode();
	}

	private void createRootNode() {
		root = new Node(this, "root", null, "root", null);
	}

	public void addNode(String id, String parentId, String type, String value) {
		if ("root".equals(id)) return;
		Node parent = getNodeById(parentId);
		if (parent == null) throw new RuntimeException("Node does not exist: " + parentId);
		parent.addChild(id, type, value);
	}

	public Node getNodeById(String id) {
		return getNodeById(root, id);
	}

	private Node getNodeById(Node node, String id) {
		if (node.getId().endsWith(id)) return node;
		for (Node child : node.getChildren()) {
			Node ret = getNodeById(child, id);
			if (ret != null) return ret;
		}
		return null;
	}

	public Node getRoot() {
		return root;
	}

	public static Model createTestInstance() {
		Model model = new Model();
		Node root = model.root;

		Node addressbook = root.addChild(Package, "addressbook");

		Node person = addressbook.addChild(Entity, "Person");
		person.addChild(TextProperty, "firstName");
		person.addChild(TextProperty, "lastName");

		Node organization = addressbook.addChild(Entity, "Organization");
		organization.addChild(TextProperty, "name");
		organization.addChild(TextProperty, "industry");

		Node auth = root.addChild(Package, "auth");

		Node user = auth.addChild(Entity, "User");
		user.addChild(TextProperty, "login");
		user.addChild(TextProperty, "password");

		return model;
	}
}
