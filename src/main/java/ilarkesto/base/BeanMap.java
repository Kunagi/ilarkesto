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

import ilarkesto.core.logging.Log;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The BeanMap is a full Map implementation where a java object (bean) acts as the data storage. A call to the
 * get(key)-method invokes the getter in the bean and the set(key, value)-method invokes the setter.
 */
public class BeanMap<T> extends AbstractMap<String, Object> implements Cloneable {

	private transient T bean;

	private transient HashMap<String, Method> readMethods = new HashMap<String, Method>();

	private transient HashMap<String, Method> writeMethods = new HashMap<String, Method>();

	private transient HashMap<String, Class> types = new HashMap<String, Class>();

	/**
	 * An empty array. Used to invoke accessors via reflection.
	 */
	public static final Object[] NULL_ARGUMENTS = {};

	/**
	 * Maps primitive Class types to transformers. The transformer transform strings into the appropriate
	 * primitive wrapper.
	 */
	public static HashMap defaultTransformers = new HashMap();

	static {
		defaultTransformers.put(Boolean.TYPE, new Transformer() {

			public Object transform(Object input) {
				return Boolean.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Character.TYPE, new Transformer() {

			public Object transform(Object input) {
				return new Character(input.toString().charAt(0));
			}
		});
		defaultTransformers.put(Byte.TYPE, new Transformer() {

			public Object transform(Object input) {
				return Byte.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Short.TYPE, new Transformer() {

			public Object transform(Object input) {
				return Short.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Integer.TYPE, new Transformer() {

			public Object transform(Object input) {
				return Integer.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Long.TYPE, new Transformer() {

			public Object transform(Object input) {
				return Long.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Float.TYPE, new Transformer() {

			public Object transform(Object input) {
				return Float.valueOf(input.toString());
			}
		});
		defaultTransformers.put(Double.TYPE, new Transformer() {

			public Object transform(Object input) {
				return Double.valueOf(input.toString());
			}
		});
	}

	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Constructs a new empty <code>BeanMap</code>.
	 */
	public BeanMap() {}

	/**
	 * Constructs a new <code>BeanMap</code> that operates on the specified bean. If the given bean is
	 * <code>null</code>, then this map will be empty.
	 * 
	 * @param bean the bean for this map to operate on
	 */
	public BeanMap(T bean) {
		this.bean = bean;
		initialise();
	}

	// Map interface
	// -------------------------------------------------------------------------

	@Override
	public String toString() {
		return "BeanMap<" + String.valueOf(bean) + ">";
	}

	/**
	 * Clone this bean map using the following process:
	 * <ul>
	 * <li>If there is no underlying bean, return a cloned BeanMap without a bean.
	 * <li>Since there is an underlying bean, try to instantiate a new bean of the same type using
	 * Class.newInstance().
	 * <li>If the instantiation fails, throw a CloneNotSupportedException
	 * <li>Clone the bean map and set the newly instantiated bean as the underlying bean for the bean map.
	 * <li>Copy each property that is both readable and writable from the existing object to a cloned bean
	 * map.
	 * <li>If anything fails along the way, throw a CloneNotSupportedException.
	 * <ul>
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		BeanMap newMap = (BeanMap) super.clone();

		if (bean == null) {
			// no bean, just an empty bean map at the moment. return a newly
			// cloned and empty bean map.
			return newMap;
		}

		Object newBean = null;
		Class beanClass = null;
		try {
			beanClass = bean.getClass();
			newBean = beanClass.newInstance();
		} catch (Exception e) {
			// unable to instantiate
			throw new CloneNotSupportedException("Unable to instantiate the underlying bean \"" + beanClass.getName()
					+ "\": " + e);
		}

		try {
			newMap.setBean(newBean);
		} catch (Exception exception) {
			throw new CloneNotSupportedException("Unable to set bean in the cloned bean map: " + exception);
		}

		try {
			// copy only properties that are readable and writable. If its
			// not readable, we can't get the value from the old map. If
			// its not writable, we can't write a value into the new map.
			Iterator<String> readableKeys = readMethods.keySet().iterator();
			while (readableKeys.hasNext()) {
				String key = readableKeys.next();
				if (getWriteMethod(key) != null) {
					newMap.put(key, get(key));
				}
			}
		} catch (Exception exception) {
			throw new CloneNotSupportedException("Unable to copy bean values to cloned bean map: " + exception);
		}

		return newMap;
	}

	/**
	 * Puts all of the writable properties from the given BeanMap into this BeanMap. Read-only and Write-only
	 * properties will be ignored.
	 * 
	 * @param map the BeanMap whose properties to put
	 */
	public void putAllWriteable(BeanMap map) {
		Iterator<String> readableKeys = map.readMethods.keySet().iterator();
		while (readableKeys.hasNext()) {
			String key = readableKeys.next();
			if (getWriteMethod(key) != null) {
				this.put(key, map.get(key));
			}
		}
	}

	/**
	 * Returns true if the bean defines a property with the given name.
	 * <p>
	 * The given name must be a <code>String</code>; if not, this method returns false. This method will also
	 * return false if the bean does not define a property with that name.
	 * <p>
	 * Write-only properties will not be matched as the test operates against property read methods.
	 * 
	 * @param name the name of the property to check
	 * @return false if the given name is null or is not a <code>String</code>; false if the bean does not
	 *         define a property with that name; or true if the bean does define a property with that name
	 */
	@Override
	public boolean containsKey(Object name) {
		Method method = getReadMethod(name);
		return method != null;
	}

	/**
	 * Returns true if the bean defines a property whose current value is the given object.
	 * 
	 * @param value the value to check
	 * @return false true if the bean has at least one property whose current value is that object, false
	 *         otherwise
	 */
	@Override
	public boolean containsValue(Object value) {
		// use default implementation
		return super.containsValue(value);
	}

	/**
	 * Returns the value of the bean's property with the given name.
	 * <p>
	 * The given name must be a {@link String} and must not be null; otherwise, this method returns
	 * <code>null</code>. If the bean defines a property with the given name, the value of that property is
	 * returned. Otherwise, <code>null</code> is returned.
	 * <p>
	 * Write-only properties will not be matched as the test operates against property read methods.
	 * 
	 * @param name the name of the property whose value to return
	 * @return the value of the property with that name
	 */
	@Override
	public Object get(Object name) {
		if (bean != null) {
			Method method = getReadMethod(name);
			if (method != null) {
				try {
					return method.invoke(bean, NULL_ARGUMENTS);
				} catch (Exception e) {
					throw new RuntimeException("Invoking " + bean.getClass().getSimpleName() + "." + method.getName()
							+ "() failed", e);
				}
			}
		}
		return null;
	}

	/**
	 * Sets the bean property with the given name to the given value.
	 * 
	 * @param name the name of the property to set
	 * @param value the value to set that property to
	 * @return the previous value of that property
	 * @throws IllegalArgumentException if the given name is null; if the given name is not a {@link String};
	 *             if the bean doesn't define a property with that name; or if the bean property with that
	 *             name is read-only
	 */
	@Override
	public Object put(String name, Object value) throws IllegalArgumentException, ClassCastException {
		Log.DEBUG("------------- setting property ", name, "->", Utl.toStringWithType(value));
		if (bean != null) {
			Object oldValue = get(name);
			Reflect.setProperty(bean, name, value);
			return oldValue;
		}
		return null;
	}

	/**
	 * Returns the number of properties defined by the bean.
	 * 
	 * @return the number of properties defined by the bean
	 */
	@Override
	public int size() {
		return readMethods.size();
	}

	/**
	 * Get the keys for this BeanMap.
	 * <p>
	 * Write-only properties are <b>not</b> included in the returned set of property names, although it is
	 * possible to set their value and to get their type.
	 * 
	 * @return BeanMap keys. The Set returned by this method is not modifiable.
	 */
	@Override
	public Set<String> keySet() {
		return readMethods.keySet();
	}

	/**
	 * Gets a Set of MapEntry objects that are the mappings for this BeanMap.
	 * <p>
	 * Each MapEntry can be set but not removed.
	 * 
	 * @return the unmodifiable set of mappings
	 */
	@Override
	public Set entrySet() {
		return new AbstractSet() {

			@Override
			public Iterator iterator() {
				return entryIterator();
			}

			@Override
			public int size() {
				return BeanMap.this.readMethods.size();
			}
		};
	}

	/**
	 * Returns the values for the BeanMap.
	 * 
	 * @return values for the BeanMap. The returned collection is not modifiable.
	 */
	@Override
	public Collection values() {
		ArrayList answer = new ArrayList(readMethods.size());
		for (Iterator iter = valueIterator(); iter.hasNext();) {
			answer.add(iter.next());
		}
		return answer;
	}

	// Helper methods
	// -------------------------------------------------------------------------

	/**
	 * Returns the type of the property with the given name.
	 * 
	 * @param name the name of the property
	 * @return the type of the property, or <code>null</code> if no such property exists
	 */
	public Class getType(String name) {
		return types.get(name);
	}

	/**
	 * Convenience method for getting an iterator over the keys.
	 * <p>
	 * Write-only properties will not be returned in the iterator.
	 * 
	 * @return an iterator over the keys
	 */
	public Iterator keyIterator() {
		return readMethods.keySet().iterator();
	}

	/**
	 * Convenience method for getting an iterator over the values.
	 * 
	 * @return an iterator over the values
	 */
	public Iterator valueIterator() {
		final Iterator iter = keyIterator();
		return new Iterator() {

			public boolean hasNext() {
				return iter.hasNext();
			}

			public Object next() {
				Object key = iter.next();
				return get(key);
			}

			public void remove() {
				throw new UnsupportedOperationException("remove() not supported for BeanMap");
			}
		};
	}

	/**
	 * Convenience method for getting an iterator over the entries.
	 * 
	 * @return an iterator over the entries
	 */
	public Iterator entryIterator() {
		final Iterator iter = keyIterator();
		return new Iterator() {

			public boolean hasNext() {
				return iter.hasNext();
			}

			public Object next() {
				Object key = iter.next();
				Object value = get(key);
				return new MyMapEntry(BeanMap.this, key, value);
			}

			public void remove() {
				throw new UnsupportedOperationException("remove() not supported for BeanMap");
			}
		};
	}

	// Properties
	// -------------------------------------------------------------------------

	/**
	 * Returns the bean currently being operated on. The return value may be null if this map is empty.
	 * 
	 * @return the bean being operated on by this map
	 */
	public T getBean() {
		return bean;
	}

	/**
	 * Sets the bean to be operated on by this map. The given value may be null, in which case this map will
	 * be empty.
	 * 
	 * @param newBean the new bean to operate on
	 */
	public void setBean(T newBean) {
		bean = newBean;
		reinitialise();
	}

	/**
	 * Returns the accessor for the property with the given name.
	 * 
	 * @param name the name of the property
	 * @return the accessor method for the property, or null
	 */
	public Method getReadMethod(String name) {
		return readMethods.get(name);
	}

	/**
	 * Returns the mutator for the property with the given name.
	 * 
	 * @param name the name of the property
	 * @return the mutator method for the property, or null
	 */
	public Method getWriteMethod(String name) {
		return writeMethods.get(name);
	}

	// Implementation methods
	// -------------------------------------------------------------------------

	/**
	 * Returns the accessor for the property with the given name.
	 * 
	 * @param name the name of the property
	 * @return null if the name is null; null if the name is not a {@link String}; null if no such property
	 *         exists; or the accessor method for that property
	 */
	protected Method getReadMethod(Object name) {
		return readMethods.get(name);
	}

	/**
	 * Returns the mutator for the property with the given name.
	 * 
	 * @param name the name of the
	 * @return null if the name is null; null if the name is not a {@link String}; null if no such property
	 *         exists; null if the property is read-only; or the mutator method for that property
	 */
	protected Method getWriteMethod(Object name) {
		return writeMethods.get(name);
	}

	/**
	 * Reinitializes this bean. Called during {@link #setBean(Object)}. Does introspection to find properties.
	 */
	protected void reinitialise() {
		readMethods.clear();
		writeMethods.clear();
		types.clear();
		initialise();
	}

	private void initialise() {
		if (getBean() == null) return;

		Class beanClass = getBean().getClass();
		try {
			// BeanInfo beanInfo = Introspector.getBeanInfo( bean, null );
			BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			if (propertyDescriptors != null) {
				for (int i = 0; i < propertyDescriptors.length; i++) {
					PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
					if (propertyDescriptor != null) {
						String name = propertyDescriptor.getName();
						Method readMethod = propertyDescriptor.getReadMethod();
						Method writeMethod = propertyDescriptor.getWriteMethod();
						Class aType = propertyDescriptor.getPropertyType();

						if (readMethod != null) {
							readMethods.put(name, readMethod);
						}
						if (writeMethod != null) {
							writeMethods.put(name, writeMethod);
						}
						types.put(name, aType);
					}
				}
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	protected void firePropertyChange(Object key, Object oldValue, Object newValue) {}

	protected class MyMapEntry extends AbstractMapEntry {

		private BeanMap owner;

		protected MyMapEntry(BeanMap owner, Object key, Object value) {
			super(key, value);
			this.owner = owner;
		}

		@Override
		public Object setValue(Object value) {
			String key = (String) getKey();
			Object oldValue = owner.get(key);

			owner.put(key, value);
			Object newValue = owner.get(key);
			super.setValue(newValue);
			return oldValue;
		}
	}

	protected Object[] createWriteMethodArguments(Method method, Object value) throws IllegalAccessException,
			ClassCastException {
		try {
			if (value != null) {
				Class[] types = method.getParameterTypes();
				if (types != null && types.length > 0) {
					Class paramType = types[0];
					if (!paramType.isAssignableFrom(value.getClass())) {
						value = convertType(paramType, value);
					}
				}
			}
			Object[] answer = { value };
			return answer;
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected Object convertType(Class newType, Object value) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		// try call constructor
		Class[] types = { value.getClass() };
		try {
			Constructor constructor = newType.getConstructor(types);
			Object[] arguments = { value };
			return constructor.newInstance(arguments);
		} catch (NoSuchMethodException e) {
			// try using the transformers
			Transformer transformer = getTypeTransformer(newType);
			if (transformer != null) { return transformer.transform(value); }
			return value;
		}
	}

	protected Transformer getTypeTransformer(Class aType) {
		return (Transformer) defaultTransformers.get(aType);
	}

	private static interface Transformer {

		public Object transform(Object input);

	}

	private abstract class AbstractMapEntry extends AbstractKeyValue implements Map.Entry {

		protected AbstractMapEntry(Object key, Object value) {
			super(key, value);
		}

		public Object setValue(Object value) {
			Object answer = this.value;
			this.value = value;
			return answer;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) { return true; }
			if (obj instanceof Map.Entry == false) { return false; }
			Map.Entry other = (Map.Entry) obj;
			return (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey()))
					&& (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
		}

		@Override
		public int hashCode() {
			return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
		}

	}

	public abstract class AbstractKeyValue {

		protected Object key;
		protected Object value;

		protected AbstractKeyValue(Object key, Object value) {
			super();
			this.key = key;
			this.value = value;
		}

		public Object getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public String toString() {
			return new StringBuffer().append(getKey()).append('=').append(getValue()).toString();
		}

	}
}
