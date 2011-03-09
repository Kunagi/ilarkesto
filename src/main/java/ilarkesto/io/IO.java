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

import ilarkesto.io.zip.Deflater;
import ilarkesto.io.zip.ZipEntry;
import ilarkesto.io.zip.ZipFile;
import ilarkesto.io.zip.ZipOutputStream;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Set of static methods to easy work with Streams or Images
 */
public abstract class IO {

	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";

	public static final int CR_INT = 13;
	public static final char CR = (char) CR_INT;

	public static final String ISO_LATIN_1 = "ISO-8859-1";
	public static final String UTF_8 = "UTF-8";

	private static LinkedList<Properties> properties = new LinkedList<Properties>();
	private static LinkedList<File> propertiesFiles = new LinkedList<File>();

	public static long getSize(File file) {
		if (file.isFile()) return file.length();
		if (file.isDirectory()) {
			long size = 0;
			File[] subfiles = file.listFiles();
			if (subfiles != null) {
				for (File f : subfiles) {
					size += getSize(f);
				}
			}
			return size;
		}
		return 0;
	}

	public static File getFirstExistingFile(File... files) {
		for (File file : files) {
			if (file.exists()) return file;
		}
		return null;
	}

	public static File getFirstExistingFile(String... filePaths) {
		for (String path : filePaths) {
			File file = new File(path);
			if (file.exists()) return file;
		}
		return null;
	}

	public static String getFirstExistingFilePath(String... filePaths) {
		for (String path : filePaths) {
			if (new File(path).exists()) return path;
		}
		return null;
	}

