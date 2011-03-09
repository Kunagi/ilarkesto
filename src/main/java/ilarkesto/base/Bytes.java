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

/**
 * Data type for storing amount of bytes.
 */
public class Bytes implements Comparable<Bytes> {

	private long bytes;

	public Bytes(long bytes) {
		this.bytes = bytes;
	}

	public long toLong() {
		return bytes;
	}

	@Override
	public String toString() {
		return String.valueOf(bytes) + " Bytes";
	}

	public String toRoundedString() {
		if (bytes > 10000000000l) return String.valueOf(Math.round(bytes / 1000000000f)) + " GB";
		if (bytes > 10000000) return String.valueOf(Math.round(bytes / 1000000f)) + " MB";
		if (bytes > 10000) return String.valueOf(Math.round(bytes / 1000f)) + " KB";
		return toString();
	}

	public static Bytes kilo(long kilobytes) {
		return new Bytes(kilobytes * 1000);
	}

	public static Bytes mega(long megabytes) {
		return new Bytes(megabytes * 1000000);
	}

	public static Bytes giga(long gigabytes) {
		return new Bytes(gigabytes * 1000000000);
	}

	@Override
	public int compareTo(Bytes o) {
		if (bytes == o.bytes) return 0;
		return bytes > o.bytes ? 1 : -1;
	}
}
