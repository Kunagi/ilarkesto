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

import ilarkesto.base.Str;
import ilarkesto.concurrent.ATask;
import ilarkesto.core.logging.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ZipTask extends ATask {

    private static final Log LOG = Log.get(ZipTask.class);

    private File currentFile;
    private List<Failure> failures;
    private int fileCount;

    // --- dependencies ---

    private File zipfile;
    private File[] files;
    private FileFilter filter;
    private int estimatedFileCount;

    public ZipTask(File zipfile, File... files) {
        this.zipfile = zipfile;
        this.files = files;
    }

    public void setFilter(FileFilter filter) {
        this.filter = filter;
    }

    public void setEstimatedFileCount(int count) {
        this.estimatedFileCount = count;
    }

    // --- ---

    @Override
    protected void perform() {
        failures = new ArrayList<Failure>();
        LOG.debug("Zipping", zipfile.getPath(), " -> ", files);
        IO.zip(zipfile, files, filter, new Observer());
    }

    @Override
    public float getProgress() {
        return estimatedFileCount == 0 ? super.getProgress() : (float) fileCount / (float) estimatedFileCount;
    }

    @Override
    public String getProgressMessage() {
        return currentFile == null ? null : currentFile.getName();
    }

    class Observer implements IO.ZipObserver {

        public boolean isAbortRequested() {
            return ZipTask.this.isAbortRequested();
        }

        public void onFileBegin(File f) {
            currentFile = f;
        }

        public void onFileEnd(File f) {
            fileCount++;
            currentFile = null;
        }

        public void onFileError(File f, Throwable ex) {
            Failure failure = new Failure(f, ex);
            failures.add(failure);
            LOG.debug(failure);
        }

    }

    public List<Failure> getFailures() {
        return failures;
    }

    public int getFileCount() {
        return fileCount;
    }

    public static class Failure {

        private File file;
        private Throwable exception;

        public Failure(File file, Throwable ex) {
            this.file = file;
            this.exception = ex;
        }

        public File getFile() {
            return file;
        }

        public Throwable getException() {
            return exception;
        }

        @Override
        public String toString() {
            return file.getName() + ": " + Str.getRootCauseMessage(exception);
        }

    }
}
