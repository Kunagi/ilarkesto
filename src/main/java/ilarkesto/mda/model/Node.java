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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Node implements Comparable<Node> {

	private String id;
	private Model model;
	private Node parent;
	private String type;
	private String value;
	private List<Node> children = new ArrayList<Node>();
	private boolean transient_;

	Node(Model model, String id, Node parent, String type, String value) {
		super();
		this.model = model;
		this.id = id;
		this.parent = parent;
		this.type = type;
		this.value = value;

		if (this.id == null) this.id = UUID.randomUUID().toString();
	}

	public Model getModel() {
		return model;
	}

	public Node addChild(String type, String value) {
		return addChild(null, type, value);
	}

	Node addChild(String id, String type, String value) {
		Node child = new Node(model, id, this, type, value);
		children.add(child);
		return child;
	}

	public boolean isType(String type) {
		return this.type.equals(type);
	}

	public boolean isType(String... types) {
		for (String type : types) {
			if (isType(type)) return true;
		}
		return false;
	}

	public boolean containsChild(String type, String value) {
		return getChild(type, value) != null;
	}

	public boolean containsChild(String type, boolean value) {
		Node child = getChildByType(type);
		return child == null ? false : child.isValue(String.valueOf(value));
	}

	public Node getChildRecursive(String type, String value) {
		for (Node child : getChildrenByType(type)) {
			if (value.equals(child.getValue())) return child;
		}
		for (Node child : getChildren()) {
			Node ret = child.getChildRecursive(type, value);
			if (ret != null) return ret;
		}
		return null;
	}

	public Node getChildOrCreate(String type, String value) {
		Node child = getChild(type, value);
		if (child == null) child = addChild(type, value);
		return child;
	}

	public Node getChild(String type, String value) {
		for (Node child : getChildrenByType(type)) {
			if (value.equals(child.getValue())) return child;
		}
		return null;
	}

	public List<Node> getChildrenByType(String type) {
		List<Node> ret = new ArrayList<Node>();
		for (Node child : getChildren()) {
			if (child.isType(type)) ret.add(child);
		}
		return ret;
	}

	public List<Node> getChildrenByTypeRecursive(String type) {
		List<Node> ret = new ArrayList<Node>();
		for (Node child : getChildren()) {
			if (child.isType(type)) ret.add(child);
			ret.addAll(child.getChildrenByTypeRecursive(type));
		}
		return ret;
	}

	public String getChildValueByType(String type) {
		Node child = getChildByType(type);
		return child == null ? null : child.getValue();
	}

	public Node getChildByType(String type) {
		for (Node child : getChildren()) {
			if (child.isType(type)) return child;
		}
		return null;
	}

	public boolean containsChildByType(String type) {
		return getChildByType(type) != null;
	}

	public boolean containsChildByTypeAndValue(String type, String value) {
		Node child = getChildByType(type);
		if (child == null) return false;
		return child.isValue(value);
	}

	public boolean containsChildByTypeAndValueTrue(String type) {
		Node child = getChildByType(type);
		if (child == null) return false;
		return child.isValueTrue();
	}

	public boolean containsChildByTypeAndValueFalse(String type) {
		Node child = getChildByType(type);
		if (child == null) return false;
		return child.isValueFalse();
	}

	public String getLabel() {
		return getType() + ":" + getValue();
	}

	public List<Node> getChildren() {
		return children;
	}

	public boolean containsChildren() {
		return !getChildren().isEmpty();
	}

	public boolean removeChild(Node child) {
		return children.remove(child);
	}

	public void removeAllChildren() {
		children.clear();
	}

	public void removeFromParent() {
		getParent().removeChild(this);
	}

	public String getId() {
		return id;
	}

	public Node getParent() {
		return parent;
	}

	public String getParentId() {
		return parent == null ? null : parent.getId();
	}

	public String getType() {
		return type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public boolean isValue(String test) {
		if (value == null && test == null) return true;
		if (value == null) return false;
		if (test == null) return false;
		return value.equals(test);
	}

	public boolean isValueTrue() {
		return isValue("true");
	}

	public boolean isValueFalse() {
		return isValue("false");
	}

	public boolean isTransient() {
		return transient_;
	}

	public void setTransient(boolean transient_) {
		this.transient_ = transient_;
	}

	@Override
	public int compareTo(Node o) {
		int typeRet = type.compareTo(o.type);
		return typeRet == 0 ? value.compareTo(o.value) : typeRet;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Node)) return false;
		return id.equals(((Node) obj).id);
	}

	// --- helper ---

	public Node getSuperparentByType(String type) {
		if (parent == null) return null;
		if (parent.isType(type)) return parent;
		return parent.getSuperparentByType(type);
	}

}
