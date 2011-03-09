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

import ilarkesto.di.BeanStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Data type for URL's.
 */
public final class Url implements BeanStorage<String> {

    private String base;
    private Map<String, String> parameters;
    private boolean immutable;

    public Url(Url template) {
        this.base = template.base;
        this.parameters = template.parameters == null ? null : new HashMap<String, String>(template.parameters);
    }

    public Url(String base) {
        this.base = base;
    }

    public boolean isInternal() {
        return !isExternal();
    }

    public boolean isExternal() {
        return isExternal(base);
    }

    public static boolean isExternal(String url) {
        return url.startsWith("http://") || url.startsWith("ftp://") || url.startsWith("mailto:")
                || url.startsWith("javascript:");
    }

    public String getBase() {
        return base;
    }

    public Url putAll(Map<String, ? extends String> parameters) {
        if (immutable) throw new RuntimeException("Url is immutable");
        if (this.parameters == null) this.parameters = new HashMap<String, String>();
        this.parameters.putAll(parameters);
        return this;
    }

    public Url put(String name, String value) {
        if (immutable) throw new RuntimeException("Url is immutable");
        if (value == null) return this;
        if (parameters == null) parameters = new HashMap<String, String>();
        parameters.put(name, value);
        return this;
    }

    public Url put(String name, Integer value) {
        if (value == null) return this;
        return put(name, value.toString());
    }

    public Url put(String name, Long value) {
        if (value == null) return this;
        return put(name, value.toString());
    }

    public Url setImmutable() {
        this.immutable = true;
        return this;
    }

    @Override
    public String toString() {
        return Str.constructUrl(base, parameters == null ? null : parameters);
    }

}
