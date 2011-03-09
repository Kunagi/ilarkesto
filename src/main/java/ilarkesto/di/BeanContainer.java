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
package ilarkesto.di;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A storage for beans used for autowireing.
 * 
 * @author wko
 */
public final class BeanContainer extends ABeanProvider implements BeanStorage<Object> {

	private Map<String, Object>	beans;

	public Set<String> beanNames() {
		return beans.keySet();
	}

	public Object getBean(String beanName) {
		return beans.get(beanName);
	}

	public Class getBeanType(String beanName) {
		Object bean = beans.get(beanName);
		if (bean == null) return null;
		return bean.getClass();
	}

	public BeanContainer put(String name, Object bean) {
		beans.put(name, bean);
		return this;
	}

	public BeanContainer putAll(Map<String, ? extends Object> map) {
		beans.putAll(map);
		return this;
	}

	public BeanContainer putAll(BeanProvider beanProvider) {
		for (String bean : beanProvider.beanNames()) {
			beans.put(bean, beanProvider.getBean(bean));
		}
		return this;
	}

	public Map<String, Object> getBeans() {
		return beans;
	}

	// --- dependencies ---

	public BeanContainer(Map<String, Object> beans) {
		this.beans = beans;
	}

	public BeanContainer() {
		this.beans = new HashMap<String, Object>();
	}

}

// $Log: BeanContainer.java,v $
// Revision 1.1 2006/08/25 15:58:37 wko
// *** empty log message ***
//
