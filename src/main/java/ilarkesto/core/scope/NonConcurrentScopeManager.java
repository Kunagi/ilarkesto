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

import ilarkesto.core.logging.Log;

public class NonConcurrentScopeManager extends ScopeManager {

	private static Log log = Log.get(NonConcurrentScopeManager.class);

	private Scope currentScope;

	protected NonConcurrentScopeManager(Scope rootScope) {
		super(rootScope);
		this.currentScope = getRootScope();
	}

	public static NonConcurrentScopeManager createCascadingScopeInstance(String rootScopeName,
			ComponentReflector componentReflector) {
		return new NonConcurrentScopeManager(new CascadingScope(null, rootScopeName, componentReflector));
	}

	@Override
	public Scope getScope() {
		assert currentScope != null;
		return currentScope;
	}

	public Scope setScope(Scope scope) {
		assert scope != null;
		this.currentScope = scope;
		log.info("Scope activated:", scope);
		return scope;
	}

}
