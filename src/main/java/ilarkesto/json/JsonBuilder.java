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
package ilarkesto.json;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class JsonBuilder {

	private SortedMap<String, Object> elements = new TreeMap<String, Object>();

	public void string(String name, String string) {
		elements.put(name, '"' + string + '"');
	}

	public JsonBuilder object(String name) {
		JsonBuilder object = new JsonBuilder();
		elements.put(name, object);
		return object;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	private String toString(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		indent++;
		for (Map.Entry<String, Object> entry : elements.entrySet()) {
			indent(sb, indent);
			sb.append("\"").append(entry.getKey()).append("\" : ");
			Object o = entry.getValue();
			if (o instanceof JsonBuilder) {
				sb.append(((JsonBuilder) o).toString(indent));
			} else {
				sb.append(o);
			}
			sb.append(",\n");
		}
		indent--;
		indent(sb, indent);
		sb.append("}");
		return sb.toString();
	}

	private void indent(StringBuilder sb, int indent) {
		for (int i = 0; i < indent; i++)
			sb.append("  ");
	}

}
