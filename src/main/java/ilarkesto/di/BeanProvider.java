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

import java.util.Map;
import java.util.Set;

/**
 * Provides beans and performs autowireing.
 * 
 * @author wko
 */
public interface BeanProvider {

    /**
     * Provides a set of all existing bean names.
     */
    public Set<String> beanNames();

    /**
     * Gets a bean by name.
     */
    public <T> Object getBean(String beanName);

    /**
     * Gets a beans type by the beans name.
     */
    public Class getBeanType(String beanName);

    /**
     * Gets all beans by their type. All beans instanceof the given type are returned.
     */
    public <T> Set<T> getBeansByType(Class<T> type);

    /**
     * Gets all existing beans.
     */
    public Map<String, Object> getAllBeans();

    // --- autowireing ---

    /**
     * Autowire the given class.
     * 
     * @see Autowire#autowireClass(Class, IBeanProvider, IObjectStringMapper)
     */
    public <T> Class<T> autowireClass(Class<T> type);

    /**
     * Autowire the given bean.
     * 
     * @see Autowire#autowire(Object, IBeanProvider, IObjectStringMapper)
     */
    public <T> T autowire(T bean);

}
