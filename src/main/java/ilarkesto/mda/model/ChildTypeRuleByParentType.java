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

import java.util.Collections;
import java.util.List;

public class ChildTypeRuleByParentType implements ChildTypeRule {

	private String parentType;
	private List<String> childTypes;

	public ChildTypeRuleByParentType(String parentType, List<String> childTypes) {
		super();
		this.parentType = parentType;
		this.childTypes = childTypes;
	}

	@Override
	public List<String> getAllowedTypes(Node parent) {
		if (parent.isType(parentType)) return childTypes;
		return Collections.emptyList();
	}

}
