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

import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A bean provider which wrapps other bean providers.
 * 
 * @author wko
 */
public final class MultiBeanProvider extends ABeanProvider {

	private static final Log LOG = Log.get(MultiBeanProvider.class);

	private Set<BeanProvider> beanProviders = new HashSet<BeanProvider>();
	private Map<String, BeanProvider> beanToBeanProvider = new HashMap<String, BeanProvider>();

	public synchronized void addBeanProvider(Object object) {
		if (object == null) throw new IllegalArgumentException("object == null");

		// identify object and create beanProvider
		BeanProvider beanProvider;
		if (object instanceof BeanProvider) {
			beanProvider = (BeanProvider) object;
		} else {
			if (object instanceof Map) {
				beanProvider = new BeanContainer((Map<String, Object>) object);
			} else {
				beanProvider = new ReflectionBeanProvider(object);
			}
		}

		// get objectStringMapper from beanProvider
		if (objectStringMapper == null && beanProvider instanceof ABeanProvider)
			objectStringMapper = ((ABeanProvider) beanProvider).objectStringMapper;

		// register beanProvider for its beans
		for (String beanName : beanProvider.beanNames()) {
			if ("beanProvider".equals(beanName)) throw new RuntimeException("Forbidden bean: beanProvider");
			beanToBeanProvider.put(beanName, beanProvider);
		}

		// add beanProvider
		beanProviders.add(beanProvider);
	}

	public Set<String> beanNames() {
		return beanToBeanProvider.keySet();
	}

	public <T> Object getBean(String beanName) {
		BeanProvider provider = beanToBeanProvider.get(beanName);
		if (provider == null) throw new BeanDoesNotExisException(beanName);
		return provider.getBean(beanName);
	}

	public Class getBeanType(String beanName) {
		BeanProvider provider = beanToBeanProvider.get(beanName);
		if (provider == null) throw new BeanDoesNotExisException(beanName);
		return provider.getBeanType(beanName);
	}

	@Override
	public String toString() {
		return "(" + Str.concat(beanProviders, ", ") + ")";
	}

	// --- dependencies ---

}
