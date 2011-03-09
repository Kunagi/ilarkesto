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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class ExclusiveFileLock {

	private File file;
	private FileLock lock;

	public ExclusiveFileLock(File file) throws FileLockedException {
		this.file = file;

		file.getParentFile().mkdirs();

		FileChannel channel;
		try {
			channel = new RandomAccessFile(file, "rw").getChannel();
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		try {
			lock = channel.tryLock();
		} catch (OverlappingFileLockException ex) {
			lock = null;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		if (lock == null) throw new FileLockedException();
	}

	public void release() {
		try {
			lock.release();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public class FileLockedException extends Exception {

		private FileLockedException() {
			super("File already locked: " + file.getName());
		}
	}

}
