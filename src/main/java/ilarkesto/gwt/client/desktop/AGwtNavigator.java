/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.gwt.client.desktop;

import ilarkesto.core.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.gwt.client.Gwt;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public abstract class AGwtNavigator implements ValueChangeHandler<String> {

	protected final Log log = Log.get(getClass());

	private boolean disabled;

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		if (isDisabled()) return;
		String token = event.getValue();
		log.info("History token changed:", token);
		handleToken(token);
	}

	public void handleToken(String token) {
		if (isDisabled()) return;
		if (Str.isBlank(token)) token = "Home";
		int activityNameEndIdx = token.indexOf(Gwt.HISTORY_TOKEN_SEPARATOR);
		String activityParamsToken = null;
		String activityName;
		if (activityNameEndIdx < 0) {
			activityName = token;
		} else {
			activityName = token.substring(0, activityNameEndIdx);
			activityParamsToken = token.substring(activityNameEndIdx + 1);
		}
		startActivity(activityName, activityParamsToken);
	}

	private void startActivity(String activityName, String activityParamsToken) {
		log.info("startActivity()", activityName, activityParamsToken);
		ActivityParameters parameters = ActivityParameters.parseToken(activityParamsToken);

		AActivity activity = AActivityCatalog.INSTANCE.instantiateActivity(activityName);

		activity.startAsRoot(parameters);
	}

	public void disable() {
		disabled = true;
	}

	public boolean isDisabled() {
		return disabled;
	}
}