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
package ilarkesto.core.diff;

import java.util.LinkedList;
import java.util.List;

public class WordTokenizer implements DiffTokenizer {

	@Override
	public List<String> tokenize(String s) {
		List<String> ret = new LinkedList<String>();
		if (s == null) return ret;
		boolean word = false;
		StringBuilder token = null;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			if (isWordChar(ch)) {
				if (token == null) {
					token = new StringBuilder();
					token.append(ch);
				} else {
					if (!word) {
						ret.add(token.toString());
						token = new StringBuilder();
					}
					token.append(ch);
				}
				word = true;
			} else {
				if (token == null) {
					token = new StringBuilder();
					token.append(ch);
				} else {
					if (word) {
						ret.add(token.toString());
						token = new StringBuilder();
					}
					token.append(ch);
				}
				word = false;
			}
		}
		if (token != null) ret.add(token.toString());
		return ret;
	}

	static boolean isWordChar(char ch) {
		return Character.isLetterOrDigit(ch);
	}

	@Override
	public String concat(List<String> tokens) {
		StringBuilder sb = new StringBuilder();
		for (String token : tokens) {
			sb.append(token);
		}
		return sb.toString();
	}

}
