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

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public final class CsvParser {

	private boolean isSeperator(int c) {
		return c == separator;
	}

	private boolean isEOL(int c) {
		return c == 13 || c == 10 || c == -1;
	}

	public List<String> nextRecord() {
		List<String> result = new ArrayList<String>();
		int c = readNextChar();
		if (c == -1) return null;
		resetLastChar();
		while (true) {
			if (quoted) {
				c = readNextChar();
				if (isSeperator(c)) {
					result.add(null);
					continue;
				}
				resetLastChar();
				try {
					parseOpeningQuote();
				} catch (EOFException ex) {
					result.add(null);
					break;
				} catch (EOLException ex) {
					result.add(null);
					parseNl();
					break;
				}
			}
			String field = parseField();
			result.add(field);
			try {
				parseSeperator();
			} catch (EOLException ex) {
				parseNl();
				break;
			} catch (EOFException ex) {
				break;
			}
		}
		return result;
	}

	private String parseField() {
		StringBuilder sb = new StringBuilder();
		int c;
		for (int i = 0; true; i++) {
			c = readNextChar();
			if (quoted && c == '"') return sb.toString();
			if (isEOL(c)) {
				if (quoted) {
					if (c == -1) throw new ParseException("Unexpected OEF in field");
				} else {
					resetLastChar();
					return sb.toString();
				}
			}
			if (!quoted && isSeperator(c)) {
				resetLastChar();
				return sb.toString();
			}
			if (c == '\\') {
				appendControlSequence(sb);
			} else sb.append((char) c);
		}
	}

	private void appendControlSequence(StringBuilder sb) {
		int seq = readNextChar();
		if (seq == '\\')
			sb.append("\\");
		else if (seq == 'b')
			sb.append("\b");
		else if (seq == 't')
			sb.append("\t");
		else if (seq == 'n')
			sb.append("\n");
		else if (seq == 'f')
			sb.append("\f");
		else if (seq == 'r')
			sb.append("\r");
		else if (seq == '\"')
			sb.append("\"");
		else if (seq == '\'')
			sb.append("\'");
		else throw new ParseException("Unsupported control sequence '" + (char) seq + "' (" + seq + ")");
	}

	private void parseSeperator() throws EOFException, EOLException {
		int c = readNextChar();
		if (c == -1) throw new EOFException();
		if (isEOL(c)) {
			resetLastChar();
			throw new EOLException();
		}
		if (isSeperator(c)) return;
		throw new ParseException("Field seperator expected, but is: '" + (char) c + "' (" + c + ")");
	}

	private void parseOpeningQuote() throws EOFException, EOLException {
		int c = readNextChar();
		if (c == -1) throw new EOFException();
		if (isEOL(c)) {
			resetLastChar();
			throw new EOLException();
		}
		if (c == '"') return;
		throw new ParseException("Quote '\"' expected, but is: '" + (char) c + "' (" + c + ")");
	}

	private void parseNl() {
		while (true) {
			int c = readNextChar();
			if (c == -1) return;
			if (!isEOL(c)) {
				resetLastChar();
				return;
			}
		}
	}

	// public String nextField() {
	// return nextField(false);
	// }
	//    
	// public String nextField(boolean forceField) {
	// StringBuilder sb = new StringBuilder();
	// boolean enclosed = false; // enclosed in quotes
	// int c = readNextChar();
	// if (isSeparator(c) && forceField) {
	// resetLastChar();
	// return "";
	// }
	// if (c == 13 || c == 10 || c == -1) {
	// if (forceField) {
	// resetLastChar();
	// return "";
	// }
	// c = readNextChar();
	// if (c != 13 && c != 10) resetLastChar();
	// return null;
	// }
	// if (isSeparator(c)) {
	// return nextField(true);
	// }
	// if (c == '"') {
	// enclosed = true;
	// c = readNextChar();
	// }
	// while (true) {
	// if (c == -1) break;
	// if (c == 13 || c == 10) {
	// resetLastChar();
	// break;
	// }
	// if (isSeparator(c) && !enclosed) {
	// resetLastChar();
	// break;
	// }
	// if (c == '"' && enclosed) break;
	// sb.append((char) c);
	// c = readNextChar();
	// }
	// if (enclosed) {
	// c = readNextChar();
	// if (c == 13 || c == 10) {
	// resetLastChar();
	// } else if (!isSeparator(c))
	// throw new ParseException("Invalid character after field end: #" + c + " '" + ((char) c) + "'", null);
	// }
	// return sb.toString();
	// }

	private void resetLastChar() {
		if (in == null) return;
		try {
			in.reset();
		} catch (IOException ex) {
			throw new ParseException("Reset failed", ex);
		}
	}

	// private char lastReadChar;

	private int readNextChar() {
		if (in == null) return -1;
		try {
			in.mark(1);
			int c = in.read();
			// lastReadChar = (char) c;
			if (c == -1) {
				in.close();
				in = null;
			}
			return c;
		} catch (IOException ex) {
			throw new ParseException("Reading failed", ex);
		}
	}

	public void skipLine() {
		skipLines(1);
	}

	public void skipLines(int count) {
		for (int i = 0; i < count; i++) {
			try {
				in.readLine();
			} catch (IOException ex) {
				throw new ParseException("Skipping Line failed", ex);
			}
		}
	}

	class EOLException extends Exception {

	}

	public class ParseException extends RuntimeException {

		public ParseException(String message, Throwable cause) {
			super(message, cause);
		}

		public ParseException(String message) {
			super(message);
		}
	}

	// --- dependencies ---

	private boolean quoted;
	private BufferedReader in;

	public CsvParser(Reader in, boolean quoted) {
		this.in = new BufferedReader(in);
		this.quoted = quoted;
	}

	public CsvParser(File file, String encoding, boolean quoted) throws FileNotFoundException,
			UnsupportedEncodingException {
		this(new InputStreamReader(new FileInputStream(file), encoding), quoted);
	}

	private char separator = ',';

	public void setSeparator(char separator) {
		this.separator = separator;
	}

}