	public static byte[] hash(String algorithm, byte[] input) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		md.reset();
		md.update(input);
		return md.digest();
	}

	public static byte[] hash(String algorithm, InputStream in) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		md.reset();

		byte[] block = new byte[1000];
		try {
			while (true) {
				int amountRead;
				amountRead = in.read(block);
				if (amountRead == -1) {
					break;
				}
				md.update(block, 0, amountRead);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return md.digest();
	}

	public static byte[] hash(String algorithm, File file) {
		BufferedInputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		byte[] hash = hash(algorithm, in);
		close(in);
		return hash;
	}

	public static String[] getFilenames(File... files) {
		if (files == null) return null;
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			names[i] = files[i].getName();
		}
		return names;
	}

	public static void process(String path, FileProcessor processor) {
		process(new File(path), processor);
	}

	public static void process(File root, FileProcessor processor) {
		if (root.isDirectory()) {
			boolean continu = processor.onFolderBegin(root);
			if (!continu) return;
			for (File file : root.listFiles()) {
				process(file, processor);
				if (processor.isAbortRequested()) return;
			}
			processor.onFolderEnd(root);
		} else {
			processor.onFile(root);
		}
	}

	public static List<File> findFiles(File root, FileFilter filter) {
		List<File> ret = new LinkedList<File>();
		File[] files = root.listFiles();
		if (files != null) {
			for (File file : files) {
				if (filter.accept(file)) ret.add(file);
				if (file.isDirectory()) {
					ret.addAll(findFiles(file, filter));
				}
			}
		}
		return ret;
	}

	public static File findFile(File root, FileFilter filter) {
		File[] files = root.listFiles();
		if (files == null) return null;
		for (File file : files) {
			if (filter.accept(file)) return file;
			if (file.isDirectory()) {
				File f = findFile(file, filter);
				if (f != null) return f;
			}
		}
		return null;
	}

	public static interface FileProcessor {

		boolean onFolderBegin(File folder);

		void onFolderEnd(File folder);

		void onFile(File file);

		boolean isAbortRequested();

	}

	public static String toHexString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			String s = Integer.toHexString(data[i]);
			if (s.length() == 1) {
				sb.append('0').append(s);
			} else if (s.length() == 8) {
				sb.append(s.substring(6));
			} else {
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public static void closeQuiet(InputStream in) {
		try {
			in.close();
		} catch (IOException ex) {}
	}

	public static void closeQuiet(OutputStream out) {
		try {
			out.close();
		} catch (IOException ex) {}
	}

	public static void closeQuiet(Socket socket) {
		try {
			socket.close();
		} catch (IOException ex) {}
	}

	public static void close(Socket socket) {
		try {
			socket.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void close(InputStream in) {
		if (in == null) return;
		try {
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void close(OutputStream out) {
		if (out == null) return;
		try {
			out.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void close(Writer out) {
		if (out == null) return;
		try {
			out.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void closeQuiet(Writer out) {
		if (out == null) return;
		try {
			out.close();
		} catch (IOException ex) {}
	}

	public static void close(Reader in) {
		if (in == null) return;
		try {
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void closeQuiet(Reader in) {
		if (in == null) return;
		try {
			in.close();
		} catch (IOException ex) {}
	}

	public static void createDirectory(String path) {
		createDirectory(new File(path));
	}

	public static void createDirectory(File dir) {
		if (dir.exists()) {
			if (dir.isDirectory()) return;
			throw new RuntimeException("A file already exists: " + dir.getPath());
		}
		if (!dir.mkdirs()) throw new RuntimeException("Failed to create directory: " + dir.getPath());
	}

	public static String getFileExtension(String filename) {
		int idx = filename.lastIndexOf('.');
		if (idx < 0 || idx == filename.length() - 1) return filename;
		return filename.substring(idx + 1);
	}

	public static String getFileMimeType(String filename) {
		return "application/" + getFileExtension(filename);
	}

	public static void move(File from, File to) {
		move(from, to, false);
	}

	public static void move(File from, File to, boolean overwrite) {
		if (to.exists()) {
			if (!overwrite)
				throw new RuntimeException("Moving file " + from + " to " + to + " failed. File already exists.");
			delete(to);
		}
		createDirectory(to.getParentFile());
		if (from.renameTo(to)) return;
		copyFile(from, to);
		delete(from);
	}

	public static boolean isHttpAvailable() {
		try {
			downloadUrl("http://www.google.com", null, null);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String getHostName() {
		String[] hostnames = getHostNames();
		if (hostnames.length == 0) return null;
		if (hostnames.length == 1) return hostnames[0];
		for (int i = 0; i < hostnames.length; i++) {
			if (!"localhost".equals(hostnames[i])) return hostnames[i];
		}
		return hostnames[0];
	}

	public static String[] getHostNames() {
		String localhostName;
		try {
			localhostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			throw new RuntimeException(ex);
		}
		InetAddress ia[];
		try {
			ia = InetAddress.getAllByName(localhostName);
		} catch (UnknownHostException ex) {
			throw new RuntimeException(ex);
		}
		String[] sa = new String[ia.length];
		for (int i = 0; i < ia.length; i++) {
			sa[i] = ia[i].getHostName();
		}
		return sa;
	}

	public static URLConnection post(URL url, Map<String, String> parameters, String encoding, String username,
			String password) throws IOException {
		StringBuffer sb = null;
		if (parameters != null) {
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				if (sb == null) {
					sb = new StringBuffer();
				} else {
					sb.append("&");
				}
				sb.append(URLEncoder.encode(entry.getKey(), UTF_8));
				sb.append("=");
				sb.append(URLEncoder.encode(entry.getValue(), UTF_8));
			}
		}
		if (sb == null) sb = new StringBuffer();

		URLConnection connection = url.openConnection();
		if (username != null) {
			connection.setRequestProperty("Authorization",
				"Basic " + Base64.encodeBytes((username + ":" + password).getBytes()));
		}
		connection.setDoOutput(true);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), encoding));
		out.println(sb.toString());
		out.println();
		close(out);
		return connection;
	}

	public static String postAndGetResult(String url, Map<String, String> parameters, String encoding, String username,
			String password) {
		try {
			return postAndGetResult(new URL(url), parameters, encoding, username, password);
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String postAndGetResult(URL url, Map<String, String> parameters, String encoding, String username,
			String password) throws IOException {
		URLConnection connection = post(url, parameters, encoding, username, password);
		encoding = connection.getContentEncoding();
		if (encoding == null) encoding = UTF_8;
		return readToString(connection.getInputStream(), encoding);
	}

	public static int httpPOST(String url, String username, String password, InputStream body, OutputStream response) {
		return httpRequest("POST", url, username, password, body, response);
	}

	public static int httpDELETE(String url, String username, String password, OutputStream response) {
		return httpRequest("DELETE", url, username, password, null, response);
	}

	public static int httpRequest(String method, String url, String username, String password, InputStream body,
			OutputStream response) {
		URL javaUrl;
		try {
			javaUrl = new URL(url);
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Malformed URL: " + url, ex);
		}
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) javaUrl.openConnection();
			connection.setRequestMethod(method);
		} catch (IOException ex) {
			throw new RuntimeException("Opening HTTP URL failed.", ex);
		}

		if (username != null) {
			// write auth header
			String credential = username;
			if (password != null) credential += ":" + password;
			String encodedCredential = Base64.encodeBytes(credential.getBytes());
			connection.setRequestProperty("Authorization", "Basic " + encodedCredential);
		}

		if (body != null) {
			// write body if we're doing POST or PUT
			connection.setDoOutput(true);
			try {
				copyData(body, connection.getOutputStream());
			} catch (IOException ex) {
				throw new RuntimeException("Writing HTTP request data failed.", ex);
			}
		}

		// do request
		try {
			connection.connect();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		int responseCode;
		try {
			responseCode = connection.getResponseCode();
		} catch (IOException ex) {
			throw new RuntimeException("Reading HTTP response code failed.", ex);
		}

		// pipe response
		if (response != null) {
			try {
				copyData(connection.getInputStream(), response);
			} catch (IOException ex) {
				throw new RuntimeException("Reading HTTP response failed.", ex);
			} finally {
				connection.disconnect();
			}
		}

		// cleanup
		connection.disconnect();

		return responseCode;
	}

	public static URL getResource(String resourceName) {
		return IO.class.getClassLoader().getResource(resourceName);
	}

	public static URL getResource(Class relative, String resourceName) {
		return relative.getResource(resourceName);
	}

	public static boolean existResource(String resourceName) {
		return getResource(resourceName) != null;
	}

	public static Icon getIcon(String resourceName) {
		return new ImageIcon(getResource(resourceName));
	}

	public static void appendLine(String file, String line) throws IOException {
		File f = new File(file);
		if (!f.exists()) createDirectory(f.getParentFile());
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
		out.println(line);
		out.close();
	}

	public static boolean isFinished(Process p) {
		try {
			p.exitValue();
		} catch (IllegalThreadStateException e) {
			return false;
		}
		return true;
	}

	public static void write(byte[] data, File file, long position) {
		try {
			RandomAccessFile f = new RandomAccessFile(file, "rw");
			f.seek(position);
			f.write(data);
			f.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static byte[] readBytes(File file, int offset, int size) {
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(file, "r");
			f.seek(offset);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		byte[] data = new byte[size];
		try {
			f.readFully(data, 0, size);
			f.close();
			return data;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void write2Bytes(OutputStream out, int bytes) throws IOException {
		int high = bytes / 0x100;
		int low = bytes - high * 0x100;
		out.write(high);
		out.write(low);
	}

	public static int read4Bytes(InputStream in) throws IOException {
		int value = 0;
		int i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += 0x01000000 * i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += 0x010000 * i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += 0x0100 * i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += i;

		return value;
	}

	public static int read3Bytes(InputStream in) throws IOException {
		int value = 0;
		int i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += 0x010000 * i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += 0x0100 * i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += i;

		return value;
	}

	public static int read2Bytes(InputStream in) throws IOException {
		int value = 0;
		int i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += 0x0100 * i;

		i = in.read();
		if (i < 0) throw new IOException("Unexpected end of file.");
		value += i;

		return value;
	}

	public static List<File> listFiles(File parent) {
		List<File> ret = new ArrayList<File>();
		if (parent == null) return ret;
		File[] files = parent.listFiles();
		if (files == null) return ret;
		for (File file : files) {
			ret.add(file);
		}
		return ret;
	}

	public static List<File> listFiles(File parent, FileFilter filter) {
		return filterFiles(parent.listFiles(), filter);
	}

	public static List<File> filterFiles(File[] files, FileFilter filter) {
		ArrayList<File> al = new ArrayList<File>();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (filter.accept(files[i])) {
					al.add(files[i]);
				}

			}
		}
		return al;
	}

	public static File[] toFileArray(Collection c) {
		Object[] oa = c.toArray();
		File[] fa = new File[oa.length];
		System.arraycopy(oa, 0, fa, 0, fa.length);
		return fa;
	}

	public static int executeProcessAndWait(String command) throws IOException {
		Process p = Runtime.getRuntime().exec(command);
		int result;
		try {
			result = p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Integer.MIN_VALUE;
		}
		return result;
	}

	public static void unzip(File zipfile, File destinationDir) {
		unzip(zipfile, destinationDir, null);
	}

	public static void unzip(File zipfile, File destinationDir, UnzipObserver observer) {
		ZipFile zf;
		try {
			zf = new ZipFile(zipfile);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		if (observer != null) observer.onFileCountAvailable(zf.size());
		try {
			Enumeration entries = zf.entries();
			while (entries.hasMoreElements()) {
				if (observer != null && observer.isAbortRequested()) return;
				ZipEntry ze = (ZipEntry) entries.nextElement();
				String name = ze.getName();
				name = name.replace((char) 129, '\u00FC');
				name = name.replace((char) 154, '\u00DC');
				name = name.replace((char) 148, '\u00F6');
				name = name.replace((char) 153, '\u00D6');
				name = name.replace((char) 132, '\u00E4');
				name = name.replace((char) 142, '\u00C4');
				name = name.replace((char) 225, '\u00DF');
				if (ze.isDirectory()) continue;
				File f = new File(destinationDir.getPath() + "/" + name);
				if (observer != null) observer.onFileBegin(f);
				try {
					createDirectory(f.getParentFile());
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
					InputStream in = zf.getInputStream(ze);
					copyData(new BufferedInputStream(in), out);
					out.close();
					long lastModified = ze.getTime();
					if (lastModified >= 0) setLastModified(f, lastModified);
				} catch (Exception ex) {
					if (observer == null) throw new RuntimeException(ex);
					observer.onFileError(f, ex);
				}
				if (observer != null) observer.onFileEnd(f);
			}
		} finally {
			try {
				zf.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void setLastModified(File file, long time) {
		if (!file.setLastModified(time)) throw new RuntimeException("Settring lastModified on " + file + " failed.");
	}

	public static interface UnzipObserver {

		void onFileCountAvailable(int count);

		void onFileBegin(File f);

		void onFileEnd(File f);

		void onFileError(File f, Throwable ex);

		boolean isAbortRequested();

	}

	public static void zip(File zipfile, File... files) {
		zip(zipfile, files, null);
	}

	public static void zip(File zipfile, File[] files, FileFilter filter) {
		zip(zipfile, files, filter, null);
	}

	public static void zip(File zipfile, File[] files, FileFilter filter, ZipObserver observer) {
		if (zipfile.exists()) delete(zipfile);
		createDirectory(zipfile.getParentFile());
		File tempFile = new File(zipfile.getPath() + "~");

		try {
			zip(new FileOutputStream(tempFile), files, filter, observer);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}

		if (observer != null && observer.isAbortRequested()) {
			IO.delete(tempFile);
		} else {
			move(tempFile, zipfile);
		}
	}

	public static void zip(OutputStream os, File... files) {
		zip(os, files, null, null);
	}

	public static void zip(OutputStream os, File[] files, FileFilter filter, ZipObserver observer) {
		ZipOutputStream zipout;
		try {
			zipout = new ZipOutputStream(new BufferedOutputStream(os));
			zipout.setLevel(Deflater.BEST_COMPRESSION);
			for (int i = 0; i < files.length; i++) {
				if (!files[i].exists()) continue;
				addZipEntry(zipout, "", files[i], filter, observer);
			}
			zipout.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void addZipEntry(ZipOutputStream zipout, String zippath, File f, FileFilter filter,
			ZipObserver observer) throws Exception {
		if (filter != null && !filter.accept(f)) return;
		if (observer != null) {
			if (observer.isAbortRequested()) return;
			observer.onFileBegin(f);
		}
		if (f.isDirectory()) {
			File[] fa = f.listFiles();
			for (int i = 0; i < fa.length; i++) {
				addZipEntry(zipout, zippath + f.getName() + "/", fa[i], filter, observer);
			}
		} else {
			try {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
				ZipEntry entry = new ZipEntry(zippath + f.getName());
				zipout.putNextEntry(entry);
				copyData(in, zipout);
				in.close();
				zipout.closeEntry();
			} catch (Exception ex) {
				if (observer == null) { throw ex; }
				observer.onFileError(f, ex);
			}
		}
		if (observer != null) observer.onFileEnd(f);
	}

	public static interface ZipObserver {

		void onFileBegin(File f);

		void onFileEnd(File f);

		void onFileError(File f, Throwable ex);

		boolean isAbortRequested();

	}

	public static void deleteContents(String folder) {
		deleteContents(new File(folder));
	}

	public static void deleteContents(File folder) {
		if (!folder.exists()) return;
		if (!folder.isDirectory()) throw new RuntimeException("Not a folder: " + folder.getAbsolutePath());
		for (File file : folder.listFiles()) {
			delete(file);
		}
	}

	public static void delete(String file) {
		delete(new File(file));
	}

	public static void delete(File f) {
		if (!f.exists()) return;
		if (f.isDirectory()) {
			File[] fa = f.listFiles();
			for (int i = 0; i < fa.length; i++) {
				delete(fa[i]);
			}
		}
		if (!f.delete()) { throw new RuntimeException("Deleting file failed: " + f.getPath()); }
	}

	@Deprecated
	public static void writeImage(Image image, String type, String file) throws IOException {
		File f = new File(file);
		createDirectory(f.getParentFile());
		ImageIO.write(toBufferedImage(image), type, f);
	}

	public static void writeImage(Image image, int width, int height, String type, String file) throws IOException {
		File f = new File(file);
		createDirectory(f.getParentFile());
		ImageIO.write(toBufferedImage(image, width, height), type, f);
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) return (BufferedImage) img;
		return toBufferedImage(img, img.getWidth(null), img.getHeight(null));
	}

	public static BufferedImage toBufferedImage(Image img, int width, int height) {
		if (img instanceof BufferedImage) return (BufferedImage) img;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return image;
	}

	public static BufferedImage loadImage(File file) {
		BufferedImage image;
		try {
			image = ImageIO.read(file);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (image == null) throw new RuntimeException("Unsupported image format.");
		return image;
	}

	public static BufferedImage loadImage(byte[] data) {
		BufferedImage image;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			image = ImageIO.read(in);
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (image == null) throw new RuntimeException("Unsupported image format.");
		return image;
	}

	public static BufferedImage loadImage(String resourcePath) {
		try {
			return ImageIO.read(IO.class.getClassLoader().getResource(resourcePath));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void saveScaled(BufferedImage image, String type, String file, int maxWidth, int maxHeight)
			throws IOException {
		Image scaled = getScaled(image, maxWidth, maxHeight);
		writeImage(scaled, type, file);
	}

	public static void scaleImage(String sourceFile, String destinationFile, String destinationType, int maxWidth,
			int maxHeight) throws IOException {
		saveScaled(loadImage(new File(sourceFile)), destinationType, destinationFile, maxWidth, maxHeight);
	}

	public static Image getScaled(BufferedImage image, int maxWidth, int maxHeight) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (width <= maxWidth && height <= maxHeight) { return image; }

		if (width > maxWidth) {
			width = maxWidth;
			height = height * maxWidth / image.getWidth();
		}

		int h;
		int w;
		if (height > maxHeight) {
			h = maxHeight;
			w = width * maxHeight / height;
		} else {
			h = height;
			w = width;
		}

		return image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	}

	public static Image scaledToWidth(BufferedImage image, int targetWidth) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (width == targetWidth) { return image; }

		width = targetWidth;
		height = height * targetWidth / image.getWidth();

		return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

	public static Image scaledToHeight(BufferedImage image, int targetHeight) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (height == targetHeight) { return image; }

		height = targetHeight;
		width = width * targetHeight / image.getHeight();

		return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

	public static void copyDataToFile(byte[] data, File file) {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		copyDataToFile(in, file);
		close(in);
	}

	public static void downloadToFile(URLConnection connection, File file) {
		InputStream is;
		try {
			is = connection.getInputStream();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		try {
			copyDataToFile(is, file);
		} finally {
			close(is);
		}
	}

	public static void copyDataToFile(InputStream is, File file) {
		copyDataToFile(is, file, null);
	}

	public static void copyDataToFile(InputStream is, File dst, CopyObserver observer) {
		createDirectory(dst.getParentFile());
		File tmp = new File(dst.getPath() + "~" + System.currentTimeMillis());

		BufferedInputStream in;
		try {
			if (is instanceof BufferedInputStream) {
				in = (BufferedInputStream) is;
			} else {
				in = new BufferedInputStream(is);
			}
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tmp, false));
			copyData(in, out, observer);
			in.close();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			tmp.delete();
			throw new RuntimeException(ex);
		}
		if (dst.exists()) if (!dst.delete()) {
			delete(tmp);
			throw new RuntimeException("Overwriting file '" + dst + "' failed.");
		}
		if (!tmp.renameTo(dst)) {
			IO.delete(tmp);
			throw new RuntimeException("Moving '" + tmp + "' to '" + dst + "' failed.");
		}
	}

	public static void copyFile(File source, File destination) {
		if (source.getAbsolutePath().equals(destination.getAbsolutePath())) return;
		if (source.isDirectory()) {
			copyFiles(source.listFiles(), destination);
			return;
		}
		FileInputStream in;
		try {
			in = new FileInputStream(source);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		try {
			copyDataToFile(in, destination);
		} finally {
			close(in);
		}
	}

	public static void copyFile(File sourceFile, OutputStream dst) {
		copyFile(sourceFile.getPath(), dst);
	}

	public static void copyFile(String sourceFile, String destinationFile) {
		copyFile(new File(sourceFile), new File(destinationFile));
	}

	public static void copyFile(String src, OutputStream dst) {
		BufferedInputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(src));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		try {
			copyData(in, dst);
		} finally {
			close(in);
		}
	}

	public static void copyFiles(File[] files, File destinationDir) {
		copyFiles(files, destinationDir, null);
	}

	public static void copyFiles(File[] files, File destinationDir, FileFilter filter) {
		createDirectory(destinationDir);
		for (File f : files) {
			if (filter != null && !filter.accept(f)) continue;
			if (f.isDirectory()) {
				copyFiles(f.listFiles(), new File(destinationDir + "/" + f.getName()), filter);
			} else {
				copyFile(f, new File(destinationDir + "/" + f.getName()));
			}
		}
	}

	public static void copyFiles(Collection<File> files, File destinationDir) {
		copyFiles(files, destinationDir, null);
	}

	public static void copyFiles(Collection<File> files, File destinationDir, FileFilter filter) {
		createDirectory(destinationDir);
		for (File f : files) {
			if (filter != null && !filter.accept(f)) continue;
			if (f.isDirectory()) {
				copyFiles(f.listFiles(), new File(destinationDir + "/" + f.getName()), filter);
			} else {
				copyFile(f, new File(destinationDir + "/" + f.getName()));
			}
		}
	}

	public static void copyFiles(String[] files, String destinationDir) {
		createDirectory(destinationDir);
		for (String file : files) {
			File f = new File(file);
			if (f.isDirectory()) {
				copyFiles(f.listFiles(), new File(destinationDir + "/" + f.getName()));
			} else {
				copyFile(file, destinationDir + "/" + f.getName());
			}
		}
	}

	public static void copyData(File file, OutputStream out) {
		BufferedInputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
		copyData(in, out);
		close(in);
	}

	public static void copyData(InputStream in, OutputStream out) {
		copyData(in, out, null);
	}

	public static void copyData(InputStream in, OutputStream out, CopyObserver observer) {
		byte[] block = new byte[1000];
		try {
			while (true) {
				if (observer != null && observer.isAbortRequested()) return;
				int amountRead;
				amountRead = in.read(block);
				if (amountRead == -1) {
					break;
				}
				out.write(block, 0, amountRead);
				if (observer != null) observer.dataCopied(amountRead);
			}

			out.flush();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public interface CopyObserver {

		boolean isAbortRequested();

		void totalSizeDetermined(long bytes);

		void dataCopied(long bytes);

	}

	private static void copyData(InputStream in, OutputStream out, long length) throws IOException {

		// if (!(in instanceof BufferedInputStream)) in = new
		// BufferedInputStream(in);
		// if (!(out instanceof BufferedOutputStream)) out = new
		// BufferedOutputStream(out);

		byte[] ba = new byte[(int) length];
		for (int i = 0; i < length; i++) {
			ba[i] = (byte) in.read();
		}
		out.write(ba, 0, (int) length);
		out.flush();
	}

	public static byte[] readToByteArray(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyData(in, out);
		close(in);
		closeQuiet(out);
		return out.toByteArray();
	}

	public static byte[] readToByteArray(File file) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyData(file, out);
		byte[] data = out.toByteArray();
		closeQuiet(out);
		return data;
	}

	public static void readToByteArray(InputStream in, byte[] data) throws IOException {
		int read = 0;
		while (read < data.length) {
			int count = in.read(data);
			read += count;
		}
	}

	public static String readResource(String name) throws IOException {
		URL url = IO.class.getClassLoader().getResource(name);
		if (url == null) throw new IOException("Resource '" + name + "' does not exist.");
		URLConnection connection = url.openConnection();
		return readToString(connection.getInputStream(), UTF_8);
	}

	public static void copyResource(String name, String dst) {
		File dstFile = new File(dst);
		if (dstFile.isDirectory()) {
			dst = dstFile.getPath() + "/" + name;
			dstFile = new File(dst);
		}
		createDirectory(dstFile.getAbsoluteFile().getParentFile());
		FileOutputStream out;
		try {
			out = new FileOutputStream(dst, false);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Directory does not exist: " + dstFile.getParent(), ex);
		}
		URL url = IO.class.getClassLoader().getResource(name);
		if (url == null) throw new RuntimeException("Resource '" + name + "' does not exist.");
		URLConnection connection;
		try {
			connection = url.openConnection();
			copyData(connection.getInputStream(), out, connection.getContentLength());
			out.close();
		} catch (IOException ex) {
			throw new RuntimeException("Writing resource '" + name + "' to '" + dst + "' failed.", ex);
		}
	}

	public static void writeFile(String fileName, String data, String charset) {
		writeFile(new File(fileName), data, charset);
	}

	public static boolean writeFileIfChanged(File file, String data, String charset) {
		if (file.exists() && IO.readFile(file, charset).equals(data)) return false;
		writeFile(file, data, charset);
		return true;
	}

	public static void writeFile(File file, String data, String charset) {
		File parent = file.getParentFile();
		if (parent != null) createDirectory(parent);
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset)));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		out.print(data);
		out.close();
	}

	public static void writeText(OutputStream out, String text, String charset) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new OutputStreamWriter(out, charset));
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
		writer.write(text);
		writer.flush();
	}

	public static void writeFile(String file, Collection<String> lines) throws IOException {
		File f = new File(file);
		createDirectory(f.getParentFile());
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		for (String s : lines) {
			out.write(s);
			out.write("\n");
		}
		out.close();
	}

	public static List<String> readLines(BufferedReader in) {
		List<String> ret = new ArrayList<String>();
		String line;
		try {
			while ((line = in.readLine()) != null)
				ret.add(line);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}

	public static String readToString(InputStream is, String encoding) {
		try {
			return readToString(new InputStreamReader(is, encoding));
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String readToString(Reader reader) {
		StringBuffer sb = new StringBuffer();
		BufferedReader in;
		in = new BufferedReader(reader);
		int ch;
		try {
			while ((ch = in.read()) >= 0) {
				sb.append((char) ch);
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return sb.toString();
	}

	public static byte[] readFileToByteArray(File file) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyData(file, out);
		close(out);
		return out.toByteArray();
	}

	public static String readFile(File file, String encoding) {
		try {
			return readToString(new FileInputStream(file), encoding);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String readFile(String fileName, String encoding) {
		return readFile(new File(fileName), encoding);
	}

	public static void touch(File f) {
		File parent = f.getParentFile();
		if (parent != null && !parent.exists()) {
			if (!parent.mkdirs()) throw new RuntimeException("Creating directory failed: " + parent.getPath());
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(f, true);
			out.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Loads the contents of a properties file (which has to be in a directory that is in the classpath) into
	 * a Properties-object and returns that.
	 * 
	 * @param filename the name of the properties file (e.g. "myname.properties")
	 * @return an object encapsulating the content of the properties file
	 */
	public static Properties loadPropertiesFromClasspath(String filename) {
		Properties p = new Properties();
		InputStream in = IO.class.getResourceAsStream("/" + filename);
		try {
			p.load(in);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} finally {
			close(in);
		}
		return p;
	}

	public static Properties loadProperties(String content) {
		return loadProperties(new StringReader(content));
	}

	public static Properties loadProperties(File f, String encoding) {
		BufferedInputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			Properties p = loadProperties(in, encoding);
			in.close();
			properties.add(p);
			propertiesFiles.add(f);
			return p;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Properties loadProperties(InputStream is, String encoding) {
		InputStreamReader in;
		try {
			in = new InputStreamReader(is, encoding);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
		try {
			return loadProperties(in);
		} finally {
			close(in);
		}
	}

	public static Properties loadProperties(Reader in) {
		Properties p = new Properties();
		try {
			p.load(in);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return p;
	}

	public static Properties loadPropertiesFromUrl(String url, String encoding) {
		try {
			return loadProperties(new URL(url), encoding);
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Malformed URL: " + url, ex);
		}
	}

	public static Properties loadProperties(URL url, String encoding) {
		if (url == null) { return new Properties(); }
		URLConnection connection;
		try {
			connection = url.openConnection();
		} catch (IOException ex) {
			throw new RuntimeException("Connecting URL failed: " + url, ex);
		}
		InputStream in;
		try {
			in = connection.getInputStream();
		} catch (IOException ex) {
			throw new RuntimeException("Loading from URL failed: " + url, ex);
		}
		Properties p = loadProperties(in, encoding);
		closeQuiet(in);
		return p;
	}

	public static void saveLoadedProperties(Properties p, String header) {
		int index = properties.indexOf(p);
		File f = index < 0 ? null : (File) propertiesFiles.get(properties.indexOf(p));
		if (f == null) throw new RuntimeException("Properties were not loaded via IOTools.loadProperties(File)");
		saveProperties(p, header, f);
	}

	public static void saveProperties(Properties p, String header, String filepath) {
		saveProperties(p, header, new File(filepath));
	}

	public static void saveProperties(Properties p, String header, File f) {
		File parent = f.getParentFile();
		if (parent != null) {
			createDirectory(parent);
		}
		try {
			saveProperties(p, header, new FileOutputStream(f));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void saveProperties(Properties p, String header, OutputStream os) {
		BufferedOutputStream out;
		try {
			out = new BufferedOutputStream(os);
			p.store(out, header);
			out.close();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	public static String toString(Properties p) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		saveProperties(p, null, out);
		return new String(out.toByteArray());
	}

	public static String detectEncoding(byte[] data) {
		String encoding = UTF_8;
		BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)), 100);
		String line;
		try {
			line = in.readLine();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (line != null) {
			int idx = line.indexOf("encoding=\"");
			if (idx > 0) {
				idx += 10;
				int endIdx = line.indexOf('"', idx);
				if (endIdx >= idx) {
					encoding = line.substring(idx, endIdx);
				}
			}
		}
		close(in);
		return encoding;
	}

	public static String toString(byte[] data, String encoding) {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data), encoding));
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
		String s = readToString(in);
		try {
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return s;
	}

	public static String downloadUrlToString(String url) {
		return downloadUrlToString(url, null, null);
	}

	public static String downloadUrlToString(String url, String username, String password) {
		BufferedReader in = new BufferedReader(openUrlReader(url, username, password));
		String s = IO.readToString(in);
		close(in);
		return s;
	}

	public static byte[] downloadUrlToByteArray(String url, String username, String password) {
		BufferedInputStream in = new BufferedInputStream(openUrlInputStream(url, username, password));
		byte[] data = readToByteArray(in);
		close(in);
		return data;
	}

	public static byte[] downloadUrl(String url, String username, String password) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedInputStream in = new BufferedInputStream(openUrlInputStream(url, username, password));
		copyData(in, out);
		close(in);
		return out.toByteArray();
	}

	public static URLConnection openUrlConnection(String url, String username, String password) {
		URLConnection connection;
		try {
			connection = new URL(url).openConnection();
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
		if (username != null) {
			connection.setRequestProperty("Authorization",
				"Basic " + Base64.encodeBytes((username + ":" + password).getBytes()));
		}
		if (connection instanceof HttpsURLConnection) {
			HttpsURLConnection sconnection = (HttpsURLConnection) connection;
			sconnection.setHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}

			});
		}
		return connection;
	}

	public static InputStream openUrlInputStream(String url, String username, String password) {
		try {
			return openUrlConnection(url, username, password).getInputStream();
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Reader openUrlReader(String url, String username, String password) {
		URLConnection connection = openUrlConnection(url, username, password);
		String encoding = connection.getContentEncoding();
		if (encoding == null) encoding = UTF_8;
		try {
			return new InputStreamReader(connection.getInputStream(), encoding);
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void downloadUrlToFile(String url, String file) {
		downloadUrlToFile(url, file, null);
	}

	public static void downloadUrlToFile(String url, String file, CopyObserver observer) {
		downloadUrlToFile(url, file, null, null, observer);
	}

	public static void downloadUrlToFile(String url, String file, String username, String password,
			CopyObserver observer) {
		InputStream in;
		try {
			URLConnection connection = new URL(url).openConnection();
			if (username != null) {
				connection.setRequestProperty("Authorization",
					"Basic " + Base64.encodeBytes((username + ":" + password).getBytes()));
			}
			connection.connect();
			int length = connection.getContentLength();
			if (observer != null && length > -1) observer.totalSizeDetermined(length);
			in = connection.getInputStream();
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}

		try {
			copyDataToFile(in, new File(file), observer);
		} finally {
			close(in);
		}
	}

	public static class StringInputStream extends InputStream {

		String s;
		int len;
		int index;

		public StringInputStream(String s) {
			this.s = s;
			len = s.length();
		}

		@Override
		public int read() {
			return index >= len ? -1 : s.charAt(index++);
		}

	}

	public static class FileList extends ArrayList {

		public File getFile(int index) {
			return (File) get(index);
		}

	}

	private static File workDir;

	public static File getWorkDir() {
		if (workDir == null) workDir = new File("dummy").getAbsoluteFile().getParentFile();
		return workDir;
	}

	private static File tempDir;

	public static File getTempDir() {
		if (tempDir == null) {
			try {
				tempDir = File.createTempFile("dummy", ".tmp").getParentFile();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		return tempDir;
	}

	public static boolean isDirWritable(String dir) {
		File testfile = new File(dir + "/.writetest.deleteme");
		try {
			touch(testfile);
		} catch (Throwable ex) {
			return false;
		} finally {
			testfile.delete();
		}
		return true;
	}

	public static boolean isFileWritable(File file) {
		if (file.exists()) return file.canWrite();
		try {
			touch(file);
		} catch (Throwable ex) {
			return false;
		} finally {
			file.delete();
		}
		return true;
	}

}
