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
package ilarkesto.base;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Utilities for reflection and meta programming.
 */
public abstract class Reflect {

	/**
	 * Call the <code>initialize()</code> method when it exists.
	 */
	public static void invokeInitializeIfThere(Object o) {
		Method m = getDeclaredMethod(o.getClass(), "initialize");
		if (m == null) return;
		try {
			m.invoke(o);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Object getProperty(Object o, String name) {
		String methodSuffix = Str.uppercaseFirstLetter(name);
		Method method = getDeclaredMethod(o.getClass(), "get" + methodSuffix);
		if (method == null) {
			method = getDeclaredMethod(o.getClass(), "is" + methodSuffix);
			Class<?> returnType = method.getReturnType();
			if (returnType != boolean.class && returnType != Boolean.class) method = null;
		}
		if (method == null)
			throw new RuntimeException("No getter method for property: " + o.getClass().getSimpleName() + "." + name);
		try {
			return method.invoke(o);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to invoke getter method: " + o.getClass().getSimpleName() + "."
					+ method.getName() + "()", ex);
		}
	}

	public static Class getPropertyType(Object o, String name) {
		String methodName = "get" + Str.uppercaseFirstLetter(name);
		Method m = getDeclaredMethod(o.getClass(), methodName);
		if (m == null) return null;
		return m.getReturnType();
	}

	public static Object getFieldValue(Object object, String fieldName) {
		return getFieldValue(object.getClass(), object, fieldName);
	}

	public static Object getFieldValue(Class<?> c, String fieldName) {
		return getFieldValue(c, null, fieldName);
	}

	public static Object getFieldValue(Class<?> c, Object object, String fieldName) {
		Field field = getDeclaredField(c, fieldName);
		if (field == null) return null;
		try {
			return field.get(object);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void setFieldValue(Object object, String fieldName, Object value) {
		setFieldValue(object.getClass(), object, fieldName, value);
	}

	public static void setFieldValue(Class<?> c, String fieldName, Object value) {
		setFieldValue(c, null, fieldName, value);
	}

	public static void setFieldValue(Class<?> c, Object object, String fieldName, Object value) {
		Field field = getDeclaredField(c, fieldName);
		if (field == null) throw new RuntimeException("Field does not exist: " + c.getName() + "." + fieldName);
		if (!field.isAccessible()) field.setAccessible(true);
		try {
			field.set(object, value);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void setProperties(Object o, Map<String, Object> properties) {
		if (properties == null) return;
		for (Map.Entry<String, ?> entry : properties.entrySet()) {
			setProperty(o, entry.getKey(), entry.getValue());
		}
	}

	public static void setProperty(Object o, String name, Object value) {
		Method setter = getSetterMethod(o.getClass(), name);
		if (setter == null) throw new RuntimeException("Property setter not found: " + o.getClass() + "." + name);
		Class[] types = setter.getParameterTypes();
		if (types.length != 1)
			throw new RuntimeException("Setter has illegar arguments: " + o.getClass() + "." + setter.getName());
		if (value != null) {
			Class type = types[0];
			if (!type.isAssignableFrom(value.getClass())) {
				if (type.equals(Boolean.class) || type.equals(boolean.class)) {
					value = Boolean.valueOf(value.toString());
				} else if (type.equals(Integer.class) || type.equals(int.class)) {
					value = Integer.valueOf(value.toString());
				} else if (type.equals(Long.class) || type.equals(long.class)) {
					value = Long.valueOf(value.toString());
				} else if (type.equals(Float.class) || type.equals(float.class)) {
					value = Float.valueOf(value.toString());
				} else if (type.equals(Double.class) || type.equals(double.class)) {
					value = Double.valueOf(value.toString());
				} else {
					value = newInstance(type, value);
				}
			}
		}
		invoke(o, setter, value);
	}

	public static void setPropertyByStringValue(Object o, String name, String valueAsString) {
		Method setterMethod = getSetterMethod(o.getClass(), name);
		if (setterMethod == null)
			throw new RuntimeException("Setter " + o.getClass().getSimpleName() + ".set"
					+ Str.uppercaseFirstLetter(name) + "(?) does not exist.");
		Class type = setterMethod.getParameterTypes()[0];
		Object value = toType(valueAsString, type);
		invoke(o, setterMethod, value);
	}

	public static Object toType(String s, Class type) {
		if (type == String.class) return s;
		if (type == Boolean.class || type == boolean.class) return toBoolean(s);
		if (type == Integer.class || type == int.class) return toInteger(s);
		if (type == Long.class || type == long.class) return toLong(s);
		if (type == Character.class || type == char.class) return toCharacter(s);
		throw new RuntimeException("Unsupported type: " + type.getName());
	}

	public static Character toCharacter(String s) {
		if (s == null) return null;
		if (s.length() < 1) return null;
		return s.charAt(0);
	}

	public static Integer toInteger(String s) {
		if (s == null) return null;
		return Integer.parseInt(s);
	}

	public static Long toLong(String s) {
		if (s == null) return null;
		return Long.parseLong(s);
	}

	public static Boolean toBoolean(String s) {
		if (s == null) return null;
		return s.equals(Boolean.TRUE.toString());
	}

	public static Object newInstance(String className) {
		try {
			return newInstance(Class.forName(className));
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static <T extends Object> T newInstance(Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static <T extends Object> T newInstance(Class<T> type, Object... constructorParameters) {
		try {
			Constructor<T> constructor = type.getConstructor(getClasses(constructorParameters));
			return constructor.newInstance(constructorParameters);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Object invoke(Object object, String method, Object... parameters) {
		Method m = getDeclaredMethodUsingAutoboxing(object.getClass(), method, getClasses(parameters));
		if (m == null)
			throw new NullPointerException("Method does not exist: " + object.getClass() + "." + method + "("
					+ Str.concat(getClassSimpleNames(parameters), ", ") + ")");
		return invoke(object, m, parameters);
	}

	public static Object invoke(Object object, Method method, Object... parameters) {
		method.setAccessible(true);
		try {
			return method.invoke(object, parameters);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static boolean isTypesCompatible(Class[] methodTypes, Class[] parameterTypes, boolean autoboxing) {
		if (methodTypes.length != parameterTypes.length) return false;
		for (int i = 0; i < methodTypes.length; i++) {
			if (!isTypeCompatible(methodTypes[i], parameterTypes[i], autoboxing)) return false;
		}
		return true;
	}

	public static boolean isTypeCompatible(Class methodType, Class parameterType, boolean autoboxing) {
		if (parameterType == null) return true;
		if (methodType.equals(parameterType)) return true;
		if (!autoboxing) return false;
		// check autoboxing
		if (methodType.equals(Float.class) && parameterType.equals(float.class)) return true;
		if (methodType.equals(float.class) && parameterType.equals(Float.class)) return true;
		if (methodType.equals(Integer.class) && parameterType.equals(int.class)) return true;
		if (methodType.equals(int.class) && parameterType.equals(Integer.class)) return true;
		if (methodType.equals(Double.class) && parameterType.equals(double.class)) return true;
		if (methodType.equals(double.class) && parameterType.equals(Double.class)) return true;
		if (methodType.equals(Long.class) && parameterType.equals(long.class)) return true;
		if (methodType.equals(long.class) && parameterType.equals(Long.class)) return true;
		return false;
	}

	public static Method getDeclaredMethodUsingAutoboxing(Class<?> clazz, String name, Class<?>... parameterTypes) {
		for (Method m : clazz.getDeclaredMethods()) {
			if (!name.equals(m.getName())) continue;
			if (isTypesCompatible(m.getParameterTypes(), parameterTypes, true)) return m;
		}
		if (clazz != Object.class)
			return getDeclaredMethodUsingAutoboxing(clazz.getSuperclass(), name, parameterTypes);
		return null;
	}

	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		Method m = null;
		try {
			m = clazz.getDeclaredMethod(name, parameterTypes);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchMethodException ex) {
			if (clazz != Object.class) {
				m = getDeclaredMethod(clazz.getSuperclass(), name, parameterTypes);
			}
		}
		return m;
	}

	public static Method getSetterMethod(Class<?> clazz, String property) {
		Method m = null;
		String methodName = "set" + Str.uppercaseFirstLetter(property);
		try {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
					m = method;
					break;
				}
			}
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		}
		if (m == null) {
			if (clazz != Object.class) {
				m = getSetterMethod(clazz.getSuperclass(), property);
			}
		}
		return m;
	}

	public static Field getDeclaredField(Class<?> clazz, String name) {
		Field f = null;
		try {
			f = clazz.getDeclaredField(name);
		} catch (SecurityException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchFieldException ex) {
			if (clazz != Object.class) {
				f = getDeclaredField(clazz.getSuperclass(), name);
			}
		}
		return f;
	}

	public static Class<?>[] getClasses(Object... objects) {
		Class<?>[] result = new Class[objects.length];
		for (int i = 0; i < objects.length; i++) {
			result[i] = objects[i] == null ? null : objects[i].getClass();
		}
		return result;
	}

	public static String[] getClassSimpleNames(Class... classes) {
		String[] names = new String[classes.length];
		for (int i = 0; i < classes.length; i++) {
			names[i] = classes[i] == null ? null : classes[i].getSimpleName();
		}
		return names;
	}

	public static String[] getClassSimpleNames(Object... objects) {
		return getClassSimpleNames(getClasses(objects));
	}

	public static Class<?> findClass(String classNameWithoutPackage, String... possiblePackageNames) {
		for (int i = 0; i < possiblePackageNames.length; i++) {
			String fullClassName = possiblePackageNames[i] + "." + classNameWithoutPackage;
			try {
				return Class.forName(fullClassName);
			} catch (ClassNotFoundException ex) {
				// nop, try next
			}
		}
		return null;
	}

	// -------------------
	// --- annotations ---
	// -------------------

	public static void processAnnotations(Object object, FieldAnnotationHandler handler) {
		processAnnotations(object, object.getClass(), handler);
	}

	public static void processAnnotations(Object object, Class<?> clazz, FieldAnnotationHandler handler) {
		Field[] fields = clazz.getDeclaredFields();
		for (int i = fields.length - 1; i >= 0; i--) {
			Annotation[] annotations = fields[i].getAnnotations();
			for (int j = 0; j < annotations.length; j++) {
				handler.handle(annotations[j], fields[i], object);
			}
		}

		Class<?> supa = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			processAnnotations(object, interfaces[i], handler);
		}
		if (supa != null && !supa.equals(Object.class)) processAnnotations(object, supa, handler);
	}

	public static void processAnnotations(Object object, MethodAnnotationHandler handler) {
		processAnnotations(object, object.getClass(), handler);
	}

	public static void processAnnotations(Object object, Class<?> clazz, MethodAnnotationHandler handler) {
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = methods.length - 1; i >= 0; i--) {
			Annotation[] annotations = methods[i].getAnnotations();
			for (int j = 0; j < annotations.length; j++) {
				handler.handle(annotations[j], methods[i], object);
			}
		}

		Class<?> supa = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			processAnnotations(object, interfaces[i], handler);
		}
		if (supa != null && !supa.equals(Object.class)) processAnnotations(object, supa, handler);
	}

	public static void processAnnotations(Object object, PropertyMethodAnnotationHandler handler, boolean getter,
			boolean setter) {
		processAnnotations(object, object.getClass(), handler, getter, setter);
	}

	public static void processAnnotations(Object object, Class<?> clazz, PropertyMethodAnnotationHandler handler,
			boolean getter, boolean setter) {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException ex) {
			throw new RuntimeException(ex);
		}
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
			if (getter) {
				Method method = propertyDescriptor.getReadMethod();
				if (method != null) {
					Annotation[] annotations = method.getAnnotations();
					for (int j = 0; j < annotations.length; j++) {
						handler.handle(annotations[j], propertyDescriptor, object);
					}
				}
			}
			if (setter) {
				Method method = propertyDescriptor.getWriteMethod();
				if (method != null) {
					Annotation[] annotations = method.getAnnotations();
					for (int j = 0; j < annotations.length; j++) {
						handler.handle(annotations[j], propertyDescriptor, object);
					}
				}
			}
		}
		Class<?> supa = clazz.getSuperclass();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			processAnnotations(object, interfaces[i], handler, getter, setter);
		}
		if (supa != null && !supa.equals(Object.class)) processAnnotations(object, supa, handler, getter, setter);
	}

	public static interface MethodAnnotationHandler {

		public void handle(Annotation annotation, Method method, Object object);

	}

	public static interface PropertyMethodAnnotationHandler {

		public void handle(Annotation annotation, PropertyDescriptor property, Object object);

	}

	public static interface FieldAnnotationHandler {

		public void handle(Annotation annotation, Field field, Object object);

	}
}
