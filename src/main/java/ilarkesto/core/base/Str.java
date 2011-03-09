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
package ilarkesto.core.base;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

public class Str {

	public static final char ue = '\u00FC';
	public static final char UE = '\u00DC';
	public static final char oe = '\u00F6';
	public static final char OE = '\u00D6';
	public static final char ae = '\u00E4';
	public static final char AE = '\u00C4';
	public static final char sz = '\u00DF';

	public static final char EUR = '\u0080';

	public static String toStringHelper(Object thiz, Object... properties) {
		return toStringHelper(getSimpleName(thiz.getClass()), properties);
	}

	public static String toStringHelper(String name, Object... properties) {
		return concat(name + "(", ")", ", ", properties);
	}

	public static String concat(String prefix, String suffix, String delimiter, Object... objects) {
		StringBuilder sb = new StringBuilder();
		if (prefix != null) sb.append(prefix);
		for (int i = 0; i < objects.length; i++) {
			sb.append(objects[i]);
			if (delimiter != null && i < objects.length - 1) {
				sb.append(delimiter);
			}
		}
		if (suffix != null) sb.append(suffix);
		return sb.toString();
	}

	public static String concat(Collection strings, String delimiter) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object s : strings) {
			if (first) {
				first = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Removes a suffix from a string, if it exists.
	 */
	public static String removeSuffix(String s, String suffixToRemove) {
		if (s == null) return null;
		if (!s.endsWith(suffixToRemove)) return s;
		return s.substring(0, s.length() - suffixToRemove.length());
	}

	public static String removePrefix(String s, String prefixToRemove) {
		if (s == null) return null;
		if (!s.startsWith(prefixToRemove)) return s;
		return s.substring(prefixToRemove.length());
	}

	public static String getFirstParagraph(String s) {
		return getFirstParagraph(s, "\n\n");
	}

	public static String getFirstParagraph(String s, String paragraphEndIndicator) {
		if (s == null) return null;
		int idx = s.indexOf(paragraphEndIndicator);
		if (idx <= 0) return s;
		return s.substring(0, idx);
	}

	public static String appendIfNotBlank(String s, String suffix) {
		if (isBlank(s)) return s;
		return s + suffix;
	}

	public static String toHtml(String s) {
		if (s == null) return null;
		s = s.replace("&", "&amp;");
		s = s.replace(String.valueOf(ae), "&auml;");
		s = s.replace(String.valueOf(ue), "&uuml;");
		s = s.replace(String.valueOf(oe), "&ouml;");
		s = s.replace(String.valueOf(AE), "&Auml;");
		s = s.replace(String.valueOf(UE), "&Uuml;");
		s = s.replace(String.valueOf(OE), "&Ouml;");
		s = s.replace(String.valueOf(sz), "&szlig;");
		s = s.replace(String.valueOf(EUR), "&euro;");
		s = s.replace("<", "&lt;");
		s = s.replace(">", "&gt;");
		s = s.replace("\"", "&quot;");
		s = s.replace("\n", "<br>");

		return s;
	}

	public static String getLeadingSpaces(String s) {
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			if (s.charAt(i) != ' ') break;
			sb.append(' ');
		}
		return sb.toString();
	}

	public static String cutFromTo(String s, String from, String to) {
		if (s == null) return null;
		s = cutFrom(s, from);
		s = cutTo(s, to);
		return s;
	}

	public static String cutFrom(String s, String from) {
		if (s == null) return null;
		int fromIdx = s.indexOf(from);
		if (fromIdx < 0) return null;
		fromIdx += from.length();
		return s.substring(fromIdx);
	}

	public static String cutTo(String s, String to) {
		if (s == null) return null;
		int toIdx = s.indexOf(to);
		if (toIdx < 0) return null;
		return s.substring(0, toIdx);
	}

	public static String toHtmlId(Object... objects) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Object object : objects) {
			if (first) {
				first = false;
			} else {
				sb.append("_");
			}
			if (object == null) {
				sb.append("null");
				continue;
			}
			sb.append(toHtmlId(object.toString()));
		}
		return sb.toString();
	}

	public static String toHtmlId(String s) {
		int len = s.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			if (Character.isLetter(ch) || (Character.isDigit(ch) && i > 0)) {
				sb.append(ch);
				continue;
			}
			sb.append('_');
		}
		return sb.toString();
	}

	// TODO rename
	public static String cutLeft(String s, int maxlength) {
		if (s.length() > maxlength) {
			return s.substring(s.length() - maxlength);
		} else return s;
	}

	// TODO rename
	public static String fillUpRight(String s, String filler, int minLength) {
		StringBuilder sb = new StringBuilder(s);
		while (sb.length() < minLength) {
			sb.append(filler);
		}
		return sb.toString();
	}

	public static boolean isEmail(String s) {
		if (isBlank(s)) return false;
		boolean at = false;
		boolean dot = false;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c == '@') {
				if (at) return false;
				at = true;
				continue;
			}
			if (c == '.') {
				dot = true;
				continue;
			}
			if (Character.isLetterOrDigit(c) || c == '-' || c == '_') continue;
			return false;
		}
		if (!dot || !at) return false;
		return true;
	}

	public static boolean isTrue(String s) {
		if (s == null) return false;
		s = s.toLowerCase();
		if (s.equals("true")) return true;
		if (s.equals("yes")) return true;
		if (s.equals("y")) return true;
		if (s.equals("1")) return true;
		if (s.equals("ja")) return true;
		if (s.equals("j")) return true;
		return false;
	}

	public static int indexOf(String text, String[] toFind, int startIdx) {
		int firstIdx = -1;
		for (int i = 0; i < toFind.length; i++) {
			int idx = text.indexOf(toFind[i], startIdx);
			if (firstIdx < 0 || (idx >= 0 && idx < firstIdx)) {
				firstIdx = idx;
			}
		}
		return firstIdx;
	}

	public static String format(Object o) {
		if (o == null) return null;
		if (o instanceof Object[]) return formatObjectArray((Object[]) o);
		if (o instanceof Map) return formatMap((Map) o);
		if (o instanceof Collection) formatCollection((Collection) o);
		if (o instanceof Enumeration) return formatEnumeration((Enumeration) o);
		if (o instanceof Throwable) return formatException((Throwable) o);
		return o.toString();
	}

	private static boolean isWrapperException(Throwable ex) {
		if (ex.getClass().getName().equals(RuntimeException.class.getName())) return true;
		if (ex.getClass().getName().equals("java.util.concurrent.ExecutionException")) return true;
		return false;
	}

	public static String formatEnumeration(Enumeration e) {
		return formatCollection(Utl.toList(e));
	}

	public static String formatCollection(Collection c) {
		return formatObjectArray(c.toArray());
	}

	public static String formatMap(Map map) {
		StringBuilder sb = new StringBuilder();
		sb.append("map[");
		sb.append(map.size());
		sb.append("]={");
		boolean following = false;
		Set<Map.Entry> entries = map.entrySet();
		for (Map.Entry entry : entries) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (following) {
				sb.append(',');
			}
			following = true;
			sb.append('"');
			sb.append(format(key));
			sb.append("\"=\"");
			sb.append(format(value));
			sb.append('"');
		}
		sb.append('}');
		return sb.toString();
	}

	public static String formatException(Throwable ex) {
		StringBuilder sb = null;
		while (ex != null) {
			Throwable cause = ex.getCause();
			String message = ex.getMessage();
			if (cause != null && message != null && message.startsWith(cause.getClass().getName())) message = null;
			while (isWrapperException(ex) && isBlank(message) && cause != null) {
				ex = cause;
				cause = ex.getCause();
				message = ex.getMessage();
				if (cause != null && message != null && message.startsWith(cause.getClass().getName())) message = null;
			}
			if (sb == null) {
				sb = new StringBuilder();
			} else {
				sb.append("\nCaused by ");
			}
			sb.append(getSimpleName(ex.getClass()));
			sb.append(": ");
			sb.append(message);
			ex = cause;
		}
		return sb.toString();
	}

	public static String formatStackTrace(StackTraceElement[] trace) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : trace)
			sb.append("    at ").append(element).append("\n");
		return sb.toString();
	}

	public static String getStackTrace(Throwable t) {
		StringBuilder sb = new StringBuilder();
		sb.append(t.toString()).append("\n");
		sb.append(formatStackTrace(t.getStackTrace()));

		Throwable cause = t.getCause();
		if (cause == null) return sb.toString();
		sb.append("Caused by: ").append(getStackTrace(cause));

		return sb.toString();
	}

	public static String formatObjectArray(Object[] oa) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < oa.length; i++) {
			if (oa[i] != null) {
				sb.append('<');
				sb.append(format(oa[i]));
				sb.append('>');
			}
			if (i != oa.length - 1) {
				sb.append(',');
			}
		}
		sb.append('}');
		return sb.toString();
	}

	public static String getSimpleName(Class type) {
		String name = type.getName();
		int idx = name.lastIndexOf('.');
		if (idx > 0) {
			name = name.substring(idx + 1);
		}
		return name;
	}

	public static boolean isBlank(String s) {
		return s == null || s.length() == 0 || s.trim().length() == 0;
	}
}
