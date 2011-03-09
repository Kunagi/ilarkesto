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

import ilarkesto.core.base.Str;

public class HtmlDiffMarker implements DiffMarker {

	@Override
	public String same(String s) {
		return toHtml(s);
	}

	@Override
	public String added(String s) {
		return "<span class=\"added\">" + toHtml(s) + "</span>";
	}

	@Override
	public String removed(String s) {
		return "<span class=\"removed\">" + toHtml(s) + "</span>";
	}

	@Override
	public String replaced(String oldS, String newS) {
		return removed(oldS) + added(newS);
	}

	private String toHtml(String s) {
		return Str.toHtml(s);
	}

}
