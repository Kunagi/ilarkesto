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
package ilarkesto.mda.swingeditor;

import ilarkesto.core.scope.CascadingScope;
import ilarkesto.core.scope.NonConcurrentScopeManager;
import ilarkesto.core.scope.Scope;
import ilarkesto.mda.model.ModellingSession;
import ilarkesto.scope.ReflectionComponentReflector;

public class Starter {

	public static void main(String[] args) {
		createModellerScope();

		Scope.get().getComponent(Workspace.class).showJFrame();
	}

	public static Scope createModellerScope() {
		NonConcurrentScopeManager.createCascadingScopeInstance("app", new ReflectionComponentReflector());

		CascadingScope scope = CascadingScope.get();
		scope.putComponent(new Workspace());
		scope.putComponent(new ModellingSession());
		scope.putComponent(new SwingModelHelper());
		scope.putComponent(new NodeListBarPanel());
		scope.putComponent(new NodeValuePanel());
		scope.putComponent(new SaveAction());
		scope.putComponent(new ProcessAction());

		return scope;
	}

}
