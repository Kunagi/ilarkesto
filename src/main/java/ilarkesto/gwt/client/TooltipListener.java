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

import ilarkesto.core.base.Str;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.Widget;


public class TooltipListener extends MouseListenerAdapter {

	private TooltipPopup tooltip;
	private HTML text;
	private boolean empty;
	private int offsetX = 10;
	private int offsetY = 35;

	public TooltipListener(HTML text) {
		this.text = text;
		this.empty = text == null || Str.isBlank(text.getHTML());
	}

	@Override
	public void onMouseEnter(Widget sender) {
		if (empty) return;
		if (tooltip != null) tooltip.hide();
		tooltip = new TooltipPopup(sender, offsetX, offsetY, text, false);
		tooltip.show();
	}

	@Override
	public void onMouseLeave(Widget sender) {
		if (tooltip != null) tooltip.hide();
	}

	@Override
	public void onMouseMove(Widget sender, int x, int y) {
		if (tooltip != null && tooltip.isActive()) {
			tooltip.hide();
		}
	}

}

