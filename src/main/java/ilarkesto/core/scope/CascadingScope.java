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
package ilarkesto.core.scope;

import ilarkesto.core.event.AEventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CascadingScope extends Scope {

	int id;
	Scope parentScope;
	String name;
	Map<Integer, CascadingScope> childScopesById = new LinkedHashMap<Integer, CascadingScope>();
	ComponentReflector componentReflector;
	Map<String, Object> componentsByName = new HashMap<String, Object>();
	private boolean wiringRequired = true;

	CascadingScope(Scope parentScope, String name, ComponentReflector componentReflector) {
		this.id = ID_GENERATOR.nextId();

		this.parentScope = parentScope;

		assert name != null;
		this.name = name;

		this.componentReflector = componentReflector;

		putComponent(AEventBus.DEFAULT_COMPONENT_NAME, new CascadingScopeEventBus(this));
	}

	public synchronized void wireComponents() {
		log.info("Wiring components:", getName());
		wiringRequired = false;

		List components = new ArrayList(componentsByName.values());
		for (Object component : components) {
			componentReflector.injectComponents(component, this);
		}
		for (Object component : components) {
			componentReflector.callInitializationMethods(component);
			componentReflector.outjectComponents(component, this);
		}

		if (wiringRequired) wireComponents();
	}

	@Override
	public Object getComponent(String name) {
		if (wiringRequired) wireComponents();

		Object component = componentsByName.get(name);
		if (component == null) return getComponentFromParentScope(name);

		return component;
	}

	private Object getComponentFromParentScope(String name) {
		if (parentScope == null) return null;
		return parentScope.getComponent(name);
	}

	@Override
	public synchronized <T> T putComponent(String name, T component) {
		assert name != null;
		assert component != null;

		Object existingComponent = componentsByName.get(name);
		if (existingComponent == component) return component;

		log.info("Putting component:", name);

		componentsByName.put(name, component);
		wiringRequired = true;

		return component;
	}

	@Override
	public List getAllComponents() {
		List ret = new ArrayList();
		if (parentScope != null) ret.addAll(parentScope.getAllComponents());
		ret.addAll(getLocalComponents());
		return ret;
	}

	public CascadingScope createScope(String name) {
		log.info("Creating sub scope:", getName(), "->", name);
		CascadingScope scope = new CascadingScope(this, name, componentReflector);
		childScopesById.put(scope.getId(), scope);
		return scope;
	}

	public List getLocalComponents() {
		if (wiringRequired) wireComponents();

		List ret = new ArrayList();
		ret.addAll(componentsByName.values());
		return ret;
	}

	@Override
	public String getName() {
		return parentScope == null ? name : parentScope.getName() + ">" + name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return id + ":" + super.toString();
	}

	public static CascadingScope get() {
		return (CascadingScope) Scope.get();
	}

	static final IdGenerator ID_GENERATOR = new IdGenerator();

	static class IdGenerator {

		int lastId = -1;

		int nextId() {
			synchronized (ID_GENERATOR) {
				return ++lastId;
			}
		}

	};

}
