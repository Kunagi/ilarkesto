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

public class LineTokenizer implements DiffTokenizer {

	@Override
	public List<String> tokenize(String s) {
		List<String> ret = new LinkedList<String>();
		if (s == null) return ret;
		int len = s.length();
		int from = 0;
		int to = s.indexOf('\n');
		while (to >= 0) {
			ret.add(s.substring(from, to));
			ret.add("\n");
			from = to + 1;
			if (from >= len) return ret;
			to = s.indexOf('\n', from);
		}
		ret.add(s.substring(from));
		return ret;
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
