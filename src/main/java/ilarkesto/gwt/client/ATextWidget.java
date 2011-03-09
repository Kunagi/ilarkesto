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
package ilarkesto.gwt.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public abstract class ATextWidget extends AWidget {

	private HTML viewer;

	@Override
	protected final Widget onInitialization() {
		viewer = new HTML();
		return viewer;
	}

	public final void setText(Object text) {
		String s = text == null ? null : text.toString();
		viewer.setText(s);
	}

	public final void setHtml(String html) {
		viewer.setHTML(html);
	}

	public void setHours(int hours) {
		setText(Gwt.formatHours(hours));
	}

}
