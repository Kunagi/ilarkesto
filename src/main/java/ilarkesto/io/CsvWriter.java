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

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CsvWriter {

	private PrintWriter out;

	private List<String> headers;

	public CsvWriter(PrintWriter out) {
		this.out = out;
	}

	public CsvWriter(Writer out) {
		this(new PrintWriter(out));
	}

	public void writeRecord(Map<String, Object> fields) {
		if (headers == null)
			throw new IllegalStateException("headers property must be set when to write record values from a map");
		for (String header : headers)
			writeField(fields.get(header));
		closeRecord();
	}

	public void writeHeaders(List<String> headers) {
		setHeaders(headers);
		writeRecord(headers);
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public void writeRecord(Collection<String> values) {
		for (String value : values)
			writeField(value);
		closeRecord();
	}

	private boolean nl = true;

	public void writeField(Object value) {
		if (!nl) {
			out.print(separator);
		}
		nl = false;
		if (value == null) {
			// value = "";
			return;
		}
		out.print('"');
		out.print(escape(value.toString()));
		out.print('"');
	}

	public void closeRecord() {
		out.print("\r\n");
		out.flush();
		nl = true;
	}

	public static String escape(String value) {
		value = Str.escapeEscapeSequences(value);
		return value;
	}

	public void close() {
		out.close();
	}

	// --- dependencies ---

	private char separator = ',';

	public void setSeparator(char separator) {
		this.separator = separator;
	}

}
