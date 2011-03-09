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
package ilarkesto.gwt.client.animation;

import com.google.gwt.user.client.ui.Widget;

public class AppearAnimation extends AObservableAnimation {

	private Widget widget;
	private int height;
	private double delayFactor = 1;

	public AppearAnimation(Integer height, Widget widget, double delayFactor) {
		this.height = height == null ? 20 : height;
		this.widget = widget;
		this.delayFactor = delayFactor;
		widget.getElement().getStyle().setProperty("visible", "false");
		widget.getElement().getStyle().setProperty("height", "0px");
		widget.getElement().getStyle().setProperty("marginTop", "0px");
		widget.getElement().getStyle().setProperty("marginBottom", "0px");
	}

	@Override
	protected void onStart() {
		widget.getElement().getStyle().setProperty("overflow", "hidden");
		widget.getElement().getStyle().setProperty("visible", "true");
	}

	@Override
	protected void onComplete() {
		widget.getElement().getStyle().setProperty("height", "auto");
		widget.getElement().getStyle().setProperty("overflow", "auto");
		fireCompletionEvent();
	}

	@Override
	protected void onUpdate(double progress) {
		progress *= this.delayFactor;
		progress -= (this.delayFactor - 1);
		if (progress <= 0) {
			progress = 0;
		}
		widget.getElement().getStyle().setProperty("height", (int) (progress * this.height) + "px");
	}

	@Override
	public void run(int duration) {
		super.run((int) (duration * this.delayFactor));
	}
}