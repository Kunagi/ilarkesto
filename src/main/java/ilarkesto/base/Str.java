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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Utilitiy methods for dealing with text/strings. Cutting, comparing, parsing, modifying.
 */
public class Str extends ilarkesto.core.base.Str {

	public static void main(String[] args) {
		System.out.println(generateRandomParagraph());
		System.exit(0);
	}

	private static final char[] UNICODE_CHARS = new char[] { ue, UE, oe, OE, ae, AE, sz, EUR };

	private static final String[][] ESCAPE_SEQUENCES = { { "\\", "\\\\" }, { "\b", "\\b" }, { "\t", "\\t" },
			{ "\n", "\\n" }, { "\f", "\\f" }, { "\r", "\\r" }, { "\"", "\\\"" }, { "\'", "\\\'" } };

	private static long lastId;

	private static final Object UIDLOCK = new Object();

	public static String multiply(String s, int factor) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < factor; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String generateRandomParagraphs(int count, String prefix, String suffix, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			if (separator != null && i > 0) sb.append(separator);
			if (prefix != null) sb.append(prefix);
			sb.append(generateRandomParagraph());
			if (suffix != null) sb.append(suffix);
		}
		return sb.toString();
	}

	public static String generateRandomParagraph() {
		return generateRandomParagraph(2, 10, 4, 12, 2, 12);
	}

	public static String generateRandomParagraph(int minSentences, int maxSentences, int minWords, int maxWords,
			int minWordLenght, int maxWordLenght) {
		int sentences = Utl.randomInt(minSentences, maxSentences);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sentences; i++) {
			String sentence = generateRandomSentence(minWords, maxWords, minWordLenght, maxWordLenght);
			if (i != 0) sb.append(" ");
			sb.append(sentence).append(".");
		}
		return sb.toString();
	}

	public static String generateRandomSentence() {
		return generateRandomSentence(4, 12);
	}

	public static String generateRandomSentence(int minWords, int maxWords) {
		return generateRandomSentence(minWords, maxWords, 2, 12);
	}

	public static String generateRandomSentence(int minWords, int maxWords, int minWordLenght, int maxWordLenght) {
		int words = Utl.randomInt(minWords, maxWords);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words; i++) {
			boolean uppercase = i == 0 || Utl.randomInt(0, 9) == 0;
			String word = generateRandomWord(minWordLenght, maxWordLenght, uppercase);
			if (i != 0) sb.append(" ");
			sb.append(word);
		}
		return sb.toString();
	}

	public static String generateRandomWord(int minLength, int maxLength, boolean uppercase) {
		String vovels = "aeiouy";
		String consonants = "bcdfghjklmnpqrstvwxz";
		int length = Utl.randomInt(minLength, maxLength);
		String word = generateRandomWord(vovels, consonants, length);
		return uppercase ? uppercaseFirstLetter(word) : word;
	}

	public static String generatePassword(int length) {
		return generateRandomWord("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!$%&=", length);
	}

	public static String generateRandomWord(String availableChars, int minLength, int maxLength) {
		int length = Utl.randomInt(minLength, maxLength);
		return generateRandomWord(availableChars, length);
	}

	public static String generateRandomWord(String charSet1, String charSet2, int length) {
		StringBuilder password = new StringBuilder();
		String charSet = charSet1;
		for (int i = 0; i < length; i++) {
			if (Utl.randomInt(0, 8) != 0) {
				if (charSet == charSet1) {
					charSet = charSet2;
				} else {
					charSet = charSet1;
				}
			}
			password.append(Utl.randomChar(charSet));
		}
		return password.toString();
	}

	public static String generateRandomWord(String availableChars, int length) {
		StringBuilder password = new StringBuilder();
		for (int i = 0; i < length; i++) {
			password.append(Utl.randomChar(availableChars));
		}
		return password.toString();
	}

	public static boolean containsDigit(String s) {
		if (s == null) return false;
		for (int i = 0; i < s.length(); i++) {
			if (Character.isDigit(s.charAt(i))) return true;
		}
		return false;
	}

	public static boolean containsLetter(String s) {
		if (s == null) return false;
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i))) return true;
		}
		return false;
	}

	public static boolean containsNonLetterOrDigit(String s) {
		if (s == null) return false;
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isLetterOrDigit(s.charAt(i))) return true;
		}
		return false;
	}

	public static boolean endsWith(String s, String... suffixes) {
		for (String suffix : suffixes) {
			if (s.endsWith(suffix)) return true;
		}
		return false;
	}

	public static String toHexString(byte[] bytes) {
		if (bytes == null) return null;
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(toHexString(b));
		}
		return sb.toString();
	}

	public static String toHexString(byte b) {
		int i = b;
		if (i < 0) i = 256 + i;
		String s = Integer.toHexString(i).toUpperCase();
		if (s.length() == 1) s = '0' + s;
		return s;
	}

	public static String toBinaryString(byte b) {
		int i = b;
		if (i < 0) i = 256 + i;
		String s = Integer.toBinaryString(i).toUpperCase();
		if (s.length() == 1) s = '0' + s;
		return s;
	}

	public static String formatWithThousandsSeparator(long value, String separator) {
		return formatWithThousandsSeparator(String.valueOf(value), separator);
	}

	public static String formatWithThousandsSeparator(String s, String separator) {
		if (s == null) return null;
		if (separator == null || s.length() <= 3) return s;
		boolean negative = false;
		if (s.startsWith("-")) {
			negative = true;
			s = s.substring(1);
		}
		if (s.length() > 3) s = s.substring(0, s.length() - 3) + separator + s.substring(s.length() - 3);
		if (s.length() > 7) s = s.substring(0, s.length() - 7) + separator + s.substring(s.length() - 7);
		if (s.length() > 11) s = s.substring(0, s.length() - 11) + separator + s.substring(s.length() - 11);
		if (negative) s = '-' + s;
		return s;
	}

	public static String substringTo(String s, String to) {
		return substringTo(s, to, 0);
	}

	public static String substringTo(String s, String to, int fromIndex) {
		if (s == null) return null;
		if (to == null) return s;
		int idx = s.indexOf(to, fromIndex);
		if (idx < 0) return s;
		return s.substring(fromIndex, idx);
	}

	public static String removeUnreadableChars(String s) {
		if (s == null) return null;
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (isReadable(c)) sb.append(c);
		}
		return sb.toString();
	}

	public static boolean isReadable(char c) {
		if (Character.isLetterOrDigit(c)) return true;
		if (c == ' ' || c == '\n' || c == '!' || c == '"' || c == '�' || c == '$' || c == '%' || c == '&' || c == '/'
				|| c == '(' || c == ')' || c == '=' || c == '?' || c == '{' || c == '}' || c == '[' || c == ']'
				|| c == '\\' || c == '*' || c == '+' || c == '~' || c == '#' || c == '\'' || c == '-' || c == '_'
				|| c == '.' || c == ':' || c == ',' || c == ';' || c == '�' || c == '<' || c == '>' || c == '@'
				|| c == EUR || c == '^' || c == '�' || c == '�' || c == '�' || c == '|') return true;
		return false;
	}

	public static String replaceUnicodeCharsWithJavaNotation(String s) {
		s = s.replace(String.valueOf(ue), "\\u00FC");
		s = s.replace(String.valueOf(UE), "\\u00DC");
		s = s.replace(String.valueOf(oe), "\\u00F6");
		s = s.replace(String.valueOf(OE), "\\u00D6");
		s = s.replace(String.valueOf(ae), "\\u00E4");
		s = s.replace(String.valueOf(AE), "\\u00C4");
		s = s.replace(String.valueOf(sz), "\\u00DF");
		s = s.replace(String.valueOf(EUR), "\\u0080");
		return s;
	}

	public static boolean containsUnicodeChar(String s) {
		return containsChar(s, UNICODE_CHARS);
	}

	public static boolean containsChar(String s, char... chars) {
		for (char c : chars) {
			if (s.indexOf(c) >= 0) return true;
		}
		return false;
	}

	public static String activateLinksInHtml(String s) {
		return activateLinksInHtml(s, 640);
	}

	public static String activateLinksInHtml(String s, int maxWidth) {
		if (s == null) return null;
		int fromIndex = 0;
		StringBuffer result = null;
		int idx = -1;
		while ((idx = firstIndexOf(s, fromIndex, "http://", "https://", "ftp://", "www.")) >= 0) {
			char pre = idx == 0 ? ' ' : s.charAt(idx - 1);
			if (pre == ' ' || pre == '\n' || pre == '>') {
				int endIdx = firstIndexOf(s, idx, " ", "<", "\n");
				if (endIdx <= 0 || !s.substring(endIdx).startsWith("</a>")) {
					if (endIdx < 0) endIdx = s.length();
					// activate
					String url = s.substring(idx, endIdx);
					if (result == null) result = new StringBuffer();
					result.append(s.substring(fromIndex, idx));
					String urlLower = url.toLowerCase();
					result.append("<a href=\"");
					result.append(url.startsWith("www.") ? "http://" + url : url);
					result.append("\" target=\"_blank\">");

					if (urlLower.startsWith("http://www.youtube.com/watch?v=")) {
						result.append(youtube(parseYoutubeVideoId(url), maxWidth));
					} else if (urlLower.startsWith("http://twitpic.com/")) {
						String id = removePrefix(url, "http://twitpic.com/");
						result.append("<img src=\"");
						result.append("http://twitpic.com/show/full/").append(id);
						result.append("\" style=\"max-width: " + maxWidth + "px; max-height: " + maxWidth
								+ "px;\" alt=\"twitpic\">");
					} else if (urlLower.endsWith(".jpg") || urlLower.endsWith(".gif") || urlLower.endsWith(".png")
							|| urlLower.endsWith(".jpeg") || url.contains(".ggpht.com/")) {
						result.append("<img src=\"");
						result.append(url.startsWith("www.") ? "http://" + url : url);
						result.append("\" style=\"max-width: " + maxWidth + "px; max-height: " + maxWidth
								+ "px;\" alt=\"img\">");
					} else {
						String label = url;
						if (url.startsWith("http://")) url = url.substring(7);
						if (url.startsWith("https://")) url = url.substring(8);
						if (url.startsWith("www.")) url = url.substring(4);
						label = cutRight(url, 30, "...");
						result.append(label);
					}

					result.append("</a>");
					fromIndex = endIdx;
					continue;
				}
			}
			// nothing to activate
			if (result == null) result = new StringBuffer();
			result.append(s.substring(fromIndex, idx));
			result.append(s.charAt(idx));
			fromIndex = idx + 1;
		}
		if (result == null) return s;
		result.append(s.substring(fromIndex));
		return result.toString();
	}

	private static String parseYoutubeVideoId(String url) {
		String prefix = "http://www.youtube.com/watch?v=";
		String s = removePrefix(url, prefix);
		int endIdx = s.indexOf("&");
		if (endIdx > 0) {
			s = s.substring(0, endIdx);
		}
		return s;
	}

	public static String youtube(String vId) {
		return youtube(vId, 640, 385);
	}

	public static String youtube(String vId, int width) {
		int height = (int) (width / 1.6623f);
		return youtube(vId, width, height);
	}

	public static String youtube(String vId, int width, int height) {
		StringBuilder sb = new StringBuilder();
		sb.append("<object width=\"").append(width).append("\" height=\"").append(height).append("\">");
		sb.append("<param name=\"movie\" value=\"http://www.youtube.com/v/").append(vId)
				.append("&hl=en_US&fs=1&\"></param>");
		sb.append("<param name=\"allowFullScreen\" value=\"true\"></param>");
		sb.append("<param name=\"allowscriptaccess\" value=\"always\"></param>");
		sb.append("<embed src=\"http://www.youtube.com/v/").append(vId)
				.append("&hl=en_US&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\"")
				.append(" allowfullscreen=\"true\" width=\"").append(width).append("\" height=\"").append(height)
				.append("\"></embed></object>");
		return sb.toString();
	}

	public static int firstIndexOf(String s, int fromIndex, String... stringsToFind) {
		int idx = Integer.MAX_VALUE;
		for (String find : stringsToFind) {
			int i = s.indexOf(find, fromIndex);
			if (i >= 0) idx = Math.min(idx, i);
		}
		idx = idx == Integer.MAX_VALUE ? -1 : idx;
		return idx;
	}

	public static boolean isUpperCase(String s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			if (!Character.isUpperCase(s.charAt(i))) return false;
		}
		return true;
	}

	public static String getFirstTokenAfter(String stringToTokenize, String delimiter, String s) {
		return getTokenAfter(stringToTokenize, delimiter, s, 0);
	}

	public static String getTokenAfter(String stringToTokenize, String delimiter, String s, int index) {
		return getStringAfter(tokenize(stringToTokenize, delimiter), s, index);
	}

	public static String getFirstStringAfter(String[] strings, String s) {
		return getStringAfter(strings, s, 0);
	}

	/**
	 * Determine the string, which is at a specified position after a specified string.
	 */
	public static String getStringAfter(String[] strings, String s, int index) {
		int i = indexOfStringInArray(s, strings);
		if (i < 0) return null;
		index += i + 1;
		if (index >= s.length()) return null;
		return strings[index];
	}

	/**
	 * Determine the index of a string inside of an string array.
	 */
	public static int indexOfStringInArray(String s, String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			if (equals(strings[i], s)) return i;
		}
		return -1;
	}

	public static boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null && s2 != null) return false;
		if (s1 != null && s2 == null) return false;
		return s1.equals(s2);
	}

	public static Collection<String> parseCommaSeparatedString(String s) {
		Collection<String> result = new ArrayList<String>();
		if (s == null) return result;
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		while (tokenizer.hasMoreTokens())
			result.add(tokenizer.nextToken().trim());
		return result;
	}

	public static String decodeQuotedPrintable(String s) {
		if (s == null) return null;

		int start = s.indexOf("=?");
		if (start < 0) return s;

		String prefix = s.substring(0, start);

		start = s.indexOf("?Q?", start);
		if (start < 0) start = s.indexOf("?q?", start);
		if (start < 0) start = s.indexOf("?B?", start);
		if (start < 0) start = s.indexOf("?b?", start);
		if (start < 0) return s;
		start += 3;

		int end = s.indexOf("?=", start);
		if (end < 0) return s;

		String suffix = s.substring(end + 2);
		s = s.substring(start, end);

		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == '=') {
				if (i + 1 == len) {
					sb.append('=');
					break;
				}
				char next = s.charAt(i + 1);
				if (next == 13 || next == 10) {
					i++;
					next = s.charAt(i + 1);
					if (next == 13 || next == 10) {
						i++;
					}
					continue;
				}
				if (next == '=') {
					sb.append('=');
					i++;
					continue;
				}
				char afterNext = s.charAt(i + 2);
				int ch = Integer.valueOf(String.valueOf(next) + afterNext, 16);
				sb.append((char) ch);
				i += 2;
			} else if (c == '_') {
				sb.append(' ');
			} else {
				sb.append(c);
			}
		}
		sb.append(suffix);
		return sb.toString();
	}

	public static boolean isFilenameCompatible(String s) {
		for (int i = 0, n = s.length(); i < n; i++) {
			char c = s.charAt(i);
			if (!(Character.isDigit(c) || Character.isLetter(c) || c == '_' || c == '-' || c == '.')) return false;
		}
		return true;
	}

	public static String uppercaseFirstLetter(String s) {
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toUpperCase(s.charAt(0)));
		sb.append(s.substring(1));
		return sb.toString();
	}

	public static String lowercaseFirstLetter(String s) {
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toLowerCase(s.charAt(0)));
		sb.append(s.substring(1));
		return sb.toString();
	}

	public static String concatWithUppercaseFirstLetter(String[] sa, boolean ignoreFirst) {
		StringBuilder sb = new StringBuilder();
		if (ignoreFirst) {
			sb.append(sa[0]);
		}
		for (int i = ignoreFirst ? 1 : 0; i < sa.length; i++) {
			sb.append(Character.toUpperCase(sa[i].charAt(0)));
			sb.append(sa[i].substring(1));
		}
		return sb.toString();
	}

	public static String formatUrl(String url) {
		if (url == null) return null;
		url = url.trim();
		if (url.startsWith("www.")) url = "http://" + url;
		return url;
	}

	public static String formatPostalcode(String s) {
		return formatNumber(s);
	}

	public static String formatNumber(String s) {
		if (s == null) return null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c)) sb.append(c);
		}
		return sb.toString();
	}

	public static String formatTelephone(String telnum) {
		if (telnum == null) return null;
		StringBuilder sb = new StringBuilder();
		boolean lastWasSpace = false;
		for (int i = 0; i < telnum.length(); i++) {
			char c = telnum.charAt(i);
			if (Character.isDigit(c) || c == '+') {
				sb.append(c);
				lastWasSpace = false;
			} else {
				if (!lastWasSpace) {
					sb.append(' ');
					lastWasSpace = true;
				}
			}
		}
		telnum = sb.toString().trim();
		if (telnum.startsWith("0") && telnum.length() > 1) telnum = "+49 " + telnum.substring(1);
		return telnum;
	}

	public static String[] tokenizeWithLongDelimiter(String s, String delimiter) {
		List<String> sl = new ArrayList<String>();
		int idx = s.indexOf(delimiter);
		int offset = 0;
		while ((idx = s.indexOf(delimiter, offset)) >= 0) {
			sl.add(s.substring(offset, idx));
			offset = idx + delimiter.length();
		}
		sl.add(s.substring(offset));
		return toStringArray(sl);
	}

	public static char getFirstNonexistingChar(String s) {
		return getFirstNonexistingChar(s, (char) 0);
	}

	public static char getFirstNonexistingChar(String s, char offset) {
		for (char c = offset; c < Character.MAX_VALUE; c++) {
			if (s.indexOf(c) < 0) return c;
		}
		return Character.MAX_VALUE;
	}

	public static String encodeUrlParameter(String s) {
		if (s == null) return "";
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '$':
					sb.append("%24");
					break;
				case '&':
					sb.append("%26");
					break;
				case '+':
					sb.append("%2B");
					break;
				case ',':
					sb.append("%2C");
					break;
				case '/':
					sb.append("%2F");
					break;
				case ':':
					sb.append("%3A");
					break;
				case ';':
					sb.append("%3B");
					break;
				case '=':
					sb.append("%3D");
					break;
				case '?':
					sb.append("%3F");
					break;
				case '@':
					sb.append("%40");
					break;
				case ' ':
					sb.append("%20");
					break;
				case '"':
					sb.append("%22");
					break;
				case '<':
					sb.append("%3C");
					break;
				case '>':
					sb.append("%3E");
					break;
				case '#':
					sb.append("%23");
					break;
				case '%':
					sb.append("%25");
					break;
				case '{':
					sb.append("7B%");
					break;
				case '}':
					sb.append("7D%");
					break;
				case '|':
					sb.append("%7C");
					break;
				case '\\':
					sb.append("%5C");
					break;
				case '^':
					sb.append("%5E");
					break;
				case '~':
					sb.append("%7E");
					break;
				case '[':
					sb.append("%5B");
					break;
				case ']':
					sb.append("%5D");
					break;
				case '`':
					sb.append("%60");
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String constructUrl(String base, Map parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append(base);
		if (parameters != null && parameters.size() > 0) {
			boolean first = true;
			Set<Map.Entry> entries = parameters.entrySet();
			for (Map.Entry entry : entries) {
				if (first) {
					sb.append('?');
					first = false;
				} else {
					sb.append("&");
				}

				Object key = entry.getKey();
				Object value = entry.getValue();
				sb.append(key);
				sb.append("=");
				if (value != null) sb.append(encodeUrlParameter(value.toString()));
			}
		}
		return sb.toString();
	}

	public static String toFileCompatibleString(String s) {
		s = s.replace('/', '-');
		s = s.replace('\\', '-');
		s = s.replace(':', '_');
		s = s.replace(';', '_');
		s = s.replace('&', '@');
		s = s.replace('?', '@');
		s = s.replace('=', '_');
		s = s.replace(String.valueOf(ae), "ae");
		s = s.replace(String.valueOf(AE), "Ae");
		s = s.replace(String.valueOf(ue), "ue");
		s = s.replace(String.valueOf(UE), "Ue");
		s = s.replace(String.valueOf(oe), "oe");
		s = s.replace(String.valueOf(OE), "Oe");
		s = s.replace(String.valueOf(sz), "ss");
		s = s.replace(String.valueOf(EUR), "EUR");
		return s;
	}

	public static String getPrimitiveTypeName(String className) {
		if (className.startsWith("java.lang.")) className = className.substring(10);
		if (className.equals("Long")) className = "long";
		if (className.equals("Integer")) className = "int";
		if (className.equals("Double")) className = "double";
		if (className.equals("Boolean")) className = "boolean";
		if (className.equals("Byte")) className = "byte";
		return className;
	}

	public static List<String> listRelativeFiles(String root, boolean recurse, boolean replaceToUnixSlash,
			boolean ignoreDirs, FileFilter filter) {
		List<String> sl = new ArrayList<String>();
		listRelateveFiles(sl, root, "", recurse, replaceToUnixSlash, ignoreDirs, true, filter);
		return sl;
	}

	public static void listRelateveFiles(List<String> container, String root, String prefix, boolean recurse,
			boolean replaceToUnixSlash, boolean ignoreDirs, boolean allowDuplicates, FileFilter filter) {

		if (replaceToUnixSlash) root = root.replace("\\", "/");

		File rootFile = new File(root);
		File[] files = rootFile.listFiles();
		if (files == null || files.length == 0) return;

		for (int i = 0; i < files.length; i++) {
			if (filter != null && !filter.accept(files[i])) continue;
			boolean dir = files[i].isDirectory();
			if (!dir || !ignoreDirs) {
				String s = prefix + files[i].getName();
				if (allowDuplicates || !container.contains(s)) {
					container.add(s);
				}
			}
			if (dir && recurse) {
				listRelateveFiles(container, root + "/" + files[i].getName(), prefix + files[i].getName() + "/",
					recurse, replaceToUnixSlash, ignoreDirs, allowDuplicates, filter);
			}
		}
	}

	public static boolean isLetterOrDigit(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (!Character.isLetterOrDigit(s.charAt(i))) return false;
		}
		return true;
	}

	public static boolean isLetter(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (!Character.isLetter(s.charAt(i))) return false;
		}
		return true;
	}

	public static boolean isMatchingKey(String s, String key) {
		if (s != null) {
			return s.toLowerCase().indexOf(key) >= 0;
		} else {
			return false;
		}
	}

	public static boolean isDigit(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (!Character.isDigit(s.charAt(i))) return false;
		}
		return true;
	}

	public static String getEnclosed(String s, String opener, String closer) {
		return (new EncloseParser(s, opener, closer)).getEnclosed();
	}

	public static class EncloseParser {

		private String opener;

		private String closer;

		private String s;

		private String prefix;

		private String enclosed;

		private String postfix;

		public EncloseParser(String s, String opener, String closer) {
			this.s = s;
			this.opener = opener;
			this.closer = closer;

			parse();
		}

		private void parse() {
			int idx = s.indexOf(opener);
			if (idx < 0) return;
			prefix = s.substring(0, idx);
			idx += opener.length();

			int stack = 1;
			StringBuilder sb = new StringBuilder();
			int startIdx = idx;
			String[] openClose = new String[] { opener, closer };
			int maxLen = Math.min(opener.length(), closer.length());

			while (stack > 0) {
				if (startIdx == s.length() - 1) return;
				idx = indexOf(s, openClose, startIdx);
				if (idx < 0) return;
				String rest = s.substring(idx, idx + maxLen);
				if (rest.startsWith(opener)) {
					stack++;
					sb.append(s.substring(startIdx, (startIdx = idx + opener.length())));
				} else {
					stack--;
					sb.append(s.substring(startIdx, idx));
					if (stack != 0) sb.append(closer);
					startIdx = idx + closer.length();
				}
			}
			postfix = s.substring(startIdx);

			enclosed = sb.toString();

		}

		public String getEnclosed() {
			return enclosed;
		}

		public String getPostfix() {
			return postfix;
		}

		public String getPrefix() {
			return prefix;
		}
	}

	public static String replaceForSql(String s) {
		s = s.replace("\\", "\\\\");
		s = s.replace("\"", "\\\"");
		return s;
	}

	public static String html2text(String s) {
		if (s == null) return null;
		StringBuilder sb = new StringBuilder();
		StringBuilder tag = null;

		boolean inside = false;
		int len = s.length();
		String href = null;
		char cPrev = (char) -1;
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (inside) {
				// inside html tag declaration
				if (c == '>') {
					inside = false;
					String t = tag.toString().toLowerCase();
					if (isTag(t, "br") || isTag(t, "ul") || isTag(t, "/ul") || isTag(t, "div")) {
						sb.append("\n");
					} else if (isTag(t, "p") || isTag(t, "h1") || isTag(t, "h2") || isTag(t, "h3") || isTag(t, "h4")
							|| isTag(t, "h5") || isTag(t, "h6")) {
						sb.append("\n\n");
					} else if (isTag(t, "li")) {
						sb.append("\n- ");
					} else if (isTag(t, "hr")) {
						sb.append("\n--------------------\n");
					} else if (isTag(t, "/a")) {
						if (href != null) {
							int hrefLen = href.length();
							if (i > hrefLen * 2 && s.substring(i - 3 - hrefLen, i - 3).equalsIgnoreCase(href)) {
								// skip
							} else {
								sb.append(" [ ").append(href).append(" ]");
							}
							href = null;
						}
					} else if (isTag(t, "a")) {
						int idx = t.indexOf("href=\"");
						if (idx >= 0) {
							idx += 6;
							int endidx = t.indexOf("\"", idx);
							if (endidx > idx) {
								href = t.substring(idx, endidx);
							}
						} else {
							idx = t.indexOf("href='");
							if (idx >= 0) {
								idx += 6;
								int endidx = t.indexOf("'", idx);
								if (endidx > idx) {
									href = t.substring(idx, endidx);
								}
							}
						}
					}
					tag = null;
				} else {
					tag.append(c);
				}
				cPrev = c;
				continue;
			} else {
				// outside html tag
				if (c == '<') {
					inside = true;
					tag = new StringBuilder();
					cPrev = c;
					continue;
				}
				if (c == '\n' || c == '\r' || (Character.isWhitespace(c) && Character.isWhitespace(cPrev))) {
					// skip
				} else {
					sb.append(c);
				}
			}
			cPrev = c;
		}

		s = sb.toString();
		s = s.replace("&nbsp;", " ");
		s = s.replace("&auml;", String.valueOf(ae));
		s = s.replace("&uuml;", String.valueOf(ue));
		s = s.replace("&ouml;", String.valueOf(oe));
		s = s.replace("&Auml;", String.valueOf(AE));
		s = s.replace("&Uuml;", String.valueOf(UE));
		s = s.replace("&Ouml;", String.valueOf(OE));
		s = s.replace("&szlig;", String.valueOf(sz));
		s = s.replace("&euro;", String.valueOf(EUR));
		s = s.replace("&amp;", "&");
		s = s.replace("&quot;", "\"");
		s = s.replace("&lt;", "<");
		s = s.replace("&gt;", ">");
		s = s.replace("<br>", "\n");

		s = s.replace(" \n", "\n");
		s = s.replace("  \n", "\n");
		s = s.replace("   \n", "\n");
		s = s.replace("\n\n\n\n\n", "\n\n");
		s = s.replace("\n\n\n\n", "\n\n");
		s = s.replace("\n\n\n", "\n\n");

		return s.trim();
	}

	private static boolean isTag(String tag, String name) {
		return tag.equals(name) || tag.startsWith(name + " ");
	}

	public static String removeHtmlTags(String s) {
		if (s == null) return null;
		StringBuilder sb = new StringBuilder();

		boolean inside = false;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (inside) {
				// inside html tag
				if (c == '>') inside = false;
				continue;
			} else {
				// outside html tag
				if (c == '<') {
					inside = true;
					continue;
				}
				sb.append(c);
			}
		}

		s = sb.toString();
		s = s.replace("&nbsp;", " ");
		s = s.replace("&auml;", String.valueOf(ae));
		s = s.replace("&uuml;", String.valueOf(ue));
		s = s.replace("&ouml;", String.valueOf(oe));
		s = s.replace("&Auml;", String.valueOf(AE));
		s = s.replace("&Uuml;", String.valueOf(UE));
		s = s.replace("&Ouml;", String.valueOf(OE));
		s = s.replace("&szlig;", String.valueOf(sz));
		s = s.replace("&euro;", String.valueOf(EUR));
		s = s.replace("&amp;", "&");
		s = s.replace("&quot;", "\"");
		s = s.replace("&lt;", "<");
		s = s.replace("&gt;", ">");
		s = s.replace("<br>", "\n");
		return s;
	}

	public static String[] remove(int index, String[] elements) {
		String[] sa = new String[elements.length - 1];
		System.arraycopy(elements, 0, sa, 0, index);
		System.arraycopy(elements, index + 1, sa, index, sa.length - index);
		return sa;
	}

	public static String[] remove(String elementToRemove, String[] elements) {
		for (int i = 0; i < elements.length; i++) {
			if (elements[i].equals(elementToRemove)) { return remove(i, elements); }
		}
		return elements;
	}

	public static String appendIfMarkerNotExists(String s, String marker, String textToAppend) {
		if (s == null) return marker + textToAppend;
		if (s.indexOf(marker) >= 0) return s;
		return s + marker + textToAppend;
	}

	public static String[] append(String newElement, String[] elements) {
		String[] sa = new String[elements.length + 1];
		System.arraycopy(elements, 0, sa, 0, elements.length);
		sa[elements.length] = newElement;
		return sa;
	}

	public static boolean contains(String textToLookFor, String[] textsToLookIn) {
		for (int i = 0; i < textsToLookIn.length; i++) {
			if (textsToLookIn[i].equals(textToLookFor)) return true;
		}
		return false;
	}

	public static String toHtmlColor(Color c) {
		StringBuilder sb = new StringBuilder("#");
		String s;

		s = Integer.toHexString(c.getRed());
		if (s.length() == 1) sb.append('0');
		sb.append(s);

		s = Integer.toHexString(c.getGreen());
		if (s.length() == 1) sb.append('0');
		sb.append(s);

		s = Integer.toHexString(c.getBlue());
		if (s.length() == 1) sb.append('0');
		sb.append(s);

		return sb.toString();
	}

	public static String fillUpLeft(String s, String filler, int minLength) {
		// TODO: optimize algorithm
		while (s.length() < minLength) {
			s = filler + s;
		}
		return s;
	}

	public static String cutLeft(String s, int maxlength, String fillerOnCut) {
		if (s == null) return null;
		if (s.length() > maxlength) {
			return fillerOnCut + s.substring(s.length() - maxlength + fillerOnCut.length());
		} else return s;
	}

	public static String cutRight(String s, int maxlength) {
		if (s == null) return null;
		if (s.length() > maxlength) {
			return s.substring(0, maxlength);
		} else return s;
	}

	public static String cutRight(String s, int maxlength, String fillerOnCut) {
		if (s == null) return null;
		if (s.length() > maxlength) {
			return s.substring(0, maxlength - fillerOnCut.length()) + fillerOnCut;
		} else return s;
	}

	public static String cutHtmlAndHeaderAndBody(String s) {
		if (s == null) return null;
		if (s.startsWith("<html")) {
			int idx = s.indexOf('>');
			s = s.substring(idx + 1).trim();
		}
		if (s.endsWith("</html>")) {
			s = s.substring(0, s.length() - 7).trim();
		}
		if (s.startsWith("<head>")) {
			int endIdx = s.indexOf("</head>");
			s = s.substring(endIdx + 7).trim();
		}
		if (s.startsWith("<body")) {
			int from = s.indexOf('>');
			int to = s.indexOf("</body>");
			s = s.substring(from + 1, to).trim();
		}
		return s;
	}

	public static long generateUID(long idTimeSub) {
		synchronized (UIDLOCK) {
			long id = System.currentTimeMillis() - idTimeSub;
			while (id <= lastId)
				id++;
			lastId = id;
			// LOG.fine("UID generated: "+id);
			return id;
		}
	}

	public static long generateUID() {
		return generateUID(0);
	}

	public static boolean isVersion1LowerThenVersion2(String version1, String version2) {
		if (version1 == null) return true;
		if (version2 == null) return false;
		return parseVersion(version1) < parseVersion(version2);
	}

	public static long parseVersion(String s) {
		long v = 0;
		int factor = 100 * 100 * 100 * 100;
		StringTokenizer tokenizer = new StringTokenizer(s, ".");
		while (tokenizer.hasMoreTokens()) {
			int i = Integer.parseInt(tokenizer.nextToken());
			v += i * factor;
			factor = factor / 100;
		}
		return v;
	}

	public static String[] toLowerCase(String[] value) {
		String[] ret = new String[value.length];
		for (int i = 0; i < value.length; i++) {
			ret[i] = value[i].toLowerCase();
		}
		return ret;
	}

	public static String toString(String message, String key, Object value) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		return toString(message, map);
	}

	public static String toString(String message, String key1, Object value1, String key2, Object value2) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(key1, value1);
		map.put(key2, value2);
		return toString(message, map);
	}

	public static String toString(String message, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		HashMap<String, Object> map = new HashMap<String, Object>(); // TODO orderedMap
		map.put(key1, value1);
		map.put(key2, value2);
		map.put(key3, value3);
		return toString(message, map);
	}

	public static String toString(String message, Map<String, Object> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(message);
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			sb.append("\n  ").append(entry.getKey()).append(" = ").append(format(entry.getValue()));
		}
		return sb.toString();
	}

	public static String getRootCauseMessage(Throwable t) {
		Throwable cause = t.getCause();
		while (cause != null) {
			t = cause;
			cause = t.getCause();
		}
		String message = t.getMessage();
		if (message == null) message = t.getClass().getName();
		return message;
	}

	public static String getStackTrace() {
		return getStackTrace(new Exception());
	}

	public static Collection<Object[]> toCollection(Object[] oa) {
		ArrayList<Object[]> al = new ArrayList<Object[]>(oa.length);
		for (int i = 0; i < oa.length; i++) {
			al.add(oa);
		}
		return al;
	}

	public static String[] merge(String[][] saa) {
		LinkedList<String> ll = new LinkedList<String>();
		for (int x = 0; x < saa.length; x++) {
			String[] sa = saa[x];
			for (int y = 0; y <= sa.length; y++) {
				ll.add(sa[y]);
			}
		}
		return toStringArray(ll);
	}

	// public static String[] subarray(String[] sa, int length) {
	// StringList sl = new StringList(sa);
	// while (sl.size() > length)
	// sl.remove(sl.size() - 1);
	// return sl.toStringArray();
	// }

	public static String[] subarray(String[] sa, int beginIndex, int length) {
		String[] result = new String[length];
		System.arraycopy(sa, beginIndex, result, 0, length);
		return result;
	}

	public static String[] subarray(String[] sa, int beginIndex) {
		return subarray(sa, beginIndex, sa.length - beginIndex);
	}

	public static String getFirstLineFromHtml(String text, int cutAfterLength, String appendAfterCut) {
		if (text == null) return "<empty>";
		if (text.startsWith("<html")) {
			text = cutHtmlAndHeaderAndBody(text);
			text = removeHtmlTags(text).trim();
		} else {
			text = Str.getFirstLine(text);
		}
		text = getFirstLine(text, cutAfterLength, appendAfterCut);
		return text;
	}

	public static String getLineFromHtml(String text, int line, int cutAfterLength, String appendAfterCut) {
		if (text == null) return "<empty>";
		if (text.startsWith("<html")) {
			text = cutHtmlAndHeaderAndBody(text);
			text = removeHtmlTags(text).trim();
		}
		text = getLine(text, line, cutAfterLength, appendAfterCut);
		return text;
	}

	public static String getFirstLine(String s) {
		return getFirstLine(s, Integer.MAX_VALUE, null);
	}

	public static String getFirstLine(String s, int cutAfterLength, String appendAfterCut) {
		return getLine(s, 0, cutAfterLength, appendAfterCut);
	}

	public static String getLine(String s, int index, int cutAfterLength, String appendAfterCut) {
		if (s == null) return null;
		BufferedReader in = new BufferedReader(new StringReader(s));
		String ret = null;
		try {
			for (int i = 0; i <= index; i++) {
				ret = in.readLine();
				if (ret == null) return "";
			}
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (ret == null) return "";
		if (ret.length() > cutAfterLength) {
			ret = ret.substring(0, cutAfterLength);
			if (appendAfterCut != null) ret += appendAfterCut;
		}
		return ret;
	}

	public static String[] toStringArray(Collection<String> c) {
		return toStringArray(c.toArray());
	}

	public static String[] toStringArray(Object[] oa) {
		String[] sa = new String[oa.length];
		for (int i = 0; i < oa.length; i++) {
			sa[i] = oa[i] == null ? null : oa[i].toString();
		}
		return sa;
	}

	public static boolean equals(String[] sa1, String[] sa2) {
		if (sa1.length != sa2.length) return false;
		for (int i = 0; i < sa1.length; i++) {
			if (!sa1[i].equals(sa2[i])) return false;
		}
		return true;
	}

	public static String[] tokenize(String s, String delimiter) {
		StringTokenizer tok = new StringTokenizer(s, delimiter);
		LinkedList<String> ll = new LinkedList<String>();
		while (tok.hasMoreTokens()) {
			ll.add(tok.nextToken());
		}
		return toStringArray(ll);
	}

	public static List<String> tokenizeString(String s) {
		if (s == null) return Collections.emptyList();
		StringTokenizer tok = new StringTokenizer(s);
		LinkedList<String> ll = new LinkedList<String>();
		while (tok.hasMoreTokens()) {
			ll.add(tok.nextToken());
		}
		return ll;
	}

	public static String[] tokenize(String s) {
		return toStringArray(tokenizeString(s));
	}

	public static String getLastToken(String s, String delimiter) {
		String result = null;
		StringTokenizer tokenizer = new StringTokenizer(s, delimiter);
		while (tokenizer.hasMoreTokens())
			result = tokenizer.nextToken();
		return result;
	}

	public static String concat(Object[] sa, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sa.length; i++) {
			sb.append(sa[i]);
			if (i < sa.length - 1) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	public static String concat(Object[] sa) {
		return concat(sa, " ");
	}

	public static String replaceForHtml(String s) {
		return toHtml(s);
	}

	public static String convertLinksToHtml(String text, String target) {
		StringBuilder sb = new StringBuilder();
		int startIdx = 0;
		int idx;
		while ((idx = indexOf(text, new String[] { "http://", "www.", "ftp://" }, startIdx)) >= 0) {
			int end = indexOf(text, new String[] { " ", "\n", ",", ";" }, idx);
			if (end < 0) end = text.length();
			String link = text.substring(idx, end);
			sb.append(text.substring(startIdx, idx));
			sb.append("<A href=\"").append(link).append("\"");
			if (target != null) {
				sb.append(" target=\"").append(target).append("\"");
			}
			sb.append(">").append(link).append("</A>");
			startIdx = end;
		}
		sb.append(text.substring(startIdx));
		return sb.toString();
	}

	public static String insertIfNotExisting(String into, int index, String insert) {
		StringBuilder sb = new StringBuilder();

		while (into.length() > index) {
			String sub = into.substring(0, index);
			sb.append(sub);
			if (sub.indexOf(insert) < 0) sb.append(insert);
			into = into.substring(index);
		}
		sb.append(into);

		return sb.toString();
	}

	public static ArrayList<String> splitWordLineToList(String line, int maxlen) {
		ArrayList<String> al = new ArrayList<String>();

		while (line.length() > maxlen) {
			int idx = line.substring(0, maxlen).lastIndexOf(" ");
			if (idx <= 0) idx = maxlen;
			al.add(line.substring(0, idx));
			line = line.substring(idx + 1);
		}
		al.add(line);

		return al;
	}

	public static String trimRight(String text) {
		while (text.endsWith(" ")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	public static String getPrefix(String text, char[] prefixChars) {
		int len = text.length();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			if (contains(prefixChars, c)) {
				sb.append(c);
			} else {
				break;
			}
		}
		return sb.toString();
	}

	public static boolean contains(char[] list, char element) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == element) return true;
		}
		return false;
	}

	public static String parseMailQuotationPrefix(String line) {
		StringBuilder sb = new StringBuilder();
		line = line.trim();
		while (line.startsWith(">") || line.startsWith(" ")) {
			sb.append(line.charAt(0));
			line = line.substring(1);
		}
		return sb.toString();
	}

	public static String quote(String text, String prefix) {
		List<String> lines = toStringList(text);
		StringBuilder sb = new StringBuilder();
		int size = lines.size();
		for (int i = 0; i < size; i++) {
			sb.append(prefix).append(lines.get(i)).append("\n");
		}
		return sb.toString();
	}

	public static String quoteMail(String text, String prefix, int maxlen) {
		if (text == null) return null;
		List<String> lines = toStringList(text);
		StringBuilder sb = new StringBuilder();
		int size = lines.size();
		for (int i = 0; i < size; i++) {
			String line = lines.get(i);
			int len = line.length();
			if (len > maxlen - 1) {
				String lineprefix = parseMailQuotationPrefix(line);
				int lplen = lineprefix.length();
				ArrayList<String> al = splitWordLineToList(line.substring(lplen), maxlen - lplen);
				for (int j = 0; j < al.size(); j++) {
					String l = al.get(j);
					l = trimRight(l);
					sb.append(prefix).append(lineprefix).append(l).append("\n");
				}
			} else {
				sb.append(prefix).append(line).append("\n");
			}
		}
		return sb.toString();
	}

	public static List<String> toStringList(String text) {
		BufferedReader in = new BufferedReader(new StringReader(text));
		List<String> lines = new ArrayList<String>();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static String escapeEscapeSequences(String text) {
		for (String[] esc : ESCAPE_SEQUENCES) {
			text = text.replace(esc[0], esc[1]);
		}
		return text;
	}

	public static class Char {

		public static final char BACKSPACE = 8;

		public static final char CR = 13;

		public static final char LF = 10;
	}

	// --- dependencies ---

}
