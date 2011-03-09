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
package ilarkesto.io;

import ilarkesto.core.logging.Log;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DynamicClassLoader extends ClassLoader {

	private static final Log LOG = Log.get(DynamicClassLoader.class);

	private String[] packages;
	private Map<String, Long> typeModificationTimes = new HashMap<String, Long>();

	public DynamicClassLoader(ClassLoader parent, String... packages) {
		super(parent);
		this.packages = packages;
		if (packages.length == 0) throw new IllegalArgumentException("At least one package required");
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		for (String pack : packages) {
			if (name.startsWith(pack)) {
				String typeName = name.replace('.', '/') + ".class";
				URL url = getParent().getResource(typeName);
				String fileName = url.getFile();
				File file = new File(fileName);
				if (!file.exists()) throw new RuntimeException("File does not exist: " + file.getPath());

				// Long lastModified = typeModificationTimes.get(name);
				// if (lastModified != null && lastModified == file.lastModified()) {
				//					
				// }
				//
				// typeModificationTimes.put(name, file.lastModified());

				LOG.debug("Defining class:", name);
				byte[] data = IO.readFileToByteArray(file);
				Class<?> type = defineClass(name, data, 0, data.length);
				resolveClass(type);
				return type;
			}
		}
		return super.loadClass(name);
	}

}
