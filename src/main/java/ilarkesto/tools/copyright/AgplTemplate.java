package ilarkesto.tools.copyright;

import ilarkesto.core.base.Str;
import ilarkesto.core.base.Utl;

public class AgplTemplate implements CopyrightTemplate {

	@Override
	public String getText(String years, String... owners) {
		StringBuilder sb = new StringBuilder();
		sb.append("/*\n");
		sb.append(" * Copyright ").append(years).append(" ");
		sb.append(Str.concat(Utl.toList(owners), ", "));
		sb.append("\n");
		sb.append(" * \n");
		sb.append(" * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public\n");
		sb.append(" * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)\n");
		sb.append(" * any later version.\n");
		sb.append(" * \n");
		sb.append(" * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the\n");
		sb.append(" * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License\n");
		sb.append(" * for more details.\n");
		sb.append(" * \n");
		sb.append(" * You should have received a copy of the GNU General Public License along with this program. If not, see\n");
		sb.append(" * <http://www.gnu.org/licenses/>.\n");
		sb.append(" */\n");
		return sb.toString();
	}

	@Override
	public boolean containsText(String content) {
		return content.contains("* Copyright ");
	}
}
