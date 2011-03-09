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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * This FileWriter writes all data to a temporary file. Olny on call on the close()-method the original file
 * will be deleted and replaced by the temporary file. This ensures a more secure way of writing files. When
 * the VM crashes, the original file will not be damaged.
 * 
 * @author wko
 */
public class TempFileWriter extends Writer {

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		out.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
		if (file.exists()) file.delete();
		tempFile.renameTo(file);
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			out.close();
		} catch (Throwable t) {}
		tempFile.delete();
	}

	// --- dependencies ---

	private File file;
	private File tempFile;
	private Writer out;

	public TempFileWriter(File file) throws IOException {
		this.file = file;

		if (file.exists() && !file.canWrite()) throw new IOException(file + " is not writable");

		tempFile = new File(file.getPath() + ".~tmp");
		out = new BufferedWriter(new FileWriter(tempFile));
	}

	public TempFileWriter(String filePath) throws IOException {
		this(new File(filePath));
	}

}
