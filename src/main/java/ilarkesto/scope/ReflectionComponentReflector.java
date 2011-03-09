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
package ilarkesto.scope;

import ilarkesto.base.Reflect;
import ilarkesto.core.logging.Log;
import ilarkesto.core.scope.ComponentReflector;
import ilarkesto.core.scope.In;
import ilarkesto.core.scope.Init;
import ilarkesto.core.scope.Out;
import ilarkesto.core.scope.Scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionComponentReflector implements ComponentReflector {

	private static final Log log = Log.get(ReflectionComponentReflector.class);

	@Override
	public void injectComponents(Object component, Scope scope) {
		Reflect.processAnnotations(component, new DependencyInjector(scope));
	}

	@Override
	public void callInitializationMethods(Object component) {
		Reflect.processAnnotations(component, new Initializer());
	}

	@Override
	public void outjectComponents(Object component, Scope scope) {
		Reflect.processAnnotations(component, new DependencyOutjector(scope));
	}

	class DependencyOutjector implements Reflect.FieldAnnotationHandler {

		private Scope scope;

		public DependencyOutjector(Scope scope) {
			super();
			this.scope = scope;
		}

		@Override
		public void handle(Annotation annotation, Field field, Object component) {
			if (annotation.annotationType() != Out.class) return;

			String outName = field.getName();
			Object outComponent;

			try {
				if (!field.isAccessible()) field.setAccessible(true);
				outComponent = field.get(component);
			} catch (Throwable ex) {
				throw new DependencyOutjectionFailedException(component, outName, ex);
			}
			if (outComponent == null) return;

			log.debug("Outjecting component field:", component.getClass().getSimpleName() + "." + field.getName());
			scope.putComponent(outName, outComponent);
		}
	}

	class Initializer implements Reflect.MethodAnnotationHandler {

		@Override
		public void handle(Annotation annotation, Method method, Object object) {
			if (annotation.annotationType() != Init.class) return;

			log.debug("Calling initialization method:", object.getClass().getSimpleName() + "." + method.getName()
					+ "()");
			try {
				if (!method.isAccessible()) method.setAccessible(true);
				method.invoke(object);
			} catch (Throwable ex) {
				throw new InitializationFaildException(object, method.getName(), ex);
			}
		}
	}

	class DependencyInjector implements Reflect.FieldAnnotationHandler {

		private Scope scope;

		public DependencyInjector(Scope scope) {
			super();
			this.scope = scope;
		}

		@Override
		public void handle(Annotation annotation, Field field, Object component) {
			if (annotation.annotationType() != In.class) return;

			String dependencyName = field.getName();
			Object dependency = scope.getComponent(dependencyName);
			if (dependency == null) return;

			try {
				if (!field.isAccessible()) field.setAccessible(true);
				Object value = field.get(component);
				if (value == dependency) return;
				log.debug("Injecting component field:", component.getClass().getSimpleName() + "." + field.getName());
				field.set(component, dependency);
			} catch (Throwable ex) {
				throw new DependencyInjectionFailedException(component, dependencyName, dependency, ex);
			}
		}
	}
}
