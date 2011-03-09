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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RuleSet implements NodeTypes {

	private List<ChildTypeRule> childTypeRules = new ArrayList<ChildTypeRule>();

	RuleSet() {
		addChildTypeRuleByParentType(Root, GwtModule, EntitySet);
		addChildTypeRuleByParentType(EntitySet, Package);
		addChildTypeRuleByParentType(GwtModule, Package, TextBundle);
		addChildTypeRuleByParentType(Package, Entity, Datastruct, JavaClass, Component, Event, ServiceCall);
		addChildTypeRuleByParentType(TextBundle, Text);
		addChildTypeRuleByParentType(Text, EN, DE);
		addChildTypeRuleByParentType(ServiceCall, Parameter, Dispensable);
		addChildTypeRuleByParentType(Entity, TextProperty, IntegerProperty, FloatProperty, BooleanProperty,
			DateProperty, TimeProperty, DateAndTimeProperty, ReferenceProperty);
		addChildTypeRuleByParentType(Component, Dependency, InitializationProcedure);
		addChildTypeRuleByParentType(Dependency, Type, Inject);
		addChildTypeRuleByParentType(Event, Parameter, Flag);
		addChildTypeRuleByParentType(Parameter, Type, Index);
	}

	public List<String> getAllowedChildTypes(Node parent) {
		if (parent == null) return Collections.emptyList();
		Set<String> types = new HashSet<String>();
		for (ChildTypeRule rule : childTypeRules) {
			types.addAll(rule.getAllowedTypes(parent));
		}
		return new ArrayList(types);
	}

	public boolean containsAllowedChildTypes(Node parent) {
		if (parent == null) return false;
		return !getAllowedChildTypes(parent).isEmpty();
	}

	public void addChildTypeRuleByParentType(String parentType, String... childTypes) {
		addChildTypeRule(new ChildTypeRuleByParentType(parentType, Arrays.asList(childTypes)));
	}

	public void addChildTypeRule(ChildTypeRule rule) {
		childTypeRules.add(rule);
	}
}
