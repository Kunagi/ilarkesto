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
package ilarkesto.persistence;

import ilarkesto.io.StringOutputStream;
import ilarkesto.io.IO.StringInputStream;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class Serializer {

	public abstract void setAlias(String alias, Class clazz);

	public abstract void serialize(Object bean, OutputStream out);

	public abstract Object deserialize(InputStream in);

	public final Object deserialize(String s) {
		if (s == null || s.length() == 0) return null;
		StringInputStream in = new StringInputStream(s);
		return deserialize(in);
	}

	public final String serializeToString(Object bean) {
		StringOutputStream out = new StringOutputStream();
		serialize(bean, out);
		return out.toString();
	}
}
