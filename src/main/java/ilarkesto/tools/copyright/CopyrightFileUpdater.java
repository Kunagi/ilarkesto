/*
 * Copyright 2011 Witoslaw Koczewski
 * 
 * Foobar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * Foobar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.tools.copyright;

import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;
import ilarkesto.io.IO.FileProcessor;

import java.io.File;

public class CopyrightFileUpdater {

	private static Log log = Log.get(CopyrightFileUpdater.class);

	public static void main(String[] args) {
		File dir = new File("src/test/java");
		// File dir = new File("../kunagi/src/test/java");
		CopyrightFileProcessor processor = new CopyrightFileProcessor(new AgplTemplate(), "2011",
				"Witoslaw Koczewsi <wi@koczewski.de>", "Artjom Kochtchi");
		IO.process(dir, processor);
		log.info(processor.getCount(), "files updated");
	}

	static class CopyrightFileProcessor implements FileProcessor {

		private CopyrightTemplate template;
		private String years;
		private String[] owners;
		int count;

		public CopyrightFileProcessor(CopyrightTemplate template, String years, String... owners) {
			super();
			this.template = template;
			this.years = years;
			this.owners = owners;
		}

		@Override
		public void onFile(File file) {
			if (!file.getName().endsWith(".java")) return;
			String content = IO.readFile(file, IO.UTF_8);
			if (content == null) return;
			if (template.containsText(content)) return;
			content = template.getText(years, owners) + content;
			IO.writeFile(file, content, IO.UTF_8);
			count++;
			log.info("Copyrighted:", file);
		}

		public int getCount() {
			return count;
		}

		@Override
		public boolean onFolderBegin(File folder) {
			return true;
		}

		@Override
		public void onFolderEnd(File folder) {}

		@Override
		public boolean isAbortRequested() {
			return false;
		}

	}

}
