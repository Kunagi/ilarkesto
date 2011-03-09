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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class TooltipPopup extends PopupPanel {

	/**
	 * The delay, in milliseconds, to display the tooltip
	 */
	private int showDelay = 500;

	/**
	 * The delay, in milliseconds, to hide the tooltip, after it is displayed
	 */
	private int hideDelay = -1;

	/**
	 * The timer to show the tool tip
	 */
	private Timer showTimer;

	/**
	 * The timer to hide the tool tip
	 */
	private Timer hideTimer;

	private boolean active;

	/**
	 * Creates a new Tool Tip with the default show delay and no auto hiding
	 * 
	 * @param sender The widget to create the tool tip for
	 * @param relLeft The left offset from the &lt;code&gt;sender&lt;/code&gt;
	 * @param relTop The top offset from the &lt;code&gt;sender&lt;/code&gt;
	 * @param text The tool tip text to display
	 * @param useRelTop If true, then use the relative top offset. If not, then just use the sender's offset
	 *            height.
	 */
	public TooltipPopup(Widget sender, int relLeft, int relTop, final HTML contents, boolean useRelTop) {
		super(true);

		this.showTimer = null;
		this.hideTimer = null;

		add(contents);

		int left = getPageScrollLeft() + sender.getAbsoluteLeft() + relLeft;
		int top = getPageScrollTop() + sender.getAbsoluteTop();

		if (useRelTop) {
			top += relTop;
		} else {
			top += sender.getOffsetHeight() + 1;
		}

		setAutoHideEnabled(true);
		setPopupPosition(left, top);
		addStyleName("Tooltip");
	}

	@Override
	public void show() {

		// Set delay to show if specified
		if (this.showDelay > 0) {
			this.showTimer = new Timer() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.google.gwt.user.client.Timer#run()
				 */
				@Override
				public void run() {
					showTooltip();
				}
			};
			this.showTimer.schedule(this.showDelay);
		}
		// Otherwise, show the dialog now
		else {
			showTooltip();
		}

		// Set delay to hide if specified
		if (this.hideDelay > 0) {
			this.hideTimer = new Timer() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.google.gwt.user.client.Timer#run()
				 */
				@Override
				public void run() {

					TooltipPopup.this.hide();
				}
			};
			this.hideTimer.schedule(this.showDelay + this.hideDelay);
		}
	}

	@Override
	public void hide() {
		super.hide();
		active = false;

		// Cancel the show timer if necessary
		if (this.showTimer != null) {
			this.showTimer.cancel();
		}

		// Cancel the hide timer if necessary
		if (this.hideTimer != null) {
			this.hideTimer.cancel();
		}
	}

	/**
	 * Show the tool tip now
	 */
	private void showTooltip() {
		super.show();
		active = true;
	}

	public boolean isActive() {
		return active;
	}

	/**
	 * Get the offset for the horizontal scroll
	 * 
	 * @return The offset
	 */
	private int getPageScrollLeft() {
		return DOM.getAbsoluteLeft(DOM.getParent(RootPanel.getBodyElement()));
	}

	/**
	 * Get the offset for the vertical scroll
	 * 
	 * @return The offset
	 */
	private int getPageScrollTop() {
		return DOM.getAbsoluteTop(DOM.getParent(RootPanel.getBodyElement()));
	}
}

